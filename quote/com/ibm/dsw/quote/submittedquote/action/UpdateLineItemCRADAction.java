/**
 * 
 */
package com.ibm.dsw.quote.submittedquote.action;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.quote.base.action.BaseContractActionHandler;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.StateKeys;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.util.LogThrowableUtil;
import com.ibm.dsw.quote.base.util.StringHelper;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.ApplianceAddress;
import com.ibm.dsw.quote.common.domain.ApplianceLineItem;
import com.ibm.dsw.quote.common.domain.ApplianceLineItemAddrDetail;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.QuoteLineItem_Impl;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.dsw.quote.common.process.QuoteCapabilityProcess;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.QuoteModifyServiceHelper;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.customer.process.CustomerProcessFactory;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteMessageKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteActionKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.contract.UpdateLineItemCRADContract;
import com.ibm.dsw.quote.submittedquote.contract.UpdateLineItemCRADContract.LineItemParameter;
import com.ibm.ead4j.jade.bean.MessageBeanKeys;
import com.ibm.ead4j.jade.bean.ResultBean;
import com.ibm.ead4j.jade.bean.ResultBeanException;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>UpdateQuoteExpirationDateAction<code> class.
 *    
 * @author: chlincd@cn.ibm.com
 * 
 * Creation date: Jul 02, 2013
 * 
 * Modification date:July 18,2013
 * @author: jiewbj@cn.ibm.com 
 * Description: handler add ParamKeys.PARAM_REDIRECT_MSG,ParamKeys.PARAM_REDIRECT_URL.
 * 
 */
@SuppressWarnings("rawtypes")
public class UpdateLineItemCRADAction extends BaseContractActionHandler {

    /**
     * 
     */
    private static final long serialVersionUID = -7576504846571380516L;

    @Override
	public ResultBean executeBiz(ProcessContract contract, ResultHandler handler)
			throws QuoteException, ResultBeanException {
		UpdateLineItemCRADContract ct = (UpdateLineItemCRADContract) contract;
		
        String result = updateLineItemChange(contract, handler);
        //Click order button when on PP tab 
        if(StringUtils.isNotBlank(ct.getRedirectURL())){
        	handler.addObject(ParamKeys.PARAM_REDIRECT_URL, ct.getRedirectURL());
        	handler.setState(StateKeys.STATE_REDIRECT_ORDER_ACTION);
        }else{
        	handler.addObject(ParamKeys.PARAM_REDIRECT_URL, getRedirectURL(SubmittedQuoteActionKeys.DISPLAY_SUBMITTEDQT_PART_PRICE_TAB,ct.getQuoteNum()));
        	handler.addObject(ParamKeys.PARAM_FORWARD_FLAG, Boolean.FALSE); 
        	handler.addObject(ParamKeys.PARAM_REDIRECT_MSG, getRedirectMsgList(result));
        	handler.setState(StateKeys.STATE_REDIRECT_ACTION);
        }
		return handler.getResultBean();
	}
	
