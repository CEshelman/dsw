package com.ibm.dsw.quote.submittedquote.contract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.draftquote.util.date.DateUtil;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QtDateContract<code> class.
 *    
 * @author: lthalla@us.ibm.com
 * 
 * Creation date: May 14, 2007
 */
public class ExtendQuoteExpDateContract extends SubmittedQuoteBaseContract {
	
	private String quoteNum;
    
	private String expDateExtensionDay;

    private String expDateExtensionMonth;

    private String expDateExtensionYear;
    
    private String expDateExtensionJustification;
    
	private Date expDate;
    
    private String startDay;

    private String startMonth;

    private String startYear;
    
    private Date startDate;
    
    private String custReqstdArrivlDay;
    
    private String custReqstdArrivlMonth;
    
    private String custReqstdArrivlYear;
    
    private Date cradDate;
    
    private String updateSavedQuoteFlag;
    
	public String getUpdateSavedQuoteFlag() {
		return updateSavedQuoteFlag;
	}

	public void setUpdateSavedQuoteFlag(String updateSavedQuoteFlag) {
		this.updateSavedQuoteFlag = updateSavedQuoteFlag;
	}

	private String displayTabUrl;
    public String getDisplayTabUrl() {
		return displayTabUrl;
	}

	public void setDisplayTabUrl(String displayTabUrl) {
		this.displayTabUrl = displayTabUrl;
	}

