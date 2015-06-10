package com.ibm.dsw.automation.pageobject.pgs;


import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class PGSMyCurrentQuotePage extends MyCurrentQuotePage {
	

	@FindBy(linkText = "Find an existing customer")
	public WebElement  findAnExistingCustomerLink;
	
	public WebElement  cntEmailAdr;
	
	//@FindBy(xpath = "//form/table[3]/tbody/tr[21]/td/input")
	
	@FindBy(linkText = "Select a tier 1 or tier 2 reseller")
	public WebElement  findResellerLink;
		 					
	@FindBy(linkText = "Select a distributor")
	public WebElement  findDistributorLink;
	
	
	@FindBy(linkText = "Parts and pricing")
	public WebElement partsAndPricingTabLink;
	
	@FindBy(linkText = "Sales information")
	public WebElement  salesInformationTabLink;
	
	
	@FindBy(linkText = "Special bid")
	public WebElement  specialBidTabLink;
	
	public WebElement fullfillmentSrcDirect;
	
	@FindBy(id = "submitForApproval")
	public WebElement submitQuote;
	
	@FindBy(linkText = "Renewal quotes")
	public WebElement renewalQuote;
	
	
	public PGSMyCurrentQuotePage(WebDriver driver) {
		super(driver);
	}
	
	@Override
	public PartsAndPricingTabPage goToPartsAndPricingTab(){
		partsAndPricingTabLink.click();
		PartsAndPricingTabPage page = new PartsAndPricingTabPage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	public SpecialBidTabPage goToSpecialBidTab() {
		specialBidTabLink.click();
		SpecialBidTabPage page = new SpecialBidTabPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public void fillEmailAdr(String mail){
		cntEmailAdr.clear();
		cntEmailAdr.sendKeys(mail);

	}
	
	public void selectDirectChnl(){
		loggerContxt.info("select Direct Chanel start......");
		fullfillmentSrcDirect.click();
		loggerContxt.info("select Direct Chanel finished......");

		Alert alert = driver.switchTo().alert();

		loggerContxt.info("driver.switchTo().alert() switched ......" + alert);
		
		if (alert != null) {
			alert.accept();
		}
	
	}
	
	public void selectExpirationDate(String expDay,String expMonth,String expYear){
		
		loggerContxt.info("select ExpirationDate: Day"+expDay+"   Month"+expMonth+"  Year"+expYear);
		selectedOptionByValue("expirationDay",expDay,driver);
		selectedOptionByValue("expirationMonth",expMonth,driver);
		selectedOptionByValue("expirationYear",expYear,driver);
		loggerContxt.info("select ExpirationDate end");
	}
	
	public void selectExpirationDate(int expDay,int expMonth,int expYear){
		selectExpirationDate(String.valueOf(expDay),String.valueOf(expMonth),String.valueOf(expYear));
	}
	
	public void selectCurrentDate(){
		WebElement dueFromDateSelect = driver.findElement(By.id("DueFromDateSelect"));
		dueFromDateSelect.click();
		
		WebElement today = driver.findElement(By.xpath("/html/body/div[5]/table/thead/tr[2]/td[3]/div"));
		today.click();
	}
	
	
//	public SQOCurrDraftQuotePage submitCurrentDraftQuote(){
//		submitQuote.click();
//		SQOCurrDraftQuotePage page = new SQOCurrDraftQuotePage(driver);
//		loadPage(page,this.WAIT_TIME);
//		return page;
//	}
	
	public void saveQuoteAsDraftQuote(){
		saveQuoteAsDraftLink.click();
	}
	
//	
//	/**
//	 * Go to part and price tab. You need to specify the user login info and
//	 * quote type info.
//	 * 
//	 * @param loginUser
//	 *            changwei@cn.ibm.com
//	 * @param loginCntry
//	 *            US
//	 * @param accessLevel
//	 *            1
//	 * @param quoteType
//	 *            PAUN
//	 * @param acquisition
//	 *            ""
//	 * @param country
//	 *            USA
//	 * @return current part and price page object.
//	 */
//	public PGSMyCurrentQuotePage load(String loginUser, String loginCntry, String accessLevel, String quoteType, String acquisition, String country) {
//
//		SQOJumpPage jumpPage = new SQOJumpPage(driver);
//		jumpPage.loginIn(loginCntry, loginUser, accessLevel);
//
//		SQOHomePage sqoHomePage = new SQOHomePage(driver);
//
//		loggerContxt.debug("go to sqo home page finished.....");
//		SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();
//
//		// Create a SQO quote
//		return  cq.createQuote(quoteType, acquisition, country);
//	}
	
	@Override
	public String getSubmittedQuoteReference() {
		String path = "///html/body/div/div[2]/div/div/div[2]/div/div/div/table/tbody/tr[4]/td[2]";
		return getElementText(path);
	}
	
	@Override
	public void clearSelectedResellerLinkClick() {
		elementClickByLinkText("Clear selected reseller.");
	}
	
	public  String getQuoteNum(){
		// String
		// draftQuoteNumPath="//form/div/div/div[2]/div/div[5]/div/div/div/div/div/div[2]";
		// return findElementByXPath(draftQuoteNumPath).getText();
		return getDriver().findElement(By.xpath("//div[contains(.,'Quote reference:') ]/following-sibling::div[position()=1]"))
				.getText();
	}
}
