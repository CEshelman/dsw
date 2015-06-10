package com.ibm.dsw.automation.testcase.xprs;

import java.io.IOException;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.pageobject.pao.PAOHomePage;
import com.ibm.dsw.automation.pageobject.pgs.LoginPage;
import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSSiteSelectPage;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;

public class PGSEI398581Test extends BaseTestCase {
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(PGSEI398581Test.class.getSimpleName());
	

	@Test(description = "Create a SQO quote")
	public void run (){
		LoginPage page = new LoginPage(this.driver, new Long(20));
		PGSSiteSelectPage siteSelectPage= page.loginAsToSiteSelectPage(getLoginUser(), getLoginPasswd());
		PAOHomePage homePage = siteSelectPage.gotoPAOHomePage(getProperty(propSub, ".indexOfSelectedSite"));

		PGSHomePage pgsHomePage = homePage.gotoPGS();

//		CreateQuotePage cq = pgsHomePage.gotoCreateQuote();

//		MyCurrentQuotePage currentQuotePage = cq.createQuote();

		//Access currentQuotePage
		MyCurrentQuotePage currentQuotePage = pgsHomePage.gotoCurrenQuotePage();


		// in part and pricing page
		PartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();

		partsAndPricingTab.browseSoftwareAsAServiceLinkClick();
		String text = getProperty("ECustomerCare");
		if (isTextPresent(text)) {
			loggerContxt.info(text);
		}
		try {
			currentQuotePage.saveHtmlSourceAs(htmlSourceFolder + "/PGSEI398581Test.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			driver.close();
		}
		//browseSoftwareAsServiceTabPage.configureSaas4CastIron();
	//	browseSoftwareAsServiceTabPage.addFirstPart("10");
	//	browseSoftwareAsServiceTabPage.waitForElementLoading(new Long(20000));
	//	browseSoftwareAsServiceTabPage.returnToDraftQuoteLinkClick("PGS");


	}

	public static void main(String [] args) throws Exception{
		PGSEI398581Test ebiz = new PGSEI398581Test();
		ebiz.initTestData();
		ebiz.setUp();
		ebiz.run();



	}



}
