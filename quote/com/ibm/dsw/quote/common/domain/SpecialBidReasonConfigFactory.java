/*
 * Created on 2009-1-4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.common.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Element;

import com.ibm.dsw.common.base.util.PortalXMLConfigReader;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2009 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SpecialBidReasonConfigFactory<code> class.
 *    
 * @author: Fred(qinfengc@cn.ibm.com)
 * 
 * Creation date: 2009-1-4
 */
public class SpecialBidReasonConfigFactory extends PortalXMLConfigReader{
	private Map map = new HashMap();
	
	private static SpecialBidReasonConfigFactory singleton = null;
	
	private SpecialBidReasonConfigFactory()
	{
		super();
		loadConfig(this.buildConfigFileName());
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#buildConfigFileName()
	 */
	protected String buildConfigFileName() {
		return getAbsoluteFilePath(ApplicationProperties.getInstance().getSpecialBidReasonConfigFilename());
	}
	
	public String getDisplayKey(String code)
	{
		return (String)map.get(code);
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#loadConfig(java.lang.String)
	 */
	protected void loadConfig(String fileName) {
		LogContext logContext = LogContextFactory.singleton().getLogContext();
        Element element = null;
        try
        {
            logContext.debug(this, "Loading Special Bid reason code configuration from file: " + fileName);
            Iterator iter = getRootElement(fileName).getChildren().iterator();
            while ( iter.hasNext() )
            {
                Element ele = (Element)iter.next();
                String reasonCode = ele.getAttributeValue("reasonCode");
                String displayKey = ele.getAttributeValue("displayKey");
                map.put(reasonCode, displayKey);
                logContext.debug(this, "reasonCode=" + reasonCode + ", key=" + displayKey);
            }
	    } catch (Exception e) {
	        logContext.error(this, e, "Exception Loading Special Bid reason configuration from file: " + fileName);
	    }
	    logContext.debug(this, "Finished Loading Special Bid reason configuration from file: " + fileName);
	    logContext.debug(this, toString());
	}
	
	public static SpecialBidReasonConfigFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (SpecialBidReasonConfigFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = SpecialBidReasonConfigFactory.class.getName();
                Class factoryClass = Class.forName(factoryClassName);
                SpecialBidReasonConfigFactory.singleton = (SpecialBidReasonConfigFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(SpecialBidReasonConfigFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(SpecialBidReasonConfigFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(SpecialBidReasonConfigFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

	/* (non-Javadoc)
	 * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#reset()
	 */
	protected void reset() {
		singleton = null;
	}

}
