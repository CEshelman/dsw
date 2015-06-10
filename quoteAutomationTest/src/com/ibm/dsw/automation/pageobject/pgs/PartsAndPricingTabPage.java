package com.ibm.dsw.automation.pageobject.pgs;

import java.util.Date;
import java.util.List;

import org.apache.tools.ant.util.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.common.TestUtil;


public class PartsAndPricingTabPage extends PGSBasePage {

	public PartsAndPricingTabPage(WebDriver driver) {
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
	public WebElement  partsAndPricingTabLink;

	@FindBy(linkText = "Sales information")
	public WebElement  salesInformationTabLink;

	@FindBy(linkText = "Approval")
	public WebElement  approvalTabLink;

	@FindBy(linkText = "Browse parts (excludes Software as a Service)")
	public WebElement  browsePartsLink;

	@FindBy(linkText = "Find parts (excludes Software as a Service)")
	public WebElement  findPartsLink;

	@FindBy(linkText = "Browse Software as a Service")
	public WebElement  browseSoftwareAsAServiceLink;

	@FindBy(linkText = "Clear selected reseller.")
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
	
	
	@FindBy(id="jadeAction=DELETE_LINE_ITEM,action2=DISPLAY_PART_PRICE_TAB,partNum=D5CF5LL,partSeqNum=1")
	public WebElement delete_D5CF5LL;
	
	/**
	 * 
	 */
	@FindBy(linkText = "Create a copy")
	public WebElement  createACopyLink;
	
	private String lastWinHandle;
	
	public BrowsePartsTabPage browsePartsLinkClick() {
		browsePartsLink.click();
		BrowsePartsTabPage page = new BrowsePartsTabPage(this.driver);
		return page;
	}

	public BrowseSoftwareAsServiceTabPage browseSoftwareAsAServiceLinkClick() {
		browseSoftwareAsAServiceLink.click();
		checkPageAvailable(getClass());
		BrowseSoftwareAsServiceTabPage page = new BrowseSoftwareAsServiceTabPage(this.driver);
		return page;
	}
	
	public SalesInfoTabPage gotoSalesInfoTabPGS() {
		salesInformationTabLink.click();
		SalesInfoTabPage page = new SalesInfoTabPage(this.driver);
		return page;
	}

	public PGSBasePage findPartsLinkClick() {
		// findPartsLink.click();
		if (isElementExist(By.linkText("Find parts (excludes Software as a Service)"))) {
			elementClickByLinkText("Find parts (excludes Software as a Service)");
		} else if (isElementExist(By.linkText("Browse parts (excludes Software as a Service)"))) {

		}
		// } else if
		// (isElementExsit("Find parts by part number or description")) {
//			elementClickByLinkText("Find parts by part number or description");
//		} 
		PGSBasePage page = new FindPartsTabPage(this.driver);
		return page;
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 5, 2013
	 */
	public void editProvisioningFormLink() {
		elementClickByXPath("//form/table/tbody/tr[4]/td/table/tbody/tr[4]/td/p/span/span/a");
		checkPageAvailable(getClass());
		elementClickByLinkText("Return to sales quote");
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 * @param pIdPath
	 * @return
	 */
	public String getOriginalProvisioningId() {
		
		List<WebElement>  elmtsLists=getAllWebElements(By.tagName("strong"));
		String originalProvisioningId="" ;
		loggerContxt.info("elmtsLists size:"+elmtsLists.size());
		for(WebElement elmt:elmtsLists){
			if(elmt.getText().contains("Provisioning ID")){
				originalProvisioningId = elmt.getText().substring(elmt.getText().length() - 14, elmt.getText().length());
				
			}
		}
		
		
	//	WebElement pIdText = driver.findElement(By.xpath(pIdPath));
	//	String originalProvisioningId = pIdText.getText().substring(pIdText.getText().length() - 14, pIdText.getText().length());
		return originalProvisioningId;
	}
	
	
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 5, 2013
	 */
	public void createACopyLinkClick() {
		createACopyLink.click();
		elementClickById("submitForApproval");
	}
	
	public MyCurrentQuotePage createACopyLink() {
		createACopyLink.click();
		
		return new MyCurrentQuotePage(driver);

	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 * @param pIdPath
	 * @return
	 */
	public String getProvisioningId(String pIdPath) {
		WebElement pIdText = driver.findElement(By.xpath(pIdPath));
		String tmpProvisioningId = pIdText.getText().substring(pIdText.getText().length() - 14, pIdText.getText().length());
		return tmpProvisioningId;
	}
	
	public void selectExpirationDate(int targetAddDays) {

		String[] dateArray = TestUtil
				.addTargetDayFromCurrentDaay(targetAddDays);

		loggerContxt.info("select ExpirationDate: Day " + dateArray[2]
				+ "   Month " + dateArray[1] + "  Year " + dateArray[0]);
		selectedOptionByValue(By.id("expirationDay"), dateArray[2], driver);
		selectedOptionByValue(By.id("expirationMonth"), dateArray[1], driver);
		selectedOptionByValue(By.id("expirationYear"), dateArray[0], driver);
		loggerContxt.info("select ExpirationDate end");
	}
	
	public void selectExpirationDate(int targetAddDays, Date date) {

		String[] dateArray = TestUtil
				.addTargetDayFromCurrentDaay(targetAddDays, date);

		loggerContxt.info("select ExpirationDate: Day " + dateArray[2]
				+ "   Month " + dateArray[1] + "  Year " + dateArray[0]);
		selectedOptionByValue(By.id("expirationDay"), dateArray[2], driver);
		selectedOptionByValue(By.id("expirationMonth"), dateArray[1], driver);
		selectedOptionByValue(By.id("expirationYear"), dateArray[0], driver);
		loggerContxt.info("select ExpirationDate end");
	}
	
	public void selectExtensionDate(int targetAddDays) {

		String[] dateArray = TestUtil
				.addTargetDayFromCurrentDaay(targetAddDays);

		loggerContxt.info("select extension: Day " + dateArray[2]
				+ "   Month " + dateArray[1] + "  Year " + dateArray[0]);
		
		String extensionDayEltId = ".//select[contains(@id,'extensionDay')]";
		String extensionMonthEltId = ".//select[contains(@id,'extensionMonth')]";
		String extensionYearEltId =".//select[contains(@id,'extensionYear')]";
		
		selectedOptionByValue(By.xpath(extensionDayEltId), dateArray[2], driver);
		selectedOptionByValue(By.xpath(extensionMonthEltId), dateArray[1], driver);
		selectedOptionByValue(By.xpath(extensionYearEltId), dateArray[0], driver);
		
		loggerContxt.info("select extension");
	}
	
	public void selectEstimatedOrderDay(int targetAddDays) {

		String[] dateArray = TestUtil
				.addTargetDayFromCurrentDaay(targetAddDays);

		loggerContxt.info("select ExpirationDate: Day " + dateArray[2]
				+ "   Month " + dateArray[1] + "  Year " + dateArray[0]);
		selectedOptionByValue(By.id("estmtdOrdDay"), dateArray[2], driver);
		selectedOptionByValue(By.id("estmtdOrdMonth"), dateArray[1], driver);
		selectedOptionByValue(By.id("estmtdOrdYear"), dateArray[0], driver);
		loggerContxt.info("select EstimatedOrderDate end");
	}
	
	public void selectStartDate(int targetAddDays, Date date) {

		String[] dateArray = TestUtil
				.addTargetDayFromCurrentDaay(targetAddDays, date);

		loggerContxt.info("select StartDate: Day " + dateArray[2]
				+ "   Month " + dateArray[1] + "  Year " + dateArray[0]);
		selectedOptionByValue(By.id("startDay"), dateArray[2], driver);
		selectedOptionByValue(By.id("startMonth"), dateArray[1], driver);
		selectedOptionByValue(By.id("startYear"), dateArray[0], driver);
		loggerContxt.info("select StartDate end");
	}
	
	public void setExtensionService(String radioPath, String typePath, String recaluratePath){
		WebElement element = null;
		//checked extension service date to yes
		element =  driver.findElement(By.xpath(radioPath));
		element.click();
		
		//choose the extension type
		WebElement selectType = driver.findElement(By.xpath(typePath));
		List<WebElement> allTypeOptions = selectType.findElements(By.tagName("option"));
		for (WebElement option : allTypeOptions) {
			if("RS".equals(option.getAttribute("value"))){
				    option.click();
				    break;
			}
		}
		
		//set the extension date
		//set day
		WebElement selectDay = driver.findElement(By.name("extensionDay"));
		List<WebElement> allDayOptions = selectDay.findElements(By.tagName("option"));
		for (WebElement option : allDayOptions) {
			if("02".equals(option.getAttribute("value"))){
				    option.click();
				    break;
			}
		}
		
		//set month
		WebElement selectMonth = driver.findElement(By.name("extensionMonth"));
		List<WebElement> allMonthOptions = selectMonth.findElements(By.tagName("option"));
		for (WebElement option : allMonthOptions) {
			if("02".equals(option.getAttribute("value"))){
				    option.click();
				    break;
			}
		}
		
		//set year
		WebElement selectYear = driver.findElement(By.name("extensionYear"));
		List<WebElement> allYearOptions = selectYear.findElements(By.tagName("option"));
		for (WebElement option : allYearOptions) {
			if("2013".equals(option.getAttribute("value"))){
				    option.click();
				    break;
			}
		}
		
		//recal
		element =  driver.findElement(By.xpath(recaluratePath));
		element.click();
	}
	
	private void deletePart(String partNum) {		
		String id = "//a[@title='Delete this line item ?']";
		id = StringUtils.replace(id, "?", partNum);
		elementClickByXPath(id);
	}	
	
	public void deleteParts(String partNums) {
		String[] partNumArray = partNums.split(",");
		for (String partNum : partNumArray) {
			deletePart(partNum);
		}
	}
	
	public void recalculateQuotePartAndPriceTab() {
		elementClickByLinkText("Recalculate quote");
	}
	
	public String getLastWinHandle() {
		return lastWinHandle;
	}

	public void setLastWinHandle(String lastWinHandle) {
		this.lastWinHandle = lastWinHandle;
	}

	public PartAndPricingDetailsPage goToPartAndPricingDetailsPage(String partNum) {
		lastWinHandle = getDriver().getWindowHandle();
		elementClickByLinkText(partNum);
		switchToWindow(getDriver(), "IBM Partner Guided Selling: Part details");
		return new PartAndPricingDetailsPage(getDriver());
	}
	
	public void changeDisc_pct(String part_num, String line_disc_pct,
			String seqnum) {
		setValueById("_" + part_num + "_" + seqnum + "_DISC_PCT", line_disc_pct);
	}

	public void changeOrridPrice(String part_num, String override_unit_price,
			String seqnum) {
		setValueById("_" + part_num + "_" + seqnum + "_OVERRIDE_UNIT_PRC",
				override_unit_price);
	}
	
	public void changeSortSeq(String part_num, String manual_sort_seq,
			String seqnum) {
		selectedOptionByValue(By.id("_" + part_num + "_" + seqnum + "_MANUAL_SORT_SEQ"), manual_sort_seq, getDriver());
	}
	
	
	public SalesInfoTabPage goToSalesInfoTabPage(){
		salesInformationTabLink.click();
		return new SalesInfoTabPage(getDriver());
	}
	
	private String getSaasElementIDStr(String saasPID,String elementPostfix){

		String elementID="_"+saasPID+TestUtil.currentDateStr("yyyyMMdd", getServerTimeZone())+elementPostfix;
		return elementID;
	}
	
	public void validateModifyServiceDateNo(String saasPID){
		String modifyServiceDateNoradio=getSaasElementIDStr(saasPID,"_SERVICE_TERM_EXTENSION_ID2");
		WebElement eltModifyServiceDateNoradio = findElementById(modifyServiceDateNoradio);
		
		loggerContxt.info("in part and pricing tab,when modify the service date  No is checked (default is 'no').....");
		if(eltModifyServiceDateNoradio.isSelected()){
			
			//_5725F7220130217_SERVICE_DATE_MODTYPE
			String serviceDateModtypeOption=getSaasElementIDStr(saasPID,"_SERVICE_DATE_MODTYPE");
			loggerContxt.info("no option list of 'If desired select service start or end date' display.....");
			
			WebElement eltServiceDateModtypeOption = findElementById(serviceDateModtypeOption);
			assertObjectEquals( eltServiceDateModtypeOption.isDisplayed(),false);
			
			//_5725F7220130217_PROVISIONING_DAYS
			String estProvisionDayText=getSaasElementIDStr(saasPID,"_PROVISIONING_DAYS");
			loggerContxt.info("no 'Estimated provisioning days' display.....");
			
			WebElement eltEstProvisionDayText = findElementById(estProvisionDayText);
			assertObjectEquals( eltEstProvisionDayText.isDisplayed(),false);
		}
		
	}
	
	public PartsAndPricingTabPage validateModifyServiceDateYes(String saasPID,String estmProvisioningDays){
		//_5725F7220130217_SERVICE_TERM_EXTENSION_ID2
		String modifyServiceDateRadioYes=getSaasElementIDStr(saasPID,"_SERVICE_TERM_EXTENSION_ID1");
		WebElement eltModifyServiceDateRadioYes = findElementById(modifyServiceDateRadioYes);
		
		PartsAndPricingTabPage pptab= new PartsAndPricingTabPage(getDriver());
		
		loggerContxt.info("in part and pricing tab,when modify the service date  No is checked (default is 'no').....");
		if(isElementDisplayed(eltModifyServiceDateRadioYes)){
			eltModifyServiceDateRadioYes.click();
			waitForElementLoading(50L);
			
			//_5725F7220130217_SERVICE_DATE_MODTYPE
			String serviceDateModtypeOption=getSaasElementIDStr(saasPID,"_SERVICE_DATE_MODTYPE");
			assertElementPresentTrue(serviceDateModtypeOption);

			selectedOptionByValue(By.id(serviceDateModtypeOption), "CE", getDriver());
			
			String extensionDayEltId=getSaasElementIDStr(saasPID,"extensionDay");
			String extensionMonthEltId=getSaasElementIDStr(saasPID,"extensionMonth");
			String extensionYearEltId=getSaasElementIDStr(saasPID,"extensionYear");
			
			loggerContxt.info("Service date is selected.");
			
			//exist or not
			assertElementPresentTrue(extensionDayEltId);
			assertElementPresentTrue(extensionMonthEltId);
			assertElementPresentTrue(extensionYearEltId);
			
			String[] targetDateStrArr=TestUtil.addTargetDayFromCurrentDaay(360);
			selectedOptionByValue(By.id(extensionYearEltId), targetDateStrArr[0], getDriver());
			selectedOptionByValue(By.id(extensionMonthEltId), targetDateStrArr[1], getDriver());
			selectedOptionByValue(By.id(extensionDayEltId), targetDateStrArr[2], getDriver());
			
			//_5725F7220130217_PROVISIONING_DAYS
			String estProvisionDayText=getSaasElementIDStr(saasPID,"_PROVISIONING_DAYS");
			assertElementPresentTrue(estProvisionDayText);
			WebElement estProvisionDayTextElt = findElementById(estProvisionDayText);
			sendKeys(estProvisionDayTextElt,estmProvisioningDays);
			loggerContxt.info("Provisioning days field is populated.");
			
			pptab.selectEstimatedOrderDay(15);
			
			//After recalculating,
			recalculateQuotePartAndPriceTab();
			loggerContxt.info("recalculate link is clicked.");
			
			//Check that After recalculating,the selected date could display the correct value as you selected

			assertObjectEquals(verifySelectedOptionByValue(extensionYearEltId, targetDateStrArr[0], getDriver()),new Boolean("true"));
			assertObjectEquals(verifySelectedOptionByValue(extensionMonthEltId, targetDateStrArr[1], getDriver()),new Boolean("true"));
			assertObjectEquals(verifySelectedOptionByValue(extensionDayEltId, targetDateStrArr[2], getDriver()),new Boolean("true"));
			loggerContxt.info("Selected service date is verified after click recalculating link.");
			
			//verify provisioning days after click recalculate link
			estProvisionDayTextElt = findElementById(estProvisionDayText);
			assertObjectEquals(estProvisionDayTextElt.getAttribute("value"),estmProvisioningDays);
			loggerContxt.info("estimated Provisioning days are verified after click recalculating link ");
		}
		
		return pptab;
	}

	public void setProvisioningDays(String estmProvisioningDays) {
		sendKeys(getWebElementByLocator(By.xpath(".//input[contains(@id,'_PROVISIONING_DAYS')]")), estmProvisioningDays);
	}
	
	public PGSHomePage gotoHomePage() {
		//partnerGuidedSellingLink.click();
		elementClickByLinkText("Partner guided selling");
		PGSHomePage page = new PGSHomePage(this.driver);
		loadPage(page, this.WAIT_TIME);
		
		return page;
	}
	
}
