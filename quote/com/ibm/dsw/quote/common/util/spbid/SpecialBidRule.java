package com.ibm.dsw.quote.common.util.spbid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.domain.EmployeeDlgtnDiscountFactory;
import com.ibm.dsw.quote.common.domain.EmployeeDlgtnForSbFactory;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.PartsPricingConfigurationFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.domain.SpecialBidReason;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcess;
import com.ibm.dsw.quote.draftquote.process.SpecialBidProcessFactory;
import com.ibm.dsw.quote.draftquote.util.TuscanySWVNRuleHelper;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>SpecialBidRule<code> class.
 *    
 * @author: liuxinlx@cn.ibm.com
 * 
 * Creation date: Feb 25, 2008
 * 
 * $Log: SpecialBidRule.java,v $
 * Revision 1.79 2011/06/29 Fred
 * Add logic for special bid for CPQ Exception
 * 
 * Revision 1.78  2011/02/25 07:47:11  jhma
 * ebiz : DEVS-8CUJNJ : Special Bid not triggered although end date is not Anniversary, RTC#76904
 * Update special bid message .
 * Will is reviewer
 *
 * Revision 1.77  2011/02/24 13:30:49  xiaogy
 * ebiz : DEVS-8CUJNJ : Special Bid not triggered although end date is not Anniversary, RTC#76904
 *
 * Revision 1.76  2011/01/19 03:36:47  xiaogy
 * Merge code for ebiz : DEVS-8D2DWF : Fixed Term Licence,  RTC#77642
 * Reviewed by Edward
 *
 * Revision 1.75  2010/12/29 07:30:44  jhma
 * In order to resolve 'Serializable'exception ,update some codes:
 * 1.update SpecialBidRule.java to implement  Serializable
 * 2.update RenewalQuoteSpecialBidRule.java and SalesQuoteSpecialBidRule.java to cancel implement Serializable
 *
 *  Will is the reviewer.
 * Below is the exception message:
 * [12/17/10 3:44:04:461 EST] 0000025d SessionContex E   Exception is: java.io.InvalidClassException: com.ibm.dsw.quote.common.util.spbid.SalesQuoteSpecialBidRule; no valid constructor
 *         at java.io.ObjectStreamClass.<init>(ObjectStreamClass.java(Compiled Code))
 *         at java.io.ObjectStreamClass.lookup(ObjectStreamClass.java(Compiled Code))
 *         at java.io.ObjectOutputStream.writeObject0(ObjectOutputStream.java(Compiled Code))
 *         at java.io.ObjectOutputStream.defaultWriteFields(ObjectOutputStream.java(Inlined Compiled Code))
 *         at java.io.ObjectOutputStream.writeSerialData(ObjectOutputStream.java(Compiled Code))
 *         at java.io.ObjectOutputStream.writeOrdinaryObject(ObjectOutputStream.java(Compiled Code))
 *         at java.io.ObjectOutputStream.writeObject0(ObjectOutputStream.java(Compiled Code))
 *
 * Revision 1.74  2010/10/28 08:27:55  wxiaoli
 * ebiz help request : KAME-8A8L8F: SPSS Rep Delegation is not working, RTC 48607
 *
 * Revision 1.73  2010/10/22 02:48:18  wxiaoli
 * ebiz help request : KAME-8A8L8F: SPSS Rep Delegation is not working, RTC 48607
 *
 * Revision 1.72  2010/08/17 08:34:22  qinfengc
 * RTC 3135, added payment terms > 30 days and/or validity days > 30 days, special bid reason code.
 * Reviewed by Will.
 *
 * Revision 1.71  2010/08/17 05:48:54  changwei
 * RTC 3135, added payment terms > 30 days and/or validity days > 30 days, special bid reason code. Reviewed by Fred.
 *
 * Revision 1.70  2010/08/02 03:27:33  changwei
 * Task 3701 :  CLP : DSQ04 : Display current draft sales quote special bid tab
 *
 * The quote should not be flagged as special bid and should not route for approvals.
 *
 * Revision 1.69  2010/07/15 07:00:57  changwei
 * RTC 3034, 3307: Prc Inc after approval
 * Do not reset the flag for copy for price increase to fix the issue: approval info not display in status tab
 * Reviewed by Vivian.
 *
 * Revision 1.68  2010/07/01 10:00:57  changwei
 * fix PL DBOT-86UJMK, provide isTriggerTC method for special bid validation
 *
 * Revision 1.67  2010/06/11 05:44:17  wxiaoli
 * rollback code for CLP : PSQ02:  Post current draft sales quote parts & pricing tab, RTC 3225
 *
 * Revision 1.66  2010/06/10 03:32:59  wxiaoli
 * Convergence : ebiz help request : JKEY-867TTH : Remove reseller authorization logic for OEM, RTC 3353, reviewed by Will
 *
 * Revision 1.65  2010/05/27 07:45:52  wxiaoli
 * contract level prcing flag for Display parts & pricing tab, RTC 3211,3212,3213,3214,3215,3216,3217, reviewed by Will
 *
 */

