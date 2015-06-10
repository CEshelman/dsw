package com.ibm.dsw.quote.draftquote.action;

import java.util.Iterator;
import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteViewKeys;
import com.ibm.dsw.quote.draftquote.contract.DeleteLineItemContract;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;


/**
 * DeleteLineItemAction.java
 *
 * <p>
 * Copyright 2011 by IBM Corporation All rights reserved.
 * </p>
 * 
 * @author <a href="wxiaoli@cn.ibm.com">Vivian</a> <br/>
 * Apr 12, 2011
 */
public class DeleteLineItemAction extends PostPartPriceTabAction {

    protected void innerPostPartPriceTab(ProcessContract contract, ResultHandler handler) throws QuoteException {
    	DeleteLineItemContract deleteLineItemContract = (DeleteLineItemContract) contract;
    	String webQuoteNum = deleteLineItemContract.getWebQuoteNum();
        String partSeqNum = deleteLineItemContract.getPartSeqNum();
        String partNum = deleteLineItemContract.getPartNum();
        Quote quote = deleteLineItemContract.getQuote();

        PartPriceProcess process;
        try {
            process = PartPriceProcessFactory.singleton().create();
        } catch (QuoteException e) {
            logContext.fatal(this, e.getMessage());
            throw new QuoteException(e);
        }
        
        deleteLineItems(quote, webQuoteNum, partNum, partSeqNum);
        
        process.postPartPriceInfo(deleteLineItemContract);
        
        // when delete appliance line item, call sp IU_QT_LINE_ITEM_ADDRESS to update ship-to/install-at address
        try{
            CustomerProcess custProcess = null;
			try {
				custProcess = CustomerProcessFactory.singleton().create();
			} catch (TopazException e) {
				logContext.fatal(this, e.getMessage());
	            throw new QuoteException(e);
			}
			custProcess.updateLineItemAddr(webQuoteNum, deleteLineItemContract.getUserId()); 
        } catch (QuoteException e) {
            logContext.fatal(this, e.getMessage());
            throw new QuoteException(e);
        }
    }
    
    public void deleteLineItems(Quote quote, String webQuoteNum, String partNum, String partSeqNum) throws QuoteException{
    	if(quote.getLineItemList() == null || quote.getLineItemList().size() == 0){
    		return;
    	}
    	QuoteLineItem lineItem = quote.getLineItem(partNum, Integer.parseInt(partSeqNum));
        try {
            //if lineItem exists, then delete it from db
            if(lineItem != null){ 
    	        //delete the additional line items
    	    	if(lineItem.getAddtnlMaintCvrageQty() > 0){
    	    		List subLineItems = lineItem.getAddtnlYearCvrageLineItems();
    	    		if(subLineItems != null && subLineItems.size() > 0){
    	    			for (int i = 0; i < subLineItems.size(); i++) {
    	    				QuoteLineItem subLineItem = (QuoteLineItem) subLineItems.get(i);
    	    				
    	    				removeLineItemFromList(quote.getLineItemList(), subLineItem.getPartNum(), subLineItem.getSeqNum());
    	    				removeLineItemFromList(quote.getSaaSLineItems(), subLineItem.getPartNum(), subLineItem.getSeqNum());
    	    				removeLineItemFromList(quote.getMasterSoftwareLineItems(), subLineItem.getPartNum(), subLineItem.getSeqNum());
	    	    			subLineItem.delete();
    					}
    	    		}
    	    	}
    	    	
    	    	removeLineItemFromList(quote.getLineItemList(), lineItem.getPartNum(), lineItem.getSeqNum());
    	    	removeLineItemFromList(quote.getSaaSLineItems(), lineItem.getPartNum(), lineItem.getSeqNum());
    	    	removeLineItemFromList(quote.getMasterSoftwareLineItems(), lineItem.getPartNum(), lineItem.getSeqNum());
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

    protected String getValidationForm() {
        return DraftQuoteViewKeys.DELETE_LINE_ITEM_FORM;
    }

}
