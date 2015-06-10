package com.ibm.dsw.quote.configurator.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConfiguratorPartGroup implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5879656484216389006L;
	private String sapMatlTypeCodeGroupDscr;
	private String sapMatlTypeCodeGroupCode;

	public String getSapMatlTypeCodeGroupDscr() {
		return sapMatlTypeCodeGroupDscr;
	}

	public void setSapMatlTypeCodeGroupDscr(String sapMatlTypeCodeGroupDscr) {
		this.sapMatlTypeCodeGroupDscr = sapMatlTypeCodeGroupDscr;
	}

	private transient List<ConfiguratorPart> partList;


	public String getSapMatlTypeCodeGroupCode() {
		return sapMatlTypeCodeGroupCode;
	}

	public void setSapMatlTypeCodeGroupCode(String sapMatlTypeCodeGroupCode) {
		this.sapMatlTypeCodeGroupCode = sapMatlTypeCodeGroupCode;
	}

	public String getSapMatlTypeCodeDscr() {
		return sapMatlTypeCodeGroupDscr;
	}

	public void setSapMatlTypeCodeDscr(String sapMatlTypeCodeDscr) {
		this.sapMatlTypeCodeGroupDscr = sapMatlTypeCodeDscr;
	}

	public List<ConfiguratorPart> getPartList() {
		return partList;
	}

	public void setPartList(List<ConfiguratorPart> partList) {
		this.partList = partList;
	}

	public void addConfiguratorPart(ConfiguratorPart cp) {
		if (partList == null)
			partList = new ArrayList();
		partList.add(cp);

	}
}
