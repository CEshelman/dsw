package com.ibm.dsw.automation.pageobject.sqo;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ibm.dsw.automation.pageobject.BasePage;
import com.ibm.dsw.automation.util.DateUtil;

public class SQOStatusSearchTabPage extends BasePage {
	
	private SQODisplayStatusSearchReslutPage searchDisPlayPage = null;
	
	public SQOStatusSearchTabPage(WebDriver driver) {
		super(driver);
	}

	
	@FindBy(linkText = "Quote number")
	public WebElement quoteNumTabLnk;
	

	// Sales quotes
	@FindBy(id="quoteTypeFilter1")
	public WebElement quoteTypeFilter1;
	//Renewal quote edits
	@FindBy(id="quoteTypeFilter0")
	public WebElement quoteTypeFilter0;
	
	//Passport Advantage 
	@FindBy(id="lob_filter1")
	public WebElement lob_filter1;
	@FindBy(id="lob_filter2")
	public WebElement lob_filter2;
	@FindBy(id="lob_filter3")
	public WebElement lob_filter3;
	@FindBy(id="lob_filter4")
	public WebElement lob_filter4;
	@FindBy(id="lob_filter5")
	public WebElement lob_filter5;
	@FindBy(id="lob_filter6")
	public WebElement lob_filter6;
	@FindBy(id="lob_filter7")
	public WebElement lob_filter7;
	
    //quarter timeFilterQuarterOptions
	public WebElement timeFilterQuarterOptions;
	
	@FindBy(id="sortFilter")
	public WebElement sortFilter;
	
	@FindBy(linkText = "Select all classifications")
	public WebElement selectAllClassifications;
	
	//IBMer assigned tab start
	@FindBy(linkText="IBMer assigned")
	public WebElement ibmerAssignedTab;
	//What is the designated person's role on the quote or special bid[Creator/owner/submitter] 
	@FindBy(id="ownerRoles0")
	public WebElement ownerRoles0;
	// Editor 
	@FindBy(id="ownerRoles1")
	public WebElement ownerRoles1;
	//Approver named on the quote
	public WebElement ownerRoles2;
	//Reviewer
	public WebElement ownerRoles3;
	// Approver with approval pending 
	public WebElement ownerRoles4;
	
	//Assigned to me
	@FindBy(id="ownerType0")
	public WebElement ownerType0;
	//Assigned to the following person
	@FindBy(id="ownerType1")
	public WebElement ownerType1;
	//Enter the internet e-mail address, last name or last name, first name
	@FindBy(id="ownerNameOrEmail")
	public WebElement ownerNameOrEmail;
	//Save selections on this tab as my default 
	public WebElement markIBMerDefault;
	private String assigneeValidateAlertXpath = ".//div[contains(p,'The internet e-mail address, last name or last name, first name must contain a minimum of 3 character(s).')]";
	
	@FindBy(linkText = "Select all statuses")
	public WebElement selectAllStatus;
	//Find quotes and special bids submitted within the past *
	@FindBy(id="timeFilter36500")
	public WebElement timeFilterCriteria_36500;
	@FindBy(id="timeFilterQuarter")
	public WebElement timeFilterQuarter;//quarter radio box
	@FindBy(id="timeFilterMonth")
	public WebElement timeFilterMonth;//Month radio box
	//IBMer assigned tab end
	
	//Make above selections my default 
	@FindBy(id="markFilterDefault")
	public WebElement markFilterDefault;

	
	//customer Tab
	@FindBy(linkText="Customer")
	public WebElement customerTab;
	
	public WebElement markCountryDefault;
	//Customer name: input text
	@FindBy(id="customer_name")
	public WebElement cutomerNameText;
	//Find by customer name button
	@FindBy(id="custname")
	public WebElement ibm_CusName_submit;
	
	
	//partner section
	@FindBy(linkText="Partner")
	public WebElement partnerTab;
	public WebElement partnerTypeForNum0;
	public WebElement partnerTypeForNum1;
	public WebElement partnerSiteNum;
	@FindBy(id="partnerTypeForName0")
	public WebElement partnerTypeForName0;
	@FindBy(id="partnerTypeForName1")
	public WebElement partnerTypeForName1;
	@FindBy(id="partnerName")
	public WebElement partnerName;
	@FindBy(id="partname")
	public WebElement findQuoteByPartnerNameButton;
	
