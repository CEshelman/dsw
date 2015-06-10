package com.ibm.dsw.quote.common.domain;

import java.util.List;

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
 * This <code>QuoteLineItemFactory<code> class is QuoteLineItem factory.
 *    
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Mar 1, 2007
 */
public abstract class QuoteLineItemFactory {

    private static QuoteLineItemFactory singleton = null;

    public QuoteLineItemFactory() {
        super();
    }
    
    //Create a new LineItem for a Quote
    public abstract QuoteLineItem createQuoteLineItem(String webQuoteNum, Part part, String userID) throws TopazException;
    
    public abstract QuoteLineItem createQuoteLineItem(String webQuoteNum, String partNum, String userID) throws TopazException;
    
    public abstract QuoteLineItem createNewLineItemFromExistItem(QuoteLineItem item) throws TopazException;

    //Retrieve all LineItems given a WebQuoteNum
    public abstract List findLineItemsByWebQuoteNum(String webQuoteNum) throws TopazException;
    
  //Retrieve all LineItems given a WebQuoteNum
    public abstract List findPartsWithoutValidation(String webQuoteNum) throws TopazException;
    
    //Retrieve master LineItems build with additional maintenance given a WebQuoteNum
    public abstract List findMasterLineItemsByWebQuoteNum(String WebQuoteNum) throws TopazException;
    
    public abstract List findSaaSLineItemsNotPartOfAddOnTradeUpByWebQuoteNum(String WebQuoteNum) throws TopazException;
   
    public abstract void updateLineItemQty(String webQuoteNum, int seqNum, float partQty, String overrdCode, int configNum, String userID) throws TopazException;
    
    public abstract void updateLineItemCRAD(String webQuoteNum, int seqNum, java.sql.Date lineItemCRAd, String userID)throws TopazException;
    
    public abstract void getEolHistPrice(List eolPartList, String countryCode, String currencyCode, String priceLevel) throws TopazException;
    
    public abstract void delInvalidLineItemsByWebQuoteNum(String WebQuoteNum, List invalidPartList) throws TopazException;

    
    public static QuoteLineItemFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteLineItemFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(QuoteLineItemFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteLineItemFactory.singleton = (QuoteLineItemFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteLineItemFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteLineItemFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteLineItemFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
    
    // refactor, update the param to List lineItems
    public abstract List findMasterLineItems(List lineItems) throws TopazException;
    public abstract List findSaaSLineItems(List lineItems) throws TopazException;
    // retrieve all omit renewal line price 
    public abstract OmitRenewalLine getOmittedRenewalLine(String webQuoteNum) throws TopazException;
     // remove all omit renewal line by web quote number
    public abstract void reomveOmittedRenewalLine(String webQuoteNum) throws TopazException;
    
    public abstract MonthlySwLineItem createMonthlySwLineItem(String webQuoteNum,String partNum,String userID) throws TopazException;
	public abstract Integer getMTMWarningFlg(String quoteNum,String partNum,String machineType,String machineModel,String MachineSerialNumber ) throws TopazException;


}
