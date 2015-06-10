package com.ibm.dsw.quote.configurator.config;

public interface ConfiguratorParamKeys {
	String paramIDPreFix = "ID_";
	
	String CONFIGURATOR_HEADER = "CONFIGURATOR_HEADER";
	
	
	String term = "term";
	String AddOnTradeUpFlag = "AddOnTradeUpFlag";
	
	String qtySuffix = "_qtySuffix";
	String partNumSuffix = "_partNumSuffix";
	String qtyCheckBoxSuffix = "_qtyCheckBoxSuffix";
	String billingFrequencySuffix = "_billingFrequencySuffix";
	String rampUpPeriodSuffix = "_rampUpPeriodSuffix";

    String rampUpPeriodHiddenSuffix = "_rampUpPeriodHiddenSuffix";
	String termSuffix = "_termSuffix";
	
	String tierQtyMeasreSuffix = "_tierQtyMeasreSuffixSuffix";

	String rampUpQtySuffix = "_rampUpQtySuffix";
	String rampUpDurationSuffix = "_rampUpDurationSuffix";
	
	String OfferingCode = "OfferingCode";
	String ReferenceNum = "ReferenceNum";
	String Region = "Region";
	String CurrencyCode = "CurrencyCode";
	String BandLevel = "BandLevel";
	String Lob = "Lob";
	String CTFlag = "CTFlag";
	String operationType = "operationType";
	String configId = "configId";
	String orgConfigId = "orgConfigId";
	String chrgAgrmtNum = "chrgAgrmtNum";
	String CANCEL_CONFIGRTN = "CANCEL_CONFIGRTN";
	
	//Below parameters are list parameters, for example: PartNumber[D0J4NLL,D0J4MLL]	
	String PartNumber = "PartNumber"; 
	String BillingFrequency = "BillingFrequency"; 
	String Quantity = "Quantity"; 
	String RampupFlag = "RampupFlag"; 
	String RampupSeqNum = "RampupSeqNum"; 
	String Term = "Term"; 	//include ramp up duration term.
	String avaliableBillingFrequencyOptions = "avaliableBillingFrequencyOptions"; 	//redirection tool will get the available billing frequency options and send to configuration
	String activeOnAgreementFlag = "activeOnAgreementFlag"; 
	String configrtnActionCode = "configrtnActionCode";
	String existingPartNumbersInCA = "existingPartNumbersInCA";	//for setting up active on charge agreement flag for configurator part.
	String origPartNumber = "origPartNumber";
	String origQuantity = "origQuantity"; 
	String overrideFlag = "overrideFlag";
	String overridePilotFlag = "overridePilotFlag";
	String overrideRstrctFlag = "overrideRstrctFlag";

	String calcTerm = "calcTerm";
	String caEndDate = "caEndDate";
	
	String webQuoteNum = "webQuoteNum";
	String searchRestrictedPartList = "searchRestrictedPartList";
	String notFoundRestrictedPartList ="NOT_FOUND_PART_LIST";
	String existedRestriectedPartList ="EXISTED_PART_LIST";
	String neededProcessRestrictedPartList ="NEEDED_ADD_PART_LIST";
	String partList4SkipTermValidation="partListStrForSkipTermValidation";
	
}
