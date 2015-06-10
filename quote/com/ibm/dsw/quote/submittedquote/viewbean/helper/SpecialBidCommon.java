package com.ibm.dsw.quote.submittedquote.viewbean.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.SpecialBidCondition;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.domain.QuoteLineItem.PartGroup;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo.Approver;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo.ChosenApprover;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpecialBidCommon</code> contains the basic data that needed by SQ
 * special bid tab and RQ special bid tab
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Apr 26, 2007
 */

public class SpecialBidCommon implements Serializable {
    private static final long serialVersionUID = 6942812083427918107L;

	static LogContext logContext = LogContextFactory.singleton().getLogContext();  

    private Quote quote;

    public transient List regions; // String list

    public transient List districts; // String list

    public transient List categories; // String list

    public transient List specialBidTypes; // String list

    public transient List industrySegments; // CodeDescObj list

    public SpecialBidCondition spBidCondition = null;

    public transient List levelList = new ArrayList();
    
    private transient List allLevelList = new ArrayList();

    // all maps with group level as the key
    public transient Map groupMap = new HashMap();
    
    public transient Map rdyToOrderMap = new HashMap();

    public transient Map chosenApproverMap = new HashMap();

    public transient Map approverCommentMap = new HashMap();

    public transient Map approversMap = new HashMap();
    
    public int nextPendingLevel = 0;

    public int hasRoyaltyPartGroupFlag = 0;
    
    public transient List addApprCmtList = new ArrayList();
    
    /** for get sb types, FCT lob: FCT; PA/PAE lob: PAE; cause lob = PAE there no data and sb types only for FCT */
    public static final String SB_TYPE_LOB_FCT = "FCT";

    public SpecialBidCommon(Quote quote, String userId) throws QuoteException {
        this.quote = quote;
        logContext.debug(this, "\nstart initiating special bid condition");
        spBidCondition = new SpecialBidCondition(this.quote, userId);
        logContext.debug(this, "\nend initiating special bid condition");
        init();
    }

    public SpecialBidCondition getSpBidCondition() {
        return spBidCondition;
    }

    public boolean showNoSpbidText() {
    	//2008-06-13 removed the condition isDiscountOverDelegation, if hasDiscountOrOverridePrice == false, 
    	//isDiscountOverDelegation will be false forever
        return !spBidCondition.hasDiscountOrOverridePrice() && !spBidCondition.isMaintOverDefaultPeriod()
                && !spBidCondition.hasRestrictPart() && !spBidCondition.isPartGroupRequireSpBid()
                && !spBidCondition.isEMEADiscountRequireSpBid();
    }

    public boolean showCustomerIndustrySegment() {

        String geo = StringUtils.trimToEmpty(this.quote.getQuoteHeader().getCountry().getSpecialBidAreaCode());
        return QuoteConstants.GEO_AP.equals(geo) || QuoteConstants.GEO_EMEA.equals(geo);

    }

    public boolean showTransFullfilledLanedModle() {

        String geo = StringUtils.trimToEmpty(this.quote.getQuoteHeader().getCountry().getSpecialBidAreaCode());
        return QuoteConstants.GEO_AP.equals(geo);
    }

