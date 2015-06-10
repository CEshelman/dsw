/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * 
 * @author: Jack Liao(liaoqc@cn.ibm.com)
 * 
 * Creation date: Aug 6, 2014
 */
package com.ibm.dsw.quote.configurator.process.jdbc;

import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;


/**
 * DOC class global comment. Detailled comment
 */
public class ConfiguratorPartCombine {

    private ConfiguratorPart associatedDailyPart;

    private ConfiguratorPart associatedOveragePart;

    /**
     * Getter for associatedDailyPart.
     * 
     * @return the associatedDailyPart
     */
    public ConfiguratorPart getAssociatedDailyPart() {
        return this.associatedDailyPart;
    }

    /**
     * Sets the associatedDailyPart.
     * 
     * @param associatedDailyPart the associatedDailyPart to set
     */
    public void setAssociatedDailyPart(ConfiguratorPart associatedDailyPart) {
        this.associatedDailyPart = associatedDailyPart;
    }

    /**
     * Getter for associatedOveragePart.
     * 
     * @return the associatedOveragePart
     */
    public ConfiguratorPart getAssociatedOveragePart() {
        return this.associatedOveragePart;
    }

    /**
     * Sets the associatedOveragePart.
     * 
     * @param associatedOveragePart the associatedOveragePart to set
     */
    public void setAssociatedOveragePart(ConfiguratorPart associatedOveragePart) {
        this.associatedOveragePart = associatedOveragePart;
    }

}
