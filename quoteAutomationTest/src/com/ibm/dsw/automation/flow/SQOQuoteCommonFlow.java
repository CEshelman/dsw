package com.ibm.dsw.automation.flow;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.automation.common.BaseTestCase;
import com.ibm.dsw.automation.common.FunctionIdProvider;
import com.ibm.dsw.automation.common.SQOPropertiesBean;
import com.ibm.dsw.automation.common.SaasParts;
import com.ibm.dsw.automation.common.TestUtil;
import com.ibm.dsw.automation.pageobject.pgs.ImportSalesQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.MyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.pgs.PartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.pgs.ServiceConfigurePage;
import com.ibm.dsw.automation.pageobject.sqo.ActiveSoftwareAsAServiceTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOApproveQueuePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOBrowsePartsTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOBrowseSoftwareAsServiceTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOCompareQuotesPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOCreateNewPassportAdvantageExpressCustomerPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOCreateQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOCustomerAndPartnerTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayApprovalQueueTrackerPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayCustListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayDistributorListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayResellerListPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayStatusSearchReslutPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayStatusTrackerPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODisplayStatusTrackerSettingsPage;
import com.ibm.dsw.automation.pageobject.sqo.SQODraftRQCustPartnerTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOEditProvisioningForm;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsSelectTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOFindPartsTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOHomePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOJumpPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOLoginPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMonthySwBaseConfiguratorPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOMyCurrentQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOOSearchCustPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOPartsAndPricingTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOQuoteSearchIBMSelfPage;
import com.ibm.dsw.automation.pageobject.sqo.SQORQSearchReslutPage;
import com.ibm.dsw.automation.pageobject.sqo.SQORQSummaryTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQORetrieveSavedSalesQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectADistributorPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectAResellerPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuoteCPTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSelectedQuotePPTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSpecialBidTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOStatusSearchTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitCurrDraftQuotePage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitSQSalesInfoTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmitSQSpecialBidTabPage;
import com.ibm.dsw.automation.pageobject.sqo.SQOSubmittedQuoteExecSummaryTabPage;
import com.ibm.dsw.automation.vo.LogonInf;
import com.ibm.dsw.automation.vo.ProvisioinID;


@SuppressWarnings("deprecation")
public class SQOQuoteCommonFlow extends QuoteFlow{
	/*public static WebdriverLogger loggerContxt = WebdriverLogger
			.getLogger(SQOQuoteCommonFlow.class.getName());*/
	
	public SQOQuoteCommonFlow() {
		init();
	}

	SQOPropertiesBean propBean = null;

	public SQOPropertiesBean getPropBean() {
		return propBean;
	}

	public void setPropBean(SQOPropertiesBean propBean) {
		this.propBean = propBean;
	}

	/**
	 * login in SQO
	 * 
	 * <p>
	 * if there is no value set in properties for logonUser,it will use the
	 * setting.properties' user.
	 * 
	 * @param
	 * @return SQOHomePage
	 */	
	public SQOHomePage loginSqo(LogonInf logonInf) {
		loggerContxt.info("Login in SQO.....");
		
		loggerContxt.info("Login in SQO::" + logonInf.getSqoUrl());

		SQOLoginPage lp = new SQOLoginPage(this.driver);
		lp.setLoggerContxt(loggerContxt);
		
		if (StringUtils.isBlank(propBean.getLogonUser())) {
			propBean.setLogonUser(logonInf.getSqoLogonUser());
			boolean encryptFlg = Boolean.parseBoolean(getProperty("encrypted_mode"));
			if (encryptFlg) {
				String encryptedLoginPasswd = FunctionIdProvider.getPWDForFuncId(logonInf.getSqoLogonUser());
				propBean.setPassword(encryptedLoginPasswd);
			}else{
				propBean.setPassword(logonInf.getSqoUserPwd());
			}
		}

		loggerContxt.info("Login in user ......" + propBean.getLogonUser());
		SQOHomePage sqoHomePage = lp.loginAs(propBean.getLogonUser(),
				propBean.getPassword());

		loggerContxt
				.info("Login in finished ,current page is SQOHomePage......");
		return sqoHomePage;
	}
	
	public SQOHomePage loginPgsViaSqo(LogonInf logonInf) {
		loggerContxt.info("Login in PGS.....");
		
		loggerContxt.info("Login in PGS::" + logonInf.getSqoUrl());
		
		SQOLoginPage lp = new SQOLoginPage(this.driver);
		lp.setLoggerContxt(loggerContxt);
		
		if (StringUtils.isBlank(propBean.getLogonUser())) {
			propBean.setLogonUser(logonInf.getSqoLogonUser());
		}
		
		if (StringUtils.isBlank(propBean.getPassword())) {
			propBean.setPassword(logonInf.getSqoUserPwd());
		}
		
		loggerContxt.info("Login in user ......" + propBean.getLogonUser());
		SQOHomePage sqoHomePage = lp.loginPgsViaSqo(propBean.getLogonUser(),
				propBean.getPassword());
		
		loggerContxt
		.info("Login in finished ,current page is SQOHomePage......");
		return sqoHomePage;
	}

	public SQOHomePage loginSqo(LogonInf logonInf, String logonFlg) {
		loggerContxt.info("Login in SQO.....");

		if (BaseTestCase.LOGON_IN_SQO_VIA_SQO_JUMP_PAGE_BY_URL.equals(logonFlg)) {
			getDriver().close();
			String url = getProperty(getProperty("env")+".sqo_url");
			url += "?user=" + getLogonInf().getSqoLogonUser() + "&tsAccessLevel=" + getLogonInf().getAccessLevel();
			loggerContxt.info(String.format("Start to login via jump page::%s",url));
			reset(url);
			SQOHomePage page = new SQOHomePage(driver);
			return page;
		}
		
		if (BaseTestCase.LOGON_IN_SQO_VIA_SQO_JUMP_PAGE.equals(logonFlg)) {
			getDriver().close();
			reset(getProperty(getProperty("env")+".sqo_jump_url"));
		}
		
		SQOLoginPage lp = new SQOLoginPage(this.driver);
		
		if (StringUtils.isBlank(propBean.getLogonUser())) {
			propBean.setLogonUser(getLogonInf().getSqoLogonUser());
			propBean.setPassword(getLogonInf().getSqoUserPwd());
		}

		loggerContxt.info("Login in user ......" + propBean.getLogonUser());
		SQOHomePage sqoHomePage = lp.loginAs(propBean.getLogonUser(),
				propBean.getPassword());

		loggerContxt
				.info("Login in finished ,current page is SQOHomePage......");
		return sqoHomePage;
	}
	
	public SQOHomePage loginSqo(BaseTestCase testcase, LogonInf logonInf, String logonFlg) {
		loggerContxt.info("Login in SQO.....");
		
		if (BaseTestCase.LOGON_IN_SQO_VIA_SQO_JUMP_PAGE_BY_URL.equals(logonFlg)) {
			String url = getProperty(getProperty("env")+".sqo_url");
			url += "?user=" + getLogonInf().getSqoLogonUser() + "&tsAccessLevel=" + getLogonInf().getAccessLevel();
			loggerContxt.info(String.format("Start to login via jump page by url::%s",url));
			reset(testcase,url);
			SQOHomePage homePage = new SQOHomePage(driver);
			return homePage;
		}
		
		if (BaseTestCase.LOGON_IN_SQO_VIA_SQO_JUMP_PAGE.equals(logonFlg)) {
			String newUrl = getProperty(getProperty("env")+".sqo_jump_url");
			loggerContxt.info(String.format("Start to login via jump page::%s",newUrl));
			reset(testcase,newUrl);
		}
		
		SQOLoginPage lp = new SQOLoginPage(this.driver);
		
		if (StringUtils.isBlank(propBean.getLogonUser())) {
			propBean.setLogonUser(getLogonInf().getSqoLogonUser());
			propBean.setPassword(getLogonInf().getSqoUserPwd());
		}
		
		loggerContxt.info("Login in user ......" + propBean.getLogonUser());
		SQOHomePage sqoHomePage = lp.loginAs(propBean.getLogonUser(),
				propBean.getPassword());
		
		loggerContxt
		.info("Login in finished ,current page is SQOHomePage......");
		return sqoHomePage;
	}
	
	/**
	 * create a draft quote based on the properties set in the file<br/>
	 * 
	 * <p>
	 * based on properties set in the file:lob/country/acqrtnCode
	 * 
	 * @param sqoHomePage
	 *            SQOHomePage
	 * @return SQOMyCurrentQuotePage
	 */
	public SQOMyCurrentQuotePage createDraftQuote(SQOHomePage sqoHomePage) {
		loggerContxt.info(" Create new sales quote.....");

		SQOCreateQuotePage cq = sqoHomePage.gotoCreateQuote();

		// Create a SQO quote
		Map<String, String> quote = new HashMap<String, String>();
		quote.put("PROG_CODE", propBean.getLob());
		quote.put("CNTRY_CODE", propBean.getCountry());
		quote.put("ACQRTN_CODE", propBean.getAcqrtnCode());

		SQOMyCurrentQuotePage currentQuotePage = cq.createQuote(quote);
		loggerContxt
				.info("Create new sales quote finished,current page is SQOMyCurrentQuotePage.....");

		return currentQuotePage;
	}

