package com.ibm.dsw.quote.ps.action;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfigurationFactory;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.dsw.quote.ps.contract.SaveSelectedPartsContract;
import com.ibm.dsw.quote.ps.process.PartSearchProcess;
import com.ibm.dsw.quote.ps.process.PartSearchProcessFactory;
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
 * The <code>SaveSelectedPartsAction</code>
 *
 * @author: chenzhh@cn.ibm.com
 *
 * Creation date: Jan 26, 2007
 */
public class SaveSelectedPartsAction extends BaseContractActionHandler {

    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        PartSearchProcess process;
        String creatorId;
        String exccedCode="";
        //convert to the concrete class instance
        SaveSelectedPartsContract savePartsContract = (SaveSelectedPartsContract) contract;

        //get data from contract
        List selectedParts = savePartsContract.getSelectedParts();

        String searchString = savePartsContract.getSearchString();

        String retrievalType = savePartsContract.getDataRetrievalType();

        //call business process
        try {
            //this will be a process but temp placeholder method for now
            creatorId = savePartsContract.getUserId();
            
            String chrgAgrmtNum =savePartsContract.getChrgAgrmtNum();
			String orgConfigId =savePartsContract.getOrgConfigId();
			String configrtnActionCode =savePartsContract.getConfigrtnActionCode();
            List<MonthlySoftwareConfiguration> confgrtnList = MonthlySoftwareConfigurationFactory.singleton().findMonthlySwConfiguration(savePartsContract.getQuoteNum());
            if(confgrtnList !=null && confgrtnList.size() > 0){
            	for( MonthlySoftwareConfiguration confgrt  : confgrtnList){
            		if(confgrt.isAddNewMonthlySWFlag()){
            			savePartsContract.setIsAddNewMonthlySWFlag("true");
            			savePartsContract.setConfigrtnActionCode(StringUtils.isBlank(confgrt.getConfigrtnActionCode()) ? configrtnActionCode : confgrt.getConfigrtnActionCode());
            			savePartsContract.setChrgAgrmtNum(StringUtils.isBlank(confgrt.getChrgAgrmtNum()) ? chrgAgrmtNum : confgrt.getChrgAgrmtNum());
            			savePartsContract.setOrgConfigId(StringUtils.isBlank(confgrt.getConfigrtnIdFromCa()) ? orgConfigId : confgrt.getConfigrtnIdFromCa());
            			savePartsContract.setConfigrtnId(confgrt.getConfigrtnId()); 
            			break;
            		}
            	}
            }
            
            // validate  chrgAgrmtNum ---numeric
    		if(StringUtils.isNotBlank(savePartsContract.getChrgAgrmtNum()) && !StringUtils.isNumeric(savePartsContract.getChrgAgrmtNum())){
    			throw new QuoteException("Invalid chrgAgrmtNum value.");
    		}
    		// validate  configrtnId ---numeric
    		if(StringUtils.isNotBlank(savePartsContract.getConfigrtnId()) && !StringUtils.isNumeric(savePartsContract.getConfigrtnId())){
    			throw new QuoteException("Invalid configrtnId value.");
    		}
    		// validate  orgConfigId ---numeric
    		if(StringUtils.isNotBlank(savePartsContract.getOrgConfigId()) && !StringUtils.isNumeric(savePartsContract.getOrgConfigId())){
    			throw new QuoteException("Invalid orgConfigId value.");
    		}
    		// validate  configrtnActionCode ---alpha
    		if(StringUtils.isNotBlank(savePartsContract.getConfigrtnActionCode()) && !StringUtils.isAlpha(savePartsContract.getConfigrtnActionCode())){
    			throw new QuoteException("Invalid configrtnActionCode value.");
    		}
    		// validate  isAddNewMonthlySWFlag ---alpha
    		if(StringUtils.isNotBlank(savePartsContract.getIsAddNewMonthlySWFlag()) && !StringUtils.isAlpha(savePartsContract.getIsAddNewMonthlySWFlag())){
    			throw new QuoteException("Invalid isAddNewMonthlySWFlag value.");
    		}

            process = PartSearchProcessFactory.singleton().create();
            if (savePartsContract.isReplacementFlag()){
                exccedCode = process.addReplForObsParts(savePartsContract.getQuoteNum(),
                                savePartsContract.getSeqNum(),selectedParts) + "";
            }else{
            	
                exccedCode=process.saveSelectedParts(selectedParts, creatorId, searchString, retrievalType,savePartsContract.getChrgAgrmtNum(),savePartsContract.getConfigrtnActionCode(),savePartsContract.getOrgConfigId() )+"";
            }

           
        }catch (TopazException te) {
            logger.error(this, te);
        } catch (QuoteException qe) {
            logger.error(this, qe);
        } catch (RemoteException re) {
            logger.error(this, re);
        }
        
    

        //assemble result bean
        handler.addObject(PartSearchParamKeys.PARAM_ADD_NEW_MONTHLY_SW, savePartsContract.getIsAddNewMonthlySWFlag());
        handler.addObject(ParamKeys.PARAM_CHRG_AGRMT_NUM, savePartsContract.getChrgAgrmtNum());
        handler.addObject(ParamKeys.PARAM_CONFIGRTN_ID, savePartsContract.getConfigrtnId());
        handler.addObject(ParamKeys.PARAM_CONFIGRTN_ACTION_CODE, savePartsContract.getConfigrtnActionCode());
        handler.addObject(ParamKeys.PARAM_ORG_CONFIG_ID, savePartsContract.getOrgConfigId());
        handler.addObject(PartSearchParamKeys.PARAM_DATA_RETRIEVALTYPE, retrievalType);
        String redirectURL = HtmlUtil.getURLForAction(savePartsContract.getRedirectAction(retrievalType,exccedCode));
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        /*
        if(retrievalType.equals("browse")) {
            handler.setState(PartSearchStateKeys.STATE_DISPLAY_PARTSEARCH_BROWSE_REDIRECT);
        } else {
            handler.setState(PartSearchStateKeys.STATE_DISPLAY_PARTSEARCH_FIND_REDIRECT);
        }
        */

        return handler.getResultBean();
    }

}
