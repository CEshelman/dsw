package com.ibm.dsw.automation.testcase.base;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.SQOPropertiesBean;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuoteCPTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuotePPTabPage;


/**
 * 
 * https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/463333
 * 
	create a Status search and Renewal quote script for SQO, should cover below functions:
	package:com.ibm.dsw.automation.testcase.base
	scriptName:
	
	
	//for the Approval queue and statusTracker
	1.login SQO's fvt (dswweb5-Approver *FUNCTIONAL-ID*   AND Approver)
	2.Click Approval queue tracker link
	3.Click Approval queue
	4.click Software Quote and Order link
	5.Click Status tracker link
	6.Click Settings link
	
	
	//for the submittedQuoteStatusTab and ExecutiveSummary
	7.login SQO's fvt (Lion + Submitter)
	8.Click Status link
	9.Select all items, Make above selections my default
	10.click Quote number link
	11.input <0002164473> for Order number
	12.Click Find quote button
	13.click View details Link
	14.Click Executive Summary Link
	
	// for the CompareQuotes
	15.Click Status link
	16.Select all items, Make above selections my default
	17.click Quote number link
	18.input <0003048222> for Order number
	19.Click Find quote button
	20.click View status details Link
	21.click Compare quotes
	
	// for the ImportSalesQuote
	22.Click Create a sales quote link
	23.Click Import a sales quote spreadsheet link
	
	// for the renewalQuoteStatusTab
	24.Click Renewal quotes link
	25.Select <OPEN> for the Renewal status and select <Editor> for What is your role on the quote
	26.click Submit button
	27.click 26285484 link
	28.click Edit the master renewal quote link( next popup page)
	29.click Status link
	
	// for the browseSAAS
	
	30.Click create a sales quote link
	31.select <Passport Advantage/PassportAdvantage Express> for Quote type and select <United States> for Country/region
	32.click Continue button
	33.Click Parts and pricing link
	34.click Browse Software as a Service link.
 * 
 * @author suchuang
 * @date Apr 17, 2013
 */
public class SQOStatusSearchAndRenewalQuoteTest extends BaseTestCase {
	
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(PGSStatusSearchAndRenewalQuoteTest.class.getName());
	
	
	@Test(description = "create a Status Search for SQO")
	public void assemblyBizFlow() {

		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		SQOPropertiesBean prop = quoteFlow.getPropBean();
		
		//for the Approval queue and statusTracker
//		1.login SQO's fvt (dswweb5-Approver *FUNCTIONAL-ID*   AND Approver)
//		2.Click Approval queue tracker link
//		3.Click Approval queue
//		4.click Software Quote and Order link
//		5.Click Status tracker link
//		6.Click Settings link
		getLogonInf().setSqoLogonUser(prop.getApproverUser());
		getLogonInf().setAccessLevel(prop.getAccessLevelApprover());
		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf(), LOGON_IN_SQO_VIA_SQO_JUMP_PAGE_BY_URL);
		loggerContxt.info("Login SQO finished.....");

		sqoHomePage = quoteFlow.checkApprovalQueueTracker(sqoHomePage);
		loggerContxt.info("Check Approval queue tracker finished.....");
		sqoHomePage = quoteFlow.checkStatusTracker(sqoHomePage);
		loggerContxt.info("Check Status tracker finished.....");
		loggerContxt.info("for the Approval queue and statusTracker finished.....");
		
		//for the submittedQuoteStatusTab and ExecutiveSummary
//		7.login SQO's fvt (Lion + Submitter)
//		8.Click Status link
//		9.Select all items, Make above selections my default
//		10.click Quote number link
//		11.input <0002164473> for Order number
//		12.Click Find quote button
//		13.click View details Link
//		14.Click Executive Summary Link
		SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
		prop.setDraftQuoteNum(prop.getExecutiveSummaryQuoteNum());
		SQOSelectedQuoteCPTabPage sbmdCPTab = quoteFlow.findQuoteByNum(currentQuotePage);
		currentQuotePage = quoteFlow.checkExecutiveSummaryTab(sbmdCPTab);
		loggerContxt.info("for the submittedQuoteStatusTab and ExecutiveSummary finished.....");
		
		// for the CompareQuotes
//		15.Click Status link
//		16.Select all items, Make above selections my default
//		17.click Quote number link
//		18.input <0003048222> for Order number
//		19.Click Find quote button
//		20.click View status details Link
//		21.click Compare quotes
		prop.setDraftQuoteNum(prop.getCompareQuoteNum());
		SQOSelectedQuoteCPTabPage quoteCPTabPage = quoteFlow.findAllQuote(currentQuotePage);
		SQOSelectedQuotePPTabPage papTab = quoteFlow.goToPPTabSubmittedQuote(quoteCPTabPage);
		currentQuotePage = quoteFlow.createACopyLinkClick(papTab);
		quoteFlow.compareQuote(papTab);
		loggerContxt.info("for the CompareQuotes finished.....");
		
		// for the ImportSalesQuote
//		22.Click Create a sales quote link
//		23.Click Import a sales quote spreadsheet link
		sqoHomePage = quoteFlow.gotoHomePage(currentQuotePage);
		quoteFlow.openImportSalesQuote(sqoHomePage);
		loggerContxt.info("for the ImportSalesQuote finished.....");
		
		// for the renewalQuoteStatusTab
//		24.Click Renewal quotes link
//		25.Select <OPEN> for the Renewal status and select <Editor> for What is your role on the quote
//		26.click Submit button
//		27.click 26285484 link
//		28.click Edit the master renewal quote link( next popup page)
//		29.click Status link
		getLogonInf().setSqoLogonUser(prop.getSubmitterUser());
		getLogonInf().setAccessLevel(prop.getAccessLevelSubmitter());
		sqoHomePage = quoteFlow.loginSqo(getLogonInf(), LOGON_IN_SQO_VIA_SQO_JUMP_PAGE_BY_URL);
		loggerContxt.info("Login SQO finished.....");
		sqoHomePage = quoteFlow.checkRenewalQuoteStatus(sqoHomePage, "26285484");
		loggerContxt.info("for the renewalQuoteStatusTab finished.....");
		
		// for the browseSAAS
//		30.Click create a sales quote link
//		31.select <Passport Advantage/PassportAdvantage Express> for Quote type and select <United States> for Country/region
//		32.click Continue button
//		33.Click Parts and pricing link
//		34.click Browse Software as a Service link.
		currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
		SQOPartsAndPricingTabPage ppTab = currentQuotePage.goToPartsAndPricingTab();
		ppTab.browseSoftwareAsAServiceLinkClick();
		ppTab.close();
		loggerContxt.info("for the browseSAAS finished.....");
		
		loggerContxt.info("@Test SQOStatusSearchAndRenewalQuoteTest has passed!");
	}

	public static void main(String[] args) throws Exception {
		SQOStatusSearchAndRenewalQuoteTest test = new SQOStatusSearchAndRenewalQuoteTest();
		test.setUp();
		test.assemblyBizFlow();
	}

}
