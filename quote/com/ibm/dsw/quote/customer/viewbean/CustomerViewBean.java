/*
 * Created on Feb 22, 2007
 */
package com.ibm.dsw.quote.customer.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.security.SecurityUtil;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.AgreementTypeConfigFactory;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.partner.config.PartnerMessageKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author Lavanya
 */
public class CustomerViewBean extends BaseViewBean {
    
    //display variables
    boolean isDisplayAgreementNumber = false;
    boolean isDisplayContractVariant = false;
    boolean isDisplayAnniversary = false;
    boolean isDisplayState = false;
    boolean isFindAllCntryCusts = false;
    boolean isFindAllCusts = false;
    boolean isFindActivePA = false;
    boolean isFindActivePAE = false;
    boolean isFindActiveFCT = false;
    boolean isFindActivePPSS = false;
    boolean isFindActiveOEM = false;
    boolean isFindActiveCSA = false;
    boolean isDisplayCreateCustTab = false;
    private boolean isFindSSPAll = false;

    
	//request parameters
    private String lob;
    private String country;
    private String quoteNum;
    private String siteNumber;
    private String agreementNumber;
    private String ibmCustomerNumber;
    private String customerName;
    private String contractOption;
    private String anniversary;
    private String findActiveCusts;
    private String searchFor;
    private String state;
    private String findAllCntryCusts;
    private String progMigrtnCode;
    private String countryCode2;
    
    private transient List stateList;
    private transient List agreementTypeList;
    private transient List contractVariantList;
    private transient List csaContractVariantList;
    private transient List anniversaryList;
    private transient List paunAgreementTypeList;
    
    private transient List countryList;
    
    //for FCT TO PA USE
    private  String migrationReqNum = "";
    private  String pageFrom="";
    private boolean isPageFromFCT2PAMigrationReq = false;
        
    public void collectResults(Parameters param) throws ViewBeanException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.collectResults(param);
        
        lob = (String)param.getParameter(ParamKeys.PARAM_LINE_OF_BUSINESS);
        country = (String)param.getParameter(ParamKeys.PARAM_COUNTRY);
        this.setLob(SecurityUtil.htmlEncode(lob));
        this.setCountry(SecurityUtil.htmlEncode(country));
        this.setQuoteNum(SecurityUtil.htmlEncode((String)param.getParameter(ParamKeys.PARAM_QUOTE_NUM)));
        this.setSiteNumber(SecurityUtil.htmlEncode((String)param.getParameter(ParamKeys.PARAM_SITE_NUM)));
        this.setAgreementNumber(SecurityUtil.htmlEncode((String)param.getParameter(ParamKeys.PARAM_AGREEMENT_NUM)));
        this.setFindAllCntryCusts(SecurityUtil.htmlEncode((String)param.getParameter(ParamKeys.PARAM_FIND_ALL_CNTRY_CUSTS)));
        this.setCustomerName(SecurityUtil.htmlEncode((String)param.getParameter(ParamKeys.PARAM_CUST_NAME)));
        this.setContractOption(SecurityUtil.htmlEncode((String)param.getParameter(ParamKeys.PARAM_CONTRACT_OPTION)));
        this.setAnniversary(SecurityUtil.htmlEncode((String)param.getParameter(ParamKeys.PARAM_ANNIVERSARY)));
        this.setFindActiveCusts(SecurityUtil.htmlEncode((String)param.getParameter(ParamKeys.PARAM_FIND_ACTIVE_CUSTS)));
        this.setState(SecurityUtil.htmlEncode((String)param.getParameter(ParamKeys.PARAM_STATE)));
        this.setProgMigrtnCode(SecurityUtil.htmlEncode((String)param.getParameter(DraftQuoteParamKeys.PARAM_PROG_MIGRTN_CODE)));
        this.setCountryCode2(SecurityUtil.htmlEncode((String)param.getParameter(ParamKeys.PARAM_COUNTRY_CODE2)));
        
        searchFor = SecurityUtil.htmlEncode((String)param.getParameter(CustomerParamKeys.SEARCH_FOR));
        if (!CustomerConstants.SEARCH_FOR_PAYER.equalsIgnoreCase(searchFor))
            searchFor = CustomerConstants.SEARCH_FOR_CUSTOMER;
                        
