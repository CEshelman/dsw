package com.ibm.dsw.quote.newquote.spreadsheet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.hssf.util.CellRangeAddressList;
import org.apache.poi.hssf.util.CellReference;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.ead4j.common.config.EAD4JBootstrapKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>NativeExcelGenerator.java</code> class.
 * 
 * @author: liangyue@cn.ibm.com
 * 
 * Creation date: 2010-03-02
 */
public class NativeExcelGenerator{
    private static ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
    
    protected static final String codeBase = appCtx.getConfigParameter(EAD4JBootstrapKeys.EAD4J_CODEBASE_PATH_KEY);

    private HSSFWorkbook workbook = null;
    private NativeExcelUtil util = null;
    
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd-MMM-yyyy");
    
    private NativeExcelAdapter adapter = null;
    
    private static LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    public  NativeExcelGenerator(HSSFWorkbook workbook) {
        this.workbook = workbook;
        this.util = new NativeExcelUtil(workbook);
    }
    
    public static NativeExcelGenerator loadTemplate(String templateName) throws QuoteException {
        if (templateName == null)
            throw new QuoteException("invalid template: templateName is " + templateName);
        
        String templatePath =  codeBase + appCtx.getConfigParameter(templateName);
        HSSFWorkbook workbook = null;
        FileInputStream inp = null;
        try{
            inp = new FileInputStream(templatePath);
            workbook = new HSSFWorkbook(inp);
        }catch(FileNotFoundException e){
            e.printStackTrace();
            throw new QuoteException("template file is not found - " + templatePath);
        }catch(IOException e){
            e.printStackTrace();
            throw new QuoteException("template Workbook could not be set up - " + templatePath);
        }finally{
        	try {
				inp.close();
			} catch (IOException e) {
				logContext.error(inp, "Native excel digester error: "+ e.getMessage());
			}
        }
		
		NativeExcelGenerator generator = new NativeExcelGenerator(workbook);
        return generator;
    }

    public static NativeExcelGenerator loadFile(File file) throws QuoteException {
        HSSFWorkbook workbook = null;
        FileInputStream inp = null;
        try{
            inp = new FileInputStream(file);
            workbook = new HSSFWorkbook(inp);
        }catch(FileNotFoundException e){
            e.printStackTrace();
            throw new QuoteException("native excel file is not found - " + file);
        }catch(IOException e){
            e.printStackTrace();
            throw new QuoteException("native excel file could not be set up as workbook - " + file);
        }finally{
        	try {
				inp.close();
			} catch (IOException e) {
				logContext.error(inp, "Native excel digester error: "+ e.getMessage());
			}
        }

		NativeExcelGenerator generator = new NativeExcelGenerator(workbook);
        return generator;
    }
    
    public boolean isValid() {
        return workbook != null;
    }

