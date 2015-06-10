package com.ibm.dsw.quote.draftquote.contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LocaleHelperImpl;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.CognosPart;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;



public class RetrievePartFromCognosContract extends QuoteBaseContract {
    Locale locale;
    LogContext logContext = LogContextFactory.singleton().getLogContext();
    private QuoteHeader quoteHeader;
    private List<CognosPart> validCognosParts = new ArrayList<CognosPart>();
    private List<CognosPart> invalidCognosParts = new ArrayList<CognosPart>();
    private List<CognosPart> exceedMaxCognosParts = new ArrayList<CognosPart>();
    //the params of Cognos callback
    private String cognosSoldToCustNum;
    private String cognosContractNum;
    private String cognosLobCode;
    //if call Cogonos Webservice successful
    private boolean isCallCognosWebserviceSuccessful = true;
    
    //private String cognosUrl = "http://cogtest1.austin.ibm.com:9083/software/pmxpt/servlet/Gateway/rds/pagedReportData/path/Public%20Folders/Software%20Sales/DSW%20Entitlement%20Reports/Reports/Reinstatement__Quote?fmt=DataSet&p_Sold%20to%20cust=0007240515&p_SAP_CTRCT_NUM=0000044045&p_LINE_OF_BUS_CODE=PA";


    public Locale getLocale() {
        return this.locale;
    }

    /**
     * @param parameters
     * @param session
     * @see com.ibm.ead4j.jade.contract.ProcessContract#load(com.ibm.ead4j.jade.util.Parameters,
     *      com.ibm.ead4j.jade.session.JadeSession)
     */
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);

        locale = (Locale) session.getAttribute(FrameworkKeys.JADE_LOCALE_KEY);
        if (locale == null) {
            locale = LocaleHelperImpl.getDefaultDSWLocale();
        }
        
        String tempParam = "";
        tempParam = parameters.getParameterAsString(PartPriceConstants.CognosCallBackParams.SOLD_TO_CUST);
        if (StringUtils.isNotBlank(tempParam)) {
        	cognosSoldToCustNum = removeDuplicatedParam(tempParam);
        }
        tempParam = parameters.getParameterAsString(PartPriceConstants.CognosCallBackParams.SAP_CTRCT_NUM);
        if (StringUtils.isNotBlank(tempParam)) {
        	cognosContractNum = removeDuplicatedParam(tempParam);
        }
        tempParam = parameters.getParameterAsString(PartPriceConstants.CognosCallBackParams.LINE_OF_BUS_CODE);
        if (StringUtils.isNotBlank(tempParam)) {
        	cognosLobCode = removeDuplicatedParam(tempParam);
        }

    }

	public String getCognosRequestUrl() {
		StringBuffer cognosUrl = new StringBuffer();
		try{
		cognosUrl.append(HtmlUtil.getCognosWebserviceProtocol())
		.append("://")
		.append(HtmlUtil.getCognosWebserviceHost())
		.append(":")
		.append(HtmlUtil.getCognosWebservicePort())
		.append(HtmlUtil.getCognosWebserviceUrl())
		.append("?")
		.append(ParamKeys.PARAM_REDIRECT_CONGOS_FORMAT)
		.append("=")
		.append(ParamKeys.PARAM_REDIRECT_CONGOS_FORMAT_DATASET)
		.append("&")
		.append(ParamKeys.PARAM_REDIRECT_CONGOS_CUSNUM_JSP)
		.append("=")
		.append(this.quoteHeader.getSoldToCustNum())
		.append("&")
		.append(ParamKeys.PARAM_REDIRECT_CONGOS_AGREENUM)
		.append("=")
		.append(this.quoteHeader.getContractNum())
		.append("&")
		.append(ParamKeys.PARAM_REDIRECT_CONGOS_LOB)
		.append("=")
					.append(QuoteCommonUtil.transLobForCognos(this.quoteHeader.getLob().getCode(),
							this.quoteHeader.getAgrmtTypeCode()));
		}catch(Exception e){
			logContext.error(this, "Get retrieve Cognos webservice URL error: " + e.getMessage());
		}
		
		return cognosUrl.toString();
	}

	public QuoteHeader getQuoteHeader() {
		return quoteHeader;
	}

	public void setQuoteHeader(QuoteHeader quoteHeader) {
		this.quoteHeader = quoteHeader;
	}

	public List<CognosPart> getValidCognosParts() {
		return validCognosParts;
	}

	public void setValidCognosParts(List<CognosPart> validCognosParts) {
		this.validCognosParts = validCognosParts;
	}

	public List<CognosPart> getInvalidCognosParts() {
		return invalidCognosParts;
	}

	public void setInvalidCognosParts(List<CognosPart> invalidCognosParts) {
		this.invalidCognosParts = invalidCognosParts;
	}

	public List<CognosPart> getExceedMaxCognosParts() {
		return exceedMaxCognosParts;
	}

	public void setExceedMaxCognosParts(List<CognosPart> exceedMaxCognosParts) {
		this.exceedMaxCognosParts = exceedMaxCognosParts;
	}

	public String getCognosSoldToCustNum() {
		return StringUtils.trimToEmpty(cognosSoldToCustNum);
	}

	public String getCognosContractNum() {
		return StringUtils.trimToEmpty(cognosContractNum);
	}

	public String getCognosLobCode() {
		return StringUtils.trimToEmpty(cognosLobCode);
	}

	public boolean isCallCognosWebserviceSuccessful() {
		return isCallCognosWebserviceSuccessful;
	}

	public void setCallCognosWebserviceSuccessful(
			boolean isCallCognosWebserviceSuccessful) {
		this.isCallCognosWebserviceSuccessful = isCallCognosWebserviceSuccessful;
	}
	
	private String removeDuplicatedParam(String originalParam){
		String returnStr = originalParam;
		try{
			if(returnStr.contains(",")){
				return (returnStr.substring(0,returnStr.indexOf(",")));
			}
		}catch(Exception e){
			logContext.error(this, e.getMessage());
		}
		return returnStr;
	}
}
