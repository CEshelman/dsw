package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class SelectAResellerPage extends PGSBasePage {
	
	public SelectAResellerPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	@FindBy(linkText = "Return to my current quote")
	public WebElement  returnToMyCurrentQuoteLink;	
	
	@FindBy(id = "num")
	public WebElement  resellerNumber;
	
	@FindBy(name="ibm-submit")
	public WebElement  submitButton;
	
	public DisplayResellerListPage displayResellerListByNum(String resellerNum) {
		resellerNumber.sendKeys(resellerNum);
		submitButton.click();
		DisplayResellerListPage page = new DisplayResellerListPage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}	
}
