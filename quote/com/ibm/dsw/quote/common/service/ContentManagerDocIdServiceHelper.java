package com.ibm.dsw.quote.common.service;

import java.rmi.RemoteException;
import java.util.List;

import DswCustomerLibrary.ContentManagerDocId;
import DswCustomerLibrary.DocIdInfo;
import DswCustomerLibrary.DocInfo;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.CommonServiceConstants;
import com.ibm.dsw.quote.common.domain.QuoteOutput;
import com.ibm.dsw.wpi.java.util.ServiceLocator;
import com.ibm.dsw.wpi.java.util.ServiceLocatorException;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @author zhangln
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ContentManagerDocIdServiceHelper {

    private static final String QUOTE_OUTPUT_OBJECT_TYPE_VBAK = "VBAK"; 
    private LogContext log = LogContextFactory.singleton().getLogContext();

    public List execute(String sapQuoteNum, List qos) throws QuoteException {
        if (qos == null) {
            return null;
        }
        DocInfo[] docInfo = new DocInfo[qos.size()];
        for (int i = 0; i < docInfo.length; i++) {
            DocInfo di = new DocInfo();
            QuoteOutput qo = (QuoteOutput) qos.get(i);
            di.setDocumentNumber(sapQuoteNum);
            di.setObjectType(QUOTE_OUTPUT_OBJECT_TYPE_VBAK);
            di.setOutputType(qo.getOutputType());
            docInfo[i] = di;
        }
        DocIdInfo[] docIdInfo = null;
        if (docInfo.length > 0) {
            try {
                docIdInfo = execute(docInfo);
            } catch (QuoteException e) {
                log.error(this, "QuoteException: " + e.toString());
                throw new QuoteException("Failed to get quote output document id for quote number " + sapQuoteNum);
            }
        }
        for (int i = 0; docIdInfo != null && i < docIdInfo.length; i++) {
            DocIdInfo dii = docIdInfo[i];
            for (int j = 0; j < qos.size(); j++) {
                QuoteOutput qo = (QuoteOutput) qos.get(j);
                if (qo.getOutputType().equalsIgnoreCase(dii.getOutputType())) {
                	if(dii.getDocumentID()!=null && !dii.getDocumentID().trim().equals("")){
                		qo.addDocId(dii.getDocumentID());
                	}
                }
            }
        }
        return qos;
    }

    private DocIdInfo[] execute(DocInfo[] args) throws QuoteException {
        try {
            ServiceLocator sLoc = new ServiceLocator();
            ContentManagerDocId port = (ContentManagerDocId) sLoc.getServicePort(
                    CommonServiceConstants.CONTENT_MANAGER_DOC_ID_BINDING, ContentManagerDocId.class);

            return port.execute(args);
        } catch (RemoteException e) {
            log.error(this, "RemoteException: " + e.toString());
            throw new QuoteException("Failed to get quote output document id");
        } catch (ServiceLocatorException e) {
            log.error(this, "ServiceLocatorException: " + e.toString());
            throw new QuoteException("Failed to get quote output document id");
        }
    }
}
