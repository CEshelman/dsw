/*
 * Created on 2007-3-29
 *
 */
package com.ibm.dsw.quote.partdetail.viewbean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.StringEncoder;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.config.PartPriceConstants;
import com.ibm.dsw.quote.common.config.PartPriceConstants.PartnerPriceType;
import com.ibm.dsw.quote.common.domain.PartPriceSaaSPartConfigFactory;
import com.ibm.dsw.quote.partdetail.config.PartDetailsMessageKeys;
import com.ibm.dsw.quote.partdetail.config.PartDetailsParamKeys;
import com.ibm.dsw.quote.partdetail.domain.PartPriceDetail;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * @author Administrator
 *
 */
public class PartDetailsViewBean extends BaseViewBean {
    /**
	 *
	 */
	private static final long serialVersionUID = 8899674301135527799L;

	private PartPriceDetail detail;
    private String lob;
    private String renewal;
    private String submitted;
    private String quoteNum;
    private boolean showEventBaseBiling;

    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        detail = (PartPriceDetail)params.getParameter(PartDetailsParamKeys.PART_PRICE_DETAILS_OBJECT);
        lob = params.getParameterAsString(ParamKeys.PARAM_LINE_OF_BUSINESS);
        renewal = params.getParameterAsString(PartDetailsParamKeys.RENEWAL);
        submitted = params.getParameterAsString(PartDetailsParamKeys.SUBMITTED);
        quoteNum = params.getParameterAsString(ParamKeys.PARAM_QUOTE_NUM);
        showEventBaseBiling = params.getParameterAsBoolean(PartDetailsParamKeys.SHOW_EVENT_BASE_BILING);
        
    }
    public PartPriceDetail getPartPriceDetail() {
        return detail;
    }

    public String getLob() {
        return StringEncoder.textToHTML(lob);
    }

    public String getRenewal() {
        return renewal;
    }

    public String getSubmitted() {
        return submitted;
    }
    
	public boolean isSaasPart() {
		return detail.isSaasPartFlag();
	}

	public boolean isMonthlyPart() {
		return detail.isMonthlyPartFlag();
	}

    /**
     * Getter for showEventBaseBiling.
     * @return the showEventBaseBiling
     */
    public boolean isShowEventBaseBiling() {
        return this.showEventBaseBiling;
    }
    public String getQuoteNum() {
        return StringEncoder.textToHTML(quoteNum);
    }

    public boolean showResellerAuthorizationRow(){
        return QuoteConstants.LOB_PA.equals(lob)
                || QuoteConstants.LOB_PAE.equals(lob);
    }

    public boolean isPartControlled(){
        return detail.isPartCtrlld();
    }

    public String getPartNumber(){
    	return StringEncoder.textToHTML(detail.getPartNumber());
    }

    public List getPriceDetailMapKeys(){
    	if(detail.getPriceDetailMap() != null){
    		List list = new ArrayList();
    		list.addAll(detail.getPriceDetailMap().keySet());
    		Collections.sort(list);
    		return list;
    	}
    	return null;
    }

    public List getDsPriceMapKes(){
    	if (detail.getDsPriceMap() != null){
    		List list = new ArrayList();
    		list.addAll(detail.getDsPriceMap().keySet());
    		Collections.sort(list);
    		return list;
    	}
    	return null;
    }

    public double getPartPrice(Object priceDetailMapKey, int priceDetailListIndex){
    	if(detail.getPriceDetailMap() != null){
    		List priceDetailList = (List)detail.getPriceDetailMap().get(priceDetailMapKey);
    		if(priceDetailList != null && priceDetailList.size() > priceDetailListIndex){
    			return ((Double) priceDetailList.get(priceDetailListIndex)).doubleValue();
    		}
    	}
    	return 0.00;
    }

    public double getDsPartPrice(Object dsPriceMapKey, int dsPriceListIndex){
    	if (detail.getDsPriceMap() != null){
    		List dsPriceList =(List) detail.getDsPriceMap().get(dsPriceMapKey);
    		if (dsPriceList != null && dsPriceList.size() > dsPriceListIndex){
    			return ((Double)dsPriceList.get(dsPriceListIndex)).doubleValue();
    		}
    	}
    	return 0.00;
    }

    public String getMessageKey(int index){
    	String messagesKey = "";
    	switch (index) {
		case 0:
			messagesKey = PartDetailsMessageKeys.SRP_PRICE_LABEL;
			break;
		case 1:
			messagesKey = PartDetailsMessageKeys.BL_PRICE_LABEL;
			break;
		case 2:
			messagesKey = PartDetailsMessageKeys.LEVEL_D_PRICE_LABEL;
			break;
		case 3:
			messagesKey = PartDetailsMessageKeys.LEVEL_E_PRICE_LABEL;
			break;
		case 4:
			messagesKey = PartDetailsMessageKeys.LEVEL_F_PRICE_LABEL;
			break;
		case 5:
			messagesKey = PartDetailsMessageKeys.LEVEL_G_PRICE_LABEL;
			break;
		case 6:
			messagesKey = PartDetailsMessageKeys.LEVEL_H_PRICE_LABEL;
			break;
		case 7:
			messagesKey = PartDetailsMessageKeys.LEVEL_I_PRICE_LABEL;
			break;
		case 8:
			messagesKey = PartDetailsMessageKeys.LEVEL_J_PRICE_LABEL;
			break;
		case 9:
			messagesKey = PartDetailsMessageKeys.GV_PRICE_LABEL;
			break;
		case 10:
			messagesKey = PartDetailsMessageKeys.ED_PRICE_LABEL;
			break;
		default:
			break;
		}
    	return messagesKey;
    }

    public int getLoopSize(){
    	int size = 1;
    	if(getLob()!=null && ("PA".equals(getLob()) || "PAE".equals(getLob())) || "OEM".equalsIgnoreCase(getLob())){
    		size = 11;
    	}
    	return size;
    }

    public int getLoopSizeForSWPGS(boolean isSRP){
    	int size = getLoopSize();
    	if (size == 11 && !isSRP){
    		size = 10;
    	} else {
    		size = 1;
    	}

    	return size;
    }

    /**
     * @return
     * String
     * get the type of tiered pricing description
     */
    public String getTieredPricingTypeDesc(){
    	String pricngTierMdl = detail.getPricngTierMdl();
    	String messageKey = "";
    	if(PartPriceConstants.SaaSPricngTierMdl.UP_TO.equals(pricngTierMdl)){
    		messageKey = PartDetailsMessageKeys.TYPE_TIERED_PRICING_UP_TO;
    	}
    	else if(PartPriceConstants.SaaSPricngTierMdl.STEP.equals(pricngTierMdl)){
    		messageKey = PartDetailsMessageKeys.TYPE_TIERED_PRICING_STEP;
    	}
    	else if(PartPriceConstants.SaaSPricngTierMdl.GRANULAR.equals(pricngTierMdl)){
    		messageKey = PartDetailsMessageKeys.TYPE_TIERED_PRICING_GRANULAR;
    	}
    	else if(PartPriceConstants.SaaSPricngTierMdl.ON_DEMAND.equals(pricngTierMdl)){
    		messageKey = PartDetailsMessageKeys.TYPE_TIERED_PRICING_ON_DEMAND;
    	}
    	return getI18NString(I18NBundleNames.PART_DETAILS_BASE, messageKey);
    }

    public String getTieredPricingHelpInfo(){
    	return getI18NString(I18NBundleNames.PART_DETAILS_BASE, PartDetailsMessageKeys.TIERED_HELP_INFO);
    }

    public String getI18NString(String baseName, String key){
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();

        String value = appCtx.getI18nValueAsString(baseName, locale, key);

        if (value == null || value.equals("")) {
            value = key;
        }

        return value;
    }

    public String getTieredHeader(List priceDetailMapKeys, int index){
    	if(priceDetailMapKeys.get(index) == null){
    		return "";
    	}
    	//if up to tier, show UP TO 500/UP TO 1000, etc
    	if(PartPriceSaaSPartConfigFactory.singleton().showHeaderUpTo(detail.getPricngTierMdl())){
    		return getI18NString(I18NBundleNames.PART_DETAILS_BASE, PartDetailsMessageKeys.PRICE_DETAIL_UP_TO)
			+ " "
			+ String.valueOf(((BigDecimal)(priceDetailMapKeys.get(index))).intValue());
    	}
    	if(PartPriceSaaSPartConfigFactory.singleton().showHeaderLevel(detail.getPricngTierMdl())){
    		//if granular tier, show 1 - 500/501 - 1000/>=1001 etc
    		if(PartPriceConstants.SaaSPricngTierMdl.GRANULAR.equals(detail.getPricngTierMdl())){
    			if(index == priceDetailMapKeys.size() - 1){
    				return  "&ge;&nbsp;"
					 + String.valueOf(((BigDecimal)(priceDetailMapKeys.get(index))).intValue());
    			}
    			else
    			{
    				int previous = ((BigDecimal)(priceDetailMapKeys.get(index))).intValue();
    				int current = ((BigDecimal)(priceDetailMapKeys.get(index + 1))).intValue() - 1;

    				if(previous != current){
    					return String.valueOf(previous) + " - " + String.valueOf(current);

    				} else {
    					return String.valueOf(previous);
    				}
        		}
    		//other tier, show 1 - 500/501 - 1000/1001 - 2000 etc
    		}else{
    			if(index == 0){
    				int qty = ((BigDecimal)(priceDetailMapKeys.get(index))).intValue();

    				if(qty == 1){
    					return "1";
    				} else {
            			return "1 - " +  String.valueOf(qty);
    				}
        		}
        		else{
        				return String.valueOf(((BigDecimal)(priceDetailMapKeys.get(index-1))).intValue() + 1)
        					 + " - "
        					 + String.valueOf(((BigDecimal)(priceDetailMapKeys.get(index))).intValue());
        		}
    		}
    	}
    	return "";
    }

	public String getSaasPartTypeCodeDscr() {
		return detail.getSaasPartTypeCodeDscr();
	}
	public boolean showPublshdPriceDurtnCodeDscr(){
		return StringUtils.isNotBlank(detail.getPublshdPriceDurtnCodeDscr());
	}

	public String getPublshdPriceDurtnCodeDscr() {
		if (showPublshdPriceDurtnCodeDscr()) {
			return "Per " + detail.getPublshdPriceDurtnCodeDscr();
		} else {
			return "";
		}
	}

	public String getPricngTierQtyMesurDscr() {
		if(StringUtils.isBlank(detail.getPricngTierQtyMesurDscr())){
			return "None";
		}else{
			return detail.getPricngTierQtyMesurDscr();
		}
	}

	public String getBillgUpfrntFlag() {
		return getBillDscr(detail.getBillgUpfrntFlag());
	}


	public String getBillgMthlyFlag() {
		return getBillDscr(detail.getBillgMthlyFlag());
	}

	public String getBillgQtrlyFlag() {
		return getBillDscr(detail.getBillgQtrlyFlag());
	}

	public String getBillgAnlFlag() {
		return getBillDscr(detail.getBillgAnlFlag());
	}

    public String getBillgEventFlag() {
        return getBillDscr(detail.getBillgEventFlag());
    }

	public String getSaasRenwlMdlCodeDscr() {
			if(StringUtils.isBlank( detail.getSaasRenwlMdlCodeDscr())){
				return "N/A";
			}else{
				return detail.getSaasRenwlMdlCodeDscr();
			}
	}

	public String getSwSbscrptnIdDscr() {
		return detail.getSwSbscrptnIdDscr();
	}

	public String getPriceCoverageHelpInfo(){
		return  getI18NString(I18NBundleNames.PART_DETAILS_BASE, PartDetailsMessageKeys.PRICE_COVERAGE_INFO);
    }

	public boolean showSaasFields(){
		return StringUtils.isNotBlank(detail.getSaasPartTypeCodeDscr());
	}

	public boolean showPricingToDisplayLink(){
		return this.isTier1Reseller()|this.isDistributor();
	}

	public String getPricingToDisplayLinkKey(){
		if (this.isDistributor()){
			return PartDetailsMessageKeys.DISTRIBUTOR;
		}else if (this.isTier1Reseller()){
			return PartDetailsMessageKeys.TIER_1_RESELLER;
		}else{
			return "";
		}
	}


	public String getPriceType(){
		if (this.isDistributor()){
			return PartnerPriceType.J;
		}else if (this.isTier1Reseller()){
			return PartnerPriceType.H;
		}else{
			return PartnerPriceType.A;
		}
	}

	public boolean showSrpSvpLink(){
        return !(StringUtils.isBlank(detail.getPriceType()) || PartnerPriceType.A.equals(detail.getPriceType()));
	}

	private String getBillDscr(String flag) {
		return "Y".equals(flag)? "Yes" : "No";
	}

	public boolean isSaasDaily() {
		return detail.isSaasDaily();
	}


}
