package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;


public class DisplayResellerListPage extends PGSBasePage {
	@FindBy(linkText = "Return to my current quote ")
	public WebElement returnCurQuoteLink;
	
	@FindBy(linkText = "Change search criteria")
	public WebElement changeSearchCriLink;
	
	@FindBy(linkText = "Select reseller")
	public WebElement selectResellerLink;
	


	public DisplayResellerListPage(WebDriver driver) {
		super(driver);
	}

	public MyCurrentQuotePage selectReseller() {
		
		selectResellerLink.click();
		MyCurrentQuotePage page = new MyCurrentQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}	
	

	
}
