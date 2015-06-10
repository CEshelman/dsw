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
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;

/**
 * DOC class global comment. Detailled comment
 */
public class SQOCreateQuoteAndFillInfo extends JSQOBasicTestCase {

    public void testFillInformation() {
        SQOQuoteCommonFlow quoteFlow = getCommonFlow();
        SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);

        // 2.add customer
        currentQuotePage = quoteFlow.processCustPartnerTab(currentQuotePage);
        loggerContxt.info("add customer finished.....");

        SQOPartsAndPricingTabPage ppTab = currentQuotePage.goToPartsAndPricingTab();
        // Edit Sales Info Tab
        quoteFlow.editSalesInfoTab(ppTab);
        loggerContxt.info("edit sales info tab finished.....");

        // Edit Special Bid Info
        quoteFlow.editSpcialBidInf(ppTab);
        loggerContxt.info("edit special bid info finished.....");

        currentQuotePage.goToPartsAndPricingTab();

    }

}