	//Approval attributes
	@FindBy(id="markAppvlAttrDefault")
	public WebElement markAppvlAttrDefault;
	
	@FindBy(xpath = "//input[@title='Sales information']")
	public WebElement  salesInformationTabLink;
	
	@FindBy(xpath = "//input[@value='Find quotes']")
	public WebElement ibm_submit;
	
	
	@FindBy(xpath = "//input[@value='Find by site or agreement number']")
	public WebElement ibm_customer_site_submit;
	
	@FindBy(xpath = "//input[@value='Find by partner name']")
	public WebElement ibm_partner_submit;
	
	
	@FindBy(xpath = "//input[@value='Find by partner site number']")
	public WebElement ibm_partner_site_submit;
	
	@FindBy(xpath = "//input[@value='Find by partner name']")
	public WebElement ibm_partner_name_submit;
	
	@FindBy(linkText = "Customer")
	public WebElement customerTabLnk;
	
	@FindBy(linkText = "Partner")
	public WebElement partnerTabLnk;
	
	@FindBy(linkText = "Country/region")
	public WebElement countryTabLnk;
	
	public WebElement markCountryRegionDefaultCheckbox;
	
	//Approval Attributes tab
	//Select only quotes pending approver for group selected
	@FindBy(id="approverGroupFilter1")
	public WebElement approverGroupFilter1;
	//Select quotes approved by this group on this day
	public WebElement approverGroupFilter2;
	
	//Select only quotes pending approver for type selected 
	@FindBy(id="approverTypeFilter1")
	public WebElement approverTypeFilter1;
	// Select quotes approved by this type on this day 
	@FindBy(id="approverTypeFilter2")
	public WebElement approverTypeFilter2;
	
	// Include related quotes in the search results 
	public WebElement relatedQuoteFlag;
	
	
	//Include common selection criteria as part of the search 
	public WebElement commonCriteriaFlag;
	
	@FindBy(linkText = "Approval attributes")
	public WebElement approvalTabLnk;
	
	@FindBy(linkText = "Siebel number")
	public WebElement siebelNumberTabLnk;
	
	@FindBy(linkText = "Order number")
	public WebElement orderNumberTabLnk;
	


	@FindBy(linkText = "Status")
	public WebElement statusTabLink;

	
	/**
	 * @deprecated use findQuoteByNum instead
	 * @param quoteNum
	 * @return
	 */
	@Deprecated
	public SQODisplayStatusSearchReslutPage goDispQuoteReslt(String quoteNum) {
		return findQuoteByNum(quoteNum);
	}
	
	public SQODisplayStatusSearchReslutPage findQuoteByNum(String quoteNum) {
		quoteNumTabLnk.click();
		driver.findElement(By.id("number")).sendKeys(quoteNum);
		ibm_submit.click();
		return this.displaySearchResult();
	}

	public SQODisplayStatusSearchReslutPage goDispAllQuoteReslt() {
		
		
		anonymousResolutionButtonClick("lob_filter1");
		anonymousResolutionButtonClick("lob_filter2");
		quoteTypeFilter1.click();
		selectedOptionByValue("sortFilter","0",driver);
		selectAllStatus.click();
		selectAllClassifications.click();
		ownerRoles0.click();
		timeFilterCriteria_36500.click();
		ibm_submit.click();
		loggerContxt.info("Checked all the conditions for Status");
		return this.displaySearchResult();
	}
	
	/**
	 * Fill in the common conditions in the status search page.
	 * @return
	 */
	public void fillStatsSearchConditions(){
		checkAllBoxPerName("quoteTypeFilter");
		/*quoteTypeFilter0.click();
		quoteTypeFilter0.click();*/
		checkAllBoxPerName("lob_filter");
		/*checkCheckbox(quoteTypeFilter0);
		checkCheckbox(quoteTypeFilter1);
		checkCheckbox(lob_filter1);
		checkCheckbox(lob_filter2);
		checkCheckbox(lob_filter3);
		checkCheckbox(lob_filter4);
		checkCheckbox(lob_filter5);
		checkCheckbox(lob_filter6);
		checkCheckbox(lob_filter7);*/
		selectAllClassifications.click();
		selectAllStatus.click();	
		selectedOptionByValue("sortFilter","0",driver);
		checkCheckbox(timeFilterCriteria_36500);
		checkCheckbox(markFilterDefault);
	}
	
