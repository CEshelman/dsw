package com.ibm.dsw.quote.scw.userlookup.process.jdbc;


import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.scw.userlookup.process.UserLookupAPIProcess;
import com.ibm.dsw.quote.scw.userlookup.process.UserLookupAPIProcessFactory;

public class UserLookupAPIProcessFactory_jdbc extends UserLookupAPIProcessFactory {
	
	public UserLookupAPIProcessFactory_jdbc() {
		super();
	}

	@Override
	public UserLookupAPIProcess create() throws QuoteException {
		return new UserLookupAPIProcess_jdbc();
	}

}
