package com.ibm.dsw.quote.common.util;

import is.domainx.User;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.domain.CountryFactory;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartDisplayAttr;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAccess;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.draftquote.util.BidIterationMonthlyRuleHelper;
import com.ibm.dsw.quote.draftquote.util.BidIterationRuleHelper;
import com.ibm.dsw.quote.draftquote.util.BidIterationSaasRuleHelper;
import com.ibm.dsw.quote.draftquote.util.StreamlinedRuleHelper;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.util.date.PartTypeChecker;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartPriceCommon;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteBusinessRule<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-5-25
 */

public class CommonServiceUtil {
    
    private static final String CURRENT_PACKAGE_CLASS_NAME="com.ibm.dsw.quote.common.util.CommonServiceUtil";

    private static final String SAP_DATETIME_FORMAT = "yyyyMMddHHmmss";

    protected static final LogContext logger = LogContextFactory.singleton().getLogContext();
    
    //using singleton pattern for debug method object DI 
    private static CommonServiceUtil sngltn = null;

    public static CommonServiceUtil singleton() {
		if (CommonServiceUtil.sngltn == null) {
			sngltn = new CommonServiceUtil();
		}
		return sngltn;
	}
	
    /**
     *  
     */
    public CommonServiceUtil() {
    }

    public static String replaceAmps(String source) {
        String dest = source.replaceAll("&amp;", "&");
        return dest;
    }

    public static String replaceLineSeperator(String source) {
        if (source == null)
            return "";
        String dest = source.replaceAll("\r", " ");
        dest = dest.replaceAll("\n", "");
        return dest;
    }

    public static String getModDate(Date modDate, Date statModDate) {

        if (modDate == null && statModDate == null)
            return DateHelper.getDateByFormat(null, SAP_DATETIME_FORMAT);
        else if (modDate == null)
            return DateHelper.getDateByFormat(statModDate, SAP_DATETIME_FORMAT);
        else if (statModDate == null)
            return DateHelper.getDateByFormat(modDate, SAP_DATETIME_FORMAT);
        else {
            Date qtModDate = modDate.compareTo(statModDate) < 0 ? statModDate : modDate;
            return DateHelper.getDateByFormat(qtModDate, SAP_DATETIME_FORMAT);
        }

    }

    public static String getChangeType(QuoteLineItem lineItem) {

        String sapChgType = "";
        String webChgType = lineItem.getChgType();

        if (StringUtils.isBlank(webChgType))
            sapChgType = PartPriceConstants.PartChangeType.SAP_PART_NO_CHANGES;
        else if (PartPriceConstants.PartChangeType.PART_NO_CHANGES.equals(webChgType))
            sapChgType = PartPriceConstants.PartChangeType.SAP_PART_NO_CHANGES;
        else if (PartPriceConstants.PartChangeType.PART_ADDED.equals(webChgType))
            sapChgType = PartPriceConstants.PartChangeType.SAP_PART_ADDED;
        else if (PartPriceConstants.PartChangeType.PART_DELETED.equals(webChgType))
            sapChgType = PartPriceConstants.PartChangeType.SAP_PART_DELETED;
        else
            sapChgType = PartPriceConstants.PartChangeType.SAP_PART_UPDATED;
        
        
       
        logger.debug(singleton(),CURRENT_PACKAGE_CLASS_NAME + " - Quote line item change type: " + webChgType);
   	 	logger.debug(singleton(),CURRENT_PACKAGE_CLASS_NAME + " - SAP change type: " + sapChgType);
       

        return sapChgType;
    }

    public static boolean isValidDiscoutPct(double d) {
        return (0.0 < d && d <= 100.0);
    }

    /**
     * @param lineItems : Note, the logic below assumes master line item 
     *                    has small index(position in the list) than it's additional 
     *                    maintenance coverage items in this list.
     * @return master line items
     */
    public static List buildMasterLineItemsWithAddtnlMaint(List lineItems, boolean forPost) {
        
        
        List result = new ArrayList();
        if (lineItems == null || lineItems.size() == 0) {
            return result;
        }
        // first, put all items in a map with seq num as its key;
        Map itemsMap = new HashMap();
        // then, marked all items as not removed
        Map itemRemovedMap = new HashMap();
        for (Iterator itemIt = lineItems.iterator(); itemIt.hasNext();) {
            QuoteLineItem item = (QuoteLineItem) itemIt.next();
            // exclude the SaaS part
            if(item.isSaasPart() || item.isMonthlySoftwarePart()){
            	continue;
            }
            // Andy : we have to clear this, because user may call this method more than once
            item.getAddtnlYearCvrageLineItems().clear();
            Integer key = new Integer(item.getSeqNum());
            itemsMap.put(key, item);
            itemRemovedMap.put(key, Boolean.FALSE);
        }
        // iterate through each line item, if with addtional maint items, remove
        // them from master and put all into master's additioanl maint list.
        
        // Andy : Note that lineItem list are already ordered by seq number, the master line item always has a lower seq number , so it will
        // be firstly processed and it's sub line items will be marked as "removed"  
        for (Iterator itemIt = lineItems.iterator(); itemIt.hasNext();) {
            QuoteLineItem currItem = (QuoteLineItem) itemIt.next();
          // exclude the SaaS part
            if(currItem.isSaasPart() || currItem.isMonthlySoftwarePart()){
            	continue;
            }
            //if not removed - that means it's master, put into result
            boolean removed = ((Boolean) itemRemovedMap.get(new Integer(currItem.getSeqNum()))).booleanValue();
            if (!removed) {
                result.add(currItem);
            }
            if (currItem.getAddtnlMaintCvrageQty() > 0) {
                // master linked with all addtional maint with its addtnlSeqNum
                int nextAddtnlSeqNum = currItem.getAddtnlYearCvrageSeqNum();
                while (nextAddtnlSeqNum > 0) {
                    QuoteLineItem nextItem = (QuoteLineItem) itemsMap.get(new Integer(nextAddtnlSeqNum));
                    if (nextItem == null || nextAddtnlSeqNum == currItem.getSeqNum() ){
                        break;
                    }
                    currItem.getAddtnlYearCvrageLineItems().add(nextItem);
                    nextItem.setParentMasterPart(currItem);
                    // set removed to true
                    itemRemovedMap.put(new Integer(nextItem.getSeqNum()), Boolean.TRUE);
                    nextAddtnlSeqNum = nextItem.getAddtnlYearCvrageSeqNum();
                }
            }
        }
        //If this method is called within post action, the eligible for cmprss cvrage flag will be set explicitly
        if(!forPost){
            for(Iterator it = result.iterator(); it.hasNext(); ){
                QuoteLineItem qli = (QuoteLineItem)it.next();
                
                qli.determineEligibleForCmprssCvrage();
            }
        }
        
        
        return result;
    }
    
    public static List buildMasterLineItemsWithAddtnlMaint(List lineItems) {
        return buildMasterLineItemsWithAddtnlMaint(lineItems, false);
    }
    

    /*public static String getQuoteCountryCurrency(QuoteHeader header) {

        String defaultCurrencyCode = "";

        List defaultCurrencies = header.getPriceCountry().getCurrencyList();

        if ((defaultCurrencies != null) && (defaultCurrencies.size() > 0)) {
            CodeDescObj countryCurrObj = (CodeDescObj) defaultCurrencies.get(0);
            defaultCurrencyCode = countryCurrObj.getCode();
        }

        logger.debug(singleton(), CURRENT_PACKAGE_CLASS_NAME + " - defaultCurrencyCode:" + defaultCurrencyCode);
        if (defaultCurrencyCode != null) {
            defaultCurrencyCode.trim();
        }
        return defaultCurrencyCode;
    }*/

