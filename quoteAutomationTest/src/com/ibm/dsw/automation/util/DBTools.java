package com.ibm.dsw.automation.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ibm.dsw.automation.common.TestUtil;

/**
 * 
 * @author suchuang
 * @date Jan 11, 2013
 */
public class DBTools {

	/**
	 * logger
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * 
	 */
	private String clazzName = "";

	private Map<String, String> driverMap;

	private final static String MYSQL = "mysql";

	private final static String DB2 = "db2";

	/**
	 * 
	 */
	protected Properties prop;

	/**
	 * 
	 */
	private volatile Map<String, Connection> conns = new HashMap<String, Connection>();

	/**
	 * singleton instance
	 */
	private static DBTools instance = null;

	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 */
	private DBTools() {
		init();
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @return
	 */
	public static DBTools getInstance() {
		if (null == instance) {
			instance = new DBTools();
		}
		return instance;
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 10, 2013
	 */
	public void init() {
		initTestDataProp();
		driverMap = new HashMap<String, String>(2);
		driverMap.put(MYSQL, "com.mysql.jdbc.Driver");
		driverMap.put(DB2, "com.ibm.db2.jcc.DB2Driver");
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 9, 2013
	 */
	protected void initTestDataProp() {
		prop = TestUtil
				.getTestDataProp("/com/ibm/dsw/automation/common/ENV.properties");

		Properties settingsProp = TestUtil
				.getTestDataProp("/com/ibm/dsw/automation/common/Settings.properties");

		prop.putAll(settingsProp);
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 9, 2013
	 * @param path
	 * @return
	 */
	protected Properties getTestDataProp(String path) {
		return TestUtil.getTestDataProp(path);
	}

	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 * @param key
	 * @return String
	 */
	protected String getProperty(String key) {
		return prop.getProperty(key, "");
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @param connKey
	 * @param conn
	 */
	public void putConn(String connKey, Connection conn) {
		if (!conns.containsKey(connKey)) {
			synchronized (conns) {
				if (!conns.containsKey(connKey)) {
					conns.put(connKey, conn);
				}
			}
		}
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @param connKey
	 * @return
	 * @throws Exception
	 */
	public Connection getConn(String connKey) throws Exception {
		synchronized (conns) {
			if (conns.containsKey(connKey)) {
				return conns.get(connKey);
			} else {
				Connection conn = createConn(getProperty("dbdriver"),
						getProperty(connKey + ".dburl"), getProperty("dbuser"),
						DecryptManager.decrypt(getProperty("dbpsswrd")));
				putConn(connKey, conn);
				return conn;
			}
		}
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @param connKey
	 * @return
	 * @throws Exception
	 */
	public Connection getConnection(String connKey) throws Exception {
		Connection conn = createConn(getProperty("dbdriver"),
				getProperty(connKey + ".dburl"), getProperty("dbuser"),
				DecryptManager.decrypt(getProperty("dbpsswrd")));
		putConn(connKey, conn);
		return conn;
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 11, 2013
	 * @param driver
	 * @param url
	 * @param user
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	public Connection createConn(String driver, String url, String user,
			String pwd) throws Exception {
		try {
			Class.forName(driver);
			return DriverManager.getConnection(url, user, pwd);
		} catch (ClassNotFoundException e) {
			logger.fatal("Cannot to find the driver class:: " + driver, e);
		} catch (SQLException e) {
			logger.fatal("Failed to connect to database.", e);
			throw new Exception(e);
		}
		return null;
	}

}
