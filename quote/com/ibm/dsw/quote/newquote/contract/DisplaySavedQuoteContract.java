package com.ibm.dsw.quote.newquote.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplaySavedQuoteContract</code> class.
 * 
 * @author: wangxu@cn.ibm.com
 * 
 * Created on Feb 28, 2007
 */
public class DisplaySavedQuoteContract extends QuoteBaseCookieContract {

    private String markDefault ;

    private String ownerFilter;

    private String timeFilter;

    private String quoteId;

    private String ownerFilterOwned;

    private String ownerFilterDelegated;
    
    private String formFlag;

    private String quoteSort=null;
    
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);

        this.setQuoteId(parameters.getParameterAsString(NewQuoteParamKeys.PARAM_QUOTE_ID));
        loadFromCookie(parameters);
        if(parameters.hasParameter(NewQuoteParamKeys.PARAM_FORM_FLAG)){
            loadFromRequest(parameters);
        }

    }

    /**
     * @param parameters
     * @return
     */
    private boolean loadFromCookie(Parameters parameters) {
        if (sqoCookie == null)
            return false;// Normally it never goes here.
        this.setOwnerFilter(QuoteCookie.getOwnerFilter(sqoCookie));
        this.setTimeFilter(QuoteCookie.getTimeFilter(sqoCookie));
        return true;
    }

    /**
     * @param parameters
     * @return
     */
    private void loadFromRequest(Parameters parameters) {
        this.setFormFlag(parameters.getParameterAsString(NewQuoteParamKeys.PARAM_FORM_FLAG));
        this.setTimeFilter(parameters.getParameterAsString(NewQuoteParamKeys.PARAM_TIME_FILTER)); 
        this.setQuoteSort(parameters.getParameterAsString(NewQuoteParamKeys.PARAM_QUOTE_SORT));
        this.setOwnerFilter(parameters);        
        this.setOwnerFilter(parameters.getParameterAsString(NewQuoteParamKeys.PARAM_OWNER_FILTER));
        this.setMarkDefault(parameters.getParameterAsString(NewQuoteParamKeys.PARAM_MARK_AS_DEFAULT));
    }

    /**
     * @return Returns the quoteId. To be deleted
     */
    public String getQuoteId() {
        return quoteId;
    }

    /**
     * @param quoteId
     *            The quoteId to set.
     */
    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    /**
     * @return Returns the markDefault.
     */
    public String getMarkDefault() {
        return markDefault;
    }

    /**
     * @param markDefault
     *            The markDefault to set.
     */
    public void setMarkDefault(String markDefault) {
        this.markDefault = markDefault;
    }

    /**
     * @return Returns the ownerFilter.
     */
    public String getOwnerFilter() {
        return ownerFilter;
    }

    /**
     * @param parameters
     *            The ownerFilter to set.
     */
    public boolean setOwnerFilter(Parameters parameters) {
        ownerFilterOwned = parameters.getParameterAsString(NewQuoteParamKeys.PARAM_OWNER_FILTER_OWNED);
        ownerFilterDelegated = parameters.getParameterAsString(NewQuoteParamKeys.PARAM_OWNER_FILTER_DELEGATED);
        if (ownerFilterDelegated != null) {
            this.ownerFilter = ownerFilterDelegated;
            if (ownerFilterOwned != null)
                this.ownerFilter = NewQuoteParamKeys.PARAM_ALL_VALUE;
            return true;
        } else if (ownerFilterOwned != null) {
            this.ownerFilter = ownerFilterOwned;
            return true;
        }
        this.ownerFilter=null;
        return false;
    }

    /**
     * @param ownerFilter2
     */
    private void setOwnerFilter(String ownerFilter) {
        if(ownerFilter!=null){
            this.ownerFilter = ownerFilter;
            if(!ownerFilter.equals(NewQuoteParamKeys.PARAM_DELEGATED_TO_ME_VALUE))
                this.ownerFilterOwned = NewQuoteParamKeys.PARAM_OWNED_BY_ME_VALUE;
            if(!ownerFilter.equals(NewQuoteParamKeys.PARAM_OWNED_BY_ME_VALUE))
                this.ownerFilterDelegated = NewQuoteParamKeys.PARAM_DELEGATED_TO_ME_VALUE;
        }
    }

    /**
     * @return Returns the timeFilter.
     */
    public String getTimeFilter() {
        return timeFilter;
    }

    /**
     * @param timeFilter
     *            The timeFilter to set.
     */
    public void setTimeFilter(String timeFilter) {
        this.timeFilter = timeFilter;
    }

    /**
     * @return Returns the ownerFilterDelegated.
     */
    public String getOwnerFilterDelegated() {
        return ownerFilterDelegated;
    }

    /**
     * @param ownerFilterDelegated
     *            The ownerFilterDelegated to set.
     */
    public void setOwnerFilterDelegated(String ownerFilterDelegated) {
        this.ownerFilterDelegated = ownerFilterDelegated;
    }

    /**
     * @return Returns the ownerFilterOwned.
     */
    public String getOwnerFilterOwned() {
        return ownerFilterOwned;
    }

    /**
     * @param ownerFilterOwned
     *            The ownerFilterOwned to set.
     */
    public void setOwnerFilterOwned(String ownerFilterOwned) {
        this.ownerFilterOwned = ownerFilterOwned;
    }
    /**
     * @return Returns the formFlag.
     */
    public String getFormFlag() {
        return formFlag;
    }
    /**
     * @param formFlag The formFlag to set.
     */
    public void setFormFlag(String formFlag) {
        this.formFlag = formFlag;
    }

    /**
     * @return
     */
    public String getQuoteSort() {
        return this.quoteSort;
    }
    /**
     * @param quoteSort The quoteSort to set.
     */
    public void setQuoteSort(String quoteSort) {
        this.quoteSort = quoteSort;
    }
}