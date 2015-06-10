package com.ibm.dsw.quote.draftquote.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.MonthlySoftwareConfiguration;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.rule.BusinessRuleUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author Vivian
 *
 */
public class StreamlinedRuleHelper {
    
    public static final String RULE_SET_ID = "STREAMLINED_RULE";
    public static final String EXIST_PART_RULE_ID = "validateExistingParts";
    public static final String REMOVED_PART_RULE_ID = "validateRemovedParts";
    public static final String SAME_ATTRBT_RULE_ID = "validateSameAttribute";
    
    public static final String SAAS_DISCOUNT_IN_GRP_RULE_ID = "validateSaasDiscountInGrp";
    public static final String SAAS_GRP_RULE_ID = "validateSaasGroup";
    public static final String QUOTE_HEADER_RULE_ID = "validateQuoteHeader";
    public static final String SAAS_NEW_ADDED_RULE_ID = "validateNewAddedParts";
    public static final String SAAS_SPCL_BID_RULE_ID = "validateSpecialBidInfo";
    public static final String GEN_GRP_KEY = "genGroupKey";
    
    protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    protected Quote quote = null;
    protected BusinessRuleUtil xrule = null;

    public StreamlinedRuleHelper(Quote quote) throws QuoteException {
        if (quote == null)
            throw new IllegalStateException();
        this.quote = quote;
        if(isNeedInit()){
	        this.xrule = new BusinessRuleUtil(RULE_SET_ID);
        }
    }
    
    private boolean isNeedInit() {
    	if(quote.getSoftwareLineItems() != null && quote.getSoftwareLineItems().size() > 0){
    		return false;
    	}
    	return true;
    }
    
    
    public boolean isValidStreamlined() throws QuoteException {
    	if(quote.getSoftwareLineItems() != null && quote.getSoftwareLineItems().size() > 0){
    		return false;
    	}
    	if(quote.getQuoteHeader().isSSPQuote()){
            return false;
        }
    	if(!isAllAddTrdConfigs()){
    		return false;
    	}
        if (containsTermExtension()) {
            return false;
        }
    	if(isPassAllRules()){
    		return true;
    	}
    	return false;
    }

