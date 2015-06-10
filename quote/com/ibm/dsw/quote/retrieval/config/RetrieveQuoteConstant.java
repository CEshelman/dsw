package com.ibm.dsw.quote.retrieval.config;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>RetrieveQuoteConstant</code> class is to define constants for
 * quote retrieval service.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: Apr 24, 2007
 */
public interface RetrieveQuoteConstant {
    /* DIRECT - the fulfillment source of direct */
    public static final String FULFILLMENT_DIRECT = "DIRECT";

    /* CHANNEL - the fulfillment source of channel */
    public static final String FULFILLMENT_CHANNEL = "CHANNEL";
    
    public static final String CHANNEL_H = "H";
    public static final String CHANNEL_J = "J";
    public static final String CHANNEL_E = "E";

    /* QUOTE - the document type of quote */
    public static final String DOCTYPE_QUOTE = "QUOTE";
    
    /* ORDER - the document type of order */
    public static final String DOCTYPE_ORDER = "ORDER";
    
    /* RQ - the ReferringDocType if ReferringDocNumber is not null */
    public static final String REFERRINGDOCTYPE_RQ="RQ";
    /* CA - for the line items not part of addon/tradeup, the reference doc is the service agreement sales order */
    public static final String REFERRINGDOCTYPE_CA="CA";
    
    /* special bid flag */
    public static final int SPECIAL_BID_FLAG = 1;

    /** *************************************************** */

    public static final String ACTION_RETRIEVE_QUOTE = "ACTION_RETRIEVE_QUOTE";

    public static final String STATE_RETRIEVE_QUOTE = "STATE_RETRIEVE_QUOTE";

    public static final String E_QUOTE = "E_QUOTE";
    
    public static final String E_RESULT = "E_RESULT";
    
    public static final String BRETRIEVE_QUOTE_BASENAME = "appl/i18n/retrieveQuote";
    
    /** *************************************************** */
    
    public static final String PARAM_SAP_QUOTE_NUM = "sapQuoteNum";
    
    public static final String PARAM_SAP_QUOTE_IDOC = "sapQuoteIDoc";
    
    public static final String PARAM_WEB_QUOTE_NUM = "webQuoteNum";
    
    public static final String PARAM_FULFILLMENT = "fulfillment";
    
    public static final String PARAM_DOC_TYPE = "docType";
    
    public static final String PARAM_USER_ID = "userID";
    
    public static final String PARAM_USER_FLAG = "userFlag";

    /** *************************************************** */
    
    public static final String MSG_APP_ASSET_ID = "application_asset_id";
    
    public static final String CHG_LI_ST_DT_QRWS = "CHG_LI_ST_DT_QRWS";
    public static final String CHG_LI_END_DT_QRWS = "CHG_LI_END_DT_QRWS";
    
    public static final String AGREEMENT_TYPE_CODE = "N/A";
    
}
