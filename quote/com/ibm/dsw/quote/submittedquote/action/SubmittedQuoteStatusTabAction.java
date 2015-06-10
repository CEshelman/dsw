package com.ibm.dsw.quote.submittedquote.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Order;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteContact;
import com.ibm.dsw.quote.common.domain.QuoteCustomerRelatedDocument;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteOutput;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.common.process.QuoteProcess;
import com.ibm.dsw.quote.common.process.QuoteProcessFactory;
import com.ibm.dsw.quote.common.service.ContentManagerDocIdServiceHelper;
import com.ibm.dsw.quote.common.service.WorkflowDetailServiceHelper;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteStateKeys;
import com.ibm.dsw.quote.draftquote.config.DraftRQViewKeys;
import com.ibm.dsw.quote.findquote.config.FindQuoteDBConstants;
import com.ibm.dsw.quote.findquote.process.QuoteStatusProcessFactory;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteConstants;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteMessageKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteParamKeys;
import com.ibm.dsw.quote.submittedquote.config.SubmittedQuoteStateKeys;
import com.ibm.dsw.quote.submittedquote.contract.SubmittedQuoteBaseContract;
import com.ibm.dsw.quote.submittedquote.viewbean.helper.SpecialBidCommon;
import com.ibm.ead4j.jade.bean.ResultHandler;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.contract.ProcessContract;
import com.ibm.ead4j.opal.log.LogContextFactory;

public class SubmittedQuoteStatusTabAction extends SubmittedQuoteBaseAction {
    
