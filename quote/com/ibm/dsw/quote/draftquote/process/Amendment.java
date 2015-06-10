package com.ibm.dsw.quote.draftquote.process;

import java.util.Date;

public interface Amendment {
	 public String getAmendmentName();

	 public String getAmendmentUrl();

	 public String getTouUrl();

	 public Date getAmendmentDate();

	 public int getCurrentAmendmentFlag();
	 
	 public String getTermsCondsTypeCode();
	 
	 public String getDocID();
}
