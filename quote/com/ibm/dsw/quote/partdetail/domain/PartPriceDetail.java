/*
 * Created on 2007-4-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.partdetail.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ibm.dsw.quote.common.domain.Domain;

/**
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public interface PartPriceDetail extends Domain{
    public String getLob();
    public void setLob(String lob) throws Exception;
    public String getCountryCode();
    public void setCountryCode(String countryCode) throws Exception;
    public String getCurrencyCode();
    public void setCurrencyCode(String currencyCode) throws Exception;
    public String getBrandName();
    public void setBrandName(String brandName) throws Exception;

    public void setIsPartCtrlld(boolean partCtrlld);
	public boolean isPartCtrlld();


    public String getCtrlldProgCode();
    public void setCtrlldProgCode(String ctrlldProgCode) throws Exception;
    public String getCtrlldProgDesc();
    public void setCtrlldProgDesc(String ctrlldProgDesc) throws Exception;
    public List getChargeUnits();
    public void setChargeUnits(List chargeUnit) throws Exception;
    public String getCountry();
    public void setCountry(String country) throws Exception;
    public String getCurrency();
    public void setCurrency(String currency) throws Exception;
    public String getEncryptionLevelCode();
    public void setEncryptionLevelCode(String encryptionLevelCode) throws Exception;
    public List getMediaTypes();
    public void setMediaTypes(List mediaTypes) throws Exception;
    public String getPartDescription();
    public void setPartDescription(String partDescription) throws Exception;
    public List getPartGroups();
    public void setPartGroups(List partGroups) throws Exception;
    public List getPartLanguages();
    public void setPartLanguages(List partLanguages) throws Exception;
    public String getPartNumber();
    public void setPartNumber(String partNumber) throws Exception;
    public String getPartType();
    public void setPartType(String partType) throws Exception;
    public String getPartVersion();
    public void setPartVersion(String partVersion) throws Exception;
    public Map getPids();
    public void setPids(Map pids) throws Exception;
    public double getPoints();
    public void setPoints(double points) throws Exception;
    public Date getPriceEffectiveDateFrom();
    public void setPriceEffectiveDateFrom(Date priceEffectiveDateFrom) throws Exception;
    public Date getPriceEffectiveDateTo();
    public void setPriceEffectiveDateTo(Date priceEffectiveDateTo) throws Exception;
    public double getPriceLvlA();
    public void setPriceLvlA(double priceLvlA) throws Exception;
    public double getPriceLvlBL();
    public void setPriceLvlBL(double priceLvlBL) throws Exception;
    public double getPriceLvlD();
    public void setPriceLvlD(double priceLvlD) throws Exception;
    public double getPriceLvlE();
    public void setPriceLvlE(double priceLvlE) throws Exception;
    public double getPriceLvlED();
    public void setPriceLvlED(double priceLvlED) throws Exception;
    public double getPriceLvlF();
    public void setPriceLvlF(double priceLvlF) throws Exception;
    public double getPriceLvlG();
    public void setPriceLvlG(double priceLvlG) throws Exception;
    public double getPriceLvlGV();
    public void setPriceLvlGV(double priceLvlGV) throws Exception;
    public double getPriceLvlH();
    public void setPriceLvlH(double priceLvlH) throws Exception;
    public double getPriceLvlI();
    public void setPriceLvlI(double priceLvlI) throws Exception;
    public double getPriceLvlJ();
    public void setPriceLvlJ(double priceLvlJ) throws Exception;
    public String getProductDistributionChannel();
    public void setProductDistributionChannel(String productDistributionChannel) throws Exception;
    public String getProductGroup();
    public void setProductGroup(String productGroup) throws Exception;
    public String getProductName();
    public void setProductName(String productName) throws Exception;
    public List getProductPlatforms();
    public void setProductPlatforms(List productPlatforms) throws Exception;
    public String getProductTradeName();
    public void setProductTradeName(String productTradeName) throws Exception;
    public Map getCoprerequisites();
    public void setCoprerequisites(Map coprerequisites) throws Exception;
	public void setPartCntrlDistDesc(String partCntrlDistDesc) throws Exception;
	public String getPartCntrlDistDesc();
	public void setProdSubGroupDesc(String prodSubGroupDesc) throws Exception;
	public String getProdSubGroupDesc();
	public void setWebQNumber(String webQNumber) throws Exception;
	public String getWebQNumber();

	public void setSubmitted(boolean submitted);
	public boolean isSubmitted();

	public void setRenewal(boolean renewal);
	public boolean isRenewal();

	public String getRevnStrmCodeDscr();
	public void setRevnStrmCodeDscr(String revnStrmCodeDscr);

    public String getGbtBrandCodeDscr();
    public void setGbtBrandCodeDscr(String gbtBrandCodeDscr);

    public Map getPriceDetailMap();
	public void setPriceDetailMap(Map priceDetailMap);

	public Map getDsPriceMap();
	public void setDsPriceMap(Map dsPriceMap);

	public String getPricngTierMdl();
	public void setPricngTierMdl(String pricngTierMdl);

	public String getSaasPartTypeCodeDscr() ;
	public void setSaasPartTypeCodeDscr(String saasPartTypeCodeDscr) ;
	public String getPublshdPriceDurtnCodeDscr() ;
	public void setPublshdPriceDurtnCodeDscr(String publshdPriceDurtnCodeDscr) ;
	public String getPricngTierQtyMesurDscr() ;
	public void setPricngTierQtyMesurDscr(String pricngTierQtyMesurDscr) ;
	public String getBillgUpfrntFlag() ;
	public void setBillgUpfrntFlag(String billgUpfrntFlag) ;
	public String getBillgMthlyFlag() ;
	public void setBillgMthlyFlag(String billgMthlyFlag) ;
	public String getBillgQtrlyFlag() ;
	public void setBillgQtrlyFlag(String billgQtrlyFlag) ;
	public String getBillgAnlFlag() ;
	public void setBillgAnlFlag(String billgAnlFlag) ;

    public String getBillgEventFlag();
    public void setBillgEventFlag(String billgEventFlag);
	public String getSaasRenwlMdlCodeDscr();
	public void setSaasRenwlMdlCodeDscr(String saasRenwlMdlCodeDscr);
	public String getSwSbscrptnIdDscr() ;
	public void setSwSbscrptnIdDscr(String swSbscrptnIdDscr) ;
	public boolean isSaasDaily();
	public void setSaasDaily(boolean saasDaily);

	public boolean isSaasPartFlag();

	public void setSaasPartFlag(boolean saasPartFlag);

	public boolean isMonthlyPartFlag();

	public void setMonthlyPartFlag(boolean monthlyPartFlag);
	public String getPriceType();
	public void setPriceType(String priceType) throws Exception;

}
