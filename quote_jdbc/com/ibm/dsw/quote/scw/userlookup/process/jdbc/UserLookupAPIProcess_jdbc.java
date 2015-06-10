package com.ibm.dsw.quote.scw.userlookup.process.jdbc;



import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.LogHelper;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.TopazUtil;
import com.ibm.dsw.quote.common.config.CommonDBConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.jdbc.QuoteLineItem_jdbc;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.customerlist.domain.ActiveService;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.dsw.quote.scw.config.ScwDBConstants;
import com.ibm.dsw.quote.scw.userlookup.CCHolder;
import com.ibm.dsw.quote.scw.userlookup.CustomerFullAddress;
import com.ibm.dsw.quote.scw.userlookup.CustomerName;
import com.ibm.dsw.quote.scw.userlookup.Payer;
import com.ibm.dsw.quote.scw.userlookup.ScwUserLookupCAResult;
import com.ibm.dsw.quote.scw.userlookup.ScwUserLookupConfigDetailResult;
import com.ibm.dsw.quote.scw.userlookup.ScwUserLookupConfigResult;
import com.ibm.dsw.quote.scw.userlookup.ScwUserLookupContractResult;
import com.ibm.dsw.quote.scw.userlookup.ScwUserLookupCustResult;
import com.ibm.dsw.quote.scw.userlookup.ScwUserLookupResult;
import com.ibm.dsw.quote.scw.userlookup.domain.UserLookupAPIContractInfo;
import com.ibm.dsw.quote.scw.userlookup.process.UserLookupAPIProcess_impl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.common.jdbc.QueryContext;
import com.ibm.ead4j.topaz.exception.TopazException;



public class UserLookupAPIProcess_jdbc extends UserLookupAPIProcess_impl {

