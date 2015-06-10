package com.ibm.dsw.automation.testcase.base;


import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.PGSPropertiesBean;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.PGSQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSStatusSalesQuote;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;

import com.ibm.dsw.automation.pageobject.pgs.ImportSalesQuotePage;

/**
 * 
 * https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/464312
 * 
 *	create a pgs script for fvt/uat, should cover below functions:
 *	  1.create a draft quote;
 *	  2.open the Browse software as a service link;
 *	  3.from PartsAndPricingTabPage to pgsHome page;
 *	  4.open the Import a sales quote spreadsheet link
 *	  5.from importSalesQuotePage to pgsHome page
 *	  6.Find all matching quotes
 *	  7.from statusTab to pgsHome page
 *	  8.Find by Quote Num, 
 *
 * @author LiYu
 * @date April 10, 2013
 */

public class PGSStatusSearchAndRenewalQuoteTest extends BaseTestCase {

	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(PGSStatusSearchAndRenewalQuoteTest.class.getName());
	
//	private SQOMyCurrentQuotePage currentQuotePage = null;
//	private SQOHomePage sqoHomePage = null;
//	private final SQOPartsAndPricingTabPage partsAndPricingTab =null;
//	private String quoteNum="";


	public static void main(String[] args) throws Exception {
		PGSStatusSearchAndRenewalQuoteTest test = new PGSStatusSearchAndRenewalQuoteTest();
		test.setUp();
		test.assemblyBizFlow();
	}
	
	@Test(description = "create a Status Search for PGS")
	public void assemblyBizFlow() {

		 // 1.create a draft quote;
 		PGSQuoteCommonFlow quoteFlow = getPGSCommonFlow();

 		PGSHomePage pgsHomePage = quoteFlow.loginPgs(LOGON_IN_PGS_VIA_PGS_WEBAUTH);
		loggerContxt.info("Login PGS finished.....");

		MyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(pgsHomePage);
		loggerContxt
				.info("Create new sales quote finished.....");
		
        // 2.open the Browse software as a service link
		PartsAndPricingTabPage ppTab = currentQuotePage.goToPartsAndPricingTab();
	
		ppTab = quoteFlow.browseSAAS(ppTab);
		loggerContxt.info("Browse software as a service finished.....");
		
		//3 from PartsAndPricingTabPage to pgsHome page
		pgsHomePage = quoteFlow.gotoHomePage(ppTab);
		
		//4,open the Import a sales quote spreadsheet link
		ImportSalesQuotePage importSalesQuotePage = quoteFlow.openImportSalesQuote(pgsHomePage);
		loggerContxt.info("Import a sales quote spreadsheet link finished.....");
		
		//5 from importSalesQuotePage to pgsHome page
		pgsHomePage = quoteFlow.gotoHomePageFromImport(importSalesQuotePage);

		//6,Find all matching quotes
		loggerContxt.info("Find all matching quotes start.....");
		PGSStatusSalesQuote statusTab = quoteFlow.findQuoteByMatchingQuotes(pgsHomePage);
		loggerContxt.info("Find all matching quotes finished.....");
		
		//7 from statusTab to pgsHome page
		pgsHomePage = quoteFlow.gotoHomePageFromStatus(statusTab);
		
		//8,Find by Quote Num,
		loggerContxt.info("Find by Quote Num start.....");
		PGSStatusSalesQuote statusByQuoteNum = quoteFlow.findByQuoteNum(pgsHomePage);
		loggerContxt.info("Search quote finished.....");
		
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


}
