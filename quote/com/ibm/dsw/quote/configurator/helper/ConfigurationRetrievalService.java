package com.ibm.dsw.quote.configurator.helper;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.ibm.dsw.quote.base.config.ApplicationProperties;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.CommonServiceConstants;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.dsw.wpi.java.util.ServiceLocator;
import com.ibm.dsw.wpi.java.util.ServiceLocatorException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.misc.BASE64Decoder;
import com.yantra.interop.services.webservices.rpc.ejb.YIFWebService;

public class ConfigurationRetrievalService {
	private static LogContext logger = LogContextFactory.singleton().getLogContext();
	private static final String USER_NOT_AUTHENTICATED = "Cannot invoke API because user is not authenticated.";
	private static final int MAX_EXCEPTION_COUNT = 2;
	
	private static int counter = 0;
	private static Object lock = new Object();
	private static String userToken = null;
	private static ConfigurationRetrievalService  service = new ConfigurationRetrievalService();
	
	private ConfigurationRetrievalService(){
	}

	public static ConfigurationRetrievalService getInstance(){
		return service;
	}
	
	public List<ConfiguratorPart> callWebService(String configId, String webQuoteNum, StringBuffer errorCode, boolean useTokenCache, List<String> missingPartLst) throws Exception {//refer to rtc#215704
		
		List<ConfiguratorPart> parts = new ArrayList<ConfiguratorPart>();
		try {
			logger.info(this, "Retrieve configuration for: " + webQuoteNum + "-" + configId);
			YIFWebService service = getService();
			
			String userId = ApplicationProperties.getInstance().getWebserviceCPQUserId();
			
			String response = "";
			if(useTokenCache){
				//This is the first time to call service, initialize user token
				if((userToken == null) || (counter == MAX_EXCEPTION_COUNT)){
					updateUserToken(userToken);
				}
				
				try{
					response = service.dswService(genEnvStrForDswService(userToken), genXmlStrForDswService(configId, userId));
				
				} catch(Exception fault){
					logger.error(this, "Web service fault for configuration retrieval service: " + configId);
					logger.error(this, fault);
					
					if(fault.toString().indexOf(USER_NOT_AUTHENTICATED) != -1){
						updateUserToken(userToken);
						response = service.dswService(genEnvStrForDswService(userToken), genXmlStrForDswService(configId, userId));

					} else {
						counter++;
						throw new QuoteException(fault);
					}
				}
			} else {
				String token = getLoginUserToken();
				response = service.dswService(genEnvStrForDswService(token), genXmlStrForDswService(configId, userId));
			}
			
			Element root = getDocumentFromString(response).getRootElement();
			parts = parseResponse(root, errorCode, missingPartLst);//refer to rtc#215704
			
			return parts;
			
		} catch (Exception e) {
			
			counter++;
			logger.error(this, "An unexpected exception occurred while retrieving configuration. Cause:" + e);
			throw e;
		}
	}
	
	private YIFWebService getService() throws ServiceLocatorException, ServiceException{
		ServiceLocator serviceLocator = new ServiceLocator();
		YIFWebService service = (YIFWebService) serviceLocator.getServicePort(
                CommonServiceConstants.YIF_WEB_SERVICE_BINDING, YIFWebService.class);
		
		return service;
	}
	
	private void updateUserToken(String curUserToken) throws Exception{
		logger.debug(this, "prepare to update current user token: " + curUserToken);
		synchronized(lock){			
			if(userToken !=null && !userToken.equals(curUserToken)){
				logger.debug(this, "Usertoken already updated. Will retry to call webservice.");
				return;
			}
			
			userToken =  getLoginUserToken();
			counter = 0;
			logger.debug(this, "Usertoken was updated, new value is : " + userToken);
		}
	}
	
	private String getLoginUserToken() throws Exception{
		YIFWebService service = getService();
		//Call login operation to get user token
		String loginOutput = service.login(genEnvStrForLogin(), genXmlStrForLogin());
		
		Document doc = getDocumentFromString(loginOutput);
		Element root = doc.getRootElement();
		
		return  root.getAttributeValue("UserToken");
	}
	
	private List<ConfiguratorPart> parseResponse(Element root, StringBuffer errorCode, List<String> missingPartLst) throws Exception{
		
		Element dswconfig =  root.getChild("getDSWConfiguration");
		handleCallError(dswconfig, errorCode);
		
		Element configEle = dswconfig.getChild("configuration");
		List<ConfiguratorPart> configPartList = new ArrayList<ConfiguratorPart>();

		if(configEle != null){
			List<Element> itemElementList = configEle.getChildren("lineItem");
			for(Iterator<Element> it = itemElementList.iterator();it.hasNext();){
				Element itemElement = it.next();
				ConfiguratorPart part = populateConfigPart(itemElement, missingPartLst);//refer to rtc#215704
				if(part != null)
					configPartList.add(part);
			}
		}
		

    	if(logger instanceof QuoteLogContextLog4JImpl){
			if(((QuoteLogContextLog4JImpl)logger).isDebug(this)){		
				logger.debug(this, "part info returned from CPQ is: \n");
				
				StringBuffer sb = new StringBuffer();
				sb.append("{");
				for(ConfiguratorPart part : configPartList){
					sb.append("(").append(part.toString()).append("\n)");
				}
				sb.append("}");
				
				logger.debug(this, sb.toString());
			}
		}
		return processForRampUps(configPartList);
	}
	