	public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        setStartDate(parseDate(getStartYear(), getStartMonth(), getStartDay()));
        setExpDate(parseDate(getExpDateExtensionYear(), getExpDateExtensionMonth(), getExpDateExtensionDay()));
        this.setCradDate(parseDate(this.getCustReqstdArrivlYear(), this.getCustReqstdArrivlMonth(), this.getCustReqstdArrivlDay()));
    }
    
    /**
     * @return Returns the expDateExtensionDay.
     */
    public String getExpDateExtensionDay() {
        return expDateExtensionDay;
    }
    /**
     * @param expDateExtensionDay The expDateExtensionDay to set.
     */
    public void setExpDateExtensionDay(String expDateExtensionDay) {
        this.expDateExtensionDay = expDateExtensionDay;
    }
    /**
     * @return Returns the expDateExtensionMonth.
     */
    public String getExpDateExtensionMonth() {
        return expDateExtensionMonth;
    }
    /**
     * @param expDateExtensionMonth The expDateExtensionMonth to set.
     */
    public void setExpDateExtensionMonth(String expDateExtensionMonth) {
        this.expDateExtensionMonth = expDateExtensionMonth;
    }
    /**
     * @return Returns the expDateExtensionYear.
     */
    public String getExpDateExtensionYear() {
        return expDateExtensionYear;
    }
    /**
     * @param expDateExtensionYear The expDateExtensionYear to set.
     */
    public void setExpDateExtensionYear(String expDateExtensionYear) {
        this.expDateExtensionYear = expDateExtensionYear;
    }
    /**
     * @return Returns the expDate.
     */
    public Date getExpDate() {
        return expDate;
    }
    /**
     * @param expDate The expDate to set.
     */
    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }
    
    /**
     * @return Returns the startDay.
     */
    public String getStartDay() {
        return startDay;
    }
    /**
     * @param startDay The startDay to set.
     */
    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }
    /**
     * @return Returns the startMonth.
     */
    public String getStartMonth() {
        return startMonth;
    }
    /**
     * @param startMonth The startMonth to set.
     */
    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }
    /**
     * @return Returns the startYear.
     */
    public String getStartYear() {
        return startYear;
    }
    /**
     * @param startYear The startYear to set.
     */
    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }
    /**
     * @return Returns the startDate.
     */
    public Date getStartDate() {
        return startDate;
    }
    /**
     * @param startDate The startDate to set.
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public String getCustReqstdArrivlDay() {
		return custReqstdArrivlDay;
	}

	public void setCustReqstdArrivlDay(String custReqstdArrivlDay) {
		this.custReqstdArrivlDay = custReqstdArrivlDay;
	}

	public String getCustReqstdArrivlMonth() {
		return custReqstdArrivlMonth;
	}

	public void setCustReqstdArrivlMonth(String custReqstdArrivlMonth) {
		this.custReqstdArrivlMonth = custReqstdArrivlMonth;
	}

	public String getCustReqstdArrivlYear() {
		return custReqstdArrivlYear;
	}

	public void setCustReqstdArrivlYear(String custReqstdArrivlYear) {
		this.custReqstdArrivlYear = custReqstdArrivlYear;
	}

	public Date getCradDate() {
		return cradDate;
	}

	public void setCradDate(Date cradDate) {
		this.cradDate = cradDate;
	}

	public boolean isQtStartDateValid() {
        return isDateValid(getStartYear(), getStartMonth(), getStartDay(), true);
    }
    
    public boolean isQtExpDateValid() {
        return isDateValid(getExpDateExtensionYear(), getExpDateExtensionMonth(), getExpDateExtensionDay(), true);
    }
    
    public boolean isExpDateFilled(){
    	if(this.getExpDate()==null){
    		return false;
    	}
    	return true;
    }
    public boolean isExpDateExtensionJustificationFilled(){
    	if(StringUtils.isBlank(this.getExpDateExtensionJustification())){
    		return false;
    	}
    	return true;
    }
    
    private Date getMaxDate(Date first,Date second){
    	if(first==null||second==null)
    		return null;
    	
    	Calendar firstCal =  Calendar.getInstance();
    	firstCal.setTime(first);
    	Calendar secondCal =  Calendar.getInstance();
    	secondCal.setTime(second);
    	
    	if(first.before(second)){
    		return second;
    	}else{
    		return first;
    	}
    }
    
    protected boolean isDateValid(String year, String month, String day, boolean cannotBeforeCurrentDate) {
        
        if (StringUtils.isBlank(year) && StringUtils.isBlank(month) && StringUtils.isBlank(day))
            return true;
        
        if (cannotBeforeCurrentDate) {

	        if (!DateHelper.validateDate(year, month, day))
	            return false;
	        
	        Date date = parseDate(year, month, day);
	        Calendar curr = Calendar.getInstance();
	        Date now = curr.getTime();
	        Date currDate = DateUtils.truncate(now, Calendar.DATE);
	        
	        return (date != null && !currDate.after(date));
        } 
        else {
	        return DateHelper.validateDate(year, month, day);
	    }

    }
    
    protected Date parseDate(String year, String month, String day) {
        
        if (StringUtils.isBlank(year) && StringUtils.isBlank(month) && StringUtils.isBlank(day))
            return null;
        
        if (!DateHelper.validateDate(year, month, day))
            return null;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        
        try {
            date = sdf.parse(year + month + day);
        } catch (ParseException e) {
            date = null;
        }
        
        return date;
    }

    public boolean isExtensionExpDateBeforeOriginalExpDate(Date originalExpDate) {

//        Date startDate = parseDate(getStartYear(), getStartMonth(), getStartDay());
        Date extensionExpDate = parseDate(getExpDateExtensionYear(), getExpDateExtensionMonth(), getExpDateExtensionDay());

        if ( originalExpDate != null && extensionExpDate !=null) {
            return extensionExpDate.before(originalExpDate);
        } else {
            return false;
        }
    }
    
    public boolean isExtensionExpDateAfterLastDayOfQuarterOfOriginalExpDate(Date originalExpDate) {

     Date extensionExpDate = parseDate(getExpDateExtensionYear(), getExpDateExtensionMonth(), getExpDateExtensionDay());

      if ( originalExpDate != null && extensionExpDate !=null) {
    	  
    	  Date lastDayOfQuarter=DateUtil.getLastDayOfQuarter(originalExpDate);
          return extensionExpDate.after(lastDayOfQuarter);
      } else {
          return false;
      }
  }
    
   	public String getExpDateExtensionJustification() {
   		return expDateExtensionJustification;
   	}

   	public void setExpDateExtensionJustification(
   			String expDateExtensionJustification) {
   		this.expDateExtensionJustification = expDateExtensionJustification;
   	}
   	
   	public String getQuoteNum() {
		return quoteNum;
	}

	public void setQuoteNum(String quoteNum) {
		this.quoteNum = quoteNum;
	}
	private String redirectURL;
	  /**
     * @return Returns the redirectURL.
     */
    public String getRedirectURL() {
        return redirectURL;
    }

    /**
     * @param redirectURL
     *            The redirectURL to set.
     */
    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }
}
