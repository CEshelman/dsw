package com.ibm.dsw.quote.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vivian
 * the rule for validate 
 */
public class BidIterationRule {
	public BidIterationRule(){
		
	}
	
	int softwareValidationResult;
	
	int saasValidationResult;
	
	int monthlyValidationResult;
	
	List <String>softwareErrorCodeList = new ArrayList<String>();
	
	List <String>saasErrorCodeList = new ArrayList<String>();
	
	List<String> monthlyErrorCodeList = new ArrayList<String>();

	public int getSoftwareValidationResult() {
		return softwareValidationResult;
	}

	public void setSoftwareValidationResult(int softwareValidationResult) {
		this.softwareValidationResult = softwareValidationResult;
	}

	public int getSaasValidationResult() {
		return saasValidationResult;
	}

	public void setSaasValidationResult(int saasValidationResult) {
		this.saasValidationResult = saasValidationResult;
	}

	public List<String> getSoftwareErrorCodeList() {
		return softwareErrorCodeList;
	}

	public void setSoftwareErrorCodeList(List<String> softwareErrorCodeList) {
		this.softwareErrorCodeList = softwareErrorCodeList;
	}

	public List<String> getSaasErrorCodeList() {
		return saasErrorCodeList;
	}

	public void setSaasErrorCodeList(List<String> saasErrorCodeList) {
		this.saasErrorCodeList = saasErrorCodeList;
	}

	public int getMonthlyValidationResult() {
		return monthlyValidationResult;
	}

	public void setMonthlyValidationResult(int monthlyValidationResult) {
		this.monthlyValidationResult = monthlyValidationResult;
	}

	public List<String> getMonthlyErrorCodeList() {
		return monthlyErrorCodeList;
	}

	public void setMonthlyErrorCodeList(List<String> monthlyErrorCodeList) {
		this.monthlyErrorCodeList = monthlyErrorCodeList;
	}

	
}
