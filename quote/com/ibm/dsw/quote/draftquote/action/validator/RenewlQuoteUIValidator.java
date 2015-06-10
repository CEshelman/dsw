
package com.ibm.dsw.quote.draftquote.action.validator;

import java.sql.Date;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.action.PostPartPriceTabAction;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract.LineItemParameter;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code></code> class is
 *
 *
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: Jun 25, 2007
 */
public class RenewlQuoteUIValidator extends PartPriceUIValidator {

    public RenewlQuoteUIValidator(PostPartPriceTabAction ppAction) {
        super(ppAction);
    }

    public boolean validate(ProcessContract contract,boolean checkOfferPrice) throws QuoteException {
        super.validate(contract, checkOfferPrice);
        // validate UI data firstly
        PostPartPriceTabContract ct = (PostPartPriceTabContract) contract;

        //hashmap for containing error messages
        HashMap vMap = new HashMap();
        if ( !ct.isRqEditable()) {
            logContext.debug(this, "Renewal Qutoe, but not editable");
            return true;
        }

        HashMap items = ct.getItems();
        Iterator it = items.values().iterator();

        while (it.hasNext()) {

            LineItemParameter lineItem = (LineItemParameter) it.next();

            if(!validateQuantity(lineItem,vMap,ct)){
                return false;
            }

            //check if the start/end date is valid

            if (!checkRQOverrideDate(lineItem, vMap, ct)) {
                return false;
            }

            QuoteHeader header = this.quote.getQuoteHeader();
            // check if something changed in UI, if yes, check if the reason
            // is input
            if (!header.isFCTQuote() && !checkPartInfoChanged(lineItem, vMap, ct)) {
                return false;
            }

			if (!this.validationMachType(lineItem, vMap, ct)) {
				return false;
			}

			if (!this.validationMachModel(lineItem, vMap, ct)) {
				return false;
			}

			if (!this.validationSerialNum(lineItem, vMap, ct)) {
				return false;
			}
			
			if (!this.validateApplncQty(lineItem, vMap, ct)) {
				return false;
			}
        }

        validateTotalPartNumber(ct);

        logContext.debug(this, "Total line items in UI:" + ct.getItems().values().size());
        //loadQuote(ct);

        return true;
    }


    protected void validateTotalPartNumber(PostPartPriceTabContract contract){
    	List lineItems = quote.getLineItemList();

    	int totalPartNumber = 0;
    	if(lineItems != null){
    	    totalPartNumber = lineItems.size();
    	}

    	int limit = PartPriceConfigFactory.singleton().getElaLimits();

    	//Below if condition check ensures following:
    	//1. If the original renewal quote has more than maximum parts, allow user to edit the renewal quote
    	//2. User is not allowed to add any parts if current number of parts already hax maximu number
    	if(contract.isAddDuplicatePart() && (totalPartNumber >= limit)){
    	    contract.setExceedLimit(true);
    	}
    }

