package com.ibm.dsw.quote.common.domain;

import java.util.Date;
import java.util.List;

import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteHeaderFactory<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: 2007-3-8
 */

public abstract class QuoteHeaderFactory {
    
    private static QuoteHeaderFactory singleton = null;

    /**
     * 
     */
    public QuoteHeaderFactory() {
        super();
    }
    /**
     * Find the session quote by creator ID.
     * 
     * @param creatorId
     * @return
     * @throws TopazException
     */
    public abstract QuoteHeader findByCreatorID(String creatorId) throws TopazException;
    
    /**
     * Find the session quote by web quoteNum.
     * 
     * @param creatorId
     * @return
     * @throws TopazException
     */
    public abstract QuoteHeader findByWebQuoteNum(String webQuoteNum) throws TopazException;
    
    public abstract QuoteHeader findByWebQuoteNumAndUserId(String webQuoteNum, String userId) throws TopazException;
    
    /**
     * Create a quote, either for insert to DB2, or to delete from DB2. The isNew flag is set to true initially
     * 
     * @param creatorID
     * @return
     * @throws TopazException
     */
    public abstract QuoteHeader createQuote(String creatorID)throws TopazException;

    /**
     * Find the saved draft quotes
     * 
     * @param userId
     * @param ownerFilter
     * @param timeFilter
     * @return
     * @throws TopazException
     */
    public abstract List findSavedQuotes(String userId, String ownerFilter,String timeFilter) throws TopazException;
    
    /**
     * Update the cusotmer number and contract number
     * @param creatorId
     * @param webQuoteNum TODO
     * @param customerNum
     * @param contractNum
     * @throws TopazException
     */
    public abstract void updateCustomer(String creatorId, String webQuoteNum, String customerNum, String contractNum, int webCustId, String currencyCode,String endUserFlag) throws TopazException;

    /**
     * Update the cusotmer number and contract number
     * @param creatorId
     * @param webQuoteNum
     * @param customerNum
     * @param currencyCode
     * @throws TopazException
     */
    public abstract void updateEndUser(String creatorId, String webQuoteNum, String endCustNum) throws TopazException;
    
    /**
     * This is used to update the expiration date and fullfillment source
     * @param quoteNum
     * @param expireDate
     * @param fullfillmentSrc
     * @param sspType 
     * @throws TopazException
     */
    public abstract void updateExprDateFullfill(String creatorId, Date expireDate, String fullfillmentSrc,
            String partnerAccess, int rselToBeDtrmndFlg, int distribtrToBeDtrmndFlg, String quoteClassfctnCode, Date startDate,
            String oemAgreementType, int pymTermsDays, int oemBidType, Date estmtdOrdDate, Date custReqstdArrivlDate,String sspType)
            throws TopazException;
    
    public abstract void updateSalesInfo(String creatorId, Date expireDate, String briefTitle, String quoteDesc,
            String busOrgCode, String opprtntyNum, String exemptnCode, String upsideTrendTowardsPurch,
            String salesOdds, String tacticCodes, String comments, String quoteClassfctnCode, String salesStageCode, String custReasCode, Date startDate,
            String oemAgreementType, int blockRenewalReminder, int pymTermsDays, int oemBidType, Date estmtdOrdDate, Date custReqstdArrivlDate,String sspType) throws TopazException;
    
    public abstract void saveDraftQuote(String creatorId, boolean createNewCopy) throws TopazException;
    
    /**
     * This is used to update quote submission data
     * @throws TopazException
     */
    public abstract void updateQuoteSubmission(String creatorId, String webQuoteNum, int reqstICNFlag,
            int reqstPreCreditChkFlag, int inclTaxFlag, int includeFOL,int sendToQTCntFlag, int sendToPrmryCntFlag,
            int sendToAddtnlCntFlag, String addtnlCntEmailAdr, String qtCoverEmail, int incldLineItmDtlQuoteFlg,
            int sendQuoteToAddtnlPrtnrFlg, String addtnlPrtnrEmailAdr, int PAOBlockFlag, int supprsPARegstrnEmailFlag,
            String preCreditCheckedQuoteNum, Integer fctNonStdTermsConds, String quoteOutputType, String softBidIteratnQtInd, String saasBidIteratnQtInd,Integer saaSStrmlndApprvlFlag,String quoteOutputOption,Integer budgetaryQuoteFlag) throws TopazException;
    
    public abstract void updateQuoteStage(String userId, String webQuoteNum, String quoteStageCode, String sapIDocNum) throws TopazException;
    
    public abstract void updateQuoteStageToCancel(String userId, String webQuoteNum) throws TopazException;
    
    public abstract String copyUpdateSbmtQuoteToSession(String quoteNum, String creatorId, int webRefFlag) throws TopazException;
    
    public abstract void updateExpICNCRD(String quoteNum, Date expireDate, Integer reqstICNFlag, Integer reqstPreCreditChkFlag, String userEmailAdr, Date startDate, Date cradDate) throws TopazException;
    
    public abstract void updateSapIDocNum(String webQuoteNum, String sapIDocNum, String userEmailAdr, String userAction) throws TopazException;
    
    public abstract void unlockQuote(String webQuoteNum, String creatorId) throws TopazException;
    
    public abstract QuoteHeader getQuoteStage(String webQuoteNum) throws TopazException;
    
    public abstract List getDerivedApprvdBids(String webQuoteNum) throws TopazException;
    
    public static QuoteHeaderFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteHeaderFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FactoryNameHelper.singleton().getDefaultClassName(QuoteHeaderFactory.class.getName());
                Class factoryClass = Class.forName(factoryClassName);
                QuoteHeaderFactory.singleton = (QuoteHeaderFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteHeaderFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteHeaderFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteHeaderFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
    

    public abstract void deleteQuoteDeeply(String quoteId)throws TopazException;
    
	public abstract void updateTouFlag(String webQuoteNum, int saasTermCondCatFlag, String touURLs, String termsTypes,
			 String termsSubTypes, String radioFlags, int updateTouFlag, String agrmtTypeCode) throws TopazException;
	
	public abstract boolean isPlaceHolderTou(String touURL) throws TopazException;
	
	public abstract void updateWarningTouFlag(String webQuoteNum, String userId, String yesFlags, String noFileTous) throws TopazException;
	
	public abstract boolean updateQuoteAgrmtTypeByTou(String webQuoteNum,String agrmtTypeCode,String agrmtNum) throws TopazException;
	
	public abstract int getCountOfCsaTerms() throws TopazException;
	
	public abstract int getCountOfActivePartB(String webQuoteNum) throws TopazException;
	
	public abstract void updateQuoteExpirationDateExtension(String webQuoteNum, String userId,Date expireDate,String justification,String updateSavedQuoteFlag) throws TopazException;
}
