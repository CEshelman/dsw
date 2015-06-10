package com.ibm.dsw.quote.newquote.config;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NewQuoteStateKeys<code> class .
 *    
 * @author: wangxu@cn.ibm.com
 * Creation date: Mar 13, 2007
 */
public interface NewQuoteStateKeys {
    public static final String DISPLAY_DRAFT_SALES_QUOTES = "STATE_DISPLAY_DRAFT_SALES_QUOTES";

    public static final String STATE_DISPLAY_NEW_QUOTE = "STATE_DISPLAY_NEW_QUOTE";

    // upload
    public static final String STATE_DISPLAY_UPLOAD_PAGE = "STATE_DISPLAY_UPLOAD_PAGE";

    public static final String STATE_UPLOAD_SUCCESSFULLY = "STATE_UPLOAD_SUCCESSFULLY";

    public static final String STATE_REDIRECT_TO_CURRENT_DRAFT_QUOTE = "STATE_REDIRECT_TO_CURRENT_DRAFT_QUOTE";
    
    public static final String OPPORTUNITY_NUM_JSON = "OPPORTUNITY_NUM_JSON";
    
    public static final String DSJ_SEARCH_PARTNER_RESULT_JSON = "DSJ_SEARCH_PARTNER_RESULT_JSON";
}
