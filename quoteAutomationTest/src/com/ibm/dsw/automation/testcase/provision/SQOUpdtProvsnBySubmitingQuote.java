package com.ibm.dsw.automation.testcase.provision;

import java.sql.Connection;
import java.sql.SQLException;

import org.testng.annotations.Test;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.SaasParts;
import com.ibm.dsw.automation.common.WebdriverLogger;
import com.ibm.dsw.automation.flow.SQOQuoteCommonFlow;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;

/**
* This is a test class for PGS validate privisioning id
* https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/422210
*************************************************************************
please write script for Updating provisioning id through submitting quote
class:com.ibm.dsw.automation.testcase.provision.SQOUpdtProvsnBySubmtingQuote


Here is the test step&data:
Updating provisioning id through submitting quote
1.One brand - one provisioning id - one configuration

test datas:

one brand: Decision Management

one provisioning id: PV000000000034

one configuration: 5725F8720121211

test steps:

1.1. click the link "create a draf quote"

1.2. choose the start date and expiration date,  find an existing customer in the customers and partners and select a customer

1.3. browse software as a service in the part and pricing tab, choose "Information Management->SPSS->Decision Management->Decision Management" 

1.4. click the link "Configure this service", click the "NO" in the dialog and set the Quantity/Include to 1, then submit the configuration and provisioning id is null here

1.5. click the link "Edit provisioning form" to update the provisioning id

1.6. set the Sales information and save it as the draft quote

1.7. set the bid region, bid district and Justification Summary in the special bid and submit the quote

1.8. select the special bid approver and submit

1.9. find the quotes in the Status

1.10. select the quote you just submitted, and step into the part and pricing tab to check the result


2.One brand - one provisioning id - multiple configurations in a same brand

test datas:

one brand:Lotus Software

one provisioning id: PV000000000040

the first configuration: 5725F4220121211

the second configuration: 5725F4420121211



test steps:

2.1. click the link "create a draf quote"

2.2. choose the start date and expiration date,  find an existing customer in the customers and partners and select a customer

2.3. browse software as a service in the part and pricing tab, choose "Lotus Software->LotusLive->LotusLive->IBM SmartCloud Connections Government" 

2.4. click the link "Configure this service",  set the Quantity/Include to 1, then submit the configuration and provisioning id is null here

2.5. browse software as a service in the part and pricing tab, choose "Lotus Software->LotusLive->LotusLive->IBM SmartCloud Office Automation Suite Govt" 

6. click the link "Configure this service",  set the Quantity/Include to 1, then submit the configuration and provisioning id is null here

2.7. click the link "Edit provisioning form" to update the provisioning id

2.8. set the Sales information and save it as the draft quote

2.9. set the bid region, bid district and Justification Summary in the special bid and submit the quote

2.10. select the special bid approver and submit

2.11. find the quotes in the Status

2.12. select the quote you just submitted, and step into the part and pricing tab to check the result



3.one brand:Lotus Software
test datas:

one provisioning id: PV000000000041

the first configuration: 5725F4220121211

the second configuration: 5725F4420121211



test steps:

3.1. click the link "create a draf quote"

3.2. choose the start date and expiration date,  find an existing customer in the customers and partners and select a customer

3.3. browse software as a service in the part and pricing tab, choose "Lotus Software->LotusLive->LotusLive->IBM SmartCloud Connections Government" 

3.4. click the link "Configure this service", set the Quantity/Include to 1, then submit the configuration and provisioning id is null here

3.5. click the link "Edit provisioning form" to update the provisioning id

3.6. repeat the actions from 3 to 5 to add another configuration

3.7. set the Sales information and save it as the draft quote

3.8. set the bid region, bid district and Justification Summary in the special bid and submit the quote

3.9. select the special bid approver and submit

3.10. find the quotes in the Status

3.11. select the quote you just submitted, and step into the part and pricing tab to check the result


4.Multiple brands - multiple provisioning id - multiple configurations

test datas:

first brand:Other Software

the provisioning id: PV000000000043

the configuration: 5725F6720121212

second brand:Lotus Software

the provisioning id: PV000000000042

the configuration: 5725F4220121212


test steps:

4.1. click the link "create a draf quote"

4.2. choose the start date and expiration date,  find an existing customer in the customers and partners and select a customer

4.3. browse software as a service in the part and pricing tab, choose "Other Software->Coremetrics->Coremetrics Web Analytics->IBM Coremetrics AdTarget Data Feed for Partners" 

4.4. click the link "Configure this service",  set the Quantity/Include to 1, then submit the configuration and provisioning id is null here

4.5. browse software as a service in the part and pricing tab, choose "Lotus Software->LotusLive->LotusLive->IBM SmartCloud Connections Government" 

4.6. click the link "Configure this service",  set the Quantity/Include to 1, then submit the configuration and provisioning id is null here

4.7. click the link "Edit provisioning form" to update the provisioning id for the IBM Coremetrics AdTarget Data Feed for Partners

4.8.click the link "Edit provisioning form" to update the provisioning id for the IBM SmartCloud Connections Government

4.9. set the Sales information and save it as the draft quote

4.10. set the bid region, bid district and Justification Summary in the special bid and submit the quote

4.11. select the special bid approver and submit

4.12. find the quotes in the Status

4.13. select the quote you just submitted, and step into the part and pricing tab to check the result

 




test datas:

first brand:Lotus Software

the provisioning id: PV000000000046

the configuration: 5725F4220121212

second brand:Other Software

the provisioning id: PV000000000048

the configuration: 5725F6720121212

third brand:Information Management

the provisioning id: PV000000000047

the configuration: 5725F8720121212



test steps:

5.1. click the link "create a draf quote"

5.2. choose the start date and expiration date,  find an existing customer in the customers and partners and select a customer

5.3. browse software as a service in the part and pricing tab, choose "Lotus Software->LotusLive->LotusLive->IBM SmartCloud Connections Government" 

5.4. click the link "Configure this service",  set the Quantity/Include to 1, then submit the configuration and provisioning id is null here

5.5.click the link "Edit provisioning form" to update the provisioning id for the IBM SmartCloud Connections Government

5.6. browse software as a service in the part and pricing tab, choose "Other Software->Coremetrics->Coremetrics Web Analytics->IBM Coremetrics AdTarget Data Feed for Partners" 

5.7. click the link "Configure this service",  set the Quantity/Include to 1, then submit the configuration and provisioning id is null here

5.8. browse software as a service in the part and pricing tab, choose "Information Management->SPSS->Decision Management->SPSS Decision Management Software as a Service" 

5.9. click the link "Configure this service", click the "NO" in the dialog and  set the Quantity/Include to 1, then submit the configuration and provisioning id is null here

5.7. click the link "Edit provisioning form" to update the provisioning id for the IBM Coremetrics AdTarget Data Feed for Partners

5.8.click the link "Edit provisioning form" to update the provisioning id for the SPSS Decision Management Software as a Service

5.9. set the Sales information and save it as the draft quote

5.10. set the bid region, bid district and Justification Summary in the special bid and submit the quote

5.11. select the special bid approver and submit

5.12. find the quotes in the Status

5.13. select the quote you just submitted, and step into the part and pricing tab to check the result


reference LInk:
https://w3-connections.ibm.com/wikis/home?lang=zh-cn#!/wiki/We36a2acb76c7_4bce_b45c_7c104dbff999/page/the%20test%20steps%20for%20Updating%20provisioning%20id%20through%20submitting%20quote

*************************************************************************
* @date Dec 19, 2012
*/
public class SQOUpdtProvsnBySubmitingQuote extends BaseTestCase {
	/**
	 * log record
	 */
	public static WebdriverLogger loggerContxt = WebdriverLogger
			.getLogger(SQOUpdtProvsnBySubmitingQuote.class.getName());

