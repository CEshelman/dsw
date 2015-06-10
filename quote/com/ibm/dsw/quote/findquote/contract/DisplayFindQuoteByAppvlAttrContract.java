package com.ibm.dsw.quote.findquote.contract;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.findquote.config.FindQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
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

public class DisplayFindQuoteByAppvlAttrContract extends FindQuoteContract {

        String sbRegion;

        String sbDistrict;

        String approverGroup;

        String approverType;

        String markAppvlAttrDefault;
        
        String approverGroupDate;
        
        String approverTypeDate;
        
        String[] approverGroupFilter;
        
        String[] approverTypeFilter;

        public void load(Parameters parameters, JadeSession session) {
            this.loadFromCookie(parameters, session);
        }

        public void loadFromRequest(Parameters parameters, JadeSession session) {
            super.loadFromRequest(parameters, session);
            this.setSbRegion(parameters.getParameterAsString(FindQuoteParamKeys.SB_REGION));
            this.setSbDistrict(parameters.getParameterAsString(FindQuoteParamKeys.SB_DISTRICT));
            this.setApproverGroup(parameters.getParameterAsString(FindQuoteParamKeys.APPROVER_GROUP));
            this.setApproverType(parameters.getParameterAsString(FindQuoteParamKeys.APPROVER_TYPE));
            this.setMarkAppvlAttrDefault(parameters
                    .getParameterAsString(FindQuoteParamKeys.MARK_APPVL_ATTR_DEFAULT));
            this.setApproverGroupDate(parameters.getParameterAsString(FindQuoteParamKeys.APPROVER_GROUP_DATE));
            this.setApproverTypeDate(parameters.getParameterAsString(FindQuoteParamKeys.APPROVER_TYPE_DATE));
            
            if (this.getFlag != null) {
                String approverTypeFilterString = parameters.getParameterAsString(FindQuoteParamKeys.APPROVER_TYPE_FILTER);
                if (StringUtils.isNotBlank(approverTypeFilterString)) {
                    this.setApproverTypeFilter(approverTypeFilterString.split(FindQuoteParamKeys.PARAMS_SPLIT_SIGN));
                }
                String approverGroupFilterString = parameters.getParameterAsString(FindQuoteParamKeys.APPROVER_GROUP_FILTER);
                if (StringUtils.isNotBlank(approverGroupFilterString)) {
                    this.setApproverGroupFilter(approverGroupFilterString.split(FindQuoteParamKeys.PARAMS_SPLIT_SIGN));
                }
            }else{
                this.setApproverTypeFilter(parameters.getParameterWithMultiValues(FindQuoteParamKeys.APPROVER_TYPE_FILTER));
                this.setApproverGroupFilter(parameters.getParameterWithMultiValues(FindQuoteParamKeys.APPROVER_GROUP_FILTER));
            }
        }

        public void loadFromCookie(Parameters parameters, JadeSession session) {
            super.loadFromCookie(parameters, session);
            if (sqoCookie == null)
                return;// Normally it never goes here.
            this.setSbRegion(QuoteCookie.getSubmittedCustSBRegion(sqoCookie));
            this.setSbDistrict(QuoteCookie.getSubmittedCustSBDistrict(sqoCookie));
            this.setApproverGroup(QuoteCookie.getSubmittedApproverGroup(sqoCookie));
            this.setApproverType(QuoteCookie.getSubmittedApproverType(sqoCookie));
            
            this.setApproverTypeFilter(toEmptyArray(QuoteCookie.getApproverTypeFilter(sqoCookie)));
            this.setApproverGroupFilter(toEmptyArray(QuoteCookie.getApproverGroupFilter(sqoCookie)));
            
            this.setApproverGroupDate(QuoteCookie.getApproverGroupDate(sqoCookie));
            this.setApproverTypeDate(QuoteCookie.getApproverTypeDate(sqoCookie));
        }

        /**
         * @return Returns the approverGroup.
         */
        public String getApproverGroup() {
            return notNullString(approverGroup);
        }

        /**
         * @param approverGroup
         *            The approverGroup to set.
         */
        public void setApproverGroup(String approverGroup) {
            this.approverGroup = approverGroup;
        }

        /**
         * @return Returns the markAppvlAttrDefault.
         */
        public String getMarkAppvlAttrDefault() {
            return markAppvlAttrDefault;
        }
        /**
         * @param markAppvlAttrDefault The markAppvlAttrDefault to set.
         */
        public void setMarkAppvlAttrDefault(String markAppvlAttrDefault) {
            this.markAppvlAttrDefault = markAppvlAttrDefault;
        }
        /**
         * @return Returns the sbDistrict.
         */
        public String getSbDistrict() {
            return notNullString(sbDistrict);
        }

        /**
         * @param sbDistrict
         *            The sbDistrict to set.
         */
        public void setSbDistrict(String sbDistrict) {
            this.sbDistrict = sbDistrict;
        }

        /**
         * @return Returns the sbRegion.
         */
        public String getSbRegion() {
            return notNullString(sbRegion);
        }

        /**
         * @param sbRegion
         *            The sbRegion to set.
         */
        public void setSbRegion(String sbRegion) {
            this.sbRegion = sbRegion;
        }

        /**
         * @return Returns the approverType.
         */
        public String getApproverType() {
            return notNullString(approverType);
        }

        /**
         * @param approverType
         *            The approverType to set.
         */
        public void setApproverType(String approverType) {
            this.approverType = approverType;
        }
        
        
        /**
         * @return Returns the approverGroupDate.
         */
        public String getApproverGroupDate() {
            return notNullString(approverGroupDate);
        }
        /**
         * @param approverGroupDate The approverGroupDate to set.
         */
        public void setApproverGroupDate(String approverGroupDate) {
            this.approverGroupDate = approverGroupDate;
        }
        
        
        /**
         * @return Returns the approverTypeDate.
         */
        public String getApproverTypeDate() {
            return notNullString(approverTypeDate);
        }
        /**
         * @param approverTypeDate The approverTypeDate to set.
         */
        public void setApproverTypeDate(String approverTypeDate) {
            this.approverTypeDate = approverTypeDate;
        }
        
        
        /**
         * @return Returns the approverGroupFilter.
         */
        public String[] getApproverGroupFilter() {
            return approverGroupFilter;
        }
        /**
         * @param approverGroupFilter The approverGroupFilter to set.
         */
        public void setApproverGroupFilter(String[] approverGroupFilter) {
            this.approverGroupFilter = approverGroupFilter;
        }
        /**
         * @return Returns the approverTypeFilter.
         */
        public String[] getApproverTypeFilter() {
            return approverTypeFilter;
        }
        /**
         * @param approverTypeFilter The approverTypeFilter to set.
         */
        public void setApproverTypeFilter(String[] approverTypeFilter) {
            this.approverTypeFilter = approverTypeFilter;
        }
        
        private String[] toEmptyArray(List list){
            if(list != null){
                String[] result = new String[list.size()];
                for(int i = 0; i < list.size(); i++){
                    result[i] = (String)list.get(i);
                }
                return result;
            }else{
                return (new String[0]);
            }
        }
    }

