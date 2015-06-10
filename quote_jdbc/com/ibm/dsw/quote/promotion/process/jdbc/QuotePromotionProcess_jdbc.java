package com.ibm.dsw.quote.promotion.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.RowExistingException;
import com.ibm.dsw.quote.promotion.process.QuotePromotionProcess_Impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
/**
 * 
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuotePromotionProcess_jdbc</code>
 * 
 * @author zyuyang@cn.ibm.com
 * 
 * Created on 2010-10-20
 */
public class QuotePromotionProcess_jdbc extends QuotePromotionProcess_Impl{

	public void removeQuotePromotion(String webQuoteNum, String quoteTxtId, int deleteAllFlg)
			throws QuoteException {
		LogContext log = LogContextFactory.singleton().getLogContext();
		Integer quoteTxtIdValue = StringUtils.isBlank(quoteTxtId)? null:Integer.valueOf(quoteTxtId.trim());
		
		log.debug(this, "begin to remove quote promotion - quote num="
				+ webQuoteNum + " quoteTxtId =" + quoteTxtIdValue);
		

		HashMap parms = new HashMap();
		parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
		parms.put("piQuoteTxtId", quoteTxtIdValue);
		parms.put("piDeleteAllFlg", deleteAllFlg);
		
		try {
			this.beginTransaction();
			QueryContext queryCtx = QueryContext.getInstance();
			String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_D_QT_PRMTN, null);
			CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
			queryCtx.completeStatement(ps, CommonDBConstants.DB2_D_QT_PRMTN,parms);
			log.debug(this, LogHelper.logSPCall(sqlQuery, parms));
			ps.execute();
			int retStatus = ps.getInt(1);
			if (CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA == retStatus) {
				throw new NoDataException("No data existing: " + sqlQuery);
			}else if(CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
				throw new TopazException("exeute sp failed:" + sqlQuery);
			}
			this.commitTransaction();
			log.debug(this, "completed removing quote promotion");
		} catch (RowExistingException re) {
			log.error(this, re.getMessage());
			throw new QuoteException(re);
		} catch (TopazException e) {
			logContext.debug(this,e.getMessage());
			throw new QuoteException(e);
		} catch (Exception e) {
			logContext.debug(this,e.getMessage());
			throw new QuoteException(e);
		} finally {
			this.rollbackTransaction();
        }
	}
	
	public List findPromotionsByQuote(String quoteNum) throws QuoteException {
        LogContext log = LogContextFactory.singleton().getLogContext();
        List promotions = new ArrayList();
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", quoteNum);
        try {
        	this.beginTransaction();
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_PRMTN, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_PRMTN, parms);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            ResultSet rs = ps.getResultSet();
            
            String[] promotion = null;
            while (rs.next()) {
            	promotion = new String[2];
            	promotion[0] = StringUtils.trimToEmpty(rs.getString(1));
            	promotion[1] = StringUtils.trimToEmpty(rs.getString(2));
                promotions.add(promotion);
            }
            rs.close();
            this.commitTransaction();
            log.debug(this, "completed removing quote promotion");
        } catch (RowExistingException re) {
        	logContext.error(this, re.getMessage());
        	throw new QuoteException(re);
		} catch (TopazException e) {
			logContext.debug(this,e.getMessage());
			throw new QuoteException(e);
		} catch (Exception e) {
			logContext.debug(this,e.getMessage());
			throw new QuoteException(e);
		} finally {
			this.rollbackTransaction();
        }
        return promotions;
    }
}
