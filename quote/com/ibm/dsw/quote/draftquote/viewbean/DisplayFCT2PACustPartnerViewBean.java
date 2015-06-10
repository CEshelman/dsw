package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.appcache.process.CacheProcess;
import com.ibm.dsw.quote.appcache.process.CacheProcessFactory;
import com.ibm.dsw.quote.base.config.CustomerConstants;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.Contract;
import com.ibm.dsw.quote.common.domain.Customer;
import com.ibm.dsw.quote.common.domain.MigrateRequest;
import com.ibm.dsw.quote.common.domain.MigrateRequest_Impl;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.RselCtrldDistribtn;
import com.ibm.dsw.quote.customer.config.CustomerActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteActionKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.partner.config.PartnerActionKeys;
import com.ibm.dsw.quote.partner.config.PartnerMessageKeys;
import com.ibm.dsw.quote.partner.config.PartnerParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ActionHandlerKeys;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2012 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>DraftFCT2PACustResViewBean<code> class.
 * 
 * @author: zhoujunz@cn.ibm.com
 * 
 *          Creation date: 2012-5-22
 */

public class DisplayFCT2PACustPartnerViewBean extends MigrationReqBaseViewBean  {

	private MigrateRequest migrateResqt = null;
	private transient Customer cust = null;
	private Contract contract = null;
	private transient Partner reseller = null;
	private transient Partner payer = null;

	private String siteNumber = "";
	private String custRdcNumList = "";
	private String ibmCustNum = "";
	private String agreementNum = "";
	private String custName = "";
    private String custName2 = "";
    private String custName3 = "";
	private String address1 = "";
	private String address2 = "";
    private String custCntry = "";
	private String custZip = "";
	private String custCity = "";
	private String custState = "";

	private boolean isDisplayResellerInfo = false;
	private String resellerCustNum = "";
	private String resellerCustName = "";
	private String resellerAddress1 = "";
	private String resellerAddress2 = "";
	private String resellerCity = "";
	private String resellerState = "";
	private String resellerZip = "";
	private String resellerCntry = "";
	private String resellerRdcNum = "";
	private String resellerIbmCustNum = "";
	private String resellerAuthPortfolioList = "";

