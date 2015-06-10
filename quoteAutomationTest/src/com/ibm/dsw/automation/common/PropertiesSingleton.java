package com.ibm.dsw.automation.common;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;


public class PropertiesSingleton {
	private Properties envProp = null;
	private Properties settingsProp = null;
	private Properties validateProp = null;

	private Properties newCustInf = null;
	
	private Properties mailConfigProp = null;
	
	private Logger logger = Logger.getLogger(getClass());

	private static volatile PropertiesSingleton instance = null;

	private PropertiesSingleton() {
		if (null == envProp) {
			envProp = TestUtil
					.getTestDataProp("/com/ibm/dsw/automation/common/ENV.properties");
		}

		if (null == settingsProp) {
			settingsProp = TestUtil
					.getTestDataProp("/com/ibm/dsw/automation/common/Settings.properties");
		}

		if (null == validateProp) {
			validateProp = TestUtil
					.getTestDataProp("/com/ibm/dsw/automation/pageobject/validationCont.properties");
		}
		
		if (null == newCustInf) {
			newCustInf = TestUtil
			.getTestDataProp("/com/ibm/dsw/automation/testcase/mtp/NewCustomerInf.properties");
		}
		
		if (null == mailConfigProp) {
			mailConfigProp = TestUtil
			.getTestDataProp("/com/ibm/dsw/automation/common/deployAndMail.properties");
		}
	}

	public PropertiesSingleton(String filePath) {
		try {
		if (null == envProp) {
				envProp = TestUtil
						.getTestDataProp(new File(filePath + File.separator + "ENV.properties"));
		}

		if (null == settingsProp) {
			settingsProp = TestUtil
					.getTestDataProp(new File(filePath + File.separator + "Settings.properties"));
		}

		if (null == validateProp) {
			validateProp = TestUtil
					.getTestDataProp(new File(filePath + File.separator + "validationCont.properties"));
		}
		
		if (null == newCustInf) {
			newCustInf = TestUtil
			.getTestDataProp(new File(filePath + File.separator + "NewCustomerInf.properties"));
		}
		
		if (null == mailConfigProp) {
			mailConfigProp = TestUtil
			.getTestDataProp(new File(filePath + File.separator + "deployAndMail.properties"));
		}
		} catch (IOException e) {
			logger.fatal("Failed to load initial properties files.",e);
			e.printStackTrace();
		}
		
		if (null == mailConfigProp) {
			mailConfigProp = TestUtil
			.getTestDataProp("/com/ibm/dsw/automation/common/deployAndMail.properties");
		}
	}

	public Properties getEnvProperties() {
		return envProp;
	}

	public Properties getSettingProperties() {
		return settingsProp;
	}

	public Properties getValidateProperties() {
		return validateProp;
	}
	
	public Properties getNewCustInfoProperties() {
		return newCustInf;
	}
	
	public Properties getMailConfigPropperties() {
		return mailConfigProp;
	}

	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * 
	 * @param args To compatible with legacy code, 
	 * 			   the first argument can be a specified directory which store the configure files.
	 * 			   The other arguments are for future use.
	 * @return
	 */
	public static PropertiesSingleton getInstance(String...args) {

		if (instance == null){
			synchronized(PropertiesSingleton.class){
				if (null == instance) {
					if (args==null || args.length==0) {
						instance = new PropertiesSingleton();
					}else{
						instance = new PropertiesSingleton(args[0]);
					}
				}
			}
		}
		return instance;

	}

}
