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

import org.openqa.selenium.By;

import com.ibm.dsw.automation.common.JBaseTestCase;

/**
 * DOC class global comment. Detailled comment
 */
public class SQOTestKenecaQRWS extends JBaseTestCase {

    public void testOpenQRWSTestPage() {

        selenium.open(getProperty("QRWS_Test_URL"));
        driver.findElement(By.id("webQTNum")).sendKeys(getProperty("webQTNum"));
        driver.findElement(By.id("userID")).sendKeys(getProperty("userID"));
        selenium.click("css=input[type=\"submit\"]");
        selenium.waitForPageToLoad("30000");

    }

}
