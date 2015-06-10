package com.ibm.dsw.automation.pageobject.sqo;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;


public class SQODisplayCustListPage extends PGSBasePage {
	@FindBy(linkText = "Return to my current quote ")
	public WebElement returnCurQuoteLink;
	
	@FindBy(linkText = "Change search criteria")
	public WebElement changeSearchCriLink;
	
	@FindBy(partialLinkText = "Select customer")
	public WebElement selectCustLink;
	
	@FindBy(linkText = "View customer details and history")
	public WebElement viewCustDetailLink;
	
	@FindBy(xpath="//table/tbody/tr/td[9]/a")
	private WebElement firstDispalyCustomerLink;

	@FindBy(xpath="//html/body/div/div[2]/div/div/div[2]/div/div[4]/div/table/tbody/tr[6]/td[9]/a")
	private WebElement selectCustomer11520;
	
	public SQODisplayCustListPage(WebDriver driver) {
		super(driver);
	}

	public SQOMyCurrentQuotePage selectCustomer() {
		loggerContxt.info("Select the first customer displayed in the 'Customer selection and creation' page.");
		selectCustLink.click();
		loggerContxt.info("action done.");
		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		loggerContxt.info("'My current quote' page loaded successfully.");
		return page;
	}
	
	public SQOMyCurrentQuotePage selectFirstCustomer() {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		firstDispalyCustomerLink.click();
		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	public SQOMyCurrentQuotePage clickCustomer11520(){
		selectCustomer11520.click();
		loggerContxt.info("click Select Customer");
		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
}
