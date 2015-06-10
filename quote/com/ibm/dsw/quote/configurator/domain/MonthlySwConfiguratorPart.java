/**
 * 
 */
package com.ibm.dsw.quote.configurator.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.QuoteLineItemFactory;
import com.ibm.dsw.quote.configurator.contract.SubmittedMonthlySwConfiguratorContract;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @ClassName: MonthlySwConfiguratorPart
 * @author Frank
 * @Description: TODO
 * @date Dec 23, 2013 10:50:23 AM
 *
 */
@SuppressWarnings("rawtypes")
public class MonthlySwConfiguratorPart implements Serializable, Comparable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MonthlySwConfiguratorPart (MonthlySwActionConfiguratorPart actionPart){
		this.configuratorActionPart = actionPart;
	}
	
	public MonthlySwConfiguratorPart(SubmitConfiguratorPart submitConfiguratorPart){
		this.submitConfiguratorPart = submitConfiguratorPart;
	}
	
	public MonthlySwConfiguratorPart(){
		
	}
	
	protected static final LogContext logger = LogContextFactory.singleton().getLogContext();
	
	public static int maxSeqNum = 0;
	
	protected String partNum;
	
	protected Integer partQty;
	
	protected String partQtyStr;
	
	protected String seqNum;
	
	protected String partDscr;
	
	protected String billingFrequencyCode;
	
	protected Double price;
	
	protected String subId;
	
	protected String subIdDscr;
	
	protected String pid;
	
	protected String pidDscr;
	
	protected String brandId;
	
	protected String brandDscr;
	
	protected String wwideProdCodeDscr;
	
	protected Integer term;

	protected String termStr;

	protected Integer totalTerm;
	
	private Integer configuratorTerm;
	
	private boolean mustHaveQty;
	
	protected Double svpLevelA;	
	protected Double svpLevelB;
	protected Double svpLevelD;
	protected Double svpLevelE;
	protected Double svpLevelF;
	protected Double svpLevelG;
	protected Double svpLevelH;
	protected Double svpLevelI;
	protected Double svpLevelJ;
	protected Double svpLevelED;
	protected Double svpLevelGV;
	
	protected Integer tierQtyMeasre;
	
	protected String pricingTierModel;
	
	public boolean isSubscriptionPart(){
		return term != null;
	}
	
	
	protected MonthlySwActionConfiguratorPart configuratorActionPart ;
	
	private SubmitConfiguratorPart submitConfiguratorPart ;
	
    private transient List<MonthlySwSubscrptnConfiguratorPart> rampUpLineItems;
	
	@SuppressWarnings("unused")
	public class SubmitConfiguratorPart{
		
		private boolean checked;
		
		private Integer rampUpDuration;
		
		private String rampUpDurationStr;
		
		private int rampUpPeriod;
		
		private String rampUpPeriodStr;
		
		private int rampSeqNum;
		
		private boolean rampUpFlag;

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public Integer getRampUpDuration() {
			try{
				return Integer.parseInt(rampUpDurationStr);
			} catch(Exception e){
				return null;
			}
		}
		
		public int getRampUpDurationInt(){
			if (getRampUpDuration() == null){
				return 0;
			}
			return getRampUpDuration();
		}

		public void setRampUpDuration(Integer rampUpDuration) {
			this.rampUpDuration = rampUpDuration;
		}

		public int getRampUpPeriod() {
			if (StringUtils.isNotBlank(rampUpPeriodStr)){
				rampUpPeriod = Integer.parseInt(rampUpPeriodStr);
			}
			return rampUpPeriod;
		}

		public void setRampUpPeriod(int rampUpPeriod) {
			this.rampUpPeriod = rampUpPeriod;
		}

		public int getRampSeqNum() {
			return rampSeqNum;
		}

		public void setRampSeqNum(int rampSeqNum) {
			this.rampSeqNum = rampSeqNum;
		}

		public boolean isRampUpFlag() {
			return rampUpFlag;
		}

		public void setRampUpFlag(boolean rampUpFlag) {
			this.rampUpFlag = rampUpFlag;
		}
		
		
		
		public String getRampUpPeriodStr() {
			return rampUpPeriodStr;
		}

		public void setRampUpPeriodStr(String rampUpPeriodStr) {
			this.rampUpPeriodStr = rampUpPeriodStr;
		}

		public String getRampUpDurationStr() {
			return rampUpDurationStr;
		}

		public void setRampUpDurationStr(String rampUpDurationStr) {
			this.rampUpDurationStr = rampUpDurationStr;
		}

		public boolean isDeleted(){
			boolean isDelete = false;
			if (partQty == null || partQty.intValue() <= 0){
				 isDelete = true;
			}
			return isDelete;
		}
		
	}
	
	public boolean isMustHaveQty() {
		return mustHaveQty;
	}

	public void markMustHaveQty() {
		this.mustHaveQty = true;
	}


	public SubmitConfiguratorPart getSubmitConfiguratorPart() {
		return submitConfiguratorPart;
	}

	public void setSubmitConfiguratorPart(
			SubmitConfiguratorPart submitConfiguratorPart) {
		this.submitConfiguratorPart = submitConfiguratorPart;
	}

	public String getPartNum() {
		return partNum;
	}

	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}

	public Integer getPartQty() {
		return partQty;
	}

	public void setPartQty(Integer partQty) {
		this.partQty = partQty;
	}

	public String getPartQtyStr() {
		return partQtyStr;
	}

	public void setPartQtyStr(String partQtyStr) {
		this.partQtyStr = partQtyStr;
	}

	public String getPartDscr() {
		return partDscr;
	}

	public void setPartDscr(String partDscr) {
		this.partDscr = partDscr;
	}

	public String getBillingFrequencyCode() {
		return billingFrequencyCode;
	}

	public void setBillingFrequencyCode(String billingFrequencyCode) {
		this.billingFrequencyCode = billingFrequencyCode;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getSubId() {
		return subId;
	}

	public void setSubId(String subId) {
		this.subId = subId;
	}

	public String getSubIdDscr() {
		return subIdDscr;
	}

	public void setSubIdDscr(String subIdDscr) {
		this.subIdDscr = subIdDscr;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPidDscr() {
		return pidDscr;
	}

	public void setPidDscr(String pidDscr) {
		this.pidDscr = pidDscr;
	}

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public String getBrandDscr() {
		return brandDscr;
	}

	public void setBrandDscr(String brandDscr) {
		this.brandDscr = brandDscr;
	}

	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	public Integer getTotalTerm() {
		return totalTerm;
	}

	public void setTotalTerm(Integer totalTerm) {
		this.totalTerm = totalTerm;
	}

	public Double getSvpLevelA() {
		return svpLevelA;
	}

	public void setSvpLevelA(Double svpLevelA) {
		this.svpLevelA = svpLevelA;
	}

	public Double getSvpLevelB() {
		return svpLevelB;
	}

	public void setSvpLevelB(Double svpLevelB) {
		this.svpLevelB = svpLevelB;
	}

	public Double getSvpLevelD() {
		return svpLevelD;
	}

	public void setSvpLevelD(Double svpLevelD) {
		this.svpLevelD = svpLevelD;
	}

	public Double getSvpLevelE() {
		return svpLevelE;
	}

	public void setSvpLevelE(Double svpLevelE) {
		this.svpLevelE = svpLevelE;
	}

	public Double getSvpLevelF() {
		return svpLevelF;
	}

	public void setSvpLevelF(Double svpLevelF) {
		this.svpLevelF = svpLevelF;
	}

	public Double getSvpLevelG() {
		return svpLevelG;
	}

	public void setSvpLevelG(Double svpLevelG) {
		this.svpLevelG = svpLevelG;
	}

	public Double getSvpLevelH() {
		return svpLevelH;
	}

	public void setSvpLevelH(Double svpLevelH) {
		this.svpLevelH = svpLevelH;
	}

	public Double getSvpLevelI() {
		return svpLevelI;
	}

	public void setSvpLevelI(Double svpLevelI) {
		this.svpLevelI = svpLevelI;
	}

	public Double getSvpLevelJ() {
		return svpLevelJ;
	}

	public void setSvpLevelJ(Double svpLevelJ) {
		this.svpLevelJ = svpLevelJ;
	}

	public Double getSvpLevelED() {
		return svpLevelED;
	}

	public void setSvpLevelED(Double svpLevelED) {
		this.svpLevelED = svpLevelED;
	}

	public Double getSvpLevelGV() {
		return svpLevelGV;
	}

	public void setSvpLevelGV(Double svpLevelGV) {
		this.svpLevelGV = svpLevelGV;
	}

	public String getPricingTierModel() {
		return pricingTierModel;
	}

	public void setPricingTierModel(String pricingTierModel) {
		this.pricingTierModel = pricingTierModel;
	}

	public MonthlySwActionConfiguratorPart getConfiguratorActionPart() {
		return configuratorActionPart;
	}

	public void setConfiguratorActionPart(
			MonthlySwActionConfiguratorPart configuratorActionPart) {
		this.configuratorActionPart = configuratorActionPart;
	}

	public String getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(String seqNum) {
		this.seqNum = seqNum;
	}

	public String getWwideProdCodeDscr() {
		return wwideProdCodeDscr;
	}

	public void setWwideProdCodeDscr(String wwideProdCodeDscr) {
		this.wwideProdCodeDscr = wwideProdCodeDscr;
	}
	
	public String getPartKey(){
		return partNum + "_" + seqNum;
	}
	
	public String getPartKey(String partNum){
		return partNum;
	}
	
	
	public Integer getConfiguratorTerm() {
		return configuratorTerm;
	}

	public void setConfiguratorTerm(Integer configuratorTerm) {
		this.configuratorTerm = configuratorTerm;
	}
	
	public Integer getTierQtyMeasre() {
		return tierQtyMeasre;
	}

	public void setTierQtyMeasre(Integer tierQtyMeasre) {
		this.tierQtyMeasre = tierQtyMeasre;
	}
	
	public String getKey(){
		return this.partNum + "_" + this.seqNum;
	}

	/**
	 * delete parts from db
	 * @param monthlySwLineItems
	 * @throws TopazException
	 */
	public void deleteFromQuote(Map<String,MonthlySwLineItem> monthlySwLineItems) throws TopazException{
		deleteFromQuote(monthlySwLineItems,getPartKey());
	}
	
	/**
	 * 
	 * @param monthlySwLineItems
	 * @param key
	 * @throws TopazException
	 */
	public void deleteFromQuote(Map<String,MonthlySwLineItem> monthlySwLineItems ,String key)  throws TopazException{
		MonthlySwLineItem lineItem = monthlySwLineItems.get(key);
		
		if (lineItem == null){
			return;
		}
		
		monthlySwLineItems.remove(key);
		
		lineItem.delete();
	}

    /**
     * add part to db
     * 
     * @param configuratorPartFromDB
     * @param webQuoteNum
     * @param userID
     * @return TODO
     * @throws TopazException
     */
    public MonthlySwLineItem addPartToQuote(SubmittedMonthlySwConfiguratorContract submitContract,
            MonthlySwConfiguratorPart configuratorPartFromPage, MonthlySwConfiguratorPart configuratorPartFromDB)
            throws TopazException {
        MonthlySwLineItem monthlySwLineItem = addPartToQuote(submitContract, partNum);
        return monthlySwLineItem;

    }

    public MonthlySwLineItem addPartToQuote(SubmittedMonthlySwConfiguratorContract submitContract, String partNum)
            throws TopazException {
        MonthlySwLineItem qli = QuoteLineItemFactory.singleton().createMonthlySwLineItem(submitContract.getWebQuoteNum(),
                partNum, submitContract.getUserId());
        maxSeqNum = maxSeqNum + 1;
        qli.setSeqNum(maxSeqNum);
        qli.setDestSeqNum(maxSeqNum);
        qli.setProrateFlag(false);
        qli.setAssocdLicPartFlag(false);
		qli.setConfigrtnId(submitContract.getOrgConfigId());

        if (configuratorActionPart != null ) {
        	Integer refDocLineNum = configuratorActionPart.getRefDocLineNum();
        	if (refDocLineNum == null){
        		refDocLineNum = configuratorActionPart.getRelatedCotermLineItmNum();
        	}
        	qli.setRefDocLineNum(refDocLineNum);
        }
        return qli;
    }
	
	/**
	 * 
	 * @param monthlySwLineItemsMap
	 * @throws TopazException
	 */
	public MonthlySwLineItem getNeedUpdateMonthlySwLineItem(Map<String, MonthlySwLineItem> monthlySwLineItemsMap)throws TopazException{
		return monthlySwLineItemsMap.get(getPartKey());
	}
	
    public void update(MonthlySwConfiguratorPart configuratorPartFromPage, Map<String, MonthlySwLineItem> monthlySwLineItemsMap,
            SubmittedMonthlySwConfiguratorContract submitContract) throws TopazException {
        // do nothing, will do update operation in child class
    }
	
	public List<MonthlySwSubscrptnConfiguratorPart> getRampUpLineItems() {
		return rampUpLineItems;
	}
	
	public void setRampUpLineItems(List<MonthlySwSubscrptnConfiguratorPart> rampUpLineItems) {
		this.rampUpLineItems = rampUpLineItems;
	}

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Object o) {
        if (o instanceof MonthlySwConfiguratorPart) {
            MonthlySwConfiguratorPart monthlySwConfiguratorPart = (MonthlySwConfiguratorPart) o;
            if (Integer.parseInt(monthlySwConfiguratorPart.getSeqNum()) > Integer.parseInt(this.getSeqNum())) {
                return -1;
            }
        }
        return 0;
    }

	public String getTermStr() {
		return termStr;
	}

	public void setTermStr(String termStr) {
		this.termStr = termStr;
	}
	
//	public void addRampUpLineItem(MonthlySwSubscrptnConfiguratorPart rampUpLineItem, int rampSeqNum) {
//		if(rampUpLineItems == null){
//			rampUpLineItems = new ArrayList<MonthlySwSubscrptnConfiguratorPart>();
//		}
//		if(rampSeqNum == rampUpLineItems.size()){	//ie. 1=1
//			rampUpLineItems.add(rampUpLineItem);
//			
//		}else if(rampSeqNum > rampUpLineItems.size()){	//ie. 3>1
//			for(int i = rampUpLineItems.size();i<rampSeqNum;i++){
//				MonthlySwSubscrptnConfiguratorPart cp = new MonthlySwSubscrptnConfiguratorPart();
//				cp.setPartNum(this.getPartNum());
//				rampUpLineItems.add(cp);
//			}
//			rampUpLineItems.add(rampUpLineItem);
//			
//		}else if(rampSeqNum < rampUpLineItems.size()){	//ie. 1<3
//			rampUpLineItems.set(rampSeqNum, rampUpLineItem);
//			
//		}
//		if(term != null && rampUpLineItem.getrampUpDuration() != null){		////calculate term;
//			Integer duration = rampUpLineItem.getRampUpDuration();
//			term = new Integer(term.intValue() + duration.intValue());
//		}
//	}
//	
//	public void delRampUpLineItem(int rampSeqNum) {
//		if(rampUpLineItems == null){
//			return;
//		}
		
//		rampUpLineItems.remove(rampSeqNum);
//	}	
	
	
}
