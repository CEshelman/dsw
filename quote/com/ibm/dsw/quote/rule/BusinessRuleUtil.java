package com.ibm.dsw.quote.rule;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

import com.ibm.dolphin.xrules.XRules;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.process.BusinessRuleProcess;
import com.ibm.dsw.quote.common.process.BusinessRuleProcessFactory;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2008 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>BusinessRuleUtil.java</code>
 * 
 * @author: Jeff zhu
 * 
 * Creation date: 2009-4-9
 */
public class BusinessRuleUtil implements Serializable  {

    private static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    private HashMap ruleMap = new HashMap();

    private Timestamp updateTime = null;
    
    private String xRules =null;
    
    public BusinessRuleUtil(String ruleSetId) throws QuoteException{
    	initUpdateRule(ruleSetId);
    }

    
    public void initUpdateRule(String ruleSetId) throws QuoteException {
    	BusinessRuleProcess xmlProcess = BusinessRuleProcessFactory.singleton().create();

        //TUSCANY_SP_BID_AND_SUBMIT
        if (xmlProcess.needUpdateRule(ruleSetId, updateTime)) {
            this.updateTime = xmlProcess.getLastUpdateTime();
            this.xRules = xmlProcess.getXrule();
            init(ruleSetId);
        }
    }

    public void init(String ruleSetId) {
        TimeTracer tracer = TimeTracer.newInstance();

        try {
            tracer.stmtTraceStart("Init Xrule.");
            XRules xrules = new XRules();
            xrules.setXML(new ByteArrayInputStream(xRules.getBytes()));
            xrules.init();
            if (getRuleMap().get(ruleSetId) != null)
                logContext.debug(this, "Rule set " + ruleSetId + " is to be updated.");
            getRuleMap().put(ruleSetId, xrules);
            tracer.stmtTraceEnd("Init Xrule.");
        } catch (Exception e) {
            LogContextFactory.singleton().getLogContext().error(this, e);
        } finally {
            tracer.dump();
        }
    }

    public Object executeRule(String ruleSetId, String ruleId, Object[] paramObj) throws QuoteException {

    	BusinessRuleProcess xmlProcess = BusinessRuleProcessFactory.singleton().create();

        //TUSCANY_SP_BID_AND_SUBMIT
        if (xmlProcess.needUpdateRule(ruleSetId, updateTime)) {
            this.updateTime = xmlProcess.getLastUpdateTime();
            this.xRules = xmlProcess.getXrule();
            init(ruleSetId);
        }

        Object result = null;
        if (ruleMap.isEmpty()) {
            logContext.debug(this, "Rule not initialized, please call init() first.");
        } else {
            if (!ruleMap.containsKey(ruleSetId)) {
                logContext.debug(this, "Rule set " + ruleSetId + " is not found.");
                return result;
            }

            XRules xrules = (XRules) getRuleMap().get(ruleSetId);
            result = xrules.execute(ruleId, paramObj);
        }
        return result;
    }

    /**
     * @return Returns the ruleMap.
     */
    public HashMap getRuleMap() {
        return ruleMap;
    }

    /**
     * @param ruleMap The ruleMap to set.
     */
    public void setRuleMap(HashMap ruleMap) {
        this.ruleMap = ruleMap;
    }
}