    public static final String SQ_STAT_ICN_REQUESTED = "E0001";
	public static final String SQ_STAT_PRE_CREDIT_CHECK_REQUESTED = "E0002";
	public static final String RQ_STAT_ICN_REQUESTED = "E0041";
	public static final String RQ_STAT_PRE_CREDIT_CHECK_REQUESTED = "E0043";
	

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.action.SubmittedQuoteBaseAction#getSubmittedQuoteDetail(com.ibm.ead4j.jade.contract.ProcessContract,
     *      com.ibm.ead4j.jade.bean.ResultHandler)
     */
    protected Quote getSubmittedQuoteDetail(Quote quote, ProcessContract contract, ResultHandler handler)
            throws QuoteException {
        SubmittedQuoteBaseContract c = (SubmittedQuoteBaseContract) contract;
        LogContextFactory.singleton().getLogContext().debug(this, "get submitted quote detail->start");
        //fill special bid related info
        QuoteProcessFactory.singleton().create().getQuoteDetailForStatusTab(quote,c.getQuoteUserSession());
        quote.setPgsAppl(c.isPGSEnv());
        handler.addObject(SubmittedQuoteParamKeys.SPECIAL_BID_COMMON, new SpecialBidCommon(quote, c.getUserId()));

        if (QuoteConstants.QUOTE_TYPE_RENEWAL.equals(quote.getQuoteHeader().getQuoteTypeCode())) {
            QuoteProcess qp = QuoteProcessFactory.singleton().create();
            String reasonCode = StringUtils.trimToEmpty(quote.getQuoteHeader().getRnwlTermntnReasCode());

            String reasonStr = "";
            List reasons = qp.getTerminationReasons();
            for (Iterator iter = reasons.iterator(); iter.hasNext();) {
                CodeDescObj obj = (CodeDescObj) iter.next();
                if (reasonCode.equals(obj.getCode())) {
                    reasonStr = obj.getCodeDesc();
                    break;
                }
            }

            handler.addObject(DraftRQViewKeys.RQ_TERM_REASON, reasonStr);
        }
        //get related quote pre-check status
        if (QuoteConstants.QUOTE_TYPE_SALES.equals(quote.getQuoteHeader().getQuoteTypeCode())) {
        	QuoteProcess qp = QuoteProcessFactory.singleton().create();
        	List statusList = qp.getPrecheckStatus(quote.getQuoteHeader().getWebQuoteNum());
        	handler.addObject(SubmittedQuoteParamKeys.QUOTE_PRECK_STATUS, statusList);
        }
        
        String up2ReportingUserIds = "";
        if (c.getQuoteUserSession() != null) {
            up2ReportingUserIds = c.getQuoteUserSession().getUp2ReportingUserIds();
        }
        SearchResultList srl = QuoteStatusProcessFactory.singleton().create().findByQuoteNumber(c.getQuoteNum(),
                c.getUserId(), up2ReportingUserIds, getEcareFlag(c),"","","","0","0","1",FindQuoteDBConstants.FIND_QUOTE_PAGE_SIZE,"","","0","0",new HashMap());
        if (srl.getResultCount() > 0) {
            //fill status info
            Quote tempQuote = (Quote) srl.getResultList().get(0);
            quote.setOrders(tempQuote.getOrders());
            quote.setSapPrimaryStatusList(tempQuote.getSapPrimaryStatusList());
            quote.setSapSecondaryStatusList(tempQuote.getSapSecondaryStatusList());
        }

        if (quote == null || quote.getQuoteHeader() == null) {
            handler.setState(DraftQuoteStateKeys.STATE_DISPLAY_EMPTY_DRAFT_QUOTE);
            return quote;
        }

        Map orderWfStatusMap = new HashMap();

        if (quote.getQuoteHeader().containsOverallStatus(SubmittedQuoteConstants.OverallStatus.ORDER_ON_HOLD)) {
            if (quote.getOrders() != null) {
                for (Iterator iter = quote.getOrders().iterator(); iter.hasNext();) {
                    Order order = (Order) iter.next();
                    if (StringUtils.isNotBlank(order.getOrderNumber())) {
                        Map orderWorkFlowDetailMap = WorkflowDetailServiceHelper.singleton().retrieveWorkflowDetails(
                                order.getOrderNumber());
                        orderWfStatusMap.put(order.getOrderNumber(), orderWorkFlowDetailMap);
                    }
                }
            }
        }

        handler.addObject(SubmittedQuoteParamKeys.WORKFLOW_DETAIL_ORDER, orderWfStatusMap);
        
        if (quote.getQuoteHeader().containsOverallStatus(SubmittedQuoteConstants.OverallStatus.QUOTE_ON_HOLD)
                || (quote.getQuoteHeader().isSalesQuote() && (quote.containsSapPrimaryStatus(SQ_STAT_ICN_REQUESTED) || quote
                        .containsSapPrimaryStatus(SQ_STAT_PRE_CREDIT_CHECK_REQUESTED)))
                || (quote.getQuoteHeader().isRenewalQuote()
                        && (quote.containsSapSecondaryStatus(RQ_STAT_ICN_REQUESTED) || quote
                        .containsSapSecondaryStatus(RQ_STAT_ICN_REQUESTED)))) {
            //the sap quote # will be blank before sap create quote and return
            if (StringUtils.isNotBlank(quote.getQuoteHeader().getSapQuoteNum())) {
                Map quoteWorkFlowDetail = WorkflowDetailServiceHelper.singleton().retrieveWorkflowDetails(
                        quote.getQuoteHeader().getSapQuoteNum());
                handler.addObject(SubmittedQuoteParamKeys.WORKFLOW_DETAIL_QUOTE, quoteWorkFlowDetail);
            }
        }

        if (QuoteConstants.QUOTE_TYPE_RENEWAL.equals(quote.getQuoteHeader().getQuoteTypeCode())) {
            handler.setState(SubmittedQuoteStateKeys.STATE_SUBMITTED_RQ_STATUS_TAB);
        } else {
            handler.setState(SubmittedQuoteStateKeys.STATE_SUBMITTED_SQ_STATUS_TAB);
        }
        LogContextFactory.singleton().getLogContext().debug(this, "get submitted quote detail->done");

        //Access to SAP outputs from SQO

        handler.addObject(SubmittedQuoteParamKeys.QUOTE_OUTPUT, generateQuoteOutputs(quote));
        
        //Get the Customer related docements info
            
        handler.addObject(SubmittedQuoteParamKeys.CUSTOMER_RELATED_DOCUMENTS, getCustomerRelatedDocumentsInfo(quote, c));
        
        return quote;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.submittedquote.action.SubmittedQuoteBaseAction#getState(com.ibm.ead4j.jade.contract.ProcessContract)
     */
    protected String getState(ProcessContract contract) {
        return "";
    }

    public String getEcareFlag(QuoteBaseContract contract) {
        if (contract.getUser().getAccessLevel(QuoteConstants.APP_CODE_SQO) == QuoteConstants.ACCESS_LEVEL_ECARE)
            return "1";
        return "0";
    }

    private List generateQuoteOutputs(Quote quote){
        List qos = new ArrayList();
        QuoteHeader quoteHeader = quote.getQuoteHeader();
        
        if (quoteHeader.isSalesQuote() && StringUtils.isNotBlank(quoteHeader.getSapQuoteNum())){
            
            // If the quote was a PA/PAE sales quote and if Special Bid the Special bid has been approved and the fulfilment source is direct
            if ((QuoteConstants.FULFILLMENT_DIRECT.equals(quoteHeader.getFulfillmentSrc()) 
                    && (0 == quoteHeader.getSpeclBidFlag() 
                            || (1 == quoteHeader.getSpeclBidFlag() && null != quote.getSubmittedQuoteAccess().getSbApprovedDate())))
            // OR the fulfilment source is channel and it's not special bid
            // OR the fulfilment source is channel and it's special bid and it has been approved and theTBD Reseller flag is true
                || (quoteHeader.isChannelQuote() 
                        && (0 == quoteHeader.getSpeclBidFlag() 
                                || 1 == quoteHeader.getSpeclBidFlag() 
                                && null != quote.getSubmittedQuoteAccess().getSbApprovedDate() 
                                && quoteHeader.isResellerToBeDtrmndFlag()))){
                
                if (quoteHeader.isPAEQuote() || quoteHeader.isPAQuote() || quoteHeader.isFCTQuote() || quoteHeader.isPPSSQuote() || quoteHeader.isSSPQuote()
                		||quoteHeader.isOEMQuote()){
	                QuoteOutput cqPdf = new QuoteOutput();
	                if(quoteHeader.isPAEQuote() || quoteHeader.isPAQuote() || quoteHeader.isPPSSQuote() || quoteHeader.isSSPQuote()){
	                	if(quoteHeader.hasSaaSLineItem() ||quoteHeader.isHasMonthlySoftPart() ){
	                			cqPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZSQA);
	                	}else{
	                		cqPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZTQ1);
	                	}
	                }
	                if(quoteHeader.isFCTQuote()){
	                	if(quoteHeader.hasSaaSLineItem()){
	                		cqPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZSQF);
	                	}else{
	                		cqPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZTQF);
	                	}
	                }
            		// SaaS and Monthly channel
	                if((quoteHeader.hasSaaSLineItem() || quoteHeader.isHasMonthlySoftPart()) && quoteHeader.isChannelQuote()){
	                	if(0 == quoteHeader.getSpeclBidFlag()){
            				cqPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZSQC);
            			}else{
            				cqPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZABS);
            			}
	                	
	                	if(!QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteHeader.getAudCode())){
		                	if(quoteHeader.isResellerToBeDtrmndFlag()) {
	        					cqPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZSQA);
	        				}
	                	}
	                	
	                }
	                // 12.4 New RFC Flag ZZOUTTYPE = RATE
	                if((quoteHeader.hasSaaSLineItem() || quoteHeader.isHasMonthlySoftPart()) && "RATE".equalsIgnoreCase(quoteHeader.getQuoteOutputType())){
	                	if(quoteHeader.isPAEQuote() || quoteHeader.isPAQuote() || quoteHeader.isPPSSQuote()){
	                		cqPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZRQA);
	                	}
	                	if(quoteHeader.isFCTQuote()){
	                		cqPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZRQF);
	                	}
	                }

	                //14.4 Handle output type for OEM quotes
	                if(quoteHeader.isOEMQuote()){
	                	cqPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZTQM);
	                }
	                
	                cqPdf.setOutputName(SubmittedQuoteMessageKeys.QUOTE_OUTPUT_OUTPUT_NAME_CQPDF);
	                
	                List distributedTo = new ArrayList();
	                
	                if(1 == quoteHeader.getSendQuoteToPrmryCntFlag()){
	                    if(StringUtils.isNotBlank(quote.getCustomer().getCntEmailAdr())){
	                        distributedTo.add(quote.getCustomer().getCntEmailAdr());
	                    }
	                }
	                if(1 == quoteHeader.getSendQuoteToQuoteCntFlag()){
	                    if(!distributedTo.contains(((QuoteContact)quote.getContactList().get(0)).getCntEmailAdr())){
	                        distributedTo.add(((QuoteContact)quote.getContactList().get(0)).getCntEmailAdr());
	                    }
	                }
	                if(1 == quoteHeader.getSendQuoteToAddtnlCntFlag()){
	                    if(StringUtils.isNotBlank(quoteHeader.getAddtnlCntEmailAdr())){
	                        if(!distributedTo.contains(quoteHeader.getAddtnlCntEmailAdr()))
	                        distributedTo.add(quoteHeader.getAddtnlCntEmailAdr());
	                    }
	                }
	                // add quote submitter/opp owner to distributed to list
	                if(StringUtils.isNotBlank(quoteHeader.getSubmitterId())){
	                    if(!distributedTo.contains(quoteHeader.getSubmitterId()))
	                        distributedTo.add(quoteHeader.getSubmitterId());
	                }
	                if(quoteHeader.isPAEQuote() || quoteHeader.isPAQuote() 
	                        || quoteHeader.isFCTQuote() || quoteHeader.isFCTToPAQuote()){
	                    if(StringUtils.isNotBlank(quoteHeader.getOpprtntyOwnrEmailAdr())){
	                        if(!distributedTo.contains(quoteHeader.getOpprtntyOwnrEmailAdr()))
	                            distributedTo.add(quoteHeader.getOpprtntyOwnrEmailAdr());
	                    }
	                }
	                if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteHeader.getAudCode())){
	                	if (quote.getPayer()!=null && quote.getPayer().getY9EmailList()!=null){
	                		Iterator iter = quote.getPayer().getY9EmailList().iterator();
		                	while(iter.hasNext()){
		                		String y9EmailAddress = (String)iter.next();
		                		if(!distributedTo.contains(y9EmailAddress)){
		                			distributedTo.add(y9EmailAddress);
		                		}
		                	}
	                	}
	                }
	                cqPdf.setDistributedTo(distributedTo);
	                
	                qos.add(cqPdf);
                }
            }
            
            //If the quote was a channel special bid approved quote and the TBD Reseller flag is false 
            if (1 == quoteHeader.getSpeclBidFlag() && null != quote.getSubmittedQuoteAccess().getSbApprovedDate()
                    && quoteHeader.isChannelQuote() && !quoteHeader.isResellerToBeDtrmndFlag()) {
                QuoteOutput scbnPdf = new QuoteOutput();
                
                scbnPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZABN);
        		// SaaS and Monthly channel
                if((quoteHeader.hasSaaSLineItem() || quoteHeader.isHasMonthlySoftPart()) && quoteHeader.isChannelQuote()){
                	scbnPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZABS);
                }
                // 12.4 New RFC Flag ZZOUTTYPE = RATE
                if((quoteHeader.hasSaaSLineItem() || quoteHeader.isHasMonthlySoftPart()) && "RATE".equalsIgnoreCase(quoteHeader.getQuoteOutputType())){
                	if(quoteHeader.isPAEQuote() || quoteHeader.isPAQuote() || quoteHeader.isPPSSQuote()){
                		scbnPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZRQA);
                	}
                	if(quoteHeader.isFCTQuote()){
                		scbnPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZRQF);
                	}
                }
                //14.4 Handle output type for OEM quotes
                if(quoteHeader.isOEMQuote()){
                	scbnPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZTQM);
                }
                scbnPdf.setOutputName(SubmittedQuoteMessageKeys.QUOTE_OUTPUT_OUTPUT_NAME_CSBNPDF);

                List distributedTo = new ArrayList();
                if(quoteHeader.isSendQuoteToAddtnlPrtnrFlag()){
                	if (quote.getPayer()!=null){
                		 distributedTo.addAll(quote.getPayer().getY9EmailList());
                	} 
                }
                if(StringUtils.isNotBlank(quoteHeader.getAddtnlPrtnrEmailAdr())){
                    if(!distributedTo.contains(quoteHeader.getAddtnlPrtnrEmailAdr())){
                        distributedTo.add(quoteHeader.getAddtnlPrtnrEmailAdr());
                    }
                }
                // add quote submitter/opp owner to distributed to list
                if(StringUtils.isNotBlank(quoteHeader.getSubmitterId())){
                    if(!distributedTo.contains(quoteHeader.getSubmitterId()))
                        distributedTo.add(quoteHeader.getSubmitterId());
                }
                if(quoteHeader.isPAEQuote() || quoteHeader.isPAQuote() 
                        || quoteHeader.isFCTQuote() || quoteHeader.isFCTToPAQuote()){
                    if(StringUtils.isNotBlank(quoteHeader.getOpprtntyOwnrEmailAdr())){
                        if(!distributedTo.contains(quoteHeader.getOpprtntyOwnrEmailAdr()))
                            distributedTo.add(quoteHeader.getOpprtntyOwnrEmailAdr());
                    }
                }
                if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteHeader.getAudCode())){
                	if (quote.getPayer()!=null && quote.getPayer().getY9EmailList()!=null ){
                		Iterator iter = quote.getPayer().getY9EmailList().iterator();
                    	while(iter.hasNext()){
                    		String y9EmailAddress = (String)iter.next();
                    		if(!distributedTo.contains(y9EmailAddress)){
                    			distributedTo.add(y9EmailAddress);
                    		}
                    	}
                	}
                }
                scbnPdf.setDistributedTo(distributedTo);
                
                qos.add(scbnPdf);
            }
        }

        if(quoteHeader.getBudgetaryQuoteFlag()==1){
	        QuoteOutput bqPdf = new QuoteOutput();
	        // 13.4 The new budgetary quote outputs are as follows.
	        if(quoteHeader.hasSaaSLineItem() && "RATE".equalsIgnoreCase(quoteHeader.getQuoteOutputType())) {

	        	//Budgetary Rate Quotes FCT
	        	if(quoteHeader.isPAEQuote() || quoteHeader.isPAQuote()) {
	        		bqPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZRBA);
	        	}
	        	
	        	//Budgetary Rate Quotes FCT
	        	if(quoteHeader.isFCTQuote()) {
	        		bqPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZRBF);
	        	}
	        } else {
	        	//Budgetary Rate Quotes PA / PAE
	        	if(quoteHeader.isPAEQuote() || quoteHeader.isPAQuote()) {
	        		bqPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZSBA);
	        	}
	        	
	        	//Budgetary TCV Quotes FCT
	        	if(quoteHeader.isFCTQuote()) {
	        		bqPdf.setOutputType(QuoteConstants.QUOTE_OUTPUT_OUTPUT_TYPE_ZSBF);
	        	}
	        }

	        bqPdf.setOutputName(SubmittedQuoteMessageKeys.QUOTE_OUTPUT_OUTPUT_NAME_BQPDF);

            List distributedTo = new ArrayList();
            
            if(1 == quoteHeader.getSendQuoteToPrmryCntFlag()){
                if(StringUtils.isNotBlank(quote.getCustomer().getCntEmailAdr())){
                    distributedTo.add(quote.getCustomer().getCntEmailAdr());
                }
            }
            if(1 == quoteHeader.getSendQuoteToQuoteCntFlag()){
                if(!distributedTo.contains(((QuoteContact)quote.getContactList().get(0)).getCntEmailAdr())){
                    distributedTo.add(((QuoteContact)quote.getContactList().get(0)).getCntEmailAdr());
                }
            }
            if(1 == quoteHeader.getSendQuoteToAddtnlCntFlag()){
                if(StringUtils.isNotBlank(quoteHeader.getAddtnlCntEmailAdr())){
                    if(!distributedTo.contains(quoteHeader.getAddtnlCntEmailAdr()))
                    distributedTo.add(quoteHeader.getAddtnlCntEmailAdr());
                }
            }
            // add quote submitter/opp owner to distributed to list
            if(StringUtils.isNotBlank(quoteHeader.getSubmitterId())){
                if(!distributedTo.contains(quoteHeader.getSubmitterId()))
                    distributedTo.add(quoteHeader.getSubmitterId());
            }
            if(quoteHeader.isPAEQuote() || quoteHeader.isPAQuote() 
                    || quoteHeader.isFCTQuote() || quoteHeader.isFCTToPAQuote()){
                if(StringUtils.isNotBlank(quoteHeader.getOpprtntyOwnrEmailAdr())){
                    if(!distributedTo.contains(quoteHeader.getOpprtntyOwnrEmailAdr()))
                        distributedTo.add(quoteHeader.getOpprtntyOwnrEmailAdr());
                }
            }
            if(QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteHeader.getAudCode())){
            	if (quote.getPayer()!=null && quote.getPayer().getY9EmailList()!=null ){
            		Iterator iter = quote.getPayer().getY9EmailList().iterator();
                	while(iter.hasNext()){
                		String y9EmailAddress = (String)iter.next();
                		if(!distributedTo.contains(y9EmailAddress)){
                			distributedTo.add(y9EmailAddress);
                		}
                	}
            	}
            }
            bqPdf.setDistributedTo(distributedTo);
	        
	        qos.add(bqPdf);
        }
        
        if(qos != null && qos.size() > 0){
	        ContentManagerDocIdServiceHelper helper = new ContentManagerDocIdServiceHelper();
	        try{
	            qos = helper.execute(quoteHeader.getSapQuoteNum(),qos);
	        }catch(QuoteException e){
	            LogContextFactory.singleton().getLogContext().error(this, e);
	        }
        }
        
        return qos;
    }
    
    //get customer related documents info
    private List getCustomerRelatedDocumentsInfo(Quote quote, SubmittedQuoteBaseContract contract)
            throws QuoteException {
        List docsInfoList = null;
        QuoteHeader quoteHeader = quote.getQuoteHeader();
        if (quoteHeader.hasNewCustomer() &&!(quoteHeader.isCSRAQuote()||quoteHeader.isCSTAQuote()) &&(quoteHeader.isPAQuote() || quoteHeader.isPAEQuote())
                && (quote.getQuoteUserAccess().isEditor() || contract.getUserId().equals(quoteHeader.getSubmitterId()))) {
            docsInfoList = new ArrayList();
            QuoteCustomerRelatedDocument document = new QuoteCustomerRelatedDocument();
            document.setDocumentName(SubmittedQuoteMessageKeys.QUOTE_CUST_RELATED_DOC_NEW_CUST_MAIL);
            if (quote.getCustomer().getSupprsPARegstrnEmailFlag() == 1) {
                if (quoteHeader.isPAQuote() && quote.getCustomer().getPAIndCode() == 1) {
                    document.setPending(false);
                } else {
                    document.setPending(true);
                }
            } else {
                document.setPending(false);
                ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
                String msgInfo = context.getI18nValueAsString(I18NBundleNames.BASE_MESSAGES, contract.getLocale(), SubmittedQuoteMessageKeys.QUOTE_CUST_RELATED_DOC_DISTRIBUTED_TO);
                String[] args = { quote.getCustomer().getCntEmailAdr() };
                document.setDistributedTo(MessageFormat.format(msgInfo, args));
            }
            docsInfoList.add(document);
        }
        return docsInfoList;
    }
}