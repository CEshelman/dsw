package com.ibm.dsw.quote.submittedquote.viewbean;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.QuoteUserAccess;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.viewbean.CustPrtnrTabViewAdapter;
import com.ibm.dsw.quote.partner.config.PartnerActionKeys;
import com.ibm.dsw.quote.partner.config.PartnerParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.util.QuoteOutputSubmissionUtil;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>SubmittedQuoteCustPrtnrTabViewBean<code> class.
 *
 * @author: zhengmr@cn.ibm.com
 *
 * Creation date: 2007-4-28
 */

public class SubmittedQuoteCustPrtnrTabViewBean extends SubmittedQuoteBaseViewBean {

    CustPrtnrTabViewAdapter adapter = null;

    protected transient List cntList = null;
    protected transient Partner payer = null;
    protected transient Partner reseller = null;
    protected QuoteUserAccess quoteUserAccess = null;
    protected transient Customer endUser = null;

    // For sales quote
    protected String fulfillmentSource = QuoteConstants.FULFILLMENT_DIRECT;

    protected boolean isDisplayViewRptAndSSO = false;
    protected boolean isDisplayAddiCntInfo = false;

    protected boolean isDisplayFulfillSrcText = false;
    protected boolean isDisplayPartnerSession = false;

    protected boolean hasRelationshap =false;
    protected boolean isNewCustomer = true;

    protected boolean hasReseller = false;
    protected boolean hasDistributor = false;

    protected boolean isSendQuoteToAddtnlPrtnrFlag = false;

	protected boolean isDisplayUpdtRselBtn = false;
    protected boolean isDisplayRseTBDText = false;
    protected boolean isDisplayUpdtDstrbtrBtn = false;
    protected boolean isDisplayDstrbtrTBDText = false;

    protected String addtnlPrtnrEmailAdr = "";
    protected String y9EmailAddresses = "";
    protected transient List y9EmailList;

    protected boolean isDstrbtrChnlJ = false;
    protected boolean isDstrbtrChnlH = false;
    protected String searchTierType = "0";

    protected boolean isDisplayEndUserInfo = false;
    protected boolean isDisplayPODrivenInfo = false;
    private boolean isExistCustomer = false;

    // For renewal quote
    protected String partnerAccess = "";


    /* (non-Javadoc)
     * @see com.ibm.ead4j.common.bean.ModelCrawler#collectResults(com.ibm.ead4j.common.util.Parameters)
     */
    public void collectResults(Parameters params) throws ViewBeanException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.collectResults(params);

        cntList = quote.getContactList();
        reseller = quote.getReseller();
        payer = quote.getPayer();
        endUser = quote.getEndUser();

        try {
            adapter = new CustPrtnrTabViewAdapter(quote, this.getDisplayTabAction(), this.getPostTabAction(), this.locale);
        } catch (IllegalStateException e) {
            logContext.error(this, e.getMessage());
            return;
        }

        if (endUser != null ){
            isDisplayEndUserInfo = true;
        }

        fulfillmentSource = StringUtils.trimToEmpty(header.getFulfillmentSrc());
        if (reseller != null) {
            hasReseller = true;
        }
        if (payer != null) {
            hasDistributor = true;
            y9EmailList = payer.getY9EmailList();
            y9EmailAddresses = getY9EmailAddresses(y9EmailList);
        }
        if ((header.hasExistingCustomer() || header.hasNewCustomer()) && cust != null) 
        	isExistCustomer = true;
        

        isDstrbtrChnlJ = QuoteConstants.DIST_CHNL_DISTRIBUTOR.equalsIgnoreCase(header.getSapDistribtnChnlCode());
        isDstrbtrChnlH = QuoteConstants.DIST_CHNL_HOUSE_ACCOUNT.equalsIgnoreCase(header.getSapDistribtnChnlCode());
        if (isDstrbtrChnlH)
            searchTierType = "1";
        else if (isDstrbtrChnlJ)
            searchTierType = "0";

