package com.ibm.dsw.automation.vo;

public class CheckoutInf extends BaseVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9169970052799516924L;
	private String userID;
	private String userEmail;

	private String submit_first_name;
	private String submit_last_name;
	private String submit_email;
	private String submit_phone;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getSubmit_first_name() {
		return submit_first_name;
	}

	public void setSubmit_first_name(String submit_first_name) {
		this.submit_first_name = submit_first_name;
	}

	public String getSubmit_last_name() {
		return submit_last_name;
	}

	public void setSubmit_last_name(String submit_last_name) {
		this.submit_last_name = submit_last_name;
	}

	public String getSubmit_email() {
		return submit_email;
	}

	public void setSubmit_email(String submit_email) {
		this.submit_email = submit_email;
	}

	public String getSubmit_phone() {
		return submit_phone;
	}

	public void setSubmit_phone(String submit_phone) {
		this.submit_phone = submit_phone;
	}

}
