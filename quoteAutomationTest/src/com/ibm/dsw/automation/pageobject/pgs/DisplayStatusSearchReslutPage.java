package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;

public class DisplayStatusSearchReslutPage extends BasePage {
	
	private static final String partialHref = "//a[@href='quotePartner.wss?jadeAction=DISPATCH_SUBMITTED_QUOTE_TABS&quoteNum=quotenum']";
	
	public DisplayStatusSearchReslutPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(linkText = "Change selection criteria")
	public WebElement chgSelectCritLink;

	/**
	 * 
	 * @author suchuang
	 * @date Jan 8, 2013
	 * @param quoteNum
	 * @return
	 */
	public PGSStatusSalesQuote goDispQuoteReslt(String quoteNum) {
		WebElement viewDetailLink = driver.findElement(By.xpath(partialHref.replace("quotenum", quoteNum)));
		viewDetailLink.click();
		PGSStatusSalesQuote page = new PGSStatusSalesQuote(this.driver);
		return page;
	}
	
	public PGSStatusSalesQuote goDispQuoteReslt() {
		if (!isTextPresent(prop.getProperty("noquote_found_info"))) {
			elementClickByXPath("//a[contains(@href, 'DISPATCH_SUBMITTED_QUOTE_TABS')]");
			PGSStatusSalesQuote page = new PGSStatusSalesQuote(this.driver);
			loadPage(page, this.WAIT_TIME);
			return page;
		}
		
		return null;
	}
	
	public StatusSearchTabPage changeCriteriaLink() {
		chgSelectCritLink.click();
		
		StatusSearchTabPage page = new StatusSearchTabPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	
}
