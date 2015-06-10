package com.ibm.dsw.quote.ps.domain;

import java.io.Serializable;

public class PartSearchService implements Serializable{
	
	private static final long serialVersionUID = 7929084999803904761L;
	private String prodId;
	private String prodIdDscr;
	private String prodBrandCode;
	private String prodBrandCodeDscr;
	private String prodSetCode;
	private String prodSetCodeDscr;
	private String prodGrpCode;
	private String prodGrpCodeDscr;
	
	public String getProdId() {
		return prodId;
	}
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	public String getProdIdDscr() {
		return prodIdDscr;
	}
	public void setProdIdDscr(String prodIdDscr) {
		this.prodIdDscr = prodIdDscr;
	}
	public String getProdBrandCodeDscr() {
		return prodBrandCodeDscr;
	}
	public void setProdBrandCodeDscr(String prodBrandCodeDscr) {
		this.prodBrandCodeDscr = prodBrandCodeDscr;
	}
	public String getProdSetCodeDscr() {
		return prodSetCodeDscr;
	}
	public void setProdSetCodeDscr(String prodSetCodeDscr) {
		this.prodSetCodeDscr = prodSetCodeDscr;
	}
	public String getProdGrpCodeDscr() {
		return prodGrpCodeDscr;
	}
	public void setProdGrpCodeDscr(String prodGrpCodeDscr) {
		this.prodGrpCodeDscr = prodGrpCodeDscr;
	}
	public String getProdBrandCode() {
		return prodBrandCode;
	}
	public void setProdBrandCode(String prodBrandCode) {
		this.prodBrandCode = prodBrandCode;
	}
	public String getProdSetCode() {
		return prodSetCode;
	}
	public void setProdSetCode(String prodSetCode) {
		this.prodSetCode = prodSetCode;
	}
	public String getProdGrpCode() {
		return prodGrpCode;
	}
	public void setProdGrpCode(String prodGrpCode) {
		this.prodGrpCode = prodGrpCode;
	}
}
