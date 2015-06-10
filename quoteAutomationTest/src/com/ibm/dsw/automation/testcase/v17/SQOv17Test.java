package com.ibm.dsw.automation.testcase.v17;

import java.util.Properties;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.pageobject.sqo.SQOBrowsePartsTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOBrowseSoftwareAsServiceTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsSelectTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;

public class SQOv17Test extends BaseTestCase {
	
	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOv17Test.class.getName());
	public final static String PROPERTIES = "/com/ibm/dsw/automation/testcase/SQOv17Test.properties";
	
	public final static String URL = "https://w3-117fvt.etl.ibm.com/software/sales/passportadvantage/dswpricebook/PbCfgSales/quote_sales_jump.html";
	
	public final static String PART_FCT_LIST = "E039NLL,E03AXLL,E039LLL,E0394LL";
	public final static String PART_OEM_LIST = "D0NYTLL,D0NYALL,D0NYULL";
	public final static String PART_PPSS_LIST = "D59Z9LL,D59ZFLL,D59ZGLL";//Pre-Packaged Software Services
	public final static String PART_FMP_LIST = "D0NYTLL,D0NYALL,D0NYULL"; //FCT to PA migration quote
	public final static String PART_PAUN_LIST = "D0NYTLL,D0NYALL,D0NYULL";//Passport Advantage/Passport Advantage Express
	
	
	
	
	//String[] clickNodes,String[] checkNodes
	public final static String[] PART_FCT_CLICK_NODES = {"img0_0","img0_0","img0_0"};
	public final static String[] PART_FCT_CHECK_NODES = {"0_0_7","0_0_5"};
	public final static String[] PART_OEM_CLICK_NODES = {"img0_0","img0_1","img0_2","img0_0_0"};
	public final static String[] PART_OEM_CHECK_NODES = {"0_0_0_0","0_0_0_1"};
	public final static String[] PART_PPSS_CLICK_NODES = {"img0_0","img0_1","img0_2"};
	public final static String[] PART_PPSS_CHECK_NODES = {"0_0_0","0_0_1"};
	public final static String[] PART_FMP_CLICK_NODES = {"img0_0","img0_1","img0_2","img0_0_0"};
	public final static String[] PART_FMP_CHECK_NODES = {"0_0_0_0","0_0_0_1"};
	public final static String[] PART_PAUN_CLICK_NODES = {"img0_0","img0_1","img0_2","img0_0_0"};
	public final static String[] PART_PAUN_CHECK_NODES = {"0_0_0_0","0_0_0_1"};
	
	public final static String[] SAAS_PART_PAUN_CLICK_NODES = {"img0","img0_0","img0_0_0"};
	public final static String SAAS_PART_PAUN_CONFIGURE_ID = "5725F87";
	public final static String[][] SAAS_PART_PAUN_COMPONENTS = {
		{"/html/body/div[6]/div/form/table/tbody/tr[8]/td/table/tbody/tr[2]/td[2]/input","2"},
		{"/html/body/div[6]/div/form/table/tbody/tr[9]/td/table/tbody/tr[2]/td[2]/input","3"},
		
	};
	private static Properties prop;
	
	
	@Override
	@BeforeSuite
	public void initTestData() {
		clazzName = "SQOv17Test";
		if (prop == null) 
			prop = this.getTestDataProp(PROPERTIES);
	}
	
	@Parameters({ "env", "htmlSourceFolder" })
	@Test(description = "Test V17")
	public void run() {
		try{
			SQOPartsAndPricingTabPage page = new SQOPartsAndPricingTabPage(this.driver);
			
			findPartTestCase(page);
			browserPartTestCase(page);
			//browseSaasPartTestCase(page);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			quitWebdriver();
		}

	}
	private void findPartTestCase(SQOPartsAndPricingTabPage page){
		/*
		 * Find by Part test case
		 */
		loggerContxt.info("test find part FCT FNET USA");
		page = page.load(getUser(), getCntryCode(), getTsAccessLevel(), "FCT", "FNET", "USA");
		findPart(page,PART_FCT_LIST);
		reset();
		
		loggerContxt.info("test find part OEM USA");
		page.load(getUser(), getCntryCode(), getTsAccessLevel(), "OEM", "", "USA");
		findPart(page,PART_OEM_LIST);
		reset();
		
		loggerContxt.info("test find part PPSS USA");
		page.load(getUser(), getCntryCode(), getTsAccessLevel(), "PPSS", "", "USA");
		findPart(page,PART_PPSS_LIST);
		reset();
		
		loggerContxt.info("test find part FMP FNET USA");
		page.load(getUser(), getCntryCode(), getTsAccessLevel(), "FMP", "FNET", "USA");
		findPart(page,PART_FMP_LIST);
		reset();
		
		loggerContxt.info("test find part PAUN USA");
		page.load(getUser(), getCntryCode(), getTsAccessLevel(), "PAUN", "", "USA");
		findPart(page,PART_PAUN_LIST);
		reset();
	}
	
	private void browserPartTestCase(SQOPartsAndPricingTabPage page){
		/*
		 * Browse part test case
		 */
		loggerContxt.info("test browserPart FCT FNET USA");
		page = page.load(getUser(), getCntryCode(), getTsAccessLevel(), "FCT", "FNET", "USA");
		browserPart(page,PART_FCT_CLICK_NODES,PART_FCT_CHECK_NODES);
		reset();
		
		loggerContxt.info("test browserPart OEM USA");
		page.load(getUser(), getCntryCode(), getTsAccessLevel(), "OEM", "", "USA");
		browserPart(page,PART_OEM_CLICK_NODES,PART_OEM_CHECK_NODES);
		reset();
		
		loggerContxt.info("test browserPart PPSS USA");
		page.load(getUser(), getCntryCode(), getTsAccessLevel(), "PPSS", "", "USA");
		browserPart(page,PART_PPSS_CLICK_NODES,PART_PPSS_CHECK_NODES);
		reset();
		
		loggerContxt.info("test browserPart FMP FNET USA");
		page.load(getUser(), getCntryCode(), getTsAccessLevel(), "FMP", "FNET", "USA");
		browserPart(page,PART_FMP_CLICK_NODES,PART_FMP_CHECK_NODES);
		reset();
		
		loggerContxt.info("test browserPart PAUN USA");
		page.load(getUser(), getCntryCode(), getTsAccessLevel(), "PAUN", "", "USA");
		browserPart(page,PART_PAUN_CLICK_NODES,PART_PAUN_CHECK_NODES);
		reset();
	}
	
	private void browseSaasPartTestCase(SQOPartsAndPricingTabPage page){
		/*
		 * SAAS part test case
		 */
		loggerContxt.info("test browseSaasPart PAUN USA");
		page.load(getUser(), getCntryCode(), getTsAccessLevel(), "PAUN", "", "USA");
		browseSaasPart(page,SAAS_PART_PAUN_CLICK_NODES,SAAS_PART_PAUN_COMPONENTS);
		reset();
	}
	
	
	private void findPart(SQOPartsAndPricingTabPage page,String partList){
		
		SQOFindPartsTabPage sqoFindPartsTabPage = page.findPartsLinkClick();
		SQOFindPartsSelectTabPage findPartsSelectTabPage =  sqoFindPartsTabPage.findPartsLinkClick(partList);
		findPartsSelectTabPage.selectPartsClick(partList);
		findPartsSelectTabPage.rtn2DraftQuote(); 
	}
	

	private void browserPart(SQOPartsAndPricingTabPage page,String[] clickNodes,String[] checkNodes){
		SQOBrowsePartsTabPage browsePartsTabPage = page.browsePartsLinkClick();
		
		for (String clickNode : clickNodes) 
			browsePartsTabPage.elementClickById(clickNode);
		
		for(String checkNode : checkNodes)
			browsePartsTabPage.elementClickById(checkNode);
		
		browsePartsTabPage.addSelectedPartsToDraftQuoteLinkClick();
		browsePartsTabPage.returnToDraftQuoteLinkClick();
	}

	
	private void browseSaasPart(SQOPartsAndPricingTabPage partsAndPricingTab,String[] treeNodeIds,String[][] components ){
    	partsAndPricingTab.browseSoftwareAsAServiceLinkClick();
		String text = getProperty("ECustomerCare");
		if (isTextPresent(text)) {
			loggerContxt.info(text);
		}
        SQOBrowseSoftwareAsServiceTabPage browseSoftwareAsServiceTabPage = new SQOBrowseSoftwareAsServiceTabPage(getDriver());
        browseSoftwareAsServiceTabPage.waitForElementLoading(new Long(25000));
		browseSoftwareAsServiceTabPage.clickTreeNodesById(treeNodeIds);
		browseSoftwareAsServiceTabPage.configureService(SAAS_PART_PAUN_CONFIGURE_ID,components); 
		browseSoftwareAsServiceTabPage.returnToDraftQuoteLinkClick();		
	}
	
	
	public String getCntryCode(){
		return getProperty(prop, ".cntryCode").toString();
	}
	
	public String getUser(){
		return getProperty(prop, ".username").toString();	
	}
	
	public String getTsAccessLevel(){
		return getProperty(prop, ".tsAccessLevel").toString();
	}
	
	
	public static void main(String[] args) throws Exception {
		SQOv17Test test = new SQOv17Test();
		test.setUp();
		test.run();
	}
	

}
