/*
 * Created on 2007-4-10
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.partdetail.domain.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.partdetail.domain.PartPriceDetail;
import com.ibm.dsw.quote.partdetail.domain.PartPriceDetailFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author Administrator
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PartPriceDetailFactory_jdbc extends PartPriceDetailFactory{
    private static final LogContext logger = LogContextFactory.singleton().getLogContext();
    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.partdetail.domain.PartPriceDetailFactory#findByPartNumberCountryCurrency(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public PartPriceDetail getPartDetails(String partNumber, String webQNumber, String priceType, boolean loadCoPrerequisites) throws QuoteException {
        PartPriceDetail_jdbc detail = new PartPriceDetail_jdbc(loadCoPrerequisites);
        try{
            detail.setPartNumber(partNumber);
            detail.setWebQNumber(webQNumber);
            detail.setPriceType(priceType);
            detail.hydrate(TopazUtil.getConnection());
            
        }catch(Exception e){
            logger.error(this, e);
            throw new QuoteException(e);
        }
        return detail;
    }

}
