package com.ibm.dsw.quote.draftquote.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.common.domain.SpecialBidInfo;
import com.ibm.dsw.quote.common.util.BidIterationRule;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.rule.BusinessRuleUtil;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author Vivian
 *
 */
public abstract class BidIterationRuleHelper {
    
    public static final String RULE_SET_ID = "BID_ITERATION";
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
    protected BidIterationRule bidIterationRule = null;
    //originalAllParts (saas parts , monthly parts and software parts)
    protected List<QuoteLineItem> originalAllParts = new ArrayList();
    
    //originalParts (saas parts or monthly parts)
    protected List<QuoteLineItem> originalParts = new ArrayList();
    protected Map <String, QuoteLineItem> originalPartsMap = new HashMap(); //group by partNum + '_' + ruampUpNum
    protected Map <String, List<QuoteLineItem>> oriGroupByPidMap = new HashMap();  //group by PID + '_' + REVN STRM CODE + '_' + worldwide product code
    
    //currentParts (saas parts or monthly parts)
    protected List<QuoteLineItem> currentParts = new ArrayList();
    
    protected List<QuoteLineItem> existingParts = new ArrayList();
    protected List<QuoteLineItem> newAddedParts = new ArrayList();
    protected List<QuoteLineItem> removedParts = new ArrayList();

    public BidIterationRuleHelper(Quote quote, BidIterationRule bidIterationRule) throws QuoteException {
        if (quote == null)
            throw new IllegalStateException();
        this.quote = quote;
        this.bidIterationRule = bidIterationRule;
        this.xrule = new BusinessRuleUtil(RULE_SET_ID);
        init();
    }
    
    private void init() throws QuoteException {
    	initOriginalAllLineItms();
    	initOriginalLineItems();
        initOriginalPartsMap();
        initOriGroupByPidMap();
        initCurrentLineItems();
        initExistingLineItems();
        initNewAddedLineItems();
        initRemovedLineItems();
    }
    
    private void initOriginalAllLineItms() throws QuoteException{
    	List<QuoteLineItem> allParts;
		try {
			allParts = QuoteLineItemFactory.singleton().findLineItemsByWebQuoteNum(quote.getQuoteHeader().getPriorQuoteNum());
		} catch (TopazException e) {
			throw new QuoteException(e);
		}
		originalAllParts.addAll(allParts);
    }
    protected abstract void initOriginalLineItems() throws QuoteException;
    
    protected abstract void initCurrentLineItems() throws QuoteException;

    private void initOriginalPartsMap() {
    	for (Iterator iterator = originalParts.iterator(); iterator.hasNext();) {
			QuoteLineItem orgQli = (QuoteLineItem) iterator.next();
			originalPartsMap.put(getPartMappingKey(orgQli), orgQli);
		}
    }
    
    private void initOriGroupByPidMap() throws QuoteException {
    	for (Iterator iterator = originalParts.iterator(); iterator.hasNext();) {
			QuoteLineItem orgQli = (QuoteLineItem) iterator.next();
			if(oriGroupByPidMap.get(getPidGroupKey(orgQli)) ==  null){
				List<QuoteLineItem> qliList = new ArrayList<QuoteLineItem>();
				qliList.add(orgQli);
				oriGroupByPidMap.put(getPidGroupKey(orgQli), qliList);
			}else{
				oriGroupByPidMap.get(getPidGroupKey(orgQli)).add(orgQli);
			}
		}
    }
    
    private void initExistingLineItems() {
    	for (Iterator iterator = currentParts.iterator(); iterator.hasNext();) {
			QuoteLineItem crrntQli = (QuoteLineItem) iterator.next();
			if(crrntQli.isReplacedPart()){
				continue;
			}
			if(originalPartsMap.get(getPartMappingKey(crrntQli)) != null){
				existingParts.add(crrntQli);
			}
		}
    }
    
    private void initNewAddedLineItems() {
    	for (Iterator iterator = currentParts.iterator(); iterator.hasNext();) {
			QuoteLineItem crrntQli = (QuoteLineItem) iterator.next();
			if(originalPartsMap.get(getPartMappingKey(crrntQli)) == null){
				newAddedParts.add(crrntQli);
			}
		}
    }
    
    private void initRemovedLineItems() {
    	removedParts.addAll(originalParts);
    	removedParts.removeAll(existingParts);
    }
    
    private String getPartMappingKey(QuoteLineItem qli){
    	return qli.getPartNum() + '_' + qli.getRampUpPeriodNum();
    }
    
