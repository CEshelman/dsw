package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.ibm.dsw.common.base.util.PortalXMLConfigReader;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>BizRuleFactory<code> class.
 *
 * @author: cuixg@cn.ibm.com
 *
 *          Creation date: Oct 26, 2007
 */

public class PartPriceConfigFactory extends PortalXMLConfigReader {

	private static PartPriceConfigFactory singleton = null;

	private LogContext logContext = LogContextFactory.singleton()
			.getLogContext();

	Element allRootElements = null;

	private Map additionalYearMap = new HashMap();

	private Map minTranPtsMap = new HashMap();

	private Map backDatingPastYearLimitMap = new HashMap();

	public List priceLevels = new ArrayList();

	public Map priceLevelMap = new HashMap();

	// various revenue stream codes
	private List licensePartCodes = new ArrayList();

	private List initFTLPartCodes = new ArrayList();

	private List maintenancePartCodes = new ArrayList();

	private List subFTLPartCodes = new ArrayList();

	private List supportPartCodes = new ArrayList();

	private List otherContractPartCodes = new ArrayList();

	private List ocsPartCodes = new ArrayList();

	private List needDetermineDatePartCodes = new ArrayList();

	private List preventBackDatingPartCodes = new ArrayList();

	private List allowFutureStartDatePartCodes = new ArrayList();

	private List dateEditAllowedPartCodes = new ArrayList();

	private List trmlsssr_ossupt_RevnStrmCodes = new ArrayList();

	private List sysCalEndDatePartCodes = new ArrayList();

	private List enforceEndDatePartCodes = new ArrayList();

	private List renewalLicensePartRevnStrmCodes = new ArrayList();

	// channel margin config

	private ChannelMarginConfig bpConfig = new ChannelMarginConfig();

	// EOL
	public Map eolMap = new HashMap();

	// ELA

	private int elaLimits = 0;
	private int appliLimits = 0;
	private int partSearchLimits = 0;

	private PreventDateEditConfig preventBothDateEditConfig;

	private PreventDateEditConfig preventStartDateEditConfig;

	private PreventDateEditConfig preventEndDateEditConfig;

	private DateChangeTriggerSBConfig dateChangeTriggerSBConfig;

	private BidIterationConfig bidIterationConfig;

	private DateLogicConfig dateLogicConfig;
	//Switch to control Growth Delegation
	private boolean growthDelegationEnabled = false;
	
	public boolean isGrowthDelegationEnabled() {
		return growthDelegationEnabled;
	}

	private static class ChannelMarginConfig {
		public List lobs = new ArrayList();
		public String fulfillmentSource = "";
		public List quoteTypeConfigs = new ArrayList();
		public Map countryConfigMap = new HashMap();
	}

	private static class BpMarginCountryConfig {
		public String countryCode = "";
		public List contractVariants = new ArrayList();
		public boolean disableManualBpDiscount = false;
	}

	private static class LobConfig {
		public LobConfig(String lob, boolean isFMP) {
			this.lob = lob;
			this.isFMP = isFMP;
		}

