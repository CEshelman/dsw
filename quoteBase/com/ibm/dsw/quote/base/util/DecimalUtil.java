package com.ibm.dsw.quote.base.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DecimalUtil</code> class is to format the decimals
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Apr 13, 2007
 */

public class DecimalUtil {

    public static final int DEFAULT_SCALE = 2;

    /**
     * @param d
     * @return
     */
    public static String format(double d) {
        return format(d, DEFAULT_SCALE);
    }

    /**
     * Format number taking default currency and scale as pattern
     * 
     * @param d
     * @param scale
     * @return formatted string for input number
     * @author Goshen
     */
    public static String format(double d, int scale) {
        if (scale < 0) {
            scale = DEFAULT_SCALE;
        }
        // set up pattern for scale
        StringBuffer patternBuffer = new StringBuffer("#,##0");
        if (scale > 0)
            patternBuffer.append(".");
        for (int i = 0; i < scale; i++) {
            patternBuffer.append("0");
        }
        String pattern = patternBuffer.toString() + ";-" + patternBuffer.toString() + "";
        // get default decimal formatter
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return decimalFormat.format(roundAsDouble(d,scale));
    }

    public static double roundAsDouble(double d, int scale) {

        if (scale < 0) {
            scale = 0;
        }
        return new BigDecimal(String.valueOf(d)).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();

    }

    /**
     * Format a double to 0.NNNNN or NNN.NN
     * 
     * @param d
     * @return
     */
    public static String formatTo5Number(double d) {
        if (isEqual(d , 0.0)) {
            return "0.000";
        }
        boolean negative = false;
        if (d < 0) {
            d = -d;
            negative = true;
        }
        String result = "";
        if (d < 1) {
            result = new BigDecimal(String.valueOf(d)).setScale(5, BigDecimal.ROUND_HALF_UP).toString();
        } else {
            result = new BigDecimal(String.valueOf(d)).setScale(3, BigDecimal.ROUND_HALF_UP).toString();
        }

        return negative ? "-" + result : result;

    }
    
    public static String formatToTwoDecimals(double d) {
        if (isEqual(d , 0.0)) {
            return "0.00";
        }
    	
        boolean negative = false;
        if (d < 0) {
            d = -d;
            negative = true;
        }
        String result = "";
        result = new BigDecimal(String.valueOf(d)).setScale(2, BigDecimal.ROUND_HALF_UP).toString();

        return negative ? "-" + result : result;

    }
    
    public static String calculateDiscount(double discPrice, double originalPrice){
        if(originalPrice == 0.0){
            return "";
        }
        double discount = (discPrice / originalPrice);       
        discount = (1 - discount);
        discount = roundAsDouble(discount, 5);
        
        return formatTo5Number(discount * 100);
    }
    
    public static double convertStringToDouble(String value){
    	if(StringUtils.isNotEmpty(value)){
    		return Double.parseDouble(value);
    	}else{
    		return 0.0d;
    	}
    }
    
    public static String formatTo5Number(Double d){
        if(null == d){
            return "";
        }else{
            return formatTo5Number(d.doubleValue());
        }
    }

    public static boolean isNotEqual(double d1, double d2) {
        return Math.abs(d1 - d2) > .0000001;
    }

    public static boolean isEqual(double d1, double d2) {
        return Math.abs(d1 - d2) < .0000001;
    }
}
