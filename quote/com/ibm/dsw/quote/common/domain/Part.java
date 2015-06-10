package com.ibm.dsw.quote.common.domain;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.RevnStrmCodeToCategoryMapping;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.config.PartPriceConstants;

public class Part {
	private String sPartNum;
	private String sPartDesc;
	private boolean bIsPvuPart;
	private String sProdTrgtMktCode;
	private String sPartTypeCode;
	private String sRevnStrmCode;
	private String sRevnStrmCodeDesc;
	private String sCtrctProgCode;
	private String sCtrctProgCodeDesc;
	private String sProdPackTypeCode;
	private String sWwideProdCode;
	private String sWwideProdCodeDesc;
	private String sWwideProdSetCode;
	private String sWwideProdGrpCode;
	private String swSubId;
	private String controlledCode;
	private String controlledCodeDesc;
	private String sIbmProgCode;
	private String sIbmProgCodeDscr;
	private boolean bRoyalty;
	private boolean bPartEOLSet;
	private boolean bPartRestrct;
	private boolean bExprsPart;
	private boolean bIsObsolutePart;
	private String sGroupName;
	private String sRevnStrmCategoryCode;
	private String sPartType = "TBD";
	private boolean saasPart;
	private boolean saasSetUpPart;
	private boolean saasSubscrptnPart;
	private boolean saasSubsumedSubscrptnPart;
	private boolean saasSubscrptnOvragePart;
	private boolean saasSetUpOvragePart;
	private boolean saasDaily;
	private boolean saasOnDemand;
	private boolean saasProdHumanServicesPart;
	private String productId;
	private boolean applncPart;
	private boolean applncServicePack;
	private boolean applncServicePackRenewal;
	private boolean applncMain;
	private boolean applncReinstatement;
	private boolean applncUpgrade;
	private boolean applncTransceiver;
	private boolean applncRenewal;
	private boolean applncAdditional;
	private boolean applncQtyRestrctn;
	private boolean applncMainGroup;
	private String	sSwProdBrandCode;
	private String	sSwProdBrandCodeDesc;
	private boolean applncOwnerShipTransfer;

	public String getsSwProdBrandCode() {
		return sSwProdBrandCode;
	}

	public void setsSwProdBrandCode(String sSwProdBrandCode) {
		this.sSwProdBrandCode = sSwProdBrandCode;
	}

	public String getsSwProdBrandCodeDesc() {
		return sSwProdBrandCodeDesc;
	}

	public void setsSwProdBrandCodeDesc(String sSwProdBrandCodeDesc) {
		this.sSwProdBrandCodeDesc = sSwProdBrandCodeDesc;
	}

	public Part() {
	}
	
	public Part(String partNum) {
		this.sPartNum = partNum;
	}

	public String getsPartNum() {
		return sPartNum;
	}

	public void setsPartNum(String sPartNum) {
		this.sPartNum = sPartNum;
	}

	public String getsPartDesc() {
		return sPartDesc;
	}

	public void setsPartDesc(String sPartDesc) {
		this.sPartDesc = sPartDesc;
	}

	public boolean isbIsPvuPart() {
		return bIsPvuPart;
	}

	public void setbIsPvuPart(boolean bIsPvuPart) {
		this.bIsPvuPart = bIsPvuPart;
	}

	public String getsProdTrgtMktCode() {
		return sProdTrgtMktCode;
	}

	public void setsProdTrgtMktCode(String sProdTrgtMktCode) {
		this.sProdTrgtMktCode = sProdTrgtMktCode;
	}

	public String getsPartTypeCode() {
		return sPartTypeCode;
	}

	public void setsPartTypeCode(String sPartTypeCode) {
		this.sPartTypeCode = sPartTypeCode;
	}

	public String getsRevnStrmCode() {
		return sRevnStrmCode;
	}

	public void setsRevnStrmCode(String sRevnStrmCode) {
		this.sRevnStrmCode = sRevnStrmCode;
	}

	public String getsRevnStrmCodeDesc() {
		return sRevnStrmCodeDesc;
	}

	public void setsRevnStrmCodeDesc(String sRevnStrmCodeDesc) {
		this.sRevnStrmCodeDesc = sRevnStrmCodeDesc;
	}

	public String getsCtrctProgCode() {
		return sCtrctProgCode;
	}

	public void setsCtrctProgCode(String sCtrctProgCode) {
		this.sCtrctProgCode = sCtrctProgCode;
	}

	public String getsCtrctProgCodeDesc() {
		return sCtrctProgCodeDesc;
	}

	public void setsCtrctProgCodeDesc(String sCtrctProgCodeDesc) {
		this.sCtrctProgCodeDesc = sCtrctProgCodeDesc;
	}

