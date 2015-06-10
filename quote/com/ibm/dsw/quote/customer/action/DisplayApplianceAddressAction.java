package com.ibm.dsw.quote.customer.action;

import java.util.Map;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.customer.config.CustomerStateKeys;
import com.ibm.dsw.quote.customer.contract.ApplianceAddressContract;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Display customer address page
 * 
 */
public class DisplayApplianceAddressAction extends BaseContractActionHandler {

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract, com.ibm.ead4j.jade.bean.ResultHandler)
	 */
	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		
		ApplianceAddressContract applianceAddressContract = (ApplianceAddressContract) contract;
		
		String webQuoteNum = applianceAddressContract.getQuoteNum()==null?"":applianceAddressContract.getQuoteNum();
		String addressType = applianceAddressContract.getAddressType()==null?DraftQuoteConstants.APPLIANCE_SHIPTO_ADDTYPE:applianceAddressContract.getAddressType(); 
		String lob = applianceAddressContract.getLob()==null?"":applianceAddressContract.getLob(); 
		String custNum = applianceAddressContract.getCustNum()==null?"":applianceAddressContract.getCustNum(); 
		boolean readOnly = applianceAddressContract.getReadOnly();
		boolean isSubmittedQuote = false;
		String bpSiteNum = "";
		if(applianceAddressContract.isPGSEnv()){
			bpSiteNum = applianceAddressContract.getQuoteUserSession().getSiteNumber();
		}
		
		logContext.debug(this, "display ship-to/install-at address,   webQuoteNum="+webQuoteNum+"  ,addressType="+addressType+"  ,bpSiteNum="+bpSiteNum);
        try{
			CustomerProcess custProcess = CustomerProcessFactory.singleton().create();
	        //
			Map resultMap = custProcess.searchApplianceAddress(webQuoteNum, addressType,lob,custNum,bpSiteNum);
			handler.addObject(DraftQuoteConstants.APPLIANCE_ADDRESS_OPTION, resultMap.get(DraftQuoteConstants.APPLIANCE_ADDRESS_OPTION));
			handler.addObject(ParamKeys.PARAM_SIMPLE_OBJECT, resultMap);
			handler.addObject(ParamKeys.PARAM_QUOTE_NUM, webQuoteNum);
			handler.addObject(ParamKeys.PARAM_COUNTRY, applianceAddressContract.getCountry());
			handler.addObject(ParamKeys.PARAM_LINE_OF_BUSINESS, applianceAddressContract.getLob());
			handler.addObject(ParamKeys.PARAM_ADDRESS_TYPE, addressType);
			handler.addObject(ParamKeys.PARAM_AGREEMENT_NUM, applianceAddressContract.getAgreementNumber());
			handler.addObject(ParamKeys.PARAM_IS_READ_ONLY, readOnly?"true":"false");
			if(applianceAddressContract.getIsSubmittedQuote()!=null){
				String submittedQuote = applianceAddressContract.getIsSubmittedQuote();
				handler.addObject(ParamKeys.PARAM_IS_SBMT_QT, submittedQuote);
				isSubmittedQuote = submittedQuote.equals("true");
			}
			
        } catch (TopazException e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException("error executing topaz process", e);
        } 
        handler.setState(getState(readOnly, isSubmittedQuote));
        return handler.getResultBean();
	}
	
	protected String getState(boolean readOnly, boolean isSubmittedQuote) {
		String stateStr = CustomerStateKeys.STATE_DISPLAY_APPLIANCE_ADDRESS;
		if(readOnly)
			stateStr = CustomerStateKeys.STATE_DISPLAY_APPLIANCE_ADDRESS_RO;
		else{
			if(isSubmittedQuote)
				stateStr = CustomerStateKeys.STATE_DISPLAY_SUBMITTEDQT_APPLIANCE_ADDRESS;
		}
		return stateStr;	
    }
}
