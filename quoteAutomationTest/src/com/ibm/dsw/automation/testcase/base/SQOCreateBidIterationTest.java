package com.ibm.dsw.automation.testcase.base;

import java.util.Properties;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayStatusSearchReslutPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuoteCPTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuotePPTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOStatusSearchTabPage;

public class SQOCreateBidIterationTest extends SQOQuoteApproveTest
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        SQOCreateBidIterationTest test = new SQOCreateBidIterationTest();
        try
        {
            test.setUp();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        test.run();

    }

    /**
     * log record
     */
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOCreateBidIterationTest.class.getName());
	
    @Override
    public void initTestData()
    {
        clazzName = "SQOCreateBidIterationTest";
    }
    
    @Override
	@Test(description = "create bid iteration")
    public void run()
    {
     // initial properties
        Properties prop = getTestDataProp("/com/ibm/dsw/automation/testcase/sqo/fvt/SQOCreateBidIterationTest.properties");
        loggerContxt.debug("start-> login!");
        SQOHomePage homePage = login(prop);
        if (null == homePage)
        {
            loggerContxt.info("login failure!");
            throw new RuntimeException();
        }
        loggerContxt.debug("create bid iteration detail.");
        createBidIteration(prop,homePage);
    }
    
    private void createBidIteration(Properties prop,SQOHomePage homePage)
    {
        SQOStatusSearchTabPage statusSearch =  homePage.gotoStatus();
        String quoteNum = getProperty(prop, ".quoteNum");
        SQODisplayStatusSearchReslutPage statusSearchResult = statusSearch.goDispQuoteReslt(quoteNum);
        SQOSelectedQuoteCPTabPage quoteCPTabPage = statusSearchResult.goDispQuoteReslt();
        SQOSelectedQuotePPTabPage partpriceTabPage = quoteCPTabPage.createBidIteration();
        partpriceTabPage.overrideConfiguration();
        
    }
}
