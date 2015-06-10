package com.ibm.dsw.quote.relatedbid.domain;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RelatedBidInfo<code> class.
 *    
 * @author: daflores@us.ibm.com
 * 
 * Creation date: Jan 14, 2013 
 */

import java.util.List;

public class RelatedBidInfo {
	List<RelatedBid> relatedBidList;
	
	public List<RelatedBid> getRelatedBidList() {
		return relatedBidList;
	}
	
	public void setRelatedBidList(List<RelatedBid> relatedBidList) {
		this.relatedBidList = relatedBidList;
	}
}
