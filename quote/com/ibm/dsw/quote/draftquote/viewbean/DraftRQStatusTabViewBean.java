package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.QuoteStatus;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQViewKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOption;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DraftRQStatusTabViewBean</code> class is to collect required data
 * for draft renewal quote page.
 * 
 * @author cuixg@cn.ibm.com
 * 
 * Created on Apr 16, 2007
 */
public class DraftRQStatusTabViewBean extends DraftRQBaseViewBean {
    private String primaryStatusCode;

    private boolean blockedForRenewal = false;

    /* Determine whether to display the renewal quote status */
    private boolean displayRQStatus = true;

    /* Determine whether to display the Change renewal quote status section */
    private boolean displayChangeRQStatus = false;

    /* Determine whether to display the Termination tracking section */
    private boolean displayTermTracking = false;

    private transient List availablePStatuses = new ArrayList();

    private transient List termReasonOptions = new ArrayList();

    private String terminationEmailURL;

    public List getAvaliablePStatuses() {
        return availablePStatuses;
    }

    public List getTermReasonOptions() {
        return termReasonOptions;
    }

    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);

        if (getRenewalQuote() == null || getRenewalQuoteHeader() == null)
            throw new ViewBeanException("Renewal quote or quote header is null");

        //primary status
        QuoteStatus pStatus = null;
        if (getRenewalQuote().getAllWebPrimaryStatuses() != null && getRenewalQuote().getAllWebPrimaryStatuses().size() > 0) {
            pStatus = (QuoteStatus) getRenewalQuote().getAllWebPrimaryStatuses().get(0);
        }
        primaryStatusCode = pStatus == null ? "" : pStatus.getStatusCode();

        //find whether "block for renewal" contains in all secondary status
        blockedForRenewal = getRenewalQuote().containsWebSecondaryStatus(QuoteStatus.S_BLOCKED_4_AUTORENEWAL);

        Boolean bEditable = (Boolean) params.getParameter(DraftRQViewKeys.RQ_EDITABLE);
        boolean editable = bEditable == null ? false : bEditable.booleanValue();

        Boolean bUpdatable = (Boolean) params.getParameter(DraftRQViewKeys.RQ_UPDATABLE);
        boolean updatable = bUpdatable == null ? false : bUpdatable.booleanValue();

        displayChangeRQStatus = editable || updatable;
        displayTermTracking = editable || updatable;

        List pStatuses = (List) params.getParameter(DraftRQViewKeys.RQ_P_STATUS);
        List rqReasons = (List) params.getParameter(DraftRQViewKeys.RQ_TERM_REASON);

        if (pStatuses == null || rqReasons == null) {
            throw new ViewBeanException("Primary status list or termination reason list is null");
        }

        String selectOne = getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                DraftRQMessageKeys.DEFAULT_REASON_OPTION);
        termReasonOptions.add(new SelectOptionImpl(selectOne, "", false));
        for (Iterator iter = rqReasons.iterator(); iter.hasNext();) {
            CodeDescObj rqReason = (CodeDescObj) iter.next();
            SelectOption so = new SelectOptionImpl(rqReason.getCodeDesc(), rqReason.getCode(), rqReason.getCode()
                    .equals(getRQTermReason()));
            termReasonOptions.add(so);
        }

        for (Iterator iter = pStatuses.iterator(); iter.hasNext();) {
            CodeDescObj rqStatus = (CodeDescObj) iter.next();
            SelectOption so = new SelectOptionImpl(rqStatus.getCodeDesc(), rqStatus.getCode(), StringUtils.trimToEmpty(
                    rqStatus.getCode()).equals(primaryStatusCode));
            availablePStatuses.add(so);
        }

        terminationEmailURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.SET_USER_COOKIE) + "&"
                + ParamKeys.PARAM_SITE_NUM + "=" + siteNumber + "&" + ParamKeys.PARAM_AGREEMENT_NUM + "="
                + agreementNum + "&" + ParamKeys.PARAM_RNWL_QT_NUM + "=" + getRenewalQuoteHeader().getRenwlQuoteNum()
                + "&" + ParamKeys.PARAM_DEST + "=3";
    }


    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DraftRQBaseViewBean#getDisplayTabAction()
     */
    public String getDisplayTabAction() {
        return DraftQuoteActionKeys.DISPLAY_RQ_STATUS_TAB;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DraftRQBaseViewBean#getPostTabAction()
     */
    public String getPostTabAction() {
        return DraftQuoteActionKeys.POST_RQ_STATUS_TAB;
    }
    
    public boolean isDisplayRQStatus() {
        return displayRQStatus;
    }

    public boolean isDisplayChangeRQStatus() {
        return displayChangeRQStatus;
    }

    public boolean isDisplayTermTracking() {
        return displayTermTracking;
    }

    public String getRQTermReason() {
        String termReason = getRenewalQuoteHeader().getRnwlTermntnReasCode();
        return StringUtils.trimToEmpty(termReason);
    }

    public String getRQTermComment() {
        return getRenewalQuoteHeader().getTermntnComments();
    }

    protected String getI18NString(String basename, Locale locale, String key) {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        return appCtx.getI18nValueAsString(basename, locale, key);
    }

    public String getPostRQStatusActionURL() {
        return HtmlUtil.getURLForAction(DraftQuoteActionKeys.POST_RQ_STATUS_TAB);
    }

    public String getTerminationEmailURL() {
        return terminationEmailURL;
    }

    /**
     * @return Returns the blockedForRenewal.
     */
    public boolean isBlockedForRenewal() {
        return blockedForRenewal;
    }

    /**
     * @return Returns the primaryStatusCode.
     */
    public String getPrimaryStatusCode() {
        return primaryStatusCode;
    }
}
