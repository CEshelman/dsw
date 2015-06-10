package com.ibm.dsw.automation.pageobject.sqo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.common.TestUtil;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.pageobject.pgs.BrowseSoftwareAsServiceTabPage;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;
public class SQOPartsAndPricingTabPage extends PartsAndPricingTabPage { 
	public static WebdriverLogger loggerContxt = WebdriverLogger.getLogger(SQOPartsAndPricingTabPage.class.getName());
	
	@FindBy(linkText = "Parts and pricing")
	public WebElement partsAndPricingTabLink;
	//WebElement qtyElt =findElementByXPath(".//input[contains(@id,'_QTY_hidden')and contains(@id, '"+partNum+"')]");
	@FindBy(xpath="//input[contains(@id,'_D0V7NLL_') and contains(@id,'applncPocIndNo') and @value='N' and @type='radio']")
	public WebElement D0V7NLL_NO;
	
	@FindBy(xpath="//input[contains(@id,'_D0V5DLL_') and contains(@id,'applncPocIndNo') and @value='N' and @type='radio']")
	public WebElement D0V5DLL_NO;
	@FindBy(linkText="D0V7NLL")
	public WebElement D0V7NLL;
	@FindBy(linkText = "Sales information")
	public WebElement  salesInformationTabLink;
	
	
	@FindBy(linkText = "Special bid")
	public WebElement  specialBidTabLink;
	
	@FindBy(id = "_MIGRATION_FLAG_SAASID1")
	public WebElement migrationFlagSaasId1;

	@FindBy(id = "_RENEWAL_FLAG_SAASID2")
	public WebElement renewalFlagSaasId2;
	
	@FindBy(linkText="Browse parts (excludes Software as a Service)")				
	public WebElement  browsePartsLink;
	
	@FindBy(linkText = "Browse Software as a Service")
	public WebElement browseSAASPartLink;
	
	@FindBy(linkText="Find parts (excludes Software as a Service)")
	public WebElement  findPartsLink;
	
	@FindBy(id = "_D576HLL_1_OVERRIDE_UNIT_PRC")
	public WebElement  overrideUnitPrc1;
	
	@FindBy(id = "_E02F1LL_2_OVERRIDE_UNIT_PRC")
	public WebElement  overrideUnitPrc2;
	@FindBy(id = "pymntTermsInput")
	public WebElement  payMentTerms;
	
	@FindBy(linkText = "Recalculate quote")
	public WebElement  recalculateQuote;
	public WebElement pymntRadioDays;
	
	@FindBy(xpath =  "/html/body/div/div[2]/div/div/div[2]/div/form/table/tbody/tr[4]/td/table/tbody/tr[5]/td/a")
	public WebElement editProvisioning;
	
	@FindBy(xpath =  "/html/body/div/div[2]/div/div/div[2]/div/form/table/tbody/tr[4]/td/table/tbody/tr[26]/td/a")
	public WebElement editProvisioning1;
	
	@FindBy(xpath = "/html/body/div/div[2]/div/div/div[2]/div/form/table/tbody/tr[4]/td/table/tbody/tr[5]/th/div/strong")
	public WebElement provisionId;
	
	@FindBy(xpath ="/html/body/div/div[2]/div/div/div[2]/div/form/table/tbody/tr[4]/td/table/tbody/tr[6]/th/div/strong")
	public WebElement configId;
	
	@FindBy(xpath="//div[contains(.,'SQO reference:') ]/following-sibling::div[position()=1]")
	public WebElement SQONum;
	
	@FindBy(xpath = "//html/body/div/div[2]/div/div/div[2]/div/form/table/tbody/tr[5]/td/table/tbody/tr[3]/td[4]/a")
	public WebElement  D56LALLL_Qty_link;
	
	@FindBy(xpath = "//html/body/div/div[2]/div/div/div[2]/div/form/table/tbody/tr[5]/td/table/tbody/tr[10]/td[4]/a")
	public WebElement  D612WLL_Qty_link;
	
	
	@FindBy(xpath = "//a[@title='Delete this line item D5CF5LL']")
	public WebElement delete_lotus123;
	
//	@FindBy(xpath = "//html/body/div/div[2]/div/div/div[2]/div/form/div[3]/div[5]/div/div/table/tbody/tr[3]/td/a[2]")
	@FindBy(xpath="//a[contains(@id,'DELETE_LINE_ITEM')]")
	public WebElement delete_lotusMassging;

	@FindBy(xpath = "//a[contains(@title, 'Delete this line item')]")
	public List<WebElement> deletePartLinks;

	@FindBy(id = "browse_hosted_services")
	public WebElement browseSaas;

	@FindBy(xpath = "//a[@title='Remove configuration']")
	public WebElement removeSaasLink;
	
	@FindBy(xpath = "//a[@title='Calculate Equity Curve discount guidance']")
	public WebElement ecGuideLink;
	@FindBy(linkText = "Review/update omitted renewal lines")
	public WebElement  omittedLineLink;
	
	public WebElement overallYtyGrowth;
	
