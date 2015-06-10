/*
 * Created on 2007-4-3
 *
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
public class PartPriceDetail_Impl implements PartPriceDetail{

    /**
	 *
	 */
	private static final long serialVersionUID = 1096730019294240679L;

	private boolean _isModefied = false;

    private int _mode = Domain.DOMAIN_MODE_PO;

    private String lob;
    private String countryCode;
    private String currencyCode;
    private String partNumber;
	private String partDescription;
	private String brandName;
	private boolean partCtrlld;
	private String ctrlldProgCode;
	private String ctrlldProgDesc;
	private String productDistributionChannel;
	private String productGroup;
	private String productTradeName;
	private String productName;
	private transient Map pids;
	private transient List productPlatforms;
	private String encryptionLevelCode;
	private transient List mediaTypes;
	private transient List partLanguages;
	private String partVersion;
	private String partType;
	private transient List chargeUnits;
	private transient List partGroups;
	private String partCntrlDistDesc;
	private String prodSubGroupDesc;
	private String webQNumber;

	private String country;
	private String currency;
	private Date priceEffectiveDateFrom;
	private Date priceEffectiveDateTo;

	private double points;
	private double priceLvlA;
	private double priceLvlBL;
	private double priceLvlD;
	private double priceLvlE;
	private double priceLvlF;
	private double priceLvlG;
	private double priceLvlH;
	private double priceLvlI;
	private double priceLvlJ;
	private double priceLvlGV;
	private double priceLvlED;

	private boolean submitted;
	private boolean renewal;

	private String revnStrmCodeDscr;
	private String gbtBrandCodeDscr;

	private transient Map coprerequisites;

	private transient Map priceDetailMap;

	private transient Map dsPriceMap;

	private String pricngTierMdl;

    private String saasPartTypeCodeDscr;
	private String publshdPriceDurtnCodeDscr;
	private String pricngTierQtyMesurDscr;
	private String billgUpfrntFlag;
	private String billgMthlyFlag;
	private String billgQtrlyFlag;
	private String billgAnlFlag;

    private String billgEventFlag;
	private String saasRenwlMdlCodeDscr;
	private String swSbscrptnIdDscr;
	private boolean saasDaily;

	private String priceType;

	private boolean saasPartFlag;
	private boolean monthlyPartFlag;

	public boolean isSaasPartFlag() {
		return saasPartFlag;
	}

	public void setSaasPartFlag(boolean saasPartFlag) {
		this.saasPartFlag = saasPartFlag;
	}

	public boolean isMonthlyPartFlag() {
		return monthlyPartFlag;
	}

	public void setMonthlyPartFlag(boolean monthlyPartFlag) {
		this.monthlyPartFlag = monthlyPartFlag;
	}

    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) throws Exception {
        this.countryCode = countryCode;
    }
    public String getCurrencyCode() {
        return currencyCode;
    }
    public void setCurrencyCode(String currencyCode)  throws Exception{
        this.currencyCode = currencyCode;
    }
    public String getLob() {
        return lob;
    }
    public void setLob(String lob)  throws Exception{
        this.lob = lob;
    }
    public String getBrandName() {
        return brandName;
    }
    public void setBrandName(String brandName) throws Exception {
        this.brandName = brandName;
    }

    public void setIsPartCtrlld (boolean partCtrlld){
	    this.partCtrlld = partCtrlld;
	}

	public boolean isPartCtrlld(){
	    return this.partCtrlld;
	}

	public String getCtrlldProgCode() {
        return ctrlldProgCode;
    }

    public void setCtrlldProgCode(String ctrlldProgCode) throws Exception {
        this.ctrlldProgCode = ctrlldProgCode;
    }
    public String getCtrlldProgDesc() {
        return ctrlldProgDesc;
    }
    public void setCtrlldProgDesc(String ctrlldProgDesc) throws Exception {
        this.ctrlldProgDesc = ctrlldProgDesc;
    }
    public List getChargeUnits() {
        return chargeUnits;
    }
    public void setChargeUnits(List chargeUnits) throws Exception {
        this.chargeUnits = chargeUnits;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) throws Exception {
        this.country = country;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) throws Exception {
        this.currency = currency;
    }
    public String getEncryptionLevelCode() {
        return encryptionLevelCode;
    }
    public void setEncryptionLevelCode(String encryptionLevelCode) throws Exception {
        this.encryptionLevelCode = encryptionLevelCode;
    }
    public List getMediaTypes() {
        return mediaTypes;
    }
    public void setMediaTypes(List mediaTypes) throws Exception {
        this.mediaTypes = mediaTypes;
    }
    public String getPartDescription() {
        return partDescription;
    }
    public void setPartDescription(String partDescription) throws Exception {
        this.partDescription = partDescription;
    }
    public List getPartGroups() {
        return partGroups;
    }
    public void setPartGroups(List partGroups) throws Exception {
        this.partGroups = partGroups;
    }
    public List getPartLanguages() {
        return partLanguages;
    }
    public void setPartLanguages(List partLanguages) throws Exception {
        this.partLanguages = partLanguages;
    }
    public String getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(String partNumber) throws Exception {
        this.partNumber = partNumber;
    }
    public String getPartType() {
        return partType;
    }
    public void setPartType(String partType) throws Exception {
        this.partType = partType;
    }
    public String getPartVersion() {
        return partVersion;
    }
    public void setPartVersion(String partVersion) throws Exception {
        this.partVersion = partVersion;
    }
    public Map getPids() {
        return pids;
    }
    public void setPids(Map pids) throws Exception {
        this.pids = pids;
    }
    public double getPoints() {
        return points;
    }
    public void setPoints(double points)  throws Exception{
        this.points = points;
    }
    public Date getPriceEffectiveDateFrom() {
        return priceEffectiveDateFrom;
    }
    public void setPriceEffectiveDateFrom(Date priceEffectiveDateFrom) throws Exception {
        this.priceEffectiveDateFrom = priceEffectiveDateFrom;
    }
    public Date getPriceEffectiveDateTo() {
        return priceEffectiveDateTo;
    }
    public void setPriceEffectiveDateTo(Date priceEffectiveDateTo) throws Exception {
        this.priceEffectiveDateTo = priceEffectiveDateTo;
    }
    public double getPriceLvlA() {
        return priceLvlA;
    }
    public void setPriceLvlA(double priceLvlA) throws Exception {
        this.priceLvlA = priceLvlA;
    }
    public double getPriceLvlBL() {
        return priceLvlBL;
    }
    public void setPriceLvlBL(double priceLvlBL) throws Exception {
        this.priceLvlBL = priceLvlBL;
    }
    public double getPriceLvlD() {
        return priceLvlD;
    }
    public void setPriceLvlD(double priceLvlD) throws Exception {
        this.priceLvlD = priceLvlD;
    }
    public double getPriceLvlE() {
        return priceLvlE;
    }
    public void setPriceLvlE(double priceLvlE)  throws Exception{
        this.priceLvlE = priceLvlE;
    }
    public double getPriceLvlED() {
        return priceLvlED;
    }
    public void setPriceLvlED(double priceLvlED)  throws Exception{
        this.priceLvlED = priceLvlED;
    }
    public double getPriceLvlF() {
        return priceLvlF;
    }
    public void setPriceLvlF(double priceLvlF)  throws Exception{
        this.priceLvlF = priceLvlF;
    }
    public double getPriceLvlG() {
        return priceLvlG;
    }
    public void setPriceLvlG(double priceLvlG) throws Exception {
        this.priceLvlG = priceLvlG;
    }
    public double getPriceLvlGV() {
        return priceLvlGV;
    }
    public void setPriceLvlGV(double priceLvlGV) throws Exception {
        this.priceLvlGV = priceLvlGV;
    }
    public double getPriceLvlH() {
        return priceLvlH;
    }
    public void setPriceLvlH(double priceLvlH)  throws Exception{
        this.priceLvlH = priceLvlH;
    }
    public double getPriceLvlI() {
        return priceLvlI;
    }
    public void setPriceLvlI(double priceLvlI)  throws Exception{
        this.priceLvlI = priceLvlI;
    }
    public double getPriceLvlJ() {
        return priceLvlJ;
    }
    public void setPriceLvlJ(double priceLvlJ) throws Exception {
        this.priceLvlJ = priceLvlJ;
    }
    public String getProductDistributionChannel() {
        return productDistributionChannel;
    }
    public void setProductDistributionChannel(String productDistributionChannel) throws Exception {
        this.productDistributionChannel = productDistributionChannel;
    }
    public String getProductGroup() {
        return productGroup;
    }
    public void setProductGroup(String productGroup) throws Exception {
        this.productGroup = productGroup;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) throws Exception {
        this.productName = productName;
    }
    public List getProductPlatforms() {
        return productPlatforms;
    }
    public void setProductPlatforms(List productPlatforms)  throws Exception{
        this.productPlatforms = productPlatforms;
    }
    public String getProductTradeName() {
        return productTradeName;
    }
    public void setProductTradeName(String productTradeName) throws Exception {
        this.productTradeName = productTradeName;
    }
    public Map getCoprerequisites() {
        return coprerequisites;
    }
    public void setCoprerequisites(Map coprerequisites)  throws Exception{
        this.coprerequisites = coprerequisites;
    }
    public void setMode(int mode)  throws Exception{
        if(mode == Domain.DOMAIN_MODE_PO || mode == Domain.DOMAIN_MODE_VO){
            this._mode = mode;
        }
    }
    public int getMode() {
        return _mode;
    }
    public boolean isModified() {
        return _isModefied;
    }
    public void markAsModified() throws Exception{
        this._isModefied = true;
    }
	public void setPartCntrlDistDesc(String partCntrlDistDesc) throws Exception {
		this.partCntrlDistDesc = partCntrlDistDesc;
	}
	public String getPartCntrlDistDesc() {
		return this.partCntrlDistDesc;
	}
	public void setProdSubGroupDesc(String prodSubGroupDesc) throws Exception {
		this.prodSubGroupDesc = prodSubGroupDesc;
	}
	public String getProdSubGroupDesc() {
		return this.prodSubGroupDesc;
	}
	public void setWebQNumber(String webQNumber) throws Exception {
		this.webQNumber = webQNumber;
	}
	public String getWebQNumber() {
		return this.webQNumber;
	}

	public void setSubmitted(boolean submitted){
	    this.submitted = submitted;
	}

	public boolean isSubmitted(){
	    return this.submitted;
	}

	public void setRenewal(boolean renewal){
	    this.renewal = renewal;
	}
	public boolean isRenewal(){
	   return this.renewal;
	}

	public String getRevnStrmCodeDscr(){
	    return revnStrmCodeDscr;
	}
	public void setRevnStrmCodeDscr(String revnStrmCodeDscr){
	    this.revnStrmCodeDscr = revnStrmCodeDscr;
	}
    /**
     * @return the gbtBrandCodeDscr
     */
    public String getGbtBrandCodeDscr() {
        return gbtBrandCodeDscr;
    }
    /**
     * @param gbtBrandCodeDscr the gbtBrandCodeDscr to set
     */
    public void setGbtBrandCodeDscr(String gbtBrandCodeDscr) {
        this.gbtBrandCodeDscr = gbtBrandCodeDscr;
    }

    public Map getPriceDetailMap() {
		return priceDetailMap;
	}
	public void setPriceDetailMap(Map priceDetailMap) {
		this.priceDetailMap = priceDetailMap;
	}
	public String getPricngTierMdl() {
		return pricngTierMdl;
	}

	public void setPricngTierMdl(String pricngTierMdl) {
		this.pricngTierMdl = pricngTierMdl;
	}

	public String getSaasPartTypeCodeDscr() {
		return saasPartTypeCodeDscr;
	}
	public void setSaasPartTypeCodeDscr(String saasPartTypeCodeDscr) {
		this.saasPartTypeCodeDscr = saasPartTypeCodeDscr;
	}
	public String getPublshdPriceDurtnCodeDscr() {
		return publshdPriceDurtnCodeDscr;
	}
	public void setPublshdPriceDurtnCodeDscr(String publshdPriceDurtnCodeDscr) {
		this.publshdPriceDurtnCodeDscr = publshdPriceDurtnCodeDscr;
	}
	public String getPricngTierQtyMesurDscr() {
		return pricngTierQtyMesurDscr;
	}
	public void setPricngTierQtyMesurDscr(String pricngTierQtyMesurDscr) {
		this.pricngTierQtyMesurDscr = pricngTierQtyMesurDscr;
	}
	public String getBillgUpfrntFlag() {
		return billgUpfrntFlag;
	}
	public void setBillgUpfrntFlag(String billgUpfrntFlag) {
		this.billgUpfrntFlag = billgUpfrntFlag;
	}
	public String getBillgMthlyFlag() {
		return billgMthlyFlag;
	}
	public void setBillgMthlyFlag(String billgMthlyFlag) {
		this.billgMthlyFlag = billgMthlyFlag;
	}
	public String getBillgQtrlyFlag() {
		return billgQtrlyFlag;
	}
	public void setBillgQtrlyFlag(String billgQtrlyFlag) {
		this.billgQtrlyFlag = billgQtrlyFlag;
	}
	public String getBillgAnlFlag() {
		return billgAnlFlag;
	}
	public void setBillgAnlFlag(String billgAnlFlag) {
		this.billgAnlFlag = billgAnlFlag;
	}
	public String getSaasRenwlMdlCodeDscr() {
		return saasRenwlMdlCodeDscr;
	}
	public void setSaasRenwlMdlCodeDscr(String saasRenwlMdlCodeDscr) {
		this.saasRenwlMdlCodeDscr = saasRenwlMdlCodeDscr;
	}
	public String getSwSbscrptnIdDscr() {
		return swSbscrptnIdDscr;
	}
	public void setSwSbscrptnIdDscr(String swSbscrptnIdDscr) {
		this.swSbscrptnIdDscr = swSbscrptnIdDscr;
	}
	public boolean isSaasDaily() {
		return saasDaily;
	}
	public void setSaasDaily(boolean saasDaily) {
		this.saasDaily = saasDaily;
	}
	@Override
	public String getPriceType() {
		return this.priceType;
	}
	@Override
	public void setPriceType(String priceType) throws Exception {
		this.priceType = priceType;
	}
	@Override
	public Map getDsPriceMap() {
		return dsPriceMap;
	}

    @Override
	public void setDsPriceMap(Map dsPriceMap) {
		this.dsPriceMap = dsPriceMap;
	}

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.dsw.quote.partdetail.domain.PartPriceDetail#setBillgEventFlag(java.lang.String)
     */
    @Override
    public void setBillgEventFlag(String billgEventFlag) {
        this.billgEventFlag = billgEventFlag;
    }

    /**
     * Getter for billgEventFlag.
     * 
     * @return the billgEventFlag
     */
    public String getBillgEventFlag() {
        return this.billgEventFlag;
    }

}
