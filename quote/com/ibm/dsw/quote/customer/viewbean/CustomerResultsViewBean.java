package com.ibm.dsw.quote.customer.viewbean;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.customer.config.CustomerActionKeys;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.partner.config.PartnerParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>CustomerResultsViewBean<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */

public class CustomerResultsViewBean extends BaseViewBean {

    // request parameters
    String lob = null;
    
    String displayLob = null;

    String country = null;
    
    String quoteNum = null;

    String countryDesc = null;

    String siteNumber = null;

    String agreementNumber = null;

    String customerName = null;

    String contractOption = null;
    
    transient CodeDescObj contractOptionObject = null;

    String anniversary = null;

    String urlPrevious = null;

    String urlNext = null;

    String urlChangeSearchCriteria = null;

    String findActiveCustsOnly = null;

    transient List customers = null;

    int customerCount = 0;

    int startPos = 0;

    int endPos = 0;

    // display variables
    int customerSearchCriteria = 0;
    
    String tabParams = null;

    boolean isDisplaySiteNumber = false;

    boolean isDisplayAgreementNumber = false;

    boolean isDisplayIBMCustomerNumber = false;

    boolean isDisplayCustomerName = false;

    boolean isDisplayContractOption = false;

    boolean isDisplayAnniversary = false;

    boolean isDisplayFindActiveCustsOnly = false;

    boolean isDisplayPrevious = true;

    boolean isDisplayNext = true;

    boolean isDisplayCountry = false;
    
    String searchFor = null;
    
    boolean isDisplayFindAllCntries = false;
    
    String findAllCntries = null;
    
    String state = null;
    
    String progMigrtnCode = null;
    
    boolean isDisplayState = false;
    
    boolean isDisplayUSFederalFlag = false;
    
    //for FCT TO PA USE
    private  String migrationReqNum = "";
    private  String pageFrom="";
    private boolean isPageFromFCT2PAMigrationReq = false;
    
