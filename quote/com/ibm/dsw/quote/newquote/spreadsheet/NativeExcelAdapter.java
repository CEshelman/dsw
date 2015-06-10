package com.ibm.dsw.quote.newquote.spreadsheet;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NativeExcelAdapter<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 18, 2010
 */


public abstract class NativeExcelAdapter {
    // exported part cell position
	public int PART_COL_IND_PART_NUMBER = 0;
	public int PART_COL_IND_PART_DESCRIPTION = 1;
	public int PART_COL_IND_RESELLER_AUTHORIZATION = 2;
	public int PART_COL_IND_RESELLER_AUTHORIZATION_TERMS = 3;
	public int PART_COL_IND_QUANTITY = 4;
	public int PART_COL_IND_PRORATE= 5;
	public int PART_COL_IND_ADD_YEARS = 6;
	public int PART_COL_IND_STANDARD_START_DATE_FORMULA = 7;
	public int PART_COL_IND_STANDARD_END_DATE_FORMULA = 8;
	public int PART_COL_IND_OVERRIDE_START_DATE = 9;
	public int PART_COL_IND_OVERRIDE_END_DATE = 10;
	public int PART_COL_IND_RENEWAL_QUOTE_NUMBER = 11;
	public int PART_COL_IND_ITEM_POINTS = 12;
	public int PART_COL_IND_ENTITLED_UNIT_PRICE_FORMULA = 13;
	public int PART_COL_IND_OVERRIDE_PRICE = 14;
	public int PART_COL_IND_DISCOUNT_PERCENT = 15;
	public int PART_COL_IND_TOTAL_POINTS_FORMULA = 16;
	public int PART_COL_IND_BID_UNIT_PRICE_FORMULA = 17;
	public int PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA = 18;
	public int PART_COL_IND_BID_EXTENDED_PRICE_FORMULA = 19;
	public int PART_COL_IND_BP_DISCOUNT = 20;
	public int PART_COL_IND_BP_OVERRIDE_DISCOUNT = 21;
	public int PART_COL_IND_BP_EXTENDED_PRICE_FORMULA = 22;	
	public int PART_COL_IND_X_FORMULA = 23;
	public int PART_COL_IND_RENEWAL_QUOTE_LINE_ITEM_NUMBER = 24;
	public int PART_COL_IND_SORT_SEQ_NUM = 25;
	public int PART_COL_IND_ADDITIONAL_YEAR_COVERAGE_SEQ_NUM = 26;
	public int PART_COL_IND_HIDDEN_TOTAL_PRICE = 28;
	public int PART_COL_IND_HIDDEN_QUANTITY = 29;
	public int PART_COL_IND_HIDDEN_OVERRIDE_START_DATE = 30;
	public int PART_COL_IND_HIDDEN_OVERRIDE_END_DATE = 31;
	public int PART_COL_IND_HIDDEN_OVERRIDE_PRICE = 32;
	public int PART_COL_IND_HIDDEN_DISCOUNT_PERCENT = 33;
	public int PART_COL_IND_CHANGED_FLAG_FORMULA = 34;
	public int PART_COL_IND_HIDDEN_STANDARD_START_DATE = 35;
	public int PART_COL_IND_HIDDEN_BID_UNIT_PRICE = 36;
	public int PART_COL_IND_HIDDEN_ENTITLED_EXT_PRICE = 37;
	public int PART_COL_IND_HIDDEN_ENTITLED_UNIT_PRICE = 38;
	public int PART_COL_IND_HIDDEN_BP_EXTENDED_PRICE = 39;
	public int PART_COL_IND_HIDDEN_BP_OVERRIDE_DISCOUNT = 43;
	
	public int PART_COL_IND_BID_UNIT_PRICE_DECIMAL_4 = 42;
	public int PART_COL_IND_OVERRIDE_PRICE_DECIMAL_4 = 42;
	public int PART_ROW_IND_OVERRIDE_PRICE_DECIMAL_4 = 0;
	
