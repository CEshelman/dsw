package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.util.List;

public class DefaultCustAddress implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private List<ApplianceAddress> custDefaulAddreList = null;
    private int shipToOption = 0;
    private int installAtOption = 0;
    
	public List<ApplianceAddress> getCustDefaulAddreList() {
		return custDefaulAddreList;
	}

	public void setCustDefaulAddreList(List<ApplianceAddress> custDefaulAddreList) {
		this.custDefaulAddreList = custDefaulAddreList;
	}

	public int getShipToOption() {
		return shipToOption;
	}

	public void setShipToOption(int shipToOption) {
		this.shipToOption = shipToOption;
	}

	public int getInstallAtOption() {
		return installAtOption;
	}

	public void setInstallAtOption(int installAtOption) {
		this.installAtOption = installAtOption;
	}	
}
