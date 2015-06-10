package com.ibm.dsw.quote.draftquote.contract;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.base.util.StringEncoder;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;
/**
 * Use textToHTML to avoid Cross-Site Scripting reported by App Scan.
 * @author Jason
 */
public class PrepareAddLPPFormContract extends DraftQuoteBaseContract {
	String quoteNumber;
	String lineSeqNum;
    String lpp;
	private String currencyCode;
	private String type="DEFAULT"; 
	private String quoteCurrencyCode;
	private String renewalNum;
	private String priorPrice;
	private String systemPriorPrice;
	private String gdPartFlag;

    public String getQuoteCurrencyCode() {
        return quoteCurrencyCode;
    }

	public void setQuoteCurrencyCode(String quoteCurrencyCode) {
	    /**
         * avoid Cross-Site Scripting reported by App Scan.
         */
	    if (StringUtils.isNotBlank(quoteCurrencyCode)) {
            if (quoteCurrencyCode.length() > 3) {
                quoteCurrencyCode = "";
            }
            quoteCurrencyCode = StringEncoder.textToHTML(quoteCurrencyCode);
        }
		this.quoteCurrencyCode = quoteCurrencyCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
	    if (StringUtils.isNotBlank(type)) {
	        type = StringEncoder.textToHTML(type);
        }
		this.type = type;
	}

	public String getLpp() {
		return lpp;
	}

	public void setLpp(String lpp) {
	    if (StringUtils.isNotBlank(lpp)) {
	        lpp = StringEncoder.textToHTML(lpp);
        }
		this.lpp = lpp;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
	    if (StringUtils.isNotBlank(currencyCode)) {
            if (currencyCode.length() > 3) {
                currencyCode = "";
            }
            currencyCode = StringEncoder.textToHTML(currencyCode);
        }
		this.currencyCode = currencyCode;
	}

	public String getLineSeqNum() {
		return lineSeqNum;
	}

	public void setLineSeqNum(String lineSeqNum) {
	    if (StringUtils.isNotBlank(lineSeqNum)) {
            lineSeqNum = StringEncoder.textToHTML(lineSeqNum);
        }
		this.lineSeqNum = lineSeqNum;
	}

    public String getQuoteNumber() {
        return quoteNumber;
    }

	public void setQuoteNumber(String quoteNumber) {
	    if (StringUtils.isNotBlank(quoteNumber)) {
            if (quoteNumber.length() > 10) {
                quoteNumber = "";
            }
            quoteNumber = StringEncoder.textToHTML(quoteNumber);
        }
		this.quoteNumber = quoteNumber;
	}
	
	 public void load(Parameters parameters, JadeSession session) {
	        super.load(parameters, session);
	 }

	public String getRenewalNum() {
		return renewalNum;
	}

	public void setRenewalNum(String renewalNum) {
	    if (StringUtils.isNotBlank(renewalNum)) {
            if (renewalNum.length() > 10) {
                renewalNum = "";
            }
            renewalNum = StringEncoder.textToHTML(renewalNum);
        }
		this.renewalNum = renewalNum;
	}

	public String getPriorPrice() {
		return priorPrice;
	}

	public void setPriorPrice(String priorPrice) {
	    if (StringUtils.isNotBlank(priorPrice)) {
	        priorPrice = StringEncoder.textToHTML(priorPrice);
        }
		this.priorPrice = priorPrice;
	}

	public String getSystemPriorPrice() {
		return systemPriorPrice;
	}

	public void setSystemPriorPrice(String systemPriorPrice) {
	    if (StringUtils.isNotBlank(systemPriorPrice)) {
	        systemPriorPrice = StringEncoder.textToHTML(systemPriorPrice);
	    }
		this.systemPriorPrice = systemPriorPrice;
	}

	public String getGdPartFlag() {
		return gdPartFlag;
	}

	public void setGdPartFlag(String gdPartFlag) {
		this.gdPartFlag = gdPartFlag;
	}

	
	
	
	 
	 
}
