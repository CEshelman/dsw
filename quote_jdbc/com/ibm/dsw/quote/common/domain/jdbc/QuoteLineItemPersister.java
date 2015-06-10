package com.ibm.dsw.quote.common.domain.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;

import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
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
 * <br>This <code>QuoteLineItemPersister<code> class persist the state for QuoteLineItem.
 *
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 *
 * Creation date: Mar 4, 2007
 *
 * $Log: QuoteLineItemPersister.java,v $
 * Revision 1.36  2010/06/30 07:47:10  wxiaoli
 * SQR : Pushpa's area : Update web_quote sps : Need to put current quote user as mod_by, RTC 3407, reviewed by Will
 *
 * Revision 1.35  2010/06/29 06:29:06  changwei
 * Task 3334 SQO date logic : Enhance parts and pricing tab to derive 2 new line item attributes additionally
 * Reviewd by Vivian.
 *
 * Revision 1.34  2010/05/27 07:46:05  wxiaoli
 * contract level prcing flag for Display parts & pricing tab, RTC 3211,3212,3213,3214,3215,3216,3217, reviewed by Will
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class QuoteLineItemPersister extends Persister {

    QuoteLineItem_jdbc lineItem = null;

    /**
     *
     */
    public QuoteLineItemPersister(QuoteLineItem_jdbc quoteLineItem_jdbc) {
        super();
        this.lineItem = quoteLineItem_jdbc;
    }

    /**
     * @param connection
     * @throws TopazException
     */
    public void update(Connection connection) throws TopazException {

        long start = System.currentTimeMillis();
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", lineItem.getQuoteNum());
        params.put("piQuoteLineItemSeqNum", new Integer(lineItem.getSeqNum()));
        params.put("piQuoteSectnSeqNum", new Integer(lineItem.getQuoteSectnSeqNum()));
        params.put("piManualSortSeqNum", new Integer(lineItem.getManualSortSeqNum()));
        params.put("piPartNum", lineItem.getPartNum());
        params.put("piPartQty", lineItem.getPartQty());
        params.put("piVuConfigrtnNum", new Integer(lineItem.getVuConfigrtnNum()));
        params.put("piVuCalctrQtyOvrrdCode", lineItem.getPVUOverrideQtyIndCode());
        params.put("piSwProdBrandCode", lineItem.getSwProdBrandCode());
        params.put("piCntribtnUnitPts", new Double(lineItem.getContributionUnitPts()));
        params.put("piCntribtnExtndPts", new Double(lineItem.getContributionExtPts()));
        params.put("piRenwlQuoteNum", lineItem.getRenewalQuoteNum());
        params.put("piRenwlQuoteLineItemSeqNum", new Integer(lineItem.getRenewalQuoteSeqNum()));
        params.put("piRenwlEndDate", lineItem.getRenewalQuoteEndDate());
        params.put("piStartDate", lineItem.getMaintStartDate());
        params.put("piEndDate", lineItem.getMaintEndDate());
        params.put("piProratnFlag", lineItem.getProrateFlag() ? new Integer(1) : new Integer(0));
        params.put("piAddtnlMaintCvrageQty", new Integer(lineItem.getAddtnlMaintCvrageQty()));
        params.put("piAssocdLicPartFlag", lineItem.bHasAssocdLicPart ? new Integer(1) : new Integer(0));
        params.put("piLineDiscPct", new Float(lineItem.getLineDiscPct()));
        params.put("piOverrideUnitPrice", lineItem.getOverrideUnitPrc());
        params.put("piLocalUnitPrice", lineItem.getLocalUnitPrc());
        params.put("piPrratdLocalUnitPrice", lineItem.getLocalUnitProratedPrc());
        params.put("piPrratdDiscdLocalUnitPrice", lineItem.getLocalUnitProratedDiscPrc());
        params.put("piLocalExtndPrice", lineItem.getLocalExtPrc());
        params.put("piPrratdLocalExtndPrice", lineItem.getLocalExtProratedPrc());
        params.put("piPrratdDiscdLocalExtndPrice", lineItem.getLocalExtProratedDiscPrc());
        params.put("piStartDtOvrrdFlg",lineItem.bStartDtOvrrdFlg ? new Integer(1):new Integer(0));
        params.put("piEndDtOvrrdFlg",lineItem.bEndDtOvrrdFlg ? new Integer(1):new Integer(0));
        params.put("piRenwlChgCode", lineItem.sRenwlChgCode);
        params.put("piChgType", lineItem.sChgType);
        params.put("piComment", lineItem.sComment);
        params.put("piChnlUnitPrice", lineItem.dChannelUnitPrice);
        params.put("piChnlExtndPrice", lineItem.dChannelExtndPrice);
        params.put("piLocalTaxAmt", lineItem.dLocalTaxAmt);
        params.put("piLclExtndPriceIncldTax", lineItem.dLclExtndPriceIncldTax);
        params.put("piLocalChnlTaxAmt", lineItem.dLocalChnlTaxAmt);
        params.put("piLclExtndChnlPriceIncldTax", lineItem.dLclExtndChnlPriceIncldTax);
        params.put("piPrevsStartDate",lineItem.dtPrevsStartDate);
        params.put("piPrevsEndDate",lineItem.dtPrevsEndDate);
        params.put("piAddtnlYrCvrageSeqNum",new Integer(lineItem.iAddtnlYearCvrageSeqNum));
        //added for back dating and BP discount
        int backDtgFlag = lineItem.getBackDatingFlag()? 1 : 0;
        params.put("piBackDtgFlag", new Integer(backDtgFlag));
        params.put("piChnlStdDiscPct", lineItem.getChnlStdDiscPct());
        params.put("piChnlOvrrdDiscPct", lineItem.getChnlOvrrdDiscPct());
        params.put("piTotDiscPct", lineItem.getTotDiscPct());

        // apply October enhancement
        params.put("piOvrrdExtndPrice", lineItem.ovrrdExtPrice);
        params.put("piDestObjctLineItmSeqNum",new Integer(lineItem.iDestSeqNum));
        if (lineItem.offerIncldFlag != null){
            params.put("piOfferInclddFlg", (lineItem.offerIncldFlag.booleanValue()) ? new Integer(1) : new Integer(0) );
        }

        //added for EOL
        params.put("piMnlPrratdLclUnitPricFlag", new Integer(lineItem.getManualProratedLclUnitPriceFlag()));

        //Cmprss cvrage
        params.put("piCmprssCvrageMths", lineItem.getCmprssCvrageMonth());
        params.put("piCmprssCvrageDiscPct", lineItem.getCmprssCvrageDiscPct());
        params.put("piModByUserID", lineItem.getSModByUserID());

        //CLP
        params.put("piCtLevelPricingFlag",new Integer(lineItem.isLegacyBasePriceUsedFlag() ? 1 : 0));

        //Date logic
        params.put("piRelatedLineItmNum", new Integer(lineItem.getIRelatedLineItmNum()));
        params.put("piPartType", lineItem.getSPartType());

        //Saas part
        params.put("piSAASTotCmmtmtVal", lineItem.getSaasBidTCV());
        params.put("piCvrageTerm", lineItem.getICvrageTerm());
        params.put("piBillgFrqncyCode", lineItem.getBillgFrqncyCode());

        //add for DSW10.5
        params.put("piRefDocLineNum", lineItem.getRefDocLineNum());
        params.put("piConfigrtnId", lineItem.getConfigrtnId());
        params.put("piRelatedAlignLineItmNum", lineItem.getRelatedAlignLineItmNum());
        params.put("piRelatedCotermLineItmNum", lineItem.getRelatedCotermLineItmNum());
        params.put("piRampUp", lineItem.isRampupPart()?PartPriceConstants.RAMP_UP_FLAG_YES:null);
        params.put("piReplacedPart", lineItem.isReplacedPart() ? PartPriceConstants.REPLACED_PART_FLAG_YES : null);
        params.put("piCumCvrageTerm", lineItem.getCumCvrageTerm());
        params.put("piChnlSAASTotCmmtmtVal", lineItem.getSaasBpTCV());
        params.put("piSaasRenwlFlag", (lineItem.isSaasRenwl() ? new Integer(1) : new Integer(0) ));
        
        // 15.1
        if (lineItem.isNewService()==null){
        	params.put("piIsNewServiceFlag", null);
        } else {
        	params.put("piIsNewServiceFlag", (lineItem.isNewService()? "1":"0"));
        }
        
        //15.3 could be null or String
        params.put("piPartSubType", lineItem.getPartSubType());
        

        // add appliance parts attribute
        params.put("piMachineType", lineItem.getMachineType());
        params.put("piMachineModel", lineItem.getModel());
        params.put("piSerialNumber", lineItem.getSerialNumber());
        params.put("piApplncPocInd", lineItem.getApplncPocInd());
        params.put("piApplncPriorPoc", lineItem.getApplncPriorPoc());
        params.put("piWebMigrtdDocFlag", lineItem.isWebMigrtdDoc() ? "1" : "0");

        
        
        params.put("piNonIBMMachineModel", lineItem.getNonIBMModel());
        params.put("piNonIBMSerialNumber", lineItem.getNonIBMSerialNumber());
        
        params.put("piCustReqArrvDate", lineItem.getLineItemCRAD());
        
        //FCT TO PA Finalization
        params.put("piOrignlSalesOrdRefNum", lineItem.getOrignlSalesOrdRefNum());
        params.put("piOrignlConfigrtnId", lineItem.getOrignlConfigrtnId());
        params.put("piAddOnRenwlElgbltyDate", lineItem.getEarlyRenewalCompDate());
        

        // Saas 10.4 & 10.6 
        //TODO date
        params.put("piExtensionEliDate", lineItem.getExtensionEligibilityDate());
        //13.4GD 126. Proration Rounding on Bid Unit Price RSVP Price reset
        params.put("piSetLineToRsvpSrpFlag",  new Integer(lineItem.isSetLineToRsvpSrpFlag() ? 1 : 0));
        params.put("piRenewalPricingMethod",  lineItem.getRenewalPricingMethod());
        // 14.2 deployment model
        if (null != lineItem.getDeployModel()){
            params.put("piDeployModelOption",  -1 == lineItem.getDeployModel().getDeployModelOption()?null:lineItem.getDeployModel().getDeployModelOption());
            params.put("piDeployModelId",  lineItem.getDeployModel().getDeployModelId());
            params.put("piDeployModelValid",  lineItem.getDeployModel().isDeployModelInvalid() ? "1" : "0");
            params.put("piSerialNumWarningFlag",  lineItem.getDeployModel().getSerialNumWarningFlag());
        }else{
            params.put("piDeployModelOption",  null);
            params.put("piDeployModelId",  null);
            params.put("piDeployModelValid",  null);
            params.put("piSerialNumWarningFlag",  null);
        }
        // 14.4 SCW add on /trade up
        params.put("piAddReasonCode", lineItem.getAddReasonCode());
        params.put("piReplacedReasonCode", lineItem.getReplacedReasonCode());
        params.put("piNewConfigFlag", lineItem.getNewConfigFlag());
        params.put("piOriginatingItemNum", lineItem.getOriginatingItemNum());

        int retCode = -1;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        logger.debug(this, lineItem.getPartNum() + " origItem.replaceFlag  =====" + lineItem.isReplacedPart());
		
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_U_QT_LINE_ITEM, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_U_QT_LINE_ITEM, params);
            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
            //this.isNew(true);
            //this.isDeleted(false);
        } catch (Exception e) {
            logger.error("Failed to log the quote line item to the database!", e);
            throw new TopazException(e);
        }
        long end = System.currentTimeMillis();

        logger.debug(this,"Update single line item = "+ (end-start));
        
        
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#delete(java.sql.Connection)
     */
    public void delete(Connection connection) throws TopazException {

        long start = System.currentTimeMillis();
        HashMap params = new HashMap();
        params.put("piWebQuoteNum", lineItem.getQuoteNum());
        params.put("piSeqNum", new Integer(lineItem.getSeqNum()));
        params.put("piModByUserID", lineItem.getSModByUserID());
        int retCode = -1;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try {
            QueryContext context = QueryContext.getInstance();
            String sqlQuery = context.getCompletedQuery(CommonDBConstants.DB2_D_QT_LINE_ITEM, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = connection.prepareCall(sqlQuery);
            context.completeStatement(ps, CommonDBConstants.DB2_D_QT_LINE_ITEM, params);
            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
                throw new TopazException("SP call returns error code: " + retCode);
            }
            this.isDeleted(true);
        } catch (Exception e) {
            logger.error("Failed to delete the quote line item from the database!", e);
            throw new TopazException(e);
        }
        long end = System.currentTimeMillis();

        logger.debug(this,"Delete Single Line Item = "+(end-start));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        // may not need this, we always use SP to return all line items of
        // the part
        //implement this method only if we have SP to return one line item

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.AbstractPersister#insert(java.sql.Connection)
     */
    public void insert(Connection connection) throws TopazException {
        long start = System.currentTimeMillis();
        HashMap params = new HashMap();

        params.put("piWebQuoteNum", lineItem.getQuoteNum());
        params.put("piQuoteSectnSeqNum", new Integer(lineItem.getQuoteSectnSeqNum()));
        params.put("piManualSortSeqNum", new Integer(lineItem.getManualSortSeqNum()));
        params.put("piPartNum", lineItem.getPartNum());
        params.put("piPartQty", lineItem.getPartQty());
        params.put("piVuConfigrtnNum", new Integer(lineItem.getVuConfigrtnNum()));
        params.put("piVuCalctrQtyOvrrdCode", lineItem.getPVUOverrideQtyIndCode());
        params.put("piSwProdBrandCode", lineItem.getSwProdBrandCode());
        params.put("piCntribtnUnitPts", new Double(lineItem.getContributionUnitPts()));
        params.put("piCntribtnExtndPts", new Double(lineItem.getContributionExtPts()));
        params.put("piRenwlQuoteNum", lineItem.getRenewalQuoteNum());
        params.put("piRenwlQuoteLineItemSeqNum", new Integer(lineItem.getRenewalQuoteSeqNum()));
        params.put("piRenwlEndDate", lineItem.getRenewalQuoteEndDate());
        params.put("piStartDate", lineItem.getMaintStartDate());
        params.put("piEndDate", lineItem.getMaintEndDate());
        params.put("piProratnFlag", lineItem.getProrateFlag() ? new Integer(1) : new Integer(0));
        params.put("piAddtnlMaintCvrageQty", new Integer(lineItem.getAddtnlMaintCvrageQty()));
        params.put("piAssocdLicPartFlag", lineItem.bHasAssocdLicPart ? new Integer(1) : new Integer(0));
        params.put("piLineDiscPct", new Float(lineItem.getLineDiscPct()));
        params.put("piOverrideUnitPrice", lineItem.getOverrideUnitPrc());
        params.put("piLocalUnitPrice", lineItem.getLocalUnitPrc());
        params.put("piPrratdLocalUnitPrice", lineItem.getLocalUnitProratedPrc());
        params.put("piPrratdDiscdLocalUnitPrice", lineItem.getLocalUnitProratedDiscPrc());
        params.put("piLocalExtndPrice", lineItem.getLocalExtPrc());
        params.put("piPrratdLocalExtndPrice", lineItem.getLocalExtProratedPrc());
        params.put("piPrratdDiscdLocalExtndPrice", lineItem.getLocalExtProratedDiscPrc());
        params.put("piStartDtOvrrdFlg",lineItem.bStartDtOvrrdFlg ? new Integer(1):new Integer(0));
        params.put("piEndDtOvrrdFlg",lineItem.bEndDtOvrrdFlg ? new Integer(1):new Integer(0));
        params.put("piRenwlChgCode", lineItem.sRenwlChgCode);
        params.put("piChgType", lineItem.sChgType);
        params.put("piComment", lineItem.sComment);
        params.put("piChnlUnitPrice", lineItem.dChannelUnitPrice);
        params.put("piChnlExtndPrice", lineItem.dChannelExtndPrice);
        params.put("piLocalTaxAmt", lineItem.dLocalTaxAmt);
        params.put("piLclExtndPriceIncldTax", lineItem.dLclExtndPriceIncldTax);
        params.put("piLocalChnlTaxAmt", lineItem.dLocalChnlTaxAmt);
        params.put("piLclExtndChnlPriceIncldTax", lineItem.dLclExtndChnlPriceIncldTax);
        params.put("piPrevsStartDate",lineItem.dtPrevsStartDate);
        params.put("piPrevsEndDate",lineItem.dtPrevsEndDate);
        params.put("piAddtnlYrCvrageSeqNum",new Integer(lineItem.iAddtnlYearCvrageSeqNum));
        params.put("piOverrideExtndPrice", lineItem.ovrrdExtPrice);
        params.put("piDestObjctLineItmSeqNum",new Integer(lineItem.iDestSeqNum));
        //added for back dating and BP discount
        int backDtgFlag = lineItem.getBackDatingFlag()? 1 : 0;
        params.put("piBackDtgFlag", new Integer(backDtgFlag));
        params.put("piChnlStdDiscPct", lineItem.getChnlStdDiscPct());
        params.put("piChnlOvrrdDiscPct", lineItem.getChnlOvrrdDiscPct());
        params.put("piTotDiscPct", lineItem.getTotDiscPct());

        //added for EOL
        params.put("piMnlPrratdLclUnitPricFlag", new Integer(lineItem.getManualProratedLclUnitPriceFlag()));

        //Cmprss cvrage
        params.put("piCmprssCvrageMths", lineItem.getCmprssCvrageMonth());
        params.put("piCmprssCvrageDiscPct", lineItem.getCmprssCvrageDiscPct());
        params.put("piUserID", lineItem.getUserID());
        params.put("piCtLevelPricingFlag",new Integer(lineItem.isLegacyBasePriceUsedFlag() ? 1 : 0));

        //Saas part
        params.put("piSAASTotCmmtmtVal", lineItem.getSaasBidTCV());
        params.put("piCvrageTerm", lineItem.getICvrageTerm());
        params.put("piBillgFrqncyCode", lineItem.getBillgFrqncyCode());

        //add for DSW10.5
        params.put("piRefDocLineNum", lineItem.getRefDocLineNum());
        params.put("piConfigrtnId", lineItem.getConfigrtnId());
        params.put("piRelatedAlignLineItmNum", lineItem.getRelatedAlignLineItmNum());
        params.put("piRelatedCotermLineItmNum", lineItem.getRelatedCotermLineItmNum());
        params.put("piRampUp", lineItem.isRampupPart()?PartPriceConstants.RAMP_UP_FLAG_YES:null);
        params.put("piReplacedPart", lineItem.isReplacedPart() ? PartPriceConstants.REPLACED_PART_FLAG_YES : null);
        params.put("piCumCvrageTerm", lineItem.getCumCvrageTerm());
        params.put("piRelatedLineItmNum", lineItem.getIRelatedLineItmNum());
        params.put("piSAASTotCmmtmtValChnl", lineItem.getSaasBpTCV());
        

        //FCT TO PA Finalization
        params.put("piOrignlSalesOrdRefNum", lineItem.getOrignlSalesOrdRefNum());
        params.put("piOrignlConfigrtnId", lineItem.getOrignlConfigrtnId());
       
        params.put("piWebMigrtdDocFlag", lineItem.isWebMigrtdDoc() ? "1" : "0");
		params.put("piSaasRenwlFlag", (lineItem.isSaasRenwl() ? new Integer(1)
				: new Integer(0)));

		// 15.1
		if (lineItem.isNewService() == null) {
			params.put("piIsNewServiceFlag", null);
		} else {
			params.put("piIsNewServiceFlag", (lineItem.isNewService() ? "1"
					: "0"));
		}
        
		//15.3 could be null or String
        params.put("piPartSubType", lineItem.getPartSubType());
		
        //Appliance#99
        params.put("piCustReqArrvDate", lineItem.getLineItemCRAD());
        
        // Saas 10.4 & 10.6 
        //TODO date
        params.put("piExtensionEliDate", null);
        
        params.put("touName", lineItem.getTouName());
        params.put("touURL", lineItem.getTouURL());
        params.put("amendedTouFlag", lineItem.getAmendedTouFlag());
        params.put("amendedTouFlagB", lineItem.getAmendedTouFlagB());
        params.put("piQuoteLineSeqNum", lineItem.getSeqNum());
        // 14.4 SCW add on /trade up
        params.put("piAddReasonCode", lineItem.getAddReasonCode());
        params.put("piReplacedReasonCode", lineItem.getReplacedReasonCode());
        params.put("piNewConfigFlag", lineItem.getNewConfigFlag());
        params.put("piOriginatingItemNum", lineItem.getOriginatingItemNum());
        
        int retCode = -1;
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try {
            QueryContext context = QueryContext.getInstance();
			String sqlQuery = context.getCompletedQuery(
					CommonDBConstants.DB2_I_QT_LINE_ITEM, null);
            logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
            CallableStatement ps = connection.prepareCall(sqlQuery);
			context.completeStatement(ps,
					CommonDBConstants.DB2_I_QT_LINE_ITEM, params);
            ps.execute();
            retCode = ps.getInt(1);
            if (retCode != 0) {
            	if(CommonDBConstants.DB2_SP_RETURN_PART_INPUT_INVALID == retCode){
            		logger.info(this, "retStatus = " + retCode+ ", Invalid part number:"+lineItem.getPartNum());
            	}else{
            		throw new TopazException("SP call returns error code: " + retCode);
            	}
            }
            this.isNew(true);
            this.isDeleted(false);
        } catch (Exception e) {
            logger.error("Failed to log the quote line item to the database!", e);
            throw new TopazException(e);
        }
        long end = System.currentTimeMillis();
        logger.debug(this,"Insert single line item ="+(end-start));
    }
    
    
	
}