	/**
	 * select a customer/partner/distributor based on the properties set in the
	 * file<br/>
	 * 
	 * <p>
	 * 
	 * <p>
	 * 
	 * 
	 * based on properties set in the file:
	 * 
	 * @param currentQuotePage
	 *            SQOMyCurrentQuotePage
	 * @return currentQuotePage
	 */
	public SQOMyCurrentQuotePage createANewCustomer(
			SQOMyCurrentQuotePage currentQuotePage) {
		loggerContxt.info("Create new customer start.....");
		SQOCreateNewPassportAdvantageExpressCustomerPage createCustpage = currentQuotePage
				.createANewCustomer();
		currentQuotePage = createCustpage.createAPAECustomer(propBean);
		loggerContxt
				.info(" Create new customer finished,current page is SQOMyCurrentQuotePage.....");
		return currentQuotePage;
	}

	/**
	 * select a customer/partner/distributor based on the properties set in the
	 * file<br/>
	 * 
	 * <p>
	 * select customer if customerNum set,use customerNum to search as default,
	 * both customerNum and customerName set,use customerNum; if customerName
	 * set,use customerNum to search; both empty,don't search customer. <br>
	 * the same image for seller/distributor
	 * <p>
	 * 
	 * 
	 * based on properties set in the file:
	 * 
	 * @param currentQuotePage
	 *            SQOMyCurrentQuotePage
	 * @return currentQuotePage
	 */
	public SQOMyCurrentQuotePage processCustPartnerTab(
			SQOMyCurrentQuotePage currentQuotePage) {
		loggerContxt
				.info("select a customer/partner/distributor based on  the properties set in the file start.....");

		/*********************************************** select customer start **************************************************/
		currentQuotePage = createCustomer(currentQuotePage);
		/********************************************** select customer end ******************************************************/

		/********************************************** select reseller start **************************************************/
		currentQuotePage = createReseller(currentQuotePage);
		/********************************************* select reseller end ******************************************************/

		/********************************************** select distributor start **************************************************/
		currentQuotePage = createDistributor(currentQuotePage);
		/********************************************* select distributor end ******************************************************/

		loggerContxt
				.info("create quote page finished,current page is SQOMyCurrentQuotePage.....");
		return currentQuotePage;
	}

	public SQOMyCurrentQuotePage createCustomer(SQOMyCurrentQuotePage currentQuotePage) {
		// if customerName set,use customerNum to search;both customerNum and
		// customerName set,use customerNum;
		// both empty,don't search customer
		// if customerNum set,use customerNum to search as default,
		if ((StringUtils.isNotBlank(propBean.getCustomerNum()))
				|| (StringUtils.isNotBlank(propBean.getCustomerNum()))) {

			// Find an existing customer
			SQOOSearchCustPage searchCustPage = currentQuotePage
					.findCustbyClick();
			loggerContxt.info("go to search customer page.....");
			SQODisplayCustListPage displayCustListPage = null;
			if (StringUtils.isNotBlank(propBean.getCustomerNum())) {
				// display customer
				displayCustListPage = searchCustPage
						.displaySQOCustomerListBySiteNum(propBean
								.getCustomerNum());
				loggerContxt.info("display customer page by CustomerNum.....");
			}

			if ((StringUtils.isBlank(propBean.getCustomerNum()))
					&& (StringUtils.isNotBlank(propBean.getCustomerName()))) {
				// display customer
				displayCustListPage = searchCustPage
						.displayCustomerListByName(propBean.getCustomerName());
				loggerContxt.info("display customer page by CustomerName.....");
			}

			// select customer and back current quote page
			currentQuotePage = displayCustListPage.selectCustomer();
			loggerContxt
					.info("select customer and back current quote page.....");
		}
		
		// save current draftquote number for future use
		propBean.setDraftQuoteNum(currentQuotePage.getQuoteNum());
		loggerContxt.info("current quote number....."
				+ currentQuotePage.getQuoteNum());
		// Enter "xprs//@Test.com" in the "* Email: " field under Quote contact
		// section.
		currentQuotePage.fillEmailAdr(propBean.getCustmail());
		
		// set Quote expiration date
		currentQuotePage.selectExpirationDate(30);
		currentQuotePage.selectReqstdArrivlDate(30);
		
		if ("OEM".equals(propBean.getLob())) {
			currentQuotePage.selectOemAgrmntType();
			currentQuotePage.selectOemBidType();
		} else if ("FCT".equals(propBean.getLob())) {
			currentQuotePage.selectQuoteClassification();
		}
		
		return currentQuotePage;
	}
	
	public SQOMyCurrentQuotePage createReseller(SQOMyCurrentQuotePage currentQuotePage) {
		// if resellerName set,use resellerNum to search;both resellerNum and
		// resellerName set,use resellerNum;
		// both empty,don't search reseller
		// if resellerNum set,use resellerNum to search as default,
		if ((StringUtils.isNotBlank(propBean.getResellerNum()))
				|| (StringUtils.isNotBlank(propBean.getResellerName()))) {
			// Find an existing reseller
			SQOSelectAResellerPage searchResellerPage = currentQuotePage
					.findResellertbyClick();
			loggerContxt.info("go to search reseller page.....");
			SQODisplayResellerListPage displayResellerListPage = null;
			if (StringUtils.isNotBlank(propBean.getResellerNum())) {
				// display reseller
				displayResellerListPage = searchResellerPage
						.displayResellerByNum(propBean.getResellerNum());
				loggerContxt.info("display reseller page by resellerNum.....");
			}

			if ((StringUtils.isBlank(propBean.getResellerNum()))
					&& (StringUtils.isNotBlank(propBean.getResellerName()))) {
				// display reseller
				displayResellerListPage = searchResellerPage
						.displayResellerByNum(propBean.getResellerName());
				loggerContxt.info("display reseller page by resellerName.....");
			}
			// select reseller and back current quote page
			currentQuotePage = displayResellerListPage.selectReseller();
			loggerContxt
					.info("select a reseller and back to current quote page.....");
		}
		
		return currentQuotePage;
	}
	
	public SQOMyCurrentQuotePage createDistributor(SQOMyCurrentQuotePage currentQuotePage) {
		// if distributorName set,use distributorNum to search;both
		// distributorNum and distributorName set,use distributorNum;
		// both empty,don't search distributor
		// if distributorNum set,use distributorNum to search as default,
		if ((StringUtils.isNotBlank(propBean.getDistributorNum()))
				|| (StringUtils.isNotBlank(propBean.getDistributorName()))) {
			// Find distributor
			SQOSelectADistributorPage searchDistributorPage = currentQuotePage
					.findDistributorbyClick();
			loggerContxt.info("go to search distributor page.....");
			SQODisplayDistributorListPage displayDistributorListPage = null;
			if (StringUtils.isNotBlank(propBean.getDistributorNum())) {
				// display distributor
				displayDistributorListPage = searchDistributorPage
						.displayDistributorListBySiteNum(propBean
								.getDistributorNum());
				loggerContxt
						.info("display distributor page by distributorNum.....");
			}

			if ((StringUtils.isBlank(propBean.getDistributorNum()))
					&& (StringUtils.isNotBlank(propBean.getDistributorName()))) {
				// display distributor
				displayDistributorListPage = searchDistributorPage
						.displayCustomerListByName(propBean
								.getDistributorName());
				loggerContxt
						.info("display distributor page by distributorName.....");
			}
			// select distributor and back current quote page
			currentQuotePage = displayDistributorListPage.selectDistributor();
			loggerContxt
					.info("select a distributor and back to current quote page.....");
		}
		
		if (StringUtils.isNotBlank(propBean.getIsDirectPartner()) && "PAUN".equals(propBean.getLob())) {
			currentQuotePage.selectDirectChnl();
		}
		
		return currentQuotePage;
	}
	
	/**
	 * remove selected reseller
	 * @param currentQuotePage
	 */
	public void removeReseller(MyCurrentQuotePage currentQuotePage) {
		currentQuotePage.clearSelectedResellerLinkClick();
	}
	
	/**
	 * remove selected distributor
	 * @param currentQuotePage
	 */
	public void removeDistributor(MyCurrentQuotePage currentQuotePage) {
		currentQuotePage.clearSelectedDistributorLinkClick();
	}
	
	/**
	 * add software part through search(except appliance pat) to draft quote
	 * based on the properties set in the file<br/>
	 * 
	 * <p>
	 * based on properties set in the file:
	 * 
	 * @param currentQuotePage
	 *            SQOMyCurrentQuotePage
	 * @return partsAndPricingTab
	 */
	public SQOPartsAndPricingTabPage addSoftwareParts(
			SQOMyCurrentQuotePage currentQuotePage) {
		loggerContxt.info("search parts.....");

		// Select the last date of the current month as the
		// "Quote expiration date"
		currentQuotePage.selectExpirationDate(30);

		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage
				.goToPartsAndPricingTab();

		if ((StringUtils.isNotBlank(propBean.getPartList1()))
				|| StringUtils.isNotBlank(propBean.getPartList2())
				|| StringUtils.isNotBlank(propBean.getPartDes())) {
			SQOFindPartsTabPage findPartsTabPage = partsAndPricingTab
					.findPartsLinkClick();

			SQOFindPartsSelectTabPage findPartsSelect = null;
			if ((StringUtils.isNotBlank(propBean.getPartList1()))) {
				findPartsSelect = findPartsTabPage.findPartsLinkClick(propBean
						.getPartList1());
				loggerContxt.info("search first of parts:....."
						+ propBean.getPartList1());
				findPartsTabPage = findPartsSelect
						.selectPartsAndChgCriteriaClick(propBean.getPartList1());
		
			}

			if ((StringUtils.isNotBlank(propBean.getPartList2()))) {
				findPartsSelect = findPartsTabPage.findPartsLinkClick(propBean
						.getPartList2());
				loggerContxt.info("search the other parts:....."
						+ propBean.getPartList2());
				findPartsSelect.selectPartsAndChgCriteriaClick(propBean.getPartList2());
			}

			if ((StringUtils.isNotBlank(propBean.getPartDes()))) {
				findPartsSelect = findPartsTabPage.findPartsByPartDes(propBean
						.getPartDes());
				loggerContxt.info("search the parts by :....."
						+ propBean.getPartDes());
				findPartsSelect.selectPartsAndChgCriteriaClick(propBean.getPartDes());
			}

			partsAndPricingTab = findPartsSelect.rtn2DraftQuote();
			
			if ((StringUtils.isNotBlank(propBean.getPartList1()))) {
				String partNum = propBean.getPartList1().split(",")[0];
				setPartsDatas(partsAndPricingTab, partNum);
			}

		}

		loggerContxt
				.info("create quote page finished,current page is SQOMyCurrentQuotePage.....");

		return partsAndPricingTab;
	}
	
	
	

