package com.ibm.dsw.automation.pageobject.sqo;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.BrowsePartsTabPage;

public class SQOBrowsePartsTabPage extends BrowsePartsTabPage {	
	

	@FindBy(linkText = "Add selected parts to draft quote")
	public WebElement addSelectedPartsToDraftQuoteLink;
	
	@FindBy(linkText = "WebSphere Software")
	public WebElement webSphereSoftware;
	
	@FindBy(linkText = "Collaboration Solutions")
	public WebElement  lotusTabLink;
	
	@FindBy(linkText = "Return to draft quote")
	public WebElement  returnToDraftQuoteLink;
	
	@FindBy(id="img0_1")
	public WebElement apl;
 
	@FindBy(id="img0_1_0")
	public WebElement licenseSW;
	
	public SQOBrowsePartsTabPage(WebDriver driver) {
		super(driver);
	}
	/**
	 * Click the "Add selected parts to draft quote" link
	 * */
	@Override
	public void addSelectedPartsToDraftQuoteLinkClick() {
		loggerContxt.info("Click on 'Add selected parts to draft quote' link on 'Part select' page.");
		if (alertExists()) {
			driver.switchTo().alert().accept();
			clickDisplayedCheckBox();
		}
		addSelectedPartsToDraftQuoteLink.click();
		loggerContxt.info("Action done");
	}
	
	/**
	 * Click on Lotus 123 Click on License + SW Subscription & Support
	 * Check the checkbox of the first part listed
	 */
	public void browseLotusPartsTab() {		
	/*	lotusTabLink.click();
		lotusTabLink.click();
		waitForElementLoading(new Long(1000));
		try{
		anonymousResolutionButtonClick("img0_35");
		anonymousResolutionButtonClick("img0_35_0");
		anonymousResolutionButtonClick("0_35_0_0");
		} catch (NoSuchElementException e) {

			loggerContxt
					.info("We are unable to load this product,re click the notes link and have a double check!");
			if (isTextPresent("We are unable to load this product.")) {
				lotusTabLink.click();
				waitForElementLoading(new Long(1000));
				anonymousResolutionButtonClick("img0_35");
				anonymousResolutionButtonClick("img0_35_0");
				anonymousResolutionButtonClick("0_35_0_0");
			}

		}*/
		
		browseLotusParts();
	}	
	
	public void browseLotusPartsTab(String partNogeArray) {	
		if (StringUtils.isBlank(partNogeArray)) {
			browseLotusParts();
		}
		browseLotusParts(partNogeArray);
	}	
	
	public void browseLotusLiveNotesPartsTab() {		
		waitForElementLoading(new Long(1000));
		try{
		anonymousResolutionButtonClick("img0_7");
		anonymousResolutionButtonClick("0_7_1");
		} catch (NoSuchElementException e) {

			loggerContxt
					.error("We are unable to load this product,re click the notes link and have a double check!");
//			if (isTextPresent("We are unable to load this product.")) {
			lotusTabLink.click();
			waitForElementLoading(new Long(1000));
			anonymousResolutionButtonClick("img0_1");
			anonymousResolutionButtonClick("img0_1_0");
//			}

		}
	}	
	
	
	/**
	 * return to draft quote
	 * */
	public SQOPartsAndPricingTabPage returnToDraftQuoteLinkLinkClick() {
		loggerContxt.info("Click on 'Return to draft quote' link on 'Part select' page.");
		returnToDraftQuoteLink.click();
		loggerContxt.info("Action done");
		SQOPartsAndPricingTabPage page = new SQOPartsAndPricingTabPage(driver);
		loggerContxt.info("Navigate to 'My current quote' page 'Parts and pricing' tab.");
		return page;
	}
	
	public SQOBrowsePartsTabPage clickWebSphereSoftware(){
		webSphereSoftware.click();
		loggerContxt.info("click WebSphereSoftware link ");
		waitForElementLoading(new Long(10000));
		SQOBrowsePartsTabPage page = new SQOBrowsePartsTabPage(driver);
		return page;
	}
	
	public void browseAPL() {
		apl.click();
		loggerContxt.info("click APL link ");
		waitForElementLoading(new Long(125000));
		licenseSW.click();
		loggerContxt.info("click License + SW Subscription & Support  link ");
		waitForElementLoading(new Long(25000));
		clickDisplayedCheckBox();
		loggerContxt.info("click checkbox");
	}
}
