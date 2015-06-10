/*
 * Created on 2007-4-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ibm.dsw.quote.common.domain;

import java.io.Serializable;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Domain extends Serializable{
    public final static int DOMAIN_MODE_VO = 1;
    public final static int DOMAIN_MODE_PO = 0;
    public void setMode(int mode) throws Exception;
    public int getMode();
    public boolean isModified();
    public void markAsModified() throws Exception;
}
