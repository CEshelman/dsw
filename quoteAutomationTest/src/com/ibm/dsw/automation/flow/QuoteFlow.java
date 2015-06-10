package com.ibm.dsw.automation.flow;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.PropertiesSingleton;
import com.ibm.dsw.automation.common.SeleniumBase;
import com.ibm.dsw.automation.common.TestUtil;
import com.ibm.dsw.automation.vo.LogonInf;

public abstract class QuoteFlow   {

	protected Logger loggerContxt = Logger.getLogger(this.getClass().getName());
	
	WebDriverBackedSelenium selenium = null;
	WebDriver driver = null;
	/**
	 * 
	 */
	Properties prop = null;
	
	private LogonInf logonInf;

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
	
	protected void reset(String url) {
		setDriver(SeleniumBase.loadDriver());
		getDriver().get(url);
		setSelenium(new WebDriverBackedSelenium(getDriver(), url));
	}
	
	protected void reset(BaseTestCase testcase, String url) {
		//reset the driver for the QuoteFlow
		getDriver().close();
		setDriver(SeleniumBase.loadDriver());
		/*//reset the driver for the a page object
		page.getDriver().close();
		page.setDriver(getDriver());*/
		//reset the driver for a test case
		/*if (testcase.getDriver() != null) {
			testcase.getDriver().close();
		}*/
		testcase.setDriver(getDriver());
		getDriver().get(url);
		setSelenium(new WebDriverBackedSelenium(getDriver(), url));
//		page.setSelenium(new WebDriverBackedSelenium(getDriver(), url));
		testcase.setSelenium(new WebDriverBackedSelenium(getDriver(), url));
	}
	
	public void quitWebdriver() {
		getDriver().close();
		getDriver().quit();

	}
	

	public  void switchWindowByUrl(WebDriver driver, String url){
		
		Iterator<String> windowIterator = driver.getWindowHandles().iterator();
		while (windowIterator.hasNext()) {
			String windowHandle = windowIterator.next();
			if (driver.equals(windowHandle)) {
				continue;
			}
			try{
				driver.switchTo().window(windowHandle);
				String windowTile = driver.getTitle();
				String windowUrl = driver.getCurrentUrl();
				if (windowTile.contains(url) || windowUrl.contains(url)) {
					loggerContxt.info("Switch window to url: "+url);
					return;
				}
			}catch(NoSuchWindowException e){
				loggerContxt.error((String.format("Failed to switch the window to %s..",url)));
			}finally{
				loggerContxt.info("Current window title is :" + driver.getTitle());
			}
			
		}
	}
	
	public LogonInf getLogonInf() {
		return logonInf;
	}

	public void setLogonInf(LogonInf logonInf) {
		this.logonInf = logonInf;
	}

	public void init() {
		PropertiesSingleton.getInstance().setLogger(loggerContxt);
		prop = new Properties();
		prop.putAll(PropertiesSingleton.getInstance().getEnvProperties());
		prop.putAll(PropertiesSingleton.getInstance().getSettingProperties());
		prop.putAll(PropertiesSingleton.getInstance().getValidateProperties());
	}
	
	/**
	 * initialize properties for each test case
	 */
	public static Properties initTestData(Class clazz) {
		String clazzName = clazz.getSimpleName();
		String path = clazz.getPackage().getName();
		path = "/" + path.replace(".", "/");
		path += "/" + clazzName + ".properties";
		Properties prop = TestUtil.getTestDataProp(path);

		Properties validationProp = PropertiesSingleton.getInstance().getValidateProperties();
		prop.putAll(validationProp);
		Properties newCustInfo = PropertiesSingleton.getInstance().getNewCustInfoProperties();
		prop.putAll(newCustInfo);
		
		return prop;
	}

	/**
	 * @param file The folder path to store the configure files.
	 * @throws Exception 
	 */
	public static Properties initTestData(Class clazz, File file) throws Exception {
		String clazzName = clazz.getSimpleName();
		if (!(file.exists() && file.isDirectory())) {
			initTestData(clazz);
			throw new Exception("The input file is not existing, please have a check.");
		}
		File[] res = file.listFiles();
		Properties prop = new Properties();
		Map<String, File> fileMap = new HashMap<String, File>(res.length);
		for (File file2 : res) {
			fileMap.put(file2.getName(), file2);
		}
		if (!clazzName.contains(".properties")) {
			clazzName += ".properties";
		}
		Properties propSub = null;
		if (fileMap.containsKey(clazzName)) {
			propSub = TestUtil.getTestDataProp(fileMap.get(clazzName));
		}
		prop.putAll(propSub);
		Properties validationProp = null;;
		if (fileMap.containsKey("validationCont.properties")) {
			validationProp = TestUtil.getTestDataProp(fileMap.get("validationCont.properties"));
		}
		if (validationProp == null) {
			validationProp = PropertiesSingleton.getInstance(file.getAbsolutePath()).getValidateProperties();
		}
		prop.putAll(validationProp);
		Properties newCustInfo = null;
		if (fileMap.containsKey("NewCustomerInf.properties")) {
			validationProp = TestUtil.getTestDataProp(fileMap.get("NewCustomerInf.properties"));
		}
		if (null == newCustInfo) {
			newCustInfo = PropertiesSingleton.getInstance(file.getAbsolutePath()).getNewCustInfoProperties();
		}
		prop.putAll(newCustInfo);
		return prop;
	}
	
	public void setLoggerContxt(Logger loggerContxt) {
		if (null != loggerContxt) {
			this.loggerContxt = loggerContxt;
		}
	}

}
