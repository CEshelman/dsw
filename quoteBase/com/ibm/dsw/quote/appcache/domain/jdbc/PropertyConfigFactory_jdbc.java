package com.ibm.dsw.quote.appcache.domain.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.config.CacheDBConstants;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.PropertyConfigFactory;
import com.ibm.dsw.quote.base.cache.QuoteTopazCacheableFactoryHelper;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;
import com.ibm.ead4j.topaz.persistence.jdbc.KeyedPersistentObject;
import com.ibm.ead4j.topaz.persistence.jdbc.TopazReadOnlyFactory;

public class PropertyConfigFactory_jdbc extends PropertyConfigFactory implements
TopazReadOnlyFactory {
	private static final String PROPERTY_CONFIG_CNSTNT_NAME = "PROPERTYCONFIG";

	@Override
	public String getPropertyValueByName(String name) throws TopazException {
		LogContext logCtx = LogContextFactory.singleton().getLogContext();
		try {
			Object object = getFromCache(name);
			if(object != null){
				CodeDescObj obj = (CodeDescObj)object;
				return obj.getCodeDesc();
				
			}
		} catch (TopazException e) {
			logCtx.error(this,
					"Can not get the url config from db for url name" + name);
		}

		return "";
	}

	@Override
	public String getSelectSqlQueryStringKey() {
		return CacheDBConstants.DB2_S_WEB_APP_CNSTNT;
	}

	@Override
	public HashMap getSelectSqlParms() {
		ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
		String appName = appContext.getConfigParameter("appNameForCache");
		
		HashMap params = new HashMap();
		params.put("piCnstntName", appName);
		params.put("piCode", "");
		params.put("piColName", PROPERTY_CONFIG_CNSTNT_NAME);
		return params;
	}

	@Override
	public KeyedPersistentObject hydrate(ResultSet rs, boolean checkCache)
			throws TopazException {
		LogContext logCtx = LogContextFactory.singleton().getLogContext();
		CodeDescObj_jdbc propertyConfig = null;
		
		try {
			String code = StringUtils.trimToEmpty(rs.getString("CODE"));
			if (checkCache) {
				propertyConfig = (CodeDescObj_jdbc) this.getFromCache(code);
			}
			if (propertyConfig == null) {
				String codeDscr = StringUtils.trimToEmpty(rs
						.getString("CODE_DSCR"));
				propertyConfig = new CodeDescObj_jdbc(code, codeDscr);
				logCtx.debug(this, "Loading property configuration from DB: key -> " + code + "; value -> "+ codeDscr);
			}

		} catch (SQLException sqle) {
			throw new TopazException(sqle);
		}
		return propertyConfig;
	}

	@Override
	public void putInCache(Object objectId, Object object)
			throws TopazException {
		TransactionContextManager.singleton().getTransactionContext()
				.put(this.getClass(), objectId, object);
	}

	@Override
	public Object getFromCache(Object objectId) throws TopazException {
		Object object = TransactionContextManager.singleton().getTransactionContext().get(PropertyConfigFactory.class, objectId);
		if (null == object) {
			object = QuoteTopazCacheableFactoryHelper.singleton().getFromGlobalCache(this, objectId);
		}
		return object;
	}
}
