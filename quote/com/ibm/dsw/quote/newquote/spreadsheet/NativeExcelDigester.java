package com.ibm.dsw.quote.newquote.spreadsheet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NativeExcelDigester.java</code> class.
 * 
 * @author: liangyue@cn.ibm.com
 * 
 * Creation date: 2010-03-02
 */
public class NativeExcelDigester{
    private HSSFWorkbook workbook = null;
    private NativeExcelUtil util = null;
    
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    private NativeExcelAdapter adapter = null;
    
    private static LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    public  NativeExcelDigester(HSSFWorkbook workbook) {
        this.workbook = workbook;
        this.util = new NativeExcelUtil(workbook);
    }
    
    public static NativeExcelDigester loadFile(File file) throws QuoteException {
        HSSFWorkbook workbook = null;
        FileInputStream inp = null;
        try{
            inp = new FileInputStream(file);
            workbook = new HSSFWorkbook(inp);
        }catch(FileNotFoundException e){
            e.printStackTrace();
            throw new QuoteException("Native excel file is not found - " + file);
        }catch(IOException e){
            e.printStackTrace();
            throw new QuoteException("Native excel file could not be set up as workbook - " + file);
        }finally{
        	try {
				inp.close();
			} catch (IOException e) {
				logContext.error(inp, "Error on Native excel digester: "+ e.getMessage());
			}
        }

		NativeExcelDigester generator = new NativeExcelDigester(workbook);
        return generator;
    }
    
    public boolean isValid() {
        return workbook != null;
    }
    
