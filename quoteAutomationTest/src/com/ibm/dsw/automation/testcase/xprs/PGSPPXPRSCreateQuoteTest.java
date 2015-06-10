package com.ibm.dsw.automation.testcase.xprs;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.PGSQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.pgs.BrowsePartsTabPage;
import com.ibm.dsw.automation.pageobject.pgs.BrowseSoftwareAsServiceTabPage;
import com.ibm.dsw.automation.pageobject.pgs.CreateQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.DisplayCustListPage;
import com.ibm.dsw.automation.pageobject.pgs.DisplayResellerListPage;
import com.ibm.dsw.automation.pageobject.pgs.FindExistingCustomerPage;
import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.pgs.RetrieveSavedSalesQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.SalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.pgs.SelectAResellerPage;
import com.ibm.dsw.automation.pageobject.pgs.ServiceConfigurePage;
import com.ibm.dsw.automation.pageobject.pgs.SpecialBidTabPage;

/**
 * https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/410740
 * Script Name : PGSPPXPRSCreateQuoteTest<br>
 * Objective: Prerequisites:<br>
Create PGS sales quote (main script) : 
Enter_URL	Open jump page:
	
LoginPage	Enter an BP user ID and password to authenticate (avnetus@ibm.com/testing1)
Page1	Click on '7003910' '7000695' in the Sign in page
Page2	Click on "Create a sales quote" link in the left nav
Page3	Choose 'United States' in the country/region drop down list, click 'Continue' button
Page4	Click on "Find an existing customer" link
Page5	Enter '3044556' in the input box for 'Site or customer number' and click 'Submit'
Page6	Click on 'Select customer'
Page7	On Customer and business partners tab, Enter 'Dummy' in the First Name field and Last name field, enter '111' in the phone and fax field, enter 'avnetus@ibm.com' in the Email field and click 'Customer and business partners tab' to persist the change
Page8	Click on "My current quote" link on the left nav
Page9	Click on "Select a reseller" link if the link is there
Page10	Enter "0007186058" in the reseller number field and click "Submit" button, click "Select reseller" link in the reseller selection result page
Page11	Click on "My current quote" link on the left nav
Page12	Click on the "Parts and pricing" tab link
Page13	Click on the "Browse Software as a Service" link
Page14	Explore to "WebSphere Software" -->"IBM Applicatn Integratn Middleware" --> "Cast Iron" and click on the "Configure this service" on the right side of "IBM WebSphere Cast Iron Express"
Page15	Enter "10" to the first quantity input fields, click on the "Submit" button, wait until the part and price tab displays
Page16	Click on "My current quote" link on the left nav
Page17	Click on the "Sales information" tab link
Page18	Enter "XPRS test" to both the title field and full description field.
Page19	Click on the "Business partner opportunity number not provided " radio button.
Page20	Select the last date of the current month as the "Quote expiration date"
Page20a	Click on the "Approval" tab link to persist the change
Page20b	Select the first option for the field " * Approval district: ".
Page21	Enter "XPRS Test" in the "Justification Summary" field
Page22	Click on the "Approval" tab link to persist the change, input 'XPRS test' in the Justification Summary input box.
Page22a	Click on the "Save quote as draft" link
Page22b	Click on the "Create a sales quote" link on the left nav
Page23	Click on the "Retrieve a saved sales quote" sub-link in the left nav
Page24	Click the "open" link of the first quote in the result page
Page25	Click "Submit button"

 * Additional Info: check in firefox<br>
 * Original Host: <br>
 * Windows
 * 
 * @since 2012-12-6
 * @author zhou jun
 */
public class PGSPPXPRSCreateQuoteTest extends BaseTestCase {
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(PGSPPXPRSCreateQuoteTest.class.getSimpleName());
	

