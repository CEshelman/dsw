package com.ibm.dsw.quote.common.domain;

public class QuoteTotalPriceSaas extends QuoteTotalPrice_Impl {

	public String getTotalPriceType() {
		return this.totalPriceType;
	}

	public Double getTotalPoints() {
		return this.totalPoints;
	}

	public Double getTotalEntitledTcv() {
		return this.totalEntitledPrice;
	}

	public Double getTotalBidTcv() {
		return this.totalBidPrice;
	}

	public Double getTotalBpTcv() {
		return this.totalBpPrice;
	}

}
