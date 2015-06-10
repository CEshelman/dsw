package com.ibm.dsw.quote.base.domain;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import is.domainx.User;

import org.apache.commons.lang.StringUtils;

/**
 * A subset of data representing the sales rep that is stored in the <code>Session</code>
 * 
 * <p>Copyright 2006 by IBM Corporation All rights reserved.</p>
 * 
 * <p>This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information"). </p>
 * 
 * @author <a href="mailto:mgivney@us.ibm.com">Matt Givney</a><br/>
 */
public class QuoteUserSession implements Serializable {

    /** the sales rep's email address */
    private java.lang.String		emailAddress;

    /** the sales rep's country code */
    private java.lang.String		countryCode;
    
    /** the sales rep's full name */
    private java.lang.String		fullName;
    
    /** the sales rep's phone number */
    private java.lang.String		phoneNumber;
    
    /** the sales rep's fax number */
    private java.lang.String		faxNumber;

    /** a listing of direct reports for this sales rep */
    private java.util.ArrayList	reportingHierarchy;
    
    /** a listing of up 2 level reports for this sales rep*/
    private transient List up2ReportingHierarchy;
    
    /** the sales rep's serial number */
    private java.lang.String serialNumber;
    
    /** the sales rep's serial number */
    private java.lang.String countryCode3;
    
    /** the sales rep's first name */
    private java.lang.String firstName;
    
    /** the sales rep's last name */
    private java.lang.String lastName;
    
    /** the sales rep's notes id */
    private java.lang.String notesId;
    
    private String audienceCode = null;

	private String regionCode;

	private String siteNumber = null;
	
	private String sapCtrctNum = null;

	private String bpTierModel = null;// the tier model of login partner
	
	private boolean isHouseAccountFlag;//identify if the login partner is house account under tier 1 model
	
	private String uniqueID = null;
	
	private String WIID = null;
	
	private String companyName = null;
	
	private String sapSalesOrgCode = null;

	private boolean isTeleSales;
	
	private User webAuthUser = null;
	
	/**
	 * This attribute userId is generated in BaseServlet preService method.
	 */
	private String userId = null;	
	
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getAccessLevel(String code){
		return this.getWebAuthUser().getAccessLevel(code);
	}
	
	public String getEmail(){
		return this.getWebAuthUser().getEmail();
	}
	

	public User getWebAuthUser() {
		return webAuthUser;
	}


	public void setWebAuthUser(User webAuthUser) {
		this.webAuthUser = webAuthUser;
	}


	public boolean isTeleSales() {
		return isTeleSales;
	}


	public void setTeleSales(boolean isTeleSales) {
		this.isTeleSales = isTeleSales;
	}

	public String getSapSalesOrgCode() {
		return sapSalesOrgCode;
	}


	public void setSapSalesOrgCode(String sapSalesOrgCode) {
		this.sapSalesOrgCode = sapSalesOrgCode;
	}


