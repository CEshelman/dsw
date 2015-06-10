package com.ibm.dsw.quote.customer.action;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.BillingOption;
import com.ibm.dsw.quote.appcache.domain.jdbc.BillingOption_jdbc;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.ChargeAgreement;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.dao.SaasConfiguratorDaoFactory;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.preprocessor.ConfigPreProcessor;
import com.ibm.dsw.quote.customer.config.CustomerActionKeys;
import com.ibm.dsw.quote.customer.config.CustomerParamKeys;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
import com.ibm.dsw.quote.customer.contract.PrepareConfiguratorRedirectDataBaseContract;
import com.ibm.dsw.quote.customerlist.domain.ActiveService;
import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PrepareConfiguratorRedirectDataBaseAction<code> class.
 * This action is responsible for recieving informations from 'brand new' or 'Add-on/tread-up' and prepare parameters sending to CPQ(or SQO configuration) 
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: 2011-6-16
 */
public class PrepareConfiguratorRedirectDataBaseAction extends BaseContractActionHandler{
	private transient LogContext logContext = LogContextFactory.singleton().getLogContext();
//	protected String configurationURL = null;
//	protected HashMap redirectParams = new HashMap(); 
//	protected HashMap<String,String> returnedParams = new HashMap(); 
//	protected String configuratorType = CustomerParamKeys.SQO;
	
	public ConfigPreProcessor getPreProcessor(){
		return null;
	}

	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		
		PrepareConfiguratorRedirectDataBaseContract ct = (PrepareConfiguratorRedirectDataBaseContract)contract;
		RedirectConfiguratorDataBasePack dataPack = this.getRedirectConfiguratorDataPack(ct);
		String configPunchInURL = this.getConfigurationURL(ct,dataPack);
		handler.addObject(CustomerParamKeys.PARAM_SERVICE_CONFIGURATION_URL, configPunchInURL);
		
		this.preAssembleDataPack(ct, dataPack, handler);
		this.assembleDataPack(ct, dataPack);
		this.afterAssembleDataPack(ct, dataPack);
		if(this.getPreProcessor()!=null){
			this.getPreProcessor().doPreProcess(dataPack);
		}
		

		logContext.debug(this, "Quote["+dataPack.getQuote().getQuoteHeader().getWebQuoteNum()+"] Configurator Punching In URL["+dataPack.getConfiguratorType()+"] is: "+configPunchInURL);
		
		handler.addObject(CustomerParamKeys.PARAM_CPA_DATA_PACK, dataPack);
		if(ct.getDebuger()!=null&&"1".equals(ct.getDebuger())){
			handler.addObject(CustomerParamKeys.PARAM_DEBUGER, "1");
		}
		
