package com.ibm.dsw.quote.common.util;

import java.sql.Date;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;

public class EqualsCheckingUtils {

	public static boolean isEqualed(Object obj1, Object obj2){
		if(obj1==null||obj2==null){
			if(obj1==null&&obj2==null){
				return true;
			}
			return false;
		}
		
		if(obj1 instanceof String&&obj2 instanceof String){
		    return StringUtils.equals(obj1.toString(), obj2.toString());
		}
		
		if(obj1 instanceof Date&&obj2 instanceof Date){
		    return DateUtil.isYMDEqual((Date) obj1, (Date) obj2);
		}
		
		if(obj1 instanceof Integer&&obj2 instanceof Integer){
			return Integer.parseInt(obj1.toString())==Integer.parseInt(obj2.toString());			
		}
		
		if(obj1 instanceof Boolean&&obj2 instanceof Boolean){
			return Boolean.parseBoolean(obj1.toString())==Boolean.parseBoolean(obj2.toString());	
		}
		
		if(obj1 instanceof Double&&obj2 instanceof Double){
			return DecimalUtil.isEqual(Double.parseDouble(obj1.toString()), Double.parseDouble(obj2.toString()));
		}
		
		if(obj1 instanceof Long&&obj2 instanceof Long){
			return Long.parseLong(obj1.toString())==Long.parseLong(obj2.toString());	
		}
		
		if(obj1 instanceof Float&&obj2 instanceof Float){
			return Float.parseFloat(obj1.toString())== Float.parseFloat(obj2.toString());	
		}
		return false;
	}
	
	public static void main(String[] args) {
		Double d1=4.78;
		Double d2=4.78;
		System.out.println(EqualsCheckingUtils.isEqualed(d1, d2));
	}
}
