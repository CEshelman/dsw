package com.ibm.dsw.automation.pageobject.pgs;

import org.apache.tools.ant.util.StringUtils;
import org.openqa.selenium.WebDriver;

public class UnderEvaluationTabPage extends PGSBasePage {

	public UnderEvaluationTabPage(WebDriver driver) {
		super(driver);
	}

	public PGSStatusSalesQuote viewQuoteDetail(String quoteNum) {
		if (!isTextPresent(prop.getProperty("noquote_found_info"))) {
			String js = "detailUrl('?');";
			js = StringUtils.replace(js, "?", quoteNum);
			this.executeJavaScript(js);
		}
		waitForElementLoading(2000L);
		PGSStatusSalesQuote page = new PGSStatusSalesQuote(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
}
