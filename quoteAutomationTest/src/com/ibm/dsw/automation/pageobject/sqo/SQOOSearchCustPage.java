package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.FindExistingCustomerPage;


public class SQOOSearchCustPage extends FindExistingCustomerPage {

	
	public WebElement customerName;
	
	@FindBy(id = "siteNumber")
	public WebElement siteNumber;

	@FindBy(xpath="//form/table[2]/tbody/tr[5]/td/span/input")
	public WebElement nameSubmit;
	
	@FindBy(xpath="//input[@class='ibm-btn-arrow-sec'and@aria-label='Find customers by number']")
	public WebElement siteNumSubmit;

	
	public SQOOSearchCustPage(WebDriver driver) {
		super(driver);
	}
	
	public SQODisplayCustListPage displayCustomerListByName(String name){
		loggerContxt.info(String.format("Send keys '%s' to 'Partial or complete customer name:*' field.", name));
		customerName.sendKeys(name);
		loggerContxt.info("Action done.");
		loggerContxt.info("Click on the 'Submit' button on the 'Customer selection and creation' page.");
		customerName.submit();
		loggerContxt.info("Action done");
		SQODisplayCustListPage page = new SQODisplayCustListPage(driver);
		loadPage(page,this.WAIT_TIME);
		loggerContxt.info("Customer search result page dispalyed in 'Customer selection and creation' page.");
		return page;
	}

	public SQODisplayCustListPage displayCustomerListByName() {
		return displayCustomerListByName("Abbott");
	}	
	

	
	public SQODisplayCustListPage displaySQOCustomerListBySiteNum(String sitNum) {
		this.siteNumber.sendKeys(sitNum);
		siteNumSubmit.click();
		SQODisplayCustListPage page = new SQODisplayCustListPage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}	
	
}
