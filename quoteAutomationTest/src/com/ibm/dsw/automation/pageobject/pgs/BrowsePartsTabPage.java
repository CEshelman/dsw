package com.ibm.dsw.automation.pageobject.pgs;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BrowsePartsTabPage extends PGSBasePage {	
	
	public BrowsePartsTabPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(linkText = "Add selected parts to draft quote")
	public WebElement addSelectedPartsToDraftQuoteLink;
	
	@FindBy(partialLinkText = "Select all displayed parts")
	public WebElement selectedAllDisplayedParts;
	
	@FindBy(linkText = "Browse parts")
	public WebElement  browsePartsTabLink;
	
	@FindBy(linkText = "Find parts")
	public WebElement  findPartsTabLink;
	
	@FindBy(linkText = "Browse software as a service")
	public WebElement  browseSoftwareAsAServiceTabLink;
	
	@FindBy(linkText = "Information management")
	public WebElement  informationManagementLink;
	
	
	@FindBy(linkText = "Collaboration Solutions")
	public WebElement  lotusLink;
	
	@FindBy(linkText = "Rational")
	public WebElement  rationalLink;
	
	@FindBy(linkText = "Tivoli")
	public WebElement  tivoliLink;
	
	@FindBy(linkText = "WebSphere")
	public WebElement  webSphereLink;
	
	@FindBy(linkText = "Other")
	public WebElement  otherLink;
	
	@FindBy(xpath = "//input[@name='lob']")
	public WebElement lob;
	


	public void anonymousResolutionButtonClick() {		
		WebElement anonymousResolutionButton = waitForElementById("img0_0");
		anonymousResolutionButton.click();
	}
	
	private boolean isTwoLevelTree() {
		String quoteType = lob.getAttribute("value");
		return "FCT".equals(quoteType) || "PPSS".equals(quoteType);
	}

	private boolean isThreeLevelTree() {
		return !this.isTwoLevelTree();
	}

	public void anonymousResolutionSubPartsOneButtonClick() {

		if (isThreeLevelTree()) {
			WebElement anonymousResolutionSubPartsOneButton = waitForElementById("img0_0_0");
			anonymousResolutionSubPartsOneButton.click();
		}
	}
	
	public void selectAnonymousResolutionSubParts() {		
		if (this.isThreeLevelTree()) {
		WebElement partsOneButton = waitForElementById("0_0_0_0");
		partsOneButton.click();
		} else {
			WebElement partsOneButton = waitForElementById("0_0_0");
			partsOneButton.click();
		}
	}
	
	public void addSelectedPartsToDraftQuoteLinkClick() {
		waitForElementLoading(5000L);
		selectedAllDisplayedParts.click();
		addSelectedPartsToDraftQuoteLink.click();
		if(alertExists()){
			driver.switchTo().alert().accept();
			clickDisplayedCheckBox();
			addSelectedPartsToDraftQuoteLink.click();
		}
	}
	
	
	/**
	 * Click on 'Collaboration Solutions'
	 */
	public void clickCollaborationSolutionsLink() {	
		if (selenium.isTextPresent("We are unable to load this product.")) {
			
			loggerContxt.info("We are unable to load this product. Try again.");
		}
		loggerContxt.info("Click on 'Collaboration Solutions' link on 'Part select' page.");
		lotusLink.click();
		loggerContxt.info("Action done");
		waitForElementLoading(5000L);
	}
	
	public void browseLotusParts() {
		clickCollaborationSolutionsLink();
		waitForElementLoading(5000L);
		// String[] pathArray = partPath.split("-");
		try {
			loggerContxt.info("Click first level part 'Lotus Domino Messaging Express'(id='img0_52') on 'Part select' page.");
			anonymousResolutionButtonClick("img0_52");
			loggerContxt.info("Action done");
			loggerContxt.info("Click second level part  'License + SW Subscription & Support'(id='img0_52_1') on 'Part select' page.");
			anonymousResolutionButtonClick("img0_52_1");
			loggerContxt.info("Action done");
	
			loggerContxt.info("Check all the displayed checkbox.");
			waitForElementLoading(8000L);
			clickDisplayedCheckBox();
			loggerContxt.info("Action done");
		} catch (Exception e) {
			loggerContxt.info("We are unable to load this product,re click the notes link and have a double check!");
			/*WebElement unableMsgElt = findElementByXPath(".//div[contains(@id,'Ajax tree_')]");
			if (isElementDisplayed(unableMsgElt)) {*/
			loggerContxt.info("Click on 'Collaboration Solutions' link on 'Part select' page.");
			clickCollaborationSolutionsLink();
			loggerContxt.info("Action done");
			waitForElementLoading(3000L);
			loggerContxt.info("Click first level part 'Bundle Other'(id='img0_1') on 'Part select' page.");
			anonymousResolutionButtonClick("img0_1");
			loggerContxt.info("Action done");
			loggerContxt.info("Click second level part 'Standard Product'(id='img0_1_0') on 'Part select' page.");
			anonymousResolutionButtonClick("img0_1_0");
			waitForElementLoading(8000L);
			loggerContxt.info("Check all the displayed checkbox.");
			clickDisplayedCheckBox();
			loggerContxt.info("Action done");

		}
	}
	
	public void browseLotusParts(String partNodeArray) {
		clickCollaborationSolutionsLink();
		waitForElementLoading(3000L);
		String[] pathArray = partNodeArray.split(";");
		if (pathArray.length < 3) {
			loggerContxt.error("Please try to config the part node for browsering parts..");
			return;
		}
		try {
			waitForElementLoading(5000L);
			browseLotusParts(pathArray[0],pathArray[1],pathArray[2]);
		} catch (Exception e) {
			clickCollaborationSolutionsLink();
			waitForElementLoading(5000L);
			loggerContxt.info("Click first level part 'Lotus Domino Messaging Express'(id='img0_52') on 'Part select' page.");
			anonymousResolutionButtonClick("img0_52");
			loggerContxt.info("Action done");
			loggerContxt.info("Click second level part  'License + SW Subscription & Support'(id='img0_52_1') on 'Part select' page.");
			waitForElementLoading(61000L);
			anonymousResolutionButtonClick("img0_52_1");
			loggerContxt.info("Action done");
			waitForElementLoading(8000L);
			loggerContxt.info("Check all the displayed checkbox.");
			clickDisplayedCheckBox();
			loggerContxt.info("Action done");
		}
	}
	
	/**
	 * 
	 * @param args The id sequence when selecting a part(brandId,subBrandId,partId)
	 */
	public void browseLotusParts(String...args) {
		clickCollaborationSolutionsLink();
		waitForElementLoading(new Long(2000));
		try {
			WebElement product;
			for (int i = 0; i < args.length; i++) {
				switch (i) {
				case 0:
					loggerContxt.info(String.format("Click first level part (id='%s') on 'Part select' page.", args[i]));
					break;
				case 1:
					loggerContxt.info(String.format("Click second level part (id='%s') on 'Part select' page.", args[i]));
					break;
				case 2:
					loggerContxt.info(String.format("Click the part (id='%s') on 'Part select' page.", args[i]));
					break;
				default:
					break;
				}
				product= waitForElementByLocator(By.id(args[i]), 3*60L);
				if (product == null) {
					throw new Exception("Failed to find product with id :: " + args[i]);
				}
				loggerContxt.info("expand the tree at: " + args[i]);
				product.click();
				loggerContxt.info("Action done");
			}
			
			clickDisplayedCheckBox();
		} catch (Exception e) {

			loggerContxt
					.error("We are unable to load this product,re click the notes link and have a double check!",e);
			/*WebElement unableMsgElt =findElementByXPath(".//div[contains(@id,'Ajax tree_')]");
			if(isElementDisplayed(unableMsgElt)){*/
			clickCollaborationSolutionsLink();
			waitForElementLoading(5000L);
			WebElement product,subProduct;
			product= waitForElementByLocator(By.id("img0_1"), 3*60L);
			if (product == null) {
				clickCollaborationSolutionsLink();
				waitForElementLoading(5000L);
				product= waitForElementByLocator(By.id("img0_1"), 60L);
			}else{
				product.click();
			}
			subProduct = waitForElementByLocator(By.id("img0_1_0"), 60L);
			subProduct.click();
			clickDisplayedCheckBox();
			//anonymousResolutionForLocator(By.xpath(".//input[contains(@id,'0_40_0_0')]"));

		}
	}

	public void addSelectedPartsToDraftQuote() {
		anonymousResolutionButtonClick();
		anonymousResolutionSubPartsOneButtonClick();
		selectAnonymousResolutionSubParts();
		addSelectedPartsToDraftQuoteLinkClick();
		returnToDraftQuoteLinkClick();
	}
	
	public void returnToDraftQuoteLinkClick() {		
		driver.findElement(By.linkText("Return to draft quote")).click();
		new PartsAndPricingTabPage(driver);
	}	
	

	
	public BrowseSoftwareAsServiceTabPage browseSoftwareAsAServiceTabLinkClick() {		
		driver.findElement(By.linkText("Browse software as a service")).click();
		BrowseSoftwareAsServiceTabPage page = new BrowseSoftwareAsServiceTabPage(driver);
		return page;
	}	
	
	public void clickDisplayedCheckBox(){
		
		//get all the element and then click
		List<WebElement>  elmtsLists=waitForElementsByLocator(By.xpath(".//input[@type='checkbox' or @name='part']"),3*60*1000L);
		loggerContxt.info("elmtsLists size:"+elmtsLists.size());
		try{
		for(WebElement elmt:elmtsLists){
			if(elmt.isDisplayed() && !elmt.isSelected()){
				elmt.click();
				waitForElementLoading(1000L);
				loggerContxt.info("check the checkbox..");
			}
		}
		if (elmtsLists.size() == 0) {
			loggerContxt.info("click on 'Select all displayed parts' link on 'Part select' page.");
			selectedAllDisplayedParts.click();
			loggerContxt.info("Action done");
			waitForElementLoading(5000L);
		}
		loggerContxt.info("elmtsLists size:"+elmtsLists.size());
		}catch (Exception e) {
			loggerContxt.fatal("Exception happened:"+e.getMessage(),e);
		}
	} 
}
