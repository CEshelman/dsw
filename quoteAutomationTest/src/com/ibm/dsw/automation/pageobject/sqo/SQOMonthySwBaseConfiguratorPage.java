package com.ibm.dsw.automation.pageobject.sqo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.BrowseSoftwareAsServiceTabPage;

@SuppressWarnings("unused")
public class SQOMonthySwBaseConfiguratorPage extends BrowseSoftwareAsServiceTabPage {		
	
	@FindBy(id = "addConfigrtnButton")
	private WebElement  addConfigrtnButton;
	
	@FindBy(id = "cancellConfigrtnButton")
	private WebElement  cancellConfigrtnButton;
	
	@FindBy(id = "ID_D12ZMLL_2_qtySuffix")
	private WebElement  ID_D12ZMLL_2_qtySuffix;
	
	
	
	public Logger loggerContxt = Logger.getLogger(this.getClass().getName());
	
	public SQOMonthySwBaseConfiguratorPage(WebDriver driver) {
		super(driver);
	}
	
	/**
	 * set quantity<br>
	 * 
	 */
	public void settingMonthySwQantity(String partNum,String qantity) {
		WebElement eltPartNumText =findElementByXPath(".//input[contains(@id,'ID_"+partNum+"') and contains(@id,'qtySuffix')]");
		//ID_D12ZMLL_2_qtySuffix
		//WebElement eltPartNumText =findElementByXPath(".//input[contains(@id,'ID_D12ZMLL_2_qtySuffix')]");
		sendKeys(eltPartNumText,qantity);
		//addConfigrtnButton.click();
		
	}
	
	/**
	 * Validate quantity message<br>
	 * 
	 */
	public void validateMonthySwQantity() {
		assertTextPresentTrue(prop.getProperty("quantity_warning"));
	}
	/**
	 * Validate quantity message<br>
	 * 
	 */
	public void validateMonthySwTerms() {
		assertTextPresentTrue(prop.getProperty("billing_term_mismatch"));
	}
	
	/**
	 * set quantity<br>
	 * 
	 */
	public void settingTermsBilling(String partNum,String term,String billing) {
		
		
		WebElement billingSelect =findPartRegexSelectByID(partNum,"billingFrequencySuffix");
		selectedOptionByValue(billingSelect, billing, driver);
		WebElement termSelect =findPartRegexSelectByID(partNum,"termSuffix");
		selectedOptionByValue(termSelect, term, driver);
		addConfigrtnButton.click();
		
	}
	
	/**
	 * set quantity<br>
	 * 
	 */
	public void validationTermsBilling() {
		assertTextPresentTrue(prop.getProperty("billing_term_mismatch"));
		
	}
	
	/**
	 * set quantity<br>
	 * 
	 */
	public SQOMonthySwBaseConfiguratorPage submitForm() {
		
		addConfigrtnButton.click();
		return new SQOMonthySwBaseConfiguratorPage(driver);
		
	}
	
	  public WebElement findPartRegexSelectByID(String partId,String selectRegex) {
	        WebElement element = null;
	        try {
	            element = driver.findElement(By.xpath("//select[contains(@id, '" + partId
	                    + "') and contains(@id, '"+selectRegex+"')]"));
	        } catch (NoSuchElementException e) {
	            e.printStackTrace();
	        }
	        return element;
	    }
		public void uncheckMonthlyPart(String partNum) {
			
			WebElement eltPartCheck =findElementByXPath(".//input[contains(@id,'ID_"+partNum+"') and contains(@id,'qtySuffix') and @type='checkbox']");
			eltPartCheck.click();
			
		}
	
	
	
}
