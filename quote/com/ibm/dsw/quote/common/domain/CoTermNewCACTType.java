/**
 * 
 */
package com.ibm.dsw.quote.common.domain;

import java.util.Map;

import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author CrimsonLin
 *
 */
public class CoTermNewCACTType extends CoverageTermTypes {
	
	public CoTermNewCACTType(Quote quote, PartsPricingConfiguration ppc) throws TopazException {
		super(quote, ppc);
		this.configId =  ppc.getCotermConfigrtnId();
		this.isTermExtension = false;
		this.configrtrPartMap = findMainPartsFromChrgAgrm();
	}

	
	public Map<String, ConfiguratorPart> findMainPartsFromChrgAgrm() throws TopazException {
		return null;
	}
	
	public String getConfigId(){
		return ppc.getCotermConfigrtnId();
	}
}
