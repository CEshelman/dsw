package com.ibm.dsw.automation.vo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.dsw.automation.common.TestUtil;
import com.ibm.dsw.automation.util.DBTools;

/**
 * 
 * @author suchuang
 * @date Jan 14, 2013
 */
public class Quote extends BaseVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7746012379525030101L;

	/**
	 * logger
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * 
	 */
	private String clazzName = "";

	/**
	 * 
	 */
	protected Properties prop;

	/**
	 * Database connection
	 */
	private Connection conn;

	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @param connKey
	 */
	public Quote(String connKey) {
		try{
			this.conn = DBTools.getInstance().getConn(connKey);
		}catch(Exception e){
			logger.fatal("Failed to get db connection.",e);
		}
		init();
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 10, 2013
	 */
	public void init() {
		initTestDataProp();
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 9, 2013
	 */
	protected void initTestDataProp() {
		clazzName = this.getClass().getSimpleName();
		String path = this.getClass().getPackage().getName();
		path = "/" + path.replace(".", "/");
		path += "/" + clazzName + ".properties";
		prop = this.getTestDataProp(path);
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
	 * @date Jan 10, 2013
	 * @return
	 */
	public Connection getConn() {
		return conn;
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 10, 2013
	 * @param conn
	 */
	public void setConn(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @param id
	 * @return
	 */
	public Map<String, String> getQuote(String id) {
		List<Map<String, String>> quotes = getBeans(getProperty("get_quote_by_id_sql"), id);
		if (null != quotes && quotes.size() > 0) {
			return quotes.get(0);
		}

		return null;
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @param id
	 * @return
	 */
	public Map<String, String> getQuoteLineItemByQuoteNum(String id) {
		List<Map<String, String>> datas = getBeans(getProperty("get_quote_lineitem_by_id_sql"), id);
		if (null != datas && datas.size() > 0) {
			return datas.get(0);
		}

		return null;
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @param quoteNum
	 */
	public List<Map<String, String>> getQuoteCurrentLineitemDatasByQuoteNum(String quoteNum) {
		List<Map<String, String>> datas = getBeans(getProperty("get_quote_current_lineitem_datas_by_quotenum_sql"), quoteNum);
		List<Map<String, String>> seqnums = null;
		String addyear_part_num, addyear_addtnl_years, addyear_seqnum;

		for (Map<String, String> data : datas) {
			addyear_part_num = data.get("PART_NUM");
			addyear_addtnl_years = data.get("RANK_NUM");
			seqnums = getBeans(getProperty("get_quote_current_lineitem_seqnum_sql"), quoteNum, addyear_part_num, addyear_addtnl_years);
			if (null != seqnums && seqnums.size() > 0) {
				addyear_seqnum = seqnums.get(0).get("QUOTE_LINE_ITEM_SEQ_NUM");
				data.put("QUOTE_LINE_ITEM_SEQ_NUM", addyear_seqnum);

				logger.info("addyear_part_num = " + addyear_part_num + ", addyear_addtnl_years = " + addyear_addtnl_years + ", addyear_seqnum = " + addyear_seqnum);
			}
		}

		return datas;
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 10, 2013
	 * @param sql
	 * @param beanCls
	 * @return
	 */
	public List<Object> getBeans(Class<?> beanCls, String sql, String... params) {
		List<Object> lstBean = null;
		Object bean = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			int n = 1;
			for (String p : params) {
				pstmt.setString(n++, p);
			}

			rs = pstmt.executeQuery();
			if (null != rs) {
				lstBean = new ArrayList<Object>();
				while (rs.next()) {
					bean = beanCls.newInstance();
					lstBean.add(bean);

					ResultSetMetaData rsmd = rs.getMetaData();
					int colCount = rsmd.getColumnCount();
					int i = 1;
					for (; i <= colCount; i++) {
						String colName = rsmd.getColumnName(i);
						Method setM = null;
						String mName = "set" + StringUtils.capitalise(colName.toLowerCase());
						try {
							setM = beanCls.getDeclaredMethod(mName, String.class);
						} catch (NoSuchMethodException e) {
							logger.warn("No such bean method : [" + mName + "]");
						}

						if (null != setM) {
							String value = null != rs.getString(i) ? rs.getString(i).trim() : rs.getString(i);
							setM.invoke(bean, value);
						}

					}
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return lstBean;
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @param sql
	 * @return
	 */
	public List<Map<String, String>> getBeans(String sql, String... params) {
		List<Map<String, String>> lstBean = null;
		Map<String, String> bean = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			logger.info("sql:[" + sql + "]");
			pstmt = conn.prepareStatement(sql);
			int n = 1;
			for (String p : params) {
				pstmt.setString(n++, p);
			}

			rs = pstmt.executeQuery();
			if (null != rs) {
				lstBean = new ArrayList<Map<String, String>>();
				while (rs.next()) {
					bean = new HashMap<String, String>();
					lstBean.add(bean);

					ResultSetMetaData rsmd = rs.getMetaData();
					int colCount = rsmd.getColumnCount();
					int i = 1;
					for (; i <= colCount; i++) {
						String colName = rsmd.getColumnName(i);
						String value = null != rs.getString(i) ? rs.getString(i).trim() : rs.getString(i);
						bean.put(colName, value);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return lstBean;
	}
}
