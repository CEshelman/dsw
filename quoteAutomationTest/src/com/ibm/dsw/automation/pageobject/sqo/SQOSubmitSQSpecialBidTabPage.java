package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SQOSubmitSQSpecialBidTabPage extends SQOSpecialBidTabPage
{
    
    public SQOSubmitSQSpecialBidTabPage(WebDriver driver)
    {
        super(driver);
    }
    
    public void submitApproveResult()
    {
        WebElement approveLink = driver.findElement(By.id("apprvrAction"));
        approveLink.click();
        approveLink.submit();
    }
    
    public void submitRejectResult()
    {
        WebElement rejectLink = driver.findElement(By.id("apprActReject"));
        rejectLink.click();
        rejectLink.submit();
    }
}
