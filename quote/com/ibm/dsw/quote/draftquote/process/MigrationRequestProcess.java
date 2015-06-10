package com.ibm.dsw.quote.draftquote.process;


import java.util.Locale;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.common.domain.MigrateRequest;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>MigrationRequestCustProcess</code> class is business interface for display
 * 
 * 
 * 
 * @author <a href="mmzhou@cn.ibm.com">T </a> <br/>
 * 
 * Creation date: 2012-05-24
 */
public interface MigrationRequestProcess {
    
    public MigrateRequest getMigrtnReqDetailByReqNum(String requestNum) throws QuoteException;
    public void updateMigrateInfByRequestNum(MigrateRequest request,String userId) throws QuoteException;
    public MigrateRequest getMigrtnReqByReqNum(String requestNum) throws QuoteException;
    /**
     * Update sapIDocnum information when successfully call FCT to PA migration RFC, or save failure information when failed.
     */
    public void updateMigrateRequestSubmission(int piSuccessFlag,String piSapIDocNum,String piRequestNum,String piCreatorID,String piPartError,String piHeadError) throws QuoteException;
    /**
     * Call FCT to PA migration RFC.
     */
    public boolean callServicesToFCTToPAMigration(String userId,MigrateRequest migrateRequest,Locale locale)  throws QuoteException, WebServiceException ;
    
    /**
     * Only get request base
     * @param requestNum
     * @return
     * @throws QuoteException
     */
    public MigrateRequest getMigrtnReq(String requestNum) throws QuoteException;
    
    public void updateNewPAWebQuote(String userId, String webQuoteNum, String fctToPaMigrationFlag)throws QuoteException;
}
