package com.ibm.dsw.automation.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.pageobject.sqo.SQOBrowseSoftwareAsServiceTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOCreateQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayCustListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayDistributorListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayResellerListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayStatusSearchReslutPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsSelectTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOLoginPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOOSearchCustPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQORetrieveSavedSalesQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectADistributorPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectAResellerPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuoteCPTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSpecialBidTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOStatusSearchTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitCurrDraftQuotePage;
import com.ibm.dsw.automation.vo.Quote;

/**
 * 
 * @author suchuang
 * @date Jan 15, 2013
 */
public class SQOCopyWebQuote extends BaseTestCase {

	/**
	 * 
	 */
	private Logger loggerContxt = Logger.getLogger(getClass().getName());
	
	/**
	 * 
	 */
	private SQOMyCurrentQuotePage currentQuotePage = null;
	
	/**
	 * 
	 */
	private SQOHomePage sqoHomePage = null;
	
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 15, 2013
	 * @throws Exception
	 */
	public void run() throws Exception {
		Map<String, String> quote = null;
		Map<String, String> quoteLineItem = null;
		List<Map<String, String>> lineitemDatas = null;
		
//		quote = getCpQuoteFromDb("0003319858", "uat");
//		quoteLineItem = getCpQuoteLineItemFromDb("0003319858", "uat");
//		lineitemDatas = getCpQuoteLineitemDatasFromDb("0003319858", "uat");
		
//		quote.setSold_to_cust_num("0007983048");
//		quote.setRsel_cust_num("0003037758");
		
		deleteAllCookies();
		
		runCreateQuote(quote);
		runQuoteEdit(quote, quoteLineItem, lineitemDatas);
		runSubmitQuote();
	}

	
	public void runCreateQuote(Map<String, String> quote) {
		loginSQO();
//		boolean hasCurrentQuote = selenium.isTextPresent("SQO reference");
//		loggerContxt.info("having current quote....." + hasCurrentQuote);
		SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();

		// Create a SQO quote
		if (("PA".equals(quote.get("PROG_CODE").trim()))
		 || ("PAE".equals(quote.get("PROG_CODE").trim()))) {
			quote.put("PROG_CODE", "PAUN");
		}
		currentQuotePage = cq.createQuote(quote);
		loggerContxt.info("create quote page finished.....");
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".currentQuoteTitle"));
		assertTextPresentTrue(getProperty(".currentQuoteTitle"));

	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 15, 2013
	 * @param partNums
	 * @return
	 */
	public SQOPartsAndPricingTabPage addMasterParts(String partNums) {
		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
		SQOFindPartsTabPage findPartsTabPage = partsAndPricingTab.findPartsLinkClick();
		
		SQOFindPartsSelectTabPage findPartsSelect = findPartsTabPage.findPartsLinkClick(partNums);
		loggerContxt.info("search first 100 piece of parts:....." + partNums);
		findPartsSelect.selectPartsClick(partNums);
		return findPartsSelect.rtn2DraftQuote();
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 15, 2013
	 * @param partsAndPricingTab
	 * @param lineitemDatas
	 */
	public void addSubParts(SQOPartsAndPricingTabPage partsAndPricingTab, List<Map<String, String>> lineitemDatas) {
		int seqnum = 1;
		for (Map<String, String> data : lineitemDatas) {
			String part_num = data.get("PART_NUM");
			String years = data.get("ADDTNL_MAINT_CVRAGE_QTY");
//			String seqnum = data.get("QUOTE_LINE_ITEM_SEQ_NUM");
			data.put("QUOTE_LINE_ITEM_SEQ_NUM", String.valueOf(seqnum++));
			
			if (Integer.parseInt(years) > 0) {
				partsAndPricingTab.additionalYearSelect(part_num, years, data.get("QUOTE_LINE_ITEM_SEQ_NUM"));
			}
		}
		
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 15, 2013
	 * @param partsAndPricingTab
	 * @param lineitemDatas
	 */
	public void setPartsDatas(SQOPartsAndPricingTabPage partsAndPricingTab, List<Map<String, String>> lineitemDatas) {
		
		if (null != lineitemDatas && lineitemDatas.size() > 0) {
			Map<String, String> data = lineitemDatas.get(0);

			String v_part_num = data.get("PART_NUM");
			String v_seqnum = data.get("QUOTE_LINE_ITEM_SEQ_NUM");
			String v_part_qty = data.get("PART_QTY");
			String v_start_date = data.get("START_DATE");
			String v_end_date = data.get("END_DATE");
			String v_line_disc_pct = data.get("LINE_DISC_PCT");
			String v_override_unit_price = data.get("OVERRIDE_UNIT_PRICE");
			String v_chnl_ovrrd_disc_pct = data.get("CHNL_OVRRD_DISC_PCT");
			String v_cmprss_cvrage_mths = data.get("CMPRSS_CVRAGE_MTHS");
			String v_cmprss_cvrage_disc_pct = data
					.get("CMPRSS_CVRAGE_DISC_PCT");
			
			partsAndPricingTab.changeQuantity(v_part_num, v_part_qty, v_seqnum);
			partsAndPricingTab.changeDisc_pct(v_part_num, v_line_disc_pct, v_seqnum);
			partsAndPricingTab.changeOrridPrice(v_part_num, v_override_unit_price, v_seqnum);
//			partsAndPricingTab.changeBpOverrideDis(v_part_num, v_chnl_ovrrd_disc_pct, v_seqnum);
			partsAndPricingTab.changePartStdDates(v_part_num, v_start_date, v_end_date, v_seqnum);
//			partsAndPricingTab.changeCmprssCvrageMths(v_part_num, v_cmprss_cvrage_mths, v_seqnum);
//			partsAndPricingTab.changeVCmprssCvrageDiscPct(v_part_num, v_cmprss_cvrage_disc_pct, v_seqnum);
		}
	}
	
	
    /**
     * DOC Add SAAS part to quote.
     */
    protected void addSaasPartToQuote(SQOPartsAndPricingTabPage partsAndPricingTab) {
        String[] pathaArray = getProperty(".saasNodePathArray").split("/");
        String[][] components = { { getProperty(".saasPartXPath"), getProperty(".saasPartQuantity"), } };
        // open saas part and configure it.
        browseAndAddSaasPart(partsAndPricingTab, pathaArray, components);
    }

    private void browseAndAddSaasPart(SQOPartsAndPricingTabPage partsAndPricingTab, String[] treeNodeIds, String[][] components) {
    	partsAndPricingTab.browseSoftwareAsAServiceLinkClick();
		String text = getProperty("ECustomerCare");
		if (isTextPresent(text)) {
			loggerContxt.info(text);
		}
        SQOBrowseSoftwareAsServiceTabPage browseSoftwareAsServiceTabPage = new SQOBrowseSoftwareAsServiceTabPage(getDriver());
        browseSoftwareAsServiceTabPage.waitForElementLoading(new Long(25000));
        loggerContxt.info("browse SAAS page finished.....");
        
        // check if there are some Saas offerings
        // There are no Software as a Service offerings to configure.
        if (isTextPresent(getProperty(".noSaasOfferingsMsg"))) {
            return;
        }

        browseSoftwareAsServiceTabPage.clickTreeNodesById(treeNodeIds);
        configureService(browseSoftwareAsServiceTabPage, getProperty(".SaasPartId"), components);
    }

    public void configureService(SQOBrowseSoftwareAsServiceTabPage page, String configureId, String[][] components) {
        ((JavascriptExecutor) driver).executeScript("configureService('" + configureId + "')");
        WebElement webElement = page.waitForElementById("dijit_DialogUnderlay_0");
        if (webElement != null) {
            // driver.findElement(By.xpath("/html/body/div[6]/div[2]/div/div[2]/div[2]/span/span/span/span[3]")).click();//
            // gts confirm
            page.addSaasPart(components);
            loggerContxt.info("add SAAS part finished.....");
            page.waitForElementLoading(25000L);
        }
    }
    
	/**
	 * 
	 * @author suchuang
	 * @date Jan 15, 2013
	 * @param partsAndPricingTab
	 * @param quote
	 * @return
	 */
	public SQOSalesInfoTabPage setSalesInformation(SQOPartsAndPricingTabPage partsAndPricingTab, Map<String, String> quote) {
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SQOSalesInfoTabPage salesTab = partsAndPricingTab.gotoSalesInfoTab();
		loggerContxt.info("Go to SalesInf Tab end.....");
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".opportunityinfo"));
		assertTextPresentTrue(getProperty(".opportunityinfo"));
		salesTab.enterSalesInf(quote.get("QUOTE_TITLE"), getProperty(".quoteDesc"), getProperty(".busOrgCode"));
		salesTab.addQuoteEditor(getProperty(".quoteeditormail"));
		// press "Save" button, then press "Save" button again
		salesTab.saveDraftQuoteLink();

		SQORetrieveSavedSalesQuotePage savedSalesQuote = currentQuotePage.goSQORetrieveSavedSalesQuoteTab();
		loggerContxt.info("verify current page whether having this content ......" + getProperty(".retrieveSavedSalesQuoteTitle"));
		assertTextPresentTrue(getProperty(".retrieveSavedSalesQuoteTitle"));
		currentQuotePage = savedSalesQuote.goViewDetailSavedQuote();
		
		return salesTab;
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 15, 2013
	 * @param salesTab
	 */
	public void setSpecialBidInfo(SQOSalesInfoTabPage salesTab) {
		loggerContxt.info("go to Special Bid Tab.....");
		SQOSpecialBidTabPage spcialBidTab = salesTab.goToSpecialBidTabClick();
		spcialBidTab.enterSpcialBidInf(getProperty(".justificationSummary"));
		currentQuotePage = spcialBidTab.rtnToCPTab();
	}
	
	public void runQuoteEdit(Map<String, String> quote, Map<String, String> quoteLineItem, List<Map<String, String>> lineitemDatas) {

		// to create customer
		createCustomer(quote);
		
		// to create reseller
		createReseller(quote);
		
		// to create distributor
		createDistributor(quote);

		// to add all the master parts
		SQOPartsAndPricingTabPage partsAndPricingTab = addMasterParts(quoteLineItem.get("PART_NUM"));
		
		// to add SAAS part 
		addSaasPartToQuote(partsAndPricingTab);
		
		/*
		if mybrowser.link(:text, "Make PVU calculator required").exists?
			click_btn_by_alt("Make PVU calculator required")
		end
		 */
		
		// to add all the sub parts
		addSubParts(partsAndPricingTab, lineitemDatas);
		
		// to set all parts datas
		setPartsDatas(partsAndPricingTab, lineitemDatas);
		
		
		// set sales information
		SQOSalesInfoTabPage salesTab = setSalesInformation(partsAndPricingTab, quote);
		
		// set special bid information
		setSpecialBidInfo(salesTab);
		
		// Select the last date of the current month as the
		// "Quote expiration date"
		currentQuotePage.selectExpirationDate(30);
		
		//////////////////////////////////////////above code is ok
		
//		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
//		SQOFindPartsTabPage findPartsTabPage = partsAndPricingTab.findPartsLinkClick();
//
//		Connection conn = null;
//		String strPartList1 = "";
//		String strPartList2 = "";
//		/*
//		 * try {
//		 * 
//		 * conn = this.getConnection(); Part part = new Part(conn); List
//		 * partList = part.getPAPAEParts(null);
//		 * 
//		 * if (partList != null) { int partLenth = partList.size(); for (int i =
//		 * 0; i < partLenth; i++) { if (i < 100) { strPartList1 +=
//		 * partList.get(i).toString().trim() + ","; } else { strPartList2 +=
//		 * partList.get(i).toString().trim() + ","; } } } } catch
//		 * (ClassNotFoundException e) { e.printStackTrace(); } catch
//		 * (SQLException e) { e.printStackTrace(); } finally { if (conn != null)
//		 * { try { conn.close(); } catch (SQLException e) { e.printStackTrace();
//		 * } } }
//		 */
//
//		strPartList1 = getProperty(".partList1");
//		strPartList2 = getProperty(".partList2");
//
//		SQOFindPartsSelectTabPage findPartsSelect = findPartsTabPage.findPartsLinkClick(strPartList1);
//
//		loggerContxt.info("search first 100 piece of parts:....." + strPartList1);
//		findPartsTabPage = findPartsSelect.selectPartsAndChgCriteriaClick();
//		findPartsSelect = findPartsTabPage.findPartsLinkClick(strPartList2);
//		loggerContxt.info("search the other parts:....." + strPartList2);
//		findPartsSelect.selectPartsClick();
//		partsAndPricingTab = findPartsSelect.rtn2DraftQuote();
//
//		// Click on 'Browse parts' tab
//		SQOBrowsePartsTabPage browsePartsTab = partsAndPricingTab.browsePartsLinkClick();
//		loggerContxt.info("Click on 'Browse parts' tab.....");
//		loggerContxt.info("verify current page whether having this content ......" + getProperty(".partSelect"));
//		assertTextPresentTrue(getProperty(".partSelect"));
//
//		/*
//		 * Click on Brand Lotus Click on Lotus 123 Click on License + SW
//		 * Subscription & Support Check the checkbox of the first part listed
//		 * Click the "Add selected parts to draft quote" link
//		 */
//		browsePartsTab.browseLotusPartsTab();
//		browsePartsTab.addSelectedPartsToDraftQuoteLinkClick();
//		loggerContxt.info("verify current page whether having this content ......" + getProperty(".addpartsokmsg"));
//		assertTextPresentTrue(getProperty(".addpartsokmsg"));
//		partsAndPricingTab = browsePartsTab.returnToDraftQuoteLinkLinkClick();
//		loggerContxt.info("Click on Brand Lotus Click on Lotus 123 Click on License + SW Subscription & Support " + "Check the checkbox of the first part listed Click the 'Add selected parts to draft quote' link.....");
//
//		// Under Parts and pricing section, click on the "Recalculate quote"
//		// link
//		partsAndPricingTab.recalculateQuotePPTab();
//
//		// Go to SalesInf Tab
//		loggerContxt.info("Go to SalesInf Tab begin.....");
//		SQOSalesInfoTabPage salesTab = partsAndPricingTab.gotoSalesInfoTab();
//		loggerContxt.info("Go to SalesInf Tab end.....");
//		loggerContxt.info("verify current page whether having this content ......" + getProperty(".opportunityinfo"));
//		assertTextPresentTrue(getProperty(".opportunityinfo"));
//
//		salesTab.enterSalesInf(getProperty(".briefTitle"), getProperty(".quoteDesc"), getProperty(".busOrgCode"));
//
//		salesTab.addQuoteEditor(getProperty(".quoteeditormail"));
//		// press "Save" button, then press "Save" button again
//		salesTab.saveDraftQuoteLink();
//
//		SQORetrieveSavedSalesQuotePage savedSalesQuote = currentQuotePage.goSQORetrieveSavedSalesQuoteTab();
//		loggerContxt.info("verify current page whether having this content ......" + getProperty(".retrieveSavedSalesQuoteTitle"));
//		assertTextPresentTrue(getProperty(".retrieveSavedSalesQuoteTitle"));
//		currentQuotePage = savedSalesQuote.goViewDetailSavedQuote();
//
//		// set special bid information
//		setSpecialBidInfo(salesTab);
//		
//		// Select the last date of the current month as the
//		// "Quote expiration date"
//		currentQuotePage.selectExpirationDate(30);
	}


	/**
	 * 
	 * @author suchuang
	 * @date Jan 15, 2013
	 */
	public void runSubmitQuote() {

		loggerContxt.info("submit current quote page.....");
		SQOSubmitCurrDraftQuotePage currDraftQuotePage = currentQuotePage.submitCurrentDraftQuote();

		/*
		 * 
		 * Check the "E-mail this quote to the following e-mail address"
		 * checkbox, enter "chris_errichetti@us.ibm.com" in the field next to
		 * the checkbox item, turn on the No radio button for the question
		 * "Would you like to make this available to the customer on Passport Advantage Online?"
		 * , enter "XPRS Testing" in the
		 * "Enter customized text for the quote's cover email" text box and On
		 * the quote submission page, make a select from
		 * "* Would you like to make this available to the customer on Passport Advantage Online? "
		 * if applicable. Press the "Submit" button
		 */
		loggerContxt.info("submit the quote.....");
		currDraftQuotePage.submitDraftQuote(getProperty(".quoteeditormail"), getProperty(".quoteDesc"));

		loggerContxt.info("verify current page whether having this content ......" + getProperty(".quotefinalmsg"));
		assertTextPresentTrue(getProperty(".quotefinalmsg"));
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 15, 2013
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SQOCopyWebQuote test = new SQOCopyWebQuote();
		test.setUp();
		test.run();
	}
	
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
	 * @date Jan 15, 2013
	 * @param quoteNum
	 * @param env
	 * @return
	 */
	private Map<String, String> getCpQuoteLineItemFromDb(String quoteNum, String env) {
		Quote quote = new Quote(env);
		return quote.getQuoteLineItemByQuoteNum(quoteNum);
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 15, 2013
	 * @param quoteNum
	 * @param env
	 * @return
	 */
	private List<Map<String, String>> getCpQuoteLineitemDatasFromDb(String quoteNum, String env) {
		Quote quote = new Quote(env);
		List<Map<String, String>> datas = quote.getQuoteCurrentLineitemDatasByQuoteNum(quoteNum);
		return datas;
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 15, 2013
	 */
//	private void loginSQO() {
//		SQOLoginPage lp = new SQOLoginPage(this.driver);
//		lp.loginAs(getLoginUser(), "");
//		sqoHomePage = new SQOHomePage(driver);
//	}

	/**
	 * 
	 * @author suchuang
	 * @date Jan 15, 2013
	 * @param quote
	 */
	private void createCustomer(Map<String, String> quote) {
		// Find an existing customer
		SQOOSearchCustPage searchCustPage = currentQuotePage.findCustbyClick();
		loggerContxt.info("go to search customer page.....");

		// display customer
		SQODisplayCustListPage displayCustListPage = searchCustPage.displaySQOCustomerListBySiteNum(quote.get("SOLD_TO_CUST_NUM"));
		loggerContxt.info("display customer page.....");

		// select customer and back current quote page
		currentQuotePage = displayCustListPage.selectCustomer();
		loggerContxt.info("select customer and back current quote page.....");
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 15, 2013
	 * @param quote
	 */
	private void createReseller(Map<String, String> quote) {
		if (StringUtils.isBlank(quote.get("RSEL_CUST_NUM"))) {
			return;
		}
		
		// Find reseller
		SQOSelectAResellerPage searchResellerPage = currentQuotePage.findResellertbyClick();
		loggerContxt.info("go to search reseller page.....");

		// display reseller
		SQODisplayResellerListPage displayResellerListPage = searchResellerPage.displayResellerByNum(quote.get("RSEL_CUST_NUM"));
		loggerContxt.info("display reseller page.....");

		// select reseller and back current quote page
		currentQuotePage = displayResellerListPage.selectReseller();
		loggerContxt.info("select reseller and back current quote page.....");
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 15, 2013
	 * @param quote
	 */
	private void createDistributor(Map<String, String> quote) {
		if (StringUtils.isBlank(quote.get("PAYER_CUST_NUM"))) {
			return;
		}
		
		// Find distributor
		SQOSelectADistributorPage searchDistributorPage = currentQuotePage.findDistributorbyClick();
		loggerContxt.info("go to search distributor page.....");

		// display distributor
		SQODisplayDistributorListPage displayDistributorListPage = searchDistributorPage.displayDistributorListBySiteNum(quote.get("PAYER_CUST_NUM"));
		loggerContxt.info("display distributor page.....");

		// select distributor and back current quote page
		currentQuotePage = displayDistributorListPage.selectDistributor();
		loggerContxt.info("select distributor and back current quote page.....");
		
		currentQuotePage.setFulfillmentSourcesToDirect();
	}
	
	
	private void copyViaWeb(String webQuoteNum) {
		SQOLoginPage page = new SQOLoginPage(this.driver);
		SQOHomePage homePage = page.loginAs(getLoginUser(), getLoginPasswd());
		SQOStatusSearchTabPage statusSearch = homePage.gotoStatus();
		SQODisplayStatusSearchReslutPage statusSearchResult = statusSearch.findQuoteByNum(webQuoteNum);
		SQOSelectedQuoteCPTabPage quoteCPTabPage = statusSearchResult.viewQuoteDetail();

	}
	
}
