/*
 * Created on 2007-4-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.partdetail.process;

import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PartPriceDetailProcessFactory {
    private static PartPriceDetailProcessFactory singleton = null;

    public PartPriceDetailProcessFactory() {

    }

    public PartPriceDetailProcess create() throws TopazException{
        return new PartPriceDetailProcess_Impl();
    }

    public static PartPriceDetailProcessFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();
        if (singleton == null) {
            singleton = new PartPriceDetailProcessFactory();
        }
        return singleton;
    }
}
