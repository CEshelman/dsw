package com.ibm.dsw.quote.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.OmitRenewalLine;
import com.ibm.dsw.quote.common.domain.PartDisplayAttr;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.PartnerFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.RselCtrldDistribtn;
import com.ibm.dsw.quote.common.domain.SpecialBidInfoFactory;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.util.validator.PartsPricingConfigurationValidatorContainer;
import com.ibm.dsw.quote.common.util.validator.QuoteLineItemValidatorContainer;
import com.ibm.dsw.quote.draftquote.util.sort.QuoteBaseComparator;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.domain.SubmittedQuoteAccess;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>SalesQuoteValidationRule</code> class .
 *
 *
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 *
 * Creation date: Apr 25, 2007
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SalesQuoteValidationRule extends QuoteValidationRule {

    /**
     * @param user
     * @param quote
     * @param cookie
     */
    public SalesQuoteValidationRule(QuoteUserSession user, Quote quote, Cookie cookie) {
        super(quote, user, cookie);
    }


	protected boolean isSVPLvlOvrrd() {
        if (quote == null || quote.getQuoteHeader() == null)
            return true;
        QuoteHeader header = quote.getQuoteHeader();
        return StringUtils.isNotEmpty(header.getOvrrdTranLevelCode());
    }

	protected void validateSubmitByEvaluator()
	{
		if ( !quote.getQuoteHeader().isSubmitByEvaluator() )
		{
			return;
		}
		if ( quote.getSpecialBidInfo() == null )
		{
			try {
				quote.setSpecialBidInfo(SpecialBidInfoFactory.singleton().findByQuoteNum(quote.getQuoteHeader().getWebQuoteNum()));
			} catch (TopazException e) {
				logContext.error(this, "Get special bid info for evaluator submit validate: " + quote.getQuoteHeader().getWebQuoteNum() + "; "+ e.getMessage());
				logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
		}

		String evalAction = quote.getSpecialBidInfo().getEvaltnAction();
		if ( StringUtils.isBlank(evalAction)
				|| !(StringUtils.equals(evalAction, QuoteConstants.EVAL_SELECT_OPTION_RETURN) || StringUtils.equals(evalAction, QuoteConstants.EVAL_SELECT_OPTION_SUBMIT)) )
		{
			validateResult.put(QuoteCapabilityProcess.EVAL_ACTION_NOT_INPUT, Boolean.TRUE);
		}
		else if ( QuoteConstants.EVAL_SELECT_OPTION_RETURN.equals(quote.getSpecialBidInfo().getEvaltnAction())
				&& StringUtils.isBlank(quote.getSpecialBidInfo().getEvaltnComment()) )
		{
			validateResult.put(QuoteCapabilityProcess.EVAL_COMMENT_NOT_INPUT, Boolean.TRUE);
		}
	}

    public Map validateSubmissionAsFinal() throws QuoteException {
    	validateTou(false);
    	
        //validate offer price
        if(quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag()){

            validateOfferPrice();

            return validateResult;
        }
        //validate expiration date extension
        if(quote.getQuoteHeader().isExpDateExtendedFlag()){
        	
            return validateResult;
        }        //validate if meet bid iteration requirements when is bid iteration quote
        if(quote.getQuoteHeader().isBidIteratnQt()
        		&& !QuoteConstants.LOB_SSP.equals(quote.getQuoteHeader().getLob())
                && QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode())){

            validateBidIteration();

            return validateResult;
        }
        //Validate current quote qualifies for SaaS streamlined add-on/trade-up approval
        validateStreamLine();

        //Validate SSP quote
        validateSSPquote();
        //Validate extension service date
        validateServiceDate();

        validateQuoteStartDate();
        // Exipired Date must be within the number of Quote Active Days
        validateQuoteExpiredDate();
        // Validate estimated order date for add-on/trade-up/co-term quote
        validateEstmtdOrdDate();
        // Validate customer requested arrival date(CRAD)
        validateCRAD();

        // Validate appliance address part CRAD,Appliance#99
        validateLineItemCRAD();
        
        validateRnwalMdlC();

        validateRemingTermForRewalMdl();

        validateProratePart();
        // customer is not selected or created
        Customer customer = quote.getCustomer();
        if (!isCustomerSelected()) {
            validateResult.put(QuoteCapabilityProcess.CUSTOMER_NOT_SELECTED, Boolean.TRUE);
        }

        // If a contract was selected for an existing customer, the contract
        // must be active
        if (!isActiveIfContractSelected()) {
            validateResult.put(QuoteCapabilityProcess.CONTRACT_NOT_ACTIVE, Boolean.TRUE);
        } else {
            // All fields of contact must be filled in
            validateContactInfo();
        }

        // Fulfillment source must be set
        if (!isFulfillmentSrcSet()) {
            validateResult.put(QuoteCapabilityProcess.FULFILLMENT_SRC_NOT_SET, Boolean.TRUE);
        }

        if (!isQuoteClassfctnCodeSelected()) {
            validateResult.put(QuoteCapabilityProcess.QT_CLASSFCTN_NOT_SET, Boolean.TRUE);
        }

        if (!isQuoteOemAgreementTypeSelected()) {
            validateResult.put(QuoteCapabilityProcess.QT_OEM_AGREEMENT_TYPE_NOT_SET, Boolean.TRUE);
        }

        if (!isQuoteOemBidTypeSelected()) {
            validateResult.put(QuoteCapabilityProcess.OEM_BID_TYPE_NOT_SET_MSG, Boolean.TRUE);
        }

        validateResellerAndDistributor(validateResult);

        // Every part must have a quantity set
        // Part is a PVU part, and the quantity is 1
        // Every part must be active
        // Every part must have a price
        verifyEveryPart(validateResult);

        // Validate appliance part MTM,Serial Number
        verifyAppliancePart(validateResult);

        QuoteHeader header = quote.getQuoteHeader();
        if (header.isPAEQuote() && header.getHasPARenwlLineItmsFlag()) {
            validateResult.put(QuoteCapabilityProcess.PAE_HAS_RQ_PARTS, Boolean.TRUE);
        }

        // If the line of business is PA and the customer is new, the quote
        // price level must be set
        if (!verifyTransSVPLevel() ) {
            validateResult.put(QuoteCapabilityProcess.PRICE_LEVEL_NOT_SET, Boolean.TRUE);
        }
        //Override price level
        if (isSVPLvlOvrrd()) {
            validateResult.put(QuoteCapabilityProcess.IS_OVERRIDDEN_PRICE_LEVEL, Boolean.TRUE);
        }

        // Brief title must be filled in
        if (!isBriefTitleFilled()) {
            validateResult.put(QuoteCapabilityProcess.BRIEF_TITLE_NOT_FILLED, Boolean.TRUE);
        }
        // Either an opportunity number should be filled in, or an exemption
        // code must be selected
        if (!isOppNumOrExempCodeSet()) {
            validateResult.put(QuoteCapabilityProcess.OPP_NUM_OR_EXEMP_CODE_NOT_SET, Boolean.TRUE);
        }
        // A value of business organization must be selected
        if (!isBusOrgSelected()) {
            validateResult.put(QuoteCapabilityProcess.BUS_ORG_NOT_SELECTED, Boolean.TRUE);
        }
        // In order to submit quote, user should have submitter access level
        if (!audienceHasSubmitterAccess()) {
            validateResult.put(QuoteCapabilityProcess.AUD_CAN_NOT_SUBMIT_QUOTE, Boolean.TRUE);
        }

        if (!checkPAQuoteHasPACust()) {
            validateResult.put(QuoteCapabilityProcess.PA_QUOTE_HAS_NON_PA_CUST, Boolean.TRUE);
        }

        int isValid = isPartnerRegnCntryCrncyValid() ;
        if (isValid == 1) {
            validateResult.put(QuoteCapabilityProcess.CUST_PARTNER_REGION_MISMATCH, Boolean.TRUE);
        }
        if (isValid == 2) {
            validateResult.put(QuoteCapabilityProcess.CUST_PARTNER_COUNTRY_MISMATCH, Boolean.TRUE);
        }
        if (isValid == 3) {
            validateResult.put(QuoteCapabilityProcess.CUST_PAYER_CURRENCY_MISMATCH, Boolean.TRUE);
        }

        validateChannelQtWithSaaSParts();

        validateBackDating(validateResult);

        validateTuscanySWVNRule();

        validateCreditedOrderRebill();

        validateForSaaSQuote();
        
        validateCAByParts();
        
        validateSbscrptnPartsTCV();

        if(quote.getQuoteHeader().isSaasFCTToPAQuote()){
            validateSaasFCTToPAQuote();
        }

	    if(QuoteCommonUtil.isOfferPrcEquals2TotEntldExtPrc(quote)){
	        validateResult.put(QuoteCapabilityProcess.OFFER_PRICE_MISMATCH, Boolean.TRUE);
	    }


	    //Reuse Customer.getGsaStatusFlag for federal customer determination
	    boolean gsaStatusFlag = customer.getGsaStatusFlag();

	    //Appliance part
	    // when Customer is a federal customer (authzn_grp = ZFED in the customer master)
	    // and Not all appliance subscription line items' New/Refurbished flags have been set to new
		if (gsaStatusFlag && (!QuoteCommonUtil.validateApplncPriorPoc(quote))) {
	        validateResult.put(QuoteCapabilityProcess.FEDERAL_CUSTOMERS_MUST_PURCHASE_NEW_SYSTEM_APPLIANCE, Boolean.TRUE);
	    }

		validateGrwthDlgtn();

		validateSubmitByEvaluator();
		
		validateEC();

        validateTermExtension();

        validatePartsPricingConfiguration();

        validateQuoteLineItem();

        validateOmitRenewalLine();

        validateDivestedPart();
        
        validateGOEwithTBD();
        
        // Do not allow quote submission and display an error message 
        // if not all reseller and distributor in the bid have a valid CEID
        validateCEID();
        
        // only sales quote will verify migration flag and renewal flag
        if (!quote.isPgsAppl()){
        	 validateMigratiionAndRenewal();
			validateChnlDiscntOvrridReason();
        }
        
        return validateResult;
    }

    /**
     * Do not allow quote submission and display an error message 
     * if not all reseller and distributor in the bid have a valid CEID
     * @throws QuoteException 
     */
    private void validateCEID() throws QuoteException{
    	QuoteHeader header =  quote.getQuoteHeader();
    	if(header == null){
    		return;
    	}
    	if(!isInPGS() && QuoteCommonUtil.isNeedGOEReview(quote) && quote.getReseller() != null){
    		
    		try {
				if(!PartnerFactory.singleton().checkPartnerCEID(quote.getReseller().getCustNum()))
				{
					validateResult.put(QuoteCapabilityProcess.GOE_QUOTE_WITH_CEID_PARTNER, true);
				}
			} catch (TopazException te) {
				throw new QuoteException(te);
			}
    		
    	}
    	
    }
    /**
     * if quote needs GOE review and partnre is TBD then block quote submission
     * 
     */
    private void validateGOEwithTBD() {
    	QuoteHeader header =  quote.getQuoteHeader();
    	if(header == null){
    		return;
    	}
		if(QuoteCommonUtil.isNeedGOEReview(quote) && header.isTBDQuote()){
			validateResult.put(QuoteCapabilityProcess.GOE_QUOTE_WITH_TBD_PARTNER, true);
		}
	}


	/**
     * DOC Comment method "validateOmitRenewalLine".
     */
    public void validateOmitRenewalLine() throws QuoteException{
        if (QuoteConstants.QUOTE_AUDIENCE_CODE_SQO.equalsIgnoreCase(user.getAudienceCode()) && quote.getQuoteHeader().isOmittedLine()){
            if (QuoteConstants.OMIT_RECALCULATE_N == quote.getQuoteHeader().getOmittedLineRecalcFlag()){
                OmitRenewalLine omitRenewalLine = null;
                try {
                    omitRenewalLine = QuoteLineItemFactory.singleton().getOmittedRenewalLine(quote.getQuoteHeader().getWebQuoteNum());
                } catch (TopazException te) {
                    throw new QuoteException(te);
                }
                if(null != omitRenewalLine && QuoteConstants.OMIT_RECALCULATE_N == omitRenewalLine.getOmittedLineRecalcFlag()){
                    validateResult.put(QuoteCapabilityProcess.OL_UPDATE_NEEDED, Boolean.TRUE);
                }
            }else if (QuoteConstants.OMIT_RECALCULATE_Y == quote.getQuoteHeader().getOmittedLineRecalcFlag()){
                validateResult.put(QuoteCapabilityProcess.OL_CALCULATE_NEEDED, Boolean.TRUE);
            }
            return ;
        // remove all omitted line
        }else if (QuoteConstants.QUOTE_AUDIENCE_CODE_SQO.equalsIgnoreCase(user.getAudienceCode())){
        	try {
                QuoteLineItemFactory.singleton().reomveOmittedRenewalLine(quote.getQuoteHeader().getWebQuoteNum());
            } catch (TopazException te) {
                throw new QuoteException(te);
            }
        }
    }
    
    /**
     * DOC Comment method "validateQuoteLineItem".
     */
    public void validateQuoteLineItem() {
        List lineItemList = quote.getLineItemList();
        if (lineItemList == null || lineItemList.isEmpty()) {
            return;
        }
        Iterator iterator = lineItemList.iterator();
        while (iterator.hasNext()) {
            QuoteLineItem item = (QuoteLineItem) iterator.next();
            QuoteLineItemValidatorContainer.getInstance().validate(quoteLineItemValidatorKeys, quote, item, validateResult, user,
                    cookie, logContext);
        }
    }

    /**
     * DOC Comment method "validatePartsPricingConfiguration".
     */
    public void validatePartsPricingConfiguration() {
        List<PartsPricingConfiguration> configList = quote.getPartsPricingConfigrtnsList();
        for (Iterator<PartsPricingConfiguration> iterator = configList.iterator(); iterator.hasNext();) {
            PartsPricingConfiguration partsPricingConfiguration = iterator.next();
            PartsPricingConfigurationValidatorContainer.getInstance().validate(ppConfigurationValidatorKeys, quote,
                    partsPricingConfiguration,
                    validateResult, user, cookie, logContext);
        }
    }

    /**
     * DOC validate if should display error message for the 'Extend configuration end date' link.
     */
    public void validateTermExtension() {
        ppConfigurationValidatorKeys.add(PartsPricingConfigurationValidatorContainer.TERM_EXTENSION_VALIDATOR);
    }

    /**
	 * 
	 */
    private void validateEC() {
		
		if(QuoteConstants.QUOTE_AUDIENCE_CODE_SQO.equalsIgnoreCase(user.getAudienceCode())
				&& quote.getQuoteHeader().isECEligible()
				&& quote.getQuoteHeader().isECRecalculateFlag()){
			
			//non special bid can pass the quote submission
			if(this.quote.getQuoteHeader().getSpeclBidFlag() != 1){
				return;
			}
			// bid iteration can pass the quote submission
			if (this.quote.getQuoteHeader().isBidIteratnQt()||this.quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag()||this.quote.getQuoteHeader().isExpDateExtendedFlag()){
				return ;
			}
			//check if there is discount applied to EC parts for special bid
			List lineItemList = quote.getLineItemList();

			//no need to validate EC if no parts in quote
			if(lineItemList == null || lineItemList.isEmpty()){
				return;
			}
			
            quoteLineItemValidatorKeys.add(QuoteLineItemValidatorContainer.EC_VALIDATOR);

		}
	}

	/**
	 * Validate software and SaaS quote bid iteration ,return not meet reasons
	 */
    private void validateBidIteration(){
        try {
        	Set<String> errCodeSet = new HashSet<String>();
        	BidIterationRule bidIterationRule = CommonServiceUtil.validateBidIteration(quote);
        	quote.getQuoteHeader().setSaasBidIteratnQtInd(getSaasBidIteratnQtInd(bidIterationRule));
        	quote.getQuoteHeader().setSoftBidIteratnQtInd(bidIterationRule.getSoftwareValidationResult());

        	List<String> softwareList = bidIterationRule.getSoftwareErrorCodeList();
        	List<String> saasList = bidIterationRule.getSaasErrorCodeList();
        	List<String> monthlyList = bidIterationRule.getMonthlyErrorCodeList();
        	
        	Map<String,List<String>> errorCodeList4Map = new HashMap<String, List<String>>();
        	errorCodeList4Map.put("softwareList", softwareList);
        	errorCodeList4Map.put("saasList", saasList);
        	errorCodeList4Map.put("monthlyList", monthlyList);
        	
        	
        	if (quote.getQuoteHeader().hasSoftSLineItem()){
        		this.logContext.debug(this, "web quote["+quote.getQuoteHeader().getWebQuoteNum()+"] has software.");
				if (bidIterationRule.getSoftwareValidationResult() == QuoteConstants.BidIteratnValidationResult.INVALID_BID_ITERATN) {
					errCodeSet.addAll(softwareList);
				}
				
				if (bidIterationRule.getSoftwareValidationResult() != QuoteConstants.BidIteratnValidationResult.NOT_APPLICABLE_BID_ITERATN){
					errorCodeList4Map.clear();
				}
        	} else {
        		if (errorCodeList4Map != null){
        			errorCodeList4Map.remove("softwareList");
        		}
        	}
        	
        	if (quote.getQuoteHeader().hasSaaSLineItem()){
        		this.logContext.debug(this, "web quote["+quote.getQuoteHeader().getWebQuoteNum()+"] has SaaS.");
        		if (saasList!= null && (bidIterationRule.getSaasValidationResult() == QuoteConstants.BidIteratnValidationResult.INVALID_BID_ITERATN)){
        			errCodeSet.addAll(saasList);
        		} 
        		
        		if (bidIterationRule.getSaasValidationResult() != QuoteConstants.BidIteratnValidationResult.NOT_APPLICABLE_BID_ITERATN){
        			errorCodeList4Map.clear();
        		}
        	} else {
        		if (errorCodeList4Map != null){
        			errorCodeList4Map.remove("saasList");
        		}
        		
        	}
        	
        	if (quote.getQuoteHeader().isHasMonthlySoftPart()){
        		this.logContext.debug(this, "web quote["+quote.getQuoteHeader().getWebQuoteNum()+"] has monthly.");
        		if (monthlyList != null && bidIterationRule.getMonthlyValidationResult() == QuoteConstants.BidIteratnValidationResult.INVALID_BID_ITERATN){
        			errCodeSet.addAll(monthlyList);
        		} else if (bidIterationRule.getMonthlyValidationResult() != QuoteConstants.BidIteratnValidationResult.NOT_APPLICABLE_BID_ITERATN){
        			errorCodeList4Map.clear();
        		}
        	} else {
        		if (errorCodeList4Map != null){
        			errorCodeList4Map.remove("monthlyList");
        		}
        		
        	}
        	
        	if (errorCodeList4Map != null && errorCodeList4Map.size() > 0){
        		for (List<String> errorCodeList : errorCodeList4Map.values()){
        			
        			if (errorCodeList == null || errorCodeList.size() < 1){
        				continue;
        			}
        			
        			errCodeSet.addAll(errorCodeList);
        		}
        	}

       
            this.logContext.debug(this, "web quote["+quote.getQuoteHeader().getWebQuoteNum()+"] software bid iteration validate result: "+bidIterationRule.getSoftwareValidationResult());
            this.logContext.debug(this, "web quote["+quote.getQuoteHeader().getWebQuoteNum()+"] SaaS bid iteration validate result: "+bidIterationRule.getSaasValidationResult());
            this.logContext.debug(this, "web quote["+quote.getQuoteHeader().getWebQuoteNum()+"] monthly bid iteration validate result: "+bidIterationRule.getMonthlyValidationResult());
            this.logContext.debug(this, "web quote["+quote.getQuoteHeader().getWebQuoteNum()+"] error list size: "+ errCodeSet.size());

            // 1 When software only or software and SaaS, if errCodeList size >0
	        if(errCodeSet.size() > 0){
            	this.logContext.debug(this,"web quote["+quote.getQuoteHeader().getWebQuoteNum()+"] not meet bid iteration");
            	validateResult.put(QuoteCapabilityProcess.NOT_MEET_BID_ITERATION, errCodeSet);
            }

        } catch (TopazException e) {
            this.logContext.error(this, "exception when validate saas and software bid iteration");
            e.printStackTrace();
        }
    }
    
    /**
     * 1)if SaaS bid iteration validation result is 1 AND Monthly bid iteration validation result is 1, SAAS_BID_ITERATN_FLAG will be set to 1
     * 1.1)if SaaS bid iteration validation result is 1 AND Monthly bid iteration validation result is -1, SAAS_BID_ITERATN_FLAG will be set to 1
     * 1.2)if SaaS bid iteration validation result is -1 AND Monthly bid iteration validation result is 1, SAAS_BID_ITERATN_FLAG will be set to 1
     * 2)if SaaS bid iteration validation result is -1 AND Monthly bid iteration validation result is -1, SAAS_BID_ITERATN_FLAG will be set to-1
     * 3)if SaaS bid iteration validation result is 0 OR Monthly bid iteration validation result is 0, SAAS_BID_ITERATN_FLAG will be set to 0
     * @param bidIterationRule
     * @return
     */
    private int getSaasBidIteratnQtInd(BidIterationRule bidIterationRule){
    	
    	int saasValidatnResult = bidIterationRule.getSaasValidationResult();
    	int monthlyValidatnResult = bidIterationRule.getMonthlyValidationResult();
    	
    	int valiationResult = saasValidatnResult;
    	
    	if (monthlyValidatnResult == QuoteConstants.BidIteratnValidationResult.INVALID_BID_ITERATN){
    		valiationResult = monthlyValidatnResult;
    	} else if (monthlyValidatnResult == QuoteConstants.BidIteratnValidationResult.VALID_BID_ITERATN &&
    			saasValidatnResult == QuoteConstants.BidIteratnValidationResult.NOT_APPLICABLE_BID_ITERATN){
    		valiationResult = monthlyValidatnResult;
    	} 
    	
    	return valiationResult;
    	
    }


    /**
     * Validate current quote qualifies for SaaS streamlined add-on/trade-up approval
     */
    private void validateStreamLine(){
    	try {
    		quote.getQuoteHeader().setSaaSStrmlndApprvlFlag(CommonServiceUtil.isValidSaasStrmlndAddTrd(quote));
		} catch (TopazException e) {
			 this.logContext.error(this, "exception when validate if qualifies for SaaS streamlined add-on/trade-up approval");
			e.printStackTrace();
		}
    }

    /**
     * validate SSP quote
     * If the SaaS solution provider type is single end user, the end user customer information must be populated with a new or existing customer.
     * If the SaaS solution provider type is multi end user and the quote includes an end user customer, prompt the user to remove the end customer or change the SSP type to single end user.
	 * If the charge agreement has an end user customer specified, the quote must specify the same end user customer.
	 * If the charge agreement does not specify an end user customer, the quote must not specify an end user customer, either.
     */
    private void validateSSPquote(){
    	QuoteHeader quoteHeader = quote.getQuoteHeader();
    	if(!quoteHeader.isSSPQuote()){
    		return;
    	}
    	if(StringUtils.isEmpty(quoteHeader.getSspType())){
    		validateResult.put(QuoteCapabilityProcess.SSP_TYPE_NOT_SELECT, Boolean.TRUE);
    	}
    	if(QuoteConstants.SSP_PROVIDER_TYPE_SINGLE.equals(quoteHeader.getSspType())){
    		if(quote.getEndUser() == null){
    			validateResult.put(QuoteCapabilityProcess.SSP_FILL_IN_END_USER, Boolean.TRUE);
    		}

    	}else if (QuoteConstants.SSP_PROVIDER_TYPE_MULTI.equals(quoteHeader.getSspType())){
    		if(quote.getEndUser() != null){
    			validateResult.put(QuoteCapabilityProcess.SSP_CLEAR_OUT_END_USER, Boolean.TRUE);
    		}
    	}

    	//SSP add on/trade up validation
    	if(quoteHeader.isAddTrd()){

    		String caEndUserNum = quoteHeader.getCaEndUserCustNum();
    		String qtEndUserNum = null;

    		if(quote.getEndUser() != null){
    			qtEndUserNum = quote.getEndUser().getCustNum();
    		}
    		//If the charge agreement has an end user customer specified, the quote must specify the same end user customer.
    		if(StringUtils.isNotBlank(caEndUserNum)){

    			if(StringUtils.isBlank(qtEndUserNum) || !quoteHeader.getCaEndUserCustNum().equals(qtEndUserNum)){
    				validateResult.put(QuoteCapabilityProcess.SSP_CA_FILL_IN_END_USER, Boolean.TRUE);
    			}
    		}
    		//If the charge agreement does not specify an end user customer, the quote must not specify an end user customer, either.
    		if(StringUtils.isBlank(caEndUserNum) && StringUtils.isNotBlank(qtEndUserNum)){
    				validateResult.put(QuoteCapabilityProcess.SSP_CA_CLEAR_OUT_END_USER, Boolean.TRUE);
    		}
    	}

    }

    /**
     * the offer price on new copied quote can not be less than or equals to origianl bid
     */
    private void validateOfferPrice() {
        if(quote.getQuoteHeader().getQuotePriceTot() <= quote.getQuoteHeader().getOriQuotePriceTot()){
            validateResult.put(QuoteCapabilityProcess.OFFER_PRICE_NOT_VALID, Boolean.TRUE);
        }
    }

    protected void validateBackDating(Map result){
    	// if quote has back dating line items
    	QuoteHeader header = quote.getQuoteHeader();
    	boolean fail = false;
    	if(header.getBackDatingFlag()){
    		logContext.debug(this, "header.getBackDatingFlag() == true");
    		if(header.getReasonCodes() == null || header.getReasonCodes().size() == 0){
    			logContext.debug(this, "no back dating reason code, validateBackDating fails");
    			fail = true;
    		} else if(header.getReasonCodes().contains(QuoteConstants.BACK_DATING_REASON_OTHER)
    				      && StringUtils.isBlank(header.getBackDatingComment())){
    			logContext.debug(this, "reason code 'Other' is selected but no back dating comment, validateBackDating fails");
    			fail = true;
    		}

    		if(fail){
    			result.put(QuoteCapabilityProcess.SELECT_BACK_DTG_REASON, Boolean.TRUE);
    		}
    	}
    }

    /**
     * @throws QuoteException
     *
     */

    public Map validateSubmissionForApproval() throws QuoteException {
        validateSubmissionAsFinal();

        validateSpecialBidInfo();

        return validateResult;
    }

    public Map validateTou(boolean orderFlag) throws QuoteException {
    	QuoteHeader header = quote.getQuoteHeader();
    	//IBM Cloud Service Agreement will never validate for all products.
//    	if(header.getSaasTermCondCatFlag() == 2){
//    		return validateResult;
//    	}
    	/*If the quote qualifies for standalone SaaS general terms,
    	 * need specify the terms and conditions covering this order.
    	 * */
    	boolean touRadioChecked = true;
    	//Only use for CSA tou amendmend check when draft.
    	boolean touRadioCheckedForCSA = true;

    	//add for 15.3 ,check CSA quote contain hybrid offering part,if contain,stop quote submission 
    	boolean isHasHybridPartFlag = false;
//        String type = header.getLob().getCode();
//        if (QuoteConstants.LOB_PAE.equals(type) && header.isOnlySaaSParts() && !header.isChannelQuote() && header.getSaasTermCondCatFlag() <1){
//        	validateResult.put(QuoteCapabilityProcess.SPECIFY_TERMS_CONDITIONS_COVERING, Boolean.TRUE);
//        }
    	// Quote submission
    	// add new validation to ensure agreement type and parts are valid combinations. added in 14.2 release for CSA
    	String type = header.getAgrmtTypeCode();
    	int csaTermsCount = 0;
    	int activePartBCount = 0;
    	
    	if (QuoteConstants.LOB_CSRA.equals(type) || QuoteConstants.LOB_CSTA.equals(type)){
    		if(!header.isOnlySaaSParts()){
    			validateResult.put(QuoteCapabilityProcess.SUBMIT_PART_SAAS_ONLY_CSA_TOU_MSG, Boolean.TRUE);
    		}
    		//when CSA prevent channel quotes from being submitted
    		// remove the preventing for 15.2 CSA requirement should support Channel CSA quote
    		/*if(header.isChannelQuote()){
    			validateResult.put(QuoteCapabilityProcess.SUBMIT_CSA_CHANNEL_TOU_MSG, Boolean.TRUE);
    		}*/
    		try {
    			csaTermsCount = QuoteHeaderFactory.singleton().getCountOfCsaTerms();
				if(csaTermsCount < 1){
					if(orderFlag){
						validateResult.put(QuoteCapabilityProcess.MISSING_CSA_TERMS_ORDER, Boolean.TRUE);
					}
					else
						validateResult.put(QuoteCapabilityProcess.MISSING_CSA_TERMS_SUBMIT, Boolean.TRUE);
				}					
			} catch (TopazException e) {
				logContext.debug(this, e.getMessage());
			}
    	}else if(orderFlag){
    		try {
    			activePartBCount = QuoteHeaderFactory.singleton().getCountOfActivePartB(header.getWebQuoteNum());
				if(activePartBCount < 0){
					validateResult.put(QuoteCapabilityProcess.MISSING_ACTIVE_PART_B, Boolean.TRUE);
				}					
			} catch (TopazException e) {
				logContext.debug(this, e.getMessage());
			}
    	}
    	
    	try {
			QuoteHeaderFactory.singleton().updateTouFlag(header.getWebQuoteNum(), 0, "", "", "", "", 1, "");
			if(isInPGS()){
				QuoteHeaderFactory.singleton().updateTouFlag(header.getWebQuoteNum(), 0, "", "", "", "", 4, type);
			}
		} catch (TopazException e1) {
			logContext.debug(this, e1.getMessage());
		}
		
        /*TOU validation:check that all SaaS items are covered by a ToU. 
         *  If any line item is not associated with a ToU
         *  If a quote is PGS special bid quote, will be passed*/
		//if(header.isPGSQuote() && header.getSpeclBidFlag() == 1 && isInPGS()){
        if(!orderFlag && header.isPGSQuote() && header.getSpeclBidFlag() == 1 && isInPGS()){
        }else{
        	List<QuoteLineItem> itemList;
			try {
				itemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(quote.getQuoteHeader().getWebQuoteNum());
				if(null != itemList && itemList.size() > 0){
					StringBuilder missParts = new StringBuilder();
					Collections.sort(itemList, QuoteBaseComparator.createTouErrMsgComparator());
					
		            Iterator it = itemList.iterator();
		            while(it.hasNext()){
		            	QuoteLineItem qi = (QuoteLineItem)it.next();
		            	if(null != qi){
		            		if(qi.isSaasPart()){
			            		if(StringUtils.isBlank(qi.getTouName())){
		            				missParts.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;(").append(qi.getPartNum()).append(")[").append(qi.getPartDesc()).append("]</p>");
		            			}
			            		if(!orderFlag && StringUtils.isBlank(type) && header.getSaasTermCondCatFlag() == 2){  //draft pilot quote need validate CSTA header terms amendment
		            				if(null == qi.getHdrAgrmentAmdFlag()){
		            					touRadioChecked = false;
		    	            		}
			            		}
			            		else if(QuoteConstants.LOB_CSRA.equals(type) || QuoteConstants.LOB_CSTA.equals(type)){//CSA
			            			if(orderFlag){ //validate both CSA terms agreement amendment and product specific terms when ordering
			            				if(null == qi.getHdrAgrmentAmdFlag() || (!StringUtils.isBlank(qi.getTouURL()) && (null == qi.getAmendedTouFlag()))){
			            					touRadioChecked = false;
			    	            		}
			            			}else{ //validate CSA terms agreement amendment at draf only CSA terms agreement exists
			            				if(csaTermsCount > 0 && !StringUtils.isBlank(qi.getTouURL()) && null == qi.getHdrAgrmentAmdFlag()){
			            					touRadioCheckedForCSA = false;
			    	            		}
			            			}
			            			if(!StringUtils.isBlank(qi.getPartSubType()) && QuoteConstants.PART_SUB_TYPE_HYBRID.equals(StringUtils.trimToEmpty(qi.getPartSubType()))){
			            				isHasHybridPartFlag = true;
			            			}
			            		}
			            		else{//PA PAE
			            			if(!StringUtils.isBlank(qi.getTouURL()) && orderFlag ){
			            				if(null == qi.getAmendedTouFlag()){
			            					touRadioChecked = false;
			            				}
			            				if(null == qi.getAmendedTouFlagB() && activePartBCount >= 0){
			            					touRadioChecked = false;
			            				}
				            		}
			            		}
			            	}
		            		/*
		            		else if(qi.isMonthlySoftwarePart() && QuoteConstants.LOB_PA.equals(type) || QuoteConstants.LOB_PAE.equals(type)){
			            		if(StringUtils.isBlank(qi.getTouName())){
		            				missParts.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;(").append(qi.getPartNum()).append(")[").append(qi.getPartDesc()).append("]</p>");
		            			}
			            	}
			            	*/
		            	}
		            }
		            if(missParts.length()>0){
		            	String key = orderFlag ? QuoteCapabilityProcess.ORDER_PART_LOST_TOU_MSG : QuoteCapabilityProcess.SUBMIT_PART_LOST_TOU_MSG;
		            	validateResult.put(key, missParts.toString());
		            }
				} 
			} catch (TopazException e) {
				logContext.debug(this, e.getMessage());
			}
        }
        
        if(!isInPGS() && !touRadioChecked){
        	validateResult.put(QuoteCapabilityProcess.TOU_AMENDMENT_BLANK_MSG, Boolean.TRUE);
        }
        if(!isInPGS() && !touRadioCheckedForCSA){
        	validateResult.put(QuoteCapabilityProcess.CSA_TOU_AMENDMENT_BLANK_MSG, Boolean.TRUE);
        }
        if(isHasHybridPartFlag){
        	validateResult.put(QuoteCapabilityProcess.SUBMIT_PART_SAAS_ONLY_CSA_TOU_MSG, Boolean.TRUE);
        }
        return validateResult;
	}


	public Map validateSubmitCopiedApprvdBid() {

        validateUpdateQuotePartners();

        if (!validateApprvdBidExpDate()) {
            validateResult.put(QuoteCapabilityProcess.QUOTE_EXP_DATE_BEFORE_TODAY, Boolean.TRUE);
        }

        if (!audienceHasSubmitterAccess()) {
            validateResult.put(QuoteCapabilityProcess.AUD_CAN_NOT_SUBMIT_QUOTE, Boolean.TRUE);
        }

        if (!isActiveIfContractSelected()) {
            validateResult.put(QuoteCapabilityProcess.CONTRACT_NOT_ACTIVE, Boolean.TRUE);
        }
        
        if (isExceedEmailMaxLength()) {
            validateResult.put(QuoteCapabilityProcess.EMAIL_LENGTH_EXCEED_MAX, Boolean.TRUE);
        }

        verifyEveryPart(validateResult);

        return validateResult;
    }

    protected void validateResellerAndDistributor(Map result) {

        if (isFulfillmentSrcChannel()) {
            QuoteHeader header = quote.getQuoteHeader();

            //If the fulfillment source is channel, a reseller must be selected
            if (StringUtils.isEmpty(header.getRselCustNum())) {
                //In the case of a PA/E channel sales quote,
                //if a reseller has not been selected,
                //the reseller to be determined flag must be set.
                if (isPA() || isPAE()) {
                    if (!header.isResellerToBeDtrmndFlag()) {
                        result.put(QuoteCapabilityProcess.RESELLER_NOT_SELECT_FOR_PA_PAE, Boolean.TRUE);
                    }
                } else {
                    result.put(QuoteCapabilityProcess.RESELLER_NOT_SELECT, Boolean.TRUE);
                }
            }

            // If the fulfillment source is channel, a distributor must be selected.
            // If the reseller is a tier 1 reseller, the distributor should be set to the same value.

            if (StringUtils.isEmpty(header.getPayerCustNum())) {
                //In the case of a PA/E channel special bid sales quote,
                //if a distributor has not been selected,
                //the distributor to be determined flag must be set.
                if (isPA() || isPAE()) {
                    boolean isDistributorSet = (header.isDistribtrToBeDtrmndFlag() || header.isSingleTierNoDistributorFlag());
                    if ( ! isDistributorSet ) {
                        result.put(QuoteCapabilityProcess.DISTRIBUTOR_NOT_SELECT_FOR_PA_PAE, Boolean.TRUE);
                    }
                } else {
                    result.put(QuoteCapabilityProcess.DISTRIBUTOR_NOT_SELECT, Boolean.TRUE);
                }
            }

            if (isPA() || isPAE()) {
                // resellser selected
                if (!StringUtils.isEmpty(header.getRselCustNum())) {
                    logContext.debug(this,"Reseler tier type: " + quote.getReseller().getTierType());
                    //If a reseller is selected and the distributor is TBD - the user should not be able to submit
                    if (header.isDistribtrToBeDtrmndFlag()) {
                       result.put(QuoteCapabilityProcess.DISTRIBUTOR_SHOULD_NOT_TBD_FOR_RESELLER, Boolean.TRUE);
                    }
                    // tire 2 reseller , and useer selected "No distributor; quote will be fulfilled through a single-tier model"
                    if ((quote.getReseller().isTierTwo()) && (header.isSingleTierNoDistributorFlag())) {
                        result.put(QuoteCapabilityProcess.RESELLER_SHOULD_NOT_TIER1_FOR_SINGLE_TIER_MODEL, Boolean.TRUE);
                    }
                }
            }

            validatePartnerTierType();
        }
    }

    protected void validateMigratiionAndRenewal(){
    	QuoteHeader quoteHeader = quote.getQuoteHeader();
    	
    	//Start
    	List saasLineItems = quote.getSaaSLineItems();
    	List monthlyLineItems = quote.getMonthlySwQuoteDomain().getMonthlySoftwares();
    	boolean isShowSaaSRenewalQ=false;
    	boolean isShowMonthlyRenewalQ=false;
    	boolean isShowSaaSMigrationQ=false;
    	boolean isShowMonthlyMigrationQ=false;
    	
    	// for saas
		for (int i=0;i<saasLineItems.size();i++){
			QuoteLineItem lineItem = (QuoteLineItem)saasLineItems.get(i);
			if(!quote.isPgsAppl()){
				if(!quoteHeader.isFCTToPAQuote() && !hasFctToPAFinalization()){
					//1. determine if show renewal question on header
					if ((lineItem.isSaasSubscrptnPart() && !lineItem.isReplacedPart()) || lineItem.isSaasSubsumedSubscrptnPart()) 
						isShowSaaSRenewalQ=true;
					//2. determine if show migration question on header
					if((quote.getQuoteHeader().isPAQuote()|| quote.getQuoteHeader().isPAEQuote()|| quote.getQuoteHeader().isPAUNQuote()
						|| quote.getQuoteHeader().isFCTQuote()|| quote.getQuoteHeader().isSSPQuote()) && !quote.getQuoteHeader().isOEMQuote()){
						if(!lineItem.isReplacedPart() && (lineItem.isSaasSubscrptnPart()))
							isShowSaaSMigrationQ=true;
					}
				}
			}
		}	
		
		// for monthly
		for (int i=0;i<monthlyLineItems.size();i++){
			QuoteLineItem lineItem = (QuoteLineItem)monthlyLineItems.get(i);
			if(!quote.isPgsAppl()){
				if(!quoteHeader.isFCTToPAQuote() && !hasFctToPAFinalization()){
					//1. determine if show renewal question on header
					if (((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart() && !lineItem.isReplacedPart())
						isShowMonthlyRenewalQ =true;
					//2. determine if show migration question on header
					if((quote.getQuoteHeader().isPAQuote()|| quote.getQuoteHeader().isPAEQuote()|| quote.getQuoteHeader().isPAUNQuote()
						|| quote.getQuoteHeader().isFCTQuote()|| quote.getQuoteHeader().isSSPQuote()) && !quote.getQuoteHeader().isOEMQuote()){
						if (!lineItem.isReplacedPart() && ((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart())
							isShowMonthlyMigrationQ=true;
					}
				}
			}
		}

		//3. if any of the question is shown and any of the question did not be answered, then prompt the message
    	//if((isShowSaaSRenewalQ||isShowSaaSMigrationQ)&& quoteHeader.isSaasRenewalFlag()==null && quoteHeader.isSaasMigrationFlag()==null){
    	if((isShowSaaSRenewalQ && quoteHeader.isSaasRenewalFlag()==null) || (isShowSaaSMigrationQ && quoteHeader.isSaasMigrationFlag()==null)) {
    		validateResult.put(QuoteCapabilityProcess.ANSWER_SAAS_MONTHLY_QUESTION, Boolean.TRUE);
    	}
    	//if((isShowMonthlyRenewalQ||isShowMonthlyMigrationQ)&& quoteHeader.isMonthlyRenewalFlag()==null && quoteHeader.isMonthlyMigrationFlag()==null){
    	if((isShowMonthlyRenewalQ && quoteHeader.isMonthlyRenewalFlag()==null) || (isShowMonthlyMigrationQ && quoteHeader.isMonthlyMigrationFlag()==null)) {
    		validateResult.put(QuoteCapabilityProcess.ANSWER_SAAS_MONTHLY_QUESTION, Boolean.TRUE);
    	}
    }
    
	protected void validateChnlDiscntOvrridReason() {
		if (quote.getQuoteHeader().isChannelQuote()
				&& QuoteCommonUtil.isChannelOverrideDiscount(quote)
				&& StringUtils.isBlank(quote.getSpecialBidInfo()
						.getChannelOverrideDiscountReasonCode())) {
			validateResult
					.put(QuoteCapabilityProcess.SPECIFY_CHANNEL_DISCOUNT_OVERRIDE_REASON,
							Boolean.TRUE);
		}
	}
	public boolean hasFctToPAFinalization(){
		List<PartsPricingConfiguration> configList = quote.getPartsPricingConfigrtnsList();
		if(configList == null || configList.size() ==0){
			return false;
		}
		for (Iterator iterator = configList.iterator(); iterator.hasNext();) {
			PartsPricingConfiguration partsPricingConfiguration = (PartsPricingConfiguration) iterator
					.next();
			if(PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(partsPricingConfiguration.getConfigrtnActionCode())){
				return true;
			}
		}
		return false;
	}
    
    protected void validatePartnerTierType() {

        if (!isPA() && !isPAE())
            return;

        if (isChannelH()) {
            QuoteCommonUtil commonUtil = QuoteCommonUtil.create(this.quote);

            // If quote has SW VN parts, the reseller should be SW VN tier 1 reseller
            if (commonUtil.hasSWVNParts()) {
                if (quote.getReseller() != null && !quote.getReseller().isSWVNTierOne() ) {
                    validateResult.put(QuoteCapabilityProcess.RSEL_NOT_AUTH_TO_SELL, Boolean.TRUE);
                }
            }


            // If quote has PA parts, the reseller should be PA tier 1 reseller
            /**  //remove below rule by Jackie's response(RTC:123520): Notes://ltsgdb001b/85256B83004B1D94/CA30E8393BC22D28482573A7000E50E0/08443B2FE16A1E20852578A00082909B
            if (commonUtil.hasPAParts()) {
                if (quote.getReseller() != null && !quote.getReseller().isPATierOne() ) {
                    validateResult.put(QuoteCapabilityProcess.RSEL_NOT_AUTH_TO_SELL, Boolean.TRUE);
                }
            }
           /**/

            if(quote.getReseller()!=null && quote.getReseller().isSWVNTierOne() && !quote.getReseller().isPATierOne()){
            	//If the reseller logged into SQO is a swvn_tier_1 reseller and they are acting as both the reseller and distributor on the quote the following rule must match
            	// * The reseller must be named as both the tier_1_cust_num and rsel_cust_num on the sods2.rsel_ctrld_distribtn table where the wwide_prod_grp_code_code = 'N/A' for every single part on the quote.
            	//If there is a part on the quote in which the partner does not have a matching record in the sods2.rsel_ctrld_distribn table then do not let the partner submit the quote - use the same message today.

                boolean swvnAuth = false;
                Map authMap = quote.getPayer().getAuthorizedPortfolioMap();
                List parts = quote.getLineItemList();

                if(parts!=null){
                	QuoteLineItem item = null;
                	List tier1List = null;
                	RselCtrldDistribtn rselCtrldDistribtn = null;
                	for(Iterator i = parts.iterator(); i.hasNext();){
                		item = (QuoteLineItem)i.next();
                		tier1List = (List)authMap.get(item.getControlledCode());
                		if(tier1List==null||tier1List.size()==0){
                			swvnAuth = true;
                			break;
                		}
                		swvnAuth = true;
                		for(int j=0; j<tier1List.size();j++){
                			rselCtrldDistribtn = (RselCtrldDistribtn)tier1List.get(j);
                			if(quote.getReseller().getCustNum().equals(rselCtrldDistribtn.getTier1CustNum())){
                				swvnAuth = false;
                				break;
                			}
                		}
                		if(swvnAuth){
                			break;
                		}
                	}
                }

                if(swvnAuth){
                	validateResult.put(QuoteCapabilityProcess.RSEL_NOT_AUTH_TO_SELL, Boolean.TRUE);
                }

            }



        }
        else { // channel J
            if (quote.getPayer() != null && !quote.getPayer().isPATierOne() ) {
                validateResult.put(QuoteCapabilityProcess.INVALID_DISTRIBUTOR, Boolean.TRUE);
            }
        }
    }

    protected boolean isQuoteStartDateBeforeToday() {

        Date quoteStartDate = quote.getQuoteHeader().getQuoteStartDate();
        if (quoteStartDate == null)
            return false;

        Date today = getTodayInQuoteTimeZone();

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

        Date currDate = null;

        try {
        	//firstly try to parse today as yyyyMMdd format, mean to remove the hour, minute, second and then compare with quote start date
			currDate = sf.parse(sf.format(today));
		} catch (ParseException e) {
			e.printStackTrace();
			//PL MCOR-8ND5HP, to address EDT, EST switch issue
			currDate = DateUtils.truncate(today, Calendar.DATE);
		}

        logContext.debug(this, "Current date is " + currDate);
        logContext.debug(this, "Quote start date is " + quoteStartDate);


        logContext.debug(this, "Quote["+quote.getQuoteHeader().getWebQuoteNum()+"] Current date is " + currDate);
        logContext.debug(this, "Quote["+quote.getQuoteHeader().getWebQuoteNum()+"] start date is " + quoteStartDate);
        logContext.debug(this, "Quote["+quote.getQuoteHeader().getWebQuoteNum()+"] quoteStartDate.before(currDate)is " + quoteStartDate.before(currDate));

        return quoteStartDate.before(currDate);
    }

    protected void validateQuoteStartDate() {
        if (quote == null || quote.getQuoteHeader() == null)
            return;

        //At this point the start date should be validated by post action already
        QuoteHeader header = quote.getQuoteHeader();
        Date quoteStartDate = header.getQuoteStartDate();
        Date quoteExpDate = header.getQuoteExpDate();

        if (quoteStartDate == null) {
        	logContext.debug(this, "Quote["+quote.getQuoteHeader().getWebQuoteNum()+"] validateQuoteStartDate quoteStartDate == null");
            validateResult.put(QuoteCapabilityProcess.QT_START_DATE_BEFORE_TODAY, Boolean.TRUE);
            return;
        }

        if (isQuoteStartDateBeforeToday()
                || (quoteExpDate != null && quoteStartDate.after(quoteExpDate))) {
        	logContext.debug(this, "Quote["+quote.getQuoteHeader().getWebQuoteNum()+"] validateQuoteStartDate isQuoteStartDateBeforeToday");
            validateResult.put(QuoteCapabilityProcess.QT_START_DATE_BEFORE_TODAY, Boolean.TRUE);
            return;
        }

        //just validate for add-on/trade-up/co-term
        //chosen start date can not be earlier than the calculated start date
        //and can not be later than the calculated expiration date
        if (header.isAddTrdOrCotermFlag()) {
        	Date cacldStartDate = QuoteCommonUtil.calcQuoteStartDate(quote);
        	Date cacldExpiredDate = QuoteCommonUtil.calcQuoteExpirationDate(quote);
        	if( cacldStartDate != null && quoteStartDate.before(cacldStartDate)) {
        		validateResult.put(QuoteCapabilityProcess.QT_START_DATE_BEFORE_CALCD_START_DATE, DateHelper.getDateByFormat(cacldStartDate, "dd MMM yyyy"));
        		return;
        	}
        	if (cacldExpiredDate != null && quoteStartDate.after(cacldExpiredDate)) {
        		validateResult.put(QuoteCapabilityProcess.QT_START_DATE_AFTER_CALCD_EXPIRED_DATE, DateHelper.getDateByFormat(cacldExpiredDate, "dd MMM yyyy"));
        		return;
        	}
        }

        // key = swSubId ,value = ArrayList contains QuoteLineItems that are License
        // parts
        HashMap licensePartMap = new HashMap();

        // key = swSubId, value = ArrayList contains QuoteLineItems that are related
        // maint parts or unrelated parts

        HashMap maintPartMap = new HashMap();
        Date earliestOverridenStartDate = null;
        Date licensePartEndDate = null;
        Date aDate = null;

        // Only applies to PA/PAE/FCT special bid
        if (header.isPAQuote() || header.isPAEQuote() || header.isFCTQuote()) {
        	List masterLineItems = quote.getMasterSoftwareLineItems();
            if(masterLineItems == null || masterLineItems.size()==0){
                quote.setMasterSoftwareLineItems(CommonServiceUtil.buildMasterLineItemsWithAddtnlMaint(quote.getLineItemList()));
            }
            licensePartMap.clear();
            maintPartMap.clear();

            licensePartEndDate =  checkLicMnt(quote, licensePartMap , maintPartMap);
            if (licensePartMap.isEmpty() ||maintPartMap.isEmpty()|| licensePartEndDate == null)
                return ;
            //If the renewal part start date is not immediately after the License part then check this rule
            Calendar aDay = Calendar.getInstance();
            aDay.setTime(licensePartEndDate);
            aDay.add(Calendar.DATE, 1);
            aDay = DateUtils.truncate(aDay, Calendar.DATE);

            Date aDayAfterL = aDay.getTime();

            Iterator iter = maintPartMap.keySet().iterator();

            while (iter.hasNext()) {

                String subSwId = (String) iter.next();
                // check if License Parts exist with same subSwId
                List licenseParts = (List) licensePartMap.get(subSwId);
                List maintParts = (List) maintPartMap.get(subSwId);

                if (null == licenseParts) {
                   continue ;
                } else {
                    aDate= checkQtyAndDate(licenseParts, maintParts , aDayAfterL);
                    if ((earliestOverridenStartDate == null )|| (aDate != null && aDate.before(earliestOverridenStartDate))){
                        earliestOverridenStartDate = aDate ;
                    }
                }
            }

            if (licensePartEndDate != null && earliestOverridenStartDate != null
                    && DateHelper.differenceLessThanOneYear(licensePartEndDate, earliestOverridenStartDate)) {

                Date tempDate = earliestOverridenStartDate;
                Calendar temp = Calendar.getInstance();
                temp.setTime(tempDate);
                temp.set(Calendar.DATE, 1);
                temp.add(Calendar.YEAR, -1);
                temp.add(Calendar.MONTH, -1);
                Calendar curr = DateUtils.truncate(Calendar.getInstance(), Calendar.DATE);

                //If the calculated date is before today, use today as quote start date
                if (temp.before(curr)){
                    tempDate = curr.getTime() ;
                } else {
                    tempDate = temp.getTime();
                }

                if (quoteStartDate.before(tempDate)) {
                    validateResult.put(QuoteCapabilityProcess.INVALID_QUOTE_START_DATE, String.valueOf(tempDate));
                }

                logContext.debug(this, "Temp quote start date is " + temp.getTime());
                logContext.debug(this, "Final quote start date is " + tempDate);
                logContext.debug(this, "licensePartEndDate is " + licensePartEndDate);
                logContext.debug(this, "earliestOverridenStartDate is " + earliestOverridenStartDate);
            }
        }

        return;
    }

    private Date checkLicMnt(Quote quote, HashMap licensePartMap , HashMap maintPartMap) {
            String swSubId = "" ;
            boolean itemOverridenFlag = false ;
            Date licensePartEndDate = null;

            List masterLineItems = quote.getMasterSoftwareLineItems();
            // firstly , find all license part, renewal part , contract part and
            // some of unrelated part
            for (int i = 0; i < masterLineItems.size(); i++) {
                QuoteLineItem item = (QuoteLineItem) masterLineItems.get(i);

                PartDisplayAttr attr = item.getPartDispAttr();
                swSubId = item.getSwSubId();
                if (null == swSubId) {
                    continue ;
                }

                if (attr.isLicenseBehavior()) {
                    if (licensePartEndDate == null)
                        licensePartEndDate = item.getMaintEndDate();
                    List list = (List) licensePartMap.get(swSubId);
                    if (null == list) {
                        list = new ArrayList();
                    }
                    list.add(item);
                    licensePartMap.put(swSubId, list);
                }
                else if (attr.isMaintBehavior()) {
                    if (StringUtils.isNotBlank(item.getRenewalQuoteNum())) {
                        continue;
                    }
                    //Only check this if the line item date is overriden
                    itemOverridenFlag = item.getStartDtOvrrdFlg()|| item.getEndDtOvrrdFlg()|| item.getProrateFlag();
                    if (!itemOverridenFlag){
                        continue ;
                    }
                    List list = (List) maintPartMap.get(swSubId);
                    if (null == list) {
                        list = new ArrayList();
                    }

                    list.add(item);
                    maintPartMap.put(swSubId, list);
                }
            } // end of for loop

        return licensePartEndDate ;
    }
    private Date checkQtyAndDate(List licenseParts, List maintParts, Date aDay) {
        int licTotQty = sumQuantity(licenseParts);

        List qtyMatchItems = new ArrayList();
        Date earliestOverridenStartDate = null;
        Date itemStartDate = null ;
        Date today = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);

        // all those parts has associated license part, check if quantity match
        for (int i = 0; i < maintParts.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) maintParts.get(i);
            int qty = 0;
            if (item.getPartQty() != null) {
                qty = item.getPartQty().intValue();
            }
            if (qty != licTotQty) {
               continue;
            } else {
                itemStartDate = item.getMaintStartDate();
                //if (itemStartDate != null && !itemStartDate.before(today) && itemStartDate.after(aDay)) {
                if (itemStartDate != null && !itemStartDate.before(today)) {
                    if (earliestOverridenStartDate == null || itemStartDate.before(earliestOverridenStartDate)) {
                		earliestOverridenStartDate = itemStartDate;
                   }
                }
            }
        }
        //Only continue the check if earliestOverridenStartDate is after aDay. aDay is the day immediately after the license part end date.
        //Need to compare the earliestOverridenStartDate and aDay in the same format.
        if (earliestOverridenStartDate != null ){
            Calendar tmpDay = Calendar.getInstance();
            tmpDay.setTime(earliestOverridenStartDate);
            tmpDay = DateUtils.truncate(tmpDay, Calendar.DATE);
            earliestOverridenStartDate = tmpDay.getTime();
            if (!earliestOverridenStartDate.after(aDay)){
                     earliestOverridenStartDate = null ;
            }
        }
       return earliestOverridenStartDate ;
    }

    private int sumQuantity(List lineItems) {

        int qty = 0;
        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            if (item.getPartQty() != null) {
                qty += item.getPartQty().intValue();
            }

        }
        return qty;
    }


    /**
     * @throws QuoteException
     *
     */

    public Map validateOrder() throws QuoteException {
        // Special bid indicator must be set to false
		if (getSpecialBidFlag(quote.getQuoteHeader())) {
            validateResult.put(QuoteCapabilityProcess.SPECIAL_BID_INDICATOR_NOT_FALSE, Boolean.TRUE);
        }
        validateQuoteStartDate();

        // Can't be ordered immediately if quote start date after today
        if (isQuoteStartDateAfterToday()) {
            validateResult.put(QuoteCapabilityProcess.QT_START_DATE_IN_FUTURE, Boolean.TRUE);
        }

        // Exipired Date must be within the number of Quote Active Days
        validateQuoteExpiredDate();
        // Validate estimated order date for add-on/trade-up/co-term quote
        validateEstmtdOrdDate();
        // Validate customer requested arrival date(CRAD)
        validateCRAD();

        // customer is not selected or created
        if (!isCustomerSelected()) {
            validateResult.put(QuoteCapabilityProcess.CUSTOMER_NOT_SELECTED, Boolean.TRUE);
        }

        // If a contract was selected for an existing customer, the contract
        // must be active
        if (!isActiveIfContractSelected()) {
            validateResult.put(QuoteCapabilityProcess.CONTRACT_NOT_ACTIVE, Boolean.TRUE);
        }

        // All fields of contact must be filled in
        validateContactInfo();

        // Fulfillment source must be set and must be set to direct
        if (!isFulfillmentSrcDirect()) {
            validateResult.put(QuoteCapabilityProcess.FULFILLMENT_SRC_NOT_DIRECT, Boolean.TRUE);
        }

        if (!isQuoteClassfctnCodeSelected()) {
            validateResult.put(QuoteCapabilityProcess.QT_CLASSFCTN_NOT_SET, Boolean.TRUE);
        }

        // Every part must have a quantity set
        // Part is a PVU part, and the quantity is 1
        // Every part must be active
        // Every part must have a price
        verifyEveryPart(validateResult);

        // Validate appliance part MTM,Serial Number
        verifyAppliancePart(validateResult);

        // Brief title must be filled in
        if (!isBriefTitleFilled()) {
            validateResult.put(QuoteCapabilityProcess.BRIEF_TITLE_NOT_FILLED, Boolean.TRUE);
        }
        // Either an opportunity number should be filled in, or an exemption
        // code must be selected
        if (!isOppNumOrExempCodeSet()) {
            validateResult.put(QuoteCapabilityProcess.OPP_NUM_OR_EXEMP_CODE_NOT_SET, Boolean.TRUE);
        }
        // A value of business organization must be selected
        if (!isBusOrgSelected()) {
            validateResult.put(QuoteCapabilityProcess.BUS_ORG_NOT_SELECTED, Boolean.TRUE);
        }

        // audience should have telesales update access
        if (!audienceHasSubmitterAccess()) {
            validateResult.put(QuoteCapabilityProcess.AUD_CAN_NOT_SUBMIT_QUOTE, Boolean.TRUE);
        }

        int isValid = isPartnerRegnCntryCrncyValid();
        if (isValid == 1) {
            validateResult.put(QuoteCapabilityProcess.CUST_PARTNER_REGION_MISMATCH, Boolean.TRUE);
        }
        if (isValid == 2) {
            validateResult.put(QuoteCapabilityProcess.CUST_PARTNER_COUNTRY_MISMATCH, Boolean.TRUE);
        }
        if (isValid == 3) {
            validateResult.put(QuoteCapabilityProcess.CUST_PAYER_CURRENCY_MISMATCH, Boolean.TRUE);
        }

        validateForSaaSQuote();

        //validate growth delegation
        validateGrwthDlgtn();
        
        validateTou(true);
        
        validateDivestedPart();
        
        return validateResult;
    }

    protected  void validateDivestedPart() {
    	List lineItemList = quote.getLineItemList();
    	if( lineItemList == null ||lineItemList.size() == 0) 
    		return;
        Iterator iterator = lineItemList.iterator();
        StringBuffer parts = new StringBuffer();
        while (iterator.hasNext()) {
        	QuoteLineItem item = (QuoteLineItem) iterator.next();
        	if(item.isDivestedPart()){
        		parts.append(item.getPartNum()+", ");
        	}
        }
        if(parts.length()>0){
        	String partStr = parts.toString().substring(0, parts.toString().lastIndexOf(","));
        	validateResult.put(QuoteCapabilityProcess.MSG_SQ_HAS_DIVESTED_PART, partStr);
        }
    }
    
    public boolean canConvertToPAE() {
        if (quote == null || quote.getQuoteHeader() == null)
            return false;

        QuoteHeader header = quote.getQuoteHeader();
        return (header.isPAQuote()|| header.isCSRAQuote() || header.isCSTAQuote()) 
        		&& !header.isPAUNQuote() && (header.hasNewCustomer() || header.hasExistingCustomer())
                && header.getHasPartsFlag() && !header.getHasPARenwlLineItmsFlag();
    }

    protected boolean verifyTransSVPLevel() {
        QuoteHeader header = quote.getQuoteHeader();

        if (header.isPAQuote() || header.isSSPQuote()) {
            String priceLevel = header.getTranPriceLevelCode();

            if (needToCheckTotalPoints()) {
                return (QuoteConstants.PRICE_LEVEL_B.equals(priceLevel)
                		|| QuoteConstants.PRICE_LEVEL_D.equals(priceLevel)
                        || QuoteConstants.PRICE_LEVEL_E.equals(priceLevel)
                        || QuoteConstants.PRICE_LEVEL_F.equals(priceLevel)
                        || QuoteConstants.PRICE_LEVEL_G.equals(priceLevel)
                        || QuoteConstants.PRICE_LEVEL_H.equals(priceLevel));
            }
            else
                return true;
        }
        else {
            // No need to check transactional svp level for PAE/FCT/PPSS quotes
            return true;
        }
    }
    //check if we need verify the total points
    private boolean needToCheckTotalPoints(){
        QuoteHeader header = quote.getQuoteHeader();

        if (header.isPAQuote()) {
            String priceLevel = header.getTranPriceLevelCode();
            boolean checkSVPLevel = true;
            Customer cust = quote.getCustomer();

            if (hasNewCust()) {

                if (cust.isACACustomer() || cust.isGOVCustomer() || cust.isXSPCustomer()) {
                    // No need to check for ACA/GOV/XSP/STD new PA customer
                    checkSVPLevel = false;
                }
                else if (cust.isAddiSiteCustomer()) {
                    // For Additional Site new PA customer, no need to check if selected contract has svp level
                    checkSVPLevel = !isCtrctPriceLevelSetUp();
                }
                else {
                    // For STD new PA customer, check transactional svp level
                    checkSVPLevel = true;
                }
            }
            else if (hasExistCust()) {

                if (cust.isACACustomer() || cust.isGOVCustomer() || cust.isXSPCustomer()) {
                    // No need to check for ACA/GOV/XSP/STD existing PA customer
                    checkSVPLevel = false;
                }
                else {
	                // For existing customer, check transactional svp level
	                checkSVPLevel = !isCtrctPriceLevelSetUp();
                }
            }

            return checkSVPLevel;
        }
        else {
            // No need to check transactional svp level for PAE/FCT/PPSS quotes
            return false;
        }

    }

    protected boolean verifyWebCustTypeCode() {
        if (quote.getQuoteHeader().isPAQuote() && hasNewCust())
            return quote.getCustomer().isPAWebCustomer();
        else
            return true;
    }

    public Map getDraftQuoteActionButtonsRule(QuoteUserSession userSession) {
        HashMap rules = new HashMap();

        if (user == null || quote == null || quote.getQuoteHeader() == null)
            return null;

        String applCode = null;

        if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(userSession.getAudienceCode())){
        	applCode = QuoteConstants.APP_CODE_PGS;
        }else{
        	applCode = QuoteConstants.APP_CODE_SQO;
        }

        int userAccess = user.getAccessLevel(applCode);
        Partner payer = quote.getPayer();
        QuoteHeader header = quote.getQuoteHeader();

        boolean isDisplaySubmitForApproval = false;
        boolean isDisplaySaveQuoteAsDraft = false;
        boolean isDisplayExportQuoteAsSpreadsheet = false;
        boolean isDisplayEmailDraftQuote = false;
        boolean isDisplaySubmitAsFinal = false;
        boolean isDisplayOrder = false;
        boolean isDisplayNoCustOrPartMsg = false;
        boolean isDisplayCtrctNotActiveMsg = false;
        boolean isDisplayPartNoPricingMsg = false;
        boolean isDisplayChannelSQOrderMsg = false;
        boolean isDisplayNewCopyButton = false;
        boolean isDiaplaySBNotPermittedMsg = false;
        boolean isDisplayCnvtToPAEBtn = false;
        boolean isDisplayDldRichTextBtn = false;
        boolean isDisplayObsltPartMsg = false;
        boolean isDisplayNonPACustMsg = false;
        boolean isDisplayEffDateInFtrMsg = false;
        boolean isDisplayCntryNotAllowOrderMsg = false;
        boolean isDisplayOEMNotAllowOrderMsg = false;
        boolean isDisplayNotAllowOrderForCustBlockMsg = false; //not allow order as a blocked customer - sods2.customer.sales_ord_block = '01'
        boolean isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg = false; //Hide the submit button and display message if the quote is a channel quote and the quote has mixed SaaS parts(also contains software parts) in it
        boolean isDisplayNoMatchBPsOnChargeAgreementFlag = false; //Hide the submit button and display message if the quote is a channel quote and the BPs not match charge agreement
        boolean isDisplayFct2PaQuoteTerminationMsg = false;

        boolean isDisplayOrdWithoutOpptNumMsg = false;
        boolean isDisplayCannotSubmitWithSoftpartInPGS = false;

        boolean isDisplayEvalOptSection = false;
        
        //added by Ma Chao
        boolean isDisplayOrderForDivestedPartMsg = false;

        //added by Shawn
        boolean isDisplayAccept = false;

        String lob = header.getLob() == null ? "" : header.getLob().getCode();
        Contract contract = this.getQuoteContract();
        String priceLevel = header.getTranPriceLevelCode();
        String cntryCode = header.getCountry() == null ? "" : header.getCountry().getCode3();
        String  cntryCodeForOrder = "";
        if(QuoteConstants.QUOTE_AUD_CODE.PSPTRSEL.equalsIgnoreCase(header.getAudCode())){
        	cntryCodeForOrder = payer == null ? "" : payer.getCountry();
        }else{
        	cntryCodeForOrder = header.getCountry() == null ? "" : header.getCountry().getCode3();
        }
        String isMigration = header.isMigration() ? "1" : "0";
        boolean ELAFlag = header.isELAQuote();
        Customer cust = quote.getCustomer();

        boolean isCustBlocked = false;//if the customer is blocked, for hidden order button message
        if(cust !=null && cust.isCustOrdBlcked()){
        	isCustBlocked = true;
        }

        logContext.debug(this, "Application code: " + applCode);
        logContext.debug(this, "Draft sales quote common button rules begin:");
        logContext.debug(this, "Web quote number is: " + header.getWebQuoteNum());
        logContext.debug(this, "User id is: " + user.getEmail());
        logContext.debug(this, "User access is: " + userAccess);
        logContext.debug(this, "Special bid flag is: " + header.getSpeclBidFlag());
        logContext.debug(this, "Has new customer is: " + header.hasNewCustomer());
        logContext.debug(this, "Has existing customer is: " + header.hasExistingCustomer());
        logContext.debug(this, "Part has price flag is: " + header.getPartHasPriceFlag());
        logContext.debug(this, "SVP level is: " + priceLevel);
        logContext.debug(this, "Has parts flag is: " + header.getHasPartsFlag());
        logContext.debug(this, "Has obsolete parts flag is: " + header.getHasObsoletePartsFlag());
        logContext.debug(this, "Fulfillment source is: " + header.getFulfillmentSrc());
        logContext.debug(this, "Total point is: " + header.getTotalPoints());
        logContext.debug(this, "isActiveIfContractSelected: " + isActiveIfContractSelected());
        logContext.debug(this, "hasActiveContract is: " + hasActiveContract());
        logContext.debug(this, "isSelectedContractNotActive is: " + isSelectedContractNotActive());
        logContext.debug(this, "isCtrctPriceLevelSetUp is: " + isCtrctPriceLevelSetUp());
        logContext.debug(this, "LOB is: " + lob);
        logContext.debug(this, "ELA flag is: " + ELAFlag);
        logContext.debug(this, "Country code is: " + cntryCode);
        logContext.debug(this, "isSBButtonDisplay is: "
                + isSBButtonDisplay(lob, cntryCode, isMigration, header.getAcquisitionCodes()));
        logContext.debug(this, "isOrderButtonDisplay is: " + isOrderButtonDisplay(lob, ELAFlag, cntryCodeForOrder));

        if (header.isBidIteratnQt()
                && QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(header.getQuoteStageCode())
                && (userAccess == QuoteConstants.ACCESS_LEVEL_ADMINISTRATOR
                        || userAccess == QuoteConstants.ACCESS_LEVEL_ECARE
                        || userAccess == QuoteConstants.ACCESS_LEVEL_READER
                        || userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER)) {

            logContext.debug(this, "Bid iteration flag is " + header.isBidIteratnQt());
            rules.put(QuoteCapabilityProcess.DISPLAY_SAVE_QT_AS_DRAFT_BTN, Boolean.TRUE);

            if(userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER){
                rules.put(QuoteCapabilityProcess.DISPLAY_SUBMIT_AS_FINAL_BTN, Boolean.TRUE);
                rules.put(QuoteCapabilityProcess.DISPLAY_CONV_TO_STD_COPY_BTN, Boolean.TRUE);
            }

            return rules;
        }

        // User's access level is reader, submitter, administrator or eCare
        if (userAccess == QuoteConstants.ACCESS_LEVEL_ADMINISTRATOR || userAccess == QuoteConstants.ACCESS_LEVEL_ECARE
                || userAccess == QuoteConstants.ACCESS_LEVEL_READER
                || userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER) {

            isDisplaySaveQuoteAsDraft = true;
            //always display "Save as new draft quote" button after single qutoe reference
            //if (StringUtils.isNotBlank(header.getSavedQuoteNum())) {
                isDisplayNewCopyButton = true;
            //}
        }

        // User's access level is reader, submitter, approver, administrator or
        // eCare
        if (userAccess == QuoteConstants.ACCESS_LEVEL_ADMINISTRATOR || userAccess == QuoteConstants.ACCESS_LEVEL_ECARE
                || userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER
                || userAccess == QuoteConstants.ACCESS_LEVEL_READER
                || userAccess == QuoteConstants.ACCESS_LEVEL_APPROVER) {

            // Add PPSS logic
            if (!header.isPPSSQuote()) {
                isDisplayExportQuoteAsSpreadsheet = true;
                isDisplayEmailDraftQuote = true;
            }
            //If quote is PPSS do not show any export options
            if (QuoteConstants.LOB_PPSS.equalsIgnoreCase(lob) || QuoteConstants.LOB_SSP.equalsIgnoreCase(lob)) {
            	isDisplayExportQuoteAsSpreadsheet = false;
                isDisplayEmailDraftQuote = false;
            }
        }

      //added by Shawn
        if(QuoteConstants.APP_CODE_SQO.equalsIgnoreCase(applCode) && (userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER)
        		&& header.isPGSQuote()
        		&& header.isSubmittedForEval()
        		&& header.isQuoteCntryEvaluator()
        		&& StringUtils.isEmpty(header.getEvalEmailAdr())){

        	isDisplayAccept = true;
        }

        // User's access level is submit
        // Quote has been flagged as special bid
        // An existing customer has been selected or a new customer has been
        // created
        // All parts on the quote have pricing
        // All parts are active
        // For quotes for existing customers with an existing contract, the
        // contract must be active
        // For PA/FCT channel quote,must no SaaS parts in it
        // For channel quote, the BPs must match BPs of charge Agreement
        if ( QuoteConstants.APP_CODE_SQO.equalsIgnoreCase(applCode) && (userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER)
				&& header.getSpeclBidFlag() == 1
                && (header.hasNewCustomer() || header.hasExistingCustomer()) && header.getPartHasPriceFlag() == 1
                && isActiveIfContractSelected()
                && this.isSBButtonDisplay(lob, cntryCode, isMigration, header.getAcquisitionCodes())
                && verifyWebCustTypeCode()
                && verifyTransSVPLevel()
                && canSubmitQtWithSaaSOrMonthlySWParts()
                && !( header.isPGSQuote() && (header.isSubmittedForEval() || header.isReturnForChgByEval() || header.isEditForRetChgByEval()) && header.isQuoteCntryEvaluator())
        ) {
        	 isDisplaySubmitForApproval = true;
        }

        if ((QuoteConstants.APP_CODE_PGS.equalsIgnoreCase(applCode))
        		&& header.getSpeclBidFlag() == 1
                && (header.hasNewCustomer() || header.hasExistingCustomer()) && header.getPartHasPriceFlag() == 1
                && isActiveIfContractSelected()
                && this.isSBButtonDisplay(lob, cntryCode, isMigration, header.getAcquisitionCodes())
                && verifyWebCustTypeCode()
                && verifyTransSVPLevel()
                && canSubmitQtWithSaaSOrMonthlySWParts()
                && checkSubmitAccessinPGS(userSession)
                && this.checkPGSSubmitalCountryRestriction(quote, userSession)
                && !(header.isAcceptedByEval() || header.isReturnForChgByEval() || header.isSubmittedForEval())
        ) {

        	 isDisplaySubmitForApproval = true;
        }

        if(QuoteConstants.APP_CODE_SQO.equalsIgnoreCase(applCode) && (userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER || userAccess == QuoteConstants.ACCESS_LEVEL_ECARE)
        		&& header.isAcceptedByEval()
        		&& (header.isAssignedEval(userSession.getUserId()) || header.isQuoteCntryEvaluator()) ) {

        	isDisplayEvalOptSection = true;
        }

        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isDisplaySubmitForApproval is: "+isDisplaySubmitForApproval);
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] QuoteConstants.APP_CODE_PGS.equalsIgnoreCase(applCode) || userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER is: "+(QuoteConstants.APP_CODE_PGS.equalsIgnoreCase(applCode) || userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] header.getSpeclBidFlag() == 1 is: "+(header.getSpeclBidFlag() == 1));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] (header.hasNewCustomer() || header.hasExistingCustomer()) is: "+(header.hasNewCustomer() || header.hasExistingCustomer()));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] header.getPartHasPriceFlag() == 1 is: "+(header.getPartHasPriceFlag() == 1));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isActiveIfContractSelected() is: "+(isActiveIfContractSelected()));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] this.isSBButtonDisplay(lob, cntryCode, isMigration, header.getAcquisitionCodes()) is: "+this.isSBButtonDisplay(lob, cntryCode, isMigration, header.getAcquisitionCodes()));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] verifyWebCustTypeCode() is: "+verifyWebCustTypeCode());
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] verifyTransSVPLevel() is: "+verifyTransSVPLevel());
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] canSubmitQtWithSaaSOrMonthlySWParts() is: "+canSubmitQtWithSaaSOrMonthlySWParts());
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] checkSubmitAccessinPGS(userSession) is: "+checkSubmitAccessinPGS(userSession));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isDisplayEvalOptSection is: "+isDisplayEvalOptSection);


        // User's access level is submit
        // Quote has NOT been flagged as special bid
        // An existing customer has been selected or a new customer has been
        // created
        // Quote contains at least one part
        // All parts on the quote have pricing
        // All parts are active
        // For PA quotes for new customers, the price level must be SVP D - H
        // On quotes for existing customers with an existing contract, the
        // contract must be active
        // For PA/FCT channel quote,must no SaaS parts in it
        // For channel quote, the BPs must match BPs of charge Agreement
        if ( QuoteConstants.APP_CODE_PGS.equalsIgnoreCase(applCode)
        		&& header.getSpeclBidFlag() == 0
                && (header.hasNewCustomer() || header.hasExistingCustomer()) && header.getHasPartsFlag()
                && header.getPartHasPriceFlag() == 1 && isActiveIfContractSelected()
                && verifyWebCustTypeCode()
                && verifyTransSVPLevel()
                && canSubmitQtWithSaaSOrMonthlySWParts()
                && checkSubmitAccessinPGS(userSession)
                && checkPGSSubmitalCountryRestriction(quote, userSession)
                && !(header.isAcceptedByEval() || header.isReturnForChgByEval() || header.isSubmittedForEval())
        	) {
        	isDisplaySubmitAsFinal = true;
        }

        if ( QuoteConstants.APP_CODE_SQO.equalsIgnoreCase(applCode) && userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER
				&& header.getSpeclBidFlag() == 0
                && (header.hasNewCustomer() || header.hasExistingCustomer()) && header.getHasPartsFlag()
                && header.getPartHasPriceFlag() == 1 && isActiveIfContractSelected()
                && verifyWebCustTypeCode()
                && verifyTransSVPLevel()
                && canSubmitQtWithSaaSOrMonthlySWParts()
                && checkSubmitAccessinPGS(userSession)
                && !( header.isPGSQuote() && (header.isSubmittedForEval() || header.isReturnForChgByEval() || header.isEditForRetChgByEval()) && header.isQuoteCntryEvaluator())
        	) {
        	isDisplaySubmitAsFinal = true;
        }
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isDisplaySubmitAsFinal is: "+isDisplaySubmitAsFinal);
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] (QuoteConstants.APP_CODE_PGS.equalsIgnoreCase(applCode) || userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER) is: "+(QuoteConstants.APP_CODE_PGS.equalsIgnoreCase(applCode) || userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] header.getSpeclBidFlag() == 0 is: "+(header.getSpeclBidFlag() == 0));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] (header.hasNewCustomer() || header.hasExistingCustomer()) is: "+((header.hasNewCustomer() || header.hasExistingCustomer())));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] header.getHasPartsFlag() is: "+header.getHasPartsFlag());
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] header.getPartHasPriceFlag() == 1 is: "+(header.getPartHasPriceFlag() == 1));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isActiveIfContractSelected() is: "+isActiveIfContractSelected());
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] verifyWebCustTypeCode() is: "+verifyWebCustTypeCode());
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] verifyTransSVPLevel() is: "+verifyTransSVPLevel());
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] canSubmitQtWithSaaSOrMonthlySWParts() is: "+canSubmitQtWithSaaSOrMonthlySWParts());
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] checkSubmitAccessinPGS(userSession) is: "+checkSubmitAccessinPGS(userSession));


        // All rules specified under "Submit as final" are satisfied
        // An existing customer has been selected; the quote is not for a
        // newly-created customer
        // If the line of business is PA, an existing contract for that line of
        // business has been selected, and that contract is active
        // Fulfillment source must be set to direct
        // Quote start date
        if (isDisplaySubmitAsFinal && hasExistCust()
                && (QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(header.getFulfillmentSrc()))
                && isOrderButtonDisplay(lob, ELAFlag, cntryCodeForOrder)
                && !header.isCreateCtrctFlag()
                && !isQuoteStartDateAfterToday()
                && !isCustBlocked
                && !(this.isInPGS()&&quote.getQuoteHeader().hasSoftSLineItem())
                && !header.isSSPQuote()
        	) {
            if (header.isPAQuote())
                isDisplayOrder = this.hasActiveContract();
            else
                isDisplayOrder = true;
        }
        if(isDisplayOrder){
        	isDisplayOrder = !header.isHasDivestedPart();
        	isDisplayOrderForDivestedPartMsg = header.isHasDivestedPart();
        }
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isDisplayOrder is: "+isDisplayOrder);
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isDisplayOrderForDivestedPartMsg is: "+isDisplayOrderForDivestedPartMsg);
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isDisplaySubmitAsFinal is: "+isDisplaySubmitAsFinal);
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] header.getFulfillmentSrc() is: "+header.getFulfillmentSrc());

        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] hasExistCust() is: "+hasExistCust());
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(header.getFulfillmentSrc()) is: "+QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(header.getFulfillmentSrc()));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isOrderButtonDisplay(lob, ELAFlag, cntryCodeForOrder) is: "+isOrderButtonDisplay(lob, ELAFlag, cntryCodeForOrder));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] !header.isCreateCtrctFlag() is: "+!header.isCreateCtrctFlag());
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] !isQuoteStartDateAfterToday() is: "+!isQuoteStartDateAfterToday());
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] !isCustBlocked is: "+!isCustBlocked);
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] header.isPAQuote() is: "+header.isPAQuote());

        if(isDisplayOrder && header.isSaasFCTToPAQuote()){
        	isDisplayFct2PaQuoteTerminationMsg = true;
        }

        if(isDisplayOrder
        		&& QuoteConstants.FULFILLMENT_DIRECT.equals(this.quote.getQuoteHeader().getFulfillmentSrc())
        		&& StringUtils.isBlank(this.quote.getQuoteHeader().getOpprtntyNum())
        		&& (this.quote.getQuoteHeader().isHasNewLicencePart() || this.quote.getQuoteHeader().isHasSaasSubNoReNwPart() || this.quote.getQuoteHeader().isHasAppMainPart())
        		){
        	isDisplayOrdWithoutOpptNumMsg = true;
        }
        // User's access level is submit
        // Neither the submit as final nor the submit for approval button
        // display
        // An existing customer has not been selected and a new customer has not
        // been created
        // or, Parts have not been added to the quote
        if ((QuoteConstants.APP_CODE_PGS.equalsIgnoreCase(applCode) || userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER)
        		&& !isDisplaySubmitForApproval
                && !isDisplaySubmitAsFinal
                && checkSubmitAccessinPGS(userSession)
                && ((!header.hasExistingCustomer() && !header.hasNewCustomer()) || !header.getHasPartsFlag()))
            isDisplayNoCustOrPartMsg = true;

        // User's access level is submit, and
        // Neither the submit as final nor the submit for approval button
        // display, and
        // An existing customer has been selected for the quote, and
        // Parts have been added to the quote, and
        // The line of business is PA or FCT, and
        // An existing contract for that line of business has been selected, and
        // The selected contract is not active
        if ((QuoteConstants.APP_CODE_PGS.equalsIgnoreCase(applCode) || userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER)
        		&& !isDisplaySubmitForApproval
                && !isDisplaySubmitAsFinal && header.hasExistingCustomer() && header.getHasPartsFlag()
                && checkSubmitAccessinPGS(userSession)
                && (header.isPAQuote() || header.isFCTQuote()) && isSelectedContractNotActive())
            isDisplayCtrctNotActiveMsg = true;

        // User's access level is submit, and
        // Neither the submit as final nor the submit for approval button
        // displays, and
        // A customer has been selected for the quote (an existing customer has
        // been selected or a new customer has been created), and
        // Parts have been added to the quote, and
        // If the line of business is PA, an existing contract for that line of
        // business has been selected, and that contract is active, and
        // One or more parts cannot be priced.
        if ((QuoteConstants.APP_CODE_PGS.equalsIgnoreCase(applCode) || userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER)
        		&& !isDisplaySubmitForApproval
                && !isDisplaySubmitAsFinal && (header.hasNewCustomer() || header.hasExistingCustomer())
                && checkSubmitAccessinPGS(userSession)
                && header.getHasPartsFlag() && header.getPartHasPriceFlag() == 0) {
            if (header.isPAQuote())
                isDisplayPartNoPricingMsg = this.hasActiveContract();
            else
                isDisplayPartNoPricingMsg = true;
        }

        if ((QuoteConstants.APP_CODE_PGS.equalsIgnoreCase(applCode) || userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER)
                && header.isPAQuote()
                && !isDisplayNoCustOrPartMsg
                && !isDisplayCtrctNotActiveMsg
                && !isDisplayPartNoPricingMsg) {

            if (!isDisplaySubmitAsFinal && !isDisplaySubmitForApproval) {
                isDisplayNonPACustMsg = hasNewCust() && !cust.isPAWebCustomer();
            }
            else if (!isDisplayOrder) {
                isDisplayNonPACustMsg = header.isCreateCtrctFlag();
            }
        }

        isDisplayOEMNotAllowOrderMsg = header.isOEMQuote();
        if (!isDisplayOrder && !isDisplayOEMNotAllowOrderMsg && !QuoteConstants.APP_CODE_PGS.equalsIgnoreCase(applCode)) {
            isDisplayChannelSQOrderMsg = QuoteConstants.FULFILLMENT_CHANNEL
                    .equalsIgnoreCase(header.getFulfillmentSrc());

            isDisplayEffDateInFtrMsg = isQuoteStartDateAfterToday() && !isDisplayChannelSQOrderMsg;

            if(isCustBlocked){//if customer is bocked, show message
            	isDisplayNotAllowOrderForCustBlockMsg = true;
            }
        }

        // User?s access level is submitter
        // Quote has been flagged as special bid
        // The quote's line of business (quote type) and country combination do
        // not permit special bids to be submitted
        isDiaplaySBNotPermittedMsg = (userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER
                && header.getSpeclBidFlag() == 1 && !this.isSBButtonDisplay(lob, cntryCode, isMigration, header.getAcquisitionCodes()));

        isDisplayDldRichTextBtn = true;

        // Line of business is Passport Advantage
        // Customer is selected or a new customer was created.
        // At least one part has been added to the quote
        isDisplayCnvtToPAEBtn = this.canConvertToPAE();

        isDisplayObsltPartMsg = header.getHasObsoletePartsFlag();

        // Display message if country is not allowed to order, OEM order message takes precedence before country message
        if ( !isOrderButtonDisplay(lob, false, cntryCodeForOrder) && !isDisplayOEMNotAllowOrderMsg) {
            isDisplayCntryNotAllowOrderMsg = true;
        }
        //Display message if the quote is a channel quote and the quote has SaaS parts in it
        if(!isDisplaySubmitForApproval && !isDisplaySubmitAsFinal && !canSubmitQtWithSaaSOrMonthlySWParts()&& checkSubmitAccessinPGS(userSession)){
        	isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg = true;
        }

