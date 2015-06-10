package com.ibm.dsw.quote.customer.process;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceFailureException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.ApplianceAddress;
import com.ibm.dsw.quote.common.domain.ApplianceLineItem;
import com.ibm.dsw.quote.common.domain.ApplianceLineItemAddrDetail;
import com.ibm.dsw.quote.common.domain.ComparatorApplianceAddressOrItem;
import com.ibm.dsw.quote.common.domain.ComparatorApplianceLineItem;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.CustomerFactory;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.common.service.CustomerCreateService;
import com.ibm.dsw.quote.customer.contract.CreateApplianceAddressContract;
import com.ibm.dsw.quote.customer.contract.CustomerCreateContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerProcess_Impl<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */
public abstract class CustomerProcess_Impl extends TopazTransactionalProcess implements CustomerProcess {

    private LogContext logContext = LogContextFactory.singleton().getLogContext();
    /**
     *  
     */
    public CustomerProcess_Impl() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	
	public Customer createApplianceCustomer(CreateApplianceAddressContract createApplianceAddressContract) throws Exception, WebServiceFailureException
	{
		Customer customer;
		logContext.debug(this, "To call SAP RFC to create the new customer in SAP...");
		try{
			customer = CustomerFactory.singleton().createCustomer();
			customer.setCntFirstName(createApplianceAddressContract.getCntFirstName());
			customer.setCntLastName(createApplianceAddressContract.getCntLastName());
			customer.setAddress1(createApplianceAddressContract.getAddress1());
			customer.setInternalAddress(createApplianceAddressContract.getAddress2());
			customer.setCustName(createApplianceAddressContract.getCompanyName1());
			customer.setCustName2(createApplianceAddressContract.getCompanyName2());
			customer.setCity(createApplianceAddressContract.getCity());
			customer.setPostalCode(createApplianceAddressContract.getPostalCode());
			customer.setCntPhoneNumFull(createApplianceAddressContract.getCntPhoneNumFull());
			customer.setWebCustStatCode(createApplianceAddressContract.getState());
			customer.setCountryCode(createApplianceAddressContract.getCountry());
			customer.setSapRegionCode(createApplianceAddressContract.getState());
			customer.setAddressType(createApplianceAddressContract.getAddressType());
			
			//get currency code for specific country
			if(customer.getCountryCode() != null){
				Country country = CacheProcessFactory.singleton().create().getCountryByCode3(customer.getCountryCode());
				if(country != null && country.getCurrencyList() != null && country.getCurrencyList().size() > 0){
					CodeDescObj currency = (CodeDescObj)country.getCurrencyList().get(0);
					customer.setCurrencyCode(currency.getCode());
				}
						
			}
			
			CustomerCreateService service = new CustomerCreateService();
			service.execute(customer);
            logContext.debug(this, "Successfully create customer : " + customer.getCustNum());
    		return customer;
		}catch(RemoteException e){
			e.printStackTrace();
			throw new WebServiceFailureException("Failed to call SAP to create the new customer.", MessageKeys.MSG_SERVICE_UNAVAILABLE_ERROR, e);
		}
		
	}
	