	/**
	 * add software part(browse part) to draft quote based on the properties set
	 * in the file<br/>
	 * 
	 * <p>
	 * based on properties set in the file:
	 * 
	 * @param partsAndPricingTab
	 *            SQOPartsAndPricingTabPage
	 * @return partsAndPricingTab
	 */
	public SQOPartsAndPricingTabPage browsePart(
			SQOPartsAndPricingTabPage partsAndPricingTab) {

		// Click on 'Browse parts' tab
		SQOBrowsePartsTabPage browsePartsTab = partsAndPricingTab
				.browsePartsLinkClick();
		loggerContxt.info("Click on 'Browse parts' tab.....");

		/*
		 * Click on Brand Lotus Click on Lotus 123 Click on License + SW
		 * Subscription & Support Check the checkbox of the first part listed
		 * Click the "Add selected parts to draft quote" link
		 */
		if ("FCT".equals(propBean.getLob())) {
			browsePartsTab.browseLotusLiveNotesPartsTab();
		} else {
			browsePartsTab.browseLotusPartsTab();
		}
		browsePartsTab.addSelectedPartsToDraftQuoteLinkClick();

		partsAndPricingTab = browsePartsTab.returnToDraftQuoteLinkLinkClick();
		loggerContxt
				.info("Click on Brand Lotus Click on Lotus 123 Click on License + SW Subscription & Support "
						+ "Check the checkbox of the first part listed Click the 'Add selected parts to draft quote' link.....");

		// Under Parts and pricing section, click on the "Recalculate quote"
		// link
		partsAndPricingTab=partsAndPricingTab.recalculateQuotePPTab();
	

		return partsAndPricingTab;

	}

	/**
	 * add software part(browse part) to draft quote based on the properties set
	 * in the file<br/>
	 * 
	 * <p>
	 * based on properties set in the file:
	 * 
	 * @param partsAndPricingTab
	 *            SQOPartsAndPricingTabPage
	 * @return partsAndPricingTab
	 */
	public SQOPartsAndPricingTabPage browsePart(
			SQOMyCurrentQuotePage currentQuotePage) {

		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage
				.goToPartsAndPricingTab();
		return browsePart(partsAndPricingTab);

	}

	/**
	 * DOC Add SAAS part to quote.
	 */
	public SQOPartsAndPricingTabPage addSaasPartToQuote(
			SQOMyCurrentQuotePage currentQuotePage) {

		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage
				.goToPartsAndPricingTab();
		return addSaasPartToQuote(partsAndPricingTab);

	}

	/**
	 * DOC Add SAAS part to quote.
	 */
	public SQOPartsAndPricingTabPage addSaasPartToQuote(
			SQOPartsAndPricingTabPage page) {
		
		return addAndConfigureSaasPart(page,SaasParts.SAAS_PART);
		
	}
	
	private SQOPartsAndPricingTabPage addAndConfigureSaasPart(SQOPartsAndPricingTabPage page,SaasParts part){
		/**
		 * get the text of link: Browse Software as a Service
		 */
		String browerSaasLinkText = propBean.getBrowerSaas();
		loggerContxt.info("verify current page whether having this link: "
				+ browerSaasLinkText);
		
		
		SQOBrowseSoftwareAsServiceTabPage browseSoftwareAsServiceTabPage = page
				.browseSoftwareAsAServiceLinkClick();

		browseSoftwareAsServiceTabPage.waitForElementLoading(new Long(25000));

		loggerContxt.info("browse SAAS page finished.....");
		// check if there are some Saas offerings
		// There are no Software as a Service offerings to configure.
		if (selenium.isTextPresent(propBean.getNoSaasOfferingsMsg())) {
			loggerContxt.info(propBean.getNoSaasOfferingsMsg());
			browseSoftwareAsServiceTabPage.returnToDraftQuoteLinkClick();
			return page;
		}
		
		browseSoftwareAsServiceTabPage.selectAgreement();
		
		switch(part){
		case SAAS_PART:
			browseSoftwareAsServiceTabPage.browserAndConfigureService( propBean.getSaasPID());
			break;
		case BRAND_SERVICE_ONE_SAAS_PART:
			browseSoftwareAsServiceTabPage.browserAndConfigureService(
					propBean.getSaasPIDBrandServiceOneInf(), propBean.getSaasPIDBrandServiceOne());
			break;
		case BRAND_SERVICE_TWO_SAAS_PART:
			browseSoftwareAsServiceTabPage.browserAndConfigureService(
					propBean.getSaasPIDBrandServiceTwoInf(), propBean.getSaasPIDBrandServiceTwo());
			break;
		case OTHER_SAAS_PART:
			browseSoftwareAsServiceTabPage.browserAndConfigureService(
					propBean.getSaasPIDOtherInf(), propBean.getSaasPIDOther());
			
		}
		
		browseSoftwareAsServiceTabPage.editSaasPart(propBean.getSaasPartQuantity());
		// open saas part and configure it.
		// browseAndAddSaasPart(page, pathaArray, components);

		return page;
	}
	
	/**
	 * DOC Add SAAS part to quote.
	 */
	public SQOPartsAndPricingTabPage addOtherSaasPartToQuote(
			SQOPartsAndPricingTabPage page) {
	
		return addAndConfigureSaasPart(page, SaasParts.OTHER_SAAS_PART);
	}
	
	/**
	 * DOC Add SAAS part to quote.
	 */
	public SQOPartsAndPricingTabPage addOtherSaasPartToQuote(
			SQOMyCurrentQuotePage currentQuotePage) {

		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage
				.goToPartsAndPricingTab();
		return addOtherSaasPartToQuote(partsAndPricingTab);

	}
	
	/**
	 * Add 
	 */
	public SQOPartsAndPricingTabPage addBrandServiceOneSaasPartToQuote(
			SQOPartsAndPricingTabPage page) {
		
		return addAndConfigureSaasPart(page, SaasParts.BRAND_SERVICE_ONE_SAAS_PART);
	}
	
	public SQOPartsAndPricingTabPage addBrandServiceOneSaasPartToQuote(
			SQOMyCurrentQuotePage currentQuotePage) {

		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage
				.goToPartsAndPricingTab();
		return addBrandServiceOneSaasPartToQuote(partsAndPricingTab);

	}
	
	/**
	 * Add 
	 */
	public SQOPartsAndPricingTabPage addBrandServiceTwoSaasPartToQuote(
			SQOPartsAndPricingTabPage page) {
		
		return addAndConfigureSaasPart(page, SaasParts.BRAND_SERVICE_TWO_SAAS_PART);
	}
	
	
	public SQOPartsAndPricingTabPage addBrandServiceTwoSaasPartToQuote(
			SQOMyCurrentQuotePage currentQuotePage) {

		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage
				.goToPartsAndPricingTab();
		return addBrandServiceTwoSaasPartToQuote(partsAndPricingTab);

	}
	/**
	 * remove SAAS part to quote.
	 */
	public SQOPartsAndPricingTabPage removeSoftWarePart(
			SQOPartsAndPricingTabPage page) {
		loggerContxt.info("remove the part.....");
		page.deleteSoftwarePart("D5CF5LL");

		return page;
	}
	
	
	/**
	 * remove SAAS part from quote.
	 */
	public SQOPartsAndPricingTabPage removeSaasPart(
			SQOPartsAndPricingTabPage page) {
	
		page.removeSaasPart();
		loggerContxt.info("remove SAAS part from quote......");
		return page;
	}
	
