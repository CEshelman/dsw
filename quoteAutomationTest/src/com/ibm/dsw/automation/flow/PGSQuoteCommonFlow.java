package com.ibm.dsw.automation.flow;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.PGSPropertiesBean;
import com.ibm.dsw.automation.common.SQOPropertiesBean;
import com.ibm.dsw.automation.pageobject.pao.PAOHomePage;
import com.ibm.dsw.automation.pageobject.pgs.BrowsePartsTabPage;
import com.ibm.dsw.automation.pageobject.pgs.BrowseSoftwareAsServiceTabPage;
import com.ibm.dsw.automation.pageobject.pgs.CreateNewPassportAdvantageExpressCustomerPage;
import com.ibm.dsw.automation.pageobject.pgs.DisplayCustListPage;
import com.ibm.dsw.automation.pageobject.pgs.DisplayResellerListPage;
import com.ibm.dsw.automation.pageobject.pgs.DisplayStatusSearchReslutPage;
import com.ibm.dsw.automation.pageobject.pgs.FindExistingCustomerPage;
import com.ibm.dsw.automation.pageobject.pgs.FindPartsTabPage;
import com.ibm.dsw.automation.pageobject.pgs.HelpAndTutorialPage;
import com.ibm.dsw.automation.pageobject.pgs.ImportSalesQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.LoginPage;
import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSApprovalTabPage;
import com.ibm.dsw.automation.pageobject.pgs.PGSCreateQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSFindPartsSelectTabPage;
import com.ibm.dsw.automation.pageobject.pgs.PGSHomePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSJumpPage;
import com.ibm.dsw.automation.pageobject.pgs.PGSMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PGSSiteSelectPage;
import com.ibm.dsw.automation.pageobject.pgs.PGSStatusSalesQuote;
import com.ibm.dsw.automation.pageobject.pgs.PartAndPricingDetailsPage;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.pgs.RetrieveSavedSalesQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.SalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.pgs.SelectAResellerPage;
import com.ibm.dsw.automation.pageobject.pgs.SelectedQuotePPTabPage;
import com.ibm.dsw.automation.pageobject.pgs.ServiceConfigurePage;
import com.ibm.dsw.automation.pageobject.pgs.StatusSearchTabPage;
import com.ibm.dsw.automation.pageobject.pgs.SubmitSQSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.pgs.UnderEvaluationTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;

/**
 * 
 * @author suchuang
 * @date Jan 24, 2013
 */
public class PGSQuoteCommonFlow extends QuoteFlow {
	
	
	/**
	 * 
	 */
	PGSPropertiesBean propBean = null;
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 24, 2013
	 * @param propBean
	 */
	public void setPropBean(PGSPropertiesBean propBean) {
		this.propBean = propBean;
	}
	
