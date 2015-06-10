package com.ibm.dsw.automation.testcase;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.testng.log4testng.Logger;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.FunctionIdProvider;
import com.ibm.dsw.automation.flow.SQORenewalQuoteSearchFlow;
import com.ibm.dsw.automation.pageobject.sqo.CheckOutBillAndShipPage;
import com.ibm.dsw.automation.pageobject.sqo.ReviewSubmitOrderPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOApproveQueuePage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayStatusSearchReslutPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQORQCustomerActionsSearchPage;
import com.ibm.dsw.automation.pageobject.sqo.SQORenewalQuoteSearchTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuoteCPTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOStatusSearchTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitSQSpecialBidTabPage;
import com.ibm.dsw.automation.testcase.xprs.sqo.SQOLogin;
import com.ibm.dsw.automation.vo.CheckoutInf;

public class SQOOrderQuote extends BaseTestCase{
	
	
	private SQOHomePage sqoHomePage = null;
	private SQORenewalQuoteSearchFlow quoteFlow = null;
	private SQORenewalQuoteSearchTabPage renewalQuoteSearchPage = null;
	private SQORQCustomerActionsSearchPage customerActionsPage = null;
	private SQOLogin sqoLogin = new SQOLogin();
	private static Logger loggerContxt = Logger.getLogger(SQOOrderQuote.class);

	public void approveQuote(String quoteNumber) throws Exception{
		String approver = getProperty("approver.name");
		String password = getProperty("approver.password");
		if (StringUtils.isBlank(password)) {
			password = FunctionIdProvider.getPWDForFuncId(approver);
			if (StringUtils.isBlank(password)) {
				throw new Exception("Failed to get the password of approver:: " + approver);
			}
		}
		SQOLogin login = new SQOLogin();
		login.getLogonInf().setAccessLevel("5");
		login.getLogonInf().setSqoLogonUser(approver);
		login.getLogonInf().setSqoUserPwd(password);
		SQOHomePage homePage = login.getSqoHomePage();
		
		if (null == homePage) {
			loggerContxt.info("SQO Login failure!");
			throw new RuntimeException();
		}
		loggerContxt.info("Approve quote detail.");

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
		quoteNumber = quoteNumber.trim();
		quoteDetails = approveQueue.findQuoteByNum(quoteNumber);
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
	
	public void waitingForApproval(long minutes){
		this.getDriver().manage().timeouts().implicitlyWait(minutes, TimeUnit.MINUTES);
	}
	
	public void Order(String quoteNumber){
		loggerContxt.info("submit current quote page.....");
		loggerContxt.info("go to sqo home page finished.....");
		String quoteNum = getProperty("quote_Number");
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
	
	public void login(){
		try {
			sqoLogin.loginSqo();
			this.driver = sqoLogin.getDriver();
			this.selenium  = sqoLogin.getSelenium();
		} catch (Exception e) {
			loggerContxt.fatal("Failed to login to SQO:: " + this.env,e);
		}finally{
			// Now you can do whatever you need to do with it, for example copy somewhere
			try {
				captureScreenshot(null);
			} catch (Exception e1) {
				loggerContxt.fatal("Failed to copy the screenshot:: ",e1);
			}
		}
	}
	
	public static void main(String[] args) {
		SQOOrderQuote testcase = new SQOOrderQuote();
		testcase.initTestData();
		testcase.login();
		loggerContxt.info("Login in SQO page.");
	}
}
