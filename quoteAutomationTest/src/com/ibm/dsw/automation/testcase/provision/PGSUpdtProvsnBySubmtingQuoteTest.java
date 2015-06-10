package com.ibm.dsw.automation.testcase.provision;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.flow.PGSQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.pgs.BrowseSoftwareAsServiceTabPage;
import com.ibm.dsw.automation.pageobject.pgs.DisplayCustListPage;
import com.ibm.dsw.automation.pageobject.pgs.DisplayResellerListPage;
import com.ibm.dsw.automation.pageobject.pgs.DisplayStatusSearchReslutPage;
import com.ibm.dsw.automation.pageobject.pgs.FindExistingCustomerPage;
import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSApprovalTabPage;
import com.ibm.dsw.automation.pageobject.pgs.PGSCreateQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSStatusSalesQuote;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.pgs.SalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.pgs.SelectAResellerPage;
import com.ibm.dsw.automation.pageobject.pgs.ServiceConfigurePage;
import com.ibm.dsw.automation.pageobject.pgs.StatusSearchTabPage;

/**
 * This is a test class for PGS validate privisioning id
 * https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/420091
 *************************************************************************
	Here is the test step&data:
	Test steps for PGS submitted quote
	
	1.log in to pgs using 0007003910, put it to the 'Enter BP's site num' field
	
	2.create quote according below inf:
	     country:USA
	    cusomter:0007011428
	    reseller:0007186058
	     distributor:0007084774
	    
	    add Saas parts:
	
	brand:IBM Coremetrics  -  Provisioning ID: PV000000000049
	configuration:
	IBM WebSphere Cast Iron Express  -  Configuration ID: 5725F7220121212
	IBM WebSphere Cast Iron Live Standard Edition  -  Configuration ID: 5725F7020121212
	IBM Digital Analytics  -  Configuration ID: 5725E3520121212
	
	3.click 'Edit provisioning form' link
	
	4.go back to PP tab, make sure that there is a new privisioning id displayed
	
	5.enter info accordingly and submit the quote.
	
	6.after submitting,search the submitted quote through status,copy the quote and check whether the provision id is the same as the provision before copying.
	
	reference LInk:
	https://w3-connections.ibm.com/wikis/home?lang=zh-cn#!/wiki/We36a2acb76c7_4bce_b45c_7c104dbff999/page/the%20test%20steps%20for%20Updating%20provisioning%20id%20through%20submitting%20quote
 *************************************************************************
 * @author suchuang
 * @date Dec 19, 2012
 */
public class PGSUpdtProvsnBySubmtingQuoteTest extends BaseTestCase {

	/**
	 * loggerContxt
	 */
	private final Logger loggerContxt = Logger.getLogger(this.getClass().getName());
	
	/**
	 * PGS current quote page objects
	 */
	private MyCurrentQuotePage currentQuotePage = null;
	
