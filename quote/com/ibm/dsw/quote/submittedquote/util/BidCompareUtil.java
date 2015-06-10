package com.ibm.dsw.quote.submittedquote.util;

import is.domainx.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.BidCompareConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.BidCompareConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteClassificationCodeFactory;
import com.ibm.dsw.quote.common.domain.QuoteContact;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuotePriceTotals;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo.SpecialBidQuestion;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;

/**
 * Copyright 2011 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>BidCompareUtil<code> class.
 *    
 * @author: zyuyang@cn.ibm.com
 * 
 * Creation date: January 22, 2011
 */

public class BidCompareUtil {

	public static final String LABEL_YES = "Yes";
	public static final String LABEL_NO = "No";
	
	public static List doCompare(List fields, Object object1, Object object2,
			Locale locale) throws Exception {
		List result = new ArrayList();
		if (object1 == null && object2 == null) {
			return result;
		}
		Object[] objectArr = null;
		Object value1 = null, value2 = null;
		boolean flag = false;
		Iterator i = fields.iterator();
		while (i.hasNext()) {
			Map map = (Map) i.next();

			value1 = getPropertyValue(object1, map);
			value2 = getPropertyValue(object2, map);

			if (value1 == null && value2 == null) {
				continue;
			} else {
				flag = (value1 == null) ? isEquals(value2, value1) : isEquals(
						value1, value2);

				if(map.get(BidCompareConstants.ID).equals("partNum")){
					Object[] objArr = new Object[4];
					objArr[1] = value1;
					objArr[2] = value2;
					objArr[0] = map.get(BidCompareConstants.LABEL);
					objArr[3] = map.get(BidCompareConstants.ID);
					result.add(objArr);
				}else if (!flag) { // 
					objectArr = getContentDescription(map, object1, object2,
							locale, false);
					if (objectArr != null && objectArr.length > 0) {
						result.add(objectArr);
					}
				}
			}
		}

		return result;
	}

	public static List compareQuoteHead(Quote copiedQuote, Quote originalQuote,
			Locale locale) throws Exception {
		List result = new ArrayList();
		List fields = BidCompareConfigFactory.singleton()
				.getQuoteHeaderConfig();
		addCompareResult(result,doCompare(fields, copiedQuote.getQuoteHeader(), originalQuote
				.getQuoteHeader(), locale));
		addCompareResult(result,compareOppNumInfo(copiedQuote, originalQuote,locale));
		addCompareResult(result,compareQuoteContact(copiedQuote, originalQuote,locale));
		addCompareResult(result,compareChannelTotalPrice(copiedQuote, originalQuote,locale));
		return result;
	}

	private static List compareQuoteContact(Quote copiedQuote, Quote originalQuote,
			Locale locale) throws Exception {
		List copiedQuoteContactList = copiedQuote.getContactList();
		List originalQuoteContactList = originalQuote.getContactList();
		
		QuoteContact copiedQuoteContact = (QuoteContact) (copiedQuoteContactList != null
				&& copiedQuoteContactList.size() > 0 ? copiedQuoteContactList
				.get(0) : null);
		QuoteContact originalQuoteContact = (QuoteContact) (originalQuoteContactList != null
				&& originalQuoteContactList.size() > 0 ? originalQuoteContactList
				.get(0) : null);
		
		List fields = BidCompareConfigFactory.singleton().getQuoteContactConfig();
		return doCompare(fields, copiedQuoteContact,originalQuoteContact, locale);
	}
	
	private static List compareOppNumInfo(Quote copiedQuote, Quote originalQuote,
			Locale locale) throws Exception {
		String copiedOppNumInfo = getOppNumInfo(copiedQuote.getQuoteHeader(), locale);
		String originalOppNumInfo = getOppNumInfo(originalQuote.getQuoteHeader(), locale);
		List otherFieldsConfigFields = BidCompareConfigFactory.singleton().getOtherFieldsConfig();
		Map map = null;
		for (int i = 0; i < otherFieldsConfigFields.size(); i++) {
			map = (Map) otherFieldsConfigFields.get(i);
			if (map != null && map.get(BidCompareConstants.ID).equals("oppNumInfo")) {
				return doCompareotherFields(map, copiedOppNumInfo,
						originalOppNumInfo, locale);
			}
		}
		return null;
	}
	
