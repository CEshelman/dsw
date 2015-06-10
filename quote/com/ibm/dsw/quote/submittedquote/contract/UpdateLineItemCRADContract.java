/**
 * 
 */
package com.ibm.dsw.quote.submittedquote.contract;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.base.util.LocaleHelperImpl;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author crimsonlin
 *
 */
public class UpdateLineItemCRADContract extends SubmittedQuoteBaseContract {
	
	private static final String UN_SUPPORT_PARAM = "UN_SUPPORT_PARAM";
	private String partNum ="";
	private String redirectURL ="";
	
	HashMap items = new HashMap();
	
	public static class LineItemParameter {

		public String key; // format: PARTNUM_SEQNUM
		public Date custReqArrlDate;
        public String custQliReqstdArrivlDay;
        public String custQliReqstdArrivlMonth;
        public String custQliReqstdArrivlYear;
        public Boolean appSendtoMFG=false;
        public Date lineItemCRAD;
        
        //MTM_SERIAL INFO
    	public String applncPocInd;
        public String machineType;
        public String machineModel;
        public String machineSerialNumber;

        //deployment model
        public int deployModelOption;
        public String deployModelId;
        public String deployModelIdSystem;
        public boolean deployModelValid;
		
	}
	public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
       
        Locale locale = (Locale) session.getAttribute(FrameworkKeys.JADE_LOCALE_KEY);
        if (locale == null) {
            locale = LocaleHelperImpl.getDefaultDSWLocale();
        }
        redirectURL = parameters.getParameterAsString(ParamKeys.PARAM_REDIRECT_URL);
        Iterator it = parameters.getParameterNames();
        
