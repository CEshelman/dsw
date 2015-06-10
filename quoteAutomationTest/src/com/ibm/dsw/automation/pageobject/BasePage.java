package com.ibm.dsw.automation.pageobject;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ibm.dsw.automation.common.PropertiesSingleton;
import com.ibm.dsw.automation.common.SeleniumBase;
import com.ibm.dsw.automation.common.TestUtil;

public class BasePage {

	protected WebDriver driver = null;
	protected String waitForElementId = null;
	protected long timeOutInSeconds = 60;
	protected final Long WAIT_TIME = new Long(30);
	
	private boolean enablePageValidation;
/*	public static WebdriverLogger loggerContxt = WebdriverLogger
	.getLogger(BasePage.class.getName());*/
	
	protected Logger loggerContxt = Logger.getLogger(this.getClass().getName());
	
	protected WebDriverBackedSelenium selenium = null;
	protected static Properties prop = PropertiesSingleton.getInstance()
			.getValidateProperties();
	protected static Properties settingsProp = PropertiesSingleton
			.getInstance().getSettingProperties();

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

	public BasePage(WebDriver driver) {
		super();
		init(driver);
		setThisWinHandle(getDriver().getWindowHandle());
	}

	protected void init(WebDriver driver) {
		setDriver(driver);

		loadPage(this);
		long pageLoadInterval = Long.valueOf(settingsProp
				.getProperty("pageLoadInterval") != null ? settingsProp
				.getProperty("pageLoadInterval") : "0");
		// loggerContxt.info("waiting for a moment after loading the page...."
		// + pageLoadInterval);
		if (pageLoadInterval > 0) {
			waitForElementLoading(pageLoadInterval);
		}

		setSelenium(new WebDriverBackedSelenium(driver, driver.getCurrentUrl()));

		enablePageValidation =Boolean.valueOf(settingsProp.getProperty("enablePageValidation",
				"false"));
			validationPageObject(getClass());
		
//		validationNoErrPresent();
		
		if (Boolean
				.valueOf(settingsProp.getProperty("enableSaveHtml", "false"))) {
			saveHtmlFile();
		}	

	}
	
	private void saveHtmlFile() {
		String htmlSourceFolder = SeleniumBase.getHtmlSourceFolder();
		String fileDir ="";
		try {
			

			 fileDir = htmlSourceFolder + File.separator + "html"
					+ File.separator + TestUtil.currentDateStr("yyyyMMdd", getLocalTimeZone())
					+ File.separator +  this.getClass().getSimpleName() + "_"
					+ System.currentTimeMillis() + ".html";

			saveHtmlSourceAs(fileDir);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			loggerContxt.info("problems happen druing save this html..."
					+ fileDir + "..." + e1.getMessage());
		}

	}

	protected void loadPage(Object nextPage, Long wait) {
		getDriver().manage().timeouts().implicitlyWait(wait, TimeUnit.SECONDS);
		PageFactory.initElements(getDriver(), nextPage);

	}

	private void loadPage(Object nextPage) {
		loadPage(nextPage, WAIT_TIME);
	}

	public void saveHtmlSourceAs(String path) throws IOException {
		File f = new File(path);
		
		 if (!f.exists()) {
				File container = new File(f.getParent());
				if (!container.exists()) {
					container.mkdirs();
				}
			}
		FileWriter writer = new FileWriter(f);
		writer.append(getHtmlSource());
		loggerContxt.info("Report Created is in Location : "
				+ f.getAbsolutePath());
		writer.close();
	}

	public String getHtmlSource() {
		String src = getDriver().getPageSource();
		return src;
	}

	public WebElement waitForElementById(final String elementId, Long timeOut) {
	/*	if (timeOut == null) {
			timeOut = timeOutInSeconds;
		}
		WebElement id = (new WebDriverWait(getDriver(), timeOut))
				.until(new ExpectedCondition<WebElement>() {
					@Override
					public WebElement apply(WebDriver d) {
						return d.findElement(By.id(elementId));
					}
				});
		return id;*/
		
		return waitForElementByLocator( By.id(elementId) , timeOut);
		
		
	}
	
	
	public WebElement waitForElementByLocator(final By locator, Long timeOut) {
		if (timeOut == null) {
			timeOut = timeOutInSeconds;
		}
		WebElement id = (new WebDriverWait(getDriver(), timeOut))
				.until(new ExpectedCondition<WebElement>() {
					@Override
					public WebElement apply(WebDriver d) {
						WebDriverBackedSelenium selenium = new WebDriverBackedSelenium(d,d.getCurrentUrl());
						if (selenium.isTextPresent("We are unable to load this product.")) {
							return null;
						}else{
							return d.findElement(locator);
						}
					}
				});
		return id;
		
		
	}
	
