package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;

public class SpecialBidTabPage extends BasePage {
	
	public SpecialBidTabPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	@FindBy(linkText = "Customers and business partners")
	public WebElement custPartnerTabLink;
	
	@FindBy(xpath = "//input[@title='Parts and pricing']")
	public WebElement partsAndPricingTabLink;
	
	@FindBy(xpath = "//input[@title='Sales information']")
	public WebElement  salesInformationTabLink;
	
	
	@FindBy(xpath = "//input[@title='Special bid']")
	public WebElement  specialBidTabLink;
	
	@FindBy(xpath = "//input[@class='cke_show_borders']")
	public WebElement  judgementCommentText;
	
	
	
	public WebElement  spBidDist;

	
	public void enterSpcialBidInf(String summary) {
		selectedOptionByValue("spBidDist","Federal - Civilian/DHS",driver);
//		String js = "var srcEditor = CKEDITOR.instances['sec0']; srcEditor.setData('"+summary+"');";
		String js = "var allInstances = CKEDITOR.instances, editor; for ( var i in allInstances ){	editor = allInstances[i]; editor.setData('"
			+ summary + "'); }";
		this.executeJavaScript(js);
	}
	
	
	public MyCurrentQuotePage rtnToCPTab(){
		custPartnerTabLink.click();
		MyCurrentQuotePage page = new MyCurrentQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	

	
	
}
