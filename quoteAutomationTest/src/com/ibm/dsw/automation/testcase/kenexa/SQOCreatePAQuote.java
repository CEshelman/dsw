/**
 * Copyright 2013 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * 
 * @author: Jack Liao(liaoqc@cn.ibm.com)
 * 
 * Creation date: Aug 22, 2013
 */
package com.ibm.dsw.automation.testcase.kenexa;

import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;

/**
 * DOC class global comment. Detailled comment
 */
public class SQOCreatePAQuote extends JSQOBasicTestCase {

    public void testFillInformation() {
        SQOQuoteCommonFlow quoteFlow = getCommonFlow();
        SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);

        currentQuotePage.goToPartsAndPricingTab();

    }

}