        if (header.isSalesQuote()) {
            isDisplayFulfillSrcText = (header.isFCTQuote() || header.isPPSSQuote());

            quoteUserAccess = quote.getQuoteUserAccess();
            addtnlPrtnrEmailAdr = header.getAddtnlPrtnrEmailAdr();
            isSendQuoteToAddtnlPrtnrFlag = header.isSendQuoteToAddtnlPrtnrFlag();

            isDisplayViewRptAndSSO = (header.isPAQuote() || header.isPAEQuote() || header.isSSPQuote() || header.isFCTQuote() || header.isOEMQuote());
            isDisplayAddiCntInfo = (header.isPAQuote() || header.isPAEQuote());
            isDisplayPartnerSession = ((header.isPAQuote() || header.isPAEQuote() || header.isOEMQuote())
                    && QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(header.getFulfillmentSrc()) || (header
                    .isFCTQuote() && StringUtils.isNotBlank(header.getPayerCustNum())));
            isDisplayUpdtRselBtn = (quoteUserAccess.isEditor() && ( header.isResellerToBeDtrmndFlag() || (header.isDistribtrToBeDtrmndFlag() && isDstrbtrChnlJ) ));
            isDisplayRseTBDText = (!quoteUserAccess.isEditor() && header.isResellerToBeDtrmndFlag() && !hasReseller);
            isDisplayUpdtDstrbtrBtn = (quoteUserAccess.isEditor() && ( header.isDistribtrToBeDtrmndFlag() || (header.isResellerToBeDtrmndFlag() && isDstrbtrChnlJ) ));
            isDisplayDstrbtrTBDText = (!quoteUserAccess.isEditor() && header.isDistribtrToBeDtrmndFlag() && !hasDistributor);
        }
        else {
            isDisplayFulfillSrcText = false;
            if (!QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(fulfillmentSource)
                    && !QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(fulfillmentSource))
                fulfillmentSource = QuoteConstants.FULFILLMENT_NOT_SPECIFIED;

            isDisplayPartnerSession = true;
            isDisplayViewRptAndSSO = true;

            ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
            if (header.getRnwlPrtnrAccessFlag() == 1)
                partnerAccess = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                        DraftQuoteMessageKeys.SELECT_PARTNER_ACCESS_YES);
            else
                partnerAccess = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                        DraftQuoteMessageKeys.SELECT_PARTNER_ACCESS_NO);
        }

        isDisplayPODrivenInfo=adapter.isPODrivenDisplay();

        if(quote != null && quote.getQuoteHeader() != null){
        	isNewCustomer = quote.getQuoteHeader().hasNewCustomer();
        	hasRelationshap = quote.getQuoteHeader().isBPRelatedCust();
        }
    }

    public String getAddiCntInfoURL() {
        String url = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_CNTS_RPT) + "&amp;"
                + DraftQuoteParamKeys.RPT_SAP_CUST_NUM + "=" + siteNumber + "&amp;" + DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM
                + "=" + agreementNum;
        return url;
    }

    public String getAddress1() {
        return adapter.getAddress1();
    }

    public String getAddress2() {
        return adapter.getAddress2();
    }

    public String getAnniversary() {
        return adapter.getAnniversary();
    }

    public String getContractVariant() {
        return adapter.getContractVariant();
    }

    public String getCustName2() {
        return adapter.getCustName2();
    }

    public String getCustName3() {
        return adapter.getCustName3();
    }

    public String getCustZip() {
        return adapter.getCustZip();
    }

    public String getDistributorAddress1() {
        return adapter.getDistributorAddress1();
    }

    public String getDistributorAddress2() {
        return adapter.getDistributorAddress2();
    }

    public String getDistributorCity() {
        return adapter.getDistributorCity();
    }

    public String getDistributorCntry() {
        return adapter.getDistributorCntry();
    }

    public String getDistributorCustName() {
        return adapter.getDistributorCustName();
    }

    public String getDistributorCustNum() {
        return adapter.getDistributorCustNum();
    }

    public String getDistributorState() {
        return adapter.getDistributorState();
    }

    public String getDistributorZip() {
        return adapter.getDistributorZip();
    }

    public String getFulfillmentSource() {
        return fulfillmentSource;
    }

    public int getFulfillChoice() {
        int choice = 0;
        if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(fulfillmentSource))
            choice = 1;
        else if (QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(fulfillmentSource))
            choice = 2;
        return choice;
    }

    public boolean isPurchaseOrderDriven() {
        return adapter.isPurchaseOrderDriven();
    }

    public String getPrimCntctEmail() {
        return adapter.getPrimCntctEmail();
    }

    public String getPrimCntctFax() {
        return adapter.getPrimCntctFax();
    }

    public String getPrimCntctFirstName() {
        return adapter.getPrimCntctFirstName();
    }

    public String getPrimCntctLastName() {
        return adapter.getPrimCntctLastName();
    }

    public String getPrimCntctPhone() {
        return adapter.getPrimCntctPhone();
    }

    public String getQtCntctEmail() {
        return adapter.getQtCntctEmail();
    }

    public String getQtCntctFax() {
        return adapter.getQtCntctFax();
    }

    public String getQtCntctFirstName() {
        return adapter.getQtCntctFirstName();
    }

    public String getQtCntctLastName() {
        return adapter.getQtCntctLastName();
    }

    public String getQtCntctPhone() {
        return adapter.getQtCntctPhone();
    }

    public String getResellerAddress1() {
        return adapter.getResellerAddress1();
    }

    public String getResellerAddress2() {
        return adapter.getResellerAddress2();
    }

    public String getResellerCity() {
        return adapter.getResellerCity();
    }

    public String getResellerCntry() {
        return adapter.getResellerCntry();
    }

    public String getResellerCustName() {
        return adapter.getResellerCustName();
    }

    public String getResellerCustNum() {
        return adapter.getResellerCustNum();
    }

    public String getResellerState() {
        return adapter.getResellerState();
    }

    public String getResellerZip() {
        return adapter.getResellerZip();
    }

    public String getDistributorIbmCustNum() {
        return adapter.getDistributorIbmCustNum();
    }

    public String getDistributorRdcNum() {
        return adapter.getDistributorRdcNum();
    }

    public String getResellerIbmCustNum() {
        return adapter.getResellerIbmCustNum();
    }

    public String getResellerRdcNum() {
        return adapter.getResellerRdcNum();
    }

    public String getCustRdcNumList() {
        return adapter.getRdcNumList();
    }

    public String getViewCustRptURL() {
        String url = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_ENROLL_RPT) + "&amp;"
                + DraftQuoteParamKeys.RPT_SAP_CUST_NUM + "=" + siteNumber + "&amp;" + DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM
                + "=" + agreementNum;
        return url;
    }

    public String getViewCustRptHostedServicesPGSURL() {
        return adapter.getViewCustRptHostedServicesPGSURL();
    }

    public String getViewCustRptEnrollmentsURL() {
        return adapter.getViewCustRptEnrollmentsURL();
    }

    public String getViewCustRptCustomercontactsURL() {
        return adapter.getViewCustRptCustomercontactsURL();
    }

    public String getViewCustRptRnwlquotURL() {
        return adapter.getViewCustRptRnwlquotURL();
    }

    public String getViewCustRptLicenseURL() {
        return adapter.getViewCustRptLicenseURL();
    }

    public String getViewCustRptOrdhistURL() {
        return adapter.getViewCustRptOrdhistURL();
    }

    public String getViewCustRptHostedServicesURL() {
        return adapter.getViewCustRptHostedServicesURL();
    }
    
    public String getViewInventoryDeployURL() {
        return adapter.getViewInventoryDeployURL();
    }
    
    public String getViewRenewalForecastURL() {
    	return adapter.getViewRenewalForecastURL();
    }
    
    public String getViewReinstateQuoteURL() {
   	 	return adapter.getViewReinstateQuoteURL();
   }

    public String getViewSWOnlineURL() {
        return adapter.getViewSWOnlineURL();
    }

    public String getDisplayTabAction() {
        return SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB;
    }

    public boolean isDisplayAddiCntInfo() {
        return isDisplayAddiCntInfo;
    }

    public boolean isDisplayFulfillSrcText() {
        return isDisplayFulfillSrcText;
    }

    public boolean isDisplayPartnerSession() {
        return isDisplayPartnerSession;
    }

    public boolean isDisplayViewRptAndSSO() {
        return isDisplayViewRptAndSSO;
    }

    public String getCustCityStateZip() {
        return adapter.getCustCityStateZip();
    }

    public String getResellerCityStateZip() {
        return adapter.getResellerCityStateZip();
    }

    public String getDistributorCityStateZip() {
        return adapter.getDistributorCityStateZip();
    }

    public String getPartnerAccess() {
        return partnerAccess;
    }

    public String getResellerAuthPortfolioList() {
        return adapter.getResellerAuthPortfolioList();
    }

    public String getSelectResellerURL() {
        String[] keys = { SubmittedQuoteParamKeys.PARAM_WEB_QUOTE_NUM, ParamKeys.PARAM_COUNTRY,
                ParamKeys.PARAM_LINE_OF_BUSINESS, PartnerParamKeys.PARAM_SEARCH_TIER_TYPE, ParamKeys.PARAM_IS_SBMT_QT };
        String[] values = { this.webQuoteNum, this.country, this.lob, searchTierType, "true" };
        return this.getActionURL(PartnerActionKeys.DISPLAY_RESELLER_SEARCH, null, keys, values);
    }

    public String getSelectDistributorURL() {
        String[] keys = { SubmittedQuoteParamKeys.PARAM_WEB_QUOTE_NUM, ParamKeys.PARAM_COUNTRY,
                ParamKeys.PARAM_LINE_OF_BUSINESS, ParamKeys.PARAM_IS_SBMT_QT };
        String[] values = { this.webQuoteNum, this.country, this.lob, "true" };
        return this.getActionURL(PartnerActionKeys.DISPLAY_DISTRIBUTOR_SEARCH, null, keys, values);
    }

    public boolean hasReseller() {
        return hasReseller;
    }

    public boolean hasDistributor() {
        return hasDistributor;
    }

    public boolean isSendQuoteToAddtnlPrtnrFlag() {
        return isSendQuoteToAddtnlPrtnrFlag;
    }

    public String getAddtnlPrtnrEmailAdr(){
        return addtnlPrtnrEmailAdr;
    }

    protected String getY9EmailAddresses(List y9EmailList) {

        StringBuffer sb = new StringBuffer();

        if (y9EmailList != null && y9EmailList.size() > 0) {
            for (Iterator iter = y9EmailList.iterator(); iter.hasNext();)
            {
               String y9EmailAddr = (String) iter.next();

               sb.append(y9EmailAddr + "<br />");
            }
        }

        return sb.toString();
    }

    public String getY9EmailAddresses() {
        return y9EmailAddresses;
    }

    public boolean isSendAddtnlPrtnrEmailFlag() {
        if (addtnlPrtnrEmailAdr == null || addtnlPrtnrEmailAdr.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public boolean hasY9EmailAddresses() {
        if (y9EmailAddresses == null || y9EmailAddresses.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public String getSubmitUpdtPrtnrURL() {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String urlPattern = appContext.getConfigParameter(ApplicationProperties.APPLICATION_URL_PATTERN);

        return urlPattern;
    }

    public String getUpdtPrtnrParams() {
        return getBtnParams(SubmittedQuoteActionKeys.UPDATE_QUOTE_PARTNER, getDisplayTabAction());
    }

    public String getCrtCpyApprvdBidParams() {
        return getBtnParams(SubmittedQuoteActionKeys.COPY_APPROVED_BID, null);
    }

    public String getBtnParams(String actionCode, String action2Code) {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
        String action2Key = appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY);

        StringBuffer sb = new StringBuffer();
        sb.append(actionKey).append("=").append(actionCode);

        if (StringUtils.isNotBlank(action2Code))
            sb.append(",").append(action2Key).append("=").append(action2Code);

        sb.append(",").append(ParamKeys.PARAM_QUOTE_NUM).append("=").append(getWebQuoteNum());

        return sb.toString();
    }

    public boolean isDisplayUpdtPrtnrSession() {
        // *notes: when user select reseller or distributor, we don't update reseller or distributor TBD flag.
        // when user clicks Update partner button, we clear reseller or distributor TBD flag.
        return ((header.isPAQuote() || header.isPAEQuote())
                && header.isSalesQuote()
                && (header.isResellerToBeDtrmndFlag() || header.isDistribtrToBeDtrmndFlag())
                && quoteUserAccess != null
                && quoteUserAccess.isEditor())
                && hasReseller
                && hasDistributor;
    }
    public boolean isDisplayTBDCrtCpyFlag(){
        return ((header.isPAQuote() || header.isPAEQuote() || header.isCSRAQuote() || header.isCSTAQuote())
                 && header.isSalesQuote()
                 && StringUtils.isNotBlank(header.getSapQuoteNum())
                 && (header.isResellerToBeDtrmndFlag() || header.isDistribtrToBeDtrmndFlag())
                 && quoteUserAccess != null
                 && quoteUserAccess.isEditor())
                 && hasReseller
                 && hasDistributor
                 && header.getSpeclBidFlag() == 1
                 && header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_APPROVED)
                 && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.CANCEL_TERMINATED)
                 && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.EXPIRED_OR_OTHERS);		
 		}
    public boolean isDisplayTBDCrtCpyBtn() {
        // If special bid approved, display create a copy button.
        return  isDisplayTBDCrtCpyFlag()
        		&& !header.isHasDivestedPart();
    }
    
    public boolean isDisplayTBDCrtCpyBtnWithDivestedPart() {
        return  isDisplayTBDCrtCpyFlag()
				&& header.isHasDivestedPart();
    }

    public boolean isDisplayNoPrtnrSlctedMsg() {
        // *notes: when user select reseller or distributor, we don't update reseller or distributor TBD flag.
        // when user clicks Update partner button, we clear reseller or distributor TBD flag.
        return ((header.isPAQuote() || header.isPAEQuote())
                && header.isSalesQuote()
                && (header.isResellerToBeDtrmndFlag() || header.isDistribtrToBeDtrmndFlag())
                && quoteUserAccess != null
                && quoteUserAccess.isEditor())
                && !hasReseller
                && !hasDistributor;
    }

    public boolean isDisplayDstrbtrTBDText() {
        return isDisplayDstrbtrTBDText;
    }

    public boolean isDisplayRseTBDText() {
        return isDisplayRseTBDText;
    }

    public boolean isDisplayUpdtDstrbtrBtn() {
        return isDisplayUpdtDstrbtrBtn;
    }

    public boolean isDisplayUpdtRselBtn() {
        return isDisplayUpdtRselBtn;
    }

    public boolean isDstrbtrChnlH() {
        return isDstrbtrChnlH;
    }

    public boolean isDstrbtrChnlJ() {
        return isDstrbtrChnlJ;
    }

    public String getCustDesignation() {
        return adapter.getCustDesignation();
    }

    public boolean isDisplayUSFederalFlag() {
        return adapter.isDisplayUSFederalFlag();
    }

    public boolean isUSFederalCust() {
        return adapter.isUSFederalCust();
    }

    public boolean hasAddiSiteCustomer() {
        return adapter.hasAddiSiteCustomer();
    }

    public boolean isSendPartnerNotif()  {
        return isSpecialBid() && hasDistributor() && hasY9EmailAddresses();
    }

    public boolean isDisplayEndUserInfo() {
        return isDisplayEndUserInfo;
    }

    public String getEndUserName() {
        return adapter.getEndUserName();
    }
    public String getEndUserName2() {
        return adapter.getEndUserName2();
    }
    public String getEndUserName3() {
        return adapter.getEndUserName3();
    }
    public String getEndUserCntry() {
        return adapter.getEndUserCntry();
    }
    public String getEndUserAddress1() {
        return adapter.getEndUserAddress1();
    }
    public String getEndUserAddress2() {
        return adapter.getEndUserAddress2();
    }
    public String getEndUserCityStateZip() {
        return adapter.getEndUserCityStateZip();
    }
    public String getEndUserSiteNumber() {
        return adapter.getEndUserSiteNumber();
    }
    public boolean isPODrivenDisplay() {
        return isDisplayPODrivenInfo;
    }
    /**
     * check if it should display "E-mail the approved special bid business
     * partner notification to the following address" input box
     *
     * @return true for display, false for not display
     */
    public boolean isDisplayParterAddressInput() {
        return QuoteOutputSubmissionUtil.isDisplayParterAddressInput(quote);
    }
    public String getGovEntityIndCodeDesc() {
        return adapter.getGovEntityIndCodeDesc();
    }
    public boolean isCustOrdBlcked(){
        return adapter.isCustOrdBlcked();
    }

    public String getCvrageGrpCodeDesc() {
        return adapter.getCvrageGrpCodeDesc();
    }

    public Partner getPayer() {
        return payer;
    }

    public String getCustCvrageDesc() {
		return adapter.getCustCvrageDesc();
	}

    public String getInitWebAppBusDscr() {
		return adapter.getInitWebAppBusDscr();
	}

	public String getCurrWebAppBusDscr() {
		return adapter.getCurrWebAppBusDscr();
	}

    public boolean isHasRelationshap() {
		return hasRelationshap;
	}

	public boolean isNewCustomer() {
		return isNewCustomer;
	}
	public boolean isDoSpecialBidCommonInit(){
        return true;
    }
	public boolean isExistCustomer() {
        return isExistCustomer;
    }
	
    /**
     * Get the ship to address radio button checked status.
     * result: 0 or 1
     * @return int
     */
    public int getShipToChoice(){
    	return super.getQuote().getDefaultCustAddr().getShipToOption();
    }
    
    /**
     * Get the install at address radio button checked status.
     * result 0,1 or 2
     * @return int
     */
	public int getInstallAtChoice() {
		int installAtChoice = super.getQuote().getDefaultCustAddr().getInstallAtOption();
		if (this.getShipToChoice() != 0 && installAtChoice == 0)
			installAtChoice = 1;
		return installAtChoice;
	}
	
	/**
	 * Get new ship to address url params from view bean.
	 * @return String
	 */
    public String getNewShipAddrBtnParams() {
        return adapter.getNewAddrBtnParams("0");
    }
    
	/**
	 * Get new install at address url params from view bean.
	 * @return String
	 */
    public String getNewInstallAddrBtnParams() {
        return adapter.getNewAddrBtnParams("1");
    }
    
    public String getSubmittedChangeUrl(String addressType) {
    	return adapter.getSubmittedUrl(addressType);
    }
    
	public String getSoldToCustomerAddress(){
		return super.getSoldToCustomerAddress(adapter);
	}
	
	public String getSoldToCompanyAddress(){
		return super.getSoldToCompanyAddress(adapter);
	}
	
    public String getNewShipToCustomerAddress(){
    	return super.getNewShipToCustomerAddress(adapter);
    }
    
    public String getNewShipToCompanyAddress(){
    	return super.getNewShipToCompanyAddress(adapter);
    }
    
    public String getDisplayAddressParms() throws JSONException {
    	JSONObject obj = new JSONObject();
    	
    	obj.put("jadeAction", "DISPLAY_APPLIANCE_ADDRESS");
    	obj.put(ParamKeys.PARAM_QUOTE_NUM, header.getWebQuoteNum() );
    	obj.put(ParamKeys.PARAM_COUNTRY, header.getCountry().getCode3() );
    	obj.put(ParamKeys.PARAM_LINE_OF_BUSINESS, header.getLob().getCode() );
    	obj.put(ParamKeys.PARAM_CUSTNUM, header.getSoldToCustNum() );
    	obj.put(ParamKeys.PARAM_IS_SBMT_QT, "true");
    	obj.put("readOnly", true);
    	// addressType set at runtime
    	
    	return obj.toString();
    }
    
}
