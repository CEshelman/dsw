package com.ibm.dsw.automation.testcase.saas;

import java.util.Calendar;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuoteCPTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuotePPTabPage;


/**
 * task link:https://igartc03.swg.usma.ibm.com/jazz/web/projects/CVT%20DSW#action=com.ibm.team.workitem.viewWorkItem&id=427059.
 * refine the script  and ensure it covers below points:
	com.ibm.dsw.automation.testcase.saas.SQOSaas104and106Test
	
	case a-----for 10.4 requirement:
	1.create a draft quote(need test PA(PAE)/FCT/OEM); 
	2.add customer 
	3.add saas parts
	4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote 
	validation points:
	
	Modify service dates?                               radio button works well;when the service date question (default is "no"),no option list of 'If desired select service start or end date' display;
	Estimated provisioning days                         the input text works well;
	If desired select service start or end date         the option works well,if wrong input,the err msg comes out(TODO);After recaculating,the selected date could display the same as you selected
	the term field                                      After recaculating,the selected date could display the correct value as you selected(TODO)
	5.Edit the sales inf tab and special bid necesary,submit the quote
	6.search the submitted quote and check the part and pricing tab again,the mentioned point could display right
	
	case b-----for 10.6 requirement:
	1.create a draft quote(need test PA(PAE)/FCT); ; 
	2.add one of the below customer 
	##################
	Use below SQL to get the cust who can do add-on/trade up
	     * select q.SOLD_TO_CUST_NUM from ebiz1.WEB_QUOTE q where q.WEB_QUOTE_NUM in
	     * (
	     * select wq.WEB_QUOTE_NUM from ebiz1.WEB_QUOTE_CONFIGRTN wq where wq.CONFIGRTN_ACTION_CODE='AddTrd'
	     * )
	     * and q.CNTRY_CODE='USA'
	     * order by q.ADD_DATE desc
	     * fetch first 10 rows only@
	     * 
	     * SOLD_TO_CUST_NUM
	     * ----------------
	     * 0007240515
	     * 0007011428
	     * 0007011428
	     * 0007011428
	     * 0007000244
	     * 0007000244
	     * 0007000244
	     * 0007000244
	     * 0007000244
	     * 0007000244
	##################
	3.in 'customer and partner' tab,click the 'Software as a Service' link;
	in 'Active Software as a Service' tab,click 'Update configuration' link;
	in the popup,select 'no' ,then add a part and change a selected part inf(addon-tradup);
	then submit
	comment:better to update configuration for 2 kinds of product
	4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote 
	validation points:
	
	Modify service dates?                               radio button works well;when the service date question (default is "no"),no option list of 'If desired select service start or end date' display;
	Estimated provisioning days                         the input text works well;
	Service End Date                                    the option works well,if wrong input,the err msg comes out(TODO);After recaculating,the selected date could display the same as you selected
	the term field                                      After recaculating,the selected date could display the correct value as you selected(TODO)
	5.Edit the sales inf tab and special bid necesary,submit the quote
	6.search the submitted quote and check the part and pricing tab again,the mentioned point could display right
	
	case c-----bid iteration:
	We should hide the radio button section in bid iteration mode.
	could refine the common method to flow layer from bid iteration prepare(399309)
	
	
	case d---FCTtoPa
	same as normal saas add-on and trade up.
	comment:udpate the FCTtoPa prepare step soon
	
	
	reference:
	Notes://D01DBL35/8525721300181EEE/477C010BD75EC87C85256A2D006A582E/1DCAB860B2B5047585257AD0006CB1EA

 * 
 * @author Will
 * @date Jan 7, 2013
 */
public class SQOSaas104and106Test extends BaseTestCase {

	public static WebdriverLogger loggerContxt = WebdriverLogger.getLogger(SQOSaas104and106Test.class.getName());
	public static final String BUSNESS_CODE_104="104";
	public static final String BUSNESS_CODE_106="106";
	
	public void assemblyBizFlow() {

//		test104new(); // passed
		
//		test104newFCT(); // passed
		
//		test104newOEM(); // passed

		test104SpeicalBid(); // The user which name is dswweb5@us.ibm.com has gone
		
//		test106(); //  passed
		
//		test106FCT(); // There is no data
		
		loggerContxt.info("@Test SQOSaas104and106Test has passed!");
	}


