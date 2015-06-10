package com.ibm.dsw.quote.login.history.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.login.history.process.QuoteLoginHistoryProcess;
import com.ibm.dsw.quote.login.history.process.QuoteLoginHistoryProcessFactory;

public class QuoteLoginHistoryProcessFactory_jdbc extends QuoteLoginHistoryProcessFactory {
    public QuoteLoginHistoryProcess create() throws QuoteException {
        return new QuoteLoginHistoryProcess_jdbc();
    }
}
