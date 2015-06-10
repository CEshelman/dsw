/*
 * Created on 2007-7-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.draftquote.action.DisplayAddAttachmentsAction;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.dsw.quote.draftquote.contract.SpecialBidCommentDocumentsContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author helenyu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayAddSpecialBidAttachmentAction extends DisplayAddAttachmentsAction {
    
    private static final long serialVersionUID = 8014822633290180728L;

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
        
        addObjectToHandler(contract, handler);
        SpecialBidCommentDocumentsContract sbContract = (SpecialBidCommentDocumentsContract)contract;
        if ( "1".equals(sbContract.getFileInputFlag()) )
        {
            handler.setState(StateKeys.STATE_DISPLAY_ATTACH_FILES_INPUT);
        }
        else
        {
            handler.setState(StateKeys.STATE_DISPLAY_ATTACH_FILES_TO_QUOTE);
        }
        return handler.getResultBean();
    }
    
    protected void addObjectToHandler(ProcessContract contract, ResultHandler handler) throws QuoteException {
        super.addObjectToHandler(contract, handler);
        
        SpecialBidCommentDocumentsContract sbContract = (SpecialBidCommentDocumentsContract)contract;
        String role = sbContract.getUserRole();
        String secId = sbContract.getSecId();
        String stage = sbContract.getStage();
        String quoteNum = sbContract.getWebQuoteNumber();
        if ( secId == null || secId.equals("") )
        {
            secId = SpecialBidInfo.BEGIN_SUBMITTER + 1 + "";
        }
        
        if(StringUtils.isBlank(role)){
            throw new QuoteException("Invalid request parameters. Role are required entries.");
        }
        
        if(null != role && !QuoteConstants.specialbidAttachUserrole.contains(role)){
        	throw new QuoteException("Invalid request parameters. Role are illegal entries.");
        }
        
        if(null != stage && !QuoteConstants.specialbidAttachStage.contains(stage)){
        	throw new QuoteException("Invalid request parameters. Stage are illegal entries.");
        }
        
        if(null != quoteNum && StringHelper.hasNonRegularChar(quoteNum)){
        	throw new QuoteException("Invalid request parameters. Quote number are illegal entries.");
        }
        
        String attCode = sbContract.getAttCode();
        if ( StringUtils.isNotEmpty(attCode) )
        {
        	handler.addObject(DraftQuoteParamKeys.PARAM_ATT_CODE, attCode);
        }
        
        handler.addObject(SpecialBidParamKeys.PARAM_SEC_ID, secId);
        
        handler.addObject(ParamKeys.PARAM_USER_ROLE, role);
    }
}
