package com.ibm.dsw.automation.testcase.base;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.CheckOutBillAndShipPage;
import com.ibm.dsw.automation.pageobject.sqo.ReviewSubmitOrderPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOApproveQueuePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOBrowsePartsTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOBrowseSoftwareAsServiceTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOCreateQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayCustListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayDistributorListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayResellerListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayStatusSearchReslutPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsSelectTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOJumpPage;
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
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitSQSpecialBidTabPage;
import com.ibm.dsw.automation.vo.CheckoutInf;
/**
 * https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/410740
 * Script Name : SQOPPXPRSTest<br>
 * Objective: Prerequisites:<br>
Enter_URL	Open jump page:

LoginPage	Enter an IIP ID and password to authenticate (MUST USE UNIQUE IIP FOR EACH LOGIN)
Page1	click on "Create a sales quote" link in the left nav
Page2	Select "Passport Advantage/Passport Advantage Express" for Quote Type, "United States" for Country/Region, check the "Make selections my default" checkbox, press "Continue" button
Page3	Click on the "Find an existing customer" link
Page4	Under Find customer by number section, enter 7022154 and press the "Submit" button
Page5	Under Selected customers section, press the "Select customer" link for the first one 
Page6	Under Reseller information section, press "Select a Tier 1 or Tier 2 Reseller" link
Page7	Under Find resellers by attributes section, enter "ecapital" in the Partial or complete reseller name field, ensure both Tier2 reseller and Tier 1 reseller/house account checkboxes are checked and press "Submit" button
Page8	Under Reseller results section, press the "Select reseller" link for the first one
Page9	Under Distributor information section, press "Select a distributor" link.
Page10	Under Find distributors by attributes section, enter "avnet" in the Distributor name field and press the "Submit" button
Page11	Under Distributor results section, press the "Select distributor" link for the first one
Page12	Click on "Parts and pricing" tab
Page13	Click on "Find parts(excludes Software as a Service)" link
Page14a	Under Select by part number section, enter the following part numbers and press the "Submit" button

	
Page14b	Wait till the page completes loading.  Then click the "Expand all".
	Scroll to the bottom pf the page, Wait till the parts tree has been painted, click the "Select all displayed parts" link, then click the "Add selected parts to draft quote" link
Page15a	Click "Change search criteria" link, under Select by part number section, enter the following part numbers and press the "Submit" button
	

	BB19YML
	BB19ZML
	
Page15b	Wait till the page completes loading.  Then click the "Expand all".
	Scroll to the bottom pf the page, Wait till the parts tree has been painted, click the "Select all displayed parts" link, then click the "Add selected parts to draft quote" link.
Page16	Click on 'Browse parts' tab
	Click on Brand Lotus
	Click on Lotus 123 
	Click on  License + SW Subscription & Support 
	Check the checkbox of the first part listed
	Click the "Add selected parts to draft quote" link
Page17	Under Important Links section, enter quantity for all parts and click on the "Return to draft quote" link
Page18	Under Parts and pricing section, click on the "Recalculate quote" link
Page19	Click on the "Sales information" tab
Page20	Under Quote description section, enter "XPRS Testing" in the "Enter a brief title" field and the "Full description" field, click the "Business partner opportunity number not provided (exemption code 30)" radio button, select "Sales - IBM.com" in the "Business organization" dropdown.
	Under Quote editors section, enter "chris_errichetti@us.ibm.com" in the New editor's email address field, press the Add editors link  and click on the "Special bid" tab
Page20a	Click on "Customers and Partners" tab; select Direct for reseller and then OK to confirm
Page20b	Click on the "Special Bid" tab
Page21	Press the "Export quote as spreadsheet to be used in Excel" link, press "Save" button, then press "Save" button again
Page22	Press the "Export quote as spreadsheet to be used in Symphony" link, press "Save" button, then press "Save" button again
Page22a	Click on Special Bid tab, enter "test upload attachment" in the justification text editor, then press the "Add attachment" button
Page22b	Press the first "Browse" button, select the test file detached from this document previously, press the Ok button in the file dialog, then press the "Submit" button in the attachment upload dialog to start the upload (do not close this dialog).
Page23	Press "Save quote as draft" link
Page24	Click on the "Create a sales quote" link in the left nav
Page25	Click on the "Retrieve a saved sales quote" sub-link in the left nav
Page26	Check the "Owned by me" and "Assigned to me as an editor" checkboxes, select "All" in the "Modified within the last" dropdown, check the "Make selections my default" checkbox and press the "Go" button
Page27	Click the "open" link of the first quote (Abbott Laboratories) in the result page
Page28	Click the "OK" button to dismiss the "There is currently a quote in the quote worksheet ......" pop-up dialog box if applicable, click the Parts and pricing tab, then the Sales information tab, then the Special bid tab, then the Customers and partners tab
Page29	Select the last date of the current month as the "Quote expiration date"
Page30	Under Quote partners group, turn on the "Direct" radio button, press the "OK" button to dismiss the "You have changed the quote's fullfilment source to direct ......" dialog box
Page31	Enter "xprs@test.com" in the "* Email: " field under Quote contact section.
	Click on the "Submit" link
Page32	Check the "E-mail this quote to the following e-mail address" checkbox, enter "chris_errichetti@us.ibm.com" in the field next to the checkbox item, turn on the No radio button for the question "Would you like to make this available to the customer on Passport Advantage Online?",  enter "XPRS Testing" in the "Enter customized text for the quote's cover email" text box and 
	On the quote submission page, make a select from "* Would you like to make this available to the customer on Passport Advantage Online? " if applicable.
	Press the "Submit" button
Page33	Wait for 15 minutes, and click on the "Status" link in the left nav
Page34	Check "Sales quotes" checkboxes in the "Find the following submitted quotes" group, the "Passport Advantage" and the "Passport Advantage Express" checkboxes in the "Find the following quote types" group, select all checkboxes in the "Find quotes and special bids with the following overall statuses", select "1 week" in the "Find quotes and special bids submitted within the past" group, select "Date submitted (descending)" in the "Sort by" dropdown, check the "Assigned to me" radio button, select "Creator/owner/submitter" checkbox in the "What is the designated person's role on the quote or special bid" group and press the "Find quotes" button.
Page35	Click on the "View details" link for customer Abbott Laboratories (should be the first quote)
Page36	Click on the "Status" tab
Page37	Press the "Order" link at the bottom of the page
Page38	View Checkout - Shipping and Billing page
	
	Enter necessary billing and shipping information. 
	
	Enter info for Billing address:  - Enter first name, last name, phone, etc. if missing
	
	Additional customer info: 
	Customer login id    :    ak@ibm.com
	Customer eMail address    as@ibm.com
	
	Fill out submitter information section 
	
	* First name    :  Amba
	* Last name    :  Sagar
	* Email    :     as@ibm.com
	 * Phone    : 123-1234
	
	Under  Select your Organization: choose 'Inside Sales Direct Order(Channel Z)' from drop down box.
	
	Click 'Continue' button.
	
Page39	View 'Review and submit order' page. 
	
	Enter Payment information 
	Purchase order (IBM invoice):11111111
	
	
	Click 'Submit' button  to submit the order.
	Order confirmation page should be displayed.
	
Page 40	Order confirmation number should be displayed on the page.

 * Additional Info: check in firefox<br>
 * Original Host: <br>
 * Windows
 * 
 * @since 2012-12-6
 * @author zhou jun
 */
