package com.ibm.dsw.quote.ps.process;

import java.rmi.RemoteException;
import java.util.List;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.ps.domain.PartSearchResult;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SearchProcess_Impl.java</code> class
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public abstract class PartSearchProcess_Impl extends TopazTransactionalProcess implements PartSearchProcess {
   
    private boolean isBrowseByProdCodes = false;
    
    /**
     * inputs : piAudience, piLineOfBusCode, piCntryCode, piCurrncyCode, piPartNumList [32762]
     */
	public abstract PartSearchResult searchPartsByNumber(String partNumbers, String creatorId, String lob, String acqrtnCode, String countryCode, String audience)
			throws RemoteException, QuoteException, TopazException;
	
	public abstract PartSearchResult searchPartsByDescription(String description, String creatorId, String lob)
			throws RemoteException, QuoteException, TopazException;

	public abstract PartSearchResult browseParts(String prodCodes, String brand, String lob, String creatorId)
			throws RemoteException, QuoteException, TopazException;
	
	public abstract PartSearchResult searchRelatedParts(String partNumbers, String creatorId, String lob)
	throws RemoteException, QuoteException, TopazException;

	public abstract PartSearchResult searchRelatedPartsLic(String partNumbers, String creatorId, String lob)
	throws RemoteException, QuoteException, TopazException;
	
	public abstract PartSearchResult searchReplacementParts(String partNumbers, String creatorId, String lob, String acqrtnCode, String countryCode, String audience)
	throws RemoteException, QuoteException, TopazException;
	/*
	public abstract PartSearchResult searchPartsByLob(String lob, String country, String currency, String audience)
			throws RemoteException, QuoteException, TopazException;
	*/

	public abstract int saveSelectedParts(List selectedParts, String creatorId, String searchString, String dataRetrievalType, String chrgAgrmtNum, String configrtnActionCode, String orgConfigId)
			throws RemoteException, QuoteException, TopazException;
	
	protected boolean isBrowseByProdCodes(String lob) {
	    boolean result = false;
	    //add OEM support
	    if(isPA(lob) || QuoteConstants.LOB_OEM.equalsIgnoreCase(lob.trim())) {
	        result = true;
	    } else {
	        result = false;
	    }
	    return result;
	}
    
    public static boolean isPA(String lob) {
        boolean isPA = lob.trim().equals(QuoteConstants.LOB_PA)||lob.trim().equals(QuoteConstants.LOB_PAE)?true:false;
        return isPA;
    }
    
    public static boolean isPartCountBeyondExpandLimit(int count) {
        boolean result = false;
        if(count > PS_MAX_PARTS_EXPANDED) {
            result = true;
        }
        return result;
    }
}
