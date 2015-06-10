package com.ibm.dsw.quote.massdlgtn.process;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>MassDlgtnProcess</code> class is business interface for quote
 * mass delegation function.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: 2007-3-5
 */
public interface MassDlgtnProcess {

    /**
     * get user's delegate list
     * 
     * @param salesUserID
     * @return delegate list of user
     * @throws QuoteException
     */
    public List getDelegates(String salesUserID) throws QuoteException;

    /**
     * add one delegate to user's list
     * 
     * @param userID
     * @param dlgID
     * @throws QuoteException
     */
    public void addDelegate(String userID, String dlgID,String managerId) throws QuoteException;

    /**
     * remove delegate from user's listF
     * 
     * @param userID
     * @param dlgID
     * @throws QuoteException
     */
    public void removeDelegate(String userID, String dlgID, String managerId) throws QuoteException;
    
    public String retriveFullRepName(String salesUserId)throws QuoteException;

}
