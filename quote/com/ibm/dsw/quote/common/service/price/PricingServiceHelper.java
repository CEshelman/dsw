package com.ibm.dsw.quote.common.service.price;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import DswSalesLibrary.GetPricesInput;
import DswSalesLibrary.GetPricesOutput;
import DswSalesLibrary.HeaderIn;
import DswSalesLibrary.HeaderOut;
import DswSalesLibrary.ItemIn;
import DswSalesLibrary.ItemOut;
import DswSalesLibrary.PricingService;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.common.config.CommonServiceConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.MonthlySwLineItem;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteHeader;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.service.QuoteWSHandler;
import com.ibm.dsw.quote.common.service.price.rule.PricingRule;
import com.ibm.dsw.quote.common.util.CommonServiceUtil;
import com.ibm.dsw.quote.common.util.QuoteCommonUtil;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.dsw.quote.draftquote.util.sort.PartSortUtil;
import com.ibm.dsw.quote.log.util.QuoteLogContextLog4JImpl;
import com.ibm.dsw.quote.log.util.TimeTracer;
import com.ibm.dsw.wpi.java.util.ServiceLocator;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: May 21, 2007
 */

public abstract class PricingServiceHelper {
    
    ServiceLocator serviceLocator;

    protected Quote quote;

    /** the logging facility */
    protected static final LogContext logger = LogContextFactory.singleton().getLogContext();
    protected static final QuoteLogContextLog4JImpl wsLogger = (QuoteLogContextLog4JImpl)LogContextFactory.singleton().getLogContext();

    protected static final String BASELINE_PRICE_LEVEL = "B";

    protected String docCat = QuoteConstants.QUOTE_DOC_CAT; // default value

    protected PricingRule rule = null;

    protected PricingRequest pr;
    
    protected boolean isPriceOk = true;
    /**
     * Constructor
     * 
     * @param serviceEndpoint
     *            the URL where this service is located
     */
    public PricingServiceHelper(PricingRequest pr) {
        this.docCat = pr.getDocCat();
        this.quote = pr.getQuote();
        this.pr = pr;

    }

    public void setPricingRule(PricingRule rule) {
        this.rule = rule;
    }

    public String getPrice() throws Exception {
    	
    	 QuoteHeader header = quote.getQuoteHeader();
    	 
    	 if (header.isExpDateExtendedFlag()){
         	return null;
         }
    	     	 
    	TimeTracer tracer = TimeTracer.newInstance();
    	
    	String tranPriceLevel = null;
    	
        HeaderIn headerIn = mapQuoteHeader();

        ItemIn[] itemsIn = mapQuoteLineItemsToItemIn();

        if (serviceLocator == null){
            serviceLocator = new ServiceLocator();
        }
        GetPricesInput input = new GetPricesInput();
        input.setHeaderIn(headerIn);
        input.setItemsIn(itemsIn);
        
        
        if (itemsIn.length == 0) {
        	//since there is no items to send to sap
        	//just clear the transaction level/volumn discount code for draft quote
        	if (pr.isUpdatePriceLevel() && !quote.getQuoteHeader().isSubmittedQuote()) {
                header.setVolDiscLevelCode(null);
                header.setTranPriceLevelCode(null);
        	}
            logger.debug(this, "No line item should be sent to SAP");
            return tranPriceLevel;
        }
        
        tracer.stmtTraceStart("callWebService");
        GetPricesOutput output = callWebService(input);
        tracer.stmtTraceEnd("callWebService");
        
        HeaderOut headerOut = output.getHeaderOut();
        
        ItemOut[] outItems = output.getItemsOut(); 
        
        processPWSErrorMsg(headerOut);
        
        checkPriceAvaliability(outItems);
        
        
        if (pr.isUpdatePriceLevel()) {
            header.setVolDiscLevelCode(headerOut.getRSvpLvl());
            header.setTranPriceLevelCode(headerOut.getTSvpLvl());
            
            tranPriceLevel = headerOut.getTSvpLvl();
        }

        
        processPrice(outItems);
        
        header.setPriceStartDate(new Date());
        
        setLatamUpliftPct(header, headerOut);
        
        tracer.dump();
        
        return tranPriceLevel;
        
    }
    private void  checkPriceAvaliability(ItemOut [] outItems){
        
        //by default it is treated as having price error
        boolean result = true;
        if (outItems != null){
        for (int i = 0; i < outItems.length; i++) {
        	
            ItemOut item = outItems[i];
            
             
            QuoteLineItem qli = quote.getLineItemByDestSeqNum(item.getPartNum(), item.getItmNum().intValue());
            
            if(qli == null){
            	continue;
            }
            
            //negative logic
            if (!QuoteCommonUtil.isPricingError(item, qli)) {
                result = false;
            }
        }
        }
        
        this.isPriceOk = !result;
        
        if(!this.isPriceOk){
            logger.debug(this,"All Line Items have err flag");
        }
        

        
    }

