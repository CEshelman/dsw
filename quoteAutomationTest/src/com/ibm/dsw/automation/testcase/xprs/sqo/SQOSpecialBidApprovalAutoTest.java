package com.ibm.dsw.automation.testcase.xprs.sqo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOApproveQueuePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOCreateQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayCustListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayDistributorListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayResellerListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsSelectTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOOSearchCustPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectADistributorPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectAResellerPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSpecialBidTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitCurrDraftQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitSQSpecialBidTabPage;

/**
 * Used for the XPRS script auto validation in SIT phase.
 * XPRS script name: Special Bid Approval 
 * Team room link: Notes://D01DBL35/8525721300181EEE/4B87A6F6EAEAADD385256A2D006A582C/9BFDA5C0381FBA8D8525731D0072A9E6 
 * @author duzhiwei
 *
 */
@SuppressWarnings("deprecation")
public class SQOSpecialBidApprovalAutoTest extends BaseTestCase {

	private WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(this.getClass().getName());
	private SQOMyCurrentQuotePage currentQuotePage = null;
	private SQOHomePage sqoHomePage = null;
	private String quoteNum="";

	private void loginSqo() {
		loggerContxt.info("env....." + env);
		
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		
		sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");

		loggerContxt.info("verify current page whether having this content ......" + getProperty(".softQtOrd"));
		assertTextPresentTrue(getProperty(".softQtOrd"));
		driver.manage().window().maximize();
	}

	// @Test(description = "Create a SQO quote")
	public void runCreateQuote() {
		loginSqo();
		boolean hasCurrentQuote = selenium.isTextPresent("SQO reference");
		String lob = propSub.getProperty("quote.type");
		String country = propSub.getProperty("country.region");
		loggerContxt.info("having current quote....." + hasCurrentQuote);
		SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();
		// Create a SQO quote
		currentQuotePage = cq.createSalesQuote(lob, country);
		loggerContxt.info("create quote page finished.....");
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".currentQuoteTitle"));
		assertTextPresentTrue(getProperty(".currentQuoteTitle"));
	}

	// @Test(description = "quote edit")
	public void runQuoteEdit() {

		// Find an existing customer
		String customerName = propSub.getProperty("find.customer.name");
		SQOOSearchCustPage searchCustPage = currentQuotePage.findCustbyClick();
		loggerContxt.info("go to search customer page.....");

		// display customer
		SQODisplayCustListPage displayCustListPage = searchCustPage.displayCustomerListByName(customerName);
		loggerContxt.info("display customer page.....");

		// select customer and back current quote page
		currentQuotePage = displayCustListPage.selectFirstCustomer();
		loggerContxt.info("select customer and back current quote page.....");
		
		quoteNum=currentQuotePage.getQuoteNumber();
		loggerContxt.info("current quote number is :: "+quoteNum);

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

		String strPartList1 = getProperty(".partNumList1");

		SQOFindPartsSelectTabPage findPartsSelect = findPartsTabPage.findPartsLinkClick(strPartList1);

		loggerContxt.info("search first 100 piece of parts:....." + strPartList1);
//		findPartsSelect.clickExpandAllLink();
		findPartsSelect.selectPartsClick(strPartList1);
		partsAndPricingTab = findPartsSelect.rtn2DraftQuote();
		//change the quote discount percent and recalculate
		String newPercent = getProperty("quote.discount.percent");
		partsAndPricingTab.changeQuoteDiscPercent(newPercent);

		// Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SQOSalesInfoTabPage salesTab = partsAndPricingTab.gotoSalesInfoTab();
		loggerContxt.info("Go to SalesInf Tab end.....");
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".opportunityinfo"));
		assertTextPresentTrue(getProperty(".opportunityinfo"));

		salesTab.enterSalesInf(getProperty(".briefTitle"), getProperty(".quoteDesc"), getProperty(".busOrgCode"));
		
		//20 upload and download  salesTab.goToSpecialBidTabClick();
		SQOSpecialBidTabPage specialBid = currentQuotePage.goToSpecialBidTab();
		specialBid.enterSpcialBidInfo(getProperty("Justification.Summary"));
//		specialBid.uploadFile(getProperty("uploadFile"));
		//specialBid.okBtn.click();
		SQOMyCurrentQuotePage sqocq = salesTab.rtnToCPTab();
		sqocq.selectLastDateAsExpirationDate();
		//Enter the quote contact email in the CP tab
		String quoteContactEmail = getProperty("quote.contact.email");
		sqocq.fillEmailAdr(quoteContactEmail);
		
		partsAndPricingTab = sqocq.goToPartsAndPricingTab();
		//click 'Calculate Equity Curve discount guidance' link
		partsAndPricingTab.checkECGuide();
		
//		sqocq.submitQuote();
		
	}

	public void runSubmitQuote() throws Exception{
		loggerContxt.info("submit current quote page.....");
		SQOSubmitCurrDraftQuotePage currDraftQuotePage;
		try{
			currDraftQuotePage = currentQuotePage.submitCurrentDraftQuote();
		}catch(Exception e){
			loggerContxt.error("Failed to load the Submit current draft quote page, this may due to your account lack of submit priviledge..");
			throw new Exception(e);
		}
		
		loggerContxt.info("submit the quote.....");
		currDraftQuotePage.submitDraftQuoteForSpecialBidWithoutEmail(getProperty("approver.level0"), getProperty("approver.level3"));
		loggerContxt.info("Create a sales quote ended...");
		getDriver().close();
	}

	// @Test(description = "waiting for 15 min and login agin")
	public void runwaiting() {
		loggerContxt.info("quite webdirver.....");
		//quitWebdriver();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		loggerContxt.info("waiting started....." + sdf.format(new Date(System.currentTimeMillis())));

		currentQuotePage.waitForElementLoading(new Long(5*60*1000));
		
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
    
	protected void approveQuote() throws Exception {
		
		loggerContxt.info("quite webdirver.....");
		
		SQOLogin login = new SQOLogin();
		login.getLogonInf().setAccessLevel("5");
		login.getLogonInf().setSqoLogonUser(getProperty("approver.level0.user"));
		login.loginSqo(this, getProperty(".logonFlg"));
		SQOHomePage homePage = login.getSqoHomePage();
		
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
		//Note to set a new Driver
		setDriver(login.getDriver());
		setSelenium(login.getSelenium());
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

	@Test(description = "quote order")
	public void run() throws Exception {
		
		runCreateQuote();
		runQuoteEdit();
		runSubmitQuote();
		String approver = getProperty("approver.level0.user");
//		quoteNum = "0004020425";
		if (Boolean.parseBoolean(getProperty("need.approval.flag"))) {
			
			approveQuote();
		}
		loggerContxt.info("@Test " + this.getScriptName() + " has passed!");
	}

	public static void main(String[] args) throws Exception {
		SQOSpecialBidApprovalAutoTest test = new SQOSpecialBidApprovalAutoTest();
		try{
		test.setScriptName("SQO Special Bid Approval Automation Test");
		test.setUp();
//		test.initTestData();
		test.run();
		test.sendAlertMail("build/mailBody.vm","succeed",null);
		}catch(Exception e){
			File attach = test.captureScreenshot(test.getClass().getSimpleName());
			test.sendAlertMail("build/mailBody.vm","failed",attach);
//			test.loggerContxt.fatal(String.format("%s Failed..", test.getScriptName()),e);
			test.teardown();
			e.printStackTrace();
		}
	}
}
