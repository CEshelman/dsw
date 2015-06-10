package com.ibm.dsw.quote.findquote.viewbean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.findquote.config.FindQuoteMessageKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.dsw.quote.findquote.contract.DisplayFindQuoteByAppvlAttrContract;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;

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

public class DisplayFindQuoteByAppvlAttrViewBean extends DisplayFindViewBean {

    private int yearRange = 10;
    private int currYear = 0;
    private String approverGroupDateYear = "";
    private String approverTypeDateYear = "";
    private String approverGroupDateMonth = "";
    private String approverTypeDateMonth = "";
    private String approverGroupDateDay = "";
    private String approverTypeDateDay = "";
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy");
    /**
     * @return Returns the approverGroup.
     */
    public String getApproverGroup() {
        return ((DisplayFindQuoteByAppvlAttrContract) findContract).getApproverGroup();
    }
    
    /**
     * @return Returns the approverType.
     */
    public String getApproverType() {
        return ((DisplayFindQuoteByAppvlAttrContract) findContract).getApproverType();
    }

    /**
     * @return Returns the sbDistrict.
     */
    public String getSbDistrict() {
        return ((DisplayFindQuoteByAppvlAttrContract) findContract).getSbDistrict();
    }

    /**
     * @return Returns the sbRegion.
     */
    public String getSbRegion() {
        return ((DisplayFindQuoteByAppvlAttrContract) findContract).getSbRegion();
    }

    public List getSbRegionOptionList() {
        List sbRegionOptionList = new ArrayList();
        String selectOne = getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteMessageKeys.SELECT_ONE_OF_FOLLOWING);
        sbRegionOptionList.add(new SelectOptionImpl(selectOne, "", false));
        if (spRigions != null && spRigions.size() > 0)
            for (Iterator iter = spRigions.iterator(); iter.hasNext();) {
                String sbRegion = (String) iter.next();
                if (sbRegion != null && !sbRegion.equals(""))
                    sbRegionOptionList.add(new SelectOptionImpl(sbRegion, sbRegion, sbRegion
                            .equals(((DisplayFindQuoteByAppvlAttrContract) findContract).getSbRegion())));
            }

