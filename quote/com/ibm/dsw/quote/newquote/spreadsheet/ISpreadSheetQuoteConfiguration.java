package com.ibm.dsw.quote.newquote.spreadsheet;


import java.sql.Date;

import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;

public interface ISpreadSheetQuoteConfiguration extends PartsPricingConfiguration{
	public String getConfigrtnId();
	public String getConfigrtnErrCode();
	public String getConfigrtrConfigrtnId();
	public Date getEndDate();
	public String getCotermConfigrtnId();
	public boolean isConfigrtnOvrrdn();
	public String getConfigrtnActionCode();
	public String getProvisioningId();

	public String getIbmProdId();
	public String getIbmProdIdDscr();
}
