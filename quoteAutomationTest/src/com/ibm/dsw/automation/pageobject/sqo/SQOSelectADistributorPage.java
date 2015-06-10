package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.SelectADistributorPage;

public class SQOSelectADistributorPage extends SelectADistributorPage {
	@FindBy(id = "name")
	public WebElement  distributorName;
	
	@FindBy(xpath = "//input[@aria-label='Find distributors by attributes']")
	public WebElement  nameSubmit;
	
	@FindBy(xpath = "//input[@aria-label='Find distributors by number']")
	public WebElement  numSubmit;
	
	
	
	public SQOSelectADistributorPage(WebDriver driver) {
		super(driver);
	}
	
	public SQODisplayDistributorListPage displayCustomerListByName() {
		return displayCustomerListByName("avnet");
	}
	public SQODisplayDistributorListPage displayCustomerListByName(String distName) {
		loggerContxt.info(String.format("Send keys '%s' to 'Distributor name:*' field on 'Distributor selection' page.", distName));
		distributorName.sendKeys(distName);
		loggerContxt.info("Action done");
		loggerContxt.info("Click on the submit button on 'Distributor selection' page.");
		nameSubmit.click();
		loggerContxt.info("Action done");
		SQODisplayDistributorListPage page = new SQODisplayDistributorListPage(driver);
		loadPage(page,this.WAIT_TIME);
		loggerContxt.info("Navigate to 'Distributor selection result' page.");
		return page;
	}	
	public SQODisplayDistributorListPage displayDistributorListBySiteNum(String number ) {
		distributorNumber.sendKeys(number);
		numSubmit.click();
		SQODisplayDistributorListPage page = new SQODisplayDistributorListPage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}
}
