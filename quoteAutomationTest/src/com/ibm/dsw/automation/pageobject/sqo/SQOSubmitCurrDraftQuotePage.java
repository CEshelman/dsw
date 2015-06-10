package com.ibm.dsw.automation.pageobject.sqo;


import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;

public class SQOSubmitCurrDraftQuotePage extends BasePage {

	public WebElement  chkPAOBlock_yes;
	
	public WebElement  chkPAOBlock_no;
	
	@FindBy(id = "submitForFinal")
	public WebElement submitBtn;
	
	public WebElement  chkEmailAddr;
	public WebElement  custEmailAddr;
	public WebElement  customMsg;
	
	@FindBy(xpath = "//select[contains(@id, 'apprvrLevel')]")
	public List<WebElement> approvers;
	
	
	public final static String  draftQuoteNumPath="/html/body/div/div[2]/div/div/div[2]/div/div[4]/div/div/table/tbody/tr[3]/td[2]";
	                                               
	public SQOSubmitCurrDraftQuotePage(WebDriver driver) {
		super(driver);
	}
	
    private void ChooseFirstApprover(){
    	for(WebElement e : approvers){
    		selectedOptionByIndex(e.getAttribute("id"),"2");
    	}
    }
    
	public SQOSubmittedDraftQuotePage submitWithFirstApprover() {
		ChooseFirstApprover();
		setPAOToNo();

		loggerContxt.info("Select the approver you wish end");
		submitBtn.click();
		return new SQOSubmittedDraftQuotePage(driver);
	}

	public List<String> submitDraftQuote() {
		loggerContxt.info("Select the approver you wish begin");
		List<String> lstApprover = new ArrayList<String>();
		
		// Because of the option value always change, this place use label as the locator.
		if (isElementExsit(By.id("apprvrLevel0"))) { 
			String text = getAttrByElementId("apprvrLevel0", "type");
			if (!"hidden".equals(text)) {
//				selectedOptionByValue("apprvrLevel0", "SaaS - Sterling GST 0%%dlm%dswweb5@us.ibm.com%dlm%DSW Web ID *FUNCTIONAL-ID*%dlm%5", driver);
			    selectedOptionByLabel("apprvrLevel0", "DSW Web ID *FUNCTIONAL-ID* 5(dswweb5@us.ibm.com)");
			}
			lstApprover.add("1");
		}
		
		if (isElementExsit(By.id("apprvrLevel1"))) {
			String text = getAttrByElementId("apprvrLevel1", "type");
			if (!"hidden".equals(text)) {
//				selectedOptionByValue("apprvrLevel1", "Test group for executive summay%dlm%dswweb5@us.ibm.com%dlm%DSW Web ID *FUNCTIONAL-ID*%dlm%5", driver);
				selectedOptionByLabel("apprvrLevel1", "DSW Web ID *FUNCTIONAL-ID* 5(dswweb5@us.ibm.com)");
			}
			lstApprover.add("1");
		}
		
		if (isElementExsit(By.id("apprvrLevel2"))) {
			String text = getAttrByElementId("apprvrLevel2", "type");
			if (!"hidden".equals(text)) {
//				selectedOptionByValue("apprvrLevel2", "Volume Pricer - Midwest 1%dlm%dswweb5@us.ibm.com%dlm%DSW Web ID *FUNCTIONAL-ID*%dlm%5", driver);
				selectedOptionByLabel("apprvrLevel2", "DSW Web ID *FUNCTIONAL-ID* 5(dswweb5@us.ibm.com)");
			}
			lstApprover.add("1");
		}
		
		if (isElementExsit(By.id("apprvrLevel4"))) {
			String text = getAttrByElementId("apprvrLevel4", "type");
			if (!"hidden".equals(text)) {
//				selectedOptionByValue("apprvrLevel4", "DemoGroup1115%dlm%dswweb5@us.ibm.com%dlm%DSW Web ID *FUNCTIONAL-ID*%dlm%5", driver);
				selectedOptionByLabel("apprvrLevel4", "DSW Web ID *FUNCTIONAL-ID* 5(dswweb5@us.ibm.com)");
			}
			lstApprover.add("1");
		}
		
		if (isElementExsit(By.id("apprvrLevel6"))) {
			String text = getAttrByElementId("apprvrLevel6", "type");
			if (!"hidden".equals(text)) {
				selectedOptionByLabel("apprvrLevel6", "DSW Web ID *FUNCTIONAL-ID* 5(dswweb5@us.ibm.com)");
			}
			lstApprover.add("1");
		}
		
		setPAOToNo();
		
		loggerContxt.info("Select the approver you wish end");
		submitBtn.click();
		return lstApprover;
	}

