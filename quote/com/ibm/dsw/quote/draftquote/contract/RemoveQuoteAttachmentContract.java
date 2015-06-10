package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.submittedquote.contract.SaveDraftCommentsBaseContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RemoveQuoteAttachmentContract<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Sep 23, 2008
 */

public class RemoveQuoteAttachmentContract extends SaveDraftCommentsBaseContract {
    
    private static final long serialVersionUID = 5421582668996109533L;
	protected String webQuoteNum = null;
    protected String attchmtSeqNum = null;
    protected String attCode = null;
    
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
    }
    
    public String getAttchmtSeqNum() {
        return attchmtSeqNum;
    }
    
    public void setAttchmtSeqNum(String attchmtSeqNum) {
        this.attchmtSeqNum = attchmtSeqNum;
    }
    
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    
    public String getAttCode()
	{
		return attCode;
	}

	public void setAttCode(String attCode)
	{
		this.attCode = attCode;
	}

	public void setWebQuoteNum(String webQuoteNum) {
        this.webQuoteNum = webQuoteNum;
    }
}
