package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQOCustomerAndPartnerTabPage extends PGSBasePage {

	public SQOCustomerAndPartnerTabPage(WebDriver driver) {
		super(driver);
	}
	
	public ActiveSoftwareAsAServiceTabPage softwareAsAServiceLinkClick() {
		elementClickByLinkText("Software as a Service");
		return new ActiveSoftwareAsAServiceTabPage(getDriver());
	}

}