	@FindBy(xpath = "//input[contains(@id, '_YTY_GROWTH')]")
	public List<WebElement> ytyGrowthInputBox;
	
	@FindBy(id="quoteDiscountPercent")
	private WebElement quoteDiscountPercentInput;
	
	
	public SQOPartsAndPricingTabPage(WebDriver driver) {
		super(driver);
	}
	
	public void setGrowthPercentToAll(double percent){
		 for (WebElement i: ytyGrowthInputBox){
			 i.click();
			 i.sendKeys(percent+"");
		 }
	}
	public SQOOmittedLinePage viewOmittedLines(){
		this.omittedLineLink.click();
		return new SQOOmittedLinePage(this.driver);
	}
	
	@Override
	public SQOBrowsePartsTabPage browsePartsLinkClick() {
		loggerContxt.info("Click on 'Browse parts (excludes Software as a Service)' link on 'My current quote ' page.");
		browsePartsLink.click();
		loggerContxt.info("Action done.");
		checkPageAvailable(getClass());
		SQOBrowsePartsTabPage page = new SQOBrowsePartsTabPage(this.driver);
		loggerContxt.info("Navigate to 'Part select' page.");
		return page;
	}
	
	@Override
	public SQOFindPartsTabPage findPartsLinkClick() {
		findPartsLink.click();
		SQOFindPartsTabPage page = new SQOFindPartsTabPage(this.driver);
		loggerContxt.info("Click on Find parts (excludes Software as a Service--->done!");
		return page;
	}
	
	public BrowseSoftwareAsServiceTabPage SQOBrowseSAASPartsLinkClick() {
		loggerContxt.info("Click on the 'Browse Software as a Service' link on 'My current quote' page.");
		browseSAASPartLink.click();
		loggerContxt.info("Action done");
		checkPageAvailable(getClass());
		BrowseSoftwareAsServiceTabPage page = new BrowseSoftwareAsServiceTabPage(this.driver);
		loggerContxt.info("Navigate to 'Part select ' page.");
		return page;
	}