	/**
			4.in part and pricing tab,selects "yes" to modify the service date question (default is "no") for a configuration in the quote 
		validation points:
		
		Modify service dates?                               radio button works well;when the service date question (default is "no"),no option list of 'If desired select service start or end date' display;
		Estimated provisioning days                         the input text works well;
		If desired select service start or end date         the option works well,if wrong input,the err msg comes out(TODO);After recaculating,the selected date could display the same as you selected
		the term field                                      After recaculating,the selected date could display the correct value as you selected(TODO)
		
	 * <p>
	 * 
	 * @param partsAndPricingTab
	 *            SQOPartsAndPricingTabPage
	 * @return partsAndPricingTab
	 */
	public SQOPartsAndPricingTabPage changeModifyServiceDate(
			SQOPartsAndPricingTabPage partsAndPricingTab,String flg104106) {

		if ("OEM".equals(propBean.getLob())
		 || "FCT".equals(propBean.getLob())) {
			partsAndPricingTab.validateModifyServiceDateExist(false);
			return partsAndPricingTab;
		}
		
		// Go to SalesInf Tab)
		loggerContxt.info("validate when modify the service date  No is checked begin.....");
		partsAndPricingTab.validateModifyServiceDateNo(flg104106);
		loggerContxt.info("validate when modify the service date  No is checked end.....");
		
		loggerContxt.info("validate when modify the service date  Yes is checked begin.....");
		partsAndPricingTab = partsAndPricingTab.validateModifyServiceDateYes(propBean.getSaasPID(), propBean.getEstProvisioningDays(), propBean.getEstDate(), flg104106);
		loggerContxt.info("validate when modify the service date  Yes is checked end.....");

		
		return partsAndPricingTab;
	}
	public SQOPartsAndPricingTabPage changeModifyServiceDate(
			SQOPartsAndPricingTabPage partsAndPricingTab,String type,  boolean yesRadio, int provDays, Calendar quoteStartDate, Calendar expirDate, Calendar estOrdDate, Calendar extenDate ) {


		// Go to SalesInf Tab)
		loggerContxt.info("validate when modify the service date  No is checked begin.....");
//		partsAndPricingTab.validateModifyServiceDateNo(propBean.getSaasPID());
		loggerContxt.info("validate when modify the service date  No is checked end.....");
		
		loggerContxt.info("validate when modify the service date  Yes is checked begin.....");
		partsAndPricingTab = partsAndPricingTab.setExtenDateValue(propBean.getSaasPID(),type, yesRadio, provDays, quoteStartDate, expirDate, estOrdDate, extenDate );
		loggerContxt.info("validate when modify the service date  Yes is checked end.....");

		
		return partsAndPricingTab;
	}
	
	public SQOPartsAndPricingTabPage setDates(
			SQOPartsAndPricingTabPage partsAndPricingTab) {
		int estDate = Integer.valueOf(propBean.getEstDate());
		int estProvisioningDays = Integer.valueOf(propBean.getEstProvisioningDays());
		
		String[] provisioningDateArray = TestUtil.addTargetDayFromCurrentDaay(estDate + estProvisioningDays);
		
		int year = Integer.valueOf(provisioningDateArray[0]);
		int month = 0;
		int day = 0;
		// latest provisioning date
		if (Integer.valueOf(provisioningDateArray[2]) != 1) {
			month = Integer.valueOf(provisioningDateArray[1]) + 1;
			day = 1;
		} else if (Integer.valueOf(provisioningDateArray[2]) == 1) {
			month = Integer.valueOf(provisioningDateArray[1]);
			day = 1;
		}
		
		partsAndPricingTab.selectExpirationDate(estDate, TestUtil.string2Date(year + "-" + month + "-" + day));
		
		// earliest provisioning date
		if (Integer.valueOf(provisioningDateArray[2]) != 2) {
			month = Integer.valueOf(provisioningDateArray[1]);
			day = 2;
		} else if (Integer.valueOf(provisioningDateArray[2]) == 2) {
			month = Integer.valueOf(provisioningDateArray[1]) - 1;
			day = 2;
		}
		
		partsAndPricingTab.selectStartDate(estDate, TestUtil.string2Date(year + "-" + month + "-" + day));
		
		return partsAndPricingTab;
	}
	
	
	public SQOPartsAndPricingTabPage dateSetting(
			SQOPartsAndPricingTabPage partsAndPricingTab) {
		int estDate = Integer.valueOf(propBean.getEstDate());
		partsAndPricingTab.selectEstimatedOrderDay(estDate - 1);
		partsAndPricingTab.selectStartDate(0, new Date());
		partsAndPricingTab.selectExpirationDate(estDate, new Date());
//		partsAndPricingTab.selectExtensionDate(estDate - 1);
		partsAndPricingTab.setProvisioningDays(propBean.getEstProvisioningDays());
		return partsAndPricingTab;
	}
	/**
	 * edit sales info in draft quote based on the properties set in the file<br/>
	 * 
	 * <p>
	 * based on properties set in the file:
	 * 
	 * @param partsAndPricingTab
	 *            SQOPartsAndPricingTabPage
	 * @return salesTab
	 */
	public SQOSalesInfoTabPage editSalesInfoTab(
			SQOPartsAndPricingTabPage partsAndPricingTab) {

		// Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SQOSalesInfoTabPage salesTab = partsAndPricingTab.gotoSalesInfoTab();
		loggerContxt.info("Go to SalesInf Tab end.....");

		salesTab.enterSalesInf(propBean.getBriefTitle(),
				propBean.getQuoteDesc(), propBean.getBusOrgCode());

		salesTab.addQuoteEditor(propBean.getQuoteeditormail());
		// press "Save" button, then press "Save" button again
		salesTab.saveDraftQuoteLink();
		return salesTab;
	}
	
	public void editSpcialBidInf(SQOPartsAndPricingTabPage partsAndPricingTab) {
		SQOSalesInfoTabPage salesTab = partsAndPricingTab.gotoSalesInfoTab();
		
		// go to Special Bid Tab
		loggerContxt.info("go to Special Bid Tab.....");
		SQOSpecialBidTabPage spcialBidTab = salesTab.goToSpecialBidTabClick();
		
		//Justification Summary
		spcialBidTab.enterSpcialBidInf(propBean.getJustificationSummary());
	}

	/**
	 * edit special bid in draft quote based on the properties set in the file<br/>
	 * 
	 * <p>
	 * based on properties set in the file:
	 * 
	 * @param salesTab
	 *            SQOSalesInfoTabPage
	 * @return SQOMyCurrentQuotePage
	 */
	public SQOMyCurrentQuotePage processSpecialBidTab(
			SQOSalesInfoTabPage salesTab) {

		// go to Special Bid Tab
		loggerContxt.info("go to Special Bid Tab.....");
		SQOSpecialBidTabPage spcialBidTab = salesTab.goToSpecialBidTabClick();

		spcialBidTab.enterSpcialBidInf(propBean.getJustificationSummary());
		SQOMyCurrentQuotePage currentQuotePage = spcialBidTab.rtnToCPTab();
		return currentQuotePage;
	}

	/**
	 * <br/>
	 * 
	 * <p>
	 * based on properties set in the file:
	 * 
	 * @param currentQuotePage
	 *            SQOMyCurrentQuotePage
	 * @return SQOMyCurrentQuotePage
	 */
	public SQOMyCurrentQuotePage retrieveSavedSalesQuotePage(
			SQOSalesInfoTabPage salesTab) {
		SQOMyCurrentQuotePage currentQuotePage = salesTab.rtnToCPTab();
		
		

		return retrieveSavedSalesQuotePage(currentQuotePage);
	}
	
	public SQOPartsAndPricingTabPage addSassPartInActiveAassTab(
			SQOMyCurrentQuotePage currentQuotePage) {
		SQOPartsAndPricingTabPage ppTab = currentQuotePage.goToPartsAndPricingTab();
		SQOCustomerAndPartnerTabPage cpTab = currentQuotePage.goToCustomerAndPartnerTab();
		ActiveSoftwareAsAServiceTabPage actSaasTab = cpTab.softwareAsAServiceLinkClick();
		ServiceConfigurePage scPage = actSaasTab.updateConfigurationLinkClick();
		scPage.configureService();
		ppTab.setConfigurationID(scPage.getConfigurationID());
		return ppTab;
	}
	
	/**
	 * <br/>
	 * 
	 * <p>
	 * based on properties set in the file:
	 * 
	 * @param currentQuotePage
	 *            SQOMyCurrentQuotePage
	 * @return SQOMyCurrentQuotePage
	 */
	public SQOMyCurrentQuotePage retrieveSavedSalesQuotePage(
			SQOMyCurrentQuotePage currentQuotePage) {

		SQORetrieveSavedSalesQuotePage savedSalesQuote = currentQuotePage
				.goSQORetrieveSavedSalesQuoteTab();
		currentQuotePage = savedSalesQuote.goViewDetailSavedQuote();

		return currentQuotePage;
	}

	/**
	 * <br/>
	 * 
	 * <p>
	 * based on properties set in the file:
	 * 
	 * @param currentQuotePage
	 *            SQOMyCurrentQuotePage
	 * @return SQOMyCurrentQuotePage
	 */
	public SQOSelectedQuoteCPTabPage findQuoteByNum(SQOMyCurrentQuotePage currentQuotePage) { 
		SQOStatusSearchTabPage statusSearchPO = currentQuotePage.gotoStatusSearch();

		loggerContxt.info("find quotes using the quote number:....."
				+ propBean.getDraftQuoteNum());
		SQODisplayStatusSearchReslutPage resultPO = statusSearchPO
				.findQuoteByNum(propBean.getDraftQuoteNum());
		loggerContxt.info("view the detail CP.....");
		SQOSelectedQuoteCPTabPage viewDetailCPPO = resultPO.goDispQuoteReslt();

		return viewDetailCPPO; 
	}
	
