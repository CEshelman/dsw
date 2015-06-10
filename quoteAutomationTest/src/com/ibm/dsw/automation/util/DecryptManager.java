package com.ibm.dsw.automation.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.testng.log4testng.Logger;


public class DecryptManager {

	private static Map<String, String> map = new Hashtable<String, String>();
	
	private static Logger logger = Logger.getLogger(DecryptManager.class);

	private static String getDecrypt(String key, String type) throws Exception {

		String mapKey = key + "." + type;

		String str = map.get(mapKey);
		if (str != null) {

			return str;
		}

		String fileName = "";
		try {
			fileName = Utils.getConfig("decrypt.path");
			if (StringUtils.isBlank(fileName)) {
				fileName = System.getProperty("user.dir");
				/*if(fileName.contains("build")){
					fileName=userDir+File.separator;
				}else{
					fileName=userDir+File.separator+"target"+File.separator+"classes"+File.separator;
				}*/
			}
			
		} catch(MissingResourceException e) {
			System.err.println(Utils.getDateTimeMessage("Read property file error."));
			throw e;
		}

		String result = "";
		try {
			fileName = fileName + File.separator + key;
			InputStream stream = new FileInputStream(fileName);

			Properties properties = new Properties();
			properties.load(stream);

			stream.close();

			result = properties.getProperty(type);

		} catch (FileNotFoundException e) {
			System.err.println(Utils.getDateTimeMessage("File " + fileName + " is not found."));
			throw e;
		} catch (IOException e) {
			System.err.println(Utils.getDateTimeMessage("Read file error."));
			throw e;
		}

		try {
			result = Coder.decrypt(result);
		} catch (Exception e) {
			System.err.println(Utils.getDateTimeMessage("Decrypt error."));
			throw e;
		}

		map.put(mapKey, result);
		return result;
	}

	public static String getUserId(String key) throws Exception{
		return getDecrypt(key, Utils.KEY_USERID);
	}

	public static String getPassword(String key) throws Exception{
		return getDecrypt(key, Utils.KEY_PASSWORD);
	}
	
	public static String decrypt(String text){
		try {
			return Coder.decrypt(text);
		} catch (Exception e) {
			logger.fatal("Failed to decrypt the text.",e);
			return null;
		}
	}
	
	public static void main(String[] args) {

		if (args.length != 2) {
			System.err.println(Utils.getDateTimeMessage("Parameters error. Usage: EncryptManager <key> <0-userId / 1-password>"));
			System.exit(-1);
		}

		try {
			String type = "";
			if ("0".equals(args[1])) {
				type = Utils.KEY_USERID;
			} else if ("1".equals(args[1])) {
				type = Utils.KEY_PASSWORD;
			} else {
				System.err.println(Utils.getDateTimeMessage("Parameters error. Usage: EncryptManager <key> <0-userId / 1-password>"));
				System.exit(-1);
			}

			String value = getDecrypt(args[0], type);
			System.out.println(value);
			System.exit(0);

		} catch (Exception e) {
			System.err.println(Utils.getDateTimeMessage(e + ""));
			System.exit(-2);
		}
	}

}
