package com.ibm.dsw.quote.customer.viewbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.ibm.dsw.quote.common.domain.AgreementTypeConfigFactory;
import com.ibm.dsw.quote.common.domain.ButtonDisplayRuleFactory;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.CountrySignatureRuleFactory;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>AgrmntTypeViewAdapter<code> class.
 *
 * @author: zhengmr@cn.ibm.com
 *
 * Creation date: Oct 29, 2009
 */

public class AgrmntTypeViewAdapter implements Serializable {

    protected Quote quote = null;
    protected transient Customer customer = null;
    protected Locale locale = null;

    protected String cntryCode = null;
    protected String region = null;
    protected transient Country country = null;
    protected int webCtrctId = -1;

    protected String agreementType = null;
    protected String agreementNum = null;
    protected String agrmtTypeCode = null;
    protected String transSVPLevel = null;
    protected String authrztnGroup = null;
    protected int govSiteType = -1;
    protected boolean isGovTypeDisplay = false;
    protected boolean isAddiSiteGovTypeDisplay = false;

    protected transient List agreementTypeList = null;
    protected transient List agreementOptionList = null;
    protected transient List svpLevelList = null;

    protected transient Map agrmntSVPLevels = null;
    protected boolean isDisplaySignatureMsg;
    protected String cntryCode2 = null;

    public AgrmntTypeViewAdapter() {
        super();
    }

    public void collectResultsForCtrctCreate(Parameters param) throws ViewBeanException {

        quote = (Quote) param.getParameter(ParamKeys.PARAM_QUOTE_OBJECT);
        QuoteHeader header = quote.getQuoteHeader();
        customer = quote.getCustomer();
        locale = (Locale) param.getParameter(ParamKeys.PARAM_LOCAL);
        agreementTypeList = (List) param.getParameter(CustomerParamKeys.PARAM_AGRMNT_TYPES);
        agreementOptionList = (List) param.getParameter(CustomerParamKeys.PARAM_AGRMNT_OPTIONS);
        country = quote.getQuoteHeader().getCountry();
        cntryCode = country.getCode3();
        region = country.getWWRegion();
        this.setCntryCode2(country.getCode2());
        isDisplaySignatureMsg = CountrySignatureRuleFactory.singleton().isRequireSignature(country.getCode2());

        if (header.isCreateCtrctFlag()) {
            webCtrctId = header.getWebCtrctId();

            if (StringUtils.isNotBlank(header.getContractNum())) {
                agreementType = CustomerConstants.AGRMNT_TYPE_ADDI_SITE;
                agreementNum = header.getContractNum();
            }
	        else if (header.getWebCtrctId() > 0 && customer != null) {
	        	agrmtTypeCode = customer.getWebCustTypeCode();
	            agreementType = customer.getAgreementType();
	            transSVPLevel = customer.getTransSVPLevel();
	            authrztnGroup = customer.getAuthrztnGroup();
	        }
        }

        collectBaseResults();
    }

    public void collectResultsForCustCreate(Parameters param) throws ViewBeanException {

        customer = (Customer) param.getParameter(CustomerParamKeys.PARAM_CUSTOMER);
        locale = (Locale) param.getParameter(ParamKeys.PARAM_LOCAL);
        agreementTypeList = (List) param.getParameter(CustomerParamKeys.PARAM_AGRMNT_TYPES);
        agreementOptionList = (List) param.getParameter(CustomerParamKeys.PARAM_AGRMNT_OPTIONS);
        cntryCode = (String) param.getParameter(ParamKeys.PARAM_COUNTRY);
        cntryCode2 = (String) param.getParameter(ParamKeys.PARAM_COUNTRY_CODE2);
        isDisplaySignatureMsg = CountrySignatureRuleFactory.singleton().isRequireSignature(cntryCode2);

        if (customer != null) {

        	agrmtTypeCode = customer.getWebCustTypeCode();
            agreementType = customer.getAgreementType();
            agreementNum = param.getParameterAsString(ParamKeys.PARAM_AGREEMENT_NUM);
            transSVPLevel = customer.getTransSVPLevel();
            authrztnGroup = customer.getAuthrztnGroup();


            if (customer.isAddiSiteCustomer() && customer.getContractList() != null && customer.getContractList().size() > 0) {
                Contract ctrct = (Contract) customer.getContractList().get(0);

                if (CustomerConstants.AGRMNT_TYPE_GOVERNMENT.equalsIgnoreCase(ctrct.getSapContractVariantCode())
                        && CustomerConstants.COUNTRY_USA.equalsIgnoreCase(cntryCode)) {
                    isAddiSiteGovTypeDisplay = true;
                }
            }
        }

        try {
            CacheProcess process = CacheProcessFactory.singleton().create();
            country = process.getCountryByCode3(cntryCode);
            region = country.getWWRegion();

        } catch (QuoteException e) {
            throw new ViewBeanException(e);
        }
        
        collectBaseResults();
    }

    protected void collectBaseResults() {

        if (StringUtils.isNotBlank(agreementType)) {
            if (CustomerConstants.AGRMNT_TYPE_GOVERNMENT.equalsIgnoreCase(agreementType)
                    || (CustomerConstants.AGRMNT_TYPE_ADDI_SITE.equalsIgnoreCase(agreementType)
                            && isAddiSiteGovTypeDisplay)) {

	            if (CustomerConstants.AUTHRZTN_GRP_USA_FEDERAL.equalsIgnoreCase(authrztnGroup)
	                    || CustomerConstants.AUTHRZTN_GRP_CRB_FEDERAL.equalsIgnoreCase(authrztnGroup))
	                govSiteType = 1;
	            else
	                govSiteType = 0;
            }
        }

        agrmntSVPLevels = new HashMap();
        for (int i = 0; i < agreementTypeList.size(); i++) {
            CodeDescObj agrmntType = (CodeDescObj) agreementTypeList.get(i);
            String code = agrmntType.getCode();
            List svpLevels = AgreementTypeConfigFactory.singleton().getSVPLevels(code, region, cntryCode,
                        authrztnGroup);
            agrmntSVPLevels.put(code, svpLevels);
        }

        isGovTypeDisplay = ButtonDisplayRuleFactory.singleton().isGovSiteTypeOptionDisplay(cntryCode);

    }

