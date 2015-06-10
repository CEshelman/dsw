package com.ibm.dsw.quote.common.domain;

import org.apache.commons.lang.StringUtils;

public class CognosPart {
	public String partNum;
	public Integer partQty;
	
	public CognosPart(){
		
	}

	public String getPartNum() {
		return partNum;
	}

	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}

	public Integer getPartQty() {
		return partQty;
	}

	public void setPartQty(Integer partQty) {
		this.partQty = partQty;
	}
	
	/**
	 * @return
	 * if partNum is not blank, it's a valid part
	 */
	public boolean isValid(){
		if(StringUtils.isBlank(partNum)
			|| partQty == null
			|| partQty.intValue() == 0){
			return false;
		}else{
			return true;
		}
	}
	
	public String toString(){
		StringBuffer strBuff = new StringBuffer("");
		strBuff.append("[");
		strBuff.append(partNum);
		strBuff.append(",");
		strBuff.append(partQty);
		strBuff.append("]");
		return strBuff.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 * override equals method
	 */
	public boolean equals(Object o){
		CognosPart part2 = (CognosPart) o;
		if(!this.isValid() && !part2.isValid()){
			return true;
		}else{
			if(this.isValid() && this.getPartNum().equals(part2.getPartNum())){
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 * override hashCode method
	 */
	public int hashCode(){
		return this.getPartNum().hashCode();
	}

}