	public SQOSalesInfoTabPage gotoSalesInfoTab() {
		loggerContxt.info("Click on 'Sales information' tab on 'My current quote' page.");
		salesInformationTabLink.click();
		loggerContxt.info("Action done");
		checkPageAvailable(getClass());
		SQOSalesInfoTabPage page = new SQOSalesInfoTabPage(this.driver);
		loggerContxt.info("Navigate to 'Sales information' tab on 'My current quote' page.");
		return page;
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
	public SQOPartsAndPricingTabPage load(String loginUser, String loginCntry, String accessLevel, String quoteType, String acquisition, String country) {

		SQOJumpPage jumpPage = new SQOJumpPage(driver);
		jumpPage.loginIn(loginCntry, loginUser, accessLevel);

		SQOHomePage sqoHomePage = new SQOHomePage(driver);

		loggerContxt.debug("go to sqo home page finished.....");
		SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();

		// Create a SQO quote
		SQOMyCurrentQuotePage currentQuotePage = cq.createQuote(quoteType, acquisition, country);
		loggerContxt.debug("create quote page finished.....");

		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();

		return partsAndPricingTab;
	}


	/**
	 * Add some test parts by click on browser part link.
	 */
	public void browseSomeParts() {
		SQOBrowsePartsTabPage browsePartsTabPage = browsePartsLinkClick();
		browsePartsTabPage.addSelectedPartsToDraftQuote();
	}
	
	
	@Override
	public SQOBrowseSoftwareAsServiceTabPage browseSoftwareAsAServiceLinkClick() {
		elementClickByLinkText("Browse Software as a Service");
		SQOBrowseSoftwareAsServiceTabPage page = new SQOBrowseSoftwareAsServiceTabPage(this.driver);
		return page;
	}
	/**
	 * 
	 * 2.add override price, as the parts are obsolete. 
	 * D576HLL --- 11000
	 * E02F1LL --- 2390
	 * set Payment terms to 40, set Validity days to 30, recalculate quote
	 * make sure that:   0.75% Uplift applied is displayed
	 */
	public void editPPTab(String unitPrc1,String unitPrc2,String payTerms) {
		overrideUnitPrc1.sendKeys(unitPrc1);
		overrideUnitPrc2.sendKeys(unitPrc2);
		pymntRadioDays.click();
		payMentTerms.sendKeys(payTerms );
	}
	

	
	public SQOValueUnitCalculotorPage goDispQuoteReslt4D56LALLL() {
		D56LALLL_Qty_link.click();
		SQOValueUnitCalculotorPage page = new SQOValueUnitCalculotorPage(driver);
		return page;
	}
	
	public SQOValueUnitCalculotorPage goDispQuoteReslt4D612WLL() {
		D612WLL_Qty_link.click();
		SQOValueUnitCalculotorPage page = new SQOValueUnitCalculotorPage(driver);
		return page;
	}
	
	
	public SQOPartsAndPricingTabPage recalculateQuotePPTab() {
		loggerContxt.info("Click on the 'Recalculate quote' link on 'My current quote' page.");
		recalculateQuote.click();
		loggerContxt.info("Action done");
		waitForElementLoading(1500L);
		return new SQOPartsAndPricingTabPage(driver);
	}
	
	public void editProvisioningClick(){
		editProvisioning.click();
	}
	
	public void editProvisioning1Click(){
		editProvisioning1.click();
	}
	
	public void editProvisioningClickByBrandCode(String brandCode){
		String xpath = "//a[contains(@onclick, 'saasBrandCode="+brandCode+"')]";
		WebElement webElement = findElementByXPath(xpath);
		
		if(webElement != null ){
			webElement.click();
		}
		
	}
	
	public String getProvisionId(){
		String fulltext = provisionId.getText();
		String provisionID="";
		int index = fulltext.indexOf(':');
		loggerContxt.info(fulltext);
		loggerContxt.info(index);
		if (index >0){
			provisionID = fulltext.substring(index+2);
		}
		loggerContxt.info(provisionID);
		return provisionID;
	}
	
	public String getConfigId() {
		String fulltext = configId.getText();
		String configId = "";
		int index = fulltext.indexOf(':');
		loggerContxt.info(fulltext);
		loggerContxt.info(index);
		if (index >0){
			configId = fulltext.substring(index+2);
		}
		loggerContxt.info(configId);
		return configId;
	}
	
	public String getSQONum(){
		return SQONum.getText();
	}

	
	public void additionalYearSelect(String part_num, String years, String seqnum) {
		if ("-1".equals(seqnum)) {
//			selectedOptionByValue("_#{part_num}_\d*_MAINTAIN_ADDI_YEAR", years, getDriver());
		} else {
//			selectedOptionByValue("_" + part_num + "_" + seqnum + "_MAINTAIN_ADDI_YEAR", years, getDriver());
		}
		
		addYearsAndRecalculateLinkClick();
	}
	
	public void addYearsAndRecalculateLinkClick() {
//		driver.findElement(By..alt("Add years and recalculate"));
	}
	
	public void changeQuoteDiscPercent(String newPercent){
		try{
			Double.parseDouble(newPercent);
		}catch(NumberFormatException e){
			loggerContxt.error(String.format("The new percent::%s entered is not a numeric number.",newPercent));
			return;
		}
		quoteDiscountPercentInput.sendKeys(newPercent);
		this.recalculateQuotePartAndPriceTab();
	}
	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @param part_num
	 * @param part_qty
	 * @param seqnum
	 */
	public void changeQuantity(String part_num, String part_qty, String seqnum) {
//		mybrowser.text_field(:name, /#{part_num}_#{seqNum}_QTY/).set(qty.to_s)
		setValueById("_" + part_num + "_" + seqnum + "_QTY", part_qty);
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @param part_num
	 * @param line_disc_pct
	 * @param seqnum
	 */
	@Override
	public void changeDisc_pct(String part_num, String line_disc_pct, String seqnum){ 
//		mybrowser.text_field(:name, /#{part_num}_#{seqNum}_DISC_PCT/).set(disc_pct.to_s)
//		setValueById("_" + part_num + "_" + seqnum + "_DISC_PCT", line_disc_pct);
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @param part_num
	 * @param override_unit_price
	 * @param seqnum
	 */
	@Override
	public void changeOrridPrice(String part_num, String override_unit_price, String seqnum){
//		mybrowser.text_field(:name, /#{part_num}_#{seqNum}_OVERRIDE_UNIT_PRC/).set(price.to_s)
//		setValueById("_" + part_num + "_" + seqnum + "_OVERRIDE_UNIT_PRC", override_unit_price);
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @param part_num
	 * @param chnl_ovrrd_disc_pct
	 * @param seqnum
	 */
	public void changeBpOverrideDis(String part_num, String chnl_ovrrd_disc_pct, String seqnum){
//		mybrowser.text_field(:name, /#{part_num}_#{seqNum}_BP_OVERRIDE_DIS/).set(bp_override_dis.to_s)
		setValueById("_" + part_num + "_" + seqnum + "_BP_OVERRIDE_DIS", chnl_ovrrd_disc_pct);
	}
	
	public void changePartStdDates(String part_num, String start_date, String end_date, String seqnum){
		
		String changePartStdDates = "Change standard dates";
		elementClickByLinkText(changePartStdDates);
		
		String winHandleBefore = driver.getWindowHandle(); 
		
		for(String winHandle : driver.getWindowHandles()) {  
            System.out.println("+++" + winHandle);  
            driver.switchTo().window(winHandle);  
        }  
		
		if (!StringUtils.isBlank(start_date)) {
			
		}
		if (!StringUtils.isBlank(end_date)) {
			
		}
		
		elementClickByName("ibm-submit");
		
		driver.close();
		driver.switchTo().window(winHandleBefore);  
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @param part_num
	 * @param cmprss_cvrage_mths
	 * @param seqnum
	 */
	public void changeCmprssCvrageMths(String part_num, String cmprss_cvrage_mths, String seqnum){
//		mybrowser.select_list(:id, /_#{part_num}_#{seqNum}_cmprssCovrageMonth/).select_value(cmprss_cvrage_mths)
		setValueById("_" + part_num + "_" + seqnum + "_cmprssCovrageMonth", cmprss_cvrage_mths);
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 14, 2013
	 * @param part_num
	 * @param cmprss_cvrage_disc_pct
	 * @param seqnum
	 */
	public void changeVCmprssCvrageDiscPct(String part_num, String cmprss_cvrage_disc_pct, String seqnum){
//		mybrowser.text_field(:name, /#{part_num}_#{seqNum}_cmprssCovrageDisc/).set(v_cmprss_cvrage_disc_pct.to_s)
		setValueById("_" + part_num + "_" + seqnum + "_cmprssCovrageDisc", cmprss_cvrage_disc_pct);
	}
	public SQOPartsAndPricingTabPage deleteSoftwarePart(String partNum) {
		String id = "//a[@title='Delete this line item ?']";
		id = StringUtils.replace(id, "?", partNum);
		elementClickByXPath(id);
		
		SQOPartsAndPricingTabPage pptab = new SQOPartsAndPricingTabPage(driver);
		return pptab;
	}

	public SQOPartsAndPricingTabPage removeSaasPart() {
		//if (isTextPresent("Remove configuration")) {
		waitForElementLoading(500L);
		if(isElementDisplayed(removeSaasLink)){
			removeSaasLink.click();
		}
		SQOPartsAndPricingTabPage pptab = new SQOPartsAndPricingTabPage(driver);
		return pptab;
	}
	
	public SQOPartsAndPricingTabPage saveQuoteAsDraftQuote(){
		saveQuoteAsDraftLink.click();
		return new SQOPartsAndPricingTabPage(getDriver());
	}
	
	private String getSaasElementIDStr(String configurationID, String saasPID, String elementPostfix){
		String elementID = "";
		if (StringUtils.isNotBlank(configurationID)) {
			elementID = "_" + configurationID + elementPostfix;
		} else {
			elementID="_"+saasPID+TestUtil.currentDateStr("yyyyMMdd", getServerTimeZone())+elementPostfix;
		}
		return elementID;
	}
	
	/**
	 * 4.in part and pricing tab,when modify the service date  No is checked (default is "no") for a configuration in the quote 
		validation points:
		
		Modify service dates?                              ,no option list of 'If desired select service start or end date' display;
		Estimated provisioning days                         no the input text display;
		If desired select service start or end date        no End date display
	 * @author wesley
	 * @date Feb 17, 2013
	 * */
	@Override
	public void validateModifyServiceDateNo(String flg104or106){
		//_5725F7220130313_SERVICE_TERM_EXTENSION_ID2
		WebElement eltModifyServiceDateNoradio =findElementByXPath(".//input[contains(@id,'SERVICE_TERM_EXTENSION') and @value='false']");
		
		loggerContxt.info("in part and pricing tab,when modify the service date  No is checked (default is 'no').....");
		if(eltModifyServiceDateNoradio.isSelected()){
			
			//_5725F7220130217_SERVICE_DATE_MODTYPE
			loggerContxt.info("no option list of 'If desired select service start or end date' display.....");
			WebElement eltServiceDateModtypeOption = null;
			if (flg104or106.contains("104")) {
				eltServiceDateModtypeOption = findElementByXPath(".//select[contains(@id,'_SERVICE_DATE_MODTYPE')]");
			} else {
				eltServiceDateModtypeOption = findElementByXPath(".//input[contains(@name,'_SERVICE_DATE_MODTYPE')]");
			}
			assertObjectEquals( eltServiceDateModtypeOption.isDisplayed(),false);
			
			//_5725F7220130217_PROVISIONING_DAYS
			WebElement eltEstProvisionDayText =findElementByXPath(".//input[contains(@id,'_PROVISIONING_DAYS') and @type='text']");
			if (flg104or106.contains("104")) {
				assertObjectEquals( eltEstProvisionDayText.isDisplayed(), false);
				loggerContxt.info("no 'Estimated provisioning days' display....." + flg104or106);
			} else {
				assertObjectEquals( eltEstProvisionDayText.isDisplayed(), true);
				loggerContxt.info("no 'Estimated provisioning days' display....." + flg104or106);
			}
		}
		
	}
	
	/**
	 * 4.in part and pricing tab,when modify the service date  No is checked (default is "no") for a configuration in the quote 
		validation points:
		
		Modify service dates?                              ,no option list of 'If desired select service start or end date' display;
		Estimated provisioning days                         no the input text display;
		If desired select service start or end date        no End date display
	 * @author wesley
	 * @date Feb 17, 2013
	 * */
	public void validate106ModifyServiceDateNo(){
	
		//_5725F7220130313_SERVICE_TERM_EXTENSION_ID2
		WebElement eltModifyServiceDateNoradio =findElementByXPath(".//input[contains(@id,'SERVICE_TERM_EXTENSION') and @value='false']");
		
		loggerContxt.info("in part and pricing tab,when modify the service date  No is checked (default is 'no').....");
		if(eltModifyServiceDateNoradio.isSelected()){
			
			//_5725F7220130217_SERVICE_DATE_MODTYPE
			loggerContxt.info("no option list of 'If desired select service start or end date' display.....");
			
			WebElement eltServiceDateModtypeOption = findElementByXPath(".//label[contains(@for,'_SERVICE_DATE_MODTYPE')]");
			assertObjectEquals( eltServiceDateModtypeOption.isDisplayed(),false);
			
			//_5725F7220130217_PROVISIONING_DAYS
			loggerContxt.info("no 'Estimated provisioning days' display.....");
			WebElement eltEstProvisionDayText =findElementByXPath(".//input[contains(@id,'_PROVISIONING_DAYS')]");
			assertObjectEquals( eltEstProvisionDayText.isDisplayed(),true);
		}
		
	}
	
	public void validateModifyServiceDateExist(boolean isExist) {
		boolean isPresent = isTextPresent("Modify service dates");
		if (isExist) {
			assertObjectEquals(isPresent, true);
		} else {
			assertObjectEquals(isPresent, false);
		}
	}
	
	/**
	 * 4.in part and pricing tab,selects "yes" to modify the service date question for a configuration in the quote 
		validation points:
		
		Modify service dates?                               radio button works well;when the service date question (default is "no"),no option list of 'If desired select service start or end date' display;
		Estimated provisioning days                         the input text works well;
		If desired select service start or end date         the option works well,if wrong input,the err msg comes out(TODO);After recaculating,the selected date could display the same as you selected
		the term field                                      After recaculating,the selected date could display the correct value as you selected(TODO)
	 * @author wesley
	 * @date Feb 17, 2013		
	 * */
	public SQOPartsAndPricingTabPage validateModifyServiceDateYes(String saasPID, String estmProvisioningDays, String estDate, String flg104or106){
		//_5725F7220130217_SERVICE_TERM_EXTENSION_ID2
/*		String modifyServiceDateRadioYes = getSaasElementIDStr(configurationID, saasPID,"_SERVICE_TERM_EXTENSION_ID1");
		WebElement eltModifyServiceDateRadioYes = findElementById(modifyServiceDateRadioYes);
		*/
		//_5725F7220130313_SERVICE_TERM_EXTENSION_ID1
		WebElement eltModifyServiceDateRadioYes =findElementByXPath(".//input[contains(@id,'SERVICE_TERM_EXTENSION') and @value='true']");
		
		
		SQOPartsAndPricingTabPage pptab=null;
		
		loggerContxt.info("in part and pricing tab,when modify the service date  yES is checked (default is 'no').....");
		if(isElementDisplayed(eltModifyServiceDateRadioYes)){
			eltModifyServiceDateRadioYes.click();
			waitForElementLoading(50L);
			
			//_5725F7220130217_SERVICE_DATE_MODTYPE
			WebElement eltServiceDateModtypeOption = null;
			
			if (flg104or106.contains("104")) {
				eltServiceDateModtypeOption = findElementByXPath(".//select[contains(@id,'_SERVICE_DATE_MODTYPE')]");
				assertObjectEquals( eltServiceDateModtypeOption.isDisplayed(), true);
				loggerContxt.info("'SERVICE_DATE_MODTYPE' is displayed....." + flg104or106);
			} else {
				eltServiceDateModtypeOption = findElementByXPath(".//input[contains(@name,'_SERVICE_DATE_MODTYPE')]");
				assertObjectEquals( eltServiceDateModtypeOption.isDisplayed(), false);
				loggerContxt.info("'SERVICE_DATE_MODTYPE' is displayed....." + flg104or106);
			}
			
			//_5725F7220130217_SERVICE_DATE_MODTYPE
			//String serviceDateModtypeOption = getSaasElementIDStr(configurationID, saasPID,"_SERVICE_DATE_MODTYPE");
	
			//assertElementPresentTrue(serviceDateModtypeOption);
			/*
			* select the  SERVICE_DATE_MODTYPE
			* <option value="RS"> Projected Service Start Date</option>
			* <option value="LS"> Projected Service Start By Date</option>
			* <option value="CE"> Service End Date</option>
			*/
			selectedOptionByValue(By.xpath(".//select[contains(@name,'_SERVICE_DATE_MODTYPE')]"), "CE", getDriver());
			
			/*
			* _5725F7220130217extensionDay
			* _5725F7220130217extensionMonth
			* _5725F7220130217extensionYear
			*//*
			String extensionDayEltId = getSaasElementIDStr(configurationID, saasPID,"extensionDay");
			String extensionMonthEltId = getSaasElementIDStr(configurationID, saasPID,"extensionMonth");
			String extensionYearEltId = getSaasElementIDStr(configurationID, saasPID,"extensionYear");
			*/
			/*
			* _5725F7220130217extensionDay
			* _5725F7220130217extensionMonth
			* _5725F7220130217extensionYear
			*/
			String extensionDayEltId = ".//select[contains(@id,'extensionDay')]";
			String extensionMonthEltId = ".//select[contains(@id,'extensionMonth')]";
			String extensionYearEltId =".//select[contains(@id,'extensionYear')]";
			
			//exist or not
			WebElement extensionDayElt = findElementByXPath(extensionDayEltId);
			WebElement extensionMonthElt = findElementByXPath(extensionMonthEltId);
			WebElement extensionYearElt = findElementByXPath(extensionYearEltId);
			assertObjectEquals(extensionDayElt.isDisplayed(), true);
			assertObjectEquals(extensionMonthElt.isDisplayed(), true);
			assertObjectEquals(extensionYearElt.isDisplayed(), true);
			
			String[] targetDateStrArr=TestUtil.addTargetDayFromCurrentDaay(360);
			selectedOptionByValue(By.xpath(extensionDayEltId), targetDateStrArr[0], getDriver());
			selectedOptionByValue(By.xpath(extensionMonthEltId), targetDateStrArr[1], getDriver());
			selectedOptionByValue(By.xpath(extensionDayEltId), targetDateStrArr[2], getDriver());
			
			//set Estimated order date start
			String[] estDateArray=TestUtil.addTargetDayFromCurrentDaay(Integer.valueOf(estDate));
			
			loggerContxt.info("select Estimated Orde Date: Day "+estDateArray[2]+"   Month "+estDateArray[1]+"  Year "+estDateArray[0]);
			selectedOptionByValue(By.id("estmtdOrdDay"), estDateArray[2],getDriver());
			selectedOptionByValue(By.id("estmtdOrdMonth"), estDateArray[1],getDriver());
			selectedOptionByValue(By.id("estmtdOrdYear"), estDateArray[0],getDriver());
			loggerContxt.info("select Estimated Orde Date end");
			
			
			//_5725F7220130217_PROVISIONING_DAYS
			loggerContxt.info("no 'Estimated provisioning days' display.....");
			WebElement eltEstProvisionDayText =findElementByXPath(".//input[contains(@id,'_PROVISIONING_DAYS')]");
			assertObjectEquals( eltEstProvisionDayText.isDisplayed(),true);
			

			sendKeys(eltEstProvisionDayText,estmProvisioningDays);
			
			//After recaculating,
			recalculateQuote.click();
			
			pptab= new SQOPartsAndPricingTabPage(getDriver());
			
			
			//TODO NEED A TEST WITH REGEX 
			WebElement endDate =findElementByXPath(".//tr[td[contains(text(),'End date')]]");
			loggerContxt.info("endDate....."+endDate.getText());
			
			WebElement eltModifyServiceDateRadioNo =findElementByXPath(".//input[contains(@id,'SERVICE_TERM_EXTENSION') and @value='false']");
			eltModifyServiceDateRadioNo.click();
			return pptab;
		}

		pptab = new SQOPartsAndPricingTabPage(getDriver());
		return pptab;
	}
	
	public String findConfigIdByPartID(String partID){
		String configID = "";
		WebElement element = findElementByXPath("//a[contains(@onclick,'editIbmProdId="+partID+"')]");
		String onClick = element.getAttribute("onclick");
		
		int start = onClick.indexOf("editConfigrtnId=") + new String("editConfigrtnId=").length();
		int end = onClick.indexOf(",", start);
		configID = onClick.substring(start, end);
		return configID;
	}
	public String findProvisionIdByBrandCode(String brandCode){
		String provisionID = "";
		WebElement element = findElementByXPath("//a[contains(@onclick,'saasBrandCode="+brandCode+"')]");
		String onClick = element.getAttribute("onclick");
		
		int start = onClick.indexOf("provisngIdForBrand=") + new String("provisngIdForBrand=").length();
		int end = onClick.indexOf(",", start);
		provisionID = onClick.substring(start, end);
		return provisionID;
	}

	private String configurationID;
	
	public String getConfigurationID() {
		return configurationID;
	}

	public void setConfigurationID(String configurationID) {
		this.configurationID = configurationID;
	}
	
	/**
	 * 
	 * @param saasPID                  current parts and pricing ID
	 * @param modeType                 Extension type
	 * @param yesRadio                 is selected yes radio
	 * @param provDays                 estimated provisioning days
	 * @param quoteStartDate           Quote Start Date  
	 * @param expirDate                Quote Expiration Date
	 * @param estOrdDate               Estimated Order Date
	 * @param extenDate                Extension Service Date
	 * @return
	 */
	public SQOPartsAndPricingTabPage setExtenDateValue(String saasPID, String type,  boolean yesRadio, int provDays, Calendar quoteStartDate, Calendar expirDate, Calendar estOrdDate, Calendar extenDate ){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		
		//_5725F7220130217_SERVICE_TERM_EXTENSION_ID2
		String modifyServiceDateRadioYes = getSaasElementIDStr(configurationID, saasPID,"_SERVICE_TERM_EXTENSION_ID1");
		WebElement eltModifyServiceDateRadioYes = findElementById(modifyServiceDateRadioYes);
			
		SQOPartsAndPricingTabPage pptab=null;
		
		loggerContxt.info("in part and pricing tab,when modify the service date  No is checked (default is 'no').....");
		if(yesRadio){
			if(isElementDisplayed(eltModifyServiceDateRadioYes)){
				eltModifyServiceDateRadioYes.click();
				waitForElementLoading(50L);
				
				//_5725F7220130217_SERVICE_DATE_MODTYPE
				String serviceDateModtypeOption = getSaasElementIDStr(configurationID, saasPID,"_SERVICE_DATE_MODTYPE");
				assertElementPresentTrue(serviceDateModtypeOption);
				/*
				* select the  SERVICE_DATE_MODTYPE
				* <option value="RS"> Projected Service Start Date</option>
				* <option value="LS"> Projected Service Start By Date</option>
				* <option value="CE"> Service End Date</option>
				*/
				if(type!=null && type.length()>0)
					selectedOptionByValue(serviceDateModtypeOption, type, getDriver());
				
				/*
				* _5725F7220130217extensionDay
				* _5725F7220130217extensionMonth
				* _5725F7220130217extensionYear
				*/
				String extensionDayEltId = getSaasElementIDStr(configurationID, saasPID,"extensionDay");
				String extensionMonthEltId = getSaasElementIDStr(configurationID, saasPID,"extensionMonth");
				String extensionYearEltId = getSaasElementIDStr(configurationID, saasPID,"extensionYear");
				
				//Set extension service date
				assertElementPresentTrue(extensionDayEltId);
				assertElementPresentTrue(extensionMonthEltId);
				assertElementPresentTrue(extensionYearEltId);
				
				String yyyyMMddExt = sdf.format(extenDate.getTime());
				loggerContxt.info("Extension service date:" + yyyyMMddExt);
				String[] targetDateStrArrExt = yyyyMMddExt.split("-");
				selectedOptionByValue(extensionYearEltId, targetDateStrArrExt[0], getDriver());
				selectedOptionByValue(extensionMonthEltId, targetDateStrArrExt[1], getDriver());
				selectedOptionByValue(extensionDayEltId, targetDateStrArrExt[2], getDriver());
				
				
				//set Estimated order date start
				String yyyyMMddEst = sdf.format(estOrdDate.getTime());
				loggerContxt.info("Extension service date:" + yyyyMMddEst);
				String[] targetDateStrArrEst = yyyyMMddEst.split("-");
				
				loggerContxt.info("select Estimated Orde Date: Day "+targetDateStrArrEst[2]+"   Month "+targetDateStrArrEst[1]+"  Year "+targetDateStrArrEst[0]);
				selectedOptionByValue("estmtdOrdDay",targetDateStrArrEst[2],getDriver());
				selectedOptionByValue("estmtdOrdMonth",targetDateStrArrEst[1],getDriver());
				selectedOptionByValue("estmtdOrdYear",targetDateStrArrEst[0],getDriver());
				loggerContxt.info("select Estimated Orde Date end");
				
				
				//_5725F7220130217_PROVISIONING_DAYS
				//Set Estimated Provisioning Days
				String estProvisionDayText = getSaasElementIDStr(configurationID, saasPID,"_PROVISIONING_DAYS");
				assertElementPresentTrue(estProvisionDayText);
				WebElement estProvisionDayTextElt = findElementById(estProvisionDayText);
				sendKeys(estProvisionDayTextElt,String.valueOf(provDays));
				
				//set Expiration Order Date
				String yyyyMMddExp = sdf.format(expirDate.getTime());
				loggerContxt.info("Extension service date:" + yyyyMMddExp);
				String[] targetDateStrArrExp = yyyyMMddEst.split("-");

				loggerContxt.info("select ExpirationDate: Day " + targetDateStrArrExp[2]+ "   Month " + targetDateStrArrExp[1] + "  Year " + targetDateStrArrExp[0]);
				selectedOptionByValue("expirationDay", targetDateStrArrExp[2], driver);
				selectedOptionByValue("expirationMonth", targetDateStrArrExp[1], driver);
				selectedOptionByValue("expirationYear", targetDateStrArrExp[0], driver);
				loggerContxt.info("select ExpirationDate end");
				
				
				//set Quote start date
				String yyyyMMddStr = sdf.format(expirDate.getTime());
				loggerContxt.info("Extension service date:" + yyyyMMddStr);
				String[] targetDateStrArrStr = yyyyMMddStr.split("-");
				loggerContxt.info("select StartDate: Day " + targetDateStrArrStr[2]+ "   Month " + targetDateStrArrStr[1] + "  Year " + targetDateStrArrStr[0]);
           		selectedOptionByValue("startDay", targetDateStrArrStr[2], driver);
           		selectedOptionByValue("startMonth", targetDateStrArrStr[1], driver);
           		selectedOptionByValue("startYear", targetDateStrArrStr[0], driver);
           		loggerContxt.info("select StartDate end");
				                                       		
				//After recaculating,
				recalculateQuote.click();
				
				pptab= new SQOPartsAndPricingTabPage(getDriver());
				
				return pptab;
			}
		}
		
		pptab= new SQOPartsAndPricingTabPage(getDriver());
		return pptab;
	}
	public SQOPartsAndPricingTabPage deleteOnePart(){
		this.deletePartLinks.get(this.deletePartLinks.size()/2).click();
		return new SQOPartsAndPricingTabPage(getDriver());
	}
	
	
	public SQOPartsAndPricingTabPage checkECGuide( ){
		
		SQOPartsAndPricingTabPage pptab=null;
		
		loggerContxt.info("in part and pricing tab,when modify the service date  No is checked (default is 'no').....");
			if(isElementDisplayed(ecGuideLink)){
				ecGuideLink.click();
				
				pptab= new SQOPartsAndPricingTabPage(getDriver());
				
				assertTextPresentTrue(prop.getProperty("ec_top_performer"));
				assertTextPresentTrue(prop.getProperty("ec_market_average"));
				assertTextPresentTrue(prop.getProperty("ec_coverage_date"));
			}
			
			return pptab;
			
	}
	
	public void modifyOverridePrice(String overridePrice){
		
		//get all the element and then click
		List<WebElement>  elmtsLists=getAllWebElements(By.xpath(".//input[contains(@id, 'OVERRIDE_UNIT_PRC')]"));
		loggerContxt.info("elmtsLists size:"+elmtsLists.size());
		try{
		for(WebElement elmt:elmtsLists){
			if(elmt.isDisplayed()){
				sendKeys(elmt, overridePrice);
				//elmtsLists.remove(elmt);
			}
		}
		
		 elmtsLists=getAllWebElements(By.xpath(".//input[contains(@id, 'OVERRIDE_ENTITLED_PRC')]"));
			for(WebElement elmt:elmtsLists){
				if(elmt.isDisplayed()){
					sendKeys(elmt, overridePrice);
				}
			}
		
		
		loggerContxt.info("elmtsLists size:"+elmtsLists.size());
		}catch (Exception e) {
			loggerContxt.info("exception happened:"+e.getMessage());
		}
	} 
	
	
	public void checkMonthlyInfo(String partNum,String qty){
		
		WebElement qtyElt =findElementByXPath(".//input[contains(@id,'_QTY_hidden')and contains(@id, '"+partNum+"')]");
		//assertObjectEquals(qtyElt.getText(),qty);
			
	}
	
	public SQOMonthySwBaseConfiguratorPage return2MonthlyConfig(){
		
		WebElement qtyElt =findElementByXPath(".//a[contains(@onclick,'updateMonthlyTermFrequencies('quote.wss?jadeAction=BUILD_MONTHLY_SW_CONFIGUATOR')]");
		return new SQOMonthySwBaseConfiguratorPage(driver);
	}
	
	public SQOPartsAndPricingTabPage clickNoRadio(){
		loggerContxt.info("ready to clik No radio button");
		D0V7NLL_NO.click();
		D0V5DLL_NO.click();
		SQOPartsAndPricingTabPage pptab = new SQOPartsAndPricingTabPage(driver);
		return pptab;
	}

	public void deleteLotusParts() {
		loggerContxt.info("Click the delete icon to delete the parts.");
		delete_lotusMassging.click();
		loggerContxt.info("Action done");
		waitForElementLoading(new Long(1000));
	}
		
	public void applyoffer(String percent) {
		loggerContxt.info(String.format("Send keys:'%s' to field 'Apply offer: ' on 'My current quote' page.", percent));
		driver.findElement(By.id("offerPrice")).sendKeys(percent);
		loggerContxt.info("Action done");
		loggerContxt.info("Click on the 'Recalculate quote' link on 'My current quote' page.");
		driver.findElement(
				By.xpath("/html/body/div/div[2]/div/div/div[2]/div/form/div[3]/div[2]/div/div/div/div/div/div/div/div[2]/div[2]/a"))
				.click();
		loggerContxt.info("Action done");
		waitForElementLoading(new Long(500));
	}

	public void clickMrgnRwlRadio() {
		loggerContxt
				.info("Click 'yes' for 'Is this a legacy contract moving to an IBM agreement?'");
		migrationFlagSaasId1.click();
		loggerContxt
				.info("Click 'no' for 'Is this a legacy contract moving to an IBM agIs the contract expiring or are you doing a mid-term contract extension (e.g., renewal)?'");
		renewalFlagSaasId2.click();
		loggerContxt.info("Action done");
	}

	public void applyDiscount(String percent) {
		loggerContxt.info(String.format("Send keys:'%s' to field 'Apply offer: ' on 'My current quote' page.", percent));
		driver.findElement(By.id("quoteDiscountPercent")).sendKeys(percent);
		loggerContxt.info("Action done");
		loggerContxt.info("Click on the 'Recalculate quote' link on 'My current quote' page.");
		driver.findElement(
				By.xpath("/html/body/div/div[2]/div/div/div[2]/div/form/div[3]/div[2]/div/div/div/div/div/div/div/div/div[2]/a"))
				.click();
		loggerContxt.info("Action done");
		waitForElementLoading(new Long(500));
	}
}
