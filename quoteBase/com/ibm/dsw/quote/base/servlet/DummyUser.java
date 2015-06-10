/**
 * Created on July 6, 2006
 */
package com.ibm.dsw.quote.base.servlet;

import is.domainx.User;

import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A fugazi user to map from the jump page instead of web-auth
 * 
 * @author Jin Wang
 * @author Matt Givney
 */
public class DummyUser implements User {

    /** the portal id */
    private java.lang.String portal = "TLS";

    /** the email address */
    private java.lang.String email = "jinfahua@cn.ibm.com";

    /** the country code */
    private java.lang.String cntryCode = "CN";

    /** the country code */
    private java.lang.String contractNumber = "00000000";

    /** */
    private java.lang.String sAPNumber = "TLS0000000";

    int accessLevel = 4;

    //    1 = Submitter
    //    2 = Reader/Reviewer
    //    3 = No Access
    //    4 = eCare
    //    5 = Approver
    //    6 = Administrator

    private Hashtable accessLevelTable = new Hashtable();

    public DummyUser() {

    }

    public String getDominoID() {
        return "00000000";
    }

    public String getFirstName() {
        return "Fa Hua";
    }

    public String getLastName() {
        return "Jin";
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
        return getEmail();
    }

    public boolean isOkToEnterPortal(String portal) {
        return true;
    }