public abstract class SpecialBidRule implements Serializable{

	private static final long serialVersionUID = 5681602035566729819L;

	protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    protected Quote quote;

    //private boolean isSpecialBid;
    // below fields are used by special bid tab which need know the exact factor
    // causes special bid
    private boolean isDiscountSpecificed = false;

    private boolean isDiscountWithNoDelegation = false;

    private boolean isDiscountOverDelegation = false;

    private boolean isDiscountBelowDelegation = false;

    private boolean isOverridePriceSpecificed = false;

    private boolean isPartGroupSpid = false;

    private boolean isPartHasOvrExtPrc = false;

    private boolean isPartRestrict = false;

    private boolean maintOverDefaultPeriod = false;

    private boolean hasOfferPrice = false;

    private boolean isResellerAuthorizedToPortfolio = false;
    
    private boolean isQuoteBackDated = false;

    //Add on 5/15/2008
    private boolean approvalRouteFlag = true;

    private boolean isChannelDiscountOverriden = false;
    
    private boolean rqStatusRequiresSB = false;
    
    private boolean hasEOLPart = false;
    
    private boolean isDateChangeTriggerSB = false;
    
    private boolean isCmprssCvrageQuote = false;
    
    private boolean isPaymentSB = false;
    
    private boolean isCpqExcep = false;
    
    private boolean isConfgrtnOvrrdn = false;

    //Add on 12/10/2012
    private boolean exportRestricted = false;
    
    //Add on 19/02/2013 for 10.4 & 10.6
    private boolean provisngDaysChanged  = false;
    
    private boolean isPartialRenewalForItemOmitted = false;
    
    private boolean isPartialRenewalForQuantityLowered = false;
    
    private boolean isCSAAmendment = false;
    //Add on  11/4/2014 for 14.3
    private boolean  isGridDelegationFlag=false; 
    //Add on 19/1/2015 for 15.2
    private boolean isDateExtended = false;
    //Add on 10/4/2015 for 15.3
    private boolean isTrmGrt60Mon = false;
    
    private static final int TERM_GREATER_MONTHS = 60;
    
    public SpecialBidRule(Quote quote) {
        this.quote = quote;

    }

    /**
     * @param quote
     *  make sure the following datas are set
     * 1. quote.quoteHeader
     * 2. quote.lineItemList
     * 3. quote.reseller(is fulfillment source is channel)
     * @return
     */
    public static SpecialBidRule create(Quote quote) {
    	if(logContext.isDebug()){
    		checkDataRequired(quote);
    	}
    	if(quote.getSpecialBidInfo()==null){
    		//for compute whether current quote is a grid delegation quote or not 
            SpecialBidInfo specailBidInfo=null;
			try {
				SpecialBidProcess specialBidProcess = SpecialBidProcessFactory.singleton().create();
	             specailBidInfo = specialBidProcess.getSpecialBidInfo(quote.getQuoteHeader().getWebQuoteNum());
			} catch (QuoteException e) {
				e.printStackTrace();
			} 
            quote.setSpecialBidInfo(specailBidInfo);
    	}
        if (quote.getQuoteHeader().isSalesQuote()) {
            return new SalesQuoteSpecialBidRule(quote);
        } else if (quote.getQuoteHeader().isRenewalQuote()) {
            return new RenewalQuoteSpecialBidRule(quote);
        }
        logContext.info(SpecialBidRule.class, "the quote is not sales or renewal ");
        return null;
    }
    
    private static void checkDataRequired(Quote quote){
    	String className = SpecialBidRule.class.getName();
    	QuoteHeader header = quote.getQuoteHeader();
    	
    	if(header == null){
    		logContext.debug(className, "SpecialBidRule Note: quote header is null");
    	}
    	
    	if(quote.getLineItemList() == null || quote.getLineItemList().isEmpty()){
    		logContext.debug(className, "SpecialBidRule Note: quote line item list is null or empty");
    	}
    	
    	if(quote.getCustomer()==null){
    		logContext.debug(className, "SpecialBidRule Note: quote  customer is null");
    	}
    	if(quote.getSpecialBidInfo()==null){
    		//for compute whether current quote is a grid delegation quote or not 
    		logContext.debug(className, "SpecialBidRule Note: quote  special bid inot is null");
    	}
    	if(QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(header.getFulfillmentSrc())
				&& quote.getReseller() == null) {
    		logContext.debug(className, 
    				               "SpecialBidRule Note: quote fulfilment source is channel, reseller TBD is "
    				               + header.isResellerToBeDtrmndFlag() + ", reseller is null");
    	}
    }

    public abstract boolean checkSpecialBidForMaintConverage();

