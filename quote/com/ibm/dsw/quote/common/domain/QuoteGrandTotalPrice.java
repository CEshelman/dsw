package com.ibm.dsw.quote.common.domain;

public class QuoteGrandTotalPrice extends QuoteTotalPrice_Impl {
	public String getTotalPriceType() {
		return this.totalPriceType;
	}

	public Double getGrandTotalPoints() {
		return this.totalPoints;
	}

	public Double getGrandTotalEntitledPrice() {
		return this.totalEntitledPrice;
	}

	public Double getGrandTotalBidPrice() {
		return this.totalBidPrice;
	}

	public Double getGrandTotalBpPrice() {
		return this.totalBpPrice;
	}

}
