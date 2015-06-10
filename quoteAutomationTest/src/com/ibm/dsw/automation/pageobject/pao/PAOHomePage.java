package com.ibm.dsw.automation.pageobject.pao;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.ibm.dsw.automation.pageobject.pgs.PGSBasePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;


public class PAOHomePage extends PGSBasePage {

	// How how() default How.ID;
	// String using() default "";
	// String id() default "";
	// String name() default "";
	// String className() default "";
	// String css() default "";
	// String tagName() default "";
	// String linkText() default "";
	// String partialLinkText() default "";
	// String xpath() default "";

	
	public PAOHomePage(WebDriver driver) {
		super(driver);
	}

	@FindBy(linkText = "Partner guided selling")
	private WebElement pgsLink;

	public PGSHomePage gotoPGS() {
		pgsLink.click();
		PGSHomePage page = new PGSHomePage(driver);

		return page;

	}

}
