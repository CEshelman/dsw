package org.openqa.selenium.example;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

public class BaiduPage {
	// The element is now looked up using the name attribute,
	// and we never look it up once it has been used the first time
	/*
	 * java.lang.String className 
	 * java.lang.String css 
	 * How how 
	 * java.lang.String id 
	 * java.lang.String linkText 
	 * java.lang.String name 
	 * java.lang.String partialLinkText 
	 * java.lang.String tagName 
	 * java.lang.String using
	 * java.lang.String xpath
	 */

	// @FindBy(name = "wd")
	 @FindBy(id = "kw")
	//@FindBy(className = "s_ipt")
	@CacheLookup
	private WebElement searchBox;

	private WebElement kw;

	public void searchFor(String text) {
		// We continue using the element just as before
		searchBox.sendKeys(text);
		searchBox.submit();

		// kw.sendKeys(text);
		// kw.submit();
	}
	
	
	//@FindBy(linkText = "cheese_百度百科")
	//@CacheLookup
	private WebElement link;

	public void linkTo() {
		link.click();
	}
}