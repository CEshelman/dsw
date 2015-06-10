package com.ibm.dsw.automation.testcase.mtp;

import java.io.File;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.PGSPropertiesBean;
import com.ibm.dsw.automation.flow.PGSQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;


/**
 * Script Name : PGSStandAppVadProTest<br>
 * Description :pGS Standard Application Validation Procedure <br>
 * Objective: Prerequisites:<br>
 * 
Login PGS	Ensure we can login PGS with BP userid/pwd
Draft quote	1. Create new sales quote
	2. Country drop down list is working per the country rule
	3. Draft quote header display
	4. Draft quote common action buttons display
Draft quote - Customer and partner tab	1. Customer search
	2. Partner search
	3. Customer information display
	4. Partner information display
	5. Create new customer
	6. Clear partner
Draft quote - Part and pricing tab	Browser and add Saas parts
	Edit PID
	Apply offer price
	Input discount
	Override price
	Line item sort
	Price information display
	Parts information display
Draft quote - Sales infor tab	1. Enter quote title
Draft quote - Approval tab	1. Ensure the page display well
	2. Upload an non-empty attachment, download it and remove it.
	3. Upload an empty attachment, ensure application blocks the operation with proper message displayed.
Draft quote - RTF Download	
Retrieve saved quote	1. Retrieve saved quote
SaaS Reporting	1. Ensure SaaS Reporting could be displayed well both for active and inactive tab
	2. Check the popups/Tooltips display well for copy/update configuration links.
	3. Download and print pages work well.
Status search	1. Find all matching quotes
	2. Find quote by number
Attachment upload/download	1. Upload an non-empty attachment, download it and remove it on Approval tab.
Email send	1. Open my current quote
	2. Email the draft quote to a mail box
	3. Check the temp mail box and ensure your mail is there.
Help doc	1. Check each help links

 * 
 * @since 2013-1-28
 */
public class PGSStandAppVadProTest extends BaseTestCase {

	
	@Test(description = "PGS Standard Application Validation Procedure")
	public void assemblyBizFlow() throws Exception{
		PGSQuoteCommonFlow quoteFlow = getPGSCommonFlow();
		
		/**
		 * PGS-VLDTN-1
		 * Login PGS
		 * 
		 * Ensure we can login PGS with BP userid/pwd
		 */
		PGSHomePage pgsHomePage = null;
		try{
			pgsHomePage = quoteFlow
					.loginPgs(LOGON_IN_PGS_VIA_SQO_WEBAUTH);
		}catch(Exception e){
			loggerContxt.fatal("Failed to Login PGS, please re-check your user id and password. Or some issue with PGS application.",e);
			throw new Exception(e);
		}
		// PGSHomePage pgsHomePage =
		// quoteFlow.loginPgs(LOGON_IN_PGS_VIA_PGS_WEBAUTH);
		loggerContxt.info("Login PGS finished.....");
		

		/**
		 * PGS-VLDTN-2
		 * Draft quote
		 * 
		 * 	1. Create new sales quote
			2. Country drop down list is working per the country rule
			3. Draft quote header display
			4. Draft quote common action buttons display
		 */
		MyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(pgsHomePage);
		loggerContxt
				.info("Create new sales quote finished.....");

		/**
		 * PGS-VLDTN-2.1
		 * Draft quote - Customer and partner tab
		 * 
		 * 	1. Customer search
			2. Partner search
			3. Customer information display
			4. Partner information display
			5. Create new customer
			6. Clear partner
		 */
		currentQuotePage=quoteFlow.createCustomerAndPartner(currentQuotePage);
		loggerContxt.info("Create Customer and partner finished.....");

		/**
		 * PGS-VLDTN-2.2
		 * Draft quote - Part and pricing tab
		 * 
		 * 	1. Browser and add Saas parts
			2. Edit PID
			3. Apply offer price
			4. Input discount
			5. Override price
			6. Line item sort
			7. Price information display
			8. Parts information display
		 */
		PartsAndPricingTabPage partsAndPricingTab = null;
		String partNogeArray = this.getProperty(propSub,
				"browser.part.node.array");
		partsAndPricingTab = quoteFlow.browseSoftwareParts(currentQuotePage,partNogeArray);
		loggerContxt.info("Browse Parts finished.....");
		
		partsAndPricingTab=quoteFlow.removeBrowseParts(partsAndPricingTab);
		loggerContxt.info("Remove Parts which added by browsing finished.....");
		
		partsAndPricingTab = quoteFlow.findSoftwareParts(currentQuotePage);
		loggerContxt.info("Find Parts finished.....");
		
		partsAndPricingTab=quoteFlow.removeFindParts(partsAndPricingTab);
		loggerContxt.info("Remove Parts which added by finding finished.....");
		
		// partsAndPricingTab=quoteFlow.addSaasParts(partsAndPricingTab);
		// loggerContxt.info("Add Saas Parts finished.....");
		
		/**
		 * PGS-VLDTN-2.3
		 * Draft quote - Sales infor tab
		 * 
		 * 	1. Enter quote title
		 */
		quoteFlow.setSalesInfo(currentQuotePage);
		loggerContxt.info("Set Sales infor finished.....");
		
		/**
		 * PGS-VLDTN-2.4
		 * Draft quote - Approval tab
		 * 
			1. Ensure the page display well
			2. Upload an non-empty attachment, download it and remove it.
			3. Upload an empty attachment, ensure application blocks the operation with proper message displayed.
		 */
		quoteFlow.setApproval(currentQuotePage);
		loggerContxt.info("Set Approval finished.....");
		
		quoteFlow.saveQuote(currentQuotePage);
		loggerContxt.info("Save quote finished.....");
		
		/**
		 * PGS-VLDTN-3
		 * Retrieve saved quote
		 * 
			1. Retrieve saved quote;
		 */
		quoteFlow.retrieveSavedSalesQuotePage(currentQuotePage);
		loggerContxt.info("Retrieve saved quote finished.....");
		
		/**
		 * PGS-VLDTN-5
		 * Status search
		 * 
			1. Find all matching quotes
			2. Find quote by number
		 */
		
		quoteFlow.findQuoteByAllCondition(currentQuotePage);
		
//		quoteFlow.findQuoteByNum(currentQuotePage);
//		loggerContxt.info("Search quote finished.....");
		
		/**
		 * PGS-VLDTN-8
		 * Help doc
		 * 
		 *  1. Check each help links
		 */
		quoteFlow.goToHelpPage(currentQuotePage);
		
		loggerContxt.info("@Test PGSStandAppVadProTest has passed!");
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
		PGSStandAppVadProTest test = new PGSStandAppVadProTest();
		try{
			test.setScriptName(String.format("PGS Standard %s Automation Test Script",test.getEnv()));
			String configStorePath = System.getProperty("user.dir") + File.separator + "res";
			test.setUp();
			test.assemblyBizFlow();
			test.quitWebdriver();
			test.sendAlertMail("build/mailBody.vm","succeed",null);
		}catch(Exception e){
			File attach = test.captureScreenshot(test.getClass().getSimpleName());
//			test.sendAlertMail("build/mailBody.vm","failed",attach);
			test.loggerContxt.fatal(String.format("%s Failed..", test.getScriptName()),e);
			test.teardown();
			e.printStackTrace();
		}
	}

}
