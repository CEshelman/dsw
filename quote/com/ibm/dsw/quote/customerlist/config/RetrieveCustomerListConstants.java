package com.ibm.dsw.quote.customerlist.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RetrieveCustomerListConstants<code> class.
 *    
 * @author: cuixg@cn.ibm.com
 * 
 * Creation date: 2008-6-26
 */

public interface RetrieveCustomerListConstants {
    String RETRIEVE_CUSTOMER_BASE = "appl/i18n/retrieveCustomer";
    
    //option indicator
    int RETRIEVE_CUSTOMERS_BY_ID = 1;
    int RETRIEVE_CUSTOMERS_BY_ATTRIBUTE = 2;

    //error code definition
    String ERROR_CODE_1000 = "1000";
    String ERROR_CODE_1001 = "1001";
    String ERROR_CODE_1003 = "1003";
    String ERROR_CODE_1004 = "1004";
    String ERROR_CODE_1005 = "1005";
    String ERROR_CODE_1006 = "1006";
    String ERROR_CODE_1049 = "1049";
    String ERROR_CODE_1050 = "1050";

    //error description key
    String RETRIEVE_SUCCEED = "retrieve_succeed";
    String OPTION_INDICATOR_INVALID = "option_indicator_invalid";
    String CUSTOMER_NUMBER_REQUIRED = "customer_number_required";
    String CUSTOMER_NAME_REQUIRED = "customer_name_required";
    String PROGRAM_TYPE_NOT_SUPPORTED = "program_type_not_required";
    String COUNTRY_CODE_INVALID = "country_code_invalid";
    String CURRENCY_CODE_INVALID = "currency_code_invalid";
    String CUSTOMER_NOT_FOUND = "customer_not_found";
    String END_OF_RECORDS = "end_of_records";
    String MAXIMUM_ROW_EXCEEDED = "maximum_row_exceeded";
    String START_END_IDX_INVALID = "start_end_idx_invalid";
    String UNKONWN_ERROR = "unknown_error";

    //Sql error code
    int SQL_ERROR_CODE_10001 = 10001;
    int SQL_ERROR_CODE_10002 = 10002;
}
