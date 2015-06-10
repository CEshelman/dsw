package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;


public class SQODisplayResellerListPage extends PGSBasePage {
	@FindBy(linkText = "Return to my current quote ")
	public WebElement returnCurQuoteLink;
	
	@FindBy(linkText = "Change search criteria")
	public WebElement changeSearchCriLink;
	
	@FindBy(linkText = "Select reseller")
	public WebElement selectResellerLink;
	


	public SQODisplayResellerListPage(WebDriver driver) {
		super(driver);
	}

	public SQOMyCurrentQuotePage selectReseller() {
		loggerContxt.info("Click on 'Select reseller' link to select the first reseller on the 'Reseller selection result' page.");
		selectResellerLink.click();
		loggerContxt.info("Action done");
		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		loggerContxt.info("Navigate back to 'My current quote' page.");
		return page;
	}	
	

	
}
