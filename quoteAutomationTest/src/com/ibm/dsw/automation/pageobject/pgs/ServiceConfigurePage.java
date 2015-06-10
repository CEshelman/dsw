package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;


/**
 * 
 * @author suchuang
 * @date Dec 28, 2012
 */
public class ServiceConfigurePage extends PGSBasePage {

	private String configurationID;
	
	public String getConfigurationID() {
		return configurationID;
	}

	public void setConfigurationID(String configurationID) {
		this.configurationID = configurationID;
	}

	/**
	 * 
	 * @author suchuang
	 * @date Dec 28, 2012
	 * @param driver
	 */
	public ServiceConfigurePage(WebDriver driver) {
		super(driver);
	}

	/**
	 * 
	 * @author suchuang
	 * @date Dec 28, 2012
	 * @param strElmId
	 * @param strQuantity
	 */
	public void enterQuantity(String strElmId, String strQuantity) {
		setValueByXPath(strElmId, strQuantity);
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Dec 28, 2012
	 * @return
	 */
	public void submit() {
//		elementClickById("addConfigrtnButton");
		elementClickByName("jadeAction=ADD_OR_UPDATE_CONFIGRTN");
		waitForElementLoading(new Long(60000));
		checkPageAvailable(getClass());
		//PartsAndPricingTabPage page = new PartsAndPricingTabPage(driver);
		//return page;
	}
	
	@FindBy(xpath = "//input[contains(@id, 'qtySuffix')][1]")
	private WebElement  qtyElement;
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 9, 2013
	 * @return
	 */
	public void configureService() {
		qtyElement.sendKeys("1");
//		String eltXPath = "/html/body/div/div[2]/div/div/div[2]/form/table/tbody/tr[7]/td/div/div/div/table/tbody/tr/td/input";
//		String eltXPath = "//input[contains(@id, 'qtySuffix')][0]";//
//		isElementExistByXPath(eltXPath);
//		enterQuantity(eltXPath, "1");
		submit();
	}
	
	public SQOPartsAndPricingTabPage goToPartsAndPricingTab() {
		SQOPartsAndPricingTabPage ppTab = new SQOPartsAndPricingTabPage(getDriver());
		return ppTab;
	}
}
