package com.ibm.dsw.automation.testcase.xprs;

import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.PGSQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.pgs.CreateQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.CustomerDetailHistoryPage;
import com.ibm.dsw.automation.pageobject.pgs.DisplayCustListPage;
import com.ibm.dsw.automation.pageobject.pgs.FindExistingCustomerPage;
import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;
/**
 * https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/410740
 * Script Name : PGSPPXPRSRetrieveSaasReportTest<br>
 * Objective: Prerequisites:<br>
	Retrieve Customer SaaS Report (main script) : 
	LoginPage	Enter an BP user ID and password to authenticate (techdataUS@ibm.com/testing1)
	Page1	Click on '7000379' in the Sign in page
	Page2	Click on "Create a sales quote" link in the left nav
	Page3	Choose 'United States' in the country/region drop down list, click 'Continue' button
	Page4	Click on "Find an existing customer" link
	Page5	Enter '3044556' in the input box for 'Site or customer number' and click 'Submit'
	Page6	Click on 'Select customer'
	Page7	On Customer and business partners tab, click on the "View and make changes" link
	Page8	Click on the "Inactive Software as a Service" on the page with title "DSW customer details and history"

 * Additional Info: check in firefox<br>
 * Original Host: <br>
 * Windows
 * 
 * @since 2012-12-6
 * @author zhou jun
 */
public class PGSPPXPRSRetrieveSaasReportTest extends BaseTestCase {
	private static Properties prop;
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(PGSPPXPRSRetrieveSaasReportTest.class.getSimpleName());
	
	
/*	@Override
	@BeforeClass
	public void initTestData() {
		clazzName = "PGSPPXPRS_Retrieve_Saas_Reprot_Test";
		if (prop == null) {
			prop = this
					.getTestDataProp("/com/ibm/dsw/automation/testcase/pgs/PGSPPXPRSRetrieveSaasReportTest.properties");
		}
	}*/

	@Test(description = "Retrieve_Saas_Reprot_Test")
	public void run() {
		
/*		// Login PGS
		LoginPage page = new LoginPage(this.driver, new Long(20));
		PGSSiteSelectPage siteSelectPage = page.loginAsToSiteSelectPage(
				getProperty( ".username"),
				getProperty( ".password"));
		PAOHomePage homePage = siteSelectPage.gotoPAOHomePage(getProperty( ".siteSelect"));

		loggerContxt.info("go to PGS");
		PGSHomePage pgsHomePage = homePage.gotoPGS();*/
		// Login PGS
		PGSQuoteCommonFlow quoteFlow = getPGSCommonFlow();

 		PGSHomePage pgsHomePage = quoteFlow.loginPgs(LOGON_IN_PGS_VIA_PGS_WEBAUTH);
		loggerContxt.info("Login PGS finished.....");

		loggerContxt
				.info("verefy current page whether having this content ......"
						+ getProperty( ".pgsTitle"));
		assertPresentText(getProperty( ".pgsTitle"));

		loggerContxt
				.info("Click on 'Create a sales quote' link in the left nav");
		CreateQuotePage cq = pgsHomePage.gotoCreateQuote();

		loggerContxt
				.info("Choose 'United States' in the country/region drop down list, click 'Continue' button");
		MyCurrentQuotePage currentQuotePage = cq.createQuote(getProperty( ".country"));

		loggerContxt
				.info("verefy current page whether having this content ......"
						+ getProperty( ".currentQuoteTitle"));
		assertPresentText(getProperty( ".currentQuoteTitle"));

		// Find an existing customer
		FindExistingCustomerPage searchCustPage = currentQuotePage
				.goToFindCustomerTab();
		loggerContxt.info("go to search customer page.....");

		// display customer
		DisplayCustListPage displayCustListPage = searchCustPage
				.displayCustomerListBySiteNum(getProperty( ".siteNum"));
		loggerContxt.info("display customer page.....");

		// select customer and back current quote page
		currentQuotePage = displayCustListPage.selectCustomer();
		loggerContxt.info("select customer and back current quote page.....");

		// On Customer and business partners tab, click on the
		// "View and make changes" link
		CustomerDetailHistoryPage detailHistoryPage = currentQuotePage
				.goToCustDetaiHistory();
		loggerContxt
				.info("On Customer and business partners tab, click on the 'View and make changes' link.....");

		loggerContxt
				.info("verefy current page whether having this content ......"
						+ getProperty( ".customerDetailHistory"));
		assertPresentText(getProperty( ".customerDetailHistory"));

		// Click on the "Inactive Software as a Service" on the page with title
		// "DSW customer details and history"
		detailHistoryPage.goInactiveSoftwareAsaService();
		loggerContxt
				.info("Click on the 'Inactive Software as a Service' on the page with title 'DSW customer details and history'.....");
		try {
			detailHistoryPage.saveHtmlSourceAs(htmlSourceFolder+"/PGSPPXPRSRetrieveSaasReportTest.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			driver.close();
		}
	}

	public static void main(String[] args) throws Exception {

		PGSPPXPRSRetrieveSaasReportTest test = new PGSPPXPRSRetrieveSaasReportTest();
		test.initTestData();
		test.setUp();
		test.run();
	}
}
