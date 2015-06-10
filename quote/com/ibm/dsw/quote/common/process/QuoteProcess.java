package com.ibm.dsw.quote.common.process;

import is.domainx.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.common.domain.ApproverRuleValidation;
import com.ibm.dsw.quote.common.domain.ChargeAgreement;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLockInfo;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.common.domain.QuoteTxt;
import com.ibm.dsw.quote.common.domain.QuoteUserAccess;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.exception.CtrctInactiveException;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.submittedquote.domain.SubmittedQuoteAccess;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code><code> class.
 *    
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Mar 4, 2007
 */
public interface QuoteProcess {

    /**
     * Used by DSP02 (Create new sales quote) & USP02 (Upload sales quote spread
     * sheet. This method will create a new SESSION quote and persist to DB2.
     * The QuoteHeader of the created Quote will be returned with Country,
     * LineOfBusiness, CreatorId and the WebQuoteNum generated from DB2
     * 
     * @param country
     * @param lob
     * @param acqstn
     * @param creatorID
     * @return
     * @throws QuoteException
     */

    public QuoteHeader createNewSessionQuote(Country country, CodeDescObj lob, String acqstn, String creatorID,
            String progMigrationCode, String renwlQuoteNum, String audCode) throws QuoteException;

    // for quote header
    public QuoteHeader getQuoteHdrInfo(String creatorId) throws NoDataException, QuoteException;

    //  get quote header by webQuoteNum
    /**
     * @param webQuoteNum
     * @return
     * @throws NoDataException
     * @throws QuoteException
     */
    public QuoteHeader getQuoteHdrInfoByWebQuoteNum(String webQuoteNum) throws NoDataException, QuoteException;
    
    public QuoteHeader getQuoteHdrInfoByWebQuoteNumAndUserId(String webQuoteNum, String userId) throws NoDataException, QuoteException;
    

    /**
     * @param creatorID
     * @return
     * @throws QuoteException
     * @throws NoDataException
     */
    public Quote getDraftQuoteBaseInfoWithTransaction(String creatorID) throws QuoteException, NoDataException;
    public Quote getDraftQuoteBaseInfo(String creatorID) throws QuoteException, NoDataException;
    
    public Quote getDraftQuoteBaseInfoByQuoteNum(String webQuoteNum) throws QuoteException, NoDataException;

    //used by customer/partner Tab, Quote is populated with customer, reseller,
    // distributor details
    public void getQuoteDetailForCustTab(Quote quote) throws QuoteException;
    public void getQuoteDetailForCustTab(Quote quote, String bpSiteNum) throws QuoteException;

    //used by special bid Tab, Quote is populated with line item & special bid details
    public void getQuoteDetailForSpecialBidTab(Quote quote,QuoteUserSession user) throws QuoteException;
    
  //used by special bid Tab, Quote is populated with line item & special bid details
    public void getQuoteDetailForSpecialBidTab(Quote quote) throws QuoteException;

    //used by status Tab, Quote is populated with special bid details
    public void getQuoteDetailForStatusTab(Quote quote,QuoteUserSession user) throws QuoteException;

    //used by sales info Tab
    public void getQuoteDetailForSalesInfoTab(Quote quote) throws QuoteException;

    //used by submitted quote exective summary Tab
    public void getQuoteDetailForExecSummaryTab(Quote quote) throws QuoteException;
    
    /**
     * used by select customer use case, to update quoteheader's customer
     * Re-used by USP02. Updating is based on webQuoteNum, if it is unavailable, the webQuoteNum 
     * associated with the creatorId would be used to perform update.
     * @param creatorId
     * @param webQuoteNum 
     * @param customerNum
     * @param contractNum
     * 
     * @throws QuoteException
     */
    public void updateQuoteHeaderCustInfo(String creatorId, String webQuoteNum, String customerNum, String contractNum,
            int webCustId, String currencyCode,String endUserFlag) throws QuoteException;

    public void updateQuoteHeaderEndUserInfo(String creatorId, String webQuoteNum, String endCustNum) throws QuoteException;

    //below methods are directly DB2 calls, they are implemented in
    // quoteProcess_jdbc
    public void loadDraftQuoteToSession(String webQuoteNum, String creatorId, boolean openAsNewFlag) throws QuoteException;

    //by Doris Yuen
    public QuoteRightColumn getQuoteRightColumnInfo(String creatorId) throws QuoteException;

