package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code></code> class is 
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Jun 19, 2007
 */

public class ChangeAdditionalMaintContract extends PostDraftQuoteBaseContract {
	protected transient LogContext logContext = LogContextFactory.singleton().getLogContext();

    private String partNum ;
    private int seqNum;
    private int additionalYears = -1;
    //public Integer iPartQty;
    public int iManualSortSeqNum;
    //public Double dOverrideUnitPrc;
    //public double dLineDiscPct;
    public boolean bProrationFlag;
    private String sPartQty;
    private String sOverrideUnitPrc;
    private String sLineDiscPct;
    private String sKey;
    
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        logContext.debug(this,"PartNum="+parameters.getParameter(DraftQuoteParamKeys.PART_NUM));
        logContext.debug(this,"SeqNum="+parameters.getParameter(DraftQuoteParamKeys.SEQ_NUM));
        
        partNum = (String)parameters.getParameter(DraftQuoteParamKeys.PART_NUM);
        seqNum = Integer.parseInt((String)parameters.getParameter(DraftQuoteParamKeys.SEQ_NUM));
        additionalYears = Integer.parseInt((String)parameters.getParameter(partNum + "_" + seqNum + DraftQuoteParamKeys.maintainAddtionalYearSuffix));
        iManualSortSeqNum = Integer.parseInt((String)parameters.getParameter(partNum + "_" + seqNum + DraftQuoteParamKeys.specifySortOrderSuffix));
        sPartQty = (String)parameters.getParameter(partNum + "_" + seqNum + DraftQuoteParamKeys.quantitySuffix);
        sOverrideUnitPrc = (String)parameters.getParameter(partNum + "_" + seqNum + DraftQuoteParamKeys.overridePriceSuffix);
        sLineDiscPct = (String)parameters.getParameter(partNum + "_" + seqNum + DraftQuoteParamKeys.discountPriceSuffix);
        String sProrationFlag = (String)parameters.getParameter(partNum + "_" + seqNum + DraftQuoteParamKeys.prorateFirstYearToAnniSuffix);
        try{
        	bProrationFlag = Boolean.valueOf(sProrationFlag).booleanValue();
        }catch(Throwable e){
        	bProrationFlag = false;
        }
		
    }

    /**
     * @return Returns the additionalYears.
     */
    public int getAdditionalYears() {
        return additionalYears;
    }
    /**
     * @return Returns the partNum.
     */
    public String getPartNum() {
        return partNum;
    }
    /**
     * @return Returns the seqNum.
     */
    public int getSeqNum() {
        return seqNum;
    }
    /**
     * @return Returns the sPartQty.
     */
    public String getPartQty() {
        return sPartQty;
    }
    /**
     * @return Returns the iManualSortSeqNum.
     */
    public int getManualSortSeqNum() {
        return iManualSortSeqNum;
    }
    /**
     * @return Returns the sOverrideUnitPrc.
     */
    public String getOverrideUnitPrc() {
        return sOverrideUnitPrc;
    }
    /**
     * @return Returns the sLineDiscPct.
     */
    public String getLineDiscPct() {
        return sLineDiscPct;
    }
    /**
     * @return Returns the bProrationFlag.
     */
    public boolean getProrationFlag() {
        return bProrationFlag;
    }
    /**
     * @return Returns the key.
     */
    public String getKey() {
    	sKey = getPartNum() + "_" + getSeqNum();
        return sKey;
    }
    
}
