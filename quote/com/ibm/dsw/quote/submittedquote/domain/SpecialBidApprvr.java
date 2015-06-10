package com.ibm.dsw.quote.submittedquote.domain;

import com.ibm.dsw.quote.common.domain.Domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SpecialBidApprvr<code> class.
 *    
 * @author: wangtt@cn.ibm.com
 * 
 * Creation date: May 15, 2007
 */
public interface SpecialBidApprvr extends Domain{

    public String getWebQuoteNum();
    
    public String getPredecessorEmail();

    public String getSpecialBidApprGrp();
    
    public int getSpecialBidApprLvl();

    public String getApprvrEmail();

    public String getApprvrAction();
    
    public String getApplierEmail();
    
    public String getFirstName();
    
    public String getLastName();
    
    public String getLastActApprEmail();
    
    public String getReturnReason();
    
    public void setFirstName(String firstName);
    
    public void setLastName(String lastName);
    
    public void setApplierEmail(String applierEmail) throws Exception;

    public void setWebQuoteNum(String webQuoteNum) throws Exception;

    public void setSpecialBidApprGrp(String specialBidApprGrp) throws Exception;
    
    public void setPredecessorEmail(String predecessorEmail);

    public void setApprvrEmail(String apprvrEmail) throws Exception;

    public void setApprvrAction(String apprvrAction) throws Exception;
    
    public void setSpecialBidApprLvl(int specialBidApprLvl) throws Exception;

	public int getSupersedeApprvFlag();

	public void setSupersedeApprvFlag(int supersedeApprvFlag) ;
	
	public void setReturnReason(String returnReason) throws Exception ;
	
	public void setRdyToOrder(int rdyToOrder);
	
	public int getRdyToOrder();

	public void setIsPGS(int isPGS);
	
	public int getIsPGS();
}
