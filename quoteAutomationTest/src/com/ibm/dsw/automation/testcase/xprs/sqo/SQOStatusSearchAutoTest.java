package com.ibm.dsw.automation.testcase.xprs.sqo;

import java.util.List;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.SQOPropertiesBean;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.SQOStatusSearchFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOApproveQueuePage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayStatusSearchReslutPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuoteCPTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOStatusSearchTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOStatusSearchTabPage.IBMerAssignee;

/**
 * Used for the XPRS script auto validation in SIT phase.
 * XPRS script name: Status Searches (main script) 
 * Team room link: Notes://D01DBL35/8525721300181EEE/4B87A6F6EAEAADD385256A2D006A582C/9BFDA5C0381FBA8D8525731D0072A9E6 
 * @author duzhiwei
 *
 */
public class SQOStatusSearchAutoTest extends BaseTestCase {

	private WebdriverLogger loggerContxt = WebdriverLogger
			.getLogger(this.getClass().getName());

	SQOHomePage sqoHomePage = null;
	private SQOStatusSearchTabPage statusSearchPage = null;
	SQODisplayStatusSearchReslutPage searchResultPage = null;
	SQOStatusSearchFlow quoteFlow = null;
	SQOSelectedQuoteCPTabPage sbmdCPTab = null;
	SQOApproveQueuePage approveQueuePage = null;

	private void loginSqo() throws Exception {

		loggerContxt.info("env....." + env);
		quoteFlow = new SQOStatusSearchFlow(getCommonFlow());
		SQOPropertiesBean SqoProp = quoteFlow.getQuoteFlow().getPropBean();

		try {
			if (Boolean.parseBoolean(getProperty(".local"))) {
				getLogonInf().setSqoLogonUser(SqoProp.getApproverUser());
				getLogonInf().setSqoUserPwd(SqoProp.getAccessLevelApprover());
				sqoHomePage = quoteFlow.getQuoteFlow().loginSqo(getLogonInf(),
						LOGON_IN_SQO_VIA_SQO_JUMP_PAGE_BY_URL);
			} else {
				sqoHomePage = quoteFlow.getQuoteFlow().loginSqo(getLogonInf());
			}
		} catch (Exception e) {
			throw new Exception("Failed to Login SQO page....", e);
		}
		driver.manage().window().maximize();
		loggerContxt.info("Login SQO finished.....");

		loggerContxt
				.info("verify current page whether having this content ......"
						+ this.getProperty(propSub, ".softQtOrd"));
		assertPresentText(this.getProperty(propSub, ".softQtOrd"));

	}

