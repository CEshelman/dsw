package com.ibm.dsw.quote.massdlgtn.process.jdbc;

import com.ibm.dsw.quote.massdlgtn.process.MassDlgtnProcess;
import com.ibm.dsw.quote.massdlgtn.process.MassDlgtnProcessFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>MassDlgtnProcessFactory_jdbc</code> class is jdbc implementation
 * of MassDlgtnProcessFactory.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-5
 */
public class MassDlgtnProcessFactory_jdbc extends MassDlgtnProcessFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.massdlgtn.process.MassDlgtnProcessFactory#create()
     */
    public MassDlgtnProcess create() throws TopazException {
        return new MassDlgtnProcess_jdbc();
    }

}
