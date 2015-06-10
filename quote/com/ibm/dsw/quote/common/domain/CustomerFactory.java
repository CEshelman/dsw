package com.ibm.dsw.quote.common.domain;

import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.customer.contract.CustomerCreateContract;
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
 * This <code>CustomerFactory<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */
public abstract class CustomerFactory {
    private static CustomerFactory singleton = null;

    /**
     *  
     */
    public CustomerFactory() {
        super();
    }

    public abstract CustomerSearchResultList findByDswIcnRcdID(String sapCtrctNum, String dswIcnRcdID, String cntryCode, 
            String lineOfBus, int startPos, boolean searchPayer, String progMigrtnCode, String audCode, String userSiteNum) throws TopazException;

    public abstract CustomerSearchResultList findByAttribute(String customerName, String cntryCode,
            String contractOption, String anniversary, String lineOfBus, int findActiveFlag, int startPos,
            String stateCode, int searchType, String audCode, String userSiteNum) throws TopazException;
    
    public abstract SearchResultList findDuplCustomers(String customerName, String address, String city,
            String regionCode, String postalCode, String country) throws TopazException;

    public abstract Customer findCustomerByNum(String customerNum) throws TopazException;
    
    public abstract Customer createCustomer(CustomerCreateContract custCreateContract, String userID) throws TopazException;
    
    public abstract Customer createCustomer() throws Exception;
    
    public abstract Customer findByQuoteNum(String lob, int newCustFlag, String custNum, String sapCtrctNum,
            String webQuoteNum, int webCustId) throws TopazException;

    public static CustomerFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (CustomerFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(CustomerFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                CustomerFactory.singleton = (CustomerFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(CustomerFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(CustomerFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(CustomerFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
    public abstract CustomerSearchResultList findEndCusByDswID(String dswIcnRcdID, String lineOfBus,String country, int startPos)
            throws TopazException;

    public abstract CustomerSearchResultList findEndCustByAttr(String customerName, String cntryCode, String lineOfBus,
            String stateCode, int startPos) throws TopazException;
    
    public abstract void updateChannelInfo(String webQuoteNum, String chargeArgNum) throws TopazException;

	public abstract Map searchApplianceAddress(String webQuoteNum,
			String addressType,String lob,String custNum,String bpSiteNum) throws TopazException;
	
	public abstract DefaultCustAddress findCustomerDefaulAddress(String webQuoteNum, String bpSiteNum) throws TopazException;
	
	public abstract boolean updateInstallAtAddr(String webQuoteNum, int installAtOption, int shipToOption) throws TopazException;
	
	public abstract boolean updateApplianceAddress(String userId, String webQuoteNum, String addressType,ApplianceAddress address) throws TopazException;

	public abstract List findLineItemAddr(String webQuoteNum,String bpSiteNum) throws TopazException;

	public abstract boolean updateLineItemAddr(String webQuoteNum, String userId) throws TopazException;
	
    public abstract Customer findCustForExistCtrctCust(String webQuoteNum,String custNum) throws TopazException;
    
    public abstract ApplianceAddress getInstallAtByMTM(String type, String mode, String serialNum, String renewlQuoteNum, int renewalLineItemSeq) throws TopazException;
}
