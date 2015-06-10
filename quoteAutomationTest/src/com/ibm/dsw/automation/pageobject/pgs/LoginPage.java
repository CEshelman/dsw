package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pao.PAOHomePage;

public class LoginPage extends PGSBasePage {

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public LoginPage(WebDriver driver, Long wait) {
		super(driver);
		loadPage(this, wait);
	}

	@FindBy(id="logonId")
	private WebElement  logonId;
	@FindBy(id="pwd")
	private WebElement  pwd;
	@FindBy(name="jadeAction=signIn")
	private WebElement signInButton;

	public PAOHomePage loginAs(String username, String password) {
		this.logonId.sendKeys(username);
		this.pwd.sendKeys(password);
		this.signInButton.submit();
		PAOHomePage page = new PAOHomePage(driver);
		loadPage(page, this.WAIT_TIME);
	
		return page;
	}
	
	public PGSHomePage login(String username, String password) {
		this.logonId.sendKeys(username);
		this.pwd.sendKeys(password);
		this.signInButton.submit();
		PGSHomePage page = new PGSHomePage(driver);
		loadPage(page, this.WAIT_TIME);
	
		return page;
	}
	
	
	public PGSSiteSelectPage loginAsToSiteSelectPage(String username, String password) {
		this.logonId.sendKeys(username);
		this.pwd.sendKeys(password);
		this.signInButton.submit();
		PGSSiteSelectPage page = new PGSSiteSelectPage(driver);
		loadPage(page, this.WAIT_TIME);
		
		return page;
	}
	

}