	private LogContext logger = LogContextFactory.singleton().getLogContext();

	
	public ScwUserLookupResult retrieveUserLookupInfoResultNoCA(
			UserLookupAPIContractInfo usrlkpCtrInfo) throws TopazException {
		ScwUserLookupResult result = new ScwUserLookupResult();
		HashMap params = new HashMap();
		//params.put("piUserId", usrlkpCtrInfo.getWebIdentityID());
		params.put("piUserUniqueId", usrlkpCtrInfo.getWebIdentityUniqueID());
		//params.put("piUserEmail", usrlkpCtrInfo.getEmailAddress());
		params.put("piCustNum", usrlkpCtrInfo.getSiteNumber());
		params.put("piCanContainSoft", Integer.parseInt(usrlkpCtrInfo.getIfContainSoftware()));
		params.put("piCanContainSaas", Integer.parseInt(usrlkpCtrInfo.getIfContainSaaS()));
		params.put("piPID", usrlkpCtrInfo.getPid());
		params.put("piCountries", usrlkpCtrInfo.getAllowedCountries());
		TimeTracer tracer = TimeTracer.newInstance();
		ResultSet rs = null;
		ResultSet rsItem = null;
		try {
			this.beginTransaction();
			QueryContext context = QueryContext.getInstance();
			String sqlQuery = context.getCompletedQuery(
					ScwDBConstants.S_QT_USER_LOOKUP_NOCA, null);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
			CallableStatement ps = TopazUtil.getConnection().prepareCall(
					sqlQuery);
			context.completeStatement(ps, ScwDBConstants.S_QT_USER_LOOKUP_NOCA,
					params);

			tracer.stmtTraceStart("call S_QT_USER_LOOKUP_NOCA");
			boolean psResult = ps.execute();
			tracer.stmtTraceEnd("call S_QT_USER_LOOKUP_NOCA");
			int poGenStatus = ps.getInt(1);
			if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_NOSITE || poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
				result.setIsInputSiteValid(String.valueOf(ps.getInt(2)));
			}
			if (psResult) {
				
				if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
					
					rs = ps.getResultSet();
					if(rs.next()){
						handleResultSet(rs, true, result);
						
					}
					
					while(ps.getMoreResults()){
						rsItem = rs;
						rsItem.close();
						rs = ps.getResultSet();

						//we can't access resultset again when all rows are consumed
						if(rs.next()){
							handleResultSet(rs, true, result);
						}
					}
				}
			}
			this.commitTransaction();
			calculateRemainTerm(result.getScwUserLookupConfigResultList(), result.getScwUserLookupConfigDetailResultList());
		} catch (SQLException e) {
			logger.error(this, LogThrowableUtil.getStackTraceContent(e));
			throw new TopazException(
					"Exception when lookup scw user , piUserId [" + usrlkpCtrInfo.getWebIdentityID() 
					                                  + "] piUserUniqueId [" + usrlkpCtrInfo.getWebIdentityUniqueID() 
					                                  + "] piUserEmail [" + usrlkpCtrInfo.getEmailAddress() 
					                                  + "] piCustNum [" + usrlkpCtrInfo.getSiteNumber()
					                                  + "] piCanContainSoft [" + usrlkpCtrInfo.getIfContainSoftware()
					                                  + "] piCanContainSaas [" + usrlkpCtrInfo.getIfContainSaaS()
					                                  + "] piPID [" + usrlkpCtrInfo.getPid()
					                                  + "] piCountries [" + usrlkpCtrInfo.getAllowedCountries()
					                                  + "]", e);
		} catch (TopazException tce) {
			logger.error(this, LogThrowableUtil.getStackTraceContent(tce));
			throw new TopazException(tce);
        } catch (QuoteException e) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException(e);
        } finally {
			try {
				if (null != rsItem && !rsItem.isClosed()) {
					rsItem.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
			try {
				if (null != rs && !rs.isClosed()) {
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
			tracer.dump();
		}
		
		return result;
	}
	
	public void handleResultSet(ResultSet rs, boolean flag, ScwUserLookupResult result) throws TopazException, SQLException{
		try {
			String cursorName = rs.getString("SERVICES");
			if ("CA".equalsIgnoreCase(cursorName)){
				processSetCAResults(rs, result);
			} else if ("CONFIG".equalsIgnoreCase(cursorName)) {
				processSetConfigResults(rs, result);
			} else if ("CUST".equalsIgnoreCase(cursorName)) {
				processSetCustResults(rs, result, flag);
			} else if ("CTRCT".equalsIgnoreCase(cursorName)) {
				processSetContractResults(rs, result);
			} else if ("PART".equalsIgnoreCase(cursorName)) {
				processSetConfigDetailResults(rs, result);
			} else if ("DSCR".equalsIgnoreCase(cursorName)) {
				processSetConfigDscrResults(rs, result);
			}
		} catch (SQLException e) {
			
			throw e;
		} catch (TopazException tce) {
			
			throw tce;
		}
	}
	
	public ScwUserLookupResult retrieveUserLookupInfoResultWithCA(
			UserLookupAPIContractInfo usrlkpCtrInfo) throws TopazException {
		ScwUserLookupResult result = new ScwUserLookupResult();
		HashMap params = new HashMap();
		//params.put("piUserId", usrlkpCtrInfo.getWebIdentityID());
		params.put("piUserUniqueId", usrlkpCtrInfo.getWebIdentityUniqueID());
		//params.put("piUserEmail", usrlkpCtrInfo.getEmailAddress());
		params.put("piCaNum", usrlkpCtrInfo.getChargeAgreementNumber());
		params.put("piConfigId", usrlkpCtrInfo.getConfigID());
		params.put("piCountries", usrlkpCtrInfo.getAllowedCountries());
		TimeTracer tracer = TimeTracer.newInstance();
		ResultSet rs = null;
		ResultSet rsItem = null;
		try {
			this.beginTransaction();
			QueryContext context = QueryContext.getInstance();
			String sqlQuery = context.getCompletedQuery(
					ScwDBConstants.S_QT_USER_LOOKUP_WITHCA, null);
			logger.debug(this, LogHelper.logSPCall(sqlQuery, params));
			CallableStatement ps = TopazUtil.getConnection().prepareCall(
					sqlQuery);
			context.completeStatement(ps, ScwDBConstants.S_QT_USER_LOOKUP_WITHCA,
					params);

			tracer.stmtTraceStart("call S_QT_USER_LOOKUP_WITHCA");
			boolean psResult = ps.execute();
			tracer.stmtTraceEnd("call S_QT_USER_LOOKUP_WITHCA");
			int poGenStatus = ps.getInt(1);
			if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_INPUTCA_INVALID || poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
				
				result.setIsInputCAValid(String.valueOf(ps.getInt(6)));
				
				if (StringUtils.isNotBlank(usrlkpCtrInfo.getConfigID()) && result.getIsInputCAValid().equalsIgnoreCase(CommonDBConstants.INPUT_CA_ACTIVE)){
					result.setIsInputConfigValid(String.valueOf(ps.getInt(7)));
				}
			}
			if (psResult) {				
				if (poGenStatus == CommonDBConstants.DB2_SP_RETURN_SIGN_SUCCESS) {
					rs = ps.getResultSet();
					if(rs.next()){
						handleResultSet(rs, false, result);
					}
					
					while(ps.getMoreResults()){
						rsItem = rs;
						rsItem.close();
						rs = ps.getResultSet();
						

						//we can't access resultset again when all rows are consumed
						if(rs.next()){
							handleResultSet(rs, false, result);
						}
					}
				}
			}
			this.commitTransaction();
			calculateRemainTerm(result.getScwUserLookupConfigResultList(), result.getScwUserLookupConfigDetailResultList());
		} catch (SQLException e) {
			logger.error(this, LogThrowableUtil.getStackTraceContent(e));
			throw new TopazException(
					"Exception when lookup scw user , piUserId [" + usrlkpCtrInfo.getWebIdentityID() 
					                                  + "] piUserUniqueId [" + usrlkpCtrInfo.getWebIdentityUniqueID() 
					                                  + "] piUserEmail [" + usrlkpCtrInfo.getEmailAddress() 
					                                  + "] piCaNum [" + usrlkpCtrInfo.getChargeAgreementNumber()
					                                  + "] piConfigId [" + usrlkpCtrInfo.getConfigID()
					                                  + "] piCountries [" + usrlkpCtrInfo.getAllowedCountries()
					                                  + "]", e);
		} catch (TopazException tce) {
			logger.error(this, LogThrowableUtil.getStackTraceContent(tce));
			throw new TopazException(tce);
        } catch (QuoteException e) {
            logger.error(this, LogThrowableUtil.getStackTraceContent(e));
            throw new TopazException(e);
        } finally {
			try {
				if (null != rsItem && !rsItem.isClosed()) {
					rsItem.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
			try {
				if (null != rs && !rs.isClosed()) {
					rs.close();
				}
			} catch (SQLException e) {
				logger.error("Failed to close the resultset!", e);
			}
			tracer.dump();
		}
		
		return result;
	}
	
	private void processSetCAResults(ResultSet rs, ScwUserLookupResult result) throws TopazException{

		try {
			
			List<ScwUserLookupCAResult> caResultList = new ArrayList<ScwUserLookupCAResult>();
			
			
			
			do {
				
				
					ScwUserLookupCAResult caResult = new ScwUserLookupCAResult();
					caResult.setSoldToCustNum(rs.getString("sold_to_cust_num") == null ? rs.getString("sold_to_cust_num") : rs.getString("sold_to_cust_num").trim());
					caResult.setSapCtrctNum(rs.getString("SAP_CTRCT_NUM") == null? rs.getString("SAP_CTRCT_NUM") : rs.getString("SAP_CTRCT_NUM").trim());
					caResult.setChargeAgreement(rs.getString("sap_sales_ord_num") == null? rs.getString("sap_sales_ord_num"): rs.getString("sap_sales_ord_num").trim());
					caResult.setPaymentType(rs.getString("payment_method") == null? rs.getString("payment_method"): rs.getString("payment_method").trim());
					caResult.setProfileID(rs.getString("PS_PROFILE_ID") == null ? rs.getString("PS_PROFILE_ID"): rs.getString("PS_PROFILE_ID").trim());
					caResult.setUserAssociatedWithProfile(rs.getInt("is_associa_with_profile") == 1 ? "yes" : "no");
					caResult.setPurchaseOrderNumber(rs.getString("bill_to_purch_ord_num") == null? rs.getString("bill_to_purch_ord_num"): rs.getString("bill_to_purch_ord_num").trim());
					
					Payer payer = new Payer();
					payer.setPayerCustNum(rs.getString("CUST_NUM") == null ? rs.getString("CUST_NUM") : rs.getString("CUST_NUM").trim());
					
					CustomerName custName = new CustomerName();
					custName.setCustName(rs.getString("CUST_NAME")==null?rs.getString("CUST_NAME"):rs.getString("CUST_NAME").trim());
					custName.setCustName2(rs.getString("CUST_NAME_2")==null?rs.getString("CUST_NAME_2"):rs.getString("CUST_NAME_2").trim());
					
					payer.setCustomerName(custName);
					
					CustomerFullAddress custAddress = new CustomerFullAddress();
					custAddress.setAdr1(rs.getString("ADR_1")==null?rs.getString("ADR_1"):rs.getString("ADR_1").trim());
					custAddress.setAdrIntrnl(rs.getString("ADR_INTRNL")==null?rs.getString("ADR_INTRNL"):rs.getString("ADR_INTRNL").trim());
					custAddress.setCity(rs.getString("CITY")==null?rs.getString("CITY"):rs.getString("CITY").trim());
					custAddress.setPoBox(rs.getString("P_O_BOX")==null?rs.getString("P_O_BOX"):rs.getString("P_O_BOX").trim());
					custAddress.setPostalCode(rs.getString("POSTAL_CODE")==null?rs.getString("POSTAL_CODE"):rs.getString("POSTAL_CODE").trim());
					custAddress.setSapRegionCode(rs.getString("SAP_REGION_CODE")==null?rs.getString("SAP_REGION_CODE"):rs.getString("SAP_REGION_CODE").trim());
					custAddress.setCntryCode(rs.getString("CNTRY_CODE")==null?rs.getString("CNTRY_CODE"):rs.getString("CNTRY_CODE").trim());
					custAddress.setPhoneNumber(rs.getString("SAP_INTL_PHONE_NUM_FULL")==null?rs.getString("SAP_INTL_PHONE_NUM_FULL"):rs.getString("SAP_INTL_PHONE_NUM_FULL").trim());
					
					payer.setCustomerAddress(custAddress);
					
					caResult.setPayer(payer);
					
					CCHolder holder = new CCHolder();
					holder.setFirstName(rs.getString("cnt_first_name")==null?rs.getString("cnt_first_name"):rs.getString("cnt_first_name").trim());
					holder.setLastName(rs.getString("CNT_LAST_NAME")==null?rs.getString("CNT_LAST_NAME"):rs.getString("CNT_LAST_NAME").trim());
					holder.setCcHolderPhoneNumber(rs.getString("cc_holder_phone")==null?rs.getString("cc_holder_phone"):rs.getString("cc_holder_phone").trim());
					holder.setEmailAddr(rs.getString("CNT_EMAIL_ADR")==null?rs.getString("CNT_EMAIL_ADR"):rs.getString("CNT_EMAIL_ADR").trim());
					holder.setCcHolderFaxNumber(rs.getString("CNT_FAX_NUM_FULL")==null?rs.getString("CNT_FAX_NUM_FULL"):rs.getString("CNT_FAX_NUM_FULL").trim());
					
					caResult.setHolder(holder);
					
					caResultList.add(caResult);
				
					
				
			} while (rs.next());
			
			result.setScwUserLookupCAResultList(caResultList);
			
			
		} catch (Exception e) {
			logger.error("Failed to get CA results infomation", e);
			throw new TopazException(e);
		}
		
	}
	
	private void processSetConfigResults(ResultSet rs, ScwUserLookupResult result) throws TopazException{
		try {
			List<ScwUserLookupConfigResult> configResultList = new ArrayList<ScwUserLookupConfigResult>();
			do {
				ScwUserLookupConfigResult configResult = new ScwUserLookupConfigResult();
				configResult.setSapSalesOrdNum(rs.getString("sap_sales_ord_num") == null? rs.getString("sap_sales_ord_num"): rs.getString("sap_sales_ord_num").trim());
				configResult.setConfigrtnID(rs.getString("CONFIGRTN_ID") == null? rs.getString("CONFIGRTN_ID"):rs.getString("CONFIGRTN_ID").trim());
				configResult.setUpdateFlag(rs.getInt("update_flag"));
				configResult.setIsProvisioningHold(rs.getInt("isProvisioningHold"));
				configResult.setHasRenwlMdl(rs.getInt("hasRenwlMd"));
				configResult.setEndDate(rs.getString("end_date") == null? rs.getString("end_date"): rs.getString("end_date").trim() );

				configResultList.add(configResult);
				
			} while (rs.next());
			result.setScwUserLookupConfigResultList(configResultList);
		} catch (Exception e) {
			logger.error("Failed to get config results infomation", e);
			throw new TopazException(e);
		}
		
	}
	
	private void processSetConfigDetailResults(ResultSet rs, ScwUserLookupResult result) throws TopazException{
		try {
			List<ScwUserLookupConfigDetailResult> configDetailResultList = new ArrayList<ScwUserLookupConfigDetailResult>();
			do {
				ScwUserLookupConfigDetailResult configDetailResult = new ScwUserLookupConfigDetailResult();
				configDetailResult.setSoldToCustNum(rs.getString("sold_to_cust_num") == null? rs.getString("sold_to_cust_num"): rs.getString("sold_to_cust_num").trim());
				configDetailResult.setSapSalesOrdNum(rs.getString("sap_sales_ord_num") == null? rs.getString("sap_sales_ord_num"): rs.getString("sap_sales_ord_num").trim());
				configDetailResult.setSapCtrctNum(rs.getString("SAP_CTRCT_NUM") == null? rs.getString("SAP_CTRCT_NUM"): rs.getString("SAP_CTRCT_NUM").trim());
				configDetailResult.setLineItemSeqNum(rs.getInt("LINE_ITEM_SEQ_NUM"));
				configDetailResult.setPartNum(rs.getString("part_num") == null? rs.getString("part_num"): rs.getString("part_num").trim());
				configDetailResult.setPartQty(rs.getDouble("PART_QTY"));
				configDetailResult.setSapBillingFrequencyOptCode(rs.getString("SAP_BILLG_FRQNCY_OPT_CODE")==null? rs.getString("SAP_BILLG_FRQNCY_OPT_CODE"):rs.getString("SAP_BILLG_FRQNCY_OPT_CODE").trim());
				configDetailResult.setCoverageTerm(rs.getInt("CVRAGE_TERM"));
				configDetailResult.setRenewModelCode(rs.getString("RENWL_MDL_CODE")==null?rs.getString("RENWL_MDL_CODE"):rs.getString("RENWL_MDL_CODE").trim());
				configDetailResult.setSaasTotalCommitmentValue(rs.getDouble("SAAS_TOT_CMMTMT_VAL"));
                Date endDate = rs.getDate("END_DATE");
                //configDetailResult.setEndDate(endDate == null ? "" : DateUtil.formatDate(endDate, DateUtil.PATTERN6));
                configDetailResult.setEndDate(rs.getString("end_date")==null?rs.getString("end_date"):rs.getString("end_date").trim());
				configDetailResult.setCurrentPrice(rs.getString("CURRENT_PRICE")==null?rs.getString("CURRENT_PRICE"):rs.getString("CURRENT_PRICE").trim());
				configDetailResult.setConfigrtnID(rs.getString("CONFIGRTN_ID")==null?rs.getString("CONFIGRTN_ID"):rs.getString("CONFIGRTN_ID").trim());
                configDetailResult.setSaasTotalCommitmentValue(rs.getDouble("SAAS_TOT_CMMTMT_VAL"));
                // added by Jack for RAMP_UP_FLAG: We should not be returning expired ramp up line items in the User
                // Lookup Service.
                // Notes://ltsgdb001b/85256B83004B1D94/CA30E8393BC22D28482573A7000E50E0/E92BA80ED0AC822385257E0C00550E23
                configDetailResult.setRampUp(PartPriceConstants.RAMP_UP_FLAG_YES.equals(rs.getString("RAMP_UP_FLAG")));

                if (configDetailResult.isRampUp() && DateUtil.isExpirationDateBeforeToday(endDate)) {
                    continue;
                } else {
                    configDetailResultList.add(configDetailResult);
                }
				
			} while (rs.next());
			result.setScwUserLookupConfigDetailResultList(configDetailResultList);
		} catch (Exception e) {
			logger.error("Failed to get config detail results infomation", e);
			throw new TopazException(e);
		}
		
	}
	
	private void processSetCustResults(ResultSet rs, ScwUserLookupResult result, boolean flag) throws TopazException{
		try {
			List<ScwUserLookupCustResult> custResultList = new ArrayList<ScwUserLookupCustResult>();
			do {
				ScwUserLookupCustResult custResult = new ScwUserLookupCustResult();
				custResult.setCustNum(rs.getString("CUST_NUM")==null?rs.getString("CUST_NUM"):rs.getString("CUST_NUM").trim());
				custResult.setIfCreditCardAndPoAllow(rs.getInt("RSTRCTD_PAYMENT") == 1 ? true : false);
				CustomerName custName = new CustomerName();
				custName.setCustName(rs.getString("CUST_NAME")==null?rs.getString("CUST_NAME"):rs.getString("CUST_NAME").trim());
				custName.setCustName2(rs.getString("CUST_NAME_2")==null?rs.getString("CUST_NAME_2"):rs.getString("CUST_NAME_2").trim());
				custResult.setCustomerName(custName);
				CustomerFullAddress custAddress = new CustomerFullAddress();
				custAddress.setAdr1(rs.getString("ADR_1")==null?rs.getString("ADR_1"):rs.getString("ADR_1").trim());
				custAddress.setAdrIntrnl(rs.getString("ADR_INTRNL")==null?rs.getString("ADR_INTRNL"):rs.getString("ADR_INTRNL").trim());
				custAddress.setCity(rs.getString("CITY")==null?rs.getString("CITY"):rs.getString("CITY").trim());
				custAddress.setPoBox(rs.getString("P_O_BOX")==null?rs.getString("P_O_BOX"):rs.getString("P_O_BOX").trim());
				custAddress.setPostalCode(rs.getString("POSTAL_CODE")==null?rs.getString("POSTAL_CODE"):rs.getString("POSTAL_CODE").trim());
				custAddress.setSapRegionCode(rs.getString("SAP_REGION_CODE")==null?rs.getString("SAP_REGION_CODE"):rs.getString("SAP_REGION_CODE").trim());
				custAddress.setCntryCode(rs.getString("CNTRY_CODE")==null?rs.getString("CNTRY_CODE"):rs.getString("CNTRY_CODE").trim());
				custAddress.setPhoneNumber(rs.getString("SAP_INTL_PHONE_NUM_FULL")==null?rs.getString("SAP_INTL_PHONE_NUM_FULL"):rs.getString("SAP_INTL_PHONE_NUM_FULL").trim());
				custAddress.setVatNumber(rs.getString("CUST_VAT_NUM")==null?rs.getString("CUST_VAT_NUM"):rs.getString("CUST_VAT_NUM").trim());
				custResult.setCustomerAddress(custAddress);
				custResult.setCurrencyCode(rs.getString("CURRNCY_CODE")==null?rs.getString("CURRNCY_CODE"):rs.getString("CURRNCY_CODE").trim());
				if (flag) {
					custResult.setConfigStatus(rs.getInt("pid_status"));
				}
				custResultList.add(custResult);
				
			} while (rs.next());
			result.setScwUserLookupCustResultList(custResultList);
		} catch (Exception e) {
			logger.error("Failed to get customer results infomation", e);
			throw new TopazException(e);
		}
		
	}
	
	private void processSetContractResults(ResultSet rs, ScwUserLookupResult result) throws TopazException{
		try {
			List<ScwUserLookupContractResult> contractResultList = new ArrayList<ScwUserLookupContractResult>();
			do {
				ScwUserLookupContractResult contractResult = new ScwUserLookupContractResult();
				contractResult.setCustNum(rs.getString("CUST_NUM")==null?rs.getString("CUST_NUM"):rs.getString("CUST_NUM").trim());
				contractResult.setContractNo(rs.getString("SAP_CTRCT_NUM")==null?rs.getString("SAP_CTRCT_NUM"):rs.getString("SAP_CTRCT_NUM").trim());
				contractResult.setContractType(rs.getString("LINE_OF_BUS_CODE")==null?rs.getString("LINE_OF_BUS_CODE"):rs.getString("LINE_OF_BUS_CODE").trim());
				contractResult.setPriceBand(rs.getString("VOL_DISC_LEVEL_CODE")==null?rs.getString("VOL_DISC_LEVEL_CODE"):rs.getString("VOL_DISC_LEVEL_CODE").trim());
				contractResult.setProducts(Arrays.asList(org.apache.commons.lang.StringUtils.split(rs.getString("PRODUCT_COVER").trim(), "+")));
				contractResultList.add(contractResult);
				
			} while (rs.next());
			result.setScwUserLookupContractResultList(contractResultList);
		} catch (Exception e) {
			logger.error("Failed to get contract results infomation", e);
			throw new TopazException(e);
		}
		
	}
	
	private void processSetConfigDscrResults(ResultSet rs, ScwUserLookupResult result) throws TopazException{
		try {
			List<ScwUserLookupConfigResult> configResultList = result.getScwUserLookupConfigResultList();
			if (configResultList != null) {
				do {
					String orderNumber = rs.getString("sap_sales_ord_num");
					String configNumber = rs.getString("CONFIGRTN_ID");
					if(orderNumber != null && configNumber != null) {
						for (ScwUserLookupConfigResult configResult : configResultList) {
							if (configResult.getConfigrtnID().equalsIgnoreCase(configNumber.trim()) && configResult.getChargeAgreement().equalsIgnoreCase(orderNumber.trim())){
								String shortDscr = rs.getString("short_dscr");
								if (shortDscr != null) {
									String dscrList = configResult.getPartDescriptionList();
									if (dscrList == null || dscrList.equalsIgnoreCase("")){
										dscrList = shortDscr.trim();
										configResult.setPartDescriptionList(dscrList);
									} 
								} 
							}
						}
					}
				} while (rs.next());
			}
		} catch (Exception e) {
			logger.error("Failed to get contract results infomation", e);
			throw new TopazException(e);
		}
	}
	
    private void calculateRemainTerm(List<ScwUserLookupConfigResult> configResultList,
            List<ScwUserLookupConfigDetailResult> configDetailResultList) throws TopazException, QuoteException {
		if (configResultList != null && configDetailResultList != null) {
            QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
            for (ScwUserLookupConfigResult configResult : configResultList) {
                String configrtnID = configResult.getConfigrtnID();
                String chrgAgrmtNum = configResult.getChargeAgreement();

                // get activeServiceMap of CA
                Map<String, ActiveService> activeServicMap = QuoteCommonUtil.getActiveServiceMap(quoteProcess, chrgAgrmtNum,
                        configrtnID);
                for (ScwUserLookupConfigDetailResult configDetailResult : configDetailResultList){
                    String detailResultConfigrtnID = configDetailResult.getConfigrtnID();
                    if (StringUtils.equals(detailResultConfigrtnID, configrtnID)) {
                        QuoteLineItem_jdbc hookItem = new QuoteLineItem_jdbc();
                        // init renwlMdlCode value for hookItem
                        // Please notice the difference between
                        // com.ibm.dsw.quote.common.domain.QuoteLineItem_Impl.getRenwlMdlCode() and
                        // configDetailResult.getRenewModelCode(), below line bases on
                        // value of configDetailResult.getRenewModelCode() is correct.
                        hookItem.renwlMdlCode = configDetailResult.getRenewModelCode();

                        ActiveService activeService = activeServicMap.get(configDetailResult.getPartNum());
                        // call calculateRemainingTermForLineItem to calculate remainTerm for hookItem, then set
                        // remainTerm attribute for configDetailResult
                        
                        Date provsngDate = DateUtil.getCurrentDate();
                        if (activeService != null && activeService.getEndDate() != null) {
                            QuoteCommonUtil.calculateRemainingTermForLineItem(activeService, hookItem, provsngDate);
                            configDetailResult.setRemainingTerm(hookItem.getRemainingTermTillCAEndDate());
                        }
                    }
                }

            }
		}

	}

}
