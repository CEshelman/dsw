package com.ibm.dsw.quote.appcache.config;

import com.ibm.dsw.quote.base.config.DBConstants;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CommonDBConstants</code> class contains db2 constants for common domain objects
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Feb 5, 2007
 */

public interface CacheDBConstants extends DBConstants {
	//sp names
	public final static String S_QT_CNTRY_CURRNCY = "S_QT_CNTRY_CURRNCY";
	public final static String DB2_SP_S_QT_LOB = "S_QT_LOB";
	public final static String DB2_S_WEB_MEDIA_LANGS = "S_WEB_MEDIA_LANGS";
	public final static String DB2_S_WEB_COM_LANG_CDS = "S_WEB_COM_LANG_CDS";
	public final static String DB2_S_QT_INDUSTRY_CDS = "S_QT_INDUSTRY_CDS";
	//public final static String DB2_S_QT_CURR_CNV = "S_QT_CURR_CNV";
	public final static String DB2_S_QT_PA_PROD_CODES = "S_QT_PA_PROD_CODES";
	public final static String DB2_S_QT_OEM_PROD_CODES = "S_QT_OEM_PROD_CODES";	
    public static final String DB2_S_QT_ACQ = "S_QT_ACQ";
    public static final String DB2_S_QT_GET_FCT_PPTCS = "S_QT_GET_FCT_PPTCS";
    public static final String DB2_S_QT_GET_PPTCS = "S_QT_GET_PPTCS";
    public static final String DB2_S_QT_SPBID_CSTIND = "S_QT_SPBID_CSTIND";
    public static final String DB2_S_QT_GET_CPCS = "S_QT_GET_CPCs";
    public static final String DB2_S_QT_CTRCT_VRNT = "S_QT_CTRCT_VRNT";
    public static final String DB2_S_QT_BUS_ORG = "S_QT_BUS_ORG";
    public static final String DB2_S_QT_CODE_DSCR = "S_QT_CODE_DSCR";
    public static final String DB2_S_QT_SPBID_RGNDIST = "S_QT_SPBID_RGNDIST";
    public static final String DB2_S_QT_SPBID_CATGRY = "S_QT_SPBID_CATGRY";
    public static final String DB2_S_QT_SB_GROUP = "S_QT_SB_GROUP";
    public static final String DB2_S_QT_SBA_ADMINS = "S_QT_SBA_ADMINS";
    public static final String DB2_S_QT_SB_TYPES = "S_QT_SB_TYPES";
    public static final String DB2_S_QT_GET_XCHG_RATE = "S_QT_GET_XCHG_RATE";
    public static final String DB2_S_QT_GET_PORTFOLIO = "S_QT_GET_PORTFOLIO";
    public static final String DB2_S_WEB_GET_TMOFFST = "S_WEB_GET_TMOFFST";
    public static final String DB2_S_QT_LOG_CONFIG = "S_QT_LOG_CONFIG";
    public static final String DB2_S_QT_CLSFCTN_CODE = "S_QT_CLSFCTN_CODE";
    public static final String DB2_S_QT_GET_RENWL_RS = "S_QT_GET_RENWL_RS";
    public static final String DB2_S_QT_SALES_ODDS = "S_QT_SALES_ODDS";
    public static final String DB2_S_WEB_APP_CNSTNT = "S_WEB_APP_CNSTNT";
    public static final String DB2_S_QT_OEM_AGRMT_TYP = "S_QT_OEM_AGRMT_TYP";
    public static final String SP_S_QT_OVERALL_STATUS_LIST = "S_QT_OVERALL_STATUS_LIST";
    public static final String DB2_S_QT_REVN_TO_CAT_MAPPING = "S_QT_REVN_TO_CAT_MAPPING";
    public static final String DB2_S_QT_REVN_STRM_CAT_DETAIL = "S_QT_REVN_STRM_CAT_DETAIL";
    public static final String DB2_S_QT_OEM_BID_TYPES = "S_QT_OEM_BID_TYPES";
    public static final String DB2_S_QT_GET_WEB_APP_CNSTNT = "S_QT_GET_WEB_APP_CNSTNT";
    public static final String DB2_S_QT_GET_SAAS_BILLOPT = "S_QT_GET_SAAS_BILLOPT";
    public static final String DB2_S_QT_FFMT_SRC = "S_QT_FFMT_SRC";
    public static final String DB2_S_QT_GROWTH_DLG = "S_QT_GROWTH_DLG";
    public static final String DB2_S_SBA_BRAND_CODES = "S_SBA_BRAND_CODES"			;
    public static final String DB2_S_QT_PRINT_SP_TIMETRACER = "S_QT_PRINT_SP_TIMETRACER";
    
    //Reporting's SP name begin
    public static final String DB2_S_QT_SALEODDS_LST = "S_QT_SALEODDS_LST";
    public static final String DB2_S_QT_QSTATUS_LST = "S_QT_QSTATUS_LST";
    public static final String DB2_S_QT_SALESORG_LST = "S_QT_SALESORG_LST";
    public static final String DB2_S_PBC_CTRY_CURRNCY = "S_PBC_CTRY_CURRNCY";
    public static final String DB2_S_QT_CTRCTVAR_LST = "S_QT_CTRCTVAR_LST";
    //Reporting's SP end
    
    //Quote Mobile's SP name begin
    public static final String DB2_S_QTM_GET_MASS_DLGTN = "S_QTM_GET_MASS_DLGTN";
    
    public static final String DB2_S_QTM_GET_RQ_MASS_DLGTN = "S_QTM_GET_RQ_MASS_DLGTN";
    
}
