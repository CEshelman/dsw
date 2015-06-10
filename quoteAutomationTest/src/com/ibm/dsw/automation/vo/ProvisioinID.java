package com.ibm.dsw.automation.vo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class ProvisioinID extends BaseVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8249471190397645615L;
	private static Properties prop;
	/**
	 * Database connection
	 */
	private Connection conn;

	static {
		prop = new Properties();
		InputStream in = Object.class.getResourceAsStream("/com/ibm/dsw/automation/vo/ProvisionID.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ProvisioinID(Connection conn) {
		super();
		this.conn = conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getProvisionID(String quoteNum, String configId) {
		String provisionID = "";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = prop.getProperty("getProvisionSQL");
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, quoteNum);
			pstmt.setString(2, configId);

			System.out.println(pstmt.toString());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				provisionID = rs.getString(1);
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

		return provisionID;
	}

}
