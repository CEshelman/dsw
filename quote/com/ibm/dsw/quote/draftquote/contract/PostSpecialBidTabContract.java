package com.ibm.dsw.quote.draftquote.contract;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo.SpecialBidQuestion;
import com.ibm.dsw.quote.draftquote.config.SpecialBidParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PostSpecialBidTabContract</code> class .
 * 
 * 
 * @author <a href="panyg@cn.ibm.com">Goshen Pan </a> <br/>
 * 
 * Creation date: Apr 23, 2007
 */
public class PostSpecialBidTabContract extends PostDraftQuoteBaseContract {
    
    private boolean crediAndRebill;
    
    private boolean initSpeclBidApprFlag;

    private boolean termsAndCondsChg;

    private boolean setCtrctLvlPricng;

    private boolean fulfllViaLanddMdl;

    private boolean elaTermsAndCondsChg;

    private boolean answer1;

    private boolean answer2;

    private boolean preApprvdCtrctLvlPric;

    private boolean ryltyDiscExcdd;

    private String salesDiscTypeCode;

    private String creditJustText;

    private String[] spBidCategories;

    private String spBidCustIndustryCode;

    private String spBidDist;

    private String spBidJustText;

    private String spBidRgn;

    private String spBidType;

    private String[] questions;

    private String[] answers;
    
    private String competitorName;
    
    private String competitorPrice;
    
    private String competitorProduct;
    
    private String competitorTC;
    
    private String channelOverrideDiscountReason;
    
    private String[] sectionJustTexts;
    
    private String[] sectionIndexs;
    
    private String[] lastModifyTimes;
    
    private String[] textIDs;
    
    private boolean isCompetitive;
    
    private int rateBuyDown = 0;
    
    private int SWGIncur = 0;
    
    private String financeRate = "";
    
    private String progRBD = "";
    
    private String incrRBD = "";
    
    private String orgnlSalesOrdNum;
    
    private String orgnlQuoteNum;
    
    private String salesPlayNum;
    
    private String evaloptionType;
    
    private String evalComment;
    
	private boolean splitBid;
	
	private boolean secTextError = false;
    
    /**
     *  
     */
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        questions = parameters.getParameterWithMultiValues(SpecialBidParamKeys.CONFIGURABLE_QUESTION);
        if (questions != null && questions.length > 0 ){
        	answers = new String[questions.length];
        	for (int i = 0; i < questions.length; ++i){
        		answers[i] = parameters.getParameterAsString(SpecialBidParamKeys.CONFIGURABLE_QUESTION_ANSWER + i);
        	}
        }
        crediAndRebill = parameters.getParameterAsBoolean(SpecialBidParamKeys.EXPLANATION_1);
        termsAndCondsChg = parameters.getParameterAsBoolean(SpecialBidParamKeys.EXPLANATION_2);
        setCtrctLvlPricng = parameters.getParameterAsBoolean(SpecialBidParamKeys.EXPLANATION_3);

        elaTermsAndCondsChg = parameters.getParameterAsBoolean(SpecialBidParamKeys.IS_THIS_BID_FOR_ELA);
        fulfllViaLanddMdl = parameters.getParameterAsBoolean(SpecialBidParamKeys.IS_TRANSACTION_FULFILLED);

        preApprvdCtrctLvlPric = parameters.getParameterAsBoolean(SpecialBidParamKeys.REFLECT_PREVIOUS);
        salesDiscTypeCode = parameters.getParameterAsString(SpecialBidParamKeys.REFLECT_FOLLOWING);
        ryltyDiscExcdd = parameters.getParameterAsBoolean(SpecialBidParamKeys.ROYALTY_FLOOR);

        spBidCategories = parameters.getParameterWithMultiValues(SpecialBidParamKeys.CATEGORY);
        spBidCustIndustryCode = parameters.getParameterAsString(SpecialBidParamKeys.INDUSTRY_SEGMENT);
        spBidType = parameters.getParameterAsString(SpecialBidParamKeys.SPECIAL_BID_TYPE);
        
