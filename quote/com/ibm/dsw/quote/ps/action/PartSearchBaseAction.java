/*
 * Created on Nov 13, 2009
 */
package com.ibm.dsw.quote.ps.action;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.util.CommonUIValidator;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.dsw.quote.ps.contract.PartSearchContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author Gavin
 */
public class PartSearchBaseAction extends BaseContractActionHandler{
    public static final String PARAM_NULL_VALUE = "null";
    
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        PartSearchContract psCt = (PartSearchContract)contract;
        
        if(StringUtils.isBlank(psCt.getLob())){
            throw new QuoteException("No lob specified!");
        }
        
        //Check if lob code is valid
        if(!CommonUIValidator.isLobValid(psCt.getLob())){
            logger.error(this, "Invalid lob " + psCt.getLob() + " provided to part search");
            throw new QuoteException("LOB code invalid");
        }
        
        //Check if quote number is valid
        if(needToValidate(psCt.getQuoteNum()) && !CommonUIValidator.isQuoteNumValid(psCt.getQuoteNum())){
            logger.error(this, "Invalid quoteNum '" + psCt.getQuoteNum() + "' specified");
            throw new QuoteException("Invalid quoteNum specified");
        }
        
        //Check if audience code is valid
        if(needToValidate(psCt.getAudience()) && !CommonUIValidator.isAudienceValid(psCt.getAudience())){
            logger.error(this, "Invalid audience '" + psCt.getAudience() + "' specified");
            throw new QuoteException("Invalid audience specified");
        }
        
        //Check if acquisition code is valid
        if(needToValidate(psCt.getAcqrtnCode())){
            CacheProcess cache = CacheProcessFactory.singleton().create();
            Object obj = cache.getAcquisitionByCode(psCt.getAcqrtnCode());
            
            //unable to locate acquisition from cache, should be invalid acquition code
            if(obj == null){
                logger.error(this, "Invalid acquisition code: '" + psCt.getAcqrtnCode() + "' specified");
                throw new QuoteException("Invalid acquisition specified");
            }
        }
        
        if(needToValidate(psCt.getCurrency()) && !CommonUIValidator.isCurrencyValid(psCt.getCurrency())){
            logger.error(this, "Invalid currency code '" + psCt.getCurrency() + "' specified");
            throw new QuoteException("Invalid currency code specified");
        }
        
        if(needToValidate(psCt.getRetrievalType()) && !isRetrievalTypeValid(psCt.getRetrievalType())){
            logger.error(this, "Invalid retrievalType '" + psCt.getRetrievalType() + "' specified");
            throw new QuoteException("Invalid retrievalType specified");
        }
        
        if(needToValidate(psCt.getPartNumber()) && !CommonUIValidator.isPartNumberValid(psCt.getPartNumber())){
            logger.error(this, "Invalid part number '" + psCt.getPartNumber() + "' specified");
            throw new QuoteException("Invalid part number specified");
        }
        
        if(needToValidate(psCt.getSeqNum()) && !CommonUIValidator.isPartSeqNumberValid(psCt.getSeqNum())){
            logger.error(this, "Invalid part seq number '" + psCt.getSeqNum() + "' specified");
            throw new QuoteException("Invalid part seq number specified");            
        }
        
        //Check if partBrand is valid
        if(needToValidate(psCt.getPartBrand()) && !CommonUIValidator.isPartBrandValid(psCt.getPartBrand())){
            logger.error(this, "Invalid partBrand '" + psCt.getPartBrand() + "' specified");
            throw new QuoteException("Invalid partBrand specified");
        }
        
        return perform(contract, handler);
    }
    
    private boolean needToValidate(String param){
        return StringUtils.isNotBlank(param)
          && !PARAM_NULL_VALUE.equals(param);
    }
    
    private boolean isRetrievalTypeValid(String retrievalType){
        if(PartSearchParamKeys.GET_RELATED_PARTS.equals(retrievalType)
        		|| PartSearchParamKeys.GET_RELATED_PARTS_LIC.equals(retrievalType)
                || PartSearchParamKeys.GET_REPLACEMENT_PARTS.equals(retrievalType)){
            return true;
        }
        
        return false;
    }
    
    protected ResultBean perform(ProcessContract contract, ResultHandler handler) throws QuoteException, ResultBeanException{
        return null;
    }
}
