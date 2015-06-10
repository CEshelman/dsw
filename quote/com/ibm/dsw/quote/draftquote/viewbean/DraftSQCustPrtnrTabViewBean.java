package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>DraftSQCustPrtnrTabViewBean<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-3-15
 */

public class DraftSQCustPrtnrTabViewBean extends DraftSQBaseViewBean {
    
    CustPrtnrTabViewAdapter adapter = null;
    
    protected String fulfillmentSource = QuoteConstants.FULFILLMENT_DIRECT;
    protected boolean resellerToBeDetermined = false;
    protected boolean hasRelationshap = false;
    protected boolean isNewCustomer = true;
    protected boolean hasContactInfo = false;

	protected int distributorTBDFlag = 0;
    
    protected boolean isDisplayCustInfo = false;
    protected boolean isDisplayViewRptAndSSO = false;
    protected boolean isDisplayQuoteContact = false;
    protected boolean isDisplayAddiCntInfo = false;
    protected boolean isDisplayResellerInfo = false;
    protected boolean isDisplayResellerInfoLink = false;
    

	protected boolean isDisplayDistributorInfo = false;
    protected boolean isDisplayQuotePartners = false;
    protected boolean isDisplayAssgnCrtAgrmntBtn = false;
	protected boolean isDisplayAssgnCrtAgrmntBtnMsg = false;
    
    protected boolean isDisplayEndUserInfo = false;
    protected boolean isDisplayEndUserDetail = false;
    protected boolean isDisplayPODrivenInfo = false;
    private boolean isDisplayChangeCustBtn = true;
    private boolean isDisplayChangePayerBtn = true;
    private boolean isExistCustomer = false;
	
	/*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.common.bean.ModelCrawler#collectResults(com.ibm.ead4j.common.util.Parameters)
     */
    public void collectResults(Parameters params) throws ViewBeanException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.collectResults(params);

        if (quote == null || quote.getQuoteHeader() == null)
            return;
        
        QuoteHeader qh = quote.getQuoteHeader();
        Customer cust = quote.getCustomer();

        Partner reseller = quote.getReseller();
        Partner payer = quote.getPayer();
        Customer endUser = quote.getEndUser();
 
        if(quote != null && quote.getQuoteHeader() != null){
        	isNewCustomer = quote.getQuoteHeader().hasNewCustomer() && !quote.getQuoteHeader().hasExistingCustomer();
        	hasRelationshap = quote.getQuoteHeader().isBPRelatedCust();
        	List contactlist = quote.getContactList();
        	hasContactInfo = contactlist!=null&&contactlist.size()>0?true:false;
        }
        
        try {
            adapter = new CustPrtnrTabViewAdapter(quote, this.getDisplayTabAction(), this.getPostTabAction(), this.locale);
            if(!(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(quoteUserSession.getAudienceCode())&&!hasRelationshap)
            	||isNewCustomer
            ){
            	adapter.replicatePrimCntToQtCnt();	
            }
            
        
        } catch (IllegalStateException e) {
            logContext.error(this, e.getMessage());
            return;
        }

        // Display OEM/SSP section
        if (CustomerConstants.LOB_OEM.equalsIgnoreCase(lob)||QuoteConstants.LOB_SSP.equalsIgnoreCase(lob)) {
            isDisplayEndUserInfo = true;
	        if (endUser != null) {
	            isDisplayEndUserDetail = true;
	        }
        }
        
        if ((header.hasExistingCustomer() || header.hasNewCustomer()) && cust != null) {
            isDisplayCustInfo = true;
            isDisplayQuoteContact = true;
            
         
            if (header.hasExistingCustomer() && (CustomerConstants.LOB_PA.equalsIgnoreCase(lob) 
                    || CustomerConstants.LOB_PAE.equalsIgnoreCase(lob)
                    || CustomerConstants.LOB_OEM.equalsIgnoreCase(lob)
                    || CustomerConstants.LOB_SSP.equalsIgnoreCase(lob)
                    || CustomerConstants.LOB_FCT.equalsIgnoreCase(lob))) {
                isDisplayViewRptAndSSO = true;
                isDisplayAddiCntInfo = true;
            }
            
        }
        
        
        if(header.hasExistingCustomer() && cust != null)
        	isExistCustomer = true;
        
