package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Element;

import com.ibm.dsw.common.base.util.PortalXMLConfigReader;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.common.config.BidCompareConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2011 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>BidCompareConfigFactory<code> class.
 *    
 * @author: zyuyang@cn.ibm.com
 * 
 * Creation date: January 22, 2011
 */

public class BidCompareConfigFactory extends PortalXMLConfigReader {
	private LogContext logContext = LogContextFactory.singleton()
			.getLogContext();

	private List quoteHeaderConfig = new ArrayList();

	private List quoteLineItemConfig = new ArrayList();

	private List specialBidInfoConfig = new ArrayList();

	private List execSummaryConfig = new ArrayList();
	
	private List quoteUserAccessConfig = new ArrayList();
	
	private List specialBidQustnConfig = new ArrayList();
	
	private List quoteContactConfig = new ArrayList();
	
	private List otherFieldsConfig =  new ArrayList();

	private static BidCompareConfigFactory singleton = null;

	protected BidCompareConfigFactory() {
		super();
		String fileName = buildConfigFileName();
		loadConfig(fileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#reset()
	 */
	protected void reset() {
		singleton = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.dsw.common.base.util.PortalXMLConfigReader#loadConfig(java.lang
	 * .String)
	 */
	protected void loadConfig(String fileName) {
		try {
			logContext.debug(this, "Loading bid compare config from file: "
					+ fileName);
			Element rootElement = getRootElement(fileName);

			quoteHeaderConfig = parseElement(rootElement, "quoteHead");

			quoteLineItemConfig = parseElement(rootElement,"quoteLineItem");

			specialBidInfoConfig = parseElement(rootElement,"specialBidInfo");

			execSummaryConfig = parseElement(rootElement,"execSummary");
			
			quoteUserAccessConfig = parseElement(rootElement,"quoteUserAccess");
			
			specialBidQustnConfig = parseElement(rootElement,"specialBidQuestion");
			
			quoteContactConfig = parseElement(rootElement,"quoteContact");
			
			otherFieldsConfig = parseElement(rootElement,"otherFields");
			
		} catch (Exception e) {
			e.printStackTrace();
			logContext.error(this, e,
					"Exception loading bid compare config from file: "
							+ fileName);
		}
		logContext.debug(this, "Finished loading bid compare config from file: "
				+ fileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.dsw.common.base.util.PortalXMLConfigReader#buildConfigFileName()
	 */
	protected String buildConfigFileName() {
		return getAbsoluteFilePath(ApplicationProperties.getInstance()
				.getBidCompareConfigFile());
	}

	public List getQuoteHeaderConfig() {
		return quoteHeaderConfig;
	}

	public List getQuoteLineItemConfig() {
		return quoteLineItemConfig;
	}

	public List getSpecialBidInfoConfig() {
		return specialBidInfoConfig;
	}

	public List getExecSummaryConfig() {
		return execSummaryConfig;
	}

	public List getQuoteContactConfig() {
		return quoteContactConfig;
	}
	
	public List getOtherFieldsConfig() {
		return otherFieldsConfig;
	}

	public static BidCompareConfigFactory singleton() {
		LogContext logCtx = LogContextFactory.singleton().getLogContext();
		if (BidCompareConfigFactory.singleton == null) {
			String factoryClassName = null;
			try {
				factoryClassName = BidCompareConfigFactory.class.getName();
				Class factoryClass = Class.forName(factoryClassName);
				BidCompareConfigFactory.singleton = (BidCompareConfigFactory) factoryClass
						.newInstance();
			} catch (IllegalAccessException iae) {
				logCtx.error(BidCompareConfigFactory.class, iae, iae
						.getMessage());
			} catch (ClassNotFoundException cnfe) {
				logCtx.error(BidCompareConfigFactory.class, cnfe, cnfe
						.getMessage());
			} catch (InstantiationException ie) {
				logCtx
						.error(BidCompareConfigFactory.class, ie, ie
								.getMessage());
			}
		}
		return singleton;
	}
	
	private List parseElement(Element rootElement, String targetName){
		List targetElements = rootElement.getChild(targetName).getChildren();
		if( targetElements != null){
			int length = targetElements.size();
			List result = new ArrayList(length);
			Element element = null;
			Map map = null;
			Attribute attr = null;
			for(int i = 0; i < length; i++){
				element = (Element)targetElements.get(i);
				map = new HashMap();
				map.put(BidCompareConstants.ID, element.getAttribute(BidCompareConstants.ID).getValue());
				map.put(BidCompareConstants.LABEL, element.getChild(BidCompareConstants.LABEL).getValue());
				
				attr = element.getAttribute(BidCompareConstants.METHOD);
				if(attr != null){
					map.put(BidCompareConstants.METHOD, attr.getValue());
				}
				
				attr = element.getAttribute(BidCompareConstants.FORMAT);
				if(attr != null){
					map.put(BidCompareConstants.FORMAT, attr.getValue());
				}
				result.add(map);
			}
			return result;
		}
		return null;
	}

	public List getQuoteUserAccessConfig() {
		return quoteUserAccessConfig;
	}

	public List getSpecialBidQustnConfig() {
		return specialBidQustnConfig;
	}
}
