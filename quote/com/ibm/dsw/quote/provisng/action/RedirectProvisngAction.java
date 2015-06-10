package com.ibm.dsw.quote.provisng.action;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.contract.RedirectProvisngContract;
import com.ibm.dsw.quote.provisng.process.RedirectProvisngProcess;
import com.ibm.dsw.quote.provisng.process.RedirectProvisngProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

public class RedirectProvisngAction extends BaseContractActionHandler {

	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		RedirectProvisngContract  ct = (RedirectProvisngContract) contract;
		
		String provisingId = "";
		
        try{
	        TransactionContextManager.singleton().begin();
		        RedirectProvisngProcess redirectProvisngProcess = RedirectProvisngProcessFactory.singleton().create();
		        provisingId = redirectProvisngProcess.updateProvisngId(ct.getWebQuoteNum(), ct.getProvisngIdForBrand(),ct.getSaasBrandCode());
			TransactionContextManager.singleton().commit();
        }catch(TopazException e){
        	logContext.error(this, "failed to update the provisioning id");
        }
        finally{
        	try {
				TransactionContextManager.singleton().rollback();
			} catch (TopazException e) {
				// TODO Auto-generated catch block
				logContext.error(this, e.getMessage());
			}
        }
        String redirectURL = generateRedirectURL(ct, provisingId);
        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, ct.getForwardFlag());
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL, redirectURL);
        handler.setState(StateKeys.STATE_REDIRECT_ACTION);
		return handler.getResultBean();
	}
	
	private String generateRedirectURL(RedirectProvisngContract contract, String provisingId) {
		
		 String connector = "&";
         
         StringBuffer sb = new StringBuffer("");
         sb.append(QuoteCommonUtil.getProvisAppEnvUrl()+"?");
         sb.append("brand_code").append("=").append(contract.getSaasBrandCode()).append(connector)
             .append("provisioning_id").append("=").append(provisingId).append(connector)
             .append("return_url").append("=").append(HtmlUtil.urlEncode(contract.getReturnUrl()));
         
     
         return sb.toString();
	}

}
