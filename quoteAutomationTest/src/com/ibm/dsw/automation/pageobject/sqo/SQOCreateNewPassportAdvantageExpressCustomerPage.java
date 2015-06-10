package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.common.SQOPropertiesBean;
import com.ibm.dsw.automation.pageobject.pgs.CreateNewPassportAdvantageExpressCustomerPage;

public class SQOCreateNewPassportAdvantageExpressCustomerPage extends CreateNewPassportAdvantageExpressCustomerPage {
	

	@FindBy(linkText = "Return to my current quote")
	public WebElement  returnToMyCurrentQuoteLink;	
	
	@FindBy(linkText = "Find an existing customer")
	public WebElement  findAnExistingCustomerTabLink;
	
	@FindBy(linkText = "Create a new Passport Advantage Express customer")
	public WebElement  createANewPassportAdvantageExpressCustomerTabLink;
	
	public WebElement  lob;
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
	
	
	@FindBy(xpath="//input[@value='Continue']")
	public WebElement  submitButton;	
	
	public SQOCreateNewPassportAdvantageExpressCustomerPage(WebDriver driver) {
		super(driver);
	}
	
public void settingNewCustInfoWithoutSubmit(SQOPropertiesBean bean) {
		
		selectedOptionByValue("lob", bean.getLobCreaNewCust(), driver);
		sendKeys(companyName,(bean.getCompanyNameCreaNewCust()));
		sendKeys(address1,(bean.getAddress1CreaNewCust()));
		sendKeys(address2,(bean.getAddress2CreaNewCust()));
		sendKeys(city,(bean.getCityCreaNewCust()));
		selectedOptionByValue("state", bean.getStateCreaNewCust(), driver);
		
		sendKeys(postalCode,(bean.getPostalCodeCreaNewCust()));
		sendKeys(vatNum,(bean.getVatNumCreaNewCust()));
		selectedOptionByValue("industryIndicator", bean.getIndustryIndicatorCreaNewCust(), driver);
		companySizeOneThousand.click();
		sendKeys(cntFirstName,(bean.getCntFirstNameCreaNewCust()));
		sendKeys(cntLastName,(bean.getCntLastNameCreaNewCust()));
		sendKeys(cntPhoneNumFull,(bean.getCntPhoneNumFullCreaNewCust()));
		sendKeys(cntFaxNumFull,(bean.getCntFaxNumFullCreaNewCust()));
		sendKeys(cntEmailAdr,(bean.getCntEmailAdrCreaNewCust()));
		selectedOptionByValue("commLanguage", bean.getCommLanguageCreaNewCust(), driver);
		selectedOptionByValue("mediaLanguage", bean.getMediaLanguageCreaNewCust(), driver);

	
	}	
	
	public SQOMyCurrentQuotePage createAPAECustomer(SQOPropertiesBean bean) {
		
		/*selectedOptionByValue("lob", bean.getLobCreaNewCust(), driver);
		sendKeys(companyName,(bean.getCompanyNameCreaNewCust()));
		sendKeys(address1,(bean.getAddress1CreaNewCust()));
		sendKeys(address2,(bean.getAddress2CreaNewCust()));
		sendKeys(city,(bean.getCityCreaNewCust()));
		sendKeys(state,(bean.getStateCreaNewCust()));
		sendKeys(postalCode,(bean.getPostalCodeCreaNewCust()));
		sendKeys(vatNum,(bean.getVatNumCreaNewCust()));
		selectedOptionByValue("industryIndicator", bean.getIndustryIndicatorCreaNewCust(), driver);
		companySizeOneThousand.click();
		sendKeys(cntFirstName,(bean.getCntFirstNameCreaNewCust()));
		sendKeys(cntLastName,(bean.getCntLastNameCreaNewCust()));
		sendKeys(cntPhoneNumFull,(bean.getCntPhoneNumFullCreaNewCust()));
		sendKeys(cntFaxNumFull,(bean.getCntFaxNumFullCreaNewCust()));
		sendKeys(cntEmailAdr,(bean.getCntEmailAdrCreaNewCust()));
		sendKeys(commLanguage,(bean.getCommLanguageCreaNewCust()));
		sendKeys(mediaLanguage,(bean.getMediaLanguageCreaNewCust()));*/

		settingNewCustInfoWithoutSubmit(bean);
		submitButton.click();
		
		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}	
	
	
	
	
}
