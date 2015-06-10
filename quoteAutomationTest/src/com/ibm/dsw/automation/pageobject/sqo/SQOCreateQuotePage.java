package com.ibm.dsw.automation.pageobject.sqo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.CreateQuotePage;

public class SQOCreateQuotePage extends CreateQuotePage {
	/*public WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(this.getClass().getSimpleName());*/
	private final WebDriverBackedSelenium selenium;

	public SQOCreateQuotePage(WebDriver driver) {
		super(driver);
		selenium = new WebDriverBackedSelenium(driver, driver.getCurrentUrl());
	}

	public WebElement lob;

	public WebElement country;
 
	@FindBy(id="markAsDefault")
	public WebElement markAsDefault;

	@FindBy(name = "ibm-submit")
	protected WebElement continueButton;
	
	/**
	 * to create a quote which data is copy from UAT database.
	 * @author suchuang
	 * @date Jan 11, 2013
	 * @param quote
	 * @param hasCurrentQuote
	 * @return
	 */
	public SQOMyCurrentQuotePage createQuote(Map<String, String> quote) {
		
		boolean hasCurrentQuote = selenium.isTextPresent("SQO reference");
		loggerContxt.info("having current quote....." + hasCurrentQuote);
		
		String lob = StringUtils.isBlank(quote.get("PROG_CODE")) ? "PAUN" : quote.get("PROG_CODE");
		String ctry = StringUtils.isBlank(quote.get("CNTRY_CODE")) ? "USA" : quote.get("CNTRY_CODE");
		String acq = StringUtils.isBlank(quote.get("ACQRTN_CODE")) ? "DTMR" : quote.get("ACQRTN_CODE");
		
		selectedOptionByValue("lob", lob, driver);
		selectedOptionByValue("country", ctry, driver);
		markAsDefault.click();
		
		if (("FCT".equals(quote.get("PROG_CODE")))
		 || ("FMP".equals(quote.get("PROG_CODE")))) {
			selectedOptionByValue("acquisition", acq, driver);
		}
	    
		country.submit();
		loggerContxt.info("country.submit() finished......");
		if (hasCurrentQuote) {
			switchToAlert(true, getClass().getSimpleName());
		}

		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page, this.WAIT_TIME);
		assertTextPresentTrue(prop.getProperty("quote_startdate_label"));
		assertTextPresentTrue(prop.getProperty("quote_expdate_label"));
		return page;
	} 
	
	
	@Override
	public SQOMyCurrentQuotePage createQuote() {

		/*
		 * lob.sendKeys("PAUN"); country.sendKeys("United States");
		 */
		selectedOptionByValue("lob", "PAUN", driver);
		selectedOptionByValue("country", "USA", driver);
		country.submit();
		loggerContxt.info("country.submit() finished......");

		switchToAlert(true, getClass().getSimpleName());

		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;

	}

	public SQOMyCurrentQuotePage createQuote(String lob, String ctry,
			boolean hasCurrentQuote) {

		/*
		 * lob.sendKeys("PAUN"); country.sendKeys("United States");
		 */
		selectedOptionByValue("lob", lob, driver);
		selectedOptionByValue("country", ctry, driver);
		markAsDefault.click();
		country.submit();
		loggerContxt.info(String.format("The selected quote type is :: %s, the selected country is :: %s.", lob,ctry));

		if (hasCurrentQuote) {
			switchToAlert(true, getClass().getSimpleName());
		}

		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;

	}

	public SQOMyCurrentQuotePage createQuote(String lob, String ctry) {

		/*
		 * lob.sendKeys("PAUN"); country.sendKeys("United States");
		 */
		selectedOptionByValue("lob", lob, driver);
		selectedOptionByValue("country", ctry, driver);
		country.submit();
		loggerContxt.info("country.submit() finished......");

		switchToAlert(true, getClass().getSimpleName());

		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;

	}
	
	/*public void createSalesQuote(String lob, String country) {
		
		this.selectOptionByVisibleText("lob", lob);
		this.selectOptionByVisibleText("country", country);
		
		super.continueButton.click();
		
		if (this.alertExists()) {
			this.switchToAlert(true, getClass().getSimpleName());
		}
		this.setMyCurrentQuote();
	}*/
	
	public SQOMyCurrentQuotePage createSalesQuote(String lob, String country) {
		
		this.selectOptionByVisibleText("lob", lob);
		this.selectOptionByVisibleText("country", country);
		
		super.continueButton.click();
		
		if (this.alertExists()) {
			this.switchToAlert(true, getClass().getSimpleName());
		}
		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}

	public SQOMyCurrentQuotePage createQuote(String lob, String acquisition,
			String cntry) {
		

		Map<String, String> param = new HashMap<String, String>();
		param.put("CNTRY_CODE",cntry);
		param.put("PROG_CODE", lob);
		param.put("ACQRTN_CODE",acquisition);

		/*
		 * lob.sendKeys("PAUN"); country.sendKeys("United States");
		 */

/*		 selectedOptionByValue("lob", lob, driver);
		//selenium.select("id=" + "lob", "label=" + lob);
		if (StringUtils.isNotBlank(acquisition)) {
			// selectedOptionByValue("acquisition", acquisition, driver);
			selenium.select("id=" + "acquisition", "label=" + acquisition);
		}
		// selectedOptionByValue("country", cntry, driver);
		selenium.select("id=" + "country", "label=" + cntry);

		country.submit();
		loggerContxt.info("country.submit() finished......");

		// Alert alert = driver.switchTo().alert();
		// loggerContxt.info("driver.switchTo().alert() switched ......" +
		// alert);
		//
		// if (alert != null) {
		// alert.accept();
		// }

		// selenium.getConfirmation()
		// .matches(
		// "^There is currently a quote in the quote worksheet\\. Your existing quote and customer information will be overwritten\\. Are you sure you want to continue[\\s\\S]$");

		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page, this.WAIT_TIME);*/

		return createQuote(param);

	}

}
