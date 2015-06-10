/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RelatedBid_Impl<code> class.
 *    
 * @author: daflores@us.ibm.com
 * 
 * Creation date: Jan 14, 2013 
 */
 
package com.ibm.dsw.quote.relatedbid.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;


public class RelatedBid_Impl implements RelatedBid {

	private static final long serialVersionUID = 750060066313051932L;
	
	
	public String sqoRef = "";
	public String quoteNum = "";
	public String currencyCode = "";
	public String renewalQuoteNum = "";
	public List<String> renewalLineItemNumList = new ArrayList<String>();;
	public Date submittedDate = null;
	public String overallStatus = "";
	public String resellerNum = "";
	public String resellerName = "";
	public String distributorNum = "";
	public String distributorName = "";
	public Double quoteTotal = null;
	public Double overallGrowth = null;
	public Double impliedGrowth = null;

		
	public String getSqoRef(){
		return StringUtils.trimToEmpty(sqoRef);
	}
	
	public String getQuoteNum(){
		return StringUtils.trimToEmpty(quoteNum);
	}
	
	public String getCurrencyCode() {
		return currencyCode;
	}
		
	public String getRenewalQuoteNum(){
		return StringUtils.trimToEmpty(renewalQuoteNum);
	}
	
	public List<String> getRenewalLineItemNumList(){
		return renewalLineItemNumList;
	}
	
	public Date getSubmittedDate(){
		return submittedDate;
	}
	
	public String getOverallStatus(){
		return StringUtils.trimToEmpty(overallStatus);
	}
		
	public String getResellerNum(){
		return StringUtils.trimToEmpty(resellerNum);
	}
	
	public String getResellerName(){
		return StringUtils.trimToEmpty(resellerName);
	}
	
	public String getDistributorNum(){
		return StringUtils.trimToEmpty(distributorNum);
	}
	
	public String getDistributorName(){
		return StringUtils.trimToEmpty(distributorName);
	}
	
	public Double getQuoteTotal(){
		return quoteTotal;
	}
	
	public Double getOverallGrowth(){
		return overallGrowth;
	}
	
	public Double getImpliedGrowth(){
		return impliedGrowth;
	}
	
	public void addRenewalQuoteLineItemNum(String lineItemNum){
		if(StringUtils.isNotBlank(lineItemNum) && !renewalLineItemNumList.contains(lineItemNum)){
			this.renewalLineItemNumList.add(lineItemNum);
		}
	}
}