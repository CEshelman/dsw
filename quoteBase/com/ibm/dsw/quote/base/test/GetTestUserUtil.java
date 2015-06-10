package com.ibm.dsw.quote.base.test;


import is.domainx.User;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>GetTestUserUtil<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * updated by junqingz@cn.ibm.com
 * 
 * Creation date: Aug 7, 2007
 */

public class GetTestUserUtil {
	private static String strSQL = "CALL TEST.S_QT_TEST_USER (?,?,?,?,?,?,?,?,?)";


    public static User getTestUser() throws TopazException {
        TestUser user = new TestUser();
        try {
            //begin topaz transaction
            TransactionContextManager.singleton().begin();
            CallableStatement callStmt = TopazUtil.getConnection().prepareCall(strSQL);
			callStmt.registerOutParameter(1, Types.INTEGER);
			callStmt.registerOutParameter(2, Types.VARCHAR);
			callStmt.registerOutParameter(3, Types.VARCHAR);
			callStmt.registerOutParameter(4, Types.VARCHAR);
			callStmt.registerOutParameter(5, Types.VARCHAR);
			callStmt.registerOutParameter(6, Types.VARCHAR);
			callStmt.registerOutParameter(7, Types.VARCHAR);
			callStmt.setInt(8, 0);
			callStmt.setString(9, "INTERNAL");
            callStmt.execute();

            user.setEmail(StringUtils.trimToEmpty(callStmt.getString(2)));
            //end topaz transaction
            TransactionContextManager.singleton().commit();
        } catch (SQLException e) {
            throw new TopazException(e);
        } catch (TopazException te) {
            throw new TopazException(te);
        } finally {
            TransactionContextManager.singleton().rollback();
        }
        return user;
    }
    
    public static User getLoginTestUser() throws TopazException {
        TestUser user = new TestUser();
        try {
            //begin topaz transaction
            TransactionContextManager.singleton().begin();
            CallableStatement callStmt = TopazUtil.getConnection().prepareCall(strSQL);
			callStmt.registerOutParameter(1, Types.INTEGER);
			callStmt.registerOutParameter(2, Types.VARCHAR);
			callStmt.registerOutParameter(3, Types.VARCHAR);
			callStmt.registerOutParameter(4, Types.VARCHAR);
			callStmt.registerOutParameter(5, Types.VARCHAR);
			callStmt.registerOutParameter(6, Types.VARCHAR);
			callStmt.registerOutParameter(7, Types.VARCHAR);
			callStmt.setInt(8, 99);
			callStmt.setString(9, "INTERNAL");
            callStmt.execute();

            user.setEmail(StringUtils.trimToEmpty(callStmt.getString(2)));
            //end topaz transaction
            TransactionContextManager.singleton().commit();
        } catch (SQLException e) {
            throw new TopazException(e);
        } catch (TopazException te) {
            throw new TopazException(te);
        } finally {
            TransactionContextManager.singleton().rollback();
        }
        return user;
    }

    public static TestUser getPGSLoginTestUser() throws TopazException {
		LogContext logger = LogContextFactory.singleton().getLogContext();
        
        TestUser user = new TestUser();
        try {
            //begin topaz transaction
            TransactionContextManager.singleton().begin();
            CallableStatement callStmt = TopazUtil.getConnection().prepareCall(strSQL);
			callStmt.registerOutParameter(1, Types.INTEGER);
			callStmt.registerOutParameter(2, Types.VARCHAR);
			callStmt.registerOutParameter(3, Types.VARCHAR);
			callStmt.registerOutParameter(4, Types.VARCHAR);
			callStmt.registerOutParameter(5, Types.VARCHAR);
			callStmt.registerOutParameter(6, Types.VARCHAR);
			callStmt.registerOutParameter(7, Types.VARCHAR);
			callStmt.setInt(8, 99);
			callStmt.setString(9, "PSPTRSEL");
            callStmt.execute();

            user.setUserId(StringUtils.trimToEmpty(callStmt.getString(2)));
            user.setEmail(StringUtils.trimToEmpty(callStmt.getString(3)));
            user.setFirstName(StringUtils.trimToEmpty(callStmt.getString(4)));
            user.setLastName(StringUtils.trimToEmpty(callStmt.getString(5)));
            user.setUniqueID(StringUtils.trimToEmpty(callStmt.getString(6)));
            user.setSAPNumber(StringUtils.trimToEmpty(callStmt.getString(7)));
            logger.debug(null, user.toString());            
            //end topaz transaction
            TransactionContextManager.singleton().commit();
        } catch (SQLException e) {
            throw new TopazException(e);
        } catch (TopazException te) {
            throw new TopazException(te);
        } finally {
            TransactionContextManager.singleton().rollback();
        }
        return user;
    }
    public static TestUser getPGSTestUser() throws TopazException {
		LogContext logger = LogContextFactory.singleton().getLogContext();
        TestUser user = new TestUser();
        try {
            //begin topaz transaction
            TransactionContextManager.singleton().begin();
            CallableStatement callStmt = TopazUtil.getConnection().prepareCall(strSQL);
			callStmt.registerOutParameter(1, Types.INTEGER);
			callStmt.registerOutParameter(2, Types.VARCHAR);
			callStmt.registerOutParameter(3, Types.VARCHAR);
			callStmt.registerOutParameter(4, Types.VARCHAR);
			callStmt.registerOutParameter(5, Types.VARCHAR);
			callStmt.registerOutParameter(6, Types.VARCHAR);
			callStmt.registerOutParameter(7, Types.VARCHAR);
			callStmt.setInt(8, 0);
			callStmt.setString(9, "PSPTRSEL");
            callStmt.execute();

            user.setUserId(StringUtils.trimToEmpty(callStmt.getString(2)));
            user.setEmail(StringUtils.trimToEmpty(callStmt.getString(3)));
            user.setFirstName(StringUtils.trimToEmpty(callStmt.getString(4)));
            user.setLastName(StringUtils.trimToEmpty(callStmt.getString(5)));
            user.setUniqueID(StringUtils.trimToEmpty(callStmt.getString(6)));
            user.setSAPNumber(StringUtils.trimToEmpty(callStmt.getString(7)));
            logger.debug(null, user.toString());            

            //end topaz transaction
            TransactionContextManager.singleton().commit();
        } catch (SQLException e) {
            throw new TopazException(e);
        } catch (TopazException te) {
            throw new TopazException(te);
        } finally {
            TransactionContextManager.singleton().rollback();
        }
        return user;
    }
}
