package com.ibm.dsw.quote.export.viewbean;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.export.config.ExportQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartSearchResultViewBean</code> 
 * 
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: Apr 19, 2007
 */
public class ExportQuoteViewBean extends BaseViewBean {
    public static final String CONTENT_TYPE_PDF = "application/pdf; charset=UTF-8";
    public static final String CONTENT_TYPE_RTF = "application/octet-stream; charset=UTF-8";
    
    private String exportQuoteContent ;
    private byte[] exportQuoteContentData ;
    private String webQuoteNum;
    private String fileName;
    private String execDownloadFileName = "";
    private String contentType = CONTENT_TYPE_PDF;
    
    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        
        this.exportQuoteContent = params.getParameterAsString(ExportQuoteParamKeys.PARAM_EXPORT_QUOTE_STR);
        params.addParameter(ExportQuoteParamKeys.PARAM_EXPORT_QUOTE_STR,"");
        this.webQuoteNum = params.getParameterAsString(ExportQuoteParamKeys.PARAM_EXPORT_QUOTE_NUM);
        this.exportQuoteContentData = (byte[]) params.getParameter(ExportQuoteParamKeys.PARAM_EXPORT_QUOTE_DATA);
        if(params.getParameter(ExportQuoteParamKeys.PARAM_EXPORT_QUOTE_ISSUMITTED) != null&& params.getParameterAsBoolean(ExportQuoteParamKeys.PARAM_EXPORT_QUOTE_ISSUMITTED)){
        	fileName = "ExportSubmittedQuote_"+ webQuoteNum + ".xls";
        }else{
        	fileName = "ExportQuote_"+ webQuoteNum + ".xls";
        }
        
        String exportType = params.getParameterAsString(ExportQuoteParamKeys.PARAM_EXPORT_QUOTE_RTF);
        if ("RTF".equalsIgnoreCase(exportType)) {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < exportQuoteContent.length(); ++i) {
                char ch = exportQuoteContent.charAt(i);
                if (ch > 0x7F) {
                    int intValue = ch;
                    buf.append("\\uc1");
                    buf.append("\\u");
                    buf.append(intValue);
                    buf.append("\\n\\r");
                    buf.append(" ");
                } else {
                    buf.append(ch);
                }
            }
            this.exportQuoteContent = buf.toString();
        }
        
        String fileExt = params.getParameterAsString(ExportQuoteParamKeys.PARAM_EXEC_SUMMARY_DOWNLOAD_FILE_EXT);
        if(StringUtils.isNotBlank(fileExt)){
            this.execDownloadFileName = "ExecutiveSummary_"+ webQuoteNum + fileExt;
            
            if(QuoteConstants.FileExtension.PDF.equals(fileExt)){
                contentType = CONTENT_TYPE_PDF;
            }
            
            if(QuoteConstants.FileExtension.RTF.equals(fileExt)){
                contentType = CONTENT_TYPE_RTF;
            }
        }
    }
    
    
    /**
     * @return Returns the exportQuoteContent.
     */
    public String getExportQuoteContent() {
        return exportQuoteContent;
    }
    
    public byte[] getExportQuoteData(){
    	return exportQuoteContentData;
    }
    
    public void cleanExportQuoteContent() {
    	exportQuoteContent = "";
    }
    
    /**
     * @return Returns the webQuoteNum.
     */
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    
    public String getFileName(){
    	return fileName;
    }
    
    public String getExecDownloadFileName(){
        return execDownloadFileName;
    }
    
    public String getContentType(){
        return contentType;
    }
}