    public int getAccessLevel(String applicationCode) {
        Integer accessLevel = (Integer) getUIApplicationAccess().get(applicationCode);
        if (accessLevel != null) {
            return accessLevel.intValue();
        } else {
            return this.accessLevel;
        }
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
        return "TLS0151519";
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
        buffer.append("........:");
        return buffer.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see is.domainx.User#isFullyEntitled()
     */
    public boolean isFullyEntitled() {
         
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
         
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see is.domainx.User#getEntitledPortals()
     */
    public Vector getEntitledPortals() {
         
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
         
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see is.domainx.User#isCustomer()
     */
    public boolean isCustomer() {
         
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
    
    public String getValidUniqueID(String userEmail){
        String uniqueID = this.getUniqueID();
        userEmail = userEmail.toLowerCase();
        HashMap map = new HashMap();
//        map.put("changwei@cn.ibm.com", "TLS0191623"); 
//        map.put("zhengmr@cn.ibm.com", "TLS0205617"); 
//        map.put("tom_boulet@us.ibm.com", "ECC0003243"); 
//        map.put("berogers@us.ibm.com", "TLS0003304"); 
//        map.put("changhp@cn.ibm.com", "TLS0175370"); 
//        map.put("chenzhh@cn.ibm.com", "TLS0003323"); 
//        map.put("doris_yuen@us.ibm.com", "TLS0003344"); 
//        map.put("lijiatao@cn.ibm.com", "TLS0205915"); 
//        map.put("lthalla@us.ibm.com", "TLS0003423"); 
//        map.put("pushpa_baskaran@us.ibm.com", "TLS0253002"); 
//        map.put("tom_boulet@us.ibm.com", "TLS0253038"); 
//        map.put("williaga@us.ibm.com", "TLS0206001"); 
//        map.put("zhaoxw@cn.ibm.com", "TLS0003519"); 
//        map.put("cgang@cn.ibm.com", "TLS0253247"); 
//        map.put("liuxinlx@cn.ibm.com", "TLS0253387"); 
//        map.put("yuxing@cn.ibm.com", "TLS0207134"); 
//        map.put("wnan@cn.ibm.com", "TLS0207174"); 
//        map.put("doris_yuen@us.ibm.com", "TLS0003344"); 
//        map.put("liangwg@cn.ibm.com", "TLS0208212"); 
//        map.put("zhangdy@cn.ibm.com", "TLS0255251"); 
//        map.put("yuqqing@cn.ibm.com", "TLS0208288"); 
//        map.put("minhui_yang@us.ibm.com", "TLS0003440");
//        map.put("qinfengc@cn.ibm.com", "TLS0297031");
//        
//        map.put("dswfve1@us.ibm.com", "TLS0256066");
//        map.put("dswfve2@us.ibm.com", "TLS0256067");
//        map.put("dswfve3@us.ibm.com", "TLS0256065");
//        map.put("dswfve4@us.ibm.com", "TLS0256068");
//        map.put("dswfve5@us.ibm.com", "TLS0256069");
//        map.put("dswfve6@us.ibm.com", "TLS0256070");
//        map.put("dswfve7@us.ibm.com", "TLS0256071");
//        map.put("dswfve8@us.ibm.com", "TLS0256072");
//        map.put("dswfve9@us.ibm.com", "ECC0256076");
//        map.put("dswfve10@us.ibm.com", "TLS0256073");
//        map.put("dswfve11@us.ibm.com", "TLS0256074");
        
        map.put("berogers@us.ibm.com", "TLS0300141");
        map.put("changhp@cn.ibm.com", "TLS0175370");
        map.put("changwei@cn.ibm.com", "TLS0297105");
        map.put("david_smith@us.ibm.com", "TLS0300173");
        map.put("doris_yuen@us.ibm.com", "TLS0300181");
        map.put("doris_yuen@us.ibm.com", "TLS0003344");
        map.put("dswfve10@us.ibm.com", "TLS0297025");
        map.put("dswfve11@us.ibm.com", "TLS0297026");
        map.put("dswfve1@us.ibm.com", "TLS0303355");
        map.put("dswfve2@us.ibm.com", "TLS0303356");
        map.put("dswfve3@us.ibm.com", "TLS0297027");
        map.put("dswfve4@us.ibm.com", "TLS0303357");
        map.put("dswfve5@us.ibm.com", "TLS0000000");
        map.put("dswfve5a@us.ibm.com", "TLS0000000");
        map.put("dswfve6@us.ibm.com", "TLS0303359");
        map.put("dswfve7@us.ibm.com", "TLS0303360");
        map.put("dswfve8@us.ibm.com", "TLS0303361");
        map.put("dswfve9@us.ibm.com", "TLS0297029");
        map.put("junqingz@cn.ibm.com", "TLS0300230");
        map.put("pushpa_baskaran@us.ibm.com", "TLS0300290");
        map.put("fchqin@cn.ibm.com", "TLS0425602");
        map.put("xiaogy@cn.ibm.com", "TLS0303439");
        map.put("zhangln@cn.ibm.com", "TLS0303610");
        map.put("zhaoxw@cn.ibm.com", "TLS0300354");
        map.put("zhengmr@cn.ibm.com", "TLS0299949");
        map.put("yuepingl@cn.ibm.com", "TLS0425167");
        map.put("xlwangbj@cn.ibm.com", "TLS0376591");
        map.put("liangyue@cn.ibm.com", "TLS0424283");
        map.put("panjiabj@cn.ibm.com", "TLS0427059");
        map.put("wangxucd@cn.ibm.com", "TLS0563167");
        map.put("wqzhang@cn.ibm.com", "TLS0660917");
        map.put("yuanlyl@cn.ibm.com", "TLS0660981");
        map.put("sunzwnb@cn.ibm.com", "TLS0563003");
        map.put("huanj@cn.ibm.com", "TLS0563130");
        map.put("dzhiwei@cn.ibm.com", "TLS0654176");
        
        map.put("davidboi@us.ibm.com", "TLS0300174");
        map.put("jacqueline_kelley@us.ibm.com", "TLS0300206");
        map.put("jeffery_swanson@us.ibm.com", "TLS0300212");
        map.put("maureen_cotter@us.ibm.com", "TLS0300270");
        map.put("sheila_iiams@us.ibm.com", "TLS0300302");
        map.put("sychow@us.ibm.com", "TLS0300315");
        map.put("zhaoxw@cn.ibm.com", "TLS0300354");       
        map.put("dtalbot@us.ibm.com", "TLS0419490");
        map.put("bjjaskov@us.ibm.com", "TLS0302759");
        map.put("huangfup@cn.ibm.com", "TLS0414706");
        map.put("tianwang@cn.ibm.com", "TLS0304045");
        map.put("mmzhou@cn.ibm.com", "TLS0425164");
        map.put("xiongxj@cn.ibm.com", "TLS0425215");
        map.put("zgsun@cn.ibm.com", "TLS0304570");        
        map.put("zhoujunz@cn.ibm.com", "TLS0425652");
        map.put("liguotao@cn.ibm.com", "TLS0427224");
        map.put("jhma@cn.ibm.com", "TLS0425370");        
        map.put("xuyaojia@cn.ibm.com", "TLS0563227");
        map.put("zhangji@us.ibm.com", "TLS0563181");
        map.put("heshq@cn.ibm.com", "TLS0660794");
        
        if (map.containsKey(userEmail)){
            uniqueID = (String) map.get(userEmail);
        }
        return uniqueID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see is.domainx.User#isLinkedUser()
     */
    public boolean isLinkedUser() {
         
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
    public String getCookieDTime(){
    	Timestamp ts = new Timestamp(System.currentTimeMillis());
    	return String.valueOf(ts);
    }
    
    public String getCurrentPortal(){
    	return "TLS";
    }
}
