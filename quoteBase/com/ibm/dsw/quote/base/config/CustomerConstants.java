/*
 * Created on Feb 28, 2007
 */
package com.ibm.dsw.quote.base.config;

/**
 * @author Lavanya
 */
public interface CustomerConstants {
    
    //  Line of Business Constants
	public static final String LOB_PA = "PA";
	public static final String LOB_FCT = "FCT";
	public static final String LOB_PAE = "PAE";
	public static final String LOB_PPSS = "PPSS";
	public static final String LOB_PAUN = "PAUN";
	public static final String LOB_OEM = "OEM";
	public static final String LOB_SSP = "SSP";
	public static final String LOB_CSA = "CSA";
	
	//Country constants
	public static final String COUNTRY_USA = "USA";
	public static final String COUNTRY_CANADA = "CAN";
	
	//Anniversary options
	public static final String ALL_0 = "0";
	public static final String NUMBER_1 = "1";
	public static final String NUMBER_2 = "2";
	public static final String NUMBER_3 = "3";
	public static final String NUMBER_4 = "4";
	public static final String NUMBER_5 = "5";
	public static final String NUMBER_6 = "6";
	public static final String NUMBER_7 = "7";
	public static final String NUMBER_8 = "8";
	public static final String NUMBER_9 = "9";
	public static final String NUMBER_10 = "10";
	public static final String NUMBER_11 = "11";
	public static final String NUMBER_12 = "12";
	
	// Find active customer options
	public static final String ALL_CUSTS = "0";
	public static final String ACTIVE_PA_CUSTS = "1";
	public static final String ACTIVE_PAE_CUSTS = "2";
	public static final String ACTIVE_CSA_CUSTS = "3";
	
	// pagination
	public static final int PAGE_ROW_COUNT = 100;
	
	// the value of checkbox when it's checked
	public static final String CHECKBOX_CHECKED= "on";
	
	//constants while inserting a customer into database
	public static final String SSP_END_USER = "SSP_END";
	public static final String PAX = "PAX";
	public static final String PASQ = "PASQ";
	public static final String DRAFT = "DRAFT";
	public static final String PRTNR_FUNC_CODE_ZW = "ZW";
	public static final String PRTNR_FUNC_CODE_ZG = "ZG";
	
	// Search for
	public static final String SEARCH_FOR_CUSTOMER = "customer";
	public static final String SEARCH_FOR_PAYER = "payer";
	
	// New customer agreement types
	public static final String AGRMNT_TYPE_ACADEMIC = "ACA";
	public static final String AGRMNT_TYPE_GOVERNMENT = "GOV";
	public static final String AGRMNT_TYPE_STANDARD = "STD";
	public static final String AGRMNT_TYPE_XSP = "XSP";
	public static final String AGRMNT_TYPE_ADDI_SITE = "ASE";
	
	// New customer agreement options
	public static final String AGRMNT_OPTION_TRANSACTIONAL = "CSTA";
	public static final String AGRMNT_OPTION_RELATIONAL = "CSRA";
	
	// Authorization group
	public static final String AUTHRZTN_GRP_USA_FEDERAL = "ZFED";
	public static final String AUTHRZTN_GRP_CRB_FEDERAL = "CFED";
	public static final String AUTHRZTN_GRP_STATE_LOCAL = "";
	
	// Customer procd flag
	public static final int PROCD_FLAG_COMMON = 3;
	public static final int PROCD_FLAG_ADD_SITE = 5;
	
	public static final String CUST_ORD_BLCKED = "01";

	public static final String CONFIGURATOR_ADDON_TRADEUP_FLAG_0 = "0";
	public static final String CONFIGURATOR_ADDON_TRADEUP_FLAG_1 = "1";
	

	public static final String CONFIGURATOR_CONFIGRTN_ACTION_AddTrd = "AddTrd";
	public static final String CONFIGURATOR_CONFIGRTN_ACTION_NewNCt = "NewNCt";
	public static final String CONFIGURATOR_CONFIGRTN_ACTION_NewCACt = "NewCACt";
	public static final String CONFIGURATOR_CONFIGRTN_ACTION_NewCANCt = "NewCANCt";
	public static final String CONFIGURATOR_CONFIGRTN_ACTION_FCTToPA = "FctPAFnl";
	

	public static final String CONFIGURATOR_CT_FLAG_0 = "0";
	public static final String CONFIGURATOR_CT_FLAG_1 = "1";

	public static final String CONFIGURATOR_RAMPUP_FLAG_0 = "0";
	public static final String CONFIGURATOR_RAMPUP_FLAG_1 = "1";
	

	public static final String CONFIGURATOR_ACTIVEONAGREEMENTFLAG_FLAG_0 = "0";
	public static final String CONFIGURATOR_ACTIVEONAGREEMENTFLAG_FLAG_1 = "1";
	

	public static final String CONFIGURATOR_FLAG_TRUE = "1";
	public static final String CONFIGURATOR_FLAG_FALSE = "0";
	

	public static final String CONFIGURATOR_FINAL_BASIC = "0";
	public static final String CONFIGURATOR_FINAL_GST = "1";

	public static final String CONFIGURATOR_INDICATOR_BASIC = "0";
	public static final String CONFIGURATOR_INDICATOR_GST = "1";
	public static final String CONFIGURATOR_INDICATOR_RSTRCT= "2";
	public static final String CONFIGURATOR_INDICATOR_PILOT= "3";
	

	public static final String CONFIGURATOR_SOURCE_TYPE_BRANDNEW = "BN";// create a new configuration from browse service
	public static final String CONFIGURATOR_SOURCE_TYPE_ADDONTRADEUP = "AD";//update an existing configuration from internal reporting
	public static final String CONFIGURATOR_SOURCE_TYPE_COPY = "CP";//copy an existing configuration from internal reporting
	public static final String CONFIGURATOR_SOURCE_TYPE_REEDIT = "RE";//re-edit configuration from part price tab
	
	// appliance address search types
	// Search for
	public static final String SEARCH_BY_ATTR = "1";
	public static final String SEARCH_BY_SITE_NUM = "2";
	public static final String SEARCH_BY_AGREEMENT = "3";
	
	public static final String END_USER_FLAG = "1";//1=ssp end user
	
}