    //for EC line flag in quote head
    boolean isECEligible = false;

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.common.bean.ModelCrawler#collectResults(com.ibm.ead4j.common.util.Parameters)
     */
    public void collectResults(Parameters param) throws ViewBeanException {
        super.collectResults(param);

        searchFor = (String) param.getParameter(CustomerParamKeys.SEARCH_FOR);
        if (!CustomerConstants.SEARCH_FOR_PAYER.equalsIgnoreCase(searchFor))
            searchFor = CustomerConstants.SEARCH_FOR_CUSTOMER;
        
        lob = (String) param.getParameter(ParamKeys.PARAM_LINE_OF_BUSINESS);
        country = (String) param.getParameter(ParamKeys.PARAM_COUNTRY);
        quoteNum = (String) param.getParameter(ParamKeys.PARAM_QUOTE_NUM);
        countryDesc = ((Country) param.getParameter(ParamKeys.PARAM_COUNTRY_OBJECT)).getDesc();
        progMigrtnCode = (String) param.getParameter(DraftQuoteParamKeys.PARAM_PROG_MIGRTN_CODE);
        
        if (StringUtils.isNotBlank(countryDesc))
            isDisplayCountry = true;
        
        customerSearchCriteria = ((Integer) param.getParameter(ParamKeys.PARAM_CUSTOMER_SEARCH_CRITERIA)).intValue();
        startPos = ((Integer) param.getParameter(ParamKeys.PARAM_START_POSITION)).intValue() + 1;
        CustomerSearchResultList resultList = (CustomerSearchResultList) param.getParameter(ParamKeys.PARAM_SIMPLE_OBJECT);
        
        if (resultList != null) {
            customers = resultList.getResultList();
            customerCount = resultList.getResultCount();
            displayLob = resultList.getLob();
            
            int addtionalCtrctCount = 0;
            for ( int i=0; i<customers.size(); i++ ) {
				Customer cust = (Customer)customers.get(i);
				List ctrctList = cust.getContractList();
				if(ctrctList != null && ctrctList.size() > 1)
					addtionalCtrctCount = addtionalCtrctCount + ctrctList.size() - 1;
            }

            if(addtionalCtrctCount > 0)
            	endPos = startPos + addtionalCtrctCount + customers.size() - 1;
            else
            	endPos = startPos + customers.size() - 1;
            
            if (customerCount == 0) {
                startPos = 0;
                endPos = 0;
            }
            isDisplayPrevious = (startPos > 1);
            isDisplayNext = (endPos < customerCount);
        }
        
        StringBuffer tabParam = new StringBuffer();
        HtmlUtil.addURLParam(tabParam, ParamKeys.PARAM_COUNTRY, country);
        HtmlUtil.addURLParam(tabParam, ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
        HtmlUtil.addURLParam(tabParam, ParamKeys.PARAM_QUOTE_NUM, quoteNum);
        HtmlUtil.addURLParam(tabParam, DraftQuoteParamKeys.PARAM_PROG_MIGRTN_CODE, progMigrtnCode);
        this.tabParams = tabParam.toString();
        
        //for FCT to PA migration 
        migrationReqNum = (String) param.getParameter(ParamKeys.PARAM_MIGRATION_REQSTD_NUM);
        pageFrom = param.getParameterAsString(DraftQuoteParamKeys.PAGE_FROM);
        
        urlChangeSearchCriteria = getChangeCriteriaBaseUrl();
        StringBuffer url = new StringBuffer();
        
    	if(getPageFrom().equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)){
	        HtmlUtil.addURLParam(url, ParamKeys.PARAM_COUNTRY, country);
	        HtmlUtil.addURLParam(url, ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
			HtmlUtil.addURLParam(url, ParamKeys.PARAM_MIGRATION_REQSTD_NUM, StringHelper.fillString(getMigrationReqNum()));
			HtmlUtil.addURLParam(url, DraftQuoteParamKeys.PAGE_FROM, StringHelper.fillString(getPageFrom()));
	  
    	}else{
	        HtmlUtil.addURLParam(url, ParamKeys.PARAM_COUNTRY, country);
	        HtmlUtil.addURLParam(url, ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
	        HtmlUtil.addURLParam(url, ParamKeys.PARAM_QUOTE_NUM, quoteNum);
	        HtmlUtil.addURLParam(url, CustomerParamKeys.SEARCH_FOR, searchFor);
	        HtmlUtil.addURLParam(url, DraftQuoteParamKeys.PARAM_PROG_MIGRTN_CODE, progMigrtnCode);
    	}
        if (customerSearchCriteria == 0) {
            urlPrevious = HtmlUtil.getURLForAction(CustomerActionKeys.CUSTOMER_SEARCH_DSWID);
            urlNext = HtmlUtil.getURLForAction(CustomerActionKeys.CUSTOMER_SEARCH_DSWID);

            siteNumber = (String) param.getParameter(ParamKeys.PARAM_SITE_NUM);
            if (StringUtils.isNotBlank(siteNumber)) {
                isDisplaySiteNumber = true;
                HtmlUtil.addURLParam(url, ParamKeys.PARAM_SITE_NUM, siteNumber);
            }
            agreementNumber = (String) param.getParameter(ParamKeys.PARAM_AGREEMENT_NUM);
            if (StringUtils.isNotBlank(agreementNumber)) {
                isDisplayAgreementNumber = true;
                HtmlUtil.addURLParam(url, ParamKeys.PARAM_AGREEMENT_NUM, agreementNumber);
            }
            findAllCntries = (String) param.getParameter(ParamKeys.PARAM_FIND_ALL_CNTRY_CUSTS);
            if (CustomerConstants.CHECKBOX_CHECKED.equals(findAllCntries)) {
                HtmlUtil.addURLParam(url, ParamKeys.PARAM_FIND_ALL_CNTRY_CUSTS, findAllCntries);
                isDisplayFindAllCntries = true;
            }
        } else {
            urlPrevious = HtmlUtil.getURLForAction(CustomerActionKeys.CUSTOMER_SEARCH_ATTR);
            urlNext = HtmlUtil.getURLForAction(CustomerActionKeys.CUSTOMER_SEARCH_ATTR);

            customerName = (String) param.getParameter(ParamKeys.PARAM_CUST_NAME);
            if (StringUtils.isNotBlank(customerName)) {
                isDisplayCustomerName = true;
                HtmlUtil.addURLParam(url, ParamKeys.PARAM_CUST_NAME, customerName);
            }
            state = (String) param.getParameter(ParamKeys.PARAM_STATE);
            if (StringUtils.isNotBlank(state)) {
                isDisplayState = true;
                HtmlUtil.addURLParam(url, ParamKeys.PARAM_STATE, state);
            }
            contractOption = (String) param.getParameter(ParamKeys.PARAM_CONTRACT_OPTION);
            contractOptionObject = (CodeDescObj) param.getParameter(ParamKeys.PARAM_CONTRACT_OPTION_OBJECT);
            if (StringUtils.isNotBlank(contractOption)) {
                isDisplayContractOption = true;
                HtmlUtil.addURLParam(url, ParamKeys.PARAM_CONTRACT_OPTION, contractOption);
            }
            anniversary = (String) param.getParameter(ParamKeys.PARAM_ANNIVERSARY);
            if (StringUtils.isNotBlank(anniversary)) {
                isDisplayAnniversary = true;
                HtmlUtil.addURLParam(url, ParamKeys.PARAM_ANNIVERSARY, anniversary);
            }
            findActiveCustsOnly = (String)param.getParameter(ParamKeys.PARAM_FIND_ACTIVE_CUSTS);
            HtmlUtil.addURLParam(url, ParamKeys.PARAM_FIND_ACTIVE_CUSTS, findActiveCustsOnly);
        }
    
        int prePos = startPos - CustomerConstants.PAGE_ROW_COUNT - 1;
        int nextPos = endPos;
        urlPrevious = urlPrevious + url.toString()
                + HtmlUtil.addURLParam(null, ParamKeys.PARAM_START_POSITION, String.valueOf(prePos)).toString();
        urlNext = urlNext + url.toString()
                + HtmlUtil.addURLParam(null, ParamKeys.PARAM_START_POSITION, String.valueOf(nextPos)).toString();
        urlChangeSearchCriteria += url.toString();
        
        isDisplayUSFederalFlag = ((QuoteConstants.LOB_PA.equalsIgnoreCase(lob)
                || QuoteConstants.LOB_PAE.equalsIgnoreCase(lob) || QuoteConstants.LOB_PAUN.equalsIgnoreCase(lob) || QuoteConstants.LOB_FCT
                .equalsIgnoreCase(lob)) && CustomerConstants.COUNTRY_USA.equalsIgnoreCase(country));
        
        
        if (getPageFrom().equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)) {
			isPageFromFCT2PAMigrationReq = true;
		}
        
        this.setECEligible((Boolean.parseBoolean(param.getParameter(ParamKeys.ECELIGIBLE_FLAG).toString())));
    }
    
    protected String getChangeCriteriaBaseUrl()	 {
    	return HtmlUtil.getURLForAction(CustomerActionKeys.DISPLAY_SEARCH_CUSTOMER);
    }
    
    private String parseAnniversary(String anniversary) {

        String[] months = { MessageKeys.LABEL_ALL, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL,
                MessageKeys.MONTH_AUG, MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV,
                MessageKeys.MONTH_DEC };
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        LogContext logContext = LogContextFactory.singleton().getLogContext();

        if (StringUtils.isBlank(anniversary))
            return "";
        int anni = 0;
        try {
            anni = Integer.parseInt(anniversary);
        } catch (NumberFormatException e) {
            logContext.error(this, e.getMessage());
            return "";
        }
        if (anni >= 0 && anni <= 12)
            return context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, months[anni]);
        else
            return "";
    }

