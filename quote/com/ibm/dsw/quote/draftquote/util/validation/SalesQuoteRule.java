
package com.ibm.dsw.quote.draftquote.util.validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.ServiceDateModType;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.configurator.domain.CotermParameter;
import com.ibm.dsw.quote.configurator.domain.DomainAdapter;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcess;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.contract.PostPartPriceTabContract;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: May 22, 2007
 */

public abstract class SalesQuoteRule extends ValidationRule {


    SalesQuoteRule(Quote quote,PostPartPriceTabContract ct){
        super(quote,ct);
    }

    protected void validateOverrideDate(QuoteLineItem item) throws TopazException{
        String key = createKey(item);
        ValidationResult vr = getValidationResult(key);
        String ovrdStartDateFlag = ct.getOvrdStartDateFlag(key);
        String ovrdEndDateFlag = ct.getOvrdEndDateFlag(key);

        logContext.debug(this,"validateOverrideDate, Ovrd Start Flag = "+ovrdStartDateFlag);
        logContext.debug(this,"validateOverrideDate, Ovrd End Flag = "+ovrdEndDateFlag);
        if(null != ovrdStartDateFlag){

            if(DraftQuoteParamKeys.OVRD_DATE_FLAG.equals(ovrdStartDateFlag))
            {
                item.setStartDtOvrrdFlg(true);

                vr.dateOverrided = true;
            }
            if(DraftQuoteParamKeys.UN_OVRD_DATE_FLAG.equals(ovrdStartDateFlag))
            {
                item.setStartDtOvrrdFlg(false);

            }
        }

        if (null != ovrdEndDateFlag){

            if(DraftQuoteParamKeys.OVRD_DATE_FLAG.equals(ovrdEndDateFlag))
            {
                item.setEndDtOvrrdFlg(true);
                vr.dateOverrided = true;

            }
            if(DraftQuoteParamKeys.UN_OVRD_DATE_FLAG.equals(ovrdEndDateFlag))
            {
                item.setEndDtOvrrdFlg(false);
            }
        }


    }

    protected void removeZeroQtyItems(){
    	List items = quote.getLineItemList();

    	for(Iterator it = items.iterator(); it.hasNext(); ){
    		QuoteLineItem item = (QuoteLineItem)it.next();

    		if(item.getPartQty() != null && item.getPartQty().intValue() == 0){
    			it.remove();
    		}
    	}

    	//delete from master line items list
    	List masterItems = quote.getMasterSoftwareLineItems();
    	if(masterItems == null){
    		return;
    	}

    	for(Iterator it = masterItems.iterator(); it.hasNext(); ){
    		QuoteLineItem masterItem = (QuoteLineItem)it.next();

    		if(masterItem.getPartQty() != null && masterItem.getPartQty().intValue() == 0){
    			it.remove();
    		}
    	}

    }


    /**
     * @param item
     * @throws TopazException
     */
    protected boolean validateQuantity(QuoteLineItem item) throws TopazException {
        String key = createKey(item);
        ValidationResult vr = getValidationResult(key);
        //if not exist this line item in contract and qty is 0, delete this part, set the delete flag true
        if(!isPartParamExist(key) && (item.getPartQty() != null && item.getPartQty().intValue() == 0)){
        	vr.partDeleted = true;
            deleteLineItemAndConfig(item);
        	return true;
        }

        String strQuantity = isPartParamExist(key) ? ct.getPartQty(key) : "";


        if ((null == strQuantity) ) {
            if (item.getPartQty() != null){
                itemCountChanged = true;
            }
            return false;
        }
        // if the value is blank, just leave it
        if("".equals(strQuantity)){
            if (item.getPartQty() != null){
                itemCountChanged = true;
            }

            item.setPartQty(null);

            return false;
        }
        int qty = ct.getPartQtyInteger(key);
        // if quantity is 0, delete it
        if (0 == qty) {
            vr.partDeleted = true;

            deleteLineItemAndConfig(item);

            return true;
        }

        if(item.getPartQty() != null){
            int dbQty = item.getPartQty().intValue();
            if (dbQty != qty) {
                item.setPartQty(new Integer(qty));
                vr.quantityChanged = true;

            }
        }
        else{
            item.setPartQty(new Integer(qty));
            vr.quantityChanged = true;
        }

        return false;
    }