    public void generate(SpreadSheetQuote quote, OutputStream resultStream) throws QuoteException {
        if (!this.isValid())
            throw new QuoteException("No transform template available, please load template first");

        try {
            if (quote.isSubmittedQuoteFlag()){
            	if(quote.isPGSFlag())
            		adapter = new NativeExcelAdapterForSQPGS();
            	else
            		adapter = new NativeExcelAdapterForSQ();
            } else if(quote.isFCTTOPA()){
            	if(quote.isPA()){
                    adapter = new NativeExcelAdapterForFCT2PA();
                } else if (quote.isPAE()){
                    adapter = new NativeExcelAdapterForFCT2PAE();
                }
            } else if(quote.isPA()){
                adapter = new NativeExcelAdapterForPA();
            } else if (quote.isPAE()){
                adapter = new NativeExcelAdapterForPAE();
            } else if (quote.isFCT()){
                adapter = new NativeExcelAdapterForFCT();
            } else if (quote.isOEM()){
                adapter = new NativeExcelAdapterForOEM();
            } 
            
	        //Sheet1 - Spreedsheet Customer Tab
            this.generateEQCustomerTab(quote);
            
            if(!quote.isSubmittedQuoteFlag()){
                if (quote.isPA()){
                    //Hidden sheet - Spreedsheet Supporting Data
                    updatePricingSupportData(quote);
                }
                
		        //Sheet2 - Spreedsheet SW Exported Parts and Pricing Tab
	            this.generateEQExportedPartPricingTab(quote);
	            
	            //Sheet3 - Spreedsheet SW Additional Parts and Pricing Tab
				this.generateEQAdditionalPartPricingTab(quote);
				
				if(quote.isPA() || quote.isPAE() || quote.isFCT()) {
					//Sheet4 - Spreedsheet Saas Exported Parts and Pricing Tab
					this.generateEQSAASExportedPartPricingTab(quote);
					
					//Sheet5 - Spreedsheet SaaS Additional Part and Pricing Tab
					this.generateSaaSAdditionalPartPricingTab();
					
					if(quote.isSubmittedQuoteFlag() && quote.isFCTTOPA()) {
						//0-show, 1-hidden, 2-enhance hidden (cannot find in workbook, but program can find it .)
						workbook.setSheetHidden(3, 2);
						workbook.setSheetHidden(4, 2);
						this.hiddenSaasIntroInNotesTab();
					}
				}
				//Spreedsheet - Notes tab
	            this.generateEQNotesTab(quote);
            }else{
		        //Sheet2 - Submitted Spreedsheet Exported Parts and Pricing Tab
	            this.generateSubmittedEQExportedPartPricingTab(quote);
	            
	            this.generateSubmittedSaaSExportedPartPricingTab(quote);
            }
            
            workbook.write(resultStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new QuoteException("Error when write the workbook to stream");
        } catch (Exception e){
            e.printStackTrace();
            throw new QuoteException("Error when processing the workbook");
        }
    }
    
	/**
	 * Update the pricing support data for each line item in the hidden sheet EQ Supporting Data 
	 */
    private void updatePricingSupportData(SpreadSheetQuote quote) throws Exception{
        HSSFName namePricingSupportData = workbook.getNameAt(workbook.getNameIndex("pricingSupportData"));
        AreaReference referencePricingSupportData = new AreaReference(namePricingSupportData.getReference());
        HSSFSheet sheetEQSupportingData = workbook.getSheet(referencePricingSupportData.getFirstCell().getSheetName());
        int rowIndex = referencePricingSupportData.getFirstCell().getRow();
        int firstRowIndex = rowIndex;
        int lastRowIndex = rowIndex;

        for(Iterator it = quote.getPricingList().iterator(); it.hasNext();){
            SpreadsheetPricing pricing = (SpreadsheetPricing) it.next();
            
            sheetEQSupportingData.shiftRows(rowIndex, sheetEQSupportingData.getLastRowNum(), 1);
            HSSFRow newRow = sheetEQSupportingData.getRow(rowIndex);
            HSSFRow shiftedRow = sheetEQSupportingData.getRow(rowIndex + 1);
            util.copyRowStyle(sheetEQSupportingData, shiftedRow, newRow);
            
            newRow.getCell(adapter.PRICING_SUPPORT_COL_IND_PART_NUMBER).setCellValue(new HSSFRichTextString(pricing.getEpPartNumber()));
            newRow.getCell(adapter.PRICING_SUPPORT_COL_IND_REVENUE_STREAM).setCellValue(new HSSFRichTextString(pricing.getRevenueStream()));
            if(StringUtils.isNotEmpty(pricing.getSvpLevelA())){
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_A).setCellValue(Double.parseDouble(pricing.getSvpLevelA()));
            }else{
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_A).setCellFormula("\"\"");
            }
            if(StringUtils.isNotEmpty(pricing.getSvpLevelB())){
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_B).setCellValue(Double.parseDouble(pricing.getSvpLevelB()));
            }else{
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_B).setCellFormula("\"\"");
            }
            if(StringUtils.isNotEmpty(pricing.getSvpLevelD())){
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_D).setCellValue(Double.parseDouble(pricing.getSvpLevelD()));
            }else{
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_D).setCellFormula("\"\"");
            }
            if(StringUtils.isNotEmpty(pricing.getSvpLevelE())){
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_E).setCellValue(Double.parseDouble(pricing.getSvpLevelE()));
            }else{
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_E).setCellFormula("\"\"");
            }
            if(StringUtils.isNotEmpty(pricing.getSvpLevelF())){
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_F).setCellValue(Double.parseDouble(pricing.getSvpLevelF()));
            }else{
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_F).setCellFormula("\"\"");
            }
            if(StringUtils.isNotEmpty(pricing.getSvpLevelG())){
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_G).setCellValue(Double.parseDouble(pricing.getSvpLevelG()));
            }else{
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_G).setCellFormula("\"\"");
            }
            if(StringUtils.isNotEmpty(pricing.getSvpLevelH())){
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_H).setCellValue(Double.parseDouble(pricing.getSvpLevelH()));
            }else{
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_H).setCellFormula("\"\"");
            }
            if(StringUtils.isNotEmpty(pricing.getSvpLevelI())){
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_I).setCellValue(Double.parseDouble(pricing.getSvpLevelI()));
            }else{
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_I).setCellFormula("\"\"");
            }
            if(StringUtils.isNotEmpty(pricing.getSvpLevelJ())){
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_J).setCellValue(Double.parseDouble(pricing.getSvpLevelJ()));
            }else{
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_J).setCellFormula("\"\"");
            }
            if(StringUtils.isNotEmpty(pricing.getSvpLevelGV())){
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_GV).setCellValue(Double.parseDouble(pricing.getSvpLevelGV()));
            }else{
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_GV).setCellFormula("\"\"");
            }
            if(StringUtils.isNotEmpty(pricing.getSvpLevelED())){
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_ED).setCellValue(Double.parseDouble(pricing.getSvpLevelED()));
            }else{
            	newRow.getCell(adapter.PRICING_SUPPORT_COL_ED).setCellFormula("\"\"");
            }
            newRow.getCell(adapter.PRICING_SUPPORT_COL_NA).setCellValue(new HSSFRichTextString("N/A"));
            lastRowIndex = rowIndex;
            rowIndex++;
        }
        sheetEQSupportingData.shiftRows(rowIndex + 1, sheetEQSupportingData.getLastRowNum(), -1);
        //Update name reference to cover the pricing support for all line items
		namePricingSupportData.setReference("'" + namePricingSupportData.getSheetName() + "'!$" + adapter.PRICING_SUPPORT_FIRST_COL + "$" + Integer.toString(firstRowIndex + 1) + ":$" + adapter.PRICING_SUPPORT_LAST_COL + "$" + Integer.toString(lastRowIndex + 1));
    }
	
	private void generateEQCustomerTab(SpreadSheetQuote quote) throws QuoteException{
	    try {
	        HSSFSheet sheetEQCustomer = util.getSheetByNameName("quoteTypeLabel");
	        
	        //Quote Summary
            util.setCellTextValue("quoteType", quote.getLobCode());
            
            if(quote.isFCTTOPA()){
            	util.setCellTextValue("quoteTypeDescription", quote.getProgMigrationDscr());
            	if(!quote.isSubmittedQuoteFlag()){
            		util.setCellTextValue("progMigrationCode", quote.getProgMigrtnCode());
            		util.setCellTextValue("acquisitionCode", quote.getAcquisition());
            	}
            }else{
            	util.setCellTextValue("quoteTypeDescription", quote.getLobCodeDesc());
            }
            
            util.setCellTextValue("countryCode", quote.getCntryCode());
            util.setCellTextValue("countryCodeDescription", quote.getCntryCodeDesc());
            util.setCellTextValue("webQuoteNumber", quote.getWebQuoteNum());

            if(quote.isSubmittedQuoteFlag()){
                //NativeExcelAdapterForSQ adapterSQ = (NativeExcelAdapterForSQ)adapter;
                
                if(quote.isPGSFlag()){
                	util.setCellTextValue("viewQuoteNote", "View quote detail in Partner Guided Selling, copy the following URL and paste it in your browser");
                }else{
                	util.setCellTextValue("viewQuoteNote", "View quote detail in SQO, copy the following URL and paste it in your browser");
                }
                
                //Quote Summary
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_OPPORTUNITY_NUMBER, quote.getOpprtntyInfo());
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_FULFILLMENT_SOURCE, quote.getFulfillmentSrc());
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_QUOTE_NUMBER, quote.getSapQuoteNum());
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_SAP_CONFIRMATION_NUMBER, quote.getSapIDocNum());
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_OVERALL_STATUS, quote.getOverallStatus());
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_QUOTE_START_DATE, "".equals(quote.getQuoteStartDate())?"N/A":quote.getQuoteStartDate());
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_QUOTE_EXPIRATION_DATE, "".equals(quote.getQuoteExpDate())?"N/A":quote.getQuoteExpDate());
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_SB_APPROVED_DATE, "".equals(quote.getSbApprovedDate())?"N/A":quote.getSbApprovedDate());
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_QUOTE_CONTACT, quote.getQuoteOppOwnerEmail());
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_QUOTE_DETAIL_URL, quote.getViewSubmittedQuoteDetailUrl());
                //Customer info
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_SITE_NUMBER, quote.getSiteNum());
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_IBM_CUSTOMER_NUMBER, quote.getCustomerNum());
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_CUSTOMER_NAME, quote.getCustName());
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_CUSTOMER_ADDRESS1, quote.getStreetAddress());
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_CUSTOMER_ADDRESS2, quote.getCityAddress());
                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_CUSTOMER_ADDRESS3, quote.getCntryAddress());
                
                boolean agreementFlag = !"".equals(quote.getSapCtrctNum());
                boolean resellerFlag = !"".equals(quote.getResellerCustNum());
                boolean distributerFlag =!"".equals(quote.getDistributorCustNum());
                
                //Agreement info
                if(agreementFlag){
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_AGREEMENT_NUMBER, quote.getSapCtrctNum());
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_ANNIVERSARY_NUMBER, quote.getAnniversary());
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_RELATIONSHIP_SVP_LEVEL, quote.getRelationSVPLevel());
                }
                
                // Reseller info
                if(resellerFlag){
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_RESELLER_CUST_NUM, quote.getResellerCustNum());
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_RESELLER_ICN, quote.getResellerIBMCustNum());
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_RESELLER_CUST_NAME, quote.getResellerCustName());
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_RESELLER_ADDRESS1, quote.getResellerStreetAddr());
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_RESELLER_ADDRESS2, quote.getResellerCityAddr());
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_RESELLER_ADDRESS3, quote.getResellerCntryAddr());
                }
                
                //Distributer info
                if(distributerFlag){
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_DISTRIBUTER_CUST_NUM, quote.getDistributorCustNum());
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_DISTRIBUTER_ICN, quote.getDistributorIBMCustNum());
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_DISTRIBUTER_CUST_NAME, quote.getDistributorCustName());
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_DISTRIBUTER_ADDRESS1, quote.getDistributorStreetAddr());
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_DISTRIBUTER_ADDRESS2, quote.getDistributorCityAddr());
	                util.setCellTextValueByAbsolutePosition(sheetEQCustomer, adapter.SQ_POSITION_DISTRIBUTER_ADDRESS3, quote.getDistributorCntryAddr());
                }
                
                int iAgreementInfoStartRow = new CellReference(adapter.SQ_POSITION_AGREEMENT_NUMBER).getRow() - 2;
                int iAgreementInfoEndRow = new CellReference(adapter.SQ_POSITION_RELATIONSHIP_SVP_LEVEL).getRow() + 1;
                int iResellerInfoStartRow = new CellReference(adapter.SQ_POSITION_RESELLER_CUST_NUM).getRow() - 2;
                int iResellerInfoEndRow = new CellReference(adapter.SQ_POSITION_RESELLER_ADDRESS3).getRow() + 1;
                int iDistributerInfoStartRow = new CellReference(adapter.SQ_POSITION_DISTRIBUTER_CUST_NUM).getRow() - 2;
                int iDistributerInfoEndRow = new CellReference(adapter.SQ_POSITION_DISTRIBUTER_ADDRESS3).getRow() + 1;
                
                if(agreementFlag==false){
                    for(int i = iAgreementInfoStartRow; i<=iAgreementInfoEndRow; i++){
                        sheetEQCustomer.getRow(i).setZeroHeight(true);
                    }
                }
                if(distributerFlag==false){
                    for(int i = iDistributerInfoStartRow; i<=iDistributerInfoEndRow; i++){
                        sheetEQCustomer.getRow(i).setZeroHeight(true);
                    }
                }
                if(resellerFlag==false){
                    for(int i = iResellerInfoStartRow; i<=iResellerInfoEndRow; i++){
                        sheetEQCustomer.getRow(i).setZeroHeight(true);
                    }
                }
                //protect sheet with password
                if(quote.isProtected()){
                    sheetEQCustomer.protectSheet("dsw-sqo");
                }else{
                    //this deprecated method can work
                    sheetEQCustomer.setProtect(false);
                }
            }else{	        
                //Quote Summary
	            util.setCellTextValue("modDate", quote.getModDate());
	            //Customer info
	            util.setCellTextValue("siteNumber", quote.getSiteNum());
	            util.setCellTextValue("originalSiteNumber", quote.getSiteNum());
	            util.setCellTextValue("IBMCustomerNumber", quote.getCustomerNum());
	            util.setCellTextValue("customerName", quote.getCustName());
	            util.setCellTextValue("customerAddress1", quote.getStreetAddress());
	            util.setCellTextValue("addressStateCityZip", quote.getCityAddress());
	            util.setCellTextValue("addressCountry", quote.getCntryAddress());
	            if(quote.isFCT()){
	                util.setCellTextValue("acquisitionCode", quote.getAcquisition());
		            util.setCellTextValue("acquisitionCodeDescription", quote.getAcquisitionDesc()); 
	            }
	            //Agreement info
	            if(quote.isPA()){
	                util.setCellTextValue("agreementNumber", quote.getSapCtrctNum());
	                util.setCellTextValue("anniversary", quote.getAnniversary());
	                util.setCellTextValue("relationshipSVPLevel", quote.getRelationSVPLevel());
	                util.setCellTextValue("nimOffergCode", quote.getNimOffergCode());
	                util.setCellTextValue("originalRelationshipSVPLevel", quote.getRelationSVPLevel());
	            }else if(quote.isOEM() || quote.isFCT()){
	                util.setCellTextValue("agreementNumber", quote.getSapCtrctNum());
	            }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new QuoteException("Error when processing the Spreedsheet Customer tab");
        }
	}
	
	private void generateEQExportedPartPricingTab(SpreadSheetQuote quote) throws QuoteException{
	    try {
            HSSFSheet sheetEQExportedPartsAndPricing = util.getSheetByNameName("epPartNumberLabel");

	        //Currency
	        util.setCellTextValue("currency", quote.getCurrency());
	        //pricing date
	        util.setCellTextValue("pricingDate", quote.getPricingDate());
	        if(!(quote.getQuoteSVPLevel()==null||"".equals(quote.getQuoteSVPLevel().trim()))){
	            util.setCellTextValue("overrideQuoteSVPLevel", quote.getQuoteSVPLevel());
	        }
	        if(quote.isPA()){
	            util.setCellTextValue("initUseCalSVPLevel", quote.getInitSVPLevel());
	        }
	        
	        int rowIndex = util.getFirstPartRow("epPartNumberLabel").getRowNum();
	        int rowIndexOfFirstPart = rowIndex;
	        int rowIndexOfLastPart = rowIndex;
	        

	        short iRowHeight = util.getFirstPartRow("epPartNumberLabel").getHeight();
	        short iNormalHeight = sheetEQExportedPartsAndPricing.getRow(util.getFirstPartRow("epPartNumberLabel").getRowNum()+1).getHeight();
            
            //For Channel Margin Quote
            if(quote.isChannelMarginQuote()&&!quote.isFCT()){
                sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_BP_DISCOUNT,false);
                sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_BP_OVERRIDE_DISCOUNT,false);
                sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA,false);
            }
            
            String previousPartNum = "";
            HSSFCell overridePriceDecimal4FormatCell = sheetEQExportedPartsAndPricing.getRow(adapter.PART_ROW_IND_OVERRIDE_PRICE_DECIMAL_4)
					.getCell(adapter.PART_COL_IND_OVERRIDE_PRICE_DECIMAL_4);
			HSSFCell bidUnitPriceDecimal4FormatCell = null;

            // creating the part records one by one...
	        for(Iterator it = quote.getEqPartList().iterator(); it.hasNext();){
	            SpreadSheetPart part = (SpreadSheetPart) it.next();            
	            
	            HSSFRow row = sheetEQExportedPartsAndPricing.getRow(rowIndex);
	            String standard_start_date_formula = row.getCell(adapter.PART_COL_IND_STANDARD_START_DATE_FORMULA).getCellFormula();
	            // replaceAll methods to solve the unshifted formulas specifically.
	            standard_start_date_formula = standard_start_date_formula.replaceAll(new CellReference(rowIndexOfFirstPart-1, adapter.PART_COL_IND_ADDITIONAL_YEAR_COVERAGE_SEQ_NUM).formatAsString(), new CellReference(rowIndex-1, adapter.PART_COL_IND_ADDITIONAL_YEAR_COVERAGE_SEQ_NUM).formatAsString())
	                    												 .replaceAll(new CellReference(rowIndexOfFirstPart-2, adapter.PART_COL_IND_ADDITIONAL_YEAR_COVERAGE_SEQ_NUM).formatAsString(), new CellReference(rowIndex-2, adapter.PART_COL_IND_ADDITIONAL_YEAR_COVERAGE_SEQ_NUM).formatAsString())
	                    												 .replaceAll(new CellReference(rowIndexOfFirstPart-1, adapter.PART_COL_IND_OVERRIDE_END_DATE).formatAsString(), new CellReference(rowIndex-1, adapter.PART_COL_IND_OVERRIDE_END_DATE).formatAsString())
	                    												 .replaceAll(new CellReference(rowIndexOfFirstPart-1, adapter.PART_COL_IND_STANDARD_END_DATE_FORMULA).formatAsString(), new CellReference(rowIndex-1, adapter.PART_COL_IND_STANDARD_END_DATE_FORMULA).formatAsString());
	            
	            String standard_end_date_formula = row.getCell(adapter.PART_COL_IND_STANDARD_END_DATE_FORMULA).getCellFormula();

	            String bid_unit_price_formula = row.getCell(adapter.PART_COL_IND_BID_UNIT_PRICE_FORMULA).getCellFormula();
	            String entitled_extended_price_formula = row.getCell(adapter.PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA).getCellFormula();
	            String bid_extended_price_formula = row.getCell(adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA).getCellFormula();
	            String x_formula = row.getCell(adapter.PART_COL_IND_X_FORMULA).getCellFormula();
	            String changed_flag_formula = row.getCell(adapter.PART_COL_IND_CHANGED_FLAG_FORMULA).getCellFormula();
	            String entitled_unit_price_formula = null;
	            String total_points_formula = null;
	            if(quote.isPA()){
	                entitled_unit_price_formula = row.getCell(adapter.PART_COL_IND_ENTITLED_UNIT_PRICE_FORMULA).getCellFormula();
	                total_points_formula = row.getCell(adapter.PART_COL_IND_TOTAL_POINTS_FORMULA).getCellFormula();
	            }
	            
	            String bp_extended_price_formula = null;
	            if(quote.isChannelMarginQuote()&&!quote.isFCT()){
	                bp_extended_price_formula = row.getCell(adapter.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA).getCellFormula();
	            }
	            bidUnitPriceDecimal4FormatCell = row.getCell(adapter.PART_COL_IND_BID_UNIT_PRICE_DECIMAL_4);
	            String bid_unit_price_formula2 = null;
	            if(quote.isPA() || quote.isPAE() || quote.isFCT()){
	            	bid_unit_price_formula2 = bidUnitPriceDecimal4FormatCell.getCellFormula();
	            }

	            sheetEQExportedPartsAndPricing.shiftRows(rowIndex, sheetEQExportedPartsAndPricing.getLastRowNum(), 1);
	            HSSFRow newRow = sheetEQExportedPartsAndPricing.createRow(rowIndex);
	            HSSFRow shiftedRow = sheetEQExportedPartsAndPricing.getRow(rowIndex + 1);
	            util.copyRowStyle(sheetEQExportedPartsAndPricing, shiftedRow, newRow);

                newRow.getCell(adapter.PART_COL_IND_PART_NUMBER).setCellValue(new HSSFRichTextString(part.getEpPartNumber()));
                newRow.getCell(adapter.PART_COL_IND_PART_DESCRIPTION).setCellValue(new HSSFRichTextString(part.getEpPartDesc()));
                if(!quote.isFCT()){
                    newRow.getCell(adapter.PART_COL_IND_RESELLER_AUTHORIZATION).setCellValue(new HSSFRichTextString(StringUtils.trimToEmpty(part.getControlledCodeDesc())));
                    newRow.getCell(adapter.PART_COL_IND_RESELLER_AUTHORIZATION_TERMS).setCellValue(new HSSFRichTextString(StringUtils.isEmpty(part.getProgramDesc())?"SW Value Plus Open":StringUtils.trimToEmpty(part.getProgramDesc())));
                }
                if(StringUtils.isBlank(part.getEpQuantity())){
                	newRow.getCell(adapter.PART_COL_IND_QUANTITY).setCellType(HSSFCell.CELL_TYPE_BLANK);
                }else{
                	try {
						newRow.getCell(adapter.PART_COL_IND_QUANTITY).setCellValue(Integer.parseInt(part.getEpQuantity()));
					} catch (NumberFormatException e) {
						newRow.getCell(adapter.PART_COL_IND_QUANTITY).setCellValue(part.getEpQuantity());
					}
                }
                String epAddYears = part.getEpAddYears();
                int addYears = StringUtils.isEmpty(epAddYears)? 0 : Integer.parseInt(epAddYears);
                newRow.getCell(adapter.PART_COL_IND_ADD_YEARS).setCellValue(addYears);
                
                if(StringUtils.isNotEmpty(part.getEpOverrideDatesStartDate())){
                    newRow.getCell(adapter.PART_COL_IND_OVERRIDE_START_DATE).setCellValue(simpleDateFormat.parse(part.getEpOverrideDatesStartDate()));
                }                
                if(StringUtils.isNotEmpty(part.getEpOverideDatesEndDate())){
                    newRow.getCell(adapter.PART_COL_IND_OVERRIDE_END_DATE).setCellValue(simpleDateFormat.parse(part.getEpOverideDatesEndDate()));
                }
                
                if(!quote.isFCT()){
                    newRow.getCell(adapter.PART_COL_IND_RENEWAL_QUOTE_NUMBER).setCellValue(new HSSFRichTextString(part.getRenewalQuoteNumber()));
                }
                
                
                if(!("".equals(part.getEpEnterOnlyOneOverridePrice()))){
                    newRow.getCell(adapter.PART_COL_IND_OVERRIDE_PRICE).setCellValue(Double.parseDouble(part.getEpEnterOnlyOneOverridePrice()));
                    newRow.getCell(adapter.PART_COL_IND_HIDDEN_OVERRIDE_PRICE).setCellValue(Double.parseDouble(part.getEpEnterOnlyOneOverridePrice()));
                }
                if(part.getEpEnterOnlyOneDiscountPercent()!=0.0d){
                    newRow.getCell(adapter.PART_COL_IND_DISCOUNT_PERCENT).setCellValue(part.getEpEnterOnlyOneDiscountPercent());
                    newRow.getCell(adapter.PART_COL_IND_HIDDEN_DISCOUNT_PERCENT).setCellValue(part.getEpEnterOnlyOneDiscountPercent());
                }
                          
                newRow.getCell(adapter.PART_COL_IND_HIDDEN_TOTAL_PRICE).setCellValue(part.getTotalPrice());
                
                if(StringUtils.isBlank(part.getEpQuantity())){
                	newRow.getCell(adapter.PART_COL_IND_HIDDEN_QUANTITY).setCellType(HSSFCell.CELL_TYPE_BLANK);
                }else{
                	try {
						newRow.getCell(adapter.PART_COL_IND_HIDDEN_QUANTITY).setCellValue(Integer.parseInt(part.getEpQuantity()));
					} catch (NumberFormatException e) {
						newRow.getCell(adapter.PART_COL_IND_HIDDEN_QUANTITY).setCellValue(part.getEpQuantity());
					}
                }

                if(StringUtils.isNotEmpty(part.getEpOverrideDatesStartDate())){
                    newRow.getCell(adapter.PART_COL_IND_HIDDEN_OVERRIDE_START_DATE).setCellValue(simpleDateFormat.parse(part.getEpOverrideDatesStartDate()));
                }                
                if(StringUtils.isNotEmpty(part.getEpOverideDatesEndDate())){
                    newRow.getCell(adapter.PART_COL_IND_HIDDEN_OVERRIDE_END_DATE).setCellValue(simpleDateFormat.parse(part.getEpOverideDatesEndDate()));
                }
                
                if(StringUtils.isNotEmpty(part.getEpSTDStartDate())){
                    newRow.getCell(adapter.PART_COL_IND_HIDDEN_STANDARD_START_DATE).setCellValue(simpleDateFormat.parse(part.getEpSTDStartDate()));
                }else{
                    newRow.getCell(adapter.PART_COL_IND_HIDDEN_STANDARD_START_DATE).setCellFormula("\"\"");
                }
                
                newRow.getCell(adapter.PART_COL_IND_HIDDEN_BID_UNIT_PRICE).setCellValue(part.getBidUnitPrice());
                newRow.getCell(adapter.PART_COL_IND_HIDDEN_ENTITLED_EXT_PRICE).setCellValue(part.getEntitledExtPrice());
                if(quote.isPA() || quote.isPAE() || quote.isOEM()){
                	if(quote.isPA()){
                		newRow.getCell(adapter.PART_COL_IND_HIDDEN_ENTITLED_UNIT_PRICE).setCellValue(part.getEpItemPriceDouble());
                	}
                    newRow.getCell(adapter.PART_COL_IND_HIDDEN_BP_EXTENDED_PRICE).setCellValue(part.getChannelExtPrice());
                    newRow.getCell(adapter.PART_COL_IND_HIDDEN_BP_OVERRIDE_DISCOUNT).setCellValue(part.getEpBPOvrrdDiscPct());
                }
                
                newRow.getCell(adapter.PART_COL_IND_RENEWAL_QUOTE_LINE_ITEM_NUMBER).setCellValue(new HSSFRichTextString(part.getRenewalLineItemSeqNum()));
                newRow.getCell(adapter.PART_COL_IND_SORT_SEQ_NUM).setCellValue(part.getEpSortSeqNum());
                newRow.getCell(adapter.PART_COL_IND_ADDITIONAL_YEAR_COVERAGE_SEQ_NUM).setCellValue(part.getEpAddtnlYearCvrageSeqNum());
                
	            
	            
	            if(StringUtils.isBlank(part.getRenewalQuoteNumber())){
	            	newRow.getCell(adapter.PART_COL_IND_STANDARD_START_DATE_FORMULA).setCellFormula(standard_start_date_formula);
	            	if(quote.isPA()){
	    	            newRow.getCell(adapter.PART_COL_IND_STANDARD_END_DATE_FORMULA).setCellFormula(
	    	                    standard_end_date_formula.replaceAll("\\(anniversary", ("(" + util.replaceNameWithPostionString("anniversary")))
	    	                                             .replaceAll("anniversaryMonthNumber", util.replaceNameWithPostionString("anniversaryMonthNumber"))
	    	                                             .replaceAll("pricingSupportData", util.replaceNameWithPostionString("pricingSupportData"))
	    	                                             );
	    	            }else{
	    	                newRow.getCell(adapter.PART_COL_IND_STANDARD_END_DATE_FORMULA).setCellFormula(standard_end_date_formula);
	    	            }
	            }else{
	            	newRow.getCell(adapter.PART_COL_IND_STANDARD_START_DATE_FORMULA).setCellType(HSSFCell.CELL_TYPE_BLANK);
	            	newRow.getCell(adapter.PART_COL_IND_STANDARD_END_DATE_FORMULA).setCellType(HSSFCell.CELL_TYPE_BLANK);
	            	if(StringUtils.isNotBlank(part.getEpSTDStartDate())){
	            		newRow.getCell(adapter.PART_COL_IND_STANDARD_START_DATE_FORMULA).setCellValue(simpleDateFormat.parse(part.getEpSTDStartDate()));
	            	}
	            	if(StringUtils.isNotBlank(part.getEpSTDEndDate())){
	            		newRow.getCell(adapter.PART_COL_IND_STANDARD_END_DATE_FORMULA).setCellValue(simpleDateFormat.parse(part.getEpSTDEndDate()));
	            	}
	            }
	            
	            if(quote.isPA()){
	            	if(part.isObsoletePart()){
	            		newRow.getCell(adapter.PART_COL_IND_ENTITLED_UNIT_PRICE_FORMULA).setCellFormula(part.getEpItemPrice().replaceAll(",", ""));
	            	}else{
	            		newRow.getCell(adapter.PART_COL_IND_ENTITLED_UNIT_PRICE_FORMULA).setCellFormula(entitled_unit_price_formula);
	            	}
	                
	                newRow.getCell(adapter.PART_COL_IND_PRORATE).setCellValue(new HSSFRichTextString(part.getEpMaintenanceProrated()));
	            }else if(quote.isPAE()||quote.isOEM()||quote.isFCT()){
	                newRow.getCell(adapter.PART_COL_IND_ENTITLED_UNIT_PRICE_FORMULA).setCellValue(new HSSFRichTextString(part.getEpItemPrice()));
	                util.setCellBorderGrey(newRow.getCell(adapter.PART_COL_IND_ENTITLED_UNIT_PRICE_FORMULA), false, 0, 3);
	            }
	            
	            if(quote.isPA()) {
	                if(part.isNoQty()){
	                	newRow.getCell(adapter.PART_COL_IND_TOTAL_POINTS_FORMULA).setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	                	newRow.getCell(adapter.PART_COL_IND_TOTAL_POINTS_FORMULA).setCellValue(part.getEpItemPoints());
	                }else{
	                	newRow.getCell(adapter.PART_COL_IND_TOTAL_POINTS_FORMULA).setCellFormula(total_points_formula);
	                }
	                newRow.getCell(adapter.PART_COL_IND_ITEM_POINTS).setCellValue(part.getEpItemPoints());
	            }
	            
	            newRow.getCell(adapter.PART_COL_IND_BID_UNIT_PRICE_FORMULA).setCellFormula(bid_unit_price_formula);
	            
	            newRow.getCell(adapter.PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA).setCellFormula(entitled_extended_price_formula);
	            newRow.getCell(adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA).setCellFormula(bid_extended_price_formula);
	            
	            
	            newRow.getCell(adapter.PART_COL_IND_X_FORMULA).setCellFormula(x_formula);
	            newRow.getCell(adapter.PART_COL_IND_CHANGED_FLAG_FORMULA).setCellFormula(changed_flag_formula);
	            
	            if(quote.isChannelMarginQuote()&&!quote.isFCT()){
                    if(part.getEpBPStdDiscPct()!=0.0d){
                        newRow.getCell(adapter.PART_COL_IND_BP_DISCOUNT).setCellValue(part.getEpBPStdDiscPct());
                    }
                    if(part.isBPDiscOvrrdFlag()){
                    	newRow.getCell(adapter.PART_COL_IND_BP_OVERRIDE_DISCOUNT).setCellValue(part.getEpBPOvrrdDiscPct());
                    }
	                newRow.getCell(adapter.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA).setCellFormula(bp_extended_price_formula);
	            }else if(!quote.isFCT()){
	                newRow.getCell(adapter.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA).setCellFormula("\"\"");
	            }
	            
	            newRow.setHeight(iRowHeight);

	            if("0".equals(part.getEpAddYears())&&part.getEpAddtnlYearCvrageSeqNum()!=0){
	                // Set the cells on the repeated part record in the spreadsheet to be read-only
	                util.setCellReadOnly(newRow.getCell(adapter.PART_COL_IND_QUANTITY));
	                util.setCellReadOnly(newRow.getCell(adapter.PART_COL_IND_OVERRIDE_START_DATE));
	                util.setCellReadOnly(newRow.getCell(adapter.PART_COL_IND_OVERRIDE_END_DATE));
	                util.setCellReadOnly(newRow.getCell(adapter.PART_COL_IND_OVERRIDE_PRICE));
	                util.setCellReadOnly(newRow.getCell(adapter.PART_COL_IND_DISCOUNT_PERCENT));
	            }
	            
	            // 3. Override End Date
	            String refOverrideStateDate = "$" + util.convertNumToColString(adapter.PART_COL_IND_OVERRIDE_START_DATE) + "$" + (rowIndex+1);
	            DVConstraint constraintOverrideEndDate = DVConstraint.createDateConstraint(DVConstraint.OperatorType.GREATER_OR_EQUAL,
	                    "=DATE(YEAR("+refOverrideStateDate+"),MONTH("+refOverrideStateDate+")+1,DAY("+refOverrideStateDate+")-1)", null, "dd-MMM-yyyy");
	            CellRangeAddressList regionsOverrideEndDate = new CellRangeAddressList(rowIndex, rowIndex,adapter.PART_COL_IND_OVERRIDE_END_DATE, adapter.PART_COL_IND_OVERRIDE_END_DATE);
	            HSSFDataValidation validationOverrideEndDate = new HSSFDataValidation(regionsOverrideEndDate,constraintOverrideEndDate);
	            validationOverrideEndDate.createErrorBox("Incorrect Override Date", "Override End Date must be one month greater than the Override Start Date");
	            sheetEQExportedPartsAndPricing.addValidationData(validationOverrideEndDate);

	            //release 14.1 : output Equity Curve data of the part
	            
	            if((quote.isPA() || quote.isPAE()) && !quote.isFCTTOPA())
	            	outputEquityCurveRowData(newRow, part);
	            
	            rowIndexOfLastPart = rowIndex;
	            rowIndex++;
	            previousPartNum = part.getEpPartNumber();
	            if(bid_unit_price_formula2 != null){
		            newRow.getCell(adapter.PART_COL_IND_BID_UNIT_PRICE_DECIMAL_4).setCellFormula(bid_unit_price_formula2);
		            if(part.isDecimal4Flag()){
			            newRow.getCell(adapter.PART_COL_IND_OVERRIDE_PRICE).setCellStyle(overridePriceDecimal4FormatCell.getCellStyle());
			            newRow.getCell(adapter.PART_COL_IND_BID_UNIT_PRICE_FORMULA).setCellStyle(bidUnitPriceDecimal4FormatCell.getCellStyle());
						newRow.getCell(adapter.PART_COL_IND_BID_UNIT_PRICE_FORMULA).setCellFormula(bid_unit_price_formula2);
		            }
	            }
	            if (part.isUsagePart()) {
					newRow.getCell(adapter.PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA).setCellType(HSSFCell.CELL_TYPE_BLANK);
					newRow.getCell(adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA).setCellType(HSSFCell.CELL_TYPE_BLANK);
					if (quote.isChannelMarginQuote() && !quote.isFCT()) {
						newRow.getCell(adapter.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA).setCellType(HSSFCell.CELL_TYPE_BLANK);
					}
				}
	            
	            
	        }//end for
        
	        boolean emptyList = (rowIndex==rowIndexOfFirstPart&&rowIndexOfFirstPart==rowIndexOfLastPart);
	        // delete the extra row.
	        sheetEQExportedPartsAndPricing.shiftRows(rowIndex + 1, sheetEQExportedPartsAndPricing.getLastRowNum(), -1);
	        if(emptyList){
	            util.getFirstPartRow("epPartNumberLabel").setHeight(iNormalHeight);
	            rowIndexOfFirstPart--;
	            rowIndexOfLastPart--;
	        }
			
	        
	        // rowIndex --> Row: Equity Curve discount guidance - Total:
			// total values of Equity Curve . Since release 14.1  add by li rui
			// Equity Curve discount guidance - Total
	        if((quote.isPA() || quote.isPAE()) && !quote.isFCTTOPA()){
	        	outputEquityCurveTotalData(
	        			sheetEQExportedPartsAndPricing.getRow(rowIndexOfLastPart + 1),
	        			quote);
	        	rowIndex++;  //// rowIndex --> Row: Subtotal - exported Software Parts
	        }
			//Subtotal - exported Software Parts
	        // update the position for these names	   
			if(quote.isPA()){
			    // epSubtotalPoints
			    HSSFName nameOfepSubtotalPoints = workbook.getNameAt(workbook.getNameIndex("epSubtotalPoints"));
			    nameOfepSubtotalPoints.setReference(
			            new CellReference(workbook.getSheetName(workbook.getSheetIndex(sheetEQExportedPartsAndPricing)),
			                    adapter.getSubtotalRowIndex(rowIndexOfLastPart)/*rowIndex*/,
			                    adapter.PART_COL_IND_TOTAL_POINTS_FORMULA,
			                    true, true).formatAsString());
				// epTotalPoints
			    HSSFName nameOfepTotalPoints = workbook.getNameAt(workbook.getNameIndex("epTotalPoints"));
			    nameOfepTotalPoints.setReference(
			            new CellReference(workbook.getSheetName(workbook.getSheetIndex(sheetEQExportedPartsAndPricing)),
			                    adapter.getTotalRowIndex(rowIndexOfLastPart)/*rowIndex + 2*/,
			                    adapter.PART_COL_IND_TOTAL_POINTS_FORMULA,
			                    true, true).formatAsString());
			}
	        //epSubtotalPrice
	        HSSFName nameOfepSubtotalPrice = workbook.getNameAt(workbook.getNameIndex("epSubtotalPrice"));
			nameOfepSubtotalPrice.setReference(
			        new CellReference(workbook.getSheetName(workbook.getSheetIndex(sheetEQExportedPartsAndPricing)),
			        		 										adapter.getSubtotalRowIndex(rowIndexOfLastPart)/*rowIndex*/,
			                										adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA,
			                										true, true).formatAsString());
	        // epTotalPrice
	        HSSFName nameOfepTotalPrice = workbook.getNameAt(workbook.getNameIndex("epTotalPrice"));
	        nameOfepTotalPrice.setReference(
			        new CellReference(workbook.getSheetName(workbook.getSheetIndex(sheetEQExportedPartsAndPricing)),
			        		adapter.getTotalRowIndex(rowIndexOfLastPart)/*rowIndex + 2*/,
			                										adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA,
			                										true, true).formatAsString());
	        
	        //Update the cell formula for these named cells	 
	        if(quote.isPA()){
				//Update formula for epSubtotalPoints, e.g. SUM(Q11:Q13)
				HSSFCell cellOfepSubtotalPoints = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_TOTAL_POINTS_FORMULA);
				if(emptyList){
				    cellOfepSubtotalPoints.setCellFormula("\"\"");
				}else{
					String stringFormulaOfepSubtotalPoints = 
					    "SUM(" + new CellReference(rowIndexOfFirstPart,adapter.PART_COL_IND_TOTAL_POINTS_FORMULA).formatAsString()
					    + ":" + new CellReference(rowIndexOfLastPart,adapter.PART_COL_IND_TOTAL_POINTS_FORMULA).formatAsString() + ")";
					cellOfepSubtotalPoints.setCellFormula(stringFormulaOfepSubtotalPoints);
				}
				
				//Update formula for epTotalPoints, e.g. Q14+apSubtotalPoints
				HSSFCell cellOfepTotalPoints = sheetEQExportedPartsAndPricing.getRow(rowIndex + 2).getCell(adapter.PART_COL_IND_TOTAL_POINTS_FORMULA);
				String stringFormulaOfepTotalPoints = 
				    new CellReference(adapter.getSubtotalRowIndex(rowIndexOfLastPart)/*rowIndexOfLastPart + 2*/,
				    		adapter.PART_COL_IND_TOTAL_POINTS_FORMULA).formatAsString()
				    + "+apSubtotalPoints";
				cellOfepTotalPoints.setCellFormula(stringFormulaOfepTotalPoints);
	        }
			//Update formula for epSubtotalPrice, e.g. SUM(T11:T13)
			HSSFCell cellOfepSubtotalPrice = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA);
			if(emptyList){
			    cellOfepSubtotalPrice.setCellFormula("\"\"");
			}else{
				String stringFormulaOfepSubtotalPrice = 
				    "SUM(" + new CellReference(rowIndexOfFirstPart,adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA).formatAsString()
				    + ":" + new CellReference(rowIndexOfLastPart,adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA).formatAsString() + ")";
				cellOfepSubtotalPrice.setCellFormula(stringFormulaOfepSubtotalPrice);
			}
			
			//Update formula for epTotalPrice, e.g. T14+apSubtotalPrice
			HSSFCell cellOfepTotalPrice = sheetEQExportedPartsAndPricing.getRow(adapter.getTotalRowIndex(rowIndexOfLastPart)).getCell(adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA);
			String stringFormulaOfepTotalPrice = 
			    new CellReference(adapter.getSubtotalRowIndex(rowIndexOfLastPart)/*rowIndexOfLastPart + 2*/,adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA).formatAsString() + "+apSubtotalPrice";
			cellOfepTotalPrice.setCellFormula(stringFormulaOfepTotalPrice);
			
			if(quote.isChannelMarginQuote()&&!quote.isFCT() && quote.getEqPartList().size()>0){
			    HSSFCell cellOfepTotalBPExtentedPrice = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA);
				String stringFormulaOfepTotalBPExtentedPrice = 
				    "SUM(" + new CellReference(rowIndexOfFirstPart,adapter.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA).formatAsString()
				    + ":" + new CellReference(rowIndexOfLastPart,adapter.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA).formatAsString() + ")";
				cellOfepTotalBPExtentedPrice.setCellFormula(stringFormulaOfepTotalBPExtentedPrice);
			}
			
	    	//SVP transition cell - replace epTotalPoints with absolute position
	    	if(quote.isPA()){
	            HSSFCell transitCell =  sheetEQExportedPartsAndPricing.getRow(4).getCell(2);
	            transitCell.setCellFormula(transitCell.getCellFormula().replaceAll("epTotalPoints", util.replaceNameWithPostionString("epTotalPoints")));
	        }
			
			if(quote.isPA() || quote.isPAE()){
				//Update header infos that referenced on the changed names
				if(quote.isPA()) {
					//header points
					sheetEQExportedPartsAndPricing.getRow(3).getCell(adapter.PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA)
						.setCellFormula(new CellReference(adapter.getSubtotalRowIndex(rowIndexOfLastPart)/*rowIndexOfLastPart + 2*/,adapter.PART_COL_IND_TOTAL_POINTS_FORMULA).formatAsString());
					sheetEQExportedPartsAndPricing.getRow(4).getCell(adapter.PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA)
						.setCellFormula(new CellReference(adapter.getTotalRowIndex(rowIndexOfLastPart)/*rowIndexOfLastPart + 4*/,adapter.PART_COL_IND_TOTAL_POINTS_FORMULA).formatAsString());
				}
				
				//header price
				sheetEQExportedPartsAndPricing.getRow(3).getCell(adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA)
					.setCellFormula(new CellReference(adapter.getSubtotalRowIndex(rowIndexOfLastPart)/*rowIndexOfLastPart + 2*/,adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA).formatAsString());
				sheetEQExportedPartsAndPricing.getRow(4).getCell(adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA)
					.setCellFormula(new CellReference(adapter.getTotalRowIndex(rowIndexOfLastPart)/*rowIndexOfLastPart + 4*/,adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA).formatAsString());
			}else if(quote.isOEM()||quote.isFCT()){
			    //epTotalPrice
				sheetEQExportedPartsAndPricing.getRow(3).getCell(adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA)
					.setCellFormula(new CellReference(adapter.getTotalRowIndex(rowIndexOfLastPart)/*rowIndexOfLastPart + 3*/,adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA).formatAsString());
				//epSubtotalPrice
				sheetEQExportedPartsAndPricing.getRow(2).getCell(adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA)
					.setCellFormula(new CellReference(adapter.getSubtotalRowIndex(rowIndexOfLastPart)/*rowIndexOfLastPart + 1*/,adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA).formatAsString());
			}
			//Update validations
			//1. Part Quantity greater or equal to 0
			CellRangeAddressList addressList1 = new CellRangeAddressList(rowIndexOfFirstPart, rowIndexOfLastPart, adapter.PART_COL_IND_QUANTITY, adapter.PART_COL_IND_QUANTITY);
			DVConstraint dvConstraint1 = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.INTEGER, DVConstraint.OperatorType.GREATER_OR_EQUAL, "0", "0");
			HSSFDataValidation dataValidation1 = new HSSFDataValidation(addressList1, dvConstraint1);
			dataValidation1.createErrorBox("You must enter a whole number", "");
			sheetEQExportedPartsAndPricing.addValidationData(dataValidation1);
			
			//2. Override Start Date between "2000/01/01", "2099/12/31"
			CellRangeAddressList addressList2 = new CellRangeAddressList(rowIndexOfFirstPart, rowIndexOfLastPart, adapter.PART_COL_IND_OVERRIDE_START_DATE, adapter.PART_COL_IND_OVERRIDE_START_DATE);
			DVConstraint dvConstraint2 = DVConstraint.createDateConstraint(DVConstraint.OperatorType.BETWEEN, "2000/01/01", "2099/12/31", "yyyy/MM/dd");
			HSSFDataValidation dataValidation2 = new HSSFDataValidation(addressList2, dvConstraint2);
			dataValidation2.createErrorBox("Invalid Date", "Date must be greater than 1/1/2000 Date must be less than 12/31/2099");
			sheetEQExportedPartsAndPricing.addValidationData(dataValidation2);	
			
			//3. Override End Date
			
			//4. Override price greater than 0
			CellRangeAddressList addressList4 = new CellRangeAddressList(rowIndexOfFirstPart, rowIndexOfLastPart, adapter.PART_COL_IND_OVERRIDE_PRICE, adapter.PART_COL_IND_OVERRIDE_PRICE);
			DVConstraint dvConstraint4 = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.DECIMAL, DVConstraint.OperatorType.GREATER_THAN, "0", "0");
			HSSFDataValidation dataValidation4 = new HSSFDataValidation(addressList4, dvConstraint4);
			sheetEQExportedPartsAndPricing.addValidationData(dataValidation4);
			
			//5. Discount percent between 0 and 1
			int columnIndex = adapter.PART_COL_IND_DISCOUNT_PERCENT;
			util.setCellDataValidations(sheetEQExportedPartsAndPricing, rowIndexOfFirstPart
					, rowIndexOfLastPart, columnIndex, columnIndex);
			
			//6. Add years maintenance in a cell value list
			CellRangeAddressList addressList6 = new CellRangeAddressList(rowIndexOfFirstPart, rowIndexOfLastPart, adapter.PART_COL_IND_ADD_YEARS, adapter.PART_COL_IND_ADD_YEARS);
			DVConstraint dvConstraint6 = DVConstraint.createExplicitListConstraint(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"});
			HSSFDataValidation dataValidation6 = new HSSFDataValidation(addressList6, dvConstraint6);
			sheetEQExportedPartsAndPricing.addValidationData(dataValidation6);
	        
        } catch (Exception e) {
            e.printStackTrace();
            throw new QuoteException("Error when processing the Spreedsheet SW Exported Part & Pricing tab");
        }
	}
	
	private void generateEQAdditionalPartPricingTab(SpreadSheetQuote quote) throws QuoteException{
	    try {
	        HSSFName nameOfepSubtotalPrice = workbook.getNameAt(workbook.getNameIndex("epSubtotalPrice"));
	  
			//Update formula for apTotalPrice, e.g. $'EQ Exported Parts _ Pricing'.T14)+apSubtotalPrice
	        util.setCellFormulaValue("apTotalPrice", nameOfepSubtotalPrice.getReference() + "+apSubtotalPrice");

	        //Currency
	        util.setCellTextValue("apCurrency", quote.getCurrency());
	        
	        //Pricing date
	        util.setCellTextValue("apPricingDate", quote.getPricingDate());	 

	        
	        if(quote.isPA()){
	            //Update formula for apTotalPoints, e.g. ($'EQ Exported Parts _ Pricing'.Q14)+apSubtotalPoints
	        	HSSFName nameOfepSubtotalPoints = workbook.getNameAt(workbook.getNameIndex("epSubtotalPoints"));
	            util.setCellFormulaValue("apTotalPoints", nameOfepSubtotalPoints.getReference() + "+apSubtotalPoints");
	        }else if(quote.isPAE() || quote.isOEM()||quote.isFCT()){
	            util.setCellFormulaValue("apSubtotalPriceHeader", util.getCellFormulaValue("apSubtotalPrice"));
	            util.setCellFormulaValue("apTotalPriceHeader", workbook.getNameAt(workbook.getNameIndex("epTotalPrice")).getReference());
	        }
        } catch (Exception e) {
            e.printStackTrace();
            throw new QuoteException("Error when processing the Spreedsheet SW Additional Parts & Pricing tab");
        }
	}
	
	private void generateEQSAASExportedPartPricingTab(SpreadSheetQuote quote) throws QuoteException {
		//try {
        try {
        	HSSFSheet sheetEQSaasExportedPartsAndPricing = util.getSheetByNameName("epSaasPartNumberLabel");

        	int rowIndex = util.getFirstPartRow("epSaasPartNumberLabel").getRowNum();
        	int rowIndexOfFirstPart = rowIndex;
        	int rowIndexOfLastPart = rowIndex;
        	
        	short iRowHeight = util.getFirstPartRow("epSaasPartNumberLabel").getHeight();
	        short iNormalHeight = sheetEQSaasExportedPartsAndPricing.getRow(util.getFirstPartRow("epSaasPartNumberLabel").getRowNum()+1).getHeight();
	        
	        //For Channel Margin Quote
	        if(!quote.isChannelMarginQuote() || quote.isFCT()) {
            	sheetEQSaasExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_DISCOUNT,true);
            	sheetEQSaasExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT,true);
            	sheetEQSaasExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_RATE,true);
            	sheetEQSaasExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_TCV,true);
            }
	        if(quote.isPGSFlag()) {
	        	sheetEQSaasExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT,true);
	        }
	        
	        boolean hasTopBorderForOverride = true;
	        boolean hasTopBorderForRate = true;
        	for(Iterator<SpreadSheetPart> it = quote.getSaaSEqPartList().iterator(); it.hasNext();){
        		SpreadSheetPart part = it.next();
        		HSSFRow row = sheetEQSaasExportedPartsAndPricing.getRow(rowIndex);
        		
        		String entitledRateFormula = row.getCell(adapter.SAASPART_COL_IND_ENTITLED_RATE).getCellFormula();
        		String bidRateFormula = row.getCell(adapter.SAASPART_COL_IND_BID_RATE).getCellFormula();
        		String saas_entitled_tcv_formula = row.getCell(adapter.SAASPART_COL_IND_ENTITLED_TCV).getCellFormula();
        		
        		String saas_total_points_formula = null;
        		String saas_isNotChange_formula = null;
        		String saas_bpRate_formula = null;
	            if(quote.isPA()){
	                saas_total_points_formula = row.getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS).getCellFormula();
	            }
	            
	            if(quote.isPA() || quote.isPAE()) {
	            	saas_isNotChange_formula = row.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_NOT_CHANGED).getCellFormula();
	            	saas_bpRate_formula = row.getCell(adapter.SAASPART_COL_IND_BP_RATE).getCellFormula();
	            }
	            
	            String saas_bid_tcv_formula = row.getCell(adapter.SAASPART_COL_IND_BID_TCV).getCellFormula();
	            String saas_bp_tcv_formula = null;
	            if(quote.isChannelMarginQuote() && !quote.isFCT()) {
	            	saas_bp_tcv_formula = row.getCell(adapter.SAASPART_COL_IND_BP_TCV).getCellFormula();
	            }
	            
	            String billing_periods_formula = row.getCell(adapter.SAASPART_COL_IND_HIDDEN_BILLING_FREQUENCY).getCellFormula();
	            
	            String localUnitPriceFormula = row.getCell(adapter.SAASPART_COL_IND_HIDDEN_LOCAL_UNIT_PRICE_FORMULA).getCellFormula();
        		
        		sheetEQSaasExportedPartsAndPricing.shiftRows(rowIndex, sheetEQSaasExportedPartsAndPricing.getLastRowNum(), 1);
	            HSSFRow newRow = sheetEQSaasExportedPartsAndPricing.createRow(rowIndex);
	            HSSFRow shiftedRow = sheetEQSaasExportedPartsAndPricing.getRow(rowIndex + 1);
	            util.copyRowStyle(sheetEQSaasExportedPartsAndPricing, shiftedRow, newRow);
        		
	            newRow.getCell(adapter.SAASPART_COL_IND_PART_NUMBER).setCellValue(new HSSFRichTextString(part.getEpPartNumber()));
	            newRow.getCell(adapter.SAASPART_COL_IND_PART_DESCRIPTION).setCellValue(new HSSFRichTextString(part.getEpPartDesc()));
	            newRow.getCell(adapter.SAASPART_COL_IND_CONFIG_ID).setCellValue(new HSSFRichTextString(part.getConfigurationId()));
	            newRow.getCell(adapter.SAASPART_COL_IND_REPLACE_PART).setCellValue(new HSSFRichTextString(part.isReplacedPartFlag()?"YES":"NO"));
	            
	            String rampUpTile = part.getRampUpTitle();
	            if(rampUpTile != null && !"".equals(StringUtils.trimToEmpty(rampUpTile))) {
	            	newRow.getCell(adapter.SAASPART_COL_IND_Ramp_Up_Period).setCellValue(new HSSFRichTextString(part.getRampUpTitle()));
	            }
	            
	            newRow.getCell(adapter.SAASPART_COL_IND_MIGRATED_PART).setCellValue(new HSSFRichTextString(part.isMigrationFlag()?"Yes":"No"));
	            
	            if(!"N/A".equals(part.getEpQuantity()) && !part.isShowUpToSelection() && !part.isReplacedPartFlag()){
	            	newRow.getCell(adapter.SAASPART_COL_IND_QUANTITY).setCellValue(Integer.parseInt(part.getEpQuantity()));
	            	newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_QUANTITY).setCellValue(Integer.parseInt(part.getEpQuantity()));
	            	newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_SHOW_QTY).setCellValue(true);
	            } else {
	            	String epQuantity = part.isShowUpToSelection()?"Up To " + part.getEpQuantity():part.getEpQuantity();
	            	if(epQuantity.matches("\\d+")) {
	            		newRow.getCell(adapter.SAASPART_COL_IND_QUANTITY).setCellValue(Integer.parseInt(part.getEpQuantity()));
		            	newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_QUANTITY).setCellValue(Integer.parseInt(part.getEpQuantity()));
	            	} else {
	            		newRow.getCell(adapter.SAASPART_COL_IND_QUANTITY).setCellValue(new HSSFRichTextString(epQuantity));
		            	newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_QUANTITY).setCellValue(new HSSFRichTextString(epQuantity));
	            	}
	            	util.setCellReadOnly(newRow.getCell(adapter.SAASPART_COL_IND_QUANTITY));
	            	util.setCellBorderGrey(newRow.getCell(adapter.SAASPART_COL_IND_QUANTITY), false, 0, 3);
	            	
	            	boolean isShowQuantity = false;
	            	if(part.isShowUpToSelection() || part.isReplacedPartFlag()) {
	            		isShowQuantity = true;
	            	}
	            	newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_SHOW_QTY).setCellValue(isShowQuantity);
	            	
	            	//if show term select, have to set cell border right black
	            	if(part.isShowTermSelectionFlag()) {
	            		util.setCellRightBorderBlack(newRow.getCell(adapter.SAASPART_COL_IND_QUANTITY));
	            	}
	            }
	            
	            if(part.isShowTermSelectionFlag()) {
	            	int contractTerm = new Integer((null != part.getContractTerm() && !"".equals(part.getContractTerm())) ? Integer.parseInt(part.getContractTerm()) : 0);
		            newRow.getCell(adapter.SAASPART_COL_IND_TERM).setCellValue(contractTerm);
		            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_TERM).setCellValue(contractTerm);
		            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_SHOW_TERM).setCellValue(true);
	            } else {
	            	util.setCellValueBlank(newRow.getCell(adapter.SAASPART_COL_IND_TERM));
		            util.setCellValueBlank(newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_TERM));
	            	newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_SHOW_TERM).setCellValue(false);
	            }
	            
	            String billingFrequency = part.getBillingFrequency();
	            if(StringUtils.isBlank(billingFrequency)) {
	            	billingFrequency = "";
	            }
	            newRow.getCell(adapter.SAASPART_COL_IND_BILLING_FREQUENCY).setCellValue(new HSSFRichTextString(billingFrequency));
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_BILLING_FREQUENCY_FOR_RATE).setCellValue(new HSSFRichTextString(billingFrequency));
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_BILLING_FREQUENCY).setCellValue(part.getBillingPeriods());
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_BILLING_FREQUENCY).setCellFormula(billing_periods_formula);
	            
	            if(!part.isShowTermSelectionFlag()){
	            	//set term cell and billing frequency cell read only
	            	util.setCellReadOnly(newRow.getCell(adapter.SAASPART_COL_IND_TERM));
	            	util.setCellBorderGrey(newRow.getCell(adapter.SAASPART_COL_IND_TERM), hasTopBorderForRate, -1, -1);
	            	if(StringUtils.isBlank(billingFrequency)) {
	            		util.setCellReadOnly(newRow.getCell(adapter.SAASPART_COL_IND_BILLING_FREQUENCY));
		            	util.setCellBorderGrey(newRow.getCell(adapter.SAASPART_COL_IND_BILLING_FREQUENCY), hasTopBorderForRate, -1, -1);
	            	}
	            	
	            	//set hasTopBorderForRate = false
	            	hasTopBorderForRate = false;
	            } else {
	            	//set hasTopBorderForRate = true
	            	hasTopBorderForRate = true;
	            }
	            
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_SHOW_TOTALPOINTS).setCellValue(part.isShowTotalPointsFlag());
	            
	            if(part.isShowTotalPointsFlag()) {
	            	if(part.isShowUpToSelection()){
	            		try {
							newRow.getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS).setCellValue(part.getEpItemPoints() * Integer.parseInt(part.getEpQuantity()));
						} catch (NumberFormatException e) {
							newRow.getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS).setCellValue(0);
						}
	            	} else {
	            		newRow.getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS).setCellValue(part.getTotalPoints());
	            	}
	            	newRow.getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS).setCellFormula(saas_total_points_formula);
	            	newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_UNIT_POINTS).setCellValue(part.getEpItemPoints());
	            }
	            
	            if(part.getEntitledRateVal() != -1) {
	            	newRow.getCell(adapter.SAASPART_COL_IND_ENTITLED_RATE).setCellFormula(entitledRateFormula);
	            	newRow.getCell(adapter.SAASPART_COL_IND_ENTITLED_RATE).setCellValue(part.getEntitledRateVal());
	            	util.setDataFormat(newRow.getCell(adapter.SAASPART_COL_IND_ENTITLED_RATE), part.isDecimal4Flag());
	            } else {
	            	util.setCellValueBlank(newRow.getCell(adapter.SAASPART_COL_IND_ENTITLED_RATE));
	            }
	            
	            if(part.getBidRateVal() != -1) {
	            	//newRow.getCell(adapter.SAASPART_COL_IND_BID_RATE).setCellValue(part.getBidRateVal());
	            	util.setDataFormat(newRow.getCell(adapter.SAASPART_COL_IND_BID_RATE), part.isDecimal4Flag());
	            	if(!part.isDisableOverrideUnitPriceInput()) {
	            		newRow.getCell(adapter.SAASPART_COL_IND_BID_RATE).setCellFormula(bidRateFormula);
	            	} else {
	            		newRow.getCell(adapter.SAASPART_COL_IND_BID_RATE).setCellFormula(part.getBidRateVal()+"");
	            	}
	            } else {
	            	util.setCellValueBlank(newRow.getCell(adapter.SAASPART_COL_IND_BID_RATE));
	            }
	            
	            if(!part.isReplacedPartFlag()) {
	            	newRow.getCell(adapter.SAASPART_COL_IND_ENTITLED_TCV).setCellFormula(saas_entitled_tcv_formula);
		            newRow.getCell(adapter.SAASPART_COL_IND_ENTITLED_TCV).setCellValue(part.getEntitledExtPrice());
	            }
	            
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_ENTITLED_TCV).setCellValue(part.getEntitledExtPrice());
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_SHOW_ENTITLETCV).setCellValue(part.isShowEntitledTCVFlag());
	            
	            if(!("".equals(part.getEpEnterOnlyOneOverridePrice()))){
                    util.parseDoubleAndReplaceCommaForCellValue(newRow.getCell(adapter.SAASPART_COL_IND_OVERRIDE_PRICE), 
                    		DecimalUtil.format(util.parseDoubleAndReplaceComma(part.getEpEnterOnlyOneOverridePrice()),part.isDecimal4Flag()? 4 : 2));
                    
                    util.setDataFormat(newRow.getCell(adapter.SAASPART_COL_IND_OVERRIDE_PRICE), part.isDecimal4Flag());
                    
                    util.parseDoubleAndReplaceCommaForCellValue(newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_OVERRIDE_PRICE),
                    		part.getEpEnterOnlyOneOverridePrice());
                }
	            
	            newRow.getCell(adapter.SAASPART_COL_IND_DISCOUNT_PERCENT).setCellValue(part.getEpEnterOnlyOneDiscountPercent());
                newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_OVERRIDE_DISCOUNT).setCellValue(part.getEpEnterOnlyOneDiscountPercent());
                
                if(part.isDisableOverrideUnitPriceInput()) {
                	util.setCellReadOnly(newRow.getCell(adapter.SAASPART_COL_IND_OVERRIDE_PRICE));
                	util.setCellReadOnly(newRow.getCell(adapter.SAASPART_COL_IND_DISCOUNT_PERCENT));
                	
                	util.setCellBorderGrey(newRow.getCell(adapter.SAASPART_COL_IND_OVERRIDE_PRICE), hasTopBorderForOverride, -1, -1);
                	util.setCellBorderGrey(newRow.getCell(adapter.SAASPART_COL_IND_DISCOUNT_PERCENT), hasTopBorderForOverride, -1, -1);
                	
                	hasTopBorderForOverride = false;
                } else {
                	hasTopBorderForOverride = true;
                }
                if(!part.isReplacedPartFlag()) {
                	newRow.getCell(adapter.SAASPART_COL_IND_BID_TCV).setCellFormula(saas_bid_tcv_formula);
     	            newRow.getCell(adapter.SAASPART_COL_IND_BID_TCV).setCellValue(part.getBidExtPrice());
	     	        newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_BID_TCV).setCellValue(part.getBidExtPrice());
                }
                
                newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_SHOW_BIDTCV).setCellValue(part.isShowBidTCVFlag());
                
	            if(quote.isChannelMarginQuote() && !quote.isFCT()) {
	            	if(part.getEpBPStdDiscPct() != 0d) {
	            		newRow.getCell(adapter.SAASPART_COL_IND_BP_DISCOUNT).setCellValue(part.getEpBPStdDiscPct());
	            	} else {
	            		newRow.getCell(adapter.SAASPART_COL_IND_BP_DISCOUNT).setCellValue(0.00);
	            	}
	            	
	            	if(part.getEpBPOvrrdDiscPct() != 0d) {
	            		newRow.getCell(adapter.SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT).setCellValue(part.getEpBPOvrrdDiscPct());
	            	} else {
	            		util.setCellValueBlank(newRow.getCell(adapter.SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT));
	            	}
	            	
	            	newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_BPOVERRIDEDISCOUNT).setCellValue(part.getEpBPOvrrdDiscPct());
	            	if(quote.isPA() || quote.isPAE()){
	            		newRow.getCell(adapter.SAASPART_COL_IND_BP_RATE).setCellFormula(saas_bpRate_formula);
	            	}else{
	            		newRow.getCell(adapter.SAASPART_COL_IND_BP_RATE).setCellValue(new HSSFRichTextString(part.getBpRateVal()));
	            	}
	            	util.setCellTextAlign(newRow.getCell(adapter.SAASPART_COL_IND_BP_RATE), 0, 3);

	            	if(!part.isReplacedPartFlag()) {
	            		newRow.getCell(adapter.SAASPART_COL_IND_BP_TCV).setCellFormula(saas_bp_tcv_formula);
			            newRow.getCell(adapter.SAASPART_COL_IND_BP_TCV).setCellValue(part.getBpLineItemPrice());
	            	}
		            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_BP_TCV).setCellValue(part.getBpLineItemPrice());
	            }
	            
	            if(part.isShowBpTCVFlag()) {
	            	newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_SHOW_BPTCV).setCellValue(true);
	            } else {
	            	newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_SHOW_BPTCV).setCellValue(false);
	            }
        		
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_SUP).setCellValue(part.getLocalUnitProratedPrc());
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_SETUP).setCellValue(part.isSaasSetUpPart());
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_PRODHUMANSERVICE).setCellValue(part.isSaasProdHumanServicesPart());
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_SUBSCRPTN).setCellValue(part.isSaasSubscrptnPart());
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_NOT_CHANGED).setCellFormula(saas_isNotChange_formula);
	            
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_REF_DOC_NUM).setCellValue(new HSSFRichTextString(part.getRefDocNum()));
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_ERROR_CODE).setCellValue(new HSSFRichTextString(part.getErrorCode()));
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_CONFIGRTN_ACTION).setCellValue(new HSSFRichTextString(part.getConfigrtnAction()));
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_ENDDATE).setCellValue(new HSSFRichTextString(part.getConfigrationEndDate()));
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_COTERM_TO_CONFIGRTNID).setCellValue(new HSSFRichTextString(part.getCoTermToConfigrtnId()));
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_OVERRIDE_FLAG).setCellValue(part.isOverrideFlag());
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_CONFIGRTRCONFIGRTNID).setCellValue(new HSSFRichTextString(part.getConfigurationId()));
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_BRAND_CODE).setCellValue(new HSSFRichTextString(part.getBrandCode()));
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_DECIMAL4_FLAG).setCellValue(part.isDecimal4Flag());
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_LOCAL_UNIT_PRICE_FORMULA).setCellFormula(localUnitPriceFormula);
	            
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_SUBSCRPTN_OVRAGE).setCellValue(part.isSaasSubscrptnOvragePart());
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_DEST_SEQ_NUM).setCellValue((new HSSFRichTextString(part.getDestSeqNum())));
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_RELATED_LINEITM_NUM).setCellValue(new HSSFRichTextString(part.getiRelatedLineItmNum()));
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_IS_RAMPUP_PART).setCellValue(part.isbRampUpPart());
	            newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_PROVISIONING_ID).setCellValue(new HSSFRichTextString(part.getProvisioningId()));
	            
	            if("N/A".equals(part.getEpQuantity())){
	            	newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_QUANTITY_FOR_RATE).setCellValue((new HSSFRichTextString(part.getEpQuantity())));
	            } else {
	            	newRow.getCell(adapter.SAASPART_COL_IND_HIDDEN_QUANTITY_FOR_RATE).setCellValue(Integer.parseInt(part.getEpQuantity()));
	            }
	            
	            newRow.setHeight(iRowHeight);
	            rowIndexOfLastPart = rowIndex;
	            rowIndex++;
        	}
        	
        	// delete the extra row.
	        sheetEQSaasExportedPartsAndPricing.shiftRows(rowIndex + 1, sheetEQSaasExportedPartsAndPricing.getLastRowNum(), -1);
	        if(rowIndex==rowIndexOfFirstPart&&rowIndexOfFirstPart==rowIndexOfLastPart){
	            util.getFirstPartRow("epSaasPartNumberLabel").setHeight(iNormalHeight);
	            sheetEQSaasExportedPartsAndPricing.getRow(rowIndex).setHeight(iNormalHeight);
	            util.setCellTopBorder(sheetEQSaasExportedPartsAndPricing.getRow(rowIndex), 0,util.calculateColumnIndexByQuoteType(quote, 15));
	        }
	        
	      //epSubtotalPrice
	        HSSFName nameOfepSubtotalPrice = workbook.getNameAt(workbook.getNameIndex("epSaasSubtotalBidTCV"));
			nameOfepSubtotalPrice.setReference(
			        new CellReference(workbook.getSheetName(workbook.getSheetIndex(sheetEQSaasExportedPartsAndPricing)),
			                										rowIndex,
			                										adapter.SAASPART_COL_IND_BID_TCV,
			                										true, true).formatAsString());
	        
			//Update the cell formula for these named cells	 
	        if(quote.isPA()){
				//Update formula for epSaasSubtotalPoints, e.g. SUM(I11:I13)
	        	util.calculateColumnTotalValue(sheetEQSaasExportedPartsAndPricing, rowIndex
						, adapter.SAASPART_COL_IND_TOTAL_POINTS, rowIndexOfFirstPart, rowIndexOfLastPart);
				
				//Update formula for epSaasTotalPoints e.g. I28
				sheetEQSaasExportedPartsAndPricing.getRow(rowIndex + 2).getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS)
					.setCellFormula(new CellReference(rowIndexOfLastPart + 1,adapter.SAASPART_COL_IND_TOTAL_POINTS).formatAsString());
	        }
	        
	        //Update formula for entitledTCV, e.g. SUM(J11:J13)
	        util.calculateColumnTotalValue(sheetEQSaasExportedPartsAndPricing, rowIndex
					, adapter.SAASPART_COL_IND_ENTITLED_TCV, rowIndexOfFirstPart, rowIndexOfLastPart);
			
			//Update formula for epSaasSubtotalBidTCV, e.g. SUM(M11:M13)
	        util.calculateColumnTotalValue(sheetEQSaasExportedPartsAndPricing, rowIndex
					, adapter.SAASPART_COL_IND_BID_TCV, rowIndexOfFirstPart, rowIndexOfLastPart);
			
			//Update formula for epSaasTotalBidTCV e.g. M28
			sheetEQSaasExportedPartsAndPricing.getRow(rowIndex + 2).getCell(adapter.SAASPART_COL_IND_BID_TCV)
				.setCellFormula(new CellReference(rowIndexOfLastPart + 1,adapter.SAASPART_COL_IND_BID_TCV).formatAsString() + "+apSaasSubtotalBidTCV");
			
			if(quote.isChannelMarginQuote() && !quote.isFCT()) {
				//Update formula for BPTCV, e.g. SUM(P11:P13)
				util.calculateColumnTotalValue(sheetEQSaasExportedPartsAndPricing, rowIndex
						, adapter.SAASPART_COL_IND_BP_TCV, rowIndexOfFirstPart, rowIndexOfLastPart);
			} else {
				//set formula for BPTCV to null
				sheetEQSaasExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.SAASPART_COL_IND_BP_TCV)
					.setCellFormula("\"\"");
			}
			
			//Update header infos that referenced on the changed names
			if(quote.isPA() || quote.isPAE()) {
				if(quote.isPA()) {
					//points 
					sheetEQSaasExportedPartsAndPricing.getRow(3).getCell(adapter.SAASPART_COL_IND_DISCOUNT_PERCENT)
						.setCellFormula(new CellReference(rowIndexOfLastPart + 1,adapter.SAASPART_COL_IND_TOTAL_POINTS).formatAsString());
					sheetEQSaasExportedPartsAndPricing.getRow(4).getCell(adapter.SAASPART_COL_IND_DISCOUNT_PERCENT)
						.setCellFormula(new CellReference(rowIndexOfLastPart + 3,adapter.SAASPART_COL_IND_TOTAL_POINTS).formatAsString());
				}
				
				//price
				sheetEQSaasExportedPartsAndPricing.getRow(3).getCell(adapter.SAASPART_COL_IND_BID_TCV)
					.setCellFormula(new CellReference(rowIndexOfLastPart + 1,adapter.SAASPART_COL_IND_BID_TCV).formatAsString());
				sheetEQSaasExportedPartsAndPricing.getRow(4).getCell(adapter.SAASPART_COL_IND_BID_TCV)
					.setCellFormula(new CellReference(rowIndexOfLastPart + 3,adapter.SAASPART_COL_IND_BID_TCV).formatAsString());
			} else {
				//price
				sheetEQSaasExportedPartsAndPricing.getRow(3).getCell(adapter.SAASPART_COL_IND_BID_TCV-1)
					.setCellFormula(new CellReference(rowIndexOfLastPart + 1,adapter.SAASPART_COL_IND_BID_TCV).formatAsString());
				sheetEQSaasExportedPartsAndPricing.getRow(4).getCell(adapter.SAASPART_COL_IND_BID_TCV-1)
					.setCellFormula(new CellReference(rowIndexOfLastPart + 3,adapter.SAASPART_COL_IND_BID_TCV).formatAsString());
			}
			
			//Update validations
			//1. Part Quantity greater or equal to 0
			CellRangeAddressList addressList1 = new CellRangeAddressList(rowIndexOfFirstPart, rowIndexOfLastPart, adapter.SAASPART_COL_IND_QUANTITY, adapter.SAASPART_COL_IND_QUANTITY);
			DVConstraint dvConstraint1 = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.INTEGER, DVConstraint.OperatorType.GREATER_OR_EQUAL, "0", "0");
			HSSFDataValidation dataValidation1 = new HSSFDataValidation(addressList1, dvConstraint1);
			dataValidation1.createErrorBox("You must enter a whole number", "");
			sheetEQSaasExportedPartsAndPricing.addValidationData(dataValidation1);
			
			//2. Override price greater or equal to 0
			CellRangeAddressList addressList4 = new CellRangeAddressList(rowIndexOfFirstPart, rowIndexOfLastPart, adapter.SAASPART_COL_IND_OVERRIDE_PRICE, adapter.SAASPART_COL_IND_OVERRIDE_PRICE);
			DVConstraint dvConstraint4 = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.DECIMAL, DVConstraint.OperatorType.GREATER_OR_EQUAL, "0", "0");
			HSSFDataValidation dataValidation4 = new HSSFDataValidation(addressList4, dvConstraint4);
			sheetEQSaasExportedPartsAndPricing.addValidationData(dataValidation4);
			
			
			//3. Discount percent between 0 and 1
			util.setCellDataValidations(sheetEQSaasExportedPartsAndPricing, rowIndexOfFirstPart
					, rowIndexOfLastPart, adapter.SAASPART_COL_IND_DISCOUNT_PERCENT, adapter.SAASPART_COL_IND_DISCOUNT_PERCENT);
			
			//4. drop down list for Term column
			int [] termRangeAddArray = {rowIndexOfFirstPart, rowIndexOfLastPart, 
					adapter.SAASPART_COL_IND_TERM, adapter.SAASPART_COL_IND_TERM};
			String [] termDataList = new String[60];
			for(int i=1; i<61; i++) {
				termDataList[i-1] = i+"";
			}
			util.addDropDownListValidation(sheetEQSaasExportedPartsAndPricing, termRangeAddArray, termDataList);
			
			//5. drop down list for Billing Frequency column
			int [] bFRangeAddArray = {rowIndexOfFirstPart, rowIndexOfLastPart, 
					adapter.SAASPART_COL_IND_BILLING_FREQUENCY, adapter.SAASPART_COL_IND_BILLING_FREQUENCY};
			String [] bFDataList = {"Upfront", "Monthly", "Quarterly", "Annual"};
			
			util.addDropDownListValidation(sheetEQSaasExportedPartsAndPricing, bFRangeAddArray, bFDataList);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new QuoteException("Error when processing the Spreedsheet SAAS Exported Parts & Pricing tab");
		}
	}
	
	private void generateSaaSAdditionalPartPricingTab() throws QuoteException{
		HSSFName nameOfepSubtotalPrice = workbook.getNameAt(workbook.getNameIndex("epSaasSubtotalBidTCV"));
		  
		//Update formula for apTotalPrice, e.g. $'EQ Exported Parts _ Pricing'.T14)+apSubtotalPrice
        try {
			util.setCellFormulaValue("apSaasTotalBidTCV", nameOfepSubtotalPrice.getReference() + "+apSaasSubtotalBidTCV");
		} catch (Exception e) {
			 e.printStackTrace();
	         throw new QuoteException("Error when processing the Spreedsheet SaaS Additional Parts & Pricing tab");
		}
	}
	
	private void generateEQNotesTab(SpreadSheetQuote quote) throws QuoteException {
			try {
				util.setCellTextValue("noteAppUrl", quote.getNotesAccessUrl());
				util.setHyperlink(util.getCellByName("noteAppUrl"), quote.getNotesAccessUrl());
			} catch (Exception e) {
				e.printStackTrace();
				throw new QuoteException("Error when setting value for \"noteAppUrl\", in Notes tab");
			}
	}
	
	private void hiddenSaasIntroInNotesTab() throws QuoteException{
		try {
			HSSFSheet sheetNotesTab = util.getSheetByNameName("notes");
			for(int i=51; i<66; i++) {
				if(sheetNotesTab.getRow(i)!=null)
				sheetNotesTab.getRow(i).setZeroHeight(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new QuoteException("Error when processing the Notes tab");
		}
	}
	
	private void generateSubmittedEQExportedPartPricingTab(SpreadSheetQuote quote) throws QuoteException{
	    try {
	        //quoteSVPLevel
	        if(quote.isPA()){
	            util.setCellTextValue("quoteSVPLevel", quote.getInitSVPLevel());
	        }else{
	            util.setCellTextValue("quoteSVPLevel", "");
	            util.setCellTextValue("quoteSVPLevelLabel", "");
	        }
	        //Currency
	        util.setCellTextValue("currency", quote.getCurrency());
	        //pricing date
	        util.setCellTextValue("pricingDate", quote.getPricingDate());

	        int rowIndex = quote.isPGSFlag()? 9 : 10;
	        int rowIndexOfFirstPart = rowIndex;
	        int rowIndexOfLastPart = rowIndex;
	        
            HSSFSheet sheetEQExportedPartsAndPricing = util.getSheetByNameName("partsAndPricingLabel");
	        
	        short iRowHeight = sheetEQExportedPartsAndPricing.getRow(rowIndex).getHeight();	    
	        short iNormalHeight = sheetEQExportedPartsAndPricing.getRow(rowIndex+2).getHeight();
       
	        
            //For Channel Margin Quote
            if(quote.isChannelMarginQuote()&&!quote.isFCT()){
                sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_BP_DISCOUNT,false);
                sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_BP_OVERRIDE_DISCOUNT,false);
                sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA,false);
            }
            
            //for PGS tier2
            
            if(quote.isPGSFlag() && quote.isTier2ResellerFlag()){
            	
            	sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_DISCOUNT_PERCENT,true);
            	sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA,true);            	
            	sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_BP_DISCOUNT,true);
                sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_BP_OVERRIDE_DISCOUNT,true);
                sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA,true);
            }
            
            String previousPartNum = "";
            HSSFCell bidUnitPriceDecimal4FormatCell = sheetEQExportedPartsAndPricing
            											.getRow(adapter.PART_ROW_IND_OVERRIDE_PRICE_DECIMAL_4)
            												.getCell(adapter.PART_COL_IND_BID_UNIT_PRICE_DECIMAL_4);
            
            if(quote.isPGSFlag()) {
            	sheetEQExportedPartsAndPricing.getRow(5).getCell(0)
            		.setCellValue(new HSSFRichTextString("Parts and pricing for exported Software parts from Partner guided selling"));
            }

            // creating the part records one by one...
	        for(Iterator it = quote.getEqPartList().iterator(); it.hasNext();){
	            SpreadSheetPart part = (SpreadSheetPart) it.next();            
	            
	            HSSFRow row = sheetEQExportedPartsAndPricing.getRow(rowIndex);

	            sheetEQExportedPartsAndPricing.shiftRows(rowIndex, sheetEQExportedPartsAndPricing.getLastRowNum(), 1);
	            HSSFRow newRow = sheetEQExportedPartsAndPricing.createRow(rowIndex);
	            HSSFRow shiftedRow = sheetEQExportedPartsAndPricing.getRow(rowIndex + 1);
	            util.copyRowStyle(sheetEQExportedPartsAndPricing, shiftedRow, newRow);

                newRow.getCell(adapter.PART_COL_IND_PART_NUMBER).setCellValue(new HSSFRichTextString(part.getEpPartNumber()));
                newRow.getCell(adapter.PART_COL_IND_PART_DESCRIPTION).setCellValue(new HSSFRichTextString(part.getEpPartDesc()));
                newRow.getCell(adapter.PART_COL_IND_BRAND).setCellValue(new HSSFRichTextString(part.getEpBrandDesc()));
                if(adapter.PART_COL_IND_PART_TYPE != -1){
                	if(part.isExportRestricted()){
                    	newRow.getCell(adapter.PART_COL_IND_PART_TYPE).setCellValue(new HSSFRichTextString(StringUtils.trimToEmpty(part.getSubmittedGroupName()+"          Export restircted")));
                    	
                    }
                    else{
                    	newRow.getCell(adapter.PART_COL_IND_PART_TYPE).setCellValue(new HSSFRichTextString(part.getSubmittedGroupName()));
                   }
                }
            
                newRow.getCell(adapter.PART_COL_IND_RESELLER_AUTHORIZATION).setCellValue(new HSSFRichTextString(StringUtils.trimToEmpty(part.getControlledCodeDesc())));
                newRow.getCell(adapter.PART_COL_IND_RESELLER_AUTHORIZATION_TERMS).setCellValue(new HSSFRichTextString(StringUtils.isEmpty(part.getProgramDesc())?"SW Value Plus Open":StringUtils.trimToEmpty(part.getProgramDesc())));
                if(StringUtils.isBlank(part.getEpQuantity())){
                	newRow.getCell(adapter.PART_COL_IND_QUANTITY).setCellType(HSSFCell.CELL_TYPE_BLANK);
                }else{
                	try {
						newRow.getCell(adapter.PART_COL_IND_QUANTITY).setCellValue(Integer.parseInt(part.getEpQuantity()));
					} catch (NumberFormatException e) {
						newRow.getCell(adapter.PART_COL_IND_QUANTITY).setCellValue(part.getEpQuantity());
					}
                }
                if(null!=part.getRTFStartDate()&&!"".equals(part.getRTFStartDate())){
                    newRow.getCell(adapter.PART_COL_IND_STANDARD_START_DATE_FORMULA).setCellValue(new HSSFRichTextString(part.getRTFStartDate()));
                }else{
                    newRow.getCell(adapter.PART_COL_IND_STANDARD_START_DATE_FORMULA).setCellFormula("\"\"");
                }
                if(null!=part.getRTFEndDate()&&!"".equals(part.getRTFEndDate())){
                    newRow.getCell(adapter.PART_COL_IND_STANDARD_END_DATE_FORMULA).setCellValue(new HSSFRichTextString(part.getRTFEndDate()));
                }else{
                    newRow.getCell(adapter.PART_COL_IND_STANDARD_END_DATE_FORMULA).setCellFormula("\"\"");
                }                

                if(part.getProratedMonthsExcel()>0){
                	newRow.getCell(adapter.PART_COL_IND_PRORATE_MONTHS).setCellValue(part.getProratedMonthsExcel());
                }
                if(part.getComCoverageMonths()>0){
                	newRow.getCell(adapter.PART_COL_IND_COMPRESSED_COVERAGE_MONTHS).setCellValue(part.getComCoverageMonths());
                }
                newRow.getCell(adapter.PART_COL_IND_RENEWAL_QUOTE_NUMBER).setCellValue(new HSSFRichTextString(part.getRenewalQuoteNumber()));
                newRow.getCell(adapter.PART_COL_IND_ITEM_POINTS).setCellValue(part.getEpItemPoints());
                newRow.getCell(adapter.PART_COL_IND_ENTITLED_UNIT_PRICE_FORMULA).setCellValue(part.getEpItemPriceDouble());
                newRow.getCell(adapter.PART_COL_IND_TOTAL_POINTS_FORMULA).setCellValue(part.getTotalPoints());
                newRow.getCell(adapter.PART_COL_IND_BID_UNIT_PRICE_FORMULA).setCellValue(part.getBidUnitPrice());
                
                newRow.getCell(adapter.PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA).setCellValue(part.getEntitledExtPrice());
                newRow.getCell(adapter.PART_COL_IND_DISCOUNT_PERCENT).setCellValue(part.getEpEnterOnlyOneDiscountPercent());
                newRow.getCell(adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA).setCellValue(part.getBidExtPrice());
	            if(quote.isChannelMarginQuote()&&!quote.isFCT()){
                    if(part.getEpBPStdDiscPct()!=0.0d){
                        newRow.getCell(adapter.PART_COL_IND_BP_DISCOUNT).setCellValue(part.getEpBPStdDiscPct());
                    }
                    if(part.isBPDiscOvrrdFlag()){
                    	newRow.getCell(adapter.PART_COL_IND_BP_OVERRIDE_DISCOUNT).setCellValue(part.getEpBPOvrrdDiscPct());
                    }else{
                    	util.setCellValueBlank(newRow.getCell(adapter.PART_COL_IND_BP_OVERRIDE_DISCOUNT));
                    }
	                newRow.getCell(adapter.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA).setCellValue(part.getChannelExtPrice());
	            }
	            if(quote.isChannelMarginQuote()&&!quote.isFCT()){
	                newRow.getCell(adapter.PART_COL_IND_TOTAL_LINE_DISCOUNT_FORMULA).setCellValue(part.getEpTotalLineDiscount());
	            }else{
	                newRow.getCell(adapter.PART_COL_IND_TOTAL_LINE_DISCOUNT_FORMULA).setCellValue(part.getEpEnterOnlyOneDiscountPercent());    
	            }
	            
	            if(part.isDecimal4Flag()){
                	newRow.getCell(adapter.PART_COL_IND_BID_UNIT_PRICE_FORMULA).setCellStyle(bidUnitPriceDecimal4FormatCell.getCellStyle());
                }
	            
	            if(part.isUsagePart()){
	            	newRow.getCell(adapter.PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA).setCellType(HSSFCell.CELL_TYPE_BLANK);
		            newRow.getCell(adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA).setCellType(HSSFCell.CELL_TYPE_BLANK);
		            if(quote.isChannelMarginQuote()&&!quote.isFCT()){
		            	newRow.getCell(adapter.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA).setCellType(HSSFCell.CELL_TYPE_BLANK);
		            }
	            }
	            //release 14.1 : output Equity Curve Data
	            if(!quote.isPGSFlag())
	            	outputEquityCurveRowData(newRow, part);
	            newRow.setHeight(iRowHeight);
	            
	            rowIndexOfLastPart = rowIndex;
	            rowIndex++;
	            previousPartNum = part.getEpPartNumber();
	        }
	        
	        // delete the extra row.
	        sheetEQExportedPartsAndPricing.shiftRows(rowIndex + 1, sheetEQExportedPartsAndPricing.getLastRowNum(), -1);
	        if(rowIndex==rowIndexOfFirstPart&&rowIndexOfFirstPart==rowIndexOfLastPart){
	            sheetEQExportedPartsAndPricing.getRow(rowIndex).setHeight(iNormalHeight);
	        }
	        
	        if(!quote.isPGSFlag()){
	        // release 14.1: if PA or PAE quote, output the Equity Curve sub-total data .
	        outputEquityCurveTotalData(sheetEQExportedPartsAndPricing.getRow(rowIndex), quote);
	        //release 14.1: if the quote is PA or PAE, then display Equity Curve data, otherwise hide the EC data.
            showEquityCurveCellsForSubmitted(sheetEQExportedPartsAndPricing,
            		rowIndexOfFirstPart,
            		rowIndexOfLastPart,
            		(quote.isPA() || quote.isPAE())&&!quote.isFCTTOPA() );
            rowIndex++;
	        }

	        if(quote.isHasSoftwareLineItem()){
		        // hidden the points columns when the LOB is other than PA.
		        if(!quote.isPA()){
		        	sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_ITEM_POINTS, true);
		        	sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_TOTAL_POINTS_FORMULA, true);
		        }
		        
				//Update value for Total - Total Points, e.g. 929.91
				HSSFCell cellOfTotalTotalPoints = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_TOTAL_POINTS_FORMULA);
				cellOfTotalTotalPoints.setCellValue(Double.parseDouble(quote.getTotalSoftwarePoint().replaceAll(",", "")));
				
				//Update formula for Total - Entitled extended price
				HSSFCell cellOfTotalEntitledExtendedPrice = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA);
				cellOfTotalEntitledExtendedPrice.setCellValue(quote.getEntitledExtTotalPrice());
	
				//Update formula for Total - End user discount
				HSSFCell cellOfTotalEndUserDiscount = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_DISCOUNT_PERCENT);
	    		cellOfTotalEndUserDiscount.setCellValue(quote.getUserLineTotalDiscount());
				
				//Update value for Total - Bid Extended Price, e.g. 167670.46
				HSSFCell cellOfTotalBidExtendedPrice = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA);
				cellOfTotalBidExtendedPrice.setCellValue(new HSSFRichTextString(quote.getBidExtTotalPrice()));	
				
				//BP total extended price
				if(quote.isChannelMarginQuote()&&!quote.isFCT()){
					HSSFCell cellOfTotalBPExtendedPrice = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA);
					cellOfTotalBPExtendedPrice.setCellValue(quote.getBpExtTotalPrice());
				}
				
				//Update formula for Total - Total line discount
				HSSFCell cellOfTotalLineDiscount = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_TOTAL_LINE_DISCOUNT_FORMULA);
				if(quote.isChannelMarginQuote()&&!quote.isFCT()){
					cellOfTotalLineDiscount.setCellValue(quote.getTotalLineTotalDiscount());
				}else{
					cellOfTotalLineDiscount.setCellValue(quote.getUserLineTotalDiscount());
				}
				
				//Update header total infos
				if(quote.isPA()){
					if(quote.getTotalSoftwarePoint()!=null&&!quote.getTotalSoftwarePoint().trim().equals("")){
						sheetEQExportedPartsAndPricing.getRow(4).getCell(adapter.PART_COL_IND_DISCOUNT_PERCENT-2).setCellValue(Double.parseDouble(quote.getTotalSoftwarePoint().replaceAll(",", "")));
					}
				}else{
					sheetEQExportedPartsAndPricing.getRow(3).getCell(adapter.PART_COL_IND_DISCOUNT_PERCENT-2).setCellType(HSSFCell.CELL_TYPE_BLANK);
					sheetEQExportedPartsAndPricing.getRow(4).getCell(adapter.PART_COL_IND_DISCOUNT_PERCENT-2).setCellType(HSSFCell.CELL_TYPE_BLANK);
				}
				if(quote.getBidExtTotalPrice()!=null&&!quote.getBidExtTotalPrice().trim().equals("")){
					sheetEQExportedPartsAndPricing.getRow(4).getCell(adapter.PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA).setCellValue(Double.parseDouble(quote.getBidExtTotalPrice().replaceAll(",", "")));
				}
	        }else{
	        	// set all fields to zero
		        if(!quote.isPA()){
		        	sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_ITEM_POINTS, true);
		        	sheetEQExportedPartsAndPricing.setColumnHidden(adapter.PART_COL_IND_TOTAL_POINTS_FORMULA, true);
		        }
		        
				HSSFCell cellOfTotalTotalPoints = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_TOTAL_POINTS_FORMULA);
				cellOfTotalTotalPoints.setCellValue(0.0d);
				
				HSSFCell cellOfTotalEntitledExtendedPrice = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA);
				cellOfTotalEntitledExtendedPrice.setCellValue(0.0d);
	
				HSSFCell cellOfTotalEndUserDiscount = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_DISCOUNT_PERCENT);
	    		cellOfTotalEndUserDiscount.setCellValue(0.0d);
				
				HSSFCell cellOfTotalBidExtendedPrice = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_BID_EXTENDED_PRICE_FORMULA);
				cellOfTotalBidExtendedPrice.setCellValue(0.0d);
				
				if(quote.isChannelMarginQuote()&&!quote.isFCT()){
					HSSFCell cellOfTotalBPExtendedPrice = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_BP_EXTENDED_PRICE_FORMULA);
					cellOfTotalBPExtendedPrice.setCellValue(0.0d);
				}
				
				HSSFCell cellOfTotalLineDiscount = sheetEQExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.PART_COL_IND_TOTAL_LINE_DISCOUNT_FORMULA);
				if(quote.isChannelMarginQuote()&&!quote.isFCT()){
					cellOfTotalLineDiscount.setCellValue(0.0d);
				}else{
					cellOfTotalLineDiscount.setCellValue(0.0d);
				}
				
				//Update header total infos
				if(quote.isPA()){
					sheetEQExportedPartsAndPricing.getRow(4).getCell(adapter.PART_COL_IND_BID_UNIT_PRICE_FORMULA).setCellValue(0.0d);
				}else{
					sheetEQExportedPartsAndPricing.getRow(3).getCell(adapter.PART_COL_IND_BID_UNIT_PRICE_FORMULA).setCellType(HSSFCell.CELL_TYPE_BLANK);
					sheetEQExportedPartsAndPricing.getRow(4).getCell(adapter.PART_COL_IND_BID_UNIT_PRICE_FORMULA).setCellType(HSSFCell.CELL_TYPE_BLANK);
				}
				sheetEQExportedPartsAndPricing.getRow(4).getCell(adapter.PART_COL_IND_ENTITLED_EXTENDED_PRICE_FORMULA).setCellValue(0.0d);
	        }
			//protect sheet with password
            if(quote.isProtected()){
                sheetEQExportedPartsAndPricing.protectSheet("dsw-sqo");
            }else{
                //this deprecated method can work
                sheetEQExportedPartsAndPricing.setProtect(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new QuoteException("Error when processing the Submitted Spreedsheet Exported Parts & Pricing tab");
        }
	}
	//generate parts tab for SaaS
	private void generateSubmittedSaaSExportedPartPricingTab(SpreadSheetQuote quote) throws QuoteException {
        try {
	        //quoteSVPLevel
	        if(quote.isPA()){
	            util.setCellTextValue("quoteSVPLevelSaaS", quote.getInitSVPLevel());
	        }else{
	            util.setCellTextValue("quoteSVPLevelSaaS", "");
	            util.setCellTextValue("quoteSVPLevelLabelSaaS", "");
	        }
	        //Currency
	        util.setCellTextValue("currencySaaS", quote.getCurrency());
	        //pricing date
	        util.setCellTextValue("pricingDateSaaS", quote.getPricingDate());
        	
        	HSSFSheet sheetSaaSExportedPartsAndPricing = util.getSheetByNameName("epSaasPartNumberLabel");
        	
        	int rowIndex = util.getFirstPartRow("epSaasPartNumberLabel").getRowNum();
        	int rowIndexOfFirstPart = rowIndex;
        	int rowIndexOfLastPart = rowIndex;
        	
        	short iRowHeight = util.getFirstPartRow("epSaasPartNumberLabel").getHeight();
	        short iNormalHeight = sheetSaaSExportedPartsAndPricing.getRow(util.getFirstPartRow("epSaasPartNumberLabel").getRowNum()+1).getHeight();
	        
	        //For Channel Margin Quote
            if((quote.isChannelMarginQuote()&&!quote.isFCT())) {
                sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_DISCOUNT_SUBMIT, false);
                sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT_SUBMIT, false);
                sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_TCV_SUBMIT, false);
            }else{
            	sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_DISCOUNT_SUBMIT, true);
                sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT_SUBMIT, true);
                sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_TCV_SUBMIT, true);
            }
            
            if(quote.isTier2ResellerFlag()) {
  	        	 sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_ENTITLED_RATE_SUBMIT, true);
  	        	 sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BID_RATE_SUBMIT, true);
  	        	 sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BID_TCV_SUBMIT, true);
  	        	 sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_DISCOUNT_PERCENT_SUBMIT, true);
  	        	 sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_DISCOUNT_SUBMIT, true);
                 sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT_SUBMIT, true);
                 sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_TCV_SUBMIT, true);
  	        }
            
            if(quote.isPGSFlag()) {
            	//if pgs app, hide the migrated part column
            	sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_MIGRATED_PART_SUBMIT,true);
            	
            	sheetSaaSExportedPartsAndPricing.getRow(5).getCell(0)
            		.setCellValue(new HSSFRichTextString("Parts and pricing for exported Software as a Service parts from Partner guided selling"));
            	
            	//if pgs app, hide the bp override discount column
            	sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT_SUBMIT, true);
            }
            
        	for(Iterator<SpreadSheetPart> it = quote.getSaaSEqPartList().iterator(); it.hasNext();){
        		SpreadSheetPart part = it.next();
        		HSSFRow row = sheetSaaSExportedPartsAndPricing.getRow(rowIndex);
        		
        		sheetSaaSExportedPartsAndPricing.shiftRows(rowIndex, sheetSaaSExportedPartsAndPricing.getLastRowNum(), 1);
	            HSSFRow newRow = sheetSaaSExportedPartsAndPricing.createRow(rowIndex);
	            HSSFRow shiftedRow = sheetSaaSExportedPartsAndPricing.getRow(rowIndex + 1);
	            util.copyRowStyle(sheetSaaSExportedPartsAndPricing, shiftedRow, newRow);
        		
	            newRow.getCell(adapter.SAASPART_COL_IND_PART_NUMBER).setCellValue(new HSSFRichTextString(part.getEpPartNumber()));
	            newRow.getCell(adapter.SAASPART_COL_IND_PART_DESCRIPTION).setCellValue(new HSSFRichTextString(part.getEpPartDesc()));
	            newRow.getCell(adapter.SAASPART_COL_IND_CONFIG_ID).setCellValue(new HSSFRichTextString(part.getConfigurationId()));
	            newRow.getCell(adapter.SAASPART_COL_IND_REPLACE_PART).setCellValue(new HSSFRichTextString(part.isRepeatedReplaceTitle()?"YES":"NO"));
	            
	            String rampUpTile = part.getRampUpTitle();
	            if(rampUpTile != null && !"".equals(StringUtils.trimToEmpty(rampUpTile))) {
	            	newRow.getCell(adapter.SAASPART_COL_IND_Ramp_Up_Period).setCellValue(new HSSFRichTextString(part.getRampUpTitle()));
	            }
	            
	            newRow.getCell(adapter.SAASPART_COL_IND_MIGRATED_PART_SUBMIT).setCellValue(new HSSFRichTextString(part.isMigrationFlag()?"Yes":"No"));
	            
	            if(!"N/A".equals(part.getEpQuantity()) && !part.isShowUpToSelection() && !part.isReplacedPartFlag()){
	            	newRow.getCell(adapter.SAASPART_COL_IND_QUANTITY_SUBMIT).setCellValue(Integer.parseInt(part.getEpQuantity()));
	            } else {
	            	String epQuantity = part.isShowUpToSelection()?"Up To " + part.getEpQuantity():part.getEpQuantity();
	            	if(epQuantity.matches("\\d+")) {
	            		newRow.getCell(adapter.SAASPART_COL_IND_QUANTITY_SUBMIT).setCellValue(Integer.parseInt(part.getEpQuantity()));
	            	} else {
	            		newRow.getCell(adapter.SAASPART_COL_IND_QUANTITY_SUBMIT).setCellValue(new HSSFRichTextString(epQuantity));
	            	}
	            	util.setCellBorderGrey(newRow.getCell(adapter.SAASPART_COL_IND_QUANTITY_SUBMIT), false, 0, 3);
	            }
	            
	            if(part.isShowTermSelectionFlag()) {
	            	int contractTerm = new Integer("".equals(part.getContractTerm()) ? "0" : part.getContractTerm()).intValue();
		            String cvrageTermUnit = "";
		            if(contractTerm > 0) {
		            	cvrageTermUnit = contractTerm + " " + part.getCvrageTermUnit();
		            }
		            newRow.getCell(adapter.SAASPART_COL_IND_TERM_SUBMIT).setCellValue(new HSSFRichTextString(cvrageTermUnit));
	            } else {
	            	util.setCellValueBlank(newRow.getCell(adapter.SAASPART_COL_IND_TERM_SUBMIT));
	            }
	            
	            String billingFrequency = part.getBillingFrequency();
	            if(billingFrequency == null || "".equals(StringUtils.trimToEmpty(billingFrequency)) || !part.isShowBillingFrequencyFlag()) {
	            	billingFrequency = "";
	            }
	            newRow.getCell(adapter.SAASPART_COL_IND_BILLING_FREQUENCY_SUBMIT).setCellValue(new HSSFRichTextString(billingFrequency));
	            
	            if(part.getEntitledRateVal() != -1) {
	            	newRow.getCell(adapter.SAASPART_COL_IND_ENTITLED_RATE_SUBMIT).setCellValue(part.getEntitledRateVal());
	            	util.setDataFormat(newRow.getCell(adapter.SAASPART_COL_IND_ENTITLED_RATE_SUBMIT), part.isDecimal4Flag());
	            } else {
	            	util.setCellValueBlank(newRow.getCell(adapter.SAASPART_COL_IND_ENTITLED_RATE_SUBMIT));
	            }
	            
	            if(part.getBidRateVal() != -1) {
	            	newRow.getCell(adapter.SAASPART_COL_IND_BID_RATE_SUBMIT).setCellValue(part.getBidRateVal());
	            	util.setDataFormat(newRow.getCell(adapter.SAASPART_COL_IND_BID_RATE_SUBMIT), part.isDecimal4Flag());
	            } else {
	            	util.setCellValueBlank(newRow.getCell(adapter.SAASPART_COL_IND_BID_RATE_SUBMIT));
	            }
	            
	            if(part.isShowTotalPointsFlag()){
	            	newRow.getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS_SUBMIT).setCellValue(part.getTotalPoints());
	            } else {
	            	util.setCellValueBlank(newRow.getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS_SUBMIT));
	            }

	           
	            if(part.isShowEntitledTCVFlag()) {
	            	newRow.getCell(adapter.SAASPART_COL_IND_ENTITLED_TCV_SUBMIT).setCellValue(part.getEntitledExtPrice());
	            }else{
	            	util.setCellValueBlank(newRow.getCell(adapter.SAASPART_COL_IND_ENTITLED_TCV_SUBMIT));
	            }
	            
                if(part.getEpEnterOnlyOneDiscountPercent()!=0.0d){
                    newRow.getCell(adapter.SAASPART_COL_IND_DISCOUNT_PERCENT_SUBMIT).setCellValue(part.getEpEnterOnlyOneDiscountPercent());
                }
	            
                if(part.isShowBidTCVFlag()) {
                	newRow.getCell(adapter.SAASPART_COL_IND_BID_TCV_SUBMIT).setCellValue(part.getBidExtPrice());
                }else{
	            	util.setCellValueBlank(newRow.getCell(adapter.SAASPART_COL_IND_BID_TCV_SUBMIT));
	            }
	            
	            if(quote.isChannelMarginQuote()&&!quote.isFCT()){
	            	if(part.getEpBPStdDiscPct() != 0d) {
	            		newRow.getCell(adapter.SAASPART_COL_IND_BP_DISCOUNT_SUBMIT).setCellValue(part.getEpBPStdDiscPct());
	            	} else {
	            		newRow.getCell(adapter.SAASPART_COL_IND_BP_DISCOUNT_SUBMIT).setCellValue(0.00);
	            	}

	            	if(part.getEpBPOvrrdDiscPct() != 0d) {
	            		newRow.getCell(adapter.SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT_SUBMIT).setCellValue(part.getEpBPOvrrdDiscPct());
	            	} else {
	            		util.setCellValueBlank(newRow.getCell(adapter.SAASPART_COL_IND_BP_OVERRIDE_DISCOUNT_SUBMIT));
	            	}
	            	
                    if(part.isShowBpTCVFlag()){
                    	newRow.getCell(adapter.SAASPART_COL_IND_BP_TCV_SUBMIT).setCellValue(part.getBpLineItemPrice());
                	}else{
                		util.setCellValueBlank(newRow.getCell(adapter.SAASPART_COL_IND_BP_TCV_SUBMIT));
                	}
	            }
	            
	            newRow.setHeight(iRowHeight);
	            rowIndexOfLastPart = rowIndex;
	            rowIndex++;
        	}
        	
        	// delete the extra row.
	        sheetSaaSExportedPartsAndPricing.shiftRows(rowIndex + 1, sheetSaaSExportedPartsAndPricing.getLastRowNum(), -1);
	        if(rowIndex==rowIndexOfFirstPart&&rowIndexOfFirstPart==rowIndexOfLastPart){
	            util.getFirstPartRow("epSaasPartNumberLabel").setHeight(iNormalHeight);
	        }
	        
	        if(quote.isHasSaaSLineItem()){
		        // hidden the points columns when the LOB is other than PA.
		        if(!quote.isPA()){
		        	sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_TOTAL_POINTS_SUBMIT, true);
		        }
		        
				//Update value for Total - Total Points, e.g. 929.91
				HSSFCell cellOfTotalPoints = sheetSaaSExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS_SUBMIT);
				util.parseDoubleAndReplaceCommaForCellValue(cellOfTotalPoints, quote.getTotalSaasPoint());
				
				HSSFCell cellOfTotalEntitledTCV = sheetSaaSExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.SAASPART_COL_IND_ENTITLED_TCV_SUBMIT);
				util.parseDoubleAndReplaceCommaForCellValue(cellOfTotalEntitledTCV, quote.getTotalEntitledTCV());
				
				//Update value for Total - Bid Extended Price, e.g. 167670.46
				HSSFCell cellOfTotalBidTCV = sheetSaaSExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.SAASPART_COL_IND_BID_TCV_FORMULA);
				util.parseDoubleAndReplaceCommaForCellValue(cellOfTotalBidTCV, quote.getTotalCommitValue());
				
				//BP total TCV
				if(quote.isChannelMarginQuote()&&!quote.isFCT()){
					HSSFCell cellOfTotalBPTCV = sheetSaaSExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.SAASPART_COL_IND_BP_TCV_FORMULA);
					util.parseDoubleAndReplaceCommaForCellValue(cellOfTotalBPTCV, quote.getBpTCVPrice());
				}
				
				//Update header total infos
				if(quote.isPA() || quote.isPAE()){
					util.parseDoubleAndReplaceCommaForCellValue(
							sheetSaaSExportedPartsAndPricing.getRow(4).getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS_SUBMIT), quote.getTotalSaasPoint());
				}else{
					sheetSaaSExportedPartsAndPricing.getRow(3).getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS_SUBMIT).setCellType(HSSFCell.CELL_TYPE_BLANK);
					sheetSaaSExportedPartsAndPricing.getRow(4).getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS_SUBMIT).setCellType(HSSFCell.CELL_TYPE_BLANK);
				}
				util.parseDoubleAndReplaceCommaForCellValue(
						sheetSaaSExportedPartsAndPricing.getRow(4).getCell(adapter.SAASPART_COL_IND_ENTITLED_TCV_SUBMIT), quote.getTotalCommitValue());
	        }else{
	        	// set all field to zero
		        if(!quote.isPA()){
		        	sheetSaaSExportedPartsAndPricing.setColumnHidden(adapter.SAASPART_COL_IND_TOTAL_POINTS_SUBMIT, true);
		        }
		        
				HSSFCell cellOfTotalPoints = sheetSaaSExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS_SUBMIT);
				cellOfTotalPoints.setCellValue(0.0d);
				
				HSSFCell cellOfTotalEntitledTCV = sheetSaaSExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.SAASPART_COL_IND_ENTITLED_TCV_SUBMIT);
				cellOfTotalEntitledTCV.setCellValue(0.0d);
				
				HSSFCell cellOfTotalBidTCV = sheetSaaSExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.SAASPART_COL_IND_BID_TCV_FORMULA);
				cellOfTotalBidTCV.setCellValue("0.00");	
				
				//BP total TCV
				if(quote.isChannelMarginQuote()&&!quote.isFCT()){
					HSSFCell cellOfTotalBPTCV = sheetSaaSExportedPartsAndPricing.getRow(rowIndex).getCell(adapter.SAASPART_COL_IND_BP_TCV_FORMULA);
					cellOfTotalBPTCV.setCellValue("0.00");
				}
				
				if(quote.isPA() || quote.isPAE()){
					sheetSaaSExportedPartsAndPricing.getRow(4).getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS_SUBMIT).setCellValue(0.0d);
					sheetSaaSExportedPartsAndPricing.getRow(4).getCell(adapter.SAASPART_COL_IND_ENTITLED_TCV_SUBMIT).setCellValue(0.0d);
				}else{
					sheetSaaSExportedPartsAndPricing.getRow(3).getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS_SUBMIT).setCellType(HSSFCell.CELL_TYPE_BLANK);
					sheetSaaSExportedPartsAndPricing.getRow(4).getCell(adapter.SAASPART_COL_IND_TOTAL_POINTS_SUBMIT).setCellType(HSSFCell.CELL_TYPE_BLANK);
					sheetSaaSExportedPartsAndPricing.getRow(4).getCell(adapter.SAASPART_COL_IND_ENTITLED_TCV_SUBMIT).setCellValue(0.0d);
				}
				//sheetSaaSExportedPartsAndPricing.getRow(4).getCell(adapter.SAASPART_COL_IND_BID_TCV_FORMULA).setCellValue(0.0d);
	        }
			//protect sheet with password
            if(quote.isProtected()){
                sheetSaaSExportedPartsAndPricing.protectSheet("dsw-sqo");
            }else{
                //this deprecated method can work
                sheetSaaSExportedPartsAndPricing.setProtect(false);
            }
		} catch (Exception e) {
			e.printStackTrace();
			throw new QuoteException("Error when processing the Spreedsheet SAAS Exported Parts & Pricing tab");
		}
	}
	
	/**
	 * Export the Equity Curve data of a part.
	 * @param newRow
	 * @param part
	 * @since release 14.1
	 */
	private void outputEquityCurveRowData(HSSFRow newRow, SpreadSheetPart part){
        //release 14.1 Equity curve
//        newRow.getCell(adapter.PART_COL_IND_BRAND).setCellValue(new HSSFRichTextString(part.getBrandCode()));
//        newRow.getCell(adapter.PART_COL_IND_PARTTYPE).setCellValue(new HSSFRichTextString(part.getPartType()));
        newRow.getCell(adapter.PART_COL_IND_EC_TOPPERFORMER_PERCENTAGE).setCellValue(new HSSFRichTextString(part.getPreferDiscount()));
        newRow.getCell(adapter.PART_COL_IND_EC_TOPPERFORMER_UNITPRICE).setCellValue(new HSSFRichTextString(part.getPreferBidUnitPrice()));
        newRow.getCell(adapter.PART_COL_IND_EC_TOPPERFORMER_EXTENDEDPRICE).setCellValue(new HSSFRichTextString(part.getPreferBidExtendedPrice()));
        newRow.getCell(adapter.PART_COL_IND_EC_MARKETAVERAGE_PERCENTAGE).setCellValue(new HSSFRichTextString(part.getMaxDiscount()));
        newRow.getCell(adapter.PART_COL_IND_EC_MARKETAVERAGE_UNITPRICE).setCellValue(new HSSFRichTextString(part.getMaxBidUnitPrice()));
        newRow.getCell(adapter.PART_COL_IND_EC_MARKETAVERAGE_EXTENDEDPRICE).setCellValue(new HSSFRichTextString(part.getMaxBidExendedPrice()));
//        newRow.getCell(adapter.PART_COL_IND_EC_PRIORCUSTOMERPURCHASE).setCellValue(new HSSFRichTextString(
//        		part.isPriorCustomerPurchase_MarketAverage() || part.isPriorCustomerPurchase_TopPerformer()?"Y":"N"));
        newRow.getCell(adapter.PART_COL_IND_EC_PRIORCUSTOMERPURCHASE).setCellValue(new HSSFRichTextString(part.getPriorCustomerPurchase()));
        if(adapter.PART_COL_IND_BRAND != -1){
        	
        	newRow.getCell(adapter.PART_COL_IND_BRAND).setCellValue(new HSSFRichTextString(part.getEpBrandDesc()));
        }
        if(adapter.PART_COL_IND_PART_TYPE != -1){
        	if(part.isExportRestricted()){
            	newRow.getCell(adapter.PART_COL_IND_PART_TYPE).setCellValue(new HSSFRichTextString(StringUtils.trimToEmpty(part.getSubmittedGroupName()+"          Export restircted")));
            	
            }
            else{
            	newRow.getCell(adapter.PART_COL_IND_PART_TYPE).setCellValue(new HSSFRichTextString(part.getSubmittedGroupName()));
           }
        }
	}
	
	/**
	 * Export the Equity Curve Total data of the quote.
	 * @param equityCurveTotalRow which row the total data should output in.
	 * @param quote current quote
	 */
	private void outputEquityCurveTotalData(HSSFRow equityCurveTotalRow, SpreadSheetQuote quote){
		//Top Performer
		equityCurveTotalRow.getCell(adapter.PART_COL_IND_EC_TOPPERFORMER_PERCENTAGE)
		.setCellValue(new HSSFRichTextString(quote.getPreferDiscountTotal()));
		equityCurveTotalRow.getCell(adapter.PART_COL_IND_EC_TOPPERFORMER_UNITPRICE)
		.setCellValue(new HSSFRichTextString(quote.getPreferBidUnitPriceTotal()));
		equityCurveTotalRow.getCell(adapter.PART_COL_IND_EC_TOPPERFORMER_EXTENDEDPRICE)
		.setCellValue(new HSSFRichTextString(quote.getPreferBidExtendedPriceTotal()));
		//Market ave
		equityCurveTotalRow.getCell(adapter.PART_COL_IND_EC_MARKETAVERAGE_PERCENTAGE)
		.setCellValue(new HSSFRichTextString(quote.getMaxDiscountTotal()));
		equityCurveTotalRow.getCell(adapter.PART_COL_IND_EC_MARKETAVERAGE_UNITPRICE)
		.setCellValue(new HSSFRichTextString(quote.getMaxBidUnitPriceTotal()));
		equityCurveTotalRow.getCell(adapter.PART_COL_IND_EC_MARKETAVERAGE_EXTENDEDPRICE)
		.setCellValue(new HSSFRichTextString(quote.getMaxBidExendedPriceTotal()));
	}
	
	/**
	 * Show/hide the Equity Curve data cells for submitted quote.
	 * @param swSheet the "SW Exported Parts & Pricing" sheet
	 * @param showECCells whether display the equity curve data cells. true-show EC cells. false-hide EC cells.
	 *  EC Cells include seven columns and two rows(description row and EC subtotal row).
	 */
	private void showEquityCurveCellsForSubmitted(HSSFSheet swSheet, 
			int firstPartRow, int lastPartRow, boolean showECCells) {
		boolean hidden = !showECCells;
		swSheet.setColumnHidden(adapter.PART_COL_IND_EC_TOPPERFORMER_EXTENDEDPRICE,hidden);
		swSheet.setColumnHidden(adapter.PART_COL_IND_EC_TOPPERFORMER_PERCENTAGE,hidden);
		swSheet.setColumnHidden(adapter.PART_COL_IND_EC_TOPPERFORMER_UNITPRICE,hidden);
		swSheet.setColumnHidden(adapter.PART_COL_IND_EC_MARKETAVERAGE_EXTENDEDPRICE,hidden);
		swSheet.setColumnHidden(adapter.PART_COL_IND_EC_MARKETAVERAGE_PERCENTAGE,hidden);
		swSheet.setColumnHidden(adapter.PART_COL_IND_EC_MARKETAVERAGE_UNITPRICE,hidden);
		swSheet.setColumnHidden(adapter.PART_COL_IND_EC_PRIORCUSTOMERPURCHASE,hidden);
        //submitted quote sw export: hide rows
		if(hidden){
			swSheet.getRow(lastPartRow + 1).setZeroHeight(true);//Equity Curve remark row
			swSheet.getRow(firstPartRow - 1).setZeroHeight(true);//Equity Curve discount guidance - Total
		}
	}
}
