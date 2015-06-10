package com.ibm.dsw.automation.testcase.base;

import java.util.Properties;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.pageobject.sqo.SQOCreateQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayCustListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayDistributorListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayResellerListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsSelectTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOJumpPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOLoginPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOOSearchCustPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectADistributorPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectAResellerPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSpecialBidTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitCurrDraftQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOValueUnitCalculotorGuideTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOValueUnitCalculotorPage;

public class SQOWI38060Test extends BaseTestCase {

	private static Properties prop;
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOWI38060Test.class.getName());
	private SQOMyCurrentQuotePage currentQuotePage = null;
	
	@Override
	public void initTestData() {
		clazzName = "SQO_WI_38060_Test";
		if (null == prop) {
			prop = this
					.getTestDataProp("/com/ibm/dsw/automation/testcase/sqo/fvt/SQOWI38060Test.properties");
		}
	}

	@Test(description = "Create a SQO quote")
	public void run() {
	
		SQOHomePage sqoHomePage = null;
		loggerContxt.info("env....." + env.hashCode());
		loggerContxt.info("driver....." + driver.hashCode());
		if ("uat".equals(env)) {
			// Login SQO
			SQOLoginPage page = new SQOLoginPage(this.driver);

			// loggerContxt.info(message)
			loggerContxt.info("log in test finished.....");
			sqoHomePage = page.loginAs(getLoginUser(), getLoginPasswd());

			// sqoHomePage = ovpage.gotoSQO();
		}
		if ("fvt".equals(env)) {

			SQOJumpPage jumpPage = new SQOJumpPage(this.driver);
			jumpPage.loginIn("US", getLoginUser(), "1");

			sqoHomePage = new SQOHomePage(driver);
		}

		loggerContxt
				.info("verefy current page whether having this content ......"
						+ getProperty(prop, ".softQtOrd"));
		assertPresentText(getProperty(prop, ".softQtOrd"));

		loggerContxt.info("go to sqo home page finished.....");
		SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();

		// Create a SQO quote
		currentQuotePage = cq.createQuote();
		loggerContxt.info("create quote page finished.....");
		loggerContxt
				.info("verefy current page whether having this content ......"
						+ getProperty(prop, ".currentQuoteTitle"));
		assertPresentText(getProperty(prop, ".currentQuoteTitle"));

	}

	@Test(description = "quote edit")
	public void runQuoteEdit() {

		// Find an existing customer
		SQOOSearchCustPage searchCustPage = currentQuotePage.findCustbyClick();
		loggerContxt.info("go to search customer page.....");

		
		// display customer
		SQODisplayCustListPage displayCustListPage = searchCustPage
				.displaySQOCustomerListBySiteNum(getProperty(prop, ".customerNum"));
		loggerContxt.info("display customer page.....");

		// select customer and back current quote page
		currentQuotePage = displayCustListPage.selectCustomer();
		loggerContxt.info("select customer and back current quote page.....");


		// Find reseller
		SQOSelectAResellerPage searchResellerPage = currentQuotePage
				.findResellertbyClick();
		loggerContxt.info("go to search reseller page.....");

		// display reseller
		SQODisplayResellerListPage displayResellerListPage = searchResellerPage
				.displayResellerByNum(getProperty(prop, ".resellerNum"));
		loggerContxt.info("display reseller page.....");

		// select reseller and back current quote page
		currentQuotePage = displayResellerListPage.selectReseller();
		loggerContxt.info("select reseller and back current quote page.....");

		// Find distributor
		SQOSelectADistributorPage searchDistributorPage = currentQuotePage
				.findDistributorbyClick();
		loggerContxt.info("go to search distributor page.....");

		// display distributor
		SQODisplayDistributorListPage displayDistributorListPage = searchDistributorPage
				.displayDistributorListBySiteNum(getProperty(prop, ".distributorNum"));
		loggerContxt.info("display distributor page.....");

		// select distributor and back current quote page
		currentQuotePage = displayDistributorListPage.selectDistributor();
		loggerContxt
				.info("select distributor and back current quote page.....");

		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage
				.goToPartsAndPricingTab();
		SQOFindPartsTabPage findPartsTabPage = partsAndPricingTab
				.findPartsLinkClick();
		
		loggerContxt
		.info("add below part num to PP....."+getProperty(prop, ".partList"));
		SQOFindPartsSelectTabPage findPartsSelect = findPartsTabPage
				.findPartsLinkClick(getProperty(prop, ".partList"));
		findPartsSelect.selectPartsClick(getProperty(prop, ".partList"));
		
		
		partsAndPricingTab = findPartsSelect.rtn2DraftQuote();
		
		loggerContxt
		.info("set quantity  for part a....."+getProperty(prop, ".partQty"));
		SQOValueUnitCalculotorPage valueUnitCalPO1=partsAndPricingTab.goDispQuoteReslt4D56LALLL();
		SQOValueUnitCalculotorGuideTabPage valueUnitCalGuide=valueUnitCalPO1.enterPartQty(getProperty(prop, ".partQty"));
		partsAndPricingTab=valueUnitCalGuide.applyAndReturn();
		
		loggerContxt
		.info("set quantity  for part b....."+getProperty(prop, ".partQty"));
		SQOValueUnitCalculotorPage valueUnitCalPO2=partsAndPricingTab.goDispQuoteReslt4D612WLL();
		valueUnitCalGuide=valueUnitCalPO2.enterPartQty(getProperty(prop, ".partQty"));
		partsAndPricingTab=valueUnitCalGuide.applyAndReturn();

		// Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SQOSalesInfoTabPage salesTab = partsAndPricingTab.gotoSalesInfoTab();
		loggerContxt.info("Go to SalesInf Tab end.....");

		salesTab.enterSalesInf(getProperty(prop, ".briefTitle"),
				getProperty(prop, ".quoteDesc"),
				getProperty(prop, ".busOrgCode"));

		// go to Special Bid Tab
		loggerContxt.info("go to Special Bid Tab.....");
		SQOSpecialBidTabPage spcialBidTab = salesTab.goToSpecialBidTabClick();

		spcialBidTab.enterSpcialBidInf(getProperty(prop,"justificationSummary"));
		currentQuotePage = spcialBidTab.rtnToCPTab();
		currentQuotePage.selectExpirationDate(30);

		/*
		 * }catch(Exception ex){ ex.printStackTrace(); }finally{ //
		 * quitWebdriver(); }
		 */

	}

	
	// @Test(description = "quote submit ")
	public void runSubmitQuote() {

		loggerContxt.info("submit current quote page.....");
		SQOSubmitCurrDraftQuotePage currDraftQuotePage = currentQuotePage
				.submitCurrentDraftQuote();

		loggerContxt.info("select approver.....");
		currDraftQuotePage.submitDraftQuote();

		loggerContxt
				.info("verefy current page whether having this content ......"
						+ getProperty(prop, ".specialbidSubmittedMsg"));
		assertPresentText(getProperty(prop, ".specialbidSubmittedMsg"));

	}
	
	
	public static void main(String[] args) throws Exception {
		SQOWI38060Test test = new SQOWI38060Test();
		test.setUp();
		test.run();
		test.runQuoteEdit();
		test.runSubmitQuote();
	}
	

}
