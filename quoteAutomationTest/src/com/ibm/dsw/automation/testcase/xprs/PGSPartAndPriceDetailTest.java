package com.ibm.dsw.automation.testcase.xprs;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.pageobject.pao.PAOHomePage;
import com.ibm.dsw.automation.pageobject.pgs.BrowsePartsTabPage;
import com.ibm.dsw.automation.pageobject.pgs.CreateQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.LoginPage;
import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;


public class PGSPartAndPriceDetailTest extends BaseTestCase {
	private static Properties prop;
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(PGSPPXPRSCreateQuoteTest.class.getSimpleName());
	
	@Override
	@BeforeSuite
	public void initTestData() {
		clazzName = "PGSAddSaasPartToQuoteTest";
		if (prop == null) {
			prop = this
					.getTestDataProp("/com/ibm/dsw/automation/testcase/xprs/PGSPartAndPriceDetailTest.properties");
		}
	}

	@Test(description = "Part and Price Detail")
	public void run() {

		// Login PGS
		LoginPage page = new LoginPage(this.driver, new Long(20));

		PAOHomePage homePage = page.loginAs(getProperty(prop, ".username"),
				getProperty(prop, ".password"));

		PGSHomePage pgsHomePage = homePage.gotoPGS();

		CreateQuotePage cq = pgsHomePage.gotoCreateQuote();
		
		MyCurrentQuotePage currentQuotePage = cq.createQuote(getProperty(prop, ".country"));		
		
		PartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();		
		BrowsePartsTabPage browsePartsTabPage = partsAndPricingTab.browsePartsLinkClick();
		browsePartsTabPage.addSelectedPartsToDraftQuote();	
		
		// Save current page's html source code.
		driver.manage().timeouts().implicitlyWait(35, TimeUnit.SECONDS);
		try {
			cq.saveHtmlSourceAs(htmlSourceFolder + "/pgs_cust_tab.html");
		} catch (IOException e1) {
			e1.printStackTrace();
		}		

	}
	
	public static void main(String[] args) throws Exception {
		String url = "https://www-112fvt.etl.ibm.com/software/howtobuy/softwareandservices/registration/Registration?caller=PAR";
		PGSPartAndPriceDetailTest test = new PGSPartAndPriceDetailTest();
		test.initTestData();
		test.setUp();
		test.run();
		test.teardown();
	}
}
