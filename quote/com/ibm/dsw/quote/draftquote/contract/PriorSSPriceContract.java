package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * @author Jason
 */
public class PriorSSPriceContract extends PrepareAddLPPFormContract {
	private String ytySrcCode="DEFAULT";
	private double ytyGrwthPct;
	private int ytyInclddFlg = 0;
	private int impldInclddFlg = 0;
	private int renwlQliQty;
	private String localUnitPriceLpp;
	private String soldToCustNum;
	private String sapSalesOrdNum;
	private String priorQuoteNum;
	private String partNum;
	private String partQty;
	private String lppMissReas;
	private String justTxt;
	private String growthRadio = "0";
	private String renewalNum = "";
	private String gdPartFlag;

	public String getGrowthRadio() {
		return growthRadio;
	}

	public void setGrowthRadio(String growthRadio) {
		this.growthRadio = growthRadio;
	}

	public double getYtyGrwthPct() {
		return ytyGrwthPct;
	}

	public void setYtyGrwthPct(double ytyGrwthPct) {
		this.ytyGrwthPct = ytyGrwthPct;
	}

	public String getYtySrcCode() {
		return ytySrcCode;
	}

	public void setYtySrcCode(String ytySrcCode) {
		this.ytySrcCode = ytySrcCode;
	}

	public int getYtyInclddFlg() {
		return ytyInclddFlg;
	}

	public void setYtyInclddFlg(int ytyInclddFlg) {
		this.ytyInclddFlg = ytyInclddFlg;
	}

	public int getImpldInclddFlg() {
		return impldInclddFlg;
	}

	public void setImpldInclddFlg(int impldInclddFlg) {
		this.impldInclddFlg = impldInclddFlg;
	}

	public int getRenwlQliQty() {
		return renwlQliQty;
	}

	public void setRenwlQliQty(int renwlQliQty) {
		this.renwlQliQty = renwlQliQty;
	}

	public String getSoldToCustNum() {
		return soldToCustNum;
	}

	public void setSoldToCustNum(String soldToCustNum) {
		this.soldToCustNum = soldToCustNum;
	}

	public String getSapSalesOrdNum() {
		return sapSalesOrdNum;
	}

	public void setSapSalesOrdNum(String sapSalesOrdNum) {
		this.sapSalesOrdNum = sapSalesOrdNum;
	}

	public String getPriorQuoteNum() {
		return priorQuoteNum;
	}

	public void setPriorQuoteNum(String priorQuoteNum) {
		this.priorQuoteNum = priorQuoteNum;
	}

	public String getPartNum() {
		return partNum;
	}

	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}

	public String getLocalUnitPriceLpp() {
		return localUnitPriceLpp;
	}

	public void setLocalUnitPriceLpp(String localUnitPriceLpp) {
		this.localUnitPriceLpp = localUnitPriceLpp;
	}

	public String getPartQty() {
		return partQty;
	}

	public void setPartQty(String partQty) {
		this.partQty = partQty;
	}

	public String getLppMissReas() {
		return lppMissReas;
	}

	public void setLppMissReas(String lppMissReas) {
		this.lppMissReas = lppMissReas;
	}

	public String getJustTxt() {
		return justTxt;
	}

	public void setJustTxt(String justTxt) {
		this.justTxt = justTxt;
	}
	
	

	public String getRenewalNum() {
		return renewalNum;
	}

	public void setRenewalNum(String renewalNum) {
		this.renewalNum = renewalNum;
	}
	
	public String getGdPartFlag() {
		return gdPartFlag;
	}

	public void setGdPartFlag(String gdPartFlag) {
		this.gdPartFlag = gdPartFlag;
	}

	public void load(Parameters parameters, JadeSession session) {
		super.load(parameters, session);

	}

}
