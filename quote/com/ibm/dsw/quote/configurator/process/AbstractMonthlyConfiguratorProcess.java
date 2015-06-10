/**
 * 
 */
package com.ibm.dsw.quote.configurator.process;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.configurator.contract.BuildMonthlySwConfiguratorContract;
import com.ibm.dsw.quote.configurator.contract.SearchRestrictedMonthlyPartsContract;
import com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorHeader;
import com.ibm.dsw.quote.configurator.domain.CotermParameter;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorForm;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorPart;
import com.ibm.dsw.quote.configurator.domain.MonthlySwConfiguratorProduct;
import com.ibm.dsw.quote.configurator.domain.MonthlySwRampUpSubscriptionConfiguratorPart;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.TransactionContextManager;

/**
 * @ClassName: AbstractMonthlyConfiguratorProcess
 * @author Frank
 * @Description: TODO
 * @date Dec 18, 2013 1:53:02 PM
 *
 */
public abstract class AbstractMonthlyConfiguratorProcess implements MonthlyConfiguratorProcess {
	
	protected LogContext logContext = LogContextFactory.singleton().getLogContext();
	
	protected MonthlySwConfiguratorJDBC monlySwConfigJDBC;

    protected boolean newlyAddedConfiguration = false;
	
	public AbstractMonthlyConfiguratorProcess(
			MonthlySwConfiguratorJDBC monlySwConfigJDBC) {
		this.monlySwConfigJDBC = monlySwConfigJDBC;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.configurator.process.MonthlyConfiguratorProcess#bulidMonthlyConfigurator()
	 */
	@Override
	public MonthlySwConfiguratorForm bulidMonthlyConfigurator(
			BuildMonthlySwConfiguratorContract buildContract)
			throws TopazException {
		MonthlySwConfiguratorForm configuratorForm = null;
		try{
			TransactionContextManager.singleton().begin();
			
		    configuratorForm = new MonthlySwConfiguratorForm();

			//set configurator header attribute
			setConfiguratorHeaderAttribute(configuratorForm,buildContract);
			
			//set products
			setMonthlySwProducts(configuratorForm,buildContract);
			TransactionContextManager.singleton().commit();
		}
		catch(TopazException e){
			 logContext.debug(this, e.getMessage());
	         throw new TopazException(e);
		}
		finally{
			TransactionContextManager.singleton().rollback();
		}
		return configuratorForm;
	}

	protected abstract void setMonthlySwProducts (MonthlySwConfiguratorForm configuratorForm ,BuildMonthlySwConfiguratorContract buildContract) throws TopazException;
	/**
	 * add parts to quote 
	 */
    @SuppressWarnings("rawtypes")
    @Override
	public void addMonthlySwToQuote(SubmittedMonthlySwConfiguratorContract submitContract)throws TopazException ,QuoteException {
		String webQuoteNum = submitContract.getWebQuoteNum();
		String caNum = submitContract.getChrgAgrmtNum();
		String configId = submitContract.getConfigurtnId();
		String userID = submitContract.getUserId();
		String orgConfigId = submitContract.getOrgConfigId();
		String paramConfigId = "";
		if (submitContract.isAddOnConfigrtn()) {
			paramConfigId = orgConfigId;
		} else {
			paramConfigId = configId;
		}
		
		try {
			TransactionContextManager.singleton().begin();
			
			//get monthly software data from page
			//contain : ondemand part , subscription part ,set up part ,ramp up part .
			List<MonthlySwConfiguratorPart> configuratorPartsFromPage = submitContract.getConfiguratorPartList();
			if (configuratorPartsFromPage == null || configuratorPartsFromPage.size() < 1) {
				return ;
			}
			
			//get monthly software date from db
			List<MonthlySwConfiguratorPart> masterMonthlySwPartsFromDb = monlySwConfigJDBC.getMonthlySwConfgiuratorParts(
					webQuoteNum, caNum, paramConfigId, userID);
			//key :partNum +"_"+ seqNum
			Map<String,MonthlySwConfiguratorPart> masterMonthlySwPartsFromDbMap = convertToConfigratorMap(masterMonthlySwPartsFromDb);
			
				
			//update configurator
			MonthlySoftwareConfiguration configuration =  processConfiguration(submitContract,configuratorPartsFromPage,masterMonthlySwPartsFromDbMap);
			
			//mapping to quote line item
			// from web quote line item
			//key : partNum +"_"+ seqNum
			List lineItemList = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(submitContract.getWebQuoteNum());
			Map<String,MonthlySwLineItem> monthlylineItemsMap =covertToMonthlyLineItemMap(lineItemList);
			deleteAllRampuLineItems(monthlylineItemsMap);
			MonthlySwConfiguratorPart.maxSeqNum = this.getMaxLineItemSeqNum(lineItemList);
            boolean addedLineItem = processLineItemMapping(masterMonthlySwPartsFromDbMap, submitContract, monthlylineItemsMap);
			
			//line items are not monthly
			//delete configuration
            if (!addedLineItem && (monthlylineItemsMap == null || monthlylineItemsMap.size() < 1)) {
                if (configuration != null) {
                    configuration.delete();
                }
            }
			if (monthlylineItemsMap == null || monthlylineItemsMap.size() < 1){
                if (configuration != null && newlyAddedConfiguration == false) {
					configuration.delete();
				}
			}
			
			TransactionContextManager.singleton().commit();
		} catch (TopazException te) {
			TransactionContextManager.singleton().rollback();
			throw te;
		} 
		
	}


    /**
     * 
     * @param masterMonthlySwParts
     * @return main parts group
     */
	private Map<String ,MonthlySwConfiguratorProduct> getConfiguratorProductMap(
			List<MonthlySwConfiguratorPart> masterMonthlySwParts) {
		
		Map<String, MonthlySwConfiguratorProduct> productsMap = new HashMap<String , MonthlySwConfiguratorProduct>();

		// 1. is not null
		if (masterMonthlySwParts == null || masterMonthlySwParts.size() < 1) {
			return productsMap;
		}
		

		// 3.
		Iterator<MonthlySwConfiguratorPart> it = masterMonthlySwParts
				.iterator();

		while (it.hasNext()) {
			MonthlySwConfiguratorPart masterMonthlySw = it.next();
			
			//
			MonthlySwConfiguratorProduct configProduct = productsMap.get(masterMonthlySw.getPid());
			
			// set product
			if (configProduct == null) {
				configProduct = new MonthlySwConfiguratorProduct();
				
				setConfiguratorProductAttribute(configProduct,masterMonthlySw);
				
				productsMap.put(masterMonthlySw.getPid() , configProduct);

			} else {
				configProduct.addSubscrptnPart(masterMonthlySw);
				configProduct.addOnDemandPart(masterMonthlySw);
				
			}
		}
		
		return productsMap;

	}
	
	protected List<MonthlySwConfiguratorProduct> getConfiguratorProductList(List<MonthlySwConfiguratorPart> masterMonthlySwParts){
		List<MonthlySwConfiguratorProduct> configuratorProductList = new ArrayList<MonthlySwConfiguratorProduct>();
		
		configuratorProductList.addAll(getConfiguratorProductMap(masterMonthlySwParts).values());
		
		return configuratorProductList;
		
	}
	
	
	/**
	 * set configProduct attribute
	 * @param configProduct
	 * @param configuratorPart
	 */
	private void setConfiguratorProductAttribute(
			MonthlySwConfiguratorProduct configProduct,
			MonthlySwConfiguratorPart configuratorPart) {
		configProduct.setPid(configuratorPart.getPid());
		configProduct.setPidDesc(configuratorPart.getPidDscr());
		configProduct.setBid(configuratorPart.getBrandId());
		configProduct.setBidDesc(configuratorPart.getBrandDscr());
		configProduct.addOnDemandPart(configuratorPart);
		configProduct.addSubscrptnPart(configuratorPart);
	}
	
	/**
	 * 
	 * @param configratnHeader 
	 * @param webQuoteNum
	 * @throws TopazException
	 */
	private void setConfiguratorHeaderAttribute(MonthlySwConfiguratorForm configuratorForm,BuildMonthlySwConfiguratorContract buildContract) throws TopazException{
		//get QuoteHeader
		ConfiguratorHeader header = getConfigHeaderInfo(buildContract.getWebQuoteNum(), buildContract.getUserId());
		String configId = "";
		if (StringUtils.isBlank(header.getConfigId())) {
			configId = GenerateMonthlyConfigrtnId.getConfigurtnId(header.getWebQuoteNum());
		} else {
			configId = header.getConfigId();
		}
		ConfiguratorHeader configratnHeader = configuratorForm.getConfigHeader();
		configratnHeader.setCntryCode(header.getCntryCode());
		configratnHeader.setCurrencyCode(header.getCurrencyCode());
		configratnHeader.setLob(header.getLob());
		configratnHeader.setWebQuoteNum(header.getWebQuoteNum());
		configratnHeader.setConfigrtnActionCode(buildContract.getConfigrtnActionCode());
		configratnHeader.setConfigId(configId);
		configratnHeader.setChrgAgrmtNum(buildContract.getChrgAgrmtNum());
		configratnHeader.setAudience(header.getAudience());
		configratnHeader.setAcqrtnCode(header.getAcqrtnCode());
		configratnHeader.setProgMigrationCode(header.getProgMigrationCode());
		configratnHeader.setQuoteTypeCode(header.getQuoteTypeCode().trim());
		configratnHeader.setCustomerNumber(header.getCustomerNumber());
		configratnHeader.setSapContractNum(header.getSapContractNum());
		configratnHeader.setOrgConfigId(buildContract.getOrgConfigId());

		// set buildContract
		buildContract.setWebQuoteNum(header.getWebQuoteNum());
		buildContract.setConfigurtnId(configId);

	}
	
	/**
	 * 
	 * @param webQuoteNum
	 * @return
	 * @throws TopazException
	 */
	protected ConfiguratorHeader getConfigHeaderInfo(String webQuoteNum, String userId) throws TopazException {
		return ConfiguratorHeaderFactory.singleton().getHdrInfoForMonthlyConfiguratorByWebQuoteNum(webQuoteNum, userId);
	}

	protected Map<String,MonthlySwConfiguratorPart> convertToConfigratorMap (List<MonthlySwConfiguratorPart> monthlySwParts){
		Map<String,MonthlySwConfiguratorPart> monthlySwConfiguratorPartMap = new HashMap<String , MonthlySwConfiguratorPart>();
		
		if (monthlySwParts == null) {
			return monthlySwConfiguratorPartMap;
		}
		
		for (MonthlySwConfiguratorPart configuratorPart : monthlySwParts){
			monthlySwConfiguratorPartMap.put(configuratorPart.getPartKey(), configuratorPart);
		}
		
		return monthlySwConfiguratorPartMap;
	}
	
	protected Map<String,MonthlySwLineItem> covertToMonthlyLineItemMap (List lineItems){
		Map<String ,MonthlySwLineItem> monthlyLineItemsMap = new HashMap<String , MonthlySwLineItem>();
		
		if (lineItems == null) {
			return monthlyLineItemsMap;
		}
		addMonthlyLineItemToMap(lineItems,monthlyLineItemsMap);
		
		return monthlyLineItemsMap;
	}
	
	
	protected void addMonthlyLineItemToMap(List lineItems ,Map<String ,MonthlySwLineItem> monthlyLineItemsMap){
		for (Object lineItem : lineItems){
			if (lineItem instanceof MonthlySwLineItem) {
				MonthlySwLineItem item = (MonthlySwLineItem)lineItem;
				monthlyLineItemsMap.put(item.getPartKey(), item);
			}
		}
	}
	
    private boolean processLineItemMapping(
            Map<String, MonthlySwConfiguratorPart> masterMonthlySwPartsFromDbMap,
			SubmittedMonthlySwConfiguratorContract submitContract ,Map<String,MonthlySwLineItem> monthlylineItemsMap) throws TopazException {
		
		/**
		 * from page
		 */
		List<MonthlySwConfiguratorPart> configuratorPartsFromPage = submitContract.getConfiguratorPartList();
        // Collections.sort(configuratorPartsFromPage);
		if (configuratorPartsFromPage == null || configuratorPartsFromPage.size() < 1 ){
            return false;
		}
        boolean addedLineItem = false;
        CotermParameter parameter = processForCoterm(masterMonthlySwPartsFromDbMap, monthlylineItemsMap,
                configuratorPartsFromPage);
		for (MonthlySwConfiguratorPart configuratorFromPage : configuratorPartsFromPage){
			
			// skill all RampUpSubscription part, will deal with them in
			// Subscription part
			if (configuratorFromPage instanceof MonthlySwRampUpSubscriptionConfiguratorPart) {
				continue;
			}

            // came from CA or web
            MonthlySwConfiguratorPart configuratorPartFromDB = masterMonthlySwPartsFromDbMap.get(configuratorFromPage
                    .getPartKey());


            if (monthlylineItemsMap.get(configuratorFromPage.getPartKey()) != null) {
                if (isDeleted(configuratorFromPage, configuratorPartFromDB)) {
                    processDeleteLineItem(configuratorPartFromDB, configuratorFromPage, monthlylineItemsMap);
                } else {
                    processUpdateLineItem(configuratorPartFromDB, configuratorFromPage, monthlylineItemsMap, submitContract,
                            parameter);
                }

			} else {
                boolean singleAddedFlag = processAddLineItem(configuratorPartFromDB, configuratorFromPage, monthlylineItemsMap,
                        submitContract);
                if (!addedLineItem) {
                    addedLineItem = singleAddedFlag;
                }
			}
		}
        return addedLineItem;
	}

    /**
     * DOC calculate CotermParameter for ML part.
     * 
     * @param masterMonthlySwPartsFromDbMap
     * @param monthlylineItemsMap
     * @param configuratorPartsFromPage
     * @return
     */
    protected abstract CotermParameter processForCoterm(Map<String, MonthlySwConfiguratorPart> masterMonthlySwPartsFromDbMap,
            Map<String, MonthlySwLineItem> monthlylineItemsMap, List<MonthlySwConfiguratorPart> configuratorPartsFromPage);

    /**
     * DOC Comment method "processAddLineItemForNewCa".
     * 
     * @param configuratorPartFromDB
     * @param configuratorFromPage
     * @param submitContract
     * @param addedMonthlySwLineItem
     */
    protected void processAddLineItemForNewCa(MonthlySwConfiguratorPart configuratorPartFromDB,
            MonthlySwConfiguratorPart configuratorFromPage, SubmittedMonthlySwConfiguratorContract submitContract,
            List<MonthlySwLineItemConfiguratorPair> addedMonthlySwLineItem) throws TopazException {

    }

    protected abstract boolean isDeleted(MonthlySwConfiguratorPart configuratorFromPage,
            MonthlySwConfiguratorPart configuratorPartFromDB);

    protected abstract void processDeleteLineItem(MonthlySwConfiguratorPart configuratorPartFromDB,
            MonthlySwConfiguratorPart configuratorFromPage, Map<String, MonthlySwLineItem> monthlylineItemsMap)
            throws TopazException;

    protected abstract void processUpdateLineItem(MonthlySwConfiguratorPart configuratorPartFromDB,
			MonthlySwConfiguratorPart configuratorFromPage, Map<String, MonthlySwLineItem> monthlylineItemsMap,
            SubmittedMonthlySwConfiguratorContract submitContract, CotermParameter parameter)
            throws TopazException;

    protected abstract boolean processAddLineItem(MonthlySwConfiguratorPart configuratorPartFromDB,
			MonthlySwConfiguratorPart configuratorFromPage, Map<String, MonthlySwLineItem> monthlylineItemsMap,
			SubmittedMonthlySwConfiguratorContract submitContract)
            throws TopazException;
    /**
     * 
     * @param submitContract
     * @return
     * @throws TopazException
     */
	protected abstract MonthlySoftwareConfiguration processConfiguration(SubmittedMonthlySwConfiguratorContract submitContract,
            List<MonthlySwConfiguratorPart> configuratorPartsFromPage,
			Map<String, MonthlySwConfiguratorPart> masterMonthlySwPartsFromDbMap) throws TopazException;
	

	
    /**
     * 
     * @param submitContract
     * @param configuratorPartsFromPage
     * @param masterMonthlySwPartsFromDbMap
     * @return
     * @throws TopazException
     */
    public abstract Date getEndDate(SubmittedMonthlySwConfiguratorContract submitContract,
            List<MonthlySwConfiguratorPart> configuratorPartsFromPage,
            Map<String, MonthlySwConfiguratorPart> masterMonthlySwPartsFromDbMap) throws TopazException;

    /**
     * get max seq num from Quote line items
     * 
     * @param lineItems
     * @return
     */
    @SuppressWarnings("rawtypes")
    private int getMaxLineItemSeqNum(List lineItems) {
        int maxSeqNum = 0;
        if (lineItems == null || lineItems.size() < 1) {
            return maxSeqNum;
        }

        for (Object lineItem : lineItems) {
            QuoteLineItem item = (QuoteLineItem) lineItem;

            if (maxSeqNum < item.getSeqNum()) {
                maxSeqNum = item.getSeqNum();
            }
        }
        return maxSeqNum;
    }

	private void deleteAllRampuLineItems(Map<String, MonthlySwLineItem> monthlylineItemsMap) throws TopazException {
		if (monthlylineItemsMap == null) {
			return;
		}
		List<MonthlySwLineItem> allLineItems = new ArrayList<MonthlySwLineItem>(monthlylineItemsMap.values());
		for (MonthlySwLineItem lineItem : allLineItems) {
			if (lineItem.isRampupPart()) {
				monthlylineItemsMap.remove(lineItem.getPartKey());
				lineItem.delete();
			}
		}

	}
	
	public Map<String, String> searchRestrictedMonthlyPart(
			SearchRestrictedMonthlyPartsContract searchRestrictedPartContract)
			throws TopazException,QuoteException{
		String webQuoteNum=searchRestrictedPartContract.getWebQuoteNum();
		String searchString=searchRestrictedPartContract.getSearchPartList();
		try {
			TransactionContextManager.singleton().begin();
			Map map= monlySwConfigJDBC.searchRestrictedMonthlyParts(searchString, webQuoteNum);
			TransactionContextManager.singleton().commit();
			return map;
		} catch (TopazException te) {
			TransactionContextManager.singleton().rollback();
			throw te;
		} 			
		
	}
	
}