	public List<WebElement> waitForElementsByLocator(final By locator, Long timeOut) {
		if (timeOut == null) {
			timeOut = timeOutInSeconds;
		}
		List<WebElement> ids = (new WebDriverWait(getDriver(), timeOut))
				.until(new ExpectedCondition<List<WebElement>>() {
					@Override
					public List<WebElement> apply(WebDriver d) {
						WebDriverBackedSelenium selenium = new WebDriverBackedSelenium(d,d.getCurrentUrl());
						if (selenium.isTextPresent("We are unable to load this product.")) {
							return null;
						}else{
							return d.findElements(locator);
						}
					}
				});
		return ids;
	}

	// public void elementClickById(final String elementId){
	// waitForElementById(elementId).click();
	// }

	public WebElement waitForElementById(final String elementId) {
		return waitForElementById(elementId, null);
	}

	public WebElement waitForElementByLinktext(final String elementId) {
		return waitForElementByLinktext(elementId, null);
	}

	public WebElement waitForElementByLinktext(final String linkText,
			Long timeOut) {
/*		if (timeOut == null) {
			timeOut = timeOutInSeconds;
		}
		WebElement id = (new WebDriverWait(getDriver(), timeOut))
				.until(new ExpectedCondition<WebElement>() {
					@Override
					public WebElement apply(WebDriver d) {
						return d.findElement(By.linkText(linkText));
					}
				});
		return id;*/
		
		return waitForElementByLocator( By.linkText(linkText) , timeOut);
	}

	public void anonymousResolutionButtonClick(String elementId) {
		WebElement anonymousResolutionButton = waitForElementLoading(By.id(elementId), 60l);
		anonymousResolutionButton.click();
		loggerContxt.info("click Expand all Link");
	}

	public void anonymousResolutionForLinkTest(String linkText) {
		WebElement anonymousResolutionButton = waitForElementByLinktext(linkText);
		anonymousResolutionButton.click();
	}
	
	public void anonymousResolutionForLocator(By locator) {
		WebElement anonymousResolutionButton = waitForElementByLocator(locator,null);
		anonymousResolutionButton.click();
	}

	protected void implicitlyWait() {
		getDriver().manage().timeouts()
				.implicitlyWait(this.WAIT_TIME, TimeUnit.SECONDS);
	}

