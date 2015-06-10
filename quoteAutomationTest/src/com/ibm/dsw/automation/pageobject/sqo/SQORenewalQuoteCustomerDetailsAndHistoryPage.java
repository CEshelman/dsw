package com.ibm.dsw.automation.pageobject.sqo;

import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;
import com.ibm.dsw.automation.util.DateUtil;

public class SQORenewalQuoteCustomerDetailsAndHistoryPage extends BasePage{

	public SQORenewalQuoteCustomerDetailsAndHistoryPage(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(linkText="Enrollments")
	private WebElement enrollmentsTab;
	@FindBy(linkText="Return to renewal quote information")
	private WebElement returnBackLink;
	@FindBy(linkText="Contacts")
	private WebElement contactsTab;
	@FindBy(linkText="Partner information")
	private WebElement partnerInfoLink;
	@FindBy(linkText="Customer contacts")
	private WebElement customerContactsLink;
	@FindBy(linkText="Renewals")
	private WebElement renewalsTab;
	@FindBy(linkText="Entitlements")
	private WebElement entitlementsTab;
	@FindBy(linkText="Active and future Software Subscription and Support entitlements")
	private WebElement subscriptionAndSupportLink;
	@FindBy(linkText="View entitlements in FastPass")
	private WebElement viewEntitlementLink;
	@FindBy(linkText="Software as a Service / Monthly Licensing")
	private WebElement SaaSTab;
	@FindBy(linkText="Inactive Software as a Service / Monthly Licensing")
	private WebElement inactiveSaaSLink;
	@FindBy(linkText="Order history")
	private WebElement orderHistoryTab;
	@FindBy(linkText="Generate summary report")
	private WebElement generateSummaryReport;
	@FindBy(linkText="Generate detail report")
	private WebElement generateDetailReport;
	@FindBy(linkText="Search by order number")
	private WebElement searchByOrderNumberLink;
	@FindBy(id="ORDER_NUM")
	private WebElement orderNumberInput;
	
	public void clickContactsTab()
	{
		contactsTab.click();
	}
	
	public void clickPartnerInfoLinkInContactsTab()
	{
		partnerInfoLink.click();
	}
	
	public void clickCustomerContactsLink()
	{
		customerContactsLink.click();
	}
	
	public void clickRenewalsTab()
	{
		renewalsTab.click();
	}
	
	public void clickEntitlementsTab()
	{
		entitlementsTab.click();
	}
	
	public void clickSubscriptionAndSupportLink()
	{
		subscriptionAndSupportLink.click();
	}
	
	public void clickViewEntitlementLink()
	{
		viewEntitlementLink.click();
	}
	
	public void clickSaaSTab()
	{
		SaaSTab.click();
	}
	
	public void clickInactiveSaaSLink()
	{
		inactiveSaaSLink.click();
	}
	
	public void clickOrderHistoryTab()
	{
		orderHistoryTab.click();
	}
	
	public void selectSalesOrderDate()
	{
		Date now = new Date();
		int day = DateUtil.getDay(now);
		if (day>2) {
			day -= 1;
		}
		int month = DateUtil.getMonth(now);
		if (month>12) {
			month -= 10;
		}
		int year = DateUtil.getYear(now);
		
		this.selectOptionByVisibleText("FROM_DAY", String.valueOf(day));
		this.selectedOptionByIndex(By.id("FROM_MONTH"), month);
		this.selectOptionByVisibleText("FROM_YEAR", String.valueOf(year-1));
		
		this.selectOptionByVisibleText("TO_DAY", String.valueOf(day));
		this.selectedOptionByIndex(By.id("TO_MONTH"), month);
		this.selectOptionByVisibleText("TO_YEAR", String.valueOf(year));
	}
	
	public void clickgenerateSummaryReport()
	{
		generateSummaryReport.click();
	}
	
	public void clickgenerateDetailReport()
	{
		generateDetailReport.click();
	}
	
	public void clickSearchByOrderNumberLink(){
		searchByOrderNumberLink.click();
	}
	
	public void fillOrderNumberInput(String value){
		orderNumberInput.clear();
		orderNumberInput.sendKeys(value);
	}
	
	public SQORenewalQuoteCustomerDetailsAndHistoryPage newCache(){
		SQORenewalQuoteCustomerDetailsAndHistoryPage page = new SQORenewalQuoteCustomerDetailsAndHistoryPage(driver);
		this.loadPage(page, WAIT_TIME);
		return page;
	}

}
