package com.ibm.dsw.quote.draftquote.viewbean.helper;

import is.domainx.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.util.DecimalUtil;
import com.ibm.dsw.quote.base.util.StringEncoder;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.domain.ChargeableUnit;
import com.ibm.dsw.quote.common.domain.PartPriceAppliancePartConfigFactory;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.PartsPricingConfiguration;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.common.util.GrowthDelegationUtil;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>PartTableViewBean</code>
 *
 *
 * @author: xiuliw@cn.ibm.com
 *
 * Creation date: 2007-4-3
 */
public class PartTable extends PartPriceCommon {
    private Locale locale;

    public PartTable(Quote quote) {
        super(quote);
    }

    public PartTable(Quote quote, Locale locale) {
        super(quote);
        this.locale = locale;
    }

    public PartTable(Quote quote, User user, Locale locale) {
        super(quote, user, locale);
        this.locale = locale;
    }

    /**
     *
     * @return true if any parts have been selected
     */
    public boolean showPartTableSection() {
        if (hasLineItems()) {
            return true;
        }

        return false;
    }

    /**
     * Added by Andy
     *
     * @param lineItem
     * @param cookie
     * @return
     */
    public boolean showQuantityField(QuoteLineItem lineItem, Cookie cookie) {
        if (isPA() || isPAE() || isFCT() || isOEM()) {
            if (lineItem.isPvuPart()) {
                boolean usePVUCalculator = Boolean.valueOf(QuoteCookie.getMandatoryFlag(cookie)).booleanValue();
                boolean isQtyBlank = (lineItem.getPartQty() == null);
                boolean showPVQField = !(usePVUCalculator && isQtyBlank);

                boolean notReactivable = (lineItem.isObsoletePart() && !lineItem.canPartBeReactivated());
                //for PVU part that is EOl'ed that can't be reactivated, we should show the quantity input box
                //so that sales rep can enter quantity 0 to delete part
                return (showPVQField || notReactivable);
            } else {
                //return isQuantitySet(lineItem);
                return true;
            }

        }
        return false;

    }

    public boolean isQuantityFieldEditable(QuoteLineItem lineItem, Cookie cookie) {

        if (isPA() || isPAE() || isOEM()) {

            if (lineItem.isPvuPart()) {
                boolean usePVUCalculator = Boolean.valueOf(QuoteCookie.getMandatoryFlag(cookie)).booleanValue();

                return !usePVUCalculator;
            } else {
                return true;
            }

        }
        return false;

    }

    /**
     * Added by lee,March 30,2007
     *
     * @return true if Chargeable unit to be shown
     */
    public boolean showChargeableUnit(QuoteLineItem lineItem) {
        //if has charge unit, return true
    	if(lineItem.getChargeableUnits().length > 0){
    		return true;
    	}
        return false;
    }

    /**
     * Added by lee,March 30,2007
     *
     * @return chargeable unit of given part
     */
    public String getChargeableUnit(QuoteLineItem lineItem) {
        String chargeableUnit = "";
        ChargeableUnit[] chargeableUnits = lineItem.getChargeableUnits();
        int len = chargeableUnits.length;

        for (int i=0;i<len;i++) {
            ChargeableUnit chargeableUnitObj = chargeableUnits[i];
            int qty = chargeableUnitObj.getQuantity();
            String desc = chargeableUnitObj.getDesc();

            if (i>0) {
                chargeableUnit += ",";
            }

            if (desc != null) {
                if (qty > 1) {
                    chargeableUnit += String.valueOf(qty) + " " + desc;
                }
                else {
                    chargeableUnit += desc;
                }
            }
        }

        return chargeableUnit;
    }

