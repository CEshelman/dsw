package com.ibm.dsw.quote.draftquote.action.validator;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.CoTermConfiguration;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.ServiceDateModType;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcess;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcessFactory;
import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;
import com.ibm.dsw.quote.draftquote.action.PostPartPriceTabAction;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract.LineItemParameter;
import com.ibm.dsw.quote.draftquote.util.PartPriceHelper;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code></code> class is
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 *         Creation date: Jun 25, 2007
 */
public class SalesQuoteUIValidator extends PartPriceUIValidator {

	public SalesQuoteUIValidator(PostPartPriceTabAction ppAction) {
		super(ppAction);
	}

	public boolean validate(ProcessContract contract, boolean checkOfferPrice)
			throws QuoteException {
		long start = System.currentTimeMillis();

		super.validate(contract, checkOfferPrice);
		// validate UI data firstly
		PostPartPriceTabContract ct = (PostPartPriceTabContract) contract;
		// hashmap for containing error messages
		HashMap vMap = new HashMap();

		HashMap items = ct.getItems();
		Iterator it = items.values().iterator();

		if (checkOfferPrice) {
			if (!this.checkOfferPrice(vMap, ct)) {
				return false;
			}
		}

		while (it.hasNext()) {

			// validate quantity to be integer
			LineItemParameter lineItem = (LineItemParameter) it.next();
			logContext.debug(this, "Line Item " + lineItem.key
					+ "Additional Year " + lineItem.maintainAddtionalYear);
			if (QuoteCommonUtil.isSkipLineItemUIValidate(lineItem, ct)) {
				continue;
			}
			if (!validateQuantity(lineItem, vMap, ct)) {
				return false;
			}
			
			// validate override price to be double
			if (!validateOverridePrice(lineItem, vMap, ct)) {
				return false;
			}
			
			if (!this.validateDiscount(lineItem, vMap, ct)) {
				return false;
			}
			
			if (!this.validateYty(lineItem, vMap, ct)) {
				return false;
			}

			if (!this.validateLineItemPartnerDiscount(lineItem, vMap, ct)) {
				return false;
			}

			if (!this.validationMachType(lineItem, vMap, ct)) {
				return false;
			}

			if (!this.validationMachModel(lineItem, vMap, ct)) {
				return false;
			}

			if (!this.validationSerialNum(lineItem, vMap, ct)) {
				return false;
			}

			if (!this.validationApplncId(lineItem, vMap, ct)) {
				return false;
			}
			
			if (!this.validateApplncQty(lineItem, vMap, ct)) {
				return false;
			}
			
			if (!this.validateApplncPoc(lineItem, vMap, ct)) {
				return false;
			}
		}

		if (!this.validateCmprssCvrage(vMap, ct)) {
			return false;
		}

		if (!this.checkBackDating(vMap, ct)) {
			return false;
		}

		// validate EOL price: override unit price and entitled unit price are
		// required
		if (!validateEOLPartInfo(vMap, ct)) {
			return false;
		}

		validateTotalPartNumber(ct);

		if (!validateProvsningDays(vMap, ct)) {
			return false;
		}
		
		if (!validateTermRnwlMdlCode(vMap, ct)) {
	         return false;
	      }
		
		if (!validateEstimatedOrderDate(vMap, ct)) {
	         return false;
	      }
// to do , need to confirm the validate method.
		if (!validateTermExtenstionDate(vMap, ct)) {
			return false;
		}
		
		logContext.debug(this, "Total line items in UI:"
				+ ct.getItems().values().size());

		logContext.debug(this, "SalesQuoteUIValidator.validate time dump: "
				+ (System.currentTimeMillis() - start));
		checkPerGrowthYTY(ct);
		return true;

	}
	
