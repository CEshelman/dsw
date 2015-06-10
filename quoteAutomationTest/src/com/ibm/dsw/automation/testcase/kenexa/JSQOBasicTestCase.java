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

import com.ibm.dsw.automation.common.JBaseTestCase;

/**
 * DOC class global comment. Detailled comment
 */
public class JSQOBasicTestCase extends JBaseTestCase {

    /**
     * DOC JSQOBasicTestCase constructor comment.
     */
    public JSQOBasicTestCase() {
        super();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        loginSQO();
    }

}