	private List<ConfiguratorPart> processForRampUps(List<ConfiguratorPart> list){
		List<ConfiguratorPart> result = new ArrayList<ConfiguratorPart>();
		
		RampUpPartGroup grp = new RampUpPartGroup();
		for(ConfiguratorPart part : list){
			//This is a ramp up part
			if(ConfiguratorConstants.RAMP_FLAG_YES.equals(part.getRampUpFlag())){
				grp.add(part);
			} else {
				result.add(part);
			}
		}
		result.addAll(grp.getPartWithRampUps(result));
		
		if(logger instanceof QuoteLogContextLog4JImpl){
			if(((QuoteLogContextLog4JImpl)logger).isDebug(this)){		
				logger.debug(this, "part info returned from CPQ(after ramp up processing) is: \n");
				
				StringBuffer sb = new StringBuffer();
				sb.append("{");
				for(ConfiguratorPart part : result){
					sb.append("(").append(part.toString()).append("\n)");
				}
				sb.append("}");
				
				logger.debug(this, sb.toString());
			}
		}
		return result;
	}
	
	class RampUpPartGroup{
		Map<String, List<ConfiguratorPart>> map = new HashMap<String, List<ConfiguratorPart>>();
		
		void add(ConfiguratorPart part){
			List<ConfiguratorPart> list = map.get(part.getPartNum());
			
			if(list == null){
				list = new ArrayList<ConfiguratorPart>();
				map.put(part.getPartNum(), list);
			}
			list.add(part);
		}
		
		RampUpSequenceComparator comparator = new RampUpSequenceComparator();
		
		List<ConfiguratorPart> getPartWithRampUps(List<ConfiguratorPart> mainPartList){
			List<ConfiguratorPart> result = new ArrayList<ConfiguratorPart>();
			for(List<ConfiguratorPart> list : map.values()){
				Collections.sort(list, comparator);
				
				//Find the main part for this ramp up list
				ConfiguratorPart rampUpPart = list.get(0);
				ConfiguratorPart mainPart = null;
				for(Iterator it = mainPartList.iterator(); it.hasNext(); ){
					ConfiguratorPart part = (ConfiguratorPart)it.next();
					
					if(part.getPartNum().equals(rampUpPart.getPartNum())){
						mainPart = part;
						it.remove();
					}
				}
				
				//Calculate the total term
				int totalRampUpTerm = 0;
				for(ConfiguratorPart part : list){
					if(part.getTerm() != null){
						totalRampUpTerm += part.getTerm();
					}
				}
				int totalTerm = totalRampUpTerm + mainPart.getTerm();
				mainPart.setTotalTerm(totalTerm);
				
				//Compose the ramp up part list
				List<ConfiguratorPart> rampUps = new ArrayList<ConfiguratorPart>();
				for(ConfiguratorPart part : list){
					part.setTotalTerm(totalTerm);
					rampUps.add(part);
				}
				mainPart.setRampUpLineItems(rampUps);
				
				result.add(mainPart);
			}
			
			return result;
		}
	}
	
	class RampUpSequenceComparator implements Comparator<ConfiguratorPart>{
	    public int compare(ConfiguratorPart o1, ConfiguratorPart o2){
	    	int seq1 = getInt(o1.getRampUpSeqNum());
	    	int seq2 = getInt(o2.getRampUpSeqNum());
	    	
	    	return (seq1 - seq2);
	    }
	    
	    private int getInt(String str){
	    	return Integer.parseInt(str);
	    }
	}
	
