package com.ibm.dsw.quote.relatedbid.process;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RelatedBidProcess<code> class.
 *    
 * @author: daflores@us.ibm.com
 * 
 * Creation date: Jan 14, 2013 
 */
 
//import previous files
import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;


public interface RelatedBidProcess {

	public List searchRelatedbyNum(String renewalQuoteNum) throws QuoteException;
	
	public List findAllAgreementTypes(int agrmntTypeFlag) throws QuoteException;

}