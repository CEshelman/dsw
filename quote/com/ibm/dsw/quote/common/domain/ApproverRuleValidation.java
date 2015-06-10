package com.ibm.dsw.quote.common.domain;

import com.ibm.dsw.quote.common.util.BidIterationRule;
import java.util.*;

public class ApproverRuleValidation extends BidIterationRule {
	protected boolean streamLineFlag;
	protected List<String> approveTypes;
	protected boolean bidIterFlag;
	
	public boolean isStreamLineFlag() {
		return streamLineFlag;
	}
	public void setStreamLineFlag(boolean streamLineFlag) {
		this.streamLineFlag = streamLineFlag;
	}
	public List<String> getApproveTypes() {
		return approveTypes;
	}
	public void setApproveTypes(List<String> approveTypes) {
		this.approveTypes = approveTypes;
	}
	public boolean isBidIterFlag() {
		return bidIterFlag;
	}
	public void setBidIterFlag(boolean bidIterFlag) {
		this.bidIterFlag = bidIterFlag;
	}
}
