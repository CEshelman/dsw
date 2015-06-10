package com.ibm.dsw.quote.retrieval;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RetrieveQuoteResultCodes</code> class is to define the return code for
 * the quote retrieval service.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: Apr 24, 2007
 */
public interface RetrieveQuoteResultCodes {
    /* 1000 - Quote retrieval successful */
    public static final int SUCCESS = 1000;

    /* 1001 - The quote does not exist */
    public static final int QUOTE_NOT_FOUND = 1001;

    /* 1002 - The user is not entitled to view the quote */
    public static final int USER_NOT_ENTITLED = 1002;

    /* 1003 - The quote type is not supported */
    public static final int QUOTE_TYPE_NOT_SUPPORTED = 1003;

    /* 1004 - The process does not return all quote data needed */
    public static final int QUOTE_DATA_NOT_COMPLETE = 1004;

    /* 1005 - Some parts are inactive or have no active pricing */
    public static final int PARTS_INACTIVE = 1005;

    /* 1006 - The fulfillment source does not match the quote's source */
    public static final int PARTS_NO_ACTIVE_PRICING = 1006;
    
    /* 1006 - The fulfillment source does not match the quote's source */
    public static final int FULFILLMENT_NOT_MATCHED = 1007;

    /* 1007 - The quote status does not permit ordering */
    public static final int QUOTE_NOT_ORDERABLE = 1008;

    /* 1008 - The quote pricing can not be determined */
    public static final int PRICING_NOT_DETERMINED = 1009;

    /* 1049 - Input parameters not valid */
    public static final int REQUEST_NOT_VALID = 1049;

    /* 1050 - Unknown error code */
    public static final int UNKNOWN_ERROR_CODE = 1050;
    
    public static final int CUSTOMER_NOT_ENROLLED = 1014;
    
    public static final int CUSTOMER_CONTRACT_NUMBER_MISMATCH = 1015;
    
    /* 1017 - Contract number is empty*/
    public static final int CUSTOMER_ENROLL_FAILD = 1017;
}
