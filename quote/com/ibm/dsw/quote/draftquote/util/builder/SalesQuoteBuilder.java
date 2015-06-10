package com.ibm.dsw.quote.draftquote.util.builder;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SalesQuoteBuilder</code> class is a Sales quote Builder
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Apr 17, 2007
 */

public class SalesQuoteBuilder extends DraftQuoteBuilder {

    SalesQuoteBuilder(Quote q, String userID) {
        super(q, userID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.draftquote.util.builder.QuoteBuilder#buildModel()
     */
    
    public boolean needRecalculatePrice()throws TopazException { 
    	return true;	
    }
        
    protected void preBuild() throws TopazException {
        
        List items = quote.getLineItemList();
        
        for (int i = 0; i < items.size(); i++) {
            
            QuoteLineItem item = (QuoteLineItem) items.get(i);
            
           
            if ((item.getRenewalQuoteNum() == null) || ( "".equals(item.getRenewalQuoteNum()))){
                // not a part added from renewal quote
                continue;
            }
            String renwlQuoteNum = StringUtils.isNotBlank(item.getRenewalQuoteNum())?item.getRenewalQuoteNum().trim():null;
            
            String renwlChgcode = item.getRenwlChgCode();
            // if RQ not open or parts removed, remove RQ reference from Sales quote
            if ((null != renwlChgcode) && !"".equals(renwlChgcode) 
                    && (PartPriceConstants.RenwlChgCode.RQCLOSED.equals(renwlChgcode) || PartPriceConstants.RenwlChgCode.RQLIREMD.equals(renwlChgcode)) ) {
                logContext.debug(this,"Find Renwl Chang Code for line item (" +item.getPartNum()+")"+renwlChgcode );
                // clear the renewal quote info
                item.setRenewalQuoteNum(null);
                item.setRenewalQuoteSeqNum(0);
                item.setRenewalQuoteEndDate(null);
                item.setRenwlChgCode("");
            }
            
            item.getPartDispAttr().setRenwlChgCode(renwlChgcode);
            item.getPartDispAttr().setRenwlQuoteNum(renwlQuoteNum);

        }
        
    }
    
    public void clearCalculateFlag() throws TopazException{
        QuoteHeader header = quote.getQuoteHeader();
        header.setRecalcPrcFlag(0);
        
    }

    
}
