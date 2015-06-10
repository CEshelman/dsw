package com.ibm.dsw.quote.common.domain.jdbc;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.BillingOption;
import com.ibm.dsw.quote.appcache.domain.BillingOptionFactory;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.ProductPortfolioFactory;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.ChargeableUnit;
import com.ibm.dsw.quote.common.domain.DeployModel;
import com.ibm.dsw.quote.common.domain.EquityCurve;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.OmitRenewalLine;
import com.ibm.dsw.quote.common.domain.Part;
import com.ibm.dsw.quote.common.domain.PartDisplayAttr;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItem.PartGroup;
import com.ibm.dsw.quote.common.domain.QuoteLineItem.PriorYearSSPrice;
import com.ibm.dsw.quote.common.domain.QuoteLineItem.SpBidPartGroup;
import com.ibm.dsw.quote.common.domain.QuoteLineItemBillingOption;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.SaasPart;
import com.ibm.dsw.quote.common.domain.YTYGrowth;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.exception.SPException;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * <br>
 * This
 * <code>QuoteLineItemFactory_jdbc<code> class is jdbc implementation of QuoteLineItemFactory.
 *
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 *
 * Creation date: Mar 4, 2007
 *
 * $Log: QuoteLineItemFactory_jdbc.java,v $
 * Revision 1.121  2010/11/08 08:51:10  cyxu
 * RTC task 41361:Create a sales quote from this order.reviewed by Owen.
 *
 * Revision 1.120  2010/06/30 07:47:10  wxiaoli
 * SQR : Pushpa's area : Update web_quote sps : Need to put current quote user as mod_by, RTC 3407, reviewed by Will
 *
 * Revision 1.119  2010/06/25 17:00:00  dsmith
 * Refactor getGoupName() to getGroupName()
 *
 * Revision 1.118  2010/06/25 08:58:25  changwei
 * Task 3334 SQO date logic : Enhance parts and pricing tab to derive 2 new line item attributes additionally
 * Reviewed by Vivian.
 *
 * Revision 1.117  2010/06/23 14:20:22  dsmith
 * Modify Special Bid Part Group determination based on country RTC Task 3143, Ebiz Help Request #MCOR-84YHK8
 *
 * Revision 1.116  2010/06/22 11:10:05  wxiaoli
 * BidIteration : DSQ02f: Display draft sales quote parts & pricing tab in bid iteration mode, RTC 3399, reviewed by Will
 *
 * Revision 1.115  2010/06/03 07:41:25  wxiaoli
 * put current quote user as mod_by and add_by, RTC 3407, reviewed by Will
 *
 * Revision 1.114  2010/05/28 08:08:56  wxiaoli
 * Convergence : Display parts & pricing tab, RTC3352,3353,3354, reviewed by Will
 *
 * Revision 1.113  2010/05/27 07:46:05  wxiaoli
 * contract level prcing flag for Display parts & pricing tab, RTC 3211,3212,3213,3214,3215,3216,3217, reviewed by Will
 *
 */
public class QuoteLineItemFactory_jdbc extends QuoteLineItemFactory {
	private static final LogContext logger = LogContextFactory.singleton().getLogContext();
    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemFactory#createQuoteLineItem(java.lang.String)
     */
    @Override
	public QuoteLineItem createQuoteLineItem(String webQuoteNum, Part part, String userID) throws TopazException {
        QuoteLineItem_jdbc quoteLineItem_jdbc = new QuoteLineItem_jdbc(webQuoteNum, part);
        quoteLineItem_jdbc.isNew(true); //persist to DB2
        quoteLineItem_jdbc.setUserID(userID);
        return quoteLineItem_jdbc;
    }
    
