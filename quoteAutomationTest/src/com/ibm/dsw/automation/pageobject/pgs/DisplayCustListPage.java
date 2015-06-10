package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;


public class DisplayCustListPage extends PGSBasePage {
	
	@FindBy(linkText = "Return to my current quote ")
	public WebElement returnCurQuoteLink;
	
	@FindBy(linkText = "Change search criteria")
	public WebElement changeSearchCriLink;
	
	@FindBy(linkText = "Select customer")
	public WebElement selectCustLink;
	
	@FindBy(linkText = " View customer details and history")
	public WebElement viewCustDetailLink;

	public DisplayCustListPage(WebDriver driver) {
		super(driver);
	}

	public MyCurrentQuotePage selectCustomer() {
		selectCustLink.click();
		MyCurrentQuotePage page = new MyCurrentQuotePage(driver);
		return page;
	}
	
}
