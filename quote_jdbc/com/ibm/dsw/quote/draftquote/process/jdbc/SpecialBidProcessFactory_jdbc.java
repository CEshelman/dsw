/*
 * Created on May 15, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcess;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcessFactory;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpecialBidProcessFactory_jdbc extends SpecialBidProcessFactory {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.process.SpecialBidProcessFactory#create()
     */
    public SpecialBidProcess create() throws QuoteException {
        return new SpecialBidProcess_jdbc();
    }

}
