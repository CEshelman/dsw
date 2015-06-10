package com.ibm.dsw.quote.relatedbid.config;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.common.config.QuoteParamKeys;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RelatedBidParamKeys<code> class.
 *    
 * @author: daflores@us.ibm.com
 * 
 * Creation date: Jan 14, 2013 
 */

public interface RelatedBidParamKeys extends ParamKeys, QuoteParamKeys {

    public static final String PARAM_RELATED_ITEM_SELECT = "relatedItemSel";

    public static final String SELECT_SI_SIEBEL_RADIO = "sionSiebelSel";
    
    public static final String PARAM_SI_SIEBEL_RADIO = "sionSiebel";

    public static final String PARAM_SI_BRIEF_TITLE = "briefTitle";
    
    public static final String EMPTY = "Empty";
    
    public static final String STATE_DISPLAY_RELATED_RESULTS = "STATE_DISPLAY_RELATED_RESULTS";
    
    public static final String PARAM_RENEWAL_ITEM = "PARAM_RENEWAL_ITEM";
    

    public static final String PARAM_ERR_MSG = "paramErrorMessage";

    public static final String PARAM_RESOURCE_PREFIX = "resourcePrefix";
    
}