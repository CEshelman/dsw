package com.ibm.dsw.quote.loadtest.dao;


import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author julia.liu
 *
 */
public interface SqoLoadTestDao {

	boolean getWebApplCodesByColName(String cnstntName)
			throws TopazException;

}
