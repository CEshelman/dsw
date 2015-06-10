package com.ibm.dsw.automation.testcase.xprs.sqo;

import org.testng.log4testng.Logger;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.flow.SQORenewalQuoteSearchFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQORQCustomerActionsSearchPage;
import com.ibm.dsw.automation.pageobject.sqo.SQORenewalQuoteSearchTabPage;

/**
 * Used for the XPRS script auto validation in SIT phase.
 * XPRS script name: Renewal Quote Searches(main script) 
 * Team room link: Notes://D01DBL35/8525721300181EEE/4B87A6F6EAEAADD385256A2D006A582C/9BFDA5C0381FBA8D8525731D0072A9E6 
 * @author duzhiwei
 *
 */
public class SQORenewalQuoteSearchAutoTest extends BaseTestCase{

	public static Logger loggerContxt = Logger.getLogger(SQORenewalQuoteSearchAutoTest.class);
	
	SQOHomePage sqoHomePage = null;
	SQORenewalQuoteSearchFlow quoteFlow = null;
	SQORenewalQuoteSearchTabPage renewalQuoteSearchPage = null;
	SQORQCustomerActionsSearchPage customerActionsPage = null;
	SQOLogin sqoLogin = new SQOLogin();
	
	public void testRenewalQuoteSearch(){
		try {
			sqoLogin.loginSqo(getProperty(".logonFlg"));
		} catch (Exception e) {
			loggerContxt.error("Failed to login sqo main page...");
			return;
		}
		quoteFlow = new SQORenewalQuoteSearchFlow(sqoLogin.getQuoteFlow());
		sqoHomePage = sqoLogin.getSqoHomePage();
		
		renewalQuoteSearchPage = sqoHomePage.gotoSQORenewalQuote();
		
		String quoteNumber = propSub.getProperty("quote.number");
		quoteFlow.clickByNumberTab(renewalQuoteSearchPage);
		quoteFlow.findRQByNumber(renewalQuoteSearchPage, quoteNumber);
		quoteFlow.vieRQResult(renewalQuoteSearchPage, quoteNumber);
		quoteFlow.clickRQResultDetailsPageDetailsTab(renewalQuoteSearchPage);
		quoteFlow.clickRQResultDetailsPageSalesTrankingTab(renewalQuoteSearchPage);
		quoteFlow.clickRQResultDetailsPageContactsTab(renewalQuoteSearchPage);
		quoteFlow.clickRQResultDetailsPageContactsTabPartnerLink(renewalQuoteSearchPage);
		quoteFlow.clickRQResultDetailsPageContactsTabIBMLink(renewalQuoteSearchPage);
		
		renewalQuoteSearchPage = sqoHomePage.gotoSQORenewalQuote();
		String renewalStatusValue = propSub.getProperty("renewal.status");
		quoteFlow.findRQByIBMerAssignedToMe(renewalQuoteSearchPage, renewalStatusValue);
		quoteFlow.changeCriteria(renewalQuoteSearchPage);
		
		quoteFlow.clickSalesSpecifiedLinkInByIBMerTab(renewalQuoteSearchPage);
		String salesRep = propSub.getProperty("Sales.representative");
		quoteFlow.findRQByIBMerAssignedToSales(renewalQuoteSearchPage, renewalStatusValue, salesRep);
		quoteFlow.changeCriteria(renewalQuoteSearchPage);
		String coordinator = propSub.getProperty("Renewal.coordinator");
		
		quoteFlow.clickCoordinatorSpecifiedLinkInByIBMerTab(renewalQuoteSearchPage);
		quoteFlow.findRQByIBMerAssignedToCoordinator(renewalQuoteSearchPage, renewalStatusValue, coordinator);
		quoteFlow.changeCriteria(renewalQuoteSearchPage);
		
		quoteFlow.clickByCustomerTab(renewalQuoteSearchPage);
		String siteNumber = propSub.getProperty("Site.number");
		quoteFlow.findRQByCustomerAgreementAndSite(renewalQuoteSearchPage, siteNumber, renewalStatusValue);
		quoteFlow.changeCriteria(renewalQuoteSearchPage);
		
		quoteFlow.clickAttributeLinkInByCustomerTab(renewalQuoteSearchPage);
		String customerName = propSub.getProperty("Customer.name");
		String countryRegion = propSub.getProperty("country.region");
		String stateProvince = propSub.getProperty("State.province");
		quoteFlow.findRQByCustomerAttributes(renewalQuoteSearchPage, customerName, countryRegion, stateProvince, renewalStatusValue);
		quoteFlow.changeCriteria(renewalQuoteSearchPage);
		
		quoteFlow.clickByPartnerTab(renewalQuoteSearchPage);
		String partnerSiteNumber = propSub.getProperty("partner.site.number");
		quoteFlow.findRQBySellerPartnerSiteNumber(renewalQuoteSearchPage,partnerSiteNumber, renewalStatusValue);
		quoteFlow.changeCriteria(renewalQuoteSearchPage);
		quoteFlow.findRQByPayerPartnerSiteNumber(renewalQuoteSearchPage, partnerSiteNumber, renewalStatusValue);
		quoteFlow.changeCriteria(renewalQuoteSearchPage);
		
		String partnerCompanyName = propSub.getProperty("partner.company.name");
		String partnerCompanyName1 = propSub.getProperty("partner.company.name1");
		/*String countryRegion = propSub.getProperty("country.region");
		String stateProvince = propSub.getProperty("State.province");*/
		quoteFlow.clickAttributeLinkInByPartnerTab(renewalQuoteSearchPage);
		quoteFlow.findRQBySellerPartnerByAttributes(renewalQuoteSearchPage, partnerCompanyName, countryRegion, stateProvince, renewalStatusValue);
		quoteFlow.changeCriteria(renewalQuoteSearchPage);
		quoteFlow.findRQByPayerPartnerByAttributes(renewalQuoteSearchPage, partnerCompanyName1, countryRegion, stateProvince, renewalStatusValue);
		quoteFlow.changeCriteria(renewalQuoteSearchPage);
		
		quoteFlow.clickByRegionTab(renewalQuoteSearchPage);
		String saleOrganization = propSub.getProperty("sales.organization");
		quoteFlow.findRQByRegion(renewalQuoteSearchPage, saleOrganization, countryRegion, stateProvince, renewalStatusValue);
		
		customerActionsPage = sqoHomePage.gotoCustomerActions();
		quoteFlow.findRQCustomerActions(customerActionsPage, null, null);
		quoteFlow.changeCriteria(customerActionsPage);
		String customerSalesRep = propSub.getProperty("customer.sales.representative");
		quoteFlow.findRQCustomerActions(customerActionsPage, null, customerSalesRep);
		
		renewalQuoteSearchPage = sqoHomePage.gotoSQORenewalQuote();
		quoteFlow.clickByCustomerTab(renewalQuoteSearchPage);
		String siteNumber2 = propSub.getProperty("Site.number2");
		quoteFlow.findRQByCustomerAgreementAndSite(renewalQuoteSearchPage, siteNumber2, renewalStatusValue);
		quoteFlow.viewFirstQuoteDetails(renewalQuoteSearchPage);
		quoteFlow.clickAdditionInfoLink(renewalQuoteSearchPage);
		String orderQuoteNumber = propSub.getProperty("order.history.quote.number");
		quoteFlow.testCustomerDetailsAndHistoryPage(renewalQuoteSearchPage, orderQuoteNumber);
		loggerContxt.info(this.getScriptName() + " Successfully finished.....");
		
	}
	
	public static void main(String[] args)  {
		SQORenewalQuoteSearchAutoTest testcase = new SQORenewalQuoteSearchAutoTest();
		testcase.setScriptName("SQO Renewal Quote Search Script Automation Test");
		try {
			testcase.initTestData();
			testcase.env = testcase.settingsProp.getProperty("env");
			testcase.testRenewalQuoteSearch();
			testcase.sendAlertMail("build/mailBody.vm","succeed",null);
		} catch (Exception e) {
			loggerContxt.error("Some issues encountered when doing the basic verfication testing...");
//			testcase.sendAlertMail("build/mailBody.vm","failed",null);
			e.printStackTrace();
			
		}finally{
			if (testcase.getDriver() != null) {
				
				testcase.getDriver().close();
			}
			
//			testcase.teardown();
		}
	}
}
