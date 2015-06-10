/**
 * SpecialBidWorkflow.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * cf160638.12 v101006191000
 */

package com.ibm.dsw.quote.mail.process;

public class SpecialBidWorkflow  {
    private com.ibm.dsw.quote.mail.process.Approvers[] approvers;
    private java.lang.String humanTaskID;
    private com.ibm.dsw.quote.mail.process.SpecialBidStatus specialBidStatus;
    private java.lang.String comments;
    private com.ibm.dsw.quote.mail.process.SpecialBid specialBid;

    public SpecialBidWorkflow() {
    }

    public com.ibm.dsw.quote.mail.process.Approvers[] getApprovers() {
        return approvers;
    }

    public void setApprovers(com.ibm.dsw.quote.mail.process.Approvers[] approvers) {
        this.approvers = approvers;
    }

    public com.ibm.dsw.quote.mail.process.Approvers getApprovers(int i) {
        return this.approvers[i];
    }

    public void setApprovers(int i, com.ibm.dsw.quote.mail.process.Approvers value) {
        this.approvers[i] = value;
    }

    public java.lang.String getHumanTaskID() {
        return humanTaskID;
    }

    public void setHumanTaskID(java.lang.String humanTaskID) {
        this.humanTaskID = humanTaskID;
    }

    public com.ibm.dsw.quote.mail.process.SpecialBidStatus getSpecialBidStatus() {
        return specialBidStatus;
    }

    public void setSpecialBidStatus(com.ibm.dsw.quote.mail.process.SpecialBidStatus specialBidStatus) {
        this.specialBidStatus = specialBidStatus;
    }

    public java.lang.String getComments() {
        return comments;
    }

    public void setComments(java.lang.String comments) {
        this.comments = comments;
    }

    public com.ibm.dsw.quote.mail.process.SpecialBid getSpecialBid() {
        return specialBid;
    }

    public void setSpecialBid(com.ibm.dsw.quote.mail.process.SpecialBid specialBid) {
        this.specialBid = specialBid;
    }

}
