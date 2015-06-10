package com.ibm.dsw.automation.pageobject.sqo;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;

public class SQOSalesInfoTabPage extends BasePage {
	
	@FindBy(linkText = "Customers and partners")
	public WebElement custPartnerTabLink;
	
	
	@FindBy(linkText = "Parts and pricing")
	public WebElement partsAndPricingTabLink;
	
	@FindBy(linkText = "Sales information")
	public WebElement  salesInformationTabLink;
	
	
	@FindBy(linkText = "Special bid")
	public WebElement  specialBidTabLink;
	@FindBy(id = "briefTitle")
	public WebElement  briefTitle;
	@FindBy(id = "quoteDesc")
	public WebElement  quoteDesc;
	public WebElement  sionSiebelSel;
	public WebElement  sionSiebel;
	@FindBy(id="sionCustInit")
	public WebElement  sionCustInit;
	@FindBy(id="delegateId")
	public WebElement  delegateId;
	public WebElement  opprtntyNum;
	public WebElement  oppOwnerEmailAddr;
	@FindBy(linkText="Add editor")
	public WebElement  addEditor;
	@FindBy(xpath = "//a[@title='Save quote as draft']")
	public WebElement  saveAsDraftLink;
	
	
	public SQOSalesInfoTabPage(WebDriver driver) {
		super(driver);
	}
	
	public void enterSalesInf(String title,String desc) {
		briefTitle.sendKeys(title);
		quoteDesc.sendKeys(desc);
		sionCustInit.click();
		selectedOptionByValue("busOrgCode","03",driver);
	}
	
	
	public void enterSalesInf(String title,String desc,String bizOrgCd ) {
		loggerContxt.info("Clear the content in 'Enter a brief title: *' field in 'Sales information' tab.");
		briefTitle.clear();
		loggerContxt.info("Action done");
		loggerContxt.info(String.format("Send keys '%s' to 'Enter a brief title: *' field in 'Sales information' tab.", title));
		briefTitle.sendKeys(title);
		loggerContxt.info("Clear the content in 'Full description:' field in 'Sales information' tab.");
		quoteDesc.clear();
		loggerContxt.info("Action done");
		loggerContxt.info(String.format("Send keys '%s' to 'Full description:' field in 'Sales information' tab.", desc));
		quoteDesc.sendKeys(desc);
		loggerContxt.info("Action done");
		if(!sionCustInit.isSelected()){
			loggerContxt.info("Check the radio 'Acquisitions and Divestitures (exemption code 70)' on 'Sales information' page");
			sionCustInit.click();
			loggerContxt.info("Action done");
		}
		loggerContxt.info(String.format("Select the option '%s' for 'Business organization: *' field on 'Sales information' page.", bizOrgCd));
		selectedOptionByValue("busOrgCode",bizOrgCd,driver);
		loggerContxt.info("Action done");
		loggerContxt.info("input sales info tab input title="+title+"desc="+desc+"bizOrgCd="+bizOrgCd+"");
	}
	
	public void editCopyQtSalesInf(String title,String desc,String opprtnty_num,String exemptn_code,String opprtnty_ownr_email_adr ) {
	  
		briefTitle.clear();
		briefTitle.sendKeys(title);
		quoteDesc.clear();
		quoteDesc.sendKeys(desc);
		if(!StringUtils.isBlank(opprtnty_num)){
			
			sionSiebel.click();
			opprtntyNum.sendKeys(opprtnty_num);
		}
		if(!StringUtils.isBlank(exemptn_code)){
			sionCustInit.click();
		}
			
		oppOwnerEmailAddr.clear();
		oppOwnerEmailAddr.sendKeys(opprtnty_ownr_email_adr);
		
		//TODO
		selectedOptionByValue("busOrgCode","03",driver);
	
	}
	/**
	 * */
	public void addQuoteEditor(String intranet ) {
		if  (isElementExist(By.id("delegateId"))) {
			loggerContxt.info(String.format("Send keys '%s' to 'New editor's e-mail address:' field on 'Sales information' page.", intranet));
			delegateId.sendKeys(intranet);
			loggerContxt.info("Action done");
			loggerContxt.info("Click on the 'Add editor' link on 'Sales information' tab on 'My current quote' page.");
			addEditor.click();
			loggerContxt.info("Action done");
		}
	}
	
	/**
	 * */
	public SQOSalesInfoTabPage saveDraftQuoteLink( ) {
		loggerContxt.info("Click on 'Save quote as draft' link on 'Sales information' tab on 'My current quote' page.");
		saveAsDraftLink.click();
		loggerContxt.info("Action done");
	 return new SQOSalesInfoTabPage(getDriver());
	}
	
	public SQOSpecialBidTabPage goToSpecialBidTabClick() {
		loggerContxt.info("Click on 'Special bid' tab on 'My current quote' page.");
		specialBidTabLink.click();
		loggerContxt.info("Action done");
		SQOSpecialBidTabPage page = new SQOSpecialBidTabPage(this.driver);
		loadPage(page,this.WAIT_TIME);
		loggerContxt.info("Navigate to 'Special bid' tab.");
		return page;
	}
	
	
	public PartsAndPricingTabPage goToPartsAndPricingTab(){
		partsAndPricingTabLink.click();
		PartsAndPricingTabPage page = new PartsAndPricingTabPage(driver);
		return page;
	}
	
	
	public SQOMyCurrentQuotePage rtnToCPTab(){
		custPartnerTabLink.click();
		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		loggerContxt.info("return to customer and partners");
		return page;
	}
}
