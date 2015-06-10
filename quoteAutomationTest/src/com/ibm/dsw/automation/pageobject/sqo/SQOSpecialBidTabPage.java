package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.TabPage;

public class SQOSpecialBidTabPage extends TabPage {
	
	public SQOSpecialBidTabPage(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(linkText="Save quote as draft")
	public WebElement saveAsDraft;
	
	@FindBy(linkText="Export quote as spreadsheet to be used in Excel")
	public WebElement exportAsExcel;
	
	@FindBy(linkText="Export quote as spreadsheet to be used in Symphony")
	public WebElement exportAsSymphony;

	@FindBy(linkText = "Parts and pricing")
	public WebElement partsAndPricingTabLink;
	
	@FindBy(linkText = "Sales information")
	public WebElement  salesInformationTabLink;
	
	@FindBy(linkText = "Customers and partners")
	public WebElement custPartnerTabLink;
	
	@FindBy(linkText = "Special bid")
	public WebElement  specialBidTabLink;

	//@FindBy(name = "addAttachBtn")
	//@FindBy(xpath = "//html/body/div/div[2]/div/div/div[2]/div/form/table/tbody/tr[4]/td/div[3]/div[3]/div/p/a")
	@FindBy(partialLinkText="Add Attachment")
	public WebElement  addAttachLink;
	
	
	@FindBy(xpath = "//input[@class='cke_show_borders']")
	public WebElement  judgementCommentText;
	
	@FindBy(id = "sec0_val")
	public WebElement justificationSummary;
	
	@FindBy(id = "submitForApproval")
	public WebElement submit;
	
	public WebElement  spBidDist;

	public SQOSpecialBidTabPage clickSaveAsDraftLink(){
		saveAsDraft.click();
		SQOSpecialBidTabPage page = new SQOSpecialBidTabPage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	public void enterSpcialBidInf(String summary) {
		
		loggerContxt.info("Select the option 'East' as the result of drop-down list 'Special bid region:' on 'Special bid' tab.");
		selectedOptionByValue("spBidRgn", "East",driver);
		loggerContxt.info("Action done");
		loggerContxt.info("Select the option 'Pacific 1 - CA North' as the result of drop-down list 'Special bid district:' on 'Special bid' tab.");
		selectedOptionByValue("spBidDist", "Pacific 1 - CA North",driver);
		loggerContxt.info("Action done");
		
		loggerContxt.info(String.format("Send keys '%s' to 'Justification Summary*' field on 'Sales information' tab.", summary));
		String js = "var allInstances = CKEDITOR.instances, editor; for ( var i in allInstances ){	editor = allInstances[i]; editor.setData('"
			+ summary + "'); }";
//		String js = "var srcEditor = CKEDITOR.instances['sec0']; srcEditor.setData('"+summary+"');";
		
		this.executeJavaScript(js);
		loggerContxt.info("Action done");
	}
	
	public void enterSpcialBidInfo(String summary) {
		
		this.selectedOptionByIndex(By.id("spBidDist"), 0);
		
		String js = "var allInstances = CKEDITOR.instances, editor; for ( var i in allInstances ){	editor = allInstances[i]; editor.setData('"
				+ summary + "'); }";
		
//		String js = "var srcEditor = CKEDITOR.instances['sec0']; srcEditor.setData('"+summary+"');";
		
		this.executeJavaScript(js);
	}
	
	public void enterSpcialBidInf(){
		justificationSummary.sendKeys("enter some justification summary for test");
	}
	public void enterSpcialBidInf4wi38075(String justificationSummary) {
		
		String jsJudgement = "var allInstances = CKEDITOR.instances, editor, doc; for ( var i in allInstances ){	editor = allInstances[i]; editor.setData('"
			+ justificationSummary + "'); }";
		
		this.executeJavaScript(jsJudgement);
		String jsComment="var srcEditor = CKEDITOR.replace('tnc_comments_val'); srcEditor.setData('"+justificationSummary+"');";
		this.executeJavaScript(jsComment);

	}
	
	public SQOMyCurrentQuotePage rtnToCPTab(){
		loggerContxt.info("Click on 'Customers and partners' tab on 'My current quote' page.");
		custPartnerTabLink.click();
		loggerContxt.info("Action done");
		SQOMyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		loggerContxt.info("Navigate to 'Customers and partners' tab on 'My current quote' page.");
		return page;
	}
	
	

	public void uploadFile(String file){
		
		addAttachLink.click();
		
		SQOAttachSupportPage attachsupport = new SQOAttachSupportPage(driver);
		loadPage(attachsupport,this.WAIT_TIME);
		attachsupport.uploadFile(file);
		
	}

	
	public SQOSpecialBidTabPage comeToThisPage(String loginUser, String loginCntry, String accessLevel, String quoteType, String acquisition, String country) {

		SQOJumpPage jumpPage = new SQOJumpPage(driver);
		jumpPage.loginIn(loginCntry, loginUser, accessLevel);

		SQOHomePage sqoHomePage = new SQOHomePage(driver);

		loggerContxt.debug("go to sqo home page finished.....");
		SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();

		// Create a SQO quote
		SQOMyCurrentQuotePage currentQuotePage = cq.createQuote(quoteType, acquisition, country);
		loggerContxt.debug("create quote page finished.....");

		SQOSpecialBidTabPage sbTab = currentQuotePage.goToSpecialBidTab();

		return sbTab;
	}

	public void saveQuoteAsExcel(){
		
		exportAsExcel.click();
		loggerContxt.info("click Export quote as spreadsheet to be used in Excel ");
//		
//		
//		
//		exportAsExcel.click();
//		
//		Runtime  r = Runtime.getRuntime(); 
//		try {
//			r.exec("C:\\install\\AutoIt3\\AutoIt3.exe C:\\dsw7\\quote\\WebQuote_Head_Stream\\quoteAutomationTest\\src\\com\\ibm\\dsw\\automation\\util\\download.au3");
//			System.out.println("exectue script for download.........");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public void saveQuoeAsSymphony(){
		exportAsSymphony.click();
		loggerContxt.info("click Export quote as spreadsheet to be used in Excel ");
	}

	
	
}
