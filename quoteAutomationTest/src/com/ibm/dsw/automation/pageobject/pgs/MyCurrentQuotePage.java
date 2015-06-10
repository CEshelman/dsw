package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.common.TestUtil;
import com.ibm.dsw.automation.pageobject.TabPage;

public class MyCurrentQuotePage extends TabPage{	

	public MyCurrentQuotePage(WebDriver driver) {
		super(driver);
	}

	public WebElement  startDay;
	
	public WebElement  startMonth;
	
	public WebElement  startYear;
	
	public WebElement  expirationDay;
	
	public WebElement  expirationMonth;
	
	public WebElement  expirationYear;
	

	
	
	@FindBy(linkText = "Customers and business partners")
	public WebElement  customersAndBusinessPartnersTabLink;	
	
	@FindBy(linkText = "Parts and pricing")
	public WebElement partsAndPricingTabLink;
	
	@FindBy(linkText = "Sales information")
	public WebElement  salesInformationTabLink;
	
	@FindBy(linkText = "Approval")
	public WebElement  approvalTabLink;	

	@FindBy(linkText = "Find an existing customer")
	public WebElement  findAnExistingCustomerLink;
	
	@FindBy(linkText = "Create a new Passport Advantage Express customer")
	public WebElement  createPassportAdvantageExpressCustomerLink;
	
	@FindBy(linkText = "Select a reseller")
	public WebElement  selectResellerLink;
	
	@FindBy(linkText = "Clear selected reseller")
	public WebElement  clearSelectedResellerLink;
	
	@FindBy(linkText = "Select a distributor")
	public WebElement  selectDistributorLink;
	
	@FindBy(linkText = "Clear selected distributor")
	public WebElement  clearSelectedDistributorLink;
	
	@FindBy(linkText = "Create a sales quote")
	public WebElement  createSaleQuoteLink;
	
	public WebElement  pricingTypeCustomer;
	
	public WebElement  pricingTypePartner;
	
	@FindBy(linkText = "Download quote as rich text file")
	public WebElement  downloadQuoteAsRichTextFileLink;
	
	@FindBy(linkText = "Export quote as spreadsheet")
	public WebElement  exportQuoteAsSpreadsheetLink;
	
	@FindBy(linkText = "Save quote as draft")
	public WebElement  saveQuoteAsDraftLink;
	
	@FindBy(linkText = "Save as new draft quote")
	public WebElement  saveAsNewDraftQuoteLink;
	
	@FindBy(id = "submitForApproval")
	private WebElement  submitForApproval;
	
	public WebElement  cntFirstName;
	public WebElement  cntLastName;
	public WebElement  cntPhoneNumFull;
	public WebElement  cntFaxNumFull;
	public WebElement  cntEmailAdr;
	@FindBy(linkText = "View and make changes")
	public WebElement  viewMakeChanges;
	
	
	public void submitQuote(){
		if  (isElementExist(By.id("submitForApproval"))) {
			elementClickById("submitForApproval");
		} else if (isElementExist(By.id("submitForFinal"))) {
			elementClickById("submitForFinal");
		} 
	}
	
	
	public FindExistingCustomerPage goToFindCustomerTab(){
		findAnExistingCustomerLink.click();
		FindExistingCustomerPage page = new FindExistingCustomerPage(getDriver());
		return page;
	}
	
	public CreateCustomerPage goToCreateCustomerTab(){
		elementClickByLinkText("Create a new customer");
		return new CreateCustomerPage(getDriver());
	}
	
	public CreateNewPassportAdvantageExpressCustomerPage goToCreatePassportAdvantageExpressCustomerTab() {
		if (isTextPresent("Create a new customer")) {
			elementClickByLinkText("Create a new customer");
		} else if (isTextPresent("Create a new Passport Advantage Express customer")) {
			elementClickByLinkText("Create a new Passport Advantage Express customer");
		}
		
		return new CreateNewPassportAdvantageExpressCustomerPage(getDriver());
	}
	
	public HelpAndTutorialPage goToHelpAndTutorialPage() {
		elementClickByLinkText("Help and tutorial");
		return new HelpAndTutorialPage(getDriver());
	}
	
	public SalesInfoTabPage goToSalesInfoTabPage(){
		salesInformationTabLink.click();
		return new SalesInfoTabPage(getDriver());
	}
	
	
	public PartsAndPricingTabPage goToPartsAndPricingTab(){
		partsAndPricingTabLink.click();
		PartsAndPricingTabPage page = new PartsAndPricingTabPage(getDriver());
		return page;
	}
	
	public void fillCustomerContact(String firstNm,String lastNm,String phoneNum,String faxNum,String email){
		cntFirstName.clear();
		cntLastName.clear();
		cntPhoneNumFull.clear();
		cntFaxNumFull.clear();
		cntEmailAdr.clear();
		
		cntFirstName.sendKeys(firstNm);
		cntLastName.sendKeys(lastNm);
		cntPhoneNumFull.sendKeys(phoneNum);
		cntFaxNumFull.sendKeys(faxNum);
		cntEmailAdr.sendKeys(email);
		customersAndBusinessPartnersTabLink.click();
	}
	
	public MyCurrentQuotePage refreshCurrentQt(){
		myCurrentQuoteLink.click();
		MyCurrentQuotePage page = new MyCurrentQuotePage(getDriver());
		return page;
	}
	
