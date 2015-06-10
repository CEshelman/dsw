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
 * @ClassName: BidIterationSaasRulHelper
 * @author Frank
 * @Description: TODO
 * @date Feb 27, 2014 11:40:53 AM
 *
 */
public class BidIterationSaasRuleHelper extends BidIterationRuleHelper {

	/**
	 * @param quote
	 * @param bidIterationRule
	 * @throws QuoteException
	 */
	public BidIterationSaasRuleHelper(Quote quote,
			BidIterationRule bidIterationRule) throws QuoteException {
		super(quote, bidIterationRule);
	}
	
	@Override
    protected void initOriginalLineItems() throws QuoteException {
    	originalParts = CommonServiceUtil.getSaaSLineItemList(originalAllParts);
    }

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.draftquote.util.BidIterationRuleHelper#initCurrentLineItems()
	 */
	@Override
	protected void initCurrentLineItems() throws QuoteException {
		currentParts.addAll(quote.getSaaSLineItems());
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.draftquote.util.BidIterationRuleHelper#validateBidIteration()
	 */
	@Override
	public void validateBidIteration() throws QuoteException {
		Set<String> errorMsgSet = getErrorMsgSet();
		
		bidIterationRule.getSaasErrorCodeList().addAll(errorMsgSet);

		if (bidIterationRule.getSaasValidationResult() != QuoteConstants.BidIteratnValidationResult.NOT_APPLICABLE_BID_ITERATN
				&& bidIterationRule.getSaasErrorCodeList() != null
				&& bidIterationRule.getSaasErrorCodeList().size() > 0) {
			bidIterationRule
					.setSaasValidationResult(QuoteConstants.BidIteratnValidationResult.INVALID_BID_ITERATN);
		} else {
			bidIterationRule
					.setSaasValidationResult(QuoteConstants.BidIteratnValidationResult.VALID_BID_ITERATN);
		}
	}
    

}
