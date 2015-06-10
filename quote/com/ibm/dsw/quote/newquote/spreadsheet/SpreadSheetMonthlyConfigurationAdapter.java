package com.ibm.dsw.quote.newquote.spreadsheet;


import java.sql.Date;

import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;

/**
 * this class is responsible to adapt the Monthly Licensing configuration 
 * object to  PartsPricingConfiguration type.
 * @author lirui
 *
 */
public class SpreadSheetMonthlyConfigurationAdapter extends AbsSpreadSheetQuoteConfigrnAdapter implements
		ISpreadSheetQuoteConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MonthlySoftwareConfiguration target;
	
	public SpreadSheetMonthlyConfigurationAdapter(MonthlySoftwareConfiguration target){
		this.target = target;
	}
	
	public String getConfigrtnId() {
		return target.getConfigrtnId();
	}

	public String getConfigrtnErrCode() {
		return "";//target.getConfigrtnErrCode();
	}

	public String getConfigrtrConfigrtnId() {
		return target.getConfigrtrConfigrtnId();
	}

	public Date getEndDate() {
		return target.getEndDate();
	}

	public String getCotermConfigrtnId() {
		return target.getCotermConfigrtnId();
	}

	public boolean isConfigrtnOvrrdn() {
		return target.isConfigrtnOvrrdn();
	}

	public String getConfigrtnActionCode() {
		return target.getConfigrtnActionCode();
	}

	public String getProvisioningId() {
		return "";//target.getProvisioningId();
	}

	public String getIbmProdId() {
		return "";//target.getIbmProdId();
	}

	public String getIbmProdIdDscr() {
		return "Brand:"+target.getAllBrandDesc();//target.getIbmProdIdDscr();
	}

}