	public void fillStatsSearchWithCustomizedConditions(){
		this.checkMultipleBoxes("quoteTypeFilter", 1);
		this.checkMultipleBoxes("lob_filter", 0,1);
		selectAllClassifications.click();
		selectAllStatus.click();	
		selectedOptionByValue("sortFilter","0",driver);
		checkCheckbox(markFilterDefault);
	}
	
	private void fillQuotePeriod(String elementId, String[] periodArr){
		if (!markFilterDefault.isSelected()) {
			markFilterDefault.click();
		}
		selectMultipleOptionsInDropdownList(elementId,periodArr);
	}
	
	public void fillQuotePeriodAsQuarter(String[] periodArr){
		timeFilterQuarter.click();
		fillQuotePeriod("timeFilterQuarterOptions",periodArr);
	}
	
	public void fillQuotePeriodAsMonth(String[] periodArr){
		timeFilterMonth.click();
		fillQuotePeriod("timeFilterMonthOptions",periodArr);
	}
	// click the "IBMer Assigned" tab
	public void clickIBMerAssignedTab(){
		this.ibmerAssignedTab.click();
	}
	
	public SQOStatusSearchTabPage newCache(){
		SQOStatusSearchTabPage page = new SQOStatusSearchTabPage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	//Check only one checkbox each time in "IBMer Assigned" tab
	public void checkboxInIBMerAssignedTab(int i){
		loggerContxt.info(String.format("Check the checkbox id 'ownerRole%d' in 'IBMer Assigned' tab..",i));
		checkOnlyOneboxInTheGroup("ownerRoles",i);
	}
	
	//Check all the checkbox in in "IBMer Assigned" tab
	public void checkAllBoxInIBMerAssignedTab(){
		List<WebElement> ownerRoles = driver.findElements(By.name("ownerRoles"));
		for (int j = 0; j < ownerRoles.size(); j++) {
			if (!ownerRoles.get(j).isSelected()) {
				ownerRoles.get(j).click();
			}
		}
	}
	
	/**
	 * Find the following submitted quotes *
	 * @param name
	 * @param inds 0 -Renewal quote edits , 1 - Sales quotes 
	 */
	public void checkSubmittedQuoteType(String name, int...inds){
		this.checkMultipleBoxes("quoteTypeFilter", inds);
	}
	/**
	 * Find the following quote types 
	 * @param name
	 * @param inds
	 */
	public void checkLobQuoteType(String name, int...inds){
		this.checkMultipleBoxes("lob_filter", inds);
	}
	
	public void checkAllBoxPerName(String name){
		List<WebElement> elements = driver.findElements(By.name(name));
		for (WebElement element : elements) {
			loggerContxt.info(String.format("The element::%s is displayed...",element.toString()));
			new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOf(element));
			if ((!element.isSelected())) {
				 element.click();
			}
		}
	}
	
	public void findQuoteByIBMerAssigned(){
		ownerType0.click();
		ibm_submit.click();
		this.setSearchDisPlayPage();
	}
	
	
	public void findQuoteByIBMerAssigned(String assignee){
		if(!ownerType1.isSelected()){
			ownerType1.click();
		}
		ownerNameOrEmail.clear();
		ownerNameOrEmail.sendKeys(assignee);
		ibm_submit.click();
		/*if (isElementExist(By.xpath(".//input[contains(@onclick,'dojoAlertId_findByIBMerForm#ownerNameOrEmail#')"))) {
			driver.findElement(By.xpath(".//input[contains(@onclick,'dojoAlertId_findByIBMerForm#ownerNameOrEmail#')")).click();
		}*/
		this.setSearchDisPlayPage();
	}
	
	//click the "Customer" tab
	public void clickCustomerTab(){
		this.customerTab.click();
	}
	
