/**
 * Copyright 2012 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * 
 * @author: Jack Liao(liaoqc@cn.ibm.com)
 * 
 * Creation date: Dec 13, 2012
 */
package com.ibm.dsw.automation.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.testng.annotations.AfterSuite;

/**
 * DOC class global comment. Detailled comment
 */
public class SeleniumBase {

    private static List<WebDriver> webDrivers = Collections.synchronizedList(new ArrayList<WebDriver>());

    protected static Properties prop = PropertiesSingleton.getInstance().getEnvProperties();

    protected static Properties settingsProp = PropertiesSingleton.getInstance().getSettingProperties();

    protected static ThreadLocal<WebDriver> driverForThread;

    @AfterSuite
    public static void tearDown() {
        for (WebDriver driver : webDrivers) {
            driver.quit();
        }
    }

    private static WebDriver loadWebDriver() {

        String browser = settingsProp.getProperty("browser");
        String browserSite = settingsProp.getProperty("browserSite");
        WebDriver driver;
        // Create a new instance of a driver
        if (browser.equals("firefox")) {
            if (browserSite.equals("")) {
                driver = new FirefoxDriver();
            } else {
				FirefoxProfile firefoxProfile = new FirefoxProfile();

				firefoxProfile.setPreference("browser.download.folderList", 2);
				firefoxProfile.setPreference("browser.download.manager.showWhenStarting", false);
				firefoxProfile.setPreference("browser.download.dir", "c:\\downloads");
				firefoxProfile
						.setPreference(
								"browser.helperApps.neverAsk.saveToDisk",
						"application/msexcel,application/octet-stream,application/vnd.ms-excel,application/excel");

				driver = new FirefoxDriver(new FirefoxBinary(new File(browserSite)), firefoxProfile);
            }
        } else {
            driver = new HtmlUnitDriver(true);
        }
        return driver;
    }

    public static WebDriver loadDriver() {
        driverForThread = new ThreadLocal<WebDriver>() {

            @Override
            protected WebDriver initialValue() {
                WebDriver driver = loadWebDriver();
                webDrivers.add(driver);
                return driver;
            }

        };


        return driverForThread.get();
    }

    public static WebDriver getDriver() {

        return driverForThread.get();
    }

    /**
     * Getter for htmlSourceFolder.
     * 
     * @return the htmlSourceFolder
     */
    public static String getHtmlSourceFolder() {
        return settingsProp.getProperty("htmlSourceFolder");
    }
}
