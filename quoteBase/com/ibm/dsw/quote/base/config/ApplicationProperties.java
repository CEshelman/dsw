package com.ibm.dsw.quote.base.config;

import java.util.MissingResourceException;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.util.PropertyConfigCodeConstant;
import com.ibm.dsw.quote.appcache.util.PropertyConfigUtil;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>ApplicationProperties.java</code> class reads the application properties from application.xml
 *
 *
 * @author: chenzhh@cn.ibm.com
 *
 * Creation date: Jan 26, 2007
 */
public class ApplicationProperties {
    public final static String DS_NAMING_FACTORY = "DS.namingFactory";
    public final static String CONNECTION_MAX_RETRIES = "Connection.MaxRetries";
    public final static String VALUE_UNIT_BEAN_JNDI = "CustomerSearchEJB.JNDI";
    public final static String TAB_LINK_FILE_KEY = "tab.config.file";
    public final static String APPLICATION_URL_PATTERN = "application.url.pattern";
    public final static String CACHE_TIME_TO_LIVE = "cache.time.to.live";
    public final static String CACHE_CHECKVALID_INTERVAL ="cache.checkvalid_interval";
    public final static String TIMER_MANAGER_JNDI = "timer.manager.jndi";
    public final static String WORK_MANAGER_JNDI = "work.manager.jndi";
    public static final String CACHE_AVAILABLE = "cache.available";

    public final static String DEFAULT_TIME_FILTER_KEY = "quote.cookie.default.time";
    public final static String DEFAULT_OWNER_FILTER_KEY = "quote.cookie.default.owner";
    public final static String DEFAULT_LOB_KEY = "quote.cookie.default.lob";
    public final static String DEFAULT_COUNTRY_KEY = "quote.cookie.default.country";
    public final static String DEFAULT_BUSINESS_ORG_KEY = "quote.cookie.default.busorg";
    public static final String DEFAULT_DISPLAY_DETAIL_FLAG_KEY = "quot.cookie.pvu.default.display.detail.flag";
    public static final String DEFAULT_MANDATORY_FLAG_KEY = "quot.cookie.pvu.default.mandatory.flag";
    public final static String DEFAULT_BROWSE_PART_BRAND = "quote.cookie.browse.part.default.brand";
    public static final String QUOTE_TYPE_DEFAULT = "quote.cookie.default.quote.type";
    public static final String RENEWAL_OR_SALES_DEFAULT = "quote.cookie.default.renewal.sales";
    public static final String OVERALL_STATUS_DEFAULT = "quote.cookie.default.overall.status";

    public static final String APPROVER_TYPE_DEFAULT = "quote.cookie.default.approver.type";
    public static final String APPROVER_GROUP_DEFAULT = "quote.cookie.default.approver.group";

    public static final String STATUS_SORT_BY_DEFAULT = "quote.cookie.default.status.sort";
    public static final String SUBMITTED_TIME_DEFAULT = "quote.cookie.default.submitted.time";
    public static final String SUBMITTED_OWNER_DEFAULT = "quote.cookie.default.submitted.owner";
    public static final String SUBMITTED_OWNER_ROLE_DEFAULT = "quote.cookie.default.submitted.owner.role";
    public static final String SUBMITTED_CUST_STATE_DEFAULT = "quote.cookie.default.submitted.cust.state";
    public static final String SUBMITTED_CUST_SB_REGION_DEFAULT = "quote.cookie.default.submitted.cust.sb.region";
    public static final String SUBMITTED_CUST_SB_DISTRICT_DEFAULT = "quote.cookie.default.submitted.cust.sb.district";
    public static final String SUBMITTED_CUST_APPROVER_GROUP_DEFAULT = "quote.cookie.default.submitted.cust.approver.group";
    public static final String SUBMITTED_CUST_APPROVER_TYPE_DEFAULT = "quote.cookie.default.submitted.cust.approver.type";
    public static final String LINE_ITEM_DETAIL_FLAG_DEFAULT = "quote.cookie.default.line.item.detail.flag";
    public static final String BRAND_DETAIL_FLAG_DEFAULT = "quote.cookie.default.brand.detail.flag";
    public static final String APPROVAL_QUEUE_TYPE_DEFAULT = "quote.cookie.default.approval.queue.type";
    public static final String APPROVAL_QUEUE_SORT_FILTER_DEFAULT = "quote.cookie.default.approval.queue.sortfilter";
    public static final String APPROVAL_SEARCH_TYPE_DEFAULT = "quote.cookie.default.approval.search.type";

    public final static String STATUS_TRACKER_COOKIE_NAME_KEY = "status.tracker.cookie.name";
    public final static String QUOTE_COOKIE_NAME_KEY = "quote.cookie.name";
    public final static String QUOTE_COOKIE_DOMAIN_KEY = "quot.cookie.domain";
    public final static String QUOTE_COOKIE_EXPIRES_KEY = "quot.cookie.expires";
    public final static String QUOTE_COOKIE_PATH_KEY = "quot.cookie.path";
    public final static String QUOTE_COOKIE_TIME_KEY = "quot.cookie.time.key";
    public final static String QUOTE_COOKIE_OWNER_KEY = "quot.cookie.onwer.key";
    public final static String QUOTE_COOKIE_COUNTRY_KEY = "quot.cookie.country.key";
    public final static String QUOTE_COOKIE_SUB_REGION_KEY = "quot.cookie.sub.region.key";
    public final static String QUOTE_COOKIE_ACQUISITION_KEY = "quot.cookie.acqstn.key";
    public final static String QUOTE_COOKIE_LOB_KEY = "quot.cookie.lob.key";
    public final static String QUOTE_COOKIE_BUSINESS_ORG_KEY = "quot.cookie.busorg.key";
    public final static String MAIL_HOST = "mail.host";
    public final static String QUOTE_COOKIE_BROWSE_PART_BRAND = "quote.cookie.browse.part.brand.key";
    public static final String QUOTE_COOKIE_DISPLAY_DETAIL_FLAG_KEY = "quot.cookie.pvu.display.detail.flag.key";
    public static final String QUOTE_COOKIE_MANDATORY_FLAG_KEY = "quot.cookie.pvu.mandatory.flag.key";
    public final static String APPLICATION_QUOTE_APPURL = "quote.appurl.key";
    public final static String APPLICATION_QUOTE_APP_FULL_URL = "quote.app.full.url.key";
    public final static String APPLICATION_PGS_APP_FULL_URL = "pgs.app.full.url.key";
    public final static String APPLICATION_PVU_INTEGRATEDURL = "pvu.integratedurl.key";
    public final static String PS_RESULT_SELECT_DESELECT_ALL_VALVE = "ps.result.select.deselect.all.valve";
    public static final String QUOTE_MAX_EXPIRATION_DAYS_CONFIG_FILE_KEY = "quote.max.expiration.days.config";
    public static final String QUOTE_CLASSIFICATION_CONFIG_FILE_KEY = "quote.classification.config";
    public static final String DISPLAY_ACTION_BUTTON_CONFIG_FILE_KEY = "display.action.button.config";
    public static final String CUST_AGREEMENT_TYPE_CONFIG_FILE_KEY = "cust.agreement.type.config";
    public static final String XSL_EXPORT_PATH = "xsl.export.path";
    public static final String XSL_IMPORT_PATH = "xsl.import.path";
    public final static String QUOTE_COOKIE_SEARCH_CLSFCTN_KEY = "quote.cookie.search.clsfctn.key";
    public final static String QUOTE_COOKIE_SEARCH_ACQSTN_KEY = "quote.cookie.search.acqstn.key";

