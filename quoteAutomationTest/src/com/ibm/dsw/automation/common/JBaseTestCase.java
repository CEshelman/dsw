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
package com.ibm.dsw.automation.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;

import com.ibm.dsw.automation.flow.PGSQuoteCommonFlow;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.util.DecryptManager;
import com.ibm.dsw.automation.util.Utils;
import com.ibm.dsw.automation.vo.LogonInf;


/**
 * DOC class global comment. Detailled comment
 */
public class JBaseTestCase extends TestCase {

    protected WebDriver driver = null;

    private String baseUrl = "";

    protected String dbdriver;

    String dburl;

    String dbuser;

    String dbpasswrd;

    private String loginUser;

    private String loginPasswd;

    public String htmlSourceFolder;

    public String env = "";

    public String clazzName = "";

    private String browser;

    private String browserSite;

    public static WebdriverLogger loggerContxt = WebdriverLogger.getLogger(BaseTestCase.class.getSimpleName());

    protected WebDriverBackedSelenium selenium = null;

    protected Properties prop = PropertiesSingleton.getInstance().getEnvProperties();

    protected Properties settingsProp = PropertiesSingleton.getInstance().getSettingProperties();

    protected SQOHomePage sqoHomePage = null;

    protected Properties propSub;

    public BasePropertiesBean propBean;

    private LogonInf logonInf;

    public static final String LOGON_IN_PGS_VIA_SQO_JUMP_PAGE = "sqoJP";

    public static final String LOGON_IN_PGS_VIA_SQO_WEBAUTH = "sqoWH";

    public static final String LOGON_IN_PGS_VIA_PGS_WEBAUTH = "pgsWH";

    public static final String LOGON_IN_PGS_VIA_PGS_JUMP = "pgsJP";

    public static final String LOGON_IN_SQO_VIA_SQO_WEBAUTH = "sqoWH";

    public static final String LOGON_IN_SQO_VIA_SQO_JUMP_PAGE = "sqoJP";

    public static final String LOGON_IN_SQO_VIA_SQO_JUMP_PAGE_BY_URL = "sqoJPByURL";

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriverBackedSelenium getSelenium() {
        return selenium;
    }

    public void setSelenium(WebDriverBackedSelenium selenium) {
        this.selenium = selenium;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        logonInf = new LogonInf();
        env = settingsProp.getProperty("env");
        loggerContxt.info("env before setting:" + env);
        if (null == env || "${deploy_target}".equals(env)) {
            env = "fvt";
        }
        loggerContxt.info("env after setting:" + env);

        initTestData();

        login();

        htmlSourceFolder = settingsProp.getProperty("htmlSourceFolder");
        browser = settingsProp.getProperty("browser");
        browserSite = settingsProp.getProperty("browserSite");

        dbdriver = prop.getProperty("dbdriver");
        dburl = prop.getProperty(env + ".dburl");
        dbuser = settingsProp.getProperty("dbuser");
        dbpasswrd = settingsProp.getProperty("dbpsswrd");

        loggerContxt.info("browser is " + browser);
        loggerContxt.info("browserSite is " + browserSite);
        loggerContxt.info("dbdriver:" + dbdriver);
        loggerContxt.info("dburl:" + dburl);
        loggerContxt.info("dbuser:" + dbuser);
        // Create a new instance of a driver
        setDriver(SeleniumBase.loadDriver());

        driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);

        getDriver().get(baseUrl);

