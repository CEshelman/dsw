package com.ibm.dsw.quote.newquote.spreadsheet;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NativeExcelAdapterForPA<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 18, 2010
 */

public class NativeExcelAdapterForPA extends NativeExcelAdapter {
	public NativeExcelAdapterForPA(){
		//release 14.1 added "Brand" and "Part type"
		PART_COL_IND_BRAND = 2;
		PART_COL_IND_PART_TYPE = 3;

		PART_COL_IND_RESELLER_AUTHORIZATION = 4;
		PART_COL_IND_RESELLER_AUTHORIZATION_TERMS = 5;
		PART_COL_IND_QUANTITY = 6;
		PART_COL_IND_PRORATE= 7;
		PART_COL_IND_ADD_YEARS = 8;
		PART_COL_IND_STANDARD_START_DATE_FORMULA = 9;
		PART_COL_IND_STANDARD_END_DATE_FORMULA = 10;
		PART_COL_IND_OVERRIDE_START_DATE = 11;
		PART_COL_IND_OVERRIDE_END_DATE = 12;
		PART_COL_IND_RENEWAL_QUOTE_NUMBER = 13;
		PART_COL_IND_ITEM_POINTS = 14;
		PART_COL_IND_ENTITLED_UNIT_PRICE_FORMULA = 15;
		PART_COL_IND_OVERRIDE_PRICE = 16;
		PART_COL_IND_DISCOUNT_PERCENT = 17;
		//release 14.1 Equity curve
		PART_COL_IND_EC_TOPPERFORMER_PERCENTAGE = 18;
		PART_COL_IND_EC_TOPPERFORMER_UNITPRICE = 19;
		PART_COL_IND_EC_TOPPERFORMER_EXTENDEDPRICE = 20;
		PART_COL_IND_EC_MARKETAVERAGE_PERCENTAGE = 21;
		PART_COL_IND_EC_MARKETAVERAGE_UNITPRICE = 22;
		PART_COL_IND_EC_MARKETAVERAGE_EXTENDEDPRICE = 23;
		PART_COL_IND_EC_PRIORCUSTOMERPURCHASE = 24;

		PART_COL_IND_TOTAL_POINTS_FORMULA = 25;
		PART_COL_IND_BID_UNIT_PRICE_FORMULA = 26;
		PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA = 27;
		PART_COL_IND_BID_EXTENDED_PRICE_FORMULA = 28;
		PART_COL_IND_BP_DISCOUNT = 29;
		PART_COL_IND_BP_OVERRIDE_DISCOUNT = 30;
		PART_COL_IND_BP_EXTENDED_PRICE_FORMULA = 31;	
		PART_COL_IND_X_FORMULA = 32;
		PART_COL_IND_RENEWAL_QUOTE_LINE_ITEM_NUMBER = 33;
		PART_COL_IND_SORT_SEQ_NUM = 34;
		PART_COL_IND_ADDITIONAL_YEAR_COVERAGE_SEQ_NUM = 35;//col 36 is not used
		PART_COL_IND_HIDDEN_TOTAL_PRICE = 37;
		PART_COL_IND_HIDDEN_QUANTITY = 38;
		PART_COL_IND_HIDDEN_OVERRIDE_START_DATE = 39;
		PART_COL_IND_HIDDEN_OVERRIDE_END_DATE = 40;
		PART_COL_IND_HIDDEN_OVERRIDE_PRICE = 41;
		PART_COL_IND_HIDDEN_DISCOUNT_PERCENT = 42;
		PART_COL_IND_CHANGED_FLAG_FORMULA = 43;
		PART_COL_IND_HIDDEN_STANDARD_START_DATE = 44;
		PART_COL_IND_HIDDEN_BID_UNIT_PRICE = 45;
		PART_COL_IND_HIDDEN_ENTITLED_EXT_PRICE = 46;
		PART_COL_IND_HIDDEN_ENTITLED_UNIT_PRICE = 47;
		PART_COL_IND_HIDDEN_BP_EXTENDED_PRICE = 48;
		PART_COL_IND_HIDDEN_BP_OVERRIDE_DISCOUNT = 49;
		//col 50 are not used
		PART_COL_IND_BID_UNIT_PRICE_DECIMAL_4 = 51;
		PART_COL_IND_OVERRIDE_PRICE_DECIMAL_4 = 51;
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