    /**
     * DOC check if the quote is termExtension
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    private boolean containsTermExtension() {
        List partsPricingConfigrtnsList = quote.getPartsPricingConfigrtnsList();
        boolean isExtension = false;
        for (int i = 0; i < partsPricingConfigrtnsList.size(); i++) {
            PartsPricingConfiguration config = (PartsPricingConfiguration) partsPricingConfigrtnsList.get(i);
            if (config.isTermExtension()) {
                isExtension = true;
                break;
            }
        }
        return isExtension;
    }

    private boolean isAllAddTrdConfigs() {
    	if(StringUtils.isBlank(quote.getQuoteHeader().getRefDocNum())){
    		return false;
    	}
    	//Saas
    	for (Iterator iterator = quote.getPartsPricingConfigrtnsList().iterator(); iterator.hasNext();) {
			PartsPricingConfiguration config = (PartsPricingConfiguration) iterator.next();
			if(!PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(config.getConfigrtnActionCode())){
				return false;
			}
		}
    	
    	//monthly
    	if (quote.getMonthlySwQuoteDomain() != null){
    		List<MonthlySoftwareConfiguration> monthlyConfigtns = quote.getMonthlySwQuoteDomain().getMonthlySwConfgrtns();
    		if (monthlyConfigtns != null && monthlyConfigtns.size() > 0){
    			for (Iterator iterator =monthlyConfigtns.iterator();iterator.hasNext();){
    				MonthlySoftwareConfiguration monthlyConfigtn =(MonthlySoftwareConfiguration) iterator.next();
    				if (!PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(monthlyConfigtn.getConfigrtnActionCode())){
    					return false;
    				}
    			}
    		}
    	}
    	
    	return true;
    }
    
    private boolean isPassAllRules() throws QuoteException {
    	String streamlinedWebQuoteNum = quote.getQuoteHeader().getStreamlinedWebQuoteNum();
    	if(StringUtils.isBlank(streamlinedWebQuoteNum)){
    		return false;
    	}
    	
    	List<QuoteLineItem> originalParts;
    	try {
    		originalParts = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(streamlinedWebQuoteNum);
		} catch (TopazException e) {
			throw new QuoteException(e);
		}
		
		Map <String, QuoteLineItem> originalPartsMap = getOriginalPartsMap(originalParts);
		
		Map <String, List<QuoteLineItem>> originalPartsGroupByPidMap = getOriPartsGroupByPidMap(originalParts);
		
		
		List<QuoteLineItem> existingSaasParts = getExistingLineItems(originalPartsMap);
		
		List<QuoteLineItem> newAddedSaasParts = getNewAddedLineItems(originalPartsMap);
		
		List<QuoteLineItem> removedSaasParts = new ArrayList();
		removedSaasParts.addAll(originalParts);
    	removedSaasParts.removeAll(existingSaasParts);
		
		
		if(this.validateNewAddedParts(newAddedSaasParts)
			&& this.validateExistingParts(existingSaasParts, originalPartsMap)
			&& this.validateSameAttribute(newAddedSaasParts, originalParts)
			&& this.validateSaasDiscountInGrp(newAddedSaasParts, originalPartsGroupByPidMap)
			&& this.validateSaasGroup(newAddedSaasParts)
			&& this.validateSpecialBidInfo()
			&& this.validateQuoteHeader()
			&& this.validateRemovedParts(removedSaasParts)
			){
			return true;
		}
		
		return false;
    }
    
    private Map <String, QuoteLineItem> getOriginalPartsMap(List<QuoteLineItem> originalParts) {
    	Map <String, QuoteLineItem> originalPartsMap = new HashMap();
    	for (Iterator iterator = originalParts.iterator(); iterator.hasNext();) {
			QuoteLineItem orgQli = (QuoteLineItem) iterator.next();
			originalPartsMap.put(getPartMappingKey(orgQli), orgQli);
		}
    	return originalPartsMap;
    }
    
    private Map <String, List<QuoteLineItem>> getOriPartsGroupByPidMap(List<QuoteLineItem> originalParts) throws QuoteException {
    	Map <String, List<QuoteLineItem>> originalPartsGroupByPidMap = new HashMap();
    	for (Iterator iterator = originalParts.iterator(); iterator.hasNext();) {
			QuoteLineItem orgQli = (QuoteLineItem) iterator.next();
			if(originalPartsGroupByPidMap.get(getPidGroupKey(orgQli)) ==  null){
				List<QuoteLineItem> qliList = new ArrayList<QuoteLineItem>();
				qliList.add(orgQli);
				originalPartsGroupByPidMap.put(getPidGroupKey(orgQli), qliList);
			}else{
				originalPartsGroupByPidMap.get(getPidGroupKey(orgQli)).add(orgQli);
			}
		}
    	return originalPartsGroupByPidMap;
    }
    
    private List<QuoteLineItem> getExistingLineItems(Map <String, QuoteLineItem> originalPartsMap) {
    	List<QuoteLineItem> existingSaasParts = new ArrayList();
    	
    	List needAddToExistingParts = new ArrayList();
    	needAddToExistingParts.addAll(quote.getSaaSLineItems());
    	needAddToExistingParts.addAll(quote.getMonthlySwQuoteDomain().getMonthlySoftwares());
    	
    	for (Iterator iterator = needAddToExistingParts.iterator(); iterator.hasNext();) {
			QuoteLineItem crrntQli = (QuoteLineItem) iterator.next();
			if(crrntQli.isReplacedPart()){
				continue;
			}
			if(originalPartsMap.get(getPartMappingKey(crrntQli)) != null){
				existingSaasParts.add(crrntQli);
			}
		}
    	return existingSaasParts;
    }
    
    private List<QuoteLineItem> getNewAddedLineItems(Map <String, QuoteLineItem> originalPartsMap) {
    	List<QuoteLineItem> newAddedSaasParts = new ArrayList();
    	
    	List needAddToNewAddedParts = new ArrayList();
    	needAddToNewAddedParts.addAll(quote.getSaaSLineItems());
    	needAddToNewAddedParts.addAll(quote.getMonthlySwQuoteDomain().getMonthlySoftwares());
    	
    	for (Iterator iterator = needAddToNewAddedParts.iterator(); iterator.hasNext();) {
			QuoteLineItem crrntQli = (QuoteLineItem) iterator.next();
			if(originalPartsMap.get(getPartMappingKey(crrntQli)) == null){
				newAddedSaasParts.add(crrntQli);
			}
		}
    	return newAddedSaasParts;
    }
    
    private String getPartMappingKey(QuoteLineItem qli){
    	return qli.getPartNum() + '_' + qli.getRampUpPeriodNum();
    }
    
    private String getPidGroupKey(QuoteLineItem qli) throws QuoteException{
    	return executeRuleString(RULE_SET_ID, GEN_GRP_KEY, new Object[] { "qli", qli, "quote", quote });
    }
    
    
    /**
     * @return 
     * 1)new qty >=  original qty
	 * 2)new discount <=  original discount 
	 * 3)original monthly Unit price * (1+1%) >= new monthly Unit price >= original monthly Unit price * (1-1%)
     * @throws QuoteException
     */
    private boolean validateExistingParts(List<QuoteLineItem> existingSaasParts, Map <String, QuoteLineItem> originalPartsMap) throws QuoteException {
    	for (Iterator iterator = existingSaasParts.iterator(); iterator.hasNext();) {
			QuoteLineItem currentQli = (QuoteLineItem) iterator.next();
			QuoteLineItem orgQli = (QuoteLineItem) originalPartsMap.get(getPartMappingKey(currentQli));
			if(Boolean.FALSE.equals
					(executeRule(RULE_SET_ID, EXIST_PART_RULE_ID, new Object[] { "currentQli", currentQli, "orgQli", orgQli, "quote", quote }))){
				printLogBoth("validateExistingParts",orgQli,currentQli);
				return false;
			}
		}
    	return true;
    }
    
