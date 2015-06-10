package com.ibm.dsw.quote.dsj.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.bluepages.BluePageUser;
import com.ibm.dsw.quote.bluepages.BluePagesLookup;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.SalesRepFactory;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.dsj.contract.DsjBaseContract;
import com.ibm.dsw.quote.dsj.util.DsjKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.dsw.quote.newquote.config.NewQuoteViewKeys;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidInputException;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>DsjNewSalesQuoteAction<code> class.
 *    
 * @author: bjlbo@cn.ibm.com
 * 
 * Creation date: June 03, 2015
 */
public class DsjNewSalesQuoteAction extends DsjBaseAction {

	private static final long serialVersionUID = -4455161548715514244L;
	private static final String VALIDE_RETURN_CODE= "0";
	private static final String NOT_CONNECT_RETURN_CODE= "1";
	public static final String PARAM_NEW_DSJ_QUOTE = "newDsjQuote";
	
    public ResultBean executeBiz(DsjBaseContract contract, ResultHandler handler) throws ResultBeanException  
             {
    	DsjBaseContract newSalesQuoteContract = (DsjBaseContract) contract;
	        logContext.debug(this, newSalesQuoteContract.toString());
        TransactionContextManager.singleton().begin();
        SalesRep oppOwner = null;
    	try{
	       
	
	        CacheProcess CacheProcess = CacheProcessFactory.singleton().create();
	        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
	
	        String cntryCode = newSalesQuoteContract.getCountry();
	        String lobCode = newSalesQuoteContract.getLob();
	        String progMigrationCode = "";
	        
	        // only FCT quote or FCT 2 PA quote has acquisition code.
	        String acqstnCode = "";
	        if (CustomerConstants.LOB_FCT.equalsIgnoreCase(lobCode)
	                || QuoteConstants.MIGRTN_CODE_FCT_TO_PA.equalsIgnoreCase(lobCode)) {
	            acqstnCode = newSalesQuoteContract.getAcquisition();
	        }
	        
	        if (StringUtils.isBlank(cntryCode) || StringUtils.isBlank(lobCode)) {
	            throw new NewQuoteInvalidInputException();
	        }
	        
	        CodeDescObj lob = null;
	        if (QuoteConstants.MIGRTN_CODE_FCT_TO_PA.equalsIgnoreCase(lobCode)) {
	            lobCode = CustomerConstants.LOB_PAUN;
	            progMigrationCode = QuoteConstants.MIGRTN_CODE_FCT_TO_PA;
	            
	            if (StringUtils.isBlank(acqstnCode))
	                throw new NewQuoteInvalidInputException();
	        }
	        
	        Country country = CacheProcess.getCountryByCode3(cntryCode);
	        lob = CacheProcess.getLOBByCode(lobCode);
        
        	quoteProcess.createNewSessionQuote(country, lob, acqstnCode, newSalesQuoteContract.getUserId(), progMigrationCode, "", newSalesQuoteContract.getQuoteUserSession().getAudienceCode());
        	// Get quote header and customer.
        	Quote quote = null;
            QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
            String creatorId = newSalesQuoteContract.getUserId();
            quote = qProcess.getDraftQuoteBaseInfo(creatorId);
            
            String webQuoteNum=quote.getQuoteHeader().getWebQuoteNum();
            //Store the opportunity number and brief title info in DB2
            quoteProcess.dsjUpdateQuoteInfo(webQuoteNum, newSalesQuoteContract.getBriefTitle(),  newSalesQuoteContract.getOpprtntyNum(), newSalesQuoteContract.getUserId());
        	
            //update dsj flag in DB2 
            quoteProcess.dsjInsertQuoteFlag(webQuoteNum, DsjKeys.DSJ_CHANNEL, "", creatorId);
            oppOwner = SalesRepFactory.singleton().createSalesRep(newSalesQuoteContract.getOppOwnerEmailAddr());
            BluePageUser bUser = BluePagesLookup.getBluePagesInfo(oppOwner.getEmailAddress());
            oppOwner.setBluepageInformation(bUser.getCountryCode(), bUser.getFullName(), bUser.getLastName(), bUser
                    .getFirstName(), bUser.getPhoneNumber(), bUser.getFaxNumber(), null, null, oppOwner.getEmailAddress(),
                    bUser.getNotesId(), bUser.getBluePagesId());
            TransactionContextManager.singleton().commit();
            
            quoteProcess.updateOppOwner(newSalesQuoteContract.getUserId(), oppOwner);
            handler.addObject( NewQuoteParamKeys.PARAM_RETURN_OPPNUM_CODE, VALIDE_RETURN_CODE);
        	handler.addObject( DsjNewSalesQuoteAction.PARAM_NEW_DSJ_QUOTE, quote);
        	
        }catch(QuoteException e){
        	logContext.error(this, e.getMessage());
        	handler.addObject( NewQuoteParamKeys.PARAM_RETURN_OPPNUM_CODE, NOT_CONNECT_RETURN_CODE);
        } catch (NoDataException e) {
        	logContext.error(this, e.getMessage());
        	handler.addObject( NewQuoteParamKeys.PARAM_RETURN_OPPNUM_CODE, NOT_CONNECT_RETURN_CODE);
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
        	handler.addObject( NewQuoteParamKeys.PARAM_RETURN_OPPNUM_CODE, NOT_CONNECT_RETURN_CODE);
		}finally {
            try {
                TransactionContextManager.singleton().rollback();
            } catch (TopazException te) {
                logContext.error(this, te, "problems raised when doing rollback ");
                handler.addObject( NewQuoteParamKeys.PARAM_RETURN_OPPNUM_CODE, NOT_CONNECT_RETURN_CODE);
            }
        }
      

      /*  if (NewQuoteParamKeys.PARAM_MARK_DEFAULT_VALUE.equalsIgnoreCase(newSalesQuoteContract.getMarkAsDefault())) {
            //find cookie
            Cookie cookie = newSalesQuoteContract.getSqoCookie();
            //set values
            QuoteCookie.setCountryCookieValue(cookie, newSalesQuoteContract.getCountry());
            QuoteCookie.setLOBCookieValue(cookie, newSalesQuoteContract.getLob());
            QuoteCookie.setAcquisitionCookieValue(cookie, newSalesQuoteContract.getAcquisition());
        }*/
      
    	handler.setState( DsjKeys.DSJ_NEW_QUOTE_RESULT_JSON );
        return handler.getResultBean();
    }

    protected String getValidationForm() {
        return NewQuoteViewKeys.FORM_NEW_SALES_QUOTE;
    }

}