        boolean needMaxWindow = Boolean.valueOf(
                settingsProp.getProperty("needMaxWindow") != null ? settingsProp.getProperty("needMaxWindow") : "false")
                .booleanValue();
        if (needMaxWindow) {
            // maximize browser window
            getDriver().manage().window().maximize();
        }
        setSelenium(new WebDriverBackedSelenium(getDriver(), getDriver().getCurrentUrl()));
        loggerContxt.info("baseUrl:" + baseUrl);
        loggerContxt.info("driver to sting:" + getDriver().hashCode());
    }

    /**
     * DOC Comment method "login".
     * @throws Exception
     */
    protected void login() throws Exception {
        logonInf.setSqoLogonUser(settingsProp.getProperty("sqo_username"));
        logonInf.setSqoUserPwd(settingsProp.getProperty("sqo_password"));
        logonInf.setSqoUrl(prop.getProperty(env + ".sqo_jump_url"));

        if (TestUtil.isSQOOrPGS(clazzName)) {
            baseUrl = prop.getProperty(env + ".sqo_url");
            loginUser = settingsProp.getProperty("sqo_username");
            loginPasswd = settingsProp.getProperty("sqo_password");

            boolean useDecrpt = Boolean.valueOf(
                    settingsProp.getProperty("bldByRTC") != null ? settingsProp.getProperty("bldByRTC") : "false").booleanValue();
            if (useDecrpt) {
                loginUser = DecryptManager.getUserId(Utils.USER_FILE);
                loginPasswd = DecryptManager.getPassword(Utils.USER_FILE);
                baseUrl = prop.getProperty(env + ".sqo_url");
            }

            logonInf.setSqoLogonUser(loginUser);
            logonInf.setSqoUserPwd(loginPasswd);
            logonInf.setSqoUrl(baseUrl);

            propBean = new SQOPropertiesBean();
            load();
        } else {
            baseUrl = prop.getProperty(env + ".pgs_url");
            loginUser = prop.getProperty(env + ".pgs_username");
            loginPasswd = prop.getProperty(env + ".pgs_password");
            propBean = new PGSPropertiesBean();
            load();
        }

        logonInf.setPgsLogonUser(loginUser);
        logonInf.setPgsUserPwd(loginPasswd);
        logonInf.setPgsUrl(baseUrl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        loggerContxt.info("needing quit the firefox after testing...." + settingsProp.getProperty("needQuitFF"));
        boolean needQuitFF = Boolean.valueOf(
                settingsProp.getProperty("needQuitFF") != null ? settingsProp.getProperty("needQuitFF") : "false").booleanValue();

        loggerContxt.info("needing quit the firefox after testing...." + needQuitFF);
        if (needQuitFF) {
            loggerContxt.info("teardown");
            getDriver().close();
            getDriver().quit();
        }
        super.tearDown();
    }

    public String getLoginUser() {
        return loginUser;
    }

    public String getLoginPasswd() {
        return loginPasswd;
    }

    public String getEnv() {
        return env;
    }

    /**
     * initialize properties for each test clase
     */
    public void initTestData() {
        clazzName = this.getClass().getSimpleName();
        String path = this.getClass().getPackage().getName();
        path = "/" + path.replace(".", "/");
        path += "/" + clazzName + ".properties";
        propSub = this.getTestDataProp(path);

        Properties validationProp = PropertiesSingleton.getInstance().getValidateProperties();
        prop.putAll(validationProp);
        Properties newCustInfo = PropertiesSingleton.getInstance().getNewCustInfoProperties();
        propSub.putAll(newCustInfo);
    }

    /**
     * @param key
     * @return String
     */
    protected String getProperty(String key) {
        String value = getProperty(propSub, key);
        if (StringUtils.isBlank(value)) {
            return getProperty(prop, key);
        }

        return value;
    }

    /**
     * At first, it will search by env + key, if it can not be found, it will search by key
     * 
     * @param p
     * @param key
     * @return
     */
    protected String getProperty(Properties p, String key) {
        String rs = p.getProperty(env + key);
        if (StringUtils.isBlank(rs)) {
            rs = p.getProperty(key);
        }

        return rs;
    }

    protected void reset() {
        getDriver().get(getDriver().getCurrentUrl());
        setSelenium(new WebDriverBackedSelenium(getDriver(), getDriver().getCurrentUrl()));
    }

    protected void reset(String url) {
        getDriver().get(url);
        setSelenium(new WebDriverBackedSelenium(getDriver(), url));
    }

    protected void quitAndReset() {
        String url = getDriver().getCurrentUrl();

        getDriver().close();
        getDriver().quit();

        setDriver(SeleniumBase.loadDriver());
        getDriver().get(url);
        setSelenium(new WebDriverBackedSelenium(getDriver(), url));
    }

    protected Connection getConnection(String driver, String url, String user, String pssword) throws ClassNotFoundException,
            SQLException {
        Class.forName(driver);
        Connection jdbcConnection = DriverManager.getConnection(url, user, pssword);
        return jdbcConnection;
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        return getConnection(dbdriver, dburl, dbuser, dbpasswrd);
    }

    protected Properties getTestDataProp(String path) {

        return TestUtil.getTestDataProp(path);
    }

    public void quitWebdriver() {
        getDriver().close();
        getDriver().quit();

    }

    // @AfterSuite
    public void sendMail() {

        boolean needsendMail = Boolean.valueOf(
                settingsProp.getProperty("needsendMail") != null ? settingsProp.getProperty("needsendMail") : "false")
                .booleanValue();

        loggerContxt.info("needing sending mail after testing...." + needsendMail);
        if (needsendMail) {
            loggerContxt.info("mail sending start....");
            TestUtil.callSendMail();
            loggerContxt.info("mail sending end....");
        }
    }

    public WebDriver getCurrentDriver() {
        return getDriver();
    }

    /**
     * This mothod was replaced by assertTextPresentTrue
     * 
     * @deprecated
     * @param strText
     */
    @Deprecated
    public void assertPresentText(String strText) {
        assertTrue(getSelenium().isTextPresent(strText));
    }

    /**
     * This mothod was replaced by assertTextPresentFalse
     * 
     * @deprecated
     * @param strText
     */
    @Deprecated
    public void assertNotPresentText(String strText) {
        assertFalse(getSelenium().isTextPresent(strText));
    }

    /**
     * @deprecated
     * @param strText
     */
    @Deprecated
    protected boolean isTextPresent(String strText) {
        boolean isPresent = getSelenium().isTextPresent(strText);
        if (isPresent) {
            loggerContxt.info("The text [" + strText + "] is existing!");
        } else {
            loggerContxt.info("The text [" + strText + "] is not existing!");
        }
        return isPresent;
    }

    /**
     * @deprecated
     * @param strText
     */
    @Deprecated
    protected void assertTextPresentTrue(String strText) {
        assertTrue(isTextPresent(strText));
    }

    /**
     * @deprecated
     * @param strText
     */
    @Deprecated
    protected void assertTextPresentFalse(String strText) {
        assertFalse(isTextPresent(strText));
    }

    /**
     * @deprecated
     * @param strElmId
     */
    @Deprecated
    protected boolean isElementPresent(String strElmId) {
        boolean isPresent = getSelenium().isElementPresent(strElmId);
        if (isPresent) {
            loggerContxt.info("The element [" + strElmId + "] is existing!");
        } else {
            loggerContxt.info("The element [" + strElmId + "] is not existing!");
        }
        return isPresent;
    }

    /**
     * @deprecated
     * @param strElmId
     */
    @Deprecated
    protected void assertElementPresentTrue(String strElmId) {
        assertTrue(isElementPresent(strElmId));
    }

    /**
     * @deprecated
     * @param strElmId
     */
    @Deprecated
    protected void assertElementPresentFalse(String strElmId) {
        assertFalse(isElementPresent(strElmId));
    }

    /**
     * @deprecated
     * @param strElmId
     */
    @Deprecated
    protected boolean isElementSelected(String strElmId) {
        WebElement element = getDriver().findElement(By.id(strElmId));
        boolean isSelected = element.isSelected();
        if (isSelected) {
            loggerContxt.info("The element [" + strElmId + "] is selected!");
        } else {
            loggerContxt.info("The element [" + strElmId + "] is not selected!");
        }
        return isSelected;
    }

    /**
     * @deprecated
     * @param strElmId
     */
    @Deprecated
    protected void assertElementSelectedTrue(String strElmId) {
        assertTrue(isElementSelected(strElmId));
    }

    /**
     * @deprecated
     * @param strElmId
     */
    @Deprecated
    protected void assertElementSelectedFalse(String strElmId) {
        assertFalse(isElementSelected(strElmId));
    }

    /**
     * @deprecated
     * @param strElmId
     */
    @Deprecated
    protected boolean isElementDisplayed(String strElmId) {
        WebElement element = getDriver().findElement(By.id(strElmId));
        boolean isDisplayed = element.isDisplayed();
        if (isDisplayed) {
            loggerContxt.info("The element [" + strElmId + "] is not displayed!");
        } else {
            loggerContxt.info("The element [" + strElmId + "] is displayed!");
        }
        return isDisplayed;
    }

    /**
     * @deprecated
     * @param strElmId
     */
    @Deprecated
    protected void assertElementDisplayedTrue(String strElmId) {
        assertTrue(isElementDisplayed(strElmId));
    }

    /**
     * @deprecated
     * @param strElmId
     */
    @Deprecated
    protected void assertElementDisplayedFalse(String strElmId) {
        assertFalse(isElementDisplayed(strElmId));
    }

    /**
     * @deprecated
     * @param arg1
     * @param arg2
     */
    @Deprecated
    protected void assertObjectEquals(Object arg1, Object arg2) {
        if (arg1.equals(arg2)) {
            loggerContxt.info("[" + arg1 + "] equals [" + arg2 + "]");
        } else {
            loggerContxt.info("[" + arg1 + "] does not equal [" + arg2 + "]");
        }
        assertEquals(arg1, arg2);
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 
     */
    protected void deleteAllCookies() {
        getDriver().manage().deleteAllCookies();
    }

    /**
     * 
     * @param elementPath
     * @return
     */
    protected String getElementText(String elementPath) {
        WebElement pText = getDriver().findElement(By.xpath(elementPath));
        return pText.getText();
    }

    /**
     * @deprecated
     * @param element
     * @return
     */
    @Deprecated
    protected boolean isElementDisplayed(WebElement element) {
        boolean isDisplayed = element.isDisplayed();
        if (isDisplayed) {
            loggerContxt.info("The element [" + element.getText() + "] is not displayed!");
        } else {
            loggerContxt.info("The element [" + element.getText() + "] is displayed!");
        }
        return isDisplayed;
    }

    /**
     * @deprecated
     * @param strElmLinkText
     */
    @Deprecated
    protected void elementClickByLinkText(String strElmLinkText) {
        WebElement elt = findElementByLinkText(strElmLinkText);

        if (isElementDisplayed(elt)) {
            elt.click();
        }
    }

    /**
     * @deprecated
     * @param strElmLinkText
     * @return
     */
    @Deprecated
    protected WebElement findElementByLinkText(String strElmLinkText) {
        return driver.findElement(By.linkText(strElmLinkText));
    }

    protected void load() {
        try {
            TestUtil.invokeSetter(propBean, propSub);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            loggerContxt.error(e.getMessage());
        }
    }

    protected SQOQuoteCommonFlow getCommonFlow() {
        SQOQuoteCommonFlow quoteFlow = new SQOQuoteCommonFlow();
        quoteFlow.setDriver(driver);
        quoteFlow.setSelenium(selenium);
        quoteFlow.setLogonInf(getLogonInf());
        quoteFlow.setPropBean((SQOPropertiesBean) propBean);
        return quoteFlow;
    }

    protected PGSQuoteCommonFlow getPGSCommonFlow() {
        PGSQuoteCommonFlow quoteFlow = new PGSQuoteCommonFlow();
        quoteFlow.setDriver(driver);
        quoteFlow.setSelenium(selenium);
        quoteFlow.setLogonInf(getLogonInf());
        quoteFlow.setPropBean((PGSPropertiesBean) propBean);
        return quoteFlow;
    }

    public LogonInf getLogonInf() {
        return logonInf;
    }

    public SQOHomePage loginSQO() {
        sqoHomePage = getCommonFlow().loginSqo(getLogonInf());
        return sqoHomePage;
    }

    public void captureScreen(String name) {
        SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy HHmmss");
        String dateStr = formater.format(Calendar.getInstance().getTime());
        String htmlSourceFolder = SeleniumBase.getHtmlSourceFolder();
        File screenshot = new File(htmlSourceFolder + File.separator + "local_sc" + File.separator + getCaseName()
                + File.separator + dateStr + " " + System.currentTimeMillis() + " " + name + "_" + ".png");
        if (!screenshot.exists()) {
            File container = new File(screenshot.getParent());
            if (!container.exists()) {
                container.mkdirs();
                try {
                    screenshot.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            new FileOutputStream(screenshot)
                    .write(((TakesScreenshot) SeleniumBase.getDriver()).getScreenshotAs(OutputType.BYTES));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loggerContxt.info("capture screenshot to " + screenshot.getAbsolutePath());
    }

    /**
     * DOC Comment method "getCaseName".
     * 
     * @return
     */
    private String getCaseName() {
        return this.getClass().getSimpleName();
    }

}
