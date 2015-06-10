package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;

/**
 * 
 * @author suchuang
 * @date Dec 28, 2012
 */
public class PGSStatusSalesQuote extends BasePage {

	/**
	 * 
	 */
	@FindBy(linkText = "Parts and pricing")
	public WebElement partsAndPricingTabLink;
	
	/**
	 * 
	 * @author suchuang
	 * @date Dec 28, 2012
	 * @param driver
	 */
	public PGSStatusSalesQuote(WebDriver driver) {
		super(driver);
	}

	/**
	 * 
	 * @author suchuang
	 * @date Dec 28, 2012
	 * @return
	 */
	public PartsAndPricingTabPage goToPartsAndPricingTab() {
		partsAndPricingTabLink.click();
		PartsAndPricingTabPage page = new PartsAndPricingTabPage(driver);
		return page;
	}
	
	public SelectedQuotePPTabPage goToPPTab() {
		partsAndPricingTabLink.click();
		SelectedQuotePPTabPage page = new SelectedQuotePPTabPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public PGSHomePage goHomePage() {

		elementClickByLinkText("Partner guided selling");
		PGSHomePage page = new PGSHomePage(this.driver);
		loadPage(page, this.WAIT_TIME);
		
		return page;
	}
	
}