	public SelectAResellerPage goToFindResellerTab(){
		selectResellerLink.click();
		SelectAResellerPage page = new SelectAResellerPage(getDriver());
		return page;
	}
	
	public void selectLastDateAsExpirationDate(){
		
		String[] dateArray=TestUtil.setTheLastDateCurrMonth();
		
		loggerContxt.info("select ExpirationDate: Day "+dateArray[2]+"   Month "+dateArray[1]+"  Year "+dateArray[0]);
		selectedOptionByValue("expirationDay",dateArray[2],getDriver());
		selectedOptionByValue("expirationMonth",dateArray[1],getDriver());
		selectedOptionByValue("expirationYear",dateArray[0],getDriver());
		loggerContxt.info("select ExpirationDate end");
	}
	
	public void selectLastDateAsExpirationDateForCRAD(){
		String[] dateArray=TestUtil.setTheLastDateCurrMonth();
		loggerContxt.info("select ExpirationDate: Day "+dateArray[2]+"   Month "+dateArray[1]+"  Year "+dateArray[0]);
		selectedOptionByValue("custReqstdArrivlDay",dateArray[2],getDriver());
		selectedOptionByValue("custReqstdArrivlMonth",dateArray[1],getDriver());
		selectedOptionByValue("custReqstdArrivlYear",dateArray[0],getDriver());
		loggerContxt.info("select ExpirationDate end");
	}
	 
	
	public void selectExpirationDate(int targetAddDays ){
		
		String[] dateArray=TestUtil.addTargetDayFromCurrentDaay(targetAddDays);
		
		loggerContxt.info("select Expiration Date as: Day "+dateArray[2]+" , Month:: "+dateArray[1]+" , Year:: "+dateArray[0]);
		selectedOptionByValue("expirationDay",dateArray[2],getDriver());
		selectedOptionByValue("expirationMonth",dateArray[1],getDriver());
		selectedOptionByValue("expirationYear",dateArray[0],getDriver());
		loggerContxt.info("select ExpirationDate done");
	}
	public void selectReqstdArrivlDate(int targetAddDays ){
		
		String[] dateArray=TestUtil.addTargetDayFromCurrentDaay(targetAddDays);
		
		loggerContxt.info("select ReqstdArrivlDay: Day "+dateArray[2]+"   Month "+dateArray[1]+"  Year "+dateArray[0]);
		if(isElementExsit(By.id("custReqstdArrivlDay"))){
		selectedOptionByValue("custReqstdArrivlDay",dateArray[2],driver);
		selectedOptionByValue("custReqstdArrivlMonth",dateArray[1],driver);
		selectedOptionByValue("custReqstdArrivlYear",dateArray[0],driver);
		}
		
		loggerContxt.info("select ReqstdArrivlDay end");

	

	
	}
	
	public RetrieveSavedSalesQuotePage goRetrieveSavedSalesQuoteTab(){
		createSaleQuoteLink.click();
		waitForElementLoading(new Long(10000));
		retrieveSavedSalesQuoteLink.click();
		RetrieveSavedSalesQuotePage page = new RetrieveSavedSalesQuotePage(getDriver());
		return page;
	}
	
	public CustomerDetailHistoryPage goToCustDetaiHistory(){
		viewMakeChanges.click();
		CustomerDetailHistoryPage page = new CustomerDetailHistoryPage(getDriver());
		return page;
	}
	
	
	public PGSApprovalTabPage goToApprovalTab() {
		approvalTabLink.click();
		PGSApprovalTabPage page = new PGSApprovalTabPage(getDriver());
		return page;
	}
	
	public StatusSearchTabPage gotoStatus() {
		if (isTextPresent("Quote status")) {
			elementClickByLinkText("Quote status");
		} else {
			elementClickByLinkText("Status");
		} 
		
		StatusSearchTabPage page = new StatusSearchTabPage(getDriver());
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	public StatusSearchTabPage gotoStatusByNavigation() {
		elementClickByLinkText("Status");
		StatusSearchTabPage page = new StatusSearchTabPage(getDriver());
		loadPage(page,this.WAIT_TIME);
		return page;
	} 
	
	public void saveQuoteAsDraft(){
		saveQuoteAsDraftLink.click();
	
		new MyCurrentQuotePage(getDriver());
	}
	
	public String getSubmittedQuoteReference() {
		String path = "//html/body/div/div[2]/div/div/div[2]/div/div[2]/div/div/p[2]/span";
		return getElementText(path);
	}
	
	public void clearSelectedResellerLinkClick() {
		clearSelectedResellerLink.click();
	}
	
	public void clearSelectedDistributorLinkClick() {
		clearSelectedDistributorLink.click();
	}
	
	public void downloadQuoteAsRichTextFileLinkClick() {
		downloadQuoteAsRichTextFileLink.click();
		Alert alert = driver.switchTo().alert();
	}
	
	public String getQuoteNum(){
		return 	"";
	}
	
	public void selectOemAgrmntType(){
		selectedOptionByValue("oemAgrmntType", "ZOEM", driver);
	}
	
	public void selectOemBidType(){
		selectedOptionByValue("oemBidType", "1", driver);
	}
	
	public void selectQuoteClassification(){
		selectedOptionByValue("quoteClassfctnCode", "DRCT_NEW_LIC", driver);
	}
}
