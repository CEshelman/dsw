package com.ibm.dsw.automation.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class TestUtil {
	
	public static Logger loggerContxt = Logger.getLogger(TestUtil.class);

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args)  {
		
		/*
		int db2Name;
	
		String fname = "C:/dss/quote/WebQuote_head_Stream_webdriver/quoteAutomationTest/src/com/ibm/dsw/automation/common/PGSCreateQuotePage_1361769650125.html";
		
  
		
		String htmlSourceFolder = SeleniumBase.getHtmlSourceFolder();
		String fileDir ="";

		 fileDir = htmlSourceFolder
				+ File.separator
				+ "html"
				+ File.separator
				+ TestUtil.currentDateStr("yyyyMMdd",
						"Asia/Shanghai");
			 fileDir ="C:/dss/quote/WebQuote_head_Stream_webdriver/quoteAutomationTest/src/com/ibm/dsw/automation/common/20130226";

		TestUtil.findFiles(fileDir);
		
		creatFile("mailNeed.dat", "Y");
		StringBuffer sbBuffer = readFile("mailNeed.dat");
		System.out.println(sbBuffer.toString());
		
		
		List list=WebdriverLogger.getInfList();
		for (int i = 0; i < list.size(); i++) {
			System.out.println("111"+"...."+list.get(i).toString()); 
		}
		WebdriverLogger.RemoveInfList();
		System.out.println("remove");
		loggerContxt.info("p.fireBuildStarted()");
		loggerContxt.info("p.fireBuildStarted()");
		
		list=WebdriverLogger.getInfList();
		for (int i = 0; i < list.size(); i++) {
			System.out.println("sss"+"...."+list.get(i).toString()); 
		}*/

	}

	public static boolean isSQOOrPGS(String testNm) {
		testNm = testNm.toLowerCase();
		return testNm.contains("sqo");

	}

	public static void callSendMail() {
		String antFilePath = "build/SendMail.xml";
		File buildFile = new File(antFilePath);
		Project p = new Project();

		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);
		try {
			loggerContxt.info("p.fireBuildStarted()");
			p.fireBuildStarted();
			loggerContxt.info("	p.init()()");
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			loggerContxt.info("	helper.parse(p, buildFile)");
			helper.parse(p, buildFile);
			p.executeTarget("mail");
			loggerContxt.info("	p.executeTarget(p.getDefaultTarget())");
			p.fireBuildFinished(null);
			loggerContxt.info("	p.fireBuildFinished(null);");
		} catch (BuildException e) {
			loggerContxt.info("ex" + e.getMessage());
			p.fireBuildFinished(e);
		}
	}

	public static Properties getTestDataProp(String path) {

		loggerContxt.info(String.format("The needed load file %s", path));
		Properties prop = new Properties();
		InputStream in = Object.class.getResourceAsStream(path);
		try {
			prop.load(in);
			loggerContxt.info(String.format("The needed load file %s loaded completly.", path));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return prop;
	}
	
	public static Properties getTestDataProp(File file) throws IOException {
		
		loggerContxt.info(String.format("The needed load file %s", file.getAbsoluteFile()));
		Properties prop = null;
		try {
			InputStream in = new FileInputStream(file);
			prop = new Properties();
			prop.load(in);
			loggerContxt.info(String.format("The needed load file %s loaded completly.", file.getAbsoluteFile()));
			
		} catch (IOException e) {
			loggerContxt.fatal("Failed to load file:: " + file.getAbsoluteFile(),e);
			prop = null;
			throw e;
		}
		
		return prop;
	}

	public static String[] addTargetDayFromCurrentDaay(int targetAddDays) {
		Calendar cal = Calendar.getInstance();
		loggerContxt.info("date after add target Day:" + cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, targetAddDays);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String yyyyMMdd = sdf.format(cal.getTime());
		loggerContxt.info("date after add target Day:" + yyyyMMdd);
		String[] dateArr = yyyyMMdd.split("-");

		return dateArr;

	}

	public static String[] addTargetDayFromCurrentDaay(int targetAddDays, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		loggerContxt.info("date after add target Day:" + cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, targetAddDays);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String yyyyMMdd = sdf.format(cal.getTime());
		loggerContxt.info("date after add target Day:" + yyyyMMdd);
		String[] dateArr = yyyyMMdd.split("-");

		return dateArr;
	}
	
	/**
	 * 
	 * @param dateFormater
	 *  dd-MM-yyyy HHmmss
	 *  yyyyMMdd
	 * 
	 * */
	public static String currentDateStr(String dateFormater, String timeZone) {
		SimpleDateFormat formater = new SimpleDateFormat(dateFormater);
		formater.setTimeZone(TimeZone.getTimeZone(timeZone));
		String dateStr = formater.format(((GregorianCalendar) GregorianCalendar.getInstance()).getTime());
		return dateStr;
	}
	
	public static String currentDateStr(String dateFormater) {
		return currentDateStr( dateFormater,"Asia/Shanghai");
	}
	
	public static Date string2Date(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			loggerContxt.error(e.getMessage());
			e.printStackTrace();
		}
		
		return new Date();
	}

	public Object getStaticProperty(String className, String fieldName)
			throws Exception {
		Class<?> ownerClass = Class.forName(className);
		Field field = ownerClass.getField(fieldName);
		Object property = field.get(ownerClass);
		return property;
	}

	public Object invokeMethod(Object owner, String methodName, Object[] args)
			throws Exception {
		Class<?> ownerClass = owner.getClass();

		Class<?>[] argsClass = new Class[args.length];
		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}
		Method method = ownerClass.getMethod(methodName, argsClass);

		return method.invoke(owner, args);
	}

	public static Object invokeSetter(Object owner, Properties properties)
			throws Exception {

		Class<?> classType = owner.getClass();
		loggerContxt.debug(classType.getClass().getName() + " --> Class:"
				+ classType.getName());

		Field fields[] = classType.getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			String firstLetter = fieldName.substring(0, 1).toUpperCase();

			// get corresponding setXXX() method name
			String setMethodName = "set" + firstLetter + fieldName.substring(1);

			// get corresponding setXXX() method
			Method setMethod = classType.getMethod(setMethodName,
					new Class[] { field.getType() });

			String propertyVal = getProperty(properties, fieldName);

			loggerContxt.debug(classType.getClass().getName() + " --> "
					+ fieldName + ":" + propertyVal);
			// call setXXX() method
			setMethod.invoke(owner, new Object[] { propertyVal });
		}
		return owner;
	}

	public static String getProperty(Properties p, String key) {
		String rs = null;
		try {
			rs = p.getProperty("." + key);
			if (StringUtils.isBlank(rs)) {
				rs = p.getProperty(key);
			}
		} catch (Exception e) {
			rs = "";
		}

		return rs;
	}

	public static void creatFile(String fn, String fileCont) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			File f = new File(fn);
			if (!f.exists()) {

				f.createNewFile();
			}
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			// bw.newLine();
			bw.write(fileCont);
			bw.flush();
			bw.close();

		} catch (IOException ex) {
			loggerContxt.error(ex.getMessage());
		} finally {
			try {
				bw.close();
				fw.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static StringBuffer readFile(String fn) {
		BufferedReader buff = null;
		StringBuffer sb = new StringBuffer();
		File f = new File(fn);
		if (!f.exists()) {
			return sb;
		}
		try {
			FileReader file = new FileReader(fn);
			buff = new BufferedReader(file);
			boolean eof = false;
			while (!eof) {
				String line = buff.readLine();
				if (line == null) {
					eof = true;
				} else {
					sb.append(line);
					loggerContxt.error(line);
				}
			}

		} catch (IOException ex) {
			loggerContxt.error(ex.getMessage());
		} finally {
			try {
				buff.close();
				f = new File(fn);
				if (f.exists()) {
					loggerContxt.error("delete flag file...");
					f.delete();
				}
			} catch (IOException ex) {

			}

		}
		return sb;

	}
	
	public static void findFiles(String baseDirName) {

		File baseDir = new File(baseDirName);
		if (!baseDir.exists() || !baseDir.isDirectory()) {
			System.out.println("search failure" + baseDirName
					+ "not a directory");
		} else {
			String[] filelist = baseDir.list();
			for (String element : filelist) {
				if (element.contains(".htm")) {
					String targetFileNm = getJspNmFromHtml(
							baseDirName + File.separator, element).toString();
					File orginalFile = new File(baseDirName + File.separator
							+ element);

					loggerContxt.info("orginalFile name..." + baseDirName
							+ File.separator + element);
					targetFileNm = baseDirName + "rpt_use"+ File.separator+targetFileNm;
					File targetFile = new File(targetFileNm);
					File targetFileDir = new File(targetFile.getParent());
					if (!targetFileDir.exists()) {
						targetFileDir.mkdirs();
					}

					loggerContxt.info("targetFile name..." + targetFileNm);
					orginalFile.renameTo(targetFile);
				}
			
			 }
		 }
	 }
	 
	public static StringBuffer getJspNmFromHtml(String dir, String fname) {
		File f = new File(dir + File.separator + fname);
		FileReader fr;
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			fr = new FileReader(f);

			br = new BufferedReader(fr);
			String strLine;

			strLine = br.readLine();
			while (strLine != null) {
				int num = strLine.indexOf("<!--  JSP file");
				if (num != -1) {
					strLine = strLine.replaceAll("-", "");
					strLine = strLine.replaceAll("!", "");
					strLine = strLine.replaceAll("<", "");
					strLine = strLine.replaceAll(">", "");
					strLine = strLine.replaceAll(" ", "");

					String[] arryNm = strLine.split("/");
					for (String element : arryNm) {
						String jspNm = element;
						System.out.println("arryNm[i]" + element);
						if (jspNm.contains(".jsp")) {
							sb.append(jspNm);
							jspNm = jspNm.replaceAll(".jsp", "");
							sb.append("_");
						}
					}
				//	sb.append(fname); // to avoid get the same name, append the original file name to the new file name.
				}
				strLine = br.readLine();
			}
			sb.append(fname); // to avoid get the same name, append the original file name to the new file name.
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb;

	}
	
	public int string2Int(String str){
		int rtn = 0;
	    if(StringUtils.isBlank(str)){
	    	rtn= 0;
	    }else{
	     try{
	    	rtn=Integer.parseInt(str);
	    } catch (Exception ex) {
	    	loggerContxt.info("err happened during parsing..."+ex.getMessage());
		}
	    }
	    
	    
		return rtn;
	}
	
	public static String[] setTheLastDateCurrMonth() {
		Calendar cal = Calendar.getInstance();
		loggerContxt.info("date after add target Day:" + cal.getTime());
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));   
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)); 
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String yyyyMMdd = sdf.format(cal.getTime());
		loggerContxt.info("date after add target Day:" + yyyyMMdd);
		String[] dateArr = yyyyMMdd.split("-");
		return dateArr;
	}
}
