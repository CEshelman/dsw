package com.ibm.dsw.automation.testcase.xprs;


import java.io.IOException;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.PGSQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.pgs.DisplayStatusSearchReslutPage;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;
import com.ibm.dsw.automation.pageobject.pgs.StatusSearchTabPage;
/**
 * https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/410740
 * Script Name : PGSPPXPRSStatusSearchTest<br>
 * Objective: Prerequisites:<br>
Status Searches (main script) :
Enter_URL	Open jump page:
	
LoginPage	Enter an BP user ID and password to authenticate (techdataUS@ibm.com/testing1) 
Page 41	Click on '7000379' in the Sign in page   
Page 42	Click on the "Status" link in the left nav
Page 43	Check all checkboxes of the following sections
	Find the following quote types
	Find quotes and special bids with the following overall statuses
	
	Select "Date submitted (descending)" in the Sort by dropdown
	Check the Make above selections my default checkbox
	
	Select the Find all matching quotes
	
	Press on the "Find quotes" button
Page 44	On the quote status search result page, click on "Change selection criteria" link
Page 45	Click on the "Find by number" tab
	
	Enter "0002351661" in the input box of "Quote number, Quote reference or Quote confirmation number"
	
	Press on the "Find quotes" button

 * Additional Info: check in firefox<br>
 * Original Host: <br>
 * Windows
 * 
 * @since 2012-12-6
 * @author zhou jun
 */
public class PGSPPXPRSStatusSearchTest extends BaseTestCase {

	//private static Properties prop;
	
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(PGSPPXPRSStatusSearchTest.class.getSimpleName());
/*	@Override
	@BeforeClass
	public void initTestData() {
		clazzName = "PGSPPXPRS_Status_Search_Test";
		if (prop == null) {
			prop = this
					.getTestDataProp("/com/ibm/dsw/automation/testcase/xprs/PGSPPXPRSStatusSearchTest.properties");
		}
	}
*/
	@Test(description = "PGSPPXPRS_Status_Search_Test")
	public void run() {
	
	/*	// Login PGS
		LoginPage page = new LoginPage(this.driver, new Long(20));
		
		loggerContxt.info("logon in user ......"+getProperty(prop, ".username"));
		loggerContxt.info("logon in password ......"+getProperty(prop, ".password"));
		PGSSiteSelectPage siteSelectPage =  page.loginAsToSiteSelectPage(
				getProperty(prop, ".username"),
				getProperty(prop, ".password"));
		PAOHomePage homePage = siteSelectPage.gotoPAOHomePage(getProperty(prop, ".siteSelect"));

		loggerContxt.info("go to PGS");
		PGSHomePage pgsHomePage = homePage.gotoPGS();*/
		
		// Login PGS
		PGSQuoteCommonFlow quoteFlow = getPGSCommonFlow();

 		PGSHomePage pgsHomePage = quoteFlow.loginPgs(LOGON_IN_PGS_VIA_PGS_WEBAUTH);
		loggerContxt.info("Login PGS finished.....");

		loggerContxt
				.info("verefy current page whether having this content ......"
						+ getProperty(propSub, ".pgsTitle"));
		assertPresentText(getProperty(propSub, ".pgsTitle"));

		loggerContxt.info("Click on the 'Status' link in the left nav");
		StatusSearchTabPage statusSearchPage = pgsHomePage.gotoStatus();

		loggerContxt
				.info("Check all checkboxes of the following sections Find the following quote types "
						+ "Find quotes and special bids with the following overall statuses"
						+ " Select 'Date submitted (descending)' in the Sort by dropdown "
						+ "Check the Make above selections my default checkbox"
						+ " Select the Find all matching quotes Press on the 'Find quotes' button");
		DisplayStatusSearchReslutPage statusResultPage = statusSearchPage
				.goDispAllQuoteReslt();
		loggerContxt
				.info("On the quote status search result page, click on 'Change selection criteria' link");
		statusSearchPage = statusResultPage.changeCriteriaLink();

		loggerContxt
				.info("Click on the 'Find by number' tab Enter '0002351661' in the input box of 'Quote number,"
						+ "Quote reference or Quote confirmation number'Press on the 'Find quotes' button");
		statusResultPage = statusSearchPage.goDispQuoteResltByQuoteNum(getProperty(propSub, ".quoteNum"));
		
		loggerContxt.info("@Test PGSPPXPRSStatusSearchTest has passed!");
		try {
			statusResultPage.saveHtmlSourceAs(htmlSourceFolder+"/PGSPPXPRSStatusSearchTest.html");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			driver.close();
		}
	}

	public static void main(String[] args) throws Exception {

		PGSPPXPRSStatusSearchTest test = new PGSPPXPRSStatusSearchTest();
		test.initTestData();
		test.setUp();
		test.run();
	}
}
