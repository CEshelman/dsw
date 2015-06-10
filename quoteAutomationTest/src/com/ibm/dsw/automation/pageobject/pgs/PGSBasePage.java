package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;

public class PGSBasePage extends BasePage {
	
	

	public PGSBasePage(WebDriver driver) {
		super(driver);
	}

	@FindBy(linkText = "Create a sales quote")
	public WebElement createSaleQuoteLink;
	
	@FindBy(linkText = "My current quote")
	public WebElement myCurrentQuoteLink;
	
	@FindBy(xpath = ".//a[contains(@href,'DISPLAY_FIND_QUOTE_BY_')]")
	public WebElement statusLink;
	
	@FindBy(linkText = "Help and tutorial")
	public WebElement helpAndTutorialLink;
	
	@FindBy(linkText = "Import a sales quote spreadsheet")
	public WebElement importSalesQuoteSpreadsheetLink;
	
	@FindBy(linkText = "Retrieve a saved sales quote")
	public WebElement retrieveSavedSalesQuoteLink;
	
	@FindBy(linkText = "Partner guided selling")
	public WebElement partnerGuidedSellingLink;
	

	public SQOHomePage gotoSQOHomePage() {
		elementClickByLinkText("Software Quote and Order");
		SQOHomePage page = new SQOHomePage(this.driver);
		loadPage(page, this.WAIT_TIME);
		
		return page;
	}
	
	public SQOMyCurrentQuotePage gotoMyCurrentQuotePage() {
		elementClickByLinkText("My current quote");
		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(this.driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
}
