/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * 
 * @author: Jack Liao(liaoqc@cn.ibm.com)
 * 
 * Creation date: Jan 17, 2014
 */
package com.ibm.dsw.quote.configurator.process;

import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;

/**
 * DOC class global comment. Detailled comment
 */
public class MonthlySwLineItemConfiguratorPair {

    private MonthlySwLineItem monthlySwLineItem;

    private MonthlySwConfiguratorPart configuratorPart;

    /**
     * DOC MonthlySwLineItemConfiguratorPair constructor comment.
     * 
     * @param monthlySwLineItem
     * @param configuratorPart
     */
    public MonthlySwLineItemConfiguratorPair(MonthlySwLineItem monthlySwLineItem, MonthlySwConfiguratorPart configuratorPart) {
        super();
        this.monthlySwLineItem = monthlySwLineItem;
        this.configuratorPart = configuratorPart;
    }

    /**
     * Getter for monthlySwLineItem.
     * 
     * @return the monthlySwLineItem
     */
    public MonthlySwLineItem getMonthlySwLineItem() {
        return this.monthlySwLineItem;
    }

    /**
     * Getter for configuratorPart.
     * 
     * @return the configuratorPart
     */
    public MonthlySwConfiguratorPart getConfiguratorPart() {
        return this.configuratorPart;
    }
}
