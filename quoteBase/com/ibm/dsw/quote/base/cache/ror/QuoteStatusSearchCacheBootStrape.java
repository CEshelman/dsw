/*
 * Created on 2007-2-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.base.cache.ror;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ibm.dsw.quote.base.cache.QuoteCache;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.quartz.common.Cache;
import com.ibm.ead4j.quartz.common.CacheContext;
import com.ibm.ead4j.quartz.common.CacheContextFactory;
import com.ibm.ead4j.topaz.persistence.jdbc.TopazCacheableFactory;

/**
 * @author Nathan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QuoteStatusSearchCacheBootStrape {
    
    private static QuoteStatusSearchCacheBootStrape instance = null;
    
    private static ApplicationProperties appProp = ApplicationProperties.getInstance();
    
    private TopazCacheableFactory[] keys = null;
    
    private QuoteStatusSearchCacheBootStrape(){}
    
    public static QuoteStatusSearchCacheBootStrape getInstance(){
        if(instance == null){
            instance = new QuoteStatusSearchCacheBootStrape();
        }
        return instance;
    } 
    
    public void initialize(){
        loadInitConfigs();
        if(keys == null){
            keys = new TopazCacheableFactory[0];
        }
        LogContext logCtx = LogContextFactory.singleton().getLogContext();
        logCtx.info(this,"Begin to Initialize QuoteCache.");
        CacheContext cc = CacheContextFactory.singleton().getCacheContext();
        logCtx.info(this,"Initialization of CacheContext is finished.");
        try {
            Cache cache = cc.getCache();
            if(cache instanceof QuoteCache){
                logCtx.info(this,"Begin to load cache STATUS_SEARCH_CACHE_GROUP");
                ((QuoteCache)cache).loadGroup("STATUS_SEARCH_CACHE_GROUP",keys);
            }else{
                logCtx.info(this,"Cache is not a QuoteCache!");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logCtx.error(this,e);
        }
    }
    
    public TopazCacheableFactory[] getCachedKeyFactories(){
        return keys;
    }
    
    private void loadInitConfigs(){
        LogContext logCtx = LogContextFactory.singleton().getLogContext();
        logCtx.info(this,"Begin to load InitConfigs.");
        SAXBuilder builder = new SAXBuilder();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("/portal-config/init-ror-cache-config.xml");
        try {
            Document doc = builder.build(in);
            Element root = doc.getRootElement();
            Element factories = root.getChild("domain-factories");
            List factoriesList = factories.getChildren("factory");
            keys = new TopazCacheableFactory[factoriesList.size()];
            for(int i=0;i<factoriesList.size();i++){
                String factoryClass = ((Element)factoriesList.get(i)).getTextTrim();
                try{
                    Class clazz = Class.forName(factoryClass);
                    keys[i] = (TopazCacheableFactory) clazz.newInstance();
                    logCtx.debug(this,"Factory:"+ factoryClass+" has been instantiated!");
                }catch(Exception ex){
                    logCtx.error(this,"Error initializing domain object factory:"+factoryClass);
                }
            }
        } catch (JDOMException e) {
            logCtx.error(this,"Failed to load init cache configration.");
            e.printStackTrace();
        } catch (IOException ioe){
            logCtx.error(this,"Failed to load init cache configration.");
            ioe.printStackTrace();
        }
        logCtx.info(this,"Finished loading InitConfigs.");
    }
}
