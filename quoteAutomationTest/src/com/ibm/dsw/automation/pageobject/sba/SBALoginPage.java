package com.ibm.dsw.automation.pageobject.sba;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;

public class SBALoginPage extends BasePage{

	public SBALoginPage(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(xpath="//input[@value='Submit']")
	private WebElement submitQuery;
	@FindBy(id="logonId")
	private WebElement logonIdInput;
	@FindBy(id="logonPassword")
	private WebElement logonPasswordInput;
	
	public void submitQuery(){
		submitQuery.click();
	}
	
	public SQOAdministrationPage submitQueryByJump(){
		submitQuery();
		SQOAdministrationPage page = new SQOAdministrationPage(getDriver());
		loadPage(page, WAIT_TIME);
		return page;
	}
	
	public SQOAdministrationPage submitQueryViaWebAuth(String user, String password){
		logonIdInput.sendKeys(user);
		logonPasswordInput.sendKeys(password);
		submitQuery();
		SQOAdministrationPage page = new SQOAdministrationPage(getDriver());
		loadPage(page, WAIT_TIME);
		return page;
	}

}
