/*
 * Created on 2007-5-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.draftquote.process.jdbc;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.ApproveComment;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcess_Impl;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvr;
import com.ibm.dsw.quote.submittedquote.domain.SpecialBidApprvrFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpecialBidProcess_jdbc extends SpecialBidProcess_Impl {
    
    
    protected void persistSpecialPidApproverSelection(String quoteNumber, String groupName, String approverEmail,
            String applierEmail, int approverLevel, boolean isPGS) throws QuoteException {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        SpecialBidApprvr specialBidApprvr;
        try {
            specialBidApprvr = SpecialBidApprvrFactory.singleton().createSpecialBidApprvrForSelectionUpdate();
            specialBidApprvr.setWebQuoteNum(quoteNumber);
            specialBidApprvr.setSpecialBidApprGrp(groupName);
            specialBidApprvr.setSpecialBidApprLvl(approverLevel);
            specialBidApprvr.setApprvrEmail(approverEmail);
            specialBidApprvr.setApplierEmail(applierEmail);
            if (isPGS) {
            	specialBidApprvr.setIsPGS(1);
            } else {
            	specialBidApprvr.setIsPGS(0);
            }
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }
    }
    
    protected void persistSpecialPidApproverAction() throws QuoteException {
        // TODO Auto-generated method stub
    }
    
    protected String updateAndGetOldSalesPlayNum(String webQuoteNum, String salesPlayNum, String userId) throws QuoteException
    {
    	LogContext logContext = LogContextFactory.singleton().getLogContext();
        try {
            String spName = CommonDBConstants.DB2_U_QT_SALES_PLAY_NUM;
            HashMap parms = new HashMap();
            
            logContext.debug(this, "webquote number is " + webQuoteNum);
            
            parms.put("piWebQuoteNum", webQuoteNum);
            parms.put("piSalesPlayNum", salesPlayNum);
            parms.put("piUserId", userId);

            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(spName, null);

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            queryCtx.completeStatement(ps, spName, parms);

            ps.execute();

            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
            }
            return ps.getString(2);
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }
    }
    
    public void updateCategory(String quoteNum, String[] spBidCata, String userId) throws QuoteException
    {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        try {
            this.beginTransaction();
            String spName = CommonDBConstants.DB2_U_QT_SB_CAT;
            HashMap parms = new HashMap();
            
            logContext.debug(this, "webquote number is " + quoteNum);
            
            String catList = null;
            if ( spBidCata != null && spBidCata.length > 0 ) {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < spBidCata.length; i++) {
                    buffer.append(spBidCata[i] + ",");
                }
                catList = buffer.toString();
            }
            else
            {
                catList = "";
            }
            
            parms.put("piWebQuoteNum", quoteNum);
            parms.put("piCataList", catList);
            parms.put("piUserId", userId);

            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(spName, null);

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            queryCtx.completeStatement(ps, spName, parms);

            ps.execute();

            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
            }
            this.commitTransaction();
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }
        finally
        {
            this.rollbackTransaction();
        }
    }
    
    public void updateSpecialBid(String webQuoteNum, int piTNC, String userId) throws QuoteException
    {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        try {
            this.beginTransaction();
            String spName = CommonDBConstants.DB2_U_QT_SPECL_BID;
            HashMap parms = new HashMap();
            
            logContext.debug(this, "webquote number is " + webQuoteNum);
            parms.put("piWebQuoteNum", webQuoteNum);
            parms.put("piTNC", new Integer(piTNC));
            parms.put("piUserId", userId);

            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(spName, null);

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            queryCtx.completeStatement(ps, spName, parms);

            ps.execute();

            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery + ":" + webQuoteNum);
            }
            this.commitTransaction();
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }
        finally
        {
            this.rollbackTransaction();
        }
    }
    
    public void updateSpecialBidGridDelegationFlag(String webQuoteNum, boolean gridDelegationFlag)  throws QuoteException { 
    	LogContext logContext = LogContextFactory.singleton().getLogContext();
        try {
            String spName = CommonDBConstants.DB2_U_QT_SPECL_BID_GRID_FLAG;
            HashMap parms = new HashMap();            
            logContext.debug(this, "webquote number is " + webQuoteNum);
            parms.put("piWebQuoteNum", webQuoteNum);
            parms.put("piGridFlag", gridDelegationFlag==true?QuoteConstants.GRID_DELEGATION_FLAG_YES:QuoteConstants.GRID_DELEGATION_FLAG_NO); 
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(spName, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            queryCtx.completeStatement(ps, spName, parms);
            ps.execute();
            int retStatus = ps.getInt(1);
            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery + ":" + webQuoteNum);
            }
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }
    }
    public boolean checkCpqExcepCode(String webQuoteNum) throws QuoteException
    {
    	LogContext logContext = LogContextFactory.singleton().getLogContext();
        try {
            String spName = CommonDBConstants.DB2_S_QT_CPQ_EXCEP_CODE;
            HashMap parms = new HashMap();
            
            logContext.debug(this, "webquote number is " + webQuoteNum);
            
            parms.put("piQuoteNum", webQuoteNum);

            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(spName, null);

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            queryCtx.completeStatement(ps, spName, parms);

            ps.execute();

            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
            }
            int poHasError = ps.getInt(2);
            
            return poHasError == 1;
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }
    }
    
    public List<ApproveComment> getAllApproverCommentsWithType(String webQuoteNum, String aprId, int level) throws QuoteException
    {
    	LogContext logContext = LogContextFactory.singleton().getLogContext();
        try {
            this.beginTransaction();
            String spName = CommonDBConstants.DB2_S_QT_GET_APPROVE_CMTS_WITH_TYPE;
            HashMap parms = new HashMap();
            
            parms.put("piWebQuoteNum", webQuoteNum);
            parms.put("piAprId", aprId);
            parms.put("piLevel", level);

            QueryContext queryCtx = QueryContext.getInstance();

            String sqlQuery = queryCtx.getCompletedQuery(spName, null);

            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);

            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            queryCtx.completeStatement(ps, spName, parms);

            ps.execute();

            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery + ":" + webQuoteNum);
            }
            
            ResultSet rs = ps.getResultSet();
            List<ApproveComment> list = new ArrayList<ApproveComment>();
            Map<Integer, ApproveComment> map = new HashMap<Integer, ApproveComment>();
            while ( rs.next() )
            {
            	int temp = rs.getInt("SPECL_BID_APPRVR_LVL");
            	ApproveComment approverComment = map.get(temp);
            	if ( approverComment == null )
            	{
            		approverComment = new ApproveComment();
            		approverComment.setLevel(temp);
            		map.put(temp, approverComment);
            		list.add(approverComment);
            	}
            	approverComment.getCmts().add(rs.getString("quote_txt"));
            }
            rs = null;
            if ( ps.getMoreResults() )
            {
            	rs = ps.getResultSet();
            	while ( rs.next() )
            	{
            		int temp = rs.getInt("SPECL_BID_APPRVR_LVL");
            		ApproveComment cmt = map.get(temp);
            		if ( cmt == null )
            		{
            			cmt = new ApproveComment();
            			cmt.setLevel(temp);
            			map.put(temp, cmt);
            			list.add(cmt);
            		}
            		if ( cmt.getType() == null )
            		{
            			cmt.setType(rs.getString("SPECL_BID_APPRVR_TYPE"));
            		}
            	}
            }
            rs = null;
            if ( ps.getMoreResults() )
            {
            	rs = ps.getResultSet();
            	if ( rs.next() )
            	{
            		int temp = rs.getInt("SPECL_BID_APPRVR_LVL");
            		ApproveComment cmt = map.get(temp);
            		if ( cmt == null )
            		{
            			cmt = new ApproveComment();
            			cmt.setLevel(temp);
            			list.add(cmt);
            		}
            		cmt.setType(rs.getString("SPECL_BID_APPRVR_TYPE"));
            	}
            }
            this.commitTransaction();
            return list;
        } catch (Exception e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException(e);
        }
        finally
        {
            this.rollbackTransaction();
        }
    }
    
    
}
