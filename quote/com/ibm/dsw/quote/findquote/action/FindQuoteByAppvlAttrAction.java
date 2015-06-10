package com.ibm.dsw.quote.findquote.action;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.findquote.config.FindQuoteMessageKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteStateKeys;
import com.ibm.dsw.quote.findquote.contract.FindQuoteByAppvlAttrContract;
import com.ibm.dsw.quote.findquote.process.QuoteStatusProcess;
import com.ibm.dsw.quote.findquote.process.QuoteStatusProcessFactory;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DisplayFindByCountryAlterAction</code> class.
 * 
 * @author zyuyang@cn.ibm.com
 * 
 * Created on 2010-5-12
 */

public class FindQuoteByAppvlAttrAction extends FindQuoteAction  {
        /*
         * (non-Javadoc)
         * 
         * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#executeProcess(com.ibm.ead4j.jade.contract.ProcessContract,
         *      com.ibm.ead4j.jade.bean.ResultHandler)
         */
        protected ResultBean executeProcess(ProcessContract contract, ResultHandler handler) throws QuoteException,
                ResultBeanException {
            FindQuoteByAppvlAttrContract findByAppvlAttrContract = (FindQuoteByAppvlAttrContract) contract;

            QuoteStatusProcess quoteStatusProcess = QuoteStatusProcessFactory.singleton().create();

            String salesQuoteFlag = getSalesQuoteFlag(findByAppvlAttrContract);

            String quoteType = getQuoteType(findByAppvlAttrContract);

            String overallStatus = getOverallStatus(findByAppvlAttrContract);

            String submittedDays = getSubmittedDays(findByAppvlAttrContract);
            
            String ecareFlag = getEcareFlag(findByAppvlAttrContract);

            String classification = getClassification(findByAppvlAttrContract);
            
            String groupPendingFlag = isContain(findByAppvlAttrContract.getApproverGroupFilter(), "APPROVERGROUP");
            
            String typePendingFlag = isContain(findByAppvlAttrContract.getApproverTypeFilter(), "APPROVERTYPE");
            
            String groupApprovedDate = isContain(findByAppvlAttrContract.getApproverGroupFilter(), "APPROVERGROUPDATE")
                .equals("1") ? findByAppvlAttrContract.getApproverGroupDate() : null;

            String typeApprovedDate = isContain(findByAppvlAttrContract.getApproverTypeFilter(), "APPROVERTYPEDATE")
            	.equals("1")?findByAppvlAttrContract.getApproverTypeDate():null;
            
            Map infoMap = new HashMap();

            SearchResultList results = quoteStatusProcess.findByAppvlAttr(findByAppvlAttrContract.getUserId(),
                    findByAppvlAttrContract.getReportingSalesReps(), salesQuoteFlag, quoteType, overallStatus, submittedDays,
                    findByAppvlAttrContract.getSortFilter(), findByAppvlAttrContract.getPageIndex(),
                    FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE, ecareFlag, findByAppvlAttrContract.getSbRegion(),
                    findByAppvlAttrContract.getSbDistrict(), findByAppvlAttrContract.getApproverGroup(),
                    groupPendingFlag,//groupPendingFlag
                    groupApprovedDate, 
                    findByAppvlAttrContract.getApproverType(),
                    typePendingFlag,//typePendingFlag
                    typeApprovedDate,
                    classification, findByAppvlAttrContract.getActuationFilter(),infoMap);

            
            //set cookie values if markMarkAppvlAttrDefault is set
            if (findByAppvlAttrContract.getMarkAppvlAttrDefault() != null
                    && !findByAppvlAttrContract.getMarkAppvlAttrDefault().equals("")) {
                javax.servlet.http.Cookie cookie = findByAppvlAttrContract.getSqoCookie();
                QuoteCookie.setSubmittedCustSBRegion(cookie, findByAppvlAttrContract.getSbRegion());
                QuoteCookie.setSubmittedCustSBDistrict(cookie, findByAppvlAttrContract.getSbDistrict());
                QuoteCookie.setSubmittedApproverGroup(cookie, findByAppvlAttrContract.getApproverGroup());
                QuoteCookie.setSubmittedApproverType(cookie, findByAppvlAttrContract.getApproverType());
                
                List approverTypeFilterList = asEmptyList(findByAppvlAttrContract.getApproverTypeFilter());
                QuoteCookie.setApproverTypeFilter(cookie, approverTypeFilterList);
                
                List approverGroupFilterList = asEmptyList(findByAppvlAttrContract.getApproverGroupFilter());
                QuoteCookie.setApproverGroupFilter(cookie, approverGroupFilterList);
                
                QuoteCookie.setApproverGroupDate(cookie, findByAppvlAttrContract.getApproverGroupDate());
                
                QuoteCookie.setApproverTypeDate(cookie, findByAppvlAttrContract.getApproverTypeDate());   
            }

            handler.addObject(FindQuoteParamKeys.PO_GEN_STATUS, infoMap.get(FindQuoteParamKeys.PO_GEN_STATUS));
            handler.addObject(FindQuoteParamKeys.DISPLAY_FIND_CONTRACT, findByAppvlAttrContract);
            handler.addObject(FindQuoteParamKeys.FIND_RESULTS, results);
            return handler.getResultBean();
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.ibm.dsw.quote.findquote.action.FindQuoteAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
         */
        protected String getState(ProcessContract contract) {
            return FindQuoteStateKeys.STATE_DISPLAY_FIND_QUOTE_RESULT_BY_APPVLATTR;
        }

        protected String getValidationForm2(String[] forms, ProcessContract contract) {
            return "findByAppvlAttrForm";
        }
        
        private String isContain(String[] arr, String targetStr){
            if(arr != null){
                for(int i = 0; i < arr.length; i++){
                    if(arr[i] != null && arr[i].trim().equalsIgnoreCase(targetStr.trim()))
                        return "1";
                }
            }
            return "0";
        }
        
        protected boolean validate(ProcessContract contract){
            boolean valid = super.validate(contract);
            HashMap map = new HashMap();
            FindQuoteByAppvlAttrContract findByAppvlAttrContract = (FindQuoteByAppvlAttrContract) contract;
            LogContext logCtx = LogContextFactory.singleton().getLogContext();
            
            if (StringUtils.isBlank(findByAppvlAttrContract.getApproverGroup())
                && StringUtils.isBlank(findByAppvlAttrContract.getApproverType())
                && StringUtils.isBlank(findByAppvlAttrContract.getSbRegion())
                && StringUtils.isBlank(findByAppvlAttrContract.getSbDistrict())){
                
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteMessageKeys.ERR_ONE_OF_FOLLOWING_REQUIRED);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteMessageKeys.FIND_BY_APPVLATTR);
                
                map.put(FindQuoteParamKeys.SB_REGION, field);
                addToValidationDataMap(contract, map);
                valid = false;
            } 
            
