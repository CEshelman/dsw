package com.ibm.dsw.quote.customer.contract;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.customerlist.domain.ActiveService;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PrepareConfiguratorRedirectDataBaseContract<code> class.
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: 2011-6-16
 */
public class PrepareConfiguratorRedirectDataUpdateContract extends PrepareConfiguratorRedirectDataBaseContract {
	transient List<ActiveService> activeServices = new LinkedList();
	
	
	
    public List getActiveServices() {
		return activeServices;
	}
    
    /**
     * load line items information sent by pp tab
     */
	public void load(Parameters parameters, JadeSession session) {
		LogContext logCtx = LogContextFactory.singleton()
        .getLogContext();
		try{

        	super.load(parameters, session);
        	String pid = this.getPid().trim();
        	
    	String validPrefix = DraftQuoteParamKeys.CONFGRTN_PREFIX.concat(pid).concat("_");
    	String paramName = null;
    	String paramNames[] = null;
    	Map activeServicesMap = new HashMap();
    	String partKey = null;
    	ActiveService as = null;
    	for(Iterator i = parameters.getParameterNames(); i.hasNext();){
    		paramName = (String) i.next();  //something like :CONFGRTN_5725C56_D0J4ILL_8_PART_NUM
    		if(!paramName.startsWith(validPrefix)){
    			continue;
    		}
    		paramNames = paramName.split("_", 5);
    		partKey = paramNames[2].concat("_").concat(paramNames[3]);
    		as = (ActiveService) activeServicesMap.get(partKey);
    		if(as == null){
    			as = new ActiveService();
    			activeServicesMap.put(partKey, as);
    			activeServices.add(as);
    		}
    		if("_".concat(paramNames[4]).equals(DraftQuoteParamKeys.partNumberSuffix)){
    			as.setPartNumber((parameters.getParameter(paramName)==null
    											||parameters.getParameterAsString(paramName).trim().equals(""))?
    													null:parameters.getParameterAsString(paramName).trim());
    			
    		}
    		if("_".concat(paramNames[4]).equals(DraftQuoteParamKeys.BILLING_FREQUENCY)){
    			as.setBillingFrequency((parameters.getParameter(paramName)==null
						||parameters.getParameterAsString(paramName).trim().equals(""))?
								null:parameters.getParameterAsString(paramName).trim());
    			
    		}
    		if("_".concat(paramNames[4]).equals(DraftQuoteParamKeys.CVRAGE_TERM)){
    			as.setTerm((parameters.getParameter(paramName)==null
						||parameters.getParameterAsString(paramName).trim().equals(""))?
								null:parameters.getParameterAsString(paramName).trim());
    			
    		}
    		if("_".concat(paramNames[4]).equals(DraftQuoteParamKeys.quantitySuffix)){
    			as.setQuantity((parameters.getParameter(paramName)==null
						||parameters.getParameterAsString(paramName).trim().equals(""))?
								null:parameters.getParameterAsString(paramName).trim());
    			
    		}
    		if("_".concat(paramNames[4]).equals(DraftQuoteParamKeys.RAMPUP_FLAG_SUFFIX)){
    			as.setRampupFlag((parameters.getParameter(paramName)==null
						||parameters.getParameterAsString(paramName).trim().equals(""))?
								null:parameters.getParameterAsString(paramName).trim());
    			
    		}
    		if("_".concat(paramNames[4]).equals(DraftQuoteParamKeys.RAMPUP_SEQ_NUM_SUFFIX)){
    			as.setRampupSeqNum((parameters.getParameter(paramName)==null
						||parameters.getParameterAsString(paramName).trim().equals(""))?
								null:parameters.getParameterAsString(paramName).trim());
    			
    		}
    		if("_".concat(paramNames[4]).equals(DraftQuoteParamKeys.REPLACED_FLAG_SUFFIX)){
    			as.setReplaced((parameters.getParameter(paramName)==null
						||parameters.getParameterAsString(paramName).trim().equals(""))?
								null:parameters.getParameterAsString(paramName).trim());
    			
    		}
    		if("_".concat(paramNames[4]).equals(DraftQuoteParamKeys.END_DATE_SUFFIX)){
    			as.setEndDate((parameters.getParameter(paramName)==null
						||parameters.getParameterAsString(paramName).trim().equals(""))?
								null:parameters.getParameterAsString(paramName).trim());
    			
    		}
    	}

    	}catch(RuntimeException e){
    		logCtx.error(this, e);
    		throw e;
    	}
    }
	
    public static void main(String[] args){
    	String s = "100.2340";
    	
    	System.out.println(s.substring(0,s.indexOf(".")<0?s.length():s.indexOf(".")));
    	
    	
    }

	
}