	/**
	 * case a-----for 10.4 requirement:
	 * 1.create a draft quote; 
		2.add customer 
		3.add saas parts
		4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote 
		validation points:
		
		Modify service dates?                               radio button works well;when the service date question (default is "no"),no option list of 'If desired select service start or end date' display;
		Estimated provisioning days                         the input text works well;
		If desired select service start or end date         the option works well,if wrong input,the err msg comes out(TODO);After recaculating,the selected date could display the same as you selected
		the term field                                      After recaculating,the selected date could display the correct value as you selected(TODO)
		5.Edit the sales inf tab and special bid necesary,submit the quote
		6.search the submitted quote and check the part and pricing tab again,the mentioned point could display right
	 * 
	 * @author wesley
	 * @date Feb 17, 2013
	 * */
	@Test(description = "test104new")
	void test104new() {
		quitAndReset();
		
		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");

		
		SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
		loggerContxt.info("Create new sales quote,Draft quote header display, " +
				"Draft quote common action buttons display finished.....");

		// 2.add customer
		currentQuotePage=quoteFlow.processCustPartnerTab(currentQuotePage);
		loggerContxt
				.info("add customer finished.....");

		// 3.add saas part
		SQOPartsAndPricingTabPage	ppTab = quoteFlow.addSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add Saas parts finished.....");
		
		//4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote 
		ppTab = quoteFlow.changeModifyServiceDate(ppTab,BUSNESS_CODE_104);
		loggerContxt.info("validate ModifyServiceDate finished.....");
		
		ppTab = quoteFlow.setDates(ppTab);
		
		//5. Edit the sales inf tab and special bid necesary,submit the quote
		SQOSalesInfoTabPage salesTab=	quoteFlow.editSalesInfoTab(ppTab);
		currentQuotePage=quoteFlow.processSpecialBidTab(salesTab);
		
		if (currentQuotePage.hasSubmitBtn()) {
			quoteFlow.runSubmitQuote(currentQuotePage);
			loggerContxt.info("Edit the sales inf tab and special bid necesary,submit the quote finished.....");

			//6.search the submitted quote and check the part and pricing tab again,the mentioned point could display right
			SQOSelectedQuoteCPTabPage sbmdCPTabl = quoteFlow.findQuoteByNum(currentQuotePage);
			
			quoteFlow.checkSubmittedQuote(sbmdCPTabl);
			loggerContxt.info("search the submitted quote and check the part and pricing tab again,the mentioned point could display right finished.....");
			loggerContxt.info("have a check a the CP/SALES/PP/APPROVAL Tab finished.....");
		}

		loggerContxt.info("@Test test104new has passed!");
	}

	@Test(description = "test104SpeicalBid")
	void test104SpeicalBid() {
		quitAndReset();
		// TODO We should hide the radio button section in bid iteration mode.
		// TODO Notes://CAMDB10/85256B890058CBA6/8278F0CE794010B985256D24005FCB4F/00EF67AF00798F3A85257B1D0024780C
	
		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");

		SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
		loggerContxt.info("Create new sales quote,Draft quote header display, " +
				"Draft quote common action buttons display finished.....");

		// 2.add customer
		currentQuotePage = quoteFlow.processCustPartnerTab(currentQuotePage);
		loggerContxt
				.info("add customer finished.....");

		// 3.add saas part
		SQOPartsAndPricingTabPage ppTab = quoteFlow.addSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add Saas parts finished.....");
		
		SQOSalesInfoTabPage salesTab = quoteFlow.editSalesInfoTab(ppTab);
		currentQuotePage = quoteFlow.processSpecialBidTab(salesTab);
		sqoHomePage = quoteFlow.approveSpecialBidQuote(currentQuotePage);
		if (null != sqoHomePage) {
			currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
		} 
		
		//4.search the submitted quote and check the part and pricing tab again,the mentioned point could display right
		SQOSelectedQuoteCPTabPage sbmdCPTabl = quoteFlow.findQuoteByNum(currentQuotePage);
		SQOSelectedQuotePPTabPage papTab = quoteFlow.goToPPTabSubmittedQuote(sbmdCPTabl);
		currentQuotePage = papTab.createACopyLinkClick();
		ppTab = currentQuotePage.goToPartsAndPricingTab();
		
		// check the date selection weather exist.
		//5.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote
		ppTab.validateModifyServiceDateExist(true);
		loggerContxt.info("validate ModifyServiceDate finished.....");
		
		if (null != sqoHomePage) {
			papTab = quoteFlow.goToSelectedQuotePPTabPage();
		    papTab = papTab.createBidIterationLinkClick();
		    papTab.validateModifyServiceDateExist(false);
		} 

		loggerContxt.info("@Test test104SpeicalBid has passed!");
	}
	
