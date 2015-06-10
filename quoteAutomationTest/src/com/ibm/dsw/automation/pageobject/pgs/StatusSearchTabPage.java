package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class StatusSearchTabPage extends PGSBasePage {
	
	public StatusSearchTabPage(WebDriver driver) {
		super(driver);
	}
	
	public WebElement lob_filter1;
	public WebElement lob_filter2;
	
	@FindBy(linkText = "Select all statuses")
	public WebElement selectAllStatus;
	public WebElement timeFilter36500;
	public WebElement markFilterDefault;
	public WebElement timeFilterQuarter;
	

	
	@FindBy(linkText = "Find by number")
	public WebElement quoteNumTabLnk;

	
	@FindBy(xpath = "//input[@name='ibm-submit']")
	public WebElement ibm_submit;
	
	public WebElement quoteTypeFilter1;
	public WebElement ownerRoles0;
	
	public DisplayStatusSearchReslutPage goDispAllQuoteReslt() {
		
		anonymousResolutionButtonClick("lob_filter1");
		anonymousResolutionButtonClick("lob_filter2");
		selectedOptionByValue("sortFilter","0",driver);
		selectAllStatus.click();	
//		timeFilter36500.click();
		ibm_submit.click();
		DisplayStatusSearchReslutPage page = new DisplayStatusSearchReslutPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public DisplayStatusSearchReslutPage goDispQuoteResltByQuoteNum(String quoteNum) {
		quoteNumTabLnk.click();
		waitForElementLoading(new Long(10000));
		driver.findElement(By.id("number")).sendKeys(quoteNum);
		ibm_submit.click();
		DisplayStatusSearchReslutPage page = new DisplayStatusSearchReslutPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public DisplayStatusSearchReslutPage goDispQuoteByQuoteNum(String quoteNum) {
		anonymousResolutionButtonClick("lob_filter1");
		anonymousResolutionButtonClick("lob_filter2");
		selectedOptionByValue("sortFilter","0",driver);
		selectAllStatus.click();
		quoteNumTabLnk.click();
		waitForElementLoading(new Long(10000));
		driver.findElement(By.id("number")).sendKeys(quoteNum);
		ibm_submit.click();
		DisplayStatusSearchReslutPage page = new DisplayStatusSearchReslutPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public UnderEvaluationTabPage goToUnderEvaluationTab() {
		elementClickByLinkText("Under evaluation");
		return new UnderEvaluationTabPage(driver);
	}
}
