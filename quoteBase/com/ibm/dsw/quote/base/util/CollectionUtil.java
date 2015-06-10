package com.ibm.dsw.quote.base.util;

import java.util.Collection;
import java.util.Map;

/**
 * Copyright 2014 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CollectionUtils</code> class is to validate the collections is empty
 * 
 * @author <a href="lchlcd@cn.ibm.com">Jovo Li </a> <br/>
 * 
 * Creation date: Dec 2, 2014
 * Modify: add method isNotEmpty    ----Dec 08, 2014 by Wade
 */

public class CollectionUtil {
	
	/**
	 * Answer if source Collection Object is null or size is zero
	 * @param c
	 * @return true if c(java.util.Collection) is Object level null, or c is with size Zero 
	 */
	public static boolean isEmpty(final Collection<?> c){
		//Default look size zero as null
		return c==null || c.size()==0;
	}
	
	/**
	 * Answer if source Map Object is null or map contains no elements 
	 * @param map
	 * @return true if map(java.util.Map) is Object level null, or map contains no elements 
	 */
	public static boolean isEmpty(final Map<?,?> map){
		//Default look no elements as null
		return map == null || map.isEmpty();
	}
	
	/**
	 * Answer if source Collection Object is not null and size > 0
	 * @param c
	 * @return true if c(java.util.Collection) is not Object level null and c is with size > 0 
	 */
	public static boolean isNotEmpty(final Collection<?> c)
	{
		//Not object level null and size > 0
		return null != c && c.size() > 0;
	}
	
	/**
	 * Answers if this Map is not null and has any elements(size > 0)
	 * @param map
	 * @return
	 */
	public static boolean isNotEmpty(final Map<?, ?> map)
	{
		//Not object level null and contain at least one elements
		return null != map && !map.isEmpty();
	}
}