	public void findQuoteByCustomerName(String customerName){
		cutomerNameText.clear();
		cutomerNameText.sendKeys(customerName);
		ibm_CusName_submit.click();
		this.setSearchDisPlayPage();
	}
	//click the "partner" tab
	public void clickPartnerTab(){
		partnerTab.click();
	}
	
	/**
	 * 
	 * @param partnerName
	 * @param radio 0 for ' Reseller ', 1 for ' Distributor/payer '
	 */
	public void findQuoteByPartnerName(String partnerName, int radio){
		this.partnerName.clear();
		this.partnerName.sendKeys(partnerName);
		switch (radio) {
		case 0:
			loggerContxt.info("Select the partner type as 'Reseller'.");
			partnerTypeForName0.click();
			break;
		case 1:
			loggerContxt.info("Select the partner type as 'Distributor/payer'.");
			partnerTypeForName1.click();
			break;
		default:
			break;
		}
		findQuoteByPartnerNameButton.click();
		this.setSearchDisPlayPage();
	}
	
	public void clickCountryRegionTab(){
		countryTabLnk.click();
	}
	
	public void findQuoteByCountryRegion(String IMTRegion, String country, String state){
		if (!StringUtils.isBlank(IMTRegion)) {
			selectedOptionByValue(By.id("subRegion"), IMTRegion);
		}
		waitForElementLoading(3000L);
		if (!StringUtils.isBlank(country)) {
			selectedOptionByValue(By.id("country"), country);
		}
		waitForElementLoading(4000L);
		if (!StringUtils.isBlank(state)) {
			selectedOptionByValue(By.id("state"), state);
		}
		anonymousResolutionButtonClick("markCountryRegionDefault");
		ibm_submit.click();
		this.setSearchDisPlayPage();
	}
	
	public void clickApprovalAttributesTab(){
		approvalTabLnk.click();
	}
	
	public void selectOptionsForApprovalAttributesTab(String...args) throws Exception{
			if (args==null && args.length < 4) {
				loggerContxt.error("The parameters for selecting options in 'Approval Attributes' tab is not enough");
				throw new Exception("The parameters for selecting options in 'Approval Attributes' tab is not enough");
			}
			if (!StringUtils.isBlank(args[0])) {
				selectedOptionByValue(By.id("sbRegion"), args[0]);
			}
			waitForElementLoading(3000L);
			if (!StringUtils.isBlank(args[1])) {
				selectedOptionByValue(By.id("sbDistrict"), args[1]);
			}
			waitForElementLoading(5000L);
			if (!StringUtils.isBlank(args[2])) {
				selectedOptionByXpath("approverGroup", args[2]);
			}
			waitForElementLoading(5000L);
			if (!StringUtils.isBlank(args[3])) {
				try{
					selectedOptionByXpath("approverType", args[3]);
				}catch(Exception e){
					selectOptionByVisibleText(driver.findElement(By.id("approverType")), args[3]);
				}
			}
	}
	//Check the checkboxes in the Approval Attributes for Approver Group
	public void selectCheckboxForApproverGroup(boolean flg1, boolean flg2){
		if (flg1 && !approverGroupFilter1.isSelected()) {
			approverGroupFilter1.click();
		}
		if (flg2) {
			if (!approverGroupFilter2.isSelected()) {
				approverGroupFilter2.click();
			}
			Date now = new Date();
			String day = String.valueOf(DateUtil.getDay(now));
			int month = DateUtil.getMonth(now);
			if (month>12) {
				month -= 10;
			}
			String year = String.valueOf(DateUtil.getYear(now));
			selectedOptionByValue(By.id("approverGroupFilterYear"), year);
			waitForElementLoading(1000L);
			selectedOptionByIndex(By.id("approverGroupFilterMonth"), month);
			waitForElementLoading(1000L);
			selectedOptionByValue(By.id("approverGroupFilterDay"), day);
		}
	}
	//Check the checkboxes in the Approval Attributes for Approver Type
	public void selectCheckboxForApproverType(boolean flg1, boolean flg2){
		if (flg1 && !approverTypeFilter1.isSelected()) {
			approverTypeFilter1.click();
		}
		if (flg2) {
			if (!approverTypeFilter2.isSelected()) {
				approverTypeFilter2.click();
			}
			Date now = new Date();
			String day = String.valueOf(DateUtil.getDay(now));
			int month = DateUtil.getMonth(now);
			if (month>12) {
				month -= 10;
			}
			String year = String.valueOf(DateUtil.getYear(now));
			selectedOptionByValue(By.id("approverTypeFilterYear"), year);
			waitForElementLoading(1000L);
			selectedOptionByIndex(By.id("approverTypeFilterMonth"), month);
			waitForElementLoading(1000L);
			selectedOptionByValue(By.id("approverTypeFilterDay"), day);
		}
	}
	