    /**
     *  
     */
    private void init() {
        try {
            String lob = quote.getQuoteHeader().getLob().getCode();

            String countryCode = quote.getQuoteHeader().getCountry().getCode3();
            String sapRegion = "";
            if (quote.getCustomer() != null) {
                sapRegion = quote.getCustomer().getSapRegionCode();
                countryCode = quote.getCustomer().getCountryCode();
            }
            String sapContractVariantCode = null;

            if (null != quote.getCustomer()) {
                List contracts = quote.getCustomer().getContractList();
                if ((null != contracts) && (contracts.size() > 0)) {
                    Contract contract = (Contract) contracts.get(0);
                    sapContractVariantCode = contract.getSapContractVariantCode();
                }
            }
            if(null == sapContractVariantCode
            	&& QuoteConstants.COUNTRY_CODE_USA.equals(countryCode)
				&& (quote.getCustomer() != null && quote.getCustomer().isGOVCustomer())
            	){
            	sapContractVariantCode = CustomerConstants.AGRMNT_TYPE_GOVERNMENT;
            }
            
            CacheProcess process = CacheProcessFactory.singleton().create();

            regions = process.getSpBidRegions(countryCode, sapRegion, sapContractVariantCode);

            districts = process.getSpBidDistricts(countryCode, sapRegion, sapContractVariantCode);

            categories = process.getSpBidCategories(countryCode, lob);

            String geo = this.quote.getQuoteHeader().getCountry().getSpecialBidAreaCode().trim();

            if (QuoteConstants.GEO_AP.equals(geo) || QuoteConstants.GEO_EMEA.equals(geo)) {
                industrySegments = process.getSpBidCustIndSegments();

            }

            specialBidTypes = process.getSpBidTypes(countryCode, SB_TYPE_LOB_FCT);
            logContext.debug(this, "regions=" + regions + "\ndistricts=" + districts + "\ncategories=" + categories
                    + "\nindustrySegments=" + industrySegments);

            if (null == regions) {
                regions = new ArrayList();
            }
            if (null == districts) {
                districts = new ArrayList();
            }
            if (null == categories) {
                categories = new ArrayList();
            }
            if (null == industrySegments) {
                industrySegments = new ArrayList();
            }
            if (null == specialBidTypes) {
                specialBidTypes = new ArrayList();
            }

            Iterator iterator;
            // to determine whether there is a part in a royalty part group
            if (quote.getLineItemList() != null) {
                iterator = quote.getLineItemList().iterator();
                while (iterator.hasNext()) {
                    QuoteLineItem item = (QuoteLineItem) iterator.next();
                    
                    List partGroups = item.getPartGroups();
                    for(Iterator it = partGroups.iterator(); it.hasNext();) {
                    	PartGroup pg = (PartGroup)it.next();
                    	if(StringUtils.trimToEmpty(pg.getGroupName()).startsWith(QuoteConstants.ROYALTY_PART_GROUP_NAME)) {
                    		this.hasRoyaltyPartGroupFlag = 1;
                    		break;
                    	}
                    }
//                    if (item.getPartGroup() != null && item.getPartGroup().getGoupName() != null
//                            && item.getPartGroup().getGoupName().startsWith()) {
//                    	this.hasRoyaltyPartGroupFlag = 1;
//                    }
                }
            }
            logContext.debug(this, "royalty part in quote = " + this.hasRoyaltyPartGroupFlag);

            SpecialBidInfo bidInfo = quote.getSpecialBidInfo();
            // when quote is not submitted and non-special bid
            if (bidInfo == null
                    || (!quote.getQuoteHeader().getQuoteStageCode().equals(QuoteConstants.QUOTE_STAGE_CODE_SAPSBQT)
                            && !quote.getQuoteHeader().getQuoteStageCode().equals(QuoteConstants.QUOTE_STAGE_CODE_CANCEL)&& !quote.getQuoteHeader().getQuoteStageCode().equals(QuoteConstants.QUOTE_STAGE_CODE_CPPRCINC))) {
                return;
            }
            
            iterator = bidInfo.getAllTypeInfo().iterator();
            int preLvl = -1;
            while ( iterator.hasNext() )
            {
                SpecialBidInfo.Approver apr = (SpecialBidInfo.Approver)iterator.next();
                if ( apr.getLevel() != preLvl )
                {
                    preLvl = apr.getLevel();
                    allLevelList.add(new Integer(preLvl));
                }
                groupMap.put(new Integer(preLvl), apr.getGroupType());
                rdyToOrderMap.put(new Integer(preLvl), apr.getRdyToOrder());
            }
            
            iterator = bidInfo.getChosenApprovers().iterator();
            while (iterator.hasNext()) {
                SpecialBidInfo.ChosenApprover chosenApprover = (ChosenApprover) iterator.next();
                Integer tmp = new Integer(chosenApprover.groupLevel);
                if ( !levelList.contains(tmp) )
                {
                	levelList.add(tmp);
                }
                chosenApproverMap.put(new Integer(chosenApprover.getGroupLevel()), chosenApprover);
                logContext.debug(this, "\nchosen approver, level=" + chosenApprover.groupLevel + " email="
                        + chosenApprover.userEmail + " supersede approval type = "+ (chosenApprover.superSedeApproveType==null?"null":chosenApprover.superSedeApproveType) + "\n");
            }
            Collections.sort(levelList);
            // get pending group's level
            if (this.quote.getQuoteUserAccess() != null) {
                nextPendingLevel = this.quote.getQuoteUserAccess().getPendingAppLevel();
                logContext.debug(this, "next pending group level is " + nextPendingLevel);
            }
            // store approver list according to its level
            iterator = bidInfo.getAllApprovers().iterator();
            while (iterator.hasNext()) {
                SpecialBidInfo.Approver approver = (Approver) iterator.next();
                Integer key = new Integer(approver.level);
//                groupMap.put(key, approver.groupType);
                List levelApprovers = (List) approversMap.get(key);
                if (levelApprovers == null) {
                    levelApprovers = new ArrayList();
                    approversMap.put(key, levelApprovers);
                }
                levelApprovers.add(approver);
            }
            for (int i = 0; i < allLevelList.size(); i++) {
                Integer lvl = (Integer) allLevelList.get(i);
                logContext.debug(this, "\nall approvers, level=" + lvl + " members=" + approversMap.get(lvl) + "\n");
            }
            // store approver action hitories according to level
            iterator = bidInfo.getApproverComments().iterator();
            while (iterator.hasNext()) {
                SpecialBidInfo.ApproverComment approverComment = (SpecialBidInfo.ApproverComment) iterator.next();
                Integer key = new Integer(approverComment.getApproverLvl());
                List comments = (List) approverCommentMap.get(key);
                if (comments == null) {
                    comments = new ArrayList();
                    approverCommentMap.put(key, comments);
                }
                comments.add(approverComment);
            }
            
            //add addition approver comments to another list, and remove from bidInfo.getApproverComments() list
            initAddiApprCmts();
        } catch (Exception e) {
            logContext.error(this, e);
        }
    }
    
