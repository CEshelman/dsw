package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class PGSJumpPage extends PGSBasePage {

	@FindBy(xpath = "//html/body/form/input[4]")
	private WebElement submitQuery;
	
	
	public PGSJumpPage(WebDriver driver, Long wait) {
		super(driver);
	}

	public PGSHomePage login(String customerNum, String bpNum) {
		
		loggerContxt.debug("customerNum:" + customerNum + " bpNum:" + bpNum);
//		selectedOptionByValue("custNumber", customerNum, driver);
		setValueById("inputCustNumber", bpNum);
		submitQuery.submit();
		
		PGSHomePage page = new PGSHomePage(driver);
		return page;
	}
	
}
