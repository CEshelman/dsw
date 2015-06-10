package com.ibm.dsw.quote.submittedquote.action;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.ErrorKeys;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.common.config.QuoteMessageKeys;
import com.ibm.dsw.quote.common.config.QuoteParamKeys;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcess;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcessFactory;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteViewKeys;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedPartPriceContract;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedQuoteBaseContract;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcess;
import com.ibm.dsw.quote.submittedquote.process.SubmittedQuoteProcessFactory;
import com.ibm.dsw.quote.submittedquote.util.SubmittedQuoteRequest;
import com.ibm.dsw.quote.submittedquote.util.SubmittedQuoteResponse;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 8, 2007
 */

public class SubmittedQuotePartPricingTabAction extends SubmittedQuoteBaseAction {
    private static final LogContext logger = LogContextFactory.singleton().getLogContext();

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.action.SubmittedQuoteBaseAction#getSubmittedQuoteDetail(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected Quote getSubmittedQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler)
            throws QuoteException {

        SubmittedPartPriceContract ct = (SubmittedPartPriceContract) contract;

        if (null != ct.getLineItemDetailFlag()) {
            setDisplayDetailCookie(ct);
        }
        
        if(null != ct.getBrandDetailFlag()){
            setSubTotalDetailCookie(ct);
        }
        
        try {

            SubmittedQuoteProcess process = SubmittedQuoteProcessFactory.singleton().create();
            SubmittedQuoteRequest request = new SubmittedQuoteRequest(quote, ct);
            quote.setPgsAppl(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(ct.getQuoteUserSession().getAudienceCode()));
            SubmittedQuoteResponse response = process.getSubmittedPartPriceInfo(request);
            
            if (quote.getQuoteHeader().getSpeclBidFlag()==1) {
	            SpecialBidProcess specialBidProcess = SpecialBidProcessFactory.singleton().create();
	            SpecialBidInfo sbInfo = specialBidProcess.getSpecialBidInfo(quote.getQuoteHeader().getWebQuoteNum());
	            logContext.debug(this, "Dump SpecialBidInfo: " + sbInfo);
	            quote.setSpecialBidInfo(sbInfo);
            }
            if (request.needUpdateLineItemDate()) {

                if (response.isBluePageUnavailable()) {
                    String msg = getI18NString(SubmittedQuoteMessageKeys.MSG_BLUE_PAGE_UNAVAILABLE,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, ct.getLocale());

                    MessageBean mBean = MessageBeanFactory.create();
                    mBean.addMessage(msg, MessageBeanKeys.INFO);
                    handler.setMessage(mBean);
                    logContext.debug(this, "Blue page is unavailable now, the msg is: " + msg);
                }
                else if (response.isWebServiceInputInvalid()) {
                    String msg = getI18NString(SubmittedQuoteMessageKeys.INVLD_QT_DATA_MSG,
                            MessageKeys.BUNDLE_APPL_I18N_QUOTE, ct.getLocale());
                    
                    MessageBean mBean = MessageBeanFactory.create();
                    mBean.addMessage(msg, MessageBeanKeys.INFO);
                    handler.setMessage(mBean);
                    logContext.debug(this, "Invalid web service input data, the msg is " + msg);
                }
                else if (response.isUpdateLineItemFailed()) {

                    String msg = getI18NString(SubmittedQuoteMessageKeys.MSG_QUOTE_UPDATE_FAILED,
                            MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, ct.getLocale());
                    //msg = msg + " (" +DateUtil.getCurrentDateForErrorDisplay() + ")";
                    MessageBean mBean = MessageBeanFactory.create();
                    mBean.addMessage(msg, MessageBeanKeys.INFO);
                    handler.setMessage(mBean);
                    logContext.debug(this, "Update Line Item Date Failed ,the msg is " + msg);
                }

            } else {
                if (response.isPriceEngineFailed()) {

                    String msg = getI18NString(ErrorKeys.MSG_PRICE_ENGINE_UNAVAILABLE, I18NBundleNames.ERROR_MESSAGE, ct
                            .getLocale());
                    //msg = msg + " (" +DateUtil.getCurrentDateForErrorDisplay() + ")";
                    MessageBean mBean = MessageBeanFactory.create();
                    mBean.addMessage(msg, MessageBeanKeys.INFO);
                    handler.setMessage(mBean);
                    logContext.debug(this, "Calculate Price failed ,the msg is " + msg);

                }
                
                if(response.isPriceIncreaseFailed()){
                    String msg = getI18NString(SubmittedQuoteMessageKeys.OFFER_PRICE_INCREASE_FAILED,
                            MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, ct.getLocale());
                    
                    MessageBean mBean = MessageBeanFactory.create();
                    mBean.addMessage(msg, MessageBeanKeys.ERROR);
                    handler.setMessage(mBean);
                }
                
            } 

        } catch (QuoteException e) {

            logContext.fatal(this, "Get submitted quote error: "+e.getMessage());
            throw new QuoteException(e);

        }
        
        QuoteHeader header = quote.getQuoteHeader();
        
        String lob = header.getLob().getCode();

        if (header.isSalesQuote()) {

            if (QuoteConstants.LOB_PA.equals(lob) || QuoteConstants.LOB_PAE.equals(lob)) {
                handler.setState(SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_PART_PRICE_TAB_PA_PAE);
            } else if (QuoteConstants.LOB_FCT.equals(lob)) {
                handler.setState(SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_PART_PRICE_TAB_FCT);
            } else if (QuoteConstants.LOB_PPSS.equals(lob)) {
                handler.setState(SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_PART_PRICE_TAB_PPSS);
            } else if(QuoteConstants.LOB_OEM.equals(lob)){
                handler.setState(SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_PART_PRICE_TAB_OEM);
            } else if(QuoteConstants.LOB_SSP.equals(lob)){
                handler.setState(SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_PART_PRICE_TAB_SSP);
            }
        } else if (header.isRenewalQuote()) {
            if (QuoteConstants.LOB_FCT.equals(lob)){
                handler.setState(SubmittedQuoteStateKeys.STATE_SUBMITTED_RQ_PART_PRICE_FCT_TAB);
            }else if(QuoteConstants.LOB_OEM.equals(lob)){
                handler.setState(SubmittedQuoteStateKeys.STATE_SUBMITTED_RQ_PART_PRICE_TAB_OEM);
            }else{   
                handler.setState(SubmittedQuoteStateKeys.STATE_SUBMITTED_RQ_PART_PRICE_TAB);
            }
        }

        return quote;
    }

