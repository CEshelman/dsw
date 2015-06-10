package com.ibm.dsw.quote.common.domain;

public class MigratePart_Impl implements MigratePart {
	protected String confId;
	protected String partNum;
	protected String partDesc;
	protected int seqNum;
	protected boolean isMigration;
	protected boolean isOnDemandPart;
	protected boolean pastDateFlag;
	
	public boolean isMigration() {
		return isMigration;
	}
	public void setMigration(boolean isMigration) {
		this.isMigration = isMigration;
	}
	public String getConfId() {
		return confId;
	}
	public void setConfId(String confId) {
		this.confId = confId;
	}
	public boolean isPastDateFlag() {
		return pastDateFlag;
	}
	public void setPastDateFlag(boolean pastDateFlag) {
		this.pastDateFlag = pastDateFlag;
	}
	public boolean isOnDemandPart() {
		return isOnDemandPart;
	}
	public void setOnDemandPart(boolean isOnDemandPart) {
		this.isOnDemandPart = isOnDemandPart;
	}
	public String getPartNum() {
		return partNum;
	}
	public int getSeqNum() {
		return seqNum;
	}
	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}
	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}
	public String getPartDesc() {
		return partDesc;
	}
	public void setPartDesc(String partDesc) {
		this.partDesc = partDesc;
	}
}
