/**
 * 
 */
package com.ibm.dsw.quote.draftquote.util;

import java.util.Set;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.util.BidIterationRule;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;

/**
 * @ClassName: BidIterationMonthlyRuleHelper
 * @author Frank
 * @Description: TODO
 * @date Feb 27, 2014 11:41:46 AM
 *
 */
public class BidIterationMonthlyRuleHelper extends BidIterationRuleHelper {

	/**
	 * @param quote
	 * @param bidIterationRule
	 * @throws QuoteException
	 */
	public BidIterationMonthlyRuleHelper(Quote quote,
			BidIterationRule bidIterationRule) throws QuoteException {
		super(quote, bidIterationRule);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    protected void initOriginalLineItems() throws QuoteException {
    	originalParts = CommonServiceUtil.getMonthlyLineItemList(originalAllParts);
    }

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.draftquote.util.BidIterationRuleHelper#initCurrentLineItems()
	 */
	@Override
	protected void initCurrentLineItems() throws QuoteException {
		
		if (quote.getMonthlySwQuoteDomain() == null){
			return;
		}
		currentParts.addAll(quote.getMonthlySwQuoteDomain().getMonthlySoftwares());
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.draftquote.util.BidIterationRuleHelper#validateBidIteration()
	 */
	@Override
	public void validateBidIteration() throws QuoteException {
		Set<String> errorMsgSet = getErrorMsgSet();

		bidIterationRule.getMonthlyErrorCodeList().addAll(errorMsgSet);

		if (bidIterationRule.getMonthlyValidationResult() != QuoteConstants.BidIteratnValidationResult.NOT_APPLICABLE_BID_ITERATN
				&& bidIterationRule.getMonthlyErrorCodeList() != null
				&& bidIterationRule.getMonthlyErrorCodeList().size() > 0) {
			bidIterationRule
					.setMonthlyValidationResult(QuoteConstants.BidIteratnValidationResult.INVALID_BID_ITERATN);
		} else {
			bidIterationRule
					.setMonthlyValidationResult(QuoteConstants.BidIteratnValidationResult.VALID_BID_ITERATN);
		}
	}
    

}
