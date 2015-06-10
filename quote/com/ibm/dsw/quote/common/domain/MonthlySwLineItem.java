package com.ibm.dsw.quote.common.domain;

import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * 
 * @ClassName: MonthlySwLineItem
 * @author Frank
 * @Description: Get Monthly software information
 * @date Dec 12, 2013 5:05:57 PM
 *
 */
public interface MonthlySwLineItem extends QuoteLineItem {
	
	public boolean isMonthlySwPart();
	
	public boolean isMonthlySwSubscrptnPart();
	
	public boolean isMonthlySwDailyPart();
	
	public boolean isMonthlySwSubscrptnOvragePart();
	
	public boolean isMonthlySwOnDemandPart();
	
	public boolean isHasRamupPart();
	
	public void setHasRamupPart(boolean hasRamupPart) throws TopazException;
	
	public boolean isUpdateSectionFlag();
	
	public void setUpdateSectionFlag(boolean updateSectionFlag) throws TopazException;
	
	public boolean isAdditionSectionFalg();
	
    public void setAdditionSectionFlag(boolean additionSctionFlag) throws TopazException;

	public boolean isBeRampuped();
	
	public String getPartKey();
	
    public void setMonthlySwSubscrptnPart(boolean monthlySwSubscrptnPart) throws TopazException;

	public void setMonthlySwSubscrptnOvragePart(boolean monthlySwSubscrptnOvragePart) throws TopazException;

	public void setMonthlySwTcvAcv(boolean monthlySwTcvAcv) throws TopazException;

    public void setMonthlySwPart(boolean monthlySwPart) throws TopazException;

    public void setMonthlySwDailyPart(boolean monthlySwDailyPart) throws TopazException;

	public void setMonthlySwOnDemandPart(boolean monthlySwOnDemandPart) throws TopazException;

	public boolean isRampUpIndicator4QuoteCreateService();

	public void setRampUpIndicator4QuoteCreateService(boolean isRampUpIndicator4QuoteCreateService);

    public static final int NO_MONTHLY_SW_PART = 0;
    public static final int MONTHLY_SW_SUBSCRIPTION_PART = 1;
    public static final int MONTHLY_SW_OVERAGE_PART = 2;
    public static final int MONTHLY_SW_DAILY_PART = 3;
    public static final int MONTHLY_SW_ON_DEMAND_PART = 4;

}
