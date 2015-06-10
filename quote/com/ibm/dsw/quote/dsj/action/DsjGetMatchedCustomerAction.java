package com.ibm.dsw.quote.dsj.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.CustomerSearchResultList;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.dsj.contract.DsjBaseContract;
import com.ibm.dsw.quote.dsj.util.DsjKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.topaz.exception.TopazException;
/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>GetDsjMatchedCustomerAction<code> class.
 *    
 * @author: tangdi@cn.ibm.com
 * 
 * Creation date: Mar 31, 2015
 */
public class DsjGetMatchedCustomerAction extends DsjBaseAction {
	
	/**
	 * http://work.ibm.com:9080/quote/quote.wss?jadeAction=DSJ_SEARCH_CUSTOMER
	 */
	private static final long serialVersionUID = 5716477509185190976L;
	private static final String VALIDE_RETURN_CODE = "0";
	/*private static final String MORE_THAN_ONE_RETURN_CODE= "1";
	private static final String NO_RETURN_CODE= "2";*/
	private static final String NO_MATCH_RETURN_CODE = "1";

	public ResultBean executeBiz(DsjBaseContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		/*DSJ search by customer name input param begin */
		/*//String cntry = "USA";
		String cntry = contract.getCountry();
        //String lob = "PA";
        String lob = contract.getLob();
        //String customerName = "Commonwealth of Pennsylvania";
        String customerName = contract.getCustQueryName();
        String contractOption = "";
        String anniversary = "0";
        int findActiveFlag = 1;
        int startPos = -1;
        String state = "";       
        int searchType = 0;
        String audienceCode = "INTERNAL";
        String siteNum = "";*/
		/*DSJ search by customer name input param end*/
		
		/*DSJ search by customer id input param begin */
		String ctrctNum = "";
		//String dswIcnRcdID = "";
		//String dswIcnRcdID = "0007219103";
		//String dswIcnRcdID = "0036005531";
		String dswIcnRcdID = contract.getCustomerNum();
		String country = contract.getCountry();
		String searchLob = contract.getLob();
		int startPos = 0;
		boolean searchPayer = false;
		String progMigrtnCode = "";
		String audienceCode = "INTERNAL";
		String userSiteNumber = "";		
		
		/*DSJ search by customer id input param end */
        
        if (StringUtils.isBlank(country) || StringUtils.isBlank(searchLob)) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return handler.getResultBean();
        }
        else if(StringUtils.isBlank(dswIcnRcdID)){
        	handler.addObject(DsjKeys.ERROR_MESSAGE, DraftQuoteMessageKeys.NO_CUSTOMER_MATCH_FOUND_BY_ID);
			handler.addObject(DsjKeys.PARAM_RETURN_DSJ_CUSTOMER_CODE, NO_MATCH_RETURN_CODE);
        	handler.setState( DsjKeys.DSJ_CUSTOMER_JSON);
    		return handler.getResultBean();
        }
        else{
        	
        	//get the result list from db
            CustomerProcess process = null;
            CustomerSearchResultList customerresultList = null;   		            
            try {
            	
                process = CustomerProcessFactory.singleton().create();
                
                /*DSJ search by customer name begin */
                //customerresultList = process.searchCustomerByAttr(customerName, cntry, contractOption, anniversary, lob, findActiveFlag, startPos, state, searchType, audienceCode, siteNum);                
                /*test data :  resultList = process.searchCustomerByAttr('Commonwealth of Pennsylvania', 'USA ', '', '0', 'PA', 0 ,-1,'',0,'INTERNAL','');*/
                /*DSJ search by customer name end */
                
                /*DSJ search by customer id begin */
                customerresultList = process.searchCustomerByDswIcnRcdID(ctrctNum, dswIcnRcdID, country, searchLob, startPos,
                        searchPayer, progMigrtnCode, audienceCode, userSiteNumber);
                
                /*db2 call ebiz1.S_QT_CUST_BY_ID (?,'', '0007219103','%','PA','',0,10,?,'INTERNAL','')
                  db2 call ebiz1.S_QT_CUST_BY_ID (?,'0000147209', '0007583326','%','OEM','',0,10,?,'INTERNAL','')
                  db2 call ebiz1.S_QT_CUST_BY_ID (?,'', '0036005531','USA','OEM','',0,10,?,'INTERNAL','')
                  */
                /*DSJ search by customer id begin */
            } catch (TopazException e) {
                logContext.error(this, e.getMessage());
                throw new QuoteException("error executing topaz process", e);
            }
    		
            /*DSJ search by customer name begin */
    		/*if (customerresultList.getRealSize()==0){
    			handler.addObject(DsjKeys.ERROR_MESSAGE, DraftQuoteMessageKeys.NO_MATCH_DSJ_CUSTOMER);
    			handler.addObject(DsjKeys.PARAM_RETURN_DSJ_CUSTOMER_CODE, NO_RETURN_CODE);
            }
            else if (customerresultList.getRealSize()==1){    //????8----1        	
            	handler.addObject(DsjKeys.PARAM_DSJ_CUSTOMER_LIST, customerresultList); 
        		handler.addObject(DsjKeys.PARAM_RETURN_DSJ_CUSTOMER_CODE, VALIDE_RETURN_CODE);
            }
            else
            {
            	handler.addObject(DsjKeys.ERROR_MESSAGE, DraftQuoteMessageKeys.Multiple_MATCH_DSJ_CUSTOMER);
            	handler.addObject(DsjKeys.PARAM_RETURN_DSJ_CUSTOMER_CODE, MORE_THAN_ONE_RETURN_CODE);
            }*/
            /*DSJ search by customer name end */
            
            /*DSJ search by customer id begin */
            if (customerresultList.getRealSize()==1){           	
            	handler.addObject(DsjKeys.PARAM_DSJ_CUSTOMER_LIST, customerresultList); 
        		handler.addObject(DsjKeys.PARAM_RETURN_DSJ_CUSTOMER_CODE, VALIDE_RETURN_CODE);
            }
            else
            {
            	handler.addObject(DsjKeys.ERROR_MESSAGE, DraftQuoteMessageKeys.NO_CUSTOMER_MATCH_FOUND_BY_ID);
            	handler.addObject(DsjKeys.PARAM_RETURN_DSJ_CUSTOMER_CODE, NO_MATCH_RETURN_CODE);
            }
            /*DSJ search by customer id end */
    	handler.setState( DsjKeys.DSJ_CUSTOMER_JSON);
		return handler.getResultBean();
        }
	}

}
