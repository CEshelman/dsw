package com.ibm.dsw.quote.builder;

import is.domainx.User;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.ChargeAgreement;
import com.ibm.dsw.quote.common.domain.CoverageTermFactory;
import com.ibm.dsw.quote.common.domain.CoverageTermTypes;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.ServiceDateModType;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.GDPartsUtil;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartTableTotal;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

public abstract class QuoteBuilder {
	protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

	protected Quote quote;

    protected User user;

    protected String userID;

    public QuoteBuilder(Quote q, User user) {
        this.quote = q;
        this.user = user;
    }

    public QuoteBuilder(Quote q, String userID) {
        this.quote = q;
        this.userID = userID;
    }
	
	private Integer getRelatedExtLineItemNum(QuoteLineItem qli,List subSaaSlineItemList){
		TimeTracer tracer = TimeTracer.newInstance();
		Integer relatedExtLineItemNum = 0;
		Iterator subSaaSlineItemIt = subSaaSlineItemList.iterator();
		while(subSaaSlineItemIt.hasNext()){
			QuoteLineItem quoteLineItem = (QuoteLineItem) subSaaSlineItemIt.next();
			if(StringUtils.equals(qli.getPartNum(), quoteLineItem.getPartNum())){
				if(!StringUtils.isEmpty(quoteLineItem.getPartNum()) && !quoteLineItem.isReplacedPart()){
					relatedExtLineItemNum = quoteLineItem.getRelatedCotermLineItmNum();
				}
				break;
			}
		}
		tracer.dump();
		return relatedExtLineItemNum;
	}

