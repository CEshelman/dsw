/**
 * 
 */

package com.ibm.dsw.quote.login.history.process;


import java.sql.Timestamp;

import com.ibm.dsw.quote.base.exception.QuoteException;
/**
 * @author eadapa
 *
 */
public interface QuoteLoginHistoryProcess {
    public String getCSRFID(String userid, Timestamp time) throws QuoteException;
	public void persistCSRFID(String userid, String CSRFID, Timestamp time)
			throws QuoteException;
}