    protected abstract HeaderIn mapQuoteHeader() throws Exception;

    protected HeaderIn mapQuoteHeaderBasicInfo() {

        HeaderIn headerIn = new HeaderIn();
        QuoteHeader header = quote.getQuoteHeader();
        
        setCtrctOrPriceLevel(headerIn, header);
        
        //XSP requirement GET parameter name as 'contractVariation' and Legacy use 'customerType'
        if (quote.getQuoteHeader().hasNewCustomer()) {
        	headerIn.setContractVariation(quote.getCustomer().getAgreementType());
        }

        //Country
        headerIn.setCountry(header.getPriceCountry().getCode3());
        //Currency
        headerIn.setCurrency(header.getCurrencyCode());
       
        
        //Document category
        if (header.isRenewalQuote()) {
            docCat = QuoteConstants.RQUOTE_DOC_CAT;
        } else {
            docCat = QuoteConstants.QUOTE_DOC_CAT;
        }
        headerIn.setDocCat(docCat);
        // the quote tool...
        //Line of business
        headerIn.setLOB(getLobCode());
        //Payer
        headerIn.setPayer(header.getPayerCustNum());
        //Price Date
        headerIn.setPrcDate(this.convertToYYYYMMDD(header.getModDate()));
        //Per discussion with Pushpa: don't send Price Level to SAP
        //headerIn.setPrcLvl(header.getVolDiscLevelCode());
        //Reseller
        headerIn.setReseller(header.getRselCustNum());

        
        if (header.getGsaPricngFlg() == 1) {
            //when "yes" radio button of GSA pricing is selected, GSA uplifts should be included in the price
        	if (quote.getCustomer().getGsaStatusFlag() && !header.hasExistingCustomer()){
                // if new/assigend ZFED customer on draft quote, send F to SAP
        		headerIn.setNoGsaFlag(PartPriceConstants.GSA_PRICING_FLAG_F);
        	}else{
        	    // if existing ZFED customer on draft quote, send blank to SAP
        	    headerIn.setNoGsaFlag(null);
        	}
        } else {
            // when "no" radio button of GSA pricing is selected
            // GSA uplifts should not be included in the prices, so send X to SAP
            headerIn.setNoGsaFlag(PartPriceConstants.GSA_PRICING_FLAG_X);
        }
        
        try {
            String salesOrg = this.getSalesOrgCode(header.getPriceCountry().getCode3(), getLobCode());
            headerIn.setSalesOrg(salesOrg);

        } catch (TopazException qe) {
            logger.warning(this, "Failed to lookup sales org, setting to null. Cause: " + qe);
        }

        //Sold to
        headerIn.setSoldTo(header.getSoldToCustNum());
        
        headerIn.setPaymentTermDays(new Integer(header.getPymTermsDays()));
        headerIn.setProposalValidityDays(new Integer(CommonServiceUtil.getValidityDays(header)));

        return headerIn;

    }
    
    private String getLobCode(){
        return quote.getQuoteHeader().getLob().getCode();
    }
    

