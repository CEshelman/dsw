package com.ibm.dsw.automation.vo;

import java.io.Serializable;

public class LogonInf implements Serializable {
	
	private String accessLevel;
	
	private String sqoLogonUser;
	private String sqoUserPwd;
	private String sqoUrl;
	
	private String pgsLogonUser;
	private String pgsUserPwd;
	private String pgsUrl;
	
	public String getAccessLevel() {
		return accessLevel;
	}
	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}
	
	public String getSqoLogonUser() {
		return sqoLogonUser;
	}
	public void setSqoLogonUser(String sqoLogonUser) {
		this.sqoLogonUser = sqoLogonUser;
	}
	public String getSqoUserPwd() {
		return sqoUserPwd;
	}
	public void setSqoUserPwd(String sqoUserPwd) {
		this.sqoUserPwd = sqoUserPwd;
	}
	public String getSqoUrl() {
		return sqoUrl;
	}
	public void setSqoUrl(String sqoUrl) {
		this.sqoUrl = sqoUrl;
	}
	public String getPgsLogonUser() {
		return pgsLogonUser;
	}
	public void setPgsLogonUser(String pgsLogonUser) {
		this.pgsLogonUser = pgsLogonUser;
	}
	public String getPgsUserPwd() {
		return pgsUserPwd;
	}
	public void setPgsUserPwd(String pgsUserPwd) {
		this.pgsUserPwd = pgsUserPwd;
	}
	public String getPgsUrl() {
		return pgsUrl;
	}
	public void setPgsUrl(String pgsUrl) {
		this.pgsUrl = pgsUrl;
	}
	
	

}
