package com.ibm.dsw.quote.partner.contract;

import com.ibm.dsw.quote.base.contract.QuoteBaseContract;
import com.ibm.dsw.quote.draftquote.config.DraftQuoteParamKeys;
import com.ibm.ead4j.jade.session.JadeSession;
import com.ibm.ead4j.jade.util.Parameters;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>PartnerBaseContract</code> class is the base class for partner
 * search.
 * 
 * @author: zhaohuic@cn.ibm.com
 * 
 * Created on: Mar 5, 2007
 */
public class PartnerBaseContract extends QuoteBaseContract {

    private String lobCode;

    private String custCnt;
	private String migrationReqNum;
	private String agreementNum;
	private String sapCustNum;
	private String sapCtrctNum;
	private String highlightId;
	private String pageFrom;

    /**
     * @return Returns the country.
     */
    public String getCustCnt() {
        return custCnt;
    }

    /**
     * @param country
     *            The country to set.
     */
    public void setCustCnt(String custCnt) {
        this.custCnt = custCnt;
    }

    /**
     * @return Returns the lob.
     */
    public String getLobCode() {
        return lobCode;
    }

    /**
     * @param lob
     *            The lob to set.
     */
    public void setLobCode(String lobCode) {
        this.lobCode = lobCode;
    }

    private String searchMethod;

    private String pageIndex = "1";

    /**
     * @return Returns the pageIndex.
     */
    public String getPageIndex() {
        return pageIndex;
    }

    /**
     * @param pageIndex
     *            The pageIndex to set.
     */
    public void setPageIndex(String pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * @return Returns the searchMethod.
     */
    public String getSearchMethod() {
        return searchMethod;
    }

    /**
     * @param searchMethod
     *            The searchMethod to set.
     */
    public void setSearchMethod(String searchMethod) {
        this.searchMethod = searchMethod;
    }

    public void load(Parameters parameters, JadeSession session) {
        super.load(parameters, session);

    }
    
    
	public String getMigrationReqNum() {
		return migrationReqNum;
	}

	public void setMigrationReqNum(String migrationReqNum) {
		this.migrationReqNum = migrationReqNum;
	}

	public String getAgreementNum() {
		return agreementNum;
	}

	public void setAgreementNum(String agreementNum) {
		this.agreementNum = agreementNum;
	}

	public String getSapCustNum() {
		return sapCustNum;
	}

	public void setSapCustNum(String sapCustNum) {
		this.sapCustNum = sapCustNum;
	}

	public String getSapCtrctNum() {
		return sapCtrctNum;
	}

	public void setSapCtrctNum(String sapCtrctNum) {
		this.sapCtrctNum = sapCtrctNum;
	}

	public String getHighlightId() {
		return highlightId;
	}

	public void setHighlightId(String highlightId) {
		this.highlightId = highlightId;
	}
	
	public String getPageFrom() {
		return (pageFrom == null ? "" : pageFrom);
	}
	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public String getFct2PAMigrtnFlag() {
		if (getPageFrom().equalsIgnoreCase(
				DraftQuoteParamKeys.PAGE_FROM_FCT2PA_CUST_PARTNER)) {
			return "1";
		}
		return "0";
	}	
}
