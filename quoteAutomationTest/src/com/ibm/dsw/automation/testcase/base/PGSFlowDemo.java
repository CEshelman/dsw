package com.ibm.dsw.automation.testcase.base;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.PGSPropertiesBean;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.PGSQuoteCommonFlow;

public class PGSFlowDemo extends BaseTestCase {

	public static WebdriverLogger loggerContxt = WebdriverLogger
			.getLogger(PGSFlowDemo.class.getName());

	@Test(description = "PGS quote demo")
	public void assemblyBizFlow() {
		PGSQuoteCommonFlow quoteFlow = this.getPGSCommonFlow();
		quoteFlow.setDriver(driver);
		quoteFlow.setPropBean((PGSPropertiesBean) propBean);
		quoteFlow.dummy();
	}

	public static void main(String[] args) throws Exception {
		PGSFlowDemo test = new PGSFlowDemo();
		test.setUp();
		test.assemblyBizFlow();
	}

}
