package com.ibm.dsw.quote.newquote.spreadsheet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.export.exception.ExportQuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the zaiconfidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>SpreadSheetUtil</code> encapsulates all details of converting Java object to excel/rtf files or
 *  from excel to Java object. Any re-use of export/import should via this class.
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: Mar 07, 2007
 */

public class SpreadSheetUtil {
    
    private static final LogContext logger = LogContextFactory.singleton().getLogContext();
    
    private static SpreadSheetUtil instance = null;
    
    private static final String IMPORT_TEMPLATE_NAME = "xsl.import";
    
    private static final String EXPORT_TEMPLATE_SURFFIX = "xsl.export";
    
    private static final String NATIVE_EXCEL_EXPORT_TEMPLATE_SURFFIX = "native.excel.export";
    
    private static final String FCT_RENEWAL_TEMPLATE = "fct.renewal.xsl.export";
    
    private static final String SUBMITTED_TEMPLATE = "submitted.xsl.export";
    
    private static final String NATIVE_EXCEL_SUBMITTED_TEMPLATE = "submitted.native.excel.export";
    
    private static final String EXPORT_RTF_TEMPLATE_NAME = "rtf.sales.export";
    
    private SpreadSheetUtil () throws Exception {
    }
    
    /*
     * A single SpreadSheetUtil for the entire system
     */
    public static synchronized SpreadSheetUtil singleton() throws QuoteException  {
        if(instance == null)
            try {
                instance = new SpreadSheetUtil();
            } catch (Exception e) {
                logger.error(SpreadSheetUtil.class, "**FATAL*** can not init SpreadSheetUtil");
                logger.error(SpreadSheetUtil.class, LogThrowableUtil.getStackTraceContent(e));
                throw new QuoteException(e);
            }
            return instance;
    }
    
    /*
     * Check the uploaded file is whether XML spreadsheet or Native Excel spreadsheet
     */
    public  boolean isXMLFile(File file) throws QuoteException {
    	BufferedReader br = null;
    		try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = br.readLine();
            
            if(line.startsWith("<?xml")){
                logger.debug(this, file.getAbsolutePath() + " attempting to read from an XML Excel spreadsheet");
                return true;
            }else{
                logger.debug(this, file.getAbsolutePath() + " attempting to read from a Native Excel spreadsheet");
                return false;
            }
        } catch (Exception e) {
            logger.error(this, file.getAbsolutePath() + " error when checking the type of spreadsheet file");
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        }finally{
        	try {
				br.close();
			} catch (IOException e) {
				logger.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
        }
    }    
    
    /**
     * Ensure any input excel file is a well-formed and valid xml entity.
     * @param xmlFile
     * @return
     */
    public  boolean isValidXML(File xmlFile) {
        boolean valid = true;
        if (null == xmlFile)
            return false;
        try {
            SAXParserFactory.newInstance().newSAXParser().parse(xmlFile, new DefaultHandler());
        } catch (Exception e) {
            logger.error(this, xmlFile.getAbsolutePath() + " is not in Excel XML exported draft quote format");
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            valid = false;
        }
        return valid;
    }
    
    /**
     * Ensure any input excel file is a well-formed and valid xml entity.
     * @param xmlFile
     * @return
     */
    public  boolean isValidNativeExcel(File excelFile) {
        boolean valid = true;
        if (null == excelFile)
            return false;
        FileInputStream inp = null;
        try {
            inp = new FileInputStream(excelFile);
            HSSFWorkbook workbook = new HSSFWorkbook(inp);
            if(workbook.getNumberOfSheets()==7 || workbook.getNumberOfSheets()==5){
                valid = true;
            } else {
                valid = false;
            }
            
        } catch (Exception e) {
            logger.error(this, excelFile.getAbsolutePath() + " is not in Excel XML exported draft quote format");
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            valid = false;
        }finally{
        	try {
				inp.close();
			} catch (IOException e) {
				logger.error(this, LogThrowableUtil.getStackTraceContent(e));
			}
        }
        return valid;
    }
    
    /**
     * Extract excel data to Java Object
     * @param xmlFile uploaded spreadsheet 
     * @return SpreadSheetQuote
     * @throws QuoteException
     */
    public  SpreadSheetQuote importXMLSpreadSheet(File xmlFile)
    throws QuoteException {
    	InputStream importSource = null;
        ByteArrayOutputStream importResult = null ;
        try {
            //perform xlt trans
            importSource = xmlFile.toURL().openStream();
            importResult = new ByteArrayOutputStream();
            getTransformer(IMPORT_TEMPLATE_NAME).transfrom(importSource, importResult);
            
        } catch (Exception e) {
            logger.error(this, "failed to transfom " + xmlFile.getAbsolutePath());
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            try {
                if(importResult != null)
                	importResult.close();
            } catch (IOException ioe) {
                logger.error(this,  " unable to close io stream");
                throw new ExportQuoteException(ioe);
            } finally {
            	try {
    				importSource.close();
    			} catch (IOException e) {
    				logger.error(this, LogThrowableUtil.getStackTraceContent(e));
    			}
            }
        }
        return digest(importResult);
    }
    
