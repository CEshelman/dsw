package com.ibm.dsw.automation.pageobject.sqo;

import java.util.Properties;

import org.openqa.selenium.WebDriver;

import com.ibm.dsw.automation.pageobject.BasePage;

public class SQORestServicePage extends BasePage
{
	public SQORestServicePage(WebDriver driver) {
		super(driver);
	}

	/**
	 * check UserLookupRest is disable.
	 * String 
	 * @param prop
	 * @return
	 */
	public String loginUserLookupRestPage(Properties prop, String env) {
		loggerContxt.info("login in User Look for rest");
		StringBuffer sb = new StringBuffer();
		boolean Displayed = false;
		String pageSource = "";
		try {
			getDriver().get(prop.getProperty(env + ".use_look_up1_url"));
			pageSource = getDriver().getPageSource();
			Displayed = (pageSource.indexOf("ResultStatus") != -1);
			if (!Displayed)
				sb.append(prop.getProperty(env + ".use_look_up1_url") + " is Exception, \n");

			getDriver().get(prop.getProperty(env + ".use_look_up2_url"));
			pageSource = getDriver().getPageSource();
			Displayed = (pageSource.indexOf("ResultStatus") != -1);
			if (!Displayed)
				sb.append(prop.getProperty(env + ".use_look_up2_url") + " is Exception, \n");

			getDriver().get(prop.getProperty(env + ".use_look_up5_url"));
			pageSource = getDriver().getPageSource();
			Displayed = (pageSource.indexOf("ResultStatus") != -1);
			if (!Displayed)
				sb.append(prop.getProperty(env + ".use_look_up5_url") + " is Exception, \n");

			getDriver().get(prop.getProperty(env + ".use_look_up6_url"));
			pageSource = getDriver().getPageSource();
			Displayed = (pageSource.indexOf("ResultStatus") != -1);
			if (!Displayed)
				sb.append(prop.getProperty(env + ".use_look_up6_url") + " is Exception, \n");
		} catch (Exception e) {

			loggerContxt.error(e.getMessage());
		}

		return sb.toString();
	}

	/**
	 * check UserLookupRest is disable. String
	 * 
	 * @param prop
	 * @return
	 */
	public String loginAddonTrapupRestPage(Properties prop, String env) {
		loggerContxt.info("login in Add on Trap up for rest");
		StringBuffer sb = new StringBuffer();
		boolean Displayed = false;
		String pageSource = "";
		try {
			getDriver().get(prop.getProperty(env + ".addon_trapup1_url"));
			pageSource = getDriver().getPageSource();
			Displayed = (pageSource.indexOf("resultCode") != -1);
			if (!Displayed)
				sb.append(prop.getProperty(env + ".addon_trapup1_url") + " is Exception, \n");

			getDriver().get(prop.getProperty(env + ".addon_trapup2_url"));
			pageSource = getDriver().getPageSource();
			Displayed = (pageSource.indexOf("resultCode") != -1);
			if (!Displayed)
				sb.append(prop.getProperty(env + ".addon_trapup2_url") + " is Exception, \n");

			getDriver().get(prop.getProperty(env + ".addon_trapup5_url"));
			pageSource = getDriver().getPageSource();
			Displayed = (pageSource.indexOf("resultCode") != -1);
			if (!Displayed)
				sb.append(prop.getProperty(env + ".addon_trapup5_url") + " is Exception, \n");

			getDriver().get(prop.getProperty(env + ".addon_trapup6_url"));
			pageSource = getDriver().getPageSource();
			Displayed = (pageSource.indexOf("resultCode") != -1);
			if (!Displayed)
				sb.append(prop.getProperty(env + ".addon_trapup6_url") + " is Exception, \n");
		} catch (Exception e) {

			loggerContxt.error(e.getMessage());
		}

		return sb.toString();
	}
	

	 

	public static void main(String agrs[]) {
	
	}
    

}