	// @Test(description = "Status Search (main Script)")
	public void runStatusSearch() throws Exception {

		loginSqo();

		// 1, click the status link
		this.statusSearchPage = sqoHomePage.gotoStatus();
		loggerContxt.info("Login SQO finished.....");
		

		// 2, find quote in IBMer assigned tab
		//2.1 find quotes only assigned to me with each respective owner role.
		loggerContxt.info("find quotes only assigned to me with each respective owner role..");
		quoteFlow.clickIBMerAssignedTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		testIMBerAssignedTab();
		loggerContxt.info("find quotes only assigned to me with each respective owner role finished.....");
		
		//2.2 find quotes assigned to other IBMers with different owner roles.
		loggerContxt.info("find quotes assigned to other IBMers with different owner roles.");
		testIBMerAssignedTabWithAssignee("assignee.owner_role");
		
		// 3. find quote in "customer" tab
		quoteFlow.clickCustomerTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		quoteFlow.findQuoteByCustomerName(statusSearchPage, propSub.getProperty("customer.byName"));
		this.statusSearchPage = quoteFlow.changeStatusSearchCriteria(statusSearchPage);
		
		//4. find quote in "partner" tab
		quoteFlow.clickPartnerTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		testPartnerTab();
		
		//5. find quote in "country/regioin" tab
		quoteFlow.clickCountryRegionTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		testCountryRegionTab();
		
		//6. find quote in "Approval Attribute" tab
		quoteFlow.clickApprovalAttributesTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		testApprovalAttributeTab();
		
		//7. test the approver queue navigation
		approveQueuePage = sqoHomePage.gotoApproveQueue();
		int approverQueueRadio = driver.findElements(By.name("queueType")).size();
		for (int i = 0; i < approverQueueRadio; i++) {
			this.approveQueuePage = approveQueuePage.updateViewPerSelection(i);
		}
		
		// 8, find quote in IBMer assigned tab whose submitted time selected as "Quarter"
		//8.1 find quotes only assigned to me with each respective owner role.
		statusSearchPage = sqoHomePage.gotoStatus();
		quoteFlow.clickIBMerAssignedTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		//select multiple options in the Quarter dropdown list.
		String[] quaterArr = propSub.getProperty("Quarter.peroid").split(",");
		quoteFlow.fillQuotePeriodAsQuarter(statusSearchPage, quaterArr);
		
		testIMBerAssignedTab();
	
		//8. 2 find quotes assigned to other IBMers with different owner roles.
		testIBMerAssignedTabWithAssignee("assignee.owner_role");
		
		// 9. find quote in "customer" tab
		quoteFlow.clickCustomerTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		//select multiple options in the Quarter dropdown list.
		quoteFlow.fillQuotePeriodAsQuarter(statusSearchPage, quaterArr);
		quoteFlow.findQuoteByCustomerName(statusSearchPage, propSub.getProperty("customer.byName"));
		this.statusSearchPage = quoteFlow.changeStatusSearchCriteria(statusSearchPage);
		
		//10. find quote in "partner" tab
		quoteFlow.clickPartnerTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		quoteFlow.fillQuotePeriodAsQuarter(statusSearchPage, quaterArr);
		testPartnerTab();
		
		//11. find quote in "country/regioin" tab
		quoteFlow.clickCountryRegionTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		quoteFlow.fillQuotePeriodAsQuarter(statusSearchPage, quaterArr);
		testCountryRegionTab();
		
		//12. find quote in "Approval Attribute" tab
		quoteFlow.clickApprovalAttributesTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		quoteFlow.fillQuotePeriodAsQuarter(statusSearchPage, quaterArr);
		testApprovalAttributeTab();
		
		// 13, find quote in IBMer assigned tab whose submitted time selected as "Quarter"
		//13.1 find quotes only assigned to me with each respective owner role.
		quoteFlow.clickIBMerAssignedTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		//select multiple options in the Quarter dropdown list.
		String[] monthArr = propSub.getProperty("Month.peroid").split(",");
		quoteFlow.fillQuotePeriodAsMonth(statusSearchPage, monthArr);
		
		testIMBerAssignedTab();
	
		//13. 2 find quotes assigned to other IBMers with different owner roles.
		testIBMerAssignedTabWithAssignee("assignee.owner_role");
		
		// 14. find quote in "customer" tab
		quoteFlow.clickCustomerTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		//select multiple options in the Month dropdown list.
		quoteFlow.fillQuotePeriodAsMonth(statusSearchPage, monthArr);
		quoteFlow.findQuoteByCustomerName(statusSearchPage, propSub.getProperty("customer.byName"));
		this.statusSearchPage = quoteFlow.changeStatusSearchCriteria(statusSearchPage);
		
		//15. find quote in "partner" tab
		quoteFlow.clickPartnerTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		quoteFlow.fillQuotePeriodAsMonth(statusSearchPage, monthArr);
		testPartnerTab();
		
		//16. find quote in "country/regioin" tab
		quoteFlow.clickCountryRegionTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		quoteFlow.fillQuotePeriodAsMonth(statusSearchPage, monthArr);
		testCountryRegionTab();
		
		//17. find quote in "Approval Attribute" tab
		quoteFlow.clickApprovalAttributesTab(statusSearchPage);
		quoteFlow.fillStatsSearchConditions(statusSearchPage);
		quoteFlow.fillQuotePeriodAsMonth(statusSearchPage, monthArr);
		testApprovalAttributeTab();
		
		
	}

	public void testIMBerAssignedTab(){
		int count = driver.findElements(By.name("ownerRoles")).size();
		for (int i = 0; i <= count; i++) {
			quoteFlow.selectCheckboxForIBMerAssignedTab(statusSearchPage, i, count);
			quoteFlow.findQuoteByIBMerAssigned(statusSearchPage);
			//  change search criteria
			this.statusSearchPage = quoteFlow.changeStatusSearchCriteria(statusSearchPage);
		}
		loggerContxt.info("find quotes only assigned to me with each respective owner role finished.....");
		
	}
	
