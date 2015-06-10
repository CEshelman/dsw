package com.ibm.dsw.quote.configurator.process.jdbc;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcess;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcessFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerProcessFactory_jdbc<code> class.
 *    
 * @author: jhma@cn.ibm.com
 * 
 * Creation date: June 18, 2011
 */
public class ConfiguratorPartProcessFactory_jdbc extends ConfiguratorPartProcessFactory {

    /**
     *  
     */
    public ConfiguratorPartProcessFactory_jdbc() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.home.action.process.CustomerProcessFactory#create()
     */
    public ConfiguratorPartProcess create() throws QuoteException {
        return new ConfiguratorPartProcess_jdbc();
    }

}
