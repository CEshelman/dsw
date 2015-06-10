/**
 * 
 */
package com.ibm.dsw.quote.configurator.contract;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwRampUpSubscriptionConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwSubscrptnConfiguratorPart;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @ClassName: SubmittedMonthlySwConfiguratorContract
 * @author Frank
 * @Description:
 * @date Dec 19, 2013 11:49:13 AM
 * 
 */
public class SubmittedMonthlySwConfiguratorContract extends
        MonthlySwConfiguratorBaseContract {
    
    private final String UN_SUPPORT_PARAM = "UN_SUPPORT_PARAM";
    public static final String REDIRECT_ACTION_CANCELL = "0";

    private static final LogContext logger = LogContextFactory.singleton().getLogContext();
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
	protected List<MonthlySwConfiguratorPart> configuratorPartsList = new ArrayList<MonthlySwConfiguratorPart>();

	protected transient Map<String, List<MonthlySwRampUpSubscriptionConfiguratorPart>> rampUpPartByPartNumMap = new HashMap<String, List<MonthlySwRampUpSubscriptionConfiguratorPart>>();

	private Map<String, List<ContractValue>> keyValueMap = new HashMap<String, List<ContractValue>>();

    
    private String exceedCode;
    
    private String redirectAction = null;
    
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        
        loadForPost(parameters,session);
        
    }
    
    @SuppressWarnings("rawtypes")
    protected void loadForPost(Parameters parameters, JadeSession session) {
        // build MonthlySwconfiguratorPart by different name suffix.
        Iterator it = parameters.getParameterNames();

        while (it.hasNext()) {

            String name = (String) it.next();

            if (StringUtils.isBlank(name)) {
                continue;
            }
            String value = null;
            try {
                value = parameters.getParameterAsString(name);
            } catch (Throwable e) {
                logger.debug(this, "param value of " + name
                        + " is not a string, just ignore it");
                continue;
            }
            
            /**
             * partKey : partNum + seqNum
             */
            String partKey = getPartKey(name);
            
            if (UN_SUPPORT_PARAM.equals(partKey)){
                continue;
            }
            
            processKeyValue(partKey, name, value);
        }

		processAllPartParams();

		buildForRampUpPart();
    }
    
    
    private String getPartKeyFromName(String name, String suffix){
        int index = name.lastIndexOf(suffix);
        String partKey = name.substring(0, index);
        return partKey;
    }

    private String getRampUpPartKeyFromName(String name , String suffix){
        int index = name.lastIndexOf(suffix);
        String partKey = name.substring(0, index);
        return partKey;
    }
    
    private String getPartNum(String partKey){
        int index = partKey.indexOf("_");
        String partNum = partKey;
        if (index != -1){
            partNum = partKey.substring(0 , index);
        }
        return partNum;
    }
    
    private String getSeqNum(String partKey){
        int index = partKey.indexOf("_");
        return partKey.substring(index+1);
    }
    
    
    /**
     * subscription part
     * onDemand part
     * set up part
     * @param name
     */
    private String getPartKey(String name) {
        
        String partKey = UN_SUPPORT_PARAM;
        
        if (name.endsWith(ConfiguratorParamKeys.qtySuffix)) {
            partKey = getPartKeyFromName(name, ConfiguratorParamKeys.qtySuffix);


        } else if (name.endsWith(ConfiguratorParamKeys.billingFrequencySuffix)) {
            partKey = getPartKeyFromName(name,
                    ConfiguratorParamKeys.billingFrequencySuffix);

        } else if (name.endsWith(ConfiguratorParamKeys.rampUpPeriodSuffix)) {
            partKey = getPartKeyFromName(name,
                    ConfiguratorParamKeys.rampUpPeriodSuffix);

        } else if (name.endsWith(ConfiguratorParamKeys.partNumSuffix)) {
            partKey = getPartKeyFromName(name,
                    ConfiguratorParamKeys.partNumSuffix);

        } else if (name.endsWith(ConfiguratorParamKeys.rampUpQtySuffix)) {
            partKey = getRampUpPartKeyFromName(name,
                    ConfiguratorParamKeys.rampUpQtySuffix);
        }  else if (name.endsWith(ConfiguratorParamKeys.rampUpDurationSuffix)) {
            partKey = this.getRampUpPartKeyFromName(name,
                    ConfiguratorParamKeys.rampUpDurationSuffix);
        } else if (name.endsWith(ConfiguratorParamKeys.termSuffix)) {
            partKey = getPartKeyFromName(name,
                    ConfiguratorParamKeys.termSuffix);
        } else if (name.endsWith(ConfiguratorParamKeys.tierQtyMeasreSuffix)) {
            partKey = getPartKeyFromName(name,
                    ConfiguratorParamKeys.tierQtyMeasreSuffix);
        } else if (name.endsWith(ConfiguratorParamKeys.rampUpPeriodHiddenSuffix)) {
            partKey = getPartKeyFromName(name, ConfiguratorParamKeys.rampUpPeriodHiddenSuffix);
            // rampUpPeriodHiddenSuffix example: D12ZMLL_2_20_rampUpPeriodHiddenSuffix
            int index = partKey.lastIndexOf("_");
            String partSeqNum = partKey.substring(index + 1);
            partKey = partKey.substring(0, index);
            String partNum = partKey.substring(0, partKey.lastIndexOf("_"));
            partKey = partNum + "_" + partSeqNum;

        }
        return partKey;
    }
    
    
	private MonthlySwConfiguratorPart getMonthlySwconfiguratorPart(List<ContractValue> contractValueList, String partKey) {
		if (contractValueList != null) {
			boolean isRampUpPart = false;
			boolean isSubscriptionPart = false;
			for (Iterator iterator = contractValueList.iterator(); iterator.hasNext();) {
				ContractValue contractValue = (ContractValue) iterator.next();
				if (isRampUpParam(contractValue.getName())) {
					isRampUpPart = true;
					break;
				}
				if (isSubscriptionParam(contractValue.getName())) {
					isSubscriptionPart = true;
					break;
				}
			}
			return buildConfiguratorPart(isRampUpPart, isSubscriptionPart, partKey);
		}else{
			return buildConfiguratorPart(false, false, partKey);
		}
    }


    private void setPartParam(MonthlySwConfiguratorPart configuratorPart, String name, String value) {
     
		if (name.endsWith(ConfiguratorParamKeys.qtySuffix) || name.endsWith(ConfiguratorParamKeys.rampUpQtySuffix)) {
            Integer qty = getInteger(value);

            configuratorPart.setPartQtyStr(value);
            //Part quantity is 0, thus should be deleted
            if((StringUtils.isBlank(value) || (qty != null && qty.intValue() == 0))){
                //part will be deleted, do nothing here
            } else {
                configuratorPart.setPartQty(qty);
            }
            configuratorPart.markMustHaveQty();
        } 
        else if (name.endsWith(ConfiguratorParamKeys.billingFrequencySuffix)){
            configuratorPart.setBillingFrequencyCode(value);
        } else if (name.endsWith(ConfiguratorParamKeys.rampUpPeriodHiddenSuffix)) {
            configuratorPart.getSubmitConfiguratorPart().setRampUpPeriodStr(value);
		} else if (name.endsWith(ConfiguratorParamKeys.rampUpDurationSuffix)) {
            configuratorPart.getSubmitConfiguratorPart().setRampUpDurationStr(value);
        } else if (name.endsWith(ConfiguratorParamKeys.termSuffix)){
			Integer term = getInteger(value);
			configuratorPart.setTermStr(value);
			if (term != null) {
				configuratorPart.setTerm(term);
			}
        } else if (name.endsWith(ConfiguratorParamKeys.tierQtyMeasreSuffix)){
            configuratorPart.setTierQtyMeasre(StringUtils.isBlank(value) ? null : new Integer(value));
        }
         
        
    }
    
    
	private boolean isRampUpParam(String name) {
		return name.endsWith(ConfiguratorParamKeys.rampUpQtySuffix) || name.endsWith(ConfiguratorParamKeys.rampUpDurationSuffix);
	}

	private boolean isSubscriptionParam(String name) {
		return name.endsWith(ConfiguratorParamKeys.termSuffix) || name.endsWith(ConfiguratorParamKeys.billingFrequencySuffix);
	}
    

    public List<MonthlySwConfiguratorPart> getConfiguratorPartList(){

        return configuratorPartsList;
    }




    public String getExceedCode() {
        return exceedCode;
    }


    public void setExceedCode(String exceedCode) {
        this.exceedCode = exceedCode;
    }
    
    public boolean isCancellConfigrtn(){
        return REDIRECT_ACTION_CANCELL.equals(this.redirectAction);
    }
    
    private Integer getInteger(String value){
        try{
            return Integer.parseInt(value);
        } catch(Exception e){
            return null;
        }
    }

    public String getRedirectAction() {
        return redirectAction;
    }

    public void setRedirectAction(String redirectAction) {
        this.redirectAction = redirectAction;
    }
    
	private void buildForRampUpPart() {
		List<MonthlySwConfiguratorPart> allParts = getConfiguratorPartList();
		if (allParts == null || allParts.size() == 0) {
			return;
		}
		mapRampUpPartByPartNum(allParts);
		buildRelatedRampUpForMainSubscriptionPart(allParts);
	}

	/**
	 * @param allParts
	 *            <partNum, rampUpList> in order to get rampUpList according to
	 *            main subscription part's partNum
	 */
	private void mapRampUpPartByPartNum(List<MonthlySwConfiguratorPart> allParts) {
		for (MonthlySwConfiguratorPart monthlySwConfiguratorPart : allParts) {
			if (monthlySwConfiguratorPart instanceof MonthlySwRampUpSubscriptionConfiguratorPart) {
				if (rampUpPartByPartNumMap.get(monthlySwConfiguratorPart.getPartNum()) == null) {
					List<MonthlySwRampUpSubscriptionConfiguratorPart> rampUpPartList = new ArrayList<MonthlySwRampUpSubscriptionConfiguratorPart>();
					rampUpPartList.add((MonthlySwRampUpSubscriptionConfiguratorPart) monthlySwConfiguratorPart);
					rampUpPartByPartNumMap.put(monthlySwConfiguratorPart.getPartNum(), rampUpPartList);
				} else {
					rampUpPartByPartNumMap.get(monthlySwConfiguratorPart.getPartNum()).add(
							(MonthlySwRampUpSubscriptionConfiguratorPart) monthlySwConfiguratorPart);
				}
			}
		}
	}

	private void buildRelatedRampUpForMainSubscriptionPart(List<MonthlySwConfiguratorPart> allParts) {
		for (MonthlySwConfiguratorPart monthlySwConfiguratorPart : allParts) {
			if (monthlySwConfiguratorPart instanceof MonthlySwSubscrptnConfiguratorPart) {
				List<MonthlySwRampUpSubscriptionConfiguratorPart> rampUpPartList = rampUpPartByPartNumMap
						.get(monthlySwConfiguratorPart.getPartNum());
				if (rampUpPartList != null) {
					// sort the ram-up parts by it's period number
					Collections.sort(rampUpPartList, new Comparator<MonthlySwRampUpSubscriptionConfiguratorPart>() {
						@Override
						public int compare(MonthlySwRampUpSubscriptionConfiguratorPart object1,
								MonthlySwRampUpSubscriptionConfiguratorPart object2) {
							MonthlySwRampUpSubscriptionConfiguratorPart item1 = object1;
							MonthlySwRampUpSubscriptionConfiguratorPart item2 = object2;
                            return item1.getSubmitConfiguratorPart().getRampUpPeriod()
                                    - item2.getSubmitConfiguratorPart().getRampUpPeriod();
						}
					});

					for (int i = 0; i < rampUpPartList.size(); i++) {
						MonthlySwRampUpSubscriptionConfiguratorPart ramupPart = rampUpPartList.get(i);
						if (i == 0) {
							((MonthlySwSubscrptnConfiguratorPart) monthlySwConfiguratorPart)
									.setNextRampUpSubscriptionPart(ramupPart);
						}
						((MonthlySwSubscrptnConfiguratorPart) monthlySwConfiguratorPart).getRampUpParts().add(ramupPart);
					}

				}
			}
		}
	}

	public class ContractValue {
		private String partKey;
		private String name;
		private String value;

		public ContractValue(String partKey, String name, String value) {
			this.partKey = partKey;
			this.name = name;
			this.value = value;
		}

		public String getPartKey() {
			return partKey;
		}

		public void setPartKey(String partKey) {
			this.partKey = partKey;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
	
	private void processKeyValue(String partKey, String name, String value){
		if(keyValueMap.get(partKey) == null){
			List<ContractValue> contractValueList = new ArrayList<SubmittedMonthlySwConfiguratorContract.ContractValue>();
			ContractValue contractValue = new ContractValue(partKey, name, value);
			contractValueList.add(contractValue);
			keyValueMap.put(partKey, contractValueList);
		}else{
			List<ContractValue> contractValueList = keyValueMap.get(partKey);
			ContractValue contractValue = new ContractValue(partKey, name, value);
			contractValueList.add(contractValue);
		}
	}

	private void processAllPartParams() {
		if (keyValueMap != null) {
			List<String> allPartKeys = new ArrayList(keyValueMap.keySet());
			for (Iterator iterator = allPartKeys.iterator(); iterator.hasNext();) {
				String partKey = (String) iterator.next();
				List<ContractValue> contractValueList = keyValueMap.get(partKey);
				MonthlySwConfiguratorPart configuratorPart = getMonthlySwconfiguratorPart(contractValueList, partKey);
				for (Iterator iterator2 = contractValueList.iterator(); iterator2.hasNext();) {
					ContractValue contractValue = (ContractValue) iterator2.next();
					setPartParam(configuratorPart, contractValue.getName(), contractValue.getValue());
				}
				configuratorPartsList.add(configuratorPart);
			}
		}
	}

	private MonthlySwConfiguratorPart buildConfiguratorPart(boolean isRampUpPart, boolean isSubscriptionPart, String partKey) {
		MonthlySwConfiguratorPart configuratorPart = null;
		if (isRampUpPart) {
			configuratorPart = new MonthlySwRampUpSubscriptionConfiguratorPart();
		} else if (isSubscriptionPart) {
			configuratorPart = new MonthlySwSubscrptnConfiguratorPart();
		} else {
			configuratorPart = new MonthlySwConfiguratorPart();
		}
		configuratorPart.setSubmitConfiguratorPart(configuratorPart.new SubmitConfiguratorPart());
		configuratorPart.setPartNum(getPartNum(partKey));
		configuratorPart.setSeqNum(getSeqNum(partKey));
		return configuratorPart;
	}
    
    
}
