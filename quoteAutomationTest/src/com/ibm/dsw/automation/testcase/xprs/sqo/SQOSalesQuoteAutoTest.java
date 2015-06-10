package com.ibm.dsw.automation.testcase.xprs.sqo;

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
 * Used for the XPRS script auto validation in SIT phase.
 * XPRS script name: Sales Quote (main script) 
 * Team room link: Notes://D01DBL35/8525721300181EEE/4B87A6F6EAEAADD385256A2D006A582C/9BFDA5C0381FBA8D8525731D0072A9E6 
 * 
 * @since 2014-4-24
 * @author Yuan He Qiang 
 */
@SuppressWarnings("deprecation")
public class SQOSalesQuoteAutoTest extends BaseTestCase {

	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOSalesQuoteAutoTest.class.getName());
	private SQOMyCurrentQuotePage currentQuotePage = null;
	private SQOSpecialBidTabPage sqoSepcialbid;
	private SQOHomePage sqoHomePage = null;
	private final SQOPartsAndPricingTabPage partsAndPricingTab =null;
	private String quoteNum="";

	private void loginSqo() {
		loggerContxt.info("env....." + env);
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
		SQODisplayResellerListPage displayResellerListPage = searchResellerPage.displayResellerListByName(getProperty(".findByDistributorOrResllerName"));
		loggerContxt.info("display reseller page.....");

		// select reseller and back current quote page
		currentQuotePage = displayResellerListPage.selectReseller();
		loggerContxt.info("select reseller and back current quote page.....");

		// Find distributor
		SQOSelectADistributorPage searchDistributorPage = currentQuotePage.findDistributorbyClick();
		loggerContxt.info("go to search distributor page.....");

		// display distributor
		SQODisplayDistributorListPage displayDistributorListPage = searchDistributorPage.displayCustomerListByName(getProperty(".findByDistributorOrResllerName"));
		loggerContxt.info("display distributor page.....");

		// select distributor and back current quote page
		currentQuotePage = displayDistributorListPage.selectDistributor();
		loggerContxt.info("select distributor and back current quote page.....");

		// currentQuotePage.setFulfillmentSourcesToDirect();
		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
		SQOFindPartsTabPage findPartsTabPage = partsAndPricingTab.findPartsLinkClick();

		String strPartList1 = "";
		
		strPartList1 = getProperty(".partList1");

		SQOFindPartsSelectTabPage findPartsSelect = findPartsTabPage.findPartsLinkClick(strPartList1);

		loggerContxt.info("search first 100 piece of parts:....." + strPartList1);
		//findPartsSelect.clickExpandAllLink();
		findPartsSelect.selectPartsClick(strPartList1);
		partsAndPricingTab = findPartsSelect.rtn2DraftQuote();

		// Click on 'Browse parts' tab
		SQOBrowsePartsTabPage browsePartsTab = partsAndPricingTab.browsePartsLinkClick();
		loggerContxt.info("Click on 'Browse parts' tab.....");
		//loggerContxt.info("verify current page whether having this content ......" + getProperty(".partSelect"));
		assertTextPresentTrue(getProperty(".partSelect"));
		browsePartsTab = browsePartsTab.clickWebSphereSoftware();
		browsePartsTab.browseAPL();
		
		/*
		 * Click on Brand Lotus Click on Lotus 123 Click on License + SW
		 * Subscription & Support Check the checkbox of the first part listed
		 * Click the "Add selected parts to draft quote" link
		 */
		//browsePartsTab.browseLotusPartsTab();
		browsePartsTab.addSelectedPartsToDraftQuoteLinkClick();
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".addpartsokmsg"));
		assertTextPresentTrue(getProperty(".addpartsokmsg"));
		partsAndPricingTab = browsePartsTab.returnToDraftQuoteLinkLinkClick();
		loggerContxt.info("Click on Brand Lotus Click on Lotus 123 Click on License + SW Subscription & Support " + "Check the checkbox of the first part listed Click the 'Add selected parts to draft quote' link.....");

		// Under Parts and pricing section, click on the "Recalculate quote"
		// link
		partsAndPricingTab.recalculateQuotePPTab();
