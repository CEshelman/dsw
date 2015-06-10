package com.ibm.dsw.automation.pageobject.sqo;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQORQSearchReslutPage extends PGSBasePage {
	
	public SQORQSearchReslutPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(linkText = "Change search criteria")
	public WebElement chgSearchCritLink;
	
//	@FindBy(xpath = "//html/body/div/div[2]/div/div/div[2]/div/table/tbody/tr[3]/td/a")
	@FindBy(xpath = "//a[contains(@href, 'QUOTE_NUM=')]")
	public List<WebElement> viewDetailLink;
	
	public SQORQSummaryTabPage goDispQuoteReslt() {
		
		
		viewDetailLink.get(viewDetailLink.size()/3).click();
		SQORQSummaryTabPage page = new SQORQSummaryTabPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public SQORQSummaryTabPage gotoRQSummaryTabPage(String number) {
		try{
			elementClickByLinkText(number);
		}catch(Exception e){
			loggerContxt.error(String.format("The view quote number::%s doesnot exist..",number));
			return null;
		}
		SQORQSummaryTabPage page = new SQORQSummaryTabPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	} 
}
