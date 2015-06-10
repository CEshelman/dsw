package com.ibm.dsw.quote.newquote.process;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.PartsPricingConfigurationFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem_Impl;
import com.ibm.dsw.quote.common.domain.QuoteReasonFactory;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.common.domain.SalesRepFactory;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.common.util.spbid.SpecialBidRule;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcess;
import com.ibm.dsw.quote.configurator.process.ConfiguratorPartProcessFactory;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcess;
import com.ibm.dsw.quote.draftquote.process.PartPriceProcessFactory;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.export.exception.ExportQuoteException;
import com.ibm.dsw.quote.massdlgtn.process.MassDlgtnProcessFactory;
import com.ibm.dsw.quote.newquote.config.NewQuoteDBConstants;
import com.ibm.dsw.quote.newquote.config.NewQuoteMessageKeys;
import com.ibm.dsw.quote.newquote.exception.NewQuotHasBeenLockedException;
import com.ibm.dsw.quote.newquote.exception.NewQuotHasBeenModifiedException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteExistBothSWSaaSPartsException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidAcquisitionException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidCntryCurrencyException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidCntryException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidCustCurrencyException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidLOBException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidStardEndDateException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidStartEndDateDurationException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteInvalidTermException;
import com.ibm.dsw.quote.newquote.exception.NewQuoteUnSupportedLOBException;
import com.ibm.dsw.quote.newquote.spreadsheet.Configurator;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetPart;
import com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetQuote;
import com.ibm.dsw.quote.ps.domain.PartSearchPart;
import com.ibm.dsw.quote.ps.domain.PartSearchResult;
import com.ibm.dsw.quote.ps.process.PartSearchProcess;
import com.ibm.dsw.quote.ps.process.PartSearchProcessFactory;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * This <code>UploadSalesQuoteProcess_Impl<code> class is an implementation of
 * <code>UploadSalesQuoteProcess<code>
 *
 * @author: cgang@cn.ibm.com
 *
 * Creation date: 2007-3-14
 */
public abstract class UploadSalesQuoteProcess_Impl extends  TopazTransactionalProcess implements UploadSalesQuoteProcess {

    public static final String dateFromat = "yyyy-MM-dd";

    public static final String dateTimeFromat = "yyyy-MM-dd HH:mm";

    public static final String optionalDateFormat = "dd-MMM-yyyy";

    private Customer cust = null;

	private List<Configurator> configuratorList = new ArrayList<Configurator>();

	private HashMap<String, String> partNumConfigrtnIdMap = new HashMap<String, String>();

	private HashMap<String, List<String>> configrtnIdPartNumlistMap = new HashMap<String, List<String>>();

    /* (non-Javadoc)
     * @see com.ibm.dsw.quote.newquote.process.UploadSalesQuoteProcess#importSpreadSheetQuote(com.ibm.dsw.quote.newquote.spreadsheet.SpreadSheetQuote, java.lang.String)
     */
    public abstract void importSpreadSheetQuote(SpreadSheetQuote squote,  String creatorId) throws NewQuoteInvalidCntryCurrencyException, NewQuoteInvalidCustCurrencyException, NewQuoteInvalidLOBException, NewQuoteInvalidCntryException, NewQuoteInvalidAcquisitionException, QuoteException ;

    /**Update line item sourced from renwal quote */
    public abstract void updateValidRenewalLineItem(SpreadSheetQuote squote, String webQuoteNum)  throws QuoteException;

    public abstract void stripOutSaaSPartsFromInvalidParts(SpreadSheetQuote squote) throws QuoteException;

    public abstract List searchSaaSPartsByNumber(SpreadSheetQuote squote) throws QuoteException;

    public abstract void stripOutSWPartsFromInvalidParts(SpreadSheetQuote squote) throws QuoteException;

    public abstract void updateApplianceLineItem(String webQuoteNum, List appliancePartNumList) throws QuoteException;
    /**
     * @param squote
     * @param creatorId
     * @param lob
     * @param acquisition
     * @param cntry
     * @throws NewQuoteInvalidLOBException
     * @throws NewQuoteInvalidCntryException
     * @throws QuoteException
     */

    protected boolean validateImportOption(SpreadSheetQuote squote, String userId) throws QuoteException {

        LogContext logger = LogContextFactory.singleton().getLogContext();
        boolean newQuote = true;
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        String webQuoteNum = squote.getWebQuoteNum();
        // check if the web quote num exists in spreadsheet
        if (StringUtils.isNotBlank(webQuoteNum)){
            // check if user selects to create a new quote
            if (squote.isToImportNewQuote()) {
                newQuote = true;
            } else {
                try {
                    QuoteHeader qHeader = qProcess.getQuoteHdrInfoByWebQuoteNumAndUserId(webQuoteNum, userId);
                    boolean isEditor = isEditor(userId, qHeader);
                    if (isEditor
                            && (QuoteConstants.QUOTE_STAGE_CODE_SESSION.equals(StringUtils.trimToEmpty(qHeader
                                    .getQuoteStageCode())) || QuoteConstants.QUOTE_STAGE_CODE_SESSION
                                    .equals(StringUtils.trimToEmpty(qHeader.getQuoteStageCode())))) {
                        //check if existing quote alraeady locked by another user
                        if (qHeader.isLockedFlag()) {
                            squote.setLocked(true);
                            squote.setLockedBy(qHeader.getLockedBy());
                            // check if user selects to unlock the existing quote
                            if (squote.isToUnlockQuote()){
                                newQuote = false;
                            } else {
                                // throw exception to display quote already locked message
                                throw new NewQuotHasBeenLockedException("This quote " + webQuoteNum + "has been locked");
                            }
                        } else {
                            // check if date/timestamp in spreadsheet match that in SQO for the quote
                            if (StringUtils.isNotBlank(squote.getModDate()) && qHeader.getModDate() != null ) {
                                java.util.Date spreadsheetDate = DateUtil.parseUtilDate(squote.getModDate(), dateTimeFromat);
                                java.util.Date db2Date = DateUtil.parseUtilDate(qHeader.getModDate().toString(), dateTimeFromat);

                                logger.debug(this, "The mod date from spreadsheet is: " + spreadsheetDate);
                                logger.debug(this, "The mod date from db2 is: " + db2Date);

                                if (spreadsheetDate.compareTo(db2Date)!=0){
                                    throw new NewQuotHasBeenModifiedException("This quote " + webQuoteNum + "has been modified");
                                } else {
                                    newQuote = false;
                                }
                            } else {
                                newQuote = true;
                            }
                        }
                    } else {
                        newQuote = true;
                    }
                } catch (NoDataException e) {
                    newQuote = true;
                }
            }
        } else {
            newQuote = true;
        }
        return newQuote;
    }

    protected void validateCustAndCtrct(SpreadSheetQuote squote,String creatorId)
    				throws NewQuoteInvalidLOBException, NewQuoteInvalidCntryException,NewQuoteInvalidCntryCurrencyException, NewQuoteInvalidCustCurrencyException, QuoteException {

        LogContext logger = LogContextFactory.singleton().getLogContext();

        try {
            /* Validate the customer number if custNum is provided */
            if (StringUtils.isNotBlank(squote.getSiteNum())) {
                cust = validateCustomer(squote);
                squote.setCustValid(null != cust);
            } else {
                squote.setCustValid(true);
            }

            String sapCtrctNum = StringHelper.fillString(squote.getSapCtrctNum());
            /* Validate the sap contract number if sapCtrctNum is provided */
            if (StringUtils.isNotBlank(sapCtrctNum)) {
                if (null != cust) {
                    List ctrctList = cust.getContractList();

                    if (ctrctList==null || ctrctList.size()==0){
                        squote.setSapCtrctNumValid(false);
                    }

                    for(Iterator ctrctIt = ctrctList.iterator(); ctrctIt.hasNext();) {
                        Contract ctrct = (Contract)ctrctIt.next();
                        String ctrctNum = StringHelper.fillString(ctrct.getSapContractNum());

                        if(sapCtrctNum.equals(ctrctNum)) {
                            squote.setSapCtrctNumValid(true);
                            break;
                        } else {
                            squote.setSapCtrctNumValid(false);
                        }
                    }
                } else {
                    //Suppose a ctrct num must associate with a customer.
                    squote.setSapCtrctNumValid(false);
                }
            } else {
                squote.setSapCtrctNumValid(true);
            }

        } catch (Exception te) {
            logger.error(this, "Error in validating spreadsheet data " + te.getMessage());
            throw new QuoteException(te);
        }

    }

