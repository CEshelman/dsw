package com.ibm.dsw.quote.base.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mailto:mgivney@us.ibm.com">Matt Givney</a><br/>
 *
 */
public class DateHelper {
    private static LogContext logger = LogContextFactory.singleton().getLogContext();

    /**
     * Constructor
     */
    public DateHelper() {
        super();
    }
    
    /**
     * Gets the current date as DDD MMM YYYY format
     * @return the current date as <code>String</code>
     */
    public static String getCurrentDateAsDDMMMYYYY(){
        try{
        	Date currDate = new Date(System.currentTimeMillis());
           	SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM yyyy");
           	return sdfDate.format(currDate);            
        }catch( Exception exception ){
            logger.error("--error-- couldnt convert date to DDMMMYYYY format", exception );
            return null;
        }
    }
    
    public static String getDateByFormat(Date date, String format) {
        return getDateByFormat(date, format, null);
    }
    
    public static String getDateByFormat(Date date, String format, Locale locale) {
        if (date == null || StringUtils.isBlank(format))
            return "";
        
        if (locale == null)
            locale = LocaleHelperImpl.getDefaultDSWLocale();
        
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat(format, locale);
            return sdfDate.format(date);
        } 
        catch (Exception e) {
            logger.error(DateHelper.class, e);
            return "";
        }
    }
    
    public static String getTimestampByFormat(Timestamp stamp,String format){
        Locale locale = null;
        
        if (stamp == null || StringUtils.isBlank(format))
            return "";
        
        if (locale == null)
            locale = LocaleHelperImpl.getDefaultDSWLocale();
        
        try {
            
            return formatToLocalTime(stamp, format, TimeZone.getTimeZone(ParamKeys.PARAM_GMT_TIMEZONE), locale);
        } 
        catch (Exception e) {
            logger.error(DateHelper.class, e);
            return "";
        }
        
        
    }
    
    public static int numberOfMonths( Date startDate, Date endDate )
    {
      	
        if (startDate==null || endDate==null )
            return -1;
        else{
//        		System.out.println("3. No of Days between 2 dates\n");
//        		Calendar c1 = Calendar.getInstance(); 	//new GregorianCalendar();
//        		Calendar c2 = Calendar.getInstance(); 	//new GregorianCalendar();
//        	    c1.set(1999, 0 , 20); 
//        	    c2.set(1999, 0 , 22); 
//        	    System.out.println("Days Between "+c1.getTime()+" and "						+ c2.getTime()+" is");
//        	    System.out.println((c2.getTime().getTime() - 					c1.getTime().getTime())/(24*3600*1000));
//        	    System.out.println();
//        		System.out.println("-------------------------------------");
//        	}
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            start.setTime( startDate );
            end.setTime( endDate );


        }
        
        return -1;
    }
    
    public static boolean validateDate(String year, String month, String day) {
        boolean valid = false;

        if (null == year || null == month || null == day || year.equals("") || month.equals("") || day.equals(""))
            return valid;

        int m = 0;
        int d = 0;
        int y = 0;
        try {
            y = Integer.parseInt(year);
            m = Integer.parseInt(month);
            d = Integer.parseInt(day);
        } catch (NumberFormatException e) {
            return valid;
        }

        switch (m) {
	        case 1:
	        case 3:
	        case 5:
	        case 7:
	        case 8:
	        case 10:
	        case 12:
	            if (d <= 31)
	                valid = true;
	            break;
	        case 4:
	        case 6:
	        case 9:
	        case 11:
	            if (d <= 30)
	                valid = true;
	            break;
	        case 2:
	            if ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) {
	                if (d <= 29)
	                    valid = true;
	            } else {
	                if (d <= 28)
	                    valid = true;
	            }
	            break;
	        default:
	            break;
        }
        return valid;
    }
    
    public static Date getDatefromString(String sDateString) {
		//YYYYMMDDHHMMSS
		Date aDate = null;

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

		if (null != sDateString) {
			if (sDateString.length() != 14) {
				throw new IllegalStateException(
						"getDatefromString(): incorrect dateString format");
			}

			int yr = Integer.parseInt(sDateString.substring(0, 4));

			int mnth = Integer.parseInt(sDateString.substring(4, 6));
			int day = Integer.parseInt(sDateString.substring(6, 8));

			int hr = Integer.parseInt(sDateString.substring(8, 10));

			int min = Integer.parseInt(sDateString.substring(10, 12));
			int sec = Integer.parseInt(sDateString.substring(12, 14));

			String sdtString = yr + "-" + mnth + "-" + day + "-" + hr + "-"
					+ min + "-" + sec;

			try {
				aDate = df.parse(sdtString);
			} catch (java.text.ParseException ex) {
				logger.debug(DateHelper.class, "unable to parse charters string to a date format...... setting date to TODAY");

			}
		}

		return aDate;
	}
    
    /**
     * Elapsed days based on current time
     *
     * @param date Date
     *
     * @return int number of days
     */
     public static int getElapsedDays(Date date) {
         return elapsed(date, Calendar.DATE);
     }
    /**
     * Elapsed days based on two Date objects
     *
     * @param d1 Date
     * @param d2 Date
     *
     * @return int number of days
     */
     public static int getElapsedDays(Date d1, Date d2) {
         return elapsed(d1, d2, Calendar.DATE);
     }
     /**
     * Elapsed months based on current time
     *
     * @param date Date
     *
     * @return int number of months
     */
     public static int getElapsedMonths(Date date) {
         return elapsed(date, Calendar.MONTH);
     }
    /**
     * Elapsed months based on two Date objects
     *
     * @param d1 Date
     * @param d2 Date
     *
     * @return int number of months
     */
     public static int getElapsedMonths(Date d1, Date d2) {
         return elapsed(d1, d2, Calendar.MONTH);
     }
      /**
     * Elapsed years based on current time
     *
     * @param date Date
     *
     * @return int number of years
     */
     public static int getElapsedYears(Date date) {
         return elapsed(date, Calendar.YEAR);
     }
    /**
     * Elapsed years based on two Date objects
     *
     * @param d1 Date
     * @param d2 Date
     *
     * @return int number of years
     */
     public static int getElapsedYears(Date d1, Date d2) {
         return elapsed(d1, d2, Calendar.YEAR);
     }
      /**
      * All elaspsed types
      *
      * @param g1 GregorianCalendar
      * @param g2 GregorianCalendar
      * @param type int (Calendar.FIELD_NAME)
      *
      * @return int number of elapsed "type"
      */
     private static int elapsed(GregorianCalendar g1, GregorianCalendar g2, int type) {
         GregorianCalendar gc1;
         GregorianCalendar gc2;
         int elapsed = 0;
         // Create copies since we will be clearing/adding
         if (g2.after(g1)) {
             gc2 = (GregorianCalendar) g2.clone();
             gc1 = (GregorianCalendar) g1.clone();
         } else  {
             gc2 = (GregorianCalendar) g1.clone();
             gc1 = (GregorianCalendar) g2.clone();
         }
         if (type == Calendar.MONTH || type == Calendar.YEAR) {
             gc1.clear(Calendar.DATE);
             gc2.clear(Calendar.DATE);
         }
         if (type == Calendar.YEAR) {
             gc1.clear(Calendar.MONTH);
             gc2.clear(Calendar.MONTH);
         }
         while (gc1.before(gc2)) {
             gc1.add(type, 1);
             elapsed++;
         }
         return elapsed;
     }
      /**
      * All elaspsed types based on date and current Date
      *
      * @param date Date
      * @param type int (Calendar.FIELD_NAME)
      *
      * @return int number of elapsed "type"
      */
     private static int elapsed(Date date, int type) {
         return elapsed(date, new Date(), type);
     }
      /**
      * All elaspsed types
      *
      * @param d1 Date
      * @param d2 Date
      * @param type int (Calendar.FIELD_NAME)
      *
      * @return int number of elapsed "type"
      */
     private static int elapsed(Date d1, Date d2, int type) {
         Calendar cal = Calendar.getInstance();
         cal.setTime(d1);
         GregorianCalendar g1 = new GregorianCalendar(
             cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
         cal.setTime(d2);
         GregorianCalendar g2 = new GregorianCalendar(
             cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
         return elapsed(g1, g2, type);
     }
     
     public static int getElapsedSeconds(Date date1, Date date2) {
         Calendar cal = Calendar.getInstance();
         cal.setTime(date1);
         GregorianCalendar g1 = new GregorianCalendar(
             cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE),
             cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
         cal.setTime(date2);
         GregorianCalendar g2 = new GregorianCalendar(
             cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE),
             cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
         return elapsed(g1, g2, Calendar.SECOND);
     }
     
     public static String formatToLocalTime(Date date, String pattern, TimeZone timeZone, Locale locale) {
     	
     	if (date == null) return "";
     	
     	long millis = date.getTime() ;// turn off DST - dayLightSaving * 60 * 1000;
     	return DateFormatUtils.format(millis, pattern, timeZone, locale);
     }
     
     public static String formatToGMTTime(Date date, String pattern,Locale locale) {
     	return formatToLocalTime(date, pattern, TimeZone.getTimeZone(ParamKeys.PARAM_GMT_TIMEZONE), locale);
     }
     
    public static Date getDateFromTimeZone(String timezone) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        Date date1 = cal.getTime();
        
        cal.setTimeZone(TimeZone.getTimeZone(timezone));
        cal.set(Calendar.MILLISECOND, 0);
        Date date2 = cal.getTime();
        
        long timeInMillis = date1.getTime() - (date2.getTime() - date1.getTime());
        cal.setTimeInMillis(timeInMillis);
        return cal.getTime();
    }

    public static Date getTimeOfMidnight(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }
    
    // Return the month end date of input date
    public static Date extendToMonthEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
    
    // Return the date difference from now to input date
    public static int dateDifferenceFromNow(Date date) {
        int sign = 1;
        Date now = Calendar.getInstance().getTime();
        Date nowDate = DateUtils.truncate(now, Calendar.DATE);
        Date destDate = DateUtils.truncate(date, Calendar.DATE);
        
        if (destDate.before(now))
            sign = -1;
        
        int diff = sign * com.ibm.ead4j.common.util.DateHelper.singleton().daysDifference(nowDate, destDate);

        return diff;
    }
    
    public static String convertDateByTimeZone(Date date, String pattern, TimeZone fromTimeZone, 
            TimeZone toTimeZone, Locale locale) {
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setTimeZone(fromTimeZone);
        cal.set(Calendar.MILLISECOND, 0);
        
        return DateHelper.formatToLocalTime(cal.getTime(), pattern, toTimeZone, locale);
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
    
    public static boolean isDateBeforeToday(Date date){
    	Date today = getCurrentYYYYMMDDDate();
    	
    	if(date == null){
    		return false;
    	}
    	return date.before(today);
    }
    
    public static boolean differenceLessThanOneYear(Date date1, Date date2) {
        if (date1 == null || date2 == null)
            return false;
        
        Date d1 = date1.before(date2) ? date1 : date2;
        Date d2 = date1.before(date2) ? date2 : date1;
        
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        c1.add(Calendar.YEAR, 1);
        d1 = c1.getTime();
        
        return d1.after(d2);
    }

}