	@Test(description = "test104newFCT")
	void test104newFCT() {
		quitAndReset();
		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");
		quoteFlow.getPropBean().setLob("FCT");
		
		SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
		loggerContxt.info("Create new sales quote,Draft quote header display, " +
				"Draft quote common action buttons display finished.....");

		// 2.add customer
		currentQuotePage=quoteFlow.processCustPartnerTab(currentQuotePage);
		loggerContxt
				.info("add customer finished.....");

		// 3.add saas part
		SQOPartsAndPricingTabPage ppTab = quoteFlow.browsePart(currentQuotePage);
		loggerContxt.info("add Saas parts finished.....");
		
		//4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote 
		ppTab = quoteFlow.changeModifyServiceDate(ppTab,BUSNESS_CODE_104);
		loggerContxt.info("validate ModifyServiceDate finished.....");
		
		ppTab = quoteFlow.setDates(ppTab);
		
		//5. Edit the sales inf tab and special bid necesary,submit the quote
		SQOSalesInfoTabPage salesTab=	quoteFlow.editSalesInfoTab(ppTab);
		currentQuotePage=quoteFlow.processSpecialBidTab(salesTab);
		
		if (currentQuotePage.hasSubmitBtn()) {
			quoteFlow.runSubmitQuote(currentQuotePage);
			loggerContxt.info("Edit the sales inf tab and special bid necesary,submit the quote finished.....");

			//6.search the submitted quote and check the part and pricing tab again,the mentioned point could display right
			SQOSelectedQuoteCPTabPage sbmdCPTabl=quoteFlow.findQuoteByNum(currentQuotePage);
			
			quoteFlow.checkSubmittedQuote(sbmdCPTabl);
			loggerContxt.info("search the submitted quote and check the part and pricing tab again,the mentioned point could display right finished.....");
			loggerContxt.info("have a check a the CP/SALES/PP/APPROVAL Tab finished.....");
		}

		loggerContxt.info("@Test test104newFCT has passed!");
	}

	@Test(description = "test104newOEM")
	void test104newOEM() {
		quitAndReset();
		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");
		quoteFlow.getPropBean().setLob("OEM");
		
		SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
		loggerContxt.info("Create new sales quote,Draft quote header display, " +
				"Draft quote common action buttons display finished.....");

		// 2.add customer
		currentQuotePage=quoteFlow.processCustPartnerTab(currentQuotePage);
		loggerContxt
				.info("add customer finished.....");

		// 3.add saas part
		SQOPartsAndPricingTabPage	ppTab = quoteFlow.browsePart(currentQuotePage);
		loggerContxt.info("add Saas parts finished.....");
		
		//4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote 
		ppTab = quoteFlow.changeModifyServiceDate(ppTab,BUSNESS_CODE_104);
		loggerContxt.info("validate ModifyServiceDate finished.....");
		
		ppTab = quoteFlow.setDates(ppTab);
		
		//5. Edit the sales inf tab and special bid necesary,submit the quote
		SQOSalesInfoTabPage salesTab=	quoteFlow.editSalesInfoTab(ppTab);
		currentQuotePage=quoteFlow.processSpecialBidTab(salesTab);
		
		if (currentQuotePage.hasSubmitBtn()) {
			quoteFlow.runSubmitQuote(currentQuotePage);
			loggerContxt.info("Edit the sales inf tab and special bid necesary,submit the quote finished.....");

			//6.search the submitted quote and check the part and pricing tab again,the mentioned point could display right
			SQOSelectedQuoteCPTabPage sbmdCPTabl=quoteFlow.findQuoteByNum(currentQuotePage);
			
			quoteFlow.checkSubmittedQuote(sbmdCPTabl);
			loggerContxt.info("search the submitted quote and check the part and pricing tab again,the mentioned point could display right finished.....");
			loggerContxt.info("have a check a the CP/SALES/PP/APPROVAL Tab finished.....");
		}

		loggerContxt.info("@Test test104newOEM has passed!");
	}

