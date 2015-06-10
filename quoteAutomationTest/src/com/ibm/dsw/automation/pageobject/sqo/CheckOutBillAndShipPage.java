package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.BasePage;
import com.ibm.dsw.automation.vo.CheckoutInf;

public class CheckOutBillAndShipPage extends BasePage {
	
	public WebElement  userID;
	public WebElement  userEmail;

	public WebElement  submit_first_name;
	public WebElement  submit_last_name;
	public WebElement  submit_email;
	public WebElement  submit_phone;
	
	@FindBy(xpath = "//input[@name='P0=S28']")
	public WebElement continue_btn;
	
	
	public CheckOutBillAndShipPage(WebDriver driver) {
		super(driver);
	}

	
	
	public ReviewSubmitOrderPage  submitOrder(CheckoutInf dto) {
		//clear
		userID.clear();
		userEmail.clear();
		submit_first_name.clear();
		submit_last_name.clear();
		submit_email.clear();
		submit_phone.clear();
		
		userID.sendKeys(dto.getUserID());
		userEmail.sendKeys(dto.getUserEmail());
		submit_first_name.sendKeys(dto.getSubmit_first_name());
		submit_last_name.sendKeys(dto.getSubmit_last_name());
		submit_email.sendKeys(dto.getSubmit_email());
		submit_phone.sendKeys(dto.getSubmit_phone());
		selectedOptionByValue("submit_dist_chnl", "Z", driver);
		continue_btn.click();
		ReviewSubmitOrderPage page = new ReviewSubmitOrderPage(driver);
		loadPage(page, this.WAIT_TIME);
		
		return page;
	}
	
	
	
}
