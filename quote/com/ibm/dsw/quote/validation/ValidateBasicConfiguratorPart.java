package com.ibm.dsw.quote.validation;

import java.util.HashMap;
import java.util.Map;

import com.ibm.dsw.common.validator.FieldResult;
import com.ibm.dsw.common.validator.ValidatorMessageKeys;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.configurator.contract.AddOrUpdateConfigurationContract;

public class ValidateBasicConfiguratorPart extends ValidateConfiguratorPart {

	@Override
	protected void processErrorMsg(String msg, String arg, String key, AddOrUpdateConfigurationContract contract) {
		HashMap map = new HashMap();
		String boundle = MessageKeys.BUNDLE_APPL_I18N_CONFIGURATOR;
		FieldResult fieldResult = new FieldResult();

		fieldResult.setMsg(boundle, msg);
		fieldResult.addArg(boundle, arg);

		map.put(key, fieldResult);

		addToValidationDataMap(contract, map);

	}

	@Override
	protected void processErrorMsg(String msg, String[] args, boolean[] isArgsResource, String key,
			AddOrUpdateConfigurationContract contract) {
		HashMap map = new HashMap();
		String boundle = MessageKeys.BUNDLE_APPL_I18N_CONFIGURATOR;
		FieldResult fieldResult = new FieldResult();

		fieldResult.setMsg(boundle, msg);

		for (int i = 0; i < args.length; i++) {
			fieldResult.addArg(boundle, args[i], isArgsResource[i]);
		}

		map.put(key, fieldResult);

		addToValidationDataMap(contract, map);

	}

	private void addToValidationDataMap(AddOrUpdateConfigurationContract contract, HashMap vMap) {
		HashMap dataMap = contract.getValidationDataMap();
		if (dataMap == null)
			dataMap = new HashMap();
		Map xMap = vMap;
		if(dataMap.containsKey(ValidatorMessageKeys.VALIDATION_MANUALLY_KEY)){
			xMap = (Map) dataMap
					.get(ValidatorMessageKeys.VALIDATION_MANUALLY_KEY);
			xMap.putAll(vMap);
		}
		dataMap.put(ValidatorMessageKeys.VALIDATION_MANUALLY_KEY, xMap);
		contract.setValidationDataMap(dataMap);
	}

}