    //update partner information -- by Goshen Pan
    public boolean updateQuotePartnerInfo(String webQuoteNum, String lob, String partnerNum, String partnerType, String userID)
            throws QuoteException;

    public void deleteQuote(String quoteId, String userId) throws QuoteException;

    public List findDraftQuotes(String userId, String ownerFilter, String timeFilter) throws QuoteException;

    //update specific quote's opportunity owner -- by Goshen Pan
    public boolean updateOppOwner(String userId, SalesRep oppOwner) throws QuoteException;

    /**
     * used by post custoer and partner tab, to update or insert QuoteContact
     * information.
     * 
     * @param contract
     * @throws QuoteException
     */
    public void updateQuoteContact(ProcessContract contract) throws QuoteException;
    
    
    /**
     * Used in quoteSubmission.jsp 
     * 
     */
    public void updateQuoteContactInsubmit(ProcessContract contract,QuoteHeader quoteHeader)throws QuoteException;

    /**
     * used by post custoer and partner tab, to update QuoteHeader information.
     * 
     * @param creatorId
     * @param expireDate
     * @param fullfillmentSrc
     * @param partnerAccess
     * @param rselToBeDtrmndFlg
     * @param distribtrToBeDtrmndFlg
     * @param quoteClassfctnCode
     * @param startDate
     * @param oemAgreementType
     * @param sspType 
     * @throws QuoteException
     */
    public void updateQuoteHeaderCustPrtnrTab(String creatorId, Date expireDate, String fullfillmentSrc,
            String partnerAccess, int rselToBeDtrmndFlg, int distribtrToBeDtrmndFlg, String quoteClassfctnCode, Date startDate,
            String oemAgreementType, int pymTermsDays, int oemBidType, Date estmtdOrdDate, Date custReqstdArrivlDate, String sspType)
            throws QuoteException;

    /**
     * used by post sales information tab, to update QuoteHeader information.
     * @param creatorId
     * @param expireDate
     * @param briefTitle
     * @param quoteDesc
     * @param busOrgCode
     * @param opprtntyNum
     * @param exemptnCode
     * @param upsideTrendTowardsPurch
     * @param salesOdds
     * @param tacticCodes
     * @param comments
     * @param oemAgreementType
     * @param salesStageCode TODO
     * @param custReasCode TODO
     * 
     * @throws QuoteException
     */
    public void updateQuoteHeaderSalesInfoTab(String creatorId, Date expireDate, String briefTitle, String quoteDesc,
            String busOrgCode, String opprtntyNum, String exemptnCode, String upsideTrendTowardsPurch,
            String salesOdds, String tacticCodes, String comments, String quoteClassfctnCode, String salesStageCode, String custReasCode, Date startDate,
            String oemAgreementType, int blockRenewalReminder, int pymTermsDays, int oemBidType, Date estmtdOrdDate, Date custReqstdArrivlDate,String sspType) throws QuoteException;

    /**
     * @param creatorId
     * @throws QuoteException
     */
    public void saveDraftQuote(String creatorId, boolean createNewCopy) throws QuoteException, SPException;

    public void convertPaToPae(String creatorId) throws QuoteException;

    public void convertPaeToPa(String creatorId) throws QuoteException;

    public void populateRenewalQuote(String creatorId, String rnwlQuoteNum, boolean nonExpLineItems, String orignFromCustChgReqFlag) throws QuoteException;

    /**
     * given creator ID, return current draft quote detail, including quote
     * header, reseller, payer, customer, line items, delegates, opp owner.
     * 
     * @param creatorId
     * @return
     */
    public Quote getDraftQuoteDetails(String creatorId,boolean isPGSEnv) throws QuoteException, NoDataException;
    
    /**
     * given webQuoteNum, return current draft quote detail, including quote
     * header, reseller, payer, customer, line items, delegates, opp owner.
     * 
     * @param webQuoteNum
     * @return
     */
    public Quote getDraftQuoteDetailsForQuoteRetrieval(String webQuoteNum) throws QuoteException, NoDataException;

    /**
     * convert renewval quote to sales quote
     * 
     * @param creatorId
     * @throws QuoteException
     */
    public void convertToSalsQuote(String creatorId, String webQuoteNum, boolean createSpeclBidFlag)
            throws QuoteException;

    /**
     * 
     * @param quoteNum
     * @return
     * @throws QuoteException
     */
    public List getAvailablePrimaryStatus(String quoteNum, int rqAccess) throws QuoteException;

    public List getTerminationReasons() throws QuoteException;