    protected String updateLineItemChange(ProcessContract contract, ResultHandler handler)
		throws QuoteException, ResultBeanException {
		UpdateLineItemCRADContract ct = (UpdateLineItemCRADContract) contract;
		String webQuoteNum = ct.getQuoteNum();
		String userId = ct.getUserId();
        Quote quote = null;
        
        try {
            QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
            quote=quoteProcess.getQuoteForSubmittedCRAD(webQuoteNum);
            List modifiedQli = getModifiedLineItemList(quote, ct,quoteProcess);
            if(!QuoteCommonUtil.validateOwnershipTransferPartsSerialNumber(quote)){
                logContext.debug(this, "The same serial number of the ownership transfer part is exists.");
                return QuoteCapabilityProcess.OWNERSHIP_PARTS_SERIAL_NUMBER_NOT_SAME;
            }
            if(!QuoteCommonUtil.validateUpgradeApplianceSerialNumber(quote)){
                logContext.debug(this, "The same serial number of the  upgrade part is exists.");
                return QuoteCapabilityProcess.APPLIANCE_SERIAL_NUMBER_NOT_SAME;
            }
            if ( modifiedQli.size() == 0 )
            {
            	logContext.debug(this, "No crad, mtm, deploy id changes, no need do any update");
            	return SubmittedQuoteMessageKeys.QT_UP_LINE_ITEM_CHG_SUCCESS_MSG;
            }
            updateLineItemSerialNumWarningFlag( modifiedQli);
            //please be sure the mtm warning flag is updated by the new mtm value before call update ship to
            updateShipTo(quote, quote.getLineItemList(), modifiedQli);
            quote.setLineItemList(modifiedQli);
            QuoteModifyServiceHelper quoteModifyService = new QuoteModifyServiceHelper();

            try {
    	    	quoteModifyService.modifySubmittedQuoteLineItemChange(quote);
    	    	updateMtmAferSumbmittedToSap(ct,quoteProcess,modifiedQli);
    	  	} catch (Exception e) {
    	          logContext.error(this, "webServicExceptoin accor when executing quote modify RFC to modify line item CRAD.");
    	          throw new QuoteException(e);  
    	  	}
    	  	

            //update idoc, update line item by persister
            quoteProcess.updateSapIDocNum(webQuoteNum, quote.getQuoteHeader().getSapIntrmdiatDocNum(), userId,  SubmittedQuoteConstants.USER_ACTION_MODIFY_CRAD_MTM_DEPL);
        	} catch (TopazException e) {
        		logContext.error(this, LogThrowableUtil.getStackTraceContent(e));
        		throw new QuoteException(e);
			}

        	return SubmittedQuoteMessageKeys.QT_UP_LINE_ITEM_CHG_SUCCESS_MSG;
	}
	
	
    /**
     * DOC Comment method "updateLineItemSerialNumWarningFlag".
     * 
     * @param lineItemList
     * @param modifiedQli
     * @throws QuoteException 
     * @throws TopazException 
     */
    private void updateLineItemSerialNumWarningFlag(List modifiedQli) throws QuoteException, TopazException {
   	 	if (null==modifiedQli){return;}
        for (Iterator iterator = modifiedQli.iterator(); iterator.hasNext();) {
            QuoteLineItem qli = (QuoteLineItem) iterator.next();
            if (qli != null) {
                if(qli.getDeployModel()!=null &&qli.getDeployModel().getSerialNumWarningFlag()!=null){
                	qli.getDeployModel().setSerialNumWarningFlag(QuoteLineItemFactory.singleton().getMTMWarningFlg(qli.getQuoteNum(),qli.getPartNum(),qli.getMachineType(),qli.getModel(),qli.getSerialNumber()));
                }
            }
        }

    }

    /**
     * DOC Comment method "getReloadLineItem".
     * 
     * @param lineItemList
     * @param partNum
     * @param seqNum
     * @return
     */
    private QuoteLineItem getReloadLineItem(List lineItemList, String partNum, int seqNum) {
        for (Iterator iterator = lineItemList.iterator(); iterator.hasNext();) {
            QuoteLineItem quoteLineItem = (QuoteLineItem) iterator.next();
            if (quoteLineItem.getPartNum().equals(partNum) && (quoteLineItem.getSeqNum() == seqNum)) {
                return quoteLineItem;
            }
        }
        return null;
    }

