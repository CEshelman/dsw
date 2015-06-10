package com.ibm.dsw.quote.draftquote.viewbean.helper;

import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;

public class DraftPartTableElementUtil extends PartTableElementUtil {
	private static final long serialVersionUID = 4868537225382958369L;
	
	private PartTable partTable;

	public DraftPartTableElementUtil(Quote quote, PartTable partTable) {
		super(quote, (PartPriceCommon)partTable);
		this.partTable = partTable;
	}
	
	/**
	 * @param item
	 * @return standard BP discount String Value for draft quote
	 */
	public String getStdBPDiscStrVal(QuoteLineItem item){
		if(!item.isReplacedPart()){
			return DecimalUtil.formatTo5Number(item.getChnlStdDiscPct().doubleValue()) + "%";
		}else{
			QuoteLineItem masterItem = quote.getSaaSLineItem(item.getIRelatedLineItmNum());
			if(masterItem == null 
				|| masterItem.getChnlStdDiscPct() == null 
				|| masterItem.getChnlStdDiscPct().doubleValue() == 0.0){
				return DraftQuoteConstants.BLANK;
			}else{
				return DecimalUtil.formatTo5Number(masterItem.getChnlStdDiscPct().doubleValue()) + "%";
			}
		}
    }

}
