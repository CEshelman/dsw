package com.ibm.dsw.automation.pageobject.pgs;

import org.openqa.selenium.WebDriver;

public class PGSApprovalTabPage extends PGSBasePage {

	private String lastWinHandle;
	
	public PGSApprovalTabPage(WebDriver driver) {
		super(driver);
	}
	
	public void enterApprovalInfo(String spBidRgn, String spBidDist) {
		selectedOptionByValue("spBidRgn", spBidRgn, driver);
		selectedOptionByValue("spBidDist", spBidDist, driver);
		
	}

	public String getLastWinHandle() {
		return lastWinHandle;
	}

	public void setLastWinHandle(String lastWinHandle) {
		this.lastWinHandle = lastWinHandle;
	}

	public void enterSpcialBidInf(String justification) {
		String js = "var allInstances = CKEDITOR.instances, editor, doc; for ( var i in allInstances ){	editor = allInstances[i]; editor.setData('"
				+ justification + "'); }";
		
		this.executeJavaScript(js);
	}
	
	public AddAttachmentPage goToAddAttachmentPage() {
		lastWinHandle = getDriver().getWindowHandle();
		elementClickByLinkText("Add Attachment");
		switchToWindow(getDriver(), "");
		return new AddAttachmentPage(getDriver());
	}
}
