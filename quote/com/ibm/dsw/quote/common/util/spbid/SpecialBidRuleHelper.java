package com.ibm.dsw.quote.common.util.spbid;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;

public class SpecialBidRuleHelper {
	public static SpecialBidRuleExecuteResult executeSpecialBidRule(Quote quote, String userId) throws QuoteException
	{
		SpecialBidRule rule = SpecialBidRule.create(quote);
		rule.validate(userId);
		
		SpecialBidRuleExecuteResult result = new SpecialBidRuleExecuteResult();
		
		result.setOverridePriceSpecificed(rule.isOverridePriceSpecificed());
		result.setDiscountSpecificed(rule.isDiscountSpecificed());
		result.setPartGroupSpid(rule.isPartGroupSpid());
		result.setPartRestrict(rule.isPartRestrict());
		result.setMaintOverDefaultPeriod(rule.isMaintOverDefaultPeriod());
		result.setResellerAuthorizedToPortfolio(rule.isResellerAuthorizedToPortfolio());
		result.setDiscountOverDelegation(rule.isDiscountOverDelegation());
		result.setDiscountWithNoDelegation(rule.isDiscountWithNoDelegation());
		result.setApprovalRouteFlag(rule.isApprovalRouteFlag());
		result.setDiscountBelowDelegation(rule.isDiscountBelowDelegation());
		result.setQuoteBackDated(rule.isQuoteBackDated());
		result.setChannelDiscountOverriden(rule.isChannelDiscountOverriden());
		result.setSpecialBidReason(rule.getSpecialBidReason());
		result.setCmprssCvrageQuote(rule.isCmprssCvrageQuote());
		return result;
	}
}
