/**
 * 
 */
package com.ibm.dsw.quote.configurator.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MonthlySwProduct
 * @author Frank
 * @Description: TODO
 * @date Dec 25, 2013 3:27:25 PM
 *
 */
public class MonthlySwConfiguratorProduct implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String pid;
	
	private String pidDesc;
	
	private String bid;
	
	private String bidDesc;
	
	
	private List<MonthlySwSubscrptnConfiguratorPart> subscrptnParts;
	
	private List<MonthlySwOnDemandConfiguratorPart> onDemandParts;
	
	public MonthlySwConfiguratorProduct(){
		subscrptnParts = new ArrayList<MonthlySwSubscrptnConfiguratorPart>();
		onDemandParts = new ArrayList<MonthlySwOnDemandConfiguratorPart>();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPidDesc() {
		return pidDesc;
	}

	public void setPidDesc(String pidDesc) {
		this.pidDesc = pidDesc;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getBidDesc() {
		return bidDesc;
	}

	public void setBidDesc(String bidDesc) {
		this.bidDesc = bidDesc;
	}

	public List<MonthlySwSubscrptnConfiguratorPart> getSubscrptnParts() {
		return subscrptnParts;
	}

	public void setSubscrptnParts(
			List<MonthlySwSubscrptnConfiguratorPart> subscrptnParts) {
		this.subscrptnParts = subscrptnParts;
	}

	public List<MonthlySwOnDemandConfiguratorPart> getOnDemandParts() {
		return onDemandParts;
	}

	public void setOnDemandParts(
			List<MonthlySwOnDemandConfiguratorPart> onDemandParts) {
		this.onDemandParts = onDemandParts;
	}
	
	
	public void addSubscrptnPart(MonthlySwConfiguratorPart configuratorPart) {
		if (configuratorPart instanceof MonthlySwSubscrptnConfiguratorPart) {
			subscrptnParts
					.add((MonthlySwSubscrptnConfiguratorPart) configuratorPart);
		}
	}
	
	public void addOnDemandPart(MonthlySwConfiguratorPart configuratorPart) {
		if (configuratorPart instanceof MonthlySwOnDemandConfiguratorPart) {
			onDemandParts
					.add((MonthlySwOnDemandConfiguratorPart) configuratorPart);
		}
	}
	
	
	public boolean hasOnDemandParts(){
		return onDemandParts != null && onDemandParts.size() > 0;
	}
	
	public boolean hasSubscrptnParts(){
		return subscrptnParts != null && subscrptnParts.size() > 0;
	}

}
