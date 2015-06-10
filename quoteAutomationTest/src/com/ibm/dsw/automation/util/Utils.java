package com.ibm.dsw.automation.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class Utils {

	public static final String USER_FILE = "user";
	public static final String PGS_USER_FILE = "pgsUser";
	public static final String KEY_USERID = "userId";

	public static final String KEY_PASSWORD = "password";


	public static String getConfig(String key) throws MissingResourceException{
		ResourceBundle resourceBundle = ResourceBundle.getBundle("security");
		return resourceBundle.getString(key);
	}


	public static String getDateTimeMessage(String message) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date()) + " " + message;
	}
}
