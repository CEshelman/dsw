package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQODisplayApprovalQueueTrackerPage extends PGSBasePage {

	public SQODisplayApprovalQueueTrackerPage(WebDriver driver) {
		super(driver);
	}

	public SQOHomePage gotoSQOHomePage() {
		elementClickByLinkText("Software Quote and Order");
		return new SQOHomePage(getDriver());
	}
}