	/**
	 * @param currentQuotePage
	 * @param quoteNum
	 * @return SQOSelectedQuoteCPTabPage
	 * @author Marwa Arafa
	 */
	public SQOSelectedQuoteCPTabPage findQuoteByNum(SQOMyCurrentQuotePage currentQuotePage , String quoteNum) { 
		SQOStatusSearchTabPage statusSearchPO = currentQuotePage.gotoStatusSearch();

		loggerContxt.info("find quotes using the quote number:....."
				+ quoteNum);
		SQODisplayStatusSearchReslutPage resultPO = statusSearchPO
				.findQuoteByNum(quoteNum);
		loggerContxt.info("view the detail CP.....");
		SQOSelectedQuoteCPTabPage viewDetailCPPO = resultPO.viewQuoteDetail();

		return viewDetailCPPO; 
	}
	public SQOSelectedQuoteCPTabPage findQuoteByNum(SQOHomePage sqoHomePage,String quoteNum) {
		SQOStatusSearchTabPage statusSearchPO = sqoHomePage.gotoStatus();

		loggerContxt.info("find quotes using the quote number:....."
				+ quoteNum);
		SQODisplayStatusSearchReslutPage resultPO = statusSearchPO
				.findQuoteByNum(quoteNum);
		loggerContxt.info("view the detail CP.....");
		SQOSelectedQuoteCPTabPage viewDetailCPPO = resultPO.viewQuoteDetail();

		return viewDetailCPPO;
	}
	/**
	 * <br/>
	 * 
	 * <p>
	 * based on properties set in the file:
	 * 
	 * @param sbmdCPTab
	 *            SQOSelectedQuoteCPTabPage
	 * @return SQOMyCurrentQuotePage
	 */
	public SQOSubmitSQSpecialBidTabPage checkSubmittedQuote(SQOSelectedQuoteCPTabPage sbmdCPTab) { 
		SQOSelectedQuotePPTabPage  sbmdPPTab= sbmdCPTab.goToPPTab();

		loggerContxt.info("go the part and pricing tab.....");
		SQOSubmitSQSalesInfoTabPage sbmdSaleInfTab = sbmdPPTab.goToSalesInfoTab();
		loggerContxt.info("view the detail CP.....");
		SQOSubmitSQSpecialBidTabPage sbmdSpbidTab = sbmdSaleInfTab.goToSepcialBidTab();

		return sbmdSpbidTab; 
	}
	
	/**
	 * <br/>
	 * 
	 * <p>
	 * based on properties set in the file:
	 * 
	 * @param currentQuotePage
	 *            SQOMyCurrentQuotePage
	 * @return SQOMyCurrentQuotePage
	 */
	public SQOSelectedQuoteCPTabPage findAllQuote(SQOMyCurrentQuotePage currentQuotePage) { 
		SQOStatusSearchTabPage statusSearchPO = currentQuotePage.gotoStatusSearch();

		SQODisplayStatusSearchReslutPage resultPO = statusSearchPO
				.goDispAllQuoteReslt();
		loggerContxt.info("view the detail CP.....");
		SQOSelectedQuoteCPTabPage viewDetailCPPO = resultPO.viewQuoteDetail();

		return viewDetailCPPO; 
	}
	
	
	
	/**
	 * <br/>
	 * 
	 * <p>
	 * based on properties set in the file:
	 * 
	 * @param currentQuotePage
	 *            SQOMyCurrentQuotePage
	 * @return SQOMyCurrentQuotePage
	 */
	public SQOSelectedQuoteCPTabPage findQuoteByNum(SQOHomePage sqoHomePage) {
		SQOStatusSearchTabPage statusSearchPO = sqoHomePage.gotoStatus();

		loggerContxt.info("find quotes using the quote number:....."
				+ propBean.getDraftQuoteNum());
		SQODisplayStatusSearchReslutPage resultPO = statusSearchPO
				.findQuoteByNum(propBean.getDraftQuoteNum());
		loggerContxt.info("view the detail CP.....");
		SQOSelectedQuoteCPTabPage viewDetailCPPO = resultPO.goDispQuoteReslt();

		return viewDetailCPPO;
	}

	
	
	public SQOHomePage approveSpecialBidQuote(SQOMyCurrentQuotePage currentQuotePage) {
		List<String> lstApprover = runSubmitQuote(currentQuotePage);
		SQOHomePage home = null;
		if ("fvt".equals(prop.getProperty("env"))) {
			for (String approver : lstApprover) {
				home = approveQuote(approver);
			}
		}
		
		
		
//		currentQuotePage.setDriver(getDriver());
//		currentQuotePage.setSelenium(new WebDriverBackedSelenium(getDriver(), getDriver().getCurrentUrl()));
		return home;
	}

	protected SQOHomePage approveQuote(String userRole) {

		loggerContxt.info("quite webdirver.....");
		quitWebdriver();
		
		if ("fvt".equals(prop.getProperty("env"))) {
			reset(prop.getProperty("fvt.sqo_jump_url"));
		} else {
			reset(prop.getProperty("uat.sqo_url"));
		}

		getLogonInf().setSqoLogonUser(propBean.getApproverUser());
		getLogonInf().setAccessLevel(propBean.getAccessLevelApprover());
		SQOHomePage homePage = loginSqo(getLogonInf(), BaseTestCase.LOGON_IN_SQO_VIA_SQO_JUMP_PAGE_BY_URL);
//		SQOHomePage homePage = login(userRole);
		if (null == homePage) {
			loggerContxt.info("login failure!");
			throw new RuntimeException();
		}
		loggerContxt.info("approve quote detail.");

		loggerContxt.info("approveQuote");
		SQOApproveQueuePage approveQueue = homePage.gotoApproveQueue();

		SQOSubmitSQSpecialBidTabPage quoteDetails = null;
		loggerContxt.info("query quote page");
		quoteDetails = approveQueue.findQuoteByNum(propBean.getDraftQuoteNum());
		if (null != quoteDetails) {
			loggerContxt.info("quote detail page");
			String approveResult = propBean.getApproveResult();
			if ("approve".equals(approveResult)) {
				quoteDetails.submitApproveResult();
			} else if ("reject".equals(approveResult)) {
				quoteDetails.submitRejectResult();
			}
		}
		return homePage;
	}
	
	public SQOSelectedQuotePPTabPage goToSelectedQuotePPTabPage() {
		getDriver().close();
		if ("fvt".equals(prop.getProperty("env"))) {
			reset(prop.getProperty("fvt.sqo_jump_url"));
		} else {
			reset(prop.getProperty("uat.sqo_url"));
		}
		SQOHomePage sqoHomePage = loginSqo(getLogonInf(), BaseTestCase.LOGON_IN_SQO_VIA_SQO_JUMP_PAGE_BY_URL);
		SQOMyCurrentQuotePage currentQuotePage = createDraftQuote(sqoHomePage);
		SQOSelectedQuoteCPTabPage sbmdCPTabl = findQuoteByNum(currentQuotePage);
		SQOSelectedQuotePPTabPage papTab = goToPPTabSubmittedQuote(sbmdCPTabl);
		return papTab;
	}

	protected SQOHomePage login(String role) {
		SQOHomePage sqoHomePage = null;
		loggerContxt.info("Enter: login method!");
		// login system by jump.html
		SQOJumpPage jumpPage = new SQOJumpPage(getDriver());
		String countruCode = propBean.getCountryCode();
		String userName = "";
		switch (Integer.valueOf(role)) {
		case 1:
			userName = propBean.getUserName1();
			break;
		case 2:
			userName = propBean.getUserName2();
			break;
		case 4:
			userName = propBean.getUserName4();
			break;
		default:
			break;
		}
		String accessLevel = propBean.getAccessLevel5();
		jumpPage.loginIn(countruCode, userName, accessLevel);
		sqoHomePage = new SQOHomePage(getDriver());

		loggerContxt.info("Exit: login method!");
		return sqoHomePage;
	}

	public List<String> runSubmitQuote(SQOMyCurrentQuotePage currentQuotePage) {

		loggerContxt.info("submit current quote page.....");
		SQOSubmitCurrDraftQuotePage currDraftQuotePage = currentQuotePage
				.submitCurrentDraftQuote();

		/*
		 * 
		 * Check the "E-mail this quote to the following e-mail address"
		 * checkbox, enter "chris_errichetti@us.ibm.com" in the field next to
		 * the checkbox item, turn on the No radio button for the question
		 * "Would you like to make this available to the customer on Passport Advantage Online?"
		 * , enter "XPRS Testing" in the
		 * "Enter customized text for the quote's cover email" text box and On
		 * the quote submission page, make a select from
		 * "* Would you like to make this available to the customer on Passport Advantage Online? "
		 * if applicable. Press the "Submit" button
		 */
		loggerContxt.info("submit the quote.....");
		List list =currDraftQuotePage.submitDraftQuoteForSpecialBid(
				propBean.getQuoteeditormail(), propBean.getQuoteDesc());
		// save current draftquote number for future use
		//propBean.setDraftQuoteNum(currDraftQuotePage.getQuoteNum());
		//loggerContxt.info("the quote number after submitting....."
		//		+ currDraftQuotePage.getQuoteNum());
		
		return list;
	}
	
	
	public void setPartsDatas(SQOPartsAndPricingTabPage partsAndPricingTab, String partNum) {
		String seqnum = propBean.getPartSortSeq();
		partsAndPricingTab.changeDisc_pct(partNum, propBean.getPartDiscount(), seqnum);
		partsAndPricingTab.changeOrridPrice(partNum, propBean.getPartOverridePrice(), seqnum);
		partsAndPricingTab.changeSortSeq(partNum, propBean.getPartSortSeq(), seqnum);
		partsAndPricingTab.recalculateQuotePPTab();
		loggerContxt.info("Part discount=" + propBean.getPartDiscount() + ", Part override price=" + propBean.getPartOverridePrice() + ", Part sort seqnum=" + propBean.getPartSortSeq());
	}
	
