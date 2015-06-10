package com.ibm.dsw.automation.testcase.xprs.sqo;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.support.PageFactory;

import com.ibm.dsw.automation.common.SeleniumBase;
import com.ibm.dsw.automation.flow.QuoteFlow;
import com.ibm.dsw.automation.pageobject.sba.SBALoginPage;
import com.ibm.dsw.automation.pageobject.sba.SQOAdministrationPage;
import com.ibm.dsw.mail.core.EmailMessage;
import com.ibm.dsw.mail.core.HtmlEmailMessage;
import com.ibm.dsw.util.VelocityEmailBody;
import com.thoughtworks.selenium.Selenium;

/**
 * Used for the XPRS script auto validation in SIT phase.
 * XPRS script name: Special Bid Admin Tool
 * Team room link: Notes://D01DBL35/8525721300181EEE/4B87A6F6EAEAADD385256A2D006A582C/9BFDA5C0381FBA8D8525731D0072A9E6 
 * @author duzhiwei
 *
 */
public class SBASpecialBidAdminAutoTest {
	
	private static Logger logger = Logger.getLogger(SBASpecialBidAdminAutoTest.class);
	
	public static final String scriptName = "Special Bid Administration Tool Automation test.";
	private Properties prop;
	public static List<String> envs = Arrays.asList(new String[]{"local","fvt","mtp","uat","ps","prod","fvt.jump"});
	
	private String env = "";
	
	private SBALoginPage loginPage;
	
	private SQOAdministrationPage adminPage;
	
	private String baseUrl;
	
	private String username;
	
	private String password;
	
	private WebDriver driver;
	
	private Selenium selenium;
	
	public void init(){
		prop = QuoteFlow.initTestData(getClass());
		this.username = prop.getProperty(".loginUser");
		this.password = prop.getProperty(".loginPasswd");
		this.baseUrl = prop.getProperty(this.getEnv() + ".sba.baseUrl");
		env =prop.getProperty("env");
		logger.info("testing env:: " + env);
		driver = SeleniumBase.loadDriver();
	}
	
	public static void loadPage(WebDriver driver, Object nextPage, Long wait) {
		driver.manage().timeouts().implicitlyWait(wait, TimeUnit.SECONDS);
		PageFactory.initElements(driver, nextPage);

	}
	
	public void loginSBA(String env){
		if (driver == null) {
			logger.error("Please init the driver firstly...");
			return;
		}
		logger.info(baseUrl);
		driver.get(baseUrl);
		selenium = new WebDriverBackedSelenium(driver, driver.getCurrentUrl());
		logger.info(String.format("%s login in %s with url: %s",this.username,this.env,this.baseUrl));
		loginPage = new SBALoginPage(getDriver());
		loadPage(driver, loginPage, 30L);
		if (this.baseUrl.contains("jump")) {
			adminPage = loginPage.submitQueryByJump();
		}else{
			try{
			adminPage = loginPage.submitQueryViaWebAuth(username, password);
			} catch(Exception e){
				logger.info(String.format("Failed to logon on %s SBA with user:: %s..",env,username));
				e.printStackTrace();
			}
		}
	}
	
	public void test(){
		String quoteType = getProp().getProperty("line.of.business");
		String geo = getProp().getProperty("goe");
		adminPage = adminPage.clickGo(quoteType, geo);
		adminPage.editSpeicalBidConfig();
		String firstLineManager = getProp().getProperty(env+".first.line.manager");
		adminPage.editFirstLineManagerLink(firstLineManager);
		adminPage.clickFirstEditLink();
		String approverEmail = getProp().getProperty("approver.search.email");
		adminPage.searchApprover(approverEmail);
		String region = getProp().getProperty("region");
		String district = getProp().getProperty("district");
		String country = getProp().getProperty("country");
		adminPage.clickSpecialBidRegionLink();
		adminPage.editReionInfo(region, district, country);
		adminPage.viewFirstRoutingRule();
		adminPage.editFirstRoutingRule();
		String name = getProp().getProperty("rule.name");
		adminPage = adminPage.clickCreateNewPARoutingRuleLink();
		String quoteType1 = getProp().getProperty("quote.type");
		adminPage.createNewPARoutingRule(name, quoteType1, "");
		adminPage.clickTestRuleLink(quoteType);
		String busOrgs = getProp().getProperty("business.orgnization");
		String fulfillmentSource = getProp().getProperty("fulfillment.source");
		adminPage.testRule(country, quoteType1, busOrgs, fulfillmentSource);
		adminPage.clickApprovalRoutigRulesLink();
		adminPage.removeXPRSTestingRule();
		adminPage = adminPage.clickGlobalReaderGroupsLink();
		adminPage.editFirstglobalReaderGroup();
		adminPage.editFirstPartGroup("All");
		adminPage.editFirstCustomerGroup();
		logger.info("End of the script 'Special Bid Admin Tool:'  validation");
		
	}
	
	public void sendAlertMail(String templateName, String result){
		try{
		File file = new File("build/mailconfig.properties");
		File file0 = new File("build/mailServer.properties");
		Properties prop = new Properties();
		Properties prop0 = new Properties();
		prop.load(new FileInputStream(file));
		prop0.load(new FileInputStream(file0));
		prop.putAll(prop0);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("scriptName", scriptName);
		params.put("time", ((new Date()).toGMTString()));
		params.put("env", this.env);
		params.put("result", result);
		
		HtmlEmailMessage email = new HtmlEmailMessage(prop);
		email.getMessageConfigureInfo().setBody(VelocityEmailBody.generateMessageBody(params, templateName));
		Message msg = email.generateMessage();
		msg.setSubject(String.format(msg.getSubject(), this.env,(result.equals("failed")?"Failed.":"Successful.")));
		EmailMessage.sendMsg(msg);
		}catch(Exception e){
			logger.error("Failed to send the alert email...");
			e.printStackTrace();
		}
	}
	
	public static void tearDriver(WebDriver driver){
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}
	
	public Properties getProp() {
		return prop;
	}

	public void setProp(Properties prop) {
		this.prop = prop;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public Selenium getSelenium() {
		return selenium;
	}

	public void setSelenium(Selenium selenium) {
		this.selenium = selenium;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	

	public SBALoginPage getLoginPage() {
		return loginPage;
	}

	public void setLoginPage(SBALoginPage loginPage) {
		this.loginPage = loginPage;
	}

	public SQOAdministrationPage getAdminPage() {
		return adminPage;
	}

	public void setAdminPage(SQOAdministrationPage adminPage) {
		this.adminPage = adminPage;
	}

	/**
	 * 
	 * @param args env [username] password
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SBASpecialBidAdminAutoTest testcase = new SBASpecialBidAdminAutoTest();
		testcase.init();
		String enro = "";
		if (args.length > 2) {
			enro = args[0];
			testcase.setUsername(args[1]);
			testcase.setPassword(args[2]);
		}
		
		if (envs.contains(enro.toLowerCase())) {
			testcase.setEnv(enro);
		}
		try{
			testcase.setBaseUrl(testcase.getProp().getProperty(testcase.getEnv() + ".sba.baseUrl"));
			testcase.loginSBA(testcase.getEnv());
			testcase.test();
			tearDriver(testcase.getDriver());
			testcase.sendAlertMail("build/mailBody.vm", "succeed");
		}catch(Exception e ){
//			testcase.sendAlertMail("build/mailBody.vm", "failed");
			e.printStackTrace();
		}
		
	}
	
}
