/**
 * 
 */
package com.ibm.dsw.quote.configurator.domain;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.appcache.domain.BillingOption;
import com.ibm.dsw.quote.configurator.config.ConfiguratorConstants;
import com.ibm.dsw.quote.configurator.helper.BillingOptionHelper;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @ClassName: MonthlySwActionConfiguratorPart
 * @author Frank
 * @Description: TODO
 * @date Dec 23, 2013 12:46:13 PM
 *
 */
public abstract class MonthlySwActionConfiguratorPart {
	
	protected static final LogContext logger = LogContextFactory.singleton().getLogContext();
	
	
	protected static final String UPDATED_MONTHLY_SW_FLAG = "1";
	
	protected static final String ADDIONAL_MONTHLY_SW_FLAG = "3";
	
	protected static final String NOCHANGED_MONTHLY_SW_FLAG = "0";
	
	protected static final String SUPPORT_BILLING_OPTION ="1";
	protected String billgUpfrntFlag;
	protected String billgMthlyFlag;
	protected String billgAnlFlag;
	protected String billgQtrlyFlag;
	protected String billgEvtFlag;
	
	public MonthlySwActionConfiguratorPart (MonthlySwConfiguratorPart configuratorPart){
		this.configuratorPart = configuratorPart;
	}
	
	protected MonthlySwConfiguratorPart configuratorPart;
	
	public String getBillgUpfrntFlag() {
		return billgUpfrntFlag;
	}

	public void setBillgUpfrntFlag(String billgUpfrntFlag) {
		this.billgUpfrntFlag = billgUpfrntFlag;
	}

	public String getBillgMthlyFlag() {
		return billgMthlyFlag;
	}

	public void setBillgMthlyFlag(String billgMthlyFlag) {
		this.billgMthlyFlag = billgMthlyFlag;
	}

	public String getBillgAnlFlag() {
		return billgAnlFlag;
	}

	public void setBillgAnlFlag(String billgAnlFlag) {
		this.billgAnlFlag = billgAnlFlag;
	}

	public String getBillgQtrlyFlag() {
		return billgQtrlyFlag;
	}

	public void setBillgQtrlyFlag(String billgQtrlyFlag) {
		this.billgQtrlyFlag = billgQtrlyFlag;
	}

	public String getBillgEvtFlag() {
		return billgEvtFlag;
	}

	public void setBillgEvtFlag(String billgEvtFlag) {
		this.billgEvtFlag = billgEvtFlag;
	}
	
	
	public abstract Integer getPartQty();
	
	public abstract boolean isUpdatedMonthly();
	
	public abstract boolean isAddionalMonthly();
	
	public abstract boolean isNoChangedMonthly();
	
	public abstract Integer getRefDocLineNum();
	public abstract void setRefDocLineNum(Integer refDocLineNum);

	public abstract Integer getRelatedCotermLineItmNum();

	public abstract void setRelatedCotermLineItmNum(Integer relatedCotermLineItmNum);

	public abstract Date getEndDate();

	public abstract void setEndDate(Date endDate);

	public abstract Date getRenewalEndDate();

	public abstract void setRenewalEndDate(Date renewalEndDate);

	public abstract Date getNextRenwlDate();

	public abstract void setNextRenwlDate(Date nextRenwlDate);

	public abstract String getRenwlMdlCode();
	
	public abstract void setRenwlMdlCode(String renwlMdlCode);

	public abstract Integer getRenwlTermMths();

	public abstract void setRenwlTermMths(Integer renwlTermMths);

	public abstract String getSectionFlag() ;

	public abstract void setMonthlySwSectionFlag(String monthlySwSectionFlag);
	
	public abstract String getSapBillgFrqncyOptCode();

	public abstract void setSapBillgFrqncyOptCode(String sapBillgFrqncyOptCode) ;
	
	public abstract Double getLocalExtndPrice();

	public abstract void setLocalExtndPrice(Double localExtndPrice);

	public abstract Double getTotCmmtmtVal() ;

	public abstract void setTotCmmtmtVal(Double totCmmtmtVal);

	public abstract Double getLocalOvrageAmt() ;

	public abstract void setLocalOvrageAmt(Double localOvrageAmt);
	
	public abstract boolean isSupportRampUpPart();
	
	public abstract boolean isUpdateFromCA();
	
	public List<BillingOption> getBillingOptions() {
		List<BillingOption> billingOptions = new ArrayList<BillingOption>();

		BillingOptionHelper billingOptionHelp = BillingOptionHelper.singleton();

		try {

			if (SUPPORT_BILLING_OPTION.equals(billgUpfrntFlag)) {
				billingOptions
						.add(billingOptionHelp
								.getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_UPFRONT));
			}  
			if (SUPPORT_BILLING_OPTION.equals(billgMthlyFlag)) {
				billingOptions
						.add(billingOptionHelp
								.getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_MONTHLY));
			}  
			if (SUPPORT_BILLING_OPTION.equals(billgAnlFlag)) {
				billingOptions
						.add(billingOptionHelp
								.getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_ANNUAL));
			}  
			if (SUPPORT_BILLING_OPTION.equals(billgQtrlyFlag)) {
				billingOptions
						.add(billingOptionHelp
								.getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_QUARTERLY));
			} 
			if (SUPPORT_BILLING_OPTION.equals(billgEvtFlag)) {
				billingOptionHelp
						.getBillingOptionByCode(ConfiguratorConstants.BILLING_FREQUENCY_EVENT);
			}

		} catch (Exception e) {

			logger.error(this, "Can't set billing option");
		}

		return billingOptions;
	}
	
	
}