    /**
     * @param item
     */
    protected void validateSortOrder(QuoteLineItem item) throws TopazException {
        if(!isPartParamExist(createKey(item))){
        	return;
        }
    	int order = ct.getManualSortOrderInteger(createKey(item));
        if(order != item.getManualSortSeqNum()){
            item.setManualSortSeqNum(order);
        }

    }

    protected boolean processEnableCmprssCvrage(List masterItems) throws TopazException{
        boolean hasEligiableItems = false;

        for (Iterator masterIt = masterItems.iterator(); masterIt.hasNext();) {
            QuoteLineItem masterItem = (QuoteLineItem) masterIt.next();

            //if part is to be deleted then skip validation
            if(masterItem.getPartQty() != null && masterItem.getPartQty().intValue() == 0){
            	continue;
            }

            //If not eligiable for compressed coverage
            if(!masterItem.isEligibleForCmprssCvrage()){
                continue;
            }

            hasEligiableItems = true;

            String key = createKey(masterItem);
            ValidationResult vr = getValidationResult(key);

            String strCmprssCvrageMonth = ct.getCmprssCvrageMonth(key);
            logContext.debug(this, "compressed coverage month for part " + key + " is " + strCmprssCvrageMonth);

            //Cmprss cvrage month changed, need to recalculate price
            if(valueChanged(strCmprssCvrageMonth, masterItem.getCmprssCvrageMonth())){
                if(StringUtils.isBlank(strCmprssCvrageMonth)){
                    masterItem.setCmprssCvrageMonth(new Integer(0));
                } else {
                    //We have validation logic in UI validators, the value must be Integer
                    masterItem.setCmprssCvrageMonth(new Integer(strCmprssCvrageMonth));
                }

                vr.cmprssCvrageMonthChanged = true;
            }

            //Default cmprss cvrage months to actual coverage month, this happens when first enable cmprss cvrage
            if(StringUtils.isBlank(strCmprssCvrageMonth) && !masterItem.hasValidCmprssCvrageMonth()){
                //For parts added from part search, default cmprss cvrage to 12
                int months = PartPriceConstants.MAX_CMPRSS_CVRAGE_MONTH;

                //For parts referencing a renewal quote, default to the original coverage period
                if(StringUtils.isNotBlank(masterItem.getRenewalQuoteNum())){
                    if (masterItem.isEligibleForCmprssCvrage()&& (masterItem.getMaintEndDate() != masterItem.getOrigEndDate())) {
                        months = DateUtil.calculateWholeMonths(masterItem.getOrigStDate(), masterItem.getMaintEndDate());
                        } else {
                        months = DateUtil.calculateWholeMonths(masterItem.getOrigStDate(), masterItem.getOrigEndDate());
                        }
                }
                logDebug("Set cmprss cvrage month to " + months + " for line item: " + key);
                masterItem.setCmprssCvrageMonth(new Integer(months));

                vr.cmprssCvrageMonthChanged = true;
            }

            String strCmprssCvrageDisc = ct.getCmprssCvrageDisc(key);
            logContext.debug(this, "compressed coverage discount for part " + key + " is " + strCmprssCvrageDisc);

            //Cmprss cvrage discount changed, need to recalculate price
            if(valueChanged(strCmprssCvrageDisc, masterItem.getCmprssCvrageDiscPct())){
                vr.cmprssCvrageDiscChanged = true;
            }

            if(StringUtils.isBlank(strCmprssCvrageDisc)){
                masterItem.setCmprssCvrageDiscPct(null);
            } else {
                //We have validation logic in UI validators, the value must be Double
                masterItem.setCmprssCvrageDiscPct(new Double(strCmprssCvrageDisc));
            }
        }

        return hasEligiableItems;
    }

