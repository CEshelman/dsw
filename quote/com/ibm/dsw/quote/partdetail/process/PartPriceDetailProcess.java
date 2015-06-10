/*
 * Created on 2007-4-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.partdetail.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.partdetail.domain.PartPriceDetail;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface PartPriceDetailProcess {
    public PartPriceDetail getPartDetails(String partNumber, String webQNumber, String priceType, boolean loadCoPrerequsites) throws QuoteException;
}
