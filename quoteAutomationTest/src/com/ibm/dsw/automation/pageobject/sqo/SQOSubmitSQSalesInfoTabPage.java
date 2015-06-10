package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;

public class SQOSubmitSQSalesInfoTabPage extends SQOSpecialBidTabPage
{

	public SQOSubmitSQSalesInfoTabPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
	
	public SQOSubmitSQSpecialBidTabPage  goToSepcialBidTab() {
		specialBidTabLink.click();
		SQOSubmitSQSpecialBidTabPage page = new SQOSubmitSQSpecialBidTabPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
    
   
}
