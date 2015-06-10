package com.ibm.dsw.automation.pageobject.sqo;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQOValueUnitCalculotorGuideTabPage extends PGSBasePage {		
	
	public SQOValueUnitCalculotorGuideTabPage(WebDriver driver) {
		super(driver);
	}


	
	@FindBy(xpath = "//html/body/div/div[2]/div/div/div[2]/div/div[2]/div/form/table[3]/tbody/tr/td/a")
	public WebElement  applyAndReturnLink;
	


	public SQOPartsAndPricingTabPage applyAndReturn() {

		applyAndReturnLink.click();
		SQOPartsAndPricingTabPage page = new SQOPartsAndPricingTabPage(this.driver);
		return page;
	}
	
	
	
	
}
