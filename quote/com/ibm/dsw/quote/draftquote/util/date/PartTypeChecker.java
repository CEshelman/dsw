
package com.ibm.dsw.quote.draftquote.util.date;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.PartDisplayAttr;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 18, 2007
 */

public class PartTypeChecker {
    
//    private static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    // key = swSubId ,value = ArrayList contains QuoteLineItems that are License
    // parts
    private HashMap licensePartMap = new HashMap();

    // key = swSubId, value = ArrayList contains QuoteLineItems that are related
    // maint parts or unrelated parts

    private HashMap maintPartMap = new HashMap();
    
    //  key = swSubId ,value = ArrayList contains QuoteLineItems that are ftl License
    // parts
    private HashMap licenseFtlPartMap = new HashMap();

    // key = swSubId, value = ArrayList contains QuoteLineItems that are ftl related
    // maint parts or unrelated parts

    private HashMap maintFtlPartMap = new HashMap();
    
    private HashMap maintAssociatedLicMap = new HashMap();

    private Quote quote ;   
    
    private PartPriceConfigFactory.DateLogicConfig dateLogicconfig = PartPriceConfigFactory.singleton().getDateLogicConfig();

    public PartTypeChecker(Quote q) {
        this.quote = q;      
        List masterLineItems = quote.getMasterSoftwareLineItems();
        if(masterLineItems == null || masterLineItems.size()==0){
            quote.setMasterSoftwareLineItems(CommonServiceUtil.buildMasterLineItemsWithAddtnlMaint(quote.getLineItemList()));
        }
        
    }
   
    // GD 14.2
    public void checkLicAndMaitAssociation() throws QuoteException, TopazException {
    	assemblyLicAndMaitLineItems();
    	if(licensePartMap.size() > 0)
    		checkPartsAssociation(licensePartMap, maintPartMap);
    }
    