    public static String limitLength(String inStr, int maxLength) {
        String outStr = StringUtils.trimToEmpty(inStr);

        if (outStr.length() > maxLength)
            outStr = outStr.substring(0, maxLength);

        return outStr;
    }
    
    public static int getEndCustomerRoundingFactor(Quote quote){
        Country country = quote.getQuoteHeader().getPriceCountry();
        int roundingFactor = DecimalUtil.DEFAULT_SCALE;
        /* Andy : 2008-2-3: we don't need to get country from Customer objct, 
         * because after the ebiz ticket "BJJK-7B4LN4  allow cross-border for FCT" 
         * the PriceCountry always reflect the end customer's country or payer's country (FCT)
         * 
         * Customer customer = quote.getCustomer();
        if (customer != null) {
            String customerCountryCode = customer.getCountryCode();
            country = CountryFactory.singleton().findByCode3(customerCountryCode);
        }*/
        if (country != null) {
            roundingFactor = country.endCustPrckRoundingFactor();
        }
        return roundingFactor;
    }
    public static String getBestPricingBandLevel(Quote quote){
        // if the customer has contract, compare volDiscLevel  of contract with volDiscLevel in quote
        // if different , return contract.volDiscLevel. otherwise return null;
        if ((quote.getCustomer() != null) && (quote.getCustomer().getContractList() != null)
                && (quote.getCustomer().getContractList().size() > 0)) {
             
            Contract contract = (Contract) quote.getCustomer().getContractList().get(0);
            String customerPriceBandLevel = contract.getVolDiscLevelCode();
            
            String quotePriceBandLevel = quote.getQuoteHeader().getVolDiscLevelCode();
                
            logger.debug(singleton(), CURRENT_PACKAGE_CLASS_NAME + " - Customer Price Band Level = " + customerPriceBandLevel );
            logger.debug(singleton(), CURRENT_PACKAGE_CLASS_NAME + " - Quote Price Band Level = "    + quotePriceBandLevel );
            
            if((customerPriceBandLevel!=null) 
                    && (quotePriceBandLevel !=null)
                    && (!customerPriceBandLevel.equals(quotePriceBandLevel))){    
                logger.debug(singleton(), CURRENT_PACKAGE_CLASS_NAME + " - Price Level changed ,use customer Price Levle ="    + customerPriceBandLevel );
                return StringUtils.trimToEmpty(customerPriceBandLevel);                          
                                
            }
        }
        return null;
    }
    
    public static void setPriceChangeCode(Quote quote) throws TopazException{
        if(quote.getQuoteHeader().isSalesQuote()){
            return;
        }
        
        for(Iterator iter = quote.getLineItemList().iterator(); iter.hasNext();){
            QuoteLineItem qli = (QuoteLineItem) iter.next();
            if (qli.isObsoletePart() || PartPriceConstants.PartChangeType.SAP_PART_DELETED.equals(qli.getChgType())
                    || PartPriceConstants.PartChangeType.SAP_PART_ADDED.equals(qli.getChgType()))
                continue;

            if (!StringUtils.contains(qli.getChgType(), PartPriceConstants.PartChangeType.PART_PRICE_UPDATED)){
                final String PU = '_' + PartPriceConstants.PartChangeType.PART_PRICE_UPDATED;
                if (StringUtils.isBlank(qli.getChgType())){
                    qli.setChgType(PartPriceConstants.PartChangeType.PART_NO_CHANGES + PU);
                }else{
                    qli.setChgType(qli.getChgType() + PU);
                }
            }
        
        }
        
    }
    public static boolean validatePartPrice(Quote quote) {
        
        QuoteHeader header = quote.getQuoteHeader();
        QuoteAccess quoteAccess = quote.getQuoteAccess();
        
        if( (header.isRenewalQuote()&&quoteAccess.isCanEditRQ()) || header.isSalesQuote()  ){
            
            List lineItemList = quote.getLineItemList();
            Iterator iterator = lineItemList.iterator();
            
            while (iterator.hasNext()) {
                QuoteLineItem item = (QuoteLineItem) iterator.next();
                
                if (header.isRenewalQuote()){
                    if (!item.isObsoletePart() 
                            && (item.getPartQty() != null && item.getPartQty().intValue()!= 0 && (item.getLocalUnitProratedPrc() == null)) ) {
		                return false;
		            }
                }
		        if (header.isSalesQuote()){
		            if (item.getLocalUnitProratedPrc() == null) {
                        return false;
                    }
		        }
            }        
        }
        return true;
    }
    
    public static String getSalesOrgCode(String cntryCode3, String sLOB) throws TopazException {

        String result = null;
        Country country = CountryFactory.singleton().findByCode3(cntryCode3);
        List salesOrgs = country.getSalesOrgList();
        Iterator iterator = salesOrgs.iterator();

        while (iterator.hasNext()) {
            CodeDescObj salesOrg = (CodeDescObj) iterator.next();
            if (sLOB.equals(QuoteConstants.LOB_PPSS)) {
                if (salesOrg.getCode() != null && salesOrg.getCode().equals(QuoteConstants.LANDED_SALES_MODE_CODE)) {
                    result = salesOrg.getCodeDesc();
                    break;
                } else if (salesOrg.getCode() == null || "".equals(salesOrg.getCode())) {
                    result = salesOrg.getCodeDesc();
                }
            } else {
                if (salesOrg.getCode() == null || "".equals(salesOrg.getCode())) {
                    result = salesOrg.getCodeDesc();
                    break;
                }
            }
        }
        return result;
    }
    
    public static boolean isSpecialBidRnwlPart(QuoteHeader header, QuoteLineItem item){
    	
    	if(!header.isSpBiddableRQ()){
    		return false;
    	}
    	
    	return (StringUtils.isNotEmpty(header.getRenwlQuoteNum())
    			&& StringUtils.isNotEmpty(item.getRenewalQuoteNum())
				&& header.getRenwlQuoteNum().equals(item.getRenewalQuoteNum()));
    }
    
    //parts added to sales quote from internal reporting
    //not by create special bid against renewal quote or edit master renewal quote
    public static boolean isSalesQuoteOtherRnwlPart(QuoteHeader header, QuoteLineItem item){
    	
    	if(header.isSalesQuote() && item.getPartDispAttr().isFromRQ()
    			&& !isSpecialBidRnwlPart(header, item)){
    		return true;
    	}
    	
    	return false;
    }
    
    public static boolean isRnwlQuoteOriginalRQPart(QuoteHeader header, QuoteLineItem item){
    	if(header.isRenewalQuote() && item.getSeqNum() < PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ){
    		return true;
    	}
    	
    	return false;
    }
    
    public static boolean isRnwlQuotePartSearchPart(QuoteHeader header, QuoteLineItem item){
    	if(header.isRenewalQuote() && item.getSeqNum() >= PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ){
    		return true;
    	}
    	
    	return false;
    }
    
    public static final String getPartType(QuoteHeader header, QuoteLineItem item){
    	if(header.isSalesQuote()){
	    	if(StringUtils.isEmpty(item.getRenewalQuoteNum())){
	    		return PartPriceConstants.PartType.SQ_PS_PART;
	    		
	    	} else if(isSpecialBidRnwlPart(header, item)){
	    		return PartPriceConstants.PartType.SQ_SB_RQ_PART;
	    		
	    	} else {
	    		return PartPriceConstants.PartType.SQ_OTHER_RQ_PART;
	    	}
    	} else {
    		if(item.getSeqNum() >= PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ){
    			return PartPriceConstants.PartType.RQ_PS_PART;
    			
    		} else {
    			return PartPriceConstants.PartType.RQ_ORIG_RQ_PART;
    		}
    	}
    }
    
