/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>ApprovalGroupHelper.java</code> class is to define unified interface
 * for approval rule handing.
 * 
 * @author: <a href="mailto:junqingz@cn.ibm.com">Thomas Zhang </a>
 * 
 * Creation date: 2007-4-28
 */
package com.ibm.dsw.quote.draftquote.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CurrencyConversionFactorFactory;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.CustomerGroup;
import com.ibm.dsw.quote.common.domain.PartDisplayAttr;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItem.PartGroup;
import com.ibm.dsw.quote.common.domain.QuoteLineItem.SpBidPartGroup;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo.SpecialBidQuestion;
import com.ibm.dsw.quote.common.service.QuoteWSHandler;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.common.util.spbid.date.SalesContinuousCoverageChecker;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.util.date.PartTypeChecker;
import com.ibm.dsw.quote.rule.helper.Rule;
import com.ibm.dsw.quote.rule.helper.RuleHelper;
import com.ibm.dsw.quote.rule.helper.RuleHelperFactory;
import com.ibm.dsw.quote.rule.helper.RuleHelper_Impl;
import com.ibm.dsw.quote.rule.helper.Rule_Impl;
import com.ibm.dsw.spbid.common.ApprovalGroup;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

public class ApprovalGroupHelper implements RuleHelper {
	protected static final LogContext logger = LogContextFactory.singleton().getLogContext();
	//protected static final QuoteLogContextLog4JImpl wsLogger = (QuoteLogContextLog4JImpl)LogContextFactory.singleton().getLogContext();

	public List getListOfApprover(Quote quote) throws QuoteException{
		List approvers = null;
		logger.info(QuoteWSHandler.class," To getListOfApprover for quote#: "+quote.getQuoteHeader().getWebQuoteNum()+" from RuleEngine:" + quote.getQuoteHeader().getAudCode());
		
		printQuote(quote);
		String areaCode = quote.getQuoteHeader().getCountry().getSpecialBidAreaCode().trim();
		Rule rule = getRuleFromQuote(quote);
		RuleHelper_Impl helper = RuleHelperFactory.singleton().create(areaCode);
		try {
			List returnApprovers = helper.executeRules(areaCode,rule);
			approvers = filterDateExtendedGroup(quote, returnApprovers);
		} catch (QuoteException e) {
			logger.error(this, e, "Exception occurs when getListOfApprover: "+e.getMessage());
			throw e;
		}
		logger.debug(this,"End of getListOfApprover.");
		return approvers;
	};
	
	public List getListOfApprover(Quote quote, boolean isSQOEnv) throws QuoteException{
		List approvers = null;
		logger.info(QuoteWSHandler.class," To getListOfApprover for quote#: "+quote.getQuoteHeader().getWebQuoteNum()+" from RuleEngine:" + quote.getQuoteHeader().getAudCode());
		
		printQuote(quote);
		String areaCode = quote.getQuoteHeader().getCountry().getSpecialBidAreaCode().trim();
		Rule rule = getRuleFromQuote(quote, isSQOEnv);
		RuleHelper_Impl helper = RuleHelperFactory.singleton().create(areaCode);
		try {
			List returnApprovers = helper.executeRules(areaCode,rule);
			approvers = filterDateExtendedGroup(quote, returnApprovers);
		} catch (QuoteException e) {
			logger.error(this, e, "Exception occurs when getListOfApprover: "+e.getMessage());
			throw e;
		}
		logger.debug(this,"End of getListOfApprover.");
		return approvers;
	};
	
	public Rule getRuleFromQuote(Quote quote)
	{
		if ( quote.getQuoteHeader().isPGSQuote() )
		{
//			return getRuleFromPGSQuote(quote);
			//ebiz SCHW-8ZFRV4, set items not visiable for pgs quote to default value no 
			return getRuleFromSQOQuote(quote);
		}
		else
		{
			return getRuleFromSQOQuote(quote);
		}
	}
	
	public Rule getRuleFromQuote(Quote quote, boolean isSQOEnv)
	{
		Rule rule = getRuleFromSQOQuote(quote);
		
		//check GOE review when submission
		if (isSQOEnv && QuoteCommonUtil.isNeedGOEReview(quote))
		{
			rule.setNeedGoeReview(Boolean.TRUE.toString());
		}
		else
		{
			rule.setNeedGoeReview(Boolean.FALSE.toString());
		}
			
		return rule;
	}
	
