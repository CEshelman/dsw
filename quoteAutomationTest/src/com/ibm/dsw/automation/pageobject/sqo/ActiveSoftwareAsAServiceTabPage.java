package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;
import com.ibm.dsw.automation.pageobject.pgs.ServiceConfigurePage;

public class ActiveSoftwareAsAServiceTabPage extends PGSBasePage {

	public ActiveSoftwareAsAServiceTabPage(WebDriver driver) {
		super(driver);
	}
	
	public ServiceConfigurePage updateConfigurationLinkClick() {
		elementClickByXPath("/html/body/div/div[2]/div/div/div[2]/div/div/div[3]/table/tbody/tr[5]/td[7]/table/tbody/tr/td/a");
		
		if (isElementExist(By.name("ibm-cancel"))) {
			elementClickByName("ibm-cancel");
		}
		waitForElementLoading(25000L);
		
		getDriver().switchTo().frame("cpqIframeId1");
		ServiceConfigurePage scPage = new ServiceConfigurePage(getDriver());
		return scPage;
	}
}
