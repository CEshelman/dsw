/**
 * 
 */
package com.ibm.dsw.quote.configurator.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.configurator.contract.MonthlySwConfiguratorBaseContract;
import com.ibm.dsw.quote.configurator.process.MonthlyConfiguratorProcess;
import com.ibm.dsw.quote.configurator.process.MonthlySwConfiguratorProcessFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @ClassName: MonthlySwConfiguratorAction
 * @author Frank
 * @Description: TODO
 * @date Dec 18, 2013 2:36:54 PM
 *
 */
public abstract class MonthlySwBaseConfiguratorAction extends BaseContractActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final LogContext logger = LogContextFactory.singleton()
			.getLogContext();

	
	protected  MonthlyConfiguratorProcess getMonthlySwProcess(String configrtnActionCode)throws QuoteException{
		return MonthlySwConfiguratorProcessFactory.singleton().createConfiguratorProcess(configrtnActionCode);
	}
	
	protected Quote fetchCurrentQuote(MonthlySwConfiguratorBaseContract contract) throws QuoteException {
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		
		Quote quote = null;
        
        try {
            quote = quoteProcess.getDraftQuoteBaseInfo(contract.getUserId());
        } catch (NoDataException e) {
            logContext.error(this, "NoDataExceptoin accor when geting quote base info.");
            logContext.error(this, e.getMessage());
            return null; 
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
            throw e;
        }
        return quote;
	}
	
	protected abstract String getState(ProcessContract contract) ;

}
