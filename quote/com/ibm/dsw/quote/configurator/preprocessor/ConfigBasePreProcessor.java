package com.ibm.dsw.quote.configurator.preprocessor;

import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;

public abstract class ConfigBasePreProcessor implements ConfigPreProcessor{
	protected ConfigBasePreProcessor parentProcess = null;
	
	public ConfigBasePreProcessor(){
	}
	
	public ConfigBasePreProcessor(ConfigBasePreProcessor parentProcess){
		this.parentProcess = parentProcess;
	}
	
	public final void doPreProcess(RedirectConfiguratorDataBasePack dataPack){
		if(this.parentProcess!=null){
			this.parentProcess.doPreProcess(dataPack);
		}
		this.preProcessImp(dataPack);
	}

	
	abstract public void preProcessImp(RedirectConfiguratorDataBasePack dataPack);

}
