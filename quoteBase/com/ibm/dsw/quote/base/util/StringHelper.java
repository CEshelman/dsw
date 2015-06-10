package com.ibm.dsw.quote.base.util;

import is.security.RandomString;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>StringHelper<code> class.
 *
 * @author: zhengmr@cn.ibm.com
 *
 * Creation date: 2007-4-26
 */

public class StringHelper {

    public static final int DB_STRING_LENGTH = 10;
    public static final char DB_FILLED_CHAR = '0';

    /**
     *
     */
    public StringHelper() {
    }
    
    public static List parseStr2List(String rdcNums) {

        ArrayList rdcNumList = new ArrayList();
        if (StringUtils.isBlank(rdcNums))
            return rdcNumList;

        StringTokenizer st = new StringTokenizer(rdcNums, ",");
        while (st.hasMoreTokens()) {
            String rdc = st.nextToken();
            if (StringUtils.isNotBlank(rdc))
                rdcNumList.add(rdc.trim());
        }
        return rdcNumList;
    }

    public static String fillString(String inStr) {
        return fillString(inStr, DB_STRING_LENGTH, DB_FILLED_CHAR);
    }

    public static String fillString(String inStr, int length, char filledChar) {
        if (StringUtils.isBlank(inStr) || inStr.length() >= length)
            return inStr;

        int n = length - inStr.length();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < n; i++)
            sb.append(filledChar);
        sb.append(inStr);

        return sb.toString();
    }

    public static String addPercentSign(String inStr, int maxLength) {
        inStr = inStr == null ? "" : inStr.trim();
        if (StringUtils.isBlank(inStr))
            return "%";
        else if (inStr.length() >= maxLength)
            return inStr;
        else
            return "%"+inStr;
    }

    public static String addPercentSign(String inStr) {
        return addPercentSign(inStr, DB_STRING_LENGTH);
    }

    public static List parseStr2List(String tmpStr, List list){
	    String[] tempArray = tmpStr.split("\\n");
		for(int i=0;i<tempArray.length;i++){
			list.add(tempArray[i].trim());
		}
		return list;
    }

	/**
	 * @return boolean
	 * This method is to validate if the input are all regular char0~9, a~z, A~Z,
	 * it will return true, if the input are all numbers
	 */
	public static boolean hasNonRegularChar(String fieldValue) {
		fieldValue = fieldValue.trim();
		boolean hasNonchar = false;
		LogContext logCtx = LogContextFactory.singleton().getLogContext();
		try {
			for (int i = 0; i < fieldValue.length(); i++) {
				char s = fieldValue.charAt(i);
				if (!((s >= '0' && s <= '9')
					|| (s >= 'a' && s <= 'z')
					|| (s >= 'A' && s <= 'Z')
					|| (s == ' ')
					|| (s == '-')
					|| (s == '_'))) {
					hasNonchar = true;
					break;
				}
			}
		} catch (Exception e) {
			logCtx.error(StringHelper.class, e.getMessage());
		}
		return hasNonchar;
	}

	 public static String replaceStr(String tmpStr, String arg){
		Object[] args = {arg};
		MessageFormat form = new MessageFormat(tmpStr);
		return form.format(args);
    }

	/** Generates Web Auth Temp Id
     */
	public static String generateWebAuthTempId() {
	    int maxSize = 15;
	    int maxDigits = 2;
	    RandomString pc = new RandomString(maxSize, maxDigits);
	    String entKey = pc.generatePasscode();

	    return entKey;
	}

	public static boolean containsIgnoreCase(String str, String srchdStr) {
	    if (str == null || srchdStr == null)
	        return false;

	    return str.toUpperCase().indexOf(srchdStr.toUpperCase()) >= 0;
	}

	public static boolean isValidPrice(String str){
		Pattern pattern = Pattern.compile("^[0-9]+([.]{0}|[.]{1}[0-9]+)$");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()){
			return true;
		}
		return false;
	}

	public static boolean isAlphanumeric(String alphanumeric){
		Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
		Matcher isNum = pattern.matcher(alphanumeric);
		if (isNum.matches()){
			return true;
		}
		return false;

	}
	
	/**
	 * copy method code from class MailProcess_Impl
	 * @param str
	 * @return
	 */
	public static  String handleSpecialChar(String str) {
		if (str==null) return str;
		
	   	int i = 0;
	   	int pos = str.indexOf("<", i);
	   	int len = str.length();
	   	
	   	while ((pos != -1)) {
	   		if (pos+1 == len) return str;

	   		if (str.charAt(pos+1)!=' ') {
	   			str = str.substring(0,pos+1) + " " + str.substring(pos+1);
	   		    ++len;
	   		}
			    i = pos + 2;
			    pos = str.indexOf("<", i);
	   	}
	   		
		return str;
	   }
	
   public static boolean isEmptyRichEditorContent(String str){
	   if(str == null ) return true;
	   if(delHTMLTag(str).equals("")){
		   return true;
	   }
	   return false;
   }
   public static String delHTMLTag(String htmlStr) {
	   String regExscript = "<script[^>]*?>[\\s\\S]*?<\\/script>";
	   String regExstyle = "<style[^>]*?>[\\s\\S]*?<\\/style>";
	   String regExhtml = "<[^>]+>";
	   Pattern patScript = Pattern.compile(regExscript, Pattern.CASE_INSENSITIVE);
	   Matcher matScript = patScript.matcher(htmlStr);
	   htmlStr = matScript.replaceAll("");//filter script tag
	   
	   Pattern patStyle = Pattern.compile(regExstyle, Pattern.CASE_INSENSITIVE);
       Matcher MatStyle = patStyle.matcher(htmlStr);
       htmlStr = MatStyle.replaceAll(""); //filter style tag
       
       Pattern patHtml = Pattern.compile(regExhtml, Pattern.CASE_INSENSITIVE);
       Matcher matHtml = patHtml.matcher(htmlStr);
       htmlStr = matHtml.replaceAll("");// filter html tag
       
       htmlStr =htmlStr.replaceAll("&nbsp;", "");
       return htmlStr.trim();
   }
   public static String removeCDATATag(String str){
        if ( str == null )
        {
            return str;
        }
        str = str.trim();
        if ( !str.startsWith("<![CDATA[") || !str.endsWith("]]>") )
		{
			return str;
		}
		int len = str.length();
		str = str.substring(9, len - 3);
		str = str.trim();
		return str;
   }
}