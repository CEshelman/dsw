package com.ibm.dsw.quote.ps.domain;

import java.io.Serializable;
import java.util.List;

public interface PartSearchServiceResult extends Serializable {
	public List getServices();
	public void setServices(List services);
	public List getAgreements();
	public void setAgreements(List agreements);
	public List getProdBrandsList();
	public void setProdBrandsList(List prodBrandsList);
	public boolean isHasConfigrtn();
	public void setHasConfigrtn(boolean hasConfigrtn);
	public List getConfiguredPids();
	public void setConfiguredPids(List configuredPids);
}
