package com.ibm.dsw.quote.partner.viewbean;

import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.ProductPortfolioFactory;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.partner.config.PartnerActionKeys;
import com.ibm.dsw.quote.partner.config.PartnerDBConstants;
import com.ibm.dsw.quote.partner.config.PartnerMessageKeys;
import com.ibm.dsw.quote.partner.config.PartnerParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartnerViewBean</code> class is the view bean class for partner
 * search.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 5, 2007
 */
public class PartnerViewBean extends BaseViewBean {

    private String lobCode;

    private String cutCountry;

    private String num;

    private String state;

    private String name;

    private String country;

    private String tier1;

    private String tier2;

    private String searchMethod;
    
    private String authorizedPort;
    
    private String authorizedPortDesc;
    
    protected String searchTierType;
    
    protected String webQuoteNum;
    
    protected String isSubmittedQuote;
    
    private String chkMultipleProd;
    
    //for FCT TO PA USE
    protected  String migrationReqNum = "";
    protected  String pageFrom="";
    protected String isPageFromFCT2PAMigrationReq = "false";

    
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        locale = (Locale) params.getParameter(ParamKeys.PARAM_LOCAL);

        lobCode = params.getParameterAsString(PartnerParamKeys.PARAM_LINE_OF_BUSINESS);
        cutCountry = params.getParameterAsString(PartnerParamKeys.PARAM_CUST_COUNTRY);

        num = params.getParameterAsString(PartnerParamKeys.PARAM_SITE_NUM);

        name = params.getParameterAsString(PartnerParamKeys.PARAM_SITE_NAME);
        state = params.getParameterAsString(PartnerParamKeys.PARAM_STATE);
        country = params.getParameterAsString(PartnerParamKeys.PARAM_COUNTRY);

        searchMethod = params.getParameterAsString(PartnerParamKeys.PAREAM_SEARCH_METHOD);
        tier1 = params.getParameterAsString(PartnerParamKeys.PARAM_TIER1_RESELLER);
        tier2 = params.getParameterAsString(PartnerParamKeys.PARAM_TIER2_RESELLER);

        authorizedPort = params.getParameterAsString(PartnerParamKeys.PARAM_AUTHORIZED_PORT);
        authorizedPortDesc = getAuthorizedPortDesc(authorizedPort);
        
        searchTierType = params.getParameterAsString(PartnerParamKeys.PARAM_SEARCH_TIER_TYPE);
        webQuoteNum = params.getParameterAsString(DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM);
        isSubmittedQuote = params.getParameterAsString(ParamKeys.PARAM_IS_SBMT_QT);
        chkMultipleProd = params.getParameterAsString(PartnerParamKeys.PARAM_MULTIPLE_PROD_CHK);
        
