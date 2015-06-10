package com.ibm.dsw.quote.customer.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.domain.IndustryInd;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.customer.config.CustomerMessageKeys;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
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
public class DisplayCustCreateViewBean extends BaseViewBean {
    private static 
	transient List currency;
	transient List state;
	transient List communicationLanguage;
	transient List mediaLanguage;
	transient List industryIndicator;
    String country;
    String lob;
    String agrmtTypeCode;
    String quoteNum;
    String progMigrtnCode;
    boolean isPostalCodeRequired;
    boolean isVatNumRequired;
    int postalCodeMinLength;
    int postalCodeMaxLength;
    int vatNumMinLength;
    int vatNumMaxLength;
    String selectOne;
    
    String currencyCode;
    String custName;
    String address1;
    String address2;
    String city;
    String regionCode;
    String postalCode;
    String ibmCustNum;
    String vatNum;
    String industryIndCode;
    int companySize = -1;
    String firstName;
    String lastName;
    String phoneNum;
    String faxNum;
    String emailAddress;
    String commuLag;
    String mediaLag;
    int webCustId;
    String vatNumFieldLabel;
    boolean isNeedFillForState = false;
    private transient List countryList;
    private String cusCountry = null;
	private String endUserFlag = "0";
	boolean dsplyVatNumInputFlag = false;
	private String quoteAgrmtTypeCode;
	
    AgrmntTypeViewAdapter agrmntTypeView = null;
    
    public void collectResults(Parameters param) throws ViewBeanException {

        LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.collectResults(param);
        
        agrmntTypeView = new AgrmntTypeViewAdapter();
        agrmntTypeView.collectResultsForCustCreate(param);
        
        Customer customer = (Customer)param.getParameter(CustomerParamKeys.PARAM_CUSTOMER);
        if(customer !=null){
            currencyCode = customer.getCurrencyCode();
            custName = customer.getCustName();
            address1 = customer.getAddress1();
            address2 = customer.getInternalAddress();
            city = customer.getCity();
            regionCode = customer.getSapRegionCode();
            postalCode = customer.getPostalCode();
            ibmCustNum = customer.getIbmCustNum();
            vatNum = customer.getCustVatNum();
            industryIndCode = customer.getSapWwIsuCode();
            companySize = customer.getEmpTot();
            firstName = customer.getCntFirstName();
            lastName = customer.getCntLastName();
            phoneNum = customer.getCntPhoneNumFull();
            faxNum = customer.getCntFaxNumFull();
            emailAddress = customer.getCntEmailAdr();
            commuLag = customer.getIsoLangCode();
            mediaLag = customer.getUpgrLangCode();
            webCustId = customer.getWebCustId();
            agrmtTypeCode = customer.getWebCustTypeCode();
            this.quoteAgrmtTypeCode = (String)param.getParameter(CustomerParamKeys.PARAM_AGRMTTYPECODE);
        }
        
        this.setLob((String)param.getParameter(ParamKeys.PARAM_LINE_OF_BUSINESS));
        this.setCountry((String)param.getParameter(ParamKeys.PARAM_COUNTRY));
        this.setQuoteNum((String)param.getParameter(ParamKeys.PARAM_QUOTE_NUM));
        this.setProgMigrtnCode((String)param.getParameter(DraftQuoteParamKeys.PARAM_PROG_MIGRTN_CODE));
        if(this.isSSPType()){
	        this.setCusCountry((String)param.getParameter(ParamKeys.CUSTOMER_COUNTRY));
	        countryList = (List) param.getParameter(ParamKeys.PARAM_SIMPLE_OBJECT);
	        this.endUserFlag = ((String)param.getParameter(ParamKeys.PARAM_END_USER_FLAG_NAME));
        }
		//this.svpLevelList = AgreementTypeConfigFactory.singleton().getSVPLevels(agreementType, region, getCountry());
        if(QuoteConstants.IND.equalsIgnoreCase(country) || QuoteConstants.USA.equalsIgnoreCase(country) 
        		|| QuoteConstants.CAN.equalsIgnoreCase(country) || QuoteConstants.BRA.equalsIgnoreCase(country)){
        	isNeedFillForState = true;
        }
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        selectOne = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.SELECT_ONE);
        
