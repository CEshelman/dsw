package com.ibm.dsw.quote.common.xml;

import java.io.StringReader;
import java.io.StringWriter;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;



public final class XMLUtil {
	
	private static final LogContext logger = LogContextFactory.singleton().getLogContext();
	    
	/**
	 * Unmarshal input xml string to an object.
	 * @param xml xml string
	 * @param valueType the class type
	 * @return Object 
	 * @throws JAXBException JAXBException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T xml2Object(String xml, Class<T> valueType) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(valueType);  
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
		T t =  (T) jaxbUnmarshaller.unmarshal(new StringReader(xml));  
		return t;  		
	}  

	/**
	 * marshal the input object to xml string
	 * @param obj Object
	 * @return String
	 * @throws JAXBException JAXBException
	 */
	public static String object2XML(Object obj) throws JAXBException {
		StringWriter sw = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(obj.getClass());
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(obj, sw);
		return sw.toString();
	}

}
