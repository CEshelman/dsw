package com.ibm.dsw.quote.draftquote.process;

import java.util.HashMap;

import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>QuoteToUProcess</code> class 
 *
 * @author: whliul@cn.ibm.com 
 *
 * Creation date: May 31, 2013
 */
public interface QuoteToUProcess {
	public HashMap lookUpToUList(String number)  throws QuoteException;
	public int getCountOfCsaTerms();
}
