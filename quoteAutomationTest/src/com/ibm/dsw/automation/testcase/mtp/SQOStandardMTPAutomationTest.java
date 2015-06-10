/**
 * 
 */
package com.ibm.dsw.automation.testcase.mtp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.TestUtil;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.pgs.BrowseSoftwareAsServiceTabPage;
import com.ibm.dsw.automation.pageobject.sqo.CheckOutBillAndShipPage;
import com.ibm.dsw.automation.pageobject.sqo.ReviewSubmitOrderPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOBrowsePartsTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOCreateQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayCustListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayDistributorListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayResellerListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayStatusSearchReslutPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOOSearchCustPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQORestServicePage;
import com.ibm.dsw.automation.pageobject.sqo.SQORetrieveSavedSalesQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectADistributorPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectAResellerPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuoteCPTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSpecialBidTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOStatusSearchTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitCurrDraftQuotePage;
import com.ibm.dsw.automation.vo.CheckoutInf;

/**
 * @author will
 * 
 */
public class SQOStandardMTPAutomationTest extends BaseTestCase {
	// private static Properties prop;
	// public static WebdriverLogger loggerContxt =
	// WebdriverLogger.getLogger(SQOStandardMTPAutomationTest.class.getName());

	private static Logger loggerContxt = Logger
			.getLogger(SQOStandardMTPAutomationTest.class);

	private SQOMyCurrentQuotePage currentQuotePage = null;
	SQOHomePage sqoHomePage = null;

	public SQOStandardMTPAutomationTest() {
		super.setLoggerContxt(loggerContxt);
		TestUtil.loggerContxt = loggerContxt;
	}

	private void loginSqo() throws Exception {

		loggerContxt.info("The current testing environment is :: " + env);
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		quoteFlow.setLoggerContxt(loggerContxt);
		try {
			sqoHomePage = quoteFlow.loginSqo(getLogonInf());
			sqoHomePage.setLoggerContxt(loggerContxt);
			loggerContxt.info(driver.getTitle());
		} catch (Exception e) {
			loggerContxt
					.fatal("Failed to login SQO. please re-check you password, or some issue with the SQO application.",
							e);
			throw new Exception(e);
		}
		if (!(driver instanceof HtmlUnitDriver)) {
			driver.manage().window().maximize();
		}
		loggerContxt.info("Login SQO finished.....");

		loggerContxt
				.info("verify current page whether having this content ......"
						+ this.getProperty(propSub, ".softQtOrd"));
//		assertPresentText(this.getProperty(propSub, ".softQtOrd"));

	}

	// @Test(description = "Create a SQO quote")
	public void runCreateQuote() throws Exception {
		try {
			loginSqo();

			boolean hasCurrentQuote = selenium.isTextPresent("SQO reference");
			loggerContxt.info("having current quote....." + hasCurrentQuote);

			SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();
			cq.setLoggerContxt(loggerContxt);

			// Create a SQO quote
			currentQuotePage = cq.createQuote(
					this.getProperty(propSub, ".lob"),
					this.getProperty(propSub, ".country"), hasCurrentQuote);
			currentQuotePage.setLoggerContxt(loggerContxt);
			loggerContxt.info(String.format("My current quote page displayed."));
			loggerContxt
					.info("verify current page whether having this content ......"
							+ this.getProperty(propSub, ".currentQuoteTitle"));
			assertPresentText(this.getProperty(propSub, ".currentQuoteTitle"));
		} catch (Exception e) {
			throw new Exception(e);
		}

	}

