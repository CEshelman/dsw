package com.ibm.dsw.quote.export.contract;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

public class ExportContract extends QuoteBaseContract {

	private String webQuoteNum = null;
	private boolean submittedQuote = false;
	private TimeZone timezone = null;
	private boolean isXMLSpreadsheetDownload = false;
	private String downloadPricingType = null;
	private boolean RTFDownload = false;
	
	public boolean isRTFDownload() {
		return RTFDownload;
	}
	public void setRTFDownload(boolean rTFDownload) {
		RTFDownload = rTFDownload;
	}
	public String getDownloadPricingType() {
		return downloadPricingType;
	}
	public void setDownloadPricingType(String downloadPricingType) {
		this.downloadPricingType = downloadPricingType;
	}
	public boolean isXMLSpreadsheetDownload() {
		return isXMLSpreadsheetDownload;
	}
	public void setXMLSpreadsheetDownload(boolean isXMLSpreadsheetDownload) {
		this.isXMLSpreadsheetDownload = isXMLSpreadsheetDownload;
	}
	/**
	 * @return Returns the webQuoteNum.
	 */
	public String getWebQuoteNum() {
		return webQuoteNum;
	}
	/**
	 * @param webQuoteNum The webQuoteNum to set.
	 */
	public void setWebQuoteNum(String webQuoteNum) {
		this.webQuoteNum = webQuoteNum;
	}
	
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        timezone = getTimeZone(session);
        this.submittedQuote = parameters.getParameterAsBoolean(ParamKeys.PARAM_IS_SBMT_QT);
        this.downloadPricingType = parameters.getParameterAsString(ParamKeys.PARAM_PRICING_TYPE);
    }
    
    
	public boolean isSubmittedQuote() {
		return submittedQuote;
	}
	
	public TimeZone getTimeZone(){
		return timezone;
	}
	
	private TimeZone getTimeZone(JadeSession session) {
		String timeZoneID = ParamKeys.PARAM_GMT_TIMEZONE;
		String offsetStr = (String) session.getAttribute(ParamKeys.PARAM_TIMEZONEOFFSET);
		
		if(StringUtils.isNotBlank(offsetStr)) {
			int hours = 0;
			int mins = 0;
			int offset = Integer.parseInt(offsetStr);
			String symbol = offset < 0 ? "-" : "+";
			hours = Math.abs(offset) / 60;
			mins = Math.abs(offset) % 60;
			String hoursInStr = (hours < 10) ? symbol + "0" + hours : symbol + hours;
			String minsInStr = mins < 10 ? "0" + mins : mins + "";
			timeZoneID = timeZoneID + hoursInStr +":" + minsInStr;
		}
		
		return TimeZone.getTimeZone(timeZoneID);
	}
}
