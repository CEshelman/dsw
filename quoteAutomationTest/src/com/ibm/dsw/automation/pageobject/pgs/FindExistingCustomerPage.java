package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class FindExistingCustomerPage extends PGSBasePage {
	
	public FindExistingCustomerPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(linkText = "Return to my current quote")
	public WebElement  returnToMyCurrentQuoteLink;	
	
	@FindBy(linkText = "Find an existing customer")
	public WebElement  findAnExistingCustomerTabLink;
	
	@FindBy(linkText = "Create a new Passport Advantage Express customer")
	public WebElement  createANewPassportAdvantageExpressCustomerTabLink;
	
	public WebElement  customerName;
	
	public WebElement  findActivePA;
	
	public WebElement  findActivePAE;
	
	public WebElement  contractOption;
	
	public WebElement  anniversary;
	
	@FindBy(id="siteNumber")
	public WebElement  siteNumber;
	
	@FindBy(id="agreementNumber")
	public WebElement  agreementNumber;
	
	public WebElement  findAllCntryCusts;	
	
	@FindBy(linkText = "Sales information")
	public WebElement  salesInformationTabLink;
	
	@FindBy(name="ibm-submit")
	public WebElement  submitButton;	
	@FindBy(xpath="//form[2]/div/div/div/div[2]/div[2]/div/p/input")
	public WebElement  siteNumSubmitButton;	
	
	@FindBy(xpath=".//div[contains(strong,'No customer matched your selection criteria.')]")
	public WebElement warning;

	
	public DisplayCustListPage displayCustomerListBySiteNum(String siteNum) {
		siteNumber.clear();
		siteNumber.sendKeys(siteNum);
		siteNumSubmitButton.click();
		if(warning.isDisplayed()){
			loggerContxt.error(String.format(
					"Failed to find customer base on the site number:: %s, Please have a check."
					,siteNum));
			return null;
		}
		DisplayCustListPage page = new DisplayCustListPage(driver);
		return page;
	}
	
	public DisplayCustListPage displayCustomerListBySiteNum(String siteNum, String agreementNum) {
		siteNumber.sendKeys(siteNum);
		agreementNumber.sendKeys(agreementNum);
		siteNumSubmitButton.click();
		DisplayCustListPage page = new DisplayCustListPage(driver);
		return page;
	}
	
}
