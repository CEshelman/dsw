package com.ibm.dsw.quote.draftquote.config;

import com.ibm.dsw.quote.base.config.ParamKeys;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpecialBidParamKeys</code> 
 * 
 * 
 * @author: xiuliw@cn.ibm.com
 * 
 * Creation date: 2007-4-26
 */
public interface SpecialBidParamKeys extends ParamKeys {
    public static final String EXPLANATION_1 = "crediAndRebill";
    public static final String EXPLANATION_2 = "termsAndCondsChg";
    public static final String EXPLANATION_3 = "setCtrctLvlPricng";
    public static final String EXPLANATION_TEXT = "creditJustText";
    public static final String REGION = "spBidRgn";
    public static final String DISTRICT = "spBidDist";
    public static final String INDUSTRY_SEGMENT = "spBidCustIndustryCode";
    public static final String SPECIAL_BID_TYPE = "spBidType";
    public static final String IS_TRANSACTION_FULFILLED = "fulfllViaLanddMdl";
    public static final String IS_TRANSACTION_FULFILLED_ID1 = "fulfllViaLanddMdl_id1";
    public static final String IS_TRANSACTION_FULFILLED_ID2 = "fulfllViaLanddMdl_id2";
    public static final String IS_THIS_BID_FOR_ELA = "elaTermsAndCondsChg";
    public static final String MODIFY_FLAG_TNC = "modifyFlagTnc";
    public static final String MODIFY_FLAG_JUST = "modifyFlagJust";
    public static final String MODIFY_FLAG_CMT = "modifyFlagCmt";
    public static final String APPROVE_LEVEL = "approve_level";
    public static final String USER_COMMENT = "userComment";
    public static final String CONFIGURABLE_QUESTION = "question";
    public static final String CONFIGURABLE_QUESTION_ANSWER = "answer";
    public static final String CONFIGURABLE_QUESTION_1_ID1 = "answer1_id1";
    public static final String CONFIGURABLE_QUESTION_1_ID2 = "answer1_id2";
    public static final String SPECIAL_BID_COMMENT_SUBMITTER = "specialBidCommentSubmitter";
    public static final String SPECIAL_BID_COMMENT_REVIEWER = "specialBidCommentReviewer";
    public static final String SPECIAL_BID_COMMENT_APPROVE = "specialBidCommentApprove";
    public static final String SPECIAL_BID_COMMENT_ADD = "specialBidCommentAdd";
    public static final String CONFIGURABLE_QUESTION_2 = "answer2";
    public static final String CONFIGURABLE_QUESTION_2_ID1 = "answer2_id1";
    public static final String CONFIGURABLE_QUESTION_2_ID2 = "answer2_id2";
    public static final String FILE_INPUT_FLAG = "fileInput";
    
    public static final String REFLECT_PREVIOUS = "preApprvdCtrctLvlPric";
    public static final String PARAM_SPECIAL_BID_COMMON = "specialBidComm";
    public static final String REFLECT_PREVIOUS_ID1 = "preApprvdCtrctLvlPric_id1";
    public static final String REFLECT_PREVIOUS_ID2 = "preApprvdCtrctLvlPric_id2";
    public static final String REFLECT_FOLLOWING = "salesDiscTypeCode";
    public static final String REFLECT_FOLLOWING_ID1 = "salesDiscTypeCode_id1";
    public static final String REFLECT_FOLLOWING_ID2 = "salesDiscTypeCode_id2";
    public static final String ROYALTY_FLOOR = "ryltyDiscExcdd";
    public static final String ROYALTY_FLOOR_ID1 = "ryltyDiscExcdd_id1";
    public static final String ROYALTY_FLOOR_ID2 = "ryltyDiscExcdd_id2";
    public static final String SPECIAL_BID_JUSTIFICATION = "spBidJustText";
    public static final String CATEGORY = "category";
    
    public static final String COMPETITOR_NAME = "competitorName";
    public static final String COMPETITOR_PRICE = "competitorPrice";
    public static final String MODIFY_FLAG_REVIEWER = "modifyFlagReviewer";
    public static final String COMPETITOR_PRODUCT = "competitorProduct";
    public static final String COMPETITOR_TC = "competitorTC";
    public static final String SECTION_JUST_TEXTS = "secJustTexts";
    public static final String SECTION_INDEXS = "secIndexs";
    public static final String IS_COMPETITIVE = "isCompetitive";
    public static final String LAST_MODIFY_TIME = "lastModifyTimes";
    public static final String TEXT_IDS = "textIds";
    public static final String PARAM_SPECIAL_BID_REASON = "specialBidReason";
    public static final String PARAM_SEC_ID = "secId";
    public static final String PARAM_QUOTE_TXT_HISTORY = "quoteTxtHistory";
    public static final String PARAM_QUOTE_TXT_TYPE = "txtTypeCode";
    public static final String SESSION_STORE_OVER = "sessionStoreOver";
    public static final String SALES_REP_UPDATE_DATA = "salesRepUpdatedData";
    public static final String REVIEWER_COMMENT = "reviewerComment";
    public static final String APPROVER_COMMENT = "approverComment";
    public static final String APPROVER_COMMENT_ADD = "approverCommentAdd";
    public static final String SUBMITTER_COMMENT = "submitterComment";
    public static final String APVR_REVIEWER_COMMENT = "apvrReviewComments";
    public static final String JUST_COMMENTS = "justComments";
    public static final String TNC_COMMENTS = "tncComments";
    
    public static final String IS_RATE_BUY_DOWN = "isRateBuyDown";
    public static final String IS_SWG_INCUR = "isSwgIncur";
    public static final String FINANCE_RATE = "financeRate";
    public static final String PROG_RBD = "progRBD";
    public static final String INCR_RBD = "incrRBD";
    public static final String ORGNL_SALES_ORD_NUM = "orgnlSalesOrdNum";
    public static final String ORGNL_QUOTE_NUM = "orgnlQuoteNum";
    public static final String SALES_PLAY_NUM = "salesPlayNum";
    public static final String INIT_SPECL_BID_APPR_FLAG = "initSpeclBidApprFlag";
    public static final String PARAM_PRIOR_COMMENTS = "priorComments";
    public static final String RETURN_REASON = "returnReason";
    
    public static final String EVAL_SELECT_OPTION_FLAG="evalSelOptFlag";
	public static final String EVAL_COMMENT = "evalComment";
	
	public static final String SPLIT_BID = "splitBid";
    public static final String SPLIT_BID_ID1 = "splitBid_id1";
    public static final String SPLIT_BID_ID2 = "splitBid_id2";
    public static final String CHNL_OVVRD_REAS="channelOverridenReas";
}