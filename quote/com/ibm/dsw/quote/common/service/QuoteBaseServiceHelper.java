package com.ibm.dsw.quote.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import DswSalesLibrary.DocumentStatus;
import DswSalesLibrary.PartnerAddress;
import DswSalesLibrary.PartnerFunction;

import com.ibm.dsw.quote.common.domain.ApplianceAddress;
import com.ibm.dsw.quote.common.domain.ApplianceLineItem;
import com.ibm.dsw.quote.common.domain.ApplianceLineItemAddrDetail;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteStatus;
import com.ibm.dsw.quote.common.domain.QuoteStatusFactory;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteBaseServiceHelper<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Oct 20, 2008
 */

public class QuoteBaseServiceHelper {
    
    protected LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    public static final String EMPTY = "";
    
    public static final String SAP_DATE_FORMAT = "yyyyMMdd";
    public static final String SAP_DATETIME_FORMAT = "yyyyMMddHHmmss";
    
    public static final String CUST_PRTNR_FUNC_SOLD_TO = "AG";
    public static final String CUST_PRTNR_FUNC_SALES_REP = "ZX";
    public static final String CUST_PRTNR_FUNC_QUOTE_CONTACT = "ZW";
    public static final String CUST_PRTNR_FUNC_PAYER = "RG";
    public static final String CUST_PRTNR_FUNC_BILLTO = "RE";
    public static final String CUST_PRTNR_FUNC_RSEL = "YR";
    public static final String CUST_PRTNR_FUNC_SHIPTO = "WE";
    public static final String CUST_PRTNR_FUNC_ZG = "ZG";
    public static final String CUST_PRTNR_FUNC_YO = "Y0";
    public static final String CUST_PRTNR_FUNC_Z2 = "Z2";
    public static final String CUST_PRTNR_FUNC_Z0 = "Z0";
    
	public static final String SQ_STAT_ICN_REQUESTED = "E0001";
	public static final String SQ_STAT_PRECREDIT_REQUESTED = "E0002";
	public static final String SQ_STAT_SUBMITTED = "E0003";
	public static final String SQ_STAT_SPECIAL_BID_APPROVED = "E0006";
	public static final String SQ_STAT_SPECIAL_BID_CANCELLED = "E0007";
	public static final String SQ_STAT_QT_TERMINATED = "E0009";
	public static final String SQ_STAT_SPECIAL_BID_REQUESTED = "E0013";
	public static final String SQ_STAT_PPSS = "E0020";
	public static final String SQ_STAT_PRTNR_TBD = "E0027";
	public static final String SQ_STAT_ELA = "E0029";
	public static final String SQ_STAT_REBILL = "E0030";
	public static final String SQ_STAT_PAO_BLOCK = "E0031";
	public static final String SQ_STAT_SPECIAL_BID_TS_CS_HOLD = "E0022";
	  
	public static final String RQ_STAT_SALES_FOR_REVIEW = "E0002";
    public static final String RQ_STAT_OPEN= "E0003";
    public static final String RQ_STAT_PART_RNW_BAL_GPE = "E0019";
    public static final String RQ_STAT_ICN_REQUESTED = "E0041";
    public static final String RQ_STAT_PRE_CRED_CHECK_REQUESTED = "E0043";
	public static final String RQ_STAT_SPECIAL_BID_REQUESTED = "E0045";
	public static final String RQ_STAT_SPECIAL_BID_APPROVED = "E0046";
	public static final String RQ_STAT_SPECIAL_BID_CANCELLED = "E0047";
    public static final String RQ_STAT_SPECIAL_BID_REJECTED = "E0048";
    public static final String RQ_STAT_SPECIAL_BID_EXPIRED = "E0054";
  
	
	public static final int MAX_COMMENT_LINE_COUNT = 15;
	public static final int MAX_COMMENT_LINE_LENGTH = 70;
	
	public static final String QUOTE_ITEM_REF_DOC_TYPE_PS_PARTS = "G";
	public static final String QUOTE_ITEM_REF_DOC_TYPE_RQ_PARTS = "B";
	public static final String QUOTE_ITEM_REF_DOC_TYPE_RL_PARTS = "C";//being replaced
	
	public static final int MAX_NAME_LENGTH = 35;
	public static final int MAX_ADDR_LENGTH = 35;
	public static final int MAX_CITY_LENGTH = 35;
	public static final int MAX_POSTAL_LENGTH = 10;
	public static final int MAX_PHONE_LENGTH = 16;
	public static final int MAX_FAX_LENGTH = 31;
	public static final int MAX_EMAIL_LENGTH = 80;
		
