package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQOQuoteSearchIBMSelfPage extends PGSBasePage {

	@FindBy(xpath = "//input[@name='ibm-submit']")
	public WebElement ibm_submit;
	
	@FindBy(xpath = "//input[contains(@id,'Editor')]")
	public WebElement roleEditor;
	
	public SQOQuoteSearchIBMSelfPage(WebDriver driver) {
		super(driver);
	}

	public SQORQSearchReslutPage searchRenewalQuotesByIBMer() {
		roleEditor.click();
		selectedOptionByLabel("RQS", "Open");
		ibm_submit.click();
		
		SQORQSearchReslutPage page = new SQORQSearchReslutPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public SQORQSearchReslutPage searchPARenewalQuotesByIBMer() {
		selectedOptionByLabel("QUOTE_TYPE", "Passport Advantage/Passport Advantage Express");
		
		return searchRenewalQuotesByIBMer();
	}
}
