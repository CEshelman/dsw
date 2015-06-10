package com.ibm.dsw.quote.draftquote.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DraftRQMessageKeys</code> class is to define message keys for
 * draft renewal quote.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: Apr 16, 2007
 */
public interface DraftRQMessageKeys {
    public static final String RQ_STATUS = "renewal_quote_status";

    public static final String RQ_RENEWAL_STATUS = "rnwl_status";
    
    public static final String RQ_SECONDARY_STATUS = "secondary_status";
    
    public static final String CHANGE_RQ_STATUS = "change_rq_status";

    public static final String ENTER_RQ_STATUS = "enter_rq_status";

    public static final String BLOCKED_FOR_AUTORENEWAL = "blocked_for_autorenewal";

    public static final String TERMINATION_TRACKING = "termination_tracking";

    public static final String TERMINATION_TRACKING_DESC = "termination_tracking_desc";

    public static final String TERMINATION_REASON = "termination_reason";

    public static final String TERMINATION_COMMENTS = "comments";

    public static final String ENTER_MAX_CHARS = "enter_254_chars";
    
    public static final String ATTACH_TERMINATION_EMAIL = "attach_termination_email";
    
    public static final String DEFAULT_REASON_OPTION = "default_option";
    
    public static final String ERR_INVALID_ENTRY = "validation.invalid";
}