    private void assemblyLicAndMaitLineItems() throws QuoteException{
        licensePartMap.clear();
        maintPartMap.clear();
        
		PartPriceProcess process = PartPriceProcessFactory.singleton().create();
		Map<String,Double> licensePartSplit =  process.getLicensePartSplit(quote.getQuoteHeader().getWebQuoteNum());
		
        // get all non SAAS parts, excluding additional maintenance parts as well
        List masterLineItems = quote.getMasterSoftwareLineItems();
        // assembly license and renewal parts
        for (int i = 0; i < masterLineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) masterLineItems.get(i);

            // only associate non saas/appliance/ftl parts which are not from any renewal quote
            // exclude from association if split factor of the license part is unavailable
            if (GDPartsUtil.isTypeEligibleLicensePart(item) && licensePartSplit.containsKey(item.getPartNum())) {
            	item.setSplitFactor(licensePartSplit.get(item.getPartNum()));
            	this.addToLicensePartMap(licensePartMap, item);
            }else if (GDPartsUtil.isTypeEligibleRenewalPart(item)) {
                item.setAddedToLicParts(null);
                List subItems = item.getAddtnlYearCvrageLineItems();
                for (Iterator subIt = subItems.iterator(); subIt.hasNext();) {
		    		QuoteLineItem subQli = (QuoteLineItem) subIt.next();
		    		subQli.setAddedToLicParts(null);
		        }
                this.addToMaintPartMap(maintPartMap, item);
            }
        }
    }
    
    private void checkPartsAssociation(Map licensePartMap, Map maintPartMap) throws TopazException {

        Iterator iter = maintPartMap.keySet().iterator();
        while (iter.hasNext()) {

            String subSwId = (String) iter.next();
            // check if license parts exist with same subSwId
            List licenseParts = (List) licensePartMap.get(subSwId);
            List maintParts = (List) maintPartMap.get(subSwId);

            if (licenseParts != null && licenseParts.size() > 0) {
                checkCovrgDates(licenseParts, maintParts);
            }
        }
    }
    
	private void checkCovrgDates(List licenseParts, List maintParts) throws TopazException {
		Collections.sort(maintParts, new DateComparator());
		Collections.sort(licenseParts, new DateComparator());
		List noMatchedMasterMaintParts = new ArrayList();
		noMatchedMasterMaintParts.addAll(maintParts);

		// firstly, check dates match 
		// to associate maintenance parts with license parts
		for (Iterator licIt = licenseParts.iterator(); licIt.hasNext();) {
			QuoteLineItem licQli = (QuoteLineItem) licIt.next();
			Date licEndDate = licQli.getMaintEndDate();
			// match criteria
			// the end date of the license part must be one day earlier than the start date of the master renewal part
			if(licEndDate != null){
				Date startDate = DateUtil.plusOneDay(licEndDate);

				// associate renewal parts with date matched license parts
				for (int i = 0; i < maintParts.size(); i++) {
					QuoteLineItem qli = (QuoteLineItem) maintParts.get(i);
					if (DateUtil.isYMDEqual(startDate, qli.getMaintStartDate())) {
						List addedToLicParts = qli.getAddedToLicParts();
						if(addedToLicParts == null){
							addedToLicParts = new ArrayList();
						}
						addedToLicParts.add(licQli);
						qli.setAddedToLicParts(addedToLicParts);
						noMatchedMasterMaintParts.remove(qli);
						associateAdditionalParts(qli);
					}
				}
			}
		}
		// special case: a master renewal part as a sub item of a master renewal part or its last sub part
		maintParts.removeAll(noMatchedMasterMaintParts);
		for (int i = 0; i < noMatchedMasterMaintParts.size(); i++) {
			QuoteLineItem noMatchedQli = (QuoteLineItem) noMatchedMasterMaintParts.get(i);			
			Date maintStartDate = noMatchedQli.getMaintStartDate();
			if(maintStartDate != null){
				Date associatedEndDate = DateUtil.minusOneDay(maintStartDate);
				// associate the master renewal part which doesn't have match in the first dates check with date matched renewal parts
				boolean matched = checkAddiLikeCovrgDates(noMatchedQli, maintParts, associatedEndDate);
				if(matched)
					maintParts.add(noMatchedQli);
			}
		}
	}
	
	// associate additional like renewal parts with their renewal parts
	private boolean checkAddiLikeCovrgDates(QuoteLineItem noMatchedQli, List matchedParts, Date associatedEndDate) {
		boolean matched = false;
		List addedToRenewalParts = noMatchedQli.getAddedToLicParts();
		// associate the master renewal part which doesn't have match in the first round with additional renewal parts
		for (int j = 0; j < matchedParts.size(); j++) {
			QuoteLineItem qli = (QuoteLineItem) matchedParts.get(j);
			if (qli.getAddtnlMaintCvrageQty() == 0 && DateUtil.isYMDEqual(associatedEndDate, qli.getMaintEndDate())) {
				if (addedToRenewalParts == null) {
					addedToRenewalParts = new ArrayList();
				}
				addedToRenewalParts.add(qli);
				noMatchedQli.setAddedToLicParts(addedToRenewalParts);
				associateAdditionalParts(noMatchedQli);
				matched = true;
			}
			else if (qli.getAddtnlMaintCvrageQty() > 0) {
				List addiParts = qli.getAddtnlYearCvrageLineItems();
				int size = addiParts.size();
				QuoteLineItem finalAddiPart = (QuoteLineItem) addiParts.get(size - 1);
				if (finalAddiPart.getMaintEndDate() != null && DateUtil.isYMDEqual(associatedEndDate, finalAddiPart.getMaintEndDate())) {
					if (addedToRenewalParts == null) {
						addedToRenewalParts = new ArrayList();
					}
					addedToRenewalParts.add(finalAddiPart);
					noMatchedQli.setAddedToLicParts(addedToRenewalParts);
					associateAdditionalParts(noMatchedQli);
					matched = true;
				}
			}
		}
		return matched;
	}
	
	// associate additional renewal parts with their renewal parts from the previous year
	private void associateAdditionalParts(QuoteLineItem masterPart){
		if(masterPart.getAddtnlMaintCvrageQty() > 0){ 
			List subItems = masterPart.getAddtnlYearCvrageLineItems();
			for (int j = 0; j < subItems.size(); j++) {
				QuoteLineItem subQli = (QuoteLineItem) subItems.get(j);
				List addedToAddiParts = new ArrayList();
				if(j == 0)
					addedToAddiParts.add(masterPart);
				else
					addedToAddiParts.add(subItems.get(j-1));
				subQli.setAddedToLicParts(addedToAddiParts);
			}
		}
	}
    
    public void checkType() {
        if(!validate()){
            return ;
        }
        
        commonProcess();
        
        checkPartRelatedOrNot(licensePartMap, maintPartMap);
        checkPartRelatedOrNot(licenseFtlPartMap, maintFtlPartMap);
    }
    
    public void checkAutoAdjustDateMaintParts(){
        if(!validate()){
            return ;
        }
        
        commonProcess();
        
        checkNeedAutoAdjustDates(licensePartMap, maintPartMap);
        checkNeedAutoAdjustDates(licenseFtlPartMap, maintFtlPartMap);
        
        
    }
    
    private boolean validate(){
        QuoteHeader header = quote.getQuoteHeader();
        
        if(header.isRenewalQuote()){
            return false;
        }
        //only PA ,PAE ,FCT and OEM is processed
        if(!header.isPAQuote() && !header.isPAEQuote() && !header.isFCTQuote() &&!header.isOEMQuote()){
            return false;
        }
        
        return true;
    }
    
    private void commonProcess(){
        licensePartMap.clear();
        maintPartMap.clear();
        
        licenseFtlPartMap.clear();
        maintFtlPartMap.clear();
        
        List masterLineItems = quote.getMasterSoftwareLineItems();
        // firstly , find all license part, renewal part , contract part and
        // some of unrelated part
        for (int i = 0; i < masterLineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) masterLineItems.get(i);

            PartDisplayAttr attr = item.getPartDispAttr();

            if (attr.isLicenseBehavior()) {
                if(dateLogicconfig.getFtlLicRevenueStream().equals(item.getRevnStrmCode())){
                    this.addToLicensePartMap(licenseFtlPartMap, item);
                }else{
                    this.addToLicensePartMap(licensePartMap, item);
                }
                

            } else if (attr.isMaintBehavior()) {
                if (StringUtils.isNotBlank(item.getRenewalQuoteNum())) {
                    continue;
                }
                if(dateLogicconfig.getFtlMainRevenueStream().equals(item.getRevnStrmCode())){
                    this.addToMaintPartMap(maintFtlPartMap, item);
                }else{
                    this.addToMaintPartMap(maintPartMap, item);
                }
            }
        }
    }

    private void addToLicensePartMap(Map licensePartMap, QuoteLineItem item) {
        String swSubId = item.getSwSubId();
        if (null == swSubId) {
            return;
        }
        List list = (List) licensePartMap.get(swSubId);
        if (null == list) {
            list = new ArrayList();
        }
        list.add(item);
        licensePartMap.put(swSubId, list);
    }

    private void addToMaintPartMap(Map maintPartMap,QuoteLineItem item) {
        String swSubId = item.getSwSubId();
        if (null == swSubId) {
            //No sub id, this is a unrelated maint parts
            item.getPartDispAttr().markUnRelatedMaint();
            return;
        }
        List list = (List) maintPartMap.get(swSubId);
        if (null == list) {
            list = new ArrayList();
        }
        
        list.add(item);
        maintPartMap.put(swSubId, list);
    }

    private void checkPartRelatedOrNot(Map licensePartMap, Map maintPartMap) {

        Iterator iter = maintPartMap.keySet().iterator();

        while (iter.hasNext()) {

            String subSwId = (String) iter.next();
            // check if License Parts exist with same subSwId
            List licenseParts = (List) licensePartMap.get(subSwId);
            List maintParts = (List) maintPartMap.get(subSwId);

            if (null == licenseParts) {
                // no license parts, all parts are unrelated parts
                for (int i = 0; i < maintParts.size(); i++) {
                    QuoteLineItem item = (QuoteLineItem) maintParts.get(i);
                    markUnRelated(item);
                }
            } else {
                Collections.sort(licenseParts, new DestSeqComparator());
                checkQtyAndContCovrg(licenseParts, maintParts);
            }
        }
    }

    private void checkQtyAndContCovrg(List licenseParts, List maintParts) {
        //TODO RTC334 calculate lic part qty by end date
        /*Map licQtyByEndDateMap = this.sumQtyByEndDate(licenseParts);
        
        List qtyMatchItems = new ArrayList();

        // all those parts has associated license part, check if quantity match
        for (int i = 0; i < maintParts.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) maintParts.get(i);
            if(licQtyByEndDateMap != null && licQtyByEndDateMap.containsValue(item.getPartQty())){
                qtyMatchItems.add(item);
            }else{
                markUnRelated(item);
            }
        }*/
        int licTotQty = sumQuantity(licenseParts);
        
        List qtyMatchItems = new ArrayList();

        // all those parts has associated license part, check if quantity match
        for (int i = 0; i < maintParts.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) maintParts.get(i);

            int qty = 0;
            if (item.getPartQty() != null) {
                qty = item.getPartQty().intValue();
            }
            if (qty != licTotQty) {
                markUnRelated(item);
            } else {
                qtyMatchItems.add(item);
            }
        }
        if (qtyMatchItems.size() == 0) {
            return;
        }
        
        Collections.sort(qtyMatchItems, new DateComparator());
        
        //all items in qtyMatchItems meet the condition: quantity match
        for(Iterator licIt = licenseParts.iterator(); licIt.hasNext(); ){
            QuoteLineItem licQli = (QuoteLineItem)licIt.next();
            
            Date licPartEndDate = licQli.getMaintEndDate();
                
            if(licPartEndDate == null){
                
                //license part end date is blank(this should not happen, just in case)
                for(Iterator it = qtyMatchItems.iterator(); it.hasNext(); ){
                    QuoteLineItem qli = (QuoteLineItem)it.next();
                    markUnRelated(qli);
                }
                
                return;
            }
            
            checkCovrg(qtyMatchItems, DateUtil.plusOneDay(licPartEndDate), licQli);
        }
    }

	private void checkCovrg(List qtyMatchItems, Date startDate, QuoteLineItem licQli){
	    int relatedItemSeqNum = licQli.getDestSeqNum();
	    for (int i = 0; i < qtyMatchItems.size(); i++) {
	        QuoteLineItem qli = (QuoteLineItem)qtyMatchItems.get(i);

	        if(qli.getMaintStartDate() == null){
	            qli.getPartDispAttr().markUnRelatedMaint();
	            continue;
	        }
	        
	        if(DateUtil.isYMDEqual(startDate, qli.getMaintStartDate())){
	           
	            qli.getPartDispAttr().markRelatedMaint(relatedItemSeqNum);
	            
	            //mark related/unrelated attribute for additional years of maintenance coverage
	            List subItems = qli.getAddtnlYearCvrageLineItems();
	            if(subItems == null || subItems.size() == 0){
	                startDate = DateUtil.plusOneDay(qli.getMaintEndDate());
	                relatedItemSeqNum = qli.getDestSeqNum();
	                continue;
	                
	            } else {
	                //additional years of coverage must be continuous with the master line item
	                for (int j = 0; j < subItems.size(); j++) {
	                    QuoteLineItem subQli = (QuoteLineItem) subItems.get(j);
                        int subRelItemSeqNum;
                        if (j == 0) {
                            subRelItemSeqNum = qli.getDestSeqNum();
                        } else {
                            subRelItemSeqNum = ((QuoteLineItem)subItems.get(j-1)).getDestSeqNum();
                        }
                        subQli.getPartDispAttr().markRelatedMaint(subRelItemSeqNum);
	                }
	                
	                QuoteLineItem lastSubQli = (QuoteLineItem)subItems.get(subItems.size() - 1);
	                startDate = DateUtil.plusOneDay(lastSubQli.getMaintEndDate());
	                relatedItemSeqNum = lastSubQli.getDestSeqNum();
	            }
	        } else {
	            markUnRelated(qli);
	        }
	    
        }
	}
	
	private void markUnRelated(QuoteLineItem qli){
        //line item may be associated with another license part
        if(!qli.getPartDispAttr().isRelatedMaint()){
            qli.getPartDispAttr().markUnRelatedMaint();
            
            List subList = qli.getAddtnlYearCvrageLineItems();
            if(subList != null && subList.size() > 0){
                for(Iterator subIt = subList.iterator(); subIt.hasNext(); ){
                    QuoteLineItem subQli = (QuoteLineItem)subIt.next();
                    subQli.getPartDispAttr().markUnRelatedMaint();
                }
            }
        }
	}
	
	private void checkNeedAutoAdjustDates(Map licensePartMap, Map maintPartMap){
	    Iterator iter = maintPartMap.keySet().iterator();

        while (iter.hasNext()) {

            String subSwId = (String) iter.next();
            // check if License Parts exist with same subSwId
            List licenseParts = (List) licensePartMap.get(subSwId);
            List maintParts = (List) maintPartMap.get(subSwId);

            if (null == licenseParts) {
                // no license parts, all parts are unrelated parts
                for (int i = 0; i < maintParts.size(); i++) {
                    QuoteLineItem item = (QuoteLineItem) maintParts.get(i);
                    item.getPartDispAttr().markUnAssociateMaintPart();
                    item.getPartDispAttr().markLicensePartNotExist();
                }
            } else {
                markNeedAutoAdjustDates(licenseParts, maintParts);
            }
        }
	}
    
    private void markNeedAutoAdjustDates(List licItems, List maintItems) {
        //TODO RTC334 calculate lic part qty by end date
        /*Map licQtyByEndDateMap = this.sumQtyByEndDate(licItems);
        Set licEndDateKeySet = new HashSet();
        if(licQtyByEndDateMap != null){
            licEndDateKeySet = licQtyByEndDateMap.keySet();
        }
        
        
        List qtyMatchItems = new ArrayList();
        List matchLicEndDateList = new ArrayList();

        // all those parts has associated license part, check if quantity match
        for (int i = 0; i < maintItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) maintItems.get(i);
            int maintQty = item.getPartQty() == null ? 0 : item.getPartQty().intValue();
            for (Iterator iter = licEndDateKeySet.iterator(); iter.hasNext();) {
                Date endDate = (Date) iter.next();
                Integer licQty = (Integer) licQtyByEndDateMap.get(endDate);
                if(licQty.intValue() == maintQty){
                    matchLicEndDateList.add(endDate);
                }
            }
            if(licQtyByEndDateMap != null && licQtyByEndDateMap.containsValue(item.getPartQty())){
                item.getPartDispAttr().markLicensePartExistQtyMatch(maintQty);
                qtyMatchItems.add(item);
            }else{
                item.getPartDispAttr().markUnAssociateMaintPart();
                item.getPartDispAttr().markLicensePartExistQtyNotMatch(maintQty);
            }
        }*/
        
        int totalQty = sumQuantity(licItems);
        
        List qtyMatchItems = new ArrayList();

        // all those parts has associated license part, check if quantity match
        for (int i = 0; i < maintItems.size(); i++) {

            QuoteLineItem item = (QuoteLineItem) maintItems.get(i);
            
            int qty = 0;
            if (item.getPartQty() != null) {
                qty = item.getPartQty().intValue();
            }
            if (qty != totalQty) {
                item.getPartDispAttr().markUnAssociateMaintPart();
                item.getPartDispAttr().markLicensePartExistQtyNotMatch(totalQty);
            } else {
                item.getPartDispAttr().markLicensePartExistQtyMatch(totalQty);
                qtyMatchItems.add(item);
            }
        }
        if (qtyMatchItems.size() == 0) {
            return;
        }
        
        Collections.sort(qtyMatchItems, new DateComparator());
        //TODO RTC334 calculate lic part qty by end date      
        //Collections.sort(matchLicEndDateList);
        Collections.sort(licItems, new DateComparator());
        
        for (int i = 0; i < qtyMatchItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) qtyMatchItems.get(i);
            
            if (i == 0) {
                item.getPartDispAttr().markAssociatedMaintPart();
                //TODO RTC334 calculate lic part qty by end date                  
                //maintAssociatedLicMap.put(item.getPartNum()+"_"+item.getSeqNum(),getAssociatedLicItem(matchLicEndDateList, licItems));
                maintAssociatedLicMap.put(item.getPartNum()+"_"+item.getSeqNum(),licItems.get(licItems.size()-1));
            } else {
                item.getPartDispAttr().markUnAssociateMaintPart();
            }
        }
    }
    
