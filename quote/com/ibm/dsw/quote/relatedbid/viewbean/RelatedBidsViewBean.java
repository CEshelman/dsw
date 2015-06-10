/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>RelatedBaseViewBean<code> class.
 *    
 * @author: daflores@us.ibm.com
 * 
 * Creation date: Jan 28, 2013 
 */

package com.ibm.dsw.quote.relatedbid.viewbean; 

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.relatedbid.domain.RelatedBid;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.ead4j.jade.bean.ViewBean;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class RelatedBidsViewBean extends ViewBean {
	protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
	
	private List list;
	
	private static final int RENEWAL_LINE_ITEM_COUNT = 3;
	
	public void collectResults(Parameters parms) throws ViewBeanException {
		this.list = (List)parms.getParameter(ParamKeys.RELATED_BIDS_LIST);
	}
	
	public List getRelatedBidsList(){
		return this.list;
	}
	
	public String getSubmittedDate(RelatedBid bid){
		return  DateUtil.formatDate(bid.getSubmittedDate(), DateUtil.PATTERN5);
	}
	
	public String getQuoteTotal(RelatedBid bid){
		if(bid.getQuoteTotal() == null){
			return "";
		}
		return DecimalUtil.format(bid.getQuoteTotal()) + " " + bid.getCurrencyCode();
	}
	
	public boolean showPercent(Double d){
		return (d != null);
	}
	
	public String getRenewalLineItemNumStringShort(RelatedBid bid){
		List<String> renewalLineItemNumList = bid.getRenewalLineItemNumList();
		
		int total = renewalLineItemNumList.size();
		if(total == 0){
			return "";
		}
		
		StringBuffer sb = new StringBuffer();
		if(total < RENEWAL_LINE_ITEM_COUNT){
			for(String num : renewalLineItemNumList){
				sb.append(num).append(" ,");
			}
			
			sb.deleteCharAt(sb.length() - 1);
		} else {
			int i = 0;
			for(String num : renewalLineItemNumList){
				i++;
				sb.append(num).append(" ,");
				
				if(i == RENEWAL_LINE_ITEM_COUNT){
					break;
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("...");
		}
		
		return sb.toString();
	}
	
	public String getRenewalLineItemNumStringLong(RelatedBid bid){
		List<String> renewalLineItemNumList = bid.getRenewalLineItemNumList();

		if(renewalLineItemNumList.size() < 1){
			return "";
		}
		
		StringBuffer sb = new StringBuffer();
		for(String num : renewalLineItemNumList){
			sb.append(num).append(" ,");
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
		return StringUtils.trim(sb.toString());
	}
	
	public boolean showViewAllLink(RelatedBid bid){
		return (bid.getRenewalLineItemNumList().size() > RENEWAL_LINE_ITEM_COUNT);
	}
	
	public String getOverallGrowth(RelatedBid bid){
		if(bid.getOverallGrowth() == null){
			return "";
		}
		return DecimalUtil.format(bid.getOverallGrowth());
	}
	
	public String getImpliedGrowth(RelatedBid bid){
		if(bid.getImpliedGrowth() == null){
			return "";
		}
		return DecimalUtil.format(bid.getImpliedGrowth());
	}
	
	public String getRenewalQuoteUrl(String renewalQuoteNum){
	    String baseURL = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
	            ApplicationProperties.RENEWAL_QUOTE_DETAIL_URL);
	    
	    StringBuffer origRenewQuoteDetailURL = new StringBuffer(baseURL);
	    HtmlUtil.addURLParam(origRenewQuoteDetailURL, DraftRQParamKeys.PARAM_RPT_QUOTE_NUM, renewalQuoteNum);
	
	    return origRenewQuoteDetailURL.toString();
	}
	
    public String getSqlRefUrl(RelatedBid bid) {
        StringBuffer url = new StringBuffer();

        if (StringUtils.isNotBlank(bid.getSqoRef())) {
            String actionURL = HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB);
            url.append(actionURL);
            HtmlUtil.addURLParam(url, ParamKeys.PARAM_QUOTE_NUM, bid.getSqoRef());
        }
        return url.toString();
    }
}