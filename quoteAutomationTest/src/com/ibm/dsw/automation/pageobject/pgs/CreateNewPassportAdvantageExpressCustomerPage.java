package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CreateNewPassportAdvantageExpressCustomerPage extends PGSBasePage {
	
	public CreateNewPassportAdvantageExpressCustomerPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	@FindBy(linkText = "Return to my current quote")
	public WebElement  returnToMyCurrentQuoteLink;	
	
	@FindBy(linkText = "Find an existing customer")
	public WebElement  findAnExistingCustomerTabLink;
	
	@FindBy(linkText = "Create a new Passport Advantage Express customer")
	public WebElement  createANewPassportAdvantageExpressCustomerTabLink;
	
	public WebElement lob;

	public WebElement agreementOption;

	public WebElement  companyName;
	
	public WebElement  address1;
	
	public WebElement  address2;
	
	public WebElement  city;
	
	public WebElement  state;
	
	public WebElement  postalCode;
	
	public WebElement  vatNum;
	
	public WebElement  industryIndicator;	
	
	@FindBy(id = "oneToK")
	public WebElement  companySizeOneThousand;
	
	@FindBy(id = "kPlus")
	public WebElement  companySizeThousandPlus;
	
	public WebElement  cntFirstName;
	
	public WebElement  cntLastName;
	
	public WebElement  cntPhoneNumFull;
	
	public WebElement  cntFaxNumFull;
	
	public WebElement  cntEmailAdr;
	
	public WebElement  commLanguage;
	
	public WebElement  mediaLanguage;
	
	@FindBy(name="ibm-submit")
	public WebElement  submitButton;	
	
	
	public void fillCustomerForm() {
		// Company Information
		setValueById("companyName", "companyName");
		setValueById("address1", "address1");
		setValueById("address2", "address2");
		setValueById("city", "city");
		selectedOptionByValue("lob", "PA", getDriver());

		selectedOptionByValue("state", "TX", getDriver());
		setValueById("postalCode", "11111");

		selectedOptionByValue("industryIndicator", "04", getDriver());
		elementClickById("oneToK");
		
		// Primary contact information
		setValueById("cntFirstName", "cntFirstName");
		setValueById("cntLastName", "cntLastName");
		setValueById("cntPhoneNumFull", "111111");
		setValueById("cntEmailAdr", "suchang713@126.com");
		
		// Language preferences
		selectedOptionByValue("commLanguage", "EN", getDriver());
		selectedOptionByValue("mediaLanguage", "EN", getDriver());
	}
	
	public MyCurrentQuotePage submitCreateCustomer() {
		elementClickByName("ibm-submit");
		MyCurrentQuotePage page = new MyCurrentQuotePage(driver);
		return page;
	}
	
}
