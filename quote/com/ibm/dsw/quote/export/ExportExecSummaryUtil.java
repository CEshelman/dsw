/*
 * Created on Feb 10, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.export;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.draftquote.viewbean.helper.PartTableTotal;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetQuote;
import com.ibm.dsw.quote.submittedquote.domain.ExecSummary;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.BrandTotalPrice;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.BrandTotalPriceCollector;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.SubmittedNoteRow;

import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExportExecSummaryUtil {
	
	final static public ExportExecSummaryUtil util = new ExportExecSummaryUtil();
	
	public static void exportToRTF(Quote quote, SpreadSheetQuote ssQuote, OutputStream os, Locale locale, TimeZone timezone) throws QuoteException{
		Map params = new HashMap();
		
		Customer customer = quote.getCustomer();
		
		params.put("locale", locale);
		params.put("timezone", timezone);
		params.put("util", util);
		params.put("companyName", customer.getCustName());
		params.put("addrCity", customer.getCity());
		params.put("addrState", customer.getState());
		
		CacheProcess cProcess = CacheProcessFactory.singleton().create();
		Country cntry = cProcess.getCountryByCode3(customer.getCountryCode());
		params.put("addrCountry",cntry.getDesc());
		
		String agreementNum = null;
		if (customer == null || customer.getContractList() == null || customer.getContractList().size() == 0){
			agreementNum = "";
		}else{
			Contract contract = (Contract) customer.getContractList().get(0);
			if(contract != null){
				agreementNum = contract.getSapContractNum();
			}else{
				agreementNum = "";
			}
		}
		params.put("agreementNum", agreementNum);
		
		params.put("siteNum", customer.getCustNum());
		params.put("ibmCustNum", StringUtils.trimToEmpty(customer.getIbmCustNum()));
		
		CodeDescObj currency = cProcess.getCurrencyDesc(customer.getCurrencyCode());
		params.put("custCurrency", currency.getCodeDesc());
		
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		params.put("isFCTQuote", quoteHeader.isFCTQuote());
		if(quoteHeader.isChannelQuote()){
			params.put("isChannelQuote", true);
			params.put("resellerNum", ssQuote.getResellerCustNum());
			params.put("resellerName", ssQuote.getResellerCustName());
			params.put("distributorNum", ssQuote.getDistributorCustNum());
			params.put("distributorName", ssQuote.getDistributorCustName());
		}else{
			params.put("isChannelQuote", false);
			if(quoteHeader.isFCTQuote()){
				params.put("distributorNum", ssQuote.getDistributorCustNum());
				params.put("distributorName", ssQuote.getDistributorCustName());
			}
		}
		
		params.put("sqoRef", quoteHeader.getWebQuoteNum());
		NumberFormat nf = new DecimalFormat("###,##0.00");
		String quoteValue = nf.format(quoteHeader.getQuotePriceTot());
		params.put("quoteValue", quoteValue);
		
		currency = cProcess.getCurrencyDesc(quoteHeader.getCurrencyCode());
		params.put("custQuoteCurrency", currency.getCodeDesc());
		
		DateFormat sdf = new SimpleDateFormat("d MMMMM yyyy");
		if(quoteHeader.getQuoteExpDate() != null){
			params.put("quoteExpirationDate", sdf.format(quoteHeader.getQuoteExpDate()));
		}else{
			params.put("quoteExpirationDate", "");
		}
		if(quoteHeader.getQuoteStartDate() != null){
			params.put("quoteStartDate", sdf.format(quoteHeader.getQuoteStartDate()));
		}else{
			params.put("quoteStartDate", "");
		}
		ExecSummary execSummary = quote.getExecSummary();
		if(execSummary.getRecmdtFlag() == null){
			params.put("approvalRecommended", "");
		}else{
			params.put("approvalRecommended", execSummary.getRecmdtFlag().booleanValue()?"yes":"no");
		}
		params.put("approvalRecommendationText",util.htmlToRTF(StringUtils.trimToEmpty(execSummary.getRecmdtText())));
		
		String currencyCode = StringUtils.trimToEmpty(quoteHeader.getCurrencyCode());
		params.put("localCurrencyCode", currencyCode);
		String entitledPrice = execSummary.getEntitledTotalPrice()==null?"":nf.format(execSummary.getEntitledTotalPrice());
		entitledPrice += (" " +currencyCode);
		if(quoteHeader.isPAEQuote() || quoteHeader.isPAQuote()){
			entitledPrice = entitledPrice+ " (@ Level = "+ getSVPLevel(quote) +" )";
		}
		params.put("entitledPrice", StringUtils.isEmpty(entitledPrice)?"":entitledPrice);
		params.put("requestedPrice", execSummary.getSpecialBidTotalPrice()==null?"":nf.format(execSummary.getSpecialBidTotalPrice())+" "+currencyCode);
		params.put("periodBookableRevenue", execSummary.getPeriodBookableRevenue()==null?"":nf.format(execSummary.getPeriodBookableRevenue())+" "+currencyCode);
		params.put("serviceRevenue", execSummary.getServiceRevenue()==null?"":nf.format(execSummary.getServiceRevenue())+" "+currencyCode);
		params.put("hasBLPrice", Boolean.valueOf(hasBLPrice(quote)));
		params.put("totalDiscOffList", StringUtils.trimToEmpty(execSummary.getTotalDiscOffList()));
		params.put("totalDiscOffEntitled", StringUtils.trimToEmpty(execSummary.getTotalDiscOffEntitled()));
		params.put("maximumDiscOffEntitled", StringUtils.trimToEmpty(execSummary.getMaxDiscPct()));
		params.put("hasSaaSLineItem", Boolean.valueOf(ssQuote.isHasSaaSLineItem()));
//		if(ssQuote.isHasSaaSLineItem()){
//			params.put("totalCommitValue", (Double)ssQuote.getTotalCommitValue()==null?"":nf.format(ssQuote.getTotalCommitValue())+" "+currencyCode);
//		}
		params.put("termConditionsText", util.htmlToRTF(StringUtils.isEmpty(execSummary.getTermCondText())?"N/A":execSummary.getTermCondText()));
		params.put("execSupport", util.htmlToRTF(StringUtils.trimToEmpty(execSummary.getExecSupport())));
		params.put("briefOverview", util.htmlToRTF(StringUtils.trimToEmpty(execSummary.getBriefOverviewText())));
		SpecialBidInfo sbInfo = quote.getSpecialBidInfo();
		if(sbInfo != null){
			params.put("sbRegion", StringUtils.trimToEmpty(sbInfo.getSpBidRgn()));
			params.put("sbDistrict", StringUtils.trimToEmpty(sbInfo.getSpBidDist()));
		}
		SalesRep sr = quote.getCreator();
		if(sr != null){
			params.put("quoteCreator", StringUtils.trimToEmpty(sr.getFullName()));
		}
		sr = quote.getOppOwner();
		if(sr != null){
			params.put("oppOwner", StringUtils.trimToEmpty(sr.getFullName()));
		}
		params.put("fulfillmentSource", StringUtils.trimToEmpty(quoteHeader.getFulfillmentSrc()));
		List reviewerComments = null;
		if(sbInfo != null){
			reviewerComments = sbInfo.getReviewerComments();
		}
		if(reviewerComments == null){
			reviewerComments = new ArrayList();
		}
		params.put("reviewerComments", reviewerComments);
		
		List lineItems = ssQuote.getEqPartList();
		if(lineItems == null){
			lineItems = new ArrayList();
		}
		
		if(PartPriceConfigFactory.singleton().allowChannelMarginDiscount(quote) && (lineItems.size()>0)){
			params.put("isShowBPColumn", Boolean.TRUE);
		}else{
			params.put("isShowBPColumn", Boolean.FALSE);
		}
		
		params.put("lineItems", lineItems);
		
		PartTableTotal ptTotal = new PartTableTotal(quote);
		
		String swTotalEntitledExtPriceStr = ptTotal.getTotalEntitledExtendedPrice(quote.getSoftwareLineItems(), ssQuote.isPricingCallFailed());
		String swTotalBidExtPriceStr = ptTotal.getTotalBidExtendedPrice(quote.getSoftwareLineItems(), ssQuote.isPricingCallFailed());
		String saasTotalEntitledExtPriceStr = ptTotal.getTotalEntitledExtendedPrice(quote.getSaaSLineItems(), ssQuote.isPricingCallFailed());
		String saasTotalBidExtPriceStr = ptTotal.getTotalBidExtendedPrice(quote.getSaaSLineItems(), ssQuote.isPricingCallFailed());
		
		params.put("swTotalEntitledExtPrice", swTotalEntitledExtPriceStr);
		params.put("swTotalBidExtPrice",swTotalBidExtPriceStr);
		params.put("saasTotalEntitledExtPrice", saasTotalEntitledExtPriceStr);
		params.put("saasTotalBidExtPrice",saasTotalBidExtPriceStr);
		params.put("swTotalDiscFromEntitled", calculateDiscount(swTotalBidExtPriceStr,swTotalEntitledExtPriceStr));
		params.put("saasTotalDiscFromEntitled", calculateDiscount(saasTotalBidExtPriceStr,saasTotalEntitledExtPriceStr));
		
        params.put("totalDiscFromEntitled", StringUtils.rightPad(getTotalDiscFromEntitled(quote)+"%", 9));
        params.put("totalEntitledExtPrice", StringUtils.rightPad(ptTotal.getTotalEntitledExtendedPrice(quote.getLineItemList(),ssQuote
                .isPricingCallFailed()), 9));
		params.put("totalBidExtPrice", ptTotal.getTotalBidExtendedPrice(quote.getLineItemList(),ssQuote.isPricingCallFailed()));
		
		List configrtnsList = ssQuote.getPartsPricingConfigrtnsList();
		Map configrtnsMap = ssQuote.getPartsPricingConfigrtnsMap();
		if(configrtnsList == null){
			configrtnsList = new ArrayList(0);
		}
		params.put("lineItemsSize",lineItems.size());
		params.put("configrtnsListSize",configrtnsList.size());
		params.put("configrtnsList", configrtnsList);
		params.put("configrtnsMap", configrtnsMap);
		try {
			os.write(generateContent("downloadExecSummaryAsRTF.vm", params).getBytes("utf-8"));
		} catch (Exception e) {
			throw new QuoteException(e);
		}
	}
	
	public static void exportToPDF(Quote quote, SpreadSheetQuote ssQuote, OutputStream os, Locale locale, TimeZone timezone) throws QuoteException{
		Map params = new HashMap();
		
		params.put("noteRow", new SubmittedNoteRow(quote));
		
		Customer customer = quote.getCustomer();
		
		params.put("locale", locale);
		params.put("timezone", timezone);
		params.put("util", util);
		params.put("companyName", customer.getCustName());
		params.put("addrCity", customer.getCity());
		params.put("addrState", customer.getState());
		
		CacheProcess cProcess = CacheProcessFactory.singleton().create();
		Country cntry = cProcess.getCountryByCode3(customer.getCountryCode());
		params.put("addrCountry",cntry.getDesc());
		
		String agreementNum = null;
		if (customer == null || customer.getContractList() == null || customer.getContractList().size() == 0){
			agreementNum = "";
		}else{
			Contract contract = (Contract) customer.getContractList().get(0);
			if(contract != null){
				agreementNum = contract.getSapContractNum();
			}else{
				agreementNum = "";
			}
		}
		params.put("agreementNum", agreementNum);
		
		params.put("siteNum", customer.getCustNum());
		params.put("ibmCustNum", StringUtils.trimToEmpty(customer.getIbmCustNum()));
		
		CodeDescObj currency = cProcess.getCurrencyDesc(customer.getCurrencyCode());
		params.put("custCurrency", currency.getCodeDesc());
		
		QuoteHeader quoteHeader = quote.getQuoteHeader();
		params.put("isFCTQuote", quoteHeader.isFCTQuote());
		if(quoteHeader.isChannelQuote()){
			params.put("isChannelQuote", true);
			params.put("resellerNum", ssQuote.getResellerCustNum());
			params.put("resellerName", ssQuote.getResellerCustName());
			params.put("distributorNum", ssQuote.getDistributorCustNum());
			params.put("distributorName", ssQuote.getDistributorCustName());
		}else{
			params.put("isChannelQuote", false);
			if(quoteHeader.isFCTQuote()){
				params.put("distributorNum", ssQuote.getDistributorCustNum());
				params.put("distributorName", ssQuote.getDistributorCustName());
			}
		}
		
		params.put("sqoRef", quoteHeader.getWebQuoteNum());
		NumberFormat nf = new DecimalFormat("###,##0.00");
		String quoteValue = nf.format(quoteHeader.getQuotePriceTot());
		params.put("quoteValue", quoteValue);
		
		currency = cProcess.getCurrencyDesc(quoteHeader.getCurrencyCode());
		params.put("custQuoteCurrency", currency.getCodeDesc());
		
		DateFormat sdf = new SimpleDateFormat("d MMMMM yyyy");
		if(quoteHeader.getQuoteExpDate() != null){
			params.put("quoteExpirationDate", sdf.format(quoteHeader.getQuoteExpDate()));
		}else{
			params.put("quoteExpirationDate", "");
		}
		if(quoteHeader.getQuoteStartDate() != null){
			params.put("quoteStartDate", sdf.format(quoteHeader.getQuoteStartDate()));
		}else{
			params.put("quoteStartDate", "");
		}
		ExecSummary execSummary = quote.getExecSummary();
		if(execSummary.getRecmdtFlag() == null){
			params.put("approvalRecommended", "");
		}else{
			params.put("approvalRecommended", execSummary.getRecmdtFlag().booleanValue()?"yes":"no");
		}
		params.put("approvalRecommendationText",StringUtils.trimToEmpty(execSummary.getRecmdtText()));
		
		String currencyCode = StringUtils.trimToEmpty(quoteHeader.getCurrencyCode());
		params.put("localCurrencyCode", currencyCode);
		String entitledPrice = execSummary.getEntitledTotalPrice()==null?"":nf.format(execSummary.getEntitledTotalPrice());
		entitledPrice += (" " +currencyCode);
		if(quoteHeader.isPAEQuote() || quoteHeader.isPAQuote()){
			entitledPrice = entitledPrice+ " (@ Level = "+ getSVPLevel(quote) +" )";
		}
		params.put("entitledPrice", StringUtils.isEmpty(entitledPrice)?"":entitledPrice);
		params.put("requestedPrice", execSummary.getSpecialBidTotalPrice()==null?"":nf.format(execSummary.getSpecialBidTotalPrice())+" "+currencyCode);
		params.put("periodBookableRevenue", execSummary.getPeriodBookableRevenue()==null?"":nf.format(execSummary.getPeriodBookableRevenue())+" "+currencyCode);
		params.put("serviceRevenue", execSummary.getServiceRevenue()==null?"":nf.format(execSummary.getServiceRevenue())+" "+currencyCode);
		params.put("hasBLPrice", Boolean.valueOf(hasBLPrice(quote)));
		params.put("totalDiscOffList", StringUtils.trimToEmpty(execSummary.getTotalDiscOffList()));
		params.put("totalDiscOffEntitled", StringUtils.trimToEmpty(execSummary.getTotalDiscOffEntitled()));
		params.put("maximumDiscOffEntitled", StringUtils.trimToEmpty(execSummary.getMaxDiscPct()));
		params.put("hasSaaSLineItem", Boolean.valueOf(ssQuote.isHasSaaSLineItem()));
//		if(ssQuote.isHasSaaSLineItem()){
//			params.put("totalCommitValue", (Double)ssQuote.getTotalCommitValue()==null?"":nf.format(ssQuote.getTotalCommitValue())+" "+currencyCode);
//		}
		params.put("termConditionsText", StringUtils.isEmpty(execSummary.getTermCondText())?"N/A":execSummary.getTermCondText());
		params.put("execSupport", StringUtils.trimToEmpty(execSummary.getExecSupport()));
		params.put("briefOverview", StringUtils.trimToEmpty(execSummary.getBriefOverviewText()));
		SpecialBidInfo sbInfo = quote.getSpecialBidInfo();
		if(sbInfo != null){
			params.put("sbRegion", StringUtils.trimToEmpty(sbInfo.getSpBidRgn()));
			params.put("sbDistrict", StringUtils.trimToEmpty(sbInfo.getSpBidDist()));
		}
		SalesRep sr = quote.getCreator();
		if(sr != null){
			params.put("quoteCreator", StringUtils.trimToEmpty(sr.getFullName()));
		}
		sr = quote.getOppOwner();
		if(sr != null){
			params.put("oppOwner", StringUtils.trimToEmpty(sr.getFullName()));
		}
		params.put("fulfillmentSource", StringUtils.trimToEmpty(quoteHeader.getFulfillmentSrc()));
		List reviewerComments = null;
		if(sbInfo != null){
			reviewerComments = sbInfo.getReviewerComments();
		}
		if(reviewerComments == null){
			reviewerComments = new ArrayList();
		}
		params.put("reviewerComments", reviewerComments);
		
		List lineItems = ssQuote.getEqPartList();
		if(lineItems == null){
			lineItems = new ArrayList();
		}
		
		if(PartPriceConfigFactory.singleton().allowChannelMarginDiscount(quote) && (lineItems.size()>0)){
			params.put("isShowBPColumn", Boolean.TRUE);
		}else{
			params.put("isShowBPColumn", Boolean.FALSE);
		}
		
		params.put("lineItems", lineItems);
		
		List configrtnsList = ssQuote.getPartsPricingConfigrtnsList();
		Map configrtnsMap = ssQuote.getPartsPricingConfigrtnsMap();
		if(configrtnsList == null){
			configrtnsList = new ArrayList(0);
		}
		
		params.put("configrtnsList", configrtnsList);
		params.put("configrtnsMap", configrtnsMap);
		
		
		PartTableTotal ptTotal = new PartTableTotal(quote);
		
		String swTotalEntitledExtPriceStr = ptTotal.getTotalEntitledExtendedPrice(quote.getSoftwareLineItems(), ssQuote.isPricingCallFailed());
		String swTotalBidExtPriceStr = ptTotal.getTotalBidExtendedPrice(quote.getSoftwareLineItems(), ssQuote.isPricingCallFailed());
		String saasTotalEntitledExtPriceStr = ptTotal.getTotalEntitledExtendedPrice(quote.getSaaSLineItems(), ssQuote.isPricingCallFailed());
		String saasTotalBidExtPriceStr = ptTotal.getTotalBidExtendedPrice(quote.getSaaSLineItems(), ssQuote.isPricingCallFailed());
		
		params.put("swTotalEntitledExtPrice", swTotalEntitledExtPriceStr);
		params.put("swTotalBidExtPrice",swTotalBidExtPriceStr);
		params.put("saasTotalEntitledExtPrice", saasTotalEntitledExtPriceStr);
		params.put("saasTotalBidExtPrice",saasTotalBidExtPriceStr);
		params.put("swTotalDiscFromEntitled", calculateDiscount(swTotalBidExtPriceStr,swTotalEntitledExtPriceStr));
		params.put("saasTotalDiscFromEntitled", calculateDiscount(saasTotalBidExtPriceStr,saasTotalEntitledExtPriceStr));
		
		params.put("totalDiscFromEntitled", getTotalDiscFromEntitled(quote));
		params.put("totalEntitledExtPrice", ptTotal.getTotalEntitledExtendedPrice(quote.getLineItemList(),ssQuote.isPricingCallFailed()));
		params.put("totalBidExtPrice", ptTotal.getTotalBidExtendedPrice(quote.getLineItemList(),ssQuote.isPricingCallFailed()));
		
		try {
			String execSummaryHtmlContent = generateContent("execSummaryAsHTML.vm", params);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    Document doc = builder.parse(new ByteArrayInputStream(execSummaryHtmlContent.getBytes("utf-8")));

		    ITextRenderer renderer = new ITextRenderer();
		    renderer.setDocument(doc, null);

		    renderer.layout();
		    
		    renderer.createPDF(os);
		} catch (Exception e) {
		    LogContextFactory.singleton().getLogContext().error(util, e, "Error when create PDF");
			throw new QuoteException(e);
		}
	}
	
	private static boolean hasBLPrice(Quote quote){
		return (quote.getQuoteHeader().getSpeclBidFlag() == 1
				&& quote.getQuoteHeader().isPAQuote()
				&& !DecimalUtil.isEqual(quote.getExecSummary().getBaselineTotalPrice().doubleValue(), 0));
	}
	
	private static String getSVPLevel(Quote quote){
		QuoteHeader header = quote.getQuoteHeader();
		if(header.isPAQuote()){
		    return header.getTranPriceLevelCode();
		}
		if(header.isPAEQuote()){
			return "List (SRP)";
		}
		return "";
	}
	
	private static String generateContent(String templateName, Map params) throws QuoteException {
        String text = "";
        StringWriter writer = null;
        try {
            Properties p = new Properties();
            p.setProperty("resource.loader", "class");
            p.setProperty("class.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            Velocity.init(p);
            Template template = Velocity.getTemplate("appl/config/templates/" + templateName);
            VelocityContext context = new VelocityContext();
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Entry entry = (Entry) it.next();
                context.put((String) entry.getKey(), entry.getValue());
            }
            writer = new StringWriter();
            template.merge(context, writer);
            text = writer.toString();
        } catch (Exception e) {
            throw new QuoteException(e);
        }finally{
        	try {
				writer.close();
			} catch (IOException e) {
				LogContextFactory.singleton().getLogContext().error(util, e, "Error when generate content");
			}
        }
        return text;
    }
	
	private static String replaceAll ( String src , String fnd , String rep)throws Exception {
        if ( src == null || src.equals("")){ return ""; } 
        String dst = src ;
        int idx = dst.indexOf ( fnd);
        while ( idx >= 0){
            if(" ".equals(fnd)){
                if(dst.length()> idx + 1 && dst.charAt(idx+1) == ' '){
                    dst = dst.substring(0, idx)+ rep + dst.substring (idx + fnd.length (), dst.length () );
                    idx = dst.indexOf(fnd, idx + rep.length()); 
                }else{
                    idx = dst.indexOf(fnd, idx+1);
                    continue;
                }  
            }else{
                dst = dst.substring(0, idx)+ rep + dst.substring (idx + fnd.length (), dst.length () );
                idx = dst.indexOf(fnd, idx + rep.length()); 
            }
        }
     	return dst; 
	}
	
	public String htmlToRTF(String src){
		if(src == null || src.equals("")){ return ""; }
		String dst = src;
		
		try {
            dst = replaceAll(dst, "<BR>", "<br />");
            dst = replaceAll(dst, "<P>", "<p>");
			dst = replaceAll(dst, "<P ", "<p ");
			dst = replaceAll(dst, "</P>", "</p>");
			dst = replaceAll(dst, "<P/>", "<p/>");
			dst = replaceAll(dst, "<LI>", "<li>");
			dst = replaceAll(dst, "</LI>", "</li>");
        } catch (Exception e) {
            LogContextFactory.singleton().getLogContext().error(util, e, "Error when lowercase the html tags");
        }
		
		dst = dst.replaceAll("<!\\-\\-[^(\\-\\->)]*\\-\\->", "");
		
		dst = dst.replaceAll("<p[0-9a-zA-Z\\s\"'=#&%:\\-_!;,.\\()]*>","");
		dst = dst.replaceAll("<p[0-9a-zA-Z\\s\"'=#&%:\\-_!;,.\\()]*/\\s*>","\\\\par\\\\par ");
		dst = dst.replaceAll("<\\s*/\\s*p[^<^>]*\\s*>","\\\\par\\\\par ");
		dst = dst.replaceAll("<br\\s?[^<^>]*/\\s*>","\\\\par ");
		dst = dst.replaceAll("<li[0-9a-zA-Z\\s\"'=#&%:\\-_!;,.\\()]*>","    ");
		dst = dst.replaceAll("<li[0-9a-zA-Z\\s\"'=#&%:\\-_!;,.\\()]*/\\s*>","\\\\par ");
		dst = dst.replaceAll("<\\s*/\\s*li[^<^>]*\\s*>","\\\\par ");

		dst = dst.replaceAll("<[0-9a-zA-Z\\s/\"'=#&%:\\-_!;,.\\()]*>", "");

		try {
			dst = replaceAll(dst, "&amp;", "&");
		    dst = replaceAll(dst, "&lt;", "<");
		    dst = replaceAll(dst, "&gt;", ">");
		    dst = replaceAll(dst, "&quot;", "\"");
		    dst = replaceAll(dst, "&#039;", "'");
		    dst = replaceAll(dst, "&nbsp;", String.valueOf((char)32));
		    dst = replaceAll(dst, "\r\n", "\\par ");
		    dst = replaceAll(dst, "\n\r", "\\par ");
		    dst = replaceAll(dst, "\r", "\\par ");
		    dst = replaceAll(dst, "\n", "\\par ");
		    dst = replaceAll(dst, String.valueOf((char)160), String.valueOf((char)32));
		} catch (Exception e) {
		    LogContextFactory.singleton().getLogContext().error(util, e, "Error when transform the html marks to rtf marks");
		}
		
		
		return dst;
	}
	
	public String escapeXml(String content){
		return StringEscapeUtils.escapeXml(content);
	}
	
	
	private final static String[] predefinedEntities = new String[]{"&amp;","&nbsp;","&quot;","&gt;","&lt;","&copy;"};
	private static int maxLengthOfPredefinedEntity = 0;
	static{
		for(int i=0;i<predefinedEntities.length;i++){
			maxLengthOfPredefinedEntity = Math.max(maxLengthOfPredefinedEntity, predefinedEntities[i].length());
		}
	}
	
	public String escapeOthers(String content){
		try{
			content = replaceAll(content, "<BR>", "<br />");
			content = replaceAll(content, "<br>", "<br />");
			content = replaceAll(content, "<P", "<p");
			content = replaceAll(content, "</P>", "</p>");
			content = replaceAll(content, "<LI", "<li");
			content = replaceAll(content, "</LI>", "</li>");
			content = replaceAll(content, "<U", "<u");
			content = replaceAll(content, "</U>", "</u>");
			content = replaceAll(content, "<FONT", "<font");
			content = replaceAll(content, "</FONT>", "</font>");
			content = replaceAll(content, "<STRIKE", "<strike");
			content = replaceAll(content, "</STRIKE>", "</strike>");
			content = replaceAll(content, "<I", "<i");
			content = replaceAll(content, "</I>", "</i>");
			content = replaceAll(content, "<B", "<b");
			content = replaceAll(content, "</B>", "</b>");
			content = replaceAll(content, "<TABLE", "<table");
			content = replaceAll(content, "</TABLE>", "</table>");
			content = replaceAll(content, "<THEAD", "<thead");
			content = replaceAll(content, "</THEAD>", "</thead>");
			content = replaceAll(content, "<TBODY", "<tbody");
			content = replaceAll(content, "</TBODY>", "</tbody>");
			content = replaceAll(content, "<TFOOT", "<tfoot");
			content = replaceAll(content, "</TFOOT>", "</tfoot>");
			content = replaceAll(content, "<TH", "<th");
			content = replaceAll(content, "</TH>", "</th>");
			content = replaceAll(content, "<TB", "<tb");
			content = replaceAll(content, "</TB>", "</tb>");
			content = replaceAll(content, "<TD", "<td");
			content = replaceAll(content, "</TD>", "</td>");
			content = replaceAll(content, "<TR", "<tr");
			content = replaceAll(content, "</TR>", "</tr>");
			content = replaceAll(content, "<H", "<h");
			content = replaceAll(content, "</H", "</h");
			content = replaceAll(content, "<A", "<a");
			content = replaceAll(content, "</A>", "</a>");
			content = replaceAll(content, "<EM", "<em");
			content = replaceAll(content, "</EM>", "</em>");
			content = replaceAll(content, "<OL", "<ol");
			content = replaceAll(content, "</OL>", "</ol>");
			content = replaceAll(content, "<UL", "<ul");
			content = replaceAll(content, "</UL>", "</ul>");
			content = replaceAll(content, "<STRONG", "<strong");
			content = replaceAll(content, "</STRONG>", "</strong>");
			content = replaceAll(content, "<CAPTION", "<caption");
			content = replaceAll(content, "</CAPTION>", "</caption>");
			content = replaceAll(content, "<BLOCKQUOTE", "<blockquote");
			content = replaceAll(content, "</BLOCKQUOTE>", "</blockquote>");

			int currentAndPos = 0;
			StringBuffer contentSb = new StringBuffer(content);
			
//			while((currentAndPos = contentSb.indexOf("&", currentAndPos)) > 0){
//				String toCheck = contentSb.substring(currentAndPos, Math.min(currentAndPos+maxLengthOfPredefinedEntity, contentSb.length()));
//				for(int i=0;i<predefinedEntities.length;i++){
//					if(toCheck.indexOf(predefinedEntities[i])>=0){
//						currentAndPos += 1;
//						break;
//					}
//				}
//				contentSb.replace(currentAndPos, currentAndPos+1, "&amp;");
//				currentAndPos += 5;
//			}
			
			return patchIE(contentSb.toString());
		}catch(Exception e){
		    LogContextFactory.singleton().getLogContext().error(util, e, "Error when escapeOthers");
			return content;
		}
	}
	private static String patchIE(String s){
		StringBuffer content = new StringBuffer(s);
		Pattern p = Pattern.compile("<[a-zA-Z]+[0-9]*[^<^>]*(\\s[a-zA-Z]+\\s*=\\s*[0-9a-zA-Z#:;!_\\-/\\()]+[^<^>]*)+>");
		Pattern p1 = Pattern.compile("([a-zA-Z]+=[0-9a-zA-Z#:;!_\\-/\\()]+\\s*[^>]*)+");
		Matcher m = p.matcher(s);
		while(m.find()){
			String s0 = m.group(0);
			String s1 = s0.replaceAll("\\s*=\\s*","=").trim();
			Matcher m1 = p1.matcher(s1);
			
			while(m1.find()){
				String s10 = m1.group(0);
				String s2 = s10.replaceAll("=\"","=").replaceAll("=","=\"");
				String[] parts = s2.split(" ");
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<parts.length;i++){
					sb.append(parts[i]);
					if(!parts[i].endsWith("\"")){
						if(i != parts.length-1 && parts[i+1].indexOf("=")>=0){
							sb.append("\"");
						}else if(i == parts.length-1){
							sb.append("\"");
						}
					}
					if(i != parts.length-1){
						sb.append(" ");
					}
				}
				String s3 = s1.replaceAll(s10, sb.toString());
				int fromInx = content.indexOf(s0);
				if(fromInx >=0 )
				content.replace(fromInx, fromInx+s0.length(), s3);
			}
		}
		return content.toString();
	}
	public String toLocalTime(Date date, Locale loc, TimeZone tz){
		return com.ibm.dsw.quote.base.util.DateHelper.formatToLocalTime(date, "dd MMM yyyy HH:mm:ss z",tz, loc );
	}
	
	
	public static String getTotalDiscFromEntitled(Quote quote){
    	BrandTotalPriceCollector collector = new BrandTotalPriceCollector(quote);
    	Map sbPriceMap = collector.getSpecialBidPrices(QuoteConstants.DIST_CHNL_END_CUSTOMER,null,PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL);
    	Map entitledPriceMap = collector.getEntitledPrices(QuoteConstants.DIST_CHNL_END_CUSTOMER,sbPriceMap,PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL);
    	List entitledPrices = collector.convertMap2List(entitledPriceMap);
    	List specialBidPrices = collector.convertMap2List(sbPriceMap);
    	
        double entitledTotal = 0.0;
        double specialTotal = 0.0;
        for (int i = 0; i < entitledPrices.size(); i++) {
            BrandTotalPrice btp = (BrandTotalPrice) entitledPrices.get(i);
            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp.prcSumLevelCode) && PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL.equals(btp.revnStrmCategoryCode)) {
                entitledTotal = btp.localCurrencyPrice;
                break;
            }
        }
        if (entitledTotal == 0.0) {
            return "";
        }
        for (int i = 0; i < specialBidPrices.size(); i++) {
            BrandTotalPrice btp = (BrandTotalPrice) specialBidPrices.get(i);
            if (QuoteConstants.PRICE_SUM_LEVEL_TOTAL.equals(btp.prcSumLevelCode) && PartPriceConstants.PriceTotalRevnStrmCategory.NEW_ALL.equals(btp.revnStrmCategoryCode)) {
                specialTotal = btp.localCurrencyPrice;
                break;
            }
        }
        
        return DecimalUtil.calculateDiscount(specialTotal,entitledTotal);
    }
	
	public static String calculateDiscount(String specialTotal,String entitledTotal){
		if(specialTotal == null || entitledTotal == null || specialTotal.equals("") || entitledTotal.equals("")){
			return "";
		}else{
			try{
				DecimalFormat decimalFormat = new DecimalFormat();
				double specialTotalValue = decimalFormat.parse(specialTotal).doubleValue();
				double entitledTotalValue = decimalFormat.parse(entitledTotal).doubleValue();
				return DecimalUtil.calculateDiscount(specialTotalValue,entitledTotalValue);
			}catch(Exception e){
				LogContextFactory.singleton().getLogContext().error(util, e, "Error when create calculate discount");
			}
		}
		return "-";
	}
}
