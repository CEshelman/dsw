package com.ibm.dsw.quote.draftquote.process;

import java.util.Date;

public interface ToU {
	 public String getTouName();

	 public String getTouUrl();

	 public Date getReleaseDate();

	 public Date getAddDate();

	 public Amendment getAmendment();

	 public String getTermsType();
	 
	 public String getTermsSubType();
	 
	 public int getAmendmentFlag();
	 
	 public int getAmendmentBFlag();
	 
	 public String getDocId();
	 
}
