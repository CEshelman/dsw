package com.ibm.dsw.automation.flow;

import org.openqa.selenium.WebDriverBackedSelenium;

import com.ibm.dsw.automation.pageobject.sqo.SQORQCustomerActionsSearchPage;
import com.ibm.dsw.automation.pageobject.sqo.SQORenewalQuoteCustomerDetailsAndHistoryPage;
import com.ibm.dsw.automation.pageobject.sqo.SQORenewalQuoteSearchTabPage;

public class SQORenewalQuoteSearchFlow extends QuoteFlow{

	private SQOQuoteCommonFlow quoteFlow;
	
	public SQORenewalQuoteSearchFlow(){
		
	}
	
	public SQORenewalQuoteSearchFlow(SQOQuoteCommonFlow quoteFlow){
		this.quoteFlow = quoteFlow;
		setDriver(quoteFlow.getDriver());
		selenium = new WebDriverBackedSelenium(driver, driver.getCurrentUrl());
		setSelenium(selenium);
		setLogonInf(getLogonInf());
	}
	
	public void clickByNumberTab(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click the quote number tab in the Renewal Quotes page...");
		renewalQuoteSearchPage.clickByNumberTab();
		//This may be a bad manner
		renewalQuoteSearchPage = renewalQuoteSearchPage.newCache();
	}
	
	public void clickSalesSpecifiedLinkInByIBMerTab(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click 'Find quotes assigned to a specific sales representative' link in the quote number tab in the Renewal Quotes page...");
		renewalQuoteSearchPage.clickSalesSpecifiedLinkInByIBMerTab();
		//This may be a bad manner
		renewalQuoteSearchPage = renewalQuoteSearchPage.newCache();
	}
	
	public void clickCoordinatorSpecifiedLinkInByIBMerTab(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click 'Find quotes assigned to a specific renewal coordinator' link in the quote number tab in the Renewal Quotes page...");
		renewalQuoteSearchPage.clickCoordinatorSpecifiedLinkInByIBMerTab();
		//This may be a bad manner
		renewalQuoteSearchPage = renewalQuoteSearchPage.newCache();
	}
	
	public void clickByCustomerTab(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click the by customer tab in the Renewal Quotes page...");
		renewalQuoteSearchPage.clickByCustomerTab();
		//This may be a bad manner
		renewalQuoteSearchPage = renewalQuoteSearchPage.newCache();
	}
	
	public void clickAttributeLinkInByCustomerTab(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click 'Find quotes by customer attributes' link in the by customer tab in the Renewal Quotes page...");
		renewalQuoteSearchPage.clickAttributeLinkInByCustomerTab();
		//This may be a bad manner
		renewalQuoteSearchPage = renewalQuoteSearchPage.newCache();
	}
	
	public void clickAttributeLinkInByPartnerTab(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click 'Find quotes by partner attributes' link in the by Partner tab in the Renewal Quotes page...");
		renewalQuoteSearchPage.clickAttributeLinkInByPartnerTab();
		//This may be a bad manner
		renewalQuoteSearchPage = renewalQuoteSearchPage.newCache();
	}
	
	public void clickByPartnerTab(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click By Partner page...");
		renewalQuoteSearchPage.clickByPartnerTab();
		//This may be a bad manner
		renewalQuoteSearchPage = renewalQuoteSearchPage.newCache();
	}
	
	public void clickByRegionTab(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click By Region page...");
		renewalQuoteSearchPage.clickByRegionTab();
		//This may be a bad manner
		renewalQuoteSearchPage = renewalQuoteSearchPage.newCache();
	}
	
	public void findRQByNumber(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage, String quoteNumer){
		loggerContxt.info(String.format("Find Renewal quote by quote number ::%s...",quoteNumer));
		renewalQuoteSearchPage.findRQByNumber(quoteNumer);
	}
	
	public void findRQByIBMerAssignedToMe(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage, String renewalStatusValue){
		loggerContxt.info(String.format("Find Renewal quote by IBMer (Assigned to me)::%s...",renewalStatusValue));
		renewalQuoteSearchPage.findRQByIBMerAssignedToMe(renewalStatusValue);
	}
	
	public void findRQByIBMerAssignedToSales(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage, String renewalStatusValue, String salesRep){
		loggerContxt.info(String.format("Find Renewal quote by IBMer (Assigned to Sale rep %s)...",salesRep));
		renewalQuoteSearchPage.findRQByIBMerAssignedToSales(salesRep, renewalStatusValue);
	}
	
