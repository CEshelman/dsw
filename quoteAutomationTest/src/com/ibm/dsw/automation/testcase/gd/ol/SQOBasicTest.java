package com.ibm.dsw.automation.testcase.gd.ol;

import java.util.Date;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOOmittedLinePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOQuoteSearchIBMSelfPage;
import com.ibm.dsw.automation.pageobject.sqo.SQORQSearchReslutPage;
import com.ibm.dsw.automation.pageobject.sqo.SQORQSummaryTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSpecialBidTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitCurrDraftQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmittedDraftQuotePage;

public class SQOBasicTest extends BaseTestCase {


	@Override
	public void setUp(String...args) throws Exception {
		super.setUp(args);
		loginSQO();
	}

	@Test(description = "basic test")
	public void basicTest() {
		SQOQuoteSearchIBMSelfPage selfPage = sqoHomePage.gotoRenewalQuote();
		SQORQSearchReslutPage resultPage = selfPage
				.searchPARenewalQuotesByIBMer();

		SQORQSummaryTabPage summaryTabPage = resultPage.goDispQuoteReslt();
		
		SQOMyCurrentQuotePage currentQuotePage = summaryTabPage.createSpecialBidAgainstRenewalQuote(true);
		SQOPartsAndPricingTabPage ppPage = currentQuotePage.goToPartsAndPricingTab();
		
		
		
		ppPage = ppPage.deleteOnePart();
		
		SQOOmittedLinePage olPage = ppPage.viewOmittedLines();
		
		 //olPage.addOrViewOrUpdate();
		ppPage = olPage.returnToPartAndPriceTab();
		
		ppPage.selectExpirationDate(2, new Date() );
		ppPage.setGrowthPercentToAll(10);
		ppPage.recalculateQuotePartAndPriceTab();
		
		currentQuotePage = ppPage.gotoMyCurrentQuotePage();
		
		currentQuotePage.setFulfillmentSourcesToDirect();
		
		SQOSalesInfoTabPage salesPage = currentQuotePage.goToSalesInfoTab();
		
		salesPage.enterSalesInf("Test OL","Test OL");
		
		SQOSpecialBidTabPage sbPage = salesPage.goToSpecialBidTabClick();
		sbPage.enterSpcialBidInf("testing OL");
		
		currentQuotePage = sbPage.rtnToCPTab();
		SQOSubmitCurrDraftQuotePage subPage = currentQuotePage.submitCurrentDraftQuote();
		SQOSubmittedDraftQuotePage finalPage = subPage.submitWithFirstApprover();
		

	}

	public static void main(String[] args) throws Exception {
		SQOBasicTest test = new SQOBasicTest();
		test.setUp();
		test.basicTest();
	}

}
