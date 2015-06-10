package com.ibm.dsw.quote.common.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.CognosPart;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class CognosRetrieveServiceHelper {

	private static LogContext logger = LogContextFactory.singleton().getLogContext();
	
	private static CognosRetrieveServiceHelper  service = new CognosRetrieveServiceHelper();
	
	private List<CognosPart> cognosRetrievePartList = new ArrayList<CognosPart>();
	
	private final int cognosWebserviceMaxExceptionCount = HtmlUtil.getCognosWebserviceMaxExceptionCount();
	
	private CognosRetrieveServiceHelper(){
	}

	public static CognosRetrieveServiceHelper getInstance(){
		return service;
	}
	
	public boolean callWebService(String cognosRequest, String cognosHost, int cognosPort, String cognosProtocol,
			String authentication, String logon, String logoff, String webQuoteNum) throws Exception{
		if(StringUtils.isBlank(cognosRequest)){
			logger.info(this, "Blank Request For Ritrieve Part From Cognos For Quote " + webQuoteNum + ": " + cognosRequest);
		}else{
			for (int i = 0; i < cognosWebserviceMaxExceptionCount; i++) {
				
				String cognosResponse = getCognosResponse(cognosRequest, cognosHost, cognosPort, cognosProtocol, authentication, logon, logoff);
				if(isWebserviceError(cognosResponse)){
					logger.info(this, "Request For Ritrieve Part From Cognos For Quote " + webQuoteNum + ": " + cognosRequest);
					logger.info(this, "Response For Ritrieve Part From Cognos For Quote " + webQuoteNum + ": " + cognosResponse);
					//if the last time calling is still failed, means web service call is failed, otherwise call it again
					logger.debug(this, "Error call Cognos web service for " + (i + 1) + " time");
					if(i == cognosWebserviceMaxExceptionCount - 1){
						return false;
					}else{
						continue;
					}
				}else{
					cognosResponse = cognosResponse.replaceAll("xmlns=\"(.*)\">", ">");
					logger.info(this, "Request For Ritrieve Part From Cognos For Quote " + webQuoteNum + ": " + cognosRequest);
					logger.info(this, "Response For Ritrieve Part From Cognos For Quote " + webQuoteNum + ": " + cognosResponse);
					Element root = getDocumentFromString(cognosResponse).getRootElement();
					cognosRetrievePartList = parseResponse(root);
				}
			}
		}
		return true;
	}
	
    private String getCognosResponse(String cognosRequest, String cognosHost, int cognosPort, String cognosProtocol,
    		String authentication, String logon, String logoff) throws HttpException, IOException{
    	String response = "";
    	BufferedReader br = null;
	    try{
	        HttpClient client = new HttpClient();
	        client.getHostConfiguration().setHost(cognosHost, cognosPort, cognosProtocol);
	        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
	        client.getParams().setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);
	
	        PostMethod authpostLogon = new PostMethod(logon);
	
	        authpostLogon.setRequestBody(authentication);
	        client.executeMethod(authpostLogon);
	        authpostLogon.releaseConnection();
	        GetMethod redirect = new GetMethod(cognosRequest);
	        client.executeMethod(redirect);
	        
	        InputStream resStream = redirect.getResponseBodyAsStream();
	        br = new BufferedReader(new InputStreamReader(resStream));
	        StringBuffer resBuffer = new StringBuffer();
	        String resTemp = "";
	        while((resTemp = br.readLine()) != null){
	        	resBuffer.append(resTemp);
	        }
	        response = resBuffer.toString();
	        redirect.releaseConnection();
	        PostMethod authpostLogoff = new PostMethod(logoff);
	        client.executeMethod(authpostLogoff);
	    }catch(HttpException he){
    		logger.error(this, "Call Cognos Webservice got error: " + he.getMessage());
    		throw he;
    	}catch(IOException ie){
    		logger.error(this, "Call Cognos Webservice got error: " + ie.getMessage());
    		throw ie;
    	}finally{
    		if (null != br)
    		{
    			br.close();
    		}
    	}
        return response;

    }
    
	private List<CognosPart> parseResponse(Element root) throws Exception{
		List<CognosPart> cognosPartList = new ArrayList<CognosPart>();
		if(root != null){
			List<Element> itemElementList = root.getChildren("dataTable");
			for(Iterator<Element> it = itemElementList.iterator();it.hasNext();){
				Element itemElement = it.next();
				List<CognosPart> subCognosPartList = populateConfigPart(itemElement);
				if(subCognosPartList != null){
					cognosPartList.addAll(subCognosPartList);
				}
			}
		}
		
    	if(logger instanceof QuoteLogContextLog4JImpl){
			if(((QuoteLogContextLog4JImpl)logger).isDebug(this)){		
				logger.debug(this, "Ritrieve Part From Cognos Parts Info : \n");
				
				StringBuffer sb = new StringBuffer();
				sb.append("{");
				for(CognosPart part : cognosPartList){
					sb.append("(").append(part.toString()).append("\n)");
				}
				sb.append("}");
				
				logger.debug(this, sb.toString());
			}
		}
		return cognosPartList;
	}
	
	private List<CognosPart> populateConfigPart(Element itemElement) throws Exception{
		List<CognosPart> cognosPartList = new ArrayList<CognosPart>();
		List<Element> itemElementList = itemElement.getChildren("row");
		for(Iterator<Element> it = itemElementList.iterator();it.hasNext();){
			CognosPart cognosPart = new CognosPart();
			Element subElement = it.next();
			Element attr = subElement.getChild("Reinstatement__Part__Number");
			if(attr != null){
				cognosPart.setPartNum(StringUtils.trim(attr.getValue()));
			}
			attr = subElement.getChild("Reinstatement__Quantity");
			if(attr != null){
				Integer partQty = new Integer(1);
				try{
					String str = StringUtils.trim(attr.getValue());
					partQty = Integer.valueOf(Double.valueOf(str).intValue());
				}catch (Exception e) {
					logger.debug(this, e.getMessage());
				}
				cognosPart.setPartQty(partQty);
			}
			if(cognosPart.isValid()){
				cognosPartList.add(cognosPart);
			}
		}
		return cognosPartList;
	}
	
	
	private Document getDocumentFromString(String inputSource){
		try {
			// construct doc from string
			SAXBuilder builder = new SAXBuilder(false);
			return builder.build(new InputSource(new StringReader(inputSource)));   
		} catch (Exception e) {
			logger.error(this, "An unexpected exception occurred while parsing string : " + inputSource);
			logger.error(this, e);
		}

		return null;
	}
	
	/**
	 * @param cognosResponse
	 * @return
	 * judge if getting error when calling webserivce
	 */
	private boolean isWebserviceError(String cognosResponse){
		if(StringUtils.isNotBlank(cognosResponse)){
			if(cognosResponse.contains("ERROR")){
				return true;
			}
		}
		return false;
	}

	public List<CognosPart> getCognosRetrievePartList() {
		return cognosRetrievePartList;
	}


}