    public static final String QUOTE_TYPE_KEY = "quote.cookie.quote.type.key";
    public static final String RENEWAL_OR_SALES_KEY = "quote.cookie.renewal.sales.key";
    public static final String OVERALL_STATUS_KEY = "quote.cookie.overall.status.key";

    public static final String APPROVER_TYPE_FILTER_KEY = "quote.cookie.approver.type.filter.key";
    public static final String APPROVER_GROUP_FILTER_KEY = "quote.cookie.approver.group.filter.key";
    public static final String APPROVER_GROUP_DATE_KEY = "quote.cookie.approver.group.date.key";
    public static final String APPROVER_TYPE_DATE_KEY = "quote.cookie.approver.type.date.key";

    public static final String STATUS_SORT_BY_KEY = "quote.cookie.status.sort.key";
    public static final String SUBMITTED_TIME_KEY = "quote.cookie.submitted.time.key";
    public static final String SUBMITTED_TIME_OPTIONS_KEY = "quote.cookie.submitted.time.options.key";
    public static final String SUBMITTED_OWNER_KEY = "quote.cookie.submitted.owner.key";
    public static final String SUBMITTED_OWNER_ROLE_KEY = "quote.cookie.submitted.owner.role.key";
    public static final String SUBMITTED_CUST_STATE_KEY = "quote.cookie.submitted.cust.state.key";
    public static final String SUBMITTED_CUST_SB_REGION_KEY = "quote.cookie.submitted.cust.sb.region.key";
    public static final String SUBMITTED_CUST_SB_DISTRICT_KEY = "quote.cookie.submitted.cust.sb.district.key";
    public static final String SUBMITTED_CUST_APPROVER_GROUP_KEY = "quote.cookie.submitted.cust.approver.group.key";
    public static final String SUBMITTED_CUST_APPROVER_TYPE_KEY = "quote.cookie.submitted.cust.approver.type.key";
    public static final String LINE_ITEM_DETAIL_FLAG_KEY = "quote.cookie.line.item.detail.flag.key";
    public static final String BRAND_DETAIL_FLAG_KEY = "quote.cookie.brand.detail.flag.key";
    public static final String APPROVAL_QUEUE_TYPE_KEY = "quote.cookie.approval.queue.type.key";
    public static final String APPROVAL_QUEUE_SORT_FILTER_KEY = "quote.cookie.approval.queue.sortfilter.key";
    public static final String APPROVAL_QUEUE_TRACKER_TYPE_KEY = "quote.cookie.approval.queue.tracker.type.key";
    public static final String APPROVAL_QUEUE_TRACKER_SORT_FILTER_KEY = "quote.cookie.approval.queue.tracker.sortfilter.key";

    public final static String RENEWAL_QUOTE_DETAIL_URL = "RenewalQuoteDetailURL";
    public final static String RENEWAL_QUOTE_SALES_TRACKING_URL = "RenewalQuoteSalesTrackingURL";
    public final static String EORDER_URL = "eorder.url";
    public final static String PAO_SITE_URL = "pao.site.url";
    public final static String QUOTE_FOR_SALES_REP_URL = "quote.for.sales.rep.url";
    public final static String HOLD_URL = "hold.url";
    public final static String TERMINATION_EMAIL_URL = "termination.email.url";
    public final static String RELOAD_INTERVAL = "quote.reload.interval";
    public static final String FIND_QUOTE_PAGE_SIZE = "find.quote.page.size";
    public static final String FIND_QUOTE_MAX_FETCH_NUMBER = "find.quote.max.fetch.number";

    public final static String RPT_BASE_URL = "reporting.base.url";
    public final static String SQO_BASE_URL = "quote.base.url";
    public final static String SQO_BASE_URL_POP = "quote.base.url.pop";
    public final static String SQO_BASE_URL_FULL = "quote.base.url.full";
    public final static String CPQ_REDIRECT_URL = "cpq.redirect.url";

    public final static String RPT_RENEWAL_QUOTE_ORDER_URL = "rpt.renewal.quote.order.url";
    public final static String DRAFT_SALES_QUOTE_ORDER_URL = "draft.sales.quote.order.url";
    public final static String DRAFT_RENEWAL_QUOTE_ORDER_URL = "draft.renewal.quote.order.url";
    public final static String SUBMITTED_SALES_QUOTE_ORDER_URL = "submitted.sales.quote.order.url";
    public final static String SUBMITTED_RENEWAL_QUOTE_ORDER_URL = "submitted.renewal.quote.order.url";
    
	public final static String PRINT_SP_TIMETRACE_EBIZ1 = "EBIZ1.";
	public final static String PRINT_SP_TIMETRACE_SIGN = "(";


    public final static String HOME_RELATED_LINK_ROF_RUL = "home.related.link.rof.url";


    public final static String QUOTE_ANYTIME_VALUE = "quote.anytime.value";

    public final static String VIEW_CUST_SW_ONLINE_URL = "cust.software.services.online.url";

    public final static String UPLOAD_FILE_MAX_SIZE = "upload.file.max.size";

    private static ApplicationProperties instance = null;

    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    protected static final ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();

    public static final String PART_PRICE_CONFIG_KEY = "part.price.config";

    public static final String EMPLOYEE_DLGTN_FOR_SB_CONFIG_KEY = "employee.dlgtn.for.sb.config";

    public static final String FCT_SPECIAL_BID_TYPES_DESC_CONFIG_KEY = "fct.special.bid.types.desc.config";

    public static final String SPECIAL_BID_REASON_CONFIG_FILE_KEY = "special.bid.reason.config";

    public static final String OBS_CUT_OFF_DAYS = "obs.cut.off.days";

    public static final String LOG_CONFIG_PORTAL = "log.config.portal";

    public static final String LOG_CONFIG_ENABLED = "log.config.enabled";

    public static final String BLUE_PAGE_SIMPLE_SEARCH_URL = "BluePageSimpleSearchURL";

    public static final String VALIDATION_MESSAGE_CONFIG = "validation.message.config";

    public static final String SIGNATRUE_COUNTRY_CONFIG = "signature.country.config";

    public static final String IBM_GLOBAL_CREDIT_SYSTEM_URL = "ibm.global.credit.system.url";

    public static final String UPLOAD_FILEDIR_KEY = "upload.filedir.key";

    public static final String BIDCOMPARE_CONFIG_FILE= "bidCompare.config.file";

    public static final String FCT_NON_STD_TERMS_CONDS_URL = "fct.non.std.terms.conds.url";

    public static final String CONFIGURATION_STALE_TIME = "configuration.stale.time";

