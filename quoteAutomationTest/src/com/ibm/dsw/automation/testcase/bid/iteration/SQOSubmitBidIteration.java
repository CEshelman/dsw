package com.ibm.dsw.automation.testcase.bid.iteration;

import java.util.List;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.pageobject.sqo.SQOApproveQueuePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOJumpPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitSQSpecialBidTabPage;

/**
 * Write test case for xrule (4 Scenarios)---bid iteration
 * https://igartc03.swg.usma.ibm.com/jazz/web/projects/CVT%20DSW#action=com.ibm.team.workitem.viewWorkItem&id=399309
 *************************************************************************
	comment:(john cover script for step 1-6)
	1    Log into SQO and create a sales quote.
	     example:Quote Type: Passport Advantage/Passport Advantage Express;Country/region:United States.
	2    add a customers and partners.
	     steps and example:click on the link 'Find an existing customer',then input number "0015334" on the Site or customer number,and select the customer.
	3    Browse for a SaaS offering and select one.
	     step: click on the link 'Browse Software as a Service' in the parts and pricing tab,click on the links 'Lotus Software' -> 'LotusLive' -> 'LotusLive' -> 'IBM SmartCloud for Social Business    Configure this service '.
	4    Add a mix of SaaS parts, set a term duration and billing frequencies
	     parts of example: term 18 months, part numbers -> D0NR7LL,Quantity/Include -> 10
	5    Add a discount to the quote, fill in any required information and submit the quote.
	     example:Quote expiration date -> 2012.12.31 on the top section,discount -> 35 in the parts and pricing ;Enter a brief title -> bid iteration, Opportunity number -> Customer initiated/Teleweb (exemption code 70) and Business organization -> Sales - IBM.com in the sales information ;Justification Summary -> just in the special bid tab
	6    select the approvers.
	     example: level 0 and level 2: DSW Web ID *FUNCTIONAL-ID* 5(dswweb5@us.ibm.com),level 4: DSW *FUNCTIONAL-ID* FVE1(dswfve1@us.ibm.com) .
	7    Log into SQO as the approver(S) and fully approved the quote.
	    steps:click on the link 'Approval queue',and approve the quote.
	8    Log back into SQO as the submitter and pull up the quote.
	9    clink on the lick 'Click this button to create a bid iteration.' in the customers and partners.
	10   Confirm the "Bid Iteration" label appears in the right hand corner.
	11   Go to the parts and pricing tab and select the Edit Configuration button.
	12    Add some more SaaS parts and Add configuration to the Bid.
	      example: part number -> D0NR5LL,Quantity/Include -> 1
	13    Submit the bid iteration
	14    Ensure pricing approvers lower then or equal to level 1 appear
	15    Select appropriate approvers
	      example: DSW Web ID *FUNCTIONAL-ID* 5(dswweb5@us.ibm.com)
	16    Submit the bid iteration successfully.     
 *************************************************************************    
 * @author Lin Chen
 * @date Dec 21, 2012
 * 
 * @author suchuang
 * @date Jan 8, 2013
 */
public class SQOSubmitBidIteration extends BaseTestCase {

	/**
	 * loggerContxt
	 */
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOSubmitBidIteration.class.getName());
	
	/**
	 * 
	 */
	private String submittedQuoteNum;

	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 */
	protected void approveSpecialBidQuote(List<String> lstApprover) {
		String url = getProperty("fvt.sqo_jump_url");
		for (String approver : lstApprover) {
			approveQuote(approver);
			reset(url);
		}
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 * @param userRole
	 */
	protected void approveQuote(String userRole) {
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

		String quoteNum = getSubmittedQuoteNum();
		SQOSubmitSQSpecialBidTabPage quoteDetails = null;
		if (null != quoteNum) {
			loggerContxt.info("query quote page");
			quoteNum = quoteNum.trim();
			quoteDetails = approveQueue.findQuoteByNum(quoteNum);
		}
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
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 * @param role
	 * @return
	 */
	protected SQOHomePage login(String role) {
		
		// clear all the cookies
		deleteAllCookies();
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

	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 * @return
	 */
	public String getSubmittedQuoteNum() {
		return submittedQuoteNum;
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 * @param submittedQuoteNum
	 */
	public void setSubmittedQuoteNum(String submittedQuoteNum) {
		this.submittedQuoteNum = submittedQuoteNum;
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 * @throws Exception
	 */
	@Test(description = "approve special bid quote")
	public void validateApproveSpecialBidQuote() throws Exception {
		SQOCreateQuote4BidIteration createQuote4BidTest = new SQOCreateQuote4BidIteration();
		createQuote4BidTest.setUp();
		createQuote4BidTest.run();
		
		setSubmittedQuoteNum(createQuote4BidTest.getSubmittedQuoteNum());
		approveSpecialBidQuote(createQuote4BidTest.getLstApprover());
	}
	
	/**
	 * 
	 * @author suchuang
	 * @throws Exception 
	 * @date Jan 7, 2013
	 */
	public void run() throws Exception {
		validateApproveSpecialBidQuote();
		loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
		quitWebdriver();
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SQOSubmitBidIteration submitBidTest = new SQOSubmitBidIteration();
		submitBidTest.setUp();
		submitBidTest.run();
	}
}
