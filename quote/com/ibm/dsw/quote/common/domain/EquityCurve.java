package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>EquityCurve</code> class is Quote Line Item Domain.
 * 
 * 
 * @author <a href="luoyafei@cn.ibm.com">Grover </a> <br/>
 * 
 *         Creation date: 2013-4-19
 * 
 */
public class EquityCurve implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean equityCurveFlag; // EC_FLAG
	private boolean equityCurveCtrolFlag; // EC_DISC_CLCLTD_FLG
	private Double minDiscount; // EC_MIN_PCT
	private Double maxDiscount; // EC_MAX_PCT
	private String purchaseHistAppliedFlag; // EC_PRE_PURCHASE_HIST_APPLIED_FLAG

	public EquityCurve() {}

	public EquityCurve(boolean equityCurveFlag,boolean equityCurveCtrolFlag, Double minDiscount,
			Double maxDiscount, String purchaseHistAppliedFlag) {
		this.equityCurveFlag = equityCurveFlag;
		this.equityCurveCtrolFlag = equityCurveCtrolFlag;
		this.purchaseHistAppliedFlag = purchaseHistAppliedFlag;
		this.minDiscount = minDiscount;
		this.maxDiscount = maxDiscount;
	}

	public boolean isEquityCurveFlag() {
		return equityCurveFlag;
	}
	
	public boolean isEquityCurveCtrolFlag() {
		return equityCurveCtrolFlag;
	}

	public Double getMinDiscount() {
		return minDiscount;
	}

	public Double getMaxDiscount() {
		return maxDiscount;
	}

	public String getPurchaseHistAppliedFlag() {
		return purchaseHistAppliedFlag;
	}

	protected Double getMinBidUnitPrice(Double unitPrice) {
		return calculteEquityCurveUnitPrice(minDiscount,unitPrice);
	}

	protected Double getMinBidExtendedPrice(Integer quantity,Double unitPrice) {
		Double minBidUnitPrice = calculteEquityCurveUnitPrice(minDiscount,unitPrice);
		return calculteEquityCurveExtendedPrice(minBidUnitPrice, quantity);
	}

	protected Double getMaxBidUnitPrice(Double unitPrice) {
		return calculteEquityCurveUnitPrice(maxDiscount,unitPrice);
	}

	protected Double getMaxBidExendedPrice(Integer quantity,Double unitPrice) {
		Double maxBidUnitPrice = calculteEquityCurveUnitPrice(maxDiscount,unitPrice);
		return calculteEquityCurveExtendedPrice(maxBidUnitPrice, quantity);
	}

	@Override
	public String toString() {
		return "EquityCurve [equityCurveFlag=" + equityCurveFlag
		        + ", equityCurveCtrolFlag=" + equityCurveCtrolFlag
				+ ", purchaseHistAppliedFlag=" + purchaseHistAppliedFlag
				+ ", minDiscount=" + minDiscount + ", maxDiscount="
				+ maxDiscount + "]";
	}

	private static Double calculteEquityCurveUnitPrice(Double ecDiscount,Double unitPrice) {
		if (null == ecDiscount || null == unitPrice || ecDiscount < 0.00 || ecDiscount > 100.00) {
			return null;
		}
		BigDecimal discount = new BigDecimal(100.00).subtract(new BigDecimal(ecDiscount));
		BigDecimal result = discount.multiply(new BigDecimal(unitPrice)).divide(new BigDecimal(100));
		double dResult = result.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return Math.abs(dResult - 0.000) < 0.0000001 ? null : dResult;
	}

	private Double calculteEquityCurveExtendedPrice(Double unitPrice,Integer qty) {
		if (null == unitPrice || null == qty) {
			return null;
		}
		BigDecimal result = (new BigDecimal(unitPrice)).multiply(new BigDecimal(qty));
		double dResult =  result.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return Math.abs(dResult - 0.000) < 0.0000001 ? null : dResult;
	}

}