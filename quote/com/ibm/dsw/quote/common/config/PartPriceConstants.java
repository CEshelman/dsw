package com.ibm.dsw.quote.common.config;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * The <code>PartPriceConstants</code> class is to define the revenue stream
 * code and helper method to determine the part type
 *
 * Creation date: Mar 27, 2007
 */

public class PartPriceConstants {



    //public static final int MAX_MAINT_COVERAGE_YEARS = 20;

    public static final int RQ_MANUALLY_ADDED_PART_SEQ = 10000;

    public static final String PAIFXTLM = "PAIFXTLM";

    public static final String PALCSBSP = "PALCSBSP";

    public static final String PASFXTLM = "PASFXTLM";

    public static final String PATERMLM = "PATERMLM";

    public static final String PASBRNSP = "PASBRNSP";

    public static final String PATDMTSP = "PATDMTSP";

    public static final String NPLCSBSP = "NPLCSBSP";

    public static final String NPTDMTSP = "NPTDMTSP";

    public static final String SBRNSP = "SBRNSP";

    public static final String CHINA = "CHN";

    public static final String AUSTRALIA = "AUS";

    public static final String INDIA = "IND";

    public static final String QTY_FROM_CACLTR = "0";

    public static final String QTY_CACLTR_OVERRIDDEN = "1";

    public static final String QTY_MANUAL_ENTERED = "2";

    public static final String E0005 = "E0005";

    public static final String E0006 = "E0006";

    public static final String RGNBASECUR_EUR = "EUR";

    public static final String RGNBASECUR_USD = "USD";

	public static final String RGNBASECUR_JPN = "JPN";

    public static final String GSA_PRICING_FLAG_F = "F";

    public static final String GSA_PRICING_FLAG_X = "X";

    public static final int ONE_YEAR_WITH_MONTH = 12;

    public static final double MINIUM_UNIT_PRICE = 0.01d;

    public static final String APPLNC_SELECT_DEFAULT="NULL";

    public static final String APPLNC_NOT_ON_QUOTE="NOT_ON_QUOTE";

    public static final String APPLNC_NOT_ASSOCIATED="NOT_ASSOCIATED";

    public static final String REVENUE_STREAM_CODE_SLA="SSSLAA";

    public static interface PartTypeCode{
        public static final String PACTRCT = "PACTRCT";
        public static final String PADOCMED="PADOCMED";
        public static final String SHRNKWRP="SHRNKWRP";
        public static final String SERVICES="SERVICES";

    }

    public static interface RenwlChgCode {
        public static final String RQCLOSED = "RQCLOSED";
        public static final String RQLIREMD="RQLIREMD";
        public static final String RQCHGD="RQCHGD";

    }

    public static interface PartChangeType{

        public static final String SAP_PART_ADDED = "I";
        public static final String SAP_PART_DELETED = "D";
        public static final String SAP_PART_UPDATED = "U";
        public static final String SAP_PART_NO_CHANGES = "N";

        public static final String PART_ADDED = "I";
        public static final String PART_DELETED = "D";
        public static final String PART_NO_CHANGES = "___"; // 3 under scores
        // following constants are for part updated
        public static final String PART_PVU_CHANGED = "CU";
        public static final String PART_QTY_INCREASE = "QI";
        public static final String PART_QTY_DECREASE = "QD";
        public static final String PART_DISCOUNT_UNIT_PRICE_CHANAGED = "DO";
        public static final String PART_DATE_CHANGED = "DC";
        public static final String PART_OFFER_PRICE_CLEARED = "OPC";
        public static final String PART_PRICE_UPDATED = "PU";

        public static final String SEPERATOR = "_"; // single scores;

    }

    public static interface PriceType{
        public final static String ENTITLED_PRICE = "ENTITLED";
        public final static String BASELINE_PRICE = "BASELINE";
        public final static String SPECIAL_BID_PRICE = "SPECL_BID";
        public final static String CHANNEL = "CHANNEL";
        public final static String NET_REVENUE = "NET_REV";
    }

