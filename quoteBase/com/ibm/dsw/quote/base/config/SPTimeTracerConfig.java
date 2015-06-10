package com.ibm.dsw.quote.base.config;

import org.apache.commons.lang.StringUtils;

import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class SPTimeTracerConfig {
	private static LogContext log = LogContextFactory.singleton().getLogContext();
	public static final String DISABLE_TIME_TRACER = "disable.timetracer"; 
	public static final String BOOL_TRUE = "true";
	
	private boolean disableTimeTracer = false;
	
	private static SPTimeTracerConfig config = new SPTimeTracerConfig();
	
	private SPTimeTracerConfig() {
		init();
    }
	
	private void init(){
	    ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();

        String strDisable = null;
        
        try{
        	if(appContext.containsKey(DISABLE_TIME_TRACER)){
        		strDisable = appContext.getConfigParameter(DISABLE_TIME_TRACER);
        	}
        }catch(Exception ignore){
        	log.error(this, "uable to locate the parameter for disabling time tracer");
        	log.error(this, ignore);
        }
        
        if(StringUtils.isNotBlank(strDisable) && BOOL_TRUE.equalsIgnoreCase(StringUtils.trim(strDisable))){
        	disableTimeTracer = true;
        }
	}
	
	public static boolean isTimeTracerDisabled(){
		return config.disableTimeTracer;
	}
}
