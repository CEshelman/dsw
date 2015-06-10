package com.ibm.dsw.quote.base.util;

import org.apache.commons.lang.StringUtils;

public class StringEncoder { 

    /**
    * Copyright 2007 by IBM Corporation All rights reserved.
    *
    * This software is the confidential and proprietary information of IBM
    * Corporation. ("Confidential Information").
    *
    * This <code>LogThrowableUtil<code> class ...
    *
    * @author: wnan@cn.ibm.com
    *
    * Creation date: Apr 9, 2007
    */

	private static final char[] specialChar = {'&', '<', '>','\'','\"'};

    private static String replaceAll ( String src , String fnd , String rep) {
        if ( src == null || src.equals("")){ return ""; } 
        String dst = src ;
        int idx = dst.indexOf ( fnd);
        while ( idx >= 0){
            if(" ".equals(fnd)){
                if(dst.length()> idx + 1 && dst.charAt(idx+1) == ' '){
                    dst = dst.substring(0, idx)+ rep + dst.substring (idx + fnd.length (), dst.length () );
                    idx = dst.indexOf(fnd, idx + rep.length()); 
                }else{
                    idx = dst.indexOf(fnd, idx+1);
                    continue;
                }  
            }else{
                dst = dst.substring(0, idx)+ rep + dst.substring (idx + fnd.length (), dst.length () );
                idx = dst.indexOf(fnd, idx + rep.length()); 
            }
        }
     	return dst; 
	}
    
	public static String filterHTML(String src) {
	    if(src == null || src.equals("")){ return ""; }
	    String dst = src;
	    dst = replaceAll(dst, "&amp;", "&");
	    dst = replaceAll(dst, "&lt;", "<");
	    dst = replaceAll(dst, "&gt;", ">");
	    dst = replaceAll(dst, "&quot;", "");
	    dst = replaceAll(dst, "&#039;", "");
	    dst = replaceAll(dst , "&nbsp;", " ");
	    dst = replaceAll(dst, "<br />", " ");
	    dst = replaceAll(dst, "<br/>", " ");
	    dst = replaceAll(dst, "<u>", " ");
	    dst = replaceAll(dst, "</u>", " ");
	    dst = replaceAll(dst, "</ u>", " ");
	    return dst;
	}
	
	public static String textToHTML(String src) {
		return textToHTML(src, false);
	}
	
	public static String textToHTML(String src, boolean skippingSpace) {
	    if(src == null || src.equals("")){ return ""; }
	    String dst = src;
	    dst = replaceAll(dst, "&", "&amp;");
	    dst = replaceAll(dst, "<", "&lt;");
	    dst = replaceAll(dst, ">", "&gt;");
	    dst = replaceAll(dst, "\"", "&quot;");
	    dst = replaceAll(dst, "'", "&#039;");
	    if(!skippingSpace){
	    	dst = replaceAll(dst," " , "&nbsp;");
	    }
	    dst = replaceAll(dst, "\r\n", "<br />");
	    dst = replaceAll(dst, "\r", "<br />");
	    dst = replaceAll(dst, "\n", "<br />");
	    return dst;
	}
	
	public static boolean containSpecialChar(String inStr){
		if(StringUtils.containsNone(inStr, specialChar)){
			return false;
		}else return true;
	}
} 