	public static List comparePromotionList(Quote copiedQuote, Quote originalQuote,
			Locale locale) throws Exception {
		List result = new ArrayList();
		List copiedPromotionsList = copiedQuote.getPromotionsList();
		List originalPromotionsList = originalQuote.getPromotionsList();
		List otherFieldsConfigFields = BidCompareConfigFactory.singleton().getOtherFieldsConfig();
		Map promotionsConfig = null;
		boolean configflag = true;
		for (int i = 0; i < otherFieldsConfigFields.size(); i++) {
			promotionsConfig = (Map) otherFieldsConfigFields.get(i);
			if (promotionsConfig != null
					&& promotionsConfig.get(BidCompareConstants.ID).equals("promotion")) {
				configflag = false;
				break;
			}
		}
		if(configflag){
			return null;
		}
		Map originalPromotions = new HashMap(originalPromotionsList.size());
		String[] promotion = null;
		for(int i = 0; i < originalPromotionsList.size(); i++){
			promotion = (String[])originalPromotionsList.get(i);
			originalPromotions.put(promotion[1], promotion[1]);
		}
		for(int i = copiedPromotionsList.size()-1; i >= 0 ; i--){
			if(originalPromotions.remove(copiedPromotionsList.get(i)) != null){
				copiedPromotionsList.remove(i);
			}
		}
		Object[] temp = originalPromotions.keySet().toArray();
		int minSize = temp.length < copiedPromotionsList.size()? temp.length : copiedPromotionsList.size();
		for(int i = 0; i < minSize; i++){
			addCompareResult(result,doCompareotherFields(promotionsConfig, ((String[])copiedPromotionsList.get(i))[1], (String)temp[i], locale));
		}
		for(int i = minSize; i < temp.length; i++){
			addCompareResult(result,doCompareotherFields(promotionsConfig, null, (String)temp[i], locale));
		}
		for(int i = minSize; i < copiedPromotionsList.size(); i++){
			addCompareResult(result,doCompareotherFields(promotionsConfig, ((String[])copiedPromotionsList.get(i))[1], null, locale));
		}
		return result;
	}
	
	private static List compareChannelTotalPrice(Quote copiedQuote, Quote originalQuote,
			Locale locale) throws Exception {
		Double copiedChannelTotPrice = getChannelTotalPrice(copiedQuote, locale);
		Double originalChannelTotPrice = getChannelTotalPrice(originalQuote, locale);
		List otherFieldsConfigFields = BidCompareConfigFactory.singleton().getOtherFieldsConfig();
		Map map = null;
		for (int i = 0; i < otherFieldsConfigFields.size(); i++) {
			map = (Map) otherFieldsConfigFields.get(i);
			if (map != null && map.get(BidCompareConstants.ID).equals("chnlTotPrice")) {
				return doCompareotherFields(map, copiedChannelTotPrice,
						originalChannelTotPrice, locale);
			}
		}
		return null;
	}
	
	public static List compareLineItems(Quote copiedQuote, Quote originalQuote,
			Locale locale) throws Exception {
		List fields = BidCompareConfigFactory.singleton()
				.getQuoteLineItemConfig();
		return doCompareLineItems(fields, copiedQuote.getLineItemList(),
				originalQuote.getLineItemList(), locale);
	}

	public static List compareSpecialBid(Quote copiedQuote,
			Quote originalQuote, Locale locale) throws Exception {
		List result = new ArrayList();
		List fields = BidCompareConfigFactory.singleton().getQuoteUserAccessConfig();
		List temp = doCompare(fields, copiedQuote.getQuoteUserAccess(), originalQuote
				.getQuoteUserAccess(), locale);
		addCompareResult(result,temp); 
		
		fields = BidCompareConfigFactory.singleton()
				.getSpecialBidInfoConfig();
		temp = doCompare(fields, copiedQuote.getSpecialBidInfo(),
				originalQuote.getSpecialBidInfo(), locale);
		addCompareResult(result,temp);
		
		fields = BidCompareConfigFactory.singleton().getExecSummaryConfig();
		temp = doCompare(fields, copiedQuote.getExecSummary(), originalQuote
				.getExecSummary(), locale);
		addCompareResult(result,temp);
		
		return result;
	}
	
