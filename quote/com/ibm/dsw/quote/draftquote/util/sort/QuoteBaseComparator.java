package com.ibm.dsw.quote.draftquote.util.sort;

import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.QtSortOrderCPCFactory;
import com.ibm.dsw.quote.appcache.domain.QtSortOrderFctPPTCFactory;
import com.ibm.dsw.quote.appcache.domain.QtSortOrderPPTCFactory;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). <p/>
 *
 * The <code>QuoteBaseComparator</code> is a abstract class for common methods
 *
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 *
 * Creation date: Mar 20, 2007
 */

public abstract class QuoteBaseComparator implements Comparator {

    private static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    private static final PAAndPAEComparator paComparator = new PAAndPAEComparator();

    private static final PPSSComparator ppsComparator = new PPSSComparator();

    private static final FCTComparator fctComparator = new FCTComparator();

    private static final RenwlQuoteComparator rqComparator = new RenwlQuoteComparator();

    private static final RQFCTComparator rqFCTComparator = new RQFCTComparator();

    private static final OEMComparator oemComparator = new OEMComparator();

    private static final SaaSPartComparator saasPartComparator = new SaaSPartComparator();
    
    private static final MonthlySwPartComparator monthlySwPartComparator = new MonthlySwPartComparator();

    private static final TouErrMsgComparator touErrMsgComparator = new TouErrMsgComparator();
    
    //private CacheProcess process;
    public static Comparator createTouErrMsgComparator(){
        return touErrMsgComparator;
    }
    
    public static Comparator createPAAndPAEComparator() {
        return paComparator;
    }

    public static Comparator createPPSComparator() {
        return ppsComparator;
    }

    public static Comparator createFCTComparator() {
        return fctComparator;
    }
    public static Comparator createRQComparator(){
        return rqComparator;
    }
    public static Comparator createRQFCTComparator(){
        return rqFCTComparator;
    }

    public static Comparator createSaaSPartComparator(){
        return saasPartComparator;
    }
    
    public static Comparator createMonthlySwPartComparator(){
        return monthlySwPartComparator;
    }

    public static Comparator createOEMComparator(){
        return oemComparator;
    }

    public QuoteBaseComparator() {
        /*try {
            process = CacheProcessFactory.singleton().create();
        } catch (QuoteException e) {
            logContext.fatal(this, "Can't get CacheProcess, error=" + e.getMessage());
        }*/
    }

    protected int getCPCOrder(String cpc) {
        int order = Integer.MAX_VALUE;
        try {
            order = QtSortOrderCPCFactory.singleton().findQtSortOrderCPCByCode(cpc).getOrder();

        } catch (Throwable e) {
            logContext.debug(this, "Get Contract Program Code Order error , code=" + cpc + ", error=" + e.getMessage());
        }
        return order;

    }

    protected int getPPTCOrder(String pptc) {
        int order = Integer.MAX_VALUE;
        try {
            order = QtSortOrderPPTCFactory.singleton().findQtSortOrderPPTCByCode(pptc).getOrder();

        } catch (Throwable e) {
            logContext.debug(this, "Get Product Package  Type Code Order error , code=" + pptc + ", error="
                    + e.getMessage());
        }
        return order;

    }
    protected int getPPTCOrderForFCT(String pptc){
        int order = Integer.MAX_VALUE;
        try {

            order = QtSortOrderFctPPTCFactory.singleton().findQtSortOrderFctPPTCByCode(pptc).getOrder();

        } catch (Throwable e) {
            logContext.debug(this,"Get Product Package Type Coder Order (only for FCT) error, code = "+pptc+",error="+e.getMessage());

        }
        return order;
    }
    protected static int compareDate(Date d1, Date d2) {

        if ((null != d1) && (null != d2)) {
            return d1.compareTo(d2);
        }
        return 0;

    }

    protected static int compareManualSortOrder(QuoteLineItem item1, QuoteLineItem item2) {

        int seqNum1 = item1.getManualSortSeqNum();
        int seqNum2 = item2.getManualSortSeqNum();
        if(seqNum1 == 0)
        {
            seqNum1 = Integer.MAX_VALUE;
        }
        if(seqNum2 == 0)
        {
            seqNum2 = Integer.MAX_VALUE;
        }
        return seqNum1 - seqNum2;
    }

