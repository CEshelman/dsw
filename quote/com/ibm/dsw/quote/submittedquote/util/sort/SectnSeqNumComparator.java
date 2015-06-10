package com.ibm.dsw.quote.submittedquote.util.sort;

import java.util.Comparator;

import com.ibm.dsw.quote.common.domain.QuoteLineItem;

/*
 * Copyright 2008 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * @author Xiao Guo Yi
 * 
 * Created on 2008-12-4
 */
public class SectnSeqNumComparator implements Comparator{
	  public int compare(Object o1, Object o2) {
	  	if(o1 == null || o2 == null){
	  		return 0;
	  	}
	  	
	    QuoteLineItem item1 = (QuoteLineItem) o1;
	    QuoteLineItem item2 = (QuoteLineItem) o2;
	    return item1.getQuoteSectnSeqNum()-item2.getQuoteSectnSeqNum();
	}
}
