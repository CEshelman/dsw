package com.ibm.dsw.quote.draftquote.viewbean;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteContact;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.RselCtrldDistribtn;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.customer.config.CustomerActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.partner.config.PartnerActionKeys;
import com.ibm.dsw.quote.partner.config.PartnerMessageKeys;
import com.ibm.dsw.quote.partner.config.PartnerParamKeys;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>CustPrtnrTabCommonInfo<code> class.
 *
 * @author: zhengmr@cn.ibm.com
 *
 * Creation date: 2007-5-9
 */

public class CustPrtnrTabViewAdapter implements Serializable {

    protected Quote quote = null;
    protected transient QuoteHeader header = null;
    protected transient Customer cust = null;
    protected Contract contract = null;
    protected transient List cntList = null;
    protected transient Partner reseller = null;
    protected transient Partner payer = null;
    protected transient Customer endUser = null;

    protected Locale locale = null;

    protected boolean isPurchaseOrderDriven = false;

    protected String custName = "";
    protected String custCity = "";
    protected String custState = "";
    protected String custCntry = "";
    protected String address1 = "";
    protected String address2 = "";
    protected String custZip = "";
    protected String custName2 = "";
    protected String custName3 = "";
    protected String siteNumber = "";
    protected String ibmCustNum = "";
    protected String rdcNumList = "";
    protected int webCustId = 0;
    protected int endUserWebCustId = 0;

    protected String agreementNum = "";
    protected String contractVariant = "";
    protected String anniversary = "";

    protected String qtCntctFirstName = "";
    protected String qtCntctLastName = "";
    protected String qtCntctPhone = "";
    protected String qtCntctFax = "";
    protected String qtCntctEmail = "";

    protected String primCntctFirstName = "";
    protected String primCntctLastName = "";
    protected String primCntctPhone = "";
    protected String primCntctFax = "";
    protected String primCntctEmail = "";

    protected String resellerCustNum = "";
    protected String resellerCustName = "";
    protected String resellerAddress1 = "";
    protected String resellerAddress2 = "";
    protected String resellerCity = "";
    protected String resellerState = "";
    protected String resellerZip = "";
    protected String resellerCntry = "";
    protected String resellerRdcNum = "";
    protected String resellerIbmCustNum = "";
    protected String resellerAuthPortfolioList = "";

    protected String distributorCustNum = "";
    protected String distributorCustName = "";
    protected String distributorAddress1 = "";
    protected String distributorAddress2 = "";
    protected String distributorCity = "";
    protected String distributorState = "";
    protected String distributorZip = "";
    protected String distributorCntry = "";
    protected String distributorRdcNum = "";
    protected String distributorIbmCustNum = "";

    protected String custSearchBtnParams = "";
    //for End customer information
    protected String custCreateBtnParams = "";
    protected String addiCntInfoBtnParams = "";
    protected String resellerSearchBtnParams = "";
    protected String distributorSearchBtnParams = "";
    protected String payerSearchBtnParams = "";
    protected String viewCustRptBtnParams = "";
    protected String viewSWOnlineBtnParams = "";
    protected String endUserSearchBtnParams = "";

    protected String country = "";
    protected String lob = "";
    protected String systemLob = "";
    protected String webQuoteNum = "";
    protected boolean isSubmittedQuote = false;
    protected int searchTierType = 0;
    protected String progMigrtnCode = "";

    protected String displayTabAction = "";
    protected String postTabAction = "";

    protected String custDesignation = "";
    protected boolean hasNewCust = false;
    protected boolean hasAddiSiteCustomer = false;

    protected String endUserName = "";
    protected String endUserName2 = "";
    protected String endUserName3 = "";
    protected String endUserCity = "";
    protected String endUserState = "";
    protected String endUserCntry = "";
    protected String endUserAddress1 = "";
    protected String endUserAddress2 = "";
    protected String endUserZip = "";
    protected String endUserSiteNumber = "";

    protected boolean isDisplayPODrivenInfo = false;
    protected int govEntityIndCode = 0;
    protected String govEntityIndCodeDesc = "";
    private boolean isCustOrdBlcked = false;
    protected String countryCode2 = "";
    private String cvrageGrpCodeDesc = "";
    private String custCvrageDesc = "";
    private String initWebAppBusDscr = "";
    private String currWebAppBusDscr = "";
    private String addressType= "";
    //for for Customer information
    protected String custCreateCustBtnParams = "";
    
    /**
     * Constructor
     */
    public CustPrtnrTabViewAdapter(Quote quote, String displayTabAction, String postTabAction, Locale locale)
            throws IllegalStateException {
        if (quote == null)
            throw new IllegalStateException();

        this.quote = quote;
        this.displayTabAction = displayTabAction;
        this.postTabAction = postTabAction;
        this.locale = locale;

        this.collectInfo();
    }

