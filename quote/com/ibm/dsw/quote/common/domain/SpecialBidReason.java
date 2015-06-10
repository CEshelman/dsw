/*
 * Created on Dec 29, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.config.QuoteConstants;

/**
 * @author liuxin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpecialBidReason implements Serializable{
	private transient Map reasonCodeMap = new HashMap();
	
	public void addReasCode(String colName, String reason){
		if(QuoteConstants.SpecialBidReason.SB_EMEA_DISCOUNT.equals(colName)){
			reasonCodeMap.put(colName, reason);
			return;
		}
		
		List reasList = getList(colName);
		
		if(!reasList.contains(reason)){
			reasList.add(reason);
		}
	}
	
	public void addSpecialBidReason(String reason){
		List reasList = getSpecialBidReasonList();
		
		if(!reasList.contains(reason)){
			reasList.add(reason);
		}
	}
	
	public List getSpecialBidReasonList(){
		return getList(QuoteConstants.SpecialBidReason.SB_REAS_CODE);
	}
	
	public void addNoApprovalReason(){
		List reasList = getNoApprovalReasonList();
		String reason = QuoteConstants.SpecialBidReason.DISCOUNT_BELOW_DELEGATION;
		if(!reasList.contains(reason)){
			reasList.add(reason);
		}
	}
	public void addNoApprovalReasonOfGrid(){
		List reasList = getNoApprovalReasonList();
		String reason = QuoteConstants.SpecialBidReason.GRID_OVER_DELEGATION;
		if(!reasList.contains(reason)){
			reasList.add(reason);
		}
	}
	
	public List getNoApprovalReasonList(){
		return getList(QuoteConstants.SpecialBidReason.SB_NO_APPROVAL);
	}
	
	public void addEMEADiscountReason(){
		reasonCodeMap.put(QuoteConstants.SpecialBidReason.SB_EMEA_DISCOUNT, QuoteConstants.SpecialBidReason.EMEA_DISCOUNT_OVER_DEFAULT);
	}
	
	public String getEMEADiscountReason(){
		return (String)reasonCodeMap.get(QuoteConstants.SpecialBidReason.SB_EMEA_DISCOUNT);
	}
	
	public boolean isEMEADiscountRequireSpBid(){
		return reasonCodeMap.get(QuoteConstants.SpecialBidReason.SB_EMEA_DISCOUNT) != null;
	}
	
	private List getList(String key){
		List list = (List)reasonCodeMap.get(key);
		if(list == null){
			list = new ArrayList();
			reasonCodeMap.put(key, list);
		}
		
		return list;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("SpecialBidReason{");
		
		List list = getSpecialBidReasonList();
		sb.append("s.b reason[");
		boolean endsWithComma = false;
		for(Iterator it = list.iterator(); it.hasNext(); ){
			sb.append(it.next()).append(",");
			endsWithComma = true;
		}
		if(endsWithComma){
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("], ");
		
		endsWithComma = false;
		list = getNoApprovalReasonList();
		sb.append("no approval[");
		for(Iterator it = list.iterator(); it.hasNext(); ){
			sb.append(it.next()).append(",");
			endsWithComma = true;
		}
		if(endsWithComma){
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("], ");
		
		sb.append("emea discount[" + getEMEADiscountReason() + "]}");
		
		return sb.toString();
	}
}
