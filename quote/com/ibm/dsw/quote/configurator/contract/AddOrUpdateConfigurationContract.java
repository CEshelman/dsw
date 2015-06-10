package com.ibm.dsw.quote.configurator.contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorPart;
import com.ibm.dsw.quote.configurator.process.SaasConfiguratorProcess_Impl;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

public class AddOrUpdateConfigurationContract extends ConfiguratorBaseContract{
	
	public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        
        loadForPost(parameters, session);
	}
	
	public List<ConfiguratorPart> getParts() throws QuoteException{
    	List<ConfiguratorPart> list = new ArrayList<ConfiguratorPart>();

    	for(ConfiguratorPart part: map.values()){
    		if(!part.isDeleted()){
    			list.add(part);
    		}
    	}
    	
		Map allPartsFrmPidMap = SaasConfiguratorProcess_Impl.convertConfiguratorPartListToMap(this.allPartsFrmPid);

		SaasConfiguratorProcess_Impl.setSaaSPartAttribute(list,
				allPartsFrmPidMap, this.getPid(), term, true);

    	return list;
    }
}
