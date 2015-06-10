package com.ibm.dsw.quote.pvu.process;


import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.SearchResultList;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>VUConfigProcess</code> class.
 *    
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-3-20
 */
public interface VUConfigProcess {
    
    /* Select pvu config by calling EBIZ1.S_VU_CONFIG(CONFIG_NUM) <SP owned by VU Wizard> */
    public SearchResultList findVUConfigByConfigNum(String configNum, String noDscrFlag) throws QuoteException;
    
    /* Save the config to WEB_QUOTE_LINEITEM_CONFIG by calling SP IU_QT_LI_CONFIG */
    public void updateQuoteLineItemConfig(List vuConfigList, String lineItemSeqNum, String creatorId, String configNum) throws QuoteException;
    
    
}