package com.ibm.dsw.quote.dsj.contract;

import java.util.HashMap;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.dsj.util.DsjKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

public class DsjBaseContract extends QuoteBaseContract {
	// input from Sales connect
	private String channel = null;
	private String returnURL = null;
	private String requestID = null;
	private String opptNum = null;
	private String opptOwnerMail = null;
	private String description = null;
	private String customerName = null;
	private String resellerName = null;
	private String customerID = null;
	private String trialID = null;
	private String cntryCode = null;
	
	//values during DSJ process
	private String lob = null;
	private String country = null;
	private String acquisition = null;
	private String opprtntyNum = null;
	private String oppOwnerEmailAddr = null;
	private String briefTitle = null;
	private String custQueryName = null;
	private String resellerQueryName = null;
	private String webQuoteNumber = null;
	private String customerNum = null;
		

    public void load(Parameters parameters, JadeSession session) {
    	super.load(parameters, session);
    	HashMap<String, String> dsjParameters = (HashMap<String, String>) session.getAttribute(DsjKeys.dsjParameters);
    	if(dsjParameters!=null && dsjParameters.size()>0){
    		this.setOpptNum(dsjParameters.get("opptNum"));
    		this.setOpptOwnerMail(dsjParameters.get("opptOwnerMail"));
    		this.setCustomerName(dsjParameters.get("customerName"));
    		this.setCustomerID(dsjParameters.get("customerID"));
    		this.setResellerName(dsjParameters.get("resellerName"));
    		this.setDescription(dsjParameters.get("description"));
    		this.setTrialID(dsjParameters.get("trialID"));
    		this.setCntryCode(dsjParameters.get("cntryCode"));
    		
    		
    		this.setOpprtntyNum(this.getOpptNum());
    		this.setOppOwnerEmailAddr(this.getOpptOwnerMail());
    		this.setCustQueryName(this.getCustomerName());
    		this.setCustomerNum(this.getCustomerID());
    		this.setResellerQueryName(this.getResellerName());
    		this.setBriefTitle(this.getDescription());
    		this.setCountry(this.getCntryCode());
    		session.removeValue(DsjKeys.dsjParameters);
    	}
    }	
    
    
    
	

	//getter setter methods
	
    public String getCntryCode() {
		return cntryCode;
	}





	public void setCntryCode(String cntryCode) {
		this.cntryCode = cntryCode;
	}





	public String getTrialID() {
		return trialID;
	}





	public void setTrialID(String trialID) {
		this.trialID = trialID;
	}





	public String getCustomerID() {
		return customerID;
	}





	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}





	public String getCustomerNum() {
		return customerNum;
	}





	public void setCustomerNum(String customerNum) {
		this.customerNum = customerNum;
	}





	/**
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * Be careful<br>
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * This is input coming from Sales Connect
     * @return
     */
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
    /**
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * Be careful<br>
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * This is input coming from Sales Connect
     * @return
     */
	public String getReturnURL() {
		return returnURL;
	}
	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}
    /**
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * Be careful<br>
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * This is input coming from Sales Connect
     * @return
     */
	public String getRequestID() {
		return requestID;
	}
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
    /**
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * Be careful<br>
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * This is input coming from Sales Connect
     * @return
     */
	public String getOpptNum() {
		return opptNum;
	}
	public void setOpptNum(String opptNum) {
		this.opptNum = opptNum;
	}
    /**
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * Be careful<br>
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * This is input coming from Sales Connect
     * @return
     */
	public String getOpptOwnerMail() {
		return opptOwnerMail;
	}
	public void setOpptOwnerMail(String opptOwnerMail) {
		this.opptOwnerMail = opptOwnerMail;
	}
    /**
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * Be careful<br>
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * This is input coming from Sales Connect
     * @return
     */
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
    /**
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * Be careful<br>
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * This is input coming from Sales Connect
     * @return
     */
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
    /**
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * Be careful<br>
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * This is input coming from Sales Connect
     * @return
     */
	public String getResellerName() {
		return resellerName;
	}
	public void setResellerName(String resellerName) {
		this.resellerName = resellerName;
	}
	public String getLob() {
		return lob;
	}
	public void setLob(String lob) {
		this.lob = lob;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getAcquisition() {
		return acquisition;
	}
	public void setAcquisition(String acquisition) {
		this.acquisition = acquisition;
	}
	public String getOpprtntyNum() {
		return opprtntyNum;
	}
	public void setOpprtntyNum(String opprtntyNum) {
		this.opprtntyNum = opprtntyNum;
	}
	public String getOppOwnerEmailAddr() {
		return oppOwnerEmailAddr;
	}
	public void setOppOwnerEmailAddr(String oppOwnerEmailAddr) {
		this.oppOwnerEmailAddr = oppOwnerEmailAddr;
	}
	public String getBriefTitle() {
		return briefTitle;
	}
	public void setBriefTitle(String briefTitle) {
		this.briefTitle = briefTitle;
	}
	public String getCustQueryName() {
		return custQueryName;
	}
	public void setCustQueryName(String custQueryName) {
		this.custQueryName = custQueryName;
	}
	public String getResellerQueryName() {
		return resellerQueryName;
	}
	public void setResellerQueryName(String resellerQueryName) {
		this.resellerQueryName = resellerQueryName;
	}
	public String getWebQuoteNumber() {
		return webQuoteNumber;
	}
	public void setWebQuoteNumber(String webQuoteNumber) {
		this.webQuoteNumber = webQuoteNumber;
	}
	
	
	
	
	
	
}
