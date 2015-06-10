package com.ibm.dsw.quote.draftquote.action;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.DBConstants;
import com.ibm.dsw.quote.base.config.ErrorKeys;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.ArithmeticOperationException;
import com.ibm.dsw.quote.base.exception.OfferPriceException;
import com.ibm.dsw.quote.base.exception.PayerDataException;
import com.ibm.dsw.quote.base.exception.PriceEngineUnAvailableException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.ConfigurationEditParams;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.QuotePartsPriceContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.dsw.quote.ps.config.PartSearchMessageKeys;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * <p>
 * Copyright 2006 by IBM Corporation All rights reserved.
 * </p>
 * 
 * <p>
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * </p>
 * 
 * The <code>PartPriceTabAction</code> class is to display the part and price
 * for a draft quote
 * 
 * @author <a href="mailto:liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *  
 */
public class PartPriceTabAction extends DraftQuoteBaseAction {
    private static LogContext logger = LogContextFactory.singleton().getLogContext();
    /**
     * 
     * @param contract
     * @param handler
     * @return
     * @throws QuoteException
     * @throws ResultBeanException
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    @Override
	public void getDraftQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler)
                             throws QuoteException {

    	TimeTracer tracer = TimeTracer.newInstance();
    	QuotePartsPriceContract ct = (QuotePartsPriceContract) contract;

        if (null != ct.getMandatoryKey()) {
            setPVUMandatoryCookie(ct);
        }

        if (null != ct.getDisplayDetailKey()) {
            setDisplayDetailCookie(ct);
        }
        try {
            PartPriceProcess process = PartPriceProcessFactory.singleton().create();
            QuoteHeader header = quote.getQuoteHeader();
            if(ct.getOverallYtyGrowth() != null){
            	quote.setOverallYtyGrowth(ct.getOverallYtyGrowth());
            	try{
            		header.setRecalcPrcFlag(1);
            	}catch(TopazException te){
            		throw new QuoteException(te);
            	}
            }
            
            
			process.getPartPriceInfo(quote, ct.getQuoteUserSession());
            ConfigurationEditParams configurationEditParams = new ConfigurationEditParams();
            configurationEditParams.setEditIbmProdId(ct.getEditIbmProdId());
            configurationEditParams.setEditConfigrtnId(ct.getEditConfigrtnId());
            configurationEditParams.setEditOrgConfigrtnId(ct.getEditOrgConfigrtnId());
            configurationEditParams.setEditConfigrtrConfigrtnId(ct.getEditConfigrtrConfigrtnId());
            configurationEditParams.setEditTradeFlag(ct.getEditTradeFlag());
            configurationEditParams.setEditConfigurationFlag(ct.getEditConfigurationFlag());
            configurationEditParams.setOverrideFlag(ct.getOverrideFlag());
            quote.setConfigurationEditParams(configurationEditParams);
            
            boolean isShowProvisioningReminder = isShowProvisoinReminder(quote);
            
            setIsShowProvisionReminderCookie(ct, isShowProvisioningReminder+"");
            
            if(logContext instanceof QuoteLogContextLog4JImpl){
    			if(((QuoteLogContextLog4JImpl)logContext).isDebug(this)){		
    				StringBuffer sb = new StringBuffer();
                	sb.append("load back dating reason code and comment for quote[").append(header.getWebQuoteNum());
                	sb.append("] finished{comment[").append(header.getBackDatingComment());
                	sb.append("], reason code[");
                	if(header.getReasonCodes() == null){
                		sb.append("null");
                	} else {
                		for(int i = 0; i < header.getReasonCodes().size(); i++){
                			sb.append(header.getReasonCodes().get(i)).append(" ");
                		}
                	}
                	sb.append("]}");
                	logContext.debug(this, sb.toString());
    			}
    		}
            handler.addObject(DraftQuoteParamKeys.Price_Engine_UnAvailable, Boolean.FALSE);
        } catch (QuoteException e) {
            logContext.fatal(this, "Create QuoteProcess error");
            throw new QuoteException(e);
        } catch (PriceEngineUnAvailableException e) {

            String msg = "";
            if(e instanceof PayerDataException){
            	PayerDataException payerDataExcptn = (PayerDataException)e;
            	msg = getI18NString(e.getMessageKey(), I18NBundleNames.ERROR_MESSAGE, ct.getLocale(), new String[]{payerDataExcptn.getPayerDataIssueMsg()});
            } else if(e instanceof ArithmeticOperationException) {
            	ArithmeticOperationException arthOprtnExcptn = (ArithmeticOperationException)e;
            	msg = getI18NString(e.getMessageKey(), I18NBundleNames.ERROR_MESSAGE, ct.getLocale(), new String[]{arthOprtnExcptn.getArithmeticOperationIssueMsg()}); 
            } else {
            	msg = getI18NString(e.getMessageKey(), I18NBundleNames.ERROR_MESSAGE, ct.getLocale());
            }
            //msg = msg + " (" +DateUtil.getCurrentDateForErrorDisplay() + ")";
            MessageBean mBean = MessageBeanFactory.create();
            mBean.addMessage(msg, MessageBeanKeys.INFO);
            handler.setMessage(mBean);
            handler.addObject(DraftQuoteParamKeys.Price_Engine_UnAvailable, Boolean.TRUE);

        } catch (OfferPriceException e) {
            String msg = getI18NString(e.getMessageKey(), I18NBundleNames.ERROR_MESSAGE, ct.getLocale());
            MessageBean mBean = MessageBeanFactory.create();
            mBean.addMessage(msg, MessageBeanKeys.INFO);
            handler.setMessage(mBean);
            handler.addObject(DraftQuoteParamKeys.Price_Engine_UnAvailable, Boolean.FALSE);
        }
        
        logContext.debug(this, "Get PartPriceInfo successfully");
        
        List lineItems = quote.getLineItemList();
        if (lineItems.size() == 0) {
            if(!CommonServiceUtil.quoteIsDraftBidItrtn(quote.getQuoteHeader())){
                handler.setState(DraftQuoteStateKeys.STATE_EMPTY_DRAFT_QUOTE_PP);
            }else{
                handler.setState(DraftQuoteStateKeys.STATE_NOT_MEET_BID_ITERATION);
            }
            return;
        }

        QuoteHeader header = quote.getQuoteHeader();
        String lob = header.getLob().getCode();

        if (header.isSalesQuote()) {
            if (QuoteConstants.LOB_PA.equalsIgnoreCase(lob)
            	|| QuoteConstants.LOB_PAE.equalsIgnoreCase(lob)) {
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_PARTS_PRICING_PAPAE_TAB);
            } else if (QuoteConstants.LOB_FCT.equalsIgnoreCase(lob)) {
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_PARTS_PRICING_FCT_TAB);
            } else if (QuoteConstants.LOB_PPSS.equalsIgnoreCase(lob)) {
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_PARTS_PRICING_PPSS_TAB);
            }else if(QuoteConstants.LOB_OEM.equalsIgnoreCase(lob)){
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_PARTS_PRICING_OEM_TAB);
            }else if(QuoteConstants.LOB_SSP.equalsIgnoreCase(lob)){
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_PARTS_PRICING_SSP_TAB);
            }
        } else if (header.isRenewalQuote()) {

            if (QuoteConstants.LOB_PA.equalsIgnoreCase(lob)) {
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_RQ_PARTS_PRICING_PA_TAB);
            } else if (QuoteConstants.LOB_PAE.equalsIgnoreCase(lob)) {
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_RQ_PARTS_PRICING_PAE_TAB);

            } else if (QuoteConstants.LOB_FCT.equalsIgnoreCase(lob)) {
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_RQ_PARTS_PRICING_FCT_TAB);
            }else if(QuoteConstants.LOB_OEM.equalsIgnoreCase(lob)){
                handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_RQ_PARTS_PRICING_OEM_TAB);
            }
            else {
                String errMsg = "Un Supported Lob for Renewal Quote,lob = " + lob;
                logContext.fatal(this, errMsg);
                throw new QuoteException(errMsg);
            }
        }
        
        
        processForPartLimit(ct, handler);
        
        processForGrowthDelegation(quote, ct, handler);
        
//        valid4BidItrtn(quote, ct, handler);
        
        tracer.dump();
    }
    
    private void processForGrowthDelegation(Quote quote, QuotePartsPriceContract ct, ResultHandler handler){
    	 if(GrowthDelegationUtil.hasPartsExceedRsvpPrice(quote)){
    		 String message = getI18NString(DraftQuoteMessageKeys.MSG_EXCEED_RSVP_PRICE, 
                                                 I18NBundleNames.BASE_MESSAGES, ct.getLocale());
    		 
             MessageBean mBean = MessageBeanFactory.create();
             mBean.addMessage(message, MessageBeanKeys.INFO);
             handler.setMessage(mBean);
    	 }
    	 
    	 if(GrowthDelegationUtil.showGrowthDelegationNotCalculatedMessage(quote)){
    		 String message = getI18NString(DraftQuoteMessageKeys.GD_SB_NEED_RECAUCULATE, 
                                                 I18NBundleNames.BASE_MESSAGES, ct.getLocale());
    		 
             MessageBean mBean = MessageBeanFactory.create();
             mBean.addMessage(message, MessageBeanKeys.INFO);
             handler.setMessage(mBean);
    	 }
    }
    
    private void processForPartLimit(QuotePartsPriceContract ct, ResultHandler handler)
                                                   throws QuoteException{
        String exceedCode = ct.getPartLimitExceedCode();

        String msgKey = "";
        if (String.valueOf(DBConstants.DB2_SP_ALREADY_IS_MAX).equals(exceedCode)){
            msgKey = PartSearchMessageKeys.ALREADY_HAS_MAX;
        }else if (String.valueOf(DBConstants.DB2_SP_EXCEED_MAX).equals(exceedCode)){
            msgKey = PartSearchMessageKeys.EXCEED_MAX;
        }
        
        if(StringUtils.isNotBlank(msgKey)){
            String message = getI18NString(msgKey, 
                          I18NBundleNames.PART_SEARCH_BASE, ct.getLocale(),
                          new String[]{(PartPriceConfigFactory.singleton().getElaLimits()+""),(PartPriceConfigFactory.singleton().getAppliLimits()+"")});

            handler.addMessage(message ,MessageBeanKeys.INFO);
        }
    }
    
    @Override
	protected String getState(ProcessContract contract) {

        return DraftQuoteStateKeys.STATE_EMPTY_DRAFT_QUOTE_PP;

    }

    private void dump(Quote quote) {
        StringBuffer sb = new StringBuffer();
        Customer customer = quote.getCustomer();
        if (null != customer) {
            sb.append("\n----------------------Quote Customer---------------------\n");
            sb.append("customer number =" + customer.getCustNum()).append("\n");
            sb.append("ibm customer number =" + customer.getIbmCustNum()).append("\n");
            sb.append("customer name =" + customer.getCustName()).append("\n");
            sb.append("address1 =" + customer.getAddress1()).append("\n");
            sb.append("internal address =" + customer.getInternalAddress()).append("\n");
            sb.append("city =" + customer.getCity()).append("\n");
            sb.append("post code =" + customer.getPostalCode()).append("\n");
            sb.append("first name =" + customer.getCntFirstName()).append("\n");
            sb.append("last name =" + customer.getCntLastName()).append("\n");
        }
        QuoteHeader header = quote.getQuoteHeader();
        sb.append("----------------------Quote Header---------------------\n");
        sb.append(header);
        List lineItems = quote.getLineItemList();
        for (int i = 0; i < lineItems.size(); i++) {
            sb.append("------------------line item " + (i + 1) + "---------------\n");
            sb.append(lineItems.get(i)).append("\n");
        }
        logContext.debug(this, sb.toString());
    }

    private void setDisplayDetailCookie(QuotePartsPriceContract ct) {
        Cookie cookie = ct.getSqoCookie();
        QuoteCookie.setDisplayDetailFlag(cookie, ct.getDisplayDetailKey());
    }
    
    private void setIsShowProvisionReminderCookie(QuotePartsPriceContract ct,String isShowProvisioningReminder) {
        Cookie cookie = ct.getSqoCookie();
        QuoteCookie.setIsShowProvReminderFlag(cookie, isShowProvisioningReminder);
    }

    private void setPVUMandatoryCookie(QuotePartsPriceContract ct) {
        Cookie cookie = ct.getSqoCookie();
        QuoteCookie.setMandatoryFlag(cookie, ct.getMandatoryKey());
    }

    @Override
	protected boolean isDisplayTranMessage(String bundleName, String bundleKey) {
        if (super.isDisplayTranMessage(bundleName, bundleKey))
            return true;

        String[] keys = { ErrorKeys.MSG_OFFER_PRICE_ERR };
        return ArrayUtils.contains(keys, bundleKey);
    }
    
//    protected void valid4BidItrtn(Quote quote, QuotePartsPriceContract ct, ResultHandler handler) throws QuoteException{
//        if (!"TRUE".equalsIgnoreCase(ct.getBidItrtnPost()) && QuoteConstants.QUOTE_STAGE_CODE_BIDITRQT.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode())
//                && quote.getQuoteHeader().isBidIteratnQt()) {
//            try {
//                if (!CommonServiceUtil.quoteIsValid4BidItrtn(quote)) {
//                    handler.setState(DraftQuoteStateKeys.STATE_NOT_MEET_BID_ITERATION);
//                }
//            } catch (TopazException e1) {
//                throw new QuoteException(e1);
//            }
//        }
//    }
    
    @Override
	protected boolean checkQuoteInDraftStatus(Quote quote) {
        if(quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag()
                && QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode())){
            return false;
        }
        
        if(quote.getQuoteHeader().isExpDateExtendedFlag()
                && QuoteConstants.QUOTE_STAGE_CODE_CPEXDATE.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode())){
            return false;
        }
        return true;        
    }
    
    private boolean isShowProvisoinReminder(Quote quote){
    	List<PartsPricingConfiguration> configrtnList = quote.getPartsPricingConfigrtnsList();
    	if(configrtnList != null && configrtnList.size()!=0){
    		for (Iterator iterator = configrtnList.iterator(); iterator.hasNext();){
    			PartsPricingConfiguration configrtn = (PartsPricingConfiguration) iterator.next();
    			if(StringUtils.isNotBlank(configrtn.getProvisioningId())){
					continue;
				}else{
					return true;
				}
    		}
    	}
    	return false;
    }
 }
