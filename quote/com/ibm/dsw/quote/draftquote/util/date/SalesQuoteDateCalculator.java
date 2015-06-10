package com.ibm.dsw.quote.draftquote.util.date;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.PartDisplayAttr;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DateCalculator</code> class is to calculate the start/end date of
 * parts
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 28, 2007
 */

public class SalesQuoteDateCalculator extends DateCalculator {

    private static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    private PartTypeChecker partTypeChecker = null;

    private boolean isQualifiedForDateCalculation = true;

    SalesQuoteDateCalculator(Quote q) {

        super(q);

        partTypeChecker = new PartTypeChecker(q);

        //only PA and PAE and FCT is processed
        String lob = this.quote.getQuoteHeader().getLob().getCode();

        //all lobs except PPSS support date calculation
        if (QuoteConstants.LOB_PPSS.equalsIgnoreCase(lob)) {
            isQualifiedForDateCalculation = false;
        }
        
        List masterLineItems = quote.getMasterSoftwareLineItems();
        
        if(null == masterLineItems || masterLineItems.size()==0){
            masterLineItems = CommonServiceUtil.buildMasterLineItemsWithAddtnlMaint(quote.getLineItemList());
            quote.setMasterSoftwareLineItems(masterLineItems);
        }

    }

    @Override
	public void calculateDate() {

        if (!isQualifiedForDateCalculation) {
            return;
        }

        partTypeChecker.checkAutoAdjustDateMaintParts();

        adjustDate();
    }

    @Override
	public void setLineItemDates() throws TopazException {

        if (!isQualifiedForDateCalculation) {
            return;
        }

        List masterLineItems = quote.getMasterSoftwareLineItems();    

        boolean shouldApplyCmprssCvrage = !hasManualBackDatingLineItems()
                                           && quote.getQuoteHeader().getCmprssCvrageFlag();
        
        boolean isQuoteSubmitted = quote.getQuoteHeader().isSubmittedQuote();
        for (int i = 0; i < masterLineItems.size(); i++) {

            QuoteLineItem masterItem = (QuoteLineItem) masterLineItems.get(i);
            PartDisplayAttr attr = masterItem.getPartDispAttr();
            
            //Don't clear date overriden flag for submitted quote
            if(!isQuoteSubmitted){
                clearDateOvrrdFlagIfDefaultDate(masterItem);  
            }
            
            
            //Don't auto adjust line item dates if un-related(sub id and quantity) maint parts
            if(shouldSetStdDates(masterItem)){
                masterItem.setMaintStartDate(attr.getStdStartDate());
                masterItem.setMaintEndDate(attr.getStdEndDate());
            }

            setStartDateForCmprssCvrageParts(masterItem, shouldApplyCmprssCvrage);

            //always set licence part maint end date to the calculated value
            if(attr.isSysCalEndDate()){
            	masterItem.setMaintEndDate(attr.getCalEndDate());
            }
            

            if(attr.isMaintBehavior() && attr.isLicensePartExist()){
                masterItem.setAssocdLicPartFlag(attr.isAssociatedMaintPart());
                masterItem.setLicPartQty(attr.getLicensePartTotalQty());
            }   
            
            // the line item may have sub line items;
            if(attr.isMaintBehavior()){
                
                List subLineItems = masterItem.getAddtnlYearCvrageLineItems();
                
                Date endDate = masterItem.getMaintEndDate();
                
                for(int j=0;j<subLineItems.size();j++){
                    
                    QuoteLineItem subItem = (QuoteLineItem)subLineItems.get(j);
                    Date startDate = null;
                    if(endDate != null){
                    	startDate  = DateUtil.plusOneDay(endDate);
                    }
                  
                    if(startDate != null){
                    	endDate = DateUtil.plusOneYearMinusOneDay(startDate);
                    }
                    
                    subItem.setMaintStartDate(startDate);
                    subItem.setMaintEndDate(endDate);
                }
                
            }
            
            
        }
    }
    
  
    private boolean shouldSetStdDates(QuoteLineItem masterItem){
        
        //Manual overriden dates, should not auto adjust
        if( masterItem.getStartDtOvrrdFlg() || masterItem.getEndDtOvrrdFlg()){
            return false;
        }
        
        PartDisplayAttr attr = masterItem.getPartDispAttr();
        
        if(attr.isUnAssociateMaintPart()){
            //Up here, part must be un-related maintenance parts
            if(masterItem.getMaintStartDate() == null
                     || masterItem.getMaintEndDate() == null){
                //This could happen when parts are added to quote initially
                //Need to set the default dates
                return true;
            } else { 
                return false;
            }
        } else {
            return true;
        }
    }
    