	// @Test(description = "quote edit")
	public void runQuoteEdit() {

		// Find an existing customer
		SQOOSearchCustPage searchCustPage = currentQuotePage.findCustbyClick();
		searchCustPage.setLoggerContxt(loggerContxt);
		loggerContxt.info("Navigate to 'Customer selection and creation' page.");

		// Assert.assertEquals("", "");
		// assert "Beust".equals("") : "Expected name Beust, for" + "";

		// display customer
		SQODisplayCustListPage displayCustListPage = searchCustPage
				.displayCustomerListByName();
		displayCustListPage.setLoggerContxt(loggerContxt);
		loggerContxt.info("display customer page.....");

		// select customer and back current quote page
		currentQuotePage = displayCustListPage.selectCustomer();
		currentQuotePage.setLoggerContxt(loggerContxt);
		loggerContxt.info("select customer and back to current quote page.....");

		// Enter "xprs//@Test.com" in the "* Email: " field under Quote contact section.
		currentQuotePage.fillEmailAdr(this.getProperty(propSub, ".custmail"));

		// Find reseller
		SQOSelectAResellerPage searchResellerPage = currentQuotePage
				.findResellertbyClick();
		searchResellerPage.setLoggerContxt(loggerContxt);

		// display reseller
		SQODisplayResellerListPage displayResellerListPage = searchResellerPage
				.displayResellerListByName();
		displayResellerListPage.setLoggerContxt(loggerContxt);
		loggerContxt.info("The result page 'Reseller selection result' loaded successfully.");

		// select reseller and back current quote page
		currentQuotePage = displayResellerListPage.selectReseller();
		loggerContxt.info("select reseller and back current quote page.....");

		// Find distributor
		SQOSelectADistributorPage searchDistributorPage = currentQuotePage
				.findDistributorbyClick();
		searchDistributorPage.setLoggerContxt(loggerContxt);
		loggerContxt.info("Navigate to search distributor page.");

		// display distributor
		SQODisplayDistributorListPage displayDistributorListPage = searchDistributorPage
				.displayCustomerListByName();
		displayDistributorListPage.setLoggerContxt(loggerContxt);
		loggerContxt.info("'Distributor selection result' page loaded successfully.");

		// select distributor and back current quote page
		currentQuotePage = displayDistributorListPage.selectDistributor();
		loggerContxt
				.info("select distributor and back to current quote page.");

		currentQuotePage.selectDirectChnl();

		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage
				.goToPartsAndPricingTab();
		partsAndPricingTab.setLoggerContxt(loggerContxt);
		// Click on 'Browse parts' tab
		SQOBrowsePartsTabPage browsePartsTab = partsAndPricingTab
				.browsePartsLinkClick();
		browsePartsTab.setLoggerContxt(loggerContxt);
		loggerContxt
				.info("verify current page whether having this content ......"
						+ this.getProperty(propSub, ".partSelect"));
		assertPresentText(this.getProperty(propSub, ".partSelect"));

		/*
		 * add an appliance part first
		 */
		String partNogeArray = this.getProperty(propSub,
				"browser.part.node.array");
		browsePartsTab.browseLotusPartsTab(partNogeArray);
		browsePartsTab.addSelectedPartsToDraftQuoteLinkClick();
		loggerContxt
				.info("verify current page whether having this content ......"
						+ this.getProperty(propSub, ".addpartsokmsg"));
		assertPresentText(this.getProperty(propSub, ".addpartsokmsg"));
		partsAndPricingTab = browsePartsTab.returnToDraftQuoteLinkLinkClick();
		partsAndPricingTab.setLoggerContxt(loggerContxt);
		loggerContxt
				.info("Click on Brand Lotus Click on Lotus Massaging Click on License + SW Subscription & Support "
						+ "Check the checkbox of the first part listed Click the 'Add selected parts to draft quote' link.....");
		// remove appliance part
		partsAndPricingTab.deleteLotusParts();

		// browse saas parts
		BrowseSoftwareAsServiceTabPage browseSAASPartsTab = partsAndPricingTab
				.SQOBrowseSAASPartsLinkClick();
		browseSAASPartsTab.setLoggerContxt(loggerContxt);
		loggerContxt.info("Click on 'Browse SAAS' tab.....");
		loggerContxt
				.info("verify current page whether having this content ......"
						+ this.getProperty(propSub, ".partSelect"));
		assertPresentText(this.getProperty(propSub, ".partSelect"));

		// add saas part
		int serivce_index = Integer.parseInt(this.getProperty(propSub,
				this.getEnv() + ".configurator.index"));
		/*String xpaths = this.getProperty(propSub, this.getEnv()
				+ ".configurator.xpaths");
		List<String> xpathList = Arrays.asList(xpaths.split(","));
		browseSAASPartsTab.browseLotusLiveServicePart(serivce_index, xpathList);*/
		browseSAASPartsTab.browseLotusLiveServicePart(serivce_index);
		// select Migration and Renewl value for Saas part
		partsAndPricingTab.clickMrgnRwlRadio();
		// apply offer
		partsAndPricingTab.applyoffer("50");
		partsAndPricingTab.applyDiscount("20");
		partsAndPricingTab.modifyOverridePrice("10");

		// Under Parts and pricing section, click on the "Recalculate quote"
		// link
		partsAndPricingTab.recalculateQuotePPTab();
		loggerContxt.info("Navigate to 'Parts and pricing' tab on 'My current quote' page.");

		// Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SQOSalesInfoTabPage salesTab = partsAndPricingTab.gotoSalesInfoTab();
		salesTab.setLoggerContxt(loggerContxt);
		loggerContxt.info("Go to SalesInf Tab end.....");
		loggerContxt
				.info("verify current page whether having this content ......"
						+ this.getProperty(propSub, ".opportunityinfo"));
		assertPresentText(this.getProperty(propSub, ".opportunityinfo"));

		salesTab.enterSalesInf(this.getProperty(propSub, ".briefTitle"),
				this.getProperty(propSub, ".quoteDesc"),
				this.getProperty(propSub, ".busOrgCode"));

		salesTab.addQuoteEditor(this.getProperty(propSub, ".quoteeditormail"));
		// press "Save" button, then press "Save" button again
		salesTab.saveDraftQuoteLink();

		SQORetrieveSavedSalesQuotePage savedSalesQuote = currentQuotePage
				.goSQORetrieveSavedSalesQuoteTab();
		savedSalesQuote.setLoggerContxt(loggerContxt);
		loggerContxt
				.info("verify current page whether having this content ......"
						+ this.getProperty(propSub,
								".retrieveSavedSalesQuoteTitle"));
		assertPresentText(this.getProperty(propSub,
				".retrieveSavedSalesQuoteTitle"));
		currentQuotePage = savedSalesQuote.goViewDetailSavedQuote();
		currentQuotePage.setLoggerContxt(loggerContxt);
		// Select the last date of the current month as the
		// "Quote expiration date"
		currentQuotePage.selectExpirationDate(30);

		// go to Special Bid Tab
		loggerContxt.info("Navigate to 'Special Bid' Tab.");
		SQOSpecialBidTabPage spcialBidTab = salesTab.goToSpecialBidTabClick();
		spcialBidTab.setLoggerContxt(loggerContxt);
		spcialBidTab.enterSpcialBidInf(this.getProperty(propSub,
				".justificationSummary"));

		currentQuotePage = spcialBidTab.rtnToCPTab();
		// download
//		currentQuotePage.clickDownloadQTAsRich();
		currentQuotePage.clickDownloadQTAsRich(getProperty(this.getEnv()+".richLinkText"));
		currentQuotePage.clickDownloadQTAsRich(getProperty(this.getEnv()+".ExcelLinkText"));
		currentQuotePage.clickDownloadQTAsRich(getProperty(this.getEnv()+".spreadsheetLinkText"));

	}

