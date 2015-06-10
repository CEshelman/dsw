package com.ibm.dsw.quote.scw.addon.process;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.retrieval.RetrieveQuoteResultCodes;
import com.ibm.dsw.quote.retrieval.exception.RetrieveQuoteException;
import com.ibm.dsw.quote.scw.addon.ScwAddOnTradeUpResult;
import com.ibm.dsw.quote.scw.addon.domain.AddOnTradeUpInfo;
import com.ibm.dsw.quote.scw.addon.domain.RetrieveLineItem;
import com.ibm.dsw.quote.scw.addon.domain.RetrieveQuote;
import com.ibm.dsw.quote.scw.addon.domain.RetrieveQuoteHeader;
import com.ibm.dsw.quote.validation.ValidateConfiguratorPart;
import com.ibm.dsw.quote.validation.ValidateConfiguratorPartFactory;
import com.ibm.dsw.quote.validation.config.ValidateAddonTradeUpConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

public abstract class ScwAddonTradeupProcess_impl extends
		TopazTransactionalProcess implements ScwAddonTradeupProcess {

	protected ScwAddOnTradeUpResult scwAddOnTradeUpResult = new ScwAddOnTradeUpResult();

	@Override
	public abstract ScwAddOnTradeUpResult doScwAddOnTradeUp(AddOnTradeUpInfo addOnTradeUpInfo) throws TopazException,
			QuoteException;

	public void validateInputParams(AddOrUpdateConfigurationContract ct)
			throws TopazException {		
		validateDetailInputParams(ct);
	}

	protected abstract void validateBasicInputParams(AddOnTradeUpInfo addOnTradeUpInfo) throws TopazException;
	/*
	 * this is rough validate targeted  to convert from configuration to contract correctly
	 */
    protected abstract void validateAddOnTradeUpInfo(AddOnTradeUpInfo addOnTradeUpInfo);
	
	private void validateDetailInputParams(AddOrUpdateConfigurationContract ct) throws TopazException {
		ValidateConfiguratorPart validate = ValidateConfiguratorPartFactory.singleten().create(ValidateAddonTradeUpConstants.VALIDATE_SCW_ADDON_TRADEUP);
		validate.validate(ct);
		this.scwAddOnTradeUpResult.addNewErrorCodeList(validate.getScwAddonTradeUpErrorCodeList());
	}
	
	protected abstract String persiserAddOnTradeUpQuote(AddOnTradeUpInfo addOnTradeUpInfo) throws TopazException,	QuoteException;
	

	public String initAddOnTradeUpQuote(AddOnTradeUpInfo addOnTradeUpInfo) throws TopazException,	QuoteException
	{
		try
		{
			this.beginTransaction();
			String quoteNum = persiserAddOnTradeUpQuote(addOnTradeUpInfo);
			this.commitTransaction();
			return quoteNum;
		}
		catch ( Exception e )
		{
			throw new QuoteException(e);
		}
		finally
		{
			this.rollbackTransaction();
		}
	}

//	public void initAddOnTradeUpLineItem(AddOnTradeUpInfo addOnTradeUpInfo)
//			throws TopazException, QuoteException {
//		if (null == addOnTradeUpInfo || null == addOnTradeUpInfo.getConfigurations()
//				|| addOnTradeUpInfo.getConfigurations().isEmpty()) {
//			return;
//		}
//
//		TransactionContextManager.singleton().begin();
//		persiserAddOnTradeUpLineItem(addOnTradeUpInfo);
//		TransactionContextManager.singleton().commit();
//	}

	public RetrieveQuote retrieveScwAddOnTradeUpResult(
			AddOnTradeUpInfo addOnTradeUpInfo) throws TopazException,
			QuoteException {
		LogContext logContext = LogContextFactory.singleton().getLogContext();
		String chargeAgreementNum = addOnTradeUpInfo.getChargeAgreementNumber();
		String webQuoteNum = addOnTradeUpInfo.getWebQuoteNum();
		logContext.debug(this, "chargeAgreementNum:" + chargeAgreementNum
				+ " webQuoteNum:" + webQuoteNum);
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		Quote quote = null;
		try {
			quote = quoteProcess
					.getDraftQuoteDetailsForQuoteRetrieval(webQuoteNum);
		} catch (NoDataException nde) {
			throw new RetrieveQuoteException(nde,
					RetrieveQuoteResultCodes.QUOTE_NOT_FOUND);
		}
		RetrieveQuoteHeader retrieveHeader = extractRetrieveHeader(quote);
		retrieveHeader.setIdentifier(addOnTradeUpInfo.getAppIdentifier());
		retrieveHeader.setReferenceDocNum(addOnTradeUpInfo.getChargeAgreementNumber());
		retrieveHeader.setWebIDocNum(webQuoteNum);

		List<RetrieveLineItem> lineItems = extractRetrieveLineItems(quote, addOnTradeUpInfo);
		
//      Sort data by line item num	
		Collections.sort(lineItems, new Comparator<RetrieveLineItem>() {
            public int compare(RetrieveLineItem item1, RetrieveLineItem item2) { 
                int index;
                if(Integer.parseInt(item1.getLineItemNum()) > Integer.parseInt(item2.getLineItemNum())){
                	index = 1;
                } else {
        			index = -1;
        		}          
                return index;
            }
        });

		RetrieveQuote retrieveQuote = new RetrieveQuote();
		retrieveQuote.setHeader(retrieveHeader);
		retrieveQuote.setLineItems(lineItems);
		retrieveQuote = enhanceRetrieveQuote(chargeAgreementNum, webQuoteNum,
				retrieveQuote);

		return retrieveQuote;
	}

	private List<RetrieveLineItem> extractRetrieveLineItems(Quote quote, AddOnTradeUpInfo addOnTradeUpInfo) {
		List<RetrieveLineItem> retrieveLineItems = new ArrayList<RetrieveLineItem>();
		if (quote != null) {
			for (int i = 0; i < quote.getLineItemList().size(); i++) {
				QuoteLineItem qtLineItem = (QuoteLineItem) quote
						.getLineItemList().get(i);
				RetrieveLineItem lineItem = new RetrieveLineItem();
				lineItem.setLineItemNum(String.valueOf(qtLineItem
						.getDestSeqNum()));
//				lineItem.setReferringDocNumber(qtLineItem.getRenewalQuoteNum());
//				if (!StringUtils.isBlank(qtLineItem.getRenewalQuoteNum())) {
//					lineItem.setReferringDocType("RQ");
//				}
				lineItem.setRefDocLineItem(qtLineItem.getRefDocLineNum() == null ? "" : qtLineItem.getRefDocLineNum().toString());
				
				lineItem.setReferringDocNumber(addOnTradeUpInfo.getChargeAgreementNumber());
				lineItem.setPartNumber(qtLineItem.getPartNum());		
				lineItem.setQuantity(qtLineItem.getPartQty() == null ? null : String.valueOf(qtLineItem.getPartQty()));
				lineItem.setItemStartDate(formatDate(qtLineItem
						.getMaintStartDate()));
				lineItem.setItemEndDate(formatDate(qtLineItem.getMaintEndDate()));
				if (qtLineItem.isApplncPart()
						&& (!QuoteCommonUtil.isShowDatesForApplnc(qtLineItem))
						|| qtLineItem.isSaasDaily()) {
					lineItem.setItemStartDate(null);
					lineItem.setItemEndDate(null);
				}
				lineItem.setCurrencyCode(quote.getQuoteHeader()
						.getCurrencyCode());
				lineItem.setBillingOption(qtLineItem.getBillgFrqncyCode());
				lineItem.setTermValue(qtLineItem.getICvrageTerm() == null ? "" : qtLineItem.getICvrageTerm().toString());
				lineItem.setReplaceFlag(qtLineItem.isReplacedPart() ? "Y" : "N");
				lineItem.setConfigId(qtLineItem.getConfigrtnId());
				lineItem.setAddReasonCode(qtLineItem.getAddReasonCode());
				lineItem.setReplacedReasonCode(qtLineItem
						.getReplacedReasonCode());
				lineItem.setNewConfigFlag(StringUtils.isEmpty(qtLineItem.getNewConfigFlag()) ? "N" : qtLineItem.getNewConfigFlag());
				lineItem.setOriginatingItemNumber(qtLineItem.getOriginatingItemNum() == null ? "" : qtLineItem.getOriginatingItemNum().toString());
				if ( !StringUtils.equalsIgnoreCase(lineItem.getReplaceFlag(), "Y") )
				{
					lineItem.setOriginalItem(qtLineItem.getRefDocLineNum() == null ? "" : qtLineItem.getRefDocLineNum().toString());
				}
				// doNotOrderFlag
				// relatedItemNum
				if (qtLineItem.isSaasPart()) {
					if (StringUtils.equalsIgnoreCase(lineItem.getReplaceFlag(), "Y") )
					{
						lineItem.setReplacedTerm(qtLineItem.getReplacedTerm() == null ? "" : qtLineItem.getReplacedTerm().toString());
					}
					lineItem.setEarlyRnwlCompDate(formatDate(qtLineItem
							.getEarlyRenewalCompDate()));
					lineItem.setExtensionEligibilityDate(formatDate(qtLineItem
							.getExtensionEligibilityDate()));
				}
				// renewType
				// touUrlName
				lineItem.setTouUrl(StringUtils.trim(qtLineItem.getTouURL()));
				lineItem.setSubsumedSubscription(qtLineItem.isSaasSubsumedSubscrptnPart() ? "Y":"N");
				lineItem.setCotermLineSeqNum(qtLineItem.getRelatedCotermLineItmNum());
				
				if (!qtLineItem.isReplacedPart()
						&& (qtLineItem.isSaasDaily() || qtLineItem
								.isSaasSubscrptnOvragePart())) {
					lineItem.setRelatedItemNum(String.valueOf(qtLineItem.getIRelatedLineItmNum()));
				}
				
				retrieveLineItems.add(lineItem);
			}
		}
		return retrieveLineItems;
	}

	private RetrieveQuoteHeader extractRetrieveHeader(Quote quote) {
		RetrieveQuoteHeader retrieveHeader = new RetrieveQuoteHeader();
		if (quote != null) {
			retrieveHeader.setItemCount(String.valueOf(quote.getLineItemList()
					.size()));
			QuoteHeader qtHeader = quote.getQuoteHeader();
			retrieveHeader.setCurrencyCode(qtHeader.getCurrencyCode());
			retrieveHeader
					.setSalesOrganization(quote.getCustomer() == null ? ""
							: quote.getCustomer().getSalesOrg());
			// orderDate
			// referenceDocNum
			String sLob = qtHeader.getLob() == null ? "" : qtHeader.getLob()
					.getCode();
			String agreementType = qtHeader.getAgrmtTypeCode();
			if (StringUtils.isBlank(agreementType)) {
				if (QuoteConstants.LOB_PA.equals(sLob)
						|| QuoteConstants.LOB_PAE.equals(sLob)
						|| QuoteConstants.LOB_FCT.equals(sLob)) {
					if (QuoteConstants.LOB_PAE.equals(sLob)
							&& qtHeader.getSaasTermCondCatFlag() == 2) {
						agreementType = QuoteConstants.LOB_CSTA;
					} else {
						agreementType = sLob;
					}
				}
			}
			if (StringUtils.isBlank(agreementType)
					|| "N/A".equals(agreementType.trim()))
				agreementType = QuoteConstants.LOB_PA;
			if (StringUtils.isNotBlank(agreementType)) {
				retrieveHeader.setAgreementType(agreementType);
			}
		}
		return retrieveHeader;
	}

	private String formatDate(Date date) {
		SimpleDateFormat c = null;
		if (null != date) {
			c = new SimpleDateFormat("yyyy-MM-dd");
			return c.format(date);
		} else {
			return "";
		}
	}

	protected abstract RetrieveQuote enhanceRetrieveQuote(
			String chargeAgreementNum, String webQuoteNum,
			RetrieveQuote retrieveQuote) throws TopazException;

//	protected abstract void persiserAddOnTradeUpLineItem(
//			AddOnTradeUpInfo addOnTradeUpInfo) throws TopazException;
}