	public String getsProdPackTypeCode() {
		return sProdPackTypeCode;
	}

	public void setsProdPackTypeCode(String sProdPackTypeCode) {
		this.sProdPackTypeCode = sProdPackTypeCode;
	}

	public String getsWwideProdCode() {
		return sWwideProdCode;
	}

	public void setsWwideProdCode(String sWwideProdCode) {
		this.sWwideProdCode = sWwideProdCode;
	}

	public String getsWwideProdCodeDesc() {
		return sWwideProdCodeDesc;
	}

	public void setsWwideProdCodeDesc(String sWwideProdCodeDesc) {
		this.sWwideProdCodeDesc = sWwideProdCodeDesc;
	}

	public String getsWwideProdSetCode() {
		return sWwideProdSetCode;
	}

	public void setsWwideProdSetCode(String sWwideProdSetCode) {
		this.sWwideProdSetCode = sWwideProdSetCode;
	}

	public String getsWwideProdGrpCode() {
		return sWwideProdGrpCode;
	}

	public void setsWwideProdGrpCode(String sWwideProdGrpCode) {
		this.sWwideProdGrpCode = sWwideProdGrpCode;
	}

	public String getSwSubId() {
		return swSubId;
	}

	public void setSwSubId(String swSubId) {
		this.swSubId = swSubId;
	}

	public String getControlledCode() {
		return controlledCode;
	}

	public void setControlledCode(String controlledCode) {
		this.controlledCode = controlledCode;
	}

	public String getControlledCodeDesc() {
		return controlledCodeDesc;
	}

	public void setControlledCodeDesc(String controlledCodeDesc) {
		this.controlledCodeDesc = controlledCodeDesc;
	}

	public String getsIbmProgCode() {
		return sIbmProgCode;
	}

	public void setsIbmProgCode(String sIbmProgCode) {
		this.sIbmProgCode = sIbmProgCode;
	}

	public String getsIbmProgCodeDscr() {
		return sIbmProgCodeDscr;
	}

	public void setsIbmProgCodeDscr(String sIbmProgCodeDscr) {
		this.sIbmProgCodeDscr = sIbmProgCodeDscr;
	}

	public boolean isbRoyalty() {
		return bRoyalty;
	}

	public void setbRoyalty(boolean bRoyalty) {
		this.bRoyalty = bRoyalty;
	}

	public boolean isbPartEOLSet() {
		return bPartEOLSet;
	}

	public void setbPartEOLSet(boolean bPartEOLSet) {
		this.bPartEOLSet = bPartEOLSet;
	}

	public boolean isbPartRestrct() {
		return bPartRestrct;
	}

	public void setbPartRestrct(boolean bPartRestrct) {
		this.bPartRestrct = bPartRestrct;
	}

	public boolean isbExprsPart() {
		return bExprsPart;
	}

	public void setbExprsPart(boolean bExprsPart) {
		this.bExprsPart = bExprsPart;
	}

	public boolean isbIsObsolutePart() {
		return bIsObsolutePart;
	}

	public void setbIsObsolutePart(boolean bIsObsolutePart) {
		this.bIsObsolutePart = bIsObsolutePart;
	}

	public String getsGroupName() {
		return sGroupName;
	}

	public void setsGroupName(String sGroupName) {
		this.sGroupName = sGroupName;
	}

	public String getsRevnStrmCategoryCode() {
		return sRevnStrmCategoryCode;
	}

	public void setsRevnStrmCategoryCode(String sRevnStrmCategoryCode) {
		this.sRevnStrmCategoryCode = sRevnStrmCategoryCode;
	}

	public String getsPartType() {
		return sPartType;
	}

	public void setsPartType(String sPartType) {
		this.sPartType = sPartType;
	}

	public boolean isSaasPart() {
		return saasPart;
	}

	public void setSaasPart(boolean saasPart) {
		this.saasPart = saasPart;
	}

	public boolean isSaasSetUpPart() {
		return saasSetUpPart;
	}

	public void setSaasSetUpPart(boolean saasSetUpPart) {
		this.saasSetUpPart = saasSetUpPart;
	}

	public boolean isSaasSubscrptnPart() {
		return saasSubscrptnPart;
	}

	public void setSaasSubscrptnPart(boolean saasSubscrptnPart) {
		this.saasSubscrptnPart = saasSubscrptnPart;
	}

	public boolean isSaasSubsumedSubscrptnPart() {
		return saasSubsumedSubscrptnPart;
	}

