/**
 * 
 */
package com.ibm.dsw.automation.testcase;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
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
public class SQOQuoteSubmitTest extends BaseTestCase {
//	private static Properties prop;
	public static WebdriverLogger loggerContxt = WebdriverLogger.getLogger(SQOQuoteSubmitTest.class.getName());
	
	
	private SQOMyCurrentQuotePage currentQuotePage = null;
	SQOHomePage sqoHomePage = null;

	/*@Override
	public void initTestData() {
		clazzName = "SQOPPXPRSTest";
		if (null == prop) {
			prop = this.getTestDataProp("/com/ibm/dsw/automation/testcase/sqo/fvt/SQOPPXPRSTest.properties");
		}
	}*/

	private void loginSqo() {

		loggerContxt.info("env....." + env);
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();

		 sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		 driver.manage().window().maximize();
		loggerContxt.info("Login SQO finished.....");

		loggerContxt.info("verify current page whether having this content ......" + this.getProperty(propSub, ".softQtOrd"));
		assertPresentText(this.getProperty(propSub, ".softQtOrd"));

	}

	// @Test(description = "Create a SQO quote")
	public void runCreateQuote() {

		loginSqo();

		boolean hasCurrentQuote = selenium.isTextPresent("SQO reference");
		loggerContxt.info("having current quote....." + hasCurrentQuote);

		SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();

		// Create a SQO quote
		currentQuotePage = cq.createQuote(this.getProperty(propSub, ".lob"), this.getProperty(propSub, ".country"), hasCurrentQuote);
		loggerContxt.info("create quote page finished.....");
		loggerContxt.info("verify current page whether having this content ......" + this.getProperty(propSub, ".currentQuoteTitle"));
		assertPresentText(this.getProperty(propSub, ".currentQuoteTitle"));

	}

	// @Test(description = "quote edit")
	public void runQuoteEdit() {

		// Find an existing customer
		SQOOSearchCustPage searchCustPage = currentQuotePage.findCustbyClick();
		loggerContxt.info("go to search customer page.....");

		// Assert.assertEquals("", "");
		// assert "Beust".equals("") : "Expected name Beust, for" + "";

		// display customer
		SQODisplayCustListPage displayCustListPage = searchCustPage.displayCustomerListByName();
		loggerContxt.info("display customer page.....");

		// select customer and back current quote page
		currentQuotePage = displayCustListPage.selectCustomer();
		loggerContxt.info("select customer and back current quote page.....");

		// Enter "xprs//@Test.com" in the "* Email: " field under Quote contact
		// section.
		currentQuotePage.fillEmailAdr(this.getProperty(propSub, ".custmail"));

		// Find reseller
		SQOSelectAResellerPage searchResellerPage = currentQuotePage.findResellertbyClick();
		loggerContxt.info("go to search reseller page.....");

		// display reseller
		SQODisplayResellerListPage displayResellerListPage = searchResellerPage.displayResellerListByName();
		loggerContxt.info("display reseller page.....");

		// select reseller and back current quote page
		currentQuotePage = displayResellerListPage.selectReseller();
		loggerContxt.info("select reseller and back current quote page.....");

		// Find distributor
		SQOSelectADistributorPage searchDistributorPage = currentQuotePage.findDistributorbyClick();
		loggerContxt.info("go to search distributor page.....");

		// display distributor
		SQODisplayDistributorListPage displayDistributorListPage = searchDistributorPage.displayCustomerListByName();
		loggerContxt.info("display distributor page.....");

		// select distributor and back current quote page
		currentQuotePage = displayDistributorListPage.selectDistributor();
		loggerContxt.info("select distributor and back current quote page.....");

		currentQuotePage.selectDirectChnl();

		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();

		// Click on 'Browse parts' tab
		SQOBrowsePartsTabPage browsePartsTab = partsAndPricingTab.browsePartsLinkClick();
		loggerContxt.info("Click on 'Browse parts' tab.....");
		loggerContxt.info("verify current page whether having this content ......" + this.getProperty(propSub, ".partSelect"));
		assertPresentText(this.getProperty(propSub, ".partSelect"));

		/*
		 * add an appliance part first
		 */
		browsePartsTab.browseLotusPartsTab();
		browsePartsTab.addSelectedPartsToDraftQuoteLinkClick();
		loggerContxt.info("verify current page whether having this content ......" + this.getProperty(propSub, ".addpartsokmsg"));
		assertPresentText(this.getProperty(propSub, ".addpartsokmsg"));
		partsAndPricingTab = browsePartsTab.returnToDraftQuoteLinkLinkClick();
		loggerContxt.info("Click on Brand Lotus Click on Lotus Massaging Click on License + SW Subscription & Support "
				+ "Check the checkbox of the first part listed Click the 'Add selected parts to draft quote' link.....");
		// remove appliance part
		partsAndPricingTab.deleteLotusParts();

		// browse saas parts
		BrowseSoftwareAsServiceTabPage browseSAASPartsTab = partsAndPricingTab.SQOBrowseSAASPartsLinkClick();
		loggerContxt.info("Click on 'Browse SAAS' tab.....");
		loggerContxt.info("verify current page whether having this content ......" + this.getProperty(propSub, ".partSelect"));
		assertPresentText(this.getProperty(propSub, ".partSelect"));

		// add saas part
		browseSAASPartsTab.browseLotusLiveServicePart();

		// apply offer
		partsAndPricingTab.applyoffer("50");
		partsAndPricingTab.applyDiscount("20");
		partsAndPricingTab.modifyOverridePrice("10");

		// Under Parts and pricing section, click on the "Recalculate quote"
		// link
		partsAndPricingTab.recalculateQuotePPTab();

		// Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SQOSalesInfoTabPage salesTab = partsAndPricingTab.gotoSalesInfoTab();
		loggerContxt.info("Go to SalesInf Tab end.....");
		loggerContxt.info("verify current page whether having this content ......" + this.getProperty(propSub, ".opportunityinfo"));
		assertPresentText(this.getProperty(propSub, ".opportunityinfo"));

		salesTab.enterSalesInf(this.getProperty(propSub, ".briefTitle"), this.getProperty(propSub, ".quoteDesc"), this.getProperty(propSub, ".busOrgCode"));

		salesTab.addQuoteEditor(this.getProperty(propSub, ".quoteeditormail"));
		// press "Save" button, then press "Save" button again
		salesTab.saveDraftQuoteLink();

		SQORetrieveSavedSalesQuotePage savedSalesQuote = currentQuotePage.goSQORetrieveSavedSalesQuoteTab();
		loggerContxt.info("verify current page whether having this content ......" + this.getProperty(propSub, ".retrieveSavedSalesQuoteTitle"));
		assertPresentText(this.getProperty(propSub, ".retrieveSavedSalesQuoteTitle"));
		currentQuotePage = savedSalesQuote.goViewDetailSavedQuote();

		// Select the last date of the current month as the
		// "Quote expiration date"
		currentQuotePage.selectExpirationDate(30);

		// go to Special Bid Tab
		loggerContxt.info("go to Special Bid Tab.....");
		SQOSpecialBidTabPage spcialBidTab = salesTab.goToSpecialBidTabClick();

		spcialBidTab.enterSpcialBidInf(this.getProperty(propSub, "justificationSummary"));

		currentQuotePage = spcialBidTab.rtnToCPTab();
		// download
		currentQuotePage.clickDownloadQTAsRich();

	}

