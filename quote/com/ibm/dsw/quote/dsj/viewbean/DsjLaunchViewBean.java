package com.ibm.dsw.quote.dsj.viewbean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.CodeDescObj;
import com.ibm.dsw.quote.appcache.domain.Country;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.MessageKeys;
import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.common.domain.QuoteRightColumn;
import com.ibm.dsw.quote.newquote.config.NewQuoteParamKeys;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.taglib.html.SelectOption;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.jade.util.Parameters;

public class DsjLaunchViewBean extends DsjBaseViewBean {
    
    private transient List lobList = null;

    private transient List countryList = null;
    
    private transient List acqstnList = null;
    
    private transient String currentWebQuoteNumber = null;

    public void collectResults(Parameters param) throws ViewBeanException {
        super.collectResults(param);

        List lobs = (List) param.getParameter(NewQuoteParamKeys.PARAM_LOB_LIST);
        List acqstns = (List) param.getParameter(NewQuoteParamKeys.PARAM_ACQUISITION_LIST);
        List cntries = (List) param.getParameter(NewQuoteParamKeys.PARAM_COUNTRY_LIST);

        if (lobs == null) {
            throw new ViewBeanException("fail to get LOBS in viewbean");
        }
        if (cntries == null) {
            throw new ViewBeanException("fail to get Countries in viewbean");
        }
        
        String emptyString= "";
        String defaultLOB = (String) param.getParameter(NewQuoteParamKeys.PARAM_DEFAULT_LOB);
        String defaultCountry = (String) param.getParameter(NewQuoteParamKeys.PARAM_DEFAULT_COUNTRY);
        String defaultAcquisition = (String) param.getParameter(NewQuoteParamKeys.PARAM_DEFAULT_ACQSTN);
        
        if (StringUtils.isBlank(defaultLOB)) {
            //cookie does not have default lob. use the first in the list
            defaultLOB = emptyString;
        }
        if (StringUtils.isBlank(defaultCountry)) {
            //cookie does not have default country. use the first in the list
            defaultCountry = emptyString;
        }
        if (StringUtils.isBlank(defaultAcquisition)) {
            defaultAcquisition = emptyString;
        }
        String selectQuoteType = getI18NString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.SELECT_QUOTE_TYPE);
        lobList = new ArrayList();
        lobList.add(new SelectOptionImpl(selectQuoteType, emptyString, defaultLOB.equalsIgnoreCase(emptyString)));
        
        for (int i = 0; i < lobs.size(); i++) {
            CodeDescObj lob = (CodeDescObj) lobs.get(i);
            String lobCode = lob.getCode();
            String lobDesc = lob.getCodeDesc();
            
            SelectOption lobSO = new SelectOptionImpl(lobDesc, lobCode, defaultLOB.equalsIgnoreCase(lobCode));
            lobList.add(lobSO);
        }

        String selectCountry = getI18NString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.SELECT_COUNTRY);
        countryList = new ArrayList();
        countryList.add(new SelectOptionImpl(selectCountry, emptyString, defaultCountry.equalsIgnoreCase(emptyString)));
        
        for (int i = 0; i < cntries.size(); i++) {
            Country country = (Country) cntries.get(i);
            SelectOption countrySO = new SelectOptionImpl(country.getDesc(), country.getCode3(), defaultCountry
                    .equalsIgnoreCase(country.getCode3()));
            countryList.add(countrySO);
        }
        
        String selectAcquisition = getI18NString(I18NBundleNames.BASE_MESSAGES, locale, MessageKeys.SEELCT_ACQUISITION);
        acqstnList = new ArrayList();
        acqstnList.add(new SelectOptionImpl(selectAcquisition, emptyString, defaultAcquisition.equalsIgnoreCase(emptyString)));
        
        for (int i = 0; i < acqstns.size(); i++) {
            CodeDescObj acqstn = (CodeDescObj) acqstns.get(i);
            String acqCode = acqstn.getCode();
            String acqDesc = acqstn.getCodeDesc();
            
            SelectOption acqSO = new SelectOptionImpl(acqDesc, acqCode, defaultAcquisition.equalsIgnoreCase(acqCode));
            acqstnList.add(acqSO);
        }

        QuoteRightColumn quoteRightColumn = (QuoteRightColumn)param.getParameter(ParamKeys.PARAM_QUOTE_RIGHTCOLUMN);
        if (quoteRightColumn != null){
        	this.setCurrentWebQuoteNumber(quoteRightColumn.getSWebQuoteNum());
        }

    }

    public List getCountryList() {
        return countryList;
    }

    /**
     * @return Returns the lobList.
     */
    public List getLobList() {
        return lobList;
    }
    
    public List getAcqstnList() {
        return acqstnList;
    }
    
    protected String getI18NString(String basename, Locale locale, String key) {
        ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
        return appCtx.getI18nValueAsString(basename, locale, key);
    }

	public String getCurrentWebQuoteNumber() {
		return currentWebQuoteNumber;
	}

	public void setCurrentWebQuoteNumber(String currentWebQuoteNumber) {
		this.currentWebQuoteNumber = currentWebQuoteNumber;
	}
    
    
}
