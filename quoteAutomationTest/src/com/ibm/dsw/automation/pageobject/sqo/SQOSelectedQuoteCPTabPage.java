package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;


public class SQOSelectedQuoteCPTabPage extends PGSBasePage {
	
	@FindBy(linkText = "Parts and pricing")
	public WebElement partsAndPricingTabLink;
	
	@FindBy(linkText = "Sales information")
	public WebElement  salesInformationTabLink;
	
	@FindBy(linkText = "Special bid")
	public WebElement  specialBidTabLink;
	
	@FindBy(linkText = "Order")
	public WebElement  orderLink;
	
	@FindBy(linkText = "Create a copy")
	public WebElement createACopyLink;
	
	@FindBy(linkText = "Create bid iteration")
	public WebElement createBidIterationLink;
	
	@FindBy(linkText = "Status")
	public WebElement statusLink;
	
	public SQOSelectedQuoteCPTabPage(WebDriver driver) {
		super(driver);
	}

	
	
	public SQOSelectedQuotePPTabPage  goToPPTab() {
		partsAndPricingTabLink.click();
		SQOSelectedQuotePPTabPage page = new SQOSelectedQuotePPTabPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public SQOSelectedQuotePPTabPage createBidIteration() {
	    WebElement  bidIteration = driver.findElement(By.linkText("Click this button to create a bid iteration."));
	    bidIteration.click();
	    Alert alert = driver.switchTo().alert();
	    alert.accept();
	    SQOSelectedQuotePPTabPage page = new SQOSelectedQuotePPTabPage(driver);
        loadPage(page, this.WAIT_TIME);
        return page;
    }
	
	public CheckOutBillAndShipPage  goToCheckout() {
		orderLink.click();
		CheckOutBillAndShipPage page = new CheckOutBillAndShipPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public void createACopyLinkClick() {
		createACopyLink.click();
	}
	
	public SQOSubmittedQuoteExecSummaryTabPage gotoExecutiveSummaryTab() {
		elementClickByLinkText("Executive summary");
		SQOSubmittedQuoteExecSummaryTabPage page = new SQOSubmittedQuoteExecSummaryTabPage(getDriver());
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public SQOStatusSearchTabPage gotoStatePage() {

		statusLink.click();
		SQOStatusSearchTabPage page = new SQOStatusSearchTabPage(this.driver);
		loadPage(page, this.WAIT_TIME);
		
		return page;
	}
	
}
