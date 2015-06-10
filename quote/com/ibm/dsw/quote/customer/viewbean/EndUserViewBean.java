/*
 * Created on Feb 22, 2007
 */
package com.ibm.dsw.quote.customer.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
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
 * @author: doris_yuen@us.ibm.com
 * 
 * date: Dec. 15, 2009
 */
public class EndUserViewBean extends BaseViewBean {
    
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
    private String cntryCode2;
    
    private transient List stateList;
    private transient List contractVariantList;
    
    private transient List countryList;
        
    public void collectResults(Parameters param) throws ViewBeanException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.collectResults(param);
        
        lob = (String)param.getParameter(ParamKeys.PARAM_LINE_OF_BUSINESS);
        country = (String)param.getParameter(ParamKeys.PARAM_COUNTRY);
        this.setLob(lob);
        this.setCountry(country);
        this.setQuoteNum((String)param.getParameter(ParamKeys.PARAM_QUOTE_NUM));
        this.setSiteNumber((String)param.getParameter(ParamKeys.PARAM_SITE_NUM));
        this.setAgreementNumber((String)param.getParameter(ParamKeys.PARAM_AGREEMENT_NUM));
        this.setFindAllCntryCusts((String)param.getParameter(ParamKeys.PARAM_FIND_ALL_CNTRY_CUSTS));
        this.setCustomerName((String)param.getParameter(ParamKeys.PARAM_CUST_NAME));
        this.setContractOption((String)param.getParameter(ParamKeys.PARAM_CONTRACT_OPTION));
        this.setAnniversary((String)param.getParameter(ParamKeys.PARAM_ANNIVERSARY));
        this.setFindActiveCusts((String)param.getParameter(ParamKeys.PARAM_FIND_ACTIVE_CUSTS));
        this.setState((String)param.getParameter(ParamKeys.PARAM_STATE));
        this.setProgMigrtnCode((String)param.getParameter(DraftQuoteParamKeys.PARAM_PROG_MIGRTN_CODE));
        
        searchFor = (String)param.getParameter(CustomerParamKeys.SEARCH_FOR);
        if (!CustomerConstants.SEARCH_FOR_PAYER.equalsIgnoreCase(searchFor))
            searchFor = CustomerConstants.SEARCH_FOR_CUSTOMER;
                        
        if (this.isSearchForPayer() || this.isSSPType()) {
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
                    || lob.equals(CustomerConstants.LOB_OEM) ) {
                setDisplayAgreementNumber(true);
            }
            if(lob.equals(CustomerConstants.LOB_PAUN) || lob.equals(CustomerConstants.LOB_PA) 
                    || lob.equals(CustomerConstants.LOB_PAE)) {
                setDisplayContractVariant(true);
                setDisplayAnniversary(true);
            } 
                       
        }
        
        this.isFindAllCntryCusts = CustomerConstants.CHECKBOX_CHECKED.equals(getFindAllCntryCusts());
        this.isFindAllCusts = CustomerConstants.ALL_0.equals(this.findActiveCusts);
        this.isFindActivePA = CustomerConstants.NUMBER_1.equals(this.findActiveCusts);
        this.isFindActivePAE = CustomerConstants.NUMBER_2.equals(this.findActiveCusts);
        this.isFindActiveFCT = CustomerConstants.NUMBER_3.equals(this.findActiveCusts);
        this.isFindActivePPSS = CustomerConstants.NUMBER_4.equals(this.findActiveCusts);
        this.isFindActiveOEM = CustomerConstants.NUMBER_5.equals(this.findActiveCusts);
        
    }
    
    public Collection generateContractVariantOptions() {
        
        Collection collection = new ArrayList();
        collection.add(new SelectOptionImpl("ALL", "%", true));
        
        Iterator itr = contractVariantList.iterator();
        while(itr.hasNext()) {
            CodeDescObj codeDescObj = (CodeDescObj)itr.next();
            collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), false));
        }
    	return collection;

    }
    
    public Collection generateAnniversaryOptions() {

        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        
        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LABEL_ALL);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.ALL_0, true));
		
		labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.MONTH_JAN);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.NUMBER_1, false));
		
		labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.MONTH_FEB);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.NUMBER_2, false));
		
		labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.MONTH_MAR);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.NUMBER_3, false));
		
		labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.MONTH_APR);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.NUMBER_4, false));
		
		labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.MONTH_MAY);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.NUMBER_5, false));
		
		labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.MONTH_JUN);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.NUMBER_6, false));
		
		labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.MONTH_JUL);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.NUMBER_7, false));
		
		labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.MONTH_AUG);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.NUMBER_8, false));
		
		labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.MONTH_SEP);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.NUMBER_9, false));
		
		labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.MONTH_OCT);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.NUMBER_10, false));
		
		labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.MONTH_NOV);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.NUMBER_11, false));
		
		labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.MONTH_DEC);
		collection.add(new SelectOptionImpl(labelString, CustomerConstants.NUMBER_12, false));
		    	
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
        
        /**
         * Add default "Select one" option for the SSP Quote only.
         */
        if(this.isSSPType()){
        	ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        	String selectOne = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                    MessageKeys.SELECT_ONE);
        	collection.add(new SelectOptionImpl(selectOne, "", true));
        }
        
        while (iter.hasNext()) {
            Country cntry = (Country) iter.next();
            collection.add(new SelectOptionImpl(cntry.getDesc(), cntry.getCode3(), cntry.getCode3().equalsIgnoreCase(country)));
        }
        
        return collection;
    }
    
    public boolean isPAPAEPAUN() {
        return (QuoteConstants.LOB_PA.equalsIgnoreCase(lob)
                || QuoteConstants.LOB_PAE.equalsIgnoreCase(lob)
                || QuoteConstants.LOB_PAUN.equalsIgnoreCase(lob));
    }
    
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
        return (lob.equals(CustomerConstants.LOB_FCT) && CustomerConstants.SEARCH_FOR_PAYER.equalsIgnoreCase(searchFor));
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
                || this.country.equals(CustomerConstants.COUNTRY_CANADA) || this.isSearchForPayer()) ;
    }
    
    public boolean isDisplayCountry() {
        return (this.isSearchForPayer()||this.isSSPType());
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

	public String getCntryCode2() {
		return cntryCode2;
	}

	public void setCntryCode2(String cntryCode2) {
		this.cntryCode2 = cntryCode2;
	}
	
	public boolean isSSPType(){
		return QuoteConstants.LOB_SSP.equals(this.getLob());
	}

}