	// pricing support data cell position
	public int PRICING_SUPPORT_COL_IND_PART_NUMBER = 0;
	public int PRICING_SUPPORT_COL_IND_REVENUE_STREAM = 1;
	public int PRICING_SUPPORT_COL_A = 2;
	public int PRICING_SUPPORT_COL_B = 3;
	public int PRICING_SUPPORT_COL_D = 4;
	public int PRICING_SUPPORT_COL_E = 5;
	public int PRICING_SUPPORT_COL_F = 6;
	public int PRICING_SUPPORT_COL_G = 7;
	public int PRICING_SUPPORT_COL_H = 8;
	public int PRICING_SUPPORT_COL_I = 9;
	public int PRICING_SUPPORT_COL_J = 10;
	public int PRICING_SUPPORT_COL_GV = 11;
	public int PRICING_SUPPORT_COL_ED = 12;
	public int PRICING_SUPPORT_COL_NA = 13;	
	public char PRICING_SUPPORT_FIRST_COL = 'A';
	public char PRICING_SUPPORT_LAST_COL = 'N';
	
	// additional part cell position
	public int ADD_PART_COL_IND_PART_NUMBER = 0;
	public int ADD_PART_COL_IND_PART_DESCRIPTION = 1;
	public int ADD_PART_COL_IND_QUANTITY = 2;
	public int ADD_PART_COL_IND_PRORATE = 3;
	public int ADD_PART_COL_IND_ADD_YEARS = 4;
	public int ADD_PART_COL_IND_STANDARD_START_DATE_FORMULA = 5;
	public int ADD_PART_COL_IND_STANDARD_END_DATE_FORMULA = 6;
	public int ADD_PART_COL_IND_OVERRIDE_START_DATE = 7;
	public int ADD_PART_COL_IND_OVERRIDE_END_DATE = 8;
	public int ADD_PART_COL_IND_ITEM_POINTS = 9;
	public int ADD_PART_COL_IND_ENTITLED_UNIT_PRICE = 10;
	public int ADD_PART_COL_IND_OVERRIDE_PRICE = 11;
	public int ADD_PART_COL_IND_DISCOUNT_PERCENT = 12;
	
	//absolute position for EQ Customer sheet in submitted quote spreadsheet
	public String SQ_POSITION_OPPORTUNITY_NUMBER;
	public String SQ_POSITION_FULFILLMENT_SOURCE;
	public String SQ_POSITION_QUOTE_NUMBER;
	public String SQ_POSITION_SAP_CONFIRMATION_NUMBER;
	public String SQ_POSITION_OVERALL_STATUS;
	public String SQ_POSITION_QUOTE_START_DATE;
	public String SQ_POSITION_QUOTE_EXPIRATION_DATE;
	public String SQ_POSITION_SB_APPROVED_DATE;
	public String SQ_POSITION_QUOTE_CONTACT;
	public String SQ_POSITION_QUOTE_DETAIL_URL;
	public String SQ_POSITION_SITE_NUMBER;
	public String SQ_POSITION_IBM_CUSTOMER_NUMBER;
	public String SQ_POSITION_CUSTOMER_NAME;
	public String SQ_POSITION_CUSTOMER_ADDRESS1;
	public String SQ_POSITION_CUSTOMER_ADDRESS2;
	public String SQ_POSITION_CUSTOMER_ADDRESS3;
	public String SQ_POSITION_AGREEMENT_NUMBER;
	public String SQ_POSITION_ANNIVERSARY_NUMBER;
	public String SQ_POSITION_RELATIONSHIP_SVP_LEVEL;
	public String SQ_POSITION_RESELLER_CUST_NUM;
	public String SQ_POSITION_RESELLER_ICN;
	public String SQ_POSITION_RESELLER_CUST_NAME;
	public String SQ_POSITION_RESELLER_ADDRESS1;
	public String SQ_POSITION_RESELLER_ADDRESS2;
	public String SQ_POSITION_RESELLER_ADDRESS3;
	public String SQ_POSITION_DISTRIBUTER_CUST_NUM;
	public String SQ_POSITION_DISTRIBUTER_ICN;
	public String SQ_POSITION_DISTRIBUTER_CUST_NAME;
	public String SQ_POSITION_DISTRIBUTER_ADDRESS1;
	public String SQ_POSITION_DISTRIBUTER_ADDRESS2;
	public String SQ_POSITION_DISTRIBUTER_ADDRESS3;
	
	//submitted quote special columns
    public int PART_COL_IND_BRAND = -1;
    public int PART_COL_IND_TOTAL_LINE_DISCOUNT_FORMULA;
    public int PART_COL_IND_PART_TYPE = -1;
    public int PART_COL_IND_PRORATE_MONTHS;
    public int PART_COL_IND_COMPRESSED_COVERAGE_MONTHS;
    
