package com.ibm.dsw.automation.testcase.saas;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.PGSQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;
import com.ibm.dsw.automation.testcase.provision.SQOUpdtProvsnBySubmitingQuote;
/**
 * task link:https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/452563
 * refine the script  and ensure it covers below points:
com.ibm.dsw.automation.testcase.saas.PGSSaas104and106Test

case a-----for 10.4 requirement:
1.create a draft quote; 
2.add customer 
3.add saas parts
4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote 
validation points:

Modify service dates?                     radio button works well;when the service date question (default is "no"),no option list of 'If desired select service start or end date' display;
Estimated provisioning days               the input text works well;
If desired select service start or end date the option works well,if wrong input,the err msg comes out(TODO);After recaculating,the selected date could display the same as you selected
the term field                            After recaculating,the selected date could display the correct value as you selected(TODO)
5.Edit the sales inf tab and special bid necesary,submit the quote
6.search the submitted quote and check the part and pricing tab again,the mentioned point could display right



case b-----bid iteration:
 We should hide the radio button section in bid iteration mode.
could refine the common method to flow layer from bid iteration prepare(399309)



reference:
Notes://D01DBL35/8525721300181EEE/477C010BD75EC87C85256A2D006A582E/1DCAB860B2B5047585257AD0006CB1EA
 * 
 * @date Jan 7, 2013
 */
public class PGSSaas104and106Test extends BaseTestCase {

	public static WebdriverLogger loggerContxt = WebdriverLogger
	.getLogger(SQOUpdtProvsnBySubmitingQuote.class.getName());

	@Test(description = "Web automation script for Saas 10.4 and 10.6")
	public void assemblyBizFlow(){
		// 10.4 requirement
		test104();
	}

	public void test104(){
		
		// 1. create a draft quote;
		PGSQuoteCommonFlow quoteFlow = getPGSCommonFlow();
		PGSHomePage pgsHomePage = quoteFlow.loginPgs(LOGON_IN_PGS_VIA_PGS_WEBAUTH);
		loggerContxt.info("Login PGS finished.....");

		PGSMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(pgsHomePage);
		loggerContxt.info("Create new sales quote,Draft quote header display, " +
				"Draft quote common action buttons display finished.....");
		
		// 2. add customer
		quoteFlow.createCustomerAndPartner(currentQuotePage);
		loggerContxt.info("add customer finished.....");
	  
		// 3.add saas part
		PartsAndPricingTabPage	ppTab = quoteFlow.addSaasParts(currentQuotePage);
		loggerContxt.info("add Saas parts finished.....");
		
		//4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote 
		ppTab = quoteFlow.validateModifyServiceDate(ppTab);
		loggerContxt.info("validate ModifyServiceDate finished.....");
		
		
		//5. Edit the sales inf tab and special bid necesary,submit the quote
		quoteFlow.setSalesInfo(ppTab);
		
		quoteFlow.setApproval(currentQuotePage);
		//currentQuotePage=quoteFlow.processSpecialBidTab(salesTab);
		quoteFlow.submitQuote(currentQuotePage);
		loggerContxt.info("Edit the sales inf tab,submit the quote finished.....");

		//TODO
		//6.search the submitted quote and check the part and pricing tab again,the mentioned point could display right
		//PGSStatusSalesQuote sbmdCPTabl=quoteFlow.findQuoteByNum(currentQuotePage);
		//quoteFlow.checkModifiedServiceDateInPPTabAfterSubmission(sbmdCPTabl);
		loggerContxt.info("search the submitted quote and check the part and pricing tab again,the mentioned point could display right finished.....");
		loggerContxt.info("have a check a the CP/SALES/PP/APPROVAL Tab finished.....");
	}

	public static void main(String[] args) throws Exception {

		PGSSaas104and106Test test = new PGSSaas104and106Test();
		test.setUp();
		test.assemblyBizFlow();
		test.teardown();
	}
}