    protected void collectInfo() {

        header = quote.getQuoteHeader();
        cust = quote.getCustomer();
        cntList = quote.getContactList();
        reseller = quote.getReseller();
        payer = quote.getPayer();
        contract = this.getFirstContract(cust);
        endUser = quote.getEndUser();

        country = header.getCountry() == null ? "" : header.getCountry().getCode3();
        countryCode2 = header.getCountry() == null ? "" : header.getCountry().getCode2();
        lob = header.getLob() == null ? "" : header.getLob().getCode();
        systemLob = header.getSystemLOB() == null ? "" : header.getSystemLOB().getCode();
        webQuoteNum = header.getWebQuoteNum();
        isSubmittedQuote = StringUtils.isNotBlank(header.getSapIntrmdiatDocNum());
        progMigrtnCode = header.getProgMigrationCode();
        hasNewCust = (header.getWebCustId() > 0);

        if (!isSubmittedQuote) {
            searchTierType = 0; // search for both tier1 & tier2 resellers
        }
        else {
            searchTierType = 0;
            boolean isDstrbtrChnlJ = QuoteConstants.DIST_CHNL_DISTRIBUTOR.equalsIgnoreCase(header.getSapDistribtnChnlCode());
            boolean isDstrbtrChnlH = QuoteConstants.DIST_CHNL_HOUSE_ACCOUNT.equalsIgnoreCase(header.getSapDistribtnChnlCode());

            if (isDstrbtrChnlH)
                searchTierType = 1; // search for tier 1 reseller
            else if (isDstrbtrChnlJ)
                searchTierType = 2; // search for tier 2 reseller
        }
        
        if ( endUser != null )
        {
        	endUserWebCustId = header.getEndUserWebCustId();
        }

        if (cust != null) {
            custDesignation = cust.getCustDesignation();
            govEntityIndCode = cust.getGovEntityIndCode();
            govEntityIndCodeDesc = cust.getGovEntityIndCodeDesc();
            cvrageGrpCodeDesc = cust.getCvrageGrpCodeDesc();
            isPurchaseOrderDriven = ("Y".equalsIgnoreCase(cust.getPurchOrdReqrdFlag()));
            custName = cust.getCustName();
	        custCity = cust.getCity();
	        custCvrageDesc = cust.getCustCvrageDesc();
	        initWebAppBusDscr = cust.getInitWebAppBusDscr();
	        currWebAppBusDscr = cust.getCurrWebAppBusDscr();

	        Country cntry = this.getCountry(cust.getCountryCode());
	        if (cntry != null) {
	            custState = cntry.getStateDescription(cust.getSapRegionCode());
	            custCntry = cntry.getDesc();
	        }
	        else {
		        custState = cust.getSapRegionCode();
		        custCntry = cust.getCountryCode();
	        }

            address1 = cust.getAddress1();
            address2 = cust.getInternalAddress();
            custZip = cust.getPostalCode();
            custName2 = cust.getCustName2();
            custName3 = cust.getCustName3();
            siteNumber = cust.getCustNum();
	        ibmCustNum = cust.getIbmCustNum();
	        webCustId = cust.getWebCustId();

	        rdcNumList = this.getRDCNumStringList(cust.getRdcNumList());

            primCntctFirstName = cust.getCntFirstName();
            primCntctLastName = cust.getCntLastName();
            primCntctPhone = cust.getCntPhoneNumFull();
            primCntctFax = cust.getCntFaxNumFull();
            primCntctEmail = cust.getCntEmailAdr();
            hasAddiSiteCustomer = cust.isAddiSiteCustomer();

            isCustOrdBlcked = cust.isCustOrdBlcked();
        }

        if ((CustomerConstants.LOB_PA.equalsIgnoreCase(lob) || CustomerConstants.LOB_FCT.equalsIgnoreCase(lob)
                || CustomerConstants.LOB_OEM.equalsIgnoreCase(lob))
                && contract != null) {
            agreementNum = contract.getSapContractNum();
            contractVariant = this.getContractOptionDesc(contract.getSapContractVariantCode());
            anniversary = DateHelper.getDateByFormat(contract.getAnniversaryDate(), "MMM", locale);
        }

        // Get end customer information
        if (endUser != null) {

	        endUserName = endUser.getCustName();
	        endUserName2 = endUser.getCustName2();
	        endUserName3 = endUser.getCustName3();
	        endUserCity = endUser.getCity();

	        Country cntry = this.getCountry(endUser.getCountryCode());
	        if (cntry != null) {
	            endUserState = cntry.getStateDescription(endUser.getSapRegionCode());
	            endUserCntry = cntry.getDesc();
	        }
	        else {
	            endUserState = endUser.getSapRegionCode();
	            endUserCntry = endUser.getCountryCode();
	        }

	        endUserAddress1 = endUser.getAddress1();
	        endUserAddress2 = endUser.getInternalAddress();
	        endUserZip = endUser.getPostalCode();
	        endUserSiteNumber = endUser.getCustNum();
        }

        // Get contact information
        if (cntList != null && cntList.size() > 0) {
            QuoteContact qtCnt = (QuoteContact) cntList.get(0);
            if (qtCnt != null) {
	            qtCntctFirstName = qtCnt.getCntFirstName();
	            qtCntctLastName = qtCnt.getCntLastName();
	            qtCntctPhone = qtCnt.getCntPhoneNumFull();
	            qtCntctFax = qtCnt.getCntFaxNumFull();
	            qtCntctEmail = qtCnt.getCntEmailAdr();
            }
        }

        if (reseller != null) {
            resellerCustNum = reseller.getCustNum();
            resellerCustName = reseller.getCustNameFull();
            resellerAddress1 = reseller.getAddress1();
            resellerAddress2 = reseller.getAddress2();
            resellerCity = reseller.getCity();
            resellerZip = reseller.getPostalCode();
            resellerRdcNum = getRDCNumStringList(reseller.getRdcNumList());
            resellerIbmCustNum = reseller.getIbmCustNum();

            resellerAuthPortfolioList = getPortfolioListString(reseller.getAuthorizedPortfolioList(),reseller.getAuthorizedPortfolioMap());

            String cntryCode = StringUtils.trimToEmpty(reseller.getCountry());
            String stateCode = StringUtils.trimToEmpty(reseller.getState());
            Country cntry = this.getCountry(cntryCode);

	        if (cntry != null) {
	            resellerState = cntry.getStateDescription(stateCode);
	            resellerCntry = cntry.getDesc();
	        }
	        else {
	            resellerState = stateCode;
	            resellerCntry = cntryCode;
	        }
        }

        if (payer != null) {

            distributorCustNum = payer.getCustNum();
            distributorCustName = payer.getCustNameFull();
            distributorAddress1 = payer.getAddress1();
            distributorAddress2 = payer.getAddress2();
            distributorCity = payer.getCity();
            distributorZip = payer.getPostalCode();
            distributorRdcNum = getRDCNumStringList(payer.getRdcNumList());
            distributorIbmCustNum = payer.getIbmCustNum();

            String cntryCode = StringUtils.trimToEmpty(payer.getCountry());
            String stateCode = StringUtils.trimToEmpty(payer.getState());
            Country cntry = this.getCountry(cntryCode);

	        if (cntry != null) {
	            distributorState = cntry.getStateDescription(stateCode);
	            distributorCntry = cntry.getDesc();
	        }
	        else {
	            distributorState = stateCode;
	            distributorCntry = cntryCode;
	        }
        }

        isDisplayPODrivenInfo = ButtonDisplayRuleFactory.singleton().isPODrivenDisplay(header);
    }

