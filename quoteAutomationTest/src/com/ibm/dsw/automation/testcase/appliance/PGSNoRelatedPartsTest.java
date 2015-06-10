package com.ibm.dsw.automation.testcase.appliance;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.pageobject.pao.PAOHomePage;
import com.ibm.dsw.automation.pageobject.pgs.CreateQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.DisplayCustListPage;
import com.ibm.dsw.automation.pageobject.pgs.DisplayResellerListPage;
import com.ibm.dsw.automation.pageobject.pgs.FindExistingCustomerPage;
import com.ibm.dsw.automation.pageobject.pgs.FindPartsTabPage;
import com.ibm.dsw.automation.pageobject.pgs.LoginPage;
import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSApprovalTabPage;
import com.ibm.dsw.automation.pageobject.pgs.PGSFindPartsSelectTabPage;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSSiteSelectPage;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.pgs.SalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.pgs.SelectAResellerPage;

/**
 * This is a test class for PGS New appliance - no related parts
 * https://igartc03.swg.usma.ibm.com/jazz/web/projects/CVT%20DSW#action=com.ibm.team.workitem.viewWorkItem&id=420091
 ************************************************************************
 *  
 *  Assign a CRAD date in the past
	Validate you can't submit the quote
	Assign a future CRAD date
	Validate the quote can be submitted
	Validate MTM/Serial # is not populated on the quote
	Validate no MTM/Serial # display
	
	Try to submit the quote without first setting a CRAD date
	Validate you can not submit the quote
	Validate there is a field asking if this is a new or POC system	
	Validate the start/end dates are not populated on the quote
	Validate it is assigned an appliance ID
	Validate there is an mandatory field to add CRAD date
	Validate there is a field asking if this is a new or POC system

 *************************************************************************
 * 
 * 
 * @author suchuang
 * @date Dec 14, 2012
 */
public class PGSNoRelatedPartsTest extends BaseTestCase {

	/**
	 * loggerContxt
	 */
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(PGSNoRelatedPartsTest.class.getSimpleName());
	
	

	/**
	 * appliance main part number
	 */
	public final static String MAIN_PART = "D0DVPLL"; // D0DVPLL,D0LTYLL,D0NGALL

	/**
	 * appliance sub part number
	 */
	public final static String SUB_PART = "D0DQ4LL"; // D0DQ4LL,E0949LL,D0DQ3LL
	
	/**
	 * appliance ID regex
	 */
	public final static String APP_ID_REGEX = MAIN_PART + "-"/*"-\\d{8}"*/;

	/**
	 * PGS current quote page objects
	 */
	private MyCurrentQuotePage currentQuotePage = null;

	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	@Test(description = "Create a PGS quote")
	public void run() {

		// Login PGS
		LoginPage page = new LoginPage(this.driver, new Long(20));
		PGSSiteSelectPage siteSelectPage = page.loginAsToSiteSelectPage(
				getLoginUser(), getLoginPasswd());
		String paoSiteName = getProperty(".siteSelect");
		PAOHomePage homePage = siteSelectPage.gotoPAOHomePage(paoSiteName);
		loggerContxt.info("go to PGS");
		PGSHomePage pgsHomePage = homePage.gotoPGS();

		// Create a PGS quote
		CreateQuotePage cq = pgsHomePage.gotoCreateQuote();
		currentQuotePage = cq.createQuote();
		assertTextPresentTrue(getProperty(".currentQuoteTitle"));

		// Create Customer
		createCustomer(currentQuotePage);
		
		// set salse info
		setSalesInfo(currentQuotePage);
		
		// set approval
		setApproval(currentQuotePage);
		
		// Create parts
		createMainPart(currentQuotePage);
		
		
		// Validate the start/end dates are not populated on the quote
//		validateStartAndEndDate();
		
		// Validate it is assigned an appliance ID
		validateApplianceID();

		// Validate there is an mandatory field to add CRAD date
		validateCRADDate();

		// Validate there is a field asking if this is a new or POC system
		validateHasNewOrPOC();

		// Validate there is a field asking if this is a POC conversion - choose
		// No
		validateChooseNewAndNo();
	
		/*
		 * Try to submit the quote without first setting a CRAD date<br>
		 * Validate you can not submit the quote<br>
		 */
		validateSubmitWithoutCRADDate();
		
		/*
		 * Assign a CRAD date in the past
		 * Validate you can't submit the quote
		 */
		//validateSubmitPastCRADDate();
		
		/*
		 * Assign a future CRAD date
		 * Validate the quote can be submitted
		 */
		//validateSubmitFutureCRADDate();
		
		// Validate MTM/Serial # is not populated on the quote
		//validateMTMAndSerialUnpopulated();

		// Validate no MTM/Serial # display
		validateNoMTMAndSerialDisplay();
		
		validatesubmitQuote();
		
		loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
	}