        if (this.isSearchForPayer()||QuoteConstants.LOB_SSP.equalsIgnoreCase(lob)) {
            countryList = (List) param.getParameter(ParamKeys.PARAM_SIMPLE_OBJECT);
        }
        
        try {
            CacheProcess process = CacheProcessFactory.singleton().create();
            this.setContractVariantList(process.getCtrctVariantList());
            this.setStateList(process.getStateListAsCodeDescObj(country));
        } catch (QuoteException qe) {
            logContext.error(this, qe.getMessage());
            throw new ViewBeanException("error getting data from cache");
        } 
        
        if (StringUtils.isBlank(lob)) {
            throw new ViewBeanException("failed to get lob in viewbean - lob is null or empty");
        } else {
            if(lob.equals(CustomerConstants.LOB_PAUN) || lob.equals(CustomerConstants.LOB_PA) 
                    || lob.equals(CustomerConstants.LOB_PAE) 
                    || (lob.equals(CustomerConstants.LOB_FCT) && !this.isSearchForPayer())
                    || lob.equals(CustomerConstants.LOB_OEM) || QuoteConstants.LOB_SSP.equalsIgnoreCase(lob) ) {
                setDisplayAgreementNumber(true);
            }
            if(lob.equals(CustomerConstants.LOB_PAUN) || lob.equals(CustomerConstants.LOB_PA) 
                    || lob.equals(CustomerConstants.LOB_PAE)) {
                setDisplayContractVariant(true);
                setDisplayAnniversary(true);
            } 
                       
        }
        
        migrationReqNum = (String) param.getParameter(ParamKeys.PARAM_MIGRATION_REQSTD_NUM);
        pageFrom = param.getParameterAsString(DraftQuoteParamKeys.PAGE_FROM);
        
