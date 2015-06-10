package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>OmitRenewalLine</code> class is Quote header Domain.
 * 
 * 
 * @author <a href="luoyafei@cn.ibm.com">Grover </a> <br/>
 * 
 *         Creation date: 2013-6-26
 * 
 */
public class OmitRenewalLine implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean isOmittedLine;
	private int omittedLineRecalcFlag;
	private Double omittedLinePrice;

	public OmitRenewalLine() {
		super();
	}

	public OmitRenewalLine(boolean isOmittedLine, int omittedLineRecalcFlag,
			Double omittedLinePrice) {
		this.isOmittedLine = isOmittedLine;
		this.omittedLineRecalcFlag = omittedLineRecalcFlag;
		this.omittedLinePrice = omittedLinePrice;
	}

	public boolean isOmittedLine() {
		return isOmittedLine;
	}

	public void setOmittedLine(boolean isOmittedLine) {
		this.isOmittedLine = isOmittedLine;
	}

	public int getOmittedLineRecalcFlag() {
		return omittedLineRecalcFlag;
	}

	public void setOmittedLineRecalcFlag(int omittedLineRecalcFlag) {
		this.omittedLineRecalcFlag = omittedLineRecalcFlag;
	}

	public Double getOmittedLinePrice() {
		return omittedLinePrice;
	}

	public void setOmittedLinePrice(Double omittedLinePrice) {
		this.omittedLinePrice = omittedLinePrice;
	}

	@Override
	public String toString() {
		return "OmitRenewalLine [isOmittedLine=" + isOmittedLine
				+ ", omittedLineRecalcFlag=" + omittedLineRecalcFlag
				+ ", omittedLinePrice=" + omittedLinePrice + "]";
	}
	
}