	private  void checkPerGrowthYTY(PostPartPriceTabContract ct){
   	 HashMap items = ct.getItems();
   	 List lineItemsList = ct.getQuote().getLineItemList();
   	 if(lineItemsList != null && lineItemsList.size() > 0){
   		for(int i = 0; i < lineItemsList.size(); i ++){
      		 QuoteLineItem qli = (QuoteLineItem)lineItemsList.get(i);
      		 if(qli.isRenewalPart()){
      			 LineItemParameter lip = (LineItemParameter)items.get(createKey(qli));
          		 if(lip == null){
                    	continue;
                    }else{
                  	  boolean bool=  checkYTYAttrubute(lip,qli);
                  	  if(bool){
                  		  qli.setSetLineToRsvpSrpFlag(false);
                  	  }
                 } 
      		 }
      		
      	 }
   	 }
   	
   }
   public boolean checkYTYAttrubute(LineItemParameter lip,QuoteLineItem qli ){
   	String oldovrrdPrice = lip.overridePrice;
       String preOvrrdPrice = lip.prevOvrrdPrice;
       if (StringUtils.isBlank(oldovrrdPrice)) oldovrrdPrice = "";
       if (StringUtils.isBlank(preOvrrdPrice)) preOvrrdPrice = "";
       if(!oldovrrdPrice.equals(preOvrrdPrice) ){
       	return true;
       }
       String discount = lip.discountPercent;
       String preDiscount = lip.prevDiscount;
       if (StringUtils.isBlank(discount)) discount = "";
       if (StringUtils.isBlank(preDiscount)) preDiscount = "";
       if(!discount.equals(preDiscount)){
       	return true;
       }
       if(lip.ytyRadio !=null && DraftQuoteParamKeys.YTY_RADIO_YTY_GROWTH_VALUE.equals(lip.ytyRadio)){
   		String yty = lip.yty;
       	String preYty = lip.prevYty;
       	if (StringUtils.isBlank(yty)) yty = "";
       	if (StringUtils.isBlank(preYty)) preYty = "";
       	if(!yty.equals(preYty)){
       		return true;
       	}
   	}
       return false;   
   }
   
	protected void validateTotalPartNumber(PostPartPriceTabContract contract) {
		List lineItems = new ArrayList();
		lineItems.addAll(quote.getMasterSoftwareLineItems());
		lineItems.addAll(quote.getSaaSLineItems());
		String key = "";
		QuoteLineItem lineItem = null;
		LineItemParameter lineItemParam = null;
		int origRQLineItemCount = 0;
		int totalPartNumber = 0;
		int limit = PartPriceConfigFactory.singleton().getElaLimits();
		String hdrrenwlquotenum = quote.getQuoteHeader().getRenwlQuoteNum();

		// if this is a special biddable renewal quote and the renewal quote
		// number in the quote header
		// is not null or blank then allow editing of quotes with 250 line items
		if (quote.getQuoteHeader().isSpBiddableRQ()
				&& (hdrrenwlquotenum != null && !"".equals(hdrrenwlquotenum))) {
			for (Iterator it = lineItems.iterator(); it.hasNext();) {
				lineItem = (QuoteLineItem) it.next();
				key = lineItem.getPartNum() + "_" + lineItem.getSeqNum();
				lineItemParam = (LineItemParameter) contract.getItems()
						.get(key);

				if (QuoteCommonUtil.isSkipLineItemUIValidate(lineItemParam,
						contract)) {
					continue;
				}

				// if the header RQ num is same as line item RQ number - then it
				// is from orig RQ
				// so increment the renewal quote line item count
				if (lineItem.getRenewalQuoteNum() != null && lineItem.getRenewalQuoteNum().equals(hdrrenwlquotenum)) {
					origRQLineItemCount++;
					if (origRQLineItemCount >= limit) {
						int addiYear = contract
								.getMaintainAdditionalYearInteger(key);
						if (addiYear != -1 || contract.isAddDuplicatePart()) {
							contract.setExceedLimit(true);
						} else {
							return;
						}

					}
				}
			}
		}
		for (Iterator it = lineItems.iterator(); it.hasNext();) {
			lineItem = (QuoteLineItem) it.next();
			key = lineItem.getPartNum() + "_" + lineItem.getSeqNum();

			lineItemParam = (LineItemParameter) contract.getItems().get(key);
			if (lineItemParam == null) {
				continue;
			}

			if ("0".equals(StringUtils.trim(lineItemParam.quantity))) {
				// user want to remove this line item , no need to check the
				// back-dating
				continue;
			}

			if (QuoteCommonUtil.isSkipLineItemUIValidate(lineItemParam,
					contract)) {
				continue;
			}

			// Master line item is one part
			totalPartNumber++;

			int addiYear = contract.getMaintainAdditionalYearInteger(key);

			if (addiYear == -1) {
				continue;
			}

			// Add additional year of maintenance coverage
			totalPartNumber += addiYear;
		}

		if (contract.isAddDuplicatePart()) {
			totalPartNumber++;
		}

		if (totalPartNumber > limit) {
			contract.setExceedLimit(true);
		}

		logContext.debug(this, "totalPartNumber:" + totalPartNumber);
		logContext.debug(this,
				"contract.getExceedLimit()" + contract.isExceedLimit());
	}

