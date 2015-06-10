package com.ibm.dsw.quote.newquote.spreadsheet;


import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.common.domain.ServiceDateModType;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * this class is responsible to adapt the SaaS configuration object. 
 * @author lirui
 *
 */
public abstract class AbsSpreadSheetQuoteConfigrnAdapter implements
		ISpreadSheetQuoteConfiguration {

//=============

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void delete() throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getWebQuoteNum() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWebQuoteNum(String webQuoteNum) throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConfigrtnId(String configrtnId) throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConfigrtrConfigrtnId(String configrtrConfigrtnId)
			throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer getProvisngDays() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProvisngDays(Integer provisngDays) throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUserID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUserID(String userID) throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setConfigrtnActionCode(String configrtnActionCode)
			throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getChargeAgreementList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setChargeAgreementList(List<String> chargeAgreementList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEndDate(Date endDate) throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCotermConfigrtnId(String cotermConfigrtnId)
			throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, List> getCotermMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCotermMap(Map<String, List> cotermMap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAddOnTradeUp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNewNoCaNoCoterm() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNewCaCoterm() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNewCaNoCoterm() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Date getConfigrtnModDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConfigrtnModDate(Date configrtnModDate)
			throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer getProvisngDaysDefault() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getIncreaseBidTCV() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIncreaseBidTCV(Double increaseBidTCV) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Double getUnusedBidTCV() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUnusedBidTCV(Double unusedBidTCV) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAllowOvrrd() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setProvisioningId(String provisioningId) throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isProvisioningCopied() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getProdBrandCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProdBrandCodeDscr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceDateModType getServiceDateModType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setServiceDateModType(ServiceDateModType serviceDateModType)
			throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Date getServiceDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setServiceDate(Date serviceDate) throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isTermExtension() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTermExtension(boolean termExtension) throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConfigEntireExtended() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setConfigEntireExtended(boolean configEntireExtended)
			throws TopazException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	
}
