/*
 * Created on Feb 14, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.ps.domain;

import java.sql.Date;

import org.apache.commons.lang.StringUtils;


/**
 * @author tboulet
 *
 * Contains part information
 * 
 */
public class PartSearchPart {
    
    private String partID;
	private String partDescription;
	private String prodTypeID;
	private String prodTypeDescription;
	private String productID;
	private String productDescription;
	private String brandID;
	private String brandDescription;
	private String portfolioCode;
	private String portfolioDescription;
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
	private Date priceEndSoonFlag;
	private Date partEolDate;
	private boolean partRestrictedFlag;
	private boolean partObsoleteFlag;
	private String sapSalesStatCode;
	private String ibmProgCode;
	private String ibmProgDscr;
	private boolean saasDlyFlag = false;
	
	private boolean applianceFlag;
	private boolean applianceQtyRestrctnFlag;
	private boolean exportRestrctnFlag;
	
	/**
     * @return Returns the ibmProgCode.
     */
    public String getIbmProgCode() {
        return ibmProgCode;
    }
    /**
     * @param ibmProgCode The ibmProgCode to set.
     */
    public void setIbmProgCode(String ibmProgCode) {
        this.ibmProgCode = ibmProgCode;
    }
    /**
     * @return Returns the ibmProgDscr.
     */
    public String getIbmProgDscr() {
        return ibmProgDscr;
    }
    /**
     * @param ibmProgDscr The ibmProgDscr to set.
     */
    public void setIbmProgDscr(String ibmProgDscr) {
        this.ibmProgDscr = ibmProgDscr;
    }
	public PartSearchPart() {
	    //
	}
	
	public PartSearchPart(String num, String desc) {
	    partID = num;
	    partDescription = desc;
	}
	
    /**
     * @return Returns the brandDescription.
     */
    public String getBrandDescription() {
        return brandDescription;
    }
    /**
     * @param brandDescription The brandDescription to set.
     */
    public void setBrandDescription(String brandDescription) {
        this.brandDescription = brandDescription;
    }
    /**
     * @return Returns the brandID.
     */
    public String getBrandID() {
        return brandID;
    }
    /**
     * @param brandID The brandID to set.
     */
    public void setBrandID(String brandID) {
        this.brandID = brandID;
    }
    /**
     * @return Returns the partDescription.
     */
    public String getPartDescription() {
        return partDescription;
    }
    /**
     * @param partDescription The partDescription to set.
     */
    public void setPartDescription(String partDescription) {
        this.partDescription = partDescription;
    }
    /**
     * @return Returns the partEolDate.
     */
    public Date getPartEolDate() {
        return partEolDate;
    }
    /**
     * @param partEolDate The partEolDate to set.
     */
    public void setPartEolDate(Date partEolDate) {
        this.partEolDate = partEolDate;
    }
    /**
     * @return Returns the partID.
     */
    public String getPartID() {
        return partID;
    }
    /**
     * @param partID The partID to set.
     */
    public void setPartID(String partID) {
        this.partID = partID;
    }
    /**
     * @return Returns the partObsoleteFlag.
     */
    public boolean isPartObsoleteFlag() {
        return partObsoleteFlag;
    }
    /**
     * @param partObsoleteFlag The partObsoleteFlag to set.
     */
    public void setPartObsoleteFlag(boolean partObsoleteFlag) {
        this.partObsoleteFlag = partObsoleteFlag;
    }
    /**
     * @return Returns the partRestrictedFlag.
     */
    public boolean isPartRestrictedFlag() {
        return partRestrictedFlag;
    }
    /**
     * @param partRestrictedFlag The partRestrictedFlag to set.
     */
    public void setPartRestrictedFlag(boolean partRestrictedFlag) {
        this.partRestrictedFlag = partRestrictedFlag;
    }
    /**
     * @return Returns the points.
     */
    public double getPoints() {
        return points;
    }
    /**
     * @param points The points to set.
     */
    public void setPoints(double points) {
        this.points = points;
    }
    /**
     * @return Returns the priceEndSoonFlag.
     */
    public Date getPriceEndSoonFlag() {
        return priceEndSoonFlag;
    }
    /**
     * @param priceEndSoonFlag The priceEndSoonFlag to set.
     */
    public void setPriceEndSoonFlag(Date priceEndSoonFlag) {
        this.priceEndSoonFlag = priceEndSoonFlag;
    }
    /**
     * @return Returns the priceLvlA.
     */
    public double getPriceLvlA() {
        return priceLvlA;
    }
    /**
     * @param priceLvlA The priceLvlA to set.
     */
    public void setPriceLvlA(double priceLvlA) {
        this.priceLvlA = priceLvlA;
    }
    /**
     * @return Returns the priceLvlBL.
     */
    public double getPriceLvlBL() {
        return priceLvlBL;
    }
    /**
     * @param priceLvlBL The priceLvlBL to set.
     */
    public void setPriceLvlBL(double priceLvlBL) {
        this.priceLvlBL = priceLvlBL;
    }
    /**
     * @return Returns the priceLvlD.
     */
    public double getPriceLvlD() {
        return priceLvlD;
    }
    /**
     * @param priceLvlD The priceLvlD to set.
     */
    public void setPriceLvlD(double priceLvlD) {
        this.priceLvlD = priceLvlD;
    }
    /**
     * @return Returns the priceLvlE.
     */
    public double getPriceLvlE() {
        return priceLvlE;
    }
    /**
     * @param priceLvlE The priceLvlE to set.
     */
    public void setPriceLvlE(double priceLvlE) {
        this.priceLvlE = priceLvlE;
    }
    /**
     * @return Returns the priceLvlED.
     */
    public double getPriceLvlED() {
        return priceLvlED;
    }
    /**
     * @param priceLvlED The priceLvlED to set.
     */
    public void setPriceLvlED(double priceLvlED) {
        this.priceLvlED = priceLvlED;
    }
    /**
     * @return Returns the priceLvlF.
     */
    public double getPriceLvlF() {
        return priceLvlF;
    }
    /**
     * @param priceLvlF The priceLvlF to set.
     */
    public void setPriceLvlF(double priceLvlF) {
        this.priceLvlF = priceLvlF;
    }
    /**
     * @return Returns the priceLvlG.
     */
    public double getPriceLvlG() {
        return priceLvlG;
    }
    /**
     * @param priceLvlG The priceLvlG to set.
     */
    public void setPriceLvlG(double priceLvlG) {
        this.priceLvlG = priceLvlG;
    }
    /**
     * @return Returns the priceLvlGV.
     */
    public double getPriceLvlGV() {
        return priceLvlGV;
    }
    /**
     * @param priceLvlGV The priceLvlGV to set.
     */
    public void setPriceLvlGV(double priceLvlGV) {
        this.priceLvlGV = priceLvlGV;
    }
    /**
     * @return Returns the priceLvlH.
     */
    public double getPriceLvlH() {
        return priceLvlH;
    }
    /**
     * @param priceLvlH The priceLvlH to set.
     */
    public void setPriceLvlH(double priceLvlH) {
        this.priceLvlH = priceLvlH;
    }
    /**
     * @return Returns the priceLvlI.
     */
    public double getPriceLvlI() {
        return priceLvlI;
    }
    /**
     * @param priceLvlI The priceLvlI to set.
     */
    public void setPriceLvlI(double priceLvlI) {
        this.priceLvlI = priceLvlI;
    }
    /**
     * @return Returns the priceLvlJ.
     */
    public double getPriceLvlJ() {
        return priceLvlJ;
    }
    /**
     * @param priceLvlJ The priceLvlJ to set.
     */
    public void setPriceLvlJ(double priceLvlJ) {
        this.priceLvlJ = priceLvlJ;
    }
    /**
     * @return Returns the prodTypeDescription.
     */
    public String getProdTypeDescription() {
        return prodTypeDescription;
    }
    /**
     * @param prodTypeDescription The prodTypeDescription to set.
     */
    public void setProdTypeDescription(String prodTypeDescription) {
        this.prodTypeDescription = prodTypeDescription;
    }
    /**
     * @return Returns the prodTypeID.
     */
    public String getProdTypeID() {
        return prodTypeID;
    }
    /**
     * @param prodTypeID The prodTypeID to set.
     */
    public void setProdTypeID(String prodTypeID) {
        this.prodTypeID = prodTypeID;
    }
    /**
     * @return Returns the productDescription.
     */
    public String getProductDescription() {
        return productDescription;
    }
    /**
     * @param productDescription The productDescription to set.
     */
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    /**
     * @return Returns the productID.
     */
    public String getProductID() {
        return productID;
    }
    /**
     * @param productID The productID to set.
     */
    public void setProductID(String productID) {
        this.productID = productID;
    }
    
