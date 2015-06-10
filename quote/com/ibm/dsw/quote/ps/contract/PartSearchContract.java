/*
 * Created on 2007-3-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.ps.contract;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.ibm.dsw.quote.appcache.domain.BrandsFactory;
import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
import com.ibm.dsw.quote.base.util.LocaleHelperImpl;
import com.ibm.dsw.quote.common.util.QuoteCookie;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PartSearchContract extends QuoteBaseCookieContract {
	protected static final LogContext logContext = LogContextFactory.singleton().getLogContext();
	
    private String quoteNum;
    private String partDescription;
    private String partBrand;
    //private String redirectSearchString;
    private String state;
    private String retrievalType;
    private String renewal;
    private String submitted;
    private transient List selectedParts = new ArrayList();
    //private StringBuffer sbSelectedParts = null;
    private String searchType;
    private String treeName;
    private String country;
    private String currency;
    private String lob;
    private String acqrtnCode;
    private String audience;
    private String partNumbers;
    private String partNumber;
    private boolean addSuccess;
    private Locale locale;
    private String seqNum;
    private String replacementFlag;

    private String exccedCode;

    private String progMigrationCode;
    private String quoteFlag;
    
    private String customerNumber;
    private String sapContractNum;
    private String isAddNewMonthlySWFlag;
    private String configrtnId;
    private String configrtnActionCode;
    private String chrgAgrmtNum;
    private String orgConfigId;
   
	/**
     * @param exceedCode The exceedCode to set.
     */
    public void setExceedCode(String exceedCode) {
        this.exccedCode = exceedCode;
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

    /**
     * @return Returns the audience.
     */
    public String getAudience() {
        return audience;
    }
    /**
     * @param audience The audience to set.
     */
    public void setAudience(String audience) {
        this.audience = audience;
    }
    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return country;
    }
    /**
     * @param country The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }
    /**
     * @return Returns the currency.
     */
    public String getCurrency() {
        return currency;
    }
    /**
     * @param currency The currency to set.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    /**
     * @return Returns the dataRetrievalType.
     */
    public String getRetrievalType() {
        return retrievalType;
    }
    /**
     * @param dataRetrievalType The dataRetrievalType to set.
     */
    public void setRetrievalType(String dataRetrievalType) {
        this.retrievalType = dataRetrievalType;
    }
    /**
     * @return if the action is invoked from renewal quotes
     */
    public String getRenewal() {
        return renewal;
    }
    /**
     * @param renewal
     *            True if the action is invoked from renewal quotes
     */
    public void setRenewal(String renewal) {
        this.renewal = renewal;
    }
    /**
     * @return Returns the lob.
     */
    public String getLob() {
        return lob;
    }
    /**
     * @param lob The lob to set.
     */
    public void setLob(String lob) {
        this.lob = lob;
    }
    /**
     * @return Returns the treeName.
     */
    public String getTreeName() {
        return treeName;
    }
    /**
     * @param treeName The treeName to set.
     */
    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }
    public String getPartBrand() {
        return partBrand;
    }
    public void setPartBrand(String brand) {
        this.partBrand = brand;
    }
    /**
     * @return Returns the description.
     */
    public String getPartDescription() {
        return partDescription;
    }
    /**
     * @param description The description to set.
     */
    public void setPartDescription(String description) {
        this.partDescription = description;
    }
    /**
     * @return Returns the quoteNum.
     */
    public String getQuoteNum() {
        return quoteNum;
    }
    /**
     * @param quoteNum The quoteNum to set.
     */
    public void setQuoteNum(String quoteNumber) {
        this.quoteNum = quoteNumber;
    }

    /**
     * @return Returns the partNumbers.
     */
    public String getPartNumbers() {
        return partNumbers;
    }
    /**
     * @param partNumbers The partNumbers to set.
     */
    public void setPartNumbers(String partNumbers) {
        this.partNumbers = partNumbers;
    }


    /**
     * @return Returns the partNumber.
     */
    public String getPartNumber() {
        return partNumber;
    }
    /**
     * @param partNumber The partNumber to set.
     */
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public boolean isAddSuccess() {
        return addSuccess;
    }

    public boolean isRenewal() {
        return renewal != null && "true".equalsIgnoreCase(renewal);
    }

    public boolean isSubmitted() {
        return submitted != null && "true".equalsIgnoreCase(submitted);
    }

    public Locale getLocale() {
        return locale;
    }
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        addSuccess = parameters.getParameterAsBoolean("addSuccess");
        this.exccedCode = parameters.getParameterAsString("exccedCode");
        this.isAddNewMonthlySWFlag = parameters.getParameterAsString("isAddNewMonthlySWFlag") == null ? "false" : parameters.getParameterAsString("isAddNewMonthlySWFlag"); 
        this.chrgAgrmtNum=parameters.getParameterAsString("chrgAgrmtNum") == null ? "" : parameters.getParameterAsString("chrgAgrmtNum");
        this.configrtnActionCode=parameters.getParameterAsString("configrtnActionCode") == null ? "" : parameters.getParameterAsString("configrtnActionCode");
        this.configrtnId=parameters.getParameterAsString("configrtnId") == null ? "" : parameters.getParameterAsString("configrtnId");
        this.orgConfigId=parameters.getParameterAsString("orgConfigId") == null ? "" : parameters.getParameterAsString("orgConfigId");
      if(getPartBrand() == null || getPartBrand().trim().equals("") || !isPartBrandValid(getPartBrand())){
            // Setting the default value as part brand key instead of the value from QuoteCookie.browsePartBrandDefault
        	String defaultbrand = QuoteCookie.browsePartBrandKey;
            if (sqoCookie != null ) {
                String strCookieVal = sqoCookie.getValue();
                if (strCookieVal.indexOf(QuoteCookie.browsePartBrandKey) >= 0 ){
                	defaultbrand = QuoteCookie.getBrowsePartBrand(sqoCookie);
                }
            }
            setPartBrand(defaultbrand);
        }
        locale = (Locale) session.getAttribute(FrameworkKeys.JADE_LOCALE_KEY);
        if (locale == null) {
            locale = LocaleHelperImpl.getDefaultDSWLocale();
        }
    }
    
    public boolean isPartBrandValid(String brand){
    	try {
    		List brandsList = BrandsFactory.singleton().getBrandsList();
			Iterator it = brandsList.iterator();
			while(it.hasNext()){
				CodeDescObj brandObj = (CodeDescObj)it.next();
				if(brand.equals(brandObj.getCode())){
					return true;
				}
			}
		} catch (TopazException e) {
			logContext.error(this, e.getMessage());
		}
    	return false;
    }
    
    /**
     * @return Returns the acqrtnCode.
     */
    public String getAcqrtnCode() {
        return acqrtnCode;
    }
    /**
     * @param acqrtnCode The acqrtnCode to set.
     */
    public void setAcqrtnCode(String acqrtnCode) {
        this.acqrtnCode = acqrtnCode;
    }
    /**
     * @return Returns the submitted.
     */
    public String getSubmitted() {
        return submitted;
    }
    /**
     * @param submitted The submitted to set.
     */
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
    /**
     * @return Returns the seqNum.
     */
    public String getSeqNum() {
        return seqNum;
    }
    /**
     * @param seqNum The seqNum to set.
     */
    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }
    /**
     * @return Returns the replacementFlag.
     */
    public boolean isReplacementFlag() {
        return replacementFlag != null && "true".equalsIgnoreCase(replacementFlag);
    }
    /**
     * @param replacementFlag The replacementFlag to set.
     */
    public void setReplacementFlag(String replacementFlag) {
        this.replacementFlag = replacementFlag;
    }
    /**
     * @return Returns the exceedCode.
     */
    public String getExceedCode() {
        return exccedCode;
    }
	public String getProgMigrationCode() {
		return progMigrationCode;
	}
	public void setProgMigrationCode(String progMigrationCode) {
		this.progMigrationCode = progMigrationCode;
	}
	public String getQuoteFlag() {
		return quoteFlag;
	}
	public void setQuoteFlag(String quoteFlag) {
		this.quoteFlag = quoteFlag;
	}
	public String getCustomerNumber() {
		return customerNumber;
	}
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
	public String getSapContractNum() {
		return sapContractNum;
	}
	public void setSapContractNum(String sapContractNum) {
		this.sapContractNum = sapContractNum;
	}
	public String getIsAddNewMonthlySWFlag() {
		return isAddNewMonthlySWFlag;
	}
	public void setIsAddNewMonthlySWFlag(String isAddNewMonthlySWFlag) {
		this.isAddNewMonthlySWFlag = isAddNewMonthlySWFlag;
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
	public void setOrgConfigId(String orgConfigId) {
		this.orgConfigId = orgConfigId;
	}

	
}
