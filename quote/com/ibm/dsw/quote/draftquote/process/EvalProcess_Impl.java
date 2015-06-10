package com.ibm.dsw.quote.draftquote.process;

import java.util.List;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.common.domain.EvalQuote;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

public abstract class EvalProcess_Impl extends TopazTransactionalProcess implements EvalProcess {
	
	private LogContext logContext = LogContextFactory.singleton().getLogContext();
	
	@Override
	public List<EvalQuote> getUnderEvalQtList(String creator_id, String isTeleSales)throws QuoteException {
		// TODO Auto-generated method stub
		List<EvalQuote> list = null;
		try {
			super.beginTransaction();
			list = this.getEvalQuotes(creator_id, isTeleSales);
			super.commitTransaction();
		} catch (TopazException e) {
			// TODO Auto-generated catch block
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
		}finally{
			super.rollbackTransaction();
		}
		return list;
	}
	
	public abstract List<EvalQuote> getEvalQuotes(String creator_id, String isTeleSales)throws TopazException;
	
	@Override
	public List<String> getEvalsByCntry(String cntry_code)throws QuoteException{
		List<String> list = null;
		try {
			super.beginTransaction();
			list = this.getEvalsList(cntry_code);
			super.commitTransaction();
		} catch (TopazException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
		}finally{
			super.rollbackTransaction();
		}
		return list;
	}
	
	public abstract List<String> getEvalsList(String cntry_code)throws TopazException;

}