	public void setSaasSubsumedSubscrptnPart(boolean saasSubsumedSubscrptnPart) {
		this.saasSubsumedSubscrptnPart = saasSubsumedSubscrptnPart;
	}

	public boolean isSaasSubscrptnOvragePart() {
		return saasSubscrptnOvragePart;
	}

	public void setSaasSubscrptnOvragePart(boolean saasSubscrptnOvragePart) {
		this.saasSubscrptnOvragePart = saasSubscrptnOvragePart;
	}

	public boolean isSaasSetUpOvragePart() {
		return saasSetUpOvragePart;
	}

	public void setSaasSetUpOvragePart(boolean saasSetUpOvragePart) {
		this.saasSetUpOvragePart = saasSetUpOvragePart;
	}

	public boolean isSaasDaily() {
		return saasDaily;
	}

	public void setSaasDaily(boolean saasDaily) {
		this.saasDaily = saasDaily;
	}

	public boolean isSaasOnDemand() {
		return saasOnDemand;
	}

	public void setSaasOnDemand(boolean saasOnDemand) {
		this.saasOnDemand = saasOnDemand;
	}

	public boolean isSaasProdHumanServicesPart() {
		return saasProdHumanServicesPart;
	}

	public void setSaasProdHumanServicesPart(boolean saasProdHumanServicesPart) {
		this.saasProdHumanServicesPart = saasProdHumanServicesPart;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public boolean isApplncPart() {
		return applncPart;
	}

	public void setApplncPart(boolean applncPart) {
		this.applncPart = applncPart;
	}

	public boolean isApplncServicePack() {
		return applncServicePack;
	}

	public void setApplncServicePack(boolean applncServicePack) {
		this.applncServicePack = applncServicePack;
	}

	public boolean isApplncServicePackRenewal() {
		return applncServicePackRenewal;
	}

	public void setApplncServicePackRenewal(boolean applncServicePackRenewal) {
		this.applncServicePackRenewal = applncServicePackRenewal;
	}

	public boolean isApplncMain() {
		return applncMain;
	}

	public void setApplncMain(boolean applncMain) {
		this.applncMain = applncMain;
	}

	public boolean isApplncReinstatement() {
		return applncReinstatement;
	}

	public void setApplncReinstatement(boolean applncReinstatement) {
		this.applncReinstatement = applncReinstatement;
	}

	public boolean isApplncUpgrade() {
		return applncUpgrade;
	}

	public void setApplncUpgrade(boolean applncUpgrade) {
		this.applncUpgrade = applncUpgrade;
	}

	public boolean isApplncTransceiver() {
		return applncTransceiver;
	}

	public void setApplncTransceiver(boolean applncTransceiver) {
		this.applncTransceiver = applncTransceiver;
	}

	public boolean isApplncRenewal() {
		return applncRenewal;
	}

	public void setApplncRenewal(boolean applncRenewal) {
		this.applncRenewal = applncRenewal;
	}

	public boolean isApplncAdditional() {
		return applncAdditional;
	}

	public void setApplncAdditional(boolean applncAdditional) {
		this.applncAdditional = applncAdditional;
	}

	public boolean isApplncQtyRestrctn() {
		return applncQtyRestrctn;
	}

	public void setApplncQtyRestrctn(boolean applncQtyRestrctn) {
		this.applncQtyRestrctn = applncQtyRestrctn;
	}

	public boolean isApplncMainGroup() {
		return applncMainGroup;
	}

	public void setApplncMainGroup(boolean applncMainGroup) {
		this.applncMainGroup = applncMainGroup;
	}

	public boolean isApplncOwnerShipTransfer() {
		return applncOwnerShipTransfer;
	}

	public void setApplncOwnerShipTransfer(boolean applncOwnerShipTransfer) {
		this.applncOwnerShipTransfer = applncOwnerShipTransfer;
	}

	public String getRevnStrmCategoryCode(QuoteLineItem_Impl quoteLineItem_Impl){
	    if (StringUtils.isBlank(getsRevnStrmCategoryCode())) {
	        try {
	            CacheProcess cp = CacheProcessFactory.singleton().create();
	            RevnStrmCodeToCategoryMapping cat = cp.getRevnStrmCodeToCategoryMapping(getsRevnStrmCode());
	            setsRevnStrmCategoryCode((cat == null || StringUtils.isBlank(cat.getRevnStrmCategoryCode())) ?
	                    PartPriceConstants.RevnStrmCategory.OTHER : cat.getRevnStrmCategoryCode());
	        } catch (QuoteException e) {
	            QuoteLineItem_Impl.logContext.error(quoteLineItem_Impl, e.getMessage());
	        }
	    }
	
	    return getsRevnStrmCategoryCode();
	}
}