    /**
     * @param squote
     * @param creatorId
     * @throws TopazException
     * @throws QuoteException
     */
    protected SpreadSheetQuote validateSWPartNumList(SpreadSheetQuote squote) throws QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        List partNumList = new ArrayList();
        Map partNumBrandCodeMap = new HashMap();
        List eqPartList = squote.getEqPartList();
        List appliancePartNums=new ArrayList();
        if(null != eqPartList && !eqPartList.isEmpty()){

            for(Iterator it = eqPartList.iterator(); it.hasNext();){
                SpreadSheetPart ssPart = (SpreadSheetPart)it.next();
                if(!partNumList.contains(ssPart.getEpPartNumber())){
                    partNumList.add(ssPart.getEpPartNumber());
                }
            }
            logger.debug(this, "part numbers from coming spreadsheet: " + partNumList);
            PartSearchProcess partProcess;

            try {
                partProcess = PartSearchProcessFactory.singleton().create();
                PartSearchResult selectedParts = partProcess.searchPartsByNumber(squote.getPartNums(), StringUtils.EMPTY, squote.getLobCode(),
                		squote.getAcquisition(), squote.getCntryCode(), NewQuoteDBConstants.AUDIENCE);
                for( Iterator partIt = selectedParts.getParts().iterator();partIt.hasNext();) {
                    PartSearchPart part = (PartSearchPart)partIt.next();

                    if(part.isApplianceFlag()){
                    	appliancePartNums.add(part.getPartID());
                    }
                    String partNum = StringUtils.trimToEmpty(part.getPartID());
                    //remove valid partNum
                    partNumList.remove(partNum);
                    //copy brand code from db to spreadsheet parts
                    String brandCode = part.getBrandID();
                    logger.debug(this, partNum + " <---> brandCode : " + brandCode);
                    partNumBrandCodeMap.put(partNum,brandCode);

                }
                squote.setInvalidPartList(partNumList);

                List invalidPartList = squote.getInvalidPartList();
                for(Iterator it = eqPartList.iterator(); it.hasNext();){
                    SpreadSheetPart ssPart = (SpreadSheetPart)it.next();
                    String epPartNum = ssPart.getEpPartNumber();

                    if(appliancePartNums.contains(epPartNum)){
                    	squote.addAppliancePart(ssPart);
                    }

                   if(invalidPartList.contains(epPartNum)) {
                   		continue;
                   } else {
                   	ssPart.setBrandCode(partNumBrandCodeMap.get(epPartNum).toString());
                   	squote.addToValidPartList(ssPart);
                   }

                }

                stripOutSaaSPartsFromInvalidParts(squote);

                logger.debug(this, "invalid part numbers from coming spreadsheet: " + partNumList);
            } catch (Exception e) {
                 throw new QuoteException(e);
            }
        }
        return squote;
    }

    protected SpreadSheetQuote validateSaaSPartNumList(SpreadSheetQuote squote) throws QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        List partNumList = new ArrayList();
        //Map partNumBrandCodeMap = new HashMap();
        List eqSaaSPartList = squote.getSaaSEqPartList();
        if(null != eqSaaSPartList && !eqSaaSPartList.isEmpty()){
        	HashMap<String, Boolean> duplicatedPartNums = new HashMap<String, Boolean>();
            for(Iterator it = eqSaaSPartList.iterator(); it.hasNext();){
                SpreadSheetPart ssPart = (SpreadSheetPart)it.next();
                String partNum = ssPart.getEpPartNumber();
                //remove duplicate partNum
                if (duplicatedPartNums.containsKey(partNum)) {
            		duplicatedPartNums.put(partNum, true);
            		partNumList.remove(partNum);
            		if(StringUtils.isEmpty(ssPart.getRampUpTitle())) {
            			squote.addToDuplicateSaaSPartList(partNum);
            		}
            		continue;
            	}
            	duplicatedPartNums.put(partNum, false);
            	partNumList.add(partNum);
            }
            logger.debug(this, "part numbers from coming spreadsheet: " + partNumList);

	        try {
	        	List validPartNumList = searchSaaSPartsByNumber(squote);
	            if(validPartNumList != null){
		        	for( Iterator partIt = validPartNumList.iterator();partIt.hasNext();) {
		                String  partNum= (String)partIt.next();
		                //remove valid partNum
		                partNum = StringUtils.trimToEmpty(partNum);
		                partNumList.remove(partNum);

		                //copy brand code from db to spreadsheet parts
		//                        String brandCode = part.getBrandID();
		//                        logger.debug(this, partNum + " <---> brandCode : " + brandCode);
		//                        partNumBrandCodeMap.put(partNum,brandCode);

		            }
	            }
	            squote.setInvalidSaaSPartList(partNumList);

	            List invalidSaaSPartList = squote.getInvalidSaaSPartList();
	            List duplicateSaaSPartNumList = squote.getDuplicateSaaSPartNumList();
	            List inactiveSaaSPartNumList = squote.getInactiveSaaSPartNumList();
	            List uncertifiedSaaSPartNumList = squote.getUncertifiedSaaSPartNumList();
	            for(Iterator it = eqSaaSPartList.iterator(); it.hasNext();){
	                SpreadSheetPart ssPart = (SpreadSheetPart)it.next();
	                String epSaaSPartNum = ssPart.getEpPartNumber();
	               if(invalidSaaSPartList.contains(epSaaSPartNum) || duplicateSaaSPartNumList.contains(epSaaSPartNum) || inactiveSaaSPartNumList.contains(epSaaSPartNum) || uncertifiedSaaSPartNumList.contains(epSaaSPartNum)) {
	               		continue;
	               } else {
	               	//ssPart.setBrandCode(partNumBrandCodeMap.get(epPartNum).toString());
	               	squote.addToValidSaaSPartList(ssPart);
	               }
	            }

	            stripOutSWPartsFromInvalidParts(squote);

	            logger.debug(this, "invalid part numbers from coming spreadsheet: " + partNumList);
	        } catch (Exception e) {
	             throw new QuoteException(e);
	        }
        }
        return squote;
    }

    /**
     * @param squote
     * @return Customer
     * @throws TopazException
     * @throws QuoteException
     */
    protected Customer validateCustomer(SpreadSheetQuote squote) throws TopazException, QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();

        String siteNum = squote.getSiteNum();
        logger.debug(this, "finding customer.... site Number: " + siteNum);
        CustomerProcess custProcess = CustomerProcessFactory.singleton().create();
        SearchResultList resultList = custProcess.searchCustomerByDswIcnRcdID(squote.getSapCtrctNum(), siteNum, squote
                .getCntryCode(), squote.getLobCode(), 0, false, squote.getProgMigrtnCode(), QuoteConstants.QUOTE_AUDIENCE_CODE_SQO, "");
        List custList = resultList.getResultList();
        if(null != custList && custList.size() >= 1) {
            Customer cust = (Customer) custList.get(0);
            logger.debug(this, "find matched customer... " + cust);
            return cust;
        }else {
            StringBuffer logBuffer = new StringBuffer("Can not find the unique custmer. Input params:" + "\n");
            logBuffer.append("site number: " + squote.getSiteNum() + "\n");
            logBuffer.append("Cntry code: " +  squote.getCntryCode() + "\n");
            logBuffer.append("SAP ctrct number: " + squote.getSapCtrctNum() + "\n");
            logBuffer.append("LOB code: " + squote.getLobCode() + "\n");
            logBuffer.append("Will return a null object.");
            logger.info(this, logBuffer.toString());
            return null;
        }
    }

    /**
     * @param squote
     * @throws QuoteException
     */
    protected CodeDescObj validateAcquisition(SpreadSheetQuote squote) throws QuoteException {
        CodeDescObj acquisition = null;
        if(squote.isFCT()){
            CacheProcess cProcess = CacheProcessFactory.singleton().create();
            acquisition = cProcess.getAcquisitionByCode(squote.getAcquisition());
            if (acquisition == null)
                throw new NewQuoteInvalidAcquisitionException("invalid Acquisition code: " + squote.getAcquisition());
        }
        return acquisition;
    }

    /**
     * @param squote
     * @param cntry
     * @throws NewQuoteInvalidCntryException
     */
    protected void validateCurrency(SpreadSheetQuote squote) throws NewQuoteInvalidCntryCurrencyException, NewQuoteInvalidCustCurrencyException, QuoteException {
    	LogContext logger = LogContextFactory.singleton().getLogContext();
    	String currencyCode = squote.getCurrency();
    	if(cust != null ) {
    		if(currencyCode.equals(StringUtils.trimToEmpty(cust.getCurrencyCode()))) {
    			return;
    		} else {
    			logger.error(this,"Invaid Cust Currency " + currencyCode);
    		    throw new NewQuoteInvalidCustCurrencyException("Invaid Cust Currency " + currencyCode);
    		}
    	} else {
    		CacheProcess cProcess = CacheProcessFactory.singleton().create();
            Country cntry =  cProcess.getCountryByCode3(squote.getCntryCode());
            if(StringUtils.isNotBlank(currencyCode)) {
                List currencyList = cntry.getCurrencyList();
                for(Iterator it = currencyList.iterator(); it.hasNext();) {
                    CodeDescObj currency = (CodeDescObj)it.next();
                    if(currency.getCode().equals(currencyCode))
                        return;
                }
            }
            logger.error(this,"Invaid CntryCurrency " + currencyCode);
            throw new NewQuoteInvalidCntryCurrencyException("Invaid CntryCurrency " + currencyCode);
    	}
    }

    /**
     * @param spreadSheetQuote
     * @throws QuoteException
     */
    protected CodeDescObj validateLOB(SpreadSheetQuote spreadSheetQuote) throws QuoteException {
        CacheProcess cProcess = CacheProcessFactory.singleton().create();
        CodeDescObj lob =  cProcess.getLOBByCode(spreadSheetQuote.getLobCode());
        if (lob == null)
            throw new NewQuoteInvalidLOBException(
                    "invalid LOB code: "  + spreadSheetQuote.getLobCode());
        if(QuoteConstants.LOB_SSP.equals(spreadSheetQuote.getLobCode())) {
            throw new NewQuoteUnSupportedLOBException(
                    "unsupported LOB code: "  + spreadSheetQuote.getLobCode());
        }
        if (spreadSheetQuote.isPGSFlag()){
        	if(spreadSheetQuote.getLobCode().equals(QuoteConstants.LOB_PA) || spreadSheetQuote.getLobCode().equals(QuoteConstants.LOB_PAE )) {
        	}else{
    			throw new NewQuoteUnSupportedLOBException(
    					"unsupported LOB code in PGS: "  + spreadSheetQuote.getLobCode());
        	}
        }
        return lob;
    }

    protected void validateStartEndDates(SpreadSheetQuote squote) throws QuoteException {
        List eqPartList = squote.getEqPartList();
        if(null != eqPartList && !eqPartList.isEmpty()){

            for(Iterator it = eqPartList.iterator(); it.hasNext();){
                SpreadSheetPart ssPart = (SpreadSheetPart)it.next();
                String overridedStartDate =  ssPart.getEpOverrideDatesStartDate();
                Date startDate = null;
                String standardStartDate = ssPart.getEpSTDStartDate();
                if(StringUtils.isNotBlank(standardStartDate)) {
                    startDate = DateUtil.parseDate(standardStartDate, dateFromat, optionalDateFormat);
                }
                if(StringUtils.isNotBlank(overridedStartDate)) {
                    startDate = DateUtil.parseDate(overridedStartDate, dateFromat, optionalDateFormat);
                }
                Date endDate = null;
                String overridedEndDate =  ssPart.getEpOverideDatesEndDate();
                String standardEndDate = ssPart.getEpSTDEndDate();
                if(StringUtils.isNotBlank(standardEndDate)) {
                    endDate = DateUtil.parseDate(standardEndDate, dateFromat, optionalDateFormat);
                }
                if(StringUtils.isNotBlank(overridedEndDate)) {
                    endDate = DateUtil.parseDate(overridedEndDate, dateFromat, optionalDateFormat);
                }

                //make sure the overridden start/end date have valid format
                if ((StringUtils.isNotBlank(overridedStartDate) && startDate == null)
                        || (StringUtils.isNotBlank(overridedEndDate) && endDate == null)) {
                    throw new NewQuoteInvalidStardEndDateException("Invalid start/end date format", NewQuoteMessageKeys.INVALID_START_END_DATE_FORMAT);
                }

                //make sure the start date is before the end date
                if (startDate!=null && endDate!=null && endDate.before(startDate)){
                    throw new NewQuoteInvalidStardEndDateException("Invaid start/end dates", NewQuoteMessageKeys.INVALID_START_END_DATE);
                }

                //The FCT team uses this process to set non-standard dates and we have left this in place as a work around for them
                if(StringUtils.isBlank(overridedEndDate)) {
                    endDate = null;
                }
                //make sure the duration coverage is not more than 12 months
                if (DateUtil.calculateFullCalendarMonths(startDate, endDate) > 12){
                    throw new NewQuoteInvalidStartEndDateDurationException("Invaid start/end dates");
                }
                // Check backdating
                if (StringUtils.isNotBlank(overridedStartDate) && DateUtil.isDateBeforeToday(startDate) && DateUtil.calculateFullCalendarMonths(startDate,DateUtil.getCurrentDate())/12 > PartPriceConfigFactory.singleton().getBackDatingPastYearLimit(squote.getLobCode()) ){
                    throw new NewQuoteInvalidStardEndDateException("Invalid Backdating", NewQuoteMessageKeys.INVALID_START_END_DATE);
                }
            }

        }

    }

    protected void validateFrequenciesAndTerms(SpreadSheetQuote squote) throws QuoteException {
    	List eqSaaSPartList = squote.getSaaSEqPartList();
    	List invalidFrequencyPartNumList = squote.getInvalidFrequencyPartNumList();
        if(null != eqSaaSPartList && !eqSaaSPartList.isEmpty()){

            for(Iterator it = eqSaaSPartList.iterator(); it.hasNext();){
                SpreadSheetPart ssPart = (SpreadSheetPart)it.next();
                Integer term = null;
                if (ssPart.getContractTerm().contains(QuoteConstants.TERM_SUFFIX)) {
                	int end = ssPart.getContractTerm().indexOf(QuoteConstants.TERM_SUFFIX);
                	term = new Integer(ssPart.getContractTerm().substring(0, end));
                } else {
                	if (ssPart.getContractTerm().contains(".0")) {
                		int end = ssPart.getContractTerm().indexOf(".0");
                    	term = new Integer(ssPart.getContractTerm().substring(0, end));
                	} else {
                		term = StringUtils.isNotBlank(ssPart.getContractTerm()) ? new Integer(ssPart.getContractTerm()): null;
                	}
                }

                String frequency = ssPart.getBillingFrequency();

                //validate term
                if (null != term) {
                	if (1 > term.intValue() || 60 < term.intValue()) {
                    	throw new NewQuoteInvalidTermException("Invalid term");
                    }
                }

                //validate frequency
                if (!"".equals(ssPart.getBillingFrequency())) {
                    	if (PartPriceConstants.BillingOptionCode.ANNUAL.equals(ssPart.getBillingFrequency().substring(0, 1))) {
                    	} else if (PartPriceConstants.BillingOptionCode.MONTHLY.equals(ssPart.getBillingFrequency().substring(0, 1))) {
                    	} else if (PartPriceConstants.BillingOptionCode.QUARTERLY.equals(ssPart.getBillingFrequency().substring(0, 1))) {
                    	} else if (PartPriceConstants.BillingOptionCode.UPFRONT.equals(ssPart.getBillingFrequency().substring(0, 1))) {
                    	} else {
                    		invalidFrequencyPartNumList.add(ssPart.getEpPartNumber());
                    	}
                    } else if (ssPart.isSaasSubscrptnPart()) {
                    	invalidFrequencyPartNumList.add(ssPart.getEpPartNumber());
                    }
                }
            squote.setInvalidFrequencyPartNumList(invalidFrequencyPartNumList);
        }
    }

    protected void validateBothSWSaaSParts(SpreadSheetQuote squote) throws QuoteException {
    	List eqSaaSPartList = squote.getSaaSEqPartList();
    	List eqPartList = squote.getEqPartList();
        if(null != eqSaaSPartList && !eqSaaSPartList.isEmpty() && null != eqPartList && !eqPartList.isEmpty()){
        	throw new NewQuoteExistBothSWSaaSPartsException("Invalid parts");
        }
    }

    /**
     * @param spreadSheetQuote
     * @return Country
     * @throws QuoteException
     */
    protected Country validateCountry(SpreadSheetQuote spreadSheetQuote) throws QuoteException {
        CacheProcess cProcess = CacheProcessFactory.singleton().create();
        Country cntry =  cProcess.getCountryByCode3(spreadSheetQuote.getCntryCode());
        if(cntry == null)
            throw new NewQuoteInvalidCntryException(
                    "invalid cntry code: " + spreadSheetQuote.getCntryCode());
        return cntry;
    }

    /**
     * clear customer and contract infor when ICN, customer num or contract number is invalid
     * @param webQuoteNum
     * @throws QuoteException
     */
    protected void cleanCustomerInfo(String webQuoteNum, String currency, String creatorId) throws QuoteException {
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        qProcess.updateQuoteHeaderCustInfo(creatorId, webQuoteNum, StringUtils.EMPTY,StringUtils.EMPTY, -1, currency,null);
    }

    /**
     * @param webQuoteNum
     * @throws QuoteException
     */
    protected void deleteExistingLineItems(String webQuoteNum) throws QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();

        try {
            this.beginTransaction();
            List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(webQuoteNum);
            for(Iterator it = lineItemList.iterator(); it.hasNext();) {
                QuoteLineItem lineItem = (QuoteLineItem)it.next();
                logger.debug(this, "clear lineItem: " + lineItem.getSeqNum());
                lineItem.delete();
            }
            this.commitTransaction();
        } catch (TopazException e) {
            logger.error(logger, "faied to clear existing line item for quote: " + webQuoteNum);
            logger.error(logger, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

    }

    /**
     * @param webQuoteNum
     * @throws QuoteException
     */
    protected void deleteExistingConfigrtnInfo(String webQuoteNum) throws QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();

        try {
            this.beginTransaction();
            List confgrtnList = PartsPricingConfigurationFactory.singleton().findPartsPricingConfiguration(webQuoteNum);
            for(Iterator it = confgrtnList.iterator(); it.hasNext();) {
            	PartsPricingConfiguration confgrtn = (PartsPricingConfiguration) it.next();
                logger.debug(this, "clear configuration: " + confgrtn.getConfigrtnId());
                confgrtn.delete();
            }
            this.commitTransaction();
        } catch (TopazException e) {
            logger.error(logger, "faied to clear existing configuration for quote: " + webQuoteNum);
            logger.error(logger, LogThrowableUtil.getStackTraceContent(e));
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }

    }

    /**
     * @param squote
     * @param creatorId
     * @throws QuoteException
     */
    protected void processAsExportedQuote(SpreadSheetQuote squote, String userId, boolean newQuote) throws QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
        String webQuoteNum = squote.getWebQuoteNum();
        //if the quote is locked, unlock it
        if (squote.isLocked()){
            qProcess.unlockQuote(webQuoteNum, userId);
        }
        //For FCT to PA migration quote, convert renewal quote to sales quote
        if (squote.isFCTTOPA()) {
            qProcess.convertToSalsQuote("", webQuoteNum, false);
        }

        logger.debug(this, "processAsExportedQuote : " + webQuoteNum + " userId: " + userId
                + " userId " + userId);
        if (!squote.isCustValid() || !squote.isSapCtrctNumValid()) {
            logger.debug(this, " site number " + (squote.isCustValid() ? "is valid" : "is not valid"));
            logger.debug(this, " Sap ctrct number : " + (squote.isSapCtrctNumValid() ? "is valid" : "is not valid"));
            logger.debug(this, " clean cust infor, web quote number is : " + squote.getWebQuoteNum());
            cleanCustomerInfo(webQuoteNum, squote.getCurrency(), userId);
        } else {
            String siteNum = squote.getSiteNum();
            String sapCtrctNum = squote.getSapCtrctNum();

            int webCustId = -1;//always a existing cust
            String currency = squote.getCurrency();

            logger.debug(this, "update QuoteHeader cust info");
            logger.debug(this, "exportedQuoteOwner : " + userId);
            logger.debug(this, "customerNum : " + siteNum);
            logger.debug(this, "sapCtrctNum : " + sapCtrctNum);
            logger.debug(this, "webCustId : " + webCustId);
            logger.debug(this, "currency : " + currency);
            qProcess.updateQuoteHeaderCustInfo(userId, webQuoteNum, siteNum, sapCtrctNum, webCustId,
                    currency,null);
        }

        //Delete line items for this quote
        deleteExistingLineItems(webQuoteNum);

        //re-create
        addLineItemsToQuote(webQuoteNum, squote, userId);
        addSaaSLineItemsToQuote(webQuoteNum, squote, userId, newQuote);

        //Copy the draft quote to a new sesson quote
        qProcess.loadDraftQuoteToSession(webQuoteNum, userId, false);
    }


    /**
     * create new quote line items based on spreadsheet parts and update line
     * items sourced from renewal quote.
     *
     * @param webQuoteNum
     * @param squote
     * @throws QuoteException
     */
    protected void addLineItemsToQuote(String webQuoteNum, SpreadSheetQuote squote, String userID) throws QuoteException {
        boolean isPA = squote.isPA();
        boolean isPAE = squote.isPAE();

        LogContext logger = LogContextFactory.singleton().getLogContext();

        /** Validate partNum list */
        squote = validateSWPartNumList(squote);

        try {
            this.beginTransaction();

            List partList = squote.getValidPartList();

            logger.debug(this, "valid lineItems on spreadsheet: " + partList.size());

            int maxPartQty = PartPriceConfigFactory.singleton().getElaLimits();
            //if there are more than 250 valid line items, set isExceedMaxPartsQty to true
            if(partList.size() > maxPartQty){
                squote.setExceedMaxPartsQty(true);
            }
            int partQty = 0;
            int sortSeqNum = 1;

            for (Iterator it = partList.iterator();it.hasNext();) {

                SpreadSheetPart  part = (SpreadSheetPart)it.next();


                String epAddYears = part.getEpAddYears();
                int addYears = StringUtils.isEmpty(epAddYears)? 0 : Integer.parseInt(epAddYears);
                int epAddYearsCvrageSeqNum = part.getEpAddtnlYearCvrageSeqNum();
                if(addYears == 0 && epAddYearsCvrageSeqNum != 0){
                	continue;
                }

                String strQuantity = "N/A".equalsIgnoreCase(part.getEpQuantity()) ? "0" : part.getEpQuantity();

                Double epQuantity = StringUtils.isNotBlank(strQuantity) ? new Double(strQuantity): null;
                //skip 0 qty spreadsheet part. 0 qty means remove this part from quote
                if(epQuantity != null && epQuantity.intValue()< 1) {
                    logger.debug(this, part.getEpPartNumber() + " has been skipped, for zero qty. " + part.getEpQuantity());
                    continue;
                }

                QuoteLineItem lineItem = QuoteLineItemFactory.singleton().createQuoteLineItem(webQuoteNum, part.getEpPartNumber(), userID);

                logger.debug(this, "re-build lineItem: " + part.getRenewalLineItemSeqNum() + "   " + part.getEpPartNumber());

                //part qty
                lineItem.setPartQty(epQuantity == null ? null : new Integer((int) Math.round(epQuantity.doubleValue())));

                //prorate is available for PA only
                if(isPA) {
                    lineItem.setProrateFlag(BooleanUtils.toBoolean(part.getEpMaintenanceProrated()));
                } else {
                    lineItem.setProrateFlag(false);
                }

                lineItem.setQuoteSectnSeqNum(0);
                lineItem.setAssocdLicPartFlag(false);
                lineItem.setSwProdBrandCode(StringUtils.trimToEmpty(part.getBrandCode()));

                if(isPA || isPAE) {
                    //import renewal quote number
                    String renewalQuoteNum = part.getRenewalQuoteNumber();
                    if (StringUtils.isNotBlank(renewalQuoteNum))
                        lineItem.setRenewalQuoteNum(renewalQuoteNum);

                    //import renewal seq num
                    String renewalLineItemSeqNum = part.getRenewalLineItemSeqNum();
                    if (StringUtils.isNotBlank(renewalLineItemSeqNum))
                        lineItem.setRenewalQuoteSeqNum(Integer.parseInt(renewalLineItemSeqNum));
                }

                //import add years
                lineItem.setAddtnlMaintCvrageQty(addYears);
                if (SpreadSheetPart.SOURCE_AP_TAB.equals(part.getSourceTab())){
                    lineItem.setManualSortSeqNum(sortSeqNum);
                } else {
                    lineItem.setManualSortSeqNum(part.getEpSortSeqNum());
                }
                lineItem.setAddtnlYearCvrageSeqNum(part.getEpAddtnlYearCvrageSeqNum());

                //import override start date
                String overrideStartDate = part.getEpOverrideDatesStartDate();
                if(StringUtils.isNotBlank(overrideStartDate) && !part.getEpSTDStartDate().equalsIgnoreCase(overrideStartDate)) {
                    lineItem.setMaintStartDate(DateUtil.parseDate(overrideStartDate, dateFromat, optionalDateFormat));
                    lineItem.setStartDtOvrrdFlg(true);
                }else{
                	lineItem.setMaintStartDate(DateUtil.parseDate(part.getEpSTDStartDate(), dateFromat));
                }

                //import override end date
                String overrideEndDate = part.getEpOverideDatesEndDate();
                if(StringUtils.isNotBlank(overrideEndDate) && !part.getEpSTDEndDate().equalsIgnoreCase(overrideEndDate)) {
                    lineItem.setMaintEndDate(DateUtil.parseDate(overrideEndDate, dateFromat, optionalDateFormat));
                    lineItem.setEndDtOvrrdFlg(true);
                }else{
                	lineItem.setMaintEndDate(DateUtil.parseDate(part.getEpSTDEndDate(), dateFromat));
                }
                if(part.getEpBPOvrrdDiscPct() != -1){
                	lineItem.setChnlOvrrdDiscPct(new Double(DecimalUtil.roundAsDouble((part.getEpBPOvrrdDiscPct() * 100 + 0.000001),3)));
                }else{
                	lineItem.setChnlOvrrdDiscPct(null);
                }
                //set backdating flag.
                if (lineItem.getMaintStartDate() != null && DateUtil.isDateBeforeToday(lineItem.getMaintStartDate())) {
                    lineItem.setBackDatingFlag(true);
                }

                //import override price
                String overridePrice = part.getEpEnterOnlyOneOverridePrice();
                if(StringUtils.isNotBlank(overridePrice)) {
                    if (StringUtils.contains(overridePrice, ",")){
                        overridePrice = StringUtils.replace(overridePrice, ",", "");
                    }
                	overridePrice = String.valueOf(DecimalUtil.roundAsDouble(Double.parseDouble(overridePrice),4));
                    lineItem.setOverrideUnitPrc(Double.valueOf(overridePrice));
                } else {
                    lineItem.setOverrideUnitPrc(null);
                }

                //import override disc
                double discPrc = part.getEpEnterOnlyOneDiscountPercent();
            	lineItem.setLineDiscPct(DecimalUtil.roundAsDouble((discPrc * 100 + 0.000001),3));



                //always set PVU_OVERRIDE_QTY_IND_CODE to 2 - "Quantity was manually entered".
                lineItem.setPVUOverrideQtyIndCode("2");

                partQty = partQty + addYears; // add line items for additional years
                partQty++;
                sortSeqNum++;
                //only add the first 250 line items to quote
                if(partQty >= maxPartQty){
                    break;
                }
            }
            this.commitTransaction();

            splitToMultiLineItems(squote, webQuoteNum, userID);
//          update line items sourced from renewal quote
            updateValidRenewalLineItem(squote, webQuoteNum);

        	updateApplianceLineItem(webQuoteNum,squote.getApplianceParts());
        } catch (TopazException e) {
            throw new QuoteException("Get error in add line items to quote : " + webQuoteNum, e);
        } finally {
            this.rollbackTransaction();
        }
    }

    protected void addSaaSLineItemsToQuote(String webQuoteNum, SpreadSheetQuote squote, String userID, boolean newQuote) throws QuoteException {
        boolean isPA = squote.isPA();
        boolean isPAE = squote.isPAE();

        LogContext logger = LogContextFactory.singleton().getLogContext();

        /** Validate partNum list */
        squote = validateSaaSPartNumList(squote);

        if (newQuote) {
        	generateAsNewSessionConfigrtnID(squote);
        } else {
        	try {
				generateConfigrtnID(squote);
			} catch (ParseException e) {
				throw new QuoteException("parse date error...", e);
			}
        }


        try {
            this.beginTransaction();

            List<SpreadSheetPart> partList = squote.getValidSaaSPartList();

            logger.debug(this, "valid lineItems on spreadsheet: " + partList.size());

            int maxPartQty = PartPriceConfigFactory.singleton().getElaLimits();
            //if there are more than 250 valid line items, set isExceedMaxPartsQty to true
            if(partList.size() > maxPartQty){
                squote.setExceedMaxPartsQty(true);
            }
            int partQty = 0;
            int sortSeqNum = 1;
            int index = 0;
            HashMap configrtnIdCountMap = new HashMap();

            for (Iterator it = partList.iterator();it.hasNext();) {

                SpreadSheetPart  part = (SpreadSheetPart)it.next();

                String strQuantity = "";
                if ("N/A".equalsIgnoreCase(part.getEpQuantity())) {
                	strQuantity = "";
                } else if (part.getEpQuantity().contains(QuoteConstants.UP_TO)) {
                	int start = part.getEpQuantity().lastIndexOf(" ");
                	strQuantity = part.getEpQuantity().substring(start+1, part.getEpQuantity().length());
                } else {
                	strQuantity = part.getEpQuantity();
                }
                Double epQuantity = StringUtils.isNotBlank(strQuantity) ? new Double(strQuantity): null;
                String configrtnId = partNumConfigrtnIdMap.get(part.getEpPartNumber());
                //skip 0 qty spreadsheet part. 0 qty means remove this part from quote
                if(epQuantity != null && epQuantity.intValue()< 1) {
                    logger.debug(this, part.getEpPartNumber() + " has been skipped, for zero qty. " + part.getEpQuantity());
                    List partNumList = configrtnIdPartNumlistMap.get(configrtnId);
                    if(null != partNumList && !partNumList.isEmpty()){
                    	if (partNumList.size() == 1) {
                    		for(Iterator it1 = configuratorList.iterator(); it1.hasNext();){
                    			Configurator configurator = (Configurator) it1.next();
                    			if (configrtnId.equalsIgnoreCase(configurator.getConfigurationId())) {
                    				configuratorList.remove(configurator);
                    				break;
                    			}
                    		}
                    	} else {
                    		index = new Integer((configrtnIdCountMap.get(configrtnId) == null ? 0 : configrtnIdCountMap.get(configrtnId)).toString()).intValue();
                    		index++;
                    		configrtnIdCountMap.put(configrtnId, index);
                    		int count = new Integer(configrtnIdCountMap.get(configrtnId).toString()).intValue();
                    		if (count == partNumList.size()) {
                    			for(Iterator it1 = configuratorList.iterator(); it1.hasNext();){
                        			Configurator configurator = (Configurator) it1.next();
                        			if (configrtnId.equalsIgnoreCase(configurator.getConfigurationId())) {
                        				configuratorList.remove(configurator);
                        				break;
                        			}
                        		}
                    		}
                    	}
                    }
                    continue;
                }

                for(Iterator<Configurator> configurators = configuratorList.iterator(); configurators.hasNext();){
        			Configurator configurator = configurators.next();
        			if (configrtnId.equalsIgnoreCase(configurator.getConfigurationId())) {
        				configurator.setProvisioningId(part.getProvisioningId());
        				break;
        			}
        		}

                QuoteLineItem lineItem = QuoteLineItemFactory.singleton().createQuoteLineItem(webQuoteNum, part.getEpPartNumber(), userID);

                logger.debug(this, "re-build lineItem: " + part.getRenewalLineItemSeqNum() + "   " + part.getEpPartNumber());

                //part qty
                lineItem.setPartQty(epQuantity == null ? null : new Integer((int) Math.round(epQuantity.doubleValue())));

                //configuration id
                lineItem.setConfigrtnId(partNumConfigrtnIdMap.get(part.getEpPartNumber()));

                //show replace title
                lineItem.setReplacedPart(part.isShowReplaceTitle());

                //contract term
                if (part.getContractTerm().contains(QuoteConstants.TERM_SUFFIX)) {
                	int end = part.getContractTerm().indexOf(QuoteConstants.TERM_SUFFIX);
                	lineItem.setICvrageTerm(new Integer(part.getContractTerm().substring(0, end)));
                } else {
                	if (part.getContractTerm().contains(".0")) {
                		int end = part.getContractTerm().indexOf(".0");
                    	lineItem.setICvrageTerm(new Integer(part.getContractTerm().substring(0, end)));
                	} else {
                		lineItem.setICvrageTerm(StringUtils.isNotBlank(part.getContractTerm()) ? new Integer(part.getContractTerm()): null);
                	}
                }

                //billing frequency
                if (!"".equals(part.getBillingFrequency())) {
                	if (PartPriceConstants.BillingOptionCode.ANNUAL.equals(part.getBillingFrequency().substring(0, 1))) {
                		lineItem.setBillgFrqncyCode(PartPriceConstants.BillingOptionCode.ANNUAL);
                	} else if (PartPriceConstants.BillingOptionCode.MONTHLY.equals(part.getBillingFrequency().substring(0, 1))) {
                		lineItem.setBillgFrqncyCode(PartPriceConstants.BillingOptionCode.MONTHLY);
                	} else if (PartPriceConstants.BillingOptionCode.QUARTERLY.equals(part.getBillingFrequency().substring(0, 1))) {
                		lineItem.setBillgFrqncyCode(PartPriceConstants.BillingOptionCode.QUARTERLY);
                	} else if (PartPriceConstants.BillingOptionCode.UPFRONT.equals(part.getBillingFrequency().substring(0, 1))) {
                		lineItem.setBillgFrqncyCode(PartPriceConstants.BillingOptionCode.UPFRONT);
                	}
                } else {
                	lineItem.setBillgFrqncyCode(null);
                }

                lineItem.setQuoteSectnSeqNum(0);
                lineItem.setAssocdLicPartFlag(false);
                lineItem.setProrateFlag(false);
                lineItem.setBackDatingFlag(false);
                lineItem.setSwProdBrandCode(StringUtils.isNotBlank(part.getBrandCode()) ? part.getBrandCode() : "N/A");
                lineItem.setWebMigrtdDocFlag(part.isMigrationFlag());

                //import override price
                String overridePrice = part.getEpEnterOnlyOneOverridePrice();
                if(StringUtils.isNotBlank(overridePrice)) {
                    if (StringUtils.contains(overridePrice, ",")){
                        overridePrice = StringUtils.replace(overridePrice, ",", "");
                    }
                	overridePrice = String.valueOf(DecimalUtil.roundAsDouble(Double.parseDouble(overridePrice), part.isDecimal4Flag()?4:2));
            		lineItem.setOvrrdExtPrice(Double.valueOf(overridePrice));
            		lineItem.setOverrideUnitPrc(Double.valueOf(overridePrice));
                } else {
                    lineItem.setOverrideUnitPrc(null);
                    lineItem.setOvrrdExtPrice(null);
                }

                //import override disc
                double discPrc = part.getEpEnterOnlyOneDiscountPercent();
            	lineItem.setLineDiscPct(DecimalUtil.roundAsDouble((discPrc * 100 + 0.000001),3));

            	double bpDiscPrc = part.getEpBPOvrrdDiscPct();
            	if(bpDiscPrc != -1){
            		lineItem.setChnlOvrrdDiscPct(DecimalUtil.roundAsDouble((bpDiscPrc * 100 + 0.000001),3));
            	}
            	double entitleRateVal = part.getEntitledRateVal();
            	double bidRateVal = part.getBidRateVal();

            	//calculate override discount by entitle rate and bid rate
            	if(StringUtils.isBlank(overridePrice) && discPrc <= 0) {
            		if(entitleRateVal > 0 && bidRateVal >0) {
            			discPrc = (entitleRateVal-bidRateVal) / entitleRateVal;
            			lineItem.setLineDiscPct(DecimalUtil.roundAsDouble((discPrc * 100 + 0.000001),3));
                	}
            	}

            	if(StringUtils.isNotEmpty(part.getiRelatedLineItmNum())) {
            		int iRelatedLineItmNum = Integer.parseInt(part.getiRelatedLineItmNum());
            		lineItem.setIRelatedLineItmNum(iRelatedLineItmNum);
            	}

            	if(StringUtils.isNotEmpty(part.getDestSeqNum())) {
            		int destSeqNum = Integer.parseInt(part.getDestSeqNum());
                	lineItem.setDestSeqNum(destSeqNum);
            	}

            	if(lineItem instanceof QuoteLineItem_Impl) {
            		((QuoteLineItem_Impl) lineItem).part.setSaasSubscrptnPart(part.isSaasSubscrptnPart());
            		((QuoteLineItem_Impl) lineItem).part.setSaasSubscrptnOvragePart(part.isSaasSubscrptnOvragePart());
            	}

            	//part ramp up
                String rampUpTitle = part.getRampUpTitle().trim();
                if(StringUtils.isNotEmpty(rampUpTitle)) {
                	rampUpTitle = rampUpTitle.replace("Ramp Up Period ", "");
                	int iRampUpTitle = Integer.parseInt(rampUpTitle);
                	lineItem.setRampUpPeriodNum(iRampUpTitle);
                }
                lineItem.setRampUp(part.isbRampUpPart());

                partQty++;
                sortSeqNum++;
                //only add the first 250 line items to quote
                if(partQty >= maxPartQty){
                    break;
                }
            }
            this.commitTransaction();

            //delete existed configuration for exported quote
            if (!newQuote) {
            	deleteExistingConfigrtnInfo(webQuoteNum);
            }

            //update configuration to quote
            updateConfiguration(webQuoteNum, userID, newQuote);


        } catch (TopazException e) {
            throw new QuoteException("Get error in add line items to quote : " + webQuoteNum, e);
        } finally {
            this.rollbackTransaction();
        }
    }

    protected void generateAsNewSessionConfigrtnID(SpreadSheetQuote squote) throws QuoteException {
    	List partList = squote.getValidSaaSPartList();
    	String partNums = "";
    	StringBuffer buffer = new StringBuffer();
    	LogContext logger = LogContextFactory.singleton().getLogContext();

    	if(null != partList && !partList.isEmpty()){
        	for (Iterator it = partList.iterator();it.hasNext();) {
                SpreadSheetPart part = (SpreadSheetPart)it.next();
            	String partNum = part.getEpPartNumber();
                buffer.append(partNum + NewQuoteDBConstants.DELIMIT);
        	}

        	partNums = buffer.toString();
        	partNums = partNums.substring(0, partNums.length()-1);

            String sqlQuery = null;
            HashMap params = new HashMap();
            params.put("piPartNumList", partNums);

	        this.beginTransaction();
	        logger.debug(this, "get PIDs from part....");

			try {
				QueryContext queryCtx = QueryContext.getInstance();
		        sqlQuery = queryCtx.getCompletedQuery(NewQuoteDBConstants.S_QT_PID_BY_PARTS, null);
		        logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

		        CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
		        queryCtx.completeStatement(ps, NewQuoteDBConstants.S_QT_PID_BY_PARTS, params);
		        ps.execute();
		        logger.debug(logger, "finished to get PIDs from part");

		        ResultSet rs = ps.getResultSet();

		        Map configurators = new HashMap();
		        List<String> partNumList = new ArrayList<String>();
        		String lastNewConfigrtnId = "";

		        if (rs != null) {
		        	while (rs.next()) {
		        		boolean isNew = false;
                        String partPID = StringUtils.trimToEmpty(rs.getString(1));
                        String partNum = StringUtils.trimToEmpty(rs.getString(2));
                        String newConfigrtnId = QuoteCommonUtil.getNewSQOConfigurationId(partPID);
                        partNumConfigrtnIdMap.put(partNum, newConfigrtnId);
                        if (!configurators.containsKey(partPID)) {
                        	configurators.put(partPID, newConfigrtnId);
                        	Configurator configurator = new Configurator();
                    		//add new configuration info into Configurator
                    		configurator.setConfigurationId(newConfigrtnId);
                    		configurator.setConfigrtrConfigrtnId(null);
                    		configurator.setRefDocNum(null);
                    		configurator.setErrorCode(null);
                    		configurator.setConfigrtnAction("NewNCt");
                    		configurator.setEndDate(new java.sql.Date(new java.util.Date().getTime()));
                    		configurator.setCoTermToConfigrtnId(null);
                    		configurator.setOverrideFlag("0");

                        	configuratorList.add(configurator);
                        	isNew = true;
                        }

                    	if (isNew && !partNumList.isEmpty()) {
                    		List<String> partNumNewList = new ArrayList<String>();
                    		partNumNewList.addAll(partNumList);
                    		Collections.copy(partNumNewList, partNumList);
                        	partNumList.clear();
                    		configrtnIdPartNumlistMap.put(lastNewConfigrtnId, partNumNewList);
                        }
                        partNumList.add(partNum);

                        configrtnIdPartNumlistMap.put(newConfigrtnId, partNumList);
                        lastNewConfigrtnId = newConfigrtnId;
		        	}
		        }

			} catch (Exception e) {
                logger.error(this, LogHelper.logSPCall(sqlQuery, params));
                throw new QuoteException(e);
			} finally {
                this.rollbackTransaction();
            }

    	}
    }

    @SuppressWarnings("rawtypes")
	protected void generateConfigrtnID(SpreadSheetQuote squote) throws QuoteException, ParseException {
    	List partList = squote.getValidSaaSPartList();
    	List lastConfigrtnIdList = new ArrayList();
    	List<String> partNumList = new ArrayList<String>();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String partNums = "";
    	String lastConfigrtnId = "";
    	StringBuffer buffer = new StringBuffer();
    	LogContext logger = LogContextFactory.singleton().getLogContext();

    	for (Iterator it = partList.iterator();it.hasNext();) {
            SpreadSheetPart part = (SpreadSheetPart)it.next();
            String partNum = "";
            boolean isNew = false;
            if (StringUtils.isBlank(part.getConfigurationId())) {
            	partNum = part.getEpPartNumber();
                buffer.append(partNum + NewQuoteDBConstants.DELIMIT);
            } else {
            	String configrtnId = part.getConfigurationId();
            	String configrtrConfigrtnId = part.getConfigrtrConfigrtnId();
            	String refDocNum = part.getRefDocNum();
            	String errorCode = part.getErrorCode();
            	String configrtnAction = part.getConfigrtnAction();
				Date endDate = StringUtils.isBlank(part.getConfigrationEndDate()) ? null : new Date(sdf.parse(part.getConfigrationEndDate()).getTime());
            	String coTermToConfigrtnId = part.getCoTermToConfigrtnId();
            	boolean overrideFlag = part.isOverrideFlag();
            	partNum = part.getEpPartNumber();

            	if (!lastConfigrtnIdList.contains(configrtnId)) {
            		//add existed configuration info into Configurator
            		Configurator configurator = new Configurator();
            		configurator.setConfigurationId(configrtnId);
            		configurator.setConfigrtrConfigrtnId(configrtrConfigrtnId);
            		configurator.setRefDocNum(refDocNum);
            		configurator.setErrorCode(errorCode);
            		configurator.setConfigrtnAction(configrtnAction);
            		configurator.setEndDate(endDate);
            		configurator.setCoTermToConfigrtnId(coTermToConfigrtnId);
            		configurator.setOverrideFlag(overrideFlag == true ? "1" : "0");

            		configuratorList.add(configurator);
                	lastConfigrtnIdList.add(configrtnId);
                	isNew = true;
            	}
            	if (isNew && !partNumList.isEmpty()) {
            		List<String> partNumNewList = new ArrayList<String>();
            		partNumNewList.addAll(partNumList);
            		Collections.copy(partNumNewList, partNumList);
                	partNumList.clear();
            		configrtnIdPartNumlistMap.put(lastConfigrtnId, partNumNewList);
            	}

            	partNumList.add(partNum);

            	partNumConfigrtnIdMap.put(partNum, configrtnId);
            	configrtnIdPartNumlistMap.put(configrtnId, partNumList);
                lastConfigrtnId = configrtnId;
            }
    	}

    	partNums = buffer.toString();

        if (StringUtils.isNotBlank(partNums)) {
            partNums = partNums.substring(0, partNums.length()-1);

            String sqlQuery = null;
            HashMap params = new HashMap();
            params.put("piPartNumList", partNums);

    		try {
    	        this.beginTransaction();
    	        logger.debug(this, "get PIDs from part....");

    	        QueryContext queryCtx = QueryContext.getInstance();
    	        sqlQuery = queryCtx.getCompletedQuery(NewQuoteDBConstants.S_QT_PID_BY_PARTS, null);
    	        logger.debug(this, LogHelper.logSPCall(sqlQuery, params));

    	        CallableStatement ps = TopazUtil.getConnection().prepareCall(sqlQuery);
    	        queryCtx.completeStatement(ps, NewQuoteDBConstants.S_QT_PID_BY_PARTS, params);
    	        ps.execute();
    	        logger.debug(logger, "finished to get PIDs from part");

    	        ResultSet rs = ps.getResultSet();
    	        List<String> addPartNumList = new ArrayList<String>();
    	        String newConfigrtnId = "";
    	        String lastNewConfigrtnId = "";
    	        Map configurators = new HashMap();

                if (rs != null) {
                    while (rs.next()) {
                        String partPID = StringUtils.trimToEmpty(rs.getString(1));
                        String partNum = StringUtils.trimToEmpty(rs.getString(2));

            	        boolean existedPidFlag = false;
            	        boolean isNew = false;
            	        if (!configurators.containsKey(partPID)) {
	                        for (Iterator it = configuratorList.iterator();it.hasNext();) {
	                        	Configurator configrtor = (Configurator)it.next();
	                        	String configrtnId = configrtor.getConfigurationId();
	                            if (configrtnId.startsWith(partPID)) {
	                            	partNumConfigrtnIdMap.put(partNum, configrtnId);
	                            	partNumList.add(partNum);
	                            	configrtnIdPartNumlistMap.put(configrtnId, partNumList);
	                            	existedPidFlag = true;
	                            	break;
	                            }
	                    	}
	                        if (!existedPidFlag) {
                            	newConfigrtnId = QuoteCommonUtil.getNewSQOConfigurationId(partPID);
                            	partNumConfigrtnIdMap.put(partNum, newConfigrtnId);
                        		configurators.put(partPID, newConfigrtnId);
                        		//add new configuration info into Configurator
                            	Configurator configurator = new Configurator();
                        		configurator.setConfigurationId(newConfigrtnId);
                        		configurator.setConfigrtrConfigrtnId(null);
                        		configurator.setRefDocNum(null);
                        		configurator.setErrorCode(null);
                        		configurator.setConfigrtnAction("NewNCt");
                        		configurator.setEndDate(new java.sql.Date(new java.util.Date().getTime()));
                        		configurator.setCoTermToConfigrtnId(null);
                        		configurator.setOverrideFlag("0");

                        		configuratorList.add(configurator);
                        		isNew = true;

                            	if (isNew && !addPartNumList.isEmpty()) {
                                		List<String> addPartNumNewList = new ArrayList<String>();
                                		addPartNumNewList.addAll(addPartNumList);
                                		Collections.copy(addPartNumNewList, addPartNumList);
                                		addPartNumList.clear();
                                		configrtnIdPartNumlistMap.put(lastNewConfigrtnId, addPartNumNewList);
                                }
                                addPartNumList.add(partNum);

                            	configrtnIdPartNumlistMap.put(newConfigrtnId, addPartNumList);
                            	lastNewConfigrtnId = newConfigrtnId;
	                        }
            	        } else {
                            partNumConfigrtnIdMap.put(partNum, newConfigrtnId);
                            addPartNumList.add(partNum);
                        	configrtnIdPartNumlistMap.put(newConfigrtnId, addPartNumList);
                        	lastNewConfigrtnId = newConfigrtnId;
                        }
                    }
                    rs.close();
                }


    	        this.commitTransaction();
    		} catch (Exception e) {
                logger.error(this, LogHelper.logSPCall(sqlQuery, params));
                throw new QuoteException(e);
    		} finally {
                this.rollbackTransaction();
            }
        }
    }

    protected void updateConfiguration(String webQuoteNum, String userID, boolean isNewQuote) throws QuoteException {
    	ConfiguratorPartProcess configuratorPartProcess;
        configuratorPartProcess = ConfiguratorPartProcessFactory.singleton().create();

        LogContext logger = LogContextFactory.singleton().getLogContext();
    	try {
    		this.beginTransaction();

            logger.debug(this, "update configuration id on spreadsheet: " + configuratorList.size());

            for (Iterator it = configuratorList.iterator();it.hasNext();) {
            	Configurator configrtor = (Configurator)it.next();
            	String configrtnId = configrtor.getConfigurationId();
            	String configrtrConfigrtnId = configrtor.getConfigrtrConfigrtnId();
            	String refDocNum = configrtor.getRefDocNum();
            	String errorCode = configrtor.getErrorCode();
            	String configrtnAction = configrtor.getConfigrtnAction();
            	Date endDate = configrtor.getEndDate();
            	String coTermToConfigrtnId = configrtor.getCoTermToConfigrtnId();
            	String overrideFlag = configrtor.isOverrideFlag();
            	String provisioningId = configrtor.getProvisioningId();
            	String importFlag = isNewQuote ? "2" : "1";

            	configuratorPartProcess.addOrUpdateConfigrtn(webQuoteNum, configrtnId, configrtrConfigrtnId, userID, refDocNum, errorCode, configrtnAction, endDate, coTermToConfigrtnId, overrideFlag, importFlag,"0",null,null, provisioningId,0);
        	}

			this.commitTransaction();
		} catch (TopazException e) {
			throw new QuoteException("Update error configuration to quote : " + webQuoteNum, e);
		} finally {
            this.rollbackTransaction();
        }

    }

    /**
     * @param webQuoteNum
     * @throws QuoteException
     */
    private void splitToMultiLineItems(SpreadSheetQuote squote, String webQuoteNum, String userID) throws QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        try {
        this.beginTransaction();
        logger.debug(this, "Retrieving line items for " + webQuoteNum);
        List ogrinalLineitems = QuoteLineItemFactory.singleton() .findLineItemsByWebQuoteNum(webQuoteNum);
        this.commitTransaction();

        List lineitems = new ArrayList();
        HashMap ogrinalAddYears = new HashMap();

        this.beginTransaction();
        for(int i = 0; i< ogrinalLineitems.size(); i++) {
                QuoteLineItem ogrinallineitem = (QuoteLineItem) ogrinalLineitems.get(i);
                if(ogrinallineitem != null && ogrinallineitem.getAddtnlMaintCvrageQty()>0 && ogrinallineitem.getPartDispAttr().isMaintBehavior()) {
                	lineitems.add(ogrinallineitem);
                    ogrinalAddYears.put(String.valueOf(i),String.valueOf(ogrinallineitem.getAddtnlMaintCvrageQty()));
                } else if (ogrinallineitem.getAddtnlMaintCvrageQty()>0) {
                    ogrinallineitem.setAddtnlMaintCvrageQty(0);
                }
        }
        checkInvaidOCSParts(squote, ogrinalLineitems);

        checkInvaidFutureDates(squote, ogrinalLineitems);

        cleanAdditionalYear(lineitems);
        this.commitTransaction();

        logger.debug(this, "lineitems size is " + lineitems.size());
        this.beginTransaction();
        PartPriceProcess ppProcess = PartPriceProcessFactory.singleton().create();
            for (int i = 0; i < ogrinalLineitems.size(); i++) {
                if (ogrinalAddYears.get(String.valueOf(i)) != null) {
                    QuoteLineItem li = (QuoteLineItem) ogrinalLineitems.get(i);
                    String partNum = li.getPartNum();
                    int seqNum = li.getSeqNum();
                    int additionalYears = Integer.parseInt((String) ogrinalAddYears.get(String.valueOf(i)));
                    Integer partQty = li.getPartQty();
                    int manualSortSeqNum = li.getManualSortSeqNum();
                    Double overrideUnitprice = li.getOverrideUnitPrc();
                    double discPct = li.getLineDiscPct();
                    boolean prorationFlag = li.getProrateFlag();
                    logger.debug(this, "splitToMultiLineItems...............");
                    logger.debug(this, "webQuoteNum " + webQuoteNum);
                    logger.debug(this, "partNum " + partNum);
                    logger.debug(this, "seqNum " + seqNum);
                    logger.debug(this, "additionalYears " + additionalYears);
                    logger.debug(this, "partQty " + partQty);
                    logger.debug(this, "manualSortSeqNum " + manualSortSeqNum);
                    logger.debug(this, "overrideUnitprice " + overrideUnitprice);
                    logger.debug(this, "discPct " + discPct);
                    logger.debug(this, "prorationFlag " + prorationFlag);
                    ppProcess.changeAdditionalMaint(webQuoteNum, partNum,
                            seqNum, additionalYears, partQty, manualSortSeqNum,
                            overrideUnitprice, discPct, prorationFlag, userID);

                }
            }
         this.commitTransaction();

        }catch (TopazException e) {
            throw new QuoteException("Get error in split to multi LineItems  : " + webQuoteNum, e);
        } finally {
            this.rollbackTransaction();
        }

    }
    // check if user had put in dates for a OCS parts after the spread-sheet is imported, if yes, do not honor the rep entered dates for the OCS parts
    private void checkInvaidOCSParts (SpreadSheetQuote squote, List lineitems) throws TopazException {
        PartPriceConfigFactory factory = PartPriceConfigFactory.singleton();
        List invalidOCSPartNumList = new ArrayList();
        for(Iterator it = lineitems.iterator(); it.hasNext();) {
            QuoteLineItem li = (QuoteLineItem) it.next();
            if (factory.isOCSPart(li.getRevnStrmCode())){
                li.setMaintStartDate(null);
                li.setMaintEndDate(null);
                if (li.getStartDtOvrrdFlg() || li.getEndDtOvrrdFlg()) {
                    li.setStartDtOvrrdFlg(false);
                    li.setEndDtOvrrdFlg(false);
                    invalidOCSPartNumList.add(li.getPartNum());
                }
            }
        }
        squote.setInvalidOCSPartList(invalidOCSPartNumList);

    }

    // check if user had put in future dates for licence parts after the spread-sheet is imported, if yes, ignore the future dates
    private void checkInvaidFutureDates (SpreadSheetQuote squote, List lineitems) throws TopazException {
        PartPriceConfigFactory factory = PartPriceConfigFactory.singleton();
        for (Iterator it = lineitems.iterator(); it.hasNext();) {
            QuoteLineItem li = (QuoteLineItem) it.next();
            Date startDate = li.getMaintStartDate();
            if (DateUtil.isDateAfterToday(startDate)) {
                if (! factory.isFutureStartDateAllowed(li.getRevnStrmCode())) {
                    li.setMaintStartDate(null);
                    li.setMaintEndDate(null);
                    li.setStartDtOvrrdFlg(false);
                    li.setEndDtOvrrdFlg(false);
                    squote.setHasInvalidFutureDates(true);
                }
            }
        }
    }

    private void cleanAdditionalYear (List lineitems) throws TopazException {

       for(Iterator it = lineitems.iterator(); it.hasNext();) {
           QuoteLineItem li = (QuoteLineItem) it.next();
           li.setAddtnlMaintCvrageQty(0);
       }
    }
    /**
     * @param squote
     * @param creatorId
     * @throws QuoteException
     */
    protected void processAsNewSessionQuote(SpreadSheetQuote squote, String creatorId, String audCode, boolean newQuote)
    				throws QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        String siteNum = squote.getSiteNum();
        String sapCtrctNum = squote.getSapCtrctNum();
        int webCustId = -1;
        String currency = squote.getCurrency();

        CodeDescObj lob = null;
        String acquisition = null;
        Country cntry = null;

        try {
            CacheProcess cProcess = CacheProcessFactory.singleton().create();

            String lobCode = squote.getLobCode();
            //importing a PA or PAE quote, check if a customer is specified.  If none is specified, set the line of business on the imported quote to PAUN.
            //If it's FCT to PA migration quote, set the lob to PAUN
            if (((!squote.isCustValid() || StringUtils.isBlank(squote.getSiteNum())) && (squote.isPA() || squote.isPAE()))
                    || squote.isFCTTOPA()) {
                lobCode = QuoteConstants.LOB_PAUN;
            } else {
                //nothing
            }

            lob =  cProcess.getLOBByCode(lobCode);

            cntry = cProcess.getCountryByCode3(squote.getCntryCode());

            if(squote.isFCT() || StringUtils.isNotBlank(squote.getProgMigrtnCode()))
                acquisition = squote.getAcquisition();

            QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
            logger.debug(this, "processAsNewSessionQuote...");
            logger.debug(this, "cntry " + cntry);
            logger.debug(this, "lob " + lob);
            logger.debug(this, "acquisition " + acquisition);
            logger.debug(this, "creatorId " + creatorId);
            QuoteHeader qHeader = qProcess.createNewSessionQuote(cntry, lob, acquisition, creatorId, squote.getProgMigrtnCode(), squote.getRenewalQuoteNumber(), audCode);
            qHeader =  qProcess.getQuoteHdrInfo(creatorId);
            String webQuoteNum = qHeader.getWebQuoteNum();
            logger.debug(this, "create a new session quote: " + webQuoteNum);

            if(squote.isCustValid()&& squote.isSapCtrctNumValid())
            qProcess.updateQuoteHeaderCustInfo(creatorId,webQuoteNum,
                    siteNum, sapCtrctNum, webCustId, currency,null);

            this.addLineItemsToQuote(webQuoteNum,squote,creatorId);
            this.addSaaSLineItemsToQuote(webQuoteNum,squote,creatorId,newQuote);
        } catch (NoDataException ne) {
            throw new ExportQuoteException(ne);
        }

    }

    /**
     * Only if the user importing the quote is listed as the owner of the quote,
     * an editor (delegate) of the quote, or an editor (delegate) of the quote's owner
     * could edit the existing quote.
     * @param creatorID
     * @param header
     * @return
     * @throws QuoteException
     */
    protected  boolean isEditor(String creatorID, QuoteHeader header) throws QuoteException {
        LogContext logger = LogContextFactory.singleton().getLogContext();
        String quoteOwner = StringUtils.trimToEmpty(header.getCreatorId());
        String webQuoteNum = header.getWebQuoteNum();

        if (creatorID.equals(quoteOwner))
            return true;

        List deletgates = findDelegates(quoteOwner, webQuoteNum);
        logger.debug(this, "deletgates list : " + deletgates);
        for(Iterator it = deletgates.iterator(); it.hasNext(); ) {
            SalesRep sales = (SalesRep)it.next();
            logger.debug(this, "deletgate : " + sales.getEmailAddress());
            if (creatorID.equals(StringUtils.trimToEmpty(sales.getEmailAddress())))
                return true;
        }
        return false;



    }

    protected List findDelegates (String quoteOwner, String webQuoteNum) throws QuoteException {
        List deletgates = new ArrayList();
        try {
            //find editors (delegate) of the quote's owner
            List massDeletgates = MassDlgtnProcessFactory.singleton().create().getDelegates(quoteOwner);
            deletgates.addAll(massDeletgates);

            //find editors (delegate) of the quote
            this.beginTransaction();
            List quoteDelegates = SalesRepFactory.singleton().findDelegatesByQuote(webQuoteNum);
            this.commitTransaction();
            deletgates.addAll(quoteDelegates);
        } catch (TopazException e) {
            throw new QuoteException(e);
        } finally {
            this.rollbackTransaction();
        }
        return deletgates;
    }

	/**
	 * @param creatorId
	 * @throws QuoteException
	 */
	protected void updateSpecialBidFlag(String creatorId, String overrideQuoteSVPLevel) throws QuoteException {
		  LogContext logger = LogContextFactory.singleton().getLogContext();
		  boolean isSpecialBid = false;
		try {
	        this.beginTransaction();

	        QuoteProcess qProcess = QuoteProcessFactory.singleton().create();
		    QuoteHeader qHeader = qProcess.getQuoteHdrInfo(creatorId);
		    if (qHeader == null) throw new QuoteException("Can not select quote header for " + creatorId);

		    String webQuoteNum = qHeader.getWebQuoteNum();
	        logger.debug(this, "Retrieving line items for updateSpecialBidFlag " + webQuoteNum);
	        List lineitems = QuoteLineItemFactory.singleton() .findLineItemsByWebQuoteNum(webQuoteNum);

	        Quote quote = new Quote(qHeader);
	        quote.setLineItemList(lineitems);

//	        Modified on 5/15/2008 for new field: approvalRouteFlag
	        SpecialBidRule spRule = SpecialBidRule.create(quote);
	        isSpecialBid = spRule.validate(creatorId);

	        //07/16/2009, persist special bid reason
	        QuoteReasonFactory.singleton().updateSpecialBidReason(webQuoteNum, spRule.getSpecialBidReason(), creatorId);

	        qHeader.setSpeclBidSystemInitFlag(isSpecialBid ? 1 : 0);
	        qHeader.setApprovalRouteFlag(spRule.getApprovalRouteFlagAsInt());

	        Iterator plMapIt = PartPriceConfigFactory.singleton().priceLevelMap.entrySet().iterator();
	        while(plMapIt.hasNext()){
	        	Entry en = (Entry) plMapIt.next();
	        	if(en.getValue().equals(overrideQuoteSVPLevel)){
	        		qHeader.setOvrrdTranLevelCode((String)en.getKey());
	        		break;
	        	}
	        }
	        qHeader.setOmittedLineRecalcFlag(-1);
	        logger.debug(this,qHeader.getWebQuoteNum() +
	        		(isSpecialBid ? " is system special bid " : " is not system special bid"));

	        PartPriceProcessFactory.singleton().create().updateQuoteHeader(qHeader, creatorId);
	        this.commitTransaction();
		}catch (TopazException e) {
            throw new QuoteException("Get error in supdateSpecialBidFlag  : " + creatorId, e);
		} finally {
            this.rollbackTransaction();
        }

	}
}
