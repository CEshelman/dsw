package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;

public class HelpAndTutorialPage extends PGSBasePage {

	public HelpAndTutorialPage(WebDriver driver) {
		super(driver);
	}

	public MyCurrentQuotePage goToMyCurrentQuotePage() {
		elementClickByLinkText("My current quote");
		MyCurrentQuotePage page = new MyCurrentQuotePage(driver);
		return page;
	}
}