	@Test(description = "SQO Update Provisioning By Submitting Quote")
	public void assemblyBizFlow() {
		reset(getProperty("fvt.sqo_url"));
		Connection dbConnection = null;
		try {
			dbConnection = getConnection();
		} catch (ClassNotFoundException e) {
			loggerContxt.error("Problem in connecting to database");
			e.printStackTrace();
		} catch (SQLException e) {
			loggerContxt.error("Problem in connecting to database");
			e.printStackTrace();
		}
		
		// One brand - one provisioning id - one configuration
		loggerContxt.info("Scenario: One brand - one provisioning id - one configuration");
		testOneBrandOneProvsnOneConfig(dbConnection);
		setUpForNewScenario();
		
		// One brand - one provisioning id - multiple configurations 
		// Scenario 1
		loggerContxt.info("Scenario: One brand - one provisioning id - multiple configurations");
		testOneBrandOneProvsnMultiConfig1(dbConnection);
		setUpForNewScenario();
	
		// One brand - one provisioning id - multiple configurations 
		// Scenario 2
		loggerContxt.info("Scenario: One brand - one provisioning id - multiple configurations");
		testOneBrandOneProvsnMultiConfig2(dbConnection);
		setUpForNewScenario();
		
		// Multiple brands - multiple provisioning id - multiple configurations
		// Scenario 1
		loggerContxt.info("Scenario: Multiple brands - multiple provisioning id - multiple configurations");
		testMultiBrandMultiProvsnMultiConfig1(dbConnection);
		setUpForNewScenario();
		
		// Multiple brands - multiple provisioning id - multiple configurations
		// Scenario 2
		loggerContxt.info("Scenario: Multiple brands - multiple provisioning id - multiple configurations");
		testMultiBrandMultiProvsnMultiConfig2(dbConnection); 
	}
	
