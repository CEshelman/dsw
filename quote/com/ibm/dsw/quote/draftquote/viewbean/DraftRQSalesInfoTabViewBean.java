package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.domain.SalesOdds;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.ActionKeys;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.QuoteAttachment;
import com.ibm.dsw.quote.common.domain.QuoteTxt;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.TacticCode;
import com.ibm.dsw.quote.dlgtn.config.DlgtnActionKeys;
import com.ibm.dsw.quote.dlgtn.config.DlgtnParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
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
 * The <code>DraftRQSalesInfoTabViewBean</code> class.
 * 
 * @author gancm@cn.ibm.com
 * 
 * Created on 2007-4-11
 */
public class DraftRQSalesInfoTabViewBean extends DraftRQBaseViewBean {

    public final static String YES = "Yes";

    public final static String NO = "No";

    boolean isFullyEditable = false;

    boolean isEditSalesInfo = false;

    //Quote description section
    String quoteTitle = "";

    String quoteFullDesc = "";

    //Opportunity information section
    String deafultBusinessOrg;
    
    String businessOrgDesc;

    String oppEmail = "";

    transient List businessOrganization;

    //Current Quote editor
    String cqEditorFullName = "";

    String cqEditorPhone = "";

    String cqEditorEmail = "";

    String cqEditorCountry = "";

    //Opportunity owner
    String ooFullName = "";

    String ooPhone = "";

    String ooEmail = "";

    String ooCountry = "";

    //Tracking information section
    //Current tracking information
    String currentTIUpsideTransaction = "";

    transient List currentTITacticCodes;
    
    String quoteSalesStageCode = "";
    
    String quoteCustReasCode = "";
    
    String quoteSalesOddsCode = "";
    
    String quoteSalesOddsCodeDscr = "";
    
    public transient Collection salesStageCodeOptions;
    
    public transient Collection custReasOptions;
    
    public String custReasOptionArray;

    String currentTISalesComments = "";

    //Change tracking information
    transient List changeTITacticCodes;

    transient List changeTISalesOdds;

    //Quote editors section
    transient List currentEditorsList;
    
    boolean isDisplayQuoteEditors = false;
    
    transient QuoteTxt quoteComment;
    
    transient List salesCmmntAttchmnts = null;
    
    String attchmntDwnldURL = null;
    
    String detailComments = "";
    
    String detailCommentsId = "";
    
    transient List salesStageCodeList = null;
    
    transient List salesOddsList = null;
    
    private int currBlockReminder;
    