	public static final String ORDER_METHOD_SALES_QUOTE = "ZSQT";
	public static final String ORDER_METHOD_FCT_TO_PA = "FMP";
	public static final String ORDER_METHOD_PGS = "PGS";
	
	public static final String TEXT_IDENTIFIER_HEADER_LEVEL = "ZZ15";
	public static final String TEXT_IDENTIFIER_ITEM_LEVEL = "ZZ07";
	
	public static final int QUOTE_MODIFY_SHIPTO = 1;
	public static final int QUOTE_MODIFY_INSTALLAT = 2;
	
	public static final String AGREEMENT_TYPE_CODE = "N/A";

    public QuoteBaseServiceHelper() {
        super();
    }
    
    protected void addDocumentStatus(List list, String status, boolean active) {
        DocumentStatus ds = new DocumentStatus();
        ds.setStatusCode(status);
        ds.setActiveFlag(active);
        list.add(ds);
    }
    
    protected Map getQuoteStatusCodes(List quoteStatusList) {
        
        HashMap statusCodes = new HashMap();
        for (int i = 0; quoteStatusList != null && i < quoteStatusList.size(); i++) {
            QuoteStatus status = (QuoteStatus) quoteStatusList.get(i);
            String statusCode = StringUtils.trimToEmpty(status.getStatusCode());
            if (StringUtils.isNotBlank(statusCode))
                statusCodes.put(statusCode, statusCode);
        }
        
        return statusCodes;
    }
    
    private void getWebStatusChanges(StringBuffer sb, Map srcStats, Map destStats) {
        Iterator iter = srcStats.keySet().iterator();
        while (iter.hasNext()) {
            String status = (String) iter.next();
            if (!destStats.containsKey(status)) {
                if (sb.length() == 0)
                    sb.append(status);
                else
                    sb.append("," + status);
            }
        }
    }
    
    protected void persistWebStatusChange(Quote quote, Map webPrimStatuses, Map webScndStatuses) throws TopazException {
        Map origPrimStatuses = this.getQuoteStatusCodes(quote.getAllWebPrimaryStatuses());
        Map origScndStatuses = this.getQuoteStatusCodes(quote.getAllWebSecondayStatuses());
        
        StringBuffer sbActStatuses = new StringBuffer();
        StringBuffer sbInactStatuses = new StringBuffer();
        
        getWebStatusChanges(sbActStatuses, webPrimStatuses, origPrimStatuses);
        getWebStatusChanges(sbActStatuses, webScndStatuses, origScndStatuses);
        getWebStatusChanges(sbInactStatuses, origPrimStatuses, webPrimStatuses);
        getWebStatusChanges(sbInactStatuses, origScndStatuses, webScndStatuses);
        
        if (sbActStatuses.length() > 0 || sbInactStatuses.length() > 0) {
            QuoteStatusFactory.singleton().UpdateWebQuoteStatus(quote.getQuoteHeader().getWebQuoteNum(),
                    sbActStatuses.toString(), sbInactStatuses.toString());
        }
    }
    
    protected void persistWebStatusChange(QuoteHeader header, List docStatList) throws TopazException {
        if (docStatList == null)
            return;
        StringBuffer sbActStatuses = new StringBuffer();
        StringBuffer sbInactStatuses = new StringBuffer();
        
        for (int i = 0; i < docStatList.size(); i++) {
            DocumentStatus ds = (DocumentStatus) docStatList.get(i);
            if (ds.isActiveFlag()) {
                if (sbActStatuses.length() == 0)
                    sbActStatuses.append(ds.getStatusCode());
                else
                    sbActStatuses.append("," + ds.getStatusCode());
            }
            else {
                if (sbInactStatuses.length() == 0)
                    sbInactStatuses.append(ds.getStatusCode());
                else
                    sbInactStatuses.append("," + ds.getStatusCode());
            }
        }
        
        if (sbActStatuses.length() > 0 || sbInactStatuses.length() > 0) {
            QuoteStatusFactory.singleton().UpdateWebQuoteStatus(header.getWebQuoteNum(), sbActStatuses.toString(),
                    sbInactStatuses.toString());
        }
    }
    
    protected String getWebQuoteNumInfo(QuoteHeader header) {
        if (header == null)
            return "";
        else
            return " (Web quote num: " + header.getWebQuoteNum() + ")";
    }
    