	public void testIBMerAssignedTabWithAssignee(String ownerRoleProps){
		int count = driver.findElements(By.name("ownerRoles")).size();
		loggerContxt.info("find quotes assigned to other IBMers with different owner roles.");
		String assigneeInfo = propSub.getProperty(ownerRoleProps);
		List<IBMerAssignee> assignees = quoteFlow.parseAssigneeInfo(statusSearchPage,assigneeInfo,";","\\|");
		for (IBMerAssignee assignee : assignees) {
			quoteFlow.selectCheckboxForIBMerAssignedTab(statusSearchPage, assignee.getOwnerRole(), count);
			quoteFlow.findQuoteByIBMerAssigned(statusSearchPage, assignee.getEmail());
			//  change search criteria
			this.statusSearchPage = quoteFlow.changeStatusSearchCriteria(statusSearchPage);
		}
	}
	
	public void testPartnerTab(){
		//get the number of radio buttons in the cutomer tab for finding quote by parter name
		int radioCount = driver.findElements(By.name("partnerTypeForName")).size();
		for (int i = 0; i < radioCount; i++) {
			quoteFlow.findQuoteByPartnerName(statusSearchPage, propSub.getProperty("parter.byName"), i);
			this.statusSearchPage = quoteFlow.changeStatusSearchCriteria(statusSearchPage);
		}
	}
	
	public void testCountryRegionTab(){
		String IMTRegion = propSub.getProperty("region.IMTRegion");
		String country = propSub.getProperty("region.country");
		String state = propSub.getProperty("region.state");
		quoteFlow.findQuoteByCountryRegion(statusSearchPage, IMTRegion, country, state);
		this.statusSearchPage = quoteFlow.changeStatusSearchCriteria(statusSearchPage);
		IMTRegion=null;country=null;state=null;
	}
	
	public void testApprovalAttributeTab(){
		String region = propSub.getProperty("special.bid.region");
		String restrict = propSub.getProperty("sepcial.bid.restrict");
		String group = propSub.getProperty("approver.group");
		String type = propSub.getProperty(this.env+".approver.type");
		try {
			statusSearchPage.selectOptionsForApprovalAttributesTab(region,restrict,group,type);
			statusSearchPage.selectCheckboxForApproverGroup(true, false);
			statusSearchPage.selectCheckboxForApproverType(false, true);
			statusSearchPage.findQuoteByApprovalAttribute();
			this.statusSearchPage = quoteFlow.changeStatusSearchCriteria(statusSearchPage);
			statusSearchPage.selectCheckboxForApproverGroup(true, true);
			statusSearchPage.selectCheckboxForApproverType(true, true);
			statusSearchPage.findQuoteByApprovalAttribute();
			this.statusSearchPage = quoteFlow.changeStatusSearchCriteria(statusSearchPage);
		} catch (Exception e) {
			loggerContxt.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test(description = "quote order ")
	public void runCase() throws Exception {
		loginSqo();
		String assigneeInfo = propSub.getProperty("assignee.owner_role");
		loggerContxt.info(assigneeInfo);
		SQOStatusSearchFlow quoteFlow = new SQOStatusSearchFlow();
		quoteFlow.parseAssigneeInfo(statusSearchPage, assigneeInfo, null, "\\|");
	}

	public static void main(String[] args) throws Exception {
		SQOStatusSearchAutoTest test = new SQOStatusSearchAutoTest();
		test.setScriptName("SQO Status Search Automation Test");
		try{
		test.setUp();
		test.runStatusSearch();
		test.sendAlertMail("build/mailBody.vm","succeed",null);
		/*
		 * test.runQuoteEdit(); test.runSubmitQuote(); test.runwaiting();
		 * test.runOrder();
		 */
		}catch(Exception e){
//			test.sendAlertMail("build/mailBody.vm","failed",null);
			e.printStackTrace();
		}
		finally{
			if (test.getDriver() != null) {
				
				test.getDriver().close();
			}
		}
	}

}
