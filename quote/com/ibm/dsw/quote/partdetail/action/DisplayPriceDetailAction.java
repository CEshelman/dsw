/*
 * Created on 2007-3-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.partdetail.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.partdetail.config.PartDetailsParamKeys;
import com.ibm.dsw.quote.partdetail.config.PartDetailsStateKeys;
import com.ibm.dsw.quote.partdetail.contract.PartDetailsContract;
import com.ibm.dsw.quote.partdetail.domain.PartPriceDetail;
import com.ibm.dsw.quote.partdetail.process.PartPriceDetailProcess;
import com.ibm.dsw.quote.partdetail.process.PartPriceDetailProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayPriceDetailAction extends BaseContractActionHandler {
    private static final LogContext logger = LogContextFactory.singleton().getLogContext();
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        PartDetailsContract pdContract  = (PartDetailsContract)contract;
        String partNumber = pdContract.getPartNumber();
        String displayType = pdContract.getDisplayType();
        String quoteNum = pdContract.getQuoteNum();
        String priceType = pdContract.getPriceType();
        boolean showEventBaseBiling = Boolean.parseBoolean(pdContract.getShowEventBaseBiling());
        
        if (quoteNum != null) {
            handler.addObject(ParamKeys.PARAM_QUOTE_NUM, quoteNum);
        } else {
            throw new QuoteException("Missing webQNumber.");
        }
        
        if(pdContract.getLob() != null){
            handler.addObject(ParamKeys.PARAM_LINE_OF_BUSINESS, pdContract.getLob());
        } else {
            throw new QuoteException("Missing lob.");
        }
        
        try {
            PartPriceDetailProcess process = PartPriceDetailProcessFactory.singleton().create();
            PartPriceDetail details = null;
            
            logger.debug(this, "Price Type is:"+priceType);
            
            details = process.getPartDetails(partNumber,quoteNum,priceType,false);
            
            if(details != null){
                handler.addObject(PartDetailsParamKeys.PART_PRICE_DETAILS_OBJECT,details);
	            
	            handler.addObject(PartDetailsParamKeys.RENEWAL, details.isRenewal() + "");
	            handler.addObject(PartDetailsParamKeys.SUBMITTED, details.isSubmitted() + "");
                handler.addObject(PartDetailsParamKeys.SHOW_EVENT_BASE_BILING, showEventBaseBiling + "");
            }
        } catch (TopazException e) {
            logger.error(this, e);
            throw new QuoteException(e);
        } catch(Exception e){
            logger.error(this, e);
            throw new QuoteException(e);
        }
        handler.setState(getState(contract));
        
        return handler.getResultBean();
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.base.action.SimpleContractAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return PartDetailsStateKeys.STATE_DISPLAY_PRICE_DETAILS;
    }
}
