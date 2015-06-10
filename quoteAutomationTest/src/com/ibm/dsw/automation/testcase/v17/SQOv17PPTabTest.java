package com.ibm.dsw.automation.testcase.v17;

import static org.testng.AssertJUnit.assertTrue;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOBrowseSoftwareAsServiceTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOCreateQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsSelectTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuoteCPTabPage;

/**
 * Script Name : SQOv17PPTabTest<br>
 * Description : writing script for v17 migration<br>
 * Objective: Prerequisites:<br>
 * 
 * Test Case Test for PP tab Note:<br>
 * please save all the html source code for theRPT scan.<br>
 * for lob in (PA/PAE/OEM/FCT/PPSS ) for quote type in (sales ,renew)<br>
 * make sure the empty page of PP tab display in v17 style make sure find part by id and description display in v17
 * style.<br>
 * make sure find part results page display in v17 style make sure SQO basic configurator display in v17 style<br>
 * make sure PP tab with software parts and saas parts display in v17 style<br>
 * make sure part and price detail pages display in v17 style.<br>
 * Additional Info: check in firefox<br>
 * Original Host: <br>
 * Windows
 * 
 * @since 2012-12-6
 * @author zhou jun
 */
@SuppressWarnings("deprecation")
public class SQOv17PPTabTest extends BaseTestCase {
	
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOv17PPTabTest.class.getName());

    /**
     * 
     */
    private static final String CURRENT_QUOTE_TITLE = "currentQuoteTitle";

    protected final static String[] SAAS_PART_PAUN_CLICK_NODES = { "img1", "img1_0", "img1_0_0" };

    protected final static String[][] SAAS_PART_PAUN_COMPONENTS = { {
    "/html/body/div/div[2]/div/div/div[2]/form/table/tbody/tr[7]/td/div/div/div/table/tbody/tr/td/input", "1" }, };

    private SQOMyCurrentQuotePage currentQuotePage = null;

    private SQOHomePage sqoHomePage = null;

    protected SQOPartsAndPricingTabPage sqoPartsAndPricingTabPage;

    /**
     * case names: exclude default case FVT
     */
    private final String[] caseNames = new String[] { "OEM", "PAPAE", "PPSS" };

    private String currentCase = "";


    /**
     * 
     * DOC get property value for different case, use currentCase as an parameter.<br>
     * For example, if we are trying to get value of property <b>'fvt.lobType'</b> <br>
     * 
     * while currentCase is ""(default FVT), we will get property value of KEY <b>fvt.lobType</b>: <br>
     * section in properties file is: <b>fvt.lobType = Flexible Contract Type</b><br>
     * while currentCase OEM, we will get property value of KEY <b>fvt.lobType_OEM</b><br>
     * section in properties file is: <b>fvt.lobType_OEM = OEM/ASL</b><br>
     * 
     * @param key
     * @return
     */
    protected String getPropValue(String key) {
        if ("".equals(currentCase)) {
        	
            return getProperty( "." + key);
        } else {
            String value = getProperty( "." + key + "_" + currentCase);
            if (value == null) {
                return getProperty( "." + key);
            }
            return value;
        }
    }

    /**
     * login in SQO
     * 
     * @param
     * 
     * */
    protected void loginSqo() {

        loggerContxt.info("env....." + env.hashCode());

		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");

        assertPresentTextWithLog("softQtOrd");

    }