        // Display VAT field label base on country - eBiz# DEVS-7WTB9U
//        if(country.equals("FRA")) {
//            //SIRET Number
//            vatNumFieldLabel = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, CustomerMessageKeys.VAT_FRA);
//        } else if (country.equals("NLD")) {
//            //KVK Number
//            vatNumFieldLabel = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, CustomerMessageKeys.VAT_NLD);
//        } else {
            //VAT registration number
            vatNumFieldLabel = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, CustomerMessageKeys.VAT);
//       }
        
        try {
            CacheProcess process = CacheProcessFactory.singleton().create();

            this.setCommunicationLanguage(process.getCommLangCodeList());
            this.setMediaLanguage(process.getMediaLangCodeList());
            this.setIndustryIndicator(process.getIndustryIndCodeList());
            Country countryObject = null;
            if(this.isSSPType() && !StringUtils.isBlank(this.cusCountry)){
            	countryObject = process.getCountryByCode3(cusCountry);
            	this.setState(process.getStateListAsCodeDescObj(cusCountry));
            }else{
            	countryObject = process.getCountryByCode3(country);
            	this.setState(process.getStateListAsCodeDescObj(country));
            }
            this.setCurrency(countryObject.getCurrencyList());
            this.setPostalCodeRequired(countryObject.getPostalCodeRequiredFlag());
            this.setPostalCodeMinLength(countryObject.getPostalCodeMinLength());
            this.setPostalCodeMaxLength(countryObject.getPostalCodeMaxLength());
            this.setVatNumRequired(countryObject.getVatExemptNumRequiredFlag());
            this.setVatNumMinLength(countryObject.getVatExemptNumMinLength());
            this.setVatNumMaxLength(countryObject.getVatExemptNumMaxLength());
            this.setDsplyVatNumInputFlag(countryObject.getDsplyVatNumInputFlag());
            
        } catch (QuoteException qe) {
            logContext.error(this, qe.getMessage());
            throw new ViewBeanException("error getting data from cache");
        }  
    }
    // used in PGS to get Offering types: Passport Advantage/Passport Advantage Express
	public Collection generateOfferingTypes() {
        Collection collection = new ArrayList();

        String csa_code = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_CSA);
		String csa_desc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_CSA_DESC);
        String pa_code = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PA);
		String pa_desc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PA_DESC);
		String pae_code = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PAE);
		String pae_desc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PAE_DESC);
		
		// CSRA || CSTA
		if ((CustomerConstants.LOB_PA.equals(getLob())
				&& CustomerConstants.AGRMNT_OPTION_RELATIONAL.equals(getAgrmtTypeCode()))
			 ||
			 (CustomerConstants.LOB_PAE.equals(getLob())
				&& CustomerConstants.AGRMNT_OPTION_TRANSACTIONAL.equals(getAgrmtTypeCode()))){
			collection.add(new SelectOptionImpl(csa_desc, csa_code, true));
		} else {
			collection.add(new SelectOptionImpl(csa_desc, csa_code, false));
		}
		
		// PA
        if (CustomerConstants.LOB_PA.equals(getLob()) && ("".equals(getAgrmtTypeCode()) || CustomerConstants.LOB_PA.equals(getAgrmtTypeCode()))){
            collection.add(new SelectOptionImpl(pa_desc, pa_code, true));
        } else {
            collection.add(new SelectOptionImpl(pa_desc, pa_code, false));
        }
        
        // PAE
        if (CustomerConstants.LOB_PAE.equals(getLob()) && ("".equals(getAgrmtTypeCode()) || "PAX".equals(getAgrmtTypeCode()))){
            collection.add(new SelectOptionImpl(pae_desc, pae_code, true));
        } else {
            collection.add(new SelectOptionImpl(pae_desc, pae_code, false));
        }
        
    	return collection;
    }
	
	// used in SQO to get Agreement types: Cloud Services Agreement/Passport Advantage/Passport Advantage Express
	public Collection generateAgreementTypes() {
        Collection collection = new ArrayList();

        String default_desc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_DEFAULT);
        
        String csa_code = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_CSA);
		String csa_desc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_CSA_DESC);
        String pa_code = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PA);
		String pa_desc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PA_DESC);
		String pae_code = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PAE);
		String pae_desc = ApplicationContextFactory.singleton().getApplicationContext().getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LOB_PAE_DESC);
		
		if(StringUtils.isNotBlank(getLob())){
			collection.add(new SelectOptionImpl(default_desc, "", false));
		}else{
			collection.add(new SelectOptionImpl(default_desc, "", true));
		}
		
		if ((CustomerConstants.LOB_PA.equals(getLob())
				&& CustomerConstants.AGRMNT_OPTION_RELATIONAL.equals(getAgrmtTypeCode()))
			 ||
			 (CustomerConstants.LOB_PAE.equals(getLob())
				&& CustomerConstants.AGRMNT_OPTION_TRANSACTIONAL.equals(getAgrmtTypeCode()))){
			collection.add(new SelectOptionImpl(csa_desc, csa_code, true));
		} else {
			collection.add(new SelectOptionImpl(csa_desc, csa_code, false));
		}
		
        if (CustomerConstants.LOB_PA.equals(getLob()) && ("".equals(getAgrmtTypeCode()) || CustomerConstants.LOB_PA.equals(getAgrmtTypeCode()))){
            collection.add(new SelectOptionImpl(pa_desc, pa_code, true));
        } else {
            collection.add(new SelectOptionImpl(pa_desc, pa_code, false));
        }

        if (CustomerConstants.LOB_PAE.equals(getLob()) && ("".equals(getAgrmtTypeCode()) || "PAX".equals(getAgrmtTypeCode()))){
            collection.add(new SelectOptionImpl(pae_desc, pae_code, true));
        } else {
            collection.add(new SelectOptionImpl(pae_desc, pae_code, false));
        }

    	return collection;
    }
	
    public String getI18NString(String baseName, String key){
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();

        String value = appCtx.getI18nValueAsString(baseName, locale, key);

        if (value == null || value.equals("")) {
            value = key;
        }

        return value;
    }
    
    public Collection generateCommLangOptions() {
        Collection collection = new ArrayList();
        Iterator itr = communicationLanguage.iterator();
        collection.add(new SelectOptionImpl(selectOne, "", true));
        while(itr.hasNext()) {
            CodeDescObj codeDescObj = (CodeDescObj)itr.next();
            if (getCommuLag().endsWith(codeDescObj.getCode())){
                collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), true));
            } else {
                collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), false));
            }
        }
    	return collection;
    }
    
    public Collection generateMediaLangOptions() {
        Collection collection = new ArrayList();
        Iterator itr = mediaLanguage.iterator();
        collection.add(new SelectOptionImpl(selectOne, "", true));
        while(itr.hasNext()) {
            CodeDescObj codeDescObj = (CodeDescObj)itr.next();
            if (getMediaLag().endsWith(codeDescObj.getCode())){
                collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), true));
            } else {
                collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), false));
            }
        }
    	return collection;
    }
    
    public Collection generateIndustryIndOptions() {
        Collection collection = new ArrayList();
        Iterator itr = industryIndicator.iterator();
        collection.add(new SelectOptionImpl(selectOne, "", true));
        while(itr.hasNext()) {
            IndustryInd industryInd = (IndustryInd)itr.next();
            List codeDescList = industryInd.getCodeDescs();
            Iterator itr2 = codeDescList.iterator();
            while(itr2.hasNext()) {
                if (getIndustryIndCode().endsWith(industryInd.getCode())){
                    collection.add(new SelectOptionImpl((String)itr2.next(), industryInd.getCode(), true));
                } else {
                    collection.add(new SelectOptionImpl((String)itr2.next(), industryInd.getCode(), false));
                }
            }
        }
    	return collection;
    }
    
    public Collection generateStateOptions() {
        Collection collection = new ArrayList();
        Iterator itr = state.iterator();
        collection.add(new SelectOptionImpl(selectOne, "", true));
        while(itr.hasNext()) {
            CodeDescObj codeDescObj = (CodeDescObj)itr.next();
            if (getRegionCode().endsWith(StringUtils.trimToEmpty(codeDescObj.getCode()))){
                collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), StringUtils.trimToEmpty(codeDescObj.getCode()), true));
            } else {
                collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), StringUtils.trimToEmpty(codeDescObj.getCode()), false));
            }
        }
    	return collection;
    }
    
    public Collection generateCurrencyOptions() {
        
        Collection collection = new ArrayList();
        HashMap mapCurrency = new HashMap();
        Iterator itr = currency.iterator();
        
        while (itr.hasNext()) {
            CodeDescObj codeDescObj = (CodeDescObj) itr.next();
            if (!mapCurrency.containsKey(codeDescObj.getCode())) {
                if (getCurrencyCode().endsWith(codeDescObj.getCode())) {
                    collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), true));
                } else {
                    collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), false));
                }
                mapCurrency.put(codeDescObj.getCode(), codeDescObj.getCodeDesc());
            }
        }
    	return collection;
    }
    
    
	public Collection generateCountryOptions() {

		Collection collection = new ArrayList();
		Iterator iter = countryList.iterator();

		/**
		 * Add default "Select one" option for the SSP Quote only.
		 */

		while (iter.hasNext()) {
			Country cntry = (Country) iter.next();
			collection.add(new SelectOptionImpl(cntry.getDesc(), cntry.getCode3(), cntry.getCode3().equalsIgnoreCase(
					country)));
		}

		return collection;
	}
	
	public boolean isSSPType(){
		return QuoteConstants.LOB_SSP.equals(this.getLob());
	}

    /**
     * @return Returns the communicationLanguage.
     */
    public List getCommunicationLanguage() {
        return communicationLanguage;
    }
    /**
     * @param communicationLanguage The communicationLanguage to set.
     */
    public void setCommunicationLanguage(List communicationLanguage) {
        this.communicationLanguage = communicationLanguage;
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
     * @return Returns the currency.
     */
    public List getCurrency() {
        return currency;
    }
    /**
     * @param currency The currency to set.
     */
    public void setCurrency(List currency) {
        this.currency = currency;
    }
    /**
     * @return Returns the industryIndicator.
     */
    public List getIndustryIndicator() {
        return industryIndicator;
    }
    /**
     * @param industryIndicator The industryIndicator to set.
     */
    public void setIndustryIndicator(List industryIndicator) {
        this.industryIndicator = industryIndicator;
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
     * @return Returns the mediaLanguage.
     */
    public List getMediaLanguage() {
        return mediaLanguage;
    }
    /**
     * @param mediaLanguage The mediaLanguage to set.
     */
    public void setMediaLanguage(List mediaLanguage) {
        this.mediaLanguage = mediaLanguage;
    }
    /**
     * @return Returns the state.
     */
    public List getState() {
        return state;
    }
    /**
     * @param state The state to set.
     */
    public void setState(List state) {
        this.state = state;
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
     * @return Returns the isPostalCodeRequired.
     */
    public boolean isPostalCodeRequired() {
        return isPostalCodeRequired;
    }
    /**
     * @param isPostalCodeRequired The isPostalCodeRequired to set.
     */
    public void setPostalCodeRequired(boolean isPostalCodeRequired) {
        this.isPostalCodeRequired = isPostalCodeRequired;
    }
    /**
     * @return Returns the isVatNumRequired.
     */
    public boolean isVatNumRequired() {
        return isVatNumRequired;
    }
    /**
     * @param isVatNumRequired The isVatNumRequired to set.
     */
    public void setVatNumRequired(boolean isVatNumRequired) {
        this.isVatNumRequired = isVatNumRequired;
    }
    /**
     * @return Returns the postalCodeMaxLength.
     */
    public int getPostalCodeMaxLength() {
        return postalCodeMaxLength;
    }
    /**
     * @param postalCodeMaxLength The postalCodeMaxLength to set.
     */
    public void setPostalCodeMaxLength(int postalCodeMaxLength) {
        this.postalCodeMaxLength = postalCodeMaxLength;
    }
    /**
     * @return Returns the postalCodeMinLength.
     */
    public int getPostalCodeMinLength() {
        return postalCodeMinLength;
    }
    /**
     * @param postalCodeMinLength The postalCodeMinLength to set.
     */
    public void setPostalCodeMinLength(int postalCodeMinLength) {
        this.postalCodeMinLength = postalCodeMinLength;
    }
    /**
     * @return Returns the vatNumMaxLength.
     */
    public int getVatNumMaxLength() {
        return vatNumMaxLength;
    }
    /**
     * @param vatNumMaxLength The vatNumMaxLength to set.
     */
    public void setVatNumMaxLength(int vatNumMaxLength) {
        this.vatNumMaxLength = vatNumMaxLength;
    }
    /**
     * @return Returns the vatNumMinLength.
     */
    public int getVatNumMinLength() {
        return vatNumMinLength;
    }
    /**
     * @param vatNumMinLength The vatNumMinLength to set.
     */
    public void setVatNumMinLength(int vatNumMinLength) {
        this.vatNumMinLength = vatNumMinLength;
    }
    public String getAddress1() {
        return StringUtils.stripToEmpty(address1);
    }
    public String getAddress2() {
        return StringUtils.stripToEmpty(address2);
    }
    public String getCity() {
        return StringUtils.stripToEmpty(city);
    }
    public String getCommuLag() {
        return StringUtils.stripToEmpty(commuLag);
    }
    public int getCompanySize() {
        return companySize;
    }
    public String getCurrencyCode() {
        return StringUtils.stripToEmpty(currencyCode);
    }
    public String getCustName() {
        return StringUtils.stripToEmpty(custName);
    }
    public String getEmailAddress() {
        return StringUtils.stripToEmpty(emailAddress);
    }
    public String getFaxNum() {
        return StringUtils.stripToEmpty(faxNum);
    }
    public String getFirstName() {
        return StringUtils.stripToEmpty(firstName);
    }
    public String getIbmCustNum() {
        return StringUtils.stripToEmpty(ibmCustNum);
    }
    public String getIndustryIndCode() {
        return StringUtils.stripToEmpty(industryIndCode);
    }
    public String getLastName() {
        return StringUtils.stripToEmpty(lastName);
    }
    public String getMediaLag() {
        return StringUtils.stripToEmpty(mediaLag);
    }
    public String getPhoneNum() {
        return StringUtils.stripToEmpty(phoneNum);
    }
    public String getPostalCode() {
        return StringUtils.stripToEmpty(postalCode);
    }
    public String getRegionCode() {
        return StringUtils.stripToEmpty(regionCode);
    }
    public String getSelectOne() {
        return StringUtils.stripToEmpty(selectOne);
    }
    public String getVatNum() {
        return StringUtils.stripToEmpty(vatNum);
    }
    public String getWebCustId() {
        return String.valueOf(webCustId);
    }
    public String getProgMigrtnCode() {
        return progMigrtnCode;
    }
    public void setProgMigrtnCode(String progMigrtnCode) {
        this.progMigrtnCode = progMigrtnCode;
    }

    public String getVatNumFieldLabel() {
        return  vatNumFieldLabel;
    }
    
    public AgrmntTypeViewAdapter getAgrmntTypeView() {
        return agrmntTypeView;
    }
    
    public boolean isDisplaySignatureMsg() {
        return agrmntTypeView.isDisplaySignatureMsg;
    }
    
    public String getCntryCode2() {
        return agrmntTypeView.getCntryCode2();
    }
    
    public boolean isNeedFillForState() {
		return isNeedFillForState;
	}

	public List getCountryList() {
		return countryList;
	}

	public void setCountryList(List countryList) {
		this.countryList = countryList;
	}

	public String getCusCountry() {
		return cusCountry;
	}

	public void setCusCountry(String cusCountry) {
		this.cusCountry = cusCountry;
	}
    
	public String getCusCountryForShow(){
		if(!StringUtils.isBlank(this.cusCountry)){
			return this.cusCountry;
		}else{
			return this.country;
		}
	}

	public String getEndUserFlag() {
		return endUserFlag;
	}

	public void setEndUserFlag(String endUserFlag) {
		this.endUserFlag = endUserFlag;
	}

	public boolean isDsplyVatNumInputFlag() {
		return dsplyVatNumInputFlag;
	}

	public void setDsplyVatNumInputFlag(boolean dsplyVatNumInputFlag) {
		this.dsplyVatNumInputFlag = dsplyVatNumInputFlag;
	}
	
	public String getAgrmtTypeCode() {
		return agrmtTypeCode;
	}

	public void setAgrmtTypeCode(String agrmtTypeCode) {
		this.agrmtTypeCode = agrmtTypeCode;
	}
	public String getQuoteAgrmtTypeCode() {
		return quoteAgrmtTypeCode;
	}
	public void setQuoteAgrmtTypeCode(String quoteAgrmtTypeCode) {
		this.quoteAgrmtTypeCode = quoteAgrmtTypeCode;
	}
	
}