    protected List getModifiedLineItemList(Quote quote, ProcessContract contract, QuoteProcess quoteProcess)
            throws QuoteException, TopazException {
		UpdateLineItemCRADContract ct = (UpdateLineItemCRADContract) contract;
		List lineItemList = quote.getLineItemList();
		Iterator iter = lineItemList.iterator();
		List changedLineItemList = new ArrayList();
	
        //  ownership transfer appliance type parts
        List applncOwnerships = new ArrayList();
        List applncUpgradePartsList = new ArrayList();
		while (iter.hasNext()){
			QuoteLineItem_Impl qli = (QuoteLineItem_Impl) iter.next();
			if (qli!=null){
				String key = qli.getPartNum().trim()+"_"+qli.getSeqNum();
				//the read-only line item
				if (!ct.isExchangeLineItem(key)){
					continue;
				}
				Date pageLineItemCRAD = ct.getLineItemCRAD(key);
				Date dbLineItemCRAD = qli.getLineItemCRAD();
				// if lineItem CRAD field has been changed
				if ((pageLineItemCRAD!=null&&!pageLineItemCRAD.equals(dbLineItemCRAD)) ||
						(pageLineItemCRAD==null && dbLineItemCRAD!=null)){
					// if main appliance part or upgrade appliance part
					// then update line item crad with page input crad value
					qli.getModifiedProperty().setOldCrad(dbLineItemCRAD);
		            if (QuoteCommonUtil.isMainApplncOrUpgradeApplnc(qli)){
		            	
							qli.setLineItemCRAD(pageLineItemCRAD);
//							qli.custReqArrvDate=pageLineItemCRAD;				
							changedLineItemList.add(qli);
							qli.getModifiedProperty().setCrad(true);
							logContext.debug(this, "add a line item by crad: " + qli.getDestSeqNum());
							
							List qliList = quote.getLineItemList();
							Iterator iterator = qliList.iterator();
							while (iterator.hasNext()){
								//if main part crad has changed, then all referred part should be also changed
								QuoteLineItem_Impl lineItem = (QuoteLineItem_Impl) iterator.next();
								if (lineItem!=null && QuoteCommonUtil.isAssociated(lineItem) &&
										lineItem.getConfigrtnId().equals(qli.getConfigrtnId()) ){
//									lineItem.custReqArrvDate=qli.custReqArrvDate;
									lineItem.setLineItemCRAD(qli.getLineItemCRAD());
									changedLineItemList.add(lineItem);
									lineItem.getModifiedProperty().setCrad(true);
									lineItem.getModifiedProperty().setOldCrad(dbLineItemCRAD);
                                logContext.debug(this, "add a line item by crad: " + lineItem.getDestSeqNum());
								}
							}
		    		}
		            
				} 
//				try{
				if(qli.isApplncPart()){
					boolean flag=false;
					if (!validateEqual(qli.getMachineType(),ct.getMachineType(key))){
						qli.getModifiedProperty().setOldMachTypeValue(qli.getMachineType());
						qli.setMachineType(ct.getMachineType(key));
						flag=true;
					}
					if(!validateEqual(qli.getModel(),ct.getMachineModel(key))){
						qli.getModifiedProperty().setOldMachModelValue(qli.getModel());
						qli.setModel(ct.getMachineModel(key));
						flag=true;
					}
					if(!validateEqual(qli.getSerialNumber(),ct.getMachineSerialNumber(key))){
						qli.getModifiedProperty().setOldMachSerialNumberValue(qli.getSerialNumber());
						qli.setSerialNumber(ct.getMachineSerialNumber(key));
						flag=true;
					}
				
					if ( flag )
					{
						qli.getModifiedProperty().setMtm(flag);
	                    logContext.debug(this, "add a line item by mtm: " + qli.getDestSeqNum());
						if (!changedLineItemList.contains(qli)){
	                        changedLineItemList.add(qli);
					    }	
					}
				}
			    // deployment association
				if ( qli.isApplncPart() && qli.isDeploymentAssoaciatePart() )
				{
                	if (!validateEqual(qli.getDeployModel().getDeployModelOption(),ct.getDeployModelOption(key))
                                     || !validateEqual(qli.getDeployModel().getDeployModelId(),ct.getDeployModelId(key))){
                				 String oldDeplyId = qli.getDeployModel() == null ? "" : qli.getDeployModel().getDeployModelId();
                                 qli.setDeployModelOption(ct.getDeployModelOption(key));
                                 qli.setDeployModelId(ct.getDeployModelId(key));
                                 qli.setDeployModelInvalid(ct.isDeployModelValid(key));
                                 qli.getModifiedProperty().setDeployId(true);
                                 qli.getModifiedProperty().setOldDeployId(oldDeplyId);
                        logContext.debug(this, "add a line item by deploy: " + qli.getDestSeqNum());
                                 if (!changedLineItemList.contains(qli)){                          
                                     changedLineItemList.add(qli);                                 
                                 }
                                 logContext.debug(this, "deploy option:" + qli.getDeployModel().getDeployModelOption() + ":" + ct.getDeployModelOption(key));
                                 logContext.debug(this, "deploy option:" + qli.getDeployModel().getDeployModelId() + ":" + ct.getDeployModelId(key));
                 				
                             }
				}	
//				}
//				catch(Exception e){
//					logContext.error(this, "updateMTMSerial accor when executing quote modify MTMSerial to modify line item MTMSerial.");
//				}
                //add ownership transfer appliance part
                if (qli.isApplncPart() && qli.isOwerTransferPart()){
                    applncOwnerships.add(qli);
                }
                //add appliance upgrade part
                if (qli.isApplncPart() && qli.isApplncUpgrade()){
                	applncUpgradePartsList.add(qli);
                }    
			}
		}
        quote.setApplncOwnerShipParts(applncOwnerships);
        quote.setApplncUpgradeParts(applncUpgradePartsList);

		return changedLineItemList;
	}
    