    public static final String WEBSERVICE_CPQ_PRODID = "webservice.cpq.prog.id";
    public static final String WEBSERVICE_CPQ_USERID = "webservice.cpq.user.id";
    public static final String WEBSERVICE_CPQ_PASSWORD = "webservice.cpq.password";
    public static final String WEBSERVICE_CPQ_TIMEOUT = "webservice.cpq.timeout";
    public final static String COGNOS_INVENTORY_DEPLOY_URL = PropertyConfigCodeConstant.COGNOS_INVENTORY_DEPLOYMENTS_URL;//cognos.inventory.deployments.url
    public final static String COGNOS_RENEWAL_FORECAST_URL = PropertyConfigCodeConstant.COGNOS_RENEWAL_FORECAST_URL;//cognos.renewal.forecast.url
    public final static String COGNOS_REINSTAT_QUOTE_URL = PropertyConfigCodeConstant.COGNOS_REINSTAT_QUOTE_OPPORTUNITIES_URL;//cognos.reinstat.quote.opportunities.url
    public final static String COGNOS_WEBSERVICE_HOST = PropertyConfigCodeConstant.COGNOS_WEBSERVICE_HOST;//cognos.webservice.host
    public final static String COGNOS_WEBSERVICE_PORT = PropertyConfigCodeConstant.COGNOS_WEBSERVICE_PORT;//cognos.webservice.port
    public final static String COGNOS_WEBSERVICE_PROTOCOL = PropertyConfigCodeConstant.COGNOS_WEBSERVICE_PROTOCOL;//cognos.webservice.protocol
    public final static String COGNOS_WEBSERVICE_URL = PropertyConfigCodeConstant.COGNOS_WEBSERVICE_URL;//cognos.webservice.url
    public final static String COGNOS_WEBSERVICE_AUTHENTICATION = "cognos.webservice.authentication";
    public final static String COGNOS_WEBSERVICE_LOGON = PropertyConfigCodeConstant.COGNOS_WEBSERVICE_LOGON;//cognos.webservice.logon
    public final static String COGNOS_WEBSERVICE_LOGOFF = PropertyConfigCodeConstant.COGNOS_WEBSERVICE_LOGOFF;//cognos.webservice.logoff
    public final static String COGNOS_WEBSERVICE_MAX_EXCEPTION_COUNT = "cognos.webservice.max.exception.count";
    
    public static final String APPROVAL_SEARCH_TYPE_KEY = "quote.cookie.approval.search.type.key";
    
    

    public static final String CACHE_FORCE_REFRESH_INTERVAL = "cache.force.refresh.interval";

    public static final String QUOTE_APPLICATION_ENVIRONMENT = "quote.application.environment";


    private String uploadFileDir = "";
    private String dsNamingFactory = "";
    private int maxRetries = 3;
    private String customerSearchEJBJNDI = "";
    private String tabLinkFileName = "";


    private long cacheTimeToLive = -1L;
    private long cacheCheckValidInterval = 6*60*60*1000L;
    private long RORcacheCheckValidInterval = 2*60*1000L;

	private String timerManagerJNDI = "";
    private String workManagerJNDI = "";

    //keys
    private String stCookieName = "";
    private String quoteCookieName = "";
    private String quoteCookieDomain = "";
    private int quoteCookieExpires;
    private String quoteCookiePath = "";
    private String quoteCookieTimeKey = "";
    private String quoteCookieOwnerKey = "";
    private String quoteCookieLOBKey = "";
    private String displayDetailFlagKey="";
    private String quoteCookieCountryKey = "";
    private String quoteCookieSubRegionKey = "";
    private String quoteCookieAcquisitionKey = "";
    private String quoteCookieBusOrgKey = "";
    private String mandatoryFlagKey="";
    private String browsePartBrandKey = "";
    private String quoteTypeKey = "";
    private String renewalOrSalesKey = "";
    private String overallStatusKey = "";
    private String approverTypeFilterKey = "";
    private String approverGroupFilterKey = "";
    private String statusSortByKey = "";
    private String approverGroupDateKey = "";
    private String approverTypeDateKey = "";
    private String submittedTimeKey = "";
    private String submittedTimeKeyOptions = "";
    private String submittedOwnerKey = "";
    private String submittedOwnerRoleKey = "";
    private String submittedCustStateKey = "";
    private String submittedCustSBRegionKey = "";
    private String submittedCustSBDistrictKey = "";
    private String submittedApproverGroupKey = "";
    private String submittedApproverTypeKey = "";
    private String lineItemDetailFlagKey = "";
    private String brandDetailFlagKey = "";
    private String aprQueueTypeKey = "";
    private String aprQueueSortFilterKey = "";
    private String aprQueueTrackerTypeKey = "";
    private String aprQueueTrackerSortFilterKey = "";
    private String quoteCookieSearchClsfctnKey = "";
    private String quoteCookieSearchAcqstnKey = "";

    //default values
    private String displayDetailFlagDefault="";
    private String mandatoryFlagDefault="";
    private String browsePartBrandDefault = "";
    private String timeFilterDefault = "";
    private String ownerFilterDefault = "";
    private String countryDefault = "";
    private String lobDefault = "";
    private String businessOrgDefault = "";
    private String quoteTypeDefault = "";
    private String renewalOrSalesDefault = "";
    private String overallStatusDefault = "";
    private String approverTypeDefault = "";
    private String approverGroupDefault = "";
    private String statusSortByDefault = "";
    private String submittedTimeDefault = "";
    private String submittedOwnerDefault = "";
    private String submittedOwnerRoleDefault = "";
    private String submittedCustStateDefault = "";
    private String submittedCustSBRegionDefault = "";
    private String submittedCustSBDistrictDefault = "";
    private String submittedApproverGroupDefault= "";
    private String submittedApproverTypeDefault= "";
    private String lineItemDetailFlagDefault = "";
    private String brandDetailFlagDefault = "";
    private String aprQueueTypeDefault = "";
    private String aprQueueSortFilterDefault = "";

    private String reportingBaseURL = "";
    private String quoteBaseURL = "";
    private String quoteBaseURLPop = "";
    private String quoteBaseURLFull = "";
    private String redirectToCPQURL = "";
    private String draftSalesQuoteOrderURL = "";
    private String draftRenewalQuoteOrderURL = "";

    private String quoteMaxExpDaysConfigFileName = "";
    private String quoteClassfctnConfigFileName = "";
    private String displayActionButtonConfigFileName = "";
    private String custAgreementTypeConfigFileName = "";
    private int psResultSelectDeselectAllValve = 50;//50 as default.
    private String termEmailURL = "";
    private String reloadInterval = "";
    private String paoSiteURL = "";
    private String quoteForSalesRepURL = "";
    private String holdURL = "";
    private String orderHistoryDetailURL = "";

    private String webserviceCPQUserId = "";
    private String webserviceCPQPassword = "";
    private String webserviceCPQProdId = "";
    private String webserviceCPQTimeout = "";

    private int findQuoteMaxFectchNum = 1000;
    private String findQuotePageSize = "20";

    private int uploadFileMaxSize = 2;
     private String quoteAnyTimeValue;
    private boolean isCacheAvailable = false;

    private String partPriceConfigFile;

    private String empDlgtnForSbConfigFileName = "";
    private String fctSpecialBidTypesDescConfigFileName = "";
    private String specialBidReasonConfigFileName = "";
    private String validationMessageConfigFileName = "";
    private String signatrueCountryConfigFileName = "";

    private String cutOffDays = "";
    private String logConfigPortal = "";
    private boolean logConfigEnabled = false;

    private String bluePageSimpleSearchURL = "";
    private String ibmGlobalCreditSystemURL = "";

    private String bidCompareConfigFile = "";

    private String fctNonStdTermsCondsURL = "";

    private int configrtnStaleTime = 0;

    private Boolean t2PriceAvailable = false;

    private String cognosWebserviceAuthentication = "";
    private int cognosWebserviceMaxExceptionCount = 2; //set default to 2

    private long cacheForceRefreshInterval = 1000 * 60 * 5;  //default 5 minutes

    private String qtAppEnv = "";
    
    private String aprSearchTypeDefault = "";
    
    private String aprSearchTypeKey = "";

    public int getConfigrtnStaleTime() {
		return configrtnStaleTime;
	}

	public String getQuoteBaseURL() {
		return quoteBaseURL;
	}

	public String getQuoteBaseURLPop() {
		return quoteBaseURLPop;
	}

	public String getQuoteBaseURLFull() {
		return quoteBaseURLFull;
	}

