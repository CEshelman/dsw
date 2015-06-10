package com.ibm.dsw.quote.dsj.viewbean;

import java.util.List;
import java.util.Locale;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.draftquote.util.StringUtils;
import com.ibm.dsw.quote.dsj.util.DsjKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>DsjCustomerViewBean<code> class.
 *    
 * @author: tangdi@cn.ibm.com
 * 
 * Creation date: Mar 31, 2015
 */
public class DsjCustomerViewBean extends DsjBaseViewBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -402567980908934453L;
	protected String returnCode = "";
	protected String errorCode = " ";
	protected transient CustomerSearchResultList dsjCustomerList;
	transient List customers = null;
	//boolean isDisplayUSFederalFlag = false;
	
	@SuppressWarnings("unchecked")
	public void collectResults(Parameters parameters) throws ViewBeanException {
        super.collectResults(parameters);
        returnCode = (String)parameters.getParameterAsString(DsjKeys.PARAM_RETURN_DSJ_CUSTOMER_CODE);
        errorCode = (String)parameters.getParameterAsString(DsjKeys.ERROR_MESSAGE);
        dsjCustomerList = (CustomerSearchResultList)parameters.getParameter(DsjKeys.PARAM_DSJ_CUSTOMER_LIST);
        if(dsjCustomerList!=null && !dsjCustomerList.equals("")){
            customers = dsjCustomerList.getResultList();
        }
        //CustomerSearchResultList resultList = (CustomerSearchResultList) param.getParameter(ParamKeys.PARAM_SIMPLE_OBJECT);
        /*lob = (String) parameters.getParameter(DsjKeys.);
    	isDisplayUSFederalFlag = ((QuoteConstants.LOB_PA.equalsIgnoreCase(dsjCust.)
                || QuoteConstants.LOB_PAE.equalsIgnoreCase(lob) || QuoteConstants.LOB_PAUN.equalsIgnoreCase(lob) || QuoteConstants.LOB_FCT
                .equalsIgnoreCase(lob)) && CustomerConstants.COUNTRY_USA.equalsIgnoreCase(country));*/
    	
    }

	public String getReturnCode() {
		return returnCode;
	}
	
	public String getErrorCode() {
		if (errorCode!=null && !errorCode.equals("")){
		return getI18NString(I18NBundleNames.BASE_MESSAGES, locale, errorCode);
		}
		else{		
			errorCode = "";			
		return errorCode;
		}
	}

	public CustomerSearchResultList getDsjCustomerList() {
		return dsjCustomerList;
	}
	
	public String getFormattedRdcNumbers(List rdcList) {
        StringBuffer sb = new StringBuffer();
        if (rdcList != null) {
            for (int i = 0; i < rdcList.size(); i++) {
                if (i == 0)
                    sb.append((String) rdcList.get(i));
                else
                    sb.append("<br />"+(String) rdcList.get(i));
            }
        }
        return sb.toString();
    }
	
	public String getPriceLevelDesc(String prcLvlCode) {
        return PartPriceConfigFactory.singleton().getPriceLevelDesc(prcLvlCode);
    }

	/*public boolean isDisplayUSFederalFlag() {
        return isDisplayUSFederalFlag;
    }*/

	public String getDsjCustomerListJson(){
		StringBuilder itBuf = new StringBuilder("dsjCustomerList:");
		String usFedrlCust = null;
		if (dsjCustomerList != null ) {
           // customers = dsjCustomerList.getResultList();		    	   
		   if(null != customers && customers.size()==1 ){	//?????8----1		
			//CustomerSearchResultList dsjCust = (CustomerSearchResultList)dsjCustomerList.);				      
		   /*for(int i=0; i<customers.size();i++){*/
			Customer cust = (Customer)customers.get(0);			
			if (cust.getGsaStatusFlag()) { 	        			
				usFedrlCust="Yes";
	        } else {
	        	usFedrlCust="No";
	        }
			List ctrctList = cust.getContractList();
			if ( ctrctList != null && ctrctList.size() > 0 ) {
				/*for (int j = 0; j < ctrctList.size(); j++) {*/
					Contract contract = (Contract)ctrctList.get(0);

				if(null != cust){
					itBuf.append("{\"Customername\":\"").append(StringUtils.jsonStringEncoding(cust.getCustName()))
					     .append("\",\"Address1\":\"").append(StringUtils.jsonStringEncoding(cust.getAddress1()))
					     .append("\",\"InternalAddress\":\"").append(StringUtils.jsonStringEncoding(cust.getInternalAddress()))
					     .append("\",\"City\":\"").append(StringUtils.jsonStringEncoding(cust.getCity()))
						 .append("\",\"SapRegionCodeDscr\":\"").append(StringUtils.jsonStringEncoding(cust.getSapRegionCodeDscr()))
						 .append("\",\"PostalCode\":\"").append(StringUtils.jsonStringEncoding(cust.getPostalCode()))
						 .append("\",\"CountryCodeDesc\":\"").append(StringUtils.jsonStringEncoding(cust.getCountryCode()))					
						 .append("\",\"SiteNumber\":\"").append(StringUtils.jsonStringEncoding(cust.getCustNum()))
						 .append("\",\"CustomerReferenceDataNumber\":\"").append(StringUtils.jsonStringEncoding(getFormattedRdcNumbers(cust.getRdcNumList())))
						 .append("\",\"IBMCustomerNumber\":\"").append(StringUtils.jsonStringEncoding(cust.getIbmCustNum()))
						 .append("\",\"USFedrlCust\":\"").append(StringUtils.jsonStringEncoding(usFedrlCust))
						 .append("\",\"AgreeNumber\":\"").append(StringUtils.jsonStringEncoding(contract.getSapContractNum()))
						 .append("\",\"AgreeOption\":\"").append(StringUtils.jsonStringEncoding(contract.getSapContractVariantCode()))
						 .append("\",\"AgreeLevel\":\"").append(StringUtils.jsonStringEncoding(getPriceLevelDesc(contract.getVolDiscLevelCode())))
						 .append("\",\"Currency\":\"").append(StringUtils.jsonStringEncoding(cust.getCurrencyCode()))
						 .append("\"}");
				  }
			  }else{
				  
				  itBuf.append("{\"Customername\":\"").append(StringUtils.jsonStringEncoding(cust.getCustName()))
				     .append("\",\"Address1\":\"").append(StringUtils.jsonStringEncoding(cust.getAddress1()))
				     .append("\",\"InternalAddress\":\"").append(StringUtils.jsonStringEncoding(cust.getInternalAddress()))
				     .append("\",\"City\":\"").append(StringUtils.jsonStringEncoding(cust.getCity()))
					 .append("\",\"SapRegionCodeDscr\":\"").append(StringUtils.jsonStringEncoding(cust.getSapRegionCodeDscr()))
					 .append("\",\"PostalCode\":\"").append(StringUtils.jsonStringEncoding(cust.getPostalCode()))
					 .append("\",\"CountryCodeDesc\":\"").append(StringUtils.jsonStringEncoding(cust.getCountryCode()))					
					 .append("\",\"SiteNumber\":\"").append(StringUtils.jsonStringEncoding(cust.getCustNum()))
					 .append("\",\"CustomerReferenceDataNumber\":\"").append(StringUtils.jsonStringEncoding(getFormattedRdcNumbers(cust.getRdcNumList())))
					 .append("\",\"IBMCustomerNumber\":\"").append(StringUtils.jsonStringEncoding(cust.getIbmCustNum()))
					 .append("\",\"USFedrlCust\":\"").append(StringUtils.jsonStringEncoding(usFedrlCust))
					 .append("\",\"AgreeNumber\":\"").append(StringUtils.jsonStringEncoding(""))
					 .append("\",\"AgreeOption\":\"").append(StringUtils.jsonStringEncoding(""))
					 .append("\",\"AgreeLevel\":\"").append(StringUtils.jsonStringEncoding(getPriceLevelDesc("")))
					 .append("\",\"Currency\":\"").append(StringUtils.jsonStringEncoding(cust.getCurrencyCode()))
					 .append("\"}");
			    	
			    }
			/*}						
		  }*/
	   }
	}
	return itBuf.toString();
	}
	 protected String getI18NString(String basename, Locale locale, String key) {
	        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
	        return appCtx.getI18nValueAsString(basename, locale, key);
	    }

}