	private boolean isDisplayDistributorInfo = false;
	private boolean isDisplayAgreementNum = false;
	private String distributorCustNum = "";
	private String distributorCustName = "";
	private String distributorAddress1 = "";
	private String distributorAddress2 = "";
	private String distributorCity = "";
	private String distributorState = "";
	private String distributorZip = "";
	private String distributorCntry = "";
	private String distributorRdcNum = "";
	private String distributorIbmCustNum = "";
	private String fulfillmentSource = QuoteConstants.FULFILLMENT_DIRECT;

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.ead4j.common.bean.ModelCrawler#collectResults(com.ibm.ead4j.common
	 * .util.Parameters)
	 */
	public void collectResults(Parameters params) throws ViewBeanException {
		LogContext logContext = LogContextFactory.singleton().getLogContext();
		super.collectResults(params);
		
		migrateResqt=(MigrateRequest_Impl)params.getParameter(ParamKeys.PARAM_MIGRATION_REQUEST_OBJECT);

		if (migrateResqt == null) {
			return;
		}
		cust = migrateResqt.getCustomer();
		reseller = migrateResqt.getReseller();
		payer = migrateResqt.getPayer();
		contract = this.getFirstContract(cust);
		isDisplayAgreementNum = CustomerConstants.LOB_PA.equalsIgnoreCase(migrateResqt.getLob())?true:false;
		caNum=migrateResqt.getOrginalCANum();
		
		if (!"".equalsIgnoreCase(StringUtils.trimToEmpty(migrateResqt
				.getFulfillmentSrc()))) {
			fulfillmentSource = StringUtils.trimToEmpty(migrateResqt
					.getFulfillmentSrc());
		}
		if (cust != null) {

			custName = cust.getCustName();
		    custName2 = cust.getCustName2();
	        custName3 = cust.getCustName3();
			custCity = cust.getCity();
			siteNumber = cust.getCustNum();
			ibmCustNum = cust.getIbmCustNum();
			custState = cust.getSapRegionCode();
	        address1 = cust.getAddress1();
            address2 = cust.getInternalAddress();
			Country cntry = this.getCountry(cust.getCountryCode());
			if (cntry != null) {
				custState = cntry.getStateDescription(cust.getSapRegionCode());
				custCntry = cntry.getDesc();
			} else {
				custState = cust.getSapRegionCode();
				custCntry = cust.getCountryCode();
			}
			
			
			custRdcNumList = this.getRDCNumStringList(cust.getRdcNumList());
            if(contract != null) {
                agreementNum = contract.getSapContractNum() == null ? "" : contract.getSapContractNum();
            }

		}

		if (reseller != null) {
			isDisplayResellerInfo = true;
			resellerCustNum = reseller.getCustNum();
			resellerCustName = reseller.getCustNameFull();
			resellerAddress1 = reseller.getAddress1();
			resellerAddress2 = reseller.getAddress2();
			resellerCity = reseller.getCity();
			resellerZip = reseller.getPostalCode();
			resellerRdcNum = getRDCNumStringList(reseller.getRdcNumList());
			resellerIbmCustNum = reseller.getIbmCustNum();

			resellerAuthPortfolioList = getPortfolioListString(
					reseller.getAuthorizedPortfolioList(),
					reseller.getAuthorizedPortfolioMap());

			String cntryCode = StringUtils.trimToEmpty(reseller.getCountry());
			String stateCode = StringUtils.trimToEmpty(reseller.getState());
			Country cntry = this.getCountry(cntryCode);

			if (cntry != null) {
				resellerState = cntry.getStateDescription(stateCode);
				resellerCntry = cntry.getDesc();
			} else {
				resellerState = stateCode;
				resellerCntry = cntryCode;
			}

		}

		if (payer != null) {
			isDisplayDistributorInfo = true;
			distributorCustNum = payer.getCustNum();
			distributorCustName = payer.getCustNameFull();
			distributorAddress1 = payer.getAddress1();
			distributorAddress2 = payer.getAddress2();
			distributorCity = payer.getCity();
			distributorZip = payer.getPostalCode();
			distributorRdcNum = getRDCNumStringList(payer.getRdcNumList());
			distributorIbmCustNum = payer.getIbmCustNum();

			String cntryCode = StringUtils.trimToEmpty(payer.getCountry());
			String stateCode = StringUtils.trimToEmpty(payer.getState());
			Country cntry = this.getCountry(cntryCode);

			if (cntry != null) {
				distributorState = cntry.getStateDescription(stateCode);
				distributorCntry = cntry.getDesc();
			} else {
				distributorState = stateCode;
				distributorCntry = cntryCode;
			}
		}

	}


	public String getSelectParts2BeMigratedBtn() {

		String migrationPartBtnParams = genBtnParamsForAction(
				DraftQuoteActionKeys.POST_DISPLAY_FCT2PA_CUST_PARTNER,
				DraftQuoteActionKeys.DISPLAY_MIGRATE_PART_LIST,
			ParamKeys.PARAM_MIGRATION_REQSTD_NUM + "="
						+ migrationReqNum);

		return migrationPartBtnParams;

	}

	public String getSelectDiffCustBtn() {

       String country = (getCountry(cust.getCountryCode()) == null) ? "" : (getCountry(cust.getCountryCode())).getCode3();
		String custSearchBtnParams = genBtnParamsForAction(
				DraftQuoteActionKeys.POST_DISPLAY_FCT2PA_CUST_PARTNER,
				CustomerActionKeys.DISPLAY_SEARCH_CUSTOMER,
				 ParamKeys.PARAM_COUNTRY + "=" + country + ","
				 + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + migrateResqt.getLob() + ","
				+ ParamKeys.PARAM_SITE_NUM + "=" + siteNumber + ","
						+ ParamKeys.PARAM_MIGRATION_REQSTD_NUM + "="
						+ migrationReqNum + ","
						+ DraftQuoteParamKeys.PAGE_FROM+ "=" + DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER
						);
		return custSearchBtnParams;
	}

	public String getSelectResellerBtn() {
	    String country = (getCountry(cust.getCountryCode()) == null) ? "" : (getCountry(cust.getCountryCode())).getCode3();
		String resellerSearchBtnParams = genBtnParamsForAction(
				DraftQuoteActionKeys.POST_DISPLAY_FCT2PA_CUST_PARTNER,
				PartnerActionKeys.DISPLAY_RESELLER_SEARCH,
				 ParamKeys.PARAM_COUNTRY + "=" + country + ","
				 + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + migrateResqt.getLob() + ","
						 + ParamKeys.PARAM_MIGRATION_REQSTD_NUM + "="
						+ migrationReqNum + ","
						+ PartnerParamKeys.PARAM_SEARCH_TIER_TYPE + "=0,"
						+ DraftQuoteParamKeys.PAGE_FROM+ "=" + DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER
						);
		return resellerSearchBtnParams;
	}
	
	public String getDistributorSearchBtnParams() {
		
		 String country = (getCountry(cust.getCountryCode()) == null) ? "" : (getCountry(cust.getCountryCode())).getCode3();
			String distributorSearchBtnParams = genBtnParamsForAction(
					DraftQuoteActionKeys.POST_DISPLAY_FCT2PA_CUST_PARTNER,
					PartnerActionKeys.DISPLAY_DISTRIBUTOR_SEARCH,
					 ParamKeys.PARAM_COUNTRY + "=" + country + ","
					 + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + migrateResqt.getLob() + ","
						+ ParamKeys.PARAM_MIGRATION_REQSTD_NUM + "="
							+ migrationReqNum + ","
							+ DraftQuoteParamKeys.PAGE_FROM+ "=" + DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER
							);
			return distributorSearchBtnParams;
	}

	public String getSubmitForBtnParams() {
		
		String custSearchBtnParams = genBtnParamsForAction(
				DraftQuoteActionKeys.POST_DISPLAY_FCT2PA_CUST_PARTNER,
				"SUBMIT_FCTTOPA_MIGRATION_REQUEST",
			 ParamKeys.PARAM_MIGRATION_REQSTD_NUM + "="
						+ migrationReqNum);

		return custSearchBtnParams;
	}
	

	public boolean isDisplayResellerInfo() {
		return isDisplayResellerInfo;
	}

	private Country getCountry(String cntryCode) {
		LogContext logContext = LogContextFactory.singleton().getLogContext();
		Country cntry = null;
		try {
			CacheProcess cProcess = CacheProcessFactory.singleton().create();
			cntry = cProcess.getCountryByCode3(cntryCode);
		} catch (QuoteException e) {
			logContext.error(this, "Failed to get country by " + cntryCode);
		}
		return cntry;
	}
	
	

	public Customer getCust() {
		return cust;
	}


	public void setCust(Customer cust) {
		this.cust = cust;
	}


	public String getPortfolioListString(List portfolioList,
			Map authorizedPortfolioMap) {
		StringBuffer sb = new StringBuffer();
		if (portfolioList != null) {
			for (int i = 0; i < portfolioList.size(); i++) {
				boolean isGovRselOnly = true;
				CodeDescObj portfolio = (CodeDescObj) portfolioList.get(i);
				if (i == 0)
					sb.append(StringUtils.replace(portfolio.getCodeDesc(), "&",
							"&amp;"));
				else
					sb.append("<br />").append(
							StringUtils.replace(portfolio.getCodeDesc(), "&",
									"&amp;"));

				if (authorizedPortfolioMap != null) {
					List tier1List = (List) authorizedPortfolioMap
							.get(portfolio.getCode());
					if (tier1List == null)
						isGovRselOnly = false;
					else {
						for (int j = 0; j < tier1List.size(); j++) {
							RselCtrldDistribtn rselCtrldDistribtn = (RselCtrldDistribtn) tier1List
									.get(j);
							if ("0".equalsIgnoreCase(rselCtrldDistribtn
									.getSapGovRselFlag()))
								isGovRselOnly = false;
						}
					}
					if (isGovRselOnly) {
						ApplicationContext context = ApplicationContextFactory
								.singleton().getApplicationContext();
						String sGovRselOnly = context.getI18nValueAsString(
								I18NBundleNames.BASE_MESSAGES, locale,
								PartnerMessageKeys.MSG_GOV_RSEL_ONLY);
						sb.append(sGovRselOnly);
					}

				}

			}
		}
		return sb.toString();
	}

	public String getRDCNumStringList(List rdcList) {
		StringBuffer sb = new StringBuffer();
		if (rdcList != null) {
			for (int i = 0; i < rdcList.size(); i++) {
				if (i == 0)
					sb.append((String) rdcList.get(i));
				else
					sb.append("<br />" + (String) rdcList.get(i));
			}
		}
		return sb.toString();
	}

	public int getFulfillChoice() {
		int choice = 2;
	
			if (QuoteConstants.FULFILLMENT_CHANNEL
					.equalsIgnoreCase(fulfillmentSource))
				choice = 1;
			else if (QuoteConstants.FULFILLMENT_DIRECT
					.equalsIgnoreCase(fulfillmentSource))
				choice = 2;
		return choice;
	}

	public String getCustCityStateZip() {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotBlank(custCity))
			sb.append(custCity.trim());
		if (StringUtils.isNotBlank(custState)) {
			if (sb.length() == 0)
				sb.append(custState.trim());
			else
				sb.append(", " + custState.trim());
		}
		if (StringUtils.isNotBlank(custZip)) {
			if (sb.length() == 0)
				sb.append(custZip.trim());
			else if (StringUtils.isNotBlank(custState))
				sb.append(" " + custZip.trim());
			else
				sb.append(", " + custZip.trim());
		}
		return sb.toString();
	}

	public String getResellerCityStateZip() {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotBlank(resellerCity))
			sb.append(resellerCity.trim());
		if (StringUtils.isNotBlank(resellerState)) {
			if (sb.length() == 0)
				sb.append(resellerState.trim());
			else
				sb.append(", " + resellerState.trim());
		}
		if (StringUtils.isNotBlank(resellerZip)) {
			if (sb.length() == 0)
				sb.append(resellerZip.trim());
			else if (StringUtils.isNotBlank(resellerState))
				sb.append(" " + resellerZip.trim());
			else
				sb.append(", " + resellerZip.trim());
		}
		return sb.toString();
	}

	public String getDistributorCityStateZip() {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotBlank(distributorCity))
			sb.append(distributorCity.trim());
		if (StringUtils.isNotBlank(distributorState)) {
			if (sb.length() == 0)
				sb.append(distributorState.trim());
			else
				sb.append(", " + distributorState.trim());
		}
		if (StringUtils.isNotBlank(distributorZip)) {
			if (sb.length() == 0)
				sb.append(distributorZip.trim());
			else if (StringUtils.isNotBlank(distributorState))
				sb.append(" " + distributorZip.trim());
			else
				sb.append(", " + distributorZip.trim());
		}
		return sb.toString();
	}
    public String getCustName() {
        return custName == null ? "" : custName;
    }
    
    public String getCustName2() {
        return custName2 == null ? "" : custName2;
    }

    public String getCustName3() {
        return custName3 == null ? "" : custName3;
    }

    
	public String getCustRdcNumList() {
		return custRdcNumList == null ? "" : custRdcNumList;
	}

	public String getAddress1() {
		return address1 == null ? "" : address1;
	}
	
    public String getAddress2() {
        return address2 == null ? "" : address2;
    }

    public String getCustCntry() {
        return custCntry == null ? "" : custCntry;
    }

	public String getAgreementNum() {
		return agreementNum == null ? "" : agreementNum;
	}

	public String getIbmCustNum() {
		return ibmCustNum == null ? "" : ibmCustNum;
	}

	public String getSiteNumber() {
		return siteNumber == null ? "" : siteNumber;
	}

	public String getCustZip() {
		return custZip == null ? "" : custZip;
	}

	
	public boolean isDisplayDistributorInfo() {
        return isDisplayDistributorInfo;
    }
	public String getDistributorAddress1() {
		return distributorAddress1 == null ? "" : distributorAddress1;
	}

	public String getDistributorAddress2() {
		return distributorAddress2 == null ? "" : distributorAddress2;
	}

	public String getDistributorCity() {
		return distributorCity == null ? "" : distributorCity;
	}

	public String getDistributorCntry() {
		return distributorCntry == null ? "" : distributorCntry;
	}

	public String getDistributorCustName() {
		return distributorCustName == null ? "" : distributorCustName;
	}

	public String getDistributorCustNum() {
		return distributorCustNum == null ? "" : distributorCustNum;
	}

	public String getDistributorState() {
		return distributorState == null ? "" : distributorState;
	}

    public String getResellerRdcNum() {
        return resellerRdcNum == null ? "" : resellerRdcNum;
    }
	public String getDistributorZip() {
		return distributorZip == null ? "" : distributorZip;
	}

	public String getDistributorIbmCustNum() {
		return distributorIbmCustNum == null ? "" : distributorIbmCustNum;
	}

	public String getDistributorRdcNum() {
		return distributorRdcNum == null ? "" : distributorRdcNum;
	}

	public String getResellerAddress1() {
		return resellerAddress1 == null ? "" : resellerAddress1;
	}

	public String getResellerAddress2() {
		return resellerAddress2 == null ? "" : resellerAddress2;
	}
    public String getResellerIbmCustNum() {
        return resellerIbmCustNum == null ? "" : resellerIbmCustNum;
    }
    public String getResellerAuthPortfolioList() {
        return resellerAuthPortfolioList == null ? "" : resellerAuthPortfolioList;
    }
    
	public String getResellerCity() {
		return resellerCity == null ? "" : resellerCity;
	}

	public String getResellerCntry() {
		return resellerCntry == null ? "" : resellerCntry;
	}

	public String getResellerCustName() {
		return resellerCustName == null ? "" : resellerCustName;
	}

	public String getResellerCustNum() {
		return resellerCustNum == null ? "" : resellerCustNum;
	}

	public String getResellerState() {
		return resellerState == null ? "" : resellerState;
	}

	public String getResellerZip() {
		return resellerZip == null ? "" : resellerZip;
	}
	
	public boolean isDisplayAgreementNum() {
		return isDisplayAgreementNum;
	}


	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append("siteNumber").append(siteNumber).append("\n");
		buffer.append("custRdcNumList").append(custRdcNumList).append("\n");
		buffer.append("ibmCustNum").append(ibmCustNum).append("\n");
		buffer.append("agreementNum").append(agreementNum).append("\n");
		buffer.append("custName").append(custName).append("\n");
		buffer.append("custName2").append(custName2).append("\n");
		buffer.append("custName3").append(custName3).append("\n");
		buffer.append("address1").append(address1).append("\n");
		buffer.append("address2").append(address2).append("\n");
		buffer.append("custZip").append(custZip).append("\n");
		buffer.append("custCntry").append(custCntry).append("\n");
		buffer.append("custCity").append(custCity).append("\n");
		buffer.append("custState").append(custState).append("\n");
		buffer.append("isDisplayResellerInfo").append(isDisplayResellerInfo).append("\n");
		buffer.append("resellerCustNum").append(resellerCustNum).append("\n");
		buffer.append("resellerCustName").append(resellerCustName).append("\n");
		buffer.append("resellerAddress1").append(resellerAddress1).append("\n");
		buffer.append("resellerAddress2").append(resellerAddress2).append("\n");
		buffer.append("resellerCity").append(resellerCity).append("\n");
		buffer.append("resellerState").append(resellerState).append("\n");
		buffer.append("resellerZip").append(resellerZip).append("\n");
		buffer.append("resellerCntry").append(resellerCntry).append("\n");
		buffer.append("resellerRdcNum").append(resellerRdcNum).append("\n");
		buffer.append("resellerIbmCustNum").append(resellerIbmCustNum).append("\n");
		buffer.append("resellerAuthPortfolioList").append(resellerAuthPortfolioList).append("\n");
		buffer.append("isDisplayDistributorInfo").append(isDisplayDistributorInfo).append("\n");
		buffer.append("distributorCustNum").append(distributorCustNum).append("\n");
		buffer.append("distributorCustName").append(distributorCustName).append("\n");
		buffer.append("distributorAddress1").append(distributorAddress1).append("\n");
		buffer.append("distributorAddress2").append(distributorAddress2).append("\n");
		buffer.append("distributorCity").append(distributorCity).append("\n");
		buffer.append("distributorState").append(distributorState).append("\n");
		buffer.append("distributorZip").append(distributorZip).append("\n");
		buffer.append("distributorCntry").append(distributorCntry).append("\n");
		buffer.append("distributorRdcNum").append(distributorRdcNum).append("\n");
		buffer.append("distributorIbmCustNum").append(distributorIbmCustNum).append("\n");
		buffer.append("caNum").append(caNum).append("\n");
		

		return buffer.toString();
	}
	
    private String genBtnParamsForAction(String jadeAction, String redirectURL, String params) {
        ApplicationContext appContext = ApplicationContextFactory.singleton().getApplicationContext();
        String actionKey = appContext.getConfigParameter(ActionHandlerKeys.JADE_ACTION_KEY);
        String secondActionKey = appContext.getConfigParameter(QuoteConstants.JADE_SECOND_ACTION_KEY);
        StringBuffer sb = new StringBuffer();

        sb.append(actionKey).append("=").append(jadeAction);
        if (StringUtils.isNotBlank(redirectURL))
            sb.append(",").append(secondActionKey).append("=").append(redirectURL);
        if (StringUtils.isNotBlank(params))
            sb.append(",").append(params);
        return sb.toString();
    }
    
   private Contract getFirstContract(Customer customer) {
        if (customer == null || customer.getContractList() == null || customer.getContractList().size() == 0)
            return null;
        return (Contract) customer.getContractList().get(0);
    }
    

    
}
