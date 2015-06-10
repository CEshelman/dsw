package com.ibm.dsw.quote.draftquote.contract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.base.contract.QuoteBaseCookieContract;
import com.ibm.dsw.quote.base.util.DateHelper;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteConstants;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>PostDraftQuoteBaseContract<code> class.
 *    
 * @author: zhaoxw@cn.ibm.com
 * 
 * Creation date: Mar 28, 2007
 */

public class PostDraftQuoteBaseContract extends QuoteBaseCookieContract {
    
    private String webQuoteNum;

    private String expirationDay;

    private String expirationMonth;

    private String expirationYear;

    private String redirectURL;

    private String buttonName;

    private String maxExpirationDays;

    private String quoteType;

    private String forwardFlag;
    
    private Date expireDate;
    
    private String quoteClassfctnCode;
    
    private String startDay;

    private String startMonth;

    private String startYear;
    
    private Date startDate;
    
    private String oemAgrmntType;
    
    private String pymntTermsRadio;
    
    private String pymntTermsInput;
    
    private int pymntTermsDays;
    
    private int oemBidType;
    
    private String estmtdOrdDay;

    private String estmtdOrdMonth;

    private String estmtdOrdYear;
    
    private Date estmtdOrdDate;
    
    private Date custReqstdArrivlDate;
    
    private String custReqstdArrivlDay;
    
    private String custReqstdArrivlMonth;
    
    private String custReqstdArrivlYear;
    
    private String sspType;
    
    private String installAtOption;
    
    private String shipToOption;
    