    /**
     * Added by lee,March 30,2007
     *
     * @return true if caculate quantity link to be shown
     */
    public boolean showCaculateQuantityLink(QuoteLineItem lineItem) {
        if (isPAE() || isPA() || isFCT() || isOEM()) {
            if (lineItem.isPvuPart()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Added by lee,March 30,2007
     *
     * @return true if item points to be shown
     */
    public boolean showItemPoints(QuoteLineItem lineItem) {
        //If part is a contract part, and line of business is Passport
        // Advantage
        return (isPA() || isPAE() || isSSP()) && isContractPart(lineItem);
    }

    public boolean showDiscountPercent(QuoteLineItem lineItem) {
    	
    	//if the quote is bid iterator part, don't show discount;
    	//if( quote.getQuoteHeader() != null && quote.getQuoteHeader().isBidIteratnQt())
    	//	return false;
    	
    	return showOvrdUnitPriceOrDiscInputBox(lineItem);
    }

    public boolean showOverrideUnitPrice(QuoteLineItem lineItem) {
    	return showOvrdUnitPriceOrDiscInputBox(lineItem);
    }

    private boolean showOvrdUnitPriceOrDiscInputBox(QuoteLineItem lineItem){
        if(lineItem.isObsoletePart()){
            return true;
        }

        String lob = quote.getQuoteHeader().getLob().getCode();
        String partTypeCode = lineItem.getPartTypeCode();
        return PartPriceConfigFactory.singleton().allowOvrdUnitPriceOrDisc(lob,partTypeCode);
    }

    public boolean showTotalPoints(QuoteLineItem lineItem) {
        if ((isPA() || isPAE()) && isContractPart(lineItem)) {
            if ((lineItem.getPartQty() != null) && (lineItem.getPartQty().intValue() != 0)) {
                return true;
            }
        }
        return false;
    }

    public boolean showEntitledPriceInput(QuoteLineItem lineItem){
    	if(lineItem.isObsoletePart() && !lineItem.isHasEolPrice()
				&& (lineItem.canPartBeReactivated())){
    		return true;
    	}

    	return false;
    }

    public boolean showTotalPrice(QuoteLineItem lineItem) {
        if (isQuantitySet(lineItem)) {
            return true;
        }
        return false;
    }

    public String getLineItemBidUnitPrc(QuoteLineItem lineItem, boolean isPriceUnAvaialbe) {
        if (this.showLineitemPrice(lineItem, isPriceUnAvaialbe) && (lineItem.getLocalUnitProratedDiscPrc()!=null)) {
        	if(lineItem.isSetLineToRsvpSrpFlag()){
        		return DecimalUtil.format(GrowthDelegationUtil.getProratedRSVPPrice(lineItem),2);
        	}else{
        		return getFormattedPriceByPartType(lineItem, lineItem.getLocalUnitProratedDiscPrc());
        	}
        	
        } else {
            return "";
        }
    }





    public String getUnitPrice(QuoteLineItem lineItem, boolean isPriceUnAvaialbe) {
        if (isPriceUnAvaialbe
                || lineItem.getLocalUnitPrc()==null
                || (lineItem.isPvuPart() && (lineItem.getPartQty() == null))
                || (quote.getQuoteHeader().isRenewalQuote() && lineItem.getPartQty() != null && lineItem.getPartQty()
                        .intValue() == 0)) {
            return "";
        }

        return this.getFormattedPriceByPartType(lineItem, lineItem.getLocalUnitPrc());
    }

    /**
     * Added by lee,March 30,2007
     *
     * @param lineItem
     * @return part description
     */
    public String getPartDescription(QuoteLineItem lineItem) throws Exception{
        return StringEncoder.textToHTML(lineItem.getPartDesc());
    }



    /**
     *
     * @return
     */
    public boolean showRQOpenQty(QuoteLineItem lineItem) {
        if(lineItem.getSeqNum() >= PartPriceConstants.RQ_MANUALLY_ADDED_PART_SEQ)
        {
            return false;
        }
        if (quote.getQuoteAccess().isCanEditRQ()
                && (quote.containsWebPrimaryStatus(PartPriceConstants.E0005) || (quote
                        .containsWebPrimaryStatus(PartPriceConstants.E0006)))) {
            return true;
        }
        return false;
    }

    /**
     * @param qli
     * @return
     */
    public String getStartDateText(QuoteLineItem qli) {
        return DateUtil.formatDate(qli.getMaintStartDate(), DateUtil.PATTERN1, locale);
    }


    /**
     * @param qli
     * @return
     */
    public String getEndDateText(QuoteLineItem qli) {
        return DateUtil.formatDate(qli.getMaintEndDate(), DateUtil.PATTERN1, locale);
    }

    public String getRQOriginalStartDateText(QuoteLineItem item) {
        String startDateTxt = DateUtil.formatDate(item.getOrigStDate());

        if (null == startDateTxt) {
            startDateTxt = "";
        }
        return startDateTxt;
    }

    public String getRQOriginalEndDateText(QuoteLineItem item) {
        String endDateTxt = DateUtil.formatDate(item.getOrigEndDate());

        if (null == endDateTxt) {
            endDateTxt = "";
        }
        return endDateTxt;
    }



    public String getLineItemBpExtendedPrc(QuoteLineItem lineItem, boolean isPriceUnAvaialbe) {
        if (this.showLineitemPrice(lineItem, isPriceUnAvaialbe) && (lineItem.getChannelExtndPrice() != null)) {
            return getFormattedPriceByPartType(lineItem, lineItem.getChannelExtndPrice());
        } else {
            return DraftQuoteConstants.BLANK;
        }
    }

    public String getLineItemBpRate(QuoteLineItem lineItem, boolean isPriceUnAvaialbe) {
        if (this.showLineitemPrice(lineItem, isPriceUnAvaialbe)) {
        	String strChannelExtndPrice = "";
        	String strChannelUnitPrice = "";
        	if(lineItem.getChannelExtndPrice() != null){
        		strChannelExtndPrice = getFormattedPriceByPartType(lineItem, lineItem.getChannelExtndPrice());
    		}else {
    			strChannelExtndPrice = DraftQuoteConstants.BLANK;
            }
        	if(lineItem.getChannelUnitPrice() != null){
        		strChannelUnitPrice = getFormattedPriceByPartType(lineItem, lineItem.getChannelUnitPrice());
    		}else {
    			strChannelUnitPrice = DraftQuoteConstants.BLANK;
            }
        	if(lineItem.isSaasPart() || lineItem.isMonthlySoftwarePart()){
        		if(lineItem.isSaasTcvAcv()){
        			return strChannelExtndPrice;
        		}else{
        			return strChannelUnitPrice;
        		}
        	}else{
        		return strChannelUnitPrice;
        	}
        } else {
            return DraftQuoteConstants.BLANK;
        }
    }


    public String getStdBPDisc(QuoteLineItem item){
    	return DecimalUtil.formatTo5Number(item.getChnlStdDiscPct().doubleValue()) + "%";
    }

	public boolean showCoTerm(PartsPricingConfiguration configrtn){
		if(!PartPriceConstants.ConfigrtnActionCode.ADD_TRD.equals(configrtn.getConfigrtnActionCode())
				&& !PartPriceConstants.ConfigrtnActionCode.FCT_TO_PA_FNL.equals(configrtn.getConfigrtnActionCode()) ){
        	List lineItems = (List)quote.getPartsPricingConfigrtnsMap().get(configrtn);
        	if(lineItems != null && lineItems.size() > 0){
        		//Don't show coterm options if configuration cotains ramp up parts
        		for(Iterator it = lineItems.iterator(); it.hasNext(); ){
        			QuoteLineItem qli = (QuoteLineItem)it.next();
        			if(qli.getRampUpLineItems() != null && qli.getRampUpLineItems().size() > 0){
        				return false;
        			}
        		}
        	}

			return !showClearText4CA();
        }
		return false;
	}

    /**
     * @return
     * check if show the clear text for service agreement "New billing agreement"
     * if there is only one entry "New billing agreement" for the dropdown list, return true
     * else return false
     */
    public boolean showClearText4CA(){
    	if(quote.getPartsPricingConfigrtnsList() == null || quote.getPartsPricingConfigrtnsList().size() == 0){
    		return true;
    	}
    	PartsPricingConfiguration configrtn = (PartsPricingConfiguration)quote.getPartsPricingConfigrtnsList().get(0);
    	List caList = configrtn.getChargeAgreementList();
    	if(caList == null || caList.size() == 0){
    		return true;
    	}
    	return false;
    }

	 /**
	  * Get Applicance association list. created by Shaq.
	  * @param lineItem
	  * @return
	  */
	public Collection getApplianceAssociations(QuoteLineItem qli) {
		Collection associations = new ArrayList();
		ApplicationContext context = ApplicationContextFactory.singleton().getApplicationContext();
		//String relatedSoftwareDefault = context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale,PartPriceViewKeys.APPLIANCE_RELATED_SOFTWARE_DEFAULT);
		String applncNotOnQuote = context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale,PartPriceViewKeys.APPLIANCE_NOT_ON_QUOTE);
		String defaultValue=context.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale,PartPriceViewKeys.APPLIANCE_SELECTE_ONE);


		addMainAppliancePartToAssociation(qli, associations, applncNotOnQuote,defaultValue);
		addUpgradeAppliancePartToAssociation(qli, associations,defaultValue);


		return associations;

	}

	/**
	 * 1.add first option select one 2.add Appliance not on quote to drop down
	 * .For Appliance Upgrade, Transceiver & Renewal Parts 3.add Not associated
	 * with an appliance to drop down . For related software 3.add related
	 * Appliance Id with current quote to drop down
	 *
	 * @param qli
	 * @param associations
	 * @param relatedSoftwareDefault
	 * @param applncNotOnQuote
	 */
	private void addMainAppliancePartToAssociation(QuoteLineItem qli,
			Collection associations, 
			String applncNotOnQuote, String defaultValue) {

		List applncMains = quote.getApplncMains();
		if (hasApplncOwnerShipPart()){
			applncMains.addAll(quote.getApplncOwnerShipParts());
		}

		String selected = getSelectedDefaultOptions(qli);

		// add select one
		associations.add(new SelectOptionImpl(defaultValue, PartPriceConstants.APPLNC_SELECT_DEFAULT, PartPriceConstants.APPLNC_SELECT_DEFAULT.equals(selected)));

		associations.add(new SelectOptionImpl(applncNotOnQuote, PartPriceConstants.APPLNC_NOT_ON_QUOTE,PartPriceConstants.APPLNC_NOT_ON_QUOTE.equals(selected)));

		SortedSet<String> applianceIds = new TreeSet<String>();
		Map<String,String> appAssociations = new HashMap<String,String>();
		for (int i = 0; i < applncMains.size(); i++) {

			QuoteLineItem lineItem = (QuoteLineItem) applncMains.get(i);
			String applianceId = lineItem.getApplianceId();
			if (applianceId == null)
				continue;
			String applianceLabel = applianceId;
			if (StringUtils.isNotBlank(lineItem.getCcDscr()))
			{
				applianceLabel = applianceLabel +"   ("+lineItem.getCcDscr()+")";
			}
			applianceIds.add(applianceId);
			appAssociations.put(applianceId, applianceLabel);
		}
		for (String applianceId: applianceIds)
		{
			String applianceLabel = appAssociations.get(applianceId);
			associations.add(new SelectOptionImpl(applianceLabel, applianceId,applianceId.equals(selected)));
		}
	}

	private void addUpgradeAppliancePartToAssociation(QuoteLineItem qli,
			Collection associations, String defaultValue) {

		List upgradeParts = quote.getApplncUpgradeParts();
		String selected = getSelectedDefaultOptions(qli);

		SortedSet<String> applianceIds = new TreeSet<String>();
		Map<String,String> appAssociations = new HashMap<String,String>();
		for (int i = 0; i < upgradeParts.size(); i++) {
			QuoteLineItem lineItem = (QuoteLineItem) upgradeParts.get(i);
			String applianceId = lineItem.getApplianceId();
			if (applianceId == null)
				continue;
			String applianceLabel = applianceId;
			if (StringUtils.isNotBlank(lineItem.getCcDscr()))
			{
				applianceLabel = applianceLabel +"   ("+lineItem.getCcDscr()+")";
			}
			applianceIds.add(applianceId);
			appAssociations.put(applianceId, applianceLabel);
		}
		for (String applianceId: applianceIds)
		{
			String applianceLabel = appAssociations.get(applianceId);
			associations.add(new SelectOptionImpl(applianceLabel, applianceId,applianceId.equals(selected)));
		}
	}

    /**
     * 1. appliance Id is not null,selected appliance Id <br>
     * 2. appliance Id is null , part type is related software, selected Not associated with an appliance <br>
     * 3. appliance id is null, part type is not related software, selected Appliance not on quote <br>
     * 4. if MTM is not null, selecte APPLNC_NOT_ON_QUOTE
     * 
     * @param qli
     * @param applncMains
     * @param relatedSoftwareDefault
     * @return
     */
	private String getSelectedDefaultOptions(QuoteLineItem qli) {
		String selected = PartPriceConstants.APPLNC_SELECT_DEFAULT;

        // appliance Id is not null , selected appliance Id
        if (StringUtils.isNotBlank(qli.getApplianceId())) {
            selected = qli.getApplianceId();
        } else if (qli.isApplianceRelatedSoftware()) {// parts is related software
        	selected = PartPriceConstants.APPLNC_NOT_ON_QUOTE;
        }

        return selected;
	}

	public boolean showApplianceDropDown(QuoteLineItem lineItem) {
		// If the line item is in reference to a renewal quote do not show the
		// appliance ID drop down. The MTM/Serial # should be inherited from the
		// renewal quote and can't be changed.

		if (lineItem.isReferenceToRenewalQuote()) {
			return false;
		}

		PartPriceAppliancePartConfigFactory factory = PartPriceAppliancePartConfigFactory.singleton();
		return factory.displayApplianceIdDropdown(lineItem) && (hasApplncMainPart() || hasApplncUpgradePart() || hasApplncOwnerShipPart()|| (hasApplncMainPart()&&lineItem.isApplianceRelatedSoftware()));
	}

	public boolean isFederalCustomer(){
		if(quote.getCustomer() != null){
			return quote.getCustomer().getGsaStatusFlag();
		}
		return false;
	}

	private boolean hasApplncMainPart(){
		return quote.applncMains != null && quote.applncMains.size()>0;
	}

	private boolean hasApplncUpgradePart(){
		return quote.applncUpgradeParts != null && quote.applncUpgradeParts.size()>0;
	}

	private boolean hasApplncOwnerShipPart(){
		return quote.getApplncOwnerShipParts() != null && quote.getApplncOwnerShipParts().size()>0;
	}

	public boolean showApplianceAssociationMsgDraft(QuoteLineItem lineItem) {
		return lineItem.isApplianceRelatedSoftware();
	}

	public boolean showApplncAdditionalYear(QuoteLineItem lineItem) {
		return lineItem.isApplncMain()
				|| lineItem.isApplncServicePack()
				|| lineItem.isApplncServicePackRenewal()
				|| lineItem.isApplncTransceiver()
				|| lineItem.isApplncUpgrade()
				|| lineItem.isApplncRenewal();
	}


	/**
	 * judge whether renewal model is showing in configuration
	 * @param lineItem
	 * @return
	 */
	public boolean showRenwlModeForConfigrtn(List subSaasLineItems){
		boolean isShow = false;

		if (subSaasLineItems == null) return false;
		for (Object obj : subSaasLineItems) {
			QuoteLineItem lineItem = (QuoteLineItem) obj;
			// when  subscription  has a renewal model not show, configuration is not show
			if (showRenwlModeForSubscrptn(lineItem)) {
				isShow = true;
				break;
			}
		}


		return isShow;
	}

    public boolean applianceAssoRowIsNotBlank(QuoteLineItem lineItem, boolean isDraftQuote, boolean isSalesQuote) {
        if (showApplianceAssociationMsgDraft(lineItem)) {
            return true;
        } else if (showApplianceDropDown(lineItem)) {
            return true;
        } else if (showApplianceMtm(lineItem, isDraftQuote, isSalesQuote)) {
            return true;
        }
        return false;
    }
    

    
    public boolean isMandatorySerialNum(QuoteLineItem lineItem) {
     	return lineItem.isMandatorySerialNum();
    }
    
  
}