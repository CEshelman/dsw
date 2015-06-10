package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;

public class PartAndPricingDetailsPage extends PGSBasePage {

	public PartAndPricingDetailsPage(WebDriver driver) {
		super(driver);
	}

	public void close() {
		getDriver().close();
	}
}
