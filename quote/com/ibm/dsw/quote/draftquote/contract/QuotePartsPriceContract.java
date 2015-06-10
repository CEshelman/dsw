package com.ibm.dsw.quote.draftquote.contract;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.LocaleHelperImpl;
import com.ibm.dsw.quote.pvu.config.VUParamKeys;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

;

/**
 * <p>
 * Copyright 2006 by IBM Corporation All rights reserved.
 * </p>
 * 
 * <p>
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * </p>
 * 
 * @author <a href="mailto:mgivney@us.ibm.com">Matt Givney </a> <br/>
 *  
 */
public class QuotePartsPriceContract extends DraftQuoteBaseContract {
    Locale locale;

    private String mandatoryKey = null;

    private String displayDetailKey = null;
    
    private String partLimitExceedCode = null;
    
    private String bidItrtnPost = null;
    
    //add for redirect configuration params
    private String editIbmProdId;
    private String editConfigrtnId;
    private String editOrgConfigrtnId;
    private String editConfigrtrConfigrtnId;
    private String editTradeFlag;
    private String editConfigurationFlag;
    private String overrideFlag;
    private Double overallYtyGrowth;

    public Double getOverallYtyGrowth() {
		return overallYtyGrowth;
	}

	/**
     * @return Returns the displayDetailKey.
     */
    public String getDisplayDetailKey() {
        return displayDetailKey;
    }

    /**
     * @param displayDetailKey
     *            The displayDetailKey to set.
     */
    public void setDisplayDetailKey(String displayDetailKey) {
        this.displayDetailKey = displayDetailKey;
    }

    /**
     * @return Returns the mandatoryKey.
     */
    public String getMandatoryKey() {
        return mandatoryKey;
    }

    /**
     * @param mandatoryKey
     *            The mandatoryKey to set.
     */
    public void setMandatoryKey(String mandatoryKey) {
        this.mandatoryKey = mandatoryKey;
    }

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
    	LogContext logContext = LogContextFactory.singleton().getLogContext();
        super.load(parameters, session);

        locale = (Locale) session.getAttribute(FrameworkKeys.JADE_LOCALE_KEY);
        if (locale == null) {
            locale = LocaleHelperImpl.getDefaultDSWLocale();
        }
        this.mandatoryKey = parameters.getParameterAsString(VUParamKeys.MANDAROTY_FLAG);
        this.displayDetailKey = parameters.getParameterAsString(VUParamKeys.DISPLAY_DETAIL_FLAG);
        this.bidItrtnPost = parameters.getParameterAsString(VUParamKeys.BIDITRTN_POST);

        if(StringUtils.isNotBlank(parameters.getParameterAsString("editIbmProdId"))){
        	this.editIbmProdId = parameters.getParameterAsString("editIbmProdId");
        }
        if(StringUtils.isNotBlank(parameters.getParameterAsString("editConfigrtnId"))){
        	this.editConfigrtnId = parameters.getParameterAsString("editConfigrtnId");
        }
        if(StringUtils.isNotBlank(parameters.getParameterAsString("editOrgConfigrtnId"))){
        	this.editOrgConfigrtnId = parameters.getParameterAsString("editOrgConfigrtnId");
        }
        if(StringUtils.isNotBlank(parameters.getParameterAsString("editConfigrtrConfigrtnId"))){
        	this.editConfigrtrConfigrtnId = parameters.getParameterAsString("editConfigrtrConfigrtnId");
        }
        if(StringUtils.isNotBlank(parameters.getParameterAsString("editTradeFlag"))){
        	this.editTradeFlag = parameters.getParameterAsString("editTradeFlag");
        }
        if(StringUtils.isNotBlank(parameters.getParameterAsString("editConfigurationFlag"))){
        	this.editConfigurationFlag = parameters.getParameterAsString("editConfigurationFlag");
        }
        if(StringUtils.isNotBlank(parameters.getParameterAsString("overrideFlag"))){
        	this.overrideFlag = parameters.getParameterAsString("overrideFlag");
        }
        
        try{
        	overallYtyGrowth = Double.valueOf(parameters.getParameterAsString("overallYtyGrowth"));
        }catch(Exception ignore){
        	 logContext.error(this, "exception happend during load method: " + ignore.getMessage());
        }finally{
	        logContext.debug(this, "locale = " + locale);
	        logContext.debug(this, "mandatoryKey = " + mandatoryKey);
	        logContext.debug(this, "editIbmProdId = " + editIbmProdId);
	        logContext.debug(this, "editConfigrtnId = " + editConfigrtnId);
	        logContext.debug(this, "editConfigrtrConfigrtnId = " + editConfigrtrConfigrtnId);
	        logContext.debug(this, "editTradeFlag = " + editTradeFlag);
	        logContext.debug(this, "editConfigurationFlag = " + editConfigurationFlag);
	        logContext.debug(this, "overrideFlag = " + overrideFlag);
        }
    }
    /**
     * @return Returns the partLimitExceedCode.
     */
    public String getPartLimitExceedCode() {
        return partLimitExceedCode;
    }
    /**
     * @param partLimitExceedCode The partLimitExceedCode to set.
     */
    public void setPartLimitExceedCode(String partLimitExceedCode) {
        this.partLimitExceedCode = partLimitExceedCode;
    }
    public String getBidItrtnPost() {
        return bidItrtnPost;
    }
    public void setBidItrtnPost(String bidItrtnPost) {
        this.bidItrtnPost = bidItrtnPost;
    }
    
    public String getEditIbmProdId() {
		return editIbmProdId;
	}
	public String getEditConfigrtnId() {
		return editConfigrtnId;
	}
	public String getEditConfigrtrConfigrtnId() {
		return editConfigrtrConfigrtnId;
	}
	public String getEditTradeFlag() {
		return editTradeFlag;
	}
	public String getEditConfigurationFlag() {
		return editConfigurationFlag;
	}

	public String getOverrideFlag() {
		return overrideFlag;
	}

	public void setOverrideFlag(String overrideFlag) {
		this.overrideFlag = overrideFlag;
	}

	public String getEditOrgConfigrtnId() {
		return editOrgConfigrtnId;
	}

	public void setEditOrgConfigrtnId(String editOrgConfigrtnId) {
		this.editOrgConfigrtnId = editOrgConfigrtnId;
	}
}
