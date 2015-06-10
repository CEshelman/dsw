package com.ibm.dsw.automation.pageobject.sqo;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQOValueUnitCalculotorPage extends PGSBasePage {		
	
	public SQOValueUnitCalculotorPage(WebDriver driver) {
		super(driver);
	}


	
	@FindBy(xpath = "//html/body/div/div[2]/div/div/div[2]/div/div[3]/div/form/table/tbody/tr/td/table/tbody/tr[2]/td/a")
	public WebElement  submitLink;
	
	@FindBy(id = "PRC00058")
	public WebElement  link_PRC00058;
	
	@FindBy(linkText = "Collapse all")
	public WebElement  collapseAllLink;
	
	@FindBy(linkText = "Expand all")
	public WebElement  expandAllLink;
	

	public SQOValueUnitCalculotorGuideTabPage enterPartQty(String partQty) {
		
		anonymousResolutionButtonClick("imgALL");
		waitForElementLoading(new Long(20000));
		link_PRC00058.sendKeys(partQty);
		submitLink.click();
		SQOValueUnitCalculotorGuideTabPage page = new SQOValueUnitCalculotorGuideTabPage(this.driver);
		return page;
	}
	
	
	
	
}
