package com.ibm.dsw.quote.appcache.domain;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.QuotePrintSPTimeTraceHelper;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * Implements CallableStatement
 * This <code>QuoteCallableStatementWrapper<code> class is implements CallableStatement .
 *    
 * @author: <a href="jiewbj@cn.ibm.com">Crespo </a>
 * 
 * Creation date: October 1, 2013
 */
public class QuoteCallableStatementWrapper implements CallableStatement {
	private LogContext logContext = LogContextFactory.singleton().getLogContext();
	private CallableStatement callablestatement=null;
	private String sql="";
	private void setSql(String sql) {
		this.sql = sql;
	}
	public QuoteCallableStatementWrapper(CallableStatement callablestatement,String sql) {
		this.callablestatement=callablestatement;
		setSql(sql);
	 }
	@Override
	public void addBatch() throws SQLException {
		
		callablestatement.addBatch();
	}

	@Override
	public void clearParameters() throws SQLException {
		callablestatement.clearParameters();
	}

	@Override
	public boolean execute() throws SQLException {
		Long startTime,endTime;
		startTime=System.currentTimeMillis();
		boolean ex=callablestatement.execute();
		endTime=System.currentTimeMillis();
		try {
			QuotePrintSPTimeTraceHelper.printSpExecuteTime(sql, (endTime-startTime));
		} catch (QuoteException e) {
			logContext.error(this,e.getMessage()); 
		} catch (Exception e){
			logContext.error(this,e.getMessage()); 
		}
		return ex;
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		return callablestatement.executeQuery();
	}

	@Override
	public int executeUpdate() throws SQLException {
		return callablestatement.executeUpdate();
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return callablestatement.getMetaData();
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return callablestatement.getParameterMetaData();
	}

	@Override
	public void setArray(int parameterIndex, Array theArray)
			throws SQLException {
		callablestatement.setArray(parameterIndex, theArray);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream theInputStream,
			int length) throws SQLException {
		callablestatement.setAsciiStream(parameterIndex, theInputStream,length);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal theBigDecimal)
			throws SQLException {
		callablestatement.setBigDecimal(parameterIndex, theBigDecimal);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream theInputStream,
			int length) throws SQLException {
		callablestatement.setBinaryStream(parameterIndex,theInputStream,length);
	}

	@Override
	public void setBlob(int parameterIndex, Blob theBlob) throws SQLException {
		callablestatement.setBlob(parameterIndex, theBlob);

	}

	@Override
	public void setBoolean(int parameterIndex, boolean theBoolean)
			throws SQLException {
		callablestatement.setBoolean(parameterIndex, theBoolean);

	}

