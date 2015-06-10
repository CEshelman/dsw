package com.ibm.dsw.quote.pvu.domain;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>VUConfig</code> class is a domain object is mapping to EBIZ1.VU_CONFIG_INFO table 
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-19
 */
public interface VUConfig {
   
   /**
    * @return Returns the coreValUnit.
    */
   
   public String getCoreValUnit();
   
   /**
    * @return Returns the extndDVU.
    */
   
   public int getExtndDVU();
   
   /**
    * @return Returns the procrBrandCode.
    */
   
   public String getProcrBrandCode();
   
   /**
    * @return Returns the procrCode.
    */
   
   public String getProcrCode();
   
   /**
    * @return Returns the procrTypeCode.
    */
   
   public String getProcrTypeCode();
   
   /**
    * @return Returns the procrTypeQTY.
    */
   
   public int getProcrTypeQTY();
   
   /**
    * @return Returns the procrVendCode.
    */
   
   public String getProcrVendCode();
   
}