    private String getPidGroupKey(QuoteLineItem qli) throws QuoteException{
    	return executeRuleString(RULE_SET_ID, GEN_GRP_KEY, new Object[] { "qli", qli, "quote", quote });
    }
    
   
    public Set<String> getErrorMsgSet() throws QuoteException {
    	Set<String> errorMsgSet = new HashSet<String>();
    	
    	Set<String> errorMsgSet1 = new HashSet<String>();
    	if(Boolean.FALSE.equals(this.validateExistingParts(errorMsgSet1))){
    		errorMsgSet.addAll(errorMsgSet1);
    	}else{
    		errorMsgSet1.clear();
    	}
    	Set<String> errorMsgSet2 = new HashSet<String>();
    	if(Boolean.FALSE.equals(this.validateRemovedParts(errorMsgSet2))){
    		errorMsgSet.addAll(errorMsgSet2);
    	}else{
    		errorMsgSet2.clear();
    	}
    	Set<String> errorMsgSet3 = new HashSet<String>();
    	if(Boolean.FALSE.equals(this.validateSameAttribute(errorMsgSet3))){
    		errorMsgSet.addAll(errorMsgSet3);
    	}else{
    		errorMsgSet3.clear();
    	}
    	Set<String> errorMsgSet4 = new HashSet<String>();
    	if(Boolean.FALSE.equals(this.validateDiscountInGrp(errorMsgSet4))){
    		errorMsgSet.addAll(errorMsgSet4);
    	}else{
    		errorMsgSet4.clear();
    	}
    	Set<String> errorMsgSet5 = new HashSet<String>();
    	if(Boolean.FALSE.equals(this.validateGroup(errorMsgSet5))){
    		errorMsgSet.addAll(errorMsgSet5);
    	}else{
    		errorMsgSet5.clear();
    	}
    	Set<String> errorMsgSet6 = new HashSet<String>();
    	if(Boolean.FALSE.equals(this.validateQuoteHeader(errorMsgSet6))){
    		errorMsgSet.addAll(errorMsgSet6);
    	}else{
    		errorMsgSet6.clear();
    	}
    	Set<String> errorMsgSet7 = new HashSet<String>();
    	if(Boolean.FALSE.equals(this.validateNewAddedParts(errorMsgSet7))){
    		errorMsgSet.addAll(errorMsgSet7);
    	}else{
    		errorMsgSet7.clear();
    	}
    	Set<String> errorMsgSet8 = new HashSet<String>();
    	if(Boolean.FALSE.equals(this.validateSpecialBidInfo(errorMsgSet8))){
    		errorMsgSet.addAll(errorMsgSet8);
    	}else{
    		errorMsgSet8.clear();
    	}
    	return errorMsgSet;
    	
    	
    }
    
    public abstract void validateBidIteration() throws QuoteException;
    /**
     * @return 
     * 1)new term >=  original term
     * 2)new Billing frequency <= original Billing frequency
     * Billing frequency order in asc
     * Up front < Annually < Quarterly < Monthly
     * 3)new monthly Unit price >= original monthly Unit price * (1-1%)
     * @throws QuoteException
     */
    private boolean validateExistingParts(Set<String> errorMsgSet) throws QuoteException {
    	for (Iterator iterator = existingParts.iterator(); iterator.hasNext();) {
			QuoteLineItem currentQli = (QuoteLineItem) iterator.next();
			QuoteLineItem orgQli = (QuoteLineItem) originalPartsMap.get(getPartMappingKey(currentQli));
			if(Boolean.FALSE.equals
					(executeRule(RULE_SET_ID, EXIST_PART_RULE_ID, new Object[] { "currentQli", currentQli, "orgQli", orgQli, "errorMsgSet", errorMsgSet, "quote", quote }))){
				printLogBoth("validateExistingParts",orgQli,currentQli);
				return false;
			}
		}
    	return true;
    }
    
