package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author George
 */
public class ToUAmendmentContract extends QuoteBaseContract {
	
	private String lob;
	private boolean isOnlySaaSParts;
//	private boolean isChannelQuote;
	private boolean isSubmittedQuote;
	private boolean isSpecialBid;
	
	private AttachmentsContract aContract = new AttachmentsContract();
	
	private static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    private int saasTermCondCatFlag;
    private String agrmtType;
    private String currentContractNum;
    private String touURLs = null;
    private String termsTypes = null;
    private String termsSubTypes = null;
    private String radioFlags = null;
    private String noFileTous =null;
    private String yesFlags;
    private String audCode;
    public void load(Parameters parameters, JadeSession session) {
        try {
        	aContract.load(parameters, session);
   		 	lob = parameters.getParameterAsString("lob");
//   		isChannelQuote = parameters.getParameterAsBoolean("isChannelQuote");
   			isSubmittedQuote = parameters.getParameterAsBoolean("isSubmittedQuote");  		 	
   		 	isOnlySaaSParts = parameters.getParameterAsBoolean("isOnlySaaSParts");
//   		saasTermCondCatFlag = parameters.getParameterAsInt(ParamKeys.SAAS_TERM_COND_CAT_FLAG);
            agrmtType = parameters.getParameterAsString(ParamKeys.PARAM_AGREEMENT_TYPE);
            currentContractNum = parameters.getParameterAsString("choosedContractNum");
            audCode = (String) session.getAttribute("AUD_CODE");
          
        } catch (Exception e) {
        	logContext.error(this, e);
        }
        setAmendmentTous(parameters);
    }
    
    public void setAmendmentTous(Parameters parameters){
    	String[] touURL = parameters.getParameterWithMultiValues(DraftQuoteParamKeys.PARAM_TOU_URL);
        String[] termsType = parameters.getParameterWithMultiValues(DraftQuoteParamKeys.TERMS_TYPE);
        String[] termsSubType = parameters.getParameterWithMultiValues(DraftQuoteParamKeys.TERMS_SUBTYPE);
        
        StringBuilder touURLSb = new StringBuilder();
        StringBuilder termsTypeSb = new StringBuilder();
        StringBuilder termsSubTypeSb = new StringBuilder();
        StringBuilder radioFlagsSb = new StringBuilder();
        StringBuilder noFileTouSb = new StringBuilder();
        StringBuilder yesFlagsSb = new StringBuilder();
        
        for (int i = 0; i <= 19; i++) {
            String key = "rdoYesNo_" + (i+1);//DraftQuoteParamKeys.PARAM_JUSTIFICATION_DOCUMENT + (i+1);
            if (parameters.hasParameter(key)) {
            	String radioFlag = parameters.getParameterAsString(key);
            		
            	if(null != touURL && touURL.length > 0){
            		touURLSb.append(",").append(touURL[i]);
            		noFileTouSb.append(",").append(touURL[i]);
        			yesFlagsSb.append(",").append(radioFlag);
            	}
            	if(null != termsType && termsType.length > 0){
            		termsTypeSb.append(",").append(termsType[i]);
            	}
            	if(null != termsSubType && termsSubType.length > 0){
            		termsSubTypeSb.append(",").append(termsSubType[i]);
            	}
            	radioFlagsSb.append(",").append(radioFlag);
            }
        }
        
        if(touURLSb.length()>0){
        	touURLSb.deleteCharAt(0);
        }
        
        if(termsTypeSb.length()>0){
        	termsTypeSb.deleteCharAt(0);
        }
        
        if(termsSubTypeSb.length()>0){
        	termsSubTypeSb.deleteCharAt(0);
        }
        
        if(radioFlagsSb.length()>0){
        	radioFlagsSb.deleteCharAt(0);
        }
        
        if(noFileTouSb.length()>0){
        	noFileTouSb.deleteCharAt(0);
        }
        
        if(yesFlagsSb.length()>0){
        	yesFlagsSb.deleteCharAt(0);
        }
        
        touURLs = touURLSb.toString();
        termsTypes = termsTypeSb.toString();
        termsSubTypes = termsSubTypeSb.toString();
        radioFlags = radioFlagsSb.toString();
        noFileTous =noFileTouSb.toString();
        yesFlags =yesFlagsSb.toString();
    }
    
	public int getSaasTermCondCatFlag() {
		return saasTermCondCatFlag;
	}

	public void setSaasTermCondCatFlag(int saasTermCondCatFlag) {
		this.saasTermCondCatFlag = saasTermCondCatFlag;
	}

	public String getAgrmtType() {
		return agrmtType;
	}

	public void setAgrmtType(String agrmtType) {
		this.agrmtType = agrmtType;
	}

	public String getCurrentContractNum() {
		return currentContractNum;
	}

	public void setCurrentContractNum(String currentContractNum) {
		this.currentContractNum = currentContractNum;
	}

	public AttachmentsContract getaContract() {
		return aContract;
	}

	public void setaContract(AttachmentsContract aContract) {
		this.aContract = aContract;
	}

	public String getTouURLs() {
		return touURLs;
	}

	public void setTouURLs(String touURLs) {
		this.touURLs = touURLs;
	}

	public String getTermsTypes() {
		return termsTypes;
	}

	public void setTermsTypes(String termsTypes) {
		this.termsTypes = termsTypes;
	}

	public String getTermsSubTypes() {
		return termsSubTypes;
	}

	public void setTermsSubTypes(String termsSubTypes) {
		this.termsSubTypes = termsSubTypes;
	}

	public String getLob() {
		return lob;
	}

	public void setLob(String lob) {
		this.lob = lob;
	}

	public boolean isOnlySaaSParts() {
		return isOnlySaaSParts;
	}

	public void setOnlySaaSParts(boolean isOnlySaaSParts) {
		this.isOnlySaaSParts = isOnlySaaSParts;
	}

//	public boolean isChannelQuote() {
//		return isChannelQuote;
//	}
//
//	public void setChannelQuote(boolean isChannelQuote) {
//		this.isChannelQuote = isChannelQuote;
//	}

	public boolean isSubmittedQuote() {
		return isSubmittedQuote;
	}

	public void setSubmittedQuote(boolean isSubmittedQuote) {
		this.isSubmittedQuote = isSubmittedQuote;
	}

	public boolean isSpecialBid() {
		return isSpecialBid;
	}

	public void setSpecialBid(boolean isSpecialBid) {
		this.isSpecialBid = isSpecialBid;
	}

	public String getRadioFlags() {
		return radioFlags;
	}

	public void setRadioFlags(String radioFlags) {
		this.radioFlags = radioFlags;
	}

	public String getNoFileTous() {
		return noFileTous;
	}

	public void setNoFileTous(String noFileTous) {
		this.noFileTous = noFileTous;
	}

	public String getYesFlags() {
		return yesFlags;
	}

	public void setYesFlags(String yesFlags) {
		this.yesFlags = yesFlags;
	}

	public void setAudCode(String audCode) {
		this.audCode = audCode;
	}

	public String getAudCode() {
		return audCode;
	}
	
}