            String groupDateFlag = isContain(findByAppvlAttrContract.getApproverGroupFilter(), "APPROVERGROUPDATE");
            String typeDateFlag = isContain(findByAppvlAttrContract.getApproverTypeFilter(), "APPROVERTYPEDATE");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try{
                if(groupDateFlag.equals("1")){
                	String groupApprovedDate = findByAppvlAttrContract.getApproverGroupDate();
                	boolean dateValidationFlag = true;
                	if(StringUtils.isNotBlank(groupApprovedDate)){
                	    String dateStr = df.format(df.parse(groupApprovedDate));
                	    if(!dateStr.equals(groupApprovedDate)){
                	        dateValidationFlag = false;
                	    }
                	}else{ 
                	    dateValidationFlag = false;
                	}
                	
                	if(!dateValidationFlag){
                	    FieldResult field = new FieldResult();
                        field.setMsg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteMessageKeys.APPROVER_GROUP_DATE_INVALID);
                        field.addArg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteParamKeys.APPROVER_GROUP);
                        map.put(FindQuoteParamKeys.APPROVER_GROUP, field);
                        addToValidationDataMap(contract, map);
                        valid = false;
                	}
            	}
                
                if(typeDateFlag.equals("1")){
                	String typeApprovedDate = findByAppvlAttrContract.getApproverTypeDate();
                	boolean dateValidationFlag = true;
                	if(StringUtils.isNotBlank(typeApprovedDate)){
                	    String dateStr = df.format(df.parse(typeApprovedDate));
                	    if(!dateStr.equals(typeApprovedDate)){
                	        dateValidationFlag = false;
                	    }
                	}else{ 
                	    dateValidationFlag = false;
                	}
                	
                	if(!dateValidationFlag){
                	    FieldResult field = new FieldResult();
                        field.setMsg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteMessageKeys.APPROVER_TYPE_DATE_INVALID);
                        field.addArg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteParamKeys.APPROVER_TYPE);
                        map.put(FindQuoteParamKeys.APPROVER_TYPE, field);
                        addToValidationDataMap(contract, map);
                        valid = false;
                	}
            	}
            }catch(Exception e){
                logCtx.error(this, e.getMessage());
            }
            
            if(findByAppvlAttrContract.getApproverGroupFilter() != null && findByAppvlAttrContract.getApproverGroup().trim().equals("")){
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteMessageKeys.APPROVER_GROUP_REQUIRED);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteParamKeys.APPROVER_GROUP);
                map.put(FindQuoteParamKeys.APPROVER_GROUP, field);
                addToValidationDataMap(contract, map);
                valid = false;
            }
            
            if(findByAppvlAttrContract.getApproverTypeFilter() != null && findByAppvlAttrContract.getApproverType().trim().equals("")){
                FieldResult field = new FieldResult();
                field.setMsg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteMessageKeys.APPROVER_TYPE_REQUIRED);
                field.addArg(MessageKeys.BUNDLE_APPL_I18N_FIND_QUOTE, FindQuoteParamKeys.APPROVER_TYPE);
                map.put(FindQuoteParamKeys.APPROVER_TYPE, field);
                addToValidationDataMap(contract, map);
                valid = false;
            }
            return valid;
        }
        
        private List asEmptyList(String[] stringArr){
            List list = new ArrayList();
            if(stringArr != null){
                for(int i = 0; i < stringArr.length; i++){
                    list.add(stringArr[i]);
                }
            }
            return list;
        }
    }