    // exported saas part cell position
    public int SAASPART_COL_IND_PART_NUMBER = 0;
	public int SAASPART_COL_IND_PART_DESCRIPTION = 1;
	public int SAASPART_COL_IND_CONFIG_ID = 2;
	public int SAASPART_COL_IND_REPLACE_PART = 3;
	public int SAASPART_COL_IND_Ramp_Up_Period =4;
	public int SAASPART_COL_IND_MIGRATED_PART = 5;
    public int SAASPART_COL_IND_QUANTITY = 6;
    public int SAASPART_COL_IND_TERM = 7;
    public int SAASPART_COL_IND_BILLING_FREQUENCY = 8;
    public int SAASPART_COL_IND_TOTAL_POINTS = 9;
    public int SAASPART_COL_IND_ENTITLED_RATE = 10;
    public int SAASPART_COL_IND_BID_RATE = 11;
    public int SAASPART_COL_IND_ENTITLED_TCV = 12;
    public int SAASPART_COL_IND_OVERRIDE_PRICE = 13;
    public int SAASPART_COL_IND_DISCOUNT_PERCENT = 14;
    public int SAASPART_COL_IND_BID_TCV = 15;
    public int SAASPART_COL_IND_BP_DISCOUNT = 16;
    public int SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT = 17;
    public int SAASPART_COL_IND_BP_RATE = 18;
    public int SAASPART_COL_IND_BP_TCV = 19;
    
    public int SAASPART_COL_IND_HIDDEN_TERM = 21;
    public int SAASPART_COL_IND_HIDDEN_QUANTITY = 22;
    public int SAASPART_COL_IND_HIDDEN_ENTITLED_TCV = 23;
    public int SAASPART_COL_IND_HIDDEN_BID_TCV = 24;
    public int SAASPART_COL_IND_HIDDEN_BP_TCV = 25;
    public int SAASPART_COL_IND_HIDDEN_BILLING_FREQUENCY = 26;
    public int SAASPART_COL_IND_HIDDEN_SUP = 27;
    public int SAASPART_COL_IND_HIDDEN_UNIT_POINTS = 28;
    public int SAASPART_COL_IND_HIDDEN_SHOW_QTY = 29;
    public int SAASPART_COL_IND_HIDDEN_SHOW_TOTALPOINTS = 30;
    public int SAASPART_COL_IND_HIDDEN_SHOW_ENTITLETCV = 31;
    public int SAASPART_COL_IND_HIDDEN_SHOW_BIDTCV = 32;
    public int SAASPART_COL_IND_HIDDEN_SHOW_BPTCV = 33;
    public int SAASPART_COL_IND_HIDDEN_IS_SETUP=34;
    public int SAASPART_COL_IND_HIDDEN_IS_PRODHUMANSERVICE=35;
    public int SAASPART_COL_IND_HIDDEN_IS_SUBSCRPTN=36;
    public int SAASPART_COL_IND_HIDDEN_IS_SHOW_TERM=37;
    public int SAASPART_COL_IND_HIDDEN_OVERRIDE_PRICE=38;
    public int SAASPART_COL_IND_HIDDEN_OVERRIDE_DISCOUNT=39;
    public int SAASPART_COL_IND_HIDDEN_BPOVERRIDEDISCOUNT=40;
    public int SAASPART_COL_IND_HIDDEN_IS_NOT_CHANGED=41;
    
