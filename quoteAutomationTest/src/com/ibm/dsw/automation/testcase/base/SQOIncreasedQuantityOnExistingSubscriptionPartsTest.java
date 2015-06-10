package com.ibm.dsw.automation.testcase.base;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.pageobject.sqo.SQOBrowseSoftwareAsServiceTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOCreateQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayCustListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOJumpPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOOSearchCustPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSpecialBidTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitCurrDraftQuotePage;


/*
 * Task num. 399309
 */
@SuppressWarnings("deprecation")
public class SQOIncreasedQuantityOnExistingSubscriptionPartsTest extends
		BaseTestCase {

	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOIncreasedQuantityOnExistingSubscriptionPartsTest.class.getName());
	
	private SQOMyCurrentQuotePage currentQuotePage = null;
	
	public final static String[] SAAS_PART_PAUN_CLICK_NODES = {"img2","img2_0","img2_0_0"};
	public final static String SAAS_PART_PAUN_CONFIGURE_ID = "5725F67";
	public final static String[][] SAAS_PART_PAUN_COMPONENTS = {
		{"//input[contains(@id, 'qtySuffix')][1]","2"},	
		
	};


	@Test(description = "Create a SQO quote")
	public void runCreateQuote() {
		SQOHomePage sqoHomePage = null;
		loggerContxt.info("env....." + env.hashCode());
		loggerContxt.info("driver....." + driver.hashCode());
	
        reset(prop.getProperty(env + ".sqo_jump_url"));
        SQOJumpPage jumpPage = new SQOJumpPage(this.driver);
		jumpPage.loginIn("US", "dswweb5@us.ibm.com", "1");

		sqoHomePage = new SQOHomePage(driver);

		loggerContxt.info("go to sqo home page finished.....");
		SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();

		// Create a SQO quote
		currentQuotePage = cq.createQuote();
		loggerContxt.info("create quote page finished.....");

	}

	@Test(description = "quote submit ")
	public void runSubmitQuote() {
		// Find an existing customer
		SQOOSearchCustPage searchCustPage = currentQuotePage.findCustbyClick();
		loggerContxt.info("go to search customer page.....");
		
		// display customer
		SQODisplayCustListPage displayCustListPage = searchCustPage
				.displayCustomerListByName();
		loggerContxt.info("display customer page.....");

		// select customer and back current quote page
		currentQuotePage = displayCustListPage.selectCustomer();
		loggerContxt.info("select customer and back current quote page.....");

		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage
				.goToPartsAndPricingTab();		
		browseSaasPart(partsAndPricingTab,SAAS_PART_PAUN_CLICK_NODES,SAAS_PART_PAUN_COMPONENTS);
				
		// Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SQOSalesInfoTabPage salesTab = partsAndPricingTab.gotoSalesInfoTab();
		loggerContxt.info("Go to SalesInf Tab end.....");

		salesTab.enterSalesInf(getProperty( ".briefTitle"),
				getProperty( ".quoteDesc"),
				getProperty( ".busOrgCode"));

		// go to Special Bid Tab
		loggerContxt.info("go to Special Bid Tab.....");
		SQOSpecialBidTabPage spcialBidTab = salesTab.goToSpecialBidTabClick();

		spcialBidTab.enterSpcialBidInf(getProperty("justificationSummary"));
		currentQuotePage = spcialBidTab.rtnToCPTab();
		currentQuotePage.selectExpirationDate(30);

		loggerContxt.info("submit current quote page.....");
		SQOSubmitCurrDraftQuotePage currDraftQuotePage = currentQuotePage
				.submitCurrentDraftQuote();

		loggerContxt.info("select approver.....");
		currDraftQuotePage.submitDraftQuote();

	}
	
	private void browseSaasPart(SQOPartsAndPricingTabPage partsAndPricingTab,String[] treeNodeIds,String[][] components ){
    	partsAndPricingTab.browseSoftwareAsAServiceLinkClick();
		String text = getProperty("ECustomerCare");
		if (isTextPresent(text)) {
			loggerContxt.info(text);
		}
        SQOBrowseSoftwareAsServiceTabPage browseSoftwareAsServiceTabPage = new SQOBrowseSoftwareAsServiceTabPage(getDriver());
        browseSoftwareAsServiceTabPage.waitForElementLoading(new Long(25000));
        
		browseSoftwareAsServiceTabPage.clickTreeNodesById(treeNodeIds);
		browseSoftwareAsServiceTabPage.configureService(SAAS_PART_PAUN_CONFIGURE_ID, components);	
		
		//browseSoftwareAsServiceTabPage.returnToDraftQuoteLinkClick();		
	}

	public static void main(String[] args) throws Exception {
		SQOIncreasedQuantityOnExistingSubscriptionPartsTest test = new SQOIncreasedQuantityOnExistingSubscriptionPartsTest();
		test.setUp();
		test.runCreateQuote();
		test.runSubmitQuote();
		test.teardown();
		//TODO: 1. Approve the quote 2. Change parts quantity and submit
		
	}

}
