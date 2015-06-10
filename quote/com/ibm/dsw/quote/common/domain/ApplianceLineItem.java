package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.config.PartPriceConstants;

public class ApplianceLineItem implements Serializable {

	private Integer quoteLineItemSeqNum;
	private Integer destSeqNum;
	private Integer quoteSectnSeqNum;
	private int partQty;
	private String configrtnId;
	private String transceiver;
	private String ccDscr;
	private String partNum;
	private Date custReqstdArrivlDate;
	private String partDscrLong;
	private int sapLineItemSeqNum;
	private String appliance;
	
	private transient ApplianceAddress shipToAddr;
	private transient ApplianceAddress installAtAddr;
	
	private boolean isAppliancePart = false;
	
	private String machineType;
    private String model;
    private String serialNumber;
    private boolean renewalPart;
    
    private String renewalQuoteNum;

    private int renewalQuoteSeqNum;
    
    private Integer serialNumWarningFlag;
    
    private boolean useSoldTo = false;
	
	public static Map assemblyLineItemMap(Map lineItemMap){
		
		Map returnMap = new HashMap();
		if(!lineItemMap.isEmpty()){
			Iterator it = lineItemMap.keySet().iterator();
			List<ApplianceLineItem> itemList = new ArrayList<ApplianceLineItem>();
			while(it.hasNext()){
				ApplianceLineItem line = (ApplianceLineItem) lineItemMap.get((String)it.next());
				if(line != null){
					line.setAppliancePart(true);
					itemList.add(line);
					returnMap.put(String.valueOf(line.getQuoteLineItemSeqNum()), line);
				}
			}
			Set<String> lineNumSet = ApplianceLineItem.getHiddenAppliLineNumSet(lineItemMap);
			
			if(!lineNumSet.isEmpty()){
				Iterator<String> setIt = lineNumSet.iterator();
				while(setIt.hasNext()){
					returnMap.remove(setIt.next());
				}
			}
		}
		return returnMap;
	}
	
	/**
	 * 
	 * @param lineItemMap all line items in the quote
	 * @return
	 */
	public static Set<String> getHiddenAppliLineNumSet(Map lineItemMap){
		
		Set<String> lineNumSet = new HashSet<String>();
		if(!lineItemMap.isEmpty()){
			Iterator it = lineItemMap.keySet().iterator();
			List<ApplianceLineItem> itemList = new ArrayList<ApplianceLineItem>();
			while(it.hasNext()){
				ApplianceLineItem line = (ApplianceLineItem) lineItemMap.get((String)it.next());
				if(line != null){
					itemList.add(line);
				}
			}
			if(itemList.size()>1){
				for(int i=0; i<itemList.size()-1; i++){
					ApplianceLineItem lineI = itemList.get(i);
					for(int j=i+1; j<itemList.size() ; j++){
						if(StringUtils.isBlank(lineI.getConfigrtnId())){
							break;
						}
						ApplianceLineItem lineJ = itemList.get(j);
						if(StringUtils.isNotBlank(lineJ.getConfigrtnId())
								&&!PartPriceConstants.APPLNC_NOT_ON_QUOTE.equalsIgnoreCase(lineJ.getConfigrtnId().trim())
								&&!PartPriceConstants.APPLNC_NOT_ON_QUOTE.equalsIgnoreCase(lineI.getConfigrtnId().trim())
								&& StringUtils.equals(lineJ.getConfigrtnId(), lineI.getConfigrtnId())){
							if(lineJ.isAppliancePart() && !StringUtils.equals(lineJ.getAppliance(), "1")  ){
								lineNumSet.add(String.valueOf( lineJ.getQuoteLineItemSeqNum() ));
							}
							if(lineI.isAppliancePart() && !StringUtils.equals(lineI.getAppliance(), "1") ){
								lineNumSet.add(String.valueOf( lineI.getQuoteLineItemSeqNum() ));
							}
						}
						
					}
				}
			}
			
		}
		return lineNumSet;
	}
	
	public boolean isUseSoldTo() {
		return useSoldTo;
	}

	public void setUseSoldTo(boolean useSoldTo) {
		this.useSoldTo = useSoldTo;
	}

	public void copyAddr(ApplianceAddress addr,boolean type){
		if(type)
			this.shipToAddr= addr;
		else
			this.installAtAddr= addr;
	}
	
