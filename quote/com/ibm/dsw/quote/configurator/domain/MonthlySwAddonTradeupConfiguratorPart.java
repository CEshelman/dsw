/**
 * 
 */
package com.ibm.dsw.quote.configurator.domain;

import java.sql.Date;

/**
 * @ClassName: AddonTradeupConfiguratorPart
 * @author Frank
 * @Description: TODO
 * @date Dec 23, 2013 12:50:02 PM
 *
 */
public class MonthlySwAddonTradeupConfiguratorPart extends
		MonthlySwActionConfiguratorPart {
	
	
	/**
	 * @param configuratorPart
	 */
	public MonthlySwAddonTradeupConfiguratorPart(
			MonthlySwConfiguratorPart configuratorPart) {
		super(configuratorPart);
	}

	private Integer refDocLineNum;
	private Integer relatedCotermLineItmNum;
	
	private Date endDate;
	private Date renewalEndDate;
	
	private Date nextRenwlDate;
	
	private Integer partQty;
	
	private String renwlMdlCode;
	
	private Integer renwlTermMths;
	
	private String monthlySwSectionFlag;
	
	private String sapBillgFrqncyOptCode;
	
	private Double localExtndPrice;
	
	private Double totCmmtmtVal;
	
	private Double localOvrageAmt;
	
	private int rampUpPeriod;
	
	private Integer term;
	
	public int getRampUpPeriod() {
		return rampUpPeriod;
	}

	public void setRampUpPeriod(int rampUpPeriod) {
		this.rampUpPeriod = rampUpPeriod;
	}

	public Integer getRefDocLineNum() {
		return refDocLineNum;
	}

	public void setRefDocLineNum(Integer refDocLineNum) {
		this.refDocLineNum = refDocLineNum;
	}

	public Integer getRelatedCotermLineItmNum() {
		return relatedCotermLineItmNum;
	}

	public void setRelatedCotermLineItmNum(Integer relatedCotermLineItmNum) {
		this.relatedCotermLineItmNum = relatedCotermLineItmNum;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getRenewalEndDate() {
		return renewalEndDate;
	}

	public void setRenewalEndDate(Date renewalEndDate) {
		this.renewalEndDate = renewalEndDate;
	}

	public Date getNextRenwlDate() {
		return nextRenwlDate;
	}

	public void setNextRenwlDate(Date nextRenwlDate) {
		this.nextRenwlDate = nextRenwlDate;
	}

	public Integer getPartQty() {
		return partQty;
	}

	public void setPartQty(Integer partQty) {
		this.partQty = partQty;
	}

	public String getRenwlMdlCode() {
		return renwlMdlCode;
	}

	public void setRenwlMdlCode(String renwlMdlCode) {
		this.renwlMdlCode = renwlMdlCode;
	}

	public Integer getRenwlTermMths() {
		return renwlTermMths;
	}

	public void setRenwlTermMths(Integer renwlTermMths) {
		this.renwlTermMths = renwlTermMths;
	}

	public String getSectionFlag() {
		return monthlySwSectionFlag;
	}

	public void setMonthlySwSectionFlag(String monthlySwSectionFlag) {
		this.monthlySwSectionFlag = monthlySwSectionFlag;
	}
	
	
	public boolean isUpdatedMonthly(){
		return UPDATED_MONTHLY_SW_FLAG.equals(monthlySwSectionFlag);
	}
	
	public boolean isAddionalMonthly(){
		return ADDIONAL_MONTHLY_SW_FLAG.equals(monthlySwSectionFlag);
	}
	
	public boolean isNoChangedMonthly(){
		return NOCHANGED_MONTHLY_SW_FLAG.equals(monthlySwSectionFlag);
	}

	public String getSapBillgFrqncyOptCode() {
		return sapBillgFrqncyOptCode;
	}
	
	public Double getLocalExtndPrice() {
		return localExtndPrice;
	}

	public void setLocalExtndPrice(Double localExtndPrice) {
		this.localExtndPrice = localExtndPrice;
	}

	public Double getTotCmmtmtVal() {
		return totCmmtmtVal;
	}

	public void setTotCmmtmtVal(Double totCmmtmtVal) {
		this.totCmmtmtVal = totCmmtmtVal;
	}

	public Double getLocalOvrageAmt() {
		return localOvrageAmt;
	}

	public void setLocalOvrageAmt(Double localOvrageAmt) {
		this.localOvrageAmt = localOvrageAmt;
	}

	public void setSapBillgFrqncyOptCode(String sapBillgFrqncyOptCode) {
		this.sapBillgFrqncyOptCode = sapBillgFrqncyOptCode;
	}

	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#isSupportRampUpPart()
	 */
	@Override
	public boolean isSupportRampUpPart() {
		boolean isSupport = Boolean.FALSE ;
		
		if (isAddionalMonthly()){
			isSupport = Boolean.TRUE;
		}
		
		return isSupport;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#isOnlySupportUpdateQty()
	 */
	@Override
	public boolean isUpdateFromCA() {
		boolean isSupport = Boolean.TRUE ;
		
		if (isAddionalMonthly()){
			isSupport = Boolean.FALSE;
		}
		return isSupport;
	}
	
	

}