    public void collectResults(Parameters params) throws ViewBeanException {

        LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.collectResults(params);

        if (renewalQuote != null) {            
            try {
                //Opportunity information section
                //Quote editor and Opportunity owner
                CacheProcess instance = CacheProcessFactory.singleton().create();

                if (renewalQuote.getCreator() != null) {
                    checkSalesRepInfo(quote.getCreator());
                    
                    setCqEditorFullName(renewalQuote.getCreator().getFullName());
                    setCqEditorEmail(renewalQuote.getCreator().getEmailAddress());
                    setCqEditorPhone(renewalQuote.getCreator().getPhoneNumber());
                    Country cntry = instance.getCountryByCode3(renewalQuote.getCreator().getCountryCode());
	                if (cntry != null){
	                    setCqEditorCountry(cntry.getDesc());
	                }
                }
                if (renewalQuote.getOppOwner() != null) {
                    checkSalesRepInfo(quote.getOppOwner());
                    
                    setOoFullName(renewalQuote.getOppOwner().getFullName());
                    setOoPhone(renewalQuote.getOppOwner().getPhoneNumber());
                    setOoEmail(renewalQuote.getOppOwner().getEmailAddress());
                    Country cntry = instance.getCountryByCode3(renewalQuote.getOppOwner().getCountryCode());
	                if (cntry != null){
	                    setOoCountry(cntry.getDesc());
	                }
                } else if (renewalQuote.getCreator() != null) {
                    setOoFullName(renewalQuote.getCreator().getFullName());
                    setOoPhone(renewalQuote.getCreator().getPhoneNumber());
                    setOoEmail(renewalQuote.getCreator().getEmailAddress());
                    Country cntry = instance.getCountryByCode3(renewalQuote.getCreator().getCountryCode());
	                if (cntry != null){
	                    setOoCountry(cntry.getDesc());
	                }
                }

                if (renewalQuoteHeader != null) {
                    setQuoteFullDesc(renewalQuoteHeader.getQuoteDscr());
                    
                    setFullyEditable(renewalQuote.getQuoteAccess().isCanEditRQ());
                    setEditSalesInfo(renewalQuote.getQuoteAccess().isCanEditRQSalesInfo());
                    
                    
                    setQuoteTitle(renewalQuoteHeader.getQuoteTitle());
                    setOppEmail(renewalQuoteHeader.getOpprtntyOwnrEmailAdr());

                    
                    
                    //Tracking information
                    String currentTIUpsideTransactionInt = renewalQuoteHeader.getUpsideTrendTowardsPurch();
                    if (currentTIUpsideTransactionInt != null){
                        if (currentTIUpsideTransactionInt.equals(DraftQuoteConstants.UPSIDE_TRANSACTION_YES)){
                            currentTIUpsideTransaction = YES;
                        } else if (currentTIUpsideTransactionInt.equals(DraftQuoteConstants.UPSIDE_TRANSACTION_NO)){
                            currentTIUpsideTransaction = NO;
                        }
                    } 

                    currentTITacticCodes = new ArrayList();
                    List currentTITacticCodeList = renewalQuoteHeader.getTacticCodes();
                    if (currentTITacticCodeList != null) {
                        Iterator iter = currentTITacticCodeList.iterator();
                        while (iter.hasNext()) {
                            TacticCode ctc = (TacticCode) iter.next();
                            CodeDescObj tactic = instance.getTacticByCode(StringUtils.trimToEmpty(ctc.getTacticCode()));
                            if (tactic != null) {
                                currentTITacticCodes.add(tactic);
                            }
                        }
                    }

                    currentTISalesComments = renewalQuoteHeader.getSalesComments();

                    //Get the defualt business organization code
                    deafultBusinessOrg = renewalQuoteHeader.getBusOrgCode();
                    
                    CodeDescObj businessOrg = instance.getBusinessOrgByCode(deafultBusinessOrg);
	                if (businessOrg != null)
	                    businessOrgDesc = businessOrg.getCodeDesc();
	                
	                this.currBlockReminder = renewalQuoteHeader.getBlockRnwlReminder();
                }

                List tacticCodes = instance.getTacticList();
                if (tacticCodes != null) {
                    changeTITacticCodes = new ArrayList();
                    Iterator itr = tacticCodes.iterator();
                    while (itr.hasNext()) {
                        CodeDescObj codeDescObj = (CodeDescObj) itr.next();
                        boolean defaultFlag = false;
                        if (currentTITacticCodes != null && currentTITacticCodes.size() > 0) {
                            Iterator it = currentTITacticCodes.iterator();
                            while (it.hasNext()) {
                                CodeDescObj tactic = (CodeDescObj) it.next();
                                if (codeDescObj.getCode().equalsIgnoreCase(tactic.getCode())) {
                                    defaultFlag = true;
                                    break;
                                }
                            }
                        }
                        changeTITacticCodes.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(),
                                defaultFlag));
                    }
                }
                
                this.getQuoteSalesOddsInfo();
                
                setBusinessOrganization(instance.getBusinessOrgList());

            } catch (QuoteException e) {
                logContext.error(this, e.getMessage());
                throw new ViewBeanException();
            }
            
