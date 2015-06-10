package com.ibm.dsw.quote.draftquote.action;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteViewKeys;
import com.ibm.dsw.quote.draftquote.contract.RemoveConfigurationContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;


/**
 * RemoveConfigurationAction.java
 *
 * <p>
 * Copyright 2011 by IBM Corporation All rights reserved.
 * </p>
 * 
 * @author <a href="wxiaoli@cn.ibm.com">Vivian</a> <br/>
 * Jul 4, 2011
 */
public class RemoveConfigurationAction extends PostPartPriceTabAction {

    @Override
	protected void innerPostPartPriceTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
    	RemoveConfigurationContract removeConfigurationContract = (RemoveConfigurationContract) contract;
    	String webQuoteNum = removeConfigurationContract.getWebQuoteNum();
        String configurationId = removeConfigurationContract.getConfigurationId();
        Quote quote = removeConfigurationContract.getQuote();
        String removeChargeAgreementNum = null;

        PartPriceProcess process;
        try {
            process = PartPriceProcessFactory.singleton().create();
        } catch (QuoteException e) {
            logContext.fatal(this, e.getMessage());
            throw new QuoteException(e);
        }
        
        Map configrtnsMap = quote.getPartsPricingConfigrtnsMap();
        List configrtnsList = quote.getPartsPricingConfigrtnsList();
		List monthlySwConfgrtnsList = quote.getMonthlySwQuoteDomain().getMonthlySwConfgrtns();
        PartsPricingConfiguration confgrtn = null;
        for (Iterator iterator = configrtnsList.iterator(); iterator.hasNext();) {
			PartsPricingConfiguration config = (PartsPricingConfiguration) iterator.next();
			if(webQuoteNum != null && webQuoteNum.equals(config.getWebQuoteNum())
					&& ((configurationId == null && config.getConfigrtnId() == null) || configurationId != null && configurationId.equals(config.getConfigrtnId()))) {
				confgrtn = config;
				iterator.remove();
				try {
					config.delete();
				} catch (TopazException e) {
					logContext.fatal(this, e.getMessage());
		            throw new QuoteException(e);
				}
				break;
			}
		}
		boolean deleteCANum = true;
		boolean executeMonthlySwConfgrtnsListFor = true;
		for (Iterator iterator = configrtnsList.iterator(); iterator.hasNext();) {
			PartsPricingConfiguration config = (PartsPricingConfiguration) iterator.next();
			if (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(config.getConfigrtnActionCode())
					|| PartPriceConstants.ConfigrtnActionCode.NEW_CA_CT.equals(config.getConfigrtnActionCode())) {
				deleteCANum = false;
				executeMonthlySwConfgrtnsListFor = false;
				break;
			}
		}
		if (executeMonthlySwConfgrtnsListFor) {
			for (Iterator iterator = monthlySwConfgrtnsList.iterator(); iterator.hasNext();) {
				MonthlySoftwareConfiguration monthlySwConfig = (MonthlySoftwareConfiguration) iterator.next();
				if (PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(monthlySwConfig.getConfigrtnActionCode())) {
					deleteCANum = false;
					break;
				}
			}
		}
        if(deleteCANum){
            removeChargeAgreementNum = quote.getQuoteHeader().getRefDocNum();
        	quote.getQuoteHeader().setRefDocNum(null);
        	removeConfigurationContract.setChargeAgreementNum(null);
        }
        process.deleteRenwlModel(webQuoteNum, configurationId, removeChargeAgreementNum);

        List masterSaaSList = (List) configrtnsMap.get(confgrtn);
        configrtnsMap.remove(confgrtn);
        for (Iterator iterator = masterSaaSList.iterator(); iterator.hasNext();) {
			QuoteLineItem lineItem = (QuoteLineItem) iterator.next();
			
			deleteLineItems(quote, lineItem.getPartNum(), lineItem.getSeqNum());
			for (Iterator iterator2 = lineItem.getRampUpLineItems().iterator(); iterator2
					.hasNext();) {
				QuoteLineItem rampUpQli = (QuoteLineItem) iterator2.next();
				deleteLineItems(quote, rampUpQli.getPartNum(), rampUpQli.getSeqNum());
			}
		}
        
        
        process.postPartPriceInfo(removeConfigurationContract);
    }
    
    public void deleteLineItems(Quote quote, String partNum, int partSeqNum) throws QuoteException{
    	if(quote.getLineItemList() == null || quote.getLineItemList().size() == 0){
    		return;
    	}
    	QuoteLineItem lineItem = quote.getLineItem(partNum, partSeqNum);
        try {
            //if lineItem exists, then delete it from db
            if(lineItem != null){ 
    	    	
    	    	removeLineItemFromList(quote.getLineItemList(), lineItem.getPartNum(), lineItem.getSeqNum());
    	    	removeLineItemFromList(quote.getSaaSLineItems(), lineItem.getPartNum(), lineItem.getSeqNum());
    	    	removeLineItemFromList(quote.getMasterSaaSLineItems(), lineItem.getPartNum(), lineItem.getSeqNum());
    	    	lineItem.delete();
            }
        } catch(TopazException te){
        	throw new QuoteException(te);
        }
    }
    
    private void removeLineItemFromList(List list, String partNum, int partSeqNum){
    	if(list == null || list.size() == 0){
    		return ;
    	}
    	for(Iterator it = list.iterator(); it.hasNext(); ){
    		QuoteLineItem item = (QuoteLineItem)it.next();
    		
    		if(item.getPartNum().equals(partNum) && (item.getSeqNum() == partSeqNum)){
    			it.remove();
    		}
    	}
    }

    @Override
	protected String getValidationForm() {
        return DraftQuoteViewKeys.REMOVE_CONFIGURATION_FORM;
    }

}