    protected void processDisableCmprssCvrage(List masterItems) throws TopazException{

        for (Iterator masterIt = masterItems.iterator(); masterIt.hasNext();) {
            QuoteLineItem masterItem = (QuoteLineItem) masterIt.next();

            //if part is to be deleted then skip validation
            if(masterItem.getPartQty() != null && masterItem.getPartQty().intValue() == 0){
            	continue;
            }

            //This item is not cmprss cvrage enabled previously
            if(!masterItem.hasValidCmprssCvrageMonth()){
                continue;
            }

            String key = createKey(masterItem);
            ValidationResult vr = getValidationResult(key);

            if(masterItem.hasValidCmprssCvrageMonth() ){
                masterItem.setCmprssCvrageMonth(new Integer(0));
                vr.cmprssCvrageMonthChanged = true;
            }

            if(masterItem.hasValidCmprssCvrageDiscPct() ){
                masterItem.setCmprssCvrageDiscPct(new Double(0.0));
                vr.cmprssCvrageDiscChanged = true;
            }

            if(masterItem.getOverrideUnitPrc() != null){
                masterItem.setOverrideUnitPrc(null);
                vr.unitPriceOrDiscountChanged = true;
            }

            if(DecimalUtil.isNotEqual(masterItem.getLineDiscPct(), 0)){
                masterItem.setLineDiscPct(0);
                vr.unitPriceOrDiscountChanged = true;
            }

            //For line items referencing a renewal quote, reset date to original value
            if(StringUtils.isNotBlank(masterItem.getRenewalQuoteNum())){

                //Backgrounk info
                //1. For special biddable renewal parts, start date edit is not allowed
                //2. For parts added from renewal quote, start/end date edit is not allowed

                //Aleays need to reset maint start date to original value since rep will never be able
                //to manual reset maint start date
                masterItem.setStartDtOvrrdFlg(false);
                if(!DateUtil.isYMDEqual(masterItem.getMaintStartDate(), masterItem.getOrigStDate())){
                    masterItem.setMaintStartDate(masterItem.getOrigStDate());
                    vr.dateChanged = true;
                }

                //Reset maint end date for parts added from renewal quote
                //No action for special biddable renewal parts
                if(CommonServiceUtil.isSalesQuoteOtherRnwlPart(quote.getQuoteHeader(), masterItem)){
                    masterItem.setEndDtOvrrdFlg(false);
                    if(!DateUtil.isYMDEqual(masterItem.getMaintEndDate(), masterItem.getOrigEndDate())){
                        masterItem.setMaintEndDate(masterItem.getOrigEndDate());
                        vr.dateChanged = true;
                    }
                }
            }
        }
    }

