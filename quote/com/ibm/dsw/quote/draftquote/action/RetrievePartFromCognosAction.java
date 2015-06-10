package com.ibm.dsw.quote.draftquote.action;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.CognosPart;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.contract.RetrievePartFromCognosContract;
import com.ibm.dsw.quote.draftquote.process.RetrievePartFromCognosProcess;
import com.ibm.dsw.quote.draftquote.process.RetrievePartFromCognosProcessFactory;
import com.ibm.ead4j.jade.bean.MessageBean;
import com.ibm.ead4j.jade.bean.MessageBeanFactory;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;


public class RetrievePartFromCognosAction extends BaseContractActionHandler {
	private static final LogContext logger = LogContextFactory.singleton().getLogContext();
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
	throws QuoteException, ResultBeanException {

		RetrievePartFromCognosContract ct = (RetrievePartFromCognosContract) contract;
		//Only allow SQO to retrieve parts from Cognos
		if(ct.isSQOEnv()){
			RetrievePartFromCognosProcess process;
			try {
				process = RetrievePartFromCognosProcessFactory.singleton().create();
			
				process.addPartsToQuote(ct);
			} catch (TopazException te) {
				throw new QuoteException(te);
			} catch (Exception e) {
				throw new QuoteException(e);
			}
			handleRetrievePartMessage(ct, handler, process);
		}
		handler.setState(StateKeys.STATE_REDIRECT_ACTION);
		
		return handler.getResultBean();
		}
	
	   /**
	 * @param ct
	 * @param handler
	 * handle the warning messages
	 */
	protected void handleRetrievePartMessage(RetrievePartFromCognosContract ct, ResultHandler handler, RetrievePartFromCognosProcess process) {
	        Locale locale = ct.getLocale();
	        MessageBean mBean = MessageBeanFactory.create();
	        List<CognosPart> invalidCognosParts = ct.getInvalidCognosParts();
	        if(invalidCognosParts != null && invalidCognosParts.size() > 0){
	        	String msg1 = this.getI18NString(DraftQuoteMessageKeys.COGNOS_WARNING_INVALID_PARTS,
                        MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, locale);
                String[] args1 = { getWarningMsg(invalidCognosParts) };
                String msgInfo = MessageFormat.format(msg1, args1);
                mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
	        }
	        List<CognosPart> exceedMaxCognosParts = ct.getExceedMaxCognosParts();
	        if(exceedMaxCognosParts != null && exceedMaxCognosParts.size() > 0){
	        	String msg2 = this.getI18NString(DraftQuoteMessageKeys.COGNOS_WARNING_EXCEED_PARTS,
                        MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, locale);
                String[] args2 = { getWarningMsg(exceedMaxCognosParts) };
                String msgInfo = MessageFormat.format(msg2, args2);
                mBean.addMessage(msgInfo, MessageBeanKeys.INFO);
	        }
	        if(!process.isValidCognosCallback(ct)){
	        	String msg3 = this.getI18NString(DraftQuoteMessageKeys.COGNOS_WARNING_PARAMS_NOT_MATCH,
                        MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, locale);
                mBean.addMessage(msg3, MessageBeanKeys.INFO);
	        }
	        if(process.isValidCognosCallback(ct)
	        	&& (ct.getValidCognosParts() == null
	        			|| ct.getValidCognosParts().size() == 0)
	        	&& (ct.getInvalidCognosParts() == null
	        			|| ct.getInvalidCognosParts().size() == 0)){
	        	String msg4 = this.getI18NString(DraftQuoteMessageKeys.COGNOS_WARNING_NO_PARTS_RETRIEVED,
                        MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, locale);
                mBean.addMessage(msg4, MessageBeanKeys.INFO);
	        }
	        if(!ct.isCallCognosWebserviceSuccessful()){
	        	String msg5 = this.getI18NString(DraftQuoteMessageKeys.COGNOS_WARNING_WEBSERVICE_ERROR,
                        MessageKeys.BUNDLE_APPL_I18N_PART_PRICE, locale);
                mBean.addMessage(msg5, MessageBeanKeys.INFO);
	        }
	        handler.setMessage(mBean);
	    }
	   
	   private String getWarningMsg(List<CognosPart> cognosParts){
		   	StringBuffer strMsg = new StringBuffer();
       		for (int i = 0; i < cognosParts.size(); i++) {
       		CognosPart cognosPart = (CognosPart) cognosParts.get(i);
       		strMsg.append("[");
				strMsg.append(cognosPart.getPartNum());
				if(i == cognosParts.size() - 1){
					strMsg.append("]");
				}else{
					strMsg.append("], ");
				}
			}
       		return strMsg.toString();
	   }
}
