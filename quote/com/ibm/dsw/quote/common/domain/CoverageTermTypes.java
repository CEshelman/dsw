/**
 * 
 */
package com.ibm.dsw.quote.common.domain;

import java.sql.Date;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcess;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcessFactory;
import com.ibm.dsw.quote.customer.action.PrepareConfiguratorRedirectDataBaseAction;
import com.ibm.dsw.quote.customerlist.domain.ActiveService;
import com.ibm.dsw.quote.customerlist.domain.ExtensionActiveService;
import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;
import com.ibm.dsw.quote.draftquote.util.PartPriceHelper;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author CrimsonLin
 *
 */
public abstract class CoverageTermTypes {

    protected RedirectConfiguratorDataBasePack dataPack = null;
    protected int termMonths = 0;
    protected Quote quote = null;

    protected PartsPricingConfiguration ppc = null;

    protected String chargeNum = "";

    protected String configId = "";
    protected Boolean isTermExtension = false;
    protected String configrtnActionCode = "";
    protected Date provsngDate = null;
    protected List subSaaSlineItemList = null;
    protected Date aredDate = null;
    Map<String, ConfiguratorPart> configrtrPartMap = null;

    public CoverageTermTypes(Quote quote, PartsPricingConfiguration ppc) throws TopazException {
        this.quote = quote;
        this.ppc = ppc;
        this.configrtnActionCode = ppc.getConfigrtnActionCode();
        this.chargeNum = quote.getQuoteHeader().getRefDocNum();
        this.provsngDate = getProvsngDate(ppc);
        this.subSaaSlineItemList = (List) quote.getPartsPricingConfigrtnsMap().get(ppc);
        this.aredDate = null;

    }

    public RedirectConfiguratorDataBasePack calculateFinalizationTerm(Date cotermEndDate) throws TopazException {
        try {
            // In order to avoid search charge agreement multiple times, get term first, then setup each line item.
            this.dataPack = PartPriceHelper.calculateAddOnFinalizationTerm(this.chargeNum, this.configId, this.provsngDate,
                    cotermEndDate, this.configrtnActionCode, this.isTermExtension, ppc.getServiceDateModType());
            return dataPack;
        } catch (QuoteException e) {
            throw new TopazException(e);
        }
    }

