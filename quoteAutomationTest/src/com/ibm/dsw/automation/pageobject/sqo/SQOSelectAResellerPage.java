package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.SelectAResellerPage;

public class SQOSelectAResellerPage extends SelectAResellerPage {
	
	@FindBy(id="name")
	public WebElement  resellerName;
	
	@FindBy(xpath = "//form/table/tbody/tr[7]/td[2]/span/input")
	public WebElement  nameSubmit;
	
	@FindBy(xpath = "//input[@aria-label='Find resellers by number'and@class='ibm-btn-arrow-sec']")
	public WebElement  numSubmit;
	
	
	
	public SQOSelectAResellerPage(WebDriver driver) {
		super(driver);
	}
	
	public SQODisplayResellerListPage displayResellerListByName(String name) {
		loggerContxt.info(String.format("Send keys '%s' to 'Reseller name: *' field on 'Reseller selection' page.", name));
		resellerName.sendKeys(name);
		loggerContxt.info("Action done");
		loggerContxt.info("Click the submit button on the 'Reseller selection' page.");
		resellerName.submit();
		loggerContxt.info("Action done");
		return new SQODisplayResellerListPage(driver);
	}
	
	public SQODisplayResellerListPage displayResellerListByName() {
		return displayResellerListByName("ecapital");
	}
	
	
	public SQODisplayResellerListPage displayResellerByNum(String resellerNum) {
		resellerNumber.sendKeys(resellerNum);
		numSubmit.click();
		SQODisplayResellerListPage page = new SQODisplayResellerListPage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}	
	
}