	/**
	 * One brand - one provisioning id - one configuration Scenario
	 */
	private void testOneBrandOneProvsnOneConfig(Connection dbConnection){
		
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();

		SQOMyCurrentQuotePage currentQuotePage = commonScenarioBeforeProvision(quoteFlow);
		// Add SaaS part to quote
		SQOPartsAndPricingTabPage ppTab = quoteFlow
				.addSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add SaaS part to quote finished.....");

		// Edit provisioning form
		quoteFlow.editProvisioningForm(ppTab, SaasParts.SAAS_PART);
		loggerContxt.info("edit provisioning form finished.....");
		
		quoteFlow.verifyProvisionIdFromDatabase(dbConnection,SaasParts.SAAS_PART);
		loggerContxt.info("verify provisioning id from database finished ...");
		
		commonScenarioAfterProvision(quoteFlow,currentQuotePage, ppTab);
	}
	
	/**
	 * One brand - one provisioning id - multiple configurations in a same
	 * brand Scenario.
	 * First add and configure first part then edit
	 * provisioning. Add another part and then edit provisioning again.
	 */
	private void testOneBrandOneProvsnMultiConfig1(Connection dbConnection){
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();

		SQOMyCurrentQuotePage currentQuotePage = commonScenarioBeforeProvision(quoteFlow);

		// Add first SaaS part to quote
		SQOPartsAndPricingTabPage ppTab = quoteFlow
				.addBrandServiceOneSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add first SaaS part to quote finished.....");

		// Edit provisioning form
		quoteFlow.editProvisioningForm(ppTab,
				SaasParts.BRAND_SERVICE_ONE_SAAS_PART);
		loggerContxt.info("edit provisioning form finished.....");

		quoteFlow.verifyProvisionIdFromDatabase(dbConnection,SaasParts.BRAND_SERVICE_ONE_SAAS_PART);
		loggerContxt.info("verify provisioning id from database finished ...");
		
		// Add another SaaS part to quote of the same brand as the previous part
		ppTab = quoteFlow
				.addBrandServiceTwoSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add another SaaS part to quote finished.....");

		// Edit provisioning form
		quoteFlow.editProvisioningForm(ppTab,
				SaasParts.BRAND_SERVICE_TWO_SAAS_PART);
		loggerContxt.info("edit provisioning form finished.....");
		
		quoteFlow.verifyProvisionIdFromDatabase(dbConnection,SaasParts.BRAND_SERVICE_TWO_SAAS_PART);
		loggerContxt.info("verify provisioning id from database finished ...");

		commonScenarioAfterProvision(quoteFlow,currentQuotePage, ppTab);
	}
	
