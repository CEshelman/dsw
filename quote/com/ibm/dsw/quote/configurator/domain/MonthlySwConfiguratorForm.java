/**
 * 
 */
package com.ibm.dsw.quote.configurator.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MonthlySwConfiguratorForm
 * @author Frank
 * @Description: TODO
 * @date Dec 26, 2013 3:05:04 PM
 *
 */
public class MonthlySwConfiguratorForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MonthlySwConfiguratorForm (){
		configHeader = new ConfiguratorHeader();
		monthlySwProducts = new ArrayList<MonthlySwConfiguratorProduct>();
	}
	
	private ConfiguratorHeader configHeader;
	
	// --------- start new ca section
	
	private transient List<MonthlySwConfiguratorProduct> monthlySwProducts;
	
	
	//--------- end new ca section
	
	
	//---------- start ca section 
	
	private transient List<MonthlySwConfiguratorProduct> updatedMonthlySwProducts;
	
	private transient List<MonthlySwConfiguratorProduct> addionMonthlySwProducts;
	
	private transient List<MonthlySwConfiguratorProduct> noChangedMonthlySwProducts;
	
	
	//--------- end ca section

	public ConfiguratorHeader getConfigHeader() {
		return configHeader;
	}

	public void setConfigHeader(ConfiguratorHeader configHeader) {
		this.configHeader = configHeader;
	}

	public List<MonthlySwConfiguratorProduct> getMonthlySwProducts() {
		return monthlySwProducts;
	}

	public void setMonthlySwProducts(
			List<MonthlySwConfiguratorProduct> monthlySwProducts) {
		this.monthlySwProducts = monthlySwProducts;
	}

	public List<MonthlySwConfiguratorProduct> getUpdatedMonthlySwProducts() {
		return updatedMonthlySwProducts;
	}

	public void setUpdatedMonthlySwProducts(
			List<MonthlySwConfiguratorProduct> updatedMonthlySwProducts) {
		this.updatedMonthlySwProducts = updatedMonthlySwProducts;
	}

	public List<MonthlySwConfiguratorProduct> getAddionMonthlySwProducts() {
		return addionMonthlySwProducts;
	}

	public void setAddionMonthlySwProducts(
			List<MonthlySwConfiguratorProduct> addionMonthlySwProducts) {
		this.addionMonthlySwProducts = addionMonthlySwProducts;
	}

	public List<MonthlySwConfiguratorProduct> getNoChangedMonthlySwProducts() {
		return noChangedMonthlySwProducts;
	}

	public void setNoChangedMonthlySwProducts(
			List<MonthlySwConfiguratorProduct> noChangedMonthlySwProducts) {
		this.noChangedMonthlySwProducts = noChangedMonthlySwProducts;
	}

}