    public void replicatePrimCntToQtCnt() {
        if (StringUtils.isBlank(qtCntctFirstName) && StringUtils.isBlank(qtCntctLastName)
                && StringUtils.isBlank(qtCntctPhone) && StringUtils.isBlank(qtCntctFax)
                && StringUtils.isBlank(qtCntctEmail)) {
            qtCntctFirstName = primCntctFirstName;
            qtCntctLastName = primCntctLastName;
            qtCntctPhone = primCntctPhone;
            qtCntctFax = primCntctFax;
            qtCntctEmail = primCntctEmail;
        }
    }

    protected String getContractOptionDesc(String contractOption) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();

        if (StringUtils.isBlank(contractOption))
            return "";
        List  ctrctOptList = null;
        try {
            CacheProcess process = CacheProcessFactory.singleton().create();
            ctrctOptList = process.getCtrctVariantList();
        } catch (QuoteException qe) {
            logContext.error(this, qe.getMessage());
        }
        if (ctrctOptList != null && ctrctOptList.size() > 0) {
            for (int i = 0; i < ctrctOptList.size(); i++) {
                CodeDescObj codeDescObj = (CodeDescObj) ctrctOptList.get(i);
                if (contractOption.equalsIgnoreCase(codeDescObj.getCode()))
                    return codeDescObj.getCodeDesc();
            }
        }
        return contractOption;
    }

    protected Contract getFirstContract(Customer customer) {
        if (customer == null || customer.getContractList() == null || customer.getContractList().size() == 0)
            return null;
        return (Contract) customer.getContractList().get(0);
    }

    protected String getCountryDesc(String cntryCode) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        Country cntry = null;
        try {
            CacheProcess cProcess = CacheProcessFactory.singleton().create();
            cntry = cProcess.getCountryByCode3(cntryCode);
        } catch (QuoteException e) {
            logContext.error(this, "Failed to get country by "+cntryCode);
        }
        return cntry == null ? cntryCode : cntry.getDesc();
    }

    protected Country getCountry(String cntryCode) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        Country cntry = null;
        try {
            CacheProcess cProcess = CacheProcessFactory.singleton().create();
            cntry = cProcess.getCountryByCode3(cntryCode);
        } catch (QuoteException e) {
            logContext.error(this, "Failed to get country by "+cntryCode);
        }
        return cntry;
    }

    public String getCustDesignation() {
        return custDesignation == null ? "" : custDesignation;
    }

    public int getGovEntityIndCode() {
        return govEntityIndCode;
    }

    public String getGovEntityIndCodeDesc() {
        return govEntityIndCodeDesc == null ? "" : govEntityIndCodeDesc;
    }

    public String getAddress1() {
        return address1 == null ? "" : address1;
    }

    public String getAddress2() {
        return address2 == null ? "" : address2;
    }

    public String getAnniversary() {
        return anniversary == null ? "" : anniversary;
    }

    public String getContractVariant() {
        return contractVariant == null ? "" : contractVariant;
    }

    public String getCustName2() {
        return custName2 == null ? "" : custName2;
    }

    public String getCustName3() {
        return custName3 == null ? "" : custName3;
    }

    public String getCustZip() {
        return custZip == null ? "" : custZip;
    }

    public String getDistributorAddress1() {
        return distributorAddress1 == null ? "" : distributorAddress1;
    }

    public String getDistributorAddress2() {
        return distributorAddress2 == null ? "" : distributorAddress2;
    }

    public String getDistributorCity() {
        return distributorCity == null ? "" : distributorCity;
    }

    public String getDistributorCntry() {
        return distributorCntry == null ? "" : distributorCntry;
    }

    public String getDistributorCustName() {
        return distributorCustName == null ? "" : distributorCustName;
    }

    public String getDistributorCustNum() {
        return distributorCustNum == null ? "" : distributorCustNum;
    }

    public String getDistributorState() {
        return distributorState == null ? "" : distributorState;
    }

    public String getDistributorZip() {
        return distributorZip == null ? "" : distributorZip;
    }

    public String getDistributorIbmCustNum() {
        return distributorIbmCustNum == null ? "" : distributorIbmCustNum;
    }

    public String getDistributorRdcNum() {
        return distributorRdcNum == null ? "" : distributorRdcNum;
    }

    public String getResellerIbmCustNum() {
        return resellerIbmCustNum == null ? "" : resellerIbmCustNum;
    }

    public String getResellerRdcNum() {
        return resellerRdcNum == null ? "" : resellerRdcNum;
    }

    public String getResellerAuthPortfolioList() {
        return resellerAuthPortfolioList == null ? "" : resellerAuthPortfolioList;
    }

    public boolean isPurchaseOrderDriven() {
        return isPurchaseOrderDriven;
    }

    public String getPrimCntctEmail() {
        return primCntctEmail == null ? "" : primCntctEmail;
    }

    public String getPrimCntctFax() {
        return primCntctFax == null ? "" : primCntctFax;
    }

    public String getPrimCntctFirstName() {
        return primCntctFirstName == null ? "" : primCntctFirstName;
    }

    public String getPrimCntctLastName() {
        return primCntctLastName == null ? "" : primCntctLastName;
    }

    public String getPrimCntctPhone() {
        return primCntctPhone == null ? "" : primCntctPhone;
    }

    public String getQtCntctEmail() {
        return qtCntctEmail == null ? "" : qtCntctEmail;
    }

    public String getQtCntctFax() {
        return qtCntctFax == null ? "" : qtCntctFax;
    }

    public String getQtCntctFirstName() {
        return qtCntctFirstName == null ? "" : qtCntctFirstName;
    }

    public String getQtCntctLastName() {
        return qtCntctLastName == null ? "" : qtCntctLastName;
    }

    public String getQtCntctPhone() {
        return qtCntctPhone == null ? "" : qtCntctPhone;
    }

    public String getResellerAddress1() {
        return resellerAddress1 == null ? "" : resellerAddress1;
    }

    public String getResellerAddress2() {
        return resellerAddress2 == null ? "" : resellerAddress2;
    }

    public String getResellerCity() {
        return resellerCity == null ? "" : resellerCity;
    }

    public String getResellerCntry() {
        return resellerCntry == null ? "" : resellerCntry;
    }

    public String getResellerCustName() {
        return resellerCustName == null ? "" : resellerCustName;
    }

    public String getResellerCustNum() {
        return resellerCustNum == null ? "" : resellerCustNum;
    }

    public String getResellerState() {
        return resellerState == null ? "" : resellerState;
    }

    public String getResellerZip() {
        return resellerZip == null ? "" : resellerZip;
    }

    public String getRdcNumList() {
        return rdcNumList == null ? "" : rdcNumList;
    }

    public String getDisplayTabAction() {
        return displayTabAction;
    }

    public String getPostTabAction() {
        return postTabAction;
    }

    public boolean isUSFederalCust() {
        if (cust == null)
            return false;
        else
            return cust.getGsaStatusFlag();
    }

    public boolean isDisplayUSFederalFlag() {
        return ((header.isPAQuote() || header.isPAEQuote() || header.isFCTQuote()) && CustomerConstants.COUNTRY_USA
                .equalsIgnoreCase(country));
    }

    public boolean hasAddiSiteCustomer() {
        return hasAddiSiteCustomer;
    }

    public String getCustCityStateZip() {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotBlank(custCity))
            sb.append(custCity.trim());
        if (StringUtils.isNotBlank(custState)) {
            if (sb.length() == 0)
                sb.append(custState.trim());
            else
                sb.append(", "+custState.trim());
        }
        if (StringUtils.isNotBlank(custZip)) {
            if (sb.length() == 0)
                sb.append(custZip.trim());
            else if (StringUtils.isNotBlank(custState))
                sb.append( " " + custZip.trim());
            else
                sb.append(", "+custZip.trim());
        }
        return sb.toString();
    }

    public String getResellerCityStateZip() {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotBlank(resellerCity))
            sb.append(resellerCity.trim());
        if (StringUtils.isNotBlank(resellerState)) {
            if (sb.length() == 0)
                sb.append(resellerState.trim());
            else
                sb.append(", "+resellerState.trim());
        }
        if (StringUtils.isNotBlank(resellerZip)) {
            if (sb.length() == 0)
                sb.append(resellerZip.trim());
            else if (StringUtils.isNotBlank(resellerState))
                sb.append( " " + resellerZip.trim());
            else
                sb.append(", "+resellerZip.trim());
        }
        return sb.toString();
    }

    public String getDistributorCityStateZip() {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotBlank(distributorCity))
            sb.append(distributorCity.trim());
        if (StringUtils.isNotBlank(distributorState)) {
            if (sb.length() == 0)
                sb.append(distributorState.trim());
            else
                sb.append(", "+distributorState.trim());
        }
        if (StringUtils.isNotBlank(distributorZip)) {
            if (sb.length() == 0)
                sb.append(distributorZip.trim());
            else if (StringUtils.isNotBlank(distributorState))
                sb.append( " " + distributorZip.trim());
            else
                sb.append(", "+distributorZip.trim());
        }
        return sb.toString();
    }

    public String getCustCreateBtnParams() {
    	StringBuffer params;
    	if(QuoteConstants.LOB_SSP.equalsIgnoreCase(lob)){
           params = new StringBuffer(ParamKeys.PARAM_COUNTRY + "=" + country
                    + "," + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + systemLob
                    + "," + ParamKeys.PARAM_QUOTE_NUM + "=" + webQuoteNum
                    + "," + ParamKeys.PARAM_WEBCUST_ID + "=" + endUserWebCustId
                    + "," + ParamKeys.PARAM_AGREEMENT_NUM + "=" + agreementNum
                    + "," + ParamKeys.PARAM_COUNTRY_CODE2 + "=" + countryCode2);
    	}else{
            params = new StringBuffer(ParamKeys.PARAM_COUNTRY + "=" + country
                    + "," + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + systemLob
                    + "," + ParamKeys.PARAM_QUOTE_NUM + "=" + webQuoteNum
                    + "," + DraftQuoteParamKeys.PARAM_PROG_MIGRTN_CODE + "=" + progMigrtnCode
                    + "," + ParamKeys.PARAM_SITE_NUM + "=" + siteNumber
                    + "," + ParamKeys.PARAM_WEBCUST_ID + "=" + endUserWebCustId
                    + "," + ParamKeys.PARAM_AGREEMENT_NUM + "=" + agreementNum
                    + "," + ParamKeys.PARAM_COUNTRY_CODE2 + "=" + countryCode2);    		
    	}
    	params.append(",").append(ParamKeys.PARAM_END_USER_FLAG_NAME + "=1");
    	params.append(",").append(ParamKeys.PARAM_IS_NEW_CUST + "=true");
        custCreateBtnParams = genBtnParamsForAction(this.getPostTabAction(),
                CustomerActionKeys.DISPLAY_CREATE_CUSTOMER, params.toString());
        return custCreateBtnParams;
    }
    
    

    public String getCustCreateCustBtnParams() {
    	StringBuffer params;
    	if(QuoteConstants.LOB_SSP.equalsIgnoreCase(lob)){
            params = new StringBuffer(ParamKeys.PARAM_COUNTRY + "=" + country
                    + "," + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + systemLob
                    + "," + ParamKeys.PARAM_QUOTE_NUM + "=" + webQuoteNum
                    + "," + ParamKeys.PARAM_WEBCUST_ID + "=" + webCustId
                    + "," + ParamKeys.PARAM_AGREEMENT_NUM + "=" + agreementNum
                    + "," + ParamKeys.PARAM_COUNTRY_CODE2 + "=" + countryCode2
                    + "," + ParamKeys.PARAM_SITE_NUM + "=" + siteNumber
                    + "," + DraftQuoteParamKeys.PARAM_PROG_MIGRTN_CODE + "=" + progMigrtnCode
                    + "," + ParamKeys.PARAM_END_USER_FLAG_NAME + "=0"
                    );
    	}else{
            params = new StringBuffer(ParamKeys.PARAM_COUNTRY + "=" + country
                    + "," + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + systemLob
                    + "," + ParamKeys.PARAM_QUOTE_NUM + "=" + webQuoteNum
                    + "," + DraftQuoteParamKeys.PARAM_PROG_MIGRTN_CODE + "=" + progMigrtnCode
                    + "," + ParamKeys.PARAM_SITE_NUM + "=" + siteNumber
                    + "," + ParamKeys.PARAM_WEBCUST_ID + "=" + webCustId
                    + "," + ParamKeys.PARAM_AGREEMENT_NUM + "=" + agreementNum
                    + "," + ParamKeys.PARAM_COUNTRY_CODE2 + "=" + countryCode2);    		
    	}
    	
    	params.append(",").append(ParamKeys.PARAM_IS_NEW_CUST + "=true");

    	custCreateCustBtnParams = genBtnParamsForAction(this.getPostTabAction(),
                CustomerActionKeys.DISPLAY_CREATE_CUSTOMER, params.toString());
        return custCreateCustBtnParams;
    }

    public String getCustSearchBtnParams() {
    	
		String params = ParamKeys.PARAM_COUNTRY + "=" + country + ","
				+ ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + systemLob + ","
				+ ParamKeys.PARAM_QUOTE_NUM + "=" + webQuoteNum + ","
				+ ParamKeys.PARAM_SITE_NUM + "=" + siteNumber + ","
				+ ParamKeys.PARAM_AGREEMENT_NUM + "=" + agreementNum + ","
				+ DraftQuoteParamKeys.PARAM_PROG_MIGRTN_CODE + "="
				+ progMigrtnCode + "," + ParamKeys.PARAM_COUNTRY_CODE2 + "="
				+ countryCode2;

		if (QuoteConstants.LOB_SSP.equalsIgnoreCase(lob)) {
			// set findActiveCusts=1 for check SSP readio as default
			params += "," + ParamKeys.PARAM_FIND_ACTIVE_CUSTS + "=1";
		}
        custSearchBtnParams = genBtnParamsForAction(this.getPostTabAction(),
                CustomerActionKeys.DISPLAY_SEARCH_CUSTOMER,
                params);
        return custSearchBtnParams;
    }

    public String getAssgnCrtAgrmntBtnParams() {
        return genBtnParamsForAction(getPostTabAction(), DraftQuoteActionKeys.DISPLAY_ASSGN_CRT_AGRMNT_ACTION, "");
    }

    public String getAddiCntInfoBtnParams() {
        String url = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_CNTS_RPT) + "&amp;"
                + DraftQuoteParamKeys.RPT_SAP_CUST_NUM + "=" + siteNumber + "&amp;" + DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM
                + "=" + agreementNum;
        addiCntInfoBtnParams = genBtnParams(this.getPostTabAction(), url, null);
        return addiCntInfoBtnParams;
    }

    public String getPayerSearchBtnParams() {
        payerSearchBtnParams = genBtnParamsForAction(this.getPostTabAction(),
                CustomerActionKeys.DISPLAY_SEARCH_CUSTOMER,
                ParamKeys.PARAM_COUNTRY + "=" + country + ","
                + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + lob + ","
                + ParamKeys.PARAM_QUOTE_NUM + "=" + webQuoteNum
                + ",searchFor=payer");
        return payerSearchBtnParams;
    }

    public String getDistributorSearchBtnParams() {
        distributorSearchBtnParams = genBtnParamsForAction(this.getPostTabAction(),
                PartnerActionKeys.DISPLAY_DISTRIBUTOR_SEARCH,
                ParamKeys.PARAM_COUNTRY + "=" + country
                + "," + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + lob
                + "," + DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM + "=" + webQuoteNum
                + "," + ParamKeys.PARAM_IS_SBMT_QT + "=" + this.isSubmittedQuote);
        return distributorSearchBtnParams;
    }

    public String getResellerSearchBtnParams() {
        resellerSearchBtnParams = genBtnParamsForAction(this.getPostTabAction(),
                PartnerActionKeys.DISPLAY_RESELLER_SEARCH,
                ParamKeys.PARAM_COUNTRY + "=" + country
                + "," + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + lob
                + "," + DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM + "=" + webQuoteNum
                + "," + PartnerParamKeys.PARAM_SEARCH_TIER_TYPE + "=" + this.searchTierType
                + "," + ParamKeys.PARAM_IS_SBMT_QT + "=" + this.isSubmittedQuote);
        return resellerSearchBtnParams;
    }

    public String getViewSWOnlineBtnParams() {
        viewSWOnlineBtnParams = genBtnParamsForAction(this.getPostTabAction(),
                DraftQuoteActionKeys.SET_USER_COOKIE,
                ParamKeys.PARAM_SITE_NUM + "=" + siteNumber + ","
                + ParamKeys.PARAM_AGREEMENT_NUM + "=" + agreementNum);
        return viewSWOnlineBtnParams;
    }

    public String getViewCustRptBtnParams() {
        String url = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_ENROLL_RPT);
        StringBuffer sb = new StringBuffer(url);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CUST_NUM, siteNumber);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM, agreementNum);

        viewCustRptBtnParams = genBtnParams(this.getPostTabAction(), sb.toString(), null);
        return viewCustRptBtnParams;
    }

    public String getViewCustRptEnrollmentsURL() {
        String url = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_ENROLL_RPT);
        StringBuffer sb = new StringBuffer(url);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CUST_NUM, siteNumber);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM, agreementNum);

        return sb.toString();
    }

    public String getViewCustRptCustomercontactsURL() {
        String url = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_CNTS_RPT);
        StringBuffer sb = new StringBuffer(url);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CUST_NUM, siteNumber);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM, agreementNum);

        return sb.toString();
    }

    public String getViewCustRptRnwlquotURL() {
        String url = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_RNQT_RPT);
        StringBuffer sb = new StringBuffer(url);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CUST_NUM, siteNumber);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM, agreementNum);

        return sb.toString();
    }

    public String getViewCustRptLicenseURL() {
        String url = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_LCNS_RPT);
        StringBuffer sb = new StringBuffer(url);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CUST_NUM, siteNumber);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM, agreementNum);

        return sb.toString();
    }

    public String getViewCustRptOrdhistURL() {
        String url = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_HIST_RPT);
        StringBuffer sb = new StringBuffer(url);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CUST_NUM, siteNumber);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM, agreementNum);

        return sb.toString();
    }

    public String getViewCustRptHostedServicesURL() {
        String url = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_HTSV_RPT);
        StringBuffer sb = new StringBuffer(url);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CUST_NUM, siteNumber);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM, agreementNum);

        return sb.toString();
    }

    public String getViewCustRptHostedServicesPGSURL() {
        String url = HtmlUtil.getURLForPGSReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_HTSV_RPT_PGS);
        StringBuffer sb = new StringBuffer(url);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CUST_NUM_PGS, siteNumber);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM_PGS, agreementNum);
        HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM, webQuoteNum);

        return sb.toString();
    }
    
    public String getViewInventoryDeployURL(){
    	String url = HtmlUtil.getViewInventoryDeployURL();
    	StringBuffer sb = new StringBuffer(url);
    	
    	HtmlUtil.addEncodeURLParam(sb, DraftQuoteParamKeys.COGNOS_CUST_NUMBER, siteNumber);
    	if(agreementNum != null && !"".equals(agreementNum)){
    		HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.COGNOS_AGREEMENT_NUMBER, agreementNum);	
    	}
		HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.COGNOS_BUS_CODE,
				QuoteCommonUtil.transLobForCognos(lob, header.getAgrmtTypeCode()));
      	return sb.toString();
    	
    }
    
    public String getViewRenewalForecastURL(){
    	String url = HtmlUtil.getViewRenewalForecastURL();
    	StringBuffer sb = new StringBuffer(url);
    	HtmlUtil.addEncodeURLParam(sb, DraftQuoteParamKeys.COGNOS_CUST_NUMBER, siteNumber);
    	if(agreementNum != null && !"".equals(agreementNum)){
    		HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.COGNOS_AGREEMENT_NUMBER, agreementNum);	
    	}
		HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.COGNOS_BUS_CODE,
				QuoteCommonUtil.transLobForCognos(lob, header.getAgrmtTypeCode()));
    	return sb.toString();
    }
    
    public String getViewReinstateQuoteURL(){
    	String url = HtmlUtil.getViewReinstateQuoteURL();
    	StringBuffer sb = new StringBuffer(url);
    	HtmlUtil.addEncodeURLParam(sb, DraftQuoteParamKeys.COGNOS_CUST_NUMBER, siteNumber);
    	if(agreementNum != null && !"".equals(agreementNum)){
    		HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.COGNOS_AGREEMENT_NUMBER, agreementNum);	
    	}
		HtmlUtil.addURLParam(sb, DraftQuoteParamKeys.COGNOS_BUS_CODE,
				QuoteCommonUtil.transLobForCognos(lob, header.getAgrmtTypeCode()));
    	
    	return sb.toString();
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

    protected String genBtnParams(String jadeAction, String redirectURL, String params) {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
        StringBuffer sb = new StringBuffer();

        sb.append(actionKey).append("=").append(jadeAction);
        if (StringUtils.isNotBlank(redirectURL))
            sb.append(",redirectURL=").append(redirectURL);
        if (StringUtils.isNotBlank(params))
            sb.append(",").append(params);
        return sb.toString();
    }

    public String getViewSWOnlineURL() {
        String url = HtmlUtil.getURLForAction(DraftQuoteActionKeys.SET_USER_COOKIE) + "&amp;"
                + ParamKeys.PARAM_SITE_NUM + "=" + siteNumber + "&amp;" + ParamKeys.PARAM_AGREEMENT_NUM + "="
                + agreementNum + "&amp;" + ParamKeys.PARAM_DEST + "=0";
        return url;
    }

    public String getRDCNumStringList(List rdcList){
        StringBuffer sb = new StringBuffer();
        if (rdcList != null) {
            for (int i = 0; i < rdcList.size(); i++) {
                if (i == 0)
                    sb.append((String) rdcList.get(i));
                else
                    sb.append("<br />"+(String) rdcList.get(i));
            }
        }
        return sb.toString();
    }

    public String getPortfolioListString(List portfolioList,Map authorizedPortfolioMap)
    {
        StringBuffer sb = new StringBuffer();
        if (portfolioList != null) {
            for (int i = 0; i < portfolioList.size(); i++) {
                boolean isGovRselOnly = true;
                CodeDescObj portfolio = (CodeDescObj)portfolioList.get(i);
                if (i == 0)
                    sb.append(StringUtils.replace(portfolio.getCodeDesc(), "&", "&amp;"));
                else
                    sb.append("<br />").append(StringUtils.replace(portfolio.getCodeDesc(), "&", "&amp;"));

                if (authorizedPortfolioMap != null) {
                    List tier1List = (List) authorizedPortfolioMap.get(portfolio.getCode());
                    if (tier1List == null)
                        isGovRselOnly = false;
                    else {
                        for (int j = 0; j < tier1List.size(); j++) {
                            RselCtrldDistribtn rselCtrldDistribtn = (RselCtrldDistribtn) tier1List.get(j);
                            if ("0".equalsIgnoreCase(rselCtrldDistribtn.getSapGovRselFlag()))
                                isGovRselOnly = false;
                        }
                    }
                    if (isGovRselOnly) {
                        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
                        String sGovRselOnly = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                                PartnerMessageKeys.MSG_GOV_RSEL_ONLY);
                        sb.append(sGovRselOnly);
                    }

                }

            }
        }
        return sb.toString();
    }

    public String getClearResellerBtnParams() {
        String params = genBtnParamsForAction(this.getPostTabAction(),
                PartnerActionKeys.SELECT_RESELLER,
                PartnerParamKeys.PARAM_PARTNER_TYPE + "=1"
                + "," + DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM + "=" + this.webQuoteNum
                + "," + PartnerParamKeys.PARAM_PARTNER_NUM + "="
                + "," + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + this.lob
                + "," + ParamKeys.PARAM_IS_SBMT_QT + "=" + this.isSubmittedQuote);
        return params;
    }

    public String getClearDistributorBtnParams() {
        String params = genBtnParamsForAction(this.getPostTabAction(),
                PartnerActionKeys.SELECT_RESELLER,
                PartnerParamKeys.PARAM_PARTNER_TYPE + "=0"
                + "," + DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM + "=" + this.webQuoteNum
                + "," + PartnerParamKeys.PARAM_PARTNER_NUM + "="
                + "," + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + this.lob
                + "," + ParamKeys.PARAM_IS_SBMT_QT + "=" + this.isSubmittedQuote);
        return params;
    }

    public String getClearPayerBtnParams() {
        String params = genBtnParamsForAction(this.getPostTabAction(),
                PartnerActionKeys.SELECT_RESELLER,
                PartnerParamKeys.PARAM_PARTNER_TYPE + "=3"
                + "," + DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM + "=" + this.webQuoteNum
                + "," + PartnerParamKeys.PARAM_PARTNER_NUM + "="
                + "," + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + this.lob
                + "," + ParamKeys.PARAM_IS_SBMT_QT + "=" + this.isSubmittedQuote);
        return params;
    }

    public String getClearEndUserBtnParams() {
        String params = genBtnParamsForAction(getPostTabAction(),
                CustomerActionKeys.SELECT_END_USER,
                ParamKeys.PARAM_QUOTE_NUM + "=" + webQuoteNum);
        return params;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());

        buffer.append("isPurchaseOrderDriven = ").append(isPurchaseOrderDriven).append("\n");
        buffer.append("address1 = ").append(address1).append("\n");
        buffer.append("address2 = ").append(address2).append("\n");
        buffer.append("custZip = ").append(custZip).append("\n");
        buffer.append("contractVariant = ").append(contractVariant).append("\n");
        buffer.append("anniversary = ").append(anniversary).append("\n");
        buffer.append("qtCntctFirstName = ").append(qtCntctFirstName).append("\n");
        buffer.append("qtCntctLastName = ").append(qtCntctLastName).append("\n");
        buffer.append("qtCntctPhone = ").append(qtCntctPhone).append("\n");
        buffer.append("qtCntctFax = ").append(qtCntctFax).append("\n");
        buffer.append("qtCntctEmail = ").append(qtCntctEmail).append("\n");
        buffer.append("primCntctFirstName = ").append(primCntctFirstName).append("\n");
        buffer.append("primCntctLastName = ").append(primCntctLastName).append("\n");
        buffer.append("primCntctPhone = ").append(primCntctPhone).append("\n");
        buffer.append("primCntctFax = ").append(primCntctFax).append("\n");
        buffer.append("primCntctEmail = ").append(primCntctEmail).append("\n");
        buffer.append("resellerCustNum = ").append(resellerCustNum).append("\n");
        buffer.append("resellerCustName = ").append(resellerCustName).append("\n");
        buffer.append("resellerAddress1 = ").append(resellerAddress1).append("\n");
        buffer.append("resellerAddress2 = ").append(resellerAddress2).append("\n");
        buffer.append("distributorCustNum = ").append(distributorCustNum).append("\n");
        buffer.append("distributorCustName = ").append(distributorCustName).append("\n");
        buffer.append("distributorAddress1 = ").append(distributorAddress1).append("\n");
        buffer.append("distributorAddress2 = ").append(distributorAddress2).append("\n");
        buffer.append("govEntityIndCode = ").append(govEntityIndCode).append("\n");
        buffer.append("govEntityIndCodeDesc = ").append(govEntityIndCodeDesc).append("\n");
        buffer.append("custCvrageDesc = ").append(custCvrageDesc).append("\n");
        buffer.append("initWebAppBusDscr = ").append(initWebAppBusDscr).append("\n");
        buffer.append("currWebAppBusDscr = ").append(currWebAppBusDscr).append("\n");

        return buffer.toString();
    }

    public String getEndUserName() {
        return endUserName == null ? "" : endUserName;
    }
    public String getEndUserName2() {
        return endUserName2 == null ? "" : endUserName2;
    }
    public String getEndUserName3() {
        return endUserName3 == null ? "" : endUserName3;
    }
    public String getEndUserCity() {
        return endUserCity == null ? "" : endUserCity;
    }
    public String getEndUserState() {
        return endUserState == null ? "" : endUserState;
    }
    public String getEndUserCntry() {
        return endUserCntry == null ? "" : endUserCntry;
    }
    public String getEndUserAddress1() {
        return endUserAddress1 == null ? "" : endUserAddress1;
    }
    public String getEndUserAddress2() {
        return endUserAddress2 == null ? "" : endUserAddress2;
    }
    public String getEndUserZip() {
        return endUserZip == null ? "" : endUserZip;
    }
    public String getEndUserSiteNumber() {
        return endUserSiteNumber == null ? "" : endUserSiteNumber;
    }

    public String getEndUserCityStateZip() {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotBlank(endUserCity))
            sb.append(endUserCity.trim());
        if (StringUtils.isNotBlank(endUserState)) {
            if (sb.length() == 0)
                sb.append(endUserState.trim());
            else
                sb.append(", "+endUserState.trim());
        }
        if (StringUtils.isNotBlank(endUserZip)) {
            if (sb.length() == 0)
                sb.append(endUserZip.trim());
            else if (StringUtils.isNotBlank(endUserState))
                sb.append( " " + endUserZip.trim());
            else
                sb.append(", "+endUserZip.trim());
        }
        return sb.toString();
    }

    public String getEndUserSearchBtnParams() {
        endUserSearchBtnParams = genBtnParamsForAction(this.getPostTabAction(),
                CustomerActionKeys.DISPLAY_SEARCH_END_USER,
                ParamKeys.PARAM_COUNTRY + "=" + country + ","
                + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + systemLob + ","
                + ParamKeys.PARAM_QUOTE_NUM + "=" + webQuoteNum);
               // + ParamKeys.PARAM_SITE_NUM + "=" + siteNumber);
        return endUserSearchBtnParams;
    }

    public boolean isPODrivenDisplay() {
        return isDisplayPODrivenInfo;
    }
    /**
     * @return Returns the isCustOrdBlcked.
     */
    public boolean isCustOrdBlcked() {
        return isCustOrdBlcked;
    }
    /**
     * @param isCustOrdBlcked The isCustOrdBlcked to set.
     */
    public void setCustOrdBlcked(boolean isCustOrdBlcked) {
        this.isCustOrdBlcked = isCustOrdBlcked;
    }
    /**
     * @return Returns the cvrageGrpCodeDesc.
     */
    public String getCvrageGrpCodeDesc() {
        return cvrageGrpCodeDesc;
    }

	public String getCustCvrageDesc() {
		return custCvrageDesc;
	}

	public String getInitWebAppBusDscr() {
		return initWebAppBusDscr;
	}

	public String getCurrWebAppBusDscr() {
		return currWebAppBusDscr;
	}
	
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	/**
	 * Get new ship to / install at address url link
	 * @return String
	 */
	public String getNewAddrBtnParams(String addressType) {
		StringBuffer params = new StringBuffer(ParamKeys.PARAM_COUNTRY + "="
				+ country + "," + ParamKeys.PARAM_LINE_OF_BUSINESS + "="
				+ systemLob + "," + ParamKeys.PARAM_QUOTE_NUM + "="
				+ webQuoteNum + "," + ParamKeys.PARAM_WEBCUST_ID + "="
				+ webCustId + "," + ParamKeys.PARAM_AGREEMENT_NUM + "="
				+ agreementNum + "," + ParamKeys.PARAM_COUNTRY_CODE2 + "="
				+ addressType + "," + ParamKeys.PARAM_ADDRESS_TYPE + "="
				+ addressType);

		return genBtnParamsForAction(this.getPostTabAction(),
				CustomerActionKeys.DISPLAY_APPLIANCE_ADDRESS, params.toString());
	}
	
	
	public String getSubmittedUrl(String addressType) {
		StringBuffer sb = new StringBuffer();
		sb.append(HtmlUtil.getURLForAction(CustomerActionKeys.DISPLAY_APPLIANCE_ADDRESS)); 
		sb.append("&amp;" + ParamKeys.PARAM_COUNTRY + "=" + country);
		sb.append("&amp;" + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + systemLob);
		sb.append("&amp;" + ParamKeys.PARAM_QUOTE_NUM + "=" + webQuoteNum);
		sb.append("&amp;" + ParamKeys.PARAM_WEBCUST_ID + "=" + webCustId);
		sb.append("&amp;" + ParamKeys.PARAM_ADDRESS_TYPE + "=" + addressType);
		sb.append("&amp;" + ParamKeys.PARAM_IS_SBMT_QT + "=true");
		return sb.toString();
	}


}
