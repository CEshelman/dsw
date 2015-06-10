package com.ibm.dsw.automation.pageobject.pgs;

import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class BrowseSoftwareAsServiceTabPage extends PGSBasePage {
	
	public BrowseSoftwareAsServiceTabPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(linkText = "Return to draft quote")
	public WebElement  returnToDraftQuoteLink;

	@FindBy(linkText = "Browse parts")
	public WebElement  browsePartsTabLink;

	@FindBy(linkText = "Find parts")
	public WebElement  findPartsTabLink;

	@FindBy(linkText = "Browse software as a service")
	public WebElement  browseSoftwareAsAServiceTabLink;

	public WebElement  serviceAgreementid;
	
	private WebElement termInput;
	
	@FindBy(xpath="//a[@title='CONFIGURE THIS SERVICE']")
	private List<WebElement> configreServiceLinks;
	@FindBy(id="addConfigrtnButton")
	private WebElement addConfigrtnButton;

	/**
	 * Configure Saas configurator
	 */
	public void browseLotusLiveServicePart() {
		try {
			anonymousResolutionButtonClick("img1");
			anonymousResolutionButtonClick("img1_1");
			anonymousResolutionButtonClick("img1_1_0");
			// select ca to new ca
			selectedOptionByValue("serviceAgreement", "-2", driver);

			// click configure this service
			getDriver().findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/div[4]/div/ul/li[2]/ul/li[2]/ul/li/ul/li/a"))
					.click();
			waitForElementLoading(new Long(500));
			getDriver().switchTo().frame("cpqIframeId1");
			// set quantity and billingfrequency
			getDriver()
					.findElement(
							By.xpath("/html/body/div/div[2]/div/div/div[2]/form/table/tbody/tr[7]/td/div/div/div/table[2]/tbody/tr/td/input"))
					.sendKeys("1");
			getDriver()
					.findElement(
							By.xpath("/html/body/div/div[2]/div/div/div[2]/form/table/tbody/tr[7]/td/div/div/div/table[2]/tbody/tr[4]/td/input"))
					.sendKeys("5");
			/*selectedOptionByValue(
					By.xpath("/html/body/div/div[2]/div/div/div[2]/form/table/tbody/tr[8]/td/div/div/div/table/tbody/tr[3]/td[2]/select"),
					"U", driver);
			getDriver()
					.findElement(
							By.xpath("/html/body/div/div[2]/div/div/div[2]/form/table/tbody/tr[10]/td/div/div/div/table/tbody/tr/td/input"))
					.sendKeys("2000");*/
			// click submit
			getDriver().findElement(By.id("addConfigrtnButton")).click();
			waitForElementLoading(new Long(500));
		} catch (Exception e) {
			loggerContxt.info("exception happened:" + e.getMessage());
			loggerContxt.info("configure this part service failed.");
		}
	}
	
	public void browseLotusLiveServicePart(int index){
		loggerContxt.info("Click on 'Business Analytics'(id='img0') link on 'Part select ' page.");
		anonymousResolutionButtonClick("img0");
		loggerContxt.info("Action done");
		loggerContxt.info("Click on 'Algorithmics'(id='img0_0') link on 'Part select ' page.");
		anonymousResolutionButtonClick("img0_0");
		loggerContxt.info("Action done");
		loggerContxt.info("Click on 'Algo SaaS'(id='img0_0_0') link on 'Part select ' page.");
		anonymousResolutionButtonClick("img0_0_0");
		loggerContxt.info("Action done");
		
		loggerContxt.info("Click on 'Configure this service' link on 'Part select' page.");
		// click configure this service
		configreServiceLinks.get(index).click();
		loggerContxt.info("Action done");
		waitForElementLoading(5000L);
		loggerContxt.info("Switch to popup frame:: cpqIframeId1．");
		getDriver().switchTo().frame("cpqIframeId1");
		try{
			termInput = getDriver().findElement(By.id("ID_term"));
			termInput.sendKeys("12");
		}catch(Exception e){
			loggerContxt.info("Term in SaaS configurator is not configurable.");
		}
		List<WebElement> PartRowsElements = getDriver().findElements(By.xpath("//table[@class='ibm-data-table']/tbody/tr"));
		int partNum = 0;
		if (PartRowsElements != null) {
			partNum = PartRowsElements.size();
			loggerContxt.info("The line item number is partNum: " + partNum);
			PartRowsElements = null;
		}
		if (partNum != 0) {
			String partQtyElementXpath = "//table[@class='ibm-data-table']/tbody/tr[%d]/td[1]/input";
			for (int i = 1; i <= partNum; i++) {
				try {
					WebElement partQtyElement = getDriver().findElement(By.xpath(String.format(partQtyElementXpath, i)));
					loggerContxt.info("Send keys: 1 to the quantity on the frame on xpath::" + String.format(partQtyElementXpath, i));
					partQtyElement.sendKeys("1");
					loggerContxt.info("Action done");
					break;
				} catch (Exception e) {
					loggerContxt.info("Try to find a part with input textbox in next row.");
				}
			}
		}
		loggerContxt.info("Click on the Submit button on the frame.");
		// click submit
		addConfigrtnButton.click();
		loggerContxt.info("Action done");
		waitForElementLoading(2000L);
	}
	/**
	 * 
	 * @param ids
	 */
	public void browseLotusLiveServicePart(int index, List<String> xpaths) {
		try {
			
			loggerContxt.info("Click on 'Business Analytics'(id='img0') link on 'Part select ' page.");
			anonymousResolutionButtonClick("img0");
			loggerContxt.info("Action done");
			loggerContxt.info("Click on 'Algorithmics'(id='img0_0') link on 'Part select ' page.");
			anonymousResolutionButtonClick("img0_0");
			loggerContxt.info("Action done");
			loggerContxt.info("Click on 'Algo SaaS'(id='img0_0_0') link on 'Part select ' page.");
			anonymousResolutionButtonClick("img0_0_0");
			loggerContxt.info("Action done");
			
			loggerContxt.info("Click on 'Configure this service' link on 'Part select' page.");
			// click configure this service
			configreServiceLinks.get(index).click();
			loggerContxt.info("Action done");
			waitForElementLoading(5000L);
			loggerContxt.info("Switch to popup frame:: cpqIframeId1．");
			getDriver().switchTo().frame("cpqIframeId1");
			try{
				termInput = getDriver().findElement(By.id("ID_term"));
				termInput.sendKeys("12");
			}catch(Exception e){
				loggerContxt.info("Term in SaaS configurator is not configurable.");
			}
			// set quantity and billing frequency
			if ((xpaths==null) || (xpaths.size()==0)) {
				return;
			}else{
				for (Iterator<String> iterator = xpaths.iterator(); iterator.hasNext();) {
					String xpath = iterator.next();
					loggerContxt.info("Send keys: 1 to the quantity on the frame on xpath::" + xpath);
					getDriver().findElement(By.xpath(xpath)).sendKeys("1");
					loggerContxt.info("Action done");
				}
			}
			loggerContxt.info("Click on the Submit button on the frame.");
			// click submit
			addConfigrtnButton.click();
			loggerContxt.info("Action done");
			waitForElementLoading(2000L);
		} catch (Exception e) {
			loggerContxt.info("Exception happened:" + e.getMessage());
			loggerContxt.info("Configure this part service failed.");
		}
	}

	public void configureServicePopupClick() {
		getDriver().findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/div[4]/ul/li[2]/a")).click();
		getDriver().findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/div[4]/ul/li[2]/div/ul/li/a")).click();
		getDriver().findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/div[4]/ul/li[2]/div/ul/li/div/ul/li/a")).click();
		getDriver().findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/div[4]/ul/li[2]/div/ul/li/div/ul/li/div/table/tbody/tr/td[2]/a")).click();

	}


	/**
	 * Explore to "WebSphere Software" -->"IBM Applicatn Integratn Middleware" --> "Cast Iron" 
	 * and click on the "Configure this service" on the right side of "IBM WebSphere Cast Iron Express"
	 * */
	public void configureSaas4CastIron() {
										 
		getDriver().findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/div[4]/div/ul/li[3]/a/img")).click();
		waitForElementLoading(new Long(500));
		getDriver().findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/div[4]/div/ul/li[3]/div/ul/li[2]/a/img")).click();
		waitForElementLoading(new Long(500));
		getDriver().findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/div[4]/div/ul/li[3]/div/ul/li[2]/div/ul/li/a/img")).click();
		waitForElementLoading(new Long(500));
		getDriver().findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/div/div[4]/div/ul/li[3]/div/ul/li[2]/div/ul/li/div/table/tbody/tr/td[2]/a")).click();
	}

	public void addTheParts() {
		getDriver().switchTo().frame("cpqIframeId1");
		getDriver().findElement(By.xpath("/html/body/div/div[2]/div/div[2]/form/table/tbody/tr[7]/td/div/div/div/table/tbody/tr/td[1]/input")).sendKeys("1");

//		getDriver().findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/form/table/tbody/tr[9]/td/input[16]")).click();
//
		getDriver().findElement(By.id("addConfigrtnButton")).click();

//		getDriver().findElement(By.className("ibm-btn-arrow-pri")).click();

//		getDriver().findElement(By.name("jadeAction=ADD_OR_UPDATE_CONFIGRTN")).click();
	}

	public void addFirstPart(String cnt) {
		getDriver().switchTo().frame("cpqIframeId1");
		waitForElementLoading(new Long(500));
		getDriver().findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/form/table/tbody/tr[7]/td/div/div/div/table/tbody/tr/td[1]/input")).sendKeys(cnt);

//		getDriver().findElement(By.xpath("/html/body/div/div[2]/div/div/div[2]/form/table/tbody/tr[9]/td/input[16]")).click();
//
		getDriver().findElement(By.id("addConfigrtnButton")).click();

//		getDriver().findElement(By.className("ibm-btn-arrow-pri")).click();

//		getDriver().findElement(By.name("jadeAction=ADD_OR_UPDATE_CONFIGRTN")).click();
	}

	public void returnToDraftQuoteLinkClick(String appType) {
		if ("PGS".equalsIgnoreCase(appType)){
			getDriver().findElement(By.id("addConfigrtnButton")).click();
		} else if ("SQO".equalsIgnoreCase(appType)){
			getDriver().findElement(By.linkText("Return to draft quote")).click();
		}
	}

	public void returnToDraftQuoteLinkClick(){
		WebElement elt = getDriver().findElement(By.linkText("Return to draft quote"));
		System.out.println("elt=="+elt);
		elt.click();
	}

	/**
	 *  Explore to "Other Software" --> "Coremetrics " --> "Coremetrics Web Analytics" --> "IBM Marketing Center"
	 * @author suchuang
	 * @date Jan 9, 2013
	 */
	public void browser_IBM_MARKETING_CENTER() {
		String OTHER_SOFTWARE = "/html/body/div/div[2]/div/div/div[2]/div/div[4]/ul/li[2]/a/img";
		configureService(OTHER_SOFTWARE);
		
		String COREMETRICS = "/html/body/div/div[2]/div/div/div[2]/div/div[4]/ul/li[2]/div/ul/li/a/img";
		configureService(COREMETRICS);
		
		String COREMETRICS_WEB_ANALYTICS  = "/html/body/div/div[2]/div/div/div[2]/div/div[4]/ul/li[2]/div/ul/li/div/ul/li/a/img";
		configureService(COREMETRICS_WEB_ANALYTICS);
		
		String IBM_MARKETING_CENTER = "/html/body/div/div[2]/div/div/div[2]/div/div[4]/ul/li[2]/div/ul/li/div/ul/li/div/table/tbody/tr/td[2]/a";
		configureService(IBM_MARKETING_CENTER);
	}
	
	/**
	 * Explore to "WebSphere Software" --> "IBM Applicatn Integratn Middleware" --> "Cast Iron" --> "IBM WebSphere Cast Iron Express"
	 * @author suchuang
	 * @date Jan 9, 2013
	 */
	public void browser_IBM_WEBSPHERE_CAST_IRON_EXPRESS() {
		                             
		String WEBSPHERE_SOFTWARE = "/html/body/div/div[2]/div/div/div[2]/div/div[4]/div/ul/li[3]/a/img";
		configureService(WEBSPHERE_SOFTWARE);
		                                             
		String IBM_APPLICATN_INTEGRATN_MIDDLEWARE = "/html/body/div/div[2]/div/div/div[2]/div/div[4]/div/ul/li[3]/div/ul/li[2]/a/img";
		configureService(IBM_APPLICATN_INTEGRATN_MIDDLEWARE);

		String IBM_APPLICATN_INTEGRATN_MIDDLEWARE_CAST_IRON = "/html/body/div/div[2]/div/div/div[2]/div/div[4]/div/ul/li[3]/div/ul/li[2]/div/ul/li/a/img";
		configureService(IBM_APPLICATN_INTEGRATN_MIDDLEWARE_CAST_IRON);
		                                          
		String IBM_WEBSPHERE_CAST_IRON_EXPRESS = "/html/body/div/div[2]/div/div/div[2]/div/div[4]/div/ul/li[3]/div/ul/li[2]/div/ul/li/div/table/tbody/tr/td[2]/a";
		configureService(IBM_WEBSPHERE_CAST_IRON_EXPRESS);
	}
	
	public void browserAndConfigureService(String partPath) { 
		String[] pathArray = partPath.split("-");
		for (String path : pathArray) {
			configureService(path);
		}
	}
	
	/**
	 * Explore to "WebSphere Software" --> "IBM Applicatn Integratn Middleware" --> "Cast Iron" --> "IBM WebSphere Cast Iron Live Standard Edition"
	 * @author suchuang
	 * @date Jan 9, 2013
	 */
	public void browser_IBM_WEBSPHERE_CAST_IRON_LIVE_STANDARD_EDITION() {
		String WEBSPHERE_SOFTWARE = "/html/body/div/div[2]/div/div/div[2]/div/div[4]/ul/li[3]/a/img";
		configureService(WEBSPHERE_SOFTWARE);

		String IBM_APPLICATN_INTEGRATN_MIDDLEWARE = "/html/body/div/div[2]/div/div/div[2]/div/div[4]/ul/li[3]/div/ul/li[2]/a/img";
		configureService(IBM_APPLICATN_INTEGRATN_MIDDLEWARE);

		String IBM_APPLICATN_INTEGRATN_MIDDLEWARE_CAST_IRON = "/html/body/div/div[2]/div/div/div[2]/div/div[4]/ul/li[3]/div/ul/li[2]/div/ul/li/a/img";
		configureService(IBM_APPLICATN_INTEGRATN_MIDDLEWARE_CAST_IRON);

		String IBM_WEBSPHERE_CAST_IRON_LIVE_STANDARD_EDITION = "/html/body/div/div[2]/div/div/div[2]/div/div[4]/ul/li[3]/div/ul/li[2]/div/ul/li/div/table/tbody/tr[2]/td[2]/a";
		configureService(IBM_WEBSPHERE_CAST_IRON_LIVE_STANDARD_EDITION);
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Dec 19, 2012
	 * @param nodePath
	 */
	public void configureService(String nodePath) {
		getDriver().findElement(By.xpath(nodePath)).click();
		waitForElementLoading(new Long(500));
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Dec 20, 2012
	 * @return
	 */
	public ServiceConfigurePage switchToServiceConfigurePage() {
		getDriver().switchTo().frame("cpqIframeId1");
		ServiceConfigurePage scPage = new ServiceConfigurePage(getDriver());
		
		return scPage;
	}
	
	public void selectAgreement() {
		selectedOptionByValue("serviceAgreementid", "-2", getDriver());
	}
	
	public void clickAllImgs(){
		
		//get all the element and then click
		List<WebElement>  elmtsLists=getAllWebElements(By.xpath(".//a[contains(@class,'ibm-twisty-trigger ibm-twisty-trigger-closed')]"));
		loggerContxt.info("elmtsLists size:"+elmtsLists.size());
		for(WebElement elmt:elmtsLists){
			if(elmt.isDisplayed()){
				elmt.click();
				waitForElementLoading(300L);
				elmtsLists=getAllWebElements(By.xpath(".//a[contains(@class,'ibm-twisty-trigger ibm-twisty-trigger-closed')]"));
				elmtsLists.remove(elmt);
			}
			
		
			
		}
		loggerContxt.info("elmtsLists size:"+elmtsLists.size());
	} 
	
	public ServiceConfigurePage selectSaasPID(String saasPID) {
		elementClickByXPath(".//a[contains(@onclick,'"+saasPID+"')]");
		//getDriver().switchTo().frame("cpqIframeId1");
		WebElement webElement = waitForElementById("dijit_DialogUnderlay_0");
		if (webElement != null) {
		getDriver().switchTo().frame(getWebElementByLocator(By.id("cpqIframeId1")));
		}
		ServiceConfigurePage scPage = new ServiceConfigurePage(getDriver());
		
		return scPage;
	}
	

	
}