    public int SAASPART_COL_IND_HIDDEN_REF_DOC_NUM=42;
    public int SAASPART_COL_IND_HIDDEN_ERROR_CODE=43;
    public int SAASPART_COL_IND_HIDDEN_CONFIGRTN_ACTION=44;
    public int SAASPART_COL_IND_HIDDEN_ENDDATE=45;
    public int SAASPART_COL_IND_HIDDEN_COTERM_TO_CONFIGRTNID=46;
    public int SAASPART_COL_IND_HIDDEN_OVERRIDE_FLAG=47;
    public int SAASPART_COL_IND_HIDDEN_CONFIGRTRCONFIGRTNID = 48;
    public int SAASPART_COL_IND_HIDDEN_BRAND_CODE = 49;
    public int SAASPART_COL_IND_HIDDEN_IS_DECIMAL4_FLAG = 50;
    public int SAASPART_COL_IND_HIDDEN_LOCAL_UNIT_PRICE_FORMULA = 51;
    public int SAASPART_COL_IND_HIDDEN_BILLING_FREQUENCY_FOR_RATE = 52;
    public int SAASPART_COL_IND_HIDDEN_QUANTITY_FOR_RATE = 53;
    public int SAASPART_COL_IND_HIDDEN_IS_SUBSCRPTN_OVRAGE = 54;
    public int SAASPART_COL_IND_HIDDEN_DEST_SEQ_NUM = 55;
    public int SAASPART_COL_IND_HIDDEN_RELATED_LINEITM_NUM = 56;
    public int SAASPART_COL_IND_HIDDEN_IS_RAMPUP_PART = 57;
    public int SAASPART_COL_IND_HIDDEN_PROVISIONING_ID = 58;
    
    //for saas additional sheet
    public int ADD_SAASPART_COL_IND_PART_NUMBER = 0;
    public int ADD_PART_COL_IND_MIGRATED_PART = 2;
    public int ADD_SAASPART_COL_IND_QUANTITY = 3;
    public int ADD_SAASPART_COL_IND_TERM = 4;
    public int ADD_SAASPART_COL_IND_BILLING_FREQUENCY = 5;
    public int ADD_SAASPART_COL_IND_ENTITLED_RATE = 6;
    public int ADD_SAASPART_COL_IND_BID_RATE = 7;
    public int ADD_SAASPART_COL_IND_OVERRIDE_PRICE = 9;
    public int ADD_SAASPART_COL_IND_DISCOUNT_PERCENT = 10;
    
    //exported saas part cell position for submitted
    public int SAASPART_COL_IND_MIGRATED_PART_SUBMIT = 5;
    public int SAASPART_COL_IND_QUANTITY_SUBMIT = 6;
    public int SAASPART_COL_IND_TERM_SUBMIT = 7;
    public int SAASPART_COL_IND_BILLING_FREQUENCY_SUBMIT = 8;
    public int SAASPART_COL_IND_ENTITLED_RATE_SUBMIT = 9;
    public int SAASPART_COL_IND_BID_RATE_SUBMIT = 10;
	public int SAASPART_COL_IND_TOTAL_POINTS_SUBMIT = 11; 
	public int SAASPART_COL_IND_ENTITLED_TCV_SUBMIT = 12;
	public int SAASPART_COL_IND_DISCOUNT_PERCENT_SUBMIT = 13;
	public int SAASPART_COL_IND_BID_TCV_SUBMIT = 14;
	public int SAASPART_COL_IND_BID_TCV_FORMULA = 14;
	public int SAASPART_COL_IND_BP_DISCOUNT_SUBMIT = 15;
	public int SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT_SUBMIT = 16;
	public int SAASPART_COL_IND_BP_TCV_SUBMIT = 17;
	public int SAASPART_COL_IND_BP_TCV_FORMULA = 17;
	
	//release 14.1 added columns
	public int PART_COL_IND_EC_TOPPERFORMER_PERCENTAGE = 18;
	public int PART_COL_IND_EC_TOPPERFORMER_UNITPRICE = 19;
	public int PART_COL_IND_EC_TOPPERFORMER_EXTENDEDPRICE = 20;
	public int PART_COL_IND_EC_MARKETAVERAGE_PERCENTAGE = 21;
	public int PART_COL_IND_EC_MARKETAVERAGE_UNITPRICE = 22;
	public int PART_COL_IND_EC_MARKETAVERAGE_EXTENDEDPRICE = 23;
	public int PART_COL_IND_EC_PRIORCUSTOMERPURCHASE = 24;
	
	/**
	 * Indicate the subtotal row number (0-based). 
	 * default value is : last part row + 1
	 * Subclass should rewrite this method as needed.
	 * @param lastPartRowIndex
	 * @return
	 */
	public int getSubtotalRowIndex(int lastPartRowIndex){
		return lastPartRowIndex + 1;
	}
	
	/**
	 * Indicate the total row number (0-based). 
	 * default value is : last part row + 3.
	 * Subclass should rewrite this method as needed.
	 * @param lastPartRowIndex
	 * @return
	 */
	public int getTotalRowIndex(int lastPartRowIndex){
		return lastPartRowIndex + 3;
	}
}