 protected void updateMtmAferSumbmittedToSap(UpdateLineItemCRADContract ct, QuoteProcess quoteProcess,List changedLineItemList ) throws QuoteException{
	 if (null==changedLineItemList){return;}
     Map<String,Object> map=new HashMap<String,Object>();
	 for (Iterator iterator = changedLineItemList.iterator(); iterator.hasNext();) {
         QuoteLineItem qli = (QuoteLineItem) iterator.next();
    	 if (qli!=null){
    		String key = qli.getPartNum().trim()+"_"+qli.getSeqNum();
			boolean flag=false;
			if (qli.getModifiedProperty().isChangedMachTypeValue()){
				String [] machTypeValue={valueOfStr(qli.getModifiedProperty().getOldMachTypeValue()),ct.getMachineType(key)};
				map.put(SubmittedQuoteConstants.USER_ACTION_UPDT_MTM_MACH_TYPE, machTypeValue);
				flag=true;
			}
			if(qli.getModifiedProperty().isChangedMachModelValue()){
				String [] machModelValue={valueOfStr(qli.getModifiedProperty().getOldMachModelValue()),ct.getMachineModel(key)};
				map.put(SubmittedQuoteConstants.USER_ACTION_UPDT_MTM_MACH_MODEL, machModelValue);
				flag=true;
			}
			if(qli.getModifiedProperty().isChangedSerialNumberValue()){
				String [] machSerialNumberValue={valueOfStr(qli.getModifiedProperty().getOldMachSerialNumberValue()),ct.getMachineSerialNumber(key)};
				map.put(SubmittedQuoteConstants.USER_ACTION_UPDT_MTM_MACH_SERIAL_NUM, machSerialNumberValue);
				flag=true;
			}
			
			// fixed PL#JKEY-9F6RWD missed deploymtID related changed items when review audit history, 
			// RTC task: https://igartc03.swg.usma.ibm.com/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/643978
			if(qli.getModifiedProperty().isChangedLineitemCrad()){
				String [] lineItemCRAD ={DateUtil.formatDate(qli.getModifiedProperty().getOldCrad()),DateUtil.formatDate(ct.getLineItemCRAD(key))};
				map.put(SubmittedQuoteConstants.USER_ACTION_UPDT_CRAD, lineItemCRAD);
				flag=true;
			}
			
			if(qli.getModifiedProperty().isChangedDeployId()){
				String [] deploymtIdValue ={valueOfStr(qli.getModifiedProperty().getOldDeployId()),ct.getDeployModelId(key)};
				map.put(SubmittedQuoteConstants.USER_ACTION_UPDATE_DEPLOYMTID, deploymtIdValue);
				flag=true;
			}
			
			
			if(flag){
				 Set<Map.Entry<String, Object>> set = map.entrySet();
				 for (Iterator<Map.Entry<String, Object>> it = set.iterator(); it.hasNext();) {
			            Map.Entry<String, Object> entry = it.next();
			            String[] str=(String[]) entry.getValue();
			            quoteProcess.addQuoteAuditHist(qli.getQuoteNum(),
								qli.getSeqNum(), ct.getUserId(), entry.getKey(),
								str[0],str[1]);  
			     }
				 map.clear();
			
			    }	
				
			}
	 		}
		}
	
