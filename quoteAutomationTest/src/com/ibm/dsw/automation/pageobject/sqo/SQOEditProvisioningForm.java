package com.ibm.dsw.automation.pageobject.sqo;

import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQOEditProvisioningForm extends PGSBasePage {
	
	@FindBy(linkText = "Return to sales quote")
	public WebElement  returnToQuoteLink;
	
	@FindBy(id = "continueFormButton")
	public WebElement continueButton;
	
	@FindBy(id = "submitFormButton")
	public WebElement submitButton;

	public SQOEditProvisioningForm(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
	public void returnToQuoteLinkClick(){
		returnToQuoteLink.click();
		waitForElementLoading(new Long(10000));
	}
	
	public void continueButtonClick(){
		continueButton.click();
		waitForElementLoading(new Long(10000));
	}
	
	public void submitButtonClick(){
		submitButton.click();
		waitForElementLoading(new Long(10000));
	}
	
	public void populateProvisionForm(String provisionData){
		
		// parsing data from the script properties file
		String[] inputsArray = provisionData.split(",");
    	WebElement webElement = null;
    	LinkedList<String> textboxs = new LinkedList<String>();
    	LinkedList<String> radiobtns = new LinkedList<String>();
    	LinkedList<String> textareas = new LinkedList<String>();
    	
		for (int i = 1 ; i <= inputsArray.length;i++) {
			
			int start = inputsArray[i-1].indexOf(":")+1;
			int end = inputsArray[i-1].indexOf(")");
			String data = inputsArray[i-1].substring(start, end);
			
			if(inputsArray[i-1].contains("textbox:"))
			{
				textboxs.add(data);
			}
			else if(inputsArray[i-1].contains("radio:")){
				radiobtns.add(data);
			}
			else if(inputsArray[i-1].contains("textarea:")){
				textareas.add(data);
			}
			
		}
		
		// populate text boxes
		for(int i = 1 ; i <= textboxs.size()-3; i++){
				
			String xpath = "(//input[contains(@class,'controlText')])["+i+"]";
			webElement = findElementByXPath(xpath);
			webElement.sendKeys(textboxs.get(i-1));
		}
		
		List<WebElement> allRadioBtn = getDriver().findElements(By.xpath(".//input[@class = 'controlOption']"));
		
		
		// populate radio buttons
		int j = 0;
		for(int i = 0 ; i < radiobtns.size(); i++){
			
			for(; j < allRadioBtn.size(); j++){
			
				String text = (allRadioBtn.get(j)).getAttribute("value");
				if(text.equals(radiobtns.get(i))){
					allRadioBtn.get(j).click();
					break;
				}
			}
		}
		
		
		// populate text areas
		for(int i = 1 ; i <= textareas.size(); i++){
			String xpath = "(//textarea[contains(@class,'controlTextArea')])["+i+"]";
			webElement = findElementByXPath(xpath);
			webElement.sendKeys(textareas.get(i-1));
		}
	}
}