//    private QuoteLineItem findMinSeqNumLineItem(List lineItems){
//        QuoteLineItem minItem = (QuoteLineItem) lineItems.get(0);
//
//        for (int i = 1; i < lineItems.size(); i++) {
//            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
//
//            if (minItem.getSeqNum() > item.getSeqNum()) {
//                minItem = item;
//            }
//        }
//        
//        return minItem;
//    }

//    private boolean isEqual(QuoteLineItem item1, QuoteLineItem item2) {
//        String key1 = item1.getPartNum() + "_" + item1.getSeqNum();
//        String key2 = item2.getPartNum() + "_" + item2.getSeqNum();
//        return key1.equals(key2);
//    }

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
    
//    private Map sumQtyByEndDate(List lineItemsList){
//        if(lineItemsList == null || lineItemsList.size() == 0){
//            return null;
//        }
//        Map map = new HashMap();
//        Set endDateSet = new HashSet();
//        for (int i = 0; i < lineItemsList.size(); i++) {
//            QuoteLineItem item = (QuoteLineItem) lineItemsList.get(i);
//            Date endDate = item.getMaintEndDate();
//            if(endDate != null){
//                endDateSet.add(endDate);
//            }
//        }
//        for (Iterator iter = endDateSet.iterator(); iter.hasNext();) {
//            Date endDate = (Date) iter.next();
//            int qty = 0;
//            for (int i = 0; i < lineItemsList.size(); i++) {
//                QuoteLineItem item = (QuoteLineItem) lineItemsList.get(i);
//                if(endDate.equals(item.getMaintEndDate())){
//                    if (item.getPartQty() != null) {
//                        qty += item.getPartQty().intValue();
//                    }
//                }
//            }
//            map.put(endDate,new Integer(qty));
//        }
//        return map;
//    }
    