            if (StringUtils.isBlank(getQuoteTitle())&&isEditSalesInfo) {
                ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
                setQuoteTitle(context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                        DraftQuoteMessageKeys.UPDATE_SALES_INFO));
            }
            // Quote editors section
            setCurrentEditorsList(renewalQuote.getDelegatesList());
        }
        if (StringUtils.isBlank(deafultBusinessOrg))
            deafultBusinessOrg = (String) params.getParameter(DraftQuoteParamKeys.PARAM_DEFAULT_BUSORG);
        
        quoteComment = (QuoteTxt) params.getParameter(DraftQuoteParamKeys.PARAM_SI_DETAIL_COMMENTS);
        salesCmmntAttchmnts = (List) params.getParameter(DraftQuoteParamKeys.PARAM_ATTACHMENT_LIST);
        attchmntDwnldURL = HtmlUtil.getURLForAction(ActionKeys.DOWNLOAD_ATTACHMENT);
        
        detailComments = quoteComment == null ? "" : quoteComment.getQuoteText();
        if (detailComments == null)
            detailComments = "";
        detailCommentsId = quoteComment == null ? "" : String.valueOf(quoteComment.getQuoteTextId());
        
        if (salesCmmntAttchmnts != null) {
            for (int i = 0; i < salesCmmntAttchmnts.size(); i++) {
                QuoteAttachment attchmnt = (QuoteAttachment) salesCmmntAttchmnts.get(i);
                attchmnt.setRemoveURL(this.genRemoveAttachmentURL(attchmnt.getQuoteNumber(), attchmnt.getId()));
            }
        }
    }
    
    protected void checkSalesRepInfo(SalesRep salesRep) {
        if (StringUtils.isBlank(salesRep.getFullName())) {
            salesRep.getBluePageInfo();
        }
    }
    
    public void getQuoteSalesOddsInfo() throws QuoteException {
        quoteSalesStageCode = renewalQuoteHeader.getSalesStageCode();
        quoteCustReasCode = renewalQuoteHeader.getCustReasCode();
        quoteSalesOddsCode = renewalQuoteHeader.getRenwlQuoteSalesOddsOode();
        
        CacheProcess process = CacheProcessFactory.singleton().create();
        SalesOdds salesOdds = process.getSalesOddsByRenwlQuoteSalesOddsCode(quoteSalesOddsCode);
        if (salesOdds != null) {
            quoteSalesOddsCodeDscr = salesOdds.getRenwlQuoteSalesOddsCodeDesc();
            // if quote sales stage code is empty, set default value based on sales odds code.
            if (StringUtils.isBlank(quoteSalesStageCode))
                quoteSalesStageCode = salesOdds.getSalesStageCode();
            // if cust reason code is empty, set default value based on sales odds code.
            if (StringUtils.isBlank(quoteCustReasCode))
                quoteCustReasCode = salesOdds.getCustReasCode();
        }
        
        this.salesStageCodeList = process.getSalesStageCodeList();
        this.salesOddsList = process.getSalesOddsList();
        
        createSalesStageCodeOptions();
        createCustReasOptions();
        createCustReasOptionArray();
    }
    
    // Add for undo state, refresh sales stage code drop-down list & cust reason code drop-down list when undo happens
    public void setPostSalesOddsInfo(String salesStageCode, String custReasCode) {
        quoteSalesStageCode = salesStageCode;
        quoteCustReasCode = custReasCode;
        
        createSalesStageCodeOptions();
        createCustReasOptions();
    }
    
    public void createSalesStageCodeOptions() {
        salesStageCodeOptions = new ArrayList();
        
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String boDefaultOption = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                DraftQuoteMessageKeys.DEAFULT_OPTION);
        
        if (salesStageCodeList != null) {
            if(StringUtils.isBlank(quoteSalesStageCode)){
                salesStageCodeOptions.add(new SelectOptionImpl(boDefaultOption, "", true));
            } else {
                salesStageCodeOptions.add(new SelectOptionImpl(boDefaultOption, "", false));
            }
            Iterator soIter = salesStageCodeList.iterator();
            while (soIter.hasNext()) {
                CodeDescObj so = (CodeDescObj) soIter.next();
                if (so.getCode().equalsIgnoreCase(quoteSalesStageCode)) {
                    salesStageCodeOptions.add(new SelectOptionImpl(so.getCodeDesc(), so.getCode(), true));
                } else {
                    salesStageCodeOptions.add(new SelectOptionImpl(so.getCodeDesc(), so.getCode(), false));
                }

            }
        }
    }
    
    public void createCustReasOptions() {
        custReasOptions = new ArrayList();
        ArrayList custReasList = new ArrayList();
        
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        String selectOne = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.SELECT_ONE);
        String notApplicable = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                DraftQuoteMessageKeys.MSG_NOT_APPLICABLE);
        
        if (StringUtils.isNotBlank(quoteSalesStageCode)) {
            for (int i = 0; salesOddsList != null && i < salesOddsList.size(); i++) {
                SalesOdds salesOdds = (SalesOdds) salesOddsList.get(i);
                if (quoteSalesStageCode.equalsIgnoreCase(salesOdds.getSalesStageCode())
                        && StringUtils.isNotBlank(salesOdds.getCustReasCode())) {
                    custReasList.add(salesOdds);
                }
            }
        }
        
        if (custReasList.size() == 0) {
            custReasOptions.add(new SelectOptionImpl(notApplicable, "", false));
        }
        else if (custReasList.size() == 1) {
            SalesOdds salesOdds = (SalesOdds) custReasList.get(0);
            custReasOptions.add(new SelectOptionImpl(salesOdds.getCustReasCodeDesc(), salesOdds
                    .getCustReasCode(), true));
        }
        else {
            custReasOptions.add(new SelectOptionImpl(selectOne, "", false));
            for (int i = 0; i < custReasList.size(); i++) {
                SalesOdds salesOdds = (SalesOdds) custReasList.get(i);
                if (StringUtils.isNotBlank(quoteCustReasCode)
                        && quoteCustReasCode.equalsIgnoreCase(salesOdds.getCustReasCode())) {
                    custReasOptions.add(new SelectOptionImpl(salesOdds.getCustReasCodeDesc(), salesOdds
                            .getCustReasCode(), true));
                } else {
                    custReasOptions.add(new SelectOptionImpl(salesOdds.getCustReasCodeDesc(), salesOdds
                            .getCustReasCode(), false));
                }
            }
        }
    }
    
    public void createCustReasOptionArray() {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        String selectOne = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.SELECT_ONE);
        String notApplicable = appCtx.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                DraftQuoteMessageKeys.MSG_NOT_APPLICABLE);
        StringBuffer sb = new StringBuffer();
        HashMap map = new HashMap();
        
        // Group sales odds by sales stage code.
        for (int i = 0; salesOddsList != null && i < salesOddsList.size(); i++) {
            SalesOdds salesOdds = (SalesOdds) salesOddsList.get(i);
            ArrayList list = (ArrayList) map.get(salesOdds.getSalesStageCode());
            if (list == null) {
                list = new ArrayList();
                map.put(salesOdds.getSalesStageCode(), list);
            }
            if (StringUtils.isNotBlank(salesOdds.getCustReasCode()))
                list.add(salesOdds);
        }
        
        sb.append("[");
        sb.append("[[\"\", \"").append(notApplicable).append("\"]]");
        for (int i = 0; salesStageCodeList != null && i < salesStageCodeList.size(); i++) {
            CodeDescObj cdObj = (CodeDescObj) salesStageCodeList.get(i);
            ArrayList list = (ArrayList) map.get(cdObj.getCode());
            
            sb.append(",[");
            if (list == null || list.size() == 0) {
                sb.append("[\"\", \"").append(notApplicable).append("\"]");
            }
            else if (list.size() == 1) {
                SalesOdds salesOdds = (SalesOdds) list.get(0);
                sb.append("[\"").append(salesOdds.getCustReasCode()).append("\", \"");
                sb.append(salesOdds.getCustReasCodeDesc()).append("\"]");
            }
            else {
                sb.append("[\"\", \"").append(selectOne).append("\"]");
                for (int j = 0; j < list.size(); j++) {
                    SalesOdds salesOdds = (SalesOdds) list.get(j);
                    sb.append(",[\"").append(salesOdds.getCustReasCode()).append("\", \"");
                    sb.append(salesOdds.getCustReasCodeDesc()).append("\"]");
                }
            }
            sb.append("]");
        }
        sb.append("]");
        
        custReasOptionArray = sb.toString();
    }
    
    public Collection generateBusOrgOptions() {
        Collection collection = new ArrayList();
        Iterator itr = businessOrganization.iterator();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String boDefaultOption = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale,
                DraftQuoteMessageKeys.DEAFULT_OPTION);
        if (StringUtils.isBlank(deafultBusinessOrg)) {
            collection.add(new SelectOptionImpl(boDefaultOption, "", true));
        } else {
            collection.add(new SelectOptionImpl(boDefaultOption, "", false));
        }

        while (itr.hasNext()) {
            CodeDescObj codeDescObj = (CodeDescObj) itr.next();

            if (codeDescObj.getCode().equalsIgnoreCase(deafultBusinessOrg)) {
                collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), true));
            } else {
                collection.add(new SelectOptionImpl(codeDescObj.getCodeDesc(), codeDescObj.getCode(), false));
            }
        }
        return collection;
    }
    
    public Collection getSalesStageCodeOptions() {
        return this.salesStageCodeOptions;
    }
    
    public Collection getCustReasOptions() {
        return this.custReasOptions;
    }
    
    public String getCustReasOptionArray() {
        return this.custReasOptionArray;
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
     * @return Returns the addQuoteEditorURL.
     */
    public String getAddQuoteEditorURL() {
        String btnParams = this.genBtnParamsForAction(DraftQuoteActionKeys.POST_SALES_INFO_TAB,
                DlgtnActionKeys.ADD_QUOTE_DELEGATE, 
                DlgtnParamKeys.PARAM_TARGET_ACTION + "=" + DraftQuoteActionKeys.DISPLAY_SALES_INFO_TAB);
        return btnParams;
    }

    /**
     * @return Returns the businessOrganization.
     */
    public List getBusinessOrganization() {
        return businessOrganization;
    }

    /**
     * @return Returns the changeTITacticCodes.
     */
    public List getChangeTITacticCodes() {
        return changeTITacticCodes;
    }

    /**
     * @return Returns the cqEditorCountry.
     */
    public String getCqEditorCountry() {
        return notNullString(cqEditorCountry);
    }

    /**
     * @return Returns the cqEditorFullName.
     */
    public String getCqEditorFullName() {
        return notNullString(cqEditorFullName);
    }

    /**
     * @return Returns the cqEditorPhone.
     */
    public String getCqEditorPhone() {
        return notNullString(cqEditorPhone);
    }

    /**
     * @return Returns the currentEditorsList.
     */
    public List getCurrentEditorsList() {
        return currentEditorsList;
    }

    /**
     * @return Returns the currentTISalesComments.
     */
    public String getCurrentTISalesComments() {
        return notNullString(currentTISalesComments);
    }
    
    public String getQuoteSalesOddsCodeDscr() {
        return this.quoteSalesOddsCodeDscr;
    }

    /**
     * @return Returns the currentTITacticCodes.
     */
    public List getCurrentTITacticCodes() {
        return currentTITacticCodes;
    }

    /**
     * @return Returns the currentTIUpsideTransaction.
     */
    public String getCurrentTIUpsideTransaction() {
        return notNullString(currentTIUpsideTransaction);
    }

    /**
     * @return Returns the isFullyEditable.
     */
    public boolean isFullyEditable() {
        return isFullyEditable;
    }

    /**
     * @return Returns the ooCountry.
     */
    public String getOoCountry() {
        return notNullString(ooCountry);
    }

    /**
     * @return Returns the ooEmail.
     */
    public String getOoEmail() {
        return notNullString(ooEmail);
    }

    /**
     * @return Returns the ooFullName.
     */
    public String getOoFullName() {
        return notNullString(ooFullName);
    }

    /**
     * @return Returns the ooPhone.
     */
    public String getOoPhone() {
        return notNullString(ooPhone);
    }

    /**
     * @param changeTISalesOdds
     *            The changeTISalesOdds to set.
     */
    public void setChangeTISalesOdds(List changeTISalesOdds) {
        this.changeTISalesOdds = changeTISalesOdds;
    }

    /**
     * @param cqEditorEmail
     *            The cqEditorEmail to set.
     */
    public void setCqEditorEmail(String cqEditorEmail) {
        this.cqEditorEmail = cqEditorEmail;
    }

    /**
     * @return Returns the cqEditorEmail.
     */
    public String getCqEditorEmail() {
        return notNullString(cqEditorEmail);
    }

    /**
     * @param businessOrganization
     *            The businessOrganization to set.
     */
    public void setBusinessOrganization(List businessOrganization) {
        this.businessOrganization = businessOrganization;
    }

    /**
     * @param changeTITacticCodes
     *            The changeTITacticCodes to set.
     */
    public void setChangeTITacticCodes(List changeTITacticCodes) {
        this.changeTITacticCodes = changeTITacticCodes;
    }

    /**
     * @param cqEditorCountry
     *            The cqEditorCountry to set.
     */
    public void setCqEditorCountry(String cqEditorCountry) {
        this.cqEditorCountry = cqEditorCountry;
    }

    /**
     * @param cqEditorFullName
     *            The cqEditorFullName to set.
     */
    public void setCqEditorFullName(String cqEditorFullName) {
        this.cqEditorFullName = cqEditorFullName;
    }

    /**
     * @param cqEditorPhone
     *            The cqEditorPhone to set.
     */
    public void setCqEditorPhone(String cqEditorPhone) {
        this.cqEditorPhone = cqEditorPhone;
    }

    /**
     * @param currentEditorsList
     *            The currentEditorsList to set.
     */
    public void setCurrentEditorsList(List currentEditorsList) {
        for (int i = 0; currentEditorsList != null && i < currentEditorsList.size(); i++) {
            SalesRep salesRep = (SalesRep) currentEditorsList.get(i);
            checkSalesRepInfo(salesRep);
        }
        this.currentEditorsList = currentEditorsList;
    }

    /**
     * @param currentTISalesComments
     *            The currentTISalesComments to set.
     */
    public void setCurrentTISalesComments(String currentTISalesComments) {
        this.currentTISalesComments = currentTISalesComments;
    }

    /**
     * @param currentTITacticCodes
     *            The currentTITacticCodes to set.
     */
    public void setCurrentTITacticCodes(List currentTITacticCodes) {
        this.currentTITacticCodes = currentTITacticCodes;
    }

    /**
     * @param currentTIUpsideTransaction
     *            The currentTIUpsideTransaction to set.
     */
    public void setCurrentTIUpsideTransaction(String currentTIUpsideTransaction) {
        this.currentTIUpsideTransaction = currentTIUpsideTransaction;
    }

    /**
     * @param isFullyEditable
     *            The isFullyEditable to set.
     */
    public void setFullyEditable(boolean isFullyEditable) {
        this.isFullyEditable = isFullyEditable;
    }

    /**
     * @param ooCountry
     *            The ooCountry to set.
     */
    public void setOoCountry(String ooCountry) {
        this.ooCountry = ooCountry;
    }

    /**
     * @param ooEmail
     *            The ooEmail to set.
     */
    public void setOoEmail(String ooEmail) {
        this.ooEmail = ooEmail;
    }

    /**
     * @param ooFullName
     *            The ooFullName to set.
     */
    public void setOoFullName(String ooFullName) {
        this.ooFullName = ooFullName;
    }

    /**
     * @param ooPhone
     *            The ooPhone to set.
     */
    public void setOoPhone(String ooPhone) {
        this.ooPhone = ooPhone;
    }

    /**
     * @return Returns the isEditSalesInfo.
     */
    public boolean isEditSalesInfo() {
        return isEditSalesInfo;
    }

    /**
     * @param isEditSalesInfo
     *            The isEditSalesInfo to set.
     */
    public void setEditSalesInfo(boolean isEditSalesInfo) {
        this.isEditSalesInfo = isEditSalesInfo;
    }

    /**
     * @return Returns the quoteTitle.
     */
    public String getQuoteTitle() {
        return notNullString(quoteTitle);
    }
    
    
    public String getBusinessOrgDesc() {
        return notNullString(businessOrgDesc);
    }

    /**
     * @param quoteTitle
     *            The quoteTitle to set.
     */
    public void setQuoteTitle(String quoteTitle) {
        this.quoteTitle = quoteTitle;
    }

    /**
     * @return Returns the quoteFullDesc.
     */
    public String getQuoteFullDesc() {
        return notNullString(quoteFullDesc);
    }

    /**
     * @param quoteFullDesc
     *            The quoteFullDesc to set.
     */
    public void setQuoteFullDesc(String quoteFullDesc) {
        this.quoteFullDesc = quoteFullDesc;
    }

    /**
     * @return Returns the oppEmail.
     */
    public String getOppEmail() {
        return notNullString(oppEmail);
    }

    /**
     * @param oppEmail
     *            The oppEmail to set.
     */
    public void setOppEmail(String oppEmail) {
        this.oppEmail = oppEmail;
    }
    
    /**
     * @return true if user is Submitter/Approver
     */
    public boolean isDisplayQuoteEditors() {
        int accessLevel = user.getAccessLevel(QuoteConstants.APP_CODE_SQO);

        if((accessLevel == QuoteConstants.ACCESS_LEVEL_SUBMITTER) || (accessLevel == QuoteConstants.ACCESS_LEVEL_APPROVER)) {
            isDisplayQuoteEditors = true;
        }
        return isDisplayQuoteEditors;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DraftRQBaseViewBean#getDisplayTabAction()
     */
    public String getDisplayTabAction() {
        return DraftQuoteActionKeys.DISPLAY_SALES_INFO_TAB;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DraftRQBaseViewBean#getPostTabAction()
     */
    public String getPostTabAction() {
        return DraftQuoteActionKeys.POST_SALES_INFO_TAB;
    }
    
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("isFullyEditable = ").append(isFullyEditable).append("\n");  
        buffer.append("isEditSalesInfo = ").append(isEditSalesInfo).append("\n");  
        return buffer.toString();
    }
    
    public boolean isFCTQuote() {
        return renewalQuoteHeader.isFCTQuote();
    }
    
    public String getDetailCommentsId() {
        return detailCommentsId;
    }
    
    public String getDetailComments() {
        return detailComments;
    }
    
    public void setDetailComments(String dtlCmmnts) {
        this.detailComments = dtlCmmnts;
    }
    
    public List getSalesCmmntAttchmnts() {
        return salesCmmntAttchmnts;
    }
    
    public void setSalesCmmntAttachments(List attchs) {
        this.salesCmmntAttchmnts = attchs;
        if (salesCmmntAttchmnts != null) {
            for (int i = 0; i < salesCmmntAttchmnts.size(); i++) {
                QuoteAttachment attchmnt = (QuoteAttachment) salesCmmntAttchmnts.get(i);
                attchmnt.setRemoveURL(this.genRemoveAttachmentURL(attchmnt.getQuoteNumber(), attchmnt.getId()));
            }
        }
    }
    
    public String getAttchmntDwnldURL() {
        return attchmntDwnldURL;
    }
    
    protected String genRemoveAttachmentURL(String webQuoteNum, String seqNum) {
        String redirectURL = HtmlUtil.getURLForAction(DraftQuoteActionKeys.REMOVE_ATTACHMENT_ACTION);
        String btnParams = this.genBtnParams(this.getPostTabAction(), redirectURL, ParamKeys.PARAM_FORWARD_FLAG
                + "=true," + DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM + "=" + webQuoteNum + ","
                + DraftQuoteParamKeys.PARAM_ATTCH_SEQ_NUM + "=" + seqNum);
        return btnParams;
    }
    
    public String getRQSalesTrackingParams(){
        StringBuffer goBackURL = new StringBuffer();
        goBackURL.append(ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ActionHandlerKeys.JADE_ACTION_KEY)).append("=").append(getDisplayTabAction());
        
        String baseURL = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ApplicationProperties.RENEWAL_QUOTE_SALES_TRACKING_URL);
        StringBuffer rqSalesTrackURL = new StringBuffer(baseURL);
        HtmlUtil.addURLParam(rqSalesTrackURL, DraftRQParamKeys.PARAM_RPT_QUOTE_NUM, renewalQuoteNum);
        HtmlUtil.addURLParam(rqSalesTrackURL, DraftRQParamKeys.PARAM_RPT_P1, goBackURL.toString());
        String btnParams = genBtnParams(this.getPostTabAction(), rqSalesTrackURL.toString() , null);

        return btnParams;
    }
    
    public boolean isDispBlockReminder(){
        return getQuote().getQuoteHeader().isRenewalQuote() 
                  &&!getQuote().getQuoteHeader().isPPSSQuote();
    }

    /**
     * @return Returns the currBlockReminder.
     */
    public int getCurrBlockReminder() {
        return currBlockReminder;
    }
    /**
     * @param currBlockReminder The currBlockReminder to set.
     */
    public void setCurrBlockReminder(int currBlockReminder) {
        this.currBlockReminder = currBlockReminder;
    }
}
