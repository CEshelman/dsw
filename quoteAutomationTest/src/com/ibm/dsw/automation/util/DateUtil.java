package com.ibm.dsw.automation.util;

import java.text.SimpleDateFormat;

public class DateUtil {

	public String formatDateByFormat(java.util.Date date, String format) {
		String result = "";
		if (date != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				result = sdf.format(date);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static int getYear(java.util.Date date) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(date);
		return c.get(java.util.Calendar.YEAR);
	}

	
	public static int getMonth(java.util.Date date) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(date);
		int month = c.get(java.util.Calendar.MONTH) + 1 ;
		if(month < 10)
			month = 10 + month;
		return month;
	}

	
	public static int getDay(java.util.Date date) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(date);
		int day = c.get(java.util.Calendar.DAY_OF_MONTH);
		if(day < 10)
			day = 10 + day;
		return day;
	}

}
