package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>EquityCurveTotal</code> class is Quote header Domain.
 * 
 * 
 * @author <a href="luoyafei@cn.ibm.com">Grover </a> <br/>
 * 
 *         Creation date: 2013-4-19
 * 
 */
public class EquityCurveTotal implements Serializable {
	private static final long serialVersionUID = 1L;
	  
	private Double weightAverageMin; // EC_WGHT_AVRG_MIN_PCT
	private Double weightAverageMax; // EC_WGHT_AVRG_MAX_PCT
	
	private Double totalBidUnitPriceMin;
	private Double totalBidUnitPriceMax;
	
	private Double totalBidExtendedPriceMin;
	private Double totalBidExtendedPriceMax;
	
	public EquityCurveTotal() {
	}

	public EquityCurveTotal(Double weightAverageMin, Double weightAverageMax) {
		this.weightAverageMin = weightAverageMin;
		this.weightAverageMax = weightAverageMax;
	}

	public Double getWeightAverageMin() {
		return weightAverageMin;
	}

	public Double getWeightAverageMax() {
		return weightAverageMax;
	}

	public Double getTotalBidUnitPriceMin() {
		return totalBidUnitPriceMin;
	}

	public Double getTotalBidUnitPriceMax() {
		return totalBidUnitPriceMax;
	}

	public Double getTotalBidExtendedPriceMin() {
		return totalBidExtendedPriceMin;
	}

	public Double getTotalBidExtendedPriceMax() {
		return totalBidExtendedPriceMax;
	}

	public void setWeightAverageMin(Double weightAverageMin) {
		this.weightAverageMin = weightAverageMin;
	}

	public void setWeightAverageMax(Double weightAverageMax) {
		this.weightAverageMax = weightAverageMax;
	}

	public void setTotalBidUnitPriceMin(List lineItems) {
		this.totalBidUnitPriceMin = getTotalEquityCurvePrice(lineItems,11);
	}

	public void setTotalBidUnitPriceMax(List lineItems) {
		this.totalBidUnitPriceMax = getTotalEquityCurvePrice(lineItems,21);
	}

	public void setTotalBidExtendedPriceMin(List lineItems) {
		this.totalBidExtendedPriceMin = getTotalEquityCurvePrice(lineItems,12);
	}

	public void setTotalBidExtendedPriceMax(List lineItems) {
		this.totalBidExtendedPriceMax = getTotalEquityCurvePrice(lineItems,22);
	}


	@Override
	public String toString() {
		return "EquityCurveTotal [weightAverageMin=" + weightAverageMin
				+ ", weightAverageMax=" + weightAverageMax
				+ ", totalBidUnitPriceMin=" + totalBidUnitPriceMin
				+ ", totalBidUnitPriceMax=" + totalBidUnitPriceMax
				+ ", totalBidExtendedPriceMin=" + totalBidExtendedPriceMin
				+ ", totalBidExtendedPriceMax=" + totalBidExtendedPriceMax
				+ "]";
	}

	/**
     * @param lineItems quotelineitem
     * @return
     * String
     */
    private Double getTotalEquityCurvePrice(List lineItems,int operateLocation) {

        if (null == lineItems || lineItems.isEmpty()){
			return null;
		}
       BigDecimal totalEquityCurvePrice = new BigDecimal(0.00);
       switch(operateLocation){
            // calculate total minimum bid unit price
            case 11:
            	for (Iterator itemIt = lineItems.iterator(); itemIt.hasNext();) {
                    QuoteLineItem item = (QuoteLineItem) itemIt.next();
		            if (null != item.getMinBidUnitPrice()){
		            	totalEquityCurvePrice = totalEquityCurvePrice.add(new BigDecimal(item.getMinBidUnitPrice()));
		            }
            	}
	            break;
	         // calculate total minimum bid extended price
            case 12:
            	for (Iterator itemIt = lineItems.iterator(); itemIt.hasNext();) {
                    QuoteLineItem item = (QuoteLineItem) itemIt.next();
		            if (null != item.getMinBidExtendedPrice()){
		            	totalEquityCurvePrice = totalEquityCurvePrice.add(new BigDecimal(item.getMinBidExtendedPrice()));
		            }
            	}
	            break;
	         // calculate total maximum bid unit price
            case 21:
            	for (Iterator itemIt = lineItems.iterator(); itemIt.hasNext();) {
                    QuoteLineItem item = (QuoteLineItem) itemIt.next();
		            if (null != item.getMaxBidUnitPrice()){
		            	totalEquityCurvePrice = totalEquityCurvePrice.add(new BigDecimal(item.getMaxBidUnitPrice()));
		            }
            	}
	            break;
	         // calculate total maximum bid extended price
            case 22:
            	for (Iterator itemIt = lineItems.iterator(); itemIt.hasNext();) {
                    QuoteLineItem item = (QuoteLineItem) itemIt.next();
		            if (null != item.getMaxBidExendedPrice()){
		            	totalEquityCurvePrice = totalEquityCurvePrice.add(new BigDecimal(item.getMaxBidExendedPrice()));
		            }
            	}
	            break;
	        default : break;
            }
        double dResult = totalEquityCurvePrice.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return Math.abs(dResult - 0.000) < 0.0000001 ? null : dResult;
    }
}
