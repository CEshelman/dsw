package com.ibm.dsw.quote.customer.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.security.SecurityUtil;
import com.ibm.dsw.common.validator.FieldChecks;
import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.common.domain.ApplianceAddress;
import com.ibm.dsw.quote.common.domain.ApplianceLineItem;
import com.ibm.dsw.quote.common.domain.ApplianceLineItemAddrDetail;
import com.ibm.dsw.quote.common.domain.ComparatorApplianceLineItem;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.QuoteBaseServiceHelper;
import com.ibm.dsw.quote.common.service.QuoteModifyServiceHelper;
import com.ibm.dsw.quote.customer.config.CustomerMessageKeys;
import com.ibm.dsw.quote.customer.contract.ApplianceAddressContract;
import com.ibm.dsw.quote.customer.process.CustomerProcess;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

public class SaveApplianceAddressAction extends BaseContractActionHandler {
	private static final long serialVersionUID = -164028955970813218L;
	private static final String nonApplianceQuoteLineItemSeqNum = "-1";
	private static final String separator = ",";
	
	@Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		
		ApplianceAddressContract addressContract = (ApplianceAddressContract) contract;
		String webQuoteNum = addressContract.getQuoteNum()==null?"":addressContract.getQuoteNum();
		
		String nonApplianceSecId = "".equals(addressContract.getNonApplianceSecId())?"0":addressContract.getNonApplianceSecId();
		
		List<ApplianceAddress> addressList = addressContract.getAddressList();
		
		//fix appscan
		validateParams(addressList);
		
		for(int i = 0 ;i < addressList.size(); i++){
			ApplianceAddress address = addressList.get(i);
			String lineItemStr = address.getQuoteLineItemSeqNumStr();
			if(DraftQuoteConstants.APPLIANCE_SHIPTO_ADDTYPE.equals( addressContract.getAddressType())){
				if(address.getSecId() == new Integer(nonApplianceSecId).intValue()){
					// when this address is selected as non appliance address, add '-1'
					if(StringUtils.isBlank(lineItemStr)){
						lineItemStr = nonApplianceQuoteLineItemSeqNum;	// when this address is selected as non appliance address,  but has no appliance part
					}else{
						lineItemStr = lineItemStr+ separator + nonApplianceQuoteLineItemSeqNum;	// when this address is selected as non appliance address,  and has appliance part
					}
					address.setQuoteLineItemSeqNumStr(lineItemStr);
				}
			}
		}
		
		Quote quote = this.getQuote(webQuoteNum);
		QuoteHeader qtHeader = quote.getQuoteHeader();
		boolean isModifying = false;
		if(qtHeader.isSubmittedQuote()){
			ApplianceLineItemAddrDetail applianceLineItemAddrDetail = this.getApplianceLineItemAddrDetail(webQuoteNum, quote.getCustomer(), quote.getLineItemList());
			int handleInt = DraftQuoteConstants.APPLIANCE_SHIPTO_ADDTYPE.equals( addressContract.getAddressType())?QuoteBaseServiceHelper.QUOTE_MODIFY_SHIPTO:QuoteBaseServiceHelper.QUOTE_MODIFY_INSTALLAT;
			
			isModifying = this.isModifying(addressContract.getAddressList(),applianceLineItemAddrDetail,addressContract);
			logContext.debug(this, "isModifying=" + isModifying + ":handleInt=" + handleInt);
			if(isModifying){
				QuoteModifyServiceHelper quoteModifyService = new QuoteModifyServiceHelper();
				
				ApplianceLineItemAddrDetail assembleAddrDetail = new ApplianceLineItemAddrDetail();
				Set<Integer> showLines = new HashSet<Integer>();
				assembleAddrDetail = this.assembleJSPaddrDetail(handleInt,applianceLineItemAddrDetail,assembleAddrDetail,addressList,showLines);
				
				initHiddenLineItem(handleInt,applianceLineItemAddrDetail,assembleAddrDetail, showLines);
				// reload install at address
				if(handleInt == 1 ){
					// when install-at address use ship-to address 
					if(assembleAddrDetail.getInstallAtOpt() == 0 || assembleAddrDetail.getInstallAtOpt() == 1 ){
						reloadInstallAtAddress(assembleAddrDetail);
						handleInt = 0;
					}
					// when ship-to header level address changed
					if(assembleAddrDetail.getInstallAtOpt() == 2 && isHeadLevelAddrChange(assembleAddrDetail,applianceLineItemAddrDetail)){
						assembleAddrDetail.setInstallAtAddressList(applianceLineItemAddrDetail.getInstallAtAddressList());
						handleInt = 0;
					}
					
					if ( applianceLineItemAddrDetail != null && applianceLineItemAddrDetail.getShipToAddressList() != null )
					{
						//if update ship to, add mtm address to the update list
						List<ApplianceAddress> list = applianceLineItemAddrDetail.getShipToAddressList();
						for ( ApplianceAddress addr : list )
						{
							if ( !addr.isAddrBaseMTM() )
							{
								continue;
							}
							logContext.debug(this, "begin merge mtm address");
							mergeMTMToPage(assembleAddrDetail, addr);
						}
					}
				}
				
				try {
					updateApplianceItemId(qtHeader, assembleAddrDetail, quote.getLineItemList());
					quoteModifyService.modifySQShipInstallAddress(qtHeader, handleInt,assembleAddrDetail);
				} catch (WebServiceException e) {
					logContext.error(this, e.getMessage());
		            throw new QuoteException("error executing QuoteModifyServiceHelper.modifySQShipInstallAddress()", e);
				}
				
				// update idoc number to DB when update ship-to/install-at address
				try {
					QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		            quoteProcess.updateSapIDocNum(webQuoteNum, qtHeader.getSapIntrmdiatDocNum(), addressContract.getUserId(),  SubmittedQuoteConstants.UPDATE_SHIP_INSTALL_ADDRESS);
				} catch (QuoteException e) {
					logContext.error(this, e.getMessage());
		            throw new QuoteException("error executing QuoteProcess.updateSapIDocNum() when update ship-to/install-at address", e);
				} 
			}
		}
		