	@Override
	public void setByte(int parameterIndex, byte theByte) throws SQLException {
		callablestatement.setByte(parameterIndex, theByte);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] theBytes)
			throws SQLException {
		callablestatement.setBytes(parameterIndex, theBytes);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		callablestatement.setCharacterStream(parameterIndex, reader,length);
	}

	@Override
	public void setClob(int parameterIndex, Clob theClob) throws SQLException {
		callablestatement.setClob(parameterIndex, theClob);
	}

	@Override
	public void setDate(int parameterIndex, Date theDate) throws SQLException {
		callablestatement.setDate(parameterIndex, theDate);
	}

	@Override
	public void setDate(int parameterIndex, Date theDate, Calendar cal)
			throws SQLException {
		callablestatement.setDate(parameterIndex, theDate, cal);
	}

	@Override
	public void setDouble(int parameterIndex, double theDouble)
			throws SQLException {
		callablestatement.setDouble(parameterIndex, theDouble);
	}

	@Override
	public void setFloat(int parameterIndex, float theFloat)
			throws SQLException {
		callablestatement.setFloat(parameterIndex, theFloat);
	}

	@Override
	public void setInt(int parameterIndex, int theInt) throws SQLException {
		callablestatement.setInt(parameterIndex, theInt);
	}

	@Override
	public void setLong(int parameterIndex, long theLong) throws SQLException {
		callablestatement.setLong(parameterIndex, theLong);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		callablestatement.setNull(parameterIndex, sqlType);
	}

	@Override
	public void setNull(int paramIndex, int sqlType, String typeName)
			throws SQLException {
		callablestatement.setNull(paramIndex, sqlType,typeName);
	}

	@Override
	public void setObject(int parameterIndex, Object theObject)
			throws SQLException {
		callablestatement.setObject(parameterIndex, theObject);
	}

	@Override
	public void setObject(int parameterIndex, Object theObject,
			int targetSqlType) throws SQLException {
		callablestatement.setObject(parameterIndex, theObject, targetSqlType);
	}

	@Override
	public void setObject(int parameterIndex, Object theObject,
			int targetSqlType, int scale) throws SQLException {
		callablestatement.setObject(parameterIndex, theObject, targetSqlType,scale);
	}

	@Override
	public void setRef(int parameterIndex, Ref theRef) throws SQLException {
		callablestatement.setRef(parameterIndex, theRef);
	}

	@Override
	public void setShort(int parameterIndex, short theShort)
			throws SQLException {
		callablestatement.setShort(parameterIndex, theShort);
	}

	@Override
	public void setString(int parameterIndex, String theString)
			throws SQLException {
		callablestatement.setString(parameterIndex, theString);
	}

	@Override
	public void setTime(int parameterIndex, Time theTime) throws SQLException {
		callablestatement.setTime(parameterIndex, theTime);
	}

	@Override
	public void setTime(int parameterIndex, Time theTime, Calendar cal)
			throws SQLException {
		callablestatement.setTime(parameterIndex, theTime, cal);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp theTimestamp)
			throws SQLException {
		callablestatement.setTimestamp(parameterIndex, theTimestamp);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp theTimestamp,
			Calendar cal) throws SQLException {
		callablestatement.setTimestamp(parameterIndex, theTimestamp, cal);
	};

	@Override
	public void setUnicodeStream(int parameterIndex,
			InputStream theInputStream, int length) throws SQLException {
		callablestatement.setUnicodeStream(parameterIndex, theInputStream, length);
	}

	@Override
	public void setURL(int parameterIndex, URL theURL) throws SQLException {
		callablestatement.setURL(parameterIndex, theURL);
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		callablestatement.setRowId(parameterIndex, x);
	}

	@Override
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		callablestatement.setNString(parameterIndex, value);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		callablestatement.setNCharacterStream(parameterIndex, value, length);
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		callablestatement.setNClob(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		callablestatement.setClob(parameterIndex, reader, length);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		callablestatement.setBlob(parameterIndex, inputStream, length);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		callablestatement.setNClob(parameterIndex, reader, length);
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		callablestatement.setSQLXML(parameterIndex, xmlObject);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		callablestatement.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		callablestatement.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		callablestatement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		callablestatement.setAsciiStream(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		callablestatement.setBinaryStream(parameterIndex, x);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		callablestatement.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		callablestatement.setNCharacterStream(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		callablestatement.setClob(parameterIndex, reader);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		callablestatement.setBlob(parameterIndex, inputStream);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		callablestatement.setNClob(parameterIndex, reader);
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		callablestatement.addBatch(sql);
	}

	@Override
	public void cancel() throws SQLException {
		callablestatement.cancel();
	}

	@Override
	public void clearBatch() throws SQLException {
		callablestatement.clearBatch();
	}

	@Override
	public void clearWarnings() throws SQLException {
		callablestatement.clearWarnings();
	}

	@Override
	public void close() throws SQLException {
		callablestatement.close();
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		return callablestatement.execute(sql);
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		return callablestatement.execute(sql, autoGeneratedKeys);
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return callablestatement.execute(sql, columnIndexes);
	}

	@Override
	public boolean execute(String sql, String[] columnNames)
			throws SQLException {
		return callablestatement.execute(sql, columnNames);
	}

	@Override
	public int[] executeBatch() throws SQLException {
		return callablestatement.executeBatch();
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		return callablestatement.executeQuery(sql);
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		return callablestatement.executeUpdate(sql);
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {
		return callablestatement.executeUpdate(sql, autoGeneratedKeys);
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		return callablestatement.executeUpdate(sql, columnIndexes);
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames)
			throws SQLException {
		return callablestatement.executeUpdate(sql, columnNames);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return callablestatement.getConnection();
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return callablestatement.getFetchDirection();
	}

	@Override
	public int getFetchSize() throws SQLException {
		return callablestatement.getFetchSize();
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		return callablestatement.getGeneratedKeys();
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return callablestatement.getMaxFieldSize();
	}

	@Override
	public int getMaxRows() throws SQLException {
		return callablestatement.getMaxRows();
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		return callablestatement.getMoreResults();
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return callablestatement.getMoreResults(current);
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return callablestatement.getQueryTimeout();
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		return callablestatement.getResultSet();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		return callablestatement.getResultSetConcurrency();
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return callablestatement.getResultSetHoldability();
	}

	@Override
	public int getResultSetType() throws SQLException {
		return callablestatement.getResultSetType();
	}

	@Override
	public int getUpdateCount() throws SQLException {
		return callablestatement.getUpdateCount();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return callablestatement.getWarnings();
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		callablestatement.setCursorName(name);
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		callablestatement.setEscapeProcessing(enable);

	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		
		callablestatement.setFetchDirection(direction);
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		
		callablestatement.setFetchSize(rows);
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		
		callablestatement.setMaxFieldSize(max);
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		
		callablestatement.setMaxRows(max);
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		
		callablestatement.setQueryTimeout(seconds);
	}

	@Override
	public boolean isClosed() throws SQLException {
		
		return callablestatement.isClosed();
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		
		callablestatement.setPoolable(poolable); 
	}

	@Override
	public boolean isPoolable() throws SQLException {
		
		return callablestatement.isPoolable();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		
		return callablestatement.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		
		return callablestatement.isWrapperFor(iface);
	}

	@Override
	public Array getArray(int parameterIndex) throws SQLException {
		
		return callablestatement.getArray(parameterIndex);
	}

	@Override
	public Array getArray(String parameterName) throws SQLException {
		
		return callablestatement.getArray(parameterName);
	}

	@Override
	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		
		return callablestatement.getBigDecimal(parameterIndex);
	}

	@Override
	public BigDecimal getBigDecimal(int parameterIndex, int scale)
			throws SQLException {
		
		return callablestatement.getBigDecimal(parameterIndex,scale);
	}

	@Override
	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		
		return callablestatement.getBigDecimal(parameterName);
	}

	@Override
	public Blob getBlob(int parameterIndex) throws SQLException {
		
		return callablestatement.getBlob(parameterIndex);
	}

	@Override
	public Blob getBlob(String parameterName) throws SQLException {
		
		return callablestatement.getBlob(parameterName);
	}

	@Override
	public boolean getBoolean(int parameterIndex) throws SQLException {
		
		return callablestatement.getBoolean(parameterIndex);
	}

	@Override
	public boolean getBoolean(String parameterName) throws SQLException {
		
		return callablestatement.getBoolean(parameterName);
	}

	@Override
	public byte getByte(int parameterIndex) throws SQLException {
		
		return callablestatement.getByte(parameterIndex);
	}

	@Override
	public byte getByte(String parameterName) throws SQLException {
		
		return callablestatement.getByte(parameterName);
	}

	@Override
	public byte[] getBytes(int parameterIndex) throws SQLException {
		
		return callablestatement.getBytes(parameterIndex);
	}

	@Override
	public byte[] getBytes(String parameterName) throws SQLException {
		
		return callablestatement.getBytes(parameterName);
	}

	@Override
	public Clob getClob(int parameterIndex) throws SQLException {
		
		return callablestatement.getClob(parameterIndex);
	}

	@Override
	public Clob getClob(String parameterName) throws SQLException {
		
		return callablestatement.getClob(parameterName);
	}

	@Override
	public Date getDate(int parameterIndex) throws SQLException {
		
		return callablestatement.getDate(parameterIndex);
	}

	@Override
	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		
		return callablestatement.getDate(parameterIndex,cal);
	}

	@Override
	public Date getDate(String parameterName) throws SQLException {
		
		return callablestatement.getDate(parameterName);
	}

	@Override
	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		
		return callablestatement.getDate(parameterName, cal);
	}

	@Override
	public double getDouble(int parameterIndex) throws SQLException {
		
		return callablestatement.getDouble(parameterIndex);
	}

	@Override
	public double getDouble(String parameterName) throws SQLException {
		
		return callablestatement.getDouble(parameterName);
	}

	@Override
	public float getFloat(int parameterIndex) throws SQLException {
		
		return callablestatement.getFloat(parameterIndex);
	}

	@Override
	public float getFloat(String parameterName) throws SQLException {
		
		return callablestatement.getFloat(parameterName);
	}

	@Override
	public int getInt(int parameterIndex) throws SQLException {
		
		return callablestatement.getInt(parameterIndex);
	}

	@Override
	public int getInt(String parameterName) throws SQLException {
		
		return callablestatement.getInt(parameterName);
	}

	@Override
	public long getLong(int parameterIndex) throws SQLException {
		
		return callablestatement.getLong(parameterIndex);
	}

	@Override
	public long getLong(String parameterName) throws SQLException {
		
		return callablestatement.getLong(parameterName);
	}

	@Override
	public Object getObject(int parameterIndex) throws SQLException {
		
		return callablestatement.getObject(parameterIndex);
	}

	@Override
	public Object getObject(int parameterIndex, Map<String, Class<?>> map)
			throws SQLException {
		
		return callablestatement.getObject(parameterIndex, map);
	}

	@Override
	public Object getObject(String parameterName) throws SQLException {
		
		return callablestatement.getObject(parameterName);
	}

	@Override
	public Object getObject(String parameterName, Map<String, Class<?>> map)
			throws SQLException {
		
		return callablestatement.getObject(parameterName, map);
	}

	@Override
	public Ref getRef(int parameterIndex) throws SQLException {
		
		return callablestatement.getRef(parameterIndex);
	}

	@Override
	public Ref getRef(String parameterName) throws SQLException {
		
		return callablestatement.getRef(parameterName);
	}

	@Override
	public short getShort(int parameterIndex) throws SQLException {
		
		return callablestatement.getShort(parameterIndex);
	}

	@Override
	public short getShort(String parameterName) throws SQLException {
		
		return callablestatement.getShort(parameterName);
	}

	@Override
	public String getString(int parameterIndex) throws SQLException {
		
		return callablestatement.getString(parameterIndex);
	}

	@Override
	public String getString(String parameterName) throws SQLException {
		
		return callablestatement.getString(parameterName);
	}

	@Override
	public Time getTime(int parameterIndex) throws SQLException {
		
		return callablestatement.getTime(parameterIndex);
	}

	@Override
	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		
		return callablestatement.getTime(parameterIndex, cal);
	}

	@Override
	public Time getTime(String parameterName) throws SQLException {
		
		return callablestatement.getTime(parameterName);
	}

	@Override
	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		
		return callablestatement.getTime(parameterName, cal);
	}

	@Override
	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		
		return callablestatement.getTimestamp(parameterIndex);
	}

	@Override
	public Timestamp getTimestamp(int parameterIndex, Calendar cal)
			throws SQLException {
		
		return callablestatement.getTimestamp(parameterIndex, cal);
	}

	@Override
	public Timestamp getTimestamp(String parameterName) throws SQLException {
		
		return callablestatement.getTimestamp(parameterName);
	}

	@Override
	public Timestamp getTimestamp(String parameterName, Calendar cal)
			throws SQLException {
		
		return callablestatement.getTimestamp(parameterName, cal);
	}

	@Override
	public URL getURL(int parameterIndex) throws SQLException {
		
		return callablestatement.getURL(parameterIndex);
	}

	@Override
	public URL getURL(String parameterName) throws SQLException {
		
		return callablestatement.getURL(parameterName);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType)
			throws SQLException {
		
		callablestatement.registerOutParameter(parameterIndex, sqlType);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, int scale)
			throws SQLException {
		
		callablestatement.registerOutParameter(parameterIndex, sqlType, scale);
	}

	@Override
	public void registerOutParameter(int paramIndex, int sqlType,
			String typeName) throws SQLException {
		
		callablestatement.registerOutParameter(paramIndex, sqlType, typeName);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType)
			throws SQLException {
		
		callablestatement.registerOutParameter(parameterName, sqlType);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType,
			int scale) throws SQLException {
		
		callablestatement.registerOutParameter(parameterName, sqlType, scale);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType,
			String typeName) throws SQLException {
		
		callablestatement.registerOutParameter(parameterName, sqlType, typeName);
	}

	@Override
	public void setAsciiStream(String parameterName,
			InputStream theInputStream, int length) throws SQLException {
		
		callablestatement.setAsciiStream(parameterName, theInputStream, length) ;
	}

	@Override
	public void setBigDecimal(String parameterName, BigDecimal theBigDecimal)
			throws SQLException {
		
		callablestatement.setBigDecimal(parameterName, theBigDecimal);
	}

	@Override
	public void setBinaryStream(String parameterName,
			InputStream theInputStream, int length) throws SQLException {
		
		callablestatement.setBinaryStream(parameterName, theInputStream, length);
	}

	@Override
	public void setBoolean(String parameterName, boolean theBoolean)
			throws SQLException {
		
		callablestatement.setBoolean(parameterName, theBoolean);
	}

	@Override
	public void setByte(String parameterName, byte theByte) throws SQLException {
		
		callablestatement.setByte(parameterName, theByte);
	}

	@Override
	public void setBytes(String parameterName, byte[] theBytes)
			throws SQLException {
		
		callablestatement.setBytes(parameterName, theBytes);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader,
			int length) throws SQLException {
		
		callablestatement.setCharacterStream(parameterName, reader, length);
	}

	@Override
	public void setDate(String parameterName, Date theDate) throws SQLException {
		
		callablestatement.setDate(parameterName, theDate) ;
	}

	@Override
	public void setDate(String parameterName, Date theDate, Calendar cal)
			throws SQLException {
		
		callablestatement.setDate(parameterName, theDate, cal);
	}

	@Override
	public void setDouble(String parameterName, double theDouble)
			throws SQLException {
		
		callablestatement.setDouble(parameterName, theDouble);
	}

	@Override
	public void setFloat(String parameterName, float theFloat)
			throws SQLException {
		
		callablestatement.setFloat(parameterName, theFloat);
	}

	@Override
	public void setInt(String parameterName, int theInt) throws SQLException {
		
		callablestatement.setInt(parameterName, theInt);
	}

	@Override
	public void setLong(String parameterName, long theLong) throws SQLException {
		
		callablestatement.setLong(parameterName, theLong);
	}

	@Override
	public void setNull(String parameterName, int sqlType) throws SQLException {
		
		callablestatement.setNull(parameterName, sqlType);
	}

	@Override
	public void setNull(String parameterName, int sqlType, String typeName)
			throws SQLException {
		
		callablestatement.setNull(parameterName, sqlType, typeName);
	}

	@Override
	public void setObject(String parameterName, Object theObject)
			throws SQLException {
		
		callablestatement.setObject(parameterName, theObject);
	}

	@Override
	public void setObject(String parameterName, Object theObject,
			int targetSqlType) throws SQLException {
		
		callablestatement.setObject(parameterName, theObject, targetSqlType);
	}

	@Override
	public void setObject(String parameterName, Object theObject,
			int targetSqlType, int scale) throws SQLException {
		
		callablestatement.setObject(parameterName, theObject, targetSqlType, scale);
	}

	@Override
	public void setShort(String parameterName, short theShort)
			throws SQLException {
		
		callablestatement.setShort(parameterName, theShort);
	}

	@Override
	public void setString(String parameterName, String theString)
			throws SQLException {
		
		callablestatement.setString(parameterName, theString);
	}

	@Override
	public void setTime(String parameterName, Time theTime) throws SQLException {
		
		callablestatement.setTime(parameterName, theTime);
	}

	@Override
	public void setTime(String parameterName, Time theTime, Calendar cal)
			throws SQLException {
		
		callablestatement.setTime(parameterName, theTime, cal);
	}

	@Override
	public void setTimestamp(String parameterName, Timestamp theTimestamp)
			throws SQLException {
		
		callablestatement.setTimestamp(parameterName, theTimestamp);
	}

	@Override
	public void setTimestamp(String parameterName, Timestamp theTimestamp,
			Calendar cal) throws SQLException {
		
		callablestatement.setTimestamp(parameterName, theTimestamp, cal);
	}

	@Override
	public void setURL(String parameterName, URL theURL) throws SQLException {
		
		callablestatement.setURL(parameterName, theURL);
	}

	@Override
	public boolean wasNull() throws SQLException {
		
		return callablestatement.wasNull();
	}

	@Override
	public RowId getRowId(int parameterIndex) throws SQLException {
		
		return callablestatement.getRowId(parameterIndex);
	}

	@Override
	public RowId getRowId(String parameterName) throws SQLException {
		
		return callablestatement.getRowId(parameterName);
	}

	@Override
	public void setRowId(String parameterName, RowId x) throws SQLException {
		
		callablestatement.setRowId(parameterName, x);
	}

	@Override
	public void setNString(String parameterName, String value)
			throws SQLException {
		
		callablestatement.setNString(parameterName, value);
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value,
			long length) throws SQLException {
		
		callablestatement.setNCharacterStream(parameterName, value, length);
	}

	@Override
	public void setNClob(String parameterName, NClob value) throws SQLException {
		
		callablestatement.setNClob(parameterName, value);
	}

	@Override
	public void setClob(String parameterName, Reader reader, long length)
			throws SQLException {
		
		callablestatement.setClob(parameterName, reader, length);
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream,
			long length) throws SQLException {
		
		callablestatement.setBlob(parameterName, inputStream, length);
	}

	@Override
	public void setNClob(String parameterName, Reader reader, long length)
			throws SQLException {
		
		callablestatement.setNClob(parameterName, reader, length);
	}

	@Override
	public NClob getNClob(int parameterIndex) throws SQLException {
		
		return callablestatement.getNClob(parameterIndex);
	}

	@Override
	public NClob getNClob(String parameterName) throws SQLException {
		
		return callablestatement.getNClob(parameterName);
	}

	@Override
	public void setSQLXML(String parameterName, SQLXML xmlObject)
			throws SQLException {
		
		callablestatement.setSQLXML(parameterName, xmlObject);
	}

	@Override
	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		
		return callablestatement.getSQLXML(parameterIndex);
	}

	@Override
	public SQLXML getSQLXML(String parameterName) throws SQLException {
		
		return callablestatement.getSQLXML(parameterName);
	}

	@Override
	public String getNString(int parameterIndex) throws SQLException {
		
		return callablestatement.getNString(parameterIndex);
	}

	@Override
	public String getNString(String parameterName) throws SQLException {
		
		return callablestatement.getNString(parameterName);
	}

	@Override
	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		
		return callablestatement.getNCharacterStream(parameterIndex);
	}

	@Override
	public Reader getNCharacterStream(String parameterName) throws SQLException {
		
		return callablestatement.getNCharacterStream(parameterName);
	}

	@Override
	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		
		return callablestatement.getCharacterStream(parameterIndex);
	}

	@Override
	public Reader getCharacterStream(String parameterName) throws SQLException {
		
		return callablestatement.getCharacterStream(parameterName);
	}

	@Override
	public void setBlob(String parameterName, Blob x) throws SQLException {
		
		callablestatement.setBlob(parameterName, x);
	}

	@Override
	public void setClob(String parameterName, Clob x) throws SQLException {
		
		callablestatement.setClob(parameterName, x);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x, long length)
			throws SQLException {
		
		callablestatement.setAsciiStream(parameterName, x, length);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x, long length)
			throws SQLException {
		
		callablestatement.setBinaryStream(parameterName, x, length);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader,
			long length) throws SQLException {
		
		callablestatement.setCharacterStream(parameterName, reader, length);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x)
			throws SQLException {
		
		callablestatement.setAsciiStream(parameterName, x);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x)
			throws SQLException {
		
		callablestatement.setBinaryStream(parameterName, x);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader)
			throws SQLException {
		
		callablestatement.setCharacterStream(parameterName, reader);
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value)
			throws SQLException {
		
		callablestatement.setNCharacterStream(parameterName, value);
	}

	@Override
	public void setClob(String parameterName, Reader reader)
			throws SQLException {
		
		callablestatement.setClob(parameterName, reader);
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream)
			throws SQLException {
		
		callablestatement.setBlob(parameterName, inputStream);
	}

	@Override
	public void setNClob(String parameterName, Reader reader)
			throws SQLException {
		
		callablestatement.setNClob(parameterName, reader);
		
	}

	public <T> T getObject(int parameterIndex, Class<T> type)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T getObject(String parameterName, Class<T> type)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void closeOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isCloseOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
}
