package com.ibm.dsw.quote.common.service;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import DswSalesLibrary.WorkflowDetail;
import DswSalesLibrary.WorkflowDetailsInput;
import DswSalesLibrary.WorkflowDetailsOutput;
import DswSalesLibrary.WorkflowDetailsProxy;
import DswSalesLibrary.WorkflowRecipient;
import DswSalesLibrary.WorkflowResponse;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>WorkflowDetailServiceHelper</code> class is the service client
 * helper for workflow detail service.
 * 
 * @author: <a href="zhaohuic@cn.ibm.com">Aaron </a>
 * 
 * Creation date: May 22, 2007
 */
public class WorkflowDetailServiceHelper implements Serializable {
    private static final WorkflowDetailServiceHelper _instance = new WorkflowDetailServiceHelper();

    private transient LogContext logger = LogContextFactory.singleton().getLogContext();

    public static WorkflowDetailServiceHelper singleton() {
        return _instance;
    }

    public WorkflowDetailsOutput getSapWorkflowDetails(String docNum) throws QuoteException {
        WorkflowDetailsProxy proxy = new WorkflowDetailsProxy();

        WorkflowDetailsInput input = new WorkflowDetailsInput();
        input.setDocNumber(docNum);

        WorkflowDetailsOutput output = null;
        try {
            output = proxy.execute(input);
        } catch (RemoteException re) {
            String exceptionString = "Remote exception when invoking workflow detail service for doc [" + docNum + "]";
            logger.error(this, exceptionString);
            throw new QuoteException(exceptionString, re);
        }
        return output;
    }

    public Map retrieveWorkflowDetails(String docNum) throws QuoteException {
        WorkflowDetailsOutput output = getSapWorkflowDetails(docNum);
        Map statusDetails = new HashMap();

        WorkflowResponse response = output.getWorkflowResponse();

        boolean error = response.getErrorFlag() != null && response.getErrorFlag().booleanValue();
        if (error) {
            String errorString = "Error when getting workflow detail service for doc [" + docNum + "], returnCode="
                    + response.getReturnCode() + "; returnMessage=" + response.getReturnMessage();
            logger.error(this, errorString);
            //            throw new QuoteException(errorString);
            return statusDetails;
        }

        WorkflowDetail[] details = output.getWorkflowDetails();
        WorkflowRecipient[] recipients = output.getWorkflowRecipients();

        if (details == null || details.length == 0) {
            return statusDetails;
        }

        for (int i = 0; i < details.length; i++) {
            WorkflowDetail detail = details[i];
            StatusDetail statusDetail = new StatusDetail();
            statusDetail.workItemText = detail.getWorkItemText();
            if (recipients != null && recipients.length > 0) {
                for (int j = 0; j < recipients.length; j++) {
                    WorkflowRecipient recipient = recipients[j];
                    if (recipient.getWorkItemId().equals(detail.getWorkItemId())) {
                        statusDetail.recName = recipient.getRecipientName();
                        break;
                    }
                }
            }
            statusDetails.put(detail.getDocStatEcode(), statusDetail);
        }

        return statusDetails;
    }

    public class StatusDetail implements Serializable{

        String workItemText = null;
        String recName = null;
        
        public String getWorkItemText() {
            return workItemText;
        }
        
        public String getRecName() {
            return recName;
        }
    }
}