    /**
     * @return 
     * removed parts validation
     * @throws QuoteException
     */
    private boolean validateRemovedParts(Set<String> errorMsgSet) throws QuoteException {
    	for (Iterator iterator = removedParts.iterator(); iterator.hasNext();) {
			QuoteLineItem currentQli = (QuoteLineItem) iterator.next();
			if(Boolean.FALSE.equals
					(executeRule(RULE_SET_ID, REMOVED_PART_RULE_ID, new Object[] { "currentQli", currentQli, "errorMsgSet", errorMsgSet, "quote", quote }))){
				printLogCurrent("validateRemovedParts",currentQli);
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
    private boolean validateSameAttribute(Set<String> errorMsgSet) throws QuoteException {
		boolean hasSameAttrbt = false;
	   	for (Iterator iterator = newAddedParts.iterator(); iterator.hasNext();) {
	   		hasSameAttrbt = false;
			QuoteLineItem currentQli = (QuoteLineItem) iterator.next();
			for (Iterator iterator2 = originalParts.iterator(); iterator2.hasNext();) {
				QuoteLineItem orgQli = (QuoteLineItem) iterator2.next();
				if(Boolean.TRUE.equals
						(executeRule(RULE_SET_ID, SAME_ATTRBT_RULE_ID, new Object[] { "currentQli", currentQli, "orgQli", orgQli, "errorMsgSet", errorMsgSet, "quote", quote }))){
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
   private boolean validateDiscountInGrp(Set<String> errorMsgSet) throws QuoteException {
	   	for (Iterator iterator = newAddedParts.iterator(); iterator.hasNext();) {
	   		QuoteLineItem currentQli = (QuoteLineItem) iterator.next();
	   		if(oriGroupByPidMap.get(getPidGroupKey(currentQli)) == null){
	   			continue;
	   		}
	   		List<QuoteLineItem> samePidRvnStrmParts = oriGroupByPidMap.get(getPidGroupKey(currentQli));
			for (Iterator iterator2 = samePidRvnStrmParts.iterator(); iterator2.hasNext();) {
				QuoteLineItem orgQli = (QuoteLineItem) iterator2.next();
				if(Boolean.FALSE.equals
						(executeRule(RULE_SET_ID, SAAS_DISCOUNT_IN_GRP_RULE_ID, new Object[] { "currentQli", currentQli, "orgQli", orgQli, "errorMsgSet", errorMsgSet, "quote", quote }))){
					printLogBoth("validateDiscountInGrp",orgQli,currentQli);
					return false;
				}
			}
		}
	   	return true;
   }
   
   /**
    * @return new part can't be a discounted and on the "No Bid Iteration" part group
    * @throws QuoteException
    */
   private boolean validateGroup(Set<String> errorMsgSet) throws QuoteException {
	   	for (Iterator iterator = newAddedParts.iterator(); iterator.hasNext();) {
			QuoteLineItem currentQli = (QuoteLineItem) iterator.next();
			List<QuoteLineItem.PartGroup> partGroups = currentQli.getPartGroups();
			for (Iterator iterator2 = partGroups.iterator(); iterator2.hasNext();) {
				QuoteLineItem.PartGroup partGroup = (QuoteLineItem.PartGroup) iterator2.next();
				if(Boolean.FALSE.equals
						(executeRule(RULE_SET_ID, SAAS_GRP_RULE_ID, new Object[] { "currentQli", currentQli, "partGroup", partGroup, "errorMsgSet", errorMsgSet, "quote", quote }))){
					printLogCurrent("validateGroup",currentQli);
					return false;
				}
			}
		}
	   	return true;
   }
   
   /**
    * @return Expiration date can't be extended too far into the future
    * @throws QuoteException
    */
   private boolean validateQuoteHeader(Set<String> errorMsgSet) throws QuoteException {
	   if(Boolean.FALSE.equals
				(executeRule(RULE_SET_ID, QUOTE_HEADER_RULE_ID, new Object[] { "quoteHeader", quote.getQuoteHeader(), "dateUtil", new DateUtil(), "errorMsgSet", errorMsgSet, "quote", quote }))){
			return false;
		}
	   return true;
   }
   
   /**
    * @return validate new added parts
    * including new part discount <= 15%
    * new part should be a SaaS subscription, daily or overage part
    * @throws QuoteException
    */
   private boolean validateNewAddedParts(Set<String> errorMsgSet) throws QuoteException {
	   for (Iterator iterator = newAddedParts.iterator(); iterator.hasNext();) {
	   		QuoteLineItem currentQli = (QuoteLineItem) iterator.next();
	   		if(Boolean.FALSE.equals
					(executeRule(RULE_SET_ID, SAAS_NEW_ADDED_RULE_ID, new Object[] { "currentQli", currentQli, "errorMsgSet", errorMsgSet, "quote", quote }))){
	   			printLogCurrent("validateNewAddedParts",currentQli);
				return false;
			}
		}
	   return true;
   }
   
   /**
    * @return Bid Iteration cannot include a Terms and Conditions change.
    * @throws QuoteException
    */
   private boolean validateSpecialBidInfo(Set<String> errorMsgSet) throws QuoteException {
	   SpecialBidInfo sbInfo = quote.getSpecialBidInfo();
	   if(Boolean.FALSE.equals
				(executeRule(RULE_SET_ID, SAAS_SPCL_BID_RULE_ID, new Object[] { "sbInfo", sbInfo, "errorMsgSet", errorMsgSet, "quote", quote }))){
			return false;
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