	protected boolean validateCmprssCvrage(HashMap vMap,
			PostPartPriceTabContract ct) {
		// No need to do below validation if cmprss cvrage not enabled
		if (!ct.isEnableCmprssCvrage()) {
			return true;
		}

		List masterItems = ct.getQuote().getMasterSoftwareLineItems();

		if (masterItems == null || masterItems.size() == 0) {
			return true;
		}

		for (Iterator it = masterItems.iterator(); it.hasNext();) {
			QuoteLineItem masterItem = (QuoteLineItem) it.next();

			String key = masterItem.getPartNum() + "_" + masterItem.getSeqNum();
			LineItemParameter lineItemParam = (LineItemParameter) ct.getItems()
					.get(key);

			if (QuoteCommonUtil.isSkipLineItemUIValidate(lineItemParam, ct)) {
				continue;
			}

			if (!validateCmprssCvrageDiscount(lineItemParam, vMap, ct)) {
				return false;
			}

			// If part is cmprss cvrage applied, coverage length should be less
			// than 12 month
			if (masterItem.hasValidCmprssCvrageMonth()) {
				// For cmprss cvrage applied parts, start date edit edit is not
				// allowed
				// Get the maint start date from DB
				Date startDate = masterItem.getMaintStartDate();
				Date endDate = ct.getPartEndDate(key);

				if (!validateCmprssCvrageCoverageLength(startDate, endDate,
						key, vMap, ct)) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean validateCmprssCvrageDiscount(LineItemParameter lineItem,
			HashMap vMap, PostPartPriceTabContract ct) {
		try {
			String cmprssCvrageDisc = lineItem.cmprssCvrageDisc;

			if (logContext instanceof QuoteLogContextLog4JImpl) {
				if (((QuoteLogContextLog4JImpl) logContext).isDebug(this)) {
					logContext.debug(this,
							"validating compressed coverage Discount for "
									+ lineItem.key + " " + cmprssCvrageDisc);
				}
			}
			if (!StringUtils.isBlank(cmprssCvrageDisc)) {
				double tmpDisc = Double.parseDouble(cmprssCvrageDisc);

				// Discount should between 0 and 100
				if (tmpDisc < 0) {
					FieldResult fieldResult = new FieldResult();
					fieldResult
							.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
									DraftQuoteMessageKeys.CMPRSS_CVRAGE_DISC_POSITIVE_MSG);
					fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							PartPriceViewKeys.CMPRSS_CVRAGE_DISC_ERROR_MSG_HDR);
					vMap.put(
							PartPriceViewKeys.PREFIX
									+ lineItem.key
									+ DraftQuoteParamKeys.PARAM_CMPRSS_CVRAGE_DISC_SUFFIX,
							fieldResult);

					addToValidationDataMap(ct, vMap);

					return false;
				} else if (tmpDisc > 100) {
					FieldResult fieldResult = new FieldResult();
					fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							DraftQuoteMessageKeys.CMPRSS_CVRAGE_DISC_RANGE_MSG);
					fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							PartPriceViewKeys.CMPRSS_CVRAGE_DISC_ERROR_MSG_HDR);
					vMap.put(
							PartPriceViewKeys.PREFIX
									+ lineItem.key
									+ DraftQuoteParamKeys.PARAM_CMPRSS_CVRAGE_DISC_SUFFIX,
							fieldResult);

					addToValidationDataMap(ct, vMap);

					return false;
				}
			}
		} catch (Exception e) {
			FieldResult fieldResult = new FieldResult();
			fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
					DraftQuoteMessageKeys.CMPRSS_CVRAGE_DISC_MSG);
			fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
					PartPriceViewKeys.CMPRSS_CVRAGE_DISC_ERROR_MSG_HDR);
			vMap.put(PartPriceViewKeys.PREFIX + lineItem.key
					+ DraftQuoteParamKeys.PARAM_CMPRSS_CVRAGE_DISC_SUFFIX,
					fieldResult);

			addToValidationDataMap(ct, vMap);

			return false;
		}

		return true;
	}

	private boolean validateCmprssCvrageCoverageLength(Date startDate,
			Date endDate, String key, HashMap vMap, ProcessContract ct) {
		if (startDate == null || endDate == null) {
			return true;
		}

		if (!(DateUtil.calculateFullCalendarMonths(startDate, endDate) < 12)) {
			FieldResult fieldResult = new FieldResult();
			fieldResult
					.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							SubmittedQuoteMessageKeys.CMPRSS_CVRAGE_SHORTER_THAN_ONE_YEAR);
			fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
					PartPriceViewKeys.END_DATE_HDR);
			vMap.put(PartPriceViewKeys.PREFIX + key
					+ DraftQuoteParamKeys.ovrdDtEndFlagSuffix, fieldResult);

			addToValidationDataMap(ct, vMap);

			return false;
		}

		return true;
	}

	private boolean validateProvsningDays(HashMap vMap,
			PostPartPriceTabContract ct) {
		List configrtnsList = quote.getPartsPricingConfigrtnsList();
		if (configrtnsList == null || configrtnsList.size() == 0) {
			return true;
		}
		for (Iterator iterator = configrtnsList.iterator(); iterator.hasNext();) {
			PartsPricingConfiguration confgrtn = (PartsPricingConfiguration) iterator
					.next();
			// it this configuration will be removed, skip the validation
			if (skipValidationForSpecificConfigrtn(ct, confgrtn)) {
				continue;
			}
			// if select do not co-term, skip the validation
			if (ct.getCoTermsMap() != null && ct.getCoTermsMap().size() > 0) {
				String coTermedConfigrtnId = (String) ct.getCoTermsMap().get(
						confgrtn.getConfigrtnId());
				if ((coTermedConfigrtnId == null
						|| "".equals(coTermedConfigrtnId)) && !ct.isTermExtension(confgrtn.getConfigrtnId())) {
					continue;
				}
			}
			String ctProvisngDays = ct.getPrvsningDays(confgrtn
					.getConfigrtnId());
			if(ct.isTermExtension(confgrtn.getConfigrtnId())){
				if(ctProvisngDays==null || ctProvisngDays.length()<=0){
					FieldResult fieldResult = new FieldResult();
					fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							DraftQuoteMessageKeys.PROVSNING_DAYS_MUST_MSG);
					fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							PartPriceViewKeys.ESTIMATED_PROVISIONING_DAYS);
					vMap.put(PartPriceViewKeys.PREFIX + confgrtn.getConfigrtnId()
							+ DraftQuoteParamKeys.PROVISIONING_DAYS, fieldResult);

					addToValidationDataMap(ct, vMap);
					return false;
				}
				
				if ("".equals(ctProvisngDays)) {
					FieldResult fieldResult = new FieldResult();
					fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							DraftQuoteMessageKeys.PROVSNING_DAYS_MUST_MSG);
					fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							PartPriceViewKeys.ESTIMATED_PROVISIONING_DAYS);
					vMap.put(PartPriceViewKeys.PREFIX + confgrtn.getConfigrtnId()
							+ DraftQuoteParamKeys.PROVISIONING_DAYS, fieldResult);

					addToValidationDataMap(ct, vMap);
					return false;
				}
				
				try {
					if (ctProvisngDays != null) {
						Integer.parseInt(ctProvisngDays);
					}
				} catch (Exception e) {
					FieldResult fieldResult = new FieldResult();
					fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							DraftQuoteMessageKeys.PROVSNING_DAYS_INVLD_MSG);
					fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							PartPriceViewKeys.ESTIMATED_PROVISIONING_DAYS);
					vMap.put(PartPriceViewKeys.PREFIX + confgrtn.getConfigrtnId()
							+ DraftQuoteParamKeys.PROVISIONING_DAYS, fieldResult);

					addToValidationDataMap(ct, vMap);
					return false;
				}
				
			}else if(this.showEstmtdPrvsningDays(confgrtn)){
				if(ctProvisngDays==null || ctProvisngDays.length()<=0){
					FieldResult fieldResult = new FieldResult();
					fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							DraftQuoteMessageKeys.PROVSNING_DAYS_MUST_MSG);
					fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							PartPriceViewKeys.ESTIMATED_PROVISIONING_DAYS);
					vMap.put(PartPriceViewKeys.PREFIX + confgrtn.getConfigrtnId()
							+ DraftQuoteParamKeys.PROVISIONING_DAYS, fieldResult);

					addToValidationDataMap(ct, vMap);
					return false;
				}
				
				if ("".equals(ctProvisngDays)) {
					FieldResult fieldResult = new FieldResult();
					fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							DraftQuoteMessageKeys.PROVSNING_DAYS_MUST_MSG);
					fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							PartPriceViewKeys.ESTIMATED_PROVISIONING_DAYS);
					vMap.put(PartPriceViewKeys.PREFIX + confgrtn.getConfigrtnId()
							+ DraftQuoteParamKeys.PROVISIONING_DAYS, fieldResult);

					addToValidationDataMap(ct, vMap);
					return false;
				}
				
				try {
					if (ctProvisngDays != null) {
						Integer.parseInt(ctProvisngDays);
					}
				} catch (Exception e) {
					FieldResult fieldResult = new FieldResult();
					fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							DraftQuoteMessageKeys.PROVSNING_DAYS_INVLD_MSG);
					fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							PartPriceViewKeys.ESTIMATED_PROVISIONING_DAYS);
					vMap.put(PartPriceViewKeys.PREFIX + confgrtn.getConfigrtnId()
							+ DraftQuoteParamKeys.PROVISIONING_DAYS, fieldResult);

					addToValidationDataMap(ct, vMap);
					return false;
				}
			}
			
		}
		return true;
	}
	
	protected boolean validateTermRnwlMdlCode(HashMap vMap,
			PostPartPriceTabContract ct) {
		Boolean isValidate = true;

		List configrtnsList = quote.getPartsPricingConfigrtnsList();
		if (configrtnsList == null || configrtnsList.size() == 0) {
			return true;
		}
		Map configrtnsMap = quote.getPartsPricingConfigrtnsMap();
		Iterator configrtnsIt = configrtnsList.iterator();
		while (configrtnsIt.hasNext()) {
			PartsPricingConfiguration ppc = (PartsPricingConfiguration) configrtnsIt
					.next();
			if (!PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(ppc
					.getConfigrtnActionCode()) || ! ct.isTermExtension(ppc.getConfigrtnId())) {
				continue;
			}

			// Checking if in initial term

			RedirectConfiguratorDataBasePack dataPack = null;
			try {
				dataPack = PartPriceHelper.calculateAddOnFinalizationTerm(quote
						.getQuoteHeader().getRefDocNum(), ppc.getConfigrtnId(),
						getProvsngDate(quote, ppc), ppc.getEndDate(), ppc
								.getConfigrtnActionCode(),false,ppc.getServiceDateModType());
				if (Integer.valueOf(dataPack.getFctToPaCalcTerm()) < 0) {
					return true;
				}
				List subSaaSlineItemList = (List) configrtnsMap.get(ppc);
				if (subSaaSlineItemList == null
						|| subSaaSlineItemList.size() == 0) {
					continue;
				}
				Iterator subSaaSlineItemIt = subSaaSlineItemList.iterator();
				ConfiguratorPartProcess process = ConfiguratorPartProcessFactory
						.singleton().create();
				Map<String, ConfiguratorPart> configrtrPartMap = process
						.findMainPartsFromChrgAgrm(quote.getQuoteHeader()
								.getRefDocNum(), ppc.getConfigrtnId());
				if (configrtrPartMap == null || configrtrPartMap.size() == 0)
					continue;
				while (subSaaSlineItemIt.hasNext()) {
					QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemIt
							.next();
					if (qli.isSaasSubscrptnPart() && !qli.isReplacedPart()) {
						ConfiguratorPart cp = configrtrPartMap.get(qli
								.getPartNum());
						if (cp == null)
							continue;
						if (!(PartPriceConstants.RenewalModelCode.R.equals(cp
								.getRenwlMdlCode())
								|| PartPriceConstants.RenewalModelCode.C
										.equals(cp.getRenwlMdlCode()) || PartPriceConstants.RenewalModelCode.O
								.equals(cp.getRenwlMdlCode()) || PartPriceConstants.RenewalModelCode.T.equals(cp.getRenwlMdlCode()))) {
							FieldResult fieldResult = new FieldResult();
							fieldResult
									.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
											DraftQuoteMessageKeys.TERM_RENEWAL_MODEL_INVLD_MSG);
							vMap.put(PartPriceViewKeys.PREFIX+ ppc.getConfigrtnId()
									+ DraftQuoteParamKeys.PARAM_RENEWAL_MODEL_CONFIGURTN,
									fieldResult);
							addToValidationDataMap(ct, vMap);
							return false;
						}

					}
				}
			} catch (Exception e) {
				FieldResult fieldResult = new FieldResult();
				fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						DraftQuoteMessageKeys.TERM_RENEWAL_MODEL_INVLD_MSG);
				vMap.put(PartPriceViewKeys.PREFIX + ppc.getConfigrtnId()
						+ DraftQuoteParamKeys.PARAM_RENEWAL_MODEL_CONFIGURTN,
						fieldResult);
				addToValidationDataMap(ct, vMap);
				return false;
			}
		}
		return isValidate;
	}
	
	protected boolean validateEstimatedOrderDate(HashMap vMap,
			PostPartPriceTabContract ct){

		List configrtnsList = quote.getPartsPricingConfigrtnsList();
		if (configrtnsList == null || configrtnsList.size() == 0) {
			return true;
		}
		Iterator configrtnsIt = configrtnsList.iterator();
		while (configrtnsIt.hasNext()) {
			PartsPricingConfiguration ppc = (PartsPricingConfiguration) configrtnsIt
					.next();
			
			if (!ct.isTermExtension(ppc.getConfigrtnId())) {
				continue;
			}
			
			if(ct.getEstmtdOrdDate()==null) {
				FieldResult fieldResult = new FieldResult();
				fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						DraftQuoteMessageKeys.MSG_ENTER_VALID_ESTMTD_ORD_DATE);
				fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						PartPriceViewKeys.ESTIMATED_ORDER_DATE);
				vMap.put(PartPriceViewKeys.PREFIX + ppc.getConfigrtnId()
						+ DraftQuoteParamKeys.PARAM_ESTMTD_ORD_DAY, fieldResult);
				addToValidationDataMap(ct, vMap);
				return false;
			}
		}
		return true;
	}
	protected boolean validateTermExtenstionDate(HashMap vMap,
			PostPartPriceTabContract ct){
		Boolean isValidate = true;

		List configrtnsList = quote.getPartsPricingConfigrtnsList();
		if (configrtnsList == null || configrtnsList.size() == 0) {
			return true;
		}
		Iterator configrtnsIt = configrtnsList.iterator();
		while (configrtnsIt.hasNext()) {
			PartsPricingConfiguration ppc = (PartsPricingConfiguration) configrtnsIt
					.next();
			// it this configuration will be removed, skip the validation
			if (skipValidationForSpecificConfigrtn(ct, ppc)) {
				continue;
			}
			
			if (!ct.isTermExtension(ppc.getConfigrtnId())) {
				continue;
			}
			
			if(ct.getServiceDateModType(ppc.getConfigrtnId())==null){
				FieldResult fieldResult = new FieldResult();
				fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						DraftQuoteMessageKeys.EXTENSION_TYPE_VALID_MESSAGE);
				fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						PartPriceViewKeys.EXT_SERVICE_DATE_TYPE);
				vMap.put(PartPriceViewKeys.PREFIX + ppc.getConfigrtnId()
						+ DraftQuoteParamKeys.EXTENSION_TYPE_SUFFIX, fieldResult);
				addToValidationDataMap(ct, vMap);
				return false;
			}
			
			ServiceDateModType sdm = ct.getServiceDateModType(ppc.getConfigrtnId());
			
			if(sdm.name().equals(ServiceDateModType.CE.name()) 
						&& quote.getQuoteHeader().isHasRampUpPartFlag()) {
				FieldResult fieldResult = new FieldResult();
				fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						DraftQuoteMessageKeys.RAMPUP_SERVICE_END_DATE_INVLD_MSG);
				fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						PartPriceViewKeys.EX_END_DATE);
				vMap.put(PartPriceViewKeys.PREFIX + ppc.getConfigrtnId()
						+ DraftQuoteParamKeys.SERVICE_DATE_MODTYPE, fieldResult);
				addToValidationDataMap(ct, vMap);
				
				return false;
			}
			
			//confirm the extension service type is "Service End Date". 
			if(!ct.getServiceDateModType(ppc.getConfigrtnId()).equals(ServiceDateModType.CE)){
				continue;
			}
			
			//To validate the extension service date limited range is more than current date 60 months.
			Date extensionServiceDate = ct.getServiceDate(ppc.getConfigrtnId());
			if(extensionServiceDate==null){
				FieldResult fieldResult = new FieldResult();
				fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						DraftQuoteMessageKeys.TERM_EXTENSION_DATE_INVLD_MSG);
				fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						PartPriceViewKeys.QUOTE_EXTENSION_DAY);
				vMap.put(PartPriceViewKeys.PREFIX + ppc.getConfigrtnId()
						+ DraftQuoteParamKeys.EXTENSION_DATE_SUFFIX, fieldResult);
				addToValidationDataMap(ct, vMap);
				return false;
			}
			
			//To validate the service end date - (estimated order date + provisioning days)>=0
			java.util.Date estimatedOrderDate = ct.getEstmtdOrdDate();
			Calendar estCal = Calendar.getInstance();
			estCal.setTime(estimatedOrderDate);
			
			Calendar endSvcDate = Calendar.getInstance();
			endSvcDate.setTime(extensionServiceDate);
			
			estCal.add(Calendar.DATE, Integer.valueOf(ct.getPrvsningDays(ppc.getConfigrtnId())));
			
			if (endSvcDate.compareTo(estCal)<0){
				FieldResult fieldResult = new FieldResult();
				fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						DraftQuoteMessageKeys.TERM_EXTENSION_DATE_INVLD_MSG);
				vMap.put(PartPriceViewKeys.PREFIX + ppc.getConfigrtnId()
						+ DraftQuoteParamKeys.EXTENSION_DATE_SUFFIX, fieldResult);
				addToValidationDataMap(ct, vMap);
				return false;
			}
			
			
			//Validate the the service end date is in from now to next 5 years
			Calendar curCal = Calendar.getInstance();
			curCal.setTime(new java.util.Date());
			Calendar extermDate = Calendar.getInstance();
			extermDate.setTime(extensionServiceDate);
			curCal.add(Calendar.YEAR, 5);
			curCal.set(Calendar.DATE, 1);
			extermDate.set(Calendar.DATE, 1);
			if(extermDate.after(curCal)){
				FieldResult fieldResult = new FieldResult();
				fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						DraftQuoteMessageKeys.TERM_EXTENSION_DATE_INVLD_MSG);
				fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
						PartPriceViewKeys.QUOTE_EXTENSION_DAY);
				vMap.put(PartPriceViewKeys.PREFIX + ppc.getConfigrtnId()
						+ DraftQuoteParamKeys.EXTENSION_DATE_SUFFIX, fieldResult);
				addToValidationDataMap(ct, vMap);
				return false;
			}
			
			if (!PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(ppc
					.getConfigrtnActionCode())) {
				continue;
			}

			// Calculate right service date:
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(ppc.getEndDate());
//			cal.add(Calendar.YEAR, 1);
//			Date rtServiceDate = new Date(cal.getTimeInMillis());
			if (isValidateEndDate(ppc,ct.isTermExtension(ppc.getConfigrtnId()))){
				
				Date cotermDate = getCotermEndDate(ppc,ct.getChargeAgreementNum());
				
				if (!isEndDateValid(cotermDate,extensionServiceDate)){
					FieldResult fieldResult = new FieldResult();
					fieldResult.setMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							DraftQuoteMessageKeys.END_DATE_LATER_THAN_EXISTING_MSG);
					fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							PartPriceViewKeys.QUOTE_EXTENSION_DAY);
					fieldResult.addArg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
							DateUtil.formatDate(cotermDate));
					vMap.put(PartPriceViewKeys.PREFIX + ppc.getConfigrtnId()
							+ DraftQuoteParamKeys.PARAM_EXTENSION_DAY, fieldResult);
					addToValidationDataMap(ct, vMap);
					return false;
				}
				
			}
			
		}
		return isValidate;
	}
	
	private static java.sql.Date getProvsngDate(Quote quote, PartsPricingConfiguration ppc) {
		java.util.Date estmtdOrdDate = quote.getQuoteHeader().getEstmtdOrdDate() == null ? DateUtil.getCurrentYYYYMMDDDate() : quote.getQuoteHeader().getEstmtdOrdDate();
		int provisngDays = ppc.getProvisngDays() == null ? 0 : ppc.getProvisngDays().intValue();
		Calendar estdPrvsngCal = Calendar.getInstance();//Estimated provisioning Date
		estdPrvsngCal.setTime(estmtdOrdDate);
		estdPrvsngCal.add(Calendar.DATE, provisngDays);//Estimated provisioning Date = estimated Order Date + item's provisioning days
		java.sql.Date provsngDate = new java.sql.Date(estdPrvsngCal.getTime().getTime());
		return provsngDate;
	}
	
	private boolean showEstmtdPrvsningDays(PartsPricingConfiguration configrtn){
	    if(configrtn == null){
            return false;
        }else{
            return configrtn.isAddOnTradeUp() || configrtn.isNewCaCoterm();
        }
	}
	
	private boolean skipValidationForSpecificConfigrtn(PostPartPriceTabContract ct, PartsPricingConfiguration confgrtn){
		PartsPricingConfiguration removeConfgrtn = null;

		if (ct.isRemoveConfgrtn()) {
			removeConfgrtn = QuoteCommonUtil.getPartsPricingConfigurationById(
					ct.getRemoveConfigrtnId(), quote);
			// it this configuration will be removed, skip the validation
			if (removeConfgrtn != null && removeConfgrtn.equals(confgrtn)) {
				return true;
			}
		}
		return false;
		
	}
	
	private boolean isValidateEndDate(PartsPricingConfiguration config,
			boolean isTermExtension) {
		boolean isValidate = true;

		if (!config.isAddOnTradeUp()) {
			isValidate = false;
		} else if (!isTermExtension) {
			isValidate = false;
		}
		return isValidate;
	}
	
	private boolean isEndDateValid(Date cotermEndDate,Date ctServiceEndDate){
		boolean isValid = true;
		
		//15.3 Enhancement,when do extension: 
		//Modify service end date less than or  equal to the existing end date will be looked as a violation 
		if (cotermEndDate != null && !cotermEndDate.before(ctServiceEndDate)){
			isValid =false;
		}
		return isValid;
	}
	
	
	private Date getCotermEndDate(PartsPricingConfiguration config,String caNumber) {

		Date coTermEndDate = null;
		// can't get CA
//		List caNumbers = config.getChargeAgreementList();
//		if (caNumbers == null || caNumbers.size() < 1) {
//			coTermEndDate = null;
//		}

		// can't get co-term
		Map<String, List> map = config.getCotermMap();
		List coTermConfigutns = map.get(caNumber);

		if (coTermConfigutns == null || coTermConfigutns.size() < 1) {
			coTermEndDate = null;
		}

		Iterator it = coTermConfigutns.iterator();
		while (it.hasNext()) {
			CoTermConfiguration coTermConfig = (CoTermConfiguration) it.next();
			if (config.getConfigrtnId().equals(
					coTermConfig.getCotermConfigrtnId())) {
				coTermEndDate = coTermConfig.getEndDate();
				break;
			}
		}
		return coTermEndDate;
	}

}