    protected int compareCPC(QuoteLineItem item1, QuoteLineItem item2) {
        int order1 = getCPCOrder(item1.getCtrctProgCode());
        int order2 = getCPCOrder(item2.getCtrctProgCode());
        return order1 - order2;
    }

    protected int comparePPTC(QuoteLineItem item1, QuoteLineItem item2) {
        int order1 = getPPTCOrder(item1.getProdPackTypeCode());
        int order2 = getPPTCOrder(item2.getProdPackTypeCode());

        return order1 - order2;
    }

    protected static int compareString(String s1, String s2) {
        s1 = convertNulltoEmpty(s1);
        s2 = convertNulltoEmpty(s2);
        return s1.compareTo(s2);
    }

    protected int compareApplianceAttribute(QuoteLineItem item1, QuoteLineItem item2) {
    	//display the normal software part first, then appliance part
        int result = compareString(getPartTypeStrValue(item1),getPartTypeStrValue(item2));
        if (result != 0) {
            return result;
        }
        if(item1.isApplncPart() && item2.isApplncPart()){
        	//sort by appliance id
            result = compareString(getApplianceIdStrValue(item1), getApplianceIdStrValue(item2));
            if (result != 0) {
                return result;
            }
            //If appliance id is not blank, then sort parts in the group
            if(item1.isHasApplncId()){
	            //display the main appliance part first
	            //then display upgrade part first,
	            //then additional software part
	            result = compareString(getAppliancePartTypeStrValue(item1), getAppliancePartTypeStrValue(item2));
	            if (result != 0) {
	                return result;
	            }
            }
            //then if part type is same, sort by serial number and machine type
        	result = compareString(item1.getSerialNumber(), item2.getSerialNumber());
        	if (result != 0) {
                return result;
            }
        	result = compareString(item1.getMachineType(), item2.getMachineType());
        	if (result != 0) {
                return result;
            }
        	result = compareString(item1.getModel(), item2.getModel());
        	if (result != 0) {
                return result;
            }
        }

        return 0;
    }

    protected static String convertNulltoEmpty(String s) {
        if (null == s) {
            return "";
        }
        return s;
    }

    protected static boolean isRenewalQuote(QuoteLineItem item) {
        return (null != item.getRenewalQuoteNum()) && (!"".equalsIgnoreCase(item.getRenewalQuoteNum()));
    }

    protected String getPartTypeStrValue(QuoteLineItem item){
    	//if part is normal software part
    	if(!item.isApplncPart() && !item.isSaasPart() && !item.isMonthlySoftwarePart()){
    		return "1";
    	}
    	if(item.isApplncPart()){
    		return "2";
    	}
    	if(item.isMonthlySoftwarePart()){
    		return "4";
    	}
    	if(item.isSaasPart()){
    		return "5";
    	}
    	return "0";
    }

    //get appliance id string value
    protected String getApplianceIdStrValue(QuoteLineItem item){
    	if(StringUtils.isBlank(item.getApplianceId())){
    		return "ZZZZZZZZ";
    	}
    	if(PartPriceConstants.APPLNC_NOT_ON_QUOTE.equalsIgnoreCase(item.getApplianceId())){
    		return "ZZZZZZZY";
    	}
    	if(PartPriceConstants.APPLNC_NOT_ASSOCIATED.equalsIgnoreCase(item.getApplianceId())){
    		return "ZZZZZZZX";
    	}
    	if (item.isApplncMainGroup()){
    		return "AAAAAAAA";
    	}
    	return item.getApplianceId();
    }

    protected String getAppliancePartTypeStrValue(QuoteLineItem item){
    	if(item.isApplncMain()){
    		return "1";
    	}else if(item.isApplncUpgrade()){
    		return "2";
    	}else if(item.isApplncServicePack()){
    		return "3";
    	}else if(item.isApplncServicePackRenewal()){
    		return "4";
    	}else if(item.isApplncTransceiver()){
    		return "5";
    	}else if(item.isApplncRenewal()){
    		return "6";
    	}else if(item.isApplianceRelatedSoftware()){
    		return "7";
    	}else if(item.isApplncReinstatement()){
    		return "8";
    	}else{
    		return "9";
    	}
    }

}
