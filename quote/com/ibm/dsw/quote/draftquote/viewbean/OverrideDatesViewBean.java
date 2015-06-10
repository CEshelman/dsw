/*
 * Created on Apr 4, 2007
 */
package com.ibm.dsw.quote.draftquote.viewbean;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.util.StringEncoder;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.viewbean.helper.ItemCoverageDates;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author Lee
 */
public class OverrideDatesViewBean extends DraftSQBaseViewBean {
	private transient LogContext log = LogContextFactory.singleton().getLogContext();
    private String partNum;
    private String seqNum;
    private boolean isFTL;
    
    private String startDateStr;
    private int startDateYear;
    private String startDateMonth;
    private int startDateDay;
    
    private String endDateStr;
    private int endDateYear;
    private String endDateMonth;
    private int endDateDay;
    
    private ItemCoverageDates backDate;    
    private String revnStrmCode = null;
    private boolean isSysCalEndDate = false;
    private boolean isSpecialBidRnwlPart = false;
    private boolean allowEditStartDate = false;
    private boolean allowEditEndDate = false;
    private boolean cmprssCvrageApplied = false;
    private boolean isEnforceEndDateByCondition = false;
    
    private Calendar calendar = Calendar.getInstance();
    
    private int year;
    private int month;
    private int day;

    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        
        partNum = params.getParameterAsString(DraftQuoteParamKeys.PART_NUM);
        seqNum = params.getParameterAsString(DraftQuoteParamKeys.SEQ_NUM);
        startDateStr = params.getParameterAsString(DraftQuoteParamKeys.START_DATE);
        endDateStr = params.getParameterAsString(DraftQuoteParamKeys.END_DATE);
        revnStrmCode = params.getParameterAsString(DraftQuoteParamKeys.REVN_STRM_CODE);

        //parameter values derived from revnstream code
        isSysCalEndDate = PartPriceConfigFactory.singleton().isSysCalEndDate(revnStrmCode);
        isFTL = PartPriceConfigFactory.singleton().isFTLPart(revnStrmCode);
        isEnforceEndDateByCondition = PartPriceConfigFactory.singleton().enforceEndDateByCondition(revnStrmCode);
          
        backDate = new ItemCoverageDates(quote);
        
        String lob = quote.getQuoteHeader().getLob().getCode();
        
        //get day/month/year of start date
        if (startDateStr != null && startDateStr.length() != 0) {
            Date startDate = DateUtil.parseDate(startDateStr,DateUtil.PATTERN1,getLocale());

            startDateDay = startDate.getDate();
            startDateMonth = DateUtil.formatDate(startDate,DateUtil.PATTERN2,getLocale());
            startDateYear = startDate.getYear() + 1900;
        }

