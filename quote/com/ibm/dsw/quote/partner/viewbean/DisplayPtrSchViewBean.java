package com.ibm.dsw.quote.partner.viewbean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.dsw.quote.partner.config.PartnerMessageKeys;
import com.ibm.dsw.quote.partner.config.PartnerParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.taglib.html.SelectOption;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayPtrSchViewBean</code> class is to support display partner
 * search page
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Mar 15, 2007
 */
public class DisplayPtrSchViewBean extends PartnerViewBean {
    private transient List countryList;

    private transient List countryOptionList;

    private transient List stateOptionList;
    
    private transient List authPortfolioList;
    private transient List authPortfolioOptionList;
    
    private Boolean hasCtrldPart = null;

    private transient LogContext logger = LogContextFactory.singleton().getLogContext();
    
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        
        hasCtrldPart = (Boolean) params.getParameter(SubmittedQuoteParamKeys.PARAM_HAS_CTRLD_PART);
        countryList = (List) params.getParameter(NewQuoteParamKeys.PARAM_COUNTRY_LIST);
        countryOptionList = new ArrayList();
        
        for (Iterator iter = countryList.iterator(); iter.hasNext();) {
            Country cry = (Country) iter.next();
            SelectOption so = new SelectOptionImpl(cry.getDesc(), cry.getCode3(), cry.getCode3().equals(StringUtils.isBlank(getCountry())?getCutCountry():getCountry()));
            countryOptionList.add(so);
        }

        stateOptionList = new ArrayList();

