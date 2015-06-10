package com.ibm.dsw.quote.configurator.contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.common.util.DebugUtil;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

public class ConfigureHostedServiceContract extends ConfiguratorBaseContract {
	private static final long serialVersionUID = -3567939787477716914L;
	
	private String operationType = null;	// indicate what operation is taken. if blank, New Configuration will go.
	private String cntryCode = null;
	private String currencyCode = null;
	private String bandLevel = null;
	private String lob = null;
	private transient List<ConfiguratorPart> parts;
	private String[] origPartNumbers = null;
	private String[] origQtys = null;
	private String sourceType = null;


	public String[] getOrigPartNumbers() {
		return origPartNumbers;
	}

	public String[] getOrigQtys() {
		return origQtys;
	}

	public List<ConfiguratorPart> getParts() {
		return parts;
	}

	public void setParts(List<ConfiguratorPart> parts) {
		this.parts = parts;
	}

	public String getLob() {
		return lob;
	}

	public void setLob(String lob) {
		this.lob = lob;
	}

	public String getBandLevel() {
		return bandLevel;
	}

	public void setBandLevel(String bandLevel) {
		this.bandLevel = bandLevel;
	}

	public String getCntryCode() {
		return cntryCode;
	}

	public void setCntryCode(String cntryCode) {
		this.cntryCode = cntryCode;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        operationType = StringUtils.trimToEmpty((String)parameters.getParameter("operationType"));
        //As the below parameters are public now ,so set up them publicly.
        cntryCode = StringUtils.trimToEmpty((String)parameters.getParameter("Region"));
        currencyCode = StringUtils.trimToEmpty((String)parameters.getParameter("CurrencyCode"));
        bandLevel = StringUtils.trimToEmpty((String)parameters.getParameter("BandLevel"));
        lob = StringUtils.trimToEmpty((String)parameters.getParameter("Lob"));
        sourceType = StringUtils.trimToEmpty((String)parameters.getParameter("sourceType"));

        if(StringUtils.isBlank(operationType)){	
	        loadForAddOnReedit(parameters, session);
        }else if(operationType.equalsIgnoreCase(ConfiguratorConstants.OPERATION_TYPE_GO) || operationType.equalsIgnoreCase(ConfiguratorConstants.OPERATION_TYPE_REMOVE_COTERM)){	//clicking GO button or Remove co-term button.
        	loadForPost(parameters, session);
        }
    }
	private void loadForAddOnReedit(Parameters parameters, JadeSession session) {
        String[] partNumbers = parameters.getParameterWithMultiValues("PartNumber");
        List<ConfiguratorPart> cpsFromParas = null ;
        if(partNumbers != null ){
        	cpsFromParas = new ArrayList();
        	String[] bfs = parameters.getParameterWithMultiValues(ConfiguratorParamKeys.BillingFrequency);
        	String[] qtys = parameters.getParameterWithMultiValues(ConfiguratorParamKeys.Quantity);
        	String[] rampUpFlags = parameters.getParameterWithMultiValues(ConfiguratorParamKeys.RampupFlag);
        	String[] rampUpSeqNums = parameters.getParameterWithMultiValues(ConfiguratorParamKeys.RampupSeqNum);
        	String[] terms = parameters.getParameterWithMultiValues(ConfiguratorParamKeys.Term);   
        	String[] origPartNumbers = parameters.getParameterWithMultiValues(ConfiguratorParamKeys.origPartNumber);	//for re-editing add-on, will set in process
        	String[] origQtys = parameters.getParameterWithMultiValues(ConfiguratorParamKeys.origQuantity);	//for re-editing add-on, will set in process
        	this.origPartNumbers = origPartNumbers;
        	this.origQtys = origQtys;
        	for(int i = 0; i<partNumbers.length ; i++){
        		ConfiguratorPart cp = new ConfiguratorPart();
        		cp.setPartNum(partNumbers[i]);
        		cp.setChecked(true);	//If the part is in the part list, indicate the part is included, including setting checked in configurator or existing in CA(which is added quote also.)
        		if(i < bfs.length && StringUtils.isNotBlank(bfs[i]) && !bfs[i].equals("null")){
        			cp.setBillingFrequencyCode(bfs[i]);
        		}
        		if(i< qtys.length && StringUtils.isNotBlank(qtys[i]) && !qtys[i].equals("null")){
        			Integer qty = new Integer(qtys[i].substring(0,qtys[i].indexOf(".")<0?qtys[i].length():qtys[i].indexOf(".")));//Since the parameter is like '1.000'.
        			cp.setPartQty(qty);	//for re-editing add-on, original quantity will be set in process.
        		}
        		if(rampUpFlags!=null && StringUtils.isNotBlank(rampUpFlags[i]))
        			if(rampUpFlags[i].equals(ConfiguratorConstants.RAMP_FLAG_YES)){
    	        		cp.setRampUpFlag(rampUpFlags[i]);
    	        		cp.setRampUpSeqNum(rampUpSeqNums[i]);
    	        		cp.setRampUpDuration(new Integer(terms[i]));
        			}
        		else{
        			cp.setRampUpFlag(ConfiguratorConstants.RAMP_FLAG_NO);
        			if(i < terms.length && StringUtils.isNotBlank(terms[i]) && !terms[i].equals("null"))
        				cp.setTerm(new Integer(terms[i]));	//if master part ,just set the term.
        			else
        				cp.setTerm(new Integer(0));
        		}
        		cpsFromParas.add(cp);
        	}
    		Map<String,ConfiguratorPart> cpsMap = new HashMap();
    		for(ConfiguratorPart cp:cpsFromParas){
    			if(cp.getRampUpFlag().equals(ConfiguratorConstants.RAMP_FLAG_NO))
    				cpsMap.put(cp.getPartNum(), cp);
    		}
    		for(ConfiguratorPart cp:cpsFromParas){
    			if(cp.getRampUpFlag().equals(ConfiguratorConstants.RAMP_FLAG_YES)){
    				ConfiguratorPart cpMaster = cpsMap.get(cp.getPartNum());
    				int rampUpSeqNum = new Integer(cp.getRampUpSeqNum()).intValue();
    				if(rampUpSeqNum > 0 )
    					rampUpSeqNum--;	//Since current ramp up sequence starts from 1.
    				DebugUtil.showString(""+cp.getPartNum(), "loadForAddOnReedit()--->cp.getPartNum()");
    				DebugUtil.showString(""+cp.getPartQty(), "loadForAddOnReedit()--->cp.getPartQty()");
    				DebugUtil.showString(""+rampUpSeqNum, "loadForAddOnReedit()--->rampUpSeqNum");
    				cpMaster.addRampUpLineItem(cp, rampUpSeqNum);
    			}
    		}
    		super.map = cpsMap;
        }
 	}

	public String[] getAvaliableBillingFrequencyOptions() {
		return avaliableBillingFrequencyOptions;
	}

	public void setAvaliableBillingFrequencyOptions(
			String[] avaliableBillingFrequencyOptions) {
		this.avaliableBillingFrequencyOptions = avaliableBillingFrequencyOptions;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
}