	public void selectedOptionByValue(WebElement select, String value, WebDriver driver) {
		List<WebElement> allOptions = select.findElements(By
				.tagName("option"));
		for (WebElement option : allOptions) {
			if (value.equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
	}
	
	/**
	 * @deprecated
	 */
	public void selectedOptionByValue(String selectId, String optionValue,
			WebDriver driver) {
		if (isElementExsit(By.id(selectId))) {
			WebElement select = getDriver().findElement(By.id(selectId));
			List<WebElement> allOptions = select.findElements(By
					.tagName("option"));
			for (WebElement option : allOptions) {
				if (optionValue.equals(option.getAttribute("value"))) {
					option.click();
					break;
				}
			}
		}
	}
	
	
	public void selectedOptionByValue(By locator, String optionValue,
			WebDriver driver) {
		if (isElementExsit(locator)) {
			WebElement select = getDriver().findElement(locator);
			List<WebElement> allOptions = select.findElements(By
					.tagName("option"));
			for (WebElement option : allOptions) {
				if (optionValue.equals(option.getAttribute("value"))) {
					option.click();
					break;
				}
			}
		}
	}
	
	public void selectedOptionByValue(By locator, String optionValue) {
		if (isElementExsit(locator)) {
			WebElement select = getDriver().findElement(locator);
			List<WebElement> allOptions = select.findElements(By
					.tagName("option"));
			for (WebElement option : allOptions) {
				String text = option.getText();
				loggerContxt.info(String.format("The current option value is : %s..",text));
				if (optionValue.equalsIgnoreCase(text)) {
					loggerContxt.info(String.format("Click on the current option : %s..",text));
					option.click();
					break;
				}
			}
		}
	}
	
	public void selectedOptionByIndex(By locator, int index) {
		if (isElementExsit(locator)) {
			WebElement select = getDriver().findElement(locator);
			List<WebElement> allOptions = select.findElements(By
					.tagName("option"));
			if ((allOptions != null) && (allOptions.size()>0)) {
				allOptions.get(index).click();
			}
		}
	}
	
	public Boolean verifySelectedOptionByValue(String selectId, String optionValue, WebDriver driver){
		Boolean isVerified = false;
		if (isElementExsit(By.id(selectId))) {
			WebElement select = getDriver().findElement(By.id(selectId));
			List<WebElement> allOptions = select.findElements(By
					.tagName("option"));
			for (WebElement option : allOptions) {
				if (optionValue.equals(option.getAttribute("value")) && option.getAttribute("selected") != null
						&& option.getAttribute("selected").equals("true")) {
					isVerified = true;
					break;
				}
			}
		}
		return isVerified;
	}
	
	public void selectedOptionByIndex(String selectId,String ind , WebDriver driver ){
		
		if (isElementExsit(By.id(selectId))) {
			
			WebElement selectOption = driver.findElement(By.xpath(".//*[@id='"+selectId+"']/option[contains(@value,'@')]["+ind+"]"));
			selectOption.click();
			
					}
	}
	/**
	 * choose the option as per the visible option text.
	 * @param elementId
	 * @param visibleText
	 */
	public void selectOptionByVisibleText(String elementId, String visibleText){
		WebElement ele = driver.findElement(By.id(elementId));
		this.selectOptionByVisibleText(ele,visibleText);
	}
	/**
	 * 
	 * @param ele
	 * @param visibleText
	 */
	public void selectOptionByVisibleText(WebElement ele, String visibleText){
		Select select = new Select(ele);
		select.selectByVisibleText(visibleText);
	}
	/**
	 * Select the option by the index.
	 * @param by
	 * @param index
	 */
	public void selectOptionByIndex(By by, String index){
		try{
			int ind = Integer.parseInt(index);
			WebElement ele = driver.findElement(by);
			this.selectOptionByIndex(ele, ind);
		}catch(Exception e){
			loggerContxt.error(String.format("Please configure a numeric as the index of the optioin for %s..",by.toString()));
			return;
		}
	}
	
	public void selectOptionByIndex(WebElement ele, int index){
		Select select = new Select(ele);
		select.selectByIndex(index);
	}
	
	public void selectedOptionByXpath(String selectId,String ind ){
		
		if (isElementExsit(By.id(selectId))) {
			String xpath = String.format(".//select[@id='%s']/option[@value='%s']",selectId,ind);
			loggerContxt.info("Locating the xpath::" + xpath);
			WebElement selectOption = driver.findElement(By.xpath(xpath));
			selectOption.click();
			
		}
	}
	
	public void selectedOptionByIndex(String selectId, String ind) {

		if (isElementExsit(By.id(selectId))) {

			WebElement selectOption = driver.findElement(By
					.xpath(".//*[@id='" + selectId
							+ "']/option[contains(@value,'@')][" + ind + "]"));
			selectOption.click();

		}
	}

	public void selectedOptionByLabel(String id, String label) {
		if (isElementExsit(By.id(id))) {
			selenium.select("id=" + id, "label=" + label);
		}
	}

	public void waitForElementLoading(Long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		driver.manage().timeouts().implicitlyWait(millis, TimeUnit.MILLISECONDS);
	}
	
	public WebElement waitForElementLoading(By by, long time){
		WebDriverWait wait = new WebDriverWait(driver, time);
        return wait.until(ExpectedConditions.elementToBeClickable(by));
	}

	protected void executeJavaScript(String script) {
//		getDriver().manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);
		((JavascriptExecutor) getDriver()).executeScript(script);
	}

	protected String getHandleByTitle(WebDriver driver, String title) {
		String currentHandle = getDriver().getWindowHandle();
		for (String handle : getDriver().getWindowHandles()) {
			String theTitle = getDriver().switchTo().window(handle).getTitle();
			if (theTitle.matches(title)) {
				return handle;
			}
		}
		return currentHandle;
	}
/**
 * close popup window by the title name
 * @param driver
 * @param title
 * @param orginalHandle 
 * @return
 */
	protected WebDriver closePopupByTitle(WebDriver driver, String title,
			String orginalHandle) {
		WebDriver theDriver = null;
		for (String handle : getDriver().getWindowHandles()) {
			String theTitle = getDriver().switchTo().window(handle).getTitle();
			if (theTitle.contains(title)) {
				getDriver().close();
			}
			if (!handle.equalsIgnoreCase(orginalHandle)) {
				theDriver = getDriver().switchTo().window(orginalHandle);
			}
		}
		return theDriver;

	}

	protected void setValueByXPath(String xpathExpression, String value) {
		getDriver().findElement(By.xpath(xpathExpression)).sendKeys(value);
	}

	protected void setValueById(String id, String value) {
		getDriver().findElement(By.id(id)).clear();
		getDriver().findElement(By.id(id)).sendKeys(value);
	}

	protected void sendKeys(WebElement element, String value) {
		if (isElementDisplayed(element)) {
			element.clear();
			element.sendKeys(value);
		}
	}

	/**
	 * 
	 * @param name
	 * @param value
	 */
	protected void setValueByName(String name, String value) {
		getDriver().findElement(By.name(name)).sendKeys(value);
	}

	public String getMsg(String msg) {
		return msg + new Date(System.currentTimeMillis()).toLocaleString();
	}

	public boolean isElementExsit(By locator) {
		boolean flag = false;
		try {
			WebElement element = getDriver().findElement(locator);
			flag = null != element;
		} catch (NoSuchElementException e) {
			loggerContxt.info("Element:" + locator.toString()
					+ " is not exsit!");
		}
		return flag;
	}

	public boolean switchToWindow(WebDriver driver, String windowTitle) {
		boolean flag = false;
		try {
			String currentHandle = getDriver().getWindowHandle();
			Set<String> handles = getDriver().getWindowHandles();
			for (String s : handles) {
				if (s.equals(currentHandle)) {
					continue;
				} else {
					getDriver().switchTo().window(s);
					if (getDriver().getTitle().contains(windowTitle)) {
						flag = true;
						loggerContxt.info("Switch to window: " + windowTitle
								+ " successfully!");
						break;
					} else {
						continue;
					}
				}
			}
		} catch (NoSuchWindowException e) {
			loggerContxt.error(String.format("No such window tile::%s found, please re-check...",windowTitle));
			flag = false;
		}
		return flag;
	}
	
	public void switchWindow(String title, String iframeId){
		
		Iterator<String> windowIterator = driver.getWindowHandles().iterator();
		while (windowIterator.hasNext()) {
			String windowHandle = windowIterator.next();
			if (driver.equals(windowHandle)) {
				continue;
			}
			try{
				driver.switchTo().window(windowHandle);
				String windowTile = driver.getTitle();
				if (windowTile.contains(title)) {
					driver.switchTo().frame(iframeId);
				}
			}catch(NoSuchWindowException e){
				loggerContxt.error((String.format("Failed to switch the window to %s..",title)));
			}finally{
				loggerContxt.info("Current window title is :" + driver.getTitle());
			}
			
		}
	}

	public void validationPageObject(Class<? extends BasePage> pageObjectCls) {
		loggerContxt.info("validation in Pageobject ["
				+ pageObjectCls.getSimpleName() + "]");
		String pageName = prop.getProperty(pageObjectCls.getSimpleName());
		if (StringUtils.isNotBlank(pageName)) {
			assertTextPresentTrue(pageName);
		}
	}

	public void checkPageAvailable(Class<? extends BasePage> pageObjectCls) {
		String text = prop.getProperty("ECustomerCare");
		loggerContxt.info("Check if the page exists:");
		if (getSelenium().isTextPresent(text)) {
			loggerContxt.info(String.format("The text '%s' show on the page:: %s.", text,pageObjectCls.getSimpleName()));
		}else {
			loggerContxt.info(String.format("The text '%s' doesnot show on the page:: %s.", text,pageObjectCls.getSimpleName()));
		}
	}

	/**
	 * judge if the alert is existing
	 * 
	 * @throws RuntimeException
	 */
	protected boolean alertExists() {
		try {
			getDriver().switchTo().alert();
			return true;
		} catch (NoAlertPresentException ne) {
			loggerContxt.info("no alert is present");
			return false;
		} catch (Exception e) {
			loggerContxt.error("no alert is present");
			throw new RuntimeException(e);
		}
	}
	/**
	 * In case the alertExists() doesn't not work
	 * @param driver
	 * @return
	 */
	protected boolean isDialogPresent() {
        try {
        	getDriver().getTitle();
        	loggerContxt.info("no alert is present");
            return false;
        } catch (UnhandledAlertException e) {
            // Modal dialog showed
            return true;
        }
    }
	
	protected void checkCheckbox(WebElement checkbox){
		if (!checkbox.isSelected()) {
			checkbox.click();
		}
	}
	
	//Check all the checkboxes whose name are the same
	public void checkAllBoxPerName(String name){
		List<WebElement> elements = driver.findElements(By.name(name));
		for (WebElement element : elements) {
			loggerContxt.info(String.format("The element::%s is displayed...",element.toString()));
			new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOf(element));
			if ((!element.isSelected())) {
				 element.click();
			}
		}
	}
	

	/**
	 * Check only one checkbox each time
	 * @param name is the checkboxes group name.
	 * @param i is the index of the checkbox only need to be checked.
	 */
	public void checkOnlyOneboxInTheGroup(String name, int i){
		loggerContxt.info(String.format("Check the %dth checkbox in the checkbox group::%s ..",i, name));
		List<WebElement> checkboxes = driver.findElements(By.name("ownerRoles"));
		if (!checkboxes.get(i).isSelected()) {
			checkboxes.get(i).click();
		}
		for (int j = 0; j < checkboxes.size(); j++) {
			if (j != i && checkboxes.get(j).isSelected()) {
				checkboxes.get(j).click();
			}
		}
	}

	/**
	 * check all the checkboxes as per the name of the checkboxes group
	 * @param name name of the checkboxes group
	 * @param selected is the indexes of the checkboxes which need to be selected. 0 is the first index.
	 */
	public void checkMultipleBoxes(String name, int...selected){
		List<WebElement> checkboxes = driver.findElements(By.name(name));
		for (int j = 0; j < selected.length; j++) {
			if (selected[j] < checkboxes.size()) {
				if (!checkboxes.get(j).isSelected()) {
					loggerContxt.info(String.format("Check the checkbox id '%s%d' in 'IBMer Assigned' tab..",name,j));
					checkboxes.get(j).click();
				}
			}else {
				loggerContxt.error(String.format("The input index::%d is out of the checkboxes bound,Please kindly re-check...",j));
				return;
			}
		}
	}

	/**
	 * 
	 * @param isAccept
	 * @param lastPageName
	 */
	public void switchToAlert(boolean isAccept, String lastPageName) {
		if (alertExists()) {
			loggerContxt.info("Switch to alert from " + lastPageName);
			Alert alert = getDriver().switchTo().alert();
			loggerContxt.info("Action done");
			if (alert != null) {
				if (isAccept) {
					loggerContxt.info("Accept the alert.");
					alert.accept();
					loggerContxt.info("Action done");
				} else {
					loggerContxt.info("Dismiss the alert.");
					alert.dismiss();
					loggerContxt.info("Action done");
				}
			}
		}
	}
	
	public void test(){
		getDriver().switchTo().window("dd");
		
	}

	/**
	 * 
	 * @param strElmId
	 * @return
	 */
	public boolean isElementExist(By locator) {
		try {
			return isElementDisplayed(locator);
		} catch (NoSuchElementException e) {
			loggerContxt.info("Element Id:" + locator.toString() + " is not exsit!");
			return false;
		}
	}
	
	public void showWindows(){
		//get current window handler
        String currentWindow = driver.getWindowHandle(); 
        //get all the window handlers
        Set<String> handles = driver.getWindowHandles(); 
        Iterator<String> it = handles.iterator(); 
        while(it.hasNext()){ 
            String handle = it.next(); 
            loggerContxt.info(String.format("Iterat the window::%s...",handle));
            if(currentWindow.equals(handle)){
            	loggerContxt.info(String.format("current window title = %s,url = %s",driver.getTitle(),driver.getCurrentUrl())); 
            }
            WebDriver window = driver.switchTo().window(handle); 
            loggerContxt.info(String.format("title = %s,url = %s",window.getTitle(),window.getCurrentUrl())); 
        } 
	}
	
	/**
	 * @deprecated
	 * @param strElmXPath
	 * @return
	 */
	public boolean isElementExistByXPath(String strElmXPath) {
		try {
			return isElementDisplayed(findElementByXPath(strElmXPath));
		} catch (NoSuchElementException e) {
			loggerContxt
					.info("Element XPath:" + strElmXPath + " is not exsit!");
			return false;
		}
	}
	
	public void elementSubmitByName(String strElmName) {
		WebElement elt = findElementByName(strElmName);
		
		if (isElementDisplayed(elt)) {
			elt.submit();
		}
	}

	/**
	 * 
	 * @param strEltId
	 */
	public void elementClickById(String strEltId) {
		assertElementPresentTrue(strEltId);
		WebElement elt = findElementById(strEltId);

		if (isElementDisplayed(elt)) {
			elt.click();
		}
	}

	/**
	 * 
	 * @param strElmXPath
	 */
	public void elementClickByXPath(String strElmXPath) {
		WebElement elt = findElementByXPath(strElmXPath);

		if (isElementDisplayed(elt)) {
			elt.click();
		}
	}

	/**
	 * 
	 * @param strEltName
	 */
	public void elementClickByName(String strEltName) {
		WebElement elt = findElementByName(strEltName);

		if (isElementDisplayed(elt)) {
			elt.click();
		}
	}

	/**
	 * 
	 * @param strEltId
	 * @return
	 */
	public WebElement findElementById(String strEltId) {
		return driver.findElement(By.id(strEltId));
	}

	/**
	 * 
	 * @param strEltXPath
	 * @return
	 */
	public WebElement findElementByXPath(String strEltXPath) {
		return driver.findElement(By.xpath(strEltXPath));
	}
	
	/**
	 * 
	 * @param strEltName
	 * @return
	 */
	public WebElement findElementByName(String strEltName) {
		return driver.findElement(By.name(strEltName));
	}

	/**
	 * 
	 * @param strEltId
	 */
	public void assertElementPresentTrue(String strEltId) {
		assertTrue(isElementPresent(strEltId));
	}

	/**
	 * 
	 * @param strEltId
	 * @return
	 */
	public boolean isElementPresent(String strEltId) {
		boolean isPresent = getSelenium().isElementPresent(strEltId);
		if (isPresent) {
			loggerContxt.info("The element [" + strEltId + "] is existing!");
		} else {
			loggerContxt
					.info("The element [" + strEltId + "] is not existing!");
		}
		return isPresent;
	}

	/**
	 * 
	 * @param strText
	 */
	public void assertTextPresentTrue(String strText) {
		if (enablePageValidation) {
			assertTrue(isTextPresent(strText));
		}
	}

	/**
	 * 
	 * @param strText
	 */
	public boolean isTextPresent(String strText) {
		boolean isPresent = getSelenium().isTextPresent(strText);
		if (isPresent) {
			loggerContxt.info("The text [" + strText + "] is existing!");
		} else {
			loggerContxt.info("The text [" + strText + "] is not existing!");
		}
		return isPresent;
	}

	/**
	 * 
	 * @param strText
	 */
	public void assertTextPresentFalse(String strText) {
		assertFalse(isTextPresent(strText));
	}

	/**
	 * 
	 * @param strElmId
	 */
	public void assertElementPresentFalse(String strElmId) {
		assertFalse(isElementPresent(strElmId));
	}

	/**
	 * @deprecated
	 * @param strElmId
	 */
	public boolean isElementSelected(String strElmId) {
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

	public boolean isElementSelected(By locator) {
		WebElement elt = getWebElementByLocator(locator);
		boolean isSelected = elt.isSelected();
		if (isSelected) {
			loggerContxt.info("The element [" + locator.toString() + "] is selected!");
		} else {
			loggerContxt
					.info("The element [" + locator.toString() + "] is not selected!");
		}
		return isSelected;
	}
	
	/**
	 * 
	 * @param strElmId
	 */
	public void assertElementSelectedTrue(String strElmId) {
		assertTrue(isElementSelected(strElmId));
	}

	/**
	 * 
	 * @param strElmId
	 */
	public void assertElementSelectedFalse(String strElmId) {
		assertFalse(isElementSelected(strElmId));
	}

	public boolean isElementDisplayed(By locator) {
		WebElement element = getWebElementByLocator(locator);
		boolean isDisplayed = element.isDisplayed();
		if (isDisplayed) {
			loggerContxt.info("The element [" + locator.toString()
					+ "] is not displayed!");
		} else {
			loggerContxt.info("The element [" + locator.toString() + "] is displayed!");
		}
		return isDisplayed;
	}

	/**
	 * 
	 * @param strElmId
	 */
	public void assertElementDisplayedTrue(By by) {
		assertTrue(isElementDisplayed(by));
	}

	/**
	 * 
	 * @param strElmId
	 */
	public void assertElementDisplayedFalse(By by) {
		assertFalse(isElementDisplayed(by));
	}

	/**
	 * 
	 * @param periodArr is the array of the indexes of the options in the dropdown list.
	 */
	public void selectMultipleOptionsInDropdownList(String elementId, String[] periodArr){
		Actions actions = new Actions(this.driver);
		for (int i = 0; i < periodArr.length; i++) {
			try{
				int num = Integer.parseInt(periodArr[i]);
				WebElement ele = driver.findElement(
						By.xpath(String.format(".//select[@id='%s']/option[%d]",elementId,num)));
				actions.moveToElement(ele);
				if (!ele.isSelected()) {
					actions.click();
				}
			}catch(Exception e){
				loggerContxt.info(String.format("Failed to parse the radia count::%s for Quater peroid.",periodArr[i]));
				continue;
			}
		}
		actions.perform();
	}
	
	/**
	 * 
	 * @param arg1
	 * @param arg2
	 */
	public void assertObjectEquals(Object arg1, Object arg2) {
		if (arg1.equals(arg2)) {
			loggerContxt.info("[" + arg1 + "] equals [" + arg2 + "]");
		} else {
			loggerContxt.info("[" + arg1 + "] does not equal [" + arg2 + "]");
		}
		assertEquals(arg1, arg2);
	}

	/**
	 * 
	 */
	public void deleteAllCookies() {
		getDriver().manage().deleteAllCookies();
	}

	/**
	 * @deprecated
	 * @param elementPath
	 * @return
	 */
	public String getElementText(String elementPath) {
		WebElement pText = getDriver().findElement(By.xpath(elementPath));
		return pText.getText();
	}
	
	
	public String getElementText(By locator) {
		WebElement elt = getWebElementByLocator(locator);
		return elt.getText();
	}
	
	/**
	 * @deprecated
	 * @param elementId
	 * @param attrName
	 * @return
	 */
	public String getAttrByElementId(String elementId, String attrName) {
		WebElement elt = findElementById(elementId);
		return elt.getAttribute(attrName);
	}
	
	public String getAttrByElementId(By locator, String attrName) {
		WebElement elt = getWebElementByLocator(locator);
		return elt.getAttribute(attrName);
	}
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	public boolean isElementDisplayed(WebElement element) {
		boolean isDisplayed = element.isDisplayed();
		if (isDisplayed) {
			loggerContxt.info("The element [" + element.getText()
					+ "] is  displayed!");
		} else {
			loggerContxt.info("The element [" + element.getText()
					+ "] is  not displayed!");
		}
		return isDisplayed;
	}

	/**
	 * 
	 * @param strElmLinkText
	 */
	public void elementClickByLinkText(String strElmLinkText) {
		assertTextPresentTrue(strElmLinkText);
		WebElement elt = findElementByLinkText(strElmLinkText);

		if (isElementDisplayed(elt)) {
			elt.click();
		}
	}

	/**
	 * 
	 * @param strElmLinkText
	 * @return
	 */
	public WebElement findElementByLinkText(String strElmLinkText) {
		return driver.findElement(By.linkText(strElmLinkText));
	}
	
	public void validationNoErrPresent() {
		
		assertTextPresentFalse(prop.getProperty("common_err_msg"));
	}
	
	public String getServerTimeZone() {
		return settingsProp.getProperty("serverTimeZone");
	}

	public String getLocalTimeZone() {
		return settingsProp.getProperty("localTimeZone");
	}
	

	/**
	 * 
	 * @param locator
	 * @return
	 */
	public List<WebElement> getAllWebElements(By locator) {
		return driver.findElements(locator);
	}
	
	public WebElement getWebElementByLocator(By locator) {
		return driver.findElement(locator);
	}
	
	private String thisWinHandle;
	
	public String getThisWinHandle() {
		return thisWinHandle;
	}

	public void setThisWinHandle(String thisWinHandle) {
		this.thisWinHandle = thisWinHandle;
	}
	
	public void returnToSelfWindow() {
		getDriver().switchTo().window(getThisWinHandle());
	}
	
	public void close() {
		returnToSelfWindow();
		getDriver().close();
	}

	public void setLoggerContxt(Logger loggerContxt) {
		if (loggerContxt != null) {
			this.loggerContxt = loggerContxt;
		}
	}
	
}