    /**
     * @param ct
     */
    protected void setDisplayDetailCookie(SubmittedPartPriceContract ct) {
        Cookie cookie = ct.getSqoCookie();
        QuoteCookie.setLineItemDetailFlag(cookie, ct.getLineItemDetailFlag());

    }
    
    protected void setSubTotalDetailCookie(SubmittedPartPriceContract ct) {
        Cookie cookie = ct.getSqoCookie();
        QuoteCookie.setBrandDetailFlag(cookie, ct.getBrandDetailFlag());

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.action.SubmittedQuoteBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {

        return null;
    }

    protected boolean validate(ProcessContract contract){
        if (!super.validate(contract)) {
            return false;
        }
        
        SubmittedPartPriceContract ct = (SubmittedPartPriceContract) contract;
        if(StringUtils.isNotBlank(ct.getUpdateLineItemDateFlag())){
            return validateLineItemDates(ct);
        } else if(StringUtils.isNotBlank(ct.getApplyOfferPriceFlag())){
            return validateOfferPrice(ct);
        }
        
        return true;
    }
    
    protected boolean validateOfferPrice(SubmittedPartPriceContract ct){
        if(StringUtils.isBlank(ct.getOfferPrice())){
        	addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
    	             QuoteMessageKeys.CURRENT_OFFER_NULL_MSG,
    	             PartPriceViewKeys.APPLY_OFFER_TXT,
				     QuoteParamKeys.APPLY_OFFER_INPUT, ct);
        	return false;
        }
        
        Quote quote = null;
        List originalLineItemList = null;
        try{
            quote = loadQuote(ct);
            originalLineItemList = QuoteLineItemFactory.singleton().
                                       findLineItemsByWebQuoteNum(quote.getQuoteHeader().getPriorQuoteNum());
            ct.setQuote(quote);
            ct.setOriginalLineItemList(originalLineItemList);
        }catch(TopazException te){
            logger.error(this, te);
            return false;
        } catch (QuoteException qe){
            logger.error(this, qe);
            return false;
        }
        
        double bidEntitledPrcTot = 0;
        double bidExtendedPrcTot = 0;
        for(Iterator it = originalLineItemList.iterator(); it.hasNext(); ){
            QuoteLineItem qli = (QuoteLineItem)it.next();
            //Added by Randy Nov 17 to fix ebiz Notes://ltsgdb001b/85256B83004B1D94/CA30E8393BC22D28482573A7000E50E0/8347D9DDB374C40C85257D8B0060CB03
            if(!qli.isReplacedPart()){
            	if(qli.isSaasPart() || qli.isMonthlySoftwarePart()){
            		if(qli.isSaasTcvAcv()){
            			Double entitledTCV = QuoteCommonUtil.calculateEntitledTcvForSubmitQuote(qli);
            			bidEntitledPrcTot += entitledTCV == null? 0: entitledTCV.doubleValue();
            			bidExtendedPrcTot += qli.getSaasBidTCV() == null? 0: qli.getSaasBidTCV().doubleValue();
            		}else{
            			bidEntitledPrcTot += qli.getLocalExtProratedPrc() == null? 0: qli.getLocalExtProratedPrc().doubleValue();
            			bidExtendedPrcTot += qli.getLocalExtProratedDiscPrc() == null? 0: qli.getLocalExtProratedDiscPrc().doubleValue();
            		}
            	}else{
            		bidEntitledPrcTot += qli.getLocalExtProratedPrc() == null? 0: qli.getLocalExtProratedPrc().doubleValue();
            		bidExtendedPrcTot += qli.getLocalExtProratedDiscPrc() == null? 0: qli.getLocalExtProratedDiscPrc().doubleValue();
            	}
            }
        }
        
        double newBidExtendedPrc = 0;
        try{
            newBidExtendedPrc = Double.parseDouble(ct.getOfferPrice());
            
            if((newBidExtendedPrc <= bidExtendedPrcTot) || (newBidExtendedPrc > bidEntitledPrcTot)){
            	addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
        	             SubmittedQuoteMessageKeys.OFFER_PRICE_INVALID,
        	             PartPriceViewKeys.APPLY_OFFER_TXT,
					     QuoteParamKeys.APPLY_OFFER_INPUT, ct);
            	
            	return false;
            }
        } catch(Exception pe){
            logger.error(this, "Invalid double value for new bottom line price: " + ct.getOfferPrice());
           
        	addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
         	             QuoteMessageKeys.PRICE_NUMERIC_MSG,
         	             PartPriceViewKeys.APPLY_OFFER_TXT,
					     QuoteParamKeys.APPLY_OFFER_INPUT, ct);
            
