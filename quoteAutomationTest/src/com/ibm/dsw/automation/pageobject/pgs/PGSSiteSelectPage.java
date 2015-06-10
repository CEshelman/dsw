package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.ibm.dsw.automation.pageobject.pao.PAOHomePage;


public class PGSSiteSelectPage extends PGSBasePage {
	
	
	public PGSSiteSelectPage(WebDriver driver) {
		super(driver);
	}
	

	public PAOHomePage gotoPAOHomePage(String siteNum) {
		driver.findElement(By.linkText(siteNum));
		PAOHomePage page = new PAOHomePage(driver);
		return page;
	}	
	
	public PAOHomePage gotoPAOHomePage(String format, String linkText) {
		String strElmXPath = String.format(format,linkText);
		elementClickByXPath(strElmXPath);
		PAOHomePage page = new PAOHomePage(driver);
		return page;
	}	
	
}
