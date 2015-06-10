package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApplianceLineItemAddrDetail  implements Serializable{

	private transient List<ApplianceLineItem> lineItemsList;
	
	private transient List<ApplianceAddress> shipToAddressList;
	
	private transient List<ApplianceAddress> installAtAddressList;
	
	private transient int installAtOpt;

	public List<ApplianceLineItem> getLineItemsList() {
		return lineItemsList;
	}

	public void setLineItemsList(List<ApplianceLineItem> lineItemsList) {
		this.lineItemsList = lineItemsList;
	}

	public List<ApplianceAddress> getShipToAddressList() {
		return shipToAddressList;
	}

	public void setShipToAddressList(List<ApplianceAddress> shipToAddressList) {
		this.shipToAddressList = shipToAddressList;
	}

	public List<ApplianceAddress> getInstallAtAddressList() {
		return installAtAddressList;
	}

	public void setInstallAtAddressList(List<ApplianceAddress> installAtAddressList) {
		this.installAtAddressList = installAtAddressList;
	}

	public int getInstallAtOpt() {
		return installAtOpt;
	}

	public void setInstallAtOpt(int installAtOpt) {
		this.installAtOpt = installAtOpt;
	}

	public static ApplianceLineItem createApplianceLineItem(QuoteLineItem quoteLineItem, ApplianceLineItemAddrDetail ads)
    {
    	ApplianceLineItem item = ApplianceLineItem.createApplianceLineItem(quoteLineItem);
    	List<ApplianceLineItem> list = ads.getLineItemsList();
    	if ( list == null )
    	{
    		list = new ArrayList<ApplianceLineItem>();
    		ads.setLineItemsList(list);
    	}
    	list.add(item);
    	return item;
    }
	
	public void addShipToAddr(ApplianceAddress addr)
	{
		if ( this.shipToAddressList == null )
		{
			this.shipToAddressList = new ArrayList<ApplianceAddress>();
		}
		this.shipToAddressList.add(addr);
	}
	
	public static ApplianceAddress getAddrFromCustomer(Customer cust)
	{
	    ApplianceAddress addr = new ApplianceAddress();
	    addr.setCustNum(cust.getCustNum());
	    addr.setCntryCode(cust.getCountryCode());
	    addr.setCustName(cust.getCustName());
	    addr.setCustAddress(cust.getAddress1());
	    addr.setPostalCode(cust.getPostalCode());
	    addr.setCustCity(cust.getCity());
	    addr.setSapRegionCode(cust.getSapRegionCode());
	    addr.setCntFirstName(cust.getCntFirstName());
	    addr.setCntLastName(cust.getCntLastName());
	    addr.setSapIntlPhoneNumFull(cust.getCntPhoneNumFull());
	    addr.setHeadLevelAddr(false);
	    return addr;
	}
}
