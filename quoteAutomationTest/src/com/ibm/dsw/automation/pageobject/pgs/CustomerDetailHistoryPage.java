package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class CustomerDetailHistoryPage extends PGSBasePage {
	
	@FindBy(linkText = "Active Software as a Service")
	public WebElement activeSoftwareAsaService;
	
	@FindBy(linkText = "Inactive Software as a Service")
	public WebElement inactiveSoftwareAsaService;
	
	public CustomerDetailHistoryPage(WebDriver driver) {
		super(driver);
	}

	public void goInactiveSoftwareAsaService() {
		inactiveSoftwareAsaService.click();
	}	
	
}
