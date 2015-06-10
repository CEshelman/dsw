package com.ibm.dsw.automation.testcase.monthsw;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMonthySwBaseConfiguratorPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuoteCPTabPage;

/**
 * 
 *https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/643000
 * 
 *DSW 14.2 Monthly software SQO changes - wedriver for basic configurator validation 
 *Create draft sales PA/PAE quote
 *Add customer
 *Add monthly sw parts
 *Click 'Return to quote' link, will display monthly sw basic configurator(new mode)
 *make sure that UI display is correct, compare to the mock up
 *after make any changes in the basic page(quantity, term, billing, ramp up, etc.), 
 *
 *part:
 *D12ZNLL
 *D12ZMLL
 *D12ZRLL
 * 
 * @date Jan 12, 2014
 */
@SuppressWarnings("unused")
public class SQOMonthySWConfiguratorValScript extends BaseTestCase {

	public static WebdriverLogger loggerContxt = WebdriverLogger
			.getLogger(SQOMonthySWConfiguratorValScript.class.getName());

	@Test(description = "DSW 14.2 Monthly software SQO changes - wedriver for basic configurator validation ")
	public void assemblyBizFlow() {

		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();

		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf(),BaseTestCase.LOGON_IN_SQO_VIA_SQO_JUMP_PAGE);
		loggerContxt.info("Login SQO finished.....");

		SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
		loggerContxt.info("Create new sales quote,Draft quote header display, " +
				"Draft quote common action buttons display finished.....");

		// 2.add customer and partners;// 3.remove partners;
		quoteFlow.processCustPartnerTab(currentQuotePage);
		loggerContxt
				.info("Customer search;Partner search (including reseller and distributor);Customer information display; Partner information display finished.....");
		
	
		// 4.add software parts(through part num,part description);
		SQOMonthySwBaseConfiguratorPage monthlySwBaseConfig= quoteFlow
		.addMonthySoftwareParts(currentQuotePage);
		loggerContxt.info("Find and add software part (Find by ID and Find by description) finished.....");
		
		
		
		SQOPartsAndPricingTabPage ppTab= quoteFlow
			.doMonthySwBasicConfigValidaiton(monthlySwBaseConfig);
		
		loggerContxt.info(" basic configurator validation finished.....");
		monthlySwBaseConfig=quoteFlow.checkMonthlyInfoOnPPTab(ppTab);
		loggerContxt.info("check Part pricing tab finished.....");
		ppTab=quoteFlow.editMonthySwBasicConfig(monthlySwBaseConfig);
		loggerContxt.info(" Edit term, quantity, and billing frequencies finished.....");
		
		loggerContxt.info("DSW 14.2 Monthly software SQO changes - wedriver for basic configurator validation finished.....");

	}

	public static void main(String[] args) throws Exception {
		SQOMonthySWConfiguratorValScript test = new SQOMonthySWConfiguratorValScript();
		test.setUp();
		test.assemblyBizFlow();
	}
}
