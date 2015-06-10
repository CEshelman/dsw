package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;

@SuppressWarnings("unused")
public class SQOMyCurrentQuotePage extends MyCurrentQuotePage {
	/*private static Logger loggerContxt = Logger
			.getLogger(SQOMyCurrentQuotePage.class.getName());*/
	private SQOOSearchCustPage customerSearchPage = null;

	@FindBy(xpath="//form[@id='emptyDQCustPrtnrForm']/div[2]/div/div[5]/div/div/div/div/div[2]")
	private WebElement SQORef;
	

	@FindBy(id="_E1D43LL_10_OVERRIDE_UNIT_PRC")
	public WebElement overrideId;
	
	@FindBy(linkText="Customers and partners")
	public WebElement customerAndpartners;
	
	@FindBy(linkText="Open my current quote")
	public WebElement openMyCurQuote;
	
	@FindBy(linkText = "Find an existing customer")
	public WebElement  findAnExistingCustomerLink;
	
	@FindBy(id = "cntEmailAdr")
	public WebElement  cntEmailAdr;
	
	
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
	
	@FindBy(id = "fullfillmentSrcDirect")
	public WebElement fullfillmentSrcDirect;
	
	public WebElement fullfillmentSrcChannel;

	@FindBy(linkText = "Renewal quotes")
	public WebElement renewalQuote;
	
	@FindBy(id = "custReqstdArrivlDay")
	public Select custReqstdArrivlDay;
	@FindBy(linkText = "Submit as final")
	public WebElement  submit_link;
	
	@FindBy(id = "Order")
	public WebElement  order_link;
	
	
	@FindBy(id = "custReqstdArrivlMonth")
	public Select custReqstdArrivlMonth;

	@FindBy(id = "custReqstdArrivlYear")
	public Select custReqstdArrivlYear;
	
	@FindBy(linkText = "Click this button to create a bid iteration.")
	public WebElement createBidIterationLink;

	@FindBy(xpath = "//a[@href='quote.wss?jadeAction=DISPLAY_FIND_QUOTE_BY_IBMER']")
	public WebElement statusLink;

	
	public final static String  draftQuoteNumPath="//form/div/div/table/tbody/tr/td[5]/table/tbody/tr/td";
	
	@FindBy(partialLinkText="Select new ship to addresses")
	public WebElement selectNewShip;
	
	@FindBy(partialLinkText="enter a new shipping address")
	public WebElement enterNewShipping;
	
	public WebElement customerName;
	
	@FindBy(xpath="/html/body/div/div[2]/div/div/div[2]/div/form/div/div/p[3]/input")
	public WebElement submitBtn;
	
	@FindBy(linkText="Select customer")
	public WebElement selectcustomer;
	//
	@FindBy(partialLinkText="D0V5DLL-69902645 (IBM Security QRadar Core Appliance XX24)")
	public WebElement D0V5DLL_element;
	
	@FindBy(xpath="/html/body/div/div[2]/div/div/div[2]/div/form/div[5]/input")
	public WebElement shippingAddressSaveBtn;
	
	@FindBy(className="ibm-btn-arrow-pri")
	public WebElement addressOKBtn;
	public SQOMyCurrentQuotePage(WebDriver driver) {
		super(driver);
	}
	
	@Override
	public SQOPartsAndPricingTabPage goToPartsAndPricingTab(){
		loggerContxt.info("Click on 'Parts and pricing' tab.");
		partsAndPricingTabLink.click();
		loggerContxt.info("Action done");
		SQOPartsAndPricingTabPage page = new SQOPartsAndPricingTabPage(getDriver());
		loadPage(page,this.WAIT_TIME);
		loggerContxt.info("Navigate to 'Parts and pricing' tab.");
		return page;
	}
	
	public SQOSpecialBidTabPage goToSpecialBidTab() {
		specialBidTabLink.click();
		SQOSpecialBidTabPage page = new SQOSpecialBidTabPage(getDriver());
		loadPage(page, this.WAIT_TIME);
		return page;
	}

	public String getQuoteNumber(){
		try{
			String quoteNum = SQORef.getText();
			return quoteNum;
		}catch(Exception e){
			loggerContxt.info("Failed to retrieve the SQO Reference");
			return "";
		}
	}
	
