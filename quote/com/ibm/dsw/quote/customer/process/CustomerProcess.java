package com.ibm.dsw.quote.customer.process;

import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceFailureException;
import com.ibm.dsw.quote.common.domain.ApplianceAddress;
import com.ibm.dsw.quote.common.domain.ApplianceLineItemAddrDetail;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.customer.contract.CreateApplianceAddressContract;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerProcess<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */
public interface CustomerProcess {

    CustomerSearchResultList searchCustomerByDswIcnRcdID(String sapCtrctNum, String dswIcnRcdID, 
    		String cntryCode, String lineOfBus, int startPos, boolean searchPayer, String progMigrtnCode, String audCode, String userSiteNum)
            throws QuoteException;

    CustomerSearchResultList searchCustomerByAttr(String customerName, String cntryCode, String contractOption,
            String anniversary, String lineOfBus, int findActiveFlag, int startPos, String stateCode,
            int searchType, String audCode, String userSiteNum) throws QuoteException;
    
    CustomerSearchResultList searchAddressByAttr(String webQuoteNum, 
    		String customerName, String stateCode, String userSiteNum, 
    		int shareAgreement, int startIndex, String userIDInSession) throws QuoteException;

    SearchResultList findDuplCustomers(String customerName, String address, String city,
            String regionCode, String postalCode, String country) throws QuoteException;

    Customer createCustomer(ProcessContract contract) throws QuoteException;

    Customer getWebCustomerByQuoteNum(String webQuoteNum, String lob, String soldToCustNum, String sapCtrctNum,
            int webCustId) throws SPException, QuoteException;

    List findAllAgreementTypes(int agrmntTypeFlag) throws QuoteException;
    
    List findAllAgreementOptions() throws QuoteException;
    List findPAUNAgreementOptions() throws QuoteException;

    CustomerSearchResultList searchEndCustByDswID(String dswID, String lineOfBus,String country, int startPos)
            throws QuoteException;

    CustomerSearchResultList searchEndCustByAttr(String customerName, String cntryCode, String lineOfBus,
            String stateCode, int startPos) throws QuoteException;
    
    Map searchApplianceAddress(String webQuoteNum, String addressType,String lob,String custNum,String bpSiteNum)throws QuoteException;
    
    boolean updateCustInstallAtOpt(String webQuoteNum, int installAtOption, int shipToOption) throws QuoteException;
    
    Customer createApplianceCustomer(CreateApplianceAddressContract createApplianceAddressContract) throws Exception, WebServiceFailureException;
    
	void updateApplianceAddress(String userId, String webQuoteNum, String addressType,List<ApplianceAddress> addressList) throws QuoteException;
	
	ApplianceLineItemAddrDetail findLineItemAddr(String webQuoteNum,String bpSiteNum, List quoteLineItems, boolean isHasNeedShipToParts, Customer soldToCustomer)throws QuoteException;
	
	ApplianceLineItemAddrDetail findAddrLineItem(String webQuoteNum,String bpSiteNum, List quoteLineItems, boolean isHasNeedShipToParts, Customer soldToCustomer)throws QuoteException;
	
	boolean updateLineItemAddr(String webQuoteNum, String userId) throws QuoteException;
	
	public ApplianceAddress getInstallAtByMTM(String type, String mode, String serialNum, String renewlQuoteNum, int renewalLineItemSeq) throws QuoteException;
}