    protected java.sql.Date getProvsngDate(PartsPricingConfiguration ppc) {
        TimeTracer tracer = TimeTracer.newInstance();
        java.util.Date estmtdOrdDate = quote.getQuoteHeader().getEstmtdOrdDate() == null ? DateUtil.getCurrentYYYYMMDDDate()
                : quote.getQuoteHeader().getEstmtdOrdDate();
        int provisngDays = ppc.getProvisngDays() == null ? 0 : ppc.getProvisngDays().intValue();
        Calendar estdPrvsngCal = Calendar.getInstance();// Estimated provisioning Date
        estdPrvsngCal.setTime(estmtdOrdDate);
        estdPrvsngCal.add(Calendar.DATE, provisngDays);// Estimated provisioning Date = estimated Order Date + item's
                                                       // provisioning days
        java.sql.Date provsngDate = new java.sql.Date(estdPrvsngCal.getTime().getTime());
        tracer.dump();
        return provsngDate;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setupTerm(List subSaaSlineItemList, ChargeAgreement ca) throws TopazException, QuoteException {
        TimeTracer tracer = TimeTracer.newInstance();
        Iterator subSaaSlineItemIt = subSaaSlineItemList.iterator();

        // fixing term value of replaced parts
        QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
        List<ActiveService> activeServices = null;
        if (StringUtils.isNotBlank(this.chargeNum) && StringUtils.isNotBlank(this.configId)) {
            activeServices = quoteProcess.retrieveLineItemsFromOrder(this.chargeNum, this.configId);
            activeServices = PrepareConfiguratorRedirectDataBaseAction.serviceFilter(activeServices, true);
        }

        while (subSaaSlineItemIt.hasNext()) {
            QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemIt.next();
            if (qli.isSaasSubscrptnPart() || qli.isSaasSubsumedSubscrptnPart()) {
                int termMonths = qli.getICvrageTerm() == null ? 0 : qli.getICvrageTerm().intValue();
                if (quote.getQuoteHeader().isSubmittedQuote()) {
                    termMonths = qli.getICvrageTerm() == null ? 0 : qli.getICvrageTerm().intValue();
                }
                // draft quote
                else {
                    termMonths = setupTermForSub(qli);
                }
                qli.setICvrageTerm(new Integer(termMonths));

                if (dataPack != null && dataPack.isTermExtension()) {
                    setExtensionEligibilityDate(qli);
                }

                //set remaing term to CumCvrageTerm for saas part.CumCvrageTerm is used to set setup part term
                if (dataPack != null && dataPack.isTermExtension()) {
                    int remaningTerm = 0;
                    int calculateRemaningTerm = 0;
                    ExtensionActiveService eas = dataPack.getExtensionActiveServiceByPartNumSeqNum(qli.getPartNum(),
                            qli.getRefDocLineNum() == null ? 0 : qli.getRefDocLineNum().intValue());
                    if (eas != null) {
                        remaningTerm = eas.getRemaningTerm();
                        calculateRemaningTerm = eas.getCalculateRemaningTerm();
                    }
                    qli.setCumCvrageTerm(Integer.valueOf(remaningTerm));
                    qli.setCalculateRemaningTerm(calculateRemaningTerm);
                }

                if (dataPack != null && dataPack.isTermExtension()) {
                    // if CA is not null
                    if (ca != null && ca.getEndDate() != null) {
                        int remainingTermTillCAEndDate = DateUtil.calculateFullCalendarMonths(dataPack.getProvisioningDate(),
                                new Date(ca.getEndDate().getTime()));
                        if (remainingTermTillCAEndDate == -1 || remainingTermTillCAEndDate < 0) {
                            remainingTermTillCAEndDate = 0;
                        }
                        qli.setRemainingTermTillCAEndDate(remainingTermTillCAEndDate);
                        int remaningTerm = 0;
                        ExtensionActiveService eas = dataPack.getExtensionActiveServiceByPartNumSeqNum(qli.getPartNum(),
                                qli.getRefDocLineNum() == null ? 0 : qli.getRefDocLineNum().intValue());
                        if (eas != null) {
                            remaningTerm = eas.getRemaningTerm();
                        }
                        /**
                         * if remainingTermTillCAEndDate == 0, <br>
                         * it means two cases:<br>
                         * 1. CA is end, already in renewal term: use remaining renewal term <br>
                         * 2. in the last month of CA: use renewal term<br>
                         * 
                         * TODO: make sure that eas.getRemaningTerm() is the value of above 'renewal term' and
                         * 'remaining renewal term'
                         */

                        if (qli.isReplacedPart() && remainingTermTillCAEndDate == 0) {
                            if (remaningTerm != 0) {
                                qli.setRemainingTermTillCAEndDate(remaningTerm);
                            }
                        }
                    }
                }

                if (qli.isReplacedPart()) {
                    ActiveService activeService = null;
                    if (activeServices != null) {
                        for (int i = 0; i < activeServices.size(); i++) {
                            activeService = activeServices.get(i);
                            if (activeService.getPartNumber().equals(qli.getPartNum())) {
                                qli.setICvrageTerm(Integer.parseInt(activeService.getTerm()));
                            }
                        }
                    }
                }
            }
        }
        tracer.dump();

    }

    @SuppressWarnings("rawtypes")
    public void setupTermForSubmittedQuote(List subSaaSlineItemList, ChargeAgreement ca) throws TopazException, QuoteException {
        TimeTracer tracer = TimeTracer.newInstance();
        Iterator subSaaSlineItemIt = subSaaSlineItemList.iterator();

        while (subSaaSlineItemIt.hasNext()) {
            QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemIt.next();
            if (qli.isSaasSubscrptnPart() || qli.isSaasSubsumedSubscrptnPart()) {
                if (dataPack != null && dataPack.isTermExtension()) {
					int remaningTerm = 0;
					ExtensionActiveService eas = dataPack
							.getExtensionActiveServiceByPartNumSeqNum(qli
									.getPartNum(),
									qli.getRefDocLineNum() == null ? 0 : qli
											.getRefDocLineNum().intValue());
					if (eas != null) {
						remaningTerm = eas.getRemaningTerm();
					}
                    if (ca != null && ca.getEndDate() != null) {
                        int remainingTermTillCAEndDate = DateUtil.calculateFullCalendarMonths(dataPack.getProvisioningDate(),
                                new Date(ca.getEndDate().getTime()));
                        if (remainingTermTillCAEndDate == -1 || remainingTermTillCAEndDate < 0) {
                            remainingTermTillCAEndDate = 0;
                        }
                        qli.setRemainingTermTillCAEndDate(remainingTermTillCAEndDate);

                        // TODO by Jack, need to verify logic in
                        // com.ibm.dsw.quote.common.util.QuoteCommonUtil.calculateRemainingTermTillCAEndDate(Quote)
                        if (qli.isReplacedPart() && remainingTermTillCAEndDate == 0) {
                            if (remaningTerm != 0) {
                                qli.setRemainingTermTillCAEndDate(remaningTerm);
                            }
                        }
                    }
                }
            }
        }
        tracer.dump();

    }

    protected void setExtensionEligibilityDate(QuoteLineItem qli) throws TopazException {
        TimeTracer tracer = TimeTracer.newInstance();
        Date nextRenwlDate = null;
        Date caLineItemEndDate = null;
        Date renewalEndDate = null;
        List<ActiveService> activeServices = dataPack.getActiveServices();
        Integer refLineItemSeqNumer = null;
        if (qli.isReplacedPart()) {
            refLineItemSeqNumer = qli.getRefDocLineNum();
        } else {
            refLineItemSeqNumer = qli.getRelatedCotermLineItmNum();
        }
        if (activeServices != null && refLineItemSeqNumer != null) {

            for (int i = 0; i < activeServices.size(); i++) {
                ActiveService activeService = activeServices.get(i);
                if (activeService.getLineItemSeqNumber() != null
                        && activeService.getLineItemSeqNumber().intValue() == refLineItemSeqNumer.intValue()) {
                    nextRenwlDate = activeService.getNewxtRenwlDate();
                    caLineItemEndDate = DateUtil.parseDate(activeService.getEndDate());
                    renewalEndDate = activeService.getRenewalEndDate();
                    break;
                }
            }
        }

        if (nextRenwlDate == null) {
            if (caLineItemEndDate != null && renewalEndDate != null) {
                if (caLineItemEndDate.before(renewalEndDate)) {
                    nextRenwlDate = renewalEndDate;
                } else {
                    nextRenwlDate = caLineItemEndDate;
                }
            } else if (caLineItemEndDate != null) {
                nextRenwlDate = caLineItemEndDate;
            } else if (renewalEndDate != null) {
                nextRenwlDate = renewalEndDate;
            }

            if (nextRenwlDate != null) {
                nextRenwlDate = DateUtil.addDays(nextRenwlDate, 1);
            }
        }

        // Notes://ltsgdb001b/85256B83004B1D94/CA30E8393BC22D28482573A7000E50E0/A72E5A90FBF99A3E85257BEF005ECD56
        if (caLineItemEndDate != null && DateUtil.getCurrentDate().before(caLineItemEndDate)) {
            nextRenwlDate = DateUtil.addDays(caLineItemEndDate, 1);
        }

        // String renwlMdlCode = qli.getRenwlMdlCode();
        // if("O".equals(renwlMdlCode) || "R".equals(renwlMdlCode) || "C".equals(renwlMdlCode)){
        // qli.setExtensionEligibilityDate(nextRenwlDate);
        // }
        if (nextRenwlDate != null) {
            qli.setExtensionEligibilityDate(nextRenwlDate);
        }

        tracer.dump();

    }

    public int setupTermForSub(QuoteLineItem qli) throws TopazException {
        // For other action code, keep original logic.
        // if not co-termed: Months selected for the committed term
        if (qli.getRelatedCotermLineItmNum() == null) {
            termMonths = qli.getICvrageTerm() == null ? 0 : qli.getICvrageTerm().intValue();
        }
        // if Co-termed services: Number of whole months between the estimated provisioning date and the committed
        // term's end date.
        else {

            if (dataPack == null || dataPack.getFctToPaCalcTerm() == null || dataPack.getFctToPaFinalTerm() == null) {
            }
            termMonths = Integer.valueOf(dataPack.getFctToPaFinalTerm());

        }
        return termMonths;
    }

    public void setupEarlyRewnewalCompensationDate(List subSaaSlineItemList) throws TopazException {
        TimeTracer tracer = TimeTracer.newInstance();
        if (dataPack == null || StringUtils.isBlank(dataPack.getFctToPaCalcTerm())
                || StringUtils.isBlank(dataPack.getFctToPaFinalTerm())) {
            return;
        }

        // if calculated term is equal to 0 or less than 0, will set early renewal compensation date.
        if (Integer.valueOf(dataPack.getFctToPaCalcTerm()) > 1 && dataPack.isTermExtension() == false) {
            return;
        }
        Map<String, ConfiguratorPart> configrtrPartMap = findMainPartsFromChrgAgrm();

        if (configrtrPartMap == null) {
            return;
        }
        if (ppc.getConfigrtnActionCode() != null && subSaaSlineItemList != null && subSaaSlineItemList.size() > 0) {
            setEarlyRenewalCompDateForType();
        }

        tracer.dump();
    }

    public void setEarlyRenewalCompDateForType() throws TopazException {

    }
    public RedirectConfiguratorDataBasePack getDataPack() {
        return dataPack;
    }
    public Map<String, ConfiguratorPart> findMainPartsFromChrgAgrm() throws TopazException {
        ConfiguratorPartProcess process = null;
        try {
            process = ConfiguratorPartProcessFactory.singleton().create();
        } catch (QuoteException e) {
            throw new TopazException(e);
        }
        if (process == null) {
            return null;
        }
        System.out.println("chargeNum=" + chargeNum + ";  configId=" + configId);
        System.out.println(Thread.currentThread().getStackTrace()[1].getClassName());
        System.out.println(this.getClass());
        return process.findMainPartsFromChrgAgrm(chargeNum, configId);
    }

    protected void setOtherCompDate(Iterator subSaaSlineItemIt, Map<Integer, Date> cmpDateMap) throws TopazException {
        TimeTracer tracer = TimeTracer.newInstance();
        // Notes://CAMDB10/85256B890058CBA6/8278F0CE794010B985256D24005FCB4F/5B93B9511CE20B4885257A670067B07C
        while (subSaaSlineItemIt.hasNext()) {
            QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemIt.next();
            if (!qli.isReplacedPart() && qli.getRelatedCotermLineItmNum() != null) {
                Date cmpDate = cmpDateMap.get(qli.getRelatedCotermLineItmNum());
                qli.setEarlyRenewalCompDate(cmpDate);
            }
        }
        tracer.dump();
    }

    protected void setOtherCompDateNull(Iterator subSaaSlineItemIt) throws TopazException {
        TimeTracer tracer = TimeTracer.newInstance();
        while (subSaaSlineItemIt.hasNext()) {
            QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemIt.next();
            if (!qli.isReplacedPart() && qli.getRelatedCotermLineItmNum() != null) {
                if (qli.getCalculateRemaningTerm() != null && qli.getCalculateRemaningTerm() > 0
                        && qli.getEarlyRenewalCompDate() != null) {
                    qli.setEarlyRenewalCompDate(null);
                }
            }
        }
        tracer.dump();
    }

    protected Date getAREDByCurntEndDate(ConfiguratorPart cp, Date caEndDate, String configActionCode) {
        TimeTracer tracer = TimeTracer.newInstance();
        Date aradDate = null;

        return aradDate;
    }

    public String getConfigId() {
        return ppc.getConfigrtnId();
    }

}
