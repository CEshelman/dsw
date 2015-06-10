package com.ibm.dsw.quote.appcache.domain;

import java.io.Serializable;

public abstract class PrintSPTimeTrace_impl implements PrintSPTimeTrace, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3366970946625688948L;
	public String spName;
	public int timeThreshold;
	
	@Override
	public String getSPName() {
		return spName;
	}

	@Override
	public int getTimeThreshold() {
		return timeThreshold;
	}

	@Override
	public void setSPName(String spName) {
		this.spName = spName;
	}

	@Override
	public void setTimeThreshold(int timeThreshold) {
		this.timeThreshold = timeThreshold;
	}

    public String toString() {
        return "spName=" + this.getSPName() + " timeThreshold=" + this.getTimeThreshold();
    }
	
	@Override
	public int compareTo(Object o) {
		PrintSPTimeTrace_impl p = null;
		if (null == o)
			return -1;
		if (!(o instanceof PrintSPTimeTrace_impl))
			return -1;
		p = (PrintSPTimeTrace_impl)o;
		
        return this.getSPName().compareTo(p.getSPName());
	}
	
    public boolean equals(Object o){
    	PrintSPTimeTrace_impl p = null;
    	if (null == o)
			return false;
    	if (!(o instanceof PrintSPTimeTrace_impl))
    		return false;
    	p = (PrintSPTimeTrace_impl) o;
    	return this.getSPName().equals(p.getSPName()) && (this.getTimeThreshold() == p.getTimeThreshold());
    }
}