    /**
     * @param parameters
     * @param session
     * @see com.ibm.ead4j.jade.contract.ProcessContract#load(com.ibm.ead4j.jade.util.Parameters,
     *      com.ibm.ead4j.jade.session.JadeSession)
     */
    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);
        
        setStartDate(parseDate(getStartYear(), getStartMonth(), getStartDay()));
        setExpireDate(parseDate(getExpirationYear(), getExpirationMonth(), getExpirationDay()));
        setEstmtdOrdDate(parseDate(getEstmtdOrdYear(), getEstmtdOrdMonth(), getEstmtdOrdDay()));
        setCustReqstdArrivlDate(parseDate(getCustReqstdArrivlYear(),getCustReqstdArrivlMonth(),getCustReqstdArrivlDay()));
        
        if ((DraftQuoteConstants.PYMNT_TERMS_RADIO_MANUAL).equals(getPymntTermsRadio())
                && StringUtils.isNotBlank(StringUtils.trim(getPymntTermsInput()))) {
            if (isQtPymntTermsValid())
                setPymntTermsDays(Integer.parseInt(getPymntTermsInput()));
        } else {
            setPymntTermsDays(DraftQuoteConstants.PYMNT_TERMS_STAND_DAYS);
        }
        if (StringUtils.isNotBlank(parameters.getParameterAsString(ParamKeys.PARAM_OEM_BID_TYPE))) {
            try {
                oemBidType = Integer.parseInt(parameters.getParameterAsString(ParamKeys.PARAM_OEM_BID_TYPE));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * @return Returns the buttonName.
     */
    public String getButtonName() {
        return buttonName;
    }

    /**
     * @param buttonName
     *            The buttonName to set.
     */
    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    /**
     * @return Returns the expirationDay.
     */
    public String getExpirationDay() {
        return expirationDay;
    }

    /**
     * @param expirationDay
     *            The expirationDay to set.
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
     * @param expirationMonth
     *            The expirationMonth to set.
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
     * @param expirationYear
     *            The expirationYear to set.
     */
    public void setExpirationYear(String expirationYear) {
        this.expirationYear = expirationYear;
    }

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

    /**
     * @return Returns the maxExpirationDays.
     */
    public String getMaxExpirationDays() {
        return maxExpirationDays;
    }

    /**
     * @param maxExpirationDays
     *            The maxExpirationDays to set.
     */
    public void setMaxExpirationDays(String maxExpirationDays) {
        this.maxExpirationDays = maxExpirationDays;
    }

    /**
     * @return Returns the quoteType.
     */
    public String getQuoteType() {
        return quoteType;
    }

    /**
     * @param quoteType
     *            The quoteType to set.
     */
    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    /**
     * @return Returns the forwardFlag.
     */
    public Boolean getForwardFlag() {
        return Boolean.valueOf(forwardFlag);
    }

    /**
     * @param forwardFlag
     *            The forwardFlag to set.
     */
    public void setForwardFlag(String forwardFlag) {
        this.forwardFlag = forwardFlag;
    }
    
    public Date getExpireDate() {
        return expireDate;
    }
    
    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }
    
    public String getWebQuoteNum() {
        return webQuoteNum;
    }
    
    public void setWebQuoteNum(String webQuoteNum) {
        this.webQuoteNum = webQuoteNum;
    }
    
    public String getQuoteClassfctnCode() {
        return quoteClassfctnCode;
    }
    
    public void setQuoteClassfctnCode(String quoteClassfctnCode) {
        this.quoteClassfctnCode = quoteClassfctnCode;
    }
    

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }
    
    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }
    
    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getOemAgrmntType() {
        return oemAgrmntType;
    }
    
    public void setOemAgrmntType(String oemAgrmntType) {
        this.oemAgrmntType = oemAgrmntType;
    }
    
    public boolean isQtStartDateValid() {
        return isDateValid(getStartYear(), getStartMonth(), getStartDay());
    }
    
    public boolean isQtExpDateValid() {
        return isExpDateValid(getExpirationYear(), getExpirationMonth(), getExpirationDay());
    }
    
    public boolean isQtPymntTermsValid() {
        return isPymntTermsValid(getPymntTermsInput(), getPymntTermsRadio());
    }
    
    public boolean isEstmtdOrdDateValid() {
        return isEstmtdOrdDateValid(getEstmtdOrdYear(), getEstmtdOrdMonth(), getEstmtdOrdDay());
    }
    
    public boolean isCustReqstdArrivlDateValid(){
    	return isCustReqstdArrivlDateValid(getCustReqstdArrivlYear(), getCustReqstdArrivlMonth(), getCustReqstdArrivlDay());
    }
    
    protected boolean isDateValid(String year, String month, String day) {
        
        if (StringUtils.isBlank(year) && StringUtils.isBlank(month) && StringUtils.isBlank(day))
            return true;

        return DateHelper.validateDate(year, month, day);

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

    protected boolean isExpDateValid(String year, String month, String day) {
        
        if (StringUtils.isBlank(year) && StringUtils.isBlank(month) && StringUtils.isBlank(day))
            return true;
        
        if (!DateHelper.validateDate(year, month, day))
            return false;
        
        Date date = parseDate(year, month, day);
        Calendar curr = Calendar.getInstance();
        Date now = curr.getTime();
        Date currDate = DateUtils.truncate(now, Calendar.DATE);
        
        return (date != null && !currDate.after(date));
    }
    
    protected boolean isPymntTermsValid(String pymntTerms, String pymntTermsRadio) {
        String pattern = "^[1-9]\\d{1,2}$";
        Pattern p = Pattern.compile(pattern);
        if(pymntTermsRadio == null)
            return true;
        if((DraftQuoteConstants.PYMNT_TERMS_RADIO_STAND).equals(pymntTermsRadio))
            return true;
        else if ((DraftQuoteConstants.PYMNT_TERMS_RADIO_MANUAL).equals(pymntTermsRadio)) {
            if (StringUtils.isBlank(pymntTerms))
                return false;
            Matcher matcher = p.matcher(pymntTerms);
            if (matcher.matches()) {
                int temp = Integer.parseInt(pymntTerms);
                if (temp >= DraftQuoteConstants.PYMNT_TERMS_STAND_DAYS
                        && temp <= DraftQuoteConstants.PYMNT_TERMS_MAX_DAYS)
                    return true;
            }
        }
        return false;
    }
    
	protected boolean isEstmtdOrdDateValid(String year, String month, String day) {

		if (StringUtils.isBlank(year) && StringUtils.isBlank(month)
				&& StringUtils.isBlank(day))
			return true;

		if (!DateHelper.validateDate(year, month, day))
			return false;

		Date date = parseDate(year, month, day);
		Calendar curr = Calendar.getInstance();
		Date now = curr.getTime();
		Date currDate = DateUtils.truncate(now, Calendar.DATE);

		return (date != null && !currDate.after(date));
	}
	
	public boolean isCustReqstdArrivlDateValid(String year, String month, String day) {

		if (StringUtils.isBlank(year) && StringUtils.isBlank(month)
				&& StringUtils.isBlank(day))
			return true;

		if (!DateHelper.validateDate(year, month, day))
			return false;

		Date date = parseDate(year, month, day);
		Calendar curr = Calendar.getInstance();
		Date now = curr.getTime();
		Date currDate = DateUtils.truncate(now, Calendar.DATE);

		return (date != null && !currDate.after(date));
	}
    /**
     * @return Returns the pymntTermsDays.
     */
    public int getPymntTermsDays() {
        return pymntTermsDays;
    }
    /**
     * @param pymntTermsDays The pymntTermsDays to set.
     */
    public void setPymntTermsDays(int pymntTermsDays) {
        this.pymntTermsDays = pymntTermsDays;
    }
    /**
     * @return Returns the pymntTermsRadio.
     */
    public String getPymntTermsRadio() {
        return pymntTermsRadio;
    }
    /**
     * @param pymntTermsRadio The pymntTermsRadio to set.
     */
    public void setPymntTermsRadio(String pymntTermsRadio) {
        this.pymntTermsRadio = pymntTermsRadio;
    }
    /**
     * @return Returns the pymntTermsInput.
     */
    public String getPymntTermsInput() {
        return pymntTermsInput;
    }
    /**
     * @param pymntTermsInput The pymntTermsInput to set.
     */
    public void setPymntTermsInput(String pymntTermsInput) {
        this.pymntTermsInput = pymntTermsInput;
    }
    /**
     * @return Returns the oemOrAslType.
     */
    public int getOemBidType() {
        return oemBidType;
    }
    /**
     * @param oemOrAslType The oemOrAslType to set.
     */
    public void setOemBidType(int oemOrAslType) {
        this.oemBidType = oemOrAslType;
    }

	public String getEstmtdOrdDay() {
		return estmtdOrdDay;
	}

	public void setEstmtdOrdDay(String estmtdOrdDay) {
		this.estmtdOrdDay = estmtdOrdDay;
	}

	public String getEstmtdOrdMonth() {
		return estmtdOrdMonth;
	}

	public void setEstmtdOrdMonth(String estmtdOrdMonth) {
		this.estmtdOrdMonth = estmtdOrdMonth;
	}

	public String getEstmtdOrdYear() {
		return estmtdOrdYear;
	}

	public void setEstmtdOrdYear(String estmtdOrdYear) {
		this.estmtdOrdYear = estmtdOrdYear;
	}

	public Date getEstmtdOrdDate() {
		return estmtdOrdDate;
	}

	public void setEstmtdOrdDate(Date estmtdOrdDate) {
		this.estmtdOrdDate = estmtdOrdDate;
	}

	public Date getCustReqstdArrivlDate() {
		return custReqstdArrivlDate;
	}

	public void setCustReqstdArrivlDate(Date custReqstdArrivlDate) {
		this.custReqstdArrivlDate = custReqstdArrivlDate;
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

	public String getSspType() {
		return sspType;
	}

	public void setSspType(String sspType) {
		this.sspType = sspType;
	}

	public String getInstallAtOption() {
		return installAtOption;
	}

	public void setInstallAtOption(String installAtOption) {
		this.installAtOption = installAtOption;
	}

	public String getShipToOption() {
		return shipToOption;
	}

	public void setShipToOption(String shipToOption) {
		this.shipToOption = shipToOption;
	}
	
}
