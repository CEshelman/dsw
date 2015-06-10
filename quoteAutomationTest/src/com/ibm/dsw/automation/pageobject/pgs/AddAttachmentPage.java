package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;

public class AddAttachmentPage extends PGSBasePage {

	public AddAttachmentPage(WebDriver driver) {
		super(driver);
	}
	
	
	public void uploadAttachment(String files) {
		String[] fileArray = files.split(",");
		for (String file : fileArray) {
			setValueById("justificationDocument1", file);
		}
	}
	
	public void submit() {
		
	}
}