	/**
	 * Use below SQL to get the cust who can do add-on/trade up
	 * select q.SOLD_TO_CUST_NUM from ebiz1.WEB_QUOTE q where q.WEB_QUOTE_NUM in
	 * (
	 * select wq.WEB_QUOTE_NUM from ebiz1.WEB_QUOTE_CONFIGRTN wq where wq.CONFIGRTN_ACTION_CODE='AddTrd'
	 * )
	 * and q.CNTRY_CODE='USA'
	 * order by q.ADD_DATE desc
	 * fetch first 10 rows only@
	 * 
	 * SOLD_TO_CUST_NUM
	 * ----------------
	 * 0007240515
	 * 0007011428
	 * 0007011428
	 * 0007011428
	 * 0007000244
	 * 0007000244
	 * 0007000244
	 * 0007000244
	 * 0007000244
	 * 0007000244
	 * 
	 * 10 record(s) selected.
	 */
	@Test(description = "test106")
	private void test106() {
		quitAndReset();
		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		quoteFlow.getPropBean().setCustomerNum("0007011428");
		
		SQOMyCurrentQuotePage currentQuotePage = quoteFlow.CreateAddOnQuote();
		// 4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote
		SQOPartsAndPricingTabPage ppTab = currentQuotePage.goToPartsAndPricingTab();
		ppTab = quoteFlow.changeModifyServiceDate(ppTab,BUSNESS_CODE_106);
		loggerContxt.info("validate ModifyServiceDate finished.....");

		ppTab = quoteFlow.dateSetting(ppTab);
		
		if (currentQuotePage.hasSubmitBtn()) {
			quoteFlow.runSubmitQuote(currentQuotePage);
			loggerContxt.info("Edit the sales inf tab and special bid necesary,submit the quote finished.....");

			//6.search the submitted quote and check the part and pricing tab again,the mentioned point could display right
			SQOSelectedQuoteCPTabPage sbmdCPTabl=quoteFlow.findQuoteByNum(currentQuotePage);
			
			quoteFlow.checkSubmittedQuote(sbmdCPTabl);
			loggerContxt.info("search the submitted quote and check the part and pricing tab again,the mentioned point could display right finished.....");
			loggerContxt.info("have a check a the CP/SALES/PP/APPROVAL Tab finished.....");
		}
		
		loggerContxt.info("@Test test106 has passed!");
	}
	
//	@Test(description = "testFCTtoPa")
	private void testFCTtoPa() {
		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");

		
		SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
		loggerContxt.info("Create new sales quote,Draft quote header display, " +
				"Draft quote common action buttons display finished.....");

		// 2.add customer
		currentQuotePage = quoteFlow.processCustPartnerTab(currentQuotePage);
		loggerContxt
				.info("add customer finished.....");

		// 3.add saas part
		SQOPartsAndPricingTabPage ppTab = quoteFlow.addSassPartInActiveAassTab(currentQuotePage);
		
		loggerContxt.info("@Test testFCTtoPa has passed!");
	}
	
	@Test(description = "test106FCT")
	private void test106FCT() {
		quitAndReset();
		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		quoteFlow.getPropBean().setLob("FCT");
		
		SQOMyCurrentQuotePage currentQuotePage = quoteFlow.CreateAddOnQuote();
		// 4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote
		SQOPartsAndPricingTabPage ppTab = currentQuotePage.goToPartsAndPricingTab();
		ppTab = quoteFlow.changeModifyServiceDate(ppTab,BUSNESS_CODE_106);
		loggerContxt.info("validate ModifyServiceDate finished.....");

		ppTab = quoteFlow.setDates(ppTab);
		
		if (currentQuotePage.hasSubmitBtn()) {
			quoteFlow.runSubmitQuote(currentQuotePage);

			loggerContxt.info("Edit the sales inf tab and special bid necesary,submit the quote finished.....");

			//6.search the submitted quote and check the part and pricing tab again,the mentioned point could display right
			SQOSelectedQuoteCPTabPage sbmdCPTabl=quoteFlow.findQuoteByNum(currentQuotePage);
			
			//TODO
			quoteFlow.checkSubmittedQuote(sbmdCPTabl);
			loggerContxt.info("search the submitted quote and check the part and pricing tab again,the mentioned point could display right finished.....");
			loggerContxt.info("have a check a the CP/SALES/PP/APPROVAL Tab finished.....");
		}

		loggerContxt.info("@Test test106FCT has passed!");
	}