    protected ArrayList  setContactAddress(int handleInt,QuoteHeader qtHeader,ApplianceLineItemAddrDetail applianceLineItemAddrDetail){
    	if (qtHeader == null || qtHeader.getWebQuoteNum() == null)
            return new ArrayList();
        
        ArrayList prtnrAddrList = new ArrayList();
		if(applianceLineItemAddrDetail != null){
			
			if((handleInt == 0 || QUOTE_MODIFY_SHIPTO == handleInt) && applianceLineItemAddrDetail.getShipToAddressList() != null )
			{
				for(ApplianceAddress address :applianceLineItemAddrDetail.getShipToAddressList()){
					if ( address.isAddrBaseMTM() )
					{
						continue;
					}
					if(address.isHeadLevelAddr()){
			        	PartnerAddress prtnrAddressQuoteContact = new PartnerAddress();
			        	prtnrAddressQuoteContact.setPartnerFunctionCode(CUST_PRTNR_FUNC_Z2);
			        	
			        	prtnrAddressQuoteContact.setPartnerCustomerNumber(String.valueOf(address.getSapCntId()));
			        	prtnrAddressQuoteContact.setName1(CommonServiceUtil.limitLength(address.getCntFirstName(), MAX_NAME_LENGTH));
			        	prtnrAddressQuoteContact.setName2(CommonServiceUtil.limitLength(address.getCntLastName(), MAX_NAME_LENGTH));
			        	prtnrAddressQuoteContact.setTelephoneNumber(CommonServiceUtil.limitLength(address.getSapIntlPhoneNumFull(), MAX_PHONE_LENGTH));
				        prtnrAddressQuoteContact.setItemNumber(0);
				        prtnrAddrList.add(prtnrAddressQuoteContact);
				        						
					}
			        for(ApplianceLineItem item:address.getLineItemList()){
			        	if(item.isAppliancePart()){
			        	PartnerAddress prtnrAddressQuoteContact = new PartnerAddress();
			        	prtnrAddressQuoteContact.setPartnerFunctionCode(CUST_PRTNR_FUNC_Z2);
			        	
			        	prtnrAddressQuoteContact.setPartnerCustomerNumber(String.valueOf(address.getSapCntId()));
			        	prtnrAddressQuoteContact.setName1(CommonServiceUtil.limitLength(address.getCntFirstName(), MAX_NAME_LENGTH));
			        	prtnrAddressQuoteContact.setName2(CommonServiceUtil.limitLength(address.getCntLastName(), MAX_NAME_LENGTH));
			        	prtnrAddressQuoteContact.setTelephoneNumber(CommonServiceUtil.limitLength(address.getSapIntlPhoneNumFull(), MAX_PHONE_LENGTH));
				        prtnrAddressQuoteContact.setItemNumber(item.getDestSeqNum());
				        prtnrAddrList.add(prtnrAddressQuoteContact);
			        	}
			        }
				}	
			}
			
			
			if((handleInt == 0 || QUOTE_MODIFY_INSTALLAT ==handleInt) && qtHeader.isDisShipInstAdrFlag() )
			{
				// Set ship to head level address as install at head level address
				if ( applianceLineItemAddrDetail.getShipToAddressList() != null )
				{
					for(ApplianceAddress address :applianceLineItemAddrDetail.getShipToAddressList()){
						if ( address.isAddrBaseMTM() )
						{
							continue;
						}
						if(address.isHeadLevelAddr()){
				        	PartnerAddress prtnrAddressQuoteContactZ0 = new PartnerAddress();
				        	prtnrAddressQuoteContactZ0.setPartnerFunctionCode(CUST_PRTNR_FUNC_Z0);
				        	
				        	prtnrAddressQuoteContactZ0.setPartnerCustomerNumber(String.valueOf(address.getSapCntId()));
				        	prtnrAddressQuoteContactZ0.setName1(CommonServiceUtil.limitLength(address.getCntFirstName(), MAX_NAME_LENGTH));
				        	prtnrAddressQuoteContactZ0.setName2(CommonServiceUtil.limitLength(address.getCntLastName(), MAX_NAME_LENGTH));
				        	prtnrAddressQuoteContactZ0.setTelephoneNumber(CommonServiceUtil.limitLength(address.getSapIntlPhoneNumFull(), MAX_PHONE_LENGTH));
				        	prtnrAddressQuoteContactZ0.setItemNumber(0);
					        prtnrAddrList.add(prtnrAddressQuoteContactZ0);
					        
						}
					}
				}
				if ( applianceLineItemAddrDetail.getInstallAtAddressList() != null )
				{
					for(ApplianceAddress address :applianceLineItemAddrDetail.getInstallAtAddressList()){
						if ( address.isAddrBaseMTM() )
						{
							continue;
						}
						 for(ApplianceLineItem item:address.getLineItemList()){
							 if(item.isAppliancePart()){
								PartnerAddress prtnrAddressQuoteContact = new PartnerAddress();
								prtnrAddressQuoteContact.setPartnerFunctionCode(CUST_PRTNR_FUNC_Z0);
					
						        prtnrAddressQuoteContact.setPartnerCustomerNumber(String.valueOf(address.getSapCntId()));
						        prtnrAddressQuoteContact.setName1(CommonServiceUtil.limitLength(address.getCntFirstName(), MAX_NAME_LENGTH));
						        prtnrAddressQuoteContact.setName2(CommonServiceUtil.limitLength(address.getCntLastName(), MAX_NAME_LENGTH));
						        prtnrAddressQuoteContact.setTelephoneNumber(CommonServiceUtil.limitLength(address.getSapIntlPhoneNumFull(), MAX_PHONE_LENGTH));
						        prtnrAddressQuoteContact.setItemNumber(item.getDestSeqNum());
						        prtnrAddrList.add(prtnrAddressQuoteContact);			
							 }	
						 }	 
					}
				}
			}
		}
		
		return prtnrAddrList;//
    }
    
