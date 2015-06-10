package com.ibm.dsw.automation.pageobject.sqo;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;

public class SQOApproveQueuePage extends BasePage
{
    private static final String partialHref = "//a[@href='quote.wss?jadeAction=DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB&quoteNum=quotenum']";

    public SQOApproveQueuePage(WebDriver driver)
    {
        super(driver);
    }
    
    //My personal approval queue (bids awaiting approval by me) 
    @FindBy(id="queueType1")
    public WebElement queueType1;
    //Save selections as my default  
    @FindBy(id="markFilterDefault")
    public WebElement markFilterDefault;
    //Update view per my selections
    @FindBy(linkText="Update view per my selections")
    public WebElement updateView;

    public SQOSubmitSQSpecialBidTabPage findQuoteByNum(String quoteNum)
    {
    	// check radio button "Special bid pipeline (all bids visible to me that are pending approval)"
    	findElementById("queueType0").click();
    	
    	// update view after checking radio button
    	findElementByLinkText("Update view per my selections").click();
    	
    	// wait to make sure that the page has been loaded with new search criteria.
    	waitForElementById("queueType0");
    	
        WebElement viewDetailLink = driver.findElement(By.xpath(partialHref.replace("quotenum", quoteNum)));
        viewDetailLink.click();
//        checkPageAvailable(getClass());
        SQOSubmitSQSpecialBidTabPage page = new SQOSubmitSQSpecialBidTabPage(driver);
        loadPage(page, this.WAIT_TIME);
        return page;
    }
    
    /**
     * Update view per my selections
     * @param radio
     * 0 - Special bid pipeline (all bids visible to me that are pending approval) 
     * 1 - My personal approval queue (bids awaiting approval by me) 
     * 2 - My approval group's queue (bids awaiting approval by a member of my approval group) 
     * 3 - My approval type's queue (bids awaiting approval by a member of an approval group with my approval type) 
     */
    public SQOApproveQueuePage updateViewPerSelection(int radio){
    	List<WebElement> approvalQueueRadio = driver.findElements(By.name("queueType"));
    	if (radio > approvalQueueRadio.size()) {
			loggerContxt.error(String.format("The input radio::%d is out of radio count bound..",radio));
			return this;
		}
    	approvalQueueRadio.get(radio).click();
    	if (!markFilterDefault.isSelected()) {
    		markFilterDefault.click();
		}
    	updateView.click();
    	SQOApproveQueuePage page = new SQOApproveQueuePage(driver);
        loadPage(page,this.WAIT_TIME);
        return page;
    }
}
