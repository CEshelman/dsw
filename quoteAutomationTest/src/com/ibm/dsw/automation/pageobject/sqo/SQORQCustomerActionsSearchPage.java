package com.ibm.dsw.automation.pageobject.sqo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;
import com.ibm.dsw.automation.util.DateUtil;

public class SQORQCustomerActionsSearchPage extends BasePage{
	
	public SQORQCustomerActionsSearchPage(WebDriver driver) {
		super(driver);
	}
	
	@FindBy(id="SALES_REP")
	private WebElement salesRepInput;
	@FindBy(id="ACTION_TYPE")
	private WebElement customerActionType;
	@FindBy(className="ibm-btn-arrow-pri")
	private WebElement submitButton;
	@FindBy(linkText="Change search criteria")
	private WebElement changeCriteriaLink;
	
	public void selectRequestDate(){
		Date now = new Date();
		int day = DateUtil.getDay(now);
		if (day>2) {
			day -= 1;
		}
		int month = DateUtil.getMonth(now);
		if (month>12) {
			month -= 10;
		}
		int year = DateUtil.getYear(now);
		
		this.selectOptionByVisibleText("REQ_FROM_DAY", String.valueOf(day));
		this.selectedOptionByIndex(By.id("REQ_FROM_MONTH"), month);
		this.selectOptionByVisibleText("REQ_FROM_YEAR", String.valueOf(year-1));
		
		this.selectOptionByVisibleText("REQ_TO_DAY", String.valueOf(day));
		this.selectedOptionByIndex(By.id("REQ_TO_MONTH"), month);
		this.selectOptionByVisibleText("REQ_TO_YEAR", String.valueOf(year));
	}
	
	public void fillsalesRepInput(String value){
		salesRepInput.clear();
		salesRepInput.sendKeys(value);
	}
	
	public void submitCustomerActionsSearch(){
		submitButton.click();
	}
	
	public void findRQCustomerActions(String customerType, String salesRep){
		if (!StringUtils.isBlank(customerType)) {
			this.selectOptionByVisibleText(customerActionType, customerType);
		}
		if (!StringUtils.isBlank(salesRep)) {
			this.fillsalesRepInput(salesRep);
		}
		this.selectRequestDate();
		this.submitCustomerActionsSearch();
	}
	
	public SQORQCustomerActionsSearchPage changeCriteria(){
		changeCriteriaLink.click();
		return newCache();
		
	}
	
	public SQORQCustomerActionsSearchPage newCache(){
		SQORQCustomerActionsSearchPage page = new SQORQCustomerActionsSearchPage(driver);
		this.loadPage(page, WAIT_TIME);
		return page;
	}
}
