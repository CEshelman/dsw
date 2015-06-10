package com.ibm.dsw.quote.configurator.process;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.ead4j.topaz.exception.TopazException;

public abstract class SaasConfiguratorProcess_Impl implements SaasConfiguratorProcess {

	abstract public void addSaasPartsToQuote(AddOrUpdateConfigurationContract contract) throws TopazException, QuoteException;
	
	public static void setSaaSPartAttribute(List<ConfiguratorPart> list,
			Map<String, ConfiguratorPart> map, String pId, Integer term,
			boolean isBasicConfigurator) {
		for (Iterator<ConfiguratorPart> it = list.iterator(); it.hasNext();) {
			ConfiguratorPart part = it.next();

			ConfiguratorPart pidPart = map.get(part.getPartNum());
			if (pidPart == null) {
				it.remove();
				continue;
			}

			part.setSwProdBrandCode(pidPart.getSwProdBrandCode());
			part.setSubId(pidPart.getSubId());
			part.setSapMatlTypeCode(pidPart.getSapMatlTypeCode());
			part.setWwideProdCode(pidPart.getWwideProdCode());
			part.setTierQtyMeasre(pidPart.getTierQtyMeasre());
			part.setPricingTierModel(pidPart.getPricingTierModel());
			part.setSubsumedSubscrptn(pidPart.isSubsumedSubscrptn());
			// 15.3 Saas hybrid offering
			part.setHybridOfferg(pidPart.isHybridOfferg());

			if (part.getRampUpLineItems() != null) {

				for (ConfiguratorPart rampUpPart : part.getRampUpLineItems()) {
					rampUpPart.setSwProdBrandCode(pidPart.getSwProdBrandCode());
					rampUpPart.setSubId(pidPart.getSubId());
					rampUpPart.setSapMatlTypeCode(pidPart.getSapMatlTypeCode());
					rampUpPart.setWwideProdCode(pidPart.getWwideProdCode());
					rampUpPart.setTierQtyMeasre(pidPart.getTierQtyMeasre());
					rampUpPart.setPricingTierModel(pidPart.getPricingTierModel());
				}
			}
			
			// set term related attributions
			if (isBasicConfigurator) {
				if(part.isSubscrptn()){
		    		part.setTotalTerm(term);
		
					int total = 0;
		    		if(part.getRampUpLineItems() != null){
		    			for(ConfiguratorPart rampUpPart : part.getRampUpLineItems()){
		    				rampUpPart.setTerm(rampUpPart.getRampUpDuration());
		    				rampUpPart.setTotalTerm(term);
		    				rampUpPart.setBillingFrequencyCode(part.getBillingFrequencyCode());
							// refer to rtc#212987, if the input box is not a
							// valid int, duration will be null.
							total += rampUpPart.getRampUpDuration() == null ? 0
									: rampUpPart.getRampUpDuration();
		    			}
		    		}
					if(term != null){
						part.setTerm(term - total);
					}
	    		}
	    		if(part.isSetUp()){
	    			if (part.getAssociatedSubscrptnPart() == null){
	    				part.setTerm(term);
	    			}
	    			part.setTotalTerm(term);
	    		}
	            if (part.isSubsumedSubscrptn()) {
	            	part.setTerm(term);
	                part.setTotalTerm(term);
	            }
			}
            
		}

	}

	public static Map convertConfiguratorPartListToMap(List<ConfiguratorPart> partList) {
		Map<String, ConfiguratorPart> map = new HashMap<String, ConfiguratorPart>();
		for (ConfiguratorPart part : partList) {
			map.put(part.getPartNum(), part);
		}
		return map;
	}

}
