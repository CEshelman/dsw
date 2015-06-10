package com.ibm.dsw.quote.appcache.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;

import com.ibm.dsw.quote.appcache.domain.PrintSPTimeTrace_impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.KeyedPersistentObject;

public class PrintSPTimeTrace_jdbc extends PrintSPTimeTrace_impl implements KeyedPersistentObject, Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6972113436055044681L;

	public PrintSPTimeTrace_jdbc( String spName ){
		this.spName = spName;
	}
	
	public PrintSPTimeTrace_jdbc( String spName, int timeThreshold ){
		this.spName = spName;
		this.timeThreshold = timeThreshold;
	}
	

	public Object getKey() {
		return this.spName;
	}
	
	public void hydrate(Connection connection) throws TopazException {
		// TODO Auto-generated method stub

	}

	public void persist(Connection connection) throws TopazException {
		// TODO Auto-generated method stub

	}

	public void isDeleted(boolean deleteState) throws TopazException {
		// TODO Auto-generated method stub

	}

	public void isNew(boolean newState) throws TopazException {
		// TODO Auto-generated method stub

	}


}
