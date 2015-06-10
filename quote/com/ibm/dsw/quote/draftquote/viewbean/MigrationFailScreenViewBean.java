package com.ibm.dsw.quote.draftquote.viewbean;

import java.util.List;

import com.ibm.dsw.quote.base.config.ParamKeys;
import com.ibm.dsw.quote.common.domain.MigrationFailureLineItem;
import com.ibm.ead4j.jade.bean.ViewBeanException;
import com.ibm.ead4j.jade.util.Parameters;
/**
 * Copyright 2007 by IBM Corporation All rights reserved.
 * 
 * This software is the confidential and proprietary information of IBM
 * Corporation. ("Confidential Information").
 * 
 * This <code>MigrationFailScreenViewBean<code> class.
 *    
 * @author: xiongxj@cn.ibm.com
 * 
 * Creation date: 2012-5-15
 */
public class MigrationFailScreenViewBean extends MigrationReqBaseViewBean {
	
	private static final long serialVersionUID = -8858827400723177265L;
	protected List<MigrationFailureLineItem> migrationFailureIineItems = null;

	@SuppressWarnings("unchecked")
	public void collectResults(Parameters params) throws ViewBeanException {
        super.collectResults(params);
        migrationFailureIineItems = (List<MigrationFailureLineItem>) params.getParameter(ParamKeys.PARPM_MIGRATION_FAILURE_LINE_ITEMS);
	}

	public List<MigrationFailureLineItem> getMigrationFailureIineItems() {
		return migrationFailureIineItems;
	}

	public void setMigrationFailureIineItems(
			List<MigrationFailureLineItem> migrationFailureIineItems) {
		this.migrationFailureIineItems = migrationFailureIineItems;
	}
}
