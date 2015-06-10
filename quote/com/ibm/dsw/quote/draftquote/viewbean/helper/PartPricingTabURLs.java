package com.ibm.dsw.quote.draftquote.viewbean.helper;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.dsw.quote.mail.config.MailActionKeys;
import com.ibm.dsw.quote.partdetail.config.PartDetailsActionKeys;
import com.ibm.dsw.quote.partdetail.config.PartDetailsParamKeys;
import com.ibm.dsw.quote.ps.config.PartSearchActionKeys;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.dsw.quote.pvu.config.VUActionKeys;
import com.ibm.dsw.quote.pvu.config.VUDBConstants;
import com.ibm.dsw.quote.pvu.config.VUParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>PartPricingTabURLs</code> class .
 *
 *
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 *
 * Creation date: Apr 27, 2007
 */
public class PartPricingTabURLs implements Serializable {
	protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    private Quote quote;

    private String pricingCountryCode;

    private String pricingCurrencyCode;

	public String jadeActionURL = "";

    public String partDetailURL = "";

    public String partOrderDetailURL = "";


    public String addLicensePartURL = "";

    public String findPartsURL = "";

    public String browsePartsURL = "";

    public String browseHostedServicesURL = "";

    public String calculateEquityCurveURL = "";
    
    public String reviewUpdateOmittedRenewalLineUrl = "";
    
    public String resetLineToRsvpSrpUrl = "";

    public String reviewSubmitOmittedRenewalLineUrl = "";

    public String recalculateGrowthDelegationUrl = "";

    public String configPVUURL1 = "";

    public String pvuDetailURL1 = "";

    public String configPVUURL2;

    public String pvuDetailURL2;

    public String showLIDetailURL;

    public String hideLIDetailURL;

    public String showBRDetailURL;

    public String hideBRDetailURL;
    
    //Appliance#99
    public String postLineItemCRADURL;

    public String updateLIDateURL;

    public String pvuCalculatorURL;

    public String pvuURL;

    public String popUpURL;

    public String rnwQuoteURL;

    public String puvItemParam = VUParamKeys.LINE_ITEM;

    public String puvConfigParam = VUParamKeys.CONFIG_NUM;

    public String convertUrl;

    public String addDupLineItemURL;

    public String deleteLineItemURL;

    public String removeConfgrtnURL;

    public String displayAction = "";

    private String liDetailAction = "";

    private String jadeAction = "";

    private String findPartsAction = "";

    private String partDetailLink = "";

    private String partOrderDetailLink = "";

    private String addLicensePartAction = "";

    private String browsePartAction = "";

    private String browseHostedServicesAction = "";

    private String calculateEquityCurveAction = "";
    
    private String reviewUpdateOmittedLineItemAction = "";
    
    private String setLineToRsvpSrpAction = "";
    
    private String recalculateGrowthDelegationAction = "";
    
    private String displayOmittedLineItemAction = "";
    
    private String displaySubmittedOmittedLineItemAction = "";

    private String mailQuoteAction = "";

    private String partDetailParam = "";

    private String partSearchParams = "";

    private String configPVUParams1 = "";

    private String configPVUParams2 = "";

    private String pvuDetailParams1 = "";

    private String pvuDetailParams2 = "";

    private String showLIDetailsParams = "";

    private String hideLIDetailsParams = "";

    private String showBRDetailsParams = "";

    private String hideBRDetailsParams = "";

    private String updateLIDateParams = "";

    private String cpPrcIncrseParams = "";

    public String ovrrdTranLevelUrl = "";

    public String applyDiscountUrl = "";

    public String applyOfferUrl = "";

    public String clearOfferUrl = "";

    public String params = "";

    public String applyPartnerDiscountUrl = "";

    public String ytyGrowthDelegationUrl = "";


    public String applyPrcBandOvrrdUrl = "";

    public String cpPrcIncrseUrl = "";

    public String cognosAction = "";

    public String cognosActionUrl = "";

    public String changeBillingFrequencyUrl = "";

    public String exEntireCongfigEndDateUrl = "";
    
    //Appliance#99
    public String updateLineItemCRADAction = "";

    //monthly
    public String removeAllPartsURL="";
    
	public String getRemoveAllPartsURL() {
		return removeAllPartsURL;
	}