    //entitledPrice if either from EOL price table or entered by sales rep
    public static void setEntitledPriceForEOLPart(QuoteLineItem item, Double localUnitPrc, Double localUnitProratedPrc, int ovrdFlag) throws TopazException{
    	item.setManualProratedLclUnitPriceFlag(ovrdFlag);
    	
    	item.setLocalUnitPrc(localUnitPrc);
    	item.setLocalUnitProratedPrc(localUnitProratedPrc);

    	Integer qty = item.getPartQty();
    	if(qty != null && localUnitProratedPrc != null){
    		item.setLocalExtProratedPrc(new Double(localUnitProratedPrc.doubleValue() * qty.intValue()));
    	} else {
    		item.setLocalExtProratedPrc(null);
    	}
    	
    	List subList = item.getAddtnlYearCvrageLineItems();
    	if(subList == null || subList.size() == 0){
    		return;
    	}
    	
    	for(Iterator it = subList.iterator(); it.hasNext(); ){
    		QuoteLineItem subItem = (QuoteLineItem)it.next();
    		
    		setEntitledPriceForEOLPart(subItem, localUnitPrc, localUnitPrc, ovrdFlag);
    	}    	
    }
    
    public static String getValidCtrctNum(Quote quote) {
        if (quote == null || quote.getQuoteHeader() == null || quote.getCustomer() == null)
            return "";
        
        QuoteHeader header = quote.getQuoteHeader();
        Customer cust = quote.getCustomer();
        
        if (header.isPAQuote()) { // For PA quote
            if (cust.isACACustomer() || cust.isGOVCustomer() || cust.isXSPCustomer()) {
                // For ACA/GOV/XSP customer, return the newly created contract
                // which has same svp level with quote's svp level
            	
                String svpLevel = cust.getTransSVPLevel() == null ? "" : cust.getTransSVPLevel();
                List enrolledCtrctList = cust.getEnrolledCtrctList();
                
                for (int i = 0; enrolledCtrctList != null && i < enrolledCtrctList.size(); i++) {
                    Contract ctrct = (Contract) enrolledCtrctList.get(i);
                    
                    if (svpLevel.equalsIgnoreCase(ctrct.getTranPriceLevelCode())
                            || svpLevel.equalsIgnoreCase(ctrct.getVolDiscLevelCode()))
                        return ctrct.getSapContractNum();
                }
            }
            else if (cust.isAddiSiteCustomer()) {
                // For additional site customer, return the newly created contract
                // which has same contract number with quote's contract number
                List enrolledCtrctList = cust.getEnrolledCtrctList();
                String ctrctNum = header.getContractNum();
                
                for (int i = 0; enrolledCtrctList != null && i < enrolledCtrctList.size(); i++) {
                    Contract ctrct = (Contract) enrolledCtrctList.get(i);
                    
                    if (ctrctNum.equalsIgnoreCase(ctrct.getSapContractNum()))
                        return ctrct.getSapContractNum();
                }
            }
            else if (cust.isSTDCustomer()) {
                // For STD/STD Less customer, return the newly created contract.
                List enrolledCtrctList = cust.getEnrolledCtrctList();
                
                if (enrolledCtrctList != null && enrolledCtrctList.size() > 0) {
                    Contract ctrct = (Contract) enrolledCtrctList.get(0);
                    return ctrct.getSapContractNum();
                }
            }
            else if(QuoteConstants.LOB_CSRA.equals(header.getAgrmtTypeCode())){
            	String contractNum = header.getContractNum();
            	if(null == contractNum || "".equals(contractNum)){
            		List enrolledCtrctList = cust.getEnrolledCtrctList();
                    if (enrolledCtrctList != null && enrolledCtrctList.size() > 0) {
                         Contract ctrct = (Contract) enrolledCtrctList.get(0);
                         contractNum = ctrct.getSapContractNum();
                     }
            	}
            	return contractNum;
            }
            else {
                // For existing PA customer without new contract, return quote's contract number
                return header.getContractNum();
            }
        }
        else if (header.isFCTQuote()) { // For FCT quote
            // For FCT quote, return quote's contract number (may be empty).
            
            return header.getContractNum();
        }else if(header.isSSPQuote()){
        	
        	List enrolledCtrctList = cust.getEnrolledCtrctList();
             
            if (enrolledCtrctList != null && enrolledCtrctList.size() > 0) {
                 Contract ctrct = (Contract) enrolledCtrctList.get(0);
                 return ctrct.getSapContractNum();
             }
        }
        
        return "";
    }
    
    public static boolean needSendAddiMailToCreator(QuoteHeader header) {
        if (header == null)
            return false;
        
        if (header.isSalesQuote()) {
            return (header.isFCTQuote() || header.isMigration() || header.isPAQuote() || header.isPAEQuote() || header.isSSPQuote());
        }
        else {
            return (header.isFCTQuote() || header.isMigration());
        }
    }
    
    public static String genAddiEmailList(QuoteHeader header, User user, QuoteUserSession salesRep) {
        String addiCntEmail = StringUtils.trim(header.getAddtnlCntEmailAdr());
        String userEmail = user == null ? "" : user.getEmail();
        String salesRepEmail = salesRep == null ? "" : salesRep.getEmailAddress();
        String creatorEmail = StringUtils.isBlank(salesRepEmail) ? userEmail : salesRepEmail;
        
        creatorEmail = StringUtils.trim(creatorEmail);
        
        if (StringHelper.containsIgnoreCase(addiCntEmail, creatorEmail))
            return addiCntEmail;
        
        if (StringUtils.isNotBlank(addiCntEmail)) {
            if (addiCntEmail.endsWith(";"))
                return addiCntEmail + creatorEmail;
            else
                return addiCntEmail + ";" + creatorEmail;
        }
        else 
            return creatorEmail;
    }
    
    public static boolean quoteHasRQLineItems(Quote quote) {
        if (quote == null || quote.getLineItemList() == null)
            return false;
        
        List lineItems = quote.getLineItemList();
        for (int i= 0; i < lineItems.size(); i++) {
            QuoteLineItem lineItem = (QuoteLineItem)lineItems.get(i);
            
            if (StringUtils.isNotBlank(lineItem.getRenewalQuoteNum()))
                return true;
        }
        
        return false;
    }
    
    public static Double getProrateEolPrice(Quote quote, QuoteLineItem item) throws TopazException{
    	Double entitledUnitPrice = null;
    	    
        //Only calculate prorated entitled price for line items with maintenance dates
    	if(PartPriceConfigFactory.singleton().needDetermineDate(item.getRevnStrmCode())){
	    	if (item.getPartDispAttr().isFtlPart()) {
	            int coverate_weeks = DateUtil.calculateWeeks(item.getMaintStartDate(), item.getMaintEndDate());
	            
	            logger.debug(singleton(), "FTL parts, proration weeks: " + coverate_weeks + " (" + item.getPartNum() + "," + item.getSeqNum() + ")");
	            
	            entitledUnitPrice = new Double(item.getLocalUnitPrc() == null ? 0 : item.getLocalUnitPrc().doubleValue()
	                        / PartPriceConstants.FULL_YEAR_WEEK * coverate_weeks);
	        } else {
	            int coverage_months = DateUtil.calculateFullCalendarMonths(item.getMaintStartDate(), item.getMaintEndDate());
	            logger.debug(singleton(), "non-FTL parts, proration months:" + coverage_months + " (" + item.getPartNum() + "," + item.getSeqNum() + ")");
	            entitledUnitPrice = new Double(item.getLocalUnitPrc() == null ? 0 : item.getLocalUnitPrc().doubleValue()
	                        / PartPriceConstants.FULL_YEAR_MONTH * coverage_months);
	        }
    	} else {
    		entitledUnitPrice = new Double(item.getLocalUnitPrc() == null ? 0 : item.getLocalUnitPrc().doubleValue());
    	}
        
        PartPriceCommon partPrice = new PartPriceCommon(quote);
        try {
            NumberFormat format = NumberFormat.getInstance();
            entitledUnitPrice = new Double(format.parse(partPrice.getFormattedPriceByPartType(item, entitledUnitPrice)).doubleValue());
        } catch (ParseException e) {
            throw new TopazException(e);
        }
        
        return entitledUnitPrice;
    }
    