    public void checkSpecialBidForEmployeeDlgtnDisc(String userId) {
        String rsAndMaxDisc = getRsAndMaxDiscountString();
        logContext.debug(this,"Revenue stream code and discount:"+rsAndMaxDisc);
        if (!"".equals(rsAndMaxDisc)) {
            int[] result = null;
            try {
                // RMUY-7FKECG : Check delegation if a sold-to is selected
                // Use the pricing_Country_code to do the check per emp. delegation data
                if (StringUtils.isNotBlank(quote.getQuoteHeader().getSoldToCustNum())
                        || quote.getQuoteHeader().hasNewCustomer()) {
                    result = EmployeeDlgtnDiscountFactory.singleton().checkEmpDlgtnDisc(quote.getQuoteHeader(),
                            rsAndMaxDisc);
                    if(!quote.getQuoteHeader().getCreatorId().equals(userId)){
                        if(result[0] == 0 && result[1] != 1){
                            result = EmployeeDlgtnDiscountFactory.singleton().checkEmpDlgtnDiscByCrrntUser(quote.getQuoteHeader(),
                                    rsAndMaxDisc, userId);
                        }
                    }
                    logContext.debug(this, "need check employee delegation discount in db2.");
                } else {
                    result = new int[] { 4, 1 };
                }
            } catch (TopazException e) {
                logContext.debug(this, e.getMessage());
            }

            //if has record
            if (result[0] == 0) {
                if (result[1] == 1) {
                    
                    logContext.debug(this,"discount is over delegation");
                    this.isDiscountOverDelegation = true;
                    
                } else {
                    
                    this.isDiscountBelowDelegation = true;
                    logContext.debug(this,"discount is below delegation");
                    
                }

                
            } else if (result[0] == 4) {
                //if no record, check configuration file
            	this.isDiscountWithNoDelegation = true;
                
            }
        }
    }
    

    public String getRsAndMaxDiscountString() {
        List items = quote.getLineItemList();
        Map rsMaxDiscMap = new HashMap();

        for (int i = 0; i < items.size(); ++i) {
            QuoteLineItem item = (QuoteLineItem) items.get(i);
            double lineDiscPct = item.getLineDiscPct();
            String revnStrmCode = item.getRevnStrmCode();
            Integer qty = item.getPartQty();
            if ((qty != null) && (qty.intValue() == 0)) {
                continue;
            }
            if (StringUtils.isBlank(revnStrmCode)) {
                continue;
            }
            
            //ignore discount caused by reset line item price to rsvp/srp
            if(item.isSetLineToRsvpSrpFlag()){
            	continue;
            }
            
            if (DecimalUtil.isNotEqual(0.0d, lineDiscPct)) {
            	
            	this.isDiscountSpecificed = EmployeeDlgtnForSbFactory.singleton().isForceSB(quote.getQuoteHeader());
                logContext.debug(this, "isDiscountSpecificed=" + this.isDiscountSpecificed);
                
                Double discount = (Double) rsMaxDiscMap.get(revnStrmCode);
                if (null == discount || lineDiscPct > discount.doubleValue()) {
                    rsMaxDiscMap.put(revnStrmCode, new Double(lineDiscPct));
                }
            }
        }

        String rsAndMaxDisc = "";
        if (rsMaxDiscMap.size() != 0) {
            Iterator it = rsMaxDiscMap.keySet().iterator();
            while (it.hasNext()) {
                String rs = (String) it.next();
                Double discPct = (Double) rsMaxDiscMap.get(rs);
                rsAndMaxDisc += rs + "|" + discPct.toString() + ",";
            }
            rsAndMaxDisc = rsAndMaxDisc.substring(0, rsAndMaxDisc.length() - 1);
        }
        return rsAndMaxDisc;
    }
    
    protected abstract boolean skip();
    
    

