package org.openqa.selenium.example;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class UsingGoogleSearchPage {
	@Parameters({ "browser", "url", "keyword" })
	@Test(description = "Start up browser")
	public void run(String browser, String url, String keyword) {
		// Create a new instance of a driver
		WebDriver driver = null;
		System.out.println("browser is " + browser);
		
		if (browser.equals("firefox")) {
			FirefoxProfile profile = new FirefoxProfile();
			driver = new FirefoxDriver(new FirefoxBinary(new File("C:/Program Files/Mozilla Firefox/firefox.exe")),profile);
		}
		else if(browser.equals("chrome"))
			driver = new ChromeDriver();
		else {
			driver = new HtmlUnitDriver();
		}
        // Navigate to the right place
		driver.get(url);

		// Create a new instance of the search page class
		// and initialise any WebElement fields in it.
		GoogleSearchPage page = PageFactory.initElements(driver, GoogleSearchPage.class);

        // And now do the search.
		page.searchFor(keyword);
	}

	public static void main(String[] args) {
		UsingGoogleSearchPage us = new UsingGoogleSearchPage();
		us.run("firefox", "http://www.baidu.com/", "Cheese");
	}
}