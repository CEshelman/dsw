package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;

public class RetrieveSavedSalesQuotePage extends BasePage {
	
	public RetrieveSavedSalesQuotePage(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//input[@name='ibm-submit']")
	public WebElement continueBtn;
	@FindBy(partialLinkText = "open")
	public WebElement open_link;
	
	
	
	public MyCurrentQuotePage goViewDetailSavedQuote() {
		selectedOptionByValue("timeFilter","1",driver);
		continueBtn.click();
//		RetrieveSavedSalesQuotePage page = new RetrieveSavedSalesQuotePage(driver);
		open_link.click();
		switchToAlert(true, getClass().getSimpleName());
		
		MyCurrentQuotePage page = new PGSMyCurrentQuotePage(driver);
		return page;
	}
	
}
