package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;

public class SalesInfoTabPage extends BasePage {
	
	public SalesInfoTabPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	@FindBy(xpath = "//input[@title='Parts and pricing']")
	public WebElement partsAndPricingTabLink;
	
	@FindBy(xpath = "//input[@title='Sales information']")
	public WebElement  salesInformationTabLink;
	
	@FindBy(linkText = "Customers and business partners")
	public WebElement  customersAndBusinessPartnersTabLink;
	
	
	@FindBy(linkText = "Approval")
	public WebElement  specialBidTabLink;
	
	public WebElement  briefTitle;
	public WebElement  quoteDesc;

	public WebElement  sionCustInit;
	

	
	public void enterSalesInf(String title,String desc ) {
		briefTitle.sendKeys(title);
		quoteDesc.sendKeys(desc);
		sionCustInit.click();

	
	}
	
	public SpecialBidTabPage goToSpecialBidTabClick() {
		specialBidTabLink.click();
		SpecialBidTabPage page = new SpecialBidTabPage(this.driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	public MyCurrentQuotePage  goToCustPartnerTab() {
		customersAndBusinessPartnersTabLink.click();
		MyCurrentQuotePage page = new MyCurrentQuotePage(getDriver());
		return page;
	}

	public SQOSalesInfoTabPage saveDraftQuoteLink( ) {
		elementClickByXPath("//a[@title='Save quote as draft']");
		return new SQOSalesInfoTabPage(getDriver());
	}
}
