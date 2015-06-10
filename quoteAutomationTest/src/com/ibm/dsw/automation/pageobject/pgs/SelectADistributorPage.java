package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SelectADistributorPage extends PGSBasePage {
	
	public SelectADistributorPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	@FindBy(linkText = "Return to my current quote")
	public WebElement  returnToMyCurrentQuoteLink;	
	
	@FindBy(id = "num")
	public WebElement  distributorNumber;
	
	@FindBy(name="ibm-submit")
	public WebElement  submitButton;	
	
}
