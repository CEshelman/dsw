/*
 * Created on 2012-5-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.draftquote.process.MigrationRequestProcess;
import com.ibm.dsw.quote.draftquote.process.MigrationRequestProcessFactory;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MigrationRequestProcessFactory_jdbc extends MigrationRequestProcessFactory {

    public MigrationRequestProcess create() throws QuoteException {
        return new MigrationRequestProcess_jdbc();
    }

}
