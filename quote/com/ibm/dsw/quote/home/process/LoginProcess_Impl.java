package com.ibm.dsw.quote.home.process;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.bluepages.BluePageUser;
import com.ibm.dsw.quote.bluepages.BluePagesLookup;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.SalesRepFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * <p>Implements the <code>LoginProcess</code></p>
 * 
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mailto:mgivney@us.ibm.com">Matt Givney</a><br/>
 */
public abstract class LoginProcess_Impl extends TopazTransactionalProcess implements LoginProcess{

    public LoginProcess_Impl() {
        super();
    }
    
    public SalesRep logUser( String intranetId, int telesalesAccess) throws QuoteException {
        try {
			//begin the transaction
			this.beginTransaction();
			
			//create a salesRep object
			SalesRep salesRep = SalesRepFactory.singleton().create(intranetId,telesalesAccess);
			
			//query bluepage information
			BluePageUser user = null;
			try{
			    user = BluePagesLookup.getBluePagesInfo(intranetId);			
			}catch( Exception ex ){
	            throw new TopazException( "Could not get sales rep from BluePages!", ex);
	        }

			if(user!=null){
				//set bluepage information into domain object for persistence
				salesRep.setBluepageInformation(	user.getCountryCode(),
				        												user.getFullName(),
				        												user.getLastName(),
				        												user.getFirstName(),
				        												user.getPhoneNumber(),
				        												user.getFaxNumber(),
				        												user.getDirectReportsList(),
																		user.getUp2LevelReportsList(),
				        												user.getWIID(),
				        												user.getNotesId(),
				        												user.getBluePagesId() );
			}

			//commit the transaction
			this.commitTransaction();
			return salesRep;
		} catch (TopazException tce) {
			throw new QuoteException(tce);
		} finally {
			rollbackTransaction();
		}
		
    }
    
    public SalesRep evaluatorUser(String intranetId) throws QuoteException {
        try {
			//begin the transaction
			this.beginTransaction();
			
			//create a salesRep object
			SalesRep salesRep = SalesRepFactory.singleton().findDelegateByID(intranetId);
			
			//commit the transaction
			this.commitTransaction();
			
			return salesRep;
			
        } catch (TopazException tce) {
			throw new QuoteException(tce);
		} finally {
			rollbackTransaction();
		}
    }
    
}