	public void findQuoteByApprovalAttribute(){
		if (!markAppvlAttrDefault.isSelected()) {
			markAppvlAttrDefault.click();
		}
		ibm_submit.click();
		this.setSearchDisPlayPage();
	}
	//Check the checkboxes in the Approval Attributes for Approver type
	/**
	 * Find quotes in "IBMer assigned" tab
	 * @author Li Yu
	 * @date April 16, 2013
	 * @return
	 */
	public SQODisplayStatusSearchReslutPage findQuoteByIBMer() {
		
		quoteTypeFilter0.click();
		quoteTypeFilter1.click();
		anonymousResolutionButtonClick("lob_filter1");
		anonymousResolutionButtonClick("lob_filter2");
		anonymousResolutionButtonClick("lob_filter3");
		anonymousResolutionButtonClick("lob_filter4");
		anonymousResolutionButtonClick("lob_filter5");
		anonymousResolutionButtonClick("lob_filter6");
		anonymousResolutionButtonClick("lob_filter7");
		selectAllClassifications.click();
		selectAllStatus.click();	
		selectedOptionByValue("sortFilter","0",driver);
		timeFilterQuarter.click();
		selectedOptionByValue("timeFilterQuarterOptions","2013-01-01:2013-03-31",driver);
		markFilterDefault.click();

		ownerType1.click();
		driver.findElement(By.id("ownerNameOrEmail")).sendKeys("liangyue@cn.ibm.com");
		ownerRoles0.click();
		ownerRoles1.click();
		ownerRoles2.click();
		ownerRoles3.click();
		ownerRoles4.click();
		markIBMerDefault.click();
		
		
		ibm_submit.click();
		return this.displaySearchResult();
	}
	
	/**
	 * Find by quote base on site number in "Customer" tab
	 * @author Li Yu
	 * @date April 16, 2013
	 * @return
	 */
	public SQODisplayStatusSearchReslutPage findQuoteByCustomerForSite(String siteNum) {
		customerTabLnk.click();
		driver.findElement(By.id("siteNum")).sendKeys(siteNum);//0007864260 site num
		ibm_customer_site_submit.click();
		return this.displaySearchResult();
	}
	
	/**
	 * Find by Customer for name
	 * @author Li Yu
	 * @date April 16, 2013
	 * @return
	 */
	public SQODisplayStatusSearchReslutPage findQuoteByCustomerForName(String cusName) {
		customerTabLnk.click();
		driver.findElement(By.id("customer_name")).sendKeys(cusName);//customer name
		markCountryDefault.click();
		ibm_CusName_submit.click();
		return this.displaySearchResult();
	}
	
	/**
	 * Find by Partner for site num
	 * @author Li Yu
	 * @date April 16, 2013
	 * @return
	 */
	public SQODisplayStatusSearchReslutPage findQuoteByPartnerForSite(String partSiteNum) {
		partnerTabLnk.click();
		driver.findElement(By.id("partnerSiteNum")).sendKeys(partSiteNum);//0007864260 site num
		//driver.findElement(By.id("partnerTypeForNum0")).sendKeys("0");//Reseller
		partnerTypeForNum0.click();
		ibm_partner_site_submit.click();
		return this.displaySearchResult();
	}
	
	/**
	 * Find by Partner for partner name
	 * @author Li Yu
	 * @date April 16, 2013
	 * @return
	 */
	public SQODisplayStatusSearchReslutPage findQuoteByPartnerForName(String partnerName) {
		partnerTabLnk.click();
		driver.findElement(By.id("partnerName")).sendKeys(partnerName);//partner name
		//driver.findElement(By.id("partnerTypeForNum0")).sendKeys("0");//Reseller
		partnerTypeForName0.click();//Reseller
		markCountryDefault.click();
		ibm_partner_name_submit.click();
		return this.displaySearchResult();
	}
	
