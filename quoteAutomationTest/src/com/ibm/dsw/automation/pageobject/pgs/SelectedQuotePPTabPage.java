package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;

public class SelectedQuotePPTabPage extends BasePage {
	
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
	
	public SelectedQuotePPTabPage(WebDriver driver) {
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
	public void createACopyLinkClick() {
		createACopyLink.click();
		switchToAlert(true, getClass().getSimpleName());
	}
	
	
	public SubmitSQSalesInfoTabPage  goToSalesInfoTab() {
		salesInformationTabLink.click();
		SubmitSQSalesInfoTabPage page = new SubmitSQSalesInfoTabPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}

}