    public static Double getCmprssCvrageEntitlUnitPrcEol(Quote quote, QuoteLineItem item, int cmprssCvrageMonth) throws TopazException{
        Double entitled_unit_price = null;
        
        entitled_unit_price = new Double(item.getLocalUnitPrc() == null ? 0 : item.getLocalUnitPrc().doubleValue()
                / PartPriceConstants.FULL_YEAR_MONTH * cmprssCvrageMonth);
        PartPriceCommon partPrice = new PartPriceCommon(quote);
        try {
            NumberFormat format = NumberFormat.getInstance();
            entitled_unit_price = new Double(format.parse(partPrice.getFormattedPrice(entitled_unit_price)).doubleValue());
        } catch (ParseException e) {
            throw new TopazException(e);
        }
        return entitled_unit_price;
    }
    
    public static void setCmprssCvrageItemEol(Quote quote, QuoteLineItem item, Double dEntldUnitPrice, Double dOvrridPrice, double cmprssCvrageDisc) throws TopazException{
       item.setLocalUnitProratedPrc(dEntldUnitPrice);
       item.setOverrideUnitPrc(dOvrridPrice);
       item.setLocalUnitProratedDiscPrc(dOvrridPrice == null ? dEntldUnitPrice : dOvrridPrice);
       item.setLineDiscPct(cmprssCvrageDisc);
       if(dEntldUnitPrice != null && item.getPartQty() != null){
           item.setLocalExtProratedPrc(new Double(dEntldUnitPrice.doubleValue() * item.getPartQty().intValue()));
       }
       if(dOvrridPrice != null && item.getPartQty() != null){
	       item.setLocalExtProratedDiscPrc(new Double(dOvrridPrice.doubleValue() * item.getPartQty().intValue()));
	       item.setLocalExtPrc(new Double(dOvrridPrice.doubleValue() * item.getPartQty().intValue()));
       }
       if(PartPriceConfigFactory.singleton().allowChannelMarginDiscount(quote)){
           item.setChannelUnitPrice(item.getLocalUnitProratedDiscPrc());
           Double bpDiscount = item.getChnlStdDiscPct();
           Double bpOvrrdDiscount = item.getChnlOvrrdDiscPct();
           if(bpOvrrdDiscount != null){
               item.setChannelExtndPrice(new Double(item.getLocalExtProratedDiscPrc().doubleValue() * (1 - bpOvrrdDiscount.doubleValue() / 100)));
           }else if(bpDiscount != null){
               item.setChannelExtndPrice(new Double(item.getLocalExtProratedDiscPrc().doubleValue() * (1 - bpDiscount.doubleValue() / 100)));
           }else{
               item.setChannelExtndPrice(item.getLocalExtProratedDiscPrc());
           }
       }
    }
    
    public static void matchOfferPriceFailed(Quote quote) throws TopazException{
        Double lastOfferPrice = quote.getQuoteHeader().getOfferPrice();
        double newTotalBidExtPrice = quote.getQuoteHeader().getQuotePriceTot();
        try {
            if(lastOfferPrice == null){
                return;
            }
            PartPriceCommon partPrice = new PartPriceCommon(quote);
            NumberFormat format = NumberFormat.getInstance();
            Double newTotalBidExtPriceDouble = new Double(format.parse(partPrice.getFormattedPrice(newTotalBidExtPrice)).doubleValue());
            if(lastOfferPrice != null 
                    && newTotalBidExtPriceDouble != null
                    && lastOfferPrice.equals(newTotalBidExtPriceDouble)){
                quote.getQuoteHeader().setMatchOfferPriceFailed(false);
            }
            else{
                quote.getQuoteHeader().setMatchOfferPriceFailed(true);
            }
        } catch (ParseException e) {
            throw new TopazException(e);
        }
        
    }
    
    public static int getValidityDays(QuoteHeader quoteHeader) {
        int validityDays = 0;
        
        if (quoteHeader == null){
            return validityDays;
        }
        Date quoteExpDate = quoteHeader.getQuoteExpDate();
        
        if (quoteExpDate == null){
            return validityDays;
        }
        Date now = com.ibm.ead4j.common.util.DateHelper.singleton().today();
        Date currDate = DateUtils.truncate(now, Calendar.DATE);
        Date submittedDate = quoteHeader.getSubmittedDate();
        if (quoteHeader.isSubmittedQuote()) {
            if (submittedDate != null) {
                Date submtDate = DateUtils.truncate(submittedDate, Calendar.DATE);
                validityDays = com.ibm.ead4j.common.util.DateHelper.singleton().daysDifference(submtDate, quoteExpDate);
            }
        } else {
            validityDays = com.ibm.ead4j.common.util.DateHelper.singleton().daysDifference(currDate, quoteExpDate);
        }
        return validityDays;

    }
    
    public static boolean softwareIsValid4BidItrtn(Quote quote) throws TopazException {
        if (quote == null || quote.getSoftwareLineItems() == null || quote.getSoftwareLineItems().size() == 0) {
            return true;
        }
        if (!(QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode()) && quote
                .getQuoteHeader().isBidIteratnQt())) {
            return false;
        }
        
