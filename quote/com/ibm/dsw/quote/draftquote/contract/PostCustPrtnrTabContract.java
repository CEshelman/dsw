package com.ibm.dsw.quote.draftquote.contract;

import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PostCustPrtnrTabContract<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 13, 2007
 */

public class PostCustPrtnrTabContract extends PostDraftQuoteBaseContract {

    private String cntFirstName;

    private String cntLastName;

    private String cntPhoneNumFull;

    private String cntFaxNumFull;

    private String cntEmailAdr;

    private String fullfillmentSrc;
    
    private String partnerAccess;
    
    private String resellerToBeDetermined;
    
    private String distributorToBeDetermined;

    /**
     * @param parameters
     * @param session
     * @see com.ibm.ead4j.jade.contract.ProcessContract#load(com.ibm.ead4j.jade.util.Parameters,
     *      com.ibm.ead4j.jade.session.JadeSession)
     */
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
    }

    /**
     * @return Returns the cntEmailAdr.
     */
    public String getCntEmailAdr() {
        return cntEmailAdr;
    }

    /**
     * @param cntEmailAdr
     *            The cntEmailAdr to set.
     */
    public void setCntEmailAdr(String cntEmailAdr) {
        this.cntEmailAdr = cntEmailAdr;
    }

    /**
     * @return Returns the cntFaxNumFull.
     */
    public String getCntFaxNumFull() {
        return cntFaxNumFull;
    }

    /**
     * @param cntFaxNumFull
     *            The cntFaxNumFull to set.
     */
    public void setCntFaxNumFull(String cntFaxNumFull) {
        this.cntFaxNumFull = cntFaxNumFull;
    }

    /**
     * @return Returns the cntFirstName.
     */
    public String getCntFirstName() {
        return cntFirstName;
    }

    /**
     * @param cntFirstName
     *            The cntFirstName to set.
     */
    public void setCntFirstName(String cntFirstName) {
        this.cntFirstName = cntFirstName;
    }

    /**
     * @return Returns the cntLastName.
     */
    public String getCntLastName() {
        return cntLastName;
    }

    /**
     * @param cntLastName
     *            The cntLastName to set.
     */
    public void setCntLastName(String cntLastName) {
        this.cntLastName = cntLastName;
    }

    /**
     * @return Returns the cntPhoneNumFull.
     */
    public String getCntPhoneNumFull() {
        return cntPhoneNumFull;
    }

    /**
     * @param cntPhoneNumFull
     *            The cntPhoneNumFull to set.
     */
    public void setCntPhoneNumFull(String cntPhoneNumFull) {
        this.cntPhoneNumFull = cntPhoneNumFull;
    }

    /**
     * @return Returns the fullfillmentSrc.
     */
    public String getFullfillmentSrc() {
        return fullfillmentSrc;
    }

    /**
     * @param fullfillmentSrc
     *            The fullfillmentSrc to set.
     */
    public void setFullfillmentSrc(String fullfillmentSrc) {
        this.fullfillmentSrc = fullfillmentSrc;
    }

    /**
     * @return Returns the partnerAccess.
     */
    public String getPartnerAccess() {
        return partnerAccess;
    }
    /**
     * @param partnerAccess The partnerAccess to set.
     */
    public void setPartnerAccess(String partnerAccess) {
        this.partnerAccess = partnerAccess;
    }
    
    public String getDistributorToBeDetermined() {
        return distributorToBeDetermined;
    }
    
    public void setDistributorToBeDetermined(String distributorToBeDetermined) {
        this.distributorToBeDetermined = distributorToBeDetermined;
    }
    
    public String getResellerToBeDetermined() {
        return resellerToBeDetermined;
    }
    
    public void setResellerToBeDetermined(String resellerToBeDetermined) {
        this.resellerToBeDetermined = resellerToBeDetermined;
    }
}