	public static List compareSpecialBidQuestion(Quote copiedQuote,
			Quote originalQuote, Locale locale) throws Exception{
		List fields = BidCompareConfigFactory.singleton().getSpecialBidQustnConfig();
		List result = new ArrayList();
		List copiedQuestion = copiedQuote.getSpecialBidInfo().getQuestions();
		List originalQuestion = originalQuote.getSpecialBidInfo().getQuestions();
		Map originalMap = new HashMap(originalQuestion.size());
		Map resultMap = null;
		int length = originalQuestion.size();
		SpecialBidQuestion question = null;
		for(int i = 0; i < length; i++){
			question = (SpecialBidQuestion)originalQuestion.get(i);
			if(question != null){
				originalMap.put(question.getConfigNum(),question);
			}
		}
		List subResult;
		for(int i = 0; i < copiedQuestion.size(); i++){
			question = (SpecialBidQuestion)copiedQuestion.get(i);
			if(question != null){
				subResult = doCompare(fields, question, originalMap.remove(question.getConfigNum()), locale);
				if(subResult != null && subResult.size() > 0){
					resultMap = new HashMap(2);
					resultMap.put(BidCompareConstants.QUESTION_TEXT, question.getQuestionText());
					resultMap.put(BidCompareConstants.DIFFERENCE, subResult);
					result.add(resultMap);
				}
			}
		}
		Iterator iterator = originalMap.entrySet().iterator();
		while (iterator.hasNext()) {
			question = (SpecialBidQuestion) (((Map.Entry) iterator.next()).getValue());
			subResult = doCompare(fields, null, question, locale);
			if(subResult != null && subResult.size() > 0){
				resultMap = new HashMap(2);
				resultMap.put(BidCompareConstants.QUESTION_TEXT, question.getQuestionText());
				resultMap.put(BidCompareConstants.DIFFERENCE, subResult);
				result.add(resultMap);
			}
			
		}
		return result;
	}
	
	private static List addCompareResult(List result, List subResult){
		if (subResult.size() > 0) {
			result.addAll(subResult);
		}
		return result;
	}
	
	public static boolean isEquals(Object object1, Object object2) {
		if (object1 instanceof Country) {
			return ((Country) object1).getCode3().equals(
					((Country) object2).getCode3());
		} else if (object1 instanceof CodeDescObj) {
			return ((CodeDescObj) object1).getCode().equals(
					((CodeDescObj) object2).getCode());
		} else if (object1  instanceof String){
			return StringUtils.strip((String)object1).equals(StringUtils.strip((String)object2));
		} else {
			return object1.equals(object2);
		}
	}
	