    /**
     * @return 
     * 1)new added part and original part are in the same PID
     * 2)new added part and original part are in the same revenue stream code
     * @throws QuoteException
     */
    private boolean validateSameAttribute(List<QuoteLineItem> newAddedSaasParts, List<QuoteLineItem> originalParts) throws QuoteException {
		boolean hasSameAttrbt = false;
	   	for (Iterator iterator = newAddedSaasParts.iterator(); iterator.hasNext();) {
	   		hasSameAttrbt = false;
				QuoteLineItem currentQli = (QuoteLineItem) iterator.next();
				for (Iterator iterator2 = originalParts.iterator(); iterator2.hasNext();) {
					QuoteLineItem orgQli = (QuoteLineItem) iterator2.next();
					if(Boolean.TRUE.equals
							(executeRule(RULE_SET_ID, SAME_ATTRBT_RULE_ID, new Object[] { "currentQli", currentQli, "orgQli", orgQli, "quote", quote }))){
						hasSameAttrbt = true;
						break;
					}
				}
				if(!hasSameAttrbt){
					printLogCurrent("validateSameAttribute",currentQli);
					return false;
				}
			}
	   	return true;
    }
    

    
   /**
    * @return new part discount <= lowest discount of part that having the same revenue stream and PID
    * @throws QuoteException
    */
   private boolean validateSaasDiscountInGrp(List<QuoteLineItem> newAddedSaasParts, Map <String, List<QuoteLineItem>> originalPartsGroupByPidMap) throws QuoteException {
	   	for (Iterator iterator = newAddedSaasParts.iterator(); iterator.hasNext();) {
	   		QuoteLineItem currentQli = (QuoteLineItem) iterator.next();
	   		if(originalPartsGroupByPidMap.get(getPidGroupKey(currentQli)) == null){
	   			continue;
	   		}
	   		List<QuoteLineItem> samePidRvnStrmParts = originalPartsGroupByPidMap.get(getPidGroupKey(currentQli));
			for (Iterator iterator2 = samePidRvnStrmParts.iterator(); iterator2.hasNext();) {
				QuoteLineItem orgQli = (QuoteLineItem) iterator2.next();
				if(Boolean.FALSE.equals
						(executeRule(RULE_SET_ID, SAAS_DISCOUNT_IN_GRP_RULE_ID, new Object[] { "currentQli", currentQli, "orgQli", orgQli, "quote", quote }))){
					printLogBoth("validateSaasDiscountInGrp",orgQli,currentQli);
					return false;
				}
			}
		}
	   	return true;
   }
   
   /**
    * @return validate new added parts
    * including new part discount <= 35%
    * new part should be a SaaS subscription, daily or overage part
    * @throws QuoteException
    */
   private boolean validateNewAddedParts(List<QuoteLineItem> newAddedSaasParts) throws QuoteException {
	   	for (Iterator iterator = newAddedSaasParts.iterator(); iterator.hasNext();) {
	   		QuoteLineItem currentQli = (QuoteLineItem) iterator.next();
	   		if(Boolean.FALSE.equals
					(executeRule(RULE_SET_ID, SAAS_NEW_ADDED_RULE_ID, new Object[] { "currentQli", currentQli, "quote", quote }))){
	   			printLogCurrent("validateNewAddedParts",currentQli);
				return false;
			}
		}
	   	return true;
   }
   