	public PGSPropertiesBean getProbBean(){
		return propBean;
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 24, 2013
	 */
	public PGSQuoteCommonFlow() {
		init();
	}
	

	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 24, 2013
	 * @param key
	 * @return
	 */
	private String getProperty(String key) {
		return prop.getProperty(key);
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 24, 2013
	 * @param url
	 */
	@Override
	protected void reset(String url) {
		getDriver().get(url);
		setSelenium(new WebDriverBackedSelenium(getDriver(), url));
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 24, 2013
	 * @return
	 */
	public PGSHomePage loginPgs(String logonFlg) {

		PGSHomePage pgsHomePage = null;
		if (BaseTestCase.LOGON_IN_PGS_VIA_PGS_JUMP.equals(logonFlg)) {
			reset(getProperty(getProperty("env")+".pgs_jump_url"));
			PGSJumpPage jumpPage = new PGSJumpPage(this.driver, new Long(20));
		    pgsHomePage = jumpPage.login(propBean.getCustomerNum(), propBean.getBpNum());
		    loggerContxt.info("Login PGS with CustomerNum=" + propBean.getCustomerNum() + ", BpNum=" + propBean.getBpNum());
		} if (BaseTestCase.LOGON_IN_PGS_VIA_SQO_WEBAUTH.equals(logonFlg)) {
			SQOQuoteCommonFlow sqoFlow = new SQOQuoteCommonFlow();
			sqoFlow.setDriver(driver);
			sqoFlow.setSelenium(selenium);
			sqoFlow.setPropBean(new SQOPropertiesBean());
			
			try {
				/*String newUrl = getProperty(getProperty("env")+".sqo_url");
				reset(newUrl);*/
				String loginUser = getProperty("pgs_username");
				String loginPasswd = getProperty("pgs_password");
				
				getLogonInf().setSqoLogonUser(loginUser);
				getLogonInf().setSqoUserPwd(loginPasswd);
				
			} catch (Exception e) {
				loggerContxt.fatal("Failed to get configure for the login user/password.",e);
			}
		
			SQOHomePage sqoHomePage = sqoFlow.loginPgsViaSqo(getLogonInf());
			/*DistSoftHomePage sistSoftHomePage = sqoHomePage.gotoPgsSiteSelectPage();
			PGSSiteSelectPage siteSelectPage = sistSoftHomePage.gotoSelectSiteNumber();*/
			PAOHomePage homePage = sqoHomePage.gotoPAOHomePage(propBean.getSiteSelect());
			pgsHomePage = homePage.gotoPGS();
		}else if (BaseTestCase.LOGON_IN_PGS_VIA_SQO_JUMP_PAGE.equals(logonFlg)) {
			
		} else if (BaseTestCase.LOGON_IN_PGS_VIA_PGS_WEBAUTH.equals(logonFlg)) {
			LoginPage loginPage = new LoginPage(this.driver);
			String loginUser = getProperty(getProperty("env") + ".pgs_username");
			String loginPasswd = getProperty(getProperty("env")
					+ ".pgs_password");
			try {

				PGSSiteSelectPage siteSelectPage =  loginPage.loginAsToSiteSelectPage(loginUser, loginPasswd);
				loggerContxt.info("BP number...."+propBean.getSiteSelect());
				PAOHomePage homePage = siteSelectPage.gotoPAOHomePage(propBean.getSiteSelect());
				pgsHomePage = homePage.gotoPGS();
			} catch (Exception e) {
				loggerContxt.info("DecryptManager ERR");
			}
		
		}

		return pgsHomePage;
	}
	
	/**
	 * go to create quote page and create a draft quote
	 * @author suchuang
	 * @date Jan 24, 2013
	 * @param pgsHomePage
	 * @return
	 */
	public PGSMyCurrentQuotePage createDraftQuote(PGSHomePage pgsHomePage) {
		loggerContxt.info("create a quote.....");
		boolean hasCurrentQuote = selenium.isTextPresent("Quote reference");
		loggerContxt.info("having current quote....." + hasCurrentQuote);
		
		// Create a PGS quote
		PGSCreateQuotePage cq = pgsHomePage.gotoCreateQuote();
		PGSMyCurrentQuotePage currentQuotePage = cq.createQuote(hasCurrentQuote);
		return currentQuotePage;
	}

	/**
	 * find a customer by customerNum
	 * @author suchuang
	 * @date Jan 24, 2013
	 * @param currentQuotePage
	 */
	public void createCustomer(MyCurrentQuotePage currentQuotePage) {
		
		// if customerName set,use customerNum to search;both customerNum and
		// customerName set,use customerNum;
		// both empty,don't search customer
		// if customerNum set,use customerNum to search as default,
		if ((StringUtils.isNotBlank(propBean.getCustomerNum()))
				|| (StringUtils.isNotBlank(propBean.getCustomerName()))) {

			// Find an existing customer
			FindExistingCustomerPage searchCustPage = currentQuotePage.goToFindCustomerTab();
			loggerContxt.info("go to search customer page.....");
			DisplayCustListPage displayCustListPage = null;
			if (StringUtils.isNotBlank(propBean.getCustomerNum())) {
				// display customer
				displayCustListPage = searchCustPage
						.displayCustomerListBySiteNum(propBean
								.getCustomerNum());
				loggerContxt.info("display customer page by CustomerNum.....");
			}

			if ((StringUtils.isBlank(propBean.getCustomerNum()))
					&& (StringUtils.isNotBlank(propBean.getCustomerName()))) {
				// display customer
				displayCustListPage = searchCustPage
						.displayCustomerListBySiteNum(propBean.getCustomerName());
				loggerContxt.info("display customer page by CustomerName.....");
			}

			// select customer and back current quote page
			currentQuotePage = displayCustListPage.selectCustomer();
			loggerContxt
					.info("select customer and back current quote page.....");
		} else {
			CreateNewPassportAdvantageExpressCustomerPage createCustomerPage = currentQuotePage.goToCreatePassportAdvantageExpressCustomerTab();
			createCustomerPage.fillCustomerForm();
			createCustomerPage.submitCreateCustomer();
		}
		
		// save current draftquote number for future use
		propBean.setDraftQuoteNum(currentQuotePage.getQuoteNum());
		loggerContxt.info("current quote number....."
				+ currentQuotePage.getQuoteNum());
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 30, 2013
	 * @param currentQuotePage
	 */
	public void createANewCustomer(MyCurrentQuotePage currentQuotePage) {
		CreateNewPassportAdvantageExpressCustomerPage createCustomerPage = currentQuotePage.goToCreatePassportAdvantageExpressCustomerTab();
		createCustomerPage.fillCustomerForm();
		currentQuotePage = createCustomerPage.submitCreateCustomer();
	}
	
	/**
	 * find a reseller by resellerNum
	 * @author suchuang
	 * @date Jan 24, 2013
	 * @param currentQuotePage
	 */
	private void createReseller(MyCurrentQuotePage currentQuotePage) {
		
		// if resellerName set,use resellerNum to search;both resellerNum and
		// resellerName set,use resellerNum;
		// both empty,don't search reseller
		// if resellerNum set,use resellerNum to search as default,
		if ((StringUtils.isNotBlank(propBean.getResellerNum()))
				|| (StringUtils.isNotBlank(propBean.getResellerName()))) {
			// Find an existing reseller
			SelectAResellerPage searchResellerPage = currentQuotePage.goToFindResellerTab();
			
			loggerContxt.info("go to search reseller page.....");
			DisplayResellerListPage displayResellerListPage = null;
			if (StringUtils.isNotBlank(propBean.getResellerNum())) {
				// display reseller
				displayResellerListPage = searchResellerPage
						.displayResellerListByNum(propBean.getResellerNum());
				loggerContxt.info("display reseller page by resellerNum.....");
			}

			if ((StringUtils.isBlank(propBean.getResellerNum()))
					&& (StringUtils.isNotBlank(propBean.getResellerName()))) {
				// display reseller
				displayResellerListPage = searchResellerPage
						.displayResellerListByNum(propBean.getResellerName());
				loggerContxt.info("display reseller page by resellerName.....");
			}
			// select reseller and back current quote page
			currentQuotePage = displayResellerListPage.selectReseller();
			loggerContxt
					.info("select a reseller and back to current quote page.....");
		}
	}
	
	/**
	 * remove selected reseller
	 * @author suchuang
	 * @date Feb 4, 2013
	 * @param currentQuotePage
	 */
	public void removeReseller(MyCurrentQuotePage currentQuotePage) {
		currentQuotePage.clearSelectedResellerLinkClick();
	}
	
	/**
	 * select customer, reseller
	 * @author suchuang
	 * @date Jan 24, 2013
	 * @param currentQuotePage
	 * @return
	 */
	public MyCurrentQuotePage createCustomerAndPartner(
			MyCurrentQuotePage currentQuotePage) {
		loggerContxt
				.info("select a customer/partner/distributor based on  the properties set in the file start.....");

		/*********************************************** select customer start **************************************************/
		createCustomer(currentQuotePage);
		/********************************************** select customer end ******************************************************/

		// save current draftquote number for future use
		propBean.setDraftQuoteNum(currentQuotePage.getQuoteNum());
		loggerContxt.info("current quote number....."
				+ currentQuotePage.getQuoteNum());
		// Enter "xprs//@Test.com" in the "* Email: " field under Quote contact
		// section.
//		currentQuotePage.fillEmailAdr(propBean.getCustmail());

		/********************************************** select reseller start **************************************************/
		createReseller(currentQuotePage);
		/********************************************* select reseller end ******************************************************/

		
		/********************************************** select distributor start **************************************************/
		/********************************************* select distributor end ******************************************************/

		// set Quote expiration date
		currentQuotePage.selectExpirationDate(30);
		
		loggerContxt
				.info("create quote page finished,current page is SQOMyCurrentQuotePage.....");
		return currentQuotePage;
	}
	
	/**
	 * go to set Sales information tab and set sales info
	 * @author suchuang
	 * @date Jan 24, 2013
	 * @param currentQuotePage
	 */
	public void setSalesInfo(MyCurrentQuotePage currentQuotePage){
		//Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SalesInfoTabPage salesTab = currentQuotePage.goToSalesInfoTabPage();
		loggerContxt.info("Go to SalesInf Tab end.....");
		loggerContxt.info("enter sales info");
		salesTab.enterSalesInf(propBean.getBriefTitle(), propBean.getQuoteDesc());
		loggerContxt.info("enter sales info BriefTitle=" + propBean.getBriefTitle() + ", QuoteDesc=" + propBean.getQuoteDesc());
	}
	
	public SalesInfoTabPage setSalesInfo(PartsAndPricingTabPage partsAndPricingTab) {
		//Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SalesInfoTabPage salesTab = partsAndPricingTab.goToSalesInfoTabPage();
		loggerContxt.info("Go to SalesInf Tab end.....");
		
		loggerContxt.info("enter sales info");
		salesTab.enterSalesInf(propBean.getBriefTitle(), propBean.getQuoteDesc());
		loggerContxt.info("enter sales info BriefTitle=" + propBean.getBriefTitle() + ", QuoteDesc=" + propBean.getQuoteDesc());
	
		salesTab.saveDraftQuoteLink(); 
		return salesTab;
	}
	
	/**
	 * go to the Approval tab and set spcial bid Info
	 * @author suchuang
	 * @date Jan 24, 2013
	 * @param currentQuotePage
	 */
	public void setApproval(MyCurrentQuotePage currentQuotePage) {
		PGSApprovalTabPage approvalTabPage = currentQuotePage.goToApprovalTab();
		approvalTabPage.enterApprovalInfo(propBean.getSpBidRgn(), propBean.getSpBidDist());
		approvalTabPage.enterSpcialBidInf(propBean.getJustificationSummary());
		
//		AddAttachmentPage addAttachmentPage = approvalTabPage.goToAddAttachmentPage();
//		addAttachmentPage.uploadAttachment("C:/Users/IBM_ADMIN/Desktop/tmp/ImageLog.gif");
//		addAttachmentPage.submit();
	}
	
	public void downloadQuote(MyCurrentQuotePage currentQuotePage) {
		currentQuotePage.downloadQuoteAsRichTextFileLinkClick();
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 30, 2013
	 * @param currentQuotePage
	 */
	public void saveQuote(MyCurrentQuotePage currentQuotePage) {
		currentQuotePage.saveQuoteAsDraft();
	}
	
	/**
	 * submit the current quote
	 * @author suchuang
	 * @date Jan 24, 2013
	 * @param currentQuotePage
	 */
	public void submitQuote(MyCurrentQuotePage currentQuotePage) {
		loggerContxt.info("submit current quote page.....");
		currentQuotePage.submitQuote();
	}
	
	/**
	 * add saas parts to quote
	 * @author suchuang
	 * @date Jan 24, 2013
	 * @param currentQuotePage
	 * @return
	 */
	public PartsAndPricingTabPage addSaasParts(MyCurrentQuotePage currentQuotePage) { 
		PartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
		return addSaasParts(partsAndPricingTab);
	}
	
	/**
	 * add saas parts to quote
	 * @author suchuang
	 * @date Jan 24, 2013
	 * @param currentQuotePage
	 * @return
	 */
	public PartsAndPricingTabPage addSaasParts(PartsAndPricingTabPage partsAndPricingTab) { 
		//PartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
		BrowseSoftwareAsServiceTabPage browseSoftwareAsServiceTabPage = partsAndPricingTab.browseSoftwareAsAServiceLinkClick();
		browseSoftwareAsServiceTabPage.selectAgreement();
		//browseSoftwareAsServiceTabPage.browserAndConfigureService(propBean.getSaasPartPath());
		
		browseSoftwareAsServiceTabPage.clickAllImgs();
		ServiceConfigurePage scPage =browseSoftwareAsServiceTabPage.selectSaasPID(propBean.getSaasPartId());
		
		//ServiceConfigurePage scPage = browseSoftwareAsServiceTabPage.switchToServiceConfigurePage();
		scPage.configureService();
//		partsAndPricingTab.editProvisioningFormLink();

		return partsAndPricingTab;
	}
	
	public PartsAndPricingTabPage validateModifyServiceDate(
			PartsAndPricingTabPage partsAndPricingTab) {

		loggerContxt.info("validate when modify the service date  No is checked begin.....");
		partsAndPricingTab.validateModifyServiceDateNo(propBean.getSaasPartId());
		loggerContxt.info("validate when modify the service date  No is checked end.....");
		
		loggerContxt.info("validate when modify the service date  Yes is checked begin.....");
		partsAndPricingTab = partsAndPricingTab.validateModifyServiceDateYes(propBean.getSaasPartId(),propBean.getEstProvisioningDays());
		loggerContxt.info("validate when modify the service date  Yes is checked end.....");
		
		return partsAndPricingTab;
	}

	
	/**
	 * add software parts to quote
	 * @author suchuang
	 * @date Jan 25, 2013
	 * @param currentQuotePage
	 * @return
	 */
	public PartsAndPricingTabPage browseSoftwareParts(MyCurrentQuotePage currentQuotePage) {
		PartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
		BrowsePartsTabPage browsePartsTabPage = partsAndPricingTab.browsePartsLinkClick();
		browsePartsTabPage.browseLotusParts(propBean.getBrandId(), propBean.getSubBrandId());
		browsePartsTabPage.addSelectedPartsToDraftQuoteLinkClick();
		browsePartsTabPage.returnToDraftQuoteLinkClick();
		
		PartAndPricingDetailsPage ppDetailsPage = partsAndPricingTab.goToPartAndPricingDetailsPage(propBean.getPartNum());
		loggerContxt.info("Parts and Price information displayed.");
		ppDetailsPage.close();
		partsAndPricingTab.returnToSelfWindow();
		
		return partsAndPricingTab;
	}
	
	/**
	 * add software parts to quote
	 * @author anders
	 * @date Jan 25, 2013
	 * @param currentQuotePage
	 * @param partNodeArray This is a sequence of the part browser node. such as "img0_1;img0_1_0;0_1_0_0"
	 * @return
	 */
	public PartsAndPricingTabPage browseSoftwareParts(MyCurrentQuotePage currentQuotePage,String partNodeArray) {
		PartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
		BrowsePartsTabPage browsePartsTabPage = partsAndPricingTab.browsePartsLinkClick();
		browsePartsTabPage.browseLotusParts(partNodeArray);
		browsePartsTabPage.addSelectedPartsToDraftQuoteLinkClick();
		browsePartsTabPage.returnToDraftQuoteLinkClick();
		
		PartAndPricingDetailsPage ppDetailsPage = partsAndPricingTab.goToPartAndPricingDetailsPage(propBean.getPartNum());
		loggerContxt.info("Parts and Price information displayed.");
		ppDetailsPage.close();
		partsAndPricingTab.returnToSelfWindow();
		
		return partsAndPricingTab;
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 29, 2013
	 * @param currentQuotePage
	 * @return
	 */
	public PartsAndPricingTabPage findSoftwareParts(MyCurrentQuotePage currentQuotePage) {
		loggerContxt.info("search parts.....");

		// Select the last date of the current month as the
		// "Quote expiration date"
		currentQuotePage.selectExpirationDate(30);

		PartsAndPricingTabPage partsAndPricingTab = currentQuotePage
				.goToPartsAndPricingTab();

		if ((StringUtils.isNotBlank(propBean.getPartList1()))
				|| StringUtils.isNotBlank(propBean.getPartList2())
				|| StringUtils.isNotBlank(propBean.getPartDes())) {
			FindPartsTabPage findPartsTabPage = (FindPartsTabPage) partsAndPricingTab.findPartsLinkClick();

			PGSFindPartsSelectTabPage findPartsSelect = null;
			if ((StringUtils.isNotBlank(propBean.getPartList1()))) {
				findPartsSelect = findPartsTabPage.findPartsLinkClick(propBean
						.getPartList1());
				loggerContxt.info("search first of parts:....."
						+ propBean.getPartList1());
				findPartsTabPage = findPartsSelect
						.selectPartsAndChgCriteriaClick();
			}

			if ((StringUtils.isNotBlank(propBean.getPartList2()))) {
				findPartsSelect = findPartsTabPage.findPartsLinkClick(propBean
						.getPartList2());
				loggerContxt.info("search the other parts:....."
						+ propBean.getPartList2());
				findPartsSelect.selectPartsAndChgCriteriaClick();
			}

			if ((StringUtils.isNotBlank(propBean.getPartDes()))) {
				findPartsSelect = findPartsTabPage.findPartsByPartDes(propBean
						.getPartDes());
				loggerContxt.info("search the parts by :....."
						+ propBean.getPartDes());
				findPartsSelect.selectPartsAndChgCriteriaClick();
			}

			partsAndPricingTab = findPartsSelect.rtn2DraftQuote();
		}

		loggerContxt
				.info("create quote page finished,current page is SQOMyCurrentQuotePage.....");

		if ((StringUtils.isNotBlank(propBean.getPartList1()))) {
			String quoteNum = propBean.getPartList1().split(",")[0];
			setPartsDatas(partsAndPricingTab, quoteNum);
		}
		
		return partsAndPricingTab;
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 30, 2013
	 * @param partsAndPricingTab
	 */
	public PartsAndPricingTabPage removeBrowseParts(PartsAndPricingTabPage partsAndPricingTab) {
		partsAndPricingTab.recalculateQuotePartAndPriceTab();
		partsAndPricingTab.deleteParts(propBean.getPartNum());
		
		return partsAndPricingTab;
		
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 30, 2013
	 * @param partsAndPricingTab
	 */
	public PartsAndPricingTabPage removeFindParts(PartsAndPricingTabPage partsAndPricingTab) {
		partsAndPricingTab.recalculateQuotePartAndPriceTab();
		if (StringUtils.isNotBlank(propBean.getPartList1())) {
			partsAndPricingTab.deleteParts(propBean.getPartList1());
		}
		
		if (StringUtils.isNotBlank(propBean.getPartList2())) {
			partsAndPricingTab.deleteParts(propBean.getPartList2());
		}
		
		return partsAndPricingTab;
	}
	
	public PGSApprovalTabPage checkSubmittedQuote(PGSStatusSalesQuote sbmdCPTab) { 
		SelectedQuotePPTabPage sbmdPPTab = sbmdCPTab.goToPPTab();

		loggerContxt.info("go the part and pricing tab.....");
		SubmitSQSalesInfoTabPage sbmdSaleInfTab = sbmdPPTab.goToSalesInfoTab();
		loggerContxt.info("view the detail CP.....");
		PGSApprovalTabPage approvalTabPage = sbmdSaleInfTab.goToApprovalTab();

		return approvalTabPage; 
	}
	
	public PGSApprovalTabPage checkModifiedServiceDateInPPTabAfterSubmission(PGSStatusSalesQuote sbmdCPTab){
		SelectedQuotePPTabPage sbmdPPTab = sbmdCPTab.goToPPTab();

		loggerContxt.info("go the part and pricing tab.....");
		WebElement optionElem = sbmdPPTab.findElementByXPath(".//td[contains(text(),'Modify service dates')]");
		sbmdPPTab.assertObjectEquals(optionElem.getText().contains("yes"), new Boolean("true"));
		
		WebElement dateElem = sbmdPPTab.findElementByXPath(".//tr[td[contains(text(),'Service End Date')]/text()]/td[3]");
		sbmdPPTab.assertObjectEquals(dateElem.getText().trim().matches("\\d{2}-[A-Za-z]{3}-\\d{4}"),new Boolean("true"));
		loggerContxt.info("modifying service date is verified.");
		
		loggerContxt.info("go the Sales Info tab.....");
		SubmitSQSalesInfoTabPage sbmdSaleInfTab = sbmdPPTab.goToSalesInfoTab();
		loggerContxt.info("view the detail CP.....");
		PGSApprovalTabPage approvalTabPage = sbmdSaleInfTab.goToApprovalTab();

		return approvalTabPage; 
	}
	
	/**
	 * find quote by quote num
	 * @author suchuang
	 * @date Jan 25, 2013
	 * @param currentQuotePage
	 * @return
	 */
	public PGSStatusSalesQuote findQuoteByNum(MyCurrentQuotePage currentQuotePage) {
		String quoteNum = propBean.getDraftQuoteNum();
		StatusSearchTabPage statusSearchPage = currentQuotePage.gotoStatus();
		
		loggerContxt
				.info("Check all checkboxes of the following sections Find the following quote types "
						+ "Find quotes and special bids with the following overall statuses"
						+ " Select 'Date submitted (descending)' in the Sort by dropdown "
						+ "Check the Make above selections my default checkbox"
						+ " Select the Find all matching quotes Press on the 'Find quotes' button");
		DisplayStatusSearchReslutPage statusResultPage = statusSearchPage
				.goDispQuoteByQuoteNum(quoteNum);
		PGSStatusSalesQuote pgsStatusSalesQuote = null;
		pgsStatusSalesQuote = statusResultPage.goDispQuoteReslt();
		
		if (null == pgsStatusSalesQuote) {
			pgsStatusSalesQuote = findQuoteFromUnderEvaluationPage(statusSearchPage, quoteNum);
		}
		
		return pgsStatusSalesQuote;
	}
	
	public PGSStatusSalesQuote findQuoteFromUnderEvaluationPage(StatusSearchTabPage statusSearchPage, String quoteNum) {
		UnderEvaluationTabPage ueTab = statusSearchPage.goToUnderEvaluationTab();
		return ueTab.viewQuoteDetail(quoteNum);
	}
    

	public PGSStatusSalesQuote findQuoteByAllCondition(MyCurrentQuotePage currentQuotePage) {
		StatusSearchTabPage statusSearchPage = currentQuotePage.gotoStatusByNavigation();
		
		loggerContxt
				.info("Check all checkboxes of the following sections Find the following quote types "
						+ "Find quotes and special bids with the following overall statuses"
						+ " Select 'Date submitted (descending)' in the Sort by dropdown "
						+ "Check the Make above selections my default checkbox"
						+ " Select the Find all matching quotes Press on the 'Find quotes' button");
		DisplayStatusSearchReslutPage statusResultPage = statusSearchPage
				.goDispAllQuoteReslt();
		
		return statusResultPage.goDispQuoteReslt();
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 30, 2013
	 * @param currentQuotePage
	 */
	public MyCurrentQuotePage retrieveSavedSalesQuotePage(
			MyCurrentQuotePage currentQuotePage) {

		RetrieveSavedSalesQuotePage savedSalesQuote = currentQuotePage
				.goRetrieveSavedSalesQuoteTab();
		currentQuotePage = savedSalesQuote.goViewDetailSavedQuote();
		return currentQuotePage;
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 30, 2013
	 * @param currentQuotePage
	 */
	public void goToHelpPage(MyCurrentQuotePage currentQuotePage) {
		HelpAndTutorialPage page = currentQuotePage.goToHelpAndTutorialPage();
		loggerContxt.info("Help page displayed.....");
		page.goToMyCurrentQuotePage();
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 30, 2013
	 * @param partsAndPricingTab
	 * @param quoteNum
	 */
	public void setPartsDatas(PartsAndPricingTabPage partsAndPricingTab, String quoteNum) {
		String seqnum = propBean.getPartSortSeq();
		partsAndPricingTab.changeDisc_pct(quoteNum, propBean.getPartDiscount(), seqnum);
		partsAndPricingTab.changeOrridPrice(quoteNum, propBean.getPartOverridePrice(), seqnum);
		partsAndPricingTab.changeSortSeq(quoteNum, propBean.getPartSortSeq(), seqnum);
		loggerContxt.info("Part discount=" + propBean.getPartDiscount() + ", Part override price=" + propBean.getPartOverridePrice() + ", Part sort seqnum=" + propBean.getPartSortSeq());
	}
	
	public void retrieveSavedSalesQuotePage(
			SalesInfoTabPage salesTab) {
		MyCurrentQuotePage currentQuotePage=salesTab.goToCustPartnerTab();
		retrieveSavedSalesQuotePage(currentQuotePage);
	
	}
	
	/**
	 * 
	 * @author suchuang
	 * @date Jan 24, 2013
	 */
	public void dummy() {
		String url = getProperty("fvt.pgs_jump_url");
		//url = "";
		PGSHomePage sqoHomePage = loginPgs(BaseTestCase.LOGON_IN_PGS_VIA_PGS_WEBAUTH);
		PGSMyCurrentQuotePage currentQuotePage = createDraftQuote(sqoHomePage);
		createCustomerAndPartner(currentQuotePage);
		addSaasParts(currentQuotePage);
//		addSoftwareParts(currentQuotePage);
		setSalesInfo(currentQuotePage);
		setApproval(currentQuotePage);
		submitQuote(currentQuotePage);
		loggerContxt.info("@Test " + getClass().getSimpleName() + " has passed!");
	}
	
//	/**
//	 * click browseSAAS link
//	 * @author Li Yu
//	 * @date April 11, 2013
//	 * @param currentQuotePage
//	 * @return
//	 */
//	public PartsAndPricingTabPage browseSAAS(MyCurrentQuotePage currentQuotePage) {
//		PartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
//	
//		BrowseSoftwareAsServiceTabPage browseSAASTabPage = partsAndPricingTab.browseSoftwareAsAServiceLinkClick();
//
//		browseSAASTabPage.returnToDraftQuoteLinkClick();
//		
//		PartAndPricingDetailsPage ppDetailsPage = partsAndPricingTab.goToPartAndPricingDetailsPage(propBean.getPartNum());
//		loggerContxt.info("Price information display.....");
//		loggerContxt.info("Parts information display.....");
//		ppDetailsPage.close();
//		partsAndPricingTab.returnToSelfWindow();
//		
//		return partsAndPricingTab;
//	}
	
	/**
	 * click browseSAAS link
	 * @author Li Yu
	 * @date April 11, 2013
	 * @param partsAndPricingTab
	 * @return
	 */
	public PartsAndPricingTabPage browseSAAS(PartsAndPricingTabPage partsAndPricingTab) { 
		//PartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
		BrowseSoftwareAsServiceTabPage browseSoftwareAsServiceTabPage = partsAndPricingTab.browseSoftwareAsAServiceLinkClick();
		browseSoftwareAsServiceTabPage.selectAgreement();
	
		browseSoftwareAsServiceTabPage.clickAllImgs();

		return partsAndPricingTab;
	}
	
	/**
	 * click Import a sales quote spreadsheet link
	 * @author Li Yu
	 * @date April 11, 2013
	 * @param pgsHomePage
	 * @return
	 */
	public ImportSalesQuotePage openImportSalesQuote(PGSHomePage pgsHomePage) { 

		loggerContxt.info("go to Import a sales quote spreadsheet page .....");
		ImportSalesQuotePage importPage = pgsHomePage.gotoImportSalesQuote();

		return importPage;
	}
	
	/**
	 * click Import a sales quote spreadsheet link
	 * @author Li Yu
	 * @date April 11, 2013
	 * @param partsAndPricingTab
	 * @return
	 */
	public PGSHomePage gotoHomePage(PartsAndPricingTabPage partsAndPricingTab){
		
		PGSHomePage pgsHomePage = null;
		pgsHomePage = partsAndPricingTab.gotoHomePage();
		
		return pgsHomePage;
	}
	/**
	 * click Import a sales quote spreadsheet link
	 * @author Li Yu
	 * @date April 11, 2013
	 * @param importSalesQuotePage
	 * @return
	 */
	public PGSHomePage gotoHomePageFromImport(ImportSalesQuotePage importSalesQuotePage){
		
		PGSHomePage pgsHomePage = null;
		pgsHomePage = importSalesQuotePage.goHomePage();
		
		return pgsHomePage;
	}
	
	/**
	 * 
	 * @author Li Yu
	 * @date April 12, 2013
	 * @param pgsHomePage
	 * @return
	 */
	public PGSStatusSalesQuote findQuoteByMatchingQuotes(PGSHomePage pgsHomePage) {
		StatusSearchTabPage statusSearchPage = pgsHomePage.gotoStatus();
		
		loggerContxt
				.info("Check all checkboxes of the following sections Find the following quote types "
						+ "Find quotes and special bids with the following overall statuses"
						+ " Select 'Date submitted (descending)' in the Sort by dropdown "
						+ "Check the Make above selections my default checkbox"
						+ " Select the Find all matching quotes Press on the 'Find quotes' button");
		DisplayStatusSearchReslutPage statusResultPage = statusSearchPage
				.goDispAllQuoteReslt();
		
		return statusResultPage.goDispQuoteReslt();
	}
	
	/**
	 * find quote by quote num
	 * @author Li Yu
	 * @date April 12, 2013
	 * @param currentQuotePage
	 * @return
	 */
	public PGSStatusSalesQuote findByQuoteNum(PGSHomePage pgsHomePage) {
		String quoteNum = propBean.getQuoteNum();
		StatusSearchTabPage statusSearchPage = pgsHomePage.gotoStatus();
		
		loggerContxt
				.info("Check all checkboxes of the following sections Find the following quote types "
						+ "Find quotes and special bids with the following overall statuses"
						+ " Select 'Date submitted (descending)' in the Sort by dropdown "
						+ "Check the Make above selections my default checkbox"
						+ " Select the Find all matching quotes Press on the 'Find quotes' button");
		DisplayStatusSearchReslutPage statusResultPage = statusSearchPage
				.goDispQuoteByQuoteNum(quoteNum);
		PGSStatusSalesQuote pgsStatusSalesQuote = null;
		pgsStatusSalesQuote = statusResultPage.goDispQuoteReslt();
		
		if (null == pgsStatusSalesQuote) {
			pgsStatusSalesQuote = findQuoteFromUnderEvaluationPage(statusSearchPage, quoteNum);
		}
		
		return pgsStatusSalesQuote;
	}
	
	/**
	 * click status link
	 * @author Li Yu
	 * @date April 11, 2013
	 * @param importSalesQuotePage
	 * @return
	 */
	public PGSHomePage gotoHomePageFromStatus(PGSStatusSalesQuote pgsStatusSalesQuote){
		
		PGSHomePage pgsHomePage = null;
		pgsHomePage = pgsStatusSalesQuote.goHomePage();
		
		return pgsHomePage;
	}
	
}
