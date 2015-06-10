package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQOSelectedQuotePPTabPage extends PGSBasePage {
	
	@FindBy(linkText = "Parts and pricing")
	public WebElement partsAndPricingTabLink;
	
	@FindBy(linkText = "Sales information")
	public WebElement  salesInformationTabLink;
	
	
	@FindBy(linkText = "Special bid")
	public WebElement  specialBidTabLink;

	
	@FindBy(xpath = "//input[@value='hide_line_item_details']")
	public WebElement  hide_line_item_details;
	
	@FindBy(xpath = "//input[@value='hide_brand_detail_in_totals']")
	public WebElement  hide_brand_detail_in_totals;
	
	/**
	 * 
	 */
	@FindBy(linkText = "Create a copy")
	public WebElement  createACopyLink;
	
	@FindBy(linkText = "Create bid iteration")
	public WebElement createBidIterationLink;
	
	public SQOSelectedQuotePPTabPage(WebDriver driver) {
		super(driver);
	}

	
	
	public void hiddenDetails() {
		hide_line_item_details.click();
		waitForElementLoading(new Long(10000));
		hide_brand_detail_in_totals.click();
		
		waitForElementLoading(new Long(10000));
	}
	
	public void overrideConfiguration()
	{
	    WebElement overrideConfig = driver.findElement(By.xpath("//a[@title='Override configuration']"));
        overrideConfig.click();
        
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 * @param pIdPath
	 * @return
	 */
	public String getOriginalProvisioningId(String pIdPath) {
		WebElement pIdText = driver.findElement(By.xpath(pIdPath));
		String originalProvisioningId = pIdText.getText().substring(pIdText.getText().length() - 14, pIdText.getText().length());
		return originalProvisioningId;
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 5, 2013
	 */
	public SQOMyCurrentQuotePage createACopyLinkClick() {
		createACopyLink.click();
		switchToAlert(true, getClass().getSimpleName());
		return new SQOMyCurrentQuotePage(getDriver());
	}
	
	public SQOCompareQuotesPage gotoCompareQuotesPage() {
//		setThisWinHandle(getDriver().getWindowHandle());
		elementClickByLinkText("Compare quotes");
		switchToWindow(getDriver(), "IBM Software Quote and Order: Quote comparison results");
		return new SQOCompareQuotesPage(getDriver());
	}
	
	public SQOSelectedQuotePPTabPage createBidIterationLinkClick() {
		
		createBidIterationLink.click();
		switchToAlert(true, getClass().getSimpleName());
		SQOSelectedQuotePPTabPage page = new SQOSelectedQuotePPTabPage(getDriver());
		return page;
		
	}
	public SQOSubmitSQSalesInfoTabPage  goToSalesInfoTab() {
		salesInformationTabLink.click();
		SQOSubmitSQSalesInfoTabPage page = new SQOSubmitSQSalesInfoTabPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}

	public void validateModifyServiceDateExist(boolean isExist) {
		boolean isPresent = isTextPresent("Modify service dates");
		if (isExist) {
			assertObjectEquals(isPresent, true);
		} else {
			assertObjectEquals(isPresent, false);
		}
	}
}