        try {
            QuoteHeader newQuoteHeader = quote.getQuoteHeader();
            PartPriceCommon partPrice = new PartPriceCommon(quote);
            NumberFormat format = NumberFormat.getInstance();
            double newPriceTot = format.parse(partPrice.getFormattedPrice(newQuoteHeader.getQuotePriceTot()))
                    .doubleValue();
            double originalPriceTot = format.parse(partPrice.getFormattedPrice(newQuoteHeader.getOriQuotePriceTot()))
                    .doubleValue();
            double absDifferencePriceTot = Math.abs(newPriceTot - originalPriceTot);
            double absDifferenceDiscount = absDifferencePriceTot / originalPriceTot * 100;
            PartPriceConfigFactory.BidIterationConfig bidIterationConfig = PartPriceConfigFactory.singleton()
                    .getBidIterationConfig();
            //Difference in Total Contract Value (TCV = Total Bid Price) must
            // be
            // +/-W percent (ie. 10%) when TCV is less than or equal to Y (ie. 5
            // Mil USD) where
            // W and Y values are configurable.
            if (originalPriceTot <= bidIterationConfig.getLessThanPrice()) {
                if (absDifferenceDiscount > bidIterationConfig.getLessThanPercent()) {
                    logger.info(null,
                            "quoteIsValid4BidItrtn return false. Difference in Total Bid Price is larger than "
                                    + bidIterationConfig.getLessThanPercent() + "% when Total Bid Price is less than or equal to "
                                    + bidIterationConfig.getLessThanPrice() + ".\n " + " newPriceTot is " + newPriceTot
                                    + "\n" + " originalPriceTot is " + originalPriceTot + "\n"
                                    + " absDifferencePriceTot is " + absDifferencePriceTot + "\n"
                                    + " absDifferenceDiscount is " + absDifferenceDiscount);
                    return false;
                }
            }
            //Difference in Total Contract Value (TCV = Total Bid Price) must
            // be
            // +/-X percent (ie. 5%) when TCV is greater than Z (ie. 5 Mil USD)
            // where X and Z values are configurable.
            if (originalPriceTot > bidIterationConfig.getGreaterThanPrice()) {
                if (absDifferenceDiscount > bidIterationConfig.getGreaterThanPercent()) {
                    logger.info(null,
                            "quoteIsValid4BidItrtn return false. Difference in Total Bid Price is larger than "
                                    + bidIterationConfig.getGreaterThanPercent()
                                    + "% when Total Bid Price is greater than "
                                    + bidIterationConfig.getGreaterThanPrice() + ".\n " + " newPriceTot is "
                                    + newPriceTot + "\n" + " originalPriceTot is " + originalPriceTot + "\n"
                                    + " absDifferencePriceTot is " + absDifferencePriceTot + "\n"
                                    + " absDifferenceDiscount is " + absDifferenceDiscount);
                    return false;
                }
            }

            List newLineItemsList = quote.getSoftwareLineItems();
            Map originalLineItemsMap = getSoftwareLineItemMap(CommonServiceUtil.getSoftwareLineItemList(QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(
                    newQuoteHeader.getPriorQuoteNum())));
            for (int i = 0; i < newLineItemsList.size(); i++) {
                QuoteLineItem newLineItem = (QuoteLineItem) newLineItemsList.get(i);
                QuoteLineItem originalLineItem = (QuoteLineItem) originalLineItemsMap
                        .get(createSoftwareLineItemKey(newLineItem));
                double newLineDiscPct = Double.valueOf(DecimalUtil.formatTo5Number(newLineItem.getLineDiscPct()))
                        .doubleValue();
                double originalLineDiscPct = Double.valueOf(
                        DecimalUtil.formatTo5Number(originalLineItem.getLineDiscPct())).doubleValue();
                double absDiffLineItemOvrrDis = Math.abs(newLineDiscPct - originalLineDiscPct);
                //The discount change on any one line item can not exceed V
                // (2.000)
                // % where V is configurable.
                if (absDiffLineItemOvrrDis > bidIterationConfig.getLineItemDisChangeExceed()) {
                    logger.info(null, "quoteIsValid4BidItrtn return false. The discount change on part "
                            + newLineItem.getPartNum() + " exceed " + bidIterationConfig.getLineItemDisChangeExceed());
                    return false;
                }
                //Increase of discount is only allowed on non S & S parts
                if (!(newQuoteHeader.getOfferPrice() != null && newQuoteHeader.getOfferPrice().doubleValue() > 0)
                        && bidIterationConfig.isIncrDisNotAllowedRevStrm(newLineItem.getRevnStrmCode())) {
                    if (newLineDiscPct > originalLineDiscPct) {
                        logger.info(null,
                                "quoteIsValid4BidItrtn return false. Increase of discount is not allowed on "
                                        + bidIterationConfig.getIncrDisNotAllowedRevStrm().toString() + " parts."
                                        + " \n part number is " + newLineItem.getPartNum());
                        return false;
                    }
                }
            }
        } catch (ParseException e) {
            throw new TopazException(e);
        }