	public static Object formatDescription(String format, Object object, Map map,
			Locale locale, boolean isProperty) throws Exception{
		if(object == null){
			return null;
		}
		Object source = isProperty? object : getPropertyValue(object, map);
		if (source == null) {
			return null;
		} else if (StringUtils.equalsIgnoreCase(format, "currency")) {
			return formatPrice((Double) source);
		} else if (StringUtils.equalsIgnoreCase(format, "percent")) {
			return formatPercent((Double) source);
		} else if (StringUtils.equalsIgnoreCase(format, "Date")) {
			return formatDate((java.util.Date)source, locale);
		} else if (StringUtils.equalsIgnoreCase(format, "countryDesc")) {
			return ((Country) source).getDesc();
		} else if (StringUtils.equalsIgnoreCase(format, "codeDesc")) {
			return ((CodeDescObj) source).getCodeDesc();
		} else if (StringUtils.equalsIgnoreCase(format, "salesDiscTypeCode")) {
			return formatSalesDiscTypeCode((String) source);
		} else if (StringUtils.equalsIgnoreCase(format, "toBoolean")) {
			return toBoolean((Integer) source);
		} else if (StringUtils.equalsIgnoreCase(format, "point")) {
			return formatPoint((Double)source);
		} else if (StringUtils.equalsIgnoreCase(format, "truefalseToyesno")) {
			if((Boolean)source){
				return LABEL_YES;
			}else{
				return LABEL_NO;
			}
		} else if (StringUtils.equalsIgnoreCase(format, "10Toyesno")){
			if((Integer)source == 1){
				return LABEL_YES;
			}else if((Integer)source == 0){
				return LABEL_NO;
			}
		} else if (StringUtils.equalsIgnoreCase(format, "21Toyesno")){
			if(StringUtils.trim((String)source).equals("2")){
				return LABEL_YES;
			}else if(StringUtils.trim((String)source).equals("1")){
				return LABEL_NO;
			}
		} else if (StringUtils.equalsIgnoreCase(format, "acqrtn")) {
			return getAcqstnDescByCode((String)source);
		} else if (StringUtils.equalsIgnoreCase(format, "priceLevel")) {
			return getPriceLevelDesc((String)source);
		} else if (StringUtils.equalsIgnoreCase(format, "busOrg")) {
			return getBusinessOrgByCode((String)source);
		} else if (StringUtils.equalsIgnoreCase(format, "ordgMethodCodeDesc")){
			return ordgMethodCodeDesc((String)source);
		} else if (StringUtils.equalsIgnoreCase(format, "oemBidTypeDesc")){
			return oemBidTypeDesc(source.toString());
		} else if (StringUtils.equalsIgnoreCase(format, "quoteClassfctnCodeDesc")){
			return quoteClassfctnCodeDesc((String)source);
		} else if (StringUtils.equalsIgnoreCase(format, "spBidCustIndustryDesc")){
			return getSpBidCustIndustryDesc((String)source);
		} else if (StringUtils.equalsIgnoreCase(format, "billingFrequency")){
			return formatBillgFrqncy((QuoteLineItem)object, (String)source);
		} else if (StringUtils.equalsIgnoreCase(format, "cvrageTerm")){
			return formatCvrageTerm((QuoteLineItem)object, (Integer)source);
		} else if (StringUtils.equalsIgnoreCase(format,"totalTerm")){
			return formatTotalTerm((Integer)source);
		} else if("".equals(source)){
			return null;
		}
		// StringUtils.isBlank(format)
		return source;
	}
	
	public static Object[] getContentDescription(Map map, Object object1,
			Object object2, Locale locale, boolean isProperty) throws Exception{
		Object[] objArr = new Object[4];

		String format = (String) map.get(BidCompareConstants.FORMAT);
		objArr[1] = formatDescription(format, object1, map, locale, isProperty);
		objArr[2] = formatDescription(format, object2, map, locale, isProperty);
		if ((objArr[1] == objArr[2]) || (objArr[1] != null && objArr[1].equals(objArr[2]))
				|| (objArr[2] != null && objArr[2].equals(objArr[1]))) {
			return new Object[0];
		}
		objArr[0] = map.get(BidCompareConstants.LABEL);
		objArr[3] = map.get(BidCompareConstants.ID);
		return objArr;
	}

	public static List doCompareLineItems(List fields, List newLineItemsList,
			List originalLineItemslist, Locale locale) throws Exception {
		List result = new ArrayList();
		List subResult;
		Map originalLineItemsMap = CommonServiceUtil
				.getSoftwareLineItemMap(originalLineItemslist);
		QuoteLineItem originalLineItem, newLineItem;

		for (int i = 0; i < newLineItemsList.size(); i++) {
			newLineItem = (QuoteLineItem) newLineItemsList.get(i);
			originalLineItem = (QuoteLineItem) originalLineItemsMap
					.remove(CommonServiceUtil.createSoftwareLineItemKey(newLineItem));
			subResult = doCompare(fields, newLineItem, originalLineItem, locale);
			if (subResult.size() > 1) { //exclusive of partNum
				result.add(subResult);
			}
		}

		for (Iterator i = originalLineItemsMap.values().iterator(); i.hasNext();) {
			subResult = doCompare(fields, null, i.next(), locale);
			if (subResult.size() > 1) { //exclusive of partNum
				result.add(subResult);
			}
		}
		return result;
	}