	/**
	 * 
	 * @author suchuang
	 * @date Dec 21, 2012
	 * @param currentQuotePage
	 */
	private void setApproval(MyCurrentQuotePage currentQuotePage) {
		PGSApprovalTabPage approvalTabPage = currentQuotePage.goToApprovalTab();
		approvalTabPage.enterApprovalInfo(getProperty(".spBidRgn"), getProperty(".spBidDist"));
		approvalTabPage.enterSpcialBidInf("justification for test");
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Dec 21, 2012
	 * @param currentQuotePage
	 */
	private void setSalesInfo(MyCurrentQuotePage currentQuotePage){
		//Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SalesInfoTabPage salesTab = currentQuotePage.goToSalesInfoTabPage();
		loggerContxt.info("Go to SalesInf Tab end.....");
		
		loggerContxt.info("enter sales info");
		salesTab.enterSalesInf(getProperty(".briefTitle"), getProperty(".quoteDesc"));
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 6, 2013
	 * @param currentQuotePage
	 */
	private void createCustomer(MyCurrentQuotePage currentQuotePage) {
		
	 	// Find an existing customer
		FindExistingCustomerPage searchCustPage = currentQuotePage.goToFindCustomerTab();
		loggerContxt.info("go to search customer page.....");

		// display customer
		DisplayCustListPage displayCustListPage = searchCustPage.displayCustomerListBySiteNum(getProperty(".siteNum"));
		if (displayCustListPage == null) {
			displayCustListPage = searchCustPage.displayCustomerListBySiteNum(getProperty(".customerNum"));
		}
		loggerContxt.info("display customer page.....");
		
		// select customer and back current quote page
		currentQuotePage = displayCustListPage.selectCustomer();
		loggerContxt.info("select customer and back current quote page.....");
		
		loggerContxt.info("On Customer and business partners tab, Enter 'Dummy' /n" +
				" in the First Name field and Last name field, enter '111' in the phone and fax field," +
				"/n enter 'avnetus@ibm.com' in the Email field " +
				"/n and click 'Customer and business partners tab' to persist the change");
		currentQuotePage.fillCustomerContact(
				getProperty(".cntFirstName"),
				getProperty(".cntLastName"),
				getProperty(".cntPhoneNumFull"),
				getProperty(".cntFaxNumFull"),
				getProperty(".cntEmailAdr"));
		
		// Find reseller
		loggerContxt.info("Enter '0007186058' in the reseller number field and click 'Submit' button");
		SelectAResellerPage searchResellerPage = currentQuotePage.goToFindResellerTab();
		loggerContxt.info("go to search reseller page.....");
		
		// display reseller
		loggerContxt.info("click 'Select reseller' link in the reseller selection result page");
		DisplayResellerListPage displayResellerListPage = searchResellerPage
				.displayResellerListByNum(getProperty(".resellerNum"));
		loggerContxt.info("display reseller page.....");
		
		displayResellerListPage.selectReseller();
		
		currentQuotePage.selectExpirationDate(30);
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 * @param myCurrentQuotePage
	 */
	private void createMainPart(MyCurrentQuotePage myCurrentQuotePage) {
		PartsAndPricingTabPage partsAndPricingTab = myCurrentQuotePage
				.goToPartsAndPricingTab();
		
		// find main part
		findMainPart(partsAndPricingTab);
		
		//Set SalesInf
		setMainPartSalesInfo(partsAndPricingTab);
	}

	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 * @param partsAndPricingTab
	 */
	private void setMainPartSalesInfo(PartsAndPricingTabPage partsAndPricingTab){
		//Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SalesInfoTabPage salesTab = partsAndPricingTab.gotoSalesInfoTabPGS();
		loggerContxt.info("Go to SalesInf Tab end.....");
		
		loggerContxt.info("enter sales info");
		salesTab.enterSalesInf(getProperty(".briefTitle"), getProperty(".quoteDesc"));
		
		currentQuotePage.goToPartsAndPricingTab();
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 * @param myCurrentQuotePage
	 */
	private void saveQuoteAsDraftQuote(MyCurrentQuotePage myCurrentQuotePage){
		loggerContxt.info("save quote as draft");
		((PGSMyCurrentQuotePage) myCurrentQuotePage).saveQuoteAsDraftQuote();
		reset();
	}
	
	private void submitQuote(MyCurrentQuotePage myCurrentQuotePage) {
		loggerContxt.info("submit quote");
		((PGSMyCurrentQuotePage) myCurrentQuotePage).submitQuote();
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 * @param page
	 */
	private void findMainPart(PartsAndPricingTabPage page) {
		loggerContxt.info("find Main part");
		findPart(page, MAIN_PART + "," + SUB_PART);
	}

	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 * @param page
	 * @param partList
	 */
	private void findPart(PartsAndPricingTabPage page, String partList) {
		FindPartsTabPage findPartsTabPage = (FindPartsTabPage) page
				.findPartsLinkClick();
		PGSFindPartsSelectTabPage findPartsSelectTabPage = findPartsTabPage
				.findPartsLinkClick(partList);
		findPartsSelectTabPage.selectPartsClick();
		findPartsSelectTabPage.rtn2DraftQuote();
	}
	
	/*
	 * Create a sales quote in PGS Add an appliance part Validate the start/end
	 * dates are not populated on the quote Validate it is assigned an appliance
	 * ID Validate there is an mandatory field to add CRAD date Validate there
	 * is a field asking if this is a new or POC system Set the quantity to 1
	 * Choose New Validate there is a field asking if this is a POC conversion -
	 * choose No Do not make it special bid Try to submit the quote without
	 * first setting a CRAD date Validate you can not submit the quote Assign a
	 * CRAD date in the past Validate you can't submit the quote Assign a future
	 * CRAD date Validate the quote can be submitted
	 */

	/**
	 * Validate the start/end dates are not populated on the quote<br>
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	private void validateStartAndEndDate() {
		// _D0DQ4LL_1_START_DT, _D0DQ4LL_1_END_DT
		assertElementDisplayedFalse("_D0DQ4LL_1_START_DT");
		assertElementDisplayedFalse("_D0DQ4LL_1_END_DT");
	}

	/**
	 * Validate it is assigned an appliance ID<br>
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	private void validateApplianceID() {
		// D0DVPLL-50535599
		assertTextPresentTrue(APP_ID_REGEX);
	}

	/**
	 * Validate there is an mandatory field to add CRAD date<br>
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	private void validateCRADDate() {

		// custReqstdArrivlDay, custReqstdArrivlMonth, custReqstdArrivlYear
		assertElementPresentTrue("custReqstdArrivlDay");
		assertElementPresentTrue("custReqstdArrivlMonth");
		assertElementPresentTrue("custReqstdArrivlYear");
	}

	/**
	 * Validate there is a field asking if this is a new or POC system<br>
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	private void validateHasNewOrPOC() {

		// _D0DVPLL_2applncPriorPocNo, _D0DVPLL_2applncPriorPocYes
		assertElementPresentTrue("_D0DVPLL_2applncPriorPocNo");
		assertElementPresentTrue("_D0DVPLL_2applncPriorPocYes");
	}

	/**
	 * Validate there is a field asking if this is a POC conversion - choose No<br>
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	private void validateChooseNewAndNo() {

		// _D0DVPLL_2applncPriorPocNo, _D0DVPLL_2applncPocIndNo
		assertElementSelectedTrue("_D0DVPLL_2applncPriorPocNo");
		assertElementSelectedTrue("_D0DVPLL_2applncPocIndNo");
	}

	/**
	 * Try to submit the quote without first setting a CRAD date<br>
	 * Validate you can not submit the quote<br>
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	private void validateSubmitWithoutCRADDate() {
		currentQuotePage.selectReqstdArrivlDate(-2);
		
		// submit quote
		submitQuote(currentQuotePage);
		assertTextPresentTrue("Please enter a valid customer requested arrival date");
	}

	/**
	 * Assign a CRAD date in the past<br>
	 * Validate you can't submit the quote<br>
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	private void validateSubmitPastCRADDate() {

		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DAY_OF_MONTH, -1);
		int day = ca.get(Calendar.DAY_OF_MONTH);
		int month = ca.get(Calendar.MONTH) + 1;
		int year = ca.get(Calendar.YEAR);
		
		String strDay = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
		String strMonth = day < 10 ? "0" + String.valueOf(month) : String.valueOf(month);

		currentQuotePage.selectedOptionByValue("custReqstdArrivlDay",
				strDay, driver);
		currentQuotePage.selectedOptionByValue("custReqstdArrivlMonth",
				strMonth, driver);
		currentQuotePage.selectedOptionByValue("custReqstdArrivlYear",
				String.valueOf(year), driver);
		
		// submit quote
		submitQuote(currentQuotePage);
		assertTextPresentTrue("Please enter a valid customer requested arrival date");
	}

	/**
	 * Assign a future CRAD date<br>
	 * Validate the quote can be submitted<br>
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	private void validateSubmitFutureCRADDate() {

		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DAY_OF_MONTH, +1);
		int day = ca.get(Calendar.DAY_OF_MONTH);
		int month = ca.get(Calendar.MONTH) + 1;
		int year = ca.get(Calendar.YEAR);
		
		String strDay = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
		String strMonth = day < 10 ? "0" + String.valueOf(month) : String.valueOf(month);

		currentQuotePage.selectedOptionByValue("custReqstdArrivlDay",
				strDay, driver);
		currentQuotePage.selectedOptionByValue("custReqstdArrivlMonth",
				strMonth, driver);
		currentQuotePage.selectedOptionByValue("custReqstdArrivlYear",
				String.valueOf(year), driver);
		
		// submit quote
		submitQuote(currentQuotePage);
		assertTextPresentFalse("Please enter a valid customer requested arrival date");
	}

	/*
	 * Validate the CRAD date is populated on the quote in SAP Validate the
	 * appliance ID is populated on the quote in SAP Validate the POC and Prior
	 * POC indicators are not checked Validate MTM/Serial # is not populated on
	 * the quote Validate the start/end dates are not populated on the quote
	 */

	/**
	 * Validate MTM/Serial # is not populated on the quote<br>
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	private void validateMTMAndSerialUnpopulated() {

		// _D0DVPLL_2applncMachineType, _D0DVPLL_2applncMachineModel,
		// _D0DVPLL_2applncSerialNumber
		assertElementDisplayedFalse("_D0DVPLL_2applncMachineType");
		assertElementDisplayedFalse("_D0DVPLL_2applncMachineModel");
		assertElementDisplayedFalse("_D0DVPLL_2applncSerialNumber");
	}

	/*
	 * View the submitted quote in PGS Validate the CRAD date displays at the
	 * header Validate the line item is on hold Validate no MTM/Serial # display
	 * Validate the POC conversion question displays as No.
	 */

	/**
	 * View the submitted quote in PGS<br>
	 * Validate the CRAD date displays at the header<br>
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	private void validateCRADDateAtHeader() {

	}

	/**
	 * Validate no MTM/Serial # display
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	private void validateNoMTMAndSerialDisplay() {
		validateMTMAndSerialUnpopulated();
	}

	/**
	 * Validate the POC conversion question displays as No.<br>
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 */
	private void validatePOCDisplaysNo() {

	}
	
	private void validatesubmitQuote() {
		currentQuotePage.selectReqstdArrivlDate(30);
		
		// submit quote
		submitQuote(currentQuotePage);
	}
	


	////////////////////////////////need move them to BaseTestCase//////////////////////////////
	

	
	////////////////////////////////need move them to BaseTestCase//////////////////////////////
	
	/**
	 * 
	 * @author suchuang
	 * @date Dec 14, 2012
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		PGSNoRelatedPartsTest test = new PGSNoRelatedPartsTest();
		test.setUp();
		test.run();
		
		
//		Calendar ca = Calendar.getInstance();
//		ca.add(Calendar.DAY_OF_MONTH, +1);
//		int day = ca.get(Calendar.DAY_OF_MONTH);
//		int month = ca.get(Calendar.MONTH) + 1;
//		int year = ca.get(Calendar.YEAR);
		
		// D0DVPLL-90612506
//		String str = "D0DVPLL-90612506";
//		String regex = "D0DVPLL-\\d{8}";
//		Pattern pattern = Pattern.compile(regex);
//		Matcher matcher = pattern.matcher(str); 
//		System.out.println(matcher.matches());
		
	}

}