        handler.setState(CustomerStateKeys.STATE_REDIRECT_TO_CONF_FORM);
		return handler.getResultBean();
	}
	
	protected RedirectConfiguratorDataBasePack  getRedirectConfiguratorDataPack(PrepareConfiguratorRedirectDataBaseContract ct){
		return new RedirectConfiguratorDataBasePack();
	}
	
	public Quote fetchCurrentQuote(PrepareConfiguratorRedirectDataBaseContract ct, RedirectConfiguratorDataBasePack dataPack)throws QuoteException{
		 	QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();

	        Quote quote = null;
	        
	        try {
	            quote = quoteProcess.getDraftQuoteBaseInfoWithTransaction(ct.getUserId());
	        } catch (NoDataException e) {
	            logContext.error(this, "NoDataExceptoin accor when geting quote base info.");
	            logContext.error(this, e.getMessage());
	            return null; 
	        } catch (QuoteException e) {
	            logContext.error(this, e.getMessage());
	            throw e;
	        }
	        return quote;
	}
	
	private void preAssembleDataPack(PrepareConfiguratorRedirectDataBaseContract ct, RedirectConfiguratorDataBasePack dataPack, ResultHandler handler) throws QuoteException
	{

       
		Quote quote = this.fetchCurrentQuote(ct, dataPack);
        dataPack.setQuote(quote);      
		handler.addObject(CustomerParamKeys.PARAM_SERVICE_CONFIGURATION_URL, this.getConfigurationURL(ct,dataPack));
		
        if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equalsIgnoreCase(ct.getQuoteUserSession().getAudienceCode())){
            dataPack.setConsumingAppID(CustomerParamKeys.PGS);
        }else{
            dataPack.setConsumingAppID(CustomerParamKeys.SQO);
        }
        
        dataPack.setReferenceNum(quote.getQuoteHeader().getWebQuoteNum());
        dataPack.setRedirectParams(new HashMap()); 
        dataPack.setReturnedParams(new HashMap()); 
        
        if (quote.getCustomer()!=null && quote.getCustomer().getContractList() != null && quote.getCustomer().getContractList().size() > 0) {
            Contract contract = (Contract) quote.getCustomer().getContractList().get(0);
            String svpLevel = contract.getVolDiscLevelCode();
            dataPack.setBandLevel(svpLevel);
        }
        
        
        dataPack.setOfferingCode(ct.getPid());
        if(CustomerParamKeys.CPQ.equals(dataPack.getConfiguratorType())){
        	dataPack.setCountry(quote.getQuoteHeader().getCountry().getCode3());
        	dataPack.setRegion(quote.getQuoteHeader().getCountry().getWWRegion());
        	dataPack.setCurrencyCode(quote.getQuoteHeader().getCountryCurrencyCode());
        }else{
        	dataPack.setRegion(quote.getQuoteHeader().getCountry().getCode3());
        	dataPack.setCurrencyCode(quote.getQuoteHeader().getCurrencyCode());
        }
        dataPack.setLob(quote.getQuoteHeader().getLob().getCode());
        if(StringUtils.isNotBlank(ct.getChrgAgrmtNum())){
//        	this.addReturnedParams(dataPack, CustomerParamKeys.PARAM_RETURN_TO_SQO_CHRG_AGR_NUM, ct.getChrgAgrmtNum().trim());
        	dataPack.setChrgAgrmtNum(ct.getChrgAgrmtNum().trim());
        }

        if(StringUtils.isNotBlank(ct.getTarChrgAgrmtNum())){
        	dataPack.setChrgAgrmtNum(ct.getTarChrgAgrmtNum().trim());
        }
        
        if(StringUtils.isNotBlank(ct.getConfigId())){
//        	this.addReturnedParams(dataPack, CustomerParamKeys.PARAM_RETURN_TO_SQO_SQO_CONFIG_ID, ct.getConfigId().trim());
        	dataPack.setConfigId(ct.getConfigId().trim());
        }
        if(quote.getQuoteHeader().isSaasFCTToPAQuote() && StringUtils.isNotBlank(ct.getOrgConfigId())){
        	dataPack.setOrgConfigId(ct.getOrgConfigId().trim());
        }
        if(StringUtils.isNotBlank(dataPack.getChrgAgrmtNum()) && (StringUtils.isNotBlank(dataPack.getConfigId()) || StringUtils.isNotBlank(dataPack.getOrgConfigId()))){
        	String configId = null;
        	if(StringUtils.isNotBlank(dataPack.getOrgConfigId())){
        		configId = dataPack.getOrgConfigId();
        	}else{
        		configId = dataPack.getConfigId();
        	}
        	List<ActiveService> activeServices = this.retrieveActiveServicesAlreadyOnCA(dataPack.getChrgAgrmtNum(), configId); 
        	dataPack.setExistingPartsInCA(activeServices);
        	
        }
	}
	
	protected void assembleDataPack(PrepareConfiguratorRedirectDataBaseContract ct, RedirectConfiguratorDataBasePack dataPack) throws QuoteException,
    	ResultBeanException{
		
        
	}

	private void afterAssembleDataPack(PrepareConfiguratorRedirectDataBaseContract ct, RedirectConfiguratorDataBasePack dataPack)throws QuoteException{
					
			dataPack.getReturnedParams().put(ConfiguratorParamKeys.AddOnTradeUpFlag, dataPack.getAddTradeFlag());
	        if(dataPack.getConfiguratorType() != null && (!dataPack.getConfiguratorType().equals(CustomerParamKeys.CPQ) && !dataPack.getConfiguratorType().equals(CustomerParamKeys.SQO) && !dataPack.getConfiguratorType().equals(CustomerParamKeys.PGS))){
	        	logContext.error(this, "invalid configuratorType value:" +dataPack.getConfiguratorType());
	        	throw new QuoteException("get configuratorType failed.configuratorType:"+dataPack.getConfiguratorType());
	        }
	        
	        dataPack.getReturnedParams().put(CustomerParamKeys.PARAM_RETURN_TO_SQO_CONFIGURATORTYPE, dataPack.getConfiguratorType());
	        
			dataPack.getReturnedParams().put(CustomerParamKeys.PARAM_RETURN_TO_SQO_CHRG_AGR_NUM, dataPack.getChrgAgrmtNum());
			dataPack.getReturnedParams().put(CustomerParamKeys.PARAM_RETURN_TO_SQO_SQO_CONFIG_ID,dataPack.getConfigId());
			dataPack.getReturnedParams().put(CustomerParamKeys.PARAM_RETURN_TO_SQO_CONFIGRTNACTION, dataPack.getConfigrtnActionCode());
			dataPack.getReturnedParams().put(ConfiguratorParamKeys.OfferingCode, dataPack.getOfferingCode());
			dataPack.getReturnedParams().put(CustomerParamKeys.PARAM_RETURN_TO_SQO_OVERRIDE_FLAG, ct.getOverrideFlag());
			
			dataPack.setOverrideFlag(ct.getOverrideFlag());
        
        if(dataPack.getQuote()!=null){
        	dataPack.getReturnedParams().put(CustomerParamKeys.PARAM_RETURN_TO_SQO_WEB_QUOTE_NUM, dataPack.getQuote().getQuoteHeader().getWebQuoteNum());
        }
        
        

		if(StringUtils.isNotBlank(ct.getCaEndDate())){
			dataPack.setCaEndDate(DateUtil.formatDate(DateUtil.parseDate(ct.getCaEndDate(), DateUtil.PATTERN4),DateUtil.PATTERN)) ;
			dataPack.getReturnedParams().put(CustomerParamKeys.PARAM_RETURN_TO_SQO_CA_END_DATE, ct.getCaEndDate());
		}
		
		if(StringUtils.isNotBlank(dataPack.getChrgAgrmtNum()) && StringUtils.isNotBlank(dataPack.getConfigId())){
			QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
			ChargeAgreement ca = quoteProcess.getChargeAgreementInfo(dataPack.getChrgAgrmtNum(),dataPack.getConfigId());
			if(ca.getEndDate()!=null){
				dataPack.setCaEndDate(DateUtil.formatDate(ca.getEndDate(),DateUtil.PATTERN));
			}
		}
		

		
		if(CustomerConstants.CONFIGURATOR_CONFIGRTN_ACTION_FCTToPA.equals(dataPack.getConfigrtnActionCode())){
			List<ActiveService> ass = dataPack.getActiveServices();
			if(ass!=null && ass.size()>0){
				for(ActiveService as: ass){
					if(as.getCotermEndDate()!=null){
						dataPack.setCaEndDate(DateUtil.formatDate(as.getCotermEndDate()));
						break;
					}
				}
			}
		}
		
		
		if(CustomerParamKeys.SQO.equals(dataPack.getConfiguratorType())){
			dataPack.setAvaliableBillingOptions(this.getValidBillingOptions(
					dataPack.getChrgAgrmtNum(), 
					dataPack.getConfigId(), 
					dataPack.getOfferingCode(), 
					dataPack.getQuote().getQuoteHeader().getWebQuoteNum(),
					dataPack));
		}
		
		dataPack.setRedirectURL(PrepareConfiguratorRedirectDataBaseAction.getFinalReturnedURL(dataPack));
		logContext.debug(this, dataPack.toString());
		
	}
	

    public void addReturnedParams(RedirectConfiguratorDataBasePack dataPack, String paramName, String paramVal){
    	dataPack.getReturnedParams().put(paramName, paramVal);
    }
    
