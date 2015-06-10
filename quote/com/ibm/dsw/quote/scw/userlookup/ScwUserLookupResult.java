package com.ibm.dsw.quote.scw.userlookup;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name="UserLookupReturnResult")
@XmlAccessorType(XmlAccessType.NONE)
public class ScwUserLookupResult {
	
	@XmlElement(name="ResultStatus")
    private String resultStatus;
	
	@XmlElement(name="IsInputSiteValid")
	private String isInputSiteValid;
	
	@XmlElement(name="IsInputCAValid")
	private String isInputCAValid;
	
	@XmlElement(name="IsInputConfigValid")
	private String isInputConfigValid;
	
	@XmlElementWrapper(name="Customers")
	@XmlElement(name="Customer")
    private List<ScwUserLookupCustResult> scwUserLookupCustResultList;

	@XmlElementWrapper(name="Contracts")
	@XmlElement(name="Contract")
    private List<ScwUserLookupContractResult> scwUserLookupContractResultList;

	@XmlElementWrapper(name="CAs")
	@XmlElement(name="CA")
    private List<ScwUserLookupCAResult> scwUserLookupCAResultList;
	

	
	@XmlElementWrapper(name="Configs")
	@XmlElement(name="Config")
    private List<ScwUserLookupConfigResult> scwUserLookupConfigResultList;

	@XmlElementWrapper(name="ConfigDetails")
	@XmlElement(name="ConfigDetail")
    private List<ScwUserLookupConfigDetailResult> scwUserLookupConfigDetailResultList;
	
	
    public String getIsInputConfigValid(){
    	return this.isInputConfigValid;
    }
    
    public void setIsInputConfigValid(String flag){
    	this.isInputConfigValid = flag;
    }
	
    public String getIsInputCAValid(){
    	return this.isInputCAValid;
    }
    
    public void setIsInputCAValid(String flag){
    	this.isInputCAValid = flag;
    }
    
    public String getIsInputSiteValid(){
    	return this.isInputSiteValid;
    }
    
    public void setIsInputSiteValid(String flag){
    	this.isInputSiteValid = flag;
    }

	public void addCAResult(ScwUserLookupCAResult result) {
		if (this.scwUserLookupCAResultList == null)
			this.scwUserLookupCAResultList = new ArrayList<ScwUserLookupCAResult>();
		this.scwUserLookupCAResultList.add(result);
	}

	
	public List<ScwUserLookupCAResult> getScwUserLookupCAResultList() {
		return this.scwUserLookupCAResultList;
	}
	
	public void setScwUserLookupCAResultList(List<ScwUserLookupCAResult> resultList) {
		if (this.scwUserLookupCAResultList == null)
			this.scwUserLookupCAResultList = new ArrayList<ScwUserLookupCAResult>();
		if (resultList != null) {
			this.scwUserLookupCAResultList.addAll(resultList);
		}
	}
	

	
	public void addConfigResult(ScwUserLookupConfigResult result) {
		if (this.scwUserLookupConfigResultList == null)
			this.scwUserLookupConfigResultList = new ArrayList<ScwUserLookupConfigResult>();
		this.scwUserLookupConfigResultList.add(result);
	}


	public List<ScwUserLookupConfigResult> getScwUserLookupConfigResultList() {
		return this.scwUserLookupConfigResultList;
	}
	
	public void setScwUserLookupConfigResultList(List<ScwUserLookupConfigResult> resultList) {
		if (this.scwUserLookupConfigResultList == null)
			this.scwUserLookupConfigResultList = new ArrayList<ScwUserLookupConfigResult>();
		if (resultList != null) {
			this.scwUserLookupConfigResultList.addAll(resultList);
		}
	}
	
	public void addContractResult(ScwUserLookupContractResult result) {
		if (this.scwUserLookupContractResultList == null)
			this.scwUserLookupContractResultList = new ArrayList<ScwUserLookupContractResult>();
		this.scwUserLookupContractResultList.add(result);
	}


	public List<ScwUserLookupContractResult> getScwUserLookupContractResultList() {
		return this.scwUserLookupContractResultList;
	}
	
	public void setScwUserLookupContractResultList(List<ScwUserLookupContractResult> resultList) {
		if (this.scwUserLookupContractResultList == null)
			this.scwUserLookupContractResultList = new ArrayList<ScwUserLookupContractResult>();
		if (resultList != null) {
			this.scwUserLookupContractResultList.addAll(resultList);
		}
	}
	

	public void addCustResult(ScwUserLookupCustResult result) {
		if (this.scwUserLookupCustResultList == null)
			this.scwUserLookupCustResultList = new ArrayList<ScwUserLookupCustResult>();
		this.scwUserLookupCustResultList.add(result);
	}
	

	public List<ScwUserLookupCustResult> getScwUserLookupCustResultList() {
		return this.scwUserLookupCustResultList;
	}
	
	public void setScwUserLookupCustResultList(List<ScwUserLookupCustResult> resultList) {
		if (this.scwUserLookupCustResultList == null)
			this.scwUserLookupCustResultList = new ArrayList<ScwUserLookupCustResult>();
		if (resultList != null) {
			this.scwUserLookupCustResultList.addAll(resultList);
		}
	}


	public List<ScwUserLookupConfigDetailResult> getScwUserLookupConfigDetailResultList() {
		return scwUserLookupConfigDetailResultList;
	}


	public void setScwUserLookupConfigDetailResultList(
			List<ScwUserLookupConfigDetailResult> resultList) {
		if (this.scwUserLookupConfigDetailResultList == null)
			this.scwUserLookupConfigDetailResultList = new ArrayList<ScwUserLookupConfigDetailResult>();
		this.scwUserLookupConfigDetailResultList.addAll(resultList);
	}

	public void addScwUserLookupConfigDetailResultList(ScwUserLookupConfigDetailResult result) {
		if (this.scwUserLookupConfigDetailResultList == null)
			this.scwUserLookupConfigDetailResultList = new ArrayList<ScwUserLookupConfigDetailResult>();
		this.scwUserLookupConfigDetailResultList.add(result);
	}

	public String getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}

	



}
