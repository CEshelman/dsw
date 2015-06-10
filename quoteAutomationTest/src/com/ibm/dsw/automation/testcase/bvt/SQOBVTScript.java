package com.ibm.dsw.automation.testcase.bvt;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuoteCPTabPage;

/**
 * 
 * https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.
 * workitem.WorkItem/445575
 * 
 * create ascript for BVT, should cover below functions: 1.create a draft
 * quote; 2.add customer and partners; 3.remove partners; 4.add software
 * parts(through part num,part description); 5.add parts through browse 6.remove
 * parts 7.add/remove saas part 8.retrieve quote; 9.edit sales inf/approval and
 * sumbit quote 10.find by submitted quote number and have a check a of the
 * CP/SALES/PP/APPROVAL Tab
 * 
 * @date Feb 4, 2013
 */
public class SQOBVTScript extends BaseTestCase {

	public static WebdriverLogger loggerContxt = WebdriverLogger
			.getLogger(SQOBVTScript.class.getName());

	@Test(description = "create ascript for BVT")
	public void assemblyBizFlow() {

		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();

		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");

		SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
		loggerContxt.info("Create new sales quote,Draft quote header display, " +
				"Draft quote common action buttons display finished.....");

		// 2.add customer and partners;// 3.remove partners;
		quoteFlow.processCustPartnerTab(currentQuotePage);
		loggerContxt
				.info("Customer search;Partner search (including reseller and distributor);Customer information display; Partner information display finished.....");
		
	
		// 4.add software parts(through part num,part description);
		SQOPartsAndPricingTabPage ppTab = quoteFlow
		.addSoftwareParts(currentQuotePage);
		loggerContxt.info("Find and add software part (Find by ID and Find by description) finished.....");
		
		// 5.add parts through browse
		ppTab = quoteFlow.browsePart(ppTab);
		loggerContxt.info("add parts through Browser part and remove the part finished.....");
		
		// 6.remove parts
		ppTab = quoteFlow.removeSoftWarePart(ppTab);
		loggerContxt.info("add parts through Browser part and remove the part finished.....");
		
		// 7.add/remove saas part
		ppTab = quoteFlow.addSaasPartToQuote(ppTab);
		loggerContxt.info("add Saas parts finished.....");
		
		// remove saas part
		ppTab = quoteFlow.removeSaasPart(ppTab);
		
		// 9.edit sales inf/approval and sumbit quote
		SQOSalesInfoTabPage salesTab=	quoteFlow.editSalesInfoTab(ppTab);
		
		// 8.retrieve quote;
		currentQuotePage=quoteFlow.retrieveSavedSalesQuotePage(salesTab);
		loggerContxt.info("Retrieve saved quote finished.....");

	
		
		currentQuotePage=quoteFlow.processSpecialBidTab(salesTab);

		// 10.find by submitted quote number and 
		quoteFlow.runSubmitQuote(currentQuotePage);

		SQOSelectedQuoteCPTabPage sbmdCPTab = quoteFlow.findQuoteByNum(currentQuotePage);
		loggerContxt.info("status searches finished.....");
		
		quoteFlow.checkSubmittedQuote(sbmdCPTab);
		loggerContxt.info("have a check a the CP/SALES/PP/APPROVAL Tab finished.....");

	}

	public static void main(String[] args) throws Exception {
		SQOBVTScript test = new SQOBVTScript();
		test.setUp();
		test.assemblyBizFlow();
	}
}
