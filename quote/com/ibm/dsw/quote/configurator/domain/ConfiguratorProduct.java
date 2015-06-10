package com.ibm.dsw.quote.configurator.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ConfiguratorProduct implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1543710434007309035L;
	private String wwideProdCodeDscr;
	private String wwideProdCode;
	private transient List<ConfiguratorPartGroup> partGroups;
	private transient Map<String,ConfiguratorPartGroup> partGroupMap;

    public ConfiguratorProduct() {
		partGroupMap = new HashMap();
		partGroups = new ArrayList();
	}
	
	public String getWwideProdCode() {
		return wwideProdCode;
	}
	public void setWwideProdCode(String wwideProdCode) {
		this.wwideProdCode = wwideProdCode;
	}
	public String getWwideProdCodeDscr() {
		return wwideProdCodeDscr;
	}
	public void setWwideProdCodeDscr(String wwideProdCodeDscr) {
		this.wwideProdCodeDscr = wwideProdCodeDscr;
	}
	
	public List<ConfiguratorPartGroup> getPartGroups() {
		return partGroups;
	}

    /**
     * Getter for partGroupMap.
     * 
     * @return the partGroupMap
     */
    public Map<String, ConfiguratorPartGroup> getPartGroupMap() {
        return this.partGroupMap;
    }

    public void setPartGroups(List<ConfiguratorPartGroup> partGroups) {
		this.partGroups = partGroups;
	}
	public boolean hasPartGroup(String cpGroupCode){
		if(partGroupMap.get(cpGroupCode)!=null)
			return true;
		return false;
	}
	public ConfiguratorPartGroup getPartGroup(String cpGroupCode){
		return partGroupMap.get(cpGroupCode);
	}
	public void addPartGroup(ConfiguratorPartGroup cpg){
		partGroupMap.put(cpg.getSapMatlTypeCodeGroupCode(), cpg);
		partGroups.add(cpg);
	}
}
