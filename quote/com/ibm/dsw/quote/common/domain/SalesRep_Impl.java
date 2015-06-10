package com.ibm.dsw.quote.common.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibm.dsw.quote.bluepages.BluePageUser;
import com.ibm.dsw.quote.bluepages.BluePagesLookup;

/**
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 *
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 *
 * @author <a href="mailto:mgivney@us.ibm.com">Matt Givney</a><br/>
 *
 */
public abstract class SalesRep_Impl implements SalesRep{
    /** the email address */
    public String emailAddress;
    /** the full name */
    public String fullName;
    /** the first name */
    public String firstName;
    /** the last name */
    public String lastName;
    /** the sales org */
    public String salesOrg;
    /** the phone number */
    public String phoneNumber;
    /** the fax number */
    public String faxNumber;
    /** the telesales access level */
    public int telesalesAccessLevel;
    /** the reporting reps */
    public ArrayList reportingReps;
    /** the up 2 reps */
    public List up2ReportingReps;
    /** the login id */
    public String loginId;
    /** the country code */
    public String countryCode;
    /** the notes id */
    public String notesId;
    /** the serial number */
    public String serialNumber;
    /** 3-character country code */
    public String countryCode3;

    protected String company;
    
    protected boolean isEvaluator;

	/**
     * Constructor
     */
    public SalesRep_Impl() {
        super();
    }
    /**
     *  Gets the countryCode
     * @return Returns the countryCode.
     */
    public String getCountryCode() {
        return countryCode;
    }
    /**
     *  Gets the emailAddress
     * @return Returns the emailAddress.
     */
    public String getEmailAddress() {
        return emailAddress;
    }
    /**
     *  Gets the faxNumber
     * @return Returns the faxNumber.
     */
    public String getFaxNumber() {
        return faxNumber;
    }
    /**
     *  Gets the firstName
     * @return Returns the firstName.
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     *  Gets the fullName
     * @return Returns the fullName.
     */
    public String getFullName() {
        return fullName;
    }
    /**
     *  Gets the lastName
     * @return Returns the lastName.
     */
    public String getLastName() {
        return lastName;
    }
    /**
     *  Gets the loginId
     * @return Returns the loginId.
     */
    public String getLoginId() {
        return loginId;
    }
    /**
     *  Gets the notesId
     * @return Returns the notesId.
     */
    public String getNotesId() {
        return notesId;
    }
    /**
     *  Gets the phoneNumber
     * @return Returns the phoneNumber.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    /**
     *  Gets the reportingReps
     * @return Returns the reportingReps.
     */
    public ArrayList getReportingReps() {
        return reportingReps;
    }
    /**
     *  Gets the up2ReportingReps
     * @return Returns the up2ReportingReps.
     */
    public List getUp2ReportingReps() {
        return up2ReportingReps;
    }
    /**
     *  Gets the salesOrg
     * @return Returns the salesOrg.
     */
    public String getSalesOrg() {
        return salesOrg;
    }
    /**
     *  Gets the telesalesAccessLevel
     * @return Returns the telesalesAccessLevel.
     */
    public int getTelesalesAccessLevel() {
        return telesalesAccessLevel;
    }
    /**
     * @return Returns the serialNumber.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * @return Returns the countryCode3.
     */
    public String getCountryCode3() {
        return countryCode3;
    }

    public String getCompany() {
		return company;
	}

    public boolean isEvaluator() {
		return isEvaluator;
	}
    
	public void getBluePageInfo() {
        if (StringUtils.isNotBlank(getEmailAddress())) {
            BluePageUser bpUser = BluePagesLookup.getBluePagesInfo(getEmailAddress());
            if (bpUser != null) {
                if (StringUtils.isBlank(countryCode)) {
                    countryCode = StringUtils.trimToEmpty(bpUser.getCountryCode());
                }
                firstName = StringUtils.trimToEmpty(bpUser.getFirstName());
                lastName = StringUtils.trimToEmpty(bpUser.getLastName());
                fullName = StringUtils.isBlank(bpUser.getFullName()) ? firstName + " " + lastName : StringUtils
                        .trimToEmpty(bpUser.getFullName());
                phoneNumber = StringUtils.trimToEmpty(bpUser.getPhoneNumber());
                faxNumber = StringUtils.trimToEmpty(bpUser.getFaxNumber());
                loginId = getEmailAddress();
                notesId = StringUtils.trimToEmpty(bpUser.getNotesId());
                serialNumber = StringUtils.trimToEmpty(bpUser.getBluePagesId());
            }
        }
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("emailAddress = ").append(emailAddress).append("\n");
        buffer.append("fullName = ").append(fullName).append("\n");
        buffer.append("firstName = ").append(firstName).append("\n");
        buffer.append("lastName = ").append(lastName).append("\n");
        buffer.append("salesOrg= ").append(salesOrg).append("\n");
        buffer.append("phoneNumber = ").append(phoneNumber).append("\n");
        buffer.append("faxNumber = ").append(faxNumber).append("\n");
        buffer.append("telesalesAccessLevel = ").append(telesalesAccessLevel).append("\n");
        buffer.append("loginId = ").append(loginId).append("\n");
        buffer.append("notesId = ").append(notesId).append("\n");
        buffer.append("serialNumber = ").append(serialNumber).append("\n");
        buffer.append("countryCode = ").append(countryCode).append("\n");
        buffer.append("countryCode3 = ").append(countryCode3).append("\n");
        buffer.append("company = ").append(company).append("\n");
        for(int i = 0; reportingReps != null && i < reportingReps.size(); i ++){
            buffer.append("reportingReps" + i).append((String) reportingReps.get(i)).append("\n");
        }
        for(int i = 0; up2ReportingReps != null && i < up2ReportingReps.size(); i ++){
            buffer.append("up2ReportingReps" + i).append((String) up2ReportingReps.get(i)).append("\n");
        }
        return buffer.toString();
    }

}
