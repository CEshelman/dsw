package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.JustSection;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo.ApproverComment;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo.ChosenApprover;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo.SpecialBidQuestion;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpecialBidInfoPersister</code> class is delegation for persisting
 * SpecialBidInfo domain.
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 20, 2007
 */
public class SpecialBidInfoPersister extends Persister {

    private LogContext logger = LogContextFactory.singleton().getLogContext();

    private SpecialBidInfo_jdbc spBidInfo;

    /**
     * @param info_jdbc
     */
    public SpecialBidInfoPersister(SpecialBidInfo_jdbc info) {
        this.spBidInfo = info;
    }

    /**
     *  
     */

    public void update(Connection connection) throws TopazException {
        insertOrUpdate(connection);
    }

    /**
     *  
     */

    public void delete(Connection connection) throws TopazException {
        // do nothing
    }
    
    protected void fillSpeclBidInfoHeader(SpecialBidInfo_jdbc sbInfo, CallableStatement ps) throws SQLException {
        if (ps == null || sbInfo == null)
            return;
        
        sbInfo.bCredAndRebillFlag = ps.getInt(3) == 1 ? true : false;
        sbInfo.bTermsAndCondsChgFlag = ps.getInt(4) == 1 ? true : false;
        sbInfo.bSetCtrctLvlPricngFlag = ps.getInt(5) == 1 ? true : false;
        sbInfo.bFulfllViaLanddMdlFlag = ps.getInt(6) == 1 ? true : false;
        sbInfo.bElaTermsAndCondsChgFlag = ps.getInt(7) == 1 ? true : false;
        sbInfo.bPreApprvdCtrctLvlPricFlg = ps.getInt(8) == 1 ? true : false;
        sbInfo.bRyltyDiscExcddFlag = ps.getInt(9) == 1 ? true : false;
        sbInfo.sSpBidRgn = ps.getString(10);
        sbInfo.sSpBidDist = ps.getString(11);
        sbInfo.sSpBidCustIndustryCode = ps.getString(12);
        sbInfo.sSalesDiscTypeCode = ps.getString(13);
        sbInfo.sCreditJustText = ps.getString(14);
        sbInfo.sSpBidJustText = ps.getString(15);
        sbInfo.sSpBidType = ps.getString(16);
	    sbInfo.setCompetitorName(ps.getString(17));
        sbInfo.setCompetitorPrice(ps.getString(18));
        sbInfo.setCompetitorProduct(ps.getString(19));
        sbInfo.setCompetitorTC(ps.getString(20));
        sbInfo.rateBuyDown = ps.getInt(21);
        sbInfo.SWGIncur = ps.getInt(22);
        sbInfo.financeRate = ps.getObject(23) == null ? null : new Double(ps.getDouble(23));
        sbInfo.attachmentCount = ps.getInt(24);
        sbInfo.origSalesOrdNumInvld = (ps.getInt(25) == 1);
        
        sbInfo.orgnlSalesOrdNum = StringUtils.trim(ps.getString(26));
        sbInfo.orgnlQuoteNum = StringUtils.trim(ps.getString(27));
        sbInfo.salesPlayNum = StringUtils.trim(ps.getString(28));
        sbInfo.initSpeclBidApprFlag = (ps.getInt(29) == 1);
        
        sbInfo.progRBD = ps.getObject(30) == null ? null : new Double(ps.getDouble(30));
        sbInfo.incrRBD = ps.getObject(31) == null ? null : new Double(ps.getDouble(31));
        
        sbInfo.evaltnAction = ps.getString(32);
        sbInfo.evaltnComment = ps.getString(33);
        sbInfo.bSplitBidFlag = ps.getInt(34) == 1 ? true : false;
        sbInfo.channelOverrideDiscountReasonCode = ps.getString(35);
        
        if (sbInfo.getCompetitorName() == null && sbInfo.getCompetitorPrice() == null
                && sbInfo.getCompetitorProduct() == null && sbInfo.getCompetitorTC() == null) {
            sbInfo.setCompetitive(false);
        } else {
            sbInfo.setCompetitive(true);
        }
    }
    
