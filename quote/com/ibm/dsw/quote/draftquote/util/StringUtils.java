/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>StringUtils</code> class is to define a series of string util methods
 *
 * 
 * @author: <a href="mailto:mmzhou@cn.ibm.com">Tyler Zhou </a>
 * 
 * Creation date: 2012-6-25
 */
package com.ibm.dsw.quote.draftquote.util;

public class StringUtils {
	
	/**
	 * encode text so that Json can read it 
	 * @param orgText
	 * @return
	 */
	public static String jsonStringEncoding(String orgText){
		if(orgText == null){
			return orgText;
		}
		String desText  = null;
		desText = orgText.replace("\\", "\\\\");
		desText = desText.replace("\"", "\\\"");
		desText = desText.replace("'", "\\'");
		desText = desText.replace("\b", "\\b");
		desText = desText.replace("\f", "\\f");
		desText = desText.replace("\n", "\\n");
		desText = desText.replace("\r", "\\r");
		desText = desText.replace("\t", "\\t");
		return desText;
	}
	
	
}