		public String lob = "";
		public boolean isFMP = false;

		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof LobConfig)) {
				return false;
			}

			if (o == this) {
				return true;
			}

			LobConfig config = (LobConfig) o;
			if (config.lob.equals(this.lob) && (config.isFMP == this.isFMP)) {
				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hash = 1;

			if (lob != null) {
				hash = lob.hashCode();
			}

			if (isFMP) {
				hash = hash * 2 + 1;
			} else {
				hash = hash * 2;
			}

			return hash;
		}
	}

	private static class QuoteTypeConfig {
		public QuoteTypeConfig(String quoteType, boolean editable) {
			this.quoteType = quoteType;
			this.editable = editable;
		}

		public String quoteType = "";
		public boolean editable = false;

		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof QuoteTypeConfig)) {
				return false;
			}

			if (o == this) {
				return true;
			}

			QuoteTypeConfig config = (QuoteTypeConfig) o;

			if (config.quoteType.equals(this.quoteType)
					&& config.editable == this.editable) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			int hash = 1;

			if (quoteType != null) {
				hash = quoteType.hashCode();
			}

			if (editable) {
				hash = hash * 2 + 1;
			} else {
				hash = hash * 2;
			}

			return hash;
		}
	}

	private static class PreventDateEditConfig {
		public String revenueStream;
		public List preventDateEditQuoteTypes = new ArrayList();

		public void addQuoteTypes(DateEditQuoteTypeConfig quoteType) {
			if (!preventDateEditQuoteTypes.contains(quoteType)) {
				preventDateEditQuoteTypes.add(quoteType);
			}
		}
	}

	private static class DateEditQuoteTypeConfig {
		public String typeCode = "";
		public List stages = new ArrayList();
		public List partTypes = new ArrayList();

		public void addPartType(String partType) {
			if (!partTypes.contains(partType)) {
				partTypes.add(partType);
			}
		}

		public void addStages(String stage) {
			if (!stages.contains(stage)) {
				stages.add(stage);
			}
		}
	}

	private Map autoChnlDiscOvrdConfigs = new HashMap();

	// Key: lob code, value: hash set containing part type codes
	private Map allowOvrdUnitPriceOrDiscConfigMap = new HashMap();

	// For cmprss cvrage, elems in list are lob codes
	private List allowCmprssCvrageLogCodes = new ArrayList();

	private int increasePricingTryLimit = 0;

	private double increasePricingDifference = 0.0;

	private int preCrediteCheckValidMonths = 0;

	public PartPriceConfigFactory() {
		super();
		String fileName=buildConfigFileName();

		loadConfig(fileName);
	}

	public boolean isBackDatingAllowed(QuoteHeader header, String revnStrmCode) {

		String lob = header.getLob() == null ? "" : header.getLob().getCode();
		String cntryCode = header.getCountry() == null ? "" : header
				.getCountry().getCode3();
		String isMigration = header.isMigration() ? "1" : "0";

		return ButtonDisplayRuleFactory.singleton().isSBButtonDisplay(lob,
				cntryCode, isMigration, header.getAcquisitionCodes())
				&& !this.preventBackDatingPartCodes.contains(revnStrmCode);
	}

	public boolean isFutureStartDateAllowed(String revnStrmCode) {

		// String lob = header.getLob() == null ? "" :
		// header.getLob().getCode();
		// String cntryCode = header.getCountry() == null ? "" :
		// header.getCountry().getCode3();
		// String isMigration = header.isMigration() ? "1" : "0";

		return this.allowFutureStartDatePartCodes.contains(revnStrmCode);
	}

	public PartPriceTranPriceLevel findNextPriceLevel(double totalPts,
			String tSVP) {
		PartPriceTranPriceLevel pts = null;
		// PartPriceTranPriceLevel nextPts = null;
		if (null == minTranPtsMap) {
			loadConfig(buildConfigFileName());
		}
		List list = (List) minTranPtsMap.get(tSVP);
		if (null == list) {
			return null;
		}
		for (int i = 0; i < list.size(); i++) {
			pts = (PartPriceTranPriceLevel) list.get(i);
			if (totalPts < pts.getMinTranPts()) {
				return pts;
			}
		}
		return null;
	}

	public int getAddtionalYear(String lob) {
		String year = (String) additionalYearMap.get(lob);
		if (year == null)
			return 0;
		else
			return Integer.parseInt(year);
	}

	public List getOverridePriceLevels() {
		if (priceLevels == null) {
			priceLevels = new ArrayList();
			loadConfig(buildConfigFileName());
		}
		return priceLevels;
	}

	public String getPriceLevelDesc(String prcLvlCode) {
		prcLvlCode = StringUtils.trimToEmpty(prcLvlCode);
		String desc = (String) this.priceLevelMap.get(prcLvlCode);
		if (desc == null)
			return prcLvlCode;
		else
			return desc;
	}

	public static PartPriceConfigFactory singleton() {
		// GlobalContext globalCtx = GlobalContext.singleton();
		LogContext logCtx = LogContextFactory.singleton().getLogContext();

		if (PartPriceConfigFactory.singleton == null) {
			String factoryClassName = null;
			try {
				factoryClassName = PartPriceConfigFactory.class.getName();
				Class factoryClass = Class.forName(factoryClassName);
				PartPriceConfigFactory.singleton = (PartPriceConfigFactory) factoryClass
						.newInstance();
			} catch (IllegalAccessException iae) {
				logCtx.error(PartPriceConfigFactory.class, iae, iae
						.getMessage());
			} catch (ClassNotFoundException cnfe) {
				logCtx.error(PartPriceConfigFactory.class, cnfe, cnfe
						.getMessage());
			} catch (InstantiationException ie) {
				logCtx.error(PartPriceConfigFactory.class, ie, ie.getMessage());
			}
		}
		return singleton;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ibm.dsw.common.base.util.PortalXMLConfigReader#buildConfigFileName()
	 */
	@Override
	protected String buildConfigFileName() {
		return getAbsoluteFilePath(ApplicationProperties.getInstance()
				.getPartPriceConfigFile());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ibm.dsw.common.base.util.PortalXMLConfigReader#loadConfig(java.lang
	 * .String)
	 */
	@Override
	protected void loadConfig(String fileName) {

		// Element element = null;
		try {
			logContext.debug(this, "Loading part price config from file: "
					+ fileName);
			Element rootElement = getRootElement(fileName);
			// Edward 10 Nov 2010.Load comm-part-price-config.xml.
			List childs = rootElement.getChildren("import");
			for (int i = 0; i < childs.size(); i++) {
				Element el = (Element) childs.get(i);
				String commXml = el.getAttributeValue("ref");
				Element commRoot = getRootElementDirectByFileName(getAbsoluteFilePath(commXml));
				List commClds=commRoot.getChildren();
				Element el1=null;
				for (int j = 0; j < commClds.size(); j++) {
					el1 = (Element) commClds.get(j);
					rootElement.addContent((Element)el1.clone());//must be clone,else error.
				}
			}

			setAllRootElements(rootElement);

			loadBackDatingConfig(rootElement);

			loadAdditionalYearConfig(rootElement);

			loadMinTransPointConfig(rootElement);

			loadQuotePriceLevelsConfig(rootElement);

			loadRevenueStreamConfig(rootElement);

			loadChannelMarginConfig(rootElement);

			this.loadEOLConfig(rootElement);

			loadPreventDateEditConfig(rootElement);

			loadElaLimitsConf(rootElement);

			loadELAAutoChnlDiscOvrd(rootElement);

			loadDateChangeTriggerSBConfig(rootElement);

			loadAllowOvrrdUnitPriceOrDiscConfig(rootElement);

			loadAllowCmprssCvrageConfig(rootElement);

			loadIncreasePricingTryLimit(rootElement);
			
			loadGrowthDelegationEnabled(rootElement);

			loadIncreasePricingDifference(rootElement);

			loadBidIterationConfig(rootElement);

			loadDateLogicConfig(rootElement);

			loadPreCrediteCheckValidMonths(rootElement);

			loadQuoteRenewalPriceConfig(rootElement);

		} catch (Exception e) {
			e.printStackTrace();
			logContext.error(this, e,
					"Exception loading part price config from file: "
							+ fileName);
		}
		logContext.debug(this, "Finished loading part price config from file: "
				+ fileName);
		// logContext.debug(this, toString());
	}

	private void loadIncreasePricingDifference(Element root) {
		String strDifference = root
				.getChildTextTrim("increase-pricing-difference");

		try {
			increasePricingDifference = Double.parseDouble(strDifference);
		} catch (Exception ignore) {
			logContext.error(this,
					"Config entry for increase-pricing-difference is not a valid double: "
							+ strDifference);
		}
	}

	private void loadIncreasePricingTryLimit(Element root) {
		String strLimit = root.getChildTextTrim("increase-pricing-try-limit");

		try {
			increasePricingTryLimit = Integer.parseInt(strLimit);
		} catch (Exception ignore) {
			logContext.error(this,
					"Config entry for increase-pricing-try-limit is not a valid integer: "
							+ strLimit);
		}
	}
	
	private void loadGrowthDelegationEnabled(Element root){
		if (null != root) {
			String strGrowthDelegationEnabled = root.getChildTextTrim("growth-delegation-enabled");
			growthDelegationEnabled = Boolean.parseBoolean(strGrowthDelegationEnabled);
		}else {
			logContext.error(this,
					"Failed to load configure for <growth-delegation-enabled> because of parsing part-price-config.xml failure.");
		}
	}

	private void loadELAAutoChnlDiscOvrd(Element root) {
		Element elaDisc = root.getChild("auto-channel-over-ride");

		List configList = elaDisc.getChildren();
		for (Iterator it = configList.iterator(); it.hasNext();) {
			Element config = (Element) it.next();

			String spclAreaCode = config.getTextTrim();
			String strDisc = config.getAttributeValue("disc");

			autoChnlDiscOvrdConfigs.put(spclAreaCode, Double.valueOf(strDisc));
		}
	}

	private void loadAllowCmprssCvrageConfig(Element root) {
		Element cmprssCvrage = root.getChild("allow-cmprss-cvrage");

		Element lobConfig = cmprssCvrage.getChild("lob-codes");
		String[] lobArray = lobConfig.getTextTrim().split(",");
		for (int i = 0; i < lobArray.length; i++) {
			allowCmprssCvrageLogCodes.add(lobArray[i]);
		}
	}

	private void loadAllowOvrrdUnitPriceOrDiscConfig(Element root) {
		Element elaDisc = root.getChild("allow-oup-disc-configs");

		List configList = elaDisc.getChildren();
		for (Iterator it = configList.iterator(); it.hasNext();) {
			Element config = (Element) it.next();

			String lobCodes = config.getChildTextTrim("lob-codes");
			String partTypeCodes = config.getChildTextTrim("part-type-codes");

			if (StringUtils.isEmpty(partTypeCodes)) {
				continue;
			}

			if (StringUtils.isEmpty(lobCodes)) {
				continue;
			}

			String[] partTypeCodeArray = partTypeCodes.split(",");
			String[] lobCodeArray = lobCodes.split(",");

			for (int i = 0; i < lobCodeArray.length; i++) {
				Set partTypeCodeSet = (Set) allowOvrdUnitPriceOrDiscConfigMap
						.get(lobCodeArray[i]);

				if (partTypeCodeSet == null) {
					partTypeCodeSet = new HashSet();
				}

				for (int j = 0; j < partTypeCodeArray.length; j++) {
					partTypeCodeSet.add(partTypeCodeArray[j]);
				}

				allowOvrdUnitPriceOrDiscConfigMap.put(lobCodeArray[i],
						partTypeCodeSet);
			}
		}
	}

	public boolean allowOvrdUnitPriceOrDisc(String lobCode, String partTypeCode) {
		Set partTypeCodeSet = (Set) allowOvrdUnitPriceOrDiscConfigMap
				.get(lobCode);

		if (partTypeCodeSet == null) {
			return false;
		}

		return partTypeCodeSet.contains(partTypeCode);
	}

	/**
	 * @param rootElement
	 */
	private void loadChannelMarginConfig(Element root) {

		Element channelMarginElem = root.getChild("show-bp-margins").getChild(
				"show-channel-margin");

		List lobList = channelMarginElem.getChild("lob-types").getChildren(
				"lob-type");
		if (lobList != null) {
			for (int i = 0; i < lobList.size(); i++) {
				Element ele = (Element) lobList.get(i);
				String lobCode = ele.getChildTextTrim("lob-code");
				boolean isFMP = false;

				Element isFMPElem = ele.getChild("is-fmp");
				if (isFMPElem != null) {
					isFMP = Boolean.valueOf(isFMPElem.getTextTrim())
							.booleanValue();
				}

				LobConfig config = new LobConfig(lobCode, isFMP);
				bpConfig.lobs.add(config);
			}

		}

		bpConfig.fulfillmentSource = channelMarginElem
				.getChildTextTrim("fulfillment-source");

		List typeList = channelMarginElem.getChild("quote-types").getChildren(
				"quote-type");

		if (typeList != null) {
			for (int i = 0; i < typeList.size(); i++) {
				Element ele = (Element) typeList.get(i);
				String typeCode = ele.getChildTextTrim("type-code");
				boolean editable = true;

				Element editableElem = ele.getChild("quote-editable");
				if (editableElem != null) {
					editable = Boolean.valueOf(editableElem.getTextTrim())
							.booleanValue();
				}

				QuoteTypeConfig config = new QuoteTypeConfig(typeCode, editable);
				bpConfig.quoteTypeConfigs.add(config);
			}
		}

		for (Iterator iter = channelMarginElem.getChildren("enable-by-country")
				.iterator(); iter.hasNext();) {
			Element countryElem = (Element) iter.next();

			boolean disableManualBpDiscount = false;

			Element elem = countryElem.getChild("disable-at-item-level");
			if (elem != null) {
				disableManualBpDiscount = Boolean.valueOf(elem.getTextTrim())
						.booleanValue();
			}

			List contractVariantCodes = new ArrayList();

			Element contractVariantElem = countryElem
					.getChild("contract-variant");
			if (contractVariantElem != null) {
				String[] variantCodes = contractVariantElem.getTextTrim()
						.split(",");
				for (int i = 0; i < variantCodes.length; i++) {
					contractVariantCodes.add(variantCodes[i]);
				}
			}

			String[] codes = countryElem.getChildTextTrim("country-code")
					.split(",");
			for (int i = 0; i < codes.length; i++) {
				BpMarginCountryConfig countryConfig = new BpMarginCountryConfig();
				countryConfig.countryCode = codes[i];
				countryConfig.disableManualBpDiscount = disableManualBpDiscount;
				countryConfig.contractVariants = contractVariantCodes;
				bpConfig.countryConfigMap.put(codes[i], countryConfig);
			}
		}
	}

	/**
	 * @param rootElement
	 */
	private void loadRevenueStreamConfig(Element root) {
		logContext.debug(this, "loading RevenueStreamConfig started...");

		Element elem = root.getChild("revenue-stream-config");

		this.parseRevenueStreamCodes(elem, "license-revenue-stream",
				this.licensePartCodes);
		this.parseRevenueStreamCodes(elem, "initial-ftl-revenue-stream",
				this.initFTLPartCodes);
		this.parseRevenueStreamCodes(elem, "maintenance-revenue-stream",
				this.maintenancePartCodes);
		this.parseRevenueStreamCodes(elem,
				"subsequent-ftl-maintenance-revenue-stream",
				this.subFTLPartCodes);
		this.parseRevenueStreamCodes(elem, "other-license-revenue-stream",
				otherContractPartCodes);
		this.parseRevenueStreamCodes(elem, "support-revenue-stream",
				this.supportPartCodes);
		this.parseRevenueStreamCodes(elem, "ocs-revenue-stream",
				this.ocsPartCodes);
		this.parseRevenueStreamCodes(elem,
				"need-determine-dates-revenue-stream",
				this.needDetermineDatePartCodes);
		this.parseRevenueStreamCodes(elem, "can-edit-dates-revenue-stream",
				this.dateEditAllowedPartCodes);
		this.parseRevenueStreamCodes(elem,
				"prevent-back-dating-revenue-stream",
				this.preventBackDatingPartCodes);
		this.parseRevenueStreamCodes(elem,
				"allow-future-start-date-revenue-stream",
				this.allowFutureStartDatePartCodes);
		this.parseRevenueStreamCodes(elem, "trmlsssr-ossupt-revenue-stream",
				trmlsssr_ossupt_RevnStrmCodes);
		this.parseRevenueStreamCodes(elem,
				"system-calculate-end-date-revenue-stream",
				this.sysCalEndDatePartCodes);

		this.parseRevenueStreamCodes(elem,
				"enforce-end-date-by-condition-revenue-stream",
				this.enforceEndDatePartCodes);

		this.parseRevenueStreamCodes(elem,
				"renewal-license-part-revenue-stream",
				this.renewalLicensePartRevnStrmCodes);

		logContext.debug(this, "loading RevenueStreamConfig end");

	}

	private void parseRevenueStreamCodes(Element root, String elementName,
			List result) {
		String codeValue = root.getChild(elementName)
				.getChild("revenue-stream").getAttributeValue("value");
		String[] codes = codeValue.split(",");
		for (int i = 0; i < codes.length; i++) {
			String code = codes[i].trim();
			if (!"".equals(code)) {
				result.add(code);
			}
		}

		if(logContext instanceof QuoteLogContextLog4JImpl){
			if(((QuoteLogContextLog4JImpl)logContext).isDebug(this)){
				StringBuffer buffer = new StringBuffer();
				for (Iterator it = result.iterator(); it.hasNext();) {
					String revnStrm = (String) it.next();
					if (it.hasNext()) {
						buffer.append(revnStrm).append(", ");
					} else {
						buffer.append(revnStrm);
					}
				}
				logContext.debug(this, elementName + ": {" + buffer.toString() + "}");
			}
		}
	}

	/**
	 * @param rootElement
	 */
	private void loadBackDatingConfig(Element rootElement) {
		logContext
				.debug(this,
						"loading part price back dating past year configuration started...");
		Element all = rootElement.getChild("back-dating-past-year-limit");

		Iterator it = all.getChildren().iterator();
		Element element;
		String lob = null;
		String year = null;

		while (it.hasNext()) {
			element = (Element) it.next();
			lob = element.getChildTextTrim("lob");
			year = element.getChildTextTrim("year");
			logContext.debug(this, "back dating configuration values: " + lob
					+ " ---> " + year);
			backDatingPastYearLimitMap.put(lob, year);
		}

	}

	/**
	 * @param rootElement
	 */
	private void loadAdditionalYearConfig(Element rootElement) {
		Element element;
		Iterator additionalYearIter = rootElement.getChild(
				"additional-year-config").getChildren("additional-year")
				.iterator();

		while (additionalYearIter.hasNext()) {
			element = (Element) additionalYearIter.next();
			additionalYearMap.put(element.getChildTextTrim("lob"), element
					.getChildTextTrim("year"));
		}
	}

	protected void loadMinTransPointConfig(Element rootElement) {

		LogContext logContext = LogContextFactory.singleton().getLogContext();

		try {

			Iterator iter = rootElement.getChild("quote-min-tran-points")
					.getChildren().iterator();

			while (iter.hasNext()) {
				Element element = (Element) iter.next();
				String volDiscLevelCode = element
						.getChildTextTrim("volDiscLevelCode");
				String tranPriceLevelCode = element
						.getChildTextTrim("tranPriceLevelCode");
				String minTranPts = element.getChildTextTrim("minTranPts");
				PartPriceTranPriceLevel partPriceMinTranPts = new PartPriceTranPriceLevel(
						volDiscLevelCode, new Double(minTranPts).doubleValue(),
						tranPriceLevelCode);
				List volDiscLevelList = (List) minTranPtsMap
						.get(volDiscLevelCode);
				if (null == volDiscLevelList) {
					volDiscLevelList = new ArrayList();
					minTranPtsMap.put(volDiscLevelCode, volDiscLevelList);
				}
				volDiscLevelList.add(partPriceMinTranPts);
			}
		} catch (Exception e) {
			logContext.error(this, e,
					"Exception loading Price Min Tran points ");
		}
		Iterator iterator = minTranPtsMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			List list = (List) minTranPtsMap.get(key);
			Collections.sort(list);
		}

	}

	protected void loadQuotePriceLevelsConfig(Element rootElement) {

		LogContext logContext = LogContextFactory.singleton().getLogContext();

		try {
			Iterator iter = rootElement.getChild("quote-price-levels")
					.getChildren().iterator();
			while (iter.hasNext()) {
				Element element = (Element) iter.next();
				CodeDescObj priceLevel = new CodeDescObj_jdbc(element
						.getChildTextTrim("code"), element
						.getChildTextTrim("description"));
				priceLevels.add(priceLevel);
				priceLevelMap.put(element.getChildTextTrim("code"), element
						.getChildTextTrim("description"));
			}
		} catch (Exception e) {
			logContext.error(this, e, "Exception loading Quote Price Levels. ");
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#reset()
	 */
	@Override
	protected void reset() {
		singleton = null;
	}

	/**
	 * @return Returns the backDatingPastYearLimit for specific lob.
	 */
	public int getBackDatingPastYearLimit(String lob) {
		String value = (String) backDatingPastYearLimitMap.get(lob);

		if (value == null) {
			return 0;
		} else {
			return Integer.parseInt(value);
		}
	}

	/*
	 * public boolean showChnlMargin(QuoteHeader header){ boolean isChannel =
	 * header.getFulfillmentSrc().equals(QuoteConstants.FULFILLMENT_CHANNEL); if
	 * (isChannel && (header.isPAEQuote() || header.isPAQuote())){ return true;
	 * }else{ return false; } }
	 */

	/**
	 * @param string
	 * @return
	 */
	public boolean isLicensePart(String code) {
		return this.licensePartCodes.contains(code);
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean isInitFTLPart(String code) {

		return this.initFTLPartCodes.contains(code);
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean isMaintenancePart(String code) {

		return this.maintenancePartCodes.contains(code);
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean isSubFTLPart(String code) {

		return this.subFTLPartCodes.contains(code);
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean isOtherContractPart(String code) {

		return this.otherContractPartCodes.contains(code);
	}

	public boolean isSupportPart(String code) {
		return this.supportPartCodes.contains(code);
	}

	public boolean isOCSPart(String code) {
		return this.ocsPartCodes.contains(code);
	}

	public boolean needDetermineDate(String code) {
		return this.needDetermineDatePartCodes.contains(code);
	}

	public boolean isTrmlsssr_OSSupt_Part(String revnStrmCode) {
		if (StringUtils.isEmpty(revnStrmCode)) {
			return false;
		}

		return this.trmlsssr_ossupt_RevnStrmCodes.contains(revnStrmCode);
	}

	public boolean shouldTriggerSBNoRQRef(QuoteHeader header) {
		return shouldTriggerSB(header, false);
	}

	public boolean shouldTriggerSBRQRef(QuoteHeader header) {
		return shouldTriggerSB(header, true);
	}

	/**
	 * check the region, lob and quote type to determine if it qualifies for sb
	 *
	 * @param header
	 * @return
	 */
	private boolean shouldTriggerSB(QuoteHeader header, boolean rqRef) {

		Iterator it = getDateChangeTriggerSBConfig().getSbDeterminationList()
				.iterator();

		while (it.hasNext()) {

			PartPriceConfigFactory.SBDetermination sbDetermination = (PartPriceConfigFactory.SBDetermination) it
					.next();

			// region,lob and quote type as pre-condition
			if ((sbDetermination.isRQRef() == rqRef)
					&& sbDetermination.getRegionList().contains(
					header.getCountry().getSpecialBidAreaCode())
					&& sbDetermination.getLobList().contains(
							header.getLob().getCode())
					&& sbDetermination.getQuoteTypeList().contains(
							header.getQuoteTypeCode())) {

				return true;
			}
		}

		return false;
	}

	/**
	 * @param header
	 */
	public boolean allowChannelMarginDiscount(Quote quote) {
		Customer customer = quote.getCustomer();
		QuoteHeader header = quote.getQuoteHeader();
		QuoteAccess access = quote.getQuoteAccess();

		// for draft renewal quote, if quote is not editable return false
		if (header.isRenewalQuote()
				&& StringUtils.isBlank(header.getSapIntrmdiatDocNum())) {
			if (access == null || !access.isCanEditRQ()) {
				return false;
			}
		}

		// check the quote is channel
		if (!this.bpConfig.fulfillmentSource.equals(header.getFulfillmentSrc())) {
			return false;
		}
		// check the lob support channel margin
		String lob = header.getLob().getCode();
		boolean isFMP = header.isFCTToPAQuote();
		if (!this.bpConfig.lobs.contains(new LobConfig(lob, isFMP))) {
			return false;
		}

		// check quote type only if quote is draft
		if (StringUtils.isBlank(header.getSapIntrmdiatDocNum())) {
			String quoteType = header.getQuoteTypeCode();
			boolean editable = true;
			if (quote.getQuoteHeader().isRenewalQuote()) {
				if (access != null) {
					editable = access.isCanEditRQ();
				}
			}
			if (!bpConfig.quoteTypeConfigs.contains(new QuoteTypeConfig(
					quoteType, editable))) {
				return false;
			}
		}

		// if no country config, it means system don't support channel margin
		BpMarginCountryConfig countryConfig = (BpMarginCountryConfig) bpConfig.countryConfigMap
				.get(header.getCountry().getCode3());
		if (null == countryConfig) {
			return false;
		}

		// system don't have the contract variant configured, so system will
		// enable channel margin
		if (countryConfig.contractVariants.isEmpty()) {
			return true;
		} else {
			// need check the customer

			if (customer == null) {
				// no customer selected, so system will disable channel margin
				return false;
			} else {
				// check the contract variant
				if (customer.getContractList() == null
						|| customer.getContractList().size() == 0) {
					// no contract , return false
					return false;
				}
				Iterator iter = customer.getContractList().iterator();
				while (iter.hasNext()) {
					Contract contract = (Contract) iter.next();
					String variantCode = contract.getSapContractVariantCode();
					if (!countryConfig.contractVariants.contains(variantCode)) {
						return false;
					}
				}
				// all check passed
				return true;
			}
		}

	}

	/**
	 * @param header
	 * @return
	 */
	public boolean enableManualBpDiscount(Quote quote) {
		if (this.allowChannelMarginDiscount(quote)) {
			BpMarginCountryConfig countryConfig = (BpMarginCountryConfig) bpConfig.countryConfigMap
					.get(quote.getQuoteHeader().getCountry().getCode3());
			return !countryConfig.disableManualBpDiscount;
		}
		return false;
	}

	/**
	 * @param string
	 */
	public boolean isDateEditAllowed(QuoteHeader header, QuoteLineItem item) {

		return this.dateEditAllowedPartCodes.contains(item.getRevnStrmCode())
				&& !preventDateEdit(preventBothDateEditConfig, header, item);
	}

	public boolean showAddChkboxInPSRes(String lob, String sapSalesStatCode) {

		boolean lobAllow = Boolean.valueOf((String) eolMap.get(lob + "_lob"))
				.booleanValue();
		boolean statAllow = Boolean.valueOf(
				(String) eolMap.get(sapSalesStatCode + "_stat")).booleanValue();
		this.logContext.debug(this, "statCode is:" + sapSalesStatCode
				+ "statAllow is:" + statAllow);
		return (lobAllow && statAllow);

	}

	public boolean canPartBeReactivated(String sapSalesStatCode) {
		return Boolean.valueOf((String) eolMap.get(sapSalesStatCode + "_stat"))
				.booleanValue();
	}

	private void loadEOLConfig(Element rootElement) {

		LogContext logContext = LogContextFactory.singleton().getLogContext();

		try {
			Iterator iter = rootElement.getChild("allow-add-obslte-part-to-ws")
					.getChild("lob-types").getChildren().iterator();
			while (iter.hasNext()) {
				Element element = (Element) iter.next();
				eolMap.put(element.getChildTextTrim("lob-code") + "_lob",
						element.getChildTextTrim("allow-add-obslte-part"));
			}

			iter = rootElement.getChild("allow-add-obslte-part-to-ws")
					.getChild("sap-sales-stat-codes").getChildren().iterator();
			while (iter.hasNext()) {
				Element element = (Element) iter.next();
				String[] status = element.getChildTextTrim(
						"sap-sales-stat-code").split(",");
				for (int i = 0; i < status.length; i++) {
					eolMap.put(status[i].trim() + "_stat", element
							.getChildTextTrim("allow-add-obslte-part"));
				}
			}
		} catch (Exception e) {
			logContext.error(this, e, "Exception loading Quote Price Levels. ");
		}

	}

	private void loadPreventDateEditConfig(Element root) {
		logContext.debug(this, "loading prevent date edit config started...");

		Element elem = root.getChild("prevent-date-edit");

		preventBothDateEditConfig = parsePreventDateEditConfig(elem
				.getChild("both-date"));
		preventStartDateEditConfig = parsePreventDateEditConfig(elem
				.getChild("start-date"));
		preventEndDateEditConfig = parsePreventDateEditConfig(elem
				.getChild("end-date"));
	}

	private PreventDateEditConfig parsePreventDateEditConfig(Element root) {
		PreventDateEditConfig preventConfig = new PreventDateEditConfig();

		String revenueStream = root.getChild("revenue-stream")
				.getAttributeValue("value");
		preventConfig.revenueStream = revenueStream;

		List quoteTypeElems = root.getChildren("quote-type");
		if (quoteTypeElems != null && quoteTypeElems.size() > 0) {
			for (int i = 0; i < quoteTypeElems.size(); i++) {
				Element quoteTypeElem = (Element) quoteTypeElems.get(i);
				DateEditQuoteTypeConfig quoteType = new DateEditQuoteTypeConfig();

				Element stagesElem = quoteTypeElem.getChild("quote-stages");
				if (stagesElem != null && stagesElem.getChildren().size() > 0) {
					List stageList = stagesElem.getChildren();

					for (int j = 0; j < stageList.size(); j++) {
						Element stageConfig = (Element) stageList.get(j);
						quoteType.addStages(stageConfig.getText());
					}
				}

				quoteType.typeCode = quoteTypeElem.getChildText("type-code");

				Element partTypesElem = quoteTypeElem.getChild("part-types");
				if (partTypesElem != null
						&& partTypesElem.getChildren().size() > 0) {
					List typeList = partTypesElem.getChildren();

					for (int j = 0; j < typeList.size(); j++) {
						Element typeConfig = (Element) typeList.get(j);

						quoteType.addPartType(typeConfig.getText());
					}
				}

				preventConfig.addQuoteTypes(quoteType);
			}
		}

		return preventConfig;
	}

	public boolean isAllowEditStartDate(QuoteHeader header, QuoteLineItem item) {

		return this.dateEditAllowedPartCodes.contains(item.getRevnStrmCode())
				&& !preventDateEdit(preventStartDateEditConfig, header, item)
				&& !preventDateEdit(preventBothDateEditConfig, header, item);
	}

	public boolean isAllowEditEndDate(QuoteHeader header, QuoteLineItem item) {

		return this.dateEditAllowedPartCodes.contains(item.getRevnStrmCode())
				&& !preventDateEdit(preventEndDateEditConfig, header, item)
				&& !preventDateEdit(preventBothDateEditConfig, header, item);
	}

	private boolean preventDateEdit(PreventDateEditConfig preventConfig,
			QuoteHeader header, QuoteLineItem item) {
		if (preventConfig == null) {
			return false;
		}

		if (preventConfig.revenueStream.indexOf(item.getRevnStrmCode()) == -1) {
			return false;
		}

		for (Iterator it = preventConfig.preventDateEditQuoteTypes.iterator(); it
				.hasNext();) {
			DateEditQuoteTypeConfig quoteTypeConfig = (DateEditQuoteTypeConfig) it
					.next();

			String quoteStageCode = PartPriceConstants.QuoteStage.DRAFT;
			if (header.isSubmittedQuote()) {
				quoteStageCode = PartPriceConstants.QuoteStage.SUBMITTED;
			}

			// if we find an prevent entry, then return true
			if (quoteTypeConfig.stages.contains(quoteStageCode)
					&& quoteTypeConfig.typeCode.equals(header
							.getQuoteTypeCode())
					&& quoteTypeConfig.partTypes.contains(CommonServiceUtil
							.getPartType(header, item))) {
				return true;
			}
		}

		return false;
	}

	private void loadElaLimitsConf(Element root) {
		setElaLimits(Integer.parseInt(root.getChildTextTrim("ela-part-limits")));
		setAppliLimits(Integer.parseInt(root.getChildTextTrim("appli-part-limits")));
		this.setPartSearchLimits(Integer.parseInt(root
				.getChildTextTrim("part-search-limits")));
	}

	public int getElaLimits() {
		return elaLimits;
	}

	public void setElaLimits(int elaLimits) {
		this.elaLimits = elaLimits;
	}
	
	public int getAppliLimits() {
		return appliLimits;
	}

	public void setAppliLimits(int appliLimits) {
		this.appliLimits = appliLimits;
	}

	/**
	 * @return Returns the partSearchLimits.
	 */
	public int getPartSearchLimits() {
		return partSearchLimits;
	}

	/**
	 * @param partSearchLimits
	 *            The partSearchLimits to set.
	 */
	public void setPartSearchLimits(int partSearchLimits) {
		this.partSearchLimits = partSearchLimits;
	}

	public boolean isEnforceEndDate(String code) {
		return this.isMaintenancePart(code) && (!this.isSubFTLPart(code));
	}

	public Double getAutoChnlDiscOvrd(String spclAreaCode) {
		Set keys = autoChnlDiscOvrdConfigs.keySet();

		for (Iterator it = keys.iterator(); it.hasNext();) {
			String key = (String) it.next();
			if (key.indexOf(spclAreaCode) != -1) {
				return (Double) autoChnlDiscOvrdConfigs.get(key);
			}
		}

		return null;
	}

	public boolean isSpclAreaEnabledForELAAutoChnlDisc(String spclAreaCode) {
		Set keys = autoChnlDiscOvrdConfigs.keySet();

		for (Iterator it = keys.iterator(); it.hasNext();) {
			String key = (String) it.next();
			if (key.indexOf(spclAreaCode) != -1) {
				return true;
			}
		}

		return false;
	}

	/**
	 * inner class for date change trigger special bid config
	 *
	 * @author Administrator @
	 */
	public class DateChangeTriggerSBConfig {
		private List sbDeterminationList = null;

		public DateChangeTriggerSBConfig() {
			sbDeterminationList = new ArrayList(1);
		}

		/**
		 * @return Returns the sbDeterminationList.
		 */
		public List getSbDeterminationList() {
			return sbDeterminationList;
		}

		/**
		 * @param sbDeterminationList
		 *            The sbDeterminationList to set.
		 */
		public void setSbDeterminationList(List sbDeterminationList) {
			this.sbDeterminationList = sbDeterminationList;
		}
	}

	/**
	 * inner class for special bid determination
	 *
	 * @author Administrator
	 *
	 */
	public class SBDetermination {
		private boolean rqRef = false;
		private List regionList = null;
		private List lobList = null;
		private List quoteTypeList = null;

		public SBDetermination() {
			regionList = new ArrayList(1);
			lobList = new ArrayList(1);
			quoteTypeList = new ArrayList(2);
		}

		/**
		 * @return Returns the lobList.
		 */
		public List getLobList() {
			return lobList;
		}

		/**
		 * @param logList
		 *            The lobList to set.
		 */
		public void setLobList(List lobList) {
			this.lobList = lobList;
		}

		/**
		 * @return Returns the quoteTypeList.
		 */
		public List getQuoteTypeList() {
			return quoteTypeList;
		}

		/**
		 * @param quoteTypeList
		 *            The quoteTypeList to set.
		 */
		public void setQuoteTypeList(List quoteTypeList) {
			this.quoteTypeList = quoteTypeList;
		}

		/**
		 * @return Returns the regionList.
		 */
		public List getRegionList() {
			return regionList;
		}

		/**
		 * @param regionList
		 *            The regionList to set.
		 */
		public void setRegionList(List regionList) {
			this.regionList = regionList;
		}

		public boolean isRQRef() {
			return rqRef;
		}

		public void setRQRef(boolean rqRef) {
			this.rqRef = rqRef;
		}
	}

	public class BidIterationConfig {
		private double lessThanPrice;
		private double lessThanPercent;
		private double greaterThanPrice;
		private double greaterThanPercent;
		private double lineItemDisChangeExceed;
		private List incrDisNotAllowedRevStrm;

		public double getGreaterThanPercent() {
			return greaterThanPercent;
		}

		public void setGreaterThanPercent(double greaterThanPercent) {
			this.greaterThanPercent = greaterThanPercent;
		}

		public double getGreaterThanPrice() {
			return greaterThanPrice;
		}

		public void setGreaterThanPrice(double greaterThanPrice) {
			this.greaterThanPrice = greaterThanPrice;
		}

		public List getIncrDisNotAllowedRevStrm() {
			return incrDisNotAllowedRevStrm;
		}

		public void setIncrDisNotAllowedRevStrm(List incrDisNotAllowedRevStrm) {
			this.incrDisNotAllowedRevStrm = incrDisNotAllowedRevStrm;
		}

		public double getLessThanPercent() {
			return lessThanPercent;
		}

		public void setLessThanPercent(double lessThanPercent) {
			this.lessThanPercent = lessThanPercent;
		}

		public double getLessThanPrice() {
			return lessThanPrice;
		}

		public void setLessThanPrice(double lessThanPrice) {
			this.lessThanPrice = lessThanPrice;
		}

		public double getLineItemDisChangeExceed() {
			return lineItemDisChangeExceed;
		}

		public void setLineItemDisChangeExceed(double lineItemDisChangeExceed) {
			this.lineItemDisChangeExceed = lineItemDisChangeExceed;
		}

		public boolean isIncrDisNotAllowedRevStrm(String revStrm) {
			if (incrDisNotAllowedRevStrm.contains(revStrm)) {
				return true;
			}
			return false;
		}
	}

	public class DateLogicConfig {
		public String ftlLicRevenueStream;
		public String ftlMainRevenueStream;

		public String getFtlLicRevenueStream() {
			return ftlLicRevenueStream;
		}

		public void setFtlLicRevenueStream(String ftlLicRevenueStream) {
			this.ftlLicRevenueStream = ftlLicRevenueStream;
		}

		public String getFtlMainRevenueStream() {
			return ftlMainRevenueStream;
		}

		public void setFtlMainRevenueStream(String ftlMainRevenueStream) {
			this.ftlMainRevenueStream = ftlMainRevenueStream;
		}
	}

	/**
	 * parse date change trigger special bid config block
	 *
	 * @param rootElement
	 */
	private void loadDateChangeTriggerSBConfig(Element rootElement) {

		logContext.debug(this,
				"loading loadDateChangeTriggerSBConfig started...");

		// parse date-change-trigger-sb-config element
		Iterator it = rootElement.getChild("date-change-trigger-sb-config")
				.getChildren().iterator();

		dateChangeTriggerSBConfig = new DateChangeTriggerSBConfig();

		while (it.hasNext()) {

			Element element = (Element) it.next();

			SBDetermination sbDetermination = new SBDetermination();
			String strRQRef = element.getAttributeValue("RQ_REF");
			sbDetermination.setRQRef(Boolean.parseBoolean(strRQRef));

			parseStringList(element, "region", sbDetermination.getRegionList());

			parseStringList(element, "lob-code", sbDetermination.getLobList());

			parseStringList(element, "type-code", sbDetermination
					.getQuoteTypeList());

			dateChangeTriggerSBConfig.getSbDeterminationList().add(
					sbDetermination);
		}

		logContext.debug(this, "loading loadDateChangeTriggerSBConfig end");

	}

	private void loadBidIterationConfig(Element rootElement) {
		logContext.debug(this, "loading loadBidIterationConfig started...");
		Iterator it = rootElement.getChild("bid-iteration-config")
				.getChildren().iterator();
		bidIterationConfig = new BidIterationConfig();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			if (element.getName().equals("total-price-less-than")) {
				bidIterationConfig.setLessThanPrice(Double.parseDouble(element
						.getChild("price").getTextTrim()));
				bidIterationConfig
						.setLessThanPercent(Double.parseDouble(element
								.getChild("percent").getTextTrim()));
			}
			if (element.getName().equals("total-price-greater-than")) {
				bidIterationConfig.setGreaterThanPrice(Double
						.parseDouble(element.getChild("price").getTextTrim()));
				bidIterationConfig
						.setGreaterThanPercent(Double.parseDouble(element
								.getChild("percent").getTextTrim()));
			}
			if (element.getName().equals("line-item-discount-change-exceed")) {
				bidIterationConfig.setLineItemDisChangeExceed(Double
						.parseDouble(element.getTextTrim()));
			}
			if (element.getName().equals("increase-discount-not-allowed")) {
				String[] incrDisNotAllowedRevStrm = element.getChild(
						"revenue-stream").getAttributeValue("value").split(",");
				List list = new ArrayList();
				for (int i = 0; i < incrDisNotAllowedRevStrm.length; i++) {
					list.add(incrDisNotAllowedRevStrm[i]);
				}
				bidIterationConfig.setIncrDisNotAllowedRevStrm(list);
			}
		}
		logContext.debug(this, "loading loadBidIterationConfig end");
	}

	private void loadDateLogicConfig(Element rootElement) {
		logContext.debug(this, "loading loadDateLogicConfig started...");
		Iterator it = rootElement.getChild("date-logic-relationship-config")
				.getChildren().iterator();
		dateLogicConfig = new DateLogicConfig();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			if (element.getName().equals("ftl-config")) {
				dateLogicConfig.setFtlLicRevenueStream(element.getChild(
						"license-revenue-stream").getAttributeValue("value"));
				dateLogicConfig.setFtlMainRevenueStream(element.getChild(
						"maintenance-revenue-stream")
						.getAttributeValue("value"));
			}
		}
		logContext.debug(this, "loading loadDateLogicConfig end");
	}

	private void loadPreCrediteCheckValidMonths(Element root) {
		String validMonths = root
				.getChildTextTrim("pre_credite_check_valid_months");

		try {
			preCrediteCheckValidMonths = Integer.parseInt(validMonths);
		} catch (Exception ignore) {
			logContext.error(this,
					"Config entry for pre_credite_check_valid_months is not a valid int: "
							+ validMonths);
		}
	}

	/**
	 * parse string as a list with "," as the delimiter
	 *
	 * @param element
	 * @param sbDetermination
	 * @param destList
	 */
	private void parseStringList(Element element, String elementName,
			List destList) {

		String elementValues = element.getChild(elementName).getTextTrim();

		StringTokenizer token = new StringTokenizer(elementValues, ",");

		while (token.hasMoreTokens()) {

			String value = token.nextToken();

			destList.add(value);
		}
	}

	/**
	 * @return Returns the dateChangeTriggerSBConfig.
	 */
	public DateChangeTriggerSBConfig getDateChangeTriggerSBConfig() {
		return dateChangeTriggerSBConfig;
	}

	/**
	 * @param dateChangeTriggerSBConfig
	 *            The dateChangeTriggerSBConfig to set.
	 */
	public void setDateChangeTriggerSBConfig(
			DateChangeTriggerSBConfig dateChangeTriggerSBConfig) {
		this.dateChangeTriggerSBConfig = dateChangeTriggerSBConfig;
	}

	public boolean allowCmprssCvrage(String lobCode) {
		return this.allowCmprssCvrageLogCodes.contains(lobCode);
	}

	public boolean isFTLPart(String revnStrmCode) {
		return isInitFTLPart(revnStrmCode) || isSubFTLPart(revnStrmCode);
	}

	// The end date of all licence revenue stream codes except TRMLSSSR
	// will be calculated by system
	public boolean isSysCalEndDate(String revnStrmCode) {
		return sysCalEndDatePartCodes.contains(StringUtils.trim(revnStrmCode));
	}

	public boolean enforceEndDateByCondition(String revnStrmCode) {
		return enforceEndDatePartCodes.contains(StringUtils.trim(revnStrmCode));
	}

	public int getIncreasePricingTryLimit() {
		return increasePricingTryLimit;
	}

	public double getIncreasePricingDifference() {
		return increasePricingDifference;
	}

	public int getPreCrediteCheckValidMonths() {
		return preCrediteCheckValidMonths;
	}

	public BidIterationConfig getBidIterationConfig() {
		return bidIterationConfig;
	}

	public DateLogicConfig getDateLogicConfig() {
		return dateLogicConfig;
	}

	public boolean isRenewalLicensePart(String revnStrmCode){
		return renewalLicensePartRevnStrmCodes.contains(revnStrmCode);
	}

	public Element getAllRootElements() {
		return allRootElements;
	}

	public void setAllRootElements(Element allRootElements) {
		this.allRootElements = allRootElements;
	}

	private class RenewalPriceDisplayConfig {

		public List<String> lob = new ArrayList<String>();

		public List<String> quoteType = new ArrayList<String>();

		public List<String> quoteStage = new ArrayList<String>();

		public List<String> accessLevel = new ArrayList<String>();
	}

	private List<RenewalPriceDisplayConfig> renewalPriceMethodConfigList = null;

	private List<RenewalPriceDisplayConfig> renewalPriceRSVPConfigList = null;

	private List<RenewalPriceDisplayConfig> renewalPricePYPConfigList = null;


	private void loadQuoteRenewalPriceConfig(Element rootElement) {

		LogContext logContext = LogContextFactory.singleton().getLogContext();
        Element renewalPriceDisplay = rootElement.getChild("ss-renewal-pricing-display");

		RenewalPriceDisplayConfig renewalPriceConfig = null;

		List<Element> pricingElements = null;

		try {

			renewalPriceMethodConfigList = buildRenewalPriceDisplayConfigList(renewalPriceDisplay, "pricing-method");
			renewalPriceRSVPConfigList = buildRenewalPriceDisplayConfigList(renewalPriceDisplay, "rsvp-price");
			renewalPricePYPConfigList = buildRenewalPriceDisplayConfigList(renewalPriceDisplay, "ss-prior-year-price");

		} catch (Exception e) {
			logContext.error(this, e, "Exception loading ss-renewal-pricing-display. ");
		}

	}

	private List<RenewalPriceDisplayConfig> buildRenewalPriceDisplayConfigList(Element renewalPriceDisplay, String nodeName) {
		List<RenewalPriceDisplayConfig> renewalPriceConfigList = new ArrayList<RenewalPriceDisplayConfig>();

		RenewalPriceDisplayConfig renewalPriceConfig;
		List<Element> pricingElements;
		pricingElements = renewalPriceDisplay.getChildren(nodeName);

		for (Element e : pricingElements) {

			renewalPriceConfig = new RenewalPriceDisplayConfig();

			parseStringList(e, "quote-type", renewalPriceConfig.quoteType);
			parseStringList(e, "lob", renewalPriceConfig.lob);
			parseStringList(e, "quote-stage", renewalPriceConfig.quoteStage);
			parseStringList(e, "access-level", renewalPriceConfig.accessLevel);

			renewalPriceConfigList.add(renewalPriceConfig);

		}

		return renewalPriceConfigList;
	}

	public boolean showRenewalPriceMethod(String lob, String quoteType, String quoteStage, String accessLevel) {
		boolean isShow = false;

		if (renewalPriceMethodConfigList != null){
			for (RenewalPriceDisplayConfig renewalPriceMethodConfig : renewalPriceMethodConfigList) {
				if (renewalPriceMethodConfig.lob.contains(lob) && renewalPriceMethodConfig.quoteStage.contains(quoteStage) && renewalPriceMethodConfig.quoteType.contains(quoteType)
						&& renewalPriceMethodConfig.accessLevel.contains(accessLevel)) {
					isShow = true;
				}
			}
		}

		return isShow;

	}

	public boolean showRenewalPriceRSVP(String lob, String quoteType, String quoteStage, String accessLevel) {
		boolean isShow = false;

		if (renewalPriceRSVPConfigList != null){
			for (RenewalPriceDisplayConfig renewalPriceRSVPConfig : renewalPriceRSVPConfigList) {
				if (renewalPriceRSVPConfig.lob.contains(lob) 
						&& renewalPriceRSVPConfig.quoteStage.contains(quoteStage) 
						&& renewalPriceRSVPConfig.quoteType.contains(quoteType)
						&& renewalPriceRSVPConfig.accessLevel.contains(accessLevel)) {
					isShow = true;
				}
			}
		}
		return isShow;

	}

	public boolean showRenewalPricePYP(String lob, String quoteType, String quoteStage, String accessLevel) {

		boolean isShow = false;

		if (renewalPricePYPConfigList != null){
			for (RenewalPriceDisplayConfig renewalPricePYPConfig : renewalPricePYPConfigList) {
				if (renewalPricePYPConfig.lob.contains(lob) && renewalPricePYPConfig.quoteStage.contains(quoteStage) && renewalPricePYPConfig.quoteType.contains(quoteType)
						&& renewalPricePYPConfig.accessLevel.contains(accessLevel)) {
					isShow = true;
				}
			}
		}
		return isShow;

	}


}
