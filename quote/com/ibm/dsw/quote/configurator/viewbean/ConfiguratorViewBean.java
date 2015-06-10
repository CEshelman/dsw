/**
 * 
 */
package com.ibm.dsw.quote.configurator.viewbean;

import java.util.ArrayList;
import java.util.Collection;

import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.util.StringEncoder;
import com.ibm.dsw.quote.configurator.config.ConfiguratorParamKeys;
import com.ibm.dsw.quote.configurator.domain.ConfiguratorHeader;
import com.ibm.dsw.quote.draftquote.viewbean.DisplayQuoteBaseViewBean;
import com.ibm.ead4j.jade.taglib.html.SelectOptionImpl;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * @ClassName: ConfiguratorViewBean
 * @author Frank
 * @Description: TODO
 * @date Dec 26, 2013 4:43:12 PM
 *
 */
public class ConfiguratorViewBean extends DisplayQuoteBaseViewBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ConfiguratorHeader header;
	protected static final LogContext logger = LogContextFactory.singleton().getLogContext();
	
	public String getCustomerNumber(){
		return StringEncoder.textToHTML(header.getCustomerNumber());
	}
	
	public String getSapContractNum(){
		return StringEncoder.textToHTML(header.getSapContractNum());
	}
	
	public String getQuoteTypeCode(){
		return StringEncoder.textToHTML(header.getQuoteTypeCode());
	}
	
	public String getProgMigrationCode(){
		return StringEncoder.textToHTML(header.getProgMigrationCode());
	}
	
	public String getAcqrtnCode(){
		return StringEncoder.textToHTML(header.getAcqrtnCode());
	}
	
	public String getAudience(){
		return StringEncoder.textToHTML(header.getAudience());
	}
	
    public String getCntryCode(){
    	return StringEncoder.textToHTML(header.getCntryCode());
    }
    
	public String getCntryCodeDscr() {
		return StringEncoder.textToHTML(header.getCntryCodeDscr());
	}
	public String getCurrencyCode() {
		return StringEncoder.textToHTML(header.getCurrencyCode());
	}
	public String getCurrencyCodeDscr() {
		return StringEncoder.textToHTML(header.getCurrencyCodeDscr());
	}
	public String getPid() {
		return StringEncoder.textToHTML(header.getPid());
	}
	public String getPidDscr() {
		logger.debug(this, "header.getPidDscr():"+header.getPidDscr());
		return StringEncoder.textToHTML(header.getPidDscr());
	}
	
	public String getBandLevel() {
		return StringEncoder.textToHTML(header.getBandLevel());
	}
	public String getLob() {
		return StringEncoder.textToHTML(header.getLob());
	}
	
	public String getConfigrtnActionCode(){
		return StringEncoder.textToHTML(header.getConfigrtnActionCode());
	}
	
    public String getWebQuoteNum() {
        return StringEncoder.textToHTML(header.getWebQuoteNum());
    }
    
    public String getIdByName(String name){
		return ConfiguratorParamKeys.paramIDPreFix + name;
	}
	
	public Collection getTermList() {
		Collection options = new ArrayList();
		int defaultTerm = 12;
		if(header.getTerm() != null){
			defaultTerm = header.getTerm().intValue();
		}
		int totalMonths = 60;
		
	   for (int i = 1; i <= totalMonths; i++) {
		   if(i == defaultTerm){
			   options.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), true));
		   } else {
			   options.add(new SelectOptionImpl(String.valueOf(i), String.valueOf(i), false));
		   }
        }
	   
	   return options;
	}
	
	public String getDefaultTerm() {
		if (header.getTerm() != null) {
			int term = header.getTerm().intValue();
			return term + "";
		}
		return "";
	}

	   /**
     * Getter for header.
     * 
     * @return the header
     */
    public ConfiguratorHeader getHeader() {
        return this.header;
    }

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.draftquote.viewbean.DisplayQuoteBaseViewBean#getDisplayTabAction()
	 */
	@Override
	public String getDisplayTabAction() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dsw.quote.draftquote.viewbean.DisplayQuoteBaseViewBean#getPostTabAction()
	 */
	@Override
	public String getPostTabAction() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getI18NString(String key, String[] args){
    	return getI18NString(I18NBundleNames.CONFIGURATOR_MESSAGES, key, args);
    }

}
