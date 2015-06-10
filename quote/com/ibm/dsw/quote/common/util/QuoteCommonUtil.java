package com.ibm.dsw.quote.common.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import DswSalesLibrary.ItemOut;

import com.ibm.dsw.quote.appcache.domain.BillingOptionFactory;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.domain.CurrencyConversionFactorFactory;
import com.ibm.dsw.quote.appcache.domain.QtApplPGSEnvrnmtFactory;
import com.ibm.dsw.quote.appcache.domain.QtApplSQOEnvrnmtFactory;
import com.ibm.dsw.quote.appcache.domain.QuoteApplEnvrnmtFactory;
import com.ibm.dsw.quote.appcache.domain.SapSalesDocTypeMappingFactory;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.BasicConfiguration;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.DeployModel;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartPriceAppliancePartConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemBillingOption;
import com.ibm.dsw.quote.common.domain.SpecialBidReasonConfigFactory;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcess;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcessFactory;
import com.ibm.dsw.quote.customerlist.domain.ActiveService;
import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract.LineItemParameter;
import com.ibm.dsw.quote.draftquote.util.PartPriceHelper;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPriceCommon;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants.OverallStatus;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>QuoteCommonUtil<code> class.
 *
 * @author: zhengmr@cn.ibm.com
 *
 * Creation date: Jul 2, 2008
 */

public class QuoteCommonUtil {
	
	protected static final LogContext logger = LogContextFactory.singleton().getLogContext();

    public static final String AUTH_PROG_GTS = "GTS";

    public static final String AUTH_PROG_SWVN = "SWVN";

    public static final String AUTH_PROG_CONV = "CONV";

    public static final String PART_TYPE_RENWL = "RENEWAL";

    public static final String PART_TYPE_NON_RENWL = "NON_RENEWAL";

    public static final String AUTH_FOR_ALL = "ALL";

    public static final String AUTH_FOR_SOME = "SOME";

    public static final String AUTH_FOR_NONE = "NONE";

    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    protected Quote quote = null;

    protected List swvnPartList = new ArrayList();				// SW VN part list
    protected List gtsRenwlPartList = new ArrayList();			// GTS renewal part list
    protected List gtsLicnsPartList = new ArrayList();			// GTS license part list
    protected List openPartList = new ArrayList();				// Open part list
    protected List convPartList = new ArrayList();

    protected List rselSWVNPartList = new ArrayList();			// Reseller authorized SW VN part list
    protected List rselGTSRenwlPartList = new ArrayList();		// Reseller authorized GTS renewal part list
    protected List rselGTSLicnsPartList = new ArrayList();		// Reseller authorized GTS license part list
    protected List rselConvPartList = new ArrayList();

    protected List distSWVNPartList = new ArrayList();			// Reseller (associated with distributor) authorized SW VN part list
    protected List distGTSRenwlPartList = new ArrayList();		// Reseller (associated with distributor) authorized GTS renewal part list
    protected List distGTSLicnsPartList = new ArrayList();		// Reseller (associated with distributor) authorized GTS license part list
    protected List distConvPartList = new ArrayList();
	private static QuoteCommonUtil sngltn = null;


	private static final String NUMBER_PATTERN = "^[0-9]+(.[0-9]{0,1})?$";

	public static QuoteCommonUtil singleton() {
		if (QuoteCommonUtil.sngltn == null) {
			sngltn = new QuoteCommonUtil();
		}
		return sngltn;
	}

	private QuoteCommonUtil() {
    }

	public QuoteCommonUtil(Quote quote) {
        this.quote = quote;
        this.init();
    }

    public static QuoteCommonUtil create(Quote quote) {
        return new QuoteCommonUtil(quote);
    }

