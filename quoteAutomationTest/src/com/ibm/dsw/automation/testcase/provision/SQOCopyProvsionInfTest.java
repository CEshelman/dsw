package com.ibm.dsw.automation.testcase.provision;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuoteCPTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuotePPTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOStatusSearchTabPage;
import com.ibm.dsw.automation.testcase.bid.iteration.SQOSubmitBidIteration;

/**
 * Updating provisioning id through submitting quote
 **************************************************************************
	copy provisioning infomation
	1.scenario-1 copy provisioning info in submitted quote
	  operate : click "Create a copy" link in submitted quote
	  check:
	     the provisioning id is the same as original,
	     
	   
	###bid iteration prepare,please refer to workitem 399309
	2.scenario-2 copy provisioning info in order quote
	  operate : click "click this button to create a bid iteration" in reporting
	  check:
	    1. the provisioning id is the same as original,
 **************************************************************************
 * 
 * @author suchuang
 * @date Jan 4, 2013
 */
public class SQOCopyProvsionInfTest extends BaseTestCase {

	/**
	 * loggerContxt
	 */
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOCopyProvsionInfTest.class.getName());
	
	
	/**
	 * PGS current quote page objects
	 */
	private MyCurrentQuotePage currentQuotePage = null;
	
	/**
	 * 
	 */
	private String submittedQuoteNum;

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
	
/*	*//**
	 * 
	 * @author suchuang
	 * @throws Exception 
	 * @date Jan 4, 2013
	 *//*
	public void run() throws Exception {
		
		// 1.scenario-1 copy provisioning info in submitted quote
		validatePIdInSubmitted();
		
		
		// 2.scenario-2 copy provisioning info in order quote
		validatePIdInOrder();
		
		loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
		quitWebdriver();
	}*/
	
	/**
	 * 
	 * @author suchuang
	 * @throws Exception 
	 * @date Jan 5, 2013
	 */
	//@Test(description = "copy provisioning info in submitted quote")
/*	public void validatePIdInSubmitted() throws Exception {
		
		// submit the bid iteration and get the submitted quote number
		if (StringUtils.isBlank(getSubmittedQuoteNum())) {
			SQOSubmitBidIteration submittedBid = submitBidIteration();
			setSubmittedQuoteNum(submittedBid.getSubmittedQuoteNum());
		}

		// clear all the cookies
		deleteAllCookies();
		
		// Login SQO
        SQOHomePage homePage = login();
        
        SQOStatusSearchTabPage statusSearch =  homePage.gotoStatus();
        SQODisplayStatusSearchReslutPage statusSearchResult = statusSearch.goDispQuoteReslt(getSubmittedQuoteNum());
        SQOSelectedQuoteCPTabPage quoteCPTabPage = statusSearchResult.goDispQuoteReslt();
        SQOSelectedQuotePPTabPage papTab = quoteCPTabPage.goToPPTab();
        String pIdPath = "//form/table/tbody/tr[3]/td/table/tbody/tr[3]/th/div/strong";
		String originalId = papTab.getOriginalProvisioningId(pIdPath);
		papTab.createACopyLinkClick();
		currentQuotePage = new SQOMyCurrentQuotePage(driver);
		PartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
		assertTextPresentTrue("Provisioning ID:");
		pIdPath = "//form/table/tbody/tr[4]/td/table/tbody/tr[5]/th/div/strong";
		String copiedId = partsAndPricingTab.getProvisioningId(pIdPath);
		assertObjectEquals(copiedId, originalId);
		
		loggerContxt.info("@Test validatePIdInSubmitted has passed!");
		reset(getProperty("fvt.sqo_jump_url"));
	}*/
	/**
	 * 
	 * @author Marwa Arafa
	 * @date Feb 19, 2013
	 * This method is written to replace validatePIdInSubmitted() method to refine provisioning script with flow layer
	 */
	protected void validateProvIdInSubmitted(){
		
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		
		// clear all the cookies
		deleteAllCookies();
		SQOStatusSearchTabPage statusSearch ;
		SQOMyCurrentQuotePage currQuotePage = null;
		SQOSelectedQuoteCPTabPage quoteCPTabPage = null;
		//This line is just for unit testing
		//setSubmittedQuoteNum("0003045802");
		if (StringUtils.isBlank(getSubmittedQuoteNum())) {
			// Login SQO
			SQOHomePage homePage = quoteFlow.loginSqo(getLogonInf());
			loggerContxt.info("go to sqo home page finished.....");
			//create draft quote
			SQOMyCurrentQuotePage draftQuotePage = quoteFlow.createDraftQuote(homePage);
			loggerContxt.info("create quote page finished.....");
			SQOMyCurrentQuotePage custAddedDraftQuotePage = quoteFlow.createCustomer(draftQuotePage);
			setSubmittedQuoteNum(custAddedDraftQuotePage.getQuoteNum());
			loggerContxt.info("Quote number ....." + submittedQuoteNum);
			SQOPartsAndPricingTabPage ppTab = quoteFlow.addSoftwareParts(custAddedDraftQuotePage);
			ppTab = quoteFlow.addSaasPartToQuote(ppTab);
		    SQOSalesInfoTabPage salesInfoPage = quoteFlow.editSalesInfoTabWithoutSavingQuote(ppTab);
		    currQuotePage = quoteFlow.processSpecialBidTab(salesInfoPage);
		    loggerContxt.info("submit current quote page.....");
		    quoteFlow.runSubmitQuote(currQuotePage);
		    /////////////////////////Quote has been submitted
		    quoteCPTabPage = quoteFlow.findQuoteByNum(currQuotePage, getSubmittedQuoteNum());

		}
		else {
			SQOHomePage homePage = quoteFlow.loginSqo(getLogonInf());
			loggerContxt.info("go to sqo home page finished.....");
			quoteCPTabPage = quoteFlow.findQuoteByNum(homePage, getSubmittedQuoteNum());			
		}
				
		 SQOSelectedQuotePPTabPage papTab= quoteFlow.goToPPTabSubmittedQuote(quoteCPTabPage);
		 String pIdPath = "//form/table/tbody/tr[3]/td/table/tbody/tr[3]/th/div/strong";
		 String originalId = quoteFlow.getProvId(papTab, pIdPath);
		 loggerContxt.info("Original provisioning ID is ....."+originalId);
		 quoteFlow.createACopyLinkClick(papTab);
		 currentQuotePage = new SQOMyCurrentQuotePage(driver);
		 loggerContxt.info("Open current quote page .....");
		 PartsAndPricingTabPage partsAndPricingTab = quoteFlow.goToPPTabSubmittedQuote((SQOMyCurrentQuotePage)currentQuotePage);
		 pIdPath = "//form/table/tbody/tr[4]/td/table/tbody/tr[5]/th/div/strong";
		 String copiedId = quoteFlow.getProvId(partsAndPricingTab, pIdPath);
		 quoteFlow.assertObjectEquals(copiedId, originalId, partsAndPricingTab);
		 
		 loggerContxt.info("@Test validateProvIdInSubmitted has passed!");
		 
		 //reset(getProperty("fvt.sqo_jump_url"));	
	}
	
