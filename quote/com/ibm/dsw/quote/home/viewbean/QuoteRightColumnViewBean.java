package com.ibm.dsw.quote.home.viewbean;

import is.domainx.User;

import java.util.List;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>sessionQuoteRightColumnViewBean<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 5, 2007
 */

public class QuoteRightColumnViewBean extends BaseViewBean {
    
    protected boolean isDisplayRighColumn = false;
    
    protected String sWebQuoteNum ;

    protected String sCustName;

    protected int iNumOfParts = 0;

    protected String sQuoteTypeCode;
    
    protected String myDraftQuoteURL;
    
    protected transient List sqoHeadLineMsgs;
    
    protected User user = null;
    
    public void collectResults(Parameters param) throws ViewBeanException {
        super.collectResults(param);
        QuoteRightColumn quoteRightColumn = (QuoteRightColumn)param.getParameter(ParamKeys.PARAM_QUOTE_RIGHTCOLUMN);
        if (quoteRightColumn != null){
            isDisplayRighColumn = true;
            sWebQuoteNum = quoteRightColumn.getSWebQuoteNum();
            sCustName = quoteRightColumn.getSCustName();
            iNumOfParts = quoteRightColumn.getINumOfParts();
            sQuoteTypeCode = quoteRightColumn.getSQuoteTypeCode();
            myDraftQuoteURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_CURRENT_DRAFT_QUOTE);
        }
        sqoHeadLineMsgs = (List)param.getParameter(ParamKeys.PARAM_SQO_HEADLINE_MSG);
        user = (User) param.getParameter(ParamKeys.PARAM_USER_OBJECT);
    }
  
    /**
     * @return Returns the isDisplayRighColumn.
     */
    public boolean isDisplayRighColumn() {
        return isDisplayRighColumn;
    }
    /**
     * @return Returns the iNumOfParts.
     */
    public int getINumOfParts() {
        return iNumOfParts;
    }
    /**
     * @return Returns the sCustName.
     */
    public String getSCustName() {
        return sCustName;
    }
    /**
     * @return Returns the sQuoteTypeCode.
     */
    public String getSQuoteTypeCode() {
        return sQuoteTypeCode;
    }
    /**
     * @return Returns the sWebQuoteNum.
     */
    public String getSWebQuoteNum() {
        return sWebQuoteNum;
    }
    /**
     * @return Returns the myDraftQuoteURL.
     */
    public String getMyDraftQuoteURL() {
        return myDraftQuoteURL;
    }
 
    /**
     * @return Returns the sqoHeadLineMsg.
     */
    public List getSqoHeadLineMsgs() {
        return sqoHeadLineMsgs;
    }
    
    public boolean isApproverLogin(){
        return user.getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_APPROVER;
    }
    
    public boolean isDisplayStatusLink(){
    	return QuoteConstants.BPTierModel.BP_TIER_MODEL_ONE.equalsIgnoreCase(getQuoteUserSession().getBpTierModel());
    }
}
