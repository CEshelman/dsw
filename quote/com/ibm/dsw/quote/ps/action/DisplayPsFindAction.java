package com.ibm.dsw.quote.ps.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.security.SecurityUtil;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.DBConstants;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfigurationFactory;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.ps.config.PartSearchMessageKeys;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.dsw.quote.ps.config.PartSearchStateKeys;
import com.ibm.dsw.quote.ps.contract.PartSearchContract;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>DisplaySearchCustomerAction</code>
 *
 *
 * @author: chenzhh@cn.ibm.com
 *
 * Creation date: Jan 26, 2007
 */
public class DisplayPsFindAction extends PartSearchBaseAction {
	
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.SimpleContractAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return PartSearchStateKeys.STATE_DISPLAY_PARTSEARCH_FIND;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean perform(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
        PartSearchContract psCt = (PartSearchContract)contract;
        validateNonInputParam(psCt);
        LogContext logger = LogContextFactory.singleton().getLogContext();
        String lob = psCt.getLob();
        boolean addSuccess = psCt.isAddSuccess();
        String exccedCode = psCt.getExceedCode();
        if(null != psCt.getPartNumbers()){
            handler.addObject(PartSearchParamKeys.PARAM_PART_NUMBERS,psCt.getPartNumbers());
            handler.addObject(PartSearchParamKeys.PARAM_DATA_RETRIEVALTYPE, PartSearchParamKeys.SEARCH_PARTS_BY_NUMBERS);
        }

        if(null != psCt.getPartDescription()){
            handler.addObject(PartSearchParamKeys.PARAM_PART_DESCRIPTION,psCt.getPartDescription());
            handler.addObject(PartSearchParamKeys.PARAM_DATA_RETRIEVALTYPE, PartSearchParamKeys.SEARCH_PARTS_BY_DESCRIPTION);
        }

        CacheProcess cp = CacheProcessFactory.singleton().create();

        handler.addObject(PartSearchParamKeys.TREE_NAME, "none");

        handler.addObject(ParamKeys.PARAM_LINE_OF_BUSINESS, lob);

        handler.addObject(ParamKeys.PARAM_PROG_MIGRATION_CODE, psCt.getProgMigrationCode());
        
        handler.addObject(ParamKeys.PARAM_QUOTE_FLAG, psCt.getQuoteFlag());

        Country country = cp.getCountryByCode3(psCt.getCountry());
        if(country != null){
            handler.addObject(ParamKeys.PARAM_COUNTRY, country);
            String currencyCode = psCt.getCurrency();
            handler.addObject(ParamKeys.PARAM_CURRENCY,currencyCode);

            List currencyList = country.getCurrencyList();
            if (currencyList.size() == 0){
                throw new QuoteException("Country:"+psCt.getCountry()+" has no default currency");
            }else{
            	CodeDescObj cntryCurrency = (CodeDescObj) currencyList.get(0);
            	handler.addObject(PartSearchParamKeys.PARAM_COUNTRY_CURRENCY, cntryCurrency);
                if (!currencyCode.equalsIgnoreCase((cntryCurrency).getCode())){
                    handler.addObject(PartSearchParamKeys.PARAM_SHOWHINT,Boolean.TRUE);
                }
            }
        }else{
            throw new QuoteException("The input country does not exist!");
        }

        List<MonthlySoftwareConfiguration> confgrtnList;
		try {
			String chrgAgrmtNum =psCt.getChrgAgrmtNum();
			String orgConfigId =psCt.getOrgConfigId();
			String configrtnActionCode =psCt.getConfigrtnActionCode();
			confgrtnList = MonthlySoftwareConfigurationFactory.singleton().findMonthlySwConfiguration(psCt.getQuoteNum());
			if(confgrtnList !=null && confgrtnList.size() > 0){
        	for( MonthlySoftwareConfiguration confgrt  : confgrtnList){
        		if(confgrt.isAddNewMonthlySWFlag()){
        			psCt.setIsAddNewMonthlySWFlag("true");
        			psCt.setConfigrtnActionCode(StringUtils.isBlank(confgrt.getConfigrtnActionCode()) ? configrtnActionCode : confgrt.getConfigrtnActionCode());
        			psCt.setChrgAgrmtNum(StringUtils.isBlank(confgrt.getChrgAgrmtNum()) ? chrgAgrmtNum : confgrt.getChrgAgrmtNum());
        			psCt.setOrgConfigId(StringUtils.isBlank(confgrt.getConfigrtnIdFromCa()) ? orgConfigId : confgrt.getConfigrtnIdFromCa());
        			psCt.setConfigrtnId(confgrt.getConfigrtnId());       			
        			break;
        			}
        		}
			}			
		} catch (TopazException e) {
			logger.error(this, e);
		}
		/*
		// validate  chrgAgrmtNum ---numeric
		if(StringUtils.isNotBlank(psCt.getChrgAgrmtNum()) && !StringUtils.isNumeric(psCt.getChrgAgrmtNum())){
			throw new QuoteException("Invalid chrgAgrmtNum value.");
		}
		// validate  configrtnId ---numeric
		if(StringUtils.isNotBlank(psCt.getConfigrtnId()) && !StringUtils.isNumeric(psCt.getConfigrtnId())){
			throw new QuoteException("Invalid configrtnId value.");
		}
		// validate  orgConfigId ---numeric
		if(StringUtils.isNotBlank(psCt.getOrgConfigId()) && !StringUtils.isNumeric(psCt.getOrgConfigId())){
			throw new QuoteException("Invalid orgConfigId value.");
		}
		// validate  configrtnActionCode ---alpha
		if(StringUtils.isNotBlank(psCt.getConfigrtnActionCode()) && !StringUtils.isAlpha(psCt.getConfigrtnActionCode())){
			throw new QuoteException("Invalid configrtnActionCode value.");
		}
		// validate  isAddNewMonthlySWFlag ---alpha
		if(StringUtils.isNotBlank(psCt.getIsAddNewMonthlySWFlag()) && !StringUtils.isAlpha(psCt.getIsAddNewMonthlySWFlag())){
			throw new QuoteException("Invalid isAddNewMonthlySWFlag value.");
		}*/
		
		if(!SecurityUtil.isValidInput("ChrgAgrmtNum",psCt.getChrgAgrmtNum(), "Numeric", 20, true)){
			throw new QuoteException("Invalid chrgAgrmtNum value.");
		}
		if(!SecurityUtil.isValidInput("configrtnId",psCt.getConfigrtnId(), "Numeric", 20, true)){
			throw new QuoteException("Invalid configrtnId value.");
		}
		if(!SecurityUtil.isValidInput("orgConfigId",psCt.getOrgConfigId(), "Numeric", 20, true)){
			throw new QuoteException("Invalid orgConfigId value.");
		}
		if(!SecurityUtil.isValidInput("configrtnActionCode",psCt.getConfigrtnActionCode(), "Alpha", 20, true)){
			throw new QuoteException("Invalid configrtnActionCode value.");
		}
		if(!SecurityUtil.isValidInput("isAddNewMonthlySWFlag",psCt.getIsAddNewMonthlySWFlag(), "Alpha", 20, true)){
			throw new QuoteException("Invalid isAddNewMonthlySWFlag value.");
		}
        
        handler.addObject(ParamKeys.PARAM_AUDIENCE, psCt.getAudience());
        handler.addObject(ParamKeys.PARAM_QUOTE_NUM, psCt.getQuoteNum());
        handler.addObject(PartSearchParamKeys.PARAM_RENEWAL, psCt.isRenewal());
        handler.addObject(ParamKeys.PARAM_ADD_NEW_MONTHLY_SW, psCt.getIsAddNewMonthlySWFlag());
        handler.addObject(ParamKeys.PARAM_CHRG_AGRMT_NUM, psCt.getChrgAgrmtNum());
        handler.addObject(ParamKeys.PARAM_CONFIGRTN_ID, psCt.getConfigrtnId());
        handler.addObject(ParamKeys.PARAM_CONFIGRTN_ACTION_CODE, psCt.getConfigrtnActionCode());
        handler.addObject(ParamKeys.PARAM_ORG_CONFIG_ID, psCt.getOrgConfigId());
        
        handler.setState(getState(contract));
        
        if (String.valueOf(DBConstants.DB2_SP_ALREADY_IS_MAX).equals(exccedCode)){
            String message = getI18NString(PartSearchMessageKeys.ALREADY_HAS_MAX,I18NBundleNames.PART_SEARCH_BASE, psCt.getLocale(), new String[]{(PartPriceConfigFactory.singleton().getElaLimits()+""),(PartPriceConfigFactory.singleton().getAppliLimits()+"")});
            ResultBean rb = handler.getResultBean();
            rb.getMessageBean().addMessage(message ,MessageBeanKeys.INFO);
        }else if (String.valueOf(DBConstants.DB2_SP_EXCEED_MAX).equals(exccedCode)){
            String message = getI18NString(PartSearchMessageKeys.EXCEED_MAX,I18NBundleNames.PART_SEARCH_BASE, psCt.getLocale(), new String[]{(PartPriceConfigFactory.singleton().getElaLimits()+""),(PartPriceConfigFactory.singleton().getAppliLimits()+"")});
            ResultBean rb = handler.getResultBean();
            rb.getMessageBean().addMessage(message ,MessageBeanKeys.INFO);
        }else if(addSuccess){
            String message = getI18NString(PartSearchMessageKeys.ADD_SUCCESS,I18NBundleNames.PART_SEARCH_BASE, psCt.getLocale());
            ResultBean rb = handler.getResultBean();
            rb.getMessageBean().addMessage(message ,MessageBeanKeys.INFO);
            //System.err.println("Info message added!");
        }

        return  handler.getResultBean();
    }
    
    /**
     * validate the input which was not inputted by user
     * @author suchuang
     * @date Jun 6, 2013
     * @param contract ProcessContract object
     * @throws QuoteException
     */
    protected void validateNonInputParam(ProcessContract contract) throws QuoteException {
    	PartSearchContract psContract = (PartSearchContract) contract;
    	if (StringUtils.isBlank(psContract.getQuoteFlag())) {
    		return;
    	}
    	
        if(!DraftQuoteParamKeys.PARAM_QUOTE_FLAG_EMPTY.equals(psContract.getQuoteFlag()) 
        		&& !DraftQuoteParamKeys.PARAM_QUOTE_FLAG_SOFTWARE.equals(psContract.getQuoteFlag()) 
        		&& !DraftQuoteParamKeys.PARAM_QUOTE_FLAG_SAAS.equals(psContract.getQuoteFlag())) {
        	throw new QuoteException("quoteFlag : [" + psContract.getQuoteFlag() + "] is invalid value");
        }
    }
    
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#getValidationForm()
     */
    protected String getValidationForm() {
        return super.getValidationForm();

        /*
         * required fields are  retrieve type, lob, country, currency, audience
         */
    }
}