	/**
	 * One brand - one provisioning id - multiple configurations in a same
	 * brand Scenario 
	 * First add and configure the two parts then edit provisioning of both of them.
	 */
	
	void testOneBrandOneProvsnMultiConfig2(Connection dbConnection){
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();

		SQOMyCurrentQuotePage currentQuotePage = commonScenarioBeforeProvision(quoteFlow);
		
		// Add first SaaS part to quote
		SQOPartsAndPricingTabPage ppTab = quoteFlow
				.addBrandServiceOneSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add first SaaS part to quote finished.....");

		// Add another SaaS part to quote of the same brand as the previous part
		ppTab = quoteFlow
				.addBrandServiceTwoSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add another SaaS part to quote finished.....");

		// Edit provisioning form for the two saas parts
		quoteFlow.editProvisioningForm(ppTab,
				SaasParts.BRAND_SERVICE_ONE_SAAS_PART);
		loggerContxt.info("edit provisioning form finished.....");
		
		quoteFlow.verifyProvisionIdFromDatabase(dbConnection,SaasParts.BRAND_SERVICE_ONE_SAAS_PART);
		loggerContxt.info("verify provisioning id from database finished ...");

		commonScenarioAfterProvision(quoteFlow,currentQuotePage, ppTab);
	}
	
	/**
	 * Multiple brands - multiple provisioning id - multiple configurations
	 * from different brands 
	 * First add and configure the two parts then edit
	 * provisioning of both of them.
	 */
	void testMultiBrandMultiProvsnMultiConfig1(Connection dbConnection){
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();

		SQOMyCurrentQuotePage currentQuotePage = commonScenarioBeforeProvision(quoteFlow);
		
		// Add first SaaS part to quote
		SQOPartsAndPricingTabPage ppTab = quoteFlow
				.addSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add first SaaS part to quote finished.....");

		// Add another SaaS part to quote of a different brand from the previous part brand
		ppTab = quoteFlow
				.addOtherSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add another SaaS part to quote finished.....");

		// Edit provisioning form for the first saas part
		quoteFlow.editProvisioningForm(ppTab,
				SaasParts.SAAS_PART);
		loggerContxt.info("edit provisioning form finished.....");
		
		quoteFlow.verifyProvisionIdFromDatabase(dbConnection,SaasParts.SAAS_PART);
		loggerContxt.info("verify provisioning id from database finished ...");

		// Edit provisioning form for the second saas part
		quoteFlow.editProvisioningForm(ppTab,
				SaasParts.OTHER_SAAS_PART);
		loggerContxt.info("edit provisioning form finished.....");
		
		quoteFlow.verifyProvisionIdFromDatabase(dbConnection,SaasParts.OTHER_SAAS_PART);
		loggerContxt.info("verify provisioning id from database finished ...");

		commonScenarioAfterProvision(quoteFlow,currentQuotePage, ppTab);
		
	}
	/**
	 * Multiple brands - multiple provisioning id - multiple configurations
	 * from different brands 
	 * First add and configure the two parts then edit
	 * provisioning of both of them.
	 */
	private void testMultiBrandMultiProvsnMultiConfig2(Connection dbConnection){
		SQOQuoteCommonFlow quoteFlow = getCommonFlow();

		SQOMyCurrentQuotePage currentQuotePage = commonScenarioBeforeProvision(quoteFlow);

		// Add SaaS part to quote
		SQOPartsAndPricingTabPage ppTab = quoteFlow
				.addSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add SaaS part to quote finished.....");

		// Edit provisioning form
		quoteFlow.editProvisioningForm(ppTab, SaasParts.SAAS_PART);
		loggerContxt.info("edit provisioning form finished.....");
		
		quoteFlow.verifyProvisionIdFromDatabase(dbConnection,SaasParts.SAAS_PART);
		loggerContxt.info("verify provisioning id from database finished ...");
		
		// Add SaaS part to quote
		ppTab = quoteFlow
				.addBrandServiceOneSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add SaaS part to quote finished.....");
		
		// Add SaaS part to quote
		ppTab = quoteFlow
				.addOtherSaasPartToQuote(currentQuotePage);
		loggerContxt.info("add SaaS part to quote finished.....");
		
		// Edit provisioning form
		quoteFlow.editProvisioningForm(ppTab, SaasParts.BRAND_SERVICE_ONE_SAAS_PART);
		loggerContxt.info("edit provisioning form finished.....");
		
		quoteFlow.verifyProvisionIdFromDatabase(dbConnection,SaasParts.BRAND_SERVICE_ONE_SAAS_PART);
		loggerContxt.info("verify provisioning id from database finished ...");
		
		// Edit provisioning form
		quoteFlow.editProvisioningForm(ppTab, SaasParts.OTHER_SAAS_PART);
		loggerContxt.info("edit provisioning form finished.....");
		
		quoteFlow.verifyProvisionIdFromDatabase(dbConnection,SaasParts.OTHER_SAAS_PART);
		loggerContxt.info("verify provisioning id from database finished ...");
		
		commonScenarioAfterProvision(quoteFlow,currentQuotePage, ppTab);

		
	}
	
