package com.ibm.dsw.automation.vo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Country extends BaseVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5004806304936539440L;

	/**
	 * retrieve Country.properties
	 */
	private static Properties prop;

	/**
	 * Database connection
	 */
	private Connection conn;

	static {
		prop = new Properties();
		InputStream in = Object.class.getResourceAsStream("/com/ibm/dsw/automation/vo/Country.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Country(Connection conn) {
		this.conn = conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	/**
	 * Query for a country's belonging region code and sub region code
	 * 
	 * @param streamCode
	 *            default 'USA'
	 * @return a list that is a json object of name/value pairs.
	 *         The field 'WWIDE_RGN_CODE' retrieve WWIDE_RGN_CODE of SQL 'regionCode',
	 *         The field 'WWIDE_SUB_RGN_CODE' retrieve WWIDE_SUB_RGN_CODE of SQL
	 *         'regionCode'
	 */
	public List<String> getRegionCode(String countryCode) {
		List<String> regionCodes = new ArrayList<String>();
		countryCode = null == countryCode ? "USA" : countryCode;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = prop.getProperty("regionCode");
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, countryCode);
			rs = pstmt.executeQuery();
			StringBuilder dataInfo = null;
			while (rs.next()) {
				dataInfo = new StringBuilder("{");
				dataInfo.append("'WWIDE_RGN_CODE':'").append(rs.getString(1)).append("',");
				dataInfo.append("'WWIDE_SUB_RGN_CODE':'").append(rs.getString(2)).append("'");
				dataInfo.append("}");
				regionCodes.add(dataInfo.toString());
			}
		} catch (SQLException e) {
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
		return regionCodes;
	}

	/**
	 * Query the controlled country code
	 * 
	 * @param quoteNum
	 *            piWebQuoteNum
	 * @return country Code
	 */
	public String getCountryCode(String quoteNum) {
		String countryCode = null;

		CallableStatement callstmt = null;
		ResultSet rs = null;
		String sql = prop.getProperty("countryCode");
		try {
			callstmt = conn.prepareCall(sql);
			callstmt.setString(1, quoteNum);
			rs = callstmt.executeQuery();
			while (rs.next()) {
				countryCode = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (callstmt != null) {
					callstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return countryCode;
	}
}
