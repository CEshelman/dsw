package com.ibm.dsw.quote.findquote.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteClassificationCodeFactory;
import com.ibm.dsw.quote.common.util.LOBListUtil;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.findquote.config.FindQuoteMessageKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.contract.FindQuoteContract;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;

/**
 * @author wangxu@cn.ibm.com
 */
public abstract class FindQuoteAction extends BaseContractActionHandler {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.base.action.BaseContractActionHandler#executeBiz(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    public ResultBean executeBiz(ProcessContract contract, ResultHandler handler) throws QuoteException,
            ResultBeanException {
        FindQuoteContract findQuoteContract = (FindQuoteContract) contract;
        setFindQuoteCookies(findQuoteContract);

        handler.addObject(FindQuoteParamKeys.OVERALL_STATUS_LIST, CacheProcessFactory.singleton().create()
                .findOverallStatus(((QuoteBaseContract)contract).getLocale()));
        handler.addObject(FindQuoteParamKeys.LOB_LIST, LOBListUtil.getLobs(((QuoteBaseContract)contract).getLocale(),getAudienceCode(findQuoteContract)));
        handler.addObject(FindQuoteParamKeys.COUNTRY_LIST_AS_CODE_DESC, CacheProcessFactory.singleton().create()
                .getCountryListAsCodeDescObj());
        handler.addObject(FindQuoteParamKeys.CLASSIFICATION_LIST, QuoteClassificationCodeFactory.singleton().getAllQuoteClassfctnCodes());
        handler.addObject(FindQuoteParamKeys.ACTUATION_LIST, CacheProcessFactory.singleton().create().getAcquisitionList());

        handler.setState(getState(contract));
        return executeProcess(contract, handler);
    }

    /**
     * @param findQuoteContract
     */
    private void setFindQuoteCookies(FindQuoteContract findQuoteContract) {
        javax.servlet.http.Cookie cookie = findQuoteContract.getSqoCookie();
        if (findQuoteContract.getMarkFilterDefault() != null && !findQuoteContract.getMarkFilterDefault().equals("")) {
            String[] quoteTypeFilter = findQuoteContract.getQuoteTypeFilter();
            List quoteTypeFilterList = new ArrayList();
            if(quoteTypeFilter != null){
	            for (int i = quoteTypeFilter.length; i > 0; i--) {
	                quoteTypeFilterList.add(quoteTypeFilter[i - 1]);
	            }
	            QuoteCookie.setRenewalOrSales(cookie, quoteTypeFilterList);
            }

            String[] LOBsFilter = findQuoteContract.getLOBsFilter();
            List LOBsFilterList = new ArrayList();
            for (int i = LOBsFilter.length; i > 0; i--) {
                LOBsFilterList.add(LOBsFilter[i - 1]);
            }
            QuoteCookie.setQuoteType(cookie, LOBsFilterList);
            
            String[] classificationFilter = findQuoteContract.getClassificationFilter();
            if(classificationFilter != null && classificationFilter.length > 0){
	            List classificationFilterList = new ArrayList();
	            for (int i = classificationFilter.length; i > 0; i--) {
	                classificationFilterList.add(classificationFilter[i - 1]);
	            }
	            QuoteCookie.setSearchClsfctnCookieValues(cookie, classificationFilterList);
            }

            QuoteCookie.setSearchAcqstnCookieValue(cookie, findQuoteContract.getActuationFilter());

            String[] statusFilter = findQuoteContract.getStatusFilter();
            List statusFilterList = new ArrayList();
            for (int i = 0; i < statusFilter.length; i++) {
                statusFilterList.add(statusFilter[i]);
            }
            QuoteCookie.setOverallStatus(cookie, statusFilterList);

            QuoteCookie.setSubmittedTime(cookie, (findQuoteContract.getTimeFilter() != null ? findQuoteContract
                    .getTimeFilter() : ""));

            QuoteCookie.setStatusSortBy(cookie, findQuoteContract.getSortFilter());
            
            String[] timeFilterOptions = findQuoteContract.getTimeFilterOptions();
            StringBuffer timeOptionsBuffer = new StringBuffer("");
            if(timeFilterOptions != null){
                for(int i = 0; i < timeFilterOptions.length; i++){
                    timeOptionsBuffer.append(timeFilterOptions[i]+" ");
                }
            }
            QuoteCookie.setSubmittedTimeOptions(cookie, timeOptionsBuffer.toString());
        }
    }

    protected abstract ResultBean executeProcess(ProcessContract contract, ResultHandler handler)
            throws QuoteException, ResultBeanException;

    protected abstract String getState(ProcessContract contract);

    protected String[] getValidationForms(ProcessContract contract) {
        String[] forms = new String[2];
        forms[0] = "findQuoteHeaderForm";
        forms[1] = getValidationForm2(forms, contract);
        return forms;
    }

    /**
     * @param contract
     * @return
     */
    protected abstract String getValidationForm2(String[] forms, ProcessContract contract);

    /**
     * @return
     */
    public String getSalesQuoteFlag(FindQuoteContract contract) {
        String salesQuoteFlag = "";
        if(contract.getQuoteTypeFilter() != null){
	        for (int i = 0; i < contract.getQuoteTypeFilter().length; i++) {
	            salesQuoteFlag = salesQuoteFlag + contract.getQuoteTypeFilter()[i]
	                    + FindQuoteDBConstants.DB2_LIST_ITEM_DIVIDER;
	        }
        }
        return salesQuoteFlag;
    }

