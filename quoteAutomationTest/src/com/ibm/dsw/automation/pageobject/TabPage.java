package com.ibm.dsw.automation.pageobject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class TabPage extends PGSBasePage {

	public TabPage(WebDriver driver) {
		super(driver);
	}

	public void submitQuote(){
		if  (isElementExist(By.id("submitForApproval"))) {
			elementClickById("submitForApproval");
		} else if (isElementExist(By.id("submitForFinal"))) {
			elementClickById("submitForFinal");
		} 
	}
}