	public SQOOSearchCustPage findCustbyClick(){
		loggerContxt.info("Click on 'Find an existing customer' link.");
		findAnExistingCustomerLink.click();
		loggerContxt.info("action done.");
		SQOOSearchCustPage page = new SQOOSearchCustPage(getDriver());
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	public SQOMyCurrentQuotePage findCustByName(String custName){
		SQOOSearchCustPage searchCustPage =  findCustbyClick();
		SQODisplayCustListPage displayCustListPage =  searchCustPage.displayCustomerListByName();
		return displayCustListPage.selectCustomer();
	}
	
	public SQOMyCurrentQuotePage findCustByNumber(String custNumber){
		SQOOSearchCustPage searchCustPage =  findCustbyClick();
		SQODisplayCustListPage displayCustListPage =  searchCustPage.displayCustomerListByName();
		return displayCustListPage.selectCustomer();
	}
	
	
	public SQOSelectAResellerPage findResellertbyClick(){
		loggerContxt.info("Click on 'Select a tier 1 or tier 2 reseller' link on 'My current quote' page.");
		findResellerLink.click();
		loggerContxt.info("Action on");
		SQOSelectAResellerPage page = new SQOSelectAResellerPage(getDriver());
		waitForElementLoading(5000L);
		loggerContxt.info("Navigate to 'Reseller selection' page successfully.");
		return page;
	}
	
	public SQOMyCurrentQuotePage findResellerByName(String resellerName){
		SQOSelectAResellerPage selectAResellerPage =  findResellertbyClick();
		SQODisplayResellerListPage displayResellerListPage =  selectAResellerPage.displayResellerListByName(resellerName);
		return displayResellerListPage.selectReseller();
	}
	
	
	public SQOSelectADistributorPage findDistributorbyClick(){
		loggerContxt.info("Click on the 'Select a distributor' link on 'My current quote' page.");
		findDistributorLink.click();
		loggerContxt.info("Action done");
		SQOSelectADistributorPage page = new SQOSelectADistributorPage(getDriver());
		loadPage(page,this.WAIT_TIME);
		loggerContxt.info("Navigate to 'Distributor selection' page.");
		return page;
	}
	
	public void fillEmailAdr(String mail){
		loggerContxt.info(String.format("Send keys '%s' to 'Email: *' field in 'My current quote ' page.", mail));
		cntEmailAdr.clear();
		cntEmailAdr.sendKeys(mail);
		loggerContxt.info("Action done");
	}
	
	public void waitForStatus(Long mills){
		waitForElementLoading(mills);
	}
	
	public void selectDirectChnl(){
		loggerContxt.info("check the 'Fulfillment sources*' as 'Direct' on 'My current quote' page.");
		fullfillmentSrcDirect.click();
		loggerContxt.info("Acion done");
		if(alertExists()){
		Alert alert = getDriver().switchTo().alert();

		loggerContxt.info("Alert persent on 'My current quote' page when check the checkbox.");
		
		if (alert != null) {
			loggerContxt.info("Accept the Alert on 'My current quote' page.");
			alert.accept();
			loggerContxt.info("Action done");
		}
		}
	}

	public void selectCurrentDate() {
		elementClickById("DueFromDateSelect");
		elementClickByXPath("/html/body/div[5]/table/thead/tr[2]/td[3]/div");
	}

	public SQOSubmitCurrDraftQuotePage submitCurrentDraftQuote() {
		if (isElementExist(By.id("submitForApproval"))) {
			loggerContxt.info("Click on the 'Submit' button on the 'My current quote' page.");
			elementClickById("submitForApproval");
			loggerContxt.info("Action done");
			SQOSubmitCurrDraftQuotePage page = new SQOSubmitCurrDraftQuotePage(
					getDriver());
			loadPage(page, this.WAIT_TIME);
			loggerContxt.info("Navigate to 'Submit current draft quote' page.");
			return page;
		} else if (isElementExist(By.id("submitForFinal"))) {
			loggerContxt.info("Click on the 'Submit' button on the 'Submit current draft quote' page.");
			elementClickById("submitForFinal");
			loggerContxt.info("Action done");
			SQOSubmitCurrDraftQuotePage page = new SQOSubmitCurrDraftQuotePage(
					getDriver());
			loadPage(page, this.WAIT_TIME);
			loggerContxt.info("Navigate to 'Submit current draft quote' page.");
			return page;
		}
		return null;
		}
	
	public boolean hasSubmitBtn() {
		return isElementExist(By.xpath(".//input[contains(@id,'submitFor')]"));
	}
	
	public void saveQuoteAsDraftQuote(){
		saveQuoteAsDraftLink.click();
	}
	
	/**
	 * Go to part and price tab. You need to specify the user login info and
	 * quote type info.
	 * 
	 * @param loginUser
	 *            changwei@cn.ibm.com
	 * @param loginCntry
	 *            US
	 * @param accessLevel
	 *            1
	 * @param quoteType
	 *            PAUN
	 * @param acquisition
	 *            ""
	 * @param country
	 *            USA
	 * @return current part and price page object.
	 */
	public SQOMyCurrentQuotePage load(String loginUser, String loginCntry, String accessLevel, String quoteType, String acquisition, String country) {

		SQOJumpPage jumpPage = new SQOJumpPage(getDriver());
		jumpPage.loginIn(loginCntry, loginUser, accessLevel);

		SQOHomePage sqoHomePage = new SQOHomePage(getDriver());

		loggerContxt.debug("go to sqo home page finished.....");
		SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();

		// Create a SQO quote
		return  cq.createQuote(quoteType, acquisition, country);
	}
	
	
	public SQORetrieveSavedSalesQuotePage goSQORetrieveSavedSalesQuoteTab(){
		loggerContxt.info("Click on the 'Create a sales quote' link on 'My current quote' page.");
		createSaleQuoteLink.click();
		loggerContxt.info("Action done");
		waitForElementLoading(new Long(10000));
		loggerContxt.info("Click on the 'Retrieve a saved sales quote' link on the 'My current quote' page.");
		retrieveSavedSalesQuoteLink.click();
		loggerContxt.info("Action done");
		waitForElementLoading(new Long(2000));
		SQORetrieveSavedSalesQuotePage page = new SQORetrieveSavedSalesQuotePage(getDriver());
		loggerContxt.info("Navigate to 'Retrieve a saved sales quote' page.");
		return page;
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 * @return
	 */
	public SQOSelectedQuotePPTabPage createBidIterationLinkClick() {
		createBidIterationLink.click();
		switchToAlert(true, getClass().getSimpleName());
		SQOSelectedQuotePPTabPage page = new SQOSelectedQuotePPTabPage(getDriver());
		return page;
	}
	
	@Override
	public  String getQuoteNum(){
		return 	getDriver().findElement(By.xpath("//div[contains(.,'SQO reference:') ]/following-sibling::div[position()=1]")).getText();
	}
	
	public SQOCreateNewPassportAdvantageExpressCustomerPage createANewCustomer(){
		elementClickByLinkText("Create a new customer");
		SQOCreateNewPassportAdvantageExpressCustomerPage page = new SQOCreateNewPassportAdvantageExpressCustomerPage(getDriver());
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	public SQOStatusSearchTabPage gotoStatusSearch() {
		statusLink.click();
		
		SQOStatusSearchTabPage page = new SQOStatusSearchTabPage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	@Override
	public void clearSelectedResellerLinkClick() {
		elementClickByLinkText("Clear selected reseller");
	}
	
	public SQOSalesInfoTabPage goToSalesInfoTab(){
		salesInformationTabLink.click();
		return new SQOSalesInfoTabPage(getDriver());
	}
	
	public SQOCustomerAndPartnerTabPage goToCustomerAndPartnerTab() {
		elementClickByLinkText("Customers and partners");
		return new SQOCustomerAndPartnerTabPage(getDriver());
	}

	public void setFulfillmentSourcesToDirect() {
		loggerContxt.info("select Direct Chanel start......");
		fullfillmentSrcDirect.click();
		loggerContxt.info("select Direct Chanel finished......");
		if (alertExists()) {
			Alert alert = getDriver().switchTo().alert();

			loggerContxt.info("getDriver().switchTo().alert() switched ......"
					+ alert);

			if (alert != null) {
				alert.accept();
			}
		}

	}

	public void setFulfillmentSourcesToChannel() {
		loggerContxt.info("select Direct Chanel start......");
		this.fullfillmentSrcChannel.click();
		loggerContxt.info("select Direct Chanel finished......");
		if (alertExists()) {
			Alert alert = getDriver().switchTo().alert();

			loggerContxt.info("getDriver().switchTo().alert() switched ......"
					+ alert);

			if (alert != null) {
				alert.accept();
			}
		}
	}

	public void clickAllTabs() {
		customerAndpartners.click();
		partsAndPricingTabLink.click();
		salesInformationTabLink.click();

		specialBidTabLink.click();
		customerAndpartners.click();
	}

	public void setOveridIdValue() {
		overrideId.clear();
		overrideId.sendKeys("100");
	}

	public SQOOSearchCustPage getCustomerSearchPage() {
		return customerSearchPage;
	}

	public void setCustomerSearchPage(SQOOSearchCustPage customerSearchPage) {
		this.customerSearchPage = customerSearchPage;
	}

	public void setCustomerSearchPage(){
		SQOOSearchCustPage page = new SQOOSearchCustPage(driver);
		this.loadPage(page, WAIT_TIME);
		this.setCustomerSearchPage(page);
	}
  
  public void selectNewShipAddresses(String search){
//						  loggerContxt.info("select a New Ship to Addresses");
						  selectNewShip.click();
						  loggerContxt.info("");
						  waitForElementLoading(5000L);
						  enterNewShipping.click();
						  waitForElementLoading(5000L);
						  customerName.sendKeys(search);
						  submitBtn.click();	  
						  waitForElementLoading(5000L);
						  selectcustomer.click();
						  waitForElementLoading(5000L);
	  
//							  int x1=driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/form/div[2]/div[2]/div[3]/table/tbody/tr/td[3]")).getLocation().x;
//							  int y1=driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/form/div[2]/div[2]/div[3]/table/tbody/tr/td[3]")).getLocation().y;
//							  int x2=driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/form/div[4]/div[2]/div[3]/table/tbody")).getLocation().x;
//							  int y2=driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/form/div[4]/div[2]/div[3]/table/tbody")).getLocation().y;
//	  String locator1 ="xpath=//html/body/div/div[2]/div/div/div[2]/div/form/div[2]/div[2]/div[3]/table/tbody/tr/td[3]";
//	  String locator2 ="id=table_1 applianceId";
//	 selenium.mouseDownAt(locator1,""+x1+","+y1+"");
//	  loggerContxt.info("mouseDownAt locator1 x1="+x1+" locator1 y1="+y1+"");
//	 //selenium.mouseMoveAt(locator, coordString);
//	  loggerContxt.info("mouseMove locator1="+locator1+"");
//	  selenium.mouseUpAt(locator2, ""+x2+","+y2+"");
//	  loggerContxt.info("mouseDownAt locator2 x2="+x2+" locator2 y2="+y2+"");
	  
      WebElement element1 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/form/div[2]/div[2]/div[3]/table/tbody/tr/td[3]"));
//WebElement element1 = driver.findElement(By.id("dojoUnique1"));
      WebElement element2 = driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/form/div[4]/div[2]/div[3]/table/tbody"));
      //table_1 applianceId  
//	 WebElement element2 = driver.findElement(By.id("table_1"));
	  
	//  int xx = x2 - x1;
//	  int yy = y2 - y1;
//	  (new Actions(driver)).dragAndDrop(element1, element2).perform();
	  Actions actions = new Actions(driver);
	  Actions builder = new Actions(driver);
//
//	  builder.keyDown(Keys.CONTROL)
//	     .click(element1)
//	     .click(element2)
//	     .keyUp(Keys.CONTROL);
//	  
//
//	// Then get the action:
//	Action selectMultiple = builder.build();

	// And execute it:
//	selectMultiple.perform();  
	  
	//  actions.clickAndHold(element1).moveToElement(element2).release();
	 // actions.dragAndDrop(element1, element2).release().perform();
	  //actions.dragAndDropBy(element1, x2-x1, y2-y1).moveToElement(element2).build().perform();
	  
	 // selenium.dragAndDropToObject("", "");
								  actions.clickAndHold(element1).moveByOffset(1, 1);
								  actions.moveToElement(element2).release();
								  Action action = actions.build();
								  action.perform();
	  shippingAddressSaveBtn.click();
	  
	 // switchToAlert(true, getClass().getSimpleName());  
//	  if(new SQOMyCurrentQuotePage(driver).getDriver().findElement(By.className("ibm-btn-arrow-pri")).isDisplayed()){
//		  new SQOMyCurrentQuotePage(driver).getDriver().findElement(By.className("ibm-btn-arrow-pri")).click();
//	  }
//		 // new SQOMyCurrentQuotePage(driver).getDriver().findElement(By.className("ibm-btn-arrow-pri")).click();
//		  //getDriver().findElement(By.className("ibm-btn-arrow-pri")).click();
//		  //addressOKBtn.click();
//	  System.out.println("click OK btn successfully ");
  }


	public void clickDownloadQTAsRich() {
		loggerContxt.info("Click on 'Download quote as rich text file' link on 'My current quote' page.");
		driver.findElement(By.linkText("Download quote as rich text file")).click();
		loggerContxt.info("Action done");
		waitForElementLoading(new Long(500));
		loggerContxt
				.info("Click on 'Export quote to Excel XML format' link on 'My current quote' page.");
		driver.findElement(By.linkText("Export quote as spreadsheet to be used in Excel"))
				.click();
		loggerContxt.info("Action done");
		waitForElementLoading(new Long(500));
		loggerContxt
				.info("Click on 'Export quote to spreadsheet format' link on 'My current quote' page.");
		driver.findElement(By.linkText("Export quote as spreadsheet to be used in Symphony"))
				.click();
		loggerContxt.info("Action done");
		waitForElementLoading(new Long(500));
	}

	public void clickDownloadQTAsRich(String linkText){
		loggerContxt.info(String.format("Click on '%s' link on 'My current quote' page.",linkText));
		try{
			driver.findElement(By.linkText(linkText)).click();
		}catch(Exception e){
			loggerContxt.error(linkText + " does not exist on the page.");
			e.printStackTrace();
		}
		loggerContxt.info("Action done");
		waitForElementLoading(new Long(500));
	}
}