    public String getCPQRedirectURL() {
		return redirectToCPQURL;
	}
	/**
     * @return Returns the cacheCheckValidInterval.
     */
    public long getCacheCheckValidInterval() {
        return cacheCheckValidInterval;
    }
    /**
     * @param cacheCheckValidInterval The cacheCheckValidInterval to set.
     */
    public void setCacheCheckValidInterval(long cacheCheckValidInterval) {
        this.cacheCheckValidInterval = cacheCheckValidInterval;
    }
    public long getRORcacheCheckValidInterval() {
		return RORcacheCheckValidInterval;
	}

	public void setRORcacheCheckValidInterval(long rORcacheCheckValidInterval) {
		RORcacheCheckValidInterval = rORcacheCheckValidInterval;
	}
    /**
     * @return Returns the cacheTimeToLive.
     */
    public long getCacheTimeToLive() {
        return cacheTimeToLive;
    }
    /**
     * @param cacheTimeToLive The cacheTimeToLive to set.
     */
    public void setCacheTimeToLive(long cacheTimeToLive) {
        this.cacheTimeToLive = cacheTimeToLive;
    }

    private void initialize() {
        try {
            dsNamingFactory = appContext.getConfigParameter(DS_NAMING_FACTORY);
            customerSearchEJBJNDI = appContext.getConfigParameter(VALUE_UNIT_BEAN_JNDI);
            tabLinkFileName = appContext.getConfigParameter(TAB_LINK_FILE_KEY);
            timerManagerJNDI = appContext.getConfigParameter(TIMER_MANAGER_JNDI);
            workManagerJNDI = appContext.getConfigParameter(WORK_MANAGER_JNDI);
  //          schedulerJNDI = appContext.getConfigParameter(SCHEDULER_JNDI);
//            isSchedulerEnabled = appContext.getConfigParameter(SCHEDULER_ENABLE);
            timeFilterDefault = appContext.getConfigParameter(DEFAULT_TIME_FILTER_KEY);
            ownerFilterDefault = appContext.getConfigParameter(DEFAULT_OWNER_FILTER_KEY);
            countryDefault = appContext.getConfigParameter(DEFAULT_COUNTRY_KEY);
            lobDefault = appContext.getConfigParameter(DEFAULT_LOB_KEY);
            businessOrgDefault = appContext.getConfigParameter(DEFAULT_BUSINESS_ORG_KEY);
            stCookieName = appContext.getConfigParameter(STATUS_TRACKER_COOKIE_NAME_KEY);
            quoteCookieName = appContext.getConfigParameter(QUOTE_COOKIE_NAME_KEY);
            quoteCookieDomain = appContext.getConfigParameter(QUOTE_COOKIE_DOMAIN_KEY);
            quoteCookieExpires = Integer.parseInt((appContext.getConfigParameter(QUOTE_COOKIE_EXPIRES_KEY)!=null?appContext.getConfigParameter(QUOTE_COOKIE_EXPIRES_KEY):"0"));
            quoteCookiePath = appContext.getConfigParameter(QUOTE_COOKIE_PATH_KEY);
            quoteCookieTimeKey = appContext.getConfigParameter(QUOTE_COOKIE_TIME_KEY);
            quoteCookieOwnerKey = appContext.getConfigParameter(QUOTE_COOKIE_OWNER_KEY);
            quoteCookieLOBKey = appContext.getConfigParameter(QUOTE_COOKIE_LOB_KEY);
            quoteCookieCountryKey = appContext.getConfigParameter(QUOTE_COOKIE_COUNTRY_KEY);
            quoteCookieSubRegionKey = appContext.getConfigParameter(QUOTE_COOKIE_SUB_REGION_KEY);
            quoteCookieAcquisitionKey = appContext.getConfigParameter(QUOTE_COOKIE_ACQUISITION_KEY);
            lineItemDetailFlagKey = appContext.getConfigParameter(LINE_ITEM_DETAIL_FLAG_KEY);
            brandDetailFlagKey = appContext.getConfigParameter(BRAND_DETAIL_FLAG_KEY);
            aprQueueTypeKey = appContext.getConfigParameter(APPROVAL_QUEUE_TYPE_KEY);
            
            aprQueueSortFilterKey = appContext.getConfigParameter(APPROVAL_QUEUE_SORT_FILTER_KEY);
            aprQueueTrackerTypeKey = appContext.getConfigParameter(APPROVAL_QUEUE_TRACKER_TYPE_KEY);
            aprQueueTrackerSortFilterKey = appContext.getConfigParameter(APPROVAL_QUEUE_TRACKER_SORT_FILTER_KEY);
            quoteCookieSearchClsfctnKey = appContext.getConfigParameter(QUOTE_COOKIE_SEARCH_CLSFCTN_KEY);
            quoteCookieSearchAcqstnKey = appContext.getConfigParameter(QUOTE_COOKIE_SEARCH_ACQSTN_KEY);

            displayDetailFlagDefault = appContext.getConfigParameter(DEFAULT_DISPLAY_DETAIL_FLAG_KEY);
            mandatoryFlagDefault = appContext.getConfigParameter(DEFAULT_MANDATORY_FLAG_KEY);
            browsePartBrandDefault = appContext.getConfigParameter(DEFAULT_BROWSE_PART_BRAND);

            displayDetailFlagKey = appContext.getConfigParameter(QUOTE_COOKIE_DISPLAY_DETAIL_FLAG_KEY);
            mandatoryFlagKey = appContext.getConfigParameter(QUOTE_COOKIE_MANDATORY_FLAG_KEY);
            browsePartBrandKey = appContext.getConfigParameter(QUOTE_COOKIE_BROWSE_PART_BRAND);
            quoteCookieBusOrgKey = appContext.getConfigParameter(QUOTE_COOKIE_BUSINESS_ORG_KEY);

            termEmailURL = appContext.getConfigParameter(TERMINATION_EMAIL_URL);
            reloadInterval = appContext.getConfigParameter(RELOAD_INTERVAL);

            paoSiteURL = appContext.getConfigParameter(PAO_SITE_URL);
            quoteForSalesRepURL = appContext.getConfigParameter(QUOTE_FOR_SALES_REP_URL);
            holdURL = appContext.getConfigParameter(HOLD_URL);

            quoteMaxExpDaysConfigFileName = appContext.getConfigParameter(QUOTE_MAX_EXPIRATION_DAYS_CONFIG_FILE_KEY);
            quoteClassfctnConfigFileName = appContext.getConfigParameter(QUOTE_CLASSIFICATION_CONFIG_FILE_KEY);
            displayActionButtonConfigFileName = appContext.getConfigParameter(DISPLAY_ACTION_BUTTON_CONFIG_FILE_KEY);
            custAgreementTypeConfigFileName = appContext.getConfigParameter(CUST_AGREEMENT_TYPE_CONFIG_FILE_KEY);
            //partPriceMinTranPtsConfigFileName = appContext.getConfigParameter(PARTPRICE_MIN_TRAN_PTS_CONFIG_FILE_KEY);

            quoteTypeKey = appContext.getConfigParameter(QUOTE_TYPE_KEY);
            renewalOrSalesKey = appContext.getConfigParameter(RENEWAL_OR_SALES_KEY);
            overallStatusKey = appContext.getConfigParameter(OVERALL_STATUS_KEY);

            approverTypeFilterKey = appContext.getConfigParameter(APPROVER_TYPE_FILTER_KEY);
            approverGroupFilterKey = appContext.getConfigParameter(APPROVER_GROUP_FILTER_KEY);

            statusSortByKey = appContext.getConfigParameter(STATUS_SORT_BY_KEY);
            approverGroupDateKey = appContext.getConfigParameter(APPROVER_GROUP_DATE_KEY);
            approverTypeDateKey = appContext.getConfigParameter(APPROVER_TYPE_DATE_KEY);

            submittedTimeKey = appContext.getConfigParameter(SUBMITTED_TIME_KEY);
            submittedTimeKeyOptions = appContext.getConfigParameter(SUBMITTED_TIME_OPTIONS_KEY);

            submittedOwnerKey = appContext.getConfigParameter(SUBMITTED_OWNER_KEY);
            submittedOwnerRoleKey = appContext.getConfigParameter(SUBMITTED_OWNER_ROLE_KEY);
            submittedCustStateKey = appContext.getConfigParameter(SUBMITTED_CUST_STATE_KEY);
            submittedCustSBRegionKey = appContext.getConfigParameter(SUBMITTED_CUST_SB_REGION_KEY);
            submittedCustSBDistrictKey = appContext.getConfigParameter(SUBMITTED_CUST_SB_DISTRICT_KEY);
            submittedApproverGroupKey = appContext.getConfigParameter(SUBMITTED_CUST_APPROVER_GROUP_KEY);
            submittedApproverTypeKey = appContext.getConfigParameter(SUBMITTED_CUST_APPROVER_TYPE_KEY);
            quoteTypeDefault = appContext.getConfigParameter(QUOTE_TYPE_DEFAULT);
            renewalOrSalesDefault = appContext.getConfigParameter(RENEWAL_OR_SALES_DEFAULT);
            overallStatusDefault = appContext.getConfigParameter(OVERALL_STATUS_DEFAULT);

            approverTypeDefault = appContext.getConfigParameter(APPROVER_TYPE_DEFAULT);
            approverGroupDefault = appContext.getConfigParameter(APPROVER_GROUP_DEFAULT);

            statusSortByDefault = appContext.getConfigParameter(STATUS_SORT_BY_DEFAULT);
            submittedTimeDefault = appContext.getConfigParameter(SUBMITTED_TIME_DEFAULT);
            submittedOwnerDefault = appContext.getConfigParameter(SUBMITTED_OWNER_DEFAULT);
            submittedOwnerRoleDefault = appContext.getConfigParameter(SUBMITTED_OWNER_ROLE_DEFAULT);
            submittedCustStateDefault = appContext.getConfigParameter(SUBMITTED_CUST_STATE_DEFAULT);
            submittedCustSBRegionDefault = appContext.getConfigParameter(SUBMITTED_CUST_SB_REGION_DEFAULT);
            submittedCustSBDistrictDefault = appContext.getConfigParameter(SUBMITTED_CUST_SB_DISTRICT_DEFAULT);
            submittedApproverGroupDefault = appContext.getConfigParameter(SUBMITTED_CUST_APPROVER_GROUP_DEFAULT);
            submittedApproverTypeDefault = appContext.getConfigParameter(SUBMITTED_CUST_APPROVER_TYPE_DEFAULT);
            lineItemDetailFlagDefault = appContext.getConfigParameter(LINE_ITEM_DETAIL_FLAG_DEFAULT);
            brandDetailFlagDefault = appContext.getConfigParameter(BRAND_DETAIL_FLAG_DEFAULT);
            aprQueueTypeDefault = appContext.getConfigParameter(APPROVAL_QUEUE_TYPE_DEFAULT);
            aprQueueSortFilterDefault = appContext.getConfigParameter(APPROVAL_QUEUE_SORT_FILTER_DEFAULT);
            
            reportingBaseURL = appContext.getConfigParameter(RPT_BASE_URL);
            quoteBaseURL = appContext.getConfigParameter(SQO_BASE_URL);
            quoteBaseURLPop = appContext.getConfigParameter(SQO_BASE_URL_POP);
            quoteBaseURLFull = appContext.getConfigParameter(SQO_BASE_URL_FULL);
            redirectToCPQURL = appContext.getConfigParameter(CPQ_REDIRECT_URL);
            draftSalesQuoteOrderURL = appContext.getConfigParameter(DRAFT_SALES_QUOTE_ORDER_URL);
            draftRenewalQuoteOrderURL = appContext.getConfigParameter(DRAFT_RENEWAL_QUOTE_ORDER_URL);
            findQuotePageSize = appContext.getConfigParameter(FIND_QUOTE_PAGE_SIZE);
            quoteAnyTimeValue = appContext.getConfigParameter(QUOTE_ANYTIME_VALUE);
            partPriceConfigFile = appContext.getConfigParameter(PART_PRICE_CONFIG_KEY);
            empDlgtnForSbConfigFileName = appContext.getConfigParameter(EMPLOYEE_DLGTN_FOR_SB_CONFIG_KEY);
            fctSpecialBidTypesDescConfigFileName = appContext.getConfigParameter(FCT_SPECIAL_BID_TYPES_DESC_CONFIG_KEY);
            cutOffDays = appContext.getConfigParameter(OBS_CUT_OFF_DAYS);
            specialBidReasonConfigFileName = appContext.getConfigParameter(SPECIAL_BID_REASON_CONFIG_FILE_KEY);
            logConfigPortal = appContext.getConfigParameter(LOG_CONFIG_PORTAL);
            logConfigEnabled = "true".equalsIgnoreCase(appContext.getConfigParameter(LOG_CONFIG_ENABLED));

            bluePageSimpleSearchURL = appContext.getConfigParameter(BLUE_PAGE_SIMPLE_SEARCH_URL);
            validationMessageConfigFileName = appContext.getConfigParameter(VALIDATION_MESSAGE_CONFIG);
            signatrueCountryConfigFileName = appContext.getConfigParameter(SIGNATRUE_COUNTRY_CONFIG);
            ibmGlobalCreditSystemURL = appContext.getConfigParameter(IBM_GLOBAL_CREDIT_SYSTEM_URL);
            uploadFileDir = appContext.getConfigParameter(UPLOAD_FILEDIR_KEY);

            bidCompareConfigFile = appContext.getConfigParameter(BIDCOMPARE_CONFIG_FILE);

            orderHistoryDetailURL = appContext.getConfigParameter("reporting.order.history.url");

            webserviceCPQProdId = appContext.getConfigParameter("webservice.cpq.prog.id");
            webserviceCPQUserId = appContext.getConfigParameter("webservice.cpq.user.id");
            webserviceCPQPassword = appContext.getConfigParameter("webservice.cpq.password");
            webserviceCPQTimeout = appContext.getConfigParameter("webservice.cpq.timeout");
            cognosWebserviceAuthentication =  appContext.getConfigParameter(COGNOS_WEBSERVICE_AUTHENTICATION);
            
            aprSearchTypeKey = appContext.getConfigParameter(APPROVAL_SEARCH_TYPE_KEY);
            aprSearchTypeDefault = appContext.getConfigParameter(APPROVAL_SEARCH_TYPE_DEFAULT);

            qtAppEnv = appContext.getConfigParameter(QUOTE_APPLICATION_ENVIRONMENT);
            try {
            	cognosWebserviceMaxExceptionCount =  Integer.parseInt(appContext.getConfigParameter(COGNOS_WEBSERVICE_MAX_EXCEPTION_COUNT));
            }catch (NumberFormatException nfe) {
            	logContext.error("parse cognos.webservice.max.exception.count error!", nfe);
            }



            t2PriceAvailable =	"true".equalsIgnoreCase(appContext.getConfigParameter("t2.price.available"));


            try {
            	configrtnStaleTime =  Integer.parseInt(appContext.getConfigParameter("configuration.stale.time"));
            }catch (NumberFormatException nfe) {
            	logContext.error("parse configuration.stale.time error!", nfe);
            }
            try {
                findQuoteMaxFectchNum = Integer.parseInt(appContext.getConfigParameter(FIND_QUOTE_MAX_FETCH_NUMBER));
                psResultSelectDeselectAllValve = Integer.parseInt(appContext.getConfigParameter(PS_RESULT_SELECT_DESELECT_ALL_VALVE));
            } catch (NumberFormatException nfe) {
                logContext.error("read psResultSelectDeselectAllValve from property error", nfe);
            }

            try {
                maxRetries = Integer.parseInt(appContext.getConfigParameter(CONNECTION_MAX_RETRIES));
            } catch (NumberFormatException nfe) {
                logContext.error("read max retries from property error", nfe);
            }
            try {
                cacheTimeToLive = Long.parseLong(appContext.getConfigParameter(CACHE_TIME_TO_LIVE));
            } catch (NumberFormatException nfe) {
                logContext.error("read cache timeToLive from property error", nfe);
            }
            try {
                cacheCheckValidInterval = Long.parseLong(appContext.getConfigParameter(CACHE_CHECKVALID_INTERVAL));
            } catch (NumberFormatException nfe) {
                logContext.error("read cache checkValidInterval from property error", nfe);
            }
            try {
                isCacheAvailable = Integer.parseInt(appContext.getConfigParameter(CACHE_AVAILABLE)) == 1;
            } catch (NumberFormatException nfe) {
                logContext.error("read cache availability from property error", nfe);
            }
            try {
                uploadFileMaxSize = Integer.parseInt(appContext.getConfigParameter(UPLOAD_FILE_MAX_SIZE));
            } catch (NumberFormatException nfe) {
                logContext.error("read uploadFileMaxSize from property error", nfe);
            }

            try
            {
            	cacheForceRefreshInterval = Long.parseLong(appContext.getConfigParameter(CACHE_FORCE_REFRESH_INTERVAL));
            }
            catch ( NumberFormatException nfe )
            {
            	logContext.error(this, nfe);
            }
        } catch (MissingResourceException e) {
            logContext.error(this, e);
        }
    }

