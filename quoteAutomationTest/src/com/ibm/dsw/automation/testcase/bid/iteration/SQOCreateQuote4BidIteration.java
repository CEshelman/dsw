package com.ibm.dsw.automation.testcase.bid.iteration;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOBrowseSoftwareAsServiceTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOCreateQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayCustListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOOSearchCustPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSpecialBidTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitCurrDraftQuotePage;


/**
 * https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/399309
 * 1 Log into SQO and create a sales quote.
 example:Quote Type: Passport Advantage/Passport Advantage Express;Country/region:United States.
	2 add a customers and partners.
	 steps and example:click on the link 'Find an existing customer',then input number "0015334" on the Site or customer number,and select the customer.
	3 Browse for a SaaS offering and select one.
	 step: click on the link 'Browse Software as a Service' in the parts and pricing tab,click on the links 'Lotus Software' -> 'LotusLive' -> 'LotusLive' -> 'IBM SmartCloud for Social Business Configure this service '.
	4 Add a mix of SaaS parts, set a term duration and billing frequencies
	 parts of example: term 18 months, part numbers -> D0NR7LL,Quantity/Include -> 10
	5 Add a discount to the quote, fill in any required information and submit the quote.
	 example:Quote expiration date -> 2012.12.31 on the top section,discount -> 35 in the parts and pricing ;Enter a brief title -> bid iteration, Opportunity number -> Customer initiated/Teleweb (exemption code 70) and Business organization -> Sales - IBM.com in the sales information ;Justification Summary -> just in the special bid tab
	6 select the approvers.
	 example: level 0 and level 2: DSW Web ID *FUNCTIONAL-ID* 5(dswweb5@us.ibm.com),level 4: DSW *FUNCTIONAL-ID* FVE1(dswfve1@us.ibm.com) .
############################################################################

 * @author suchuang
 * @date Jan 7, 2013
 */
public class SQOCreateQuote4BidIteration extends BaseTestCase {