    private void setCtrctOrPriceLevel(HeaderIn headerIn, QuoteHeader header){
        
        //Override transaction price level always comes in the first place if presented
       if(StringUtils.isNotBlank(header.getOvrrdTranLevelCode())){
            headerIn.setPrcLvl(header.getOvrrdTranLevelCode());
        
       } else if(header.isOEMQuote()){
           //If the OEM sales quote references a renewal quote then the WEB should send the contract number
           //in the header CNTRCT field and also send the renewal quote's price level in the price group
           //field in the header PRCLVL field.   
            
            if(StringUtils.isNotBlank(header.getRenwlQuoteNum())){
                headerIn.setContract(header.getContractNum());
                
                Customer cust = quote.getCustomer();
                if (cust != null && cust.getContractList() != null && cust.getContractList().size() > 0) {
                    Contract contract = (Contract) quote.getCustomer().getContractList().get(0);
                    String svpLevel = contract.getVolDiscLevelCode();
                    
                    headerIn.setPrcLvl(svpLevel);
                }
            } else {
                //If the OEM sales quote does not reference a renewal quote
                //then the WEB will not send a contract number but instead send a B (for BL pricing) in the 
                //header PRCLVL field . 
                headerIn.setPrcLvl(QuoteConstants.PRICE_LEVEL_B);
            }
            
        } else { 
            //If quote is not OEM
            if(QuoteCommonUtil.isCustWithOverrideSVP(quote)){
                headerIn.setPrcLvl(quote.getCustomer().getTransSVPLevel());
            } else {
                //Contract
                headerIn.setContract(header.getContractNum());
            }
        }
    }


    private String getSalesOrgCode(String cntryCode3, String sLOB) throws TopazException {
        return CommonServiceUtil.getSalesOrgCode(cntryCode3,sLOB);
 
    }

