package com.ibm.dsw.quote.ps.domain.jdbc;

import java.util.List;

import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.dsw.quote.ps.domain.PartSearchResult;
import com.ibm.dsw.quote.ps.domain.PartSearchResultFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.CacheableFactory;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>oooooo.java</code> class is to oooooo
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public class PartSearchResultFactory_jdbc extends PartSearchResultFactory implements
		CacheableFactory {

	/**
	 *  
	 */
	public PartSearchResultFactory_jdbc() {
		super();
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.dsw.quote.demo.domain.CustomerListFactory#find(java.lang.String,
	 *      java.lang.String)
	 */
	public PartSearchResult findByNum(String partNumbers, String creatorId, String lob, String acqrtnCode, String countryCode, String audience)
			throws TopazException {
	    //create a PartList_jdbc
	    LogContext logContext = LogContextFactory.singleton().getLogContext();
	    logContext.debug(this,"Find part By Number --- Input parameters: "+partNumbers+" | "+creatorId+" | "+lob+" | "+acqrtnCode+" | "+countryCode+" | "+audience+" ---------");
	    PartSearchResult_jdbc partList = new PartSearchResult_jdbc(PartSearchParamKeys.SEARCH_PARTS_BY_NUMBERS, partNumbers, creatorId, lob, acqrtnCode, countryCode, audience);
	    //System.out.println("in factory, about to hydrate");
	    partList.hydrate(TopazUtil.getConnection());
	    logContext.debug(this,"Find part By Number --- Result:"+ (partList == null) +" | "+ ((partList != null &&  partList.getPartCount()==1) ? partList.getParts().get(0) : "Empty Result"));
		return partList;
	}

	public PartSearchResult findByDesc(String description, String creatorId, String lob)
			throws TopazException {
	    //create a PartList_jdbc
	    PartSearchResult_jdbc partList = new PartSearchResult_jdbc(PartSearchParamKeys.SEARCH_PARTS_BY_DESCRIPTION, description, creatorId, lob,"","","");
	    partList.hydrate(TopazUtil.getConnection());
		
		return partList;
	}
	
	public PartSearchResult findRelatedParts(String partNumber, String creatorId, String lob)
	throws TopazException {
		//create a PartList_jdbc
		PartSearchResult_jdbc partList = new PartSearchResult_jdbc(PartSearchParamKeys.GET_RELATED_PARTS, partNumber, creatorId, lob,"","","");
		partList.hydrate(TopazUtil.getConnection());
		return partList;
	}

	public PartSearchResult findRelatedPartsLic(String partNumber, String creatorId, String lob)
	throws TopazException {
		//create a PartList_jdbc
		PartSearchResult_jdbc partList = new PartSearchResult_jdbc(PartSearchParamKeys.GET_RELATED_PARTS_LIC, partNumber, creatorId, lob,"","","");
		partList.hydrate(TopazUtil.getConnection());
		return partList;
	}
	
	public PartSearchResult findReplacementParts(String partNumber, String creatorId, String lob, String acqrtnCode, String countryCode, String audience)
	throws TopazException {
		//create a PartList_jdbc
		PartSearchResult_jdbc partList = new PartSearchResult_jdbc(PartSearchParamKeys.GET_REPLACEMENT_PARTS, partNumber, creatorId, lob, acqrtnCode, countryCode, audience);
		//System.out.println("in factory, about to hydrate");
		partList.hydrate(TopazUtil.getConnection());
		return partList;
	}
	
	//for PA (and PAE) type searches
	public PartSearchResult findByProdCodes(List prodCodes, String creatorId, String lob)
			throws TopazException {
	    //create a PartList_jdbc
	    PartSearchResult_jdbc partList = new PartSearchResult_jdbc(prodCodes, creatorId, lob);
	    partList.hydrate(TopazUtil.getConnection());
		
		return partList;
	}

	//for non pa searches
	public PartSearchResult findByLob(String creatorId, String lob)
			throws TopazException {
	    //create a PartList_jdbc
	    PartSearchResult_jdbc partList = new PartSearchResult_jdbc(creatorId, lob);
	    partList.hydrate(TopazUtil.getConnection());
		
		return partList;
	}

	public PartSearchResult saveSelectedParts(List selectedParts, String creatorId, String searchString, String dataRetrievalType, String chrgAgrmtNum, String configrtnActionCode, String orgConfigId)
			throws TopazException {
	    //create a PartList_jdbc
	    PartSearchResult_jdbc partList = new PartSearchResult_jdbc(selectedParts, creatorId, searchString, dataRetrievalType, chrgAgrmtNum, configrtnActionCode, orgConfigId);
//	    try{
	        partList.persist(TopazUtil.getConnection());
	        
//	    }catch (SPException e){
	        LogContext logContext = LogContextFactory.singleton().getLogContext();
	        logContext.debug(this,"Exceed max line item limits"+partList.getExceedCode());
	        return partList;
//	    }
	}

	public String toString() {
		// Insert code to print the receiver here.
		// This implementation forwards the message to super. You may replace or
		// supplement this.
		return super.toString();
	}

	/**
	 * @see CacheableFactory#putInCache(Object, Object)
	 */
	public void putInCache(Object objectId, Object object)
			throws TopazException {
		// register new instance in the cache
		TransactionContextManager.singleton().getTransactionContext().put(
				PartSearchResultFactory.class, objectId, object);
	}

	/**
	 * @see CacheableFactory#getFromCache(Object)
	 */
	public Object getFromCache(Object objectId) throws TopazException {
		return TransactionContextManager.singleton().getTransactionContext()
				.get(PartSearchResultFactory.class, objectId);
	}

//	private String getId(String number, String contract) {
//		return number + "_" + contract;
//	}
}
