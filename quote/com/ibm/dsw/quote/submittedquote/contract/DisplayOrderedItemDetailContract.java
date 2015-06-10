package com.ibm.dsw.quote.submittedquote.contract;

import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

public class DisplayOrderedItemDetailContract extends SubmittedQuoteBaseContract {
	int destSeqNum;
	
	public int getDestSeqNum() {
		return this.destSeqNum;
	}
	
    public void load(Parameters parameters, JadeSession session) {
    	super.load(parameters, session);
    	destSeqNum = parameters.getParameterAsInt("destSeqNum");
    }
}
