package com.ibm.dsw.automation.testcase.xprs;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.pageobject.pao.PAOHomePage;
import com.ibm.dsw.automation.pageobject.pgs.CreateQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.LoginPage;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;

public class PGSLoginTest extends BaseTestCase {
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(PGSLoginTest.class.getSimpleName());
	
	 private static Properties prop;
	public void initTestData() {
		clazzName = "PGSLoginTest";
		if (prop == null) {
			prop = this
					.getTestDataProp("/com/ibm/dsw/automation/testcase/xprs/PGSLoginTest.properties");
		}
	}

	@Test(description = "Create a PGS quote")
	public void run() {

		// Login PGS
		LoginPage page = new LoginPage(this.driver, new Long(20));

		PAOHomePage homePage = page.loginAs(getProperty(prop, ".username"),
				getProperty(prop, ".password"));
		System.out.println("log in test finished.....");
		PGSHomePage pgsHomePage = homePage.gotoPGS();
		System.out.println("go to pgs home page finished.....");
		
		CreateQuotePage cq = pgsHomePage.gotoCreateQuote();
		
		// Create a PGS quote
		cq.createQuote();
		System.out.println("create quote page finished.....");


		 // Save current page's html source code.
		driver.manage().timeouts().implicitlyWait(35, TimeUnit.SECONDS);
		try {
			cq.saveHtmlSourceAs(htmlSourceFolder + "/pgs_cust_tab.html");
		} catch (IOException e1) {
			e1.printStackTrace();
		}finally{
			driver.close();
		}
/*
		// Get my current web qutoe num from db2
		Connection conn = null;
		try {
			conn = this.getConnection();
			User user = new User(env, conn);
			System.out.println("the current web quote num is : " + user.getMyCurrentWebQutoeNum("changwei@cn.ibm.com"));

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
*/
	}
	
	public static void main(String [] args) throws Exception{
		PGSLoginTest ebiz = new PGSLoginTest();
		ebiz.initTestData();
		ebiz.setUp();
		ebiz.run();



	}
}
