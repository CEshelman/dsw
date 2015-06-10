package com.ibm.dsw.automation.testcase.xprs.sqo;

import java.io.File;

import org.apache.log4j.Logger;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.SQOPropertiesBean;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;

/**
 * Software Quote and Order page login.
 * @author duzhiwei
 *
 */
public class SQOLogin extends BaseTestCase{

	private Logger loggerContxt = Logger.getLogger(this.getClass());

	protected SQOHomePage sqoHomePage = null;
	protected SQOQuoteCommonFlow quoteFlow = null;
	
	public SQOLogin(){
		try {
			String configPath = System.getProperty("user.dir") + File.separator + "res";
			setUp();
		} catch (Exception e) {
			loggerContxt.error(e.getCause().getMessage());
			e.printStackTrace();
		}
	}
	
	public SQOLogin(String baseUrl){
		setBaseUrl(baseUrl);
		try {
			setUp();
		} catch (Exception e) {
			loggerContxt.error(e.getCause().getMessage());
			e.printStackTrace();
		}
	}
	
	public void loginSqo() throws Exception {

		loggerContxt.info("env....." + env);
		quoteFlow = getCommonFlow();
		SQOPropertiesBean SqoProp = quoteFlow.getPropBean();

		try {
			if (Boolean.parseBoolean(getProperty(".local"))) {
				getLogonInf().setSqoLogonUser(SqoProp.getApproverUser());
				getLogonInf().setSqoUserPwd(SqoProp.getAccessLevelApprover());
				sqoHomePage = quoteFlow.loginSqo(getLogonInf(),
						LOGON_IN_SQO_VIA_SQO_JUMP_PAGE_BY_URL);
			} else {
				sqoHomePage = quoteFlow.loginSqo(getLogonInf());
			}
		} catch (Exception e) {
			throw new Exception("Failed to Login SQO page....", e);
		}
		driver.manage().window().maximize();
		loggerContxt.info("Login SQO finished.....");

		loggerContxt
				.info("verify current page whether having this content ......"
						+ this.getProperty(propSub, ".softQtOrd"));
		assertPresentText(this.getProperty(propSub, ".softQtOrd"));

	}
	
	public void loginSqo(String logonFlg) throws Exception {

		loggerContxt.info("env....." + env);
		quoteFlow = getCommonFlow();

		try {
			if (LOGON_IN_SQO_VIA_SQO_JUMP_PAGE_BY_URL.equalsIgnoreCase(logonFlg)) {
				sqoHomePage = quoteFlow.loginSqo(getLogonInf(),
						LOGON_IN_SQO_VIA_SQO_JUMP_PAGE_BY_URL);
				
			} else if(LOGON_IN_SQO_VIA_SQO_JUMP_PAGE.equalsIgnoreCase(logonFlg)){
				sqoHomePage = quoteFlow.loginSqo(getLogonInf(),
						LOGON_IN_SQO_VIA_SQO_JUMP_PAGE);
			}
			else {
				sqoHomePage = quoteFlow.loginSqo(getLogonInf());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to Login SQO page....", e);
		}
		driver.manage().window().maximize();
		loggerContxt.info("Login SQO finished.....");

		loggerContxt
				.info("verify current page whether having this content ......"
						+ this.getProperty(propSub, ".softQtOrd"));
		loggerContxt.info(driver.getCurrentUrl());
		loggerContxt.info(driver.getTitle());
		assertPresentText(this.getProperty(".softQtOrd"));

	}
	
	public void loginSqo(BaseTestCase testcase, String logonFlg) throws Exception {
		
		loggerContxt.info("env....." + env);
		quoteFlow = getCommonFlow();
		
		try {
			if (LOGON_IN_SQO_VIA_SQO_JUMP_PAGE_BY_URL.equalsIgnoreCase(logonFlg)) {
				sqoHomePage = quoteFlow.loginSqo(this, getLogonInf(),
						LOGON_IN_SQO_VIA_SQO_JUMP_PAGE_BY_URL);
				
			} else if(LOGON_IN_SQO_VIA_SQO_JUMP_PAGE.equalsIgnoreCase(logonFlg)){
				sqoHomePage = quoteFlow.loginSqo(this, getLogonInf(),
						LOGON_IN_SQO_VIA_SQO_JUMP_PAGE);
			}
			else {
				sqoHomePage = quoteFlow.loginSqo(getLogonInf());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Failed to Login SQO page....", e);
		}
		driver.manage().window().maximize();
		loggerContxt.info("Login SQO finished.....");
		
		loggerContxt
		.info("verify current page whether having this content ......"
				+ this.getProperty(propSub, ".softQtOrd"));
		loggerContxt.info(driver.getCurrentUrl());
		loggerContxt.info(driver.getTitle());
		assertPresentText(this.getProperty(".softQtOrd"));
		
	}

	public SQOHomePage getSqoHomePage() {
		return sqoHomePage;
	}

	public SQOQuoteCommonFlow getQuoteFlow() {
		return quoteFlow;
	}
	
}