	protected boolean validate(ProcessContract contract){
		if (!super.validate(contract)) {
            return false;
        }
		logContext.debug(this, "validate updatelineItemCRADContract:");
		HashMap map = new HashMap();
		UpdateLineItemCRADContract ct = (UpdateLineItemCRADContract) contract;
		PostMTMSerialActionValidator p=new PostMTMSerialActionValidator();
		if(!p.validationMTMSubmittingToSAP(map, ct)){ return false;};
        if (ct.getItems().size()!=0){
        	for (Object key :  ct.getItems().keySet()) {
        		LineItemParameter item = (LineItemParameter) ct.getItems().get(key);
        		if(item.appSendtoMFG!=null && item.appSendtoMFG&&(
        				(item.lineItemCRAD!=null&&!item.lineItemCRAD.equals(item.custReqArrlDate))|| item.lineItemCRAD==null && item.custReqArrlDate!=null) ){
        			Boolean isLineItemCRADValid = ct.isCustReqstdArrivlDateValid(item.custQliReqstdArrivlYear,item.custQliReqstdArrivlMonth
        				,item.custQliReqstdArrivlDay);
        			if (!isLineItemCRADValid){
    					FieldResult field = new FieldResult();
    			        field.setMsg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, DraftQuoteMessageKeys.MSG_ENTER_VALID_CUST_REQSTD_ARRIVL_DATE);
    			        field.addArg(MessageKeys.BUNDLE_APPL_I18N_QUOTE, MessageKeys.CUST_REQSTD_ARRIVL_DATE);
    			        map.put(DraftQuoteParamKeys.PARAM_CUST_REQSTD_ARRIVAL_DATE_SUFFIX, field);
    			        addToValidationDataMap(ct, map);
    			        return false;
        			} 
        		}
        		//MTM_SERIAL INFO
        		if(item.machineType!=null&&item.machineModel!=null&&item.machineSerialNumber!=null){
        			boolean flag= p.validationMachType(item, map, ct)&&p.validationMachModel(item, map, ct)&&p.validationSerialNum(item, map, ct);
        		    if(!flag)
        		    	return false;
        		}
        	}
        }
        
