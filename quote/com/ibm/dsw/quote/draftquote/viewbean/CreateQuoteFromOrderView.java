package com.ibm.dsw.quote.draftquote.viewbean; 

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.QuoteLockInfo;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQParamKeys;
import com.ibm.dsw.quote.draftquote.contract.EditRQContract;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2010 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM 
 * Corporation. ("Confidential Information").
 * 
 * This <code>CreateQuoteFromOrderView<code> class.
 *    
 * @author: cyxu@cn.ibm.com
 * 
 * Creation date: Nov 3, 2010
 */
public class CreateQuoteFromOrderView extends DisplayQuoteBaseViewBean {
    
    private String returnBtnParams = "";
    private boolean isDisplayReturnBtn = false;
    
    public void collectResults(Parameters params) throws ViewBeanException {
       
        String redirectParams = (String) params.getParameter(ParamKeys.PARAM_CREATE_QT_FROM_ORDER_URL);
        
        if(StringUtils.isNotBlank(redirectParams)) {
            isDisplayReturnBtn = true;
            returnBtnParams = genGobackURL(redirectParams);
        }
        
    }

    @Override
    public String getDisplayTabAction() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPostTabAction() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getReturnBtnParams() {
        return returnBtnParams;
    }

    public boolean isDisplayReturnBtn() {
        return isDisplayReturnBtn;
    }
    
    private String genGobackURL(String returnUrl) {
        String decodedURL = HtmlUtil.urlDecode(returnUrl);
        String realURL = StringUtils.replace(decodedURL, ";", "&amp;");
        StringBuffer sb = new StringBuffer(HtmlUtil.getURLForReporting(realURL));
        return sb.toString();
    }

}
 