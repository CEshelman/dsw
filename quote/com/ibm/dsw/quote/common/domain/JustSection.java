package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>JustSection<code> class.
 *    
 * @author: qinfengc@cn.ibm.com
 * 
 * Creation date: 2008-6-5
 */

public interface JustSection extends Comparable, Serializable{
    public int getSecId();
    
    public void setSecId(int secId);
    
    public List getAttachs();
    
    public void setAttachs(List attachs); 
    
    public List getJustTexts();
    
    public void setJustTexts(List justTexts);
    
    public boolean isEmpty();
}