    private boolean valueChanged(String newValue, Integer oldValue){
        if(oldValue == null){
            if(StringUtils.isNotBlank(newValue)){
                return true;
            } else {
                return false;
            }

        } else{
            if(StringUtils.isBlank(newValue)){
                return true;
            }

            int intNewValue = Integer.parseInt(newValue);

            if(intNewValue != oldValue.intValue()){
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean valueChanged(String newValue, Double oldValue){
        if(oldValue == null){
            if(StringUtils.isNotBlank(newValue)){
                return true;
            } else {
                return false;
            }

        } else{
            if(StringUtils.isBlank(newValue)){
                return true;
            }

            double dblNewValue = Double.parseDouble(newValue);

            if(DecimalUtil.isNotEqual(dblNewValue, oldValue.doubleValue())){
                return true;
            } else {
                return false;
            }
        }
    }

    protected void checkIfLineItemEligibleForCmprssCvrage(){
        List masterItems = quote.getMasterSoftwareLineItems();

        if(masterItems == null || masterItems.size() == 0){
            return;
        }

        for(Iterator it = masterItems.iterator(); it.hasNext(); ){
            QuoteLineItem qli = (QuoteLineItem)it.next();
            qli.determineEligibleForCmprssCvrage();
        }
    }

    protected void validateCmprssCvrage() throws TopazException{
        List masterItems = quote.getMasterSoftwareLineItems();

        if (masterItems == null || masterItems.size() == 0) {
            return;
        }

        boolean isToEnableCmprssCvrage = ct.isEnableCmprssCvrage();
        boolean hasBackDatingLineItems = quote.getQuoteHeader().getBackDatingFlag();

        //User needs to enable compressed coverage
        boolean hasEligibleItems = true;
        if(isToEnableCmprssCvrage && !hasBackDatingLineItems){
            hasEligibleItems = processEnableCmprssCvrage(masterItems);

        } else {

            processDisableCmprssCvrage(masterItems);
        }

        quote.getQuoteHeader().setCmprssCvrageFlag(isToEnableCmprssCvrage && !hasBackDatingLineItems);
        if(!hasEligibleItems){
            logContext.debug(this, "No line item in the quote is eligible for compressed coverage, clear the cmprss cvrage flag from quote header");

            processDisableCmprssCvrage(masterItems);
            quote.getQuoteHeader().setCmprssCvrageFlag(false);
        }
    }

    protected void validatePaymentTermDays() throws TopazException {
        int headerPymTermsDays = quote.getQuoteHeader().getPymTermsDays();
        int ctPymTermsDays = ct.getPymntTermsDays();
        if(headerPymTermsDays != ctPymTermsDays){
            this.pymTermsDaysChanged = true;
        }
    }

    protected void validateValidityDays() throws TopazException {
        if (ct.getExpireDate() == null) {
            return;
        }
        QuoteHeader quoteHeader = quote.getQuoteHeader();
        if (quoteHeader == null) {
            return;
        }
        Date quoteExpDate = quoteHeader.getQuoteExpDate();

        if (quoteExpDate == null) {
            return;
        }
        if (com.ibm.ead4j.common.util.DateHelper.singleton().daysDifference(ct.getExpireDate(), quoteExpDate) != 0) {
            this.validityDaysChanged = true;
        }
    }

    private boolean valueChanged(String newValue, String oldValue){
        if(newValue == null && oldValue == null){
        	return false;
        }else if(newValue == null && oldValue != null){
        	return true;
        }else if(newValue != null && oldValue == null){
        	return true;
        }else{
        	if(!newValue.equals(oldValue)){
        		return true;
        	}
        }
        return false;

    }

    private boolean serviceChanged(Boolean webTermExtension,Boolean ctTermExtension,ServiceDateModType webServiceDateModType,
    		ServiceDateModType ctServiceDateModType,Date webServiceDate,Date ctServiceDate){
    	if(webTermExtension != ctTermExtension){
    		return true;
    	}
    	
    	if(webServiceDateModType == null){
    		if(ctServiceDateModType != null){
    			return true;
    		}
    	}else{    		
    		if(!webServiceDateModType.equals(ctServiceDateModType)){
    			return true;
    		}
    	}
    	
    	if(webServiceDate == null ){
    		if(ctServiceDate != null){
    			return true;
    		}
    	}else{    		
    		if(!webServiceDate.equals(ctServiceDate)){
    			return true;
    		}
    	}
    	
    	return false;
    	
    }


    protected void processPrvsningDays(Quote quote, PostPartPriceTabContract ct) throws TopazException {
    	List configrtnsList = quote.getPartsPricingConfigrtnsList();
    	if(configrtnsList == null || configrtnsList.size() == 0){
    		return;
    	}
    	for (Iterator iterator = configrtnsList.iterator(); iterator.hasNext();) {
    		PartsPricingConfiguration confgrtn = (PartsPricingConfiguration) iterator.next();

    		String webProvisngDays = confgrtn.getProvisngDays() == null ? null : confgrtn.getProvisngDays().toString();
    		String ctProvisngDays = ct.getPrvsningDays(confgrtn.getConfigrtnId());
    		if(valueChanged(webProvisngDays, ctProvisngDays)){
    			Integer intProvisngDays = ctProvisngDays == null || "".equals(ctProvisngDays) ? null : new Integer(ctProvisngDays);
    			confgrtn.setProvisngDays(intProvisngDays);
    			confgrtn.setUserID(ct.getUserId());
    			this.provsningDaysChanged = true;
    		}

		}
    }
    
    protected void processServiceDate(Quote quote, PostPartPriceTabContract ct) throws TopazException{
    	List configrtnsList = quote.getPartsPricingConfigrtnsList();
    	if(configrtnsList == null || configrtnsList.size() == 0){
    		return;
    	}
    	for (Iterator iterator = configrtnsList.iterator(); iterator.hasNext();) {
    		PartsPricingConfiguration confgrtn = (PartsPricingConfiguration) iterator.next();
    		String configurationId = confgrtn.getConfigrtnId();
    	    
    	    Boolean webTermExtension = confgrtn.isTermExtension();
    	    Boolean ctTermExtension = ct.isTermExtension(configurationId);
    	    
    	    ServiceDateModType webServiceDateModType = confgrtn.getServiceDateModType();
    	    ServiceDateModType ctServiceDateModType = ct.getServiceDateModType(configurationId);
    	    
    	    java.sql.Date webServiceDate = confgrtn.getServiceDate();
    	    java.sql.Date ctServiceDate = ct.getServiceDate(configurationId);
    		
    		if(serviceChanged(webTermExtension,ctTermExtension,webServiceDateModType,
    	    		ctServiceDateModType,webServiceDate,ctServiceDate)){
    			
    			Boolean termExtension = (ctTermExtension == null ? false : ctTermExtension);
    			ServiceDateModType serviceDateModType = ctServiceDateModType;
    			java.sql.Date serviceDate = ctServiceDate;
    			
    			confgrtn.setTermExtension(termExtension);
    			confgrtn.setServiceDateModType(serviceDateModType);
    			confgrtn.setServiceDate(serviceDate);
    			confgrtn.setUserID(ct.getUserId());
    			this.serviceDateChanged = true;
    		}

		}
    }

    /**
     * @param quote
     * @param ct
     * process for co-terms, to co-term one configuration to another configuration
     * @throws TopazException
     */
    protected void processCoTerms(Quote quote, PostPartPriceTabContract ct) throws TopazException {
    	Map cotermMap = ct.getCoTermsMap();
    	if(cotermMap == null || cotermMap.size() == 0){
    		return;
    	}
    	coTermChanged = true;
    	for (Iterator iterator = cotermMap.keySet().iterator(); iterator.hasNext();) {
			String orignlConfigrtnId = (String) iterator.next();
			PartsPricingConfiguration orignlConfigrtn = QuoteCommonUtil.getPartsPricingConfigurationById(orignlConfigrtnId, quote);
			String cotermConfigrtnId = (String) cotermMap.get(orignlConfigrtnId);
			processCoTermsLineItems(cotermConfigrtnId, orignlConfigrtn, quote, ct.getChargeAgreementNum());
		}


    }

    /**
     * @param cotermConfigrtnId
     * @param orignlonfigrtn
     * @param quote
     * process for co-term items, to co-term one configuration to another configuration
     * TODO:need add detail logic
     * @throws TopazException
     */
    protected void processCoTermsLineItems(String cotermConfigrtnId, PartsPricingConfiguration orignlConfigrtn, Quote quote, String chargeAgreementNum) throws TopazException {
    	try {
    		if(orignlConfigrtn == null){
    			return;
    		}
    		List saasList = (List)quote.getPartsPricingConfigrtnsMap().get(orignlConfigrtn);
    		//if do-not-coterm is selected, update the relatedCotermLineItmNum to null
    		if(chargeAgreementNum == null || "".equals(chargeAgreementNum)
    			|| cotermConfigrtnId == null || "".equals(cotermConfigrtnId)){
    			for (Iterator iterator = saasList.iterator(); iterator.hasNext();) {
    				QuoteLineItem qli = (QuoteLineItem) iterator.next();
    				if((qli.isSaasSubscrptnPart() && !qli.isReplacedPart()) || qli.isSaasSetUpPart()){
    					qli.setRelatedCotermLineItmNum(null);
    				}
    			}
    			orignlConfigrtn.setEndDate(null);
    			orignlConfigrtn.setCotermConfigrtnId(null);
    			orignlConfigrtn.setUserID(ct.getUserId());
    			//a co-termed configuration was set to no longer co-term on the parts and pricing tab
    			//Setting the "configurator last mod date" to null would trigger quote submission to force that configuration to go to the configurator
    			if(orignlConfigrtn.isNewCaCoterm()
    				&& chargeAgreementNum != null && !"".equals(chargeAgreementNum)){
    				orignlConfigrtn.setConfigrtnModDate(null);
    			}
    			String configrtnActionCode;
    			if(chargeAgreementNum == null || "".equals(chargeAgreementNum)){
    				configrtnActionCode = PartPriceConstants.ConfigrtnActionCode.NEW_NCT;
    			}else{
    				configrtnActionCode = PartPriceConstants.ConfigrtnActionCode.NEW_CA_NCT;
    			}
    			orignlConfigrtn.setConfigrtnActionCode(configrtnActionCode);

				if (!orignlConfigrtn.isTermExtension()) {
					orignlConfigrtn.setProvisngDays(null); // Do not clear it when Modify service dates is yes.
				}
    		}else{
				ConfiguratorPartProcess configPartPrcss = ConfiguratorPartProcessFactory.singleton().create();
				List<DomainAdapter> replacedParts = new ArrayList();
				for (Iterator iterator = saasList.iterator(); iterator.hasNext();) {
					QuoteLineItem qli = (QuoteLineItem) iterator.next();
					if(qli.isReplacedPart()){
						replacedParts.add(DomainAdapter.create(qli));
					}
				}
				CotermParameter ctParam = configPartPrcss.getCotermToPartInfo(chargeAgreementNum, cotermConfigrtnId, replacedParts,null);
				if(ctParam == null){
					return;
				}

				for (Iterator iterator = saasList.iterator(); iterator.hasNext();) {
					QuoteLineItem qli = (QuoteLineItem) iterator.next();
					if((qli.isSaasSubscrptnPart() && !qli.isReplacedPart()) || qli.isSaasSetUpPart() || qli.isSaasSubsumedSubscrptnPart()){
						qli.setRelatedCotermLineItmNum(ctParam.getRefDocLineItemSeqNum());
					}
				}
				orignlConfigrtn.setEndDate(ctParam.getEndDate());
				orignlConfigrtn.setCotermConfigrtnId(cotermConfigrtnId);
				orignlConfigrtn.setUserID(ct.getUserId());
				//2)not co-termed configuration set to co-termed
				//	set the CONFIGRTN_MOD_DATE to current date
				//3)co-termed configuration is changed to another co-termed configuration
				//	set the CONFIGRTN_MOD_DATE to current date
				if((orignlConfigrtn.isNewCaNoCoterm() || orignlConfigrtn.isNewNoCaNoCoterm())
					|| (orignlConfigrtn.isNewCaCoterm() && !cotermConfigrtnId.equals(orignlConfigrtn.getCotermConfigrtnId()))){
					orignlConfigrtn.setConfigrtnModDate(DateUtil.getCurrentDate());
				}
				orignlConfigrtn.setConfigrtnActionCode(PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT);
				if(orignlConfigrtn.getProvisngDays() == null){
					orignlConfigrtn.setProvisngDays(orignlConfigrtn.getProvisngDaysDefault());
				}
    		}
		} catch (QuoteException e) {
			throw new TopazException(e);
		}
    }

    protected void processEstmtdOrdDate(Quote quote, PostPartPriceTabContract ct) throws TopazException {
    	java.util.Date ctEstmtdOrdDate = ct.getEstmtdOrdDate();
    	if(ctEstmtdOrdDate != null){
    		this.estmtdOrdDateChanged = true;
    	}
    }

    private void logDebug(String debugMsg){
    	if(logContext instanceof QuoteLogContextLog4JImpl){
			if(((QuoteLogContextLog4JImpl)logContext).isDebug(this)){
				logContext.debug(this, debugMsg);
			}
		}
	 }


}