        while (it.hasNext()) {
        	String name = (String) it.next();
        	try {
        		String value = null;
                try{
                    value = parameters.getParameterAsString(name);
                }catch(Throwable e){
                    LogContextFactory.singleton().getLogContext().debug(this,"param value of "+name+" is not a string ,just ignore it");
                    continue;
                }

                String key = getKey(name);
                LogContextFactory.singleton().getLogContext().debug(this,"param name="+name+"->key="+key);
                if (UN_SUPPORT_PARAM.equals(key)) {
                    continue;
                }
                
                LineItemParameter item = getLineItem(key);
                
    	        if (name.endsWith(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVAL_DATE_SUFFIX)){
    	        	LogContextFactory.singleton().getLogContext().debug(this,"lineItem cust req arrival date:"+value.trim());
    	        	item.custReqArrlDate = DateUtil.parseDate(value.trim(), DateUtil.PATTERN,locale);
    	        } else if (name.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_DAY)){
    	        	LogContextFactory.singleton().getLogContext().debug(this,"lineItem cust req arrival day:"+value.trim());
    	        	item.custQliReqstdArrivlDay = value.trim();
    	        } else if (name.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_MONTH)){
    	        	LogContextFactory.singleton().getLogContext().debug(this,"lineItem cust req arrival month:"+value.trim());
    	        	item.custQliReqstdArrivlMonth = value.trim();
    	        } else if (name.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_YEAR)){
    	        	LogContextFactory.singleton().getLogContext().debug(this,"lineItem cust req arrival year:"+value.trim());
    	        	item.custQliReqstdArrivlYear = value.trim();
    	        } else if (name.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_APP_SEND_MFG_FLG)){
    	        	LogContextFactory.singleton().getLogContext().debug(this,"lineItem app send mfg flag:"+value.trim());
    	        	item.appSendtoMFG = Boolean.valueOf(value.trim());
    	        } else if (name.endsWith(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVAL_DATE_ORG_SUFFIX)){
    	        	LogContextFactory.singleton().getLogContext().debug(this,"original lineItem  CRAD:"+value.trim());
    	        	item.lineItemCRAD = DateUtil.parseDate(value.trim(), DateUtil.PATTERN,locale);
    	        }
                
    	      //MTM_SERIAL INFO
    	        else if(name.endsWith(DraftQuoteParamKeys.APPLNC_MTM_TYPE)){
		        	item.machineType = value.trim();
		        } else if(name.endsWith(DraftQuoteParamKeys.APPLNC_MTM_MODEL)){
		        	item.machineModel = value.trim();
		        } else if(name.endsWith(DraftQuoteParamKeys.APPLNC_MTM_SERIAL_NUMBER)){
		        	item.machineSerialNumber = value.trim();
		        } else if(name.endsWith(DraftQuoteParamKeys.APPLNC_POC_IND)){
    	        	item.applncPocInd = value.trim();
		        }
    	        //deployment model
		        else if (name.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ASSOCIATION)) {
		        	item.deployModelOption = NumberUtils.stringToInt(value.trim());
		        } else if(name.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ID)) {
		        	item.deployModelId = value.trim();
		        } else if (name.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ID_SYSTEM)) {
    	        	item.deployModelIdSystem = value.trim();
    	        } else if(name.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ID_VALID)) {
    	        	item.deployModelValid = PartPriceConstants.DEPLOYMENT_ID_INVALID.equalsIgnoreCase(value.trim());
    	        }
                
        	} catch (Exception e){
        		 LogContextFactory.singleton().getLogContext().fatal(this, "Load parameter error:" + e.getMessage());
        	}

        }
        
        Iterator iter = items.values().iterator();
        while(iter.hasNext()){
            LineItemParameter item = (LineItemParameter)iter.next();
            if (StringUtils.isNotEmpty(item.custQliReqstdArrivlDay)&&StringUtils.isNotEmpty(item.custQliReqstdArrivlMonth)&&
            		StringUtils.isNotEmpty(item.custQliReqstdArrivlYear)&&(item.custReqArrlDate==null)){
            	String tempDateStr = item.custQliReqstdArrivlYear + "-" + item.custQliReqstdArrivlMonth + "-" + item.custQliReqstdArrivlDay;
            	try {
            		item.custReqArrlDate = DateUtil.parseDate(tempDateStr.trim(), DateUtil.PATTERN,locale);
            		LogContextFactory.singleton().getLogContext().debug(this,"item.custReqArrlDate="+item.custReqArrlDate);
            	} catch (Exception e) {
                    LogContextFactory.singleton().getLogContext().fatal(this, "Load parameter error:" + e.getMessage());
                }
            	
            }
            LogContextFactory.singleton().getLogContext().debug(this,"key="+item.key);
            LogContextFactory.singleton().getLogContext().debug(this,"After load parameter: item.custReqArrlDate="+item.custReqArrlDate);
        }
  
    }
	
    private LineItemParameter getLineItem(String key) {
        if (key != null && key.trim().length() != 0) {
            LineItemParameter item = (LineItemParameter) items.get(key);
            if (null == item) {
                item = new LineItemParameter();
                item.key = key;
            }
            items.put(key, item);
            return item;
        }
        return null;
    }
    
    private String getKey(String value) {
    	if (value != null && value.trim().length() != 0) {
    		//Appliance#99
	        if (value.endsWith(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVAL_DATE_SUFFIX)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVAL_DATE_SUFFIX);
	        	value = value.substring(0,index);
	        }
            //
	        else if (value.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_DAY)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_DAY);
	        	value = value.substring(0,index);
	        }
	        else if (value.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_MONTH)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_MONTH);
	        	value = value.substring(0,index);
	        }
	        else if (value.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_YEAR)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_LINEITEM_CUST_REQSTD_ARRIVL_YEAR);
	        	value = value.substring(0,index);
	        }
	        else if (value.endsWith(DraftQuoteParamKeys.PARAM_LINEITEM_APP_SEND_MFG_FLG)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_LINEITEM_APP_SEND_MFG_FLG);
	        	value = value.substring(0,index);
	        }
	        else if (value.endsWith(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVAL_DATE_ORG_SUFFIX)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVAL_DATE_ORG_SUFFIX);
	        	value = value.substring(0,index);
	        }
	        
	        //MTM_SERIAL INFO
	        else if(value.endsWith(DraftQuoteParamKeys.APPLNC_MTM_TYPE)){
  	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_MTM_TYPE);
  	        	value = value.substring(0,index);
  	        } else if(value.endsWith(DraftQuoteParamKeys.APPLNC_MTM_MODEL)){
  	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_MTM_MODEL);
  	        	value = value.substring(0,index);
  	        } else if(value.endsWith(DraftQuoteParamKeys.APPLNC_MTM_SERIAL_NUMBER)){
  	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_MTM_SERIAL_NUMBER);
  	        	value = value.substring(0,index);
  	        } else if(value.endsWith(DraftQuoteParamKeys.APPLNC_POC_IND)){
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_POC_IND);
	        	value = value.substring(0,index);
  	        } else if(value.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ASSOCIATION)) {
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_DEPLOY_ASSOCIATION);
	        	value = value.substring(0,index);
	        } else if(value.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ID)) {
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_DEPLOY_ID);
	        	value = value.substring(0,index);
	        } else if(value.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ID_SYSTEM)) {
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_DEPLOY_ID_SYSTEM);
	        	value = value.substring(0,index);
	        } else if(value.endsWith(DraftQuoteParamKeys.APPLNC_DEPLOY_ID_VALID)) {
	        	int index = value.lastIndexOf(DraftQuoteParamKeys.APPLNC_DEPLOY_ID_VALID);
	        	value = value.substring(0,index);
	        }
            else {
                value = UN_SUPPORT_PARAM;
            }
    	}
    	return value;
    }
    
    /**
     * @return Returns the items.
     */
    public HashMap getItems() {
        return items;
    }
    
    public String getPartNum() {
    	return partNum;
    }
    
	public String getRedirectURL() {
		return redirectURL;
	}

	public Date getOrgLineItemCRAD(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.lineItemCRAD != null ? item.lineItemCRAD:null;
	}
    
	public Date getLineItemCRAD(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.custReqArrlDate != null ? item.custReqArrlDate:null;
	}
    
	public boolean isCustReqstdArrivlDateValid(String year, String month, String day) {

		if (StringUtils.isBlank(year) && StringUtils.isBlank(month)
				&& StringUtils.isBlank(day))
			return true;

		if (!DateHelper.validateDate(year, month, day))
			return false;

		java.util.Date date = parseDate(year, month, day);
		Calendar curr = Calendar.getInstance();
		java.util.Date now = curr.getTime();
		java.util.Date currDate = DateUtils.truncate(now, Calendar.DATE);

		return (date != null && !currDate.after(date));
	}
	
    protected java.util.Date parseDate(String year, String month, String day) {
        
        if (StringUtils.isBlank(year) && StringUtils.isBlank(month) && StringUtils.isBlank(day))
            return null;
        
        if (!DateHelper.validateDate(year, month, day))
            return null;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        java.util.Date date = null;
        
        try {
            date = sdf.parse(year + month + day);
        } catch (ParseException e) {
            date = null;
        }
        
        return date;
    }
    
    //MTM_SERIAL INFO
    public String getMachineType(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.machineType != null ? item.machineType.trim():"" ;
	}

	public String getMachineModel(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.machineModel != null ? item.machineModel.trim():"";
	}

	public String getMachineSerialNumber(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.machineSerialNumber != null ? item.machineSerialNumber.trim():"";
	}

	public String getApplncPocInd(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item.applncPocInd != null ? item.applncPocInd.trim():DraftQuoteParamKeys.PARAM_NO;
	}
    //appliance deployment model
    public int getDeployModelOption(String key) {
    	LineItemParameter item = (LineItemParameter) items.get(key);
    	if (item == null) {
    		return 0;
    	}
    	return item.deployModelOption;
    }
    
    public String getDeployModelId(String key) {
		LineItemParameter item = (LineItemParameter) items.get(key);
		if (item == null) {
			return null;
		}
		if (PartPriceConstants.DEPLOYMENT_NOT_ON_QUOTE.equals(getDeployModelOption(key))){
			return StringUtils.trimToEmpty(item.deployModelId);
		}else if(PartPriceConstants.DEPLOYMENT_SELECT_DEFAULT.equals(getDeployModelOption(key))){
			return null;
		}
		return StringUtils.trimToEmpty(item.deployModelIdSystem);
    }

    public boolean isDeployModelValid(String key) {
        LineItemParameter item = (LineItemParameter) items.get(key);
        return item.deployModelValid;
    }
    // whether the current line item is a read-only item or not
    public boolean isExchangeLineItem(String key){
		LineItemParameter item = (LineItemParameter) items.get(key);
		return item != null ? true : false ;
	}
}
