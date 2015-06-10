package com.ibm.dsw.quote.base.xstream;

 
import com.thoughtworks.xstream.XStream;  
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;  
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;  
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;  
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;  
import com.thoughtworks.xstream.io.xml.DomDriver;  
import com.thoughtworks.xstream.io.xml.XppDriver;  

public class XStreamFacade {
	 public static final String JAXP_DOM_XML = "JAXP DOM";  
	  
    public static final String XPP3_XML_PARSER = "XPP3";  
  
    public static final String STAX_JSON_PARSER = "Jettison StAX";  
  
    public static final String WRITER_JSON_PARSER = "Only Writer JSON";
    
    /** 
     * get XStream object. 
     *  
     * <pre> 
     * get XStream obj based on driver type, if do not provide driver, use default driver JAXP-DOM
     * </pre> 
     * @param driver driverName 
     * @param isStaticSupported 
     * @return XStream 
     */  
    public synchronized static XStream getXStream(String driver, boolean isStaticSupported)  {  
        ReflectionProvider reflectProvider = null; 
        XStream xstream = null;
  
        if (isStaticSupported)  
        {  
            reflectProvider = new EnhancedModeReflectProvider();  
        }  
        else  
        {  
            reflectProvider = new Sun14ReflectionProvider();  
        }  
  
        if (JAXP_DOM_XML.equals(driver))  
        {  
  
            xstream = new XStream(reflectProvider, new DomDriver());  
            xstream.autodetectAnnotations(true);  
  
            System.err.println(xstream.getReflectionProvider());  
        }  
        else if (XPP3_XML_PARSER.equals(driver))  
        {  
            xstream = new XStream(reflectProvider, new XppDriver());  
            xstream.autodetectAnnotations(true);  
        }  
        else if (STAX_JSON_PARSER.equals(driver))  
        {  
            xstream = new XStream(reflectProvider, new JettisonMappedXmlDriver());  
            xstream.setMode(XStream.NO_REFERENCES);  
        }  
        else if (WRITER_JSON_PARSER.equals(driver))  
        {  
            xstream = new XStream(reflectProvider, new JsonHierarchicalStreamDriver());  
            xstream.setMode(XStream.NO_REFERENCES);  
        }  
        else  
        {  
        	xstream = new XStream(reflectProvider, new XppDriver());  
            xstream.autodetectAnnotations(true);   
        }  
  
        return xstream;
    }
}
