package com.ibm.dsw.quote.base.util;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>HtmlUtil<code> class .
 *
 * @author: chenzhh@cn.ibm.com
 *
 * Creation date: Apr 1, 2006
 */
public class HtmlUtil {
    public static final String POUND = "#";

    public static final String ACHOR = "achor";

    public static String getNoBorderTableHeadHtml(String width) {
        StringBuffer buf = new StringBuffer();

        buf.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"");

        if (width != null && !width.trim().equals("")) {
            buf.append(" width=\"");
            buf.append(width);
            buf.append("\"");
        }

        buf.append(">\n");

        return buf.toString();
    }

    public static String getTableTailHtml() {
        return "</table>\n";
    }

    /**
     * Get the the image tag of "c.gif".
     *
     * <p>
     * The result text as follow: <img alt="" class=".." height=".." width=".."
     * src="//www.ibm.com/i/c.gif" />.
     *
     * @param height
     *            java.lang.String, image's height
     * @param width
     *            java.lang.String, image's width
     * @param style
     *            java.lang.String, image's style
     * @return the html of image
     */
    public static String getWhiteSpaceImageHtml(String css, String height, String width) {
        StringBuffer buf = new StringBuffer();

        buf.append("<img alt=\"\"");

        if (css != null && !css.trim().equals("")) {
            buf.append(" class=\"");
            buf.append(css);
            buf.append("\"");
        }

        buf.append(" height=\"");
        buf.append(height);
        buf.append("\" width=\"");
        buf.append(width);
        buf.append("\" src=\"");
        buf.append("//www.ibm.com/i/c.gif");
        buf.append("\" />");

        return buf.toString();
    }

    public static String getURLForAction(String actionKey) {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String urlPattern = appContext.getConfigParameter(ApplicationProperties.APPLICATION_URL_PATTERN);
        String actionURL = urlPattern + "?" + appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY) + "="
                + actionKey;
        return actionURL;
    }

    public static String getURLForAction(String action1, String action2) {
    	return getURLForAction(action1, action2, null);
    }

    public static String getURLForAction(String action1, String action2, String params){
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
        StringBuffer sb = new StringBuffer();

        sb.append(actionKey).append("=").append(action1);
        if (StringUtils.isNotBlank(action2))
            sb.append("," + appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY) + "=").append(action2);
        if (StringUtils.isNotBlank(params))
            sb.append(",").append(params);
        return sb.toString();
    }

    public static String getURLForReporting(String actionKey) {
        String urlPattern = ApplicationProperties.getInstance().getReportingBaseURL();
        String actionURL = urlPattern + "?" + ParamKeys.PARAM_RPT_JADE_ACTION + "="
                + actionKey;
        return actionURL;
    }

    public static String getURLForPGSReporting(String actionKey) {
        String urlPattern = ApplicationProperties.getInstance().getQuoteBaseURLFull();
        String actionURL = urlPattern + "?" + ParamKeys.PARAM_RPT_JADE_ACTION + "="
                + actionKey;
        return actionURL;
    }
    
    
    public static String getViewInventoryDeployURL() {
    	return  ApplicationProperties.getInstance().getCognosInventoryDeployURL();
    }
    
    public static String getViewRenewalForecastURL() {
    	return  ApplicationProperties.getInstance().getCognosRenewalForecastURL();
    }
    
    public static String getViewReinstateQuoteURL() {
    	return  ApplicationProperties.getInstance().getCognosReinstatQuoteURL();
    }
    
	public static String getCognosWebserviceHost() {
		return ApplicationProperties.getInstance().getCognosWebserviceHost();
	}

	public static int getCognosWebservicePort() {
		return ApplicationProperties.getInstance().getCognosWebservicePort();
	}

	public static String getCognosWebserviceProtocol() {
		return ApplicationProperties.getInstance().getCognosWebserviceProtocol();
	}

	public static String getCognosWebserviceUrl() {
		return ApplicationProperties.getInstance().getCognosWebserviceUrl();
	}

	public static String getCognosWebserviceAuthentication() {
		return ApplicationProperties.getInstance().getCognosWebserviceAuthentication();
	}

	public static String getCognosWebserviceLogon() {
		return ApplicationProperties.getInstance().getCognosWebserviceLogon();
	}

	public static String getCognosWebserviceLogoff() {
		return ApplicationProperties.getInstance().getCognosWebserviceLogoff();
	}
	
	public static int getCognosWebserviceMaxExceptionCount() {
		return ApplicationProperties.getInstance().getCognosWebserviceMaxExceptionCount();
	}
    
    

    public static String getURLForCPQRedirect() {
        String url = ApplicationProperties.getInstance().getCPQRedirectURL();
        return url;
    }

    public static String getURLForSqo(String actionKey) {
        String urlPattern = ApplicationProperties.getInstance().getQuoteBaseURL();
        String actionURL = urlPattern + "?" + ParamKeys.PARAM_RPT_JADE_ACTION + "="
                + actionKey;
        return actionURL;
    }
    public static String getURLForSqoPop(String actionKey) {
        String urlPattern = ApplicationProperties.getInstance().getQuoteBaseURLPop();
        String actionURL = urlPattern + "?" + ParamKeys.PARAM_RPT_JADE_ACTION + "="
                + actionKey;
        return actionURL;
    }

    public static String getURLForSqoFullPath(String actionKey) {
        String urlPattern = ApplicationProperties.getInstance().getQuoteBaseURLFull();
        String actionURL = urlPattern + "?" + ParamKeys.PARAM_RPT_JADE_ACTION + "="
                + actionKey;
        return actionURL;
    }
    
    public static String getQuoteOrderURLForPGS() {
    	String tempSpecialFlag = "&amp;";
    	String tempFlag = "=";
    	String urlPattern = ApplicationProperties.getInstance().getDraftSalesQuoteOrderURL();
        String actionURL =  urlPattern+ "?" + ParamKeys.PARAM_DRAFT_PGS_ORDER_P0 + tempFlag
                +  ParamKeys.PARAM_DRAFT_PGS_ORDER_P0_VALUE +tempSpecialFlag+ParamKeys.PARAM_DRAFT_PGS_ORDER_ISORDERNOW +tempFlag+ParamKeys.PARAM_DRAFT_PGS_ORDER_ISORDERNOW_VALUE+tempSpecialFlag+ParamKeys.PARAM_DRAFT_PGS_ORDER_WEBQUTENUMBER+tempFlag;
        
        return actionURL;
    }
    
    

    /**
     * The method for mail template
     * @param actionKey
     * @return
     */
    public static String getQuoteFullUrl(String actionKey)
    {
    	String link = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ApplicationProperties.APPLICATION_QUOTE_APP_FULL_URL);
    	link += "?" + ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY)
    		+ "=" + actionKey;
        return link;
    }

    /**
     * The method for mail template
     * @param actionKey
     * @return
     */
    public static String getPGSFullUrl(String actionKey)
    {
    	String link = ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(
                ApplicationProperties.APPLICATION_PGS_APP_FULL_URL);
    	link += "?" + ApplicationContextFactory.singleton().getApplicationContext().getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY)
    		+ "=" + actionKey;
        return link;
    }

    public static String urlEncode(String str) {
        String encodedStr = str;
        try {
            encodedStr = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogContextFactory.singleton().getLogContext().error(HtmlUtil.class, e);
        }
        return encodedStr;
    }
    
    public static String paramEncode(String param) {
    	  param = param.replace("+","%20");
		return param;
    }
    
    
    public static StringBuffer addEncodeURLParam(StringBuffer url, String key, String value) {
        if (url == null) {
			url = new StringBuffer("");
		}
        if (key != null) {
            url.append("&amp;");
            key = HtmlUtil.urlEncode(key);
            key = HtmlUtil.paramEncode(key);
            url.append(key);
            url.append("=");
            url.append(urlEncode(value != null ? value : ""));
        }

        return url;
    }


    /**
     * Add parameter to the url
     * @param url
     * @param key
     * @param value
     * @return
     */
    public static StringBuffer addURLParam(StringBuffer url, String key, String value) {
        if (url == null) {
			url = new StringBuffer("");
		}
        if (key != null) {
            url.append("&amp;");
            url.append(urlEncode(key));
            url.append("=");
            url.append(urlEncode(value != null ? value : ""));
        }
        return url;
    }

    /**
     * Get Quote default URL from property.xml
     * @return
     */
    public static String getQuoteAppUrl(){
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String sqoUrl = appContext.getConfigParameter(ApplicationProperties.APPLICATION_QUOTE_APPURL);
        return sqoUrl ;
    }


    /**
     * Get integrated PVU URL
     * @return
     */
    public static String getIntegratedPVUUrl(){
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String pvuCtxRoot = appContext.getConfigParameter(ApplicationProperties.APPLICATION_PVU_INTEGRATEDURL);
        return pvuCtxRoot;
    }

    public static String getTranMessageParam(String bundleName, String bundleKey, boolean isInfoMessage, String[] args) {
        String msgSplitter = "##";
        String argSplitter = "#";
        StringBuffer sb = new StringBuffer();
        String msgType = isInfoMessage ? "0" : "1";

        sb.append(StringUtils.trimToEmpty(bundleName) + msgSplitter);
        sb.append(StringUtils.trimToEmpty(bundleKey) + msgSplitter);
        sb.append(msgType + msgSplitter);

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (i == args.length - 1)
                    sb.append(args[i]);
                else
                    sb.append(args[i] + argSplitter);
            }
        }
        sb.append(msgSplitter);

        return urlEncode(sb.toString());
    }

    public static String[] parseParameter(String param, String splitter) {
        ArrayList tokens = new ArrayList();
        if (param == null || splitter == null)
            return new String[0];

        while (param.indexOf(splitter) >= 0) {
            int pos = param.indexOf(splitter);
            String token = param.substring(0, pos);
            param = param.substring(pos+splitter.length());
            tokens.add(token);
        }
        tokens.add(param);

        return (String[]) tokens.toArray(new String[0]);
    }

    public static String urlDecode(String str) {
        String decodedStr = str;
        try {
            decodedStr = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogContextFactory.singleton().getLogContext().error(HtmlUtil.class, e);
        }
        return decodedStr;
    }
    
    public static String getAppFullUrl(boolean isPGSFlag) {
    	ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String appUrl = appContext.getConfigParameter(ApplicationProperties.APPLICATION_QUOTE_APP_FULL_URL);
         
    	if(isPGSFlag) {
    		appUrl = appContext.getConfigParameter(ApplicationProperties.APPLICATION_PGS_APP_FULL_URL);
    	}
    	
    	return appUrl;
    }
}