    public boolean validate(String userId) throws QuoteException  {
       
        if(skip()){
            return quote.getQuoteHeader().getSpeclBidFlag() == 1;
        }
        logContext.debug(this,"Approval Flg before validation :"+quote.getQuoteHeader().getApprovalRouteFlag());

        if (QuoteConstants.LOB_PPSS.equals(this.quote.getQuoteHeader().getLob().getCode())) {
            return false;
        }

        if (quote.getQuoteHeader().getOfferPrice() != null) {
            logContext.debug(this, "quote has offer price");
            this.hasOfferPrice = true;
        }
        
        if(quote.getQuoteHeader().isGridFlag()){
        	this.isGridDelegationFlag = true;
        }

        if (checkSpecialBidForMaintConverage()) {
            this.maintOverDefaultPeriod = true;
            logContext.debug(this, "Maint converage need spbid ");
        }

        checkSpecialBidForEmployeeDlgtnDisc(userId);
        
        validatePartialRenewal();
        
        String agreementCode = quote.getQuoteHeader().getAgrmtTypeCode();
        
        List items = quote.getLineItemList();
        
        for (int i = 0; i < items.size(); i++) {

            QuoteLineItem item = (QuoteLineItem) items.get(i);

            logContext.debug(this, "Begin to validate the special bid for  " + item.getPartNum() + ","
                    + item.getSeqNum());

            if ((item.getPartQty() != null) && (item.getPartQty().intValue() == 0)) {
                logContext.debug(this, item.getPartNum() + "'s qty is 0, ignore the validation");
                continue;
            }
            
            //ignore discount caused by reset line item price to rsvp/srp
            if (item.getOverrideUnitPrc() != null && !item.isSetLineToRsvpSrpFlag()) {
                logContext.debug(this, "isOverridePriceSpecificed");

                this.isOverridePriceSpecificed = true;
            }

            if (item.isPartGroupRequireSpBid()) {

                logContext.debug(this, "isPartGroupSpid");
                this.isPartGroupSpid = true;
            }

            if (item.getOvrrdExtPrice() != null) {
                this.isPartHasOvrExtPrc = true;
            }

            if (item.isPartRestrct()) {
                logContext.debug(this, "part restricted");
                if (!quote.getQuoteHeader().isFCTQuote()) {
                    this.isPartRestrict = true;
                }
            }
            
            if((item.getChnlOvrrdDiscPct() != null) && (item.getChnlOvrrdDiscPct().doubleValue()>0)){
                logContext.debug(this, "line item: " + item.getPartNum() + "-" + item.getSeqNum() + " has chnnel discount overriden");
            	this.isChannelDiscountOverriden = true;
            }
            
            if(item.isObsoletePart()){
            	this.hasEOLPart = true;
            	logContext.debug(this, "line item: " + item.getPartNum() + "-" + item.getSeqNum() + " is EOL part");
            }
            
            if(item.isExportRestricted()){
            	this.exportRestricted = true;
            	logContext.debug(this, "line item: " + item.getPartNum() + "-" + item.getSeqNum() + " is export restricted");
            }
            //If any line item alreay trigger s.b due to coverage date length, then skip                   
            if(!isDateChangeTriggerSB && doesDateChangeTriggerSB(item)){
                
                this.isDateChangeTriggerSB = true;
                logContext.debug(this, "line item: " + item.getPartNum() + "-" + item.getSeqNum() + " date change trigger special bid.");
            }
            
            Boolean hdrFlag = item.getHdrAgrmentAmdFlag();
    		if(null != hdrFlag && hdrFlag.booleanValue() 
    				&& (QuoteConstants.LOB_CSRA.equals(agreementCode) || QuoteConstants.LOB_CSTA.equals(agreementCode))){
    			this.isCSAAmendment = true;
    		}
    		
    		if (item.getICvrageTerm() != null && item.getICvrageTerm() > TERM_GREATER_MONTHS) {
            	this.isTrmGrt60Mon = true;
            }
        }
        
        rqStatusRequiresSB = rnwlQuoteStatusRequiresSpecialBid(userId);
        
        
        this.isResellerAuthorizedToPortfolio = resellerAuthorizedToPortfolio();
        logContext.debug(this, "isResellerAuthorizedToPortfolio=="+this.isResellerAuthorizedToPortfolio);
        QuoteHeader header = quote.getQuoteHeader();
        
        if(!header.isRenewalQuote()){
        	BackDatingSpecialBidChecker checker = new BackDatingSpecialBidChecker(quote);
        	if(checker.execute()){
        		this.isQuoteBackDated = true;
        		logContext.debug(this, "Quote has back dated items and isQuoteBackDated is true");
        	}
        }
        
        if(header.getCmprssCvrageFlag()){
            this.isCmprssCvrageQuote = true;
        }
        
        if(header.isTriggerTC()){
            this.isPaymentSB = true;
        }
        
        //Add logic for set cpq exception flag
        this.isCpqExcep = checkCpqExcep(quote.getQuoteHeader().getWebQuoteNum());
        
        this.isConfgrtnOvrrdn = checkConfgrtnOvrrdn(quote);
        
        this.provisngDaysChanged = checkProvisngDaysChanged(quote.getQuoteHeader().getWebQuoteNum());
        
        this.isDateExtended = quote.getQuoteHeader().isExpDateExtendedFlag();

        
        boolean sysInitSbFlag = getSysInitSbFlag();
        boolean manualInitSbFlag = (header.getSpeclBidManualInitFlg() == 1);
        // set the grid  delegation flag

        logContext.debug(this,
				"Grid delegation Flag is "+ this.isGridDelegationFlag);
        // set the approval routing flag
        
        if(manualInitSbFlag){
            
            logContext.debug(this,"User force special bid, the routing flag must be true");
            this.approvalRouteFlag = true;
            
        } else{
            
            if(sysInitSbFlag){
                
                if (needRoutingToApproval()){      
                    logContext.debug(this,"need routing to approval");
                    this.approvalRouteFlag = true;
                    
                } else{
                    
                    logContext.debug(this,"User didn't manual set SB, FCT/FCTToPA quote,  discount is within delegation, and no other reasons trigger sb, so approval routing flag is flase");
                    this.approvalRouteFlag = false;
                }
            } else{
                // not special bid
                logContext.debug(this,"both sys init flag and manual sb flag are false ,so approval routing flag is flase");
                this.approvalRouteFlag = false;
            }
        }
        
        //Set special bid flag for draft quotes only 
        //Do NOT set flag for copying for price increase quote 
        if(shouldResetSpecialBidFlag()){
	        header.setSpeclBidSystemInitFlag(sysInitSbFlag? 1 : 0);
	        header.setSpeclBidFlag((sysInitSbFlag || manualInitSbFlag)? 1 : 0);
	        header.setApprovalRouteFlag(approvalRouteFlag? 1 : 0);
        }
        
        return sysInitSbFlag;
        
        
    }
    
