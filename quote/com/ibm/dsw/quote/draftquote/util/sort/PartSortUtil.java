package com.ibm.dsw.quote.draftquote.util.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). <p/>
 * 
 * The <code>PartSort</code> use java library to sort the QuoteLineItems
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <p/>
 * 
 * 
 * Creation date: Mar 19, 2007
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PartSortUtil {
    
    public static void sort(Quote quote) throws TopazException{
        
        /*QuoteHeader header = quote.getQuoteHeader();
        
        String lob = quote.getQuoteHeader().getLob().getCode();
        
        if(header.isSalesQuote()){            
            
        	List softwareMasterLineItems = quote.getMasterSoftwareLineItems();
            
            sortSoftware(true, softwareMasterLineItems, lob, quote);
            
            quote.sortMonthlySwParts();
            
            sortSaaS(true, quote, lob);
            
            flattenSorttedMasterLineItems(quote);
            
        } else{
            
            List lineItems = quote.getLineItemList();
            
            sortSoftware(false, lineItems, lob, quote);
        }*/
        
        if(quote.getQuoteHeader().isSalesQuote()){            
        	sortSoftware(quote);
			if (quote.getMonthlySwQuoteDomain().getMonthlySwConfgrtns() != null
					&& quote.getMonthlySwQuoteDomain().getMonthlySwConfgrtns().size() > 0) {
				sortMonthlyParts(quote);
			}
            
			if (quote.getPartsPricingConfigrtnsList() != null && quote.getPartsPricingConfigrtnsList().size() > 0) {
				sortSaas(quote);
			}
                        
        } else{
        	sortNonSalesSoftware(quote);
        }
    }
    
    protected static void flattenSorttedMasterLineItems(Quote quote) throws TopazException{
        
        List sorttedLineItemList = new ArrayList();
        List sorttedSoftwareLineItemList = new ArrayList();
        
        List masterSoftwareLineItems = quote.getMasterSoftwareLineItems();
        List masterMonthlySwLineItems = quote.getMonthlySwQuoteDomain().getMasterMonthlySwLineItems();
        List masterSaaSLineItems = quote.getMasterSaaSLineItems();
        if(quote.getLineItemList() == null || quote.getLineItemList().size() == 0){            
            return;
        }
        if(masterSoftwareLineItems != null){
	        for (int i = 0; i < masterSoftwareLineItems.size(); i++) {			
	            
	            QuoteLineItem item = (QuoteLineItem) masterSoftwareLineItems.get(i);
				sorttedLineItemList.add(item);
				
				List subLineItems = item.getAddtnlYearCvrageLineItems();				
				sorttedLineItemList.addAll(subLineItems);		
				
				sorttedSoftwareLineItemList.add(item);
				sorttedSoftwareLineItemList.addAll(subLineItems);		
			}
        }
        
        // add monthly software parts to the lienItemList
        if(masterMonthlySwLineItems != null){
	        for (int i = 0; i < masterMonthlySwLineItems.size(); i++) {			
	            QuoteLineItem item = (QuoteLineItem) masterMonthlySwLineItems.get(i);	
				sorttedLineItemList.add(item);
				List ramUpLineItems = item.getRampUpLineItems();
				sorttedLineItemList.addAll(ramUpLineItems);	
			}
        }
        
        // add SaaSparts to the lienItemList
        if(masterSaaSLineItems != null){
	        for (int i = 0; i < masterSaaSLineItems.size(); i++) {			
	            QuoteLineItem item = (QuoteLineItem) masterSaaSLineItems.get(i);	
				sorttedLineItemList.add(item);
				List ramUpLineItems = item.getRampUpLineItems();
				sorttedLineItemList.addAll(ramUpLineItems);	
			}
        }
        
        quote.setLineItemList(sorttedLineItemList);
        quote.setSoftwareLineItems(sorttedSoftwareLineItemList);
        
    }

    private static void sortSoftware(boolean isSalesQuote, List lineItems, String lob, Quote quote) throws TopazException {
        if (isSalesQuote) {
            if (QuoteConstants.LOB_PA.equalsIgnoreCase(lob) || QuoteConstants.LOB_PAE.equalsIgnoreCase(lob)
                    || QuoteConstants.LOB_PAUN.equals(lob)) {
                Collections.sort(lineItems, QuoteBaseComparator.createPAAndPAEComparator());
            } else if (QuoteConstants.LOB_PPSS.equalsIgnoreCase(lob)) {
                Collections.sort(lineItems, QuoteBaseComparator.createPPSComparator());
            } else if (QuoteConstants.LOB_FCT.equalsIgnoreCase(lob)) {
                Collections.sort(lineItems, QuoteBaseComparator.createFCTComparator());
            }else if(QuoteConstants.LOB_OEM.equalsIgnoreCase(lob)){
                Collections.sort(lineItems, QuoteBaseComparator.createOEMComparator());
            }
        } else {
            if (QuoteConstants.LOB_FCT.equalsIgnoreCase(lob)) {
                Collections.sort(lineItems, QuoteBaseComparator.createRQFCTComparator());
            } else {
                Collections.sort(lineItems, QuoteBaseComparator.createRQComparator());
            }

        }

        // user's specified order always has the top priority, for example:
        // 4 QLIs on the quote
        // QLI_1 : standard order
        // QLI_2 : standard order
        // QLI_3 : user specified order 2
        // QLI_4 : standard order
        // in the sorting result, QLI_3 will be the first one, others will be
        // sortd using standard order
        int order = 0;
        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            int manualSortOrder = i+1;
            
            if (item.getManualSortSeqNum() != 0) {
                item.setManualSortSeqNum(manualSortOrder);
            }
            order = order + 1;
            item.setQuoteSectnSeqNum(order);
            item.setDestSeqNum(order);
            
            List subLineItems = item.getAddtnlYearCvrageLineItems();
            for (int j = 0; j < subLineItems.size(); j++) {
                QuoteLineItem subItem = (QuoteLineItem) subLineItems.get(j);
                order = order + 1;
                subItem.setQuoteSectnSeqNum(order);
                subItem.setDestSeqNum(order);
                // make sure the manual sort order of sub line item is same as the main line item
                subItem.setManualSortSeqNum(item.getManualSortSeqNum());
            }

        }
    }
    public static void sortByDestSeqNumber(List lineItems){
        
        Collections.sort(lineItems, new  Comparator(){
            public int compare(Object o1, Object o2) {
                QuoteLineItem item1 = (QuoteLineItem) o1;
                QuoteLineItem item2 = (QuoteLineItem) o2;
                return item1.getDestSeqNum() - item2.getDestSeqNum();
            }
        });
        
    }
    
    /**
     * @param isSalesQuote
     * @param softwareLineItems
     * @param SaaSLineItems
     * @param lob
     * @throws TopazException
     * void
     *  sort the SaaS parts
     */
    private static void sortSaaS(boolean isSalesQuote, Quote quote, String lob) throws TopazException {
    	if (isSalesQuote) {
            if (QuoteConstants.LOB_PA.equalsIgnoreCase(lob) 
            		|| QuoteConstants.LOB_PAE.equalsIgnoreCase(lob)
                    || QuoteConstants.LOB_PAUN.equals(lob)
                    || QuoteConstants.LOB_SSP.equalsIgnoreCase(lob)
                    || QuoteConstants.LOB_FCT.equalsIgnoreCase(lob)) {
            	List configrtnsList = quote.getPartsPricingConfigrtnsList();
            	Map configrtnsMap = quote.getPartsPricingConfigrtnsMap();
            	if(configrtnsList != null && configrtnsList.size() > 0){
            		List sortedMasterSaasPars = new ArrayList();
            		for (Iterator iterator = configrtnsList.iterator(); iterator
							.hasNext();) {
						PartsPricingConfiguration configrtn = (PartsPricingConfiguration) iterator.next();
						List saasParts = (List)configrtnsMap.get(configrtn);
						Collections.sort(saasParts, QuoteBaseComparator.createSaaSPartComparator());
						sortedMasterSaasPars.addAll(saasParts);
					}
            		quote.setMasterSaaSLineItems(sortedMasterSaasPars);
            	}
            } else {
            	return;
            }
        } else {
        	return;
        }

        // user's specified order always has the top priority, for example:
        // 4 QLIs on the quote
        // QLI_1 : standard order
        // QLI_2 : standard order
        // QLI_3 : user specified order 2
        // QLI_4 : standard order
        // in the sorting result, QLI_3 will be the first one, others will be
        // sortd using standard order
    	List softwareLineItems = quote.getSoftwareLineItems();
    	List monthlySwLineItems = quote.getMonthlySwQuoteDomain().getMonthlySoftwares();
    	List masterSaaSLineItems = quote.getMasterSaaSLineItems();
        int manualSortOrder = 0;
        int order = 0;
        if(softwareLineItems != null){
        	manualSortOrder += softwareLineItems.size();
        	order += softwareLineItems.size();
        }
        if(monthlySwLineItems != null){
        	manualSortOrder += monthlySwLineItems.size();
        	order += monthlySwLineItems.size();
        }
        List sorttedSaaSLineItems = new ArrayList();
        for (int i = 0; i < masterSaaSLineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) masterSaaSLineItems.get(i);
            manualSortOrder = manualSortOrder+1;
            if (item.getManualSortSeqNum() != 0) {
                item.setManualSortSeqNum(manualSortOrder);
            }
            order = order + 1;
            item.setQuoteSectnSeqNum(order);
            sorttedSaaSLineItems.add(item);
            List rampUpLineItems = item.getRampUpLineItems();
            for (int j = 0; j < rampUpLineItems.size(); j++) {
                QuoteLineItem rampUpItem = (QuoteLineItem) rampUpLineItems.get(j);
                order = order + 1;
                rampUpItem.setQuoteSectnSeqNum(order);
                // make sure the manual sort order of sub line item is same as the main line item
                rampUpItem.setManualSortSeqNum(item.getManualSortSeqNum());
                sorttedSaaSLineItems.add(rampUpItem);
            }
        }
        quote.setSaaSLineItems(sorttedSaaSLineItems);
        
        // adjust 'RELATED_LINE_ITM_NUM' for Monthly daily part
        adjustRelatedLineItmNum4MonthlyDailyParts(quote);
        sortDestSeqNums(quote, quote.getMonthlySwQuoteDomain().getMonthlySoftwares(), quote.getSoftwareLineItems().size());
        sortDestSeqNums(quote, quote.getSaaSLineItems(), quote.getSoftwareLineItems().size()
                + quote.getMonthlySwQuoteDomain().getMonthlySoftwares().size());
        
    }
    
    /**
     * @param partsPricingConfigrtnsList
     * sort configuration by product id description
     */
    public static void sortConfiguration(List partsPricingConfigrtnsList){
        Collections.sort(partsPricingConfigrtnsList, new  Comparator(){
            public int compare(Object o1, Object o2) {
                PartsPricingConfiguration confgrtn1 = (PartsPricingConfiguration) o1;
                PartsPricingConfiguration confgrtn2 = (PartsPricingConfiguration) o2;
                String IbmProdIdDscr1 = confgrtn1.getIbmProdIdDscr();
                String IbmProdIdDscr2 = confgrtn2.getIbmProdIdDscr();
                if(IbmProdIdDscr1 == null && IbmProdIdDscr2 ==  null){
                	return 0;
                }
                if(IbmProdIdDscr1 == null && IbmProdIdDscr2 != null){
                	return -1;
                }
                if(IbmProdIdDscr1 != null && IbmProdIdDscr2 == null){
                	return 1;
                }
                return confgrtn1.getIbmProdIdDscr().compareTo(confgrtn2.getIbmProdIdDscr());
            }
        });
    }
    
    public static void sortDestSeqNums(Quote quote, List<MonthlySwLineItem> monthlyItems, int destSeqNum) throws TopazException {
        if(monthlyItems == null || monthlyItems.size() == 0){
            return;
        }
        
        //sort list by seq num
        Collections.sort(monthlyItems, new  Comparator(){
            public int compare(Object o1, Object o2) {
                QuoteLineItem item1 = (QuoteLineItem) o1;
                QuoteLineItem item2 = (QuoteLineItem) o2;
                return item1.getSeqNum()-item2.getSeqNum();
            }
        });
        
        
        List<QuoteLineItem> sortedItems = new ArrayList<QuoteLineItem>();
        List<QuoteLineItem> replacedPart = new ArrayList<QuoteLineItem>();
        HashMap<String,List<QuoteLineItem>> rampUpParts =  new HashMap<String,List<QuoteLineItem>>();
        List<QuoteLineItem> initSubId = new ArrayList<QuoteLineItem>();
        
        for (Iterator iterator = monthlyItems.iterator(); iterator.hasNext();) {
            QuoteLineItem qli = (QuoteLineItem) iterator.next();
            //find replaced part
            if(qli.isReplacedPart()){
                replacedPart.add(qli);
                continue;
            }
            
            if(rampUpParts.containsKey(qli.getSwSubId())){
                rampUpParts.get(qli.getSwSubId()).add(qli);
            }else{
                List<QuoteLineItem> tempInitPart = new ArrayList<QuoteLineItem>();
                tempInitPart.add(qli);
                rampUpParts.put(qli.getSwSubId(), tempInitPart);
            }
            
			if (qli.getIRelatedLineItmNum() == -1 || qli.isSaasSetUpPart()) {
                initSubId.add(qli);
            }
            
        }
        //add replaced part
        sortedItems.addAll(replacedPart);
        
        for(int i=0;i<initSubId.size();i++){
            QuoteLineItem item = initSubId.get(i);
            //add init part
            sortedItems.add(item);
            
            
            /**
             * 1 : Daily part for that initial ramp-up line<br>
             * 2 : Overage for the initial ramp-up line<br>
             * 3 : The additional ramp-up line <br>
             * 4 : Overage for the additional ramp-up line<br>
             * 5 : Final ramp-up line <br>
             * 6 : Overage for the final ramp-up line<br>
             */
            // add daily part
            QuoteLineItem dailyItem = findRelatedItem(rampUpParts.get(item.getSwSubId()), item.getDestSeqNum(), "", 1);
            if (dailyItem != null) {
                sortedItems.add(dailyItem);
            }

            // add overage part
            QuoteLineItem overageItem = findRelatedItem(rampUpParts.get(item.getSwSubId()), item.getDestSeqNum(), "", 2);
            if (overageItem != null) {
                sortedItems.add(overageItem);
            }
            
            QuoteLineItem rampUpItem = findRelatedItem(rampUpParts.get(item.getSwSubId()), item.getDestSeqNum(),
                    item.getPartNum(), 3);
            boolean haveRampUp = rampUpItem != null;
            QuoteLineItem lastRampUpItem = null;
            if (haveRampUp) {
                while (haveRampUp) {
                    lastRampUpItem = rampUpItem;
                    if (rampUpItem != null) {
                        sortedItems.add(rampUpItem);
                        // find cover for ramp up
                        QuoteLineItem covRamp = findRelatedItem(rampUpParts.get(item.getSwSubId()), rampUpItem.getDestSeqNum(),
                                "", 4);
                        if (covRamp != null) {
                            sortedItems.add(covRamp);
                        }
                    }
                    rampUpItem = findRelatedItem(rampUpParts.get(item.getSwSubId()), rampUpItem.getDestSeqNum(),
                            rampUpItem.getPartNum(), 3);
                    haveRampUp = rampUpItem != null;
                }
            } else {
                lastRampUpItem = item;
            }

            // add subscription part
            QuoteLineItem subscriptionPart = findRelatedItem(rampUpParts.get(item.getSwSubId()), lastRampUpItem.getDestSeqNum(),
                    item.getPartNum(), 5);
            if (subscriptionPart != null) {
                sortedItems.add(subscriptionPart);
                QuoteLineItem overagePart = findRelatedItem(rampUpParts.get(item.getSwSubId()), subscriptionPart.getDestSeqNum(),
                        item.getPartNum(), 2);
                if (overagePart != null) {
                    sortedItems.add(overagePart);
                }
            }
        }
        
        Map desSeqNumMap = new HashMap();
        Map<String, List<QuoteLineItem>> relatedItemNumMap = new HashMap<String, List<QuoteLineItem>>();
        List<QuoteLineItem> tempLineItems = new ArrayList<QuoteLineItem>();
        tempLineItems.addAll(sortedItems);
        for (Iterator iterator = sortedItems.iterator(); iterator.hasNext();) {
            destSeqNum ++;
            QuoteLineItem qli = (QuoteLineItem) iterator.next();
            desSeqNumMap.put(qli.getPartNum()+qli.getSeqNum(), destSeqNum);
            updateRelatedItemMap(qli, tempLineItems, relatedItemNumMap);
        }
        
        for (Iterator iterator = monthlyItems.iterator(); iterator.hasNext();) {
            QuoteLineItem subQli = (QuoteLineItem) iterator.next();
            Integer desSeqNumInt = (Integer) desSeqNumMap.get(subQli.getPartNum() + subQli.getSeqNum());
            if(desSeqNumInt != null){
                List<QuoteLineItem> relatedItemList = relatedItemNumMap.get(getLineItemDestNumKey(subQli));
                if (relatedItemList != null) {
                    for (Iterator it = relatedItemList.iterator(); it.hasNext();) {
                        QuoteLineItem relatedLineItem = (QuoteLineItem) it.next();
                        relatedLineItem.setIRelatedLineItmNum(desSeqNumInt.intValue());
                    }
                }
                subQli.setDestSeqNum(desSeqNumInt.intValue());
            }
        }
    }

    /**
     * DOC before add this adjust logic, RelatedLineItmNum of monthly daily part is the DEST_OBJCT_LINE_ITM_SEQ_NUM of
     * subscription part.<BR>
     * it should be DEST_OBJCT_LINE_ITM_SEQ_NUM of the initial ramp-up line item.<BR>
     * please refer to RTC # 696310 and corresponding eBiz ticket for more details. Example of sequence for two ramp-up
     * parts on the same quote. <BR>
     * 1) Initial ramp-up line for part A<BR>
     * 2) Daily part for that initial ramp-up line for part A<BR>
     * 3) Overage for the initial ramp-up line for part A<BR>
     * 4) The additional ramp-up line for part A<BR>
     * 5) Overage for the additional ramp-up line for part A<BR>
     * 6) 4 & 5 could be repeated a number of times for part A<BR>
     * 7) Final ramp-up line for part A<BR>
     * 8) Overage for the final ramp-up line for part A<BR>
     * 9) Initial ramp-up line for part B<BR>
     * 10) Daily part for that initial ramp-up line for part B<BR>
     * 11) Overage for the additional ramp-up line for part B<BR>
     * 12)The additional ramp-up line for part B<BR>
     * 13) Overage for the additional ramp-up line for part B<BR>
     * 14) 12 & 13 could be repeated a number of times for part B<BR>
     * 15) Final ramp-up line for part B<BR>
     * 16) Overage for the final ramp-up line for part B<BR>
     * 
     * @param quote
     */
    private static void adjustRelatedLineItmNum4MonthlyDailyParts(Quote quote) throws TopazException {
        List monthlySwItems = quote.getMonthlySwQuoteDomain().getMonthlySoftwares();
        if (monthlySwItems == null || monthlySwItems.size() == 0) {
            return;
        }
        List monthlySwItemsCopy = new ArrayList(monthlySwItems.size());
        monthlySwItemsCopy.addAll(monthlySwItems);

        for (Iterator iterator = monthlySwItems.iterator(); iterator.hasNext();) {
            MonthlySwLineItem lineItem = (MonthlySwLineItem) iterator.next();
            if (lineItem.isMonthlySwSubscrptnPart()) {
                List rampUpParts = lineItem.getRampUpLineItems();
                QuoteLineItem dailyLineItem = findRelatedItem(monthlySwItemsCopy, lineItem.getDestSeqNum(), "", 1);

                if (dailyLineItem != null) {
                    for (Iterator rampIterator = rampUpParts.iterator(); rampIterator.hasNext();) {
                        MonthlySwLineItem rampLineItem = (MonthlySwLineItem) rampIterator.next();
                        if (rampLineItem.getIRelatedLineItmNum() == -1) {
                            dailyLineItem.setIRelatedLineItmNum(rampLineItem.getDestSeqNum());
                            break;
                        }
                    }
                }
            }
        }

    }

    /**
     * DOC Comment method "updateRelatedItemMap".
     * 
     * @param qli
     * @param tempLineItems
     * @param relatedItemMap
     */
    private static void updateRelatedItemMap(QuoteLineItem qli, List<QuoteLineItem> tempLineItems, Map relatedItemMap) {
        String key = getLineItemDestNumKey(qli);
        List relatedItemList = (List) relatedItemMap.get(key);
        if (relatedItemList == null) {
            relatedItemList = new ArrayList<QuoteLineItem>();
            relatedItemMap.put(key, relatedItemList);
        }

        for (Iterator iterator = tempLineItems.iterator(); iterator.hasNext();) {
            QuoteLineItem item = (QuoteLineItem) iterator.next();
            if (qli.getDestSeqNum() == item.getIRelatedLineItmNum()) {
                if (!relatedItemList.contains(item)) {
                    relatedItemList.add(item);
                }
            }
        }
    }
    /**
     * DOC Comment method "getLineItemDestNumKey".
     * @param qli
     * @return
     */
    private static String getLineItemDestNumKey(QuoteLineItem qli) {
        String key = qli.getSeqNum() + "_" + qli.getPartNum() + "_" + qli.getDestSeqNum();
        return key;
    }
    private static QuoteLineItem findRelatedItem(List<QuoteLineItem> list, int relatedId, String partNum, int flag) {
    	/*
    	 flag explain
    	 1 :  Daily part for that initial ramp-up line
    	      this.RELATED_LINE_ITM_NUM = init.SEQ_NUM
    	      this.SAAS_DLY_FLAG = 1 
    	      
         2 :  Overage for the initial ramp-up line
              this.RELATED_LINE_ITM_NUM = init.SEQ_NUM
              this.SAAS_SBSCRPTN_OVRAGE_FLAG = 1
              
	     3 :  The additional ramp-up line 
	          this.RELATED_LINE_ITM_NUM = rampup.SEQ_NUM
              this.RAMP_UP_FLAG = 1
              
		 4 :  Overage for the additional ramp-up line 
		      this.RELATED_LINE_ITM_NUM = rampup.SEQ_NUM
              this.SAAS_SBSCRPTN_OVRAGE_FLAG = 1
    	 */
        Iterator<QuoteLineItem> iter = list.iterator();
        switch (flag) {
        case 1:// daily
            while (iter.hasNext()) {
                QuoteLineItem item = iter.next();
                if (item instanceof MonthlySwLineItem) {
                    MonthlySwLineItem monthlySwLineItem = (MonthlySwLineItem) item;
                    if (monthlySwLineItem.isMonthlySwDailyPart() && item.getIRelatedLineItmNum() == relatedId) {
                        return item;
                    }
                }
                if (item.isSaasDaily() && item.getIRelatedLineItmNum() == relatedId) {
                    return item;
                }
            }
            break;
        case 2:// overage
            while (iter.hasNext()) {
                QuoteLineItem item = iter.next();
                if (item instanceof MonthlySwLineItem) {
                    MonthlySwLineItem monthlySwLineItem = (MonthlySwLineItem) item;
                    if (monthlySwLineItem.isMonthlySwSubscrptnOvragePart() && item.getIRelatedLineItmNum() == relatedId) {
                        return item;
                    }
                }
                if (item.isSaasSubscrptnOvragePart() && item.getIRelatedLineItmNum() == relatedId) {
                    return item;
                }
            }
            break;
        case 3:// ramp up
            while (iter.hasNext()) {
                QuoteLineItem item = iter.next();
                if (item.isRampupPart() && item.getPartNum().equals(partNum) && item.getIRelatedLineItmNum() == relatedId) {
                    return item;
                }
            }
            break;
        case 4:// overage for ramp up
            while (iter.hasNext()) {
                QuoteLineItem item = iter.next();
                if (item instanceof MonthlySwLineItem) {
                    MonthlySwLineItem monthlySwLineItem = (MonthlySwLineItem) item;
                    if (monthlySwLineItem.isMonthlySwSubscrptnOvragePart() && item.getIRelatedLineItmNum() == relatedId) {
                        return item;
                    }
                }
                if (item.isSaasSubscrptnOvragePart() && item.getIRelatedLineItmNum() == relatedId) {
                    return item;
                }
            }
            break;
        case 5:// subscription part for ramp up
            while (iter.hasNext()) {
                QuoteLineItem item = iter.next();
                if (!item.isRampupPart() && item.getPartNum().equals(partNum) && item.getIRelatedLineItmNum() == relatedId) {
                    return item;
                }
            }
            break;
        default:
            return null;
        }
        return null;
    }
    
    public static void sortMonthlyParts(Quote quote) throws TopazException{
    	quote.sortMonthlySwParts();
    	flattenMonthlyswMasterLineItems(quote);
	}
    
    public static void sortSaas(Quote quote)throws TopazException{
        sortSaaS(true, quote, quote.getQuoteHeader().getLob().getCode());
    	flattenSaasMasterLineItems(quote);
	}
    
    public static void sortSoftware(Quote quote) throws TopazException{
        sortSoftware(true, quote.getMasterSoftwareLineItems(), quote.getQuoteHeader().getLob().getCode(), quote);
        flattenSoftWareSorttedMasterLineItems(quote);
	}
    public static void sortNonSalesSoftware(Quote quote) throws TopazException{
    	sortSoftware(false, quote.getLineItemList(), quote.getQuoteHeader().getLob().getCode(), quote);
	}
    
    protected static void flattenSoftWareSorttedMasterLineItems(Quote quote) throws TopazException{
        
        List sorttedLineItemList = new ArrayList();
        List sorttedSoftwareLineItemList = new ArrayList();
   	 	List orginalLineItemList = quote.getLineItemList();
   	 	List orginalSubLineItemList = new ArrayList();
        
        List masterSoftwareLineItems = quote.getMasterSoftwareLineItems();
        if(quote.getLineItemList() == null || quote.getLineItemList().size() == 0){            
            return;
        }
        if(masterSoftwareLineItems != null){
	        for (int i = 0; i < masterSoftwareLineItems.size(); i++) {			
	            
	            QuoteLineItem item = (QuoteLineItem) masterSoftwareLineItems.get(i);
				sorttedLineItemList.add(item);
				
				List subLineItems = item.getAddtnlYearCvrageLineItems();				
				sorttedLineItemList.addAll(subLineItems);		
				orginalSubLineItemList.addAll(subLineItems);	
				
				sorttedSoftwareLineItemList.add(item);
				sorttedSoftwareLineItemList.addAll(subLineItems);		
			}
        }
        orginalLineItemList.removeAll(masterSoftwareLineItems);
        orginalLineItemList.removeAll(orginalSubLineItemList);
        orginalLineItemList.addAll(sorttedLineItemList);
        quote.setLineItemList(orginalLineItemList);
        quote.setSoftwareLineItems(sorttedSoftwareLineItemList);
        
    }
    
    protected static void flattenMonthlyswMasterLineItems(Quote quote) throws TopazException{
    	 List sorttedLineItemList = new ArrayList();
    	 List orginalLineItemList = quote.getLineItemList();
    	 List orginalRamUpLineItemList = new ArrayList();
         //List sorttedSoftwareLineItemList = new ArrayList();
         
         List masterMonthlySwLineItems = quote.getMonthlySwQuoteDomain().getMasterMonthlySwLineItems();
         if(quote.getLineItemList() == null || quote.getLineItemList().size() == 0){            
             return;
         }
         // add monthly software parts to the lienItemList
         if(masterMonthlySwLineItems != null){
 	        for (int i = 0; i < masterMonthlySwLineItems.size(); i++) {			
 	            QuoteLineItem item = (QuoteLineItem) masterMonthlySwLineItems.get(i);	
 				sorttedLineItemList.add(item);
 				List ramUpLineItems = item.getRampUpLineItems();
 				sorttedLineItemList.addAll(ramUpLineItems);	
 				orginalRamUpLineItemList.addAll(ramUpLineItems);	
 			}
         }
         orginalLineItemList.removeAll(masterMonthlySwLineItems);
         orginalLineItemList.removeAll(orginalRamUpLineItemList);
         orginalLineItemList.addAll(sorttedLineItemList);
         quote.setLineItemList(orginalLineItemList);
        // quote.setSoftwareLineItems(sorttedSoftwareLineItemList);
        
    }
    
    protected static void flattenSaasMasterLineItems(Quote quote) throws TopazException{
   	 	List sorttedLineItemList = new ArrayList();
   	 	List orginalLineItemList = quote.getLineItemList();
   	 	List orginalRamUpLineItemList = new ArrayList();
        //List sorttedSoftwareLineItemList = new ArrayList();
        
        List masterSaaSLineItems = quote.getMasterSaaSLineItems();
        if(quote.getLineItemList() == null || quote.getLineItemList().size() == 0){            
            return;
        }
        // add SaaSparts to the lienItemList
        if(masterSaaSLineItems != null){
	        for (int i = 0; i < masterSaaSLineItems.size(); i++) {			
	            QuoteLineItem item = (QuoteLineItem) masterSaaSLineItems.get(i);	
				sorttedLineItemList.add(item);
				List ramUpLineItems = item.getRampUpLineItems();
				sorttedLineItemList.addAll(ramUpLineItems);	
				orginalRamUpLineItemList.addAll(ramUpLineItems);
			}
        }
        orginalLineItemList.removeAll(masterSaaSLineItems);
        orginalLineItemList.removeAll(orginalRamUpLineItemList);
        orginalLineItemList.addAll(sorttedLineItemList);
        quote.setLineItemList(orginalLineItemList);
        //quote.setSoftwareLineItems(sorttedSoftwareLineItemList);
   }
}