    public void hydrateHeader(Connection connection) throws TopazException {
        HashMap parms = new HashMap();
        String quoteNum = spBidInfo.getWebQuoteNum();
        parms.put("piWebQuoteNum", quoteNum);
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_SBHDR, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_SBHDR, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            
            fillSpeclBidInfoHeader(spBidInfo, ps);
            
            this.isNew(false);
            this.isDeleted(false);
            
        } catch (SQLException e) {
            logger.error("Failed to get the Special bid info header from DB for quote number: " + quoteNum +": ", e);
            throw new TopazException(e);
        }
    }

    public void hydrate(Connection connection) throws TopazException {
        HashMap parms = new HashMap();
        String quoteNum = spBidInfo.getWebQuoteNum();
        parms.put("piWebQuoteNum", quoteNum);
        ResultSet rs = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_SBINFO, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_SBINFO, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            boolean retCode = ps.execute();
            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }
            
            fillSpeclBidInfoHeader(spBidInfo, ps);

            // hydrate category resultset
            do {
                rs = ps.getResultSet();
                ResultSetMetaData metaData = rs.getMetaData();
                String firstColumnName = metaData.getColumnName(1);
                String secondColumnName = metaData.getColumnName(2);                

                if (firstColumnName.equals("SPECL_BID_CONFIGRTN_SEQ_NUM")) {// categories
                    while (rs.next()) {
                        String cat = rs.getString(2);
                        spBidInfo.spBidCategories.add(cat);
                    }
                } else if (firstColumnName.equals("APPRVR_EMAIL")) {// Chosen
                    // approvers
                    while (rs.next()) {
                        ChosenApprover chosenApprover = new ChosenApprover();
                        chosenApprover.userEmail = rs.getString(1);
                        chosenApprover.groupName = rs.getString(2);
                        chosenApprover.lastAction = rs.getString(3);
                        chosenApprover.modeDate = rs.getDate(5);
                        chosenApprover.groupLevel = rs.getInt(4);
                        chosenApprover.approverName = rs.getString(6);
                        chosenApprover.superSedeApproveType = rs.getString(7);
                        chosenApprover.rdyToOrder = rs.getInt(8);
                        spBidInfo.chosenApprovers.add(chosenApprover);
                    }
                } else if (firstColumnName.equals("SPECL_BID_APPRVR_LVL")) {// All
                    // approvers
                    while (rs.next()) {
                        SpecialBidInfo.Approver approver = new SpecialBidInfo.Approver();
                        approver.level = rs.getInt(1);
                        approver.userEmail = rs.getString(2);
                        approver.groupType = rs.getString(3);
                        approver.approverName = rs.getString(4);
                        spBidInfo.allApprovers.add(approver);
                    }
                } else if (firstColumnName.equals("USER_EMAIL_ADR") && secondColumnName.equals("USER_ACTION")) {// approval
                    // comments
                    while (rs.next()) {
                        String userEmail = rs.getString(1);
                        SpecialBidInfo.ApproverComment approverComment = new ApproverComment();
                        approverComment.userEmail = userEmail;
                        approverComment.approverName = rs.getString(5);
                        approverComment.approverLvl = rs.getInt(6);                        
                        approverComment.idoc = rs.getString(7);
                        approverComment.returnReason = rs.getString(8);
                        Date commentDate = null;
                        if(rs.getTimestamp(3) != null){
                            commentDate = new Date(rs.getTimestamp(3).getTime());
                        }                        
                        approverComment.comment = new SpecialBidInfo.CommentInfo(commentDate, rs.getString(4), rs
                                .getString(2));
                        spBidInfo.approverComments.add(approverComment);
                    }
                }
                else if ( firstColumnName.equals("REQUEST_TXT") )
                {
                    List list = spBidInfo.getApproverReviewRequests();
                    while ( rs.next() )
                    {
                        SpecialBidInfo.ReviewerComment comment = new SpecialBidInfo.ReviewerComment();
                        comment.requestTxt = rs.getString("REQUEST_TXT");
                        comment.requestDate = rs.getTimestamp("mod_date");
                        comment.requesterEmail = rs.getString("user_email_adr");
                        comment.requesterName = rs.getString("user_name");
                        comment.reviewerEmail = rs.getString("rvwr_email_adr");
                        comment.reviewerName = rs.getString("REVIEWER_NAME");
                        list.add(comment);
                    }
                }
                else if ( firstColumnName.equals("REV_TXT") )
                {
                    List list = spBidInfo.getReviewerReviews();
                    while ( rs.next() )
                    {
                        SpecialBidInfo.ReviewerComment comment = new SpecialBidInfo.ReviewerComment();
                        comment.reviewerTxt = rs.getString("REV_TXT");
                        comment.reviewerEmail = rs.getString("user_email_adr");
                        comment.revwerCommetTimestamp = rs.getTimestamp("mod_date");
                        list.add(comment);
                    }
                 }else if (firstColumnName.equals("CONFIGRTN_QUESTION_SEQ_NUM")) {
                    while (rs.next()) {
                        SpecialBidInfo.SpecialBidQuestion question = new SpecialBidQuestion();
                        question.configNum = rs.getInt("CONFIGRTN_QUESTION_SEQ_NUM");
                        question.answer = rs.getInt("SPECL_BID_ANSR");
                        question.questionText = rs.getString("SPECL_BID_CONFIGRTN_VAL");
                        question.questionInfo = rs.getString("SPECL_BID_CONFIGRTN_INFO");
                        spBidInfo.questions.add(question);
                    }
                } else if (firstColumnName.equals("QUOTE_TXT_ID")) {// additional
                                                                 // just text
                    int temp = SpecialBidInfo.BEGIN_SUBMITTER;
                    spBidInfo.addtnlJustTexts.clear();
                    while (rs.next()) {
//                        spBidInfo.addtnlJustTexts.add(new SpecialBidInfo.CommentInfo(rs.getDate("MOD_DATE"), rs
//                                .getString("QUOTE_TXT"), ""));
                        SpecialBidInfo.CommentInfo cmtInfo = new SpecialBidInfo.CommentInfo();
                        cmtInfo.textId = rs.getString("QUOTE_TXT_ID");
                        cmtInfo.comment = rs.getString("QUOTE_TXT");
                        cmtInfo.commentDate = rs.getTimestamp("MOD_DATE");
                        cmtInfo.typeCode = rs.getString("QUOTE_TXT_TYPE_CODE");
                        
                        String secIdStr = rs.getString("JSTFCTN_SECTN_ID");
                        try
                        {
                            cmtInfo.secId = Integer.parseInt(secIdStr);
                        }
                        catch ( Exception e )
                        {
                            if ( SpecialBidInfo.CommentInfo.SPBID_J.equals(cmtInfo.typeCode) )
                            {
                                cmtInfo.secId = SpecialBidInfo.BEGIN_SUBMITTER;
                            }
                            else if ( SpecialBidInfo.CommentInfo.SBADJUST.equals(cmtInfo.typeCode) )
                            {
                                cmtInfo.secId = SpecialBidInfo.BEGIN_USER_COMMENTS;
                            }
                        }
                        spBidInfo.addtnlJustTexts.add(cmtInfo);
                    }
                }
                else if ( firstColumnName.equals("SPECL_BID_APPRVR_TYPE") )
                {
                    List list = spBidInfo.getAllTypeInfo();
                    while ( rs.next() )
                    {
                        SpecialBidInfo.Approver apr = new SpecialBidInfo.Approver();
                        apr.level = rs.getInt("SPECL_BID_APPRVR_LVL");
                        apr.groupType = rs.getString("SPECL_BID_APPRVR_TYPE");
                        apr.rdyToOrder = rs.getInt("RDY_TO_ORD_FLAG");
                        list.add(apr);
                    }
                }
            } while (ps.getMoreResults());
            
            
            this.isNew(false);
            this.isDeleted(false);
        } catch (Exception e) {
            logger.error("Failed to get the Special bid info from DB for quote number: " + quoteNum +": ", e);
            throw new TopazException(e);
        }finally{
        	try {
				if (null != rs)
				{
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
        }
    }

    /**
     *  
     */

    public void insert(Connection connection) throws TopazException {
        insertOrUpdate(connection);
    }

    /**
     * @param connection
     * @throws TopazException
     */
    private void insertOrUpdate(Connection connection) throws TopazException {
        HashMap params = new HashMap();
        String catList = null;
        if (spBidInfo.spBidCategories != null) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < spBidInfo.spBidCategories.size(); i++) {
                buffer.append(spBidInfo.spBidCategories.get(i) + ",");
            }
            catList = buffer.toString();
        }
        String questionList = null;
        String answerList = null;
        if (spBidInfo.getQuestions() != null) {
            StringBuffer questionBuffer = new StringBuffer();
            StringBuffer answerBuffer = new StringBuffer();
            for (int i = 0; i < spBidInfo.questions.size(); i++) {
                questionBuffer.append(((SpecialBidInfo.SpecialBidQuestion) spBidInfo.questions.get(i)).configNum + ",");
                answerBuffer.append(((SpecialBidInfo.SpecialBidQuestion) spBidInfo.questions.get(i)).answer + ",");
            }
            questionList = questionBuffer.toString();
            answerList = answerBuffer.toString();
        }
        params.put("piUserId", spBidInfo.getUserMail());
        params.put("piWebQuoteNum", spBidInfo.sWebQuoteNum);
        params.put("piCredAndRebillFlg", spBidInfo.bCredAndRebillFlag ? new Integer(1) : new Integer(0));
        params.put("piTermsAndCondsChgFlag", spBidInfo.bTermsAndCondsChgFlag ? new Integer(1) : new Integer(0));
        params
                .put("piSetCtrctLvlPricngFlg", spBidInfo.bSetCtrctLvlPricngFlag ? new Integer(1) : new Integer(
                        0));
        params
                .put("piFulfllViaLanddMdlFlg", spBidInfo.bFulfllViaLanddMdlFlag  ? new Integer(1) : new Integer(
                        0));
        params.put("piElaTermsAndCondsChgFLag", spBidInfo.bElaTermsAndCondsChgFlag  ? new Integer(1)
                : new Integer(0));
        params.put("piPreApprvdCtrctLvlPricFlg", spBidInfo.bPreApprvdCtrctLvlPricFlg  ? new Integer(1)
                : new Integer(0));
        params.put("piRyltyDiscExcddFlag", spBidInfo.bRyltyDiscExcddFlag  ? new Integer(1) : new Integer(0));
        params.put("piSpeclBidRgn", spBidInfo.sSpBidRgn);
        params.put("piSpeclBidDist", spBidInfo.sSpBidDist);
        params.put("piSpeclBidIndusCode", spBidInfo.sSpBidCustIndustryCode);
        params.put("piSalesDiscTypeCode", spBidInfo.sSalesDiscTypeCode);
        params.put("piCreditJustText", spBidInfo.getCreditJustText());
        //params.put("piSpBidJustText", spBidInfo.getSpBidJustText());
        params.put("piCategoriesList", catList);
        params.put("piSpeclBidType", spBidInfo.getSpBidType());
        params.put("piQuestnNumsList", questionList);
        params.put("piAnswersList", answerList);
        params.put("piCompName", spBidInfo.getCompetitorName());
        params.put("piCompPrice", spBidInfo.getCompetitorPrice());
        params.put("piCompProduct", spBidInfo.getCompetitorProduct());
        params.put("piCompTC", spBidInfo.getCompetitorTC());
        params.put("piRateBuyDown", new Integer(spBidInfo.getRateBuyDown()));
        if ( StringUtils.equals(spBidInfo.getSalesDiscTypeCode(), "2") )
        {
            params.put("piSalesPlayNum", spBidInfo.getSalesPlayNum());
        }
        if ( spBidInfo.getRateBuyDown() == 1 )
        {
            //params.put("piSWGIncur", new Integer(spBidInfo.getSWGIncur()));
            //params.put("piFinanceRate", spBidInfo.getFinanceRate());
        	params.put("piProgRBD", spBidInfo.getProgRBD());
        	params.put("piIncrRBD", spBidInfo.getIncrRBD());
        }
        if ( spBidInfo.bCredAndRebillFlag )
        {
            params.put("piOrgnlSalesOrdNum", spBidInfo.getOrgnlSalesOrdNum());
            params.put("piOrgnlQuoteNum", spBidInfo.getOrgnlQuoteNum());
        }
        params.put("piInitSpeclBidApprFlag", spBidInfo.isInitSpeclBidApprFlag() ? new Integer(1) : new Integer(0));
        params.put("piEvaltnAction", spBidInfo.getEvaltnAction());
        params.put("piEvalComment", spBidInfo.getEvaltnComment());
		params.put("piSplitBidFlag", spBidInfo.bSplitBidFlag ? new Integer(1) : new Integer(0));
		params.put("piGridDelegationFlag", spBidInfo.isGridFlag() ? new Integer(1) : new Integer(0));
		params.put("piChnlOvrrdDiscReas", spBidInfo.getChannelOverrideDiscountReasonCode());
        
        int retCode = -1;

        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_SBINFO, null);
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_SBINFO, params);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

            ps.execute();
            retCode = ps.getInt(1);
            logger.debug(this, "the return code of calling " + sqlQuery + " is: " + retCode);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code for quote number: " + spBidInfo.sWebQuoteNum + ": " + retCode);
            }
            this.isNew(true);
            this.isDeleted(false);
        } catch (Exception e) {
            logger.error("Failed to insert the Special Bid Info to the database for quote number: " + spBidInfo.sWebQuoteNum + ": ", e);
            logger.error(this, e);
            throw new TopazException(e);
        }
        
        try
        {
            logger.debug(this, "begin to handle quote txt");
	        List list = this.spBidInfo.getJustSections();
	        for ( int i = 0; i < list.size(); i++ )
	        {
	            JustSection sec = (JustSection)list.get(i);
	            params.clear();
	            List textList = sec.getJustTexts();
	            if ( textList != null && textList.size() > 0 )
	            {
	                SpecialBidInfo.CommentInfo cmtInfo = (SpecialBidInfo.CommentInfo)textList.get(0);
	                params.put("piWebQuoteNum", this.spBidInfo.getWebQuoteNum());
	                Integer txtId = null;
	                try
	                {
	                    txtId = new Integer(cmtInfo.getTextId());
	                }
	                catch ( Exception e )
	                {
	                	logger.error(this, e);
	                }
	                params.put("piQuoteTxtID", txtId);
	                params.put("piQuoteTxtTypeCode", cmtInfo.getTypeCode());
	                params.put("piQuoteTxt", cmtInfo.getComment());
	                params.put("piUserEmailAdr", this.spBidInfo.getUserMail());
	                params.put("piJstfctnSectnID", Integer.toString(cmtInfo.getSecId()));
	                //params.put("piIsForceUpdate", spBidInfo.isForceUpdate() ? "1" : "0");
	                params.put("piLastUpdateTime", cmtInfo.getCommentDateText());
	                QueryContext context = QueryContext.getInstance();
	                String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_IU_QT_TXT, null);
	                CallableStatement ps = connection.prepareCall(sqlQuery);
	                context.completeStatement(ps, CommonDBConstants.DB2_IU_QT_TXT, params);
	                
	                logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

	                ps.execute();
	                retCode = ps.getInt(1);
	                logger.debug(this, "the return code of calling " + sqlQuery + " is: " + retCode);
	                if (retCode != 0) {
	                    throw new TopazException("SP call returns error code for quote number: " + spBidInfo.sWebQuoteNum + ": " + retCode);
	                }
	            }
	        }
        }
        catch ( Exception e )
        {
            logger.error("Failed to insert the Special Bid Info to the database for quote number: " + spBidInfo.sWebQuoteNum + ": ", e);
            logger.error(this, e);
            throw new TopazException(e);
        }
    }

}