   /**
    * @return new part can't be a discounted and on the "No Bid Iteration" part group
    * @throws QuoteException
    */
   private boolean validateSaasGroup(List<QuoteLineItem> newAddedSaasParts) throws QuoteException {
	   	for (Iterator iterator = newAddedSaasParts.iterator(); iterator.hasNext();) {
				QuoteLineItem currentQli = (QuoteLineItem) iterator.next();
				List<QuoteLineItem.PartGroup> partGroups = currentQli.getPartGroups();
				for (Iterator iterator2 = partGroups.iterator(); iterator2.hasNext();) {
					QuoteLineItem.PartGroup partGroup = (QuoteLineItem.PartGroup) iterator2.next();
					if(Boolean.FALSE.equals
							(executeRule(RULE_SET_ID, SAAS_GRP_RULE_ID, new Object[] { "currentQli", currentQli, "partGroup", partGroup, "quote", quote }))){
						printLogCurrent("validateSaasGroup",currentQli);
						return false;
					}
				}
			}
	   	return true;
   }
   
   /**
    * @return Bid Iteration cannot include a Terms and Conditions change.
    * @throws QuoteException
    */
   private boolean validateSpecialBidInfo() throws QuoteException {
	   SpecialBidInfo sbInfo = quote.getSpecialBidInfo();
	   if(Boolean.FALSE.equals
				(executeRule(RULE_SET_ID, SAAS_SPCL_BID_RULE_ID, new Object[] { "sbInfo", sbInfo, "quote", quote }))){
			return false;
	   }
	   return true;
   }
   
   /**
    * @return validate quote header
    * @throws QuoteException
    */
   private boolean validateQuoteHeader() throws QuoteException {
	   if(Boolean.FALSE.equals
				(executeRule(RULE_SET_ID, QUOTE_HEADER_RULE_ID, new Object[] { "quoteHeader", quote.getQuoteHeader(), "dateUtil", new DateUtil(), "quote", quote }))){
			return false;
	   }
	   return true;
   }
   
   /**
    * @return 
    * removed parts validation
    * @throws QuoteException
    */
   private boolean validateRemovedParts(List<QuoteLineItem> removedSaasParts) throws QuoteException {
   		for (Iterator iterator = removedSaasParts.iterator(); iterator.hasNext();) {
			QuoteLineItem currentQli = (QuoteLineItem) iterator.next();
			if(Boolean.FALSE.equals
					(executeRule(RULE_SET_ID, REMOVED_PART_RULE_ID, new Object[] { "currentQli", currentQli, "quote", quote }))){
				printLogCurrent("validateRemovedParts",currentQli);
				return false;
			}
		}
   		return true;
   }
   
    
    protected boolean executeRule(String ruleSetId, String ruleId, Object[] objects) throws QuoteException {
    	Object result = xrule.executeRule(ruleSetId,
                ruleId, objects);

        logContext.debug(this, "XRule (" + ruleSetId + " -> " + ruleId + ") implementation result is: " + result);
        Boolean extResult;
        try{
        	extResult = Boolean.valueOf(result.toString()).booleanValue();
        }catch(Exception e){
        	logContext.error(this, e.getMessage());
        	return false;
        }
        return extResult;
    }
    
    protected String executeRuleString(String ruleSetId, String ruleId, Object[] objects) throws QuoteException {
  		Object result = xrule.executeRule(ruleSetId,
              ruleId, objects);

      logContext.debug(this, "XRule (" + ruleSetId + " -> " + ruleId + ") implementation result is: " + result);
      String extResult;
      try{
      		extResult = result.toString();
      }catch(Exception e){
      		logContext.error(this, e.getMessage());
      		return "";
      }
      return extResult;
  }
    
    private void printLogCurrent(String logString, QuoteLineItem qli){
    	logContext.info(this, "webquotenum " + quote.getQuoteHeader().getWebQuoteNum() + " ---> " 
    			+ logString + " validation is false cause current part --> " 
    			+ qli.getPartNum() + "_" + qli.getSeqNum());
    }
    
    private void printLogBoth(String logString, QuoteLineItem oriQli, QuoteLineItem currQli){
    	logContext.info(this, "webquotenum " + quote.getQuoteHeader().getWebQuoteNum() + " ---> " 
    			+ logString + " validation is false cause original part --> " 
    			+ oriQli.getPartNum() + "_" + oriQli.getSeqNum()
    			+ " current part --> " + currQli.getPartNum() + "_" + currQli.getSeqNum());
    }
    

}

