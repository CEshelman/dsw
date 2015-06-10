package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQODisplayStatusTrackerPage extends PGSBasePage {

	public SQODisplayStatusTrackerPage(WebDriver driver) {
		super(driver);
	}

	public SQOHomePage gotoSQOHomePage() {
		elementClickByLinkText("Software Quote and Order");
		return new SQOHomePage(getDriver());
	}
	
	public SQODisplayStatusTrackerSettingsPage gotoStatusTrackerSettingsPage() {
		elementClickByLinkText("Settings");
		return new SQODisplayStatusTrackerSettingsPage(getDriver());
	}
}