//        if(!isDisplaySubmitForApproval && !isDisplaySubmitAsFinal && !isDisplayOrder && this.isInPGS()&&quote.getQuoteHeader().hasSoftSLineItem()){
//        	isDisplayCannotSubmitWithSoftpartInPGS = true;
//        }

        rules.put(QuoteCapabilityProcess.DISPLAY_SAVE_QT_AS_DRAFT_BTN, Boolean.valueOf(isDisplaySaveQuoteAsDraft));
        rules.put(QuoteCapabilityProcess.DISPLAY_NEW_COPY_BTN, Boolean.valueOf(isDisplayNewCopyButton));
        rules.put(QuoteCapabilityProcess.DISPLAY_EXPORT_QT_AS_SPREADSHEET_BTN, Boolean.valueOf(isDisplayExportQuoteAsSpreadsheet));
        rules.put(QuoteCapabilityProcess.DISPLAY_EMAIL_DRAFT_QUOTE_BTN, Boolean.valueOf(isDisplayEmailDraftQuote));
        rules.put(QuoteCapabilityProcess.DISPLAY_SUBMIT_FOR_APPR_BTN, Boolean.valueOf(isDisplaySubmitForApproval));
        rules.put(QuoteCapabilityProcess.DISPLAY_SUBMIT_AS_FINAL_BTN, Boolean.valueOf(isDisplaySubmitAsFinal));
        rules.put(QuoteCapabilityProcess.DISPLAY_ORDER_BTN, Boolean.valueOf(isDisplayOrder));
        rules.put(QuoteCapabilityProcess.DISPLAY_NO_CUST_OR_PART_MSG, Boolean.valueOf(isDisplayNoCustOrPartMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CTRCT_NOT_ACTIVE_MSG, Boolean.valueOf(isDisplayCtrctNotActiveMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_PART_NO_RPC_MSG, Boolean.valueOf(isDisplayPartNoPricingMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CHANNEL_QT_ORDER_MSG, Boolean.valueOf(isDisplayChannelSQOrderMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_SB_NOT_PERMITTED_MSG, Boolean.valueOf(isDiaplaySBNotPermittedMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CNVT_TO_PAE_BTN, Boolean.valueOf(isDisplayCnvtToPAEBtn));
        rules.put(QuoteCapabilityProcess.DISPLAY_DOWNLOAD_RICH_TEXT_BTN, Boolean.valueOf(isDisplayDldRichTextBtn));
        rules.put(QuoteCapabilityProcess.DISPLAY_HAS_OBSLT_PART_MSG, Boolean.valueOf(isDisplayObsltPartMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_NON_PA_CUST_MSG, Boolean.valueOf(isDisplayNonPACustMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_EFF_DATE_IN_FTR_MSG, Boolean.valueOf(isDisplayEffDateInFtrMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_OEM_NOT_ALLOW_ORDER_MSG, Boolean.valueOf(isDisplayOEMNotAllowOrderMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CNTRY_NOT_ALLOW_ORDER_MSG, Boolean.valueOf(isDisplayCntryNotAllowOrderMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_ORDER_FOR_CUST_BLOCK_MSG, Boolean.valueOf(isDisplayNotAllowOrderForCustBlockMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_SUBMIT_FOR_CHNNL_QT_HAS_MIX_SAAS_PART_MSG, Boolean.valueOf(isDisplayNotAllowSubmitForChnnlQtHasMixSaaSPartMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_PGS_SUBMITTER, Boolean.valueOf(checkSubmitAccessinPGS(userSession)));
        rules.put(QuoteCapabilityProcess.DISPLAY_ORD_WITHOUT_OPPTNUM_MSG, Boolean.valueOf(isDisplayOrdWithoutOpptNumMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_FCTTOPA_QUOTE_TERMINATION_MSG, Boolean.valueOf(isDisplayFct2PaQuoteTerminationMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CANNOT_SUBMIT_WITH_SOFTPART_IN_PGS_MSG, Boolean.valueOf(isDisplayCannotSubmitWithSoftpartInPGS));
        rules.put(QuoteCapabilityProcess.DISPLAY_EVALUATOR_OPTIONS_SECTION, Boolean.valueOf(isDisplayEvalOptSection));
        //added by Shawn
        rules.put(QuoteCapabilityProcess.DISPLAY_ACCEPT_QUOTE, Boolean.valueOf(isDisplayAccept));

        rules.put(QuoteCapabilityProcess.SOFTWARE_PART_WITHOUT_APPINC_ID, isSoftwarePartWithoutAppIncId());
        
        //added by Ma Chao
        rules.put(QuoteCapabilityProcess.NOT_ALLOW_ORDER_FOR_DIVESTED_PART, Boolean.valueOf(isDisplayOrderForDivestedPartMsg));

		rules.put(QuoteCapabilityProcess.PRICE_LEVEL_NOT_SET, Boolean.valueOf(!verifyTransSVPLevel()));

        return rules;
    }


    //this method doesn't check mixed parts quote
    protected boolean checkPGSSubmitalCountryRestriction(Quote quote, QuoteUserSession userSession)
    {
    	QuoteHeader header = quote.getQuoteHeader();
    	
    	//no country restriction for saas, software and appliance parts with 13.2 requirement
    	if ( QuoteConstants.BPTierModel.BP_TIER_MODEL_ONE.equalsIgnoreCase(userSession.getBpTierModel()) )
    	{
    		//no country restriction for saas
    		if ( header.hasSaaSLineItem() )
    		{
    			return true;
    		}
    		//have restriction for soft and appliance. Currently, there is no restriction country in the configuration file in DSW 13.2.
    		return ButtonDisplayRuleFactory.singleton().isPGSQuoteSubmisAllowed(header,QuoteConstants.BPTierModel.BP_TIER_MODEL_ONE);
    	}
    	else
    	{
    		//soft, appliance, saas all have the same country restriction
    		return ButtonDisplayRuleFactory.singleton().isPGSQuoteSubmisAllowed(header,QuoteConstants.BPTierModel.BP_TIER_MODEL_TWO);
    	}
    }

    /**
     * check PGS user submit access
     * @param userSession
     * @return
     */
    private boolean checkSubmitAccessinPGS(QuoteUserSession userSession) {

    	//by pass the check for SQO users
    	if(!QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(userSession.getAudienceCode())){
    		return true;
    	}

    	boolean t1ActAsT2Flag = false;
    	if(userSession.isHouseAccountFlag()
    			&& null != quote.getPayer()
    			&& null != userSession.getSiteNumber()
    			&& !userSession.getSiteNumber().equalsIgnoreCase(quote.getPayer().getCustNum())){
    		t1ActAsT2Flag = true;
    	}

    	//for PGS users, only tier 1 bp and not IBMer emualting bp can submit quotes
    	if(QuoteConstants.BPTierModel.BP_TIER_MODEL_ONE.equalsIgnoreCase(userSession.getBpTierModel())
    			&& !userSession.isTeleSales()){
    		if(t1ActAsT2Flag && (quote.getQuoteHeader().getSpeclBidFlag() != 1)){
    			return false;
    		}
    		else {
    			return true;
    		}
    	}
    	if(QuoteConstants.BPTierModel.BP_TIER_MODEL_TWO.equalsIgnoreCase(userSession.getBpTierModel()) && !userSession.isTeleSales()){
    		return true;
    	}
		return false;
	}

	public Map getSubmittedQuoteActionButtonsRule() {

        HashMap rules = new HashMap();

        if (user == null || quote == null || quote.getQuoteHeader() == null)
            return null;

        QuoteHeader header = quote.getQuoteHeader();

        String applCode = null;

        if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(user.getAudienceCode())){
        	applCode = QuoteConstants.APP_CODE_PGS;
        }else{
        	applCode = QuoteConstants.APP_CODE_SQO;
        }

        int userAccess = user.getAccessLevel(applCode);

        if ((header.isCopied4PrcIncrQuoteFlag()
                && QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC.equalsIgnoreCase(header.getQuoteStageCode()))
                && userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER) {

            logContext.debug(this, "Copied sb to increase price flag is " + header.isCopied4PrcIncrQuoteFlag());

            rules.put(QuoteCapabilityProcess.DISPLAY_SUBMIT_AS_FINAL_BTN, Boolean.TRUE);
            return rules;
        }
        
        if ((header.isExpDateExtendedFlag() 
        		&& QuoteConstants.QUOTE_STAGE_CODE_CPEXDATE.equalsIgnoreCase(header.getQuoteStageCode()))
                && userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER) {
            logContext.debug(this, "Copied sb to change expiry date is " + header.isExpDateExtendedFlag());
           
            rules.put(QuoteCapabilityProcess.DISPLAY_SAVE_QT_AS_DRAFT_BTN, Boolean.TRUE); 
            rules.put(QuoteCapabilityProcess.DISPLAY_SUBMIT_AS_FINAL_BTN, Boolean.TRUE);
            rules.put(QuoteCapabilityProcess.DISPLAY_CONV_TO_STD_COPY_BTN, Boolean.TRUE);
            return rules;    	
        }

        if (header.isBidIteratnQt()
                && QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(header.getQuoteStageCode())
                && (userAccess == QuoteConstants.ACCESS_LEVEL_ADMINISTRATOR
                        || userAccess == QuoteConstants.ACCESS_LEVEL_ECARE
                        || userAccess == QuoteConstants.ACCESS_LEVEL_READER
                        || userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER)) {

            logContext.debug(this, "Bid iteration flag is " + header.isBidIteratnQt());
            rules.put(QuoteCapabilityProcess.DISPLAY_SAVE_QT_AS_DRAFT_BTN, Boolean.TRUE);

            if(userAccess == QuoteConstants.ACCESS_LEVEL_SUBMITTER){
                rules.put(QuoteCapabilityProcess.DISPLAY_SUBMIT_AS_FINAL_BTN, Boolean.TRUE);
                rules.put(QuoteCapabilityProcess.DISPLAY_CONV_TO_STD_COPY_BTN, Boolean.TRUE);
            }

            return rules;
        }

        Customer cust = quote.getCustomer();
        String ibmCustNum = cust == null ? "" : cust.getIbmCustNum();
        SubmittedQuoteAccess sbmtQtAccess = quote.getSubmittedQuoteAccess();
        String lob = header.getLob() == null ? "" : header.getLob().getCode();
        Partner payer = quote.getPayer();
        String cntryCode =  header.getCountry() == null ? "" : header.getCountry().getCode3();
        String  cntryCodeForOrder= "";
        if(isInPGS()){
        	cntryCodeForOrder = payer == null ? "" : payer.getCountry();
        }else{
        	cntryCodeForOrder = header.getCountry() == null ? "" : header.getCountry().getCode3();
        }
        boolean ELAFlag = header.isELAQuote();
        boolean isPreCreditCheckOptionDisplay = ButtonDisplayRuleFactory.singleton().isPreCreditCheckOptionDisplay(
                header);
        boolean gsaStatusFlag = (cust == null) ? false : cust.getGsaStatusFlag();

        boolean isDisplayOverrideExpDate = false;
        boolean isDisplayEnterAddiSBJust = false;
        boolean isDisplaySubmitRequest = false;
        boolean isDisplayReqICN = false;
        boolean isDisplayReqPreCreditCheck = false;
        boolean isDisplayUpdateQuote = false;
        boolean isDisplayCreateCopy = false;
        boolean isDisplayCreateCopyCheckbox = true;
        boolean isDisplayOrder = false;
        boolean isDisplayCancelQuote = false;
        boolean isDisplayApprAction = false;
        boolean isDisplayAddiComments = false;
        boolean isDisplaySaveComments = false;
        boolean isDisplayManualIntrvntnNeededMsg = false;
        boolean isDisplayQtProcessInSapMsg = false;
        boolean isDisplayNoCustEnrollMsg = false;
        boolean isDisplaySVPLevelNotMatchMsg = false;
        boolean isDisplayCtrctNumNotMatchMsg = false;
        boolean isDisplayHasOrderedMsg = false;
        boolean isDisplayCancelledQuoteMsg = false;
        boolean isDisplaySpecBidQuoteMsg = false;
        boolean isDisplaySubmitterAccessRequiredMsg = false;
        boolean isDisplayChannelSQOrderMsg = false;
        boolean isDisplayDldRichTextBtn = false;
        boolean isDisplayExecSmmryMsg = false;
        boolean isDisplayObsltPartMsg = false;
        boolean isDisplayExprtSprdshtBtn = false;
        boolean isDisplayExportQuoteAsSpreadsheet = false;
        boolean isDisplayElaSQOrderMsg = false;
        boolean canCancelApprovedBid = false;
        boolean isDisplayCntryNotAllowOrderMsg = false;
        boolean isDisplayOEMNotAllowOrderMsg = false;
        boolean isDisplayEffDateInFtrMsg = false;
        boolean isDispCp4PrcIncrButton = false;
        boolean isDispBidIterButton = false;
        boolean isDisplayNotAllowOrderForCustBlockMsg = false; //not allow order as a blocked customer - sods2.customer.sales_ord_block = '01'
        boolean isDispChgOutputOption = false;
        boolean isDispChgOutputOptionFlag = false;
        boolean isDispChgOutputOptionWithDivestedPartMsg = false;
        boolean isDisplayOrdWithoutOpptNumMsg = false;
        boolean isDisplayFct2PaQuoteTerminationMsg = false;
        boolean isDisplaySSPWithOutContract = false;
        boolean isDisplayOrderForDivestedPartMsg = false;
        boolean isDispCp4PrcIncr4DivestdPartMsg = false;
        boolean isDispBidIterDivestdPartMsg = false;
        boolean isDispCp4ExpiryDateButton = false;
        
        logContext.debug(this, "Submitted sales quote common button rules begin:");
        logContext.debug(this, "Web quote number is: " + header.getWebQuoteNum());
        logContext.debug(this, "User id is: " + user.getEmail());
        logContext.debug(this, "User access is: " + userAccess);
        logContext.debug(this, "SpeclBidFlag is: " + header.getSpeclBidFlag());
        logContext.debug(this, "userIsFirstAppGrpMember is: " + userIsFirstAppGrpMember());
        logContext.debug(this, "quoteHasNoneApproval is: " + quoteHasNoneApproval());
        logContext.debug(this, "userIsQuoteCreator is: " + userIsQuoteCreator());
        logContext.debug(this, "userIsQuoteEditor is: " + userIsQuoteEditor());
        logContext.debug(this, "userIsAnyEditor is: " + userIsAnyEditor());
        logContext.debug(this, "ReqstIbmCustNumFlag is: " + header.getReqstIbmCustNumFlag());
        logContext.debug(this, "ReqstPreCreditCheckFlag is: " + header.getReqstPreCreditCheckFlag());
        logContext.debug(this, "userIsPendingAppGrpMember is: " + userIsPendingAppGrpMember());
        logContext.debug(this, "userIsAnyAppGrpMember is: " + userIsAnyAppGrpMember());
        logContext.debug(this, "userIsReviewer is: " + userIsReviewer());
        logContext.debug(this, "userCanChangeBidExpDate is: " + userCanChangeBidExpDate());
        logContext.debug(this, "userCanApprove is: " + userCanApprove());
        logContext.debug(this, "userCanViewExecSummary is: " + userCanViewExecSummary());
        logContext.debug(this, "Quote stage code is: " + header.getQuoteStageCode());
        logContext.debug(this, "Sap quote number is: " + header.getSapQuoteNum());
        logContext.debug(this, "LOB is: " + lob);
        logContext.debug(this, "ELA flag is: " + ELAFlag);
        logContext.debug(this, "Country code is: " + cntryCode);
        logContext.debug(this, "isOrderButtonDisplay is: " + isOrderButtonDisplay(lob, ELAFlag, cntryCodeForOrder));
        logContext.debug(this, "isExprtSprdshtButtonDisplay is: " + isExprtSprdshtButtonDisplay(lob));
        logContext.debug(this, "Has obsolete parts flag is: " + header.getHasObsoletePartsFlag());
        logContext.debug(this, "Copied sb to increase price flag is: " + header.isCopied4PrcIncrQuoteFlag());
        logContext.debug(this, "Copied sb to change expiry date flag is: " + header.isExpDateExtendedFlag());

        if (sbmtQtAccess != null) {
            logContext.debug(this, "No status in 1 hour is: " + sbmtQtAccess.isNoStatusInOneHour());
            logContext.debug(this, "No status over 1 hour is: " + sbmtQtAccess.isNoStatusOverOneHour());
            logContext.debug(this, "Is has ordered is: " + sbmtQtAccess.isHasOrdered());
            logContext.debug(this, "No cust enroll is: " + sbmtQtAccess.isNoCustEnroll());
        }
        boolean isCustomerMatchDistributor = true;
        boolean isCustomerMatchReseller = true;
        if(quote != null && quote.getPayer() != null && user != null && user.getSiteNumber() != null){
            if(isInPGS() && !user.getSiteNumber().equals(quote.getPayer().getCustNum())){
            	isCustomerMatchDistributor = false;
            }
        }
        if(quote != null && quote.getReseller() != null && user != null && user.getSiteNumber() != null){
            if(isInPGS() && !user.getSiteNumber().equals(quote.getReseller().getCustNum())){
            	isCustomerMatchReseller = false;
            }
        }
        logContext.debug(this, "isCustomerMatchDistributor:"+isCustomerMatchDistributor);

        List overallStatuses = header.getQuoteOverallStatuses();
        if (overallStatuses != null) {
            for (int i = 0; i < overallStatuses.size(); i++) {
                CodeDescObj qtOS = (CodeDescObj) overallStatuses.get(i);
                if (qtOS != null)
                    logContext.debug(this, "Overall status contains: " + qtOS.getCodeDesc());
            }
        }

        // The special bid flag is true, and
        // The current user's access level is approver, and
        // The quote's overall status is Awaiting special bid approval, and
        // No approvers have approved the quote, and
        // The current user is a member of the first approval group listed on
        // the quote
        // 15.2 expiration date extension,add by Bourne.If a quote is expiration date extension quote,Approver can not
        // override   start day and expiration day. 
        isDisplayOverrideExpDate = (header.getSpeclBidFlag() == 1 && this.isAccessLevelApprover(userAccess, false)
                && header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR)
                && userCanChangeBidExpDate()&&!header.isExpDateExtendedFlag());

        if (this.isAccessLevelSubmitter(userAccess, true)) {
            if (header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_ADDI_INFO))
                isDisplayEnterAddiSBJust = this.userIsAnyEditor();
            else if (header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR))
                isDisplayEnterAddiSBJust = (this.userIsAnyEditor() && this.quoteHasNoneApproval());
        }

        boolean submitRequestBase = (!header
                .containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_CHG)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_REJECTED)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDER_ON_HOLD)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDERED_NOT_BILLED)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.BILLED_ORDER)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.CANCEL_TERMINATED) && !header
                .containsOverallStatus(SubmittedQuoteConstants.OverallStatus.EXPIRED_OR_OTHERS))
                && StringUtils.isNotBlank(header.getSapQuoteNum());

        isDisplayReqICN = (cust != null && StringUtils.isBlank(ibmCustNum) && header.getReqstIbmCustNumFlag() == 0
                && submitRequestBase && this.userIsAnyEditor()
                && !gsaStatusFlag);

        isDisplayReqPreCreditCheck = (header.getReqstPreCreditCheckFlag() == 0 && submitRequestBase
                && this.userIsAnyEditor() && isPreCreditCheckOptionDisplay);

        isDisplaySubmitRequest = (this.isAccessLevelSubmitter(userAccess, false) && submitRequestBase
                && (this.userIsAnyEditor()) && (isDisplayReqICN || isDisplayReqPreCreditCheck));

//        isDisplayUpdateQuote = (QuoteConstants.ACCESS_LEVEL_SUBMITTER == userAccess
//                && (header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_CHG)) && this
//                .userIsAnyEditor());

        // Any access level and overall status
        isDisplayCreateCopy = !this.isSubmitterWithoutEditPrivilege(userAccess, false) && (isCustomerMatchDistributor || isCustomerMatchReseller);

        // Remove reference CheckBox in PGS when copy quote created or submitted in SQO
        if(isInPGS()){
        	if(QuoteConstants.QUOTE_AUDIENCE_CODE_SQO.equals(this.quote.getQuoteHeader().getAudCode())
        	 || this.quote.getQuoteHeader().isSubmittedOnSQO()){
        		isDisplayCreateCopyCheckbox = false;
        	}
        }

        isDisplayDldRichTextBtn = true;

        isDisplayOrder = (
        		(this.isAccessLevelSubmitter(userAccess, true))
                && header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.READY_TO_ORDER)
                && (isInPGS() || QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(header.getFulfillmentSrc()))
                && (isInPGS() || this.userIsAnyEditor())
                && !QuoteConstants.QUOTE_STAGE_CODE_CANCEL.equalsIgnoreCase(header.getQuoteStageCode())
                && isOrderButtonDisplay(lob, ELAFlag, cntryCodeForOrder)
                && validateNewContract()
                && this.checkSubmitAccessinPGS(user)
                && isCustomerMatchDistributor
        		);
        
        isDisplaySSPWithOutContract = checkSSPContractCust(lob,isDisplayOrder);
        
        isDisplayOrder = (isDisplayOrder && !isDisplaySSPWithOutContract);
        
        
        if(isDisplayOrder && (header.getSaasTermCondCatFlag() != 2)) {
        	try {
				this.validateTou(false);
			} catch (QuoteException e) {
				logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
        }
        if(isDisplayOrder){
        	isDisplayOrder = !header.isHasDivestedPart();
        	isDisplayOrderForDivestedPartMsg = header.isHasDivestedPart();
        }
        
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isDisplayOrder is: "+isDisplayOrder);
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isDisplayOrderForDivestedPartMsg is: "+isDisplayOrderForDivestedPartMsg);
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] (this.isAccessLevelSubmitter(userAccess, true)) is: "+(this.isAccessLevelSubmitter(userAccess, true)));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.READY_TO_ORDER) is: "+header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.READY_TO_ORDER));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isInPGS() is: "+isInPGS());
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] (isInPGS() || QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(header.getFulfillmentSrc())) is: "+(isInPGS() || QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(header.getFulfillmentSrc())));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] (isInPGS() || this.userIsAnyEditor()) is: "+(isInPGS() || this.userIsAnyEditor()));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] !QuoteConstants.QUOTE_STAGE_CODE_CANCEL.equalsIgnoreCase(header.getQuoteStageCode()) is: "+!QuoteConstants.QUOTE_STAGE_CODE_CANCEL.equalsIgnoreCase(header.getQuoteStageCode()));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isOrderButtonDisplay(lob, ELAFlag, cntryCodeForOrder) is: "+isOrderButtonDisplay(lob, ELAFlag, cntryCodeForOrder));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] validateNewContract() is: "+validateNewContract());
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] this.checkSubmitAccessinPGS(user) is: "+this.checkSubmitAccessinPGS(user));
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isCustomerMatchDistributor is: "+isCustomerMatchDistributor);
        logContext.debug(this, "web quote["+header.getWebQuoteNum()+"] isDisplaySSPWithOutContract is: "+isDisplaySSPWithOutContract);
        
        if(isDisplayOrder && header.isSaasFCTToPAQuote()){
        	isDisplayFct2PaQuoteTerminationMsg = true;
        }

        if(isDisplayOrder
        		&& QuoteConstants.FULFILLMENT_DIRECT.equals(this.quote.getQuoteHeader().getFulfillmentSrc())
        		&& StringUtils.isBlank(this.quote.getQuoteHeader().getOpprtntyNum())
        		&& (this.quote.getQuoteHeader().isHasNewLicencePart() || this.quote.getQuoteHeader().isHasSaasSubNoReNwPart() || this.quote.getQuoteHeader().isHasAppMainPart())
        		){
        	isDisplayOrdWithoutOpptNumMsg = true;
        }

        isDisplayCancelQuote = (this.isAccessLevelSubmitter(userAccess, true)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_CHG)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_REJECTED)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDER_ON_HOLD)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDERED_NOT_BILLED)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.BILLED_ORDER)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.CANCEL_TERMINATED)
                && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.EXPIRED_OR_OTHERS)
                && this.userIsAnyEditor() && !QuoteConstants.QUOTE_STAGE_CODE_CANCEL.equalsIgnoreCase(header
                .getQuoteStageCode()))
                && StringUtils.isNotBlank(header.getSapQuoteNum())
                && isCustomerMatchDistributor;
        if ( isDisplayCancelQuote && header.isPGSQuote() && this.isInPGS() && header.isSubmittedOnSQO() )
        {
        	isDisplayCancelQuote = false;
        }

        // whether the approver can approve this special bid.
        isDisplayApprAction = (this.isAccessLevelApprover(userAccess, false)
                && (header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR))
                && this.userCanApprove());

        // whether the approver can add additional comments.
        if (this.isAccessLevelApprover(userAccess, false) && this.userIsAnyAppGrpMember()) {
            if (header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR))
                isDisplayAddiComments = !userIsPendingAppGrpMember() && !this.userCanApprove();
            else
                isDisplayAddiComments = true;
        }

        isDisplaySaveComments = this.userIsReviewer();
       //If there are only SaaS parts on the quote, not PA,PAE&FCT quote, do not show any export options
        isDisplayExprtSprdshtBtn = ((this.isAccessLevelSubmitter(userAccess, false)
        		|| this.isAccessLevelApprover(userAccess, false)  || this.isAccessLevelReader(userAccess, false)
				|| this.isAccessLevelEcare(userAccess, false)  || this.isAccessLevelAdministrator(userAccess, false) )
                && this.isExprtSprdshtButtonDisplay(lob) && !(header.isOnlySaaSParts()&& (!QuoteConstants.LOB_PA.equalsIgnoreCase(lob) &&
                		!QuoteConstants.LOB_PAE.equalsIgnoreCase(lob) && !QuoteConstants.LOB_FCT.equalsIgnoreCase(lob)
                		|| header.isFCTToPAQuote()))
               );

        // open export for PGS
        if(QuoteConstants.APP_CODE_PGS.equalsIgnoreCase(applCode) ){
        	isDisplayExprtSprdshtBtn = true;
        }

        if ( isDisplayExprtSprdshtBtn && header.isUnderEvaluation() )
        {
        	isDisplayExprtSprdshtBtn = false;
        }

        if ( header.isUnderEvaluation() )
        {
        	isDisplayExportQuoteAsSpreadsheet = true;
        }

        isDisplayOEMNotAllowOrderMsg = header.isOEMQuote();
        if (!isDisplayOrder && !isDisplayOEMNotAllowOrderMsg && !header.isUnderEvaluation()) {
            isDisplaySubmitterAccessRequiredMsg = !(this.isAccessLevelSubmitter(userAccess, true));

            isDisplayChannelSQOrderMsg = this.isNotInPGS() && (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(header
                    .getFulfillmentSrc()) && !isDisplaySubmitterAccessRequiredMsg);

            isDisplayElaSQOrderMsg = ( ELAFlag
            		&& !isDisplaySubmitterAccessRequiredMsg
            		&& !isDisplayChannelSQOrderMsg
            		&& !isDisplayCancelledQuoteMsg);

            isDisplayCancelledQuoteMsg = ((header
                    .containsOverallStatus(SubmittedQuoteConstants.OverallStatus.CANCEL_TERMINATED) || header
                    .containsOverallStatus(SubmittedQuoteConstants.OverallStatus.EXPIRED_OR_OTHERS))
                    && !isDisplaySubmitterAccessRequiredMsg && !isDisplayChannelSQOrderMsg);

            isDisplayHasOrderedMsg = ((sbmtQtAccess == null ? false : sbmtQtAccess.isHasOrdered())
                    && !isDisplaySubmitterAccessRequiredMsg && !isDisplayChannelSQOrderMsg
                    && !isDisplayCancelledQuoteMsg&& !isDisplayElaSQOrderMsg);

            isDisplaySpecBidQuoteMsg = ((header
                    .containsOverallStatus(SubmittedQuoteConstants.OverallStatus.AWAITING_SPEC_BID_APPR)
                    || header
                            .containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_ADDI_INFO) || header
                    .containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_RETURN_FOR_CHG))
                    && !isDisplaySubmitterAccessRequiredMsg
                    && !isDisplayChannelSQOrderMsg && !isDisplayCancelledQuoteMsg
                    && !isDisplayHasOrderedMsg && !isDisplayElaSQOrderMsg);

            isDisplayQtProcessInSapMsg = ((sbmtQtAccess == null ? false : sbmtQtAccess.isNoStatusInOneHour())
                    && !isDisplaySubmitterAccessRequiredMsg && !isDisplayChannelSQOrderMsg
                    && !isDisplayCancelledQuoteMsg && !isDisplayHasOrderedMsg
                    && !isDisplaySpecBidQuoteMsg  && !isDisplayElaSQOrderMsg);

            isDisplayManualIntrvntnNeededMsg = ((sbmtQtAccess == null ? false : sbmtQtAccess.isNoStatusOverOneHour())
                    && !isDisplaySubmitterAccessRequiredMsg && !isDisplayChannelSQOrderMsg
                    && !isDisplayCancelledQuoteMsg && !isDisplayHasOrderedMsg
                    && !isDisplaySpecBidQuoteMsg&& !isDisplayQtProcessInSapMsg
                    && !isDisplayElaSQOrderMsg);

            isDisplayNoCustEnrollMsg = ((sbmtQtAccess == null ? false : sbmtQtAccess.isNoCustEnroll())
                    && !isDisplaySubmitterAccessRequiredMsg && !isDisplayChannelSQOrderMsg
                    && !isDisplayCancelledQuoteMsg && !isDisplayHasOrderedMsg
                    && !isDisplaySpecBidQuoteMsg && !isDisplayQtProcessInSapMsg && !isDisplayManualIntrvntnNeededMsg);

            isDisplaySVPLevelNotMatchMsg = (!verifyTransSVPLevelForNewCtrct()
                    && !isDisplaySubmitterAccessRequiredMsg && !isDisplayChannelSQOrderMsg
                    && !isDisplayCancelledQuoteMsg && !isDisplayHasOrderedMsg
                    && !isDisplaySpecBidQuoteMsg && !isDisplayQtProcessInSapMsg
                    && !isDisplayManualIntrvntnNeededMsg && !isDisplayNoCustEnrollMsg);

            isDisplayCtrctNumNotMatchMsg = (!verifyAgrmntNumForNewCtrct()
                    && !isDisplaySubmitterAccessRequiredMsg  && !isDisplayChannelSQOrderMsg
                    && !isDisplayCancelledQuoteMsg && !isDisplayHasOrderedMsg
                    && !isDisplaySpecBidQuoteMsg && !isDisplayQtProcessInSapMsg
                    && !isDisplayManualIntrvntnNeededMsg && !isDisplayNoCustEnrollMsg );

            if ( !isOrderButtonDisplay(lob, false, cntryCodeForOrder)) {
                isDisplayCntryNotAllowOrderMsg = true;
            }

            isDisplayEffDateInFtrMsg = isQuoteStartDateAfterToday() && !isDisplayChannelSQOrderMsg && !isDisplayCntryNotAllowOrderMsg;

            //if customer is blocked, show message
            isDisplayNotAllowOrderForCustBlockMsg = (
	        		   (cust!=null)
	        		&& cust.isCustOrdBlcked()
                    && !isDisplaySubmitterAccessRequiredMsg  && !isDisplayChannelSQOrderMsg
                    && !isDisplayCancelledQuoteMsg && !isDisplayHasOrderedMsg
                    && !isDisplaySpecBidQuoteMsg && !isDisplayQtProcessInSapMsg
                    && !isDisplayManualIntrvntnNeededMsg && !isDisplayNoCustEnrollMsg
                    && !isDisplayCntryNotAllowOrderMsg
                    && !isDisplayEffDateInFtrMsg
                    );
        }

        isDisplayExecSmmryMsg = (header.getSpeclBidFlag() == 1 && !userCanViewExecSummary());

        isDisplayObsltPartMsg = header.getHasObsoletePartsFlag();

        canCancelApprovedBid = canCancelApprovedBid(userAccess);

        //display creat copy approved sb for price increase button if qutoe is approved sb
        isDispCp4PrcIncrButton = header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_APPROVED)
                                 && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.EXPIRED_OR_OTHERS)
                                 && !header.getCmprssCvrageFlag()
                                 && !QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC.equalsIgnoreCase(header.getQuoteStageCode())
                                 && userIsAnyEditor()
                                 && header.hasDiscountableItems()
                                 && !header.isBidIteratnQt()
                                 && !this.isSubmitterWithoutEditPrivilege(userAccess, true)
                                 ;
        if(isDispCp4PrcIncrButton){
        	                     isDispCp4PrcIncrButton = !header.isHasDivestedPart();
                                 isDispCp4PrcIncr4DivestdPartMsg = header.isHasDivestedPart();
        	}
        
        //display creat copy for changing expiry date button 
        isDispCp4ExpiryDateButton =  header.containsOverallStatus4Cpexdate(SubmittedQuoteConstants.OverallStatus.SPEC_BID_APPROVED)
                                 && !(quote.getQuoteBusinessDomain().isQuoteHasAppliancePart(quote.getLineItemList()))
        		                 && !QuoteConstants.LOB_FCT.equals(lob)
        		                 &&	!header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_REJECTED)        		                 
        		                 && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDER_ON_HOLD)
        		                 && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDERED_NOT_BILLED)
        		                 && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.BILLED_ORDER)
        		                 && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.CANCEL_TERMINATED)
        		                 && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDER_REJECTED)
        		                 && !this.isSubmitterWithoutEditPrivilege(userAccess, true)
        		                 && !header.isBidIteratnQt()
        		                 && !header.isCopied4PrcIncrQuoteFlag()
        		                 && !header.isExpDateExtendedFlag()
        		                 && userIsAnyEditor()
        		                 ;
       isDispBidIterButton = header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_APPROVED)
                               && !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.EXPIRED_OR_OTHERS)
                               && userIsAnyEditor()
                               && header.isQtEligible4BidIteratn()
                               && !header.isBidIteratnQt()
                               && !header.isELAQuote()
                               && !this.isSubmitterWithoutEditPrivilege(userAccess, true)
                               && !QuoteConstants.LOB_SSP.equals(lob)
                               ;
       if(isDispBidIterButton){
    	                        isDispBidIterButton = !header.isHasDivestedPart();
                                isDispBidIterDivestdPartMsg = header.isHasDivestedPart();
                                   }
        isDispChgOutputOption =  header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.SPEC_BID_APPROVED)
    								&& !header.containsOverallStatus(SubmittedQuoteConstants.OverallStatus.EXPIRED_OR_OTHERS)
    								&& userIsAnyEditor()
    								&& !this.isSubmitterWithoutEditPrivilege(userAccess, true)
    								&& StringUtils.isNotBlank(header.getSapQuoteNum())
    								&& !header.isRebillCreditOrder()
    								&& !header.isELAQuote();
        isDispChgOutputOptionFlag = isDispChgOutputOption && !header.isHasDivestedPart();
        isDispChgOutputOptionWithDivestedPartMsg = isDispChgOutputOption && header.isHasDivestedPart(); 

        rules.put(QuoteCapabilityProcess.DISPLAY_OVERRIDE_EXP_DATE, Boolean.valueOf(isDisplayOverrideExpDate));
        rules.put(QuoteCapabilityProcess.DISPLAY_SUBMITTER_ENTER_ADDI_SB_JUST, Boolean.valueOf(isDisplayEnterAddiSBJust));
        rules.put(QuoteCapabilityProcess.DISPLAY_SUBMITTER_SUBMIT_REQUEST, Boolean.valueOf(isDisplaySubmitRequest));
        rules.put(QuoteCapabilityProcess.DISPLAY_ICN_REQUIRED, Boolean.valueOf(isDisplayReqICN));
        rules.put(QuoteCapabilityProcess.DISPLAY_PRE_CREDIT_CHK_REQUIRED, Boolean.valueOf(isDisplayReqPreCreditCheck));
        rules.put(QuoteCapabilityProcess.DISPLAY_UPDATE_QUOTE_BTN, Boolean.valueOf(isDisplayUpdateQuote));
        rules.put(QuoteCapabilityProcess.DISPLAY_CREATE_COPY_BTN, Boolean.valueOf(isDisplayCreateCopy));
        rules.put(QuoteCapabilityProcess.DISPLAY_CREATE_COPY_CHECKBOX_MSG, Boolean.valueOf(isDisplayCreateCopyCheckbox));
        rules.put(QuoteCapabilityProcess.DISPLAY_ORDER_BTN, Boolean.valueOf(isDisplayOrder));
        rules.put(QuoteCapabilityProcess.DISPLAY_CANCEL_QUOTE_BTN, Boolean.valueOf(isDisplayCancelQuote));
        rules.put(QuoteCapabilityProcess.DISPLAY_APPROVER_ACTION, Boolean.valueOf(isDisplayApprAction));
        rules.put(QuoteCapabilityProcess.DISPLAY_APPROVER_ADDI_COMMENTS, Boolean.valueOf(isDisplayAddiComments));
        rules.put(QuoteCapabilityProcess.DISPLAY_REVIEWER_SAVE_COMMENTS, Boolean.valueOf(isDisplaySaveComments));
        rules.put(QuoteCapabilityProcess.DISPLAY_MANUAL_INTRVNTN_NEEDED_MSG, Boolean.valueOf(isDisplayManualIntrvntnNeededMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_QT_PROCESS_IN_SAP_MSG, Boolean.valueOf(isDisplayQtProcessInSapMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_NO_CUST_ENROLL_MSG, Boolean.valueOf(isDisplayNoCustEnrollMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_HAS_ORDERED_MSG, Boolean.valueOf(isDisplayHasOrderedMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CANCELLED_QT_MSG, Boolean.valueOf(isDisplayCancelledQuoteMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_SB_QT_MSG, Boolean.valueOf(isDisplaySpecBidQuoteMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_SBMT_ACL_REQ_MSG, Boolean.valueOf(isDisplaySubmitterAccessRequiredMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CHANNEL_QT_ORDER_MSG, Boolean.valueOf(isDisplayChannelSQOrderMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_DOWNLOAD_RICH_TEXT_BTN, Boolean.valueOf(isDisplayDldRichTextBtn));
        rules.put(QuoteCapabilityProcess.DISPLAY_EXEC_SMMRY_MSG, Boolean.valueOf(isDisplayExecSmmryMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_HAS_OBSLT_PART_MSG, Boolean.valueOf(isDisplayObsltPartMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_EXPRT_SPRDSHT_BTN, Boolean.valueOf(isDisplayExprtSprdshtBtn));
        rules.put(QuoteCapabilityProcess.DISPLAY_EXPORT_QT_AS_SPREADSHEET_BTN, Boolean.valueOf(isDisplayExportQuoteAsSpreadsheet));
        rules.put(QuoteCapabilityProcess.DISPLAY_ELA_QT_ORDER_MSG, Boolean.valueOf(isDisplayElaSQOrderMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CANCEL_APPROVED_BID, Boolean.valueOf(canCancelApprovedBid));
        rules.put(QuoteCapabilityProcess.DISPLAY_SVP_LVL_NOT_MATCH_MSG, Boolean.valueOf(isDisplaySVPLevelNotMatchMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CTRCT_NUM_NOT_MATCH_MSG, Boolean.valueOf(isDisplayCtrctNumNotMatchMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_EFF_DATE_IN_FTR_MSG, Boolean.valueOf(isDisplayEffDateInFtrMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_OEM_NOT_ALLOW_ORDER_MSG, Boolean.valueOf(isDisplayOEMNotAllowOrderMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_CNTRY_NOT_ALLOW_ORDER_MSG, Boolean.valueOf(isDisplayCntryNotAllowOrderMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_COPY_SB_FOR_PRICE_INCREASE, Boolean.valueOf(isDispCp4PrcIncrButton));
        rules.put(QuoteCapabilityProcess.DISPLAY_BID_ITERATION_BTN, Boolean.valueOf(isDispBidIterButton));
        rules.put(QuoteCapabilityProcess.DISPLAY_NOT_ALLOW_ORDER_FOR_CUST_BLOCK_MSG, Boolean.valueOf(isDisplayNotAllowOrderForCustBlockMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_COPY_SB_FOR_CHNAGE_OUTPUT, Boolean.valueOf(isDispChgOutputOptionFlag));
        rules.put(QuoteCapabilityProcess.DISPLAY_PGS_SUBMITTER, Boolean.valueOf(checkSubmitAccessinPGS(user)));
        rules.put(QuoteCapabilityProcess.DISPLAY_ORD_WITHOUT_OPPTNUM_MSG, Boolean.valueOf(isDisplayOrdWithoutOpptNumMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_FCTTOPA_QUOTE_TERMINATION_MSG, Boolean.valueOf(isDisplayFct2PaQuoteTerminationMsg));
        rules.put(QuoteCapabilityProcess.DISPLAY_SSP_NO_CONTACT, Boolean.valueOf(isDisplaySSPWithOutContract));
        rules.put(QuoteCapabilityProcess.NOT_COPY_CHNAGE_OUTPUT_FOR_DIVESTED_PART, Boolean.valueOf(isDispChgOutputOptionWithDivestedPartMsg));
       
        //added by Ma Chao
        rules.put(QuoteCapabilityProcess.NOT_ALLOW_ORDER_FOR_DIVESTED_PART, Boolean.valueOf(isDisplayOrderForDivestedPartMsg));
        rules.put(QuoteCapabilityProcess.NOT_COPY_PRICE_INCREASE_FOR_DIVESTED_PART, Boolean.valueOf(isDispCp4PrcIncr4DivestdPartMsg));
        rules.put(QuoteCapabilityProcess.NOT_COPY_BID_ITERATION_FOR_DIVESTED_PART, Boolean.valueOf(isDispBidIterDivestdPartMsg));
        
        //added by Teddy to verify the expiry date copy type
        rules.put(QuoteCapabilityProcess.DISPLAY_COPY_FOR_EXPRIY_DATE,Boolean.valueOf(isDispCp4ExpiryDateButton));
        return rules;
    }
	
    protected int isPartnerRegnCntryCrncyValid() {
        // 0 : Valid data
        // 1 : Invalid region
        // 2 : Invalid country
        // 3 : Invalid currency

        QuoteHeader header = quote.getQuoteHeader();
        Customer cust = quote.getCustomer();
        Partner payer = quote.getPayer();
        Partner reseller = quote.getReseller();

        String geo = header.getCountry().getSpecialBidAreaCode();
        String custCntry = cust.getCountryCode();
        String custSalesOrg = cust.getSalesOrg();
        String custCurrency = cust.getCurrencyCode();

        if (header.isPPSSQuote()) {
            return 0;
        } else if (header.isFCTQuote()) {

            if (payer != null) {
                String payerCntry = StringUtils.trimToEmpty(payer.getCountry());
                String payerSalesOrg = StringUtils.trimToEmpty(payer.getSapSalesOrgCode());
                String payerCurrency = StringUtils.trimToEmpty(payer.getCurrencyCode());

                if (QuoteConstants.GEO_AP.equalsIgnoreCase(geo)) {
                    if (!custSalesOrg.equalsIgnoreCase(payerSalesOrg))
                        return 1;

                    // For AP FCT quote, customer currency should match the
                    // payer's currency
                    if (custCurrency == null || payerCurrency == null || !payerCurrency.equalsIgnoreCase(custCurrency))
                        return 3;
                }
            }
        } else if (header.isPAQuote() || header.isPAEQuote()) {

            if (payer != null) {
                String payerCntry = StringUtils.trimToEmpty(payer.getCountry());
                String payerSalesOrg = StringUtils.trimToEmpty(payer.getSapSalesOrgCode());

                if (QuoteConstants.GEO_AP.equalsIgnoreCase(geo)) {
                    if (!custSalesOrg.equalsIgnoreCase(payerSalesOrg))
                        return 1;
                }
            }

            if (reseller != null) {
                String resellerCntry = StringUtils.trimToEmpty(reseller.getCountry());
                String resellerSalesOrg = StringUtils.trimToEmpty(reseller.getSapSalesOrgCode());

                if (QuoteConstants.GEO_AP.equalsIgnoreCase(geo)) {
                    if (!custSalesOrg.equalsIgnoreCase(resellerSalesOrg))
                        return 1;
                }
            }
        }

        return 0;
    }

    protected void validateQuoteExpiredDate() {
        if (quote == null || quote.getQuoteHeader() == null)
            return;

        QuoteHeader header = quote.getQuoteHeader();
        Date quoteExpDate = header.getQuoteExpDate();
        int qtMaxExpDays = quote.getQuoteHeader().getQuoteExpDays();
        int tempMaxExpDays = 0 ;

        // 1. Check regular quote expiration date rule
        boolean valid = this.checkQuoteExpirationDate(qtMaxExpDays);
        if (!valid) {
            validateResult.put(QuoteCapabilityProcess.EXPIRED_DATE_NOT_WITHIN_ACTIVIE_DAYS,
                    String.valueOf(qtMaxExpDays));
            return;
      }

      //2. just validate for add-on/trade-up/co-term,chosen expiration date can not be later than the calculated expiration date
        if (header.isAddTrdOrCotermFlag()) {
        	Date cacldExpiredDate = QuoteCommonUtil.calcQuoteExpirationDate(quote);
        	if( cacldExpiredDate != null && quoteExpDate.after(cacldExpiredDate)) {
        		validateResult.put(QuoteCapabilityProcess.EXPIRED_DATE_AFTER_CALCD_EXPIRED_DATE, DateHelper.getDateByFormat(cacldExpiredDate, "dd MMM yyyy"));
        		return;
        	}
        }


        // 3. Check related prorated maintenance parts first
        Integer reltdMaxExpDays = this.getMaxExpDaysForRelatedMaintenanceParts();
        if (reltdMaxExpDays != null) {
            int iReltdMaxExpDays = reltdMaxExpDays.intValue();
            tempMaxExpDays = iReltdMaxExpDays < qtMaxExpDays ? iReltdMaxExpDays : qtMaxExpDays ;
            valid = this.checkQuoteExpirationDate(tempMaxExpDays);

            if (!valid) {
                validateResult.put(QuoteCapabilityProcess.EXP_DATE_NOT_WITHIN_SBMT_MONTH, Boolean.TRUE);
            }
            return;
        }

        // 4. Check unrelated prorated maintenance parts earliest start date
        Integer unrltdMaxExpDays = this.getMaxExpDaysForUnrelatedMaintenanceParts();
        if (unrltdMaxExpDays != null) {
            int iUnReltdMaxExpDays = unrltdMaxExpDays.intValue();
            tempMaxExpDays = iUnReltdMaxExpDays < qtMaxExpDays ? iUnReltdMaxExpDays : qtMaxExpDays ;
            valid = this.checkQuoteExpirationDate(tempMaxExpDays);

            if (!valid) {
                validateResult.put(QuoteCapabilityProcess.EXPIRED_DATE_NOT_WITHIN_ACTIVIE_DAYS,
                        String.valueOf(tempMaxExpDays));
            }
            return;
        }
    }

    public Map validateUpdateQuotePartners() {
        QuoteHeader header = quote.getQuoteHeader() ;
        int isValid = isPartnerRegnCntryCrncyValid() ;
        if (isValid == 1) {
            validateResult.put(QuoteCapabilityProcess.CUST_PARTNER_REGION_MISMATCH, Boolean.TRUE);
        }
        if (isValid == 2) {
            validateResult.put(QuoteCapabilityProcess.CUST_PARTNER_COUNTRY_MISMATCH, Boolean.TRUE);
        }
        if (isValid == 3) {
            validateResult.put(QuoteCapabilityProcess.CUST_PAYER_CURRENCY_MISMATCH, Boolean.TRUE);
        }

        QuoteCommonUtil commonUtil = QuoteCommonUtil.create(this.quote);
        if (!commonUtil.isResellerAuthorizedToAllWithDistrbtr()) {
            validateResult.put(QuoteCapabilityProcess.RSEL_NOT_AUTH_TO_PORTFOLIO, Boolean.TRUE);
        }

        String sapDistChannel = header.getSapDistribtnChnlCode();
        String calcDistChannel = commonUtil.calcSAPDistributionChannel();

        if (!sapDistChannel.equalsIgnoreCase(calcDistChannel)) {
            validateResult.put(QuoteCapabilityProcess.WRNG_RSEL_DSTRBTR_CMBNTN, Boolean.TRUE);
        }

        String custCurrency = quote.getCustomer() == null ? null : quote.getCustomer().getCurrencyCode();
        String ptnrCurrency = quote.getPayer() == null ? null : StringUtils.trimToEmpty(quote.getPayer()
                .getCurrencyCode());

        if (header.getSpeclBidFlag() == 1) {
            if (custCurrency == null || !custCurrency.equalsIgnoreCase(ptnrCurrency)) {
                validateResult.put(QuoteCapabilityProcess.CUST_PARTNER_CURRENCY_MISMATCH, Boolean.TRUE);
            }
        }

        validatePartnerTierType();

        return validateResult;
    }

    protected boolean validateApprvdBidExpDate() {

        Date expireDate = quote.getQuoteHeader().getQuoteExpDate();

        if (expireDate == null)
            return false;

        Calendar curr = Calendar.getInstance();
        //Check to make sure the quot expiration date is not before today's date
        Date now = curr.getTime();
        //Remove time from today's date to compare date only, quote expiration date can be today's date for FCT to PA RQs
        Date currDate = DateUtils.truncate(now, Calendar.DATE);

        if (currDate.after(expireDate))
            return false;
        else
            return true;
    }

    public boolean canCnvrtToSalesQuote() {
        return false;
    }

    public boolean canCnvrtToSpeclBid() {
        return false;
    }

    public void validateCreditedOrderRebill() {
        if (quote.getQuoteHeader().getSpeclBidFlag() == 1) {
            if (quote.getSpecialBidInfo().isOrigSalesOrdNumInvld()) {
                validateResult.put(QuoteCapabilityProcess.ORIG_SALES_ORD_NUM_INVALID, Boolean.TRUE);
            }
        }
    }

    public boolean verifyTransSVPLevelForNewCtrct() {

        if (quote.getQuoteHeader().getWebCustId() > 0 || quote.getQuoteHeader().isCreateCtrctFlag()) {
            // quote has a new customer or a assigned/created contract
            Customer cust = quote.getCustomer();

            if (cust.isACACustomer() || cust.isGOVCustomer() || cust.isXSPCustomer()) {
                // For ACA, GOV, XSP agreement types
                return cust.isEnrolledCtrctTranPrcLvlValid();
            }
            else {
                // For ASE, STD agreement type
                return true;
            }
        }
        else {
            // quote has an existing PA customer without new contract
            return true;
        }
    }

    public boolean verifyAgrmntNumForNewCtrct() {

        if (quote.getQuoteHeader().getWebCustId() > 0 || quote.getQuoteHeader().isCreateCtrctFlag()) {
            // quote has a new customer or a assigned/created contract
            Customer cust = quote.getCustomer();

            if (cust.isAddiSiteCustomer()) {
                // For ASE agreement type
                return cust.isEnrolledCtrctNumValid();
            }
            else {
                // For ACA, GOV, STD, XSP, STL agreement type
                return true;
            }
        }
        else {
            // quote has an existing PA customer without new contract
            return true;
        }
    }

    public boolean validateNewContract() {

        return (verifyTransSVPLevelForNewCtrct() && verifyAgrmntNumForNewCtrct());
    }
    
    protected boolean isQuoteStartDateAfterToday() {

        Date quoteStartDate = quote.getQuoteHeader().getQuoteStartDate();
        if (quoteStartDate == null)
            return false;

        Date today = Calendar.getInstance().getTime();
        Date currDate = DateUtils.truncate(today, Calendar.DATE);

        logContext.debug(this, "Current date is " + currDate);
        logContext.debug(this, "Quote start date is " + quoteStartDate);

        return quoteStartDate.after(currDate);
    }

    public void  validateRnwalMdlC()throws QuoteException{
    	try {
			if(!QuoteCommonUtil.validateRnwalMdlC(quote)){
				validateResult.put(QuoteCapabilityProcess.CANNOT_CALC_SAAS_CONF_DUE_END_DATE_MESSAGE,Boolean.TRUE);
			}
		} catch (TopazException e) {
			throw new QuoteException(e);
		}
    }

    private void validateRemingTermForRewalMdl()throws QuoteException{
    	try {
			if (!QuoteCommonUtil.validateRemingTermForRewalMdl(quote)){
				validateResult.put(QuoteCapabilityProcess.REMAINING_SUBSCRPT_TERM_CANNOT_LESS_THAN_ZERO, Boolean.TRUE);
			}
		} catch (TopazException e) {
			throw new QuoteException(e);
		}
    }

    private void validateServiceDate() {
        ppConfigurationValidatorKeys.add(PartsPricingConfigurationValidatorContainer.SERVICE_DATE_VALIDATOR);
    }
}
