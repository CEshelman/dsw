package com.ibm.dsw.quote.draftquote.process;

import java.util.Date;

public class Amendment_Impl implements Amendment{
	
	 public String amendmentName="";

	 public String amendmentUrl="";

	 public String touUrl="";
	 
	 public Date amendmentDate = null;

	 public int currentAmendmentFlag= 0;
	 
	 public String termsCondsTypeCode="";
	 
	 public String docID="";
	 
	 

	public String getTouUrl() {
		return touUrl;
	}

	public void setTouUrl(String touUrl) {
		this.touUrl = touUrl;
	}


	public void setAmendmentName(String amendmentName) {
		this.amendmentName = amendmentName;
	}

	public void setAmendmentUrl(String amendmentUrl) {
		this.amendmentUrl = amendmentUrl;
	}

	public void setAmendmentDate(Date amendmentDate) {
		this.amendmentDate = amendmentDate;
	}

	public String getAmendmentName() {
		return amendmentName;
	}

	public String getAmendmentUrl() {
		return amendmentUrl;
	}

	public Date getAmendmentDate() {
		return amendmentDate;
	}

	public int getCurrentAmendmentFlag() {
		return currentAmendmentFlag;
	}

	public void setCurrentAmendmentFlag(int currentAmendmentFlag) {
		this.currentAmendmentFlag = currentAmendmentFlag;
	}

	public String getTermsCondsTypeCode() {
		return termsCondsTypeCode;
	}

	public void setTermsCondsTypeCode(String termsCondsTypeCode) {
		this.termsCondsTypeCode = termsCondsTypeCode;
	}

	public String getDocID() {
		return docID;
	}

	public void setDocID(String docID) {
		this.docID = docID;
	}

	
	

	
	

}
