package com.ibm.dsw.quote.customer.action;

import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.customer.contract.PrepareConfiguratorRedirectDataBaseContract;
import com.ibm.dsw.quote.customerlist.domain.RedirectConfiguratorDataBasePack;
import com.ibm.ead4j.jade.bean.ResultBeanException;

public class PrepareConfiguratorRedirectDataCreateNewAction extends PrepareConfiguratorRedirectDataBaseAction {

	protected void assembleDataPack(PrepareConfiguratorRedirectDataBaseContract ct, RedirectConfiguratorDataBasePack dataPack) throws QuoteException,
    	ResultBeanException{
		dataPack.setAddTradeFlag(CustomerConstants.CONFIGURATOR_ADDON_TRADEUP_FLAG_0);
		dataPack.setSourceType(CustomerConstants.CONFIGURATOR_SOURCE_TYPE_BRANDNEW);
		
		if(ct.getChrgAgrmtNum()!=null && ct.getChrgAgrmtNum().trim().length()>0){
			if(ct.getCTFlag()!=null && ct.getCTFlag().trim().equals(CustomerConstants.CONFIGURATOR_CT_FLAG_1)){
//				this.addReturnedParams(dataPack, CustomerParamKeys.PARAM_RETURN_TO_SQO_CONFIGRTNACTION, CustomerConstants.CONFIGURATOR_CONFIGRTN_ACTION_NewCACt);
				dataPack.setConfigrtnActionCode(CustomerConstants.CONFIGURATOR_CONFIGRTN_ACTION_NewCACt);
			}else{
//				this.addReturnedParams(dataPack, CustomerParamKeys.PARAM_RETURN_TO_SQO_CONFIGRTNACTION, CustomerConstants.CONFIGURATOR_CONFIGRTN_ACTION_NewCANCt);
				dataPack.setConfigrtnActionCode(CustomerConstants.CONFIGURATOR_CONFIGRTN_ACTION_NewCANCt);
			}
		}else{
//			this.addReturnedParams(dataPack, CustomerParamKeys.PARAM_RETURN_TO_SQO_CONFIGRTNACTION, CustomerConstants.CONFIGURATOR_CONFIGRTN_ACTION_NewNCt);
			dataPack.setConfigrtnActionCode(CustomerConstants.CONFIGURATOR_CONFIGRTN_ACTION_NewNCt);
		}
		
		dataPack.setCtFlag(ct.getCTFlag());
	}
}