        sectionJustTexts = parameters.getParameterWithMultiValues(SpecialBidParamKeys.SECTION_JUST_TEXTS);
        sectionIndexs = parameters.getParameterWithMultiValues(SpecialBidParamKeys.SECTION_INDEXS);
        lastModifyTimes = parameters.getParameterWithMultiValues(SpecialBidParamKeys.LAST_MODIFY_TIME);
        isCompetitive = parameters.getParameterAsBoolean(SpecialBidParamKeys.IS_COMPETITIVE);
        textIDs = parameters.getParameterWithMultiValues(SpecialBidParamKeys.TEXT_IDS);
        if(StringUtils.isNotEmpty(parameters.getParameterAsString(SpecialBidParamKeys.IS_RATE_BUY_DOWN)))
        	this.rateBuyDown = parameters.getParameterAsInt(SpecialBidParamKeys.IS_RATE_BUY_DOWN);
        if(StringUtils.isNotEmpty(parameters.getParameterAsString(SpecialBidParamKeys.IS_SWG_INCUR)))
        	this.SWGIncur = parameters.getParameterAsInt(SpecialBidParamKeys.IS_SWG_INCUR);
        if(StringUtils.isNotEmpty(parameters.getParameterAsString(SpecialBidParamKeys.FINANCE_RATE)))
        	this.financeRate = parameters.getParameterAsString(SpecialBidParamKeys.FINANCE_RATE);
        if(StringUtils.isNotEmpty(parameters.getParameterAsString(SpecialBidParamKeys.PROG_RBD)))
        	this.progRBD = parameters.getParameterAsString(SpecialBidParamKeys.PROG_RBD);
        if(StringUtils.isNotEmpty(parameters.getParameterAsString(SpecialBidParamKeys.INCR_RBD)))
        	this.incrRBD = parameters.getParameterAsString(SpecialBidParamKeys.INCR_RBD);
        this.orgnlSalesOrdNum = parameters.getParameterAsString(SpecialBidParamKeys.ORGNL_SALES_ORD_NUM);
        this.orgnlQuoteNum = parameters.getParameterAsString(SpecialBidParamKeys.ORGNL_QUOTE_NUM);
        this.salesPlayNum = parameters.getParameterAsString(SpecialBidParamKeys.SALES_PLAY_NUM);
        this.initSpeclBidApprFlag = parameters.getParameterAsBoolean(SpecialBidParamKeys.INIT_SPECL_BID_APPR_FLAG);
        initSecText(sectionJustTexts);
        
