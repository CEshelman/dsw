package com.ibm.dsw.quote.common.service;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import DswSalesLibrary.FctToPaMigration;
import DswSalesLibrary.FctToPaMigrationHeader;
import DswSalesLibrary.FctToPaMigrationInput;
import DswSalesLibrary.FctToPaMigrationLineItem;
import DswSalesLibrary.FctToPaMigrationOutput;
import DswSalesLibrary.PartnerAddress;
import DswSalesLibrary.PartnerFunction;

import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.exception.WebServiceException;
import com.ibm.dsw.quote.base.exception.WebServiceUnavailableException;
import com.ibm.dsw.quote.common.config.CommonServiceConstants;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.MigratePart;
import com.ibm.dsw.quote.common.domain.MigrateRequest;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.wpi.java.util.ServiceLocator;
import com.ibm.dsw.wpi.java.util.ServiceLocatorException;

public class MigrationCreateServiceHelper extends QuoteBaseServiceHelper {

	public static final String TLS = "TLS";
	public static final String FMP = "FMP";
	public static final String FNSH= "WE"; //"SH"; //Ship-to
	public static final String FNBT= "RE"; //"BP"; //Bill-to
	public static final String FNPA= "RG"; //"PY"; //payer
    public static final String  ORDER_TYPE_ZWOR = "ZWOR";
    public static final String  ORDER_TYPE_ZORD = "ZORD";
    public MigrationCreateServiceHelper() {
        super();
    }

    public FctToPaMigrationOutput callFCTToPAMigrationService(MigrateRequest migrateRequest) throws WebServiceException,QuoteException {

    	FctToPaMigrationOutput fctToPaMigrationOutput = null;
    	FctToPaMigrationInput fctToPaMigrationInput = initFctToPaMigrationInput(migrateRequest);

        try {
            ServiceLocator serviceLocator = new ServiceLocator();
            FctToPaMigration fctToPAMigration = (FctToPaMigration) serviceLocator.getServicePort(
                    CommonServiceConstants.QUOTE_MIGRATION_BINDING, FctToPaMigration.class);
            fctToPaMigrationOutput = fctToPAMigration.execute(fctToPaMigrationInput);
       } catch (RemoteException e) {
            throw new WebServiceUnavailableException("The FCT to PA migration create service is unavailable now."
                    + getMigrationInfo(migrateRequest), e);
        } catch (ServiceLocatorException e) {
            throw new WebServiceUnavailableException("The FCT to PA migration create service is unavailable now."
                    + getMigrationInfo(migrateRequest), e);
        } catch (Exception e) {
            throw new WebServiceUnavailableException("The FCT to PA migration create service is unavailable now."
                    + getMigrationInfo(migrateRequest), e);
        }

        return fctToPaMigrationOutput;
    }

    protected FctToPaMigrationInput initFctToPaMigrationInput(MigrateRequest migrateRequest) throws WebServiceException{
    	FctToPaMigrationInput migrationInput = new FctToPaMigrationInput();
        logContext.debug(this, "Generating FctToPaMigration input:");
        migrationInput.setHeader(genFctToPaMigrationHeader(migrateRequest));
        migrationInput.setLineItems( genFctToPaMigrationLineItems(migrateRequest));
        migrationInput.setPartnerAddresses(genPartnerAddress(migrateRequest));
        migrationInput.setPartnerFunctions(genPartnerFunctions(migrateRequest));

		return migrationInput;
    }
    
    protected FctToPaMigrationLineItem[] genFctToPaMigrationLineItems(MigrateRequest migrateRequest){
		ArrayList<FctToPaMigrationLineItem> lineItemList = new ArrayList<FctToPaMigrationLineItem>();
    	List<MigratePart> parts = migrateRequest.getParts();
    	for(MigratePart part:parts){
    		FctToPaMigrationLineItem lineItem = new FctToPaMigrationLineItem();
    		lineItem.setOrderId(migrateRequest.getRequestNum());
    		lineItem.setItemNumber(part.getSeqNum());
    		lineItemList.add(lineItem);
    	}
    	return (FctToPaMigrationLineItem[])lineItemList.toArray(new FctToPaMigrationLineItem[0]);
    }

