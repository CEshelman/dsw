package com.ibm.dsw.quote.relatedbid.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RelatedBid<code> class.
 *    
 * @author: daflores@us.ibm.com
 * 
 * Creation date: Jan 10, 2013
 */
 
 public interface RelatedBid extends Serializable{
  
	public String getSqoRef();
	public String getCurrencyCode();
	public String getQuoteNum();
	public String getRenewalQuoteNum();
	public List<String> getRenewalLineItemNumList();
	public Date getSubmittedDate();
	public String getOverallStatus();
	public String getResellerNum();
	public String getResellerName();
	public String getDistributorNum();	
	public String getDistributorName();
	public Double getQuoteTotal();
	public Double getOverallGrowth();
	public Double getImpliedGrowth();
}