    public SpreadSheetQuote digest() throws QuoteException {
        if (!this.isValid())
            throw new QuoteException("No spreadsheet file available, please load template first");
        
        SpreadSheetQuote quote = new SpreadSheetQuote();
        try {
            quote.setLobCode(util.getCellTextValue("quoteType"));
            String templateAdapter = util.getCellTextValue("templateAdapter");
            if(templateAdapter != null && !templateAdapter.isEmpty()){
            	if(templateAdapter.equalsIgnoreCase("FCT2PA")){
            		adapter = new NativeExcelAdapterForFCT2PA();
            	}
            	else if(templateAdapter.equalsIgnoreCase("FCT2PAE")){
            		adapter = new NativeExcelAdapterForFCT2PAE();
            	}
            }
            else if(quote.isPA()){
                adapter = new NativeExcelAdapterForPA();
            } else if (quote.isPAE()){
                if(util.isExistSheetByNameName("epSaasPartNumberLabel")) {
                	adapter = new NativeExcelAdapterForPAE();
                } else {
                	adapter = new NativeExcelAdapterForPAEOld();
                }
            } else if (quote.isFCT()){
                adapter = new NativeExcelAdapterForFCT();
            } else if (quote.isOEM()){
                adapter = new NativeExcelAdapterForOEM();
            } 
            
	        //Sheet1 - EQ Customer
            this.digestEQCustomerTab(quote);
            
	        //Sheet2 - EQ Exported Parts and Pricing
            this.digestEQExportedPartPricingTab(quote);
            
            //Sheet3 - EQ Additional Parts and Pricing
            this.digestEQAdditionalPartPricingTab(quote);
            
            if(util.isExistSheetByNameName("epSaasPartNumberLabel")) {
            	//Sheet4 - EQ Exported Parts and Pricing
                this.digestEQExportedSaaSPartPricingTab(quote);
                
                //Sheet5 - EQ Additional SAAS Parts and Pricing
                this.digestEQAdditionalSaaSPartPricingTab(quote);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new QuoteException("Error when uploading the workbook");
        }
        return quote;
    }
	
	private void digestEQCustomerTab(SpreadSheetQuote quote) throws QuoteException{
		try{
		    //Quote Summary
	        quote.setWebQuoteNum(util.getCellTextValue("webQuoteNumber"));
	        quote.setCntryCode(util.getCellTextValue("countryCode"));
	        quote.setModDate(util.getCellTextValue("modDate"));
	        
	        //Customer info
	        quote.setSiteNum(util.getCellTextValue("siteNumber"));
	        quote.setCustomerNum(util.getCellTextValue("IBMCustomerNumber"));
	        //Agreement info
	        if(quote.isPA()){
	            quote.setSapCtrctNum(util.getCellTextValue("agreementNumber"));
	            quote.setQuoteSVPLevel(util.getCellTextValue("overrideQuoteSVPLevel"));	            
	        }else if(quote.isOEM()){
	            quote.setSapCtrctNum(util.getCellTextValue("agreementNumber"));
	        }else if(quote.isFCT()){
	            quote.setAcquisition(util.getCellTextValue("acquisitionCode"));
	            quote.setSapCtrctNum(util.getCellTextValue("agreementNumber"));
	        }
	        if(quote.isPA() || quote.isPAE()){
		        if(!"".equals(util.getCellTextValue("progMigrationCode"))){
	            	quote.setProgMigrtnCode(util.getCellTextValue("progMigrationCode"));
	            }
		        if(!"".equals(util.getCellTextValue("acquisitionCode"))){
	            	quote.setAcquisition(util.getCellTextValue("acquisitionCode"));
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new QuoteException("Error when reading the EQ Customer tab");
	    }
	}
	
	private void digestEQExportedPartPricingTab(SpreadSheetQuote quote) throws QuoteException{
		try{
            //Currency
            quote.setCurrency(util.getCellTextValue("currency"));
            
            HSSFSheet sheetEQExportedPartsAndPricing = util.getSheetByNameName("epPartNumberLabel");
            
            HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(workbook);
            
            List partList = new ArrayList();
            
            for(int i = util.getFirstPartRow("epPartNumberLabel").getRowNum();i<=util.getLastPartRow(quote,"epSubtotalPrice").getRowNum();i++){
                HSSFRow rowExportedPart = sheetEQExportedPartsAndPricing.getRow(i);
                SpreadSheetPart spreadsheetPart = new SpreadSheetPart();
                spreadsheetPart.setEpPartNumber(rowExportedPart.getCell(adapter.PART_COL_IND_PART_NUMBER).getRichStringCellValue().getString());
                if(rowExportedPart.getCell(adapter.PART_COL_IND_QUANTITY).getCellType() == HSSFCell.CELL_TYPE_BLANK){
                	spreadsheetPart.setEpQuantity(null);
                }else{

                	if(rowExportedPart.getCell(adapter.PART_COL_IND_QUANTITY).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                		spreadsheetPart.setEpQuantity(Double.toString(rowExportedPart.getCell(adapter.PART_COL_IND_QUANTITY).getNumericCellValue()));
                	}else{
                		spreadsheetPart.setEpQuantity(rowExportedPart.getCell(adapter.PART_COL_IND_QUANTITY).getRichStringCellValue().getString().replace(QuoteConstants.UP_TO, ""));
                	}
                	//spreadsheetPart.setEpQuantity(Double.toString(rowExportedPart.getCell(adapter.PART_COL_IND_QUANTITY).getNumericCellValue()));
                }
                spreadsheetPart.setEpSTDStartDate(simpleDateFormat.format(HSSFDateUtil.getJavaDate(evaluator.evaluate(rowExportedPart.getCell(adapter.PART_COL_IND_STANDARD_START_DATE_FORMULA)).getNumberValue())));
                spreadsheetPart.setEpSTDEndDate(simpleDateFormat.format(HSSFDateUtil.getJavaDate(evaluator.evaluate(rowExportedPart.getCell(adapter.PART_COL_IND_STANDARD_END_DATE_FORMULA)).getNumberValue())));
                if(quote.isPA()){
                    spreadsheetPart.setEpMaintenanceProrated(rowExportedPart.getCell(adapter.PART_COL_IND_PRORATE).getRichStringCellValue().getString());
                }
                spreadsheetPart.setEpAddYears(Integer.toString(new Double(rowExportedPart.getCell(adapter.PART_COL_IND_ADD_YEARS).getNumericCellValue()).intValue()));
                
                if(rowExportedPart.getCell(adapter.PART_COL_IND_OVERRIDE_START_DATE).getNumericCellValue()>0.0d){
                    spreadsheetPart.setEpOverrideDatesStartDate(simpleDateFormat.format(HSSFDateUtil.getJavaDate(rowExportedPart.getCell(adapter.PART_COL_IND_OVERRIDE_START_DATE).getNumericCellValue())));
                }
                
                if(rowExportedPart.getCell(adapter.PART_COL_IND_OVERRIDE_END_DATE).getNumericCellValue()>0.0d){
                    spreadsheetPart.setEpOverideDatesEndDate(simpleDateFormat.format(HSSFDateUtil.getJavaDate(rowExportedPart.getCell(adapter.PART_COL_IND_OVERRIDE_END_DATE).getNumericCellValue())));
                }
                
                if(rowExportedPart.getCell(adapter.PART_COL_IND_OVERRIDE_PRICE).getNumericCellValue()>0.0d){
                    spreadsheetPart.setEpEnterOnlyOneOverridePrice(Double.toString(rowExportedPart.getCell(adapter.PART_COL_IND_OVERRIDE_PRICE).getNumericCellValue()));
                }
                if(rowExportedPart.getCell(adapter.PART_COL_IND_DISCOUNT_PERCENT).getNumericCellValue()>0.0d){
                    spreadsheetPart.setEpEnterOnlyOneDiscountPercent(rowExportedPart.getCell(adapter.PART_COL_IND_DISCOUNT_PERCENT).getNumericCellValue());
                }
                if(!quote.isFCT()){
                    spreadsheetPart.setRenewalQuoteNumber(rowExportedPart.getCell(adapter.PART_COL_IND_RENEWAL_QUOTE_NUMBER).getRichStringCellValue().getString());
                }
                spreadsheetPart.setEpBPOvrrdDiscPct(rowExportedPart.getCell(adapter.PART_COL_IND_BP_OVERRIDE_DISCOUNT).getNumericCellValue());
                boolean isStr = rowExportedPart.getCell(adapter.PART_COL_IND_RENEWAL_QUOTE_LINE_ITEM_NUMBER).getCellType() == HSSFCell.CELL_TYPE_STRING ? true : false;
                if(isStr){
                	spreadsheetPart.setRenewalLineItemSeqNum(rowExportedPart.getCell(adapter.PART_COL_IND_RENEWAL_QUOTE_LINE_ITEM_NUMBER).getRichStringCellValue().getString());
                }else{
                	spreadsheetPart.setRenewalLineItemSeqNum((int)rowExportedPart.getCell(adapter.PART_COL_IND_RENEWAL_QUOTE_LINE_ITEM_NUMBER).getNumericCellValue()+"");
                }
                spreadsheetPart.setEpSortSeqNum((int)rowExportedPart.getCell(adapter.PART_COL_IND_SORT_SEQ_NUM).getNumericCellValue());
                spreadsheetPart.setEpAddtnlYearCvrageSeqNum((int)rowExportedPart.getCell(adapter.PART_COL_IND_ADDITIONAL_YEAR_COVERAGE_SEQ_NUM).getNumericCellValue());
                
                spreadsheetPart.setSourceTab(SpreadSheetPart.SOURCE_EP_TAB);
                quote.addEQPart(spreadsheetPart);
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new QuoteException("Error when reading the EQ Exported Part & Pricing tab");
	    }
	}
	
	private void digestEQExportedSaaSPartPricingTab(SpreadSheetQuote quote) throws QuoteException{
		try{
            //Currency
            quote.setCurrency(util.getCellTextValue("currency"));
            
            HSSFSheet sheetEQExportedPartsAndPricing = util.getSheetByNameName("epSaasPartNumberLabel");
            
            HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(workbook);
            
            List partList = new ArrayList();
            
            for(int i = util.getFirstPartRow("epSaasPartNumberLabel").getRowNum();i<=util.getLastPartRow(quote,"epSaasSubtotalBidTCV").getRowNum();i++){
                HSSFRow rowExportedPart = sheetEQExportedPartsAndPricing.getRow(i);
                SpreadSheetPart spreadsheetPart = new SpreadSheetPart();
                spreadsheetPart.setEpPartNumber(rowExportedPart.getCell(adapter.SAASPART_COL_IND_PART_NUMBER).getRichStringCellValue().getString());
                spreadsheetPart.setRampUpTitle(rowExportedPart.getCell(adapter.SAASPART_COL_IND_Ramp_Up_Period).getRichStringCellValue().getString());
                
                String migrationFlag = rowExportedPart.getCell(adapter.SAASPART_COL_IND_MIGRATED_PART).getRichStringCellValue().getString();
                spreadsheetPart.setMigrationFlag(migrationFlag.equalsIgnoreCase("yes") ? true : false);
                if(rowExportedPart.getCell(adapter.SAASPART_COL_IND_QUANTITY).getCellType() == HSSFCell.CELL_TYPE_BLANK){
                	spreadsheetPart.setEpQuantity(null);
                }else{
                	if(rowExportedPart.getCell(adapter.SAASPART_COL_IND_QUANTITY).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                		spreadsheetPart.setEpQuantity(Double.toString(rowExportedPart.getCell(adapter.SAASPART_COL_IND_QUANTITY).getNumericCellValue()));
                	}else{
                		spreadsheetPart.setEpQuantity(rowExportedPart.getCell(adapter.SAASPART_COL_IND_QUANTITY).getRichStringCellValue().getString().replace(QuoteConstants.UP_TO, ""));
                	}
                }
                if(rowExportedPart.getCell(adapter.SAASPART_COL_IND_TERM).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                	spreadsheetPart.setContractTerm(rowExportedPart.getCell(adapter.SAASPART_COL_IND_TERM).getNumericCellValue()+"");
                } else {
                	spreadsheetPart.setContractTerm(rowExportedPart.getCell(adapter.SAASPART_COL_IND_TERM).getRichStringCellValue().getString().replace(" months", ""));
                }
                spreadsheetPart.setBillingFrequency(rowExportedPart.getCell(adapter.SAASPART_COL_IND_BILLING_FREQUENCY).getRichStringCellValue().getString());
                if(rowExportedPart.getCell(adapter.SAASPART_COL_IND_OVERRIDE_PRICE).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
	                if(rowExportedPart.getCell(adapter.SAASPART_COL_IND_OVERRIDE_PRICE).getNumericCellValue()>0.0d){
	                    spreadsheetPart.setEpEnterOnlyOneOverridePrice(Double.toString(rowExportedPart.getCell(adapter.SAASPART_COL_IND_OVERRIDE_PRICE).getNumericCellValue()));
	                }
                }else{
                	spreadsheetPart.setEpEnterOnlyOneOverridePrice(rowExportedPart.getCell(adapter.SAASPART_COL_IND_OVERRIDE_PRICE).getRichStringCellValue().getString());
                }
                if(rowExportedPart.getCell(adapter.SAASPART_COL_IND_DISCOUNT_PERCENT).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                	spreadsheetPart.setEpEnterOnlyOneDiscountPercent(rowExportedPart.getCell(adapter.SAASPART_COL_IND_DISCOUNT_PERCENT).getNumericCellValue());
                }else{
                	if(StringUtils.isNotEmpty(rowExportedPart.getCell(adapter.SAASPART_COL_IND_DISCOUNT_PERCENT).getRichStringCellValue().getString())){
                		spreadsheetPart.setEpEnterOnlyOneDiscountPercent(new Double(rowExportedPart.getCell(adapter.SAASPART_COL_IND_DISCOUNT_PERCENT).getRichStringCellValue().getString()));
                	}
                }
                if(rowExportedPart.getCell(adapter.SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                	spreadsheetPart.setEpBPOvrrdDiscPct(rowExportedPart.getCell(adapter.SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT).getNumericCellValue());
                }else{
                	if(StringUtils.isNotEmpty(rowExportedPart.getCell(adapter.SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT).getRichStringCellValue().getString())){
                    	spreadsheetPart.setEpBPOvrrdDiscPct(new Double(rowExportedPart.getCell(adapter.SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT).getRichStringCellValue().getString()));
                    }
                }
                spreadsheetPart.setBrandCode(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_BRAND_CODE).getRichStringCellValue().getString());
                spreadsheetPart.setConfigrtrConfigrtnId(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_CONFIGRTRCONFIGRTNID).getRichStringCellValue().getString());
                spreadsheetPart.setConfigurationId(rowExportedPart.getCell(adapter.SAASPART_COL_IND_CONFIG_ID).getRichStringCellValue().getString());
                spreadsheetPart.setRefDocNum(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_REF_DOC_NUM).getRichStringCellValue().getString());
                spreadsheetPart.setErrorCode(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_ERROR_CODE).getRichStringCellValue().getString());
                spreadsheetPart.setConfigrtnAction(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_CONFIGRTN_ACTION).getRichStringCellValue().getString());
                spreadsheetPart.setConfigrationEndDate(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_ENDDATE).getRichStringCellValue().getString());
                spreadsheetPart.setCoTermToConfigrtnId(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_COTERM_TO_CONFIGRTNID).getRichStringCellValue().getString());
                spreadsheetPart.setOverrideFlag(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_OVERRIDE_FLAG).getBooleanCellValue());
                spreadsheetPart.setSaasProdHumanServicesPart(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_PRODHUMANSERVICE).getBooleanCellValue());
                spreadsheetPart.setSaasSetUpPart(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_SETUP).getBooleanCellValue());
                spreadsheetPart.setSaasSubscrptnPart(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_SUBSCRPTN).getBooleanCellValue());
                spreadsheetPart.setDecimal4Flag(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_DECIMAL4_FLAG).getBooleanCellValue());
                spreadsheetPart.setSourceTab(SpreadSheetPart.SOURCE_SAAS_EP_TAB);
                
                if(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_RAMPUP_PART) != null) {
                	spreadsheetPart.setSaasSubscrptnOvragePart(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_SUBSCRPTN_OVRAGE).getBooleanCellValue());
                    spreadsheetPart.setDestSeqNum(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_DEST_SEQ_NUM).getRichStringCellValue().getString());
                    spreadsheetPart.setiRelatedLineItmNum(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_RELATED_LINEITM_NUM).getRichStringCellValue().getString());
                    spreadsheetPart.setbRampUpPart(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_RAMPUP_PART).getBooleanCellValue());
                }
                
                if(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_PROVISIONING_ID) != null) {
                	spreadsheetPart.setProvisioningId(rowExportedPart.getCell(adapter.SAASPART_COL_IND_HIDDEN_PROVISIONING_ID).getRichStringCellValue().getString());
                }
                
                quote.addSaaSEQPart(spreadsheetPart);
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new QuoteException("Error when reading the EQ Exported SaaS Part & Pricing tab");
	    }
	}
	
	
	private void digestEQAdditionalPartPricingTab(SpreadSheetQuote quote) throws QuoteException{
		try{
            HSSFSheet sheetEQAdditionalPartsAndPricing = util.getSheetByNameName("apPartNumberLabel");
            
            HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(workbook);
            
            for(int i = util.getFirstPartRow("apPartNumberLabel").getRowNum();i<=util.getLastPartRow(quote,"apSubtotalPrice").getRowNum();i++){
                HSSFRow rowAdditionalPart = sheetEQAdditionalPartsAndPricing.getRow(i);
                String partNum = rowAdditionalPart.getCell(adapter.ADD_PART_COL_IND_PART_NUMBER).getRichStringCellValue().getString();
                if(!(null==partNum||"".equals(partNum.trim()))){
                    SpreadSheetPart spreadsheetPart = new SpreadSheetPart();
                    spreadsheetPart.setEpPartNumber(partNum);
                    spreadsheetPart.setEpQuantity(Double.toString(rowAdditionalPart.getCell(adapter.ADD_PART_COL_IND_QUANTITY).getNumericCellValue()));
                    
                    if(quote.isPA()){
                        spreadsheetPart.setEpMaintenanceProrated(rowAdditionalPart.getCell(adapter.ADD_PART_COL_IND_PRORATE).getRichStringCellValue().getString());
                    }
                    spreadsheetPart.setEpAddYears(Integer.toString(new Double(rowAdditionalPart.getCell(adapter.ADD_PART_COL_IND_ADD_YEARS).getNumericCellValue()).intValue()));
                    
                    if(rowAdditionalPart.getCell(adapter.ADD_PART_COL_IND_OVERRIDE_START_DATE).getNumericCellValue()>0.0d){
                        spreadsheetPart.setEpOverrideDatesStartDate(simpleDateFormat.format(HSSFDateUtil.getJavaDate(rowAdditionalPart.getCell(adapter.ADD_PART_COL_IND_OVERRIDE_START_DATE).getNumericCellValue())));
                    }
                    
                    if(rowAdditionalPart.getCell(adapter.ADD_PART_COL_IND_OVERRIDE_END_DATE).getNumericCellValue()>0.0d){
                        spreadsheetPart.setEpOverideDatesEndDate(simpleDateFormat.format(HSSFDateUtil.getJavaDate(rowAdditionalPart.getCell(adapter.ADD_PART_COL_IND_OVERRIDE_END_DATE).getNumericCellValue())));
                    }
                    
                    if(rowAdditionalPart.getCell(adapter.ADD_PART_COL_IND_OVERRIDE_PRICE).getNumericCellValue()>0.0d){
                        spreadsheetPart.setEpEnterOnlyOneOverridePrice(Double.toString(rowAdditionalPart.getCell(adapter.ADD_PART_COL_IND_OVERRIDE_PRICE).getNumericCellValue()));
                    }
                    if(rowAdditionalPart.getCell(adapter.ADD_PART_COL_IND_DISCOUNT_PERCENT).getNumericCellValue()>0.0d){
                        spreadsheetPart.setEpEnterOnlyOneDiscountPercent(rowAdditionalPart.getCell(adapter.ADD_PART_COL_IND_DISCOUNT_PERCENT).getNumericCellValue());
                    }
                    spreadsheetPart.setSourceTab(SpreadSheetPart.SOURCE_AP_TAB);
                    quote.addEQPart(spreadsheetPart);
                }
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new QuoteException("Error when reading the EQ Additional Part & Pricing tab");
	    }
	}

	private void digestEQAdditionalSaaSPartPricingTab(SpreadSheetQuote quote) throws QuoteException{
		try{
            HSSFSheet sheetEQAdditionalPartsAndPricing = util.getSheetByNameName("apSaasPartNumberLabel");
            
            HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(workbook);
            
            for(int i = util.getFirstPartRow("apSaasPartNumberLabel").getRowNum();i<=util.getLastPartRow(quote,"apSaasSubtotalBidTCV").getRowNum();i++){
                HSSFRow rowAdditionalPart = sheetEQAdditionalPartsAndPricing.getRow(i);
                String partNum = rowAdditionalPart.getCell(adapter.ADD_SAASPART_COL_IND_PART_NUMBER).getRichStringCellValue().getString();
                if(!(null==partNum||"".equals(partNum.trim()))){
                    SpreadSheetPart spreadsheetPart = new SpreadSheetPart();
                    spreadsheetPart.setEpPartNumber(partNum);
                    String migrationFlag = rowAdditionalPart.getCell(adapter.ADD_PART_COL_IND_MIGRATED_PART).getRichStringCellValue().getString();
                    spreadsheetPart.setMigrationFlag(migrationFlag.equalsIgnoreCase("yes") ? true : false);
                    spreadsheetPart.setEpQuantity(Double.toString(rowAdditionalPart.getCell(adapter.ADD_SAASPART_COL_IND_QUANTITY).getNumericCellValue()));
                    spreadsheetPart.setContractTerm(Double.toString(rowAdditionalPart.getCell(adapter.ADD_SAASPART_COL_IND_TERM).getNumericCellValue()));
                    spreadsheetPart.setBillingFrequency(rowAdditionalPart.getCell(adapter.ADD_SAASPART_COL_IND_BILLING_FREQUENCY).getRichStringCellValue().getString());
                    
                    spreadsheetPart.setEntitledRateVal(rowAdditionalPart.getCell(adapter.ADD_SAASPART_COL_IND_ENTITLED_RATE).getNumericCellValue());
                    spreadsheetPart.setBidRateVal(rowAdditionalPart.getCell(adapter.ADD_SAASPART_COL_IND_BID_RATE).getNumericCellValue());
                    
                    if(rowAdditionalPart.getCell(adapter.ADD_SAASPART_COL_IND_OVERRIDE_PRICE).getNumericCellValue()>0.0d){
                        spreadsheetPart.setEpEnterOnlyOneOverridePrice(Double.toString(rowAdditionalPart.getCell(adapter.ADD_SAASPART_COL_IND_OVERRIDE_PRICE).getNumericCellValue()));
                    }
                    if(rowAdditionalPart.getCell(adapter.ADD_SAASPART_COL_IND_DISCOUNT_PERCENT).getNumericCellValue()>0.0d){
                        spreadsheetPart.setEpEnterOnlyOneDiscountPercent(rowAdditionalPart.getCell(adapter.ADD_SAASPART_COL_IND_DISCOUNT_PERCENT).getNumericCellValue());
                    }
                    spreadsheetPart.setSourceTab(SpreadSheetPart.SOURCE_SAAS_AP_TAB);
                    quote.addSaaSEQPart(spreadsheetPart);
                }
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new QuoteException("Error when reading the EQ Additional SaaS Part & Pricing tab");
	    }
	}

}

