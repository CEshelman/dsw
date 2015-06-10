package com.ibm.dsw.automation.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriverBackedSelenium;

import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuoteCPTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOStatusSearchTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOStatusSearchTabPage.IBMerAssignee;


public class SQOStatusSearchFlow extends QuoteFlow{
	
	private SQOQuoteCommonFlow quoteFlow;
	
	public SQOStatusSearchFlow(){
		
	}
	
	public SQOStatusSearchFlow(SQOQuoteCommonFlow quoteFlow){
		this.quoteFlow = quoteFlow;
		setDriver(quoteFlow.getDriver());
		selenium = new WebDriverBackedSelenium(driver, driver.getCurrentUrl());
		setSelenium(selenium);
		setLogonInf(getLogonInf());
	}
	
	public void fillStatsSearchConditions(SQOStatusSearchTabPage statusSearchPage){
		statusSearchPage.fillStatsSearchConditions();
	}
	
	public void fillStatsSearchWithCustomizedConditions(SQOStatusSearchTabPage statusSearchPage){
		statusSearchPage.fillStatsSearchWithCustomizedConditions();
	}
	
	public void fillQuotePeriodAsQuarter(SQOStatusSearchTabPage statusSearchPage, String[] peroidArr){
		if (peroidArr == null || peroidArr.length<1) {
			loggerContxt.error("Please kindly configure the indexes of the options for the dropdown list...");
			return;
		}
		statusSearchPage.fillQuotePeriodAsQuarter(peroidArr);
	}
	
	public void fillQuotePeriodAsMonth(SQOStatusSearchTabPage statusSearchPage, String[] peroidArr){
		if (peroidArr == null || peroidArr.length<1) {
			loggerContxt.error("Please kindly configure the indexes of the options for the dropdown list...");
			return;
		}
		statusSearchPage.fillQuotePeriodAsMonth(peroidArr);
	}
	
	/**
	 * click "IBMer assgined" tab 
	 * @param statusSearchPage
	 */
	public void clickIBMerAssignedTab(SQOStatusSearchTabPage statusSearchPage){
		statusSearchPage.clickIBMerAssignedTab();
		statusSearchPage = statusSearchPage.newCache();
	}
	/**
	 * click "customer" tab 
	 * @param statusSearchPage
	 */
	public void clickCustomerTab(SQOStatusSearchTabPage statusSearchPage){
		statusSearchPage.clickCustomerTab();
		statusSearchPage = statusSearchPage.newCache();
	}
	/**
	 * click "partner" tab 
	 * @param statusSearchPage
	 */
	public void clickPartnerTab(SQOStatusSearchTabPage statusSearchPage){
		statusSearchPage.clickPartnerTab();
		statusSearchPage = statusSearchPage.newCache();
	}
	
	public void clickCountryRegionTab(SQOStatusSearchTabPage statusSearchPage){
		statusSearchPage.clickCountryRegionTab();
		statusSearchPage = statusSearchPage.newCache();
	}
	
	public void clickApprovalAttributesTab(SQOStatusSearchTabPage statusSearchPage){
		statusSearchPage.clickApprovalAttributesTab();
		statusSearchPage = statusSearchPage.newCache();
	}
	/**
	 * 
	 * @param statusSearchPage
	 * @param current 0- ' Creator/owner/submitter ', 1-' Editor ', 2 - ' Approver named on the quote '
	 *                3 - ' Reviewer ', 4 - ' Approver with approval pending ', 5 - All
	 * @param count should equal to the amount of the radio button(here is 5).
	 */
	public void selectCheckboxForIBMerAssignedTab(SQOStatusSearchTabPage statusSearchPage, int current, int count){
		if (current != count) {
			statusSearchPage.checkboxInIBMerAssignedTab(current);
		}else{
			statusSearchPage.checkAllBoxInIBMerAssignedTab();
		}
	}
	/**
	 * 
	 * @param sqoHomePage
	 * @return
	 */
	public void findQuoteByIBMerAssigned(SQOStatusSearchTabPage statusSearchPage){
		
		loggerContxt
		.info("Check all checkboxes of the following sections Find the following quote types "
				+ "Find quotes and special bids with the following overall statuses"
				+ " Select 'Date submitted (descending)' in the Sort by dropdown "
				+ "Check the Make above selections my default checkbox"
				+ " Select the IBMer assigned Press on the 'Find quotes' button");
		statusSearchPage.findQuoteByIBMerAssigned();
	}
	