	public static void main(String[] args) throws Exception {
		SQOSaas104and106Test test = new SQOSaas104and106Test();
		test.setUp();
		test.assemblyBizFlow();
		
//		TestUtil.string2Date("2013" + "-" + "5" + "-" + "1");
//		test.test104_467750();
//		test.test104new();
	}

	void test104_467750() {
		
		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");

		
		SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
		loggerContxt.info("Create new sales quote,Draft quote header display, " +
				"Draft quote common action buttons display finished.....");

		// 3.add saas part
		SQOPartsAndPricingTabPage	ppTab = quoteFlow.addSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add Saas parts finished.....");
		
		//4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote 
		String type = "CE";
		boolean yesRadio = true;
		int provDays = 20;
		Calendar quoteStartDate = Calendar.getInstance();
		Calendar expirDate = Calendar.getInstance();
		expirDate.add(Calendar.DATE, 30);
		Calendar estOrdDate = Calendar.getInstance();
		estOrdDate.add(Calendar.DATE, 10);
		Calendar extenDate =  Calendar.getInstance();
		extenDate.add(Calendar.YEAR, 5);
		
		//SQOPartsAndPricingTabPage partsAndPricingTab,String type,  boolean yesRadio, int provDays, Calendar quoteStartDate, Calendar expirDate, Calendar estOrdDate, Calendar extenDate ) {
		ppTab = quoteFlow.changeModifyServiceDate(ppTab,type,yesRadio,provDays,quoteStartDate,expirDate,estOrdDate,extenDate);
		
		extenDate.add(Calendar.MONTH, 1);
		extenDate.set(Calendar.DATE, 1);
		ppTab = quoteFlow.changeModifyServiceDate(ppTab,type,yesRadio,provDays,quoteStartDate,expirDate,estOrdDate,extenDate);
		loggerContxt.info("validate ModifyServiceDate finished.....");
	}

	void test104_467070() {
			
			// 1.create a draft quote;
			SQOQuoteCommonFlow quoteFlow = getCommonFlow();
			SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf());
			loggerContxt.info("Login SQO finished.....");
	
			
			SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
			loggerContxt.info("Create new sales quote,Draft quote header display, " +
					"Draft quote common action buttons display finished.....");
	
			// 2.add customer
			currentQuotePage=quoteFlow.processCustPartnerTab(currentQuotePage);
			loggerContxt
					.info("add customer finished.....");
	
			// 3.add saas part
			SQOPartsAndPricingTabPage	ppTab = quoteFlow.addSaasPartToQuote(currentQuotePage);
			loggerContxt.info("add Saas parts finished.....");
			
			//4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote 
			String type = "CE";
			boolean yesRadio = false;
			int provDays = 20;
			Calendar quoteStartDate = Calendar.getInstance();
			Calendar expirDate = Calendar.getInstance();
			expirDate.add(Calendar.DATE, 30);
			Calendar estOrdDate = Calendar.getInstance();
			estOrdDate.add(Calendar.DATE, 10);
			Calendar extenDate =  Calendar.getInstance();
			extenDate.add(Calendar.YEAR, 1);
			
			ppTab = quoteFlow.changeModifyServiceDate(ppTab,type,yesRadio,provDays,quoteStartDate,expirDate,estOrdDate,extenDate);
			loggerContxt.info("validate ModifyServiceDate finished.....");
			
			//SQOPartsAndPricingTabPage partsAndPricingTab,String type,  boolean yesRadio, int provDays, Calendar quoteStartDate, Calendar expirDate, Calendar estOrdDate, Calendar extenDate ) {
			
