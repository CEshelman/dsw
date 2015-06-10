package com.ibm.dsw.quote.common.domain;
import java.io.Serializable;
import java.util.List;
public interface MigrateRequest extends Serializable{
	public int getCoverageTerm();
	public void setCoverageTerm(int coverageTerm);
	public String getBillingFreq();
	public void setBillingFreq(String billingFreq);
	public String getRequestNum();
	public void setRequestNum(String requestNum);
	/**
	 * Only two field has value for the migrate part: partNum, seqNum
	 * @return
	 */
	public List<MigratePart> getParts();
	public void setParts(List<MigratePart> parts);
	
	// customer, partner information
	
	public Customer getCustomer() ;
	public void setCustomer(Customer customer);
	public Partner getPayer();
	public void setPayer(Partner payer) ;
	public Partner getReseller() ;
	public void setReseller(Partner reseller);

	public String getSoldToCustNum();
	public void setSoldToCustNum(String soldToCustNum);
	public String getSapCtrctNum();
	public void setSapCtrctNum(String sapCtrctNum);
	public String getReslCustNum();
	public void setReslCustNum(String reslCustNum) ;
	public String getPayerCustNum();
	public void setPayerCustNum(String payerCustNum) ;
	public String getSapIDocNum();
	public void setSapIDocNum(String sapIDocNum) ;
	public String getSapDistChnl();
	public void setSapDistChnl(String sapDistChnl);
	public String getMigrtnStageCode();
	public void setMigrtnStageCode(String migrtnStageCode) ;
	public String getLob();
	public void setLob(String lob);

	public String getOrginalCANum();
	public void setOrginalCANum(String orginalCANum);

	public String getFulfillmentSrc();
	public void setFulfillmentSrc(String fulfillmentSrc);
	
	public String getAcqCode();
	public void setAcqCode(String acqCode);
	
	public List<MigrationFailureLineItem> getLineItems();
	public void setLineItems(List<MigrationFailureLineItem> lineItems);
	
	// country code of customer
	public String getCountryCode();
	public void setCountryCode(String countryCode);
	

	public String getCurrencyCode();
	public void setCurrencyCode(String currencyCode);
	
	public String getOrignalSapDistChnl();
	public void setOrignalSapDistChnl(String orignalSapDistChnl);	
	
}
