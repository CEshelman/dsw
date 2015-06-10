package com.ibm.dsw.quote.base.servlet;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.config.SessionKeys;
import com.ibm.ead4j.jade.util.ParameterNotFoundException;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.jade.util.ParametersFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class SQOCSRFDefense extends WebCSRFGuard {

	public static final String PARAM_REWRITE_IGNORED_ACTIONS_FUZZY = "rewriteIgnoredActionsFuzzy";

	protected static final String PARAM_GUARDED_ACTIONS_MAP = "guardedAtionsMap";

	private Map<String, String> guardedAtionsMap = null;
	
	private List<String> safeUrls = null;
	
	LogContext log = LogContextFactory.singleton().getLogContext();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		loadGuardedActions();
		loadSaftUrls();
	}

	private void loadGuardedActions(){
		log.debug(this,"Begin to load guarded actions.");
        SAXBuilder builder = new SAXBuilder();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("/appl/config/guardAction.xml");//QuoteConstants.CSRF_GUARD_ACTIONS
        try {
            Document doc = builder.build(in);
            Element root = doc.getRootElement();
            @SuppressWarnings("unchecked")
			List<Element> factoriesList = root.getChildren();
            guardedAtionsMap = new HashMap<String, String>();
            for(int i=0;i<factoriesList.size();i++){
            	Element action = (Element)factoriesList.get(i);
            	guardedAtionsMap.put(action.getChildText("key"), action.getChildText("value"));
            }
        } catch (JDOMException e) {
        	log.error(this,"Format error for guardaction.xml.");
            e.printStackTrace();
        } catch (IOException ioe){
        	log.error(this,"Failed to load guarded actions.");
            ioe.printStackTrace();
        }
        log.debug(this,"Finished to load guarded actions.");
    }
	
	private void loadSaftUrls(){
		log.debug(this,"Begin to load safe Urls.");
        SAXBuilder builder = new SAXBuilder();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("/appl/config/safeUrls.xml");//QuoteConstants.CSRF_GUARD_ACTIONS
        try {
            Document doc = builder.build(in);
            Element root = doc.getRootElement();
			safeUrls = root.getChildren();
        } catch (JDOMException e) {
        	log.error(this,"Format error for safeUrls.xml.");
            e.printStackTrace();
        } catch (IOException ioe){
        	log.error(this,"Failed to load safe Urls.");
            ioe.printStackTrace();
        }
        log.debug(this,"Finished to load safe Urls.");
    }

	@Override
	protected String getAuthenticatedUserId(HttpServletRequest httpRequest) {
		String userId = null;
		HttpSession httpSession = httpRequest.getSession(false);
		if (httpSession != null) {
			userId = (String) httpSession
					.getAttribute(SessionKeys.SESSION_USER_ID);
			log.debug(this, "Authenticated user Id: " + userId);
		}
		return userId;
	}

	@Override
	protected boolean isRequestGuarded(ServletRequest request)
			throws ServletException {
		String actionCode = getRequestAction(request);
		String method = ((HttpServletRequest) request).getMethod();
		if(null != guardedAtionsMap){
			String matchActionMethod = guardedAtionsMap.get(actionCode);
			if(null != matchActionMethod && ("BOTH".equals(matchActionMethod) || method.equalsIgnoreCase(matchActionMethod))){
				log.debug(this, "isRequestGuarded return true: actionCode=" + actionCode);
				return true;
			}
		}
		return false;
	}
	
	protected boolean isSecureToServe(ServletRequest request) throws ServletException{
		boolean secureFlag = super.isSecureToServe(request);
		String actionCode = getRequestAction(request);
		log.debug(this, "isSecureToServe: secureFlag=" + secureFlag + ", actionCode=" + actionCode);
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		//temporary map only use in 12.4, will remove next version
		HashSet<String> temporaryActions = new HashSet<String>(){
			{			
				add("NEW_SALES_QUOTE_ACTION");
				add("CONFIG_HOSTED_SERVICE");
				add("RETURN_FROM_CONF_TO_SQO");
				add("STATUS_TRACKER");
				add("UPLOAD_SALES_QUOTE");
				add("DISPLAY_PART_PRICE_TAB");
				add("DISPLAY_SUBMITTEDQT_PART_PRICE_TAB");				
				add("POST_SQ_CUST_PRTNR_TAB");			
				add("DISPLAY_PARTSEARCH_FIND_RESULT");
				add("DISPLAY_FIND_QUOTE_BY_SIEBEL_NUM_CSC");
				add("FIND_QUOTE_BY_SIEBEL_NUM");				
				add("LOAD_APPROVAL_QUEUE");
				
				add("DISPLAY_DRAFT_SALES_QUOTES");
				add("MASS_DLGTN_ADD_DELEGATE");
				add("DISPLAY_FIND_QUOTE_BY_IBMER_CSC");
				add("FIND_QUOTE_BY_CUSTOMER");
				add("LOAD_APMASS_DLGTN_ADD_DELEGATEPROVAL_QUEUE");
				add("DISPLAY_FIND_QUOTE_BY_CUSTOMER_CSC");
				add("DISPLAY_FIND_QUOTE_BY_COUNTRY_CSC");
				add("DISPLAY_FIND_QUOTE_BY_APPVLATTR_CSC");
				add("DISPLAY_FIND_QUOTE_BY_IBMER_CSC");
				add("FIND_QUOTE_BY_APPVLATTR");
				add("FIND_QUOTE_BY_COUNTRY");
				add("FIND_QUOTE_BY_ORDER_NUM");
				add("DISPLAY_FIND_QUOTE_BY_ORDER_NUM_CSC");
				add("SEARCH_RESELLER_BY_ATTR");
				add("SEARCH_DISTRIBUTOR_BY_NUM");
				add("SEARCH_DISTRIBUTOR_BY_ATTR");
				add("FIND_QUOTE_BY_IBMER");
				add("COPY_UPDATE_SUBMITTED_QUOTE");
				add("SUBMIT_DRAFT_SQ_AS_FINAL");
				add("DISPLAY_CUST_PRTNR_TAB");
				add("MASS_DLGTN_ADD_DELEGATE");
				add("ADD_TOU_AMENDMENT_ACTION");
				add("SUBMIT_QUOTE_SUBMISSION");
				add("ADD_SB_ATTACHMENT_ACTION");
				add("END_USER_SEARCH_ATTR");
				add("CREATE_NEW_APPLIANCE_ADDRESS");
				add("OVRRD_TRAN_LEVEL_CODE_ACTION");
				add("POST_CALCULATE_EQUITY_CURVE");
				add("DISPLAY_FCT2PA_CUST_PARTNER");
				add("ADD_MIGRATE_PART");
				add("ADD_QUOTE_DELEGATE");
				add("SUBMIT_QUOTE_RTF_DOWNLOAD");
				add("EXPORT_SUBMITTED_QUOTE_NATIVE_EXCEL");
				add("OVERRIDE_DATE");
				add("SEARCH_RESELLER_BY_PORTFOLIO");
				add("APPROVAL_QUEUE_TRACKER");
				add("SEND_MAIL");
				add("DISPLAY_ToU_QUOTE");
				add("UPDATE_QUOTE_DATE");
				
				//Reporting jadeActions
				add("ordhistdetaildwnld");
				add("quoteitemsdetail");
				add("custactrpt");
				add("ordhistsummarydwnld");
				add("quotesrchqtnum");			
				add("quotesmryibmself");
				add("hostedServices");
				add("quotesmrycustsite");
				add("quotesmryptnrsite");
				add("quotesmryptnrattr");
				add("quotesmrycustattr");
				add("quotesmryqtnum");
				add("quotesmryregion");
				add("quotesmryibmrep");
				add("ordhist");
				add("hostedServicesdwnldsubmit");
				add("ordhistrpt");
				 			
				// PGS jadeActions
				add("SAVE_SELECTED_PARTS");
				add("POST_SPECIAL_BID_TAB");
				add("FIND_QUOTE_BY_NUM");
				add("DISPLAY_FIND_QUOTE_BY_PARTNER_CSC");
				add("PREPARE_CONF_REDIRECT_DATA_UPDATE");
				add("POST_PART_PRICE_TAB");
				add("ADD_OR_UPDATE_CONFIGRTN");
				add("APPLY_OFFER");
				add("FIND_QUOTE_BY_PARTNER");
				add("DISPLAY_SUBMITTEDQT_SALES_INFO_TAB");
				add("DISPLAY_SUBMITTEDQT_SPECIAL_BID_TAB");
				add("DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB");
				add("DISPLAY_SUBMITTEDQT_STATUS_TAB");
				add("CUSTOMER_SEARCH_DSWID");
				add("POST_SALES_INFO_TAB");
				add("CREATE_NEW_CUSTOMER");
				add("CUSTOMER_SEARCH_ATTR");
				add("SEARCH_RESELLER_BY_NUM");
				add("HOSTED_SERVICES");
				add("SEARCH_RESELLER_BY_NUM");
				add("REMOVE_CONFIGURATION");
				add("CLEAR_OFFER");
				add("APPLY_DISCOUNT");
				add("DISPLAY_EVAL_QUOTE_UNDER_EVALUATION");
				add("APPLIANCE_ADDRESS_SEARCH");
				add("SAVE_APPLIANCE_ADDRESS");
				add("CHECK_DUP_APPLIANCE_ADDRESS");
				add("HOSTED_SERVICES_DOWNLOAD_SUBMIT");
				add("DELETE_LINE_ITEM");				
				add("DISPLAY_FIND_QUOTE_BY_NUM_CSC");
				add("DISPLAY_SEARCH_CUSTOMER");
				
				//SBA jadeActions
				add("DISPLAY_HOME");
				add("CREATE_EDIT_APPROVAL_TYPE");
				add("DISPLAY_APRVL_GRP_LIST");
				add("SAVE_APPRVL_GRP");
				add("CREATE_REGION_DISTRICT");
				add("CREATE_DISTRICT_CONTRACT");
				add("TEST_PA_RULES");
				add("TEST_RULES");
				add("CREATE_FCT_RULE_SUBMIT");
				add("CREATE_OEM_RULE_SUBMIT");
				add("CREATE_PA_RULE_SUBMIT");
				add("DISPLAY_GLOBAL_READER_GROUPS");
				add("CREATE_SSP_RULE_SUBMIT");
				add("CREATE_EDIT_GLBL_READER_GROUP");
				add("CREATE_EDIT_PART_GROUP");
				add("CREATE_EDIT_CUST_GROUP");
				add("SUBMIT_SPECIAL_BID_CONFIGURATION");				
				add("DISPLAY_OMITTED_LINEITEM");
				add("DISPLAY_SUBMITTED_OMITTED_LINEITEM");
				add("AUTHENTICATION_DEFAULT_ACTION_HANDLER");						
			}
		};
		if(temporaryActions.contains(actionCode)){
			String refer = httpRequest.getHeader("Referer");
			log.debug(this, "isSecureToServe: refer url=" + refer + ", actionCode=" + actionCode);
			if(null != refer && !"".equals(refer) && null != safeUrls && !safeUrls.isEmpty()){
				if(refer.startsWith("/")){
					return true;
				}
				boolean safeUrlFlag = false;
				Iterator it = safeUrls.iterator();
				while(it.hasNext()){
					Element url = (Element)it.next();
					if(refer.startsWith(url.getText())){
						safeUrlFlag = true;
						break;
					}
				}
				return safeUrlFlag;
			}
		}
		return secureFlag;
	}
	
	@Override
	protected String getRequestAction(ServletRequest request) throws ServletException {
		String actionCode = super.getRequestAction(request);
		if (StringUtils.isNotBlank(actionCode)){
			return actionCode;
		}

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		try {
			Parameters params = ParametersFactory.create(httpRequest);
			actionCode = params.getParameterAsString(super.getActionKey());
		} catch (ParameterNotFoundException e) {
			getFilterConfig().getServletContext().log("Exception when create and initialize EAD4J parameters", e);
		}
		return actionCode;
	}

	@Override
	protected String getRequestFinalAction(ServletRequest request)
			throws ServletException {
		String actionCode = "";
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		Parameters params;
		try {
			params = ParametersFactory.create(httpRequest);
			String actionCode2 = params
					.getParameterAsString(QuoteConstants.CSRF_REQUEST_ACTION_2);
			if (actionCode2 == null || actionCode2.trim().length() == 0) {
				actionCode = getRequestAction(request);
			} else {
				actionCode = actionCode2;
			}

		} catch (ParameterNotFoundException e) {
			e.printStackTrace();
		}

		return actionCode;
	}
	
	protected boolean isActionRewriteIgnored(String actionCode) {
		boolean flag = QuoteConstants.downloadActionsSet.contains(actionCode) || super.isActionRewriteIgnored(actionCode);
		log.debug(this, "isActionRewriteIgnored: " + actionCode + ":" + flag);
		return flag;
	}

}