    public long getCacheForceRefreshInterval() {
		return cacheForceRefreshInterval;
	}

	public ApplicationProperties() {
        initialize();
    }

    public static synchronized ApplicationProperties getInstance() {
        if (instance == null) {
            instance = new ApplicationProperties();
        }
        return instance;
    }

    /**
     * Get the name of the naming factory for datasource
     *
     * @return a String representing the name of Naming Factory for datasource
     */
    public String getDsNamingFactory() {
        return dsNamingFactory;
    }

    /**
     * Get the Max retry times.
     *
     * @return a Integer representing the max retry times
     */
    public int getMaxRetries() {
        return maxRetries;
    }

    public String getCustomerSearchBeanJNDI() {
        return customerSearchEJBJNDI;
    }

    public String getTabLinkFileName() {
        return tabLinkFileName;
    }
    /**
     * @return
     */
    public String getTimerManagerJNDI() {
        return timerManagerJNDI;
    }
    /**
     * @return
     */
    public String getWorkManagerJNDI() {
        return workManagerJNDI;
    }
    /**
     * @return Returns the country.
     */
    public String getCountryDefault() {
        return countryDefault;
    }
    /**
     * @return Returns the lob.
     */
    public String getLobDefault() {
        return lobDefault;
    }
    /**
     * @return Returns the ownerFilter.
     */
    public String getOwnerFilterDefault() {
        return ownerFilterDefault;
    }
    /**
     * @return Returns the timeFilter.
     */
    public String getTimeFilterDefault() {
        return timeFilterDefault;
    }
    /**
     * @return Returns the quoteCookieCountryKey.
     */
    public String getQuoteCookieCountryKey() {
        return quoteCookieCountryKey;
    }
    /**
     * @return Returns the quoteCookieSubRegionKey.
     */
    public String getQuoteCookieSubRegionKey() {
        return quoteCookieSubRegionKey;
    }

