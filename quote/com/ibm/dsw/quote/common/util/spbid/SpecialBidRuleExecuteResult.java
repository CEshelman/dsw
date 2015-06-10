package com.ibm.dsw.quote.common.util.spbid;

import com.ibm.dsw.quote.common.domain.SpecialBidReason;

public class SpecialBidRuleExecuteResult implements java.io.Serializable{

	private static final long serialVersionUID = 5317734544868039056L;
	
	private boolean overridePriceSpecificed;
	private boolean discountSpecificed;
	private boolean partGroupSpid;
	private boolean partRestrict;
	private boolean maintOverDefaultPeriod;
	private boolean resellerAuthorizedToPortfolio;
	private boolean discountOverDelegation;
	private boolean discountWithNoDelegation;
	private boolean approvalRouteFlag;
	private boolean discountBelowDelegation;
	private boolean quoteBackDated;
	private boolean channelDiscountOverriden;
	private SpecialBidReason specialBidReason;
	private boolean cmprssCvrageQuote;
	public boolean isOverridePriceSpecificed() {
		return overridePriceSpecificed;
	}
	public void setOverridePriceSpecificed(boolean overridePriceSpecificed) {
		this.overridePriceSpecificed = overridePriceSpecificed;
	}
	public boolean isDiscountSpecificed() {
		return discountSpecificed;
	}
	public void setDiscountSpecificed(boolean discountSpecificed) {
		this.discountSpecificed = discountSpecificed;
	}
	public boolean isPartGroupSpid() {
		return partGroupSpid;
	}
	public void setPartGroupSpid(boolean partGroupSpid) {
		this.partGroupSpid = partGroupSpid;
	}
	public boolean isPartRestrict() {
		return partRestrict;
	}
	public void setPartRestrict(boolean partRestrict) {
		this.partRestrict = partRestrict;
	}
	public boolean isMaintOverDefaultPeriod() {
		return maintOverDefaultPeriod;
	}
	public void setMaintOverDefaultPeriod(boolean maintOverDefaultPeriod) {
		this.maintOverDefaultPeriod = maintOverDefaultPeriod;
	}
	public boolean isResellerAuthorizedToPortfolio() {
		return resellerAuthorizedToPortfolio;
	}
	public void setResellerAuthorizedToPortfolio(boolean resellerAuthorizedToPortfolio) {
		this.resellerAuthorizedToPortfolio = resellerAuthorizedToPortfolio;
	}
	public boolean isDiscountOverDelegation() {
		return discountOverDelegation;
	}
	public void setDiscountOverDelegation(boolean discountOverDelegation) {
		this.discountOverDelegation = discountOverDelegation;
	}
	public boolean isDiscountWithNoDelegation() {
		return discountWithNoDelegation;
	}
	public void setDiscountWithNoDelegation(boolean discountWithNoDelegation) {
		this.discountWithNoDelegation = discountWithNoDelegation;
	}
	public boolean isApprovalRouteFlag() {
		return approvalRouteFlag;
	}
	public void setApprovalRouteFlag(boolean approvalRouteFlag) {
		this.approvalRouteFlag = approvalRouteFlag;
	}
	public boolean isDiscountBelowDelegation() {
		return discountBelowDelegation;
	}
	public void setDiscountBelowDelegation(boolean discountBelowDelegation) {
		this.discountBelowDelegation = discountBelowDelegation;
	}
	public boolean isQuoteBackDated() {
		return quoteBackDated;
	}
	public void setQuoteBackDated(boolean quoteBackDated) {
		this.quoteBackDated = quoteBackDated;
	}
	public boolean isChannelDiscountOverriden() {
		return channelDiscountOverriden;
	}
	public void setChannelDiscountOverriden(boolean channelDiscountOverriden) {
		this.channelDiscountOverriden = channelDiscountOverriden;
	}
	public SpecialBidReason getSpecialBidReason() {
		return specialBidReason;
	}
	public void setSpecialBidReason(SpecialBidReason specialBidReason) {
		this.specialBidReason = specialBidReason;
	}
	public boolean isCmprssCvrageQuote() {
		return cmprssCvrageQuote;
	}
	public void setCmprssCvrageQuote(boolean cmprssCvrageQuote) {
		this.cmprssCvrageQuote = cmprssCvrageQuote;
	}
}