        //get day/month/year of end date
        if (endDateStr != null && endDateStr.length() != 0) {
            Date endDate = DateUtil.parseDate(endDateStr,DateUtil.PATTERN1,getLocale());

            endDateDay = endDate.getDate();
            endDateMonth = DateUtil.formatDate(endDate,DateUtil.PATTERN2,getLocale());
            endDateYear = endDate.getYear() + 1900;
        }
        
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        
        isSpecialBidRnwlPart = params.getParameterAsBoolean(DraftQuoteParamKeys.IS_SPECIAL_BID_RNWL_PART);
        allowEditStartDate = params.getParameterAsBoolean(DraftQuoteParamKeys.ALLOW_EDIT_START_DATE);
        allowEditEndDate = params.getParameterAsBoolean(DraftQuoteParamKeys.ALLOW_EDIT_END_DATE);
        cmprssCvrageApplied = params.getParameterAsBoolean(DraftQuoteParamKeys.CMPRSS_CVRAGE_APPLIED);
    }
    
    private QuoteLineItem findLineItem(List list) throws ViewBeanException{
    	if(list == null || list.size() == 0){
    		log.debug(this, "can't find the line item[partNum:" 
    				         + partNum + ", seqNum:" + seqNum + "] return null");
    		throw new ViewBeanException("can't find the line item[partNum:" 
			         + partNum + ", seqNum:" + seqNum + "] return null");
    	} else {
    		for(int i = 0; i < list.size(); i++){
    			QuoteLineItem tmp = (QuoteLineItem)list.get(i);
    			
    			if(tmp.getPartNum().equals(partNum) 
    					&& String.valueOf(tmp.getSeqNum()).equals(seqNum)){
    				log.debug(this, "find the line item" + tmp.toString());
    				return tmp;
    			}
    		}
    	}
    	
    	return null;
    }

    private String formatCode2(String s) {
        if (s == null)
            return null;
        if (s.length() == 1)
            return "0"+s;
        else
            return s;
    }

    /**
     * generate start day options
     * @return
     */
    public Collection generateStartDayOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sDay = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);
        
        collection.add(new SelectOptionImpl(sDay, "", false));
        
        for (int i = 1; i <= DAY_OF_MONTH; i++) {
            if (startDateDay == i)
                collection.add(new SelectOptionImpl(String.valueOf(i), formatCode2(String.valueOf(i)), true));
            else
                collection.add(new SelectOptionImpl(String.valueOf(i), formatCode2(String.valueOf(i)), false));

        }

        return collection;
    }
    
    /**
     * generate end day options
     * @return
     */
    public Collection generateEndDayOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sDay = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);
        
        if(isSysCalEndDate()){
        	collection.add(new SelectOptionImpl(sDay, "", true));
        } else {
        	collection.add(new SelectOptionImpl(sDay, "", true));
        	
        	for (int i = 1; i <= DAY_OF_MONTH; i++) {
        		if (endDateDay == i)
        			collection.add(new SelectOptionImpl(String.valueOf(i), formatCode2(String.valueOf(i)), true));
        		else
        			collection.add(new SelectOptionImpl(String.valueOf(i), formatCode2(String.valueOf(i)), false));

        	}
        }

        return collection;
    }

    /**
     * generate start month options
     * @return
     */
    public Collection generateStartAnniversaryOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};
        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);

        collection.add(new SelectOptionImpl(labelString, "", false));

        for (int i = 1; i < labels.length; i++) {
	        labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
	        if (labelString.indexOf(startDateMonth) != -1)
	            collection.add(new SelectOptionImpl(labelString, labelString.substring(0,3), true));
	        else
	            collection.add(new SelectOptionImpl(labelString, labelString.substring(0,3), false));
        }
		    	
    	return collection;
    }

    /**
     * generate end month options
     * @return
     */
    public Collection generateEndAnniversaryOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};
        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);
        
        if(isSysCalEndDate()){
        	collection.add(new SelectOptionImpl(labelString + "    ", "", true));
        } else {
        	collection.add(new SelectOptionImpl(labelString, "", false));
        	
	        for (int i = 1; i < labels.length; i++) {
		        labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
		        if (labelString.indexOf(endDateMonth) != -1)
		            collection.add(new SelectOptionImpl(labelString, labelString.substring(0,3), true));
		        else
		            collection.add(new SelectOptionImpl(labelString, labelString.substring(0,3), false));
	        }
        }
        
    	return collection;
    }

    /**
     * generate start year options
     * @return
     */
    public Collection generateStartYearOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        collection.add(new SelectOptionImpl(sYear, "", false));
        int totalYears = getAdditionalYears();
        int maintStartYear = getMaintStartYear();

        for (int i=0;i<totalYears;i++) {
            if (startDateYear == maintStartYear + i) {
                collection.add(new SelectOptionImpl(String.valueOf(maintStartYear + i), String.valueOf(maintStartYear + i), true));
            }
            else {
                collection.add(new SelectOptionImpl(String.valueOf(maintStartYear + i), String.valueOf(maintStartYear + i), false));
            }
        }

        return collection;
    }
    
    /**
     * generate end year options
     * @return
     */
    public Collection generateEndYearOptions() {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        
        
        int totalYears = getAdditionalYears();
        int maintStartYear = getMaintStartYear();
        
        if(isSysCalEndDate()){
        	collection.add(new SelectOptionImpl(sYear, "", true));
        } else {
        	collection.add(new SelectOptionImpl(sYear, "", false));
        	
	        for (int i=0;i<totalYears;i++) {
	            if (endDateYear == maintStartYear + i) {
	                collection.add(new SelectOptionImpl(String.valueOf(maintStartYear + i), String.valueOf(maintStartYear + i), true));
	            }
	            else {
	                collection.add(new SelectOptionImpl(String.valueOf(maintStartYear + i), String.valueOf(maintStartYear + i), false));
	            }
	        }
        }

        return collection;
    }

    /**
     * @return Returns the yearRange.
     */
    public int getAdditionalYears() {
        return backDate.getTotalMaintYears();
    }
    
    /**
     * @return Returns the endDate.
     */
    public String getEndDateStr() {
        return endDateStr;
    }
    /**
     * @return Returns the partNum.
     */
    public String getPartNum() {
        return StringEncoder.textToHTML(partNum);
    }
    /**
     * @return Returns the seqNum.
     */
    public String getSeqNum() {
        return StringEncoder.textToHTML(seqNum);
    }
    /**
     * @return Returns the isFTL.
     */
    public boolean isFTL() {
        return isFTL;
    }
    /**
     * @return Returns the startDate.
     */
    public String getStartDateStr() {
        return startDateStr;
    }

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DraftSQBaseViewBean#getDisplayTabAction()
     */
    public String getDisplayTabAction() {
        return null;
    }
    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.draftquote.viewbean.DraftSQBaseViewBean#getPostTabAction()
     */
    public String getPostTabAction() {
        return null;
    }
    /**
     * @return Returns the endDateDay.
     */
    public String getEndDateDay() {
        return endDateDay + "";
    }
    /**
     * @return Returns the endDateMonth.
     */
    public String getEndDateMonth() {
        return endDateMonth + "";
    }
    /**
     * @return Returns the endDateYear.
     */
    public String getEndDateYear() {
        return endDateYear + "";
    }
    /**
     * @return Returns the startDateDay.
     */
    public String getStartDateDay() {
        return startDateDay + "";
    }
    /**
     * @return Returns the startDateMonth.
     */
    public String getStartDateMonth() {
        return startDateMonth + "";
    }
    /**
     * @return Returns the startDateYear.
     */
    public String getStartDateYear() {
        return startDateYear + "";
    }
    
    public boolean isBackDatingAllowed(){
    	return backDate.isBackDatingAllowed(revnStrmCode);
    }
    
    public boolean isFutureStartDateAllowed(){
    	return backDate.isFutureStartDateAllowed(revnStrmCode);
    }
    
    public int getMaintStartYear(){
    	return backDate.getMaintStartYear(revnStrmCode);
    }
    
    public int getMaintEndYear(){
    	return backDate.getMaintEndYear(revnStrmCode);
    }
    
    public int getBackDatingPastYearLimit(){
    	return backDate.getBackDatingPastYearLimit();
    }
    
    public boolean isSysCalEndDate(){
    	return isSysCalEndDate;
    }
    
    public int getCurrentYear(){
    	return year;
    }
    
    public int getCurrentMonth(){
    	return month;
    }
    
    public int getCurrentDay(){
    	return day;
    }
    
    public boolean isSpecialBidRnwlPart(){
    	return isSpecialBidRnwlPart;
    }
        
    public boolean isAllowEditStartDate(){
    	return allowEditStartDate;
    }
    
    public boolean isAllowEditEndDate(){
    	return allowEditEndDate;
    }
    
    public boolean isSubSeqFTLPart(){
    	return PartPriceConfigFactory.singleton().isSubFTLPart(revnStrmCode);
    }
    
    public boolean isEnforceEndDate(){
        return PartPriceConfigFactory.singleton().isEnforceEndDate(revnStrmCode);
    }
    /**
     * @return Returns the cmprssCvrageApplied.
     */
    public boolean isCmprssCvrageApplied() {
        return cmprssCvrageApplied;
    }
    
    public boolean isEnforceEndDateByCondition(){
    	return isEnforceEndDateByCondition;
    }
}