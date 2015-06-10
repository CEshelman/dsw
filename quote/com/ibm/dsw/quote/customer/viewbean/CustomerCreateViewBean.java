/*
 * Created on Mar 13, 2007
 */
package com.ibm.dsw.quote.customer.viewbean;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.customer.config.CustomerActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author Lavanya
 */
public class CustomerCreateViewBean extends BaseViewBean {
    
    private String country;
    private String lob;
    private String quoteNum;
    private String currency;
    private String companyName;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private String vatNum;
    private String industryIndicator;
    private String companySize; //value in db is int
    private String cntFirstName;
    private String cntLastName;
    private String cntPhoneNum;
    private String cntFaxNum;
    private String cntEmail;
    private String commLanguage;
    private String mediaLanguage;
    private String webCustId; //value in db is int
    private String customerNumber;
    private String sapContractNum;
    private String tempAccessNum;
    private String sapContactId; //value in db is int
    private String webCustTypeCode;
    private String sapIntlPhoneNum;
    private String mktgEmailFlag;
    private String webCustStatCode;
    private String sapCntPrtnrFuncCode;
    private String SapContractVariantCode;
    private String agreementType;
    private String transSVPLevel;
    private String govSiteType;
    private String agreementNumber;
    private String isAddiSiteGovTypeDisplay;
    private String endUserFlag;    
    private transient List customers = null;

    
    public void collectResults(Parameters param) throws ViewBeanException {
        super.collectResults(param);
        
        LogContext logCtx = LogContextFactory.singleton().getLogContext();
        
        SearchResultList resultList = (SearchResultList) param.getParameter(ParamKeys.PARAM_SIMPLE_OBJECT);
        if (resultList != null) {
            customers = resultList.getResultList();
            
            if(logCtx instanceof QuoteLogContextLog4JImpl){
    			if(((QuoteLogContextLog4JImpl)logCtx).isDebug(this)){		
    				 logCtx.debug(this, "getResultList is not null" );
    			}
    		}                  
        } else {
        	if(logCtx instanceof QuoteLogContextLog4JImpl){
    			if(((QuoteLogContextLog4JImpl)logCtx).isDebug(this)){		
    				logCtx.debug(this, "getResultList is null" );
    			}
    		} 
        }
        
        this.setLob((String)param.getParameter(ParamKeys.PARAM_LINE_OF_BUSINESS));
        
        this.setEndUserFlag(CustomerConstants.END_USER_FLAG);
        this.setCountry((String)param.getParameter(ParamKeys.PARAM_COUNTRY));
        this.setQuoteNum((String)param.getParameter(ParamKeys.PARAM_QUOTE_NUM));
        
        this.setCurrency((String)param.getParameter(ParamKeys.PARAM_CURRENCY));
        this.setCompanyName((String)param.getParameter(ParamKeys.PARAM_COMPANY_NAME));
        this.setAddress1((String)param.getParameter(ParamKeys.PARAM_ADDRESS1));
        this.setAddress2((String)param.getParameter(ParamKeys.PARAM_ADDRESS2));
        this.setCity((String)param.getParameter(ParamKeys.PARAM_CITY));
        this.setState((String)param.getParameter(ParamKeys.PARAM_STATE));
        this.setPostalCode((String)param.getParameter(ParamKeys.PARAM_POSTAL_CODE));
        this.setVatNum((String)param.getParameter(ParamKeys.PARAM_VAT_NUM));
        this.setIndustryIndicator((String)param.getParameter(ParamKeys.PARAM_INDUSTRY_IND));
        this.setCompanySize((String)param.getParameter(ParamKeys.PARAM_COMPANY_SIZE));
        this.setCntFirstName((String)param.getParameter(ParamKeys.PARAM_CNT_FNAME));
        this.setCntLastName((String)param.getParameter(ParamKeys.PARAM_CNT_LNAME));
        this.setCntPhoneNum((String)param.getParameter(ParamKeys.PARAM_CNT_PHONE));
        this.setCntFaxNum((String)param.getParameter(ParamKeys.PARAM_CNT_FAX));
        this.setCntEmail((String)param.getParameter(ParamKeys.PARAM_CNT_EMAIL));
        this.setCommLanguage((String)param.getParameter(ParamKeys.PARAM_COMM_LANGUAGE));
        this.setMediaLanguage((String)param.getParameter(ParamKeys.PARAM_MEDIA_LANGUAGE));
        this.setWebCustId((String)param.getParameter(ParamKeys.PARAM_WEBCUST_ID));
        this.setCustomerNumber((String)param.getParameter(ParamKeys.PARAM_CUST_NUM));
        this.setSapContractNum((String)param.getParameter(ParamKeys.PARAM_SAP_CONTRACT_NUM));
        this.setTempAccessNum((String)param.getParameter(ParamKeys.PARAM_TEMP_ACCESS_NUM));
        this.setSapContactId((String)param.getParameter(ParamKeys.PARAM_SAP_CONTACT_ID));
        this.setWebCustTypeCode((String)param.getParameter(ParamKeys.PARAM_WEBCUST_TYPE_CODE));
        this.setSapIntlPhoneNum((String)param.getParameter(ParamKeys.PARAM_SAP_INTL_PHONE));
        this.setMktgEmailFlag((String)param.getParameter(ParamKeys.PARAM_MKTG_EMAIL_FLAG));
        this.setWebCustStatCode((String)param.getParameter(ParamKeys.PARAM_WEBCUST_STAT_CODE));
        this.setSapCntPrtnrFuncCode((String)param.getParameter(ParamKeys.PARAM_SAP_CNT_PRTNR_FUNC_CODE));
        this.setAgreementType((String)param.getParameter(ParamKeys.PARAM_AGREEMENT_TYPE));
        this.setTransSVPLevel((String)param.getParameter(ParamKeys.PARAM_TRANS_SVP_LEVEL));
        this.setGovSiteType((String)param.getParameter(ParamKeys.PARAM_GOV_SITE_TYPE));
        this.setAgreementNumber((String)param.getParameter(ParamKeys.PARAM_AGREEMENT_NUM));
        this.setIsAddiSiteGovTypeDisplay((String)param.getParameter(DraftQuoteParamKeys.PARAM_ADDI_SITE_GOV_TYPE_DISPLAY));
    }

