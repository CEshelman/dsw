package com.ibm.dsw.automation.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.testng.log4testng.Logger;


public class EncryptManager {


	private static Logger logger = Logger.getLogger(EncryptManager.class);
	
	private static String createFileName() throws MissingResourceException{
		DateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");

		String path = "";
		try {
			path = Utils.getConfig("encrypt.path");
			if (!StringUtils.isBlank(path)) {
				path = System.getProperty("user.dir") + File.separator;
			}
		} catch(MissingResourceException e) {
			System.err.println(Utils.getDateTimeMessage("Read property file error."));
			throw e;
		}

		return path + "USER-" + format.format(new Date());
	}

	private static String createUserFile(String userId, String password) throws Exception{

		Properties properties = new Properties();

		try {
			properties.put(Utils.KEY_USERID, Coder.encrypt(userId));
			properties.put(Utils.KEY_PASSWORD, Coder.encrypt(password));
		} catch (Exception e) {
			System.err.println(Utils.getDateTimeMessage("Encrypt Error."));
			throw e;
		}

		String fileName = createFileName();

		try {
			OutputStream stream = new FileOutputStream(fileName);
			properties.store(stream, "");
			stream.close();
		} catch (FileNotFoundException e) {
			System.err.println(Utils.getDateTimeMessage("Can not create file:" + fileName));
			throw e;
		} catch (IOException e) {
			System.err.println(Utils.getDateTimeMessage("Create file Error."));
			throw e;
		}

		return fileName;
	}
	

	public static String encrypt(String text){
		try {
			return Coder.encrypt(text);
		} catch (Exception e) {
			logger.fatal("Failed to encrypt the text.",e);
			return null;
		}
	}

	/**
	 */
	public static void main(String[] args) {

		if (args.length != 2) {
			System.err.println(Utils.getDateTimeMessage("Parameter error. Usage: EncryptManager <userId> <password>"));
			System.exit(-1);
		}

		try {
			String fileName = createUserFile(args[0], args[1]);
			System.out.println(Utils.getDateTimeMessage("File was created successfully: " + fileName));
			System.exit(0);

		} catch (Exception e) {
			System.err.println(Utils.getDateTimeMessage(e + ""));
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.out.println(e.getStackTrace());
			System.exit(-2);
		}
	}

}
