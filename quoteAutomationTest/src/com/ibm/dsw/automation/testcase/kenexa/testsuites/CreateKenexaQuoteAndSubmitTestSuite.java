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
package com.ibm.dsw.automation.testcase.kenexa.testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.ibm.dsw.automation.testcase.kenexa.SQOAddKenexaParts;
import com.ibm.dsw.automation.testcase.kenexa.SQOCreatePAQuote;
import com.ibm.dsw.automation.testcase.kenexa.SQOSubmitQuote;


/**
 * DOC class global comment. Detailled comment
 */
public class CreateKenexaQuoteAndSubmitTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite(CreateKenexaQuoteAndSubmitTestSuite.class.getName());
        // $JUnit-BEGIN$
        suite.addTestSuite(SQOCreatePAQuote.class);
        suite.addTestSuite(SQOAddKenexaParts.class);
        suite.addTestSuite(SQOSubmitQuote.class);
        // $JUnit-END$
        return suite;

    }
}
