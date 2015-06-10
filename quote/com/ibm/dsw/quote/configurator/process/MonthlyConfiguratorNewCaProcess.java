/**
 * 
 */
package com.ibm.dsw.quote.configurator.process;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfigurationFactory;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.configurator.contract.BuildMonthlySwConfiguratorContract;
import com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract;
import com.ibm.dsw.quote.configurator.domain.CotermParameter;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorForm;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;
import com.ibm.ead4j.topaz.exception.TopazException;


/**
 * @ClassName: MonthlyConfiguratorNewCaProcess
 * @author Frank
 * @Description: TODO
 * @date Dec 18, 2013 1:56:45 PM
 *
 */
public class MonthlyConfiguratorNewCaProcess extends AbstractMonthlyConfiguratorProcess {

	/**
	 * @param monlySwConfigJDBC
	 */
	public MonthlyConfiguratorNewCaProcess(
			MonthlySwConfiguratorJDBC monlySwConfigJDBC) {
		super(monlySwConfigJDBC);
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.process.AbstractMonthlyConfiguratorProcess#setMonthlySwProducts(com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorForm)
	 */
	@Override
	protected void setMonthlySwProducts(
			MonthlySwConfiguratorForm configuratorForm,
			BuildMonthlySwConfiguratorContract buildContract)
			throws TopazException {

		/**
		 * get main parts from JDBC main parts: subscription parts , domain
		 * parts
		 */
        List<MonthlySwConfiguratorPart> masterMonthlySwParts = monlySwConfigJDBC.getMonthlySwConfgiuratorParts(
                buildContract.getWebQuoteNum(), null, null, null);

        // set configurator List
        configuratorForm.setMonthlySwProducts(getConfiguratorProductList(masterMonthlySwParts));

	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.process.AbstractMonthlyConfiguratorProcess#getEndDate(com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract, java.util.List, java.util.List)
	 */
	@Override
	public Date getEndDate(
			SubmittedMonthlySwConfiguratorContract submitContract,
			List<MonthlySwConfiguratorPart> configuratorPartsFromPage,
			Map<String,MonthlySwConfiguratorPart> masterMonthlySwPartsFromDbMap)
			throws TopazException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.process.AbstractMonthlyConfiguratorProcess#processDeleteLineItem(com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart, java.util.Map)
	 */
	@Override
	protected void processDeleteLineItem(
			MonthlySwConfiguratorPart configuratorPartFromDB,
			MonthlySwConfiguratorPart configuratorFromPage,
			Map<String, MonthlySwLineItem> monthlylineItemsMap) throws TopazException {
		
		if (configuratorPartFromDB == null) {
			return ;
		}
		
		configuratorPartFromDB.deleteFromQuote(monthlylineItemsMap);
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.process.AbstractMonthlyConfiguratorProcess#processUpdateLineItem(com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart, java.util.Map)
	 */
	@Override
    protected void processUpdateLineItem(MonthlySwConfiguratorPart configuratorPartFromDB,
            MonthlySwConfiguratorPart configuratorFromPage, Map<String, MonthlySwLineItem> monthlylineItemsMap,
            SubmittedMonthlySwConfiguratorContract submitContract, CotermParameter parameter) throws TopazException {
		
		if (configuratorPartFromDB == null){
			return ;
		}
		
		configuratorPartFromDB.update(configuratorFromPage, monthlylineItemsMap, submitContract);

	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.process.AbstractMonthlyConfiguratorProcess#processAddLineItem(com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart, com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart, com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract)
	 */
    @Override
    protected boolean processAddLineItem(MonthlySwConfiguratorPart configuratorPartFromDB,
            MonthlySwConfiguratorPart configuratorFromPage, Map<String, MonthlySwLineItem> monthlylineItemsMap,
            SubmittedMonthlySwConfiguratorContract submitContract) throws TopazException {
        return false;
    }



    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.dsw.quote.configurator.process.AbstractMonthlyConfiguratorProcess#isDeleted(com.ibm.dsw.quote.configurator
     * .domain.MonthlySwConfiguratorPart, com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart)
     */
	@Override
	protected boolean isDeleted(MonthlySwConfiguratorPart configuratorFromPage,
			MonthlySwConfiguratorPart configuratorPartFromDB) {
		return configuratorFromPage.getSubmitConfiguratorPart().isDeleted();
	}

	protected MonthlySoftwareConfiguration processConfiguration(SubmittedMonthlySwConfiguratorContract submitContract,
			List<MonthlySwConfiguratorPart> configuratorPartsFromPage,
			Map<String, MonthlySwConfiguratorPart> masterMonthlySwPartsFromDbMap) throws TopazException {
		String webQuoteNum = submitContract.getWebQuoteNum();
		String orgConfigId = submitContract.getOrgConfigId();

		/**
		 * get monthly softwareConfiguration
		 */
		List<MonthlySoftwareConfiguration> monthlySwConfigurationList = MonthlySoftwareConfigurationFactory.singleton()
				.findMonthlySwConfiguration(webQuoteNum);

		if (monthlySwConfigurationList == null || monthlySwConfigurationList.size() < 1) {
			// create configuraor
			MonthlySoftwareConfiguration monthlyConfigurtn = MonthlySoftwareConfigurationFactory.singleton()
					.createMonthlyConfiguration(submitContract.getUserId());
			monthlyConfigurtn.setConfigrtnActionCode(submitContract.getConfigrtnActionCode());
			monthlyConfigurtn.setChrgAgrmtNum(submitContract.getChrgAgrmtNum());
			monthlyConfigurtn.setWebQuoteNum(webQuoteNum);
			monthlyConfigurtn.setConfigrtnId(GenerateMonthlyConfigrtnId.getConfigurtnId(webQuoteNum));
			monthlyConfigurtn.setConfigrtnActionCode(submitContract.getConfigrtnActionCode());
			monthlyConfigurtn.setEndDate(getEndDate(submitContract, configuratorPartsFromPage, masterMonthlySwPartsFromDbMap));
			monthlyConfigurtn.setNeedReconfigFlag(Boolean.FALSE);
			monthlyConfigurtn.setAddNewMonthlySWFlag(Boolean.FALSE);
			monthlyConfigurtn.setConfigrtnIdFromCa(orgConfigId);
			newlyAddedConfiguration = true;
			return monthlyConfigurtn;
		} else {
			// update
			for (MonthlySoftwareConfiguration monthlyConfigurtn : monthlySwConfigurationList) {
				monthlyConfigurtn.setConfigrtnActionCode(submitContract.getConfigrtnActionCode());
				monthlyConfigurtn.setChrgAgrmtNum(submitContract.getChrgAgrmtNum());
				monthlyConfigurtn.setNeedReconfigFlag(Boolean.FALSE);
				monthlyConfigurtn.setAddNewMonthlySWFlag(Boolean.FALSE);
				monthlyConfigurtn
						.setEndDate(getEndDate(submitContract, configuratorPartsFromPage, masterMonthlySwPartsFromDbMap));
				monthlyConfigurtn.setConfigrtnIdFromCa(orgConfigId);
				// always set dirty to config, force to always call SP to update
				// price_recalculate_flag = true
				// fix the issue of not call pricing service after update
				// configuration
				monthlyConfigurtn.setDirty();
				return monthlyConfigurtn;

			}
		}
		return null;
	}

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.configurator.process.AbstractMonthlyConfiguratorProcess#processForCoterm(java.util.Map,
     * java.util.Map, java.util.List)
     */
    @Override
    protected CotermParameter processForCoterm(Map<String, MonthlySwConfiguratorPart> masterMonthlySwPartsFromDbMap,
            Map<String, MonthlySwLineItem> monthlylineItemsMap, List<MonthlySwConfiguratorPart> configuratorPartsFromPage) {
        return null;
    }


}
