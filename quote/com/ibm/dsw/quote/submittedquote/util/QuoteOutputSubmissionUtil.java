/*
 * Created on Sep 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;

/**
 * @author zhangln
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QuoteOutputSubmissionUtil {

    /**
     * Whether display 'Request ICN for customer' option
     * @param quote
     * @return true to display
     */
    public static boolean isRequestedIBMCustomerNumber(Quote quote) {
        Customer customer = quote.getCustomer();

        boolean gsaStatusFlag = customer == null ? false : customer.getGsaStatusFlag();
        // Get ICN display logic from config file
        boolean isICNRquestOptionDisplay = ButtonDisplayRuleFactory.singleton().isRquestICNOptionDisplay(quote.getQuoteHeader());

        // A new customer was created as the quote customer and it is not US Federal customer,
        // or an existing customer was selected, and that customer does not have an ICN.
        if ( ( quote.getQuoteHeader().hasNewCustomer()
                || ( quote.getQuoteHeader().hasExistingCustomer() && StringUtils.isBlank(quote.getCustomer().getIbmCustNum())) )
                && !gsaStatusFlag
                && isICNRquestOptionDisplay )
            return true;
        else
            return false;
    }

    /**
     * Get value of 'Request ICN for customer' option
     * @param quote
     * @return true means selected
     */
    public static boolean getRequestedIBMCustomerNumberFlagValue(Quote quote) {
        return quote.getQuoteHeader().getReqstIbmCustNumFlag() == 1 ;
    }

    /**
     * Whether display 'Request a pre-credit check' option
     * @param quote
     * @return true to display
     */
    public static boolean isPreCreditCheckOptionDisplay(Quote quote) {
    	return ButtonDisplayRuleFactory.singleton().isPreCreditCheckOptionDisplay(quote.getQuoteHeader());
    }


    /**
     * Get value of 'Request a pre-credit check' option
     * @param quote
     * @return true means selected
     */
    public static boolean getPreCreditCheckOptionDisplayFlagValue(Quote quote) {
        return quote.getQuoteHeader().getReqstPreCreditCheckFlag() == 1 ;
    }

    /**
     * Whether display 'NOT show tax on the quote output' option
     * @param quote
     * @return true to display submission option
     */
    public static boolean isTaxOnQuoteOutput(Quote quote) {
        return (!isRenewalQuote(quote) || isOpenedStatus(quote));
    }

    public static boolean isRenewalQuote(Quote quote){
        return quote.getQuoteHeader().getQuoteTypeCode().equals(QuoteConstants.QUOTE_TYPE_RENEWAL);
    }

    public static boolean isOpenedStatus(Quote quote){
        if (isRenewalQuote(quote)) {
            return quote.containsWebPrimaryStatus(QuoteConstants.RENEWALSTATUS_CODE_OPEN)
                    || quote.containsWebPrimaryStatus(QuoteConstants.RENEWALSTATUS_CODE_BALOPEN);
        } else {
            return false;
        }
    }

    /**
     * Get value of 'NOT show tax on the quote output' option
     * @param quote
     * @return true means selected
     */
    public static boolean getTaxOnQuoteOutputFlagValue(Quote quote) {
        return quote.getQuoteHeader().getInclTaxFinalQuoteFlag() == 0 ;
    }

    /**
     * Whether display 'NOT show line item pricing or points (if applicable) on output' option
     * @param quote
     * @return true to display submission option
     */
    public static boolean isLiPrcOrPointsOnQuoteOutput(Quote quote) {
        boolean isDisplayNoPrcOrPointsOutput = ButtonDisplayRuleFactory.singleton().isDisplayNoPrcOrPointsOutput(
                quote.getQuoteHeader());
        String lob = quote.getQuoteHeader().getLob().getCode();
        return ((!isRenewalQuote(quote) || isOpenedStatus(quote)) && isDisplayNoPrcOrPointsOutput)&&!QuoteConstants.LOB_SSP.equalsIgnoreCase(lob);
    }

    /**
     * Get value of 'NOT show line item pricing or points (if applicable) on output' option
     * @param quote
     * @return true means selected
     */
    public static boolean getLiPrcOrPointsOnQuoteOutputFlagValue(Quote quote) {
        return quote.getQuoteHeader().getIncldLineItmDtlQuoteFlg() == 0 ;
    }

    /**
     * Whether display 'E-mail to the quote contact' option
     * @param quote
     * @return true to display submission option
     */
    public static boolean isEmailQuoteContact(Quote quote) {
        return ((!isRenewalQuote(quote) && isDirect(quote))
                || isShowSendQuoteToCustText(quote)) && !hasAddiSiteCustomer(quote);
    }

    public static boolean isDirect(Quote quote) {
        return quote.getQuoteHeader().getFulfillmentSrc().equals(QuoteConstants.FULFILLMENT_DIRECT);
    }

    public static boolean isShowSendQuoteToCustText(Quote quote) {
        return !isRenewalQuote(quote) && isChannel(quote)
        	&& ( quote.getQuoteHeader().isResellerToBeDtrmndFlag()
        	        || quote.getQuoteHeader().isDistribtrToBeDtrmndFlag() );
    }

    public static boolean isChannel(Quote quote) {
        return quote.getQuoteHeader().getFulfillmentSrc().equals(QuoteConstants.FULFILLMENT_CHANNEL);
    }

    public static boolean hasAddiSiteCustomer(Quote quote) {
        Customer customer = quote.getCustomer();
	    if (customer != null) {
	        return customer.isAddiSiteCustomer();
	    } else {
	        return false;
	    }
    }

    /**
     * Get value of 'E-mail to the quote contact' option
     * @param quote
     * @return true means selected
     */
    public static boolean getEmailQuoteContactFlagValue(Quote quote) {
        return quote.getQuoteHeader().getSendQuoteToQuoteCntFlag() == 1 ;
    }

    /**
     * Whether display 'E-mail to customer's primary contact' option
     * @param quote
     * @return true to display submission option
     */
    public static boolean isEmailQuotePrimaryContact(Quote quote) {
        return (((!isRenewalQuote(quote) && (isDirect(quote) || isNotSpec(quote)))
                || (isRenewalQuote(quote) && (isDirect(quote) || isNotSpec(quote))
                        && isOpenedStatus(quote) && !quote.getQuoteHeader().isFCTQuote()))
                || isShowSendQuoteToCustText(quote)) && !hasAddiSiteCustomer(quote);
    }

    public static boolean isNotSpec(Quote quote) {
        return quote.getQuoteHeader().getFulfillmentSrc().equals(QuoteConstants.FULFILLMENT_NOT_SPECIFIED);
    }

    /**
     * Get value of 'E-mail to customer's primary contact' option
     * @param quote
     * @return true means selected
     */
    public static boolean getEmailQuotePrimaryContactFlagValue(Quote quote){
        return quote.getQuoteHeader().getSendQuoteToPrmryCntFlag() == 1 ;
    }

    /**
     * Whether display 'E-mail to the following e-mail address' option
     * @param quote
     * @return true to display submission option
     */
    public static boolean isEmailQuoteToAddress(Quote quote) {
        return ((!isRenewalQuote(quote) && (isDirect(quote) || isNotSpec(quote)))
                || (isRenewalQuote(quote) && (isDirect(quote) || isNotSpec(quote))
                        && isOpenedStatus(quote) && !quote.getQuoteHeader().isFCTQuote())
                || isShowSendQuoteToCustText(quote)) && !hasAddiSiteCustomer(quote);
    }

    /**
     * Get value of 'E-mail to the following e-mail address' option
     * @param quote
     * @return true means selected
     */
    public static boolean getEmailQuoteToAddressFlagValue(Quote quote) {
        return quote.getQuoteHeader().getSendQuoteToAddtnlCntFlag() == 1 ;
    }

    /**
     * Whether display 'make available to customer on Passport Advantage Online' option
     * @param quote
     * @return true to display submission option
     */
    public static boolean isDisplayPAOBlockFlag(Quote quote) {
        return (!quote.getQuoteHeader().isFCTQuote() && !isChannel(quote) && !quote.getQuoteHeader().isPPSSQuote()
                && !isRenewalQuote(quote) && !quote.getQuoteHeader().isOEMQuote() && !quote.getQuoteHeader().hasLotusLiveItem()
				&& !quote.getQuoteHeader().hasSaaSLineItem() && !quote.getQuoteHeader().isHasMonthlySoftPart());
    }

    /**
     * Get value of 'make available to customer on Passport Advantage Online' option
     * @param quote
     * @return true means selected
     */
    public static boolean getDisplayPAOBlockFlagVaule(Quote quote) {
        return quote.getQuoteHeader().getPAOBlockFlag() == 1 ;
    }

    /**
     * Whether display 'E-mail to customer's renewal contact' option
     * @param quote
     * @return true to display submission option
     */
    public static boolean isEmailRQContact(Quote quote) {
        return (isRenewalQuote(quote) && (isDirect(quote) || isNotSpec(quote)) && isOpenedStatus(quote) && !quote.getQuoteHeader().isFCTQuote());
    }

    /**
     * Get value of 'E-mail to customer's renewal contact' option
     * @param quote
     * @return true means selected
     */
    public static boolean getEmailRQContactFlagValue(Quote quote) {
        return quote.getQuoteHeader().getSendQuoteToQuoteCntFlag() == 1 ;
    }

    /**
     * Whether display 'E-mail special bid partner notification' option
     * @param quote
     * @return true to display submission option
     */
    public static boolean isEmailPartnerAddress(Quote quote) {
        return (quote.getQuoteHeader().isPAQuote() || quote.getQuoteHeader().isPAEQuote() || quote.getQuoteHeader().isOEMQuote())
			&& isChannel(quote) && isSpBid(quote)
			&& !quote.getQuoteHeader().isResellerToBeDtrmndFlag()
			&& !quote.getQuoteHeader().isDistribtrToBeDtrmndFlag()
			&& !hasAddiSiteCustomer(quote);
    }

    public static boolean isSpBid(Quote quote){
        return quote.getQuoteHeader().getSpeclBidFlag() == 1 ;
    }

    /**
     * Get value of 'E-mail special bid partner notification' option
     * @param quote
     * @return true means selected
     */
    public static boolean getEmailPartnerY9ListAddressFlagValue(Quote quote){
        return quote.getQuoteHeader().isSendQuoteToAddtnlPrtnrFlag();
    }
    public static boolean getEmailPartnerInputAddressFlagValue(Quote quote){
        return StringUtils.isNotBlank(quote.getQuoteHeader().getAddtnlPrtnrEmailAdr());
    }

    public static boolean isDisplayQuoteSubmissionQuoteOutput(Quote quote) {
        return (!isDirectELAQuote(quote) && !isCredOrderRebillQuote(quote));
    }

    public static boolean isDirectELAQuote(Quote quote){
        return quote.getQuoteHeader().isELAQuote()
    	&& quote.getQuoteHeader().getFulfillmentSrc().equalsIgnoreCase(QuoteConstants.FULFILLMENT_DIRECT);
    }

    public static boolean isCredOrderRebillQuote(Quote quote){
        if (quote.getQuoteHeader().getSpeclBidFlag() == 1) {
            return quote.getSpecialBidInfo().isCredAndRebill();
        }else{
            return false;
        }

    }

    public static boolean isNoPartnerY9Email(Quote quote) {
        List emailList = new ArrayList();
        String distChannelCode = quote.getQuoteHeader().getSapDistribtnChnlCode();
        if (QuoteConstants.DIST_CHNL_HOUSE_ACCOUNT.equals(distChannelCode)){
        	if (quote.getReseller()==null){
        		return true;
        	} else {
        		 emailList = quote.getReseller().getY9EmailList();
        	}
            
        }else if (QuoteConstants.DIST_CHNL_DISTRIBUTOR.equals(distChannelCode)){
        	if (quote.getPayer()==null){
        		return true;
        	} else {
        		emailList = quote.getPayer().getY9EmailList();
        	}
            
        }
        if (emailList == null || emailList.size() == 0){
            return true;
        }
        return false;
    }

    public static String getPartnerY9EmailList(Quote quote) {
        String y9EmailList = "";
        List emailList = new ArrayList();
        String distChannelCode = quote.getQuoteHeader().getSapDistribtnChnlCode();
        if (QuoteConstants.DIST_CHNL_HOUSE_ACCOUNT.equals(distChannelCode)){
        	if (quote.getReseller()!=null){
        		 emailList = quote.getReseller().getY9EmailList();
        	}
            
        }else if (QuoteConstants.DIST_CHNL_DISTRIBUTOR.equals(distChannelCode)){
        	if (quote.getPayer()!=null){
        		emailList = quote.getPayer().getY9EmailList();
        	}
            
        }
        if (emailList != null && emailList.size() > 0){
            for (int i=0; i<emailList.size(); ++i){
                y9EmailList += (String)emailList.get(i) + ";";
            }
        }
        if (y9EmailList.length() > 0){
            y9EmailList = y9EmailList.substring(0, y9EmailList.length() -1);
        }
        return "(" + y9EmailList + ")";
    }

    /**
     * determin if quoteSubmission page should display "E-mail the approved
     * special bid business partner notification to the following address" input
     * box it should not be displayed for US/Canada and Channel quote
     *
     * @return true for display, false for not display
     */
    public static boolean isDisplayParterAddressInput(Quote quote) {
        return ButtonDisplayRuleFactory.singleton().isDisplayParterAddressInput(quote.getQuoteHeader());
    }

    public boolean isDisplayQuoteOutput(Quote quote) {
        boolean isDirectELAQuote = quote.getQuoteHeader().isELAQuote()
                && quote.getQuoteHeader().getFulfillmentSrc().equalsIgnoreCase(QuoteConstants.FULFILLMENT_DIRECT);
        boolean isCredOrderRebillQuote = false;
        if (quote.getQuoteHeader().getSpeclBidFlag() == 1) {
            isCredOrderRebillQuote = quote.getSpecialBidInfo().isCredAndRebill();
        }
        return (!isDirectELAQuote && !isCredOrderRebillQuote);
    }

    public static boolean isDisplayFctNonStdTermsConds(Quote quote){
    	boolean shouldDisplay = ButtonDisplayRuleFactory.singleton().isDisplayFCTNonStdTermsConds(quote.getQuoteHeader());
    	boolean termsCondsSet = quote.getQuoteHeader().getFctNonStdTermsConds() != null;

    	return (shouldDisplay && termsCondsSet);
    }

    public static boolean isFctNonStdTermsCondsYesChecked(Quote quote){
    	Integer fctNonStdTermsConds = quote.getQuoteHeader().getFctNonStdTermsConds();

    	return ((fctNonStdTermsConds != null)
    			 && (fctNonStdTermsConds.intValue() == QuoteConstants.FCTNonStdTermsConds.YES));
    }

    public static boolean isFctNonStdTermsCondsNoChecked(Quote quote){
    	Integer fctNonStdTermsConds = quote.getQuoteHeader().getFctNonStdTermsConds();

    	return ((fctNonStdTermsConds != null)
    			 && (fctNonStdTermsConds.intValue() == QuoteConstants.FCTNonStdTermsConds.NO));
    }

    public static boolean isFctNonStdTermsCondsNotApplicableChecked(Quote quote){
    	Integer fctNonStdTermsConds = quote.getQuoteHeader().getFctNonStdTermsConds();

    	return ((fctNonStdTermsConds != null)
    			 && (fctNonStdTermsConds.intValue() == QuoteConstants.FCTNonStdTermsConds.NOT_APPLICABLE));
    }

    /**
     * Checked the 'Include Firm Order Letter' option
     * @param quote
     * @return true to checked submission option
     */
	public static boolean isInclFirmOrdLtr(Quote quote) {
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		return  (quoteHeader.getFirmOrdLtrFlag()==1);
	}

	/**
     * Whether display 'Include Firm Order Letter' option
     * @param quote
     * @return true to display submission option
     */
	public static boolean isDisplayInclFirmOrdLtr(Quote quote) {
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		return (quoteHeader.isSalesQuote() && (quoteHeader.isPAQuote() || quoteHeader.isPAEQuote() ||
		quoteHeader.isFCTQuote() || quoteHeader.isFCTToPAQuote()));
	}

	public static boolean isShowPrcOrPointsOnQuoteOutputWithSaas(Quote quote) {
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		return (quote.getQuoteHeader().hasSaaSLineItem() && (quoteHeader.isPAQuote() || quoteHeader.isPAEQuote() ||
		quoteHeader.isFCTQuote() ));
	}

	public static boolean isInclPaymentSchedule(Quote quote) {
		String quoteOutputOption = quote.getQuoteHeader().getQuoteOutputOption();
		return  (quoteOutputOption != null && QuoteConstants.PAYMENT_SCHEDULE_CHECKBOX_VALUE.equals(quoteOutputOption));
	}
	
	/*Display the new "Include Payment Schedule Attachment with quote output" checkbox defaulted to unchecked state 
	 * in the "Quote output" section in the quote submission screen for PA/PAE/FCT sales quotes regardless of part content.  
	 * Display this new checkbox in the "Quote output" section of the quote submission screen all the time.*/
	//Remove the 'Include payment schedule' output option on the quote submission screen if the quote has any parts with event-based billing
	public static boolean isDisplayInclPaymentSchedule(Quote quote) {
        if (hasEventBasedBilling(quote)) {
	        return false;
	    }
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		return (quoteHeader.isPAQuote() || quoteHeader.isPAEQuote() ||
		quoteHeader.isFCTQuote() || quoteHeader.isFCTToPAQuote());
	}
	
	/**
     * DOC if the quote has any parts with event-based billing
     * @param quote
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean hasEventBasedBilling(Quote quote) {
        List lineItemList = quote.getLineItemList();
        for (Iterator iterator = lineItemList.iterator(); iterator.hasNext();) {
            QuoteLineItem qtLineItem = (QuoteLineItem) iterator.next();
            if(QuoteConstants.SaaSBillFreq.EVENT
                    .equalsIgnoreCase(qtLineItem.getBillgFrqncyCode())){
                return true;
            }
        }
        return false;
    }

    public static boolean isDisplayQuoteSubmitQuoteOutputType(Quote quote){
		boolean isDisplay = false;
		if (hasSaaSLineItem(quote) && ((isPA(quote)) || (isPAE(quote)) || (isFCT(quote))) ) {
			if(showPrcOrPointsOnQuoteOutputWithSaas(quote)){
				isDisplay = true;
			}
		}
		
		return isDisplay;
	}
	
	/**
	 * If there are SaaS parts on the quote and For each configuration on the
	 * quote, all subscription parts billing frequencies are the same and For
	 * each configuration on the quote, all subscription part ramp-up periods,
	 * when present, match in number of ramp-up periods and durations.return
	 * true
	 * 
	 * @param no
	 * 
	 * @return true/false
	 */
	public static boolean showPrcOrPointsOnQuoteOutputWithSaas(Quote quote) {


			List configrtnsList = quote.getPartsPricingConfigrtnsList();
			Map configrtnsMap = quote.getPartsPricingConfigrtnsMap();
						
			if (configrtnsList != null && configrtnsList.size() > 0) {
				for (int i = 0; i < configrtnsList.size(); i++) {
					PartsPricingConfiguration ppc = (PartsPricingConfiguration) configrtnsList
							.get(i);

					// configSaaSlineItemList:all QuoteLineItems in the config
					List configSaaSlineItemList = (List) configrtnsMap.get(ppc);

					if ((configSaaSlineItemList != null)
							&& (configSaaSlineItemList.size() > 0)) {
						
						//definition of some useful variables 
						String subscrptnBillgFrqncyCode = null;
						List rampUpLineItems = null;
						QuoteLineItem rampUpLineItem = null;
						QuoteLineItem currentRampUpLineItem = null;
						QuoteLineItem currentLineItem = null;
						
						for (int j = 0; j < configSaaSlineItemList.size(); j++) {
							currentLineItem =  (QuoteLineItem) configSaaSlineItemList.get(j);
							if(!currentLineItem.isSaasSubscrptnPart() || currentLineItem.isSaasSLApart()){
								continue;
							}
							
							//Matching billing frequency 
							if(subscrptnBillgFrqncyCode == null ){
								subscrptnBillgFrqncyCode = currentLineItem.getBillgFrqncyCode() == null? "":currentLineItem.getBillgFrqncyCode().trim();
							}else{
								if(!subscrptnBillgFrqncyCode.equals(currentLineItem.getBillgFrqncyCode())){
									return false;
								}
							}
							
							//Matching RampUp
							if(rampUpLineItems == null){
								if( currentLineItem.getRampUpLineItems() != null							
									&& currentLineItem.getRampUpLineItems().size() > 0){
									rampUpLineItems = currentLineItem.getRampUpLineItems();
								}
								
							}else if(currentLineItem.getRampUpLineItems() != null							
									&& currentLineItem.getRampUpLineItems().size() > 0){
								if(rampUpLineItems.size() != currentLineItem.getRampUpLineItems().size()){
									return false;
								}
								
								for (int l = 0; l < rampUpLineItems.size(); l++) {
									rampUpLineItem = (QuoteLineItem) rampUpLineItems.get(l);
									currentRampUpLineItem = (QuoteLineItem) currentLineItem.getRampUpLineItems().get(l);
									if(rampUpLineItem.getRampUpPeriodNum() != currentRampUpLineItem.getRampUpPeriodNum()){
										return false;
									}
								if (!(String.valueOf(rampUpLineItem.getICvrageTerm())).equals(String.valueOf(currentRampUpLineItem
										.getICvrageTerm()))) {
									return false;
								}
								}
							}
								
						}
					}

				}
			}

		return true;
	}
	
	/**
	 * justify whether is FCT Quote type or not
	 * @param no
	 * 
	 * @return true/false
	 */
	public static boolean isFCT(Quote quote) {
		return quote.getQuoteHeader().isFCTQuote();
	}
	
	/**
	 * justify whether is PAE Quote type or not
	 * 	 This method is only for FOL requirement, which requires excluding the FCT to PA quote
	 * @param no
	 * 
	 * @return true/false
	 */
	public static boolean isPAE(Quote quote) {
		return quote.getQuoteHeader().isPAEQuote()&&!quote.getQuoteHeader().isFCTToPAQuote();
	}
	
	/**
	 * justify whether is PA Quote type or not
	 * This method is only for FOL requirement, which requires excluding the FCT to PA quote
	 * @param no
	 * 
	 * @return true/false
	 */
	public static boolean isPA(Quote quote) {
		return quote.getQuoteHeader().isPAQuote()&&!quote.getQuoteHeader().isFCTToPAQuote();
	}
	
	/**
	 * justify whether  has SaaSLineItem or not
	 * @param no
	 * 
	 * @return true/false
	 */
	public static boolean hasSaaSLineItem(Quote quote) {
		return quote.getQuoteHeader().hasSaaSLineItem();
	}
	
    public static boolean getBudgetaryQuoteOutputFlagValue(Quote quote) {
	    return quote.getQuoteHeader().getBudgetaryQuoteFlag() == 1 ;
	}

}
