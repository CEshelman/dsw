/**
 * Created on July 6, 2006
 */
package com.ibm.dsw.quote.base.test;

import is.domainx.User;

import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A fugazi user to map from the jump page instead of web-auth
 * 
 * @author Jin Wang
 * @author Matt Givney
 */
public class TestUser implements User {
	private java.lang.String userId = "";
    /** the uniqueID */
    private java.lang.String uniqueID = "TLS0151519";

    /** the portal id */
    private java.lang.String portal = "TLS";

    /** the email address */
    private java.lang.String email;

    /** the country code */
    private java.lang.String cntryCode = "US";

    /** the country code */
    private java.lang.String contractNumber = "00000000";

    /** */
    private java.lang.String sAPNumber = "TLS0000000";
    
    /** A customer number the current user can work on*/
    private java.lang.String custNum = "";

    /** A customer number the current user can work on*/
    private java.lang.String firstName = "";

    /** A customer number the current user can work on*/
    private java.lang.String lastName = "";
    
    int accessLevel = 1; 
//    1 = Submitter
//    2 = Reader/Reviewer
//    3 = No Access
//    4 = eCare
//    5 = Approver
//    6 = Administrator

    private Hashtable accessLevelTable = new Hashtable();

    public TestUser() { 
        accessLevelTable.put("SQO", new Integer(this.getAccessLevel("SQO")));
    }

    public String getDominoID() {
        return "00000000";
    }

    public String getFirstName() {
        return "Tester";
    }

    public String getLastName() {
        return "Tester";
    }

    /**
     * Gets the portal
     * 
     * @return the portal id
     */
    public String getPortal() {
        return this.portal;
    }

    /**
     * Sets the portal
     * 
     * @param portalId
     *            the portal to set
     */
    public void setPortal(String portalId) {
        this.portal = portalId;
    }

    /**
     * Gets the SAP Number
     * 
     * @return the SAP Number
     * @see is.domainx.User#getSAPNumber()
     */
    public String getSAPNumber() {
        return this.sAPNumber;
    }

    /**
     * Sets the sAPNumber
     * 
     * @param number
     *            The sAPNumber to set.
     */
    public void setSAPNumber(java.lang.String number) {
        this.sAPNumber = number;
    }

    public String getWIID() {
        return "TLS0151519";
    }

    public boolean isOkToEnterPortal(String portal) {
        return true;
    }

    public int getAccessLevel(String applicationCode) {
        return this.accessLevel;
    }

    public int getAccessLevel(Object applicationCode) {
        return this.getAccessLevel((String)applicationCode);
    }

    public String getRole() {
        return "P";
    }

    public boolean isEntitledToApplication(String applicationCode) {
        return true;
    }

    /**
     * Sets the email address
     * 
     * @param email
     *            the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the email address
     * 
     * @return the email address
     * @see is.domainx.User#getEmail()
     */
    public String getEmail() {
        return this.email;
    }

    public Enumeration getApplicationCodes() {
        Enumeration appKeys = this.accessLevelTable.keys();
        if (appKeys.hasMoreElements())
            return this.accessLevelTable.keys();
        else {
            Vector v = new Vector();
            v.add("SQO");
            return v.elements();
        }
    }

    public boolean isPartiallyEntitled() {
        return true;
    }

    public boolean isOkToEnterPortal(String[] portal) {
        return true;
    }

    /**
     * Gets the country code
     * 
     * @return the country code
     * @see is.domainx.User#getCntryCode()
     */
    public String getCntryCode() {
        return this.cntryCode;
    }

    /**
     * Sets the country code
     * 
     * @param countryCode
     *            the country code to set
     */
    public void setCntryCode(String countryCode) {
        this.cntryCode = countryCode;
    }

    /**
     * Gets the contract number
     * 
     * @return the contract number
     * @see is.domainx.User#getContractNumber()
     */
    public String getContractNumber() {
        return this.contractNumber;
    }

    /**
     * Sets the contract number
     * 
     * @param contractNumber
     *            the contract number to set
     */
    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getStatCode() {
        return "1";
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public String getModDate() {
        return null;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Uid:").append(getUniqueID()).append("\n");
        buffer.append("Portal:").append(getPortal()).append("\n");
        buffer.append("Site Number:").append(getSAPNumber()).append("\n");
        buffer.append("Domino ID:").append(getDominoID()).append("\n");
        buffer.append("Email:").append(getEmail()).append("\n");
        buffer.append("Role:").append(getRole()).append("\n");
        buffer.append("userId:").append(getUserId()).append("\n");
        buffer.append("custNum:").append(getCustNum()).append("\n");
        buffer.append("sAPNumber:").append(getSAPNumber()).append("\n");
                
        buffer.append("........:");
        return buffer.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see is.domainx.User#isFullyEntitled()
     */
    public boolean isFullyEntitled() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see is.domainx.User#isTelesalesUser()
     */
    public boolean isTelesalesUser() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see is.domainx.User#hasEntitledPortal(java.lang.String)
     */
    public boolean hasEntitledPortal(String portal) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see is.domainx.User#getEntitledPortals()
     */
    public Vector getEntitledPortals() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see is.domainx.User#getUIApplicationAccess()
     */
    public Hashtable getUIApplicationAccess() {
        return accessLevelTable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see is.domainx.User#isReseller()
     */
    public boolean isReseller() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see is.domainx.User#isCustomer()
     */
    public boolean isCustomer() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see is.domainx.User#getUIApplicationAccessLevel(java.lang.String)
     */
    public int getUIApplicationAccessLevel(String code) {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see is.domainx.User#isLinkedUser()
     */
    public boolean isLinkedUser() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see is.domainx.User#getCCMSNumber()
     */
    public String getCCMSNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see is.domainx.User#isBHDeployed()
     */
    public boolean isBHDeployed() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see is.domainx.User#hasCCMSSiteNumber()
     */
    public boolean hasCCMSSiteNumber() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see is.domainx.User#getEntitledRenewalQuoteNumber()
     */
    public String getEntitledRenewalQuoteNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see is.domainx.User#setEntitledRenewalQuoteNumber(java.lang.String)
     */
    public void setEntitledRenewalQuoteNumber(String entitledRenewalQuoteNumber) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see is.domainx.User#isBasicInfoEquals(is.domainx.User)
     */
    public boolean isBasicInfoEquals(User arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isRenewalUser() {
        return false;
    }

    public boolean isWICallNeeded() {
        return false;
    }

	public java.lang.String getsAPNumber() {
		return sAPNumber;
	}

	public java.lang.String getCustNum() {
		return custNum;
	}

	public void setCustNum(java.lang.String custNum) {
		this.custNum = custNum;
	}

	public int getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
	}

	public void setUniqueID(java.lang.String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public java.lang.String getUserId() {
		return userId;
	}

	public void setUserId(java.lang.String userId) {
		this.userId = userId;
	}

	public void setFirstName(java.lang.String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(java.lang.String lastName) {
		this.lastName = lastName;
	}
	
	public String getCurrentPortal(){
		return "TLS";
	}
	
	public String getCookieDTime(){
	    	Timestamp ts = new Timestamp(System.currentTimeMillis());
	    	return String.valueOf(ts);
	}
}
