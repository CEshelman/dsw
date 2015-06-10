package com.ibm.dsw.automation.pageobject.sqo;

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;

public class SQOAttachSupportPage extends PGSBasePage {

	public SQOAttachSupportPage(WebDriver driver) {
		super(driver);
	}
	@FindBy(name="ibm-submit")
	private WebElement ibm_submit;
	
	//upload the file
	public void uploadFile(String file){
		boolean flag = switchToWindowAttach( driver, "Attach supporting files for quote");	
		if (!flag) {
			waitForElementLoading(5000L);
			switchToWindowAttach( driver, "Attach supporting files for quote");
		}
		driver.findElement(By.id("justificationDocument1")).sendKeys(file);
		loggerContxt.info("find input id justificationDocument1 and set value "+file);
		ibm_submit.click();
		WebElement temp = waitForElementByLocator(By.name("ibm-submit"), null);
		if (temp.isDisplayed()) {
			
			boolean flag0 = switchToWindow( driver, "IBM Software Quote and Order");
			if (!flag0) {
				waitForElementLoading(5000L);
				switchToWindow( driver, "IBM Software Quote and Order");
			}
			closePopupByTitle(null,"Attach supporting files for quote",driver.getWindowHandle());
		}
	}
	
	public boolean switchToWindowAttach(WebDriver driver, String windowTitle) {
		boolean flag = false;
		try {
			String currentHandle = getDriver().getWindowHandle();
			Set<String> handles = getDriver().getWindowHandles();
			for (String s : handles) {
				if (s.equals(currentHandle)) {
					continue;
				} else {
					getDriver().switchTo().window(s);
					if (getDriver().getTitle().contains(windowTitle)) {
						driver.switchTo().frame("hiddenTarget");
						flag = true;
						loggerContxt.info("Switch to window: " + windowTitle
								+ " successfully!");
						break;
					} else {
						getDriver().switchTo().window(currentHandle);
						continue;
					}
				}
			}
		} catch (NoSuchWindowException e) {
			loggerContxt.fatal(String.format("Failed to swith to window whose title contains:: ", windowTitle),e);
			flag = false;
		}
		return flag;
	}
}
