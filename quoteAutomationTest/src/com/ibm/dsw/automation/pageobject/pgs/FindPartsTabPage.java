package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class FindPartsTabPage extends PGSBasePage {		
	
	public FindPartsTabPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	@FindBy(linkText = "Return to draft quote")
	public WebElement  returnToDraftQuoteLink;
	
	@FindBy(linkText = "Browse parts")
	public WebElement  browsePartsTabLink;
	
	@FindBy(linkText = "Find parts")
	public WebElement  findPartsTabLink;
	
	@FindBy(linkText = "Browse software as a service")
	public WebElement  browseSoftwareAsAServiceTabLink;
	
	public WebElement  partNumbers;
	
	public WebElement  partDescription;
	
	@FindBy(name="ibm-submit")
	public WebElement  submitButton;
	
	
	public PGSFindPartsSelectTabPage findPartsLinkClick(String partList) {
		partNumbers.sendKeys(partList);
		partNumbers.submit();
		PGSFindPartsSelectTabPage page = new PGSFindPartsSelectTabPage(this.driver);
		return page;
	}
	
	public PGSFindPartsSelectTabPage findPartsByPartDes(String partList) {
		partDescription.clear();
		partDescription.sendKeys(partList);
		partDescription.submit();
		PGSFindPartsSelectTabPage page = new PGSFindPartsSelectTabPage(this.driver);
		return page;
	}
}