    public void calculateCoverageTerm(Quote quote) throws TopazException, QuoteException {
		TimeTracer tracer = TimeTracer.newInstance();
		List SaaSLineItems = quote.getSaaSLineItems();
		if(SaaSLineItems == null || SaaSLineItems.size() == 0){
			return;
		}
		//If quote is a FCT to PA Finalization or Add on quote, need to calculate term and set quote line item per provisioning date.
		//Meanwhile, need to set up early renewal compensation date again.
		List configrtnsList = quote.getPartsPricingConfigrtnsList();
		if (configrtnsList == null || configrtnsList.size() == 0) {
			return;
		}
		Map configrtnsMap = quote.getPartsPricingConfigrtnsMap();
		Iterator configrtnsIt = configrtnsList.iterator();
		while(configrtnsIt.hasNext()){
			PartsPricingConfiguration ppc = (PartsPricingConfiguration) configrtnsIt.next();
			RedirectConfiguratorDataBasePack dataPack = null;
			CoverageTermTypes coTermTypes = CoverageTermFactory.singleton().getCoTermTypes(quote,ppc);
			boolean isTermExtension = ppc.isTermExtension();
			Date cotermEndDate = null;

            QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();

            // Notes://ltsgdb001b/85256B83004B1D94/1EE18F1E5F9468B085256A1700797288/F191F14EFD70BE4F85257B870077E754
            String configId = coTermTypes.getConfigId();
            String chrgAgrmtNum = quote.getQuoteHeader().getRefDocNum();
            ChargeAgreement ca = null;
            if (chrgAgrmtNum != null) {
                ca = quoteProcess.getChargeAgreementInfoWithoutTransaction(chrgAgrmtNum, configId);
            }

			if(isTermExtension && ppc.getServiceDateModType() != null && ServiceDateModType.CE.equals(ppc.getServiceDateModType())){
				cotermEndDate = ppc.getServiceDate();
				// co-term end date. move code to this can save end date
				ppc.setEndDate(cotermEndDate);
				if(this.user != null) {
					ppc.setUserID(this.user.getEmail());
				} else {
					ppc.setUserID(this.userID);
				}
			}else{
				// this is the rep first add/on or trade up and the ppc's end date is the initial CA line item end date.
				if(ppc.getServiceDate() == null){
					cotermEndDate = ppc.getEndDate();
				// this is the rep have modified service end date,the end date now is the modified end date,the end date should
				// reset from ChargeAgreement.
				}else{
                    if (chrgAgrmtNum != null) {
                        // if chrgAgrmtNum != null, then ca will not be null.
                        if (ca.getEndDate() != null) {
                            cotermEndDate = DateUtil.parseDate(DateUtil.formatDate(ca.getEndDate(), DateUtil.PATTERN));
                            ppc.setEndDate(cotermEndDate);
                            ppc.setServiceDate(null);
                            if (this.user != null) {
                                ppc.setUserID(this.user.getEmail());
                            } else {
                                ppc.setUserID(this.userID);
                            }
                        }
                    }
				}
			}

            if (!quote.getQuoteHeader().isSubmittedQuote()) {// Fix for the PL: SGOD-8YJNN5 : Getting error message when
                                                             // trying to view parts and price tab.
                dataPack = coTermTypes.calculateFinalizationTerm(cotermEndDate);

                List subSaaSlineItemList = (List) configrtnsMap.get(ppc);
                if (subSaaSlineItemList == null || subSaaSlineItemList.size() == 0) {
                    continue;
                }
                coTermTypes.setupTerm(subSaaSlineItemList, ca);
                coTermTypes.setupEarlyRewnewalCompensationDate(subSaaSlineItemList);
            } else {
                dataPack = coTermTypes.calculateFinalizationTerm(cotermEndDate);
                List subSaaSlineItemList = (List) configrtnsMap.get(ppc);
                if (subSaaSlineItemList == null || subSaaSlineItemList.size() == 0) {
                    continue;
                }
                coTermTypes.setupTermForSubmittedQuote(subSaaSlineItemList, ca);
            }
		}
		tracer.dump();
	}
	/**
	 * @param quote
	 * calculate configration's net increase and unused price
	 */
    @SuppressWarnings("rawtypes")
    public static void calculateIncreaseUnusedTCV(Quote quote, boolean isSubmittedQuote) {
		TimeTracer tracer = TimeTracer.newInstance();
		List configrtnList = quote.getPartsPricingConfigrtnsList();
		if(configrtnList == null || configrtnList.size() == 0){
			return;
		}
		for (Iterator iterator = configrtnList.iterator(); iterator.hasNext();) {
			PartsPricingConfiguration configrtn = (PartsPricingConfiguration) iterator.next();
			List SaasPartList = (List)quote.getPartsPricingConfigrtnsMap().get(configrtn);
			boolean hasRepacedPart = false;
			for (Iterator iterator2 = SaasPartList.iterator(); iterator2.hasNext();) {
				QuoteLineItem qli = (QuoteLineItem) iterator2.next();
				if(qli.isSaasSubscrptnPart() && qli.isReplacedPart()){
					hasRepacedPart = true;
					break;
				}
			}
			if(!hasRepacedPart){
				continue;
			}
			Integer remaningTerm = null;
			
            boolean isTermExtension = configrtn.isTermExtension();
			
            Integer masterPartTerm = null;

            for (Iterator iterator2 = SaasPartList.iterator(); iterator2.hasNext();) {
                QuoteLineItem qli = (QuoteLineItem) iterator2.next();
                if (qli.isSaasSubscrptnPart()) {
                    if (!qli.isReplacedPart()) {
                        masterPartTerm = qli.getICvrageTerm();
                        break;
                    }
                }
            }

			for (Iterator iterator2 = SaasPartList.iterator(); iterator2.hasNext();) {
				QuoteLineItem qli = (QuoteLineItem) iterator2.next();
				if (qli.isSaasSubscrptnPart()) {
                    if (qli.isReplacedPart()) {
                        /**
                         * if is NOT TermExtension, directly use ICvrageTerm of master line item:<br>
                         * refer to: CoverageTermTypes.setupTerm(List, ChargeAgreement)
                         */
                        if (!isTermExtension) {
                            remaningTerm = masterPartTerm;
                        }
                        /**
                         * if is TermExtension, use remainingTermTillCAEndDate<br>
                         * in fact, this variable means:<br>
                         * remainingTermTillCAEndDate: add on/trad up is being processed before the last month of the
                         * initial term<br>
                         * Or renewalTerm: add on/trad up is being processed in the last month of the initial term, and
                         * renewal term is not null<br>
                         * Or remainingRenewalTerm: add on/trad up is being processed after the end of initial term and
                         * in renewal term(initial term was over, renewal end date is in future)<br>
                         * 
                         * renewalTerm and remainingRenewalTerm, both come from ExtensionActiveService.getRemaningTerm()
                         */
                        else {
                            if (qli.getRemainingTermTillCAEndDate() != null) {
                                remaningTerm = qli.getRemainingTermTillCAEndDate();
                            }
                        }
						break;
					}
				}
			}
			
			if (!isSubmittedQuote) {
				calcValue(quote, configrtn, remaningTerm, SaasPartList);
			} else {
				// add tcv net change and tcv unused if it exists for 14.4
				if ("AddTrd".equals(configrtn.getConfigrtnActionCode())) {
					if (configrtn.getIncreaseBidTCV() != null && configrtn.getUnusedBidTCV() != null) {
						configrtn.setIncreaseBidTCV(configrtn.getIncreaseBidTCV());
						configrtn.setUnusedBidTCV(configrtn.getUnusedBidTCV());
					} else {
						//calculate as drafted quote
						calcValue(quote, configrtn, remaningTerm, SaasPartList);
					}
				}
			}
			
		}
		tracer.dump();
	}
	
