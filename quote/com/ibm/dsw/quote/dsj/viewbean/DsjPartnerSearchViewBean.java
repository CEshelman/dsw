package com.ibm.dsw.quote.dsj.viewbean;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.common.domain.Partner;
import com.ibm.dsw.quote.common.domain.PortfolioWithGovFlag;
import com.ibm.dsw.quote.common.domain.SearchResultList;
import com.ibm.dsw.quote.draftquote.util.StringUtils;
import com.ibm.dsw.quote.partner.config.PartnerParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;

public class DsjPartnerSearchViewBean extends DsjBaseViewBean {

	private static final long serialVersionUID = 5422956874992982991L;
	private SearchResultList partnerList;
	private String returnCode;

	public void collectResults(Parameters parameters) throws ViewBeanException {
        super.collectResults(parameters);
        partnerList = (SearchResultList) parameters.getParameter(PartnerParamKeys.DSJ_PARTNER_SEARCH_LIST);
        returnCode = parameters.getParameterAsString(PartnerParamKeys.DSJ_PARTNER_SEARCH_RETURN_CODE);
    }

	public String getReturnCode() {
		return returnCode;
	}

	public SearchResultList getPartnerList() {
		return partnerList;
	}

	public void setPartnerList(SearchResultList partnerList) {
		this.partnerList = partnerList;
	}
	
	public String getDSJPartnerSearchResultJSON(){
		
		StringBuilder resultBuf = new StringBuilder("\"searchResult\":{");
		
		if (partnerList != null && partnerList.getRealSize() == 1) {
			
			Partner partner  = (Partner)partnerList.getResultList().get(0);
			
			resultBuf.append("\"")
					 .append(StringUtils.jsonStringEncoding(PartnerParamKeys.PARAM_SITE_NUM))
					 .append("\":\"")
					 .append(StringUtils.jsonStringEncoding(partner.getCustNum()))
					 .append("\",");
			
			resultBuf.append("rdcNumList: [");
			List rdcNumList = partner.getRdcNumList();
			if(null != rdcNumList && rdcNumList.size() > 0){
				Iterator it = rdcNumList.iterator();
				while(it.hasNext()){
					String crdNum = (String)it.next();
					if(null != crdNum){
						resultBuf.append("{\"")
								 .append(StringUtils.jsonStringEncoding(PartnerParamKeys.PARAM_CRD_NUMBER))
								 .append("\":\"")
								 .append(StringUtils.jsonStringEncoding(crdNum))
								 .append("\"},");
					}
				}
				resultBuf.deleteCharAt(resultBuf.length()-1);
			}
			resultBuf.append("],");
			
			resultBuf.append("\"")
					 .append(StringUtils.jsonStringEncoding(PartnerParamKeys.PARAM_IBM_CUST_NUM))
					 .append("\":\"")
					 .append(StringUtils.jsonStringEncoding(partner.getIbmCustNum()))
					 .append("\",");
			resultBuf.append("\"")
					 .append(StringUtils.jsonStringEncoding(PartnerParamKeys.PARAM_CUST_NAME_FULL))
					 .append("\":\"")
					 .append(StringUtils.jsonStringEncoding(partner.getCustNameFull()))
					 .append("\",");
			resultBuf.append("\"")
					 .append(StringUtils.jsonStringEncoding(PartnerParamKeys.PARAM_PARTNER_ADDRESS_1))
					 .append("\":\"")
					 .append(StringUtils.jsonStringEncoding(partner.getAddress1()))
					 .append("\",");
			resultBuf.append("\"")
					 .append(StringUtils.jsonStringEncoding(PartnerParamKeys.PARAM_PARTNER_ADDRESS_2))
					 .append("\":\"")
					 .append(StringUtils.jsonStringEncoding(partner.getAddress2()))
					 .append("\",");
			resultBuf.append("\"")
					 .append(StringUtils.jsonStringEncoding(PartnerParamKeys.PARAM_PARTNER_CITY))
					 .append("\":\"")
					 .append(StringUtils.jsonStringEncoding(partner.getCity()))
					 .append("\",");
			resultBuf.append("\"")
					 .append(StringUtils.jsonStringEncoding(PartnerParamKeys.PARAM_STATE))
					 .append("\":\"")
					 .append(StringUtils.jsonStringEncoding(partner.getState()))
					 .append("\",");
			resultBuf.append("\"")
					 .append(StringUtils.jsonStringEncoding(PartnerParamKeys.PARAM_POST_CODE))
					 .append("\":\"")
					 .append(StringUtils.jsonStringEncoding(partner.getPostalCode()))
					 .append("\",");
			
			resultBuf.append("\"")
					 .append(StringUtils.jsonStringEncoding(PartnerParamKeys.PARAM_COUNTRY))
					 .append("\":\"")
					 .append(StringUtils.jsonStringEncoding(partner.getCountry()))
					 .append("\",");
			
			resultBuf.append("\"")
					 .append(StringUtils.jsonStringEncoding(PartnerParamKeys.PARAM_RESELLER_TYPE))
					 .append("\":\"")
					 .append(this.getI18NString(I18NBundleNames.BASE_MESSAGES, locale, "partner_tier" + partner.getTierType()))
					 .append("\",");
			
			resultBuf.append("authorizedList: [");
			List authorizedList = partner.getAuthorizedPortfolioList();
			if(null != authorizedList && authorizedList.size() > 0){
				Iterator it = authorizedList.iterator();
				while(it.hasNext()){
					PortfolioWithGovFlag portfolioWithGovFlag = (PortfolioWithGovFlag)it.next();
					if(null != portfolioWithGovFlag){
						resultBuf.append("{govermentFlag:\"")
								 .append(StringUtils.jsonStringEncoding(portfolioWithGovFlag.getGovermentFlag()))
								 .append("\",portfolioCodeDesc:\"")
								 .append(portfolioWithGovFlag.getPortfolio() != null ? portfolioWithGovFlag.getPortfolio().getCodeDesc() : "")
								 .append("\"},");
					}
				}
				resultBuf.deleteCharAt(resultBuf.length()-1);
			}
			resultBuf.append("]");
		}
		
		resultBuf.append("}");
		return resultBuf.toString();
	}
	
    protected String getI18NString(String basename, Locale locale, String key) {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        return appCtx.getI18nValueAsString(basename, locale, key);
    }
}