        logger.debug(singleton(), "quoteIsValid4BidItrtn return true.");
        return true;
    }
    
    public static String createSoftwareLineItemKey(QuoteLineItem item) {
        return item.getPartNum().trim() + "_" + item.getSeqNum();
    }
    
    public static Map getSoftwareLineItemMap(List lineItemList){
        Map lineItemMap = new HashMap();
        for (int i = 0; i < lineItemList.size(); i++) {
            QuoteLineItem lineItem = (QuoteLineItem) lineItemList.get(i);
            lineItemMap.put(createSoftwareLineItemKey(lineItem), lineItem);
        }
        return lineItemMap;
    }
    
    public static String createSaasLineItemKey(QuoteLineItem item) {
        return item.getPartNum().trim() + "_" + item.getRampUpPeriodNum();
    }
    
    public static Map getSaasLineItemMap(List lineItemList){
        Map lineItemMap = new HashMap();
        for (int i = 0; i < lineItemList.size(); i++) {
            QuoteLineItem lineItem = (QuoteLineItem) lineItemList.get(i);
            lineItemMap.put(createSaasLineItemKey(lineItem), lineItem);
        }
        return lineItemMap;
    }
    
    public static boolean softwareNotChanged4BidItrtn(Quote quote) throws TopazException {
        if (quote == null || quote.getSoftwareLineItems() == null){
            return true;
        }
        if (!(QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode()))) {
            return false;
        }
        QuoteHeader newQuoteHeader = quote.getQuoteHeader();
        QuoteHeader originalQuoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNum(newQuoteHeader.getPriorQuoteNum());
        if(newQuoteHeader.getQuotePriceTot() != originalQuoteHeader.getQuotePriceTot()
           || (newQuoteHeader.getOfferPrice() != null &&  originalQuoteHeader.getOfferPrice() != null && newQuoteHeader.getOfferPrice().doubleValue() != originalQuoteHeader.getOfferPrice().doubleValue())
           ){
            return false;
        }
        List newLineItemsList = quote.getSoftwareLineItems();
        Map originalLineItemsMap = getSoftwareLineItemMap(CommonServiceUtil.getSoftwareLineItemList(QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(newQuoteHeader.getPriorQuoteNum())));
        if(newLineItemsList.size() != originalLineItemsMap.size()){
            return false;
        }
        for (int i = 0; i < newLineItemsList.size(); i++) {
            QuoteLineItem newLineItem = (QuoteLineItem) newLineItemsList.get(i);
            QuoteLineItem originalLineItem = (QuoteLineItem) originalLineItemsMap.get(createSoftwareLineItemKey(newLineItem));
            if(isLineItemChanged4BidItrtn(newLineItem, originalLineItem)){
            	return false;
            }
        }
        logger.info(null,"softwareNotChanged4BidItrtn return true.");
        return true;
    }
    
    public static boolean saasNotChanged4BidItrtn(Quote quote) throws TopazException {
        if (quote == null || quote.getSaaSLineItems() == null){
            return true;
        }
        if (!(QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode()))) {
            return false;
        }
        QuoteHeader newQuoteHeader = quote.getQuoteHeader();
        QuoteHeader originalQuoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNum(newQuoteHeader.getPriorQuoteNum());
        if(newQuoteHeader.getQuotePriceTot() != originalQuoteHeader.getQuotePriceTot()
           || (newQuoteHeader.getOfferPrice() != null &&  originalQuoteHeader.getOfferPrice() != null && newQuoteHeader.getOfferPrice().doubleValue() != originalQuoteHeader.getOfferPrice().doubleValue())
           ){
            return false;
        }
        if(!DateUtil.isYMDEqual(newQuoteHeader.getQuoteExpDate(), originalQuoteHeader.getQuoteExpDate())){
        	return false;
        }
        List newLineItemsList = quote.getSaaSLineItems();
        Map originalLineItemsMap = getSaasLineItemMap(CommonServiceUtil.getSaaSLineItemList(QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(newQuoteHeader.getPriorQuoteNum())));
        if(newLineItemsList.size() != originalLineItemsMap.size()){
            return false;
        }
        for (int i = 0; i < newLineItemsList.size(); i++) {
            QuoteLineItem newLineItem = (QuoteLineItem) newLineItemsList.get(i);
            QuoteLineItem originalLineItem = (QuoteLineItem) originalLineItemsMap.get(createSaasLineItemKey(newLineItem));
            if(isLineItemChanged4BidItrtn(newLineItem, originalLineItem)){
            	return false;
            }
        }
        logger.info(null,"saasNotChanged4BidItrtn return true.");
        return true;
    }
    
    public static boolean monthlyNotChanged4BidItrtn(Quote quote) throws TopazException {
        if (quote == null || quote.getMonthlySwQuoteDomain() == null || quote.getMonthlySwQuoteDomain().getMonthlySoftwares() == null
        		|| quote.getMonthlySwQuoteDomain().getMonthlySoftwares().size() < 1){
            return true;
        }
        if (!(QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode()))) {
            return false;
        }
        QuoteHeader newQuoteHeader = quote.getQuoteHeader();
        QuoteHeader originalQuoteHeader = QuoteHeaderFactory.singleton().findByWebQuoteNum(newQuoteHeader.getPriorQuoteNum());
        if(newQuoteHeader.getQuotePriceTot() != originalQuoteHeader.getQuotePriceTot()
           || (newQuoteHeader.getOfferPrice() != null &&  originalQuoteHeader.getOfferPrice() != null && newQuoteHeader.getOfferPrice().doubleValue() != originalQuoteHeader.getOfferPrice().doubleValue())
           ){
            return false;
        }
        if(!DateUtil.isYMDEqual(newQuoteHeader.getQuoteExpDate(), originalQuoteHeader.getQuoteExpDate())){
        	return false;
        }
        List newLineItemsList = quote.getMonthlySwQuoteDomain().getMonthlySoftwares();
        Map originalLineItemsMap = getSaasLineItemMap(CommonServiceUtil.getMonthlyLineItemList(QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(newQuoteHeader.getPriorQuoteNum())));
        if(newLineItemsList.size() != originalLineItemsMap.size()){
            return false;
        }
        for (int i = 0; i < newLineItemsList.size(); i++) {
            QuoteLineItem newLineItem = (QuoteLineItem) newLineItemsList.get(i);
            QuoteLineItem originalLineItem = (QuoteLineItem) originalLineItemsMap.get(createSaasLineItemKey(newLineItem));
            if(isLineItemChanged4BidItrtn(newLineItem, originalLineItem)){
            	return false;
            }
        }
        logger.info(null,"monthlyNotChanged4BidItrtn return true.");
        return true;
    }

	/**
	 * @param newLineItem
	 * @param originalLineItem
	 * boolean , judge if the line item changed for bid iteration validation 
	 */
	private static boolean isLineItemChanged4BidItrtn(QuoteLineItem newLineItem, QuoteLineItem originalLineItem) {
		//if discount changed, return true
		if(newLineItem.getLineDiscPct() != originalLineItem.getLineDiscPct()){
			return true;
		}
		//if qty changed, return true
		if((newLineItem.getPartQty() != null && originalLineItem.getPartQty() != null && newLineItem.getPartQty().intValue() != originalLineItem.getPartQty().intValue())){
			return true;
		}
		//if start date changed, return true
		if((newLineItem.getMaintStartDate() != null && originalLineItem.getMaintStartDate() != null && newLineItem.getMaintStartDate().compareTo(originalLineItem.getMaintStartDate()) != 0)){
			return true;
		}
		//if compress coverage month changed, return true
		if((newLineItem.hasValidCmprssCvrageMonth() && originalLineItem.hasValidCmprssCvrageMonth() && newLineItem.getCmprssCvrageMonth().intValue() != originalLineItem.getCmprssCvrageMonth().intValue())){
			return true;
		}
		//if compress coverage discount changed, return true
		if((newLineItem.hasValidCmprssCvrageDiscPct() && originalLineItem.hasValidCmprssCvrageDiscPct() && newLineItem.getCmprssCvrageDiscPct().doubleValue() != originalLineItem.getCmprssCvrageDiscPct().doubleValue())){
			return true;
		}
		//if term changed, return true
		if(newLineItem.getICvrageTerm() !=  null && originalLineItem.getICvrageTerm() !=  null 
			&& newLineItem.getICvrageTerm().intValue() != originalLineItem.getICvrageTerm().intValue()){
			return true;
		}
		//if billing frequency changed, return true
		if(!StringUtils.equals(newLineItem.getBillgFrqncyCode(), originalLineItem.getBillgFrqncyCode())){
			return true;
		}
		return false;
	}
    
    public static boolean quoteIsDraftBidItrtn(QuoteHeader quoteHeader) {
        if (quoteHeader == null) {
            return false;
        }
        return QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(quoteHeader.getQuoteStageCode())
                && quoteHeader.isBidIteratnQt();
    }
    
    public static void updateRelatedItemSeqNum(Quote quote) throws TopazException{
        if (quote == null
                || quote.getQuoteHeader() == null
                || quote.getLineItemList() == null
                || quote.getLineItemList().size() == 0) {
            return;
        }
        PartTypeChecker checker = new PartTypeChecker(quote);
        checker.checkType();
        List items = quote.getLineItemList();
        for (int i = 0; i < items.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) items.get(i);
            // for SaaS part, blank the part_type
            if(item.isSaasPart() || item.isMonthlySoftwarePart()){
                item.setSPartType(PartPriceConstants.PartTypeCategory.BLANKPT);
                continue;
            }
            
            PartDisplayAttr partDispAttr = item.getPartDispAttr();
            if(partDispAttr == null){
                return;
            }
            String partType;
            if(partDispAttr.isLicenseBehavior() 
                    && !(PartPriceConstants.InitFtlRevnStrm.IFXTLM.equals(item.getRevnStrmCode()))){
                partType = PartPriceConstants.PartTypeCategory.LICENSE_PART;
            }else if(partDispAttr.isFtlPart() 
                    && (PartPriceConstants.InitFtlRevnStrm.IFXTLM.equals(item.getRevnStrmCode()))){
                partType = PartPriceConstants.PartTypeCategory.FTL_PART;
            }else if(partDispAttr.isRelatedMaint()){
                partType = PartPriceConstants.PartTypeCategory.RELATED_MAINTENANCE_PART;
            }else if(partDispAttr.isUnRelatedMaint()){
                partType = PartPriceConstants.PartTypeCategory.UNRELATED_MAINTENANCE_PART;
            }else if(partDispAttr.isFromRQ()){
                partType = PartPriceConstants.PartTypeCategory.REFERENCES_RENEWAL_PART;
            }else{
                if (item.getMaintStartDate() == null && item.getMaintEndDate() == null) {
                    partType = PartPriceConstants.PartTypeCategory.BLANKPT;
                } else {
                    partType = PartPriceConstants.PartTypeCategory.UNKNOWN;
                }
            }
            item.setIRelatedLineItmNum(partDispAttr.getRelatedItemSeqNum());
            item.setSPartType(partType);

        }
    }
    
    /**
     * check if the quote has at least one part is an obsolete part and without override price
     * 
     * @param quote
     * @return boolean
     * @throws TopazException
     */
    public static boolean qtHasObsltPtWioutOrridPri(Quote quote) throws TopazException{
        if(quote == null || quote.getQuoteHeader() == null) {
            return false;
        }
        //Only load quote line items when they are not loaded
        if(quote.getLineItemList() == null || quote.getLineItemList().size() == 0){
            List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(
                    quote.getQuoteHeader().getWebQuoteNum());
            quote.setLineItemList(lineItemList);
        }
        if(quote.getLineItemList() == null
           || quote.getLineItemList().size() == 0){
            return false;
        }
        List items = quote.getLineItemList();
        for (int i = 0; i < items.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) items.get(i);
            if(item.isObsoletePart() && item.getOverrideUnitPrc() == null){
                return true;
            }
        }
        return false;
    }
    
    /**
     * get the SaaS parts from quotelineitem
     * 
     * @param lineItemList
     * @return List
     */
    public static List getSaaSLineItemList(List lineItemList){
    	List SaaSLineItemList = new ArrayList();
    	Iterator iter = lineItemList.iterator();
    	while(iter.hasNext()){
    		QuoteLineItem  item = (QuoteLineItem)iter.next();
    		if(item.isSaasPart()){
    			SaaSLineItemList.add(item);
    		}
    	}
    	return SaaSLineItemList;
    }
    
    public static List getMonthlyLineItemList(List lineItemList){
    	List monthlyLineItemList = new ArrayList();
    	Iterator iter = lineItemList.iterator();
    	while(iter.hasNext()){
    		QuoteLineItem  item = (QuoteLineItem)iter.next();
    		if(item.isMonthlySoftwarePart()){
    			monthlyLineItemList.add(item);
    		}
    	}
    	return monthlyLineItemList;
    }
    
    /**
     * get the software parts from quote line item list
     * 
     * @param lineItemList
     * @return List
     */
    public static List getSoftwareLineItemList(List lineItemList){
    	List softwareLineItemList = new ArrayList();
    	Iterator iter = lineItemList.iterator();
    	while(iter.hasNext()){
    		QuoteLineItem  item = (QuoteLineItem)iter.next();
    		if(!item.isSaasPart() && !item.isMonthlySoftwarePart()){
    			softwareLineItemList.add(item);
    		}
    	}
    	return softwareLineItemList;
    }
    
    /**
     * @param saasItemList
     * @return the master saas line items that exclude the ramp-up parts and sub overrage parts
     */
    public static List getMasterSaaSLineItemList(List saasItemList){
    	List masterSaaSLineItemList = new ArrayList();
    	if(saasItemList == null || saasItemList.size() == 0){
    		return masterSaaSLineItemList;
    	}
    	masterSaaSLineItemList.addAll(saasItemList);
    	for (Iterator iterator = masterSaaSLineItemList.iterator(); iterator.hasNext();) {
    		QuoteLineItem qli = (QuoteLineItem) iterator.next();
    		if(qli.isRampupPart() || (qli.isSaasSubscrptnOvragePart() && !qli.isMasterOvrage())){
    			iterator.remove();
    		}
		}
    	return masterSaaSLineItemList;
    }
    
    public static String getSaaSPartType(QuoteLineItem qli){
    	String SaaSPartType = "";
    	if(qli.isSaasPart()){
    		if(qli.isSaasSetUpPart()){
    			SaaSPartType = "SETUP";
    		}else if(qli.isSaasSubscrptnPart()){
    			SaaSPartType = "SBSCRPTN";
    		}else if(qli.isSaasOnDemand()){
    			SaaSPartType = "ON_DMND";
    		}else if(qli.isSaasSubscrptnOvragePart()){
    			SaaSPartType = "SUBSCRPTN_OVRAGE";
    		}else if(qli.isSaasSetUpOvragePart()){
    			SaaSPartType = "SETUP_OVRAGE";
    		}else if(qli.isSaasDaily()){
    			SaaSPartType = "DLY";
    		}else if(qli.isSaasProdHumanServicesPart()){
    			SaaSPartType = "PRODCTZD_HUMAN_SERVS";
    		}else if(qli.isSaasSubsumedSubscrptnPart()){
    			SaaSPartType="SUBSUMED";
    		}
    	}else if(qli.isMonthlySoftwarePart()){
    		if(((MonthlySwLineItem)qli).isMonthlySwSubscrptnPart()){
    			SaaSPartType = "MONTHLY_SW_SBSCRPTN";
    		}else if(((MonthlySwLineItem)qli).isMonthlySwOnDemandPart()){
    			SaaSPartType = "MONTHLY_SW_ON_DMND";
    		}else if(((MonthlySwLineItem)qli).isMonthlySwSubscrptnOvragePart()){
    			SaaSPartType = "MONTHLY_SW_SUBSCRPTN_OVRAGE";
    		}else if(((MonthlySwLineItem)qli).isMonthlySwDailyPart()){
    			SaaSPartType = "MONTHLY_SW_DLY";
    		}
    	}
    	return SaaSPartType;
    }
    
    public static String getSaaSPartType(ConfiguratorPart part){
    	String SaaSPartType = "";
		if(part.isSetUp()){
			SaaSPartType = "SETUP";
		}else if(part.isSubscrptn()){
			SaaSPartType = "SBSCRPTN";
		}else if(part.isOnDemand()){
			SaaSPartType = "ON_DMND";
		}else if(part.isOvrage()){
			SaaSPartType = "SUBSCRPTN_OVRAGE";
		}else if(part.isAddiSetUp()){
			SaaSPartType = "SETUP_OVRAGE";
		}else if(part.isDaily()){
			SaaSPartType = "DLY";
		}else if(part.isHumanSrvs()){
			SaaSPartType = "PRODCTZD_HUMAN_SERVS";
		}
    	return SaaSPartType;
    }
	//Ensure line item quantity is not null for non-saas usage parts
    public static int getPartQty(QuoteLineItem qli){
        if (PartPriceSaaSPartConfigFactory.singleton().isNoQty(qli) && !qli.isSaasSubsumedSubscrptnPart()) {
    		return PartPriceConstants.SAAS_PART_DEFAULT_QTY;
    	}
    	
    	return qli.getPartQty() == null ? 0 : qli.getPartQty().intValue();
    }

    public static boolean checkIsUsagePart(QuoteLineItem item){
    	boolean isUsagePart = false;
    	if(item.isSaasPart() || item.isMonthlySoftwarePart()){
    		isUsagePart = PartPriceSaaSPartConfigFactory.singleton().isUsagePart(item);
    	}
    	return isUsagePart;
    }