    public SpreadSheetQuote importNativeExcelSpreadSheet(File file)
    throws QuoteException {
        try {
            NativeExcelDigester digester = NativeExcelDigester.loadFile(file);
            return digester.digest();
        } catch (Exception e) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        }
    }
    
    /**
     * @param importResult
     * @return
     * @throws QuoteException
     */
    private SpreadSheetQuote digest(ByteArrayOutputStream importResult) throws QuoteException {
        SpreadSheetQuote ssQuote = new SpreadSheetQuote();
        ByteArrayInputStream tempStream = new ByteArrayInputStream(importResult.toByteArray());
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(ssQuote.getContentHandler());
            parser.parse(new InputSource(tempStream));
            logger.debug(this, "reading ssQuote.....");
            logger.debug(this, ssQuote.toXMLString());
        } catch (Exception e) {
            throw new QuoteException(e);
        } finally {
            try {
                if(tempStream != null)
                	tempStream.close();
            } catch (IOException ioe) {
                logger.error(this,  " unable to close io stream");
                throw new ExportQuoteException(ioe);
            }
        }
        return ssQuote;
    }
    
    /**
     * @param ssQuote quote data returned from DB
     * @param excelResult quote data wrapped in this output stream
     * @throws QuoteException
     */
    public synchronized void exportToExcel(SpreadSheetQuote quote, OutputStream excelResult) throws QuoteException {
        ByteArrayInputStream quoteData = null;
        String exportTamplateName = "";
        if(quote.isSubmittedQuoteFlag()){
        	exportTamplateName = SUBMITTED_TEMPLATE;
        }else if (quote.isFCT() && quote.isRenwalQuoteFlag()){
            exportTamplateName = FCT_RENEWAL_TEMPLATE;
        } else {
            exportTamplateName = quote.getLobCode().toLowerCase() + "." + EXPORT_TEMPLATE_SURFFIX;
        }
        this.export(quote, excelResult, exportTamplateName, "UTF-8");
    }
    
    public void exportToRTF(SpreadSheetQuote quote, OutputStream excelResult) throws QuoteException {
        this.export(quote, excelResult, EXPORT_RTF_TEMPLATE_NAME, "UTF-8");
    }
    
    private void export(SpreadSheetQuote quote, OutputStream excelResult, String templateName, String encoding) throws QuoteException {
        logger.debug(this, "exportTamplateName : " + templateName);
        ByteArrayInputStream quoteData = null;
        
        try {
            logger.debug(this, "Before calling ByteArrayInputStream ......");
            quoteData = new ByteArrayInputStream(quote.toXMLString().getBytes(encoding));
            logger.debug(this, "After calling ByteArrayInputStream ......");
            XMLSSTransformer exportTransformer = this.getTransformer(templateName);
            logger.debug(this, "After calling XMLSSTransformer ......");
            exportTransformer.transfrom(quoteData, excelResult);
            logger.debug(this, "After calling exportTransformer.transfrom ......");
            excelResult.flush();
            logger.debug(this, "After excelResult.flush();");
        } catch (Exception e) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            try {
                if(quoteData != null)
                	quoteData.close();
            } catch (IOException ioe) {
                logger.error(this,  " unable to close io stream");
                throw new ExportQuoteException(ioe);
            }
        }
    }
    
    public void exportToNativeExcel(SpreadSheetQuote quote, OutputStream excelResult) throws QuoteException {
        String templateName = "";
        if(quote.isSubmittedQuoteFlag()){
            templateName = NATIVE_EXCEL_SUBMITTED_TEMPLATE;
        }else if (quote.isFCT() && quote.isRenwalQuoteFlag()){
            templateName = FCT_RENEWAL_TEMPLATE;
        } else {
        	if(quote.isFCTTOPA()){
        		templateName = "fct2" +quote.getLobCode().toLowerCase() + "." + NATIVE_EXCEL_EXPORT_TEMPLATE_SURFFIX;
        	}
        	else{
        		templateName = quote.getLobCode().toLowerCase() + "." + NATIVE_EXCEL_EXPORT_TEMPLATE_SURFFIX;
        	}
        }
        
        logger.debug(this, "exportNativeExcelTemplateName : " + templateName);
        
        try {
            NativeExcelGenerator generator = NativeExcelGenerator.loadTemplate(templateName);
            generator.generate(quote, excelResult);
            excelResult.flush();
        } catch (Exception e) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        }
    }
    
    protected XMLSSTransformer getTransformer(String templateName) throws ExportQuoteException {
        
        try {
            return XMLSSTransformer.loadTransformer(templateName);
        } catch (Exception e) {
            logger.error(this,  " unable to create a template from " + templateName);
            throw new ExportQuoteException(e);
        }
    }
}