        if (CustomerConstants.LOB_PA.equalsIgnoreCase(lob) || CustomerConstants.LOB_PAE.equalsIgnoreCase(lob)
                || CustomerConstants.LOB_OEM.equalsIgnoreCase(lob)) {
            fulfillmentSource = StringUtils.trimToEmpty(qh.getFulfillmentSrc());
            resellerToBeDetermined = qh.isResellerToBeDtrmndFlag();
            distributorTBDFlag = qh.isSingleTierNoDistributorFlag() ? 2 : qh.isDistribtrToBeDtrmndFlag() ? 1 : 0;
            
            
            if (reseller != null){
            	isDisplayResellerInfo = true;
            
            }
            if (payer != null){
            	isDisplayDistributorInfo = true;
            }
        }
 
        if (CustomerConstants.LOB_FCT.equalsIgnoreCase(lob)) {

            if (payer != null) {
            	isDisplayDistributorInfo = true;
            	//We should not allow the payer to be changed when a charge agreement is referenced on a draft quote
            	if (StringUtils.isNotBlank(header.getRefDocNum())) {
            		isDisplayChangePayerBtn = false;
            		//If the bill-to and sold-to is the same then no payer info needs to be displayed
            		if (isDisplayCustInfo && header.getSoldToCustNum().equals(header.getPayerCustNum())){
            			isDisplayDistributorInfo = false;
            		}
            	}
            		
            }else if (StringUtils.isNotBlank(header.getRefDocNum())){
            	isDisplayChangePayerBtn = false;
            }
        }
        
        // if it is PA/PAE/PAUN quote, and customer has no contract
        // or customer has a contract assigned or created by user, show new contract button
        // if it is CSA quote, always display new contract button
        if((qh.isCSRAQuote() || qh.isCSTAQuote())&& qh.hasExistingCustomer()&& cust != null){
        	isDisplayAssgnCrtAgrmntBtn = true;
        }else{
			if ((qh.isPAQuote() || qh.isPAEQuote() || qh.isPAUNQuote()) && qh.hasExistingCustomer() && cust != null
					&& (!cust.isActivePACust() && !cust.isInactivePACust() || qh.isCreateCtrctFlag())) {
        		isDisplayAssgnCrtAgrmntBtn = true;
				if (qh.isPAQuote() && StringUtils.isEmpty(this.getAgreementNum()))
					isDisplayAssgnCrtAgrmntBtnMsg = true;

        	}
        }
      
        isDisplayPODrivenInfo = adapter.isPODrivenDisplay();
        
