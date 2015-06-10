
package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.ibm.dsw.common.base.util.PortalXMLConfigReader;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.ead4j.common.util.GlobalContext;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2008 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>EmployeeDlgtnForSbFactory.java</code>
 * 
 * @author: zhangdy@cn.ibm.com
 * 
 * Created on: Apr 9, 2008
 */
public class EmployeeDlgtnForSbFactory extends PortalXMLConfigReader {
    private static EmployeeDlgtnForSbFactory singleton;
    private Map empDlgtnMap = new HashMap();    
    /**
     * 
     */
    private EmployeeDlgtnForSbFactory() {
        super();
        loadConfig(buildConfigFileName());
    }

    public boolean isForceSB(QuoteHeader header){
        
        String lob = header.getLob().getCode(); 
        String migrationCode = StringUtils.trimToEmpty(header.getProgMigrationCode());
        
        List list = (List) empDlgtnMap.get(lob);
        if (null == list) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            EmployeeDlgtnForSb edfs = (EmployeeDlgtnForSb) list.get(i);
            if(migrationCode.equals(edfs.getMigrationCode())){
                return edfs.isForceSB();
            }
        }
        return false;
    }
    public static EmployeeDlgtnForSbFactory singleton() {
        GlobalContext globalCtx = GlobalContext.singleton();
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (singleton == null) {
            String factoryClassName = null;
            try {
                factoryClassName = EmployeeDlgtnForSbFactory.class.getName();
                Class factoryClass = Class.forName(factoryClassName);
                singleton = (EmployeeDlgtnForSbFactory) factoryClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(EmployeeDlgtnForSbFactory.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(EmployeeDlgtnForSbFactory.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(EmployeeDlgtnForSbFactory.class, ie, ie.getMessage());
            }
        }
        return singleton;
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#buildConfigFileName()
     */
    protected String buildConfigFileName() {
        return getAbsoluteFilePath(ApplicationProperties.getInstance().getEmpDlgtnForSbConfigFileName());
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#loadConfig(java.lang.String)
     */
    protected void loadConfig(String fileName) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        Element element = null;
        EmployeeDlgtnForSb empDlgtnForSb = null;
        String lob = null;
        String migrationCode = null;
        String forceSB = null;
        try {
            logContext.debug(this, "Loading Employee Delegation for SB configuration from file: " + fileName);
            Iterator empForceSbIterator = getRootElement(fileName).getChildren().iterator();
            while (empForceSbIterator.hasNext()) {
                element = (Element) empForceSbIterator.next();
                lob = element.getChildTextTrim("lob");
                migrationCode = StringUtils.trimToEmpty(element.getChildTextTrim("migrationCode"));
                forceSB = element.getChildTextTrim("forceSB");
                boolean bforceSB = "1".equals(forceSB) ? true : false;
                empDlgtnForSb = new EmployeeDlgtnForSb(lob, migrationCode, bforceSB);
                List forceSbList = (List) empDlgtnMap.get(lob);
                if (null == forceSbList) {
                    forceSbList = new ArrayList();
                    empDlgtnMap.put(lob, forceSbList);
                }
                forceSbList.add(empDlgtnForSb);
            }
        } catch (Exception e) {
            logContext.error(this, e, "Exception Loading Employee Delegation for SB configuration from file: " + fileName);
        }
        logContext.debug(this, "Finished Loading Employee Delegation for SB configuration from file: " + fileName);
        logContext.debug(this, toString());

    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.common.base.util.PortalXMLConfigReader#reset()
     */
    protected void reset() {
        singleton = null;

    }

}
