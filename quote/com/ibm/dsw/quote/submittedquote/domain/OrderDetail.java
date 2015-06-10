package com.ibm.dsw.quote.submittedquote.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>OrderDetail<code> class.
 * 
 * @author: zzlili@cn.ibm.com
 * 
 *          Creation date: 2012-12-17
 */

public class OrderDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2342308810548015268L;
	private String orderLineItemSeq;
	private String machineType;
	private String model;
	private String sevialNum;
	private String orderStatus;
	private Date ccadDate;
	private List<String> sapDocUserStatusList;

	/**
     * 
     */
	public OrderDetail() {
	}

	public String getOrderLineItemSeq() {
		return orderLineItemSeq;
	}

	public void setOrderLineItemSeq(String orderLineItemSeq) {
		this.orderLineItemSeq = orderLineItemSeq;
	}

	public String getMachineType() {
		return StringUtils.trimToEmpty(machineType);
	}

	public void setMachineType(String machineType) {
		this.machineType = machineType;
	}

	public String getModel() {
		return StringUtils.trimToEmpty(model);
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSevialNum() {
		return StringUtils.trimToEmpty(sevialNum);
	}

	public void setSevialNum(String sevialNum) {
		this.sevialNum = sevialNum;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Date getCcadDate() {
		return ccadDate;
	}

	public void setCcadDate(Date ccadDate) {
		this.ccadDate = ccadDate;
	}

	public List<String> getSapDocUserStatusList() {
		return sapDocUserStatusList;
	}

	public void setSapDocUserStatusList(List<String> sapDocUserStatusList) {
		this.sapDocUserStatusList = sapDocUserStatusList;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("orderLineItemSeq = ").append(orderLineItemSeq)
				.append("\n");
		buffer.append("machineType = ").append(machineType).append("\n");
		buffer.append("model = ").append(model).append("\n");
		buffer.append("sevialNum = ").append(sevialNum).append("\n");
		buffer.append("orderStatus = ").append(orderStatus).append("\n");
		buffer.append("ccadDate = ").append(ccadDate).append("\n");
		buffer.append("sapDocUserStatusList = ").append(sapDocUserStatusList)
				.append("\n");
		return buffer.toString();
	}
}
