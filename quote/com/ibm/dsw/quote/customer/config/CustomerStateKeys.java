package com.ibm.dsw.quote.customer.config;

import com.ibm.dsw.quote.base.config.StateKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerStateKeys<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Apr 20, 2007
 */

public interface CustomerStateKeys extends StateKeys {

    public static final String STATE_DISPLAY_SEARCH_CUSTOMER = "STATE_DISPLAY_SEARCH_CUSTOMER";
    public static final String STATE_DISPLAY_CUSTOMER_RESULTS = "STATE_DISPLAY_CUSTOMER_RESULTS";
    public static final String STATE_DISPLAY_CREATE_CUSTOMER = "STATE_DISPLAY_CREATE_CUSTOMER";
    public static final String STATE_DISPLAY_DUP_CUST = "STATE_DISPLAY_DUP_CUST";
    
    public static final String STATE_DISPLAY_SEARCH_END_USER = "STATE_DISPLAY_SEARCH_END_USER";
    public static final String STATE_DISPLAY_END_USER_RESULTS = "STATE_DISPLAY_END_USER_RESULTS";

    public static final String STATE_REDIRECT_TO_CONF_FORM = "STATE_REDIRECT_TO_CONF_FORM";
    public static final String STATE_CONF_SELECT_RESULT = "STATE_CONF_SELECT_RESULT";
    public static final String STATE_CREATE_NEW_QUOTE_RESULT = "STATE_CREATE_NEW_QUOTE_RESULT";
    
    public static final String STATE_DISPLAY_APPLIANCE_ADDRESS="STATE_DISPLAY_APPLIANCE_ADDRESS";
    public static final String STATE_DISPLAY_APPLIANCE_ADDRESS_RO="STATE_DISPLAY_APPLIANCE_ADDRESS_READ_ONLY";
    public static final String STATE_DISPLAY_SELECT_APPLIANCE_ADDRESS = "STATE_DISPLAY_SELECT_APPLIANCE_ADDRESS";
    public static final String STATE_DISPLAY_SUBMITTEDQT_SELECT_APPLIANCE_ADDRESS = "STATE_DISPLAY_SUBMITTEDQT_SELECT_APPLIANCE_ADDRESS";
    public static final String STATE_DISPLAY_APPLIANCE_ADDRESS_RESULTS = "STATE_DISPLAY_APPLIANCE_ADDRESS_RESULTS";
    public static final String STATE_DISPLAY_SUBMITTEDQT_APPLIANCE_ADDRESS_RESULTS = "STATE_DISPLAY_SUBMITTEDQT_APPLIANCE_ADDRESS_RESULTS";
    public static final String STATE_DISPLAY_DUP_APPLIANCE_ADDRESS = "STATE_DISPLAY_DUP_APPLIANCE_ADDRESS";
    public static final String STATE_DISPLAY_SUBMITTEDQT_DUP_APPLIANCE_ADDRESS = "STATE_DISPLAY_SUBMITTEDQT_DUP_APPLIANCE_ADDRESS";
	public static final String STATE_DISPLAY_SUBMITTEDQT_APPLIANCE_ADDRESS = "STATE_DISPLAY_SUBMITTEDQT_APPLIANCE_ADDRESS";
}
