package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>Partner_Impl</code> class implements the Partner domain object.
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Mar 5, 2007
 */
public class Partner_Impl implements Partner {

    private boolean reseller = true;

    public String address1;

    public String address2;

    public String city;

    public String country;

    public String custNum;

    public String custNameFull;
    
    public String custName;
    
    public String custName2;
    
    public String custName3;

    public String postalCode;
    
    public int PATierType;		// 1: PA Tier 1, 2: PA Tier 2         
    
    public int SWVNTierType;	// 1: SW VN Tier 1, 2: PA Tier 2
    
    public String primaryContact;

    public String quoteContact;

    public String state;
    
    public String phoneNum;
    
    public String faxNum;
    
    public String email;
    
    public String lineOfBusiness;
    
    public List rdcNumList = new ArrayList();
    
    public String ibmCustNum;
    
    public String currencyCode;
    
    public String sapSalesOrgCode;
    
    public List y9EmailList = new ArrayList();

    // Authorized portfolios regardless of tier1 partners (element type is CodeDescObj)
    public List authorizedPortfolioList = new ArrayList();
    
    // Authorized portfolios and associated tier1 partners
    public Map authorizedPortfolioMap = new LinkedHashMap();

    public Partner_Impl() {

    }

    public Partner_Impl(boolean isReseller) {
        reseller = isReseller;
    }

    /**
     * @return Returns the custNum.
     */
    public String getCustNum() {
        return custNum;
    }

    /**
     * @return Returns the tierType.
     */
    public int getTierType() {
        if (PATierType == 1 || SWVNTierType == 1)
            return 1;
        else
            return 2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Partner#getCustName()
     */
    public String getCustNameFull() {
        return custNameFull;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Partner#getCustName2()
     */
    public String getCustName() {
        return custName;
    }
    public String getCustName2() {
        return custName2;
    }
    public String getCustName3() {
        return custName3;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Partner#getAddress1()
     */
    public String getAddress1() {
        return address1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Partner#getAddress2()
     */
    public String getAddress2() {
        return address2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Partner#getCity()
     */
    public String getCity() {
        return city;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Partner#getState()
     */
    public String getState() {
        return state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Partner#getPostalCode()
     */
    public String getPostalCode() {
        return postalCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Partner#getCounty()
     */
    public String getCountry() {
        return country;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Partner#getprimaryContact()
     */
    public String getPrimaryContact() {
        return primaryContact;
    }

    /**
     * @return Returns the reseller.
     */
    public boolean isReseller() {
        return reseller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Partner#isDistributor()
     */
    public boolean isDistributor() {
        return !reseller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.Partner#getQuoteContact()
     */
    public String getQuoteContact() {
        return quoteContact;
    }

    /**
     * @return Returns the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return Returns the faxNum.
     */
    public String getFaxNum() {
        return faxNum;
    }

    /**
     * @return Returns the phoneNum.
     */
    public String getPhoneNum() {
        return phoneNum;
    }
    
    /**
     * @return Returns the lineOfBusiness.
     */
    public String getLineOfBusiness() {
        return lineOfBusiness;
    }
    /**
     * @return Returns the ibmCustNum.
     */
    public String getIbmCustNum() {
        return ibmCustNum;
    }
    /**
     * @return Returns the rdcNum.
     */
    public List getRdcNumList() {
        return rdcNumList;
    }
    
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    public String getSapSalesOrgCode() {
        return sapSalesOrgCode;
    }
    
    public List getAuthorizedPortfolioList() {
        return authorizedPortfolioList;
    }
    
    public Map getAuthorizedPortfolioMap() {
        return authorizedPortfolioMap;
    }
    
    public List getY9EmailList() {
        return y9EmailList;
    }
    
    public boolean isAuthorizedPortfolio(String code, int custGovEntIndCode) {

        if (authorizedPortfolioMap == null)
            return false;

        List tier1List = (List) authorizedPortfolioMap.get(code);

        // for cust is gov entity
        if (custGovEntIndCode == 1) {
            return (tier1List != null && tier1List.size() > 0);
        }
        // for cust is NOT gov entity
        else {
            for (int i = 0; tier1List != null && i < tier1List.size(); i++) {
                RselCtrldDistribtn rselCtrldDistribtn = (RselCtrldDistribtn) tier1List.get(i);
                if ("0".equalsIgnoreCase(rselCtrldDistribtn.getSapGovRselFlag()))
                    return true;
            }

            return false;
        }
    }
    
    // If reseller is authorized to portfolio associated with tier 1 partner
    public boolean isAuthorizedPortfolio(String code, String tier1CustNum, int custGovEntIndCode) {

        if (authorizedPortfolioMap == null)
            return false;

        List tier1List = (List) authorizedPortfolioMap.get(code);

        for (int i = 0; tier1List != null && i < tier1List.size(); i++) {
            RselCtrldDistribtn rselCtrldDistribtn = (RselCtrldDistribtn) tier1List.get(i);
            boolean matchTier1 = false;
            // for cust is gov entity
            if (custGovEntIndCode == 1) {
                matchTier1 = tier1CustNum.equalsIgnoreCase(rselCtrldDistribtn.getTier1CustNum());
            }
            // for cust is NOT gov entity
            else {
                matchTier1 = tier1CustNum.equalsIgnoreCase(rselCtrldDistribtn.getTier1CustNum())
                        && "0".equalsIgnoreCase(rselCtrldDistribtn.getSapGovRselFlag());
            }

            if (matchTier1)
                return true;
        }

        return false;
    }
    
    public boolean isTierOne(){
        return (this.PATierType == 1 || this.SWVNTierType == 1);
    }
    
    public boolean isTierTwo(){
        return (this.PATierType != 1 && this.SWVNTierType != 1);
    }
    
    public boolean isPATierOne() {
        return this.PATierType == 1;
    }
    
    public boolean isSWVNTierOne() {
        return this.SWVNTierType == 1;
    }

}