	public void findRQByIBMerAssignedToCoordinator(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage, String renewalStatusValue, String coordinator){
		loggerContxt.info(String.format("Find Renewal quote by IBMer (Assigned to Sale rep %s)...",coordinator));
		renewalQuoteSearchPage.findRQByIBMerAssignedToCoordinator(coordinator, renewalStatusValue);
	}
	
	public void findRQByCustomerAgreementAndSite(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage, String siteNumber, String renewalStatusValue){
		loggerContxt.info(String.format("Find Renewal quote by Customer (Site Number %s)...",siteNumber));
		renewalQuoteSearchPage.findRQByCustomerAgreementAndSite(siteNumber, renewalStatusValue);
	}
	
	public void findRQByCustomerAttributes(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage, String customerName,String countryRegion,String stateProvince, String renewalStatusValue){
		loggerContxt.info(String.format("Find Renewal quote by Customer (Customer name %s)...",customerName));
		renewalQuoteSearchPage.findRQByCustomerAttributes(customerName, countryRegion, stateProvince, renewalStatusValue);
	}
	
	public void findRQBySellerPartnerSiteNumber(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage,String partnerSiteNumber, String renewalStatusValue){
		loggerContxt.info(String.format("Find Renewal quote by reseller Partner (Partner site number %s)...",partnerSiteNumber));
		renewalQuoteSearchPage.findRQBySellerPartnerSiteNumber(partnerSiteNumber, renewalStatusValue);
	}
	
	public void findRQByPayerPartnerSiteNumber(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage,String partnerSiteNumber, String renewalStatusValue){
		loggerContxt.info(String.format("Find Renewal quote by payer Partner (Partner site number %s)...",partnerSiteNumber));
		renewalQuoteSearchPage.findRQByPayerPartnerSiteNumber(partnerSiteNumber, renewalStatusValue);
	}
	
	public void findRQBySellerPartnerByAttributes(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage,String partnerCompanyName, String countryRegion, String stateProvince, String renewalStatusValue){
		loggerContxt.info(String.format("Find Renewal quote by payer Partner (Partner company name %s)...",partnerCompanyName));
		renewalQuoteSearchPage.findRQBySellerPartnerByAttributes(partnerCompanyName, countryRegion, stateProvince, renewalStatusValue);
	}
	
	public void findRQByPayerPartnerByAttributes(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage,String partnerCompanyName, String countryRegion, String stateProvince, String renewalStatusValue){
		loggerContxt.info(String.format("Find Renewal quote by payer Partner (Partner company name %s)...",partnerCompanyName));
		renewalQuoteSearchPage.findRQByPayerPartnerByAttributes(partnerCompanyName, countryRegion, stateProvince, renewalStatusValue);
	}
	
	public void findRQByRegion(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage,String saleOrganization, String countryRegion, String stateProvince, String renewalStatusValue){
		loggerContxt.info(String.format("Find Renewal quote by Region (sales organization name %s)...",saleOrganization));
		renewalQuoteSearchPage.findRQByRegion(saleOrganization, countryRegion, stateProvince, renewalStatusValue);
	}

	public void vieRQResult(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage, String quoteNumber){
		loggerContxt.info(String.format("Click the quote number::%s link in the Selected renewal quotes page..",quoteNumber));
		renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().viewQuoteDetailsByNumber(quoteNumber);
	}
	
	public void viewFirstQuoteDetails(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click the first quote number link in the Selected renewal quotes page..");
		renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().viewFirstQuoteDetails();
	}
	
	public void changeCriteria(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click the Change search criteria link in Selected renewal quotes page..");
		renewalQuoteSearchPage = renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().changeCriteria();
	}
	
	public void clickRQResultDetailsPageDetailsTab(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click the 'Detais' tab in the Renewal quote information page...");
		renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().getRQSearchResultDetailsPage().clickDetailsTab();
		renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().getRQSearchResultDetailsPage().newCache();
	}
	
	public void clickRQResultDetailsPageSalesTrankingTab(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click the 'Sales tranking' tab in the Renewal quote information page...");
		renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().getRQSearchResultDetailsPage().clickSalesTrankingTab();
		renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().getRQSearchResultDetailsPage().newCache();
	}
	
	public void clickRQResultDetailsPageContactsTab(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click the 'Contacts' tab in the Renewal quote information page...");
		renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().getRQSearchResultDetailsPage().clickContactsTab();
		renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().getRQSearchResultDetailsPage().newCache();
	}
	
