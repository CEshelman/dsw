package com.ibm.dsw.quote.common.domain;
import java.io.Serializable;

import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.topaz.exception.TopazException;
/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>YTYGrowth</code> class is Quote Line Item Domain.
 *
 *
 * @author <a href="junqingz@cn.ibm.com">Goshen Pan </a> <br/>
 *
 * Creation date: 2013-3-8
 *
 */


public abstract class YTYGrowth implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String quoteNum;// WEB_QUOTE_NUM
	public int    lineItemSeqNum;// QUOTE_LINE_ITEM_SEQ_NUM
	public int    includedInImpliedYTYGrowthFlag = 0; //IMPLD_INCLDD_FLG
	public int    includedInOverallYTYGrowthFlag = 0; //YTY_INCLDD_FLG
	public Double YTYGrowthPct; //YTY_GRWTH_PCT
	public Double manualLPP;  //LOCAL_UNIT_PRICE_LPP
	public String manualLPPCustNum; //SOLD_TO_CUST_NUM
	public String manualLPPPartNum; //PART_NUM
	public String ytyGrwothRadio; // GRWTH_DLGTN_COND_APPLD_BUTTON
	public String ytySourceCode; // YTY_SRC_CODE
	public Integer renwlQliQty; //RENWL_QUOTE_LINE_ITEM_QTY
	public String sapSalesOrdNum; //SAP_SALES_ORD_NUM
	public String priorQuoteNum;//PRIOR_QUOTE_NUM
	public Integer partQty;//PART_QTY
	public String lppMissReas;//LPP_MISSG_REAS
	public String justTxt;//LPP_MISSG_REAS_DSCR
	public Double ytyUnitPrice; //Annually Unit price for YTY
	public Double ytyBidUnitPrice;//Annually bid Unit price for YTY
	public double discOffRsvpUnit; //Discount Off RSVP/SRP based on YTY unit price
	public double discOffRsvpBidUnit; //Discount Off RSVP/SRP based on YTY bid unit price

	public Double sysComputedPriorPrice ; //LOCAL_UNIT_PRICE
	/**
	 * @return the includedInImpliedYTYGrowth
	 */
	public boolean isIncludedInImpliedYTYGrowth() {
		return includedInImpliedYTYGrowthFlag > 0;
	}
	/**
	 * @param includedInImpliedYTYGrowthFlag
	 * @param includedInOverallYTYGrowthFlag
	 * @param manualLPP
	 * @param manualLPPCustNum
	 * @param manualLPPPartNum
	 * @param ytyGrwothRadio
	 * @param ytySourceCode
	 * @param renwlQliQty
	 * @param sapSalesOrdNum
	 * @param priorQuoteNum
	 * @param partQty
	 * @param lppMissReas
	 * @param justTxt
	 */
	public YTYGrowth(
			int includedInImpliedYTYGrowthFlag,
			int includedInOverallYTYGrowthFlag,
			Double YTYGrowthPct,
			Double manualLPP,
			String manualLPPCustNum, 
			String manualLPPPartNum,
			String ytyGrwothRadio, 
			String ytySourceCode, 
			Integer renwlQliQty,
			String sapSalesOrdNum, 
			String priorQuoteNum, 
			Integer partQty,
			String lppMissReas, 
			String justTxt) {
		super();
		this.includedInImpliedYTYGrowthFlag = includedInImpliedYTYGrowthFlag;
		this.includedInOverallYTYGrowthFlag = includedInOverallYTYGrowthFlag;
		this.manualLPP = manualLPP;
		this.YTYGrowthPct = YTYGrowthPct;
		this.manualLPPCustNum = manualLPPCustNum;
		this.manualLPPPartNum = manualLPPPartNum;
		this.ytyGrwothRadio = ytyGrwothRadio;
		this.ytySourceCode = ytySourceCode;
		this.renwlQliQty = renwlQliQty;
		this.sapSalesOrdNum = sapSalesOrdNum;
		this.priorQuoteNum = priorQuoteNum;
		this.partQty = partQty;
		this.lppMissReas = lppMissReas;
		this.justTxt = justTxt;
	}
	/**
	 * 
	 */
	public YTYGrowth() {
		super();
		this.ytySourceCode = "DEFAULT";
	}
	
	
	public String getQuoteNum() {
		return quoteNum;
	}

	public void setQuoteNum(String quoteNum) {
		this.quoteNum = quoteNum;
	}

	public int getLineItemSeqNum() {
		return lineItemSeqNum;
	}

	public void setLineItemSeqNum(int lineItemSeqNum) {
		this.lineItemSeqNum = lineItemSeqNum;
	}

	/**
	 * @param includedInImpliedYTYGrowth
	 *            the includedInImpliedYTYGrowth to set
	 * @throws TopazException
	 */
	public abstract void setIncludedInImpliedYTYGrowthFlag(
			int includedInImpliedYTYGrowthFlag) throws TopazException;

	/**
	 * @return the includedInImpliedYTYGrowthFlag
	 */
	public int getIncludedInImpliedYTYGrowthFlag() {
		return includedInImpliedYTYGrowthFlag;
	}
	/**
	 * @return the includedInOverallYTYGrowth
	 */
	public boolean isIncludedInOverallYTYGrowth() {
		return includedInOverallYTYGrowthFlag > 0;
	}
	/**
	 * @param includedInOverallYTYGrowthFlag
	 *            the includedInOverallYTYGrowthFlag to set
	 * @throws TopazException
	 */
	public abstract void setIncludedInOverallYTYGrowthFlag(int includedInOverallYTYGrowthFlag) throws TopazException;

	/**
	 * @return the includedInImpliedYTYGrowthFlag
	 */
	public int getIncludedInOverallYTYGrowthFlag() {
		return includedInOverallYTYGrowthFlag;
	}
	/**
	 * @return the yTYGrowthPct
	 */
	public Double getYTYGrowthPct() {
		return YTYGrowthPct;
	}
	/**
	 * @param yTYGrowthPct
	 *            the yTYGrowthPct to set
	 * @throws TopazException
	 */
	public abstract void setYTYGrowthPct(Double yTYGrowthPct) throws TopazException;

	/**
	 * @return the manualLPP
	 */
	public Double getManualLPP() {
		return manualLPP;
	}
	/**
	 * @param manualLPP
	 *            the manualLPP to set
	 * @throws TopazException
	 */
	public abstract void setManualLPP(Double manualLPP) throws TopazException;

	/**
	 * @return the manualLPPCustNum
	 */
	public String getManualLPPCustNum() {
		return manualLPPCustNum;
	}
	/**
	 * @param manualLPPCustNum
	 *            the manualLPPCustNum to set
	 * @throws TopazException
	 */
	public abstract void setManualLPPCustNum(String manualLPPCustNum) throws TopazException ;

	/**
	 * @return the manualLPPPartNum
	 */
	public String getManualLPPPartNum() {
		return manualLPPPartNum;
	}
	/**
	 * @param manualLPPPartNum
	 *            the manualLPPPartNum to set
	 * @throws TopazException
	 */
	public abstract void setManualLPPPartNum(String manualLPPPartNum)throws TopazException;

	/**
	 * @return the ytyGrwothRadio
	 */
	public String getYtyGrwothRadio() {
		return ytyGrwothRadio;
	}
	/**
	 * @param ytyGrwothRadio
	 *            the ytyGrwothRadio to set
	 * @throws TopazException
	 */
	public abstract void setYtyGrwothRadio(String ytyGrwothRadio) throws TopazException;

	/**
	 * @return the ytySourceCode
	 */
	public String getYtySourceCode() {
		return ytySourceCode;
	}
	/**
	 * @param ytySourceCode
	 *            the ytySourceCode to set
	 * @throws TopazException
	 */
	public abstract void setYtySourceCode(String ytySourceCode) throws TopazException ;

	/**
	 * @return the renwlQliQty
	 */
	public Integer getRenwlQliQty() {
		return renwlQliQty;
	}
	/**
	 * @param renwlQliQty
	 *            the renwlQliQty to set
	 * @throws TopazException
	 */
	public abstract void setRenwlQliQty(Integer renwlQliQty) throws TopazException ;

	/**
	 * @return the sapSalesOrdNum
	 */
	public String getSapSalesOrdNum() {
		return sapSalesOrdNum;
	}
	/**
	 * @param sapSalesOrdNum
	 *            the sapSalesOrdNum to set
	 * @throws TopazException
	 */
	public abstract void setSapSalesOrdNum(String sapSalesOrdNum) throws TopazException;

	/**
	 * @return the priorQuoteNum
	 */
	public String getPriorQuoteNum() {
		return priorQuoteNum;
	}
	/**
	 * @param priorQuoteNum
	 *            the priorQuoteNum to set
	 * @throws TopazException
	 */
	public abstract void setPriorQuoteNum(String priorQuoteNum) throws TopazException ;

	/**
	 * @return the partQty
	 */
	public Integer getPartQty() {
		return partQty;
	}
	/**
	 * @param partQty
	 *            the partQty to set
	 * @throws TopazException
	 */
	public abstract void setPartQty(Integer partQty) throws TopazException ;

	/**
	 * @return the lppMissReas
	 */
	public String getLppMissReas() {
		return lppMissReas;
	}
	/**
	 * @param lppMissReas
	 *            the lppMissReas to set
	 * @throws TopazException
	 */
	public abstract void setLppMissReas(String lppMissReas) throws TopazException;

	/**
	 * @return the justTxt
	 */
	public String getJustTxt() {
		return justTxt;
	}
	/**
	 * @param justTxt
	 *            the justTxt to set
	 * @throws TopazException
	 */
	public abstract void setJustTxt(String justTxt) throws TopazException;
	
	public boolean isPctManuallyEntered(){
		return DraftQuoteParamKeys.YTY_RADIO_YTY_GROWTH_VALUE.equals(ytyGrwothRadio);
	}

	public Double getYtyUnitPrice() {
		return ytyUnitPrice;
	}
	
	public abstract void setYtyUnitPrice(Double ytyUnitPrice);

	public Double getYtyBidUnitPrice() {
		return ytyBidUnitPrice;
	}
	
	public abstract void setYtyBidUnitPrice(Double ytyBidUnitPrice);

	public double getDiscOffRsvpUnit() {
		return discOffRsvpUnit;
	}
	
	public abstract void setDiscOffRsvpUnit(double discOffRsvpUnit);

	public double getDiscOffRsvpBidUnit() {
		return discOffRsvpBidUnit;
	}
	
	public abstract void setDiscOffRsvpBidUnit(double discOffRsvpBidUnit);

	//if LPP is manually entered, return true
	//else return false
	public boolean isUnitPriceManuallyEntered(){
		if(DraftQuoteConstants.LPP_POPUP_SOURCE_OVRRD_LPP.equals(ytySourceCode)){
			return true;
		}
		return false;
	}
	public Double getSysComputedPriorPrice() {
		return sysComputedPriorPrice;
	}
	public abstract void setSysComputedPriorPrice(Double sysComputedPriorPrice) throws TopazException;
	
	public abstract void delete() throws TopazException;
	
	
}
