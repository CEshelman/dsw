package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;

public class ApplianceAddress implements Serializable {

	private String custName;
	private String custAddress;
	private String addressIntrnl;
	private String custCity;
	private String sapRegionCode;
	private String postalCode;
	private String cntryCode;
	private String custNum ;
	private String sapCntId ;
	private int webCustId;
	private Integer quoteLineItemSeqNum;
	private String cntFirstName;
	private String cntLastName;
	private String sapIntlPhoneNumFull;
	private int cntId;
	private Integer secId;
	private int addressType;
	private boolean headLevelAddr = false;
	// whether do or not display  customer infor and company address in PGS quote
	private boolean isShowPGSInfor = true;
	
	private String addByUserName;
	
	private boolean addrBaseMTM = false;
	
	public boolean isAddrBaseMTM() {
		return addrBaseMTM;
	}
	public void setAddrBaseMTM(boolean addrBaseMTM) {
		this.addrBaseMTM = addrBaseMTM;
	}

	private transient List<ApplianceLineItem> lineItemList = new ArrayList<ApplianceLineItem>();
	
	private String quoteLineItemSeqNumStr = "";  //appliance part sequence number string eg, sequence number1,sequence number2,sequence number3
	
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCustAddress() {
		return custAddress;
	}
	public void setCustAddress(String custAddress) {
		this.custAddress = custAddress;
	}
	public String getAddressIntrnl() {
		return addressIntrnl;
	}
	public void setAddressIntrnl(String addressIntrnl) {
		this.addressIntrnl = addressIntrnl;
	}
	public String getCustCity() {
		return custCity;
	}
	public void setCustCity(String custCity) {
		this.custCity = custCity;
	}
	public String getSapRegionCode() {
		return sapRegionCode;
	}
	public void setSapRegionCode(String sapRegionCode) {
		this.sapRegionCode = sapRegionCode;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getCntryCode() {
		return cntryCode;
	}
	public void setCntryCode(String cntryCode) {
		this.cntryCode = cntryCode;
	}
	public String getCustNum() {
		return custNum;
	}
	public void setCustNum(String custNum) {
		this.custNum = custNum;
	}
	public int getWebCustId() {
		return webCustId;
	}
	public void setWebCustId(int webCustId) {
		this.webCustId = webCustId;
	}
	public Integer getQuoteLineItemSeqNum() {
		return quoteLineItemSeqNum;
	}
	public void setQuoteLineItemSeqNum(Integer quoteLineItemSeqNum) {
		this.quoteLineItemSeqNum = quoteLineItemSeqNum;
	}
	public String getCntFirstName() {
		return cntFirstName;
	}
	public void setCntFirstName(String cntFirstName) {
		this.cntFirstName = cntFirstName;
	}
	public String getCntLastName() {
		return cntLastName;
	}
	public void setCntLastName(String cntLastName) {
		this.cntLastName = cntLastName;
	}
	public String getSapIntlPhoneNumFull() {
		return sapIntlPhoneNumFull;
	}
	public void setSapIntlPhoneNumFull(String sapIntlPhoneNumFull) {
		this.sapIntlPhoneNumFull = sapIntlPhoneNumFull;
	}
	public int getCntId() {
		return cntId;
	}
	public void setCntId(int cntId) {
		this.cntId = cntId;
	}
	public Integer getSecId() {
		return secId;
	}
	public void setSecId(Integer secId) {
		this.secId = secId;
	}
	public List<ApplianceLineItem> getLineItemList() {
		return lineItemList;
	}
	public void setLineItemList(List<ApplianceLineItem> lineItemList) {
		this.lineItemList = lineItemList;
	}
	
	public int getAddressType() {
		return addressType;
	}
	public void setAddressType(int addressType) {
		this.addressType = addressType;
	}
	public String getQuoteLineItemSeqNumStr() {
		return quoteLineItemSeqNumStr;
	}
	public void setQuoteLineItemSeqNumStr(String quoteLineItemSeqNumStr) {
		this.quoteLineItemSeqNumStr = quoteLineItemSeqNumStr;
	}
	public boolean isHeadLevelAddr() {
		return headLevelAddr;
	}
	public void setHeadLevelAddr(boolean headLevelAddr) {
		this.headLevelAddr = headLevelAddr;
	}
	public String getSapCntId() {
		return sapCntId;
	}
	public void setSapCntId(String sapCntId) {
		this.sapCntId = sapCntId;
	}
	public boolean isShowPGSInfor() {
		return isShowPGSInfor;
	}
	public void setShowPGSInfor(boolean isShowPGSInfor) {
		this.isShowPGSInfor = isShowPGSInfor;
	}
	public String getAddByUserName() {
		return addByUserName;
	}
	public void setAddByUserName(String addByUserName) {
		this.addByUserName = addByUserName;
	}
	
	public static boolean validParams(Map<String,String> map){
		Iterator<Entry<String, String>>  it = map.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, String> entry = it.next();
			if(ParamKeys.PARAM_QUOTE_NUM.equals(entry.getKey())){
				if(!StringUtils.isNumeric(entry.getValue()))
					return false;
			}
			if(ParamKeys.PARAM_COUNTRY.equals(entry.getKey())){
				String regex="^[A-Za-z]+$"; 
				Pattern pattern=Pattern.compile(regex);
				Matcher matcher=pattern.matcher(entry.getValue()); 		
				if(entry.getValue()==null || entry.getValue().length()>3 || !matcher.find())
					return false;
			}
			if(ParamKeys.PARAM_LINE_OF_BUSINESS.equals(entry.getKey())){
				if("PA".equalsIgnoreCase(entry.getValue())){
					continue;
				}else if("FCT".equalsIgnoreCase(entry.getValue())){
					continue;
				}else if("PAUN".equalsIgnoreCase(entry.getValue())){
					continue;
				}else if("PAE".equalsIgnoreCase(entry.getValue())){
					continue;
				}else if("PPSS".equalsIgnoreCase(entry.getValue())){
					continue;
				}else if("OEM".equalsIgnoreCase(entry.getValue())){
					continue;
				}else if("SSP".equalsIgnoreCase(entry.getValue())){
					continue;
				}else {
					return false;
				}
			}
			if(ParamKeys.PARAM_ADDRESS_TYPE.equals(entry.getKey())){
				if(entry.getValue()==null || entry.getValue().length()>1 || !(entry.getValue().equals(DraftQuoteConstants.APPLIANCE_SHIPTO_ADDTYPE) || entry.getValue().equals(DraftQuoteConstants.APPLIANCE_INSTALLAT_ADDTYPE)))
					return false;
			}
			if(ParamKeys.PARAM_AGREEMENT_NUM.equals(entry.getKey())){
				if(entry.getValue()!= null){
					if(!(entry.getValue().equals("null") || entry.getValue().equals("")  || StringUtils.isNumeric(entry.getValue())))
						return false;
				}
			}
			if(ParamKeys.PARAM_IS_SBMT_QT.equals(entry.getKey())){
				if(entry.getValue()!= null && !(entry.getValue().equals("null") || entry.getValue().equals("")  || entry.getValue().equals("true") || entry.getValue().equals("false")))
					return false;
			}
			if(ParamKeys.PARAM_SEARCH_METHOD.equals(entry.getKey())){
				if(!StringUtils.isNumeric(entry.getValue()))
					return false;
			}
		}
		return true;
	}
	
	public boolean compareMTMAddr(ApplianceAddress temp)
	{
    	if ( !StringUtils.equals(this.getCustNum(), temp.getCustNum()) )
    	{
    		return false;
    	}
    	
    	if ( !StringUtils.equals(this.getCustName(), temp.getCustName()) )
    	{
    		return false;
    	}
    	
    	if ( !StringUtils.equals(this.getPostalCode(), temp.getPostalCode()) )
    	{
    		return false;
    	}
    	
    	if ( !StringUtils.equals(this.getCustCity(), temp.getCustCity()) )
    	{
    		return false;
    	}
    	
    	if ( !StringUtils.equals(this.getSapRegionCode(), temp.getSapRegionCode()) )
    	{
    		return false;
    	}
    	
    	return true;
	}
	
	public void addLineItem(QuoteLineItem item)
	{
		if ( lineItemList == null )
		{
			lineItemList = new ArrayList<ApplianceLineItem>();
		}
		lineItemList.add(ApplianceLineItem.createApplianceLineItem(item));
	}
	
}
