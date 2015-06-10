package com.ibm.dsw.quote.export.exception;

import com.ibm.dsw.quote.export.config.ExportQuoteMessageKeys;


/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>InvildPartQtyException</code> class.
 * 
 * @author: cgang@cn.ibm.com
 * 
 * Creation date: 2007-7-21
 */
public class InvildPartQtyException extends ExportQuoteException {
	 /**
	 * @param string
	 */
	public InvildPartQtyException(String str) {
		super(str);
	}

	public String getMessageKey() {
        return ExportQuoteMessageKeys.INVALID_PART_QTY;
    }
}