    public static interface PVUDetail{
        public final static String PROCESSOR_CORES = "processor cores";
        public final static String PVUs_PER_CORE = "PVUs per core";
        public final static String TOTAL_PVUs = "total PVUs";
    }

    public static final String PART_REMOVED_QTY = "0";

	public static interface PartType{
		//sales quote
		String SQ_SB_RQ_PART = "SQ_SB_RQ_PART"; // special bid renewal quote parts in sales quote
		String SQ_OTHER_RQ_PART = "SQ_OTHER_RQ_PART"; // other (not special bid) renewal quote parts in sales quote
		String SQ_PS_PART = "SQ_PS_PART"; // parts added from part search result in sales quote
		//renewal quote
		String RQ_PS_PART = "RQ_PS_PART"; // parts added from part search result to the session renewal quote
		String RQ_ORIG_RQ_PART = "RQ_ORIG_RQ_PART"; // parts copied over when editing renewal quote
	}

	public static interface QuoteStage{
		String DRAFT = "DRAFT";
		String SUBMITTED = "SUBMITTED";
	}

	public static interface PWSErrorCodes{
		String ERROR_CODE_028 = "028";
	}

	public static interface PriceLevel{
		String PRC_LVL_SRP = "SRP";
	}

	public static final String REVN_STRM_CODE_DSCR_SEPERATOR = ", ";

	public static final int MAX_CMPRSS_CVRAGE_MONTH = 12;

	public static interface OfferPriceAction{

	    public static final int APPLY_OFFER_PRICE_ACTION = 1;

	    public static final int CLEAR_OFFER_PRICE_ACTION = 2;
	}

	public static final int FULL_YEAR_MONTH = 12;

	public static final int TOTAL_RAMP_UP_DURATION_MONTH = 59;	//refer to rtc# 212987

	public static final int FULL_YEAR_WEEK = 52;

	public static interface SaasDefaultTerm{
		int MONTH = 12;
		int YEAR = 1;
	}

	public static interface PartBrand{
	    public static final String DB2 = "DB2";
	    public static final String LOTUS = "LOTUS";
	    public static final String RATIONAL = "RATIONAL";
	    public static final String TIVOL = "TIVOL";
	    public static final String WEBSPHER = "WEBSPHER";
	    public static final String OTHER = "OTHER";
	}

	public static interface PriceTotalRevnStrmCategory{
	    String LEGACY_ALL = "ALL";
	    String NEW_ALL = "TOTAL";
	}

	public static interface PartTypeCategory{
	    String LICENSE_PART = "LIC";
	    String FTL_PART = "FTL";
	    String RELATED_MAINTENANCE_PART = "RMN";
	    String UNRELATED_MAINTENANCE_PART = "UMN";
	    String REFERENCES_RENEWAL_PART = "REN";
	    String UNKNOWN = "UNK";   // for undetermined or unknown
	    String BLANKPT = "";   // for line items where web does not calculate dates
	}

	public static interface PartRelNumAndTypeDefault{
	    int RELATED_LINE_ITM_NUM_DEFAULT = -1;
	    String PART_TYPE_DEFAULT = "TBD";
	}

	public static interface InitFtlRevnStrm{
        String IFXTLM = "IFXTLM";
    }

	public static interface RevnStrmCategory{
        String NEWLICNS = "NEWLICNS";
        String SBSCNSPT = "SBSCNSPT";
        String OTHER = "OTHER";
    }

	public static final String SAAS_PART_FLAG_YES = "1";
	public static final String FLAG_YES = "1";
	public static final String SAAS_PART_DEFLG_CODE = "D";
	public static final String SAAS_TERM_UNIT_ANNUAL = "A";
	public static final String SAAS_TERM_UNIT_MONTH = "M";

	public static final int SAAS_PART_DEFAULT_QTY = 1;

	public static interface SaaSOverrideType{
        String UNIT = "0";
        String EXTENDED = "1";
        String DEFAULT = "0";
    }

	public static interface SaaSPricngTierMdl{
        String UP_TO = "VALU";
        String STEP = "GRAD";
        String GRANULAR = "QNTY";
        String ON_DEMAND = "ONDM";
    }

	public static interface ToUStatus{
		String HOLD = "E0022";
		String DECLINED = "E0023";
	}