    /**
     * Post the renewal quote status tab to update the renewal quote status and
     * termination tracking info.
     * 
     * @param quoteNum
     * @param pStatus
     * @param sStatus
     * @param activeSStatus
     * @param termReason
     * @param termComment
     * @throws QuoteException
     */
    public void postRQStatusTab(String quoteNum, String pStatus, String sStatus, boolean activeSStatus,
            String termReason, String termComment , String userID) throws QuoteException;
    
    public void updateQuoteSubmission(String creatorId, String webQuoteNum, int reqstICNFlag,
            int reqstPreCreditChkFlag, int inclTaxFlag, int includeFOL, int sendToQTCntFlag, int sendToPrmryCntFlag,
            int sendToAddtnlCntFlag, String addtnlCntEmailAdr, String qtCoverEmail, int incldLineItmDtlQuoteFlg,
            int sendQuoteToAddtnlPrtnrFlg, String addtnlPrtnrEmailAdr, int PAOBlockFlag,  int supprsPARegstrnEmailFlag,
            String preCreditCheckedQuoteNum, Integer fctNonStdTermsConds, String quoteOutputType, String softBidIteratnQtInd, String saasBidIteratnQtInd,Integer saaSStrmlndApprvlFlag, String quoteOutputOption,Integer budgetaryQuoteFlag) throws QuoteException;
    

    /**
     * 
     * @param quoteNum
     * @param pStatusCode
     * @param statusType --
     *            primary status or secondary status
     * @param active
     */
    public void validateAndSaveRQStatus(String quoteNum, String statusCode, int statusType, boolean active)
            throws QuoteException;

    public void updateTerminationTracking(String quoteNum, String reasonCode, String comments, String userID) throws QuoteException;
    
    public void orderDraftQuote(User user, QuoteUserSession salesRep, Quote quote) throws QuoteException,
            WebServiceException;
    
    public void updateQuoteStage(String userId, Quote quote) throws QuoteException;
    
    public void updateQuoteStageForSubmission(String userId, Quote quote) throws QuoteException;
    
    public void substituteSessionQuote(String creatorId, String webQuoteNum) throws QuoteException;
    
    public void updateSavedQuoteWithSessionData(String creatorId) throws QuoteException;
    
    public String copyUpdateSbmtQuoteToSession(String quoteNum, String creatorId, int webRefFlag) throws QuoteException;
    
    public void updateExpICNCRD(String quoteNum, Date expireDate, Integer reqstICNFlag, Integer reqstPreCreditChkFlag, String userEmailAdr, Date startDate) throws QuoteException;
    
    public void updateSapIDocNum(String webQuoteNum, String sapIDocNum, String userEmailAdr, String userAction) throws QuoteException;
    
    public Quote getSubmittedQuoteBaseInfo(String webQuoteNum, String userId, String up2ReportingUserIds) throws QuoteException, NoDataException;

    public void addQuoteAuditHist(String webQuoteNum, Integer lineItemNum, String userEmail, String userAction, String oldValue, String newValue) throws QuoteException;
    
    public void updateOpprInfo(String webQuoteNum, String oldOpprNum, String opprNum, String oldExemptionCode, String exemptionCode, String userEmail) throws QuoteException;
    
    public QuoteUserAccess getQuoteUserAccess(String webQuoteNum, String userId, String up2ReportingUserIds) throws QuoteException;
    
    public SubmittedQuoteAccess getSubmittedQuoteAccess(String webQuoteNum) throws QuoteException;
    
    public List findApprvrActHistsByQuoteNum(String webQuoteNum) throws QuoteException;
    
    public List findApprvrsByQuoteNum(String webQuoteNum) throws QuoteException;
    
    public void updateCustomerCreate(String webQuoteNum, Customer customer, String userID) throws QuoteException;
    
    public void updateRenewalQuoteSBInfo(String userId, String webQuoteNum) throws QuoteException;
    
    public Quote getDraftQuoteDetailsForSubmit(String creatorId, boolean isSubmittedByEvaluator) throws QuoteException, NoDataException;
    
    public Quote getDraftQuoteDetailsForSubmit(String creatorId) throws QuoteException, NoDataException;
    
    public Quote getSpecialBidQuoteInfoForEmail(String webQuoteNum) throws QuoteException, NoDataException;
    
    public QuoteTxt getRenewalQuoteDetailComment(String webQuoteNum) throws QuoteException;
    