	/**
	 * 
	 * @author suchuang
	 * @date Dec 19, 2012
	 */
	public void run() {
		
		// validate provisioning Id
		validateQuote();
		
		loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Dec 26, 2012
	 */
	@Test(description = "Updating provisioning id")
	public void validateQuote() {
		// Login PGS
/*		String pageUrl = getProperty(".pgs_jump_url");
		reset(pageUrl);
		PGSJumpPage page = new PGSJumpPage(this.driver, new Long(20));
		PGSHomePage pgsHomePage = page.login(getProperty(".customerNum"), getProperty(".bpNum"));*/
		
		PGSQuoteCommonFlow quoteFlow = getPGSCommonFlow();

 		PGSHomePage pgsHomePage = quoteFlow.loginPgs(LOGON_IN_PGS_VIA_PGS_WEBAUTH);
		loggerContxt.info("login PGS");
		
		boolean hasCurrentQuote = selenium.isTextPresent("Quote reference");
		loggerContxt.info("having current quote....." + hasCurrentQuote);
		
		// Create a PGS quote
		PGSCreateQuotePage cq = pgsHomePage.gotoCreateQuote();
		currentQuotePage = cq.createQuote(hasCurrentQuote);
		assertTextPresentTrue(getProperty(".currentQuoteTitle"));
		
		// Create Customer
		createCustomer(currentQuotePage);
		
		// browser software IBM WebSphere Cast Iron Express  -  Configuration ID: 5725F7220121212
		loggerContxt.info("browser software IBM WebSphere Cast Iron Express");
		String provisioningId = browserAndConfigureService(currentQuotePage, getProperty(".partPath"));

		// set salse info
		setSalesInfo(currentQuotePage);
		
		// set approval
		setApproval(currentQuotePage);
		
		currentQuotePage.submitQuote();
		
		String quoteNum = getWebReference();
		
		loggerContxt.info("Click on the 'Status' link in the left nav");
		StatusSearchTabPage statusSearchPage = currentQuotePage.gotoStatus();
		
		loggerContxt
				.info("Check all checkboxes of the following sections Find the following quote types "
						+ "Find quotes and special bids with the following overall statuses"
						+ " Select 'Date submitted (descending)' in the Sort by dropdown "
						+ "Check the Make above selections my default checkbox"
						+ " Select the Find all matching quotes Press on the 'Find quotes' button");
		DisplayStatusSearchReslutPage statusResultPage = statusSearchPage
				.goDispQuoteByQuoteNum(quoteNum);
		
		PGSStatusSalesQuote statusSalesQuote = statusResultPage.goDispQuoteReslt(quoteNum);
		statusSalesQuote.goToPartsAndPricingTab();
		
		assertTextPresentTrue(provisioningId);
	}
	
	public void waitForElementLoading(Long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 9, 2013
	 * @return
	 */
	public String createQuote() {
		// Login PGS
		//String pageUrl = getProperty(".pgs_jump_url");
/*		String pageUrl=getLogonInf().getPgsUrl();
		setBaseUrl(pageUrl);
		waitForElementLoading((long) 10000);
		this.driver.get(pageUrl);
		PGSJumpPage page = new PGSJumpPage(this.driver, new Long(20));
		PGSHomePage pgsHomePage = page.login(getProperty(".customerNum"), getProperty(".bpNum"));
		*/
		PGSQuoteCommonFlow quoteFlow = getPGSCommonFlow();
		PGSHomePage pgsHomePage = quoteFlow.loginPgs(LOGON_IN_PGS_VIA_PGS_WEBAUTH);
		loggerContxt.info("login PGS");
		
		boolean hasCurrentQuote = selenium.isTextPresent("Quote reference");
		loggerContxt.info("having current quote....." + hasCurrentQuote);
		
		// Create a PGS quote
		PGSCreateQuotePage cq = pgsHomePage.gotoCreateQuote();
		currentQuotePage = cq.createQuote(hasCurrentQuote);
		assertTextPresentTrue(getProperty(".currentQuoteTitle"));
		
		// Create Customer
		createCustomer(currentQuotePage);
		
		// browser software IBM WebSphere Cast Iron Express  -  Configuration ID: 5725F7220121212
		loggerContxt.info("browser software IBM WebSphere Cast Iron Express");
		String provisioningId = browserAndConfigureService(currentQuotePage, getProperty(".partPath"));
		
		// set salse info
		setSalesInfo(currentQuotePage);
		
		// set approval
		setApproval(currentQuotePage);
		
		currentQuotePage.submitQuote();
		
		String quoteNum = getWebReference();
		
		return quoteNum;
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
	private void createCustomer(MyCurrentQuotePage currentQuotePage) {
		
	 	// Find an existing customer
		FindExistingCustomerPage searchCustPage = currentQuotePage.goToFindCustomerTab();
		loggerContxt.info("go to search customer page.....");

		// display customer
		DisplayCustListPage displayCustListPage = searchCustPage.displayCustomerListBySiteNum(getProperty(".siteNum"));
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
		
		// set Quote expiration date
		currentQuotePage.selectExpirationDate(30);
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 9, 2013
	 * @param partsAndPricingTab
	 * @return
	 */
	private String getProvisioningId(PartsAndPricingTabPage partsAndPricingTab) {
		String pIdPath = "//form/table/tbody/tr[4]/td/table/tbody/tr[4]/th/div/strong";
		return partsAndPricingTab.getProvisioningId(pIdPath);
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Dec 26, 2012
	 * @return
	 */
	private String getWebReference() {
		String path = "//html/body/div/div[2]/div/div/div[2]/div/div[2]/div/div/p[2]/span";
		return getElementText(path);
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 9, 2013
	 * @param currentQuotePage
	 * @return
	 */
	private String browserAndConfigureService(MyCurrentQuotePage currentQuotePage, String partPath) {
		PartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
		BrowseSoftwareAsServiceTabPage browseSoftwareAsServiceTabPage = partsAndPricingTab.browseSoftwareAsAServiceLinkClick();
		browseSoftwareAsServiceTabPage.selectAgreement();
		
		browseSoftwareAsServiceTabPage.clickAllImgs();
		ServiceConfigurePage scPage =browseSoftwareAsServiceTabPage.selectSaasPID(getProperty(".SaasPartId"));
		
		//ServiceConfigurePage scPage = browseSoftwareAsServiceTabPage.switchToServiceConfigurePage();
		scPage.configureService();
	
/*		browseSoftwareAsServiceTabPage.browserAndConfigureService(partPath);

		ServiceConfigurePage scPage = browseSoftwareAsServiceTabPage.switchToServiceConfigurePage();
		scPage.configureService();*/
//		partsAndPricingTab.editProvisioningFormLink();
		
		return getProvisioningId(partsAndPricingTab);
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
	
	//////////////////////////
	
	/**
	 * 
	 * @author suchuang
	 * @date Dec 19, 2012
	 * @param currentQuotePage
	 * @param ca
	 */
	public void setExpirationDate(MyCurrentQuotePage currentQuotePage, Calendar ca) {
		
		if (null == ca) {
			ca = Calendar.getInstance();
			ca.add(Calendar.DAY_OF_MONTH, +1);
		} 

		int day = ca.get(Calendar.DAY_OF_MONTH);
		int month = ca.get(Calendar.MONTH) + 1;
		int year = ca.get(Calendar.YEAR);
		
		String strDay = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
		String strMonth = day < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
		
		currentQuotePage.selectedOptionByValue("expirationDay",
				strDay, driver);
		currentQuotePage.selectedOptionByValue("expirationMonth",
				strMonth, driver);
		currentQuotePage.selectedOptionByValue("expirationYear",
				String.valueOf(year), driver);
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Dec 21, 2012
	 * @param currentQuotePage
	 * @param ca
	 */
	public void setCRADDate(MyCurrentQuotePage currentQuotePage, Calendar ca) {
		
		if (null == ca) {
			ca = Calendar.getInstance();
			ca.add(Calendar.DAY_OF_MONTH, +1);
		} 

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
	}
	
	/**
	 * 
	 */
	public static void main(String[] args) throws Exception {
		PGSUpdtProvsnBySubmtingQuoteTest test = new PGSUpdtProvsnBySubmtingQuoteTest();
		test.setUp();
		test.run();
	}
}
