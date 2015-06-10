package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.dlgtn.config.DlgtnActionKeys;
import com.ibm.dsw.quote.dlgtn.config.DlgtnParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.promotion.config.PromotionKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>DraftSQSalesInfoTabViewBean<code> class.
 *
 * @author: zhaoxw@cn.ibm.com
 *
 * Creation date: Mar 26, 2007
 */

public class DraftSQSalesInfoTabViewBean extends DraftSQBaseViewBean {
    private Quote quote;

    transient List businessOrganization;
    transient List quoteDelegates;
    transient List quotePromotions;
    String deafultBusinessOrg;
    String boDefaultOption;
	String qcFullName = "";
    String qcPhone = "";
    String qcEmail = "";
    String qcCompany = "";
    String qcCountry = "";
    String ooFullName = "";
    String ooPhone = "";
    String ooEmail = "";
    String ooCountry = "";
    String busOrgCode = "";
    String changeOppOwnerURL;
    String removeQuoteEditorURL;
    String addQuoteEditorURL;

    // Get default values from quote header
    String oppNumber = "";
    String quoteTitle = "";
    String quoteDesc = "";
    String oppOwnerEmail = "";
    String selectedExemptnCode = "";

    boolean isDisplayQuoteCreator = false;
    boolean isDisplayOppOwner = false;
    boolean isDisplayQuoteEditors = false;
    boolean isDisplayQuotePromotions = false;
    boolean isDisplayOppNumInput = false;

    // default exemption code,not user select
    String defaultExemptionCode = "";
 // user select,if the exemption code is not 80, the code is the same with default exemption code
    String exemptionCode = "";
    // default exemption code i18n key
    String exemptionCodeTextKey = "";
    
    boolean isDsjFlag=false;

    public void collectResults(Parameters params) throws ViewBeanException {

        LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.collectResults(params);

        quote = getQuote();
        QuoteHeader qh = quote.getQuoteHeader();

        if (quote != null) {

            try {
                //Opportunity information section
                //Quote editor and Opportunity owner
                CacheProcess process = CacheProcessFactory.singleton().create();

	            // Set quote creator attributes
	            if (quote.getCreator() != null) {
	                checkSalesRepInfo(quote.getCreator());

	                setQCFullName(quote.getCreator().getFullName());
	                setQCPhone(quote.getCreator().getPhoneNumber());
	                setQCEmail(quote.getCreator().getEmailAddress());
	                Country cntry = process.getCountryByCode3(quote.getCreator().getCountryCode());
	                if (cntry != null)
	                    setQCCountry(cntry.getDesc());
	                setQcCompany(quote.getCreator().getCompany());
	            }

	            // Set opportunity owner attributes
	            if (quote.getOppOwner() != null) {
	                checkSalesRepInfo(quote.getOppOwner());

	                setOOFullName(quote.getOppOwner().getFullName());
	                setOOPhone(quote.getOppOwner().getPhoneNumber());
	                setOOEmail(quote.getOppOwner().getEmailAddress());
	                Country cntry = process.getCountryByCode3(quote.getOppOwner().getCountryCode());
	                if (cntry != null)
	                    setOOCountry(cntry.getDesc());
	            } else if (quote.getCreator() != null) {
	                setOOFullName(quote.getCreator().getFullName());
	                setOOPhone(quote.getCreator().getPhoneNumber());
	                setOOEmail(quote.getCreator().getEmailAddress());
	                Country cntry = process.getCountryByCode3(quote.getCreator().getCountryCode());
	                if (cntry != null)
	                    setOOCountry(cntry.getDesc());
	            }

	            // Set delegates list
	            setQuoteDelegates(quote.getDelegatesList());

	            //
	            this.setQuotePromotions(quote.getPromotionsList());
	            // Set quote header attributes
	            if (qh != null) {
	                setBusOrgCode(qh.getBusOrgCode());
	                oppNumber = qh.getOpprtntyNum();
	                quoteTitle = qh.getQuoteTitle();
	                quoteDesc = qh.getQuoteDscr();
	                oppOwnerEmail = qh.getOpprtntyOwnrEmailAdr();
	                selectedExemptnCode = qh.getExemptnCode();

	                deafultBusinessOrg = qh.getBusOrgCode();
	            }

		        // Get business organization option list from application cache
		        this.setBusinessOrganization(process.getBusinessOrgList());

		        this.setExemptionInfo(quote);
		        
		        this.isDsjFlag=qh.isDsjFlag();

	        } catch (QuoteException qe) {
	            logContext.error(this, qe.getMessage());
	            throw new ViewBeanException("error getting data from cache");
	        }

        // Get the defualt business organization code
	    if (StringUtils.isBlank(deafultBusinessOrg))
                deafultBusinessOrg = (String) params.getParameter(DraftQuoteParamKeys.PARAM_DEFAULT_BUSORG);

        } // End if (quote != null)
    }