	private SQOMyCurrentQuotePage commonScenarioBeforeProvision(SQOQuoteCommonFlow quoteFlow)
	{
		// Login to SQO
		SQOHomePage sqoHomePage = quoteFlow.loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");

		// Go to create draft quote page
		SQOMyCurrentQuotePage currentQuotePage = quoteFlow
				.createDraftQuote(sqoHomePage);
		loggerContxt.info("create quote page finished.....");

		// Find an existing customer
		SQOMyCurrentQuotePage custAddedDraftQuotePage = quoteFlow
				.createCustomer(currentQuotePage);

		loggerContxt.info("find an existing customer finished.....");
		
		return custAddedDraftQuotePage;
	}
	
	private void commonScenarioAfterProvision(SQOQuoteCommonFlow quoteFlow,SQOMyCurrentQuotePage currentQuotePage, SQOPartsAndPricingTabPage ppTab)
	{
		// Edit Sales Info Tab
		quoteFlow.editSalesInfoTab(ppTab);
		loggerContxt.info("edit sales info tab finished.....");

		// Edit Special Bid Info
		quoteFlow.editSpcialBidInf(ppTab);
		loggerContxt.info("edit special bid info finished.....");

		// Submit Quote
		quoteFlow.runSubmitQuote(currentQuotePage);
		loggerContxt.info("Quote Submitted!");
		
		// find the quotes in the Status, select the quote you just submitted,
		// and step into the part and pricing tab to check the result
		quoteFlow.findQuoteByNum(currentQuotePage);
		loggerContxt.info("find quote by number finished.....");
	}
	
	private void setUpForNewScenario(){
		teardown();
		try {
			setUp();
		} catch (Exception e) {
			loggerContxt.info("Error in setup test scenario.");
			e.printStackTrace();
		}
		reset(getProperty("fvt.sqo_url"));
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SQOUpdtProvsnBySubmitingQuote test = new SQOUpdtProvsnBySubmitingQuote();
		test.setUp();
		test.assemblyBizFlow();
		test.teardown();
	}

}
