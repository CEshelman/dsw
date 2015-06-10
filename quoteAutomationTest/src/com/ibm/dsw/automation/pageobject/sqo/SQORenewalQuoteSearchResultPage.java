package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;

public class SQORenewalQuoteSearchResultPage extends BasePage{

	public SQORenewalQuoteSearchResultPage(WebDriver driver) {
		super(driver);
	}
	
	private SQORenewalQuoteSearchResultDetailsPage RQSearchResultDetailsPage = null;
	
	@FindBy(linkText="Change search criteria")
	public WebElement changeSearchCriteriaLink;
	@FindBy(linkText="Download report")
	public WebElement downloadReportLink;
	@FindBy(linkText="Printable version")
	public WebElement printableVersionLink;
	//First result link in the 'Selected renewal quotes' page
	@FindBy(xpath=".//tbody/tr[2]/td/a")
	public WebElement firstResultLink;
	
	public void viewQuoteDetailsByNumber(String number){
		WebElement ele = driver.findElement(By.linkText(number));
		ele.click();
		this.setRQSearchResultDetailsPage();
	}
	
	public void viewFirstQuoteDetails(){
		firstResultLink.click();
		this.setRQSearchResultDetailsPage();
	}
	
	public SQORenewalQuoteSearchResultPage newCache(){
		SQORenewalQuoteSearchResultPage page = new SQORenewalQuoteSearchResultPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public SQORenewalQuoteSearchTabPage changeCriteria(){
		changeSearchCriteriaLink.click();
		SQORenewalQuoteSearchTabPage page = new SQORenewalQuoteSearchTabPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public SQORenewalQuoteSearchResultDetailsPage displaySearchResultDetails(){
		SQORenewalQuoteSearchResultDetailsPage page = new SQORenewalQuoteSearchResultDetailsPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}

	public SQORenewalQuoteSearchResultDetailsPage getRQSearchResultDetailsPage() {
		return RQSearchResultDetailsPage;
	}

	public void setRQSearchResultDetailsPage(
			SQORenewalQuoteSearchResultDetailsPage rQSearchResultDetailsPage) {
		RQSearchResultDetailsPage = rQSearchResultDetailsPage;
	}
	
	public void setRQSearchResultDetailsPage() {
		RQSearchResultDetailsPage = this.displaySearchResultDetails();
	}

}
