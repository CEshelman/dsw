package com.ibm.dsw.automation.testcase.base;


import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuoteCPTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOStatusSearchTabPage;

/**
 * 
 * https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/463279
 * 
 *	create a sqo script for fvt/uat, should cover below functions:
 *	  1.login the SQO ;
 *	  2.open the Status link;
 *	  3.choose all items ,Click IBMer assigned Tab, Search all.
 *	  4.The quotes that match your selection criteria are listed.
 *	  5.Click the View details link,
 *	  6.return Status
 *	  7.Click find by Customer,and Search,
 *    8.The quotes that match your selection criteria are listed.
 *	  9.Click the View details link,
 **	  10.return Status
 **	  11.Click find by Partner,and Search,
 **	  12.The quotes that match your selection criteria are listed.
 **	  13.Click the View details link,
 **	  14.return Status
 **	  15.Click find by Country/region,and Search,
 **	  16.The quotes that match your selection criteria are listed.
 **	  17.Click the View details link,
 **	  18.return Status
 **	  19.Click find by Approval attributes,and Search,
 **	  20.The quotes that match your selection criteria are listed.
 **	  21.Click the View details link,
 **	  22.return Status
 **	  23.Click find by Siebel Number,and Search,
 **	  24.The quotes that match your selection criteria are listed.
 **	  25.Click the View details link,
 **	  26.return Status
 **	  27.Click find by Order Number,and Search,
 **	  28.The quotes that match your selection criteria are listed.
 **	  29.Click the View details link,
 **	  30.return Status
 **	  31.Click find by Quote Number,and Search,
 **	  32.The quotes that match your selection criteria are listed.
 **	  33.Click the View details link,
 *	  34.return Status
 *
 * @author LiYu
 * @date April 10, 2013
 */

public class SQOStatusSearchTest extends BaseTestCase {

	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOStatusSearchTest.class.getName());
	
//	private SQOMyCurrentQuotePage currentQuotePage = null;
//	private SQOHomePage sqoHomePage = null;
//	private final SQOPartsAndPricingTabPage partsAndPricingTab =null;
//	private String quoteNum="";


	public static void main(String[] args) throws Exception {
		SQOStatusSearchTest test = new SQOStatusSearchTest();
		test.setUp();
		test.assemblyBizFlow();
	}
	
	@Test(description = "create a Status Search for SQO")
	public void assemblyBizFlow() {

		//1, create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();

		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");

		
		//2, find by IBMer assigned
		loggerContxt.info("status searches by IBMer assigned start.....");
		SQOSelectedQuoteCPTabPage sbmdCPTab = quoteFlow.findQuoteByIBMer(sqoHomePage);
		loggerContxt.info("status searches by IBMer assigned finished.....");
		
		//3, go to Status;
		SQOStatusSearchTabPage statusTab = quoteFlow.gotoStatus(sbmdCPTab);
		
		//4, find by Customer for site
		loggerContxt.info("status searches by Customer for site start.....");
		sbmdCPTab = quoteFlow.findQuoteByCustomerForSite(statusTab);
		loggerContxt.info("status searches by Customer for site finished.....");
		
		//5, go to Status;
		statusTab = quoteFlow.gotoStatus(sbmdCPTab);
		
		//6, find by Customer for name
		loggerContxt.info("status searches by Customer for name start.....");
		sbmdCPTab = quoteFlow.findQuoteByCustomerForName(statusTab);
		loggerContxt.info("status searches by Customer for name finished.....");

		//7, go to Status;
		statusTab = quoteFlow.gotoStatus(sbmdCPTab);
		
		//8, find by Partner for site
		loggerContxt.info("status searches by Partner for site start.....");
		sbmdCPTab = quoteFlow.findQuoteByPartnerForSite(statusTab);
		loggerContxt.info("status searches by Partner for site finished.....");
		
		//9, go to Status;
		statusTab = quoteFlow.gotoStatus(sbmdCPTab);
		
		//10, find by Partner for name
		loggerContxt.info("status searches by Partner for name start.....");
		sbmdCPTab = quoteFlow.findQuoteByPartnerForName(statusTab);
		loggerContxt.info("status searches by Partner for name finished.....");
		
		//11, go to Status;
		statusTab = quoteFlow.gotoStatus(sbmdCPTab);
		
		//12, find by Country/region
		loggerContxt.info("status searches by Country/region start.....");
		sbmdCPTab = quoteFlow.findQuoteByCountry(statusTab);
		loggerContxt.info("status searches by Country/region finished.....");
		
		//13, go to Status;
		statusTab = quoteFlow.gotoStatus(sbmdCPTab);
		
		//14, find by Approval attributes
		loggerContxt.info("status searches by Approval attributes start.....");
		sbmdCPTab = quoteFlow.findQuoteByApprovalAttri(statusTab);
		loggerContxt.info("status searches by Approval attributes finished.....");
		
		//15, go to Status;
		statusTab = quoteFlow.gotoStatus(sbmdCPTab);
		
		//16, find by Siebel Number
		loggerContxt.info("status searches by Siebel Number start.....");
		sbmdCPTab = quoteFlow.findQuoteBySiebelNum(statusTab);
		loggerContxt.info("status searches by Siebel Number finished.....");
		
		//17, go to Status;
		statusTab = quoteFlow.gotoStatus(sbmdCPTab);
		
		//18, find by Order Number
		loggerContxt.info("status searches by Order Number start.....");
		sbmdCPTab = quoteFlow.findQuoteByOrderNum(statusTab);
		loggerContxt.info("status searches by Order Number finished.....");
		
		//19, go to Status;
		statusTab = quoteFlow.gotoStatus(sbmdCPTab);
		
		//20, find by Quote Number
		loggerContxt.info("status searches by Quote Number start.....");
		sbmdCPTab = quoteFlow.findQuoteByQuoteNum(statusTab);
		loggerContxt.info("status searches by Quote Number finished.....");
		
		//21, go to Status;
		statusTab = quoteFlow.gotoStatus(sbmdCPTab);
		
	}


}
