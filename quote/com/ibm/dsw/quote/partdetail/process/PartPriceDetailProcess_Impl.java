/*
 * Created on 2007-4-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.partdetail.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.partdetail.domain.PartPriceDetail;
import com.ibm.dsw.quote.partdetail.domain.PartPriceDetailFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PartPriceDetailProcess_Impl extends TopazTransactionalProcess implements PartPriceDetailProcess {

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.partdetail.process.PartPriceDetailProcess#findByPartNumberCountryCurrency(java.lang.String, java.lang.String, java.lang.String)
     */
    public PartPriceDetail getPartDetails(String partNumber, String webQNumber, String priceType, boolean loadCoPrerequisites)throws QuoteException {
        PartPriceDetail detail = null;
        try{
        	this.beginTransaction();
            detail = PartPriceDetailFactory.singleton().getPartDetails(partNumber, webQNumber, priceType, loadCoPrerequisites);
            this.commitTransaction();
        }catch(TopazException e){
            throw new QuoteException(e);
        }finally{
        	this.rollbackTransaction();
        }
        return detail;
    }
    
}
