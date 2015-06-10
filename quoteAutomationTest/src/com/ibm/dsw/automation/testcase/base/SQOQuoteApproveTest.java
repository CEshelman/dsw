package com.ibm.dsw.automation.testcase.base;

import java.util.Properties;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.pageobject.sqo.SQOApproveQueuePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOJumpPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitSQSpecialBidTabPage;

/**
 * @author yafei
 */
@SuppressWarnings("deprecation")
public class SQOQuoteApproveTest extends BaseTestCase
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        SQOQuoteApproveTest test = new SQOQuoteApproveTest();
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
    public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOQuoteApproveTest.class.getName());
    @Override
    public void initTestData()
    {
        clazzName = "SQOQuoteApproveTest";
    }

    @Test(description = "Approval queue")
    public void run()
    {
        // initial properties
        Properties prop = getTestDataProp("/com/ibm/dsw/automation/testcase/sqo/fvt/SQOQuoteApproveTest.properties");
        prop.putAll(settingsProp);
        loggerContxt.debug("start-> login!");
        SQOHomePage homePage = login(prop);
        if (null == homePage)
        {
            loggerContxt.info("login failure!");
            throw new RuntimeException();
        }
        loggerContxt.debug("approve quote detail.");
        approveQuote(prop,homePage);
    }

    protected SQOHomePage login(Properties prop)
    {
        SQOHomePage sqoHomePage = null;

       
        reset(prop.getProperty(env + ".sqo_jump_url"));
        // login system by jump.html
        SQOJumpPage jumpPage = new SQOJumpPage(this.driver);
        String countruCode = getProperty(prop, ".countryCode");
        String userName = getProperty(prop, ".userName");
        String accessLevel = getProperty(prop, ".accessLevel");
        jumpPage.loginIn(countruCode, userName, accessLevel);

        sqoHomePage = new SQOHomePage(driver);
      
        loggerContxt.debug("Exit: login method!");
        return sqoHomePage;
    }

    private void approveQuote(Properties prop,SQOHomePage homePage)
    {
        loggerContxt.debug("home page");
        SQOApproveQueuePage approveQueue = homePage.gotoApproveQueue();
        String quoteNum = getProperty(prop, ".quoteNum");
        SQOSubmitSQSpecialBidTabPage quoteDetails = null;
        if (null != quoteNum)
        {
            loggerContxt.debug("query quote page");
            quoteNum = quoteNum.trim();
            quoteDetails = approveQueue.findQuoteByNum(quoteNum);
        }
        if (null != quoteDetails)
        {
            loggerContxt.debug("quote detail page");
            String approveResult = getProperty(prop, ".approveResult");
            if ("approve".equals(approveResult))
            {
                quoteDetails.submitApproveResult();
            }
            else if ("reject".equals(approveResult))
            {
                quoteDetails.submitRejectResult();
            }
        }
    }

}
