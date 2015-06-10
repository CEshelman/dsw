package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteOmittedLineItemVO;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * 
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>QuoteOmittedLineItemViewBean</code> class.
 * 
 * @author liguotao@cn.ibm.com
 * 
 *         Created on 2013-6-6
 */
public  class QuoteOmittedLineItemViewBean extends BaseViewBean {
	protected String custName = "";
	protected String custCity = "";
	protected String custState = "";
    protected String custStateDesc = "";
    protected String custCntry = "";
    protected String custCntryCode = "";
    protected String custCurrencyCode = "";
	protected Quote quote = null;
	protected String webQuoteNum = "";
	protected transient Customer cust = null;
	public QuoteOmittedLineItemVO quoteOmittedLineItemVO = null;
	public List quoteOmittedLineItemVOList = null;
	public String omittedAddOrUpdateUrl;
	protected String currencyCode = "";
	protected String returnToDraftQuoteUrl = "";
	protected String displayAction = "";
	private String jadeAction = "";
	private String redirectMsg = "";


	public void collectResults(Parameters params) throws ViewBeanException {
		super.collectResults(params);
		custName = params.getParameterAsString(ParamKeys.PARAM_CUST_NAME);
		custCity = params.getParameterAsString(ParamKeys.PARAM_COUNTRY);
		webQuoteNum = params.getParameterAsString(ParamKeys.PARAM_QUOTE_NUM);
		redirectMsg = params.getParameterAsString(ParamKeys.PARAM_REDIRECT_MSG);
		//currencyCode = params.getParameterAsString(ParamKeys.PARAM_QUOTE_CURRENCY_CODE);
		quoteOmittedLineItemVOList = (List)	params.getParameter(ParamKeys.OMITTED_LINEITEM_LIST);
		quote = (Quote) params.getParameter(ParamKeys.PARAM_QUOTE_OBJECT);
		cust = quote.getCustomer();
		currencyCode =  this.getCurrencyDesc(quote.getQuoteHeader().getPriceCountry(),quote.getQuoteHeader().getCurrencyCode());
		if(cust != null){
			 custName = cust.getCustName();
		        custCity = cust.getCity();
		        custState = cust.getSapRegionCode();
		        custCntryCode = cust.getCountryCode();
		        custCurrencyCode = cust.getCurrencyCode();
		        Country cntryObj = this.getCountry(custCntryCode);
		        if (cntryObj != null) {
		            custStateDesc = cntryObj.getStateDescription(custState);
		            custCntry = cntryObj.getDesc();
		        }
		        else {
			        custStateDesc = custState ;
			        custCntry = custCntryCode;
		        }
			
		}
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustCity() {
		return custCity;
	}

	public void setCustCity(String custCity) {
		this.custCity = custCity;
	}

	public Quote getQuote() {
		return quote;
	}

	public QuoteOmittedLineItemVO getQuoteOmittedLineItemVO() {
		return quoteOmittedLineItemVO;
	}

	public void setQuoteOmittedLineItemVO(
			QuoteOmittedLineItemVO quoteOmittedLineItemVO) {
		this.quoteOmittedLineItemVO = quoteOmittedLineItemVO;
	}

	public List getQuoteOmittedLineItemVOList() {
		return quoteOmittedLineItemVOList;
	}

	public void setQuoteOmittedLineItemVOList(
			List quoteOmittedLineItemVOList) {
		this.quoteOmittedLineItemVOList = quoteOmittedLineItemVOList;
	}
	
	
	
	public String getOmittedAddOrUpdateUrl() {
		omittedAddOrUpdateUrl = HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_OMITTED_LINEITEM);
		return omittedAddOrUpdateUrl;
	}
	
	 public String getI18NString(String baseName, String key){
	        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();

	        String value = appCtx.getI18nValueAsString(baseName, locale, key);

	        if (value == null || value.equals("")) {
	            value = key;
	        }

	        return value;
	    }

	public String getWebQuoteNum() {
		return webQuoteNum;
	}

	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	protected String getCurrencyDesc(Country cntry, String currencyCode) {
        String currencyDesc = null;
        if (cntry != null) {
            List currencyList = cntry.getCurrencyList();
            if (currencyList != null && currencyList.size() > 0) {
                for (int i = 0; i < currencyList.size(); i++) {
                    CodeDescObj obj = (CodeDescObj) currencyList.get(i);
                    if (obj != null) {
                        String objKey = obj.getCode();
                        if (objKey != null && objKey.equalsIgnoreCase(currencyCode))
                            currencyDesc = obj.getCodeDesc();
                    }
                }
            }
        }
        return StringUtils.isNotBlank(currencyDesc) ? currencyDesc : currencyCode;
    }

	public String getCustState() {
		return custState;
	}

	public void setCustState(String custState) {
		this.custState = custState;
	}

	public String getCustStateDesc() {
		return custStateDesc;
	}

	public void setCustStateDesc(String custStateDesc) {
		this.custStateDesc = custStateDesc;
	}

	public String getCustCntry() {
		return custCntry;
	}

	public void setCustCntry(String custCntry) {
		this.custCntry = custCntry;
	}

	public String getCustCntryCode() {
		return custCntryCode;
	}

	public void setCustCntryCode(String custCntryCode) {
		this.custCntryCode = custCntryCode;
	}

	public String getCustCurrencyCode() {
		return custCurrencyCode;
	}

	public void setCustCurrencyCode(String custCurrencyCode) {
		this.custCurrencyCode = custCurrencyCode;
	}
	
	
	public String getRedirectMsg() {
		return redirectMsg;
	}

	public void setRedirectMsg(String redirectMsg) {
		this.redirectMsg = redirectMsg;
	}

	protected Country getCountry(String cntryCode) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        Country cntry = null;
        try {
            CacheProcess cProcess = CacheProcessFactory.singleton().create();
            cntry = cProcess.getCountryByCode3(cntryCode);
        } catch (QuoteException e) {
            logContext.error(this, "Failed to get country by "+cntryCode);
        }
        return cntry;
    }

	public String getReturnToDraftQuoteUrl() {
		 String urlPattern = ApplicationProperties.getInstance().getQuoteBaseURL();
		 		returnToDraftQuoteUrl = urlPattern + "?" + ParamKeys.PARAM_RPT_JADE_ACTION + "="+DraftQuoteActionKeys.DISPLAY_PARTS_PRICE_TAB;
		
		return returnToDraftQuoteUrl;
		
	}

	public String getReturnSubmittedQuoteURL() {
		String urlPattern = ApplicationProperties.getInstance().getQuoteBaseURL();
		if(redirectMsg != null && "execSummaryTab".equals(redirectMsg)){
			 String actionURL = urlPattern + "?" + ParamKeys.PARAM_RPT_JADE_ACTION + "="+ SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_EXEC_SUMMARY_TAB;
		     String params = "quoteNum="+quote.getQuoteHeader().getWebQuoteNum();
		     return actionURL+"&amp;"+params;
		}else{
	        String actionURL = urlPattern + "?" + ParamKeys.PARAM_RPT_JADE_ACTION + "="+ SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_PART_PRICE_TAB;
	        String params = "quoteNum="+quote.getQuoteHeader().getWebQuoteNum();
	        return actionURL+"&amp;"+params;
		}
      
    }
	

}
