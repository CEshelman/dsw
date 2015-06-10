package com.ibm.dsw.quote.newquote.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>NewQuoteDBConstants</code> class is the class to define DB
 * constants.
 * 
 * @author: wangxu@cn.ibm.com
 * 
 * Created on: Mar 13, 2007
 */
public interface NewQuoteDBConstants {
    public static final String PART_DELIMIT = "_";

    public static final String DELIMIT = ",";

    public static final String UPLOADWARNING_DELIMIT = ",,";

    public static final String AUDIENCE = "INTERNAL";

    public static final String SP_LOAD_QUOTES = "SELECT_QUOTES";
    
    public static final String SP_LOAD_SAVE_QUOTE = "LOAD_SAVED_QUOTE";

    public static final String SP_CREATE_QUOTE = "I_QT_QUOTE";

    public static final String SP_DELETE_QUOTE = "DELETE_QUOTE";
    
    public static final String SP_VALIDATE_RENEWAL_LINEITEM = "U_QT_VALID_REN";
    
    public static final String S_QT_SAAS_PART_BY_ID = "S_QT_SAAS_PART_BY_ID";
    
    public static final String S_QT_PART_BY_ID = "byNum";
    
    public static final String S_QT_PID_BY_PARTS = "S_QT_PID_BY_PARTS";
    
    public static final String IU_QT_APPLIANCE  = "IU_QT_APPLIANCE";
}