	public String editProvisioningForm(SQOPartsAndPricingTabPage partsAndPricingTab, SaasParts part)
	{
		    loggerContxt.info("verify if has provisioning id");
	        String provisionID = "";
        	String provisionData1 = "";
        	String provisionData2 = "";
        	String brandCode="";
        	switch(part){
        	case SAAS_PART:
        		provisionData1 = getPropBean().getSaasPartProvisionFormTab1();
        		provisionData2 = getPropBean().getSaasPartProvisionFormTab2();
        		brandCode = getPropBean().getSaasPartBrandCode();
        		break;
        	case BRAND_SERVICE_ONE_SAAS_PART:
        		provisionData1 = getPropBean().getSaasPartOneProvisionFormTab1();
        		provisionData2 = getPropBean().getSaasPartOneProvisionFormTab2();
        		brandCode = getPropBean().getSaasPartOneBrandCode();
        		break;
        	case BRAND_SERVICE_TWO_SAAS_PART:
        		provisionData1 = getPropBean().getSaasPartTwoProvisionFormTab1();
        		provisionData2 = getPropBean().getSaasPartTwoProvisionFormTab2();
        		brandCode = getPropBean().getSaasPartTwoBrandCode();
        		break;
        	case OTHER_SAAS_PART:
        		provisionData1 = getPropBean().getSaasOtherPartProvisionFormTab1();
        		provisionData2 = getPropBean().getSaasOtherPartProvisionFormTab2();
        		brandCode= getPropBean().getSaasPartOtherBrandCode();
        	}
        	loggerContxt.info("before click edit provisioning form link");
     	    partsAndPricingTab.editProvisioningClickByBrandCode(brandCode);
     	    loggerContxt.info("finish click edit provisioning form link");
     	    SQOEditProvisioningForm editProvisionForm = new SQOEditProvisioningForm(driver);
    		// if provisioning form questionnaire is not available
	        if(partsAndPricingTab.isTextPresent(prop.getProperty("SQOProvisioningFormQuestionnaireNotAvailable")))
	        {
	        	loggerContxt.info("Provisioning Questinnaire is not available.");
	        	editProvisionForm.returnToQuoteLinkClick();
	        
	        }else{
	        	editProvisionForm.populateProvisionForm(provisionData1);
	        	loggerContxt.info("Provisioning form are being populated with data");
	        	editProvisionForm.continueButtonClick();
    			editProvisionForm.waitForElementLoading(new Long(10000));
	        	boolean reachSubmission = false;
	        	int tabCounter = 1;
	        	do{	
	        		if(!editProvisionForm.isTextPresent("Please confirm below entered information")){
	        			tabCounter++;
	        			if(tabCounter == 2){
		        			editProvisionForm.populateProvisionForm(provisionData2);
		        		}
	        			editProvisionForm.continueButtonClick();
	        			editProvisionForm.waitForElementLoading(new Long(10000));
	        		}else{
	        			reachSubmission = true;
	        		}
	        	}while(!reachSubmission);
	        	loggerContxt.info("view Provisioning form entered data");
	        	editProvisionForm.submitButtonClick();
	        	loggerContxt.info("Provisioning Form was submitted");
	        	editProvisionForm.returnToQuoteLinkClick();
	        	loggerContxt.info("go back from edit provisioning form page");
	        	loggerContxt.info("Back to draft quote.");
	 	        loggerContxt.info("verify if has provisioning id");
	 	        provisionID = partsAndPricingTab.findProvisionIdByBrandCode(brandCode);
	 	        loggerContxt.info("Provision ID is " + provisionID);
	        }
	        return provisionID;
	}
	
	//verify provisionId from Database
	public void verifyProvisionIdFromDatabase(Connection conn,SaasParts part){
	
		String dbProvisionID = "";
		ProvisioinID id = new ProvisioinID(conn);
		
		String partID="";
		String brandCode="";
    	switch(part){
    	case SAAS_PART:
    		partID= getPropBean().getSaasPID();
    		brandCode= getPropBean().getSaasPartBrandCode();
    		break;
    	case BRAND_SERVICE_ONE_SAAS_PART:
    		partID= getPropBean().getSaasPIDBrandServiceOne();
    		brandCode = getPropBean().getSaasPartOneBrandCode();
    		break;
    	case BRAND_SERVICE_TWO_SAAS_PART:
    		partID= getPropBean().getSaasPIDBrandServiceTwo();
    		brandCode = getPropBean().getSaasPartTwoBrandCode();
    		break;
    	case OTHER_SAAS_PART:
    		partID= getPropBean().getSaasPIDOther();
    		brandCode = getPropBean().getSaasPartOtherBrandCode();
    	}
		
    	SQOPartsAndPricingTabPage ppTab = new SQOPartsAndPricingTabPage(driver);
    	String configId= ppTab.findConfigIdByPartID(partID);
    	String provisionID = ppTab.findProvisionIdByBrandCode(brandCode);
		dbProvisionID = id.getProvisionID(ppTab.getSQONum(), configId);
		loggerContxt.info("verify provisioning id from DB: "+dbProvisionID);
		loggerContxt.info("Configuration id: "+ configId);
		loggerContxt.info("SQO Number: " + ppTab.getSQONum() );
		if (dbProvisionID!=null && !dbProvisionID.equals("")&&
				dbProvisionID.equals(provisionID)){
			loggerContxt.info("Provisioning ID "+ provisionID+" exists in DB. ");
		} else {
			loggerContxt.info("Provisioning ID is not matched any ID in DB. ");
			//throw new RuntimeException();
		}
	}
	public SQOSelectedQuotePPTabPage goToPPTabSubmittedQuote(SQOSelectedQuoteCPTabPage quoteCPTabPage){
		SQOSelectedQuotePPTabPage papTab = quoteCPTabPage.goToPPTab();
		loggerContxt.info("Part and pricing tab is diplayed for a submitted quote .....");
		return papTab;
	}
	public PartsAndPricingTabPage goToPPTabSubmittedQuote(SQOMyCurrentQuotePage currentQuotePage){
		PartsAndPricingTabPage partsAndPricingTab = currentQuotePage.goToPartsAndPricingTab();
		loggerContxt.info("Part and pricing tab is diplayed for a draft quote .....");
		return partsAndPricingTab;
	}
	
	public String getProvId (Object papTab, String pIdPath){
		String provId = "";
		if(papTab instanceof SQOSelectedQuotePPTabPage){
			provId = ((SQOSelectedQuotePPTabPage)papTab).getOriginalProvisioningId(pIdPath); 
		}
		else if (papTab instanceof PartsAndPricingTabPage){
			provId = ((PartsAndPricingTabPage)papTab).getOriginalProvisioningId(); 
		}
		 		loggerContxt.info("Provisioning ID is ....."+provId);
		return provId;
	}
	
	public SQOMyCurrentQuotePage createACopyLinkClick (SQOSelectedQuotePPTabPage papTab){
		loggerContxt.info("Create a copy link has been clicked.....");
		return papTab.createACopyLinkClick();
	}
	
	public SQOSelectedQuotePPTabPage compareQuote(SQOSelectedQuotePPTabPage papTab) {
		SQOCompareQuotesPage cqPage = papTab.gotoCompareQuotesPage();
		cqPage.close();
		papTab.returnToSelfWindow();
		return papTab;
	}
	
	public SQOHomePage gotoHomePage(SQOMyCurrentQuotePage currentQuotePage){
		return currentQuotePage.gotoSQOHomePage();
	}
	
	public SQOSelectedQuotePPTabPage createBidIterationLinkClick(SQOSelectedQuotePPTabPage papTab){
		papTab = papTab.createBidIterationLinkClick();
		loggerContxt.info("Create bid link has been clicked ...");
		return papTab;
	}
	
	public void assertObjectEquals(String copiedId,String originalId ,Object partsAndPricingTab ) {
		if(partsAndPricingTab instanceof SQOSelectedQuotePPTabPage){
			((SQOSelectedQuotePPTabPage)partsAndPricingTab).assertObjectEquals(copiedId, originalId);
		}
		else if (partsAndPricingTab instanceof PartsAndPricingTabPage){
			((PartsAndPricingTabPage)partsAndPricingTab).assertObjectEquals(copiedId, originalId); 
		}
		
	}
	public SQOSalesInfoTabPage editSalesInfoTabWithoutSavingQuote(
			SQOPartsAndPricingTabPage partsAndPricingTab) {
		// Go to SalesInf Tab
		loggerContxt.info("Go to SalesInf Tab begin.....");
		SQOSalesInfoTabPage salesTab = partsAndPricingTab.gotoSalesInfoTab();
		loggerContxt.info("Go to SalesInf Tab end.....");

		salesTab.enterSalesInf(propBean.getBriefTitle(),
				propBean.getQuoteDesc(), propBean.getBusOrgCode());

		salesTab.addQuoteEditor(propBean.getQuoteeditormail());
		return salesTab;
	}
	
