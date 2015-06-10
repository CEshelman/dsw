package com.ibm.dsw.quote.common.process;

import is.domainx.User;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.MigrateRequest;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteAccess;
import com.ibm.dsw.quote.common.domain.QuoteAccessFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.SpecialBidCondition;
import com.ibm.dsw.quote.common.service.QuoteTimestampService;
import com.ibm.dsw.quote.common.util.MigrationReqValidationRule;
import com.ibm.dsw.quote.common.util.QuoteValidationRule;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteCapabilityProcess_Impl</code> class is abstract
 * implementation of QuoteCapacityProcess.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 3, 2007
 */
public abstract class QuoteCapabilityProcess_Impl extends TopazTransactionalProcess implements QuoteCapabilityProcess {

    LogContext logContext = LogContextFactory.singleton().getLogContext();

    /**
     */

    public Map validateSubmissionForApproval(QuoteUserSession user, Quote quote, Cookie cookie) throws QuoteException {
        Map result = null;
        try {
            this.beginTransaction();
            QuoteValidationRule rule = QuoteValidationRule.createRule(user, quote, cookie);
            result = rule.validateSubmissionForApproval();
            this.commitTransaction();
            
        } catch (Exception e) {
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        return result;
    }
    
    public  boolean isSubmitPGSLevel0SPBid(QuoteUserSession user, Quote quote, Cookie cookie)throws QuoteException{
    	boolean result = false;
    	try {
            this.beginTransaction();
            QuoteValidationRule rule = QuoteValidationRule.createRule(user, quote, cookie);
            result = rule.isSubmitPGSLevel0SPBid(user, quote, cookie);
            this.commitTransaction();
            
        } catch (Exception e) {
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        return result;
    }

    /**
     * @throws QuoteException 
     *  
     */

    public boolean isSystemInitiatedSpBid(Quote quote, String userId) throws QuoteException {
        // validate rules:
        // 1. Any part on the quote has a discount percent specified
        // 2. Any part on the quote has an override unit price specified
        // 3. Maintenance coverage on any line item on the quote has been
        // extended
        // beyond 2 years and the country is in AG, LA or EMEA (any region
        // except AP; AP does not have this rule)
        // 4. Any part is restricted in the restriction tables
        // 5. Part belongs to a part groups that is flagged as forcing a special
        // bid.
        // 6. If override start or end date is set

        SpecialBidCondition spBidCondition = new SpecialBidCondition(quote, userId);

        return spBidCondition.hasDiscountOrOverridePrice() || spBidCondition.isMaintNotInAPOverDefaultPeriod()
                || spBidCondition.hasRestrictPart() || spBidCondition.isPartGroupRequireSpBid();
    }

    /**
     *  
     */

    public Map validateForOrder(QuoteUserSession user, Quote quote, Cookie cookie) throws QuoteException {
        Map validateResult = null;
        try {
            this.beginTransaction();
            QuoteValidationRule rule = QuoteValidationRule.createRule(user, quote, cookie);
            validateResult = rule.validateOrder();
            this.commitTransaction();
            
        } catch (Exception e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        return validateResult;
    }

    /**
     *  
     */

    public Map validateTouForOrder(QuoteUserSession user, Quote quote, Cookie cookie) throws QuoteException {
        Map validateResult = null;
        try {
            this.beginTransaction();
            QuoteValidationRule rule = QuoteValidationRule.createRule(user, quote, cookie);
            validateResult = rule.validateTou(true);
            this.commitTransaction();
            
        } catch (Exception e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        return validateResult;
    }
    
    public Map validateForSubmissionAsFinal(QuoteUserSession user, Quote quote, Cookie cookie) throws QuoteException {
        Map validateResult = null;
        try {
            this.beginTransaction();
            QuoteValidationRule validationRule = QuoteValidationRule.createRule(user, quote, cookie);
            validateResult = validationRule.validateSubmissionAsFinal();
            this.commitTransaction();
            
        } catch (Exception e) {
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        return validateResult;

    }
    
    public Map validateFCTToPAMigrationRequest(QuoteUserSession user, MigrateRequest migrateRequest) throws QuoteException {
        Map validateResult = null;
        try {
            this.beginTransaction();
            MigrationReqValidationRule validationRule =  new MigrationReqValidationRule(migrateRequest,user);;
            validateResult = validationRule.validateFCTToPAMigrationRequest();
            this.commitTransaction();
            
        } catch (Exception e) {
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        return validateResult;

    }

    public int validateEditRQ(User user, QuoteUserSession quoteUserSession, String renwlQuoteNum)
            throws QuoteException {
        logContext.debug(this, "To validate the user's accsss level.");
        if (!this.audienceHasSubmitterAccess(user)) {
            logContext.debug(this, "Validation is failed, the user's accsss level isn't submitter.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.NO_ACCESS;
        }
        Map statusMap = this.getRnwlQuoteStatus(user, quoteUserSession, renwlQuoteNum);
        
        logContext.debug(this, "To validate if the user has privilege");
        
        boolean isUserValid = ((Boolean) statusMap.get(IS_USER_VALID)).booleanValue();
        
        if (!isUserValid) {
            logContext.debug(this, "Validation is failed, user have no privilege.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.NO_ACCESS;
        }
            
        boolean isEditable = ((Boolean) statusMap.get(EDIT_RQ)).booleanValue();
        logContext.debug(this, "To validate if the quote is editable.");
        if (!isEditable) {
            logContext.debug(this, "Validation is failed, The quote isn't editable.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.QT_NOT_EDITABLE;
        }
        Date rqModDate = (Date) statusMap.get(RQ_MOD_DATE);
        Date rqStatModDate = (Date) statusMap.get(RQ_STATUS_MOD_DATE);
        String sapDocNum = (String) statusMap.get(RQ_IDOC_NUM);
        logContext.debug(this, "To validate if the quote has not recently been updated in SAP.");
        if (!this.checkSapConsistent(renwlQuoteNum, sapDocNum, rqModDate, rqStatModDate)) {
            logContext.debug(this, "Validation is failed, the mod date isn't matched.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.QT_NOT_MATCH_WITH_SAP;
        }
        return QuoteConstants.RNWLQT_VALIDATION_RESULT.SUCCESS;
    }
    
    public int validateEditGPExprdRQ(User user, QuoteUserSession quoteUserSession, String renwlQuoteNum)
            throws QuoteException {
        logContext.debug(this, "To validate the user's accsss level.");
        if (!this.audienceHasSubmitterAccess(user)) {
            logContext.debug(this, "Validation is failed, the user's accsss level isn't submitter.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.NO_ACCESS;
        }
        Map statusMap = this.getRnwlQuoteStatus(user, quoteUserSession, renwlQuoteNum);
        
        logContext.debug(this, "To validate if the user has privilege");
        
        boolean isUserValid = ((Boolean) statusMap.get(IS_USER_VALID)).booleanValue();
        
        if (!isUserValid) {
            logContext.debug(this, "Validation is failed, user have no privilege.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.NO_ACCESS;
        }
        
        boolean isEditable = ((Boolean) statusMap.get(EDIT_GPE_RQ)).booleanValue();
        logContext.debug(this, "To validate if the quote is GPE.");
        if (!isEditable) {
            logContext.debug(this, "Validation is failed, The quote isn't GPE.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.QT_NOT_GPE;
        }
        Date rqModDate = (Date) statusMap.get(RQ_MOD_DATE);
        Date rqStatModDate = (Date) statusMap.get(RQ_STATUS_MOD_DATE);
        String sapDocNum = (String) statusMap.get(RQ_IDOC_NUM);
        logContext.debug(this, "To validate if the quote has not recently been updated in SAP.");
        if (!this.checkSapConsistent(renwlQuoteNum, sapDocNum, rqModDate, rqStatModDate)) {
            logContext.debug(this, "Validation is failed, the mod date isn't matched.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.QT_NOT_MATCH_WITH_SAP;
        }
        return QuoteConstants.RNWLQT_VALIDATION_RESULT.SUCCESS;
    }

    public int validateEditRQStatus(User user, QuoteUserSession quoteUserSession, String renwlQuoteNum)
            throws QuoteException {
        logContext.debug(this, "To validate the user's accsss level.");
        if (!this.audienceHasSubmitterAccess(user)) {
            logContext.debug(this, "Validation is failed, the user's accsss level isn't submitter.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.NO_ACCESS;
        }
        Map statusMap = this.getRnwlQuoteStatus(user, quoteUserSession, renwlQuoteNum);
        
        logContext.debug(this, "To validate if the user has privilege");
        
        boolean isUserValid = ((Boolean) statusMap.get(IS_USER_VALID)).booleanValue();
        
        if (!isUserValid) {
            logContext.debug(this, "Validation is failed, user have no privilege.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.NO_ACCESS;
        }
        
        boolean isEditable = ((Boolean) statusMap.get(EDIT_RQ_STATUS)).booleanValue();
        logContext.debug(this, "To validate if the quote status is editable.");
        if (!isEditable) {
            logContext.debug(this, "Validation is failed, The quote status isn't editable.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.QT_NOT_EDITABLE;
        }
        Date rqModDate = (Date) statusMap.get(RQ_MOD_DATE);
        Date rqStatModDate = (Date) statusMap.get(RQ_STATUS_MOD_DATE);
        String sapDocNum = (String) statusMap.get(RQ_IDOC_NUM);
        logContext.debug(this, "To validate if the quote has not recently been updated in SAP.");
        if (!this.checkSapConsistent(renwlQuoteNum, sapDocNum, rqModDate, rqStatModDate)) {
            logContext.debug(this, "Validation is failed, the mod date isn't matched.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.QT_NOT_MATCH_WITH_SAP;
        }
        return QuoteConstants.RNWLQT_VALIDATION_RESULT.SUCCESS;
    }

    public int validateEditRQSalesTracking(User user, QuoteUserSession quoteUserSession, String renwlQuoteNum)
            throws QuoteException {
        logContext.debug(this, "To validate the user's accsss level.");
        if (!this.audienceHasSubmitterAccess(user)) {
            logContext.debug(this, "Validation is failed, the user's accsss level isn't submitter.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.NO_ACCESS;
        }
        Map statusMap = this.getRnwlQuoteStatus(user, quoteUserSession, renwlQuoteNum);
        
        logContext.debug(this, "To validate if the user has privilege");
        
        boolean isUserValid = ((Boolean) statusMap.get(IS_USER_VALID)).booleanValue();
        
        if (!isUserValid) {
            logContext.debug(this, "Validation is failed, user have no privilege.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.NO_ACCESS;
        }
        
        boolean isEditable = ((Boolean) statusMap.get(EDIT_RQ_ST)).booleanValue();
        logContext.debug(this, "To validate if the quote's sales tracking is editable.");
        if (!isEditable) {
            logContext.debug(this, "Validation is failed, The quote's sales tracking isn't editable.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.QT_SALES_TRACKING_NOT_EDITABLE;
        }
        Date rqModDate = (Date) statusMap.get(RQ_MOD_DATE);
        Date rqStatModDate = (Date) statusMap.get(RQ_STATUS_MOD_DATE);
        String sapDocNum = (String) statusMap.get(RQ_IDOC_NUM);
        logContext.debug(this, "To validate if the quote has not recently been updated in SAP.");
        if (!this.checkSapConsistent(renwlQuoteNum, sapDocNum, rqModDate, rqStatModDate)) {
            logContext.debug(this, "Validation is failed, the mod date isn't matched.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.QT_NOT_MATCH_WITH_SAP;
        }
        return QuoteConstants.RNWLQT_VALIDATION_RESULT.SUCCESS;
    }

    public int validateOrderRQ(User user, QuoteUserSession quoteUserSession, String renwlQuoteNum)
            throws QuoteException {
        logContext.debug(this, "To validate the user's accsss level.");
        if (!this.audienceHasSubmitterAccess(user)) {
            logContext.debug(this, "Validation is failed, the user's accsss level isn't submitter.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.NO_ACCESS;
        }
        Map statusMap = this.getRnwlQuoteStatus(user, quoteUserSession, renwlQuoteNum);
        
//        logContext.debug(this, "To validate if the user has privilege");
//        
//        boolean isUserValid = ((Boolean) statusMap.get(IS_USER_VALID)).booleanValue();
//        
//        if (!isUserValid) {
//            logContext.debug(this, "Validation is failed, user have no privilege.");
//            return QuoteConstants.RNWLQT_VALIDATION_RESULT.NO_ACCESS;
//        }
        
        boolean isOrderable = ((Boolean) statusMap.get(ORDER_RQ)).booleanValue();
        logContext.debug(this, "To validate if the quote is orderable.");
        if (!isOrderable) {
            logContext.debug(this, "Validation is failed, The quote isn't orderable.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.QT_NOT_ORDERABLE;
        }
        Date rqModDate = (Date) statusMap.get(RQ_MOD_DATE);
        Date rqStatModDate = (Date) statusMap.get(RQ_STATUS_MOD_DATE);
        String sapDocNum = (String) statusMap.get(RQ_IDOC_NUM);
        logContext.debug(this, "To validate if the quote has not recently been updated in SAP.");
        if (!this.checkSapConsistent(renwlQuoteNum, sapDocNum, rqModDate, rqStatModDate)) {
            logContext.debug(this, "Validation is failed, the mod date isn't matched.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.QT_NOT_MATCH_WITH_SAP;
        }
        return QuoteConstants.RNWLQT_VALIDATION_RESULT.SUCCESS;
    }
    
    public int validateCreateRQSpeclBid(User user, QuoteUserSession quoteUserSession, String renwlQuoteNum)
            throws QuoteException {
        logContext.debug(this, "To validate the user's accsss level.");
        if (!this.audienceHasSubmitterAccess(user)) {
            logContext.debug(this, "Validation is failed, the user's accsss level isn't submitter.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.NO_ACCESS;
        }
        Map statusMap = this.getRnwlQuoteStatus(user, quoteUserSession, renwlQuoteNum);
        
        logContext.debug(this, "To validate if the user has privilege");
        
        boolean isUserValid = ((Boolean) statusMap.get(IS_USER_VALID)).booleanValue();
        
        if (!isUserValid) {
            logContext.debug(this, "Validation is failed, user have no privilege.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.NO_ACCESS;
        }
        
        boolean canCreateRQSpeclBid = ((Boolean) statusMap.get(CREATE_RQ_SPECL_BID)).booleanValue();
        logContext.debug(this, "To validate if special bid can be created against this quote.");
        if (!canCreateRQSpeclBid) {
            logContext.debug(this, "Validation is failed, special bid can't be created against this quote.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.SB_NOT_CREATABLE;
        }
        Date rqModDate = (Date) statusMap.get(RQ_MOD_DATE);
        Date rqStatModDate = (Date) statusMap.get(RQ_STATUS_MOD_DATE);
        String sapDocNum = (String) statusMap.get(RQ_IDOC_NUM);
        logContext.debug(this, "To validate if the quote has not recently been updated in SAP.");
        if (!this.checkSapConsistent(renwlQuoteNum, sapDocNum, rqModDate, rqStatModDate)) {
            logContext.debug(this, "Validation is failed, the mod date isn't matched.");
            return QuoteConstants.RNWLQT_VALIDATION_RESULT.QT_NOT_MATCH_WITH_SAP;
        }
        return QuoteConstants.RNWLQT_VALIDATION_RESULT.SUCCESS;
    }
    
    public Map getDraftQuoteActionButtonsRule(QuoteUserSession user, Quote quote, QuoteUserSession salesRep) {
        QuoteValidationRule validationRule = null;
        try {
            validationRule = QuoteValidationRule.createRule(user, quote, null);
        } catch (Exception e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
        }
        return validationRule.getDraftQuoteActionButtonsRule(salesRep);
    }

    /**
     * @param user
     * @return
     */
    protected boolean audienceHasSubmitterAccess(User user) {
        return user.getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_SUBMITTER;
    }

    protected boolean checkSapConsistent(String rnwlQuoteNum, String sapIDocNum, Date rqModDate, Date rqStatModDate) {
        QuoteTimestampService ws = new QuoteTimestampService();
        boolean isSuccessfull = false;
        try {
            isSuccessfull = ws.execute(rnwlQuoteNum, sapIDocNum, rqModDate, rqStatModDate);
        } catch (RemoteException e) {
            logContext.error(this, "Error occurred when calling QuoteTimeStamp service: "
                    + LogThrowableUtil.getStackTraceContent(e));
        }
        return isSuccessfull;
    }

    protected Map getRnwlQuoteStatus(User user, QuoteUserSession quoteUserSession, String renwlQuoteNum) throws QuoteException {
        String userList = getDirectReportsList(user, quoteUserSession);
        Map statusMap = this.getRnwlQuoteStatus(renwlQuoteNum, userList, 0, 1, QuoteConstants.RNWL_QUOTE_DURATION, true);
        return statusMap;
    }

    protected String getDirectReportsList(User user, QuoteUserSession quoteUserSession) {
        if (quoteUserSession != null) {
            ArrayList reportsList = quoteUserSession.getReportingHierarchy();
            StringBuffer sb = new StringBuffer(quoteUserSession.getEmailAddress());
            for (int i = 0; reportsList != null && i < reportsList.size(); i++) {
                String email = reportsList.get(i) == null ? "" : reportsList.get(i).toString();
                sb.append(";").append(StringUtils.lowerCase(email));
            }
            return sb.toString();
        } else {
            String email = user.getEmail() == null ? "" : user.getEmail().toLowerCase();
            return email;
        }
    }

    /*public boolean validateLineItemRevnStrm(Quote quote) throws QuoteException{
        boolean isValid = true;
        if (quote.getLineItemList() != null) {
            Iterator iterator = quote.getLineItemList().iterator();
            while (iterator.hasNext()) {
                QuoteLineItem item = (QuoteLineItem) iterator.next();
                if (!(PartPriceConstants.RevenueStreamCode.SFXTLM.equals(item.getRevnStrmCode())
                        || PartPriceConstants.RevenueStreamCode.OS_SUPT.equals(item.getRevnStrmCode())
                        || PartPriceConstants.RevenueStreamCode.OSNOSPT.equals(item.getRevnStrmCode()) || PartPriceConstants.RevenueStreamCode.RNWMNTSP
                        .equals(item.getRevnStrmCode()))) {
                    isValid = false;
                    break;
                }
            }
        }
        logContext.debug(this, "validateLineItemRevnStrm return flag : " + isValid);
        return isValid;
    }*/
    
    public boolean validateLineItemNOTReferRQ(Quote quote) throws QuoteException{
        boolean isValid = false;
        Iterator iterator = quote.getLineItemList().iterator();
        if(!(quote.getLineItemList().size()>0)){
            isValid = true;
        } else {
            while (iterator.hasNext()) {
                QuoteLineItem item = (QuoteLineItem) iterator.next();
                if (StringUtils.isBlank(item.getRenewalQuoteNum())) {
                    isValid = true;
                    break;
                }
            }
        }
        logContext.debug(this, "validateLineItemNOTReferRQ return flag : " + isValid);
        return isValid;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.process.QuoteCapabilityProcess#getSubmittedQuoteActionButtonsRule(is.domainx.User, com.ibm.dsw.quote.common.domain.Quote, java.util.List)
     */
    public Map getSubmittedQuoteActionButtonsRule(QuoteUserSession user, Quote quote) {
    	Map ruleMap = null;
        QuoteValidationRule validationRule = null;
        try {
            this.beginTransaction();
            validationRule = QuoteValidationRule.createRule(user, quote, null);
            ruleMap = validationRule.getSubmittedQuoteActionButtonsRule();
            this.commitTransaction();
        } catch (Exception te) {
            logContext.debug(this, te.getMessage());
        } finally {
            this.rollbackTransaction();
        }
        return ruleMap;
    }
    
    public QuoteAccess getRnwlQuoteAccess(String userId, String renwlQuoteNum) throws QuoteException {
        Map statusMap = null;
        QuoteAccess access = new QuoteAccess();
        
        statusMap = this.getRnwlQuoteStatus(renwlQuoteNum, userId, 1, 1, QuoteConstants.RNWL_QUOTE_DURATION, false);
        
        boolean quoteEditable = ((Boolean) statusMap.get(EDIT_RQ)).booleanValue();
        boolean quoteStatusEditable = ((Boolean) statusMap.get(EDIT_RQ_STATUS)).booleanValue();
        boolean salesTrackingEditable = ((Boolean) statusMap.get(EDIT_RQ_ST)).booleanValue();
        boolean quoteOrderable = ((Boolean) statusMap.get(ORDER_RQ)).booleanValue();
        boolean canBeAddedToSQ = ((Boolean) statusMap.get(ADD_RQ_TO_SQ)).booleanValue();
        boolean canCreateRQSpeclBid = ((Boolean) statusMap.get(CREATE_RQ_SPECL_BID)).booleanValue();
        boolean isSalesRep = ((Boolean) statusMap.get(IS_SALES_REP)).booleanValue();
        boolean isQuotingRep = ((Boolean) statusMap.get(IS_QUOTING_REP)).booleanValue();
        boolean isGPEEditable = ((Boolean) statusMap.get(EDIT_GPE_RQ)).booleanValue();
        
        access.setCanEditRQ(quoteEditable);
        access.setCanUpdateRQStatus(quoteStatusEditable);
        access.setCanEditRQSalesInfo(salesTrackingEditable);
        access.setCanOrderRQ(quoteOrderable);
        access.setCanBeAddedToSQ(canBeAddedToSQ);
        access.setCanCreateRQSpeclBid(canCreateRQSpeclBid);
        access.setSalesRep(isSalesRep);
        access.setQuotingRep(isQuotingRep);
        access.setCanEditGPExprdRQ(isGPEEditable);
        
        return access;
    }
    
    public Map getRnwlQuoteStatus(String quoteNum, String userIdList, int qaNeeded, int steNeeded, int duration,
            boolean checkSalesRep) throws QuoteException {

        Map results = null;

        try {
            //begin topaz transaction
            
            results = QuoteAccessFactory.singleton().getRenwlQuoteStatus(quoteNum, userIdList, qaNeeded, steNeeded,
                    duration, checkSalesRep);
            
            //end topaz transaction
        } catch (TopazException te) {
            logContext.debug(this, te.getMessage());
            throw new QuoteException(te);
        } 

        return results;
    }
    
   
    
}
