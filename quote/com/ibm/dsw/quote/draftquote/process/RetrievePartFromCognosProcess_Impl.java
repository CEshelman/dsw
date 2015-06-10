package com.ibm.dsw.quote.draftquote.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.CognosPart;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.CognosRetrieveServiceHelper;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.contract.RetrievePartFromCognosContract;
import com.ibm.dsw.quote.newquote.config.NewQuoteDBConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

public abstract class RetrievePartFromCognosProcess_Impl extends TopazTransactionalProcess implements RetrievePartFromCognosProcess {
	protected LogContext logContext = LogContextFactory.singleton().getLogContext();
	public void addPartsToQuote(RetrievePartFromCognosContract ct) throws Exception{
		try{
			TransactionContextManager.singleton().begin();
				
			loadQuoteHeaderInfo(ct);
			if(isValidCognosCallback(ct)){
				List<CognosPart> allCognosPartList = getCognosParts(ct);
				if(allCognosPartList != null && allCognosPartList.size() > 0){
					processValidCognosParts(allCognosPartList, ct);
					processInvalidCognosParts(allCognosPartList, ct);
					removeOriginalParts(ct);
					addValidCognosPartsToquote(ct);
				}
			}
			
			TransactionContextManager.singleton().commit();
		} catch(TopazException te){
			TransactionContextManager.singleton().rollback();
			logContext.error(this, te);
			throw te;
		} catch (Exception e) {
			TransactionContextManager.singleton().rollback();
			logContext.error(this, e);
			throw e;
		}
	}
	
	/**
	 * @param ct
	 * @return
	 * validate the params of sending to Cognos and the params of Cognos callback, if match or not
	 */
	public boolean isValidCognosCallback(RetrievePartFromCognosContract ct){
		if(StringUtils.equals(ct.getQuoteHeader().getSoldToCustNum(), ct.getCognosSoldToCustNum())
			&& StringUtils.equals(ct.getQuoteHeader().getContractNum(), ct.getCognosContractNum())
				&& StringUtils.equals(QuoteCommonUtil.transLobForCognos(ct.getQuoteHeader().getLob().getCode(), ct
						.getQuoteHeader().getAgrmtTypeCode()), ct.getCognosLobCode())
			){
			return true;
		}else{
			return false;
		}
	}
	
	private void loadQuoteHeaderInfo(RetrievePartFromCognosContract ct) throws QuoteException {
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		try{
			QuoteHeader quoteHeader = quoteProcess.getQuoteHdrInfo(ct.getUserId());
			ct.setQuoteHeader(quoteHeader);
		} catch (NoDataException e) {
            logContext.error(this, "Get draft qutoe base info error: " + e.getMessage());
            throw new QuoteException(e);
        }
	}
	
	/**
	 * @param ct
	 * @return
	 * @throws Exception
	 * Call Cognos Web Service to get all the Cognos Parts
	 */
	private List<CognosPart> getCognosParts(RetrievePartFromCognosContract ct) throws Exception{
		logContext.info(this,"CognosRequestUrl ============= " + ct.getCognosRequestUrl());
		logContext.info(this,"Logon =========== "+ HtmlUtil.getCognosWebserviceLogon());
		logContext.info(this,"Logoff ========== "+ HtmlUtil.getCognosWebserviceLogoff());
		if(CognosRetrieveServiceHelper.getInstance().callWebService(
				ct.getCognosRequestUrl(), 
				HtmlUtil.getCognosWebserviceHost(), 
				HtmlUtil.getCognosWebservicePort(), 
				HtmlUtil.getCognosWebserviceProtocol(),
				HtmlUtil.getCognosWebserviceAuthentication(),
				HtmlUtil.getCognosWebserviceLogon(),
				HtmlUtil.getCognosWebserviceLogoff(),
				ct.getQuoteHeader().getWebQuoteNum())){
			ct.setCallCognosWebserviceSuccessful(true);
		}else{
			ct.setCallCognosWebserviceSuccessful(false);
		}
		return CognosRetrieveServiceHelper.getInstance().getCognosRetrievePartList();
	}
	