    protected boolean checkCpqExcep(String webQuoteNum)
    {
    	try
    	{
    		return SpecialBidProcessFactory.singleton().create().checkCpqExcepCode(webQuoteNum);
    	}
    	catch ( QuoteException e )
    	{
    		logContext.error(this, e.getMessage());
    		return false;
    	}
		
    }
    
    protected boolean checkConfgrtnOvrrdn(Quote quote){
    	List confgrtnList = new ArrayList();
		try {
			confgrtnList = PartsPricingConfigurationFactory.singleton().findPartsPricingConfiguration(quote.getQuoteHeader().getWebQuoteNum());
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
        }
    	for (Iterator iterator = confgrtnList.iterator(); iterator.hasNext();) {
			PartsPricingConfiguration confgrtn = (PartsPricingConfiguration) iterator.next();
			if(confgrtn.isConfigrtnOvrrdn()){
				return true;
			}
		}
    	return false;
    }
    
    public boolean checkProvisngDaysChanged(String webQuoteNum){
    	List confgrtnList = new ArrayList();
		try {
			confgrtnList = PartsPricingConfigurationFactory.singleton().findPartsPricingConfiguration(quote.getQuoteHeader().getWebQuoteNum());
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
        }
    	for (Iterator iterator = confgrtnList.iterator(); iterator.hasNext();) {
			PartsPricingConfiguration confgrtn = (PartsPricingConfiguration) iterator.next();
			if(confgrtn.isTermExtension()){
				int provisngDaysDefault = confgrtn.getProvisngDaysDefault() == null ? 0 : confgrtn.getProvisngDaysDefault().intValue();
				int provisngDays = confgrtn.getProvisngDays() == null ? 0 : confgrtn.getProvisngDays().intValue();
				if(provisngDaysDefault != provisngDays){
					return true;
				}
			}
		}
    	return false;
    }
    
    private boolean shouldResetSpecialBidFlag(){
        QuoteHeader header = quote.getQuoteHeader();
        return !(header.isSubmittedQuote() || header.isCopied4PrcIncrQuoteFlag());
    }
    
    protected abstract boolean rnwlQuoteStatusRequiresSpecialBid(String userId);
    
