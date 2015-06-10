package com.ibm.dsw.automation.pageobject.sba;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.pageobject.BasePage;

public class SQOAdministrationPage extends BasePage {
	
	private WebdriverLogger logger = WebdriverLogger.getLogger(this.getClass().getName());
	public SQOAdministrationPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(id="quoteType")
	private WebElement SelectLineOfBusiness;
	@FindBy(id="geo")
	private WebElement selectGEO;
	@FindBy(xpath="//input[@value='Go']")
	private WebElement goDetailsLink;
	
	@FindBy(linkText="Special bid configuration")
	private WebElement speicalBidConfigLink;
	@FindBy(partialLinkText="Edit")
	private WebElement editLink;
	@FindBy(linkText="Approval types")
	private WebElement approvalTypesLink;
	@FindBy(xpath="//tr[td[1]='First Line Manager']/td/a")
	private WebElement editFirstLineManagerLink;
	@FindBy(linkText="Approval groups")
	private WebElement approvalGroupsLink;
	@FindBy(xpath="//tbody/tr[2]/td[6]/a")
	private WebElement firstEditLink;
	@FindBy(id="approverId")
	private WebElement approverSearchInput;
	@FindBy(xpath="//a[contains(@href,'approvalGroupList')]")
	private WebElement submitApproverSearch;
	
	@FindBy(linkText="Special bid regions and districts")
	private WebElement specialBidRegionLink;
	
	@FindBy(linkText="Approval routing rules")
	private WebElement approvalRoutigRulesLink;
	@FindBy(xpath="//tbody/tr/td[4]/a")
	private WebElement viewFirstRoutingRule;
	@FindBy(xpath="//tbody/tr/td[5]/a")
	private WebElement editFirstRoutingRule;
	
	@FindBy(linkText="Create a new routing rule for Passport Advantage or Passport Advantage Express quotes")
	private WebElement createNewPARoutingRuleLink;
	@FindBy(id="ruleName")
	private WebElement ruleNameInput;
	@FindBy(id="quoteType")
	private WebElement quoteTypeSelect;
	@FindBy(id="approvalGroups")
	private WebElement approvalGroupsSelect;
	@FindBy(xpath=".//input[@value='Test rules']")
	private WebElement submitTestRuleButton;
	@FindBy(xpath="//input[@value='Submit']")
	private WebElement submitCreateRuleButton;
	@FindBy(partialLinkText="Test rules")
	private WebElement testRuleLink;
	@FindBy(id="country")
	private WebElement countrySelect;
	@FindBy(id="bizOrgs")
	private WebElement businessOrganizationSelect;
	@FindBy(id="ffmtSource")
	private WebElement fulfillmentSourceSelect;
	@FindBy(xpath="//tbody/tr[td[1]='XPRS Testing']/td[6]/a")
	private WebElement removeXPRSRuleLink;
	@FindBy(className="ibm-btn-arrow-pri")
	private WebElement confirmToRemoveButton;
	
	
	@FindBy(partialLinkText="Global reader groups")
	private WebElement globalReaderGroupsLink;
	@FindBy(xpath="//tbody/tr/td[3]/a")
	private WebElement firstGlobalReaderGroup;
	@FindBy(linkText="Part groups")
	private WebElement partGroupsLink;
	@FindBy(id="cntryCodes")
	private WebElement countryCodesSelect;
	@FindBy(partialLinkText="Edit")
	private WebElement firstPartGroup;
	
	@FindBy(linkText="Customer groups")
	private WebElement customerGroupsLink;
	@FindBy(xpath="//tbody/tr[1]/td[2]/a")
	private WebElement firstCustomerGroup;
	
	@FindBy(partialLinkText="SQO Administration")
	private WebElement sqoAdminLink;
	
	public SQOAdministrationPage newCache(){
		
		SQOAdministrationPage page = new SQOAdministrationPage(this.getDriver());
		loadPage(page, WAIT_TIME);
		return page;
	}
	public SQOAdministrationPage clickGo(){
		goDetailsLink.click();
		return newCache();
	}
	
	public SQOAdministrationPage clickGo(String quoteTpe, String geo){
		logger.info(String.format("Specify you geo::%s, %s in 'SQO Administration' page... ",quoteTpe,geo));
		selectOptionByVisibleText(SelectLineOfBusiness, quoteTpe);
		selectOptionByVisibleText(selectGEO, geo);
		return clickGo();
	}
	
	public void clickSpeicalBidConfigLink(){
		logger.info("Click on the 'Special bid configuration' link...");
		speicalBidConfigLink.click();
	}
	public void editSpeicalBidConfig(){
		clickSpeicalBidConfigLink();
		logger.info("Click on the 'edit' link in Special bid configuration page");
		editLink.click();
	}
	
	public void clickApprovalTypesLink(){
		logger.info("Click on the 'Approval types' link...");
		approvalTypesLink.click();
	}
	
	public void editFirstLineManagerLink(String linkText){
		clickApprovalTypesLink();
		logger.info("Click on the 'Edit' link for the 'First Line Manager' approval type");
		try{
			editFirstLineManagerLink.click();	
		}catch (Exception e){
			WebElement temp = driver.findElement(By.xpath(String.format("//tr[td[1]='%s']/td/a",linkText)));
			temp.click();
		}
	}
	public void clickApprovalGroupsLink(){
		logger.info("Click on the 'Approval groups' sub-link in the left nav");
		approvalGroupsLink.click();
	}
	
