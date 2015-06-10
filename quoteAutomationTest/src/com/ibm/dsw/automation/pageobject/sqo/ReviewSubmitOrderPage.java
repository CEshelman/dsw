package com.ibm.dsw.automation.pageobject.sqo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ibm.dsw.automation.pageobject.BasePage;

public class ReviewSubmitOrderPage extends BasePage {
	
	public WebElement  payment_number_purchase_order;

	
	public WebElement submitButton;
	
	
	public ReviewSubmitOrderPage(WebDriver driver) {
		super(driver);
	}

	
	
	public OrderConfirmationPage  gotoConfirmation(String invoice ) {
		//clear
		payment_number_purchase_order.clear();
	
		payment_number_purchase_order.sendKeys(invoice);
		submitButton.submit();
		OrderConfirmationPage page = new OrderConfirmationPage(driver);
		loadPage(page, this.WAIT_TIME);
		return page;
	}
	
	
	
}