	// @Test(description = "quote submit ")
	public void runSubmitQuote() {

		loggerContxt.info("submit current quote page.....");
		SQOSubmitCurrDraftQuotePage currDraftQuotePage = currentQuotePage
				.submitCurrentDraftQuote();
		currDraftQuotePage.setLoggerContxt(loggerContxt);
		/*
		 * 
		 * Check the "E-mail this quote to the following e-mail address"
		 * checkbox, enter "chris_errichetti@us.ibm.com" in the field next to
		 * the checkbox item, turn on the No radio button for the question
		 * "Would you like to make this available to the customer on Passport Advantage Online?"
		 * , enter "XPRS Testing" in the
		 * "Enter customized text for the quote's cover email" text box and On
		 * the quote submission page, make a select from
		 * "* Would you like to make this available to the customer on Passport Advantage Online? "
		 * if applicable. Press the "Submit" button
		 */
		loggerContxt.info("submit the quote.....");
		// currDraftQuotePage.submitDraftQuote(this.getProperty(propSub,
		// ".quoteeditormail"), this.getProperty(propSub, ".quoteDesc"));
		if ("prod".equalsIgnoreCase(this.env)) {
			return;
		}
		currDraftQuotePage.submitDraftQuoteForSpecialBidWithoutEmail(
				getProperty("approver.level0"), getProperty("approver.level3"));

		loggerContxt
				.info("verify current page whether having this content ......"
						+ this.getProperty(propSub, ".quotefinalmsg"));
		assertPresentText(this.getProperty(propSub, ".quotefinalmsg"));

	}

