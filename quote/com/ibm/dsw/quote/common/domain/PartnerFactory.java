package com.ibm.dsw.quote.common.domain;

import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PartnerFactory<code> class is the abstract domain factory for Partner 
 * domain object.
 *    
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Mar 4, 2007
 */
public abstract class PartnerFactory {
    private static PartnerFactory singleton = null;

    public PartnerFactory() {
    }

    public abstract SearchResultList findResellersByNum(String lobCode, String custCnt, String custNum, int tierType,
            int pageIndex, String webQuoteNum, String audCode, int FCT2PAMigrtnFlag) throws TopazException;

    public abstract SearchResultList findResellersByAttr(String lobCode, String custCnt, String custName,
            String country, String state, int tierType, int pageIndex, String webQuoteNum, int FCT2PAMigrtnFlag) throws TopazException;

    public abstract SearchResultList findDistributorsByNum(String lobCode, String custCnt, String custNum,
            int pageIndex, String webQuoteNum, String audCode, int FCT2PAMigrtnFlag) throws TopazException;

    public abstract SearchResultList findDistributorsByAttr(String lobCode, String custCnt, String custName,
            String country, String state, int pageIndex, String webQuoteNum, int FCT2PAMigrtnFlag) throws TopazException;

    public abstract Partner findPartnerByNum(String partnerNum, String lob) throws TopazException;
    
    public abstract SearchResultList findResellersByPortfolio(String portfolios, String lob, String custCntry,
            int pageIndex , int multipleProdFlag) throws TopazException;

    public static PartnerFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();

        LogContext logCtx = LogContextFactory.singleton().getLogContext();
        if (PartnerFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(PartnerFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                PartnerFactory.singleton = (PartnerFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(PartnerFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(PartnerFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(PartnerFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
    
    public abstract boolean checkPartnerCEID(String custNum) throws TopazException;

}
