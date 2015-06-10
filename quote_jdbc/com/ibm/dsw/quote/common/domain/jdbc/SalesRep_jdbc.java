package com.ibm.dsw.quote.common.domain.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.ibm.dsw.quote.common.domain.SalesRep_Impl;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject;

/**
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 *
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 *
 * @author <a href="mailto:mgivney@us.ibm.com">Matt Givney</a><br/>
 *
 */
public class SalesRep_jdbc extends SalesRep_Impl implements PersistentObject, Serializable {

    /** the persister object */
	private transient SalesRepPersister persister;


    /**
     * Constructor
     */
    public SalesRep_jdbc() {
        super();
    }

    public SalesRep_jdbc( String internetId )
    {
        this.setEmailAddress( internetId );
        persister = new SalesRepPersister( this );
    }
    /**
     * Constructor
     * @param internetId the user's email address
     * @param telesalesAccessLevel the telesales access level
     */
    public SalesRep_jdbc( String internetId, int telesalesAccessLevel ){
        this(internetId);
        this.setTelesalesAccessLevel( telesalesAccessLevel );
    }

    /**
     *
     * @param connection
     * @throws com.ibm.ead4j.topaz.exception.TopazException
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#hydrate(java.sql.Connection)
     */
    public void hydrate(Connection connection) throws TopazException {
        persister.hydrate(connection);
    }

    /**
     * @param connection
     * @throws com.ibm.ead4j.topaz.exception.TopazException
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObject#persist(java.sql.Connection)
     */
    public void persist(Connection connection) throws TopazException {
        persister.persist(connection);
    }

    /**
     * @param deleteState
     * @throws com.ibm.ead4j.topaz.exception.TopazException
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isDeleted(boolean)
     */
    public void isDeleted(boolean deleteState) throws TopazException {
    }

    /**
     * @param newState
     * @throws com.ibm.ead4j.topaz.exception.TopazException
     * @see com.ibm.ead4j.topaz.persistence.jdbc.PersistentObjectState#isNew(boolean)
     */
    public void isNew(boolean newState) throws TopazException {
        persister.isNew(newState);
    }

    /**
     *
     * @param countryCode
     * @param fullName
     * @param lastName
     * @param firstName
     * @param phoneNumber
     * @param faxNumber
     * @param DirectReportList
     * @param emailAddress
     * @param notesId
     * @param serialNumber
     * @throws TopazException
     * @see com.ibm.dsw.quote.common.domain.SalesRep#setBluepageInformation(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.ArrayList, java.lang.String, java.lang.String)
     */
    public void setBluepageInformation( String countryCode,
            													String fullName,
            													String lastName,
            													String firstName,
            													String phoneNumber,
            													String faxNumber,
            													ArrayList DirectReportList,
																List up2ReportList,
            													String emailAddress,
            													String notesId,
            													String serialNumber)
    throws TopazException {
        this.setCountryCode( countryCode );
        this.setFullName( fullName );
        this.setLastName( lastName );
        this.setFirstName( firstName );
        this.setPhoneNumber( phoneNumber );
        this.setFaxNumber( faxNumber );
        this.setReportingReps( DirectReportList );
        this.setUp2ReportingReps(up2ReportList);
        this.setLoginId( emailAddress );
        this.setNotesId( notesId );
        this.setSerialNumber( serialNumber );
    }


    /**
     * Sets the countryCode
     * @param countryCode The countryCode to set.
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    /**
     * Sets the emailAddress
     * @param emailAddress The emailAddress to set.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    /**
     * Sets the faxNumber
     * @param faxNumber The faxNumber to set.
     */
    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }
    /**
     * Sets the firstName
     * @param firstName The firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * Sets the fullName
     * @param fullName The fullName to set.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    /**
     * Sets the lastName
     * @param lastName The lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * Sets the loginId
     * @param loginId The loginId to set.
     */
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
    /**
     * Sets the notesId
     * @param notesId The notesId to set.
     */
    public void setNotesId(String notesId) {
        this.notesId = notesId;
    }
    /**
     * Sets the phoneNumber
     * @param phoneNumber The phoneNumber to set.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    /**
     * Sets the reportingReps
     * @param reportingReps The reportingReps to set.
     */
    public void setReportingReps(ArrayList reportingReps) {
        this.reportingReps = reportingReps;
    }
    /**
     * Sets the up2ReportingReps
     * @param up2ReportingReps The up2ReportingReps to set.
     */
    public void setUp2ReportingReps(List up2ReportingReps) {
        this.up2ReportingReps = up2ReportingReps;
    }
    /**
     * Sets the salesOrg
     * @param salesOrg The salesOrg to set.
     */
    public void setSalesOrg(String salesOrg) {
        this.salesOrg = salesOrg;
    }
    /**
     * Sets the telesalesAccessLevel
     * @param telesalesAccessLevel The telesalesAccessLevel to set.
     */
    public void setTelesalesAccessLevel(int telesalesAccessLevel) {
        this.telesalesAccessLevel = telesalesAccessLevel;
    }
    /**
     * Sets the serialNumber
     * @param serialNumber The serialNumber to set.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setCompany(String company){
    	this.company = company;
    }
    
   public void setEvaluator(boolean isEvaluator) {
		this.isEvaluator = isEvaluator;
	}
}
