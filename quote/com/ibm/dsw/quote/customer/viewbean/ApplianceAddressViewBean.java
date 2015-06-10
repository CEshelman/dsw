package com.ibm.dsw.quote.customer.viewbean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.ApplianceAddress;
import com.ibm.dsw.quote.common.domain.ApplianceLineItem;
import com.ibm.dsw.quote.common.domain.ComparatorApplianceAddressOrItem;
import com.ibm.dsw.quote.common.domain.ComparatorApplianceLineItem;
import com.ibm.dsw.quote.customer.config.CustomerActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class ApplianceAddressViewBean extends BaseViewBean {

	private transient List<ApplianceAddress> applianceAddressList;
	private transient List<ApplianceAddress> originApplianceAddressList;
	private transient Map originApplianceLineItemMap;
	private transient ApplianceAddress selectedCust;
	private transient ApplianceAddress soldToAddr;
	
	private String webQuoteNum;
	private String addressType;
	private String country;
	private String lob;
	private Integer currMaxSecId;
	private String agreementNumber;
	private String isSubmittedQuote;
	
	public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        
        Map jdbcResultList  = (Map) params.getParameter(ParamKeys.PARAM_SIMPLE_OBJECT);
        // poOption  -- for ship to: 0(sold to for all addresses), 1(many addresses); install at: 0,1,2
        int poOption = (Integer)params.getParameter(DraftQuoteConstants.APPLIANCE_ADDRESS_OPTION);
        this.webQuoteNum = (String)params.getParameter(ParamKeys.PARAM_QUOTE_NUM);
        this.country = (String)params.getParameter(ParamKeys.PARAM_COUNTRY);
        this.lob = (String)params.getParameter(ParamKeys.PARAM_LINE_OF_BUSINESS);
        this.addressType = (String)params.getParameter(ParamKeys.PARAM_ADDRESS_TYPE);
        this.agreementNumber = (String)params.getParameter(ParamKeys.PARAM_AGREEMENT_NUM);
        this.applianceAddressList = assemblyList( jdbcResultList,poOption);
        this.isSubmittedQuote = (String)params.getParameter(ParamKeys.PARAM_IS_SBMT_QT);
        logContext.debug(this, "this.applianceAddressList:"+ this.applianceAddressList.size());
        boolean isReadOnly = StringUtils.equals((String)params.getParameter(ParamKeys.PARAM_IS_READ_ONLY),"true")?true:false;
        //default sold to addr
        if(!isReadOnly){
        	initSoldToAddr();
        }
		
		if(this.applianceAddressList != null){
			for(ApplianceAddress addr : this.applianceAddressList){
				List<ApplianceLineItem> lineItemList = addr.getLineItemList();
				if(lineItemList!=null){
					for(ApplianceLineItem item : lineItemList){
						if(item !=null ){
							if(item.getConfigrtnId()==null || (item.getConfigrtnId()!=null &&
									(PartPriceConstants.APPLNC_NOT_ON_QUOTE.equals(item.getConfigrtnId().trim()) 
											|| PartPriceConstants.APPLNC_NOT_ASSOCIATED.equals(item.getConfigrtnId().trim()) )
							)){
								item.setConfigrtnId("");
							}
						}
					}
				}
				// whether do or not display  customer infor and company address in PGS quote
				if(!addr.isShowPGSInfor()){
					addr.setAddressIntrnl("");
					addr.setCustAddress("");
				}
			}
		}
		
        if(!isReadOnly && jdbcResultList.containsKey(DraftQuoteConstants.SELECTED_CUSTOMER)){
			ApplianceAddress customer = (ApplianceAddress) jdbcResultList.get(DraftQuoteConstants.SELECTED_CUSTOMER);
			if(customer != null && !isExistCust(customer)){
				customer.setSecId(this.applianceAddressList.get(this.applianceAddressList.size()-1).getSecId());
				customer.setSecId(this.currMaxSecId+1);
				customer.setQuoteLineItemSeqNum(Integer.MAX_VALUE);
				this.currMaxSecId += 1;
				this.selectedCust = customer;
				if(!customer.isShowPGSInfor()){
					customer.setCntFirstName("");
					customer.setCntLastName("");
					customer.setAddressIntrnl("");
					customer.setCustAddress("");
					customer.setSapIntlPhoneNumFull("");
				}
				Country cntryObj  = this.getCntryObject(customer.getCntryCode());
				customer.setCntryCode(cntryObj.getDesc());
				this.applianceAddressList.add(customer);
				logContext.debug(this, "this.applianceAddressList:"+ this.applianceAddressList.size());
				logContext.debug(this, "customer:"+ customer.getCntFirstName());
			}
		}
    }
	
	
	private List<ApplianceAddress> assemblyList(Map queryList,int poOption){
		if(queryList != null && !queryList.isEmpty()){
			List<ApplianceAddress> addressList = (List<ApplianceAddress>) queryList.get(DraftQuoteConstants.APPLIANCE_ADDRESS_LIST);
			this.originApplianceAddressList = addressList;
			Map originItemMap = (Map) queryList.get(DraftQuoteConstants.LINE_ITEM_MAP);
			
			this.originApplianceLineItemMap = originItemMap;
			
			Map itemMap = ApplianceLineItem.assemblyLineItemMap(originItemMap);
			
			this.soldToAddr = (ApplianceAddress)queryList.get(DraftQuoteConstants.SOLD_TO_ADDRESS);
			
			List<ApplianceAddress> returnList = new ArrayList<ApplianceAddress>();
			
			Set<String> lineItemSet = new HashSet<String>();
			
			if( addressList.size()==1
					&& addressList.get(0).getQuoteLineItemSeqNum() == -2 ){
				ApplianceAddress newAdd = addressList.get(0);
				List<ApplianceLineItem> lineItemList = new ArrayList<ApplianceLineItem>();
				  for (Object value : itemMap.values()) {
					  lineItemList.add((ApplianceLineItem)value);
				  }
				  Country cntryObj  = this.getCntryObject(newAdd.getCntryCode());
				  newAdd.setCntryCode(cntryObj.getDesc());
				  newAdd.setSapRegionCode(cntryObj.getStateDescription(StringUtils.trimToEmpty(newAdd.getSapRegionCode())));
				  Collections.sort(lineItemList, new ComparatorApplianceLineItem());
				  newAdd.setLineItemList(lineItemList);
				  newAdd.setHeadLevelAddr(true);
				returnList.add(newAdd);	
				this.currMaxSecId = 1;
			}else{
				ComparatorApplianceAddressOrItem comparator = null;
				if(addressList!=null && !addressList.isEmpty()){
					comparator=new ComparatorApplianceAddressOrItem();
					Collections.sort(addressList, comparator);
				}
				
				Set<String> custNumSet = new HashSet<String>();
				Set<Integer> secIdSet = new HashSet<Integer>();
				Set<Integer> LineItemSeqNumSet = new HashSet<Integer>();
				for(int i =0; i<addressList.size();i++){
					ApplianceAddress temp = addressList.get(i);
					if(LineItemSeqNumSet.contains(temp.getQuoteLineItemSeqNum())){
						continue;
					}else{
						LineItemSeqNumSet.add(temp.getQuoteLineItemSeqNum());
					}
					if(custNumSet.contains(temp.getCustNum())){
						ApplianceAddress existAddress = getExistAddress(returnList,temp.getCustNum());
						if(temp.getQuoteLineItemSeqNum() != DraftQuoteConstants.DEFAULT_APPLIANCE_ADDRESS){
							List<ApplianceLineItem> existItemList = existAddress.getLineItemList();
							ApplianceLineItem item = (ApplianceLineItem) itemMap.get(String.valueOf(temp.getQuoteLineItemSeqNum()));
							if(item!=null){
								existItemList.add(item);
								lineItemSet.add(String.valueOf(item.getQuoteLineItemSeqNum()));
							}
						}else{
							existAddress.setQuoteLineItemSeqNum(DraftQuoteConstants.DEFAULT_APPLIANCE_ADDRESS);
							existAddress.setHeadLevelAddr(true);
						}
					}else{
						custNumSet.add(temp.getCustNum());
						secIdSet.add(temp.getSecId());
						Country cntryObj  = this.getCntryObject(temp.getCntryCode());
						temp.setCntryCode(cntryObj.getDesc());
						temp.setSapRegionCode(cntryObj.getStateDescription(StringUtils.trimToEmpty(temp.getSapRegionCode())));
						List<ApplianceLineItem> newItemList = new ArrayList<ApplianceLineItem>();
						if(temp.getQuoteLineItemSeqNum() != DraftQuoteConstants.DEFAULT_APPLIANCE_ADDRESS){
							ApplianceLineItem item = (ApplianceLineItem) itemMap.get(String.valueOf(temp.getQuoteLineItemSeqNum()));
							if(item!=null){
								newItemList.add(item);
								lineItemSet.add(String.valueOf(item.getQuoteLineItemSeqNum()));
							}
						}else{
							temp.setQuoteLineItemSeqNum(DraftQuoteConstants.DEFAULT_APPLIANCE_ADDRESS);
							temp.setHeadLevelAddr(true);
						}
						temp.setLineItemList(newItemList);
						returnList.add(temp);
					}
				}
				this.currMaxSecId=0;
				if(secIdSet.size()>0){
					int[] secIdArray = new int[secIdSet.size()];
					Iterator<Integer> it = secIdSet.iterator();
					int i = 0;
					while(it.hasNext()){
						secIdArray[i] = it.next();
						i++;
					}
					Arrays.sort(secIdArray);
					this.currMaxSecId = (Integer) secIdArray[secIdArray.length-1];
				}
				
				// line item sort
				for(int x=0;x<returnList.size();x++){
					ApplianceAddress atemp = returnList.get(x);
					if(atemp.getQuoteLineItemSeqNum() == DraftQuoteConstants.DEFAULT_APPLIANCE_ADDRESS){
						Set<String> itemMapKeySet = itemMap.keySet();
						if(lineItemSet.size() < itemMapKeySet.size()){  // add new appliance line item to head level address after saving address
							itemMapKeySet.removeAll(lineItemSet);
							for(Object key:itemMapKeySet){
								if(StringUtils.isNotBlank((String)key)){
									atemp.getLineItemList().add((ApplianceLineItem)itemMap.get((String)key));
								}
							}
						}
						
					}
					if(atemp.getLineItemList() != null){
						Collections.sort(atemp.getLineItemList(), new ComparatorApplianceLineItem());
					}
				}
				
			}
			return returnList;
		}else{
			return new ArrayList<ApplianceAddress>();
		}
	}
	
	private void initSoldToAddr() {
		boolean flag = false;
		if(applianceAddressList!=null && !applianceAddressList.isEmpty())
		{
			for(int i =0; i<applianceAddressList.size();i++)
			{
				ApplianceAddress addr = applianceAddressList.get(i);
				if( ( !StringUtils.isBlank(this.soldToAddr.getCustNum()) && StringUtils.equals(addr.getCustNum(), this.soldToAddr.getCustNum()))
						|| ( StringUtils.isBlank(this.soldToAddr.getCustNum()) && StringUtils.isBlank(addr.getCustNum()) && addr.getWebCustId()==this.soldToAddr.getWebCustId() )
						){
					flag = true;
				}
			}
		}
		if(!flag){
			List<ApplianceAddress> returnList = new ArrayList<ApplianceAddress>();
			if(soldToAddr==null) soldToAddr = new ApplianceAddress();
			soldToAddr.setSecId(-1);
			soldToAddr.setQuoteLineItemSeqNum(Integer.MAX_VALUE);
			if(!soldToAddr.isShowPGSInfor()){
				soldToAddr.setAddressIntrnl("");
				soldToAddr.setCustAddress("");
			}
			returnList.add(soldToAddr);
			returnList.addAll(this.applianceAddressList);
			this.applianceAddressList = returnList;
		}
	}


	private ApplianceAddress getExistAddress(List<ApplianceAddress> list, String custNum) {
		
		for(int i =0;i< list.size();i++){
			ApplianceAddress temp = list.get(i);
			if(StringUtils.equals(temp.getCustNum(), custNum)){
				return temp;
			}
		}
		return null;
	}
	
	private boolean isExistCust(ApplianceAddress cust) {
		
		boolean flag = false;
		if(originApplianceAddressList!=null && !originApplianceAddressList.isEmpty())
		{
			for(int i =0; i<originApplianceAddressList.size();i++)
			{
				ApplianceAddress addr = originApplianceAddressList.get(i);
				if(addr.getCustNum()!=null 
						&& cust.getCustNum()!=null 
						&& StringUtils.equals(addr.getCustNum(), cust.getCustNum()) ){
					flag = true;
				}
			}
		}
		return flag;
	}
	
    public Country getCntryObject(String cntryCode) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        CacheProcess cProcess = null;
        Country cntryObj = null;
        try {
            cProcess = CacheProcessFactory.singleton().create();
            cntryObj = cProcess.getCountryByCode3(cntryCode);
            return cntryObj;
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
            return null;
        }
    }	
	
	public List<ApplianceAddress> getApplianceAddressList() {
		return applianceAddressList;
	}
	public void setApplianceAddressList(List<ApplianceAddress> applianceAddressList) {
		this.applianceAddressList = applianceAddressList;
	}
	public List<ApplianceAddress> getOriginApplianceAddressList() {
		return originApplianceAddressList;
	}
	public void setOriginApplianceAddressList(
			List<ApplianceAddress> originApplianceAddressList) {
		this.originApplianceAddressList = originApplianceAddressList;
	}
	
	public Map getOriginApplianceLineItemMap() {
		return originApplianceLineItemMap;
	}


	public void setOriginApplianceLineItemMap(Map originApplianceLineItemMap) {
		this.originApplianceLineItemMap = originApplianceLineItemMap;
	}


	public String getWebQuoteNum() {
		return webQuoteNum;
	}
	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}
	public String getAddressType() {
		return addressType;
	}
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}
	
	public Integer getCurrMaxSecId() {
		return currMaxSecId;
	}


	public void setCurrMaxSecId(Integer currMaxSecId) {
		this.currMaxSecId = currMaxSecId;
	}


	public ApplianceAddress getSelectedCust() {
		return selectedCust;
	}


	public void setSelectedCust(ApplianceAddress selectedCust) {
		this.selectedCust = selectedCust;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getLob() {
		return lob;
	}


	public void setLob(String lob) {
		this.lob = lob;
	}


	public String getSelectAddrUrl() {
	
		String urlParams = ParamKeys.PARAM_COUNTRY + "="
		+ country + "," + ParamKeys.PARAM_LINE_OF_BUSINESS + "="
		+ lob + "," + ParamKeys.PARAM_QUOTE_NUM + "="
		+ webQuoteNum + "," + ParamKeys.PARAM_ADDRESS_TYPE + "="
		+ addressType+","+ParamKeys.PARAM_AGREEMENT_NUM+"="+agreementNumber;
		
		if(Boolean.valueOf(this.isSubmittedQuote))
			urlParams += ","+ParamKeys.PARAM_IS_SBMT_QT + "=true";
		
		StringBuffer params = new StringBuffer(urlParams);
		return genBtnParamsForAction(CustomerActionKeys.SAVE_APPLIANCE_ADDRESS,
				CustomerActionKeys.DISPLAY_SEARCH_APPL_ADDRESS, params.toString());
	}
	
    protected String genBtnParamsForAction(String jadeAction, String redirectURL, String params) {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
        String secondActionKey = appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY);
        StringBuffer sb = new StringBuffer();

        sb.append(actionKey).append("=").append(jadeAction);
        if (StringUtils.isNotBlank(redirectURL))
            sb.append(",").append(secondActionKey).append("=").append(redirectURL);
        if (StringUtils.isNotBlank(params))
            sb.append(",").append(params);
        return sb.toString();
    }
    
    public boolean isShipTo() {
    	return DraftQuoteConstants.APPLIANCE_SHIPTO_ADDTYPE.equals(getAddressType() );
    }
    
    public boolean shipNonAppliance(ApplianceAddress address) {
    	return DraftQuoteConstants.DEFAULT_APPLIANCE_ADDRESS == address.getQuoteLineItemSeqNum().intValue();
    }
    
    
    public String getReadOnlyTitleKey() {
    	return isShipTo() ? "ship_address" : "install_address";
    }
    
    public String getLineItemTitle() {
    	return isShipTo() ? "non_appliance_text1" : "install_non_appliance_text1";
    }
    
    public String getAddressNote() {
    	return isShipTo() ? "ship_address_note" : "install_address_note";
    }
    
    public String getReturnUrl() {
    	return HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_CURRENT_DRAFT_QUOTE);
    }

	/**
	 * @return the isSubmittedQuote
	 */
	public String getIsSubmittedQuote() {
		return isSubmittedQuote;
	}


	/**
	 * @param isSubmittedQuote the isSubmittedQuote to set
	 */
	public void setIsSubmittedQuote(String isSubmittedQuote) {
		this.isSubmittedQuote = isSubmittedQuote;
	}
}