//    @Test(description = "Create SQO quote for PA/PAE/OEM/FCT/PPSS")
    public void run() {
        loginSqo();
        runCreateQuoteByLob(getPropValue("lobType"), getPropValue("acquisition_LOTUS"), getPropValue("country_US"));

        for (int i = 0; i < caseNames.length; i++) {
            /**
             * set currentCase to be caseNames[i]
             */
            currentCase = caseNames[i];
            /**
             * while call runCreateQuoteByLob again, the currentCase value will be used in the parameters, then the case
             * will be switched to currentCase
             */
            runCreateQuoteByLob(getPropValue("lobType"), getPropValue("acquisition_LOTUS"), getPropValue("country_US"));
        }
        
        loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
    }

    public void createQuoteByLob(String lob) {
    	quitAndReset();
    	
		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf(), LOGON_IN_SQO_VIA_SQO_JUMP_PAGE);
		quoteFlow.getPropBean().setLob(lob);
		
		loggerContxt.info("Login SQO finished.....");

		SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
		loggerContxt.info("Create new sales quote,Draft quote header display, " +
				"Draft quote common action buttons display finished.....");

		// 2.add customer
		currentQuotePage = quoteFlow.processCustPartnerTab(currentQuotePage);
		loggerContxt
				.info("add customer finished.....");

		// 3.add part
		SQOPartsAndPricingTabPage ppTab = null;
		if (!"OEM".equals(quoteFlow.getPropBean().getLob())) {
			ppTab = quoteFlow.addSaasPartToQuote(currentQuotePage);
			loggerContxt.info("add Saas parts finished.....");
		} else {
			ppTab = currentQuotePage.goToPartsAndPricingTab();
		}
		
		ppTab = quoteFlow.browsePart(ppTab);
		
		//5. Edit the sales inf tab and special bid necesary,submit the quote
		SQOSalesInfoTabPage salesTab = quoteFlow.editSalesInfoTab(ppTab);
		currentQuotePage = quoteFlow.processSpecialBidTab(salesTab);
		
		if (currentQuotePage.hasSubmitBtn()) {
			quoteFlow.runSubmitQuote(currentQuotePage);
			loggerContxt.info("Edit the sales inf tab and special bid necesary,submit the quote finished.....");

			//6.search the submitted quote and check the part and pricing tab again,the mentioned point could display right
			SQOSelectedQuoteCPTabPage sbmdCPTabl = quoteFlow.findQuoteByNum(currentQuotePage);
			
			//TODO
			quoteFlow.checkSubmittedQuote(sbmdCPTabl);
			loggerContxt.info("search the submitted quote and check the part and pricing tab again,the mentioned point could display right finished.....");
			loggerContxt.info("have a check a the CP/SALES/PP/APPROVAL Tab finished.....");
		}

		loggerContxt.info("@Test test104new has passed!");
    }
    
    @Test(description = "Create SQO quote for PA/PAE/OEM/FCT/PPSS")
    public void assemblyBizFlow() {
    	createQuoteByLob("PAUN");
    	createQuoteByLob("FCT"); // have no saas part
    	createQuoteByLob("OEM");
    }
    
    /**
     * create Sales Quote
     * 
     * @param lob
     * @param acquisition
     * @param lobcountry
     * 
     * */
    protected void runCreateQuoteByLob(String lob, String acquisition, String country) {

        loggerContxt.info("go to sqo home page finished.....");
        //assertPresentLinkWithLog("createSalesQuoteTitle");

        SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();

      //  assertPresentTextWithLog("createSalesQuoteTitle");

        /**
         * Create a SQO quote: fill the form with values of lob, acquisition and country
         */
        currentQuotePage = cq.createQuote(lob, acquisition, country);
        loggerContxt.info("create quote page finished.....");
        assertPresentTextWithLog("createEditSalesQuoteSubTitle");

        goToPartsAndPricingTab();
        addSaasPartToQuote();

        assertPresentTextWithLog(CURRENT_QUOTE_TITLE);

    }

    /**
     * DOC assertPresentText.<br>
     * before assertPresentText, use loggerContxt to log info: verefy current page whether having this content ......<br>
     * if 'presentTextKey' presented in page, the application will go on, otherwise will quit
     * 
     * @param presentTextKey
     */
    protected void assertPresentTextWithLog(String presentTextKey) {
        String value = getPropValue(presentTextKey);
        loggerContxt.info("verefy current page whether having this text ......" + value);
        assertPresentText(value);
    }

    /**
     * 
     * DOC "assertNotPresentTextWithLog".<br>
     * before assertNotPresentText, use loggerContxt to log info: verefy current page whether NOT having this text
     * ......<br>
     * if 'notPresentTextKey' presented in page, the application will quit, otherwise will go on<br>
     * 
     * @param notPresentTextKey
     */
    protected void assertNotPresentTextWithLog(String notPresentTextKey) {
        String value = getPropValue(notPresentTextKey);
        loggerContxt.info("verefy current page whether NOT having this text ......" + value);
        assertNotPresentText(value);
    }

    /**
     * assertTrue(if the link "presentLinkKey" presented in the page)
     * 
     * @param presentLinkKey
     */
    protected void assertPresentLinkWithLog(String presentLinkKey) {
        String linkText = getPropValue(presentLinkKey);
        // check if the browerSaasLink is presented
        boolean linkExist = getSelenium().isElementPresent("link=" + linkText);
        loggerContxt.info("verefy current page whether having this link ......" + linkText);
        assertTrue(linkExist);
    }

    /**
     * DOC go to 'Parts and Pricing' tab.<br>
     * click 'Find Parts'<br>
     */
    protected void goToPartsAndPricingTab() {
        SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
        assertPresentTextWithLog("parts_pricing_des");

        SQOFindPartsTabPage findPartsTabPage = partsAndPricingTab.findPartsLinkClick();
        loggerContxt.info("find parts page finished.....");
        assertPresentTextWithLog("findByPartNumber");

        // find parts by ID
        SQOFindPartsSelectTabPage findPartsSelect = findPartsTabPage.findPartsLinkClick(getPropValue("partsIDs"));
        assertNotPresentTextWithLog("noPartsFound");

        sqoPartsAndPricingTabPage = addFoundParts2Quote(findPartsSelect);
        loggerContxt.info("add found parts to quote finished.....");
        assertPresentTextWithLog(CURRENT_QUOTE_TITLE);

        // find parts by Des
        boolean findByDesFunctionIsOK = Boolean.valueOf(getPropValue("findPartsByDesSwitch"));
        if (findByDesFunctionIsOK) {
            findPartsByDes(findPartsTabPage);
        }

    }

    /**
     * DOC Comment method "findPartsByDes".
     * 
     * @param findPartsTabPage
     */
    private void findPartsByDes(SQOFindPartsTabPage findPartsTabPage) {
        SQOFindPartsSelectTabPage findPartsSelect;
        loggerContxt.info("go to Part Select page again, for find by des.....");
        sqoPartsAndPricingTabPage.findPartsLinkClick();

        assertPresentTextWithLog("findByPartDes");
        findPartsSelect = findPartsTabPage.findPartsByPartDes(getPropValue("partsDes"));
        assertNotPresentTextWithLog("noPartsFound");

        addFoundParts2Quote(findPartsSelect);

        loggerContxt.info("add found parts to quote finished.....");
        assertPresentTextWithLog(CURRENT_QUOTE_TITLE);
    }

    /**
     * DOC Comment method "addFoundParts2Quote".
     * 
     * @param findPartsSelect
     * @return
     */
    protected SQOPartsAndPricingTabPage addFoundParts2Quote(SQOFindPartsSelectTabPage findPartsSelect) {
        findPartsSelect.waitForElementLoading(new Long(25000));

        findPartsSelect.selectAllDispPartsLink.click();
        findPartsSelect.addSelectedParts2QuoteLink.click();

        assertPresentTextWithLog("partaddokmsg");

        SQOPartsAndPricingTabPage partsAndPricingTab = findPartsSelect.rtn2DraftQuote();
        return partsAndPricingTab;
    }

    /**
     * DOC Add SAAS part to quote.
     */
    protected void addSaasPartToQuote() {
        /**
         * get the text of link: Browse Software as a Service
         */
        String browerSaasLinkText = getPropValue("browerSaas");
        loggerContxt.info("verefy current page whether having this link: " + browerSaasLinkText);

        // check if the browerSaasLink is presented
        boolean browerSaasLinkExist = getSelenium().isElementPresent("link=" + browerSaasLinkText);
        if (browerSaasLinkExist) {
            loggerContxt.info("link: " + browerSaasLinkText + " exist in this page");
            String[] pathaArray = getPropValue("saasNodePathArray").split("/");
            String[][] components = { { getPropValue("saasPartXPath"), getPropValue("saasPartQuantity"), } };
            // open saas part and configure it.
            browseAndAddSaasPart(sqoPartsAndPricingTabPage, pathaArray, components);
        } else {
            loggerContxt.info("link: " + browerSaasLinkText + " does NOT exist in this page");
        }
    }

    private void browseAndAddSaasPart(SQOPartsAndPricingTabPage partsAndPricingTab, String[] treeNodeIds, String[][] components) {
    	partsAndPricingTab.browseSoftwareAsAServiceLinkClick();
		String text = getProperty("ECustomerCare");
		if (isTextPresent(text)) {
			loggerContxt.info(getClass().getSimpleName() + " - " + text);
		}
        SQOBrowseSoftwareAsServiceTabPage browseSoftwareAsServiceTabPage = new SQOBrowseSoftwareAsServiceTabPage(getDriver());
        browseSoftwareAsServiceTabPage.waitForElementLoading(new Long(25000));

        loggerContxt.info("browse SAAS page finished.....");
        // check if there are some Saas offerings
        // There are no Software as a Service offerings to configure.
        if (selenium.isTextPresent(getPropValue("noSaasOfferingsMsg"))) {
            loggerContxt.info(getPropValue("noSaasOfferingsMsg"));
            return;
        }

        browseSoftwareAsServiceTabPage.clickTreeNodesById(treeNodeIds);
        configureService(browseSoftwareAsServiceTabPage, getPropValue("SaasPartId"), components);

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

    public static void main(String[] args) throws Exception {
        SQOv17PPTabTest test = new SQOv17PPTabTest();
        test.setUp();
        test.assemblyBizFlow();
        // test.runQuoteEdit();
        // test.runSubmitQuote();
    }

}
