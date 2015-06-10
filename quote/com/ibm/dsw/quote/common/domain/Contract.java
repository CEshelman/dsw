package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>Contract<code> class.
 *    
 * @author: zhengmr@cn.ibm.com
 * 
 * Creation date: Mar 1, 2007
 */
public class Contract implements Serializable {

    private String sapContractNum = null;

    private String custNum = null;

    private String sapContractVariantCode = null;

    private String volDiscLevelCode = null;
    
    private String tranPriceLevelCode = null;
    
    private Date ctrctStartDate = null;
    
    private Date ctrctEndDate = null;
    
    private Date anniversaryDate = null;
    
    private int isContractActiveFlag;
    
    private int webCtrctId = -1;
    
    private String authrztnGroup = null;
    
    private String nimOffergCode = null;

    /**
     *  
     */
    public Contract() {
    }

    public Contract(String sapContractNum, String custNum, String sapContractVariantCode, String volDiscLevelCode) {
        this.sapContractNum = sapContractNum;
        this.custNum = custNum;
        this.sapContractVariantCode = sapContractVariantCode;
        this.volDiscLevelCode = volDiscLevelCode;
    }

    /**
     * @return Returns the custNum.
     */
    public String getCustNum() {
        return custNum;
    }

    /**
     * @param custNum
     *            The custNum to set.
     */
    public void setCustNum(String custNum) {
        this.custNum = custNum;
    }

    /**
     * @return Returns the sapContractNum.
     */
    public String getSapContractNum() {
        return sapContractNum;
    }

    /**
     * @param sapContractNum
     *            The sapContractNum to set.
     */
    public void setSapContractNum(String sapContractNum) {
        this.sapContractNum = sapContractNum;
    }

    /**
     * @return Returns the sapContractVariantCode.
     */
    public String getSapContractVariantCode() {
        return sapContractVariantCode;
    }

    /**
     * @param sapContractVariantCode
     *            The sapContractVariantCode to set.
     */
    public void setSapContractVariantCode(String sapContractVariantCode) {
        this.sapContractVariantCode = sapContractVariantCode;
    }

    /**
     * @return Returns the volDiscLevelCode.
     */
    public String getVolDiscLevelCode() {
        return volDiscLevelCode;
    }

    /**
     * @param volDiscLevelCode
     *            The volDiscLevelCode to set.
     */
    public void setVolDiscLevelCode(String volDiscLevelCode) {
        this.volDiscLevelCode = volDiscLevelCode;
    }
    
    /**
     * @return Returns the ctrctEndDate.
     */
    public Date getCtrctEndDate() {
        return ctrctEndDate;
    }

    /**
     * @param ctrctEndDate
     *            The ctrctEndDate to set.
     */
    public void setCtrctEndDate(Date ctrctEndDate) {
        this.ctrctEndDate = ctrctEndDate;
    }

    /**
     * @return Returns the ctrctStartDate.
     */
    public Date getCtrctStartDate() {
        return ctrctStartDate;
    }

    /**
     * @param ctrctStartDate
     *            The ctrctStartDate to set.
     */
    public void setCtrctStartDate(Date ctrctStartDate) {
        this.ctrctStartDate = ctrctStartDate;
    }
    
    /**
     * @return Returns the anniversaryDate.
     */
    public Date getAnniversaryDate() {
        return anniversaryDate;
    }
    
    /**
     * @param anniversaryDate The anniversaryDate to set.
     */
    public void setAnniversaryDate(Date anniversaryDate) {
        this.anniversaryDate = anniversaryDate;
    }
    
    /**
     * @return Returns the isContractActiveFlag.
     */
    public int getIsContractActiveFlag() {
        return isContractActiveFlag;
    }
    /**
     * @param isContractActiveFlag The isContractActiveFlag to set.
     */
    public void setIsContractActiveFlag(int isContractActiveFlag) {
        this.isContractActiveFlag = isContractActiveFlag;
    }
    
    public String getAuthrztnGroup() {
        return authrztnGroup;
    }
    
    public void setAuthrztnGroup(String authrztnGroup) {
        this.authrztnGroup = authrztnGroup;
    }
    
    public int getWebCtrctId() {
        return webCtrctId;
    }
    
    public void setWebCtrctId(int webCtrctId) {
        this.webCtrctId = webCtrctId;
    }
    
    public boolean isWebContract() {
        return webCtrctId > 0;
    }
    
    public String getTranPriceLevelCode() {
        return tranPriceLevelCode;
    }
    
    public void setTranPriceLevelCode(String tranPriceLevelCode) {
        this.tranPriceLevelCode = tranPriceLevelCode;
    }
    
    /**
     * @return Returns the nimOffergCode.
     */
    public String getNimOffergCode() {
        return nimOffergCode;
    }
    /**
     * @param nimOffergCode The nimOffergCode to set.
     */
    public void setNimOffergCode(String nimOffergCode) {
        this.nimOffergCode = nimOffergCode;
    }
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("sapContractNum=").append(sapContractNum).append("\n");
        buffer.append("custNum=").append(custNum).append("\n");
        buffer.append("sapContractVariantCode=").append(sapContractVariantCode).append("\n");
        buffer.append("volDiscLevelCode=").append(volDiscLevelCode).append("\n");
        buffer.append("tranPriceLevelCode=").append(tranPriceLevelCode).append("\n");
        buffer.append("ctrctStartDate=").append(ctrctStartDate).append("\n");
        buffer.append("ctrctEndDate=").append(ctrctEndDate).append("\n");
        buffer.append("anniversaryDate=").append(anniversaryDate).append("\n");
        buffer.append("isContractActiveFlag=").append(isContractActiveFlag).append("\n");
        buffer.append("authrztnGroup=").append(authrztnGroup).append("\n");
        buffer.append("webCtrctId=").append(webCtrctId).append("\n");
        buffer.append("nimOffergCode=").append(nimOffergCode).append("\n");
        return buffer.toString();
    }
}
