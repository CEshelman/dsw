package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQOOmittedLinePage extends PGSBasePage {

	public SQOOmittedLinePage(WebDriver driver) {
		super(driver);
	}
	
	public WebElement addOrUpdateForDraft;
	
	
	
	@FindBy(linkText = "Return to draft quote")
	public WebElement  returnToDraftQuoteLink;
	
	public void addOrViewOrUpdate(){
		this.addOrUpdateForDraft.click();
		driver.switchTo().frame("cpqIframeId1"); 
			
		
	}
	
	public SQOPartsAndPricingTabPage returnToPartAndPriceTab(){
		this.returnToDraftQuoteLink.click();
		return new SQOPartsAndPricingTabPage(this.driver);
	}

}
