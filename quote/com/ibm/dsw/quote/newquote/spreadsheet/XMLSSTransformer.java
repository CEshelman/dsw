
package com.ibm.dsw.quote.newquote.spreadsheet;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.ibm.ead4j.common.config.EAD4JBootstrapKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>XMLSSTransformer.java</code> class.
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-4-1
 */
public class XMLSSTransformer {

    private static ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
    
    protected static final String codeBase = appCtx.getConfigParameter(EAD4JBootstrapKeys.EAD4J_CODEBASE_PATH_KEY);
    
    private static TransformerFactory xmlssfactory = TransformerFactory.newInstance();
    
    private static Map cachedTransformers = new HashMap();
    
    private Templates template = null;

    public  XMLSSTransformer(Templates template) {
        this.template = template;
    }

    public static XMLSSTransformer loadTransformer(String templateName) throws Exception {
            if (templateName == null)
                throw new Exception("invalid template: templateName is " + templateName);
            
            XMLSSTransformer transformer = null;
            if(cachedTransformers.containsKey(templateName)) {
                transformer = (XMLSSTransformer) cachedTransformers.get(templateName);
            } else {
                String templatePath =  codeBase + appCtx.getConfigParameter(templateName);
                File template = new File(templatePath);
                transformer = new XMLSSTransformer(xmlssfactory.newTemplates(new StreamSource(template)));
                cachedTransformers.put(templateName, transformer);
            }
        return transformer;
    }
    
    public boolean isValid() {
        return template != null;
    }

    public void transfrom(InputStream sourceStream, OutputStream resultStream) throws Exception {
        if (!this.isValid())
            throw new Exception( "No transform template avliable, please load template first");
        Source source = new StreamSource(sourceStream);
        Result result = new StreamResult(resultStream);
        template.newTransformer().transform(source, result);
    }
}

