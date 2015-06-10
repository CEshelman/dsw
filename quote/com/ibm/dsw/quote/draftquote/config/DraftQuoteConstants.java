package com.ibm.dsw.quote.draftquote.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>DraftQuoteConstants<code> class.
 *
 * @author: zhaoxw@cn.ibm.com
 *
 * Creation date: Mar 30, 2007
 */

public interface DraftQuoteConstants {

    public static final String OPPNUM_RADIO_OPPNUM = "0";
    public static final String OPPNUM_RADIO_EXEMPTNCODE = "1";
    public static final String OPPNUM_RADIO_80_EXEMPTNCODE = "3";
    public static final String SELECT_RADIO_OPPNUM = "2";

    public static final String EXEMPTNCODE_30 = "30";
    public static final String EXEMPTNCODE_40 = "40";
    public static final String EXEMPTNCODE_50 = "50";
    public static final String EXEMPTNCODE_60 = "60";
    public static final String EXEMPTNCODE_70 = "70";
    public static final String EXEMPTNCODE_80 = "80";

    public static final double QUOTE_TOTAL_VALUE_UPPER_LIMIT = 50000;
    public static final double QUOTE_TOTAL_VALUE_ZERO = 0;

    public static final String PARTNER_ACCESS_YES = "1";
    public static final String PARTNER_ACCESS_NO = "0";

    public static final String UPSIDE_TRANSACTION_YES = "Y";
    public static final String UPSIDE_TRANSACTION_NO = "N";

    public static final int MAX_RQ_SALSE_COMM = 254;

    public static final int MAX_RNWL_END_DATE_EXPIRE_DAYS = 90;

    public static final String SEPARATOR = "-";

    public static final String ED_SVP = "ED";

    public static final String GV_SVP = "GV";

    public static final String NP_MSG = "- No price -";

    public static final String BLANK = "";
    
    public static final String N_A = "N/A";

    public static final String BLANK_SPACE = "&nbsp;";

    public static final String BLOCK_RENEWAL_REMINDER_YES = "Y";

    public static final String BLOCK_RENEWAL_REMINDER_NO = "N";

    public static final String PYMNT_TERMS_RADIO_STAND="0";

    public static final String PYMNT_TERMS_RADIO_MANUAL="1";

    public static final int PYMNT_TERMS_STAND_DAYS = 30;

    public static final int PYMNT_TERMS_MAX_DAYS = 120;

    public static final int PYMNT_BID_VALIDITY_DAYS = 30;

    public static final String VALUE_TRUE = "true";
    public static final String VALUE_false = "false";
    
    public static final String TO_BE_EVAL = "Awaiting assignment";
    
    public static final String ASGN_EVAL = "Under evaluation";
    
    public static final String RETD_SESS_EVAL = "Returned";
    
    public static final String DELT_EVAL = "Deleted";
    
    public static final String LPP_POPUP_SOURCE_CURRENCY_CONVERSION = "CURRNCY";
    public static final String LPP_POPUP_SOURCE_OVRRD_LPP = "LPP_OVRRD";
    public static final String LPP_POPUP_SOURCE_NO_LPP = "NO_LPP";
    public static final String LPP_SOURCE_DEFAULT = "DEFAULT";
    
    public static final String APPLIANCE_ADDRESS_LIST = "APPLIANCE_ADDRESS_LIST";
    public static final String SELECTED_CUSTOMER = "SELECTED_CUSTOMER";
    public static final String SOLD_TO_ADDRESS = "SOLD_TO_ADDRESS";
    public static final String LINE_ITEM_MAP = "LINE_ITEM_MAP";
    public static final int DEFAULT_APPLIANCE_ADDRESS = -1;
    public static final String APPLIANCE_ADDRESS_OPTION = "POOPTION";
    
    public static final String APPLIANCE_SHIPTO_ADDTYPE = "0";
    public static final String APPLIANCE_INSTALLAT_ADDTYPE = "1";
}