	private static String formatPrice(Double extProratedPrc) {
		if (extProratedPrc != null) {
			return DecimalUtil.format(extProratedPrc.doubleValue(),
					DecimalUtil.DEFAULT_SCALE);
		} else {
			return null;// DraftQuoteConstants.NP_MSG
		}
	}

	private static String formatDate(java.util.Date d, Locale locale) {
		if (d == null) {
			return null;
		}
		return DateUtil
				.formatDate(d, DateUtil.PATTERN1, locale);
	}

	private static String formatPercent(Double d) {
		if (d != null) {
			return DecimalUtil.formatTo5Number(d) + "%";
		} else {
			return null;
		}
	}

	public static Quote initQuoteInfo(String webQuoteNum, User user, String userId, String up2ReportingUserIds)
			throws Exception {
		Quote quote = QuoteProcessFactory.singleton().create()
				.getQuoteDetailForComparison(webQuoteNum, user, userId, up2ReportingUserIds);
		return quote;
	}

	private static Boolean formatSalesDiscTypeCode(String salesDiscTypeCode) {
		if (salesDiscTypeCode == null) {
			return null;
		}
		return Integer.valueOf(salesDiscTypeCode).intValue() == 2;
	}

	private static Boolean toBoolean(Integer value) {
		if (value == null) {
			return null;
		}
		return value.intValue() == 1;
	}

	private static String formatPoint(Double value) {
		if (value == null) {
			return null;
		}
		return DecimalUtil.format(value.doubleValue());
	}

	private static Object getPropertyValue(Object object, Map map)
			throws Exception {
		String fieldName = (String) map.get(BidCompareConstants.ID);
		String method = (String) map.get(BidCompareConstants.METHOD);
		if (StringUtils.isNotBlank(method)) {
			return object == null ? null : MethodUtils.invokeExactMethod(
					object, method, null);
		} else {
			return object == null ? null : PropertyUtils.getProperty(object,
					fieldName);
		}
	}

	private static String getAcqstnDescByCode(String acqstnCode) throws Exception{
        CodeDescObj acqstn = null;
        try {
            CacheProcess CacheProcess = CacheProcessFactory.singleton().create();
            acqstn = CacheProcess.getAcquisitionByCode(acqstnCode);
        } catch (QuoteException e) {
            e.setMessageKey("Failed to get acquisition by "+acqstnCode);
            throw e;
        }
        if (acqstn != null)
            return acqstn.getCodeDesc();
        else
            return acqstnCode;
    }
	
	private static String getPriceLevelDesc(String prcLvlCode) {
		prcLvlCode = StringUtils.trimToEmpty(prcLvlCode);
		String desc = PartPriceConfigFactory.singleton().getPriceLevelDesc(prcLvlCode);
		if (desc == null)
			return prcLvlCode;
		else
			return desc;
    }
	
	private static String getBusinessOrgByCode(String code) throws QuoteException {
		CacheProcess instance = CacheProcessFactory.singleton().create();
        CodeDescObj businessOrg = instance.getBusinessOrgByCode(code);
        String businessOrgDesc = null;
        if (businessOrg != null){
        	businessOrgDesc = businessOrg.getCodeDesc();
        } else {
        	businessOrgDesc = code;
        }
        return StringUtils.trim(businessOrgDesc);
    }
	
	private static String ordgMethodCodeDesc(String code) throws QuoteException {
		CodeDescObj codeDesc = CacheProcessFactory.singleton().create()
				.getOemAgrmntTypeByCode(code);
		if (codeDesc != null) {
			return codeDesc.getCodeDesc();
		} else {
			return code;
		}
	}

	private static String oemBidTypeDesc(String code) throws QuoteException {
		CodeDescObj codeDesc = CacheProcessFactory.singleton().create()
				.getOemBidTypeByCode(code);
		if (codeDesc != null) {
			return codeDesc.getCodeDesc();
		} else {
			return code;
		}
	}