    public QuoteLineItem createQuoteLineItem(String webQuoteNum, String partNum, String userID) throws TopazException {
    	return this.createQuoteLineItem(webQuoteNum, new Part (partNum), userID);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemFactory#findLineItemByWebQuoteNum(java.lang.String)
     */
    @Override
	public List findLineItemsByWebQuoteNum(String webQuoteNum) throws TopazException {
    	TimeTracer tracer = TimeTracer.newInstance();

        List result = new ArrayList();
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);
        LogContext logger = LogContextFactory.singleton().getLogContext();
        ResultSet rsConfig = null;
        ResultSet rs = null;
        try {
        	//get billing option from cache to fix the issue of disabled cache raising exception
        	
        	getBillingOptionByCode("");
            ProductPortfolioFactory portfolioFactory = ProductPortfolioFactory.singleton();
            Map mapPortfolio = portfolioFactory.getProductPorfolioMap();
            
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_ITEMS, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_ITEMS, params);

            tracer.stmtTraceStart("call S_QT_GET_LINE_ITMS");
            boolean psResult = ps.execute();
            tracer.stmtTraceEnd("call S_QT_GET_LINE_ITMS");

            String quoteType = ps.getString(3).trim();
            if (psResult) {
            	
                // iterator through config list, and put the config into a
                // map with sequence num as the key.
                rsConfig = ps.getResultSet();
                Map configMap = new HashMap();
                while (rsConfig.next()) {
                    QuoteLineItemConfig_jdbc config = new QuoteLineItemConfig_jdbc();
                    config.sProcrVendCode = rsConfig.getString("PROCR_VEND_CODE");
                    config.sProcrVendCodeDesc = rsConfig.getString("PROCRVENDCODEDSCR");
                    config.sProcrBrandCode = rsConfig.getString("PROCR_BRAND_CODE");
                    config.sProcrBrandCodeDesc = rsConfig.getString("PROCRBRANDCODEDSCR");
                    config.sProcrTypeCode = rsConfig.getString("PROCR_TYPE_CODE");
                    config.sProcrTypeCodeDesc = rsConfig.getString("PROCRTYPECODEDSCR");
                    config.dCoreValueUnit = rsConfig.getInt("CORE_VAL_UNIT");
                    config.iExtndDVU = rsConfig.getInt("EXTND_DVU");
                    config.iProcrTypeQty = rsConfig.getInt("PROCR_TYPE_QTY");
                    config.iQuoteLineItemSecNum = rsConfig.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
                    config.sProcrCode = rsConfig.getString("PROCR_CODE");
                    config.webQuoteNum = webQuoteNum;
                    // try to get the corresponding list from map and add the
                    // new config.
                    Object key = new Integer(config.iQuoteLineItemSecNum);
                    List configsOfSpecificQuote = (List) configMap.get(key);
                    if (null == configsOfSpecificQuote) {
                        configsOfSpecificQuote = new ArrayList();
                        configsOfSpecificQuote.add(config);
                        configMap.put(key, configsOfSpecificQuote);
                    } else {
                        configsOfSpecificQuote.add(config);
                    }
                }
                // get the part group info
                Map partGoupMap = new HashMap();
                Map partGroups = new HashMap();
                if (ps.getMoreResults()) {
                    rs = ps.getResultSet();
                    String partNum = null;
                    QuoteLineItem.PartGroup partGroup = null;
                    while (rs.next()) {

                        partNum = rs.getString("PART_NUM").trim();
                        List partGroupList = (List) partGroups.get(partNum);
                        if (partGroupList == null){
                            partGroupList = new ArrayList();
                            partGroups.put(partNum, partGroupList);
                        }

                        final String groupName = rs.getString("SPECL_BID_PART_GRP_NAME");
                        final boolean excluGridDeleFlag = rs.getInt("EXCLU_GRID_DELE_GRP_FLAG") == 1;
                  //      final boolean isSBRequired = rs.getBoolean("SPECL_BID_REQRD_FLG");
                        partGroup = new QuoteLineItem.PartGroup() {
                            @Override
							public String getGroupName() {
                                return groupName;
                            }
                            
                            @Override
							public boolean getExcluGridDeleFlag() {
								// TODO Auto-generated method stub
								return excluGridDeleFlag;
							}


                   //         public boolean getSpeclBidReordFlag() {
                   //             return isSBRequired;
                   //         }

                   //         public String toString() {
                   //             return "group:" + groupName + " Special bid required:" + isSBRequired;
                   //         }
                        };
                        partGroupList.add(partGroup);
                        partGoupMap.put(partNum, partGroup);
                    }


                }
                //new code for DSW 10.1
                Map spBidPartGroupMap = new HashMap();
                Map spBidPartGroups = new HashMap();

                if (ps.getMoreResults()) {
                    rs = ps.getResultSet();
                    String partNum = null;
                    QuoteLineItem.SpBidPartGroup spBidPartGroup = null;
                    while (rs.next()) {

                        partNum = rs.getString("PART_NUM").trim();
                        List spBidPartGroupList = (List) spBidPartGroups.get(partNum);
                        if (spBidPartGroupList == null){
                            spBidPartGroupList = new ArrayList();
                            spBidPartGroups.put(partNum, spBidPartGroupList);
                        }

                        final String spBidPartGroupName = rs.getString("SPECL_BID_PART_GRP_NAME");
//                        final String spBidCountryCode= rs.getString("CNTRY_CODE");
                        final boolean isSBRequired = rs.getBoolean("SPECL_BID_REQRD_FLG");
                        final boolean lvl0ConfFlag = rs.getInt("LVL_ZERO_CONFIGRTN_GRP_FLAG") == 1;
                        spBidPartGroup = new QuoteLineItem.SpBidPartGroup() {
//                            public String getSpBidCountryCode(){
//                                return spBidCountryCode;
//                            }
                            @Override
							public String getSpBidGroupName() {
                                return spBidPartGroupName;
                            }

                            public boolean getLvl0ConfFlag()
                            {
                            	return lvl0ConfFlag;
                            }
                            
                            
//                            public boolean getSpeclBidReordFlag() {
//                                return isSBRequired;
//                            }

                            @Override
							public String toString() {
                                return "group:" + spBidPartGroupName + " Special bid required:" + isSBRequired;
                            }
                        };
                        spBidPartGroupList.add(spBidPartGroup);
                        spBidPartGroupMap.put(partNum, spBidPartGroup);
                    }


                }



                Map quoteItemMap = new HashMap();
                // populate quote line item
                if (ps.getMoreResults()) {
                    rs = ps.getResultSet();
                    while (rs.next()) {
                    	
                    	
                        // key attributes to identify a line item
                        int seqNum = rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
                        String partNum = rs.getString("PART_NUM").trim();
                        
                        boolean isMonthlySoftware = "1".equals(rs.getString("MONTHLY_SW_FLAG"));
                        
                        QuoteLineItem_jdbc lineItem = null;
                        
						if (isMonthlySoftware) {
							lineItem = new MonthlySwLineItem_jdbc(webQuoteNum,partNum);
						} else {
							lineItem = new QuoteLineItem_jdbc(webQuoteNum, createPart(
									partNum, rs));
						}

                        lineItem.iSeqNum = seqNum;
                        lineItem.iDestSeqNum = (rs.getObject("DEST_OBJCT_LINE_ITM_SEQ_NUM")== null ? -1: rs.getInt("DEST_OBJCT_LINE_ITM_SEQ_NUM"));
                        String sPartQty = rs.getString("PART_QTY");
                        lineItem.iPartQty = (sPartQty == null) ? null : Integer.valueOf(sPartQty);
                        lineItem.iManualSortSeqNum = rs.getInt("MANUAL_SORT_SEQ_NUM");
                        lineItem.iQuoteSectnSeqNum = rs.getInt("QUOTE_SECTN_SEQ_NUM");

                        // renewal quote related attributes
                        lineItem.sRenewalQuoteNum = rs.getString("RENWL_QUOTE_NUM") != null ? rs.getString(
                                "RENWL_QUOTE_NUM").trim() : null;
                        lineItem.iRenewalQuoteSeqNum = rs.getInt("RENWL_QUOTE_LINE_ITEM_SEQ_NUM");
                        lineItem.dtRenewalQuoteEndDate = rs.getDate("RENWL_END_DATE");
                        lineItem.dtMaintEndDate = rs.getDate("END_DATE");
                        lineItem.dtMaintStartDate = rs.getDate("START_DATE");

                        // VU config related attributes
                        lineItem.part.setbIsPvuPart(rs.getBoolean("ISDVUPART"));
                        //                        lineItem.sCUId = rs.getString("CU_ID");
                        lineItem.sPVUOverrideQtyIndCode = (rs.getString("VU_CALCTR_QTY_OVRRD_CODE") == null ? null : rs
                                .getString("VU_CALCTR_QTY_OVRRD_CODE").trim());
                        lineItem.iVuConfigrtnNum = rs.getInt("VU_CONFIGRTN_NUM");

                        // proration related attributes
                        lineItem.bProrationFlag = rs.getBoolean("PRORATN_FLAG");
                        lineItem.iAddtnlMaintCvrageQty = rs.getInt("addtnl_maint_cvrage_qty");
                        lineItem.iAddtnlYearCvrageSeqNum = rs.getInt("ADDTNL_YR_CVRAGE_SEQ_NUM");
                        lineItem.bHasAssocdLicPart = rs.getBoolean("ASSOCD_LIC_PART_FLAG");

                        // use-inputted pricing discounts attributes
                        lineItem.dLineDiscPct = rs.getObject("LINE_DISC_PCT") ==null? 0.0 : Double.parseDouble(rs.getObject("LINE_DISC_PCT").toString());
                        
                        lineItem.dOverrideUnitPrc = rs.getObject("OVERRIDE_UNIT_PRICE") == null ? null : new Double(rs
                                .getDouble("OVERRIDE_UNIT_PRICE"));

                        // prices - attributes
                        lineItem.dLocalUnitPrc = rs.getObject("LOCAL_UNIT_PRICE")== null ? null: new Double(rs.getDouble("LOCAL_UNIT_PRICE"));
                        lineItem.dLocalUnitProratedPrc = rs.getObject("PRRATD_LOCAL_UNIT_PRICE")==null ? null: new Double(rs.getDouble("PRRATD_LOCAL_UNIT_PRICE"));
                        lineItem.dLocalUnitProratedDiscPrc = rs.getObject("PRRATD_DISCD_LOCAL_UNIT_PRICE")==null ? null: new Double(rs.getDouble("PRRATD_DISCD_LOCAL_UNIT_PRICE"));
                        lineItem.dLocalExtPrc = rs.getObject("LOCAL_EXTND_PRICE")==null? null : new Double(rs.getDouble("LOCAL_EXTND_PRICE"));
                        lineItem.dLocalExtProratedPrc = rs.getObject("PRRATD_LOCAL_EXTND_PRICE")==null ? null : new Double(rs.getDouble("PRRATD_LOCAL_EXTND_PRICE"));
                        lineItem.dLocalExtProratedDiscPrc = rs.getObject("PRRATD_DISCD_LOCAL_EXTND_PRICE")==null ? null : new Double(rs.getDouble("PRRATD_DISCD_LOCAL_EXTND_PRICE"));

                        // Equity Curve 13.2
                        boolean equityCurveFlag = rs.getObject("EC_FLAG")== null ? false : rs.getInt("EC_FLAG") == 1;
                        if(equityCurveFlag){
	                        boolean equityCurveCtrolFlag = rs.getObject("EC_DISC_CLCLTD_FLG")== null ? false : rs.getInt("EC_DISC_CLCLTD_FLG") == 1;
	                        Double minDiscount = rs.getObject("EC_MIN_PCT")== null ? null: new Double(rs.getDouble("EC_MIN_PCT"));
	                        Double maxDiscount = rs.getObject("EC_MAX_PCT")== null ? null: new Double(rs.getDouble("EC_MAX_PCT"));
	                        String purchaseHistAppliedFlag = rs.getObject("EC_PRE_PURCH_HIST_APPLD_FLAG")== null ? "0" : rs.getString("EC_PRE_PURCH_HIST_APPLD_FLAG").trim();
	                        EquityCurve equityCurve = new EquityCurve(equityCurveFlag,equityCurveCtrolFlag,minDiscount,maxDiscount,purchaseHistAppliedFlag);
	                        lineItem.equityCurve = equityCurve;
                        }
                        
                        // part attributes
                        lineItem.part.setsSwProdBrandCode(rs.getString("SW_PROD_BRAND_CODE") == null ? null : rs.getString(
                        		"SW_PROD_BRAND_CODE").trim());
                        lineItem.part.setsSwProdBrandCodeDesc(rs.getString("BRANDDSCR"));
                        lineItem.part.setsCtrctProgCode(rs.getString("CTRCT_PROG_CODE"));
                        lineItem.part.setsCtrctProgCodeDesc(rs.getString("ctrct_prog_code_dscr"));
                        lineItem.part.setsPartDesc(StringUtils.trim(rs.getString("PART_DSCR_LONG")));
                        lineItem.part.setsPartTypeCode(rs.getString("PARTTYPECODE") == null ? "" : rs.getString(
                                "PARTTYPECODE").trim());
                        lineItem.part.setsRevnStrmCode(StringUtils.trimToEmpty(rs.getString("REVN_STREAM_CODE")));
                        lineItem.part.setsRevnStrmCodeDesc(rs.getString("revn_stream_code_dscr"));
                        lineItem.part.setsWwideProdCode(rs.getString("WWIDE_PROD_CODE"));
                        lineItem.part.setsWwideProdCodeDesc(rs.getString("wwide_prod_code_dscr"));
                        lineItem.part.setsWwideProdSetCode(rs.getString("WWIDE_PROD_SET_CODE"));
                        lineItem.part.setsWwideProdGrpCode(rs.getString("WWIDE_PROD_GRP_CODE"));
                        lineItem.part.setSwSubId(rs.getString("SW_SBSCRPTN_ID"));
                        lineItem.part.setsIbmProgCode(StringUtils.trim(rs.getString("IBM_PROG_CODE")));
                        lineItem.part.setsIbmProgCodeDscr(StringUtils.trim(rs.getString("IBM_PROG_CODE_DSCR")));
                        if (lineItem.isControlled()){
                            lineItem.setSControlledDistributionType(QuoteConstants.CONTROLLED_DISTRIBUTION_RSEL_NOT_AUTHORIZED);
                        }
                        lineItem.part.setControlledCode(rs.getString("SAP_MATL_GRP_5_COND_CODE"));


                        //added for back dating and BP discount
                        lineItem.bBackDatingFlag = rs.getInt("BACK_DTG_FLAG") == 1?true : false;
                        lineItem.dChnlOvrrdDiscPct = rs.getObject("CHNL_OVRRD_DISC_PCT")==null ? null :new Double(rs.getDouble("CHNL_OVRRD_DISC_PCT"));
                        lineItem.dChnlStdDiscPct = new Double(rs.getDouble("CHNL_STD_DISC_PCT"));
                        lineItem.dTotDiscPct = new Double(rs.getDouble("TOT_DISC_PCT"));

                        //added for EOL
                        lineItem.bHasEolPrice = (rs.getInt("HAS_EOL_PRC_FLAG") == 1);
                        //get controlled code desc from app cache rather than from cursor.
                        CodeDescObj descObj = (CodeDescObj) mapPortfolio.get(lineItem.getControlledCode());
                        if(descObj != null) {
                            lineItem.setControlledCodeDesc(StringUtils.trimToEmpty(descObj.getCodeDesc()));
                        } else {
                            lineItem.setControlledCodeDesc(StringUtils.EMPTY);
                        }
                        // associated flags attributes for any parts
                        lineItem.dtEOL = rs.getDate("PART_EOL_DT");
                        lineItem.part.setbIsObsolutePart(rs.getBoolean("PART_OBSLTE_FLAG"));
                        lineItem.part.setbPartRestrct(rs.getBoolean("BPARTRESTRCT"));
                        lineItem.dContributionUnitPts = rs.getDouble("CNTRIBTN_UNIT_PTS");
                        lineItem.dContributionExtPts = rs.getDouble("CNTRIBTN_EXTND_PTS");

                        // added product pack type code
                        lineItem.part.setsProdPackTypeCode(rs.getString("PROD_PACK_TYPE_CODE"));
                        lineItem.bEndDtOvrrdFlg = rs.getBoolean("END_DT_OVRRD_FLG");
                        lineItem.bStartDtOvrrdFlg = rs.getBoolean("START_DT_OVRRD_FLG");

                        // get configs from the map and set
                        Object key = new Integer(lineItem.iSeqNum);
                        lineItem.lineItemConfigs = (List) configMap.get(key);
                        // set part group info
                        lineItem.partGroup = (PartGroup) partGoupMap.get(lineItem.part.getsPartNum());
                        List groupList = (List) partGroups.get(lineItem.part.getsPartNum());
                        if (groupList != null){
                            lineItem.partGroups = groupList;
                        }
                        // set special bid part group info
                        lineItem.spBidPartGroup = (SpBidPartGroup) spBidPartGroupMap.get(lineItem.part.getsPartNum());
                        List spBidPartGroupList = (List) spBidPartGroups.get(lineItem.part.getsPartNum());
                        if (spBidPartGroupList != null){
                            lineItem.spBidPartGroups = spBidPartGroupList;
                        }
                        // added comment, change type code - Apr 12, 2007
                        lineItem.sComment = rs.getString("COMMENT");
                        lineItem.sChgType = rs.getString("CHG_TYPE");

                        // added channel price - May 16, 2007
                        lineItem.dChannelUnitPrice = rs.getObject("CHNL_UNIT_PRICE")==null? null : new Double(rs.getDouble("CHNL_UNIT_PRICE"));
                        lineItem.dChannelExtndPrice = rs.getObject("CHNL_EXTND_PRICE")==null? null : new Double(rs.getDouble("CHNL_EXTND_PRICE"));

                        lineItem.dLclExtndChnlPriceIncldTax = rs.getObject("LCL_EXTND_CHNL_PRICE_INCLD_TAX")==null ? null : new Double(rs.getDouble("LCL_EXTND_CHNL_PRICE_INCLD_TAX"));
                        lineItem.dLocalChnlTaxAmt = rs.getObject("LOCAL_CHNL_TAX_AMT")==null ? null : new Double(rs.getDouble("LOCAL_CHNL_TAX_AMT"));

                        lineItem.part.setsProdTrgtMktCode(rs.getString("PROD_TRGT_MKT_CODE"));

                        // added renewal change code 6/6/2007
                        lineItem.sRenwlChgCode = rs.getString("RENWL_CHG_CODE");

                        lineItem.dtPrevsStartDate = rs.getDate("PREVS_START_DATE");
                        lineItem.dtPrevsEndDate = rs.getDate("PREVS_END_DATE");

                        lineItem.setPartDispAttr(new PartDisplayAttr(lineItem));


                        Object oep = rs.getObject("ovrrd_extnd_price");
                        lineItem.ovrrdExtPrice = (oep == null? null: new Double(rs.getDouble("ovrrd_extnd_price")));


                        Object oif = rs.getObject("offer_incldd_flg");
                        lineItem.offerIncldFlag = (oif == null? null: Boolean.valueOf(rs.getInt("offer_incldd_flg") == 1));

                        lineItem.iManualProratedLclUnitPriceFlag = rs.getInt("MNL_PRRATD_LCL_UNIT_PRIC_FLAG");
                        lineItem.sPartStatus = rs.getString("SAP_SALES_STAT_CODE");

                        lineItem.dLclExtndPriceIncldTax = rs.getObject("LCL_EXTND_PRICE_INCLD_TAX") == null? null : new Double(rs.getDouble("LCL_EXTND_PRICE_INCLD_TAX"));

                        //Cmprss cvreage
                        lineItem.iCmprssCvrageMonth = rs.getObject("CMPRSS_CVRAGE_MTHS")==null? null : new Integer(rs.getInt("CMPRSS_CVRAGE_MTHS"));
                        lineItem.dCmprssCvrageDiscPct = rs.getObject("CMPRSS_CVRAGE_DISC_PCT")==null? null : new Double(rs.getDouble("CMPRSS_CVRAGE_DISC_PCT"));

                        lineItem.legacyBasePriceUsedFlag = rs.getInt("CTRCT_LVL_PRICNG_FLAG") == 1 ? true : false;

                        lineItem.iRelatedLineItmNum = rs.getInt("RELATED_LINE_ITM_NUM");
                        lineItem.part.setsPartType(rs.getString("PART_TYPE"));

                        lineItem.sModByUserID = rs.getString("MOD_BY_USER_NAME");
                        // sap line item needn't to add this logic sine it delegate to quote line item ??
                        // no - per Any, this delegation is wrong, we should fix it!

                        lineItem.saasBidTCV = rs.getObject("SAAS_TOT_CMMTMT_VAL") == null? null : new Double(rs.getDouble("SAAS_TOT_CMMTMT_VAL"));
                        lineItem.iCvrageTerm = rs.getObject("CVRAGE_TERM") == null? null : new Integer(rs.getInt("CVRAGE_TERM"));
                        lineItem.billgFrqncyCode = StringUtils.trim(rs.getString("BILLG_FRQNCY_CODE"));
                        lineItem.billgFrqncyCodeDesc = StringUtils.trim(rs.getString("BILLG_FRQNCY_CODE_DSCR"));
                        lineItem.pricingTierModel = rs.getString("pricng_tier_mdl");

                        String tierQtyMesur = rs.getString("pricng_tier_qty_mesur");
                        if(StringUtils.isNotBlank(tierQtyMesur)){
                        	lineItem.tierQtyMeasre = new Integer(tierQtyMesur);
                        } else {
                        	lineItem.tierQtyMeasre = null;
                        }
                        lineItem.pricngIndCode = rs.getString("PRICNG_IND_CODE");
                        lineItem.provisngHold = PartPriceConstants.SAAS_PART_FLAG_YES.equals(rs.getString("PROVISNG_HOLD_FLAG"));
                        lineItem.migrtnCode = StringUtils.trim(rs.getString("MIGRTN_CODE"));
                        lineItem.configrtnId = StringUtils.trim(rs.getString("CONFIGRTN_ID"));
                        lineItem.refDocLineNum = rs.getObject("REF_DOC_LINE_NUM")==null? null : new Integer(rs.getInt("REF_DOC_LINE_NUM"));
                        lineItem.relatedCotermLineItmNum = rs.getObject("RELATED_COTERM_LINE_ITM_NUM")==null? null : new Integer(rs.getInt("RELATED_COTERM_LINE_ITM_NUM"));
                        lineItem.relatedAlignLineItmNum = rs.getObject("RELATED_ALIGN_LINE_ITM_NUM")==null? null : new Integer(rs.getInt("RELATED_ALIGN_LINE_ITM_NUM"));
                        lineItem.rampUp = PartPriceConstants.RAMP_UP_FLAG_YES.equals(rs.getString("RAMP_UP_FLAG"));
                        lineItem.replacedPart = PartPriceConstants.REPLACED_PART_FLAG_YES.equals(rs.getString("REPL_FLAG"));
                        lineItem.cumCvrageTerm = rs.getObject("CUM_CVRAGE_TERM")==null? null : new Integer(rs.getInt("CUM_CVRAGE_TERM"));
                        lineItem.setTriggerToUHold(PartPriceConstants.TERMS_AND_CONDS_HOLD_FLAG.equals(StringUtils.trim(rs.getString("TERMS_AND_CONDS_HOLD_FLAG"))));
                        lineItem.setQuoteReplicated(rs.getInt("IS_QUOTE_REPLICATED") == 1);
                        lineItem.isOrdered = rs.getInt("IS_QUOTE_ORDERED") == 1;
                        lineItem.saasBpTCV = rs.getObject("SAAS_TOT_CMMTMT_VAL_CHNL") == null? null : new Double(rs.getDouble("SAAS_TOT_CMMTMT_VAL_CHNL"));
                        lineItem.saasRenwl = (rs.getInt("SAAS_RENWL_FLAG") == 1);
                        String isNewService =  rs.getString("IS_NEW_SERVICE_FLAG");
                        if (isNewService==null || "".equals(isNewService.trim())){
                        	lineItem.isNewService = null;
                        }else {
                        	lineItem.isNewService = ("1".equals(isNewService));
                        }
                        
                        lineItem.webMigrtdDocFlag = PartPriceConstants.RAMP_UP_FLAG_YES.equals(rs.getString("WEB_MIGRTD_DOC_FLAG"));

                        //add by sunzw add 2 flags to indicate whether or not the serialnum is mandatory or optional
                        boolean isOptSerialNum = rs.getInt("IS_OPTIONAL_SERIALNUM")==1?true:false;
                        boolean isMandSerialNum = rs.getInt("IS_MANDATORY_SERIALNUM")==1?true:false;
                        lineItem.setDisplayModelAndSerialNum(isOptSerialNum || isMandSerialNum);
                        lineItem.setMandatorySerialNum(rs.getInt("IS_MANDATORY_SERIALNUM")==1?true:false);
                        //end
                        
                      //FCT TO PA Finalization
                        lineItem.orignlSalesOrdRefNum = StringUtils.trim(rs.getString("ORIGNL_SALES_ORD_REF_NUM"));
                        lineItem.orignlConfigrtnId = StringUtils.trim(rs.getString("ORIGNL_CONFIGRTN_ID"));
                        lineItem.earlyRenewalCompDate = rs.getDate("ADD_ON_RENWL_ELGBLTY_DATE");
                        lineItem.hasProvisngHold = rs.getInt("HAS_PROVISNG_HOLD") == 1;


                        //export restricted notification
                        lineItem.regulationCode  = rs.getString("ENCRYPTN_IND");

                        // 10.4 & 10.6
                        lineItem.extensionEligibilityDate = rs.getDate("EXT_RENWL_ELGBLTY_DATE");
                        
                        //14.1 SDD
                        lineItem.divestedPart = rs.getInt("DIVESTED_FLAG") == 1 ? true :false;

                        lineItem.tierdPrice = rs.getObject("TIERD_PRC") == null ? null : new Double(rs.getDouble("TIERD_PRC"));
                        
                        lineItem.dLocalTaxAmt = rs.getObject("LOCAL_TAX_AMT") == null ? null : new Double(rs.getDouble("LOCAL_TAX_AMT"));
                        
                        int amendedTouFlag = rs.getInt("TOU_AMENDD_FLAG");
                        if(amendedTouFlag == 1){
                        	lineItem.setAmendedTouFlag(true);
                        }else if(amendedTouFlag == 2){
                        	lineItem.setAmendedTouFlag(false);
                        }
                        int amendedTouFlagB = rs.getInt("TOU_BRAND_AMENDD_FLAG");
                        if(amendedTouFlagB == 1){
                        	lineItem.setAmendedTouFlagB(true);
                        }else if(amendedTouFlagB == 2){
                        	lineItem.setAmendedTouFlagB(false);
                        }
                        lineItem.setTouName(rs.getString("TOU_NAME"));
                        lineItem.setTouURL(rs.getString("TOU_URL"));
                        lineItem.setPartTypeDsc(rs.getString("partTypeDsc"));
                        //ANDY release 14.2 retrieve HdrAgrmentAmdFlag value from SP result 
                        int hdrAgrmentAmdFlag = rs.getInt("HDR_AGREEMENT_AMENDED");
                        if(hdrAgrmentAmdFlag == 1){
                        	lineItem.setHdrAgrmentAmdFlag(true);
                        }else if(hdrAgrmentAmdFlag == 2){
                        	lineItem.setHdrAgrmentAmdFlag(false);
                        }
                        // 14.4 SCW add on /trade up
                        lineItem.addReasonCode = StringUtils.trim(rs.getString("ADD_REAS_CODE"));
                        lineItem.replacedReasonCode = StringUtils.trim(rs.getString("REPLD_REAS_CODE"));
                        lineItem.newConfigFlag = StringUtils.trim(rs.getString("NEW_CONFIGRTN_FLAG"));
                        lineItem.originatingItemNum = rs.getObject("ORIGNL_QUOTE_LINE_ITEM_SEQ_NUM") == null? null : new Integer(rs.getInt("ORIGNL_QUOTE_LINE_ITEM_SEQ_NUM"));
                        //0 means has price, 1 means no.
                        lineItem.partHasPrice = rs.getInt("HAS_PRICE_FLAG") == 1 ? true :false;
                        
                        lineItem.monthlySoftwarePart = isMonthlySoftware;
                        
                        result.add(lineItem);
                        quoteItemMap.put(lineItem.part.getsPartNum() + lineItem.iSeqNum, lineItem);
                    }
                }

                //added chargeable unit info
                if (ps.getMoreResults()) {

                    Map cuInfoMap = new HashMap();
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        String id = rs.getString("part_num").trim() + rs.getInt("quote_line_item_seq_num");
                        if (cuInfoMap.get(id) == null){
                            cuInfoMap.put(id, new ArrayList());
                        }
                        ChargeableUnit cu = new ChargeableUnit();
                        cu.setId(StringUtils.trim(rs.getString("CU_ID")));
                        cu.setDesc(StringUtils.trim(rs.getString("CU_CODE_DSCR")));
                        cu.setQuantity(rs.getInt("CU_QTY"));
                        logger.debug(this, "id: " + cu.getId() + ", desc: " + cu.getDesc() + ", qty: " + cu.getQuantity());
                        ((List)cuInfoMap.get(id)).add(cu);
                    }

                    for(Iterator iter = result.iterator(); iter.hasNext();){
                        QuoteLineItem_jdbc item = (QuoteLineItem_jdbc)iter.next();
                        List cuList = (List)cuInfoMap.get(item.getPartNum() + item.getSeqNum());
                        int arraySize = cuList == null ? 0 : cuList.size();
                        ChargeableUnit[] cuArray= new ChargeableUnit[arraySize];
                        for(int i = 0; i < arraySize; i ++){
                            cuArray[i] = (ChargeableUnit)cuList.get(i);
                        }
                        item.setChargeableUnits(cuArray);
                    }
                }

                //cursor maxPrcEndDate to get the max PRICE_END_DATE for each line item
                if (ps.getMoreResults()) {
                    rs = ps.getResultSet();
                    while (rs.next()) {
                    	int seqNum = rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
        	            String partNum = rs.getString("PART_NUM").trim();
        	            QuoteLineItem_jdbc lineItem = (QuoteLineItem_jdbc) quoteItemMap.get(partNum + seqNum);
        	            if (null != lineItem) {
        	            	lineItem.dtPartPrcEndDate = rs.getDate("PRICE_END_DATE");
        	            }
                    }
                }


                // below two cursor are conditionally opened
                while(ps.getMoreResults()){
                    rs = ps.getResultSet();

                    //we can't access resultset again when all rows are consumed
                    if(rs.next()){
                        String cursorName = rs.getString(1);
                        if ("RQLI".equalsIgnoreCase(cursorName)){
                            processRenewalQuoteInfo(rs,quoteItemMap);
                        }else if("SAP-QLI-DATA".equalsIgnoreCase(cursorName)){
                            processSapSeqNum(rs,quoteItemMap,quoteType);
                        }else if ("ORDER_STATUS".equalsIgnoreCase(cursorName)){
                            processOrderStatus(rs, quoteItemMap,quoteType);
                        }else if("RQLIDATE".equalsIgnoreCase(cursorName)){
                            processRenewalQliDate(rs, quoteItemMap);
                        }else if ("CONTROLLED_DISTRIBUTION_DETAILS".equalsIgnoreCase(cursorName)){
                            processControlDistribution(rs, quoteItemMap);
                        }else if ("ORI_ADDTNL_MAINT_CVRAGE_QTY".equalsIgnoreCase(cursorName)){
                            processOriAddtnlYear(rs, quoteItemMap);
                        }else if ("QLI_SAP_DOC_USER_STAT".equals(cursorName)){
                        	processQliSapDocUserStat(rs, quoteItemMap, quoteType);
                        }else if ("PRIOR_YEAR_SS_PRICE".equalsIgnoreCase(cursorName)){
                            processPriorYearPrice(rs, quoteItemMap);
                        }else if ("APPLIANCE_ATTRIBUTE".equalsIgnoreCase(cursorName)){
                            processApplianceItem(rs, quoteItemMap);
                        }
                        else if ("AUTO_RENEWAL_MODEL".equals(cursorName)){
                        	processAutoRenwlModel(rs,quoteItemMap);
                        }
                        else if ("YTY_GROWTH_DELEGATION".equals(cursorName)){
                        	processGrowthDelegation(rs,quoteItemMap);
                        }
                        else if ("QLI_BLG_OPTION".equalsIgnoreCase(cursorName)){
                            processQliBillingOption(rs, quoteItemMap);
                        }
                        else if ("DEPLOYMENT_ATTRIBUTE".equalsIgnoreCase(cursorName)){
                        	processDeploymentModel(rs, quoteItemMap);
                        } 
                        else if ("MONTHLY_SW_QLI".equalsIgnoreCase(cursorName)){
                        	processMonthlySwLineItem(rs,quoteItemMap);
                        } else if ("RENEWAL_DEPLOYMT_ATTRIBUTE".equalsIgnoreCase(cursorName)) {
                        	// added for dsw 14.2 task# 627593 deploymentID display renewal part
                        	processDeploymtID(rs,quoteItemMap);
                        }else if ("SAAS_QLI".equalsIgnoreCase(cursorName)){
                        	processSaasLineItem(rs,quoteItemMap);
                        }else if ("APPLIANCE_QLI".equalsIgnoreCase(cursorName)){
                        	processApplianceLineItem(rs,quoteItemMap);
	                    }
                    }
                }


            }
            applyPVUInfoToSubLineItems(result);
            processRelatedSaasLineItem(result);

        } catch (Exception e) {
            logger.error("Failed to get the quote line item configs from the database!", e);
            throw new TopazException(e);
        } finally{
        	try {
				if (null != rsConfig && !rsConfig.isClosed())
				{
					rsConfig.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
			try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
        	tracer.dump();
        }

        return result;
    }

	private Part createPart(String partNum, ResultSet rs)
			throws SQLException {
			return new Part(partNum);
	}
    /*
     * Derive all parts info just including parts num and parts sequence num,while no necessary get validatied
     * (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemFactory#findPartsWithoutValidation(java.lang.String)
     */
    @Override
	public List findPartsWithoutValidation(String webQuoteNum) throws TopazException {

        List result = new ArrayList();
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);
        LogContext logger = LogContextFactory.singleton().getLogContext();
        ResultSet rs = null;
        try {

            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_PARTS_WITHOUT_VALDT, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_PARTS_WITHOUT_VALDT, params);

            ps.execute();

            int poGenStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != poGenStatus) {
                logger.error(this, "the return code of calling " + sqlQuery + " is: " + poGenStatus);
                throw new QuoteException("SP call return code : " + poGenStatus);
            }

            rs = ps.getResultSet();

            while (rs.next()) {
                String partNum = rs.getString("PART_NUM").trim();
                int lineItemSeqNum = rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
                QuoteLineItem_jdbc lineItem = new QuoteLineItem_jdbc(webQuoteNum, new Part(partNum));
                lineItem.iSeqNum = lineItemSeqNum;
                result.add(lineItem);
            }

        } catch (Exception e) {
            logger.error("Failed to get the quote line item configs from the database!", e);
            throw new TopazException(e);
        } finally{
        	try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
        }

        return result;
    }
    
    private void processRelatedSaasLineItem(List lineItems )throws TopazException{
    	if (lineItems == null) return;
    	
    	for (int i = 0 ;i < lineItems.size(); i++){
    		QuoteLineItem_jdbc qli =(QuoteLineItem_jdbc) lineItems.get(i);
    		
    		if (!qli.isSaasPart()) {
    			continue;
    		}
    		
    		if(!qli.isSaasSetUpPart() && !qli.isSaasSubscrptnPart()){
    			continue;
    		}
    		
    		for (int j = 0 ; j < lineItems.size(); j++){
    			QuoteLineItem lineItem = (QuoteLineItem)lineItems.get(j);
    			//subscription part related 
    			
    			//set up part related 
    			if (qli.isSaasSetUpPart() && lineItem.isSaasSubsumedSubscrptnPart()
                        && qli.getIRelatedLineItmNum() == lineItem.getDestSeqNum()) {
        			qli.setUpRelatedSubsumedPart = lineItem;
        			break;
        		}
    			
    		}
    		
    	}
    	
    }
    /**
     * @param rs
     * @param quoteItemMap
     * @param quoteType
     */
    private void processSapSeqNum(ResultSet rs, Map lineItemMap, String quoteType) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
	        do {
	            String partNum = StringUtils.trim(rs.getString("part_num"));
	            String sapSeqNum = StringUtils.trim(rs.getString("line_item_seq_num"));


	            QuoteLineItem_jdbc lineItem = null;
	            String seqNum = StringUtils.trim(rs.getString("WEB_QUOTE_LINE_ITEM_SEQ_NUM"));

	            if(StringUtils.isBlank(seqNum)){
	                // this is a data issue, SAP should feed back the correct seq number ,
	                // please refer to ebiz ticket DEVS-7LKK9K: Approvers Can not view Tabs in SB's
	                String errMsg = "The SODS2.QUOTE_LINE_ITEM.WEB_QUOTE_LINE_ITEM_SEQ_NUM is NULL for part ="+partNum;
                    logger.error(this,errMsg);
                    throw new TopazException(errMsg);
                }
	            if (QuoteConstants.QUOTE_TYPE_SALES.equalsIgnoreCase(quoteType)){
	                // for sales quote, SODS2.QUOTE_LINE_ITEM.WEB_QUOTE_LINE_ITEM_SEQ_NUM equals  EBIZ1.WEB_QUOTE_LINE_ITEM.DEST_OBJCT_LINE_ITM_SEQ_NUM
	                lineItem = findLineItemByDestSeqNum(Integer.valueOf(seqNum).intValue(),lineItemMap);
	            }
	            else{
	                // for renewal quote, SODS2.QUOTE_LINE_ITEM.WEB_QUOTE_LINE_ITEM_SEQ_NUM equals  EBIZ1.WEB_QUOTE_LINE_ITEM.QUOTE_LINE_ITEM_SEQ_NUM
	                lineItem = (QuoteLineItem_jdbc) lineItemMap.get(partNum + seqNum);

	            }
	            if(null != lineItem){
	                lineItem.iSapSeqNum = Integer.valueOf(sapSeqNum).intValue();
	            }
	        } while(rs.next());
        }catch(Throwable e){
            logger.error("Failed to get Sap Seq number",e);
            throw new TopazException(e);
        }

    }
    private QuoteLineItem_jdbc findLineItemByDestSeqNum(int destNum,Map lineItemMap){
        Iterator iter = lineItemMap.values().iterator();
        while(iter.hasNext()){
            QuoteLineItem_jdbc lineItem = (QuoteLineItem_jdbc) iter.next();
            if(lineItem.getDestSeqNum() == destNum){
                return lineItem;
            }
        }
        return null;
    }
    /**
     *
     */
    private void processRenewalQuoteInfo(ResultSet rs,Map lineItemMap) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
	        do {
	            int seqNum = rs.getInt("LINE_ITEM_SEQ_NUM");
	            String partNum = rs.getString("PART_NUM").trim();
	            QuoteLineItem_jdbc lineItem = (QuoteLineItem_jdbc) lineItemMap.get(partNum + seqNum);
	            if (null != lineItem) {
	                lineItem.iOrderQty = (int) rs.getDouble("ORDERQTY");
	                lineItem.iOpenQty = (int) rs.getDouble("OPENQTY");
	                lineItem.dtOrigStDate = rs.getDate("ORIGSTDATE");
	                lineItem.dtOrigEndDate = rs.getDate("ORIGENDDATE");
	                lineItem.dOrigUnitPrice = rs.getDouble("ORIGUNITPRICE");
	                lineItem.dOrigExtPrice = rs.getDouble("origExtndPrice");
	                lineItem.bSalesQuoteRefFlag = (rs.getInt("slsQtRefFlag") == 1);
	                lineItem.isSetLineToRsvpSrpFlag  = (rs.getInt("RESET_TO_RSVP_FLAG") == 1);
	                lineItem.renewalPricingMethod  = null!=rs.getObject("PRICNG_STRTGY_CODE") ? rs.getString("PRICNG_STRTGY_CODE").trim() : null;
	            }
	        } while(rs.next());
        }catch(Exception e){
            logger.error("Failed to get renewal quote infomation",e);
            throw new TopazException(e);
        }
    }

    private void processRenewalQliDate(ResultSet rs,Map lineItemMap) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
	        do {
	            int seqNum = rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
	            String partNum = rs.getString("PART_NUM").trim();
	            QuoteLineItem_jdbc lineItem = (QuoteLineItem_jdbc) lineItemMap.get(partNum + seqNum);
	            if (null != lineItem) {
	                lineItem.dtOrigStDate = rs.getDate("ORIGSTDATE");
	                lineItem.dtOrigEndDate = rs.getDate("ORIGENDDATE");
	            }
	        } while(rs.next());
        }catch(Exception e){
            logger.error("Failed to get renewal quote infomation",e);
            throw new TopazException(e);
        }
    }


    private void processControlDistribution(ResultSet rs,Map lineItemMap) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        List resultList = new ArrayList();
        try{
	        do {
	            String grp5Code = rs.getString("SAP_MATL_GRP_5_COND_CODE");
	            int associatedFlag = rs.getInt("ASSOCIATED_FLAG");
	            int govauthFlag = rs.getInt("GOVAUTH");
	            Map tempMap = new HashMap();
	            tempMap.put("SAP_MATL_GRP_5_COND_CODE",grp5Code);
	            tempMap.put("ASSOCIATED_FLAG",Integer.toString(associatedFlag));
	            tempMap.put("GOVAUTH",Integer.toString(govauthFlag));
	            resultList.add(tempMap);
	        } while(rs.next());
        }catch(Exception e){
            logger.error("Failed to get crsrcontrolledDistributionDetails",e);
            throw new TopazException(e);
        }
        Collection lineItemValues =  lineItemMap.values();
        for (Iterator itr = lineItemValues.iterator(); itr.hasNext();) {
            QuoteLineItem_jdbc lineItem = (QuoteLineItem_jdbc) itr.next();
            boolean authorizedFlag = false;
            boolean associatedFlag = false;
            if (!(lineItem.isControlled())){
                lineItem.setSControlledDistributionType(QuoteConstants.CONTROLLED_DISTRIBUTION_NONE);
                continue;
            }

            for (int j = 0; j < resultList.size(); j++) {
                Map tempMap = (Map) resultList.get(j);
                if (lineItem.getControlledCode().equalsIgnoreCase(tempMap.get("SAP_MATL_GRP_5_COND_CODE").toString())
                        && "1".equals(tempMap.get("GOVAUTH"))) {
                    authorizedFlag = true;
                    lineItem.setSControlledDistributionType(QuoteConstants.CONTROLLED_DISTRIBUTION_NONE);
                    if ("1".equals(tempMap.get("ASSOCIATED_FLAG"))) {
                        associatedFlag = true;
                        lineItem.setSControlledDistributionType(QuoteConstants.CONTROLLED_DISTRIBUTION_NONE);
                        break;
                    }
                }
            }

            if(!authorizedFlag){
                lineItem.setSControlledDistributionType(QuoteConstants.CONTROLLED_DISTRIBUTION_RSEL_NOT_AUTHORIZED);
            }
            if(authorizedFlag && (!associatedFlag)){
                lineItem.setSControlledDistributionType(QuoteConstants.CONTROLLED_DISTRIBUTION_RSEL_DIST_NOT_ASSOCIATED);
            }
        }
    }

    /**
     *
     */
    private void processOrderStatus(ResultSet rs,Map lineItemMap, String quoteType) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
	        do {
	            int seqNum = rs.getInt("LINE_ITEM_SEQ_NUM");
	            String partNum = rs.getString("PART_NUM").trim();
	            logger.debug(this,"Processing partNum=(" + partNum + ")seqNum=(" +seqNum +")for order status.");
	            QuoteLineItem_jdbc lineItem ;
	            if (QuoteConstants.QUOTE_TYPE_SALES.equalsIgnoreCase(quoteType)){
	                // for sales quote, SODS2.QUOTE_LINE_ITEM.WEB_QUOTE_LINE_ITEM_SEQ_NUM equals  EBIZ1.WEB_QUOTE_LINE_ITEM.DEST_OBJCT_LINE_ITM_SEQ_NUM
	                lineItem = findLineItemByDestSeqNum(seqNum,lineItemMap);
	            }
	            else{
	                // for renewal quote, SODS2.QUOTE_LINE_ITEM.WEB_QUOTE_LINE_ITEM_SEQ_NUM equals  EBIZ1.WEB_QUOTE_LINE_ITEM.QUOTE_LINE_ITEM_SEQ_NUM
	                lineItem = (QuoteLineItem_jdbc) lineItemMap.get(partNum + seqNum);

	            }
	            if (null != lineItem) {

	                lineItem.setOrderStatusCode(rs.getString("SAP_RJCTN_CODE"));
	                logger.debug(this,"Order status code at line item leve is: " + lineItem.getOrderStatusCode());
	            }else{
	                logger.debug(this, "Can not find line item for order status.");
	            }
	        } while(rs.next());
        }catch(Exception e){
            logger.error("Failed to get order stautus.",e);
            throw new TopazException(e);
        }
    }

    private void processQliSapDocUserStat(ResultSet rs,Map lineItemMap, String quoteType) throws TopazException{
    	try{
	        do {
	            int seqNum = rs.getInt("LINE_ITEM_SEQ_NUM");
	            String partNum = rs.getString("PART_NUM").trim();
	            QuoteLineItem_jdbc lineItem ;
	            if (QuoteConstants.QUOTE_TYPE_SALES.equalsIgnoreCase(quoteType)){
	                // for sales quote, SODS2.QUOTE_LINE_ITEM.WEB_QUOTE_LINE_ITEM_SEQ_NUM equals  EBIZ1.WEB_QUOTE_LINE_ITEM.DEST_OBJCT_LINE_ITM_SEQ_NUM
	                lineItem = findLineItemByDestSeqNum(seqNum,lineItemMap);
	            }
	            else{
	                // for renewal quote, SODS2.QUOTE_LINE_ITEM.WEB_QUOTE_LINE_ITEM_SEQ_NUM equals  EBIZ1.WEB_QUOTE_LINE_ITEM.QUOTE_LINE_ITEM_SEQ_NUM
	                lineItem = (QuoteLineItem_jdbc) lineItemMap.get(partNum + seqNum);
	            }

	            if (null != lineItem) {
	                lineItem.addSapDocUserStat(rs.getString("CODE"),rs.getString("CODE_DSCR"));
	            }
	        } while(rs.next());
        }catch(Exception e){
            logger.error("Failed to get sap doc user status.", e);
            throw new TopazException(e);
        }
    }

    private void processOriAddtnlYear(ResultSet rs,Map lineItemMap) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
	        do {
	            int seqNum = rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
	            String partNum = rs.getString("PART_NUM").trim();
	            QuoteLineItem_jdbc lineItem = (QuoteLineItem_jdbc) lineItemMap.get(partNum + seqNum);
	            if (null != lineItem) {
	                lineItem.iOriAddtnlMaintCvrageQty = rs.getInt("ORIADDTNL_MAINT_CVRAGE_QTY");
	            }
	        } while(rs.next());
        }catch(Exception e){
            logger.error("Failed to get OriAddtnlYear infomation",e);
            throw new TopazException(e);
        }
    }
    private void processPriorYearPrice(ResultSet rs,Map lineItemMap) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
	        do {
	            int seqNum = rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
	            String partNum = rs.getString("PART_NUM").trim();
	            QuoteLineItem_jdbc lineItem = (QuoteLineItem_jdbc) lineItemMap.get(partNum + seqNum);
				final String priorYrLocalUnitPrice12Mnths = rs.getString("LOCAL_UNIT_PRICE_LPP");
				final String priorYrCurrncyCode = rs.getString("CURRNCY_CODE_LPP");
				final boolean isEvolved = "Y".equalsIgnoreCase(rs.getString("EVOLVED_FLAG"));
				lineItem.renewalPricingMethod = rs.getString("PRICING_STRTGY_CODE");
				
				Double rsvpFromPYP = rs.getDouble("SVP_PRICE");
				Double rsvpFromSAP = GDPartsUtil.getRenewalRsvpPrice(lineItem.getLocalUnitPrc(), lineItem);
				if(rsvpFromSAP != null){
					lineItem.renewalRsvpPrice = rsvpFromSAP;
				} else {
					lineItem.renewalRsvpPrice = rsvpFromPYP;
				}
				
				
				
	            if (null != lineItem) {
	                lineItem.setPriorYearSSPrice(new PriorYearSSPrice () {
		        		@Override
						public String getPriorYrLocalUnitPrice12Mnths(){
		        			return priorYrLocalUnitPrice12Mnths;
		        		}
		                @Override
						public String getPriorYrCurrncyCode(){
		                	return priorYrCurrncyCode;
		                }
		                @Override
						public boolean isEvolved(){
		                	return isEvolved;
		                }
		                @Override
						public boolean isChannel(){
							return false;
		                }
		                @Override
						public boolean isShowPriorYearPrice(){
							return true;

		                }
		            });
	            }
	        } while(rs.next());
        }catch(Exception e){
            logger.error("Failed to get OriAddtnlYear infomation",e);
            throw new TopazException(e);
        }
    }

    private void applyPVUInfoToSubLineItems(List lineItems) throws TopazException{
        List masterLineItems = CommonServiceUtil.buildMasterLineItemsWithAddtnlMaint(lineItems);
        for(int i=0;i<masterLineItems.size();i++){
            QuoteLineItem_jdbc masterLineItem = (QuoteLineItem_jdbc) masterLineItems.get(i);
            if(masterLineItem.isPvuPart()){
	            Iterator iter = masterLineItem.getAddtnlYearCvrageLineItems().iterator();
	            while(iter.hasNext()){
	                QuoteLineItem_jdbc subLineItem = (QuoteLineItem_jdbc) iter.next();
	                subLineItem.iPartQty = masterLineItem.iPartQty;
	                subLineItem.lineItemConfigs = masterLineItem.lineItemConfigs;
	                subLineItem.sPVUOverrideQtyIndCode = masterLineItem.sPVUOverrideQtyIndCode;
	                subLineItem.iVuConfigrtnNum = masterLineItem.iVuConfigrtnNum;
	            }
            }
            masterLineItem.addtnlYearCvrageLineItems.clear();
        }
    }
    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemFactory#updateLineItemQty(java.lang.String,
     *      int, int, java.lang.String, int)
     */
    @Override
	public void updateLineItemQty(String webQuoteNum, int seqNum, float partQty, String overrdCode, int configNum, String userID)
            throws TopazException {

        //convert page index to start index

		HashMap<String, Object> parms = new HashMap<String, Object>();
        parms.put("piWebQuoteNum", webQuoteNum);
        parms.put("piQuoteLineItemSeqNum", "" + seqNum);
        parms.put("piPartQty", "" + partQty);
        parms.put("piVUCalctrQtyOverrdCode", overrdCode);
        parms.put("piVUConfigrtnNum", "" + configNum);
        parms.put("piUserID", userID);
        LogContext logContext = LogContextFactory.singleton().getLogContext();

        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_LI_PVU, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
            queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_LI_PVU, parms);

            ps.execute();
            int resultCode = ps.getInt(1);
            if (resultCode == CommonDBConstants.DB2_SP_RETURN_NOT_VALUE_UNIT) {
                // if not calculate by value unit,then do nothing
                return;
            }

            if (resultCode == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
                throw new NoDataException();
            } else if (resultCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
                throw new SPException(LogHelper.logSPCall(sqlQuery, parms), resultCode);
            }

        } catch (SQLException e) {
            logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_U_QT_LI_PVU, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemFactory#findMasterItemsByWebQuoteNum(java.lang.String)
     */
    @Override
	public List findMasterLineItemsByWebQuoteNum(String WebQuoteNum) throws TopazException {
        return CommonServiceUtil.buildMasterLineItemsWithAddtnlMaint(findLineItemsByWebQuoteNum(WebQuoteNum));
    }
    //refactor, update the param to List lineItems
    @Override
	public List findMasterLineItems(List lineItems) throws TopazException {
        return CommonServiceUtil.buildMasterLineItemsWithAddtnlMaint(lineItems);
    }

    @Override
	public List findSaaSLineItems(List lineItems) throws TopazException {
        return CommonServiceUtil.getSaaSLineItemList(lineItems);
    }

    @Override
	public void getEolHistPrice(List eolPartList, String countryCode, String currencyCode, String priceLevel) throws TopazException{
        Map eolPartMap = new HashMap();
        for(Iterator it = eolPartList.iterator(); it.hasNext(); ){
        	QuoteLineItem item = (QuoteLineItem)it.next();

        	List partList = (List)eolPartMap.get(item.getPartNum());
        	if(partList == null){
        		partList = new ArrayList();
        		eolPartMap.put(item.getPartNum(), partList);
        	}

        	partList.add(item);
        }

        StringBuffer partNumStr = new StringBuffer();
        for(Iterator it = eolPartMap.keySet().iterator(); it.hasNext(); ){
        	partNumStr.append(it.next()).append(",");
        }

        partNumStr.deleteCharAt(partNumStr.length() - 1);

    	HashMap parms = new HashMap();
        parms.put("piPartNumList", partNumStr.toString());
        parms.put("piCntryCode", countryCode);
        parms.put("piCurrncyCode", currencyCode);
        parms.put("piPriceLevel", priceLevel == null? "" : priceLevel);
        ResultSet set = null;
        try {
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_S_QT_GET_EOL_PRC, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            queryCtx.completeStatement(ps, CommonDBConstants.DB2_S_QT_GET_EOL_PRC, parms);

            ps.execute();
            int retStatus = ps.getInt(1);
            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus = "+retStatus+", sql: " + sqlQuery);
            }

            set = ps.getResultSet();
            while(set.next()){
            	String partNum = set.getString("PART_NUM").trim();

            	BigDecimal decPrice = (BigDecimal)set.getObject("PRICE");

            	Double price = null;
            	if(decPrice != null){
            		price = new Double(decPrice.doubleValue());
            	}

            	List partList = (List)eolPartMap.remove(partNum);
            	boolean hasPrice = price == null? false : true;
            	setLineItemPrice(price, partList, hasPrice);
            }

            //for the parts remained in the map, clear price and set has eol price flag to false
            for(Iterator it = eolPartMap.values().iterator(); it.hasNext(); ){
            	List partList = (List)it.next();
            	setLineItemPrice(null, partList, false);
            }

        } catch (SQLException e) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_S_QT_GET_EOL_PRC, e);
        }
        finally{
        	try {
				if (null != set && !set.isClosed())
				{
					set.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
        }
    }

    private void setLineItemPrice(Double price, List partList, boolean hasEOLPrice) throws TopazException{
    	for(Iterator it = partList.iterator(); it.hasNext(); ){
    		QuoteLineItem item = (QuoteLineItem)it.next();

    		if(hasEOLPrice){
        		CommonServiceUtil.setEntitledPriceForEOLPart(item, price, price, 0);
        		logger.debug(this, "EOL price for obsolete part["
        				            + item.getPartDesc() + "_" + item.getDestSeqNum() + "] is " + price);
//        		int i =0;
    		} else if(item.getManualProratedLclUnitPriceFlag() == 1){
        		logger.debug(this, "can't find EOL price for obsolete part["
			            + item.getPartDesc() + "_" + item.getDestSeqNum() + "]");
        		//if user has overriden price previously, then update extended price(unit * quantity)
        		CommonServiceUtil.setEntitledPriceForEOLPart(item, item.getLocalUnitPrc(), item.getLocalUnitProratedPrc(), 1);
    		}
    	}
    }

    /*
     * Delete invalid parts from DB by web quote num
     * (non-Javadoc)
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemFactory#delInvalidLineItemsByWebQuoteNum(java.lang.String, java.util.List)
     */
    @Override
	public void delInvalidLineItemsByWebQuoteNum(String webQuoteNum, List invalidPartList) throws TopazException {
        for(Iterator iter = invalidPartList.iterator(); iter.hasNext();) {
            QuoteLineItem_jdbc lineItem = new QuoteLineItem_jdbc(webQuoteNum, new Part());
            lineItem.iSeqNum = Integer.parseInt(iter.next().toString());
            lineItem.isDeleted(true);
        }

    }



    private void processApplianceItem(ResultSet rs,Map lineItemMap) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
	        do {
	            int seqNum = rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
	            String partNum = rs.getString("PART_NUM").trim();
	            QuoteLineItem_jdbc lineItem = (QuoteLineItem_jdbc) lineItemMap.get(partNum + seqNum);
	            if (null != lineItem) {
	            	lineItem.applncPocInd = rs.getString("APPLNC_POC_IND")!=null?rs.getString("APPLNC_POC_IND").trim():"";
	            	lineItem.applncPriorPoc = rs.getString("APPLNC_PRIOR_POC")!=null?rs.getString("APPLNC_PRIOR_POC").trim():"";
	            	lineItem.machineType = rs.getString("HW_MACH_TYPE")!=null?rs.getString("HW_MACH_TYPE").trim():"";
	            	lineItem.model = rs.getString("HW_MACH_MODEL")!=null?rs.getString("HW_MACH_MODEL").trim():"";
	        	 	lineItem.serialNumber = rs.getString("HW_MACH_SERIAL_NUM")!=null?rs.getString("HW_MACH_SERIAL_NUM").trim():"";
	        	 	lineItem.custCmmttdArrivlDate = rs.getDate("CUST_CMMTTD_ARRIVL_DATE");
	        	 	lineItem.ccDscr = rs.getString("CC_DSCR")!=null?rs.getString("CC_DSCR").trim():"";
	        	 	lineItem.custReqArrvDate = rs.getDate("CUST_REQSTD_ARRIVL_DATE");
	        	 	lineItem.applncSendMFGFlg = rs.getString("APPLNC_SEND_TO_MFG_FLAG")!=null?rs.getString("APPLNC_SEND_TO_MFG_FLAG").equals("1"):false;
	        	 	lineItem.nonIBMModel = rs.getString("NON_IBM_HW_MACH_MODEL")!=null?rs.getString("NON_IBM_HW_MACH_MODEL").trim():"";
	        	 	lineItem.nonIBMSerialNumber = rs.getString("NON_IBM_HW_MACH_SERIAL_NUM")!=null?rs.getString("NON_IBM_HW_MACH_SERIAL_NUM").trim():"";
	            }
	        } while(rs.next());
        }catch(Exception e){
            logger.error("Failed to get appliance part infomation",e);
            throw new TopazException(e);
        }
    }

    private void processAutoRenwlModel(ResultSet rs,Map lineItemMap)throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
	        do {
	            int seqNum = rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
	            String partNum = rs.getString("PART_NUM").trim();
	            QuoteLineItem_jdbc lineItem = (QuoteLineItem_jdbc) lineItemMap.get(partNum + seqNum);
                if (null != lineItem) {
                    lineItem.renwlMdlCode = rs.getString("RENWL_MDL_CODE") != null ? rs.getString("RENWL_MDL_CODE").trim() : "";
                    lineItem.renwlMdlCodeLevel = rs.getString("RENWL_MDL_CODE_LVL") != null ? rs.getString("RENWL_MDL_CODE_LVL")
                            .trim() : "";
                    lineItem.saasRewlModelCode = rs.getString("SAAS_RENWL_MDL_CODE") != null ? rs
                            .getString("SAAS_RENWL_MDL_CODE").trim() : "";
                    lineItem.fixedRewlModFalg = "1".equals(rs.getString("FIXED_RENWL_MDL_FLAG"));
                    lineItem.supportRenwlMdl = true;
                    lineItem.defultRenwlMdlCode = rs.getString("DEFAULT_RENWL_MDL_CODE") != null ? rs.getString(
                            "DEFAULT_RENWL_MDL_CODE").trim() : "";
                    lineItem.isRewlModelCodeFromCa = "1".equals(rs.getString("RENWL_MDL_CODE_SOURCE_FLAG"));
                }
	        } while(rs.next());
        }catch(Exception e){
            logger.error("Failed to get auto renewal model infomation",e);
            throw new TopazException(e);
        }
    }

    private void processGrowthDelegation(ResultSet rs,Map lineItemMap) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
        	do {
        		String quoteNum = rs.getString("QUOTE_NUM");
	            int seqNum = rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
	            String partNum = rs.getString("PART_NUM").trim();
	            String ytySourceCode = rs.getString("YTY_SRC_CODE");
	            if(ytySourceCode != null){
	            	ytySourceCode = ytySourceCode.trim();
	            }
	            Double YTYGrowthPct = rs.getDouble("YTY_GRWTH_PCT");
	            QuoteLineItem_jdbc lineItem = (QuoteLineItem_jdbc) lineItemMap.get(partNum + seqNum);
	            int includedInImpliedYTYGrowthFlag = rs.getInt("IMPLD_INCLDD_FLG");
	            int includedInOverallYTYGrowthFlag = rs.getInt("YTY_INCLDD_FLG");
	            int renwlQliQty = rs.getInt("RENWL_QUOTE_LINE_ITEM_QTY");

        		Double manualLPP = null;
        		if(rs.getObject("LOCAL_UNIT_PRICE_LPP") != null){
        			manualLPP = rs.getDouble("LOCAL_UNIT_PRICE_LPP");
        		}
        		Double sysComputedPrice = null;
        		if(rs.getObject("LOCAL_UNIT_PRICE") != null){
        			sysComputedPrice = rs.getDouble("LOCAL_UNIT_PRICE");
        		}
	            String manualLPPCustNum = rs.getString("SOLD_TO_CUST_NUM");
	            String sapSalesOrdNum = rs.getString("SAP_SALES_ORD_NUM");
	            String priorQuoteNum = rs.getString("PRIOR_QUOTE_NUM");
	            Integer partQty =null ;
	            if(rs.getObject("PART_QTY") != null){
	            	partQty = rs.getInt("PART_QTY");
	            }
	            String manualLPPartNum = rs.getString("YTY_PART_NUM");
	            String lppMissReas = rs.getString("LPP_MISSG_REAS");
	            String ytyGrwothRadio = rs.getString("GRWTH_DLGTN_COND_APPLD_BUTTON");
	            String justTxt=rs.getString("LPP_MISSG_REAS_DSCR");

	            YTYGrowth ytyGrowth = new YTYGrowth_jdbc();
	            ytyGrowth.includedInImpliedYTYGrowthFlag = includedInImpliedYTYGrowthFlag;
	            ytyGrowth.includedInOverallYTYGrowthFlag = includedInOverallYTYGrowthFlag;
	            ytyGrowth.manualLPP = manualLPP;
	            ytyGrowth.YTYGrowthPct = YTYGrowthPct;
	            ytyGrowth.manualLPPCustNum = manualLPPCustNum;
	            ytyGrowth.manualLPPPartNum = manualLPPartNum;
	            ytyGrowth.ytyGrwothRadio = ytyGrwothRadio;
	            ytyGrowth.ytySourceCode = ytySourceCode;
	            ytyGrowth.renwlQliQty = renwlQliQty;
	            ytyGrowth.sapSalesOrdNum = sapSalesOrdNum;
	            ytyGrowth.priorQuoteNum = priorQuoteNum;
	            ytyGrowth.partQty = partQty;
	            ytyGrowth.lppMissReas = lppMissReas;
	            ytyGrowth.justTxt = justTxt;
	            ytyGrowth.lineItemSeqNum = seqNum;
	            ytyGrowth.quoteNum = quoteNum; 
	    		ytyGrowth.setSysComputedPriorPrice(sysComputedPrice);
                lineItem.ytyGrowth = ytyGrowth;
            
	        } while(rs.next());
        }catch(Exception e){
            logger.error("Failed to get YTY Growth  infomation",e);
            throw new TopazException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItemFactory#findSaaSLineItemsNotPartOfAddOnTradeUpByWebQuoteNum(java.lang.String)
     */
    @Override
    public List findSaaSLineItemsNotPartOfAddOnTradeUpByWebQuoteNum(String webQuoteNum) throws TopazException {
    	TimeTracer tracer = TimeTracer.newInstance();

        List result = new ArrayList();
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", webQuoteNum);
        LogContext logger = LogContextFactory.singleton().getLogContext();
        ResultSet rs = null;
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_S_QT_LINE_ITMS_NOT_ADDON_TRADEUP, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_S_QT_LINE_ITMS_NOT_ADDON_TRADEUP, params);

            tracer.stmtTraceStart("call S_QT_LINE_ITMS_NOT_ADDON_TRADEUP");
            boolean psResult = ps.execute();
            tracer.stmtTraceEnd("call S_QT_LINE_ITMS_NOT_ADDON_TRADEUP");

            if (psResult) {
            	// iterator through config list, and put the config into a
                // map with sequence num as the key.
            	rs = ps.getResultSet();
                while (rs.next()) {
                String partNum = rs.getString("PART_NUM").trim();
                QuoteLineItem_jdbc lineItem = new QuoteLineItem_jdbc(webQuoteNum, new SaasPart(partNum));
	            if (null != lineItem) {
	            	lineItem.part.setsPartDesc(rs.getString("part_dscr_long")!=null?rs.getString("part_dscr_long").trim():"");
	            	lineItem.orignlSalesOrdRefNum = rs.getString("SAP_SALES_ORD_NUM")!=null?rs.getString("SAP_SALES_ORD_NUM").trim():"";
	            	lineItem.refDocLineNum = rs.getInt("LINE_ITEM_SEQ_NUM");
	            	lineItem.renwlMdlCode = rs.getString("RENWL_MDL_CODE")!=null?rs.getString("RENWL_MDL_CODE").trim():"";
	            }
	            result.add(lineItem);
                }
            }

        } catch (Exception e) {
            logger.error("Failed to get the quote line item from the database!", e);
            throw new TopazException(e);
        } finally{
        	try {
				if (null != rs && !rs.isClosed())
				{
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
        	tracer.dump();
        }

        return result;
    }

	@Override
	public QuoteLineItem createNewLineItemFromExistItem(QuoteLineItem item)
			throws TopazException {
        QuoteLineItem_jdbc quoteLineItem_jdbc = (QuoteLineItem_jdbc) item;
        quoteLineItem_jdbc.isNew(true); //persist to DB2
        return quoteLineItem_jdbc;
	}

	private void processQliBillingOption(ResultSet rs, Map lineItemMap) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
            Map<String, List<QuoteLineItemBillingOption>> map = new HashMap<String, List<QuoteLineItemBillingOption>>();
            do {
            	String partNum = rs.getString("PART_NUM").trim();
            	List<QuoteLineItemBillingOption> options = map.get(partNum);
                if(options == null){
                    options = new ArrayList<QuoteLineItemBillingOption>();
                    map.put(partNum, options);
                }
                
				if ("1".equals(StringUtils.trimToEmpty(rs.getString("BILLG_ANL_FLAG")))) {
					QuoteLineItemBillingOption annualBillingOption = new QuoteLineItemBillingOption(false, getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_ANNUAL));
					options.add(annualBillingOption);
				}
				if ("1".equals(StringUtils.trimToEmpty(rs.getString("BILLG_MTHLY_FLAG")))) {
					QuoteLineItemBillingOption monthlyBillingOption = new QuoteLineItemBillingOption(false, getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_MONTHLY));
					options.add(monthlyBillingOption);						
				}
				if ("1".equals(StringUtils.trimToEmpty(rs.getString("BILLG_QTRLY_FLAG")))) {
					QuoteLineItemBillingOption quarterlyBillingOption = new QuoteLineItemBillingOption(false, getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_QUARTERLY));
					options.add(quarterlyBillingOption);
				}
				if ("1".equals(StringUtils.trimToEmpty(rs.getString("BILLG_UPFRNT_FLAG")))) {
					QuoteLineItemBillingOption upfrontBillingOption = new QuoteLineItemBillingOption(false, getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_UPFRONT));
					options.add(upfrontBillingOption);
				}
				if("1".equals(StringUtils.trimToEmpty(rs.getString("BILLG_EVENT_FLAG")))){
					QuoteLineItemBillingOption eventBillingOption = new QuoteLineItemBillingOption(false, getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_EVENT));
					options.add(eventBillingOption);
				}
            } while(rs.next());

            Iterator lineItemIter = lineItemMap.values().iterator();
            while(lineItemIter.hasNext()){
                QuoteLineItem lineItem = (QuoteLineItem)lineItemIter.next();
                List<QuoteLineItemBillingOption> billingOptions = map.get(lineItem.getPartNum());

                if(billingOptions != null){
                    lineItem.setBillingOptions(billingOptions);
                }
            }
        }catch(Exception e){
            logger.error("Failed to get billing options infomation",e);
            throw new TopazException(e);
        }
    }
	
	private BillingOption getBillingOptionByCode(String code) throws TopazException{
		Map<String, BillingOption> billingOptionMap = BillingOptionFactory.singleton().getBillingOptionMap();
		if (StringUtils.isBlank(code))
			return null;
		if (billingOptionMap == null || billingOptionMap.values().size() == 0)
			return null;
		BillingOption billingOption = billingOptionMap.get(code);
		return billingOption;
	}

    @Override
    public OmitRenewalLine getOmittedRenewalLine(String webQuoteNum) throws TopazException {
        OmitRenewalLine omitRenewalLine = new OmitRenewalLine();

        TimeTracer tracer = TimeTracer.newInstance();
        String spName = CommonDBConstants.DB2_S_QT_GET_OMIT_GROWTH_DELEGATION;
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        ResultSet rs = null;
        try {
            logger.debug(this, "webquote number is " + webQuoteNum);
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(spName, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            tracer.stmtTraceStart("call S_QT_GET_OMIT_GROWTH_DELEGATION");
            boolean psResult = ps.execute();
            tracer.stmtTraceEnd("call S_QT_GET_OMIT_GROWTH_DELEGATION");

            int retStatus = ps.getInt(1);
            int omitRecalculateFlag = ps.getInt(3);
            Double omittedLineGrowth = ps.getDouble(4);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
            }
            omitRenewalLine.setOmittedLineRecalcFlag(omitRecalculateFlag);
            omitRenewalLine.setOmittedLinePrice(omittedLineGrowth);
        } catch (Exception e) {
             logger.error("Failed to get the total price of the omit renewal line.",e);
            throw new TopazException(e);
        }

        return omitRenewalLine;
    }

	@Override
	public void updateLineItemCRAD(String webQuoteNum, int seqNum,
			java.sql.Date lineItemCRAD, String userID) throws TopazException {
		HashMap<String, Object> parms = new HashMap<String, Object>();
    	parms.put("piWebQuoteNum", webQuoteNum);
    	parms.put("piQuoteLineItemSeqNum", new Integer(seqNum));
    	parms.put("piCustReqArrivDate", lineItemCRAD);
    	parms.put("piUserID", userID);
    	LogContext logContext = LogContextFactory.singleton().getLogContext();
    	
    	try {
    		QueryContext queryCtx = QueryContext.getInstance();
    		String sqlQuery = queryCtx.getCompletedQuery(CommonDBConstants.DB2_U_QT_LI_CRAD, null);
    		CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
    		logContext.debug(this, LogHelper.logSPCall(sqlQuery, parms));
    		queryCtx.completeStatement(ps, CommonDBConstants.DB2_U_QT_LI_CRAD, parms);

    		ps.execute();
    		int resultCode = ps.getInt(1);
    		if (resultCode == CommonDBConstants.DB2_SP_RETURN_NOT_VALUE_UNIT) {
    			// if not calculate by value unit,then do nothing
    			return;
    		}
    		if (resultCode == CommonDBConstants.DB2_SP_RETURN_SIGN_NODATA) {
    			throw new NoDataException();
    		} else if (resultCode != CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
    			throw new SPException(LogHelper.logSPCall(sqlQuery, parms), resultCode);
    		}
    	}catch (SQLException e) {
    		logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
    		throw new TopazException("Exception when execute the SP " + CommonDBConstants.DB2_U_QT_LI_CRAD, e);
    	}
		
	}
	
    @Override
    public void reomveOmittedRenewalLine(String webQuoteNum) throws TopazException{
        TimeTracer tracer = TimeTracer.newInstance();
        String spName = CommonDBConstants.DB2_D_QT_OMIT_LINES;
        HashMap parms = new HashMap();
        parms.put("piWebQuoteNum", webQuoteNum);
        ResultSet rs = null;
        try {
            logger.debug(this, "webquote number is " + webQuoteNum);
            QueryContext queryCtx = QueryContext.getInstance();
            String sqlQuery = queryCtx.getCompletedQuery(spName, null);
            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
            queryCtx.completeStatement(ps, spName, parms);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, parms));

            tracer.stmtTraceStart("call D_QT_OMIT_LINES");
            boolean psResult = ps.execute();
            tracer.stmtTraceEnd("call D_QT_OMIT_LINES");

            int retStatus = ps.getInt(1);

            if (CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS != retStatus) {
                throw new TopazException("exeute sp failed, retStatus=" + retStatus + ",sql:" + sqlQuery);
            }
        } catch (Exception e) {
             logger.error("Failed to get the total price of the omit renewal line.",e);
            throw new TopazException(e);
        }
    }

    private void processDeploymentModel(ResultSet rs,Map lineItemMap) throws TopazException{
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try{
	        do {
	            int seqNum = rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
	            String partNum = rs.getString("PART_NUM").trim();
	            QuoteLineItem_jdbc lineItem = (QuoteLineItem_jdbc) lineItemMap.get(partNum + seqNum);
	            if (null != lineItem) {
	            	Integer deployModelOption = rs.getInt("DEPLOYMT_OPT");
	            	String deployModelId = rs.getString("DEPLOYMT_ID");
	            	boolean deployModelInvalid = PartPriceConstants.DEPLOYMENT_ID_INVALID.equals(rs.getString("DEPLOYMT_ID_INVD"));
                    Integer serialNumWarningFlag = rs.getObject("APPLNC_SERIAL_NUM_WARNG_FLAG") == null ? null : rs
                            .getInt("APPLNC_SERIAL_NUM_WARNG_FLAG");
	            	lineItem.setDeployModel(new DeployModel(deployModelOption,deployModelId,deployModelInvalid,serialNumWarningFlag));
	            }
	        } while(rs.next());
        }catch(Exception e){
            logger.error("Failed to get appliance part infomation",e);
            throw new TopazException(e);
        }
    }
    
	private void processMonthlySwLineItem(ResultSet rs, Map lineItemMap)
			throws TopazException {
		LogContext logger = LogContextFactory.singleton().getLogContext();
		try {
			do {

				int seqNum = rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
				String partNum = rs.getString("PART_NUM") != null ? rs
						.getString("PART_NUM").trim() : "";

				MonthlySwLineItem_jdbc mntlySotwre = (MonthlySwLineItem_jdbc) lineItemMap
						.get(partNum + seqNum);

				if (mntlySotwre != null) {
					mntlySotwre.monthlySwPart = "1".equals(rs
							.getString("MONTHLY_SW_FLAG"));
					mntlySotwre.monthlySwSubscrptnPart = "1".equals(rs
							.getString("MONTHLY_SW_SBSCRPTN_FLAG"));
					mntlySotwre.monthlySwSubscrptnOvragePart = "1".equals(rs
							.getString("MONTHLY_SW_SBSCRPTN_OVRAGE_FLAG"));
					mntlySotwre.monthlySwDailyPart = "1".equals(rs
							.getString("MONTHLY_SW_DLY_FLAG"));
					mntlySotwre.monthlySwOnDemandPart = "1".equals(rs
							.getString("MONTHLY_SW_ON_DMND_FLAG"));
					mntlySotwre.hasRamupPart = "1".equals(rs
							.getString("HAS_RAMP_UP_FLAG"));
					mntlySotwre.updateSectionFlag = "1".equals(rs
							.getString("UPDATE_SECTION_FLAG"));
					mntlySotwre.additionSectionFlag = "1".equals(rs
							.getString("ADDITION_SECTION_FLAG"));
					mntlySotwre.saasTcvAcv = "1".equals(rs
							.getString("MONTHLY_SW_TCV_ACV_FLAG"));

				}

			} while (rs.next());

		} catch (Exception e) {
			logger.error("Failed to get monthly software infomation", e);
			throw new TopazException(e);
		}
	}

	// added for dsw 14.2 task# 627593 deploymentID display renewal part
	private void processDeploymtID(ResultSet rs, Map lineItemMap)
			throws TopazException {
		LogContext logger = LogContextFactory.singleton().getLogContext();
		try {
			do {
				int seqNum = rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
				String partNum = rs.getString("PART_NUM").trim();
				QuoteLineItem_jdbc lineItem = (QuoteLineItem_jdbc) lineItemMap
						.get(partNum + seqNum);
				if (null != lineItem) {
					String deployModelId = rs.getString("applnc_deploymt_id");
					if (StringUtils.isNotBlank(deployModelId)) {
						lineItem.setRenewalDeploymtID(deployModelId);
					}					
				}
			} while (rs.next());
		} catch (Exception e) {
			logger.error("Failed to get appliance part infomation", e);
			throw new TopazException(e);
		}
	}
	
	private void processSaasLineItem(ResultSet rs, Map lineItemMap)
			throws TopazException {
		LogContext logger = LogContextFactory.singleton().getLogContext();
		try {
			do {

				int seqNum = rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
				String partNum = rs.getString("PART_NUM") != null ? rs
						.getString("PART_NUM").trim() : "";

				QuoteLineItem_jdbc lineItem = (QuoteLineItem_jdbc) lineItemMap
						.get(partNum + seqNum);

				if (lineItem != null) {
					lineItem.part.setSaasPart(PartPriceConstants.SAAS_PART_FLAG_YES.equals(rs.getString("SAAS_FLAG")));
                    lineItem.part.setSaasSetUpPart(PartPriceConstants.SAAS_PART_FLAG_YES.equals(rs.getString("SAAS_SETUP_FLAG")));
                    lineItem.part.setSaasSubscrptnPart(PartPriceConstants.SAAS_PART_FLAG_YES.equals(rs.getString("SAAS_SBSCRPTN_FLAG")));
                    //13.4kenexa (Add subsumedSubscription part)
                    lineItem.part.setSaasSubsumedSubscrptnPart(PartPriceConstants.SAAS_PART_FLAG_YES.equals(rs.getString("SUB_SUMED_SCRIPTION_FLAG")));
                    lineItem.part.setSaasSubscrptnOvragePart(PartPriceConstants.SAAS_PART_FLAG_YES.equals(rs.getString("SAAS_SBSCRPTN_OVRAGE_FLAG")));
                    lineItem.part.setSaasSetUpOvragePart(PartPriceConstants.SAAS_PART_FLAG_YES.equals(rs.getString("SAAS_SETUP_OVRAGE_FLAG")));
                    lineItem.part.setSaasDaily(PartPriceConstants.SAAS_PART_FLAG_YES.equals(rs.getString("SAAS_DLY_FLAG")));
                    lineItem.part.setSaasOnDemand(PartPriceConstants.SAAS_PART_FLAG_YES.equals(rs.getString("SAAS_ON_DMND_FLAG")));
                    lineItem.part.setSaasProdHumanServicesPart(PartPriceConstants.SAAS_PART_FLAG_YES.equals(rs.getString("PRODCTZD_HUMAN_SERVS_FLAG")));
                    lineItem.partSubType = rs.getString("PART_SUB_TYPE");
                    lineItem.saasTcvAcv = PartPriceConstants.SAAS_PART_FLAG_YES.equals(rs.getString("SAAS_TCV_ACV_FLAG"));
				}

			} while (rs.next());

		} catch (Exception e) {
			logger.error("Failed to get saas part infomation", e);
			throw new TopazException(e);
		}
	}
	
	private void processApplianceLineItem(ResultSet rs, Map lineItemMap)
			throws TopazException {
		LogContext logger = LogContextFactory.singleton().getLogContext();
		try {
			do {

				int seqNum = rs.getInt("QUOTE_LINE_ITEM_SEQ_NUM");
				String partNum = rs.getString("PART_NUM") != null ? rs
						.getString("PART_NUM").trim() : "";

				QuoteLineItem_jdbc lineItem = (QuoteLineItem_jdbc) lineItemMap
						.get(partNum + seqNum);

				if (lineItem != null) {
					//appliance part
                    lineItem.part.setApplncPart(PartPriceConstants.APPLIANCE_PART_FLAG_YES.equals(rs.getString("APPLIANCE_FLAG")));
                    lineItem.part.setApplncMain(PartPriceConstants.APPLIANCE_PART_FLAG_YES.equals(rs.getString("APPLIANCE")));
                    lineItem.part.setApplncReinstatement(PartPriceConstants.APPLIANCE_PART_FLAG_YES.equals(rs.getString("REINSTATEMENT")));
                    lineItem.part.setApplncServicePack(PartPriceConstants.APPLIANCE_PART_FLAG_YES.equals(rs.getString("SERVICE_PACK")));
                    lineItem.part.setApplncServicePackRenewal(PartPriceConstants.APPLIANCE_PART_FLAG_YES.equals(rs.getString("SERVICE_PACK_RENEWAL")));
                    lineItem.part.setApplncUpgrade(PartPriceConstants.APPLIANCE_PART_FLAG_YES.equals(rs.getString("APPLIANCE_UPGRADE")));
                    lineItem.part.setApplncTransceiver(PartPriceConstants.APPLIANCE_PART_FLAG_YES.equals(rs.getString("TRANSCEIVER")));
                    lineItem.part.setApplncRenewal(PartPriceConstants.APPLIANCE_PART_FLAG_YES.equals(rs.getString("RENEWAL_PARTS")));
                    lineItem.part.setApplncAdditional(PartPriceConstants.APPLIANCE_PART_FLAG_YES.equals(rs.getString("ADDITIONAL_SOFTWARE")));
                    lineItem.part.setApplncQtyRestrctn(PartPriceConstants.APPLIANCE_PART_FLAG_YES.equals(rs.getString("APPLNC_QTY_RESTRCTN_FLAG")));
                    lineItem.part.setApplncOwnerShipTransfer(PartPriceConstants.APPLIANCE_PART_FLAG_YES.equals(rs.getString("APPLNC_OWNRSHP_TRNSFR_FLAG")));
				}

			} while (rs.next());

		} catch (Exception e) {
			logger.error("Failed to get appliance part infomation", e);
			throw new TopazException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.common.domain.QuoteLineItemFactory#createMonthlySwLineItem(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public MonthlySwLineItem createMonthlySwLineItem(String webQuoteNum,
			String partNum, String userID) throws TopazException {
		
		MonthlySwLineItem_jdbc monthlySwLineItem_jdbc = new MonthlySwLineItem_jdbc(webQuoteNum,partNum);
		
		monthlySwLineItem_jdbc.setUserID(userID);
		monthlySwLineItem_jdbc.isNew(true);
		return monthlySwLineItem_jdbc;
	}
	
	public  Integer getMTMWarningFlg (String quoteNum,String partNum,String machineType,String machineModel,String MachineSerialNumber ) throws TopazException {
	    	TimeTracer tracer = TimeTracer.newInstance();

	        HashMap params = new HashMap();
	        params.put("piWebQuoteNum", quoteNum);
	        params.put("piPartNum", partNum);
	        params.put("piHMMachineType", machineType);
	        params.put("piHMMachineModel", machineModel);
	        params.put("piHMMachineSerialNumber", MachineSerialNumber);
	        LogContext logger = LogContextFactory.singleton().getLogContext();
	        ResultSet rs = null;
	        Integer result=null;
	        try {
	            QueryContext context = QueryContext.getInstance();
	            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_S_QT_VALIDATE_SERIAL_NUM, null);
	            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
	            CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
	            context.completeStatement(ps, CommonDBConstants.DB2_S_QT_VALIDATE_SERIAL_NUM, params);

	            tracer.stmtTraceStart("call S_QT_VALIDATE_SERIAL_NUM");
	            boolean psResult = ps.execute();
	            tracer.stmtTraceEnd("call S_QT_VALIDATE_SERIAL_NUM");

            result = (ps.getObject(2) == null ? null : ps.getInt(2));

	        } catch (Exception e) {
	            logger.error("Failed to get the quote line item from the database!", e);
	            throw new TopazException(e);
	        } finally{
	        	try {
					if (null != rs && !rs.isClosed())
					{
						rs.close();
					}
				} catch (SQLException e) {
					logger.error("Failed to close the resultset!", e);
				}
	        	tracer.dump();
	        }

	        return result;
	    }
}
