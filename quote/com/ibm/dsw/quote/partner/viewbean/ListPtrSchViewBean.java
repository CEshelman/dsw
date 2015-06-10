package com.ibm.dsw.quote.partner.viewbean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.partner.config.PartnerActionKeys;
import com.ibm.dsw.quote.partner.config.PartnerMessageKeys;
import com.ibm.dsw.quote.partner.config.PartnerParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ListPtrSchViewBean</code> class is to support display result of
 * partner search
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Mar 15, 2007
 */
public class ListPtrSchViewBean extends PartnerViewBean {
    private SearchResultList partnerList;

    private String stateName;

    private String countryName;

    /**
     * @return Returns the partnerList.
     */
    public SearchResultList getPartnerList() {
        return partnerList;
    }

    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        partnerList = (SearchResultList) params.getParameter(PartnerParamKeys.PARAM_PARTNER_LIST);
        countryName = params.getParameterAsString(PartnerParamKeys.PARAM_COUNTRY_NAME);
        stateName = params.getParameterAsString(PartnerParamKeys.PARAM_STATE_NAME);
    }

    public String getKeySearchCriteria() {
        return PartnerMessageKeys.MSG_SEARCH_CRITERIA;
    }

    public String getSearchResellerUrl() {
        return HtmlUtil.getURLForAction(PartnerActionKeys.DISPLAY_RESELLER_SEARCH);
    }

    public String getSearchDistributorUrl() {
        return HtmlUtil.getURLForAction(PartnerActionKeys.DISPLAY_DISTRIBUTOR_SEARCH);
    }

    public String getSelectResellerUrl() {
        //TODO should modify it after constants are defined
        return HtmlUtil.getURLForAction("SELECT_RESELLER");
    }

    public String getSelectDistributorUrl() {
        //TODO should modify it after constants are defined
        return HtmlUtil.getURLForAction("SELECT_DISTRIBUTOR");
    }

    public int getTierInfo() {
        if (StringUtils.isBlank(getTier1())) {
            return 2;
        } else if (StringUtils.isBlank(getTier2())) {
            return 1;
        } else {
            return 0;
        }
    }

    public String getMsgChangeCriteria() {
        return PartnerMessageKeys.MSG_CHANGE_SEARCH_CRITERIA;
    }

    public String getMsgResellerResult() {
        return PartnerMessageKeys.MSG_RESELLER_RESULT;
    }

    public String getMsgResellerResultDesc() {
        return PartnerMessageKeys.MSG_RESELLER_RESULT_DESC;
    }

    public String getMsgDistributorResult() {
        return PartnerMessageKeys.MSG_DISTRIBUTOR_RESULT;
    }

    public String getMsgDistributorResultDesc() {
        return PartnerMessageKeys.MSG_DISTRIBUTOR_RESULT_DESC;
    }

    public String getMsgResellerResultTitle() {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        return getI18NString(appContext.getConfigParameter(FrameworkKeys.JADE_I18N_BASENAME_KEY), locale,
                PartnerMessageKeys.MSG_RESELLER_RESULT_TITLE);

    }

    public String getMsgDistributorResultTitle() {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        return getI18NString(appContext.getConfigParameter(FrameworkKeys.JADE_I18N_BASENAME_KEY), locale,
                PartnerMessageKeys.MSG_DISTRIBUTOR_RESULT_TITLE);

    }

    /**
     * @return Returns the countryName.
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * @return Returns the stateName.
     */
    public String getStateName() {
        return stateName;
    }

    public List getNavTrialParams() {
        List params = new ArrayList();
        params.add("");
        params.add("");
        params.add("country=" + getCutCountry() + "&amp;lob=" + getLobCode());
        return params;
    }
}