	public String getCompanyName() {
		return companyName;
	}


	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}


	public String getWIID() {
		return WIID;
	}


	public void setWIID(String wIID) {
		WIID = wIID;
	}


	public String getUniqueID() {
		return uniqueID;
	}


	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}


	/**
     * Constructor
     */
    public QuoteUserSession() {
        super();
    }
    
    
    public String getAudienceCode() {
		return audienceCode;
	}


	public void setAudienceCode(String audienceCode) {
		this.audienceCode = audienceCode;
	}


	/**
     *  Gets the emailAddress
     * @return Returns the emailAddress.
     */
    public java.lang.String getEmailAddress() {
        return emailAddress;
    }
    /**
     * Sets the emailAddress
     * @param emailAddress The emailAddress to set.
     */
    public void setEmailAddress(java.lang.String emailAddress) {
        this.emailAddress = emailAddress;
    }
    /**
     *  Gets the countryCode
     * @return Returns the countryCode.
     */
    public java.lang.String getCountryCode() {
        return countryCode;
    }
    /**
     * Sets the countryCode
     * @param countryCode The countryCode to set.
     */
    public void setCountryCode(java.lang.String countryCode) {
        this.countryCode = countryCode;
    } 
    /**
     *  Gets the reportingHierarchy
     * @return Returns the reportingHierarchy.
     */
    public java.util.ArrayList getReportingHierarchy() {
        return reportingHierarchy;
    }
    /**
     *  Gets the up2ReportingHierarchy
     * @return Returns the up2ReportingHierarchy.
     */
    public List getUp2ReportingHierarchy() {
        return up2ReportingHierarchy;
    }
    /**
     * Sets the reportingHierarchy
     * @param reportingHierarchy The reportingHierarchy to set.
     */
    public void setReportingHierarchy(java.util.ArrayList reportingHierarchy) {
        this.reportingHierarchy = reportingHierarchy;
    }
    /**
     * Sets the up2ReportingHierarchy
     * @param up2ReportingHierarchy The up2ReportingHierarchy to set.
     */
    public void setUp2ReportingHierarchy(List up2ReportingHierarchy) {
        this.up2ReportingHierarchy = up2ReportingHierarchy;
    }
    /**
     * @return Returns the fullName.
     */
    public java.lang.String getFullName() {
        return fullName;
    }
    /**
     * @param fullName The fullName to set.
     */
    public void setFullName(java.lang.String fullName) {
        this.fullName = fullName;
    }
    /**
     * @return Returns the phoneNumber.
     */
    public java.lang.String getPhoneNumber() {
        return phoneNumber;
    }
    /**
     * @param phoneNumber The phoneNumber to set.
     */
    public void setPhoneNumber(java.lang.String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    /**
     * @return Returns the serialNumber.
     */
    public java.lang.String getSerialNumber() {
        return serialNumber;
    }
    /**
     * @param serialNumber The serialNumber to set.
     */
    public void setSerialNumber(java.lang.String serialNumber) {
        this.serialNumber = serialNumber;
    }
    /**
     * @return Returns the countryCode3.
     */
    public java.lang.String getCountryCode3() {
        return countryCode3;
    }
    /**
     * @param countryCode3 The countryCode3 to set.
     */
    public void setCountryCode3(java.lang.String countryCode3) {
        this.countryCode3 = countryCode3;
    }
    /**
     * @return Returns the firstName.
     */
    public java.lang.String getFirstName() {
        return firstName;
    }
    /**
     * @param firstName The firstName to set.
     */
    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }
    /**
     * @return Returns the lastName.
     */
    public java.lang.String getLastName() {
        return lastName;
    }
    /**
     * @param lastName The lastName to set.
     */
    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }
    /**
     * @return Returns the faxNumber.
     */
    public java.lang.String getFaxNumber() {
        return faxNumber;
    }
    /**
     * @param faxNumber The faxNumber to set.
     */
    public void setFaxNumber(java.lang.String faxNumber) {
        this.faxNumber = faxNumber;
    }
    /**
     * @return Returns the notesId.
     */
    public java.lang.String getNotesId() {
        return notesId;
    }
    /**
     * @param notesId The notesId to set.
     */
    public void setNotesId(java.lang.String notesId) {
        this.notesId = notesId;
    }
    /**
     * Get the up2ReportingHierarchy user ids
     * @return
     */
    public String getUp2ReportingUserIds() {
        StringBuffer up2ReportingUserIds = new StringBuffer();
        List userIDList = getUp2ReportingHierarchy();
        if (userIDList != null && userIDList.size() > 0) {
            up2ReportingUserIds.append(",");
            Iterator iter = userIDList.iterator();
            while (iter.hasNext()) {
                up2ReportingUserIds.append(iter.next()).append(",");
            }
        }
        return StringUtils.lowerCase(up2ReportingUserIds.toString());
    }
    
    /**
     * Gets the contents of this object as a <code>String</code>
     * @return a string representation of this object
     * @see java.lang.Object#toString()
     */
    public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("QuoteUserSession contents: \n");
      buffer.append("email address: ").append(getEmailAddress()).append("\n");
      buffer.append("country code: ").append(getCountryCode()).append("\n");
      buffer.append("serial number: ").append(getSerialNumber()).append("\n");
      buffer.append("country code 3: ").append(getCountryCode3()).append("\n");
      buffer.append("first name: ").append(getFirstName()).append("\n");
      buffer.append("last name: ").append(getLastName()).append("\n");
      if( reportingHierarchy != null && reportingHierarchy.size() > 0 ){
          buffer.append("Reporting hierarchy as follows:\n");
          for( int i=0 ; i < reportingHierarchy.size() ; i++ ){
              buffer.append( (String) reportingHierarchy.get(i) ).append("\n");
          }
      }
      if( up2ReportingHierarchy != null && up2ReportingHierarchy.size() > 0 ){
        buffer.append("Up2 level reporting hierarchy as follows:\n");
        for( int i=0 ; i < up2ReportingHierarchy.size() ; i++ ){
            buffer.append( (String) up2ReportingHierarchy.get(i) ).append("\n");
        }
    }
      return buffer.toString();
    }


	/**
	 * @return the regionCode
	 */
	public String getRegionCode() {
		return regionCode;
	}


	/**
	 * @param regionCode the regionCode to set
	 */
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}


	public String getSiteNumber() {
		return siteNumber;
	}


	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}
	
	public String getBpTierModel() {
		return bpTierModel;
	}

	public void setBpTierModel(String bpTierModel) {
		this.bpTierModel = bpTierModel;
	}

    public boolean isHouseAccountFlag() {
		return isHouseAccountFlag;
	}

	public void setHouseAccountFlag(boolean isHouseAccountFlag) {
		this.isHouseAccountFlag = isHouseAccountFlag;
	}


	public String getSapCtrctNum() {
		return sapCtrctNum;
	}


	public void setSapCtrctNum(String sapCtrctNum) {
		this.sapCtrctNum = sapCtrctNum;
	}
    
}
