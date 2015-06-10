package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import com.ibm.dsw.common.base.util.PortalXMLConfigReader;
import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>FCTSpecialBidTypesDescFactory<code> class.
 *    
 * @author: qinfengc@cn.ibm.com
 * 
 * Creation date: 2008-5-6
 */

public class FCTSpecialBidTypesDescFactory extends PortalXMLConfigReader{
    private List list = new ArrayList();
    
    private FCTSpecialBidTypesDescFactory()
    {
        super();
        loadConfig(buildConfigFileName());
    }
    
    public List getSpeicalTypesDescs(String lob, String type, String migration, String inputType, String value)
    {
        for ( int i = 0; i < list.size(); i++ )
        {
            FCTSpecialBidTypesDesc desc = (FCTSpecialBidTypesDesc)list.get(i);
            if ( desc.contain(lob, type, migration, inputType) )
            {
                return desc.getDescItemList();
            }
        }
        return null;
    }

    private static FCTSpecialBidTypesDescFactory singleton = null;

    public static FCTSpecialBidTypesDescFactory singleton() {
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (FCTSpecialBidTypesDescFactory.singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = FCTSpecialBidTypesDescFactory.class.getName();
                Class factoryClass = Class.forName(factoryClassName);
                FCTSpecialBidTypesDescFactory.singleton = (FCTSpecialBidTypesDescFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(FCTSpecialBidTypesDescFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(FCTSpecialBidTypesDescFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(FCTSpecialBidTypesDescFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#buildConfigFileName()
     */
    protected String buildConfigFileName() {
        return getAbsoluteFilePath(ApplicationProperties.getInstance().getFCTSpecialBidTypesDescConfigFileName());
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#loadConfig(java.lang.String)
     */
    protected void loadConfig(String fileName) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        Element element = null;
        try
        {
            logContext.debug(this, "Loading FCT Special Bid Types Desc configuration from file: " + fileName);
            Iterator iter = getRootElement(fileName).getChildren().iterator();
            while ( iter.hasNext() )
            {
                Element ele = (Element)iter.next();
                String lobs = ele.getAttributeValue("lob");
                String types = ele.getAttributeValue("type");
                String migrations = ele.getAttributeValue("migration");
                String value = ele.getAttributeValue("value");
                String inputTypes = ele.getAttributeValue("inputType");
                List descList = new ArrayList();
                Iterator itemIter = ele.getChildren().iterator();
                while ( itemIter.hasNext() )
                {
                    Element itemEle = (Element)itemIter.next();
                    CodeDescObj_jdbc codeDesc = new CodeDescObj_jdbc(itemEle.getAttributeValue("title"), itemEle.getText());
                    descList.add(codeDesc);
                }
                FCTSpecialBidTypesDesc fctDesc = new FCTSpecialBidTypesDesc();
                fctDesc.setInputTypes(inputTypes);
                fctDesc.setValue(value);
                fctDesc.setMigrations(migrations);
                fctDesc.setLobs(lobs);
                fctDesc.setTypes(types);
                fctDesc.setDescItemList(descList);
                list.add(fctDesc);
            }
	    } catch (Exception e) {
	        logContext.error(this, e, "Exception Loading FCT Special Bid Types Desc configuration from file: " + fileName);
	    }
	    logContext.debug(this, "Finished Loading FCT Special Bid Types Desc configuration from file: " + fileName);
	    logContext.debug(this, toString());
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#reset()
     */
    protected void reset() {
        singleton = null;
    }
}
