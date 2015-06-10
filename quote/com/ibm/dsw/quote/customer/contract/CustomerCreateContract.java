
package com.ibm.dsw.quote.customer.contract;

import com.ibm.dsw.quote.customer.viewbean.AgrmntTypeViewAdapter;
import com.ibm.dsw.quote.customer.viewbean.DisplayCustCreateViewBean;
import com.ibm.ead4j.common.bean.Result;
import com.ibm.ead4j.common.bean.View;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.config.ResultBeanKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;


/**
 * @author Lavanya
 */

public class CustomerCreateContract extends AgrmntBaseContract {
    
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
    private String cntPhoneNumFull;
    private String cntFaxNumFull;
    private String cntEmailAdr;
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
    private String cusCountry;
    private String customerType;
    
    private DisplayCustCreateViewBean previousViewBean = null;
    
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        
        // get previous viewbean.
        getPreviousViewBean(parameters, session);
        
        verifyAgreementType();
        verifyAgreementNumber();
        verifyGovSiteType();
        verifyTransSVPLevel();
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
     * @return Returns the cntEmailAdr.
     */
    public String getCntEmailAdr() {
        return cntEmailAdr;
    }
    /**
     * @param cntEmailAdr The cntEmailAdr to set.
     */
    public void setCntEmailAdr(String cntEmailAdr) {
        this.cntEmailAdr = cntEmailAdr;
    }
    /**
     * @return Returns the cntFaxNumFull.
     */
    public String getCntFaxNumFull() {
        return cntFaxNumFull;
    }
    /**
     * @param cntFaxNumFull The cntFaxNumFull to set.
     */
    public void setCntFaxNumFull(String cntFaxNumFull) {
        this.cntFaxNumFull = cntFaxNumFull;
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
     * @return Returns the cntPhoneNumFull.
     */
    public String getCntPhoneNumFull() {
        return cntPhoneNumFull;
    }
    /**
     * @param cntPhoneNumFull The cntPhoneNumFull to set.
     */
    public void setCntPhoneNumFull(String cntPhoneNumFull) {
        this.cntPhoneNumFull = cntPhoneNumFull;
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
    
    protected void getPreviousViewBean(Parameters parameters, JadeSession session) {
        String undoResultKey = genUndoResultKey();
        Result previousResult = (Result) session.getAttribute(undoResultKey);
        
        if (previousResult != null)  {
            View previousView = previousResult.getView();
            if (previousView != null && previousView instanceof DisplayCustCreateViewBean) {
                previousViewBean = (DisplayCustCreateViewBean) previousView;
            }
        }
    }
    
    protected String genUndoResultKey() {
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String key = context.getConfigParameter(ResultBeanKeys.JADE_PREVIOUS_RESULT_KEY);
        String prefix = context.getConfigParameter(FrameworkKeys.JADE_NAME_SPACE_PREFIX);
        if (prefix == null || prefix.length() == 0) {
            prefix = FrameworkKeys.JADE_NAME_SPACE_PREFIX_DEFAULT;
        }
        return prefix + key;
    }
    
    public void setAddiSiteGovTypeDisplay(boolean isAddiSiteGovTypeDisplay) {
        if (previousViewBean == null)
            return;
        
        AgrmntTypeViewAdapter viewAdapter = previousViewBean.getAgrmntTypeView();
        viewAdapter.setAddiSiteGovTypeDisplay(isAddiSiteGovTypeDisplay);
    }
    
    public void setAddiSiteGovType(int govType) {
        if (previousViewBean == null)
            return;
        
        AgrmntTypeViewAdapter viewAdapter = previousViewBean.getAgrmntTypeView();
        viewAdapter.setGovSiteType(govType);
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer("CustomerCreateContract values:\n");
        
        sb.append("lob=").append(getLob()).append("\n");
        sb.append("country=").append(getCountry()).append("\n");
        sb.append("agreementType=").append(agreementType).append("\n");
        sb.append("agreementNumber=").append(getAgreementNumber()).append("\n");
        sb.append("transSVPLevel=").append(transSVPLevel).append("\n");
        sb.append("govSiteType=").append(govSiteType).append("\n");
        sb.append("authrztnGroup=").append(authrztnGroup).append("\n");
        
        return sb.toString();
    }

	public String getCusCountry() {
		return cusCountry;
	}

	public void setCusCountry(String cusCountry) {
		this.cusCountry = cusCountry;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
    	
    
}