	public SQOMyCurrentQuotePage CreateAddOnQuote(/* String custNum, String CAID */) {

		// 1.create a draft quote;
		SQOHomePage sqoHomePage = loginSqo(getLogonInf());
		loggerContxt.info("Login SQO finished.....");

		SQOMyCurrentQuotePage currentQuotePage = createDraftQuote(sqoHomePage);
		loggerContxt.info("Create new sales quote,Draft quote header display, " + "Draft quote common action buttons display finished.....");

		// 2.add customer
		currentQuotePage = processCustPartnerTab(currentQuotePage);
		loggerContxt.info("add customer finished.....");

		// 3.add saas part
		SQOPartsAndPricingTabPage ppTab = addSassPartInActiveAassTab(currentQuotePage);
		loggerContxt.info("add Saas parts finished.....");
		
		dateSetting(ppTab);

		// 5. Edit the sales inf tab and special bid necesary,submit the quote
		SQOSalesInfoTabPage salesTab = editSalesInfoTab(ppTab);
		currentQuotePage = processSpecialBidTab(salesTab);
		return currentQuotePage;

	}

	public ImportSalesQuotePage openImportSalesQuote(SQOHomePage sqoHomePage) { 

		loggerContxt.info("go to Import a sales quote spreadsheet page .....");
		ImportSalesQuotePage importPage = sqoHomePage.gotoImportSalesQuote();

		return importPage;
	}
	
	public SQOMyCurrentQuotePage checkExecutiveSummaryTab(SQOSelectedQuoteCPTabPage sbmdCPTab) {
		SQOSubmittedQuoteExecSummaryTabPage sqesTab = sbmdCPTab.gotoExecutiveSummaryTab();
		return sqesTab.gotoMyCurrentQuotePage();
	}
	
	public SQOHomePage checkApprovalQueueTracker(SQOHomePage sqoHomePage) {
		SQODisplayApprovalQueueTrackerPage aqTracker = sqoHomePage.gotoApprovalQueueTrackerPage();
		aqTracker.close();
		sqoHomePage.returnToSelfWindow();
		return sqoHomePage;
	}
	
	public SQOHomePage checkStatusTracker(SQOHomePage sqoHomePage) {
		SQODisplayStatusTrackerPage sTracker = sqoHomePage.gotoStatusTrackerPage();
		SQODisplayStatusTrackerSettingsPage stSettings = sTracker.gotoStatusTrackerSettingsPage();
		sqoHomePage.close();
		stSettings.returnToSelfWindow();
		return stSettings.gotoSQOHomePage();
	}
	
	public SQOHomePage checkRenewalQuoteStatus(SQOHomePage sqoHomePage, String number) {
		SQOQuoteSearchIBMSelfPage renewalQuote = sqoHomePage.gotoRenewalQuote();
		SQORQSearchReslutPage rqsrPage = renewalQuote.searchRenewalQuotesByIBMer();
		SQORQSummaryTabPage rqsTabPage = rqsrPage.gotoRQSummaryTabPage(number);
		SQODraftRQCustPartnerTabPage drqcpTabPage = rqsTabPage.editTheMasterRenewalQuote(true);
		
		if (null != drqcpTabPage) {
			sqoHomePage = drqcpTabPage.gotoSQOHomePage();
		} else {
			sqoHomePage = rqsTabPage.gotoSQOHomePage();
		}
		return sqoHomePage;
	}
	
	
	public void dummy() {
		SQOHomePage sqoHomePage = loginSqo(null);
		SQOMyCurrentQuotePage currentQuotePage = createDraftQuote(sqoHomePage);
		processCustPartnerTab(currentQuotePage);
		SQOPartsAndPricingTabPage ppTab = addSoftwareParts(currentQuotePage);
		addSaasPartToQuote(ppTab);
		editSalesInfoTab(ppTab);
	}

	private String getProperty(String key) {
		return prop.getProperty(key);
	}
	
	/**
	 * find quote by IBMer
	 * @author Li Yu
	 * @date April 16, 2013
	 * @param sqoHomepage
	 * @return
	 */
	public SQOSelectedQuoteCPTabPage findQuoteByIBMer(SQOHomePage sqoHomePage){
		
		String quoteNum = propBean.getQuoteNum();
		SQOStatusSearchTabPage statusSearchPage = sqoHomePage.gotoStatus();

		loggerContxt
		.info("Check all checkboxes of the following sections Find the following quote types "
				+ "Find quotes and special bids with the following overall statuses"
				+ " Select 'Date submitted (descending)' in the Sort by dropdown "
				+ "Check the Make above selections my default checkbox"
				+ " Select the IBMer assigned Press on the 'Find quotes' button");
		
		SQODisplayStatusSearchReslutPage statusResultPage = statusSearchPage.findQuoteByIBMer();
		
		
		return statusResultPage.gotoDispQuoteReslt();
	}
	
	
	/**
	 * find quote by Customer for site
	 * @author Li Yu
	 * @date April 16, 2013
	 * @param sqotatusSearchTabPage
	 * @return
	 */
	public SQOSelectedQuoteCPTabPage findQuoteByCustomerForSite(SQOStatusSearchTabPage sqotatusSearchTabPage){
		
		String siteNum = propBean.getSiteNum();
		loggerContxt
		.info("Check all checkboxes of the following sections Find the following quote types "
				+ "Find quotes and special bids with the following overall statuses"
				+ " Select 'Date submitted (descending)' in the Sort by dropdown "
				+ "Check the Make above selections my default checkbox"
				+ " Select the Customer Press on the 'Find quotes' button");
		SQODisplayStatusSearchReslutPage statusResultPage = sqotatusSearchTabPage.findQuoteByCustomerForSite(siteNum);
	
		return statusResultPage.gotoDispQuoteReslt();
	}
	
	/**
	 * find quote by Customer for name
	 * @author Li Yu
	 * @date April 16, 2013
	 * @param sqotatusSearchTabPage
	 * @return
	 */
	public SQOSelectedQuoteCPTabPage findQuoteByCustomerForName(SQOStatusSearchTabPage sqotatusSearchTabPage){
		
		String cusName = propBean.getCustomerName();
		loggerContxt
		.info("Check all checkboxes of the following sections Find the following quote types "
				+ "Find quotes and special bids with the following overall statuses"
				+ " Select 'Date submitted (descending)' in the Sort by dropdown "
				+ "Check the Make above selections my default checkbox"
				+ " Select the Customer Press on the 'Find quotes' button");
		SQODisplayStatusSearchReslutPage statusResultPage = sqotatusSearchTabPage.findQuoteByCustomerForName(cusName);
	
		return statusResultPage.gotoDispQuoteReslt();
	}
	
	/**
	 * find quote by Partner for site
	 * @author Li Yu                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      5                     
	 * @param sqotatusSearchTabPage
	 * @return
	 */
	public SQOSelectedQuoteCPTabPage findQuoteByPartnerForSite(SQOStatusSearchTabPage sqotatusSearchTabPage){
		
		String siteNum = propBean.getPartSiteNum();
		loggerContxt
		.info("Check all checkboxes of the following sections Find the following quote types "
				+ "Find quotes and special bids with the following overall statuses"
				+ " Select 'Date submitted (descending)' in the Sort by dropdown "
				+ "Check the Make above selections my default checkbox"
				+ " Select the Partner Press on the 'Find by partner site number' button");
		SQODisplayStatusSearchReslutPage statusResultPage = sqotatusSearchTabPage.findQuoteByPartnerForSite(siteNum);
	
		return statusResultPage.gotoDispQuoteReslt();
	}
	
	/**
	 * find quote by Partner for name
	 * @author Li Yu                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      5                     
	 * @param sqotatusSearchTabPage
	 * @return
	 */
	public SQOSelectedQuoteCPTabPage findQuoteByPartnerForName(SQOStatusSearchTabPage sqotatusSearchTabPage){
		
		String partnerName = propBean.getPartnerName();
		loggerContxt
		.info("Check all checkboxes of the following sections Find the following quote types "
				+ "Find quotes and special bids with the following overall statuses"
				+ " Select 'Date submitted (descending)' in the Sort by dropdown "
				+ "Check the Make above selections my default checkbox"
				+ " Select the Partner Press on the 'Find by partner name' button");
		SQODisplayStatusSearchReslutPage statusResultPage = sqotatusSearchTabPage.findQuoteByPartnerForName(partnerName);
	
		return statusResultPage.gotoDispQuoteReslt();
	}
	
	/**
	 * find quote by Country 
	 * @author Li Yu
	 * @date April 16, 2013
	 * @param sqotatusSearchTabPage
	 * @return
	 */
	public SQOSelectedQuoteCPTabPage findQuoteByCountry(SQOStatusSearchTabPage sqotatusSearchTabPage){
		
		loggerContxt
		.info("Check all checkboxes of the following sections Find the following quote types "
				+ "Find quotes and special bids with the following overall statuses"
				+ " Select 'Date submitted (descending)' in the Sort by dropdown "
				+ "Check the Make above selections my default checkbox"
				+ " Select the Customer Press on the 'Find quotes' button");
		SQODisplayStatusSearchReslutPage statusResultPage = sqotatusSearchTabPage.findQuoteByCountry();
	
		return statusResultPage.gotoDispQuoteReslt();
	}
	
	/**
	 * find quote by Approval Attributes
	 * @author Li Yu
	 * @date April 16, 2013
	 * @param sqotatusSearchTabPage
	 * @return
	 */
	public SQOSelectedQuoteCPTabPage findQuoteByApprovalAttri(SQOStatusSearchTabPage sqotatusSearchTabPage){
		
		loggerContxt
		.info("Check all checkboxes of the following sections Find the following quote types "
				+ "Find quotes and special bids with the following overall statuses"
				+ " Select 'Date submitted (descending)' in the Sort by dropdown "
				+ "Check the Make above selections my default checkbox"
				+ " Select the Customer Press on the 'Find quotes' button");
		SQODisplayStatusSearchReslutPage statusResultPage = sqotatusSearchTabPage.findQuoteByApprovalAttri();
	
		return statusResultPage.gotoDispQuoteReslt();
	}
	