			//5. Edit the sales inf tab and special bid necesary,submit the quote
			SQOSalesInfoTabPage salesTab=	quoteFlow.editSalesInfoTab(ppTab);
			currentQuotePage=quoteFlow.processSpecialBidTab(salesTab);
			
			if (currentQuotePage.hasSubmitBtn()) {
				quoteFlow.runSubmitQuote(currentQuotePage);
				loggerContxt.info("Edit the sales inf tab and special bid necesary,submit the quote finished.....");
		
				//6.search the submitted quote and check the part and pricing tab again,the mentioned point could display right
				SQOSelectedQuoteCPTabPage sbmdCPTabl=quoteFlow.findQuoteByNum(currentQuotePage);
				
				//TODO
				quoteFlow.checkSubmittedQuote(sbmdCPTabl);
				loggerContxt.info("search the submitted quote and check the part and pricing tab again,the mentioned point could display right finished.....");
				loggerContxt.info("have a check a the CP/SALES/PP/APPROVAL Tab finished.....");
			}

			loggerContxt.info("test104new has passed!");
	
			// TODO need to verify this PL Notes://CAMDB10/85256B890058CBA6/CD76522BA873968E85256D33004FBB0B/517B9574BD480B5585257B10006E5A54
	
		}
	
	void test104_4672245() {
		test104_467070();
	}
	
	void test104_459342() {
	}
	
	void test104_459261() {
		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");

		
		SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
		loggerContxt.info("Create new sales quote,Draft quote header display, " +
				"Draft quote common action buttons display finished.....");

		// 3.add saas part
		SQOPartsAndPricingTabPage	ppTab = quoteFlow.addSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add Saas parts finished.....");
		
		//4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote 
		String type = "CE";
		boolean yesRadio = false;
		int provDays = 20;
		Calendar quoteStartDate = Calendar.getInstance();
		Calendar expirDate = Calendar.getInstance();
		expirDate.add(Calendar.DATE, 30);
		Calendar estOrdDate = Calendar.getInstance();
		estOrdDate.add(Calendar.DATE, 10);
		Calendar extenDate =  Calendar.getInstance();
		extenDate.add(Calendar.YEAR, 5);
		
		//SQOPartsAndPricingTabPage partsAndPricingTab,String type,  boolean yesRadio, int provDays, Calendar quoteStartDate, Calendar expirDate, Calendar estOrdDate, Calendar extenDate ) {
		ppTab = quoteFlow.changeModifyServiceDate(ppTab,type,yesRadio,provDays,quoteStartDate,expirDate,estOrdDate,extenDate);
		
		loggerContxt.info("validate ModifyServiceDate finished.....");
	}
	
	void test104_458374() {
		// 1.create a draft quote;
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();
		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");

		
		SQOMyCurrentQuotePage currentQuotePage = quoteFlow.createDraftQuote(sqoHomePage);
		loggerContxt.info("Create new sales quote,Draft quote header display, " +
				"Draft quote common action buttons display finished.....");

		// 3.add saas part
		SQOPartsAndPricingTabPage	ppTab = quoteFlow.addSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add Saas parts finished.....");
		
		//4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote 
		String type = "";
		boolean yesRadio = false;
		int provDays = 20;
		Calendar quoteStartDate = Calendar.getInstance();
		Calendar expirDate = Calendar.getInstance();
		expirDate.add(Calendar.DATE, 30);
		Calendar estOrdDate = Calendar.getInstance();
		estOrdDate.add(Calendar.DATE, 10);
		Calendar extenDate =  Calendar.getInstance();
		extenDate.add(Calendar.YEAR, 5);
		
		//SQOPartsAndPricingTabPage partsAndPricingTab,String type,  boolean yesRadio, int provDays, Calendar quoteStartDate, Calendar expirDate, Calendar estOrdDate, Calendar extenDate ) {
		ppTab = quoteFlow.changeModifyServiceDate(ppTab,type,yesRadio,provDays,quoteStartDate,expirDate,estOrdDate,extenDate);
		
		loggerContxt.info("validate ModifyServiceDate finished.....");
	}
	
	void test104_457353() {
		test104_467070();
	}
	
	void test104_456743() {
		test104_467070();
	}
	
	void test104_456742() {
		test104_467070();
	}
}
