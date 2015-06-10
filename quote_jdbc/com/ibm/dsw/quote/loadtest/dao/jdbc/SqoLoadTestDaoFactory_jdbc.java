package com.ibm.dsw.quote.loadtest.dao.jdbc;

import com.ibm.dsw.quote.loadtest.dao.SqoLoadTestDao;
import com.ibm.dsw.quote.loadtest.dao.SqoLoadTestDaoFactory;
import com.ibm.dsw.quote.loadtest.dao.impl.SqoLoadTestDaoJdbc;

/**
 * @author julia.liu
 *
 */
public class SqoLoadTestDaoFactory_jdbc extends SqoLoadTestDaoFactory {

	public SqoLoadTestDaoFactory_jdbc() {
		super();
	}
	
	
	@Override
	public SqoLoadTestDao create() {
		return new SqoLoadTestDaoJdbc();
	}

}
