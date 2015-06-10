package org.openqa.selenium.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class BaiduPageTest {
	@Parameters({ "browser", "url", "keyword" })
	@Test(description = "Start up browser")
	public void run(String browser, String url, String keyword) {
		// Create a new instance of a driver
		WebDriver driver = null;
		System.out.println("browser is " + browser);
		if (browser.equals("firefox")) {
			driver = new FirefoxDriver();
		} else {
			driver = new HtmlUnitDriver();
		}
        // Navigate to the right place
		driver.get(url);

		// Create a new instance of the search page class
		// and initialise any WebElement fields in it.
		BaiduPage searchPage = PageFactory.initElements(driver, BaiduPage.class);

        // And now do the search.
		searchPage.searchFor(keyword);
		
		searchPage.linkTo();
		
//		BaiduResultPage resultPage = PageFactory.initElements(driver, BaiduResultPage.class);
//
//        // And now do the search.
//		resultPage.linkTo();
		
		
	}

	public static void main(String[] args) {
		BaiduPageTest us = new BaiduPageTest();
		us.run("firefox", "http://www.baidu.com/", "Cheese");
		// try {
		// String host = InetAddress.getLocalHost().getHostName();
		// System.out.println(host);
		// } catch (UnknownHostException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
}