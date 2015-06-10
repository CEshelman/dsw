package com.ibm.dsw.quote.ps.viewbean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.dsw.quote.ps.domain.PartSearchService;
import com.ibm.ead4j.common.config.EAD4JBootstrapKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class PartSearchServiceViewBean extends BaseViewBean {
	 
	private transient List browseServicesResults;
	transient List brandsList;
	transient List serviceAgreements;
	transient List configuredPids;
    String lob;	
    String country;
    String currency;	
    String countryDesc;
    String currencyDesc;
    String quoteNumber;
    String audience = SalesRep.AUDIENCE;
    String quoteFlag = "";
    String customerNumber;
	String sapContractNum;
    private boolean hasConfigrtn = false;
    transient Map nodesMap;
    boolean showFindPart = true;
    String addNewMonthlySWFlag = "false";
    String configrtnId;
    String configrtnActionCode;
    String chrgAgrmtNum;
    String orgConfigId;
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        lob = params.getParameterAsString(ParamKeys.PARAM_LINE_OF_BUSINESS);
        Country contry = (Country)params.getParameter(ParamKeys.PARAM_COUNTRY);
        country = contry.getCode3();
        countryDesc = contry.getDesc();
        quoteFlag =params.getParameterAsString(ParamKeys.PARAM_QUOTE_FLAG);
        currency = (String)params.getParameter(ParamKeys.PARAM_CURRENCY);
        CodeDescObj cntryCurrency = (CodeDescObj)params.getParameter(PartSearchParamKeys.PARAM_COUNTRY_CURRENCY);
        currencyDesc = cntryCurrency.getCodeDesc();
        quoteNumber = params.getParameterAsString(ParamKeys.PARAM_QUOTE_NUM);
	    browseServicesResults = (List)params.getParameter(PartSearchParamKeys.BROWSE_SERVICES_RESULTS);
	    configuredPids = (List)params.getParameter(PartSearchParamKeys.BROWSE_SERVICES_CONFIGURED_PIDS);
	    brandsList = (List)params.getParameter(PartSearchParamKeys.BROWSE_SERVICES_BRANDS_LIST);
	    serviceAgreements = (List)params.getParameter(PartSearchParamKeys.BROWSE_SERVICES_AGREEMENTS);
	    Boolean hasConfigrtnObj = (Boolean)params.getParameter(PartSearchParamKeys.BROWSE_SERVICES_HAS_CONF_AGR);
	    hasConfigrtn = hasConfigrtnObj == null? false: hasConfigrtnObj.booleanValue();
	    addNewMonthlySWFlag = params.getParameterAsString(PartSearchParamKeys.PARAM_ADD_NEW_MONTHLY_SW) == null ? "false" : params.getParameterAsString(PartSearchParamKeys.PARAM_ADD_NEW_MONTHLY_SW); 
	    chrgAgrmtNum=params.getParameterAsString(ParamKeys.PARAM_CHRG_AGRMT_NUM) == null ? "" : params.getParameterAsString(ParamKeys.PARAM_CHRG_AGRMT_NUM);
        configrtnActionCode=params.getParameterAsString(ParamKeys.PARAM_CONFIGRTN_ACTION_CODE) == null ? "" : params.getParameterAsString(ParamKeys.PARAM_CONFIGRTN_ACTION_CODE);
        configrtnId=params.getParameterAsString(ParamKeys.PARAM_CONFIGRTN_ID) == null ? "" : params.getParameterAsString(ParamKeys.PARAM_CONFIGRTN_ID);
        orgConfigId=params.getParameterAsString(ParamKeys.PARAM_ORG_CONFIG_ID) == null ? "" : params.getParameterAsString(ParamKeys.PARAM_ORG_CONFIG_ID);
        sortBrandsList();
	    initialNodesMap();
	    customerNumber = (String)params.getParameter(ParamKeys.PARAM_CUST_NUM);
	    sapContractNum = (String)params.getParameter(ParamKeys.PARAM_SAP_CONTRACT_NUM);
	    if(hasConfigrtn && serviceAgreements != null &&  serviceAgreements.size() > 0 ){
	    	 showFindPart = false;
	    }
	    if(QuoteConstants.LOB_SSP.equalsIgnoreCase(lob)){
            showFindPart = false;
        }
    }

	public List getBrandsList() {
		return brandsList;
	}
	
	public List getServiceAgreements() {
		return serviceAgreements;
	}

	public String getLob() {
		return lob;
	}

	public String getCountry() {
		return country;
	}

	public String getCurrency() {
		return currency;
	}

	public String getCountryDesc() {
		return countryDesc;
	}

	public String getCurrencyDesc() {
		return currencyDesc;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}
	
	public String getAudience() {
		return audience;
	}

	public String getQuoteFlag() {
		return quoteFlag;
	}
	
    public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getSapContractNum() {
		return sapContractNum;
	}

	public void setSapContractNum(String sapContractNum) {
		this.sapContractNum = sapContractNum;
	}
	
	public List serviceAgreementsOptions(){
		List options;
		ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
		int optionsSize = (serviceAgreements  == null || serviceAgreements.isEmpty())? 1 : serviceAgreements.size()+1;
		options = new ArrayList(optionsSize);
		StringBuffer optionLabel;
		StringBuffer optionValue;
		String newServiceAgreement = appCtx.getI18nValueAsString(I18NBundleNames.PART_SEARCH_BASE, locale, PartSearchParamKeys.NEW_SERVICE_AGREEMENT);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd",getLocale());
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy",getLocale());
		
		if( serviceAgreements == null || serviceAgreements.size() == 0){
			options.add(new SelectOptionImpl(newServiceAgreement,"-2", true));
		}else{
			if(!hasConfigrtn){
				String selectOne = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.SELECT_ONE);
				options.add(new SelectOptionImpl(selectOne,"-1", true));
				options.add(new SelectOptionImpl(newServiceAgreement,"-2", false));
			}
			Object[] agreementParams;
			for(int i = 0; i < serviceAgreements.size(); i++){
				agreementParams = (Object[])serviceAgreements.get(i);
				optionLabel = new StringBuffer((String)agreementParams[0] + " - " +(String)agreementParams[2]);
				optionValue = new StringBuffer((String)agreementParams[0] +"-"+(String)agreementParams[1]+"-");
				
				if( agreementParams[3] != null){
					optionValue.append((String)agreementParams[3]+"-");
				}
				
				//filtered the agreement when it's endDate is null
				if(agreementParams[4] == null){
	        		continue;
				}else{
					optionLabel.append(" - "+sdf2.format((java.sql.Date)agreementParams[4]));
					optionValue.append(sdf.format(agreementParams[4]));
				}
				
				
				//@see PartSearchProcess_jdbc.getPartSearchServiceResults() : 
				//      params[5] = StringUtils.trim(rs.getString("WITH_ON_DEMOND"));
				//      WITH_SUBSCRIPTION : 1 - existing subscription; 0 - not existing WITH_SUBSCRIPTION
				// do not allow co-terming with a configuration that does not have any subscription parts
				optionValue.append("-"+agreementParams[5]);
				
				options.add(new SelectOptionImpl(optionLabel.toString(),optionValue.toString(), false));
			}
		}
		return options;
	}
	
	 public String getPartSearchURLParams() {
	        return ParamKeys.PARAM_COUNTRY + "=" + getCountry()
	        	+ "&amp;" + ParamKeys.PARAM_CURRENCY + "=" + getCurrency()
	        	+ "&amp;" + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + getLob()
	        	+ "&amp;" + ParamKeys.PARAM_AUDIENCE + "=" + getAudience()
	        	+ "&amp;" + ParamKeys.PARAM_QUOTE_NUM + "=" + getQuoteNumber()
	        	+ "&amp;" + ParamKeys.PARAM_QUOTE_FLAG + "=" + getQuoteFlag();
	}
	 
	public Map getNodesMap() {
		return nodesMap;
	}
	
	private void initialNodesMap(){
		nodesMap = new HashMap();
		if(browseServicesResults != null){
			StringBuffer keyBuffer;
			StringBuffer signKeyBuffer;
			List list;
			List signList = new ArrayList();
			Map configuredPidsMap = null;
			
			if(configuredPids != null && configuredPids.size() > 0){
				configuredPidsMap = new HashMap(configuredPids.size());
				for(String pid : (List<String>)configuredPids){
					configuredPidsMap.put(pid, true);
				}
			}else{
				configuredPidsMap = new HashMap(0);
			}
				
			for (int i = 0; i < browseServicesResults.size(); i++) {
				PartSearchService service = (PartSearchService) browseServicesResults.get(i);		

				keyBuffer = new StringBuffer(service.getProdBrandCode());
				signKeyBuffer = new StringBuffer(service.getProdBrandCode());
				signKeyBuffer.append("_").append(service.getProdSetCode());
				
				list = (List) nodesMap.get(keyBuffer.toString());	
				if (list == null) {
					list = new ArrayList();
					nodesMap.put(keyBuffer.toString(),list);
				}
				
				if(!signList.contains(signKeyBuffer.toString())){
					list.add(new String[]{service.getProdSetCode(),service.getProdSetCodeDscr()});
					signList.add(signKeyBuffer.toString());
				}
				
				keyBuffer.append("_").append(service.getProdSetCode());
				signKeyBuffer.append("_").append(service.getProdGrpCode());
				list = (List) nodesMap.get(keyBuffer.toString());	
				if (list == null) {
					list = new ArrayList();
					nodesMap.put(keyBuffer.toString(),list);
				}
				if(!signList.contains(signKeyBuffer.toString())){
					list.add(new String[]{service.getProdGrpCode(),service.getProdGrpCodeDscr()});
					signList.add(signKeyBuffer.toString());
				}
					
				keyBuffer.append("_").append(service.getProdGrpCode());
				signKeyBuffer.append("_").append(service.getProdId());
				list = (List) nodesMap.get(keyBuffer.toString());	
				if (list == null) {
					list = new ArrayList();
					nodesMap.put(keyBuffer.toString(),list);
				}
						
				if(!signList.contains(signKeyBuffer.toString())){
					list.add(new String[] { service.getProdId(),
							service.getProdIdDscr(),
							configuredPidsMap.get(service.getProdId()) != null? "0":"1" });
					signList.add(signKeyBuffer.toString());
				}
			}
			
			Iterator iterator = nodesMap.keySet().iterator();
			while(iterator.hasNext()){
				Collections.sort((List)nodesMap.get(iterator.next()),new Comparator(){
				    public int compare(Object o1,Object o2) {
				    	if(o1 instanceof String[]){
				    		return ((String[])o1)[1].compareTo(((String[])o2)[1]);
				    	}
				    	return 0;
				    }
				});
			}
		}
	}
	
	private List sortBrandsList() {
		if(brandsList != null){
			Collections.sort(brandsList,new Comparator(){
			    public int compare(Object o1,Object o2) {
			    	if(o1 instanceof String[]){
			    		if("OTHER".equals(((String[])o1)[0])){
							return 2147483647;
						}
			    		if("OTHER".equals(((String[])o2)[0])){
							return -2147483647;
						}
			    		return ((String[])o1)[1].compareTo(((String[])o2)[1]);
			    	}
			    	return 0;
			    }
			});
		}
		return brandsList;
	}

	public boolean isShowFindPart() {
		if (!ParamKeys.PARAM_QUOTE_FLAG_EMPTY.equals(getQuoteFlag())){
			showFindPart = false;
		}
		return showFindPart;
	}
	
	public Boolean  isAddNewMonthlySW(){
		if(addNewMonthlySWFlag !=null && addNewMonthlySWFlag.equals("true")){
			return true;
		}
	  return false;	

	}

	public String getAddNewMonthlySWFlag() {
		return addNewMonthlySWFlag;
	}

	public void setAddNewMonthlySWFlag(String addNewMonthlySWFlag) {
		this.addNewMonthlySWFlag = addNewMonthlySWFlag;
	}

	public String getConfigrtnId() {
		return configrtnId;
	}

	public void setConfigrtnId(String configrtnId) {
		this.configrtnId = configrtnId;
	}

	public String getConfigrtnActionCode() {
		return configrtnActionCode;
	}

	public void setConfigrtnActionCode(String configrtnActionCode) {
		this.configrtnActionCode = configrtnActionCode;
	}

	public String getChrgAgrmtNum() {
		return chrgAgrmtNum;
	}

	public void setChrgAgrmtNum(String chrgAgrmtNum) {
		this.chrgAgrmtNum = chrgAgrmtNum;
	}
		
	public String getOrgConfigId() {
		return orgConfigId;
	}

	public String getPUVLink(){
		  ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
		  String codeBase = appCtx.getConfigParameter(EAD4JBootstrapKeys.EAD4J_CODEBASE_PATH_KEY);
		  String configXMLFile = codeBase + "/" + appCtx.getConfigParameter("portal.settings.folder") + "/" + appCtx.getConfigParameter("taglib.generate.important.links");
		  String puvLink ="";
		  SAXBuilder saxBuilder =new SAXBuilder(false);
  	  try {
			Document doc =saxBuilder.build(new File(configXMLFile));
			Element root =doc.getRootElement();
			List<Element> importantLinks =root.getChildren("importantLinks");
			
			for(Element link :importantLinks){								
				if(link.getAttributeValue("id").equals("pvu")){					
					puvLink =link.getChild("importantLink").getChildText("href");
					break;
				}
			}			
		 } catch (Exception e) {
			LogContextFactory.singleton().getLogContext().error(this, "tag-important-links-config.xml file parse error - " + configXMLFile);
		 } 
		
		 return puvLink;
   }
}
