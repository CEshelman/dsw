package com.ibm.dsw.quote.draftquote.util.date;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateUtils;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DateUtil</code> class provide basic functions for Date
 * calculation
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Mar 28, 2007
 */

public class DateUtil {

    private static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    public static final String PATTERN = "yyyy-MM-dd";

    public static final String PATTERN1 = "dd MMM yyyy";

    public static final String PATTERN2 = "MMM";
    
    public static final String PATTERN3 = "MM/dd/yy";
    
    public static final String PATTERN4 = "yyyyMMdd";
    
    public static final String PATTERN5 = "dd-MMM-yyyy";

    public static final String PATTERN6 = "MM/dd/yyyy";

    //IMPORTANT : don't set it except testing purpose
    public static Date currentDate = null;

    public static Date getCurrentDate() {
        if (currentDate == null) {
            Calendar c = Calendar.getInstance();
            return new Date(c.getTimeInMillis());
        } else {
            return currentDate;
        }
    }

    public static Date plusOneYearMinusOneDay(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        //  get today's date
        int day = c.get(Calendar.DATE);
        // set year to 1 year from now
        c.add(Calendar.YEAR, 1);
        c.add(Calendar.DATE, -1);

        return new Date(c.getTimeInMillis());
    }

    public static Date getNonFTLEndDate(Date d) {

        Date nextYearDate = plusOneYearMinusOneDay(d);

        return moveToLastDayofMonth(nextYearDate);

    }

    public static boolean isFirstDayOfMonth(Date d) {
        return d.getDate() == 1;
    }

    public static boolean isLastDayOfMonth(Date d) {

        Date temp = moveToLastDayofMonth(d);
        return temp.getDate() == d.getDate();
    }

    public static Date moveToLastDayofMonth(Date d) {

        Calendar c = Calendar.getInstance();
        c.setTime(d);

        int day = c.get(Calendar.DATE);
        if (1 != day) {
            c.set(Calendar.DATE, 1);

        }
        c.add(Calendar.MONTH, 1);

        c.add(Calendar.DATE, -1);
        return new Date(c.getTimeInMillis());
    }

