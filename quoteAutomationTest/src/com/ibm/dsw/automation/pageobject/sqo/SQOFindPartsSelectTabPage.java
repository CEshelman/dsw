package com.ibm.dsw.automation.pageobject.sqo;


import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.common.TestUtil;
import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQOFindPartsSelectTabPage extends PGSBasePage {		
	
	public SQOFindPartsSelectTabPage(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(linkText = "Return to draft quote")
	public WebElement  returnToMyCurrentQuoteLink;	
	
	@FindBy(linkText = "Processor Value Unit (PVU) calculator")
	public WebElement  pvuCalculotorLink;	
	
	@FindBy(linkText = "Change search criteria")
	public WebElement   changeSearchCriteriaLink;
	
	@FindBy(linkText = "Browse parts")
	public WebElement  browsePartsTabLink;
	
	@FindBy(linkText = "Browse software as a service")
	public WebElement  browseSfAsaServiceTabLink;
	
	@FindBy(linkText = "Select all displayed parts")
	public WebElement  selectAllDispPartsLink;
	
	//@FindBy(xpath = "//input[@alt='Add selected parts to draft quote.']")
	@FindBy(linkText="Add selected parts to draft quote")
	public WebElement  addSelectedParts2QuoteLink;
	
	@FindBy(linkText = "Collapse all")
	public WebElement  collapseAllLink;
	
	@FindBy(linkText = "Expand all")
	public WebElement  expandAllLink;
	
	@FindBy(id="0_0_0_0_0")
	public WebElement checkOffId;
	
	public SQOFindPartsTabPage selectPartsAndChgCriteriaClick(String partNums) {
		
		selectPartsClick(partNums);
		switchToAlert(true,this.getClass().getSimpleName());
		//WebElement changeSearchCriteriaLink=findElementByXPath(".//a[contains(@href,'quote.wss?jadeAction=DISPLAY_PARTSEARCH_FIND']");
		changeSearchCriteriaLink.click();
		SQOFindPartsTabPage page = new SQOFindPartsTabPage(this.driver);
		return page;
	}
	
	public void selectPartsClick(String partNumList) {
		
		//Wait till the page completes loading.  Then click the "Expand all".
		//Scroll to the bottom pf the page, Wait till the parts tree has been painted, 
		//click the "Select all displayed parts" link, then click the "Add selected parts to draft quote" link
		if (StringUtils.isBlank(partNumList)) {
			loggerContxt.error(String.format("A invalid part list information :: %s.",partNumList));
			return;
		}
		String partNum = partNumList.split(",")[0];
		
		if(!isTextPresent(TestUtil.getProperty(prop,"nopartsfoundmsg"))){
		waitForElementLoading(new Long(99000));
		anonymousResolutionButtonClick("imgALL");
		if(waitForElementByLocator(By.linkText(partNum), new Long(16*60*1000)).isDisplayed()){
			selectAllDispPartsLink.click();
			loggerContxt.info("click DispPartsLink");
			waitForElementLoading(new Long(5000));
			switchToAlert(true,this.getClass().getSimpleName());
			addSelectedParts2QuoteLink.click();
			loggerContxt.info("click Select all displayed parts Link");
			waitForElementLoading(new Long(35000));
			switchToAlert(true,this.getClass().getSimpleName());
		}
		//waitForElementLoading(new Long(16*60*1000));
		}
	}
	
	public void selectPartsSearch() {
		
		//Wait till the page completes loading.  Then click the "Expand all".
		//Scroll to the bottom pf the page, Wait till the parts tree has been painted, 
		//click the "Select all displayed parts" link, then click the "Add selected parts to draft quote" link
		if(!isTextPresent(TestUtil.getProperty(prop,"nopartsfoundmsg"))){
		waitForElementLoading(new Long(80000));
		anonymousResolutionButtonClick("imgALL");
		waitForElementLoading(new Long(60*1000));
		selectAllDispPartsLink.click();
		loggerContxt.info("click DispPartsLink");
		waitForElementLoading(new Long(5000));
		switchToAlert(true,this.getClass().getSimpleName());
		addSelectedParts2QuoteLink.click();
		loggerContxt.info("click Select all displayed parts Link");
		waitForElementLoading(new Long(35000));
		switchToAlert(true,this.getClass().getSimpleName());
		}
	}
	
	public void selectPartOfParts(){
		//click the "Select all displayed parts" link, then click the "Add selected parts to draft quote" link
		if(!isTextPresent(TestUtil.getProperty(prop,"nopartsfoundmsg"))){
		loggerContxt.info("Check off the first part number and click on 'add selected parts to draft quote' link");
		waitForElementLoading(new Long(5000));
		anonymousResolutionButtonClick("imgALL");
		waitForElementLoading(new Long(5000));
		selectAllDispPartsLink.click();
		loggerContxt.info("click DispPartsLink");
		waitForElementLoading(new Long(5000));
		checkOffId.click();
		addSelectedParts2QuoteLink.click();
		loggerContxt.info("Check off the first part number and click on 'add selected parts to draft quote' link--->done!");
		loggerContxt.info("Click on 'Return to draft quote' link");
		returnToMyCurrentQuoteLink.click();
		loggerContxt.info("Click on 'Return to draft quote' link--->done!");
		
		}
	}
	
	
	public void selectAllDispPartsLink(){
		selectAllDispPartsLink.click();
	}
	
	public void clickSelectedParts2QuoteLink(){
		addSelectedParts2QuoteLink.click();
	}
	
	public void clickCollapseAllLink(){
		collapseAllLink.click();
	}
	
	public void clickExpandAllLink(){
		expandAllLink.click();
	}
	
	public SQOPartsAndPricingTabPage rtn2DraftQuote() {
		returnToMyCurrentQuoteLink.click();
		
		loggerContxt.info("click  return to Draft Quote ");
		waitForElementLoading(8000L);
		return new SQOPartsAndPricingTabPage(this.driver);
	}
	
	public SQOMonthySwBaseConfiguratorPage go2MonthlySwBaseConfig() {
		returnToMyCurrentQuoteLink.click();
		waitForElementLoading(8000L);
		return new SQOMonthySwBaseConfiguratorPage(this.driver);
	}
}
