package com.ibm.dsw.quote.configurator.contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.dao.SaasConfiguratorDaoFactory;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.customer.contract.PrepareConfiguratorRedirectDataBaseContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

public class ConfiguratorBaseContract extends PrepareConfiguratorRedirectDataBaseContract{
	private static final LogContext logger = LogContextFactory.singleton().getLogContext();
	private final String UN_SUPPORT_PARAM = "UN_SUPPORT_PARAM";

	protected String webQuoteNum = null;
	protected Integer term;
	protected String[] avaliableBillingFrequencyOptions;

	protected Integer calcTerm = null;	//first calculation step of FCT TO PA Finalization term.


	protected transient Map<String, ConfiguratorPart> map = new HashMap<String, ConfiguratorPart>();

	protected List<ConfiguratorPart> allPartsFrmPid = new ArrayList<ConfiguratorPart>();

	public boolean isAddOnTradeUp(){
		return PartPriceConstants.ADD_ON_TRADE_UP_FLAG.equals(getAddOnTradeUpFlag());
	}

	public boolean isCoTerm(){
		return PartPriceConstants.CO_TERM_FLAG.equals(getCTFlag());
	}

	public String getWebQuoteNum() {
		return webQuoteNum;
	}
	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum=webQuoteNum;
	}
	public void addPartToMap(ConfiguratorPart part){
		map.put(part.getPartNum(), part);
	}
	public boolean ishavePart(String partNum){
		if(map.get(partNum)==null){
			return false;
		}else{
			return true;
		}
	}
	public Integer getTerm() {
		return term;
	}

	public void load(Parameters parameters, JadeSession session) {
		super.load(parameters, session);
		webQuoteNum = (String)parameters.getParameter(ConfiguratorParamKeys.ReferenceNum);
		String tmpTerm = (String)parameters.getParameter(ConfiguratorParamKeys.term);
		term = getTermIntegerValue(tmpTerm);
    	avaliableBillingFrequencyOptions = parameters.getParameterWithMultiValues(ConfiguratorParamKeys.avaliableBillingFrequencyOptions);	//will handle options in process - build header.
    	//Below is for Finalization
        String tmpCalcTerm = (String)parameters.getParameter(ConfiguratorParamKeys.calcTerm);
        if(StringUtils.isNotBlank(tmpCalcTerm)){
        	calcTerm = Integer.parseInt(tmpCalcTerm);
        }
        String tmpCaEndDate = (String)parameters.getParameter(ConfiguratorParamKeys.caEndDate);
        if(StringUtils.isNotBlank(tmpCaEndDate)){
        	super.setCaEndDate(tmpCaEndDate);
        }
		try {
			this.allPartsFrmPid = SaasConfiguratorDaoFactory.singleton().create()
					.findPartsByWebQuoteNumPID(this.getWebQuoteNum(), this.getPid());
		} catch (TopazException e) {
			logger.error(this, e.getMessage());
		}

	}

	protected void loadForPost(Parameters parameters, JadeSession session) {
        Iterator it = parameters.getParameterNames();
        boolean isRampUpParam = false;
        while (it.hasNext()) {
            String name = (String) it.next();

            if(StringUtils.isBlank(name)){
            	continue;
            }
            String value = null;
            try{
                value = parameters.getParameterAsString(name);
            }catch(Throwable e){
            	logger.debug(this,"param value of " + name + " is not a string, just ignore it");
                continue;
            }

            String partKey = name;
            if(name.endsWith(ConfiguratorParamKeys.qtySuffix)){
            	partKey = getPartKeyFromName(name, ConfiguratorParamKeys.qtySuffix);

            } else if(name.endsWith(ConfiguratorParamKeys.qtyCheckBoxSuffix)){
            	partKey = getPartKeyFromName(name, ConfiguratorParamKeys.qtyCheckBoxSuffix);

            } else if(name.endsWith(ConfiguratorParamKeys.rampUpQtySuffix)){
            	partKey = getRampUpPartKeyFromName(name, ConfiguratorParamKeys.rampUpQtySuffix);
            	isRampUpParam = true;

            } else if(name.endsWith(ConfiguratorParamKeys.billingFrequencySuffix)){
            	partKey = getPartKeyFromName(name, ConfiguratorParamKeys.billingFrequencySuffix);

            } else if(name.endsWith(ConfiguratorParamKeys.rampUpDurationSuffix)){
            	partKey = this.getRampUpPartKeyFromName(name, ConfiguratorParamKeys.rampUpDurationSuffix);
            	isRampUpParam = true;

            } else if(name.endsWith(ConfiguratorParamKeys.rampUpPeriodSuffix)){
            	partKey = getPartKeyFromName(name, ConfiguratorParamKeys.rampUpPeriodSuffix);

            } else if(name.endsWith(ConfiguratorParamKeys.partNumSuffix)){
            	partKey = getPartKeyFromName(name, ConfiguratorParamKeys.partNumSuffix);

            } else {
            	partKey = UN_SUPPORT_PARAM;
            }

            logger.debug(this,"param name = " + partKey + " -> value = " + value);
            if (UN_SUPPORT_PARAM.equals(partKey)) {
                continue;
            }

            ConfiguratorPart part = null;
            //Ramp up parameter format: part#_rampUpSeqNumber_rampUpParameterName
            if(isRampUpParam){
            	int index = partKey.indexOf("_");
            	String partNum = partKey.substring(0, index);
            	ConfiguratorPart masterPart = getPart(partNum);

            	String tmp = partKey.substring(index + 1);
            	int rampSeqNum = Integer.parseInt(tmp);

            	//get the ramp up part associated with the master part
            	part = getRampUpPart(masterPart, rampSeqNum);
            } else {
            	part = getPart(partKey);
            }

            if(name.endsWith(ConfiguratorParamKeys.qtySuffix)
            		|| name.endsWith(ConfiguratorParamKeys.rampUpQtySuffix)){
            	Integer qty = getQty(value);

            	part.setPartQtyStr(value);
            	//Part quantity is 0, thus should be deleted
            	if((StringUtils.isBlank(value) || (qty != null && qty.intValue() == 0))){
            		part.markDeleted();
            	} else {
            		part.setPartQty(qty);
            	}
            	part.markMustHaveQty();
            } else if(name.endsWith(ConfiguratorParamKeys.qtyCheckBoxSuffix)){
            	part.markChecked();

            }else if(name.endsWith(ConfiguratorParamKeys.billingFrequencySuffix)){
            	part.setBillingFrequencyCode(value);

            } else if(name.endsWith(ConfiguratorParamKeys.rampUpDurationSuffix)){
            	Integer rampUpDuration = getRampUpDuration(value); //refer to rtc# 212987
            	part.setRampUpDuration(rampUpDuration);
            	part.setRampUpDurationStr(value);

            } else if(name.endsWith(ConfiguratorParamKeys.rampUpPeriodSuffix)){
            	part.setRampUpPeriod(Integer.parseInt(value));
            }

            isRampUpParam = false;
       }
	}
	private ConfiguratorPart getRampUpPart(ConfiguratorPart part, int rampSeqNum){
		ConfiguratorPart rampUpPart = part.getRampUpPart(rampSeqNum);

		if(rampUpPart == null){
			rampUpPart = new ConfiguratorPart();
			rampUpPart.setPartNum(part.getPartNum());
			part.addRampUpLineItem(rampUpPart, rampSeqNum);
		}
		return rampUpPart;
	}

	private ConfiguratorPart getPart(String partNum){
		ConfiguratorPart part = map.get(partNum);

		if(part == null){
			part = new ConfiguratorPart();
			part.setPartNum(partNum);
			map.put(partNum, part);
		}

		return part;
	}

    private Integer getQty(String value){
		try{
			return Integer.parseInt(value);
		} catch(Exception e){
			return null;
		}
    }

    public List<ConfiguratorPart> getConfiguratorParts(){
    	List<ConfiguratorPart> list = new ArrayList<ConfiguratorPart>();
    	list.addAll(map.values());

    	return list;
    }
    /**
     * refer to rtc# 212987
     * @param value
     * @return
     */
    private Integer getRampUpDuration(String value){
		try{
			return Integer.parseInt(value);
		} catch(Exception e){
			return null;
		}
    }
    
	private Integer getTermIntegerValue(String value) {
		try{
			return Integer.parseInt(value);
		} catch(Exception e){
			return null;
		}
    }
    
	public Integer getCalcTerm() {
		return calcTerm;
	}

	public void setCalcTerm(Integer calcTerm) {
		this.calcTerm = calcTerm;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}
	private String getPartKeyFromName(String name, String suffix){
		int startIndex = name.indexOf("_");
    	int index = name.lastIndexOf(suffix);
    	String partKey = name.substring(startIndex + 1, index);
    	return partKey;
	}

	private String getRampUpPartKeyFromName(String name , String suffix){
		int index = name.lastIndexOf(suffix);
    	String partKey = name.substring(0, index);
    	return partKey;
	}

	public List<ConfiguratorPart> getAllPartsFrmPid() {
		return allPartsFrmPid;
	}

	public void setAllPartsFrmPid(List<ConfiguratorPart> allPartsFrmPid) {
		this.allPartsFrmPid = allPartsFrmPid;
	}

}