    public static Date plusOneDay(java.util.Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, 1);
        return new java.sql.Date(c.getTimeInMillis());
    }

    public static Date minusOneDay(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, -1);
        return new java.sql.Date(c.getTimeInMillis());
    }

    public static Date minusYears(Date d, int years) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.YEAR, -years);
        return new java.sql.Date(c.getTimeInMillis());
    }

    /**
     * @param startDates
     * @param endDates
     * @param firstYearStart
     * @param firstYearEnd
     */
    /*public static void fillDateList(List startDates, List endDates, Date firstYearStart, Date firstYearEnd) {

        startDates.add(firstYearStart);
        endDates.add(firstYearEnd);

        Date end = firstYearEnd;

        for (int i = 1; i <= PartPriceConstants.MAX_MAINT_COVERAGE_YEARS; i++) {
            Date start = DateUtil.plusOneDay(end);
            end = DateUtil.plusOneYearMinusOneDay(start);
            startDates.add(start);
            endDates.add(end);
        }
    }*/

    public static Date parseDate(String value, String pattern) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            return new Date(formatter.parse(value).getTime());
        } catch (Throwable e) {
            logContext.debug(DateUtil.class, "Parsing date error, value = " + value + "err=" + e.getMessage());
            return null;

        }
    }
    
    public static java.util.Date parseUtilDate(String value, String pattern) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            return formatter.parse(value);
        } catch (Throwable e) {
            logContext.debug(DateUtil.class, "Parsing date error, value = " + value + "err=" + e.getMessage());
            return null;
        }
    }

    public static Date parseDate(String value, String pattern1, String pattern2) {
        Date tempDate = parseDate(value, pattern1);
        if (tempDate == null){
            tempDate = parseDate(value, pattern2);
        }
        return tempDate;
    }
    
    public static Date parseDate(String value, String pattern, Locale locale) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
            return new Date(formatter.parse(value).getTime());
        } catch (Throwable e) {
            logContext.debug(DateUtil.class, "Parsing date error, value = " + value + "err=" + e.getMessage());
            return null;

        }
    }

    public static Date parseDate(String value) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DateUtil.PATTERN);
            return new Date(formatter.parse(value).getTime());
        } catch (Throwable e) {
            logContext.debug(DateUtil.class, "Parsing date error, value = " + value + "err=" + e.getMessage());
            return null;

        }
    }
    
    public static String formatDate(Date d, String pattern) {
        if (null == d) {
            return "";
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            return formatter.format(d);
        } catch (Throwable e) {
            logContext.debug(DateUtil.class, "format date error, value = " + d + "err=" + e.getMessage());
            return null;

        }
    }
    
    public static String formatDate(java.util.Date d, String pattern) {
        if (null == d) {
            return "";
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            return formatter.format(d);
        } catch (Throwable e) {
            logContext.debug(DateUtil.class, "format date error, value = " + d + "err=" + e.getMessage());
            return null;

        }
    }
    
    /**
     * format a date as a string with given format pattern.
     * @param d mandatory.
     * @param pattern mandatory.
     * @param timezone optional. may retrieve system default timezone by TimeZoneUtils class.
     * @param locale optional. 
     * @return
     */
    public static String formatDate(java.util.Date d, String pattern, TimeZone timezone, Locale locale){
        if (null == d) {
            return "";
        }
        try {
        	SimpleDateFormat formatter = null;
        	if(locale == null)
        		formatter = new SimpleDateFormat(pattern);
        	else 
        		formatter = new SimpleDateFormat(pattern, locale);
        	if(timezone != null){
        		formatter.setTimeZone(timezone);
        	}
            return formatter.format(d);
        } catch (Throwable e) {
            logContext.debug(DateUtil.class, "format date error, value = " + d + "err=" + e.getMessage());
            return null;

        }
    }
    
    public static String formatDate(java.util.Date d, String pattern, Locale locale) {
        if (null == d) {
            return "";
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
            return formatter.format(d);
        } catch (Throwable e) {
            logContext.debug(DateUtil.class, "format date error, value = " + d + "err=" + e.getMessage());
            return null;

        }
    }

    public static String formatDate(Date d, String pattern, Locale locale) {
        if (null == d) {
            return "";
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
            return formatter.format(d);
        } catch (Throwable e) {
            logContext.debug(DateUtil.class, "format date error, value = " + d + "err=" + e.getMessage());
            return null;

        }
    }

    public static String formatDate(Date d) {
        if (null == d) {
            return "";
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DateUtil.PATTERN);
            return formatter.format(d);
        } catch (Throwable e) {
            logContext.debug(DateUtil.class, "format date error, value = " + d + "err=" + e.getMessage());
            return null;

        }
    }

    public static Date plusOneYear(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.MONTH, 12);
        return new java.sql.Date(c.getTimeInMillis());
    }

    /**
     * 
     * @param currentDate
     * @param anniversaryDate
     *            only the month and day is used
     * @return
     */
    public static Date getNextAnniversary(java.util.Date currentDate, java.util.Date anniversaryDate) {
        String sDate = formatDate(anniversaryDate, PATTERN);
        int pos = sDate.indexOf("-");
        String monthAndDay = sDate.substring(pos);
        int year = currentDate.getYear() + 1900;

        Date firstAnniversaryDate = parseDate(year + monthAndDay, PATTERN);

        if (firstAnniversaryDate.after(currentDate)) {
            return firstAnniversaryDate;
        } else {
            return plusOneYear(firstAnniversaryDate);
        }
    }

    public static int calculateFullCalendarMonths(Date startDate, Date endDate) {

        if ((startDate == null) || (endDate == null)) {
            return -1;
        }
        
        if(isYMDEqual(plusOneYearMinusOneDay(startDate),endDate)){
            return 12;
        }

        int yearSpan = endDate.getYear() - startDate.getYear();

        int months = (yearSpan * 12 + endDate.getMonth()) - (startDate.getMonth() + 1);

        // start date is the first day of month
        if (DateUtil.isFirstDayOfMonth(startDate)) {
            months++;
        }

        Date lastDayOfMonth = DateUtil.moveToLastDayofMonth(endDate);

        if (endDate.getDate() == lastDayOfMonth.getDate()) {
            months++;
        }

        return months;

    }

    public static Date moveToNextFirstDayOfMonth(Date d) {
        if (DateUtil.isFirstDayOfMonth(d)) {
            // already first day of month
            return d;
        } else {
            // move to first day of next month
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            c.add(Calendar.MONTH, 1);
            c.set(Calendar.DATE, 1);
            return new Date(c.getTimeInMillis());
        }
    }

    public static int calculateWeeks(Date startDate, Date endDate) {

        if ((startDate == null) || (endDate == null)) {
            return -1;
        }
        return (int) ((endDate.getTime() - startDate.getTime()) / (7 * 24 * 3600 * 1000));

    }

    public static boolean isEqual(Date d1, Date d2) {

        if ((null == d1) || (null == d2)) {
            return false;
        }
        return d1.equals(d2);
    }

    public static boolean isYMDEqual(java.util.Date d1, java.util.Date d2) {
        if ((null == d1) || (null == d2)) {
            return false;
        }

        return formatDate(d1, PATTERN).equals(formatDate(d2, PATTERN));

    }

    public static String getCurrentDateForErrorDisplay() {
        String pattern = "yy/MM/dd hh:mm:ss";
        Date d = getCurrentDate();

        return formatDate(d, pattern);
    }
    
    public static Date minusDays(Date d, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, -days);
        return new Date(c.getTimeInMillis());
    }
    
    public static Date addDays(Date d, int days){
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, days);
        
        return new Date(c.getTimeInMillis());
    }
    
    public static Date getCurrentYYYYMMDDDate(){
    	Calendar c = Calendar.getInstance();
    	
    	int year = c.get(Calendar.YEAR);
    	int month = c.get(Calendar.MONTH);
    	int day = c.get(Calendar.DAY_OF_MONTH);
    	
    	c.clear();
    	c.set(year, month, day);
    	
    	return new Date(c.getTimeInMillis());
    }
    
    public static String getCurrentDateString(){
    	return formatDate(getCurrentYYYYMMDDDate(), PATTERN4);
    }
    
    public static boolean isDateBeforeToday(Date date){
    	Date today = getCurrentYYYYMMDDDate();
    	
    	if(date == null){
    		return false;
    	}
    	return date.before(today);
    }
    
    public static boolean isDateAfterToday(Date date){
    	Date today = getCurrentYYYYMMDDDate();
    	
    	if(date == null){
    		return false;
    	}
    	
    	//Change date to YYYY-MM-dd format
    	date = parseDate(formatDate(date), PATTERN);
    	return date.after(today);
    }
    
    //partial month should be considered whole month
    public static int calculateWholeMonths(Date startDate, Date endDate){
        if ((startDate == null) || (endDate == null)) {
            return -1;
        }

        int yearSpan = endDate.getYear() - startDate.getYear();

        return ((yearSpan * 12) + (endDate.getMonth() - startDate.getMonth() + 1));
    }
    
    public static Date getFirstDayDateOfCurrentMonth(){
    	Calendar c = Calendar.getInstance();
    	
    	int year = c.get(Calendar.YEAR);
    	int month = c.get(Calendar.MONTH);
    	
    	c.clear();
    	c.set(year, month, 1);
    	
    	return new Date(c.getTimeInMillis());        
    }
    
    public static Date plusMonthMinusOneDay(Date startDate, int months){
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.MONTH, months);
        c.add(Calendar.DATE, -1);
        return new Date(c.getTimeInMillis());
    }

    public static Date plusMonth(Date startDate, int months){
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.MONTH, months);
        return new Date(c.getTimeInMillis());
    }
    
    public static boolean isDateWithinOneMonthFromToday(Date startDate){
        Date today = getCurrentYYYYMMDDDate();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DAY_OF_MONTH, 1);
        
        Date oneMonthLater = new Date(c.getTimeInMillis());
        
        return startDate.before(oneMonthLater);
    }
    
    public static boolean isWithinSameQuarterYear(java.util.Date d1,java.util.Date d2){
    	if (null == d1 || null == d2){
    		return false;
    	}
    	if (d1.getYear()!=d2.getYear()){
    		return false;
    	}
    	int m1 = d1.getMonth()+1;
    	int m2 = d2.getMonth()+1;
    	if(m1 >= 1 && m1 <= 3){
    		return m2 >= 1 && m2 <= 3;
    	}else if(m1 >= 4 && m1 <=6 ){
    		return m2 >= 4 && m2 <= 6;
    	}else if(m1 >= 7 && m1 <= 9){
    		return m2 >= 7 && m2 <= 9;
    	}else{
    		return m2 >= 10 && m2 <= 12;
    	}
    }
    
    public static boolean isWithinSameYear(java.util.Date d1,java.util.Date d2){
    	if (null == d1 || null == d2){
    		return false;
    	}
    	return d1.getYear()==d2.getYear();
    }

    /**
     * judge if the param1 is before current date, is so then return true
     * @param expirationDate
     * @return
     */
    public static boolean isExpirationDateBeforeToday(java.util.Date expirationDate) {
	   if (expirationDate == null) {
		   return true;
	   }

        Calendar curr = Calendar.getInstance();
        //Check to make sure the quot expiration date is not before today's date
        java.util.Date now = curr.getTime();
        java.util.Date currDate = DateUtils.truncate(now, Calendar.DATE);

        if (currDate.after(expirationDate)) {
            return true; 
        }
       return false;
	}
    
    public static int calculateDateBefore(java.util.Date first, java.util.Date second){
    	Calendar firstCal =  Calendar.getInstance();
    	firstCal.setTime(first);
    	Calendar secondCal =  Calendar.getInstance();
    	secondCal.setTime(second);
    	
    	 int days = ((int) (secondCal.getTime().getTime() / 1000) - (int) (firstCal   
                 .getTime().getTime() / 1000)) / 3600 / 24;   
          
         return days;   
    }
    
    /**
     * return the whole months between the beginning date<code>startDate</code> and the end date<code>endDate</code>.
     * 
     * @param startDate
     * @param endDate
     * @return whole months
     */
    public static int calculateFullMonths(Date startDate, Date endDate)
    {
        if ((startDate == null) || (endDate == null))
        {
            return -1;
        }

        Calendar startDay = Calendar.getInstance();
        startDay.setTime(startDate);

        Calendar endDay = Calendar.getInstance();
        endDay.setTime(endDate);

        int yearSpan = endDay.get(Calendar.YEAR) - startDay.get(Calendar.YEAR);

        int months = (yearSpan * 12 + endDay.get(Calendar.MONTH)) - (startDay.get(Calendar.MONTH));

        // start date is the first day of month and end date is the last day of month
        if (1 == startDay.get(Calendar.DATE) && endDay.getActualMaximum(Calendar.DATE) ==  endDay.get(Calendar.DATE))
        {
            months++;
        }
        
        Calendar expectedMonth = Calendar.getInstance();
        expectedMonth.set(startDay.get(Calendar.YEAR), startDay.get(Calendar.MONTH), startDay.get(Calendar.DATE));
        expectedMonth.add(Calendar.MONTH, months);
        expectedMonth.add(Calendar.DATE, -1);
        if (expectedMonth.get(Calendar.MONTH) == endDay.get(Calendar.MONTH) && expectedMonth.get(Calendar.DATE) > endDay.get(Calendar.DATE))
        {
            months --;
        }

        return months;
    }
    
    /** return the last day of current quarter          
     * @Methods Name getLastDayOfQuarter 
     * @return Date 
     */  
    public static java.util.Date getLastDayOfQuarter(java.util.Date date)   {     
        Calendar cDay = Calendar.getInstance();     
        cDay.setTime(date);  
        int curMonth = cDay.get(Calendar.MONTH);  
        if (curMonth >= Calendar.JANUARY && curMonth <= Calendar.MARCH){    
             cDay.set(Calendar.MONTH, Calendar.MARCH);  
        }  
        if (curMonth >= Calendar.APRIL && curMonth <= Calendar.JUNE){    
             cDay.set(Calendar.MONTH, Calendar.JUNE);  
        }  
        if (curMonth >= Calendar.JULY && curMonth <= Calendar.AUGUST) {    
             cDay.set(Calendar.MONTH, Calendar.AUGUST);  
        }  
        if (curMonth >= Calendar.OCTOBER && curMonth <= Calendar.DECEMBER) {    
             cDay.set(Calendar.MONTH, Calendar.DECEMBER);  
        }  
             cDay.set(Calendar.DAY_OF_MONTH, cDay.getActualMaximum(Calendar.DAY_OF_MONTH));  
             cDay.set(Calendar.HOUR_OF_DAY, 0);
             cDay.set(Calendar.MINUTE, 0);
             cDay.set(Calendar.SECOND, 0);
             return cDay.getTime();     
        }  
    
    /** return the first day of current quarter          
     * @Methods Name getFirstDayOfQuarter 
     * @return Date 
     */  
    public static java.util.Date getFirstDayOfQuarter(java.util.Date date)   {     
        Calendar cDay = Calendar.getInstance();     
        cDay.setTime(date);  
        int curMonth = cDay.get(Calendar.MONTH);  
        if (curMonth >= Calendar.JANUARY && curMonth <= Calendar.MARCH){    
             cDay.set(Calendar.MONTH, Calendar.JANUARY);  
        }  
        if (curMonth >= Calendar.APRIL && curMonth <= Calendar.JUNE){    
             cDay.set(Calendar.MONTH, Calendar.APRIL);  
        }  
        if (curMonth >= Calendar.JULY && curMonth <= Calendar.AUGUST) {    
             cDay.set(Calendar.MONTH, Calendar.JULY);  
        }  
        if (curMonth >= Calendar.OCTOBER && curMonth <= Calendar.DECEMBER) {    
             cDay.set(Calendar.MONTH, Calendar.OCTOBER);  
        }  
             cDay.set(Calendar.DAY_OF_MONTH, 1); 
             cDay.set(Calendar.HOUR_OF_DAY, 0);
             cDay.set(Calendar.MINUTE, 0);
             cDay.set(Calendar.SECOND, 0);
             return cDay.getTime();     
        }
    public static void main(String[] args){
        Date startDate = new Date(115, 1, 28);
        Date endDate = new Date(115, 6, 29);
    	System.out.println(DateUtil.calculateFullMonths(startDate, endDate));

    }
}
