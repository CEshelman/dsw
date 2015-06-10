package com.ibm.dsw.quote.draftquote.process;


import java.util.Map;

import com.ibm.dsw.quote.base.domain.QuoteUserSession;
import com.ibm.dsw.quote.base.exception.PriceEngineUnAvailableException;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.submittedquote.contract.UpdateLineItemCRADContract;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>PartPriceProcess</code> class is business interface for display
 * and post part and price for draft quote.
 *
 *
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: 2007-3-27
 */
public interface PartPriceProcess {
	
	public static final String PROCESS_TYPE_SW = "SOFTWARE";
	public static final String PROCESS_TYPE_SAAS = "SAAS";

    public void getPartPriceInfo(Quote quote,QuoteUserSession user) throws QuoteException, PriceEngineUnAvailableException;
    public void getPartPriceInfoNoTransation(Quote quote, QuoteUserSession user) throws QuoteException, PriceEngineUnAvailableException;

    /**
     * Get current price info, including adjusting line item dates, calling pricing service and storing the updated data to db2.
     * @param quote
     * @throws QuoteException
     * @throws PriceEngineUnAvailableException
     */
    public void getCurrentPriceForSubmit(Quote quote,String userID) throws QuoteException, PriceEngineUnAvailableException;

    public void getCurrentPriceForOrder(Quote quote, String userID) throws QuoteException, PriceEngineUnAvailableException;

    public void postPartPriceInfo(ProcessContract contract) throws QuoteException;

    public void updateQuoteHeader(QuoteHeader header, String userID) throws QuoteException;

    /**
     * add a new line item given the partNum to session quote owned by creatorId
     *
     * @param partNum
     * @param creatorId
     */
    public void addDupLineItem(String partNum, String creatorId) throws QuoteException;

    /**
     * add renewal part to the session quote owned by creatorId
     * @param rqNum
     * @param partNum
     * @param creatorId
     * @throws QuoteException
     */
    public int addRenewalPart(String rqNum, String partNum, String creatorId) throws QuoteException;
    
    /**
     * Add entir parts to quote 
     * @param raNum
     */

    public int addEntirParts(String webquoteNum, String caNum, String configNum, String isFromReporting) throws QuoteException;
    
    //public void calculateDate(Quote quote) throws QuoteException;

    //public List calculateDateWithNoUpdate(Quote quote) throws QuoteException;

    //public boolean calculatePrices(Quote quote) throws QuoteException;

    //public void updatePriceTotals(Quote quote) throws TopazException;

    public void changeAdditionalMaint(String webQuoteNum,String partNum,int seqNum,
			  int additionalYears,Integer partQty,int manualSortSeqNum,
			  Double overrideUnitprice,double discPct,boolean prorationFlag,String userID) throws QuoteException;

    public void applyDiscount(PostPartPriceTabContract contract) throws QuoteException;

    public void applyOffer(PostPartPriceTabContract contract) throws QuoteException;

    public void clearOffer(PostPartPriceTabContract contract) throws QuoteException;

    public void applyPartnerDiscount(PostPartPriceTabContract contract) throws QuoteException;

    public void applyPrcBandOvrrd(PostPartPriceTabContract contract) throws QuoteException;

    public void addRenwlModel(String userId,String renwlModelStr,String webQuoteNum,String configLevelRenwlModStr, String source) throws QuoteException;
    
    public void applyYtyGrowthDelegation(PostPartPriceTabContract contract) throws QuoteException;
    
    public void changeBillingFrequency(PostPartPriceTabContract contract) throws QuoteException;
    
    public void deleteYtyGrowth(String quoteNum,Integer seqNum)throws QuoteException;
    
    public void updateEquityCurvePart(String webQuoteNum)throws QuoteException;
    
    public void reviewUpdateOmittedRenewalLine(String webQuoteNum)throws QuoteException;
    
    public void submitLineItemCRAD(Quote quote,UpdateLineItemCRADContract ct) throws QuoteException;

	public void resetToRsvpSrp(PostPartPriceTabContract ct)throws QuoteException;
	
	public Map<String,Double> getLicensePartSplit(String webQuoteNum)throws QuoteException;
	
    public Map<String,String> validateOrCreateDeploymentId(String webQuoteNum, int quoteLineItemSeqNum,int deployModelOption,String deployModelId) throws QuoteException;

    public String getApplianceDeploymentIdByMTM(String applncMachineType, String applncMachineModel, String applncSerialNumber) throws QuoteException;
    
    public void doCalculateDeployModel(Quote quote) throws QuoteException;
    
    public void addOrUpdateDeploymentId(QuoteLineItem item, String userId) throws QuoteException;
    
    public void updateIncreaseBidTCV(String webQuoteNum, String configrtnIds, String increaseBidTCVs, String unUsedTCVs) throws QuoteException;
    
    public void updateTCVs(String webQuoteNum, String configrtnIds,String increaseBidTCVs, String unUsedTCVs) throws QuoteException;

    public void deleteRenwlModel(String webQuoteNum , String configrtnId, String chargeAgreementNum) throws QuoteException;
}
