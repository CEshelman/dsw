package com.ibm.dsw.automation.pageobject.pgs;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class CreateQuotePage extends PGSBasePage {
	

	public CreateQuotePage(WebDriver driver) {
		super(driver);
	}

	
	public WebElement country;
	
	@FindBy(name="ibm-submit")
	public WebElement continueButton;
	
	public MyCurrentQuotePage createQuote() {
		/*It will implicitly wait for alert. 
		If alert is not present it will throw 'Alert is not present Exception'.*/
		/*WebDriverWait driverWait = new WebDriverWait(driver,10000);
		driverWait.until(ExpectedConditions.alertIsPresent());
		driver.switchTo().alert().accept();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);*/
		loggerContxt.info("Anders testing here...");
		Select countries = new Select(country);
		//country.sendKeys("Canada");
		List<WebElement> options = countries.getAllSelectedOptions();
		for (WebElement element : options) {
			loggerContxt.info(String.format("-->The content of the countries option: %s",element.getText()));
		}
		countries.selectByIndex(9);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		continueButton.submit();
		loggerContxt.info("country.submit() finished......");

		switchToAlert(true, getClass().getSimpleName());
		
		MyCurrentQuotePage page = new MyCurrentQuotePage(getDriver());
		return page;
		
	}
	
	public MyCurrentQuotePage createQuote(String ctyCd) {
		selectedOptionByValue("country",ctyCd,driver);
		country.submit();
		loggerContxt.info("country.submit() finished......");

		switchToAlert(true, getClass().getSimpleName());
		
		MyCurrentQuotePage page = new MyCurrentQuotePage(driver);
		return page;
		
	}
	
	
}
