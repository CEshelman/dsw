package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.QuoteConstants;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>Customer_Impl<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */
public abstract class Customer_Impl implements Customer {

    public String address1 = "";

    public String internalAddress = "";

    public String city = "";
    
    public String state = "";

    public String countryCode = "";

    public String custName = "";

    public String custNum = "";

    public String ibmCustNum = "";

    public String postalCode = "";

    public String sapRegionCodeDscr = "";
    
    public String sapContractNum = "";
    
    public int webCustId = 0;
    
    public String tempAccessNum = "";
    
    public String webCustTypeCode = "";
    
    public int sapCntId = 0;
    
    public String currencyCode = "";
    
    public String sapIntlPhoneNumFull = "";
    
    public String custVatNum = "";
    
    public String sapWwIsuCode = "";
    
    public int empTot = 0;
    
    public String isoLangCode = "";
    
    public String upgrLangCode = "";
    
    public String cntFirstName = "";
    
    public String cntLastName = "";
    
    public String cntPhoneNumFull = "";
    
    public String cntFaxNumFull = "";
    
    public String cntEmailAdr = "";
    
    public String mktgEmailFlag = "";
    
    public String webCustStatCode = "";
    
    public String sapRegionCode = "";
    
    public String sapCntPrtnrFuncCode = "";
    
    public boolean gsaStatusFlag;
    
    public ArrayList contractList = new ArrayList();

    public int ctrctHasPreApprPrcLvlFlg;
    
    public String purchOrdReqrdFlag; // "N" or "Y"
    
    public String custName2 = "";
    
	public String custName3 = "";
    
    public String salesOrg = "";
    
    public List rdcNumList = new ArrayList();

    public String custDesignation = "";
    
    public String custSetDesCode = "";
    
    public String custSetDesCode2 = "";
    
    public String authrztnGroup = "";			// Authorization group specified by user, only valid for new US govenment customer
    
    public String agreementType = "";		// Agreement type code specified by user, only valid for new customer
    
    public String transSVPLevel = "";		// Transaction SVP level specified by user, only valid for new customer
    
    public int govEntityIndCode = 0;
    
    public String govEntityIndCodeDesc = "";
    
    public List enrolledCtrctList = new ArrayList();
    
    public List csaContractList = new ArrayList(); //SAP_CTRCT_VARIANT_CODE='CSA'
    public List noCsaContractList = new ArrayList();//SAP_CTRCT_VARIANT_CODE!='CSA'

    public int PAIndCode = 0;
    
    public int supprsPARegstrnEmailFlag = 0 ;
    


	private boolean isCustOrdBlcked = false;
    
    private String cvrageGrpCodeDesc = "";
    
    private String custCvrageDesc = "";
    
    private String initWebAppBusDscr = "";
    
    private String currWebAppBusDscr = "";
    
    private boolean relatedwithBPFlag = false;
    
    private String customerType = "";
    
    private String addressType = null;
    
	public boolean isBPRelatedCust;
	
	private boolean isCustOnlySSP=false;
	
	public boolean isGOECust = false; 
	
	public boolean isMthlySwTermsAccptd;

	public boolean isMthlySwTermsAccptd() {
		return isMthlySwTermsAccptd;
	}

	public void setMthlySwTermsAccptd(boolean isMthlySwTermsAccptd) {
		this.isMthlySwTermsAccptd = isMthlySwTermsAccptd;
	}

	public void setCustOnlySSP(boolean isCustOnlySSP) {
		this.isCustOnlySSP = isCustOnlySSP;
	}

