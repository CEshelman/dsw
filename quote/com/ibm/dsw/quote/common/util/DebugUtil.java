package com.ibm.dsw.quote.common.util;

import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class DebugUtil {
	private static final LogContext logger = LogContextFactory.singleton().getLogContext();
	private static DebugUtil singleton = null;
	
	private DebugUtil(){
		
	}
	
	public static DebugUtil singleton() {
		if (DebugUtil.singleton == null) {
			singleton = new DebugUtil();
		}
		return singleton;
	}

//	public static DebugUtil singleton() {
//		LogContext logCtx = LogContextFactory.singleton().getLogContext();
//
//		if (DebugUtil.singleton == null) {
//			String factoryClassName = null;
//			try {
//				factoryClassName = DebugUtil.class.getName();
//				Class factoryClass = Class.forName(factoryClassName);
//				DebugUtil.singleton = (DebugUtil) factoryClass.newInstance();
//			} catch (IllegalAccessException iae) {
//				logCtx.error(DebugUtil.class, iae, iae.getMessage());
//			} catch (ClassNotFoundException cnfe) {
//				logCtx.error(DebugUtil.class, cnfe, cnfe.getMessage());
//			} catch (InstantiationException ie) {
//				logCtx.error(DebugUtil.class, ie, ie.getMessage());
//			}
//		}
//		return singleton;
//	}

	public static void  showStringArray(String[] strs, String name) {
		if (isDebug()) {
			logger.debug(singleton(), "String array para name:" + name);
			if (strs == null) {
				logger.debug(singleton(), "String array is null");
				return;
			}
			logger.debug(singleton(), "String array size :" + strs.length);
			for (int i = 0; i < strs.length; i++) {
				logger.debug(singleton(), name + "[" + i + "] : " + strs[i]);
			}
		}
	}

	public static void showString(String str, String name) {
		if (isDebug()) {
			logger.debug(singleton(), "String para name:" + name);
			if (str == null) {
				logger.debug(singleton(), "String is null");
				return;
			}
			logger.debug(singleton(), "String size :" + str.length());
			logger.debug(singleton(), name + ":" + str);
		}
	}

	public static void showCheckNull(Object obj, String name) {
		if (isDebug()) {
			logger.debug(singleton(), "String para name:" + name);
			if (obj == null) {
				logger.debug(singleton(), "Object is null");
				return;
			}
			logger.debug(singleton(), "Object is not null");
		}
	}

	public static void showList(List lst, String name) {
		if (isDebug()) {
			logger.debug(singleton(), "List name:" + name);
			if (lst == null) {
				logger.debug(singleton(), "List is null");
				return;
			}
			logger.debug(singleton(), "List size :" + lst.size());
			if (lst.size() > 0) {
				Object obj = lst.get(0);
				logger.debug(singleton(), "Class name in list is :"
						+ obj.getClass().getName());
			}
		}
	}

	public static void showStep(int i, String name) {
		if (isDebug()) {
			logger.debug(singleton(), name + ": test step----" + i);
		}
	}

	public static void showMap(Map map, String name) {
		if (isDebug()) {
			logger.debug(singleton(), "Map name:" + name);
			if (map == null) {
				logger.debug(singleton(), "Map is null");
				return;
			}
			logger.debug(singleton(), "Map size :" + map.values().size());
			if (map.values().size() > 0) {
				Object[] objs = map.values().toArray();
				logger.debug(singleton(), "Class name in map is :"
						+ objs[0].getClass().getName());
			}
		}
	}
	
	 private static boolean isDebug(){
		boolean isDebug = false;
    	if(logger instanceof QuoteLogContextLog4JImpl){
			if(((QuoteLogContextLog4JImpl)logger).isDebug(singleton())){		
				isDebug = true;
			}
		}
    	return isDebug;
	}
}
