package com.ibm.dsw.quote.submittedquote.contract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.ibm.dsw.quote.base.util.DateHelper;
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
public class QtDateContract extends SubmittedQuoteBaseContract {
    
    private String expirationDay;

    private String expirationMonth;

    private String expirationYear;
    
    private Date expDate;
    
    private String startDay;

    private String startMonth;

    private String startYear;
    
    private Date startDate;
    
    private String custReqstdArrivlDay;
    
    private String custReqstdArrivlMonth;
    
    private String custReqstdArrivlYear;
    
    private Date cradDate;
    
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
        setExpDate(parseDate(getExpirationYear(), getExpirationMonth(), getExpirationDay()));
        this.setCradDate(parseDate(this.getCustReqstdArrivlYear(), this.getCustReqstdArrivlMonth(), this.getCustReqstdArrivlDay()));
    }
    
    /**
     * @return Returns the expirationDay.
     */
    public String getExpirationDay() {
        return expirationDay;
    }
    /**
     * @param expirationDay The expirationDay to set.
     */
    public void setExpirationDay(String expirationDay) {
        this.expirationDay = expirationDay;
    }
    /**
     * @return Returns the expirationMonth.
     */
    public String getExpirationMonth() {
        return expirationMonth;
    }
    /**
     * @param expirationMonth The expirationMonth to set.
     */
    public void setExpirationMonth(String expirationMonth) {
        this.expirationMonth = expirationMonth;
    }
    /**
     * @return Returns the expirationYear.
     */
    public String getExpirationYear() {
        return expirationYear;
    }
    /**
     * @param expirationYear The expirationYear to set.
     */
    public void setExpirationYear(String expirationYear) {
        this.expirationYear = expirationYear;
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
        return isDateValid(getExpirationYear(), getExpirationMonth(), getExpirationDay(), false);
    }
    
    public boolean isCRADDateValid() {
        return isDateValid(this.getCustReqstdArrivlYear(), this.getCustReqstdArrivlMonth(), this.getCustReqstdArrivlDay(), true);
    }
    
    
    protected boolean isDateValid(String year, String month, String day, boolean isQuoteStartDate) {
        
        if (StringUtils.isBlank(year) && StringUtils.isBlank(month) && StringUtils.isBlank(day))
            return true;
        
        if (isQuoteStartDate) {

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

    public boolean isStartDateAfterExpDate() {

        Date startDate = parseDate(getStartYear(), getStartMonth(), getStartDay());
        Date expDate = parseDate(getExpirationYear(), getExpirationMonth(), getExpirationDay());

        if ( startDate != null && expDate !=null) {
            return startDate.after(expDate);
        } else {
            return false;
        }
    }
    
    
    
}
