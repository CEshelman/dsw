package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQOFindPartsTabPage extends PGSBasePage {
	
	public SQOFindPartsTabPage(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(id = "partNumbers")
	public WebElement  partNumbers;
	@FindBy(id = "partDescription")
	public WebElement  partDescription;
	@FindBy(xpath = "//form/table/tbody/tr[6]/td/span/input")
	public WebElement  partNumSubmit;
	
	@FindBy(xpath = "//form[2]/table/tbody/tr[6]/td/span/input")
	public WebElement  partDescriptionSubmit;
	
	@FindBy(linkText = "Return to draft quote")
	public WebElement  returnToMyCurrentQuoteLink;	
	
	@FindBy(linkText = "Processor Value Unit (PVU) calculator")
	public WebElement  pvuCalculotorLink;	
	
	@FindBy(linkText = "Browse parts")
	public WebElement  browsePartsTabLink;
	
	@FindBy(linkText = "Browse software as a service")
	public WebElement  browseSfAsaServiceTabLink;
	
	
	
	
	
	public SQOFindPartsSelectTabPage findPartsLinkClick(String partList) {
		partNumbers.clear();
		partNumbers.sendKeys(partList);
		partNumbers.submit();
		SQOFindPartsSelectTabPage page = new SQOFindPartsSelectTabPage(this.driver);
		return page;
	}
	
	public SQOFindPartsSelectTabPage findPartsByPartDes(String partList) {
		partDescription.clear();
		partDescription.sendKeys(partList);
		partDescription.submit();
		SQOFindPartsSelectTabPage page = new SQOFindPartsSelectTabPage(this.driver);
		return page;
	}
}

