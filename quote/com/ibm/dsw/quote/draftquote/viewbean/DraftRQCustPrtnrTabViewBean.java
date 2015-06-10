/*
 * Created on Apr 11, 2007
 */
package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.QuoteAccess;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author Lavanya
 */
public class DraftRQCustPrtnrTabViewBean extends DraftRQBaseViewBean {
    
    CustPrtnrTabViewAdapter adapter = null;
    
    protected int partnerAccess = -1;

    protected boolean isDisplayResellerSection = false;
    protected boolean isDisplayResellerInfo = false;
    protected boolean isDisplayChangeResellerBtn = false;
    protected boolean isDisplayChangeDistribBtn = false;
    protected boolean isDisplayPODrivenInfo = false;
    
    protected String fulfillmentSource = "";
    private boolean isExistCustomer = false;
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.common.bean.ModelCrawler#collectResults(com.ibm.ead4j.common.util.Parameters)
     */
    public void collectResults(Parameters params) throws ViewBeanException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.collectResults(params);

        if (renewalQuote == null || renewalQuote.getQuoteHeader() == null)
            return;
        
        QuoteHeader qh = renewalQuote.getQuoteHeader();
        Customer cust = renewalQuote.getCustomer();
        List cntList = renewalQuote.getContactList();
        Partner reseller = renewalQuote.getReseller();
        Partner payer = renewalQuote.getPayer();
        QuoteAccess quoteAccess = renewalQuote.getQuoteAccess();
        
        try {
            adapter = new CustPrtnrTabViewAdapter(renewalQuote, this.getDisplayTabAction(), this.getPostTabAction(), this.locale);
        } catch (IllegalStateException e) {
            logContext.error(this, e.getMessage());
            return;
        }
        
        // Get quot head info
        partnerAccess = qh.getRnwlPrtnrAccessFlag();
        
        fulfillmentSource = StringUtils.trimToEmpty(qh.getFulfillmentSrc());
        if (!QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(fulfillmentSource)
                && !QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(fulfillmentSource)
                && !QuoteConstants.FULFILLMENT_NOT_SPECIFIED.equalsIgnoreCase(fulfillmentSource)) {
            if (qh.getSpeclBidFlag() == 1)
                fulfillmentSource = QuoteConstants.FULFILLMENT_DIRECT;
            else
                fulfillmentSource = QuoteConstants.FULFILLMENT_NOT_SPECIFIED;
        }
        
        if ((header.hasExistingCustomer() || header.hasNewCustomer()) && cust != null)
        	isExistCustomer = true;