	// @Test(description = "waiting for 15 min and login agin")
	public void runwaiting() {
		loggerContxt.info("quite webdirver.....");
		quitWebdriver();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		loggerContxt.info("waiting started....."
				+ sdf.format(new Date(System.currentTimeMillis())));

		try {
			Thread.sleep(5 * 60 * 1000L);
			loggerContxt.info("waiting end....."
					+ sdf.format(new Date(System.currentTimeMillis())));
			setUp();
			loginSqo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Test(description = "quote order ")
	public void runOrder() {

		loggerContxt.info("submit current quote page.....");
		loggerContxt.info("go to sqo home page finished.....");
		SQOStatusSearchTabPage statusSearchPO = sqoHomePage.gotoStatus();
		statusSearchPO.setLoggerContxt(loggerContxt);

		loggerContxt.info("find quotes using the quote number:....."
				+ this.getProperty(propSub, ".quoteNum"));
		SQODisplayStatusSearchReslutPage resultPO = statusSearchPO
				.goDispAllQuoteReslt();
		resultPO.setLoggerContxt(loggerContxt);
		loggerContxt.info("view the detail CP.....");
		SQOSelectedQuoteCPTabPage viewDetailCPPO = resultPO.goDispQuoteReslt();
		viewDetailCPPO.setLoggerContxt(loggerContxt);

		loggerContxt
				.info("Press the 'Order' link at the bottom of the page.....");
		CheckOutBillAndShipPage checkoutPO = viewDetailCPPO.goToCheckout();
		checkoutPO.setLoggerContxt(loggerContxt);
		loggerContxt
				.info("verify current page whether having this content ......"
						+ this.getProperty(propSub, ".reviewsubmitorder"));
		assertPresentText(this.getProperty(propSub, ".reviewsubmitorder"));

		CheckoutInf dto = new CheckoutInf();
		dto.setUserID(this.getProperty(propSub, ".loginid"));
		dto.setUserEmail(this.getProperty(propSub, ".emailaddress"));
		dto.setSubmit_first_name(this.getProperty(propSub,
				".submittedfirstname"));
		dto.setSubmit_last_name(this.getProperty(propSub, ".submittedlastname"));
		dto.setSubmit_email(this.getProperty(propSub, ".submittedmail"));
		dto.setSubmit_phone(this.getProperty(propSub, ".submittedphone"));
		loggerContxt
				.info("View Checkout - Shipping and Billing page Enter necessary billing and shipping information. .....");
		ReviewSubmitOrderPage reviewSubmitOrder = checkoutPO.submitOrder(dto);
		reviewSubmitOrder.setLoggerContxt(loggerContxt);
		loggerContxt
				.info("verify current page whether having this content ......"
						+ this.getProperty(propSub, ".reviewsubmitorder"));
		assertPresentText(this.getProperty(propSub, ".reviewsubmitorder"));

		reviewSubmitOrder.gotoConfirmation(this.getProperty(propSub,
				".invoiceno"));

		loggerContxt
				.info("verify current page whether having this content ......"
						+ this.getProperty(propSub, ".orderconfirm"));
		assertPresentText(this.getProperty(propSub, ".orderconfirm"));

	}

	@Test(description = "quote order ")
	public void runCase() throws Exception {
		runCreateQuote();
		runQuoteEdit();
		runSubmitQuote();
		runwaiting();
		runOrder();
	}

	// @Test(description="quoteRest userLookup is available ")
	protected void checkUserLookupRestPage() {
		SQORestServicePage sqouserlookuppage = new SQORestServicePage(this.driver);
		String retStr = sqouserlookuppage.loginUserLookupRestPage(prop, env);
		if (!retStr.equals(""))
			try {
				throw new Exception("rest Service Exception :" + retStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	// @Test(description="quoteRest userLookup is available ")
	protected void checkAddonTradeupRestPage() {
		SQORestServicePage sqoaddontrapuppage = new SQORestServicePage(this.driver);
		String retStr = sqoaddontrapuppage.loginAddonTrapupRestPage(prop, env);
		if (!retStr.equals(""))
			try {
				throw new Exception("rest Service Exception :" + retStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	public static void main(String[] args) throws Exception {
		SQOStandardMTPAutomationTest test = new SQOStandardMTPAutomationTest();
		try {
			test.setScriptName(String.format(
					"SQO Standard %s Automation Test Script", test.getEnv()));
			String configStoredPath = System.getProperty("user.dir") + File.separator + "res";
			test.setUp();
			test.runCreateQuote();
			test.runQuoteEdit();
			 if (!"prod".equalsIgnoreCase(test.getEnv())) {
			 test.runSubmitQuote();

			try {
				test.runOrder();
			} catch (NoSuchElementException e) {
				loggerContxt.error("Have no access to Order the quote",e);
			}
			 }
			if ("uat".equalsIgnoreCase(test.getEnv())) {
				test.checkUserLookupRestPage();
				test.checkAddonTradeupRestPage();
			}
			test.quitWebdriver();
			loggerContxt.info(test.getScriptName() + " ended successfully.");
			test.sendAlertMail("build/mailBody.vm", "succeed",null);
		} catch (Exception e) {
			loggerContxt.error(
					String.format("%s Failed..", test.getScriptName()), e);
//			File attach = test.captureScreenshot(test.getClass().getSimpleName());
//			test.sendAlertMail("build/mailBody.vm","failed",attach);
//			test.teardown();
			e.printStackTrace();
		}
		// test.runOrder();
	}

}
