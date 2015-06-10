package com.ibm.dsw.quote.common.domain;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/** Java Bean used to convert data to JSON using getter methods 
 * as object names (without get/is prefix).
 * @see JSONObject for converting java beans to JSON */
public class LineItemAddress {
	
	String partNumber = "", crad = "", partDescription = "", 
		shipToAddress = "", installToAddress = "";

	public String getPartNumber() {
		return partNumber;
	}

	public String getCrad() {
		return crad;
	}

	public String getPartDescription() {
		return partDescription;
	}

	public String getShipToAddress() {
		return shipToAddress;
	}

	public String getInstallToAddress() {
		return installToAddress;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public void setCrad(String crad) {
		this.crad = crad;
	}

	public void setPartDescription(String partDescription) {
		this.partDescription = partDescription;
	}

	public void setShipToAddress(String shipToAddress) {
		this.shipToAddress = shipToAddress;
	}

	public void setInstallToAddress(String installToAddress) {
		this.installToAddress = installToAddress;
	}

	public void copy(ApplianceLineItem obj) {
		this.partNumber = obj.getPartNum(); 
		this.crad = obj.getCustReqstdArrivlDate() == null ? "" : DateHelper.getDateByFormat(obj.getCustReqstdArrivlDate(), "dd MMM yyyy");; 
		this.partDescription = obj.getPartDscrLong(); 
		
		this.shipToAddress =  getAddr(obj.getShipToAddr()); 
		
		this.installToAddress = getAddr(obj.getInstallAtAddr());
	}
	
	public String getAddr(ApplianceAddress addr){
		
		String addrStr = "";
		if(addr!=null){
			 if (StringUtils.isNotBlank(addr.getCustName())) { 
				 addrStr += addr.getCustName() +", ";
			 } if (addr.isShowPGSInfor() && StringUtils.isNotBlank(addr.getCustAddress())) { 
				 addrStr += addr.getCustAddress() +", ";
			 } if (addr.isShowPGSInfor() && StringUtils.isNotBlank(addr.getAddressIntrnl())) { 
				 addrStr += addr.getAddressIntrnl() +", ";
			 } if (StringUtils.isNotBlank(addr.getCustCity())) { 
				 addrStr += addr.getCustCity() +", ";
			 } if (StringUtils.isNotBlank(addr.getSapRegionCode())) {
				 addrStr += addr.getSapRegionCode() +", ";
			 } if (StringUtils.isNotBlank(addr.getPostalCode())) { 
				 addrStr += addr.getPostalCode() +", ";
			 } if (StringUtils.isNotBlank(addr.getCntryCode())) { 
				 addrStr += this.getCntryObject(addr.getCntryCode()).getDesc() +", ";
			 } 
			
		}
		return addrStr;
	}
	
	public Country getCntryObject(String cntryCode) {
        LogContext logContext = LogContextFactory.singleton().getLogContext();
        CacheProcess cProcess = null;
        Country cntryObj = null;
        try {
            cProcess = CacheProcessFactory.singleton().create();
            cntryObj = cProcess.getCountryByCode3(cntryCode);
            return cntryObj;
        } catch (QuoteException e) {
            logContext.error(this, e.getMessage());
            return null;
        }
    }	
	
	
}