	public void clickRQResultDetailsPageContactsTabPartnerLink(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click the Parter link in 'Contacts' tab in the Renewal quote information page...");
		renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().getRQSearchResultDetailsPage().clickPartnerLinkInContactsTab();
		renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().getRQSearchResultDetailsPage().newCache();
	}
	
	public void clickRQResultDetailsPageContactsTabIBMLink(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click the IBM link in 'Contacts' tab in the Renewal quote information page...");
		renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().getRQSearchResultDetailsPage().clickIBMLinkInContactsTab();
		renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().getRQSearchResultDetailsPage().newCache();
	}
	
	public void findRQCustomerActions(SQORQCustomerActionsSearchPage customerActioinsSearchPage,String customerType, String salesRep){
		loggerContxt.info("Click submit button in 'Customer actions search' page...");
		customerActioinsSearchPage.findRQCustomerActions(customerType, salesRep);
	}

	public void changeCriteria(SQORQCustomerActionsSearchPage customerActioinsSearchPage){
		loggerContxt.info("Click the Change search criteria link in 'Customer actions report' page..");
		customerActioinsSearchPage = customerActioinsSearchPage.changeCriteria();
	}
	
	public void clickAdditionInfoLink(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage){
		loggerContxt.info("Click 'Additional agreement information' link in the 'Renewal quote information' page, Summary tab...");
		renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().getRQSearchResultDetailsPage().clickAdditionInfoLink();
	}
	
	public void testCustomerDetailsAndHistoryPage(SQORenewalQuoteSearchTabPage renewalQuoteSearchPage, String quoteNumber){
		SQORenewalQuoteCustomerDetailsAndHistoryPage page = renewalQuoteSearchPage.getRenewalQuoteSearchResultPage().getRQSearchResultDetailsPage().getCustomerDetailsAndHistoryPage();
		loggerContxt.info("Click on 'Contacts' tab in 'DSW customer details and history' page...");
		page.clickContactsTab();
		loggerContxt.info("Click 'Partner information' link in 'Contacts' tab in 'DSW customer details and history' page...");
		page.clickPartnerInfoLinkInContactsTab();
		loggerContxt.info("Click 'Renewals' tab in 'DSW customer details and history' page...");
		page.clickRenewalsTab();
		loggerContxt.info("Click 'Entitlements' tab in 'DSW customer details and history' page...");
		page.clickEntitlementsTab();
		loggerContxt.info("Click 'Active and future Software Subscription and Support entitlements' link in 'Entitlements' tab in 'DSW customer details and history' page...");
		page.clickSubscriptionAndSupportLink();
		loggerContxt.info("Click 'View entitlements in FastPass' link in 'Entitlements' tab in 'DSW customer details and history' page...");
		page.clickViewEntitlementLink();
		loggerContxt.info("Click 'Software as a Service / Monthly Licensing' tab in 'DSW customer details and history' page...");
		page.clickSaaSTab();
		loggerContxt.info("Click 'Inactive Software as a Service / Monthly Licensing' link in 'Software as a Service / Monthly Licensing' tab in 'DSW customer details and history' page...");
		page.clickInactiveSaaSLink();
		loggerContxt.info("Click 'Order History' tab in 'DSW customer details and history' page...");
		page.clickOrderHistoryTab();
		page.selectSalesOrderDate();
		page.clickgenerateSummaryReport();
		page = page.newCache();
		loggerContxt.info("Click 'Generate Details Report' link in 'Order History' tab in 'DSW customer details and history' page...");
		page.clickgenerateDetailReport();
		page.clickSearchByOrderNumberLink();
		page = page.newCache();
		page.fillOrderNumberInput(quoteNumber);
		loggerContxt.info(String.format("Click 'Generate Summary Report' link for quote number::%s in 'Order History' tab in 'DSW customer details and history' page...",quoteNumber));
		page.clickgenerateSummaryReport();
		page = page.newCache();
		loggerContxt.info(String.format("Click 'Generate Details Report' link for quote number::%s in 'Order History' tab in 'DSW customer details and history' page...",quoteNumber));
		page.clickgenerateDetailReport();
		
	}
	
	public SQOQuoteCommonFlow getQuoteFlow() {
		return quoteFlow;
	}
	
	public void setQuoteFlow(SQOQuoteCommonFlow quoteFlow) {
		this.quoteFlow = quoteFlow;
	}
	
}
