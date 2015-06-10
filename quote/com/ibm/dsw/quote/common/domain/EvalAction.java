package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mailto:kunzhwh@cn.ibm.com">Jason Zhang</a><br/>
 */
public interface EvalAction extends  Serializable {
	
	public String getFullName();
	
	public void setFullName(String fullName);
	
	public String getEmailAdr();
	
	public void setEmailAdr(String emailAdr);
	
	public String getUserAction();
	
	public void setUserAction(String userAction);
	
	public Date getAddDate();
	
	public void setAddDate(Date addDate);
    
	public String getComments();
	
	public void setComments(String comments);
}
