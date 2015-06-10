package com.ibm.dsw.quote.configurator.domain;

class ConfiguratorDomainAdapter extends DomainAdapter {
	private ConfiguratorPart part;
	
	public ConfiguratorDomainAdapter(ConfiguratorPart part){
		this.part = part;
	}
	public String getPartNum() {
		return part.getPartNum();
	}

	public int getRefDocLineItemNum() {
		return part.getRefDocLineNum();
	}
}