@SuppressWarnings("deprecation")
public class SQOPPXPRSTest extends BaseTestCase {

	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOPPXPRSTest.class.getName());
	
	private SQOMyCurrentQuotePage currentQuotePage = null;
	private SQOHomePage sqoHomePage = null;
	private final SQOPartsAndPricingTabPage partsAndPricingTab =null;
	private String quoteNum="";


	private void loginSqo() {
		loggerContxt.info("env....." + env);
	/*	loggerContxt.info("driver....." + driver.hashCode());

		SQOLoginPage lp = new SQOLoginPage(this.driver);
		lp.loginAs(getLoginUser(), "");
		sqoHomePage = new SQOHomePage(driver);*/
		
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();

		sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");

		loggerContxt.info("verify current page whether having this content ......" + getProperty(".softQtOrd"));
		assertTextPresentTrue(getProperty(".softQtOrd"));

	}

	// @Test(description = "Create a SQO quote")
	public void runCreateQuote(Map<String, String> quote) {

		loginSqo();

		boolean hasCurrentQuote = selenium.isTextPresent("SQO reference");
		loggerContxt.info("having current quote....." + hasCurrentQuote);

		SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();

		// Create a SQO quote
		currentQuotePage = cq.createQuote(getProperty(".lob"), getProperty(".country"), hasCurrentQuote);
		loggerContxt.info("create quote page finished.....");
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".currentQuoteTitle"));
		assertTextPresentTrue(getProperty(".currentQuoteTitle"));

	}

	// @Test(description = "quote edit")
	public void runQuoteEdit(Map<String, String> quote) {

		// Find an existing customer
		SQOOSearchCustPage searchCustPage = currentQuotePage.findCustbyClick();
		loggerContxt.info("go to search customer page.....");

		// Assert.assertEquals("", "");
		// assert "Beust".equals("") : "Expected name Beust, for" + "";

		// display customer
		SQODisplayCustListPage displayCustListPage = searchCustPage.displaySQOCustomerListBySiteNum(quote.get("SOLD_TO_CUST_NUM"));
		loggerContxt.info("display customer page.....");

		// select customer and back current quote page
		currentQuotePage = displayCustListPage.selectCustomer();
		loggerContxt.info("select customer and back current quote page.....");
		
		quoteNum=currentQuotePage.getQuoteNum();
		loggerContxt.info("current quote number....."+quoteNum);
		// Enter "xprs//@Test.com" in the "* Email: " field under Quote contact
		// section.
		currentQuotePage.fillEmailAdr(getProperty(".custmail"));

		// Find reseller
		SQOSelectAResellerPage searchResellerPage = currentQuotePage.findResellertbyClick();
		loggerContxt.info("go to search reseller page.....");

		// display reseller
		SQODisplayResellerListPage displayResellerListPage = searchResellerPage.displayResellerByNum(quote.get("RSEL_CUST_NUM"));
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

		 currentQuotePage.setFulfillmentSourcesToDirect();

		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
		SQOFindPartsTabPage findPartsTabPage = partsAndPricingTab.findPartsLinkClick();

		Connection conn = null;
		String strPartList1 = "";
		String strPartList2 = "";
		/*
		 * try {
		 * 
		 * conn = this.getConnection(); Part part = new Part(conn); List
		 * partList = part.getPAPAEParts(null);
		 * 
		 * if (partList != null) { int partLenth = partList.size(); for (int i =
		 * 0; i < partLenth; i++) { if (i < 100) { strPartList1 +=
		 * partList.get(i).toString().trim() + ","; } else { strPartList2 +=
		 * partList.get(i).toString().trim() + ","; } } } } catch
		 * (ClassNotFoundException e) { e.printStackTrace(); } catch
		 * (SQLException e) { e.printStackTrace(); } finally { if (conn != null)
		 * { try { conn.close(); } catch (SQLException e) { e.printStackTrace();
		 * } } }
		 */

		strPartList1 = getProperty(".partList1");
		strPartList2 = getProperty(".partList2");
		
		SQOFindPartsSelectTabPage findPartsSelect = findPartsTabPage.findPartsLinkClick(strPartList1);

		loggerContxt.info("search first 100 piece of parts:....." + strPartList1);
		findPartsTabPage = findPartsSelect.selectPartsAndChgCriteriaClick(strPartList1);
		findPartsSelect = findPartsTabPage.findPartsLinkClick(strPartList2);
		loggerContxt.info("search the other parts:....." + strPartList2);
		findPartsSelect.selectPartsClick(strPartList2);
		partsAndPricingTab = findPartsSelect.rtn2DraftQuote();

		// Click on 'Browse parts' tab
		SQOBrowsePartsTabPage browsePartsTab = partsAndPricingTab.browsePartsLinkClick();
		loggerContxt.info("Click on 'Browse parts' tab.....");
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".partSelect"));
		assertTextPresentTrue(getProperty(".partSelect"));

		/*
		 * Click on Brand Lotus Click on Lotus 123 Click on License + SW
		 * Subscription & Support Check the checkbox of the first part listed
		 * Click the "Add selected parts to draft quote" link
		 */
		browsePartsTab.browseLotusPartsTab();
		browsePartsTab.addSelectedPartsToDraftQuoteLinkClick();
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".addpartsokmsg"));
		assertTextPresentTrue(getProperty(".addpartsokmsg"));
		partsAndPricingTab = browsePartsTab.returnToDraftQuoteLinkLinkClick();
		loggerContxt.info("Click on Brand Lotus Click on Lotus 123 Click on License + SW Subscription & Support " + "Check the checkbox of the first part listed Click the 'Add selected parts to draft quote' link.....");

		// Under Parts and pricing section, click on the "Recalculate quote"
		// link
		//partsAndPricingTab.recalculateQuotePPTab();
		
		addSaasPartToQuote(partsAndPricingTab);

		// Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SQOSalesInfoTabPage salesTab = partsAndPricingTab.gotoSalesInfoTab();
		loggerContxt.info("Go to SalesInf Tab end.....");
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".opportunityinfo"));
		assertTextPresentTrue(getProperty(".opportunityinfo"));

		salesTab.enterSalesInf(getProperty(".briefTitle"), getProperty(".quoteDesc"), getProperty(".busOrgCode"));

		salesTab.addQuoteEditor(getProperty(".quoteeditormail"));
		// press "Save" button, then press "Save" button again
		salesTab.saveDraftQuoteLink();

		SQORetrieveSavedSalesQuotePage savedSalesQuote = currentQuotePage.goSQORetrieveSavedSalesQuoteTab();
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".retrieveSavedSalesQuoteTitle"));
		assertTextPresentTrue(getProperty(".retrieveSavedSalesQuoteTitle"));
		currentQuotePage = savedSalesQuote.goViewDetailSavedQuote();

		// Select the last date of the current month as the
		// "Quote expiration date"
		currentQuotePage.selectExpirationDate(30);

		// go to Special Bid Tab
		loggerContxt.info("go to Special Bid Tab.....");
		SQOSpecialBidTabPage spcialBidTab = salesTab.goToSpecialBidTabClick();

		spcialBidTab.enterSpcialBidInf(getProperty(".justificationSummary"));
		currentQuotePage = spcialBidTab.rtnToCPTab();

	}

	public List<String> runSubmitQuote() {

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
		return currDraftQuotePage.submitDraftQuoteForSpecialBid(getProperty(".quoteeditormail"), getProperty(".quoteDesc"));
	}

	// @Test(description = "waiting for 15 min and login agin")
	public void runwaiting() {
		loggerContxt.info("quite webdirver.....");
		//quitWebdriver();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		loggerContxt.info("waiting started....." + sdf.format(new Date(System.currentTimeMillis())));

		currentQuotePage.waitForElementLoading(new Long(900001));
		
		loggerContxt.info("waiting end....." + sdf.format(new Date(System.currentTimeMillis())));
		
		teardown();
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

		loggerContxt.info("find quotes using the quote number:....."+quoteNum);
		SQODisplayStatusSearchReslutPage resultPO = statusSearchPO.findQuoteByNum(quoteNum);
		loggerContxt.info("view the detail CP.....");
		SQOSelectedQuoteCPTabPage viewDetailCPPO = resultPO.goDispQuoteReslt();

		loggerContxt.info("Press the 'Order' link at the bottom of the page.....");
		CheckOutBillAndShipPage checkoutPO = viewDetailCPPO.goToCheckout();
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".reviewsubmitorder"));
		assertTextPresentTrue(getProperty(".reviewsubmitorder"));

		CheckoutInf dto = new CheckoutInf();
		dto.setUserID(getProperty(".loginid"));
		dto.setUserEmail(getProperty(".emailaddress"));
		dto.setSubmit_first_name(getProperty(".submittedfirstname"));
		dto.setSubmit_last_name(getProperty(".submittedlastname"));
		dto.setSubmit_email(getProperty(".submittedmail"));
		dto.setSubmit_phone(getProperty(".submittedphone"));
		loggerContxt.info("View Checkout - Shipping and Billing page Enter necessary billing and shipping information. .....");
		ReviewSubmitOrderPage reviewSubmitOrder = checkoutPO.submitOrder(dto);
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".reviewsubmitorder"));
		assertTextPresentTrue(getProperty(".reviewsubmitorder"));

		reviewSubmitOrder.gotoConfirmation(getProperty(".invoiceno"));

		loggerContxt.info("verify current page whether having this content ......" + getProperty(".orderconfirm"));
		//assertPresentText(getProperty(".orderconfirm"));
	}
	

    /**
     * DOC Add SAAS part to quote.
     */
    protected void addSaasPartToQuote(SQOPartsAndPricingTabPage page) {
        /**
         * get the text of link: Browse Software as a Service
         */
        String browerSaasLinkText = getProperty(".browerSaas");
        loggerContxt.info("verefy current page whether having this link: " + browerSaasLinkText);

        // check if the browerSaasLink is presented
        boolean browerSaasLinkExist = getSelenium().isElementPresent("link=" + browerSaasLinkText);
        loggerContxt.info("browerSaasLinkExist hashcode: " + getSelenium().hashCode());
        if (browerSaasLinkExist) {
            loggerContxt.info("link: " + browerSaasLinkText + " exist in this page");
            String[] pathaArray = getProperty(".saasNodePathArray").split("/");
            String[][] components = { { getProperty(".saasPartXPath"), getProperty(".saasPartQuantity"), } };
            // open saas part and configure it.
            browseAndAddSaasPart(page, pathaArray, components);
        } else {
            loggerContxt.info("link: " + browerSaasLinkText + " does NOT exist in this page");
        }
    }

    private void browseAndAddSaasPart(SQOPartsAndPricingTabPage partsAndPricingTab, String[] treeNodeIds, String[][] components) {
    	partsAndPricingTab.browseSoftwareAsAServiceLinkClick();
		String text = getProperty("ECustomerCare");
		if (isTextPresent(text)) {
			loggerContxt.info(text);
		}
        SQOBrowseSoftwareAsServiceTabPage browseSoftwareAsServiceTabPage = new SQOBrowseSoftwareAsServiceTabPage(getDriver());
        browseSoftwareAsServiceTabPage.waitForElementLoading(new Long(25000));

        loggerContxt.info("browse SAAS page finished.....");
        // check if there are some Saas offerings
        // There are no Software as a Service offerings to configure.
        if (selenium.isTextPresent(getProperty(".noSaasOfferingsMsg"))) {
            loggerContxt.info(getProperty(".noSaasOfferingsMsg"));
            return;
        }

        browseSoftwareAsServiceTabPage.clickTreeNodesById(treeNodeIds);
        configureService(browseSoftwareAsServiceTabPage, getProperty(".SaasPartId"), components);

    }

    public void configureService(SQOBrowseSoftwareAsServiceTabPage page, String configureId, String[][] components) {
        ((JavascriptExecutor) driver).executeScript("configureService('" + configureId + "')");
        WebElement webElement = page.waitForElementById("dijit_DialogUnderlay_0");
        if (webElement != null) {
            // driver.findElement(By.xpath("/html/body/div[6]/div[2]/div/div[2]/div[2]/span/span/span/span[3]")).click();//
            // gts confirm
            page.addSaasPart(components);
            loggerContxt.info("add SAAS part finished.....");
            page.waitForElementLoading(25000L);
        }
    }
    
    
	protected void approveSpecialBidQuote(List<String> lstApprover) {
		for (String approver : lstApprover) {
			approveQuote(approver);
			reset();
		}
	}


	protected void approveQuote(String userRole) {
		
		loggerContxt.info("quite webdirver.....");
		quitWebdriver();
		try {
			setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SQOHomePage homePage = login(userRole);
		if (null == homePage) {
			loggerContxt.info("login failure!");
			throw new RuntimeException();
		}
		loggerContxt.info("approve quote detail.");

		loggerContxt.info("approveQuote");
		SQOApproveQueuePage approveQueue = homePage.gotoApproveQueue();

		loggerContxt
				.info("verefy current page whether having this content ......"
						+ getProperty(".approveQueueTitle"));
		assertTextPresentTrue(getProperty(".approveQueueTitle"));

		SQOSubmitSQSpecialBidTabPage quoteDetails = null;
		loggerContxt.info("query quote page");
		quoteNum = quoteNum.trim();
		quoteDetails = approveQueue.findQuoteByNum(quoteNum);
		if (null != quoteDetails) {
			loggerContxt.info("quote detail page");
			String approveResult = getProperty(".approveResult");
			if ("approve".equals(approveResult)) {
				quoteDetails.submitApproveResult();
			} else if ("reject".equals(approveResult)) {
				quoteDetails.submitRejectResult();
			}
		}
	}
	

	protected SQOHomePage login(String role) {
		SQOHomePage sqoHomePage = null;
		loggerContxt.info("Enter: login method!");
		reset(prop.getProperty(env + ".sqo_jump_url"));
		// login system by jump.html
		SQOJumpPage jumpPage = new SQOJumpPage(getDriver());
		String countruCode = getProperty(".countryCode");
		String userName = getProperty(".userName" + role);
		String accessLevel = getProperty(".accessLevel5");
		
		jumpPage.loginIn(countruCode, userName, accessLevel);
		sqoHomePage = new SQOHomePage(getDriver());

		loggerContxt.info("Exit: login method!");
		return sqoHomePage;
	}

	@Test(description = "quote order")
	public void run() {
		Map<String, String> quote = new HashMap<String, String>();
		quote.put("CURRNCY_CODE", getProperty(".country"));
		quote.put("SOLD_TO_CUST_NUM", getProperty(".customerNum"));
		quote.put("RSEL_CUST_NUM", getProperty(".resellerNum"));
		
		runCreateQuote(quote);
		runQuoteEdit(quote);
		approveSpecialBidQuote(runSubmitQuote());
		runwaiting();
		runOrder();
		loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
	}

	public static void main(String[] args) throws Exception {
		SQOPPXPRSTest test = new SQOPPXPRSTest();
		test.setUp();
		test.run();
	}

}
