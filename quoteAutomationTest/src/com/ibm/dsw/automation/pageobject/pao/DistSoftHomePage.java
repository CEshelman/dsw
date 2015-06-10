package com.ibm.dsw.automation.pageobject.pao;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSSiteSelectPage;

public class DistSoftHomePage extends PGSBasePage {
	@FindBy(id="custtype2")
	public WebElement custtype2;
	public DistSoftHomePage(WebDriver driver) {
		super(driver);
	}

	public PGSSiteSelectPage goToPGSSiteSelectPage(String siteNum) {
		elementClickByLinkText("Search Customer/Business Partner by site number");
		waitForElementLoading(5000L);
		// elementClickById("custtype2");
		custtype2.click();
		setValueById("CustNum", siteNum);
		elementSubmitByName("Search");
		return new PGSSiteSelectPage(getDriver());
	}
	
	public PGSSiteSelectPage gotoSelectSiteNumber(){
		return new PGSSiteSelectPage(getDriver());
	}
	
}
