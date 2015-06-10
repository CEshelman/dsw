/**
 * 
 */
package com.ibm.dsw.quote.common.domain;

import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author CrimsonLin
 *
 */
public class CoverageTermFactory {
	
	private LogContext logContext = LogContextFactory.singleton().getLogContext();

	private static CoverageTermFactory singleton = null;
	
	public static CoverageTermFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (CoverageTermFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = CoverageTermFactory.class.getName();
                Class factoryClass = Class.forName(factoryClassName);
                CoverageTermFactory.singleton = (CoverageTermFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(CoverageTermFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(CoverageTermFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(CoverageTermFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
	
	public CoverageTermTypes getCoTermTypes(Quote quote,PartsPricingConfiguration ppc) throws TopazException {
		if (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(ppc.getConfigrtnActionCode())) {
			return new CoTermAddTrdType(quote,ppc);
		} 
		else if (PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT.equals(ppc.getConfigrtnActionCode())) {
			return new CoTermNewCACTType(quote,ppc);
		} 
		else if (PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(ppc.getConfigrtnActionCode())) {
			return new CoTermFct2PAFnlType(quote,ppc);
		} //PartPriceConstants.ConfigrtnActionCode.NEW_NCT.equals(ConfigrtnActionCode)
		else return new CoTermNewNCTType(quote,ppc);

	}
}
