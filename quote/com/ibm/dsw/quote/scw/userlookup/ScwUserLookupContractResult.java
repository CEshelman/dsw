package com.ibm.dsw.quote.scw.userlookup;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.NONE)
public class ScwUserLookupContractResult {

	@XmlElement(name="contractNo")
	private String contractNo;
	
	@XmlElement(name="custNum")
	private String custNum;
	
	@XmlElement(name="contractType")
	private String contractType;
	
	@XmlElement(name="priceBand")
	private String priceBand;
	
	@XmlElementWrapper(name="products")
	@XmlElement(name="product")
	private List<String> products = new ArrayList<String>();
	

	//	public String getContractNo() {
	//	return contractNo;
	//}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	//public String getContractType() {
	//	return contractType;
	//}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	//public String getPriceBand() {
	//	return priceBand;
	//}

	public void setPriceBand(String priceBand) {
		this.priceBand = priceBand;
	}


	//public List<String> getProducts() {
	//	return products;
	//}

	public void setProducts(List<String> products) {
		this.products.addAll(products);
	}
	
	public void addProduct(String product) {
		this.products.add(product);
	}

	//public String getCustNum() {
	//	return custNum;
	//}

	public void setCustNum(String custNum) {
		this.custNum = custNum;
	}

}