    public int updateQuoteComment(String webQuoteNum, int qtTxtId, String qtTxtTypeCode, String qtTxt,
            String userEmail, String sectnId) throws QuoteException;
    
    public void callServicesToCreateQuote(String userId, User user, QuoteUserSession salesRep, Quote quote)
            throws QuoteException, WebServiceException;
    
    public void updateQuoteExecSummary(String webQuoteNum, String userId, Boolean recmdtFlag, String recmdtText, Double periodBookableRevenue, Double services,
    		                           String execSupport, String briefOverviewText) throws QuoteException;
    
    public void assignNewAgreement(String creatorId, String sapCtrctNum, Integer webCtrctId) throws QuoteException, CtrctInactiveException;
    
    public int createOrUpdateWebCtrct(int webCtrctId, String agrmntType, String authrztnGroup, String transSVPLevel, String userID) throws QuoteException;
    
    public Contract getContractByNum(String sapCtrctNum, String lob) throws QuoteException;
    
    public void unlockQuote(String webQuoteNum, String creatorId) throws QuoteException;
    
    public QuoteLockInfo getQuoteLockInfo(String webQuoteNum, String creatorId) throws QuoteException;
    
    public boolean isQuoteBelongsToUser(String creatorId, String quoteNum) throws QuoteException;

    /**
     * @param userId
     * @param quoteNum
     * @throws QuoteException
     */

    public void conv2StdCopy(String userId, String webQuoteNum) throws QuoteException;
    
    public void completeCustChgReqest(String userID, String quoteNum) throws QuoteException;

    public Quote getQuoteByNumForTest(String webQuoteNum,boolean isPGSEnv) throws QuoteException;
    
    public List getSQOHeadLineMsg(String applCode) throws QuoteException;
    //to check if bid iteration can be submitted
    public ApproverRuleValidation filterApprover(QuoteHeader header, List groups) throws QuoteException;
    //get related quote pre-check status
    public List getPrecheckStatus(String webQuoteNum) throws QuoteException;
    
    public Map createQuoteFromOrder(String userId, String orderNum) throws QuoteException;
    
    public Quote getQuoteDetailsForCreateQtFromOrder(String webQuoteNum) throws QuoteException, NoDataException;
    
    public void delInvalidLineItemsByWebQuoteNum(String webQuoteNum, List invalidPartList) throws QuoteException;

    /**
     * Get submitted quote info as a draft quote for some special copy usage.
     * @param webQuoteNum
     * @param creatorId
     * @return
     * @throws QuoteException
     * @throws NoDataException
     */
    public Quote getSubmittedQuoteDetailsAsDraft(String webQuoteNum,String creatorId) throws QuoteException, NoDataException;
    
    public String getPreCreditCheckedQuoteNum(String currentQuoteNum, int validMonths) throws QuoteException;
    
    /**
     * different with deleteQuote, this service delete record 
     * in table EBIZ1.WEB_QUOTE_TO_CREATOR_MAPPING
     * instead of setting SAVED_WEB_QUOTE_NUM = null;
     * @param quoteId
     * @throws QuoteException
     */
    public void deleteQuoteDeeply(String quoteId) throws QuoteException;
    
    public Quote getQuoteDetailForComparison(String webQuoteNum, User user, String userId, String up2ReportingUserIds) throws QuoteException;
    public List getFctNonStdTcAttachments(QuoteHeader quoteHeader) throws QuoteException;
    
    public int isProdSuppbyCPQ(String prodID, String webQuoteNum, String audCode) throws QuoteException;    
    public List retrieveLineItemsFromOrder(String chargeAgreementNum, String configId) throws QuoteException;
    public List retrieveLineItemsFromOrderNoTx(String chargeAgreementNum, String configId) throws QuoteException;

    public String createQuoteFromOrderForConfigurator(String orderNum, String userId, String audCode) throws QuoteException;

    public void copyQuoteInfoFromOrderForConfigurator(String orderNum, String webQuoteNum, String userId, String audCode) throws QuoteException;
    
    public void copyCustFromOrderToQuote(String webQuoteNum, String orderNum, String userId) throws QuoteException;
    

    public ChargeAgreement getChargeAgreementInfo(String chargeAgreementNum, String configurationId) throws QuoteException;
    
	public ChargeAgreement getChargeAgreementInfoWithoutTransaction(String chargeAgreementNum, String configurationId) throws QuoteException;

    public List getMigrateParts(String caNum) throws QuoteException;

