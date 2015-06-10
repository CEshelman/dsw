package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>JustSection_Impl<code> class.
 *    
 * @author: qinfengc@cn.ibm.com
 * 
 * Creation date: 2008-6-5
 */

public class JustSection_Impl implements JustSection, Serializable{
    protected int secId;
    protected transient List attachs;
    protected transient List justTexts;
 
    /**
     * @return Returns the attachs.
     */
    public List getAttachs() {
        if ( attachs == null )
        {
            attachs = new ArrayList();
        }
        return attachs;
    }
    /**
     * @param attachs The attachs to set.
     */
    public void setAttachs(List attachs) {
        this.attachs = attachs;
    }
    /**
     * @return Returns the seq.
     */
    public int getSecId() {
        return secId;
    }
    /**
     * @param seq The seq to set.
     */
    public void setSecId(int secId) {
        this.secId = secId;
    }
    
    /**
     * @return Returns the justTexts.
     */
    public List getJustTexts() {
        if ( justTexts == null )
        {
            justTexts = new ArrayList();
        }
        return justTexts;
    }
    /**
     * @param justTexts The justTexts to set.
     */
    public void setJustTexts(List justTexts) {
        this.justTexts = justTexts;
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        if ( o == null )
        {
            return -1;
        }
        JustSection sec = (JustSection)o;
        return secId - sec.getSecId();
    }
    
    public boolean isEmpty()
    {
        List list = this.getJustTexts();
        for ( int i = 0; i < list.size(); i++ )
        {
            SpecialBidInfo.CommentInfo cmtInfo = (SpecialBidInfo.CommentInfo)list.get(i);
            if ( cmtInfo.getComment() != null && !cmtInfo.getComment().equals("") )
            {
                return false;
            }
        }
        if ( this.getAttachs().size() != 0 )
        {
            return false;
        }
        return true;
    }
}
