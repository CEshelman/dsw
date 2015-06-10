package com.ibm.dsw.quote.ps.viewbean;


import java.io.File;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import com.ibm.dsw.common.security.SecurityUtil;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.util.HtmlUtil;
import com.ibm.dsw.quote.base.viewbean.BaseViewBean;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.SalesRep;
import com.ibm.dsw.quote.draftquote.util.StringFilter;
import com.ibm.dsw.quote.ps.config.PartSearchActionKeys;
import com.ibm.dsw.quote.ps.config.PartSearchMessageKeys;
import com.ibm.dsw.quote.ps.config.PartSearchParamKeys;
import com.ibm.ead4j.common.config.EAD4JBootstrapKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContextFactory;


/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 *
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 *
 * The <code>PartSearchResultViewBean</code>
 *
 *
 * @author: chenzhh@cn.ibm.com
 *
 * Creation date: Jan 26, 2007
 */
public class PartSearchViewBean extends BaseViewBean {

    String audience = SalesRep.AUDIENCE;
    //use URL like this to enter browse or find parts
    //.../quote/quote.wss?jadeAction=DISPLAY_PARTSEARCH_BROWSE_RESULT&creatorID=123456&country=USA&currency=USD&lob=PA&audience=INTERNAL
    //.../quote/quote.wss?jadeAction=DISPLAY_PARTSEARCH_FIND&creatorID=123456&country=USA&currency=USD&lob=PA&audience=INTERNAL
    String lob;	// = "PA"; //Set to PA or PAE for PA, set to IEMS for NonPA, PA as default.
    String acqrtnCode;
    String country;	// = "USA"; //USA as default.
    String currency;	// = "USD"; //USD as default.
    String countryDesc;
    String currencyDesc;
    String[] searchPartnums;
    String description;
    String state;
    String quoteNumber;
    String PartSearchURLParams;

    //values for tree section of jsp
    String javascriptLocation = "js/ajaxtree.js";
    String treeName;	// = "ProdPartTree"; //for 4 level tree, set to BrandProdTypePartTree, for 3 level tree, set to ProdTypePartTree, for 2 level tree set to ProdPartTree
    String dataRetrievalType; //for search by num, set to byNum, for by description, set to byDesc, (browse not yet implemented) for browse set to browse
    String autoExpandLevel;
    String brand;	// = "DB2";
    String treeServletName = HtmlUtil.getURLForAction(PartSearchActionKeys.TREE_CONTROLLER);
    String treeTitle;
    String treeHeader1 = "Part number";
    String treeHeader2 = "Part description";
    boolean showHint;

    String renewal = "false";
    String submitted = "false";

    String seqNum = "";
    String replacementFlag = "false";

    String migrationCode = "";
    String quoteFlag = "";
    List brandsTabLinks = null;
    String[] brandsList = null;
    String addNewMonthlySWFlag = "false";
    String configrtnId;
    String configrtnActionCode;
    String chrgAgrmtNum;
    String orgConfigId;
    

    public String getDescription() {
        return description;
    }

    public String[] getSearchPartnums() {
        return searchPartnums;
    }