	public static final String REPLACED_PART_FLAG_YES = "1";
	public static final String RAMP_UP_FLAG_YES = "1";
	public static final String ADD_ON_TRADE_UP_FLAG = "1";
	public static final String CO_TERM_FLAG = "1";
	public static final String TERMS_AND_CONDS_HOLD_FLAG = "1";
	public static interface BillingOptionCode{
		String ANNUAL = "A";
		String QUARTERLY = "Q";
		String MONTHLY = "M";
		String UPFRONT = "U";
	}

	public static interface ConfigrtnActionCode {
		//for add-ons/trade-ups, automatically co-termed
		String ADD_TRD = "AddTrd";

		//new configuration (no reference to existing charge agreement) - co-term radio-button "No" is selected
		String NEW_NCT = "NewNCt";

		//new configuration for an existing charge agreement - co-term radio-button "Yes" is selected
		String NEW_CA_CT = "NewCACt";

		//new configuration for an existing charge agreement - co-term radio-button "No" is selected
		String NEW_CA_NCT = "NewCANCt";

		//FCT TO PA finialization new configuration for an existing charge agreement - co-term radio-button "No" is selected
		String FCT_TO_PA_FNL = "FctPAFnl";

	}

	public static interface PartnerPriceType{
		String A = "A";
		String H = "H";
		String J = "J";
	}

	public static interface AppliancePartType{
		String APPLIANCE = "APPLIANCE";
		String REINSTATEMENT = "REINSTATEMENT";
		String APPLIANCE_UPGRADE = "APPLIANCE_UPGRADE";
		String SERVICE_PACK = "SERVICE_PACK";
		String SERVICE_PACK_RENEWAL = "SERVICE_PACK_RENEWAL";
		String TRANSCEIVER = "TRANSCEIVER";
		String RENEWAL_PARTS = "RENEWAL_PARTS";
		String ADDITIONAL_SOFTWARE = "ADDITIONAL_SOFTWARE";
        String OWNERSHIP_TRANSFER = "OWNERSHIP_TRANSFER";
	}
	public static final String APPLIANCE_PART_FLAG_YES = "1";

	public static interface CognosCallBackParams{
		String SOLD_TO_CUST = "SOLD_TO_CUST";
		String SAP_CTRCT_NUM = "SAP_CTRCT_NUM";
		String LINE_OF_BUS_CODE = "LINE_OF_BUS_CODE";
	}

	public static interface RenewalModelCode{
		String C = "C";//continuous billing
		String R = "R";//Renew service term for 12 months
		String N = "N";//not applicable
		String O = "O";//Renew service term for original term
		String T = "T";//Terminate at end of term
		String CONFIG_RENWL_MDL_CODE_LEVEL = "CONFIG";
		String LINE_RENWL_MDL_CODE_LEVEL="LINE";
	}
	
	public static interface ApplncSerialNumWarngFlag{
		Integer NO_VALIDATION = null;  //no validation
		Integer VALIDATION_SUCCESS = 0; //Validated successfully
		Integer MTM_OF_DIFFERENT_CUSTOMER = 1; //MTM is associated with a different customer
		Integer MTM_OF_DIFFERENT_CONTRACT = 2; //MTM is associated with a different contract
		Integer MTM_OF_DIFFERENT_BRAND = 3; //MTM is associated with a different brand
		Integer MTM_NO_EXIST = 4 ;  //MTM can not be validated (no exist)
	}
	
	public static final String PRICING_METHOD_RSVP_SRP = "SV";
	public static final String PRICING_METHOD_PYP = "LP";
	public static final String PRICING_METHOD_EXCEPTION_CONTRACT = "EC";
	// 14.2 deployment model
    public static final Integer DEPLOYMENT_SELECT_DEFAULT = 0;
    public static final Integer DEPLOYMENT_NOT_ON_QUOTE = 1;
    public static final Integer DEPLOYMENT_NEW_ID = 2;
    public static final Integer DEPLOYMENT_ASSOCIATED = 3;
	public static final String DEPLOYMENT_ID_INVALID = "1";
}