	private static String quoteClassfctnCodeDesc(String code) {
		CodeDescObj codeDesc = QuoteClassificationCodeFactory.singleton()
				.findByCode(code);
		if (codeDesc != null) {
			return codeDesc.getCodeDesc();
		} else {
			return code;
		}
	}
	
	public static String getSpBidCustIndustryDesc(String spBidCustIndustryCode) throws QuoteException {
		CodeDescObj codeDesc = CacheProcessFactory.singleton().create()
				.getSpBidCustIndustryByCode(spBidCustIndustryCode);
		if (codeDesc != null) {
			return codeDesc.getCodeDesc();
		} else {
			return spBidCustIndustryCode;
		}
	}
	
	public static String getOppNumInfo(QuoteHeader quoteHeader, Locale locale) {
		String oppNumInfo = "";
		String exemptionCode = quoteHeader.getExemptnCode();
		String oppNumber = quoteHeader.getOpprtntyNum();
		if (StringUtils.isBlank(exemptionCode)
				&& StringUtils.isNotBlank(oppNumber)) {
			oppNumInfo = oppNumber == null ? "" : oppNumber;
		}
		if (StringUtils.isBlank(oppNumber)
				&& StringUtils.isNotBlank(exemptionCode)) {
			ApplicationContext context = ApplicationContextFactory.singleton()
					.getApplicationContext();
			oppNumInfo = context.getI18nValueAsString(
					I18NBundleNames.BASE_MESSAGES, locale, "exemption_code")
					+ " " + exemptionCode;
		}
		return oppNumInfo;
	}
	
	private static Double getChannelTotalPrice(Quote quote, Locale locale) {
		Double channelTotalPrice = null;
		List prcTotals = quote.getPriceTotals();
		if(prcTotals != null){
			for (int i = 0; i < prcTotals.size(); i++) {
				QuotePriceTotals prcTotal = (QuotePriceTotals) prcTotals.get(i);
				if (prcTotal.getPriceType().equals(PartPriceConstants.PriceType.CHANNEL)
						&& prcTotal.getPriceSumLevelCode().equals(QuoteConstants.PRICE_SUM_LEVEL_TOTAL)
						&& prcTotal.getCurrencyCode().equals(quote.getQuoteHeader().getCurrencyCode())
						&& prcTotal.getRevnStrmCategoryCode().equals(PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL)) {
					channelTotalPrice = prcTotal.getExtAmount();
				}
			}
		}
		return channelTotalPrice;
	}
	
	private static List doCompareotherFields(Map field, Object value1,
			Object value2, Locale locale) throws Exception {
		List result = new ArrayList(1);
		if (value1 == null && value2 == null) {
			return result;
		}
		boolean flag = false;
		flag = (value1 == null) ? isEquals(value2, value1) : isEquals(value1,
				value2);
		if (!flag) {
			Object[] objectArr = getContentDescription(field, value1, value2,locale, true);
			if (objectArr != null && objectArr.length > 0) {
				result.add(objectArr);
			}
		}
		return result;
	}
	
	private static String formatBillgFrqncy(QuoteLineItem lineItem, String billgFrqncyCode){
    	return lineItem.getBillgFrqncyDscr();
	}
	
	private static String formatCvrageTerm(QuoteLineItem lineItem, Integer cvrageTerm){
		if(cvrageTerm != null){
			if(PartPriceConstants.SAAS_TERM_UNIT_ANNUAL.equals(lineItem.getPricngIndCode())){
	    		return cvrageTerm+" Years";
	    	} else if(PartPriceConstants.SAAS_TERM_UNIT_MONTH.equals(lineItem.getPricngIndCode())){
	    		return cvrageTerm+" Months";
	    	}
			return cvrageTerm.toString();
		}
		return null;
	}
	
	private static String formatTotalTerm(Integer cumCvrageTerm){
		if(cumCvrageTerm != null){
			return cumCvrageTerm + " Months";
		}
		return null;
	}
}