    /**
     * @return Returns the address1.
     */
    public String getAddress1() {
        return address1;
    }
    /**
     * @param address1 The address1 to set.
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    /**
     * @return Returns the address2.
     */
    public String getAddress2() {
        return address2;
    }
    /**
     * @param address2 The address2 to set.
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    /**
     * @return Returns the city.
     */
    public String getCity() {
        return city;
    }
    /**
     * @param city The city to set.
     */
    public void setCity(String city) {
        this.city = city;
    }
    /**
     * @return Returns the cntEmail.
     */
    public String getCntEmail() {
        return cntEmail;
    }
    /**
     * @param cntEmail The cntEmail to set.
     */
    public void setCntEmail(String cntEmail) {
        this.cntEmail = cntEmail;
    }
    /**
     * @return Returns the cntFaxNum.
     */
    public String getCntFaxNum() {
        return cntFaxNum;
    }
    /**
     * @param cntFaxNum The cntFaxNum to set.
     */
    public void setCntFaxNum(String cntFaxNum) {
        this.cntFaxNum = cntFaxNum;
    }
    /**
     * @return Returns the cntFirstName.
     */
    public String getCntFirstName() {
        return cntFirstName;
    }
    /**
     * @param cntFirstName The cntFirstName to set.
     */
    public void setCntFirstName(String cntFirstName) {
        this.cntFirstName = cntFirstName;
    }
    /**
     * @return Returns the cntLastName.
     */
    public String getCntLastName() {
        return cntLastName;
    }
    /**
     * @param cntLastName The cntLastName to set.
     */
    public void setCntLastName(String cntLastName) {
        this.cntLastName = cntLastName;
    }
    /**
     * @return Returns the cntPhoneNum.
     */
    public String getCntPhoneNum() {
        return cntPhoneNum;
    }
    /**
     * @param cntPhoneNum The cntPhoneNum to set.
     */
    public void setCntPhoneNum(String cntPhoneNum) {
        this.cntPhoneNum = cntPhoneNum;
    }
    /**
     * @return Returns the commLanguage.
     */
    public String getCommLanguage() {
        return commLanguage;
    }
    /**
     * @param commLanguage The commLanguage to set.
     */
    public void setCommLanguage(String commLanguage) {
        this.commLanguage = commLanguage;
    }
    /**
     * @return Returns the companyName.
     */
    public String getCompanyName() {
        return companyName;
    }
    /**
     * @param companyName The companyName to set.
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    /**
     * @return Returns the companySize.
     */
    public String getCompanySize() {
        return companySize;
    }
    /**
     * @param companySize The companySize to set.
     */
    public void setCompanySize(String companySize) {
        this.companySize = companySize;
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
    public String getCurrency() {
        return currency;
    }
    /**
     * @param currency The currency to set.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    /**
     * @return Returns the industryIndicator.
     */
    public String getIndustryIndicator() {
        return industryIndicator;
    }
    /**
     * @param industryIndicator The industryIndicator to set.
     */
    public void setIndustryIndicator(String industryIndicator) {
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
    public String getMediaLanguage() {
        return mediaLanguage;
    }
    /**
     * @param mediaLanguage The mediaLanguage to set.
     */
    public void setMediaLanguage(String mediaLanguage) {
        this.mediaLanguage = mediaLanguage;
    }
    /**
     * @return Returns the postalCode.
     */
    public String getPostalCode() {
        return postalCode;
    }
    /**
     * @param postalCode The postalCode to set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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
     * @return Returns the vatNum.
     */
    public String getVatNum() {
        return vatNum;
    }
    /**
     * @param vatNum The vatNum to set.
     */
    public void setVatNum(String vatNum) {
        this.vatNum = vatNum;
    }
    /**
     * @return Returns the customerNumber.
     */
    public String getCustomerNumber() {
        return customerNumber;
    }
    /**
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }
    /**
     * @return Returns the mktgEmailFlag.
     */
    public String getMktgEmailFlag() {
        return mktgEmailFlag;
    }
    /**
     * @param mktgEmailFlag The mktgEmailFlag to set.
     */
    public void setMktgEmailFlag(String mktgEmailFlag) {
        this.mktgEmailFlag = mktgEmailFlag;
    }
    /**
     * @return Returns the sapContactId.
     */
    public String getSapContactId() {
        return sapContactId;
    }
    /**
     * @param sapContactId The sapContactId to set.
     */
    public void setSapContactId(String sapContactId) {
        this.sapContactId = sapContactId;
    }
    /**
     * @return Returns the sapContractNum.
     */
    public String getSapContractNum() {
        return sapContractNum;
    }
    /**
     * @param sapContractNum The sapContractNum to set.
     */
    public void setSapContractNum(String sapContractNum) {
        this.sapContractNum = sapContractNum;
    }
    /**
     * @return Returns the sapIntlPhoneNum.
     */
    public String getSapIntlPhoneNum() {
        return sapIntlPhoneNum;
    }
    /**
     * @param sapIntlPhoneNum The sapIntlPhoneNum to set.
     */
    public void setSapIntlPhoneNum(String sapIntlPhoneNum) {
        this.sapIntlPhoneNum = sapIntlPhoneNum;
    }
    /**
     * @return Returns the tempAccessNum.
     */
    public String getTempAccessNum() {
        return tempAccessNum;
    }
    /**
     * @param tempAccessNum The tempAccessNum to set.
     */
    public void setTempAccessNum(String tempAccessNum) {
        this.tempAccessNum = tempAccessNum;
    }
    /**
     * @return Returns the webCustId.
     */
    public String getWebCustId() {
        return webCustId;
    }
    /**
     * @param webCustId The webCustId to set.
     */
    public void setWebCustId(String webCustId) {
        this.webCustId = webCustId;
    }
    /**
     * @return Returns the webCustStatCode.
     */
    public String getWebCustStatCode() {
        return webCustStatCode;
    }
    /**
     * @param webCustStatCode The webCustStatCode to set.
     */
    public void setWebCustStatCode(String webCustStatCode) {
        this.webCustStatCode = webCustStatCode;
    }
    /**
     * @return Returns the webCustTypeCode.
     */
    public String getWebCustTypeCode() {
        return webCustTypeCode;
    }
    /**
     * @param webCustTypeCode The webCustTypeCode to set.
     */
    public void setWebCustTypeCode(String webCustTypeCode) {
        this.webCustTypeCode = webCustTypeCode;
    }
    
    /**
     * @return Returns the customers.
     */
    public List getCustomers() {
        return customers;
    }

    /**
     * @return Returns the sapContractVariantCode.
     */
    public String getSapContractVariantCode() {
        return SapContractVariantCode;
    }
    /**
     * @param sapContractVariantCode The sapContractVariantCode to set.
     */
    public void setSapContractVariantCode(String sapContractVariantCode) {
        SapContractVariantCode = sapContractVariantCode;
    }
        
    public String getSelectCustURL(String customerNum, String sapContractNum , String currency, String endUserFlag) {
                
        StringBuffer url = new StringBuffer();
        String acionURL = HtmlUtil.getURLForAction(CustomerActionKeys.SELECT_CUSTOMER);
        url.append(acionURL);
        if (StringUtils.isNotBlank(customerNum)) {
            HtmlUtil.addURLParam(url, ParamKeys.PARAM_SITE_NUM, customerNum);
        }
        if (StringUtils.isNotBlank(sapContractNum)) {
            HtmlUtil.addURLParam(url, ParamKeys.PARAM_AGREEMENT_NUM, sapContractNum);
        }
        
        if (StringUtils.isNotBlank(currency)) {
            HtmlUtil.addURLParam(url, ParamKeys.PARAM_CURRENCY, currency);
        }
        
        if(StringUtils.isNotBlank(endUserFlag)){
        	HtmlUtil.addURLParam(url, ParamKeys.PARAM_END_USER_FLAG_NAME, endUserFlag);
        }
        
        return url.toString();
    }
        
    /**
     * @return Returns the sapCntPrtnrFuncCode.
     */
    public String getSapCntPrtnrFuncCode() {
        return sapCntPrtnrFuncCode;
    }
    /**
     * @param sapCntPrtnrFuncCode The sapCntPrtnrFuncCode to set.
     */
    public void setSapCntPrtnrFuncCode(String sapCntPrtnrFuncCode) {
        this.sapCntPrtnrFuncCode = sapCntPrtnrFuncCode;
    }
    
    public String getAgreementType() {
        return agreementType;
    }
    
    public void setAgreementType(String agreementType) {
        this.agreementType = agreementType;
    }
    
    public String getGovSiteType() {
        return govSiteType;
    }
    
    public void setGovSiteType(String govSiteType) {
        this.govSiteType = govSiteType;
    }
    
    public String getTransSVPLevel() {
        return transSVPLevel;
    }
    
    public void setTransSVPLevel(String transSVPLevel) {
        this.transSVPLevel = transSVPLevel;
    }
    
    public String getAgreementNumber() {
        return agreementNumber;
    }
    
    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber;
    }
    
    public String getIsAddiSiteGovTypeDisplay() {
        return isAddiSiteGovTypeDisplay;
    }
    
    public void setIsAddiSiteGovTypeDisplay(String isAddiSiteGovTypeDisplay) {
        this.isAddiSiteGovTypeDisplay = isAddiSiteGovTypeDisplay;
    }

	public String getEndUserFlag() {
		return endUserFlag;
	}

	public void setEndUserFlag(String endUserFlag) {
		this.endUserFlag = endUserFlag;
	}
    
    
}
