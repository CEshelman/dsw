/*
 * Created on 2007-5-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.contract;

import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.domain.Domain;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvrFactory;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UpdateApproverContract extends SaveDraftCommentsBaseContract {

	private static final long serialVersionUID = 887811175509797457L;

	private transient List approvers;
    
    private String quoteNum;
    
    private String displayAction;
    
    
    public String getDisplayAction() {
        return displayAction;
    }
   
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        this.displayAction = parameters.getParameterAsString(SubmittedQuoteParamKeys.PARAM_DISPLAY_ACTION);
        approvers = new ArrayList();
        for(int i=0 ;i <= QuoteConstants.APPROVAL_TYPE_MAX_LEVLE; i++){
            String approverEmail = parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_APPROVER_LEVEL+i);
            String approverGroup = null;
            String predecessorEmail = null;
            SpecialBidApprvr approver = null;
            if(approverEmail != null && !"".equals(approverEmail.trim())){
                try{
	                approver = SpecialBidApprvrFactory.singleton().createSpecialBidApprvrForActionUpdate();
	                approver.setMode(Domain.DOMAIN_MODE_VO);
	                approver.setApprvrEmail(approverEmail);
	                approverGroup = parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_APPROVER_GROUP+i);
	                if(approverGroup != null && !"".equals(approverGroup)){
	                    approver.setSpecialBidApprGrp(approverGroup);
	                }
	                approver.setSpecialBidApprLvl(i);
	                approver.setApplierEmail(getUserId());
	                predecessorEmail = parameters.getParameterAsString(DraftQuoteParamKeys.PARAM_PRIOR_APPROVER+i);
	                if(predecessorEmail != null && !"".equals(predecessorEmail)){
		                approver.setPredecessorEmail(predecessorEmail);
	                }
	                approver.setWebQuoteNum(quoteNum);
                }catch(Exception e){
                	LogContextFactory.singleton().getLogContext().error(this, e);
                }
            }
            if(approver != null){
                approvers.add(approver);
            }
        }
    }
    
    public void setQuoteNum(String quoteNum) {
        this.quoteNum = quoteNum;
    }
    
    public String getWebQuoteNum() {
        return quoteNum;
    }
    
    public List getApprovers() {
        return approvers;
    }
}
