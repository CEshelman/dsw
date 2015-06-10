package com.ibm.dsw.quote.ps.contract;

import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.base.config.DBConstants;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.draftquote.util.StringFilter;
import com.ibm.dsw.quote.ps.config.PartSearchActionKeys;
import com.ibm.dsw.quote.ps.config.PartSearchMessageKeys;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>SaveSelectedPartsContract.java</code> class
 *
 *
 * @author: chenzhh@cn.ibm.com
 *
 * Creation date: Jan 26, 2007
 */
public class SaveSelectedPartsContract extends QuoteBaseContract {

    String country;
    String currency;
    String lob;
    String audience;
    String dataRetrievalType;
    String searchString;

    String searchStringKey;
    transient List selectedParts = new ArrayList();

    String quoteNum;
    String seqNum;
    String replacementFlag;
    String progMigrationCode;
    String quoteFlag;
    String isAddNewMonthlySWFlag;
    private String configrtnId;
    private String configrtnActionCode;
    private String chrgAgrmtNum;
    private String orgConfigId;

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return country;
    }
    /**
     * @param country The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }
    /**
     * @return Returns the currency.
     */
    public String getCurrency() {
        return currency;
    }
    /**
     * @param currency The currency to set.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    /**
     * @return Returns the dataRetrievalType.
     */
    public String getDataRetrievalType() {
        return dataRetrievalType;
    }
    /**
     * @param dataRetrievalType The dataRetrievalType to set.
     */
    public void setDataRetrievalType(String dataRetrievalType) {
        this.dataRetrievalType = dataRetrievalType;
    }
    /**
     * @return Returns the lob.
     */
    public String getLob() {
        return lob;
    }
    /**
     * @param lob The lob to set.
     */
    public void setLob(String lob) {
        this.lob = lob;
    }
    /**
     * @return Returns the audience.
     */
    public String getAudience() {
        return audience;
    }
    /**
     * @param audience The audience to set.
     */
    public void setAudience(String audience) {
        this.audience = audience;
    }
    public void setSelectedParts(String[] parts) {
        for(int i = 0;i < parts.length; i++) {
            selectedParts.add(parts[i]);
        }
    }
    /**
     * @return Returns the testSelectedParts.
     */
    public List getSelectedParts() {
        return selectedParts;
    }
    public String getRedirectAction(String retrievalType, String exccedCode ) {
        String msgKey=PartSearchMessageKeys.ADD_SUCCESS;
        if (String.valueOf(DBConstants.DB2_SP_ALREADY_IS_MAX).equals(exccedCode)){
            msgKey=PartSearchMessageKeys.ALREADY_HAS_MAX;
        }else if (String.valueOf(DBConstants.DB2_SP_EXCEED_MAX).equals(exccedCode)){
            msgKey=PartSearchMessageKeys.EXCEED_MAX;
        }
        String msgInfo = HtmlUtil.getTranMessageParam(I18NBundleNames.PART_SEARCH_BASE,
                msgKey, true, null);

        if (PartSearchMessageKeys.ADD_SUCCESS.equals(msgKey) || PartSearchMessageKeys.ALREADY_HAS_MAX
        		.equals(msgKey)){
        	quoteFlag = ParamKeys.PARAM_QUOTE_FLAG_SOFTWARE;
        }

        if(retrievalType.equals("browse")) {
            return PartSearchActionKeys.DISPLAY_PARTSEARCH_BROWSE_RESULT
            		+ "&" + ParamKeys.PARAM_COUNTRY + "=" + country
            		+ "&" + ParamKeys.PARAM_CURRENCY + "=" + currency
            		+ "&" + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + lob
            		+ "&" + ParamKeys.PARAM_AUDIENCE + "=" + audience
            		+ "&" + PartSearchParamKeys.PARAM_ADD_SUCCESS + "=true"
            		+ "&" + PartSearchParamKeys.PARAM_EXCCED_CODE + "=" + exccedCode
            		+ "&" + ParamKeys.PARAM_TRAN_MSG + "=" + msgInfo
            		+ "&" + ParamKeys.PARAM_QUOTE_NUM + "=" + quoteNum
            		+ "&" + ParamKeys.PARAM_PROG_MIGRATION_CODE + "=" + StringFilter.urlEncode(progMigrationCode)
            		+ "&" + ParamKeys.PARAM_QUOTE_FLAG + "=" + StringFilter.filter(quoteFlag)
            		+ "&" + ParamKeys.PARAM_ADD_NEW_MONTHLY_SW + "=" + StringFilter.filter(isAddNewMonthlySWFlag)
            		+ "&" + ParamKeys.PARAM_CHRG_AGRMT_NUM + "=" + StringFilter.filter(chrgAgrmtNum)
            		+ "&" + ParamKeys.PARAM_CONFIGRTN_ID + "=" + StringFilter.filter(configrtnId)
            		+ "&" + ParamKeys.PARAM_CONFIGRTN_ACTION_CODE + "=" + StringFilter.filter(configrtnActionCode)
            		+ "&" + ParamKeys.PARAM_ORG_CONFIG_ID + "=" + StringFilter.filter(orgConfigId);
        } else {
            if(retrievalType.equals(PartSearchParamKeys.SEARCH_PARTS_BY_NUMBERS) || retrievalType.equals(PartSearchParamKeys.GET_REPLACEMENT_PARTS) || retrievalType.equals(PartSearchParamKeys.GET_RELATED_PARTS ) || retrievalType.equals(PartSearchParamKeys.GET_RELATED_PARTS_LIC )) {
                searchStringKey = PartSearchParamKeys.PARAM_PART_NUMBERS;
            } else {
                searchStringKey = PartSearchParamKeys.PARAM_PART_DESCRIPTION;
            }
            return PartSearchActionKeys.DISPLAY_PARTSEARCH_FIND_RESULT
            		+ "&" + ParamKeys.PARAM_COUNTRY + "=" + country
            		+ "&" + ParamKeys.PARAM_CURRENCY + "=" + currency
            		+ "&" + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + lob
            		+ "&" + ParamKeys.PARAM_AUDIENCE + "=" + audience
            		+ "&" + searchStringKey + "=" + searchString
            		+ "&" + PartSearchParamKeys.PARAM_ADD_SUCCESS + "=true"
            		+ "&" + PartSearchParamKeys.PARAM_EXCCED_CODE + "=" + exccedCode
            		+ "&" + ParamKeys.PARAM_TRAN_MSG + "=" + msgInfo
            		+ "&" + ParamKeys.PARAM_QUOTE_NUM + "=" + quoteNum
            		+ "&" + ParamKeys.PARAM_PROG_MIGRATION_CODE + "=" + StringFilter.urlEncode(progMigrationCode)
            		+ "&" + ParamKeys.PARAM_QUOTE_FLAG + "=" + StringFilter.filter(quoteFlag)
            		+ "&" + ParamKeys.PARAM_ADD_NEW_MONTHLY_SW + "=" + StringFilter.filter(isAddNewMonthlySWFlag)
            		+ "&" + ParamKeys.PARAM_CHRG_AGRMT_NUM + "=" + StringFilter.filter(chrgAgrmtNum)
            		+ "&" + ParamKeys.PARAM_CONFIGRTN_ID + "=" + StringFilter.filter(configrtnId)
            		+ "&" + ParamKeys.PARAM_CONFIGRTN_ACTION_CODE + "=" + StringFilter.filter(configrtnActionCode)
            		+ "&" + ParamKeys.PARAM_ORG_CONFIG_ID + "=" + StringFilter.filter(orgConfigId);
        }
    }
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }

    /**
     * @return Returns the quoteNum.
     */
    public String getQuoteNum() {
        return quoteNum;
    }
    /**
     * @param quoteNum The quoteNum to set.
     */
    public void setQuoteNum(String quoteNum) {
        this.quoteNum = quoteNum;
    }

    /**
     * @return Returns the seqNum.
     */
    public int getSeqNum() {
        return Integer.parseInt(seqNum);
    }
    /**
     * @param seqNum The seqNum to set.
     */
    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }
    /**
     * @return Returns the replacementFlag.
     */
    public boolean isReplacementFlag() {
        return replacementFlag != null && "true".equalsIgnoreCase(replacementFlag);
    }
    /**
     * @param replacementFlag The replacementFlag to set.
     */
    public void setReplacementFlag(String replacementFlag) {
        this.replacementFlag = replacementFlag;
    }



    public String getProgMigrationCode() {
		return progMigrationCode;
	}
	public void setProgMigrationCode(String progMigrationCode) {
		this.progMigrationCode = progMigrationCode;
	}


	public String getQuoteFlag() {
		return quoteFlag;
	}
	public void setQuoteFlag(String quoteFlag) {
		this.quoteFlag = quoteFlag;
	}
	
	
	public String getIsAddNewMonthlySWFlag() {
		return isAddNewMonthlySWFlag;
	}
	public void setIsAddNewMonthlySWFlag(String isAddNewMonthlySWFlag) {
		this.isAddNewMonthlySWFlag = isAddNewMonthlySWFlag;
	}
	
	public String getConfigrtnId() {
		return configrtnId;
	}
	public void setConfigrtnId(String configrtnId) {
		this.configrtnId = configrtnId;
	}
	public String getConfigrtnActionCode() {
		return configrtnActionCode;
	}
	public void setConfigrtnActionCode(String configrtnActionCode) {
		this.configrtnActionCode = configrtnActionCode;
	}
	public String getChrgAgrmtNum() {
		return chrgAgrmtNum;
	}
	public void setChrgAgrmtNum(String chrgAgrmtNum) {
		this.chrgAgrmtNum = chrgAgrmtNum;
	}	
	
	public String getOrgConfigId() {
		return orgConfigId;
	}
	public void setOrgConfigId(String orgConfigId) {
		this.orgConfigId = orgConfigId;
	}
	public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        setSelectedParts(parameters.getParameterWithMultiValues(PartSearchParamKeys.PARAM_PART_CHECKBOX_NAME));
        this.isAddNewMonthlySWFlag = parameters.getParameterAsString(PartSearchParamKeys.PARAM_ADD_NEW_MONTHLY_SW) == null ? "false" : parameters.getParameterAsString(PartSearchParamKeys.PARAM_ADD_NEW_MONTHLY_SW); 
        this.chrgAgrmtNum=parameters.getParameterAsString(ParamKeys.PARAM_CHRG_AGRMT_NUM) == null ? "" : parameters.getParameterAsString(ParamKeys.PARAM_CHRG_AGRMT_NUM);
        this.configrtnActionCode=parameters.getParameterAsString(ParamKeys.PARAM_CONFIGRTN_ACTION_CODE) == null ? "" : parameters.getParameterAsString(ParamKeys.PARAM_CONFIGRTN_ACTION_CODE);
        this.configrtnId=parameters.getParameterAsString(ParamKeys.PARAM_CONFIGRTN_ID) == null ? "" : parameters.getParameterAsString(ParamKeys.PARAM_CONFIGRTN_ID);
        this.orgConfigId=parameters.getParameterAsString(ParamKeys.PARAM_ORG_CONFIG_ID) == null ? "" : parameters.getParameterAsString(ParamKeys.PARAM_ORG_CONFIG_ID);
	}

}
