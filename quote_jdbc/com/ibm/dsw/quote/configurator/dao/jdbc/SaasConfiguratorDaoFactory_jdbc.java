package com.ibm.dsw.quote.configurator.dao.jdbc;


import com.ibm.dsw.quote.configurator.dao.SaasConfiguratorDao;
import com.ibm.dsw.quote.configurator.dao.SaasConfiguratorDaoFactory;
import com.ibm.dsw.quote.configurator.dao.impl.SaasConfiguratorDaoJdbc;

public class SaasConfiguratorDaoFactory_jdbc extends SaasConfiguratorDaoFactory {
	public SaasConfiguratorDaoFactory_jdbc() {
		super();
	}

	@Override
	public SaasConfiguratorDao create() {
		return new SaasConfiguratorDaoJdbc();
	}
}
