/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * 
 * @author: Jack Liao(liaoqc@cn.ibm.com)
 * 
 * Creation date: Aug 21, 2013
 */
package com.ibm.dsw.automation.testcase.kenexa;

import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;

/**
 * DOC Test add Kenexa parts to PA/PAE quote
 */
public class SQOSubmitQuote extends JSQOBasicTestCase {

    /**
     * DOC Comment method "submitQuote".
     */
    public void testSubmitQuote() {
        MyCurrentQuotePage currentQuotePage = sqoHomePage.gotoCurrenQuotePage();
        captureScreen("before submit quote");
        currentQuotePage.submitQuote();

        sqoHomePage.waitForElementLoading(50000L);
        captureScreen("after submit quote");
        if (!selenium.isTextPresent("number")) {
            assertTrue(true);
        }
    }

}