    /**
     * Makes the call to the PWS using the HeaderIn provided for the various
     * pricing types
     * 
     * @param headerIn
     *            the pricing method specific <code>HeaderIn</code>
     * @throws Exception
     */
    protected GetPricesOutput callWebService(GetPricesInput input) throws Exception {
        try {
            PricingService pricingService = (PricingService) serviceLocator.getServicePort(
                    CommonServiceConstants.PRICING_SERVICE_BINDING, PricingService.class);
            
            wsLogger.info(QuoteWSHandler.class, "Pricing for quote: " + quote.getQuoteHeader().getWebQuoteNum());
            GetPricesOutput output = pricingService.execute(input);
            HeaderOut headerOut = output.getHeaderOut();

            return output;

        } catch (Exception e) {
            logger.error(this, "Call Pricing Service error:" + e.getMessage());
            throw new Exception("An unexpected exception occurred while updating quote with pricing info. Cause: " + e);
        }
    }
    protected void checkDestSeqNum() throws Exception{
        // for the legacy production data the dest_seq_num could be -1, this method will correct the dest_seq_num .
        
        List lineItems = quote.getLineItemList();
        boolean needCreateDestSeqNum = false;
        for(Iterator iter = lineItems.iterator(); iter.hasNext();){
            QuoteLineItem lineItem = (QuoteLineItem) iter.next();
            if(lineItem.getDestSeqNum()<=0){
                logger.info(this, "Line item (" + lineItem.getPartNum() + "," + lineItem.getSeqNum() + 
                         ") has a dest seq num <=0");               
                needCreateDestSeqNum = true;
                break;
            }
        }        
        if(needCreateDestSeqNum){
            PartSortUtil.sort(quote);
        }
        
    }
    /**
     * Converts the <code>SapLineItem</code> to the service's
     * <code>ItemIn</code> data structure
     * 
     * @param sapLineItems
     *            the List of sap line items from the service
     * @return an array of <code>ItemIn</code> object
     */
    protected ItemIn[] mapQuoteLineItemsToItemIn() throws Exception {
        //temp container for these objects as we process them
        List itemsIn = new ArrayList();
        
        checkDestSeqNum();
        
        List lineItems = quote.getLineItemList();
        QuoteHeader header = quote.getQuoteHeader();
        
        ItemIn itemIn = null;
        
        boolean cmprssCvrageEnabled = quote.getQuoteHeader().getCmprssCvrageFlag();
        
        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem lineItem = (QuoteLineItem) lineItems.get(i);            
            
            if (shouldNotSendToSAP(lineItem)) {
            	//Prices for replaced parts are from original CA
            	if(!lineItem.isReplacedPart()){
            		lineItem.clearPrices();
            	}
                continue;
            }
            // allocate a item number : from 1 to n
            
            itemIn = new ItemIn();
            if (StringUtils.isBlank(header.getOvrrdTranLevelCode())) {
                // added for renewal quote pricing protection
                if (header.isRenewalQuote()) {
                    itemIn.setRefDocNum(header.getRenwlQuoteNum());

                    if (StringUtils.isBlank(header.getSapIntrmdiatDocNum())) {
                        // for draft renewal quote
                        if (lineItem.getSeqNum() < PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ) {
                            int refDocItemNum = lineItem.getRenewalQuoteSeqNum();
                            itemIn.setRefDocItemNum(new Integer(refDocItemNum));
                        }
                    } else {
                        // for submitted renewal quote
                        int refDocItemNum = lineItem.getSapLineItemSeqNum();
                        if (refDocItemNum > 0)
                            itemIn.setRefDocItemNum(new Integer(refDocItemNum));
                    }
                } else {
                    // for sales quote
                    if (StringUtils.isNotBlank(lineItem.getRenewalQuoteNum())) {
                        itemIn.setRefDocNum(lineItem.getRenewalQuoteNum());
                        itemIn.setRefDocItemNum(new Integer(lineItem.getRenewalQuoteSeqNum()));
                    }else if ((lineItem.isSaasPart() || lineItem.isMonthlySoftwarePart()) && lineItem.getIRelatedLineItmNum()>=0){ //DSW 10.5 Use  REF_ITM field for related item data
                    	itemIn.setRefDocItemNum(lineItem.getIRelatedLineItmNum());
                    }
                }
            }

            // the SAP service needs a sequence from 1 to n
            itemIn.setItmNum(lineItem.getDestSeqNum());

            itemIn.setPartNum(lineItem.getPartNum());
            itemIn.setQty(CommonServiceUtil.getPartQty(lineItem));
            
            itemIn.setStartDt(this.convertToYYYYMMDD(lineItem.getMaintStartDate()));
            itemIn.setEndDt(this.convertToYYYYMMDD(lineItem.getMaintEndDate()));
            
            setLineItemDiscOrOvrrdPrice(lineItem, itemIn);

            //set channel override discount if allow change margin or 
            //should auto set BP discount
            if(showChnlMargin() || QuoteCommonUtil.shouldSetELAAutoChnlDisc(quote)){
            	if(lineItem.isSaasDaily()){
            		// do nothing
            	}else if(lineItem.isMonthlySoftwarePart() && ((MonthlySwLineItem)lineItem).isMonthlySwDailyPart()){
            		// do nothing
            	}else if((lineItem.getChnlOvrrdDiscPct()!=null) ){ //Do not set PrtnrDisc for daily parts
                    itemIn.setPrtnrDiscPct(lineItem.getChnlOvrrdDiscPct());
                }
            }
            
            //Do compressed coverage processing for both draft and submitted quotes
            if(cmprssCvrageEnabled){
                processForCmprssCvrage(lineItem, itemIn);
            }
            
            itemIn.setBillFreq(lineItem.getBillgFrqncyCode());
            if(lineItem.isSaasSetUpPart()){
            	itemIn.setItemTerm(lineItem.getCumCvrageTerm());
            }else{
            	itemIn.setItemTerm(lineItem.getICvrageTerm());
            }
            
            postMapQuoteLineItem(lineItem, itemIn);
            
            itemsIn.add(itemIn);
        }
        //lets put it in the required return format (which is ItemIn[] ) and
        // continue
        ItemIn[] items = new ItemIn[itemsIn.size()];
        itemsIn.toArray(items);
        return items;
    }
    
    private void setLineItemDiscOrOvrrdPrice(QuoteLineItem lineItem, ItemIn itemIn){
    	if(lineItem.isSaasPart()){
    		
    		//Set up, subscription, human service parts
    		if(lineItem.isSaasSetUpPart() || lineItem.isSaasSubscrptnPart() || lineItem.isSaasProdHumanServicesPart()){
    			if(lineItem.getOvrrdExtPrice() != null){
    				itemIn.setExtPrc(lineItem.getOvrrdExtPrice());
    			} else if(lineItem.getLineDiscPct() != 0){
    				itemIn.setDiscPct(new Double(DecimalUtil.roundAsDouble(lineItem.getLineDiscPct(), 3)));
    			}
    		}
    		
    		if(lineItem.isSaasOnDemand() || lineItem.isSaasSetUpOvragePart()
    				   || lineItem.isSaasSubscrptnOvragePart()){
    			//Even if discount provided, it will be translated to Ovrrd unit price
    			if(lineItem.getOverrideUnitPrc() != null){
    				itemIn.setSpbdOveragePrc(lineItem.getOverrideUnitPrc());
    			}else if(lineItem.getLineDiscPct() != 0){
    				itemIn.setSpbdOveragePrc(QuoteCommonUtil.calculatePriceByDiscount(lineItem.getLineDiscPct(), lineItem.getLocalUnitProratedPrc()));
    			}
    		}
    	}else if(lineItem.isMonthlySoftwarePart()){
    		
    		//Set up, subscription, human service parts
    		if(((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnPart()){
    			if(lineItem.getOvrrdExtPrice() != null){
    				itemIn.setExtPrc(lineItem.getOvrrdExtPrice());
    			} else if(lineItem.getLineDiscPct() != 0){
    				itemIn.setDiscPct(new Double(DecimalUtil.roundAsDouble(lineItem.getLineDiscPct(), 3)));
    			}
    		}
    		
    		if(((MonthlySwLineItem)lineItem).isMonthlySwOnDemandPart()
    				   || ((MonthlySwLineItem)lineItem).isMonthlySwSubscrptnOvragePart()){
    			//Even if discount provided, it will be translated to Ovrrd unit price
    			if(lineItem.getOverrideUnitPrc() != null){
    				itemIn.setSpbdOveragePrc(lineItem.getOverrideUnitPrc());
    			}else if(lineItem.getLineDiscPct() != 0){
    				itemIn.setSpbdOveragePrc(QuoteCommonUtil.calculatePriceByDiscount(lineItem.getLineDiscPct(), lineItem.getLocalUnitProratedPrc()));
    			}
    		}
    	} else {
            if(!lineItem.isObsoletePart()){
    	        // if there is override extend price, ignore others
    	        if (lineItem.getOvrrdExtPrice() != null && pr.isSendOvrrdExtndPrice()) {
    	            itemIn.setExtPrc(lineItem.getOvrrdExtPrice());
    	        } else if (lineItem.getOverrideUnitPrc() != null) {
    	            // for some case , use may don't want to send override price to
    	            // SAP
    	            itemIn.setUnitPrc(lineItem.getOverrideUnitPrc());
    	
    	        } else if (lineItem.getLineDiscPct() != 0) {
    	            itemIn.setDiscPct(new Double(DecimalUtil.roundAsDouble(lineItem.getLineDiscPct(), 3)));
    	        }
            } else{
            	//for obsolete parts, never send discount or override extended price
            	itemIn.setUnitPrc(lineItem.getOverrideUnitPrc());
            }
    	}
    }
    
    protected void processForCmprssCvrage(QuoteLineItem qli, ItemIn itemIn){
        //If cmprss cvrage month is not set, return
        if(!qli.hasValidCmprssCvrageMonth()){
            return;
        }
        
        logger.debug(this, "Line item: destSeqNum=" + qli.getDestSeqNum() + " is eligible for compressed coverage");
        
        //Clear pricing fields
        itemIn.setUnitPrc(null);
        itemIn.setExtPrc(null);
        itemIn.setDiscPct(null);
        
        Date endDate = DateUtil.plusMonthMinusOneDay(
                                  DateUtil.parseDate(itemIn.getStartDt(), "yyyyMMdd"),
                                  qli.getCmprssCvrageMonth().intValue());
        
        if(logger instanceof QuoteLogContextLog4JImpl){
			if(((QuoteLogContextLog4JImpl)logger).isDebug(this)){		
				logger.debug(this, "Cmprss cvrage start date: " + itemIn.getStartDt() + ", end date: " + endDate);
			}
		}
        
        itemIn.setEndDt(convertToYYYYMMDD(endDate));
        
        itemIn.setDiscPct(qli.getCmprssCvrageDiscPct());
    }
        
    protected boolean showChnlMargin(){
        return PartPriceConfigFactory.singleton().allowChannelMarginDiscount(quote);
    }
    private boolean shouldNotSendToSAP(QuoteLineItem lineItem) {
        //if the part is a PVU part, and qty is null, don't send to SAP
        if (lineItem.getPartQty() == null 
        		&& !(PartPriceSaaSPartConfigFactory.singleton().isNoQty(lineItem))) {
            logger.debug(this, "Line item (" + lineItem.getPartNum() + "," + lineItem.getSeqNum() + ","
                    + lineItem.getSeqNum() + ")  qty is null");
            return true;
        }
        if((lineItem.getPartQty()!=null )&& (lineItem.getPartQty().intValue()==0)){
            logger.debug(this, "Line item (" + lineItem.getPartNum() + "," + lineItem.getSeqNum() + ","
                    + lineItem.getSeqNum() + ")  qty is 0");
            return true;
        }
        
        if(lineItem.isReplacedPart()){
        	logger.debug(this, "Line item (" + lineItem.getPartNum() + "," + lineItem.getSeqNum() + " is replaced");
        	return true;
        }
        
        return false;
    }

    protected void postMapQuoteLineItem(QuoteLineItem lineItem, ItemIn itemIn) {
        // do nothing

    }

    protected abstract void processPrice(ItemOut[] itemOut) throws Exception;

    /**
     * A helper method to format the date the way SAP wants it
     * 
     * @param date
     *            the date to format
     * @return the date as a string in YYYYMMDD format...for example, 5/1/2007
     *         is 20070501
     */
    protected String convertToYYYYMMDD(Date date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            logger.debug(this, " PWSHelper >> Formatting date " + date.toString() + " to YYYMMDD");
            return formatter.format(date);
        } catch (Exception e) {
            return null;
        }
    }
    
    protected void processPWSErrorMsg(HeaderOut headerOut) throws Exception{
    	if (headerOut.getErrFlag() || StringUtils.isNotBlank(headerOut.getErrCode()) ){
    		throw new Exception (headerOut.getErrMsg());
    	}
    	
    }

    /*protected QuoteLineItem getSapLineItem(String partNum, int seqNum) {

        List lineItems = quote.getLineItemList();
        for (int i = 0; i < lineItems.size(); i++) {
            QuoteLineItem item = (QuoteLineItem) lineItems.get(i);
            if (item.getPartNum().endsWith(partNum) && (item.getDestSeqNum() == seqNum)) {
                return item;
            }
        }
        return null;

    }*/

    /**
     * @return Returns the isPriceOk.
     */
    public boolean isPriceOk() {
        return isPriceOk;
    }
    
    
    /**
     * @param serviceLocator The serviceLocator to set.
     */
    void setServiceLocator(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }
    
    protected abstract void setLatamUpliftPct(QuoteHeader header, HeaderOut headerOut);
}
