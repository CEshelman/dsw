package com.ibm.dsw.automation.testcase.bvt;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.PGSPropertiesBean;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.PGSQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSStatusSalesQuote;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.pgs.SalesInfoTabPage;

/**
 * 
 * https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/445574
 * 
	create a pgs script for BVT, should cover below functions:
	  1.create a draft quote;
	  2.add customer and partners;
	  3.remove partners;
	  4.add software parts(through part num,part description);
	  5.add parts through browse
	  6.remove parts
	  7.add/remove saas part
	  8.retrieve quote;
	  9.edit sales inf/approval and sumbit quote
	  10.find by submitted quote number and have a check a the CP/SALES/PP/APPROVAL Tab 
 * 
 * @author suchuang
 * @date Feb 4, 2013
 */
public class PGSBVTScript extends BaseTestCase {
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(PGSBVTScript.class.getName());
	
	@Test(description = "quote demo")
	public void assemblyBizFlow(){
		
        // 1.create a draft quote;
 		PGSQuoteCommonFlow quoteFlow = getPGSCommonFlow();

 		PGSHomePage pgsHomePage = quoteFlow.loginPgs(LOGON_IN_PGS_VIA_PGS_WEBAUTH);
		loggerContxt.info("Login PGS finished.....");
		
		MyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(pgsHomePage);
		loggerContxt
				.info("Create new sales quote finished.....");
		
        // 2.add customer and partners;
		quoteFlow.createCustomerAndPartner(currentQuotePage);
		loggerContxt.info("Create Customer and partner finished.....");
		
        // 3.remove partners;
//		quoteFlow.removeReseller(currentQuotePage);
		loggerContxt.info("Remove Reseller finished.....");
		
        // 4.add software parts(through part num,part description);
		PartsAndPricingTabPage ppTab = currentQuotePage.goToPartsAndPricingTab();
		ppTab = quoteFlow.findSoftwareParts(currentQuotePage);
		loggerContxt.info("Find Parts finished.....");
		
		ppTab = quoteFlow.removeFindParts(ppTab);
		loggerContxt.info("Remove Parts which added by finding finished.....");
		
        // 5.add parts through browse
		ppTab = quoteFlow.browseSoftwareParts(currentQuotePage);
		loggerContxt.info("Browse Parts finished.....");
		
        // 6.remove parts
		ppTab = quoteFlow.removeBrowseParts(ppTab);
		loggerContxt.info("Remove Parts which added by browsing finished.....");
		
        // 7.add/remove saas part
		// add saas part
		ppTab = quoteFlow.addSaasParts(ppTab);
		loggerContxt.info("Add Saas Parts finished.....");
		// remove saas part
		
        // 9.edit sales inf/approval and sumbit quote
		SalesInfoTabPage salesTab = quoteFlow.setSalesInfo(ppTab);
		loggerContxt.info("Set Sales infor finished.....");
		
        // 8.retrieve quote;
		currentQuotePage = quoteFlow.retrieveSavedSalesQuotePage(currentQuotePage);
		loggerContxt.info("Retrieve saved quote finished.....");
		
		quoteFlow.setApproval(currentQuotePage);
		loggerContxt.info("Set Approval finished.....");
		
        // 10.find by submitted quote number and have a check a the CP/SALES/PP/APPROVAL Tab 
		quoteFlow.submitQuote(currentQuotePage);
		
		PGSStatusSalesQuote sbmdCPTab = quoteFlow.findQuoteByNum(currentQuotePage);
		loggerContxt.info("Search quote finished.....");
		
		quoteFlow.checkSubmittedQuote(sbmdCPTab);
		loggerContxt.info("have a check a the CP/SALES/PP/APPROVAL Tab finished.....");
		
	}

	@Override
	protected PGSQuoteCommonFlow getPGSCommonFlow() {
		PGSQuoteCommonFlow quoteFlow = new PGSQuoteCommonFlow();
		quoteFlow.setDriver(driver);
		quoteFlow.setSelenium(selenium);
		quoteFlow.setPropBean((PGSPropertiesBean) propBean);
		quoteFlow.setLogonInf(getLogonInf());
		
		return quoteFlow;
	}

	public static void main(String[] args) throws Exception {
		PGSBVTScript test = new PGSBVTScript();
		test.setUp();
		test.assemblyBizFlow();
	}
}