	public boolean isCustOnlySSP() {
		return isCustOnlySSP;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public boolean isRelatedwithBPFlag() {
		return relatedwithBPFlag;
	}

	public void setRelatedwithBPFlag(boolean relatedwithBPFlag) {
		this.relatedwithBPFlag = relatedwithBPFlag;
	}

	public Customer_Impl() {
        super();
    }
    
    /**
     * @return Returns the address1.
     */
    public String getAddress1() {
        return address1;
    }
    /**
     * @return Returns the city.
     */
    public String getCity() {
        return city;
    }
    /**
     * @return Returns the cntEmailAdr.
     */
    public String getCntEmailAdr() {
        return cntEmailAdr;
    }
    /**
     * @return Returns the cntFaxNumFull.
     */
    public String getCntFaxNumFull() {
        return cntFaxNumFull;
    }
    /**
     * @return Returns the cntFirstName.
     */
    public String getCntFirstName() {
        return cntFirstName;
    }
    /**
     * @return Returns the cntLastName.
     */
    public String getCntLastName() {
        return cntLastName;
    }
    /**
     * @return Returns the cntPhoneNumFull.
     */
    public String getCntPhoneNumFull() {
        return cntPhoneNumFull;
    }
    /**
     * @return Returns the countryCode.
     */
    public String getCountryCode() {
        return countryCode;
    }
    /**
     * @return Returns the currencyCode.
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    /**
     * @return Returns the custName.
     */
    public String getCustName() {
        return custName;
    }
    /**
     * @return Returns the custNum.
     */
    public String getCustNum() {
        return custNum;
    }
    /**
     * @return Returns the custVatNum.
     */
    public String getCustVatNum() {
        return custVatNum;
    }
    /**
     * @return Returns the empTot.
     */
    public int getEmpTot() {
        return empTot;
    }
    /**
     * @return Returns the ibmCustNum.
     */
    public String getIbmCustNum() {
        return ibmCustNum;
    }
    /**
     * @return Returns the internalAddress.
     */
    public String getInternalAddress() {
        return internalAddress;
    }
    /**
     * @return Returns the isoLangCode.
     */
    public String getIsoLangCode() {
        return isoLangCode;
    }
    /**
     * @return Returns the mktgEmailFlag.
     */
    public String getMktgEmailFlag() {
        return mktgEmailFlag;
    }
    /**
     * @return Returns the postalCode.
     */
    public String getPostalCode() {
        return postalCode;
    }
    /**
     * @return Returns the sapCntId.
     */
    public int getSapCntId() {
        return sapCntId;
    }
    /**
     * @return Returns the sapContractNum.
     */
    public String getSapContractNum() {
        return sapContractNum;
    }
    /**
     * @return Returns the sapIntlPhoneNumFull.
     */
    public String getSapIntlPhoneNumFull() {
        return sapIntlPhoneNumFull;
    }
    /**
     * @return Returns the sapRegionCode.
     */
    public String getSapRegionCode() {
        return sapRegionCode;
    }
    /**
     * @return Returns the sapRegionCodeDscr.
     */
    public String getSapRegionCodeDscr() {
        return sapRegionCodeDscr;
    }
    /**
     * @return Returns the sapWwIsuCode.
     */
    public String getSapWwIsuCode() {
        return sapWwIsuCode;
    }
    /**
     * @return Returns the tempAccessNum.
     */
    public String getTempAccessNum() {
        return tempAccessNum;
    }
    /**
     * @return Returns the upgrLangCode.
     */
    public String getUpgrLangCode() {
        return upgrLangCode;
    }
    /**
     * @return Returns the webCustId.
     */
    public int getWebCustId() {
        return webCustId;
    }
    /**
     * @return Returns the webCustStatCode.
     */
    public String getWebCustStatCode() {
        return webCustStatCode;
    }
    /**
     * @return Returns the webCustTypeCode.
     */
    public String getWebCustTypeCode() {
        return webCustTypeCode;
    }
    /**
     * @return Returns the sapCntPrtnrFuncCode.
     */
    public String getSapCntPrtnrFuncCode() {
        return sapCntPrtnrFuncCode;
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.Customer#getGsaStatusFlag()
     */
    public boolean getGsaStatusFlag() {
        return gsaStatusFlag;
    }
    /**
     * @return Returns the contractList.
     */
    public List getContractList() {
        return contractList;
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.Customer#getCtrctHasPreApprPrcLvlFlg()
     */
    public int getCtrctHasPreApprPrcLvlFlg() {
        return ctrctHasPreApprPrcLvlFlg;
    }
    
    /**
     * @return Returns the purchOrdReqrdFlag.
     */
    public String getPurchOrdReqrdFlag() {
        return purchOrdReqrdFlag;
    }
    /**
     * @return Returns the custName2.
     */
    public String getCustName2() {
        return custName2;
    }
    /**
     * @return Returns the custName3.
     */
    public String getCustName3() {
        return custName3;
    }
    /**
     * @return Returns the salesOrg.
     */
    public String getSalesOrg() {
        return salesOrg;
    }
    
    public List getRdcNumList() {
        return rdcNumList;
    }
    
    public boolean isPAWebCustomer() {
        return QuoteConstants.LOB_PA.equalsIgnoreCase(getWebCustTypeCode()) || QuoteConstants.LOB_CSRA.equalsIgnoreCase(getWebCustTypeCode());
    }
    
    public boolean isAddiSiteCustomer() {
        return CustomerConstants.AGRMNT_TYPE_ADDI_SITE.equalsIgnoreCase(getAgreementType());
    }
    
    public boolean isACACustomer() {
        return CustomerConstants.AGRMNT_TYPE_ACADEMIC.equalsIgnoreCase(getAgreementType());
    }
    
    public boolean isGOVCustomer() {
        return CustomerConstants.AGRMNT_TYPE_GOVERNMENT.equalsIgnoreCase(getAgreementType());
    }
    
    public boolean isSTDCustomer() {
        return CustomerConstants.AGRMNT_TYPE_STANDARD.equalsIgnoreCase(getAgreementType());
    }
    
    public boolean isXSPCustomer() {
        return CustomerConstants.AGRMNT_TYPE_XSP.equalsIgnoreCase(getAgreementType());
    }

    public boolean hasContract() {
        List ctrctList = this.getContractList();
        if (ctrctList != null && ctrctList.size() > 0) {
            Iterator iter = ctrctList.iterator();
            while (iter.hasNext()) {
                Contract ctrct = (Contract) iter.next();
                if (ctrct != null && StringUtils.isNotBlank(ctrct.getSapContractNum()))
                    return true;
            }
        }
        return false;
    }
    
    public boolean hasActiveContract() {
        List ctrctList = this.getContractList();
        if (ctrctList != null && ctrctList.size() > 0) {
            Iterator iter = ctrctList.iterator();
            while (iter.hasNext()) {
                Contract ctrct = (Contract) iter.next();
                if (ctrct != null && StringUtils.isNotBlank(ctrct.getSapContractNum())
                        && ctrct.getIsContractActiveFlag() == 1)
                    return true;
            }
        }
        return false;
    }

    /**
     * @return Returns the custDesignation.
     */
    public String getCustDesignation() {
        return custDesignation;
    }
    
    public String getAuthrztnGroup() {
        return authrztnGroup;
    }
    public String getCustSetDesCode() {
        return custSetDesCode;
    }
    public void setCustSetDesCode(String custSetDesCode) {
        this.custSetDesCode = custSetDesCode;
    }
    public String getCustSetDesCode2() {
        return custSetDesCode2;
    }
    public void setCustSetDesCode2(String custSetDesCode2) {
        this.custSetDesCode2 = custSetDesCode2;
    }
    public String getAgreementType() {
        return agreementType;
    }
    
    public String getTransSVPLevel() {
        return transSVPLevel;
    }
    
    public List getEnrolledCtrctList() {
        return enrolledCtrctList;
    }
    public List getCsaContractList() {
		return csaContractList;
	}

	public List getNoCsaContractList() {
		return noCsaContractList;
	}
    public int getPAIndCode() {
        return PAIndCode;
    }
    
    public boolean isActivePACust() {
        return (PAIndCode == 1);
    }
    
    public boolean isInactivePACust() {
        return (PAIndCode == 2 || PAIndCode == 3);
    }
    
    public int getGovEntityIndCode() {
        return govEntityIndCode;
    }
    
    public String getGovEntityIndCodeDesc() {
        return govEntityIndCodeDesc;
    }
    
    public boolean isEnrolledCtrctTranPrcLvlValid() {
        
        if (isACACustomer() || isGOVCustomer() || isXSPCustomer()) {
            
            for (int i = 0; getEnrolledCtrctList() != null && i < getEnrolledCtrctList().size(); i++) {
                Contract ctrct = (Contract) getEnrolledCtrctList().get(i);
                if (StringUtils.equalsIgnoreCase(getTransSVPLevel(), ctrct.getTranPriceLevelCode()))
                    return true;
                if (StringUtils.equalsIgnoreCase(getTransSVPLevel(), ctrct.getVolDiscLevelCode()))
                    return true;
            }
            return false;
        }
        else {
            return true;
        }
    }
    
    public boolean isEnrolledCtrctNumValid() {
        
        if (isAddiSiteCustomer()) {
            
            for (int i = 0; getEnrolledCtrctList() != null && i < getEnrolledCtrctList().size(); i++) {
                Contract ctrct = (Contract) getEnrolledCtrctList().get(i);
                if (StringUtils.equalsIgnoreCase(getSapContractNum(), ctrct.getSapContractNum()))
                    return true;
            }
            return false;
        }
        else {
            return true;
        }
    }
    
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("address1 = ").append(address1).append("\n");                
        buffer.append("internalAddress = ").append(internalAddress).append("\n");
        buffer.append("city = ").append(city).append("\n");                        
        buffer.append("state = ").append(state).append("\n");     
        buffer.append("countryCode = ").append(countryCode).append("\n");          
        buffer.append("custName = ").append(custName).append("\n");                
        buffer.append("custNum= ").append(custNum).append("\n");                  
        buffer.append("ibmCustNum = ").append(ibmCustNum).append("\n");            
        buffer.append("postalCode = ").append(postalCode).append("\n");            
        buffer.append("sapRegionCodeDscr = ").append(sapRegionCodeDscr).append("\n");
        buffer.append("sapContractNum = ").append(sapContractNum).append("\n"); 
        buffer.append("webCustId = ").append(webCustId).append("\n");        
        buffer.append("tempAccessNum = ").append(tempAccessNum).append("\n");      
        buffer.append("webCustTypeCode = ").append(webCustTypeCode).append("\n");
        buffer.append("sapCntId = ").append(sapCntId).append("\n");          
        buffer.append("currencyCode = ").append(currencyCode).append("\n");        
        buffer.append("sapIntlPhoneNumFull = ").append(sapIntlPhoneNumFull).append("\n");
        buffer.append("custVatNum = ").append(custVatNum).append("\n");            
        buffer.append("sapWwIsuCode = ").append(sapWwIsuCode).append("\n");        
        buffer.append("empTot = ").append(empTot).append("\n");              
        buffer.append("isoLangCode = ").append(isoLangCode).append("\n");          
        buffer.append("upgrLangCode = ").append(upgrLangCode).append("\n");        
        buffer.append("cntFirstName = ").append(cntFirstName).append("\n");        
        buffer.append("cntLastName = ").append(cntLastName).append("\n");          
        buffer.append("cntPhoneNumFull = ").append(cntPhoneNumFull).append("\n");
        buffer.append("cntFaxNumFull = ").append(cntFaxNumFull).append("\n");      
        buffer.append("cntEmailAdr = ").append(cntEmailAdr).append("\n");          
        buffer.append("mktgEmailFlag = ").append(mktgEmailFlag).append("\n");      
        buffer.append("webCustStatCode = ").append(webCustStatCode).append("\n");
        buffer.append("sapRegionCode = ").append(sapRegionCode).append("\n");
        buffer.append("sapCntPrtnrFuncCode = ").append(sapCntPrtnrFuncCode).append("\n");
        buffer.append("gsaStatusFlag = ").append(gsaStatusFlag).append("\n");
        buffer.append("ctrctHasPreApprPrcLvlFlg = ").append(ctrctHasPreApprPrcLvlFlg).append("\n");
        buffer.append("purchOrdReqrdFlag = ").append(purchOrdReqrdFlag).append("\n");
        buffer.append("ContractList = ").append("\n");
        buffer.append("custDesignation = ").append(custDesignation).append("\n");
        buffer.append("PAIndCode = ").append(PAIndCode).append("\n");
        buffer.append("govEntityIndCode = ").append(govEntityIndCode).append("\n");
        buffer.append("govEntityIndCodeDesc = ").append(govEntityIndCodeDesc).append("\n");
        buffer.append("supprsPARegstrnEmailFlag = ").append(supprsPARegstrnEmailFlag).append("\n");
        for(int i = 0; contractList != null && i < contractList.size(); i ++){
            Contract contract = (Contract) contractList.get(i);
            buffer.append("contract" + i).append(contract.toString()).append("\n");
        }
        return buffer.toString();
    }
    /**
     * @return Returns the state.
     */
    public String getState() {
        return state;
    }
    /**
     * @return Returns the supprsPARegstrnEmailFlag.
     */
    public int getSupprsPARegstrnEmailFlag() {
        return supprsPARegstrnEmailFlag;
    }
    /**
     * @param supprsPARegstrnEmailFlag The supprsPARegstrnEmailFlag to set.
     */
    public void setSupprsPARegstrnEmailFlag(int supprsPARegstrnEmailFlag) {
        this.supprsPARegstrnEmailFlag = supprsPARegstrnEmailFlag;
    }
    /**
     * @return Returns the isCustOrdBlck.
     */
    public boolean isCustOrdBlcked() {
        return isCustOrdBlcked;
    }
    /**
     * @param isCustOrdBlck The isCustOrdBlck to set.
     */
    public void setCustOrdBlcked(boolean isCustOrdBlck) {
        this.isCustOrdBlcked = isCustOrdBlck;
    }
    /**
     * @return Returns the cvrageGrpCodeDesc.
     */
    public String getCvrageGrpCodeDesc() {
        return cvrageGrpCodeDesc;
    }
    /**
     * @param cvrageGrpCodeDesc The cvrageGrpCodeDesc to set.
     */
    public void setCvrageGrpCodeDesc(String cvrageGrpCodeDesc) {
        this.cvrageGrpCodeDesc = cvrageGrpCodeDesc;
    }

	public String getCustCvrageDesc() {
		return custCvrageDesc;
	}

	public void setCustCvrageDesc(String custCvrageDesc) {
		this.custCvrageDesc = custCvrageDesc;
	}

	public String getInitWebAppBusDscr() {
		return initWebAppBusDscr;
	}

	public void setInitWebAppBusDscr(String initWebAppBusDscr) {
		this.initWebAppBusDscr = initWebAppBusDscr;
	}

	public String getCurrWebAppBusDscr() {
		return currWebAppBusDscr;
	}

	public void setCurrWebAppBusDscr(String currWebAppBusDscr) {
		this.currWebAppBusDscr = currWebAppBusDscr;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
    public void setCustName2(String custName2) {
		this.custName2 = custName2;
	}
    
	public boolean isBPRelatedCust() {
		return isBPRelatedCust;
	}
    
	public void setBPRelatedCust(boolean isBPRelatedCust) {
		this.isBPRelatedCust = isBPRelatedCust;
	}

	public boolean isGOECust() {
		return isGOECust;
	}   
	
	
 }
