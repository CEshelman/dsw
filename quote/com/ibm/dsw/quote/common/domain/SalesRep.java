package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.List;

import com.ibm.ead4j.topaz.exception.TopazException;

/**
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 *
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 *
 * @author <a href="mailto:mgivney@us.ibm.com">Matt Givney</a><br/>
 *
 */
public interface SalesRep extends User {

    public static final String AUDIENCE = "INTERNAL";

    /**
     * The country code
     * @return the country code
     */
    public String getCountryCode();

    /**
     * Gets the email address
     * @return the email address
     */
    public String getEmailAddress();

    /**
     * Gets the full name
     * @return the full name
     */
    public String getFullName();

    /**
     * Gets the first name
     * @return the first name
     */
    public String getFirstName();

    /**
     * Gets the last name
     * @return the last name
     */
    public String  getLastName();

    /**
     * Gets the sales org
     * @return the sales org
     */
    public String getSalesOrg();

    /**
     * Gets the phone number
     * @return the phone number
     */
    public String getPhoneNumber();

    /**
     * Gets the fax number
     * @return the fax number
     */
    public String getFaxNumber();

    /**
     * Gets the telesales access level
     * @return the telesales access leve
     */
    public int getTelesalesAccessLevel();

    /**
     * Gets the reporting reps
     * @return an <code>ArrayList</code> of employee ids in reporting chain
     */
    public ArrayList 	getReportingReps();

    /**
     * Gets the up 2 level reporting reps
     * @return
     */
    public List getUp2ReportingReps();

    public String getNotesId();

    /**
     * @return Returns the serialNumber.
     */
    public String getSerialNumber();

    /**
     * @return Returns the countryCode3.
     */
    public String getCountryCode3();

    /**
     * Sets the blue pages information
     *
     * @param countryCode the country code
     * @param fullName the full name
     * @param lastName the last name
     * @param firstName the first name
     * @param phoneNumber the phone number
     * @param faxNumber the fax number
     * @param DirectReportList the direct report list
     * @param emailAddress the user's internet id
     * @param notesId
     * @param serialNumber
     * @throws TopazException
     */
    public void setBluepageInformation(	String countryCode,
            													String fullName,
            													String lastName,
            													String firstName,
            													String phoneNumber,
            													String faxNumber,
            													ArrayList DirectReportList,
																List up2reportList,
            													String emailAddress,
            													String notesId,
            													String serialNumber)
    throws TopazException;

    public void getBluePageInfo();

	public String getCompany();
	
	public boolean isEvaluator();

}