    public SpecialBidReason getSpecialBidReason(){
    	SpecialBidReason sbReason = new SpecialBidReason();
    	QuoteHeader header = quote.getQuoteHeader();
    	
    	if(header.getSpeclBidFlag() == 1){
    		if(header.getApprovalRouteFlag() == 1){
    			if(this.isDateExtended){
					sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.EXPIRATION_DATE_EXTENDED);
				}
					
    			if(isCSAAmendment()){
        			sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.TERMS_CONDITIONS_CHANGE);
        		}
		    	if ((isDiscountSpecificed() || isOverridePriceSpecificed())
		    	        && (isDiscountWithNoDelegation() || isDiscountOverDelegation())) {
					if (isDiscountWithNoDelegation()) {
						boolean manualDisc = false;
						boolean ytyDisc = false;
						for(QuoteLineItem item : (List<QuoteLineItem>) quote.getLineItemList()){
							if(manualDisc && ytyDisc){
								break;
							}
							
				            //ignore discount caused by reset line item price to rsvp/srp
				            if(item.isSetLineToRsvpSrpFlag()){
				            	continue;
				            }
							
							//yty results in a special bid
							if(DecimalUtil.isNotEqual(0.0d, item.getLineDiscPct())
									&& item.getYtyGrowth() != null
									&& item.getYtyGrowth().getYtyGrwothRadio().equals("3")
									&& !ytyDisc){
								ytyDisc = true;
								sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.YTY_DISCOUNT_NO_DELEGATION);
							}
							
							//manual discount/price results in a special bid
							if((DecimalUtil.isNotEqual(0.0d, item.getLineDiscPct()) || item.getOverrideUnitPrc() != null)
									&& (item.getYtyGrowth() == null || !item.getYtyGrowth().getYtyGrwothRadio().equals("3"))
									&& !manualDisc){
								manualDisc = true;
								sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.MANUAL_DISCOUNT_NO_DELEGATION);
							}
						}
						//sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.DISCOUNT_NO_DELEGATION);
					} else if (isDiscountOverDelegation()) {
						sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.DISCOUNT_OVER_DELEGATION);
					}
				}
		
				if (isMaintOverDefaultPeriod()) {
					sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.MAINT_OVER_DEFAULT_PERIOD);
				}
				
				if (isPartRestrict()) {
					sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.PART_RESTRICT);
				}
				
				if (isPartGroupSpid()) {
					sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.PART_GROUP_REQUIRE_SPECIAL_BID);
				}
		
				if (isQuoteBackDated()) {
					sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.LINE_ITEM_BACK_DATED);
				}
				
				CodeDescObj lob = quote.getQuoteHeader().getLob();
				if (QuoteConstants.LOB_PA.equals(lob.getCode())
						|| QuoteConstants.LOB_PAE.equals(lob.getCode())
						) {
					if (!isResellerAuthorizedToPortfolio()) {
						sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.RESELLER_NOT_AUTHORIZE_TO_PORTFOLIO);
					}
					if (isChannelDiscountOverriden()) {
						sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.CHANNEL_DISCOUNT_OVERRIDEN);
					}
				}
				
				if(hasEOLPart()){
					sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.HAS_EOL_PART);
				}
				
				if(isRqStatusRequiresSB()){
					sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.RQ_STATUS_REQUIRE_SB);
				}

				if(isDateChangeTriggerSB){
				    sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.COVERAGE_LESS_ONE_YEAR_SB);
				}
				
				if(isCmprssCvrageQuote()){
				    sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.CMPRSS_CVRAGE_QUOTE);
				}
				
				if ( this.isCpqExcep() ){
					sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.CPQ_EXCEP);
				}
				
				if(this.isConfgrtnOvrrdn()){
					sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.CONFGRTN_OVRRDN);
				}
				
				if(this.isPartialRenewalForItemOmitted()){
					sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.PARTIAL_RENEWAL_ITEM_OMITTED);
				}
				
				if(this.isPartialRenewalForQuantityLowered()){
					sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.PARTIAL_RENEWAL_QUANTITY_LOWERED);
				}
				
				if(this.isProvisngDaysChanged()){
					sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.PROVISNGDAYS_CHANGED);
				}
				
				if(this.isExportRestricted()){
					sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.EXPORT_RESTRICTED);
				}
				
				if(this.isTrmGrt60Mon){
					sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.TERM_GREATER_THAN_60_MONTHS);
				}
				
				
    		} else if (header.getApprovalRouteFlag() == 0){
    			
    			if((isDiscountSpecificed() || isOverridePriceSpecificed())
    					&& !isDiscountWithNoDelegation()
						&& isDiscountBelowDelegation()){
    				sbReason.addNoApprovalReason();
    				
    			}else if(isGridDelegationFlag){
    				sbReason.addNoApprovalReasonOfGrid();
    			}
    		}
    	}
    	
    	if(checkEMEADiscount()){
    		sbReason.addEMEADiscountReason();
    	}
    	
    	if(isPaymentSB()){
		    sbReason.addSpecialBidReason(QuoteConstants.SpecialBidReason.PAYMENT_TERMS_AND_VALIDITY_DAYS);
		}
    	
    	if(logContext instanceof QuoteLogContextLog4JImpl){
			if(((QuoteLogContextLog4JImpl)logContext).isDebug(this)){		
				logContext.debug(this, sbReason.toString());
			}
		}
    	
    	quote.setSpecialBidReason(sbReason);
    	
		return sbReason;
	}
    
    private boolean checkEMEADiscount() {
        
    	List list = quote.getLineItemList();
    	if(list == null || list.size() == 0){
    		return false;
    	}
        Country country = quote.getQuoteHeader().getCountry();
        String geo = country.getSpecialBidAreaCode();
        if (QuoteConstants.GEO_EMEA.equals(geo)) {
        	for(Iterator it = list.iterator(); it.hasNext(); ){
        		QuoteLineItem item = (QuoteLineItem)it.next();
        		if(item.getLineDiscPct() > 85.0){
        			return true;
        		}
        	}
        }
        
        return false;
    }
    
    boolean needRoutingToApproval(){
        //for GRID delegation
        if(this.isGridDelegationFlag()){
        	 //return false represent that the quote will bypass the approvals
			 return false;
        }
        
        QuoteHeader header = quote.getQuoteHeader();
        //for FCT and FCT TO PA
        if(header.isFCTQuote() || (header.isFCTToPAQuote())){
            // there are other reasons (besides discount within delegation) which triggered the special bid
            if (this.maintOverDefaultPeriod || this.isDiscountWithNoDelegation
                    || this.isDiscountOverDelegation  || this.isOverridePriceSpecificed || this.isPartGroupSpid
                    || this.isPartRestrict || !this.isResellerAuthorizedToPortfolio
                    || this.isQuoteBackDated || this.isChannelDiscountOverriden || this.rqStatusRequiresSB
                    || this.hasEOLPart || isDateChangeTriggerSB || isCmprssCvrageQuote
                    || this.isPaymentSB || this.isCpqExcep
                    || this.isConfgrtnOvrrdn || this.isExportRestricted()
                    || this.isPartialRenewalForItemOmitted() || this.isPartialRenewalForQuantityLowered()
                    || this.isProvisngDaysChanged()
                    || this.isCSAAmendment || this.isDateExtended || this.isTrmGrt60Mon) {
                return true;
            }
            else{
                return false;
            }
        } else {
            return true;
        }
    }
    //for justify grid 
    private boolean justOverDelegation(){
    	if ((this.isDiscountOverDelegation || this.isDiscountWithNoDelegation || this.isOverridePriceSpecificed) && !this.maintOverDefaultPeriod 
   			 && !this.isPartGroupSpid
   			 && ! this.isPartRestrict && this.isResellerAuthorizedToPortfolio
   			 && ! this.isQuoteBackDated &&  !this.isChannelDiscountOverriden && !this.rqStatusRequiresSB
   			 && ! this.hasEOLPart && !isDateChangeTriggerSB && !isCmprssCvrageQuote
   			 && ! this.isPaymentSB && !this.isCpqExcep
   			 && ! this.isConfgrtnOvrrdn && ! this.isExportRestricted()
                && ! this.isPartialRenewalForItemOmitted() && !this.isPartialRenewalForQuantityLowered()
                && ! this.isProvisngDaysChanged()
                && ! this.isCSAAmendment && !this.isDateExtended || !this.isTrmGrt60Mon) {
    		return true;
    	}else{
    		return false;
    	}
    }
    private boolean getSysInitSbFlag(){
        
        if (this.hasOfferPrice || this.maintOverDefaultPeriod 
                || ((this.isDiscountSpecificed || this.isOverridePriceSpecificed) && (this.isDiscountOverDelegation || this.isDiscountWithNoDelegation) )
                || this.isPartGroupSpid
                || this.isPartHasOvrExtPrc || this.isPartRestrict || !this.isResellerAuthorizedToPortfolio
				|| this.isQuoteBackDated || this.isChannelDiscountOverriden
				|| this.rqStatusRequiresSB || this.hasEOLPart
				|| isDateChangeTriggerSB || isCmprssCvrageQuote
				|| this.isPaymentSB || this.isCpqExcep
				|| this.isConfgrtnOvrrdn || this.isPartialRenewalForItemOmitted() || this.isPartialRenewalForQuantityLowered()
				|| isExportRestricted()
				|| this.isProvisngDaysChanged()
				|| this.isCSAAmendment || this.isDateExtended || this.isTrmGrt60Mon) {
            return true;
        }

        return false;
    }
 

    protected HashMap groupByPartNum(List items) {

        HashMap map = new HashMap();

        for (int i = 0; i < items.size(); i++) {

            QuoteLineItem item = (QuoteLineItem) items.get(i);
            if ((item.getMaintStartDate() == null) || (item.getMaintEndDate() == null)) {
                continue;
            }
            String partNum = item.getPartNum();
            List l = (List) map.get(partNum);
            if (null == l) {
                l = new ArrayList();
                map.put(partNum, l);
            }
            l.add(item);

        }

        return map;
    }

    /**
     * @return Returns the isDiscountSpecificed.
     */
    public boolean isDiscountSpecificed() {
        return isDiscountSpecificed;
    }

    /**
     * @return Returns the isOverridePriceSpecificed.
     */
    public boolean isOverridePriceSpecificed() {
        return isOverridePriceSpecificed;
    }

    /**
     * @return Returns the isPartGroupSpid.
     */
    public boolean isPartGroupSpid() {
        return isPartGroupSpid;
    }

    /**
     * @return Returns the isPartHasOvrExtPrc.
     */
    public boolean isPartHasOvrExtPrc() {
        return isPartHasOvrExtPrc;
    }

    /**
     * @return Returns the isPartRestrict.
     */
    public boolean isPartRestrict() {
        return isPartRestrict;
    }

    /**
     * @return Returns the isResellerNotAuthorizedToPortfolio.
     */
    public boolean isResellerAuthorizedToPortfolio() {
        return isResellerAuthorizedToPortfolio;
    }

    /**
     * @return Returns the maintOverDefaultPeriod.
     */
    public boolean isMaintOverDefaultPeriod() {
        return maintOverDefaultPeriod;
    }

    public boolean hasOfferPrice() {
        return this.hasOfferPrice;
    }

    /**
     * @return Returns the isDiscountOverDelegation.
     */
    public boolean isDiscountOverDelegation() {
        return isDiscountOverDelegation;
    }

    /**
     * @return Returns the isApprovalRouteFlag.
     */
    public boolean isApprovalRouteFlag() {
        return approvalRouteFlag;
    }

    //conveninent method for database storage
    public int getApprovalRouteFlagAsInt() {
        return isApprovalRouteFlag() ? 1 : 0;
    }
    /**
     * @return Returns the isDiscountBelowDelegation.
     */
    public boolean isDiscountBelowDelegation() {
        return isDiscountBelowDelegation;
    }
	/**
	 * @return Returns the isDiscountWithNoDelegation.
	 */
	public boolean isDiscountWithNoDelegation() {
		return isDiscountWithNoDelegation;
	}
	
	public boolean isQuoteBackDated(){
		return isQuoteBackDated;
	}

    /**
     * @return
     */
    public boolean isChannelDiscountOverriden() {
        
        return  this.isChannelDiscountOverriden ;
    }
    
    public boolean isRqStatusRequiresSB(){
    	return this.rqStatusRequiresSB;
    }
    
    public boolean hasEOLPart(){
    	return this.hasEOLPart;
    }
    
    public boolean isCSAAmendment(){
    	return this.isCSAAmendment;
    }
    
    public boolean isCmprssCvrageQuote(){
        return this.isCmprssCvrageQuote;
    }
    
    private boolean resellerAuthorizedToPortfolio() {
        boolean result = false;
        if ((!QuoteConstants.FULFILLMENT_CHANNEL.equalsIgnoreCase(this.quote.getQuoteHeader().getFulfillmentSrc()))
                || this.quote.getQuoteHeader().isRenewalQuote()) {
            return true;
        }
        CodeDescObj lob = quote.getQuoteHeader().getLob();
		if (!QuoteConstants.LOB_PA.equals(lob.getCode())
				&& !QuoteConstants.LOB_PAE.equals(lob.getCode())
				) {
		    return true;
		}

        try {
            
            TuscanySWVNRuleHelper helper = new TuscanySWVNRuleHelper(quote);
            HashMap reasons = new HashMap();
            result = helper.validateSpecialBid(reasons);
           
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
            
        	if(logContext instanceof QuoteLogContextLog4JImpl){
    			if(((QuoteLogContextLog4JImpl)logContext).isDebug(this)){		
    				   logContext.debug(this, "error in method resellerAuthorizedToPortfolio " + e);
    			}
    		}
        } 

        return result;

    }
    
    /**
     * implement new special bid rule: trigger sb when the coverage of renewal part is less than one year and not pro-rated to anniversary
     * @param item quote line item
     * @param header quote header
     * @return flag to trigger sb
     */
    private boolean doesDateChangeTriggerSB(QuoteLineItem item){        
        //non renewal part won't trigger sb
        if(!item.isRenewalPart()){
            return false;
        }

        if(item.isReinstatementPart()){
            return false;
        }
        
        if(!PartPriceConfigFactory.singleton().needDetermineDate(item.getRevnStrmCode())){
        	return false;
        }
        
        if(item.isApplncPart()){
        	return false;
        }
        
        if(StringUtils.isBlank(item.getRenewalQuoteNum())){
        	
        	if(PartPriceConfigFactory.singleton().shouldTriggerSBNoRQRef((quote.getQuoteHeader()))){

        		//check if the end date of item lines up to anniversary of customer
                if(!checkLineUp2Anniversary(item)){
                    return true;
                }
        	}
        } else {
        	//Line item is in reference to a renewal quote
        	if(PartPriceConfigFactory.singleton().shouldTriggerSBRQRef((quote.getQuoteHeader()))){
                //check if the end date of item are edited and not line up to anniversary of customer
                if(!checkLineUp2Anniversary(item)){
                    return true;
                }
        	}
        }
             
        return false;
    }
    
