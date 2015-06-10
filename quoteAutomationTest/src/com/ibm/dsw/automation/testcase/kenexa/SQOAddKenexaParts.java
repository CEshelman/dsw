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

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.ibm.dsw.automation.pageobject.pgs.ServiceConfigurePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOBrowseSoftwareAsServiceTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;

/**
 * DOC Test add Kenexa parts to PA/PAE quote
 */
public class SQOAddKenexaParts extends JSQOBasicTestCase {

    private ServiceConfigurePage serviceConfigurePage;

    public void testAddKenexaParts() {
        serviceConfigurePage = openBasicConfigurator();

        fillBasicConfigurator();

        submitConfigurator();

        captureScreen("after submit basic configurator");

    }

    /**
     * DOC Comment method "submitConfigurator".
     */
    protected void submitConfigurator() {
        serviceConfigurePage.elementClickById("addConfigrtnButton");
        serviceConfigurePage.waitForElementLoading(60000L);
    }

    public void fillBasicConfigurator() {
        loggerContxt.info("begin to fill the fileds in Basic Configurator");
        fillPartUpSelect("D0P79LL", "1");
        fillPartBillingSelect("D0P79LL", "E");
        loggerContxt.info("finish fill part D0P79LL");
        fillPartUpSelect("D0P70LL", "1");
    }

    /**
     * DOC Comment method "fillPartUpSelect".
     */
    private void fillPartUpSelect(String partId, String value) {
        WebElement partUptoSelect = findPartSelectByID(partId);
        loggerContxt.info("find part select" + partUptoSelect.getAttribute("id"));
        serviceConfigurePage.selectedOptionByValue(partUptoSelect, value, driver);
    }

    /**
     * DOC Comment method "fillPartBillingSelect".
     */
    private void fillPartBillingSelect(String partId, String value) {
        WebElement billingSelect = findPartBillingSelectByID(partId);
        serviceConfigurePage.selectedOptionByValue(billingSelect, value, driver);
        captureScreen("select " + partId);
    }

    public WebElement findPartBillingSelectByID(String partId) {
        WebElement element = null;
        try {
            element = driver.findElement(By.xpath("//select[contains(@id, '" + partId
                    + "') and contains(@id, 'billingFrequencySuffix')]"));
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
        return element;
    }

    public WebElement findPartSelectByID(String partId) {
        WebElement element = null;
        try {
            element = driver.findElement(By.xpath("//select[contains(@id, '" + partId + "')]"));
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
        return element;
    }

    private ServiceConfigurePage openBasicConfigurator() {
        SQOMyCurrentQuotePage currentPage = sqoHomePage.gotoMyCurrentQuotePage();
        SQOPartsAndPricingTabPage ppPage = currentPage.goToPartsAndPricingTab();
        SQOBrowseSoftwareAsServiceTabPage browseSoftwareAsServiceTabPage = ppPage.browseSoftwareAsAServiceLinkClick();

        browseSoftwareAsServiceTabPage.waitForElementLoading(new Long(25000));

        loggerContxt.info("browse SAAS page finished.....");
        selenium.click("link=Collaboration Solutions");
        selenium.click("link=Kenexa");
        selenium.click("css=#div1_0 > li > a[title=\"expand Collapse\"]");
        selenium.click("css=#div1_0_0 > li > a.ibm-popup-link");

        ServiceConfigurePage serviceConfigurePage = selectSaasPID(browseSoftwareAsServiceTabPage);
        return serviceConfigurePage;
    }

    private ServiceConfigurePage selectSaasPID(SQOBrowseSoftwareAsServiceTabPage browseSoftwareAsServiceTabPage) {
        // getDriver().switchTo().frame("cpqIframeId1");
        WebElement webElement = browseSoftwareAsServiceTabPage.waitForElementById("dijit_DialogUnderlay_0");
        if (webElement != null) {
            getDriver().switchTo().frame(browseSoftwareAsServiceTabPage.getWebElementByLocator(By.id("cpqIframeId1")));
        }
        ServiceConfigurePage scPage = new ServiceConfigurePage(getDriver());

        return scPage;
    }

}