    private void setStartDateForCmprssCvrageParts(QuoteLineItem masterItem,
                                        boolean shouldApplyCmprssCvrage) throws TopazException{
        PartDisplayAttr attr = masterItem.getPartDispAttr();
        
        //Only do this processing for draft quotes
        if(!quote.getQuoteHeader().isSubmittedQuote()){
            //Set maint start date for cmprss cvrage parts, start date is first day of current month
            if(shouldApplyCmprssCvrage && masterItem.isEligibleForCmprssCvrage()){
                masterItem.setMaintStartDate(attr.getStdStartDate(true));
            }
            
            //Special handling for disable cmprss cvrage, need to restore standard start date
            //For cmprss cvrage eligible parts with no reference to renewal quotes or special biddable renewal parts, end dates must be overriden
            //But in previous check if(!masterItem.getStartDtOvrrdFlg() && !masterItem.getEndDtOvrrdFlg()) start date can't be restored
            if(masterItem.hasValidCmprssCvrageMonth()  && !shouldApplyCmprssCvrage
                    && shouldSetStdDates(masterItem)){
                //For cmprss cvrage applied parts, when 
                masterItem.setMaintStartDate(attr.getStdStartDate());
            }
        }
    }
    
    private void clearDateOvrrdFlagIfDefaultDate(QuoteLineItem qli) throws TopazException{
        PartDisplayAttr attr = qli.getPartDispAttr();
        
        if(qli.getStartDtOvrrdFlg()){
            if(DateUtil.isYMDEqual(qli.getMaintStartDate(), attr.getStdStartDate())){
                qli.setStartDtOvrrdFlg(false);
            }
        }
        
        if(qli.getEndDtOvrrdFlg()){
            if(DateUtil.isYMDEqual(qli.getMaintEndDate(), attr.getStdEndDate())){
                qli.setEndDtOvrrdFlg(false);
            }
        }
    }