	public PartPricingTabURLs(Quote quote) {
    	setQuote(quote);

    	String lobCode = quote.getQuoteHeader().getLob().getCode();
    	pricingCountryCode = quote.getQuoteHeader().getPriceCountry().getCode3();
    	pricingCurrencyCode = quote.getQuoteHeader().getCurrencyCode();

        displayAction = DraftQuoteActionKeys.DISPLAY_PARTS_PRICE_TAB;
        liDetailAction = SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_PART_PRICE_TAB;

        jadeAction = DraftQuoteActionKeys.POST_PART_PRICE_TAB;
        findPartsAction = PartSearchActionKeys.DISPLAY_PARTSEARCH_FIND;
        partDetailLink = HtmlUtil.getURLForAction(PartDetailsActionKeys.DISPLAY_PART_DETAILS);
        partOrderDetailLink = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_ORDERED_ITEM_DETAIL);
        addLicensePartAction = PartSearchActionKeys.DISPLAY_RELATED_OR_REPLACEMENT_PARTS;
        browsePartAction = PartSearchActionKeys.DISPLAY_PARTSEARCH_BROWSE_RESULT;
        browseHostedServicesAction = PartSearchActionKeys.DISPLAY_PARTSEARCH_BROWSE_SERVICES_RESULT;
        calculateEquityCurveAction = PartSearchActionKeys.POST_CALCULATE_EQUITY_CURVE;
        reviewUpdateOmittedLineItemAction = DraftQuoteActionKeys.CREATE_OMIT_RENEWAL_LINE;
        setLineToRsvpSrpAction = DraftQuoteActionKeys.SET_LINE_TO_RSVP_SRP;
        recalculateGrowthDelegationAction = DraftQuoteActionKeys.RECALCULATE_GROWTH_DELEGATION;
        displayOmittedLineItemAction = DraftQuoteActionKeys.DISPLAY_OMITTED_LINEITEM;
        displaySubmittedOmittedLineItemAction = DraftQuoteActionKeys.DISPLAY_SUBMITTED_OMITTED_LINEITEM;
        mailQuoteAction = MailActionKeys.DISPLAY_SEND_MAIL;
        cognosAction = DraftQuoteActionKeys.RETRIEVE_PART_FROM_COGNOS;
        //Appliance#99
        updateLineItemCRADAction = SubmittedQuoteActionKeys.UPDATE_LINE_ITEM_CRAD_DATE;

        partDetailParam = "&displayType=PART_DETAILS";
        partSearchParams = ",country=" + pricingCountryCode
                          + ",currency=" + pricingCurrencyCode
                          + ",lob=" + lobCode
                          + ",audience=" + quote.getQuoteHeader().getAudCode().trim()
                          + ",acqrtnCode=" + (quote.getQuoteHeader().getAcqrtnCode() == null?
                                               "" : quote.getQuoteHeader().getAcqrtnCode())
                          + ",quoteNum=" + quote.getQuoteHeader().getWebQuoteNum()
                          + ",progMigrationCode=" +quote.getQuoteHeader().getProgMigrationCode()
                          + ",quoteFlag=" +getQuoteFlag()
                          + ",renewal=" + VUDBConstants.RENEWAL_QUOTE.equals(quote.getQuoteHeader().getQuoteTypeCode().trim());
        if(null != quote.getCustomer()){
        	partSearchParams += ",customerNumber=" + quote.getCustomer().getCustNum();
        	partSearchParams += ",sapContractNum=" + quote.getCustomer().getSapContractNum();
        }
        configPVUParams1 = ",mandatoryKey=true";
        configPVUParams2 = ",mandatoryKey=false";
        pvuDetailParams1 = ",displayDetailKey=true";
        pvuDetailParams2 = ",displayDetailKey=false";

        showLIDetailsParams = SubmittedQuoteParamKeys.LI_DETAIL_FLAG + "=true," + SubmittedQuoteParamKeys.PARAM_QUOTE_NUM
                + "=" + quote.getQuoteHeader().getWebQuoteNum();
        hideLIDetailsParams = SubmittedQuoteParamKeys.LI_DETAIL_FLAG + "=false,"
                + SubmittedQuoteParamKeys.PARAM_QUOTE_NUM + "=" + quote.getQuoteHeader().getWebQuoteNum();