       return true;
	}
	private String getRedirectURL(String displayTabUrl,String quoteNum){
	    String baseUrl = HtmlUtil.getURLForAction(displayTabUrl);
	    StringBuffer url = new StringBuffer(baseUrl);
	    HtmlUtil.addEncodeURLParam(url, ParamKeys.PARAM_QUOTE_NUM, quoteNum);
	    return url.toString();
    }

    private List<String> getRedirectMsgList(String result) {
	   List<String> redirectMsgList = new ArrayList<String>();
       if(QuoteCapabilityProcess.OWNERSHIP_PARTS_SERIAL_NUMBER_NOT_SAME.equalsIgnoreCase(result)){
    	   redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":" + QuoteCapabilityProcess.OWNERSHIP_PARTS_SERIAL_NUMBER_NOT_SAME.toLowerCase() + ":" + MessageBeanKeys.ERROR);
       }else if(QuoteCapabilityProcess.APPLIANCE_SERIAL_NUMBER_NOT_SAME.equalsIgnoreCase(result)){
    	   redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":" + QuoteCapabilityProcess.APPLIANCE_SERIAL_NUMBER_NOT_SAME.toLowerCase() + ":" + MessageBeanKeys.ERROR);
       }else{
    	   redirectMsgList.add(MessageKeys.BUNDLE_APPL_I18N_QUOTE + ":" + SubmittedQuoteMessageKeys.QT_UP_LINE_ITEM_CHG_SUCCESS_MSG + ":" + MessageBeanKeys.SUCCESS);
       }
	   return redirectMsgList;
     }
	
    private class PostMTMSerialActionValidator{
		 protected boolean validationMachType(LineItemParameter lineItemParam,HashMap vMap, UpdateLineItemCRADContract ct) {
				logContext.debug(this, "machineType =" + lineItemParam.machineType);
				return validateMTM(lineItemParam.machineType,4,
						DraftQuoteMessageKeys.MACHINE_TYPE_ALPHANUMERIC_MSG,
						DraftQuoteMessageKeys.MACHINE_TYPE_EXCEED_LIMIT,
						PartPriceViewKeys.MACHINE_TYPE_HDR,
						DraftQuoteParamKeys.APPLNC_MTM_TYPE
						,lineItemParam,vMap,ct);
			}

			protected boolean validationMachModel(LineItemParameter lineItemParam,HashMap vMap, UpdateLineItemCRADContract ct) {	
				int validateLength = 3;
				String lengthMsg = DraftQuoteMessageKeys.MACHINE_MODEL_EXCEED_LIMIT;
				
				logContext.debug(this, "machineModel =" + lineItemParam.machineModel);
				return validateMTM(lineItemParam.machineModel,validateLength,
						DraftQuoteMessageKeys.MACHINE_MODEL_ALPHANUMERIC_MSG,
						lengthMsg,
						PartPriceViewKeys.MACHINE_MODEL_HDR,
						DraftQuoteParamKeys.APPLNC_MTM_MODEL
						,lineItemParam,vMap,ct);
			}
			
			

			protected boolean validationSerialNum(LineItemParameter lineItemParam,HashMap vMap, UpdateLineItemCRADContract ct) {		
				int validateLength = 7;
				String lengthMsg = DraftQuoteMessageKeys.MACHINE_SERIAL_NUBMER_EXCEED_LIMIT;
				boolean flag = true;
					flag = validateMTM(lineItemParam.machineSerialNumber,validateLength,
							DraftQuoteMessageKeys.MACHINE_SERIAL_NUBMER_MSG,
							lengthMsg,
							PartPriceViewKeys.MACHINE_SERIAL_NUMBER_HDR,
							DraftQuoteParamKeys.APPLNC_MTM_SERIAL_NUMBER
							,lineItemParam,vMap,ct);	
				return flag;
			}
			
			

			private boolean validateMTM(String validateProperty,int validateLength,
					String alphanumericMsg,String lengthMsg, String arg, String key,
					LineItemParameter lineItemParam,HashMap vMap, UpdateLineItemCRADContract ct){
				if (StringUtils.isNotBlank(validateProperty) 
						&& (lineItemParam.applncPocInd == null ||"true".equalsIgnoreCase(lineItemParam.applncPocInd) )) {
					validateProperty = validateProperty.trim();
					if (!StringHelper.isAlphanumeric(validateProperty)) {
						addValidationErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
								alphanumericMsg,arg,
								PartPriceViewKeys.PREFIX + lineItemParam.key + key, vMap,ct);
						return false;
					}
					
					
					if (validateProperty.length() != validateLength) {
						addValidationErrorMsg(MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,lengthMsg,arg,PartPriceViewKeys.PREFIX + lineItemParam.key + key, vMap,ct);
						return false;
					}
				}
				return true;
			}
			
			private LineItemParameter getPartInfoFromContract(String key,UpdateLineItemCRADContract ct){
				LineItemParameter item =null;
			if (ct.getItems().size()!=0){
	        	for (Object mapKey :  ct.getItems().keySet()) {
	        		if(key.equalsIgnoreCase(mapKey.toString())){
	        		 item = (LineItemParameter) ct.getItems().get(key);
	        		break;
	        		}
	        	}}
			return item;
			}
			
			private boolean validationMTMSubmittingToSAP(HashMap vMap,
				UpdateLineItemCRADContract ct) {
			boolean flag = true;
			List lineItemList = null;
			try {
				Quote quote = loadQuote(ct);
				lineItemList = quote.getLineItemList();

				if (null == lineItemList) {
					return false;
				}
				List changedLineItemList = new ArrayList();
				int mtCnt = 0, mmCnt = 0, msCnt = 0, mtmChgCnt = 0;
				for (Iterator iterator = lineItemList.iterator(); iterator
						.hasNext();) {
					QuoteLineItem qli = (QuoteLineItem) iterator.next();
					String key = qli.getPartNum().trim() + "_"
							+ qli.getSeqNum();

					if (qli.isApplncPart()) {
						LineItemParameter lineItemFromCt = getPartInfoFromContract(
								key, ct);

						if (null == lineItemFromCt) {
							continue;
						}
						if (qli.isApplianceRelatedSoftware()) {
							continue;
						}
						if (StringUtils.isEmpty(lineItemFromCt.machineType)) {
							if ((!StringUtils
									.trimToEmpty(qli.getMachineType())
									.equals(StringUtils
											.trimToEmpty(lineItemFromCt.machineType)))
									&& (mtCnt < 1)) {
								addValidationErrorMsg(
										MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
										QuoteCapabilityProcess.MACHINE_TYPE_NOT_NULL
												.toLowerCase(),
										PartPriceViewKeys.MACHINE_TYPE_HDR,
										PartPriceViewKeys.PREFIX
												+ key
												+ DraftQuoteParamKeys.APPLNC_MTM_TYPE,
										vMap, ct);
								flag = false;
								mtCnt++;
							}
						} else if ((!StringUtils
								.trimToEmpty(qli.getMachineType())
								.equals(StringUtils
										.trimToEmpty(lineItemFromCt.machineType)))) {
							qli.setMachineType(lineItemFromCt.machineType);
							mtmChgCnt++;
						}

						if (StringUtils.isEmpty(lineItemFromCt.machineModel)) {
							if ((!StringUtils
									.trimToEmpty(qli.getModel())
									.equals(StringUtils
											.trimToEmpty(lineItemFromCt.machineModel)))
									&& (mmCnt < 1)) {
								addValidationErrorMsg(
										MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
										QuoteCapabilityProcess.MACHINE_MODEL_NOT_NULL
												.toLowerCase(),
										PartPriceViewKeys.MACHINE_MODEL_HDR,
										PartPriceViewKeys.PREFIX
												+ key
												+ DraftQuoteParamKeys.APPLNC_MTM_MODEL,
										vMap, ct);
								flag = false;
								mmCnt++;
							}
						} else if ((!StringUtils
								.trimToEmpty(qli.getModel())
								.equals(StringUtils
										.trimToEmpty(lineItemFromCt.machineModel)))
								&& (mmCnt < 1)) {
							qli.setModel(lineItemFromCt.machineModel);
							mtmChgCnt++;
						}

						if (StringUtils
								.isEmpty(lineItemFromCt.machineSerialNumber)) {
							if ((!StringUtils
									.trimToEmpty(qli.getSerialNumber())
									.equals(StringUtils
											.trimToEmpty(lineItemFromCt.machineSerialNumber)))
									&& (msCnt < 1)) {
								addValidationErrorMsg(
										MessageKeys.BUNDLE_APPL_I18N_PART_PRICE,
										QuoteCapabilityProcess.MACHINE_SERIAL_NUMBER_NOT_NULL
												.toLowerCase(),
										PartPriceViewKeys.MACHINE_SERIAL_NUMBER_HDR,
										PartPriceViewKeys.PREFIX
												+ key
												+ DraftQuoteParamKeys.APPLNC_MTM_SERIAL_NUMBER,
										vMap, ct);
								flag = false;
								msCnt++;
							}
						} else if ((!StringUtils
								.trimToEmpty(qli.getSerialNumber())
								.equals(StringUtils
										.trimToEmpty(lineItemFromCt.machineSerialNumber)))
								&& (msCnt < 1)) {
							qli.setSerialNumber(lineItemFromCt.machineSerialNumber);
							mtmChgCnt++;
						}

					}

					changedLineItemList.add(qli);

				}

				if (mtmChgCnt > 0) {
					quote.setLineItemList(changedLineItemList);
					QuoteProcess quoteProcess = QuoteProcessFactory.singleton().create();
					quoteProcess.updateLineItemCRAD(quote,ct.getUserId());
				}
			} catch (Exception e) {
				logContext
						.error(this, LogThrowableUtil.getStackTraceContent(e));
			}

			return flag;
		}

			private Quote loadQuote(UpdateLineItemCRADContract ct)
				throws QuoteException, NoDataException, TopazException {
			String webQuoteNum = ct.getQuoteNum();
			QuoteProcess quoteProcess = QuoteProcessFactory.singleton()
					.create();
			return quoteProcess.getQuoteForSubmittedCRAD(webQuoteNum);

		}
			
			
			private void addValidationErrorMsg(String resource, String msg, String arg, String key,HashMap vMap, UpdateLineItemCRADContract ct){
		        FieldResult fieldResult = new FieldResult();
		        fieldResult.setMsg(resource,msg);
		        fieldResult.addArg(resource, arg);
		        vMap.put(key, fieldResult);
		        addToValidationDataMap(ct, vMap);
		    }

	 }

    protected boolean validateEqual(Object originalValue, Object targetValue){
        if (null == originalValue && null == targetValue){
            return true;
        }
        if (null != originalValue && null != targetValue && originalValue.equals(targetValue)){
            return true;
        }
        return false;
    }
    
    private void updateShipTo(Quote quote, List lineItems, List modifiedList) throws TopazException, QuoteException
    {
    	ApplianceLineItemAddrDetail ads = null;
    	
    	List<ApplianceAddress> addrList = new ArrayList<ApplianceAddress>();
    	//compare with previous line items to determine if has ship to change
    	for ( int i = 0; i < modifiedList.size(); i++ )
    	{
    		QuoteLineItem item = (QuoteLineItem)modifiedList.get(i);
    		logContext.debug(this, "item mtm changed: " + item.getModifiedProperty().isMtm() + "; " + item.getConfigrtnId() + ";" + item.getModifiedProperty().isMtm() + ";" + 
    				item.isRenewalPart() + ";" + item.isApplncUpgrade());
    		if ( item.getModifiedProperty().isMtm() 
    				&& ( !item.isApplncUpgrade() 
    						&& (StringUtils.isBlank(item.getConfigrtnId()) || StringUtils.equals(item.getConfigrtnId().trim(), PartPriceConstants.APPLNC_NOT_ON_QUOTE)) 
    					) 	
    				)
    		{
//    			QuoteLineItem temp = null;
//    			for ( int j = 0; j < lineItems.size(); j++ )
//    			{
//    				QuoteLineItem item2 = (QuoteLineItem)lineItems.get(j);
//    				if ( item.getSeqNum() == item2.getSeqNum() )
//    				{
//    					temp = item2;
//    					break;
//    				}
//    			}
//    			if ( temp == null )
//    			{
//    				//continue;
//    				logContext.debug(this, "get line item from quote line item list null, skip");
//    			}
    			
    			
    			ApplianceAddress addr = null;
    			if(StringUtils.isNotBlank(item.getMachineType()) && StringUtils.isNotBlank(item.getModel()) && StringUtils.isNotBlank(item.getSerialNumber()))
    			addr = CustomerProcessFactory.singleton().create().getInstallAtByMTM(item.getMachineType(), item.getModel(), item.getSerialNumber(), "", 0);
    			ApplianceAddress soldToAddr = null;
    	    	if ( addr == null )
    	    	{
    	    		logContext.debug(this, "get addr from mtm is null, set as sold to");
//    	    				if ( ads == null )
//    	    				{
//    	    					ads = CustomerProcessFactory.singleton().create().findAddrLineItem(quote.getQuoteHeader().getWebQuoteNum(), "", lineItems, quote.getQuoteHeader().isDisShipInstAdrFlag());
//    	    				}
//    	    				addr = getAddrByLineItem(ads, item);
//    	    				if ( addr != null )
//    	    				{
//    	    					if ( addr.isAddrBaseMTM() )
//    	    					{
//    	    						//need to override previous one; do nothing now
//    	    					}
//    	    					else
//    	    					{
//    	    						//do nothing
//    	    					}
//    	    				}
    	    		if ( soldToAddr == null )
    	    		{
    	    			soldToAddr = ApplianceLineItemAddrDetail.getAddrFromCustomer(quote.getCustomer());
    	    			addrList.add(soldToAddr);
    	    			soldToAddr.setAddrBaseMTM(true);
    	    		}
    	    		soldToAddr.addLineItem(item);
    	    	}
    	    	else
    	    	{
    	    		//add the address to return list
    	    		logContext.debug(this, "get addr from mtm:" + addr.getCustNum());
    	    		addrList.add(addr);
    	    		addr.addLineItem(item);
    	    	}
    		}
    	}
    	if ( addrList.size() > 0 )
    	{
    		logContext.debug(this, "there has new mtm address: " + addrList.size());
    		ApplianceLineItemAddrDetail retAddr = new ApplianceLineItemAddrDetail();
    		retAddr.setShipToAddressList(addrList);
    		quote.setApplianceLineItemAddrDetail(retAddr);
    	}
    }
    
    private ApplianceAddress getAddrByLineItem(ApplianceLineItemAddrDetail ads, QuoteLineItem item)
    {
    	if ( ads == null)
    	{
    		return null;
    	}
    	List<ApplianceAddress> list = ads.getShipToAddressList();
    	if ( list == null )
    	{
    		return null;
    	}
    	for ( ApplianceAddress addr : list )
    	{
    		List<ApplianceLineItem> appItems = addr.getLineItemList();
    		if ( appItems == null )
    		{
    			continue;
    		}
    		for ( ApplianceLineItem appItem : appItems )
    		{
    			if ( item.getSeqNum() == appItem.getQuoteLineItemSeqNum() )
    			{
    				return addr;
    			}
    		}
    	}
    	return null;
    }
    
    private String valueOfStr(String value) {
    	String validStr = value == null ? "" : value;
    	return validStr;
    }
    
    
}