	/**
	 * @param cognosPartList
	 * @param ct
	 * @return the valid Cognos parts
	 * @throws QuoteException
	 */
	private void processValidCognosParts(List<CognosPart> allCognosPartList, RetrievePartFromCognosContract ct) throws QuoteException{
		StringBuffer partNumList = new StringBuffer();
		List<CognosPart> validCognosParts = new ArrayList<CognosPart>();
		if(allCognosPartList != null && allCognosPartList.size() > 0){
			for (Iterator iterator = allCognosPartList.iterator(); iterator.hasNext();) {
				CognosPart cognosPart = (CognosPart) iterator.next();
				partNumList.append(cognosPart.getPartNum())
				.append(NewQuoteDBConstants.DELIMIT);
			}
		}
		if(StringUtils.isNotBlank(partNumList.toString())){
			try {
				List<String> validPartNumbers = getValidPartsByPartSearch(partNumList.toString(), ct);
				for (Iterator iterator = allCognosPartList.iterator(); iterator.hasNext();) {
					CognosPart cognosPart = (CognosPart) iterator.next();
					if(cognosPart.isValid()){
						for (Iterator iterator2 = validPartNumbers.iterator(); iterator2.hasNext();) {
							String validPartNumber = (String) iterator2.next();
							if(StringUtils.isNotBlank(validPartNumber)
								&& validPartNumber.equals(cognosPart.getPartNum())){
								validCognosParts.add(cognosPart);
								break;
							}
						}
					}
				}
			} catch (QuoteException e) {
				logContext.error(this, "Get valid Cognos parts error: " +e.getMessage());
				throw new QuoteException(e);
			}
		}
		ct.setValidCognosParts(validCognosParts);
	}
	
	/**
	 * @param ct
	 * process the Invalide Cognos Parts
	 */
	private void processInvalidCognosParts(List<CognosPart> allCognosPartList, RetrievePartFromCognosContract ct){
		List<CognosPart> validCognosParts = ct.getValidCognosParts();
		List<CognosPart> invalidCognosParts = new ArrayList<CognosPart>();
		invalidCognosParts.addAll(allCognosPartList);
		invalidCognosParts.removeAll(validCognosParts);
		ct.setInvalidCognosParts(invalidCognosParts);
	}
	
	/**
	 * @param webQuoteNum
	 * @throws TopazException
	 * remove the original parts from current session quote
	 */
	private void removeOriginalParts(RetrievePartFromCognosContract ct) throws TopazException{
		try{
			if(ct.getValidCognosParts() != null && ct.getValidCognosParts().size() > 0){
				List<QuoteLineItem> lineItemsList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(ct.getQuoteHeader().getWebQuoteNum());
				if(lineItemsList != null && lineItemsList.size() > 0){
					for (Iterator iterator = lineItemsList.iterator(); iterator.hasNext();) {
						QuoteLineItem quoteLineItem = (QuoteLineItem) iterator.next();
						quoteLineItem.delete();
					}
				}
			}
		} catch(TopazException te){
			TransactionContextManager.singleton().rollback();
			logContext.error(this, te);
			throw te;
		}
	}
	
	/**
	 * @param ct
	 * @throws TopazException
	 * add the valid parts to quote, and get the exceed 250 parts
	 */
	private void addValidCognosPartsToquote(RetrievePartFromCognosContract ct) throws TopazException{
		try{
			List<CognosPart> validCognosParts = ct.getValidCognosParts();
			List<CognosPart> exceedMaxCognosParts = new ArrayList<CognosPart>();
			int mixmumPartsLimit = PartPriceConfigFactory.singleton().getElaLimits();
			if(validCognosParts != null && validCognosParts.size() > 0){
				for (int i = 0; i < validCognosParts.size(); i++) {
					CognosPart cognosPart = (CognosPart) validCognosParts.get(i);
					//if exceed the 250 Maximum limit, add the rest parts to ct.getExceedMaxCognosParts()
					if(i >= mixmumPartsLimit){
						exceedMaxCognosParts.add(cognosPart);
					//else add the new part to DB
					}else{
						QuoteLineItem qli = QuoteLineItemFactory.singleton().createQuoteLineItem(ct.getQuoteHeader().getWebQuoteNum(), cognosPart.getPartNum(), ct.getQuoteHeader().getCreatorId());
						qli.setPartQty(cognosPart.getPartQty());
					}
				}
			}
			ct.setExceedMaxCognosParts(exceedMaxCognosParts);
		} catch(TopazException te){
			TransactionContextManager.singleton().rollback();
			logContext.error(this, te);
			throw te;
		}
	}
}
