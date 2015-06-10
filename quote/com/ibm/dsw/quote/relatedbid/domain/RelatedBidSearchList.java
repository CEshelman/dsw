package com.ibm.dsw.quote.relatedbid.domain;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RelatedBidSeachList<code> class.
 *    
 * @author: daflores@us.ibm.com
 * 
 * Creation date: Jan 20, 2013 
 */

import com.ibm.dsw.quote.common.domain.SearchResultList;


public class RelatedBidSearchList extends SearchResultList {
	
	public RelatedBidSearchList() {
        super();
    }
	
	public RelatedBidSearchList(int resultCount, int pageSize, int pageNumber) {
        super(resultCount, pageSize, pageNumber);
	}
}
