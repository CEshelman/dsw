package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class PriceBookPage extends PGSBasePage {
	
	public PriceBookPage(WebDriver driver) {
		super(driver);
	}
	@FindBy(linkText="IBM distributed software price book")
	public WebElement loginTitle;
	
	public void testIsLogin(String username,String pwd){
		driver.get("https://"+username+":"+pwd+"@w3-117.ibm.com/software/sales/passportadvantage/dswpricebook/PbCfgInternal?E0=0");
		PriceBookPage page = new PriceBookPage(this.driver);
		if(loginTitle.isDisplayed()){
			loggerContxt.info("login Price Book successfully");
		}else{
			loggerContxt.info("login Price Book failed");
		}		 
	}
}

