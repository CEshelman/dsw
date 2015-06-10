package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;


public class SQOOverviewPage extends PGSBasePage {
	@FindBy(linkText = "Software quote and order (SQO)")
	public WebElement goSQOLink;
	public SQOOverviewPage(WebDriver driver) {
		super(driver);
	}

	public SQOHomePage gotoSQO() {
		goSQOLink.click();
		SQOHomePage page = new SQOHomePage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}	
	
}