        List stateList = (List) params.getParameter(PartnerParamKeys.PARAM_STATE_LIST);
        if (stateList.size() > 0) {
            String selectOne = getI18NString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.SELECT_ONE);
            stateOptionList.add(new SelectOptionImpl(selectOne, "", false));
            for (Iterator iter = stateList.iterator(); iter.hasNext();) {
                CodeDescObj cdo = (CodeDescObj) iter.next();
                SelectOption so = new SelectOptionImpl(cdo.getCodeDesc(), cdo.getCode(), cdo.getCode().equals(
                        getState()));
                stateOptionList.add(so);
            }
        } else {
            String notAvailable = getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                    PartnerMessageKeys.MSG_NOT_AVAILABLE);
            stateOptionList.add(new SelectOptionImpl(notAvailable, "", false));
        }

        if (this.isPA() || this.isPAE()) {
	        
	        authPortfolioList = (List) params.getParameter(PartnerParamKeys.PARAM_AUTHORIZED_PORTFOLIO_LIST);
	        String currentAuthPortfolio = getAuthorizedPort();

	        if (authPortfolioList != null ) {
	            
		        authPortfolioOptionList = new ArrayList();
		        String[] currentAuthPortLists = new String[0];;
		        
	            if (StringUtils.isNotBlank(currentAuthPortfolio)) {
	                currentAuthPortLists = currentAuthPortfolio.split(",");
	            }

	            boolean selectAllFlag = false;
	            for (int i = 0; i < currentAuthPortLists.length; i++) {
	                if ((currentAuthPortLists[i]).equals("%")) {
	                    selectAllFlag = true;
	                    break;
	                }
	            }
	            
	            authPortfolioOptionList.add(new SelectOptionImpl(getSelectAllText(), FindQuoteDBConstants.DB2_UNKNOWN_SIGN, selectAllFlag));
	            
	            for (Iterator iter = authPortfolioList.iterator(); iter.hasNext();) {
	                CodeDescObj cdo = (CodeDescObj) iter.next();
	                boolean defaultFlag = false;
	                
	                if (currentAuthPortLists != null && currentAuthPortLists.length > 0 ){
	                	for (int i = 0; i < currentAuthPortLists.length; ++i){
                            if ( cdo.getCode().equalsIgnoreCase(currentAuthPortLists[i]) ) {
                                defaultFlag = true;
                                break;
                            }
	                	}//End for loop ==> currentAuthPortLists
	                } 
	                
	                SelectOption so = new SelectOptionImpl(cdo.getCodeDesc(), cdo.getCode(), defaultFlag);
	                authPortfolioOptionList.add(so);
	            } //End for loop ==> authPortfolioList
	            
	        }            
            
        }//End if isPA || isPAE
    }

    /**
     * @return Returns the countryList.
     */
    public List getCountryList() {
        return countryList;
    }

    /**
     * @return Returns the countryOptionList.
     */
    public List getCountryOptionList() {
        return countryOptionList;
    }

    public String getMsgFindResellerBySiteNum() {
        return PartnerMessageKeys.MSG_FIND_RESELLER_BY_SITE_NUM;
    }

    public String getMsgFindDistributorBySiteNum() {
        return PartnerMessageKeys.MSG_FIND_DISTRIBUTOR_BY_SITE_NUM;
    }

    public String getMsgDistributorAttrSelection() {
        return PartnerMessageKeys.MSG_DISTRIBUTOR_ATTRIBUTE_SELECTION;
    }

    public String getMsgResellerAttrSelection() {
        return PartnerMessageKeys.MSG_RESELLER_ATTRIBUTE_SELECTION;
    }

    public String getMsgTier1Reseller() {
        return PartnerMessageKeys.MSG_TIER1_RESELLER;
    }

    public String getMsgTier2Reseller() {
        return PartnerMessageKeys.MSG_TIER2_RESELLER;
    }

    public String getPkCountry() {
        return PartnerParamKeys.PARAM_COUNTRY;
    }

    public String getPkResellerType() {
        return PartnerParamKeys.PARAM_RESELLER_TYPE;
    }

    public String getPkSearchMethod() {
        //        "Pk" means param key
        return PartnerParamKeys.PAREAM_SEARCH_METHOD;
    }

    public String getPkSiteName() {
        return PartnerParamKeys.PARAM_SITE_NAME;
    }

    public String getPkSiteNum() {
        return PartnerParamKeys.PARAM_SITE_NUM;
    }

    public String getPkState() {
        return PartnerParamKeys.PARAM_STATE;
    }

    public String getPkTier1Reseller() {
        return PartnerParamKeys.PARAM_TIER1_RESELLER;
    }

    public String getPkTier2Reseller() {
        return PartnerParamKeys.PARAM_TIER2_RESELLER;
    }
    
    public String getPkHasCtrldPart() {
        return SubmittedQuoteParamKeys.PARAM_HAS_CTRLD_PART;
    }

    public String getMsgResellerSearchTitle() {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        return getI18NString(appContext.getConfigParameter(FrameworkKeys.JADE_I18N_BASENAME_KEY), locale,
                PartnerMessageKeys.MSG_RESELLER_SEARCH_TITLE);
    }

    public String getMsgDistributorSearchTitle() {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        return getI18NString(appContext.getConfigParameter(FrameworkKeys.JADE_I18N_BASENAME_KEY), locale,
                PartnerMessageKeys.MSG_DISTRIBUTOR_SEARCH_TITLE);
    }

    /**
     * @return Returns the stateOptionList.
     */
    public List getStateOptionList() {
        return stateOptionList;
    }

    /**
     * @return Returns the distributor_name_text.
     */
    public String getMsgDistributorName() {
        return PartnerMessageKeys.MSG_DISTRIBUTOR_NAME;
    }
    
    /**
     * @return Returns the reseller_name_text.
     */
    public String getMsgResellerName() {
        return PartnerMessageKeys.MSG_RESELLER_NAME;
    }
    
    /**
     * @return Returns the find_reseller_by_port text.
     */
    public String getMsgFindResellerByPort() {
        return PartnerMessageKeys.MSG_FIND_RESELLER_BY_PORTFOLIO;
    }

    public String getPkAuthorizedPort() {
        return PartnerParamKeys.PARAM_AUTHORIZED_PORT;
    }
    
    /**
     * @return Returns the authPortfolioList.
     */
    public List getAuthPortfolioList() {
        return authPortfolioList;
    }

    /**
     * @return Returns the authPortfolioOptionList.
     */
    public List getAuthPortfolioOptionList() {
        return authPortfolioOptionList;
    }

    /** 
     * @return true if the line of business is PA
     */
    public boolean isPA() {
        return QuoteConstants.LOB_PA.equalsIgnoreCase(getLobCode());
    }

    /**
     * @return true if the line of business is PAE
     */
    public boolean isPAE() {
        return QuoteConstants.LOB_PAE.equalsIgnoreCase(getLobCode());
    }
    
    public boolean isDisplaySearchByPort() {
        return ((isPA() || isPAE()) && !"true".equalsIgnoreCase(isSubmittedQuote));
    }

    /** 
     * @return true if the line of business is FCT
     */
    public boolean isFCT() {
        return QuoteConstants.LOB_FCT.equalsIgnoreCase(getLobCode());
    }
    
    public String getHasCtrldPart() {
        if (hasCtrldPart != null && hasCtrldPart.booleanValue())
            return "true";
        else
            return "false";
    }

    /** 
     * @return true if the line of business is OEM
     */
    public boolean isOEM() {
        return QuoteConstants.LOB_OEM.equalsIgnoreCase(getLobCode());
    }
    
    public String getChkMultipleProdParam() {
        return PartnerParamKeys.PARAM_MULTIPLE_PROD_CHK;
    }
    public String getMsgMultipleProdChk() {
        return PartnerMessageKeys.MSG_MULTIPLE_PROD_CHK;
    }
}
