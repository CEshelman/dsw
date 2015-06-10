package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;

public class CreateCustomerPage extends PGSBasePage {

	public CreateCustomerPage(WebDriver driver) {
		super(driver);
	}
	
	public void fillCustomerForm() {
		// Company Information
		selectedOptionByValue("lob", "PAE", getDriver());
		setValueById("companyName", "companyName");
		setValueById("address1", "address1");
		setValueById("address2", "address2");
		setValueById("city", "city");
		selectedOptionByValue("state", "TX", getDriver());
		setValueById("postalCode", "postalCode");
		setValueById("vatNum", "vatNum");
		selectedOptionByValue("industryIndicator", "04", getDriver());
		elementClickById("oneToK");
		
		// Primary contact information
		setValueById("cntFirstName", "cntFirstName");
		setValueById("cntLastName", "cntLastName");
		setValueById("cntPhoneNumFull", "111111");
		setValueById("cntEmailAdr", "suchang713@126.com");
		
		// Language preferences
		selectedOptionByValue("commLanguage", "EN", getDriver());
		selectedOptionByValue("mediaLanguage", "EN", getDriver());
	}
	
	public MyCurrentQuotePage submitCreateCustomer() {
		elementClickByName("ibm-submit");
		MyCurrentQuotePage page = new MyCurrentQuotePage(driver);
		return page;
	}

}