    /**
     * @return Returns the agreementNumber.
     */
    public String getAgreementNumber() {
        return agreementNumber;
    }

    /**
     * @return Returns the anniversary.
     */
    public String getAnniversary() {
        return parseAnniversary(anniversary);
    }

    /**
     * @return Returns the contractOption.
     */
    public String getContractOption() {
        if ("%".equals(contractOption)){
            return "ALL";
        } else if(contractOptionObject !=null){
            return contractOptionObject.getCodeDesc();
        } else {
            return contractOption;
        }
    }
    
    public String getPriceLevelDesc(String prcLvlCode) {
        return PartPriceConfigFactory.singleton().getPriceLevelDesc(prcLvlCode);
    }

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return Returns the customerCount.
     */
    public int getCustomerCount() {
        return customerCount;
    }

    /**
     * @return Returns the customerName.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @return Returns the customers.
     */
    public List getCustomers() {
        return customers;
    }

    /**
     * @return Returns the customerSearchCriteria.
     */
    public int getCustomerSearchCriteria() {
        return customerSearchCriteria;
    }

    /**
     * @return Returns the isDisplayAgreementNumber.
     */
    public boolean isDisplayAgreementNumber() {
        return isDisplayAgreementNumber;
    }

    /**
     * @return Returns the isDisplayAnniversary.
     */
    public boolean isDisplayAnniversary() {
        boolean isPAE = ((QuoteConstants.LOB_PA.equalsIgnoreCase(lob) || QuoteConstants.LOB_PAE.equalsIgnoreCase(lob) 
                || QuoteConstants.LOB_PAUN.equalsIgnoreCase(lob))
                && CustomerConstants.ACTIVE_PAE_CUSTS.equalsIgnoreCase(findActiveCustsOnly));
        return (isDisplayAnniversary && !isPAE);
    }

