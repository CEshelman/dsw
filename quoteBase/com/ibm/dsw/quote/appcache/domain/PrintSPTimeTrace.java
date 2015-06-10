package com.ibm.dsw.quote.appcache.domain;

import java.io.Serializable;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PrintSPTimeTrace</code> class is for get SP name and time threshold.
 * 
 * 
 * @author <a href="machaomc@cn.ibm.com">Chao AM Ma </a> <br/>
 * 
 * Creation date: 2013-10-16
 */
public interface PrintSPTimeTrace extends Comparable, Serializable{

	public abstract String getSPName();
	public abstract int getTimeThreshold();
	
	public abstract void setSPName(String spName);
	public abstract void setTimeThreshold(int timeThreshold);
	
}
