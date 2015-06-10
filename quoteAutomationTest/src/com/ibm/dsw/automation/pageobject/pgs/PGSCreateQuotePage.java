package com.ibm.dsw.automation.pageobject.pgs;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class PGSCreateQuotePage extends CreateQuotePage {

	public PGSCreateQuotePage(WebDriver driver) {
		super(driver);
	}

	@FindBy(id="country")
	private WebElement country;

	@Override
	public MyCurrentQuotePage createQuote() {
		/*selectedOptionByValue("country", "USA", getDriver());
		country.submit();
		loggerContxt.info("country.submit() finished......");*/
		
		/*It will implicitly wait for alert. 
		If alert is not present it will throw 'Alert is not present Exception'.*/
		/*WebDriverWait driverWait = new WebDriverWait(driver,10000);
		driverWait.until(ExpectedConditions.alertIsPresent());
		driver.switchTo().alert().accept();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);*/
		loggerContxt.info("Anders testing here...");
		Select countries = new Select(country);
		List<WebElement> options = countries.getAllSelectedOptions();
		for (WebElement element : options) {
			loggerContxt.info(String.format("-->The content of the countries option: %s",element.getText()));
		}
		countries.selectByIndex(10);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		continueButton.submit();
		switchToAlert(true, getClass().getSimpleName());
		MyCurrentQuotePage page = new PGSMyCurrentQuotePage(getDriver());
		loadPage(page, this.WAIT_TIME);
		return page;

	}
	
	public PGSMyCurrentQuotePage createQuote(boolean hasCurrentQuote) {
		if (isElementExsit(By.id("lob"))) {
			selectedOptionByValue("lob", "PAUN", getDriver());
		}
		selectedOptionByValue("country", "USA", getDriver());
		country.submit();
		loggerContxt.info("country.submit() finished......");

		if (hasCurrentQuote) {
//			Alert alert = getDriver().switchTo().alert();
//			Robot robot = null;
//			try {
//				robot = new Robot();
//			} catch (AWTException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			robot.keyPress(KeyEvent.VK_ENTER);  
//			robot.keyRelease(KeyEvent.VK_ENTER); 
//			
//			Actions action = new Actions(getDriver()) ;
////			action.keyDown(driver.findElement(getBy()),Keys.ENTER);
//			action.sendKeys(Keys.ARROW_RIGHT).perform();
//			action.sendKeys(Keys.ARROW_LEFT).perform();
//			action.sendKeys(Keys.ARROW_RIGHT).perform();
			switchToAlert(true, getClass().getSimpleName());
		}

		PGSMyCurrentQuotePage page = new PGSMyCurrentQuotePage(getDriver());
		loadPage(page, this.WAIT_TIME);
		return page;
		
		
	}
	
	public PGSCreateQuotePage createQuote(String lob, String ctry) {
		selectedOptionByValue("lob", lob, getDriver());
		selectedOptionByValue("country", ctry, getDriver());
		country.submit();
		loggerContxt.info("country.submit() finished......");
		switchToAlert(true, getClass().getSimpleName());
		PGSCreateQuotePage page = new PGSCreateQuotePage(getDriver());
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public PGSCreateQuotePage createQuote(String lob, String acquisition, String cntry) {
		selectedOptionByValue("lob", lob, getDriver());
		if (StringUtils.isNotBlank(acquisition)) {
		selectedOptionByValue("acquisition", acquisition, getDriver());
		}
		selectedOptionByValue("country", cntry, getDriver());
		country.submit();
		loggerContxt.info("country.submit() finished......");
		switchToAlert(true, getClass().getSimpleName());
		PGSCreateQuotePage page = new PGSCreateQuotePage(getDriver());
		loadPage(page, this.WAIT_TIME);
		return page;
	}

	@FindBy(name="ibm-submit")
	private WebElement continueButton;
}
