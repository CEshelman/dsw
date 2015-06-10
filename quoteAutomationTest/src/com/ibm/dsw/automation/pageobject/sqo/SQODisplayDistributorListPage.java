package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;


public class SQODisplayDistributorListPage extends PGSBasePage {

	@FindBy(linkText = "Return to my current quote ")
	public WebElement returnCurQuoteLink;
	
	@FindBy(linkText = "Change search criteria")
	public WebElement changeSearchCriLink;
	
	@FindBy(linkText = "Select distributor")
	public WebElement selectDistributorLink;
	
	@FindBy(linkText = " View customer details and history")
	public WebElement viewCustDetailLink;

	public SQODisplayDistributorListPage(WebDriver driver) {
		super(driver);
	}

	public SQOMyCurrentQuotePage selectDistributor() {
		loggerContxt.info("Click on 'Select distributor' link on 'Distributor selection result' page.");
		selectDistributorLink.click();
		loggerContxt.info("Action done");
		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		loggerContxt.info("Navigate back to 'My current quote' page.");
		return page;
	}	
	

	
}
