package com.ibm.dsw.quote.partner.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartnerDBConstants</code> class is the class to define DB
 * constants.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 6, 2007
 */
public interface PartnerDBConstants {
    public static final String S_QT_PRTNR_BY_ID = "S_QT_PRTNR_BY_ID";

    public static final String S_QT_PRTNR_BY_ATT = "S_QT_PRTNR_BY_ATT";
    
    public static final String S_QT_PRTNR_BY_PORT = "S_QT_PRTNR_BY_PORT";

    public static final int PARTNER_TYPE_RESELLER = 1;

    public static final int PARTNER_TYPE_DISTRIBUTOR = 0;

    public static final int PARTNER_TYPE_TIER_ALL = 0;

    public static final int PARTNER_TYPE_TIER1 = 1;

    public static final int PARTNER_TYPE_TIER2 = 2;

    public static final int PARTNER_PAGE_ROWS = 100;

    public static final String SEARCH_METHOD_BY_NUM = "0";

    public static final String SEARCH_METHOD_BY_ATT = "1";
    
    public static final String SEARCH_METHOD_BY_PORT = "2";
}
