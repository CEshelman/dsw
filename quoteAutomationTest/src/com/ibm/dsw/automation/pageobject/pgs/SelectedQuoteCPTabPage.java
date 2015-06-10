package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;
import com.ibm.dsw.automation.pageobject.sqo.CheckOutBillAndShipPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuotePPTabPage;

public class SelectedQuoteCPTabPage extends BasePage {
	
	@FindBy(linkText = "Parts and pricing")
	public WebElement partsAndPricingTabLink;
	
	@FindBy(linkText = "Sales information")
	public WebElement  salesInformationTabLink;
	
	
	@FindBy(linkText = "Special bid")
	public WebElement  specialBidTabLink;
	
	@FindBy(linkText = "Order")
	public WebElement  orderLink;
	
	public SelectedQuoteCPTabPage(WebDriver driver) {
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
	
	public CheckOutBillAndShipPage goToCheckout() {
		orderLink.click();
		CheckOutBillAndShipPage page = new CheckOutBillAndShipPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
}
