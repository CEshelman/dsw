package com.ibm.dsw.quote.draftquote.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.QuoteHeaderFactory;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.draftquote.process.Amendment;
import com.ibm.dsw.quote.draftquote.process.QuoteToUProcess_Impl;
import com.ibm.dsw.quote.draftquote.process.ToU;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>QuoteToUProcess_jdbc</code> class is jdbc implementation of
 * QuoteToUProcess.
 *
 *
 * @author <a href="whliul@cn.ibm.com">Long Liu </a> <br/>
 *
 * Creation date: 2013-6-4
 */

public class QuoteToUProcess_jdbc extends QuoteToUProcess_Impl{

	LogContext logContext = LogContextFactory.singleton().getLogContext();
	@Override	
    public HashMap lookUpToUList(String piQuoteNum) throws QuoteException{
		
		HashMap lookUpToMap = new HashMap();
		CallableStatement cstmt = null;		
		ResultSet rs = null;
		QueryContext queryCtx;
		String sqlQuery = null;
		HashMap params = new HashMap();
		params.put("piQuoteNum", piQuoteNum);
		params.put("piAgreementNum", "");
		params.put("piOrderNum", "");
		params.put("piCallFlag", new Integer(1));
		try {
			this.beginTransaction();
			
			queryCtx = QueryContext.getInstance();			

			logContext.debug(this, "begin transaction");
			sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_TOU, null);
			cstmt = TopazUtil.getConnection().prepareCall(sqlQuery);
			queryCtx.completeStatement(cstmt, CommonDBConstants.DB2_S_QT_GET_TOU, params);
			logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));
			cstmt.execute();
			
			int returnCode = cstmt.getInt(1);
			logContext.debug(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
            if (returnCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
            	logContext.error(this, "the return code of calling " + sqlQuery + " is: " + returnCode);
                throw new SPException(returnCode);
            }
	            
	      //  rs = cstmt.getResultSet();
	        assembleQuotes(cstmt, rs, lookUpToMap);
	        this.commitTransaction();
	        return lookUpToMap;
		} catch (Exception e) {
		//	logContext.error(this, LogHelper.logSPCall(sqlQuery, params));
		        throw new QuoteException(e);
		}
		
        
		
	}
	
	private void assembleQuotes(CallableStatement ps, ResultSet rs, HashMap map)
		throws SQLException, Exception {
	    
		rs =ps.getResultSet();
		assembleToUList(rs, map);
		if (ps.getMoreResults()) {
			rs = ps.getResultSet();
			assembleAmendmentList(rs, map);
		}
		 
		//2 cursors loop:
		/*for (int i = 2; i > 0; i--) {
		    if (ps.getMoreResults()) {
		        rs = ps.getResultSet();
		        if (rs.next())
		            if (isToUListCursor(rs)) {
		            	map = assembleToUList(rs, map);
		            } else if (isAmendmentListCursor(rs)) {
		            	map = assembleAmendmentList(rs, map);
		            }else {
		                logContext.error(this, "Result set not correct...");
		                throw new Exception();
		            }
		    } else
		        return;
		}*/
	}
	
	private boolean isToUListCursor(ResultSet rs) throws SQLException {
        if (StringUtils.trimToEmpty(rs.getString("cur_name")).equals("quote_tou_list"))
            return true;

        return false;

    }
	
	private boolean isAmendmentListCursor(ResultSet rs) throws SQLException {
        if (StringUtils.trimToEmpty(rs.getString("cur_name")).equals("quote_amendment_list"))
            return true;

        return false;

    }
	
	private void assembleToUList(ResultSet rs, HashMap map) throws SQLException, Exception {
		 ToU_jdbc tou = null;
		 SortedSet<ToU> touSet = new TreeSet<ToU>();
		 Map<String, String> noAssociatedPartMap = new LinkedHashMap<String, String>();
		
		 while (rs.next()) {
			 if (isToUListCursor(rs)) {
				 if (rs.getString("tou_name")== null || rs.getString("tou_url") == null) {
					 noAssociatedPartMap.put(StringUtils.trimToEmpty(rs.getString("part_num")), StringUtils.trimToEmpty(rs.getString("part_desc")));
				 } 
				 else {
					 tou = new ToU_jdbc();
					 tou.touName= StringUtils.trimToEmpty(rs.getString("tou_name"));
					 tou.touUrl= StringUtils.trimToEmpty(rs.getString("tou_url"));
					 tou.releaseDate= rs.getDate("rel_date");
					 tou.addDate= rs.getDate("add_date");
					 tou.amendmentFlag= rs.getInt("TOU_AMENDD_FLAG");
					 tou.amendmentBFlag= rs.getInt("TOU_BRAND_AMENDD_FLAG");
					 tou.termsType=StringUtils.trimToEmpty(rs.getString("terms_conds_type_code"));
					 tou.termsSubType=StringUtils.trimToEmpty(rs.getString("terms_conds_sub_type_code"));
					 tou.docId =StringUtils.trimToEmpty(rs.getString("doc_id"));
					 if(!touSet.contains(tou)){
						 touSet.add(tou);
					 }
					 
				}
			 }
		 }
			 
		 if (noAssociatedPartMap.size() > 0) {
			 map.put("noAssociatedPartList", noAssociatedPartMap);
		 }
		 map.put("touList", touSet);
	}
	 
	 private void assembleAmendmentList(ResultSet rs, HashMap map) throws SQLException, Exception {
		 Amendment_jdbc amendment = null;
		 List<Amendment> amendmentList = new ArrayList<Amendment>();
		 while (rs.next()) {
			 if (isAmendmentListCursor(rs)) {
				 amendment = new Amendment_jdbc();
				 amendment.amendmentName= StringUtils.trimToEmpty(rs.getString("attchmt_file_name"));
				 amendment.amendmentUrl= StringUtils.trimToEmpty(rs.getString("attchmt_seq_num"));
				 amendment.amendmentDate= rs.getDate("add_date");
				 amendment.currentAmendmentFlag= rs.getInt("isCurrentAmendmentFlag");
				 amendment.touUrl= StringUtils.trimToEmpty(rs.getString("tou_url"));
				 amendment.docID =StringUtils.trimToEmpty(rs.getString("doc_id"));
				 amendment.termsCondsTypeCode=StringUtils.trimToEmpty(rs.getString("terms_conds_type_code"));
				 amendmentList.add(amendment);
			 }
		 }
		 map.put("amendmentList", amendmentList);
	}

	@Override
	public int getCountOfCsaTerms()
    {
    	int count = 0;
        try {
            this.beginTransaction();
            count = QuoteHeaderFactory.singleton().getCountOfCsaTerms();
            this.commitTransaction();
        } catch (TopazException tce) {
        	logContext.error(this, tce.getMessage());
        } finally {
            rollbackTransaction();
        }
        return count;
    }

}