    protected void initAddiApprCmts()
    {
        for (int i = 0; i < allLevelList.size(); i++) {
            Integer lvl = (Integer) allLevelList.get(i);
            logContext.debug(this, "level=" + lvl + " action histories=" + approverCommentMap.get(lvl) + "\n");
            List comments = (List)approverCommentMap.get(lvl);
            if ( comments == null )
            {
                continue;
            }
            logContext.debug(this, "level: " + lvl + ", commnts size: " + comments.size());
            int j = 0;
            if ( !levelList.contains(lvl) )
            {
            	j = comments.size();
            }
            else
            {
	            for (j = comments.size() - 1; j >= 0; j--)
	            {
	                SpecialBidInfo.ApproverComment acmt = (SpecialBidInfo.ApproverComment)comments.get(j);
	                if ( SubmittedQuoteConstants.APPRVR_ACTION_APPROVE.equals(acmt.getComment().getAction()) )
	                {
	                    break;
	                }
	            }
            }
            for ( int k = 0; k < j; k++ )
            {
                this.addApprCmtList.add(comments.get(k));
                comments.remove(k);
                k--;
                j--;
            }
        }
        Collections.sort(this.addApprCmtList);
    }

    public Map getApproverCommentMap() {
        return approverCommentMap;
    }

    public Map getApproversMap() {
        return approversMap;
    }

    public Map getChosenApproverMap() {
        return chosenApproverMap;
    }

    public Map getGroupMap() {
        return groupMap;
    }
    
    public Map getRdyToOrderMap() {
        return rdyToOrderMap;
    }

    public List getLevelList() {
        return levelList;
    }

    public int getHasRoyaltyPartGroupFlag() {
        return hasRoyaltyPartGroupFlag;
    }

    public int getNextPendingLevel() {
        return nextPendingLevel;
    }
    public List getAddApprCmtList() {
        return addApprCmtList;
    }
}
