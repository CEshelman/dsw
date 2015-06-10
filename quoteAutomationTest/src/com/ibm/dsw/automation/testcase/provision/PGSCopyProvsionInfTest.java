package com.ibm.dsw.automation.testcase.provision;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.PGSQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.pgs.DisplayStatusSearchReslutPage;
import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSStatusSalesQuote;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.pgs.StatusSearchTabPage;

/**
 * Updating provisioning id through submitting quote
 **************************************************************************
	copy provisioning infomation
	1.scenario-1 copy provisioning info in submitted quote
	  operate : click "Create a copy" link in submitted quote
	  check:
	     the provisioning id is the same as original,
	     
	   
	###bid iteration prepare,please refer to workitem 399309
	2.scenario-2 copy provisioning info in order quote
	  operate : click "click this button to create a bid iteration" in reporting
	  check:
	    1. the provisioning id is the same as original,
 **************************************************************************
 * 
 * @author suchuang
 * @date Jan 4, 2013
 */
public class PGSCopyProvsionInfTest extends BaseTestCase {

	/**
	 * loggerContxt
	 */
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(PGSCopyProvsionInfTest.class.getSimpleName());
	
	
	/**
	 * PGS current quote page objects
	 */
	private MyCurrentQuotePage currentQuotePage = null;
	
	/**
	 * 
	 * @author suchuang
	 * @throws Exception 
	 * @date Jan 4, 2013
	 */
	public void run() throws Exception {
		validatePIdInSubmitted();
		loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
	}
	
	/**
	 * 
	 * @author suchuang
	 * @throws Exception 
	 * @date Jan 5, 2013
	 */
	@Test(description = "copy provisioning info in submitted quote")
	public void validatePIdInSubmitted() throws Exception {
		String quoteNum = getQuoteNum();
		reset();
		
		// Login PGS
		//String pageUrl = getProperty(".pgs_jump_url");
		String pageUrl=getLogonInf().getPgsUrl();
		this.driver.get(pageUrl);
	/*	PGSJumpPage page = new PGSJumpPage(this.driver, new Long(20));
		page.login(getProperty(".customerNum"), getProperty(".bpNum"));*/
		PGSQuoteCommonFlow quoteFlow = getPGSCommonFlow();
		PGSHomePage pgsHomePage = quoteFlow.loginPgs(LOGON_IN_PGS_VIA_PGS_WEBAUTH);
		loggerContxt.info("login PGS");
		
	/*	boolean hasCurrentQuote = selenium.isTextPresent("Quote reference");
		loggerContxt.info("having current quote....." + hasCurrentQuote);*/
		
		// Create a PGS quote
		//currentQuotePage = new PGSMyCurrentQuotePage(driver);
		
		//
		loggerContxt.info("Click on the 'Status' link in the left nav");
		StatusSearchTabPage statusSearchPage = pgsHomePage.gotoStatus();
		
		loggerContxt
				.info("Check all checkboxes of the following sections Find the following quote types "
						+ "Find quotes and special bids with the following overall statuses"
						+ " Select 'Date submitted (descending)' in the Sort by dropdown "
						+ "Check the Make above selections my default checkbox"
						+ " Select the Find all matching quotes Press on the 'Find quotes' button");
		DisplayStatusSearchReslutPage statusResultPage = statusSearchPage
				.goDispQuoteByQuoteNum(quoteNum);
		
		PGSStatusSalesQuote statusSalesQuote = statusResultPage.goDispQuoteReslt(quoteNum);
		PartsAndPricingTabPage papTab = statusSalesQuote.goToPartsAndPricingTab();
		
		String pIdPath = "//form/table/tbody/tr[3]/td/table/tbody/tr[3]/th/div/strong";
		String originalId = papTab.getOriginalProvisioningId();
		currentQuotePage=papTab.createACopyLink();
		
		/*statusSearchPage = currentQuotePage.gotoStatus();
		loggerContxt
				.info("Check all checkboxes of the following sections Find the following quote types "
						+ "Find quotes and special bids with the following overall statuses"
						+ " Select 'Date submitted (descending)' in the Sort by dropdown "
						+ "Check the Make above selections my default checkbox"
						+ " Select the Find all matching quotes Press on the 'Find quotes' button");
		statusResultPage = statusSearchPage
				.goDispQuoteByQuoteNum(quoteNum);
		
		statusSalesQuote = statusResultPage.goDispQuoteReslt(quoteNum);*/
		papTab = currentQuotePage.goToPartsAndPricingTab();
		
	//	pIdPath = "//form/table/tbody/tr[3]/td/table/tbody/tr[3]/th/div/strong";
		//	Service brand: IBM Applicatn Integratn Middleware
  		
		//	&nbsp;-&nbsp;&nbsp;Provisioning ID: PV000000000130
	

		String copiedId = papTab.getOriginalProvisioningId();
		assertObjectEquals(copiedId, originalId);
	}
	
	protected String getQuoteNum() throws Exception {
		PGSUpdtProvsnBySubmtingQuoteTest test = new PGSUpdtProvsnBySubmtingQuoteTest();
		test.setUp();
		return test.createQuote();
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 5, 2013
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		PGSCopyProvsionInfTest test = new PGSCopyProvsionInfTest();
		test.setUp();
		test.run();
	}

}