    public String getAgreementType() {
        return agreementType;
    }
    public void setAgreementType(String agreementType) {
        this.agreementType = agreementType;
    }

    public String getAgreementNum() {
        return agreementNum;
    }

    public int getGovSiteType() {
        return govSiteType;
    }

    public void setGovSiteType(int govSiteType) {
        this.govSiteType = govSiteType;
    }

    public String getTransSVPLevel() {
        return transSVPLevel;
    }

    public List getAgreementTypeList() {
        return agreementTypeList;
    }
    public void setAgreementTypeList(List agreementTypeList) {
        this.agreementTypeList = agreementTypeList;
    }

    public List getAgreementOptionList() {
        return agreementOptionList;
    }
    public void setAgreementOptionList(List agreementOptionList) {
        this.agreementOptionList = agreementOptionList;
    }
    
    public List getSVPLevelList() {
        return svpLevelList;
    }
    public void setSVPLevelList(List svpLevelList) {
        this.svpLevelList = svpLevelList;
    }

    // get PA agreement type options
    public Collection generateAgreeTypeOptions() {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        String selectOne = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.SELECT_ONE);

        Collection collection = new ArrayList();
        Iterator itr = agreementTypeList.iterator();
        collection.add(new SelectOptionImpl(selectOne, "", true));
        while(itr.hasNext()) {
            CodeDescObj codeDescObj = (CodeDescObj)itr.next();
            if (agreementType != null && agreementType.equals(codeDescObj.getCode())){
                collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), true));
            } else {
                collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), false));
            }
        }

    	return collection;
    }

    // get CSA agreement type options
    public Collection generateAgreeOptionList() {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        String selectOne = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.SELECT_ONE);

        Collection collection = new ArrayList();
        if ( agreementOptionList != null ) {
        	Iterator itr = agreementOptionList.iterator();
            collection.add(new SelectOptionImpl(selectOne, "", true));
            while(itr.hasNext()) {
                CodeDescObj codeDescObj = (CodeDescObj)itr.next();
                if (agrmtTypeCode != null && agrmtTypeCode.equals(codeDescObj.getCode())){
                    collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), true));
                } else {
                    collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), false));
                }
            }
        }
        
    	return collection;
    }
    
    protected Collection genSVPLevelOptions(String agrmntTypeCode) {
        Collection collection = new ArrayList();

        List svpLevels = (List) agrmntSVPLevels.get(agrmntTypeCode);
        for (int i = 0; svpLevels != null && i < svpLevels.size(); i++) {

            CodeDescObj lvl = (CodeDescObj) svpLevels.get(i);
            String lvlCode = lvl.getCode();
            String lvlDesc = lvl.getCodeDesc();
            boolean selected = false;

            if (svpLevels.size() == 1
                    || (agrmntTypeCode.equalsIgnoreCase(agreementType) && lvlCode.equalsIgnoreCase(transSVPLevel)))
                selected = true;

            collection.add(new SelectOptionImpl(lvlDesc, lvlCode, selected));
        }

        return collection;
    }

    public String getACASvpLevel() {
        return getSVPLevelValueString(CustomerConstants.AGRMNT_TYPE_ACADEMIC);
    }

    public String getSVPLevelValueString(String agrmntType) {
        List svpLevels = (List) agrmntSVPLevels.get(agrmntType);
        if (svpLevels != null && svpLevels.size() > 0)
            return ((CodeDescObj) svpLevels.get(0)).getCodeDesc();
        else
            return "";
    }

    public Collection getXSPSvpLevelOptions() {
    	Collection collection = new ArrayList();
    	collection.add(new SelectOptionImpl(QuoteConstants.PRICE_LEVEL_D, QuoteConstants.PRICE_LEVEL_D, true));
        return collection;
    }

    public boolean isGovTypeDisplay() {
        return isGovTypeDisplay;
    }

    public String getCntryCode() {
        return cntryCode;
    }

    public String getWebCtrctId() {
        return String.valueOf(webCtrctId);
    }

    public String isAddiSiteGovTypeDisplay() {
        return String.valueOf(isAddiSiteGovTypeDisplay);
    }

    public void setAddiSiteGovTypeDisplay(boolean isAddiSiteGovTypeDisplay) {
        this.isAddiSiteGovTypeDisplay = isAddiSiteGovTypeDisplay;
    }

    /**
     * @return Returns the isDisplaySignatureMsg.
     */
    public boolean isDisplaySignatureMsg() {
        return isDisplaySignatureMsg;
    }
    /**
     * @return Returns the cntryCode2.
     */
    public String getCntryCode2() {
        return cntryCode2;
    }
    /**
     * @param cntryCode2 The cntryCode2 to set.
     */
    public void setCntryCode2(String cntryCode2) {
        this.cntryCode2 = cntryCode2;
    }
    
	public String getAgrmtTypeCode() {
		return agrmtTypeCode;
	}

	public void setAgrmtTypeCode(String agrmtTypeCode) {
		this.agrmtTypeCode = agrmtTypeCode;
	}
}