		// ebiz1 save
		CustomerProcess custProcess;
		try {
			if( !qtHeader.isSubmittedQuote() || (qtHeader.isSubmittedQuote() && isModifying)){
				custProcess = CustomerProcessFactory.singleton().create();
				custProcess.updateApplianceAddress(addressContract.getUserId(),addressContract.getQuoteNum(),addressContract.getAddressType(), addressList);
			}
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
            throw new QuoteException("error executing CustomerProcess.UpdateApplianceAddress()", e);
		}
		
		//Add change log for ship to / install at address
		if(qtHeader.isSubmittedQuote() && isModifying){
			this.createShipToInstallAtAddrLog(addressContract, webQuoteNum); 
		} 
		
        handler.setState(getState(addressContract));
        handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.TRUE);
        handler.addObject(ParamKeys.PARAM_REDIRECT_URL,qtHeader.isSubmittedQuote()? HtmlUtil
                .getURLForAction(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_CUST_PARTNER_TAB):HtmlUtil
                .getURLForAction(DraftQuoteActionKeys.DISPLAY_CUST_PRTNR_TAB));       
        return handler.getResultBean();
	}
	
	protected void validateParams(List<ApplianceAddress> addressList)throws QuoteException{
		if(addressList != null){
			CharSequence c1 = "<";
			CharSequence c2 = "%";
			for(ApplianceAddress address : addressList){
				if(StringUtils.isNotBlank(address.getCntFirstName()) && (address.getCntFirstName().contains(c1) || address.getCntFirstName().contains(c2))){
					throw new QuoteException("Invalid CntFirstName value: "+address.getCntFirstName());
				}
				if(StringUtils.isNotBlank(address.getCntLastName()) && (address.getCntLastName().contains(c1) || address.getCntLastName().contains(c2))){
					throw new QuoteException("Invalid CntLastName value: "+address.getCntLastName());
				}
				if(StringUtils.isNotBlank(address.getSapIntlPhoneNumFull()) && (address.getSapIntlPhoneNumFull().contains(c1) || address.getSapIntlPhoneNumFull().contains(c2))){
					throw new QuoteException("Invalid PhoneNum value: "+address.getSapIntlPhoneNumFull());
				}
				if(StringUtils.isNotBlank(address.getCustNum())){
		    		if(!SecurityUtil.isValidInput(ParamKeys.PARAM_CUSTNUM,address.getCustNum(), "Numeric", 11, true)){
		    			throw new QuoteException("Invalid CustNum value: "+address.getCustNum());
		    		}
		    	}
				// Add the case of SapCntId -1 for new customer creation in SQO, It's a valid value (eBiz # IASR-9TT5C6)
				if(StringUtils.isNotBlank(address.getSapCntId()) && !StringUtils.equals("-1", address.getSapCntId())){
		    		if(!SecurityUtil.isValidInput(ParamKeys.PARAM_SAP_CONTACT_ID,address.getSapCntId(), "Numeric", 11, true)){
		    			throw new QuoteException("Invalid SapCntId value: "+address.getSapCntId());
		    		}
		    	}
				if(StringUtils.isNotBlank(address.getQuoteLineItemSeqNumStr()) && (address.getQuoteLineItemSeqNumStr().contains(c1) || address.getQuoteLineItemSeqNumStr().contains(c2))){
					throw new QuoteException("Invalid QuoteLineItemSeqNumStr value: "+address.getQuoteLineItemSeqNumStr());
				}
			}
		}
	}
	
	private void updateApplianceItemId(QuoteHeader qtHeader, ApplianceLineItemAddrDetail adr, List quoteLineItems)
	{
		if ( StringUtils.isBlank(qtHeader.getSapQuoteNum()) )
		{
			return;
		}
		List<ApplianceLineItem> items = new ArrayList<ApplianceLineItem>();
		List<ApplianceLineItem> list = adr.getLineItemsList();
		if ( list != null )
		{
			for ( ApplianceLineItem item : list )
			{
				if ( item != null && !items.contains(item) )
				{
					items.add(item);
				}
			}
		}
		
		List<ApplianceAddress> shipTos = adr.getShipToAddressList();
		if ( shipTos != null )
		{
			for ( ApplianceAddress shipTo : shipTos )
			{
				addLineItems(items, shipTo);
			}
		}
		List<ApplianceAddress> installAts = adr.getInstallAtAddressList();
		if ( installAts != null )
		{
			for ( ApplianceAddress installAt : installAts )
			{
				addLineItems(items, installAt);
			}
		}
		for ( ApplianceLineItem item : items )
		{
			for ( int i = 0; i < quoteLineItems.size(); i++ )
			{
				QuoteLineItem qtItem = (QuoteLineItem)quoteLineItems.get(i);
				if ( qtItem.getDestSeqNum() == item.getDestSeqNum() )
				{
					item.setDestSeqNum(qtItem.getSapLineItemSeqNum());
					break;
				}
			}
		}
	}
	
	private void addLineItems(List<ApplianceLineItem> items, ApplianceAddress addr)
	{
		if ( addr == null )
		{
			return;
		}
		List<ApplianceLineItem> lineItems = addr.getLineItemList();
		if ( lineItems == null )
		{
			return;
		}
		for ( ApplianceLineItem lineItem : lineItems )
		{
			if ( !items.contains(lineItem) )
			{
				items.add(lineItem);
			}
		}
	}
	
	private void mergeMTMToPage(ApplianceLineItemAddrDetail detail, ApplianceAddress addr)
	{
		logContext.debug(this, "mtm address: " + addr.getCustNum());
		//add mtm address to modify service call list
		List<ApplianceAddress> shipTos = detail.getShipToAddressList(); 
		if ( shipTos == null )
		{
			shipTos = new ArrayList<ApplianceAddress>();
			detail.setShipToAddressList(shipTos);
		}
		
		List<ApplianceLineItem> partsList = addr.getLineItemList();
		if ( partsList == null || partsList.size() == 0 )
		{
			return;
		}
		logContext.debug(this, "parts lise size is: " + partsList.size() + "; cust num: " + addr.getCustNum() + "; shipTos size: " + shipTos.size());
		
		mergeItem(partsList, shipTos);
		
		if ( !shipTos.contains(addr) )
		{
			shipTos.add(addr);
		}
		
		
	}
	
	private void mergeItem(List<ApplianceLineItem> partsList, List<ApplianceAddress> shipTos)
	{
		for ( ApplianceLineItem item : partsList )
		{
			for ( ApplianceAddress addr : shipTos )
			{
				if ( addr.getLineItemList() == null )
				{
					continue;
				}
				Iterator<ApplianceLineItem> iter = addr.getLineItemList().iterator();
				while ( iter.hasNext() )
				{
					logContext.debug(this, "item.getDestSeqNum()=" + item.getDestSeqNum() + ";item.getPartNum()=" + item.getPartNum());
					ApplianceLineItem item2 = iter.next();
					if ( item.getDestSeqNum() == item2.getDestSeqNum() && item.getPartNum() == item2.getPartNum() )
					{
						logContext.debug(this, "remove line item: " + item2.getDestSeqNum() + "; part num: " + item2.getPartNum());
						iter.remove();
						break;
					}
				}
			}
		}
		Iterator<ApplianceAddress> iter = shipTos.iterator();
		while ( iter.hasNext() )
		{
			ApplianceAddress addr = iter.next();
			if ( addr.getLineItemList() == null || addr.getLineItemList().size() == 0 )
			{
				logContext.debug(this, "remove address: " + addr.getCustNum());
				iter.remove();
			}
		}
	}


	private void createShipToInstallAtAddrLog(ApplianceAddressContract addressContract, String webQuoteNum)
			throws QuoteException {
		QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
		String userAction = null;
		String value = null;
		if(DraftQuoteConstants.APPLIANCE_SHIPTO_ADDTYPE.equalsIgnoreCase(addressContract.getAddressType())){
			userAction = SubmittedQuoteConstants.USER_ACTION_UPDT_SHIPTO;
			value = SubmittedQuoteConstants.USER_ACTION_UPDT_SHIPTO_VALUE;
		} else if(DraftQuoteConstants.APPLIANCE_INSTALLAT_ADDTYPE.equalsIgnoreCase(addressContract.getAddressType())){
			userAction = SubmittedQuoteConstants.USER_ACTION_UPDT_INSTALLAT;
			value = SubmittedQuoteConstants.USER_ACTION_UPDT_INSTALLAT_VALUE;
		}
		quoteProcess.addQuoteAuditHist(webQuoteNum,null, addressContract.getUserId(), userAction,value,null);
	}
	
	
	private boolean isHeadLevelAddrChange(
			ApplianceLineItemAddrDetail assembleAddrDetail,
			ApplianceLineItemAddrDetail applianceLineItemAddrDetail) {
		for(ApplianceAddress jdbcAddr : applianceLineItemAddrDetail.getShipToAddressList()){
			if(jdbcAddr.isHeadLevelAddr()){
				for(ApplianceAddress jspAddr : assembleAddrDetail.getShipToAddressList()){
					if(jspAddr.isHeadLevelAddr()){
						if(jdbcAddr.getCustNum().equals(jspAddr.getCustNum())
								&& jdbcAddr.getCntFirstName().equals(jspAddr.getCntFirstName())
								&& jdbcAddr.getCntLastName().equals(jspAddr.getCntLastName())
								&& jdbcAddr.getSapIntlPhoneNumFull().equals(jspAddr.getSapIntlPhoneNumFull())){
							return false;
						}else{
							return true;
						}
					}
					continue;
				}
			}
			continue;
		}
		
		
		return false;
	}


	private void reloadInstallAtAddress(
			ApplianceLineItemAddrDetail assembleAddrDetail) {
		List<ApplianceAddress>  shipAddrList = assembleAddrDetail.getShipToAddressList();
		List<ApplianceLineItem> allLineItemList = new ArrayList<ApplianceLineItem>();
		if(shipAddrList != null){
			for(ApplianceAddress shipAddr : shipAddrList){
				List<ApplianceLineItem> shipLineItemList = shipAddr.getLineItemList();
				for(ApplianceLineItem line : shipLineItemList){
					if(line.isAppliancePart()){
						line.setInstallAtAddr(line.getShipToAddr());
						line.setShipToAddr(shipAddr);
						allLineItemList.add(line);
					}
				}
			}
			assembleAddrDetail.setLineItemsList(allLineItemList);
			
			List<ApplianceAddress> installAtAddressList = assembleAddrDetail.getShipToAddressList();
			if(installAtAddressList != null){
				List<ApplianceAddress> installReturnList = new ArrayList<ApplianceAddress>();
				for(ApplianceAddress insAddr : installAtAddressList){
					if(insAddr.getLineItemList().size()>0){
						installReturnList.add(insAddr);
					}
				}
				assembleAddrDetail.setInstallAtAddressList(installReturnList);
			}
		}
	}


	private ApplianceLineItemAddrDetail assembleJSPaddrDetail(
			int handleInt, ApplianceLineItemAddrDetail applianceLineItemAddrDetail,
			ApplianceLineItemAddrDetail assembleAddrDetail, List<ApplianceAddress> addressList,Set<Integer> showLines) {
		
		List<ApplianceLineItem>  lineItemList = applianceLineItemAddrDetail.getLineItemsList();
		if(handleInt == QuoteBaseServiceHelper.QUOTE_MODIFY_INSTALLAT){
			assembleAddrDetail.setInstallAtOpt(2);
			
			List<ApplianceAddress> installAtAddressList = new ArrayList<ApplianceAddress>();
			for(ApplianceAddress addr : addressList){
				addr.setQuoteLineItemSeqNum(Integer.MAX_VALUE);
				String[] lineItemArray = StringUtils.isNotBlank(addr.getQuoteLineItemSeqNumStr())? addr.getQuoteLineItemSeqNumStr().split(separator):new String[0];
				List<ApplianceLineItem> lineItemListTemp = new ArrayList<ApplianceLineItem>();
				for(String lineNum : lineItemArray){
					if(StringUtils.isBlank(lineNum)){continue;}
					if(!StringUtils.equals(lineNum, nonApplianceQuoteLineItemSeqNum))
					{
						ApplianceLineItem item = new ApplianceLineItem();
						item.setAppliancePart(true);
						for(ApplianceLineItem lineTemp : lineItemList){
							if(lineTemp.getQuoteLineItemSeqNum() == Integer.valueOf(lineNum)){
								item.copy(lineTemp);
								item.setConfigrtnId(lineTemp.getConfigrtnId());
								item.setDestSeqNum(lineTemp.getDestSeqNum());
								lineItemListTemp.add(item);
								showLines.add(item.getQuoteLineItemSeqNum());
								break;
							}
						}
					}
				}
				addr.setLineItemList(lineItemListTemp);
				if(lineItemListTemp.size()>0)
					installAtAddressList.add(addr);
			}
			assembleAddrDetail.setInstallAtAddressList(installAtAddressList);
			
			if(applianceLineItemAddrDetail != null && applianceLineItemAddrDetail.getShipToAddressList() != null){
				List<ApplianceAddress> shipToAddressList = new ArrayList<ApplianceAddress>();
				for(ApplianceAddress addr : applianceLineItemAddrDetail.getShipToAddressList()){
					if(addr.isHeadLevelAddr()){
						shipToAddressList.add(addr);
						assembleAddrDetail.setShipToAddressList(shipToAddressList);
						break;
					}
				}
			}
			
		}else{
			assembleAddrDetail.setInstallAtOpt(applianceLineItemAddrDetail.getInstallAtOpt());
			
			List<ApplianceAddress> shipToAddressList = new ArrayList<ApplianceAddress>();
			for(ApplianceAddress addr : addressList){
				addr.setQuoteLineItemSeqNum(Integer.MAX_VALUE);
				String[] lineItemArray = StringUtils.isNotBlank(addr.getQuoteLineItemSeqNumStr())?addr.getQuoteLineItemSeqNumStr().split(separator):new String[0];
				
				List<ApplianceLineItem> lineItemListTemp = new ArrayList<ApplianceLineItem>();
				for(String lineNum : lineItemArray){
					if(StringUtils.isBlank(lineNum)){continue;}
					if(!StringUtils.equals(lineNum, nonApplianceQuoteLineItemSeqNum))
					{
						ApplianceLineItem item = new ApplianceLineItem();
						item.setAppliancePart(true);
						for(ApplianceLineItem lineTemp : lineItemList){
							if(lineTemp.getQuoteLineItemSeqNum() == Integer.valueOf(lineNum)){
								item.copy(lineTemp);
								item.setConfigrtnId(lineTemp.getConfigrtnId());
								item.setDestSeqNum(lineTemp.getDestSeqNum());
								lineItemListTemp.add(item);
								showLines.add(item.getQuoteLineItemSeqNum());
								break;
							}
						}
					}else{
						addr.setHeadLevelAddr(true);
					}
				}
				addr.setLineItemList(lineItemListTemp);
				
				if(addr.isHeadLevelAddr() || addr.getLineItemList().size()>0){
					shipToAddressList.add(addr);
				}
			}
			assembleAddrDetail.setShipToAddressList(shipToAddressList);
		}
		
		return this.filterNoModifyCustNumList(handleInt, applianceLineItemAddrDetail,assembleAddrDetail,true);
		
	}


	private ApplianceLineItemAddrDetail filterNoModifyCustNumList(int handleInt,
			ApplianceLineItemAddrDetail applianceLineItemAddrDetail,
			ApplianceLineItemAddrDetail assembleAddrDetail,boolean sendAll) {
		
		if(sendAll)
			return assembleAddrDetail;
		
		Set<String> noModifyCust = new HashSet<String>();
		
		List<ApplianceAddress>  jdbcAppAddrList= null;
		List<ApplianceAddress>  jspAppAddrList= null;
		if(handleInt == QuoteBaseServiceHelper.QUOTE_MODIFY_SHIPTO){
			jspAppAddrList = assembleAddrDetail.getShipToAddressList();
			jdbcAppAddrList = applianceLineItemAddrDetail.getShipToAddressList();
		}else if(handleInt == QuoteBaseServiceHelper.QUOTE_MODIFY_INSTALLAT){
			jspAppAddrList = assembleAddrDetail.getInstallAtAddressList();
			jdbcAppAddrList = applianceLineItemAddrDetail.getInstallAtAddressList();
		}
		
		if(jspAppAddrList != null){
			
			for(int j=0; j< jspAppAddrList.size(); j++){
				ApplianceAddress jspAddr = jspAppAddrList.get(j);
				for(ApplianceAddress jdbcAddr : jdbcAppAddrList){
					
					if( StringUtils.equals(jspAddr.getCustNum(), jdbcAddr.getCustNum())
							&& StringUtils.equals(jspAddr.getCntFirstName(), jdbcAddr.getCntFirstName())
							&& StringUtils.equals(jspAddr.getCntLastName(), jdbcAddr.getCntLastName())
							&& StringUtils.equals(jspAddr.getSapIntlPhoneNumFull(), jdbcAddr.getSapIntlPhoneNumFull())
							&&(  handleInt == QuoteBaseServiceHelper.QUOTE_MODIFY_INSTALLAT ||
									(handleInt == QuoteBaseServiceHelper.QUOTE_MODIFY_SHIPTO && jspAddr.isHeadLevelAddr() == jdbcAddr.isHeadLevelAddr() )
									)
							){
						
						if(jspAddr.isHeadLevelAddr() != jdbcAddr.isHeadLevelAddr()){
							continue;
						}
						
						List<ApplianceLineItem> jspLineItemList = jspAddr.getLineItemList();
						
						List<ApplianceLineItem> jdbcLineItemListTemp = jdbcAddr.getLineItemList();
						
						List<ApplianceLineItem> jdbcLineItemList = new ArrayList<ApplianceLineItem>();
						for(ApplianceLineItem jdbcLine : jdbcLineItemListTemp){
							
							if(jdbcLine.isAppliancePart()
									&& (
											StringUtils.isBlank(jdbcLine.getConfigrtnId()) || 
									    StringUtils.equals(jdbcLine.getAppliance(), "1")
									    || (StringUtils.equals(jdbcLine.getAppliance(), "0") && StringUtils.equals(jdbcLine.getTransceiver(), "1"))
									)
									){
								jdbcLineItemList.add(jdbcLine);
							}
						}
						
						if(jspLineItemList.size() == jdbcLineItemList.size()){
							Collections.sort(jspLineItemList, new ComparatorApplianceLineItem());
							Collections.sort(jdbcLineItemList, new ComparatorApplianceLineItem());
							boolean ooo = true;
							for(int i=0;i<jspLineItemList.size();i++)
							{
								if(jspLineItemList.get(i).getQuoteLineItemSeqNum() != jdbcLineItemList.get(i).getQuoteLineItemSeqNum()){
									ooo = false;
								}else{
									continue;
								}
							}
							if(ooo){
								noModifyCust.add(jspAddr.getCustNum());
								break;
							}
						}
					}
				}
			}
		}
		
		if(!noModifyCust.isEmpty())
		{
			ApplianceLineItemAddrDetail returnDetail = new ApplianceLineItemAddrDetail();
			returnDetail.setInstallAtOpt(assembleAddrDetail.getInstallAtOpt());
			
			List<ApplianceAddress> mmAddressList = new ArrayList<ApplianceAddress>();
			
			if(handleInt == QuoteBaseServiceHelper.QUOTE_MODIFY_SHIPTO){
				// ship-to
				for(ApplianceAddress addr : assembleAddrDetail.getShipToAddressList()){
					if(!noModifyCust.contains(addr.getCustNum()) )
						mmAddressList.add(addr);
				}
				returnDetail.setShipToAddressList(mmAddressList);
			}else if(handleInt == QuoteBaseServiceHelper.QUOTE_MODIFY_INSTALLAT){
				// install-at
				returnDetail.setShipToAddressList(assembleAddrDetail.getShipToAddressList());
				for(ApplianceAddress addr : assembleAddrDetail.getInstallAtAddressList()){
					if(!noModifyCust.contains(addr.getCustNum()))
						mmAddressList.add(addr);
				}
				returnDetail.setInstallAtAddressList(mmAddressList);
			}
			
			return returnDetail;
		}else{
			
			return assembleAddrDetail;
		}
	}

	// re-init ralated non-appliance parts && appliance parts
	private void initHiddenLineItem(int handleInt,
			ApplianceLineItemAddrDetail applianceLineItemAddrDetail,
			ApplianceLineItemAddrDetail returnDetail,Set<Integer> showLines
			) {
		Set<ApplianceLineItem> lineNumSet = new HashSet<ApplianceLineItem>();
		List<ApplianceLineItem> jdbcLineItemList = applianceLineItemAddrDetail.getLineItemsList();
		for(ApplianceLineItem jdbcLine : jdbcLineItemList){
			if(!showLines.contains(jdbcLine.getQuoteLineItemSeqNum()) && jdbcLine.isAppliancePart()){
				lineNumSet.add(jdbcLine);
			}
		}
		
		if(!lineNumSet.isEmpty()){
			List<ApplianceAddress> mmAddressList = null;
			ApplianceAddress headlevelAddr = null;
			if(handleInt == QuoteBaseServiceHelper.QUOTE_MODIFY_INSTALLAT){
				mmAddressList = returnDetail.getInstallAtAddressList();
			}else{
				mmAddressList = returnDetail.getShipToAddressList();
				for(ApplianceAddress addr : mmAddressList){
					if(addr.isHeadLevelAddr()){
						headlevelAddr = addr;
					}
				}
			}
			Iterator<ApplianceLineItem> setIt = lineNumSet.iterator();
			while(setIt.hasNext()){
				ApplianceLineItem lineitJdbc = setIt.next();
				if(lineitJdbc != null){
					ApplianceAddress raddrJdbc = null;
					for(ApplianceAddress  raddr : mmAddressList){
						List<ApplianceLineItem> rlinelist = raddr.getLineItemList();
						for(ApplianceLineItem it : rlinelist){
							if(
									lineitJdbc.isAppliancePart() && StringUtils.isNotBlank(it.getConfigrtnId()) &&	StringUtils.equals(it.getConfigrtnId(),lineitJdbc.getConfigrtnId())
							){
								raddrJdbc = raddr;
							}
							
						}
					}
					if(raddrJdbc != null){
						raddrJdbc.getLineItemList().add(lineitJdbc);
					}else if(headlevelAddr != null){
						headlevelAddr.getLineItemList().add(lineitJdbc);
					}
				}
			}
			
		}
	}

	private boolean isModifying(List<ApplianceAddress> addressList,
			ApplianceLineItemAddrDetail applianceLineItemAddrDetail,
			ApplianceAddressContract addressContract) {
		
		if(addressList!=null && applianceLineItemAddrDetail!=null){
			 List<ApplianceAddress>  jdbcAddressList = null;
			 boolean shipToModify = false;
			 int mtmAddrNums = 0;
			if(DraftQuoteConstants.APPLIANCE_SHIPTO_ADDTYPE.equals( addressContract.getAddressType()))
			{
				jdbcAddressList = applianceLineItemAddrDetail.getShipToAddressList();
				//here add address base mtm to draft list
				mtmAddrNums = getMTMAddrNum(jdbcAddressList);
				shipToModify = true;
			}else{
				jdbcAddressList = applianceLineItemAddrDetail.getInstallAtAddressList();
			}
			logContext.debug(this, "addressList.size()=" + addressList.size() + ";mtmAddrNums=" + mtmAddrNums + ";jdbcAddressList.size()=" + jdbcAddressList.size());
			if((addressList.size() + mtmAddrNums) != jdbcAddressList.size()){
				return true;
			}
			
			for(ApplianceAddress jspAddr : addressList){
				ApplianceAddress jdbcAddr = this.isExistInJdbcAddr(jspAddr,jdbcAddressList);
				if(jdbcAddr == null){
					return true; 
				}else if( !StringUtils.equals(jspAddr.getCntFirstName(), jdbcAddr.getCntFirstName())
						|| !StringUtils.equals(jspAddr.getCntLastName(), jdbcAddr.getCntLastName())
						|| !StringUtils.equals(jspAddr.getSapIntlPhoneNumFull(), jdbcAddr.getSapIntlPhoneNumFull())
					){
					return true;
				}else{
					String[] lineItemArray =  StringUtils.isNotBlank(jspAddr.getQuoteLineItemSeqNumStr())?jspAddr.getQuoteLineItemSeqNumStr().split(separator):new String[0];
					if( !this.isEqualsLineItems(lineItemArray,jdbcAddr,shipToModify)){
						return true; 
					}
				}
			}
		}
		
		return false;
	}
	
	private int getMTMAddrNum(List<ApplianceAddress> addressList)
	{
		if ( addressList == null ) return 0;
		int num = 0;
		for ( ApplianceAddress addr : addressList )
		{
			if ( addr.isAddrBaseMTM() )
			{
				num++;
			}
		}
		return num;
	}
	
	
	
	private boolean isEqualsLineItems(String[] lineItemArray,
			ApplianceAddress jdbcAddr,boolean shipToModify) {
		
		List<ApplianceLineItem> jdbcLineItemList = jdbcAddr.getLineItemList();
		
		
		List<Integer> jdbcLineItemNumList = new ArrayList<Integer>();
		List<Integer> lineItemNumList = new ArrayList<Integer>();
		
		if(jdbcLineItemList!=null){
			for(ApplianceLineItem it : jdbcLineItemList){
				if(it.isAppliancePart()
						&& (
								StringUtils.isBlank(it.getConfigrtnId()) || 
						(  StringUtils.equals(it.getAppliance(), "1") || (StringUtils.equals(it.getAppliance(), "0") && StringUtils.equals(it.getTransceiver(), "1")))
						)
						){
					jdbcLineItemNumList.add(it.getQuoteLineItemSeqNum());
				}
			}
		}
		
		if(shipToModify){
			if(jdbcAddr.isHeadLevelAddr()){
				boolean isHeadLevelAddrJsp = false;
				for(String lineItemNum : lineItemArray){
					if(StringUtils.equals(lineItemNum, String.valueOf(DraftQuoteConstants.DEFAULT_APPLIANCE_ADDRESS))){
						isHeadLevelAddrJsp = true;
					}
				}
				if(!isHeadLevelAddrJsp) return false;
			}
		}
		
		for(String lineItemNum : lineItemArray){
			if(StringUtils.isBlank(lineItemNum)){continue;}
			if(!StringUtils.equals(lineItemNum, String.valueOf(DraftQuoteConstants.DEFAULT_APPLIANCE_ADDRESS))){
				lineItemNumList.add(Integer.valueOf(lineItemNum));
			}
		}
		
		if(jdbcLineItemNumList.size() != lineItemNumList.size()){
			return false;
		}else{
			
			int[] jdbcObjIntArray = new int[jdbcLineItemNumList.size()];
			int[] jspObjIntArray = new int[lineItemNumList.size()];
			
			for(int i=0;i<jdbcObjIntArray.length;i++){
				jdbcObjIntArray[i] = jdbcLineItemNumList.get(i);
			}
			Arrays.sort(jdbcObjIntArray);
			
			for(int i=0;i<jspObjIntArray.length;i++){
				jspObjIntArray[i] = lineItemNumList.get(i);
			}
			Arrays.sort(jspObjIntArray);
			
			for(int i=0;i<jdbcObjIntArray.length;i++){
				if(jdbcObjIntArray[i] != jspObjIntArray[i])
					return false;
			}
		}
		
		return true;
	}

	private ApplianceAddress isExistInJdbcAddr(ApplianceAddress jspAddr,
			List<ApplianceAddress> jdbcAddressList) {
		for(ApplianceAddress jdbcAddr : jdbcAddressList){
			if(StringUtils.equals(jspAddr.getCustNum(), jdbcAddr.getCustNum()) ){
				return jdbcAddr;
			}
		}
		return null;
	}

	private ApplianceLineItemAddrDetail getApplianceLineItemAddrDetail(String webQuoteNum, Customer soldToCustomer, List quoteLineItems) throws QuoteException{
		CustomerProcess custProcess;
    	ApplianceLineItemAddrDetail applianceLineItemAddrDetail =null;
    	
    	// get address details
		try {
			custProcess = CustomerProcessFactory.singleton().create();
			try {
				applianceLineItemAddrDetail = custProcess.findAddrLineItem(webQuoteNum,"", quoteLineItems, true, soldToCustomer);
			} catch (QuoteException e) {
				logContext.error(this, e.getMessage());
	            throw new QuoteException("error executing CustomerProcessFactory.findAddrLineItem()", e);
			} 
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
            throw new QuoteException("error executing CustomerProcessFactory.singleton().create()", e);
		}
		return applianceLineItemAddrDetail;
	}
	
	private Quote getQuote(String webQuoteNum) throws QuoteException{
		Quote quote = null;
		try {
			QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
			quote = quoteProcess.getQuoteForApplianceAddress(webQuoteNum);
			List quoteLineItems = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(webQuoteNum);
			quote.setLineItemList(quoteLineItems);
			
		} catch (TopazException e) {
            logContext.error(this, e.getMessage());
            throw new QuoteException("error executing QuoteProcess.getQuoteHdrInfoByWebQuoteNum()", e);
		}
		return quote;
	}
	
	protected String getState(ProcessContract contract) {
        return StateKeys.STATE_REDIRECT_ACTION;
    }
	
	protected boolean validate(ProcessContract contract) {
		super.validate(contract);
		HashMap vMap =  new HashMap();

		ApplianceAddressContract addressContract = (ApplianceAddressContract) contract;
		if (addressContract == null)
			return false;

		boolean isValid = true;
		List<ApplianceAddress> addressList = addressContract.getAddressList();
		
		String firstName = "";
		String lastName = "";
		String phone = "";
		
		for(int i = 0 ;i < addressList.size(); i++){
			ApplianceAddress address = addressList.get(i);
			firstName = address.getCntFirstName();
			lastName = address.getCntLastName();
			phone = address.getSapIntlPhoneNumFull();
			
			if (StringUtils.isBlank(firstName)) {
				FieldResult field = new FieldResult();
				field.setMsg(MessageKeys.MSG_REQUIRED);
				field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
						CustomerMessageKeys.FIRST_NAME
						);
				vMap.put(ParamKeys.PARAM_CNT_FNAME+"_"+i, field);
				isValid = false;
			}else{
				if(firstName.contains("_") || firstName.contains("!")){
					FieldResult field = new FieldResult();
					field.setMsg(MessageKeys.MSG_NAME);
					field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
							CustomerMessageKeys.FIRST_NAME
							);
					vMap.put(ParamKeys.PARAM_CNT_FNAME+"_"+i, field);
					isValid = false;
				}
			}
			
			if (StringUtils.isBlank(lastName)) {
				FieldResult field = new FieldResult();
				field.setMsg(MessageKeys.MSG_REQUIRED);
				field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
						CustomerMessageKeys.LAST_NAME);
				vMap.put(ParamKeys.PARAM_CNT_LNAME+"_"+i, field);
				isValid = false;
			}

			if (StringUtils.isBlank(phone)) {
				FieldResult field = new FieldResult();
				field.setMsg(MessageKeys.MSG_REQUIRED);
				field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
						CustomerMessageKeys.PHONE_NUM);
				vMap.put(ParamKeys.PARAM_CNT_PHONE+"_"+i, field);
				isValid = false;
			} else if (!FieldChecks.validateISOLatin1(phone)) {
				FieldResult field = new FieldResult();
				field.setMsg(MessageKeys.MSG_ISOLATIN1_ONLY);
				field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE,
						CustomerMessageKeys.PHONE_NUM);
				vMap.put(ParamKeys.PARAM_CNT_PHONE+"_"+i, field);
				isValid = false;
			}
			
			
		}

		addToValidationDataMap(addressContract, vMap);
		return isValid;
	}
}
