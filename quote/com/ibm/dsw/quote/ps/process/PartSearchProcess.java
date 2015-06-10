package com.ibm.dsw.quote.ps.process;

import java.rmi.RemoteException;
import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.ps.domain.PartSearchResult;
import com.ibm.dsw.quote.ps.domain.PartSearchServiceResult;
import com.ibm.ead4j.topaz.exception.TopazException;
/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SearchProcess</code> clsss defines the interface for search
 * process
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public interface PartSearchProcess {
    
    public final static int PS_MAX_PARTS_EXPANDED = 50;
    public final static int PS_MAX_PRODUCTS_EXPANDED = 10;
    
    public PartSearchResult searchPartsByNumber(String partNumbers, String creatorId, String lob, String acqrtnCode, String countryCode, String audience) throws RemoteException, QuoteException, TopazException;
    public PartSearchResult searchPartsByDescription(String description, String creatorId, String lob) throws RemoteException, QuoteException, TopazException;
    public PartSearchResult browseParts(String prodCodes, String brand, String lob, String creatorId) throws RemoteException, QuoteException, TopazException;
    public PartSearchResult searchRelatedParts(String partNumber, String creatorId, String lob) throws RemoteException, QuoteException, TopazException;
    public PartSearchResult searchRelatedPartsLic(String partNumber, String creatorId, String lob) throws RemoteException, QuoteException, TopazException;
    public PartSearchResult searchReplacementParts(String partNumber, String creatorId, String lob, String acqrtnCode, String countryCode, String audience) throws RemoteException, QuoteException, TopazException;
    //public PartSearchResult searchPartsByLob(String lob, String country, String currency, String audience) throws RemoteException, QuoteException, TopazException;
    public int saveSelectedParts(List parts, String creatorId, String parentPartNum, String dataRetrievalType, String chrgAgrmtNum, String configrtnActionCode, String orgConfigId) throws RemoteException, QuoteException, TopazException;
    public int addReplForObsParts(String webQuoteNumber,int seqNumber,List parts) throws QuoteException;
    public PartSearchServiceResult getPartSearchServiceResults(String creatorId) throws QuoteException;
}