    /**
     * @return Returns the isDisplayContractOption.
     */
    public boolean isDisplayContractOption() {
        boolean isPAE = ((QuoteConstants.LOB_PA.equalsIgnoreCase(lob) || QuoteConstants.LOB_PAE.equalsIgnoreCase(lob) 
                || QuoteConstants.LOB_PAUN.equalsIgnoreCase(lob))
                && CustomerConstants.ACTIVE_PAE_CUSTS.equalsIgnoreCase(findActiveCustsOnly));
        return (isDisplayContractOption && !isPAE);
    }

    /**
     * @return Returns the isDisplayCustomerName.
     */
    public boolean isDisplayCustomerName() {
        return isDisplayCustomerName;
    }

    /**
     * @return Returns the isDisplayFindActiveCustsOnly.
     */
    public boolean isDisplayFindActiveCustsOnly() {
        return isDisplayFindActiveCustsOnly;
    }

    /**
     * @return Returns the isDisplayIBMCustomerNubmer.
     */
    public boolean isDisplayIBMCustomerNumber() {
        return isDisplayIBMCustomerNumber;
    }

    /**
     * @return Returns the isDisplaySiteNumber.
     */
    public boolean isDisplaySiteNumber() {
        return isDisplaySiteNumber;
    }

    /**
     * @return Returns the lob.
     */
    public String getLob() {
        return lob;
    }

    /**
     * @return Returns the siteNumber.
     */
    public String getSiteNumber() {
        return siteNumber;
    }

    /**
     * @return Returns the endPos.
     */
    public int getEndPos() {
        return endPos;
    }

    /**
     * @return Returns the isDisplayNext.
     */
    public boolean isDisplayNext() {
        return isDisplayNext;
    }

    /**
     * @return Returns the isDisplayPrevious.
     */
    public boolean isDisplayPrevious() {
        return isDisplayPrevious;
    }

    /**
     * @return Returns the startPos.
     */
    public int getStartPos() {
        return startPos;
    }

    /**
     * @return Returns the urlNext.
     */
    public String getUrlNext() {
        return urlNext;
    }

    /**
     * @return Returns the urlPrevious.
     */
    public String getUrlPrevious() {
        return urlPrevious;
    }

    /**
     * @return Returns the quoteNum.
     */
    public String getQuoteNum() {
        return quoteNum;
    }

    /**
     * @return Returns the tabParams.
     */
    public String getTabParams() {
        return tabParams;
    }
    
