package com.ibm.dsw.quote.draftquote.viewbean.helper;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.appcache.domain.ReasonCodeFactory;
import com.ibm.dsw.quote.base.config.I18NBundleNames;
import com.ibm.dsw.quote.base.config.QuoteConstants;
import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.base.util.StringEncoder;
import com.ibm.dsw.quote.common.domain.PartPriceConfigFactory;
import com.ibm.dsw.quote.common.domain.Quote;
import com.ibm.dsw.quote.common.domain.QuoteLineItem;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteViewKeys;
import com.ibm.dsw.quote.draftquote.config.PartPriceViewKeys;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.jade.config.ApplicationContext;
import com.ibm.ead4j.jade.config.ApplicationContextFactory;
import com.ibm.ead4j.jade.config.FrameworkKeys;
import com.ibm.ead4j.jade.util.Parameters;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;
import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * @author Xiao Guo Yi
 */
public class ItemCoverageDates extends PartPriceCommon{
	
    private static LogContext log = LogContextFactory.singleton().getLogContext();
	
    //configured value (additional-year-config) in portal-config/part-price-config.xml
    private int totalYears;
	
    //configured value (back-dating-past-year-limit) in portal-config/part-price-config.xml    
    private int backDatingPastYearLimit;
    
	//used in submitted quote screen
	private boolean showReasonCodes = false;
	//used in submitted quote screen
	private boolean showBackDatingComments = false;
	
    private int currentYear = DateUtil.getCurrentDate().getYear() + 1900;
    
    static String DISPLAY_NO = "none";
    static String DISPLAY_YES = "";
	
	public ItemCoverageDates(Quote quote){
		super(quote);
		init();
	}
	
	private void init(){
        //for back dating
		String lob = quote.getQuoteHeader().getLob().getCode();
		totalYears = PartPriceConfigFactory.singleton().getAddtionalYear(lob);
        backDatingPastYearLimit = PartPriceConfigFactory.singleton().getBackDatingPastYearLimit(lob);

        	log.debug(this, "back date init completes{totalYears = " + totalYears
        			          + ", currentYear = " + currentYear);
	 }
	

	
	public String getDisplayStyle(HttpServletRequest request){
		String show = DISPLAY_NO;
		
		if(!isPPSS()){
			List masterLineItems = quote.getSoftwareLineItems();
			
			if(masterLineItems == null || masterLineItems.size() == 0){
				return show;
			}
			
			//check if this is a validation fail
			String tmpShow = getHiddenValue(request);
			if(tmpShow != null){
				if(!DISPLAY_NO.equals(tmpShow) && !DISPLAY_YES.equals(tmpShow)){
					tmpShow = DISPLAY_NO;
				}
				log.debug(this, "getDisplayStyle from hidden input filed[" + tmpShow + "]");
				return StringEncoder.textToHTML(tmpShow);
			}
			
			QuoteLineItem lineItem = null;
			Date currentDate = new Date();
			for(Iterator it = masterLineItems.iterator(); it.hasNext(); ){
				lineItem = (QuoteLineItem)it.next();
				
				//Don't trigger back dating for cmprss cvrage parts
				if((!lineItem.hasValidCmprssCvrageMonth())
				        && StringUtils.isBlank(lineItem.getRenewalQuoteNum())  
				        && lineItem.isItemBackDated()){
					show = DISPLAY_YES;
					break;
				}
			}
		}
		
		log.debug(this, "getDisplayStyle by checking if any line item is backdated[" + show + "]");
		return show;
	}
	
	private String getHiddenValue(HttpServletRequest request){
        Parameters parms = (Parameters) request.getAttribute(FrameworkKeys.JADE_UNDO_PARAMETER_KEY);
        if (parms != null) {
            return parms.getParameterAsString(DraftQuoteParamKeys.BACK_DATE_DISPLAY_STYLE);
        }
        
        return null;
	}
	
