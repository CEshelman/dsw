package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.sql.Date;

public class CoTermConfiguration implements Serializable {
	String chargeAgreementNum;
	String cotermConfigrtnId;
	String ibmProdId;
	String ibmProdIdDscr;
	Date endDate;
	public CoTermConfiguration(String chargeAgreementNum, String cotermConfigrtnId, String ibmProdId, String ibmProdIdDscr, Date endDate){
		this.chargeAgreementNum = chargeAgreementNum;
		this.cotermConfigrtnId = cotermConfigrtnId;
		this.ibmProdId = ibmProdId;
		this.ibmProdIdDscr = ibmProdIdDscr;
		this.endDate = endDate;
	}
    public String getChargeAgreementNum() {
		return chargeAgreementNum;
	}
	public String getCotermConfigrtnId() {
		return cotermConfigrtnId;
	}
	public String getIbmProdId() {
		return ibmProdId;
	}
	public String getIbmProdIdDscr() {
		return ibmProdIdDscr;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	public int hashCode(){
    	int hash = 0;
    	if (this.cotermConfigrtnId != null){
    		hash += cotermConfigrtnId.hashCode()*1;
    	}
    	return hash;
    }
    
    public boolean equals(Object other) {
		if (this == other) 
			return true;
		if (other == null)
			return false;
		if (!(other instanceof CoTermConfiguration))
			return false;

		final CoTermConfiguration ppc = (CoTermConfiguration) other;
		if (((this.getChargeAgreementNum() == null && ppc.getChargeAgreementNum() == null)
				|| (this.getChargeAgreementNum() != null && this.getChargeAgreementNum().equals(ppc.getChargeAgreementNum())))
			&& ((this.getCotermConfigrtnId() == null && ppc.getCotermConfigrtnId() == null)
				|| this.getCotermConfigrtnId() != null && this.getCotermConfigrtnId().equals(ppc.getCotermConfigrtnId()))
			) {
			return true;
		}
		return false;
	}
}