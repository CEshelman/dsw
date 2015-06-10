package com.ibm.dsw.quote.scw.userlookup.process;


import com.ibm.dsw.quote.scw.userlookup.ScwUserLookupResult;
import com.ibm.dsw.quote.scw.userlookup.domain.UserLookupAPIContractInfo;
import com.ibm.ead4j.topaz.exception.TopazException;


public interface UserLookupAPIProcess {



	public ScwUserLookupResult retrieveUserLookupInfoResultNoCA(
			UserLookupAPIContractInfo usrLkpCtrctInfo)  throws TopazException;

	public ScwUserLookupResult retrieveUserLookupInfoResultWithCA(
			UserLookupAPIContractInfo usrLkpCtrctInfo)  throws TopazException;
}