        return sbRegionOptionList;
    }

    public List getSbDistrictOptionList() {
        List sbDistrictOptionList = new ArrayList();
        String selectOne = getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteMessageKeys.SELECT_ONE_OF_FOLLOWING);
        sbDistrictOptionList.add(new SelectOptionImpl(selectOne, "", false));
        if (spDistricts != null && spDistricts.size() > 0)
            for (Iterator iter = spDistricts.iterator(); iter.hasNext();) {
                String sbDistrict = (String) iter.next();
                if (sbDistrict != null && !sbDistrict.equals(""))
                    sbDistrictOptionList.add(new SelectOptionImpl(sbDistrict, sbDistrict, sbDistrict
                            .equals(((DisplayFindQuoteByAppvlAttrContract) findContract).getSbDistrict())));
            }
        return sbDistrictOptionList;
    }

    public List getApproverGroupOptionList() {
        List approverGruopOptionList = new ArrayList();
        String selectOne = getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteMessageKeys.SELECT_ONE_OF_FOLLOWING);
        approverGruopOptionList.add(new SelectOptionImpl(selectOne, "", false));
        if (spGroups != null && spGroups.size() > 0)
            for (Iterator iter = spGroups.iterator(); iter.hasNext();) {
                String sbGroup = (String) iter.next();
                approverGruopOptionList.add(new SelectOptionImpl(sbGroup, sbGroup, sbGroup
                        .equals(((DisplayFindQuoteByAppvlAttrContract) findContract).getApproverGroup())));
            }

        return approverGruopOptionList;
    }

    public List getApproverTypeOptionList() {
        List approverTypeOptionList = new ArrayList();
        String selectOne = getI18NString(I18NBundleNames.BASE_MESSAGES, locale,
                FindQuoteMessageKeys.SELECT_ONE_OF_FOLLOWING);
        approverTypeOptionList.add(new SelectOptionImpl(selectOne, "", false));
        if (spTypes != null && spTypes.size() > 0)
            for (Iterator iter = spTypes.iterator(); iter.hasNext();) {
                String sbType = (String) iter.next();
                approverTypeOptionList.add(new SelectOptionImpl(sbType, sbType, sbType
                        .equals(((DisplayFindQuoteByAppvlAttrContract) findContract).getApproverType())));
            }

        return approverTypeOptionList;
    }
    public String getAppvlAttrPageURL(){
        String appvlAttrURL = "";

        appvlAttrURL += "&" + FindQuoteParamKeys.SB_REGION + "=" + this.getSbRegion();
        appvlAttrURL += "&" + FindQuoteParamKeys.SB_DISTRICT + "=" + this.getSbDistrict();
        appvlAttrURL += "&" + FindQuoteParamKeys.APPROVER_GROUP + "=" + this.getApproverGroup();
        appvlAttrURL += "&" + FindQuoteParamKeys.APPROVER_TYPE + "=" + this.getApproverType();
        
        String[] approverGroupFilterArr = this.getApproverGroupFilter();
        if(approverGroupFilterArr != null && approverGroupFilterArr.length > 0){
            appvlAttrURL += "&" + FindQuoteParamKeys.APPROVER_GROUP_FILTER + "=";
            for(int i = 0; i < approverGroupFilterArr.length; i++){
                appvlAttrURL += approverGroupFilterArr[i] + FindQuoteParamKeys.PARAMS_SPLIT_SIGN;
            }
        }
        
        if(StringUtils.isNotBlank(this.getApproverGroupDate())){
            appvlAttrURL += "&" + FindQuoteParamKeys.APPROVER_GROUP_DATE + "=" + this.getApproverGroupDate().trim();
        }
        
        if(StringUtils.isNotBlank(this.getApproverTypeDate())){
            appvlAttrURL += "&" + FindQuoteParamKeys.APPROVER_TYPE_DATE + "=" + this.getApproverTypeDate().trim();
        }
        
        String[] approverTypeFilterArr = this.getApproverTypeFilter();
        if(approverTypeFilterArr != null && approverTypeFilterArr.length > 0){
            appvlAttrURL += "&" + FindQuoteParamKeys.APPROVER_TYPE_FILTER + "=";
            for(int i = 0; i < approverTypeFilterArr.length; i++){
                appvlAttrURL += approverTypeFilterArr[i] + FindQuoteParamKeys.PARAMS_SPLIT_SIGN;
            }
        }
        
		return appvlAttrURL;
        
    }
    
    public String getPrePageURL(){
        String prePageURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_APPVLATTR");
        prePageURL += super.getPrePageURL();
        prePageURL += this.getAppvlAttrPageURL();
        
        return prePageURL;
    }
    
    public String getNextPageURL(){
        String nextPageURL = HtmlUtil.getURLForAction("FIND_QUOTE_BY_APPVLATTR");
        nextPageURL += super.getNextPageURL();
        nextPageURL += this.getAppvlAttrPageURL();
        
        return nextPageURL;
    }
    
    public String getChangeCriteriaURL(){
        String criteriaURL = HtmlUtil.getURLForAction("DISPLAY_FIND_QUOTE_BY_APPVLATTR_CSC");
        criteriaURL += super.getChangeCriteriaURLDetails();
        criteriaURL += this.getAppvlAttrPageURL();
        return criteriaURL;
    }
    
    
    public Collection generateYearOptions(String target) {
        Collection collection = new ArrayList();
        String yearValue="";
        if(target.equalsIgnoreCase("approverTypeFilter")){
            yearValue = approverTypeDateYear;
        }else if(target.equalsIgnoreCase("approverGroupFilter")){
            yearValue = approverGroupDateYear;
        }
        
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String sYear = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.YEAR);

        collection.add(new SelectOptionImpl(sYear, "", false));
        for (int i=0;i<yearRange;i++) {
           collection.add(new SelectOptionImpl(String.valueOf(currYear - i), String.valueOf(currYear - i), String.valueOf(currYear - i).equals(yearValue)));   
        }
        
        return collection;
    }
    
    public Collection generateMonthOptions(String target) {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String[] labels = {MessageKeys.MONTH, MessageKeys.MONTH_JAN, MessageKeys.MONTH_FEB, MessageKeys.MONTH_MAR,
                MessageKeys.MONTH_APR, MessageKeys.MONTH_MAY, MessageKeys.MONTH_JUN, MessageKeys.MONTH_JUL, MessageKeys.MONTH_AUG,
                MessageKeys.MONTH_SEP, MessageKeys.MONTH_OCT, MessageKeys.MONTH_NOV, MessageKeys.MONTH_DEC};
        
        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[0]);
        collection.add(new SelectOptionImpl(labelString, "", false));
        
        String monthValue="";
        if(target.equalsIgnoreCase("approverTypeFilter")){
            monthValue = approverTypeDateMonth;
        }else if(target.equalsIgnoreCase("approverGroupFilter")){
            monthValue = approverGroupDateMonth;
        }
        
        for (int i = 1; i < labels.length; i++) {
            labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, labels[i]);
            collection.add(new SelectOptionImpl(labelString, String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0').equalsIgnoreCase(monthValue)));
        }		    	
    	return collection;
    
    }

    public Collection generateDayOptions(String target) {
        Collection collection = new ArrayList();
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String labelString = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.DAY);
        collection.add(new SelectOptionImpl(labelString, "", false));
        
        String dayValue = "";
        if(target.equalsIgnoreCase("approverTypeFilter")){
            dayValue = approverTypeDateDay;
        }else if(target.equalsIgnoreCase("approverGroupFilter")){
            dayValue = approverGroupDateDay;
        }
        
        for (int i = 1; i <= 31; i++) {
	        collection.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), StringHelper.fillString(String.valueOf(i),2,'0').equals(dayValue)));
        }		    	
    	return collection;
    
    }
    
    public void collectResults(Parameters param) throws ViewBeanException {
        super.collectResults(param);
        Calendar rightNow = Calendar.getInstance();
        currYear = rightNow.get(Calendar.YEAR);
        
        String[] approverGroupDateArr = ((DisplayFindQuoteByAppvlAttrContract) findContract).getApproverGroupDate().split("-");
        String[] approverTypeDateArr = ((DisplayFindQuoteByAppvlAttrContract) findContract).getApproverTypeDate().split("-");
        if(approverGroupDateArr.length == 3){
            approverGroupDateYear = approverGroupDateArr[0];
            approverGroupDateMonth = approverGroupDateArr[1];
            approverGroupDateDay = approverGroupDateArr[2];
          
        }
        if(approverTypeDateArr.length == 3){
            approverTypeDateYear = approverTypeDateArr[0];
            approverTypeDateMonth = approverTypeDateArr[1];
            approverTypeDateDay = approverTypeDateArr[2];
        }
    }
    
    /**
     * @return Returns the yearRange.
     */
    public int getYearRange() {
        return yearRange;
    }
    
    
    /**
     * @return Returns the currYear.
     */
    public int getCurrYear() {
        return currYear;
    }
    
    public String getApproverGroupDate() {
        return ((DisplayFindQuoteByAppvlAttrContract) findContract).getApproverGroupDate();
    }
    
    public String getApproverTypeDate() {
        return ((DisplayFindQuoteByAppvlAttrContract) findContract).getApproverTypeDate();
    }
    


    /**
     * @return Returns the approverGroupFilter.
     */
    public String[] getApproverGroupFilter() {
        return ((DisplayFindQuoteByAppvlAttrContract) findContract).getApproverGroupFilter();
    }
    
    /**
     * @return Returns the approverTypeFilter.
     */
    public String[] getApproverTypeFilter() {
        return ((DisplayFindQuoteByAppvlAttrContract) findContract).getApproverTypeFilter();
    }
    
    public boolean isContain(String[]targetArray, String source){
        if(targetArray == null){
            return false;
        }else{
            for(int i = 0; i < targetArray.length; i++){
                if(StringUtils.isNotBlank(targetArray[i])){
                   if(targetArray[i].trim().equals(source.trim()))
                       return true;
                }
            }
            return false;
        }
    }
    
    public String getApproverGroupFilterName(){
        StringBuffer sb = new StringBuffer("");
        String result = "";
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String groupFilter;
        if(isContain(getApproverGroupFilter(),"APPROVERGROUP")){
            groupFilter = context.getI18nValueAsString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.QUOTE_PENDING_APPROVER_FOR_GROUP);
            sb.append(", " + StringUtils.lowerCase(groupFilter));
        }
        if(isContain(getApproverGroupFilter(),"APPROVERGROUPDATE")){
            groupFilter = context.getI18nValueAsString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.QUOTE_APPROVED_BY_GROUP_ON_DAY);
            sb.append(", " + StringUtils.lowerCase(groupFilter));
            if(getApproverGroupDate().length() > 0){
                try{
                    sb.append(" "+sdf2.format(sdf.parse(getApproverGroupDate())));
                }catch(Exception e){
                    sb.append(" "+getApproverGroupDate());
                }
            }
        }
        if(sb.length() >= 2){
            result = " (" + sb.substring(2) + ")";
        }
        return result;
    }
    
    public String getApproverTypeFilterName(){
        StringBuffer sb = new StringBuffer("");
        String result = "";
        Date date = null;
        
        ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
        String typeFilter;
        if(isContain(getApproverTypeFilter(),"APPROVERTYPE")){
            typeFilter = context.getI18nValueAsString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.QUOTE_PENDING_APPROVER_FOR_TYPE);
            sb.append(", " + StringUtils.lowerCase(typeFilter));
        }
        if(isContain(getApproverTypeFilter(),"APPROVERTYPEDATE")){
            typeFilter = context.getI18nValueAsString(I18NBundleNames.FIND_QUOTE_BASE, locale, FindQuoteParamKeys.QUOTE_APPROVED_BY_TYPE_ON_DAY);
            sb.append(", " + StringUtils.lowerCase(typeFilter));
            
            if(getApproverTypeDate().length() > 0){
                try{
                    sb.append(" "+sdf2.format(sdf.parse(getApproverTypeDate())));
                }catch(Exception e){
                    sb.append(" "+getApproverTypeDate());
                }
            }
        }
        if(sb.length() >= 2){
            result = " (" + sb.substring(2) + ")";
        }
        return result;
    }
 
}