	/**
	 * find quote by Siebel Number
	 * @author Li Yu
	 * @date April 16, 2013
	 * @param sqotatusSearchTabPage
	 * @return
	 */
	public SQOSelectedQuoteCPTabPage findQuoteBySiebelNum(SQOStatusSearchTabPage sqotatusSearchTabPage){

		String siebelNum = propBean.getSiebelNum();
		loggerContxt
		.info("Check all checkboxes of the following sections Find the following quote types "
				+ "Find quotes and special bids with the following overall statuses"
				+ " Select 'Date submitted (descending)' in the Sort by dropdown "
				+ "Check the Make above selections my default checkbox"
				+ " Select the Customer Press on the 'Find quotes' button");
		SQODisplayStatusSearchReslutPage statusResultPage = sqotatusSearchTabPage.findQuoteBySiebelNum(siebelNum);
	
		return statusResultPage.gotoDispQuoteReslt();
	}
	
	/**
	 * find quote by Order Number
	 * @author Li Yu
	 * @date April 16, 2013
	 * @param sqotatusSearchTabPage
	 * @return
	 */
	public SQOSelectedQuoteCPTabPage findQuoteByOrderNum(SQOStatusSearchTabPage sqotatusSearchTabPage){
		
		String orderNum = propBean.getOrderNum();
		loggerContxt
		.info("Check all checkboxes of the following sections Find the following quote types "
				+ "Find quotes and special bids with the following overall statuses"
				+ " Select 'Date submitted (descending)' in the Sort by dropdown "
				+ "Check the Make above selections my default checkbox"
				+ " Select the Order number Press on the 'Find quotes' button");
		SQODisplayStatusSearchReslutPage statusResultPage = sqotatusSearchTabPage.findQuoteByOrderNum(orderNum);
	
		return statusResultPage.gotoDispQuoteReslt();
	}
	
	/**
	 * find quote by Quote Number
	 * @author Li Yu
	 * @date April 16, 2013
	 * @param sqotatusSearchTabPage
	 * @return
	 */
	public SQOSelectedQuoteCPTabPage findQuoteByQuoteNum(SQOStatusSearchTabPage sqotatusSearchTabPage){
		
		String quoteNum = propBean.getQuoteNum();
		loggerContxt
		.info("Check all checkboxes of the following sections Find the following quote types "
				+ "Find quotes and special bids with the following overall statuses"
				+ " Select 'Date submitted (descending)' in the Sort by dropdown "
				+ "Check the Make above selections my default checkbox"
				+ " Select the Quote number Press on the 'Find quotes' button");
		SQODisplayStatusSearchReslutPage statusResultPage = sqotatusSearchTabPage.findQuoteByQuoteNum(quoteNum);
	
		return statusResultPage.gotoDispQuoteReslt();
	}
	
	/**
	 * go to Status Page
	 * @author Li Yu
	 * @date April 16, 2013
	 * @param sqoSelectedQuoteCPTabPage
	 * @return
	 */
	public SQOStatusSearchTabPage gotoStatus(SQOSelectedQuoteCPTabPage sqoSelectedQuoteCPTabPage){
		
		SQOStatusSearchTabPage sqoStatusSearchTabPage = null;
		sqoStatusSearchTabPage = sqoSelectedQuoteCPTabPage.gotoStatePage();
		
		return sqoStatusSearchTabPage;
		
	}
	
	/**
	 * go to Status Page
	 * @author Wesley
	 * @date July 16, 2013
	 * @param partsAndPricingTab
	 * @return
	 */
	public SQOPartsAndPricingTabPage checkECFecture(SQOPartsAndPricingTabPage partsAndPricingTab ) {


		// Check EC begin
		loggerContxt.info("modify all override price begin.....");
		partsAndPricingTab.modifyOverridePrice(propBean.getPartOverridePrice());
		loggerContxt.info("modify all override price end.....");
		
		loggerContxt.info("validate EC  begin.....");
		partsAndPricingTab = partsAndPricingTab.checkECGuide();
		loggerContxt.info("validate EC end.....");

		
		return partsAndPricingTab;
	}
	
	/**
	 * add software part through search(except appliance pat) to draft quote
	 * based on the properties set in the file<br/>
	 * 
	 * <p>
	 * based on properties set in the file:
	 * 
	 * @param currentQuotePage
	 *            SQOMyCurrentQuotePage
	 * @return partsAndPricingTab
	 */
	public SQOMonthySwBaseConfiguratorPage addMonthySoftwareParts(
			SQOMyCurrentQuotePage currentQuotePage) {
		loggerContxt.info("search parts.....");

		// Select the last date of the current month as the
		// "Quote expiration date"
		currentQuotePage.selectExpirationDate(30);

		SQOPartsAndPricingTabPage partsAndPricingTab = currentQuotePage
				.goToPartsAndPricingTab();
		SQOFindPartsSelectTabPage findPartsSelect = null;
		if ((StringUtils.isNotBlank(propBean.getPartList1()))
				|| StringUtils.isNotBlank(propBean.getPartList2())
				|| StringUtils.isNotBlank(propBean.getPartDes())) {
			SQOFindPartsTabPage findPartsTabPage = partsAndPricingTab
					.findPartsLinkClick();

	
			if ((StringUtils.isNotBlank(propBean.getPartList1()))) {
				findPartsSelect = findPartsTabPage.findPartsLinkClick(propBean
						.getPartList1());
				loggerContxt.info("search first of parts:....."
						+ propBean.getPartList1());
				findPartsTabPage = findPartsSelect
						.selectPartsAndChgCriteriaClick(propBean.getPartList1());
		
			}

			if ((StringUtils.isNotBlank(propBean.getPartList2()))) {
				findPartsSelect = findPartsTabPage.findPartsLinkClick(propBean
						.getPartList2());
				loggerContxt.info("search the other parts:....."
						+ propBean.getPartList2());
				findPartsSelect.selectPartsAndChgCriteriaClick(propBean.getPartList2());
			}

		}
		SQOMonthySwBaseConfiguratorPage monthlySwBaseConfig = findPartsSelect.go2MonthlySwBaseConfig();
		getDriver().switchTo().frame("cpqIframeId1");
		loggerContxt.info("create quote page finished,current page is SQOMyCurrentQuotePage.....");

		return monthlySwBaseConfig;
	}
	
	
	/**
	 * Monthly software SQO changes - wedriver for basic configurator validation 
	 * @author Wesley
	 * @date July 16, 2013
	 * @param partsAndPricingTab
	 * @return
	 */
	public SQOPartsAndPricingTabPage doMonthySwBasicConfigValidaiton(SQOMonthySwBaseConfiguratorPage monthlySwBaseConfig ) {


		// Check EC begin
		loggerContxt.info("Monthly software  basic configurator validation  begin.....");
		loggerContxt.info("setting the quantity to s.....");
		monthlySwBaseConfig.settingMonthySwQantity("D12ZMLL","s");
		monthlySwBaseConfig.submitForm();
		monthlySwBaseConfig.validateMonthySwQantity();
		
		loggerContxt.info("setting the quantity to 55555555555555555555555555.....");
		monthlySwBaseConfig.settingMonthySwQantity("D12ZMLL","55555555555555555555555555");
		monthlySwBaseConfig.submitForm();
		monthlySwBaseConfig.validateMonthySwQantity();
		
		loggerContxt.info("setting the quantity to 2,Terms 5,Billing Quater.....");
		monthlySwBaseConfig.settingMonthySwQantity("D12ZMLL","2");
		monthlySwBaseConfig.settingTermsBilling("D12ZMLL","5","Q");
		monthlySwBaseConfig.validationTermsBilling();
		
		loggerContxt.info("setting the quantity to 2,Terms 5,Billing Quater.....");
		monthlySwBaseConfig.settingMonthySwQantity("D12ZMLL","2");
		monthlySwBaseConfig.settingTermsBilling("D12ZMLL","4","M");
		
		loggerContxt.info("Monthly software  basic configurator validation  end.....");
		monthlySwBaseConfig.waitForElementLoading(3000L);
		return new SQOPartsAndPricingTabPage(driver);
	}
	
	public   SQOMonthySwBaseConfiguratorPage checkMonthlyInfoOnPPTab(SQOPartsAndPricingTabPage pptab ) {
		pptab.checkMonthlyInfo("D12ZMLL","2");
		pptab.return2MonthlyConfig();
		getDriver().switchTo().frame("cpqIframeId1");
		return new SQOMonthySwBaseConfiguratorPage(driver);
	}
	
	
	public SQOPartsAndPricingTabPage editMonthySwBasicConfig(SQOMonthySwBaseConfiguratorPage monthlySwBaseConfig ) {
		monthlySwBaseConfig.uncheckMonthlyPart("D12ZMLL");
		monthlySwBaseConfig.settingMonthySwQantity("D12ZMLL","15");
		monthlySwBaseConfig.submitForm();
		monthlySwBaseConfig.waitForElementLoading(3000L);
		
		return new SQOPartsAndPricingTabPage(driver);
	}

	
	
}
