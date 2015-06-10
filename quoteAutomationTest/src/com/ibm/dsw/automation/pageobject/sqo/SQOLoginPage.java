package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQOLoginPage extends PGSBasePage {

	public SQOLoginPage(WebDriver driver) {
		super(driver);
	}


	@FindBy(id="logonId")
	private WebElement logonId;
	@FindBy(name="j_password")
	private WebElement logonPassword;

	@FindBy(name = "ibm-submit")
	private WebElement signinButton;
	
	@FindBy(id="pwd")
	private WebElement pgsLogonPassword;
	
	@FindBy(name="jadeAction=signIn")
	private WebElement pgsSingnButton;

	public SQOHomePage loginAs(String username, String password) {
		if(driver.getCurrentUrl().endsWith("jump.html")){
			
			jumpIn("US", username, "1");
		}else{
		this.logonId.sendKeys(username);
		this.logonPassword.sendKeys(password);
		this.logonPassword.submit();
		}
		
		if (isTextPresent("Software quote and order (SQO)")) {
		elementClickByLinkText("Software quote and order (SQO)");
		}
		waitForElementLoading(2500L);
		// this.signinButton.submit();
		SQOHomePage page = new SQOHomePage(driver);
		loadPage(page,this.WAIT_TIME);

		return page;
	}
	
	public SQOHomePage loginAs(String username, String password,String env) {
		if(driver.getCurrentUrl().endsWith("jump.html")){
			
			jumpIn("US", username, "1");
		}else{
			this.logonId.sendKeys(username);
			this.logonPassword.sendKeys(password);
			this.signinButton.submit();
			
		}
		// this.signinButton.submit();
		SQOHomePage page = new SQOHomePage(driver);
		loadPage(page,this.WAIT_TIME);

		return page;
	}
	
	public SQOHomePage loginPgsViaSqo(String username, String password) {
		this.logonId.sendKeys(username);
		this.pgsLogonPassword.sendKeys(password);
		this.pgsSingnButton.submit();
		
		SQOHomePage page = new SQOHomePage(driver);
		loadPage(page,this.WAIT_TIME);
		
		return page;
	}
	
	public void jumpIn(String cntryCode, String user, String tsAccessLevel) {
		SQOJumpPage jumpPage = new SQOJumpPage(driver);
		jumpPage.loginIn(cntryCode, user, tsAccessLevel);
	}
	
	

}
