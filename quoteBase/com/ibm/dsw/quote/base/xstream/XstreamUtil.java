package com.ibm.dsw.quote.base.xstream;

import java.io.*;


import com.ibm.dsw.quote.appcache.domain.jdbc.CodeDescObj_jdbc;
import com.thoughtworks.xstream.XStream;


public class XstreamUtil {
	 
	 public static void writeBean2XML(Object obj) {
		XStream xstream = XStreamFacade.getXStream(null, true);
	    try {
	    	String className = obj.getClass().getSimpleName();
	    	String path = "c:/JunitTestDataForSQO/" + className;
            //System.out.println(path);
            File file = new File(path);
            if(!file.exists()){
            	file.mkdirs();
            	file = new File(path + "/" +className + ".xml");
            }else{
            	file = new File(path + "/" +className + ".xml");
            	if(file.exists()){
            		file = new File(path);
            		File[] dirFile = file.listFiles();
               	 	int maxSuffix = 0;
                    for (File f : dirFile) {
                   	 String name = f.getName();
                       if (name.endsWith(".xml")){ //
                       	int suffix = 0;
                       	try{
                       		suffix = Integer.parseInt(name.substring(name.lastIndexOf("_")+1, name.indexOf(".xml")));
                       	}catch(Exception e){
                       		continue;
                       	}
                       	if(suffix > maxSuffix)
                       		maxSuffix = suffix;
                       }  
                    }
                    maxSuffix ++;
                    file = new File(path + "/" + className + "_" + maxSuffix + ".xml");
            	}
            }
            FileWriter fileW = new FileWriter(file);
            
	    	//xstream.aliasPackage("test", "test name");
	    	xstream.toXML(obj,fileW);
	    	fileW.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	 
	@SuppressWarnings("unchecked")  
    public static <T> T readXml2Object(Class<T> beanClass, String xmlFile) {  
        if (beanClass == null || xmlFile == null || xmlFile.isEmpty())  
            throw new IllegalArgumentException("paremeter can not be null.");
        XStream xstream = XStreamFacade.getXStream(null, true);
        xstream.registerConverter(new EnhancedReflectionConverter(xstream.getMapper(),xstream.getReflectionProvider()), xstream.PRIORITY_VERY_LOW);
        T obj = null;
        
        try{
        	File file = new File(xmlFile);
            FileReader xmlReader = new FileReader(file);
            
            obj = (T)xstream.fromXML(xmlReader);
            
            xmlReader.close();
        }catch (Exception e) {  
            e.printStackTrace();  
        }  

        return obj;  
    }
	 
    public static void main(String[] args) {
    	CodeDescObj_jdbc obj = new CodeDescObj_jdbc("code","desc");
    	//writeBean2XML(obj);
    	//xstream.readXml2Object(CodeDescObj_jdbc.class,"c:\\CodeDescObj_jdbc.xml");;
    	String name = "quo_te_11.xml";
    	System.out.println(name.substring(name.lastIndexOf("_")+1, name.indexOf(".xml")));
    }
    
}
