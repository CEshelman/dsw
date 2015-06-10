package com.ibm.dsw.quote.configurator.config;

public interface ConfiguratorConstants {
	//Billing frequency
	String AVAIL = "AVAIL";
	String CONFG = "CONFG";
	
	String BILLING_FREQUENCY_UPFRONT = "U";
	String BILLING_FREQUENCY_ANNUAL = "A";
	String BILLING_FREQUENCY_MONTHLY = "M";
	String BILLING_FREQUENCY_QUARTERLY = "Q";
	String BILLING_FREQUENCY_EVENT = "E";
	String OPERATION_TYPE_GO = "GO";
	String OPERATION_TYPE_DISPLAY = "DISPLAY";
	String OPERATION_TYPE_REMOVE_COTERM = "REMOVE_COTERM";
	String CTFLAG_TRUE = "1";
	String CTFLAG_FALSE = "0";
	
	interface SAAS_PART_TYPE{
		String SETUP = "ZSTU";
		String ADDI_SETUP = "ZSTA";
		String SUBSCRPTN = "ZSAS";
		String SUBSCRPTN_OVRAGE = "ZSSO";
		String DAILY = "ZSDS";
		String ON_DEMAND = "ZODM";
		String HUMAN_SRVS = "ZHUS";

        String SUBSUMED_SUBSCRPTN = "ZSBM";
	}
	
	String RAMP_FLAG_YES = "1";
	String RAMP_FLAG_NO = "0";
	String ACTIVE_ON_CHARGE_AGREEMENT_FLAG_YES = "1";
	
	String BILLING_OPTIONS_PARA_SPLIT_CHAR = ",";
	String OVERRIDE_FLAG_YES = "1";
	String OVERRIDE_FLAG_NO = "0";
	String OVERRIDE_PILOT_FLAG_YES = "1";
	String OVERRIDE_PILOT_FLAG_NO="0";
	String OVERRIDE_RSTRCT_FLAG_YES = "1";
	String OVERRIDE_RSTRCT_FLAG_NO = "0";
	interface RETRIEVAL_SERVICE_RET_CODE{
		String CODE_1003 = "1003";
		String CODE_1004 = "1004";
		String CODE_1005 = "1005";
	}

}