        if (getPageFrom().equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)) {
			isPageFromFCT2PAMigrationReq = true;
		}
		
        
        if ( !isSearchForPayer()  && !isPageFromFCT2PAMigrationReq ) {
            isDisplayCreateCustTab = true;
        }

        this.isFindAllCntryCusts = CustomerConstants.CHECKBOX_CHECKED.equals(getFindAllCntryCusts());
        this.isFindAllCusts = CustomerConstants.ALL_0.equals(this.findActiveCusts);
        this.isFindActivePA = CustomerConstants.NUMBER_1.equals(this.findActiveCusts);
        this.isFindActivePAE = CustomerConstants.NUMBER_2.equals(this.findActiveCusts);
        this.isFindActiveFCT = CustomerConstants.NUMBER_3.equals(this.findActiveCusts);
        this.isFindActivePPSS = CustomerConstants.NUMBER_4.equals(this.findActiveCusts);
        this.isFindActiveOEM = CustomerConstants.NUMBER_5.equals(this.findActiveCusts);
        this.isFindSSPAll = CustomerConstants.ALL_0.equals(this.findActiveCusts);
        
        if(isPAPAEPAUN() && CustomerConstants.ACTIVE_CSA_CUSTS.equals(this.findActiveCusts))
        	this.isFindActiveCSA = true;
        
        List agreementTypeTempList =  new ArrayList();
        CodeDescObj codeDescObj = new CodeDescObj_jdbc( CustomerConstants.ALL_CUSTS,MessageKeys.LOB_ALL);
        agreementTypeTempList.add(codeDescObj);
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.ACTIVE_CSA_CUSTS,MessageKeys.LOB_CSA_DESC);
        agreementTypeTempList.add(codeDescObj);
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.ACTIVE_PA_CUSTS,MessageKeys.LOB_PA_DESC);
        agreementTypeTempList.add(codeDescObj);
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.ACTIVE_PAE_CUSTS,MessageKeys.LOB_PAE_DESC);
        agreementTypeTempList.add(codeDescObj);
        this.setAgreementTypeList(agreementTypeTempList);
        
        try {
        	this.setCsaContractVariantList(null);
            this.setPaunAgreementTypeList(null);
        } catch (QuoteException qe) {
            logContext.error(this, qe.getMessage());
            throw new ViewBeanException("error getting data from cache");
        } 
        
        List anniversaryTempList = new ArrayList();
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.NUMBER_1,MessageKeys.MONTH_JAN);
        anniversaryTempList.add(codeDescObj);
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.NUMBER_2,MessageKeys.MONTH_FEB);
        anniversaryTempList.add(codeDescObj);
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.NUMBER_3,MessageKeys.MONTH_MAR);
        anniversaryTempList.add(codeDescObj);
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.NUMBER_4,MessageKeys.MONTH_APR);
        anniversaryTempList.add(codeDescObj);
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.NUMBER_5,MessageKeys.MONTH_MAY);
        anniversaryTempList.add(codeDescObj);
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.NUMBER_6,MessageKeys.MONTH_JUN);
        anniversaryTempList.add(codeDescObj);
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.NUMBER_7,MessageKeys.MONTH_JUL);
        anniversaryTempList.add(codeDescObj);
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.NUMBER_8,MessageKeys.MONTH_AUG);
        anniversaryTempList.add(codeDescObj);
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.NUMBER_9,MessageKeys.MONTH_SEP);
        anniversaryTempList.add(codeDescObj);
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.NUMBER_10,MessageKeys.MONTH_OCT);
        anniversaryTempList.add(codeDescObj);
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.NUMBER_11,MessageKeys.MONTH_NOV);
        anniversaryTempList.add(codeDescObj);
        codeDescObj = new CodeDescObj_jdbc( CustomerConstants.NUMBER_12,MessageKeys.MONTH_DEC);
        anniversaryTempList.add(codeDescObj);
        
        this.setAnniversaryList(anniversaryTempList);
    }
    
    public Collection generateActiveCustOptions(){
    	Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String labelString = null;
		
		Iterator itr = agreementTypeList.iterator();
        while(itr.hasNext()) {
            CodeDescObj codeDescObj = (CodeDescObj)itr.next();
            labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, codeDescObj.getCodeDesc());
            if(CustomerConstants.ALL_CUSTS.equals(codeDescObj.getCode()))
            	collection.add(new SelectOptionImpl(labelString, codeDescObj.getCode(), true));
            else
            	collection.add(new SelectOptionImpl(labelString, codeDescObj.getCode(), false));
        }
    	return collection;
    }
    
    public Collection generateContractVariantOptions() {
        
        Collection collection = new ArrayList();
        collection.add(new SelectOptionImpl("ALL", "%", true));
        
        Iterator itr = null;
        if(CustomerConstants.ALL_CUSTS.equals(findActiveCusts)){
        	itr = paunAgreementTypeList.iterator();
        }else{
        	itr = contractVariantList.iterator();
        }
        while(itr.hasNext()) {
            CodeDescObj codeDescObj = (CodeDescObj)itr.next();
            collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), false));
        }
    	return collection;

    }
    
    public Collection generateAllContractVariantOptions() {
    	Collection collection = new ArrayList();
    	collection.add(new SelectOptionImpl("ALL", "%", true));
    	collection.add(new SelectOptionImpl("Not available", "", true));
        
        collection.addAll(this.generateContractVariantOptions());
        collection.addAll(this.generateCsaContractVariantOptions());
        
        return collection;
    }
    
    public Collection generateCsaContractVariantOptions() {
    	Collection collection = new ArrayList();
    	//collection.add(new SelectOptionImpl("ALL", "%", true));
        Iterator itr = csaContractVariantList.iterator();
        while(itr.hasNext()) {
            CodeDescObj codeDescObj = (CodeDescObj)itr.next();
            collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), false));
        }
    	return collection;
    }
    
    public Collection generateAllAnniversaryOptions() {

        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        
        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LABEL_ALL);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.ALL_0, true));
		collection.add(new SelectOptionImpl("Not available", "", true));
		
		Iterator itr = anniversaryList.iterator();
        while(itr.hasNext()) {
            CodeDescObj codeDescObj = (CodeDescObj)itr.next();
            labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, codeDescObj.getCodeDesc());
            collection.add(new SelectOptionImpl(labelString, codeDescObj.getCode(), false));
        }
    	return collection;
    
    }
    
    public Collection generateAnniversaryOptions() {

        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        
        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LABEL_ALL);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.ALL_0, true));
		
		Iterator itr = anniversaryList.iterator();
        while(itr.hasNext()) {
            CodeDescObj codeDescObj = (CodeDescObj)itr.next();
            labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, codeDescObj.getCodeDesc());
            collection.add(new SelectOptionImpl(labelString, codeDescObj.getCode(), false));
        }
    	return collection;
    
    }
    
    public Collection generateStateOptions() {
        Collection collection = new ArrayList();
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();

        if (CustomerConstants.COUNTRY_USA.equalsIgnoreCase(country)
                || CustomerConstants.COUNTRY_CANADA.equalsIgnoreCase(country)) {

            String selectOne = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                    MessageKeys.SELECT_ONE);
            Iterator itr = stateList.iterator();
            collection.add(new SelectOptionImpl(selectOne, "", true));

            while (itr.hasNext()) {
                CodeDescObj codeDescObj = (CodeDescObj) itr.next();
                collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), StringUtils.trimToEmpty(codeDescObj
                        .getCode()), false));
            }
        } else {

            String notAvailable = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                    PartnerMessageKeys.MSG_NOT_AVAILABLE);
            collection.add(new SelectOptionImpl(notAvailable, "", true));

        }
        return collection;
    }
    
    public Collection generateCountryOptions() {
        
        Collection collection = new ArrayList();
        Iterator iter = countryList.iterator();
        
        while (iter.hasNext()) {
            Country cntry = (Country) iter.next();
            collection.add(new SelectOptionImpl(cntry.getDesc(), cntry.getCode3(), cntry.getCode3().equalsIgnoreCase(country)));
        }
        
        return collection;
    }
    
    public boolean isPAPAEPAUN() {
        return isPA() || isPAE() || isPAUN();
    }
    
    public boolean isPA()  { return isLobType(CustomerConstants.LOB_PA); }
    public boolean isPAE()  { return isLobType(CustomerConstants.LOB_PAE); }
    public boolean isPAUN()  { return isLobType(CustomerConstants.LOB_PAUN); }
    public boolean isFCT() { return isLobType(CustomerConstants.LOB_FCT); }
    public boolean isSSPType() { return isLobType(CustomerConstants.LOB_SSP); }
    
    private boolean isLobType(String lobType) { return lobType.equalsIgnoreCase(getLob() ); }
    
    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return country;
    }
    /**
     * @param country The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }
    /**
     * @return Returns the lob.
     */
    public String getLob() {
        return lob;
    }
    /**
     * @param lob The lob to set.
     */
    public void setLob(String lob) {
        this.lob = lob;
    }
    /**
     * @return Returns the quoteNum.
     */
    public String getQuoteNum() {
        return quoteNum;
    }
    /**
     * @param quoteNum The quoteNum to set.
     */
    public void setQuoteNum(String quoteNum) {
        this.quoteNum = quoteNum;
    }
    /**
     * @return Returns the agreementNumber.
     */
    public String getAgreementNumber() {
        return agreementNumber;
    }
    /**
     * @param agreementNumber The agreementNumber to set.
     */
    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber;
    }
    /**
     * @return Returns the anniversary.
     */
    public String getAnniversary() {
        return anniversary;
    }
    /**
     * @param anniversary The anniversary to set.
     */
    public void setAnniversary(String anniversary) {
        this.anniversary = anniversary;
    }
    /**
     * @return Returns the contractOption.
     */
    public String getContractOption() {
        return contractOption;
    }
    /**
     * @param contractOption The contractOption to set.
     */
    public void setContractOption(String contractOption) {
        this.contractOption = contractOption;
    }
    /**
     * @return Returns the customerName.
     */
    public String getCustomerName() {
        return customerName;
    }
    /**
     * @param customerName The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    /**
     * @return Returns the findActiveCusts.
     */
    public String getFindActiveCusts() {
        return findActiveCusts;
    }
    /**
     * @param findActiveCusts The findActiveCusts to set.
     */
    public void setFindActiveCusts(String findActiveCusts) {
        this.findActiveCusts = findActiveCusts;
    }
    /**
     * @return Returns the ibmCustomerNumber.
     */
    public String getIbmCustomerNumber() {
        return ibmCustomerNumber;
    }
    /**
     * @param ibmCustomerNumber The ibmCustomerNumber to set.
     */
    public void setIbmCustomerNumber(String ibmCustomerNumber) {
        this.ibmCustomerNumber = ibmCustomerNumber;
    }
    /**
     * @return Returns the siteNumber.
     */
    public String getSiteNumber() {
        return siteNumber;
    }
    /**
     * @param siteNumber The siteNumber to set.
     */
    public void setSiteNumber(String siteNumber) {
        this.siteNumber = siteNumber;
    }
    /**
     * @return Returns the isDisplayAgreementNumber.
     */
    public boolean isDisplayAgreementNumber() {
        return isDisplayAgreementNumber;
    }
    /**
     * @param isDisplayAgreementNumber The isDisplayAgreementNumber to set.
     */
    public void setDisplayAgreementNumber(boolean isDisplayAgreementNumber) {
        this.isDisplayAgreementNumber = isDisplayAgreementNumber;
    }
    /**
     * @return Returns the isDisplayAnniversary.
     */
    public boolean isDisplayAnniversary() {
        return isDisplayAnniversary;
    }
    /**
     * @param isDisplayAnniversary The isDisplayAnniversary to set.
     */
    public void setDisplayAnniversary(boolean isDisplayAnniversary) {
        this.isDisplayAnniversary = isDisplayAnniversary;
    }
    /**
     * @return Returns the isDisplayContractVariant.
     */
    public boolean isDisplayContractVariant() {
        return isDisplayContractVariant;
    }
    /**
     * @param isDisplayContractVariant The isDisplayContractVariant to set.
     */
    public void setDisplayContractVariant(boolean isDisplayContractVariant) {
        this.isDisplayContractVariant = isDisplayContractVariant;
    }
    /**
     * @return Returns the contractVariantList.
     */
    public List getContractVariantList() {
        return contractVariantList;
    }
    /**
     * @param contractVariantList The contractVariantList to set.
     */
    public void setContractVariantList(List contractVariantList) {
        this.contractVariantList = contractVariantList;
    }
    /**
     * @return Returns the searchFor.
     */
    public String getSearchFor() {
        return searchFor;
    }
    
    public boolean isSearchForPayer() {
        return CustomerConstants.LOB_FCT.equals(lob) && CustomerConstants.SEARCH_FOR_PAYER.equalsIgnoreCase(searchFor);
    }
        
    /**
     * @return Returns the stateList.
     */
    public List getStateList() {
        return stateList;
    }
    /**
     * @param stateList The stateList to set.
     */
    public void setStateList(List stateList) {
        this.stateList = stateList;
    }
        
    /**
     * @return Returns the isFindActivePA.
     */
    public boolean isFindActivePA() {
        return isFindActivePA;
    }
    /**
     * @return Returns the isFindActivePAE.
     */
    public boolean isFindActivePAE() {
        return isFindActivePAE;
    }
    /**
     * @return Returns the isFindAllCusts.
     */
    public boolean isFindAllCusts() {
        return isFindAllCusts;
    }
    /**
     * @return Returns the isFindActiveFCT.
     */
    public boolean isFindActiveFCT() {
        return isFindActiveFCT;
    }
    /**
     * @return Returns the isFindActivePPSS.
     */
    public boolean isFindActivePPSS() {
        return isFindActivePPSS;
    }
    /**
     * @return Returns the isFindActiveOEM.
     */
    public boolean isFindActiveOEM() {
        return isFindActiveOEM;
    }
    /**
     * @return Returns the state.
     */
    public String getState() {
        return state;
    }
    /**
     * @param state The state to set.
     */
    public void setState(String state) {
        this.state = state;
    }
    /**
     * @return Returns the isFindAllCntryCusts.
     */
    public boolean isFindAllCntryCusts() {
        return isFindAllCntryCusts;
    }
    
    
    public boolean isPageFromFCT2PAMigrationReq() {
		return isPageFromFCT2PAMigrationReq;
	}

	public void setPageFromFCT2PAMigrationReq(boolean isPageFromFCT2PAMigrationReq) {
		this.isPageFromFCT2PAMigrationReq = isPageFromFCT2PAMigrationReq;
	}

	/**
     * @return Returns the findAllCntryCusts.
     */
    public String getFindAllCntryCusts() {
        return findAllCntryCusts;
    }
    /**
     * @param findAllCntryCusts The findAllCntryCusts to set.
     */
    public void setFindAllCntryCusts(String findAllCntryCusts) {
        this.findAllCntryCusts = findAllCntryCusts;
    }
    /**
     * @return Returns the isDisplayState.
     */
    public boolean isDisplayState() {
        return (this.country.equals(CustomerConstants.COUNTRY_USA)
                || this.country.equals(CustomerConstants.COUNTRY_CANADA) || this.isSearchForPayer());
    }
    
    public boolean isDisplayCountry() {
        return (this.isSearchForPayer());
    }
    
    public List getCountryList() {
        return countryList;
    }
    
    public void setCountryList(List countryList) {
        this.countryList = countryList;
    }
    
    public String getProgMigrtnCode() {
        return progMigrtnCode;
    }
    
    public void setProgMigrtnCode(String progMigrtnCode) {
        this.progMigrtnCode = progMigrtnCode;
    }
    
    public boolean isDisplayCreateCustTab() {
        return isDisplayCreateCustTab;
    }
    /**
     * @return Returns the countryCode2.
     */
    public String getCountryCode2() {
        return countryCode2;
    }
    /**
     * @param countryCode2 The countryCode2 to set.
     */
    public void setCountryCode2(String countryCode2) {
        this.countryCode2 = countryCode2;
    }

	public String getMigrationReqNum() {
		return (migrationReqNum == null ? "" : migrationReqNum);
	}

	public void setMigrationReqNum(String migrationReqNum) {
		this.migrationReqNum = migrationReqNum;
	}

	public String getPageFrom() {
		return (pageFrom == null ? "" : pageFrom);
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}
	public String getRtnToFCT2PACustPartnerURL() {
		String returnToFCT2PACustPartnerURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_FCT2PA_CUST_PARTNER);
		StringBuffer url = new StringBuffer();
		HtmlUtil.addURLParam(url, ParamKeys.PARAM_MIGRATION_REQSTD_NUM, StringHelper.fillString(getMigrationReqNum()));
		HtmlUtil.addURLParam(url, DraftQuoteParamKeys.PAGE_FROM, StringHelper.fillString(getPageFrom()));
		returnToFCT2PACustPartnerURL+=url.toString();
		return returnToFCT2PACustPartnerURL;
	}
    

	 
	 public boolean isFindSSPAll() {
	        return isFindSSPAll;
	    }
	/**
	 * 
	 * @return Returns the active customer
	 */
    public String getActiveCustOption(){
    	if(StringUtils.isBlank(findActiveCusts)){
    		if(CustomerConstants.ALL_CUSTS.equals(findActiveCusts))return CustomerConstants.ALL_CUSTS;
    		if(CustomerConstants.ACTIVE_PA_CUSTS.equals(findActiveCusts))return CustomerConstants.ACTIVE_PA_CUSTS;
    		if(CustomerConstants.ACTIVE_PAE_CUSTS.equals(findActiveCusts))return CustomerConstants.ACTIVE_PAE_CUSTS;
    		if(CustomerConstants.ACTIVE_CSA_CUSTS.equals(findActiveCusts))return CustomerConstants.ACTIVE_CSA_CUSTS;
    	}
    	return CustomerConstants.ALL_CUSTS;
    }

	public boolean isFindActiveCSA() {
		return isFindActiveCSA;
	}

	public void setCsaContractVariantList(List csaContractVariantList) throws QuoteException {
		if(csaContractVariantList == null){
			this.csaContractVariantList = AgreementTypeConfigFactory.singleton().getAgrmntOptionList();
		}else{
			this.csaContractVariantList = csaContractVariantList;
		}
	}

	public void setAnniversaryList(List anniversaryList) {
		this.anniversaryList = anniversaryList;
	}

	public List getCsaContractVariantList() {
		return csaContractVariantList;
	}

	public List getAnniversaryList() {
		return anniversaryList;
	}

	public List getAgreementTypeList() {
		return agreementTypeList;
	}

	public void setAgreementTypeList(List agreementTypeList) {
		this.agreementTypeList = agreementTypeList;
	}

	public List getPaunAgreementTypeList() {
		return paunAgreementTypeList;
	}

	public void setPaunAgreementTypeList(List paunAgreementTypeList) throws QuoteException {
		if(paunAgreementTypeList == null){
			this.paunAgreementTypeList = AgreementTypeConfigFactory.singleton().getPAUNAgrmntOptionList();
		}else
			this.paunAgreementTypeList = paunAgreementTypeList;
	}
    
}