        //if quote created against a renewal quote,do not show search cust and create new cust buttons
        //We should not allow the customer to be changed when a charge agreement is referenced on a draft quote
        if(!StringUtils.isBlank(header.getRenwlQuoteNum()) || StringUtils.isNotBlank(header.getRefDocNum())) {
            if (!qh.isMigration() || StringUtils.isNotBlank(header.getRefDocNum())) {
                isDisplayChangeCustBtn = false;
            }
        }
        
    }

    public String getAddress1() {
        return adapter.getAddress1();
    }
    
    public String getAddress2() {
        return adapter.getAddress2();
    }

    public String getCustZip() {
        return adapter.getCustZip();
    }

    public String getAnniversary() {
        return adapter.getAnniversary();
    }

    public String getContractVariant() {
        return adapter.getContractVariant();
    }

    public String getDistributorAddress1() {
        return adapter.getDistributorAddress1();
    }
    
    public String getDistributorAddress2() {
        return adapter.getDistributorAddress2();
    }

    public String getDistributorCustName() {
        return adapter.getDistributorCustName();
    }

    public String getDistributorCustNum() {
        return adapter.getDistributorCustNum();
    }

    public String getFulfillmentSource() {
        return fulfillmentSource;
    }
    
    public int getFulfillChoice() {
        int choice = 0;
        if (quote.getQuoteHeader().isSalesQuote()) {
            if (quote.getQuoteHeader().getRenwlQuoteSpeclBidFlag() == 1) {
                // if created against a renewl quote, don't set default value
                if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(fulfillmentSource))
                    choice = 1;
                else if (QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(fulfillmentSource))
                    choice = 2;
            }
            else {
                // if not created against a renewl quote, the default value is DIRECT
                if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(fulfillmentSource))
                    choice = 1;
                else if (QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(fulfillmentSource))
                    choice = 2;
            }
        }
        else {
	        if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(fulfillmentSource))
	            choice = 1;
	        else if (QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(fulfillmentSource))
	            choice = 2;
        }
        return choice;
    }
    
    public int getDistributorTBDFlag() {
        return distributorTBDFlag;
    }
    
    public boolean isResellerToBeDetermined() {
        return resellerToBeDetermined;
    }
    
    public boolean isDisplayCustInfo() {
        return isDisplayCustInfo;
    }

    public boolean isHasRelationshap() {
		return hasRelationshap;
	}
    
    public boolean isNewCustomer() {
		return isNewCustomer;
	}

	public boolean isDisplayDistributorInfo() {
        return isDisplayDistributorInfo;
    }

    public boolean isDisplayQuoteContact() {
        return isDisplayQuoteContact;
    }

    public boolean isDisplayResellerInfo() {
        return isDisplayResellerInfo;
    }

    public boolean isDisplayViewRptAndSSO() {
        return isDisplayViewRptAndSSO;
    }
    
    public boolean isExistCustomer() {
        return isExistCustomer;
    }

    public boolean isPurchaseOrderDriven() {
        return adapter.isPurchaseOrderDriven();
    }

    public String getPrimCntctEmail() {
        return adapter.getPrimCntctEmail();
    }

    public String getPrimCntctFax() {
        return adapter.getPrimCntctFax();
    }

    public String getPrimCntctFirstName() {
        return adapter.getPrimCntctFirstName();
    }
    
    public String getPrimCntctLastName() {
        return adapter.getPrimCntctLastName();
    }

    public String getPrimCntctPhone() {
        return adapter.getPrimCntctPhone();
    }

    public String getQtCntctEmail() {
        return adapter.getQtCntctEmail();
    }

    public String getQtCntctFax() {
        return adapter.getQtCntctFax();
    }

    public String getQtCntctFirstName() {
        return adapter.getQtCntctFirstName();
    }
    
    public String getQtCntctLastName() {
        return adapter.getQtCntctLastName();
    }

    public String getQtCntctPhone() {
        return adapter.getQtCntctPhone();
    }

    public String getResellerAddress1() {
        return adapter.getResellerAddress1();
    }
    
    public String getResellerAddress2() {
        return adapter.getResellerAddress2();
    }

    public String getResellerCustName() {
        return adapter.getResellerCustName();
    }

    public String getResellerCustNum() {
        return adapter.getResellerCustNum();
    }
    
    public String getDistributorIbmCustNum() {
        return adapter.getDistributorIbmCustNum();
    }
    
    public String getDistributorRdcNum() {
        return adapter.getDistributorRdcNum();
    }

    public String getResellerIbmCustNum() {
        return adapter.getResellerIbmCustNum();
    }

    public String getResellerRdcNum() {
        return adapter.getResellerRdcNum();
    }

    public boolean isDisplayAddiCntInfo() {
        return isDisplayAddiCntInfo;
    }

    public String getCustCreateBtnParams() {
        return adapter.getCustCreateBtnParams();
    }
    
    public String getCustCreateCustBtnParams() {
        return adapter.getCustCreateCustBtnParams();
    }

    public String getCustSearchBtnParams() {
        return adapter.getCustSearchBtnParams();
    }

    public String getAddiCntInfoBtnParams() {
        return adapter.getAddiCntInfoBtnParams();
    }

    public String getPayerSearchBtnParams() {
        return adapter.getPayerSearchBtnParams();
    }
    
    public String getDistributorSearchBtnParams() {
        return adapter.getDistributorSearchBtnParams();
    }

    public String getResellerSearchBtnParams() {
        return adapter.getResellerSearchBtnParams();
    }

    public String getViewSWOnlineBtnParams() {
        return adapter.getViewSWOnlineBtnParams();
    }

    public String getViewCustRptBtnParams() {
        return adapter.getViewCustRptBtnParams();
    }

    public String getViewCustRptEnrollmentsURL() {
        return adapter.getViewCustRptEnrollmentsURL();
    }

    public String getViewCustRptCustomercontactsURL() {
        return adapter.getViewCustRptCustomercontactsURL();
    }

    public String getViewCustRptRnwlquotURL() {
        return adapter.getViewCustRptRnwlquotURL();
    }

    public String getViewCustRptLicenseURL() {
        return adapter.getViewCustRptLicenseURL();
    }

    public String getViewCustRptOrdhistURL() {
        return adapter.getViewCustRptOrdhistURL();
    }

    public String getViewCustRptHostedServicesURL() {
        return adapter.getViewCustRptHostedServicesURL();
    }
    
    public String getViewCustRptHostedServicesPGSURL() {
        return adapter.getViewCustRptHostedServicesPGSURL();
    }
    
    public String getViewInventoryDeployURL() {
        return adapter.getViewInventoryDeployURL();
    }
    
    public String getViewRenewalForecastURL() {
    	return adapter.getViewRenewalForecastURL();
    }
    
    public String getViewReinstateQuoteURL() {
   	 	return adapter.getViewReinstateQuoteURL();
   }
    
    public String getDisplayTabAction() {
        return DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB;
    }

    public String getPostTabAction() {
        return DraftQuoteActionKeys.POST_SQ_CUST_PRTNR_TAB;
    }
    
    public String getClearResellerBtnParams() {
        return adapter.getClearResellerBtnParams();
    }
    
    public String getClearDistributorBtnParams() {
        return adapter.getClearDistributorBtnParams();
    }
    
    public String getClearPayerBtnParams() {
        return adapter.getClearPayerBtnParams();
    }
    
    public String getClearEndUserBtnParams() {
        return adapter.getClearEndUserBtnParams();
    }
    
    public String getAssgnCrtAgrmntBtnParams() {
        return adapter.getAssgnCrtAgrmntBtnParams();
    }

    public boolean isDisplayQuotePartners() {
        return isDisplayQuotePartners;
    }

    public String getCustName2() {
        return adapter.getCustName2();
    }

    public String getCustName3() {
        return adapter.getCustName3();
    }
    
    public String getCustRdcNumList() {
        return adapter.getRdcNumList();
    }

    public String getResellerCntry() {
        return adapter.getResellerCntry();
    }

    public String getDistributorCntry() {
        return adapter.getDistributorCntry();
    }

    public String getCustCityStateZip() {
        return adapter.getCustCityStateZip();
    }

    public String getResellerCityStateZip() {
        return adapter.getResellerCityStateZip();
    }

    public String getDistributorCityStateZip() {
        return adapter.getDistributorCityStateZip();
    }
    
    public String getViewSWOnlineURL() {
        return adapter.getViewSWOnlineURL();
    }
    
    public String getResellerAuthPortfolioList() {
        return adapter.getResellerAuthPortfolioList();
    }

    public String getCustDesignation() {
        return adapter.getCustDesignation();
    }
    
    public boolean isDisplayUSFederalFlag() {
        return adapter.isDisplayUSFederalFlag();
    }
    
    public boolean isUSFederalCust() {
        return adapter.isUSFederalCust();
    }
    
    public boolean isDisplayAssgnCrtAgrmntBtn() {
        return isDisplayAssgnCrtAgrmntBtn;
    }

	public boolean isDisplayAssgnCrtAgrmntBtnMsg() {
		return isDisplayAssgnCrtAgrmntBtnMsg;
	}

    public boolean isDisplayEndUserInfo() {
        return isDisplayEndUserInfo;
    }
    public boolean isDisplayEndUserDetail() {
        return isDisplayEndUserDetail;
    }
    public String getEndUserName() {
        return adapter.getEndUserName();
    }  
    public String getEndUserName2() {
        return adapter.getEndUserName2();
    } 
    public String getEndUserName3() {
        return adapter.getEndUserName3();
    } 
    public String getEndUserCntry() {
        return adapter.getEndUserCntry();
    }
    public String getEndUserAddress1() {
        return adapter.getEndUserAddress1();
    }
    public String getEndUserAddress2() {
        return adapter.getEndUserAddress2();
    }
    public String getEndUserCityStateZip() {
        return adapter.getEndUserCityStateZip();
    }    
    public String getEndUserSiteNumber() {
        return adapter.getEndUserSiteNumber();
    }  
    public String getEndUserSearchBtnParams() {
        return adapter.getEndUserSearchBtnParams();
    }
    public boolean isPODrivenDisplay() {
        return isDisplayPODrivenInfo ;
    }
    public String getGovEntityIndCodeDesc() {
        return adapter.getGovEntityIndCodeDesc();
    }
    
    public boolean isCustOrdBlcked(){
        return adapter.isCustOrdBlcked();
    }
    
    public String getCvrageGrpCodeDesc() {
        return adapter.getCvrageGrpCodeDesc();
    }
    
    public boolean isDisplayChangeCustBtn() {
        return isDisplayChangeCustBtn;
    }
    
    public String getCustCvrageDesc() {
		return adapter.getCustCvrageDesc();
	}
    
    public String getInitWebAppBusDscr() {
		return adapter.getInitWebAppBusDscr();
	}

	public String getCurrWebAppBusDscr() {
		return adapter.getCurrWebAppBusDscr();
	}
	
	public boolean isDisplayChangePayerBtn() {
        return isDisplayChangePayerBtn;
    }
	
    public boolean isDisplayResellerInfoLink() {
		return isDisplayResellerInfoLink;
	}

	public boolean isHasContactInfo() {
		return hasContactInfo;
	}
	
	/**
	 * Get new ship to address url params from view bean.
	 * @return String
	 */
    public String getNewShipAddrBtnParams() {
        return adapter.getNewAddrBtnParams("0");
    }
    
	/**
	 * Get new install at address url params from view bean.
	 * @return String
	 */
    public String getNewInstallAddrBtnParams() {
        return adapter.getNewAddrBtnParams("1");
    }
    
    /**
     * Get the ship to address radio button checked status.
     * result: 0 or 1
     * @return int
     */
    public int getShipToChoice(){
    	return super.getQuote().getDefaultCustAddr().getShipToOption();
    }
    
    /**
     * Get the install at address radio button checked status.
     * result 0,1 or 2
     * @return int
     */
	public int getInstallAtChoice() {
		int installAtChoice = super.getQuote().getDefaultCustAddr().getInstallAtOption();
		if (this.getShipToChoice() != 0 && installAtChoice == 0)
			installAtChoice = 1;
		return installAtChoice;
	}
	
	public String getSoldToCustomerAddress(){
		return super.getSoldToCustomerAddress(adapter);
	}
	
	public String getSoldToCompanyAddress(){
		return super.getSoldToCompanyAddress(adapter);
	}
	
    public String getNewShipToCustomerAddress(){
    	return super.getNewShipToCustomerAddress(adapter);
    }
    
    public String getNewShipToCompanyAddress(){
    	return super.getNewShipToCompanyAddress(adapter);
    }
	
	public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append(adapter.toString());

        buffer.append("fulfillmentSource = ").append(fulfillmentSource).append("\n");
        buffer.append("isDisplayCustInfo = ").append(isDisplayCustInfo).append("\n");
        buffer.append("isDisplayAgreementInfo = ").append(isDisplayAgreementInfo).append("\n");
        buffer.append("isDisplayViewRptAndSSO = ").append(isDisplayViewRptAndSSO).append("\n");
        buffer.append("isDisplayQuoteContact = ").append(isDisplayQuoteContact).append("\n");
        buffer.append("isDisplayAddiCntInfo = ").append(isDisplayAddiCntInfo).append("\n");
        buffer.append("isDisplayResellerInfo = ").append(isDisplayResellerInfo).append("\n");
        buffer.append("isDisplayDistributorInfo = ").append(isDisplayDistributorInfo).append("\n");
        buffer.append("isDisplayNewCtrctBtn = ").append(isDisplayAssgnCrtAgrmntBtn).append("\n");
		buffer.append("isDisplayAssgnCrtAgrmntBtnMsg = ").append(isDisplayAssgnCrtAgrmntBtnMsg).append("\n");
        buffer.append("isDisplayPODrivenInfo = ").append(isDisplayPODrivenInfo).append("\n");
        buffer.append("isExistCustomer = ").append(isExistCustomer).append("\n");
        
        return buffer.toString();
    }
}