    private boolean checkRQOverrideDate(LineItemParameter lineItem, HashMap vMap, PostPartPriceTabContract contract) {

        boolean isValid = true;

        Date rqStartDate = null;
        Date rqEndDate = null;

        if (!DateHelper.validateDate(lineItem.rqOvrdStartYear, lineItem.rqOvrdStartMonth, lineItem.rqOvrdStartDay)) {

            logContext.debug(this, "Start date is not valid : " + lineItem.rqOvrdStartYear + "-"
                    + lineItem.rqOvrdStartMonth + "-" + lineItem.rqOvrdStartDay);

            addStartEndDateInValidMsg(true, lineItem, vMap, contract);

            return false;
        }
        if (!DateHelper.validateDate(lineItem.rqOvrdEndYear, lineItem.rqOvrdEndMonth, lineItem.rqOvrdEndDay)) {

            logContext.debug(this, "End date is not valid : " + lineItem.rqOvrdEndYear + "-" + lineItem.rqOvrdEndMonth
                    + "-" + lineItem.rqOvrdEndDay);

            addStartEndDateInValidMsg(false, lineItem, vMap, contract);

            return false;
        }

        rqStartDate = lineItem.getRQStartDate();
        rqEndDate = lineItem.getRQEndDate();

        int index = lineItem.key.indexOf("_");
        String partNum = lineItem.key.substring(0, index);
        String strSeqNum = lineItem.key.substring(index + 1);

        QuoteLineItem qteLineItem = quote.getLineItem(partNum, Integer.parseInt(strSeqNum));
        boolean isFutureStartDateAllowed = false;
        if(qteLineItem != null){
        	isFutureStartDateAllowed = PartPriceConfigFactory.singleton().isFutureStartDateAllowed(qteLineItem.getRevnStrmCode());
        }

        if(!isFutureStartDateAllowed && DateUtil.isDateAfterToday(rqStartDate)){
        	addStartDateInFutureInValidMsg(lineItem, vMap, contract);
        	return false;
        }

        // if a part is a FTL part , the first day /last day rule will not be applied
        if (!lineItem.isFTL) {

            if (!DateUtil.isFirstDayOfMonth(rqStartDate)) {
                logContext.debug(this, "Start Date " + rqStartDate + " is not the first of month ,line item "
                        + lineItem.key);
                addStartEndDateInValidMsg(true, lineItem, vMap, contract);
                return false;
            }

            if (!DateUtil.isLastDayOfMonth(rqEndDate)) {
                addStartEndDateInValidMsg(false, lineItem, vMap, contract);
                return false;
            }
        }

        // check duration
        if ((rqStartDate != null) && (rqEndDate != null)) {

            if (lineItem.isFTL) {

                int weeks = DateUtil.calculateWeeks(rqStartDate, rqEndDate);

                if (weeks < 1) {
                    isValid = false;
                }

            } else {

                int month = DateUtil.calculateFullCalendarMonths(rqStartDate, rqEndDate);
                logContext.debug(this, "rq start date " + rqStartDate);
                logContext.debug(this, "rq end date " + rqEndDate);
                logContext.debug(this, "full calendar month is " + month);
                if (month < 1 || month > 12) {
                    isValid = false;

                }

            }
            if (!isValid) {

                FieldResult fieldResult = new FieldResult();
                fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
                        DraftQuoteMessageKeys.RQ_OVRD_DATE_DURATION_MSG);
                fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.SOFTWARE_MAINTENANCE_RQ);
                vMap.put(PartPriceViewKeys.PREFIX + lineItem.key + DraftRQParamKeys.ovrdDtStartDaySuffix, fieldResult);

                addToValidationDataMap(contract, vMap);
            }

        }
        return isValid;

    }

    private void addStartEndDateInValidMsg(boolean isStartDate, LineItemParameter lineItem, HashMap vMap,
            PostPartPriceTabContract contract) {

        String msgKey = "";
        if (isStartDate) {
            msgKey = DraftQuoteMessageKeys.RQ_OVRD_INVALID_START_DATE_MSG;
        } else {
            msgKey = DraftQuoteMessageKeys.RQ_OVRD_INVALID_END_DATE_MSG;
        }

        FieldResult fieldResult = new FieldResult();

        fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, msgKey);

        if (isStartDate) {
            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.OVR_START_DATE);
            vMap.put(PartPriceViewKeys.PREFIX + lineItem.key + DraftRQParamKeys.ovrdDtStartDaySuffix, fieldResult);
        } else {
            fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.OVR_END_DATE);
            vMap.put(PartPriceViewKeys.PREFIX + lineItem.key + DraftRQParamKeys.ovrdDtEndDaySuffix, fieldResult);
        }

        addToValidationDataMap(contract, vMap);
    }

    private void addStartDateInFutureInValidMsg(LineItemParameter lineItem, HashMap vMap, PostPartPriceTabContract contract){
    	FieldResult fieldResult = new FieldResult();
    	fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, DraftQuoteMessageKeys.NO_FUTURE_MAINT_START_DATE);
    	fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, PartPriceViewKeys.OVR_START_DATE);

    	vMap.put(PartPriceViewKeys.PREFIX + lineItem.key + DraftRQParamKeys.ovrdDtStartDaySuffix, fieldResult);
    	addToValidationDataMap(contract, vMap);
    }

    private boolean checkPartInfoChanged(LineItemParameter lineItem, HashMap map, PostPartPriceTabContract ct) {

        logContext.debug(this, "Begine to check if part info changed");

        boolean changeReasonIsNotEmpty = (lineItem.rqChangeReason != null) && !"".equals(lineItem.rqChangeReason);
        int seqNum = parseSeqNum(lineItem);
        if (changeReasonIsNotEmpty) {
            return true;
        } else if (seqNum >= PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ) {
            // part is manually added
            addRQCommentInValidMsg(lineItem, map, ct);
            return false;

        } else {
            if (quantityIsZero(lineItem.quantity)) {
                logContext.debug(this, "Quantity is set to 0");
                addRQCommentInValidMsg(lineItem, map, ct);
                return false;
            }
            if (!lineItem.quantity.equals(lineItem.rqPrevQty)) {
                logContext.debug(this, "Quantity Changed: " + lineItem.rqPrevQty + "->" + lineItem.quantity);
                addRQCommentInValidMsg(lineItem, map, ct);
                return false;
            }
            if (isRQDateChanged(lineItem)) {
                logContext.debug(this, "RQ Date changed: " + lineItem.rqPrevStartDate + "->"
                        + lineItem.getRQStartDate());
                logContext.debug(this, "RQ Date changed: " + lineItem.rqPrevEndDate + "->" + lineItem.getRQEndDate());
                addRQCommentInValidMsg(lineItem, map, ct);
                return false;
            }

        }

        return true;
    }

    private boolean isRQDateChanged(LineItemParameter lineItem) {
        Date currentStartDate = lineItem.getRQStartDate();
        Date preStartDate = DateUtil.parseDate(lineItem.rqPrevStartDate);
        if (!DateUtil.isEqual(currentStartDate, preStartDate)) {
            return true;
        }
        Date curEndDate = lineItem.getRQEndDate();
        Date preEndDate = DateUtil.parseDate(lineItem.rqPrevEndDate);
        if (!DateUtil.isEqual(curEndDate, preEndDate)) {
            return true;
        }
        return false;
    }

    private boolean isRQOvrdUnitPriceChanged(LineItemParameter lineItem) {

        String curPrice = lineItem.overridePrice;
        String prePrice = lineItem.prevOvrrdPrice;
        double curOvrdPrice = parseDouble(lineItem.overridePrice);
        double preOvrdPrice = parseDouble(lineItem.prevOvrrdPrice);

        return !DecimalUtil.isEqual(curOvrdPrice, preOvrdPrice);

    }

    private double parseDouble(String value) {
        // the value has been validated
        if ((value == null) || ("".equals(value))) {
            return 0.0;
        } else {
            try {
                return numberFormatter.parse(value).doubleValue();
            } catch (ParseException e) {
                return 0;
            }
        }
    }

    private boolean isRQDiscountChanged(LineItemParameter lineItem) {
        double curDiscount = parseDouble(lineItem.discountPercent);
        double preDiscount = parseDouble(lineItem.prevDiscount);
        return !DecimalUtil.isEqual(curDiscount, preDiscount);

    }

    private boolean quantityIsZero(String qty) {
        return "0".equals(qty);
    }

    private int parseSeqNum(LineItemParameter lineItem) {
        int pos = lineItem.key.indexOf("_");
        return Integer.valueOf(lineItem.key.substring(pos + 1)).intValue();
    }

    private void addRQCommentInValidMsg(LineItemParameter lineItem, HashMap vMap, PostPartPriceTabContract contract) {

        int pos = lineItem.key.indexOf("_");
        int seqNum = parseSeqNum(lineItem);

        String messageKey = "";
        String fieldLabel = "";
        String fieldSuffix = "";

        if (seqNum >= PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ) {
            // part is manually added

            logContext.debug(this, "Part is manually added ");
            messageKey = DraftQuoteMessageKeys.REASON_FOR_INSERT_REQUIRED;
            fieldLabel = PartPriceViewKeys.REASON_FOR_ADDITION;
            fieldSuffix = DraftRQParamKeys.RQ_REASON_FOR_ADD_SUFFIX;

        } else {

            logContext.debug(this, "Part is originally added from RQ Quote ");
            if (quantityIsZero(lineItem.quantity)) {

                messageKey = DraftQuoteMessageKeys.REASON_FOR_DELETE_REQUIRED;
                fieldLabel = PartPriceViewKeys.REASON_FOR_DELETION;
                fieldSuffix = DraftRQParamKeys.RQ_REASON_FOR_DEL_SUFFIX;

            } else {

                messageKey = DraftQuoteMessageKeys.REASON_FOR_CHANGE_REQUIRED;
                fieldLabel = PartPriceViewKeys.REASON_FOR_CHG;
                fieldSuffix = DraftRQParamKeys.RQ_REASON_FOR_CHG_SUFFIX;
            }
        }

        FieldResult fieldResult = new FieldResult();
        fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, messageKey);
        fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, fieldLabel);
        vMap.put(PartPriceViewKeys.PREFIX + lineItem.key + fieldSuffix, fieldResult);

        addToValidationDataMap(contract, vMap);
    }

}