    protected void adjustDate() {
        // Firstly ,calculate the start/end date for License /Contract part
        // because maint part start date depends on the license part
        List masterLineItems = quote.getMasterSoftwareLineItems();    
        
        calculateNonMaintPartDates(masterLineItems);
        
        //now handle the related maint part and unrelated maint part
        boolean quoteBackDated = hasManualBackDatingLineItems();
        boolean shouldApplyCmprssCvrage = !quoteBackDated && quote.getQuoteHeader().getCmprssCvrageFlag();
        
        for (int i = 0; i < masterLineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) masterLineItems.get(i);
            PartDisplayAttr attr = item.getPartDispAttr();

            if(attr.isMaintBehavior() && StringUtils.isNotBlank(item.getRenewalQuoteNum()) ){
                
                ////For cmprss cvrage applied parts, don't adjust maint dates when quote is submitted
                if(quote.getQuoteHeader().isSubmittedQuote()){
                    attr.fillMaintDate(item.getMaintStartDate(),item.getMaintEndDate());
                } else {
                    if(shouldApplyCmprssCvrage && item.isEligibleForCmprssCvrage()){
                        attr.fillMaintDate(DateUtil.getFirstDayDateOfCurrentMonth(), item.getMaintEndDate());
                    } else {
                        attr.fillMaintDate(item.getMaintStartDate(),item.getMaintEndDate());
                    }
                }
            } else if (attr.isMaintBehavior()) { //Part is a maintenance part with no reference to renewal quote

                Date startDate = null;
                
                startDate = calculateMaintPartStandardStartDate(item, shouldApplyCmprssCvrage);
                Date[] endDates = new Date[]{null,null};
                
                if(startDate != null){
                	endDates = calculateMaintPartStandardEndDate(startDate, item);	
                }
                

                //endDates[0] is proration end date
                //endDates[1] is non-proration end date
                attr.fillMaintDate(startDate, endDates[0], endDates[1]);
               
                if(logContext instanceof QuoteLogContextLog4JImpl){
        			if(((QuoteLogContextLog4JImpl)logContext).isDebug(this)){		
        				logContext.debug(this, "Master maint line item[" + item.getPartNum() 
    			                + "_" + item.getSeqNum() + "] date detail{ "
								+ "startDate [" + startDate + "], proEndDate[" + endDates[0]
								+ "], nonProEndDate[" + endDates[1] + "]}");
        			}
        		}
            }

        }
        // now all master line items has been ajusted to the correct date,
        // adjust the sub line items
        for(int i=0;i<masterLineItems.size();i++){
            QuoteLineItem item = (QuoteLineItem) masterLineItems.get(i);
            PartDisplayAttr attr = item.getPartDispAttr();
            if(attr.isMaintBehavior()){
                List subLineItems = item.getAddtnlYearCvrageLineItems();
                if(subLineItems == null){
                    continue;
                }
                Date endDate = attr.getStdEndDate();
                for(int j=0;j<subLineItems.size();j++){
                    QuoteLineItem subItem = (QuoteLineItem)subLineItems.get(j);
                    Date startDate = null;
                    if(endDate != null){
                    	startDate = DateUtil.plusOneDay(endDate);	
                    }
                    
                    if(startDate != null){
                    	endDate = DateUtil.plusOneYearMinusOneDay(startDate);
                    }
                    subItem.getPartDispAttr().fillMaintDate(startDate,endDate);
                    subItem.getPartDispAttr().fillMaintDate(startDate,endDate,endDate);
                }
                
            }
        }
    }
    
    protected void calculateNonMaintPartDates(List masterLineItems){
        if(!CommonServiceUtil.quoteIsDraftBidItrtn(quote.getQuoteHeader())){
	        for (int i = 0; i < masterLineItems.size(); i++) {

                QuoteLineItem item = (QuoteLineItem) masterLineItems.get(i);
                PartDisplayAttr attr = item.getPartDispAttr();
                Date startDate = null;
                Date endDate = null;
                Date stdStartDate = null;
                Date stdEndDate = null;

                /*
                 * if (!(attr.isLicense() || attr.isContract()||
                 * attr.isSupport())) { continue; }
                 */
                if (attr.isLicenseBehavior() || attr.isSupport() || item.isApplncReinstatement()) {
                	//Start Date
                    stdStartDate = DateUtil.getCurrentDate();
                    if (item.getStartDtOvrrdFlg()) {
                        startDate = item.getMaintStartDate();
                    } 
                    else {
                        startDate = stdStartDate;
                    }
                    
                    if (attr.isInitFtl()) {
                        stdEndDate = DateUtil.plusOneYearMinusOneDay(stdStartDate);
                        endDate = DateUtil.plusOneYearMinusOneDay(startDate);
                    }
                 
                    else {
                        stdEndDate = DateUtil.getNonFTLEndDate(stdStartDate);
                        endDate = DateUtil.getNonFTLEndDate(startDate);
                    }
                }

                if (attr.isOtherContract()) {
                    stdStartDate = DateUtil.getCurrentDate();
                    stdEndDate = DateUtil.getNonFTLEndDate(stdStartDate);

                    if (item.getStartDtOvrrdFlg()) {
                        startDate = item.getMaintStartDate();
                    } else {
                        startDate = DateUtil.getCurrentDate();
                    }

                    endDate = DateUtil.getNonFTLEndDate(startDate);
                }
                
                if(attr.shouldNotAutoAdjustDates()){
                	stdStartDate = item.getMaintStartDate();
                	stdEndDate = item.getMaintEndDate();
                	endDate = stdEndDate;
                }

                if (attr.isLicenseBehavior() || attr.isSupport() || attr.isOtherContract() || item.isApplncReinstatement()) {
                    attr.setCalEndDate(endDate);
                    attr.fillMaintDate(stdStartDate, stdEndDate);
                    if(logContext instanceof QuoteLogContextLog4JImpl){
            			if(((QuoteLogContextLog4JImpl)logContext).isDebug(this)){		
            				   logContext.debug(this, "Master line item[" + item.getPartNum() + "_" + item.getSeqNum()
                                       + "] date detai{" + "stdStartDate[" + stdStartDate + "], stdEndDate[" + stdEndDate
                                       + "], startDate[" + startDate + "], endDate[" + endDate + "]");
            			}
            		}
                }
            }
        } else {
            for (int i = 0; i < masterLineItems.size(); i++) {

                QuoteLineItem item = (QuoteLineItem) masterLineItems.get(i);
                PartDisplayAttr attr = item.getPartDispAttr();
                Date startDate = null;
                Date endDate = null;
                Date stdStartDate = null;
                Date stdEndDate = null;

                /*
                 * if (!(attr.isLicense() || attr.isContract()||
                 * attr.isSupport())) { continue; }
                 */
                if (attr.isLicenseBehavior() || attr.isSupport()) {
                    stdStartDate = item.getMaintStartDate();

                    if (item.getStartDtOvrrdFlg()) {
                        startDate = item.getMaintStartDate();
                    } else {
                        startDate = stdStartDate;
                    }

                    if (attr.isInitFtl()) {
                        stdEndDate = DateUtil.plusOneYearMinusOneDay(stdStartDate);
                        endDate = DateUtil.plusOneYearMinusOneDay(startDate);
                    } else {
                        stdEndDate = DateUtil.getNonFTLEndDate(stdStartDate);
                        endDate = DateUtil.getNonFTLEndDate(startDate);
                    }
                }

                if (attr.isOtherContract()) {
                    stdStartDate = item.getMaintStartDate();
                    stdEndDate = DateUtil.getNonFTLEndDate(stdStartDate);

                    if (item.getStartDtOvrrdFlg()) {
                        startDate = item.getMaintStartDate();
                    } else {
                        startDate = item.getMaintStartDate();
                    }

                    endDate = DateUtil.getNonFTLEndDate(startDate);
                }
                
                if(attr.shouldNotAutoAdjustDates()){
                	stdStartDate = item.getMaintStartDate();
                	stdEndDate = item.getMaintEndDate();
                	endDate = stdEndDate;
                }

                if (attr.isLicenseBehavior() || attr.isSupport() || attr.isOtherContract()) {
                    attr.setCalEndDate(endDate);
                    attr.fillMaintDate(stdStartDate, stdEndDate);
                    
                    if(logContext instanceof QuoteLogContextLog4JImpl){
            			if(((QuoteLogContextLog4JImpl)logContext).isDebug(this)){		
            			    logContext.debug(this, "Master line item[" + item.getPartNum() + "_" + item.getSeqNum()
                                    + "] date detai{" + "stdStartDate[" + stdStartDate + "], stdEndDate[" + stdEndDate
                                    + "], startDate[" + startDate + "], endDate[" + endDate + "]");
            			}
            		}
                }
            }
        }
    }
    
    protected Date calculateMaintPartStandardStartDate(QuoteLineItem item, boolean shouldApplyCmprssCvrage){
        Date startDate = null;

        PartDisplayAttr attr = item.getPartDispAttr();
        
        if (attr.isAssociatedMaintPart()) {
            
            QuoteLineItem licenseItem = partTypeChecker.getAccociatedLicItemByMainItem(item);
            //getCalEndDate gets the calculated end date(if part is back dated
            // the start date begins from that date
            //or else from the current date)
            //The exception to this is TRMLSSSR date(licence part that allow rep change end date)
            Date licensePartEndDate = licenseItem.getMaintEndDate();
            if(licenseItem.getPartDispAttr().isSysCalEndDate()){
                licensePartEndDate = licenseItem.getPartDispAttr().getCalEndDate();
            }
            
            startDate = DateUtil.plusOneDay(licensePartEndDate);                       
            

        } else {
            if(!CommonServiceUtil.quoteIsDraftBidItrtn(quote.getQuoteHeader())){
	            QuoteHeader header = quote.getQuoteHeader();
	            
	            if(header.isSubmittedQuote()){
	                // For submitted quote, take un-associated line item maintenance date as standard
	            	startDate = item.getMaintStartDate();
	            } else {
		            if(header.isPAQuote() || header.isPAEQuote() || header.isOEMQuote()){
		                
		                startDate = DateUtil.moveToNextFirstDayOfMonth(DateUtil.getCurrentDate());
		                
		            } else{
		                startDate = DateUtil.getCurrentDate();
		            }
	            }
            }else{
                startDate = item.getMaintStartDate();
            }
        }
        
        if(quote.getQuoteHeader().isSubmittedQuote()){
            //For submitted quote cmprss cvrage applied parts, start date should not be auto adjusted
            if(item.hasValidCmprssCvrageMonth() ){
                startDate = item.getMaintStartDate();
            }
        } else {
            //For draft quote cmprss cvrage applied parts, default start date to first day of current month
            if(shouldApplyCmprssCvrage && item.isEligibleForCmprssCvrage()){
                if(!CommonServiceUtil.quoteIsDraftBidItrtn(quote.getQuoteHeader())){
                    startDate = DateUtil.getFirstDayDateOfCurrentMonth();
                }else{
                    startDate = item.getMaintStartDate();
                }
            }
        }
        
        return startDate;
    }
    
    protected Date[] calculateMaintPartStandardEndDate(Date startDate, QuoteLineItem item){
        PartDisplayAttr attr = item.getPartDispAttr();
        Date proEndDate;
        Date nonProEndDate;
        
        //non proration date
        if (attr.isFtlPart()) {
            nonProEndDate = DateUtil.plusOneYearMinusOneDay(startDate);
        } else {
            nonProEndDate = DateUtil.getNonFTLEndDate(startDate);
        }
        //only PA quote has proration end date, for others , we set proration end date = non-proration end date
        if(quote.getQuoteHeader().isPAQuote() || quote.getQuoteHeader().isOEMQuote()){
            proEndDate = calculateEndDateForProration(quote, startDate);  
        }
        else{
            proEndDate = nonProEndDate;  
        }
        
        //For cmprss cvrage applied parts, don't adjust maint dates for submitted quote
        if(quote.getQuoteHeader().isSubmittedQuote() && item.hasValidCmprssCvrageMonth() ){
            nonProEndDate = item.getMaintEndDate();
            proEndDate = nonProEndDate;
        }
        
        //For submitted quote, take un-associated line item maintenance date as standard
        if(quote.getQuoteHeader().isSubmittedQuote() && item.getPartDispAttr().isUnAssociateMaintPart()){
            nonProEndDate = item.getMaintEndDate();
            proEndDate = nonProEndDate;
        }
        
        return new Date[]{proEndDate, nonProEndDate};
    }

    private Date calculateEndDateForProration(Quote quote, Date firstYearStartDate) {

        Date firstAnniversary = null;
        if (QuoteCommonUtil.hasExistingPACustomerAndContract(quote)) {

            Contract contract = (Contract) quote.getCustomer().getContractList().get(0);
            firstAnniversary = contract.getAnniversaryDate() == null ? null : new Date(contract.getAnniversaryDate()
                    .getTime());

        }
        //firstAnniversary = DateUtil.parseDate("2004-03-31",DateUtil.PATTERN);
        if (firstAnniversary == null) {
            // should be an error
            logContext.debug(this,
                    "The anniversary date is null,pro-ration end date = first year start plus one year minus one day");
            return DateUtil.plusOneYearMinusOneDay(firstYearStartDate);

        }
        // Andy : if annniversary is 4-30 (only month and day is valid)
        // case one: current date is 2007-3-04 , next anniversay will be
        // 2007-4-30
        // case two : current date is 2007-5-30 ,next anniversary will be
        // 2008-4-30 (plus one year)
        Date nextAnniversary = DateUtil.getNextAnniversary(firstYearStartDate, firstAnniversary);

        return DateUtil.minusOneDay(nextAnniversary);
    }

    private QuoteLineItem getAssociatedLicensePart(QuoteLineItem maintLineItem) {
        List lineItems = this.quote.getMasterSoftwareLineItems();
        //there may be more than one licence parts, get the first licence part 
        QuoteLineItem minLicItem = null;
        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            if (item.getPartDispAttr().isLicenseBehavior()) {
                if (maintLineItem.getSwSubId().equals(item.getSwSubId())) {
                	if(minLicItem == null){
                		minLicItem = item;
                		
                	} else if(minLicItem.getSeqNum() > item.getSeqNum()){
                		minLicItem = item;
                	}
                }
            }
        }

        return minLicItem;

    }
    
    private boolean hasManualBackDatingLineItems(){
    	QuoteHeader header = quote.getQuoteHeader();

        List masterItems = quote.getMasterSoftwareLineItems();
        
        if (masterItems == null || masterItems.size() == 0) {
            logContext.debug(this, "Master line item is empty, check manual back dating flag returns false");
            return false;
        }
        
        for (Iterator masterIt = masterItems.iterator(); masterIt.hasNext();) {

            QuoteLineItem masterItem = (QuoteLineItem) masterIt.next();
            
            //Skip check for parts referencing renewal quote
        	if(StringUtils.isNotBlank(masterItem.getRenewalQuoteNum())){
        	    continue;
        	}
        	
        	if(masterItem.getPartQty() != null && masterItem.getPartQty().intValue() == 0){
        	    continue;
        	}
        	
        	if(masterItem.isItemBackDated() && masterItem.getStartDtOvrrdFlg()){
        	    return true;
        	}
        }
        
        return false;
    }
}
