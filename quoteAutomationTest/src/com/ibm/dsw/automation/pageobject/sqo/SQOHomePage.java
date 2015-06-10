package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pao.DistSoftHomePage;
import com.ibm.dsw.automation.pageobject.pao.PAOHomePage;
import com.ibm.dsw.automation.pageobject.pgs.ImportSalesQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;


public class SQOHomePage extends PGSBasePage {

	@FindBy(linkText = "Renewal quotes")
	public WebElement renewalQuote;
	@FindBy(linkText="Customer actions")
	public WebElement customerActions;
	
	@FindBy(linkText="Special bid")
	public WebElement specialBid;
	public SQOHomePage(WebDriver driver) {
		super(driver);
	}

	public SQOCreateQuotePage gotoCreateQuote() {
		loggerContxt.info("Click on create a Sales Quote");
		createSaleQuoteLink.click();
		waitForElementLoading(5000L);
		SQOCreateQuotePage page = new SQOCreateQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		loggerContxt.info("Enter 'DB2 Connect Enterprise Edition' into Part description field and click submit--->done!");
		return page;
	}	
	
	public SQOStatusSearchTabPage gotoStatus() {
		statusLink.click();
		SQOStatusSearchTabPage page = new SQOStatusSearchTabPage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	public SQOApproveQueuePage gotoApproveQueue() {
	    WebElement approveQueueLink = driver.findElement(By.linkText("Approval queue"));
	    approveQueueLink.click();
        SQOApproveQueuePage page = new SQOApproveQueuePage(driver);
        loadPage(page,this.WAIT_TIME);
        return page;
    }
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 7, 2013
	 * @return
	 */
	public MyCurrentQuotePage gotoCurrenQuotePage(){
		myCurrentQuoteLink.click();
		MyCurrentQuotePage page = new SQOMyCurrentQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	public DistSoftHomePage goToDistributedSoftwareOnlinePage() {
		elementClickByLinkText("Distributed software online");
		return new DistSoftHomePage(getDriver());
	}
	
	public DistSoftHomePage gotoPgsSiteSelectPage() {
		return new DistSoftHomePage(getDriver());
	}
	
	public ImportSalesQuotePage gotoImportSalesQuote() {
		
		loggerContxt.info("Click Create sales quote link .....");
		createSaleQuoteLink.click();
		loggerContxt.info("Click Import a sales quote spreadsheet link .....");
		importSalesQuoteSpreadsheetLink.click();
		ImportSalesQuotePage page = new ImportSalesQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	public SQODisplayApprovalQueueTrackerPage gotoApprovalQueueTrackerPage() {
		elementClickByLinkText("Approval queue tracker");
		switchToWindow(getDriver(), "IBM Software Quote and Order: ");
		return new SQODisplayApprovalQueueTrackerPage(getDriver());
	}
	
	public SQODisplayStatusTrackerPage gotoStatusTrackerPage() {
		elementClickByLinkText("Status tracker");
		switchToWindow(getDriver(), "IBM Software Quote and Order: ");
		return new SQODisplayStatusTrackerPage(getDriver());
	}
	
	public SQOQuoteSearchIBMSelfPage gotoRenewalQuote(){
		renewalQuote.click();
		SQOQuoteSearchIBMSelfPage page = new SQOQuoteSearchIBMSelfPage(getDriver());
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	public SQORQCustomerActionsSearchPage gotoCustomerActions(){
		if (!customerActions.isDisplayed()) {
			renewalQuote.click();
		}
		customerActions.click();
		SQORQCustomerActionsSearchPage page = new SQORQCustomerActionsSearchPage(getDriver());
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	public SQORenewalQuoteSearchTabPage gotoSQORenewalQuote(){
		renewalQuote.click();
		SQORenewalQuoteSearchTabPage page = new SQORenewalQuoteSearchTabPage(getDriver());
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	public SQOSpecialBidTabPage gotoSpecialBid(){
		specialBid.click();
		SQOSpecialBidTabPage page = new SQOSpecialBidTabPage(getDriver());
		loadPage(page,this.WAIT_TIME);
		return page;
	}
	
	public PAOHomePage gotoPAOHomePage(String siteNum) {
		loggerContxt.info(String.format("Try to choose the site number as::%s ont sign in page before login PGS system.",siteNum));
		if (isTextPresent("Site numbers")) {
			driver.findElement(By.linkText(siteNum)).click();
		}else{
			loggerContxt.info("Only one default site, no other sites for choices.");
		}
		loggerContxt.info("Action done");
		PAOHomePage page = new PAOHomePage(driver);
		return page;
	}	
}
