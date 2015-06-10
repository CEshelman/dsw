package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;

public class SQORenewalQuoteSearchTabPage extends BasePage {
	
	public SQORenewalQuoteSearchTabPage(WebDriver driver) {
		super(driver);
	}
	
	SQORenewalQuoteSearchResultPage renewalQuoteSearchResultPage = null;

	//Quote Type
	@FindBy(id="QUOTE_TYPE")
	public WebElement quoteType;
	//Renewal Status
	@FindBy(id="RQS")
	public WebElement renewalStatusOptions;
	
//	By IBMer tab
	@FindBy(linkText="By IBMer")
	public WebElement byIBMerTab;
	@FindBy(linkText="Find quotes assigned to a specific sales representative")
	public WebElement byIBMerTabWithSalesSpecifiedLink;
	@FindBy(id="SALES_REP")
	public WebElement salesRepInput;
	@FindBy(linkText="Find quotes assigned to a specific renewal coordinator")
	public WebElement byIBMerTabWithCoordinatorSpecifiedLink;
	@FindBy(id="MAINT_COORD")
	public WebElement renewalCoordinatorInput;
	
//	By customer tab
	@FindBy(linkText="By customer")
	public WebElement byCustomerTab;
	@FindBy(linkText="Find quotes by customer agreement and/or site number")
	public WebElement byCustomerTabAgreementAndSiteLink;
	@FindBy(linkText="Find quotes by customer attributes")
	public WebElement byCustomerTabAttributesLink;
	@FindBy(id="AGREE_NUM")
	public WebElement customerAgreementNumber;
	@FindBy(id="CUST_SITE_NUM")
	public WebElement customerSiteNumber;
	@FindBy(id="CUST_NAME")
	public WebElement customerNameInput;
	@FindBy(id="COUNTRY")
	public WebElement countryRegionOption;
	@FindBy(id="STATE")
	public WebElement stateProvince;
	
//	By partner tab
	@FindBy(linkText="By partner")
	public WebElement byPartnerTab;
	@FindBy(id="PARTNER_SITE_NUM")
	public WebElement partnerSiteNumInput;
	@FindBy(id="PARTNER_TYPE_RESELLER")
	public WebElement partnerTypeResellerRadio;
	@FindBy(id="PARTNER_TYPE_PAYER")
	public WebElement partnerTypePayerRadio;
	@FindBy(linkText="Find quotes by partner attributes")
	public WebElement byPartnerTabAttributesLink;
	@FindBy(id="COMPANY_NAME")
	public WebElement partnerCompanyName;
	
//	By region tab
	@FindBy(linkText="By region")
	public WebElement byRegionTab;
	
//	By Number tab
	@FindBy(linkText="By number")
	public WebElement byNumberTab;
	@FindBy(id="QUOTE_NUM")
	public WebElement quoteNumberInput;
	@FindBy(id="roleCustomer")
	public WebElement roleCustomerCheckbox;
	@FindBy(id="roleQuoting")
	public WebElement roleQuotingCheckbox;
	@FindBy(id="roleFCTEditor")
	public WebElement roleFCTEditorCheckbox;
	
	
	@FindBy(xpath = "//input[@name='ibm-submit']")
	public WebElement ibm_submit;
	