    /**
     * @return Returns the portfolioCode.
     */
    public String getPortfolioCode() {
        return portfolioCode;
    }
    /**
     * @param portfolioCode The portfolioCode to set.
     */
    public void setPortfolioCode(String portfolioCode) {
        this.portfolioCode = portfolioCode;
    }
    /**
     * @return Returns the portfolioDescription.
     */
    public String getPortfolioDescription() {
        return portfolioDescription;
    }
    /**
     * @param portfolioDescription The portfolioDescription to set.
     */
    public void setPortfolioDescription(String portfolioDescription) {
        this.portfolioDescription = portfolioDescription;
    }
    
    public String getSapSalesStatCode() {
		return sapSalesStatCode;
	}

	public void setSapSalesStatCode(String sapSalesStatCode) {
		this.sapSalesStatCode = sapSalesStatCode;
	}

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Part with number:"+getPartID();
    }
    
    public boolean isPartControlled(){
        return StringUtils.isNotBlank(ibmProgCode);
    }
    public boolean getSaasDlyFlag() {
		return saasDlyFlag;
	}

	public void setSaasDlyFlag(boolean saasDlyFlag) {
		this.saasDlyFlag = saasDlyFlag;
	}
	
	public boolean isApplianceFlag() {
		return applianceFlag;
	}
	public void setApplianceFlag(boolean applianceFlag) {
		this.applianceFlag = applianceFlag;
	}
	public boolean isApplianceQtyRestrctnFlag() {
		return applianceQtyRestrctnFlag;
	}
	public void setApplianceQtyRestrctnFlag(boolean applianceQtyRestrctnFlag) {
		this.applianceQtyRestrctnFlag = applianceQtyRestrctnFlag;
	}
	
	public boolean isExportRestrctnFlag() {
		return exportRestrctnFlag;
	}
	public void setExportRestrctnFlag(boolean exportRestrctnFlag) {
		this.exportRestrctnFlag = exportRestrctnFlag;
	}
	
}