	private void setPAOToNo() {
		if (isElementExsit(By.id("chkPAOBlock_no"))) {
			chkPAOBlock_no.click();
		}
	}
	
	

	
	
	/**
	 * 
	 * Check the "E-mail this quote to the following e-mail address" checkbox,
	 * enter "chris_errichetti@us.ibm.com" in the field next to the checkbox
	 * item, turn on the No radio button for the question
	 * "Would you like to make this available to the customer on Passport Advantage Online?"
	 * , enter "XPRS Testing" in the
	 * "Enter customized text for the quote's cover email" text box and On the
	 * quote submission page, make a select from
	 * "* Would you like to make this available to the customer on Passport Advantage Online? "
	 * if applicable. Press the "Submit" button
	 * 
	 * 
	 * */
	public SQOSubmittedDraftQuotePage submitDraftQuote(String mail, String customizedInf) {
		loggerContxt.info("Select the approver you wish begin");
		
		List<String> lstApprover = new ArrayList<String>();
		
		if (isElementExsit(By.id("apprvrLevel0"))) {
			selectedOptionByValue("apprvrLevel0",
					"AG - Tealeaf - Team Lead%dlm%dswweb5@us.ibm.com%dlm%DSW Web ID *FUNCTIONAL-ID*%dlm%5", driver);
			lstApprover.add("1");
		}
		
		if (isElementExsit(By.id("apprvrLevel1"))) {
			selectedOptionByValue("apprvrLevel1","Test group for executive summay%dlm%dswweb5@us.ibm.com%dlm%DSW Web ID *FUNCTIONAL-ID*%dlm%5", driver);
			lstApprover.add("1");
		}
		
		if (isElementExsit(By.id("apprvrLevel2"))) {
			selectedOptionByValue("apprvrLevel2",
					"Volume Pricer - Midwest 1%dlm%dswweb5@us.ibm.com%dlm%DSW Web ID *FUNCTIONAL-ID*%dlm%5", driver);
			lstApprover.add("1");
		}
		if (isElementExsit(By.id("apprvrLevel3"))) {
			selectedOptionByValue("apprvrLevel3",
					"Volume Pricer - Midwest 1%dlm%dswweb5@us.ibm.com%dlm%DSW Web ID *FUNCTIONAL-ID*%dlm%5", driver);
			lstApprover.add("1");
		}
		
		loggerContxt.info("Select the approver you wish end");

		chkEmailAddr.click();
		custEmailAddr.clear();
		custEmailAddr.sendKeys(mail);
		customMsg.clear();
		customMsg.sendKeys(customizedInf);
		
		setPAOToNo();
	
		submitBtn.click();
		SQOSubmittedDraftQuotePage page = new SQOSubmittedDraftQuotePage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public List<String> submitDraftQuoteForSpecialBid(String mail,String customizedInf) {
		loggerContxt.info("Select the approver you wish begin");
		
		List<String> lstApprover = new ArrayList<String>();
		
		if (isElementExsit(By.id("apprvrLevel0"))) {
			//selectedOptionByValue("apprvrLevel0","SaaS - Unica%dlm%dswweb5@us.ibm.com%dlm%DSW Web ID *FUNCTIONAL-ID*%dlm%5", driver);
			//selectedOptionByValue("apprvrLevel0","SaaS - SmartCloud - Team Lead%dlm%dswweb5@us.ibm.com%dlm%DSW Web ID *FUNCTIONAL-ID*%dlm%5", driver);
			selectedOptionByIndex("apprvrLevel0", "2", driver);
			lstApprover.add("1");
		}
		
		if (isElementExsit(By.id("apprvrLevel1"))) {
			selectedOptionByValue("apprvrLevel1","Test group for executive summay%dlm%dswweb5@us.ibm.com%dlm%DSW Web ID *FUNCTIONAL-ID*%dlm%5", driver);
			lstApprover.add("1");
		}
		
		if (isElementExsit(By.id("apprvrLevel3"))) {
			selectedOptionByValue("apprvrLevel3",
					"Volume Pricer - Midwest 1%dlm%dswweb5@us.ibm.com%dlm%DSW Web ID *FUNCTIONAL-ID*%dlm%5", driver);
			lstApprover.add("1");
		}
		
		if (isElementExsit(By.id("apprvrLevel4"))) {
			selectedOptionByValue("apprvrLevel4","DemoGroup1115%dlm%dswweb5@us.ibm.com%dlm%DSW Web ID *FUNCTIONAL-ID*%dlm%5", driver);
			lstApprover.add("1");
		}
		
		if (isElementExsit(By.id("apprvrLevel6"))) {
			selectedOptionByIndex("apprvrLevel6", "2", driver);
			lstApprover.add("1");
		}
		
		loggerContxt.info("Select the approver you wish end");
		chkEmailAddr.click();
		custEmailAddr.clear();
		custEmailAddr.sendKeys(mail);
		
		setPAOToNo();
	
		customMsg.clear();
		customMsg.sendKeys(customizedInf);
		submitBtn.click();
		
		return lstApprover;
	}
	
	/**
	 * Select the approver as specified, if no correct approver entered, then select 3rd option as default.
	 * @param approverList
	 */
	public void submitDraftQuoteForSpecialBidWithoutEmail(String...approverList) {
		loggerContxt.info("Select the approver begin");
		
		if (approverList == null || approverList.length<1) {
			loggerContxt.error("Please kindly configure a approver for the Special bid approvers");
			return;
		}
		if (isElementExist(By.tagName("select"))) {
			List<WebElement> approvers = driver.findElements(By.tagName("select"));
			for (int i = 0; i < approvers.size(); i++) {
				try{
					if (approverList.length < i) {
						loggerContxt.info(String.format("Select %s as the option for the %d th drop-down list on 'Submit current draft quote' page.", approvers.get(i),i+1));
						selectOptionByVisibleText(approvers.get(i), approverList[0]);
						loggerContxt.info("Action done");
					}else{
						loggerContxt.info(String.format("Select %s as the option for the %d th drop-down list on 'Submit current draft quote' page.", approvers.get(i),i+1));
						selectOptionByVisibleText(approvers.get(i), approverList[i]);
						loggerContxt.info("Action done");
					}
				}catch(Exception e){
					loggerContxt.info(String.format("Select third option for the drop-down list on 'Submit current draft quote' page."));
					selectOptionByIndex(approvers.get(i), 3);
					loggerContxt.info("Action done");
				}
			}
		}
		
		loggerContxt.info("Select the approver you wish end");
		
		setPAOToNo();
		if (isElementExist(By.id("chkNoTax"))) {
			loggerContxt.info("Check the checkbox 'Do NOT show tax on the quote output' for 'Quote output' on 'Submit current draft quote' page.");
			driver.findElement(By.id("chkNoTax")).click();
			loggerContxt.info("Action done");
		}
		if (isElementExist(By.id("chkEmailY9PartnerAddrList_no"))) {
			driver.findElement(By.id("chkEmailY9PartnerAddrList_no")).click();
		}
		loggerContxt.info("Click the submit button with approver selected on 'Submit current draft quote' page.");
		submitBtn.click();
		loggerContxt.info("Action done");
		
	}
	
	
	public  String getQuoteNum(){
		waitForElementLoading(3000L);
		String quoteNum=getDriver().findElement(By.xpath(draftQuoteNumPath)).getText();
		loggerContxt.info("the quote number is ..."+quoteNum);
		return quoteNum	;
		
	}
	
	
	
}
