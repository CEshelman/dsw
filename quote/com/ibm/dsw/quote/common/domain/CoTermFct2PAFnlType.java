/**
 * 
 */
package com.ibm.dsw.quote.common.domain;

import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author CrimsonLin
 *
 */
public class CoTermFct2PAFnlType extends CoverageTermTypes {

	public CoTermFct2PAFnlType(Quote quote, PartsPricingConfiguration ppc) throws TopazException {
		super(quote, ppc);
		this.configId =  QuoteCommonUtil.getOrignlConfigrtnIdForFnl(quote.getSaaSLineItems(),ppc);
		this.isTermExtension = ppc.isTermExtension();
		this.configrtrPartMap = findMainPartsFromChrgAgrm();
	}

	@Override
	public int setupTermForSub(QuoteLineItem qli) throws TopazException {
		//For FCT_TO_PA_FNL, use new logic.
		//if replaced part, do not need to set term, just get term to calculate billing periods.
		if(qli.isReplacedPart()){
			termMonths = qli.getICvrageTerm() == null ? 0 : qli.getICvrageTerm().intValue();
		}
		else {
			if(dataPack == null || dataPack.getFctToPaCalcTerm() == null || dataPack.getFctToPaFinalTerm() == null) {
			}
			termMonths = Integer.valueOf(dataPack.getFctToPaFinalTerm());
			qli.setICvrageTerm(new Integer(termMonths));
		}
		return termMonths;
		
	}

	
	protected Date getAREDByCurntEndDate(ConfiguratorPart cp,Date caEndDate,String configActionCode){
		TimeTracer tracer = TimeTracer.newInstance();
		Date aradDate = null;
		
		aradDate = cp.getCotermEndDate();
		
		if (caEndDate != null && DateUtil.getCurrentDate().before(caEndDate)){
			aradDate = caEndDate;
		}
		tracer.dump();
		return aradDate;
	}
	
	public void setEarlyRenewalCompDateForType() throws TopazException {
		Map<Integer, Date> cmpDateMap = new HashMap<Integer, Date>();
		Iterator subSaaSlineItemIt = subSaaSlineItemList.iterator();
		boolean isExtensionTerm =  dataPack.isTermExtension();
		while(subSaaSlineItemIt.hasNext()){
			QuoteLineItem qli = (QuoteLineItem) subSaaSlineItemIt.next();
			 if(qli.isReplacedPart()){
				ConfiguratorPart cp = configrtrPartMap.get(qli.getPartNum());
				Date aredDate = getAREDByCurntEndDate(cp,cp.getEndDate(),ppc.getConfigrtnActionCode());
				if(isExtensionTerm == false){							
					if(cp !=null && aredDate != null) {
						qli.setEarlyRenewalCompDate(DateUtil.plusOneDay(aredDate));
						cmpDateMap.put(qli.getRefDocLineNum(), qli.getEarlyRenewalCompDate());
					}
				}else{
					if(qli.getCalculateRemaningTerm() != null && qli.getCalculateRemaningTerm() <= 0){
						qli.setEarlyRenewalCompDate(DateUtil.plusOneDay(aredDate));
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
}
