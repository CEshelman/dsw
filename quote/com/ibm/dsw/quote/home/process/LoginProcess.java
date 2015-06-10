package com.ibm.dsw.quote.home.process;

import java.rmi.RemoteException;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SalesRep;


/**
 * <p>The LoginProcess represents what activities the LoginProcess can invoke.</p>
 * 
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mailto:mgivney@us.ibm.com">Matt Givney</a><br/>
 *
 */
public interface LoginProcess{

    /**
     * Logs the user to the database
     * @param salesRep the <code>SalesRep</code>
     * @throws RemoteException
     * @throws QuoteException
     */
    public SalesRep logUser( String intranetId, int telesalesAccess) throws QuoteException;
    
    public SalesRep evaluatorUser(String intranetId) throws QuoteException;
}