    protected ArrayList setShipInstallAddress(int handleInt,QuoteHeader qtHeader,ApplianceLineItemAddrDetail applianceLineItemAddrDetail){
    	ArrayList funcList = new ArrayList();
    	if (qtHeader == null || qtHeader.getWebQuoteNum() == null)
            return funcList;
    	
    	// get all ship to appliance address detail
		if(applianceLineItemAddrDetail != null){
			String headLevelAddrCustNum = "";
			String headLevelAddrContactId = "";
			if((handleInt == 0 || QUOTE_MODIFY_SHIPTO == handleInt) && applianceLineItemAddrDetail.getShipToAddressList() != null)
			{
				// Ship to address select 'Select new ship to addresses ' radio
				for(ApplianceAddress address :applianceLineItemAddrDetail.getShipToAddressList()){
					logContext.debug(this, "addr before send to sap: " + address.isHeadLevelAddr() + ":" + address.isAddrBaseMTM() + ":" + address.getCustNum() + ":" + address.hashCode());
					if(address.isHeadLevelAddr()){
						PartnerFunction partnerFunctionHeadAddress = new PartnerFunction();
						partnerFunctionHeadAddress.setPartnerFunctionCode(CUST_PRTNR_FUNC_SHIPTO);
						partnerFunctionHeadAddress.setPartnerCustomerNumber(address.getCustNum());
						partnerFunctionHeadAddress.setItemNumber(0);
						
						funcList.add(partnerFunctionHeadAddress);
						headLevelAddrCustNum = address.getCustNum();
						headLevelAddrContactId = address.getSapCntId();
					}
					
					for(ApplianceLineItem lineItem:address.getLineItemList()){
						logContext.debug(this, "line items from address: " + lineItem.getPartNum() + ":" + lineItem.getDestSeqNum());
						if(lineItem.isAppliancePart()){
							PartnerFunction partnerFunctionAddress = new PartnerFunction();
							partnerFunctionAddress.setPartnerFunctionCode(CUST_PRTNR_FUNC_SHIPTO);
							partnerFunctionAddress.setPartnerCustomerNumber(address.getCustNum());
							partnerFunctionAddress.setItemNumber(lineItem.getDestSeqNum());	
							logContext.debug(this, "set partner function item number: " + address.isAddrBaseMTM() + ";" + lineItem.getSapLineItemSeqNum());
							if ( address.isAddrBaseMTM() && lineItem.getSapLineItemSeqNum() > 0 )
							{
								partnerFunctionAddress.setItemNumber(lineItem.getSapLineItemSeqNum());
							}
							funcList.add(partnerFunctionAddress);
						}
					}
					
				}
				// Ship to address select 'Select new ship to addresses ' radio
				for(ApplianceAddress address :applianceLineItemAddrDetail.getShipToAddressList()){
					if ( address.isAddrBaseMTM() )
					{
						continue;
					}
					if(address.isHeadLevelAddr()){
						
						PartnerFunction partnerFunctionHeadAddressZ2 = new PartnerFunction();
						partnerFunctionHeadAddressZ2.setPartnerFunctionCode(CUST_PRTNR_FUNC_Z2);
						partnerFunctionHeadAddressZ2.setPartnerCustomerNumber(String.valueOf(address.getSapCntId()));
						partnerFunctionHeadAddressZ2.setItemNumber(0);
						
						funcList.add(partnerFunctionHeadAddressZ2);	
						headLevelAddrCustNum = address.getCustNum();
						headLevelAddrContactId = address.getSapCntId();
					}
					
					for(ApplianceLineItem lineItem:address.getLineItemList()){
						if(lineItem.isAppliancePart() && !address.isAddrBaseMTM()){
							PartnerFunction partnerFunctionAddressZ2 = new PartnerFunction();
							partnerFunctionAddressZ2.setPartnerFunctionCode(CUST_PRTNR_FUNC_Z2);
							partnerFunctionAddressZ2.setPartnerCustomerNumber(String.valueOf(address.getSapCntId()));
							partnerFunctionAddressZ2.setItemNumber(lineItem.getDestSeqNum());	
							
							funcList.add(partnerFunctionAddressZ2);
						}
					}
					
				}
			}
			
			if( qtHeader.isDisShipInstAdrFlag() && (handleInt == 0  || QUOTE_MODIFY_INSTALLAT == handleInt))
			{
				if(StringUtils.isBlank(headLevelAddrCustNum) && applianceLineItemAddrDetail.getShipToAddressList() != null){
					List<ApplianceAddress> shipToAddressList = applianceLineItemAddrDetail.getShipToAddressList();
					// Ship to address select 'Use sold to for all addresses' radio
					if(null == shipToAddressList||shipToAddressList.size() == 0){
						headLevelAddrCustNum = qtHeader.getSoldToCustNum();
					}else{
						// Ship to address select 'Select new ship to addresses ' radio
						for(ApplianceAddress address :applianceLineItemAddrDetail.getShipToAddressList()){
							if(address.isHeadLevelAddr()){
								headLevelAddrCustNum = address.getCustNum();
								headLevelAddrContactId = address.getSapCntId();
								break;
							}
						}			
					}
				}
				
				// set all install at appliance address detail
				if(StringUtils.isNotBlank(headLevelAddrCustNum)){
					PartnerFunction partnerFunctionAddress = new PartnerFunction();
					partnerFunctionAddress.setPartnerFunctionCode(CUST_PRTNR_FUNC_YO);
					partnerFunctionAddress.setPartnerCustomerNumber(headLevelAddrCustNum);
					partnerFunctionAddress.setItemNumber(0);
					
					funcList.add(partnerFunctionAddress);
				}			
				if ( applianceLineItemAddrDetail.getInstallAtAddressList() == null )
				{
					return funcList;
				}
				for(ApplianceAddress address :applianceLineItemAddrDetail.getInstallAtAddressList()){
					for(ApplianceLineItem lineItem:address.getLineItemList()){
						if(lineItem.isAppliancePart()){
							PartnerFunction IstallAtAddress = new PartnerFunction();
							IstallAtAddress.setPartnerFunctionCode(CUST_PRTNR_FUNC_YO);
							IstallAtAddress.setPartnerCustomerNumber(address.getCustNum());
							IstallAtAddress.setItemNumber(lineItem.getDestSeqNum());
							
							funcList.add(IstallAtAddress);
						}	        
					}
				}						
				
				if(StringUtils.isNotBlank(headLevelAddrContactId)){
					PartnerFunction partnerFunctionAddressZ0 = new PartnerFunction();
					partnerFunctionAddressZ0.setPartnerFunctionCode(CUST_PRTNR_FUNC_Z0);
					partnerFunctionAddressZ0.setPartnerCustomerNumber(headLevelAddrContactId);
					partnerFunctionAddressZ0.setItemNumber(0);
					
					funcList.add(partnerFunctionAddressZ0);
				}					
				for(ApplianceAddress address :applianceLineItemAddrDetail.getInstallAtAddressList()){
					for(ApplianceLineItem lineItem:address.getLineItemList()){
						if(lineItem.isAppliancePart()){
							PartnerFunction IstallAtAddressZ0 = new PartnerFunction();
							IstallAtAddressZ0.setPartnerFunctionCode(CUST_PRTNR_FUNC_Z0);
							IstallAtAddressZ0.setPartnerCustomerNumber(String.valueOf(address.getSapCntId()));
							IstallAtAddressZ0.setItemNumber(lineItem.getDestSeqNum());
							funcList.add(IstallAtAddressZ0);
						}			
					}
				}
			}
		}
					
		
		return funcList;//(PartnerFunction[]) funcList.toArray(new PartnerFunction[0]);
    }

}
