package com.ibm.dsw.automation.testcase.probe;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.PriceBookPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOCreateQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayCustListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsSelectTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOJumpPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOOSearchCustPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;

@SuppressWarnings("deprecation")
public class SQOInternalProbeScript extends BaseTestCase {

	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOInternalProbeScript.class.getName());
	private SQOMyCurrentQuotePage currentQuotePage = null;
	private SQOHomePage sqoHomePage = null;
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
		loggerContxt.info("Click on create a Sales Quote");
		currentQuotePage = cq.createQuote(getProperty(".lob"), getProperty(".country"), hasCurrentQuote);
		loggerContxt.info("Choose Passport Advantage and Country United States then click continue.  Click on Okay in pop-up.");
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".currentQuoteTitle"));
		assertTextPresentTrue(getProperty(".currentQuoteTitle"));
	}

	// @Test(description = "quote edit")
	public void runQuoteEdit(Map<String, String> quote) {
		//3. Find an existing customer
		loggerContxt.info("Click on Find an existing customer");
		SQOOSearchCustPage searchCustPage = currentQuotePage.findCustbyClick();
		loggerContxt.info("go to search customer page.....");

		// display customer
		loggerContxt.info("Put in Alstom Power in the Partial or Complete name field and click on Submit");
		SQODisplayCustListPage displayCustListPage = searchCustPage.displayCustomerListByName(quote.get("FINDBY_CUSTOMER_NAME"));
		loggerContxt.info("display customer page.....");

		// select customer and back current quote page
		loggerContxt.info("Choose select customer link for Agreement 11520");
		currentQuotePage = displayCustListPage.clickCustomer11520();
		loggerContxt.info("Choose select customer link for Agreement 11520--->done!");
		loggerContxt.info("select customer and back current quote page.....");
		loggerContxt.info("Click on parts and pricing tab");
		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
		loggerContxt.info("Click on parts and pricing tab--->done!");
		loggerContxt.info("Click on Find parts (excludes Software as a Service");
		SQOFindPartsTabPage findPartsTabPage = partsAndPricingTab.findPartsLinkClick();
		loggerContxt.info("Click on Find parts (excludes Software as a Service--->done!");
		String strPartList1 = getProperty(".customerDescrip");
		loggerContxt.info("Enter 'DB2 Connect Enterprise Edition' into Part description field and click submit");
		SQOFindPartsSelectTabPage findPartsSelect = findPartsTabPage.findPartsByPartDes(strPartList1);
		loggerContxt.info("Enter 'DB2 Connect Enterprise Edition' into Part description field and click submit--->done!");
		loggerContxt.info("search first 100 piece of parts:....." + strPartList1);
		loggerContxt.info("Check off the first part number and click on 'add selected parts to draft quote' link");
		findPartsSelect.selectPartOfParts();
		loggerContxt.info("Check off the first part number and click on 'add selected parts to draft quote' link--->done!");
		loggerContxt.info("Click on sales information tab");
		partsAndPricingTab.gotoSalesInfoTab(); 
		loggerContxt.info("Click on sales information tab--->done!");
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
		quote.put("FINDBY_CUSTOMER_NAME", getProperty(".findByCustomerName"));
		
		runCreateQuote(quote);
		runQuoteEdit(quote);
		loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
	}
	
	public void testIsLogin(){
		PriceBookPage pricebok = new PriceBookPage(driver);
		pricebok.testIsLogin(settingsProp.getProperty("sqo_username"),settingsProp.getProperty("sqo_password"));
	}

	public static void main(String[] args) throws Exception {
		SQOInternalProbeScript test = new SQOInternalProbeScript();
		test.setScriptName("SQO Internal Probe Script Automation Test");
		try {
			test.setUp();
            test.run();
			//input the login userid and password
			test.testIsLogin();
			test.sendAlertMail("build/mailBody.vm","succeed",null);
		} catch (Exception e) {
			// TODO: handle exception
			loggerContxt.error(e.getCause().getMessage());
			test.sendAlertMail("build/mailBody.vm","faild",null);
			e.printStackTrace();
		}
	}
}