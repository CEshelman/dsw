/*
 * Created on 2007-5-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcess;
import com.ibm.dsw.quote.draftquote.process.QuoteAttachmentProcessFactory;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QuoteAttachmentProcessFactory_jdbc extends QuoteAttachmentProcessFactory {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.process.SpecialBidFinalProcessFactory#create()
     */
    public QuoteAttachmentProcess create() throws QuoteException {
        return new QuoteAttachmentProcess_jdbc();
    }

}
