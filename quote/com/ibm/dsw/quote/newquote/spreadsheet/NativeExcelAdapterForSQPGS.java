package com.ibm.dsw.quote.newquote.spreadsheet;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NativeExcelAdapterForSQ<code> class.
 *    
 * @author: liangyue@cn.ibm.com
 * 
 * Creation date: Mar 25, 2010
 */

public class NativeExcelAdapterForSQPGS extends NativeExcelAdapter {
    
    public NativeExcelAdapterForSQPGS(){
        this.PART_COL_IND_BRAND = 2;
        this.PART_COL_IND_PART_TYPE = 3;
        this.PART_COL_IND_RESELLER_AUTHORIZATION = 4;
        this.PART_COL_IND_RESELLER_AUTHORIZATION_TERMS = 5;
        this.PART_COL_IND_QUANTITY = 6;
        this.PART_COL_IND_STANDARD_START_DATE_FORMULA = 7;
        this.PART_COL_IND_STANDARD_END_DATE_FORMULA = 8;
        this.PART_COL_IND_PRORATE_MONTHS = 9;
        this.PART_COL_IND_COMPRESSED_COVERAGE_MONTHS = 10;
        this.PART_COL_IND_RENEWAL_QUOTE_NUMBER = 11;
        this.PART_COL_IND_ITEM_POINTS = 12;
        this.PART_COL_IND_ENTITLED_UNIT_PRICE_FORMULA = 13;
        this.PART_COL_IND_TOTAL_POINTS_FORMULA = 14;
        this.PART_COL_IND_BID_UNIT_PRICE_FORMULA = 15;
        this.PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA = 16;
        this.PART_COL_IND_DISCOUNT_PERCENT = 17;
        this.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA = 18;
        this.PART_COL_IND_BP_DISCOUNT = 19;
        this.PART_COL_IND_BP_OVERRIDE_DISCOUNT = 20;
        this.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA = 21;
        this.PART_COL_IND_TOTAL_LINE_DISCOUNT_FORMULA = 22;
        this.PART_COL_IND_BID_UNIT_PRICE_DECIMAL_4 = 23;
        
        this.SQ_POSITION_OPPORTUNITY_NUMBER = "C10";
        this.SQ_POSITION_FULFILLMENT_SOURCE = "C11";
        this.SQ_POSITION_QUOTE_NUMBER = "C13";
        this.SQ_POSITION_SAP_CONFIRMATION_NUMBER = "C14";
        this.SQ_POSITION_OVERALL_STATUS = "C16";
        this.SQ_POSITION_QUOTE_START_DATE = "C17";
        this.SQ_POSITION_QUOTE_EXPIRATION_DATE = "C18";
        this.SQ_POSITION_SB_APPROVED_DATE = "C19";
        this.SQ_POSITION_QUOTE_CONTACT = "C20";
        this.SQ_POSITION_QUOTE_DETAIL_URL = "C22";
        this.SQ_POSITION_SITE_NUMBER = "C27";
        this.SQ_POSITION_IBM_CUSTOMER_NUMBER = "C28";
        this.SQ_POSITION_CUSTOMER_NAME = "C30";
        this.SQ_POSITION_CUSTOMER_ADDRESS1 = "C31";
        this.SQ_POSITION_CUSTOMER_ADDRESS2 = "C32";
        this.SQ_POSITION_CUSTOMER_ADDRESS3 = "C33";
        this.SQ_POSITION_AGREEMENT_NUMBER = "C38";
        this.SQ_POSITION_ANNIVERSARY_NUMBER = "C40";
        this.SQ_POSITION_RELATIONSHIP_SVP_LEVEL = "C41";
        this.SQ_POSITION_RESELLER_CUST_NUM = "C46";
        this.SQ_POSITION_RESELLER_ICN = "C47";
        this.SQ_POSITION_RESELLER_CUST_NAME = "C49";
        this.SQ_POSITION_RESELLER_ADDRESS1 = "C50";
        this.SQ_POSITION_RESELLER_ADDRESS2 = "C51";
        this.SQ_POSITION_RESELLER_ADDRESS3 = "C52";
        this.SQ_POSITION_DISTRIBUTER_CUST_NUM = "C57";
        this.SQ_POSITION_DISTRIBUTER_ICN = "C58";
        this.SQ_POSITION_DISTRIBUTER_CUST_NAME = "C60";
        this.SQ_POSITION_DISTRIBUTER_ADDRESS1 = "C61";
        this.SQ_POSITION_DISTRIBUTER_ADDRESS2 = "C62";
        this.SQ_POSITION_DISTRIBUTER_ADDRESS3 = "C63";
    }
}
