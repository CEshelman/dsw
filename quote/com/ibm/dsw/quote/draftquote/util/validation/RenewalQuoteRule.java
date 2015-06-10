package com.ibm.dsw.quote.draftquote.util.validation;

import java.sql.Date;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.util.date.DateCalculator;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: Apr 18, 2007
 */

class RenewalQuoteRule extends ValidationRule {



    RenewalQuoteRule(Quote q, PostPartPriceTabContract ct) {
        super(q, ct);
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.util.validation.ValidationRule#validate()
     */
    public void validate() throws TopazException {

        Iterator iter = this.quote.getLineItemList().iterator();
        while (iter.hasNext()) {

            QuoteLineItem item = (QuoteLineItem) iter.next();

            //No data on UI for this part
            if(!isPartParamExist(createKey(item))){
            	continue;
            }

            boolean partDeleted = validateRQQuantity(item);

            if(partDeleted){
                continue;
            }
            validatePVUPartQtyStatus(item);

            this.setPrevDates(item);
            // need calculate the standard date for manually added part
            calculateStandardDate();

            validateRQOverrideStartDate(item);

            validateRQOverrideEndDate(item);

            validateRQComment(item);

            createChangeTypeCode(item);

            //update MTM
            validationMTM(item);
        }

        this.calcuateDate();
        //this.validateSpecialBid();

    }
    public void calculateStandardDate(){
        DateCalculator calculator = DateCalculator.create(quote);
        calculator.calculateDate();
    }
    private void validateRQOvrdUnitPirceAndDiscount(QuoteLineItem item){
        String key = createKey(item);
        ValidationResult vr = getValidationResult(key);

        String sOverrideUnitPrice = ct.getPartPrice(key);
        String sDiscountPercent = ct.getPartDiscount(key);

        if ((null == sOverrideUnitPrice) || null == sDiscountPercent) {
            return;
        }
        double discount = 0.0;
        try{
            discount = Double.valueOf(sDiscountPercent).doubleValue();
        }
        catch(Throwable e){
            logContext.error(this,"Parse the discount percent failed"+ sDiscountPercent);
        }
        if("".equals(sOverrideUnitPrice) &&  DecimalUtil.isEqual(0.0,discount)){
            vr.unitPriceOrDiscountSet = false;
        }
        else{
            vr.unitPriceOrDiscountSet = true;
        }
    }

    /*protected boolean checkSpecialBidForMaintConverage() {
        HashMap map = this.groupByPartNum();
        Iterator iter = map.keySet().iterator();
        while(iter.hasNext()){
            String partNum = (String)iter.next();

            List items = (List)map.get(partNum);

            if(items.size()<3){
                continue;
            }

            // sort by start date
            Collections.sort(items,new Comparator(){
                public int compare(Object o1, Object o2){

                    QuoteLineItem item1 = (QuoteLineItem)o1;
                    QuoteLineItem item2 = (QuoteLineItem)o2;

                    return item1.getMaintStartDate().compareTo(item2.getMaintStartDate());
                }
                });


            for(int i=0;i<items.size();i++){
                int continusYears = 1;
                List checkedLineItms = new ArrayList();
                QuoteLineItem currentItem = (QuoteLineItem)items.get(i);
                checkedLineItms.add(currentItem);
                QuoteLineItem continusItem = currentItem;
                do{
	                continusItem = this.findLineItemWithSpecificStartDate(continusItem,checkedLineItms,items);
	                if(continusItem != null){
	                    continusYears ++;
	                    checkedLineItms.add(continusItem);
	                }
                }while(continusItem !=null);

                // ok, we find a part has more than 3 years continus maintainence
                if(continusYears >=3){
                    return true;
                }
            }

        }
        return false;

    }
    private QuoteLineItem findLineItemWithSpecificStartDate(QuoteLineItem item,List checkedItems,List items){
        Date diseredStartDate =  DateUtil.plusOneDay(item.getMaintEndDate());
        for(int i=0;i<items.size();i++){
            QuoteLineItem tmp = (QuoteLineItem)items.get(i);

            if(checkedItems.contains(tmp)){
                continue;
            }


            if(diseredStartDate.equals(tmp.getMaintStartDate())){
                return tmp;
            }
        }
        return null;
    }
    private HashMap groupByPartNum(){

        HashMap map = new HashMap();

        List items = quote.getLineItemList();

        for (int i = 0; i < items.size(); i++) {

            QuoteLineItem item = (QuoteLineItem) items.get(i);
            if((item.getMaintStartDate() == null) || (item.getMaintEndDate() == null)){
                continue;
            }
            String partNum = item.getPartNum();
            List l = (List)map.get(partNum);
            if(null == l){
                l = new ArrayList();
                map.put(partNum,l);
            }
            l.add(item);

        }


        return map;
    }*/
    /**
     * @param item
     */
    private void validateRQComment(QuoteLineItem item) throws TopazException {

        String key = createKey(item);
        String comment = ct.getRQComment(key);
        item.setComment(comment);

    }

    private void createChangeTypeCode(QuoteLineItem item) throws TopazException {

        String key = createKey(item);

        ValidationResult vr = getValidationResult(key);

        //if the part is manually added, we always keep the change code as "I"
        if(item.getSeqNum()>= PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ){
            item.setChgType(PartPriceConstants.PartChangeType.PART_ADDED);
            return;
        }

        // if the part is not manually added
        if (vr.partDeleted) {
            item.setChgType(PartPriceConstants.PartChangeType.PART_DELETED);
            return;
        }

        StringBuffer buffer = new StringBuffer();
        if (vr.quantityChanged) {
            buffer.append(vr.quantityChangeCode);
        }
        buffer.append("_");

//        if (vr.unitPriceOrDiscountSet) {
//            buffer.append(PartPriceConstants.PartChangeType.PART_DISCOUNT_UNIT_PRICE_CHANAGED);
//        }
        buffer.append("_");

        if (vr.dateOverrided) {
            buffer.append(PartPriceConstants.PartChangeType.PART_DATE_CHANGED);
        }
        buffer.append("_");
        // check if there are existing code for PVU changes
        if(StringUtils.contains(item.getChgType(),PartPriceConstants.PartChangeType.PART_PVU_CHANGED)){
            buffer.append(PartPriceConstants.PartChangeType.PART_PVU_CHANGED);
        }

        buffer.append("_");
        // check if there are codes for price change "PU". set by price calculator
        if (StringUtils.contains(item.getChgType(),PartPriceConstants.PartChangeType.PART_PRICE_UPDATED)){
            buffer.append(PartPriceConstants.PartChangeType.PART_PRICE_UPDATED);
        }

        String chgType = buffer.toString();

        // if no change, set null
        if (StringUtils.isBlank(chgType.replaceAll("_", ""))){
            chgType = null;
        }

        item.setChgType(chgType);


    }

    /**
     * @param item
     */
    private void validateRQOverrideStartDate(QuoteLineItem item) throws TopazException {

        String key = createKey(item);
        ValidationResult vr = getValidationResult(key);

        Date startDate = ct.getRQStartDate(key);
        if(null == startDate){
            return;
        }

        Date stdStartDate = null;
        if(item.getSeqNum()>= PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ){
            logContext.debug(this,"std start date :"+item.getPartDispAttr().getStdStartDate());
            stdStartDate = item.getPartDispAttr().getStdStartDate();
        }
        else{
            stdStartDate = item.getOrigStDate();

        }
        if (null == stdStartDate) {
            logContext.info(this,"The standard start date  is null");
            return;
        }

        if(DateUtil.isEqual(stdStartDate,startDate)){
            //vr.dateOverrided = false;   // can't set it to false, beacuse other method (validateRQOverrideEndDate)arealdy set it to true, will add a new field to solve this later
            item.setStartDtOvrrdFlg(false);
        }
        else{
            vr.dateOverrided = true;
            item.setStartDtOvrrdFlg(true);

        }
        if (!DateUtil.isEqual(item.getMaintStartDate(),startDate)) {
        	vr.dateChanged = true;
            item.setMaintStartDate(startDate);
        }

    }

    private void validateRQOverrideEndDate(QuoteLineItem item) throws TopazException {

        String key = createKey(item);
        ValidationResult vr = getValidationResult(key);

        Date endDate = ct.getRQEndDate(key);

        if(null == endDate){
            return;
        }

        Date stdEndDate = null;
        if(item.getSeqNum()>= PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ){
            logContext.debug(this,"std end date :"+item.getPartDispAttr().getStdEndDate());
            stdEndDate = item.getPartDispAttr().getStdEndDate();
        }
        else{
            stdEndDate = item.getOrigEndDate();
        }

        if (null == stdEndDate) {
            logContext.info(this,"The standard end date  is null");
            return;
        }

        if(DateUtil.isEqual(stdEndDate,endDate)){
            //vr.dateOverrided = false;   // can't set it to false, beacuse other method (validateRQOverrideEndDate)arealdy set it to true, will add a new field to solve this later
            item.setEndDtOvrrdFlg(false);
        }
        else{
            vr.dateOverrided = true;
            item.setEndDtOvrrdFlg(true);
        }

        if (!DateUtil.isEqual(item.getMaintEndDate(),endDate)) {
        	vr.dateChanged = true;
            item.setMaintEndDate(endDate);
        }

    }

    private boolean  validateRQQuantity(QuoteLineItem item) throws TopazException {

        String key = createKey(item);

        ValidationResult vr = getValidationResult(key);

        String strQuantity = ct.getPartQty(createKey(item));


        if ((null == strQuantity) ) {
            if (item.getPartQty() != null){
                itemCountChanged = true;
            }

            return false;
        }
        //  if the value is blank, just leave it
        if("".equals(strQuantity)){
            if (item.getPartQty() != null){
                itemCountChanged = true;
            }

            item.setPartQty(null);
            return false;
        }

        int qty = ct.getPartQtyInteger(key);

        if (0 == qty) {

            if(item.getSeqNum()>= PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ){
                deleteLineItemAndConfig(item);
                return true;
            }
            else{
                item.setPartQty(new Integer(0));
                vr.partDeleted = true;
                return false;
            }

        }

        if (item.getOpenQty() != qty) {

            vr.quantityChanged = true;

            if(qty > item.getOpenQty()){

                vr.quantityChangeCode = PartPriceConstants.PartChangeType.PART_QTY_INCREASE;

            }
            else{

                vr.quantityChangeCode = PartPriceConstants.PartChangeType.PART_QTY_DECREASE;
            }
        }

        item.setPartQty(new Integer(qty));

        return false;
    }

    private void validationMTM(QuoteLineItem item) throws TopazException{

    	 String key = createKey(item);
    	item.setMachineType(ct.getMachineType(key));
    	item.setModel(ct.getMachineModel(key));
    	item.setSerialNumber(ct.getMachineSerialNumber(key));
    	item.setNonIBMModel(ct.getNonIBMModel(key));
    	item.setNonIBMSerialNumber(ct.getNonIBMSerialNum(key));
    	//appliance deployment model
		item.setDeployModelOption(ct.getDeployModelOption(key));
		item.setDeployModelId(ct.getDeployModelId(key));
		item.setDeployModelInvalid(ct.isDeployModelValid(key));
    	validateApplianceMtm(item);

    }

}