    public String getSelectCustURL(String customerNum, String contractNum, String currency) {
        StringBuffer url = new StringBuffer();
        if (this.isSearchForPayer()) {
            String acionURL = HtmlUtil.getURLForAction(CustomerActionKeys.SELECT_PAYER);
            url.append(acionURL);
            HtmlUtil.addURLParam(url, ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
            HtmlUtil.addURLParam(url, PartnerParamKeys.PARAM_PARTNER_NUM, StringHelper.fillString(customerNum));
            HtmlUtil.addURLParam(url, PartnerParamKeys.PARAM_PARTNER_TYPE, "3");
            HtmlUtil.addURLParam(url, DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM, this.quoteNum);
            HtmlUtil.addURLParam(url, ParamKeys.PARAM_IS_SBMT_QT, "false");
        } 
        else {
        	if(getPageFrom().equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)){
        	       String acionURL = HtmlUtil.getURLForAction(CustomerActionKeys.SELECT_CUSTOMER);
                   url.append(acionURL);
                   
                   if (StringUtils.isNotBlank(customerNum)) {
                       HtmlUtil.addURLParam(url, ParamKeys.PARAM_SITE_NUM, StringHelper.fillString(customerNum));
                   }
                   if (StringUtils.isNotBlank(contractNum)) {
                       HtmlUtil.addURLParam(url, ParamKeys.PARAM_AGREEMENT_NUM, StringHelper.fillString(contractNum));
                   }
             
                   if (StringUtils.isNotBlank(getMigrationReqNum())) {
                       HtmlUtil.addURLParam(url, ParamKeys.PARAM_MIGRATION_REQSTD_NUM, StringHelper.fillString(getMigrationReqNum()));
                   }
                   if (StringUtils.isNotBlank(getPageFrom())) {
                       HtmlUtil.addURLParam(url, DraftQuoteParamKeys.PAGE_FROM, StringHelper.fillString(getPageFrom()));
                   }
        	}else{
            String acionURL = HtmlUtil.getURLForAction(CustomerActionKeys.SELECT_CUSTOMER);
            url.append(acionURL);
            if (StringUtils.isNotBlank(customerNum)) {
                HtmlUtil.addURLParam(url, ParamKeys.PARAM_SITE_NUM, StringHelper.fillString(customerNum));
            }
            if (StringUtils.isNotBlank(contractNum)) {
                HtmlUtil.addURLParam(url, ParamKeys.PARAM_AGREEMENT_NUM, StringHelper.fillString(contractNum));
            }
            if (StringUtils.isNotBlank(currency)) {
                HtmlUtil.addURLParam(url, ParamKeys.PARAM_CURRENCY, currency);
            }
        	}
        }
        
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String secondActionKey = appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY);
        
