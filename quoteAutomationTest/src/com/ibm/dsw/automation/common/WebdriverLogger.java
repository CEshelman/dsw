package com.ibm.dsw.automation.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class WebdriverLogger {
	Logger loggerContxt;
	private static List infList = null;

	private WebdriverLogger(String name) {
		loggerContxt = Logger.getLogger(name);
		infList = new ArrayList();
	}
	
	private WebdriverLogger(Class clazz) {
		loggerContxt = Logger.getLogger(clazz);
		infList = new ArrayList();
	}

	public static WebdriverLogger getLogger( String  name) {
		return new WebdriverLogger(name);
	}
	
	public static WebdriverLogger getLogger( Class  clazz) {
		return new WebdriverLogger(clazz);
	}

	public void info(Object message) {
		infList.add(TestUtil.currentDateStr("dd-MM-yyyy hh:mm:ss.SSS")+"..."+message);
		loggerContxt.info(message);

	}

	public static List getInfList() {
		return infList;
	}
	
	public static void RemoveInfList() {
		infList.removeAll(infList);
	}

	public void setInfList(List infList) {
		this.infList = infList;
	}

	public void error(Object message) {
		infList.add(TestUtil.currentDateStr("dd-MM-yyyy hh:mm:ss.SSS")+"..."+message);
		loggerContxt.error(message);
	}

	public void warn(Object message) {
		infList.add(TestUtil.currentDateStr("dd-MM-yyyy hh:mm:ss.SSS")+"..."+message);
		loggerContxt.warn(message);
	}

	public void debug(Object message) {
		//infList.add(message);
		loggerContxt.debug(message);
	}

}
