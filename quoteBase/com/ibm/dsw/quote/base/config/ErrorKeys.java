package com.ibm.dsw.quote.base.config;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ErrorKeys.java</code> class contains keys for error messages
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public interface ErrorKeys {
    //Error message
    public static final String MSG_UNKNOWN_ERR = "msg_unknown_err";
    public static final String MSG_DATA_MISSING = "msg_data_missing";
    public static final String TOO_MANY_RESULTS = "msg_too_many_results";
    public static final String MSG_INPUT_INVALID = "msg_input_invalid";
    public static final String MSG_PRICE_ENGINE_UNAVAILABLE = "msg_price_engine_unavilable";
    public static final String MSG_PRICE_ENGINE_UNAVAILABLE_FOR_PAYER_DATA_ISSUE = "msg_price_engine_unavilable_payer_data_issue";
    public static final String MSG_PRICE_ENGINE_UNAVAILABLE_FOR_ARITHMETIC_OPERATION_ISSUE="msg_price_engine_unavailable_for_arithmetic_operation_issue";
    public static final String MSG_OFFER_PRICE_ERR = "msg_offer_price_err";
    public static final String MSG_UN_AUTHORIZED = "msg_un_authorized";
    
    public static final String NO_LOB_COUNTRY = "NO_LOB_COUNTRY";
    public static final String NO_QUOTE_TITLE = "NO_QUOTE_TITLE";
    
    public static final String MSG_SELECT_CHKBOX = "msg_select_chkbox";
    public static final String MSG_REQUEST_FAILED = "msg_request_failed";
    
}