    /**
     * @return Returns the quoteCookieAcquisitionKey.
     */
    public String getQuoteCookieAcquisitionKey() {
        return quoteCookieAcquisitionKey;
    }
    /**
     * @return Returns the quoteCookieDomain.
     */
    public String getQuoteCookieDomain() {
        return quoteCookieDomain;
    }
    /**
     * @return Returns the quoteCookieLOBKey.
     */
    public String getQuoteCookieLOBKey() {
        return quoteCookieLOBKey;
    }
    /**
     * @return Returns the quoteCookieName.
     */
    public String getQuoteCookieName() {
        return quoteCookieName;
    }
    /**
     * @return Returns the quoteCookieOwnerKey.
     */
    public String getQuoteCookieOwnerKey() {
        return quoteCookieOwnerKey;
    }
    /**
     * @return Returns the quoteCookieTimeKey.
     */
    public String getQuoteCookieTimeKey() {
        return quoteCookieTimeKey;
    }
    /**
     * @return
     */
    public String getMandatoryFlagKey() {
        return mandatoryFlagKey;
    }
    /**
     * @return
     */
    public String getDisplayDetailFlagKey() {
        return displayDetailFlagKey;
    }
    /**
     * @return
     */
    public String getMandatoryFlagDefault() {
        return mandatoryFlagDefault;
    }
    /**
     * @return
     */
    public String getDisplayDetailFlagDefault() {
        return displayDetailFlagDefault;
    }

    public String getQuoteMaxExpDaysConfigFileName() {
        return quoteMaxExpDaysConfigFileName;
    }

    public String getQuoteClassfctnConfigFileName() {
        return quoteClassfctnConfigFileName;
    }

    public String getDisplayActionButtonConfigFileName() {
        return displayActionButtonConfigFileName;
    }

    public String getCustAgreementTypeConfigFileName() {
        return custAgreementTypeConfigFileName;
    }

    public String getBrowsePartBrandKey() {
        return browsePartBrandKey;
    }
    public String getBrowsePartBrandDefault() {
        return browsePartBrandDefault;
    }

    public int getPsResultSelectDeselectAllValve() {
        return psResultSelectDeselectAllValve;
    }

    /**
     * @return Returns the businessOrgDefault.
     */
    public String getBusinessOrgDefault() {
        return businessOrgDefault;
    }
    /**
     * @return Returns the quoteCookieBusOrgKey.
     */
    public String getQuoteCookieBusOrgKey() {
        return quoteCookieBusOrgKey;
    }
    /**
     * @return Returns the quoteCookiePath.
     */
    public String getQuoteCookiePath() {
        return quoteCookiePath;
    }
    /**
     * @return Returns the quoteCookieExpires.
     */
    public int getQuoteCookieExpires() {
        return quoteCookieExpires;
    }

    public String getTerminationEmailURL() {
        return termEmailURL;
    }
    public String getPaoSiteURL() {
        return paoSiteURL;
    }

    public String getQuoteForSalesRepURL() {
        return quoteForSalesRepURL;
    }
    /**
     * @return Returns the holdURL.
     */
    public String getHoldURL() {
        return holdURL;
    }
    /**
     * @return Returns the overallStatusKey.
     */
    public String getOverallStatusKey() {
        return StringUtils.trimToEmpty(overallStatusKey);
    }

    public String getApproverTypeFilterKey() {
        return StringUtils.trimToEmpty(approverTypeFilterKey);
    }

    public String getApproverGroupFilterKey() {
        return StringUtils.trimToEmpty(approverGroupFilterKey);
    }

