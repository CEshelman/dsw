package com.ibm.dsw.quote.ps.domain;

import java.util.List;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>CustomerListFactory.java</code> class is the abstract factory
 * class to create CustomerList domain objects.
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public abstract class PartSearchResultFactory {
    private static PartSearchResultFactory singleton = null;

    /**
     * AddressFactory constructor comment.
     */
    public PartSearchResultFactory() {
        super();
    }

    /**
     * @return PartList
     */
    public abstract PartSearchResult findByNum(String partNumbers, String creatorId, String lob, String acqrtnCode, String countryCode, String audience) throws TopazException;
    /**
     * @return PartList
     */
    public abstract PartSearchResult findByDesc(String desc, String creatorId, String lob) throws TopazException;

    public abstract PartSearchResult findRelatedParts(String partNumber, String creatorId, String lob) throws TopazException;

    public abstract PartSearchResult findRelatedPartsLic(String partNumber, String creatorId, String lob) throws TopazException;    
    
    public abstract PartSearchResult findReplacementParts(String partNumber, String creatorId, String lob, String acqrtnCode, String countryCode, String audience) throws TopazException;
    /**
     * findByBrand is used for part browsing
     * @return PartList
     */
    public abstract PartSearchResult findByProdCodes(List prodCodes, String creatorId, String lob) throws TopazException;

    /**
     * findByBrand is used for part browsing
     * @return PartList
     */
    public abstract PartSearchResult findByLob(String creatorId, String lob) throws TopazException;

    /**
     * findByBrand is used for part browsing
     * @return PartList
     */
    public abstract PartSearchResult saveSelectedParts(List selectedParts, String creatorId, String searchString, String dataRetrievalType, String chrgAgrmtNum, String configrtnActionCode, String orgConfigId) throws TopazException;

    /**
     * @return PartListFactory
     */
    public static PartSearchResultFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (PartSearchResultFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(
                        PartSearchResultFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                PartSearchResultFactory.singleton = (PartSearchResultFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(PartSearchResultFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {

                logCtx.error(PartSearchResultFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(PartSearchResultFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
}
