/**
 * 
 */
package com.ibm.dsw.quote.configurator.domain;

import java.sql.Date;


/**
 * @ClassName: NewCAConfiguratorPart
 * @author Frank
 * @Description: TODO
 * @date Dec 23, 2013 12:49:13 PM
 *
 */
public class MonthlySwNewCAConfiguratorPart extends MonthlySwActionConfiguratorPart {
	
	public MonthlySwNewCAConfiguratorPart(MonthlySwConfiguratorPart configuratorPart){
		super(configuratorPart);
	}
	
	private int rampUpPeriod;
	


	public int getRampUpPeriod() {
		return rampUpPeriod;
	}

	public void setRampUpPeriod(int rampUpPeriod) {
		this.rampUpPeriod = rampUpPeriod;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#getPartQty()
	 */
	@Override
	public Integer getPartQty() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#isUpdatedMonthly()
	 */
	@Override
	public boolean isUpdatedMonthly() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#isAddionalMonthly()
	 */
	@Override
	public boolean isAddionalMonthly() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#isNoChangedMonthly()
	 */
	@Override
	public boolean isNoChangedMonthly() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#getRefDocLineNum()
	 */
	@Override
	public Integer getRefDocLineNum() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#setRefDocLineNum(java.lang.Integer)
	 */
	@Override
	public void setRefDocLineNum(Integer refDocLineNum) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#getRelatedCotermLineItmNum()
	 */
	@Override
	public Integer getRelatedCotermLineItmNum() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#setRelatedCotermLineItmNum(java.lang.Integer)
	 */
	@Override
	public void setRelatedCotermLineItmNum(Integer relatedCotermLineItmNum) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#getEndDate()
	 */
	@Override
	public Date getEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#setEndDate(java.sql.Date)
	 */
	@Override
	public void setEndDate(Date endDate) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#getRenewalEndDate()
	 */
	@Override
	public Date getRenewalEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#setRenewalEndDate(java.sql.Date)
	 */
	@Override
	public void setRenewalEndDate(Date renewalEndDate) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#getNextRenwlDate()
	 */
	@Override
	public Date getNextRenwlDate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#setNextRenwlDate(java.sql.Date)
	 */
	@Override
	public void setNextRenwlDate(Date nextRenwlDate) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#getRenwlMdlCode()
	 */
	@Override
	public String getRenwlMdlCode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#setRenwlMdlCode(java.lang.String)
	 */
	@Override
	public void setRenwlMdlCode(String renwlMdlCode) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#getRenwlTermMths()
	 */
	@Override
	public Integer getRenwlTermMths() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#setRenwlTermMths(java.lang.Integer)
	 */
	@Override
	public void setRenwlTermMths(Integer renwlTermMths) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#getSectionFlag()
	 */
	@Override
	public String getSectionFlag() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#setMonthlySwSectionFlag(java.lang.String)
	 */
	@Override
	public void setMonthlySwSectionFlag(String monthlySwSectionFlag) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#getSapBillgFrqncyOptCode()
	 */
	@Override
	public String getSapBillgFrqncyOptCode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#setSapBillgFrqncyOptCode(java.lang.String)
	 */
	@Override
	public void setSapBillgFrqncyOptCode(String sapBillgFrqncyOptCode) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#getLocalExtndPrice()
	 */
	@Override
	public Double getLocalExtndPrice() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#setLocalExtndPrice(java.lang.Double)
	 */
	@Override
	public void setLocalExtndPrice(Double localExtndPrice) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#getTotCmmtmtVal()
	 */
	@Override
	public Double getTotCmmtmtVal() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#setTotCmmtmtVal(java.lang.Double)
	 */
	@Override
	public void setTotCmmtmtVal(Double totCmmtmtVal) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#getLocalOvrageAmt()
	 */
	@Override
	public Double getLocalOvrageAmt() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#setLocalOvrageAmt(java.lang.Double)
	 */
	@Override
	public void setLocalOvrageAmt(Double localOvrageAmt) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#isSupportRampUpPart()
	 */
	@Override
	public boolean isSupportRampUpPart() {
		return Boolean.TRUE;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.domain.MonthlySwActionConfiguratorPart#isOnlySupportUpdateQty()
	 */
	@Override
	public boolean isUpdateFromCA() {
		// TODO Auto-generated method stub
		return Boolean.FALSE;
	}


}
