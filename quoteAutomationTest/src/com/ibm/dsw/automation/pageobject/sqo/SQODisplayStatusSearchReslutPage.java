package com.ibm.dsw.automation.pageobject.sqo;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;



public class SQODisplayStatusSearchReslutPage extends BasePage {

	public SQODisplayStatusSearchReslutPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(linkText = "Change selection criteria")
	public WebElement chgSelectCritLink;

	@FindBy(xpath = "//a[contains(@href, 'DISPATCH_SUBMITTED_QUOTE_TABS')]")
	public WebElement viewDetailLink;

	@FindBy(linkText="Change selection criteria")
	public WebElement changeCriteria;
	
	/**
	 * @deprecated use viewQuoteDetail() instead
	 * @return
	 */
	@Deprecated
	public SQOSelectedQuoteCPTabPage goDispQuoteReslt() {

		return viewQuoteDetail();
	}

	public SQOSelectedQuoteCPTabPage viewQuoteDetail() {
		if (!isTextPresent(prop.getProperty("noquote_found_info"))) {
			viewDetailLink.click();
			waitForElementLoading(5000L);
			SQOSelectedQuoteCPTabPage page = new SQOSelectedQuoteCPTabPage(driver);
			loadPage(page, this.WAIT_TIME);
			return page;
		}
		return null;
	}
	
	public SQOSelectedQuoteCPTabPage gotoDispQuoteReslt() {
		if (!isTextPresent(prop.getProperty("noquote_found_info"))) {
			elementClickByXPath("//a[contains(@href, 'DISPATCH_SUBMITTED_QUOTE_TABS')]");

		}
		
		SQOSelectedQuoteCPTabPage page = new SQOSelectedQuoteCPTabPage(this.driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public SQOStatusSearchTabPage changeCriteria(){
		waitForElementByLinktext("Change selection criteria", 6000L);
		changeCriteria.click();
		SQOStatusSearchTabPage statusSearchPage = new SQOStatusSearchTabPage(this.driver);
		loadPage(statusSearchPage, this.WAIT_TIME);
		return statusSearchPage;
	}
}
