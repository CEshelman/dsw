package com.ibm.dsw.automation.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.ibm.dsw.automation.flow.PGSQuoteCommonFlow;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.util.DecryptManager;
import com.ibm.dsw.automation.util.FileNameFilter0;
import com.ibm.dsw.automation.util.Utils;
import com.ibm.dsw.automation.utils.MyAlerts4SQO;
import com.ibm.dsw.automation.vo.LogonInf;
import com.ibm.dsw.mail.core.EmailMessage;
import com.ibm.dsw.mail.core.EmailMessageWithAttach;
import com.ibm.dsw.mail.core.HtmlEmailMessage;
import com.ibm.dsw.util.VelocityEmailBody;

public abstract class BaseTestCase {

	protected WebDriver driver = null;
	private String scriptName = "";
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
	protected Logger loggerContxt = Logger.getLogger(this.getClass());
	protected WebDriverBackedSelenium selenium = null;
	protected Properties prop = null;
	protected Properties settingsProp = null;
	protected SQOHomePage sqoHomePage = null;
	protected Properties propSub;
	public BasePropertiesBean propBean;
	protected MyAlerts4SQO myAlerts = new MyAlerts4SQO();
	
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

	/**
	 * 
	 * @param args To compatible with legacy code, 
	 * 			   the first argument can be a specified directory which store the configure files.
	 * 			   The other arguments are for future use.
	 * @throws Exception
	 */
	@BeforeClass
	public void setUp(String...args) throws Exception {
		logonInf=new LogonInf();
		if (args == null || args.length == 0) {
			initTestData();
		}else{
			initTestData(new File(args[0]));
		}
		
		env = settingsProp.getProperty("env");
		loggerContxt.info("env before setting:" + env);
		if (null == env || "${deploy_target}".equals(env)) {
			env = "fvt";
		}
		loggerContxt.info("env after setting:" + env);
		
		logonInf.setSqoLogonUser(settingsProp.getProperty("sqo_username"));
		logonInf.setSqoUserPwd(settingsProp.getProperty("sqo_password"));
		logonInf.setSqoUrl(prop.getProperty(env + ".sqo_jump_url"));
		
		loggerContxt.info("The current base url is : " + logonInf.getSqoUrl());
		
		if (TestUtil.isSQOOrPGS(clazzName)) {
			baseUrl = prop.getProperty(env + ".sqo_url");
			loginUser = settingsProp.getProperty("sqo_username");
			loginPasswd = settingsProp.getProperty("sqo_password");
			
			boolean useDecrpt = Boolean.valueOf(
					settingsProp.getProperty("bldByRTC") != null ? settingsProp
							.getProperty("bldByRTC") : "false").booleanValue();
			if(useDecrpt){
				loginUser  = DecryptManager.getUserId(Utils.USER_FILE);
				loginPasswd =  DecryptManager.getPassword(Utils.USER_FILE);
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

		getDriver().get(baseUrl);

		boolean needMaxWindow = Boolean
				.valueOf(
						settingsProp.getProperty("needMaxWindow") != null ? settingsProp
								.getProperty("needMaxWindow") : "false")
				.booleanValue();
		if (needMaxWindow) {
			// maximize browser window
			getDriver().manage().window().maximize();
		}
		setSelenium(new WebDriverBackedSelenium(getDriver(), getDriver().getCurrentUrl()));
		loggerContxt.info("baseUrl:" + baseUrl);
		loggerContxt.info("driver to sting:" + getDriver().hashCode());

	}
	
	public File captureScreenshot(String fileName) throws Exception{
		if (StringUtils.isBlank(fileName)) {
			fileName = System.getProperty("user.dir") + "/reporting/screenshot_" + System.currentTimeMillis();
		}
		try {
			fileName = System.getProperty("user.dir")+"/reporting/" + fileName + System.currentTimeMillis() + ".png";
			File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			File targetFile = new File(fileName);
			FileUtils.copyFile(scrFile, targetFile);
			return targetFile;
		} catch (Exception e1) {
			loggerContxt.fatal("Failed to copy the screenshot:: ",e1);
			throw new Exception(e1);
		}
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
		PropertiesSingleton.getInstance().setLogger(loggerContxt);
		prop = PropertiesSingleton.getInstance().getEnvProperties();
		settingsProp = PropertiesSingleton.getInstance().getSettingProperties();
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
	 * @param file The folder path to store the configure files.
	 * @throws Exception 
	 */
	public void initTestData(File file) throws Exception {
		clazzName = this.getClass().getSimpleName();
		if (!(file.exists() && file.isDirectory())) {
			loggerContxt.error("The input file is not existing, please have a check.");
			initTestData();
			throw new Exception("The input file is not existing, please have a check.");
		}
		PropertiesSingleton.getInstance(file.getAbsolutePath()).setLogger(loggerContxt);
		prop = PropertiesSingleton.getInstance(file.getAbsolutePath()).getEnvProperties();
		settingsProp = PropertiesSingleton.getInstance(file.getAbsolutePath()).getSettingProperties();
		File[] res = file.listFiles();
		Map<String, File> fileMap = new HashMap<String, File>(res.length);
		for (File file2 : res) {
			fileMap.put(file2.getName(), file2);
		}
		if (!clazzName.contains(".properties")) {
			clazzName += ".properties";
		}
		if (fileMap.containsKey(clazzName)) {
			propSub = this.getTestDataProp(fileMap.get(clazzName));
		}
		Properties validationProp = null;;
		if (fileMap.containsKey("validationCont.properties")) {
			validationProp = this.getTestDataProp(fileMap.get("validationCont.properties"));
		}
		if (validationProp == null) {
			validationProp = PropertiesSingleton.getInstance(file.getAbsolutePath()).getValidateProperties();
		}
		prop.putAll(validationProp);
		Properties newCustInfo = null;
		if (fileMap.containsKey("NewCustomerInf.properties")) {
			validationProp = this.getTestDataProp(fileMap.get("NewCustomerInf.properties"));
		}
		if (null == newCustInfo) {
			newCustInfo = PropertiesSingleton.getInstance(file.getAbsolutePath()).getNewCustInfoProperties();
		}
		propSub.putAll(newCustInfo);
		
	}
	
	/**
	 * 
	 * @param file The parent directory 
	 * @param filename The file name should search under the parent directory
	 * @return
	 * @throws Exception 
	 */
	private Properties loadFile(File file, String filename) throws Exception {
		File[] res = file.listFiles(new FileNameFilter0(filename));
		Properties p = null;
		try{
			for (File file2 : res) {
				//Only load the first listed file
				p = this.getTestDataProp(file2);
				break;
			}
			res = null;
		}catch(Exception e){
			loggerContxt.error("Failed to load properties for " + clazzName,e);
			throw e;
		}
		return p;
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
	 * At first, it will search by env + key, if it can not be found, it will
	 * search by key
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

	protected Connection getConnection(String driver, String url, String user,
			String pssword) throws ClassNotFoundException, SQLException {
		Class.forName(driver);
		Connection jdbcConnection = DriverManager.getConnection(url, user,
				pssword);
		return jdbcConnection;
	}

	public Connection getConnection() throws ClassNotFoundException,
			SQLException {
		return getConnection(dbdriver, dburl, dbuser, dbpasswrd);
	}

	protected Properties getTestDataProp(String path) {

		return TestUtil.getTestDataProp(path);
	}
	
	protected Properties getTestDataProp(File file) throws IOException {
		
		return TestUtil.getTestDataProp(file);
	}

	public void quitWebdriver() {
		getDriver().close();
		getDriver().quit();

	}

	@AfterClass
	public void teardown() {
		loggerContxt.info("needing quit the firefox after testing...."
				+ settingsProp.getProperty("needQuitFF"));
		boolean needQuitFF = Boolean.valueOf(
				settingsProp.getProperty("needQuitFF") != null ? settingsProp
						.getProperty("needQuitFF") : "false").booleanValue();

		loggerContxt.info("needing quit the firefox after testing...."
				+ needQuitFF);
		if (needQuitFF) {
			loggerContxt.info("teardown");
			getDriver().close();
			getDriver().quit();
		}

	}

	//@AfterSuite
	public void sendMail() {

		boolean needsendMail = Boolean.valueOf(
				settingsProp.getProperty("needsendMail") != null ? settingsProp
						.getProperty("needsendMail") : "false").booleanValue();

		loggerContxt.info("needing sending mail after testing...."
				+ needsendMail);
		if (needsendMail) {
			loggerContxt.info("mail sending start....");
			TestUtil.callSendMail();
			loggerContxt.info("mail sending end....");
		}
	}
	
	/**
	 * To send a email after the scripts run. All the email configure in 'mailconfig.properties'
	 * and mail server configure in 'mailServer.properties'.
	 * @param templateName The message body velocity template
	 * @param result The script running result 'success'/'failed'
	 * @param attachement The attachment for the email, if null then will send a mail without attachment.
	 */
	public void sendAlertMail(String templateName, String result, File screenshot){
		try{
		File file = new File("build/mailconfig.properties");
		File file0 = new File("build/mailServer.properties");
		Properties prop = new Properties();
		Properties prop0 = new Properties();
		prop.load(new FileInputStream(file));
		prop0.load(new FileInputStream(file0));
		prop.putAll(prop0);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("scriptName", this.scriptName);
		params.put("time", ((new Date()).toGMTString()));
		params.put("env", this.env);
		params.put("result", result);
		
		Message msg = null;
		HtmlEmailMessage email = new HtmlEmailMessage(prop);
		email.getMessageConfigureInfo().setBody(VelocityEmailBody.generateMessageBody(params, templateName));
		if (screenshot != null && screenshot.isFile()) {
			EmailMessageWithAttach email0 = new EmailMessageWithAttach(email);
			email0.setAttach(screenshot);
			email0.getMessageConfigureInfo().setBody(VelocityEmailBody.generateMessageBody(params, templateName));
			msg = email0.generateMessage();
		}else{
			msg = email.generateMessage();
		}
		msg.setSubject(String.format(msg.getSubject(), this.getEnv(),("failed".equals(result)?"Failed.":"Successful.")));
		EmailMessage.sendMsg(msg);
		myAlerts.sendMessage(msg.getSubject(), getProperty(settingsProp,"lead.contacts"));
		}catch(Exception e){
			loggerContxt.error("Failed to send the alert email...");
			e.printStackTrace();
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
			loggerContxt
					.info("The element [" + strElmId + "] is not existing!");
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
			loggerContxt
					.info("The element [" + strElmId + "] is not selected!");
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
			loggerContxt.info("The element [" + strElmId
					+ "] is not displayed!");
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
			loggerContxt.info("The element [" + element.getText()
					+ "] is not displayed!");
		} else {
			loggerContxt.info("The element [" + element.getText()
					+ "] is displayed!");
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
		selenium = new WebDriverBackedSelenium(driver, driver.getCurrentUrl());
		quoteFlow.setSelenium(selenium);
		quoteFlow.setLogonInf(getLogonInf());
		quoteFlow.setPropBean((SQOPropertiesBean) propBean);
		return quoteFlow;
	}
	protected PGSQuoteCommonFlow getPGSCommonFlow(){
		PGSQuoteCommonFlow quoteFlow = new PGSQuoteCommonFlow();
		quoteFlow.setDriver(driver);
		selenium = new WebDriverBackedSelenium(driver, driver.getCurrentUrl());
		quoteFlow.setSelenium(selenium);
		quoteFlow.setLogonInf(getLogonInf());
		quoteFlow.setPropBean((PGSPropertiesBean) propBean);
		return quoteFlow;
	}

	public LogonInf getLogonInf() {
		return logonInf;
	}
	
	public SQOHomePage loginSQO(){
		sqoHomePage = getCommonFlow().loginSqo(getLogonInf());
		return sqoHomePage;
	}

	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setLoggerContxt(Logger loggerContxt) {
		this.loggerContxt = loggerContxt;
	}

}
