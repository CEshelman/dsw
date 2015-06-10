package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;


public class SubmitSQSalesInfoTabPage extends PGSBasePage
{

	public SubmitSQSalesInfoTabPage(WebDriver driver) {
		super(driver);
	}
	
	public PGSApprovalTabPage goToApprovalTab() {
		elementClickByLinkText("Approval");
		PGSApprovalTabPage page = new PGSApprovalTabPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
    
}
