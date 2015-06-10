package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteOmittedLineItem<code> class.
 * 
 * @author: liguotao@cn.ibm.com
 * 
 *          Creation date: Jun 06, 2013
 */

public class QuoteOmittedLineItemVO implements Serializable {
	private String custNum = null;
	private String agreeNum = null;
	private String custName = null;
	private String quoteNum = null;
	private String type = null;
	private String quoteStatus = null;
	
	private String totalPoints = null;
	private String totalPrice = null;
	private String currency = null;
	private String renewalNum = null;
	private String partNum = "";
	private String itemPoints = null;
	private String itemPrice = null;
	private String lineItem = null;
	private String partDscr = null;
	private Date startDate = null;
	private Date endDate = null;
	private String priorPrice = null;
	private Integer partQty = 0;
	private double YTYGrowth = 0;
	private String rsvpPrice = null;
	private Integer renewalSecNum = 0;
	private String currencyCodeParam = "";
	private String ytySrcCode = "";
	private Integer renwlQuoteLineItemQty;
	private Double systemPriorPrice;
	private String pricingMethod;
	private double localPriorPrice;
	private int lineOpenQty = 0;
	public String getCustNum() {
		return custNum;
	}
	public void setCustNum(String custNum) {
		this.custNum = custNum;
	}
	public String getAgreeNum() {
		return agreeNum;
	}
	public void setAgreeNum(String agreeNum) {
		this.agreeNum = agreeNum;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getQuoteNum() {
		return quoteNum;
	}
	public void setQuoteNum(String quoteNum) {
		this.quoteNum = quoteNum;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getQuoteStatus() {
		return quoteStatus;
	}
	public void setQuoteStatus(String quoteStatus) {
		this.quoteStatus = quoteStatus;
	}
	
	
	public String getTotalPoints() {
		return totalPoints;
	}
	public void setTotalPoints(String totalPoints) {
		this.totalPoints = totalPoints;
	}
	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	

	public QuoteOmittedLineItemVO() {
	}

	
	public String getRenewalNum() {
		return renewalNum;
	}
	public void setRenewalNum(String renewalNum) {
		this.renewalNum = renewalNum;
	}
	public String getPartNum() {
		return partNum;
	}
	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}
	public String getItemPoints() {
		return itemPoints;
	}
	public void setItemPoints(String itemPoints) {
		this.itemPoints = itemPoints;
	}
	public String getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(String itemPrice) {
		this.itemPrice = itemPrice;
	}
	public String getLineItem() {
		return lineItem;
	}
	public void setLineItem(String lineItem) {
		this.lineItem = lineItem;
	}
	public String getPartDscr() {
		return partDscr;
	}
	public void setPartDscr(String partDscr) {
		this.partDscr = partDscr;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public Integer getPartQty() {
		return partQty;
	}
	public void setPartQty(Integer partQty) {
		this.partQty = partQty;
	}
	public String getPriorPrice() {
		return priorPrice;
	}
	public void setPriorPrice(String priorPrice) {
		this.priorPrice = priorPrice;
	}
	public double getYTYGrowth() {
		return YTYGrowth;
	}
	public void setYTYGrowth(double yTYGrowth) {
		YTYGrowth = yTYGrowth;
	}
	public String getRsvpPrice() {
		return rsvpPrice;
	}
	public void setRsvpPrice(String rsvpPrice) {
		this.rsvpPrice = rsvpPrice;
	}
	public Integer getRenewalSecNum() {
		return renewalSecNum;
	}
	public void setRenewalSecNum(Integer renewalSecNum) {
		this.renewalSecNum = renewalSecNum;
	}
	public String getCurrencyCodeParam() {
		return currencyCodeParam;
	}
	public void setCurrencyCodeParam(String currencyCodeParam) {
		this.currencyCodeParam = currencyCodeParam;
	}
	public String getYtySrcCode() {
		return ytySrcCode;
	}
	public void setYtySrcCode(String ytySrcCode) {
		this.ytySrcCode = ytySrcCode;
	}
	public Integer getRenwlQuoteLineItemQty() {
		return renwlQuoteLineItemQty;
	}
	public void setRenwlQuoteLineItemQty(Integer renwlQuoteLineItemQty) {
		this.renwlQuoteLineItemQty = renwlQuoteLineItemQty;
	}
	public Double getSystemPriorPrice() {
		return systemPriorPrice;
	}
	public void setSystemPriorPrice(Double systemPriorPrice) {
		this.systemPriorPrice = systemPriorPrice;
	}
	public String getPricingMethod() {
		return pricingMethod;
	}
	public void setPricingMethod(String pricingMethod) {
		this.pricingMethod = pricingMethod;
	}
	public int getLineOpenQty() {
		return lineOpenQty;
	}
	public void setLineOpenQty(int lineOpenQty) {
		this.lineOpenQty = lineOpenQty;
	}
	public double getLocalPriorPrice() {
		return localPriorPrice;
	}
	public void setLocalPriorPrice(double localPriorPrice) {
		this.localPriorPrice = localPriorPrice;
	}
	
	
	
	
	
	 
	

}


