package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;

public class SQORetrieveSavedSalesQuotePage extends BasePage {
	
	public SQORetrieveSavedSalesQuotePage(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//input[@name='ibm-submit']")
	public WebElement continueBtn;
	
	//@FindBy(xpath = "//a[contains(@href, 'SELECT_DRAFT_SALES_QUOTE')]")
	//@FindBy(xpath = "//html/body/div/div[2]/div/div/div[2]/div/div[2]/table/tbody/tr/td[6]/a")
	@FindBy(partialLinkText = "open")
	public WebElement open_link;
	public WebElement ownerFilterOwned;
	public WebElement ownerFilterDelegated;
	public WebElement markAsDefault;

	public SQOMyCurrentQuotePage goViewDetailSavedQuote(){
		loggerContxt.info("Click on the 'open' link on the first retrieved quote on 'Retrieve a saved sales quote' page.");
		open_link.click();
		loggerContxt.info("Action done");
		switchToAlert(true, getClass().getSimpleName());
		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loggerContxt.info("Navigate to 'My current quote' page.");
		return page;
	}
	
	public void retrieveQuote(String timeFilter,String value) {
		if(!ownerFilterOwned.isSelected()){
			ownerFilterOwned.click();
		}
		if(!ownerFilterDelegated.isSelected()){
			ownerFilterDelegated.click();
		}
		if(!markAsDefault.isSelected()){
			markAsDefault.click();
		}
		selectedOptionByValue(timeFilter, value, driver);
		continueBtn.click();
		new SQORetrieveSavedSalesQuotePage(driver);
		
	}
}
