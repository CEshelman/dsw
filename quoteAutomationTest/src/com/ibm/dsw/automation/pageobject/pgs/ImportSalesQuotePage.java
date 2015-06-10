package com.ibm.dsw.automation.pageobject.pgs;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;

public class ImportSalesQuotePage extends PGSBasePage {
	
	public ImportSalesQuotePage(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(linkText = "Status")
	public WebElement statusLnk;

	
	public PGSStatusSalesQuote goStatus() {

		statusLnk.click();	
		PGSStatusSalesQuote page = new PGSStatusSalesQuote(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public PGSHomePage goHomePage() {

		elementClickByLinkText("Partner guided selling");
		PGSHomePage page = new PGSHomePage(this.driver);
		loadPage(page, this.WAIT_TIME);
		
		return page;
	}
	
	public SQOMyCurrentQuotePage goToSQOMyCurrentQuotePage() {
		
		loggerContxt.info("Click My current quote link .....");
		myCurrentQuoteLink.click();
		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		return page;
	}
}