/*	*//**
	 * 
	 * @author suchuang
	 * @throws Exception 
	 * @date Jan 5, 2013
	 *//*
	//@Test(description = "copy provisioning info in order quote")
	public void validatePIdInOrder() throws Exception {
		
		// submit the bid iteration and get the submitted quote number
		if (StringUtils.isBlank(getSubmittedQuoteNum())) {
			SQOSubmitBidIteration submittedBid = submitBidIteration();
			setSubmittedQuoteNum(submittedBid.getSubmittedQuoteNum());
		}

		// clear all the cookies
		deleteAllCookies();
		
		// Login SQO
        SQOHomePage homePage = login();
        currentQuotePage = homePage.gotoCurrenQuotePage();
        
        SQOStatusSearchTabPage statusSearch =  homePage.gotoStatus();
        SQODisplayStatusSearchReslutPage statusSearchResult = statusSearch.goDispQuoteReslt(getSubmittedQuoteNum());
        SQOSelectedQuoteCPTabPage cpTab = statusSearchResult.goDispQuoteReslt();
        SQOSelectedQuotePPTabPage ppTab = cpTab.goToPPTab();
        
        String pIdPath = "//form/table/tbody/tr[3]/td/table/tbody/tr[3]/th/div/strong";
		String originalId = ppTab.getOriginalProvisioningId(pIdPath);
		ppTab = ((SQOMyCurrentQuotePage)currentQuotePage).createBidIterationLinkClick();
		pIdPath = "//form/table/tbody/tr[4]/td/table/tbody/tr[5]/th/div/strong";
		String copiedId = ppTab.getOriginalProvisioningId(pIdPath);
		assertObjectEquals(copiedId, originalId);
		
		loggerContxt.info("@Test validatePIdInOrder has passed!");
	//	reset();
		
		
		
	}*/
	/**
	 * 
	 * @author Marwa Arafa
	 * @date Feb 19, 2013
	 * This method is written to replace validatePIdInOrder() method to refine provisioning script with flow layer
	 */
	
	protected void validateProvIdInOrder(){
			
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		// clear all the cookies
		deleteAllCookies();
		
		SQOStatusSearchTabPage statusSearch ;
		SQOMyCurrentQuotePage currQuotePage = null;
		SQOSelectedQuoteCPTabPage quoteCPTabPage = null;
		//This line is just for unit testing
		setSubmittedQuoteNum("0003045765");
		if (StringUtils.isBlank(getSubmittedQuoteNum())) {
			// Login SQO
			SQOHomePage homePage = quoteFlow.loginSqo(getLogonInf());
			loggerContxt.info("go to sqo home page finished.....");
			//create draft quote
			SQOMyCurrentQuotePage draftQuotePage = quoteFlow.createDraftQuote(homePage);
			loggerContxt.info("create quote page finished.....");
			SQOMyCurrentQuotePage custAddedDraftQuotePage = quoteFlow.createCustomer(draftQuotePage);
			setSubmittedQuoteNum(custAddedDraftQuotePage.getQuoteNum());
			loggerContxt.info("Quote number ....." + submittedQuoteNum);
			SQOPartsAndPricingTabPage ppTab = quoteFlow.addSoftwareParts(custAddedDraftQuotePage);
			ppTab = quoteFlow.addSaasPartToQuote(ppTab);
		    SQOSalesInfoTabPage salesInfoPage = quoteFlow.editSalesInfoTabWithoutSavingQuote(ppTab);
		    currQuotePage = quoteFlow.processSpecialBidTab(salesInfoPage);
		    loggerContxt.info("submit current quote page.....");
		    quoteFlow.runSubmitQuote(currQuotePage);
		    /////////////////////////Quote has been submitted
		    quoteCPTabPage = quoteFlow.findQuoteByNum(currQuotePage, getSubmittedQuoteNum());

		}
		else {
			SQOHomePage homePage = quoteFlow.loginSqo(getLogonInf());
			loggerContxt.info("go to sqo home page finished.....");
			quoteCPTabPage = quoteFlow.findQuoteByNum(homePage, getSubmittedQuoteNum());	
						
		}
		
		if (null != quoteCPTabPage) {
			SQOSelectedQuotePPTabPage papTab = quoteFlow.goToPPTabSubmittedQuote(quoteCPTabPage);
			String pIdPath = "//form/table/tbody/tr[3]/td/table/tbody/tr[3]/th/div/strong";
			String originalId = quoteFlow.getProvId(papTab, pIdPath);
			loggerContxt.info("Original provisioning ID is ....." + originalId);
			papTab = quoteFlow.createBidIterationLinkClick(papTab);
			pIdPath = "//form/table/tbody/tr[4]/td/table/tbody/tr[5]/th/div/strong";
	        String copiedId = quoteFlow.getProvId(papTab, pIdPath);
			quoteFlow.assertObjectEquals(copiedId, originalId, papTab);
		}

		loggerContxt.info("@Test validateProvIdInOrder has passed!");
		reset();
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 8, 2013
	 * @return
	 * @throws Exception
	 */
	protected SQOSubmitBidIteration submitBidIteration() throws Exception {
		SQOSubmitBidIteration submitBidTest = new SQOSubmitBidIteration();
		submitBidTest.setUp();
		submitBidTest.run();
		return submitBidTest;
	}
	
/*	*//**
	 * 
	 * @author suchuang
	 * @date Jan 5, 2013
	 * @return
	 *//*
	protected SQOHomePage login() {
		SQOHomePage sqoHomePage = null;
		loggerContxt.info("Enter: login method!");
		
		reset(prop.getProperty(env + ".sqo_jump_url"));
		// login system by jump.html
		SQOJumpPage jumpPage = new SQOJumpPage(this.driver);
		String countruCode = getProperty(".countryCode");
		String userName = getProperty(".userName");
		String accessLevel = getProperty(".accessLevel1");
		jumpPage.loginIn(countruCode, userName, accessLevel);
		sqoHomePage = new SQOHomePage(driver);
		
		loggerContxt.info("Exit: login method!");
		return sqoHomePage;
	}*/
	
		
	/**
	 * 
	 * @author Marwa Arafa
	 * @date Feb 13, 2013
	 */
	@Test(description = "Copy a quote")
	protected void assemblyBizFlow(){
		//validateProvIdInSubmitted();
		validateProvIdInOrder();	
		
		loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
       // quitWebdriver();
	}
    
    /**
     * 
     * @author suchuang
     * @date Jan 5, 2013
     * @param args
     * @throws Exception
     */
	public static void main(String[] args) throws Exception {
		SQOCopyProvsionInfTest test = new SQOCopyProvsionInfTest();
		test.setUp();
//		test.run();
		test.assemblyBizFlow();
		loggerContxt.info("Test case finished successfully......");
	}
}
