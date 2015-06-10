package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQORQSummaryTabPage extends PGSBasePage {
	
	@FindBy(linkText = "Return to renewal quote list")
	public WebElement rtn2RQList;

	@FindBy(linkText = "Edit the master renewal quote")
	public WebElement editTheMasterRenewalQuoteLink;
	
	@FindBy(linkText = "Create a special bid against this renewal quote")
	public WebElement createSBLink;
	
	public SQORQSummaryTabPage(WebDriver driver) {
		super(driver);
	}

	public SQODraftRQCustPartnerTabPage editTheMasterRenewalQuote(boolean isYes) {
		editTheMasterRenewalQuoteLink.click();
		
		if (isYes) {
			if (isElementExist(By.name("ibm-continue"))) {
				elementClickByName("ibm-continue");
			}
			
			return new SQODraftRQCustPartnerTabPage(driver);
		} else {
			if (isElementExist(By.name("ibm-cancel"))) {
				elementClickByName("ibm-cancel");
			}
			
			return null;
		}
	}
	
	public SQOMyCurrentQuotePage createSpecialBidAgainstRenewalQuote(boolean isYes) {
		createSBLink.click();
		
		if (isYes) {
			if (isElementExist(By.name("ibm-continue"))) {
				elementClickByName("ibm-continue");
			}
			
			return new SQOMyCurrentQuotePage(driver);
		} else {
			if (isElementExist(By.name("ibm-cancel"))) {
				elementClickByName("ibm-cancel");
			}
			
			return null;
		}
	}
}
