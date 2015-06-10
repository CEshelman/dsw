package com.ibm.dsw.automation.vo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Customer extends BaseVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -586529815091343819L;

	/**
	 * retrieve Customer.properties
	 */
	private static Properties prop;

	/**
	 * Database connection
	 */
	private Connection conn;

	static {
		prop = new Properties();
		InputStream in = Object.class.getResourceAsStream("/com/ibm/dsw/automation/vo/Customer.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Customer(Connection conn) {
		this.conn = conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	/**
	 * get web identity id for customer
	 * 
	 * @param custNum
	 *            customer's number
	 * @return unique user identity
	 */
	public String getUserId(String custNum) {
		String userId = null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = prop.getProperty("userId");
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, custNum);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				userId = rs.getString(1);
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
		return userId;
	}

	/**
	 * Query the government customers
	 * 
	 * @param govtEntityCode
	 *            default 1
	 * @return a list that contain cust_num of customer.
	 */
	public List<String> getCustomerNums(Integer govtEntityCode) {
		List<String> customerNums = new ArrayList<String>();
		govtEntityCode = null == govtEntityCode ? 1 : govtEntityCode;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = prop.getProperty("customerNum");
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, govtEntityCode);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				customerNums.add(rs.getString(1));
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
		return customerNums;
	}

	/**
	 * Add mass delegation for someone
	 * 
	 * @param piUserID
	 * @param piDlgtnUserID
	 * @param piManagerID
	 * @param piEmpCntryCode2
	 * @param piNotesID
	 * @param piEmpNameFull
	 * @param piEmpFirstName
	 * @param piEmpLastName
	 * @param piIntlPhnNumFull
	 * @param piIntlFaxNumFull
	 * @param piInactFlag
	 * @return
	 */
	public Integer addMassDelegation(String piUserID, String piDlgtnUserID, String piManagerID, String piEmpCntryCode2, String piNotesID, String piEmpNameFull, String piEmpFirstName, String piEmpLastName, String piIntlPhnNumFull, String piIntlFaxNumFull, Integer piInactFlag) {
		Integer poGenStatus = null;

		CallableStatement callstmt = null;
		ResultSet rs = null;
		String sql = prop.getProperty("addMassDelegation");
		try {
			callstmt = conn.prepareCall(sql);
			callstmt.registerOutParameter(1, Types.INTEGER);
			callstmt.setString(2, piUserID);
			callstmt.setString(3, piDlgtnUserID);
			callstmt.setString(4, piManagerID);
			callstmt.setString(5, piEmpCntryCode2);
			callstmt.setString(6, piNotesID);
			callstmt.setString(7, piEmpNameFull);
			callstmt.setString(8, piEmpFirstName);
			callstmt.setString(9, piEmpLastName);
			callstmt.setString(10, piIntlPhnNumFull);
			callstmt.setString(11, piIntlFaxNumFull);
			callstmt.setInt(12, piInactFlag);
			callstmt.execute();
			poGenStatus = callstmt.getInt(1);
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
		return poGenStatus;
	}

	/**
	 * Query the distributor for given reseller
	 * 
	 * @param piRselCustNum
	 *            piWebQuoteNum
	 * @return distributor
	 */
	public String getDistributor(String piRselCustNum) {
		String distributor = "";

		CallableStatement callstmt = null;
		ResultSet rs = null;
		String sql = prop.getProperty("queryDistributor");
		try {
			callstmt = conn.prepareCall(sql);
			callstmt.setString(1, piRselCustNum);
			rs = callstmt.executeQuery();
			while (rs.next()) {
				distributor = rs.getString(1);
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
		return distributor;
	}

	/**
	 * Get the cust num which can do add-on / trade up
	 * 
	 * @return part number
	 */
	public List<String> getCustomNumber() {
		List<String> partNums = new ArrayList<String>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = prop.getProperty("queryCustomerNum");
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				partNums.add(rs.getString(1));
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
		return partNums;
	}
}
