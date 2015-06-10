package com.ibm.dsw.quote.configurator.preprocessor;

import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;

public interface ConfigPreProcessor {
	public void doPreProcess(RedirectConfiguratorDataBasePack dataPack);
}