    public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);

        description = params.getParameterAsString(PartSearchParamKeys.PARAM_PART_DESCRIPTION);
        String numbers = params.getParameterAsString(PartSearchParamKeys.PARAM_PART_NUMBERS);
        brand = params.getParameterAsString(PartSearchParamKeys.PARAM_PART_BRAND);
        if(numbers != null){
            StringTokenizer st = new StringTokenizer(numbers,", \t\n\r\f");
            searchPartnums = new String[st.countTokens()];
            int i = 0;
            while(st.hasMoreTokens()){
                searchPartnums[i++] = st.nextToken();
            }
        }
        treeName = params.getParameterAsString(PartSearchParamKeys.TREE_NAME);
        lob = params.getParameterAsString(ParamKeys.PARAM_LINE_OF_BUSINESS);
        migrationCode = params.getParameterAsString(ParamKeys.PARAM_PROG_MIGRATION_CODE);
        quoteFlag = params.getParameterAsString(ParamKeys.PARAM_QUOTE_FLAG);
        acqrtnCode = params.getParameterAsString(ParamKeys.PARAM_ACQRTN_CODE);
        Country contry = (Country)params.getParameter(ParamKeys.PARAM_COUNTRY);
        country = contry.getCode3();
        countryDesc = contry.getDesc();
        currency = (String)params.getParameter(ParamKeys.PARAM_CURRENCY);
        CodeDescObj cntryCurrency = (CodeDescObj)params.getParameter(PartSearchParamKeys.PARAM_COUNTRY_CURRENCY);
        currencyDesc = cntryCurrency.getCodeDesc();
        dataRetrievalType = params.getParameterAsString(PartSearchParamKeys.PARAM_DATA_RETRIEVALTYPE);
        state = params.getParameterAsString("state");
        quoteNumber = params.getParameterAsString(ParamKeys.PARAM_QUOTE_NUM);
        brand = params.getParameterAsString(PartSearchParamKeys.PARAM_PART_BRAND);
	    brandsTabLinks = (List)params.getParameter(PartSearchParamKeys.BROWSE_BRANDS_TABLINKS);
	    brandsList = (String[])params.getParameter(PartSearchParamKeys.BROWSE_BRANDS_LIST);
        treeTitle = treeName + " " + dataRetrievalType;
        Boolean showHintObj = (Boolean)params.getParameter(PartSearchParamKeys.PARAM_SHOWHINT);
        showHint = showHintObj == null? false: showHintObj.booleanValue();
        Boolean isRenewal = (Boolean)params.getParameter(PartSearchParamKeys.PARAM_RENEWAL);
        Boolean isSubmitted = (Boolean)params.getParameter(PartSearchParamKeys.PARAM_SUBMITTED);
        addNewMonthlySWFlag = params.getParameterAsString(PartSearchParamKeys.PARAM_ADD_NEW_MONTHLY_SW) == null ? "false" : params.getParameterAsString(PartSearchParamKeys.PARAM_ADD_NEW_MONTHLY_SW); 
        chrgAgrmtNum=params.getParameterAsString(ParamKeys.PARAM_CHRG_AGRMT_NUM) == null ? "" : params.getParameterAsString(ParamKeys.PARAM_CHRG_AGRMT_NUM);
        configrtnActionCode=params.getParameterAsString(ParamKeys.PARAM_CONFIGRTN_ACTION_CODE) == null ? "" : params.getParameterAsString(ParamKeys.PARAM_CONFIGRTN_ACTION_CODE);
        configrtnId=params.getParameterAsString(ParamKeys.PARAM_CONFIGRTN_ID) == null ? "" : params.getParameterAsString(ParamKeys.PARAM_CONFIGRTN_ID);
        orgConfigId=params.getParameterAsString(ParamKeys.PARAM_ORG_CONFIG_ID) == null ? "" : params.getParameterAsString(ParamKeys.PARAM_ORG_CONFIG_ID);
        
        SecurityUtil.htmlEncode(addNewMonthlySWFlag);
        SecurityUtil.htmlEncode(chrgAgrmtNum);
        SecurityUtil.htmlEncode(configrtnActionCode);
        SecurityUtil.htmlEncode(configrtnId);
        SecurityUtil.htmlEncode(orgConfigId);
        
        if (isRenewal != null && isRenewal.booleanValue()) {
            renewal = "true";
        }
        if (isSubmitted != null && isSubmitted.booleanValue()) {
            submitted = "true";
        }
        seqNum = params.getParameterAsString(PartSearchParamKeys.PARAM_SEQ_NUM);
        Boolean isReplacementFlag = (Boolean)params.getParameter(PartSearchParamKeys.PARAM_REPLACEMENT_FLAG);
        if (isReplacementFlag != null && isReplacementFlag.booleanValue()) {
            replacementFlag = "true";
        }

        if (isPGSFlag()) {
            audience = QuoteConstants.QUOTE_AUD_CODE.PSPTRSEL;
        }

    }

    public boolean isPGSFlag() {
        return QuoteConstants.QUOTE_AUDIENCE_CODE_PGS.equals(quoteUserSession.getAudienceCode());
    }

    /**
     * @return Returns the autoExpandLevel.
     */
    public String getAutoExpandLevel() {
        return autoExpandLevel;
    }

    /**
     * @param autoExpandLevel The autoExpandLevel to set.
     */
    public void setAutoExpandLevel(String autoExpandLevel) {
        this.autoExpandLevel = autoExpandLevel;
    }

    /**
     * @return Returns the dataRetrievalType.
     */
    public String getDataRetrievalType() {
        return dataRetrievalType;
    }

    /**
     * @param dataRetrievalType The dataRetrievalType to set.
     */
    public void setDataRetrievalType(String dataRetrievalType) {
        this.dataRetrievalType = dataRetrievalType;
    }

    /**
     * @return Returns the javascriptLocation.
     */
    public String getJavascriptLocation() {
        return javascriptLocation;
    }

    /**
     * @param javascriptLocation The javascriptLocation to set.
     */
    public void setJavascriptLocation(String javascriptLocation) {
        this.javascriptLocation = javascriptLocation;
    }

    /**
     * @return Returns the treeHeader1.
     */
    public String getTreeHeader1() {
        return treeHeader1;
    }

    /**
     * @param treeHeader1 The treeHeader1 to set.
     */
    public void setTreeHeader1(String treeHeader1) {
        this.treeHeader1 = treeHeader1;
    }

    /**
     * @return Returns the treeHeader2.
     */
    public String getTreeHeader2() {
        return treeHeader2;
    }

    /**
     * @param treeHeader2 The treeHeader2 to set.
     */
    public void setTreeHeader2(String treeHeader2) {
        this.treeHeader2 = treeHeader2;
    }

    /**
     * @return Returns the treeName.
     */
    public String getTreeName() {
        return treeName;
    }

    /**
     * @return Returns the treeServletName.
     */
    public String getTreeServletName() {
        return treeServletName;
    }

    /**
     * @return Returns the treeTitle.
     */
    public String getTreeTitle() {
        return treeTitle;
    }

    protected String getSearchItems() {
        StringBuffer partnumsb = new StringBuffer();
        /*
        //temporary
        searchItems = new String[5];
        searchItems[0] = "D51Q2LL";
        searchItems[1] = "D51Q7LL";
        searchItems[2] = "E00J1LL";
        searchItems[3] = "D5AW0LL";
        searchItems[4] = "D5AW3LL";
        //end temporary
         * D51Q2LL,D51Q7LL,E00J1LL,D5AW0LL,D5AW3LL
         * */
        for(int i=0;i<searchPartnums.length;i++) {
            if(i>0) partnumsb.append(",");
            partnumsb.append(searchPartnums[i]);
        }
        return partnumsb.toString();

    }

    public String getSearchString() {
        String result = "";
        if(dataRetrievalType.equals(PartSearchParamKeys.SEARCH_PARTS_BY_DESCRIPTION)) {
            result = description;
        } else if(dataRetrievalType.equals(PartSearchParamKeys.SEARCH_PARTS_BY_NUMBERS)) {
	        result = getSearchItems();
        } else if(dataRetrievalType.equals(PartSearchParamKeys.GET_RELATED_PARTS)) {
	        result = getSearchItems();
        } else if(dataRetrievalType.equals(PartSearchParamKeys.GET_RELATED_PARTS_LIC)) {
	        result = getSearchItems();
        } else if(dataRetrievalType.equals(PartSearchParamKeys.GET_REPLACEMENT_PARTS)) {
            result = getSearchItems();
        }
        return result;
    }

    /**
     * @return Returns the audience.
     */
    public String getAudience() {
        return audience;
    }

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return Returns the currncy.
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @return Returns the lob.
     */
    public String getLob() {
        return lob;
    }

    public String getAcqrtnCode() {
        return acqrtnCode == null ? "": acqrtnCode;
    }

    /**
     * @return Returns the searchBrand.
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @return Returns the Brands TabLinks.
     */
    public List getTabLinks() {
        return brandsTabLinks;
    }  

    /**
     * @return Returns the Brands List.
     */
    public String[] getBrandsList() {
        return brandsList;
    }  
    
    /**
     * @return Returns the quoteNumber.
     */
    public String getQuoteNumber() {
        return quoteNumber;
    }
    /**
     * @param quoteNumber The quoteNumber to set.
     */
    public void setQuoteNumber(String quoteNumber) {
        this.quoteNumber = quoteNumber;
    }
    /**
     * @return Returns the state.
     */
    public String getState() {
        return state;
    }
    /**
     * @param state The state to set.
     */
    public void setState(String state) {
        this.state = state;
    }
        
    public String getConfigrtnId() {
		return configrtnId;
	}

	public void setConfigrtnId(String configrtnId) {
		this.configrtnId = configrtnId;
	}

	public String getConfigrtnActionCode() {
		return configrtnActionCode;
	}

	public void setConfigrtnActionCode(String configrtnActionCode) {
		this.configrtnActionCode = configrtnActionCode;
	}

	public String getChrgAgrmtNum() {
		return chrgAgrmtNum;
	}

	public void setChrgAgrmtNum(String chrgAgrmtNum) {
		this.chrgAgrmtNum = chrgAgrmtNum;
	}
	
	public String getOrgConfigId() {
		return orgConfigId;
	}

	/**
     * @return Returns the partSearchURLParams.
     */
    public String getPartSearchURLParams() {
        return ParamKeys.PARAM_COUNTRY + "=" + getCountry()
        	+ "&amp;" + ParamKeys.PARAM_CURRENCY + "=" + getCurrency()
        	+ "&amp;" + ParamKeys.PARAM_LINE_OF_BUSINESS + "=" + getLob()
        	+ "&amp;" + ParamKeys.PARAM_AUDIENCE + "=" + getAudience()
        	+ "&amp;" + ParamKeys.PARAM_QUOTE_NUM + "=" + getQuoteNumber()
        	+ "&amp;" + ParamKeys.PARAM_PROG_MIGRATION_CODE + "=" + getMigrationCode()
        	+ "&amp;" + ParamKeys.PARAM_QUOTE_FLAG + "=" + getQuoteFlag()
        	+ "&amp;" + PartSearchParamKeys.PARAM_RENEWAL + "=" + getRenewal()
            + "&amp;" + PartSearchParamKeys.PARAM_ADD_NEW_MONTHLY_SW + "=" + getAddNewMonthlySWFlag()
        	+ "&amp;" + ParamKeys.PARAM_CHRG_AGRMT_NUM + "=" + getChrgAgrmtNum()
        	+ "&amp;" + ParamKeys.PARAM_CONFIGRTN_ID + "=" + getConfigrtnId()
        	+ "&amp;" + ParamKeys.PARAM_CONFIGRTN_ACTION_CODE + "=" + getConfigrtnActionCode()
        	+ "&amp;" + ParamKeys.PARAM_ORG_CONFIG_ID + "=" + getOrgConfigId();
    }


    public String[] getElaLimits(){
        String[] limits = new String[3];
        limits[0] = String.valueOf(PartPriceConfigFactory.singleton().getPartSearchLimits());
        limits[1] = String.valueOf(PartPriceConfigFactory.singleton().getElaLimits());
        limits[2] = String.valueOf(PartPriceConfigFactory.singleton().getAppliLimits());
    	return limits;
    }

    public String getCountryDesc() {
        return countryDesc;
    }

    public String getCurrencyDesc() {
        return currencyDesc;
    }
    public boolean isPAOrPAE(){

    	return QuoteConstants.LOB_PA.equalsIgnoreCase(getLob())||QuoteConstants.LOB_PAE.equalsIgnoreCase(getLob());
    }
    /**
     * @return Returns the showHint.
     */
    public boolean isShowHint() {
        return showHint;
    }
    public String getHintMsg(){
        if(QuoteConstants.LOB_FCT.equals(lob)){
            return PartSearchMessageKeys.CURRENCY_HINT_FCT;
        }else{
            return PartSearchMessageKeys.CURRENCY_HINT;
        }
    }
    public String getRenewal() {
        return renewal;
    }

    public String getSubmitted() {
        return submitted;
    }

    public String getSeqNum() {
        return seqNum;
    }

    public String isReplacementFlag() {
        return replacementFlag;
    }

    public boolean isOEM(){
        return QuoteConstants.LOB_OEM.equalsIgnoreCase(getLob());
    }

	public boolean isFCT(){
	    return QuoteConstants.LOB_FCT.equalsIgnoreCase(getLob());
	}



	public String getMigrationCode() {
		return StringFilter.urlEncode(migrationCode);
	}

	public String getQuoteFlag() {
		return StringFilter.filter(quoteFlag);
	}

	public boolean isFCTToPAQuote(){
		return ((QuoteConstants.LOB_PA.equalsIgnoreCase(getLob())
                || QuoteConstants.LOB_PAE.equalsIgnoreCase(getLob())
                || QuoteConstants.LOB_PAUN.equalsIgnoreCase(getLob()))
                && QuoteConstants.MIGRTN_CODE_FCT_TO_PA.equalsIgnoreCase(this.getMigrationCode()));
	}
	
	public Boolean  isAddNewMonthlySW(){
		if(addNewMonthlySWFlag !=null && addNewMonthlySWFlag.equals("true")){
			return true;
		}
	  return false;	

	}

	public String getAddNewMonthlySWFlag() {
		return addNewMonthlySWFlag;
	}

	public void setAddNewMonthlySWFlag(String addNewMonthlySWFlag) {
		this.addNewMonthlySWFlag = addNewMonthlySWFlag;
	}
	
	public String getPUVLink(){
		  ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
		  String codeBase = appCtx.getConfigParameter(EAD4JBootstrapKeys.EAD4J_CODEBASE_PATH_KEY);
		  String configXMLFile = codeBase + "/" + appCtx.getConfigParameter("portal.settings.folder") + "/" + appCtx.getConfigParameter("taglib.generate.important.links");
		  String puvLink ="";
		  SAXBuilder saxBuilder =new SAXBuilder(false);
    	  try {
			Document doc =saxBuilder.build(new File(configXMLFile));
			Element root =doc.getRootElement();
			List<Element> importantLinks =root.getChildren("importantLinks");
			
			for(Element link :importantLinks){								
				if(link.getAttributeValue("id").equals("pvu")){					
					puvLink =link.getChild("importantLink").getChildText("href");
					break;
				}
			}			
		 } catch (Exception e) {
			LogContextFactory.singleton().getLogContext().error(this, "tag-important-links-config.xml file parse error - " + configXMLFile);
		 } 
		
		 return puvLink;
     }
	
}