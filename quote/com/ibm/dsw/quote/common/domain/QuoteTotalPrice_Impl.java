package com.ibm.dsw.quote.common.domain;

public abstract class QuoteTotalPrice_Impl implements QuoteTotalPrice {
	
	public String totalPriceType;
	public Double totalPoints;
	public Double totalEntitledPrice;
	public Double totalBidPrice;
	public Double totalBpPrice;

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
	
	public void setTotalPriceType(String totalPriceType) {
		this.totalPriceType = totalPriceType;
	}

	public void setTotalPoints(Double totalPoints) {
		this.totalPoints = totalPoints;
	}

	public void setTotalEntitledPrice(Double totalEntitledPrice) {
		this.totalEntitledPrice = totalEntitledPrice;
	}

	public void setTotalBidPrice(Double totalBidPrice) {
		this.totalBidPrice = totalBidPrice;
	}

	public void setTotalBpPrice(Double totalBpPrice) {
		this.totalBpPrice = totalBpPrice;
	}


}
