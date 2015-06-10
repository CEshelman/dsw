/*
 * Created on Mar 13, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.common.util.spbid.date;

import java.sql.Date;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpecialBidUtil</code>  
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 10, 2008
 */
public class SpecialBidUtil {
    public static void sortLineItemByStartDate(List items){
        
        Collections.sort(items, new Comparator() {
			public int compare(Object obj1, Object obj2) {
				if (obj1 instanceof QuoteLineItem && obj2 instanceof QuoteLineItem) {
					Date date1 = ((QuoteLineItem) obj1).getMaintStartDate();
					Date date2 = ((QuoteLineItem) obj2).getMaintStartDate();
					return date1.compareTo(date2);
				}
				return 0;
			}
		});
    }
}
