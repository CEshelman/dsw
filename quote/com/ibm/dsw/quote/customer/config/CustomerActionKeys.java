package com.ibm.dsw.quote.customer.config;

import com.ibm.dsw.quote.base.config.ActionKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerActionKeys<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Apr 20, 2007
 */

public interface CustomerActionKeys extends ActionKeys {
    public static final String DISPLAY_SEARCH_CUSTOMER = "DISPLAY_SEARCH_CUSTOMER";
	public static final String DISPLAY_CUSTOMER_RESULTS = "DISPLAY_CUSTOMER_RESULTS";
	public static final String CUSTOMER_SEARCH_ATTR = "CUSTOMER_SEARCH_ATTR";
	public static final String CUSTOMER_SEARCH_DSWID = "CUSTOMER_SEARCH_DSWID";
	public static final String SELECT_CUSTOMER = "SELECT_CUSTOMER";
	public static final String CREATE_NEW_CUSTOMER = "CREATE_NEW_CUSTOMER";
	public static final String INSERT_CUST_AFTER_DUP = "INSERT_CUST_AFTER_DUP";
	public static final String DISPLAY_CREATE_CUSTOMER = "DISPLAY_CREATE_CUSTOMER";
	public static final String SELECT_PAYER = "SELECT_PAYER";
	public static final String DISPLAY_SEARCH_END_USER = "DISPLAY_SEARCH_END_USER";
	public static final String END_USER_SEARCH_ATTR = "END_USER_SEARCH_ATTR";
	public static final String END_USER_SEARCH_DSWID = "END_USER_SEARCH_DSWID";
	public static final String SELECT_END_USER = "SELECT_END_USER";
	public static final String CONFIG_HOSTED_SERVICE = "CONFIG_HOSTED_SERVICE";
	public static final String RETURN_FROM_CONF_TO_SQO = "RETURN_FROM_CONF_TO_SQO";
	public static final String DISPLAY_APPLIANCE_ADDRESS = "DISPLAY_APPLIANCE_ADDRESS";  //Action key for the new ship to address shown.
	public static final String DISPLAY_SEARCH_APPL_ADDRESS = "DISPLAY_SELECT_APPLIANCE_ADDRESS";
	public static final String APPL_ADDRESS_SEARCH = "APPLIANCE_ADDRESS_SEARCH";
	public static final String DISPLAY_CREATE_APPLIANCE_ADDRESS = "DISPLAY_CREATE_APPLIANCE_ADDRESS";
	public static final String CREATE_NEW_APPLIANCE_ADDRESS = "CREATE_NEW_APPLIANCE_ADDRESS";
	public static final String INSERT_APPLIANCE_ADDRESS_AFTER_DUP = "INSERT_APPLIANCE_ADDRESS_AFTER_DUP";
	public static final String SELECT_APPLIANCE_ADDRESS = "SELECT_APPLIANCE_ADDRESS";
	public static final String SAVE_APPLIANCE_ADDRESS = "SAVE_APPLIANCE_ADDRESS";
	public static final String CHECK_DUP_APPLIANCE_ADDRESS = "CHECK_DUP_APPLIANCE_ADDRESS";
}
