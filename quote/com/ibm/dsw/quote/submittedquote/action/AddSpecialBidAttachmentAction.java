/*
 * Created on 2007-7-26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.action;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.JustSection;
import com.ibm.dsw.quote.common.domain.JustSection_Impl;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.draftquote.action.AddAttachmentAction;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.contract.SpecialBidCommentDocumentsContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author helenyu
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class AddSpecialBidAttachmentAction extends AddAttachmentAction {
    private static final long serialVersionUID = 4417529517020147699L;

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        logContext.debug(this, "Begin to execute AddSpecialBidAttachmentAction.executeBiz");
        uploadFile(contract, handler);
        addContractToResult(contract, handler);
        logContext.debug(this, "End executing AddSpecialBidAttachmentAction.executeBiz");

        handler.setState(StateKeys.STATE_DISPLAY_ATTACH_FILES_COMPLETE);
        return handler.getResultBean();
    }

    protected void addContractToResult(ProcessContract contract, ResultHandler handler) {
        super.addContractToResult(contract, handler);
        SpecialBidCommentDocumentsContract sbContract = (SpecialBidCommentDocumentsContract) contract;
        String role = sbContract.getUserRole();
        handler.addObject(ParamKeys.PARAM_USER_ROLE, role);
        handler.addObject(DraftQuoteParamKeys.PARAM_ATT_CODE, sbContract.getAttCode());
    }
    
    protected String getAttchmtClassfctnCode() {
    	return QuoteConstants.QT_ATTCHMNT_SPEL_BID;
    }
    
    protected void handleFileUploadMessages(ResultHandler handler, List files, Locale locale, ProcessContract contract) 
    {
    	super.handleFileUploadMessages(handler, files, locale, contract);
    	SpecialBidCommentDocumentsContract attachContract = (SpecialBidCommentDocumentsContract)contract;
    	if (!StringUtils.isEmpty(attachContract.getAttCode()) )
    	{
    		return;
    	}
    	try
		{
    		SpecialBidInfo bidInfo = attachContract.getSpBidInfo();    		
        	if ( bidInfo == null )
        	{
        		logContext.debug(this, "bidinfo is null");
        		return;
        	}
        	logContext.debug(this, "spbid info code: " + bidInfo.hashCode());
        	for ( int i = 0; i < files.size(); i++ )
        	{
        		com.ibm.dsw.quote.common.domain.QuoteAttachment att = (com.ibm.dsw.quote.common.domain.QuoteAttachment)files.get(i);
        		att.setAddByUserName(attachContract.getUserName());
        		att.setAddDate(new java.util.Date());
        		int secId = -1;
        		try
    			{
        			secId = Integer.parseInt(att.getSecId());
    			}
        		catch ( Throwable e )
    			{
        			secId = SpecialBidInfo.BEGIN_SUBMITTER + 1;
    			}
        		logContext.debug(this, "add file to pre bean: fileName=" + att.getFileName() + ", secId=" + att.getSecId() + ":" + secId);
        		if ( secId == SpecialBidInfo.BEGIN_REVIEWER )
        		{
        			logContext.debug(this, "add reviewer attachs");
        			List list = bidInfo.getReviewerAttachs();
        			list.add(att);
        		}
        		else if ( secId == SpecialBidInfo.BEGIN_APPROVER )
        		{
        			logContext.debug(this, "add approver attachs");
        			List list = bidInfo.getApproverAttachs();
        			list.add(att);
        		}
        		else if ( secId >= SpecialBidInfo.BEGIN_SUBMITTER )
        		{
        			logContext.debug(this, "add sales attachs");
        			List secList = bidInfo.getJustSections();
        			if ( secList != null )
        			{
        				JustSection sec = null;
        				for ( int j = 0; j < secList.size(); j++ )
        				{
        					JustSection temp = (JustSection)secList.get(j);
        					if ( secId == temp.getSecId() )
        					{
        						sec = temp;
        						break;
        					}
        				}
        				if ( sec == null )
        				{
        					logContext.debug(this, "sec in secList is null, create new and add to secList");
        					sec = new JustSection_Impl();
        					sec.setSecId(secId);
        					secList.add(sec);
        				}
        				sec.getAttachs().add(att);
        			}
        		}
        	}
        	Collections.sort(bidInfo.getJustSections());
		}
    	catch ( Throwable t )
		{
    		logContext.error(this, t);
		}
    }
}