	protected Rule getRuleFromPGSQuote(Quote quote)
	{
		Rule rule = new Rule_Impl();
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		SpecialBidInfo sbInfo = quote.getSpecialBidInfo();
		List lineItems = quote.getLineItemList();
		//set factors directly from quoteHeader
		rule.setAcquisitions(getList(quoteHeader.getAcqrtnCode()));

		rule.setQuoteTypes(getList(quoteHeader.getLob().getCode()));
//		if(quoteHeader.isMigration()){
//			rule.setQuoteTypes(getList(QuoteConstants.LOB_FCT));
//		}
		//Should set the ismigration condition no matter the quote is migrated or not. 2008/8/26 -joe
		rule.setIsMigration(String.valueOf(quoteHeader.isMigration()));
		if(sbInfo.getSpBidRgn()!=null && !sbInfo.getSpBidRgn().equalsIgnoreCase("0"))
			rule.setSbRegion(sbInfo.getSpBidRgn()); 
		if(sbInfo.getSpBidDist()!=null && !sbInfo.getSpBidDist().equalsIgnoreCase("0"))
			rule.setSbDistrict(sbInfo.getSpBidDist()); 
//		rule.setSbTypes(getList(sbInfo.getSpBidType()));
		rule.setBizOrgs(getList(quoteHeader.getBusOrgCode()));
		rule.setFfmtSource(quoteHeader.getFulfillmentSrc());
		rule.setCustSegs(getList(sbInfo.getSpBidCustIndustryCode()));
		rule.setCountry(quoteHeader.getCountry().getCode3());
		//set factors from line items - begin
		List rvnStrmsShared = new ArrayList();
		List rvnStrmsDscntd = new ArrayList();
		List discountList =  new ArrayList();
		List forceSbGroups =  new ArrayList();
		List dscntdSbGroups =  new ArrayList();
		List allPartGroups = new ArrayList();
		List brandsDscnted = new ArrayList();
		List ctrctVars = new ArrayList();
		List trgtMktCodeList = new ArrayList();
		List ctrldPortfolios = new ArrayList();
        List custSetDes  = new ArrayList();
        
		rule.setHasUnpublishedParts(Boolean.FALSE.toString());
		rule.setIsExtendedAllowedYears(Boolean.FALSE.toString());
		rule.setIsDateOutsideRules(Boolean.FALSE.toString());
//		rule.setIsTermChangeInvolved(Boolean.FALSE.toString());
//		rule.setIsCtrctLvlPricing(Boolean.FALSE.toString());
//		rule.setIsRflctPrevPricing(Boolean.FALSE.toString());
//		rule.setIsForELA(Boolean.FALSE.toString());
//		rule.setIsLandedModel(Boolean.FALSE.toString());
//		rule.setIsRoyaltyDscntd(Boolean.FALSE.toString());
//		rule.setIsDueToSalesPlay(Boolean.FALSE.toString());
		rule.setHasUnauthorizedProd(Boolean.FALSE.toString());
		rule.setIsBackDated(Boolean.FALSE.toString());
		rule.setIsBidCntsChnlDscnt(Boolean.FALSE.toString());
		rule.setRewlQuoteReOpened(Boolean.FALSE.toString());
		rule.setEolPartsContained(Boolean.FALSE.toString());
//		rule.setRateBuyDown(Boolean.FALSE.toString());
//		rule.setCompressedCover(Boolean.FALSE.toString());
//		rule.setInitSpeclBidApprFlag(Boolean.FALSE.toString());
		rule.setGovEntityIndCode(Boolean.FALSE.toString());
//		rule.setAgreementType(null);
//		rule.setOEMASLType(null);
//		rule.setBidIterationFlag(Boolean.FALSE.toString());
		
		boolean isStartDateOverriden = false;
		boolean isEndDateOverriden = false;
		boolean isDateOutsideRules = false;
		
		//to detemine the part type
		//(is it a maintenance part? is it a related maintenance part? is it an unrelated maintenance part?)
		PartTypeChecker checker = new PartTypeChecker(quote);
        checker.checkType();
        
        //get reseller to check if is autorized to sell controlled products
        
        //Add for channel margin overriden discount and total discount
        Double chnlOvrrdDiscPct=new Double(0);
        Double defaultBPDist = new Double(0);
        
        Double totDiscPct=new Double(0);
        
        if(quoteHeader.getBackDatingFlag())
        	rule.setIsBackDated(Boolean.TRUE.toString());
        
        if(quoteHeader.getRenwlQuoteSpeclBidFlag() == 1)
            rule.setRewlQuoteReOpened(Boolean.TRUE.toString());
        
        if(quoteHeader.getHasObsoletePartsFlag())
            rule.setEolPartsContained(Boolean.TRUE.toString());
		
//        if ( quoteHeader.isOEMQuote() )
//        {
//        	rule.setAgreementType(quoteHeader.getOrdgMethodCode());
//        	rule.setOEMASLType(String.valueOf(quoteHeader.getOemBidType()));
//        }
        
		//controlled product
        if (!sbInfo.isResellerAuthorizedToPortfolio())
        	rule.setHasUnauthorizedProd(Boolean.TRUE.toString());

        for(int i=0;i<lineItems.size();i++){
			QuoteLineItem qli = (QuoteLineItem)lineItems.get(i);
			
			if ( !rvnStrmsShared.contains(qli.getRevnStrmCode()) )
			{
			    rvnStrmsShared.add(qli.getRevnStrmCode());
			}
			
			List partGroups = qli.getPartGroups();
			if ( partGroups != null )
			{
				for ( int j = 0; j < partGroups.size(); j++ )
				{
					PartGroup partGroup = (PartGroup)partGroups.get(j);
					if ( !allPartGroups.contains(partGroup.getGroupName()) )
					{
						allPartGroups.add(partGroup.getGroupName());
					}
				}
			}
			List spBidPartGroups=qli.getSpBidPartGroups();
			//If a part is discounted or not
			if(qli.getLineDiscPct() != 0 || (qli.getOverrideUnitPrc()!=null && Double.parseDouble(String.valueOf(qli.getOverrideUnitPrc())) > 0)
					|| (qli.getChnlOvrrdDiscPct() != null && qli.getChnlOvrrdDiscPct().doubleValue() > 0) 
					){
				if(!rvnStrmsDscntd.contains(qli.getRevnStrmCode()))
				    rvnStrmsDscntd.add(qli.getRevnStrmCode());
				if(partGroups != null && partGroups.size()>0){
				    for(int j=0; j<partGroups.size(); j++){
				        PartGroup partGroup = (PartGroup)partGroups.get(j);
	                    if(!spBidPartGroups.contains(partGroup.getGroupName()) && !dscntdSbGroups.contains(partGroup.getGroupName()))
				        dscntdSbGroups.add(partGroup.getGroupName());
				    }
				}
				discountList.add(new Double(qli.getLineDiscPct()));
				if(StringUtils.isNotBlank(qli.getSwProdBrandCode())){
				    if(!brandsDscnted.contains(qli.getSwProdBrandCode()))
				        brandsDscnted.add(qli.getSwProdBrandCode());
				}
				if(StringUtils.isNotBlank(qli.getProdTrgtMktCode())){
				    if(!trgtMktCodeList.contains(qli.getProdTrgtMktCode()))
				        trgtMktCodeList.add(qli.getProdTrgtMktCode());
				}
			}
			if(qli.isPartRestrct())
				rule.setHasUnpublishedParts(Boolean.TRUE.toString());
			
			//controlled product
			if(qli.isControlled()){
			    if(!ctrldPortfolios.contains(qli.getControlledCode()))
			        ctrldPortfolios.add(qli.getControlledCode());
			}
			
			// To determine the value of isExtendedAllowedYears
			PartDisplayAttr attr = qli.getPartDispAttr();
            if(attr.isMaintBehavior()){
                if(attr.isRelatedMaint() && (qli.getAddtnlMaintCvrageQty()>1)){
                    rule.setIsExtendedAllowedYears(Boolean.TRUE.toString());
                }
                else if(attr.isUnRelatedMaint() && (qli.getAddtnlMaintCvrageQty()>2)){
                    rule.setIsExtendedAllowedYears(Boolean.TRUE.toString());
                }
            }			
			// To determine the value of isDateOutsideRules - begin
			//For renewal quote
			if(quoteHeader.isRenewalQuote() && !isDateOutsideRules){
			    int months = DateUtil.calculateFullCalendarMonths(qli.getMaintStartDate(), qli.getMaintEndDate()); 
			    //for non-FTL parts
			    if(!qli.getPartDispAttr().isFtlPart()){
			        if(months > 12){
						isDateOutsideRules=true;
					}
			        if(!DateUtil.isLastDayOfMonth(qli.getMaintEndDate())){
			            isDateOutsideRules=true;
			        }
			    } 
			    //for PAE quote
				if(quoteHeader.isPAEQuote()){
				    if(qli.getPartDispAttr().isFtlPart()){
				        if(DateUtil.calculateWeeks(qli.getMaintStartDate(), qli.getMaintEndDate()) < 52){
							isDateOutsideRules=true;
						}
				    }else{
				        if(months < 12){
							isDateOutsideRules=true;
						}
				    }
				}
			}
			
			//New for DSW 10.1
			if(spBidPartGroups != null && spBidPartGroups.size()>0){
						    for(int j=0; j<spBidPartGroups.size(); j++){			        
						        SpBidPartGroup spBidPartGroup = (SpBidPartGroup)spBidPartGroups.get(j);
						        if(!forceSbGroups.contains(spBidPartGroup.getSpBidGroupName()))
								 forceSbGroups.add(spBidPartGroup.getSpBidGroupName());
						    }
						}
			
			if(qli.getStartDtOvrrdFlg())
				isStartDateOverriden = true;
			if(qli.getEndDtOvrrdFlg())
				isEndDateOverriden =true;

			//Get max channel margin overriden discount and total discount
			if(qli.getChnlOvrrdDiscPct()!=null && chnlOvrrdDiscPct.compareTo(qli.getChnlOvrrdDiscPct()) <= 0)
				chnlOvrrdDiscPct = qli.getChnlOvrrdDiscPct();
			if ( qli.getChnlStdDiscPct() != null && defaultBPDist.compareTo(qli.getChnlStdDiscPct()) <= 0 )
				defaultBPDist = qli.getChnlStdDiscPct();
			if(qli.getTotDiscPct()!=null && totDiscPct.compareTo(qli.getTotDiscPct()) <= 0)
				totDiscPct = qli.getTotDiscPct();

		}
        
        SalesContinuousCoverageChecker ck = new SalesContinuousCoverageChecker(quote);
        if ( ck.checkSpecialBid() )
        {
        	rule.setIsExtendedAllowedYears(Boolean.TRUE.toString());
        }

		//Sort the discount list and get the largest discount
		Collections.sort(discountList);
		Collections.sort(brandsDscnted);
		if(discountList.size()>0) {
		    rule.setDiscount(((Double)discountList.get(discountList.size()-1)).toString());
		}
		else
			rule.setDiscount("0.0");
		rule.setRvnStrmsDscntd(rvnStrmsDscntd);
		rule.setRvnStrmsShared(rvnStrmsShared);
		if ( rvnStrmsShared.size() == 1 )
		{
		    rule.setRvnStrmsSharedNot(rvnStrmsShared);
		}
		rule.setRvnStrmsNotMltplShared(rvnStrmsShared);
		rule.setRvnStrmsNotLimited(rvnStrmsShared);
		
		//set factors from line items - end

		rule.setTargetMarkets(trgtMktCodeList);
		rule.setForceSbGroups(forceSbGroups);
		rule.setDscntdGroups(dscntdSbGroups);
		rule.setDscntdGroupsNo(dscntdSbGroups);
		rule.setPartGroups(allPartGroups);
		
        //Calculate total price into USD
		Double tot = getTotalPriceInUSD(quoteHeader);
		if(tot != null){
		    rule.setTotalPrice(String.valueOf(tot.doubleValue()));
		}
		
		if ( null != quote.getCustomer() )
		{
		    custSetDes.add(this.getCustSetDesCode(quote.getCustomer().getCustSetDesCode(), quote.getCustomer().getCustSetDesCode2()));
		    rule.setCustSetDes(custSetDes);
		}
		
		//setCtrctVar
		if(null != quote.getCustomer() && null != quote.getCustomer().getContractList())
		for(int i=0;i<quote.getCustomer().getContractList().size();i++){
			Contract contract = (Contract)quote.getCustomer().getContractList().get(i);
			if(!ctrctVars.contains(contract.getSapContractVariantCode()))
			    ctrctVars.add(contract.getSapContractVariantCode());
		}
		rule.setCtrctVar(ctrctVars);
		
		rule.setIsTermChangeInvolved(String.valueOf(quote.getQuoteHeader().isTriggerTC()));
//		rule.setIsCtrctLvlPricing(String.valueOf(sbInfo.isSetCtrctLvlPricng()));
//		rule.setIsRflctPrevPricing(String.valueOf(sbInfo.isPreApprvdCtrctLvlPric()));
//		rule.setIsForELA(String.valueOf(sbInfo.isElaTermsAndCondsChg()));
//		rule.setIsLandedModel(String.valueOf(sbInfo.isFulfllViaLanddMdl()));
//		rule.setInitSpeclBidApprFlag(String.valueOf(sbInfo.isInitSpeclBidApprFlag()));
//		rule.setBidIterationFlag(String.valueOf(quoteHeader.isBidIteratnQt()));
		rule.setCpqExcep(String.valueOf(sbInfo.isCpqExcep()));
		
		if ( quote.getCustomer() != null )
		{
		    rule.setGovEntityIndCode(quote.getCustomer().getGovEntityIndCode() == 1 ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
		}

		//Set channel margin factors
		if(chnlOvrrdDiscPct.doubleValue() != 0){
			rule.setIsBidCntsChnlDscnt(Boolean.TRUE.toString());
		}
		if (chnlOvrrdDiscPct.doubleValue() == 0 && defaultBPDist.doubleValue() > 0 )
        {
        	chnlOvrrdDiscPct = defaultBPDist;
        }
		rule.setChnlMrgnDscnt(chnlOvrrdDiscPct.toString());
		rule.setCmbdChnlDscnt(totDiscPct.toString());

		//rule.setIsDateOutsideRules
		if(isDateOutsideRules){
			rule.setIsDateOutsideRules(Boolean.TRUE.toString());
		}
		else{
			if(quoteHeader.isSalesQuote()){
				if (isStartDateOverriden || isEndDateOverriden)
					rule.setIsDateOutsideRules(Boolean.TRUE.toString());
				else
					rule.setIsDateOutsideRules(Boolean.FALSE.toString());
			}
		}		
		
		rule.setBrandsDscntd(brandsDscnted);
		rule.setControlledProducts(ctrldPortfolios);
		
		if ( sbInfo.isPartLessOneYear() )
		{
		    rule.setIsDateOutsideRules(Boolean.TRUE.toString());
		}
		
//		if("2".equals(sbInfo.getSalesDiscTypeCode())){
//				rule.setIsDueToSalesPlay(Boolean.TRUE.toString());
//		}
//		else{
//			rule.setIsDueToSalesPlay(Boolean.FALSE.toString());
//		}

		rule.setIsRoyaltyDscntd(String.valueOf(sbInfo.isRyltyDiscExcdd()));
		
		//special bid configuration questions
//		List answers = new ArrayList();
//		if(sbInfo.getQuestions()!=null){
//		    for(int i=0; i<sbInfo.getQuestions().size(); i++){
//		        SpecialBidQuestion quz = (SpecialBidQuestion)sbInfo.getQuestions().get(i);
//		        String answer = String.valueOf(quz.getConfigNum())+"@@";
//		        if(quz.getAnswer()==0)
//		            answer = answer + Boolean.FALSE.toString();
//		        else if(quz.getAnswer()==1)
//		            answer = answer + Boolean.TRUE.toString();
//		        answers.add(answer);
//		    }
//		    rule.setAnswers(answers);
//		}
		
//		if ( sbInfo.getRateBuyDown() == 1 )
//		{
//		    rule.setRateBuyDown(Boolean.TRUE.toString());
//		}
//		
//		if ( sbInfo.isCompressedCover() )
//		{
//		    rule.setCompressedCover(Boolean.TRUE.toString());
//		}
		return rule;
	}

	protected Rule getRuleFromSQOQuote(Quote quote){
		Rule rule = new Rule_Impl();
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		SpecialBidInfo sbInfo = quote.getSpecialBidInfo();
		List lineItems = quote.getLineItemList();
		//set factors directly from quoteHeader
		rule.setAcquisitions(getList(quoteHeader.getAcqrtnCode()));

		rule.setQuoteTypes(getList(quoteHeader.getLob().getCode()));
		if(quoteHeader.isMigration()){
			rule.setQuoteTypes(getList(QuoteConstants.LOB_FCT));
			//rule.setIsMigration(String.valueOf(quoteHeader.isMigration())); --coment this out to fix the no issue
		}
		//Should set the ismigration condition no matter the quote is migrated or not. 2008/8/26 -joe
		rule.setIsMigration(String.valueOf(quoteHeader.isMigration()));
		if(sbInfo.getSpBidRgn()!=null && !sbInfo.getSpBidRgn().equalsIgnoreCase("0"))
			rule.setSbRegion(sbInfo.getSpBidRgn()); 
		if(sbInfo.getSpBidDist()!=null && !sbInfo.getSpBidDist().equalsIgnoreCase("0"))
			rule.setSbDistrict(sbInfo.getSpBidDist()); 
		rule.setSbTypes(getList(sbInfo.getSpBidType()));
		rule.setBizOrgs(getList(quoteHeader.getBusOrgCode()));
		rule.setFfmtSource(quoteHeader.getFulfillmentSrc());
		rule.setCustSegs(getList(sbInfo.getSpBidCustIndustryCode()));
		rule.setCountry(quoteHeader.getCountry().getCode3());
		//set factors from line items - begin
		List rvnStrmsShared = new ArrayList();
		List rvnStrmsDscntd = new ArrayList();
		List discountList =  new ArrayList();
		List forceSbGroups =  new ArrayList();
		List dscntdSbGroups =  new ArrayList();
		List allPartGroups = new ArrayList();
		List allCustomerGroups = new ArrayList();
		List brandsDscnted = new ArrayList();
		List ctrctVars = new ArrayList();
		List trgtMktCodeList = new ArrayList();
		List ctrldPortfolios = new ArrayList();
        List custSetDes  = new ArrayList();
        
		rule.setHasUnpublishedParts(Boolean.FALSE.toString());
		rule.setIsExtendedAllowedYears(Boolean.FALSE.toString());
		rule.setIsDateOutsideRules(Boolean.FALSE.toString());
		rule.setIsTermChangeInvolved(Boolean.FALSE.toString());
		rule.setIsCtrctLvlPricing(Boolean.FALSE.toString());
		rule.setIsRflctPrevPricing(Boolean.FALSE.toString());
		rule.setIsForELA(Boolean.FALSE.toString());
		rule.setIsLandedModel(Boolean.FALSE.toString());
		rule.setIsRoyaltyDscntd(Boolean.FALSE.toString());
		rule.setIsDueToSalesPlay(Boolean.FALSE.toString());
		rule.setHasUnauthorizedProd(Boolean.FALSE.toString());
		rule.setIsBackDated(Boolean.FALSE.toString());
		rule.setIsBidCntsChnlDscnt(Boolean.FALSE.toString());
		rule.setRewlQuoteReOpened(Boolean.FALSE.toString());
		rule.setEolPartsContained(Boolean.FALSE.toString());
		rule.setRateBuyDown(Boolean.FALSE.toString());
		rule.setCompressedCover(Boolean.FALSE.toString());
		rule.setInitSpeclBidApprFlag(Boolean.FALSE.toString());
		rule.setGovEntityIndCode(Boolean.FALSE.toString());
		rule.setAgreementType(null);
		rule.setOEMASLType(null);
		rule.setBidIterationFlag(Boolean.FALSE.toString());
		rule.setCpqExcep(Boolean.FALSE.toString());
		rule.setExportRestricted(Boolean.FALSE.toString());
		
		rule.setGrowthDelegation(QuoteConstants.QUOTE_GRWTH_DLGTN_TYPE_NO);
		rule.setPartialGrowthDelegation(Boolean.FALSE.toString());
		rule.setSplitGrowthDelegation(Boolean.FALSE.toString());
		rule.setPriceDecline(Boolean.FALSE.toString());
		rule.setMultiYearDeal(Boolean.FALSE.toString());
		rule.setImpldGrwthPct(null);
		rule.setProvisngDaysChanged(Boolean.FALSE.toString());
		rule.setNeedGoeReview(Boolean.FALSE.toString());
		rule.setBidExpDateExtended(Boolean.FALSE.toString());
		rule.setTermsGreaterThan60(Boolean.FALSE.toString());
		
		//If this is a ELA quote, bypass growth delegation routing factors
		if(!sbInfo.isElaTermsAndCondsChg()){
			String type = GrowthDelegationUtil.getQuoteGrwthDlgtnType(quote);
			rule.setGrowthDelegation(type);
			if(GrowthDelegationUtil.isGrwthDltgnQuote(type)){
				if(GrowthDelegationUtil.isFullGrwthDlgtn(type)){
					if(GrowthDelegationUtil.isPartialRenewal(quote)){
						rule.setPartialGrowthDelegation(Boolean.TRUE.toString());
					}
				}
				
				if(quote.getQuoteHeader().getImpldGrwthPct() != null){
					rule.setImpldGrwthPct(quote.getQuoteHeader().getImpldGrwthPct().toString());
				}
				
				if(sbInfo.isSplitBid()){
					rule.setSplitGrowthDelegation(Boolean.TRUE.toString());
				}
				
				if(GrowthDelegationUtil.isPriceDecline(lineItems, quoteHeader)){
					rule.setPriceDecline(Boolean.TRUE.toString());
				}
				
				if ( null != lineItems && lineItems.size() != 0 ){
					for(int i = 0; i < lineItems.size(); i++){
						QuoteLineItem qli = (QuoteLineItem)lineItems.get(i);
						if(qli.isReferenceToRenewalQuote() && null != qli.getAddtnlYearCvrageLineItems() && qli.getAddtnlYearCvrageLineItems().size() != 0 ){
							rule.setMultiYearDeal(Boolean.TRUE.toString());
							break;
						}
					}
				}
			}
		}
		
		boolean isStartDateOverriden = false;
		boolean isEndDateOverriden = false;
		boolean isDateOutsideRules = false;
//		boolean isBidCntsChnlDscnt = false;
//		Calendar startDate = null;
//		Calendar endDate = null;
		
		//to detemine the part type
		//(is it a maintenance part? is it a related maintenance part? is it an unrelated maintenance part?)
		PartTypeChecker checker = new PartTypeChecker(quote);
        checker.checkType();
        
        //get reseller to check if is autorized to sell controlled products
//        Partner reseller = quote.getReseller();
        
        //Add for channel margin overriden discount and total discount
        Double chnlOvrrdDiscPct=new Double(0);
        Double defaultBPDist = new Double(0);
        
        Double totDiscPct=new Double(0);
        
        if(sbInfo.isTrmGrt60Mon())
        	rule.setTermsGreaterThan60(Boolean.TRUE.toString());
        
        if(quoteHeader.getBackDatingFlag())
        	rule.setIsBackDated(Boolean.TRUE.toString());
        
        if(quoteHeader.getRenwlQuoteSpeclBidFlag() == 1)
            rule.setRewlQuoteReOpened(Boolean.TRUE.toString());
        
        if(quoteHeader.getHasObsoletePartsFlag())
            rule.setEolPartsContained(Boolean.TRUE.toString());
		
        if ( quoteHeader.isOEMQuote() )
        {
        	rule.setAgreementType(quoteHeader.getOrdgMethodCode());
        	rule.setOEMASLType(String.valueOf(quoteHeader.getOemBidType()));
        }
        
        if (null != quote.getCustomer()){
            List customerGroups = quote.getCustomer().getCustomerGroupList();
            if (null != customerGroups && !customerGroups.isEmpty()){
            	for ( int j = 0; j < customerGroups.size(); j++ )
				{
            		CustomerGroup customerGroup = (CustomerGroup)customerGroups.get(j);
					if ( !allCustomerGroups.contains(customerGroup.getCustomerGroupName()) )
					{
						allCustomerGroups.add(customerGroup.getCustomerGroupName());
					}
				}
            	rule.setCustomerGroups(allCustomerGroups);
            }
        }
		//controlled product
        if (!sbInfo.isResellerAuthorizedToPortfolio())
        	rule.setHasUnauthorizedProd(Boolean.TRUE.toString());

        for(int i=0;i<lineItems.size();i++){
			QuoteLineItem qli = (QuoteLineItem)lineItems.get(i);
			
			if(qli.isExportRestricted()){
				rule.setExportRestricted(Boolean.TRUE.toString());
			}
			
			if ( !rvnStrmsShared.contains(qli.getRevnStrmCode()) )
			{
			    rvnStrmsShared.add(qli.getRevnStrmCode());
			}
			
			List partGroups = qli.getPartGroups();
			if ( partGroups != null )
			{
				for ( int j = 0; j < partGroups.size(); j++ )
				{
					PartGroup partGroup = (PartGroup)partGroups.get(j);
					if ( !allPartGroups.contains(partGroup.getGroupName()) )
					{
						allPartGroups.add(partGroup.getGroupName());
					}
				}
			}
			List spBidPartGroups=qli.getSpBidPartGroups();
			//If a part is discounted or not
			if(qli.getLineDiscPct() != 0 || (qli.getOverrideUnitPrc()!=null && Double.parseDouble(String.valueOf(qli.getOverrideUnitPrc())) > 0)
					|| (qli.getChnlOvrrdDiscPct() != null && qli.getChnlOvrrdDiscPct().doubleValue() > 0) 
					){
				if(!rvnStrmsDscntd.contains(qli.getRevnStrmCode()))
				    rvnStrmsDscntd.add(qli.getRevnStrmCode());
				if(partGroups != null && partGroups.size()>0){
				    for(int j=0; j<partGroups.size(); j++){
				        PartGroup partGroup = (PartGroup)partGroups.get(j);
	                    if(!spBidPartGroups.contains(partGroup.getGroupName()) && !dscntdSbGroups.contains(partGroup.getGroupName()))
				        dscntdSbGroups.add(partGroup.getGroupName());
				    }
				}
				discountList.add(new Double(qli.getLineDiscPct()));
				if(StringUtils.isNotBlank(qli.getSwProdBrandCode())){
				    if(!brandsDscnted.contains(qli.getSwProdBrandCode()))
				        brandsDscnted.add(qli.getSwProdBrandCode());
				}
				if(StringUtils.isNotBlank(qli.getProdTrgtMktCode())){
				    if(!trgtMktCodeList.contains(qli.getProdTrgtMktCode()))
				        trgtMktCodeList.add(qli.getProdTrgtMktCode());
				}
			}
			if(qli.isPartRestrct())
				rule.setHasUnpublishedParts(Boolean.TRUE.toString());
			
			//controlled product
			if(qli.isControlled()){
			    if(!ctrldPortfolios.contains(qli.getControlledCode()))
			        ctrldPortfolios.add(qli.getControlledCode());
			}
			
			// To determine the value of isExtendedAllowedYears
			PartDisplayAttr attr = qli.getPartDispAttr();
            if(attr.isMaintBehavior()){
                if(attr.isRelatedMaint() && (qli.getAddtnlMaintCvrageQty()>1)){
                    rule.setIsExtendedAllowedYears(Boolean.TRUE.toString());
                }
                else if(attr.isUnRelatedMaint() && (qli.getAddtnlMaintCvrageQty()>2)){
                    rule.setIsExtendedAllowedYears(Boolean.TRUE.toString());
                }
            }			
			// To determine the value of isDateOutsideRules - begin
			//For renewal quote
			if(quoteHeader.isRenewalQuote() && !isDateOutsideRules){
			    int months = DateUtil.calculateFullCalendarMonths(qli.getMaintStartDate(), qli.getMaintEndDate()); 
			    //for non-FTL parts
			    if(!qli.getPartDispAttr().isFtlPart()){
			        if(months > 12){
						isDateOutsideRules=true;
					}
			        if(!DateUtil.isLastDayOfMonth(qli.getMaintEndDate())){
			            isDateOutsideRules=true;
			        }
			    } 
			    //for PAE quote
				if(quoteHeader.isPAEQuote()){
				    if(qli.getPartDispAttr().isFtlPart()){
				        if(DateUtil.calculateWeeks(qli.getMaintStartDate(), qli.getMaintEndDate()) < 52){
							isDateOutsideRules=true;
						}
				    }else{
				        if(months < 12){
							isDateOutsideRules=true;
						}
				    }
				}
			}
			
			//New for DSW 10.1
			if(spBidPartGroups != null && spBidPartGroups.size()>0){
						    for(int j=0; j<spBidPartGroups.size(); j++){			        
						        SpBidPartGroup spBidPartGroup = (SpBidPartGroup)spBidPartGroups.get(j);
						        if(!forceSbGroups.contains(spBidPartGroup.getSpBidGroupName()))
								 forceSbGroups.add(spBidPartGroup.getSpBidGroupName());
						    }
						}
			
			if(qli.getStartDtOvrrdFlg())
				isStartDateOverriden = true;
			if(qli.getEndDtOvrrdFlg())
				isEndDateOverriden =true;

			//Get max channel margin overriden discount and total discount
			if(qli.getChnlOvrrdDiscPct()!=null && chnlOvrrdDiscPct.compareTo(qli.getChnlOvrrdDiscPct()) <= 0)
				chnlOvrrdDiscPct = qli.getChnlOvrrdDiscPct();
			if ( qli.getChnlStdDiscPct() != null && defaultBPDist.compareTo(qli.getChnlStdDiscPct()) <= 0 )
				defaultBPDist = qli.getChnlStdDiscPct();
			if(qli.getTotDiscPct()!=null && totDiscPct.compareTo(qli.getTotDiscPct()) <= 0)
				totDiscPct = qli.getTotDiscPct();

		}
        
        SalesContinuousCoverageChecker ck = new SalesContinuousCoverageChecker(quote);
        if ( ck.checkSpecialBid() )
        {
        	rule.setIsExtendedAllowedYears(Boolean.TRUE.toString());
        }

		//Sort the discount list and get the largest discount
		Collections.sort(discountList);
		Collections.sort(brandsDscnted);
		if(discountList.size()>0) {
		    rule.setDiscount(((Double)discountList.get(discountList.size()-1)).toString());
		}
		else
			rule.setDiscount("0.0");
		rule.setRvnStrmsDscntd(rvnStrmsDscntd);
		rule.setRvnStrmsShared(rvnStrmsShared);
		if ( rvnStrmsShared.size() == 1 )
		{
		    rule.setRvnStrmsSharedNot(rvnStrmsShared);
		}
		rule.setRvnStrmsNotMltplShared(rvnStrmsShared);
		rule.setRvnStrmsNotLimited(rvnStrmsShared);
		
		//set factors from line items - end

		rule.setTargetMarkets(trgtMktCodeList);
		rule.setForceSbGroups(forceSbGroups);
		rule.setDscntdGroups(dscntdSbGroups);
		rule.setDscntdGroupsNo(dscntdSbGroups);
		rule.setPartGroups(allPartGroups);
		
        //Calculate total price into USD
		Double tot = getTotalPriceInUSD(quoteHeader);
		if(tot != null){
		    rule.setTotalPrice(String.valueOf(tot.doubleValue()));
		}
		
		Double entitledTotalPrice = this.getEntitledTotalPriceInUSD(quote);
		if (null != entitledTotalPrice) {
			rule.setEntitledTotalPrice(String.valueOf(entitledTotalPrice.doubleValue()));
		}
		if ( null != quote.getCustomer() )
		{
		    custSetDes.add(this.getCustSetDesCode(quote.getCustomer().getCustSetDesCode(), quote.getCustomer().getCustSetDesCode2()));
		    rule.setCustSetDes(custSetDes);
		}
		
		//setCtrctVar
		if(null != quote.getCustomer() && null != quote.getCustomer().getContractList())
		if(quote.getCustomer().getContractList().size() == 0){			
			if(quote.getQuoteHeader().isPAQuote() || quote.getQuoteHeader().isPAEQuote() || quote.getQuoteHeader().isOEMQuote()
					|| quote.getQuoteHeader().isSSPQuote()){
				ctrctVars.add(QuoteConstants.CONTRACT_VAR_NONE);
			}
		}
		for(int i=0;i<quote.getCustomer().getContractList().size();i++){
			Contract contract = (Contract)quote.getCustomer().getContractList().get(i);
			if(!ctrctVars.contains(contract.getSapContractVariantCode()))
			    ctrctVars.add(contract.getSapContractVariantCode());
		}
		rule.setCtrctVar(ctrctVars);
		
		rule.setIsTermChangeInvolved(String.valueOf(sbInfo.isTermsAndCondsChg() || quote.getQuoteHeader().isTriggerTC()));
		rule.setIsCtrctLvlPricing(String.valueOf(sbInfo.isSetCtrctLvlPricng()));
		rule.setIsRflctPrevPricing(String.valueOf(sbInfo.isPreApprvdCtrctLvlPric()));
		rule.setIsForELA(String.valueOf(sbInfo.isElaTermsAndCondsChg()));
		rule.setIsLandedModel(String.valueOf(sbInfo.isFulfllViaLanddMdl()));
		rule.setInitSpeclBidApprFlag(String.valueOf(sbInfo.isInitSpeclBidApprFlag()));
		rule.setBidIterationFlag(String.valueOf(quoteHeader.isBidIteratnQt()));
		rule.setCpqExcep(String.valueOf(sbInfo.isCpqExcep() || sbInfo.isOverBySQOBasic()));
		
		if ( quote.getCustomer() != null )
		{
		    rule.setGovEntityIndCode(quote.getCustomer().getGovEntityIndCode() == 1 ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
		}

		//Set channel margin factors
		if(chnlOvrrdDiscPct.doubleValue() != 0){
			rule.setIsBidCntsChnlDscnt(Boolean.TRUE.toString());
		}
		if (chnlOvrrdDiscPct.doubleValue() == 0 && defaultBPDist.doubleValue() > 0 )
        {
        	chnlOvrrdDiscPct = defaultBPDist;
        }
		rule.setChnlMrgnDscnt(chnlOvrrdDiscPct.toString());
		rule.setCmbdChnlDscnt(totDiscPct.toString());

		//rule.setIsDateOutsideRules
		if(isDateOutsideRules){
			rule.setIsDateOutsideRules(Boolean.TRUE.toString());
		}
		else{
			if(quoteHeader.isSalesQuote()){
				if (isStartDateOverriden || isEndDateOverriden)
					rule.setIsDateOutsideRules(Boolean.TRUE.toString());
				else
					rule.setIsDateOutsideRules(Boolean.FALSE.toString());
			}
		}		
		
		rule.setBrandsDscntd(brandsDscnted);
		rule.setControlledProducts(ctrldPortfolios);
		
		if ( sbInfo.isPartLessOneYear() )
		{
		    rule.setIsDateOutsideRules(Boolean.TRUE.toString());
		}
		
		if("2".equals(sbInfo.getSalesDiscTypeCode())){
				rule.setIsDueToSalesPlay(Boolean.TRUE.toString());
		}
		else{
			rule.setIsDueToSalesPlay(Boolean.FALSE.toString());
		}

		rule.setIsRoyaltyDscntd(String.valueOf(sbInfo.isRyltyDiscExcdd()));
		
		//special bid configuration questions
		List answers = new ArrayList();
		if(sbInfo.getQuestions()!=null){
		    for(int i=0; i<sbInfo.getQuestions().size(); i++){
		        SpecialBidQuestion quz = (SpecialBidQuestion)sbInfo.getQuestions().get(i);
		        String answer = String.valueOf(quz.getConfigNum())+"@@";
		        if(quz.getAnswer()==0)
		            answer = answer + Boolean.FALSE.toString();
		        else if(quz.getAnswer()==1)
		            answer = answer + Boolean.TRUE.toString();
		        answers.add(answer);
		    }
		    rule.setAnswers(answers);
		}
		
		if ( sbInfo.getRateBuyDown() == 1 )
		{
		    rule.setRateBuyDown(Boolean.TRUE.toString());
		}
		
		if ( sbInfo.isCompressedCover() )
		{
		    rule.setCompressedCover(Boolean.TRUE.toString());
		}
		
		if(sbInfo.isProvisngDaysChanged())
		{
			rule.setProvisngDaysChanged(Boolean.TRUE.toString());
		}
		
		//check GOE review for rule testing only
		if(QuoteCommonUtil.isNeedGOEReview(quote))
		{
			rule.setNeedGoeReview(Boolean.TRUE.toString());
		}
		//check if date had extended 
		if(quoteHeader.isExpDateExtendedFlag()){
			rule.setBidExpDateExtended(Boolean.TRUE.toString());
		}
		
		return rule;
	}
	
	private Double getTotalPriceInUSD(QuoteHeader header){
        try {
            String quoteCntryCode = header.getPriceCountry().getCode3();
            String quoteCurrencyCode = header.getCurrencyCode();
            
            Double tot = CurrencyConversionFactorFactory.singleton()
                                  .convertToAnotherCurrency(quoteCntryCode, quoteCurrencyCode, 
                                                         QuoteConstants.CURRENCY_USD, header.getQuotePriceTot());
            return tot;
		} catch (TopazException e) {
			logger.error(this,e, "Failed to get currency rate for this quote. ");
		}
		
		return null;
	}
	
	private Double getEntitledTotalPriceInUSD(Quote quote){
        try {
    		QuoteHeader header = quote.getQuoteHeader();
            String quoteCntryCode = header.getPriceCountry().getCode3();
            String quoteCurrencyCode = header.getCurrencyCode();
            
            Double entitledTotalPrice = CurrencyConversionFactorFactory.singleton()
        			.convertToAnotherCurrency(quoteCntryCode, quoteCurrencyCode, 
        					QuoteConstants.CURRENCY_USD, header.getQuoteTotalEntitledExtendedPrice());
            return entitledTotalPrice;
		} catch (TopazException e) {
			logger.error(this,e, "Failed to get currency rate for this quote when converting Entitled Total Price to USD. ");
		}
		
		return null;
	}

	public void printQuote(Quote quote){
		if(logger.getLogLevel()>LogContext.LOG_LEVEL_WARNING)
			return;
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		SpecialBidInfo sbInfo = quote.getSpecialBidInfo();
		List lineItems = quote.getLineItemList();
		//set factors directly from quoteHeader
		logger.debug(this,"quoteHeader.getSapQuoteNum()     :" +quoteHeader.getSapQuoteNum());       
		logger.debug(this,"quoteHeader.getRenwlQuoteNum()   :" +quoteHeader.getRenwlQuoteNum());       
		logger.debug(this,"quoteHeader.getWebQuoteNum()     :" +quoteHeader.getWebQuoteNum());       
		logger.debug(this,"quoteHeader.getAcqrtnCode()      :" +quoteHeader.getAcqrtnCode()        );       
		logger.debug(this,"quoteHeader.getLob().getCode()   :" +quoteHeader.getLob().getCode()     );
		logger.debug(this,"quoteHeader.isMigration()        :" +quoteHeader.isMigration()    );
		logger.debug(this,"rule.isMigration()               :" +quoteHeader.isMigration());
		if(quoteHeader.isMigration()){
		    logger.debug(this,"rule.quoteType is set to         : FCT" );
		}
		logger.debug(this,"sbInfo.getSpBidRgn()             :" +quoteHeader.getCountry().getCode3());
		logger.debug(this,"sbInfo.getSpBidRgn()             :" + sbInfo.getSpBidRgn()              );
		logger.debug(this,"sbInfo.getSpBidDist()            :" + sbInfo.getSpBidDist()             );
		logger.debug(this,"sbInfo.getSpBidType()            :" + sbInfo.getSpBidType()             );
		logger.debug(this,"quoteHeader.getBusOrgCode()      :" + quoteHeader.getBusOrgCode()       );
		logger.debug(this,"quoteHeader.getFulfillmentSrc()  :" + quoteHeader.getFulfillmentSrc());
		logger.debug(this,"sbInfo.getSpBidCustIndustryCode():" + sbInfo.getSpBidCustIndustryCode() );
		logger.debug(this,"quote.getBackDatingFlg()         :" + quoteHeader.getBackDatingFlag());
		logger.debug(this,"quote.getRenwlQuoteSpeclBidFlag() :" + quoteHeader.getRenwlQuoteSpeclBidFlag());
		logger.debug(this,"quote.getHasObsoletePartsFlag()         :" + quoteHeader.getHasObsoletePartsFlag());
		logger.debug(this, "sbInfo.isInitSpeclBidAppr: " + sbInfo.isInitSpeclBidApprFlag());
		if ( quote.getCustomer() != null )
		{
		    logger.debug(this, "customer set desgination: " + this.getCustSetDesCode(quote.getCustomer().getCustSetDesCode(), quote.getCustomer().getCustSetDesCode2()));
		    logger.debug(this, "agreement type: " + quote.getCustomer().getAgreementType());
		}

		//set factors from line items - begin
		List rvnStrmsShared = new ArrayList();
		List rvnStrmsDscntd = new ArrayList();
//		List brandsDscntd = new ArrayList();
		List discountList =  new ArrayList();
		List forceSbGroups =  new ArrayList();
		List dscntdSbGroups =  new ArrayList();
		List brandsDscnted = new ArrayList();
		List ctrctVars = new ArrayList();
//		List salesOrgList = null;
		List trgtMktCodeList = new ArrayList();
		List ctrldPortfolios = new ArrayList();
//		rule.setHasUnpublishedParts(Boolean.FALSE.toString());
//		rule.setIsExtendedAllowedYears(Boolean.FALSE.toString());
		boolean isStartDateOverriden = false;
		boolean isEndDateOverriden = false;
		boolean isDateOutsideRules = false;

        //Add for channel margin overriden discount and total discount
        Double chnlOvrrdDiscPct=new Double(0);
        Double defaultBPDist = new Double(0);
        Double totDiscPct=new Double(0);
		
		//to detemine the part type 
		//(is it a maintenance part? is it a related maintenance part? is it an unrelated maintenance part?)
		PartTypeChecker checker = new PartTypeChecker(quote);
        checker.checkType();
        
        //get reseller for controlled product authorization judgement
        Partner reseller = quote.getReseller();
        
		//controlled product
	    logger.debug(this,"rule.setHasUnauthorizedProd      :" + !sbInfo.isResellerAuthorizedToPortfolio() );

	    for(int i=0;i<lineItems.size();i++){
			QuoteLineItem qli = (QuoteLineItem)lineItems.get(i);
			
			if ( !rvnStrmsShared.contains(qli.getRevnStrmCode()) )
			{
			    rvnStrmsShared.add(qli.getRevnStrmCode());
			}
			
			List partGroups = qli.getPartGroups();
			List spBidPartGroups = qli.getSpBidPartGroups();
			logger.debug(this,"\t----To determine if a part is discounted or not----");
			//If a part is discounted or not
			if(qli.getLineDiscPct() !=0 || (qli.getOverrideUnitPrc()!=null && Double.parseDouble(String.valueOf(qli.getOverrideUnitPrc())) > 0)){
				if(!rvnStrmsDscntd.contains(qli.getRevnStrmCode())){
				    rvnStrmsDscntd.add(qli.getRevnStrmCode());
				    logger.debug(this,"rvnStrmsDscntd catch a value     :" + rvnStrmsDscntd );
				}
				
				if(partGroups != null && partGroups.size()>0){
				    for(int j=0; j<partGroups.size(); j++){
				        PartGroup partGroup = (PartGroup)partGroups.get(j);
				        if(!dscntdSbGroups.contains(partGroup.getGroupName())){
				            dscntdSbGroups.add(partGroup.getGroupName());
				            logger.debug(this,"dscntdSbGroups catch a value     :" + dscntdSbGroups);
				        }
				    }
				}
				discountList.add(new Double(qli.getLineDiscPct()));
				logger.debug(this,"discountList   catch a value     :" + discountList);
				if(StringUtils.isNotBlank(qli.getSwProdBrandCode())){
					if(!brandsDscnted.contains(qli.getSwProdBrandCode())){
					    brandsDscnted.add(qli.getSwProdBrandCode());
					    logger.debug(this,"brandsDscnted  catch a value     :" + brandsDscnted);
					}
				}
				if(StringUtils.isNotBlank(qli.getProdTrgtMktCode())){
				    if(!trgtMktCodeList.contains(qli.getProdTrgtMktCode())){
				        trgtMktCodeList.add(qli.getProdTrgtMktCode());
				        logger.debug(this,"trgtMktCodeList catch a value    :" + trgtMktCodeList);
				    }
				}
			}
			logger.debug(this,"\t----To determine if a part is discounted or not----End");
			logger.debug(this,"qli.isPartRestrct()			    :" + qli.isPartRestrct() );
			if(qli.isPartRestrct())
				logger.debug(this,"\t\trule.setHasUnpublishedParts('true')" );
			
//			controlled product
			if(qli.isControlled()){
			    if(!ctrldPortfolios.contains(qli.getControlledCode())){
			        ctrldPortfolios.add(qli.getControlledCode());
			        logger.debug(this,"ctrldPortfolios  catch a value     :" + ctrldPortfolios);
			    }
			    
			    int custGovEntIndCode = quote.getCustomer() == null ? 1 : quote.getCustomer().getGovEntityIndCode();
			    
			    if(reseller!=null && !reseller.isAuthorizedPortfolio(qli.getControlledCode(), custGovEntIndCode)){
			        logger.debug(this,"\t\tqli.getControlledCode() "+qli.getControlledCode());
			    }
			}

			logger.debug(this,"\t----To determin isExtendedAllowedYears----");
			PartDisplayAttr attr = qli.getPartDispAttr();
			logger.debug(this,"qli.getAddtnlMaintCvrageQty()	       :" + qli.getAddtnlMaintCvrageQty() );
			logger.debug(this,"qli.getPartDispAttr().isMaint()	       :" + attr.isMaintBehavior() );
			logger.debug(this,"qli.getPartDispAttr().isRelatedMaint()  :" + attr.isRelatedMaint() );
			logger.debug(this,"qli.getPartDispAttr().isUnRelatedMaint():" + attr.isUnRelatedMaint() );
            if(attr.isMaintBehavior()){
                if(attr.isRelatedMaint() && (qli.getAddtnlMaintCvrageQty()>1)){
                    logger.debug(this,"\t\trule.setIsExtendedAllowedYears('ture')" );
                }
                else if(attr.isUnRelatedMaint() && (qli.getAddtnlMaintCvrageQty()>2)){
                    logger.debug(this,"\t\trule.setIsExtendedAllowedYears('ture')" );
                }
            }
				
			// To determine the value of isDateOutsideRules - begin
			
			logger.debug(this,"\t----To determin isDateOutsidtRules----");
			logger.debug(this,"qli.getMaintStartDate()       	:" + qli.getMaintStartDate());
			if(qli.getMaintStartDate()!=null)
				logger.debug(this,"qli.getMaintStartDate().getTime 	:" + qli.getMaintStartDate().getTime());
			logger.debug(this,"qli.getMaintEndDate()         	:" + qli.getMaintEndDate()  );
			if(qli.getMaintEndDate()!=null)
				logger.debug(this,"qli.getMaintEndDate().getTime   	:" + qli.getMaintEndDate().getTime());
			// To measure this on non-FTL parts, if the start date is not the first of the month, move it to the first of the next month for measurement purposes only.
			logger.debug(this,"qli.getPartDispAttr()   	:" + qli.getPartDispAttr());
			logger.debug(this,"quoteHeader.isRenewalQuote()  	:" + quoteHeader.isRenewalQuote());
			
			//For renewal quote
			if(quoteHeader.isRenewalQuote() && !isDateOutsideRules){
			    int months = DateUtil.calculateFullCalendarMonths(qli.getMaintStartDate(), qli.getMaintEndDate());
			    logger.debug(this,"DateUtil.calculateFullCalendarMonths(startDate, endDate)  	:" + months);
			    //for non-FTL parts
			    if(!qli.getPartDispAttr().isFtlPart()){
			        if(months > 12){
			            isDateOutsideRules = true;
			            logger.debug(this,"Non-FTL part, duration > 12 months ==> Set isDateOutsideRules = true");
					}
			        if(!DateUtil.isLastDayOfMonth(qli.getMaintEndDate())){
			            isDateOutsideRules = true;
			            logger.debug(this,"Non-FTL part, endDate is not the last day of the month ==> Set isDateOutsideRules = true");
			        }
			    }
			    //for PAE quote
				if(quoteHeader.isPAEQuote()){
				    if(qli.getPartDispAttr().isFtlPart()){
				        logger.debug(this,"DateUtil.calculateWeeks(startDate, endDate)  	:" + DateUtil.calculateWeeks(qli.getMaintStartDate(), qli.getMaintEndDate()));
				        if(DateUtil.calculateWeeks(qli.getMaintStartDate(), qli.getMaintEndDate()) < 52){
				            isDateOutsideRules = true;
				            logger.debug(this,"PAE quote, FTL part, duration < 52 weeks ==> Set isDateOutsideRules = true");
						}
				    }else{
				        if(months < 12){
				            isDateOutsideRules = true;
				            logger.debug(this,"PAE quote, Non-FTL part, duration < 12 months ==> Set isDateOutsideRules = true");
						}
				    }
				}
			}
			// To determine the value of isDateOutsideRules - end
			logger.debug(this,"\t----To tegermin isDateOutsidtRules----End ");

			if(spBidPartGroups != null && spBidPartGroups.size()>0){
			    for(int j=0; j<spBidPartGroups.size(); j++){			        
			        SpBidPartGroup spBidPartGroup = (SpBidPartGroup)spBidPartGroups.get(j);
			        if(!forceSbGroups.contains(spBidPartGroup.getSpBidGroupName())){
				        forceSbGroups.add(spBidPartGroup.getSpBidGroupName());
				        logger.debug(this,"forceSbGroups catch a new value	:" + forceSbGroups);
			        }
			    }
			}
			logger.debug(this,"qli.getStartDtOvrrdFlg()     	:" + qli.getStartDtOvrrdFlg());
			if(qli.getStartDtOvrrdFlg()){
				isStartDateOverriden = true;
				logger.debug(this,"\tisStartDateOverriden           :" + isStartDateOverriden);
			}
			logger.debug(this,"qli.getEndDtOvrrdFlg()       	:" + qli.getEndDtOvrrdFlg());
			if(qli.getEndDtOvrrdFlg()){
				isEndDateOverriden =true;
				logger.debug(this,"\tisEndDateOverriden	            :" + isEndDateOverriden);
			}

			logger.debug(this,"\tqli.getChnlOvrrdDiscPct()	:"+qli.getChnlOvrrdDiscPct());
			logger.debug(this,"\tqli.getTotDiscPct()		:"+qli.getTotDiscPct());
			
			//Get max channel margin overriden discount and total discount
			if(qli.getChnlOvrrdDiscPct()!=null && chnlOvrrdDiscPct.compareTo(qli.getChnlOvrrdDiscPct()) <= 0)
				chnlOvrrdDiscPct = qli.getChnlOvrrdDiscPct();
			if ( qli.getChnlStdDiscPct() != null && defaultBPDist.compareTo(qli.getChnlStdDiscPct()) <= 0 )
				defaultBPDist = qli.getChnlStdDiscPct();
			if(qli.getTotDiscPct()!=null && totDiscPct.compareTo(qli.getTotDiscPct()) <= 0)
				totDiscPct = qli.getTotDiscPct();


		}

		//Sort the discount list and get the largest discount
		Collections.sort(discountList);
		Collections.sort(brandsDscnted);
		if(discountList.size()>0) {
			logger.debug(this,"discountList.get(0)       		:" + discountList.get(0));
			logger.debug(this,"discountList.get("+(discountList.size()-1)+")       		:" + discountList.get(discountList.size()-1));
			logger.debug(this,"rule.setDiscount 	      		:" + discountList.get(discountList.size()-1));
//			rule.setDiscount(((Double)discountList.get(0)).toString());
		}
		logger.debug(this,"\trule.setRvnStrmsDscntd      		:" + rvnStrmsDscntd);
		logger.debug(this,"\trule.rvnStrmsShared      		:" + rvnStrmsShared);
		if ( rvnStrmsShared.size() == 1 )
		{
		    logger.debug(this,"\trule.rvnStrmsSharedNot   		:" + rvnStrmsShared);
		}
//		rule.setRvnStrmsDscntd(rvnStrmsDscntd);
//		rule.setRvnStrmsShared(rvnStrmsShared);
		//set factors from line items - end

		logger.debug(this,"\trule.setTargetMarkets      	:" + trgtMktCodeList);
		logger.debug(this,"\trule.setForceSbGroups      	:" + forceSbGroups);
		logger.debug(this,"\trule.setDscntdGroups      		:" + dscntdSbGroups);
		logger.debug(this,"quoteHeader.getQuotePriceTot()	:" + quoteHeader.getQuotePriceTot());
        //Calculate total price into USD
		Double tot = getTotalPriceInUSD(quoteHeader);
		if(tot != null){
		    logger.debug(this,"\trule.setTotalPrice	      		:" + String.valueOf(tot.doubleValue()));
		} else {
		    logger.debug(this,"\trule.setTotalPrice	      		:null(can't convert to USD price, check currency exchange logic)");
		}
//		rule.setTargetMarkets(trgtMktCodeList);
//		rule.setForceSbGroups(forceSbGroups);
//		rule.setDscntdGroups(dscntdSbGroups);
//		rule.setTotalPrice(String.valueOf(quoteHeader.getQuotePriceTot()));
		if (null != quoteHeader.getCountry() && null != quoteHeader.getCountry().getSalesOrgList()){
//			salesOrgList = quoteHeader.getCountry().getSalesOrgList();	
			logger.debug(this,"quoteHeader.getCountry().getSalesOrgList()   	:" + quoteHeader.getCountry().getSalesOrgList());
			
		}
		
		//setCtrctVar
		if(null != quote.getCustomer() && null != quote.getCustomer().getContractList())
		for(int i=0;i<quote.getCustomer().getContractList().size();i++){
			Contract contract = (Contract)quote.getCustomer().getContractList().get(i);
			if(!ctrctVars.contains(contract.getSapContractVariantCode())){
			    ctrctVars.add(contract.getSapContractVariantCode());
			    logger.debug(this,"\tctrctVars added		   		:" + contract.getSapContractVariantCode());
			}
		}
		logger.debug(this,"\trule.setCtrctVar		      	:" + ctrctVars);
//		rule.setCtrctVar(ctrctVars);

		
//		For PA/PAE
//		rule.setIsCreditInvolved(String.valueOf(sbInfo.isCredAndRebill()));
//		rule.setIsTermChangeInvolved(String.valueOf(sbInfo.isTermsAndCondsChg()));
//		rule.setIsCtrctLvlPricing(String.valueOf(sbInfo.isSetCtrctLvlPricng()));
//		rule.setIsRflctPrevPricing(String.valueOf(sbInfo.isPreApprvdCtrctLvlPric()));
//		rule.setIsForELA(String.valueOf(sbInfo.isElaTermsAndCondsChg()));
//		rule.setIsLandedModel(String.valueOf(sbInfo.isFulfllViaLanddMdl()));
		logger.debug(this,"\trule.setIsCreditInvolved      from sbInfo.isCredAndRebill()         :" + sbInfo.isCredAndRebill()         );
		logger.debug(this,"\trule.setIsTermChangeInvolved  from sbInfo.isTermsAndCondsChg()      :" + sbInfo.isTermsAndCondsChg()      );
		logger.debug(this,"\trule.setIsCtrctLvlPricing     from sbInfo.isSetCtrctLvlPricng()     :" + sbInfo.isSetCtrctLvlPricng()     );
		logger.debug(this,"\trule.setIsRflctPrevPricing    from sbInfo.isPreApprvdCtrctLvlPric() :" + sbInfo.isPreApprvdCtrctLvlPric() );
		logger.debug(this,"\trule.setIsForELA              from sbInfo.isElaTermsAndCondsChg()   :" + sbInfo.isElaTermsAndCondsChg()   );
		logger.debug(this,"\trule.setIsLandedModel         from sbInfo.isFulfllViaLanddMdl()     :" + sbInfo.isFulfllViaLanddMdl()     );
		logger.debug(this,"\trule.setRateBuyDown           from sbInfo.getRateBuyDown()          :" + sbInfo.getRateBuyDown()     );
		logger.debug(this, "\trule.setIsDateOutsideRules   from sbInfo.isPartLessOneYear()       :" + sbInfo.isPartLessOneYear());

		//Set channel margin factors
		if(chnlOvrrdDiscPct.doubleValue() != 0){
			logger.debug(this,"\trule.setIsBidCntsChnlDscnt 	: true");
		}
		else{
			logger.debug(this,"\trule.setIsBidCntsChnlDscnt 	: false");
		}
		if ( chnlOvrrdDiscPct.doubleValue() == 0 && defaultBPDist.doubleValue() > 0 )
		{
			chnlOvrrdDiscPct = defaultBPDist;
		}
		logger.debug(this,"\trule.setChnlMrgnDscnt    		:" + chnlOvrrdDiscPct.toString());
		logger.debug(this,"\trule.setCmbdChnlDscnt    		:" + totDiscPct.toString());

		//rule.setIsDateOutsideRules
		if(isDateOutsideRules){
//			rule.setIsDateOutsideRules(Boolean.TRUE.toString());
			logger.debug(this,"\trule.setIsDateOutsideRules 	:true");
	}
		else{
			if(quoteHeader.isSalesQuote()){
				if (isStartDateOverriden || isEndDateOverriden){
					logger.debug(this,"\trule.setIsDateOutsideRules     :true");
				}
				else{
					logger.debug(this,"\trule.setIsDateOutsideRules     :false");
				}
			}
		}		
		
		if("2".equals(sbInfo.getSalesDiscTypeCode())){
			logger.debug(this,"\tsbInfo.getSalesDiscTypeCode()  :"+sbInfo.getSalesDiscTypeCode());
//				rule.setIsDueToSalesPlay(Boolean.TRUE.toString());
//				rule.setIsDueToSalesPromotion(Boolean.FALSE.toString());
				logger.debug(this,"\trule.setIsDueToSalesPlay       :true");
		}
		else{
//			rule.setIsDueToSalesPlay(Boolean.FALSE.toString());
//			rule.setIsDueToSalesPromotion(Boolean.FALSE.toString());			
			logger.debug(this,"\trule.setIsDueToSalesPlay       :false");
		}
		
		if ( sbInfo.isCompressedCover() )
		{
		    logger.debug(this,"\trule.setCompressedCover    		:" + Boolean.TRUE.toString());
		}
		
		logger.debug(this,"\trule.setIsRoyaltyDscntd        :"+sbInfo.isRyltyDiscExcdd());
		boolean govIndeCode = false;
		if ( quote.getCustomer() != null && quote.getCustomer().getGovEntityIndCode() == 1 )
		{
		    govIndeCode = true;
		}
		logger.debug(this, "\trule.setGovEntityIndeCode: " + govIndeCode);
//		rule.setIsRoyaltyDscntd(String.valueOf(sbInfo.isRyltyDiscExcdd()));
		
//		special bid configuration questions
		logger.debug(this,"\tSpecial Bid Configuration Questions: ");
		if(sbInfo.getQuestions()!=null){
		    for(int i=0; i<sbInfo.getQuestions().size(); i++){
		        SpecialBidQuestion quz = (SpecialBidQuestion)sbInfo.getQuestions().get(i);
		        String answer = String.valueOf(quz.getConfigNum())+"@@";
		        if(quz.getAnswer()==0)
		            answer = answer + Boolean.FALSE.toString();
		        else if(quz.getAnswer()==1)
		            answer = answer + Boolean.TRUE.toString();
		        logger.debug(this,"\tQuestion seq num: "+quz.getConfigNum()+"\tAnswer: "+quz.getAnswer());
		        logger.debug(this,"\tAnswers  catch a new value: "+answer);
		    }
		}

//		return rule;
	}	
	
	private ArrayList getList(String str){
		if(StringUtils.isNotBlank(str)){
			ArrayList list = new ArrayList();
			list.add(str);
			return list;
		}
		else
			return null;
	}
	
	private String getCustSetDesCode(String code, String code2)
	{
	    if ( code2 == null )
        {
            code2 = "_";
        }
        else if ( code2.trim().equals("") )
        {
            code2 = "-";
        }
        else
        {
            code2 = code2.trim();
        }
	    return code + "_" + code2;
	}
	
	private List filterDateExtendedGroup(Quote quote, List approvers)
	{
		List groupList = new ArrayList();
		
		// ensure date extended bid routing to a sole approval group
		for(int i=0; i < approvers.size(); i++){
			if(quote.getQuoteHeader().isExpDateExtendedFlag() && ((ApprovalGroup) approvers.get(i)).getType().getExtendExpDate() == 1){
				groupList.add(approvers.get(i));
			}
		}
		
		// return all approval groups if no extended bid
		if(groupList.size() == 0)
		{
			groupList = approvers;
		}
		logger.debug(this,"filterGroups result: get " + groupList.size() + " ApprovalGroups,");
		return groupList;
	}	
}
