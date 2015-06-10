package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


/**
 * 
 * @author suchuang
 * @date Dec 14, 2012
 */
public class PGSFindPartsSelectTabPage extends PGSBasePage {

	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 * @param driver
	 */
	public PGSFindPartsSelectTabPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * 
	 */
	@FindBy(linkText = "Return to draft quote")
	public WebElement returnToMyCurrentQuoteLink;

	/**
	 * 
	 */
	@FindBy(linkText = "Processor Value Unit (PVU) calculator")
	public WebElement pvuCalculotorLink;

	/**
	 * 
	 */
	@FindBy(linkText = "Change search criteria")
	public WebElement changeSearchCriteriaLink;

	/**
	 * 
	 */
	@FindBy(linkText = "Browse parts")
	public WebElement browsePartsTabLink;

	/**
	 * 
	 */
	@FindBy(linkText = "Browse software as a service")
	public WebElement browseSfAsaServiceTabLink;

	/**
	 * 
	 */
	@FindBy(linkText = "Select all displayed parts")
	public WebElement selectAllDispPartsLink;

	/**
	 * 
	 */
	@FindBy(linkText = "Add selected parts to draft quote")
	public WebElement addSelectedParts2QuoteLink;

	/**
	 * 
	 */
	@FindBy(linkText = "Collapse all")
	public WebElement collapseAllLink;

	/**
	 * 
	 */
	@FindBy(linkText = "Expand all")
	public WebElement expandAllLink;

	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 * @return
	 */
	public FindPartsTabPage selectPartsAndChgCriteriaClick() {
		selectPartsClick();
		//WebElement changeSearchCriteriaLink=findElementByXPath("//a[contains(@href, 'partDescription=IBM+Lotus+123') and @class='ibm-callaction-link']");
		changeSearchCriteriaLink.click();
		FindPartsTabPage page = new FindPartsTabPage(this.driver);
		return page;
	}

	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	public void selectPartsClick() {

		// Wait till the page completes loading. Then click the "Expand all".
		// Scroll to the bottom pf the page, Wait till the parts tree has been
		// painted,
		// click the "Select all displayed parts" link, then click the
		// "Add selected parts to draft quote" link
		anonymousResolutionButtonClick("imgALL");
		waitForElementLoading(new Long(25000));
		selectAllDispPartsLink.click();
		addSelectedParts2QuoteLink.click();
	}

	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	public void selectAllDispPartsLink() {
		selectAllDispPartsLink.click();
	}

	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	public void clickSelectedParts2QuoteLink() {
		addSelectedParts2QuoteLink.click();
	}

	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	public void clickCollapseAllLink() {
		collapseAllLink.click();
	}

	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	public void clickExpandAllLink() {
		expandAllLink.click();
	}

	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	public PartsAndPricingTabPage rtn2DraftQuote() {
		returnToMyCurrentQuoteLink.click();
		return new PartsAndPricingTabPage(this.driver);
	}
}