        //for FCT to PA migration 
        migrationReqNum = (String) params.getParameter(ParamKeys.PARAM_MIGRATION_REQSTD_NUM);
        pageFrom = params.getParameterAsString(DraftQuoteParamKeys.PAGE_FROM);
        if (getPageFrom().equalsIgnoreCase(DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)) {
			isPageFromFCT2PAMigrationReq = "true";
		}
        
    }
    
    protected String getAuthorizedPortDesc(String authorizedPorts) {
        
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        if (StringUtils.isBlank(authorizedPorts) || "%".equals(authorizedPorts))
            return "";
        
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(authorizedPorts, ",");
        ProductPortfolioFactory portFactory = ProductPortfolioFactory.singleton();
        
        try {
            while (st.hasMoreTokens()) {
                String code = st.nextToken();
                CodeDescObj portObj = portFactory.findPortfolionByCode(code);
                if (portObj != null) {
                    if (sb.length() == 0)
                        sb.append(portObj.getCodeDesc());
                    else
                        sb.append("<br />"+portObj.getCodeDesc());
                }
            }
        } catch (TopazException e) {
            logContext.error(this, "Failed to get descriptions of authorized portfolios.");
            return authorizedPorts;
        }
        
        return sb.toString();
    }

    /**
     * @return Returns the authorizedPort.
     */
    public String getAuthorizedPort() {
        return authorizedPort;
    }
    
    public String getAuthorizedPortDesc() {
        return authorizedPortDesc;
    }

    /**
     * @return Returns the custName.
     */
    public String getCustName() {
        return name;
    }

    /**
     * @return Returns the custNum.
     */
    public String getNum() {
        return num;
    }

    /**
     * @return Returns the cutCountry.
     */
    public String getCutCountry() {
        return cutCountry;
    }

    /**
     * @return Returns the lobCode.
     */
    public String getLobCode() {
        return lobCode;
    }

    /**
     * @return Returns the state.
     */
    public String getState() {
        return state;
    }

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    public String getSearchMethod() {
        return searchMethod;
    }

    public String getQuoteHomeUrl() {
        return HtmlUtil.getURLForAction(DraftQuoteActionKeys.DISPLAY_CURRENT_DRAFT_QUOTE);
    }
    
    public String getRtrnToSbmtQtUrl() {
        return HtmlUtil.getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB) + "&amp;"
                + SubmittedQuoteParamKeys.PARAM_QUOTE_NUM + "=" + webQuoteNum;
    }

    public String getSearchResellerByNumUrl() {
        return HtmlUtil.getURLForAction(PartnerActionKeys.SEARCH_RESELLER_BY_NUM);
    }

    public String getSearchResellerByAttUrl() {
        return HtmlUtil.getURLForAction(PartnerActionKeys.SEARCH_RESELLER_BY_ATTR);
    }
    
    public String getSearchResellerByPortUrl() {
        return HtmlUtil.getURLForAction(PartnerActionKeys.SEARCH_RESELLER_BY_PORTFOLIO);
    }

    public String getSearchDistributorByNumUrl() {
        return HtmlUtil.getURLForAction(PartnerActionKeys.SEARCH_DISTRIBUTOR_BY_NUM);
    }

    public String getSearchDistributorByAttUrl() {
        return HtmlUtil.getURLForAction(PartnerActionKeys.SEARCH_DISTRIBUTOR_BY_ATTR);
    }

    public boolean isSearchByNum() {
        return PartnerDBConstants.SEARCH_METHOD_BY_NUM.equals(searchMethod);
    }
    
    public boolean isSearchByPort() {
        return PartnerDBConstants.SEARCH_METHOD_BY_PORT.equals(searchMethod);
    }    
    

    /**
     * @return Returns the tier1.
     */
    public String getTier1() {
        return tier1;
    }

    /**
     * @return Returns the tier2.
     */
    public String getTier2() {
        return tier2;
    }

    public String getMsgReturnToQuote() {
        return PartnerMessageKeys.MSG_RETURN_TO_QUOTE;
    }

    public String getMsgCustName() {
        return PartnerMessageKeys.MSG_CUSTOMER_NAME;
    }

    public String getMsgCountry() {
        return PartnerMessageKeys.MSG_COUNTRY;
    }

    public String getMsgState() {
        return PartnerMessageKeys.MSG_STATE;
    }

    public String getMsgResellerType() {
        return PartnerMessageKeys.MSG_RESELLER_TYPE;
    }

    public String getMsgResellerSiteNum() {
        return PartnerMessageKeys.MSG_RESELLER_SITE_NUM;
    }

    public String getMsgDistributorSiteNum() {
        return PartnerMessageKeys.MSG_DISTRIBUTOR_SITE_NUM;
    }

    public String getPkLob() {
        return PartnerParamKeys.PARAM_LINE_OF_BUSINESS;
    }

    public String getPkCustCny() {
        return PartnerParamKeys.PARAM_CUST_COUNTRY;
    }
    
    public String getPkWebQuoteNum() {
        return DraftQuoteParamKeys.PARAM_WEB_QUOTE_NUM;
    }
    
    public String getPkSearchTierType() {
        return PartnerParamKeys.PARAM_SEARCH_TIER_TYPE;
    }
    
    public String getPkIsSubmittedQuote() {
        return ParamKeys.PARAM_IS_SBMT_QT;
    }

    public String getMsgAuthorizedPort() {
        return PartnerMessageKeys.MSG_AUTHORIZED_PORT;
    }    

    public String getSelectAllText() {
        return getI18NString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.LABEL_ALL);
    } 
    
    protected String getI18NString(String basename, Locale locale, String key) {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        return appCtx.getI18nValueAsString(basename, locale, key);
    }
    
    public String getSearchTierType() {
        return searchTierType;
    }
    
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    
    public String getIsSubmittedQuote() {
        return isSubmittedQuote;
    }
    
    public int getTierTypeOption() {
        int tierType = 0;
        if ("1".equals(searchTierType))
            tierType = 1;
        else if ("2".equals(searchTierType))
            tierType = 2;
        return tierType;
    }
    
    public String getChkMultipleProd() {
        return chkMultipleProd;
    }
   
	public String getMigrationReqNum() {
		return (migrationReqNum == null ? "" : migrationReqNum);
	}

	public void setMigrationReqNum(String migrationReqNum) {
		this.migrationReqNum = migrationReqNum;
	}

	public String getPageFrom() {
		return (pageFrom == null ? "" : pageFrom);
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}
	
    public String getIsPageFromFCT2PAMigrationReq() {
		return isPageFromFCT2PAMigrationReq;
	}

	public void setPageFromFCT2PAMigrationReq(String isPageFromFCT2PAMigrationReq) {
		this.isPageFromFCT2PAMigrationReq = isPageFromFCT2PAMigrationReq;
	}
	
	public String getRtrnToFCT2PACustPartnerURL() {
		String returnToFCT2PACustPartnerURL = HtmlUtil
				.getURLForAction(DraftQuoteActionKeys.DISPLAY_FCT2PA_CUST_PARTNER);
		StringBuffer url = new StringBuffer();
		HtmlUtil.addURLParam(url, ParamKeys.PARAM_MIGRATION_REQSTD_NUM,
				StringHelper.fillString(getMigrationReqNum()));
		HtmlUtil.addURLParam(url, DraftQuoteParamKeys.PAGE_FROM,
				StringHelper.fillString(getPageFrom()));
		returnToFCT2PACustPartnerURL += url.toString();
		return returnToFCT2PACustPartnerURL;
	}

}
