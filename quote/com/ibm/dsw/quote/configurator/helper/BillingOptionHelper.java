package com.ibm.dsw.quote.configurator.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.BillingOption;
import com.ibm.dsw.quote.appcache.domain.BillingOptionFactory;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

public class BillingOptionHelper {
	protected static LogContext logContext = LogContextFactory.singleton().getLogContext();
	private static BillingOptionHelper singleton = null;
	public static BillingOptionHelper singleton() {
		LogContext logCtx = LogContextFactory.singleton().getLogContext();

		if (BillingOptionHelper.singleton == null) {
			String factoryClassName = null;
			try {
				factoryClassName = BillingOptionHelper.class.getName();
				Class factoryClass = Class.forName(factoryClassName);
				BillingOptionHelper.singleton = (BillingOptionHelper) factoryClass
						.newInstance();
			} catch (IllegalAccessException iae) {
				logCtx.error(BillingOptionHelper.class, iae, iae
						.getMessage());
			} catch (ClassNotFoundException cnfe) {
				logCtx.error(BillingOptionHelper.class, cnfe, cnfe
						.getMessage());
			} catch (InstantiationException ie) {
				logCtx.error(BillingOptionHelper.class, ie, ie.getMessage());
			}
		}
		return singleton;
	}
	public BillingOption getBillingOptionByCode(String code) throws TopazException{
		Map<String, BillingOption> billingOptionMap = BillingOptionFactory.singleton().getBillingOptionMap();
		if (StringUtils.isBlank(code))
			return null;
		if (billingOptionMap == null || billingOptionMap.values().size() == 0)
			return null;
		BillingOption billingOption = billingOptionMap.get(code);
		return billingOption;
	}

	public List getAvaliableBillingFrequencyOptions(ConfiguratorPart cp) throws TopazException{
		List billingOptions = new ArrayList();
		if (cp.getBillgAnlFlag() != null && cp.getBillgAnlFlag().equals("1"))
			billingOptions
					.add(getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_ANNUAL));
		if (cp.getBillgMthlyFlag() != null
				&& cp.getBillgMthlyFlag().equals("1"))
			billingOptions
					.add(getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_MONTHLY));
		if (cp.getBillgQtrlyFlag() != null
				&& cp.getBillgQtrlyFlag().equals("1"))
			billingOptions
					.add(getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_QUARTERLY));
		if (cp.getBillgUpfrntFlag() != null
				&& cp.getBillgUpfrntFlag().equals("1"))
			billingOptions
					.add(getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_UPFRONT));
		if (cp.getBillgEvtFlag() !=null 
				&& cp.getBillgEvtFlag().equals("1"))
			billingOptions
					.add(getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_EVENT));
		return billingOptions;
	}

	public int getBillingOptionMonthBycode(String code) throws TopazException{
		Map<String, BillingOption> billingOptionMap = BillingOptionFactory.singleton().getBillingOptionMap();
		int mon = 0;
		if (StringUtils.isBlank(code))
			return 0;
		if (billingOptionMap == null || billingOptionMap.values().size() == 0)
			return 0;
		BillingOption billingOption = billingOptionMap.get(code);
		if(billingOption != null)
			mon = billingOption.getMonths();
		return mon;
	}
	public Map getFinalBillingFrequencyOptions(String[] avaliableBillingFrequencyOptions) throws TopazException{	//get final billing options from parameters.
		Map<String, List> boMap = new HashMap();	// String- part number, List - billing options list.
		if(avaliableBillingFrequencyOptions != null && avaliableBillingFrequencyOptions.length > 0){
			for(String abfo : avaliableBillingFrequencyOptions){
				String[] tmp = abfo.split(ConfiguratorConstants.BILLING_OPTIONS_PARA_SPLIT_CHAR);	//Per interface rule, item 0 is part number, item 1-n is billing option code. 
				if(!tmp[1].equals("-1")){	//if item 1 is -1, means no billing options for current part number.
					List<BillingOption> bos = new ArrayList();
					for(int i = 1; i<tmp.length; i++)
						bos.add(getBillingOptionByCode(tmp[i]));
					boMap.put(tmp[0], bos);
				}
			}
		}
		return boMap;
		
	}
}
