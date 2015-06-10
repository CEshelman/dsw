package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;

import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.util.CheckPersisterUtil;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;
import com.ibm.ead4j.topaz.persistence.jdbc.Persister;

/**
 * 
 * @ClassName: MonthlySwLineItem_jdbc
 * @author Frank
 * @Description: Get Monthly software Info from DB
 * @date Dec 12, 2013 5:04:33 PM
 *
 */
public class MonthlySwLineItem_jdbc extends QuoteLineItem_jdbc implements MonthlySwLineItem, PersistentObject, Serializable {
	
	transient Persister persister;
	

	public MonthlySwLineItem_jdbc(String webQuoteNum , String partNum) {
		super(webQuoteNum,partNum);
		this.persister = new MonthlySoftwarePersister(this);
	}

	private static final long serialVersionUID = 1L;

	public boolean monthlySwPart ;
	
	public boolean monthlySwSubscrptnPart;
	
	public boolean monthlySwSubscrptnOvragePart;
	
	public boolean monthlySwDailyPart;
	
	public boolean monthlySwOnDemandPart;
	
	public boolean hasRamupPart;
	
	public boolean updateSectionFlag;
	
	public boolean additionSectionFlag;
	
	public boolean isRampUpIndicator4QuoteCreateService;

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem#delete()
     */
    public void delete() throws TopazException {
        persister.isDeleted(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        persister.hydrate(connection);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    public void persist(Connection connection) throws TopazException {
        persister.persist(connection);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean deleteState) throws TopazException {
        persister.isDeleted(deleteState);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    public void isNew(boolean newState) throws TopazException {
		super.isNew(newState);
        persister.isNew(newState);
    }

	public boolean isMonthlySwPart() {
		return monthlySwPart;
	}

	public boolean isMonthlySwSubscrptnPart() {
		return monthlySwSubscrptnPart;
	}


	public boolean isMonthlySwSubscrptnOvragePart() {
		return monthlySwSubscrptnOvragePart;
	}



	public boolean isMonthlySwDailyPart() {
		return monthlySwDailyPart;
	}

	public boolean isMonthlySwOnDemandPart() {
		return monthlySwOnDemandPart;
	}



	public boolean isHasRamupPart() {
		return hasRamupPart;
	}

	public void setHasRamupPart(boolean hasRamupPart)throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.hasRamupPart, hasRamupPart, persister);
		this.hasRamupPart = hasRamupPart;
	}

	public boolean isUpdateSectionFlag() {
		return updateSectionFlag;
	}

	public void setUpdateSectionFlag(boolean updateSectionFlag) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.updateSectionFlag, updateSectionFlag, persister);
		this.updateSectionFlag = updateSectionFlag;
	}

	public boolean isAdditionSectionFalg()  {
		return additionSectionFlag;
	}
	
	public void setAdditionSectionFalg(boolean additionSctionFlag)throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.additionSectionFlag, additionSectionFlag, persister);
		this.additionSectionFlag = additionSctionFlag;
	}
	
	public boolean isBeRampuped() {
		return (isMonthlySwSubscrptnPart() && getRampUpLineItems() != null && getRampUpLineItems().size() > 0);
	}
	
	public String getPartKey(){
		return getPartNum()+"_" +this.getSeqNum();
	}

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.MonthlySwLineItem#setMonthlySwSubscrptnPart(boolean)
     */
    @Override
    public void setMonthlySwSubscrptnPart(boolean monthlySwSubscrptnPart) throws TopazException {
        CheckPersisterUtil.checkPersisterDirty(this.monthlySwSubscrptnPart, monthlySwSubscrptnPart, persister);
        this.monthlySwSubscrptnPart = monthlySwSubscrptnPart;
    }

	@Override
	public void setMonthlySwSubscrptnOvragePart(boolean monthlySwSubscrptnOvragePart) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.monthlySwSubscrptnOvragePart, monthlySwSubscrptnOvragePart, persister);
		this.monthlySwSubscrptnOvragePart = monthlySwSubscrptnOvragePart;
	}

	@Override
	public void setMonthlySwTcvAcv(boolean monthlySwTcvAcv) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.saasTcvAcv, monthlySwTcvAcv, persister);
		this.saasTcvAcv = monthlySwTcvAcv;
	}

    /**
     * Getter for additionSectionFlag.
     * 
     * @return the additionSectionFlag
     */
    public boolean isAdditionSectionFlag() {
        return this.additionSectionFlag;
    }

    /**
     * Sets the additionSectionFlag.
     * 
     * @param additionSectionFlag the additionSectionFlag to set
     */
    public void setAdditionSectionFlag(boolean additionSectionFlag) throws TopazException {
        CheckPersisterUtil.checkPersisterDirty(this.additionSectionFlag, additionSectionFlag, persister);
        this.additionSectionFlag = additionSectionFlag;
    }

    /**
     * Sets the monthlySwPart.
     * 
     * @param monthlySwPart the monthlySwPart to set
     */
    public void setMonthlySwPart(boolean monthlySwPart) throws TopazException {
        CheckPersisterUtil.checkPersisterDirty(this.monthlySwPart, monthlySwPart, persister);
        this.monthlySwPart = monthlySwPart;
    }

    /**
     * Sets the monthlySwDailyPart.
     * 
     * @param monthlySwDailyPart the monthlySwDailyPart to set
     */
    public void setMonthlySwDailyPart(boolean monthlySwDailyPart) throws TopazException {
        CheckPersisterUtil.checkPersisterDirty(this.monthlySwDailyPart, monthlySwDailyPart, persister);
        this.monthlySwDailyPart = monthlySwDailyPart;
    }

	public void setMonthlySwOnDemandPart(boolean monthlySwOnDemandPart) throws TopazException {
		CheckPersisterUtil.checkPersisterDirty(this.monthlySwOnDemandPart, monthlySwOnDemandPart, persister);
		this.monthlySwOnDemandPart = monthlySwOnDemandPart;
	}

	public boolean isRampUpIndicator4QuoteCreateService() {
		return isRampUpIndicator4QuoteCreateService;
	}

	public void setRampUpIndicator4QuoteCreateService(boolean isRampUpIndicator4QuoteCreateService) {
		this.isRampUpIndicator4QuoteCreateService = isRampUpIndicator4QuoteCreateService;
	}

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.common.domain.QuoteLineItem_Impl#getReplacedTerm()
     */
    @Override
    public Integer getReplacedTerm() {
        if (isReplacedPart()) {
            return this.getICvrageTerm() == null ? 0 : Integer.valueOf(this.getICvrageTerm());
        }
        return 0;
    }

}
