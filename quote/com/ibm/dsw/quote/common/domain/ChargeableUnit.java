package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ChargeableUnit</code> class is a pojo contains chargeable unit
 * value
 * 
 * @author: <a href="cuixg@cn.ibm.com">Mark </a>
 * 
 * Creation date: Oct 24, 2007
 */
public class ChargeableUnit implements Serializable{
    private String id;

    private String desc;

    private int quantity;

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Returns the desc.
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc
     *            The desc to set.
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @return Returns the quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity
     *            The quantity to set.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