    protected FctToPaMigrationHeader genFctToPaMigrationHeader(MigrateRequest migrateRequest) throws WebServiceException {
    	FctToPaMigrationHeader migrationHeader = new FctToPaMigrationHeader();
    	migrationHeader.setIdentifier(TLS);
    	migrationHeader.setAcqCode(migrateRequest.getAcqCode());
    	migrationHeader.setBillFrequency(migrateRequest.getBillingFreq()==null?"":migrateRequest.getBillingFreq().trim());
    	migrationHeader.setCurrencyCode(migrateRequest.getCustomer().getCurrencyCode());
    	migrationHeader.setDistributionChannelCode(migrateRequest.getOrignalSapDistChnl()==null?"":migrateRequest.getOrignalSapDistChnl().trim());
    	migrationHeader.setOrderId(migrateRequest.getRequestNum());
    	String orderType = this.getQuoteType(migrateRequest);
		migrationHeader.setOrderType(orderType);
		if(ORDER_TYPE_ZWOR.equalsIgnoreCase(orderType)){
			migrationHeader.setContractNumber(migrateRequest.getSapCtrctNum());
		}else if (ORDER_TYPE_ZORD.equalsIgnoreCase(orderType)){
			migrationHeader.setContractNumber(null);
		}
    	migrationHeader.setOrigDocNumber(migrateRequest.getOrginalCANum());
    	migrationHeader.setPaymentCode(FMP);
    	migrationHeader.setSalesOrganization(migrateRequest.getCustomer().getSalesOrg());
    	migrationHeader.setTerm(migrateRequest.getCoverageTerm()==0?null:migrateRequest.getCoverageTerm());

        logContext.debug(this, "FCT to PA miagration header begin:");
        logContext.debug(this, "Migration request["+migrateRequest.getRequestNum()+"] identifier = TLS");
        logContext.debug(this, "Migration request["+migrateRequest.getRequestNum()+"] acqCode = " + migrateRequest.getAcqCode());
        logContext.debug(this, "Migration request["+migrateRequest.getRequestNum()+"] BillFrequency = " + migrateRequest.getBillingFreq());
        logContext.debug(this, "Migration request["+migrateRequest.getRequestNum()+"] ContractNumber = " + migrateRequest.getSapCtrctNum());
        logContext.debug(this, "Migration request["+migrateRequest.getRequestNum()+"] CurrencyCode = " + migrateRequest.getCustomer().getCurrencyCode());
        logContext.debug(this, "Migration request["+migrateRequest.getRequestNum()+"] DistributionChannelCode = " + migrateRequest.getOrignalSapDistChnl()==null?"":migrateRequest.getOrignalSapDistChnl().trim());
        logContext.debug(this, "Migration request["+migrateRequest.getRequestNum()+"] OrderId = " + migrateRequest.getRequestNum());
        logContext.debug(this, "Migration request["+migrateRequest.getRequestNum()+"] OrderType = " + migrateRequest.getLob());
        logContext.debug(this, "Migration request["+migrateRequest.getRequestNum()+"] OrigDocNumber = " + migrateRequest.getOrginalCANum());
        logContext.debug(this, "Migration request["+migrateRequest.getRequestNum()+"] PaymentCode = FMP");
        logContext.debug(this, "Migration request["+migrateRequest.getRequestNum()+"] SalesOrganization = " + migrateRequest.getCustomer().getSalesOrg());
        logContext.debug(this, "Migration request["+migrateRequest.getRequestNum()+"] Term = " + (migrateRequest.getCoverageTerm()==0?null:migrateRequest.getCoverageTerm()));
        return migrationHeader;
    }

    protected String getMigrationInfo(MigrateRequest migrateRequest){
    	if(migrateRequest == null){
    		return "";
    	}else {
    		return "FCT to PA migration :"+migrateRequest.getRequestNum();
    	}
    }
    
