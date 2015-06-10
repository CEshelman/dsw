package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;

import com.ibm.dsw.quote.common.domain.DeployModel;
import com.ibm.dsw.quote.common.domain.Part;
import com.ibm.dsw.quote.common.domain.QuoteLineItem_Impl;
import com.ibm.dsw.quote.common.domain.YTYGrowth;
import com.ibm.dsw.quote.common.util.CheckPersisterUtil;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code><code> class.
 *
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 *
 * Creation date: Mar 4, 2007
 *
 * $Log: QuoteLineItem_jdbc.java,v $
 * Revision 1.48  2010/06/30 07:47:09  wxiaoli
 * SQR : Pushpa's area : Update web_quote sps : Need to put current quote user as mod_by, RTC 3407, reviewed by Will
 *
 * Revision 1.47  2010/06/25 08:58:24  changwei
 * Task 3334 SQO date logic : Enhance parts and pricing tab to derive 2 new line item attributes additionally
 * Reviewed by Vivian.
 *
 * Revision 1.46  2010/06/22 11:10:05  wxiaoli
 * BidIteration : DSQ02f: Display draft sales quote parts & pricing tab in bid iteration mode, RTC 3399, reviewed by Will
 *
 * Revision 1.45  2010/05/27 07:46:05  wxiaoli
 * contract level prcing flag for Display parts & pricing tab, RTC 3211,3212,3213,3214,3215,3216,3217, reviewed by Will
 *
 */
public class QuoteLineItem_jdbc extends QuoteLineItem_Impl implements PersistentObject, Serializable {

    /**
	 *
	 */
	private static final long serialVersionUID = -1464935964324860450L;

	transient Persister persister;

	private transient String userID;
	
	public QuoteLineItem_jdbc(){
		
	}

