package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;

import com.ibm.dsw.quote.common.domain.YTYGrowth;
import com.ibm.dsw.quote.common.util.CheckPersisterUtil;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

public class YTYGrowth_jdbc extends YTYGrowth implements PersistentObject,Serializable {
	
	private static final long serialVersionUID = -1562400462600213104L;
	transient YTYGrowthPersister persister = new YTYGrowthPersister(this);
	
	public YTYGrowth_jdbc() {
		this.ytySourceCode = "DEFAULT";
	}
	
	public YTYGrowth_jdbc(String webQuoteNum, int lineItemSeqNum) throws TopazException {
		this.setQuoteNum(webQuoteNum);
		this.setLineItemSeqNum(lineItemSeqNum);
		this.ytySourceCode = "DEFAULT";
		persister = new YTYGrowthPersister(this);
	}

	@Override
	public void isDeleted(boolean deleteState) throws TopazException {
		persister.isDeleted(deleteState);
	}

	@Override
	public void isNew(boolean newState) throws TopazException {
		persister.isNew(newState);
	}

	@Override
	public void hydrate(Connection connection) throws TopazException {
		persister.hydrate(connection);
	}

	@Override
	public void persist(Connection connection) throws TopazException {
		persister.persist(connection);
	}

	public void setPersister(YTYGrowthPersister persister) {
		this.persister = persister;
	}

	public void setIncludedInImpliedYTYGrowthFlag(int includedInImpliedYTYGrowthFlag) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.includedInImpliedYTYGrowthFlag, includedInImpliedYTYGrowthFlag, persister);
		this.includedInImpliedYTYGrowthFlag = includedInImpliedYTYGrowthFlag;
	}

	public void setIncludedInOverallYTYGrowthFlag(int includedInOverallYTYGrowthFlag) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.includedInOverallYTYGrowthFlag, includedInOverallYTYGrowthFlag, persister);
		this.includedInOverallYTYGrowthFlag = includedInOverallYTYGrowthFlag;
	}

	public void setYTYGrowthPct(Double yTYGrowthPct) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.YTYGrowthPct, yTYGrowthPct, persister);
		YTYGrowthPct = yTYGrowthPct;
	}

	public void setManualLPP(Double manualLPP) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.manualLPP, manualLPP, persister);
		this.manualLPP = manualLPP;
	}

	public void setManualLPPCustNum(String manualLPPCustNum)throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.manualLPPCustNum, manualLPPCustNum, persister);
		this.manualLPPCustNum = manualLPPCustNum;
	}

	public void setManualLPPPartNum(String manualLPPPartNum)throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.manualLPPPartNum, manualLPPPartNum, persister);
		this.manualLPPPartNum = manualLPPPartNum;
	}

	public void setYtyGrwothRadio(String ytyGrwothRadio) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.ytyGrwothRadio, ytyGrwothRadio, persister);
		this.ytyGrwothRadio = ytyGrwothRadio;
	}

	public void setYtySourceCode(String ytySourceCode) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.ytySourceCode, ytySourceCode, persister);
		this.ytySourceCode = ytySourceCode;
	}

	public void setRenwlQliQty(Integer renwlQliQty) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.renwlQliQty, renwlQliQty, persister);
		this.renwlQliQty = renwlQliQty;
	}

	public void setSapSalesOrdNum(String sapSalesOrdNum) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.sapSalesOrdNum, sapSalesOrdNum, persister);
		this.sapSalesOrdNum = sapSalesOrdNum;
	}

	public void setPriorQuoteNum(String priorQuoteNum) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.priorQuoteNum, priorQuoteNum, persister);
		this.priorQuoteNum = priorQuoteNum;
	}

	public void setPartQty(Integer partQty) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.partQty, partQty, persister);
		this.partQty = partQty;
	}

	public void setLppMissReas(String lppMissReas) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.lppMissReas, lppMissReas, persister);
		this.lppMissReas = lppMissReas;
	}

	public void setJustTxt(String justTxt) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.justTxt, justTxt, persister);
		this.justTxt = justTxt;;
	}

	public void setYtyUnitPrice(Double ytyUnitPrice){
		this.ytyUnitPrice = ytyUnitPrice;
	}

	public void setYtyBidUnitPrice(Double ytyBidUnitPrice){
		this.ytyBidUnitPrice = ytyBidUnitPrice;
	}

	public void setDiscOffRsvpUnit(double discOffRsvpUnit) {
		this.discOffRsvpUnit = discOffRsvpUnit;
	}

	public void setDiscOffRsvpBidUnit(double discOffRsvpBidUnit){
		this.discOffRsvpBidUnit = discOffRsvpBidUnit;
	}

	@Override
	public void setSysComputedPriorPrice(Double sysComputedPriorPrice)
			throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.sysComputedPriorPrice, sysComputedPriorPrice, persister);
		this.sysComputedPriorPrice = sysComputedPriorPrice;
		
	}

	@Override
	public void delete() throws TopazException {
		persister.isDeleted(true);
	}
}