        this.evaloptionType = parameters.getParameterAsString(SpecialBidParamKeys.EVAL_SELECT_OPTION_FLAG);
        this.evalComment = parameters.getParameterAsString(SpecialBidParamKeys.EVAL_COMMENT);
        if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(this.evalComment)) == true){
        	this.evalComment = "";
        }
        this.splitBid = parameters.getParameterAsBoolean(SpecialBidParamKeys.SPLIT_BID);
        
        this.channelOverrideDiscountReason = parameters.getParameterAsString(SpecialBidParamKeys.CHNL_OVVRD_REAS);
    }
    
    private void initSecText(String[] arr)
    {
    	if ( arr != null )
        {
        	for ( int i = 0; i < arr.length; i++ ){
        		if(arr[i] != null && arr[i].startsWith("<![CDATA[") && !arr[i].endsWith("]]>")){
        			//throw new QuoteException();
        			secTextError = true;
        		}
	        	if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(arr[i])) == true){
	        		arr[i] = "";
	        	}else{
	        		arr[i] = StringHelper.removeCDATATag(arr[i]);
	        		arr[i] = com.ibm.dsw.quote.draftquote.util.StringFilter.filter(arr[i]);
	        	}
        	}
        }
    }
    
   
    
    public boolean isAnswer1() {
        return answer1;
    }

    public boolean isAnswer2() {
        return answer2;
    }

    public boolean isCrediAndRebill() {
        return crediAndRebill;
    }

    public String getCreditJustText() {
        return creditJustText;
    }

    public boolean isInitSpeclBidApprFlag() {
        return initSpeclBidApprFlag;
    }
    /**
     * @param creditJustText
     *            The creditJustText to set.
     */
    public void setCreditJustText(String creditJustText) {
    	if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(creditJustText)) == true){
    		this.creditJustText = "";
    	}else{
    		this.creditJustText = StringHelper.removeCDATATag(creditJustText);
    	}
    }

    public boolean isElaTermsAndCondsChg() {
        return elaTermsAndCondsChg;
    }

    public boolean isFulfllViaLanddMdl() {
        return fulfllViaLanddMdl;
    }

    public boolean isPreApprvdCtrctLvlPric() {
        return preApprvdCtrctLvlPric;
    }

    public boolean isRyltyDiscExcdd() {
        return ryltyDiscExcdd;
    }

    public String getSalesDiscTypeCode() {
        return salesDiscTypeCode;
    }

    /**
     * @param salesDiscTypeCode
     *            The salesDiscTypeCode to set.
     */
    public void setSalesDiscTypeCode(String salesDiscTypeCode) {
        this.salesDiscTypeCode = salesDiscTypeCode;
    }

    public boolean isSetCtrctLvlPricng() {
        return setCtrctLvlPricng;
    }

    public String getSpBidCustIndustryCode() {
        return spBidCustIndustryCode;
    }

    /**
     * @param spBidCustIndustryCode
     *            The spBidCustIndustryCode to set.
     */
    public void setSpBidCustIndustryCode(String spBidCustIndustryCode) {
        this.spBidCustIndustryCode = spBidCustIndustryCode;
    }

    public String getSpBidDist() {
        return spBidDist;
    }

    /**
     * @param spBidDist
     *            The spBidDist to set.
     */
    public void setSpBidDist(String spBidDist) {
        this.spBidDist = spBidDist;
    }

    public String getSpBidJustText() {
        return spBidJustText;
    }

    /**
     * @param spBidJustText
     *            The spBidJustText to set.
     */
    public void setSpBidJustText(String spBidJustText) {
    	if(StringHelper.isEmptyRichEditorContent(StringHelper.removeCDATATag(spBidJustText)) == true){
    		this.spBidJustText = "";
    	}else{	
    		this.spBidJustText = spBidJustText;
    	}
    }

    public String getSpBidRgn() {
        return spBidRgn;
    }

    /**
     * @param spBidRgn
     *            The spBidRgn to set.
     */
    public void setSpBidRgn(String spBidRgn) {
        this.spBidRgn = spBidRgn;
    }

    public String getSpBidType() {
        return spBidType;
    }

    /**
     * @param spBidType
     *            The spBidType to set.
     */
    public void setSpBidType(String spBidType) {
        this.spBidType = spBidType;
    }

    public boolean isTermsAndCondsChg() {
        return termsAndCondsChg;
    }

    /**
     * @return
     */
    public List getSpBidCategories() {
        if (spBidCategories == null) {
            return null;
        }
        List result = new ArrayList();
        for (int i = 0; i < spBidCategories.length; i++) {
            result.add(spBidCategories[i]);
        }
        return result;
    }

    public List getSpBidQuestions() {
        if (questions == null || answers == null) {
            return null;
        }
        List result = new ArrayList();
        for (int i = 0; i < questions.length; i++) {
            SpecialBidInfo.SpecialBidQuestion question = new SpecialBidQuestion(questions[i], answers[i]);
            result.add(question);
        }
        return result;
    }
    /**
     * @return Returns the isCompetitive.
     */
    public boolean isCompetitive() {
        return isCompetitive;
    }
    /**
     * @param isCompetitive The isCompetitive to set.
     */
    public void setCompetitive(boolean isCompetitive) {
        this.isCompetitive = isCompetitive;
    }
    
    /**
     * @return Returns the competitorName.
     */
    public String getCompetitorName() {
        return competitorName;
    }
    /**
     * @param competitorName The competitorName to set.
     */
    public void setCompetitorName(String competitorName) {
        this.competitorName = competitorName;
    }
    /**
     * @return Returns the competitorPrice.
     */
    public String getCompetitorPrice() {
        return competitorPrice;
    }
    /**
     * @param competitorPrice The competitorPrice to set.
     */
    public void setCompetitorPrice(String competitorPrice) {
        this.competitorPrice = competitorPrice;
    }
    /**
     * @return Returns the competitorProduct.
     */
    public String getCompetitorProduct() {
        return competitorProduct;
    }
    /**
     * @param competitorProduct The competitorProduct to set.
     */
    public void setCompetitorProduct(String competitorProduct) {
        this.competitorProduct = competitorProduct;
    }
    /**
     * @return Returns the competitorTC.
     */
    public String getCompetitorTC() {
        return competitorTC;
    }
    /**
     * @param competitorTC The competitorTC to set.
     */
    public void setCompetitorTC(String competitorTC) {
        this.competitorTC = competitorTC;
    }
    
    /**
     * @return Returns the lastModifyTimes.
     */
    public String[] getLastModifyTimes() {
        return lastModifyTimes;
    }
    /**
     * @return Returns the sectionIndexs.
     */
    public String[] getSectionIndexs() {
        return sectionIndexs;
    }
    /**
     * @return Returns the sectionJustTexts.
     */
    public String[] getSectionJustTexts() {
        return sectionJustTexts;
    }
    
    /**
     * @return Returns the textIDs.
     */
    public String[] getTextIDs() {
        return textIDs;
    }
    /**
     * @return Returns the financeRate.
     */
    public String getFinanceRate() {
        return financeRate;
    }
    /**
     * @return Returns the rateBuyDown.
     */
    public int getRateBuyDown() {
        return rateBuyDown;
    }
    /**
     * @return Returns the sWGIncur.
     */
    public int getSWGIncur() {
        return SWGIncur;
    }
    
    /**
     * @return Returns the progRBD.
     */
    public String getProgRBD() {
        return progRBD;
    }
    
    /**
     * @return Returns the incrRBD.
     */
    public String getIncrRBD() {
        return incrRBD;
    }
    
    public void setFinanceRate(String financeRate) {
        this.financeRate = financeRate;
    }
    
    public void setProgRBD(String progRBD) {
        this.progRBD = progRBD;
    }
    
    public void setIncrRBD(String incrRBD) {
        this.incrRBD = incrRBD;
    }
    /**
     * @return Returns the orgnlQuoteNum.
     */
    public String getOrgnlQuoteNum() {
        return orgnlQuoteNum;
    }
    /**
     * @return Returns the orgnlSalesOrdNum.
     */
    public String getOrgnlSalesOrdNum() {
        return orgnlSalesOrdNum;
    }
    /**
     * @return Returns the salesPlayNum.
     */
    public String getSalesPlayNum() {
        return salesPlayNum;
    }

	public String getEvaloptionType() {
		return evaloptionType;
	}

	public void setEvaloptionType(String evaloptionType) {
		this.evaloptionType = evaloptionType;
	}

	public String getEvalComment() {
		return evalComment;
	}

	public void setEvalComment(String evalComment) {
		this.evalComment = evalComment;
	}

    public boolean isSplitBid() {
		return splitBid;
	}

	public void setSplitBid(boolean splitBid) {
		this.splitBid = splitBid;
	}

	public boolean isSecTextError() {
		return secTextError;
	}

	public String getChannelOverrideDiscountReason() {
		return channelOverrideDiscountReason;
	}

	public void setChannelOverrideDiscountReason(
			String channelOverrideDiscountReason) {
		this.channelOverrideDiscountReason = channelOverrideDiscountReason;
	}
}
