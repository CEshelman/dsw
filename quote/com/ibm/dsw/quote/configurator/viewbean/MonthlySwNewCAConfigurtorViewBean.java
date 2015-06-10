/**
 * 
 */
package com.ibm.dsw.quote.configurator.viewbean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.BillingOption;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorProduct;
import com.ibm.dsw.quote.configurator.domain.MonthlySwNewCAConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwOnDemandConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwSubscrptnConfiguratorPart;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;

/**
 * @ClassName: MonthlySwNewCAConfigurtorViewBean
 * @author Frank
 * @Description: TODO
 * @date Dec 20, 2013 12:30:00 PM
 *
 */
public class MonthlySwNewCAConfigurtorViewBean extends
	MonthlySwConfigurtorViewBean {

	private static final long serialVersionUID = 1L;
	
	
	public List getRampUpOptions(MonthlySwConfiguratorPart configuratorPart) throws QuoteException{
		List options = new ArrayList();
		MonthlySwNewCAConfiguratorPart newCAConfiguratoPart= getMonthlySwNewCAConfiguratorPart(configuratorPart);
		
		int defaultRampUp  = newCAConfiguratoPart.getRampUpPeriod();

		int max = 4;
	    for (int i = 0; i <= max; i++) {
		   if(i == defaultRampUp){
			   options.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), true));
		   } else {
			   options.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), false));
		   }
        }
	   
	   return options;
	}
	
	public List getBillingFrequencyOptions(MonthlySwConfiguratorPart configuratorPart){
		MonthlySwNewCAConfiguratorPart newCAConfiguratorPart = getMonthlySwNewCAConfiguratorPart(configuratorPart);
		
		String defaultBillingCode = configuratorPart.getBillingFrequencyCode();
		
		if (StringUtils.isBlank(defaultBillingCode)){
			defaultBillingCode =ConfiguratorConstants.BILLING_FREQUENCY_MONTHLY;
		}
		
		List<BillingOption> billingOptions = newCAConfiguratorPart.getBillingOptions();
		
		List collection = new ArrayList();
        Collections.sort(billingOptions, new Comparator() {
            public int compare(Object o1, Object o2) {
            	BillingOption op1 = (BillingOption) o1;
            	BillingOption op2 = (BillingOption) o2;
            	return op1.getMonths() - op2.getMonths();
            }
        });
        
        for(BillingOption bo : billingOptions){
    		if(StringUtils.equals(defaultBillingCode, bo.getCode())){
    			collection.add(new SelectOptionImpl(bo.getCodeDesc(), bo.getCode(), true));
    		} else {
    			collection.add(new SelectOptionImpl(bo.getCodeDesc(), bo.getCode(), false));
    		}
        }
        
        return collection;
	}
	

	public MonthlySwNewCAConfiguratorPart getMonthlySwNewCAConfiguratorPart(MonthlySwConfiguratorPart configuratorPart){
		
		MonthlySwNewCAConfiguratorPart newCAConfiguratorPart = null;
		
		if (configuratorPart != null ){
			
			MonthlySwActionConfiguratorPart actionConfiguratorPart = configuratorPart.getConfiguratorActionPart();
			
			if (actionConfiguratorPart instanceof MonthlySwNewCAConfiguratorPart){
				newCAConfiguratorPart = (MonthlySwNewCAConfiguratorPart) actionConfiguratorPart;
			}
		}
		
		// avoid null point exception
		if (newCAConfiguratorPart == null) {
			newCAConfiguratorPart = new MonthlySwNewCAConfiguratorPart(configuratorPart );
		}
		
		return newCAConfiguratorPart;
	}
	
	public String getAllWwideProdCodeDscr(MonthlySwConfiguratorProduct prod){
		StringBuilder allWwideProdCodeDscr = new StringBuilder(500);
		List<MonthlySwOnDemandConfiguratorPart> onDemandParts =  prod.getOnDemandParts();
		List<MonthlySwSubscrptnConfiguratorPart> subscrptnParts = prod.getSubscrptnParts();
		Set allWwideProdCodeDscrSet = new HashSet();
		if(onDemandParts != null && onDemandParts.size() > 0){
			for (Iterator iterator = onDemandParts.iterator(); iterator
					.hasNext();) {
				MonthlySwOnDemandConfiguratorPart onDemandPart = (MonthlySwOnDemandConfiguratorPart) iterator.next();
				allWwideProdCodeDscrSet.add(onDemandPart.getWwideProdCodeDscr());
			}
		}
		if(subscrptnParts != null && subscrptnParts.size() > 0){
			for (Iterator iterator = subscrptnParts.iterator(); iterator
					.hasNext();) {
				MonthlySwSubscrptnConfiguratorPart subscrptnPart = (MonthlySwSubscrptnConfiguratorPart) iterator.next();
				allWwideProdCodeDscrSet.add(subscrptnPart.getWwideProdCodeDscr());
				if(subscrptnPart.getDailyPart() != null){
					allWwideProdCodeDscrSet.add(subscrptnPart.getDailyPart().getWwideProdCodeDscr());
				}
				if(subscrptnPart.getOveargePart() != null){
					allWwideProdCodeDscrSet.add(subscrptnPart.getOveargePart().getWwideProdCodeDscr());
				}
			}
		}
		if(allWwideProdCodeDscrSet.isEmpty()){
			return "";
		}else{
			for (Iterator iterator = allWwideProdCodeDscrSet.iterator(); iterator
					.hasNext();) {
				String wwideProdCodeDscr = (String) iterator.next();
				allWwideProdCodeDscr.append(wwideProdCodeDscr);
				allWwideProdCodeDscr.append(", ");
			}
			String result = allWwideProdCodeDscr.toString();
			return new String(result.substring(0, result.lastIndexOf(", ")));
		}
	}
	
}