    @SuppressWarnings("rawtypes")
    public static void calcValue(Quote quote, PartsPricingConfiguration configrtn, Integer masterPartTerms, List SaasPartList) {
		double unusedBidTCV = 0.0;
		double increaseBidTCV = 0.0;
		double totBidTCV = 0.0;
        PartTableTotal partTableTotalUtil = new PartTableTotal(quote);
        for (Iterator iterator = SaasPartList.iterator(); iterator.hasNext();) {
            QuoteLineItem qli = (QuoteLineItem) iterator.next();
            // TODO, check if should use isPriceUnAvaialbe flag to adjust the calculation logic. Before that, use false
            // as default value of isPriceUnAvaialbe
            boolean isPriceUnAvaialbe = false;
            if (!partTableTotalUtil.showLineitemPrice(qli, isPriceUnAvaialbe)) {
                continue;
            }
            Integer iCvrageTerm = qli.getICvrageTerm();
            boolean saasTcvAcv = qli.isSaasTcvAcv();
            if (quote.getQuoteHeader().isChannelQuote()) {
                // update calculation logic by referring PartTableTotal.getTotalBpTCV(List, boolean)
                if (CommonServiceUtil.checkIsUsagePart(qli)) {
                    continue;
                }
                Double saasBpTCV = qli.getSaasBpTCV();
                Double channelExtndPrice = qli.getChannelExtndPrice();
                if (qli.isReplacedPart()) {
                    // since only SaasSubscrptnPart has term attribute
                    if (qli.isSaasSubscrptnPart()) {
                        if (saasTcvAcv) {
                            if (iCvrageTerm != null && saasBpTCV != null && masterPartTerms != null) {
                                unusedBidTCV += saasBpTCV.doubleValue() / iCvrageTerm.intValue() * masterPartTerms.intValue();
                            }
                        } else {
                            if (iCvrageTerm != null && channelExtndPrice != null && masterPartTerms != null) {
                                unusedBidTCV += channelExtndPrice.doubleValue() / iCvrageTerm.intValue()
                                        * masterPartTerms.intValue();
                            }
                        }
                    }
                } else {
                    if (saasTcvAcv) {
                        if (saasBpTCV != null) {
                            totBidTCV += saasBpTCV.doubleValue();
                        }
                    } else {
                        if (channelExtndPrice != null) {
                            totBidTCV += channelExtndPrice.doubleValue();
                        }
                    }
                }
            } else {
                // update unused bid TCV, increased bid TCV calculation logic by referring the logic in
                // PartTableTotal.getTotalBidExtendedPrice(List, boolean)
                Double saasBidTCV = qli.getSaasBidTCV();
                Double localExtProratedDiscPrc = qli.getLocalExtProratedDiscPrc();
                if (qli.isReplacedPart()) {
                    // since only SaasSubscrptnPart has term attribute
                    if (qli.isSaasSubscrptnPart()) {
                        if (saasTcvAcv) {
                            if (iCvrageTerm != null && saasBidTCV != null && masterPartTerms != null) {
                                unusedBidTCV += saasBidTCV.doubleValue() / iCvrageTerm.intValue() * masterPartTerms.intValue();
                            }
                        } else {
                            if (localExtProratedDiscPrc != null) {
                                if (iCvrageTerm != null && localExtProratedDiscPrc != null && masterPartTerms != null) {
                                    unusedBidTCV += localExtProratedDiscPrc.doubleValue() / iCvrageTerm.intValue()
                                            * masterPartTerms.intValue();
                                }
                            }
                        }
                    }
                } else {
                    if (saasTcvAcv) {
                        if (saasBidTCV != null) {
                            totBidTCV += saasBidTCV.doubleValue();
                        }
                    } else {
                        if (localExtProratedDiscPrc != null) {
                            totBidTCV += localExtProratedDiscPrc.doubleValue();
                        }
                    }
                }
            }
        }

		configrtn.setUnusedBidTCV(new Double(unusedBidTCV));
		increaseBidTCV = totBidTCV - unusedBidTCV;
		configrtn.setIncreaseBidTCV(new Double(increaseBidTCV));
	}

