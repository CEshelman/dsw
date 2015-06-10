/*
 * Created on 2009-10-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.submittedquote.contract;

import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * @author Fred
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UpdateCategoryContract extends SaveDraftCommentsBaseContract {
    
    private String quoteNum;
    private String[] spBidCategories;
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        spBidCategories = parameters.getParameterWithMultiValues(SpecialBidParamKeys.CATEGORY);
    }
    
    public void setQuoteNum(String quoteNum) {
        this.quoteNum = quoteNum;
    }
    
    public String getWebQuoteNum() {
        return quoteNum;
    }
    
    
    /**
     * @return Returns the quoteNum.
     */
    public String getQuoteNum() {
        return quoteNum;
    }
    /**
     * @return Returns the spBidCategories.
     */
    public String[] getSpBidCategories() {
        return spBidCategories;
    }
}
