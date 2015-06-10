package com.ibm.dsw.quote.provisng.process;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.process.jdbc.TopazTransactionalProcess_jdbc;

public abstract class RedirectProvisngProcess_Impl extends TopazTransactionalProcess_jdbc implements RedirectProvisngProcess {
	
	protected LogContext logContext = LogContextFactory.singleton().getLogContext();
	
}
