package com.ibm.dsw.quote.scw.userlookup.process;


import com.ibm.dsw.quote.scw.userlookup.ScwUserLookupResult;
import com.ibm.dsw.quote.scw.userlookup.domain.UserLookupAPIContractInfo;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

public abstract class UserLookupAPIProcess_impl extends
		TopazTransactionalProcess implements UserLookupAPIProcess {

	protected ScwUserLookupResult usrLkpRslt = new ScwUserLookupResult();


	public abstract ScwUserLookupResult retrieveUserLookupInfoResultNoCA(
			UserLookupAPIContractInfo usrlkpCtrInfo)  throws TopazException;
	
	public abstract ScwUserLookupResult retrieveUserLookupInfoResultWithCA(
			UserLookupAPIContractInfo usrlkpCtrInfo)  throws TopazException;
	

}