	@Test(description = "Create a SQO quote")
	public void run() {
		// Login PGS
		PGSQuoteCommonFlow quoteFlow = getPGSCommonFlow();

 		PGSHomePage pgsHomePage = quoteFlow.loginPgs(LOGON_IN_PGS_VIA_PGS_WEBAUTH);
		loggerContxt.info("Login PGS finished.....");
	/*	
		LoginPage page = new LoginPage(this.driver, new Long(20));
		PGSSiteSelectPage siteSelectPage= page.loginAsToSiteSelectPage(getLoginUser(), getLoginPasswd());
		PAOHomePage homePage = siteSelectPage.gotoPAOHomePage(getProperty( ".siteSelect"));

		loggerContxt.info("go to PGS");
		PGSHomePage pgsHomePage = homePage.gotoPGS();*/

		loggerContxt
				.info("verefy current page whether having this content ......"
						+ getProperty( ".pgsTitle"));
		//assertPresentText(getProperty( ".pgsTitle"));

		loggerContxt.info("Click on 'Create a sales quote' link in the left nav");
		CreateQuotePage cq = pgsHomePage.gotoCreateQuote();
		
		loggerContxt.info("Choose 'United States' in the country/region drop down list, click 'Continue' button");
		MyCurrentQuotePage currentQuotePage = cq.createQuote(getProperty( ".country"));

		loggerContxt
				.info("verefy current page whether having this content ......"
						+ getProperty( ".currentQuoteTitle"));
		assertPresentText(getProperty( ".currentQuoteTitle"));
	 	// Find an existing customer
		FindExistingCustomerPage searchCustPage=currentQuotePage.goToFindCustomerTab();
		loggerContxt.info("go to search customer page.....");

		// display customer
		DisplayCustListPage displayCustListPage=searchCustPage.displayCustomerListBySiteNum(getProperty( ".siteNum"));
		loggerContxt.info("display customer page.....");
		
		// select customer and back current quote page
		currentQuotePage=displayCustListPage.selectCustomer();
		loggerContxt.info("select customer and back current quote page.....");


		loggerContxt.info("On Customer and business partners tab, Enter 'Dummy' /n" +
				" in the First Name field and Last name field, enter '111' in the phone and fax field," +
				"/n enter 'avnetus@ibm.com' in the Email field " +
				"/n and click 'Customer and business partners tab' to persist the change");
		currentQuotePage.fillCustomerContact(
				getProperty( ".cntFirstName"),
				getProperty( ".cntLastName"),
				getProperty( ".cntPhoneNumFull"),
				getProperty( ".cntFaxNumFull"),
				getProperty( ".cntEmailAdr"));
		

		loggerContxt.info("Click on 'My current quote' link on the left nav");
		currentQuotePage.refreshCurrentQt();
		
		// Find reseller
		loggerContxt.info("Enter '0007186058' in the reseller number field and click 'Submit' button");
		SelectAResellerPage searchResellerPage=currentQuotePage.goToFindResellerTab();
		loggerContxt.info("go to search reseller page.....");
		
		// display reseller
		loggerContxt.info("click 'Select reseller' link in the reseller selection result page");
		DisplayResellerListPage displayResellerListPage = searchResellerPage
				.displayResellerListByNum(getProperty( ".resellerNum"));
		loggerContxt.info("display reseller page.....");
		
		loggerContxt.info("Click on 'My current quote' link on the left nav");
		currentQuotePage.refreshCurrentQt();
		
		loggerContxt.info("Click on the 'Parts and pricing' tab link");
		PartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
		
		loggerContxt
				.info("verefy current page whether having this content ......"
						+ getProperty( ".partAndPricing"));
		assertPresentText(getProperty( ".partAndPricing"));
		
		
		
		loggerContxt.info("Click on Brand Lotus Click on Lotus 123 Click on License + SW Subscription & Support " + "Check the checkbox of the first part listed Click the 'Add selected parts to draft quote' link.....");
		BrowsePartsTabPage browsePartsTabPage = partsAndPricingTab.browsePartsLinkClick();
		browsePartsTabPage.browseLotusParts();
		browsePartsTabPage.addSelectedPartsToDraftQuoteLinkClick();
		browsePartsTabPage.returnToDraftQuoteLinkClick();	
		
		loggerContxt.info("delete the part in order to add saas part.....");
		partsAndPricingTab.deleteParts("D5CF5LL");
		
		//xplore to "WebSphere Software" -->"IBM Applicatn Integratn Middleware" --> "Cast Iron" and click on the 
		//"Configure this service" on the right side of "IBM WebSphere Cast Iron Express"
		loggerContxt.info("xplore to WebSphere Software -->IBM Applicatn Integratn Middleware --> Cast Iron and click on the Configure this service on the right side of IBM WebSphere Cast Iron Express.....");
		/*partsAndPricingTab.browseSoftwareAsAServiceLinkClick();
		String text = getProperty("ECustomerCare");
		if (isTextPresent(text)) {
			loggerContxt.info(text);
		}
		
		BrowseSoftwareAsServiceTabPage browseSoftwareAsServiceTabPage = new SQOBrowseSoftwareAsServiceTabPage(this.driver);
		browseSoftwareAsServiceTabPage.configureSaas4CastIron();
		browseSoftwareAsServiceTabPage.addFirstPart("10");
		browseSoftwareAsServiceTabPage.waitForElementLoading(new Long(25000));*/
		
		
		BrowseSoftwareAsServiceTabPage browseSoftwareAsServiceTabPage = partsAndPricingTab.browseSoftwareAsAServiceLinkClick();
		browseSoftwareAsServiceTabPage.selectAgreement();
		//browseSoftwareAsServiceTabPage.browserAndConfigureService(propBean.getSaasPartPath());
		
		browseSoftwareAsServiceTabPage.clickAllImgs();
		ServiceConfigurePage scPage =browseSoftwareAsServiceTabPage.selectSaasPID(getProperty(".SaasPartId"));
		
		//ServiceConfigurePage scPage = browseSoftwareAsServiceTabPage.switchToServiceConfigurePage();
		scPage.configureService();
//		partsAndPricingTab.editProvis
		
		
		
		//Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SalesInfoTabPage salesTab=partsAndPricingTab.gotoSalesInfoTabPGS();
		loggerContxt.info("Go to SalesInf Tab end.....");
		
		salesTab.enterSalesInf(getProperty( ".briefTitle"),getProperty( ".quoteDesc"));
		
		//go to Special Bid Tab
		loggerContxt.info("go to Special Bid Tab.....");
		SpecialBidTabPage spcialBidTab=salesTab.goToSpecialBidTabClick();
		spcialBidTab.enterSpcialBidInf(getProperty( ".justificationSummary"));
		currentQuotePage=spcialBidTab.rtnToCPTab();
		currentQuotePage.selectExpirationDate(30);
		
		currentQuotePage.saveQuoteAsDraft();
		
		RetrieveSavedSalesQuotePage savedSalesQuote=currentQuotePage.goRetrieveSavedSalesQuoteTab();
		loggerContxt
				.info("verefy current page whether having this content ......"
						+ getProperty( ".retrieveSavedSalesQuoteTitle"));
		assertPresentText(getProperty( ".retrieveSavedSalesQuoteTitle"));
		savedSalesQuote.goViewDetailSavedQuote();
		
		loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
	}
	
	public static void main(String[] args) throws Exception {
	
		PGSPPXPRSCreateQuoteTest test = new PGSPPXPRSCreateQuoteTest();
		test.initTestData();
		test.setUp();
		test.run();
		//test.teardown();
		//test.quitWebdriver();
	}
}