	public void clickFirstEditLink(){
		clickApprovalGroupsLink();
		logger.info("Click on the 'Edit' link for the first group in the approval group list");
		firstEditLink.click();
	}
	
	public void searchApprover(String email){
		approvalGroupsLink.click();
		logger.info(String.format("Enter %s in the text box under 'Approver Search' and click 'Submit' button",email));
		approverSearchInput.sendKeys(email);
		submitApproverSearch.click();
	}
	
	public void clickSpecialBidRegionLink(){
		logger.info("Click on the 'Special bid regions and districts' sub-link in the left nav");
		specialBidRegionLink.click();
	}
	
	public void editReionInfo(String region, String district, String country){
		logger.info(String.format("Click on the 'Edit' link for the region::%s, District::%s, Country::%s", region, district, country));
		String xpath = String.format("//tbody/tr[contains(td[1],'%s') and contains(td[2],'%s') and contains(td[3],'%s')]/td[6]/a", region, district, country);
		logger.info(xpath);
		WebElement ele = driver.findElement(By.xpath(xpath));
		ele.click();
	}
	
	public void clickApprovalRoutigRulesLink(){
		logger.info("Click on the 'Approval routing rules' link in the left nav");
		approvalRoutigRulesLink.click();
	}
	
	public void viewFirstRoutingRule(){
		clickApprovalRoutigRulesLink();
		logger.info("Click on the 'View' link for the first rule in the rule list");
		viewFirstRoutingRule.click();
	}
	
	public void editFirstRoutingRule(){
		clickApprovalRoutigRulesLink();
		logger.info("Click on the 'Edit' link for the first rule in the rule list");
		editFirstRoutingRule.click();
	}
	
	public SQOAdministrationPage clickCreateNewPARoutingRuleLink(){
		clickApprovalRoutigRulesLink();
		createNewPARoutingRuleLink.click();
		SQOAdministrationPage newPage = newCache();
		return newPage;
	}
	
	public void createNewPARoutingRule(String name, String quoteType, String approvelGroup){
		logger.info(
				String.format("Enter '%s' in the 'Rule name' field, select %s in the 'Quote type' dropdown",name,quoteType));
		ruleNameInput.sendKeys(name);
		this.selectOptionByVisibleText(quoteTypeSelect, quoteType);
		if (StringUtils.isBlank(approvelGroup)) {
			logger.info(" select the first group in the 'Assign this rule to the following approval groups' list box");
			this.selectOptionByIndex(approvalGroupsSelect, 0);
		}else{
			this.selectOptionByVisibleText(approvalGroupsSelect,approvelGroup);
		}
		logger.info("Press the 'Submit' button'...");
		submitCreateRuleButton.click();
	}
	
	public void clickTestRuleLink(String quoteType){
		logger.info(String.format("Select '%s'  in the 'Quote type' dropdown and press the 'Test rules' button",quoteType));
		this.selectOptionByVisibleText(quoteTypeSelect, quoteType);
		testRuleLink.click();
	}
	
	public void testRule(String country, String quoteType, String busOrgs, String fulfillmentSource){
		logger.info(
				String.format("Select '%s' in the Country dropdown, '%s' in the 'Quote type' dropdown, '%s' in the 'Business organization' dropdown, '%s' in the 'Fulfillment source' dropdown  and press the 'Test rules' button",country,quoteType,busOrgs,fulfillmentSource));
		this.selectOptionByVisibleText(countrySelect, country);
		this.selectOptionByVisibleText(quoteTypeSelect, quoteType);
		this.selectOptionByVisibleText(businessOrganizationSelect, busOrgs);
		this.selectOptionByVisibleText(fulfillmentSourceSelect,fulfillmentSource);
		submitTestRuleButton.click();
	}
	
	public void removeXPRSTestingRule(){
		logger.info("Click on the 'Remove' link for the 'XPRS Testing' routing rule name and press 'Ok' to confirm removal");
		removeXPRSRuleLink.click();
		confirmToRemoveButton.click();
	}
	
	public SQOAdministrationPage clickGlobalReaderGroupsLink(){
		logger.info("Click on the 'Global readers groups' link in the left nav");
		try{
			globalReaderGroupsLink.click();
		}catch (Exception e){
			driver.manage().timeouts().implicitlyWait(3L, TimeUnit.SECONDS);
			globalReaderGroupsLink.click();
		}
		return newCache();
	}
	
	public void editFirstglobalReaderGroup(){
		logger.info("Click on the 'Edit' link for the first global reader group in the list page");
		firstGlobalReaderGroup.click();
	}
	
	public void clickPartGroupsLink(){
		logger.info("Click on the 'Part groups' link in the left nav");
		partGroupsLink.click();
	}
	
	public void editFirstPartGroup(String country){
		clickPartGroupsLink();
		this.selectOptionByVisibleText(countryCodesSelect, country);
		logger.info("Click on the 'Edit' link for the first part group in the list");
		firstPartGroup.click();
	}
	
	public void clickCustomerGroupsLink(){
		logger.info("Click on the 'Customer groups' link in the left nav");
		checkError();
		customerGroupsLink.click();
	}
	
	public void editFirstCustomerGroup(){
		clickCustomerGroupsLink();
		logger.info("Click on the 'Edit' link in the first customer group in the list");
		firstCustomerGroup.click();
	}
	
	public void checkError(){
		if(selenium.isTextPresent("System error")){
			logger.error("The Previous step failed due to access issue...");
			logger.info("Return back to the SQO Adminstration page...");
			sqoAdminLink.click();
		}
	}
}
