package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;
/**
 * Use SQOLoginPage.jumpIn instead.
 * 
 * @author will
 * 
 */
@Deprecated
public class SQOJumpPage extends PGSBasePage {

	public SQOJumpPage(WebDriver driver) {
		super(driver);
	}
	
	private Select cntryCodeSelect =  new Select(driver.findElement(By.name("cntryCode")));
	          
	private Select userSelect =  new Select(driver.findElement(By.name("user")));
	
	@FindBy(name = "tsAccessLevel")
	private WebElement tsAccessLevelSelect;
	
	
	public void loginIn(String cntryCode,String user,String tsAccessLevel) {
		
		loggerContxt.debug("cntryCode:" + cntryCode + " user:" + user + " tsAccessLevel:" + tsAccessLevel);
		cntryCodeSelect.selectByValue(cntryCode);
		userSelect.selectByValue(user);
		tsAccessLevelSelect.sendKeys(tsAccessLevel);
		tsAccessLevelSelect.submit();
	}
	
	
	

	
}
