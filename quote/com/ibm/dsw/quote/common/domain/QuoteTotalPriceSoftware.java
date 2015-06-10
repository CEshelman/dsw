package com.ibm.dsw.quote.common.domain;


public class QuoteTotalPriceSoftware extends QuoteTotalPrice_Impl {
	
	@Override
	public String getTotalPriceType() {
		return this.totalPriceType;
	}

	@Override
	public Double getTotalPoints() {
		return this.totalPoints;
	}

	@Override
	public Double getTotalEntitledPrice() {
		return this.totalEntitledPrice;
	}

	@Override
	public Double getTotalBidPrice() {
		return this.totalBidPrice;
	}

	@Override
	public Double getTotalBpPrice() {
		return this.totalBpPrice;
	}


}
