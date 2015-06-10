package com.ibm.dsw.automation.testcase.base;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.pageobject.sqo.SQOCreateQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsSelectTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSpecialBidTabPage;
import com.ibm.dsw.automation.vo.Quote;

/**
 * 
 * @author suchuang
 * @date Jan 17, 2013
 */
public class SQOAppliancePartTest extends BaseTestCase {
	
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOAppliancePartTest.class.getSimpleName());
	
	public final static String CUST_NAME = "Abott";
	public final static String RESELLER_NAME = "ecapital";
	
	//Appliance Part 
	public final static String MAIN_PART = "D0NGALL";
	public final static String REINSTATEMENT_PART = "D0NYJLL,D0NYLLL,D0NYNLL";
	public final static String UPGRADE_PART = "D0NYTLL,D0NYALL,D0NYULL";
	public final static String ADDITIONAL_SOFTWARE_PART = "D0DQ3LL,D0DQ4LL,E0949LL,E0948LL,D0DQ1LL,D0DQ2LL";
	public final static String RENEWAL_PART = "E093GLL,E0971LL,E0D1ALL,E0D4ELL,E0DBULL";
	public final static String SERVICE_PACK_PART = "D0NFVLL";
	public final static String TRANSCEIVER_PART = "D0NGBLL";
	
	//Appliance Msg
	public final static String PART_MAIN_PART_MSG = "PART_MAIN_PART";
	public final static String REINSTATEMENT_PART_MSG = "REINSTATEMENT_PART";
	public final static String UPGRADE_PART_MSG = "UPGRADE_PART";
	public final static String ADDITIONAL_SOFTWARE_PART_MSG = "ADDITIONAL_SOFTWARE_PART";
	public final static String RENEWAL_PART_MSG = "RENEWAL_PART";
	public final static String TRANSCEIVER_PART_MSG = "TRANSCEIVER_PART";
	
	public final static String UPGRADE_REINSTATEMENT_PART_MSG = "UPGRADE_REINSTATEMENT_PART";
	public final static String SPECIAL_BID_JUSTIFICATION_SUMMARY = "SPECIAL_BID_JUSTIFICATION_SUMMARY";
	
	public final static String TITLE = "_TITLE";
	public final static String DESC = "_DESC";
	
	
	private SQOMyCurrentQuotePage currentQuotePage = null;
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 15, 2013
	 * @param quoteNum
	 * @param env
	 * @return
	 */
	private Map<String, String> getCpQuoteFromDb(String quoteNum, String env) {
		Quote quote = new Quote(env);
		return quote.getQuote(quoteNum);
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 17, 2013
	 */
//	private void loginSQO() {
//		/*SQOLoginPage lp = new SQOLoginPage(this.driver);
//		lp.loginAs(getLoginUser(), "");
//		sqoHomePage = new SQOHomePage(driver);*/
//		
//		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
//
//		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf());
//		loggerContxt.info("Login SQO finished.....");
//	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 17, 2013
	 * @param quote
	 */
	public void runCreateQuote(Map<String, String> quote) {
		loginSQO();
		SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();

		// Create a SQO quote
		if (("PA".equals(quote.get("PROG_CODE").trim()))
		 || ("PAE".equals(quote.get("PROG_CODE").trim()))) {
			quote.put("PROG_CODE", "PAUN");
		}
		currentQuotePage = cq.createQuote(quote);
		loggerContxt.info("create quote page finished.....");
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".currentQuoteTitle"));
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 17, 2013
	 * @param quote
	 */
	public void runQuoteEdit(Map<String, String> quote) {
		// to create customer
		currentQuotePage.findCustByName(CUST_NAME);
		
		// to create reseller
		currentQuotePage.findResellerByName(RESELLER_NAME);
		
		currentQuotePage.selectCurrentDate();
		
		createMainPart(currentQuotePage);
		createUpgradePart(currentQuotePage);
		createUpgradeNReinstatementPart(currentQuotePage);
	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 17, 2013
	 */
	@Test(description = "Test Appliance Part")
	public void run() {
//		Map<String, String> quote = getCpQuoteFromDb("0003319858", "uat");
		
		Map<String, String> quote = new HashMap<String, String>();
		quote.put("PROG_CODE", getProperty(".lob"));
		quote.put("CNTRY_CODE", getProperty(".cntryCode"));
		
		deleteAllCookies();
		
		runCreateQuote(quote);
		runQuoteEdit(quote);
		
		quitWebdriver();
		loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
	}
	
	protected void saveQuoteAsDraftQuote(SQOMyCurrentQuotePage myCurrentQuotePage){
		SQOSpecialBidTabPage specialBidTabPage = myCurrentQuotePage.goToSpecialBidTab();
		triggerSpecialBid(specialBidTabPage);
		myCurrentQuotePage.saveQuoteAsDraftQuote();
	}
	
	protected void createMainPart(SQOMyCurrentQuotePage myCurrentQuotePage){
		SQOPartsAndPricingTabPage partsAndPricingTab = myCurrentQuotePage.goToPartsAndPricingTab();
		findMainPart(partsAndPricingTab);
		SQOSalesInfoTabPage salesInfoTabPage = myCurrentQuotePage.goToSalesInfoTab();
		setMainPartSalesInfo(salesInfoTabPage);
		saveQuoteAsDraftQuote(myCurrentQuotePage);
	}
	
	protected void createUpgradePart(SQOMyCurrentQuotePage myCurrentQuotePage){
		SQOPartsAndPricingTabPage partsAndPricingTab = myCurrentQuotePage.goToPartsAndPricingTab();
		findUpgradePart(partsAndPricingTab);
		SQOSalesInfoTabPage salesInfoTabPage = myCurrentQuotePage.goToSalesInfoTab();
		setUpgradePartInfo(salesInfoTabPage);
		saveQuoteAsDraftQuote(myCurrentQuotePage);
	}
	
	protected void createUpgradeNReinstatementPart(SQOMyCurrentQuotePage myCurrentQuotePage){
		SQOPartsAndPricingTabPage partsAndPricingTab = myCurrentQuotePage.goToPartsAndPricingTab();
		findUpgradeNReinstatementPart(partsAndPricingTab);
		SQOSalesInfoTabPage salesInfoTabPage = myCurrentQuotePage.goToSalesInfoTab();
		setUpgradeNReinstatementPartInfo(salesInfoTabPage);
		saveQuoteAsDraftQuote(myCurrentQuotePage);
	}
	
	protected void triggerSpecialBid(SQOSpecialBidTabPage specialBidTabPage){
		specialBidTabPage.enterSpcialBidInf(SPECIAL_BID_JUSTIFICATION_SUMMARY);
	}
	
	protected void findPart(SQOPartsAndPricingTabPage page,String partList){
		SQOFindPartsTabPage sqoFindPartsTabPage = page.findPartsLinkClick();
		SQOFindPartsSelectTabPage findPartsSelectTabPage =  sqoFindPartsTabPage.findPartsLinkClick(partList);
		findPartsSelectTabPage.selectPartsClick(partList);
		findPartsSelectTabPage.rtn2DraftQuote(); 
	}
	
	protected void findMainPart(SQOPartsAndPricingTabPage page){
		loggerContxt.info("find Main part");
		findPart(page,MAIN_PART);
		for(String part : MAIN_PART.split(",")){
			setOverrideUnitPrice(part,"10");
			setOverrideEntitledPrice(part,"10");
		}
	}
	
	protected void setOverrideUnitPrice(String id,String value){
		setOverrideUnitPrice(id,"1",value);
	}
	
	protected void setOverrideUnitPrice(String id,String seqNumber ,String value){
		WebElement webElement =  driver.findElement(By.id("_" + id + "_"+ seqNumber +"_OVERRIDE_UNIT_PRC"));
		if(webElement != null)
			webElement.sendKeys(value);
	}
	
	protected void setOverrideEntitledPrice(String id,String value){
		setOverrideEntitledPrice(id,"1",value);
	}
	
	protected void setOverrideEntitledPrice(String id,String seqNumber,String value){
		WebElement webElement =  driver.findElement(By.id("_" + id + "_" + seqNumber + "_OVERRIDE_ENTITLED_PRC"));
		if(webElement != null)
			webElement.sendKeys(value);
	}
	
	protected void findReinstatementPart(SQOPartsAndPricingTabPage page){
		loggerContxt.info("find ReinstatementPart part");
		findPart(page,REINSTATEMENT_PART);
	}
	
	protected void findUpgradePart(SQOPartsAndPricingTabPage page){
		loggerContxt.info("find Upgrade part");
		findPart(page,UPGRADE_PART);
	}
	
	protected void findAdditionalSoftwarePart(SQOPartsAndPricingTabPage page){
		loggerContxt.info("find Additional Software part");
		findPart(page,ADDITIONAL_SOFTWARE_PART);
	}
	
	protected void findRenewalPart(SQOPartsAndPricingTabPage page){
		loggerContxt.info("find Renewal part");
		findPart(page,RENEWAL_PART);
	}
	
	protected void findServicePackPart(SQOPartsAndPricingTabPage page){
		loggerContxt.info("find Service Pack part");
		findPart(page,SERVICE_PACK_PART);
	}	
	
	protected void findUpgradeNReinstatementPart(SQOPartsAndPricingTabPage page){
		findUpgradePart(page);
		findReinstatementPart(page);
	}
	
	protected void findTransceiverPart(SQOPartsAndPricingTabPage page){
		loggerContxt.info("find Transceiver part");
		findPart(page,TRANSCEIVER_PART);
		for(String part : TRANSCEIVER_PART.split(",")){
			setOverrideUnitPrice(part,"10");
			setOverrideEntitledPrice(part,"10");
		}
	}

	protected void setMainPartSalesInfo(SQOSalesInfoTabPage salesInfoTabPage){
		loggerContxt.info("set MainPartSalesInfo");
		String title = salesInfoTabPage.getMsg(PART_MAIN_PART_MSG + TITLE);
		String desc = salesInfoTabPage.getMsg(PART_MAIN_PART_MSG + DESC);
		salesInfoTabPage.enterSalesInf(title,desc);
	}
	
	protected void setUpgradeNReinstatementPartInfo(SQOSalesInfoTabPage salesInfoTabPage){
		loggerContxt.info("set UpgradeNReinstatementPartInfo");
		String title = salesInfoTabPage.getMsg(UPGRADE_REINSTATEMENT_PART_MSG + TITLE);
		String desc = salesInfoTabPage.getMsg(UPGRADE_REINSTATEMENT_PART_MSG + DESC);
		salesInfoTabPage.enterSalesInf(title,desc);
	}
	
	protected void setReinstatementPartInfo(SQOSalesInfoTabPage salesInfoTabPage){
		loggerContxt.info("set ReinstatementPartSalesInfo");
		String title = salesInfoTabPage.getMsg(REINSTATEMENT_PART_MSG + TITLE);
		String desc = salesInfoTabPage.getMsg(REINSTATEMENT_PART_MSG + DESC);
		salesInfoTabPage.enterSalesInf(title,desc);
	}
	
	protected void setUpgradePartInfo(SQOSalesInfoTabPage salesInfoTabPage){
		loggerContxt.info("set UpgradePartInfo");
		String title = salesInfoTabPage.getMsg(UPGRADE_PART_MSG + TITLE);
		String desc = salesInfoTabPage.getMsg(UPGRADE_PART_MSG + DESC);
		salesInfoTabPage.enterSalesInf(title,desc);
	}
	
	protected void setAdditionalSoftwarePartInfo(SQOSalesInfoTabPage salesInfoTabPage){
		loggerContxt.info("set AdditionalSoftwarePartInfo");
		String title = salesInfoTabPage.getMsg(ADDITIONAL_SOFTWARE_PART_MSG + TITLE);
		String desc = salesInfoTabPage.getMsg(ADDITIONAL_SOFTWARE_PART_MSG + DESC);
		salesInfoTabPage.enterSalesInf(title,desc);
	}
	
	protected void setRenewalPartInfo(SQOSalesInfoTabPage salesInfoTabPage){
		loggerContxt.info("set RenewalPartInfo");
		String title = salesInfoTabPage.getMsg(RENEWAL_PART_MSG + TITLE);
		String desc =  salesInfoTabPage.getMsg(RENEWAL_PART_MSG + DESC);
		salesInfoTabPage.enterSalesInf(title,desc);
	}
	
	protected void setTransceiverPartInfo(SQOSalesInfoTabPage salesInfoTabPage){
		loggerContxt.info("set TransceiverPartInfo");
		String title = salesInfoTabPage.getMsg(TRANSCEIVER_PART_MSG + TITLE);
		String desc = salesInfoTabPage.getMsg(TRANSCEIVER_PART_MSG + DESC);
		salesInfoTabPage.enterSalesInf(title,desc);
	}
	

	public static void main(String[] args) throws Exception {
		SQOAppliancePartTest test = new SQOAppliancePartTest();
		test.setUp();
		test.run();
	}
}