//    private boolean dateManualChanged(QuoteLineItem item){
//        return item.getStartDtOvrrdFlg() || item.getEndDtOvrrdFlg();
//    }
    /**
     * @return Returns the isDateChangeTriggerSB.
     */
    public boolean isDateChangeTriggerSB() {
        return isDateChangeTriggerSB;
    }
    
    /**
     * check if the end date of one item lines up to anniversary
     * @param item
     * @return
     */
    private boolean checkLineUp2Anniversary(QuoteLineItem item){
    	return QuoteCommonUtil.checkLineUp2Anniversary(quote, item);
    }

    /**
     * @return Returns the isPaymentSB.
     */
    public boolean isPaymentSB() {
        return isPaymentSB;
    }

	public boolean isCpqExcep() {
		return isCpqExcep;
	}
	public boolean isConfgrtnOvrrdn() {
		return isConfgrtnOvrrdn;
	}
	 public boolean isExportRestricted() {
			return exportRestricted;
	 }
	 
	 public boolean isProvisngDaysChanged(){
		 return provisngDaysChanged;
	 }
	 
	 public boolean isPartialRenewalForItemOmitted(){
		 return isPartialRenewalForItemOmitted;
	 }
	 
	 public boolean isPartialRenewalForQuantityLowered(){
		 return isPartialRenewalForQuantityLowered;
	 }
	 public boolean  isGridDelegationFlag(){
		 return isGridDelegationFlag;
	 }
	 public void validatePartialRenewal(){
		 String type = GrowthDelegationUtil.getQuoteGrwthDlgtnType(quote);
		 boolean grwthDltgnFlag = GrowthDelegationUtil.isGrwthDltgnQuote(type);
		 
		 isPartialRenewalForItemOmitted = grwthDltgnFlag && GrowthDelegationUtil.isPartialRenewalSpBidForItemOmitted(quote);
		 isPartialRenewalForQuantityLowered = grwthDltgnFlag && GrowthDelegationUtil.isPartialRenewalSpBidForQuantityLowered(quote);
	 }

	public boolean isDateExtended() {
		return isDateExtended;
	}
	
	public boolean isTrmGrt60Mon() {
		return isTrmGrt60Mon;
	}
	 
}
