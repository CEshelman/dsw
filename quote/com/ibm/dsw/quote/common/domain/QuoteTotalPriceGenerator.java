package com.ibm.dsw.quote.common.domain;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.util.FactoryNameHelper;

public class QuoteTotalPriceGenerator {
	
	 private static QuoteTotalPriceGenerator singleton = null;
	 
	 public QuoteTotalPrice create(String quoteTotalPriceType) {
		 
		 if(QuoteTotalPrice.TOT_PRC_TYPE_NORMAL_SW.equals(quoteTotalPriceType)){
			 return new QuoteTotalPriceSoftware(); 
		 }else if(QuoteTotalPrice.TOT_PRC_TYPE_MONTHLY_SW.equals(quoteTotalPriceType)){
			 return new QuoteTotalPriceMonthlySoftware();
		 }else if(QuoteTotalPrice.TOT_PRC_TYPE_SAAS.equals(quoteTotalPriceType)){
			 return new QuoteTotalPriceSaas();
		 }else if(QuoteTotalPrice.TOT_PRC_TYPE_GRAND_TOT.equals(quoteTotalPriceType)){
			 return new QuoteGrandTotalPrice(); 
		 }
		 
		 return new QuoteTotalPriceSoftware(); 
	 }

     public static QuoteTotalPriceGenerator singleton() {
        
        LogContext logCtx = LogContextFactory.singleton().getLogContext();

        if (QuoteTotalPriceGenerator.singleton == null) {
            String generatorClassName = null;
            try {
                generatorClassName = FactoryNameHelper.singleton().getDefaultClassName(QuoteTotalPriceGenerator.class.getName());
                Class generatorClass = Class.forName(generatorClassName);
                QuoteTotalPriceGenerator.singleton = (QuoteTotalPriceGenerator) generatorClass.newInstance();
            } catch (IllegalAccessException iae) {
                logCtx.error(QuoteTotalPriceGenerator.class, iae, iae.getMessage());
            } catch (ClassNotFoundException cnfe) {
                logCtx.error(QuoteTotalPriceGenerator.class, cnfe, cnfe.getMessage());
            } catch (InstantiationException ie) {
                logCtx.error(QuoteTotalPriceGenerator.class, ie, ie.getMessage());
            }
        }
        return singleton;
     }

}
