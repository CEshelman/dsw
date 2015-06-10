package com.ibm.dsw.quote.common.domain;

import java.util.List;
import java.util.Map;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>Partner<code> class is to define the Partner domain object.
 *    
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Mar 4, 2007
 */
public interface Partner {

    public String getQuoteContact();

    public String getCustNum();

    public String getCustNameFull();

    public String getCustName();
    public String getCustName2();
    public String getCustName3();
    
    public String getAddress1();

    public String getAddress2();

    public String getCity();

    public String getState();

    public String getPostalCode();

    public String getCountry();

    public String getPrimaryContact();
    
    public boolean isTierOne();
    
    public boolean isTierTwo();
    
    public int getTierType();

    public boolean isReseller();

    public boolean isDistributor();
    
    public String getEmail();
    
    public String getFaxNum();
    
    public String getPhoneNum();
    
    public String getLineOfBusiness();
    
    public List getRdcNumList();
    
    public String getIbmCustNum();
    
    public String getCurrencyCode();
    
    public String getSapSalesOrgCode();
    
    public List getAuthorizedPortfolioList();
    
    // If reseller is authorized to porfolio regardless of tier 1 partner
    public boolean isAuthorizedPortfolio(String code, int custGovEntIndCode);
    
    // If reseller is authorized to portfolio associated with tier 1 partner
    public boolean isAuthorizedPortfolio(String code, String tier1CustNum, int custGovEntIndCode);
    
    public List getY9EmailList();
    
    public Map getAuthorizedPortfolioMap();
    
    public boolean isPATierOne();
    
    public boolean isSWVNTierOne();
}