    /**
     * @return Returns the quoteTypeKey.
     */
    public String getQuoteTypeKey() {
        return StringUtils.trimToEmpty(quoteTypeKey);
    }
    /**
     * @return Returns the renewalOrSalesKey.
     */
    public String getRenewalOrSalesKey() {
        return StringUtils.trimToEmpty(renewalOrSalesKey);
    }
    /**
     * @return Returns the statusSortByKey.
     */
    public String getStatusSortByKey() {
        return StringUtils.trimToEmpty(statusSortByKey);
    }

    public String getApproverGroupDateKey() {
        return StringUtils.trimToEmpty(approverGroupDateKey);
    }

    public String getApproverTypeDateKey() {
        return StringUtils.trimToEmpty(approverTypeDateKey);
    }

    /**
     * @return Returns the submittedApproverGroupKey.
     */
    public String getSubmittedApproverGroupKey() {
        return StringUtils.trimToEmpty(submittedApproverGroupKey);
    }
    /**
     * @return Returns the submittedCustSBDistinctKey.
     */
    public String getSubmittedCustSBDistrictKey() {
        return StringUtils.trimToEmpty(submittedCustSBDistrictKey);
    }
    /**
     * @return Returns the submittedCustSBRegionKey.
     */
    public String getSubmittedCustSBRegionKey() {
        return StringUtils.trimToEmpty(submittedCustSBRegionKey);
    }
    /**
     * @return Returns the submittedCustStateKey.
     */
    public String getSubmittedCustStateKey() {
        return StringUtils.trimToEmpty(submittedCustStateKey);
    }
    /**
     * @return Returns the submittedOwnerKey.
     */
    public String getSubmittedOwnerKey() {
        return StringUtils.trimToEmpty(submittedOwnerKey);
    }
    /**
     * @return Returns the submittedOwnerRoleKey.
     */
    public String getSubmittedOwnerRoleKey() {
        return StringUtils.trimToEmpty(submittedOwnerRoleKey);
    }
    /**
     * @return Returns the submittedTimeKey.
     */
    public String getSubmittedTimeKey() {
        return StringUtils.trimToEmpty(submittedTimeKey);
    }
    /**
     * @return Returns the submittedTimeOptionsKey.
     */
    public String getSubmittedTimeOptionsKey() {
        return StringUtils.trimToEmpty(submittedTimeKeyOptions);
    }

    /**
     * @return Returns the termEmailURL.
     */
    public String getTermEmailURL() {
        return StringUtils.trimToEmpty(termEmailURL);
    }
    /**
     * @return Returns the orderHistoryDetailURL.
     */
    public String getOrderHistoryDetailURL() {
    	return StringUtils.trimToEmpty(orderHistoryDetailURL);
    }

    public int getReloadInterval(){
        String interval = StringUtils.trimToEmpty(reloadInterval);
        if (StringUtils.isBlank(interval)){
            return 15 * 60;
        }else{
            return Integer.parseInt(interval);
        }
    }
    /**
     * @return Returns the overallStatusDefault.
     */
    public String getOverallStatusDefault() {
        return StringUtils.trimToEmpty(overallStatusDefault);
    }
    /**
     * @return Returns the approverTypeDefault.
     */
    public String getApproverTypeDefault() {
        return StringUtils.trimToEmpty(approverTypeDefault);
    }

    /**
     * @return Returns the approverGroupDefault.
     */
    public String getApproverGroupDefault() {
        return StringUtils.trimToEmpty(approverGroupDefault);
    }

    /**
     * @return Returns the quoteTypeDefault.
     */
    public String getQuoteTypeDefault() {
        return StringUtils.trimToEmpty(quoteTypeDefault);
    }
    /**
     * @return Returns the renewalOrSalesDefault.
     */
    public String getRenewalOrSalesDefault() {
        return StringUtils.trimToEmpty(renewalOrSalesDefault);
    }
    /**
     * @return Returns the statusSortByDefault.
     */
    public String getStatusSortByDefault() {
        return StringUtils.trimToEmpty(statusSortByDefault);
    }
    /**
     * @return Returns the submittedApproverGroupDefault.
     */
    public String getSubmittedApproverGroupDefault() {
        return StringUtils.trimToEmpty(submittedApproverGroupDefault);
    }
    /**
     * @return Returns the submittedCustSBDistrictDefault.
     */
    public String getSubmittedCustSBDistrictDefault() {
        return StringUtils.trimToEmpty(submittedCustSBDistrictDefault);
    }
    /**
     * @return Returns the submittedCustSBRegionDefault.
     */
    public String getSubmittedCustSBRegionDefault() {
        return StringUtils.trimToEmpty(submittedCustSBRegionDefault);
    }
    /**
     * @return Returns the submittedCustStateDefault.
     */
    public String getSubmittedCustStateDefault() {
        return StringUtils.trimToEmpty(submittedCustStateDefault);
    }
    /**
     * @return Returns the submittedOwnerDefault.
     */
    public String getSubmittedOwnerDefault() {
        return StringUtils.trimToEmpty(submittedOwnerDefault);
    }
    /**
     * @return Returns the submittedOwnerRoleDefault.
     */
    public String getSubmittedOwnerRoleDefault() {
        return StringUtils.trimToEmpty(submittedOwnerRoleDefault);
    }
    /**
     * @return Returns the submittedTimeDefault.
     */
    public String getSubmittedTimeDefault() {
        return StringUtils.trimToEmpty(submittedTimeDefault);
    }
    /**
     * @return Returns the reportingBaseURL.
     */
    public String getReportingBaseURL() {
        return reportingBaseURL;
    }
    /**
     * @return Returns the stCookieName.
     */
    public String getStCookieName() {
        return stCookieName;
    }
    /**
     * @return Returns the lineItemDetailFlagDefault.
     */
    public String getLineItemDetailFlagDefault() {
        return StringUtils.trimToEmpty(lineItemDetailFlagDefault);
    }
    /**
     * @return Returns the brandDetailFlagDefault.
     */
    public String getBrandDetailFlagDefault() {
        return StringUtils.trimToEmpty(brandDetailFlagDefault);
    }
    /**
     * @return Returns the lineItemDetailFlagKey.
     */
    public String getLineItemDetailFlagKey() {
        return StringUtils.trimToEmpty(lineItemDetailFlagKey);
    }
    /**
     * @return Returns the brandDetailFlagKey.
     */
    public String getBrandDetailFlagKey() {
        return StringUtils.trimToEmpty(brandDetailFlagKey);
    }
    /**
     * @return Returns the draftQuoteOrderURL.
     */
    public String getDraftSalesQuoteOrderURL() {
        return draftSalesQuoteOrderURL;
    }
    /**
     * @return
     */
    public int getFindQuoteMaxFectchNum() {
        return findQuoteMaxFectchNum;
    }
    /**
     * @return
     */
    public String getFindQuotePageSize() {
        return findQuotePageSize;
    }
    /**
     * @return Returns the draftRenewalQuoteOrderURL.
     */
    public String getDraftRenewalQuoteOrderURL() {
        return draftRenewalQuoteOrderURL;
    }

    public String getQuoteAnyTimeValue(){
        return quoteAnyTimeValue;
    }
    /**
     * @return
     */
    public boolean isCacheAvailable() {
        return isCacheAvailable;
    }

    /**
     * @return Returns the uploadFileMaxSize.
     */
    public int getUploadFileMaxSize() {
        return uploadFileMaxSize;
    }

    /**
     * @return Returns the aprQueueSortFilterKey.
     */
    public String getAprQueueSortFilterKey() {
        return aprQueueSortFilterKey;
    }

