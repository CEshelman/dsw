package com.ibm.dsw.quote.draftquote.action;

import is.domainx.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.OfferPriceException;
import com.ibm.dsw.quote.base.exception.PriceEngineUnAvailableException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.domain.ValidationMessageFactory;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcessFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.contract.DraftQuoteSubmitBaseContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcess;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcessFactory;
import com.ibm.dsw.quote.draftquote.util.ApprovalGroupHelper;
import com.ibm.dsw.quote.mail.process.SpecialBidEmailHelper;
import com.ibm.dsw.spbid.common.ApprovalGroup;
import com.ibm.dsw.spbid.common.ApprovalGroupMember;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.bean.StateBean;
import com.ibm.ead4j.jade.bean.StateBeanFactory;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.config.StateKeys;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>DraftQuoteSubmitBaseAction<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-4-16
 */

public abstract class DraftQuoteSubmitBaseAction extends BaseContractActionHandler {

    protected transient LogContext logContext = LogContextFactory.singleton().getLogContext();
    protected Locale locale = null;

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        handler.setState(getState());

        DraftQuoteSubmitBaseContract baseContract = (DraftQuoteSubmitBaseContract) contract;
        String creatorId = baseContract.getUserId();
        User user = baseContract.getUser();
        Quote quote = null;
        locale = baseContract.getLocale();

