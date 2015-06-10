package com.ibm.dsw.quote.customer.viewbean;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.customer.config.CustomerActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
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
 * This <code>EndUserResultsViewBean<code> class.
 *    
 * @author: doris_yuen@us.ibm.com
 * 
 * Creation date: Dec. 15, 2009
 */

public class EndUserResultsViewBean extends BaseViewBean {

    // request parameters
    String lob = null;
    
    String displayLob = null;

    String country = null;
    
    String quoteNum = null;

    String countryDesc = null;

    String siteNumber = null;

    String customerName = null;

    String urlPrevious = null;

    String urlNext = null;

    String urlChangeSearchCriteria = null;

    transient List customers = null;

    int customerCount = 0;

    int startPos = 0;

    int endPos = 0;

    // display variables
    int customerSearchCriteria = 0;
    
    String tabParams = null;

    boolean isDisplaySiteNumber = false;

    boolean isDisplayCustomerName = false;

    boolean isDisplayPrevious = true;

    boolean isDisplayNext = true;

    boolean isDisplayCountry = false;

    String state = null;

    boolean isDisplayState = false;
    private String cusCountry;
    private String cusCountryDesc;

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ead4j.common.bean.ModelCrawler#collectResults(com.ibm.ead4j.common.util.Parameters)
     */
    public void collectResults(Parameters param) throws ViewBeanException {
        super.collectResults(param);

        lob = (String) param.getParameter(ParamKeys.PARAM_LINE_OF_BUSINESS);
        country = (String) param.getParameter(ParamKeys.PARAM_COUNTRY);
        quoteNum = (String) param.getParameter(ParamKeys.PARAM_QUOTE_NUM);
        countryDesc = ((Country) param.getParameter(ParamKeys.PARAM_COUNTRY_OBJECT)).getDesc();
        this.setCusCountry((String) param.getParameter(ParamKeys.CUSTOMER_COUNTRY));
        
        
        if (StringUtils.isNotBlank(countryDesc)){
            isDisplayCountry = true;
        }
        if(this.isSSPType() && StringUtils.isBlank(this.getCusCountry())){
        	isDisplayCountry = false;
        }
        
        if(this.isDisplayCountry()){
        	try {
            	if(!country.equals(this.getCusCountry())){
    				List countryList = CacheProcessFactory.singleton().create().getCountryList();
    				Iterator iter = countryList.iterator();
    				
    				while(iter.hasNext()){
    					Country cntry = (Country) iter.next();
    					String cntryCode = cntry.getCode3();
    					if(cntryCode.equals(this.getCusCountry())){
    						this.setCusCountryDesc(cntry.getDesc());
    						break;
    					}
    				}
    			}else{
    				this.setCusCountryDesc(countryDesc);
    			}
    			
    		} catch (QuoteException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
        customerSearchCriteria = ((Integer) param.getParameter(ParamKeys.PARAM_CUSTOMER_SEARCH_CRITERIA)).intValue();
        startPos = ((Integer) param.getParameter(ParamKeys.PARAM_START_POSITION)).intValue() + 1;
        CustomerSearchResultList resultList = (CustomerSearchResultList) param.getParameter(ParamKeys.PARAM_SIMPLE_OBJECT);
        
        if (resultList != null) {
            customers = resultList.getResultList();
            customerCount = resultList.getResultCount();
            displayLob = resultList.getLob();
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
        if(QuoteConstants.LOB_SSP.equals(lob)){
        	HtmlUtil.addURLParam(tabParam, ParamKeys.CUSTOMER_COUNTRY, cusCountry);
        }
        HtmlUtil.addURLParam(tabParam, ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
        HtmlUtil.addURLParam(tabParam, ParamKeys.PARAM_QUOTE_NUM, quoteNum);
        this.tabParams = tabParam.toString();

        urlChangeSearchCriteria = HtmlUtil.getURLForAction(CustomerActionKeys.DISPLAY_SEARCH_END_USER);
        StringBuffer url = new StringBuffer();
        HtmlUtil.addURLParam(url, ParamKeys.PARAM_COUNTRY, country);
        if(QuoteConstants.LOB_SSP.equals(lob)){
        	HtmlUtil.addURLParam(url, ParamKeys.CUSTOMER_COUNTRY, cusCountry);
        }
        HtmlUtil.addURLParam(url, ParamKeys.PARAM_LINE_OF_BUSINESS, lob);
        HtmlUtil.addURLParam(url, ParamKeys.PARAM_QUOTE_NUM, quoteNum);
        
        if (customerSearchCriteria == 0) {
            urlPrevious = HtmlUtil.getURLForAction(CustomerActionKeys.END_USER_SEARCH_DSWID);
            urlNext = HtmlUtil.getURLForAction(CustomerActionKeys.END_USER_SEARCH_DSWID);

            siteNumber = (String) param.getParameter(ParamKeys.PARAM_SITE_NUM);
            if (StringUtils.isNotBlank(siteNumber)) {
                isDisplaySiteNumber = true;
                HtmlUtil.addURLParam(url, ParamKeys.PARAM_SITE_NUM, siteNumber);
            }
        } else {
            urlPrevious = HtmlUtil.getURLForAction(CustomerActionKeys.END_USER_SEARCH_ATTR);
            urlNext = HtmlUtil.getURLForAction(CustomerActionKeys.END_USER_SEARCH_ATTR);

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
        }

        int prePos = startPos - CustomerConstants.PAGE_ROW_COUNT - 1;
        int nextPos = endPos;
        urlPrevious = urlPrevious + url.toString()
                + HtmlUtil.addURLParam(null, ParamKeys.PARAM_START_POSITION, String.valueOf(prePos)).toString();
        urlNext = urlNext + url.toString()
                + HtmlUtil.addURLParam(null, ParamKeys.PARAM_START_POSITION, String.valueOf(nextPos)).toString();
        urlChangeSearchCriteria += url.toString();
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
     * @return Returns the isDisplayCustomerName.
     */
    public boolean isDisplayCustomerName() {
        return isDisplayCustomerName;
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

            String acionURL = HtmlUtil.getURLForAction(CustomerActionKeys.SELECT_END_USER);
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

        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String secondActionKey = appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY);
        HtmlUtil.addURLParam(url, secondActionKey, DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB);
        
        return url.toString();
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

	public String getCusCountry() {
		return cusCountry;
	}

	public void setCusCountry(String cusCountry) {
		this.cusCountry = cusCountry;
	}

	public String getCusCountryDesc() {
		return cusCountryDesc;
	}

	public void setCusCountryDesc(String cusCountryDesc) {
		this.cusCountryDesc = cusCountryDesc;
	}
    
    public boolean isSSPType(){
    	return QuoteConstants.LOB_SSP.equals(lob);
    }
}