    /**
     * @return Returns the aprQueueTypeKey.
     */
    public String getAprQueueTypeKey() {
        return aprQueueTypeKey;
    }
    /**
     * @return Returns the aprQueueSortFilterDefault.
     */
    public String getAprQueueSortFilterDefault() {
        return aprQueueSortFilterDefault;
    }

    /**
     * @return Returns the aprQueueTypeDefault.
     */
    public String getAprQueueTypeDefault() {
        return aprQueueTypeDefault;
    }
    /**
     * @return the aprQueueTrackerTypeKey
     */
    public String getAprQueueTrackerTypeKey() {
        return aprQueueTrackerTypeKey;
    }
    /**
     * @return the aprQueueTrackerSortFilterKey
     */
    public String getAprQueueTrackerSortFilterKey() {
        return aprQueueTrackerSortFilterKey;
    }
    /**
     * @return Returns the submittedApproverTypeDefault.
     */
    public String getSubmittedApproverTypeDefault() {
        return submittedApproverTypeDefault;
    }

    /**
     * @return Returns the submittedApproverTypeKey.
     */
    public String getSubmittedApproverTypeKey() {
        return submittedApproverTypeKey;
    }
    /**
     * @return Returns the quotePriceBizRuleFile.
     */
    public String getPartPriceConfigFile() {
        return partPriceConfigFile;
    }

    public String getEmpDlgtnForSbConfigFileName() {
        return empDlgtnForSbConfigFileName;
    }

    /**
     * @return
     */
    public String getFCTSpecialBidTypesDescConfigFileName() {
        return fctSpecialBidTypesDescConfigFileName;
    }

    public String getSpecialBidReasonConfigFilename()
    {
    	return specialBidReasonConfigFileName;
    }

    public String getCutOffDays() {
        return cutOffDays;
    }

    public String getLogConfigPortal() {
        return logConfigPortal;
    }

    public boolean getT2PriceAvailable(){
    	return t2PriceAvailable;
    }

    public boolean isLogConfigEnabled() {
        return logConfigEnabled;
    }

    /**
     * @return Returns the quoteCookieSearchClsfctnKey.
     */
    public String getQuoteCookieSearchClsfctnKey() {
        return quoteCookieSearchClsfctnKey;
    }
    /**
     * @param quoteCookieSearchClsfctnKey The quoteCookieSearchClsfctnKey to set.
     */
    public void setQuoteCookieSearchClsfctnKey(String quoteCookieSearchClsfctnKey) {
        this.quoteCookieSearchClsfctnKey = quoteCookieSearchClsfctnKey;
    }
    /**
     * @return Returns the quoteCookieSearchAcqstnKey.
     */
    public String getQuoteCookieSearchAcqstnKey() {
        return quoteCookieSearchAcqstnKey;
    }
    /**
     * @param quoteCookieSearchAcqstnKey The quoteCookieSearchAcqstnKey to set.
     */
    public void setQuoteCookieSearchAcqstnKey(String quoteCookieSearchAcqstnKey) {
        this.quoteCookieSearchAcqstnKey = quoteCookieSearchAcqstnKey;
    }

    public String getBluePageSimpleSearchURL(){
    	return bluePageSimpleSearchURL;
    }

    public String getValidationMessageConfigFileName() {
        return validationMessageConfigFileName;
    }

    /**
     * @return Returns the signatrueCountryConfigFileName.
     */
    public String getSignatrueCountryConfigFileName() {
        return signatrueCountryConfigFileName;
    }
    /**
     * @return Returns the ibmGlobalCreditSystemURL.
     */
    public String getIbmGlobalCreditSystemURL() {
        return ibmGlobalCreditSystemURL;
    }
    public String getUploadFileDir() {
        return uploadFileDir;
    }
    
    public String getBidCompareConfigFile(){
    	return bidCompareConfigFile;
    }

    public String getWebserviceCPQUserId() {
		return webserviceCPQUserId;
	}

	public void setWebserviceCPQUserId(String webserviceCPQUserId) {
		this.webserviceCPQUserId = webserviceCPQUserId;
	}

	public String getWebserviceCPQPassword() {
		return webserviceCPQPassword;
	}

	public void setWebserviceCPQPassword(String webserviceCPQPassword) {
		this.webserviceCPQPassword = webserviceCPQPassword;
	}

	public String getWebserviceCPQProdId() {
		return webserviceCPQProdId;
	}

	public void setWebserviceCPQProdId(String webserviceCPQProdId) {
		this.webserviceCPQProdId = webserviceCPQProdId;
	}

	public String getWebserviceCPQTimeout() {
		return webserviceCPQTimeout;
	}

	public void setWebserviceCPQTimeout(String webserviceCPQTimeout) {
		this.webserviceCPQTimeout = webserviceCPQTimeout;
	}

	public String getFctNonStdTermsCondsURL(String areaCode){
    	String key=FCT_NON_STD_TERMS_CONDS_URL+"."+areaCode.toLowerCase();
    	fctNonStdTermsCondsURL = appContext.getConfigParameter(key);
    	return fctNonStdTermsCondsURL;
    }
    public String getFctNonStdTermsCondsURL(){
    	fctNonStdTermsCondsURL = appContext.getConfigParameter("FCT_NON_STD_TERMS_CONDS_URL");
    	return fctNonStdTermsCondsURL;
    }

	public String getCognosInventoryDeployURL() {
		return PropertyConfigUtil.getUrlConfigFromCacheByName(COGNOS_INVENTORY_DEPLOY_URL);
	}

	public String getCognosRenewalForecastURL() {
		return PropertyConfigUtil.getUrlConfigFromCacheByName(COGNOS_RENEWAL_FORECAST_URL);
	}

	public String getCognosReinstatQuoteURL() {
		return PropertyConfigUtil.getUrlConfigFromCacheByName(COGNOS_REINSTAT_QUOTE_URL);
	}

	public String getCognosWebserviceHost() {
		return PropertyConfigUtil.getUrlConfigFromCacheByName(COGNOS_WEBSERVICE_HOST);
	}

	public int getCognosWebservicePort() {
		return Integer.parseInt(PropertyConfigUtil.getUrlConfigFromCacheByName(COGNOS_WEBSERVICE_PORT));
	}

	public String getCognosWebserviceProtocol() {
		return PropertyConfigUtil.getUrlConfigFromCacheByName(COGNOS_WEBSERVICE_PROTOCOL);
	}

	public String getCognosWebserviceUrl() {
		return PropertyConfigUtil.getUrlConfigFromCacheByName(COGNOS_WEBSERVICE_URL);
	}

	public String getCognosWebserviceAuthentication() {
		return cognosWebserviceAuthentication;
	}

	public String getCognosWebserviceLogon() {
		return PropertyConfigUtil.getUrlConfigFromCacheByName(COGNOS_WEBSERVICE_LOGON);
	}

	public String getCognosWebserviceLogoff() {
		return PropertyConfigUtil.getUrlConfigFromCacheByName(COGNOS_WEBSERVICE_LOGOFF);
	}

	public int getCognosWebserviceMaxExceptionCount() {
		return cognosWebserviceMaxExceptionCount;
	}

	public String getQtAppEnv(){
		return qtAppEnv;
	}

    /**
     * @return Returns the aprSearchTypeDefault.
     */
    public String getAprSearchTypeDefault() {
        return aprSearchTypeDefault;
    }
    
    /**
     * @return Returns the aprSearchTypeKey.
     */
    public String getAprSearchTypeKey() {
        return aprSearchTypeKey;
    }
    
}
