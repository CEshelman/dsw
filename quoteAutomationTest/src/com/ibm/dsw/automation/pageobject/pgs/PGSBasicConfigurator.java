package com.ibm.dsw.automation.pageobject.pgs;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class PGSBasicConfigurator extends PGSBasePage {

	public PGSBasicConfigurator(WebDriver driver) {
		super(driver);
	}

	@FindBy(xpath = "//input[@type='text']")
	public List<WebElement> allInputBox;

	public WebElement addConfigrtnButton;

	public void addAllSaasPart(int qty) {
		for (WebElement we : allInputBox) {
			loggerContxt.info("All the input box : " + we.getText());
			we.sendKeys(qty + "");

		}

		addConfigrtnButton.click();
	}

}
