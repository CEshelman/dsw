package com.ibm.dsw.quote.newquote.spreadsheet;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NativeExcelAdapterForPAE<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 18, 2010
 */

public class NativeExcelAdapterForPAE extends NativeExcelAdapter {
    {
//      exported part cell position
        PART_COL_IND_PART_NUMBER = 0;
        PART_COL_IND_PART_DESCRIPTION = 1;
    	
        //release 14.1 added "Brand" and "Part type"
    	PART_COL_IND_BRAND = 2;
    	PART_COL_IND_PART_TYPE = 3;
    	
        PART_COL_IND_RESELLER_AUTHORIZATION = 4;
        PART_COL_IND_RESELLER_AUTHORIZATION_TERMS = 5;
        PART_COL_IND_QUANTITY = 6;
        PART_COL_IND_ADD_YEARS = 7;
        PART_COL_IND_STANDARD_START_DATE_FORMULA = 8;
        PART_COL_IND_STANDARD_END_DATE_FORMULA = 9;
        PART_COL_IND_OVERRIDE_START_DATE = 10;
        PART_COL_IND_OVERRIDE_END_DATE = 11;
        PART_COL_IND_RENEWAL_QUOTE_NUMBER = 12;
        PART_COL_IND_ENTITLED_UNIT_PRICE_FORMULA = 13;
        PART_COL_IND_OVERRIDE_PRICE = 14;
        PART_COL_IND_DISCOUNT_PERCENT = 15;
        
    	//release 14.1 Equity curve
    	PART_COL_IND_EC_TOPPERFORMER_PERCENTAGE = 16;
    	PART_COL_IND_EC_TOPPERFORMER_UNITPRICE = 17;
    	PART_COL_IND_EC_TOPPERFORMER_EXTENDEDPRICE = 18;
    	PART_COL_IND_EC_MARKETAVERAGE_PERCENTAGE = 19;
    	PART_COL_IND_EC_MARKETAVERAGE_UNITPRICE = 20;
    	PART_COL_IND_EC_MARKETAVERAGE_EXTENDEDPRICE = 21;
    	PART_COL_IND_EC_PRIORCUSTOMERPURCHASE = 22;
        
        PART_COL_IND_BID_UNIT_PRICE_FORMULA = 23;
        PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA = 24;
        PART_COL_IND_BID_EXTENDED_PRICE_FORMULA = 25;
        PART_COL_IND_BP_DISCOUNT = 26;
        PART_COL_IND_BP_OVERRIDE_DISCOUNT = 27;
        PART_COL_IND_BP_EXTENDED_PRICE_FORMULA = 28;
        PART_COL_IND_X_FORMULA = 29;
        PART_COL_IND_RENEWAL_QUOTE_LINE_ITEM_NUMBER = 30;
        PART_COL_IND_SORT_SEQ_NUM = 31;
        PART_COL_IND_ADDITIONAL_YEAR_COVERAGE_SEQ_NUM = 32; //33 is not used -- Now  33 start to use add by Karl
        PART_COL_IND_HIDDEN_BP_EXTENDED_PRICE = 33;
        PART_COL_IND_HIDDEN_TOTAL_PRICE = 34;
        PART_COL_IND_HIDDEN_QUANTITY = 35;
    	PART_COL_IND_HIDDEN_OVERRIDE_START_DATE = 36;
    	PART_COL_IND_HIDDEN_OVERRIDE_END_DATE = 37;
        PART_COL_IND_HIDDEN_OVERRIDE_PRICE = 38;
        PART_COL_IND_HIDDEN_DISCOUNT_PERCENT = 39;
        PART_COL_IND_CHANGED_FLAG_FORMULA = 40;
        PART_COL_IND_HIDDEN_STANDARD_START_DATE = 41;
        PART_COL_IND_HIDDEN_BID_UNIT_PRICE = 42;
        PART_COL_IND_HIDDEN_ENTITLED_EXT_PRICE = 43; //44 is not used -- Now  44 start to use add by Karl
        PART_COL_IND_HIDDEN_BP_OVERRIDE_DISCOUNT=44;
        
    	PART_COL_IND_BID_UNIT_PRICE_DECIMAL_4 = 45;
    	PART_COL_IND_OVERRIDE_PRICE_DECIMAL_4 = 45;
        
    	// additional part cell position
    	ADD_PART_COL_IND_PART_NUMBER = 0;
    	ADD_PART_COL_IND_PART_DESCRIPTION = 1;
    	ADD_PART_COL_IND_QUANTITY = 2;
    	ADD_PART_COL_IND_ADD_YEARS = 3;
    	ADD_PART_COL_IND_STANDARD_START_DATE_FORMULA = 4;
    	ADD_PART_COL_IND_STANDARD_END_DATE_FORMULA = 5;
    	ADD_PART_COL_IND_OVERRIDE_START_DATE = 6;
    	ADD_PART_COL_IND_OVERRIDE_END_DATE = 7;
    	ADD_PART_COL_IND_ENTITLED_UNIT_PRICE = 8;
    	ADD_PART_COL_IND_OVERRIDE_PRICE = 9;
    	ADD_PART_COL_IND_DISCOUNT_PERCENT = 10;
    	
    	// exported saas part cell position
    	SAASPART_COL_IND_ENTITLED_RATE = 9;
        SAASPART_COL_IND_BID_RATE = 10;
        SAASPART_COL_IND_ENTITLED_TCV = 11;
        SAASPART_COL_IND_OVERRIDE_PRICE = 12;
        SAASPART_COL_IND_DISCOUNT_PERCENT = 13;
        SAASPART_COL_IND_BID_TCV = 14;
        SAASPART_COL_IND_BP_DISCOUNT = 15;
        SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT = 16;
        SAASPART_COL_IND_BP_RATE = 17;
        SAASPART_COL_IND_BP_TCV = 18;
    }
    
	/**
	 * Indicate the subtotal row number for draft PA quote template.
	 * the return value is lastPartRowIndex + 2.
	 */
	@Override
	public int getSubtotalRowIndex(int lastPartRowIndex) {
		return lastPartRowIndex + 2;
	}

	/**
	 * Indicate the subtotal row number for draft PA quote template.
	 * the return value is lastPartRowIndex + 4
	 */
	@Override
	public int getTotalRowIndex(int lastPartRowIndex) {
		return lastPartRowIndex + 4;
	}	
}
