package com.ibm.dsw.quote.common.domain;

public interface MigratePart extends java.io.Serializable{
	public String getConfId();
	public String getPartNum();
	public String getPartDesc();
	public int getSeqNum();
	public boolean isMigration();
	public boolean isOnDemandPart();
	public boolean isPastDateFlag();

	public void setConfId(String confId);
	public void setPartNum(String partNum);
	public void setPartDesc(String partDesc);
	public void setSeqNum(int seqNum);
	public void setMigration(boolean isMigration);
	public void setOnDemandPart(boolean isOnDemandPart);
	public void setPastDateFlag(boolean pastDateFlag);
}
