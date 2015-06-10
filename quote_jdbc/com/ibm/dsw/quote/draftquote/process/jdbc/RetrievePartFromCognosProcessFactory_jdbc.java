package com.ibm.dsw.quote.draftquote.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.process.RetrievePartFromCognosProcess;
import com.ibm.dsw.quote.draftquote.process.RetrievePartFromCognosProcessFactory;

public class RetrievePartFromCognosProcessFactory_jdbc extends RetrievePartFromCognosProcessFactory{
    public RetrievePartFromCognosProcess create() throws QuoteException {
        return new RetrievePartFromCognosProcess_jdbc();
    }
}