	private void handleCallError(Element element, StringBuffer errorCode) throws Exception{
		
		String errCode = StringUtils.trim(element.getAttributeValue("errorCode"));
		errorCode.append(errCode);
		String errorDescription = element.getAttributeValue("errorDescription");
		StringBuffer bf = new StringBuffer().append("Web service call completed --> ")
		.append("return code : ").append(errorCode).append(", code describetion : ")
		.append(errorDescription);
		
		logger.debug(this, bf.toString());
		
		if("1001".equals(errorCode) || "1002".equals(errorCode)){
			throw new Exception(bf.toString());
		}
	}
	
	
	private ConfiguratorPart populateConfigPart(Element itemElement, List<String> missingPartLst) throws Exception{
		if(itemElement.getAttributeValue("missingPart") != null && itemElement.getAttributeValue("missingPart").equals("1"))//refer to rtc#215704
		{
			if(itemElement.getAttributeValue("partNumber") != null)
				missingPartLst.add(itemElement.getAttributeValue("partNumber"));
			return null;
		}
		
		ConfiguratorPart configPart = new ConfiguratorPart();
		
		configPart.setPartNum(itemElement.getAttributeValue("partNumber"));
		configPart.setBillingFrequencyCode((itemElement.getAttributeValue("billingFrequencyID")));
		
		Attribute attr = itemElement.getAttribute("quantity");
		if(attr != null && StringUtils.isNotBlank(attr.getValue())){
			configPart.setPartQty(attr.getIntValue());
		}
		
		attr = itemElement.getAttribute("billingFrequencyID");
		if(attr != null && StringUtils.isNotBlank(attr.getValue())){
			configPart.setBillingFrequencyCode(StringUtils.trim(attr.getValue()));
		}
		
		attr = itemElement.getAttribute("rampUpFlag");
		if(attr != null && StringUtils.isNotBlank(attr.getValue())){
			configPart.setRampUpFlag(StringUtils.trim(attr.getValue()));
		}
		
		attr = itemElement.getAttribute("rampUpSequenceNumber");
		if(attr != null && StringUtils.isNotBlank(attr.getValue())){
			configPart.setRampUpSeqNum(StringUtils.trim(attr.getValue()));
		}
		
		attr = itemElement.getAttribute("term");
		if(attr != null && StringUtils.isNotBlank(attr.getValue())){
			configPart.setTerm(attr.getIntValue());
		}
		
		return configPart;
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
	
	
	private String genEnvStrForLogin(){
		
		StringBuffer bf = new StringBuffer();
		bf.append("<YFSEnvironment progId=\"")
		.append(ApplicationProperties.getInstance().getWebserviceCPQProdId()).append("\" ")
		.append("userId=\"").append(ApplicationProperties.getInstance().getWebserviceCPQUserId())
		.append("\" />");
		
		logger.debug(this, "Environment xml string for login:" + bf.toString());
		return bf.toString();
	}
	
	private String genXmlStrForLogin() throws IOException{
		StringBuffer bf = new StringBuffer();
        String encodedPassword = ApplicationProperties.getInstance().getWebserviceCPQPassword();
        BASE64Decoder deCoder = new BASE64Decoder();
        try{
        	byte[] password = deCoder.decodeBuffer(encodedPassword);
    		bf.append("<Login LoginID=\"")
    		.append(ApplicationProperties.getInstance().getWebserviceCPQUserId()).append("\" ")
    		.append("Password=\"").append(new String(password)).append("\" ")
    		.append("Timeout=\"").append(ApplicationProperties.getInstance().getWebserviceCPQTimeout())
    		.append("\" />");
        }
        catch(IOException e){
        	logger.error(this, "base64 decode password failed :" + encodedPassword);
        	logger.error(this, e.getMessage());
        	throw e;
        }
        
        logger.debug(this, "Input xml string for login:" + bf.toString());
		return bf.toString();
	}
	
	private String genEnvStrForDswService(String userToken){
		
		StringBuffer bf = new StringBuffer();
		bf.append("<YFSEnvironment progId=\"")
		.append(ApplicationProperties.getInstance().getWebserviceCPQProdId()).append("\" ")
		.append("tokenId=\"").append(userToken).append("\" ")
		.append("userId=\"").append(ApplicationProperties.getInstance().getWebserviceCPQUserId())
		.append("\" />");

		logger.debug(this, "Environment xml string for dsw service call:" + bf.toString());
		
		return bf.toString();
	}
	
	private String genXmlStrForDswService(String configId, String userId){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
		String timestamp =  dateFormat.format((new Date(System.currentTimeMillis())));
		
		Random random = new Random();
		String ramdomLongValue = String.valueOf(random.nextLong());
		
		StringBuffer bf = new StringBuffer();
		bf.append(configId).append(timestamp).append(ramdomLongValue);
		
		String messageId = bf.toString();
		int length = messageId.length();
		if(length > 50){
			messageId = messageId.substring(0,49);
		}
		else if(length < 50){
			for(int j=length;j<50;j++){
				bf.append("0");
			}
			messageId = bf.toString();
		}

		StringBuffer bf2 = new StringBuffer();
		bf2.append("<dswGateway operation=\"getDSWConfiguration\">")
		.append("<getDSWConfiguration configurationID=\"").append(configId)
		.append("\" timestamp=\"").append(timestamp)
		.append("\" requestMessageUniqueID=\"").append(messageId)
		.append("\" requestorID=\"").append(userId)
		.append("\"/></dswGateway>");
		
		logger.debug(this, "XML string for dsw service call:" + bf2.toString());
		
		return bf2.toString();
	}
}