    protected String getQuoteType(MigrateRequest migrateRequest) {
		if(QuoteConstants.LOB_PA.equalsIgnoreCase(migrateRequest.getLob())){
			return ORDER_TYPE_ZWOR;			
		}else {
			return ORDER_TYPE_ZORD;
		}
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected PartnerFunction[] genPartnerFunctions(MigrateRequest migrateRequest) {

        if (migrateRequest == null)
            return new PartnerFunction[0];

        ArrayList funcList = new ArrayList();
        String fulfillmentSrc = migrateRequest.getFulfillmentSrc().trim();

        // set the sold-to info
        String soldToCustNum = migrateRequest.getSoldToCustNum();
		PartnerFunction prtnrFunctionSoldTo = new PartnerFunction();
		prtnrFunctionSoldTo.setPartnerFunctionCode(CUST_PRTNR_FUNC_SOLD_TO);
		prtnrFunctionSoldTo.setPartnerCustomerNumber(soldToCustNum);
		funcList.add(prtnrFunctionSoldTo);

		// set the reseller info
		String resellerCustNum = "";
        if (QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(fulfillmentSrc) && StringUtils.isBlank(migrateRequest.getPayerCustNum())) {
             resellerCustNum = migrateRequest.getSoldToCustNum();
        } else {
             resellerCustNum = migrateRequest.getReslCustNum();
        }
        PartnerFunction prtnrFunctionReseller = new PartnerFunction();
        prtnrFunctionReseller.setPartnerFunctionCode(CUST_PRTNR_FUNC_RSEL);
        prtnrFunctionReseller.setPartnerCustomerNumber(resellerCustNum);
        funcList.add(prtnrFunctionReseller);

		// set the payer info
		String payerCustNum = "";
		if (QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(fulfillmentSrc) && StringUtils.isBlank(migrateRequest.getPayerCustNum())) {
		     payerCustNum = migrateRequest.getSoldToCustNum();
		} else {
			 payerCustNum = migrateRequest.getPayerCustNum();
		}
		PartnerFunction prtnrFunctionPayer = new PartnerFunction();
		prtnrFunctionPayer.setPartnerFunctionCode(CUST_PRTNR_FUNC_PAYER);
		prtnrFunctionPayer.setPartnerCustomerNumber(payerCustNum);
		funcList.add(prtnrFunctionPayer);

		logContext.debug(this, "Partner function begin:");
		logContext.debug(this, "soldToCustNum = " + soldToCustNum);
		logContext.debug(this, "payerCustNum = " + payerCustNum);

		return (PartnerFunction[]) funcList.toArray(new PartnerFunction[0]);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected PartnerAddress[] genPartnerAddress(MigrateRequest migrateRequest) {
        if (migrateRequest == null)
            return new PartnerAddress[0];

		ArrayList prtnrAddrList = new ArrayList();
		String fulfillmentSrc = migrateRequest.getFulfillmentSrc().trim();
	
		//Set payer addresses. 
		String payerCustNum = "";
		PartnerAddress payerAddress= new PartnerAddress();
		if (QuoteConstants.FULFILLMENT_DIRECT.equalsIgnoreCase(fulfillmentSrc)&& StringUtils.isBlank(migrateRequest.getPayerCustNum())) {
		        payerCustNum = migrateRequest.getSoldToCustNum();
		        Customer customer = migrateRequest.getCustomer();
				payerAddress.setPartnerFunctionCode(FNPA);
				payerAddress.setPartnerCustomerNumber(payerCustNum);
				payerAddress.setAddress1(customer.getAddress1());
//				payerAddress.setAddress2();
				payerAddress.setCity(customer.getCity());
				payerAddress.setCountryCode(customer.getCountryCode());
				payerAddress.setName1(customer.getCntFirstName());
				payerAddress.setName2(customer.getCntLastName());
				payerAddress.setPostalCode(customer.getPostalCode());
				System.out.println(customer.getSapRegionCode());
				payerAddress.setRegionCode(customer.getSapRegionCode());
		} else {
				payerCustNum = migrateRequest.getPayerCustNum();
				Partner payer = migrateRequest.getPayer();
				payerAddress.setPartnerFunctionCode(FNPA);
				payerAddress.setPartnerCustomerNumber(payerCustNum);
				payerAddress.setAddress1(payer.getAddress1());
				payerAddress.setAddress2(payer.getAddress2());
				payerAddress.setCity(payer.getCity());
				payerAddress.setCountryCode(payer.getCountry());
				payerAddress.setName1(payer.getCustName());
				payerAddress.setName2(payer.getCustName2());
				payerAddress.setPostalCode(payer.getPostalCode());
				payerAddress.setRegionCode(payer.getState());
		}
		prtnrAddrList.add(payerAddress);
		
		logContext.debug(this, "Partner Address begin:");
		logContext.debug(this, "PartnerCustomerNumber = " + payerCustNum);
		
        return (PartnerAddress[]) prtnrAddrList.toArray(new PartnerAddress[0]);
    }

}
