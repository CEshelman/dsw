package com.ibm.dsw.quote.ps.domain;

import java.util.List;

public class PartSearchServiceResult_Impl implements PartSearchServiceResult{
	
	private static final long serialVersionUID = -6185698621437371638L;
	private transient List services;
	private transient List agreements;
	private transient List prodBrandsList;
	private transient List configuredPids;
	private boolean hasConfigrtn;
	
	public List getServices() {
		return services;
	}
	public void setServices(List services) {
		this.services = services;
	}
	public List getAgreements() {
		return agreements;
	}
	public void setAgreements(List agreements) {
		this.agreements = agreements;
	}
	public List getProdBrandsList() {
		return prodBrandsList;
	}
	public void setProdBrandsList(List prodBrandsList) {
		this.prodBrandsList = prodBrandsList;
	}
	public boolean isHasConfigrtn() {
		return hasConfigrtn;
	}
	public void setHasConfigrtn(boolean hasConfigrtn) {
		this.hasConfigrtn = hasConfigrtn;
	}
	public List getConfiguredPids() {
		return configuredPids;
	}
	public void setConfiguredPids(List configuredPids) {
		this.configuredPids = configuredPids;
	}
}
