/*
 * Created on Mar 19, 2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.newquote.spreadsheet;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddressList;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * @author liangyue@cn.ibm.com
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NativeExcelUtil {

    private HSSFWorkbook workbook;
    
    protected NativeExcelUtil(HSSFWorkbook workbook){
        this.workbook = workbook;
    }
	/**
	 * return the sheet where the specified name is residing in
	 */
    protected HSSFSheet getSheetByNameName(String nameString) throws Exception{
        HSSFName name = workbook.getNameAt(workbook.getNameIndex(nameString));
        CellReference reference = new CellReference(name.getReference());
        return workbook.getSheet(reference.getSheetName());    
    }
    
    /**
	 * check the saas tab exist or not by sheet name
	 */
    protected boolean isExistSheetByNameName(String nameString) throws Exception{
    	return workbook.getNameIndex(nameString)!=-1? true : false; 
    }
    
	/**
	 * replace name in the formula with the absolute position, this is the workaround for reading named cell value from another sheet
	 */    
    protected String replaceNameWithPostionString(String nameString) throws Exception{
        HSSFName name = workbook.getNameAt(workbook.getNameIndex(nameString));
        return name.getReference().replaceAll("\\$", "\\\\\\$");
    }
    
    
    protected String stringCheckOnResellerAuthorizationTerms(String source) throws Exception{
        return StringUtils.isEmpty(source)?"SW Value Plus Open":source;
    }
    
	/**
	 * set text string value for single cell that bound with a name.
	 */
    protected void setCellTextValue(String nameName, String value) throws Exception{
    	HSSFCell cell = this.getCellByName(nameName);
		cell.setCellValue(new HSSFRichTextString(value));
    }
    
    protected HSSFCell getCellByName(String nameName) {
    	HSSFName name = workbook.getNameAt(workbook.getNameIndex(nameName));
		CellReference reference = new CellReference(name.getReference());
		HSSFSheet sheet = workbook.getSheet(reference.getSheetName());		
		HSSFRow row = sheet.getRow(reference.getRow());
		return row.getCell((int) reference.getCol());
    }
	/**
	 * get text string value for single cell that bound with a name.
	 */
    protected String getCellTextValue(String nameName) throws Exception{
        if (workbook.getNameIndex(nameName) > -1) {
            HSSFName name = workbook.getNameAt(workbook.getNameIndex(nameName));
            CellReference reference = new CellReference(name.getReference());
            HSSFSheet sheet = workbook.getSheet(reference.getSheetName());
            HSSFRow row = sheet.getRow(reference.getRow());
            HSSFCell cell = row.getCell((int) reference.getCol());
            return cell.getRichStringCellValue().getString();
        } else {
            return "";
        }
    }
    
	/**
	 * set text string value for single cell located by absolute position.
	 */
    protected void setCellTextValueByAbsolutePosition(HSSFSheet sheet, String positionString, String value) throws Exception{
		CellReference reference = new CellReference(positionString);
		HSSFRow row = sheet.getRow(reference.getRow());
		HSSFCell cell = row.getCell((int) reference.getCol());
		cell.setCellValue(new HSSFRichTextString(value));
    }

	/**
	 * set formula value for single cell that bound with a name.
	 */
    protected void setCellFormulaValue(String nameName, String value) throws Exception{
		HSSFName name = workbook.getNameAt(workbook.getNameIndex(nameName));
		CellReference reference = new CellReference(name.getReference());
		HSSFSheet sheet = workbook.getSheet(reference.getSheetName());		
		HSSFRow row = sheet.getRow(reference.getRow());
		HSSFCell cell = row.getCell((int) reference.getCol());
		cell.setCellFormula(value);
    }
    
	/**
	 * get formula value for single cell that bound with a name.
	 */
    protected String getCellFormulaValue(String nameName) throws Exception{
		HSSFName name = workbook.getNameAt(workbook.getNameIndex(nameName));
		CellReference reference = new CellReference(name.getReference());
		HSSFSheet sheet = workbook.getSheet(reference.getSheetName());		
		HSSFRow row = sheet.getRow(reference.getRow());
		HSSFCell cell = row.getCell((int) reference.getCol());
		return cell.getCellFormula();
    }

	/**
	 * get the first part row in the exported quotes sheet.
	 */
    protected HSSFRow getFirstPartRow(String nameOfPartNumberLabel) throws Exception{
        HSSFName name = workbook.getNameAt(workbook.getNameIndex(nameOfPartNumberLabel));
        CellReference reference = new CellReference(name.getReference());
        HSSFSheet sheet = workbook.getSheet(reference.getSheetName());
        int iRowFirstPart = 0;
        if("epPartNumberLabel".equals(nameOfPartNumberLabel)){
            iRowFirstPart = reference.getRow() + 4;
        }
        if("apPartNumberLabel".equals(nameOfPartNumberLabel)){
            iRowFirstPart = reference.getRow() + 5;
        }
        if("epSaasPartNumberLabel".equals(nameOfPartNumberLabel)) {
        	iRowFirstPart = reference.getRow() + 4;
        }
        if("apSaasPartNumberLabel".equals(nameOfPartNumberLabel)){
            iRowFirstPart = reference.getRow() + 5;
        }
        HSSFRow rowFirstPart = sheet.getRow(iRowFirstPart);        
        return rowFirstPart;
    }
    
	/**
	 * get the last part row in the exported quotes sheet.
	 */
    protected HSSFRow getLastPartRow(SpreadSheetQuote quote,String nameOfSubtotalPrice) throws Exception{
    	int index = workbook.getNameIndex(nameOfSubtotalPrice);
    	HSSFName name = null;
    	if(index != -1){
    		name = workbook.getNameAt(index);
    	}else{
    		if(nameOfSubtotalPrice.equals("apSaasSubtotalBidTCV")){
    			return this.getFirstPartRow("apSaasPartNumberLabel");
    		}
    	}
        CellReference reference = new CellReference(name.getReference());
        HSSFSheet sheet = workbook.getSheet(reference.getSheetName());
        int iRow =reference.getRow() - 1;
        
        // for  SQO PA PAE  quote, SW Exported Parts & Pricing tab has one more row "Equity Curve discount guidance - Total".
        if(!quote.isPGSFlag() && "epSubtotalPrice".equals(nameOfSubtotalPrice) && (quote.isPA() || quote.isPAE()) ){
        	if(quote.isFCTTOPA()){
        		iRow = reference.getRow() - 1 ;
        	}
        	else{
        		iRow = reference.getRow() - 2 ;
        	}
        		
        }
               
        HSSFRow row = sheet.getRow(iRow);
        
        return row;
    }
   
	/**
	 * copy cell styles from source row to the target row
	 */
	protected void copyRowStyle(HSSFSheet sheet, HSSFRow sourceRow, HSSFRow targetRow) throws Exception{
		// copy height of row
		targetRow.setHeight(sourceRow.getHeight());
		
		//copy cell by cell
		for (int i = sourceRow.getFirstCellNum(); i < sourceRow.getLastCellNum(); i++) {
			HSSFCell sourceCell = sourceRow.getCell(i);
			if (sourceCell == null) {
				continue;
			}
			HSSFCell targetCell = targetRow.createCell(i);
			targetCell.setCellStyle(sourceCell.getCellStyle());
			int cType = sourceCell.getCellType();
			targetCell.setCellType(cType);
		}
	}

	/**
	 * Set the cells on the repeated part record in the spreadsheet to be read-only
	 */
	protected void setCellReadOnly(HSSFCell cell){
        HSSFCellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(cell.getCellStyle());
        style.setLocked(true);
        style.setFillForegroundColor(HSSFColor.WHITE.index);
        cell.setCellStyle(style);	    
	}
	
	/**
	 * Set the cells right-border in the spreadsheet to be black color
	 */
	protected void setCellRightBorderBlack(HSSFCell cell){
        HSSFCellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(cell.getCellStyle());
        style.setRightBorderColor(HSSFColor.BLACK.index);
        cell.setCellStyle(style);	    
	}
	
	/**
	 * Set the borders of the cell is grey
	 * @param cell
	 * @param isTopBorderBlack
	 * @param verticalAlign VERTICAL_TOP = 0, VERTICAL_CENTER = 1, VERTICAL_BOTTOM = 2, VERTICAL_JUSTIFY = 3
	 * @param align ALIGN_LEFT = 1, ALIGN_CENTER = 2, ALIGN_RIGHT = 3
	 */
	protected void setCellBorderGrey(HSSFCell cell, boolean isTopBorderBlack, int verticalAlign, int align){
        HSSFCellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(cell.getCellStyle());
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBottomBorderColor(HSSFColor.GREY_25_PERCENT.index);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setLeftBorderColor(HSSFColor.GREY_25_PERCENT.index);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setRightBorderColor(HSSFColor.GREY_25_PERCENT.index);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        if(!isTopBorderBlack) {
        	style.setTopBorderColor(HSSFColor.GREY_25_PERCENT.index);
        }
        
        if(verticalAlign >= 0) {
        	style.setVerticalAlignment((short)verticalAlign);
        }
        
        if(align >= 0) {
        	style.setAlignment((short)align);
        }
        
        cell.setCellStyle(style);	    
	}
	
	/**
	 * @param cell
	 * @param verticalAlign VERTICAL_TOP = 0, VERTICAL_CENTER = 1, VERTICAL_BOTTOM = 2, VERTICAL_JUSTIFY = 3
	 * @param align ALIGN_LEFT = 1, ALIGN_CENTER = 2, ALIGN_RIGHT = 3
	 */
	protected void setCellTextAlign(HSSFCell cell, int verticalAlign, int align) {
		HSSFCellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(cell.getCellStyle());
        style.setVerticalAlignment((short)verticalAlign);
        style.setAlignment((short)align);
        
        cell.setCellStyle(style);
	}
	
	protected void setCellTopBorder(HSSFRow row, int firstIndex, int lastIndex) {
		for(int i=firstIndex; i<=lastIndex; i++) {
			setCellBorderGrey(row.getCell(i), false, -1, -1);
		}
	}
	
	/**
	 * 
	 * Takes in a 0-based base-10 column and returns a ALPHA-26 representation.
	 */
	protected String convertNumToColString(int col){
		int excelColNum = col + 1;

		String colRef = "";
		int colRemain = excelColNum;

		while (colRemain > 0) {
		  int thisPart = colRemain % 26;
		  if (thisPart == 0) thisPart = 26;
		  colRemain = (colRemain - thisPart) / 26;

		  char colChar = (char)(thisPart + 64);
		  colRef = colChar + colRef;
		}
		return colRef;
	  }
	
	/**
	 * calculate column total value
	 * @param hssfSheet -Sheet object
	 * @param cellRowIndex
	 * @param colIndex
	 * @param rowIndexOfFirstPart
	 * @param rowIndexOfLastPart
	 */
	protected void calculateColumnTotalValue(HSSFSheet hssfSheet, int cellRowIndex, int colIndex, 
			int rowIndexOfFirstPart, int rowIndexOfLastPart) {
		//Update formula for BidTCV, e.g. SUM(J11:J13)
		HSSFCell cellOfepSubtotalPoints = hssfSheet.getRow(cellRowIndex).getCell(colIndex);
		if(cellRowIndex==rowIndexOfFirstPart&&rowIndexOfFirstPart==rowIndexOfLastPart){
		    cellOfepSubtotalPoints.setCellFormula("\"\"");
		}else{
			String stringFormulaOfepSubtotalPoints = 
			    "SUM(" + new CellReference(rowIndexOfFirstPart,colIndex).formatAsString()
			    + ":" + new CellReference(rowIndexOfLastPart,colIndex).formatAsString() + ")";
			cellOfepSubtotalPoints.setCellFormula(stringFormulaOfepSubtotalPoints);
		}
	}
	
	/**
	 * Spreedsheet some columns: PA colIndex-1 = (FCT or PAE) colIndex;
	 */
	protected int calculateColumnIndexByQuoteType(SpreadSheetQuote quote, int index) {
		return quote.isPA() || quote.isPAE()?index:index-1;
	}
	
	// data validation for discount percent, between 0 and 1
	protected void setCellDataValidations(HSSFSheet sheet, int firstPartIndex
			, int lastPartIndex, int startColumnIndex, int endColumnIndex) {
		CellRangeAddressList addressList = new CellRangeAddressList(
				firstPartIndex, lastPartIndex, startColumnIndex, endColumnIndex);
		DVConstraint dvConstraint = DVConstraint.createNumericConstraint(
			    DVConstraint.ValidationType.DECIMAL,
			    DVConstraint.OperatorType.BETWEEN, "0", "1");
		HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
		
		dataValidation.setErrorStyle(HSSFDataValidation.ErrorStyle.STOP);
		dataValidation.createErrorBox("Number is not a percentage", "Please enter a percentage");
		sheet.addValidationData(dataValidation);
	}
	
	//create hyperlinks
	protected void setHyperlink(HSSFCell cell, String link) {
		HSSFHyperlink hyperlink = new HSSFHyperlink(HSSFHyperlink.LINK_URL);
		hyperlink.setAddress(link);
	    cell.setHyperlink(hyperlink);
	}
	
	protected void setDataFormat(HSSFCell cell, boolean decimal4Flag){
        HSSFCellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(cell.getCellStyle());
        if(!decimal4Flag) {
        	style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
        }
        cell.setCellStyle(style);	    
	}
	
	protected void parseDoubleAndReplaceCommaForCellValue(HSSFCell cell, String sDouble) {
		if(StringUtils.isBlank(sDouble)) {
			cell.setCellValue(new HSSFRichTextString(StringUtils.EMPTY));
		} else {
			cell.setCellValue(Double.parseDouble(sDouble.replaceAll(",", "")));
		}
	}
	
	protected Double parseDoubleAndReplaceComma(String sDouble) {
		if(!StringUtils.isBlank(sDouble)) {
			return Double.parseDouble(sDouble.replaceAll(",", ""));
		}
		
		return 0d;
	}
	
	protected void setCellValueBlank(HSSFCell cell) {
		cell.setCellValue(new HSSFRichTextString(StringUtils.EMPTY));
	}
	
	/**
	 * addDropDownListValidation
	 * @param sheet HSSFSheet
	 * @param rangeAddressArray - new CellRangeAddressList(int firstRow, int lastRow, int firstCol, int lastCol)
	 * @param dataList data for drop down list
	 */
	protected void addDropDownListValidation(HSSFSheet sheet, int[] rangeAddressArray, String[] dataList) {
		  CellRangeAddressList addressList = 
			  	new CellRangeAddressList(rangeAddressArray[0], rangeAddressArray[1], rangeAddressArray[2], rangeAddressArray[3]);
		  DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(dataList);
		  HSSFDataValidation dataValidation = new HSSFDataValidation
		    (addressList, dvConstraint);
		  dataValidation.setSuppressDropDownArrow(false);
		  sheet.addValidationData(dataValidation);
	}
	
}