	public Integer getQuoteLineItemSeqNum() {
		return quoteLineItemSeqNum;
	}
	public void setQuoteLineItemSeqNum(Integer quoteLineItemSeqNum) {
		this.quoteLineItemSeqNum = quoteLineItemSeqNum;
	}
	public int getPartQty() {
		return partQty;
	}
	public void setPartQty(int partQty) {
		this.partQty = partQty;
	}
	public String getConfigrtnId() {
		return configrtnId;
	}
	public void setConfigrtnId(String configrtnId) {
		this.configrtnId = configrtnId;
	}
	public String getTransceiver() {
		return transceiver;
	}
	public void setTransceiver(String transceiver) {
		this.transceiver = transceiver;
	}
	public String getCcDscr() {
		return ccDscr;
	}
	public void setCcDscr(String ccDscr) {
		this.ccDscr = ccDscr;
	}
	public Integer getQuoteSectnSeqNum() {
		return quoteSectnSeqNum;
	}
	public void setQuoteSectnSeqNum(Integer quoteSectnSeqNum) {
		this.quoteSectnSeqNum = quoteSectnSeqNum;
	}
	public String getPartNum() {
		return partNum;
	}
	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}
	public Date getCustReqstdArrivlDate() {
		return custReqstdArrivlDate;
	}
	public void setCustReqstdArrivlDate(Date custReqstdArrivlDate) {
		this.custReqstdArrivlDate = custReqstdArrivlDate;
	}
	public String getPartDscrLong() {
		return partDscrLong;
	}
	public void setPartDscrLong(String partDscrLong) {
		this.partDscrLong = partDscrLong;
	}
	public ApplianceAddress getShipToAddr() {
		return shipToAddr;
	}
	public void setShipToAddr(ApplianceAddress shipToAddr) {
		this.shipToAddr = shipToAddr;
	}
	public ApplianceAddress getInstallAtAddr() {
		return installAtAddr;
	}
	public void setInstallAtAddr(ApplianceAddress installAtAddr) {
		this.installAtAddr = installAtAddr;
	}
	public Integer getDestSeqNum() {
		return destSeqNum;
	}
	public void setDestSeqNum(Integer destSeqNum) {
		this.destSeqNum = destSeqNum;
	}

	public boolean isAppliancePart() {
		return isAppliancePart;
	}

	public void setAppliancePart(boolean isAppliancePart) {
		this.isAppliancePart = isAppliancePart;
	}

	public String getAppliance() {
		return appliance;
	}

	public String getMachineType() {
		return machineType;
	}

	public void setMachineType(String machineType) {
		this.machineType = machineType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public boolean isRenewalPart() {
		return renewalPart;
	}

	public void setRenewalPart(boolean renewalPart) {
		this.renewalPart = renewalPart;
	}

	public String getRenewalQuoteNum() {
		return renewalQuoteNum;
	}

	public void setRenewalQuoteNum(String renewalQuoteNum) {
		this.renewalQuoteNum = renewalQuoteNum;
	}

	public int getRenewalQuoteSeqNum() {
		return renewalQuoteSeqNum;
	}

	public void setRenewalQuoteSeqNum(int renewalQuoteSeqNum) {
		this.renewalQuoteSeqNum = renewalQuoteSeqNum;
	}

	public Integer getSerialNumWarningFlag() {
		return serialNumWarningFlag;
	}

	public void setSerialNumWarningFlag(Integer serialNumWarningFlag) {
		this.serialNumWarningFlag = serialNumWarningFlag;
	}

	public void setAppliance(String appliance) {
		this.appliance = appliance;
	}

	public int getSapLineItemSeqNum() {
		return sapLineItemSeqNum;
	}

	public void setSapLineItemSeqNum(int sapLineItemSeqNum) {
		this.sapLineItemSeqNum = sapLineItemSeqNum;
	}

	public void copy(ApplianceLineItem itLineOrg) {
		this.appliance = itLineOrg.getAppliance();
		this.transceiver = itLineOrg.getTransceiver();
		this.quoteLineItemSeqNum = itLineOrg.getQuoteLineItemSeqNum();
		this.quoteSectnSeqNum = itLineOrg.getQuoteSectnSeqNum();
	}
	
	public static ApplianceLineItem createApplianceLineItem(QuoteLineItem item)
	{
		ApplianceLineItem applncItem = new ApplianceLineItem();
		applncItem.setQuoteLineItemSeqNum(item.getSeqNum());
		applncItem.setDestSeqNum(item.getDestSeqNum());
		applncItem.setConfigrtnId(item.getConfigrtnId());
		applncItem.setPartNum(item.getPartNum());
		applncItem.setQuoteSectnSeqNum(item.getQuoteSectnSeqNum());
		applncItem.setPartDscrLong(item.getPartDesc());
		applncItem.setCustReqstdArrivlDate(item.getLineItemCRAD());
		applncItem.setAppliancePart(item.isApplncPart());
		
		applncItem.setModel(item.getModel());
		applncItem.setMachineType(item.getMachineType());
		applncItem.setSerialNumber(item.getSerialNumber());
		applncItem.setRenewalPart(item.isRenewalPart());
		applncItem.setRenewalQuoteNum(item.getRenewalQuoteNum());
		applncItem.setRenewalQuoteSeqNum(item.getRenewalQuoteSeqNum());
		
		applncItem.setSapLineItemSeqNum(item.getSapLineItemSeqNum());
		
		return applncItem;
	}
}
