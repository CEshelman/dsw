package com.ibm.dsw.quote.common.domain.jdbc;

import com.ibm.dsw.quote.common.domain.YTYGrowth;
import com.ibm.dsw.quote.common.domain.YTYGrowthFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

public class YTYGrowthFactory_jdbc extends YTYGrowthFactory {

	@Override
	public YTYGrowth createYTYGrowth(String webQuoteNum, int lineItemSeqNum)
			throws TopazException {
		YTYGrowth_jdbc ytyGrowth = new YTYGrowth_jdbc(webQuoteNum, lineItemSeqNum);
		ytyGrowth.isNew(true);
		return ytyGrowth;
	}
}