	/**
	 * Find by Country/region
	 * @author Li Yu
	 * @date April 16, 2013
	 * @return
	 */
	public SQODisplayStatusSearchReslutPage findQuoteByCountry() {
		countryTabLnk.click();
		selectedOptionByValue("subRegion","NAMERICA",driver);//North America
		//selectedOptionByValue("country","USA",driver);//country:USA

		anonymousResolutionButtonClick("markCountryRegionDefault");
		ibm_submit.click();
		return this.displaySearchResult();
	}
	
	/**
	 * Find by Approval attributes
	 * @author Li Yu
	 * @date April 16, 2013
	 * @return
	 */
	public SQODisplayStatusSearchReslutPage findQuoteByApprovalAttri() {
		approvalTabLnk.click();
		selectedOptionByValue("sbRegion","GCG",driver);//GCG
		//selectedOptionByValue("sbDistrict","GCG",driver);//GCG
		selectedOptionByValue("approverGroup","AG FCT",driver);//AG FCT
		selectedOptionByValue("approverType","AG FCT",driver);//AG FCT
		
		anonymousResolutionButtonClick("approverGroupFilter1");
		markAppvlAttrDefault.click();
		ibm_submit.click();
		return this.displaySearchResult();
	}
	
	/**
	 * Find by Siebel Num
	 * @author Li Yu
	 * @date April 16, 2013
	 * @return
	 */
	public SQODisplayStatusSearchReslutPage findQuoteBySiebelNum(String siebelNum) {
		siebelNumberTabLnk.click();
		driver.findElement(By.id("number")).sendKeys(siebelNum);

		// Include related quotes in the search results 
		anonymousResolutionButtonClick("relatedQuoteFlag");
		//Include common selection criteria as part of the search 
		anonymousResolutionButtonClick("commonCriteriaFlag");
		ibm_submit.click();
		return this.displaySearchResult();
	}
	
	/**
	 * Find by Order Num
	 * @author Li Yu
	 * @date April 16, 2013
	 * @return
	 */
	public SQODisplayStatusSearchReslutPage findQuoteByOrderNum(String orderNum) {
		orderNumberTabLnk.click();
		driver.findElement(By.id("number")).sendKeys(orderNum);

		ibm_submit.click();
		return this.displaySearchResult();
	}
	
	/**
	 * Find by Quote Num
	 * @author Li Yu
	 * @date April 16, 2013
	 * @return
	 */
	public SQODisplayStatusSearchReslutPage findQuoteByQuoteNum(String quoteNum) {
		quoteNumTabLnk.click();
		driver.findElement(By.id("number")).sendKeys(quoteNum);

		// Include related quotes in the search results 
		anonymousResolutionButtonClick("relatedQuoteFlag");
		//Include common selection criteria as part of the search 
		anonymousResolutionButtonClick("commonCriteriaFlag");
		
		ibm_submit.click();
		return this.displaySearchResult();
	}
	
	private SQODisplayStatusSearchReslutPage displaySearchResult(){
		searchDisPlayPage = new SQODisplayStatusSearchReslutPage(this.driver);
		loadPage(searchDisPlayPage, this.WAIT_TIME);
		return searchDisPlayPage;
	}
	
	public void setSearchDisPlayPage(){
		searchDisPlayPage = this.displaySearchResult();
		this.setSearchDisPlayPage(searchDisPlayPage);
	}

	public SQODisplayStatusSearchReslutPage getSearchDisPlayPage() {
		return searchDisPlayPage;
	}

	public void setSearchDisPlayPage(
			SQODisplayStatusSearchReslutPage searchDisPlayPage) {
		this.searchDisPlayPage = searchDisPlayPage;
	}
	
	public class IBMerAssignee{
		private String email;
		private int ownerRole;
		
		public IBMerAssignee(String email, int ownerRole){
			this.email = email;
			this.ownerRole = ownerRole;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public int getOwnerRole() {
			return ownerRole;
		}
		public void setOwnerRole(int ownerRole) {
			this.ownerRole = ownerRole;
		}
	}
}
