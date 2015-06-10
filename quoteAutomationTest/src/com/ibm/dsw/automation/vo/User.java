package com.ibm.dsw.automation.vo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class User extends BaseVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3561465741219718605L;
	private static Properties prop = null;
	private String env = null;
	private String userName;
	private Connection conn;

	public User(String env) {
		super();
		this.env = env;
	}

	public User(String env, Connection conn) {
		super();
		this.env = env;
		this.conn = conn;
	}

	public String getUserName() {
		return userName;
	}

	public String getPGSDemoUserName() {
		return prop.getProperty(env + ".username");
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public String getPGSDemoPassword() {
		return prop.getProperty(env + ".password");
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private String password;

	public static void main(String[] args) {
		User user = new User("fvt");
		System.out.println(user.getPGSDemoUserName());
	}

	public String getMyCurrentWebQutoeNum(String userID) {
		String webQuoteNum = "";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = prop.getProperty("mycurrentwebquote");
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				webQuoteNum = rs.getString("web_quote_num");
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
		return webQuoteNum;
	}

	static {
		prop = new Properties();
		InputStream in = Object.class.getResourceAsStream("/com/ibm/dsw/automation/vo/User.properties");
		try {
			prop.load(in);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
