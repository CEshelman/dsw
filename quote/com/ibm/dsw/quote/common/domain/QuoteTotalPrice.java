package com.ibm.dsw.quote.common.domain;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>QuoteTotalPrice</code> class is Quote Total Price Domain.
 *
 *
 * @author <a href="jiamengz@cn.ibm.com">Linda Jia </a> <br/>
 *
 * Creation date: 2013-12-9
 *
 * $Log: QuoteTotalPrice.java,v $
 *
 */
public interface QuoteTotalPrice {
	
	public static final String TOT_PRC_TYPE_NORMAL_SW = "NORMAL_SW";
	public static final String TOT_PRC_TYPE_MONTHLY_SW = "MONTHLY_SW";
	public static final String TOT_PRC_TYPE_SAAS = "SAAS";
	public static final String TOT_PRC_TYPE_GRAND_TOT = "GRAND_TOT";
	
	public String getTotalPriceType();
	public Double getTotalPoints();
	public Double getTotalEntitledPrice();
	public Double getTotalBidPrice();
	public Double getTotalBpPrice();
	
	public void setTotalPriceType(String totalPriceType);

	public void setTotalPoints(Double totalPoints);

	public void setTotalEntitledPrice(Double totalEntitledPrice);

	public void setTotalBidPrice(Double totalBidPrice);

	public void setTotalBpPrice(Double totalBpPrice);
	
}