        try {
            // Get quote detail, you can override this method if needed.
            quote = getQuoteDetail(creatorId,baseContract.isPGSEnv());
            // put quote details in parameter for viewbean
            handler.addObject(ParamKeys.PARAM_QUOTE_OBJECT, quote);
            handler.addObject(ParamKeys.PARAM_CUST_ACT_URL_PARAM, baseContract.getSearchCriteriaUrlParam());
            handler.addObject(ParamKeys.PARAM_CUST_ACT_REQUESTOR, baseContract.getRequestorEMail());
        } catch (NoDataException e) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return handler.getResultBean();
        } catch (PriceEngineUnAvailableException peue) {
            this.handlePriceEngineUnavailableException(baseContract, handler);
            return handler.getResultBean();
        } catch (OfferPriceException ope) {
            this.handleOfferPriceException(baseContract, handler, ope);
            return handler.getResultBean();
        }
        
        Cookie sqoCookie = baseContract.getSqoCookie();
        QuoteHeader quoteHeader = quote.getQuoteHeader();
        if ( quote.getQuoteHeader().isPGSQuote() 
        		&& QuoteConstants.QUOTE_AUDIENCE_CODE_SQO.equalsIgnoreCase(baseContract.getQuoteUserSession().getAudienceCode()) 
        		&& quoteHeader.isAcceptedByEval() 
    		&& (quoteHeader.isAssignedEval(baseContract.getQuoteUserSession().getUserId()) || quoteHeader.isQuoteCntryEvaluator()) )
        {
        	quoteHeader.setSubmitByEvaluator(true);
        }
        
        // Validate quote
        Map validResult = validateQuote(baseContract.getQuoteUserSession(), quote, sqoCookie);
        
        //for evaluator return
        if ( quoteHeader.isSubmitByEvaluator() )
        {
        	if (validResult != null && validResult.size() > 0){
                
                return handleValidateResult(validResult, handler, locale);
            }
        	if ( QuoteConstants.EVAL_SELECT_OPTION_RETURN.equals(quote.getSpecialBidInfo().getEvaltnAction()) )
        	{
	        	QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
	        	quoteProcess.updateBPQuoteStage(baseContract.getUserId(), quoteHeader.getWebQuoteNum(), QuoteConstants.QUOTE_STAGE_CODE_RETURN_FORCHG_EVAL, 
	        			0, 0, quote.getSpecialBidInfo().getEvaltnComment());
	        	//@todo here add mail function for Evaluator return
	        	SpecialBidEmailHelper helper = new SpecialBidEmailHelper();
	    		helper.evalReturn(quote);
	        	List<String> redirectMsgList = new ArrayList<String>();
	        	redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":" + DraftQuoteMessageKeys.MSG_EVALUATOR_RETURN_QUOTE_TO_BP + ":" + MessageBeanKeys.INFO);
	        	String redirectURL = HtmlUtil.getURLForAction(baseContract.getCurTabDisAct()) + "&quoteNum=" + quoteHeader.getWebQuoteNum();
	            handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
	            handler.setState(com.ibm.dsw.quote.base.config.StateKeys.STATE_REDIRECT_ACTION);
	            handler.addObject(ParamKeys.PARAM_REDIRECT_MSG, redirectMsgList);
	            handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.FALSE);
	            return handler.getResultBean();
        	}
        }
       
        //handle not meet bid iteration case
        if (validResult != null && validResult.containsKey(QuoteCapabilityProcess.NOT_MEET_BID_ITERATION)){
        	Set<String> bidIteratnReasons = (Set<String>)validResult.get(QuoteCapabilityProcess.NOT_MEET_BID_ITERATION);
        	logContext.debug(this, "web quote["+quote.getQuoteHeader().getWebQuoteNum()+"] return not meet bid iteration reasons");
        	handler.addObject(ParamKeys.PARMA_BID_ITERATN_REASONS, bidIteratnReasons);
            handler.addObject(ParamKeys.PARAM_USER_OBJECT, user);
            handler.setState(DraftQuoteStateKeys.STATE_NOT_MEET_BID_ITERATION);
            return handler.getResultBean();
        }
        
        //retrieve approval information for special bid quotes
        List approverGroup = null;
        if (quote.getQuoteHeader().getSpeclBidFlag() == 1 
                && quote.getQuoteHeader().getApprovalRouteFlag() == 1
                && !quote.getQuoteHeader().isCopied4PrcIncrQuoteFlag()
                && !QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC.equalsIgnoreCase(quote.getQuoteHeader().getQuoteStageCode())){
        	
        		if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(baseContract.getQuoteUserSession().getAudienceCode())){
        				if( isSubmitPGSLevel0SpBid(baseContract.getQuoteUserSession(),quote,sqoCookie)){
        					approverGroup = executeSubmitForApproval(contract, handler, quote, user);
        					
        					//stop quote submission if no approvers return for PGS quotes in PGS
            	        	if(approverGroup == null || approverGroup.isEmpty()){
            	    			validResult.put(QuoteCapabilityProcess.NO_APPROVER_RETURNED, Boolean.TRUE);
            	        	}
        				}
        				
        			
        		}else{
        			approverGroup = executeSubmitForApproval(contract, handler, quote, user);
        			approverGroup = removeRdyToOrdApproveType(approverGroup);
        	        handler.addObject(DraftQuoteParamKeys.PARAM_APPROVAL_GROUPS, approverGroup);       
        		}
        	
        }
        
        if (validResult != null && validResult.size() > 0){
            
            return handleValidateResult(validResult, handler, locale);
        }
        
        
        // save increaseBidTCV to database.
        List saasConfigrtnList = quote.getPartsPricingConfigrtnsList();
        ListIterator li = saasConfigrtnList.listIterator();
        StringBuilder configrtnIds = new StringBuilder();
        StringBuilder increaseBidTCVs =new StringBuilder();
        StringBuilder unUsedTCVs = new StringBuilder();
        while(li.hasNext()) {
    		PartsPricingConfiguration cli = (PartsPricingConfiguration) li.next();
    		if(cli.getConfigrtnActionCode() != null && cli.getConfigrtnActionCode().equals("AddTrd") && cli.getIncreaseBidTCV() !=null){
    			configrtnIds.append(cli.getConfigrtnId() +",");
    			increaseBidTCVs.append(cli.getIncreaseBidTCV()+",");
    			unUsedTCVs.append(cli.getUnusedBidTCV()+",");
    			
    		}
        }
        PartPriceProcess partPriceProcess = PartPriceProcessFactory.singleton().create();
		partPriceProcess.updateTCVs(quoteHeader.getWebQuoteNum(), configrtnIds.toString(), increaseBidTCVs.toString(),unUsedTCVs.toString());
        
		// check if the BP channel orverride discount reason value meets its conditions
		SpecialBidInfo spbidInfo=quote.getSpecialBidInfo();
		if (spbidInfo != null){
			if (!quote.getQuoteHeader().isChannelQuote() || !QuoteCommonUtil.isChannelOverrideDiscount(quote)){
				if (StringUtils.isNotBlank(spbidInfo.getChannelOverrideDiscountReasonCode())) {
					SpecialBidProcess specialBidProcess = SpecialBidProcessFactory.singleton().create();
					specialBidProcess.removeChannelOverrideDiscountReason(spbidInfo);
				}
			}
		}
  
        // execute other process if necessary
        excuteSubmitProcess(contract, handler, quote, user, approverGroup);
        if ( "true".equals(handler.getParameters().getParameterAsString(ParamKeys.PARAM_UNDO_FLAG)) )
        {
        	return handler.getUndoResultBean();
        }
        
        
        return handler.getResultBean();
    }

    /**
     * @param contract
     * @param handler
     */
    protected void excuteSubmitProcess(ProcessContract contract, ResultHandler handler, Quote quote, User user, List approverGroup)
            throws QuoteException {
    }

    /**
     * @return
     */
    protected abstract String getState();

    protected Quote getQuoteDetail(String creatorId,boolean isPGSEnv) throws NoDataException, QuoteException,
            PriceEngineUnAvailableException {
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        Quote quote = qProcess.getDraftQuoteDetails(creatorId,isPGSEnv);
        return quote;
    }

    protected abstract Map validateQuote(QuoteUserSession user, Quote quote, Cookie cookie) throws QuoteException;

    /**
     * @param validResult
     *            You can override this method if needed.
     */
    protected ResultBean handleValidateResult(Map validResult, ResultHandler handler, Locale locale) {
        ResultBean resultBean = handler.getUndoResultBean();
        MessageBean messageBean = resultBean.getMessageBean();
        
        String msgHead = getValidMsgHead(locale);
        messageBean.addMessage(msgHead, MessageKeys.LAYER_MSG_HEAD);

        Set keySet = validResult.keySet();
        Iterator iter = keySet.iterator();
        
        while (iter.hasNext()) {
            String key = (String) iter.next();
            Object value = validResult.get(key);
            String param = null;
            
            // if the value is a string, it will be considered as the message parameter.
            if (value != null && value instanceof String) {
                param = (String) value;
            }

            String message = ValidationMessageFactory.singleton().getValidationMessage(key, locale, param);
            messageBean.addMessage(message, MessageKeys.LAYER_MSG_ITEM);
        }

        return resultBean;
    }

    protected abstract String getValidMsgHead(Locale locale);
    
    protected void handlePriceEngineUnavailableException(DraftQuoteSubmitBaseContract contract, ResultHandler handler) {
        logContext.info(this, "Failed to get current price info, call pricing service error.");
        
        String msg = getI18NString(DraftQuoteMessageKeys.MSG_PRICE_ENGINE_UNAVAILABLE_WHEN_SUBMIT,
                I18NBundleNames.ERROR_MESSAGE, contract.getLocale());
        MessageBean mBean = MessageBeanFactory.create();
        mBean.addMessage(msg, MessageBeanKeys.INFO);
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        StateBean stateBean = StateBeanFactory.create(context.getConfigParameter(StateKeys.JADE_UNDO_STATE_KEY));
        
        handler.setState(stateBean);
        handler.setMessage(mBean);
    }
    
    protected void handleOfferPriceException(DraftQuoteSubmitBaseContract contract, ResultHandler handler,
            OfferPriceException ope) {
        logContext.info(this, "Catched offer price exception.");

        String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_PARTS_PRICE_TAB);
        String msgInfo = HtmlUtil.getTranMessageParam(I18NBundleNames.ERROR_MESSAGE, ope.getMessageKey(), true, null);
        StringBuffer sb = new StringBuffer(redirectURL);
        sb.append("&amp;").append(ParamKeys.PARAM_TRAN_MSG).append("=").append(msgInfo);
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, sb.toString());

        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
        handler.setState(com.ibm.dsw.quote.base.config.StateKeys.STATE_REDIRECT_ACTION);
    }
    
    /**
     * retrieve special bid approval information via rule engine
     * @param contract
     * @param handler
     * @param quote
     * @param user
     * @return
     * @throws QuoteException
     */
    protected List executeSubmitForApproval(ProcessContract contract, ResultHandler handler, Quote quote, User user)
    throws QuoteException {

    	// load approval groups
    	List groups = null;
    	QuoteHeader quoteHeader = quote.getQuoteHeader();
    	boolean isNoApprvrs = false;
    	boolean isMultipleGroups = false;
    	boolean isOneLvlApprvrOnly = false;
    	logContext.debug(this, "begin to load approval group info");

    	try {
    		groups = new ApprovalGroupHelper().getListOfApprover(quote, ((DraftQuoteSubmitBaseContract) contract).isSQOEnv());
    	} catch (Exception e) {
    		logContext.error(this, e, "can't get approval group information from rule engine due to web service error.");
    		// set rule engine not available flag so that viewbean & JSP can
    		// handle this situation and add msg to display on UI.
    		String msg = getI18NString(DraftQuoteMessageKeys.RULE_ENGINE_NOT_AVAILABLE, I18NBundleNames.ERROR_MESSAGE,
    				((DraftQuoteSubmitBaseContract) contract).getLocale());
    		MessageBean messageBean = MessageBeanFactory.create();
    		messageBean.addMessage(msg, MessageBean.ERROR);
    		handler.setMessage(messageBean);
    		handler.addObject(DraftQuoteParamKeys.RULE_ENGINE_NOT_AVAILABLE, Boolean.FALSE);
    		return null;
    	}

    	logContext.debug(this, "end loading approval group info");
    	if (groups == null || groups.size() == 0) {
    		isNoApprvrs = true;
    	} else {
    		dumpGroupsInfo(groups);
    		Iterator iterator = groups.iterator();
    		HashMap lvlMap = new HashMap();

    		while (iterator.hasNext()) {
    			ApprovalGroup group = (ApprovalGroup) iterator.next();
    			if (group.getMembers().length == 0) {
    				isNoApprvrs = true;
    			}
    			lvlMap.put(new Integer(group.getType().getLevel()), group);
    		}

    		if (lvlMap.keySet().size() == 1 && !quoteHeader.isFCTQuote() && !quoteHeader.isMigration()
    				&& QuoteConstants.GEO_AG.equalsIgnoreCase(quoteHeader.getCountry().getSpecialBidAreaCode()))
    			isOneLvlApprvrOnly = true;

    		// quick search to determine isMultipleGroups flag.
    		for (int i = 0; i < groups.size(); i++) {
    			ApprovalGroup currGroup = (ApprovalGroup) groups.get(i);
    			for (int j = i + 1; j < groups.size(); j++) {
    				ApprovalGroup compareGroup = (ApprovalGroup) groups.get(j);
    				String currName = StringUtils.trimToEmpty(currGroup.getName());
    				String cmprName = StringUtils.trimToEmpty(compareGroup.getName());

    				if (currGroup.getType().getLevel() == compareGroup.getType().getLevel()
    						&& !currName.equals(cmprName)) {
    					isMultipleGroups = true;
    					break;
    				}
    			}
    		}
    		handler.addObject(DraftQuoteParamKeys.PARAM_APPROVAL_GROUPS, groups);
    	}

    	logContext.debug(this, "To send no approver notification is " + isNoApprvrs);
    	logContext.debug(this, "To send multi groups notification is " + isMultipleGroups);
    	logContext.debug(this, "To send one level approver only notification is " + isOneLvlApprvrOnly);

    	handler.addObject(DraftQuoteParamKeys.PARAM_SEND_NO_APPRVR_NOTIF, Boolean.valueOf(isNoApprvrs));
    	handler.addObject(DraftQuoteParamKeys.PARAM_SEND_MULTI_GRPS_NOTIF, Boolean.valueOf(isMultipleGroups));
    	handler.addObject(DraftQuoteParamKeys.PARAM_SEND_ONE_LVL_APPRVR_NOTIF, Boolean.valueOf(isOneLvlApprvrOnly));

    	return groups;
    }
    
    private void dumpGroupsInfo(List groups) {
        Iterator iterator = groups.iterator();
        logContext.debug(this, "\nApproval group information from rule engine:" + groups.size() + " groups returned");
        while (iterator.hasNext()) {
            ApprovalGroup group = (ApprovalGroup) iterator.next();
            StringBuffer logs = new StringBuffer("\nGroup name: " + group.getName() + " Group type: "
                    + group.getType().getTypeName() + " level:" + group.getType().getLevel());
            logs.append("\nMembers:");
            ApprovalGroupMember[] members = group.getMembers();
            for (int i = 0; i < members.length; i++) {
                ApprovalGroupMember member = group.getMembers()[i];
                logs.append("\n" + member.getEmail() + " " + member.getFirstName() + "-" + member.getLastName());
            }
            logContext.debug(this, logs.toString());
        }
    }
    
    protected boolean isSubmitPGSLevel0SpBid(QuoteUserSession user, Quote quote, Cookie cookie)throws QuoteException{
    	return QuoteCapabilityProcessFactory.singleton().create().isSubmitPGSLevel0SPBid(user,quote,cookie);
    }
    
    private List removeRdyToOrdApproveType(List approverGroup){
     	 Map groupMap = new HashMap();
          for (int i = 0; i < approverGroup.size(); i++) {
  			ApprovalGroup currGroup = (ApprovalGroup) approverGroup.get(i);
  			int level = currGroup.getType().getLevel();
  			if (groupMap.containsKey(level)) {
  				((List)groupMap.get(level)).add(currGroup);
  			} else {
  				List groupList = new ArrayList();
  				groupList.add(currGroup);
  				groupMap.put(level, groupList);
  			}						
          }
          List retList = new ArrayList();
          Iterator iter = groupMap.keySet().iterator();
          while(iter.hasNext()) {
          	Object key = iter.next();
          	List list = (List)groupMap.get(key);
          	if (list.size() > 1) {
          		boolean hasNotRdy2OrdFlag = false;
          		for (int j = 0; j < list.size(); j++) {
          			ApprovalGroup ag = (ApprovalGroup)list.get(j);        			
          			if (j != list.size() - 1) {
          				if (ag.getType().getRdyToOrder() == 1) {
          					list.remove(ag);
          					j--;
          				} else {
          					hasNotRdy2OrdFlag = true;
          				}
          			} else if (hasNotRdy2OrdFlag) {
          				if (ag.getType().getRdyToOrder() == 1) {
          					list.remove(ag);
          					j--;
          				}
          			}
          		}
          	}
          	retList.addAll(list);
          }
          return retList;
     }
}