//    public String getReturnedAction(){
//    	return CustomerActionKeys.RETURN_FROM_CONF_TO_SQO;
//    }

    public static String getFinalReturnedURL(RedirectConfiguratorDataBasePack dataPack){
    	String returnedURL = null;
    	if(CustomerParamKeys.CPQ.equals(dataPack.getConfiguratorType())){
    		returnedURL = HtmlUtil.getURLForSqoFullPath(CustomerActionKeys.RETURN_FROM_CONF_TO_SQO);
    	}else{
    		returnedURL = HtmlUtil.getURLForAction(CustomerActionKeys.RETURN_FROM_CONF_TO_SQO);    		
    	}
    	if(dataPack.getReturnedParams().size()>0){
    		String paramName = null;
    		String paramVal = null;
    		for(Iterator i = dataPack.getReturnedParams().keySet().iterator(); i.hasNext();){
    			paramName = (String)i.next();
    			paramVal = (String)dataPack.getReturnedParams().get(paramName);
    			if(paramVal!=null){
        			returnedURL = returnedURL+"&amp;"+paramName+"="+paramVal;
    			}
    		}
    	}
        return returnedURL;
    }

    private String getConfigurationURL(PrepareConfiguratorRedirectDataBaseContract baseContract, RedirectConfiguratorDataBasePack dataPack)throws QuoteException {
    	String pid = baseContract.getPid();
    	
        if(CustomerConstants.CONFIGURATOR_FLAG_TRUE.equals(baseContract.getOverrideFlag()) || CustomerConstants.CONFIGURATOR_FINAL_BASIC.equals(baseContract.getConfigFinal())){
        	if(CustomerConstants.CONFIGURATOR_INDICATOR_PILOT.equals(baseContract.getConfigIndicator())){
        		dataPack.setOverridePilotFlag(CustomerConstants.CONFIGURATOR_FLAG_TRUE);
        	}
        	if(CustomerConstants.CONFIGURATOR_INDICATOR_RSTRCT.equals(baseContract.getConfigIndicator())){
        		dataPack.setOverrideRstrctFlag(CustomerConstants.CONFIGURATOR_FLAG_TRUE);
        	}
        	dataPack.setConfigurationURL(HtmlUtil.getURLForAction(CustomerActionKeys.CONFIG_HOSTED_SERVICE));
        	dataPack.setConfiguratorType(CustomerParamKeys.SQO);
        }else{
        	dataPack.setConfigurationURL(HtmlUtil.getURLForCPQRedirect());
        	dataPack.setConfiguratorType(CustomerParamKeys.CPQ);
        }
        
        return dataPack.getConfigurationURL();
    }
    
    
    
    public List<ConfiguratorPart> getValidBillingOptions(String chrgAgrmtNum, String configurationId, String pid, String webQuoteNum, RedirectConfiguratorDataBasePack dataPack)throws QuoteException{
    	List<ActiveService> activeServices =dataPack.getExistingPartsInCA();
    	List<ConfiguratorPart> pidAllParts = this.findConfiguratorPartsByWebQuoteNumPID(pid, webQuoteNum);
    	this.caculateBillingOptions(activeServices, pidAllParts);
    	return pidAllParts;
    }
    

    public List<ActiveService> retrieveActiveServicesAlreadyOnCA(String chrgAgrmtNum, String configurationId)throws QuoteException {
    	if(chrgAgrmtNum == null || chrgAgrmtNum.trim().length() == 0 || configurationId == null || configurationId.trim().length() == 0){
    		return null;
    	}

		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		List<ActiveService> activeServices = quoteProcess.retrieveLineItemsFromOrder(chrgAgrmtNum, configurationId);
    	activeServices = this.serviceFilter(activeServices, true);
    	return activeServices;
    }
    
    public List<ConfiguratorPart> findConfiguratorPartsByWebQuoteNumPID(String pid, String webQuoteNum)throws QuoteException{

		try {

			List<ConfiguratorPart> pidAllParts = SaasConfiguratorDaoFactory.singleton().create()
					.findPartsByWebQuoteNumPID(webQuoteNum, pid);
			
			return pidAllParts;
			
		} catch (TopazException te) {
			throw new QuoteException(te);
		}
    }
    

	protected List<ConfiguratorPart> caculateBillingOptions(List<ActiveService> activeServices, List<ConfiguratorPart> pidAllParts){
		
		if(activeServices==null || activeServices.size()==0){
			return pidAllParts;
		}
		
		String maxEndDateStr = null;
		Date maxEndDate = null;
		Date tmpEndDate = null;
		Set<String> validBillingOptionsInSubscriptionPart = new HashSet<String>(); // billing frequencies belong to active service which end date match the max end date of CA
		Set<String> validBillingOptionsInSetupPart = new HashSet<String>(); 
		Map<String,String> activeServicesbillingMap = new HashMap<String, String>();
		
		for(ActiveService as:activeServices){ // get max end date in active services
			if(as.isRampupPart()){
				continue;
			}
			if(as.getBillingFrequency()==null || "".equals(as.getBillingFrequency().trim())
					){
				continue;
			}
			activeServicesbillingMap.put(as.getPartNumber(), as.getBillingFrequency());
			
			if(as.isSetupFlag()){
				validBillingOptionsInSetupPart.add(as.getBillingFrequency());
			}
			if(as.getEndDate()==null || "".equals(as.getEndDate().trim())){
				continue;
			}
			if(as.isSbscrptnFlag()){

				if(maxEndDateStr == null){
					maxEndDateStr = as.getEndDate();
					maxEndDate = DateUtil.parseDate(maxEndDateStr);
				}else{
					tmpEndDate = DateUtil.parseDate(as.getEndDate());
					if(tmpEndDate.after(maxEndDate)){
						maxEndDateStr = as.getEndDate();
						maxEndDate = tmpEndDate;
					}
				}
				
			}
		}
		
		if(maxEndDateStr!=null){ // get billing options set from active services which end date  match max end date
			for(ActiveService as:activeServices){
				if(as.isRampupPart()){
					continue;
				}
				if(!as.isSbscrptnFlag()){
					continue;
				}
				if(as.getBillingFrequency()==null || "".equals(as.getBillingFrequency().trim())
						||as.getEndDate()==null || "".equals(as.getEndDate().trim()) ){
					continue;
				}
				if(maxEndDateStr.equals(as.getEndDate())){
					validBillingOptionsInSubscriptionPart.add(as.getBillingFrequency());
				}
			}
		}
		
		String billingFrequency = null;
		List<BillingOption>  availableBillingOptions = null;
		List<BillingOption>  finalBillingOptions = null;
		for(ConfiguratorPart cp: pidAllParts){
			if(cp.getAvailableBillingOptions()==null || cp.getAvailableBillingOptions().size()==0){// if part hasn't billing options, continue 
				continue;
			}

			if(cp.isRampUp()){
				continue;
			}
			
            if (!existInActiveService(cp, activeServices)) {
                continue;
            }

			billingFrequency = activeServicesbillingMap.get(cp.getPartNum());
			
			if(cp.isSetUp()){
				finalBillingOptions = new LinkedList<BillingOption>();
				availableBillingOptions = cp.getAvailableBillingOptions();
				boolean hasUpfront = false;
				for(BillingOption bo: availableBillingOptions){
					if(validBillingOptionsInSetupPart.contains(bo.getCode())){
						finalBillingOptions.add(bo);
						if("U".equals(bo.getCode())){
							hasUpfront = true;
						}
					}
				}
				if(!hasUpfront){
					BillingOption b = new BillingOption_jdbc("U","U",0);					
					finalBillingOptions.add(b);
				}
				
				cp.setAvailableBillingOptions(finalBillingOptions);
				
				
			}
			
			if(cp.isSubscrptn()){
				if(billingFrequency != null){ // if part exists on CA
					finalBillingOptions = new LinkedList<BillingOption>();
					availableBillingOptions = cp.getAvailableBillingOptions();
					for(BillingOption bo: availableBillingOptions){
						if(bo.getCode().equals(billingFrequency)){
							finalBillingOptions.add(bo);
							break;
						}
					}
				}else{// if part doesn't exists on CA
					finalBillingOptions = new LinkedList<BillingOption>();
					availableBillingOptions = cp.getAvailableBillingOptions();
					for(BillingOption bo: availableBillingOptions){
						if(validBillingOptionsInSubscriptionPart.contains(bo.getCode())){
							finalBillingOptions.add(bo);
						}
					}
				}
				if(finalBillingOptions!=null && finalBillingOptions.size()>0){
					cp.setAvailableBillingOptions(finalBillingOptions);
				}				
			}
			
		}
		
		return pidAllParts;
		
	}
	
    /**
     * DOC Comment method "existInActiveService".
     * 
     * @param cp
     * @param activeServices
     * @return
     */
    private boolean existInActiveService(ConfiguratorPart cp, List<ActiveService> activeServices) {
        for (Iterator<ActiveService> iterator = activeServices.iterator(); iterator.hasNext();) {
            ActiveService activeService = (ActiveService) iterator.next();
            if (activeService.getPartNumber().equals(cp.getPartNum())) {
                return true;
            }
        }
        return false;
    }

    public static List<ActiveService> serviceFilter(List<ActiveService> services, boolean isActive) {
		if(services == null || services.size() == 0){
			return services;
		}
		List<ActiveService> returnList = new LinkedList<ActiveService>();
		for(ActiveService as: services){
			if(!(as.isActiveFlag()^isActive)){
				returnList.add(as);
			}
		}
		return returnList;
	}

    
    public void putRedirectParam(RedirectConfiguratorDataBasePack dataPack,String paramName, String value){
    	dataPack.getRedirectParams().put(paramName, value);
    }

	public String getConfigurationURL(RedirectConfiguratorDataBasePack dataPack) {
		return dataPack.getConfigurationURL();
	}

	public void setConfigurationURL(RedirectConfiguratorDataBasePack dataPack, String configurationURL) {
		dataPack.setConfigurationURL(configurationURL);
	}

	public HashMap getRedirectParams(RedirectConfiguratorDataBasePack dataPack) {
		return dataPack.getRedirectParams();
	}

	public HashMap getReturnedParams(RedirectConfiguratorDataBasePack dataPack) {
		return dataPack.getReturnedParams();
	}
    
}