        // Get reseller information
        isDisplayResellerSection = (reseller != null || quoteAccess.isCanEditRQ());
        isDisplayResellerInfo = (reseller != null);
        isDisplayChangeResellerBtn = quoteAccess.isCanEditRQ();
        isDisplayChangeDistribBtn = quoteAccess.isCanEditRQ();
        isDisplayPODrivenInfo = adapter.isPODrivenDisplay();
    }
    
    public Collection genPartnerAccessOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String prntrAccess = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                DraftQuoteMessageKeys.SELECT_PARTNER_ACCESS);
        String prntrAccessYes = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                DraftQuoteMessageKeys.SELECT_PARTNER_ACCESS_YES);
        String prntrAccessNo = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                DraftQuoteMessageKeys.SELECT_PARTNER_ACCESS_NO);
        boolean bPrntrAccess = (partnerAccess != 0 && partnerAccess != 1);
        boolean bPrntrAccessYes = (partnerAccess == 1);
        boolean bPrntrAccessNo = (partnerAccess == 0);

        collection.add(new SelectOptionImpl(prntrAccess, "", bPrntrAccess));
        collection.add(new SelectOptionImpl(prntrAccessYes, String.valueOf(1), bPrntrAccessYes));
        collection.add(new SelectOptionImpl(prntrAccessNo, String.valueOf(0), bPrntrAccessNo));

        return collection;
    }
    
    public String getDisplayTabAction() {
        return DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB;
    }

    public String getPostTabAction() {
        return DraftQuoteActionKeys.POST_RQ_CUST_PRTNR_TAB;
    }
    /**
     * @return Returns the address.
     */
    public String getAddress1() {
        return adapter.getAddress1();
    }
    
    public String getAddress2() {
        return adapter.getAddress2();
    }

    /**
     * @return Returns the customer city, state, zip.
     */
    public String getCustZip() {
        return adapter.getCustZip();
    }
    
    /**
     * @return Returns the anniversary.
     */
    public String getAnniversary() {
        return adapter.getAnniversary();
    }

    /**
     * @return Returns the contractVariant.
     */
    public String getContractVariant() {
        return adapter.getContractVariant();
    }

    /**
     * @return Returns the distributorAddress.
     */
    public String getDistributorAddress1() {
        return adapter.getDistributorAddress1();
    }
    
    public String getDistributorAddress2() {
        return adapter.getDistributorAddress2();
    }

    /**
     * @return Returns the distributorCustName.
     */
    public String getDistributorCustName() {
        return adapter.getDistributorCustName();
    }

    /**
     * @return Returns the distributorCustNum.
     */
    public String getDistributorCustNum() {
        return adapter.getDistributorCustNum();
    }

    /**
     * @return Returns the isDisplayResellerInfo.
     */
    public boolean isDisplayResellerInfo() {
        return isDisplayResellerInfo;
    }

    /**
     * @return Returns the isDisplayChangeDistribBtn.
     */
    public boolean isDisplayChangeDistribBtn() {
        return isDisplayChangeDistribBtn;
    }
    /**
     * @return Returns the isDisplayChangeResellerBtn.
     */
    public boolean isDisplayChangeResellerBtn() {
        return isDisplayChangeResellerBtn;
    }
    /**
     * @return Returns the isPurchaseOrderDriven.
     */
    public boolean isPurchaseOrderDriven() {
        return adapter.isPurchaseOrderDriven();
    }

    /**
     * @return Returns the primCntctEmail.
     */
    public String getPrimCntctEmail() {
        return adapter.getPrimCntctEmail();
    }

    /**
     * @return Returns the primCntctFax.
     */
    public String getPrimCntctFax() {
        return adapter.getPrimCntctFax();
    }

    /**
     * @return Returns the primCntctName.
     */
    public String getPrimCntctFirstName() {
        return adapter.getPrimCntctFirstName();
    }
    
    public String getPrimCntctLastName() {
        return adapter.getPrimCntctLastName();
    }

    /**
     * @return Returns the primCntctPhone.
     */
    public String getPrimCntctPhone() {
        return adapter.getPrimCntctPhone();
    }

    /**
     * @return Returns the qtCntctEmail.
     */
    public String getQtCntctEmail() {
        return adapter.getQtCntctEmail();
    }

    /**
     * @return Returns the qtCntctFax.
     */
    public String getQtCntctFax() {
        return adapter.getQtCntctFax();
    }

    /**
     * @return Returns the qtCntctName.
     */
    public String getQtCntctFirstName() {
        return adapter.getQtCntctFirstName();
    }
    
    public String getQtCntctLastName() {
        return adapter.getQtCntctLastName();
    }

    /**
     * @return Returns the qtCntctPhone.
     */
    public String getQtCntctPhone() {
        return adapter.getQtCntctPhone();
    }

    /**
     * @return Returns the resellerAddress.
     */
    public String getResellerAddress1() {
        return adapter.getResellerAddress1();
    }
    
    public String getResellerAddress2() {
        return adapter.getResellerAddress2();
    }

    /**
     * @return Returns the resellerCustName.
     */
    public String getResellerCustName() {
        return adapter.getResellerCustName();
    }

    /**
     * @return Returns the resellerCustNum.
     */
    public String getResellerCustNum() {
        return adapter.getResellerCustNum();
    }

    /**
     * @return Returns the distributorSearchBtnParams.
     */
    public String getDistributorSearchBtnParams() {
        return adapter.getDistributorSearchBtnParams();
    }
    /**
     * @return Returns the resellerSearchBtnParams.
     */
    public String getResellerSearchBtnParams() {
        return adapter.getResellerSearchBtnParams();
    }

    /**
     * @return Returns the custName2.
     */
    public String getCustName2() {
        return adapter.getCustName2();
    }
    
    /**
     * @return Returns the custName3.
     */
    public String getCustName3() {
        return adapter.getCustName3();
    }

    /**
     * @return Returns the resellerCountry.
     */
    public String getResellerCntry() {
        return adapter.getResellerCntry();
    }

    /**
     * @return Returns the distributorCountry.
     */
    public String getDistributorCntry() {
        return adapter.getDistributorCntry();
    }

    /**
     * @return Returns the viewSWOnlineBtnParams.
     */
    public String getViewSWOnlineBtnParams() {
        return adapter.getViewSWOnlineBtnParams();
    }
    /**
     * @return Returns the viewCustRptBtnParams.
     */
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
    
    /**
     * @return Returns the viewAddtnlContactBtnParams.
     */
    public String getViewAddtnlContactBtnParams() {
        return adapter.getAddiCntInfoBtnParams();
    }
    /**
     * @return Returns the customer city, state, zip.
     */    
    public String getCustCityStateZip() {
        return adapter.getCustCityStateZip();
    }

    /**
     * @return Returns the reseller city, state, zip.
     */ 
    public String getResellerCityStateZip() {
        return adapter.getResellerCityStateZip();
    }

    /**
     * @return Returns the distributor city, state, zip.
     */ 
    public String getDistributorCityStateZip() {
        return adapter.getDistributorCityStateZip();
    }
    /**
     * @return Returns the fulfillmentSource.
     */
    public String getFulfillmentSource() {
        return fulfillmentSource;
    }
    /**
     * @return Returns the isDisplayResellerSection.
     */
    public boolean isDisplayResellerSection() {
        return isDisplayResellerSection;
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
    
    public boolean isPODrivenDisplay() {
        return isDisplayPODrivenInfo ;
    }
    
    public String getGovEntityIndCodeDesc() {
        return adapter.getGovEntityIndCodeDesc();
    }
    
    public String getCvrageGrpCodeDesc() {
        return adapter.getCvrageGrpCodeDesc();
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
	
	public String getViewInventoryDeployURL() {
        return adapter.getViewInventoryDeployURL();
    }
    
    public String getViewRenewalForecastURL() {
    	return adapter.getViewRenewalForecastURL();
    }
    
    public String getViewReinstateQuoteURL() {
   	 	return adapter.getViewReinstateQuoteURL();
   }
	
	
    
    public int getFulfillChoice() {
        int choice = header.getSpeclBidFlag() == 1 ? 2 : 0;
        
        if (QuoteConstants.FULFILLMENT_NOT_SPECIFIED.equalsIgnoreCase(fulfillmentSource))
            choice = 0;
        else if (QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(fulfillmentSource))
            choice = 1;
        else if (QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(fulfillmentSource))
            choice = 2;
        return choice;
    }
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append(adapter.toString());
        
        buffer.append("isDisplayAgreementInfo = ").append(isDisplayAgreementInfo).append("\n");
        buffer.append("isDisplayResellerInfo = ").append(isDisplayResellerInfo).append("\n");
        buffer.append("isDisplayChangeResellerBtn = ").append(isDisplayChangeResellerBtn).append("\n");
        buffer.append("isDisplayChangeDistribBtn = ").append(isDisplayChangeDistribBtn).append("\n");
        buffer.append("isDisplayPODrivenInfo = ").append(isDisplayPODrivenInfo).append("\n");
        return buffer.toString();
    }
  
    public boolean isCustOrdBlcked(){
        return adapter.isCustOrdBlcked();
    }
    
    public boolean isExistCustomer() {
        return isExistCustomer;
    }
}