	public String getAllBackDateCheckItemIDs(){
		List masterLineItems = quote.getMasterSoftwareLineItems();
		if(masterLineItems == null || masterLineItems.size() == 0){
			log.debug(this, "getAllBackDateCheckItemIDs(), no master line items, return empty");
			return "";
		}
		
		StringBuffer sb = new StringBuffer();

		QuoteLineItem lineItem = null;
		for (Iterator it = masterLineItems.iterator(); it.hasNext();) {
			lineItem = (QuoteLineItem) it.next();
			sb.append(PartPriceViewKeys.PREFIX); 
			sb.append(lineItem.getPartNum());
			sb.append("_");
			sb.append(lineItem.getSeqNum());
			sb.append(DraftQuoteParamKeys.BACK_DATE_SUFFIX);
			sb.append(",");
		}
		
		if(sb.length() > 0){
			sb.deleteCharAt(sb.length() - 1);
		}
		
		log.debug(this, "getAllBackDateCheckItemIDs() return " + sb.toString());

		return sb.toString();
	}
	
	public List getAllReasonCodes() throws QuoteException {
		try{
			return ReasonCodeFactory.singleton().findAllReasonCodes();
		}
		catch(TopazException e){
			log.error(this, e);
			
			throw new QuoteException(e);
		}
	}
	
	//whether to display the line item back dating reason and comment
	public boolean showSubmittedReasAndCmmt(){
		List list = quote.getQuoteHeader().getReasonCodes();
		showReasonCodes =  (list != null && list.size() > 0);
		
		showBackDatingComments = StringUtils.isNotBlank(quote.getQuoteHeader().getBackDatingComment());
		
		return (showReasonCodes || showBackDatingComments);
	}
	
	public boolean showSubmittedReasonCodes(){
		
		return showReasonCodes;
	}
	
	public boolean showSubmittedCmmts(){
		
		return showBackDatingComments;
	}
	
	public List getSelectedReasonCodes(){
		return quote.getQuoteHeader().getReasonCodes();
	}
	
	public String getBackDatingComment(){
		return quote.getQuoteHeader().getBackDatingComment();
	}
	
	public int getTotalMaintYears(){
		return totalYears;
	}
	
	public boolean isBackDatingAllowed(String revnStrmCode){
		return PartPriceConfigFactory.singleton().isBackDatingAllowed(quote.getQuoteHeader(), revnStrmCode);
	}
	
	public boolean isFutureStartDateAllowed(String revnStrmCode){
		return PartPriceConfigFactory.singleton().isFutureStartDateAllowed(revnStrmCode);
	}
	
	public int getMaintStartYear(String revnStrmCode){
		if(isBackDatingAllowed(revnStrmCode)){
			return (currentYear - backDatingPastYearLimit);
		} else {
			return currentYear;
		}
	}
	
	public int getBackDatingPastYearLimit(){
		return backDatingPastYearLimit;
	}
	
	public int getMaintEndYear(String revnStrmCode){
		return getMaintStartYear(revnStrmCode) + totalYears - 1;
	}
	
	public String getLabelForReasonCode(Locale locale, String reasonCode, String reasonCodeDesc){
		ApplicationContext appCtx = ApplicationContextFactory.singleton().getApplicationContext();
		
		if(QuoteConstants.BACK_DATING_REASON_OTHER.equals(reasonCode)){
			return appCtx.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale, DraftQuoteViewKeys.BACK_DTG_REAS_CODE_OTHER_LBL);
			
		} else {
			String lbl = appCtx.getI18nValueAsString(I18NBundleNames.PART_PRICE_MESSAGES, locale, DraftQuoteViewKeys.BACK_DTG_REAS_CODE_LBL);
			
			return MessageFormat.format(lbl, new Object[]{reasonCodeDesc});
		}
	}
	
	public boolean reasonCodeIsOther(String reasonCode){
	    return QuoteConstants.BACK_DATING_REASON_OTHER.equals(reasonCode);
	}
}
