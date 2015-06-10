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

public class SQOBrowseSoftwareAsServiceTabPage extends BrowseSoftwareAsServiceTabPage {		
	
	@FindBy(id = "addConfigrtnButton")
	private WebElement  addConfigrtnButton;
	
	@FindBy(id = "cancellConfigrtnButton")
	private WebElement  cancellConfigrtnButton;
	
	

	@FindBy(xpath = "//input[contains(@id, 'qtySuffix')][1]")
	private WebElement  qtyElement;
	
	public Logger loggerContxt = Logger.getLogger(this.getClass().getName());
	
	public SQOBrowseSoftwareAsServiceTabPage(WebDriver driver) {
		super(driver);
	}
	
	
	protected void clickAddConfigrtnButton(){
		addConfigrtnButton.click();
	}
	protected void clickCancellConfigrtnButton(){
		cancellConfigrtnButton.click();
	}
	public void clickTreeNodeById(String nodeId){
		elementClickById(nodeId);
	}
	
	public void clickTreeNodesById(String[] nodeIds){
		for(String nodeId : nodeIds)
			clickTreeNodeById(nodeId);
	}
	
	public void configureService(String configureId,String[][] components){
		executeJavaScript("configureService('" + configureId + "')");
		
		// if Confirmation dialog displayed
		if(	isTextPresent(prop.getProperty("SQOBrowseSaaSConfirmationDialog"))){
			WebElement webElement = findElementById("dialog_dojoConfirmYesNoButton2IdAddNewConfiguration");
	
	        if (webElement != null) {
	
	        	loggerContxt.info("pop up a alert");
	        	((JavascriptExecutor) driver).executeScript("ibmweb.overlay.hide('dojoConfirmYesNoButton2IdAddNewConfiguration');dojoDSWNoButtonFunc();");
	        	loggerContxt.info("click No button by executing the js");
	            waitForElementLoading(10000L);
	        }
		}
		
		WebElement webElement =  waitForElementById("dijit_DialogUnderlay_0");
		if(webElement != null){
			//driver.findElement(By.xpath("/html/body/div[6]/div[2]/div/div[2]/div[2]/span/span/span/span[3]")).click();//gts confirm
			addSaasPart(components);
			//returnToDraftQuoteLinkClick();
		}
		
	}
	
    public void addSaasPart(String[][] components) {
//		if(waitForElementById("cpqIframeId1") != null){
//			loggerContxt.info("cpqIframeId1 is existed");
			driver.switchTo().frame("cpqIframeId1"); //switch to popup iframe
			loggerContxt.info("switchTo cpqIframeId1 is done");
			for(String[] component  : components) {
				loggerContxt.info("component[0] =:"+component[0]);
				loggerContxt.info("component[1] =:"+component[1]);
				setValueByXPath(component[0], component[1]);}
			clickAddConfigrtnButton();	
//		}
	}

    public void modifyPart(String[][] components) {
    	loggerContxt.info("entering modify SaaS Part");
    	if(waitForElementById("dijit_Dialog_0") != null){
    		loggerContxt.info("dijit_Dialog_0 is existed");
			driver.switchTo().frame("dijit_Dialog_0"); 
			for(String[] component  : components) {
				loggerContxt.info("dijit_Dialog_0:  "+component[0] + "   %%%%   " + component[1]);
				setValueByXPath(component[0], component[1]);
			}
			clickAddConfigrtnButton();	
    	}
    }
    
    
	@Override
	public void configureService(String nodePath) {
		getDriver().findElement(By.xpath(nodePath)).click();
		waitForElementLoading(new Long(1000));
	}
	
	
	   public void fillSaaspartQty(String cnt) {

				driver.switchTo().frame("cpqIframeId1"); //switch to popup iframe
				loggerContxt.info("switchTo cpqIframeId1 is done");
				qtyElement.sendKeys(cnt);
				
				//
				partTermsFreqtRelated();
				clickAddConfigrtnButton();	
		}
	
	   public void browserAndConfigureService(String partAltDes,String configureId) { 
		   
		   if (StringUtils.isNotBlank(partAltDes)){
			String[] altArray = partAltDes.split(",");
			for (String altDes : altArray) {
				configureService("//input[@alt='"+altDes+"']");
			}
			
			((JavascriptExecutor) driver).executeScript("configureService('"
					+ configureId + "')");
			
			// if Confirmation dialog displayed
			if(	isTextPresent(prop.getProperty("SQOBrowseSaaSConfirmationDialog"))){
				WebElement webElement = findElementById("dialog_dojoConfirmYesNoButton2IdAddNewConfiguration");
		
		        if (webElement != null) {
		
		        	loggerContxt.info("pop up a alert");
		        	((JavascriptExecutor) driver).executeScript("ibmweb.overlay.hide('dojoConfirmYesNoButton2IdAddNewConfiguration');dojoDSWNoButtonFunc();");
		        	loggerContxt.info("click No button by executing the js");
		            waitForElementLoading(25000L);
		        }
			}
		   }
		}
	   
	@Override
	public void browserAndConfigureService(String configureId) { 
		   
		   clickAllImgs();
			
			((JavascriptExecutor) driver).executeScript("configureService('"
					+ configureId + "')");
			
			// if Confirmation dialog displayed
			if(	isTextPresent(prop.getProperty("SQOBrowseSaaSConfirmationDialog"))){
				WebElement webElement = findElementById("dialog_dojoConfirmYesNoButton2IdAddNewConfiguration");
		
		        if (webElement != null) {
		
		        	loggerContxt.info("pop up a alert");
		        	((JavascriptExecutor) driver).executeScript("ibmweb.overlay.hide('dojoConfirmYesNoButton2IdAddNewConfiguration');dojoDSWNoButtonFunc();");
		        	loggerContxt.info("click No button by executing the js");
		            waitForElementLoading(25000L);
		        }
			}
		}
	
	
    public void editSaasPart(String cnt) {
    	
		WebElement webElement = waitForElementById("dijit_DialogUnderlay_0");
		if (webElement != null) {
	
			fillSaaspartQty(cnt);
			loggerContxt.info("add SAAS part finished.....");
			waitForElementLoading(25000L);
		}
    }

	@Override
	public void selectAgreement() {
		selectedOptionByValue("serviceAgreement", "-2", getDriver());
	}
	
	public void partTermsFreqtRelated() {
		try{
		//TODO 
		selectedOptionByValue(".//select[contains(@id,'_billingFrequencySuffix')]", "Q", driver);
		selectedOptionByValue(".//select[contains(@id,'_rampUpPeriodSuffix')]", "2", driver);
		
		
		elementClickByXPath("//a[contains(@onclick,'jadeAction=CONFIG_HOSTED_SERVICE')]");
		waitForElementLoading(50L);
		
		sendKeys(findElementByXPath(".//input[contains(@id,'_0_rampUpQtySuffix') and @type='text']"), "1");
		sendKeys(findElementByXPath(".//input[contains(@id,'_0_rampUpDurationSuffix') and @type='text']"), "2");
		
		sendKeys(findElementByXPath(".//input[contains(@id,'_1_rampUpQtySuffix') and @type='text']"), "2");
		sendKeys(findElementByXPath(".//input[contains(@id,'_1_rampUpDurationSuffix') and @type='text']"), "3");
		
		}catch (NoSuchElementException ex) {
			loggerContxt.info("NoSuchElementException....."+ex.getMessage());
		}finally{
			
		}
	}
	
}
