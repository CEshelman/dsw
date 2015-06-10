package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.CountryFactory;
import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.domain.EquityCurveTotal;
import com.ibm.dsw.quote.common.domain.TacticCode;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.dsw.quote.newquote.config.NewQuoteDBConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>QuoteHeaderPersister<code> class is the domrsvpFlagain persister for quote header.
 *
 * @author: zhengmr@cn.ibm.com
 *
 * Creation date: 2007-3-8
 */

public class QuoteHeaderPersister extends Persister {

    private QuoteHeader_jdbc quoteHeader_jdbc = null;

    private LogContext logContext = LogContextFactory.singleton().getLogContext();

    private static final String NON_ACQUISITION = "NON_ACQUISITION";

    /**
     *
     */
    public QuoteHeaderPersister(QuoteHeader_jdbc qh_jdbc) {
        super();
        this.quoteHeader_jdbc = qh_jdbc;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#update(java.sql.Connection)
     */
    public void update(Connection connection) throws TopazException {

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
     */
    public void insert(Connection connection) throws TopazException {
        //always use the first currency if a country has multiple currencies.
        CodeDescObj currency = (CodeDescObj) quoteHeader_jdbc.getCountry().getCurrencyList().get(0);

        HashMap params = new HashMap();
        params.put("piCreatorID", quoteHeader_jdbc.getCreatorId());
        params.put("piCountryCode", quoteHeader_jdbc.getCountry().getCode3());
        params.put("piCurrncyCode", currency.getCode());
        params.put("piLineOfBusi", quoteHeader_jdbc.getSystemLOB().getCode());
        params.put("piAcqrtnCode", quoteHeader_jdbc.getAcqrtnCode());
        params.put("piProgMigrtnCode", StringUtils.trimToEmpty(quoteHeader_jdbc.getProgMigrationCode()));
        params.put("piRenwlQuoteNum", StringUtils.trimToEmpty(quoteHeader_jdbc.getRenwlQuoteNum()));
        params.put("piAudCode", StringUtils.trimToEmpty(quoteHeader_jdbc.getAudCode()));

        QueryContext queryCtx = QueryContext.getInstance();
        String sqlQuery = queryCtx.getCompletedQuery(NewQuoteDBConstants.SP_CREATE_QUOTE, null);
        CallableStatement callStmt;
        try {
            callStmt = connection.prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, NewQuoteDBConstants.SP_CREATE_QUOTE, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            callStmt.execute();

            int poGenStatus = callStmt.getInt(1);

            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS)
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + NewQuoteDBConstants.SP_CREATE_QUOTE, e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
     */
    public void delete(Connection connection) throws TopazException {
        HashMap params = new HashMap();
        params.put("piQuoteId", quoteHeader_jdbc.getWebQuoteNum());
        params.put("piDeleteRecord", "0");
        params.put("piDeleteBy", quoteHeader_jdbc.getDeleteBy());

        QueryContext queryCtx = QueryContext.getInstance();
        String sqlQuery = queryCtx.getCompletedQuery(NewQuoteDBConstants.SP_DELETE_QUOTE, null);
        CallableStatement callStmt;
        try {
            callStmt = connection.prepareCall(sqlQuery);
            queryCtx.completeStatement(callStmt, NewQuoteDBConstants.SP_DELETE_QUOTE, params);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, params));

            callStmt.execute();

            int poGenStatus = callStmt.getInt(1);
            if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS)
                throw new SPException(LogHelper.logSPCall(sqlQuery, params), poGenStatus);
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + NewQuoteDBConstants.SP_DELETE_QUOTE, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
    	TimeTracer tracer = TimeTracer.newInstance();

        HashMap parms = new HashMap();
        String creatorId = quoteHeader_jdbc.getCreatorId();
        String webQuoteNum = quoteHeader_jdbc.getWebQuoteNum();
        parms.put("piCreatorId", creatorId == null ? "" : creatorId);
        parms.put("piWebQuoteNum", webQuoteNum == null ? "" : webQuoteNum);
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance(); 
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_HDRINFO, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_HDRINFO, parms); //CommonDBConstants.DB2_S_QT_GET_HDRINFO
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            tracer.stmtTraceStart("call S_QT_GET_HDRINFO");
            boolean retCode = ps.execute();
            tracer.stmtTraceEnd("call S_QT_GET_HDRINFO");

            int poGenStatus = ps.getInt(1);

            if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
            	logContext.error(this, LogHelper.logSPCall(sqlQuery, parms));
            	logContext.error(this, "SP call return code : " + poGenStatus);
                throw new NoDataException();
            } else if (poGenStatus != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), poGenStatus);
            }

            String countryCode = StringUtils.trimToEmpty(ps.getString(4));
            this.getQuoteHead_jdbc().currencyCode = StringUtils.trimToEmpty(ps.getString(5));
            this.getQuoteHead_jdbc().sapIntrmdiatDocNum = StringUtils.trimToEmpty(ps.getString(6));
            this.getQuoteHead_jdbc().soldToCustNum = StringUtils.trimToEmpty(ps.getString(7));
            this.getQuoteHead_jdbc().custName = StringUtils.trimToEmpty(ps.getString(8));
            this.getQuoteHead_jdbc().fulfillmentSrc = StringUtils.trimToEmpty(ps.getString(9));
            this.getQuoteHead_jdbc().rselCustNum = StringUtils.trimToEmpty(ps.getString(10));
            this.getQuoteHead_jdbc().payerCustNum = StringUtils.trimToEmpty(ps.getString(11));
            this.getQuoteHead_jdbc().quotePriceTot = ps.getDouble(12);
            this.getQuoteHead_jdbc().quoteTitle = StringUtils.trimToEmpty(ps.getString(13));
            this.getQuoteHead_jdbc().quoteDscr = StringUtils.trimToEmpty(ps.getString(14));
            this.getQuoteHead_jdbc().priorQuoteNum = StringUtils.trimToEmpty(ps.getString(15));
            this.getQuoteHead_jdbc().webQuoteNum = StringUtils.trimToEmpty(ps.getString(16));
            this.getQuoteHead_jdbc().contractNum = StringUtils.trimToEmpty(ps.getString(17));
            this.getQuoteHead_jdbc().volDiscLevelCode = StringUtils.trimToEmpty(ps.getString(18));
            this.getQuoteHead_jdbc().gsaPricngFlg = ps.getInt(19);
            this.getQuoteHead_jdbc().opprtntyOwnrEmailAdr = StringUtils.trimToEmpty(ps.getString(20));
            this.getQuoteHead_jdbc().busOrgCode = StringUtils.trimToEmpty(ps.getString(21));
            this.getQuoteHead_jdbc().opprtntyNum = StringUtils.trimToEmpty(ps.getString(22));
            this.getQuoteHead_jdbc().exemptnCode = StringUtils.trimToEmpty(ps.getString(23));
            this.getQuoteHead_jdbc().speclBidFlag = ps.getInt(24);

            String systemLobCode = StringUtils.trimToEmpty(ps.getString(25));

            this.getQuoteHead_jdbc().webCustId = ps.getInt(26);
            this.getQuoteHead_jdbc().quoteExpDate = ps.getDate(27);
            this.getQuoteHead_jdbc().speclBidSystemInitFlg = ps.getInt(28);
            this.getQuoteHead_jdbc().speclBidManualInitFlg = ps.getInt(29);
            this.getQuoteHead_jdbc().priceRecalcFlag = ps.getInt(30);
            this.getQuoteHead_jdbc().renwlQuoteNum = StringUtils.trimToEmpty(ps.getString(31));
            this.getQuoteHead_jdbc().audCode = StringUtils.trimToEmpty(ps.getString(32));
            this.getQuoteHead_jdbc().creatorId = StringUtils.trimToEmpty(ps.getString(33));
            this.getQuoteHead_jdbc().acqrtnCode = StringUtils.trimToEmpty(ps.getString(34));
            this.getQuoteHead_jdbc().quoteTypeCode = StringUtils.trimToEmpty(ps.getString(35));
            this.getQuoteHead_jdbc().priceStartDate = ps.getDate(36);
            this.getQuoteHead_jdbc().quoteStageCode = StringUtils.trimToEmpty(ps.getString(37));
            this.getQuoteHead_jdbc().tranPriceLevelCode = StringUtils.trimToEmpty(ps.getString(38));
            this.getQuoteHead_jdbc().lastQuoteDate = ps.getDate(39);
            this.getQuoteHead_jdbc().sapDistribtnChnlCode = StringUtils.trimToEmpty(ps.getString(40));
            this.getQuoteHead_jdbc().sapSalesDocTypeCode = StringUtils.trimToEmpty(ps.getString(41));
            this.getQuoteHead_jdbc().reqstIbmCustNumFlag = ps.getInt(42);
            this.getQuoteHead_jdbc().reqstPreCreditCheckFlag = ps.getInt(43);
            this.getQuoteHead_jdbc().partHasPriceFlag = ps.getInt(44);
            this.getQuoteHead_jdbc().hasPartsFlag = ps.getInt(45);
            this.getQuoteHead_jdbc().totalPoints = ps.getDouble(46);

            String prtnrAccess = StringUtils.trimToEmpty(ps.getString(47));
            if (DraftQuoteConstants.PARTNER_ACCESS_YES.equalsIgnoreCase(prtnrAccess))
                this.getQuoteHead_jdbc().rnwlPrtnrAccessFlag = 1;
            else if (DraftQuoteConstants.PARTNER_ACCESS_NO.equalsIgnoreCase(prtnrAccess))
                this.getQuoteHead_jdbc().rnwlPrtnrAccessFlag = 0;
            else
                this.getQuoteHead_jdbc().rnwlPrtnrAccessFlag = -1;

            this.getQuoteHead_jdbc().renwlEndDate = ps.getDate(48);
            this.getQuoteHead_jdbc().ordQtyTotal = ps.getInt(49);
            this.getQuoteHead_jdbc().upsideTrendTowardsPurch = StringUtils.trimToEmpty(ps.getString(50));
            this.getQuoteHead_jdbc().renwlQuoteSalesOddsOode = StringUtils.trimToEmpty(ps.getString(51));
            this.getQuoteHead_jdbc().inclTaxFinalQuoteFlag = ps.getInt(52);
            this.getQuoteHead_jdbc().sendQuoteToPrmryCntFlag = ps.getInt(53);
            this.getQuoteHead_jdbc().sendQuoteToQuoteCntFlag = ps.getInt(54);
            this.getQuoteHead_jdbc().sendQuoteToAddtnlCntFlag = ps.getInt(55);
            this.getQuoteHead_jdbc().addtnlCntEmailAdr = StringUtils.trimToEmpty(ps.getString(56));
            this.getQuoteHead_jdbc().rnwlTermntnReasCode = StringUtils.trimToEmpty(ps.getString(57));
            this.getQuoteHead_jdbc().salesComments = StringUtils.trimToEmpty(ps.getString(58));
            this.getQuoteHead_jdbc().termntnComments = StringUtils.trimToEmpty(ps.getString(59));
            this.getQuoteHead_jdbc().qSubmitCoverText = StringUtils.trimToEmpty(ps.getString(60));
            this.getQuoteHead_jdbc().rqModDate = ps.getTimestamp(61);
            this.getQuoteHead_jdbc().rqStatModDate = ps.getTimestamp(62);
            this.getQuoteHead_jdbc().submittedDate = ps.getTimestamp(63);
            this.getQuoteHead_jdbc().sapQuoteNum = ps.getString(64);
            this.getQuoteHead_jdbc().calcBLPriceFlag = ps.getInt(65);
            this.getQuoteHead_jdbc().submitterId = ps.getString(66);
            this.getQuoteHead_jdbc().dLocalExtndTaxAmtTot = ps.getDouble(67);
            this.getQuoteHead_jdbc().dQuotePriceTotIncldTax = ps.getDouble(68);
            this.getQuoteHead_jdbc().dateOvrrdByApprvrFlg = ps.getInt(69)==1 ? true:false;
            this.getQuoteHead_jdbc().originalIdocNum = ps.getString(70);
            this.getQuoteHead_jdbc().containLineItemRevnStrm = ps.getInt(71);
            this.getQuoteHead_jdbc().savedQuoteNum = StringUtils.trimToEmpty(ps.getString(72));
            this.getQuoteHead_jdbc().ovrrdTranLevelCode = StringUtils.trimToEmpty(ps.getString(73));


            Object opObj = ps.getObject(74);
            if (opObj != null){
                this.getQuoteHead_jdbc().offerPrice = new Double(ps.getDouble(74));
            }

            this.getQuoteHead_jdbc().incldLineItmDtlQuoteFlg = ps.getInt(75);
            this.getQuoteHead_jdbc().priceCountry = CountryFactory.singleton().findByCode3(StringUtils.trimToEmpty(ps.getString(76)));
            this.getQuoteHead_jdbc().progMigrationCode = StringUtils.trimToEmpty(ps.getString(77));
            this.getQuoteHead_jdbc().approvalRouteFlag = ps.getObject(78) == null ? 1 : ps.getInt(78);
            this.getQuoteHead_jdbc().distribtrToBeDtrmndFlag = ps.getInt(79);
            this.getQuoteHead_jdbc().resellerToBeDtrmndFlag = (ps.getInt(80) == 1);
            this.getQuoteHead_jdbc().sendQuoteToAddtnlPrtnrFlag = (ps.getInt(81) == 1);
            this.getQuoteHead_jdbc().addtnlPrtnrEmailAdr = ps.getString(82);
            this.getQuoteHead_jdbc().backDatingFlag = (ps.getInt(83) == 1);
            this.getQuoteHead_jdbc().quoteClassfctnCode = StringUtils.trimToNull(ps.getString(84));
            this.getQuoteHead_jdbc().renwlQuoteSpeclBidFlag = ps.getInt(85);
            this.getQuoteHead_jdbc().ELAFlag = (ps.getInt(86) == 1);
            this.getQuoteHead_jdbc().PAOBlockFlag = ps.getInt(87);
            this.getQuoteHead_jdbc().priorQuoteSbmtDate = ps.getTimestamp(88);
            this.getQuoteHead_jdbc().priorSapQuoteNum = ps.getString(89);
            this.getQuoteHead_jdbc().setRSVPSRPOnly("1".equals(ps.getString(90)));
            String saasRenewalFlag = ps.getString(91);
            String saasMigrationFlag = ps.getString(92);
            String monthlyRenewalFlag =ps.getString(93);
            String monthlyMigrationFlag = ps.getString(94);
            if (saasRenewalFlag==null || "".equals(saasRenewalFlag.trim())){
            	this.getQuoteHead_jdbc().setSaasRenewalFlag(null);
            } else {
            	 this.getQuoteHead_jdbc().setSaasRenewalFlag("1".equals(saasRenewalFlag));
            }
            if (saasMigrationFlag==null||"".equals(saasMigrationFlag.trim())){
            	this.getQuoteHead_jdbc().setSaasMigrationFlag(null);
            } else {
            	 this.getQuoteHead_jdbc().setSaasMigrationFlag("1".equals(saasMigrationFlag));
            }
            if (monthlyRenewalFlag==null||"".equals(monthlyRenewalFlag.trim())){
            	this.getQuoteHead_jdbc().setMonthlyRenewalFlag(null);
            } else {
            	 this.getQuoteHead_jdbc().setMonthlyRenewalFlag("1".equals(monthlyRenewalFlag));
            }
            if (monthlyMigrationFlag==null||"".equals(monthlyMigrationFlag.trim())){
            	this.getQuoteHead_jdbc().setMonthlyMigrationFlag(null);
            } else {
            	 this.getQuoteHead_jdbc().setMonthlyMigrationFlag("1".equals(monthlyMigrationFlag));
            }
            
            rs = ps.getResultSet();
            List tacticCodes = new ArrayList();
            while (rs.next()) {
                TacticCode tacticCode = new TacticCode();
                tacticCode.setTacticCode(StringUtils.trimToEmpty(rs.getString("MKTG_TACTIC_CODE")));
                tacticCodes.add(tacticCode);
            }
            rs.close();
            this.getQuoteHead_jdbc().tacticCodes = tacticCodes;

            if (ps.getMoreResults()) {
                rs = ps.getResultSet();
                List acquisitionCodes = new ArrayList();
                while (rs.next()) {
                    String acqstnCode = rs.getString(1);
                    if (StringUtils.isBlank(acqstnCode))
                        acqstnCode = NON_ACQUISITION;
                    acquisitionCodes.add(acqstnCode);
                }
                rs.close();
                this.getQuoteHead_jdbc().acquisitionCodes = acquisitionCodes;
            }

            if (ps.getMoreResults()) {
                rs = ps.getResultSet();
                while (rs.next()) {
                    this.getQuoteHead_jdbc().salesStageCode = StringUtils.trimToEmpty(rs.getString("SALES_STAGE_CODE"));
                    this.getQuoteHead_jdbc().custReasCode = StringUtils.trimToEmpty(rs.getString("CUST_REAS_CODE"));
                    this.getQuoteHead_jdbc().webCtrctId = rs.getInt("WEB_CTRCT_ID");
                    this.getQuoteHead_jdbc().createCtrctFlag = (rs.getInt("CREATE_CTRCT_FLAG") == 1);
                    this.getQuoteHead_jdbc().copyFromApprvdBidFlag = (rs.getInt("CPY_FROM_APPRVD_BID_FLAG") == 1);
                    this.getQuoteHead_jdbc().quoteStartDate = (rs.getDate("EFF_DATE"));
                    this.getQuoteHead_jdbc().ordgMethodCode = StringUtils.trimToEmpty(rs.getString("ORDG_METHOD_CODE"));
                    this.getQuoteHead_jdbc().endUserCustNum = StringUtils.trimToEmpty(rs.getString("END_USER_CUST_NUM"));
                    this.getQuoteHead_jdbc().cmprssCvrageFlag = (rs.getInt("CMPRSS_CVRAGE_FLAG") == 1);
                    this.getQuoteHead_jdbc().hasPARenwlLineItmsFlag = (rs.getInt("HAS_PA_RN_LINEITEM") == 1);
                    this.getQuoteHead_jdbc().setLockedFlag((rs.getInt("IS_LOCKED_FLAG") == 1));
                    this.getQuoteHead_jdbc().setLockedBy(StringUtils.trimToEmpty(rs.getString("LOCKED_BY")));
                    this.getQuoteHead_jdbc().setCopied4PrcIncrQuoteFlag((rs.getInt("CPY_FOR_PRIC_INCRS_FLAG") == 1));
                    this.getQuoteHead_jdbc().setQuoteAddDate(rs.getTimestamp("ADD_DATE"));
                    this.getQuoteHead_jdbc().setBlockRnwlReminder(rs.getInt("BLOCK_RENWL_REMNDR_FLAG"));
                    this.getQuoteHead_jdbc().modDate = rs.getTimestamp("MOD_DATE");
                    this.getQuoteHead_jdbc().setOriQuotePriceTot(rs.getDouble("OriQuotePriceTot"));
                    this.getQuoteHead_jdbc().setQtEligible4BidIteratn(rs.getInt("QtEligible4BidIteratn") == 1);
                    this.getQuoteHead_jdbc().setPymTermsDays(rs.getInt("PMT_TERMS_DAYS"));
                    this.getQuoteHead_jdbc().setHasDiscountableItems(rs.getInt("HasDiscountableItems") == 1);
                    this.getQuoteHead_jdbc().setSoftwarePartWithoutApplncId(rs.getInt("IS_SOFTWARE_PART_WITHOUT_APPINCID")==1);
//                    this.getQuoteHead_jdbc().setBidIteratnQt(rs.getInt("SPECL_BID_ITERATN_FLAG") == 1);
                    this.getQuoteHead_jdbc().setSoftBidIteratnQtInd(rs.getInt("SPECL_BID_ITERATN_FLAG"));
                    this.getQuoteHead_jdbc().setLatamUpliftPct(rs.getDouble("UPLIFT_PCT"));
                    this.getQuoteHead_jdbc().setHasLotusLiveItem(rs.getInt("HAS_LOTUS_LIVE_LINEITEM") == 1);
                    this.getQuoteHead_jdbc().setOemBidType(rs.getInt("OEM_QUOTE_TYPE"));
                    this.getQuoteHead_jdbc().setSubmitterName(rs.getString("SUBMITTER_NAME"));
                    this.getQuoteHead_jdbc().setCopiedForOutputChangeFlag(rs.getInt("CPY_FOR_CHG_OP_OPTN_FLAG")==1);
                    this.getQuoteHead_jdbc().fctNonStdTermsConds = (rs.getObject("FCT_NON_STD_TERMS_CONDS_IND") == null?
                    		                                                                null : new Integer(rs.getInt("FCT_NON_STD_TERMS_CONDS_IND")));
                    this.getQuoteHead_jdbc().setHasSaaSLineItem(rs.getInt("HAS_SAAS_LINEITEM") == 1);
                    if(rs.getObject("SAAS_TOT_CMMTMT_VAL")!=null){
                    	this.getQuoteHead_jdbc().setSaasTotCmmtmtVal(new Double(rs.getDouble("SAAS_TOT_CMMTMT_VAL")));
                    }
                    this.getQuoteHead_jdbc().setOnlySaaSParts(rs.getInt("ONLY_SAAS_PARTS") == 1);
                    this.getQuoteHead_jdbc().setRefDocNum(rs.getString("REF_DOC_NUM"));
                    this.getQuoteHead_jdbc().setEstmtdOrdDate(rs.getDate("ESTD_ORD_DT"));
                    this.getQuoteHead_jdbc().setCustReqstArrivlDate(rs.getDate("CUST_REQSTD_ARRIVL_DATE"));
                    this.getQuoteHead_jdbc().setMaxEstdNumProvisngDays(rs.getInt("MAX_ESTD_NUM_PROVISNG_DAYS"));
                    this.getQuoteHead_jdbc().setHasConfigrtnFlag(rs.getInt("HAS_CONFIGRTN_FLAG") == 1);
                    this.getQuoteHead_jdbc().setCACustCurrncyNotMatchFlag(rs.getInt("CA_CUST_CURRNCY_NOT_MATCH") == 1);
                    this.getQuoteHead_jdbc().setAddTrdOrCotermFlag(rs.getInt("ADD_TRD_OR_COTERM") == 1);
                    this.getQuoteHead_jdbc().setHasNewConfFlag(rs.getInt("HAS_NEW_CONF_FLAG") == 1);
                    this.getQuoteHead_jdbc().setBPRelatedCust(rs.getInt("BP_RELATED_CUST") == 1);
                    this.getQuoteHead_jdbc().setQcCompany(rs.getString("QC_COMPANY"));
                    this.getQuoteHead_jdbc().setQcCountry(rs.getString("QC_COUNTRY"));
                    this.getQuoteHead_jdbc().setMatchBPsInChargeAgreementFlag(rs.getInt("MATCH_BPS_CHARGE_AGREEMENT_FLAG") == 1);
                    if(rs.getObject("SAAS_TOT_CMMTMT_VAL_CHNL")!=null){
                    	this.getQuoteHead_jdbc().setSaasBpTCV(new Double(rs.getDouble("SAAS_TOT_CMMTMT_VAL_CHNL")));
                    }
                    this.getQuoteHead_jdbc().setCreatorEmail(StringUtils.trimToEmpty(rs.getString("CREATOR_EMAIL")));
                    this.getQuoteHead_jdbc().setSubmittedOnSQO(rs.getInt("SUBMITTED_ON_SQO") == 1);
                    this.getQuoteHead_jdbc().setHasNewLicencePart(rs.getInt("HAS_NEW_LICENSE_PART") == 1);
                    this.getQuoteHead_jdbc().setHasSaasSubNoReNwPart(rs.getInt("HAS_SAAS_SUB_NO_RNWL_PART") == 1);
                    this.getQuoteHead_jdbc().setFirmOrdLtrFlag(rs.getInt("FIRM_ORD_LTR_FLAG"));
                    this.getQuoteHead_jdbc().setHasAppliancePartFlag(rs.getInt("HAS_APPLIANCE_PART_FLAG") == 1);
                    this.getQuoteHead_jdbc().setQuoteOutputType(rs.getString("QUOTE_OUTPUT_TYPE"));
                    this.getQuoteHead_jdbc().setHasAppMainPart(rs.getInt("HAS_APP_MAIN_PART") == 1);
                    this.getQuoteHead_jdbc().setSaasFCTToPAQuote(rs.getInt("FCT_TO_PA_MIGRTN_FLAG")==1);
                    this.getQuoteHead_jdbc().setTermDiffInDiffFctConfig(rs.getInt("TERM_DIFF_IN_DIFF_FCT_CFG")==1);
                    this.getQuoteHead_jdbc().setOrignlSalesOrdRefNum(rs.getString("ORIGNL_SALES_ORD_REF_NUM"));
                    this.getQuoteHead_jdbc().setSaasBidIteratnQtInd(rs.getInt("SAAS_BID_ITERATN_FLAG"));
                    
                    this.getQuoteHead_jdbc().setBidIteratnQt(this.getQuoteHead_jdbc().getSoftBidIteratnQtInd() == 1 || this.getQuoteHead_jdbc().getSaasBidIteratnQtInd() == 1);
                    this.getQuoteHead_jdbc().setSaaSStrmlndApprvlFlag(rs.getInt("SAAS_STRMLND_APPRVL_FLAG") == 1);
                    this.getQuoteHead_jdbc().setPriorQuoteExpDate(rs.getDate("PRIOR_QUOTE_EXP_DATE"));
                    this.getQuoteHead_jdbc().setQuoteOutputOption(rs.getString("QUOTE_OUTPUT_OPTION"));
                    this.getQuoteHead_jdbc().setHasAppUpgradePart(rs.getInt("HAS_APP_UPGRADE_PART") == 1);
                    this.getQuoteHead_jdbc().setRebillCreditOrder(rs.getInt("RebillCreditOrder") == 1);
                    this.getQuoteHead_jdbc().setEvalDate(rs.getTimestamp("EVALTN_DATE"));
                    this.getQuoteHead_jdbc().setEvalEmailAdr(rs.getString("EVALTR_EMAIL_ADR"));
                    this.getQuoteHead_jdbc().setHasEvaluator(rs.getInt("IsHasEvaluator") == 1);
                    this.getQuoteHead_jdbc().setQuoteCntryEvaluator(rs.getInt("IsQuoteCntryEvaluator") == 1);
                    this.getQuoteHead_jdbc().setSspType(rs.getString("sspProviderType"));
                    this.getQuoteHead_jdbc().setBpSubmitterEmail(rs.getString("BPSubmitterEmailAdr"));
                    this.getQuoteHead_jdbc().setBpSubmitDate(rs.getDate("BPSubmitDate"));
                    this.getQuoteHead_jdbc().setBpSubmitterName(rs.getString("BPSubmitterFullName"));
                    this.getQuoteHead_jdbc().setPGSNewPAEnrolled(rs.getInt("IsPGSNewPAEnrolled") == 1);
                    this.getQuoteHead_jdbc().setCaEndUserCustNum(rs.getString("CA_END_USER_CUST_NUM"));
                    if(rs.getObject("IMPLD_GRWTH_PCT")!=null){
                    	this.getQuoteHead_jdbc().setImpldGrwthPct(new Double(rs.getDouble("IMPLD_GRWTH_PCT")));
                    }
                    this.getQuoteHead_jdbc().setYtyGrwthPct(rs.getDouble("YTY_GRWTH_PCT"));
                    this.getQuoteHead_jdbc().setRQLineItemCount(rs.getInt("RQ_LINE_ITEM_COUNT"));
                    this.getQuoteHead_jdbc().setContainRQLineItem((rs.getInt("HAS_RQ_LINE_ITEM") == 1));
                    this.getQuoteHead_jdbc().setAddTrd(rs.getInt("isAddTrd") == 1);
                    this.getQuoteHead_jdbc().setEvalActionCode(rs.getString("EvalActionCode"));
                    this.getQuoteHead_jdbc().setAllPartsHasMediaAttr((rs.getInt("AllPartsHasMediaAttr") == 1));
                   // this.getQuoteHead_jdbc().setTotalPriceInUSD((rs.getDouble("TotalPriceInUSD")));
                    this.getQuoteHead_jdbc().setECEligible(rs.getInt("isECEligible") == 1);
                    this.getQuoteHead_jdbc().setECRecalculateFlag(rs.getInt("isECRecalculateFlag") == 1);
                    Double weightAverageMin = rs.getObject("EC_WTD_AVG_MIN_PCT") == null? null : rs.getDouble("EC_WTD_AVG_MIN_PCT");
                    Double weightAverageMax = rs.getObject("EC_WTD_AVG_MAX_PCT") == null? null : rs.getDouble("EC_WTD_AVG_MAX_PCT");
                    this.getQuoteHead_jdbc().setEquityCurveTotal(new EquityCurveTotal(weightAverageMin, weightAverageMax));
                    this.getQuoteHead_jdbc().setDisShipInstAdrFlag(rs.getInt("isDisShipInstAdrFlag") == 1);
                    this.getQuoteHead_jdbc().setOmittedLine(rs.getInt("isOmittedLine") == 1);
                    this.getQuoteHead_jdbc().setOmittedLineRecalcFlag(rs.getInt("omittedLineRecalcFlag"));
                    this.getQuoteHead_jdbc().setBudgetaryQuoteFlag(rs.getInt("BUDGTRY_QUOTE_FLAG"));
                    this.getQuoteHead_jdbc().setStdloneSaasGenTermFlag(rs.getInt("STDLONE_SAAS_GEN_TERM_FLAG") == 1);
                    this.getQuoteHead_jdbc().setSaasTermCondCatFlag(rs.getInt("SAAS_TERM_COND_CAT_FLAG"));
                    this.getQuoteHead_jdbc().setT2CreatedFlag(rs.getInt("T2_CREATED_FLAG") == 1);
                    this.getQuoteHead_jdbc().setCreatorName(rs.getString("CREATEOR_NAME"));
                    this.getQuoteHead_jdbc().setOpprtntyOwnrName(rs.getString("OPPRTNTY_OWNR_NAME"));
                    this.getQuoteHead_jdbc().setSapOrderNum(rs.getString("SAP_SALES_ORD_NUM"));
                    this.getQuoteHead_jdbc().setHasDivestedPart(rs.getInt("HAS_DIVESTED_PART") == 1);
                    this.getQuoteHead_jdbc().setEndUserWebCustId(rs.getInt("END_USER_WEB_CUST_ID"));
                    this.getQuoteHead_jdbc().setDivstdObsltPartFlag(rs.getInt("DIVSTD_OBSLT_PART_FLAG"));
                    this.getQuoteHead_jdbc().setAgrmtTypeCode(StringUtils.trimToEmpty(rs.getString("AGRMT_TYPE_CODE")));
                    this.getQuoteHead_jdbc().setSerialNumWarningFlag(rs.getInt("HAS_INVALID_APPLNC_MTM") == 1);
                    this.getQuoteHead_jdbc().setNeedReconfigFlag(rs.getInt("NEED_RECONFIG_FLAG") == 1);
                    this.getQuoteHead_jdbc().setHasMonthlySoftPart(rs.getInt("HAS_MONTYLY_SOFT_PART") == 1);
					this.getQuoteHead_jdbc().setHasSoftSLineItem(rs.getInt("HAS_SOFT_PART") == 1);
					this.getQuoteHead_jdbc().setSubRgnCode(rs.getString("WWIDE_SUB_RGN_CODE"));
					this.getQuoteHead_jdbc().setExpDateExtendedFlag(rs.getInt("isExpirationDateExtendedFlag")==1);
					this.getQuoteHead_jdbc().setExpDateExtensionJustification(rs.getString("expirationDateExtensionJustification"));
	                this.getQuoteHead_jdbc().setGridFlag(rs.getInt("GRID_DELEGATION_FLAG") == 1);

                }
                rs.close();
            }
            
            if(ps.getMoreResults()){
            	rs = ps.getResultSet();
                if(rs.next()){
                    String cursorName = rs.getString(1);
                    if ("OVERAL_STATS".equalsIgnoreCase(cursorName)){
                    	processOveralStats4Cpexdate(rs);
                    }
                }
            }
            
            while(ps.getMoreResults()){
            	rs2 = ps.getResultSet();
                if(rs2.next()){
                    String cursorName = rs2.getString(1);
                    if ("OVERAL_STATS".equalsIgnoreCase(cursorName)){
                    	processOveralStats(rs2);
                    }else if("STREAMLINED_INFO".equalsIgnoreCase(cursorName)){
                    	processStreamline(rs2);
                    }
                }
            }

            this.getQuoteHead_jdbc().country = CountryFactory.singleton().findByCode3(countryCode);
            this.getQuoteHead_jdbc().setLobCode(systemLobCode);

            this.isNew(false);
            this.isDeleted(false);

            if (StringUtils.isBlank(this.getQuoteHead_jdbc().getWebQuoteNum())){
            	logContext.error(this, "creatorId: " + creatorId + "  webQuoteNum: " + getQuoteHead_jdbc().getWebQuoteNum());
                throw new NoDataException();
            }
        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            logContext.error(this, LogHelper.logSPCall(CommonDBConstants.DB2_S_QT_GET_HDRINFO, parms));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_GET_HDRINFO, e);
        } finally{
	    	try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
				if (null != rs2 && !rs2.isClosed())
				{
					rs2.close();
				}
			} catch (SQLException e) {
				logContext.error(this,"Failed to close the resultset!");
			}
        	tracer.dump();
        }
    }

    public QuoteHeader_jdbc getQuoteHead_jdbc() {
        return this.quoteHeader_jdbc;
    }
    
    private void processOveralStats(ResultSet rs) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
            List overallStatuses = new ArrayList();
            do {
                String osCode = StringUtils.trimToEmpty(rs.getString("STAT"));
                String osCodeDesc = rs.getString("DSCR");
                CodeDescObj_jdbc cdObj_jdbc = new CodeDescObj_jdbc(osCode, osCodeDesc);
                overallStatuses.add(cdObj_jdbc);
            } while (rs.next());
            rs.close();
            this.getQuoteHead_jdbc().quoteOverallStatuses = overallStatuses;
        }catch(Exception e){
            logger.error("Failed to get overall states info",e);
            throw new TopazException(e);
        }
    }
    
    private void processOveralStats4Cpexdate(ResultSet rs) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
            List overallStatuses = new ArrayList();
            do {
                String osCode = StringUtils.trimToEmpty(rs.getString("STAT"));
                String osCodeDesc = rs.getString("DSCR");
                CodeDescObj_jdbc cdObj_jdbc = new CodeDescObj_jdbc(osCode, osCodeDesc);
                overallStatuses.add(cdObj_jdbc);
            } while (rs.next());
            rs.close();
            this.getQuoteHead_jdbc().quoteOverallStatuses4Cpexdate = overallStatuses;
        }catch(Exception e){
            logger.error("Failed to get overall states info",e);
            throw new TopazException(e);
        }
    }
    
    private void processStreamline(ResultSet rs) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
            this.getQuoteHead_jdbc().streamlinedWebQuoteNum = StringUtils.trimToEmpty(rs.getString("WEB_QUOTE_NUM"));
            rs.close();
        }catch(Exception e){
            logger.error("Failed to get streamline info",e);
            throw new TopazException(e);
        }
    }

}