    public static void setCmprssCvrageOUP(Quote quote) throws TopazException{
    	TimeTracer tracer = TimeTracer.newInstance();
    	if(!quote.getQuoteHeader().getCmprssCvrageFlag()){
            return;
        }

        for(Iterator it = quote.getMasterSoftwareLineItems().iterator(); it.hasNext(); ){
            QuoteLineItem qli = (QuoteLineItem)it.next();

            if(qli.isEligibleForCmprssCvrage()){
                if(qli.isObsoletePart()){
                    qli.setOverrideUnitPrc(qli.getOverrideUnitPrc());
                }else{
                    qli.setOverrideUnitPrc(qli.getLocalUnitProratedDiscPrc());
                }
            }
        }
        tracer.dump();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void updateEOLPartPrices() throws TopazException{
    	TimeTracer tracer = TimeTracer.newInstance();
    	List lineItems = quote.getLineItemList();
    	if(lineItems.size() == 0){
    		return;
    	}

    	List eolParts = new ArrayList();
    	int partHasPriceFlag = quote.getQuoteHeader().getPartHasPriceFlag();
    	for(Iterator it = lineItems.iterator(); it.hasNext(); ){
    		QuoteLineItem item = (QuoteLineItem)it.next();
    		if(item.isObsoletePart()){
    			//we only calculate prices for re-activable parts
    			if(item.canPartBeReactivated()){
    				eolParts.add(item);
    			}
    		}
    		if(!item.isPartHasPrice()){
    		    partHasPriceFlag = 0;
    		}
    	}
    	QuoteHeader header = quote.getQuoteHeader();
    	header.setPartHasPriceFlag(partHasPriceFlag);
    	if(eolParts.size() > 0){
    		String prcLvl = getPriceLvl();
    		logDebug("begin to update prices for EOL parts, using price level code" + prcLvl);
    		QuoteLineItemFactory.singleton().getEolHistPrice(eolParts, header.getCountry().getCode3(),header.getCurrencyCode(), prcLvl);
    	}

    	for (int i = 0; i < eolParts.size(); i++) {
    	    updateProrateEolPrice((QuoteLineItem) eolParts.get(i));
        }
    	tracer.dump();
    }

    private void logDebug(String debugMsg){
    	if(logContext instanceof QuoteLogContextLog4JImpl){
			if(((QuoteLogContextLog4JImpl)logContext).isDebug(this)){
				logContext.debug(this, debugMsg);
			}
		}
	 }

    protected abstract String getPriceLvl();

    protected abstract void updateProrateEolPrice(QuoteLineItem item) throws TopazException;

    public void setApplncMainGroup(List lineItems){
    	TimeTracer tracer = TimeTracer.newInstance();
    	String applncMainId = "";
    	if (lineItems != null){
    		for (Object obj : lineItems){
    			QuoteLineItem qli = (QuoteLineItem)obj;
    			if (qli.isApplncPart() && qli.isApplncMain()){
    				applncMainId = qli.getApplianceId();
    				break;
    			}
    		}

    		for (Object obj : lineItems){
    			QuoteLineItem qli = (QuoteLineItem)obj;
    			if (qli.isApplncPart()){
    				if (StringUtils.isNotEmpty(qli.getApplianceId())
        					&& applncMainId.equals(qli.getApplianceId())){
    					qli.setApplncMainGroup(true);
    				} else {
    					qli.setApplncMainGroup(false);
    				}

    			}
    		}
    	}
    	tracer.dump();
    }

	protected void processForGrowthDelegation() throws TopazException, QuoteException{
		TimeTracer tracer = TimeTracer.newInstance();
		if(GrowthDelegationUtil.isDisplayOverallYTYTotal(quote)) {
	        GrowthDelegationUtil.reCalculateYTYGrowthStatusAndYtyPct(quote);
	    }
		// 14.2 GD When renewal parts added to a license part is need to
		// calculate yty
		if (GDPartsUtil.isQuoteEligibleForRenewalGrowthDelegation(quote)) {
			GDPartsUtil.processForGrowthDelegation(quote);
		}
		tracer.dump();
	}
	
	
}
