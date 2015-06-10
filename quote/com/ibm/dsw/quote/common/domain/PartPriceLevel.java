package com.ibm.dsw.quote.common.domain;

import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

/**
 * Copyright 2006 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * The <code></code> class is 
 * 
 * 
 * @author <a href="liuxinlx@cn.ibm.com">Andy Liu </a> <br/>
 * 
 * Creation date: Jun 22, 2007
 */

public  class PartPriceLevel{
    protected static LogContext logContext = LogContextFactory.singleton().getLogContext();
    
    private double srpPrice;
    private double svpA;
    private double svpB;
    private double svpC;
    private double svpD;
    private double svpE;
    private double svpF;
    private double svpG;
    private double svpH;
    private double svpI;
    private double svpJ;
    private double svpED;
    private double svpGV;
    
    public PartPriceLevel(double srpPrice,double svpPrices[]){
        this.srpPrice = srpPrice;
        if(svpPrices.length <12){
            logContext.info(this,"svpPrices is not valid");
            return;
        }
        svpA = svpPrices[0];
        svpB = svpPrices[1];
        svpC = svpPrices[2];
        svpD = svpPrices[3];
        svpE = svpPrices[4];
        svpF = svpPrices[5];
        svpG = svpPrices[6];
        svpH = svpPrices[7];
        svpI = svpPrices[8];
        svpJ = svpPrices[9];
        svpED = svpPrices[10];
        svpGV = svpPrices[11];
        
    }
    public double getSRPPrice(){
        return srpPrice;
    }
    
    public double getSVPPrice(String svpLevel){      
        if(null == svpLevel){
            logContext.info(this,"svp level is null");
            return 0.0;
        }
        svpLevel = svpLevel.trim();
        
        if("A".equals(svpLevel)){
            return svpA;
        }
        if("B".equals(svpLevel)){
            return svpB;
        }
        if("C".equals(svpLevel)){
            return svpC;
        }
        if("D".equals(svpLevel)){
            return svpD;
        }
        if("E".equals(svpLevel)){
            return svpE;
        }
        if("F".equals(svpLevel)){
            return svpF;
        }
        if("G".equals(svpLevel)){
            return svpG;
        }
        if("H".equals(svpLevel)){
            return svpH;
        }
        if("I".equals(svpLevel)){
            return svpI;
        }
        if("J".equals(svpLevel)){
            return svpJ;
        }
        if("ED".equals(svpLevel)){
            return svpED;
        }
        if("GV".equals(svpLevel)){
            return svpGV;
        }
        logContext.info(this,"Invalid svp level "+svpLevel);
        return 0.0;
    }
}