/**
 * Get appliance part type.
 * @param qli
 * @return
 */
    public static String getAppliancePartType(QuoteLineItem qli){
    	String appliancePartType = "";
    	if(qli.isApplncPart()){
    		if(qli.isApplncMain()){
    			appliancePartType = PartPriceConstants.AppliancePartType.APPLIANCE;
    		}else if(qli.isApplncReinstatement()){
    			appliancePartType = PartPriceConstants.AppliancePartType.REINSTATEMENT;
    		}else if(qli.isApplncUpgrade()){
    			appliancePartType = PartPriceConstants.AppliancePartType.APPLIANCE_UPGRADE;
    		}else if(qli.isApplncTransceiver()){
    			appliancePartType = PartPriceConstants.AppliancePartType.TRANSCEIVER;
    		}else if(qli.isApplncRenewal()){
    			appliancePartType = PartPriceConstants.AppliancePartType.RENEWAL_PARTS;
    		}else if(qli.isApplianceRelatedSoftware()){
    			appliancePartType = PartPriceConstants.AppliancePartType.ADDITIONAL_SOFTWARE;
    		}else if(qli.isApplncServicePack()){
    			appliancePartType = PartPriceConstants.AppliancePartType.SERVICE_PACK;
    		}else if(qli.isApplncServicePackRenewal()){
    			appliancePartType = PartPriceConstants.AppliancePartType.SERVICE_PACK_RENEWAL;
    		}else if(qli.isOwerTransferPart()){
    			appliancePartType = PartPriceConstants.AppliancePartType.OWNERSHIP_TRANSFER;
    		}
    		
    	}
    	return appliancePartType;
    }	
    
    /**
     * @param quote
     * validation for software and SaaS bid iteration
     * @throws TopazException
     */
    public static BidIterationRule validateBidIteration(Quote quote) throws TopazException {
    	BidIterationRule rule = new BidIterationRule();
    	try {
    		if(quote.getSoftwareLineItems() == null || quote.getSoftwareLineItems().size() == 0){
    			rule.setSoftwareValidationResult(QuoteConstants.BidIteratnValidationResult.NOT_APPLICABLE_BID_ITERATN);
    		}else if(softwareNotChanged4BidItrtn(quote)){
    			rule.setSoftwareValidationResult(QuoteConstants.BidIteratnValidationResult.NOT_APPLICABLE_BID_ITERATN);
    			rule.getSoftwareErrorCodeList().add(QuoteConstants.BidIterationErrorMsg.BID_ITERATION_NOT_APPLICABLE);
    		}else if(!softwareIsValid4BidItrtn(quote)){
    			rule.setSoftwareValidationResult(QuoteConstants.BidIteratnValidationResult.INVALID_BID_ITERATN);
    			rule.getSoftwareErrorCodeList().add(QuoteConstants.BidIterationErrorMsg.SOFTWARE_INVALID);
    		}else{
    			rule.setSoftwareValidationResult(QuoteConstants.BidIteratnValidationResult.VALID_BID_ITERATN);
    		}
    		
    		if(quote.getSaaSLineItems() == null || quote.getSaaSLineItems().size() == 0){
    			rule.setSaasValidationResult(QuoteConstants.BidIteratnValidationResult.NOT_APPLICABLE_BID_ITERATN);
    		}else if(saasNotChanged4BidItrtn(quote)){
    			rule.setSaasValidationResult(QuoteConstants.BidIteratnValidationResult.NOT_APPLICABLE_BID_ITERATN);
    			rule.getSaasErrorCodeList().add(QuoteConstants.BidIterationErrorMsg.BID_ITERATION_NOT_APPLICABLE);
    		}else{
				BidIterationRuleHelper bidIterationRuleHelper = new BidIterationSaasRuleHelper(quote, rule);
				bidIterationRuleHelper.validateBidIteration();
    		}
    		
    		if (quote.getMonthlySwQuoteDomain() == null || quote.getMonthlySwQuoteDomain().getMonthlySoftwares() == null ||
    				quote.getMonthlySwQuoteDomain().getMonthlySoftwares().size() < 1){
    			rule.setMonthlyValidationResult(QuoteConstants.BidIteratnValidationResult.NOT_APPLICABLE_BID_ITERATN);
    		} else if (monthlyNotChanged4BidItrtn(quote)){
    			rule.setMonthlyValidationResult(QuoteConstants.BidIteratnValidationResult.NOT_APPLICABLE_BID_ITERATN);
    			rule.getMonthlyErrorCodeList().add(QuoteConstants.BidIterationErrorMsg.BID_ITERATION_NOT_APPLICABLE);
    		} else {
    			BidIterationRuleHelper bidIterationRuleHelper = new BidIterationMonthlyRuleHelper(quote, rule);
    			bidIterationRuleHelper.validateBidIteration();
    		}
		} catch (QuoteException e) {
			throw new TopazException(e);
		}
    	return rule;
    }
    
    /**
     * @param quote
     * judge whether the quote is valid for SaaS Streamlined add-on/trade-ups
     * @throws TopazException
     */
    public static boolean isValidSaasStrmlndAddTrd(Quote quote) throws TopazException {
    	boolean result;
    	try {
	    	StreamlinedRuleHelper streamlinedRuleHelper = new StreamlinedRuleHelper(quote);
	    	result = streamlinedRuleHelper.isValidStreamlined();
    	} catch (QuoteException e) {
			throw new TopazException(e);
		}
    	return result;
    }

	public static List getApplncMainPart(List lineItemList) {
		List applncMains = new ArrayList();
		for (Object obj : lineItemList){
    		QuoteLineItem lineItem = (QuoteLineItem)obj;
    		if (lineItem != null && lineItem.isApplncPart()){
    			if (lineItem.isApplncMain())
    				applncMains.add(lineItem); 
    		}
    	}
		return applncMains;
	}

	public static List getApplncUpgradePart(List lineItemList) {
		List applncUpgradeParts = new ArrayList(); 
		for (Object obj : lineItemList){
    		QuoteLineItem lineItem = (QuoteLineItem)obj;
    		if (lineItem != null && lineItem.isApplncPart()){
    			if(lineItem.isApplncUpgrade())
    				applncUpgradeParts.add(lineItem);
    		}
    	}
		
		return applncUpgradeParts;
	}
	
	public static List getApplncOwnershipPart(List lineItemList) {
		List applncOwnerShipParts = new ArrayList(); 
		for (Object obj : lineItemList){
    		QuoteLineItem lineItem = (QuoteLineItem)obj;
    		if (lineItem != null && lineItem.isApplncPart()){
    			if(lineItem.isOwerTransferPart())
    				applncOwnerShipParts.add(lineItem);
    		}
    	}
		
		return applncOwnerShipParts;
	}
	
	public static boolean getHasRampUpPartFlag(List<QuoteLineItem> masterSaaSLineItems) {
		for (QuoteLineItem item : masterSaaSLineItems) {
			List<QuoteLineItem> rampUpLineItems = item.getRampUpLineItems();
			if(!rampUpLineItems.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	public static String correctLineItemPartType(QuoteLineItem item) {
		String partType = item.getSPartType();
		if (PartPriceConstants.PartRelNumAndTypeDefault.PART_TYPE_DEFAULT.equalsIgnoreCase(partType)){
			if (item.isSaasPart() || item.isMonthlySoftwarePart()) {
				partType = PartPriceConstants.PartTypeCategory.BLANKPT;
			} else {
				PartDisplayAttr partDispAttr = item.getPartDispAttr();
				if (partDispAttr.isLicenseBehavior()
						&& !(PartPriceConstants.InitFtlRevnStrm.IFXTLM.equals(item
								.getRevnStrmCode()))) {
					partType = PartPriceConstants.PartTypeCategory.LICENSE_PART;
				} else if (partDispAttr.isFtlPart()
						&& (PartPriceConstants.InitFtlRevnStrm.IFXTLM.equals(item
								.getRevnStrmCode()))) {
					partType = PartPriceConstants.PartTypeCategory.FTL_PART;
				} else if (partDispAttr.isRelatedMaint()) {
					partType = PartPriceConstants.PartTypeCategory.RELATED_MAINTENANCE_PART;
				} else if (partDispAttr.isUnRelatedMaint()) {
					partType = PartPriceConstants.PartTypeCategory.UNRELATED_MAINTENANCE_PART;
				} else if (partDispAttr.isFromRQ()) {
					partType = PartPriceConstants.PartTypeCategory.REFERENCES_RENEWAL_PART;
				} else {
					if (item.getMaintStartDate() == null
							&& item.getMaintEndDate() == null) {
						partType = PartPriceConstants.PartTypeCategory.BLANKPT;
					} else {
						partType = PartPriceConstants.PartTypeCategory.UNKNOWN;
					}
				}
			}
		}
		return partType;
	}	
    
}