//    private QuoteLineItem getAssociatedLicItem(List matchEndDateLicList, List sortedLicList){
//        for (int i = sortedLicList.size() - 1; i >= 0; i--) {
//            QuoteLineItem licItem = (QuoteLineItem) sortedLicList.get(i);
//            if(matchEndDateLicList.size() > 0){
//                Date endDate = (Date) matchEndDateLicList.get(matchEndDateLicList.size() - 1);
//                if(DateUtil.isYMDEqual(endDate, licItem.getMaintEndDate())){
//                    return licItem;
//                }
//            }
//        }
//        return null;
//    }
    
    public QuoteLineItem getAccociatedLicItemByMainItem(QuoteLineItem maintItem){
        if(maintAssociatedLicMap.size() > 0){
            return (QuoteLineItem)maintAssociatedLicMap.get(maintItem.getPartNum()+"_"+maintItem.getSeqNum());
        }else{
            return null;
        }
    }

    public static class DateComparator implements Comparator{
        public int compare(Object o1, Object o2){
            QuoteLineItem qli1 = (QuoteLineItem)o1;
            QuoteLineItem qli2 = (QuoteLineItem)o2;
            
            Date date1 = qli1.getMaintStartDate();
            Date date2 = qli2.getMaintStartDate();
            
            if(DateUtil.isYMDEqual(date1, date2)){
                Integer addtnlQty1 = new Integer(qli1.getAddtnlMaintCvrageQty());
                Integer addtnlQty2 = new Integer(qli2.getAddtnlMaintCvrageQty());
                if(addtnlQty1.compareTo(addtnlQty2) != 0){
                    return addtnlQty2.compareTo(addtnlQty1);
                }else{
                    return ( new Integer(qli1.getDestSeqNum()).compareTo(new Integer(qli2.getDestSeqNum())));
                }
            }
            
            if(date1 != null && date2 == null){
                return -1;
            }
            
            if(date1 == null && date2 != null){
                return 1;
            }
            
            if(date1 == null && date2 == null){
                return 0;
            }
            
            if(date1.before(date2)){
                return -1;
            }
            
            if(date1.after(date2)){
                return 1;
            }
            
            return 0;
        }
    }
    
    class DestSeqComparator implements Comparator{
        public int compare(Object o1, Object o2){
            QuoteLineItem qli1 = (QuoteLineItem)o1;
            QuoteLineItem qli2 = (QuoteLineItem)o2;
            
            Date date1 = qli1.getMaintEndDate();
            Date date2 = qli2.getMaintEndDate();
            if(DateUtil.isYMDEqual(date1, date2)){
                return ( new Integer(qli2.getDestSeqNum()).compareTo(new Integer(qli1.getDestSeqNum())));
            }
            else{
                if(date1 != null && date2 == null){
                    return 1;
                }
                
                if(date1 == null && date2 != null){
                    return -1;
                }
                
                if(date1 == null && date2 == null){
                    return 0;
                }
                
                if(date1.before(date2)){
                    return -1;
                }
                
                if(date1.after(date2)){
                    return 1;
                }
                
                return 0;
            }
            
        }
    }
    
}
