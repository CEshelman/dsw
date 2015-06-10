package com.ibm.dsw.quote.base.config;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code>DBConstants.java</code> class contains general constants used for DB2 calls
 * 
 * 
 * @author: chenzhh@cn.ibm.com
 * 
 * Creation date: Jan 26, 2007
 */
public interface DBConstants {
 
    //sp return result
    public final static int DEFAULT_ROWCOUNT = -1;

    //SP execute success
    public final static int DB2_SP_RETURN_SIGN_SUCCESS = 0;

    //input parameters not valid
    public final static int DB2_SP_RETURN_SIGN_INPUT_INVALID = 34100;
    
   //valid part can not insert to DB
    public final static int DB2_SP_RETURN_PART_INPUT_INVALID = 34200;

    public final static int DB2_SP_RETURN_SIGN_INPUTCA_INVALID = 34300;
    
    public final static int DB2_SP_RETURN_SIGN_NOSITE = 34400;
    //no result returned
    public final static int DB2_SP_RETURN_SIGN_NODATA = 5000;

    //too many rows
    public final static int DB2_SP_RETURN_SIGN_TOOMANY_ROWS = 5001;
    
    // Contract inactive
    public final static int DB2_SP_RETURN_CONTRACT_INACTIVE = 5002;
    
    // Lob is not available when create sales quote from order
    public final static int DB2_SP_RETURN_LOB_INVALID = 5003;
    
    //Acquisition is not available when create sales quote from order
    public final static int DB2_SP_RETURN_ACQUISITION_INVALID = 5004;
    
    //row existed in DB
    public final static int DB2_SP_RETURN_SIGN_ROW_EXISTING = 7003;
    
    public final static String DB2_SP_RETURN_VAR_NAME = "poGenStatus";
    
    public final static int DB2_SP_RETURN_NOT_VALUE_UNIT = 36100;
    
    //For ELA
    public final static int DB2_SP_ALREADY_IS_MAX=1001;
    public final static int DB2_SP_EXCEED_MAX=1002;
    
    public final static int DB2_SP_OVER_LIMIT_TOTAL_ROWS = 30000;
    
	//user lookup service sp indicator status
	public final static String INPUT_CA_ACTIVE = "1";
	public final static String INPUT_CA_INVALID = "2";
	public final static String INPUT_CA_INACTIVE = "3";
	//1: the CA is active
    //2: the CA is invalid, that means CA is not exist.
    //3: the CA is inactive, that means there is not any subscription line item in CA.

	public final static String INPUT_CONFIG_ACTIVE = "1";
	public final static String INPUT_CONFIG_INVALID = "2";
	public final static String INPUT_CONFIG_INACTIVE = "3";
	//1: Config is active
    //2: Config is invalid, that means config is not exist on that you input CA.
    //3: Config is inactive, that means there is not any subscription line item in Config.

	public final static String NO_INPUT_SITE = "0";
	public final static String INPUT_SITE_VALID = "1";
	public final static String INPUT_SITE_INVALID = "2";
	public final static String NO_SITE_FOUND_BY_UNIQID = "3";
	//0: when site num do not passed in.
    //1: when site num passed in, site num is valid.
    //2: when site num passed in, site num is invalid.
    //3: site list not found by user Unique ID.

	public final static String NO_INPUT_PID = "0";
	public final static String CONFIG_FOR_INPUT_PID = "1";
	public final static String NO_CONFIG_FOR_INPUT_PID = "2";
	public final static String CONFIG_FOR_INPUT_PID_NO_SUB_LINE_ITEM = "3";
	//0: no pid passed in.
    //1: have config for this pid active
    //2: no config for this pid
    //3: have config for this pid is not any subscription line item.

}