	// @Test(description = "quote submit ")
	public void runSubmitQuote() {

		loggerContxt.info("submit current quote page.....");
		SQOSubmitCurrDraftQuotePage currDraftQuotePage = currentQuotePage.submitCurrentDraftQuote();

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
		currDraftQuotePage.submitDraftQuote(this.getProperty(propSub, ".quoteeditormail"), this.getProperty(propSub, ".quoteDesc"));

		loggerContxt.info("verify current page whether having this content ......" + this.getProperty(propSub, ".quotefinalmsg"));
		assertPresentText(this.getProperty(propSub, ".quotefinalmsg"));

	}

	// @Test(description = "waiting for 15 min and login agin")
	public void runwaiting() {
		loggerContxt.info("quite webdirver.....");
		quitWebdriver();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		loggerContxt.info("waiting started....." + sdf.format(new Date(System.currentTimeMillis())));

		currentQuotePage.waitForElementLoading(new Long(900001));
		loggerContxt.info("waiting end....." + sdf.format(new Date(System.currentTimeMillis())));
		try {
			setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loginSqo();
	}

	// @Test(description = "quote order ")
	public void runOrder() {

		loggerContxt.info("submit current quote page.....");
		loggerContxt.info("go to sqo home page finished.....");
		SQOStatusSearchTabPage statusSearchPO = sqoHomePage.gotoStatus();

		loggerContxt.info("find quotes using the quote number:....." + this.getProperty(propSub, ".quoteNum"));
		SQODisplayStatusSearchReslutPage resultPO = statusSearchPO.goDispAllQuoteReslt();
		loggerContxt.info("view the detail CP.....");
		SQOSelectedQuoteCPTabPage viewDetailCPPO = resultPO.goDispQuoteReslt();

		loggerContxt.info("Press the 'Order' link at the bottom of the page.....");
		CheckOutBillAndShipPage checkoutPO = viewDetailCPPO.goToCheckout();
		loggerContxt.info("verify current page whether having this content ......" + this.getProperty(propSub, ".reviewsubmitorder"));
		assertPresentText(this.getProperty(propSub, ".reviewsubmitorder"));

		CheckoutInf dto = new CheckoutInf();
		dto.setUserID(this.getProperty(propSub, ".loginid"));
		dto.setUserEmail(this.getProperty(propSub, ".emailaddress"));
		dto.setSubmit_first_name(this.getProperty(propSub, ".submittedfirstname"));
		dto.setSubmit_last_name(this.getProperty(propSub, ".submittedlastname"));
		dto.setSubmit_email(this.getProperty(propSub, ".submittedmail"));
		dto.setSubmit_phone(this.getProperty(propSub, ".submittedphone"));
		loggerContxt.info("View Checkout - Shipping and Billing page Enter necessary billing and shipping information. .....");
		ReviewSubmitOrderPage reviewSubmitOrder = checkoutPO.submitOrder(dto);
		loggerContxt.info("verify current page whether having this content ......" + this.getProperty(propSub, ".reviewsubmitorder"));
		assertPresentText(this.getProperty(propSub, ".reviewsubmitorder"));

		reviewSubmitOrder.gotoConfirmation(this.getProperty(propSub, ".invoiceno"));

		loggerContxt.info("verify current page whether having this content ......" + this.getProperty(propSub, ".orderconfirm"));
		assertPresentText(this.getProperty(propSub, ".orderconfirm"));

	}

	@Test(description = "quote order ")
	public void runCase() {
		runCreateQuote();
		runQuoteEdit();
		runSubmitQuote();
		runwaiting();
		runOrder();
	}

	public static void main(String[] args) throws Exception {
		SQOQuoteSubmitTest test = new SQOQuoteSubmitTest();
		test.setUp();
		test.runCreateQuote();
		test.runQuoteEdit();
		test.runSubmitQuote();
		test.runwaiting();
		// test.runOrder();
	}

}
