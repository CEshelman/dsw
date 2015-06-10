package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;


public class PGSHomePage extends PGSBasePage {

	public PGSHomePage(WebDriver driver) {
		super(driver);
	}

	public PGSCreateQuotePage gotoCreateQuote() {
		createSaleQuoteLink.click();
		PGSCreateQuotePage page = new PGSCreateQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}

	public MyCurrentQuotePage gotoCurrenQuotePage(){

		myCurrentQuoteLink.click();
		MyCurrentQuotePage page = new MyCurrentQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;

	}
	
	public PGSHomePage goPartnerGuidedSelling() {

		partnerGuidedSellingLink.click();
		PGSHomePage page = new PGSHomePage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	
	}
	

	public StatusSearchTabPage gotoStatus() {
		statusLink.click();
		StatusSearchTabPage page = new StatusSearchTabPage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}	
	
	public ImportSalesQuotePage gotoImportSalesQuote() {
		
		loggerContxt.info("Click Import a sales quote spreadsheet link .....");
		importSalesQuoteSpreadsheetLink.click();
		ImportSalesQuotePage page = new ImportSalesQuotePage(driver);
		loadPage(page,this.WAIT_TIME);
		return page;
	}

}