	public String addMigrateParts(String caNum,String migrtnReqstNum,String partNums,String lineNums,
			String billingFreq,String coverageTerm,String userId)throws QuoteException;
	
	public Map getWebApplCodesByColName(String cnstntName)throws QuoteException;
	public void updateBPQuoteStage(String userId, String webQuoteNum,String quoteStageCode,Integer delSessFlag,Integer delLockFlag, String comments)throws QuoteException;

	public boolean eraseECData(String creatorId) throws QuoteException ;

    public List getQuoteLineItemsForSubmittedCRAD(String webQuoteNum) throws QuoteException;

	public Quote getQuoteForSubmittedCRAD(String webQuoteNum) throws QuoteException;
	
	public void updateLineItemCRAD(Quote quote, String userId)throws QuoteException;

	public void updateExpICNCRD(String quoteNum, Date expireDate, Integer reqstICNFlag, Integer reqstPreCreditChkFlag, String userEmailAdr, Date startDate, Date cradDate) throws QuoteException;
	
	public void updateTouFlag(String webQuoteNum, int saasTermCondCatFlag, String touURLs, String termsTypes, String termsSubTypes, String radioFlags, int updateTouFlag) throws QuoteException;
	
	public void updateWarningTouFlag(String webQuoteNum, String userId, String yesFlags, String noFileTous) throws QuoteException;
	/**
	 * retrieve necessary quote data for mail out the amended ToU.
	 * the data of quote includes:
	 *  - creator name/email
	 *  - submitter name/email(if submitted)
	 *  - opportunity owner name/email
	 *  - Customer number/name
	 *  - SAP quote number
	 *  - SAP order number(if ordered)
	 * @param webQuoteNum
	 * @return
	 * @throws QuoteException
	 * @throws NoDataException
	 */
    public Quote getQuoteForToUMailOutByQuoteNum(String webQuoteNum) throws QuoteException, NoDataException;

	public void updateTouFlag(String webQuoteNum) throws QuoteException;
	
	public boolean isPlaceHolderTou(String touURL) throws QuoteException;
	/**
	 * change quote agreement type if user chose different in TOU overlay
	 * @param webQuoteNum
	 * @param agrmtTypeCode(PA,CSRA,PAE,CSTA)
	 * @param agrmtNum
	 * @throws QuoteException
	 */
	public boolean updateQuoteAgrmtTypeByTou(String webQuoteNum,String agrmtTypeCode,String agrmtNum) throws QuoteException;
	
	/**
	 * @Abstract:  CSA - provide an interface to Overlay to search customer existing contracts
	 * @param webQuoteNum
	 * @param custNum
	 * @return Customer(1.CSA contract:Customer.getCsaContractList() 2.OTHER contract:Customer.getNoCsaContractList() )
	 * @throws QuoteException
	 */
	public Customer findCustForExistCtrctCust(String webQuoteNum,String custNum) throws QuoteException;
	
	public Quote getQuoteForApplianceAddress(String webQuoteNum) throws QuoteException;
	
	public void updateSapCtrctNum(String webQuoteNum, String sapCtrctNum) throws QuoteException;
	
	public String getWebEnrollmentNum()throws QuoteException;
	
	public Map<String,String> refreshSCODSConnPoolConfig()throws QuoteException;
	
	public void updateQuoteExpirationDateExtension(String webQuoteNum, String userId,Date expireDate,String justification,String updateSavedQuoteFlag)
			throws QuoteException;
	
	//  get quote header by webQuoteNum
    /**
     * @param webQuoteNum
     * @return
     * @throws NoDataException
     * @throws QuoteException
     */
    public QuoteHeader getQuoteHdrInfoByWebQuoteNumWithTransaction(String webQuoteNum) throws NoDataException, QuoteException;
    
    /**
     * update dsj quote info 
     * @param webQuoteNum
     * @param briefTitle    
     * @param opprNum
     * @param userEmail
     * @throws QuoteException
     */
    public void dsjUpdateQuoteInfo(String webQuoteNum,String briefTitle,  String opprNum, String userEmail) throws QuoteException; 

    /**
     * 
     * @param webQuoteNum
     * @param channel
     * @param trailId
     * @param userEmail
     * @throws QuoteException
     */
    public void dsjInsertQuoteFlag(String webQuoteNum,String channel,  String trailId, String userEmail) throws QuoteException;
    /**
     * 
     * @param webQuoteNum
     * @return
     * @throws QuoteException
     */
    public boolean isDsjQuote(String webQuoteNum) throws QuoteException ;
}