    /**
     *
     */
    public QuoteLineItem_jdbc(String webQuoteNum, Part part) {
        this.sQuoteNum = webQuoteNum;
        this.part = part;
        persister = new QuoteLineItemPersister(this);
    }
    
    
    public QuoteLineItem_jdbc(String webQuoteNum, String partNum) {
        this.sQuoteNum = webQuoteNum;
        this.part = new Part(partNum);
        persister = new QuoteLineItemPersister(this);
    }

    
    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#delete()
     */
    public void delete() throws TopazException {
        persister.isDeleted(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        persister.hydrate(connection);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    public void persist(Connection connection) throws TopazException {
        persister.persist(connection);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean deleteState) throws TopazException {
        persister.isDeleted(deleteState);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    public void isNew(boolean newState) throws TopazException {
        persister.isNew(newState);
    }
    
  
    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setAddtnlMaintCvrageQty(int)
     */
    public void setAddtnlMaintCvrageQty(int qty) throws TopazException {
    		CheckPersisterUtil.checkPersisterDirty(this.iAddtnlMaintCvrageQty, qty, persister);
    		this.iAddtnlMaintCvrageQty=qty;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setContributionExtUnitPts(double)
     */
    public void setContributionExtPts(double unitPts) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dContributionExtPts, unitPts, persister);	
    	this.dContributionExtPts=unitPts;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setContributionUnitPts(double)
     */
    public void setContributionUnitPts(double unitPts) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dContributionUnitPts, unitPts, persister);
    	this.dContributionUnitPts=unitPts;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setCtrctProgCode(java.lang.String)
     */
    public void setCtrctProgCode(String code) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.part.getsCtrctProgCode(), code, persister);
    	this.part.setsCtrctProgCode(code);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setCtrctProgCodeDesc(java.lang.String)
     */
    public void setCtrctProgCodeDesc(String codeDesc) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.part.getsCtrctProgCodeDesc(), codeDesc, persister);
    	this.part.setsCtrctProgCodeDesc(codeDesc);
    }

   /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setCUCode(java.lang.String)

    public void setCUCode(String code) throws TopazException {
        this.sCUCode = code;
        persister.setDirty();
    }


     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setCUCodeDscr(java.lang.String)

    public void setCUCodeDscr(String codeDesc) throws TopazException {
        this.sCUCodeDscr = codeDesc;
        persister.setDirty();
    }


     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setCUQty(int)

    public void setCUQty(int qty) throws TopazException {
        this.iCUQty = qty;
        persister.setDirty();
    }*/

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setPvuPart(boolean)
     */
    public void setPvuPart(boolean is) throws TopazException {
    		CheckPersisterUtil.checkPersisterDirty(this.part.isbIsPvuPart(), is, persister);
    		this.part.setbIsPvuPart(is);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setHasAssocdLicPart(boolean)
     */
    public void setAssocdLicPartFlag(boolean has) throws TopazException {
    		 CheckPersisterUtil.checkPersisterDirty(this.bHasAssocdLicPart, has, persister);
    		 this.bHasAssocdLicPart=has;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setHasExprsPart(boolean)
     */
    public void setHasExprsPart(boolean has) throws TopazException {
    	    CheckPersisterUtil.checkPersisterDirty(this.part.isbExprsPart(), has, persister);
    	    this.part.setbExprsPart(has);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setLicPartQty(int)
     */
    public void setLicPartQty(int qty) throws TopazException {
    	    CheckPersisterUtil.checkPersisterDirty(this.iLicPartQty, qty, persister);
    	    this.iLicPartQty=qty;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setLineDiscPct(double)
     */
    public void setLineDiscPct(double discPct) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dLineDiscPct, discPct, persister);
        this.dLineDiscPct=discPct;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setLocalExtPrc(double)
     */
    public void setLocalExtPrc(Double localExtPrc) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dLocalExtPrc, localExtPrc, persister);
        this.dLocalExtPrc=localExtPrc;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setLocalExtProratedDiscPrc(double)
     */
    public void setLocalExtProratedDiscPrc(Double localExtProratedDiscPrc) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dLocalExtProratedDiscPrc, localExtProratedDiscPrc, persister);
    	this.dLocalExtProratedDiscPrc=localExtProratedDiscPrc; 
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setLocalExtProratedPrc(double)
     */
    public void setLocalExtProratedPrc(Double localExtProratedPrc) throws TopazException {
    	 CheckPersisterUtil.checkPersisterDirty(this.dLocalExtProratedPrc, localExtProratedPrc, persister);
    	 this.dLocalExtProratedPrc=localExtProratedPrc;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setLocalUnitPrc(double)
     */
    public void setLocalUnitPrc(Double localUnitPrc) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dLocalUnitPrc, localUnitPrc, persister);
    	this.dLocalUnitPrc=localUnitPrc;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setLocalUnitProratedDiscPrc(double)
     */
    public void setLocalUnitProratedDiscPrc(Double localUnitProratedDiscPrc) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dLocalUnitProratedDiscPrc, localUnitProratedDiscPrc, persister);
    	this.dLocalUnitProratedDiscPrc=localUnitProratedDiscPrc;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setLocalUnitProratedPrc(double)
     */
    public void setLocalUnitProratedPrc(Double localUnitproratedPrc) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dLocalUnitProratedPrc, localUnitproratedPrc, persister);
    	this.dLocalUnitProratedPrc=localUnitproratedPrc;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setMaintEndDate(java.sql.Date)
     */
    public void setMaintEndDate(Date maintEndDate) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dtMaintEndDate, maintEndDate, persister);
    	this.dtMaintEndDate=maintEndDate;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setMaintStartDate(java.sql.Date)
     */
    public void setMaintStartDate(Date maintStartDate) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dtMaintStartDate, maintStartDate, persister);
    	this.dtMaintStartDate=maintStartDate;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setManualSortSeqNum(int)
     */
    public void setManualSortSeqNum(int seqNum) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.iManualSortSeqNum, seqNum, persister);
    	this.iManualSortSeqNum=seqNum;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setOverrideUnitPrc(double)
     */
    public void setOverrideUnitPrc(Double overrideUnitPrc) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dOverrideUnitPrc, overrideUnitPrc, persister);
    	this.dOverrideUnitPrc=overrideUnitPrc;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setPartEOLSet(boolean)
     */
    public void setPartEOLSet(boolean is) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.part.isbPartEOLSet(), is, persister);
    	this.part.setbPartEOLSet(is);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setPartNum(java.lang.String)
     */
    public void setPartNum(String partNum) throws TopazException {
    	 CheckPersisterUtil.checkPersisterDirty(this.part.getsPartNum(), partNum, persister);
    	 this.part.setsPartNum(partNum);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setPartQty(int)
     */
    public void setPartQty(Integer partQty) throws TopazException {
    		 CheckPersisterUtil.checkPersisterDirty(this.iPartQty, partQty, persister);
    		 this.iPartQty=partQty;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setPartRestrct(boolean)
     */
    public void setPartRestrct(boolean is) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.part.isbPartRestrct(), is, persister);
    	this.part.setbPartRestrct(is);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setPartTypeCode(java.lang.String)
     */
    public void setPartTypeCode(String code) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.part.getsPartTypeCode(), code, persister);
    	this.part.setsPartTypeCode(code);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setProrateQty(boolean)
     */
    public void setProrateFlag(boolean flag) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.bProrationFlag, flag, persister);
    	this.bProrationFlag=flag;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setPVUOverrideQtyIndCode(java.lang.String)
     */
    public void setPVUOverrideQtyIndCode(String code) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.sPVUOverrideQtyIndCode, code, persister);
    	this.sPVUOverrideQtyIndCode=code;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setQuoteNum(java.lang.String)
     */
    public void setQuoteNum(String quoteNum) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.sQuoteNum, quoteNum, persister);
    	this.sQuoteNum=quoteNum;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setRenewalQuoteEndDate(java.sql.Date)
     */
    public void setRenewalQuoteEndDate(Date renewalQuoteEndDate) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dtRenewalQuoteEndDate, renewalQuoteEndDate, persister);	
    	this.dtRenewalQuoteEndDate=renewalQuoteEndDate;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setRenewalQuoteNum(java.lang.String)
     */
    public void setRenewalQuoteNum(String quoteNum) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.sRenewalQuoteNum, quoteNum, persister);
    	this.sRenewalQuoteNum=quoteNum;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setRenewalQuoteSeqNum(int)
     */
    public void setRenewalQuoteSeqNum(int seqNum) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.iRenewalQuoteSeqNum, seqNum, persister);
    	this.iRenewalQuoteSeqNum=seqNum;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setRevnStrmCode(java.lang.String)
     */
    public void setRevnStrmCode(String code) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.part.getsRevnStrmCode(), code, persister);
    	this.part.setsRevnStrmCode(code);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setRevnStrmCodeDesc(java.lang.String)
     */
    public void setRevnStrmCodeDesc(String codeDesc) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.part.getsRevnStrmCodeDesc(), codeDesc, persister);	
    	this.part.setsRevnStrmCodeDesc(codeDesc);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setRoyalty(boolean)
     */
    public void setRoyalty(boolean is) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.part.isbRoyalty(), is, persister);
    	this.part.setbRoyalty(is);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setSeqNum(int)
     */
    public void setSeqNum(int seqNum) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.iSeqNum, seqNum, persister);
    	this.iSeqNum=seqNum;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setSwProdBrandCode(java.lang.String)
     */
    public void setSwProdBrandCode(String code) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.part.getsSwProdBrandCode(), code, persister);
    	this.part.setsSwProdBrandCode(code);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setSwProdBrandCodeDesc(java.lang.String)
     */
    public void setSwProdBrandCodeDesc(String codeDesc) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.part.getsSwProdBrandCodeDesc(), codeDesc, persister);	
    	this.part.setsSwProdBrandCodeDesc(codeDesc);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setSwSubId(java.lang.String)
     */
    public void setSwSubId(String id) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.part.getSwSubId(), id, persister);
    	this.part.setSwSubId(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setSystemSortSeqNum(int)
     */
    public void setQuoteSectnSeqNum(int seqNum) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.iQuoteSectnSeqNum, seqNum, persister);
    	this.iQuoteSectnSeqNum=seqNum;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setWwideProdCode(java.lang.String)
     */
    public void setWwideProdCode(String code) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.part.getsWwideProdCode(), code, persister);
    	this.part.setsWwideProdCode(code);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setWwideProdCodeDesc(java.lang.String)
     */
    public void setWwideProdCodeDesc(String codeDesc) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.part.getsWwideProdCodeDesc(), codeDesc, persister);	
    	this.part.setsWwideProdCodeDesc(codeDesc);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setStartDtOvrrdFlg(boolean)
     */
    public void setStartDtOvrrdFlg(boolean flag) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.bStartDtOvrrdFlg, flag, persister);
    	this.bStartDtOvrrdFlg=flag;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setEndDtOvrrdFlg(boolean)
     */
    public void setEndDtOvrrdFlg(boolean flag) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.bEndDtOvrrdFlg, flag, persister);
    	this.bEndDtOvrrdFlg=flag;
    }

    public void setRenwlChgCode(String code) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.sRenwlChgCode, code, persister);
    	this.sRenwlChgCode=code;
    }

    /**
     *
     */

    public void setChgType(String type) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.sChgType, type, persister);
    	this.sChgType=type;
    }
    
    /**
     *
     */

    public void setComment(String comment) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.sComment, comment, persister);
    	this.sComment=comment;
    }

    public void setChannelExtndPrice(Double price)throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dChannelExtndPrice, price, persister);
    	this.dChannelExtndPrice=price;
    }

    public void setChannelUnitPrice(Double price)throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dChannelUnitPrice, price, persister);
    	this.dChannelUnitPrice=price;
    }

    public void setLclExtndPriceIncldTax(Double tax) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dLclExtndPriceIncldTax, tax, persister);
    	this.dLclExtndPriceIncldTax=tax;
    }

    public void setLocalTaxAmt(Double tax) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dLocalTaxAmt, tax, persister);	
    	this.dLocalTaxAmt=tax;
    }

    public void setLclExtndChnlPriceIncldTax(Double tax) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dLclExtndChnlPriceIncldTax, tax, persister);	
    	this.dLclExtndChnlPriceIncldTax=tax;
    }

    public void setLocalChnlTaxAmt(Double tax) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.dLocalChnlTaxAmt, tax, persister);	
    	this.dLocalChnlTaxAmt=tax;
    }
    
    public void setPrevsStartDate(Date startDate) throws TopazException{
        this.dtPrevsStartDate = startDate;
    }

    public void setPrevsEndDate(Date endDate) throws TopazException{
        this.dtPrevsEndDate = endDate;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setAddtnlYearCvrageSeqNum(int)
     */
    public void setAddtnlYearCvrageSeqNum(int seqNum) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.iAddtnlYearCvrageSeqNum, seqNum, persister);	
    	this.iAddtnlYearCvrageSeqNum=seqNum;
    }
    public void setPersister(Persister p){
        this.persister = p;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setOvrrdExtPrice(java.lang.Double)
     */
    public void setOvrrdExtPrice(Double ovrrdExtPrice) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.ovrrdExtPrice, ovrrdExtPrice, persister);
    	this.ovrrdExtPrice=ovrrdExtPrice;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#setOfferIncldFlag(boolean)
     */
    public void setOfferIncldFlag(Boolean offerIncldFlag) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.offerIncldFlag, offerIncldFlag, persister);
    	this.offerIncldFlag=offerIncldFlag;
    }
    
    public void setDestSeqNum(int itmNum) throws TopazException{
    	CheckPersisterUtil.checkPersisterDirty(this.iDestSeqNum, itmNum, persister);	
    	this.iDestSeqNum=itmNum;
	}

    public void setBackDatingFlag(boolean backDatingFlag) throws TopazException{
    	CheckPersisterUtil.checkPersisterDirty(this.bBackDatingFlag, backDatingFlag, persister);	
    	this.bBackDatingFlag=backDatingFlag;
    }

    public void setChnlOvrrdDiscPct(Double chnlOvrrdDiscPct) throws TopazException{
    	CheckPersisterUtil.checkPersisterDirty(this.dChnlOvrrdDiscPct, chnlOvrrdDiscPct, persister);	
    	this.dChnlOvrrdDiscPct=chnlOvrrdDiscPct;
    }

    public void setChnlStdDiscPct(Double chnlStdDiscPct) throws TopazException{
    	CheckPersisterUtil.checkPersisterDirty(this.dChnlStdDiscPct, chnlStdDiscPct, persister);	
    	this.dChnlStdDiscPct=chnlStdDiscPct;
    }

    public void setTotDiscPct(Double totDiscPct) throws TopazException{
    	CheckPersisterUtil.checkPersisterDirty(this.dTotDiscPct, totDiscPct, persister);	
    	this.dTotDiscPct=totDiscPct;
    }

    public void setSalesQuoteRefFlag(boolean salesQuoteRefFlag) throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.bSalesQuoteRefFlag, salesQuoteRefFlag, persister);	
    	this.bSalesQuoteRefFlag=salesQuoteRefFlag;
    }

    public void setManualProratedLclUnitPriceFlag(int manualProratedLclUnitPriceFlag) throws TopazException{
    	CheckPersisterUtil.checkPersisterDirty(this.iManualProratedLclUnitPriceFlag, manualProratedLclUnitPriceFlag, persister);	
    	this.iManualProratedLclUnitPriceFlag=manualProratedLclUnitPriceFlag;
    }

	public void setCmprssCvrageMonth(Integer cmprssCvrageMonth) throws TopazException{
		CheckPersisterUtil.checkPersisterDirty(this.iCmprssCvrageMonth, cmprssCvrageMonth, persister);	
		this.iCmprssCvrageMonth=cmprssCvrageMonth;
	}

	public void setCmprssCvrageDiscPct(Double cmprssCvrageDiscPct) throws TopazException{
		CheckPersisterUtil.checkPersisterDirty(this.dCmprssCvrageDiscPct, cmprssCvrageDiscPct, persister);	
		this.dCmprssCvrageDiscPct=cmprssCvrageDiscPct;
	}
    /**
     * @return Returns the userID.
     */
    public String getUserID() {
        return userID;
    }
    /**
     * @param userID The userID to set.
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setLegacyBasePriceUsedFlag(boolean legacyBasePriceUsedFlag) throws TopazException{
    	CheckPersisterUtil.checkPersisterDirty(this.legacyBasePriceUsedFlag, legacyBasePriceUsedFlag, persister);	
    	this.legacyBasePriceUsedFlag=legacyBasePriceUsedFlag;
    }

    public void setIOriAddtnlMaintCvrageQty(int oriAddtnlMaintCvrageQty) {
        iOriAddtnlMaintCvrageQty = oriAddtnlMaintCvrageQty;
    }

    /**
     * @param relatedLineItmNum The iRelatedLineItmNum to set.
     */
    public void setIRelatedLineItmNum(int relatedLineItmNum) throws TopazException{
    	CheckPersisterUtil.checkPersisterDirty(iRelatedLineItmNum, relatedLineItmNum, persister);	
    	iRelatedLineItmNum=relatedLineItmNum;
    }

    /**
     * @param partType The sPartType to set.
     */
    public void setSPartType(String partType)throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.part.getsPartType(), partType, persister);	 
    	this.part.setsPartType(partType);
    }

    public void setSModByUserID(String modByUserID)throws TopazException {
    	CheckPersisterUtil.checkPersisterDirty(this.sModByUserID, modByUserID, persister);	
    	this.sModByUserID=modByUserID;
    }


	public void setSaasBidTCV(Double saasBidTCV)  throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.saasBidTCV, saasBidTCV, persister);
		this.saasBidTCV=saasBidTCV;
	}

	public void setICvrageTerm(Integer contractTerm)  throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(iCvrageTerm, contractTerm, persister);	
		iCvrageTerm=contractTerm;
	}

	public void setBillgFrqncyCode(String billingFrequency)  throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(billgFrqncyCode, billingFrequency, persister);	
		billgFrqncyCode=billingFrequency;
	}

	public void setConfigrtnId(String configrtnId)  throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.configrtnId, configrtnId, persister);	
		this.configrtnId=configrtnId;
	}

	public void setReplacedPart(boolean repacedPart)  throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.replacedPart, repacedPart, persister);	
		this.replacedPart=repacedPart;
	}

	public void setRefDocLineNum(Integer refDocLineNum)  throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.refDocLineNum, refDocLineNum, persister);	
		this.refDocLineNum=refDocLineNum;
	}

	public void setRelatedCotermLineItmNum(Integer relatedCotermLineItmNum)  throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.relatedCotermLineItmNum, relatedCotermLineItmNum, persister);	
		this.relatedCotermLineItmNum=relatedCotermLineItmNum;
	}

	public void setRelatedAlignLineItmNum(Integer relatedAlignLineItmNum)  throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.relatedAlignLineItmNum, relatedAlignLineItmNum, persister);	
		this.relatedAlignLineItmNum=relatedAlignLineItmNum;
	}

	public void setRampUp(boolean rampUp)  throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.rampUp, rampUp, persister);	
		this.rampUp=rampUp;
	}



	public void setCumCvrageTerm(Integer cumCvrageTerm)  throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.cumCvrageTerm, cumCvrageTerm, persister);	
		this.cumCvrageTerm=cumCvrageTerm;
	}

	public void setSaasBpTCV(Double saasBpTCV) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.saasBpTCV, saasBpTCV, persister);	
		this.saasBpTCV=saasBpTCV;
	}

	public void setSaasRenwl(boolean saasRenwl)throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.saasRenwl, saasRenwl, persister);	
		this.saasRenwl=saasRenwl;
	}

	public void setWebMigrtdDocFlag(boolean webMigrtdDocFlag) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.webMigrtdDocFlag, webMigrtdDocFlag, persister);	
		this.webMigrtdDocFlag=webMigrtdDocFlag;
	}



	public void setMachineType(String machineType) throws TopazException{
		CheckPersisterUtil.checkPersisterDirty(this.machineType, machineType, persister);	
		this.machineType=machineType;
	}

	public void setModel(String model) throws TopazException{
		CheckPersisterUtil.checkPersisterDirty(this.model, model, persister);	
		this.model=model;
	}

	public void setSerialNumber(String serialNumber) throws TopazException{
		CheckPersisterUtil.checkPersisterDirty(this.serialNumber, serialNumber, persister);	
		this.serialNumber=serialNumber;
	}

	public void setApplncPocInd(String applncPocInd) throws TopazException{
		CheckPersisterUtil.checkPersisterDirty(this.applncPocInd, applncPocInd, persister);	
		this.applncPocInd=applncPocInd;
	}

	public void setApplncPriorPoc(String applncPriorPoc) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.applncPriorPoc, applncPriorPoc, persister);	
		this.applncPriorPoc=applncPriorPoc;
	}
	//FCT TO PA Finalization
	public void setOrignlSalesOrdRefNum(String orignlSalesOrdRefNum) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.orignlSalesOrdRefNum, orignlSalesOrdRefNum, persister);	
		this.orignlSalesOrdRefNum=orignlSalesOrdRefNum;
	}
	public void setOrignlConfigrtnId(String orignlConfigrtnId) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.orignlConfigrtnId, orignlConfigrtnId, persister);	
		this.orignlConfigrtnId=orignlConfigrtnId;
	}
	public void setEarlyRenewalCompDate(Date earlyRenewalCompDate) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.earlyRenewalCompDate, earlyRenewalCompDate, persister);	
		this.earlyRenewalCompDate=earlyRenewalCompDate;
	}
	
	public void setExtensionEligibilityDate(Date extensionEligibilityDate) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.extensionEligibilityDate, extensionEligibilityDate, persister);	
		this.extensionEligibilityDate=extensionEligibilityDate;
	}
	
	public void setYtyGrowth(YTYGrowth ytyGrowth) throws TopazException {
		this.ytyGrowth = ytyGrowth;
	}
	
	//Appliance#99
	public void setLineItemCRAD(Date lineItemCRAD) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.custReqArrvDate, lineItemCRAD, persister);	
		this.custReqArrvDate=lineItemCRAD;
	}
	

	public void setNonIBMModel(String nonIBMModel) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.nonIBMModel, nonIBMModel, persister);	
		this.nonIBMModel=nonIBMModel;
	}


	public void setNonIBMSerialNumber(String nonIBMSerialNumber) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.nonIBMSerialNumber, nonIBMSerialNumber, persister);	
		this.nonIBMSerialNumber=nonIBMSerialNumber;
	}
	
    public void setDeployModelOption(int deployModelOption) throws TopazException {
    	DeployModel deployModel = getDeployModel();
		CheckPersisterUtil.checkPersisterDirty(deployModel.getDeployModelOption(), deployModelOption, persister);
		deployModel.setDeployModelOption(deployModelOption);
    }
    
    public void setDeployModelId(String deployModelId) throws TopazException {
    	DeployModel deployModel = getDeployModel();
    	CheckPersisterUtil.checkPersisterDirty(deployModel.getDeployModelId(), deployModelId, persister);
        deployModel.setDeployModelId(deployModelId);
    }
    
    public void setDeployModelInvalid(boolean deployModelInvalid) throws TopazException {
    	DeployModel deployModel = getDeployModel();
    	CheckPersisterUtil.checkPersisterDirty(deployModel.isDeployModelInvalid(), deployModelInvalid, persister);
        deployModel.setDeployModelInvalid(deployModelInvalid);
    }

	public void setAddReasonCode(String addReasonCode) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.addReasonCode, addReasonCode, persister);
		this.addReasonCode = addReasonCode;
	}

	public void setReplacedReasonCode(String replacedReasonCode) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.replacedReasonCode, replacedReasonCode, persister);
		this.replacedReasonCode = replacedReasonCode;
	}

	public void setNewConfigFlag(String newConfigFlag) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.newConfigFlag, newConfigFlag, persister);
		this.newConfigFlag = newConfigFlag;
	}

	public void setOriginatingItemNum(Integer originatingItemNum) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.originatingItemNum, originatingItemNum, persister);
		this.originatingItemNum = originatingItemNum;
	}

	@Override
	public void setIsNewService(Boolean isNewService) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.isNewService, isNewService, persister);
		this.isNewService = isNewService;
	}
	
	//15.3 judge if this part is a hybird part
	public void setPartSubType(String partSubType) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.partSubType, partSubType, persister);
		this.partSubType = partSubType;
	}

}
