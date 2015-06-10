package com.ibm.dsw.quote.draftquote.process;

import java.util.Date;

import com.ibm.dsw.quote.base.config.QuoteConstants;

public class ToU_Impl implements ToU,Comparable{
	
	 public String touName="";

	 public String touUrl="";

	 public Date releaseDate = null;

	 public Date addDate = null;

	 public Amendment amendment= null;

	 public int amendmentFlag = 0;
	 
	 public int amendmentBFlag = 0;
	 
	 public String termsType ="";
	 
	 public String termsSubType="";
	 
	 public String docId="";

	public String getTouName() {
		return touName;
	}

	public void setTouName(String touName) {
		this.touName = touName;
	}

	public String getTouUrl() {
		return touUrl;
	}

	public void setTouUrl(String touUrl) {
		this.touUrl = touUrl;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Amendment getAmendment() {
		return amendment;
	}

	public void setAmendment(Amendment amendment) {
		this.amendment = amendment;
	}

	public int getAmendmentFlag() {
		return amendmentFlag;
	}

	public void setAmendmentFlag(int amendmentFlag) {
		this.amendmentFlag = amendmentFlag;
	}
	
	

	public String getTermsType() {
		return termsType;
	}

	public void setTermsType(String termsType) {
		this.termsType = termsType;
	}

	public String getTermsSubType() {
		return termsSubType;
	}

	public void setTermsSubType(String termsSubType) {
		this.termsSubType = termsSubType;
	}		

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}
	
	public int getAmendmentBFlag() {
		return amendmentBFlag;
	}

	public void setAmendmentBFlag(int amendmentBFlag) {
		this.amendmentBFlag = amendmentBFlag;
	}
	
	@Override
	public int compareTo(Object obj) {
		ToU tou =(ToU)obj;
		if(QuoteConstants.ToU_TERMSTYPE_CSA.equals(tou.getTermsType())){
			return 999;
		}
		else if(tou.getTermsType().equals(this.getTermsType()) && tou.getDocId().equals(this.getDocId())){
			return 0;
		}else if(QuoteConstants.ToU_SUBTYPE_PARTB.equals(this.getTermsSubType())){
			return 999;
		}else{
			return (tou.getTermsType()+tou.getDocId()).compareTo(this.getTermsType()+this.getDocId());
		}
	}
 

}