	public CustomerSearchResultList searchCustomerByDswIcnRcdID(String sapCtrctNum, String dswIcnRcdID, String cntryCode, 
            String lineOfBus, int startPos, boolean searchPayer, String progMigrtnCode, String audCode, String userSiteNum) throws QuoteException {
        CustomerSearchResultList resultList = null;
        try {
            //begin the transaction
            this.beginTransaction();

            //country = CountryFactory.singleton().findByCode3(code3);
            resultList = CustomerFactory.singleton().findByDswIcnRcdID(sapCtrctNum, dswIcnRcdID, cntryCode, lineOfBus,
                    startPos, searchPayer, progMigrtnCode, audCode, userSiteNum);

            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return resultList;
    }
  
    
    public CustomerSearchResultList searchCustomerByAttr(String customerName, String cntryCode, String contractOption,
            String anniversary, String lineOfBus, int findActiveFlag, int startPos, String stateCode,
            int searchType, String audCode, String userSiteNum) throws QuoteException {
        CustomerSearchResultList resultList = null;
        try {
            //begin the transaction
            this.beginTransaction();

            resultList = CustomerFactory.singleton().findByAttribute(customerName, cntryCode, contractOption, 
                    anniversary, lineOfBus, findActiveFlag, startPos, stateCode, searchType, audCode, userSiteNum);

            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return resultList;
    }

    public Customer createCustomer(ProcessContract contract) throws QuoteException {
        Customer customer; 
        try {
            CustomerCreateContract custCreateContract = (CustomerCreateContract)contract;
            String userID = custCreateContract.getUserId();
            this.beginTransaction();
            //customer = CustomerFactory.singleton().createCustomer(custCreateContract.getCustomerNumber(), userID);
            customer = CustomerFactory.singleton().createCustomer(custCreateContract, userID);
  
            this.commitTransaction();
        
        } catch (TopazException e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        } finally {
            rollbackTransaction();
        }
        return customer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.customer.process.CustomerProcess#findDuplCustomers(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public SearchResultList findDuplCustomers(String customerName, String address, String city, String regionCode,
            String postalCode, String country) throws QuoteException {
        SearchResultList resultList = null;
        try {
            //begin the transaction
            this.beginTransaction();

            resultList = CustomerFactory.singleton().findDuplCustomers(customerName, address, city, regionCode,
                    postalCode, country);

            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return resultList;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.customer.process.CustomerProcess#getWebCustomerByQuoteNum(java.lang.String)
     */
    public Customer getWebCustomerByQuoteNum(String webQuoteNum, String lob, String soldToCustNum, String sapCtrctNum,
            int webCustId) throws SPException, QuoteException {
        
        Customer customer = null;
        int newCustFlag = StringUtils.isBlank(soldToCustNum) ? 1 : 0;
        
        try {
            //begin the transaction
            this.beginTransaction();
            logContext.debug(this, "To retrieve customer by web quote num: " + webQuoteNum);
            customer = CustomerFactory.singleton().findByQuoteNum(lob, newCustFlag, soldToCustNum, sapCtrctNum,
                    webQuoteNum, webCustId);
            logContext.debug(this, "Customer:\n" + customer.toString());

            //commit the transaction
            this.commitTransaction();
        } catch (SPException spe) {
            throw new SPException(spe);
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return customer;
    }
    
    public CustomerSearchResultList searchEndCustByDswID(String dswID, String lineOfBus,String country, int startPos ) throws QuoteException {
        CustomerSearchResultList resultList = null;
        try {
            //begin the transaction
            this.beginTransaction();

            //country = CountryFactory.singleton().findByCode3(code3);
            resultList = CustomerFactory.singleton().findEndCusByDswID(dswID, lineOfBus,country, startPos);

            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return resultList;
    }
  
    
    public CustomerSearchResultList searchEndCustByAttr(String customerName, String cntryCode, String lineOfBus, String stateCode, int startPos) throws QuoteException{
        CustomerSearchResultList resultList = null;
        try {
            //begin the transaction
            this.beginTransaction();

            resultList = CustomerFactory.singleton().findEndCustByAttr(customerName, cntryCode, lineOfBus, stateCode, startPos );

            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return resultList;
    }


	@Override
	public Map searchApplianceAddress(
			String webQuoteNum, String addressType,String lob,String custNum,String bpSiteNum) throws QuoteException {
		Map resultList = null;
		try {
            //begin the transaction
            this.beginTransaction();

            resultList = CustomerFactory.singleton().searchApplianceAddress(webQuoteNum,  addressType,lob,custNum, bpSiteNum);

            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
        return resultList;
	}
    
	public boolean updateCustInstallAtOpt(String webQuoteNum, int installAtOption, int shipToOption) throws QuoteException {
		boolean result = false;
		try {
			this.beginTransaction();
			result = CustomerFactory.singleton().updateInstallAtAddr(
					webQuoteNum, installAtOption, shipToOption);
			this.commitTransaction();
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
			throw new QuoteException(e);
		} finally {
			rollbackTransaction();
		}
		return result;
	}
	
	@Override
	public void updateApplianceAddress(String userId, String webQuoteNum, String addressType,List<ApplianceAddress> addressList) throws QuoteException {
		try {
            //begin the transaction
            this.beginTransaction();
            
            for(ApplianceAddress address:addressList){
            	CustomerFactory.singleton().updateApplianceAddress(userId,webQuoteNum,addressType,address);
            }
            
            //commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
        	logContext.error(this, tce.getMessage());
            throw new QuoteException(tce);
        } finally {
            rollbackTransaction();
        }
	}
	
	public ApplianceLineItemAddrDetail findLineItemAddr(String webQuoteNum,String bpSiteNum, List quoteLineItems, boolean isHasNeedShipToParts, Customer soldToCustomer)throws QuoteException{ 
		ApplianceLineItemAddrDetail details = null;
		if ( isHasNeedShipToParts )
		{
			details = findLineItemAddr(webQuoteNum, bpSiteNum, quoteLineItems);
		}
		if ( details == null )
		{
			details = new ApplianceLineItemAddrDetail();
		}
		updateAddressByMTM(quoteLineItems, details, soldToCustomer);
		return details;
	}
	
	private ApplianceLineItemAddrDetail findLineItemAddr(String webQuoteNum,String bpSiteNum, List quoteLineItems)throws QuoteException{
		List jdbcResultList = null;
		ApplianceLineItemAddrDetail details = null;
		try {
			//begin the transaction
            this.beginTransaction();
            
            jdbcResultList = CustomerFactory.singleton().findLineItemAddr(webQuoteNum, bpSiteNum);
            if(jdbcResultList!=null && jdbcResultList.size()== 2){
            	details = new ApplianceLineItemAddrDetail();
            }else{
            	return null;
            }
            //all line items in this quote
    		Map lineItemDetailMap = getLineItemMap(quoteLineItems);
    		
    		Map shipToMap = (Map) jdbcResultList.get(0);
    		int shipPoOption = (Integer)shipToMap.get(DraftQuoteConstants.APPLIANCE_ADDRESS_OPTION);
    		//line items need to set ship to / install at
    		Map applianceLineItemMap =  (Map) shipToMap.get(DraftQuoteConstants.LINE_ITEM_MAP);
    		Set applineitem = applianceLineItemMap.keySet();
    		
    		// re-init appliance part
    		if(!applianceLineItemMap.isEmpty() && !lineItemDetailMap.isEmpty()){
    			Iterator itTemp =  applineitem.iterator();
    			while(itTemp.hasNext())
    			{
    				String itStr = (String) itTemp.next();
    				if(lineItemDetailMap.get(itStr) != null){
    					ApplianceLineItem itLineOrg = (ApplianceLineItem) applianceLineItemMap.get(itStr);
    					ApplianceLineItem itLine = (ApplianceLineItem) lineItemDetailMap.get(itStr);
    					itLine.copy(itLineOrg);
    					lineItemDetailMap.put(itStr, itLine);
    				}
    			}
    		}
    		
    		List<ApplianceAddress> shipToAddrList = assemblyList(shipToMap,shipPoOption,lineItemDetailMap,true,applineitem);
    		details.setShipToAddressList(shipToAddrList);
    		
    		Map installAtMap = (Map) jdbcResultList.get(1);
    		int installPoOption = (Integer)installAtMap.get(DraftQuoteConstants.APPLIANCE_ADDRESS_OPTION);
    		
    		List<ApplianceAddress> installAddrList = assemblyList(installAtMap,installPoOption,lineItemDetailMap,false,applineitem);
    		details.setInstallAtAddressList(installAddrList);
    		
    		details.setInstallAtOpt(installPoOption);
    		
    		Iterator it = lineItemDetailMap.values().iterator();
    		List<ApplianceLineItem> itemList = new ArrayList<ApplianceLineItem>();
    		while(it.hasNext()){
    			itemList.add((ApplianceLineItem) it.next());
    		}
    		
    		Collections.sort(itemList, new ComparatorApplianceLineItem());
    		details.setLineItemsList(itemList);
    		
    		//commit the transaction
            this.commitTransaction();
        } catch (TopazException tce) {
            throw new QuoteException(tce);
        }finally {
            rollbackTransaction();
        }
        return details;
	}
	
	private void updateAddressByMTM(List quoteLineItems, ApplianceLineItemAddrDetail ads, Customer soldToCustomer) throws QuoteException
	{
    	if ( quoteLineItems == null )
    	{
    		logContext.debug(this, "list is null");
    		return;
    	}
    	logContext.debug(this, "begin for renewal taxation rules:" + quoteLineItems.size());
    	ApplianceAddress soldToAddress = null;
    	
    	for ( int i = 0; i < quoteLineItems.size(); i++ )
    	{
    		QuoteLineItem item = (QuoteLineItem)quoteLineItems.get(i);
    		if ( !item.isApplncPart() )
    		{
    			continue;
    		}
    		/**
    		 if item has address, and tied to a config id, use this address
    		 	continue;
    		 else if item has mtm, not upgrade, sales part
    		 	get address base mtm
    		 else if renewal part
    		    get mtm from sods.quote_line_item
    		    then get install at from SODS2.MTM_SERIAL_DTL
    		 end if;
    		 */
    		ApplianceLineItem applncLineItem = getExistsApplncLineItem(item, ads);
    		logContext.debug(this, "exists line item object: " + applncLineItem);
    		if ( applncLineItem != null && applncLineItem.getShipToAddr() != null && !StringUtils.isBlank(item.getConfigrtnId()) 
    				&& !StringUtils.equals(PartPriceConstants.APPLNC_NOT_ON_QUOTE, item.getConfigrtnId().trim()) )
    		{
    			logContext.debug(this, "part " + item.getPartNum() + ":" + item.getSeqNum() + " has ship to address");
    			continue;
    		}
    		else
    		{
    			ApplianceAddress addr = null;
    			logContext.debug(this, "item.isRenewalPart(): " + item.isRenewalPart() + ";item.isApplncUpgrade():" + item.isApplncUpgrade() + ";" + item.getPartNum() + ":" + item.getMachineType() + ":" + item.getModel() + ":" + item.getSerialNumber());
    			if ( !item.isReferenceToRenewalQuote() && !item.isApplncUpgrade() 
    					&& (StringUtils.isBlank(item.getConfigrtnId()) || StringUtils.equals(PartPriceConstants.APPLNC_NOT_ON_QUOTE, item.getConfigrtnId().trim()) )
    					&& StringUtils.isNotEmpty(item.getMachineType()) && StringUtils.isNotEmpty(item.getModel()) && StringUtils.isNotEmpty(item.getSerialNumber()) )
        		{
    				logContext.debug(this, "part " + item.getPartNum() + ":" + item.getSeqNum() + " has valide mtm");
        			//get address by mtm
        			addr = getInstallAtByMTM(item.getMachineType(), item.getModel(), item.getSerialNumber(), null, 0);
        		}
        		else if (item.isReferenceToRenewalQuote() && (StringUtils.isBlank(item.getConfigrtnId()) || StringUtils.equals(PartPriceConstants.APPLNC_NOT_ON_QUOTE, item.getConfigrtnId().trim())))
        		{
        			logContext.debug(this, "part " + item.getPartNum() + ":" + item.getSeqNum() + " is renewal part");
        			addr = getInstallAtByMTM(item.getMachineType(), item.getModel(), item.getSerialNumber(), item.getRenewalQuoteNum(), item.getRenewalQuoteSeqNum());
        		}
        		else
        		{
        			continue;
        		}
    			if ( addr != null )
    			{
    				ApplianceAddress temp = getExistsMTMAddress(addr, ads);
    				if ( temp != null )
    				{
    					addr = temp;
    				}
    				else
    				{
    					addr.setHeadLevelAddr(false);
        				addr.setAddrBaseMTM(true);
    					addMTMToApplianceAddressList(addr, ads);
    				}
    				logContext.debug(this, "get addr by mtm: " + addr.getCustNum());
    				if ( applncLineItem == null )
    				{
    					applncLineItem = ApplianceLineItemAddrDetail.createApplianceLineItem(item, ads);
    				}
    				
    				applncLineItem.setShipToAddr(addr);
    			}
    			else if ( addr == null && StringUtils.isNotEmpty(item.getMachineType()) && StringUtils.isNotEmpty(item.getModel()) && StringUtils.isNotEmpty(item.getSerialNumber()) )
    			{
    				//set sold to as the appliance part's ship to
    				logContext.debug(this, "this item will use sold to as ship to:" + item.getPartNum() + ":" + item.getDestSeqNum());
    				if ( applncLineItem == null )
    				{
    					applncLineItem = ApplianceLineItemAddrDetail.createApplianceLineItem(item, ads);
    				}
    				
    				if ( soldToAddress == null )
    				{
    					soldToAddress = ApplianceLineItemAddrDetail.getAddrFromCustomer(soldToCustomer);
    					soldToAddress.setAddrBaseMTM(true);
    					addMTMToApplianceAddressList(soldToAddress, ads);
    				}
    				applncLineItem.setUseSoldTo(true);
    				applncLineItem.setShipToAddr(soldToAddress);
    			}
    		}
    	}
	}
	
	private ApplianceAddress getExistsMTMAddress(ApplianceAddress addr, ApplianceLineItemAddrDetail ads)
	{
		List<ApplianceAddress> list = ads.getShipToAddressList();
    	if ( list == null )
    	{
    		return null;
    	}
    	for ( ApplianceAddress temp : list )
    	{
    		if ( temp.isAddrBaseMTM() && addr.compareMTMAddr(temp) )
    		{
    			return temp;
    		}
    	}
    	return null;
	}
	
	private void addMTMToApplianceAddressList(ApplianceAddress addr, ApplianceLineItemAddrDetail ads)
    {
    	List<ApplianceAddress> list = ads.getShipToAddressList();
    	if ( list == null )
    	{
    		list = new ArrayList<ApplianceAddress>();
    		ads.setShipToAddressList(list);
    	}
    	list.add(addr);
    }
	
	private ApplianceLineItem getExistsApplncLineItem(QuoteLineItem item, ApplianceLineItemAddrDetail ads)
    {
    	List<ApplianceLineItem> list = ads.getLineItemsList();
    	
    	if ( list == null )
    	{
    		return null;
    	}
    	logContext.debug(this, "exists line items list size: " + list.size());
    	for ( ApplianceLineItem applncItem : list )
    	{
    		logContext.debug(this, "exists line item applncItem.getDestSeqNum()=" + applncItem.getDestSeqNum() + "; item.getDestSeqNum()=" + item.getDestSeqNum());
    		if ( applncItem.getDestSeqNum() != null  && applncItem.getDestSeqNum().intValue() == item.getDestSeqNum() )
    		{
    			return applncItem;
    		}
    	}
    	return null;
    }
	
	private Map getLineItemMap(List quoteLineItems)
	{
//		lineItem = new ApplianceLineItem();
//      lineItem.setQuoteLineItemSeqNum(rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM"));
//      lineItem.setDestSeqNum(rs.getInt("DEST_OBJCT_LINE_ITM_SEQ_NUM"));
//      lineItem.setConfigrtnId(rs.getString("CONFIGRTN_ID"));
//      lineItem.setPartNum(rs.getString("PART_NUM"));
//      lineItem.setQuoteSectnSeqNum(rs.getInt("QUOTE_SECTN_SEQ_NUM"));
//      lineItem.setPartDscrLong(rs.getString("PART_DSCR_LONG"));
//      lineItem.setCustReqstdArrivlDate(rs.getDate("CUST_REQSTD_ARRIVL_DATE"));
//      boolean isAppl = rs.getInt("IS_APPLIANCE_PART")==1?true:false;
//      lineItem.setAppliancePart(isAppl);
//      returnMap.put(String.valueOf(lineItem.getQuoteLineItemSeqNum()),  lineItem);
		Map map = new HashMap();
		if ( quoteLineItems == null )
		{
			return map;
		}
		for ( int i = 0; i < quoteLineItems.size(); i++ )
		{
			QuoteLineItem item = (QuoteLineItem)quoteLineItems.get(i);
			ApplianceLineItem applncItem = ApplianceLineItem.createApplianceLineItem(item);
			map.put(applncItem.getQuoteLineItemSeqNum().toString(), applncItem);
		}
		return map;
	}

	// Assemble ship to/install at address info
	public ApplianceLineItemAddrDetail findAddrLineItem(String webQuoteNum,String bpSiteNum, List quoteLineItems, boolean isHasNeedShipToParts, Customer soldToCustomer)throws QuoteException{
		ApplianceLineItemAddrDetail detail = this.findLineItemAddr(webQuoteNum, bpSiteNum, quoteLineItems, isHasNeedShipToParts, soldToCustomer);
		
		List<ApplianceAddress> shipToAddressList = detail.getShipToAddressList();
		if(shipToAddressList != null){
			for(int i=0;i<shipToAddressList.size();i++){
				ApplianceAddress addr = shipToAddressList.get(i);
				List<ApplianceLineItem> lineItemList = new ArrayList<ApplianceLineItem>();
				logContext.debug(this, "addr after mtm: " + addr.getCustNum() + ":" + addr.isAddrBaseMTM() + ":" + addr.hashCode());
				for(int j=0;j<detail.getLineItemsList().size();j++){
					ApplianceLineItem item = detail.getLineItemsList().get(j);
					logContext.debug(this, "item after mtm: " + shipToAddressList.size() +":"+ item.getPartNum() + ":" + item.getDestSeqNum() + ":" + (item.getShipToAddr() == null ? "null ship to" : item.getShipToAddr().getCustNum()));
					//StringUtils.equals( item.getShipToAddr().getCustNum(),addr.getCustNum() )
					if(shipToAddressList.size() == 1 || (item.getShipToAddr() != null  && item.getShipToAddr() == addr)){
						lineItemList.add(item);
					}
				}
				addr.setLineItemList(lineItemList);
			}
		}
		
		
		List<ApplianceAddress> installAtAddressList = detail.getInstallAtAddressList();
		if(installAtAddressList != null){
			for(int i=0;i<installAtAddressList.size();i++){
				ApplianceAddress addr = installAtAddressList.get(i);
				List<ApplianceLineItem> lineItemList = new ArrayList<ApplianceLineItem>();
				for(int j=0;j<detail.getLineItemsList().size();j++){
					ApplianceLineItem item = detail.getLineItemsList().get(j);
					if(item.getInstallAtAddr() != null ){
						if((installAtAddressList.size() == 1 && item.isAppliancePart()  ) || ( StringUtils.equals( item.getInstallAtAddr().getCustNum(),addr.getCustNum() ))){
							lineItemList.add(item);
						}
					}
				}
				addr.setLineItemList(lineItemList);
			}
			
			
			List<ApplianceAddress> installReturnList = new ArrayList<ApplianceAddress>();
			for(ApplianceAddress insAddr : installAtAddressList){
				if(insAddr.getLineItemList().size()>0){
					installReturnList.add(insAddr);
				}
			}
			detail.setInstallAtAddressList(installReturnList);
				
		}
		
		return detail;
	}
	/**
	 * 
	 * @param queryList   ship to /install at address detail map from S_QT_CUST_ADDRESS
	 * @param poOption    sold to, new; sold to, ship to; new
	 * @param lineItemDetailMap all line items in this quote
	 * @param type		  true ship to; false install at
	 * @param applineitem appliance line item parts
	 * @return
	 */
	private List<ApplianceAddress> assemblyList(Map queryList,int poOption,Map lineItemDetailMap,boolean type, Set applineitem){
		if(queryList != null && !queryList.isEmpty()){
			List<ApplianceAddress> originApplianceAddressList = (List<ApplianceAddress>) queryList.get(DraftQuoteConstants.APPLIANCE_ADDRESS_LIST);
			
			//items associated with a main part
			Set<String> lineNumSet = ApplianceLineItem.getHiddenAppliLineNumSet(lineItemDetailMap);
			
			List<ApplianceAddress> returnList = new ArrayList<ApplianceAddress>();
			
			Set<String> lineItemSet = new HashSet<String>();
			
			ApplianceAddress headLevelAddr = null;
			// -2 use sold to
			if(originApplianceAddressList.size()==1
					&& originApplianceAddressList.get(0).getQuoteLineItemSeqNum() == -2){
				ApplianceAddress newAdd = originApplianceAddressList.get(0);
				  for (Object value : lineItemDetailMap.values()) {
					  ApplianceLineItem temp = (ApplianceLineItem)value;
					  if(type){
						  newAdd.setQuoteLineItemSeqNum(DraftQuoteConstants.DEFAULT_APPLIANCE_ADDRESS);
						  newAdd.setHeadLevelAddr(true);
						  temp.copyAddr(newAdd,type);
						  lineItemDetailMap.put(String.valueOf(temp.getQuoteLineItemSeqNum()), temp);
					  }else{
						  if(temp.isAppliancePart()){
							  temp.copyAddr(newAdd,type);
							  lineItemDetailMap.put(String.valueOf(temp.getQuoteLineItemSeqNum()), temp);
						  }
					  }
					  headLevelAddr = newAdd;
				  }
				  
				returnList.add(newAdd);	
			}else{
				ComparatorApplianceAddressOrItem comparator = null;
				if(originApplianceAddressList!=null && !originApplianceAddressList.isEmpty()){
					comparator=new ComparatorApplianceAddressOrItem();
					Collections.sort(originApplianceAddressList, comparator);
				}
				Set<String> custNumSet = new HashSet<String>();
				Set<Integer> LineItemSeqNumSet = new HashSet<Integer>();
				for(int i =0; i<originApplianceAddressList.size();i++){
					ApplianceAddress temp = originApplianceAddressList.get(i);
					
					if(LineItemSeqNumSet.contains(temp.getQuoteLineItemSeqNum())){
						continue;
					}else{
						LineItemSeqNumSet.add(temp.getQuoteLineItemSeqNum());
					}
					
					if(custNumSet.contains(temp.getCustNum())){
						ApplianceAddress existAddress = getExistAddress(returnList,temp.getCustNum());
						if(temp.getQuoteLineItemSeqNum() != DraftQuoteConstants.DEFAULT_APPLIANCE_ADDRESS){
							ApplianceLineItem item = (ApplianceLineItem) lineItemDetailMap.get(String.valueOf(temp.getQuoteLineItemSeqNum()));
							if(item!=null){
								item.copyAddr(existAddress,type);
								lineItemSet.add(String.valueOf(item.getQuoteLineItemSeqNum()));
								lineItemDetailMap.put(String.valueOf(item.getQuoteLineItemSeqNum()), item);
							}
						}else{
							existAddress.setQuoteLineItemSeqNum(DraftQuoteConstants.DEFAULT_APPLIANCE_ADDRESS);
							existAddress.setHeadLevelAddr(true);
							headLevelAddr = existAddress;
						}
					}else{
						custNumSet.add(temp.getCustNum());
						
						if(temp.getQuoteLineItemSeqNum() != DraftQuoteConstants.DEFAULT_APPLIANCE_ADDRESS){
							ApplianceLineItem item = (ApplianceLineItem) lineItemDetailMap.get(String.valueOf(temp.getQuoteLineItemSeqNum()));
							if(item!=null){
								item.copyAddr(temp,type);
								lineItemSet.add(String.valueOf(item.getQuoteLineItemSeqNum()));
								lineItemDetailMap.put(String.valueOf(item.getQuoteLineItemSeqNum()), item);
							}
						}else{
							temp.setQuoteLineItemSeqNum(DraftQuoteConstants.DEFAULT_APPLIANCE_ADDRESS);
							temp.setHeadLevelAddr(true);
							headLevelAddr = temp;
						}
						returnList.add(temp);
					}
				}
				
				Set<String> itemMapKeySet = lineItemDetailMap.keySet();
				Set<String> itemMapKeySetTemp = new HashSet<String>();
				Iterator<String>  iterator = itemMapKeySet.iterator();
				while(iterator.hasNext()){
					itemMapKeySetTemp.add(iterator.next());
				}
				if(lineItemSet.size() < itemMapKeySetTemp.size()){  // add non-appliance line item to head level address 
					itemMapKeySetTemp.removeAll(lineItemSet);
					for(Object key:itemMapKeySetTemp){
						if(StringUtils.isNotBlank((String)key)&&!DraftQuoteConstants.APPLIANCE_ADDRESS_OPTION.equals((String)key)){
							ApplianceLineItem aitem = (ApplianceLineItem)lineItemDetailMap.get((String)key);
							if(aitem != null){
								if(type && headLevelAddr != null){
									aitem.copyAddr(headLevelAddr,type);
									lineItemDetailMap.put(String.valueOf(aitem.getQuoteLineItemSeqNum()), aitem);
								}
							}
						}
					}
				}
				
			}
			
			// for affiliated appliance part
			if(!lineNumSet.isEmpty()){
				Iterator<String> setIt = lineNumSet.iterator();
				while(setIt.hasNext()){
					ApplianceLineItem hiddenLineItem = (ApplianceLineItem)lineItemDetailMap.get(setIt.next());
					if(hiddenLineItem != null){
						Iterator lid = lineItemDetailMap.keySet().iterator();
						while(lid.hasNext()){
							ApplianceLineItem aitemp = (ApplianceLineItem)lineItemDetailMap.get(lid.next());
							if(aitemp!=null && aitemp.isAppliancePart()
									&& StringUtils.equals(aitemp.getAppliance(), "1")
									&& StringUtils.equals(aitemp.getConfigrtnId().trim(),hiddenLineItem.getConfigrtnId().trim())){
								hiddenLineItem.setInstallAtAddr(aitemp.getInstallAtAddr());
								hiddenLineItem.setShipToAddr(aitemp.getShipToAddr());
								lineItemDetailMap.put(String.valueOf(hiddenLineItem.getQuoteLineItemSeqNum()), hiddenLineItem);
							}
							
						}
					}
				}
			}
			return returnList;
		}else{
			return new ArrayList<ApplianceAddress>();
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
	
	public boolean updateLineItemAddr(String webQuoteNum, String userId) throws QuoteException
	{
		boolean result = false;
		try {
			result = CustomerFactory.singleton().updateLineItemAddr(
					webQuoteNum, userId);
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
			throw new QuoteException(e);
		} 
		return result;
	}
	
	public ApplianceAddress getInstallAtByMTM(String type, String mode, String serialNum, String renewlQuoteNum, int renewalLineItemSeq) throws QuoteException
	{
		ApplianceAddress result = null;
		try {
			this.beginTransaction();
			result = CustomerFactory.singleton().getInstallAtByMTM(type, mode, serialNum, renewlQuoteNum, renewalLineItemSeq);
			this.commitTransaction();
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
			throw new QuoteException(e);
		} finally {
			rollbackTransaction();
		}
		return result;
	}
	
}
