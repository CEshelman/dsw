/**
 * 
 */
package com.ibm.dsw.quote.common.domain;

import java.sql.Date;
import java.util.Map;

import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author CrimsonLin
 *
 */
public class CoTermNewNCTType extends CoverageTermTypes {

	public CoTermNewNCTType(Quote quote, PartsPricingConfiguration ppc) throws TopazException {
		super(quote, ppc);
		this.isTermExtension = ppc.isTermExtension();
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.common.domain.CoverageTermTypes#calculateFinalizationTerm(java.lang.String, java.lang.String, java.sql.Date, java.sql.Date, java.lang.String, boolean, com.ibm.dsw.quote.common.domain.ServiceDateModType)
	 */
	@Override
	public RedirectConfiguratorDataBasePack calculateFinalizationTerm(Date cotermEndDate) throws TopazException {
		if ( ppc.getServiceDateModType() != null && ServiceDateModType.CE.equals( ppc.getServiceDateModType())) {
		    dataPack = new RedirectConfiguratorDataBasePack();
			dataPack.setCotermEndDate(cotermEndDate);
			dataPack.setTermExtension(this.isTermExtension);
			dataPack.setProvisioningDate(this.provsngDate);
			return dataPack;
		} else return null;
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.common.domain.CoverageTermTypes#setupTermForSub(java.util.List, com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack, java.lang.String)
	 */
	@Override
	public int setupTermForSub(QuoteLineItem qli) throws TopazException {
		// for NewNCt term = term(service end date - (Estimated order date + Estimated provisioning days));
		if(dataPack != null && dataPack.isTermExtension() && dataPack.getProvisioningDate() != null && dataPack.getCotermEndDate()!= null){
			termMonths = DateUtil.calculateFullMonths(dataPack.getProvisioningDate(),dataPack.getCotermEndDate());
			if(termMonths == -1){
				termMonths = qli.getICvrageTerm() == null ? 0 : qli.getICvrageTerm().intValue();
			}
			qli.setICvrageTerm(new Integer(termMonths));
		}else{
			termMonths = qli.getICvrageTerm() == null ? 0 : qli.getICvrageTerm().intValue();
		}
		return termMonths;
		
	}

	
	public Map<String, ConfiguratorPart> findMainPartsFromChrgAgrm() throws TopazException {
		return null;
	}

}
