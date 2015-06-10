package com.ibm.dsw.quote.configurator.preprocessor;

import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;

public class EmptyPreProcessor extends ConfigBasePreProcessor {

	public EmptyPreProcessor(){
	}
	
	public EmptyPreProcessor(ConfigBasePreProcessor parentProcess){
		this.parentProcess = parentProcess;
	}
	
	@Override
	public void preProcessImp(RedirectConfiguratorDataBasePack dataPack) {
		
	}

}