//		addSaasPartToQuote(partsAndPricingTab);

		// Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SQOSalesInfoTabPage salesTab = partsAndPricingTab.gotoSalesInfoTab();
		loggerContxt.info("Go to SalesInf Tab end.....");
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".opportunityinfo"));
		assertTextPresentTrue(getProperty(".opportunityinfo"));

		salesTab.enterSalesInf(getProperty(".briefTitle"), getProperty(".quoteDesc"), getProperty(".busOrgCode"));

		salesTab.addQuoteEditor(getProperty(".quoteeditormail"));
		salesTab.specialBidTabLink.click();
		//salesTab.custPartnerTabLink.click();
		SQOMyCurrentQuotePage sqocq = salesTab.rtnToCPTab();
		// press "Save" button, then press "Save" button again
		//salesTab.saveDraftQuoteLink();
		sqocq.selectDirectChnl();
		
		//20 upload and download  salesTab.goToSpecialBidTabClick();
		SQOSpecialBidTabPage specialBid = currentQuotePage.goToSpecialBidTab();
        //SQOSpecialBidTabPage specialBid = sqoHomePage.gotoMyCurrentQuotePage().goToSpecialBidTab();
//		//export files
		/*specialBid.saveQuoteAsExcel();
		specialBid.saveQuoeAsSymphony();*/
		currentQuotePage.clickDownloadQTAsRich(getProperty(this.getEnv()+".richLinkText"));
		currentQuotePage.clickDownloadQTAsRich(getProperty(this.getEnv()+".ExcelLinkText"));
		currentQuotePage.clickDownloadQTAsRich(getProperty(this.getEnv()+".spreadsheetLinkText"));
		
		specialBid.enterSpcialBidInf(getProperty(".justificationSummary"));
		specialBid.uploadFile(getProperty("uploadFile"));		
		//saveQuoteAsDraftLink
		SQOSpecialBidTabPage specialBidpage = specialBid.clickSaveAsDraftLink();
		
		SQORetrieveSavedSalesQuotePage savedSalesQuote = currentQuotePage.goSQORetrieveSavedSalesQuoteTab();
		savedSalesQuote.retrieveQuote(getProperty("retrieveTimeFilter"),getProperty("retrieveValue"));
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".retrieveSavedSalesQuoteTitle"));
		assertTextPresentTrue(getProperty(".retrieveSavedSalesQuoteTitle"));
		currentQuotePage = savedSalesQuote.goViewDetailSavedQuote();
		
		
		currentQuotePage.clickAllTabs();
		// Select the last date of the current month as the
		// "Quote expiration date"
		currentQuotePage.selectLastDateAsExpirationDate();
		currentQuotePage.selectDirectChnl();
		currentQuotePage.fillEmailAdr(getProperty(".custmail"));
		
		SQOSalesInfoTabPage saleInfopage = currentQuotePage.goToSalesInfoTab();
		saleInfopage.enterSalesInf(getProperty(".briefTitle"), getProperty(".quoteDesc"), getProperty(".busOrgCode"));
		currentQuotePage = saleInfopage.rtnToCPTab();
		currentQuotePage.submitCurrentDraftQuote();
		
	}

	public List<String> runSubmitQuote() {

		loggerContxt.info("submit current quote page.....");
		SQOSubmitCurrDraftQuotePage currDraftQuotePage = currentQuotePage.submitCurrentDraftQuote();
		if (currDraftQuotePage == null) {
			return null;
		}
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

		currentQuotePage.waitForElementLoading(new Long(60));
		
		loggerContxt.info("waiting end....." + sdf.format(new Date(System.currentTimeMillis())));
		
		teardown();
		
		try {
			Thread.sleep(15*60*1000);
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
		if(lstApprover == null){
			return;
		}
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
		if (!"prod".equalsIgnoreCase(getEnv())) {
			
		approveSpecialBidQuote(runSubmitQuote());
		runwaiting();
		runOrder();
		}
		loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
	}

	public static void main(String[] args) throws Exception {
		SQOSalesQuoteAutoTest test = new SQOSalesQuoteAutoTest();
		test.setScriptName("SQO Sales Quote(main script) Automation Test.");
		try {
			test.setUp();
			test.run();
			test.getDriver().close();
			test.sendAlertMail("build/mailBody.vm","succeed",null);
		} catch (Exception e) {
			// TODO: handle exception
			if (e.getCause() != null) {
				loggerContxt.error(e.getCause().getMessage());
			}else{
				loggerContxt.error(e.getMessage());
				
			}
//			test.sendAlertMail("build/mailBody.vm","faild",null);
			e.printStackTrace();
		}
	}
}