            return false;
        }
        
        return true;
    }
    
    protected boolean validateLineItemDates(SubmittedPartPriceContract ct){
        if (null == ct.getUpdateLineItemDateFlag()) {
            return true;
        }
        HashMap items = ct.getItems();

        if (items.size() == 0) {
            return true;
        }

        Quote quote = null;
        try{
            quote = loadQuote(ct);
        }catch(TopazException te){
            logger.error(this, te);
            return false;
        } catch (QuoteException qe){
            logger.error(this, qe);
            return false;
        }
        
        //This should not happend, just to prevent null pointer exception
        if(quote == null){
            return false;
        }
        
        List lineItems = quote.getLineItemList();
        if(lineItems == null || lineItems.size() == 0){
            return true;
        }
        
        ct.setQuote(quote);
        
        QuoteHeader header = quote.getQuoteHeader();
        PartPriceConfigFactory factory = PartPriceConfigFactory.singleton();
        
        for(Iterator itemIt = lineItems.iterator(); itemIt.hasNext(); ){
            QuoteLineItem qli = (QuoteLineItem)itemIt.next();
            String key = getKey(qli);
            
            if(items.get(key) == null){
                continue;
            }
            
            //Start date validation
            if(!factory.isAllowEditStartDate(header, qli)){
                continue;
            }
            
            if(!validateStartDate(qli, ct, factory, header)){
                return false;
            }
            
            //End date validation
            if(!factory.isAllowEditEndDate(header, qli)){
                continue;
            }
            
            if(!validateEndDate(qli, ct, factory, header)){
                return false;
            }
        }
        
        return true;
    }
    
    private String getKey(QuoteLineItem qli){
        return qli.getPartNum() + "_" + qli.getSeqNum();
    }
    
    private boolean validateEndDate(QuoteLineItem qli, SubmittedPartPriceContract ct, PartPriceConfigFactory factory, QuoteHeader header){
        String key = getKey(qli);
        SubmittedPartPriceContract.LineItemParameter itemParam = (SubmittedPartPriceContract.LineItemParameter)ct.getItems().get(key);
        
        //validate maint end date
    	if(DateHelper.validateDate(itemParam.ovrdEndYear, itemParam.ovrdEndMonth, itemParam.ovrdEndDay)){   	    
            // check the duration
            Date ovrdStartDate = DateUtil.parseDate(itemParam.getOvrdStartDate());
            
            //For cmprss cvrage applied parts, start date edit is not allowed
            if(qli.hasValidCmprssCvrageMonth() ){
                ovrdStartDate = qli.getMaintStartDate();
            }
            
            Date ovrdEndDate = DateUtil.parseDate(itemParam.getOvrdEndDate());
            
            boolean isFTLPart = PartPriceConfigFactory.singleton().isFTLPart(itemParam.revnStrmCode);
            
            int months = DateUtil.calculateFullCalendarMonths(ovrdStartDate, ovrdEndDate);
            if(isFTLPart){
                int weeks = DateUtil.calculateWeeks(ovrdStartDate, ovrdEndDate);
                
                if((weeks < 1) || (months > 12)){
                	addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
                	       SubmittedQuoteMessageKeys.MSG_OVRRD_FTL_DURATION_NOT_VALID,
 						   SubmittedQuoteViewKeys.OVR_END_DATE,
 						   (itemParam.key + SubmittedQuoteParamKeys.ovrdDtEndDaySuffix), ct);
             	
                 return false; 
                }
            } else {

                if ((months < 1) || (months > 12)) {
                	
                	addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
                			   SubmittedQuoteMessageKeys.MSG_OVRRD_DURATION_NOT_VALID,
    						   SubmittedQuoteViewKeys.OVR_END_DATE,
    						   (itemParam.key + SubmittedQuoteParamKeys.ovrdDtEndDaySuffix), ct);
                	
                    return false;
                }
            }
            boolean isEnforceEndDate = PartPriceConfigFactory.singleton().enforceEndDateByCondition(itemParam.revnStrmCode);
            if(isEnforceEndDate){
                if(!DateUtil.isFirstDayOfMonth(ovrdStartDate) && !DateUtil.isLastDayOfMonth(ovrdEndDate)){
                    addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
             			   SubmittedQuoteMessageKeys.MSG_OVRRD_START_DATE_NOT_VALID,
 						   SubmittedQuoteViewKeys.OVR_END_DATE,
 						   (itemParam.key + SubmittedQuoteParamKeys.ovrdDtEndDaySuffix), ct);
             	
                 return false;
                }
            }
            
    	} else {
        	addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
    		     	SubmittedQuoteMessageKeys.MSG_OVRRD_DATE_NOT_VALID,
					SubmittedQuoteViewKeys.OVR_END_DATE,
					(itemParam.key + SubmittedQuoteParamKeys.ovrdDtEndDaySuffix), ct);
        	
        	return false;
    	}
    	
    	return true;
    }
    
    protected Quote loadQuote(SubmittedPartPriceContract ct) throws QuoteException, NoDataException, TopazException{
        String webQuoteNum = ct.getQuoteNum();
        QuoteUserSession quoteUserSession = ct.getQuoteUserSession();
        String up2ReportingUserIds = quoteUserSession == null ? "" : quoteUserSession.getUp2ReportingUserIds();
        String userId = ct.getUserId();
        
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        Quote quote = qProcess.getSubmittedQuoteBaseInfo(webQuoteNum, userId, up2ReportingUserIds);
        quote.setLineItemList(QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(ct.getQuoteNum()));
        quote.setPgsAppl(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteUserSession.getAudienceCode()));
        
        return quote;
    }
    
    private boolean validateStartDate(QuoteLineItem qli, SubmittedPartPriceContract ct, PartPriceConfigFactory factory, QuoteHeader header){
        String key = getKey(qli);
        SubmittedPartPriceContract.LineItemParameter itemParam = (SubmittedPartPriceContract.LineItemParameter)ct.getItems().get(key);
        
        if(qli.hasValidCmprssCvrageMonth() ){
            return true;
        }
        
        if(DateHelper.validateDate(itemParam.ovrdStartYear, itemParam.ovrdStartMonth, itemParam.ovrdStartDay)){
        	//check if future start date allowed
        	if(!factory.isFutureStartDateAllowed(qli.getRevnStrmCode()) && startDateAfterToday(itemParam)){
        		logContext.debug(this, "maint start date of line item[" + itemParam.key + "] should not be in the future");
        		addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
        				SubmittedQuoteMessageKeys.MSG_NO_FUTURE_MAINT_START_DATE,
						SubmittedQuoteViewKeys.OVR_START_DATE,
						(itemParam.key + SubmittedQuoteParamKeys.ovrdDtStartDaySuffix), ct);
        		
                return false;
        	}
        	
        	//skip back dating related validation for renewal quote
        	if(!header.isRenewalQuote()){
            	if(!factory.isBackDatingAllowed(header, qli.getRevnStrmCode()) && startDateBeforeToday(itemParam)){
            		logContext.debug(this, "back dating not allowed for line item[" + itemParam.key + "]");
        			addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
  					      SubmittedQuoteMessageKeys.MSG_START_DATE_BACK_DTG_NOT_ALLOWED, 
							  SubmittedQuoteViewKeys.OVR_START_DATE,
							  (itemParam.key + SubmittedQuoteParamKeys.ovrdDtStartDaySuffix), ct);
        			return false;
            	}
            	
            	if(startDateBeforeToday(itemParam)){
            		if(backDatingExceedsRange(factory.getBackDatingPastYearLimit(header.getLob().getCode()), itemParam)){
            			
            			logContext.debug(this, "line item[" + itemParam.key + "] exceeds back dating past years limit");
            			addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
            					      SubmittedQuoteMessageKeys.MSG_BACK_DTG_EXCEEDS_RANGE, 
									  new String[]{SubmittedQuoteViewKeys.OVR_START_DATE, String.valueOf(ct.getPastYearLimit())},
									  new boolean[]{true, false},
									  (itemParam.key + SubmittedQuoteParamKeys.ovrdDtStartDaySuffix), ct);
            			return false;
            		}
            	}
        	}
        } else {
        	// start date not valid
        	addErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
    		     	SubmittedQuoteMessageKeys.MSG_OVRRD_DATE_NOT_VALID,
					SubmittedQuoteViewKeys.OVR_START_DATE,
					(itemParam.key + SubmittedQuoteParamKeys.ovrdDtStartDaySuffix), ct);
        	
        	return false;            	
        }
	
        return true;
    }
    
    protected void addErrorMsg(String boundle, String msg, String arg, String key, ProcessContract contract){
    	HashMap map = new HashMap();
    	FieldResult fieldResult = new FieldResult();
    	
        fieldResult.setMsg(boundle, msg);
        fieldResult.addArg(boundle, arg);
        
        map.put(key, fieldResult);
        
        addToValidationDataMap(contract, map);
    }
    
    private void addErrorMsg(String boundle, String msg, String[] args, boolean[] isArgsResource,
    		             String key, ProcessContract contract){
    	HashMap map = new HashMap();
    	FieldResult fieldResult = new FieldResult();
    	
        fieldResult.setMsg(boundle, msg);
        
        for(int i = 0; i < args.length; i++){
        	fieldResult.addArg(boundle, args[i], isArgsResource[i]);
        }
        
        map.put(key, fieldResult);
        
        addToValidationDataMap(contract, map);
    }
    
    protected boolean startDateValid(SubmittedPartPriceContract.LineItemParameter lineItem){
    	if(StringUtils.isEmpty(lineItem.ovrdStartYear) 
    			|| StringUtils.isEmpty(lineItem.ovrdStartMonth)
				|| StringUtils.isEmpty(lineItem.ovrdStartDay)){
    		return false;
    	}
    	
    	return true;
    }
    
    protected boolean startDateBeforeToday(SubmittedPartPriceContract.LineItemParameter lineItem){	
    	boolean before = DateUtil.isDateBeforeToday(DateUtil.parseDate(lineItem.getOvrdStartDate(), DateUtil.PATTERN));
    	lineItem.startDateBackDated = before;
    	
    	return before;
    }
    
    protected boolean startDateAfterToday(SubmittedPartPriceContract.LineItemParameter lineItem){
    	return DateUtil.isDateAfterToday(DateUtil.parseDate(lineItem.getOvrdStartDate(), DateUtil.PATTERN));
    }
    
    protected boolean backDatingExceedsRange(int pastYearLimit, SubmittedPartPriceContract.LineItemParameter lineItem){
    	Date startDate = DateUtil.parseDate(lineItem.getOvrdStartDate(), DateUtil.PATTERN);
    	
    	int months = DateUtil.calculateFullCalendarMonths(startDate, DateUtil.getCurrentYYYYMMDDDate());
    	int monthLimit = pastYearLimit * 12;
    	
    	return (months > monthLimit);
    }
    
    protected Quote getSubmittedQuoteBaseInfo(QuoteProcess qProcess, String webQuoteNum, 
                                                           String userId, String up2ReportingUserIds, 
                                                           SubmittedQuoteBaseContract baseContract)
                                                                                throws NoDataException, QuoteException{
        if(baseContract.getQuote() != null){
            return baseContract.getQuote();
        } else {
            return super.getSubmittedQuoteBaseInfo(qProcess, webQuoteNum, userId, up2ReportingUserIds, baseContract);
        }
    }
}