    protected void init() {

        if ((quote == null) || (quote.getLineItemList() == null)) {
			return;
		}

        List items = quote.getLineItemList();
        for (int i = 0; i < items.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) items.get(i);

            if (QuoteConstants.IBM_PROG_CODE_SW_VN.equalsIgnoreCase(item.getIbmProgCode())) {
                swvnPartList.add(item);

                if (isRselAuth(item)) {
					rselSWVNPartList.add(item);
				}

                if (isRselAndDistAuth(item)) {
					distSWVNPartList.add(item);
				}
            }
            else if (QuoteConstants.IBM_PROG_CODE_GTS.equalsIgnoreCase(item.getIbmProgCode())) {
                if (item.isRenewalPart()) {
                    gtsRenwlPartList.add(item);

                    if (isRselAuth(item)) {
						rselGTSRenwlPartList.add(item);
					}

                    if (isRselAndDistAuth(item)) {
						distGTSRenwlPartList.add(item);
					}
                }
                else {
                    gtsLicnsPartList.add(item);

                    if (isRselAuth(item)) {
						rselGTSLicnsPartList.add(item);
					}

                    if (isRselAndDistAuth(item)) {
						distGTSLicnsPartList.add(item);
					}
                }
            }
            else if (QuoteConstants.IBM_PROG_CODE_CONV.equalsIgnoreCase(item.getIbmProgCode())) {
                convPartList.add(item);

                if (isRselAuth(item)) {
					rselConvPartList.add(item);
				}

                if (isRselAndDistAuth(item)) {
					distConvPartList.add(item);
				}
            }
            else {
                openPartList.add(item);
            }
        }
    }

    protected boolean isRselAuth(QuoteLineItem item) {
        Partner reseller = quote.getReseller();

        // default government indicate code to 1 if customer is not selected.
        int custGovEntIndCode = quote.getCustomer() == null ? 1 : quote.getCustomer().getGovEntityIndCode();

        if (reseller != null) {
			return reseller.isAuthorizedPortfolio(item.getControlledCode(), custGovEntIndCode);
		} else {
			return true;
		}
    }

    protected boolean isRselAndDistAuth(QuoteLineItem item) {
        Partner reseller = quote.getReseller();
        String payerCustNum = quote.getQuoteHeader().getPayerCustNum();
        //default government indicate code to 1 if customer is not selected.
        int custGovEntIndCode = quote.getCustomer() == null ? 1 : quote.getCustomer().getGovEntityIndCode();

        if (reseller != null) {
            if (StringUtils.isNotBlank(payerCustNum)) {
				return reseller.isAuthorizedPortfolio(item.getControlledCode(), payerCustNum, custGovEntIndCode);
			} else {
				return reseller.isAuthorizedPortfolio(item.getControlledCode(), custGovEntIndCode);
			}
        } else {
			return true;
		}
    }

    public boolean isResellerAuthorizedToAllWithDistrbtr() {

        if (!QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(this.quote.getQuoteHeader().getFulfillmentSrc())) {
            return true;
        }

		boolean auth = AUTH_FOR_ALL.equals(getAuthType(swvnPartList, distSWVNPartList)) && AUTH_FOR_ALL.equals(getAuthType(gtsRenwlPartList, distGTSRenwlPartList))
				&& AUTH_FOR_ALL.equals(getAuthType(gtsLicnsPartList, distGTSLicnsPartList)) && AUTH_FOR_ALL.equals(getAuthType(convPartList, distConvPartList));

        return auth;
    }

    private String getAuthType(List total, List auth) {
        if (total.size() == auth.size()) {
			return AUTH_FOR_ALL;
		} else if ((total.size() > auth.size()) && (auth.size() > 0)) {
			return AUTH_FOR_SOME;
		} else {
			return AUTH_FOR_NONE;
		}
    }

    /**
     * Check if the reseller is authorized for controlled parts regardless of distributor/payer
     * @param authProgram: authorization program
     * @param partType: controlled part type
     * @return NONE/SOME/ALL
     */
    public String isResellerAuthorizedToPartGroups(String authProgram, String partType) {

        if (AUTH_PROG_SWVN.equalsIgnoreCase(authProgram)) {
			return getAuthType(swvnPartList, rselSWVNPartList);
		} else if(AUTH_PROG_CONV.equalsIgnoreCase(authProgram)) {
			return getAuthType(convPartList, rselConvPartList);
		} else {
            if (PART_TYPE_RENWL.equalsIgnoreCase(partType)) {
				return getAuthType(gtsRenwlPartList, rselGTSRenwlPartList);
			} else {
				return getAuthType(gtsLicnsPartList, rselGTSLicnsPartList);
			}
        }
    }

    /**
     * Check if the reseller is authorized (associated with distributor/payer) for controlled parts
     * If distributor/payer is empty, the result is same as isResellerAuthorizedToPartGroups()
     * @param authProgram: authorization program
     * @param partType: controlled part type
     * @return NONE/SOME/ALL
     */
    public String isResellerAssociatedWithDistributor(String authProgram, String partType) {

        if (AUTH_PROG_SWVN.equalsIgnoreCase(authProgram)) {
			return getAuthType(rselSWVNPartList, distSWVNPartList);
		} else if(AUTH_PROG_CONV.equalsIgnoreCase(authProgram)) {
			return getAuthType(rselConvPartList, distConvPartList);
		} else {
            if (PART_TYPE_RENWL.equalsIgnoreCase(partType)) {
				return getAuthType(rselGTSRenwlPartList, distGTSRenwlPartList);
			} else {
				return getAuthType(rselGTSLicnsPartList, distGTSLicnsPartList);
			}
        }
    }

    // Controll parts include: SW VN parts, GTS renewal parts & GTS license parts
    public boolean hasControlledParts(String authProgram, String partType) {
        return ((swvnPartList.size() > 0) || (gtsLicnsPartList.size() > 0) || (gtsRenwlPartList.size() > 0) || (convPartList.size() > 0));
    }

    public boolean hasSWVNParts() {
        return (swvnPartList.size() > 0);
    }

    // PA parts include: Open parts, GTS renewal parts & GTS license parts
    public boolean hasPAParts() {
        return ((openPartList.size() > 0) || (gtsLicnsPartList.size() > 0) || (gtsRenwlPartList.size() > 0) || (convPartList.size() > 0));
    }

    public String calcSAPDistributionChannel() {
        String distChannel = QuoteConstants.DIST_CHNL_END_CUSTOMER;
        QuoteHeader header = quote.getQuoteHeader();

        if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(header.getFulfillmentSrc())) {
            String rselNum = header.getRselCustNum();
            String payerNum = header.getPayerCustNum();

            if ((rselNum != null) && rselNum.equalsIgnoreCase(payerNum)) {
				distChannel = QuoteConstants.DIST_CHNL_HOUSE_ACCOUNT;
			} else {
				distChannel = QuoteConstants.DIST_CHNL_DISTRIBUTOR;
			}
        }
        return distChannel;
    }

    public static boolean acceptPrice(ItemOut itemOut, QuoteLineItem qli){
    	if(qli.isObsoletePart() && (qli.getOverrideUnitPrc() != null)){
    		return true;
    	}

    	return (StringUtils.isBlank(itemOut.getErrCode()));
    }

    public static boolean isPricingError(ItemOut itemOut, QuoteLineItem qli){
    	if(StringUtils.isNotBlank(itemOut.getErrCode())){
    		if(qli.isObsoletePart()
        			&& PartPriceConstants.PWSErrorCodes.ERROR_CODE_028.equals(itemOut.getErrCode())){
        		return false;
    		}
        		return true;  //return pricing error when any non blank part line item error code is not 028 and the part is not obsolete
        }
    	return false;   //do not return pricing error when part line item is blank
    }

    public static void setLineItemPrice(ItemOut itemOut, QuoteLineItem lineItem, Quote quote) throws TopazException{
        lineItem.setLocalUnitPrc(PricingServiceUtil.getLocalUnitPrc(itemOut, lineItem, quote));
    	lineItem.setLocalUnitProratedPrc(PricingServiceUtil.getLocalUnitProratedPrc(itemOut, lineItem, quote));
    	lineItem.setLocalUnitProratedDiscPrc(PricingServiceUtil.getLocalUnitProratedDiscPrc(itemOut, lineItem, quote));
    	lineItem.setLocalExtPrc(PricingServiceUtil.getLocalExtPrc(itemOut, lineItem, quote));
    	lineItem.setLocalExtProratedPrc(PricingServiceUtil.getLocalExtProratedPrc(itemOut, lineItem, quote));
    	lineItem.setLocalExtProratedDiscPrc(PricingServiceUtil.getLocalExtProratedDiscPrc(itemOut, lineItem, quote));
    	lineItem.setChannelUnitPrice(PricingServiceUtil.getChannelUnitPrice(itemOut, lineItem, quote));
    	lineItem.setChannelExtndPrice(PricingServiceUtil.getChannelExtndPrice(itemOut, lineItem, quote));
    	lineItem.setLocalTaxAmt(PricingServiceUtil.getLocalTaxAmt(itemOut, lineItem, quote));
    	lineItem.setLclExtndPriceIncldTax(PricingServiceUtil.getLclExtndPriceIncldTax(itemOut, lineItem, quote));
    	lineItem.setLocalChnlTaxAmt(PricingServiceUtil.getLocalChnlTaxAmt(itemOut, lineItem, quote));
    	lineItem.setLclExtndChnlPriceIncldTax(PricingServiceUtil.getLclExtndChnlPriceIncldTax(itemOut, lineItem, quote));
    	lineItem.setSaasBidTCV(PricingServiceUtil.getSaasBidTCV(itemOut, lineItem, quote));
    	lineItem.setLegacyBasePriceUsedFlag(PricingServiceUtil.getLegacyBasePriceUsedFlag(itemOut, lineItem, quote));
		if (quote.getQuoteHeader().isRenewalQuote()|| (quote.getQuoteHeader().isSalesQuote() && null != quote.getQuoteHeader().getRenwlQuoteNum())) {
			String pm = PricingServiceUtil.getRenewalPricingMethod(itemOut, lineItem, quote);
			if (StringUtils.isNotBlank(pm)) {
				lineItem.setRenewalPricingMethod(pm);
			}
			lineItem.setRenewalRsvpPrice(PricingServiceUtil.getRenewalRsvpPrice(itemOut, lineItem));
		}
    	lineItem.setChnlStdDiscPct(PricingServiceUtil.getChnlStdDiscPct(itemOut, lineItem, quote));
    	lineItem.setChnlOvrrdDiscPct(PricingServiceUtil.getChnlOvrrdDiscPct(itemOut, lineItem, quote));
    	lineItem.setSaasBpTCV(PricingServiceUtil.getSaasBpTCV(itemOut, lineItem, quote));
    }

    public static boolean shouldSetELAAutoChnlDisc(Quote quote){
        QuoteHeader header = quote.getQuoteHeader();
        if (header.isSalesQuote() && header.isChannelQuote()
                && (header.isPAEQuote() || header.isPAQuote())
                && PartPriceConfigFactory.singleton().
                isSpclAreaEnabledForELAAutoChnlDisc(header.getCountry().getSpecialBidAreaCode())) {

            if (header.isELAQuote()) {
                return true;
            }

            if(header.getSpeclBidFlag() == 1){
	            Customer customer = quote.getCustomer();
	            if ((customer != null) && (customer.getContractList() != null) && (customer.getContractList().size() > 0)) {
	                Contract ct = (Contract) customer.getContractList().get(0);
	                if (QuoteConstants.CONTRACT_VARIANT.ELA.equalsIgnoreCase(ct.getSapContractVariantCode())) {
	                    return true;
	                }
	            }
            }
        }

        return false;
    }

    public static boolean isCustWithOverrideSVP(Quote quote) {
		Customer customer = quote.getCustomer();
		if (customer == null) {
			return false;
		}

		return (!customer.isAddiSiteCustomer() && !customer.isSTDCustomer() &&!customer.isXSPCustomer() && StringUtils
				.isNotBlank(quote.getCustomer().getTransSVPLevel()));
	}

    public static boolean hasExistingPACustomerAndContract(Quote quote) {

        String lob = quote.getQuoteHeader().getLob().getCode();
        // not a PA part
        if (!QuoteConstants.LOB_PA.equals(lob)) {
            return false;
        }

        Customer customer = quote.getCustomer();

        // customer dosent's exist
        if (null == customer) {
            return false;
        }

        // no contract exist
        List contractList = customer.getContractList();
        if ((null == contractList) || (0 == contractList.size())) {
            return false;
        }
        // Customer is new
        if (StringUtils.isBlank(quote.getQuoteHeader().getSoldToCustNum())) {
            return false;
        }
        return true;

    }

    public static boolean isEligibleForCmprssCvrage(QuoteLineItem qli) {
        if ((qli.getPartQty() != null) && (qli.getPartQty().intValue() == 0)) {
            return false;
        }


        //Applies to renewal part only
        if (!PartPriceConfigFactory.singleton().isMaintenancePart(qli.getRevnStrmCode())) {
            return false;
        }

        //Applies to non-future dated parts
        if (!PartPriceConfigFactory.singleton().needDetermineDate(qli.getRevnStrmCode())) {
            return false;
        }

        //For parts referencing a renewal quote, cc applies when the line item does not have future start date.
        if (StringUtils.isNotBlank(qli.getRenewalQuoteNum())) {
            if ((qli.getOrigStDate() != null)
                    && !DateUtil.isDateAfterToday(qli.getOrigStDate())) {
                if(qli.getOrigEndDate() == null){
                    return false;
                }

                Date firstDayOfCurrentMonth = DateUtil.getFirstDayDateOfCurrentMonth();
                Date ymdFormatEndDate = DateUtil.parseDate(DateUtil.formatDate(qli.getOrigEndDate()));
                //If original start date for renewal line item is equal to first day of current month then cc does not apply
                if(DateUtil.isYMDEqual(firstDayOfCurrentMonth,qli.getOrigStDate())){
                    return false;
                }
                //End date must be after first day of current month
                if(ymdFormatEndDate.after(firstDayOfCurrentMonth)){

                    if(qli.getMaintEndDate() != null){
                        //If rep extends maintenance period, compressed coverage should not apply
                        if(qli.getMaintEndDate().after(ymdFormatEndDate)){
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        // This should not happen, just avoid null pointer exception
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            //The line item start dates should not be manually over-ridden  (back-dated or future dated)
            if (qli.getStartDtOvrrdFlg()) {
                return false;
            }

            Date maintStartDate = qli.getMaintStartDate();
            Date maintEndDate = qli.getMaintEndDate();


            if((maintStartDate == null) || (maintEndDate == null)){
                return false;
            }

            //The line item coverage should not be for 12 months
            if(DateUtil.calculateFullCalendarMonths(qli.getMaintStartDate(), qli.getMaintEndDate()) == 12){
                return false;
            }

           //End date must be after first day of current month
            Date firstDayOfCurrentMonth = DateUtil.getFirstDayDateOfCurrentMonth();
            Date ymdFormatEndDate = DateUtil.parseDate(DateUtil.formatDate(maintEndDate));
            if(!ymdFormatEndDate.after(firstDayOfCurrentMonth)){
                return false;
            }

            //If CC is applied to line item already, start day will be set to first day of current month
            //below validation doesn't make any sense, only check if the item has cmprss cvrage month set
            if(qli.hasValidCmprssCvrageMonth() ){
                return true;
            }

            //The renewal start date is within 1 month of today's date (to match the first of the month of the next month)
            if (DateUtil.isDateBeforeToday(maintStartDate)
                     || !DateUtil.isDateWithinOneMonthFromToday(maintStartDate)) {
                return false;
            }

            return true;
        }
    }



    /**
     * check if quote line item should be included in offer price apply
     * @param quote
     * @param qli
     * @return
     */
    public static boolean shouldIncludeInOfferPrice(Quote quote, QuoteLineItem qli){

    	//exclude SAAS parts with unit prices only or it's a replace part
    	if((qli.isSaasPart() || qli.isMonthlySoftwarePart()) &&
    			(!PartPriceSaaSPartConfigFactory.singleton().showBidExtndPrice(qli)
    			 || qli.isReplacedPart())){
    		return false;
    	}


    	//Parts to be included in offer price should have prices
    	if((qli.isSaasPart() || qli.isMonthlySoftwarePart())){
    		if((qli.getLocalExtProratedDiscPrc() == null) || (qli.getLocalExtProratedDiscPrc().doubleValue() == 0)){
    			return false;
    		}
    	} else {
            if((qli.getLocalUnitProratedPrc() == null) || (qli.getLocalUnitProratedPrc().doubleValue()==0)){
                return false;
            }
    	}

        //exclude EOL parts
        if(qli.isObsoletePart()){
        	return false;
        }

        //Exclude compressed coverage parts
    	if(qli.hasValidCmprssCvrageMonth() ){
    	    return false;
    	}

        if((qli.isSaasPart() || qli.isMonthlySoftwarePart())){
        	return ((qli.isOfferIncldFlag() == null) || qli.isOfferIncldFlag().booleanValue());
        } else {
        	// all part in FCT should be regard as contract part
        	if (quote.getQuoteHeader().isFCTQuote()){
            	return ((qli.isOfferIncldFlag() == null) || qli.isOfferIncldFlag().booleanValue());
        	}else{
        		return PartPriceConstants.PartTypeCode.PACTRCT.equals(qli.getPartTypeCode()) && (
        				(qli.isOfferIncldFlag() == null) || qli.isOfferIncldFlag().booleanValue());
        	}
        }
    }

    public static boolean shouldIncludeForTotalPrice(QuoteHeader header, QuoteLineItem qli){
    	if(qli.isSaasPart() || qli.isMonthlySoftwarePart()){
    		//Only SaaS parts with bid ext prices and is not replaced part
    		return (PartPriceSaaSPartConfigFactory.singleton().showBidExtndPrice(qli)
    		       && !qli.isReplacedPart() );
    	} else {
            if(header.isFCTQuote()){
                return true;
            }

            //Only contract and service parts
            if(PartPriceConstants.PartTypeCode.PACTRCT.equals(qli.getPartTypeCode())
                     || PartPriceConstants.PartTypeCode.SERVICES.equals(qli.getPartTypeCode())){
                return true;
            }
    	}

        return false;
    }

    public static String[] getRegionAndReportingCurrencyCodes(QuoteHeader header){

        String geo = header.getPriceCountry().getSpecialBidAreaCode();
        String regionCurrency = null;

        if (QuoteConstants.GEO_EMEA.equals(geo)){
            regionCurrency =  QuoteConstants.CURRENCY_EUR;
        }
        else{
            regionCurrency = QuoteConstants.CURRENCY_USD;
        }

        String reportingCurrency = QuoteConstants.CURRENCY_USD;
        if(regionCurrency.equals(reportingCurrency)){
            return new String[]{regionCurrency, regionCurrency};
        }
        else{
            return new String[]{regionCurrency,reportingCurrency};
        }
    }

    /**
     * @param map
     * @param reverse, if false sort by asc, if true sort by desc
     * @return
     * Map
     */
    public static Map sortByValue(Map map, final boolean reverse) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {

            @Override
			public int compare(Object o1, Object o2) {
                if (reverse) {
                    return -((Comparable) ((Map.Entry) (o1)).getValue())
                            .compareTo(((Map.Entry) (o2)).getValue());
                }
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


    public static String mapSapSalesDocTypeByLob(Quote quote)throws TopazException {
    	if((quote == null) || (quote.getQuoteHeader() == null) || (quote.getQuoteHeader().getLob() == null)
    			|| (quote.getQuoteHeader().getLob().getCode() == null)){
    		return null;
    	}
		return SapSalesDocTypeMappingFactory.singleton().mapSapSalesDocTypeByLob(quote.getQuoteHeader().getLob().getCode());
    }

    public static int validateSubscrptnPartBillingOption(Quote quote) throws QuoteException{
    	List lineItems = quote.getLineItemList();

    	if((lineItems == null) || (lineItems.size() == 0)){
    		return QuoteConstants.SaasBillgOptErrorCode.VALID;
    	}

    	for(Iterator it = lineItems.iterator(); it.hasNext(); ){
    		QuoteLineItem qli = (QuoteLineItem)it.next();

    		if(qli.isSaasSubscrptnPart() || (qli.isMonthlySoftwarePart() && ((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart())){
    			if(StringUtils.isBlank(qli.getBillgFrqncyCode()) || (qli.getICvrageTerm() == null)){
    				return QuoteConstants.SaasBillgOptErrorCode.BILLG_OPT_OR_CTRCT_TERM_NOT_SELECTED;
    			}

    			if(needCheckTermDivisible(quote, qli) && !QuoteConstants.SaaSBillFreq.UPFRONT.equals(qli.getBillgFrqncyCode())){
    				int billingOptionMonths = getBillingOptioMonths(qli.getBillgFrqncyCode());

    				int termInMonths = getSubscrptnPartCvrageTermInMonths(qli);

    				if((termInMonths % billingOptionMonths) != 0){
    					return QuoteConstants.SaasBillgOptErrorCode.SUBSCRPTN_NOT_DIVISIBLE;
    				}
    			}
    		}
    	}

    	return QuoteConstants.SaasBillgOptErrorCode.VALID;
    }

    private static boolean needCheckTermDivisible(Quote quote, QuoteLineItem qli) {
		// We should not be checking to see if the term is divisible by the
		// billing period on add-ons/trade-ups/co-term. SAP will pro-rate on
		// these orders.

		String configrtnId = StringUtils.trimToEmpty(qli.getConfigrtnId());
		List configrtnList = qli.isSaasSubscrptnPart()? quote.getPartsPricingConfigrtnsList() : quote.getMonthlySwQuoteDomain().getMonthlySwConfgrtns();
		if ((configrtnList == null) || configrtnList.isEmpty()
				|| "".equals(configrtnId)) {
			return false;
		}
		ListIterator li = configrtnList.listIterator();
		while (qli.isSaasSubscrptnPart() && li.hasNext()) {
			PartsPricingConfiguration cli = (PartsPricingConfiguration) li
					.next();
			if (configrtnId.equals(StringUtils
					.trimToEmpty(cli.getConfigrtnId()))
					&& (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(cli.getConfigrtnActionCode())
							|| PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT.equals(cli.getConfigrtnActionCode()))
							|| PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(cli.getConfigrtnActionCode())) {
				return false;
			}

		}
		while (!qli.isSaasSubscrptnPart() && li.hasNext()) {
			BasicConfiguration cli = (BasicConfiguration) li
					.next();
			if (configrtnId.equals(StringUtils
					.trimToEmpty(cli.getConfigrtnId()))
					&& (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(cli.getConfigrtnActionCode())
							|| PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT.equals(cli.getConfigrtnActionCode()))
							|| PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(cli.getConfigrtnActionCode())) {
				return false;
			}

		}
		return true;
	}

    private static AssociatedSaasParts getSaasAssociatedSetUpAndSubscrptnParts(List lineItems){
    	AssociatedSaasParts setUpSubscrptnParts = new AssociatedSaasParts();

    	for(Iterator it = lineItems.iterator(); it.hasNext(); ){
    		QuoteLineItem qli = (QuoteLineItem)it.next();
    		if(qli.isSaasSubscrptnPart() || qli.isSaasSetUpPart() || qli.isSaasSubsumedSubscrptnPart()){
    			setUpSubscrptnParts.addSaasPart(qli);
    		}
    	}

    	return setUpSubscrptnParts;
    }

    static class AssociatedSaasParts {
    	private Map<String, List<QuoteLineItem>> subscrptnPartMap = new HashMap<String, List<QuoteLineItem>>();
    	private List<QuoteLineItem> setUpPartList= new ArrayList<QuoteLineItem>();

    	static final String SUB_ID = "SUB_ID";
    	static final String WWIDE_PROD_CODE = "WWIDE_PROD_CODE";
    	static final String MIGRTN_CODE = "MIGRTN_CODE";

    	void addSaasPart(QuoteLineItem qli){
    		if(qli.isSaasSetUpPart()){
    			add(qli);
    		} else if(qli.isSaasSubscrptnPart()){
        		add(SUB_ID, qli.getSwSubId(), qli);
        		add(WWIDE_PROD_CODE, qli.getWwideProdCode(), qli);
        		add(MIGRTN_CODE, qli.getMigrtnCode(), qli);
    		}else if(qli.isSaasSubsumedSubscrptnPart()){
    		    add(SUB_ID, qli.getSwSubId(), qli);
    		}
    	}

    	private void add(QuoteLineItem qli){
    		setUpPartList.add(qli);
    	}


    	private void add(String categoryKey, String qlikey, QuoteLineItem qli){
    		if(qlikey != null){
    			String key = categoryKey + qlikey;
    			List<QuoteLineItem> list = subscrptnPartMap.get(key);
    			if(list == null){
    				list = new ArrayList<QuoteLineItem>();
    			}

    			list.add(qli);

    			subscrptnPartMap.put(categoryKey + qlikey, list);
    		}
    	}

    	List<QuoteLineItem> getRelatedSubscrptionParts(QuoteLineItem qli){
    		List<QuoteLineItem> list = getRelatedSubscrptionParts(SUB_ID, qli.getSwSubId());
    		if((list != null) && (list.size() > 0)){
    			return list;
    		}

    		list = getRelatedSubscrptionParts(WWIDE_PROD_CODE, qli.getWwideProdCode());
    		if((list != null) && (list.size() > 0)){
    			return list;
    		}

    		list = getRelatedSubscrptionParts(MIGRTN_CODE, qli.getMigrtnCode());
    		if((list != null) && (list.size() > 0)){
    			return list;
    		}

    		return null;
    	}

    	private List<QuoteLineItem> getRelatedSubscrptionParts(String catKey, String qliKey){
    		if(qliKey == null){
    			return null;
    		}

    		return subscrptnPartMap.get(catKey + qliKey);
    	}
    }

    //Make sure the part is a saas subscription part
	public static int getSubscrptnPartCvrageTermInMonths(QuoteLineItem qli) {
		// Using total term, not the term of each individual subscription line item making up a ramped up subscription
		return getSubscrptnPartCvrageTermInMonths(qli.getPricngIndCode(),
				(qli.isBeRampuped() || qli.isRampupPart()) ?
						(qli.getCumCvrageTerm()==null?qli.getICvrageTerm().intValue():qli.getCumCvrageTerm().intValue()) : qli.getICvrageTerm().intValue());
	}

    //Make sure the part is a saas subscription part
    public static int getSubscrptnPartCvrageTermInMonths(String termUnit, int qty){
    	if(PartPriceConstants.SAAS_TERM_UNIT_ANNUAL.equals(termUnit)){
    		return qty * PartPriceConstants.ONE_YEAR_WITH_MONTH;
    	} else {
    		return qty;
    	}
    }

    public static int getBillingOptioMonths(String billingOptoinCode) throws QuoteException{
    	try {
    		int billingOptionMonths = BillingOptionFactory.singleton().getBillingOptionMonths(billingOptoinCode);

    		return billingOptionMonths;
    	} catch (TopazException e) {
    		logContext.error("QuoteCommonUtil", "error getting billing option months in QuoteCommonUtil.validateSaasPartBillgOption: " + billingOptoinCode + "; exception is " + e);

    		throw new QuoteException(e);
    	}
    }

    public static boolean isPartHasPrice(QuoteLineItem qli){
    	if(qli.isSaasPart() || qli.isMonthlySoftwarePart()){
    		if(PartPriceSaaSPartConfigFactory.singleton().showBidUnitPrice(qli)){
    			return (qli.getLocalUnitProratedDiscPrc() != null);
    		}

    		if(PartPriceSaaSPartConfigFactory.singleton().showBidExtndPrice(qli)){
    			return (qli.getLocalExtProratedDiscPrc() != null);
    		}

    		return true;
    	} else {
    		return (qli.getLocalUnitProratedDiscPrc() != null);
    	}
    }

    public static boolean needValidateSaaSMultiple(QuoteLineItem qli){
    	return (PartPriceSaaSPartConfigFactory.singleton().needValidateMltpl(qli)
    			  && (qli.getTierQtyMeasre() != null));
    }

    public static boolean needValidateSaaSMultiple(ConfiguratorPart part){
    	return (PartPriceSaaSPartConfigFactory.singleton().needValidateMltpl(part)
    			  && (part.getTierQtyMeasre() != null)
    			  && !part.isDeleted());
    }

    /**
     * @param discount
     * @param standardPrice
     * @return
     * Double, return the price that calculated by discount
     */
    public static Double calculatePriceByDiscount(double discount, Double standardPrice){
    	if((standardPrice == null) || (standardPrice.doubleValue() == 0.0)){
    		return null;
    	}
    	return new Double( (1 - discount/100) * standardPrice.doubleValue());
    }

    /**
     * @param item
     * @param originalPrice
     * @return
     * Double, return the formated as it's max decimal price
     */
    public static Double getFormatDecimalPrice(Quote quote, QuoteLineItem item, Double originalPrice){
    	if((item == null) || (originalPrice == null)){
    		return null;
    	}
    	if(!item.isSaasPart() && !item.isMonthlySoftwarePart()){
    		return originalPrice;
    	}
    	if(PartPriceSaaSPartConfigFactory.singleton().shldClcltOvrrdUnitPricPerDisc(item)){
    		return DecimalUtil.roundAsDouble(originalPrice.doubleValue(), QuoteCommonUtil.getPartPriceRoundingFactor(quote.getQuoteHeader(), item));
    	}
    	return originalPrice;
    }

    /**
     * @param lineItemParameter
     * @param contract
     * @return
     * boolean
     * check if to skip the line item UI validation
     */
    public static boolean isSkipLineItemUIValidate(LineItemParameter lineItemParameter, PostPartPriceTabContract contract){
    	if(lineItemParameter == null){
    		return true;
    	}
    	//user want to remove this line item , no need to do the validation
		if(PartPriceConstants.PART_REMOVED_QTY.equals(StringUtils.trim(lineItemParameter.quantity))){
			return true;
		}
		//if user click the "remove" button, no need to do the validation
    	if(contract.isDeleteLineItem()){
			if(lineItemParameter.key.equals(contract.getDeletePartNum()+"_"+contract.getDeletePartSeq())){
				logContext.debug(singleton(), "Skip Line Item UI Validate: " + lineItemParameter.key);
			}
				return true;
		}
    	return false;
    }

    public static boolean hasSaaSPartsApplicableForOfferPrice(Quote quote){
    	List list = quote.getLineItemList();

    	if((list == null) || (list.size() == 0)){
    		return false;
    	}

    	for(Iterator it = list.iterator(); it.hasNext(); ){
    		QuoteLineItem qli = (QuoteLineItem)it.next();

    		if(qli.isSaasPart()
    			&& PartPriceSaaSPartConfigFactory.singleton().showBidExtndPrice(qli)
    			&& !qli.isReplacedPart()){
    			return true;
    		}
    	}

    	return false;
    }
    public static java.util.Date calcQuoteStartDate(Quote quote){
    	return calcQuoteDate(quote, 1);//calculate quote start date
    }

    public static java.util.Date calcQuoteExpirationDate(Quote quote){
    	return calcQuoteDate(quote, 2);//calculate quote expiration date
    }
    private static java.util.Date calcQuoteDate(Quote quote, int type){
        /*
          type:
      	1.calculate quote start date
      	2.calculate quote expiration date
      	*/
      	java.util.Date calcdQuoteDate = null;
      	if((quote == null) || (quote.getQuoteHeader() == null)) {
			return calcdQuoteDate;
		}
      	QuoteHeader header = quote.getQuoteHeader();
      	int maxEstdNumProvisngDays = header.getMaxEstdNumProvisngDays();
      	java.util.Date estdOrdDate = header.getEstmtdOrdDate();
      	if((maxEstdNumProvisngDays < 0) || (estdOrdDate == null)) {
			return calcdQuoteDate;
		}

      	Calendar estdPartStartCal = Calendar.getInstance();//Estimated parts Start Date
      	Calendar calcdCal = Calendar.getInstance();//Calculated start or expiration date
      	Calendar latestPrvsngCal = Calendar.getInstance();//Calculate the latest valid provisioning date:  first of the month on or after the line item start date.
      	Calendar earliestPrvsngCal = Calendar.getInstance();//Calculate the earliest valid provisioning date:  second of the month on or before the line item start date.

      	estdPartStartCal.setTime(estdOrdDate);//Initiate estimated parts Start Date
      	estdPartStartCal.add(Calendar.DATE, maxEstdNumProvisngDays);//Estimated parts Start Date = estimated Order Date + max number of provisioning days

      	latestPrvsngCal.setTime(estdPartStartCal.getTime());//Initiate latest valid provisioning date
      	earliestPrvsngCal.setTime(estdPartStartCal.getTime());//Initiate earliest valid provisioning date

      	/*If the day of estimated parts Start Date is just the first day of the month
      	treat estimated parts Start Date as the latest valid provisioning date
      	set earliest valid provisioning date to be the 2nd of the prior month before the estimated parts Start Date
      	Otherwise the latest valid provisioning date is the first day of the next month of estimated parts Start Date
      	earliest valid provisioning date is the 2nd day of the same month of estimated parts Start Date*/

      	if (estdPartStartCal.getMinimum(Calendar.DATE) == estdPartStartCal.get(Calendar.DATE)) {
      		earliestPrvsngCal.add(Calendar.MONTH, -1);
      		earliestPrvsngCal.set(Calendar.DATE, 2);//the earliest valid provisioning date is the 2nd of the prior month before the estimated parts Start Date
      	}else {
      		latestPrvsngCal.add(Calendar.MONTH, 1);
      		latestPrvsngCal.set(Calendar.DATE, 1);//the latest valid provisioning date is the first day of the next month of estimated parts Start Date
      		earliestPrvsngCal.set(Calendar.DATE, 2);//the earliest valid provisioning date is the second day of the same month of estimated parts Start Date
      	}
      	//Quote Expiration Date = Latest provisioning date - provisioning days
      	//Quote Start Date = Earliest provisioning date - provisioning days

      	if( 1 == type) {//Calculate quote start date
      		calcdCal.setTime(earliestPrvsngCal.getTime());//Initiate quote start date
      	}else if ( 2 == type ) {//Calculate quote expiration date
      		calcdCal.setTime(latestPrvsngCal.getTime());//Initiate quote expiration date
      	}
      	calcdCal.add(Calendar.DATE, -maxEstdNumProvisngDays);
      	calcdQuoteDate = calcdCal.getTime();

      	return calcdQuoteDate;
      }

    public static String getNewSQOConfigurationId(String pId){
    	String dateStr = DateUtil.getCurrentDateString();

    	return pId + dateStr;
    }


	/**
	 * @param masterSaaSLineItems
	 * @param confgrtnList
	 * @return
	 * group by PID, PID description, configuration id
	 */
	public static Map getPartsPricingConfigurations(List masterSaaSLineItems, List confgrtnList){
		Map confgrnsMap = new HashMap();
		if((masterSaaSLineItems == null) || (masterSaaSLineItems.size() == 0)){
			return confgrnsMap;
		}
		if((confgrtnList == null) || (confgrtnList.size() == 0)){
			return confgrnsMap;
		}

		//put the <key,value>(<PartsPricingConfiguration,SaaSLineItems>) to the confgrnsMap
		Iterator cnfgrtnIt = confgrtnList.iterator();
		while (cnfgrtnIt.hasNext()) {
			PartsPricingConfiguration ppc = (PartsPricingConfiguration) cnfgrtnIt.next();
			List qliList = new ArrayList();
			for (int j = 0; j < masterSaaSLineItems.size(); j++) {
				QuoteLineItem qli = (QuoteLineItem)masterSaaSLineItems.get(j);
				if((ppc.getConfigrtnId() != null) && ppc.getConfigrtnId().equals(qli.getConfigrtnId())){
					qliList.add(qli);
				}
			}
			confgrnsMap.put(ppc, qliList);
		}
		return confgrnsMap;
	}

	/**
	 * @param SaaSLineItems
	 * calculate the SaaS part entitled total commit value
	 * @throws TopazException
	 */
	public static void calculateEntitledTCV(Quote quote) throws TopazException{
		logContext.debug(singleton(), "quote number :" + quote.getQuoteHeader().getWebQuoteNum());
		List SaaSLineItems = quote.getSaaSLineItems();
		if((SaaSLineItems == null) || (SaaSLineItems.size() == 0)){
			return;
		}
		for (Iterator iterator = SaaSLineItems.iterator(); iterator.hasNext();) {
			QuoteLineItem qli = (QuoteLineItem) iterator.next();
			//Setup and Human Services Parts:  The extended price (rate) equals the TCV.
			if(qli.isSaasSetUpPart() || qli.isSaasProdHumanServicesPart() || qli.isSaasSubsumedSubscrptnPart()){
				qli.setSaasEntitledTCV(qli.getLocalExtProratedPrc());
			}
			if(qli.isSaasSubscrptnPart()){
				qli.setSaasEntitledTCV(qli.getLocalExtProratedPrc() == null ? null : new Double(qli.getLocalExtProratedPrc().doubleValue() * qli.getBillingPeriods()));
			}
		}
	}

	/**
	 * @param SaaSLineItems
	 * calculate the SaaS part entitled total commit value
	 * for submitted quote line item
	 * @throws TopazException
	 */
	public static Double calculateEntitledTcvForSubmitQuote(QuoteLineItem qli){
		Double entitledTcv = null;
		if(qli.isSaasPart()){
			if(qli.isSaasSetUpPart() || qli.isSaasProdHumanServicesPart() || qli.isSaasSubsumedSubscrptnPart()){
				entitledTcv = qli.getLocalExtProratedPrc();
				}
			if(qli.isSaasSubscrptnPart()){
				entitledTcv = qli.getLocalExtProratedPrc() == null ? null : new Double(qli.getLocalExtProratedPrc().doubleValue() * qli.getBillingPeriods());
			}
		}else if(qli.isMonthlySoftwarePart()){
			if(((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart()){
				entitledTcv = qli.getLocalExtProratedPrc() == null ? null : new Double(qli.getLocalExtProratedPrc().doubleValue() * qli.getBillingPeriods());
			}
		}
		return entitledTcv;
	}




	/**
	 * @param saasList
	 * set master saas part ramp-up line items list
	 */
	public static void buildSaaSLineItemsWithRampUp(List saasList){
		if ((saasList == null) || (saasList.size() == 0)) {
            return;
        }

        // first, put all items in a map with seq num as its key;
        Map itemsMap = new HashMap();
        for (Iterator itemIt = saasList.iterator(); itemIt.hasNext();) {
            QuoteLineItem item = (QuoteLineItem) itemIt.next();
            if(!item.isRampupPart()){
            	continue;
            }
            Integer key = new Integer(item.getDestSeqNum());
            itemsMap.put(key, item);
        }

        for (Iterator itemIt = saasList.iterator(); itemIt.hasNext();) {
            QuoteLineItem currItem = (QuoteLineItem) itemIt.next();
            List rampUPList = new ArrayList();
            //if subscription part, loop the related ramp-up parts, then add them to the ramp-up list
            if(currItem.isSaasSubscrptnPart() && !currItem.isRampupPart()){
	            // master linked with all ramp-up with its RELATED_LINE_ITM_NUM
	            int nextRampUpSeqNum = currItem.getIRelatedLineItmNum();
	            while (nextRampUpSeqNum > 0) {
	                QuoteLineItem nextItem = (QuoteLineItem) itemsMap.get(new Integer(nextRampUpSeqNum));
	                if ((nextItem == null) || !nextItem.isRampupPart()){
	                    break;
	                }
	                rampUPList.add(nextItem);
	                nextRampUpSeqNum = nextItem.getIRelatedLineItmNum();
	            }
	            currItem.setRampUpLineItems(rampUPList);
	            //sort the ram-up parts by it's seqNum
	            Collections.sort(currItem.getRampUpLineItems(), new  Comparator(){
	                @Override
					public int compare(Object o1, Object o2) {
	                    QuoteLineItem item1 = (QuoteLineItem) o1;
	                    QuoteLineItem item2 = (QuoteLineItem) o2;
	                    return item1.getSeqNum() - item2.getSeqNum();
	                }
	            });
	            //set the ramp-up period num by the sort order
	            for (int i = 0; i < currItem.getRampUpLineItems().size(); i++) {
	            	QuoteLineItem rampUpQli = (QuoteLineItem) currItem.getRampUpLineItems().get(i);
	            	rampUpQli.setRampUpPeriodNum(i+1);
	            }
	            //the master over rage part is related to the master subscription part
	            QuoteLineItem masterOvrageQli = getSaasSubscrptnOvragePartByDestNum(saasList, currItem.getDestSeqNum());
	            //set the sub over rage parts to the master over rage part
	            if(masterOvrageQli != null){
	            	masterOvrageQli.setMasterOvrage(true);
		            masterOvrageQli.setRampUpLineItems(getSubOvrageParts(saasList, currItem.getRampUpLineItems()));
				}
            }
        }
	}


	/**
	 * @param configrtnId
	 * @param quote
	 * @return get the PartsPricingConfiguration object by configrtnId
	 */
	public static PartsPricingConfiguration getPartsPricingConfigurationById(String configrtnId, Quote quote){
		PartsPricingConfiguration currentConfigrtn = null;
		List configrtnsList = quote.getPartsPricingConfigrtnsList();
		for (Iterator iterator = configrtnsList.iterator(); iterator.hasNext();) {
			PartsPricingConfiguration configrtn = (PartsPricingConfiguration) iterator.next();
            if (StringUtils.equals(configrtnId, configrtn.getConfigrtnId())) {
                currentConfigrtn = configrtn;
                break;
			}
		}
		return currentConfigrtn;
	}





	/**
	 * @param SaaSLineItems
	 * @param rampUpDestNum, it's the ramp-up part's destSeqNum that related by subscription overrage part
	 * @return
	 * return the subscription overrage part
	 */
	public static QuoteLineItem getSaasSubscrptnOvragePartByDestNum(List SaaSLineItems, int rampUpDestNum) {
		if (SaaSLineItems == null) {
			return null;
		}
		for (int i = 0; i < SaaSLineItems.size(); i++) {
			QuoteLineItem item = (QuoteLineItem) SaaSLineItems.get(i);
			if(item.isSaasSubscrptnOvragePart()){
				if (item.getIRelatedLineItmNum() == rampUpDestNum){
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * @param SaaSLineItems, all the SaaS parts
	 * @param rampUpLineItems, the ramp-up parts related to the master subscription part
	 * @param masterOvrageQli, the master over rage part
	 * @return
	 * the sub over rage parts related to the master over rage part
	 */
	public static List getSubOvrageParts(List SaaSLineItems, List rampUpLineItems){
		List ovrageSubLineItems = new ArrayList();
		for (int i = 0; i < rampUpLineItems.size(); i++) {
        	QuoteLineItem rampUpQli = (QuoteLineItem) rampUpLineItems.get(i);
            QuoteLineItem ovrageQli = getSaasSubscrptnOvragePartByDestNum(SaaSLineItems, rampUpQli.getDestSeqNum());
            if(ovrageQli != null){
            	ovrageQli.setMasterOvrage(false);
            	ovrageQli.setRampUpPeriodNum(i+1);
            	ovrageSubLineItems.add(ovrageQli);
            }
		}
		return ovrageSubLineItems;
	}


	/**
	 * @param quote
	 * @return
	 * if offer price is equal to quote total extended price, return true
	 * else return false
	 */
	public static boolean isOfferPrcEquals2TotEntldExtPrc(Quote quote){
		Double offerPrc = quote.getQuoteHeader().getOfferPrice();
		if(offerPrc == null){
			return false;
		}
		if(quote.getQuoteHeader().getQuoteTotalEntitledExtendedPrice() == offerPrc.doubleValue()){
			return true;
		}
		return false;
	}

	/**
	 * @param quote
	 * @param qli
	 * @return part price rounding factor
	 * if rounding factor is config by part type in comm-part-price-config.xml (maximum-decimal-config)
	 * then return the config rounding factor
	 * else return the rounding factor by country
	 */
	public static int getPartPriceRoundingFactor(QuoteHeader header, QuoteLineItem qli){
		int roundingFactor = DecimalUtil.DEFAULT_SCALE;
		if (PartPriceConstants.RGNBASECUR_JPN.equals(header.getPriceCountry()
				.getCode3())) {
			return roundingFactor = header.getPriceCountry()
					.endCustPrckRoundingFactor();
		}
        int roundingByPartTypeFactor = PartPriceSaaSPartConfigFactory.singleton().getMaxDecimal(qli);
        if(roundingByPartTypeFactor == -1){
        	Country country = header.getPriceCountry();
	        if (country != null) {
	            roundingFactor = country.endCustPrckRoundingFactor();
	        }
        }else{
        	roundingFactor = roundingByPartTypeFactor;
        }
        return roundingFactor;
	}

    public static String getI18NString(String baseName,Locale locale, String key){
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        String value = appCtx.getI18nValueAsString(baseName, locale, key);
        if ((value == null) || value.equals("")) {
            value = key;
        }
        return value;
    }

	/**
	 * @param termMonths
	 * @param qli
	 * @return Number of Billing Periods (Committed Term / Months per Billing Period)
	 */
	public static double calculateBillingPeriods(int termMonths, QuoteLineItem qli){
		double billingPeriods = 0.0;
		if(PartPriceConstants.BillingOptionCode.ANNUAL.equals(qli.getBillgFrqncyCode())){
			billingPeriods = ((double)termMonths) / 12;
		}
		if(PartPriceConstants.BillingOptionCode.QUARTERLY.equals(qli.getBillgFrqncyCode())){
			billingPeriods = ((double)termMonths) / 3;
		}
		if(PartPriceConstants.BillingOptionCode.MONTHLY.equals(qli.getBillgFrqncyCode())){
			billingPeriods = ((double)termMonths) / 1;
		}
		if(PartPriceConstants.BillingOptionCode.UPFRONT.equals(qli.getBillgFrqncyCode())){
			billingPeriods = 1;
		}
		logContext.debug("QuoteCommonUtil","Line Item "+qli.getPartNum()+" billingPeriods is: " + billingPeriods);
		return billingPeriods;
	}


	/**
	 * 1.vailidate type is not null
	 * 2.validate model is not null
	 * 3.validate serial number is not null
	 * @param quote
	 * @return error message key
	 */
	public static Set validateApplncParts(Quote quote) {

		Set keys = new HashSet();

		List applncMTMParts = quote.getApplncMTMParts();
		if (applncMTMParts != null && applncMTMParts.size() > 0) {
			for (Object obj : applncMTMParts) {
				QuoteLineItem qli = (QuoteLineItem) obj;
				  if (qli.isApplianceRelatedSoftware()&&StringUtils.isEmpty(qli.getMachineType())&&StringUtils.isEmpty(qli.getModel())&&StringUtils.isEmpty(qli.getSerialNumber())) {
					    continue;
					}
				if (StringUtils.isEmpty(qli.getMachineType())) {
					keys.add(QuoteCapabilityProcess.MACHINE_TYPE_NOT_NULL);
				}

				if (StringUtils.isEmpty(qli.getModel())) {
					keys.add(QuoteCapabilityProcess.MACHINE_MODEL_NOT_NULL);
				}

				if (StringUtils.isEmpty(qli.getSerialNumber())) {

					keys.add(QuoteCapabilityProcess.MACHINE_SERIAL_NUMBER_NOT_NULL);
				}

				validateAddtnlYearLineItemMTM(qli);

			}
		}

		//add by Fall. Quantities can't be over limit
		Set errorKeys=null;
		if(quote.getApplncParts()!=null)
			errorKeys=isOverLimit(quote.getApplncParts());
		if(errorKeys!=null) keys.addAll(errorKeys);

		if(quote.getApplncParts()!=null&&
					quote.getApplncParts().size()>0&&
						isTotalQuantityOverLimit(quote))
			keys.add(QuoteCapabilityProcess.TOTAL_QTY_EXCEED_LIMIT);

		//Validate Appliance Upgrade part
		if (!validateUpgradeApplianceSerialNumber(quote)) {
			keys.add(QuoteCapabilityProcess.APPLIANCE_SERIAL_NUMBER_NOT_SAME);
	    }
		
		//14.2 validate Ownership Transfer Parts
		if (!validateOwnershipTransferPartsSerialNumber(quote)){
			keys.add(QuoteCapabilityProcess.OWNERSHIP_PARTS_SERIAL_NUMBER_NOT_SAME);
			
		}
		
		//14.2 Validate Appliance Part deployment id entered can be validated
		if(!validateAppliancePartDeploymentID(quote)){
			keys.add(QuoteCapabilityProcess.APPLIANCE_PARTS_DEPLOYMENTID_NOTBE_VALIDATED);
		}

		//15.3 Validate proof of concept for the appliance part
        if(!validateApplncPoc(quote)){
            keys.add(QuoteCapabilityProcess.APPLIANCE_PARTS_POC_VALIDATED);
        }

		return keys;
	}
	
    //Appliance#99
    public static boolean validationLineItemCRAD(Quote quote) {
    	QuoteHeader header = quote.getQuoteHeader();
    	List quoteItemList = quote.getMasterSoftwareLineItems();
    	Iterator iterator = quoteItemList.iterator();

    	boolean islineItemCRADError = false;
    	while (iterator.hasNext()){
    		QuoteLineItem qli = (QuoteLineItem)iterator.next();
    		// only validate crad for main/upgrade appliance part with app_send_mfg_flg=1
    		if (qli!=null && (qli.isApplncMain() || qli.isApplncUpgrade()) && qli.getApplncSendMFGFLG()){
    			Date lineItemCRAD = qli.getLineItemCRAD();
    			if (lineItemCRAD == null){
        			islineItemCRADError =  true;
        		} 
    		}

    	}
		return !islineItemCRADError;
    }
    
 // Check if App Send MFG flg =1 and is main appliance  or upgrade appliance part
    public static boolean isMainApplncOrUpgradeApplnc(QuoteLineItem qli){
 		boolean isApplncSendMFGFlg = qli.getApplncSendMFGFLG();
    	boolean isMainPartFlg = qli.isApplncMain();
    	boolean isUpgradeFlg = qli.isApplncUpgrade();
    	return isApplncSendMFGFlg &&(isMainPartFlg||isUpgradeFlg);	
 	}
 	
 	// check if is associated
    public static boolean isAssociated(QuoteLineItem qli){
 		boolean isApplncSendMFGFlg = qli.getApplncSendMFGFLG();
 		boolean isAssociatedFlg = !qli.isApplncMain() && qli.getConfigrtnId()!=null && !qli.getConfigrtnId().equals("NOT_ON_QUOTE");
 		boolean isTransceiverFlg = qli.isApplncTransceiver();
 		return isAssociatedFlg && (isTransceiverFlg || isApplncSendMFGFlg);
 	}
 	
    private static boolean isTotalQuantityOverLimit(Quote quote){
		List<QuoteLineItem> items=quote.getLineItemList();
		int count=0;
		for(QuoteLineItem item:items){
            /**
             * total quantities verification <BR>
             * 1. if part has restriction, count the qty as items else, just set it as one<BR>
             * 2. If the part has a part attribute to restrict quantity to 1 then count the part quantity as the number
             * of line items (appliance, appliance renewal, service packs)<BR>
             * - also include appliance related software when it is associated with an appliance that has a quantity of
             * > 1 <BR>
             */
            if (item.isApplncQtyRestrctn()) {
                count += item.getPartQty();
            } else if (isSpecialApplianceRelatedSoftware(item, quote)) {
                count += item.getPartQty();
            } else
                count++;
		}

		if(count>PartPriceAppliancePartConfigFactory.singleton().getTotalLineItemLimit()) return true;

		return false;
	}

    /**
     * DOC return true when appliance related software is associated with an appliance that has a quantity of > 1<BR>
     *  deal with special value of ApplianceId: APPLNC_NOT_ON_QUOTE, APPLNC_SELECT_DEFAULT and  APPLNC_NOT_ASSOCIATED
     * 
     * @param applRelatedSoftwareItem
     * @param quote
     * @return
     */
    private static boolean isSpecialApplianceRelatedSoftware(QuoteLineItem applRelatedSoftwareItem, Quote quote) {
        String relatedApplianceId = applRelatedSoftwareItem.getApplianceId();
        if ((applRelatedSoftwareItem.isApplianceRelatedSoftware()) && StringUtils.isNotBlank(relatedApplianceId)
                && !(PartPriceConstants.APPLNC_NOT_ON_QUOTE.equals(relatedApplianceId))
                && !(PartPriceConstants.APPLNC_SELECT_DEFAULT.equals(relatedApplianceId))
                && !(PartPriceConstants.APPLNC_NOT_ASSOCIATED.equals(relatedApplianceId))) {            
            QuoteLineItem relatedApplianceItem = getLineItemByApplianceId(relatedApplianceId, quote);            
            if (relatedApplianceItem != null && relatedApplianceItem.getPartQty() > 1) {                   
            	return true;
            }
        }
        return false;
    }

    /**
     * add by Fall verify quantitiy is over limit There're 3 rules. <BR>
     * 1.If quantities of related part are over limit. <br>
     * 2.if total quantities are over limit <br>
     * 3.if appliance related parts are over 1.<br>
     * 
     * 4.modify the 3rd rule above: refer to 611190: DSW 14.2 Appliance SQO/PGS Serial # validation requirement -
     * Quantity of > 1 can be accepted
     */
	private static Set isOverLimit(List appincParts){
		Set errorKeys=new HashSet();
        int count = 0;
		for(Object o:appincParts){
			QuoteLineItem item=(QuoteLineItem)o;

            // if quantities is of appliance related parts are over 1
            // Related software can have a quantity > 1 even if there is a MTM/Serial # on the line item
            if (!errorKeys.contains(QuoteCapabilityProcess.MTM_APPLIANCE_QTY_GREATER_THAN_ONE)
                    && item.isApplncQtyRestrctn()
                    && (StringUtils.isNotBlank(item.getMachineType()) || StringUtils.isNotBlank(item.getSerialNumber()) || StringUtils
                            .isNotBlank(item.getModel())) && item.getPartQty() > 1) {
                errorKeys.add(QuoteCapabilityProcess.MTM_APPLIANCE_QTY_GREATER_THAN_ONE);
            }
            // verify quantity of related appliance part
            // This will now include appliance related software but we only do this check if the associated appliance
            // has a quantity > 1.
			String relatedNum=item.getApplianceId();
            if (!errorKeys.contains(QuoteCapabilityProcess.RELATED_ITEM_QTY_EXCEED_APPLIANCE)
                    && (item.isApplncMain() || item.isApplncUpgrade()) && StringUtils.isNotBlank(relatedNum)) {

				List relatedItems=getItemsById(relatedNum,appincParts);

				for(int i=0;relatedItems!=null&&i<relatedItems.size();i++){

					QuoteLineItem qli = (QuoteLineItem)relatedItems.get(i);
                    // Quote submission rules:
                    if (qli.isApplianceRelatedSoftware() && item.getPartQty() == 1) {
                        continue;
                    }
                    if (!qli.isApplncQtyRestrctn() && (!(qli.isApplianceRelatedSoftware() && item.getPartQty() > 1))) {
						continue;
					}
					if(qli.getPartQty() > item.getPartQty()){
						errorKeys.add(QuoteCapabilityProcess.RELATED_ITEM_QTY_EXCEED_APPLIANCE);
						break;
					}
				}
			}
		}

		return errorKeys.size()==0?null:errorKeys;
	}

	private static List getItemsById(String num,List lineItems){
		List items=null;
		for(Object o:lineItems){
			QuoteLineItem item=(QuoteLineItem)o;
			if(PartPriceAppliancePartConfigFactory.singleton().displayApplianceIdDropdown(item)
					&& StringUtils.isNotBlank(item.getApplianceId())
					&& item.getApplianceId().equals(num)){
				if(items==null) items=new ArrayList();
				items.add(item);
			}
		}

		return items;
	}


	private static void validateAddtnlYearLineItemMTM(QuoteLineItem lineItem){
		if(lineItem.getAddtnlYearCvrageLineItems()!=null && lineItem.getAddtnlYearCvrageLineItems().size()>0){
			for (Object obj : lineItem.getAddtnlYearCvrageLineItems()){
				QuoteLineItem qli = (QuoteLineItem) obj;

				if (StringUtils.isEmpty(qli.getMachineType())) {
					logContext.debug(singleton(), "part number:"+qli.getPartNum()+" A machine type must be not null");
				}

				if (StringUtils.isEmpty(qli.getModel())) {
					logContext.debug(singleton(), "part number:"+qli.getPartNum()+" A machine model must be not null");
				}

				if (StringUtils.isEmpty(qli.getSerialNumber())) {
					logContext.debug(singleton(), "part number:"+qli.getPartNum()+" A machine serial number must be not null");
				}
			}
		}
	}


	/**
	 * Appliance Part all selected New system return true, others return false
	 */
	public static Boolean validateApplncPriorPoc(Quote quote){
		Boolean isValidate = true;
		List applianceParts = quote.getApplncParts();
		if (applianceParts != null) {
			for (Object obj : applianceParts) {
				QuoteLineItem qli = (QuoteLineItem) obj;
				if (qli.isApplncPriorPoc()) {
					isValidate = false;
					break;
				}
			}
		}
		return isValidate;
	}

	/**
	 * Upgrade Appliance part can't have same serial number
	 * @param quote
	 * @return
	 */
	public static Boolean validateUpgradeApplianceSerialNumber(Quote quote){
		Boolean isValidate = true;

		if(quote.getApplncUpgradeParts() != null)
			for(Object obj: quote.getApplncUpgradeParts()){
				QuoteLineItem qli = (QuoteLineItem)obj;
				for(Object obj2: quote.getApplncUpgradeParts()){
					QuoteLineItem compareQli = (QuoteLineItem)obj2;
					if(compareQli != qli
							&& (!StringUtils.trim(qli.getSerialNumber()).equals(""))
							&& (!StringUtils.trim(compareQli.getSerialNumber()).equals(""))
							&& qli.getSerialNumber().equals(compareQli.getSerialNumber())){
						isValidate = false;
						break;
					}
				}
				if(!isValidate)
					break;
			}
		return isValidate;
	}
	
	/**
	 * Ownership Transfer Parts can't have same serial number
	 * @param quote
	 * @return
	 */
	public static Boolean validateOwnershipTransferPartsSerialNumber(Quote quote){
		Boolean isValidate = true;
		
		if(quote.getApplncOwnerShipParts() != null){
			Set serialNums = new HashSet();
			for(Object ownershipTransferPart : quote.getApplncOwnerShipParts()){
				QuoteLineItem qli = (QuoteLineItem)ownershipTransferPart;
				// repeat the serial num for all ownership transfer
				if(StringUtils.isNotBlank(qli.getSerialNumber()) && serialNums.contains(qli.getSerialNumber())){
					isValidate = false;
					break;
				}else if(StringUtils.isNotBlank(qli.getSerialNumber())){
						serialNums.add(qli.getSerialNumber());
					}
				}
		}
		
		return isValidate;
	}
	
	/**
	 * appliance part deploy id must not be empty and format must be 'W' + 10 digit
	 * @param quote
	 * @return
	 */
	public static Boolean validateAppliancePartDeploymentID(Quote quote){
		Boolean isValidate = true;
		
		List mainApplianceList = quote.getApplncMains();
		if(mainApplianceList != null){
			for(int i = 0; i < mainApplianceList.size(); i++){
				QuoteLineItem qli = (QuoteLineItem)mainApplianceList.get(i);
				DeployModel deployModel = qli.getDeployModel();
				
				if( PartPriceConstants.DEPLOYMENT_SELECT_DEFAULT.equals(deployModel.getDeployModelOption()) ){
					continue;
				}
				
				if( StringUtils.isBlank(deployModel.getDeployModelId()) ){
					return false;
					//isValidate = false;
					//break;
				}
			}
		}
		return isValidate;
	}
	

	/**
	 * @param qli
	 * @return if show appliance part start date and end date, return true
	 */
	public static boolean isShowDatesForApplnc(QuoteLineItem qli){
		boolean isShowDatesForApplnc = false;
		// appliance main part, upgrade part, transceiver part and ownership transfer part don't display start/end date
		if(qli.isApplncMain() || qli.isApplncUpgrade() || qli.isApplncTransceiver() || qli.isOwerTransferPart()){
			isShowDatesForApplnc = false;
		}
		else if(qli.isApplncServicePack()
				|| qli.isApplncServicePackRenewal()
				|| qli.isApplncRenewal()
				|| qli.isApplianceRelatedSoftware()){
			if(qli.isHasApplncId()){
				isShowDatesForApplnc = false;
			}else{
				isShowDatesForApplnc = true;
			}
		}
		//reinstatement part display start/end date
		else if(qli.isApplncReinstatement())
			isShowDatesForApplnc = true;

		return isShowDatesForApplnc;
	}

	public static String getOrignlConfigrtnIdForFnl(List SaaSLineItems,PartsPricingConfiguration ppc){
		String orignlConfigrtnId = null;
		Iterator subSaaSlineItemIt = SaaSLineItems.iterator();
		while(subSaaSlineItemIt.hasNext()){
			QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemIt.next();
			if(qli.isSaasPart() && qli.getOrignlConfigrtnId() != null && qli.getConfigrtnId().equalsIgnoreCase(ppc.getConfigrtnId())){
				orignlConfigrtnId = qli.getOrignlConfigrtnId();
				break;
			}
		}
		return orignlConfigrtnId;
	}

    /**
     *  transform the quote lob to cognos lob
     *  YWHC-8WD54P No result returned according to test data.
     *  Notes://CAMDB10/85256B890058CBA6/39528B5E1014EA3E85256D24005FCB4D/731EDC0FF0A6873285257A41000E7117
     * @param quoteLob
     * @return
     */
    public static String transLobForCognos(String quoteLob){
    	if(quoteLob==null){
    		return null;
    	}
    	quoteLob = quoteLob.trim();
    	if(QuoteConstants.LOB_PA.equals(quoteLob)){
    		return QuoteConstants.CognosLob.LOB_PA;
    	}else if(QuoteConstants.LOB_PAE.equals(quoteLob)){
    		return QuoteConstants.CognosLob.LOB_PX;
    	}else if(QuoteConstants.LOB_OEM.equals(quoteLob)){
    		return QuoteConstants.CognosLob.LOB_PA;
    	}else if(QuoteConstants.LOB_FCT.equals(quoteLob)){
    		return QuoteConstants.CognosLob.LOB_FC;
    	}else if(QuoteConstants.LOB_PPSS.equals(quoteLob)){
    		return QuoteConstants.CognosLob.LOB_PX;
    	}else if(QuoteConstants.LOB_PAUN.equals(quoteLob)){
    		return QuoteConstants.CognosLob.LOB_PX;
    	}else if(QuoteConstants.LOB_SSP.equals(quoteLob)){
    		return QuoteConstants.CognosLob.LOB_SP;
    	}else {
        	return null;
    	}
    }

	/**
	 * transform the quote lob to cognos lob YWHC-8WD54P No result returned
	 * according to test data.
	 * Notes://CAMDB10/85256B890058CBA6/39528B5E1014EA3E85256D24005FCB4D
	 * /731EDC0FF0A6873285257A41000E7117
	 * 
	 * @param quoteLob
	 * @return
	 */
	public static String transLobForCognos(String quoteLob, String quoteAgrmtTypeCode) {
		if (quoteLob == null) {
			return null;
		}
		quoteLob = quoteLob.trim();
		if (QuoteConstants.LOB_PA.equals(quoteLob)) {
			if (QuoteConstants.LOB_CSRA.equals(quoteAgrmtTypeCode)) {
				return QuoteConstants.CognosLob.LOB_CR;
			}
			return QuoteConstants.CognosLob.LOB_PA;
		} else if (QuoteConstants.LOB_PAE.equals(quoteLob)) {
			if (QuoteConstants.LOB_CSTA.equals(quoteAgrmtTypeCode)) {
				return QuoteConstants.CognosLob.LOB_CT;
			}
			return QuoteConstants.CognosLob.LOB_PX;
		} else if (QuoteConstants.LOB_OEM.equals(quoteLob)) {
			return QuoteConstants.CognosLob.LOB_PA;
		} else if (QuoteConstants.LOB_FCT.equals(quoteLob)) {
			return QuoteConstants.CognosLob.LOB_FC;
		} else if (QuoteConstants.LOB_PPSS.equals(quoteLob)) {
			return QuoteConstants.CognosLob.LOB_PX;
		} else if (QuoteConstants.LOB_PAUN.equals(quoteLob)) {
			return QuoteConstants.CognosLob.LOB_PX;
		} else if (QuoteConstants.LOB_SSP.equals(quoteLob)) {
			return QuoteConstants.CognosLob.LOB_SP;
		} else {
			return null;
		}
	}

	/**
	 * Check renewal model code - C for Add on
	 * Notes://CAMDB10/85256B890058CBA6/5D3D446FEB23FC918525718E006EAFDE/9E66451688684F2485257A7F0011215D
	 */
	public static Boolean validateRnwalMdlC(Quote quote) throws TopazException{
		Boolean isValidate = true;
		List SaaSLineItems = quote.getSaaSLineItems();
		if(SaaSLineItems == null || SaaSLineItems.size() == 0){
			return true;
		}
		//If quote is a Add on quote, need to calculate term and check renewal mode code.

		List configrtnsList = quote.getPartsPricingConfigrtnsList();
		if (configrtnsList == null || configrtnsList.size() == 0) {
			return true;
		}
		Map configrtnsMap = quote.getPartsPricingConfigrtnsMap();
		Iterator configrtnsIt = configrtnsList.iterator();
		while(configrtnsIt.hasNext()){
			PartsPricingConfiguration ppc = (PartsPricingConfiguration) configrtnsIt.next();
			if(!PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(ppc.getConfigrtnActionCode())){
				continue;
			}
			RedirectConfiguratorDataBasePack dataPack = null;
			try {
				dataPack = PartPriceHelper.calculateAddOnFinalizationTerm(
						quote.getQuoteHeader().getRefDocNum(), ppc.getConfigrtnId(), getProvsngDate(quote, ppc), ppc.getEndDate(), ppc.getConfigrtnActionCode(),false,ppc.getServiceDateModType());
				if(null != dataPack && StringUtils.isNotBlank(dataPack.getFctToPaCalcTerm()) && Integer.valueOf(dataPack.getFctToPaCalcTerm()) >= 1) {//initial calculated term is greater than 1.
					continue;
				}
				List subSaaSlineItemList = (List)configrtnsMap.get(ppc);
				if(subSaaSlineItemList == null || subSaaSlineItemList.size() == 0) {
					continue;
				}
				Iterator subSaaSlineItemIt = subSaaSlineItemList.iterator();

				ConfiguratorPartProcess process = ConfiguratorPartProcessFactory.singleton().create();
				Map<String, ConfiguratorPart> configrtrPartMap = process.findMainPartsFromChrgAgrm(quote.getQuoteHeader().getRefDocNum(), ppc.getConfigrtnId());
				if(configrtrPartMap == null || configrtrPartMap.size() ==0 )
					continue;
				while(subSaaSlineItemIt.hasNext()){
					QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemIt.next();
					if(qli.isSaasSubscrptnPart() && !qli.isReplacedPart()){
						ConfiguratorPart cp = configrtrPartMap.get(qli.getPartNum());
						if(cp == null || cp.getPartQty() == null)
							continue;
						if(!cp.getPartQty().equals(qli.getPartQty()) && PartPriceConstants.RenewalModelCode.C.equals(cp.getRenwlMdlCode())){
							isValidate = false;
							break;
						}
					}
				}
			} catch (QuoteException e) {
				throw new TopazException(e);
			}
		}
		return isValidate;
	}

    public static Boolean validateRemingTermForRewalMdl(Quote quote) throws  TopazException{
    	boolean isValidate = true;
    	Map configrtnsMap = quote.getPartsPricingConfigrtnsMap();
    	List configrtnsList = quote.getPartsPricingConfigrtnsList();
    	if (configrtnsList == null || configrtnsList.size() == 0) {
			return true;
		}
		Iterator configrtnsIt = configrtnsList.iterator();
		while(configrtnsIt.hasNext()){
			PartsPricingConfiguration ppc = (PartsPricingConfiguration) configrtnsIt.next();
			if(!PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(ppc.getConfigrtnActionCode())){
				continue;
			}

			try {
				List subSaaSlineItemList = (List)configrtnsMap.get(ppc);
				if(subSaaSlineItemList == null || subSaaSlineItemList.size() == 0) {
					continue;
				}

				Iterator subSaaSlineItemIt = subSaaSlineItemList.iterator();
				ConfiguratorPartProcess	process = ConfiguratorPartProcessFactory.singleton().create();
				Map<String, ConfiguratorPart> configrtrPartMap = process.findMainPartsFromChrgAgrm(quote.getQuoteHeader().getRefDocNum(), ppc.getConfigrtnId());
				if(configrtrPartMap == null || configrtrPartMap.size() ==0 )
					continue;
				while(subSaaSlineItemIt.hasNext()){
					QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemIt.next();
					if (qli.isSaasSubscrptnPart()){
						ConfiguratorPart cp = configrtrPartMap.get(qli.getPartNum());
						if (cp == null) continue;
						if (PartPriceConstants.RenewalModelCode.C.equals(cp.getRenwlMdlCode())
								|| PartPriceConstants.RenewalModelCode.T.equals(cp.getRenwlMdlCode())){
							if (cp.getNextRenwlDate() == null && qli.getCumCvrageTerm()!=null && qli.getCumCvrageTerm().intValue() <0){
								isValidate = false;
								break;
							}
						}
					}
				}
			} catch (QuoteException e) {
				throw new TopazException(e);
			}

		}
		return isValidate;
    }

	private static java.sql.Date getProvsngDate(Quote quote, PartsPricingConfiguration ppc) {
		java.util.Date estmtdOrdDate = quote.getQuoteHeader().getEstmtdOrdDate() == null ? DateUtil.getCurrentYYYYMMDDDate() : quote.getQuoteHeader().getEstmtdOrdDate();
		int provisngDays = ppc.getProvisngDays() == null ? 0 : ppc.getProvisngDays().intValue();
		Calendar estdPrvsngCal = Calendar.getInstance();//Estimated provisioning Date
		estdPrvsngCal.setTime(estmtdOrdDate);
		estdPrvsngCal.add(Calendar.DATE, provisngDays);//Estimated provisioning Date = estimated Order Date + item's provisioning days
		java.sql.Date provsngDate = new java.sql.Date(estdPrvsngCal.getTime().getTime());
		return provsngDate;
	}

    public static String getProvisAppEnvUrl(){
    	String code = ApplicationProperties.getInstance().getQtAppEnv();
    	String provisAppEnvUrl = "";
    	try {
			provisAppEnvUrl =QuoteApplEnvrnmtFactory.singleton().findQuoteApplEnvrnmtURLByCode(code);
		} catch (TopazException e) {
			logContext.debug(QuoteCommonUtil.class, "Can not get Application environment description. code ="+ code );
		}
		return provisAppEnvUrl;
    }

    public static String getQtAppSQOEnvrnmUrl(){
    	String code = ApplicationProperties.getInstance().getQtAppEnv();
    	String qtAppSQOEnvUrl = "";
    	try {
			qtAppSQOEnvUrl = QtApplSQOEnvrnmtFactory.singleton().findQtApplSQOEnvrnmtURLByCode(code);
		} catch (TopazException e) {
			logContext.debug(QuoteCommonUtil.class, "Can not get SQO Application environment description. code ="+ code );
		}
		return qtAppSQOEnvUrl;
    }

    public static String getQtAppPGSEnvrnmUrl(){
    	String code = ApplicationProperties.getInstance().getQtAppEnv();
    	String qtAppPGSEnvUrl = "";
    	try {
    		qtAppPGSEnvUrl = QtApplPGSEnvrnmtFactory.singleton().findQtApplPGSEnvrnmtURLByCode(code);
		} catch (TopazException e) {
			logContext.debug(QuoteCommonUtil.class, "Can not get PGS Application environment description. code ="+ code );
		}
		return qtAppPGSEnvUrl;

    }
	public static boolean isApplncQtyGtOne(QuoteLineItem qli){
    	if(qli != null){
    		Integer qty = qli.getPartQty();

    		if(qli.isApplncQtyRestrctn()
    				&& (qty != null && qty.intValue() > 1)){
    			return true;
    		}
    	}

    	return false;
    }

    public static boolean isDecimalNumber(String number) {
    	  return match(NUMBER_PATTERN, number);
    }

    public static boolean isSkipLineItemDateValidate(QuoteLineItem lineItem){
    	 if(lineItem.isApplncPart()){
             if(lineItem.isApplncMain()
                 || lineItem.isApplncReinstatement()
                 || lineItem.isApplncTransceiver()
            	 || lineItem.isOwerTransferPart()){
                 return true;
             }else{
                 if(StringUtils.isNotBlank(lineItem.getApplianceId())
                     && !PartPriceConstants.APPLNC_NOT_ON_QUOTE.equals(lineItem.getApplianceId())
                     && !PartPriceConstants.APPLNC_NOT_ASSOCIATED.equals(lineItem.getApplianceId())){
                     return true;
                 }
             }
         }
         return false;
    }

    public static List<QuoteLineItem> findChangeAbleBillingFrequencyIineItems(Quote quote) {
		List SaaSLineItems = quote.getSaaSLineItems();
		List monthlySwLineItems = quote.getMonthlySwQuoteDomain().getMasterMonthlySwLineItems();
		if((SaaSLineItems == null || SaaSLineItems.size() == 0)&&(monthlySwLineItems == null || monthlySwLineItems.size() == 0)){
			return null;
		}

		List configrtnsList = quote.getPartsPricingConfigrtnsList();
		List<QuoteLineItem> changeAbleLineItems = new ArrayList<QuoteLineItem>();
		if(configrtnsList != null && configrtnsList.size()>0){
			Map configrtnsMap = quote.getPartsPricingConfigrtnsMap();
			Iterator configrtnsIt = configrtnsList.iterator();
			while(configrtnsIt.hasNext()) {
				PartsPricingConfiguration ppc = (PartsPricingConfiguration) configrtnsIt.next();
				// Check if it is not 'AddTrd', 'trade-ups' and 'FCT_TO_PA_FNL' configuration.
				if(!PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(ppc.getConfigrtnActionCode()) &&
						!PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(ppc.getConfigrtnActionCode())) {
					List subSaaSlineItemList = (List)configrtnsMap.get(ppc);
					if(subSaaSlineItemList != null || subSaaSlineItemList.size() != 0) {
						Iterator subSaaSlineItemIt = subSaaSlineItemList.iterator();
						while(subSaaSlineItemIt.hasNext()){
							QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemIt.next();
							// Check if the lineltem has the billing frequency value.
							// Check if it's default billing frequency is 'monthly'.
							if (StringUtils.isNotEmpty(qli.getBillgFrqncyCode()) &&
									ConfiguratorConstants.BILLING_FREQUENCY_MONTHLY.equals(qli.getBillgFrqncyCode())) {
								changeAbleLineItems.add(qli);
							}
						}
					}
				}
			}
		}
		List monthlyBillingSwLineItems=findChangeAbleBillingFrequencyMonthlySoftWareIineItems(quote);
		if(null!=monthlyBillingSwLineItems){
			changeAbleLineItems.addAll(monthlyBillingSwLineItems);
		}
		Set<List> set = new HashSet<List>();
		for (QuoteLineItem lineItem : changeAbleLineItems) {
			List<QuoteLineItemBillingOption> options = lineItem.getBillingOptions();
			List<String> partOptionList = new ArrayList<String>();
			for (QuoteLineItemBillingOption option : options) {
				partOptionList.add(option.getCode());
			}
			//Check if the changeable lineItems have the same billing frequency options.
			set.add(partOptionList);
		}
		if (set.size() == 1) {
			return changeAbleLineItems;
		} else {
			return null;
		}
	}
    
    public static List<QuoteLineItem> findChangeAbleBillingFrequencyMonthlySoftWareIineItems(Quote quote) {
    	List<QuoteLineItem> changeAbleLineItems = new ArrayList<QuoteLineItem>();
    	List<MonthlySoftwareConfiguration> configrtnsList= quote.getMonthlySwQuoteDomain().getMonthlySwConfgrtns();
    	Map monthlyConfigrtnsMap=quote.getMonthlySwQuoteDomain().getMonthlySwConfigrtnsMap();
    	Iterator<MonthlySoftwareConfiguration> configrtnsIt = configrtnsList.iterator();
		while(configrtnsIt.hasNext()) {
			MonthlySoftwareConfiguration ppc = configrtnsIt.next();
			// Check if it is not 'AddTrd', 'trade-ups' and 'FCT_TO_PA_FNL' configuration.
			if(!PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(ppc.getConfigrtnActionCode()) &&
					!PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(ppc.getConfigrtnActionCode())) {
				List monthlylineItemList = (List)monthlyConfigrtnsMap.get(ppc);
				if(monthlylineItemList != null || monthlylineItemList.size() != 0) {
					Iterator monthlylineItemIt = monthlylineItemList.iterator();
					while(monthlylineItemIt.hasNext()){
						QuoteLineItem qli = (QuoteLineItem) monthlylineItemIt.next();
						// Check if the lineltem has the billing frequency value.
						// Check if it's default billing frequency is 'monthly'.
						if (StringUtils.isNotEmpty(qli.getBillgFrqncyCode()) &&
								ConfiguratorConstants.BILLING_FREQUENCY_MONTHLY.equals(qli.getBillgFrqncyCode())) {
							changeAbleLineItems.add(qli);
						}
					}
				}
			}
		}	
		return changeAbleLineItems;
	}

    private static boolean match(String pattern, String str) {
    	  Pattern p = Pattern.compile(pattern);
    	  Matcher m = p.matcher(str);
    	  return m.find();
    }
    
    public static void clearOfferPrice(Quote quote) throws TopazException{
    	if(quote != null){
    		if(quote.getLineItemList() != null && quote.getLineItemList().size() > 0){
    			for( Iterator iter= quote.getLineItemList().iterator(); iter.hasNext();){
                	QuoteLineItem qli = (QuoteLineItem)iter.next();
                	if(qli != null){
                		if((qli.isOfferIncldFlag() == null) || (qli.isOfferIncldFlag().booleanValue())){
        					qli.setOvrrdExtPrice(null);
                		}
                		qli.setOfferIncldFlag(null);
                	}
                }
    		}
    		if(quote.getQuoteHeader() != null ){
    			quote.getQuoteHeader().setOfferPrice(null);
    		}
    	}
    }
    
    /**
     * check if the end date of one item lines up to anniversary
     * @param item
     * @return
     */
    public static boolean checkLineUp2Anniversary(Quote quote,QuoteLineItem item){
        //get start date and end date of current line item
    	java.util.Date endDate = item.getMaintEndDate();
    	java.util.Date startDate = item.getMaintStartDate();
        //get anniversary of customer
        Customer cust = quote.getCustomer();
        
        if(startDate == null
                || endDate == null
                || cust == null 
                || cust.getContractList() == null
                || cust.getContractList().size() <= 0){
            return false;
        }
        Contract contract = (Contract)cust.getContractList().get(0);
        if(contract != null){
            //get anniversary of customer
        	java.util.Date tempAnniversary = contract.getAnniversaryDate();
            if(tempAnniversary != null){
                
                //check if anniverary is equals with the next day of endDate
            	java.util.Date endDateNextDay = DateUtil.plusOneDay(endDate);
                
            	java.util.Date anniversary = DateUtil.getNextAnniversary(startDate,tempAnniversary);
                if(DateUtil.isYMDEqual(endDateNextDay,anniversary)){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * if quote is PA/PAE, channel, special bid and GOE review is true, 
     * then set GOE review message into Special Customer Groupings list
     */
    public static boolean isNeedGOEReview(Quote quote){

    	if( quote.getQuoteHeader().isSalesQuote() && (quote.getQuoteHeader().isPAQuote() || quote.getQuoteHeader().isPAEQuote() && !quote.getQuoteHeader().isMigration() ) 
        		&& QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(quote.getQuoteHeader().getFulfillmentSrc())
        		&& quote.getQuoteHeader().getSpeclBidFlag() == 1
        		&& quote.getCustomer() != null
        		&& quote.getCustomer().isGOECust()){
    		return true;
    	}
    	return false;
    }
    
	public static boolean isAddApplncMTMMsgToMessageBean(Quote quote) {
        return quote.getQuoteHeader().isSerialNumWarningFlag();
	}
    
	public static String getMonthlyOrignlConfigrtnIdForFnl(List SaaSLineItems,MonthlySoftwareConfiguration ppc){
		String orignlConfigrtnId = null;
		Iterator subSaaSlineItemIt = SaaSLineItems.iterator();
		while(subSaaSlineItemIt.hasNext()){
			QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemIt.next();
			if(qli.isMonthlySoftwarePart() && qli.getOrignlConfigrtnId() != null && qli.getConfigrtnId().equalsIgnoreCase(ppc.getConfigrtnId())){
				orignlConfigrtnId = qli.getOrignlConfigrtnId();
				break;
			}
		}
		return orignlConfigrtnId;
	}
	
	public static boolean isNeededConfigMonthlySWMsgToMessageBean(Quote quote) {
		return quote.getQuoteHeader().isNeedReconfigFlag();
	}

    public static boolean isQuoteBeforeOrder(QuoteHeader quoteHeader) {
        if (quoteHeader != null & quoteHeader.getQuoteOverallStatuses().size() != 0) {
            if (quoteHeader.containsOverallStatus(OverallStatus.AWAITING_SPEC_BID_APPR)) {
                return true;
            }
            if (quoteHeader.containsOverallStatus(OverallStatus.SPEC_BID_RETURN_FOR_ADDI_INFO)) {
                return true;
            }
            if (quoteHeader.containsOverallStatus(OverallStatus.SPEC_BID_APPROVED)) {
                return true;
            }
            if (quoteHeader.containsOverallStatus(OverallStatus.QUOTE_ON_HOLD)) {
                return true;
            }
            if (quoteHeader.containsOverallStatus(OverallStatus.READY_TO_ORDER)) {
                return true;
            }
            return false;
        }
        return true;
    }
    
    public static List translateReasonCode2Key(List list)
    {
        List ret = new ArrayList();
        if ( list == null || list.size() == 0 )
        {
            return ret;
        }
        SpecialBidReasonConfigFactory factory = SpecialBidReasonConfigFactory.singleton();
        for ( int i = 0; i < list.size(); i++ )
        {
            String code = (String)list.get(i);
            String key = (String)factory.getDisplayKey(code);
            if ( key != null )
            {
                ret.add(key);
            }
        }
        return ret;
    }
    public static Double getTotalPriceInUSD(QuoteHeader header){
        try {
            String quoteCntryCode = header.getPriceCountry().getCode3();
            String quoteCurrencyCode = header.getCurrencyCode();           
            Double tot = CurrencyConversionFactorFactory.singleton()
                                  .convertToAnotherCurrency(quoteCntryCode, quoteCurrencyCode, 
                                                         QuoteConstants.CURRENCY_USD, header.getQuotePriceTot());
            return tot;
		} catch (TopazException e) {
			logContext.error(QuoteCommonUtil.class, LogThrowableUtil.getStackTraceContent(e));
		}
		
		return 0.0D;
	}
    
    /**
     * calculate total net TCV increase by sum up all the configuration
     * @param partsPricingCfgList
     * @return null or total net tcv increase
     */
    public static Double calculateTotalNetTCVIncrease(Collection<PartsPricingConfiguration> partsPricingCfgList){
    	if(partsPricingCfgList == null || partsPricingCfgList.size() == 0) return null;
    	Double totalNetTcvIncrease = null;
    	Iterator<PartsPricingConfiguration> iterator = partsPricingCfgList.iterator();
    	while(iterator.hasNext()){
    		PartsPricingConfiguration ppc = iterator.next();
    		Double tcvInc = ppc.getIncreaseBidTCV();
    		if(tcvInc != null){
    			totalNetTcvIncrease = tcvInc + (totalNetTcvIncrease == null ? 0.0 : totalNetTcvIncrease);
    		}
    	}
    	return totalNetTcvIncrease;
    }
    
    /**
     *	Calculate remainingTermTillCAEndDate
     * @throws QuoteException 
     * 
     * */
   public static void calculateRemainingTermTillCAEndDate(Quote quote) throws QuoteException{
	   
	   //1. get configuration list
	   List configrtnsList = quote.getPartsPricingConfigrtnsList();
	   if (configrtnsList == null || configrtnsList.size() == 0) {
			return;
		}
	   
	   Iterator configrtnsIt = configrtnsList.iterator();
	   while(configrtnsIt.hasNext()){
		   //2. get detailed part price configuration
		   PartsPricingConfiguration ppc = (PartsPricingConfiguration) configrtnsIt.next();
		   boolean isTermExtension = ppc.isTermExtension();
		   
		   QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		   String configId = ppc.getConfigrtnId();
		   String chrgAgrmtNum = quote.getQuoteHeader().getRefDocNum();
           
			// if the configuration is not AddOn-TradeUp or the charge agreement
			// number is null,continue
			if (!ppc.isAddOnTradeUp() || StringUtils.isBlank(chrgAgrmtNum)||!isTermExtension) {
				continue;
			}

			//3. get activeServiceMap of CA
			Map<String, ActiveService> activeServicMap = getActiveServiceMap(quoteProcess, chrgAgrmtNum, ppc.getConfigrtnId());
           
           //4. calculate provisioning date
           //4.1 get estimated order date
           java.util.Date estmtdOrdDate = quote.getQuoteHeader().getEstmtdOrdDate() == null ? DateUtil.getCurrentYYYYMMDDDate() : quote.getQuoteHeader().getEstmtdOrdDate();
           //4.2 get provisioning days from configuration
           int provisngDays = ppc.getProvisngDays() == null ? 0 : ppc.getProvisngDays().intValue();
           //4.3 Estimated provisioning Date = estimated Order Date + item's provisioning days
   		   Calendar estdPrvsngCal = Calendar.getInstance();
   		   estdPrvsngCal.setTime(estmtdOrdDate);
   		   estdPrvsngCal.add(Calendar.DATE, provisngDays);
   		   Date provsngDate = new Date(estdPrvsngCal.getTime().getTime());
   		   
   		   //5. get ppc lineitem list 
   		   Map configrtnsMap = quote.getPartsPricingConfigrtnsMap();
   		   List subSaaSlineItemList = (List) configrtnsMap.get(ppc);
   		   if (subSaaSlineItemList == null || subSaaSlineItemList.size() == 0) {
   			   continue;
   		   }
   		   Iterator subSaaSlineItemIt = subSaaSlineItemList.iterator();
   		   
   		   //6. get end date from active Service map for each line Item
   		   while (subSaaSlineItemIt.hasNext()) {
 			   QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemIt.next();
 			   if ((qli.isSaasSubscrptnPart() || qli.isSaasSubsumedSubscrptnPart())&& qli.isReplacedPart()) {
 				   if (activeServicMap!=null){
 					  ActiveService as = activeServicMap.get(qli.getPartNum());
 	 				  
 	 				  if (as == null) {
 	 	                  continue;
 	 	 			  }

 	 				  calculateRemainingTermForLineItem(as, qli, provsngDate);
 	 				  continue;
 				   } 
 			   }
 			   
   		   }        
	   }
   }
   
    public static void calculateRemainingTermForLineItem(ActiveService as,
			QuoteLineItem qli, Date provsngDate) {
		Date CAEndDate = DateUtil.parseDate(as.getEndDate());
		Date renewalEndDate = as.getRenewalEndDate();
		String renewalModelCode = qli.getRenwlMdlCode();
		int remainTerm = 0;
		// 1. compute remain term by caEndDate
		if (CAEndDate != null) {
			remainTerm = DateUtil.calculateFullMonths(provsngDate,
					CAEndDate);
		}
		// 2.compute remain term by renewalEndDate
		if (remainTerm <= 0 && renewalEndDate != null) {
			remainTerm = DateUtil.calculateFullMonths(provsngDate,
					renewalEndDate);
		}

		// 3.if term >0 means ca end date not expired so set calculate term to
		// remain term
		if (remainTerm == 0) {
			if (PartPriceConstants.RenewalModelCode.R
					.equalsIgnoreCase(renewalModelCode)
					|| PartPriceConstants.RenewalModelCode.O
							.equalsIgnoreCase(renewalModelCode)) {
				remainTerm = as.getRenwlTermMths();
			}
		}
		if (remainTerm < 0)
			remainTerm = 0;
		qli.setRemainingTermTillCAEndDate(remainTerm);

	}

   //get active service map for given chargeAgreement and configuration
   public static Map<String, ActiveService> getActiveServiceMap(
			QuoteProcess quoteProcess, String caNum, String configId)
			throws QuoteException {
		Map<String, ActiveService> activeServicMap = new HashMap<String, ActiveService>();
		if (caNum == null) {
			return activeServicMap;
		}

		List<ActiveService> activeServices = quoteProcess
				.retrieveLineItemsFromOrderNoTx(caNum, configId);

		if (activeServices == null || activeServices.size() < 1) {
			return activeServicMap;
		}
		
		for (ActiveService as : activeServices ){
			if (as.isActiveFlag() && as.isSbscrptnFlag() && !as.isRampupPart()){
				activeServicMap.put(as.getPartNumber(),as);
			}
		}
		return activeServicMap;
	}
   
   
   private static QuoteLineItem getLineItemByApplianceId(String applianceId, Quote quote){    	
   	for(Object o : quote.getApplncParts()) {
			QuoteLineItem item=(QuoteLineItem)o;
			if(StringUtils.isNotBlank(item.getApplianceId())
					&& item.getApplianceId().equals(applianceId)
					&& item.getPartNum().equals(applianceId.split("-")[0])) {
				return item;
			}
		}
		return null;
	}
   
   public static boolean isChannelOverrideDiscount(Quote quote){
	   List items = quote.getLineItemList();
	   if (items==null) {
		   return false;
	   }
	   for (int i = 0; i < items.size(); i++) {
		   QuoteLineItem item = (QuoteLineItem) items.get(i);
		   if((item.getChnlOvrrdDiscPct() != null) && (item.getChnlOvrrdDiscPct().doubleValue()>0)){
			   return true;  	   
           }
	   }
	   return false;
   }

   /**
    * validate the proof of concept for appliance information indicator
    * 
    * @param quote
    * @return
    */
   public static Boolean validateApplncPoc(Quote quote)
   {
       Boolean isCompleted = true;
       PartPriceCommon common = new PartPriceCommon(quote);
       List applianceParts = quote.getApplncParts();
       if (applianceParts != null)
       {
           for (int i = 0; i < applianceParts.size(); i++)
           {
               QuoteLineItem qli = (QuoteLineItem) applianceParts.get(i);
               if (null == qli || qli.isApplncUpgrade() || qli.isOwerTransferPart())
               {
                   continue;
               }
               else if (!common.showApplianceInformation(qli, quote.getQuoteHeader().isSubmittedQuote(), quote.getQuoteHeader().isSalesQuote()))
               {
                   continue;
               }
               else
               {
                   if (StringUtils.isBlank(qli.getApplncPocInd()))
                   {
                       isCompleted = false;
                       break;
                   }
               }
           }
       }
       return isCompleted;
   }
}