	public void findQuoteByIBMerAssigned(SQOStatusSearchTabPage statusSearchPage, String assignee){
		loggerContxt.info(String.format("Change the criteria to assigned to IBMer::%s",assignee));
		statusSearchPage.findQuoteByIBMerAssigned(assignee);
		}
	//Find the quotes in customer tab base on customer name
	public void findQuoteByCustomerName(SQOStatusSearchTabPage statusSearchPage, String customerName){
		loggerContxt.info(String.format("Find quote in customer tab base on customer name::%s", customerName));
		statusSearchPage.findQuoteByCustomerName(customerName);
	}
	
	public void findQuoteByPartnerName(SQOStatusSearchTabPage statusSearchPage, String partnerName, int radio){
		loggerContxt.info(String.format("Find quote in partner tab base on partner name::%s",partnerName));
		statusSearchPage.findQuoteByPartnerName(partnerName,radio);
	}
	
	public void findQuoteByCountryRegion(SQOStatusSearchTabPage statusSearchPage,String IMTRegion, String country, String state){
		loggerContxt.info(String.format("Find quote in Country/Region tab base on:: IMTRegtion=%s, country=%s, state=%s...",IMTRegion,country,state));
		statusSearchPage.findQuoteByCountryRegion(IMTRegion, country, state);
	}
	
	public SQOStatusSearchTabPage changeStatusSearchCriteria(SQOStatusSearchTabPage statusSearchPage){
		return statusSearchPage.getSearchDisPlayPage().changeCriteria();
	}
	
	public SQOSelectedQuoteCPTabPage viewQuoteDetail(SQOStatusSearchTabPage statusSearchPage){
		SQOSelectedQuoteCPTabPage page = statusSearchPage.getSearchDisPlayPage().viewQuoteDetail();
		return page;
	}
	
	/**
	 * The data format e.g. a|2;b|3;c|4
	 * @param info
	 * @param delim1 the first level delimiter
	 * @param delim2 the second level delimiter
	 * @return
	 */
	public List<IBMerAssignee> parseAssigneeInfo(SQOStatusSearchTabPage statusSearchPage, String info, String delim1, String delim2){
		if (StringUtils.isBlank(delim1)) {
			delim1 = ";";
		}
		if (StringUtils.isBlank(delim2)) {
			delim2 = "\\|";
		}
		List<IBMerAssignee> assigneeList = new ArrayList<IBMerAssignee>();
		String email = "";
		int ownerRole = 0;
		StringTokenizer tokenizer = new StringTokenizer(info, delim1);
		while (tokenizer.hasMoreElements()) {
			String temp = (String) tokenizer.nextElement();
			loggerContxt.info(String.format("Now processing the assignee info::%s..", temp));
			String[] tempArr = temp.split(delim2);
			if (tempArr == null || tempArr.length<2) {
				continue;
			}
			email = tempArr[0];
			try{
				ownerRole = Integer.parseInt(tempArr[1]);
			}catch(Exception e){
				loggerContxt.error(String.format("Failed to parse the owner role %s to a int value.", tempArr[1]));
				continue;
			}
			loggerContxt.info(String.format("testing assignee is %s..",email));
			SQOStatusSearchTabPage.IBMerAssignee assignee = statusSearchPage. new IBMerAssignee(email, ownerRole);
			assigneeList.add(assignee);
		}
		return assigneeList;
	}


	public SQOQuoteCommonFlow getQuoteFlow() {
		return quoteFlow;
	}


	public void setQuoteFlow(SQOQuoteCommonFlow quoteFlow) {
		this.quoteFlow = quoteFlow;
	}
	

}
