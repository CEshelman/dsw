package com.ibm.dsw.quote.common.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CommonServiceConstants<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Apr 9, 2007
 */

public interface CommonServiceConstants {
    
    public static final String SERVICE_PREFIX = "java:comp/env/service/";
    public static final String QUOTE_TIMESTAMP_BINDING = SERVICE_PREFIX+ "QuoteTimestampWSExport_QuoteTimestampHttpService";
	public static final String CUSTOMER_CREATE_BINDING = SERVICE_PREFIX + "CustomerCreateWSExport_CustomerCreateHttpService";
	public static final String QUOTE_STATUS_CHANGE_BINDING = SERVICE_PREFIX + "QuoteStatusChangeWSExport_QuoteStatusChangeHttpService";
	public static final String CONTENT_MANAGER_BINDING = SERVICE_PREFIX + "CMWIHttpServiceService";
    public static final String SPECIAL_BIDS_API_BINDING = SERVICE_PREFIX + "SpecialBidsAPI";
    public static final String QUOTE_CREATE_BINDING = SERVICE_PREFIX + "QuoteCreateWSExport_QuoteCreateHttpService";
    public static final String QUOTE_MIGRATION_BINDING = SERVICE_PREFIX + "FctToPaMigrationWSExport_FctToPaMigrationHttpService";
    public static final String QUOTE_MODIFY_BINDING = SERVICE_PREFIX + "QuoteModifyWSExport_QuoteModifyHttpService";
    public static final String PRICING_SERVICE_BINDING = SERVICE_PREFIX + "PricingServiceWSExport_PricingServiceHttpService";
    public static final String CONTENT_MANAGER_DOC_ID_BINDING = SERVICE_PREFIX + "ContentMgrDocIdWSExport_ContentManagerDocIdHttpService";
    public static final String QUICK_ENROLLMENT_BINDING = SERVICE_PREFIX + "QuickEnrollmentWSExport_QuickEnrollmentHttpService";
    public static final String YIF_WEB_SERVICE_BINDING = SERVICE_PREFIX + "YIFWebServiceService";
    public static final String SAPIDOC_C_CODE = "C";
    public static final String SAPIDOC_T_CODE = "T";
}
