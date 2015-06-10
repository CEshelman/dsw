/**
 * 
 */
package com.ibm.dsw.quote.common.domain;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author CrimsonLin
 *
 */
public class CoTermAddTrdType extends CoverageTermTypes {

	public CoTermAddTrdType(Quote quote, PartsPricingConfiguration ppc) throws TopazException {
		super(quote, ppc);
		this.configId =  ppc.getConfigrtnId();
		this.isTermExtension = ppc.isTermExtension();
		this.configrtrPartMap = findMainPartsFromChrgAgrm();
	}

	
    protected Date getAREDByProvisioningDate(Date provisioningDate, ConfiguratorPart cp)
    {
        TimeTracer tracer = TimeTracer.newInstance();
        Date aredDate = null;
        // Renewal Model Code = R or O, Billing Frequency = "Q" or "A"
        if (null != provisioningDate
                && (PartPriceConstants.RenewalModelCode.R.equals(cp.getRenwlMdlCode()) || PartPriceConstants.RenewalModelCode.O.equals(cp.getRenwlMdlCode()))
                && (PartPriceConstants.BillingOptionCode.ANNUAL.equals(cp.getBillingFrequencyCode()) || PartPriceConstants.BillingOptionCode.QUARTERLY.equals(cp.getBillingFrequencyCode())))
        {
            // "End Date" is less than 1 mth away from Estimated Prov Date (based on Est Ord Date + Est Num Prov Days)
            if (null != cp.getEndDate() && provisioningDate.before(cp.getEndDate()) && DateUtil.plusMonth(provisioningDate, 1).after(cp.getEndDate()))
            {
               // SCENARIO 1: Renewal Start & End Dates are BLANK
                if (null == cp.getRenewalEndDate())
                {
                    aredDate = DateUtil.plusOneDay(cp.getEndDate());
                }
                // SCENARIO 3,4: both "end" and "rnwl end" dates are in the future - "end" <1 mth & "rnwl end" >1 mth
//                else if (DateUtil.plusMonth(provisioningDate, 1).before(cp.getRenewalEndDate()))
//                {
//                    aredDate = null;
//                }
            }
            // SCENARIO 2: "End Date" is in the past, "Rnwl End Date" is less than 1 mth away from Estimated Prov Date (based on Est Ord Date + Est Num Prov Days)
            else if (null != cp.getEndDate() && cp.getEndDate().before(DateUtil.getCurrentDate()) && null != cp.getRenewalEndDate()
                    && provisioningDate.before(cp.getRenewalEndDate()) && DateUtil.plusMonth(provisioningDate, 1).after(cp.getRenewalEndDate()))
            {
                aredDate = DateUtil.plusOneDay(cp.getRenewalEndDate());
            }
        }
        tracer.dump();
        return aredDate;
    }

	public void setEarlyRenewalCompDateForType() throws TopazException {
		Map<Integer, Date> cmpDateMap = new HashMap<Integer, Date>();
		Iterator subSaaSlineItemIt = subSaaSlineItemList.iterator();
		boolean isExtensionTerm =  dataPack.isTermExtension();
		while(subSaaSlineItemIt.hasNext()){
			QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemIt.next();
			 if(qli.isReplacedPart()){
				 ConfiguratorPart cp = configrtrPartMap.get(qli.getPartNum());
	             Date aredDate = getAREDByProvisioningDate(dataPack.getProvisioningDate(), cp);
				 if(isExtensionTerm == false){							 
					 if(cp !=null) {
						 qli.setEarlyRenewalCompDate(aredDate);
						 cmpDateMap.put(qli.getRefDocLineNum(), qli.getEarlyRenewalCompDate());
					 }
				 }else{
					if(qli.getCalculateRemaningTerm() != null && qli.getCalculateRemaningTerm() <= 0){
						
						qli.setEarlyRenewalCompDate(aredDate);
						cmpDateMap.put(qli.getRefDocLineNum(), qli.getEarlyRenewalCompDate());
					}
					
					if(qli.getCalculateRemaningTerm() != null && qli.getCalculateRemaningTerm() > 0 && 
							qli.getEarlyRenewalCompDate() != null){
						qli.setEarlyRenewalCompDate(null);
					}
				 }
			 }
		}
		setOtherCompDate(subSaaSlineItemList.iterator(), cmpDateMap);
		if(isExtensionTerm == true){					
			setOtherCompDateNull(subSaaSlineItemList.iterator());
		}
	}

    protected void setOtherCompDate(Iterator subSaaSlineItemIt, Map<Integer, Date> cmpDateMap) throws TopazException
    {
        TimeTracer tracer = TimeTracer.newInstance();
        while (subSaaSlineItemIt.hasNext())
        {
            QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemIt.next();
            if (!qli.isReplacedPart() && qli.getRelatedCotermLineItmNum() != null)
            {
                if ((PartPriceConstants.BillingOptionCode.ANNUAL.equals(qli.getBillgFrqncyCode()) || PartPriceConstants.BillingOptionCode.QUARTERLY.equals(qli.getBillgFrqncyCode())))
                {
                    Date cmpDate = cmpDateMap.get(qli.getRelatedCotermLineItmNum());
                    qli.setEarlyRenewalCompDate(cmpDate);
                }
                else
                {
                    qli.setEarlyRenewalCompDate(null);
                }
            }
        }
        tracer.dump();
    }
}
