/**
 * Copyright 2012 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * 
 * @author: Jack Liao(liaoqc@cn.ibm.com)
 * 
 * Creation date: Sep 2, 2013
 */
package com.ibm.dsw.automation.testcase.kenexa;

import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;

/**
 * DOC class global comment. Detailled comment
 */
public class SQOOpenKenecaSetUpPartDetails extends JSQOBasicTestCase {

    public void testOpenKenexaPartDetailsPage() {

        SQOMyCurrentQuotePage currentPage = sqoHomePage.gotoMyCurrentQuotePage();
        currentPage.goToPartsAndPricingTab();

        selenium.click("link=D0P79LL");
        selenium.waitForPopUp("", "30000");

        String winHandleBefore = driver.getWindowHandle();

        for (String winHandle : driver.getWindowHandles()) {
            System.out.println("+++" + winHandle);
            if (winHandle.equals(winHandleBefore)) {
                continue;
            }
            driver.switchTo().window(winHandle);
        }

        selenium.click("link=Price detail");

        assertTrue(selenium.isTextPresent("Event Based Billing"));

        captureScreen("Event Based Billing Option - Yes");
        driver.close();
        driver.switchTo().window(winHandleBefore);


    }

}
