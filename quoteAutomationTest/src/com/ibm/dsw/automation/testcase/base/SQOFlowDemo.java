package com.ibm.dsw.automation.testcase.base;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.SQOPropertiesBean;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;

public class SQOFlowDemo extends BaseTestCase {

	public static WebdriverLogger loggerContxt =  WebdriverLogger.getLogger(SQOFlowDemo.class.getName());
	
	@Test(description = "quote demo")
	public void assemblyBizFlow(){
		SQOQuoteCommonFlow quoteFlow=new SQOQuoteCommonFlow();
		quoteFlow.setDriver(driver);
		quoteFlow.setPropBean((SQOPropertiesBean)propBean);
		quoteFlow.setSelenium(selenium);
		
		quoteFlow.dummy();
		
	}



	public static void main(String[] args) throws Exception {
		SQOFlowDemo test = new SQOFlowDemo();
		test.setUp();
		test.assemblyBizFlow();
	}

}