        showBRDetailsParams = SubmittedQuoteParamKeys.BR_DETAIL_FLAG + "=true," + SubmittedQuoteParamKeys.PARAM_QUOTE_NUM
        		+ "=" + quote.getQuoteHeader().getWebQuoteNum();
        hideBRDetailsParams = SubmittedQuoteParamKeys.BR_DETAIL_FLAG + "=false,"
        		+ SubmittedQuoteParamKeys.PARAM_QUOTE_NUM + "=" + quote.getQuoteHeader().getWebQuoteNum();

        updateLIDateParams = SubmittedQuoteParamKeys.UPDATE_LINE_ITEM_DATE_FLAG + "=true,"
                + SubmittedQuoteParamKeys.PARAM_QUOTE_NUM + "=" + quote.getQuoteHeader().getWebQuoteNum();

        pvuCalculatorURL = HtmlUtil.getIntegratedPVUUrl();

        popUpURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.OVERRIDE_DATE);
        partDetailURL = partDetailLink + partDetailParam;
        partOrderDetailURL = partOrderDetailLink;
        String pvuPortalParam = QuoteConstants.PVU_PARAM_PORTAL_INTERNAL;
        if (quote.getQuoteHeader().isPGSQuote()) {
        	pvuPortalParam = QuoteConstants.PVU_PARAM_PORTAL_EXTERNAL;
        }
        pvuURL = genRedirectURLsAndParams(jadeAction, pvuCalculatorURL + "?" + QuoteConstants.PVU_PARAM + "="
                + getAppPvuConfigUrl() + "&" + QuoteConstants.PVU_PARAM_PORTAL + "="
                + pvuPortalParam, null);

        jadeActionURL = genLinkURLsAndParams(jadeAction, displayAction, null);
        cognosActionUrl = genLinkURLsAndParams(cognosAction, displayAction, null);
        findPartsURL = genLinkURLsAndParams(jadeAction, findPartsAction + partSearchParams, null);
        browsePartsURL = genLinkURLsAndParams(jadeAction, browsePartAction + partSearchParams, null);
        browseHostedServicesURL = genLinkURLsAndParams(jadeAction, browseHostedServicesAction + partSearchParams, null);
        calculateEquityCurveURL = genLinkURLsAndParams(calculateEquityCurveAction, displayAction, null);
        reviewUpdateOmittedRenewalLineUrl = genLinkURLsAndParams(reviewUpdateOmittedLineItemAction, displayOmittedLineItemAction, null);
        resetLineToRsvpSrpUrl = genLinkURLsAndParams(setLineToRsvpSrpAction, displayAction, null);
        reviewSubmitOmittedRenewalLineUrl = genLinkURLsAndParams(displaySubmittedOmittedLineItemAction,null, null);
        recalculateGrowthDelegationUrl = genLinkURLsAndParams(recalculateGrowthDelegationAction, displayAction, null);
        configPVUURL1 = genLinkURLsAndParams(jadeAction, displayAction, configPVUParams1);
        configPVUURL2 = genLinkURLsAndParams(jadeAction, displayAction, configPVUParams2);
        pvuDetailURL1 = genLinkURLsAndParams(jadeAction, displayAction, pvuDetailParams1);
        pvuDetailURL2 = genLinkURLsAndParams(jadeAction, displayAction, pvuDetailParams2);

        showLIDetailURL = genLinkURLsAndParams(liDetailAction, null, showLIDetailsParams);
        hideLIDetailURL = genLinkURLsAndParams(liDetailAction, null, hideLIDetailsParams);

        showBRDetailURL = genLinkURLsAndParams(liDetailAction, null, showBRDetailsParams);
        hideBRDetailURL = genLinkURLsAndParams(liDetailAction, null, hideBRDetailsParams);
        
        //Appliance#99
        //Modification by Wang Jie,pass action2
        postLineItemCRADURL = genLinkURLsAndParams(updateLineItemCRADAction,null,null);

        updateLIDateURL = genLinkURLsAndParams(liDetailAction, null, updateLIDateParams);

        convertUrl = genLinkURLsAndParams(jadeAction, DraftQuoteActionKeys.CONVERT_QUOTE, null);
        addDupLineItemURL = genLinkURLsAndParams(DraftQuoteActionKeys.ADD_DUP_LINE_ITEM, displayAction, null);

        deleteLineItemURL = genLinkURLsAndParams(DraftQuoteActionKeys.DELETE_LINE_ITEM, displayAction, null);

        removeConfgrtnURL = genLinkURLsAndParams(DraftQuoteActionKeys.REMOVE_CONFIGURATION, displayAction, null);

        String ovrrdTranLevelAction = DraftQuoteActionKeys.OVRRD_TRAN_LEVEL_CODE_ACTION;
        ovrrdTranLevelUrl = genLinkURLsAndParams(ovrrdTranLevelAction, displayAction, "");

        String applyDiscountAction = DraftQuoteActionKeys.APPLY_DISCOUNT_ACTION;
        applyDiscountUrl = genLinkURLsAndParams(applyDiscountAction, displayAction, "");

        String applyOfferAction = DraftQuoteActionKeys.APPLY_OFFER_ACTION;
        applyOfferUrl = genLinkURLsAndParams(applyOfferAction, displayAction, "");

        String clearOfferAction = DraftQuoteActionKeys.CLEAR_OFFER_ACTION;
        clearOfferUrl = genLinkURLsAndParams(clearOfferAction, displayAction, "");

        String applyPartnerDiscountAction = DraftQuoteActionKeys.APPLY_PARTNER_DISCOUNT_ACTION;
        applyPartnerDiscountUrl = genLinkURLsAndParams(applyPartnerDiscountAction, displayAction, "");

        ytyGrowthDelegationUrl = genLinkURLsAndParams(DraftQuoteActionKeys.YTY_GROWTH_DELEGATION_ACTION, displayAction, ""); //YTY Growth delegation actoin

        applyPrcBandOvrrdUrl = genLinkURLsAndParams(DraftQuoteActionKeys.APPLY_PRC_BAND_OVRRD, displayAction, "");

        cpPrcIncrseParams = SubmittedQuoteParamKeys.APPLY_OFFER_PRICE_FLAG + "=true,"
                            + SubmittedQuoteParamKeys.PARAM_QUOTE_NUM + "=" + quote.getQuoteHeader().getWebQuoteNum();
        cpPrcIncrseUrl = genLinkURLsAndParams(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_PART_PRICE_TAB, null, cpPrcIncrseParams);

        changeBillingFrequencyUrl = genLinkURLsAndParams(DraftQuoteActionKeys.CHANGE_BILLING_FREQUENCY_ACTION, displayAction, "");

        exEntireCongfigEndDateUrl = genLinkURLsAndParams(DraftQuoteActionKeys.ADD_ETR_PARTS_TO_SQ , displayAction ,"");
        
        removeAllPartsURL = genLinkURLsAndParams(DraftQuoteActionKeys.REMOVE_ALL_PARTS, displayAction, null);
	}

    public String getPartParams(String partNum,String delimiter) {
    	String lobCode = quote.getQuoteHeader().getLob().getCode();
    	String params = "";
    	boolean isSubmmited = quote.getQuoteHeader().isSubmittedQuote();

    	if (delimiter.equals("&")) {
    		params = "partNumber=" + partNum
			         + delimiter + "countryCode=" + pricingCountryCode
			         + delimiter + "currencyCode=" + pricingCurrencyCode
			         + delimiter + "lob=" + lobCode
			         + delimiter + "quoteNum=" + quote.getQuoteHeader().getWebQuoteNum();
    	}
    	else if (delimiter.equals(",")) {
    		params = "partNumber=" + partNum
    		         + delimiter + "country=" + pricingCountryCode
    		         + delimiter + "currency=" + pricingCurrencyCode
    		         + delimiter + "lob=" + lobCode
			         + delimiter + "quoteNum=" + quote.getQuoteHeader().getWebQuoteNum();
    	}

    	if (isSubmmited) {
    	    params = params +delimiter+ "submitted=true";

    	}

    	return params;
    }

    public String getPartDetailParams(String partNum, String delimiter){
        String lobCode = quote.getQuoteHeader().getLob().getCode();
        QuoteLineItem lineItem = getQuoteLineItem(partNum);
        boolean showEventBaseBiling = false;

        if (lineItem.isSaasSetUpPart()) {
            showEventBaseBiling = true;
        }

        params = "partNumber=" + partNum + delimiter 
               + "quoteNum=" + quote.getQuoteHeader().getWebQuoteNum() + delimiter 
               + "lob=" + lobCode + delimiter 
               + PartDetailsParamKeys.SHOW_EVENT_BASE_BILING + "=" + showEventBaseBiling;

		return params;
    }

    /**
     * DOC get QuoteLineItem by partNum
     * 
     * @param partNum
     * @return
     */
    @SuppressWarnings("rawtypes")
    private QuoteLineItem getQuoteLineItem(String partNum) {
        Iterator iterator = quote.getLineItemList().iterator();
        while (iterator.hasNext()) {
            QuoteLineItem item = (QuoteLineItem) iterator.next();
            if(item.getPartNum().equals(partNum)){
                return item;
            }
        }
        return null;
    }

    public String getPartOrderDetailParams(QuoteLineItem qli, String delimiter){
        String webQuoteNum  = qli.getQuoteNum();
        String destSeqNum = String.valueOf(qli.getDestSeqNum());

		params = "quoteNum=" + webQuoteNum + delimiter
		       + "destSeqNum=" + destSeqNum;

		return params;
    }

    /**
     * @return
     */
    private String getAppPvuConfigUrl() {
        return HtmlUtil.getQuoteAppUrl() + HtmlUtil.getURLForAction(VUActionKeys.APPLY_PVU_CONFIG);
    }

    protected String genLinkURLsAndParams(String targetAction, String secondAction, String targetParams) {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
        StringBuffer sb = new StringBuffer();

        sb.append(actionKey).append("=").append(targetAction);
        if (StringUtils.isNotBlank(secondAction))
            sb.append("," + appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY) + "=").append(
                    secondAction);
        if (StringUtils.isNotBlank(targetParams))
            sb.append(",").append(targetParams);
        return sb.toString();
    }

    protected String genRedirectURLsAndParams(String targetAction, String secondAction, String targetParams) {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
        StringBuffer sb = new StringBuffer();

        sb.append(actionKey).append("=").append(targetAction);
        if (StringUtils.isNotBlank(secondAction))
            sb.append(",redirectURL=").append(secondAction);
        if (StringUtils.isNotBlank(targetParams))
            sb.append(",").append(targetParams);
        return sb.toString();
    }

    /**
     * @return Returns the addDupLineItemURL.
     */
    public String getAddDupLineItemURL() {
        return addDupLineItemURL;
    }

    /**
     * @return
     * String deleteLineItemURL
     */
    public String getDeleteLineItemURL() {
        return deleteLineItemURL;
    }

    public String getRemoveConfgrtnURL() {
    	return removeConfgrtnURL;
    }

    /**
     * @return Returns the addLicensePartURL.
     */
    public String getAddLicensePartURL(QuoteLineItem qli) {
        if (qli.isObsoletePart()) {
            String addLicensePartParam = ",retrievalType=" + PartSearchParamKeys.GET_REPLACEMENT_PARTS;
            addLicensePartURL = genLinkURLsAndParams(jadeAction, addLicensePartAction + addLicensePartParam, null);
        } else {
            String addLicensePartParam = ",retrievalType=" + PartSearchParamKeys.GET_RELATED_PARTS_LIC;
            addLicensePartURL = genLinkURLsAndParams(jadeAction, addLicensePartAction + addLicensePartParam, null);
        }

        return addLicensePartURL;
    }

    public String getEOLReplacementPartURL(QuoteLineItem qli) {
        String param = ",retrievalType=" + PartSearchParamKeys.GET_REPLACEMENT_PARTS;
        String replacementPartURL = genLinkURLsAndParams(jadeAction, addLicensePartAction + param, null);
        // append country and currency
        replacementPartURL += "," + getPartParams(qli.getPartNum(),",");
        // append quoteNumber, seqNum
        replacementPartURL += ",seqNum="+qli.getSeqNum()+","+"replacementFlag=true";
        return replacementPartURL;
    }

    public String getAddiOvrYearGoUrl(QuoteLineItem qli) {
    	String addiOverYearGoUrl = "";
        String partNum = qli.getPartNum().trim();
        String seqNum = String.valueOf(qli.getSeqNum());
        int addMaintCovQty = qli.getAddtnlMaintCvrageQty();

        String addiCovParam = DraftQuoteParamKeys.PART_NUM + "=" + partNum + "," +
					       	      DraftQuoteParamKeys.SEQ_NUM + "=" + seqNum;
        String addiCovGoAction = DraftQuoteActionKeys.CHANGE_ADDITIONAL_MAINT_ACTION;

        addiOverYearGoUrl = genLinkURLsAndParams(addiCovGoAction, displayAction, addiCovParam);

        return addiOverYearGoUrl;
    }

    public String getRnwQuoteSearchUrl(QuoteLineItem qli) {

    	String rnwQuoteNum = "";
    	if(quote.getQuoteHeader().isSubmittedQuote()){
    	    rnwQuoteNum = qli.getRenewalQuoteNum() == null? "" : qli.getRenewalQuoteNum();
    	} else {
            //for draft quote, renewal quote num will be removed if the original RQ is closed, or this part is deleted
    	    rnwQuoteNum = qli.getPartDispAttr().getRenwlQuoteNum()==null?"":qli.getPartDispAttr().getRenwlQuoteNum();
    	}

        StringBuffer goBackURL = new StringBuffer();
        goBackURL.append(ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ActionHandlerKeys.JADE_ACTION_KEY)).append("=").append(DraftQuoteActionKeys.DISPLAY_PARTS_PRICE_TAB);

        String baseURL = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ApplicationProperties.RENEWAL_QUOTE_DETAIL_URL);
        StringBuffer origRenewQuoteDetailURL = new StringBuffer(baseURL);
        HtmlUtil.addURLParam(origRenewQuoteDetailURL, DraftRQParamKeys.PARAM_RPT_QUOTE_NUM, rnwQuoteNum);
        HtmlUtil.addURLParam(origRenewQuoteDetailURL, DraftRQParamKeys.PARAM_RPT_P1, goBackURL.toString());

        return origRenewQuoteDetailURL.toString();
    }

    private String getQuoteFlag(){
    	String quoteFlag = DraftQuoteParamKeys.PARAM_QUOTE_FLAG_EMPTY ;
    	List softwareLineItems = quote.getMasterSoftwareLineItems();
    	List saasLineItems = quote.getSaaSLineItems();
    	if(softwareLineItems != null && softwareLineItems.size()>0){
    		quoteFlag = DraftQuoteParamKeys.PARAM_QUOTE_FLAG_SOFTWARE;
    	} else if (saasLineItems != null && saasLineItems.size() >0){
    		quoteFlag = DraftQuoteParamKeys.PARAM_QUOTE_FLAG_SAAS;
    	}

    	return quoteFlag;
    }

    public String getEditProvisngFormUrl(boolean isPgsAppl, boolean isSubmittedQuote) {
        StringBuffer url = new StringBuffer();
        String returnUrl = "";
        if(isPgsAppl){
            returnUrl = QuoteCommonUtil.getQtAppPGSEnvrnmUrl();
        }else{
            returnUrl = QuoteCommonUtil.getQtAppSQOEnvrnmUrl();
        }
        if (isSubmittedQuote) {
        	returnUrl += "?jadeAction=DISPLAY_SUBMITTEDQT_PART_PRICE_TAB&quoteNum="+quote.getQuoteHeader().getWebQuoteNum();
        	url.append(genLinkURLsAndParams("REDIRECT_PROVISIONING", "", null));
        }else{
        	returnUrl += "?jadeAction=DISPLAY_PART_PRICE_TAB";
        	url.append(genLinkURLsAndParams(jadeAction, "REDIRECT_PROVISIONING", null));
        }

        url.append(",returnUrl=");
        url.append(returnUrl);

        return url.toString();
    }

    public String getOvrrdTranLevelUrl() {
        return ovrrdTranLevelUrl;
    }

    /**
     * @return Returns the applyDiscountUrl.
     */
    public String getApplyDiscountUrl() {
        return applyDiscountUrl;
    }

    /**
     * @return Returns the applyOfferUrl.
     */
    public String getApplyOfferUrl() {
        return applyOfferUrl;
    }

    /**
     * @return Returns the clearOfferUrl.
     */
    public String getClearOfferUrl() {
        return clearOfferUrl;
    }

    /**
     * @return Returns the applyDiscountUrl.
     */
    public String getApplyPartnerDiscountUrl() {
        return applyPartnerDiscountUrl;
    }

    /**
     * @return Returns the browsePartsURL.
     */
    public String getBrowsePartsURL() {
        return browsePartsURL;
    }

    public String getBrowseHostedServicesURL() {
    	return browseHostedServicesURL;
    }


    public String getCalculateEquityCurveURL() {
		return calculateEquityCurveURL;
	}


    public String getReviewUpdateOmittedRenewalLineUrl() {
		return reviewUpdateOmittedRenewalLineUrl;
	}
    
    
    
    public String getResetLineToRsvpSrpUrl() {
    	
        /*String partNum = qli.getPartNum().trim();
        String seqNum = String.valueOf(qli.getSeqNum());
        String patternUrl = ApplicationProperties.getInstance().getQuoteBaseURL();
        String params = "setLineToRsvpSrpId="+ partNum+"_"+seqNum+"_OVERRIDE_UNIT_PRC"+"&amp;webQuoteNum="+webQuoteNum;
        setLineToRsvpSrpUrl = patternUrl + "?" + ParamKeys.PARAM_RPT_JADE_ACTION + "="+setLineToRsvpSrpAction;
        return setLineToRsvpSrpUrl+"&amp;"+params;
		*/
    	return resetLineToRsvpSrpUrl;
	}

	public String getReviewSubmitOmittedRenewalLineUrl() {
		return reviewSubmitOmittedRenewalLineUrl;
	}

    public String getRecalculateGrowthDelegationUrl() {
		return recalculateGrowthDelegationUrl;
	}
    
	/**
     * @return Returns the configPVUURL1.
     */
    public String getConfigPVUURL1() {
        return configPVUURL1;
    }

    /**
     * @return Returns the configPVUURL2.
     */
    public String getConfigPVUURL2() {
        return configPVUURL2;
    }

    /**
     * @return Returns the convertUrl.
     */
    public String getConvertUrl() {
        return convertUrl;
    }

    /**
     * @return Returns the displayAction.
     */
    public String getDisplayAction() {
        return displayAction;
    }

    /**
     * @return Returns the findPartsURL.
     */
    public String getFindPartsURL() {
        return findPartsURL;
    }

    /**
     * @return Returns the hideLIDetailURL.
     */
    public String getHideLIDetailURL() {
        return hideLIDetailURL;
    }

    /**
     * @return Returns the hideBRDetailURL.
     */
    public String getHideBRDetailURL() {
        return hideBRDetailURL;
    }

    /**
     * @return Returns the jadeActionURL.
     */
    public String getJadeActionURL() {
        return jadeActionURL;
    }

    /**
     * @return Returns the liDetailAction.
     */
    public String getLiDetailAction() {
        return liDetailAction;
    }

    /**
     * @return Returns the partDetailURL.
     */
    public String getPartDetailURL() {
        return partDetailURL;
    }

    /**
     * @return Returns the popUpURL.
     */
    public String getPopUpURL() {
        return popUpURL;
    }

    /**
     * @return Returns the puvConfigParam.
     */
    public String getPuvConfigParam() {
        return puvConfigParam;
    }

    /**
     * @return Returns the puvItemParam.
     */
    public String getPuvItemParam() {
        return puvItemParam;
    }

    /**
     * @return Returns the pvuDetailURL1.
     */
    public String getPvuDetailURL1() {
        return pvuDetailURL1;
    }

    /**
     * @return Returns the pvuDetailURL2.
     */
    public String getPvuDetailURL2() {
        return pvuDetailURL2;
    }

    /**
     * @return Returns the pvuURL.
     */
    public String getPvuURL() {
        return pvuURL;
    }

    /**
     * @return Returns the showLIDetailURL.
     */
    public String getShowLIDetailURL() {
        return showLIDetailURL;
    }

    /**
     * @return Returns the showBRDetailURL.
     */
    public String getShowBRDetailURL() {
        return showBRDetailURL;
    }
    
    public String getpostLineItemCRADURL() {
    	return postLineItemCRADURL;
    }

    /**
     * @return Returns the updateLIDateURL.
     */
    public String getUpdateLIDateURL() {
        return updateLIDateURL;
    }

    /**
     * @return Returns the pvuCalculatorURL.
     */
    public String getPvuCalculatorURL() {
        return pvuCalculatorURL;
    }

	/**
	 * @return Returns the quote.
	 */
	public Quote getQuote() {
		return quote;
	}
	/**
	 * @param quote The quote to set.
	 */
	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	public String getApplyPrcBandOvrrdUrl(){
	    return applyPrcBandOvrrdUrl;
	}

	public String getCpPrcIncrseUrl(){
	    return cpPrcIncrseUrl;
	}

	public String getCognosActionUrl() {
		return cognosActionUrl;
	}

	public String getYtyGrowthDelegationUrl() {
		return ytyGrowthDelegationUrl;
	}

	public void setYtyGrowthDelegationUrl(String ytyGrowthDelegationUrl) {
		this.ytyGrowthDelegationUrl = ytyGrowthDelegationUrl;
	}

	public String getChangeBillingFrequencyUrl() {
		return changeBillingFrequencyUrl;
	}
}