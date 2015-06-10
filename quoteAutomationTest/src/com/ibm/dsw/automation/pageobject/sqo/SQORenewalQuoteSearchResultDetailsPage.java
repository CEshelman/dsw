package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;

public class SQORenewalQuoteSearchResultDetailsPage extends BasePage{

	public SQORenewalQuoteSearchResultDetailsPage(WebDriver driver) {
		super(driver);
	}
	
	private SQORenewalQuoteCustomerDetailsAndHistoryPage customerDetailsAndHistoryPage = null;
	
	@FindBy(linkText="Details")
	public WebElement detailsTabLink;
	
	@FindBy(linkText="Sales tracking")
	public WebElement salesTrankingTabLink;
	
	@FindBy(linkText="Contacts")
	public WebElement contactsTabLink;
	@FindBy(linkText="Partner")
	public WebElement parterLinkInContactTab;
	@FindBy(linkText="IBM")
	public WebElement IBMLinkInContactTab;
	
	@FindBy(partialLinkText="Return to renewal quote list")
	public WebElement returnToSearchResultLink;
	@FindBy(partialLinkText="Additional agreement information")
	public WebElement additionInfoLink;
	
	public void clickDetailsTab(){
		detailsTabLink.click();
	}
	
	public void clickSalesTrankingTab(){
		salesTrankingTabLink.click();
	}
	
	public void clickContactsTab(){
		contactsTabLink.click();
	}
	
	public void clickPartnerLinkInContactsTab(){
		parterLinkInContactTab.click();
	}
	
	public void clickIBMLinkInContactsTab(){
		IBMLinkInContactTab.click();
	}
	
	public void clickAdditionInfoLink(){
		additionInfoLink.click();
		this.setCustomerDetailsAndHistoryPage();
	}

	public SQORenewalQuoteSearchResultDetailsPage newCache(){
		SQORenewalQuoteSearchResultDetailsPage page = new SQORenewalQuoteSearchResultDetailsPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}

	public SQORenewalQuoteCustomerDetailsAndHistoryPage getCustomerDetailsAndHistoryPage() {
		return customerDetailsAndHistoryPage;
	}

	public void setCustomerDetailsAndHistoryPage(
			SQORenewalQuoteCustomerDetailsAndHistoryPage customerDetailsAndHistoryPage) {
		this.customerDetailsAndHistoryPage = customerDetailsAndHistoryPage;
	}
	
	
	public void setCustomerDetailsAndHistoryPage() {
		SQORenewalQuoteCustomerDetailsAndHistoryPage page = new SQORenewalQuoteCustomerDetailsAndHistoryPage(driver);
		this.loadPage(page, WAIT_TIME);
		this.setCustomerDetailsAndHistoryPage(page);
	}
	
}