	/**
	 * loggerContxt
	 */
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOCreateQuote4BidIteration.class.getName());
	
	
	/**
	 * 
	 */
	private SQOMyCurrentQuotePage currentQuotePage = null;
	
	/**
	 * 
	 */
	private String submittedQuoteNum;

	/**
	 * 
	 */
	private List<String> lstApprover;
	
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 */
	protected void runQuoteEdit() {

		// Find an existing customer
		SQOOSearchCustPage searchCustPage = currentQuotePage.findCustbyClick();
		loggerContxt.info("go to search customer page.....");

		// display customer
		SQODisplayCustListPage displayCustListPage = searchCustPage
				.displaySQOCustomerListBySiteNum(getProperty(".siteNum"));
		loggerContxt.info("display customer page.....");

		// select customer and back current quote page
		currentQuotePage = displayCustListPage.selectCustomer();
		loggerContxt.info("select customer and back current quote page.....");
		
		
		String draftQuoteNumPath = "//form/div/div/div[2]/div/div[5]/div/div/div[2]";
		String quoteNum = getDriver().findElement(By.xpath(draftQuoteNumPath)).getText();
		setSubmittedQuoteNum(quoteNum);
		
		//go to the part and pricing tab
		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage
				.goToPartsAndPricingTab();
		
		//go to the browse software as a service page
		partsAndPricingTab.browseSoftwareAsAServiceLinkClick();
		loggerContxt.info("go to browse software as a service page");
		
		addSaasPartToQuote();
		
		//Quote Expiration date
		partsAndPricingTab.selectExpirationDate(30);
		loggerContxt.info("select the expiration date");
		
		// Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SQOSalesInfoTabPage salesTab = partsAndPricingTab.gotoSalesInfoTab();
		loggerContxt.info("Go to SalesInf Tab end.....");

		salesTab.enterSalesInf(getProperty(".briefTitle"),
				getProperty(".quoteDesc"),
				getProperty(".busOrgCode"));

		// go to Special Bid Tab
		loggerContxt.info("go to Special Bid Tab.....");
		SQOSpecialBidTabPage spcialBidTab = salesTab.goToSpecialBidTabClick();
		
		//Justification Summary
		spcialBidTab.enterSpcialBidInf(getProperty(".justificationSummary"));
	}
    
    /**
     * 
     * @author suchuang
     * @date Jan 7, 2013
     */
	protected void runSubmitQuote() {
		loggerContxt.info("submit current quote page.....");
		SQOSubmitCurrDraftQuotePage currDraftQuotePage = currentQuotePage
				.submitCurrentDraftQuote();
		
		setLstApprover(currDraftQuotePage.submitDraftQuote());
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 * @param page
	 * @param configureId
	 * @param components
	 */
	protected void configureService(SQOBrowseSoftwareAsServiceTabPage page, String configureId, String[][] components) {
		loggerContxt.info("before execute js.....");
        ((JavascriptExecutor) getDriver()).executeScript("configureService('" + configureId + "')");
        loggerContxt.info("find popup frame");
        getDriver().findElement(By.xpath("/html/body/div[5]/div[2]/div/div[2]/div/div[2]/div/div[2]/p/input[2]")).click();
        WebElement webElement = page.waitForElementById("dijit_DialogUnderlay_0");
        if (webElement != null) {
        	loggerContxt.info("add parts");
            page.addSaasPart(components);
            loggerContxt.info("add SAAS part finished.....");
            page.waitForElementLoading(25000L);
        }
    }
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 * @param treeNodeIds
	 * @param components
	 */
	protected void browseAndAddSaasPart(String[] treeNodeIds, String[][] components) {

        loggerContxt.info("before click the node tree.....");
        SQOBrowseSoftwareAsServiceTabPage browseSoftwareAsServiceTabPage = new SQOBrowseSoftwareAsServiceTabPage(getDriver());
        browseSoftwareAsServiceTabPage.clickTreeNodesById(treeNodeIds);
        loggerContxt.info("after click the node tree.....");
        configureService(browseSoftwareAsServiceTabPage, getProperty(".SaasPartId"), components);
        loggerContxt.info("finished config the service.....");
    }
	
	/**
	 * Add SAAS Part to quote
	 * @author suchuang
	 * @date Jan 7, 2013
	 */
    protected void addSaasPartToQuote() {
        /**
         * get the text of link: Browse Software as a Service
         */
    	loggerContxt.info("get node tree path and components list from prop...");
    	String[] pathaArray = getProperty(".saasNodePathArray").split("/");
        String[][] components = { { getProperty(".saasPartQuantityXPath"), getProperty(".saasPartQuantity"), } };
        // open saas part and configure it.
        browseAndAddSaasPart(pathaArray, components);
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
	 * @date Jan 23, 2013
	 * @return
	 */
	public List<String> getLstApprover() {
		return lstApprover;
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 23, 2013
	 * @param lstApprover
	 */
	public void setLstApprover(List<String> lstApprover) {
		this.lstApprover = lstApprover;
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 */
	@Test(description = "Create Quote for Bid Iteration")
	public void validateCreateQuote4BidIteration() {
		SQOHomePage sqoHomePage = null;
		
	
		
		if ("fvt".equals(env)) {
		/*	SQOJumpPage jumpPage = new SQOJumpPage(getDriver());
			jumpPage.loginIn("US", "zhoujunz@cn.ibm.com", "1");
			
			
			sqoHomePage = new SQOHomePage(getDriver());*/
			
			SQOQuoteCommonFlow quoteFlow = getCommonFlow();
			sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		}

		loggerContxt.info("go to sqo home page finished.....");
	   	SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();

		// Create a Sales quote
	   	currentQuotePage = cq.createQuote();
		loggerContxt.info("create quote page finished.....");
		
		// create quote
		runQuoteEdit();
		
		// submit quote
		runSubmitQuote();
		
		assertTextPresentTrue("Your special bid quote has been submitted for approval");
		loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
	}
	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 */
	public void run() {
		validateCreateQuote4BidIteration();
		loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
		//quitWebdriver();
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SQOCreateQuote4BidIteration test = new SQOCreateQuote4BidIteration();
		test.setUp();
		test.run();
	}
}
