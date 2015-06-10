package com.ibm.dsw.automation.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.testng.log4testng.Logger;

import com.ibm.dsw.automation.util.DBTools;
import com.ibm.dsw.automation.util.DecryptManager;

public class FunctionIdProvider {

	private static Logger logger = Logger.getLogger(FunctionIdProvider.class);
	
	private static final String SQL0 = "SELECT ENCRYPT_PASSWORD FROM FUNCTIONALID WHERE TESTID = ?;";
	
	public static String getPWDForFuncId(String userid){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String password = "";
		try {
			conn = DBTools.getInstance().getConnection("mysql");
			ps = conn.prepareStatement(SQL0);
			ps.setString(1, userid);
			rs = ps.executeQuery();
			if (null != rs) {
				while (rs.next()) {
					password = rs.getString("ENCRYPT_PASSWORD");
				}
			}
			return DecryptManager.decrypt(password);
		} catch (SQLException e) {
			logger.fatal("Failed to get the password from the database.",e);
			return null;
		} catch (Exception e) {
			logger.fatal("Failed to get the password from the database.",e);
			e.printStackTrace();
			return null;
		}finally{
			try {
				if (null != rs) {
					rs.close();
				}
				if (null != ps) {
					ps.close();
				}
				if (null != conn) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
