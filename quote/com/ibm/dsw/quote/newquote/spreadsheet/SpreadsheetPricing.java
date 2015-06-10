/*
 * Created on 2007-7-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.newquote.spreadsheet;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SpreadSheetPart.java</code> class.
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-7-17
 */
public class SpreadsheetPricing implements Comparable, Serializable {

	private String epPartNumber = "";

	private String revenueStream = "";

	private String svpLevelB;

	private String svpLevelA;

	private String svpLevelD;

	private String svpLevelE;

	private String svpLevelF;

	private String svpLevelG;

	private String svpLevelH;

	private String svpLevelI;

	private String svpLevelJ;

	private String svpLevelED;

	private String svpLevelGV;
	/**
	 * @return Returns the brandCode.
	 */
	public String getRevenueStream() {
		return revenueStream;
	}
	/**
	 * @param brandCode The brandCode to set.
	 */
	public void setRevenueStream(String revenueStream) {
		this.revenueStream = revenueStream;
	}
	/**
	 * @return Returns the epPartNumber.
	 */
	public String getEpPartNumber() {
		return epPartNumber;
	}
	/**
	 * @param epPartNumber The epPartNumber to set.
	 */
	public void setEpPartNumber(String epPartNumber) {
		this.epPartNumber = epPartNumber;
	}
	/**
	 * @return Returns the svpLevelA.
	 */
	public String getSvpLevelA() {
		return svpLevelA;
	}
	/**
	 * @param svpLevelA The svpLevelA to set.
	 */
	public void setSvpLevelA(String svpLevelA) {
		this.svpLevelA = svpLevelA;
	}
	/**
	 * @return Returns the svpLevelB.
	 */
	public String getSvpLevelB() {
		return svpLevelB;
	}
	/**
	 * @param svpLevelB The svpLevelB to set.
	 */
	public void setSvpLevelB(String svpLevelB) {
		this.svpLevelB = svpLevelB;
	}
	/**
	 * @return Returns the svpLevelD.
	 */
	public String getSvpLevelD() {
		return svpLevelD;
	}
	/**
	 * @param svpLevelD The svpLevelD to set.
	 */
	public void setSvpLevelD(String svpLevelD) {
		this.svpLevelD = svpLevelD;
	}
	/**
	 * @return Returns the svpLevelE.
	 */
	public String getSvpLevelE() {
		return svpLevelE;
	}
	/**
	 * @param svpLevelE The svpLevelE to set.
	 */
	public void setSvpLevelE(String svpLevelE) {
		this.svpLevelE = svpLevelE;
	}
	/**
	 * @return Returns the svpLevelED.
	 */
	public String getSvpLevelED() {
		return svpLevelED;
	}
	/**
	 * @param svpLevelED The svpLevelED to set.
	 */
	public void setSvpLevelED(String svpLevelED) {
		this.svpLevelED = svpLevelED;
	}
	/**
	 * @return Returns the svpLevelF.
	 */
	public String getSvpLevelF() {
		return svpLevelF;
	}
	/**
	 * @param svpLevelF The svpLevelF to set.
	 */
	public void setSvpLevelF(String svpLevelF) {
		this.svpLevelF = svpLevelF;
	}
	/**
	 * @return Returns the svpLevelG.
	 */
	public String getSvpLevelG() {
		return svpLevelG;
	}
	/**
	 * @param svpLevelG The svpLevelG to set.
	 */
	public void setSvpLevelG(String svpLevelG) {
		this.svpLevelG = svpLevelG;
	}
	/**
	 * @return Returns the svpLevelGV.
	 */
	public String getSvpLevelGV() {
		return svpLevelGV;
	}
	/**
	 * @param svpLevelGV The svpLevelGV to set.
	 */
	public void setSvpLevelGV(String svpLevelGV) {
		this.svpLevelGV = svpLevelGV;
	}
	/**
	 * @return Returns the svpLevelH.
	 */
	public String getSvpLevelH() {
		return svpLevelH;
	}
	/**
	 * @param svpLevelH The svpLevelH to set.
	 */
	public void setSvpLevelH(String svpLevelH) {
		this.svpLevelH = svpLevelH;
	}
	/**
	 * @return Returns the svpLevelI.
	 */
	public String getSvpLevelI() {
		return svpLevelI;
	}
	/**
	 * @param svpLevelI The svpLevelI to set.
	 */
	public void setSvpLevelI(String svpLevelI) {
		this.svpLevelI = svpLevelI;
	}
	/**
	 * @return Returns the svpLevelJ.
	 */
	public String getSvpLevelJ() {
		return svpLevelJ;
	}
	/**
	 * @param svpLevelJ The svpLevelJ to set.
	 */
	public void setSvpLevelJ(String svpLevelJ) {
		this.svpLevelJ = svpLevelJ;
	}
	
	
	 public String toXMLString() throws Exception {
        Map propsMap = BeanUtils.describe(this);
        StringBuffer bf = new StringBuffer("<pricingData ");

        Set keys = propsMap.entrySet();
        for (Iterator it = keys.iterator(); it.hasNext();) {
            java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
            bf.append(entry.getKey() + " =\"" +  StringEscapeUtils.escapeXml((String)entry.getValue()) + "\"  ");
        }
        bf.append("/>");
        return bf.toString();
    }
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		SpreadsheetPricing obj = (SpreadsheetPricing)o;
		return this.getEpPartNumber().compareTo(obj.getEpPartNumber());
	}
}