    protected void checkSalesRepInfo(SalesRep salesRep) {
        if (StringUtils.isBlank(salesRep.getFullName())) {
            salesRep.getBluePageInfo();
        }
    }

    public Collection generateBusOrgOptions() {
        Collection collection = new ArrayList();
        Iterator itr = businessOrganization.iterator();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        boDefaultOption = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                DraftQuoteMessageKeys.DEAFULT_OPTION);
        if (StringUtils.isBlank(deafultBusinessOrg)) {
            collection.add(new SelectOptionImpl(boDefaultOption, "", true));
        } else {
            collection.add(new SelectOptionImpl(boDefaultOption, "", false));
        }

        while (itr.hasNext()) {
            CodeDescObj codeDescObj = (CodeDescObj) itr.next();

            if(codeDescObj.getCode().equalsIgnoreCase(deafultBusinessOrg)){
                collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), true));
            } else {
                collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), false));
            }

        }
        return collection;
    }

    /**
     * @param businessOrg
     *            The businessOrganization to set.
     */
    public void setBusinessOrganization(List businessOrg) {
        this.businessOrganization = businessOrg;
    }

    /**
     * @return Returns the businessOrganization.
     */
    public List getBusinessOrganization() {
        return businessOrganization;
    }

    /**
     * @param businessOrg
     *            The businessOrganization to set.
     */
    public void setQuoteDelegates(List delegates) {
        for (int i = 0; delegates != null && i < delegates.size(); i++) {
            SalesRep salesRep = (SalesRep) delegates.get(i);
            checkSalesRepInfo(salesRep);
        }
        this.quoteDelegates = delegates;
    }

    /**
     * @return Returns the businessOrganization.
     */
    public List getQuoteDelegates() {

        return quoteDelegates;
    }

    public List getQuotePromotions() {
		return quotePromotions;
	}

	public void setQuotePromotions(List quotePromotions) {
		this.quotePromotions = quotePromotions;
	}

	/**
     * @param lob
     *            The busOrgCode to set.
     */
    public void setBusOrgCode(String busOrgCode) {
        this.busOrgCode = busOrgCode;
    }

    /**
     * @return Returns the busOrgCode.
     */
    public String getBusOrgCode() {
        return busOrgCode;
    }

    /**
     * @param fullName
     *            The quote creator full name to set.
     */
    public void setQCFullName(String fullName) {
        this.qcFullName = fullName;
    }

    /**
     * @return Returns the quote creator full name.
     */
    public String getQCFullName() {
        return qcFullName;
    }

    /**
     * @param fullName
     *            The quote creator phone number to set.
     */
    public void setQCPhone(String phoneNum) {
        this.qcPhone = phoneNum;
    }

    /**
     * @return Returns the quote creator phone number.
     */
    public String getQCPhone() {
        return qcPhone;
    }

    /**
     * @param fullName
     *            The quote creator email to set.
     */
    public void setQCEmail(String email) {
        this.qcEmail = email;
    }

    /**
     * @return Returns the quote creator email.
     */
    public String getQCEmail() {
        return qcEmail;
    }

    /**
     * @param fullName
     *            The quote creator qcCompany to set.
     */
    public String getQcCompany() {
		return qcCompany;
	}
    /**
     * @return Returns the quote creator qcCompany.
     */
	public void setQcCompany(String qcCompany) {
		this.qcCompany = qcCompany;
	}

    /**
     * @param fullName
     *            The quote creator country to set.
     */
    public void setQCCountry(String country) {
        this.qcCountry = country;
    }

    /**
     * @return Returns the quote creator country.
     */
    public String getQCCountry() {
        return qcCountry;
    }

    /**
     * @param fullName
     *            The opportunity owner full name to set.
     */
    public void setOOFullName(String fullName) {
        this.ooFullName = fullName;
    }

    /**
     * @return Returns the opportunity owner full name.
     */
    public String getOOFullName() {
        return ooFullName;
    }

    /**
     * @param fullName
     *            The opportunity owner phone number to set.
     */
    public void setOOPhone(String phoneNum) {
        this.ooPhone = phoneNum;
    }

    /**
     * @return Returns the opportunity owner phone number.
     */
    public String getOOPhone() {
        return ooPhone;
    }

    /**
     * @param fullName
     *            The opportunity owner emaile to set.
     */
    public void setOOEmail(String email) {
        this.ooEmail = email;
    }

    /**
     * @return Returns the opportunity owner email.
     */
    public String getOOEmail() {
        return ooEmail;
    }

    /**
     * @param fullName
     *            The opportunity owner country to set.
     */
    public void setOOCountry(String country) {
        this.ooCountry = country;
    }

    /**
     * @return Returns the opportunity owner country.
     */
    public String getOOCountry() {
        return ooCountry;
    }

    /**
     * @return Returns the opportunity number.
     */
    public String getOppNumber() {
        return oppNumber == null ? "" : oppNumber;
    }

    /**
     * @return Returns the opportunity owner email.
     */
    public String getOppOwnerEmail() {
        return oppOwnerEmail == null ? "" : oppOwnerEmail;
    }

    /**
     * @return Returns the quote title.
     */
    public String getQuoteTitle() {
        return quoteTitle == null ? "" : quoteTitle.trim();
    }

    /**
     * @return Returns the quote desc.
     */
    public String getQuoteDesc() {
        return quoteDesc == null ? "" : quoteDesc.trim();
    }

    /**
     * @return true .
     */
    public boolean isSiebelOppNumChecked() {
        if( StringUtils.isBlank(selectedExemptnCode) && StringUtils.isNotBlank(oppNumber) ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return true .
     */
    public boolean isCustInitChecked() {
        if( StringUtils.isBlank(oppNumber) && StringUtils.isNotBlank(selectedExemptnCode) 
        		&& (selectedExemptnCode.equalsIgnoreCase(DraftQuoteConstants.EXEMPTNCODE_80)
        				|| (!selectedExemptnCode.equalsIgnoreCase(DraftQuoteConstants.EXEMPTNCODE_80)
        					&&	selectedExemptnCode.equalsIgnoreCase(defaultExemptionCode))
        					)
        					) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return true if any of the quote creator attributes is not blank
     */
    public boolean isDisplayQuoteCreator() {

        if (StringUtils.isNotBlank(qcFullName) || StringUtils.isNotBlank(qcPhone) || StringUtils.isNotBlank(qcEmail)
                || StringUtils.isNotBlank(qcCountry)) {

            isDisplayQuoteCreator = true;
        }

        return isDisplayQuoteCreator;
    }

    /**
     * @return true if any of the opp. owner attributes is not blank
     */
    public boolean isDisplayOppOwner() {

        if (StringUtils.isNotBlank(ooFullName) || StringUtils.isNotBlank(ooPhone) || StringUtils.isNotBlank(ooEmail)
                || StringUtils.isNotBlank(ooCountry)) {

            isDisplayOppOwner = true;
        }

        return isDisplayOppOwner;
    }

    /**
     * @return true if isDisplayQuoteCreator or isDisplayOppOwner is true
     */
    public boolean isDisplayQuoteCreatorOppOwner() {

        if (isDisplayQuoteCreator() || isDisplayOppOwner()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return true if user is Submitter/Approver
     */
    public boolean isDisplayQuoteEditors() {
        int accessLevel = user.getAccessLevel(QuoteConstants.APP_CODE_SQO);
        if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteUserSession.getAudienceCode())){
        	isDisplayQuoteEditors = true;
        }
        else if((accessLevel == QuoteConstants.ACCESS_LEVEL_SUBMITTER) || (accessLevel == QuoteConstants.ACCESS_LEVEL_APPROVER)) {
            isDisplayQuoteEditors = true;
        }
        return isDisplayQuoteEditors;
    }


    public boolean isDisplayQuotePromotions() {
        isDisplayQuotePromotions = ButtonDisplayRuleFactory.singleton().isDisplayPromotions(header);

        return isDisplayQuotePromotions;
    }

    /**
     * @return Returns the addQuoteEditorURL.
     */
    public String getAddQuoteEditorURL() {
        String btnParams = this.genBtnParamsForAction(DraftQuoteActionKeys.POST_SALES_INFO_TAB,
                DlgtnActionKeys.ADD_QUOTE_DELEGATE,
                DlgtnParamKeys.PARAM_TARGET_ACTION + "=" + DraftQuoteActionKeys.DISPLAY_SALES_INFO_TAB);
        return btnParams;
    }

    /**
     * @return Returns the addQuotePromotionURL.
     */
    public String getAddQuotePromotionURL() {
        String btnParams = this.genBtnParamsForAction(DraftQuoteActionKeys.POST_SALES_INFO_TAB,
        		PromotionKeys.ADD_UPDATE_QUOTE_PROMOTION,
        		PromotionKeys.PARAM_TARGET_ACTION + "=" + DraftQuoteActionKeys.DISPLAY_SALES_INFO_TAB);
        return btnParams;
    }

    /**
     * @return Returns the changeOppOwnerURL.
     */
    public String getChangeOppOwnerURL() {
        String btnParams = this.genBtnParamsForAction(DraftQuoteActionKeys.POST_SALES_INFO_TAB,
                DraftQuoteActionKeys.UPDATE_OPP_OWNER, null);
        return btnParams;
    }

    /**
     * @return Returns the removeQuoteEditorURL.
     */
    public String getRemoveQuoteEditorURL(String delegateId) {
        String btnParams = this.genBtnParamsForAction(DraftQuoteActionKeys.POST_SALES_INFO_TAB,
                DlgtnActionKeys.REMOVE_QUOTE_DELEGATE,
                DlgtnParamKeys.PARAM_TARGET_ACTION + "=" + DraftQuoteActionKeys.DISPLAY_SALES_INFO_TAB
                + "," + DlgtnParamKeys.PARAM_DELEGATEID + "=" + delegateId);
        return btnParams;
    }

    /**
     * @return Returns the removeQuotePromotionURL.
     */
    public String getRemoveQuotePromotionURL(String quoteTxtId) {
        String btnParams = this.genBtnParamsForAction(DraftQuoteActionKeys.POST_SALES_INFO_TAB,
        		PromotionKeys.REMOVE_QUOTE_PROMOTION,
        		PromotionKeys.PARAM_TARGET_ACTION + "=" + DraftQuoteActionKeys.DISPLAY_SALES_INFO_TAB
                + "," + PromotionKeys.PARAM_QUOTE_TXT_ID + "=" + quoteTxtId);
        return btnParams;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DraftSQBaseViewBean#getDisplayTabAction()
     */
    public String getDisplayTabAction() {
        return DraftQuoteActionKeys.DISPLAY_SALES_INFO_TAB;
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DraftSQBaseViewBean#getPostTabAction()
     */
    public String getPostTabAction() {
        return DraftQuoteActionKeys.POST_SALES_INFO_TAB;
    }

    private void setExemptionInfo(Quote quote) throws QuoteException{
    	String[] exemptionInfoArray = initExemptionInfo(quote);
    	this.defaultExemptionCode = exemptionInfoArray[0];
        this.exemptionCodeTextKey = exemptionInfoArray[1];
        this.exemptionCode = exemptionInfoArray[2];
    }

    public String getDefaultExemptionCode(){
    	return defaultExemptionCode;
    }
    public String getExemptionCode() {
        return exemptionCode;
    }
    public String getExemptionCodeTextKey() {
        return exemptionCodeTextKey;
    }

    public boolean isDisplayOppNumInput() {
        return isDisplayOppNumInput;
    }
    
 

    /**
	 * @return the isDsjFlag
	 */
	public boolean isDsjFlag() {
		return isDsjFlag;
	}

	/**
	 * @param isDsjFlag the isDsjFlag to set
	 */
	public void setDsjFlag(boolean isDsjFlag) {
		this.isDsjFlag = isDsjFlag;
	}

	public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("isDisplayOppNumInput = ").append(isDisplayOppNumInput).append("\n");
        buffer.append("exemptionCode = ").append(exemptionCode).append("\n");
        buffer.append("exemptionCodeTextKey = ").append(exemptionCodeTextKey).append("\n");
        buffer.append("defaultExemptionCode = ").append(defaultExemptionCode).append("\n");
        return buffer.toString();
    }
}