	public SQORQSearchReslutPage goDispQuoteReslt(String quoteNum) {
		byNumberTab.click();
		driver.findElement(By.id("QUOTE_NUM")).sendKeys(quoteNum);
		ibm_submit.click();
		SQORQSearchReslutPage page = new SQORQSearchReslutPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	public void clickByNumberTab(){
		byNumberTab.click();
	}
	
	public void clickByCustomerTab(){
		byCustomerTab.click();
	}
	
	public void clickByPartnerTab(){
		byPartnerTab.click();
	}
	
	public void clickByRegionTab(){
		byRegionTab.click();
	}
	
	public void checkAllCheckboxesInByIBMerTab(){
		checkCheckbox(roleCustomerCheckbox);
		checkCheckbox(roleQuotingCheckbox);
		checkCheckbox(roleFCTEditorCheckbox);
	}
	
	public void chooseRenewalStatus(String renewalStatus){
		selectedOptionByXpath("RQS", renewalStatus);
	}
	
	public void clickSalesSpecifiedLinkInByIBMerTab(){
		this.byIBMerTabWithSalesSpecifiedLink.click();
	}
	
	public void clickCoordinatorSpecifiedLinkInByIBMerTab(){
		this.byIBMerTabWithCoordinatorSpecifiedLink.click();
	}
	
	public void clickAttributeLinkInByCustomerTab(){
		this.byCustomerTabAttributesLink.click();
	}
	
	public void clickAttributeLinkInByPartnerTab(){
		this.byPartnerTabAttributesLink.click();
	}
	//click the submit button in the "By IBMer" tab
	public void findRQByIBMerAssignedToMe(String renewalStatusValue){
		this.checkAllCheckboxesInByIBMerTab();
		this.chooseRenewalStatus(renewalStatusValue);
		this.clickSubmitButton();
	}
	
	public void findRQByIBMerAssignedToSales(String salesRep, String renewalStatusValue){
		this.salesRepInput.sendKeys(salesRep);
		this.chooseRenewalStatus(renewalStatusValue);
		this.checkAllCheckboxesInByIBMerTab();
		this.clickSubmitButton();
	}
	
	public void findRQByIBMerAssignedToCoordinator(String renewalCoordinator, String renewalStatusValue){
		this.renewalCoordinatorInput.sendKeys(renewalCoordinator);
		this.chooseRenewalStatus(renewalStatusValue);
		this.clickSubmitButton();
	}

	public void findRQByCustomerAgreementAndSite(String siteNumber, String renewalStatusValue){
		this.customerSiteNumber.sendKeys(siteNumber);
		this.chooseRenewalStatus(renewalStatusValue);
		this.clickSubmitButton();
	}
	
	public void findRQByCustomerAttributes(String customerName, String countryRegion, String stateProvince, String renewalStatusValue){
		this.customerNameInput.clear();
		this.customerNameInput.sendKeys(customerName);
		this.selectOptionByVisibleText(countryRegionOption, countryRegion);
		this.waitForElementLoading(2000L);
		this.selectOptionByVisibleText(this.stateProvince, stateProvince);
		this.chooseRenewalStatus(renewalStatusValue);
		this.clickSubmitButton();
	}
	
	//click the submit button in the "By Number" tab
	public void findRQByNumber(String quoteNumber){
		this.quoteNumberInput.sendKeys(quoteNumber);
		this.clickSubmitButton();
	}
	
	public void findRQBySellerPartnerSiteNumber(String partnerSiteNumber, String renewalStatusValue){
		this.partnerTypeResellerRadio.click();
		this.partnerSiteNumInput.clear();
		this.partnerSiteNumInput.sendKeys(partnerSiteNumber);
		this.chooseRenewalStatus(renewalStatusValue);
		this.clickSubmitButton();
	}
	
	public void findRQByPayerPartnerSiteNumber(String partnerSiteNumber, String renewalStatusValue){
		this.partnerTypePayerRadio.click();
		this.partnerSiteNumInput.clear();
		this.partnerSiteNumInput.sendKeys(partnerSiteNumber);
		this.chooseRenewalStatus(renewalStatusValue);
		this.clickSubmitButton();
	}
	
	public void findRQBySellerPartnerByAttributes(String partnerCompanyName, String countryRegion, String stateProvince, String renewalStatusValue){
		this.partnerTypeResellerRadio.click();
		this.partnerCompanyName.clear();
		this.partnerCompanyName.sendKeys(partnerCompanyName);
		this.selectOptionByVisibleText(countryRegionOption, countryRegion);
		this.waitForElementLoading(2000L);
		this.selectOptionByVisibleText(this.stateProvince, stateProvince);
		this.chooseRenewalStatus(renewalStatusValue);
		this.clickSubmitButton();
	}
	
	public void findRQByPayerPartnerByAttributes(String partnerCompanyName, String countryRegion, String stateProvince, String renewalStatusValue){
		this.partnerTypePayerRadio.click();
		this.partnerCompanyName.clear();
		this.partnerCompanyName.sendKeys(partnerCompanyName);
		this.selectOptionByVisibleText(countryRegionOption, countryRegion);
		this.waitForElementLoading(2000L);
		this.selectOptionByVisibleText(this.stateProvince, stateProvince);
		this.chooseRenewalStatus(renewalStatusValue);
		this.clickSubmitButton();
	}
	
	public void findRQByRegion(String saleOrganization, String countryRegion, String stateProvince, String renewalStatusValue){
		selectOptionByVisibleText("SALES_ORG", saleOrganization);
		this.selectOptionByVisibleText(countryRegionOption, countryRegion);
		this.waitForElementLoading(2000L);
		this.selectOptionByVisibleText(this.stateProvince, stateProvince);
		this.chooseRenewalStatus(renewalStatusValue);
		this.clickSubmitButton();
	}
	
	//click on the quote number link to view the result details
	public void vieRQResult(String quoteNumber){
		
		renewalQuoteSearchResultPage.viewQuoteDetailsByNumber(quoteNumber);
	}
	
	public void clickSubmitButton(){
		this.ibm_submit.click();
		this.setRenewalQuoteSearchResultPage();
	}
	
	public SQORenewalQuoteSearchTabPage newCache(){
		SQORenewalQuoteSearchTabPage page = new SQORenewalQuoteSearchTabPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}

	private SQORenewalQuoteSearchResultPage displaySearchResult(){
		renewalQuoteSearchResultPage = new SQORenewalQuoteSearchResultPage(this.driver);
		loadPage(renewalQuoteSearchResultPage, this.WAIT_TIME);
		return renewalQuoteSearchResultPage;
	}
	
	public SQORenewalQuoteSearchResultPage getRenewalQuoteSearchResultPage() {
		return renewalQuoteSearchResultPage;
	}

	public void setRenewalQuoteSearchResultPage(
			SQORenewalQuoteSearchResultPage renewalQuoteSearchResultPage) {
		this.renewalQuoteSearchResultPage = renewalQuoteSearchResultPage;
	}
	//Associate 'renewal quotes' page with the 'Selected renewal quotes' page
	public void setRenewalQuoteSearchResultPage() {
		this.renewalQuoteSearchResultPage = this.displaySearchResult();
	}
	
}
