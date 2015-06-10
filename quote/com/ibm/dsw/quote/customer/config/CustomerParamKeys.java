package com.ibm.dsw.quote.customer.config;

import com.ibm.dsw.quote.base.config.ParamKeys;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerParamKeys<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-3-22
 */

public interface CustomerParamKeys extends ParamKeys {
    
    public static final String SEARCH_FOR = "searchFor";
    public static final String PARAM_CUSTOMER = "customer";
    public static final String PARAM_AGRMNT_TYPES = "agrmntTypes";
    public static final String PARAM_AGRMNT_OPTIONS = "agrmntOptions";
    
    public static final String PARAM_CPA_DATA_PACK ="cpqDataPack";
    public static final String PARAM_SERVICE_CONFIGURATION_URL = "serviceConfigurationURL";
    public static final String PARAM_CONFIG_INDICATOR = "configIndicator";
    public static final String SQO = "SQO";
    public static final String PGS = "PGS";
    public static final String CPQ = "CPQ";
    public static final String PARAM_RETURN_TO_SQO_CHRG_AGR_NUM="chrgAgrmtNum";
    public static final String PARAM_RETURN_TO_SQO_WEB_QUOTE_NUM="webQuoteNum";
    public static final String PARAM_RETURN_TO_SQO_SQO_CONFIG_ID="configId";
    public static final String PARAM_RETURN_TO_SQO_ADDTRADE_FLAG="addTradeFlag";
    public static final String PARAM_RETURN_TO_SQO_CONFIGURATORTYPE="configuratorType";
    public static final String PARAM_RETURN_TO_SQO_CONFIGRTNACTION="configrtnActionCode";
    public static final String PARAM_RETURN_TO_SQO_CALCTERM="calcTerm";
    public static final String PARAM_DEBUGER ="debuger";
    public static final String PARAM_RETURN_TO_SQO_OVERRIDE_FLAG="overrideFlag";

    public static final String PARAM_CONFIGURATOR_ReferenceNum = "ReferenceNum";
    public static final String PARAM_CONFIGURATOR_RedirectAction = "RedirectAction";
    public static final String PARAM_CONFIGURATOR_ConfigurationID = "ConfigurationID";
    

    public static final String PARAM_CONFIGURATOR_overridePilotFlag = "overridePilotFlag";
    public static final String PARAM_CONFIGURATOR_overrideRstrctFlag = "overrideRstrctFlag";
    public static final String PARAM_RETURN_TO_SQO_CA_END_DATE = "caEndDate";
    
    public static final String PARAM_RESULT_CREATE_QUOTE_AJAX="createResult";
    
    public static final String PARAM_AGRMTTYPECODE="agrmtTypeCode";
}
