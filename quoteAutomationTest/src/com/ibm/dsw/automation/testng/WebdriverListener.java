/**
 * Copyright 2012 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM Corporation. ("Confidential Information").
 * 
 * 
 * 
 * Creation date: Dec 13, 2012
 */
package com.ibm.dsw.automation.testng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.IClass;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.ibm.dsw.automation.common.MailSender;
import com.ibm.dsw.automation.common.PropertiesSingleton;
import com.ibm.dsw.automation.common.SeleniumBase;
import com.ibm.dsw.automation.common.TestUtil;
import com.ibm.dsw.automation.common.WebdriverLogger;

public class WebdriverListener implements ITestListener {

	Logger loggerContxt = Logger.getLogger(this.getClass().getSimpleName());
	private static Properties settingsProp = PropertiesSingleton.getInstance()
			.getSettingProperties();
	private static final String NEED_SEND_MAIL_FILE="mailneed.dat";
	private static final String NEED_SEND_MAIL_FLAG="Y";
	SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS");

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.ITestListener#onFinish(org.testng.ITestContext)
	 */
	@Override
	public void onFinish(ITestContext arg0) {
		try{
		if(hasFailureCase()){
			boolean needsendMail = Boolean.valueOf(
					settingsProp.getProperty("needsendMail") != null ? settingsProp
							.getProperty("needsendMail") : "false").booleanValue();

			loggerContxt.info("needing sending mail after testing...."
					+ needsendMail);
			if (needsendMail) {
				loggerContxt.info("mail sending start....");
				//using ant to send mail
				//TestUtil.callSendMail();
				
				//based on html to send java mail
				sendHtmlMail(arg0);
				loggerContxt.info("mail sending end....");
			}
		loggerContxt.info("mail sending end -------------- finished");
		}
		
		} catch (Exception ex) {
			loggerContxt.info("mail sending exception --------------"+ex.getMessage());
		}
		
		try{
		boolean enableSaveHtml = Boolean
				.valueOf(
						settingsProp.getProperty("enableSaveHtml") != null ? settingsProp
								.getProperty("enableSaveHtml") : "false")
				.booleanValue();
		if (enableSaveHtml) {
			String htmlSourceFolder = SeleniumBase.getHtmlSourceFolder();

			String fileDir = htmlSourceFolder
					+ File.separator
					+ "html"
					+ File.separator
					+ TestUtil.currentDateStr("yyyyMMdd",
							settingsProp.getProperty("localTimeZone"));

			TestUtil.findFiles(fileDir);
			loggerContxt.info("replace html name finished --------------");
		}
		} catch (Exception ex) {
			loggerContxt.info("enableSaveHtml ex --------------"+ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.ITestListener#onStart(org.testng.ITestContext)
	 */
	@Override
	public void onStart(ITestContext testResult) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.testng.ITestListener#onTestFailedButWithinSuccessPercentage(org.testng
	 * .ITestResult)
	 */
	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult testResult) {

	}

	/*
	 * Method Name : onTestFailure<br> Description : while assert failed, take a
	 * screen capture with detailed description about how to reproduce this
	 * case<br> Objective: while assert failed, take a screen capture image and
	 * put it into folder: htmlSourceFolder
	 * 
	 * @since 12/13/2012
	 * 
	 * @author jack liao
	 */
	@Override
	public void onTestFailure(ITestResult testResult) {
		if (Boolean.valueOf(settingsProp.getProperty("enalbeLogInReport",
				"false"))) {
			printLog(testResult);
		}
	/*	SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy HHmmss");
		String dateStr = formater.format(Calendar.getInstance().getTime());*/
		String htmlSourceFolder = SeleniumBase.getHtmlSourceFolder();
		String pngName= testResult.getTestClass().getName() + "_"+TestUtil.currentDateStr("dd-MM-yyyy HHmmss", settingsProp.getProperty("localTimeZone"))
		+ ".png";
		File screenshot = new File(htmlSourceFolder + File.separator + "html"
				+ File.separator +pngName);
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
					.write(((TakesScreenshot) SeleniumBase.getDriver())
							.getScreenshotAs(OutputType.BYTES));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			loggerContxt.info(" FileNotFoundException --------------"+e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			loggerContxt.info(" IOException --------------"+e.getMessage());
		}catch (Exception ex) {
			ex.printStackTrace();
			loggerContxt.info(" Exception --------------"+ex.getMessage());
		}

		loggerContxt.info("Written screenshot to "
				+ screenshot.getAbsolutePath());
		
		TestUtil.creatFile(NEED_SEND_MAIL_FILE, NEED_SEND_MAIL_FLAG);
		
		testResult.setAttribute(testResult.getName(),pngName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.ITestListener#onTestSkipped(org.testng.ITestResult)
	 */
	@Override
	public void onTestSkipped(ITestResult testResult) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.ITestListener#onTestStart(org.testng.ITestResult)
	 */
	@Override
	public void onTestStart(ITestResult testResult) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.testng.ITestListener#onTestSuccess(org.testng.ITestResult)
	 */
	@Override
	public void onTestSuccess(ITestResult testResult) {
		if (Boolean.valueOf(settingsProp.getProperty("enalbeLogInReport",
				"false"))) {
			printLog(testResult);
		}
	}

	private void printLog(ITestResult testResult) {
		
		//String dateStr = formater.format(Calendar.getInstance().getTime());

		IClass ic = testResult.getTestClass();
		String clazzName = ic.getName();

		Reporter.log(clazzName + "."
				+ testResult.getMethod().getMethodName() + " log start---------");

		List list = WebdriverLogger.getInfList();
		for (int i = 0; i < list.size(); i++) {
			Reporter.log(list.get(i).toString());
		}

		WebdriverLogger.RemoveInfList();

		Reporter.log(clazzName + "."
				+ testResult.getMethod().getMethodName() + " log end---------");
		Reporter.log("--------------------------------------------------------");
	
		
	}


	
	private boolean hasFailureCase(){
		
		String fileCont=TestUtil.readFile(NEED_SEND_MAIL_FILE).toString();
		return fileCont.contains(NEED_SEND_MAIL_FLAG);
		
	}
	
	private void sendHtmlMail(ITestContext arg0){
		
		Map param=new HashMap();
		int passedTestsCnt=arg0.getPassedTests().getAllResults().size();
		int failedTestsCnt=arg0.getFailedTests().getAllResults().size();
		int skippedTestsCnt=arg0.getSkippedTests().getAllResults().size();
		
		int totalTestsCnt=passedTestsCnt+failedTestsCnt+skippedTestsCnt;
		int passRate=(passedTestsCnt * 100 / totalTestsCnt);
		
		param.put("passedTestsCnt", passedTestsCnt);
		param.put("failedTestsCnt", failedTestsCnt);
		param.put("skippedTestsCnt",skippedTestsCnt);
		param.put("passRate", passRate);
		param.put("env", settingsProp.getProperty("env"));
		
		loggerContxt.info(" arg0.getPassedTests().getAllResults().size() --------------"+ passedTestsCnt);
		loggerContxt.info(" arg0.getFailedTests().getAllResults().size() --------------"+ failedTestsCnt);
		loggerContxt.info(" arg0.getSkippedTests().getAllResults().size() --------------"+skippedTestsCnt);
		
		Properties mailConfigProp=PropertiesSingleton.getInstance().getMailConfigPropperties();
		param.put("mailConfig", mailConfigProp);
		MailSender.sendhtml(param);
		
	}

}
