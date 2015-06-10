package com.ibm.dsw.quote.audit.process;

import java.util.List;

import com.ibm.dsw.quote.base.exception.QuoteException;
import com.ibm.dsw.quote.common.domain.QuoteRightColumnFactory;
import com.ibm.dsw.quote.common.exception.NoDataException;
import com.ibm.ead4j.topaz.exception.TopazException;
import com.ibm.ead4j.topaz.process.TopazTransactionalProcess;

/**
 * 
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>QuoteAuditProcess_Impl<code> class.
 *    
 * @author: mmzhou@cn.ibm.com
 * 
 * Creation date: Dec 27, 2010
 */
public abstract class QuoteAuditProcess_Impl  extends TopazTransactionalProcess implements QuoteAuditProcess {
}
