package com.ibm.dsw.quote.audit.viewbean;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.ibm.dsw.quote.audit.domain.QuoteAuditHistInfo;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteAuditHistoriesViewBean<code> class.
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: Dec 27, 2010
 */
public class QuoteAuditHistoriesViewBean extends BaseViewBean {
	
	private transient List auditHistoryList = null;
	private String sqoReference = null;
	private final static List actionsNotShowValue = Arrays.asList(new String[] {
			"REQST_ICN", "REQST_PRE_CRED_CHK", "SEND_PA_REG_EMAIL",
			"UNLOCK_PAO_ACCESS", "UPDT_PRTNR_IDOC" });
	private final static List actionsNeedFormatValue = Arrays.asList(new String[] { 
			"CHG_EXP_DATE", "CHG_START_DATE","CHG_LI_ST_DT", "CHG_LI_END_DT" });
	private final static String[] valuePatterns = new String[]{"yyyy-MM-dd","MM/dd/yyyy"};
	private final static String requiredPattern = "dd MMM yyyy";
	
	public void collectResults(Parameters params) throws ViewBeanException {
		LogContext logContext = LogContextFactory.singleton().getLogContext();
		super.collectResults(params);
		this.setSqoReference(params.getParameterAsString(ParamKeys.PARAM_QUOTE_NUM));
		List list = (List) params.getParameter(ParamKeys.PARAM_QT_AUDIT_HIST_LIST);
		if(list == null){
			list = new LinkedList();
		}
		auditHistoryList = list;
		
		if(auditHistoryList!=null&&auditHistoryList.size()>0){
			for (Iterator iterator = auditHistoryList.iterator(); iterator
					.hasNext();) {
				QuoteAuditHistInfo auditHist = (QuoteAuditHistInfo) iterator.next(); 
				if (actionsNeedFormatValue.contains(auditHist.getUserAction())){
					try {
						auditHist.setNewVal(parseDateWithLeniency(auditHist.getNewVal(),valuePatterns,requiredPattern,true));
					} catch (ParseException e) {
						logContext.error(this, e.getMessage());
					}
					try {
						auditHist.setOldVal(parseDateWithLeniency(auditHist.getOldVal(),valuePatterns,requiredPattern,true));
					} catch (ParseException e) {
						logContext.error(this, e.getMessage());
					}
				}
				if(actionsNotShowValue.contains(auditHist.getUserAction())){
				    auditHist.setNewVal("");
				    auditHist.setOldVal("");
				}
				
				if(auditHist.getModDate()!=null){
					auditHist.setModDateStr(DateHelper.formatToLocalTime(auditHist.getModDate(),
							"dd MMM yyyy HH:mm:ss zzz", this.getDisplayTimeZone(), locale));
				}
			}
		}
		
	}

	public List getAuditHistoryList() {
		return auditHistoryList;
	}

	public void setAuditHistoryList(List auditHistoryList) {
		this.auditHistoryList = auditHistoryList;
	}
	
	

    protected String getI18NString(String key, String basename, Locale locale,String[] params) {
        String i18nText = getI18NString(key, basename,locale);
        if(params!=null){
            i18nText = MessageFormat.format(i18nText,(String[])params);
        }
        
        return i18nText;
				
    }
    

    protected String getI18NString(String key, String basename, Locale locale) {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();

        Object i18nValue = appCtx.getI18nValue(basename, locale, key);
        if (i18nValue instanceof String) {
            return (String) i18nValue;
        } else {
            return key;
        }
    }

	public String getSqoReference() {
		return sqoReference;
	}

	public void setSqoReference(String sqoReference) {
		this.sqoReference = sqoReference;
	}
	 /**
	 * Parses a string representing a date by trying a parser.
	 * 
	 * The parse will try the parse pattern in turn. 
	 * A parse is only deemed successful if it parses the whole of the input string. 
	 * If no parse patterns match, a ParseException is thrown.
	 * 
	 * @param str
	 *            the date string to parse, not null
	 * @param parsePatterns
	 *            parsePatterns  the date format patterns to use, see SimpleDateFormat, not null
	 * @param lenient
	 *            Specify whether or not date/time parsing is to be lenient.
	 * @return the parsed date
	 * @throws IllegalArgumentException
	 *             if the date string or pattern array is null
	 * @throws ParseException
	 *             if none of the date patterns were suitable
	 * @see java.util.Calender#isLenient()
	 * @throws ParseException
	 */
	private String parseDateWithLeniency(String str,
			String[] parsePatterns,String requiredPattern, boolean lenient) throws ParseException {
		if (str == null || parsePatterns == null || requiredPattern == null) {
			throw new IllegalArgumentException(
					"Date and Patterns must not be null");
		}

		SimpleDateFormat parser = new SimpleDateFormat();
		parser.setLenient(lenient);
		ParsePosition pos = new ParsePosition(0);
		for (int i = 0; i < parsePatterns.length; i++) {
			String pattern = parsePatterns[i];
			parser.applyPattern(pattern);
			pos.setIndex(0);
			Date date = parser.parse(str, pos);
			if (date != null && pos.getIndex() == str.length()) {
				return DateHelper.getDateByFormat(date, requiredPattern, locale);
			}
		}
		throw new ParseException("Unable to parse the date: " + str, -1);
	}
	
}