        if(getPageFrom().equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)){
        	HtmlUtil.addURLParam(url, secondActionKey, DraftQuoteActionKeys.DISPLAY_FCT2PA_CUST_PARTNER);
        }else{
        	HtmlUtil.addURLParam(url, secondActionKey, DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB);
        }
        return url.toString();
    }
    
    public String getFormattedRdcNumbers(List rdcList) {
        StringBuffer sb = new StringBuffer();
        if (rdcList != null) {
            for (int i = 0; i < rdcList.size(); i++) {
                if (i == 0)
                    sb.append((String) rdcList.get(i));
                else
                    sb.append("<br />"+(String) rdcList.get(i));
            }
        }
        return sb.toString();
    }

    public String getChangeCriteriaURL() {
        return urlChangeSearchCriteria;
    }

    /**
     * @return Returns the isDisplayCountry.
     */
    public boolean isDisplayCountry() {
        return isDisplayCountry;
    }

    /**
     * @return Returns the countryDesc.
     */
    public String getCountryDesc() {
        return countryDesc;
    }
    /**
     * @return Returns the searchFor.
     */
    public String getSearchFor() {
        return searchFor;
    }
    
    public boolean isSearchForPayer() {
        return CustomerConstants.SEARCH_FOR_PAYER.equalsIgnoreCase(searchFor);
    }
    
    public boolean isPA() {
        return ((QuoteConstants.LOB_PA.equalsIgnoreCase(lob) || QuoteConstants.LOB_PAE.equalsIgnoreCase(lob) 
                || QuoteConstants.LOB_PAUN.equalsIgnoreCase(lob))
                && (QuoteConstants.LOB_PA.equals(displayLob)
                        || CustomerConstants.ACTIVE_PA_CUSTS.equalsIgnoreCase(findActiveCustsOnly)));
    }
    
    public boolean isCSA() {
        return ((QuoteConstants.LOB_PA.equalsIgnoreCase(lob) || QuoteConstants.LOB_PAE.equalsIgnoreCase(lob) 
                || QuoteConstants.LOB_PAUN.equalsIgnoreCase(lob) || QuoteConstants.LOB_CSA.equalsIgnoreCase(lob) )
                && (QuoteConstants.LOB_CSA.equals(displayLob)
                        || CustomerConstants.ACTIVE_CSA_CUSTS.equalsIgnoreCase(findActiveCustsOnly)))
                        && !StringUtils.equals(contractOption, QuoteConstants.LOB_CSTA);
    }
    
    public boolean isPAE() {
        return ((QuoteConstants.LOB_PA.equalsIgnoreCase(lob) || QuoteConstants.LOB_PAE.equalsIgnoreCase(lob) 
                || QuoteConstants.LOB_PAUN.equalsIgnoreCase(lob))
                && (QuoteConstants.LOB_PAE.equals(displayLob) 
                        || CustomerConstants.ACTIVE_PAE_CUSTS.equalsIgnoreCase(findActiveCustsOnly)));
    }
    
    public boolean isPPSS() {
        return QuoteConstants.LOB_PPSS.equalsIgnoreCase(lob);
    }
    
    public boolean isFCT() {
        return QuoteConstants.LOB_FCT.equalsIgnoreCase(lob);
    }
    
    public boolean isOEM() {
        return QuoteConstants.LOB_OEM.equalsIgnoreCase(lob);
    }
    /**
     * @return Returns the isDisplayFindAllCntries.
     */
    public boolean isDisplayFindAllCntries() {
        return isDisplayFindAllCntries;
    }
    /**
     * @return Returns the isDisplayState.
     */
    public boolean isDisplayState() {
        return isDisplayState;
    }
    /**
     * @return Returns the state.
     */
    public String getState() {
        return state;
    }
    /**
     * @return Returns the findActiveCustsOnly.
     */
    public String getFindActiveCustsOnly() {
        return findActiveCustsOnly;
    }
    /**
     * @return Returns the findAllCntries.
     */
    public String getFindAllCntries() {
        return findAllCntries;
    }
    
    public boolean isDisplayUSFederalFlag() {
        return isDisplayUSFederalFlag;
    }
    
    public Country getCntryObject(String cntryCode3) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        CacheProcess cProcess = null;
        Country cntryObj = null;
        try {
            cProcess = CacheProcessFactory.singleton().create();
            cntryObj = cProcess.getCountryByCode3(cntryCode3);
            return cntryObj;
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
            return null;
        }
    }
    
    public String getCustReportURL(String siteNumber, String agreementNum) {
        StringBuffer url = new StringBuffer();        
        String actionURL = HtmlUtil.getURLForReporting(DraftQuoteActionKeys.DISPLAY_CUST_DTL_ENROLL_RPT);
        url.append(actionURL);

        HtmlUtil.addURLParam(url, DraftQuoteParamKeys.RPT_SAP_CUST_NUM, siteNumber);
        HtmlUtil.addURLParam(url, DraftQuoteParamKeys.RPT_SAP_CTRCT_NUM, agreementNum);
        
        String reportURL = "javascript:popup('" + url.toString() + "','internal',600,600)"; 

        return reportURL;        
    }
    
    public boolean isDisplayCreateCustTab() {
        return !lob.equals(CustomerConstants.LOB_OEM) && !isSearchForPayer()&& !isPageFromFCT2PAMigrationReq() && !QuoteConstants.LOB_SSP.equalsIgnoreCase(lob);
    }
    
    public boolean isDisplayHistoryLink() {
        return lob.equals(CustomerConstants.LOB_PA)||lob.equals(CustomerConstants.LOB_PAE)||lob.equals(CustomerConstants.LOB_PAUN);
        }
    
	public String getMigrationReqNum() {
		return (migrationReqNum == null ? "" : migrationReqNum);
	}

	public void setMigrationReqNum(String migrationReqNum) {
		this.migrationReqNum = migrationReqNum;
	}

	public String getPageFrom() {
		return (pageFrom == null ? "" : pageFrom);
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}
	
    public boolean isPageFromFCT2PAMigrationReq() {
		return isPageFromFCT2PAMigrationReq;
	}

	public void setPageFromFCT2PAMigrationReq(boolean isPageFromFCT2PAMigrationReq) {
		this.isPageFromFCT2PAMigrationReq = isPageFromFCT2PAMigrationReq;
	}
	
	public String getRtnToFCT2PACustPartnerURL() {
		String returnToFCT2PACustPartnerURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_FCT2PA_CUST_PARTNER);
		StringBuffer url = new StringBuffer();
		HtmlUtil.addURLParam(url, ParamKeys.PARAM_MIGRATION_REQSTD_NUM, StringHelper.fillString(getMigrationReqNum()));
		HtmlUtil.addURLParam(url, DraftQuoteParamKeys.PAGE_FROM, StringHelper.fillString(getPageFrom()));
		returnToFCT2PACustPartnerURL+=url.toString();
		return returnToFCT2PACustPartnerURL;
	}
	
	public boolean isSSPType(){
		return QuoteConstants.LOB_SSP.equalsIgnoreCase(lob);
	}

	public boolean isECEligible() {
		return isECEligible;
	}

	public void setECEligible(boolean isECEligible) {
		this.isECEligible = isECEligible;
	}
    
}