    /**
     * @param contract
     * @return
     */
    public String getQuoteType(FindQuoteContract contract) {
        String quoteType = "";
        for (int i = 0; i < contract.getLOBsFilter().length; i++) {
            quoteType = quoteType + contract.getLOBsFilter()[i] + FindQuoteDBConstants.DB2_LIST_ITEM_DIVIDER;
        }
        return quoteType;
    }

    /**
     * @param contract
     * @return
     */
    public String getOverallStatus(FindQuoteContract contract) {
        String overallStatus = "";
        for (int i = 0; i < contract.getStatusFilter().length; i++) {
            overallStatus = overallStatus + contract.getStatusFilter()[i] + FindQuoteDBConstants.DB2_LIST_ITEM_DIVIDER;
        }
        return overallStatus;
    }

    /**
     * @param contract
     * @return
     */
    public String getSubmittedDays(FindQuoteContract contract)  throws QuoteException{
        String timeFilter = contract.getTimeFilter();
        String submittedDays = "";
        
        if(timeFilter.equals("Quarter") || timeFilter.equals("Month")){
            return mergeDate(contract.getTimeFilterOptions());
        }else{
            Calendar calendar = Calendar.getInstance();
    		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"); 
    		submittedDays = ":"+df.format(calendar.getTime())+",";
            if(timeFilter.equals("7")){
                calendar.add(Calendar.DATE, -7);
                submittedDays = df.format(calendar.getTime()) + submittedDays;
                return submittedDays;
            }else if(timeFilter.equals("36500")){
                calendar.add(Calendar.YEAR, -100);
                submittedDays = df.format(calendar.getTime()) + submittedDays;
                return submittedDays;
            }else{
                throw new QuoteException("error param:timeFilter = "+timeFilter);
            }
        }
        
    }

    /**
     * @param findQuoteContract
     * @return
     */
    public String getEcareFlag(FindQuoteContract contract) {
        if (contract.getUser().getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_ECARE)
            return "1";
        return "0";
    }

    protected boolean validate(ProcessContract contract) {
    	boolean valid = super.validate(contract);
        FindQuoteContract findQuoteContract = (FindQuoteContract) contract;
        boolean isPGSFlag = findQuoteContract.isPGSFlag();
        if (validateLoBTypes(contract)) {
        	String[] quoteTypes = findQuoteContract.getQuoteTypeFilter();
            String[] lobs = findQuoteContract.getLOBsFilter();
            HashMap map = new HashMap();
            if (!isPGSFlag && (quoteTypes == null || quoteTypes.length < 1)) {
               valid = false;
               FieldResult field = new FieldResult();
               field.setMsg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteMessageKeys.MSG_QUOTE_TYPES_REQUIRED);
               field.addArg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteParamKeys.QUOTE_TYPES);
               map.put(FindQuoteParamKeys.QUOTE_TYPES, field);
            }
            if (lobs == null || lobs.length < 1) {
                valid = false;
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteMessageKeys.MSG_LOBS_FILTER_REQUIRED);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteParamKeys.LOBS_FILTER);
                map.put(FindQuoteParamKeys.LOBS_FILTER, field);
            } else {
	            for(int i = 0; i < lobs.length; i++){
	                if(QuoteConstants.LOB_FCT.equalsIgnoreCase(lobs[i])){
	                    if(findQuoteContract.getClassificationFilter() == null || findQuoteContract.getClassificationFilter().length < 1){
	                        valid = false;
	                        FieldResult field = new FieldResult();
	                        field.setMsg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteMessageKeys.MSG_LOBS_FILTER_REQUIRED);
	                        field.addArg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteMessageKeys.QUOTE_CLASSIFICATION);
	                        map.put(FindQuoteParamKeys.CLASSIFICATION_FILTER, field);
	                        break;
	                    }
	                }
	            }
            }
            addToValidationDataMap(findQuoteContract, map);
        }

        return valid;
    }

    protected boolean validateLoBTypes(ProcessContract contract) {
        return true;
    }

    /**
     * @param contract
     * @return
     */
    public String getClassification(FindQuoteContract contract) {
        String classification = "";
        if(contract.getClassificationFilter() != null){
            classification = FindQuoteDBConstants.DB2_LIST_ITEM_DIVIDER;
	        for (int i = 0; i < contract.getClassificationFilter().length; i++) {
	            classification = classification + contract.getClassificationFilter()[i] + FindQuoteDBConstants.DB2_LIST_ITEM_DIVIDER;
	        }
        }
        return classification;
    }
    
    private String mergeDate(String[] argArr)  throws QuoteException{
		StringBuffer sb = new StringBuffer("");
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"); 
		List list = new ArrayList();
		
		for(int i = 0; i < argArr.length; i++){
		    if(argArr[i] != null){
		        list.add(argArr[i]);
		    }
		}
		
		Collections.sort(list);
		Calendar calendar = Calendar.getInstance();
		List nl = new ArrayList();
		try{
			for(int i = 0; i < list.size()-1; i++){
				String[] strArr = ((String)list.get(i)).split(":");
				calendar.setTime(df.parse(strArr[1]));
				
				String[] strArr2 = ((String)list.get(i+1)).split(":");
				calendar.add(Calendar.DATE,1); 
				boolean flag = calendar.getTime().equals(df.parse(strArr2[0]));
				if(flag){
					list.remove(i+1);
					list.set(i,strArr[0] + ":" + strArr2[1]);
					i--;
				}
			}
		}catch(Exception e){
		    throw new QuoteException(e);
		}
		for(int i = 0; i < list.size(); i++){
		    sb.append(list.get(i)+",");
		}
		String result = sb.toString();
		
		return result;

	}
    
    
    public String getAudienceCode(QuoteBaseContract contract){
    	return contract.getQuoteUserSession().getAudienceCode();
    }
}
