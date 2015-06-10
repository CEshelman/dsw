package com.ibm.dsw.quote.newquote.config;

import com.ibm.dsw.quote.base.config.ParamKeys;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NewQuoteParamKeys<code> class .
 *    
 * @author: wangxu@cn.ibm.com
 * Creation date: Mar 13, 2007
 */
public interface NewQuoteParamKeys extends ParamKeys{
	public static final String PARAM_TIME_FILTER= "timeFilter";
	public static final String PARAM_OWNER_FILTER_OWNED = "ownerFilterOwned";
	public static final String PARAM_OWNER_FILTER_DELEGATED = "ownerFilterDelegated";
	public static final String PARAM_QUOTE_ID = "quoteId";
	public static final String PARAM_OWNER_FILTER = "ownerFilter";	
	public static final String PARAM_DRAFT_SALES_QUOTE_LIST ="draftQuotes";
    public static final String PARAM_DISPLAYED_DRAFT_SALES_QUOTE_CONTRACT = "displayedDQContract";
    public static final Object URL_PARAM = "urlParam";
    
    public static final String PARAM_OWNED_BY_ME_VALUE ="1";
    public static final String PARAM_DELEGATED_TO_ME_VALUE ="2";
    public static final String PARAM_ALL_VALUE ="3";
    
    public static final String PARAM_FORM_FLAG="formFlag";
    public static final String PARAM_FORM_FLAG_VALUE="1";
    public static final String PARAM_ONE_WEEK_VALUE="7";
    public static final String PARAM_ONE_MONTH_VALUE="30";
    public static final String PARAM_THREE_MONTHS_VALUE="90";
    public static final String PARAM_SIX_MONTHS_VALUE="180";
    public static final String PARAM_TIME_ALL_VALUE="0";
    
    //for new sale quote use case
    public static final String PARAM_LOB_LIST = "PARAM_LOB_LIST";
    public static final String PARAM_ACQUISITION_LIST = "PARAM_ACQUISITION_LIST";
    public static final String PARAM_COUNTRY_LIST = "PARAM_COUNTRY_LIST";
    public static final String PARAM_OEM_AGREEMENT_TYPE_LIST = "PARAM_OEM_AGRMT_TYP_LIST";
    public static final String PARAM_DEFAULT_LOB = "PARAM_DEFAULT_LOB";
    public static final String PARAM_DEFAULT_COUNTRY = "PARAM_DEFAULT_COUNTRY";
    public static final String PARAM_DEFAULT_ACQSTN = "PARAM_DEFAULT_ACQSTN";
    public static final String PARAM_DEFAULT_OEM_AGREEMENT_TYPE = "PARAM_DEFAULT_OEM_AGRMT_TYP";
    
    //HTML names
    public static final String PARAM_LOB = "lob";
    public static final String PARAM_ACQUISITION = "acquisition";

    public static final String PARAM_QUOTE_SORT = "sortKey";
    public static final String PARAM_COUNTRY = "country";
    public static final String PARAM_MARK_AS_DEFAULT = "markAsDefault";
    public static final String PARAM_MARK_DEFAULT_VALUE = "TRUE";
    public static final String PARAM_OPEN_AS_NEW = "openAsNew";
    public static final String PARAM_INVALID_PARTS_WARNING = "invalidPartsWarning";
    
    //Upload Quote
    public static final String PARAM_INVILD_DATA = "PARAM_INVILD_DATA";
    public static final String INVALID_CUST_NUM = "INVALID_CUST_NUM";
    public static final String INVALID_SAP_NUM = "INVALID_SAP_NUM";
    public static final String INVALID_PART_LIST = "INVALID_PART_LIST";
    public static final String UPLOAD_FILE = "spreadfile";
    public static final String IMPORT_BUTTON = "Import";
    public static final String UPLOAD_WARNING = "&uploadWarning=";
    public static final String UPLOAD_WARNING_CUST_NUM = "customernumber";
    public static final String UPLOAD_WARNING_CTRCT_NUM = "contractnumber";
    public static final String UPLOAD_WARNING_PART_NUM_LIST = "partlist,";
    public static final String UPLOAD_WARNING_OCS_PART_NUM_LIST = "ocspartlist,";
    public static final String UPLOAD_WARNING_INVALID_FUTURE_DATE = "futuredate,";
    public static final String UPLOAD_WARNING_SAAS_PART_NUM_LIST = "saaspartlist,";
    public static final String UPLOAD_WARNING_SW_INCLUDING_SAAS_PART_NUM_LIST = "sw_including_saaspartlist,";
    public static final String UPLOAD_WARNING_SAAS_INCLUDING_SW_PART_NUM_LIST = "saas_including_swpartlist,";
    public static final String UPLOAD_WARNING_FREQENCY_PART_NUM_LIST = "frequencypartlist,";
    public static final String UPLOAD_WARNING_INCLUDING_SAAS_PART_NUM_LIST = "including_saaspartlist";
    public static final String UPLOAD_WARNING_INCLUDING_SW_PART_PGS = "including_swpartlist_pgs";
    public static final String UPLOAD_WARNING_DUPLICATE_SAAS_PART_NUM_LIST = "duplicatesaaspartlist,";
    public static final String UPLOAD_WARNING_INACTIVE_SAAS_PART_NUM_LIST = "inactivesaaspartlist,";
    public static final String UPLOAD_WARNING_UNCERTIFIED_SAAS_PART_NUM_LIST = "uncertifiedsaaspartlist,";
    public static final String UPLOAD_WARNING_MAX_PART_QTY = "maxpartqty";
    public static final String UPLOAD_WARNING_INVALID_APPLIANCE_PARTS_QUANTITY = "invalid_appliance_qty_partlist";
    
    public static final String PARAM_IS_LOCKED = "islocked";
    public static final String UPLOAD_OPTOIN = "uploadOption";
    public static final String UPLOAD_OPTOIN_NEW_QUOTE_ID = "newQuote";
    public static final String UPLOAD_OPTOIN_UNLOCK_QUOTE_ID = "unlockQuote";
    public static final String UPLOAD_OPTOIN_VALUE_NEW_QUOTE = "1";
    public static final String UPLOAD_OPTOIN_VALUE_UNLOCK_QUOTE = "2";
    
    public static final String PARAM_OPPORTUNIRY_NUMS = "opportunityNumList";
    public static final String PARAM_RETURN_OPPNUM_CODE = "returnOppNumCode";
    
    public static final String PARAM_EVAL_QUOTE_LIST ="evalQuoteList";
}
