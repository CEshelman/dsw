package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartPriceTranPriceLevel</code> class is POJO which contain
 * minimaze points for transactional level.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-30
 */
public class PartPriceTranPriceLevel implements Comparable, Serializable {
    private String volDiscLevelCode;

    private double minTranPts;

    private String tranPriceLevelCode;

    /**
     * @param volDiscLevelCode
     * @param minTranPts
     * @param tranPriceLevelCode
     */
    public PartPriceTranPriceLevel(String volDiscLevelCode, double minTranPts, String tranPriceLevelCode) {
        super();
        this.volDiscLevelCode = volDiscLevelCode;
        this.minTranPts = minTranPts;
        this.tranPriceLevelCode = tranPriceLevelCode;
    }

    public double getMinTranPts() {
        return minTranPts;
    }

    /**
     * @param minTranPts
     *            The minTranPts to set.
     */
    public void setMinTranPts(int minTranPts) {
        this.minTranPts = minTranPts;
    }

    public String getTranPriceLevelCode() {
        return tranPriceLevelCode;
    }

    /**
     * @param tranPriceLevelCode
     *            The tranPriceLevelCode to set.
     */
    public void setTranPriceLevelCode(String tranPriceLevelCode) {
        this.tranPriceLevelCode = tranPriceLevelCode;
    }

    public String getVolDiscLevelCode() {
        return volDiscLevelCode;
    }

    /**
     * @param volDiscLevelCode
     *            The volDiscLevelCode to set.
     */
    public void setVolDiscLevelCode(String volDiscLevelCode) {
        this.volDiscLevelCode = volDiscLevelCode;
    }

    public int compareTo(Object o) {
        PartPriceTranPriceLevel p = (PartPriceTranPriceLevel) o;
        return (this.getMinTranPts() > p.getMinTranPts() ? 1 : 0);
    }
}
